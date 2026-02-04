package com.blockforge.moderex.gateway;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gateway client for connecting to the ModereX central gateway.
 *
 * Handles:
 * - Outbound WebSocket connection to gateway
 * - Server registration with unique ID
 * - Heartbeat maintenance
 * - Message forwarding between gateway and HybridPanelServer
 * - Automatic reconnection with exponential backoff
 */
public class GatewayClient {

    private static final Gson GSON = new Gson();
    private static final long HEARTBEAT_INTERVAL_MS = 30_000; // 30 seconds
    private static final int MAX_BACKOFF_SECONDS = 300; // 5 minutes max

    private final ModereX plugin;
    private final String gatewayUrl;
    private final boolean debugLogging;

    private WebSocketClient wsClient;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTask;
    private ScheduledFuture<?> reconnectTask;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean registered = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private final AtomicLong connectedSince = new AtomicLong(0);
    private final AtomicLong lastHeartbeat = new AtomicLong(0);

    // Callback for handling incoming messages from gateway
    private GatewayMessageHandler messageHandler;

    public GatewayClient(ModereX plugin) {
        this.plugin = plugin;
        this.gatewayUrl = plugin.getConfigManager().getSettings().getGatewayUrl();
        this.debugLogging = plugin.getConfigManager().getSettings().isGatewayDebugLogging();
    }

    /**
     * Set the message handler for incoming gateway messages.
     */
    public void setMessageHandler(GatewayMessageHandler handler) {
        this.messageHandler = handler;
    }

    /**
     * Start the gateway client and connect to the gateway.
     */
    public void start() {
        if (!plugin.getConfigManager().getSettings().isGatewayEnabled()) {
            plugin.getLogger().info("[Gateway] Gateway connection disabled in config");
            return;
        }

        if (gatewayUrl == null || gatewayUrl.isEmpty()) {
            plugin.getLogger().warning("[Gateway] No gateway URL configured");
            return;
        }

        shuttingDown.set(false);
        scheduler = Executors.newScheduledThreadPool(2);

        logImportant("Connecting to gateway: " + gatewayUrl);
        connect();
    }

    /**
     * Stop the gateway client and disconnect.
     */
    public void stop() {
        shuttingDown.set(true);

        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }
        if (reconnectTask != null) {
            reconnectTask.cancel(true);
        }

        if (wsClient != null) {
            try {
                wsClient.closeBlocking();
            } catch (InterruptedException ignored) {}
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        connected.set(false);
        registered.set(false);

        log("Gateway client stopped");
    }

    /**
     * Force reconnect to the gateway.
     */
    public void reconnect() {
        log("Force reconnect requested");
        reconnectAttempts.set(0);

        if (wsClient != null) {
            wsClient.close();
        }

        scheduleReconnect(0);
    }

    /**
     * Connect to the gateway server.
     */
    private void connect() {
        try {
            URI uri = new URI(gatewayUrl);

            wsClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    handleOpen();
                }

                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    handleClose(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    handleError(ex);
                }
            };

            // Set connection timeout
            wsClient.setConnectionLostTimeout(60);
            wsClient.connect();

        } catch (Exception e) {
            plugin.getLogger().warning("[Gateway] Failed to create WebSocket connection: " + e.getMessage());
            scheduleReconnect(getBackoffSeconds());
        }
    }

    /**
     * Handle WebSocket connection opened.
     */
    private void handleOpen() {
        connected.set(true);
        connectedSince.set(System.currentTimeMillis());
        reconnectAttempts.set(0);

        logImportant("Connected to gateway!");

        // Send registration message
        sendRegistration();

        // Start heartbeat
        startHeartbeat();
    }

    /**
     * Handle incoming WebSocket message.
     */
    private void handleMessage(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String type = json.has("type") ? json.get("type").getAsString() : null;

            if (type == null) {
                log("Received message without type: " + message);
                return;
            }

            switch (type) {
                case "registered" -> handleRegistered(json);
                case "heartbeat_ack" -> handleHeartbeatAck();
                case "url_prefix_update" -> handleUrlPrefixUpdate(json);
                case "browser_connected" -> handleBrowserConnected(json);
                case "browser_disconnected" -> handleBrowserDisconnected(json);
                case "error" -> handleGatewayError(json);
                default -> {
                    // Forward to message handler (panel requests)
                    if (messageHandler != null) {
                        logImportant("Forwarding message type '" + type + "' to panel handler");
                        messageHandler.handleMessage(type, json);
                    } else {
                        logWarning("No message handler registered for type: " + type);
                    }
                }
            }

        } catch (Exception e) {
            plugin.logDebug("[Gateway] Error processing message: " + e.getMessage());
        }
    }

    /**
     * Handle WebSocket connection closed.
     */
    private void handleClose(int code, String reason, boolean remote) {
        connected.set(false);
        registered.set(false);
        connectedSince.set(0);

        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }

        if (!shuttingDown.get()) {
            String closeReason = remote ? "by gateway" : "locally";
            log("Disconnected from gateway " + closeReason + " (code: " + code + ", reason: " + reason + ")");
            scheduleReconnect(getBackoffSeconds());
        }
    }

    /**
     * Handle WebSocket error.
     */
    private void handleError(Exception ex) {
        if (!shuttingDown.get()) {
            plugin.logDebug("[Gateway] WebSocket error: " + ex.getMessage());
        }
    }

    /**
     * Send registration message to gateway.
     */
    private void sendRegistration() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "register");
        msg.addProperty("serverId", plugin.getServerIdentity().getServerId());
        msg.addProperty("serverName", plugin.getServerIdentity().getServerName());
        msg.addProperty("version", plugin.getDescription().getVersion());
        msg.addProperty("players", plugin.getServer().getOnlinePlayers().size());

        send(msg);
        log("Sent registration for server: " + plugin.getServerIdentity().getServerId());
    }

    /**
     * Handle successful registration response.
     */
    private void handleRegistered(JsonObject json) {
        registered.set(true);

        String serverId = json.has("serverId") ? json.get("serverId").getAsString() : "unknown";
        String urlPrefix = json.has("urlPrefix") ? json.get("urlPrefix").getAsString() : null;
        int prefixGroups = json.has("prefixGroups") ? json.get("prefixGroups").getAsInt() : 1;

        // Update URL prefix groups if gateway assigned a different one
        if (prefixGroups > 0) {
            plugin.getServerIdentity().setUrlPrefixGroups(prefixGroups);
        }

        logImportant("Registered! Server ID: " + serverId + ", URL prefix: " + urlPrefix);
        logImportant("Panel URL: " + plugin.getServerIdentity().getPanelUrl("moderex.net"));
    }

    /**
     * Handle URL prefix update from gateway (when uniqueness changes).
     */
    private void handleUrlPrefixUpdate(JsonObject json) {
        int prefixGroups = json.has("prefixGroups") ? json.get("prefixGroups").getAsInt() : 1;
        String newPrefix = json.has("urlPrefix") ? json.get("urlPrefix").getAsString() : null;

        plugin.getServerIdentity().setUrlPrefixGroups(prefixGroups);

        log("URL prefix updated to: " + newPrefix + " (" + prefixGroups + " groups)");
    }

    /**
     * Handle browser connected notification.
     */
    private void handleBrowserConnected(JsonObject json) {
        String clientId = json.has("clientId") ? json.get("clientId").getAsString() : "unknown";
        String clientIp = json.has("clientIp") ? json.get("clientIp").getAsString() : "unknown";

        log("Browser connected: " + clientId + " from " + clientIp);
    }

    /**
     * Handle browser disconnected notification.
     */
    private void handleBrowserDisconnected(JsonObject json) {
        String clientId = json.has("clientId") ? json.get("clientId").getAsString() : "unknown";

        log("Browser disconnected: " + clientId);
    }

    /**
     * Handle error message from gateway.
     */
    private void handleGatewayError(JsonObject json) {
        String code = json.has("code") ? json.get("code").getAsString() : "UNKNOWN";
        String message = json.has("message") ? json.get("message").getAsString() : "Unknown error";

        plugin.getLogger().warning("[Gateway] Error from gateway: " + code + " - " + message);
    }

    /**
     * Start heartbeat timer.
     */
    private void startHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }

        heartbeatTask = scheduler.scheduleAtFixedRate(
            this::sendHeartbeat,
            HEARTBEAT_INTERVAL_MS,
            HEARTBEAT_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Send heartbeat message.
     */
    private void sendHeartbeat() {
        if (!connected.get() || !registered.get()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "heartbeat");
        msg.addProperty("players", plugin.getServer().getOnlinePlayers().size());

        send(msg);
        lastHeartbeat.set(System.currentTimeMillis());
    }

    /**
     * Handle heartbeat acknowledgment.
     */
    private void handleHeartbeatAck() {
        // Connection is healthy
        log("Heartbeat acknowledged");
    }

    /**
     * Schedule reconnection with backoff.
     */
    private void scheduleReconnect(int delaySeconds) {
        if (shuttingDown.get()) return;

        reconnectAttempts.incrementAndGet();
        log("Scheduling reconnect in " + delaySeconds + " seconds (attempt " + reconnectAttempts.get() + ")");

        if (reconnectTask != null) {
            reconnectTask.cancel(false);
        }

        reconnectTask = scheduler.schedule(this::connect, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Calculate backoff delay for reconnection.
     */
    private int getBackoffSeconds() {
        int baseDelay = plugin.getConfigManager().getSettings().getGatewayReconnectDelay();
        int attempts = reconnectAttempts.get();

        // Exponential backoff: base * 2^attempts, capped at MAX_BACKOFF_SECONDS
        int backoff = (int) Math.min(baseDelay * Math.pow(2, attempts), MAX_BACKOFF_SECONDS);
        return backoff;
    }

    /**
     * Send a JSON message to the gateway.
     */
    public void send(JsonObject message) {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send(GSON.toJson(message));
        }
    }

    /**
     * Send a response to a specific browser client via gateway.
     * Wraps the response to preserve the original message type.
     */
    public void sendToClient(String clientId, JsonObject response) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", "panel_response");
        wrapper.addProperty("clientId", clientId);
        wrapper.add("response", response); // Preserve original response including its type
        send(wrapper);
    }

    /**
     * Broadcast a message to all browsers connected to this server via gateway.
     */
    public void broadcast(JsonObject data) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "broadcast");
        msg.add("data", data);
        send(msg);
    }

    /**
     * Check if connected to gateway.
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Check if registered with gateway.
     */
    public boolean isRegistered() {
        return registered.get();
    }

    /**
     * Get connection uptime in milliseconds.
     */
    public long getUptime() {
        long since = connectedSince.get();
        return since > 0 ? System.currentTimeMillis() - since : 0;
    }

    /**
     * Get number of reconnect attempts.
     */
    public int getReconnectAttempts() {
        return reconnectAttempts.get();
    }

    /**
     * Get the timestamp when the connection was established.
     */
    public long getConnectedAt() {
        return connectedSince.get();
    }

    /**
     * Check if currently in the process of reconnecting.
     */
    public boolean isReconnecting() {
        return !connected.get() && !shuttingDown.get() && reconnectTask != null && !reconnectTask.isDone();
    }

    /**
     * Log a message (respects debug-logging config).
     */
    private void log(String message) {
        if (debugLogging) {
            plugin.getLogger().info("[Gateway] " + message);
        } else {
            plugin.logDebug("[Gateway] " + message);
        }
    }

    /**
     * Log an important message (always visible).
     */
    private void logImportant(String message) {
        plugin.getLogger().info("[Gateway] " + message);
    }

    /**
     * Log a warning message (always visible).
     */
    private void logWarning(String message) {
        plugin.getLogger().warning("[Gateway] " + message);
    }
}

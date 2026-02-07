package com.blockforge.moderex.gateway;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonArray;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final GatewaySecretManager secretManager;

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
        this.secretManager = new GatewaySecretManager(plugin);
        this.secretManager.initialize();
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

        // Register LuckPerms permission change listener (if LP is available)
        if (plugin.getHookManager().isLuckPermsEnabled()) {
            plugin.getHookManager().getLuckPermsHook().registerPermissionChangeListener(plugin);
            log("Registered LuckPerms permission change listener for gateway sync");
        }

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
                case "admin_announcement" -> handleAdminAnnouncement(json);
                case "global_pre_auth" -> handleGlobalPreAuth(json);
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

        // Include gateway secret for authentication (from .gateway-secret file)
        String secret = secretManager.getSecret();
        if (secret != null && !secret.isEmpty()) {
            msg.addProperty("secret", secret);
        }

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

        // Handle premium status from gateway
        boolean premium = json.has("premium") && json.get("premium").getAsBoolean();
        long premiumExpiresAt = json.has("premiumExpiresAt") ? json.get("premiumExpiresAt").getAsLong() : 0;
        plugin.getServerIdentity().setPremium(premium, premiumExpiresAt);

        logImportant("Registered! Server ID: " + serverId + ", URL prefix: " + urlPrefix);
        logImportant("Panel URL: " + plugin.getServerIdentity().getPanelUrl("moderex.net"));
        if (premium) {
            logImportant("Premium status: ACTIVE");
        }

        // After registration, sync all player permissions to gateway
        triggerPermissionSync();
    }

    /**
     * Sync all players with web panel access to the gateway.
     * Called after successful gateway registration.
     * LuckPerms is optional — if not available, skips the sync.
     */
    private void triggerPermissionSync() {
        if (!plugin.getHookManager().isLuckPermsEnabled()) {
            log("LuckPerms not available — skipping permission sync");
            return;
        }

        log("Starting permission sync to gateway...");
        plugin.getHookManager().getLuckPermsHook().getAllWebPanelPlayers(plugin, players -> {
            if (!players.isEmpty() && isConnected() && isRegistered()) {
                syncAllPermissions(players);
            }
        });
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
     * Handle admin announcement from gateway (broadcast from ModereX admin panel).
     * This forwards the announcement to all connected web panel clients.
     */
    private void handleAdminAnnouncement(JsonObject json) {
        if (!json.has("data")) {
            log("Admin announcement missing data");
            return;
        }

        JsonObject data = json.getAsJsonObject("data");
        String announcementId = data.has("id") ? data.get("id").getAsString() : "unknown";
        String title = data.has("title") ? data.get("title").getAsString() : "";

        logImportant("Received admin announcement: " + title + " (ID: " + announcementId + ")");

        // Forward to HybridPanelServer to broadcast to all connected web panels
        if (messageHandler != null) {
            // Create the message to broadcast to web panels
            JsonObject broadcastMsg = new JsonObject();
            broadcastMsg.addProperty("type", "ADMIN_ANNOUNCEMENT");
            broadcastMsg.add("data", data);

            // Use the message handler to broadcast (HybridPanelServer implements this)
            messageHandler.broadcastToAllClients(broadcastMsg);
        }
    }

    /**
     * Handle global pre-auth request from gateway.
     * Creates a session for a user authenticated via global token,
     * so they can access this server's panel without a local token.
     */
    private void handleGlobalPreAuth(JsonObject json) {
        String clientId = json.has("clientId") ? json.get("clientId").getAsString() : null;
        String uuid = json.has("uuid") ? json.get("uuid").getAsString() : null;
        String username = json.has("username") ? json.get("username").getAsString() : null;

        if (clientId == null || uuid == null) {
            log("Invalid global_pre_auth: missing clientId or uuid");
            return;
        }

        log("Global pre-auth request for " + username + " (clientId: " + clientId + ")");

        // Create session via the message handler (HybridPanelServer)
        if (messageHandler != null) {
            try {
                // Parse permissions from the request
                JsonArray permsArray = json.has("permissions") ? json.getAsJsonArray("permissions") : new JsonArray();
                List<String> permissions = new java.util.ArrayList<>();
                for (int i = 0; i < permsArray.size(); i++) {
                    permissions.add(permsArray.get(i).getAsString());
                }

                // Delegate to message handler to create session
                messageHandler.handleGlobalPreAuth(clientId, UUID.fromString(uuid), username, permissions);
            } catch (Exception e) {
                logWarning("Failed to handle global pre-auth: " + e.getMessage());

                // Send failure response back to gateway
                JsonObject response = new JsonObject();
                response.addProperty("type", "global_pre_auth_result");
                response.addProperty("clientId", clientId);
                response.addProperty("success", false);
                response.addProperty("error", "Session creation failed: " + e.getMessage());
                send(response);
            }
        } else {
            logWarning("No message handler for global pre-auth");
        }
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

    // ========================================================================
    // Global Token System — Outbound Messages to Gateway
    // ========================================================================

    /**
     * Bulk sync all players with web panel access to the gateway.
     * Called on server startup after gateway registration.
     *
     * @param players List of maps with keys: uuid, username, rank, permissions (List<String>)
     */
    public void syncAllPermissions(List<Map<String, Object>> players) {
        if (!isConnected() || !isRegistered()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "permission_sync");

        JsonArray playersArray = new JsonArray();
        for (Map<String, Object> player : players) {
            JsonObject p = new JsonObject();
            p.addProperty("uuid", (String) player.get("uuid"));
            p.addProperty("username", (String) player.get("username"));
            p.addProperty("rank", (String) player.get("rank"));

            JsonArray perms = new JsonArray();
            @SuppressWarnings("unchecked")
            List<String> permList = (List<String>) player.get("permissions");
            if (permList != null) {
                for (String perm : permList) perms.add(perm);
            }
            p.add("permissions", perms);
            playersArray.add(p);
        }
        msg.add("players", playersArray);

        send(msg);
        logImportant("Synced " + players.size() + " player permissions to gateway");
    }

    /**
     * Sync a single player's permission change to the gateway.
     * Called when LuckPerms fires a permission recalculate event.
     */
    public void syncPlayerPermission(UUID uuid, String username, String rank,
                                      List<String> permissions, boolean hasAccess) {
        if (!isConnected() || !isRegistered()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "permission_update");
        msg.addProperty("uuid", uuid.toString());
        msg.addProperty("username", username);
        msg.addProperty("rank", rank);
        msg.addProperty("hasAccess", hasAccess);

        JsonArray perms = new JsonArray();
        if (permissions != null) {
            for (String perm : permissions) perms.add(perm);
        }
        msg.add("permissions", perms);

        send(msg);
        log("Permission update sent for " + username + " (access: " + hasAccess + ")");
    }

    /**
     * Register a global token on the gateway.
     * Called when /mx gettoken generates a token with gateway enabled.
     */
    public void registerGlobalToken(UUID uuid, String username, String tokenHash, long expiresAt) {
        if (!isConnected() || !isRegistered()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "token_register");
        msg.addProperty("uuid", uuid.toString());
        msg.addProperty("username", username);
        msg.addProperty("tokenHash", tokenHash);
        msg.addProperty("expiresAt", expiresAt);

        send(msg);
        log("Global token registered for " + username);
    }

    /**
     * Revoke a global token on the gateway.
     * Called when /mx revoketoken is used with gateway enabled.
     */
    public void revokeGlobalToken(UUID uuid) {
        if (!isConnected() || !isRegistered()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "token_revoke");
        msg.addProperty("uuid", uuid.toString());

        send(msg);
        log("Global token revoked for " + uuid);
    }

    /**
     * Sync user settings (color scheme + device fingerprints) to gateway.
     * Called when a user changes settings in the panel.
     */
    public void syncUserSettings(UUID uuid, String colorScheme, List<String> deviceFingerprints) {
        if (!isConnected() || !isRegistered()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "settings_sync");
        msg.addProperty("uuid", uuid.toString());
        msg.addProperty("colorScheme", colorScheme);

        JsonArray fps = new JsonArray();
        if (deviceFingerprints != null) {
            for (String fp : deviceFingerprints) fps.add(fp);
        }
        msg.add("deviceFingerprints", fps);

        send(msg);
        log("Settings synced for " + uuid);
    }

    /**
     * Unregister this server from the gateway.
     * Called when gateway.enabled is set to false in config.
     * Removes all server_access entries for this server on the gateway.
     */
    public void unregisterServer() {
        if (!isConnected()) return;

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "server_unregister");

        send(msg);
        logImportant("Server unregistered from gateway (access entries removed)");
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

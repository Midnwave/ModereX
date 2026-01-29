package com.blockforge.moderex.webpanel;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.alert.AlertManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.web.WebAuthManager;
import com.blockforge.moderex.webpanel.debug.DebugCategory;
import com.blockforge.moderex.webpanel.debug.ErrorCode;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import com.blockforge.moderex.webpanel.netty.WebSocketFrameHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * TODO: Advanced Global Search Bar
 * - Search across all data: players, punishments, automod, watchlist, etc.
 * - Fuzzy matching for player names and UUIDs
 * - Search syntax: "player:name", "ban:reason", "ip:192.168.*"
 * - Real-time search results as you type
 * - Keyboard shortcuts (Ctrl+K or / to focus search)
 * - Recent searches history
 * - Search result preview with quick actions
 * - Filter by date ranges, punishment types, etc.
 */
public class HybridPanelServer {

    private static final Gson GSON = new Gson();
    private static final long CONNECT_CODE_EXPIRY = 5 * 60 * 1000; // 5 minutes
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html; charset=UTF-8");
        MIME_TYPES.put("css", "text/css; charset=UTF-8");
        MIME_TYPES.put("js", "application/javascript; charset=UTF-8");
        MIME_TYPES.put("json", "application/json; charset=UTF-8");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
    }

    private final ModereX plugin;
    private final int port;
    private final Path panelDirectory;
    private final boolean serveFromJar;

    private ServerSocket serverSocket;
    private ExecutorService executor;
    private ExecutorService broadcastExecutor; // Single-threaded executor for broadcasts to prevent thread explosion
    private volatile boolean running = false;

    // WebSocket state
    private final Set<WebSocketConnection> connections = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketConnection, WebPanelSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, PendingConnection> pendingCodes = new ConcurrentHashMap<>();
    private final Map<UUID, UserPanelSettings> userSettings = new ConcurrentHashMap<>();

    // Same-port (Netty) WebSocket connections
    private final Map<String, WebSocketFrameHandler> samePortConnections = new ConcurrentHashMap<>();
    private final Map<String, WebPanelSession> samePortSessions = new ConcurrentHashMap<>();

    public HybridPanelServer(ModereX plugin, int port) {
        this.plugin = plugin;
        this.port = port;
        // Use hidden .panel folder for any extracted files (fallback only)
        this.panelDirectory = plugin.getDataFolder().toPath().resolve(".data").resolve(".panel");
        // Prefer serving from JAR to keep plugin folder clean
        this.serveFromJar = true;
    }

    public void start() {
        try {
            setupPanelDirectory();

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            executor = Executors.newCachedThreadPool();
            // Single-threaded executor for broadcasts to prevent thread explosion
            broadcastExecutor = Executors.newSingleThreadExecutor();
            running = true;

            executor.submit(this::acceptLoop);
            loadUserSettings();

            plugin.getLogger().info("Web panel server started on port " + port);
            plugin.getLogger().info("Access the panel at: http://localhost:" + port + "/");

            // Debug: Server started
            debugSuccess(DebugCategory.CONNECTION, "Web panel server started", "Port: " + port);

        } catch (IOException e) {
            plugin.logError("Failed to start web panel server on port " + port, e);
            debugError(ErrorCode.SERVER_BIND_FAILED, "Port: " + port + ", Error: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;

        // Close all WebSocket connections
        for (WebSocketConnection conn : connections) {
            conn.close();
        }
        connections.clear();
        sessions.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        if (executor != null) {
            executor.shutdownNow();
        }

        if (broadcastExecutor != null) {
            broadcastExecutor.shutdownNow();
        }

        plugin.getLogger().info("Web panel server stopped");
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(() -> handleConnection(socket));
            } catch (IOException e) {
                if (running) {
                    plugin.logDebug("Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleConnection(Socket socket) {
        try {
            socket.setSoTimeout(30000);

            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                socket.close();
                return;
            }

            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(':');
                if (idx > 0) {
                    headers.put(line.substring(0, idx).trim().toLowerCase(), line.substring(idx + 1).trim());
                }
            }

            String upgrade = headers.get("upgrade");
            String wsKey = headers.get("sec-websocket-key");

            if ("websocket".equalsIgnoreCase(upgrade) && wsKey != null) {
                handleWebSocketUpgrade(socket, headers, wsKey);
            } else {
                handleHttpRequest(socket, requestLine, headers);
            }

        } catch (Exception e) {
            plugin.logDebug("Connection error: " + e.getMessage());
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // ==================== HTTP Handling ====================

    private void handleHttpRequest(Socket socket, String requestLine, Map<String, String> headers) throws IOException {
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        try {
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                sendHttpError(out, 400, "Bad Request");
                return;
            }

            String method = parts[0];
            String path = parts[1];

            if ("OPTIONS".equals(method)) {
                sendCorsResponse(out);
                return;
            }

            int queryIdx = path.indexOf('?');
            if (queryIdx > 0) path = path.substring(0, queryIdx);
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);

            // Handle POST requests for AI chat
            if ("POST".equals(method) && path.equals("/api/ai/chat")) {
                handleAiChatRequest(socket, in, out, headers);
                return;
            }

            if (!"GET".equals(method)) {
                sendHttpError(out, 405, "Method Not Allowed");
                return;
            }

            if (path.equals("/api/config")) {
                sendConfigResponse(out, headers);
            } else if (path.equals("/api/panel-version")) {
                sendPanelVersionResponse(out);
            } else if (path.equals("/api/plugin-version")) {
                sendPluginVersionResponse(out);
            } else {
                serveStaticFile(out, path);
            }
        } finally {
            socket.close();
        }
    }

    private void handleAiChatRequest(Socket socket, InputStream socketIn, OutputStream out, Map<String, String> headers) throws IOException {
        // Check if AI is enabled
        if (!plugin.getConfigManager().getSettings().isAiEnabled()) {
            sendHttpError(out, 503, "AI assistant is disabled");
            return;
        }

        String aiEndpoint = plugin.getConfigManager().getSettings().getAiEndpoint();
        String aiApiKey = plugin.getConfigManager().getSettings().getAiApiKey();
        // Read request body
        int contentLength = 0;
        try {
            contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        } catch (NumberFormatException ignored) {}

        if (contentLength <= 0 || contentLength > 100000) {
            sendHttpError(out, 400, "Invalid content length");
            return;
        }

        byte[] bodyBytes = new byte[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = socketIn.read(bodyBytes, totalRead, contentLength - totalRead);
            if (read < 0) break;
            totalRead += read;
        }
        String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);

        // Forward to AI API (Ollama)
        try {
            URL url = new URL(aiEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            // Only add Authorization header if API key is provided
            if (aiApiKey != null && !aiApiKey.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + aiApiKey);
            }
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            try (OutputStream aiOut = conn.getOutputStream()) {
                aiOut.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream responseStream = responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String responseBody = responseStream != null ? readStream(responseStream) : "";
            if (responseStream != null) responseStream.close();

            if (responseCode >= 200 && responseCode < 300) {
                sendJsonResponse(out, 200, responseBody);
            } else {
                // Return the actual error from the API for debugging
                plugin.logDebug("AI API error " + responseCode + ": " + responseBody);
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("error", "AI API returned " + responseCode);
                errorResponse.addProperty("details", responseBody);
                sendJsonResponse(out, responseCode, GSON.toJson(errorResponse));
            }
        } catch (Exception e) {
            plugin.logDebug("AI API error: " + e.getMessage());
            sendHttpError(out, 502, "AI service unavailable");
        }
    }

    private String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    private void sendJsonResponse(OutputStream out, int statusCode, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendPanelVersionResponse(OutputStream out) throws IOException {
        JsonObject version = new JsonObject();
        // Read version from panel-version.properties file
        try (InputStream in = plugin.getResource("panel-version.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                version.addProperty("version", props.getProperty("version", "UNKNOWN"));
                version.addProperty("buildDate", props.getProperty("buildDate", ""));
                version.addProperty("buildNumber", Integer.parseInt(props.getProperty("buildNumber", "0")));
                version.addProperty("notes", props.getProperty("notes", ""));
            } else {
                version.addProperty("version", "UNKNOWN");
                version.addProperty("buildDate", "");
                version.addProperty("buildNumber", 0);
                version.addProperty("notes", "Version file not found");
            }
        } catch (Exception e) {
            version.addProperty("version", "ERROR");
            version.addProperty("buildDate", "");
            version.addProperty("buildNumber", 0);
            version.addProperty("notes", "Failed to read version: " + e.getMessage());
        }
        sendJson(out, version);
    }

    private void sendPluginVersionResponse(OutputStream out) throws IOException {
        JsonObject version = new JsonObject();
        String versionStr = plugin.getDescription().getVersion();
        version.addProperty("version", versionStr);
        version.addProperty("name", plugin.getDescription().getName());

        // Extract build number from version string (format: X.Ydev-BUILD or X.Y-BUILD)
        int buildNumber = 0;
        if (versionStr.contains("-")) {
            String[] parts = versionStr.split("-");
            if (parts.length > 1) {
                try {
                    buildNumber = Integer.parseInt(parts[parts.length - 1]);
                } catch (NumberFormatException ignored) {}
            }
        }
        version.addProperty("buildNumber", buildNumber);
        sendJson(out, version);
    }

    private void sendConfigResponse(OutputStream out, Map<String, String> headers) throws IOException {
        JsonObject config = new JsonObject();

        String host = plugin.getConfigManager().getSettings().getWebPanelHost();
        if (host == null || host.isEmpty()) {
            String hostHeader = headers.get("host");
            if (hostHeader != null) {
                int idx = hostHeader.indexOf(':');
                host = idx > 0 ? hostHeader.substring(0, idx) : hostHeader;
            } else {
                host = "localhost";
            }
        }

        config.addProperty("host", host);
        config.addProperty("wsPort", port);
        config.addProperty("serverName", plugin.getConfigManager().getSettings().getWebPanelServerName());
        config.addProperty("serverVersion", plugin.getDescription().getVersion());

        // AI configuration
        config.addProperty("aiEnabled", plugin.getConfigManager().getSettings().isAiEnabled());
        config.addProperty("aiModel", plugin.getConfigManager().getSettings().getAiModel());

        sendJson(out, config);
    }

    private void serveStaticFile(OutputStream out, String path) throws IOException {
        if (path.equals("/") || path.isEmpty()) path = "/index.html";
        if (path.contains("..")) {
            sendHttpError(out, 403, "Forbidden");
            return;
        }

        String resourcePath = "panel" + path;
        String ext = "";
        int dot = path.lastIndexOf('.');
        if (dot > 0) ext = path.substring(dot + 1).toLowerCase();
        String contentType = MIME_TYPES.getOrDefault(ext, "application/octet-stream");

        byte[] body = null;

        // Try serving from JAR resources first (keeps plugin folder clean)
        if (serveFromJar) {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in != null) {
                    body = in.readAllBytes();
                }
            }
        }

        // Fallback to extracted files if not found in JAR
        if (body == null) {
            Path filePath = panelDirectory.resolve(path.substring(1));
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                body = Files.readAllBytes(filePath);
            }
        }

        if (body == null) {
            sendHttpError(out, 404, "Not Found");
            return;
        }

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Cache-Control: no-cache\r\n" +
                "\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendJson(OutputStream out, JsonObject json) throws IOException {
        byte[] body = GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendHttpError(OutputStream out, int code, String msg) throws IOException {
        String body = code + " " + msg;
        String response = "HTTP/1.1 " + code + " " + msg + "\r\n" +
                "Content-Length: " + body.length() + "\r\n\r\n" + body;
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    private void sendCorsResponse(OutputStream out) throws IOException {
        String response = "HTTP/1.1 204 No Content\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization\r\n\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    // ==================== WebSocket Handling ====================

    private void handleWebSocketUpgrade(Socket socket, Map<String, String> headers, String wsKey) throws Exception {
        String acceptKey = generateAcceptKey(wsKey);

        OutputStream out = socket.getOutputStream();
        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();

        socket.setSoTimeout(0); // No timeout for WebSocket

        WebSocketConnection conn = new WebSocketConnection(socket);
        connections.add(conn);

        plugin.logDebug("WebSocket connected from: " + socket.getRemoteSocketAddress());
        debugInfo(DebugCategory.CONNECTION, "WebSocket connection opened", "From: " + socket.getRemoteSocketAddress());

        // Start reading WebSocket frames
        String disconnectReason = "Clean close";
        try {
            while (running && !socket.isClosed()) {
                String message = conn.readMessage();
                if (message == null) {
                    disconnectReason = "Client sent close frame or connection reset";
                    break;
                }
                handleWebSocketMessage(conn, message);
            }
            if (!running) {
                disconnectReason = "Server shutting down";
            }
        } catch (java.net.SocketTimeoutException e) {
            disconnectReason = "Socket timeout - No activity from client";
        } catch (java.net.SocketException e) {
            disconnectReason = "Socket error: " + (e.getMessage() != null ? e.getMessage() : "Connection reset");
        } catch (java.io.EOFException e) {
            disconnectReason = "Connection closed unexpectedly by client";
        } catch (java.io.IOException e) {
            disconnectReason = "IO error: " + (e.getMessage() != null ? e.getMessage() : "Unknown IO error");
        } catch (Exception e) {
            String errorType = e.getClass().getSimpleName();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "No message";
            disconnectReason = errorType + ": " + errorMsg;

            // Log stack trace in debug mode for troubleshooting
            if (plugin.getConfigManager().getSettings().isDebugMode()) {
                plugin.getLogger().warning("[WebPanel] Full error details:");
                e.printStackTrace();
            }
        } finally {
            connections.remove(conn);
            WebPanelSession session = sessions.remove(conn);
            if (session != null) {
                long sessionDuration = System.currentTimeMillis() - session.connectedAt;
                String durationStr = formatDuration(sessionDuration);
                if (plugin.getConfigManager().getSettings().isDebugMode()) {
                    plugin.getLogger().info("[WebPanel] Disconnected: " + session.playerName +
                            " | Duration: " + durationStr +
                            " | IP: " + (conn.getRemoteAddress() != null ? conn.getRemoteAddress() : "unknown") +
                            " | Reason: " + disconnectReason);
                } else {
                    plugin.logDebug("[WebPanel] Disconnected: " + session.playerName +
                            " | Duration: " + durationStr);
                }
                debugInfo(DebugCategory.CONNECTION, "Session disconnected",
                        "Player: " + session.playerName + ", Duration: " + durationStr + ", Reason: " + disconnectReason);
            } else {
                if (plugin.getConfigManager().getSettings().isDebugMode()) {
                    plugin.getLogger().info("[WebPanel] Disconnected: Unauthenticated connection" +
                            " | IP: " + (conn.getRemoteAddress() != null ? conn.getRemoteAddress() : "unknown") +
                            " | Reason: " + disconnectReason);
                } else {
                    plugin.logDebug("[WebPanel] Disconnected: Unauthenticated connection");
                }
                debugInfo(DebugCategory.CONNECTION, "Unauthenticated connection closed",
                        "Reason: " + disconnectReason);
            }
            conn.close();
        }
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        }
        return seconds + "s";
    }

    private String generateAcceptKey(String key) throws Exception {
        String magic = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return Base64.getEncoder().encodeToString(md.digest(magic.getBytes(StandardCharsets.UTF_8)));
    }

    private void handleWebSocketMessage(WebSocketConnection conn, String message) {
        try {
            // Skip empty or non-JSON messages
            if (message == null || message.isEmpty() || !message.trim().startsWith("{")) {
                plugin.logDebug("Skipping non-JSON message: " + (message != null ? message.substring(0, Math.min(50, message.length())) : "null"));
                return;
            }

            JsonObject json = GSON.fromJson(message, JsonObject.class);
            if (json == null || !json.has("type")) {
                plugin.logDebug("Invalid message format - missing type");
                return;
            }

            String type = json.get("type").getAsString();

            if (type.equals("AUTH_CONNECT_CODE")) {
                handleConnectCodeAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_URL_TOKEN")) {
                handleUrlTokenAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_PERMANENT_TOKEN")) {
                handlePermanentTokenAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_CONSOLE")) {
                handleConsoleAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_SESSION")) {
                handleSessionAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_DEVICE_TRUST")) {
                handleDeviceTrustAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_DEV_UUID")) {
                handleDevUuidAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_DEV_UUID_LOGIN")) {
                handleDevUuidLogin(conn, json);
                return;
            }
            if (type.equals("DEV_TOKEN_STRESS_TEST")) {
                handleDevTokenStressTest(conn, json);
                return;
            }
            if (type.equals("HEARTBEAT") || type.equals("PONG")) {
                // Update session activity
                WebPanelSession session = sessions.get(conn);
                if (session != null) {
                    session.lastActivity = System.currentTimeMillis();
                }
                return;
            }
            if (type.equals("PING")) {
                // Respond with PONG for latency measurement
                JsonObject pong = new JsonObject();
                pong.addProperty("type", "PONG");
                conn.send(GSON.toJson(pong));
                return;
            }

            WebPanelSession session = sessions.get(conn);
            if (session == null) {
                sendError(conn, "NOT_AUTHENTICATED", "You must authenticate first");
                return;
            }

            handleAuthenticatedMessage(conn, type, json, session);
        } catch (Exception e) {
            plugin.logDebug("Error processing WebSocket message: " + e.getMessage() + " - Message: " +
                (message != null ? message.substring(0, Math.min(100, message.length())) : "null"));
        }
    }

    // ==================== Authentication ====================

    public String generateConnectCode(Player player) {
        cleanExpiredCodes();
        pendingCodes.entrySet().removeIf(e -> e.getValue().playerUuid.equals(player.getUniqueId()));

        String code = generateRandomCode();
        PendingConnection pending = new PendingConnection();
        pending.playerUuid = player.getUniqueId();
        pending.playerName = player.getName();
        pending.createdAt = System.currentTimeMillis();
        // Note: moderex.webpanel is a protected permission - OPs do NOT automatically get it
        pending.hasPermission = player.hasPermission("moderex.webpanel");

        if (plugin.getHookManager().isLuckPermsEnabled()) {
            pending.prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(player);
            pending.suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(player);
        }

        pendingCodes.put(code, pending);
        return code;
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        pendingCodes.entrySet().removeIf(e -> now - e.getValue().createdAt > CONNECT_CODE_EXPIRY);
    }

    private void handleConnectCodeAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String code = data.has("code") ? data.get("code").getAsString().toUpperCase().trim() : "";
        cleanExpiredCodes();

        // Rate limit connect code attempts
        String clientIp = getClientIp(conn);
        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager != null && authManager.isRateLimited(clientIp)) {
            long remaining = authManager.getRemainingLockoutSeconds(clientIp);
            sendAuthFailed(conn, "RATE_LIMITED",
                    "Too many failed attempts. Try again in " + remaining + " seconds.");
            return;
        }

        PendingConnection pending = pendingCodes.remove(code);
        if (pending == null) {
            // Record failed attempt
            if (authManager != null) {
                authManager.recordFailedAttempt(clientIp);
            }
            sendAuthFailed(conn, "INVALID_CODE", "Invalid or expired connect code. Use /mx connect in-game.");
            return;
        }

        if (!pending.hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        createSession(conn, pending.playerUuid, pending.playerName, "MINECRAFT", pending.prefix, pending.suffix);
    }

    private void handleConsoleAuth(WebSocketConnection conn, JsonObject json) {
        // Legacy console auth - now requires permanent token
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String token = data.has("token") ? data.get("token").getAsString().trim() : "";

        if (token.isEmpty()) {
            sendAuthFailed(conn, "TOKEN_REQUIRED", "Authentication token is required. Use /mx gettoken in-game.");
            return;
        }

        // Delegate to permanent token auth
        handlePermanentTokenAuth(conn, json);
    }

    private void handleUrlTokenAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String token = data.has("token") ? data.get("token").getAsString().trim() : "";

        // Get client IP for rate limiting
        String clientIp = getClientIp(conn);

        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendAuthFailed(conn, "AUTH_UNAVAILABLE", "Authentication system not available");
            return;
        }

        // Check rate limiting
        if (authManager.isRateLimited(clientIp)) {
            long remaining = authManager.getRemainingLockoutSeconds(clientIp);
            sendAuthFailed(conn, "RATE_LIMITED",
                    "Too many failed attempts. Try again in " + remaining + " seconds.");
            return;
        }

        if (token.isEmpty()) {
            authManager.recordFailedAttempt(clientIp);
            sendAuthFailed(conn, "INVALID_TOKEN", "URL token is required");
            return;
        }

        // Consume the temp token (validates and removes it)
        WebAuthManager.AuthenticatedSession authSession = authManager.consumeTempToken(token);
        if (authSession == null) {
            authManager.recordFailedAttempt(clientIp);
            sendAuthFailed(conn, "INVALID_TOKEN", "Invalid or expired URL token. Use /mx connect to get a new one.");
            return;
        }

        // Get player info
        String prefix = "", suffix = "";
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(authSession.playerUuid);

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(authSession.playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(authSession.playerUuid);
        }

        // Get device fingerprint if provided for trust registration
        String deviceFingerprint = null;
        if (data.has("deviceFingerprint")) {
            deviceFingerprint = data.get("deviceFingerprint").getAsString().trim();
        }

        createSession(conn, authSession.playerUuid, authSession.playerName, "URL_TOKEN", prefix, suffix, authSession.sessionId, deviceFingerprint);
    }

    private void handlePermanentTokenAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String token = data.has("token") ? data.get("token").getAsString().trim() : "";

        if (token.isEmpty()) {
            sendAuthFailed(conn, "INVALID_TOKEN", "Token is required");
            return;
        }

        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendAuthFailed(conn, "AUTH_UNAVAILABLE", "Authentication system not available");
            return;
        }

        // Get client IP for rate limiting
        String clientIp = getClientIp(conn);

        // Check rate limiting
        if (authManager.isRateLimited(clientIp)) {
            long remaining = authManager.getRemainingLockoutSeconds(clientIp);
            sendAuthFailed(conn, "RATE_LIMITED",
                    "Too many failed attempts. Try again in " + remaining + " seconds.");
            return;
        }

        // Validate permanent token
        UUID playerUuid = authManager.validatePermanentToken(token, clientIp);
        if (playerUuid == null) {
            sendAuthFailed(conn, "INVALID_TOKEN", "Invalid token. Make sure you're using the correct token.");
            return;
        }

        // Check permission - moderex.webpanel is protected, OPs do NOT automatically get it
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        boolean hasPermission = false;
        String prefix = "", suffix = "";
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerUuid.toString();

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel");
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            hasPermission = plugin.getHookManager().getLuckPermsHook()
                    .hasPermission(playerUuid, "moderex.webpanel");
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(playerUuid);
        } else {
            // Without LuckPerms, we can't check offline permissions
            hasPermission = false;
        }

        if (!hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        // Get device fingerprint if provided for trust registration
        String deviceFingerprint = null;
        if (data.has("deviceFingerprint")) {
            deviceFingerprint = data.get("deviceFingerprint").getAsString().trim();
        }

        // Create authenticated session
        WebAuthManager.AuthenticatedSession authSession = authManager.createSession(playerUuid, playerName);
        createSession(conn, playerUuid, playerName, "PERMANENT_TOKEN", prefix, suffix, authSession.sessionId, deviceFingerprint);
    }

    private void handleSessionAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String sessionId = data.has("sessionId") ? data.get("sessionId").getAsString().trim() : "";

        // Get client IP for rate limiting
        String clientIp = getClientIp(conn);

        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendAuthFailed(conn, "AUTH_UNAVAILABLE", "Authentication system not available");
            return;
        }

        // Check rate limiting
        if (authManager.isRateLimited(clientIp)) {
            long remaining = authManager.getRemainingLockoutSeconds(clientIp);
            sendAuthFailed(conn, "RATE_LIMITED",
                    "Too many failed attempts. Try again in " + remaining + " seconds.");
            return;
        }

        if (sessionId.isEmpty()) {
            authManager.recordFailedAttempt(clientIp);
            sendAuthFailed(conn, "INVALID_SESSION", "Session ID is required");
            return;
        }

        // Validate existing session
        WebAuthManager.AuthenticatedSession authSession = authManager.validateSession(sessionId);
        if (authSession == null) {
            authManager.recordFailedAttempt(clientIp);
            sendSessionExpired(conn);
            return;
        }

        // Get player info
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(authSession.playerUuid);
        String prefix = "", suffix = "";

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(authSession.playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(authSession.playerUuid);
        }

        createSession(conn, authSession.playerUuid, authSession.playerName, "SESSION", prefix, suffix, sessionId, null);
    }

    private void handleDeviceTrustAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String deviceFingerprint = data.has("deviceFingerprint") ? data.get("deviceFingerprint").getAsString().trim() : "";

        // Get client IP for rate limiting
        String clientIp = getClientIp(conn);

        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendAuthFailed(conn, "AUTH_UNAVAILABLE", "Authentication system not available");
            return;
        }

        // Check rate limiting first
        if (authManager.isRateLimited(clientIp)) {
            long remaining = authManager.getRemainingLockoutSeconds(clientIp);
            sendAuthFailed(conn, "RATE_LIMITED",
                    "Too many failed attempts. Try again in " + remaining + " seconds.");
            return;
        }

        if (deviceFingerprint.isEmpty() || deviceFingerprint.length() < 16) {
            authManager.recordFailedAttempt(clientIp);
            sendAuthFailed(conn, "INVALID_FINGERPRINT", "Invalid device fingerprint");
            return;
        }

        // Validate trusted device
        WebAuthManager.TrustedDevice trustedDevice = authManager.validateTrustedDevice(deviceFingerprint);
        if (trustedDevice == null) {
            // Device not trusted - record failed attempt (prevents fingerprint brute-forcing)
            authManager.recordFailedAttempt(clientIp);
            sendAuthFailed(conn, "DEVICE_NOT_TRUSTED", "This device is not trusted. Please authenticate with your token.");
            return;
        }

        // Check player permission
        // moderex.webpanel is protected - OPs do NOT automatically get it
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(trustedDevice.playerUuid);
        boolean hasPermission = false;
        String prefix = "", suffix = "";
        String playerName = trustedDevice.playerName;

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel");
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            hasPermission = plugin.getHookManager().getLuckPermsHook()
                    .hasPermission(trustedDevice.playerUuid, "moderex.webpanel");
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(trustedDevice.playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(trustedDevice.playerUuid);
        } else {
            // Without LuckPerms, we can't check offline permissions
            hasPermission = false;
        }

        if (!hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        // Check if user has device trust enabled
        UserPanelSettings settings = getUserSettings(trustedDevice.playerUuid);
        if (!settings.deviceTrustEnabled) {
            // User has disabled device trust - require token auth
            sendAuthFailed(conn, "DEVICE_TRUST_DISABLED", "Device trust is disabled. Please authenticate with your token.");
            return;
        }

        // Create authenticated session
        WebAuthManager.AuthenticatedSession authSession = authManager.createSession(trustedDevice.playerUuid, playerName);
        createSession(conn, trustedDevice.playerUuid, playerName, "TRUSTED_DEVICE", prefix, suffix, authSession.sessionId, null);

        plugin.logDebug("Trusted device auto-login for " + playerName);
    }

    /**
     * Handle UUID-based dev authentication.
     * Creates a temporary token that is revoked when the connection closes.
     */
    private void handleDevUuidAuth(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String uuidStr = data.has("uuid") ? data.get("uuid").getAsString().trim() : "";

        if (uuidStr.isEmpty()) {
            sendAuthFailed(conn, "INVALID_UUID", "UUID is required");
            return;
        }

        UUID playerUuid;
        try {
            playerUuid = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            sendAuthFailed(conn, "INVALID_UUID", "Invalid UUID format");
            return;
        }

        // Check if current session has admin permission (must be authenticated already)
        WebPanelSession currentSession = sessions.get(conn);
        if (currentSession == null || !currentSession.hasPermission) {
            sendAuthFailed(conn, "NOT_AUTHENTICATED", "You must be authenticated to use dev UUID auth");
            return;
        }

        // Get player info
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown-" + uuidStr.substring(0, 8);

        String prefix = "", suffix = "";
        if (plugin.getHookManager().isLuckPermsEnabled()) {
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(playerUuid);
        }

        // Create a new session as the target UUID
        WebAuthManager authManager = plugin.getWebAuthManager();
        WebAuthManager.AuthenticatedSession authSession = authManager != null
                ? authManager.createSession(playerUuid, playerName)
                : null;

        createSession(conn, playerUuid, playerName, "DEV_UUID", prefix, suffix, authSession != null ? authSession.sessionId : null, null);

        plugin.logDebug("Dev UUID auth for " + playerName + " (" + playerUuid + ") - token will be revoked on disconnect");
    }

    /**
     * Handle UUID-based login from the login screen (dev mode).
     * Allows logging in by entering a player's UUID in the token field.
     * This is for development/testing purposes only.
     */
    private void handleDevUuidLogin(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        String uuidStr = data.has("uuid") ? data.get("uuid").getAsString().trim() : "";

        if (uuidStr.isEmpty()) {
            sendAuthFailed(conn, "INVALID_UUID", "UUID is required");
            return;
        }

        UUID playerUuid;
        try {
            playerUuid = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            sendAuthFailed(conn, "INVALID_UUID", "Invalid UUID format. Use: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            return;
        }

        // Get player info
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        String playerName = offlinePlayer.getName();

        if (playerName == null) {
            // Player has never joined the server
            sendAuthFailed(conn, "PLAYER_NOT_FOUND", "No player found with this UUID. They must have joined the server at least once.");
            return;
        }

        // Check if player has webpanel permission - OPs do NOT automatically get it
        boolean hasPermission = false;
        String prefix = "", suffix = "";

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel");
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            hasPermission = plugin.getHookManager().getLuckPermsHook()
                    .hasPermission(playerUuid, "moderex.webpanel");
            prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(playerUuid);
            suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(playerUuid);
        } else {
            // Without LuckPerms, we can't check offline permissions
            hasPermission = false;
        }

        if (!hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        // Create session
        WebAuthManager authManager = plugin.getWebAuthManager();
        WebAuthManager.AuthenticatedSession authSession = authManager != null
                ? authManager.createSession(playerUuid, playerName)
                : null;

        createSession(conn, playerUuid, playerName, "DEV_UUID_LOGIN", prefix, suffix, authSession != null ? authSession.sessionId : null, null);

        plugin.getLogger().info("[DevMode] UUID login: " + playerName + " (" + playerUuid + ")");
    }

    /**
     * Handle token stress test - generates many tokens to test SHA-256 validation speed.
     * This tests the hashing/validation performance, not actual token storage.
     * After completion, the user session is terminated and they must re-authenticate.
     */
    private void handleDevTokenStressTest(WebSocketConnection conn, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : json;
        int count = data.has("count") ? data.get("count").getAsInt() : 1000;
        count = Math.min(50000, Math.max(100, count));

        // Must be authenticated
        WebPanelSession session = sessions.get(conn);
        if (session == null || !session.hasPermission) {
            sendError(conn, "NOT_AUTHENTICATED", "You must be authenticated to run stress tests");
            return;
        }

        final int tokenCount = count;
        final long startTime = System.currentTimeMillis();
        final UUID playerUuid = session.playerUuid;

        // Run async to not block
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            WebAuthManager authManager = plugin.getWebAuthManager();
            if (authManager == null) {
                sendError(conn, "AUTH_UNAVAILABLE", "Auth manager not available");
                return;
            }

            // Generate and validate tokens (tests SHA-256 hashing performance)
            int processed = 0;
            for (int i = 0; i < tokenCount; i++) {
                // Generate a random token in the same format as real tokens
                StringBuilder token = new StringBuilder();
                String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
                java.security.SecureRandom random = new java.security.SecureRandom();
                for (int j = 0; j < 40; j++) {
                    if (j > 0 && j % 10 == 0) token.append("-");
                    token.append(chars.charAt(random.nextInt(chars.length())));
                }

                // Validate the token (this exercises SHA-256 hashing)
                // Tokens won't match but the hash computation is performed
                authManager.validatePermanentToken(token.toString(), "stress-test");
                processed++;

                // Send progress every 500 tokens
                if (i % 500 == 0) {
                    JsonObject progress = new JsonObject();
                    progress.addProperty("type", "TOKEN_STRESS_PROGRESS");
                    progress.addProperty("current", i);
                    progress.addProperty("total", tokenCount);
                    conn.send(GSON.toJson(progress));
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            double tokensPerSecond = duration > 0 ? tokenCount / (duration / 1000.0) : tokenCount;

            // Send completion
            JsonObject complete = new JsonObject();
            complete.addProperty("type", "TOKEN_STRESS_COMPLETE");
            complete.addProperty("total", tokenCount);
            complete.addProperty("duration", duration);
            complete.addProperty("tokensPerSecond", String.format("%.0f", tokensPerSecond));
            complete.addProperty("hashAlgorithm", "SHA-256");
            conn.send(GSON.toJson(complete));

            plugin.logDebug("Token stress test complete: " + tokenCount + " SHA-256 hashes in " + duration + "ms (" + String.format("%.0f", tokensPerSecond) + " hashes/sec)");

            // Force disconnect user session after 2 seconds
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                // Terminate the session
                sessions.remove(conn);

                // Send disconnect message
                JsonObject disconnect = new JsonObject();
                disconnect.addProperty("type", "SESSION_TERMINATED");
                disconnect.addProperty("reason", "Token stress test complete - please re-authenticate");
                conn.send(GSON.toJson(disconnect));

                // Close the WebSocket connection
                try {
                    conn.close();
                } catch (Exception ignored) {}

                plugin.logDebug("Terminated session for " + playerUuid + " after stress test");
            }, 40L); // 2 seconds (40 ticks)
        });
    }

    private String getClientIp(WebSocketConnection conn) {
        try {
            if (conn.socket != null && conn.socket.getRemoteSocketAddress() instanceof InetSocketAddress) {
                return ((InetSocketAddress) conn.socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    /**
     * Extract IP address from a session join log entry content.
     * Format: "Joined from <ip>"
     */
    private String extractIpFromJoinLog(String content) {
        if (content == null) return null;
        String prefix = "Joined from ";
        if (content.startsWith(prefix)) {
            return content.substring(prefix.length()).trim();
        }
        return null;
    }

    private boolean isFloodgatePlayer(UUID uuid) {
        if (uuid == null) return false;
        // Floodgate UUIDs start with 00000000-0000-0000
        String uuidStr = uuid.toString();
        return uuidStr.startsWith("00000000-0000-0000");
    }

    private int getWarningCount(UUID playerUuid) {
        try {
            Integer count = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_punishments WHERE player_uuid = ? AND type = 'WARN' AND active = TRUE",
                    rs -> rs.next() ? rs.getInt("cnt") : 0,
                    playerUuid.toString()
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void createSession(WebSocketConnection conn, UUID uuid, String name, String method, String prefix, String suffix) {
        createSession(conn, uuid, name, method, prefix, suffix, null, null);
    }

    private void createSession(WebSocketConnection conn, UUID uuid, String name, String method, String prefix, String suffix, String sessionId, String deviceFingerprint) {
        // Clear failed login attempts on successful authentication
        String clientIp = getClientIp(conn);
        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager != null && clientIp != null) {
            authManager.clearFailedAttempts(clientIp);
        }

        WebPanelSession session = new WebPanelSession();
        session.playerUuid = uuid;
        session.authSessionId = sessionId;
        session.playerName = name;
        session.authMethod = method;
        session.hasPermission = true;
        session.prefix = prefix;
        session.suffix = suffix;
        session.connectedAt = System.currentTimeMillis();

        session.lastActivity = System.currentTimeMillis();
        sessions.put(conn, session);

        // Register trusted device if fingerprint provided and user has device trust enabled
        UserPanelSettings settings = getUserSettings(uuid);
        boolean deviceTrusted = false;
        if (deviceFingerprint != null && !deviceFingerprint.isEmpty() && settings.deviceTrustEnabled) {
            if (authManager != null) {
                deviceTrusted = authManager.registerTrustedDevice(uuid, name, deviceFingerprint);
            }
        }

        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTH_SUCCESS");

        // Wrap data in a 'data' property as the JS client expects
        JsonObject data = new JsonObject();
        data.addProperty("playerUuid", uuid.toString());
        data.addProperty("playerName", name);
        data.addProperty("authMethod", method);
        data.addProperty("prefix", prefix != null ? prefix : "");
        data.addProperty("suffix", suffix != null ? suffix : "");
        data.addProperty("serverName", plugin.getConfigManager().getSettings().getWebPanelServerName());
        data.addProperty("pluginVersion", plugin.getDescription().getVersion());
        data.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        data.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());
        data.addProperty("deviceTrusted", deviceTrusted);

        // Include session ID for reconnection
        if (sessionId != null) {
            data.addProperty("sessionId", sessionId);
        }

        // Add rank information from LuckPerms
        JsonObject rankData = new JsonObject();
        var luckPermsHook = plugin.getHookManager() != null ? plugin.getHookManager().getLuckPermsHook() : null;
        if (luckPermsHook != null) {
            String groupName = luckPermsHook.getPrimaryGroup(uuid);
            rankData.addProperty("name", groupName != null && !groupName.isEmpty() ? groupName : "default");
            rankData.addProperty("weight", luckPermsHook.getGroupWeight(groupName != null ? groupName : "default"));
            rankData.addProperty("prefix", prefix != null ? prefix : "");

            // Get rank color from config
            var rankColors = plugin.getConfigManager().getSettings().getRankColors();
            String color = rankColors.getOrDefault(groupName != null ? groupName.toLowerCase() : "default",
                    rankColors.getOrDefault("default", "#8b5cf6"));
            rankData.addProperty("color", color);
        } else {
            rankData.addProperty("name", "Member");
            rankData.addProperty("weight", 0);
            rankData.addProperty("prefix", "");
            rankData.addProperty("color", "#8b5cf6");
        }
        data.add("rank", rankData);

        data.add("settings", settings.toJson());

        response.add("data", data);

        conn.send(GSON.toJson(response));
        plugin.getLogger().info("Web panel authenticated: " + name + " (" + method + ")");

        // Debug: Authentication successful
        debugSuccess(DebugCategory.AUTH, "Authentication successful",
                "Player: " + name + ", Method: " + method + ", Device trusted: " + deviceTrusted);
    }

    private void sendSessionExpired(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SESSION_EXPIRED");
        JsonObject data = new JsonObject();
        data.addProperty("message", "Your session has expired due to inactivity. Please reconnect.");
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    // ==================== Message Handlers ====================

    private void handleAuthenticatedMessage(WebSocketConnection conn, String type, JsonObject json, WebPanelSession session) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : new JsonObject();

        switch (type) {
            case "GET_PLAYERS" -> sendPlayerList(conn);
            case "GET_PLAYER_DETAILS" -> sendPlayerDetails(conn, data);
            case "GET_PUNISHMENTS" -> sendPunishments(conn, data);
            case "GET_COMMAND_HISTORY" -> sendCommandHistory(conn, data);
            case "GET_CHAT_LOGS" -> sendChatLogs(conn, data);
            case "GET_AUTOMOD_LOGS" -> sendAutomodLogs(conn, data);
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(conn);
            case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(conn, data, session);
            case "CREATE_AUTOMOD_RULE" -> createAutomodRule(conn, data, session);
            case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(conn, data, session);
            case "GET_ANTICHEAT_INFO" -> sendAnticheatInfo(conn);
            case "GET_ANTICHEAT_ALERTS" -> sendAnticheatAlerts(conn);
            case "GET_ANTICHEAT_CHECKS" -> sendAnticheatChecks(conn);
            case "GET_STAFF_ANTICHEAT_SETTINGS" -> sendStaffAnticheatSettings(conn, session);
            case "UPDATE_STAFF_ANTICHEAT_SETTING" -> updateStaffAnticheatSetting(conn, data, session);
            case "GET_STAFF_ALERT_PREFS" -> sendStaffAlertPrefs(conn, session);
            case "UPDATE_STAFF_ALERT_PREF" -> updateStaffAlertPref(conn, data, session);
            case "GET_ALERT_PRESETS" -> sendAlertPresets(conn);
            case "GET_WATCHLIST" -> sendWatchlist(conn);
            case "GET_SETTINGS" -> sendSettings(conn);
            case "GET_USER_SETTINGS" -> sendUserSettings(conn, session);
            case "GET_TEMPLATES" -> sendTemplates(conn);
            case "CREATE_TEMPLATE" -> createTemplate(conn, data, session);
            case "UPDATE_TEMPLATE" -> updateTemplate(conn, data, session);
            case "DELETE_TEMPLATE" -> deleteTemplate(conn, data, session);
            case "GET_STATS" -> sendStats(conn);
            case "CREATE_PUNISHMENT" -> createPunishment(conn, data, session);
            case "REVOKE_PUNISHMENT" -> revokePunishment(conn, data, session);
            case "ADD_WATCHLIST", "WATCHLIST_ADD" -> addToWatchlist(conn, data, session);
            case "REMOVE_WATCHLIST", "WATCHLIST_REMOVE" -> removeFromWatchlist(conn, data);
            case "SEND_STAFFCHAT", "STAFFCHAT_MESSAGE" -> sendStaffChatFromPanel(conn, data, session);
            case "KICK_PLAYER" -> kickPlayer(conn, data, session);
            case "CLEAR_CHAT" -> clearChat(conn, session);
            case "UPDATE_USER_SETTINGS" -> updateUserSettings(conn, data, session);
            case "MARK_CHANGELOG_READ" -> markChangelogRead(conn, data, session);
            case "SET_CHAT_LOCK" -> setChatLock(conn, data, session);
            case "SET_SLOWMODE" -> setSlowmode(conn, data, session);
            case "GET_CHAT_STATUS" -> sendChatStatus(conn);
            case "CLEAR_TRUSTED_DEVICES" -> clearTrustedDevices(conn, session);
            case "GET_TRUSTED_DEVICE_COUNT" -> sendTrustedDeviceCount(conn, session);
            case "GET_EXTERNAL_PUNISHMENTS" -> getExternalPunishments(conn, data);
            case "IMPORT_EXTERNAL_PUNISHMENTS" -> importExternalPunishments(conn, data, session);
            case "KICK_ALL" -> kickAllPlayers(conn, data, session);
            case "GET_REPLAYS" -> sendReplayList(conn);
            case "GET_REPLAY" -> sendReplayData(conn, data);
            case "GET_SERVER_STATUS" -> sendServerStatus(conn);
            case "GET_LUCKPERMS_STATUS" -> sendLuckPermsStatus(conn);
            case "GET_GEYSER_STATUS" -> sendGeyserStatus(conn);
            case "GET_MODERATION_PLUGINS" -> sendModerationPlugins(conn);
            case "GET_SERVER_SETTINGS" -> sendServerSettings(conn);
            case "UPDATE_MUTE_SETTINGS" -> updateMuteSettings(conn, data, session);
            case "UPDATE_WARN_SETTINGS" -> updateWarnSettings(conn, data, session);
            case "UPDATE_ANTICHEAT_SETTINGS" -> updateAnticheatSettings(conn, data, session);
            case "GET_DEV_CHECKLIST" -> sendDevChecklist(conn);
            case "TOGGLE_CHECKLIST_ITEM" -> toggleChecklistItem(conn, data, session);
            case "ADD_CHECKLIST_ITEM" -> addChecklistItem(conn, data, session);
            case "DELETE_CHECKLIST_ITEM" -> deleteChecklistItem(conn, data, session);
            case "TRIGGER_PLUGIN_UPDATE" -> triggerPluginUpdate(conn, session);
            case "DEV_STRESS_CREATE_PLAYERS" -> handleDevStressCreatePlayers(conn, data);
            case "DEV_STRESS_CREATE_PUNISHMENTS" -> handleDevStressCreatePunishments(conn, data);
            case "DEV_STRESS_CLEANUP" -> handleDevStressCleanup(conn);
            case "DEV_STRESS_STOP" -> handleDevStressStop(conn);
            default -> sendError(conn, "UNKNOWN_TYPE", "Unknown message type: " + type);
        }
    }

    private void sendPlayerList(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PLAYERS_DATA");

        JsonObject data = new JsonObject();
        JsonArray players = new JsonArray();

        // Add all known players from profile manager
        for (var profile : plugin.getPlayerProfileManager().getAllProfiles()) {
            JsonObject p = new JsonObject();
            p.addProperty("uuid", profile.getUuid().toString());
            p.addProperty("name", profile.getUsername());

            Player onlinePlayer = profile.getPlayer();
            boolean isOnline = onlinePlayer != null;

            p.addProperty("online", isOnline);
            if (isOnline) {
                p.addProperty("status", plugin.getVanishManager().isVanished(onlinePlayer) ? "vanished" : "online");
                p.addProperty("ip", onlinePlayer.getAddress() != null ? onlinePlayer.getAddress().getAddress().getHostAddress() : "");
            } else {
                p.addProperty("status", "offline");
                p.addProperty("ip", profile.getIpAddress() != null ? profile.getIpAddress() : "");
            }

            // Check for Floodgate/Geyser (Bedrock players have UUIDs starting with 00000000-0000-0000)
            boolean isBedrockPlayer = isFloodgatePlayer(profile.getUuid());
            p.addProperty("geyser", isBedrockPlayer);
            p.addProperty("platform", isBedrockPlayer ? "Bedrock" : "Java");
            p.addProperty("flags", plugin.getWatchlistManager().isWatched(profile.getUuid()) ? 3 : 0);
            p.addProperty("warnings", getWarningCount(profile.getUuid()));
            p.addProperty("lastJoin", profile.getLastJoin());
            p.addProperty("firstJoin", profile.getFirstJoin());

            // Check active punishments
            if (plugin.getPunishmentManager().isMuted(profile.getUuid())) {
                p.addProperty("muted", true);
            }
            if (plugin.getPunishmentManager().isBanned(profile.getUuid())) {
                p.addProperty("banned", true);
            }

            players.add(p);
        }
        data.add("players", players);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendPlayerDetails(WebSocketConnection conn, JsonObject data) {
        String uuidStr = data.has("uuid") ? data.get("uuid").getAsString() : "";

        try {
            UUID playerUuid = UUID.fromString(uuidStr);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);

            JsonObject details = new JsonObject();
            details.addProperty("uuid", uuidStr);
            details.addProperty("name", offlinePlayer.getName());
            details.addProperty("online", offlinePlayer.isOnline());
            details.addProperty("firstPlayed", offlinePlayer.getFirstPlayed());
            details.addProperty("lastPlayed", offlinePlayer.getLastPlayed());
            details.addProperty("watched", plugin.getWatchlistManager().isWatched(playerUuid));

            // Get nickname if player is online and has one
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                Player onlinePlayer = offlinePlayer.getPlayer();
                String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(onlinePlayer.displayName());
                if (!displayName.equals(onlinePlayer.getName())) {
                    details.addProperty("nickname", displayName);
                }
            }

            // Check for Geyser/Bedrock player
            boolean isBedrockPlayer = isFloodgatePlayer(playerUuid);
            details.addProperty("geyser", isBedrockPlayer);
            details.addProperty("platform", isBedrockPlayer ? "Bedrock" : "Java");

            // Get active punishment status
            details.addProperty("muted", plugin.getPunishmentManager().isMuted(playerUuid));
            details.addProperty("banned", plugin.getPunishmentManager().isBanned(playerUuid));
            details.addProperty("warnings", getWarningCount(playerUuid));

            // Get player profile info
            var profile = plugin.getPlayerProfileManager().getProfile(playerUuid);
            if (profile != null) {
                details.addProperty("ip", profile.getIpAddress());
            }

            // Fetch punishments and recent commands asynchronously
            plugin.getPunishmentManager().getPunishments(playerUuid).thenAccept(punishments -> {
                JsonArray punsArray = new JsonArray();
                for (Punishment p : punishments) {
                    punsArray.add(punishmentToJson(p));
                }
                details.add("punishments", punsArray);

                // Fetch recent commands, chat logs, automod logs, and IP history from database
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        // Fetch recent commands
                        List<JsonObject> commands = plugin.getDatabaseManager().query("""
                                SELECT command, executed_at FROM moderex_command_history
                                WHERE player_uuid = ?
                                ORDER BY executed_at DESC
                                LIMIT 50
                                """,
                                rs -> {
                                    List<JsonObject> list = new java.util.ArrayList<>();
                                    while (rs.next()) {
                                        JsonObject cmd = new JsonObject();
                                        cmd.addProperty("cmd", rs.getString("command"));
                                        cmd.addProperty("t", rs.getLong("executed_at"));
                                        list.add(cmd);
                                    }
                                    return list;
                                },
                                playerUuid.toString()
                        );

                        JsonArray cmdArray = new JsonArray();
                        for (JsonObject cmd : commands) {
                            cmdArray.add(cmd);
                        }
                        details.add("recentCommands", cmdArray);

                        // Fetch activity logs (chat, automod, IP changes) if activity log is enabled
                        if (plugin.getActivityLogManager() != null && plugin.getActivityLogManager().isEnabled()) {
                            int maxChatLogs = plugin.getConfigManager().getSettings().getMaxChatLogs();
                            int maxCommandLogs = plugin.getConfigManager().getSettings().getMaxCommandLogs();

                            // Chat logs
                            List<ActivityLogEntry> chatLogs = plugin.getActivityLogManager().getEntries(
                                    playerUuid, List.of(ActivityType.CHAT), 0, 1, Math.min(maxChatLogs, 100));
                            JsonArray chatArray = new JsonArray();
                            for (ActivityLogEntry entry : chatLogs) {
                                JsonObject log = new JsonObject();
                                log.addProperty("t", entry.getTimestamp());
                                log.addProperty("content", entry.getContent());
                                log.addProperty("server", entry.getServer());
                                chatArray.add(log);
                            }
                            details.add("chatLogs", chatArray);

                            // Automod logs
                            List<ActivityLogEntry> automodLogs = plugin.getActivityLogManager().getEntries(
                                    playerUuid, List.of(ActivityType.AUTOMOD_TRIGGER), 0, 1, 50);
                            JsonArray automodArray = new JsonArray();
                            for (ActivityLogEntry entry : automodLogs) {
                                JsonObject log = new JsonObject();
                                log.addProperty("t", entry.getTimestamp());
                                log.addProperty("rule", entry.getExtra()); // Rule name stored in extra
                                log.addProperty("content", entry.getContent());
                                log.addProperty("server", entry.getServer());
                                automodArray.add(log);
                            }
                            details.add("automodLogs", automodArray);

                            // IP history
                            List<ActivityLogEntry> ipLogs = plugin.getActivityLogManager().getEntries(
                                    playerUuid, List.of(ActivityType.IP_CHANGE, ActivityType.SESSION_JOIN), 0, 1, 20);
                            JsonArray ipArray = new JsonArray();
                            Set<String> seenIps = new HashSet<>();
                            for (ActivityLogEntry entry : ipLogs) {
                                String ip = entry.getType() == ActivityType.IP_CHANGE ?
                                        entry.getContent() : // IP_CHANGE stores new IP in content
                                        extractIpFromJoinLog(entry.getContent()); // SESSION_JOIN has "Joined from IP"
                                if (ip != null && !ip.isEmpty() && seenIps.add(ip)) {
                                    JsonObject ipEntry = new JsonObject();
                                    ipEntry.addProperty("ip", ip);
                                    ipEntry.addProperty("t", entry.getTimestamp());
                                    ipEntry.addProperty("server", entry.getServer());
                                    ipArray.add(ipEntry);
                                }
                            }
                            details.add("ipHistory", ipArray);
                        } else {
                            details.add("chatLogs", new JsonArray());
                            details.add("automodLogs", new JsonArray());
                            details.add("ipHistory", new JsonArray());
                        }

                        JsonObject response = new JsonObject();
                        response.addProperty("type", "PLAYER_DETAILS");
                        response.add("data", details);
                        conn.send(GSON.toJson(response));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to fetch player details: " + e.getMessage());
                        // Send response with empty arrays if query fails
                        details.add("recentCommands", new JsonArray());
                        details.add("chatLogs", new JsonArray());
                        details.add("automodLogs", new JsonArray());
                        details.add("ipHistory", new JsonArray());
                        JsonObject response = new JsonObject();
                        response.addProperty("type", "PLAYER_DETAILS");
                        response.add("data", details);
                        conn.send(GSON.toJson(response));
                    }
                });
            });
        } catch (IllegalArgumentException e) {
            sendError(conn, "INVALID_UUID", "Invalid player UUID: " + uuidStr);
        }
    }

    private void sendPunishments(WebSocketConnection conn, JsonObject filters) {
        int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 100;
        plugin.getPunishmentManager().getRecentPunishments(limit).thenAccept(punishments -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "PUNISHMENTS_DATA");

            JsonObject data = new JsonObject();
            JsonArray array = new JsonArray();
            for (Punishment p : punishments) {
                array.add(punishmentToJson(p));
            }
            data.add("punishments", array);
            response.add("data", data);
            conn.send(GSON.toJson(response));
        });
    }

    private void sendCommandHistory(WebSocketConnection conn, JsonObject filters) {
        String uuidStr = filters.has("uuid") ? filters.get("uuid").getAsString() : "";
        int page = filters.has("page") ? filters.get("page").getAsInt() : 1;
        int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 50;
        String search = filters.has("search") ? filters.get("search").getAsString() : "";

        if (uuidStr.isEmpty()) {
            sendError(conn, "MISSING_UUID", "Player UUID is required");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                UUID playerUuid = UUID.fromString(uuidStr);
                int offset = (page - 1) * limit;

                // Build query with optional search filter
                String searchClause = search.isEmpty() ? "" : " AND command LIKE ?";
                String countQuery = "SELECT COUNT(*) FROM moderex_command_history WHERE player_uuid = ?" + searchClause;
                String dataQuery = "SELECT command, executed_at, server FROM moderex_command_history WHERE player_uuid = ?" +
                        searchClause + " ORDER BY executed_at DESC LIMIT ? OFFSET ?";

                // Get total count
                int total = plugin.getDatabaseManager().query(countQuery, rs -> {
                    if (rs.next()) return rs.getInt(1);
                    return 0;
                }, search.isEmpty() ? new Object[]{uuidStr} : new Object[]{uuidStr, "%" + search + "%"});

                // Get paginated data
                List<JsonObject> commands = plugin.getDatabaseManager().query(dataQuery, rs -> {
                    List<JsonObject> list = new java.util.ArrayList<>();
                    while (rs.next()) {
                        JsonObject cmd = new JsonObject();
                        cmd.addProperty("cmd", rs.getString("command"));
                        cmd.addProperty("t", rs.getLong("executed_at"));
                        String server = rs.getString("server");
                        if (server != null) cmd.addProperty("server", server);
                        list.add(cmd);
                    }
                    return list;
                }, search.isEmpty() ?
                        new Object[]{uuidStr, limit, offset} :
                        new Object[]{uuidStr, "%" + search + "%", limit, offset});

                JsonObject response = new JsonObject();
                response.addProperty("type", "COMMAND_HISTORY_DATA");

                JsonObject data = new JsonObject();
                JsonArray cmdArray = new JsonArray();
                for (JsonObject cmd : commands) {
                    cmdArray.add(cmd);
                }
                data.add("commands", cmdArray);
                data.addProperty("page", page);
                data.addProperty("limit", limit);
                data.addProperty("total", total);
                data.addProperty("totalPages", (int) Math.ceil((double) total / limit));
                data.addProperty("uuid", uuidStr);

                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to fetch command history: " + e.getMessage());
                sendError(conn, "DATABASE_ERROR", "Failed to fetch command history");
            }
        });
    }

    private void sendChatLogs(WebSocketConnection conn, JsonObject filters) {
        plugin.logDebug("[WebPanel] Received GET_CHAT_LOGS request");
        String uuidStr = filters.has("uuid") ? filters.get("uuid").getAsString() : "";
        int page = filters.has("page") ? filters.get("page").getAsInt() : 1;
        int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 50;
        String search = filters.has("search") ? filters.get("search").getAsString() : "";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int offset = (page - 1) * limit;
                List<JsonObject> logs;
                int total;

                if (uuidStr.isEmpty()) {
                    // Get all chat logs
                    plugin.logDebug("[WebPanel] Fetching all chat logs (page=" + page + ", limit=" + limit + ")");
                    logs = plugin.getDatabaseManager().getAllChatLogs(limit, offset);
                    total = plugin.getDatabaseManager().getTotalChatLogCount();
                } else {
                    // Get chat logs for specific player
                    plugin.logDebug("[WebPanel] Fetching chat logs for player " + uuidStr);
                    logs = plugin.getDatabaseManager().getChatLogs(uuidStr, limit, offset);
                    total = plugin.getDatabaseManager().getChatLogCount(uuidStr);
                }

                JsonObject response = new JsonObject();
                response.addProperty("type", "CHAT_LOGS_DATA");

                JsonObject data = new JsonObject();
                JsonArray logsArray = new JsonArray();
                for (JsonObject log : logs) {
                    logsArray.add(log);
                }
                data.add("logs", logsArray);
                data.addProperty("page", page);
                data.addProperty("limit", limit);
                data.addProperty("total", total);
                data.addProperty("totalPages", (int) Math.ceil((double) total / limit));
                if (!uuidStr.isEmpty()) {
                    data.addProperty("uuid", uuidStr);
                }

                response.add("data", data);
                conn.send(GSON.toJson(response));
                plugin.logDebug("[WebPanel] Sent CHAT_LOGS_DATA with " + logs.size() + " logs");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to fetch chat logs: " + e.getMessage());
                sendError(conn, "DATABASE_ERROR", "Failed to fetch chat logs");
            }
        });
    }

    private void sendAutomodLogs(WebSocketConnection conn, JsonObject filters) {
        String uuidStr = filters.has("uuid") ? filters.get("uuid").getAsString() : "";
        int page = filters.has("page") ? filters.get("page").getAsInt() : 1;
        int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 50;
        String search = filters.has("search") ? filters.get("search").getAsString() : "";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (plugin.getActivityLogManager() == null || !plugin.getActivityLogManager().isEnabled()) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "AUTOMOD_LOGS_DATA");
                    JsonObject data = new JsonObject();
                    data.add("logs", new JsonArray());
                    data.addProperty("total", 0);
                    data.addProperty("page", 1);
                    response.add("data", data);
                    conn.send(GSON.toJson(response));
                    return;
                }

                int offset = (page - 1) * limit;

                // Build query - optionally filter by player UUID
                String whereClause = "WHERE type = 'AUTOMOD_TRIGGER'";
                List<Object> params = new java.util.ArrayList<>();

                if (!uuidStr.isEmpty()) {
                    whereClause += " AND player_uuid = ?";
                    params.add(uuidStr);
                }

                if (!search.isEmpty()) {
                    whereClause += " AND (content LIKE ? OR extra LIKE ?)";
                    params.add("%" + search + "%");
                    params.add("%" + search + "%");
                }

                String countQuery = "SELECT COUNT(*) FROM moderex_activity_log " + whereClause;
                String dataQuery = "SELECT * FROM moderex_activity_log " + whereClause +
                        " ORDER BY timestamp DESC LIMIT ? OFFSET ?";

                // Get total count
                int total = plugin.getDatabaseManager().query(countQuery, rs -> {
                    if (rs.next()) return rs.getInt(1);
                    return 0;
                }, params.toArray());

                // Add pagination params
                params.add(limit);
                params.add(offset);

                // Get paginated data
                List<JsonObject> logs = plugin.getDatabaseManager().query(dataQuery, rs -> {
                    List<JsonObject> list = new java.util.ArrayList<>();
                    while (rs.next()) {
                        JsonObject log = new JsonObject();
                        log.addProperty("t", rs.getLong("timestamp"));
                        log.addProperty("playerUuid", rs.getString("player_uuid"));
                        log.addProperty("playerName", rs.getString("player_name"));
                        log.addProperty("rule", rs.getString("extra")); // Rule name in extra field
                        log.addProperty("content", rs.getString("content"));
                        log.addProperty("server", rs.getString("server"));
                        list.add(log);
                    }
                    return list;
                }, params.toArray());

                JsonObject response = new JsonObject();
                response.addProperty("type", "AUTOMOD_LOGS_DATA");

                JsonObject data = new JsonObject();
                JsonArray logsArray = new JsonArray();
                for (JsonObject log : logs) {
                    logsArray.add(log);
                }
                data.add("logs", logsArray);
                data.addProperty("page", page);
                data.addProperty("limit", limit);
                data.addProperty("total", total);
                data.addProperty("totalPages", (int) Math.ceil((double) total / limit));

                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to fetch automod logs: " + e.getMessage());
                sendError(conn, "DATABASE_ERROR", "Failed to fetch automod logs");
            }
        });
    }

    private void sendAutomodRules(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTOMOD_RULES_DATA");
        JsonObject data = new JsonObject();
        JsonArray rules = new JsonArray();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            JsonObject r = new JsonObject();
            r.addProperty("id", rule.getId());
            r.addProperty("name", rule.getName());
            r.addProperty("type", rule.getType().name());
            r.addProperty("enabled", rule.isEnabled());
            r.addProperty("builtIn", rule.isBuiltIn());
            r.addProperty("priority", rule.getPriority());
            r.addProperty("exactMatch", rule.isExactMatch());

            // Add blacklisted words
            com.google.gson.JsonArray blacklist = new com.google.gson.JsonArray();
            for (String word : rule.getBlacklistedWords()) {
                blacklist.add(word);
            }
            r.add("blacklistedWords", blacklist);

            // Add whitelist/exclusion words
            com.google.gson.JsonArray whitelist = new com.google.gson.JsonArray();
            for (String word : rule.getExclusionWords()) {
                whitelist.add(word);
            }
            r.add("exclusionWords", whitelist);
            r.add("whitelist", whitelist); // Alias for clarity in frontend

            // Add auto punishment info if present
            if (rule.getAutoPunishment() != null) {
                JsonObject ap = new JsonObject();
                ap.addProperty("enabled", rule.getAutoPunishment().isEnabled());
                ap.addProperty("type", rule.getAutoPunishment().getType().name());
                ap.addProperty("duration", rule.getAutoPunishment().getDuration());
                ap.addProperty("triggerCount", rule.getAutoPunishment().getTriggerCount());
                ap.addProperty("timeWindow", rule.getAutoPunishment().getTimeWindow());
                r.add("autoPunishment", ap);
            }

            rules.add(r);
        }
        data.add("rules", rules);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void updateAutomodRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            // Check permission
            if (!hasAutomodPermission(session.playerUuid)) {
                sendError(conn, "FORBIDDEN", "You do not have permission to modify automod rules (moderex.admin.automod)");
                return;
            }

            // Support multiple formats: { id: "..." } or { ruleId: "...", rule: {...} }
            String ruleId = null;
            JsonObject ruleData = data;

            if (data.has("ruleId")) {
                ruleId = data.get("ruleId").getAsString();
                if (data.has("rule") && data.get("rule").isJsonObject()) {
                    ruleData = data.getAsJsonObject("rule");
                }
            } else if (data.has("id")) {
                ruleId = data.get("id").getAsString();
            } else if (ruleData.has("id")) {
                ruleId = ruleData.get("id").getAsString();
            }

            if (ruleId == null) {
                sendError(conn, "INVALID_REQUEST", "Missing rule ID");
                return;
            }

            AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);

            if (rule == null) {
                sendError(conn, "NOT_FOUND", "Rule not found: " + ruleId);
                return;
            }

            // Update rule properties from ruleData
            if (ruleData.has("enabled")) rule.setEnabled(ruleData.get("enabled").getAsBoolean());
            if (ruleData.has("priority")) rule.setPriority(ruleData.get("priority").getAsInt());
            if (ruleData.has("exactMatch")) rule.setExactMatch(ruleData.get("exactMatch").getAsBoolean());

            // Update spam protection settings
            if (ruleData.has("spamMessageCount")) rule.setSpamMessageCount(ruleData.get("spamMessageCount").getAsInt());
            if (ruleData.has("spamTimeWindowSeconds")) rule.setSpamTimeWindowSeconds(ruleData.get("spamTimeWindowSeconds").getAsInt());
            if (ruleData.has("spamDetectSimilar")) rule.setSpamDetectSimilar(ruleData.get("spamDetectSimilar").getAsBoolean());
            if (ruleData.has("spamSimilarityThreshold")) rule.setSpamSimilarityThreshold(ruleData.get("spamSimilarityThreshold").getAsDouble());

            // Update caps filter settings
            if (ruleData.has("capsMaxPercentage")) rule.setCapsMaxPercentage(ruleData.get("capsMaxPercentage").getAsInt());
            if (ruleData.has("capsMinLength")) rule.setCapsMinLength(ruleData.get("capsMinLength").getAsInt());

            // Update AFK settings
            if (ruleData.has("afkTimeoutMinutes")) rule.setAfkTimeoutMinutes(ruleData.get("afkTimeoutMinutes").getAsInt());
            if (ruleData.has("afkKickEnabled")) rule.setAfkKickEnabled(ruleData.get("afkKickEnabled").getAsBoolean());

            // Update anticheat settings
            if (ruleData.has("anticheatAlertThreshold")) rule.setAnticheatAlertThreshold(ruleData.get("anticheatAlertThreshold").getAsInt());
            if (ruleData.has("anticheatTimeWindowSeconds")) rule.setAnticheatTimeWindowSeconds(ruleData.get("anticheatTimeWindowSeconds").getAsInt());

            // Update blacklisted words
            if (ruleData.has("blacklistedWords")) {
                List<String> words = new ArrayList<>();
                ruleData.getAsJsonArray("blacklistedWords").forEach(e -> words.add(e.getAsString()));
                rule.setBlacklistedWords(words);
            } else if (ruleData.has("blacklistedPhrases")) {
                List<String> words = new ArrayList<>();
                ruleData.getAsJsonArray("blacklistedPhrases").forEach(e -> words.add(e.getAsString()));
                rule.setBlacklistedPhrases(words);
            }

            // Update exclusion/whitelist words
            if (ruleData.has("exclusionWords") || ruleData.has("whitelist") || ruleData.has("exclusionPhrases")) {
                List<String> words = new ArrayList<>();
                JsonArray arr = ruleData.has("exclusionWords") ? ruleData.getAsJsonArray("exclusionWords") :
                        ruleData.has("exclusionPhrases") ? ruleData.getAsJsonArray("exclusionPhrases") :
                        ruleData.getAsJsonArray("whitelist");
                if (arr != null) {
                    arr.forEach(e -> words.add(e.getAsString()));
                    rule.setExclusionWords(words);
                }
            }

            // Handle frontend 'exceptions' array (string array of exception phrases)
            if (ruleData.has("exceptions") && ruleData.get("exceptions").isJsonArray()) {
                List<String> exceptions = new ArrayList<>();
                ruleData.getAsJsonArray("exceptions").forEach(e -> exceptions.add(e.getAsString()));
                rule.setExclusionPhrases(exceptions);
            }

            // Handle frontend 'conditions' array format
            // Conditions format: [{ kind: 'contains', value: 'phrase1, phrase2' }, { kind: 'regex', value: '...' }]
            if (ruleData.has("conditions") && ruleData.get("conditions").isJsonArray()) {
                JsonArray conditions = ruleData.getAsJsonArray("conditions");
                List<String> blacklistedPhrases = new ArrayList<>();

                for (int i = 0; i < conditions.size(); i++) {
                    JsonObject cond = conditions.get(i).getAsJsonObject();
                    String kind = cond.has("kind") ? cond.get("kind").getAsString() : "contains";
                    String value = cond.has("value") ? cond.get("value").getAsString() : "";

                    if ("contains".equals(kind) && !value.isEmpty()) {
                        // Split by comma for comma-separated phrases
                        for (String phrase : value.split(",")) {
                            String trimmed = phrase.trim();
                            if (!trimmed.isEmpty()) {
                                blacklistedPhrases.add(trimmed);
                            }
                        }
                    } else if ("regex".equals(kind) && !value.isEmpty()) {
                        // For regex conditions, store as-is with a prefix
                        blacklistedPhrases.add(value);
                        // Also set filter mode to REGEX if this is the primary mode
                        rule.setFilterMode(AutomodRule.FilterMode.REGEX);
                    }
                }

                if (!blacklistedPhrases.isEmpty()) {
                    rule.setBlacklistedPhrases(blacklistedPhrases);
                }
            }

            // Update rule name
            if (ruleData.has("name") && !ruleData.get("name").isJsonNull()) {
                String newName = ruleData.get("name").getAsString().trim();
                if (!newName.isEmpty() && !rule.isBuiltIn()) {
                    rule.setName(newName);
                }
            }

            // Update applies to settings (chat/nicknames/both)
            if (ruleData.has("applyToNicknames")) {
                rule.setApplyToNicknames(ruleData.get("applyToNicknames").getAsBoolean());
            }
            if (ruleData.has("nicknameOnly")) {
                rule.setNicknameOnly(ruleData.get("nicknameOnly").getAsBoolean());
            }

            // Update block flag (maps to FlagAction.BLOCK)
            if (ruleData.has("block")) {
                boolean shouldBlock = ruleData.get("block").getAsBoolean();
                rule.setFlagAction(shouldBlock ? AutomodRule.FlagAction.BLOCK : AutomodRule.FlagAction.LOG_ONLY);
            }

            // Update action from frontend format
            if (ruleData.has("action") && ruleData.get("action").isJsonObject()) {
                JsonObject actionObj = ruleData.getAsJsonObject("action");
                String kind = actionObj.has("kind") ? actionObj.get("kind").getAsString() : "none";
                String extra = actionObj.has("extra") ? actionObj.get("extra").getAsString() : "";
                String duration = actionObj.has("duration") ? actionObj.get("duration").getAsString() : "";

                if ("none".equals(kind)) {
                    rule.setAutoPunishment(null);
                } else {
                    AutomodRule.AutoPunishment ap = rule.getAutoPunishment();
                    if (ap == null) {
                        ap = new AutomodRule.AutoPunishment();
                        rule.setAutoPunishment(ap);
                    }
                    ap.setEnabled(true);
                    try {
                        ap.setType(com.blockforge.moderex.punishment.PunishmentType.valueOf(kind.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        ap.setType(com.blockforge.moderex.punishment.PunishmentType.WARN);
                    }
                    ap.setReason(extra);
                    // Parse duration string to milliseconds (e.g., "1d" -> 86400000)
                    if (duration != null && !duration.isEmpty()) {
                        if (duration.equalsIgnoreCase("perm") || duration.equalsIgnoreCase("permanent")) {
                            ap.setDuration(-1);
                        } else {
                            ap.setDuration(DurationParser.parse(duration));
                        }
                    }
                }
            }

            // Update threshold from frontend format
            if (ruleData.has("threshold") && ruleData.get("threshold").isJsonObject()) {
                JsonObject thrObj = ruleData.getAsJsonObject("threshold");
                if (rule.getAutoPunishment() == null) {
                    AutomodRule.AutoPunishment newAp = new AutomodRule.AutoPunishment();
                    newAp.setType(com.blockforge.moderex.punishment.PunishmentType.WARN); // Default type
                    rule.setAutoPunishment(newAp);
                }
                if (thrObj.has("hits")) {
                    rule.getAutoPunishment().setTriggerCount(thrObj.get("hits").getAsInt());
                }
                if (thrObj.has("windowMins")) {
                    // Convert minutes to milliseconds for storage (timeWindow is stored in ms)
                    rule.getAutoPunishment().setTimeWindow(thrObj.get("windowMins").getAsInt() * 60000L);
                }
            }

            // Save rule (this also broadcasts the update via AutomodManager)
            plugin.logDebug("[WebPanel] About to save rule: " + rule.getId());
            try {
                plugin.getAutomodManager().saveRule(rule);
                plugin.logDebug("[WebPanel] saveRule completed successfully");
            } catch (Exception saveEx) {
                plugin.logError("[WebPanel] saveRule threw exception", saveEx);
                throw saveEx;
            }

            plugin.logDebug("[WebPanel] Building response for rule update");
            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_UPDATED");
            // Data must be nested under "data" property for frontend WebSocket handler
            JsonObject responseData = new JsonObject();
            responseData.addProperty("id", ruleId);
            response.add("data", responseData);

            plugin.logDebug("[WebPanel] Sending AUTOMOD_RULE_UPDATED response");
            conn.send(GSON.toJson(response));
            plugin.logDebug("[WebPanel] Response sent successfully");

            plugin.logDebug("[WebPanel] Automod rule updated: " + rule.getName() + " by " + session.playerName);
            debugSuccess(DebugCategory.AUTOMOD, "Automod rule updated",
                    "Rule: " + rule.getName() + ", By: " + session.playerName);
        } catch (Exception e) {
            plugin.logError("[WebPanel] updateAutomodRule exception: " + e.getClass().getName() + " - " + e.getMessage(), e);
            try {
                sendError(conn, "UPDATE_ERROR", "Failed to update rule: " + e.getMessage());
            } catch (Exception sendEx) {
                plugin.logError("[WebPanel] Failed to send error response", sendEx);
            }
            debugError(ErrorCode.AUTOMOD_RULE_UPDATE_FAILED, "Error: " + e.getMessage());
        }
    }

    private void createAutomodRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            // Check permission
            if (!hasAutomodPermission(session.playerUuid)) {
                sendError(conn, "FORBIDDEN", "You do not have permission to create automod rules (moderex.admin.automod)");
                return;
            }

            String name = data.get("name").getAsString();
            AutomodRule rule = plugin.getAutomodManager().createRule(name);

            // Set initial properties
            if (data.has("exactMatch")) rule.setExactMatch(data.get("exactMatch").getAsBoolean());
            if (data.has("priority")) rule.setPriority(data.get("priority").getAsInt());

            // Set blacklisted words
            if (data.has("blacklistedWords")) {
                List<String> words = new ArrayList<>();
                data.getAsJsonArray("blacklistedWords").forEach(e -> words.add(e.getAsString()));
                rule.setBlacklistedWords(words);
            }

            // Set exclusion/whitelist words
            if (data.has("exclusionWords") || data.has("whitelist")) {
                JsonArray arr = data.has("exclusionWords") ?
                        data.getAsJsonArray("exclusionWords") :
                        data.getAsJsonArray("whitelist");
                List<String> exclusions = new ArrayList<>();
                arr.forEach(e -> exclusions.add(e.getAsString()));
                rule.setExclusionWords(exclusions);
            }

            // Save rule (this also broadcasts the update via AutomodManager)
            plugin.getAutomodManager().saveRule(rule);

            // Return full rule data so frontend can immediately use it
            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_CREATED");
            JsonObject responseData = serializeRule(rule);
            response.add("data", responseData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Automod rule created: " + name + " (id=" + rule.getId() + ") by " + session.playerName);
            debugSuccess(DebugCategory.AUTOMOD, "Automod rule created",
                    "Rule: " + name + ", By: " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "CREATE_ERROR", "Failed to create rule: " + e.getMessage());
            plugin.logError("Failed to create automod rule from web panel", e);
            debugError(ErrorCode.AUTOMOD_RULE_CREATE_FAILED, "Error: " + e.getMessage());
        }
    }

    private void deleteAutomodRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            // Check permission
            if (!hasAutomodPermission(session.playerUuid)) {
                sendError(conn, "FORBIDDEN", "You do not have permission to delete automod rules (moderex.admin.automod)");
                return;
            }

            String ruleId = data.get("id").getAsString();
            AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);

            if (rule == null) {
                sendError(conn, "NOT_FOUND", "Rule not found");
                return;
            }

            if (rule.isBuiltIn()) {
                sendError(conn, "FORBIDDEN", "Cannot delete built-in rules");
                return;
            }

            // Delete rule (this also broadcasts the update via AutomodManager)
            plugin.getAutomodManager().deleteRule(ruleId);

            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_DELETED");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("id", ruleId);
            response.add("data", responseData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Automod rule deleted: " + rule.getName() + " by " + session.playerName);
            debugSuccess(DebugCategory.AUTOMOD, "Automod rule deleted",
                    "Rule: " + rule.getName() + ", By: " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "DELETE_ERROR", "Failed to delete rule: " + e.getMessage());
            plugin.logError("Failed to delete automod rule from web panel", e);
            debugError(ErrorCode.AUTOMOD_RULE_DELETE_FAILED, "Error: " + e.getMessage());
        }
    }

    public void broadcastAutomodRules() {
        try {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", "AUTOMOD_RULES_DATA");
            JsonObject data = new JsonObject();
            JsonArray rules = new JsonArray();

            for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
                try {
                    JsonObject r = new JsonObject();
                    r.addProperty("id", rule.getId());
                    r.addProperty("name", rule.getName() != null ? rule.getName() : "Unknown");
                    r.addProperty("type", rule.getType() != null ? rule.getType().name() : "WORD_FILTER");
                    r.addProperty("enabled", rule.isEnabled());
                    r.addProperty("builtIn", rule.isBuiltIn());
                    r.addProperty("priority", rule.getPriority());
                    r.addProperty("description", rule.getDescription() != null ? rule.getDescription() : "");

                    // For ANTICHEAT rules, send minimal data to reduce payload size
                    if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                        r.addProperty("anticheatName", rule.getAnticheatName() != null ? rule.getAnticheatName() : "");
                        r.addProperty("checkName", rule.getCheckName() != null ? rule.getCheckName() : "");
                        r.addProperty("anticheatAlertThreshold", rule.getAnticheatAlertThreshold());
                        r.addProperty("anticheatTimeWindowSeconds", rule.getAnticheatTimeWindowSeconds());

                        // Include auto punishment for anticheat rules
                        if (rule.getAutoPunishment() != null) {
                            JsonObject ap = new JsonObject();
                            ap.addProperty("enabled", rule.getAutoPunishment().isEnabled());
                            String punishType = rule.getAutoPunishment().getType() != null ?
                                    rule.getAutoPunishment().getType().name() : "WARN";
                            ap.addProperty("type", punishType);
                            ap.addProperty("duration", rule.getAutoPunishment().getDuration());
                            ap.addProperty("triggerCount", rule.getAutoPunishment().getTriggerCount());
                            ap.addProperty("reason", rule.getAutoPunishment().getReason() != null ? rule.getAutoPunishment().getReason() : "");
                            r.add("autoPunishment", ap);

                            // Frontend action format
                            JsonObject action = new JsonObject();
                            action.addProperty("kind", rule.getAutoPunishment().isEnabled() && rule.getAutoPunishment().getType() != null ?
                                    rule.getAutoPunishment().getType().name().toLowerCase() : "none");
                            action.addProperty("extra", rule.getAutoPunishment().getReason() != null ? rule.getAutoPunishment().getReason() : "");
                            long durationMs = rule.getAutoPunishment().getDuration();
                            String durationStr = durationMs == -1 ? "perm" : durationMs == 0 ? "" : DurationParser.format(durationMs);
                            action.addProperty("duration", durationStr);
                            r.add("action", action);
                        } else {
                            JsonObject action = new JsonObject();
                            action.addProperty("kind", "none");
                            action.addProperty("extra", "");
                            action.addProperty("duration", "");
                            r.add("action", action);
                        }

                        // Threshold for frontend
                        JsonObject threshold = new JsonObject();
                        threshold.addProperty("hits", rule.getAutoPunishment() != null ?
                                rule.getAutoPunishment().getTriggerCount() : 3);
                        threshold.addProperty("windowMins", rule.getAutoPunishment() != null ?
                                (rule.getAutoPunishment().getTimeWindow() / 60000) : 5);
                        r.add("threshold", threshold);

                        // Empty arrays for frontend compatibility
                        r.add("blacklistedWords", new com.google.gson.JsonArray());
                        r.add("blacklistedPhrases", new com.google.gson.JsonArray());
                        r.add("exclusionWords", new com.google.gson.JsonArray());
                        r.add("conditions", new com.google.gson.JsonArray());
                        r.add("exceptions", new com.google.gson.JsonArray());

                        rules.add(r);
                        continue; // Skip full serialization for anticheat rules
                    }

                    r.addProperty("exactMatch", rule.isExactMatch());

                    // Spam protection settings
                    r.addProperty("spamMessageCount", rule.getSpamMessageCount());
                    r.addProperty("spamTimeWindowSeconds", rule.getSpamTimeWindowSeconds());
                    r.addProperty("spamDetectSimilar", rule.isSpamDetectSimilar());
                    r.addProperty("spamSimilarityThreshold", rule.getSpamSimilarityThreshold());

                    // Caps filter settings
                    r.addProperty("capsMaxPercentage", rule.getCapsMaxPercentage());
                    r.addProperty("capsMinLength", rule.getCapsMinLength());

                    // AFK settings
                    r.addProperty("afkTimeoutMinutes", rule.getAfkTimeoutMinutes());
                    r.addProperty("afkKickEnabled", rule.isAfkKickEnabled());

                    // Anticheat settings (for non-anticheat rules, these will be empty)
                    r.addProperty("anticheatName", rule.getAnticheatName() != null ? rule.getAnticheatName() : "");
                    r.addProperty("checkName", rule.getCheckName() != null ? rule.getCheckName() : "");
                    r.addProperty("anticheatAlertThreshold", rule.getAnticheatAlertThreshold());
                    r.addProperty("anticheatTimeWindowSeconds", rule.getAnticheatTimeWindowSeconds());

                    // Flag action and filter mode
                    r.addProperty("flagAction", rule.getFlagAction() != null ? rule.getFlagAction().name() : "BLOCK");
                    r.addProperty("filterMode", rule.getFilterMode() != null ? rule.getFilterMode().name() : "CONTAINS_PHRASE");
                    // Block flag for frontend - true if flagAction is BLOCK
                    r.addProperty("block", rule.getFlagAction() == AutomodRule.FlagAction.BLOCK);

                    // Applies to settings (chat/nicknames/both)
                    r.addProperty("applyToNicknames", rule.isApplyToNicknames());
                    r.addProperty("nicknameOnly", rule.isNicknameOnly());

                    // Blacklisted words/phrases - with null safety
                    com.google.gson.JsonArray blacklist = new com.google.gson.JsonArray();
                    List<String> blacklistedWords = rule.getBlacklistedWords();
                    if (blacklistedWords != null) {
                        for (String word : blacklistedWords) {
                            if (word != null) blacklist.add(word);
                        }
                    }
                    r.add("blacklistedWords", blacklist);
                    r.add("blacklistedPhrases", blacklist);

                    // Exclusion/whitelist words - with null safety
                    com.google.gson.JsonArray whitelist = new com.google.gson.JsonArray();
                    List<String> exclusionWords = rule.getExclusionWords();
                    if (exclusionWords != null) {
                        for (String word : exclusionWords) {
                            if (word != null) whitelist.add(word);
                        }
                    }
                    r.add("exclusionWords", whitelist);
                    r.add("exclusionPhrases", whitelist);
                    r.add("whitelist", whitelist);

                    // Exceptions array (string array for frontend) - with null safety
                    com.google.gson.JsonArray exceptionsArray = new com.google.gson.JsonArray();
                    List<String> exclusionPhrases = rule.getExclusionPhrases();
                    if (exclusionPhrases != null) {
                        for (String exception : exclusionPhrases) {
                            if (exception != null) exceptionsArray.add(exception);
                        }
                    }
                    r.add("exceptions", exceptionsArray);

            // Conditions array for frontend (convert blacklisted phrases to conditions format)
            com.google.gson.JsonArray conditionsArray = new com.google.gson.JsonArray();
            List<String> phrases = rule.getBlacklistedPhrases();
            if (phrases != null && !phrases.isEmpty()) {
                // Check filter mode - if REGEX, each phrase is a separate regex condition
                if (rule.getFilterMode() == AutomodRule.FilterMode.REGEX) {
                    for (String phrase : phrases) {
                        JsonObject cond = new JsonObject();
                        cond.addProperty("kind", "regex");
                        cond.addProperty("value", phrase);
                        conditionsArray.add(cond);
                    }
                } else {
                    // CONTAINS_PHRASE mode - combine all phrases into one condition
                    JsonObject cond = new JsonObject();
                    cond.addProperty("kind", "contains");
                    cond.addProperty("value", String.join(", ", phrases));
                    conditionsArray.add(cond);
                }
            }
            r.add("conditions", conditionsArray);

            // Auto punishment
            if (rule.getAutoPunishment() != null) {
                JsonObject ap = new JsonObject();
                ap.addProperty("enabled", rule.getAutoPunishment().isEnabled());
                String punishType = rule.getAutoPunishment().getType() != null ?
                        rule.getAutoPunishment().getType().name() : "WARN";
                ap.addProperty("type", punishType);
                ap.addProperty("duration", rule.getAutoPunishment().getDuration());
                ap.addProperty("triggerCount", rule.getAutoPunishment().getTriggerCount());
                ap.addProperty("timeWindow", rule.getAutoPunishment().getTimeWindow());
                ap.addProperty("reason", rule.getAutoPunishment().getReason() != null ? rule.getAutoPunishment().getReason() : "");
                r.add("autoPunishment", ap);

                // Frontend action format for compatibility
                JsonObject action = new JsonObject();
                action.addProperty("kind", rule.getAutoPunishment().isEnabled() && rule.getAutoPunishment().getType() != null ?
                        rule.getAutoPunishment().getType().name().toLowerCase() : "none");
                action.addProperty("extra", rule.getAutoPunishment().getReason() != null ?
                        rule.getAutoPunishment().getReason() : "");
                // Format duration: -1 = permanent, 0 = instant, else format as string
                long durationMs = rule.getAutoPunishment().getDuration();
                String durationStr = durationMs == -1 ? "perm" : durationMs == 0 ? "" : DurationParser.format(durationMs);
                action.addProperty("duration", durationStr);
                r.add("action", action);
            } else {
                // Default action object for rules without auto punishment
                JsonObject action = new JsonObject();
                action.addProperty("kind", "none");
                action.addProperty("extra", "");
                action.addProperty("duration", "");
                r.add("action", action);
            }

            // Threshold for frontend compatibility
            JsonObject threshold = new JsonObject();
            threshold.addProperty("hits", rule.getAutoPunishment() != null ?
                    rule.getAutoPunishment().getTriggerCount() : 3);
            // Convert milliseconds to minutes for frontend (timeWindow is stored in ms)
            threshold.addProperty("windowMins", rule.getAutoPunishment() != null ?
                    (rule.getAutoPunishment().getTimeWindow() / 60000) : 5);
            r.add("threshold", threshold);

            rules.add(r);
                } catch (Exception e) {
                    // Log but continue with other rules - don't let one bad rule crash the broadcast
                    plugin.logError("Failed to serialize automod rule " + rule.getId() + ": " + e.getMessage(), e);
                }
            }

            data.add("rules", rules);
            broadcast.add("data", data);
            String message = GSON.toJson(broadcast);

            plugin.logDebug("[WebPanel] Broadcasting automod rules: " + rules.size() + " rules, " +
                    (message.length() / 1024) + "KB payload");

            // Broadcast to regular WebSocket connections
            for (WebSocketConnection conn : sessions.keySet()) {
                try {
                    conn.send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast automod rules to connection: " + e.getMessage());
                }
            }

            // Also broadcast to same-port (Netty) connections
            for (var entry : samePortConnections.entrySet()) {
                try {
                    entry.getValue().send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast automod rules to same-port connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            plugin.logError("Failed to broadcast automod rules: " + e.getMessage(), e);
        }
    }

    /**
     * Broadcast a single rule update to all connected clients.
     * This is more efficient than broadcasting all rules when only one changed.
     */
    public void broadcastSingleRuleUpdate(AutomodRule rule) {
        try {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", "AUTOMOD_RULE_UPDATED");
            JsonObject data = serializeRule(rule);
            broadcast.add("data", data);
            String message = GSON.toJson(broadcast);

            plugin.logDebug("[WebPanel] Broadcasting single rule update: " + rule.getId() + " (" + (message.length() / 1024) + "KB)");

            // Broadcast to regular WebSocket connections
            for (WebSocketConnection conn : sessions.keySet()) {
                try {
                    conn.send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast rule update to connection: " + e.getMessage());
                }
            }

            // Also broadcast to same-port (Netty) connections
            for (var entry : samePortConnections.entrySet()) {
                try {
                    entry.getValue().send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast rule update to same-port connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            plugin.logError("Failed to broadcast single rule update: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize a single automod rule to JSON for broadcasting.
     */
    private JsonObject serializeRule(AutomodRule rule) {
        JsonObject r = new JsonObject();
        r.addProperty("id", rule.getId());
        r.addProperty("name", rule.getName() != null ? rule.getName() : "Unknown");
        r.addProperty("type", rule.getType() != null ? rule.getType().name() : "WORD_FILTER");
        r.addProperty("enabled", rule.isEnabled());
        r.addProperty("builtIn", rule.isBuiltIn());
        r.addProperty("priority", rule.getPriority());
        r.addProperty("description", rule.getDescription() != null ? rule.getDescription() : "");

        // For ANTICHEAT rules, send minimal data
        if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
            r.addProperty("anticheatName", rule.getAnticheatName() != null ? rule.getAnticheatName() : "");
            r.addProperty("checkName", rule.getCheckName() != null ? rule.getCheckName() : "");
            r.addProperty("anticheatAlertThreshold", rule.getAnticheatAlertThreshold());
            r.addProperty("anticheatTimeWindowSeconds", rule.getAnticheatTimeWindowSeconds());
            addAutoPunishmentToRule(r, rule);
            r.add("blacklistedWords", new JsonArray());
            r.add("blacklistedPhrases", new JsonArray());
            r.add("exclusionWords", new JsonArray());
            r.add("conditions", new JsonArray());
            r.add("exceptions", new JsonArray());
            return r;
        }

        // Full data for non-anticheat rules
        r.addProperty("exactMatch", rule.isExactMatch());
        r.addProperty("spamMessageCount", rule.getSpamMessageCount());
        r.addProperty("spamTimeWindowSeconds", rule.getSpamTimeWindowSeconds());
        r.addProperty("spamDetectSimilar", rule.isSpamDetectSimilar());
        r.addProperty("spamSimilarityThreshold", rule.getSpamSimilarityThreshold());
        r.addProperty("capsMaxPercentage", rule.getCapsMaxPercentage());
        r.addProperty("capsMinLength", rule.getCapsMinLength());
        r.addProperty("afkTimeoutMinutes", rule.getAfkTimeoutMinutes());
        r.addProperty("afkKickEnabled", rule.isAfkKickEnabled());
        r.addProperty("anticheatName", rule.getAnticheatName() != null ? rule.getAnticheatName() : "");
        r.addProperty("checkName", rule.getCheckName() != null ? rule.getCheckName() : "");
        r.addProperty("anticheatAlertThreshold", rule.getAnticheatAlertThreshold());
        r.addProperty("anticheatTimeWindowSeconds", rule.getAnticheatTimeWindowSeconds());
        r.addProperty("flagAction", rule.getFlagAction() != null ? rule.getFlagAction().name() : "BLOCK");
        r.addProperty("filterMode", rule.getFilterMode() != null ? rule.getFilterMode().name() : "CONTAINS_PHRASE");
        r.addProperty("block", rule.getFlagAction() == AutomodRule.FlagAction.BLOCK);
        r.addProperty("applyToNicknames", rule.isApplyToNicknames());
        r.addProperty("nicknameOnly", rule.isNicknameOnly());

        // Blacklisted words/phrases
        JsonArray blacklist = new JsonArray();
        if (rule.getBlacklistedWords() != null) {
            for (String word : rule.getBlacklistedWords()) {
                if (word != null) blacklist.add(word);
            }
        }
        r.add("blacklistedWords", blacklist);
        r.add("blacklistedPhrases", blacklist);

        // Exclusion words
        JsonArray whitelist = new JsonArray();
        if (rule.getExclusionWords() != null) {
            for (String word : rule.getExclusionWords()) {
                if (word != null) whitelist.add(word);
            }
        }
        r.add("exclusionWords", whitelist);
        r.add("exclusionPhrases", whitelist);
        r.add("whitelist", whitelist);

        // Exceptions
        JsonArray exceptionsArray = new JsonArray();
        if (rule.getExclusionPhrases() != null) {
            for (String exception : rule.getExclusionPhrases()) {
                if (exception != null) exceptionsArray.add(exception);
            }
        }
        r.add("exceptions", exceptionsArray);

        // Conditions
        JsonArray conditionsArray = new JsonArray();
        List<String> phrases = rule.getBlacklistedPhrases();
        if (phrases != null && !phrases.isEmpty()) {
            if (rule.getFilterMode() == AutomodRule.FilterMode.REGEX) {
                for (String phrase : phrases) {
                    JsonObject cond = new JsonObject();
                    cond.addProperty("kind", "regex");
                    cond.addProperty("value", phrase);
                    conditionsArray.add(cond);
                }
            } else {
                JsonObject cond = new JsonObject();
                cond.addProperty("kind", "contains");
                cond.addProperty("value", String.join(", ", phrases));
                conditionsArray.add(cond);
            }
        }
        r.add("conditions", conditionsArray);

        addAutoPunishmentToRule(r, rule);
        return r;
    }

    /**
     * Helper to add auto punishment fields to a rule JSON object.
     */
    private void addAutoPunishmentToRule(JsonObject r, AutomodRule rule) {
        if (rule.getAutoPunishment() != null) {
            JsonObject ap = new JsonObject();
            ap.addProperty("enabled", rule.getAutoPunishment().isEnabled());
            String punishType = rule.getAutoPunishment().getType() != null ?
                    rule.getAutoPunishment().getType().name() : "WARN";
            ap.addProperty("type", punishType);
            ap.addProperty("duration", rule.getAutoPunishment().getDuration());
            ap.addProperty("triggerCount", rule.getAutoPunishment().getTriggerCount());
            ap.addProperty("timeWindow", rule.getAutoPunishment().getTimeWindow());
            ap.addProperty("reason", rule.getAutoPunishment().getReason() != null ? rule.getAutoPunishment().getReason() : "");
            r.add("autoPunishment", ap);

            JsonObject action = new JsonObject();
            action.addProperty("kind", rule.getAutoPunishment().isEnabled() && rule.getAutoPunishment().getType() != null ?
                    rule.getAutoPunishment().getType().name().toLowerCase() : "none");
            action.addProperty("extra", rule.getAutoPunishment().getReason() != null ? rule.getAutoPunishment().getReason() : "");
            long durationMs = rule.getAutoPunishment().getDuration();
            String durationStr = durationMs == -1 ? "perm" : durationMs == 0 ? "" : DurationParser.format(durationMs);
            action.addProperty("duration", durationStr);
            r.add("action", action);
        } else {
            JsonObject action = new JsonObject();
            action.addProperty("kind", "none");
            action.addProperty("extra", "");
            action.addProperty("duration", "");
            r.add("action", action);
        }

        JsonObject threshold = new JsonObject();
        threshold.addProperty("hits", rule.getAutoPunishment() != null ?
                rule.getAutoPunishment().getTriggerCount() : 3);
        threshold.addProperty("windowMins", rule.getAutoPunishment() != null ?
                (rule.getAutoPunishment().getTimeWindow() / 60000) : 5);
        r.add("threshold", threshold);
    }

    private void sendAnticheatInfo(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ANTICHEAT_INFO");
        JsonObject data = new JsonObject();

        boolean alertsEnabled = plugin.getConfigManager().getSettings().isAnticheatAlertsEnabled();

        // Get list of enabled anticheats as plugin objects for frontend
        JsonArray plugins = new JsonArray();
        for (String anticheat : plugin.getAnticheatManager().getEnabledAnticheats()) {
            JsonObject pluginObj = new JsonObject();
            pluginObj.addProperty("name", anticheat);
            pluginObj.addProperty("alertsEnabled", alertsEnabled);
            plugins.add(pluginObj);
        }
        data.add("plugins", plugins);

        // Also include legacy fields for backward compatibility
        JsonArray anticheats = new JsonArray();
        for (String anticheat : plugin.getAnticheatManager().getEnabledAnticheats()) {
            anticheats.add(anticheat);
        }
        data.add("enabledAnticheats", anticheats);
        data.addProperty("hasAnyHook", plugin.getAnticheatManager().hasAnyHook());
        data.addProperty("alertsEnabled", alertsEnabled);

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Send anticheat alerts data with all checks grouped by category.
     * This is what the Anticheat Alerts page expects.
     */
    private void sendAnticheatAlerts(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ANTICHEAT_ALERTS");

        JsonObject data = new JsonObject();

        // Get all enabled anticheats and their checks
        JsonArray anticheats = new JsonArray();
        for (String acName : plugin.getAnticheatManager().getEnabledAnticheats()) {
            JsonObject ac = new JsonObject();
            ac.addProperty("name", acName);

            // Get all checks for this anticheat from the registry
            JsonArray checks = new JsonArray();
            for (AnticheatChecks.CheckInfo check : AnticheatChecks.getChecks(acName)) {
                JsonObject checkObj = new JsonObject();
                checkObj.addProperty("name", check.getName());
                checkObj.addProperty("displayName", check.getDisplayName());
                checkObj.addProperty("category", check.getCategory().name());
                checkObj.addProperty("categoryDisplay", check.getCategory().getDisplayName());
                checkObj.addProperty("description", check.getDescription());
                checks.add(checkObj);
            }

            // Group checks by category
            JsonObject categories = new JsonObject();
            for (AnticheatChecks.Category cat : AnticheatChecks.Category.values()) {
                JsonArray catChecks = new JsonArray();
                for (AnticheatChecks.CheckInfo check : AnticheatChecks.getChecksByCategory(acName, cat)) {
                    JsonObject checkObj = new JsonObject();
                    checkObj.addProperty("name", check.getName());
                    checkObj.addProperty("displayName", check.getDisplayName());
                    checkObj.addProperty("description", check.getDescription());
                    catChecks.add(checkObj);
                }
                if (catChecks.size() > 0) {
                    categories.add(cat.name().toLowerCase(), catChecks);
                }
            }

            ac.add("checks", checks);
            ac.add("categories", categories);
            anticheats.add(ac);
        }

        data.add("anticheats", anticheats);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Send all detected anticheat checks to the web panel.
     * Returns all checks from all connected anticheats.
     */
    private void sendAnticheatChecks(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ANTICHEAT_CHECKS");
        JsonObject data = new JsonObject();

        var alertManager = plugin.getAnticheatManager().getAlertManager();
        JsonArray checksArray = new JsonArray();

        // For each enabled anticheat, get all detected checks
        for (String anticheat : plugin.getAnticheatManager().getEnabledAnticheats()) {
            for (var rule : alertManager.getRulesForAnticheat(anticheat)) {
                JsonObject checkObj = new JsonObject();
                checkObj.addProperty("anticheat", rule.getAnticheat());
                checkObj.addProperty("checkName", rule.getCheckName());
                checkObj.addProperty("key", rule.getKey());
                checkObj.addProperty("enabled", rule.isEnabled());
                checkObj.addProperty("minVL", rule.getMinVL());
                checkObj.addProperty("thresholdCount", rule.getThresholdCount());
                checkObj.addProperty("thresholdDuration", rule.getThresholdDuration());
                checksArray.add(checkObj);
            }
        }

        data.add("checks", checksArray);
        data.add("enabledAnticheats", GSON.toJsonTree(plugin.getAnticheatManager().getEnabledAnticheats()));
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Send the staff member's anticheat alert preferences.
     */
    private void sendStaffAnticheatSettings(WebSocketConnection conn, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "STAFF_ANTICHEAT_SETTINGS");
        JsonObject data = new JsonObject();

        // Get the staff member's settings
        var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
        if (staffSettings == null) {
            staffSettings = new com.blockforge.moderex.staff.StaffSettings(session.playerUuid);
        }

        // Get all check alert preferences
        JsonArray preferencesArray = new JsonArray();
        for (var entry : staffSettings.getCheckAlertPreferences().entrySet()) {
            var pref = entry.getValue();
            JsonObject prefObj = new JsonObject();
            prefObj.addProperty("checkKey", pref.getCheckKey());
            prefObj.addProperty("alertLevel", pref.getAlertLevel().name());
            prefObj.addProperty("thresholdCount", pref.getThresholdCount());
            prefObj.addProperty("timeWindowSeconds", pref.getTimeWindowSeconds());
            prefObj.addProperty("configured", pref.isConfigured());
            preferencesArray.add(prefObj);
        }

        data.add("preferences", preferencesArray);
        data.addProperty("globalAnticheatAlerts", staffSettings.getAnticheatAlerts().name());
        data.addProperty("anticheatMinVL", staffSettings.getAnticheatMinVL());
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Update a single anticheat check preference for the staff member.
     */
    private void updateStaffAnticheatSetting(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String checkKey = data.get("checkKey").getAsString();
            String[] parts = checkKey.split(":", 2);
            if (parts.length != 2) {
                sendError(conn, "INVALID_CHECK_KEY", "Invalid check key format");
                return;
            }
            String anticheat = parts[0];
            String checkName = parts[1];

            // Get or create staff settings
            var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
            if (staffSettings == null) {
                staffSettings = new com.blockforge.moderex.staff.StaffSettings(session.playerUuid);
            }

            var pref = staffSettings.getCheckAlertPreference(anticheat, checkName);

            // Update preference fields
            if (data.has("alertLevel")) {
                String levelStr = data.get("alertLevel").getAsString();
                pref.setAlertLevel(com.blockforge.moderex.staff.StaffSettings.AlertLevel.valueOf(levelStr));
            }
            if (data.has("thresholdCount")) {
                pref.setThresholdCount(data.get("thresholdCount").getAsInt());
            }
            if (data.has("timeWindowSeconds")) {
                pref.setTimeWindowSeconds(data.get("timeWindowSeconds").getAsInt());
            }

            // Save the settings
            staffSettings.setCheckAlertPreference(anticheat, checkName, pref);
            plugin.getStaffSettingsManager().saveSettings(staffSettings);

            // Send success response with updated preference
            JsonObject response = new JsonObject();
            response.addProperty("type", "STAFF_ANTICHEAT_SETTING_UPDATED");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("checkKey", checkKey);
            responseData.addProperty("alertLevel", pref.getAlertLevel().name());
            responseData.addProperty("thresholdCount", pref.getThresholdCount());
            responseData.addProperty("timeWindowSeconds", pref.getTimeWindowSeconds());
            response.add("data", responseData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] " + session.playerName + " updated anticheat setting for " + checkKey);

        } catch (Exception e) {
            plugin.logError("Failed to update staff anticheat setting", e);
            sendError(conn, "UPDATE_FAILED", "Failed to update anticheat setting: " + e.getMessage());
        }
    }

    /**
     * Send staff alert preferences (frontend format: anticheat.checkName keys).
     */
    private void sendStaffAlertPrefs(WebSocketConnection conn, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "STAFF_ALERT_PREFS");
        JsonObject data = new JsonObject();

        var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
        if (staffSettings == null) {
            staffSettings = new com.blockforge.moderex.staff.StaffSettings(session.playerUuid);
        }

        // Build preferences organized by anticheat
        // Format: { "grim": { "killaura": { alertLevel, thresholdCount, timeWindowSeconds }, ... }, ... }
        JsonObject preferences = new JsonObject();
        for (String anticheatName : plugin.getAnticheatManager().getEnabledAnticheats()) {
            JsonObject anticheatPrefs = new JsonObject();
            String acLower = anticheatName.toLowerCase();

            for (var entry : staffSettings.getCheckAlertPreferences().entrySet()) {
                String checkKey = entry.getKey();
                if (checkKey.toLowerCase().startsWith(acLower + ":")) {
                    var pref = entry.getValue();
                    String checkName = checkKey.substring(checkKey.indexOf(':') + 1);
                    JsonObject prefObj = new JsonObject();
                    prefObj.addProperty("alertLevel", pref.getAlertLevel().name());
                    prefObj.addProperty("thresholdCount", pref.getThresholdCount());
                    prefObj.addProperty("timeWindowSeconds", pref.getTimeWindowSeconds());
                    anticheatPrefs.add(checkName, prefObj);
                }
            }
            preferences.add(acLower, anticheatPrefs);
        }

        data.add("preferences", preferences);
        response.add("data", data);
        conn.send(GSON.toJson(response));

        debugSuccess(DebugCategory.ANTICHEAT, "Alert prefs sent",
                "Sent " + staffSettings.getCheckAlertPreferences().size() + " prefs to " + session.playerName);
    }

    /**
     * Update staff alert preference (frontend format with anticheat and checkName separate).
     */
    private void updateStaffAlertPref(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String anticheat = data.has("anticheat") ? data.get("anticheat").getAsString() : "";
            String checkName = data.has("checkName") ? data.get("checkName").getAsString() : "";
            String alertLevel = data.has("alertLevel") ? data.get("alertLevel").getAsString() : "EVERYONE";
            int thresholdCount = data.has("thresholdCount") ? data.get("thresholdCount").getAsInt() : 1;
            int timeWindowSeconds = data.has("timeWindowSeconds") ? data.get("timeWindowSeconds").getAsInt() : 60;

            if (anticheat.isEmpty() || checkName.isEmpty()) {
                sendError(conn, "MISSING_PARAMETER", "Anticheat and check name are required");
                debugError(ErrorCode.REQUEST_MISSING_PARAMETER, "Missing anticheat or checkName in UPDATE_STAFF_ALERT_PREF");
                return;
            }

            // Get or create staff settings
            var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
            if (staffSettings == null) {
                staffSettings = new com.blockforge.moderex.staff.StaffSettings(session.playerUuid);
            }

            var pref = staffSettings.getCheckAlertPreference(anticheat, checkName);

            // Update preference
            try {
                pref.setAlertLevel(com.blockforge.moderex.staff.StaffSettings.AlertLevel.valueOf(alertLevel.toUpperCase()));
            } catch (IllegalArgumentException e) {
                pref.setAlertLevel(com.blockforge.moderex.staff.StaffSettings.AlertLevel.EVERYONE);
            }
            pref.setThresholdCount(thresholdCount);
            pref.setTimeWindowSeconds(timeWindowSeconds);

            // Save to database
            staffSettings.setCheckAlertPreference(anticheat, checkName, pref);
            plugin.getStaffSettingsManager().saveSettings(staffSettings);

            // Debug log
            debugSuccess(DebugCategory.ANTICHEAT, "Alert pref updated",
                    session.playerName + " set " + anticheat + ":" + checkName +
                    " -> " + alertLevel + " (thr:" + thresholdCount + ", win:" + timeWindowSeconds + "s)");

            // Send success response
            JsonObject response = new JsonObject();
            response.addProperty("type", "STAFF_ALERT_PREF_UPDATED");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("anticheat", anticheat);
            responseData.addProperty("checkName", checkName);
            responseData.addProperty("alertLevel", pref.getAlertLevel().name());
            responseData.addProperty("thresholdCount", pref.getThresholdCount());
            responseData.addProperty("timeWindowSeconds", pref.getTimeWindowSeconds());
            response.add("data", responseData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] " + session.playerName + " updated alert pref: " + anticheat + ":" + checkName);

        } catch (Exception e) {
            plugin.logError("Failed to update staff alert pref", e);
            sendError(conn, "UPDATE_FAILED", "Failed to update alert preference: " + e.getMessage());
            debugError(ErrorCode.OPERATION_UPDATE_FAILED, "Error updating alert pref: " + e.getMessage());
        }
    }

    /**
     * Send available alert presets.
     */
    private void sendAlertPresets(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ALERT_PRESETS");
        JsonObject data = new JsonObject();
        JsonArray presets = new JsonArray();

        // All On preset
        JsonObject allOn = new JsonObject();
        allOn.addProperty("id", "all_on");
        allOn.addProperty("name", "All On");
        allOn.addProperty("description", "Receive all anticheat alerts");
        allOn.addProperty("alertLevel", "EVERYONE");
        allOn.addProperty("thresholdCount", 1);
        allOn.addProperty("timeWindowSeconds", 60);
        presets.add(allOn);

        // Watchlist Only preset
        JsonObject watchlist = new JsonObject();
        watchlist.addProperty("id", "watchlist_only");
        watchlist.addProperty("name", "Watchlist Only");
        watchlist.addProperty("description", "Only alerts for watched players");
        watchlist.addProperty("alertLevel", "WATCHLIST_ONLY");
        watchlist.addProperty("thresholdCount", 1);
        watchlist.addProperty("timeWindowSeconds", 60);
        presets.add(watchlist);

        // Reduced Spam preset
        JsonObject reduced = new JsonObject();
        reduced.addProperty("id", "reduced_spam");
        reduced.addProperty("name", "Reduced Spam");
        reduced.addProperty("description", "Higher thresholds to reduce alert spam");
        reduced.addProperty("alertLevel", "EVERYONE");
        reduced.addProperty("thresholdCount", 5);
        reduced.addProperty("timeWindowSeconds", 30);
        presets.add(reduced);

        // Combat Focus preset
        JsonObject combat = new JsonObject();
        combat.addProperty("id", "combat_focus");
        combat.addProperty("name", "Combat Focus");
        combat.addProperty("description", "Focus on combat-related checks");
        combat.addProperty("alertLevel", "EVERYONE");
        combat.addProperty("thresholdCount", 3);
        combat.addProperty("timeWindowSeconds", 60);
        presets.add(combat);

        // All Off preset
        JsonObject allOff = new JsonObject();
        allOff.addProperty("id", "all_off");
        allOff.addProperty("name", "All Off");
        allOff.addProperty("description", "Disable all anticheat alerts");
        allOff.addProperty("alertLevel", "OFF");
        allOff.addProperty("thresholdCount", 1);
        allOff.addProperty("timeWindowSeconds", 60);
        presets.add(allOff);

        data.add("presets", presets);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendWatchlist(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "WATCHLIST_DATA");
        JsonObject data = new JsonObject();
        JsonArray watchlist = new JsonArray();
        for (UUID uuid : plugin.getWatchlistManager().getWatchedPlayers()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            JsonObject w = new JsonObject();
            w.addProperty("playerUuid", uuid.toString());
            w.addProperty("name", player.getName() != null ? player.getName() : uuid.toString());
            w.addProperty("online", player.isOnline());
            watchlist.add(w);
        }
        data.add("watchlist", watchlist);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendSettings(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SETTINGS");
        JsonObject settings = new JsonObject();
        var config = plugin.getConfigManager().getSettings();
        settings.addProperty("language", config.getLanguage());
        settings.addProperty("chatEnabled", config.isChatEnabled());
        settings.addProperty("defaultSlowmode", config.getDefaultSlowmodeSeconds());
        response.add("data", settings);
        conn.send(GSON.toJson(response));
    }

    private void sendUserSettings(WebSocketConnection conn, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "USER_SETTINGS_DATA");
        JsonObject data = getUserSettings(session.playerUuid).toJson();

        plugin.logDebug("[WebPanel] Sending user settings to " + session.playerName);

        // Include in-game staff settings for synchronization
        var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
        if (staffSettings != null) {
            // Add staff settings that should be synced
            data.addProperty("staffChatEnabled", staffSettings.isStaffChatEnabled());
            data.addProperty("staffChatSound", staffSettings.isStaffChatSound());
            data.addProperty("watchlistJoinAlerts", staffSettings.isWatchlistJoinAlerts());
            data.addProperty("watchlistQuitAlerts", staffSettings.isWatchlistQuitAlerts());
            data.addProperty("watchlistActivityAlerts", staffSettings.isWatchlistActivityAlerts());
            data.addProperty("autoVanishOnJoin", staffSettings.isAutoVanishOnJoin());
            data.addProperty("vanishNightVision", staffSettings.isVanishNightVision());
            data.addProperty("compactMode", staffSettings.isCompactMode());
            data.addProperty("inGameSoundEnabled", staffSettings.isSoundEnabled());
            data.addProperty("actionBarAlerts", staffSettings.isActionBarAlerts());
            data.addProperty("inGameChatAlerts", staffSettings.isChatAlerts());
            data.addProperty("bossBarAlerts", staffSettings.isBossBarAlerts());

            // Punishment alert levels (Everyone, Watchlist Only, Off)
            data.addProperty("banAlerts", staffSettings.getBanAlerts().name().toLowerCase());
            data.addProperty("kickAlerts", staffSettings.getKickAlerts().name().toLowerCase());
            data.addProperty("muteAlerts", staffSettings.getMuteAlerts().name().toLowerCase());
            data.addProperty("warnAlerts", staffSettings.getWarnAlerts().name().toLowerCase());
            data.addProperty("pardonAlerts", staffSettings.getPardonAlerts().name().toLowerCase());

            // Other alert types
            data.addProperty("automodAlerts", staffSettings.getAutomodAlerts().name().toLowerCase());
            data.addProperty("anticheatAlerts", staffSettings.getAnticheatAlerts().name().toLowerCase());
            data.addProperty("anticheatMinVL", staffSettings.getAnticheatMinVL());
            data.addProperty("nicknameAlerts", staffSettings.getNicknameAlerts().name().toLowerCase());
            data.addProperty("commandAlerts", staffSettings.getCommandAlerts().name().toLowerCase());
            data.addProperty("joinLeaveAlerts", staffSettings.getJoinLeaveAlerts().name().toLowerCase());
            data.addProperty("lagAlerts", staffSettings.isLagAlerts());

            // Web panel notification modes
            data.addProperty("webNotifyPunishments", staffSettings.getWebNotifyPunishments().name().toLowerCase());
            data.addProperty("webNotifyAutomod", staffSettings.getWebNotifyAutomod().name().toLowerCase());
            data.addProperty("webNotifyAnticheat", staffSettings.getWebNotifyAnticheat().name().toLowerCase());
            data.addProperty("webNotifyWatchlist", staffSettings.getWebNotifyWatchlist().name().toLowerCase());
            data.addProperty("webNotifyStaffChat", staffSettings.getWebNotifyStaffChat().name().toLowerCase());
            data.addProperty("webNotifyCommands", staffSettings.getWebNotifyCommands().name().toLowerCase());
            data.addProperty("webNotifyNickname", staffSettings.getWebNotifyNickname().name().toLowerCase());
            data.addProperty("webNotifyLag", staffSettings.getWebNotifyLag().name().toLowerCase());

            // Web panel display settings
            data.addProperty("webToastPosition", staffSettings.getWebToastPosition().getCssClass());
            data.addProperty("webAlertDurationSeconds", staffSettings.getWebAlertDurationSeconds());

            // Web panel sound settings per alert type
            data.addProperty("webSoundPunishments", staffSettings.isWebSoundPunishments());
            data.addProperty("webSoundAutomod", staffSettings.isWebSoundAutomod());
            data.addProperty("webSoundAnticheat", staffSettings.isWebSoundAnticheat());
            data.addProperty("webSoundWatchlist", staffSettings.isWebSoundWatchlist());
            data.addProperty("webSoundStaffChat", staffSettings.isWebSoundStaffChat());
            data.addProperty("webSoundCommands", staffSettings.isWebSoundCommands());
            data.addProperty("webSoundNickname", staffSettings.isWebSoundNickname());
            data.addProperty("webSoundLag", staffSettings.isWebSoundLag());

            plugin.logDebug("[WebPanel] Alert settings for " + session.playerName + ": " +
                "ban=" + staffSettings.getBanAlerts() + ", " +
                "kick=" + staffSettings.getKickAlerts() + ", " +
                "mute=" + staffSettings.getMuteAlerts() + ", " +
                "warn=" + staffSettings.getWarnAlerts() + ", " +
                "command=" + staffSettings.getCommandAlerts() + ", " +
                "toastPos=" + staffSettings.getWebToastPosition());
        }

        // Include read changelog builds
        JsonArray readChangelogs = getReadChangelogBuilds(session.playerUuid);
        data.add("readChangelogs", readChangelogs);

        // Include user permissions for alert type checks
        JsonArray permissions = getUserPermissions(session.playerUuid);
        data.add("permissions", permissions);
        plugin.logDebug("[WebPanel] User " + session.playerName + " permissions: " + permissions);

        response.add("data", data);
        conn.send(GSON.toJson(response));
        plugin.logDebug("[WebPanel] Sent user settings with " + readChangelogs.size() + " read changelogs to " + session.playerName);
    }

    /**
     * Get list of changelog builds the user has read
     */
    private JsonArray getReadChangelogBuilds(java.util.UUID uuid) {
        JsonArray builds = new JsonArray();
        try {
            plugin.getDatabaseManager().query(
                "SELECT build_number FROM moderex_changelog_reads WHERE uuid = ?",
                rs -> {
                    while (rs.next()) {
                        builds.add(rs.getInt("build_number"));
                    }
                    return null;
                },
                uuid.toString()
            );
        } catch (Exception e) {
            plugin.logError("Failed to load changelog reads for " + uuid, e);
        }
        return builds;
    }

    /**
     * Get the user's ModereX permissions for the frontend.
     * Checks all alert-related permissions for the user.
     * Works for both online players (Bukkit) and offline players (LuckPerms).
     */
    private JsonArray getUserPermissions(UUID uuid) {
        JsonArray permissions = new JsonArray();

        // List of permissions to check
        String[] alertPermissions = {
            "moderex.staff",
            "moderex.admin",
            "moderex.admin.automod",  // Permission for automod configuration
            "moderex.alerts.*",
            "moderex.alerts.ban",
            "moderex.alerts.kick",
            "moderex.alerts.mute",
            "moderex.alerts.warn",
            "moderex.alerts.pardon",
            "moderex.alerts.anticheat",
            "moderex.alerts.automod",
            "moderex.alerts.commands",
            "moderex.alerts.nickname",
            "moderex.alerts.joinleave",
            "moderex.alerts.lag",
            "moderex.alerts.watchlist",
            "moderex.alerts.staffchat",
            "moderex.alerts.punishments"
        };

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // Player is online - use PermissionUtil (which handles OP bypass)
            plugin.logDebug("[WebPanel] Checking permissions for online player " + player.getName());
            for (String perm : alertPermissions) {
                if (PermissionUtil.hasPermission(player, perm)) {
                    permissions.add(perm);
                }
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            // Player is offline - use LuckPerms for permission check
            plugin.logDebug("[WebPanel] Checking permissions via LuckPerms for offline player " + uuid);
            var lpHook = plugin.getHookManager().getLuckPermsHook();
            for (String perm : alertPermissions) {
                if (lpHook.hasPermission(uuid, perm)) {
                    permissions.add(perm);
                }
            }
        } else {
            // No way to check permissions for offline player without LuckPerms
            plugin.logDebug("[WebPanel] Cannot check permissions for offline player " + uuid + " - LuckPerms not available");
            // Grant all permissions as fallback for web panel users (they already have webpanel permission)
            plugin.logDebug("[WebPanel] Granting all alert permissions as fallback");
            for (String perm : alertPermissions) {
                permissions.add(perm);
            }
        }

        plugin.logDebug("[WebPanel] Found " + permissions.size() + " permissions for " + uuid);
        return permissions;
    }

    /**
     * Check if user has permission to manage automod rules.
     * Requires moderex.admin.automod or moderex.admin.* or OP status.
     */
    private boolean hasAutomodPermission(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // Player is online - check permissions directly
            return PermissionUtil.hasPermission(player, "moderex.admin.automod") ||
                   PermissionUtil.hasPermission(player, "moderex.admin.*") ||
                   PermissionUtil.hasPermission(player, "moderex.admin");
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            // Player is offline - use LuckPerms
            var lpHook = plugin.getHookManager().getLuckPermsHook();
            return lpHook.hasPermission(uuid, "moderex.admin.automod") ||
                   lpHook.hasPermission(uuid, "moderex.admin.*") ||
                   lpHook.hasPermission(uuid, "moderex.admin");
        } else {
            // No way to check - allow by default for web panel users (they already passed auth)
            plugin.logDebug("[WebPanel] Cannot check automod permission for offline user - allowing by default");
            return true;
        }
    }

    /**
     * Mark a changelog as read for a user
     */
    private void markChangelogRead(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        int buildNumber = data.has("build") ? data.get("build").getAsInt() : 0;
        if (buildNumber <= 0) {
            sendError(conn, "INVALID_BUILD", "Invalid build number");
            return;
        }

        try {
            plugin.getDatabaseManager().update(
                """
                INSERT INTO moderex_changelog_reads (uuid, build_number, read_at)
                VALUES (?, ?, ?)
                ON CONFLICT(uuid, build_number) DO UPDATE SET read_at = excluded.read_at
                """,
                session.playerUuid.toString(),
                buildNumber,
                System.currentTimeMillis()
            );

            plugin.logDebug("[WebPanel] " + session.playerName + " marked changelog build " + buildNumber + " as read");

            JsonObject response = new JsonObject();
            response.addProperty("type", "CHANGELOG_MARKED_READ");
            response.addProperty("build", buildNumber);
            conn.send(GSON.toJson(response));
        } catch (Exception e) {
            plugin.logError("Failed to mark changelog read for " + session.playerName, e);
            sendError(conn, "DATABASE_ERROR", "Failed to save changelog read status");
        }
    }

    // Same-port wrapper overload
    private void markChangelogRead(SamePortConnectionWrapper wrapper, JsonObject data, WebPanelSession session) {
        int buildNumber = data.has("build") ? data.get("build").getAsInt() : 0;
        if (buildNumber <= 0) {
            sendError(wrapper, "INVALID_BUILD", "Invalid build number");
            return;
        }

        try {
            plugin.getDatabaseManager().update(
                """
                INSERT INTO moderex_changelog_reads (uuid, build_number, read_at)
                VALUES (?, ?, ?)
                ON CONFLICT(uuid, build_number) DO UPDATE SET read_at = excluded.read_at
                """,
                session.playerUuid.toString(),
                buildNumber,
                System.currentTimeMillis()
            );

            plugin.logDebug("[WebPanel] " + session.playerName + " marked changelog build " + buildNumber + " as read");

            JsonObject response = new JsonObject();
            response.addProperty("type", "CHANGELOG_MARKED_READ");
            response.addProperty("build", buildNumber);
            wrapper.send(GSON.toJson(response));
        } catch (Exception e) {
            plugin.logError("Failed to mark changelog read for " + session.playerName, e);
            sendError(wrapper, "DATABASE_ERROR", "Failed to save changelog read status");
        }
    }

    private void sendTemplates(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "TEMPLATES");
        JsonArray templates = new JsonArray();

        // Get templates from database
        for (com.blockforge.moderex.punishment.PunishmentTemplate template : plugin.getTemplateManager().getAllTemplates()) {
            templates.add(template.toJson());
        }

        response.add("data", templates);
        conn.send(GSON.toJson(response));
    }

    private void createTemplate(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String name = data.get("name").getAsString();
            String typeStr = data.get("type").getAsString();
            String duration = data.has("duration") ? data.get("duration").getAsString() : "";
            String reason = data.has("reason") ? data.get("reason").getAsString() : "";
            String category = data.has("category") ? data.get("category").getAsString() : "General";
            int priority = data.has("priority") ? data.get("priority").getAsInt() : 0;

            com.blockforge.moderex.punishment.PunishmentType type =
                com.blockforge.moderex.punishment.PunishmentType.valueOf(typeStr.toUpperCase());

            com.blockforge.moderex.punishment.PunishmentTemplate template =
                new com.blockforge.moderex.punishment.PunishmentTemplate(name, type, duration, reason);
            template.setCategory(category);
            template.setPriority(priority);
            template.setCreatedBy(session.playerUuid);
            template.setCreatedByName(session.playerName);

            plugin.getTemplateManager().saveTemplate(template);

            // Broadcast update to all connected clients
            broadcastTemplates();

            JsonObject response = new JsonObject();
            response.addProperty("type", "TEMPLATE_CREATED");
            response.add("data", template.toJson());
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Template created: " + name + " by " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "TEMPLATE_ERROR", "Failed to create template: " + e.getMessage());
            plugin.logError("Failed to create template from web panel", e);
        }
    }

    private void updateTemplate(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String id = data.get("id").getAsString();
            com.blockforge.moderex.punishment.PunishmentTemplate template = plugin.getTemplateManager().getTemplate(id);

            if (template == null) {
                sendError(conn, "NOT_FOUND", "Template not found");
                return;
            }

            // Update fields
            if (data.has("name")) template.setName(data.get("name").getAsString());
            if (data.has("type")) {
                template.setType(com.blockforge.moderex.punishment.PunishmentType.valueOf(
                    data.get("type").getAsString().toUpperCase()));
            }
            if (data.has("duration")) template.setDuration(data.get("duration").getAsString());
            if (data.has("reason")) template.setReason(data.get("reason").getAsString());
            if (data.has("category")) template.setCategory(data.get("category").getAsString());
            if (data.has("priority")) template.setPriority(data.get("priority").getAsInt());
            if (data.has("active")) template.setActive(data.get("active").getAsBoolean());

            plugin.getTemplateManager().saveTemplate(template);

            // Broadcast update to all connected clients
            broadcastTemplates();

            JsonObject response = new JsonObject();
            response.addProperty("type", "TEMPLATE_UPDATED");
            response.add("data", template.toJson());
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Template updated: " + template.getName() + " by " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "TEMPLATE_ERROR", "Failed to update template: " + e.getMessage());
            plugin.logError("Failed to update template from web panel", e);
        }
    }

    private void deleteTemplate(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String id = data.get("id").getAsString();
            boolean deleted = plugin.getTemplateManager().deleteTemplate(id);

            if (deleted) {
                // Broadcast update to all connected clients
                broadcastTemplates();

                JsonObject response = new JsonObject();
                response.addProperty("type", "TEMPLATE_DELETED");
                response.addProperty("id", id);
                conn.send(GSON.toJson(response));

                plugin.logDebug("[WebPanel] Template deleted: " + id + " by " + session.playerName);
            } else {
                sendError(conn, "TEMPLATE_ERROR", "Failed to delete template");
            }
        } catch (Exception e) {
            sendError(conn, "TEMPLATE_ERROR", "Failed to delete template: " + e.getMessage());
            plugin.logError("Failed to delete template from web panel", e);
        }
    }

    /**
     * Broadcast updated templates list to all connected web panel clients.
     * Call this after any template changes (create, update, delete) from in-game.
     */
    public void broadcastTemplates() {
        JsonObject broadcast = new JsonObject();
        broadcast.addProperty("type", "TEMPLATES");
        JsonArray templates = new JsonArray();

        for (com.blockforge.moderex.punishment.PunishmentTemplate template : plugin.getTemplateManager().getAllTemplates()) {
            templates.add(template.toJson());
        }

        broadcast.add("data", templates);
        String message = GSON.toJson(broadcast);

        for (WebSocketConnection conn : sessions.keySet()) {
            try {
                conn.send(message);
            } catch (Exception e) {
                plugin.logDebug("Failed to broadcast templates to connection: " + e.getMessage());
            }
        }
    }

    private void sendStats(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "STATS");
        JsonObject stats = new JsonObject();
        stats.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        stats.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());
        stats.addProperty("watchlistSize", plugin.getWatchlistManager().getWatchedPlayers().size());
        response.add("data", stats);
        conn.send(GSON.toJson(response));
    }

    private void createPunishment(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String targetName = data.has("playerName") ? data.get("playerName").getAsString() : "";
        String typeStr = data.has("type") ? data.get("type").getAsString() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "No reason specified";
        String durationStr = data.has("duration") ? data.get("duration").getAsString() : "";

        if (targetName.isEmpty() || typeStr.isEmpty()) {
            sendError(conn, "MISSING_DATA", "Player name and type required");
            return;
        }

        // Parse the duration
        long durationMs = DurationParser.parse(durationStr);

        // Get the target player's UUID
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String resolvedName = target.getName() != null ? target.getName() : targetName;

        // Use the session's staff info (actual connected player) instead of "Console"
        UUID staffUuid = session.playerUuid;
        String staffName = session.playerName;

        // Execute punishment via PunishmentManager directly
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PunishmentType type = PunishmentType.valueOf(typeStr.toUpperCase());

                switch (type) {
                    case BAN -> plugin.getPunishmentManager().ban(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case MUTE -> plugin.getPunishmentManager().mute(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case KICK -> plugin.getPunishmentManager().kick(targetUuid, resolvedName, staffUuid, staffName, reason);
                    case WARN -> plugin.getPunishmentManager().warn(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case IPBAN -> {
                        Player onlineTarget = Bukkit.getPlayer(targetUuid);
                        String ip = onlineTarget != null && onlineTarget.getAddress() != null
                                ? onlineTarget.getAddress().getAddress().getHostAddress()
                                : null;
                        if (ip != null) {
                            plugin.getPunishmentManager().ipBan(targetUuid, resolvedName, ip, staffUuid, staffName, durationMs, reason);
                        } else {
                            sendError(conn, "NO_IP", "Cannot IP ban - player has no stored IP address");
                            return;
                        }
                    }
                }

                sendSuccess(conn, "Punishment issued for " + resolvedName);
                plugin.getLogger().info("[WebPanel] " + staffName + " issued " + typeStr + " to " + resolvedName + ": " + reason);

            } catch (IllegalArgumentException e) {
                sendError(conn, "INVALID_TYPE", "Unknown punishment type: " + typeStr);
            }
        });
    }

    private void revokePunishment(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String caseId = data.has("caseId") ? data.get("caseId").getAsString() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Revoked via Web Panel";

        if (caseId.isEmpty()) {
            sendError(conn, "MISSING_DATA", "Case ID required");
            return;
        }

        // First get punishment details for broadcasting
        plugin.getPunishmentManager().getPunishmentByCaseId(caseId).thenAccept(punishment -> {
            if (punishment == null) {
                sendError(conn, "NOT_FOUND", "Punishment not found");
                return;
            }

            if (!punishment.isActive()) {
                sendError(conn, "ALREADY_REVOKED", "Punishment is already revoked");
                return;
            }

            // Use removePunishmentByCaseId for proper handling
            plugin.getPunishmentManager().removePunishmentByCaseId(caseId,
                session.playerUuid, session.playerName, reason).thenAccept(success -> {

                if (success) {
                    sendSuccess(conn, "Punishment revoked");
                    plugin.getLogger().info("[WebPanel] " + session.playerName + " revoked " + caseId +
                        " (" + punishment.getType() + " for " + punishment.getPlayerName() + ")");

                    // Broadcast to all web panel clients
                    JsonObject broadcast = new JsonObject();
                    broadcast.addProperty("type", "PUNISHMENT_REVOKED");
                    JsonObject broadcastData = new JsonObject();
                    broadcastData.addProperty("caseId", caseId);
                    broadcastData.addProperty("playerUuid", punishment.getPlayerUuid().toString());
                    broadcastData.addProperty("playerName", punishment.getPlayerName());
                    broadcastData.addProperty("punishmentType", punishment.getType().name());
                    broadcastData.addProperty("revokedBy", session.playerName);
                    broadcastData.addProperty("revokedAt", System.currentTimeMillis());
                    broadcastData.addProperty("reason", reason);
                    broadcast.add("data", broadcastData);
                    broadcast(GSON.toJson(broadcast));
                } else {
                    sendError(conn, "FAILED", "Failed to revoke punishment");
                }
            });
        });
    }

    private void addToWatchlist(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String uuid = data.has("playerUuid") ? data.get("playerUuid").getAsString() : "";
        String name = data.has("playerName") ? data.get("playerName").getAsString() : "Unknown";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Added via Web Panel";

        try {
            plugin.getWatchlistManager().addToWatchlist(UUID.fromString(uuid), name,
                session.playerUuid, session.playerName, reason);
            sendSuccess(conn, "Added to watchlist");
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid UUID");
        }
    }

    private void removeFromWatchlist(WebSocketConnection conn, JsonObject data) {
        String uuid = data.has("uuid") ? data.get("uuid").getAsString() : "";
        try {
            plugin.getWatchlistManager().removeFromWatchlist(UUID.fromString(uuid));
            sendSuccess(conn, "Removed from watchlist");
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid UUID");
        }
    }

    private void sendStaffChatFromPanel(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String message = data.has("message") ? data.get("message").getAsString() : "";
        if (message.isEmpty()) {
            sendError(conn, "EMPTY_MESSAGE", "Message cannot be empty");
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getStaffChatManager().broadcastFromWebPanel(session.playerName, message);
        });
        sendSuccess(conn, "Message sent");
    }

    private void kickPlayer(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String playerName = data.has("playerName") ? data.get("playerName").getAsString() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Kicked via Web Panel";

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                player.kick(TextUtil.parse("<red>" + reason));
                sendSuccess(conn, "Player kicked");
            } else {
                sendError(conn, "NOT_ONLINE", "Player not online");
            }
        });
    }

    private void clearChat(WebSocketConnection conn, WebPanelSession session) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!player.hasPermission("moderex.bypass.clearchat")) {
                    for (int i = 0; i < 100; i++) player.sendMessage("");
                }
            }
            sendSuccess(conn, "Chat cleared");
            plugin.getLogger().info("[WebPanel] " + session.playerName + " cleared chat");
        });
    }

    private void updateUserSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        UserPanelSettings settings = getUserSettings(session.playerUuid);
        if (data.has("chatAlerts")) settings.chatAlerts = data.get("chatAlerts").getAsBoolean();
        if (data.has("soundEnabled")) settings.soundEnabled = data.get("soundEnabled").getAsBoolean();
        if (data.has("watchlistToasts")) settings.watchlistToasts = data.get("watchlistToasts").getAsBoolean();
        if (data.has("themeColor")) settings.themeColor = data.get("themeColor").getAsString();
        if (data.has("backgroundPattern")) settings.backgroundPattern = data.get("backgroundPattern").getAsString();
        if (data.has("watchlistAlerts")) settings.watchlistAlerts = data.get("watchlistAlerts").getAsBoolean();
        if (data.has("deviceTrustEnabled")) {
            boolean newValue = data.get("deviceTrustEnabled").getAsBoolean();
            // If disabling device trust, clear all trusted devices for this user
            if (!newValue && settings.deviceTrustEnabled) {
                WebAuthManager authManager = plugin.getWebAuthManager();
                if (authManager != null) {
                    int removed = authManager.removeAllTrustedDevices(session.playerUuid);
                    plugin.logDebug("Removed " + removed + " trusted devices for " + session.playerName);
                }
            }
            settings.deviceTrustEnabled = newValue;
        }
        saveUserSettings();

        // Sync to in-game staff settings
        var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
        if (staffSettings != null) {
            boolean changed = false;
            if (data.has("staffChatEnabled")) {
                staffSettings.setStaffChatEnabled(data.get("staffChatEnabled").getAsBoolean());
                changed = true;
            }
            if (data.has("staffChatSound")) {
                staffSettings.setStaffChatSound(data.get("staffChatSound").getAsBoolean());
                changed = true;
            }
            if (data.has("watchlistJoinAlerts")) {
                staffSettings.setWatchlistJoinAlerts(data.get("watchlistJoinAlerts").getAsBoolean());
                changed = true;
            }
            if (data.has("watchlistQuitAlerts")) {
                staffSettings.setWatchlistQuitAlerts(data.get("watchlistQuitAlerts").getAsBoolean());
                changed = true;
            }
            if (data.has("watchlistActivityAlerts")) {
                staffSettings.setWatchlistActivityAlerts(data.get("watchlistActivityAlerts").getAsBoolean());
                changed = true;
            }
            if (data.has("autoVanishOnJoin")) {
                staffSettings.setAutoVanishOnJoin(data.get("autoVanishOnJoin").getAsBoolean());
                changed = true;
            }
            if (data.has("vanishNightVision")) {
                staffSettings.setVanishNightVision(data.get("vanishNightVision").getAsBoolean());
                changed = true;
            }
            if (data.has("compactMode")) {
                staffSettings.setCompactMode(data.get("compactMode").getAsBoolean());
                changed = true;
            }
            if (data.has("inGameSoundEnabled")) {
                staffSettings.setSoundEnabled(data.get("inGameSoundEnabled").getAsBoolean());
                changed = true;
            }
            if (data.has("actionBarAlerts")) {
                staffSettings.setActionBarAlerts(data.get("actionBarAlerts").getAsBoolean());
                changed = true;
            }
            if (data.has("inGameChatAlerts")) {
                staffSettings.setChatAlerts(data.get("inGameChatAlerts").getAsBoolean());
                changed = true;
            }
            if (data.has("bossBarAlerts")) {
                staffSettings.setBossBarAlerts(data.get("bossBarAlerts").getAsBoolean());
                changed = true;
            }

            // Punishment alert levels
            if (data.has("banAlerts")) {
                staffSettings.setBanAlerts(parseAlertLevel(data.get("banAlerts").getAsString()));
                changed = true;
            }
            if (data.has("kickAlerts")) {
                staffSettings.setKickAlerts(parseAlertLevel(data.get("kickAlerts").getAsString()));
                changed = true;
            }
            if (data.has("muteAlerts")) {
                staffSettings.setMuteAlerts(parseAlertLevel(data.get("muteAlerts").getAsString()));
                changed = true;
            }
            if (data.has("warnAlerts")) {
                staffSettings.setWarnAlerts(parseAlertLevel(data.get("warnAlerts").getAsString()));
                changed = true;
            }
            if (data.has("pardonAlerts")) {
                staffSettings.setPardonAlerts(parseAlertLevel(data.get("pardonAlerts").getAsString()));
                changed = true;
            }

            // Other alert types
            if (data.has("automodAlerts")) {
                staffSettings.setAutomodAlerts(parseAlertLevel(data.get("automodAlerts").getAsString()));
                changed = true;
            }
            if (data.has("anticheatAlerts")) {
                staffSettings.setAnticheatAlerts(parseAlertLevel(data.get("anticheatAlerts").getAsString()));
                changed = true;
            }
            if (data.has("anticheatMinVL")) {
                staffSettings.setAnticheatMinVL(data.get("anticheatMinVL").getAsInt());
                changed = true;
            }
            if (data.has("nicknameAlerts")) {
                staffSettings.setNicknameAlerts(parseAlertLevel(data.get("nicknameAlerts").getAsString()));
                changed = true;
            }
            if (data.has("commandAlerts")) {
                staffSettings.setCommandAlerts(parseCommandAlertLevel(data.get("commandAlerts").getAsString()));
                changed = true;
            }
            if (data.has("joinLeaveAlerts")) {
                staffSettings.setJoinLeaveAlerts(parseAlertLevel(data.get("joinLeaveAlerts").getAsString()));
                changed = true;
            }
            if (data.has("lagAlerts")) {
                staffSettings.setLagAlerts(data.get("lagAlerts").getAsBoolean());
                changed = true;
            }

            // Web panel notification modes
            if (data.has("webNotifyPunishments")) {
                staffSettings.setWebNotifyPunishments(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyPunishments").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyAutomod")) {
                staffSettings.setWebNotifyAutomod(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyAutomod").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyAnticheat")) {
                staffSettings.setWebNotifyAnticheat(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyAnticheat").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyWatchlist")) {
                staffSettings.setWebNotifyWatchlist(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyWatchlist").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyStaffChat")) {
                staffSettings.setWebNotifyStaffChat(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyStaffChat").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyCommands")) {
                staffSettings.setWebNotifyCommands(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyCommands").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyNickname")) {
                staffSettings.setWebNotifyNickname(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyNickname").getAsString()));
                changed = true;
            }
            if (data.has("webNotifyLag")) {
                staffSettings.setWebNotifyLag(
                    com.blockforge.moderex.staff.StaffSettings.WebNotifyMode.fromString(data.get("webNotifyLag").getAsString()));
                changed = true;
            }

            // Web panel display settings
            if (data.has("webToastPosition")) {
                staffSettings.setWebToastPosition(
                    com.blockforge.moderex.staff.StaffSettings.ToastPosition.fromString(data.get("webToastPosition").getAsString()));
                changed = true;
            }
            if (data.has("webAlertDurationSeconds")) {
                staffSettings.setWebAlertDurationSeconds(data.get("webAlertDurationSeconds").getAsInt());
                changed = true;
            }

            // Web panel sound settings
            if (data.has("webSoundPunishments")) {
                staffSettings.setWebSoundPunishments(data.get("webSoundPunishments").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundAutomod")) {
                staffSettings.setWebSoundAutomod(data.get("webSoundAutomod").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundAnticheat")) {
                staffSettings.setWebSoundAnticheat(data.get("webSoundAnticheat").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundWatchlist")) {
                staffSettings.setWebSoundWatchlist(data.get("webSoundWatchlist").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundStaffChat")) {
                staffSettings.setWebSoundStaffChat(data.get("webSoundStaffChat").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundCommands")) {
                staffSettings.setWebSoundCommands(data.get("webSoundCommands").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundNickname")) {
                staffSettings.setWebSoundNickname(data.get("webSoundNickname").getAsBoolean());
                changed = true;
            }
            if (data.has("webSoundLag")) {
                staffSettings.setWebSoundLag(data.get("webSoundLag").getAsBoolean());
                changed = true;
            }

            if (changed) {
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                plugin.logDebug("[WebPanel] Saved staff settings for " + session.playerName + " to database: " +
                    "ban=" + staffSettings.getBanAlerts() + ", " +
                    "kick=" + staffSettings.getKickAlerts() + ", " +
                    "mute=" + staffSettings.getMuteAlerts() + ", " +
                    "command=" + staffSettings.getCommandAlerts() + ", " +
                    "toastPos=" + staffSettings.getWebToastPosition() + ", " +
                    "duration=" + staffSettings.getWebAlertDurationSeconds() + "s");
            }
        }

        sendSuccess(conn, "Settings saved");
    }

    // Helper method to parse AlertLevel from string
    private com.blockforge.moderex.staff.StaffSettings.AlertLevel parseAlertLevel(String s) {
        if (s == null) return com.blockforge.moderex.staff.StaffSettings.AlertLevel.EVERYONE;
        return switch (s.toLowerCase()) {
            case "watchlist_only" -> com.blockforge.moderex.staff.StaffSettings.AlertLevel.WATCHLIST_ONLY;
            case "off" -> com.blockforge.moderex.staff.StaffSettings.AlertLevel.OFF;
            default -> com.blockforge.moderex.staff.StaffSettings.AlertLevel.EVERYONE;
        };
    }

    // Helper method to parse CommandAlertLevel from string
    private com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel parseCommandAlertLevel(String s) {
        if (s == null) return com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel.BLACKLISTED_ONLY;
        return switch (s.toLowerCase()) {
            case "everyone" -> com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel.EVERYONE;
            case "watchlist_only" -> com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel.WATCHLIST_ONLY;
            case "off" -> com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel.OFF;
            default -> com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel.BLACKLISTED_ONLY;
        };
    }

    private void setChatLock(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        boolean locked = data.has("locked") && data.get("locked").getAsBoolean();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getConfigManager().getSettings().setChatEnabled(!locked);

            // Notify all players
            String message = locked
                    ? "<red><bold>Chat has been locked by staff."
                    : "<green>Chat has been unlocked.";
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(TextUtil.parse(message));
            }

            sendSuccess(conn, locked ? "Chat locked" : "Chat unlocked");
            broadcastChatStatus();
            plugin.getLogger().info("[WebPanel] " + session.playerName + (locked ? " locked" : " unlocked") + " chat");
        });
    }

    private void setSlowmode(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        int seconds = data.has("seconds") ? data.get("seconds").getAsInt() : 0;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(Math.max(0, Math.min(300, seconds)));

            if (seconds > 0) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.sendMessage(TextUtil.parse("<yellow>Slowmode has been set to <white>" + seconds + " seconds<yellow>."));
                }
            }

            sendSuccess(conn, seconds > 0 ? "Slowmode set to " + seconds + "s" : "Slowmode disabled");
            broadcastChatStatus();
            plugin.getLogger().info("[WebPanel] " + session.playerName + " set slowmode to " + seconds + "s");
        });
    }

    private void sendChatStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "CHAT_STATUS");
        JsonObject data = new JsonObject();
        data.addProperty("chatEnabled", plugin.getConfigManager().getSettings().isChatEnabled());
        data.addProperty("slowmodeSeconds", plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds());
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendServerSettings(WebSocketConnection conn) {
        var settings = plugin.getConfigManager().getSettings();
        JsonObject response = new JsonObject();
        response.addProperty("type", "SERVER_SETTINGS");
        JsonObject data = new JsonObject();

        // Chat settings
        data.addProperty("chatEnabled", settings.isChatEnabled());
        data.addProperty("slowmodeSeconds", settings.getDefaultSlowmodeSeconds());

        // Mute settings
        JsonObject muteSettings = new JsonObject();
        muteSettings.addProperty("chat", settings.isMuteBlocksChat());
        muteSettings.addProperty("msg", settings.isMuteBlocksMsg());
        muteSettings.addProperty("signs", settings.isMuteBlocksSigns());
        muteSettings.addProperty("books", settings.isMuteBlocksBooks());
        muteSettings.addProperty("broadcast", settings.isMuteBlocksBroadcast());
        muteSettings.addProperty("voice", settings.isMuteBlocksVoice());
        muteSettings.addProperty("voiceJoin", settings.isMuteBlocksVoiceJoin());
        data.add("muteSettings", muteSettings);

        // Warn settings
        JsonObject warnSettings = new JsonObject();
        warnSettings.addProperty("notify", settings.isWarnNotifyStaff());
        warnSettings.addProperty("autoEscalate", settings.isWarnAutoEscalate());
        data.add("warnSettings", warnSettings);

        // Anticheat settings
        JsonObject acSettings = new JsonObject();
        acSettings.addProperty("rebrandAlerts", settings.isAnticheatRebrandAlerts());
        acSettings.addProperty("blockOriginalMessages", settings.isAnticheatBlockOriginalMessages());
        data.add("anticheatSettings", acSettings);

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void updateMuteSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        var settings = plugin.getConfigManager().getSettings();

        if (data.has("chat")) settings.setMuteBlocksChat(data.get("chat").getAsBoolean());
        if (data.has("msg")) settings.setMuteBlocksMsg(data.get("msg").getAsBoolean());
        if (data.has("signs")) settings.setMuteBlocksSigns(data.get("signs").getAsBoolean());
        if (data.has("books")) settings.setMuteBlocksBooks(data.get("books").getAsBoolean());
        if (data.has("broadcast")) settings.setMuteBlocksBroadcast(data.get("broadcast").getAsBoolean());
        if (data.has("voice")) settings.setMuteBlocksVoice(data.get("voice").getAsBoolean());
        if (data.has("voiceJoin")) settings.setMuteBlocksVoiceJoin(data.get("voiceJoin").getAsBoolean());

        sendSuccess(conn, "Mute settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated mute settings");
    }

    private void updateWarnSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        var settings = plugin.getConfigManager().getSettings();

        if (data.has("notify")) settings.setWarnNotifyStaff(data.get("notify").getAsBoolean());
        if (data.has("autoEscalate")) settings.setWarnAutoEscalate(data.get("autoEscalate").getAsBoolean());

        sendSuccess(conn, "Warn settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated warn settings");
    }

    private void updateAnticheatSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        var settings = plugin.getConfigManager().getSettings();

        if (data.has("rebrandAlerts")) settings.setAnticheatRebrandAlerts(data.get("rebrandAlerts").getAsBoolean());
        if (data.has("blockOriginalMessages")) settings.setAnticheatBlockOriginalMessages(data.get("blockOriginalMessages").getAsBoolean());

        sendSuccess(conn, "Anticheat settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated anticheat settings");
    }

    private void broadcastServerSettings() {
        for (WebSocketConnection conn : sessions.keySet()) {
            sendServerSettings(conn);
        }
        for (var entry : samePortSessions.entrySet()) {
            WebSocketFrameHandler handler = samePortConnections.get(entry.getKey());
            if (handler != null) {
                var settings = plugin.getConfigManager().getSettings();
                JsonObject response = new JsonObject();
                response.addProperty("type", "SERVER_SETTINGS");
                JsonObject data = new JsonObject();
                data.addProperty("chatEnabled", settings.isChatEnabled());
                data.addProperty("slowmodeSeconds", settings.getDefaultSlowmodeSeconds());

                JsonObject muteSettings = new JsonObject();
                muteSettings.addProperty("chat", settings.isMuteBlocksChat());
                muteSettings.addProperty("msg", settings.isMuteBlocksMsg());
                muteSettings.addProperty("signs", settings.isMuteBlocksSigns());
                muteSettings.addProperty("books", settings.isMuteBlocksBooks());
                muteSettings.addProperty("broadcast", settings.isMuteBlocksBroadcast());
                muteSettings.addProperty("voice", settings.isMuteBlocksVoice());
                muteSettings.addProperty("voiceJoin", settings.isMuteBlocksVoiceJoin());
                data.add("muteSettings", muteSettings);

                JsonObject warnSettings = new JsonObject();
                warnSettings.addProperty("notify", settings.isWarnNotifyStaff());
                warnSettings.addProperty("autoEscalate", settings.isWarnAutoEscalate());
                data.add("warnSettings", warnSettings);

                JsonObject acSettings = new JsonObject();
                acSettings.addProperty("rebrandAlerts", settings.isAnticheatRebrandAlerts());
                acSettings.addProperty("blockOriginalMessages", settings.isAnticheatBlockOriginalMessages());
                data.add("anticheatSettings", acSettings);

                response.add("data", data);
                handler.send(GSON.toJson(response));
            }
        }
    }

    private void clearTrustedDevices(WebSocketConnection conn, WebPanelSession session) {
        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendError(conn, "AUTH_UNAVAILABLE", "Authentication system not available");
            return;
        }

        int removed = authManager.removeAllTrustedDevices(session.playerUuid);
        sendSuccess(conn, "Cleared " + removed + " trusted device(s)");
        plugin.logDebug("Cleared " + removed + " trusted devices for " + session.playerName + " via web panel");
    }

    private void sendTrustedDeviceCount(WebSocketConnection conn, WebPanelSession session) {
        WebAuthManager authManager = plugin.getWebAuthManager();
        int count = 0;
        if (authManager != null) {
            count = authManager.getTrustedDeviceCount(session.playerUuid);
        }

        JsonObject response = new JsonObject();
        response.addProperty("type", "TRUSTED_DEVICE_COUNT");
        JsonObject data = new JsonObject();
        data.addProperty("count", count);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void getExternalPunishments(WebSocketConnection conn, JsonObject data) {
        String uuidStr = data.has("playerUuid") ? data.get("playerUuid").getAsString() : null;
        if (uuidStr == null) {
            sendError(conn, "MISSING_PARAMETER", "Missing playerUuid");
            return;
        }

        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
        var moderationHookManager = plugin.getModerationHookManager();

        if (moderationHookManager == null || !moderationHookManager.hasAvailableHooks()) {
            // Send empty response
            JsonObject response = new JsonObject();
            response.addProperty("type", "EXTERNAL_PUNISHMENTS");
            JsonObject responseData = new JsonObject();
            responseData.add("punishments", new JsonObject());
            responseData.add("plugins", new JsonArray());
            response.add("data", responseData);
            conn.send(GSON.toJson(response));
            return;
        }

        // Get punishments from all available plugins
        var allPunishments = moderationHookManager.getAllPunishments(uuid);

        JsonObject response = new JsonObject();
        response.addProperty("type", "EXTERNAL_PUNISHMENTS");
        JsonObject responseData = new JsonObject();

        // Build punishments object by plugin
        JsonObject punishmentsJson = new JsonObject();
        for (var entry : allPunishments.entrySet()) {
            String pluginName = entry.getKey();
            var punishments = entry.getValue();

            JsonArray punArray = new JsonArray();
            for (var pun : punishments) {
                JsonObject punJson = new JsonObject();
                punJson.addProperty("source", pun.getSource());
                punJson.addProperty("playerName", pun.getPlayerName());
                punJson.addProperty("type", pun.getType());
                punJson.addProperty("reason", pun.getReason());
                punJson.addProperty("staff", pun.getStaff());
                punJson.addProperty("createdAt", pun.getCreatedAt());
                punJson.addProperty("expiresAt", pun.getExpiresAt());
                punJson.addProperty("active", pun.isActive());
                punJson.addProperty("serverId", pun.getServerId());
                punArray.add(punJson);
            }
            punishmentsJson.add(pluginName, punArray);
        }
        responseData.add("punishments", punishmentsJson);

        // Add available plugins list
        JsonArray pluginsArray = new JsonArray();
        for (var hook : moderationHookManager.getAvailableHooks()) {
            pluginsArray.add(hook.getPluginName());
        }
        responseData.add("plugins", pluginsArray);

        response.add("data", responseData);
        conn.send(GSON.toJson(response));
    }

    private void importExternalPunishments(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String uuidStr = data.has("playerUuid") ? data.get("playerUuid").getAsString() : null;
        String pluginName = data.has("plugin") ? data.get("plugin").getAsString() : null;

        if (uuidStr == null) {
            sendError(conn, "MISSING_PARAMETER", "Missing playerUuid");
            return;
        }

        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
        var moderationHookManager = plugin.getModerationHookManager();

        if (moderationHookManager == null) {
            sendError(conn, "NOT_AVAILABLE", "Moderation hook manager not available");
            return;
        }

        int imported;
        if (pluginName == null) {
            // Import from all plugins
            imported = moderationHookManager.importAllPunishments(uuid);
        } else {
            // Import from specific plugin
            imported = moderationHookManager.importPunishments(pluginName, uuid);
        }

        // Send success response
        JsonObject response = new JsonObject();
        response.addProperty("type", "IMPORT_SUCCESS");
        JsonObject responseData = new JsonObject();
        responseData.addProperty("imported", imported);
        responseData.addProperty("playerUuid", uuidStr);
        response.add("data", responseData);
        conn.send(GSON.toJson(response));

        // Log the import
        plugin.getLogger().info(session.playerName + " imported " + imported + " punishment(s) for " + uuid +
                (pluginName != null ? " from " + pluginName : " from all plugins"));
    }

    private void kickAllPlayers(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Server maintenance";

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            int kickedCount = 0;
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                // Don't kick the staff member if they're in-game
                if (player.getUniqueId().equals(session.playerUuid)) {
                    continue;
                }

                net.kyori.adventure.text.Component kickMessage = buildKickAllMessage(reason, session.playerName);
                player.kick(kickMessage);
                kickedCount++;
            }

            // Notify in-game staff
            net.kyori.adventure.text.Component staffNotification = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<dark_gray>[<red><bold>KICKALL</bold></red>]</dark_gray> " +
                            "<gold>" + session.playerName + "</gold> <gray>kicked</gray> <red>" + kickedCount + " player(s)</red> " +
                            "<dark_gray>»</dark_gray> <white>" + reason + "</white>"
            );

            for (org.bukkit.entity.Player staff : plugin.getServer().getOnlinePlayers()) {
                if (PermissionUtil.hasPermission(staff, "moderex.notify.punishments")) {
                    staff.sendMessage(staffNotification);
                }
            }

            // Log
            plugin.getLogger().info(session.playerName + " (via web panel) kicked " + kickedCount + " player(s) - Reason: " + reason);

            // Broadcast to web panel
            broadcastKickAll(session.playerName, kickedCount, reason);

            // Send success to requestor
            JsonObject response = new JsonObject();
            response.addProperty("type", "KICK_ALL_SUCCESS");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("count", kickedCount);
            responseData.addProperty("reason", reason);
            response.add("data", responseData);
            conn.send(GSON.toJson(response));
        });
    }

    private net.kyori.adventure.text.Component buildKickAllMessage(String reason, String staffName) {
        String message = "\n§c§lKICKED FROM SERVER\n\n§7Reason: §f" + reason + "\n\n§8Staff: §e" + staffName + "\n";
        return net.kyori.adventure.text.Component.text(message);
    }

    private void sendReplayList(WebSocketConnection conn) {
        plugin.getReplayManager().getSavedReplays().thenAccept(replays -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "REPLAY_LIST");
            JsonObject data = new JsonObject();
            JsonArray replayArray = new JsonArray();

            for (var replay : replays) {
                JsonObject r = new JsonObject();
                r.addProperty("sessionId", replay.sessionId());
                r.addProperty("primaryUuid", replay.primaryUuid().toString());
                r.addProperty("primaryName", replay.primaryName());
                r.addProperty("startTime", replay.startTime());
                r.addProperty("endTime", replay.endTime());
                r.addProperty("worldName", replay.worldName());
                r.addProperty("reason", replay.reason().name());
                r.addProperty("playerCount", replay.playerCount());
                replayArray.add(r);
            }

            // Always include an example replay for demo purposes
            JsonObject example = new JsonObject();
            long now = System.currentTimeMillis();
            example.addProperty("sessionId", "example-demo-replay");
            example.addProperty("primaryUuid", "00000000-0000-0000-0000-000000000001");
            example.addProperty("primaryName", "DemoPlayer");
            example.addProperty("startTime", now - 10000);
            example.addProperty("endTime", now);
            example.addProperty("worldName", "world");
            example.addProperty("reason", "MANUAL");
            example.addProperty("playerCount", 2);
            example.addProperty("isExample", true);
            replayArray.add(example);

            data.add("replays", replayArray);
            response.add("data", data);
            conn.send(GSON.toJson(response));
        });
    }

    private void sendReplayData(WebSocketConnection conn, JsonObject requestData) {
        String sessionId = requestData.has("sessionId") ? requestData.get("sessionId").getAsString() : null;
        if (sessionId == null) {
            sendError(conn, "MISSING_SESSION_ID", "Session ID is required");
            return;
        }

        // Handle example replay
        if ("example-demo-replay".equals(sessionId)) {
            sendExampleReplayData(conn);
            return;
        }

        plugin.getReplayManager().loadReplay(sessionId).thenAccept(session -> {
            if (session == null) {
                sendError(conn, "REPLAY_NOT_FOUND", "Replay not found: " + sessionId);
                return;
            }

            JsonObject response = new JsonObject();
            response.addProperty("type", "REPLAY_DATA");
            JsonObject data = new JsonObject();

            // Replay metadata
            JsonObject replay = new JsonObject();
            replay.addProperty("sessionId", session.getSessionId());
            replay.addProperty("primaryUuid", session.getPrimaryPlayerUuid().toString());
            replay.addProperty("primaryName", session.getPrimaryPlayerName());
            replay.addProperty("startTime", session.getStartTime());
            replay.addProperty("endTime", session.getEndTime());
            replay.addProperty("worldName", session.getWorldName());
            replay.addProperty("reason", session.getReason().name());
            data.add("replay", replay);

            // Snapshots with player info
            JsonArray snapshots = new JsonArray();
            for (UUID playerUuid : session.getRecordedPlayerUuids()) {
                String playerName = session.getPlayerName(playerUuid);
                for (var snapshot : session.getSnapshots(playerUuid)) {
                    JsonObject s = new JsonObject();
                    s.addProperty("playerUuid", playerUuid.toString());
                    s.addProperty("playerName", playerName);
                    s.addProperty("timestamp", snapshot.getTimestamp());
                    s.addProperty("x", snapshot.getX());
                    s.addProperty("y", snapshot.getY());
                    s.addProperty("z", snapshot.getZ());
                    s.addProperty("yaw", snapshot.getYaw());
                    s.addProperty("pitch", snapshot.getPitch());
                    s.addProperty("sneaking", snapshot.isSneaking());
                    s.addProperty("sprinting", snapshot.isSprinting());
                    s.addProperty("swimming", snapshot.isSwimming());
                    s.addProperty("gliding", snapshot.isGliding());
                    s.addProperty("onGround", snapshot.isOnGround());
                    s.addProperty("action", snapshot.getAction().name());
                    s.addProperty("actionData", snapshot.getActionData());
                    snapshots.add(s);
                }
            }
            data.add("snapshots", snapshots);

            response.add("data", data);
            conn.send(GSON.toJson(response));
        });
    }

    private void sendExampleReplayData(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "REPLAY_DATA");
        JsonObject data = new JsonObject();

        long now = System.currentTimeMillis();
        long startTime = now - 10000; // 10 seconds ago

        // Replay metadata
        JsonObject replay = new JsonObject();
        replay.addProperty("sessionId", "example-demo-replay");
        replay.addProperty("primaryUuid", "00000000-0000-0000-0000-000000000001");
        replay.addProperty("primaryName", "DemoPlayer");
        replay.addProperty("startTime", startTime);
        replay.addProperty("endTime", now);
        replay.addProperty("worldName", "world");
        replay.addProperty("reason", "MANUAL");
        replay.addProperty("isExample", true);
        data.add("replay", replay);

        // Generate example snapshots - 2 players moving in circles for 10 seconds
        JsonArray snapshots = new JsonArray();
        String player1Uuid = "00000000-0000-0000-0000-000000000001";
        String player2Uuid = "00000000-0000-0000-0000-000000000002";
        String player1Name = "DemoPlayer";
        String player2Name = "TestSubject";

        double centerX = 0, centerZ = 0;

        for (int i = 0; i < 100; i++) {
            long timestamp = startTime + (i * 100); // 100ms intervals

            // Player 1 moves in a circle
            double angle1 = (i * 0.1);
            double p1X = centerX + Math.cos(angle1) * 8;
            double p1Z = centerZ + Math.sin(angle1) * 8;
            float p1Yaw = (float) Math.toDegrees(angle1) + 90;

            // Player 2 moves in opposite circle
            double angle2 = -(i * 0.08);
            double p2X = centerX + Math.cos(angle2) * 12;
            double p2Z = centerZ + Math.sin(angle2) * 12;
            float p2Yaw = (float) Math.toDegrees(angle2) + 90;

            // Determine player states
            boolean p1Sprinting = i >= 20 && i < 40;
            boolean p1Sneaking = i >= 50 && i < 70;
            boolean p2Sprinting = i >= 30 && i < 60;
            boolean p2Sneaking = i >= 80 && i < 100;

            // Player 1 snapshot
            JsonObject s1 = new JsonObject();
            s1.addProperty("playerUuid", player1Uuid);
            s1.addProperty("playerName", player1Name);
            s1.addProperty("timestamp", timestamp);
            s1.addProperty("x", p1X);
            s1.addProperty("y", 64);
            s1.addProperty("z", p1Z);
            s1.addProperty("yaw", p1Yaw);
            s1.addProperty("pitch", 0);
            s1.addProperty("sneaking", p1Sneaking);
            s1.addProperty("sprinting", p1Sprinting);
            s1.addProperty("swimming", false);
            s1.addProperty("gliding", false);
            s1.addProperty("onGround", true);
            s1.addProperty("action", "NONE");
            s1.addProperty("actionData", "");
            snapshots.add(s1);

            // Player 2 snapshot
            JsonObject s2 = new JsonObject();
            s2.addProperty("playerUuid", player2Uuid);
            s2.addProperty("playerName", player2Name);
            s2.addProperty("timestamp", timestamp);
            s2.addProperty("x", p2X);
            s2.addProperty("y", 64);
            s2.addProperty("z", p2Z);
            s2.addProperty("yaw", p2Yaw);
            s2.addProperty("pitch", 0);
            s2.addProperty("sneaking", p2Sneaking);
            s2.addProperty("sprinting", p2Sprinting);
            s2.addProperty("swimming", false);
            s2.addProperty("gliding", false);
            s2.addProperty("onGround", true);
            s2.addProperty("action", "NONE");
            s2.addProperty("actionData", "");
            snapshots.add(s2);

            // Add some events
            if (i == 25) {
                JsonObject chatSnap = new JsonObject();
                chatSnap.addProperty("playerUuid", player1Uuid);
                chatSnap.addProperty("playerName", player1Name);
                chatSnap.addProperty("timestamp", timestamp);
                chatSnap.addProperty("x", p1X);
                chatSnap.addProperty("y", 64);
                chatSnap.addProperty("z", p1Z);
                chatSnap.addProperty("yaw", p1Yaw);
                chatSnap.addProperty("pitch", 0);
                chatSnap.addProperty("sneaking", false);
                chatSnap.addProperty("sprinting", false);
                chatSnap.addProperty("swimming", false);
                chatSnap.addProperty("gliding", false);
                chatSnap.addProperty("onGround", true);
                chatSnap.addProperty("action", "CHAT");
                chatSnap.addProperty("actionData", "Hello from the demo replay!");
                snapshots.add(chatSnap);
            }

            if (i == 50) {
                JsonObject cmdSnap = new JsonObject();
                cmdSnap.addProperty("playerUuid", player2Uuid);
                cmdSnap.addProperty("playerName", player2Name);
                cmdSnap.addProperty("timestamp", timestamp);
                cmdSnap.addProperty("x", p2X);
                cmdSnap.addProperty("y", 64);
                cmdSnap.addProperty("z", p2Z);
                cmdSnap.addProperty("yaw", p2Yaw);
                cmdSnap.addProperty("pitch", 0);
                cmdSnap.addProperty("sneaking", false);
                cmdSnap.addProperty("sprinting", false);
                cmdSnap.addProperty("swimming", false);
                cmdSnap.addProperty("gliding", false);
                cmdSnap.addProperty("onGround", true);
                cmdSnap.addProperty("action", "COMMAND");
                cmdSnap.addProperty("actionData", "/gamemode creative");
                snapshots.add(cmdSnap);
            }

            if (i == 75) {
                JsonObject attackSnap = new JsonObject();
                attackSnap.addProperty("playerUuid", player1Uuid);
                attackSnap.addProperty("playerName", player1Name);
                attackSnap.addProperty("timestamp", timestamp);
                attackSnap.addProperty("x", p1X);
                attackSnap.addProperty("y", 64);
                attackSnap.addProperty("z", p1Z);
                attackSnap.addProperty("yaw", p1Yaw);
                attackSnap.addProperty("pitch", 0);
                attackSnap.addProperty("sneaking", false);
                attackSnap.addProperty("sprinting", false);
                attackSnap.addProperty("swimming", false);
                attackSnap.addProperty("gliding", false);
                attackSnap.addProperty("onGround", true);
                attackSnap.addProperty("action", "DAMAGE_DEALT");
                attackSnap.addProperty("actionData", "Dealt 5.0 dmg to TestSubject");
                snapshots.add(attackSnap);
            }
        }

        data.add("snapshots", snapshots);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    public void broadcastChatStatus() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT_STATUS");
        JsonObject data = new JsonObject();
        data.addProperty("chatEnabled", plugin.getConfigManager().getSettings().isChatEnabled());
        data.addProperty("slowmodeSeconds", plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    // ==================== Helper Methods ====================

    private JsonObject punishmentToJson(Punishment p) {
        JsonObject json = new JsonObject();
        json.addProperty("caseId", p.getCaseId());
        json.addProperty("playerUuid", p.getPlayerUuid().toString());
        json.addProperty("playerName", p.getPlayerName());
        json.addProperty("type", p.getType().name());
        json.addProperty("reason", p.getReason());
        json.addProperty("staffName", p.getStaffName());
        json.addProperty("createdAt", p.getCreatedAt());
        json.addProperty("expiresAt", p.getExpiresAt());
        json.addProperty("active", p.isActive());

        // Add duration field
        long expiresAt = p.getExpiresAt();
        long createdAt = p.getCreatedAt();
        if (expiresAt == -1) {
            json.addProperty("duration", "Permanent");
        } else if (expiresAt > 0 && createdAt > 0) {
            long durationMs = expiresAt - createdAt;
            json.addProperty("duration", DurationParser.format(durationMs, true));
        } else {
            json.addProperty("duration", "Unknown");
        }

        return json;
    }

    private void sendError(WebSocketConnection conn, String code, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ERROR");
        JsonObject data = new JsonObject();
        data.addProperty("code", code);
        data.addProperty("message", message);
        response.add("data", data);
        conn.send(GSON.toJson(response));

        // Debug: Error sent
        ErrorCode errorCode = mapErrorCodeToEnum(code);
        if (errorCode != null) {
            debugError(errorCode, message);
        } else {
            debugWarning(DebugCategory.REQUEST, "Error response sent", "Code: " + code + ", Message: " + message);
        }
    }

    private ErrorCode mapErrorCodeToEnum(String code) {
        return switch (code) {
            case "NOT_AUTHENTICATED" -> ErrorCode.AUTH_NOT_AUTHENTICATED;
            case "INVALID_UUID" -> ErrorCode.REQUEST_INVALID_UUID;
            case "MISSING_FIELD" -> ErrorCode.REQUEST_MISSING_FIELD;
            case "PLAYER_NOT_FOUND" -> ErrorCode.REQUEST_PLAYER_NOT_FOUND;
            case "RULE_NOT_FOUND" -> ErrorCode.AUTOMOD_RULE_NOT_FOUND;
            case "DATABASE_ERROR" -> ErrorCode.DATABASE_QUERY_FAILED;
            default -> null;
        };
    }

    private void sendAuthFailed(WebSocketConnection conn, String code, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTH_FAILED");
        JsonObject data = new JsonObject();
        data.addProperty("code", code);
        data.addProperty("message", message);
        response.add("data", data);
        conn.send(GSON.toJson(response));

        // Debug: Authentication failed
        ErrorCode errorCode = mapAuthCodeToErrorCode(code);
        if (errorCode != null) {
            debugError(errorCode, "IP: " + getClientIp(conn));
        } else {
            debugWarning(DebugCategory.AUTH, "Authentication failed", "Code: " + code + ", Message: " + message);
        }
    }

    private ErrorCode mapAuthCodeToErrorCode(String code) {
        return switch (code) {
            case "INVALID_CODE" -> ErrorCode.AUTH_INVALID_CODE;
            case "INVALID_TOKEN" -> ErrorCode.AUTH_INVALID_TOKEN;
            case "TOKEN_REQUIRED" -> ErrorCode.AUTH_TOKEN_REQUIRED;
            case "RATE_LIMITED" -> ErrorCode.AUTH_RATE_LIMITED;
            case "SESSION_EXPIRED" -> ErrorCode.AUTH_SESSION_EXPIRED;
            case "AUTH_UNAVAILABLE" -> ErrorCode.AUTH_UNAVAILABLE;
            default -> null;
        };
    }

    private void sendSuccess(WebSocketConnection conn, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SUCCESS");
        JsonObject data = new JsonObject();
        data.addProperty("message", message);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendAccessDenied(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ACCESS_DENIED");
        JsonObject data = new JsonObject();
        data.addProperty("message", "No permission to access web panel");
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    // ==================== Broadcast Methods ====================

    public void broadcastStaffChat(String playerName, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "STAFFCHAT_MESSAGE");
        JsonObject data = new JsonObject();
        data.addProperty("player", playerName);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastPunishment(Punishment punishment) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PUNISHMENT_CREATED");
        json.add("data", punishmentToJson(punishment));
        broadcast(GSON.toJson(json));

        // Debug: Punishment broadcast
        debugSuccess(DebugCategory.PUNISHMENT, "Punishment broadcast",
                "Type: " + punishment.getType() + ", Target: " + punishment.getPlayerName() +
                ", Staff: " + punishment.getStaffName());
    }

    public void broadcastPlayerJoin(Player player) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_JOIN");
        JsonObject data = new JsonObject();
        data.addProperty("uuid", player.getUniqueId().toString());
        data.addProperty("name", player.getName());
        json.add("data", data);
        broadcast(GSON.toJson(json));

        // Debug: Player join broadcast
        debugInfo(DebugCategory.PLAYER, "Player join broadcast", "Player: " + player.getName());
    }

    public void broadcastPlayerQuit(Player player) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_QUIT");
        JsonObject data = new JsonObject();
        data.addProperty("uuid", player.getUniqueId().toString());
        data.addProperty("name", player.getName());
        json.add("data", data);
        broadcast(GSON.toJson(json));

        // Debug: Player quit broadcast
        debugInfo(DebugCategory.PLAYER, "Player quit broadcast", "Player: " + player.getName());
    }

    public void broadcastWatchlistAlert(String type, String playerName, String details) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "WATCHLIST_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("alertType", type);
        data.addProperty("playerName", playerName);
        data.addProperty("details", details);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));

        // Debug: Watchlist alert broadcast
        debugWarning(DebugCategory.WATCHLIST, "Watchlist alert: " + type, "Player: " + playerName + ", Details: " + details);
    }

    public void broadcastWatchlistUpdate() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "WATCHLIST_UPDATE");
        JsonObject data = new JsonObject();

        // Build watchlist array
        JsonArray watchlist = new JsonArray();
        for (java.util.UUID uuid : plugin.getWatchlistManager().getWatchedPlayers()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("playerUuid", uuid.toString());
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            entry.addProperty("playerName", player.getName() != null ? player.getName() : "Unknown");
            entry.addProperty("online", player.isOnline());
            watchlist.add(entry);
        }
        data.add("watchlist", watchlist);
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAutomodTrigger(String playerName, String rule, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_TRIGGER");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("rule", rule);
        data.addProperty("message", message);
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAnticheatAlert(String anticheat, java.util.UUID playerUuid, String playerName,
                                         String checkName, String checkType, int violations, double vlLevel) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ANTICHEAT_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("anticheat", anticheat);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("playerName", playerName);
        data.addProperty("checkName", checkName);
        data.addProperty("checkType", checkType);
        data.addProperty("violations", violations);
        data.addProperty("vlLevel", vlLevel);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAlert(String title, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("message", message);
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    /**
     * Broadcast a custom alert to the web panel.
     * This respects each user's permissions and notification settings based on the category.
     *
     * @param category The alert category (anticheat, automod, punishments, watchlist, staffChat)
     * @param playerName The name of the player causing the alert
     * @param playerUuid The UUID of the player (can be null for offline players)
     * @param title The alert title
     * @param message The alert message
     */
    public void broadcastCustomAlert(String category, String playerName, java.util.UUID playerUuid, String title, String message) {
        // Get the AlertType from the category to check permissions
        AlertManager.AlertType alertType = AlertManager.AlertType.fromString(category);
        String requiredPermission = alertType.getPermission();

        JsonObject json = new JsonObject();
        json.addProperty("type", "CUSTOM_ALERT");

        JsonObject data = new JsonObject();
        data.addProperty("category", category);
        data.addProperty("playerName", playerName);
        if (playerUuid != null) {
            data.addProperty("playerUuid", playerUuid.toString());
        }
        data.addProperty("title", title);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());

        json.add("data", data);
        String jsonMessage = GSON.toJson(json);

        // Use permission-filtered broadcast
        broadcastWithPermission(jsonMessage, requiredPermission);
    }

    /**
     * Broadcast a message only to users who have the specified permission.
     *
     * @param message The JSON message to broadcast
     * @param permission The permission required to receive this message
     */
    private void broadcastWithPermission(String message, String permission) {
        if (broadcastExecutor != null && !broadcastExecutor.isShutdown()) {
            broadcastExecutor.execute(() -> {
                // WebSocket connections
                for (WebSocketConnection conn : connections) {
                    try {
                        WebPanelSession session = sessions.get(conn);
                        if (session != null) {
                            // Check if this user has permission to see this alert
                            if (hasAlertPermission(session.playerUuid, permission)) {
                                if (!conn.sendAsync(message)) {
                                    connections.remove(conn);
                                    sessions.remove(conn);
                                    conn.close();
                                }
                            }
                        }
                    } catch (Exception e) {
                        plugin.logDebug("[WebPanel] Error broadcasting with permission: " + e.getMessage());
                    }
                }

                // Same-port HTTP WebSocket connections
                for (Map.Entry<String, WebPanelSession> entry : samePortSessions.entrySet()) {
                    try {
                        WebPanelSession session = entry.getValue();
                        if (session != null) {
                            // Check if this user has permission to see this alert
                            if (hasAlertPermission(session.playerUuid, permission)) {
                                WebSocketFrameHandler handler = samePortConnections.get(entry.getKey());
                                if (handler != null) {
                                    handler.send(message);
                                }
                            }
                        }
                    } catch (Exception e) {
                        plugin.logDebug("[WebPanel] Error broadcasting to same-port with permission: " + e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * Check if a player has permission to see an alert type.
     *
     * @param playerUuid The UUID of the player
     * @param permission The permission to check
     * @return true if the player has permission
     */
    private boolean hasAlertPermission(UUID playerUuid, String permission) {
        if (playerUuid == null) {
            plugin.logDebug("[WebPanel] hasAlertPermission: playerUuid is null");
            return false;
        }

        // Check if player is online and has permission
        Player player = Bukkit.getPlayer(playerUuid);
        if (player != null && player.isOnline()) {
            // Staff permission is master permission
            if (!PermissionUtil.hasPermission(player, "moderex.staff")) {
                plugin.logDebug("[WebPanel] hasAlertPermission: " + player.getName() + " lacks moderex.staff");
                return false;
            }
            // Check for all-alerts permission
            if (PermissionUtil.hasPermission(player, "moderex.alerts.*")) {
                return true;
            }
            // Check specific permission
            boolean hasPerm = PermissionUtil.hasPermission(player, permission);
            plugin.logDebug("[WebPanel] hasAlertPermission: " + player.getName() + " " + permission + " = " + hasPerm);
            return hasPerm;
        }

        // For offline players, use LuckPerms if available
        if (plugin.getHookManager().isLuckPermsEnabled()) {
            var lpHook = plugin.getHookManager().getLuckPermsHook();
            // Check staff permission first
            if (!lpHook.hasPermission(playerUuid, "moderex.staff")) {
                plugin.logDebug("[WebPanel] hasAlertPermission (LP): " + playerUuid + " lacks moderex.staff");
                return false;
            }
            // Check for all-alerts permission
            if (lpHook.hasPermission(playerUuid, "moderex.alerts.*")) {
                plugin.logDebug("[WebPanel] hasAlertPermission (LP): " + playerUuid + " has moderex.alerts.*");
                return true;
            }
            // Check specific permission
            boolean hasPerm = lpHook.hasPermission(playerUuid, permission);
            plugin.logDebug("[WebPanel] hasAlertPermission (LP): " + playerUuid + " " + permission + " = " + hasPerm);
            return hasPerm;
        }

        // Fallback: If they authenticated to the web panel, they have moderex.webpanel
        // Grant alert permissions to authenticated users without LuckPerms
        plugin.logDebug("[WebPanel] hasAlertPermission: Granting " + permission + " to authenticated user " + playerUuid + " (no LP)");
        return true;
    }

    public void broadcastVanishUpdate(String playerName, boolean vanished) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "VANISH_UPDATE");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("vanished", vanished);
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastKickAll(String staffName, int count, String reason) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "KICK_ALL");
        JsonObject data = new JsonObject();
        data.addProperty("staffName", staffName);
        data.addProperty("count", count);
        data.addProperty("reason", reason);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    /**
     * Disconnect a player's web panel session.
     * Used when token is revoked for security.
     *
     * @param playerUuid The player's UUID
     * @param code The disconnect code
     * @param message The disconnect message
     */
    public void disconnectPlayer(UUID playerUuid, String code, String message) {
        // Find and disconnect all connections for this player
        for (WebSocketConnection conn : new ArrayList<>(connections)) {
            WebPanelSession session = sessions.get(conn);
            if (session != null && session.playerUuid.equals(playerUuid)) {
                // Send disconnect message
                JsonObject response = new JsonObject();
                response.addProperty("type", "FORCED_DISCONNECT");
                JsonObject data = new JsonObject();
                data.addProperty("code", code);
                data.addProperty("message", message);
                response.add("data", data);
                conn.send(GSON.toJson(response));

                // Close connection
                connections.remove(conn);
                sessions.remove(conn);
                conn.close();

                plugin.logDebug("[WebPanel] Force disconnected session for " + session.playerName + " (" + code + ")");
                debugWarning(DebugCategory.AUTH, "Session force disconnected",
                        "Player: " + session.playerName + ", Reason: " + code);
            }
        }

        // Also check same-port connections
        for (Map.Entry<String, WebPanelSession> entry : new ArrayList<>(samePortSessions.entrySet())) {
            if (entry.getValue().playerUuid.equals(playerUuid)) {
                String connId = entry.getKey();
                WebSocketFrameHandler handler = samePortConnections.get(connId);

                if (handler != null) {
                    // Send disconnect message
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "FORCED_DISCONNECT");
                    JsonObject data = new JsonObject();
                    data.addProperty("code", code);
                    data.addProperty("message", message);
                    response.add("data", data);
                    handler.send(GSON.toJson(response));
                    handler.close();
                }

                samePortSessions.remove(connId);
                samePortConnections.remove(connId);

                plugin.logDebug("[WebPanel] Force disconnected same-port session for " + entry.getValue().playerName);
            }
        }
    }

    private void broadcast(String message) {
        // Run broadcasts on dedicated single-thread executor to prevent thread explosion
        // Using a single thread ensures broadcasts are processed sequentially, avoiding thread buildup
        if (broadcastExecutor != null && !broadcastExecutor.isShutdown()) {
            broadcastExecutor.execute(() -> {
                for (WebSocketConnection conn : connections) {
                    try {
                        if (sessions.containsKey(conn)) {
                            if (!conn.sendAsync(message)) {
                                // Connection failed - remove it
                                connections.remove(conn);
                                sessions.remove(conn);
                                conn.close();
                            }
                        }
                    } catch (Exception e) {
                        // Handle any errors during send to prevent broadcast thread from dying
                        connections.remove(conn);
                        sessions.remove(conn);
                        try { conn.close(); } catch (Exception ignored) {}
                    }
                }
            });
        }
    }

    // ==================== Event Broadcasting ====================

    public void broadcastChatMessage(String playerName, UUID playerUuid, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT_MESSAGE");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastPrivateMessage(String senderName, UUID senderUuid, String targetName, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PRIVATE_MESSAGE");
        JsonObject data = new JsonObject();
        data.addProperty("senderName", senderName);
        data.addProperty("senderUuid", senderUuid.toString());
        data.addProperty("targetName", targetName);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastCommand(String playerName, UUID playerUuid, String command) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "COMMAND_EXECUTED");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("command", command);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAutomodTrigger(String playerName, UUID playerUuid, String ruleName, String message, String action) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_TRIGGER");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("rule", ruleName);
        data.addProperty("message", message);
        data.addProperty("action", action);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAutomodAlert(String playerName, UUID playerUuid, String ruleName, String triggeredMessage) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("rule", ruleName);
        data.addProperty("message", triggeredMessage);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastAnticheatAlert(String playerName, UUID playerUuid, String checkName, int violationLevel, String details) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ANTICHEAT_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("check", checkName);
        data.addProperty("vl", violationLevel);
        data.addProperty("details", details);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastPlayerJoin(String playerName, UUID playerUuid, boolean isGeyser) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_JOIN");
        JsonObject data = new JsonObject();
        data.addProperty("name", playerName);
        data.addProperty("uuid", playerUuid.toString());
        data.addProperty("geyser", isGeyser);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    public void broadcastPlayerQuit(String playerName, UUID playerUuid) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_QUIT");
        JsonObject data = new JsonObject();
        data.addProperty("name", playerName);
        data.addProperty("uuid", playerUuid.toString());
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    // ==================== Server Status ====================

    private void sendServerStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SERVER_STATUS");

        var statusManager = plugin.getServerStatusManager();
        if (statusManager != null) {
            response.add("data", statusManager.getStatusJson());
        } else {
            JsonObject data = new JsonObject();
            data.addProperty("error", "Server status monitoring is not enabled");
            response.add("data", data);
        }

        conn.send(GSON.toJson(response));
    }

    public void broadcastServerStatus(JsonObject statusData) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "SERVER_STATUS");
        json.add("data", statusData);
        broadcast(GSON.toJson(json));
    }

    // ==================== Integration Status ====================

    private void sendLuckPermsStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "LUCKPERMS_STATUS");

        JsonObject data = new JsonObject();
        boolean available = plugin.getHookManager() != null && plugin.getHookManager().isLuckPermsAvailable();
        data.addProperty("available", available);

        if (available) {
            data.addProperty("version", plugin.getHookManager().getLuckPermsVersion());
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendGeyserStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "GEYSER_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean geyserAvailable = hookManager != null && hookManager.isGeyserAvailable();
        boolean floodgateAvailable = hookManager != null && hookManager.isFloodgateAvailable();
        boolean citizensAvailable = hookManager != null && hookManager.hasCitizens();

        data.addProperty("geyserAvailable", geyserAvailable);
        data.addProperty("floodgateAvailable", floodgateAvailable);
        data.addProperty("citizensAvailable", citizensAvailable);

        if (geyserAvailable) {
            data.addProperty("geyserVersion", hookManager.getGeyserVersion());
        }

        if (floodgateAvailable) {
            data.addProperty("floodgateVersion", hookManager.getFloodgateVersion());
        }

        if (citizensAvailable) {
            data.addProperty("citizensVersion", hookManager.getCitizensVersion());
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendModerationPlugins(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "MODERATION_PLUGINS");

        JsonObject data = new JsonObject();
        JsonArray plugins = new JsonArray();

        // Check for common moderation plugins
        var hookManager = plugin.getModerationHookManager();
        if (hookManager != null) {
            for (var hook : hookManager.getDetectedPlugins()) {
                JsonObject pluginObj = new JsonObject();
                pluginObj.addProperty("name", hook.getName());
                pluginObj.addProperty("punishmentCount", hook.getPunishmentCount());
                plugins.add(pluginObj);
            }
        }

        data.add("plugins", plugins);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    public void broadcastRulesUpdate(java.util.List<com.blockforge.moderex.rules.Rule> rules) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "RULES_UPDATE");
        JsonArray rulesArray = new JsonArray();
        for (var rule : rules) {
            JsonObject ruleObj = new JsonObject();
            ruleObj.addProperty("id", rule.getId());
            ruleObj.addProperty("order", rule.getOrder());
            ruleObj.addProperty("title", rule.getTitle());
            ruleObj.addProperty("description", rule.getDescription());
            ruleObj.addProperty("category", rule.getCategory());
            ruleObj.addProperty("enabled", rule.isEnabled());
            rulesArray.add(ruleObj);
        }
        json.add("rules", rulesArray);
        broadcast(GSON.toJson(json));
    }

    public void broadcastLogEvent(String severity, String category, String title, String detail) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOG_EVENT");
        JsonObject data = new JsonObject();
        data.addProperty("severity", severity);
        data.addProperty("category", category);
        data.addProperty("title", title);
        data.addProperty("detail", detail);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    // ==================== User Settings ====================

    private UserPanelSettings getUserSettings(UUID uuid) {
        return userSettings.computeIfAbsent(uuid, k -> new UserPanelSettings());
    }

    private void loadUserSettings() {
        try {
            plugin.getDatabaseManager().query("SELECT * FROM moderex_webpanel_settings", rs -> {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    UserPanelSettings settings = new UserPanelSettings();

                    // Load legacy fields
                    settings.chatAlerts = rs.getBoolean("chat_alerts");
                    settings.soundEnabled = rs.getBoolean("sound_enabled");
                    settings.watchlistToasts = rs.getBoolean("watchlist_toasts");

                    try {
                        settings.punishmentAlerts = rs.getBoolean("punishment_alerts");
                        settings.automodAlerts = rs.getBoolean("automod_alerts");
                        settings.anticheatAlerts = rs.getBoolean("anticheat_alerts");
                        settings.staffChatAlerts = rs.getBoolean("staffchat_notifications");
                        settings.compactMode = rs.getBoolean("compact_mode");

                        // Load JSON settings for alert bar and other extended settings
                        String settingsJson = rs.getString("settings_json");
                        if (settingsJson != null && !settingsJson.isEmpty()) {
                            JsonObject json = GSON.fromJson(settingsJson, JsonObject.class);
                            settings.fromJson(json);
                        }
                    } catch (SQLException ignored) {
                        // Columns may not exist in older databases
                    }

                    userSettings.put(uuid, settings);
                }
                return null;
            });
        } catch (SQLException ignored) {}
    }

    private void saveUserSettings() {
        for (Map.Entry<UUID, UserPanelSettings> entry : userSettings.entrySet()) {
            saveUserSettingsForUuid(entry.getKey(), entry.getValue());
        }
    }

    private void saveUserSettingsForUuid(UUID uuid, UserPanelSettings s) {
        try {
            plugin.getDatabaseManager().update(
                """
                INSERT OR REPLACE INTO moderex_webpanel_settings
                (uuid, chat_alerts, sound_enabled, watchlist_toasts, staffchat_notifications,
                 punishment_alerts, automod_alerts, anticheat_alerts, compact_mode, settings_json, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                uuid.toString(),
                s.chatAlerts,
                s.soundEnabled,
                s.watchlistToasts,
                s.staffChatAlerts,
                s.punishmentAlerts,
                s.automodAlerts,
                s.anticheatAlerts,
                s.compactMode,
                GSON.toJson(s.toJson()),
                System.currentTimeMillis()
            );
        } catch (SQLException ignored) {}
    }

    public void updateUserSettings(UUID uuid, JsonObject settingsJson) {
        UserPanelSettings settings = getUserSettings(uuid);
        settings.fromJson(settingsJson);
        userSettings.put(uuid, settings);
        saveUserSettingsForUuid(uuid, settings);
    }

    public JsonObject getUserSettingsJson(UUID uuid) {
        return getUserSettings(uuid).toJson();
    }

    // ==================== Panel Directory Setup ====================

    private void setupPanelDirectory() {
        // Panel files are served directly from JAR, no extraction needed
        // This keeps the plugin folder clean and prevents users from accidentally
        // modifying panel files that will be overwritten on updates

        // Only create directory structure if needed for customization
        if (!serveFromJar) {
            try {
                if (!Files.exists(panelDirectory)) {
                    Files.createDirectories(panelDirectory);
                }
                Files.createDirectories(panelDirectory.resolve("js"));
                Files.createDirectories(panelDirectory.resolve("css"));

                Path indexPath = panelDirectory.resolve("index.html");
                if (!Files.exists(indexPath)) {
                    extractResource("panel/index.html", indexPath);
                    extractResource("panel/css/styles.css", panelDirectory.resolve("css/styles.css"));
                    extractResource("panel/js/utils.js", panelDirectory.resolve("js/utils.js"));
                    extractResource("panel/js/state.js", panelDirectory.resolve("js/state.js"));
                    extractResource("panel/js/websocket.js", panelDirectory.resolve("js/websocket.js"));
                    extractResource("panel/js/auth.js", panelDirectory.resolve("js/auth.js"));
                    extractResource("panel/js/ui.js", panelDirectory.resolve("js/ui.js"));
                    extractResource("panel/js/sounds.js", panelDirectory.resolve("js/sounds.js"));
                    extractResource("panel/js/app.js", panelDirectory.resolve("js/app.js"));
                    extractResource("panel/537154108207028818e303ef9465c1f66717660d_96.png",
                        panelDirectory.resolve("537154108207028818e303ef9465c1f66717660d_96.png"));
                    plugin.getLogger().info("Extracted panel files to hidden directory");
                }
            } catch (IOException e) {
                plugin.logError("Failed to setup panel directory", e);
            }
        }
    }

    private void extractResource(String resourcePath, Path targetPath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                Files.createDirectories(targetPath.getParent());
                Files.copy(in, targetPath);
            }
        } catch (IOException ignored) {}
    }

    // ==================== Debug Helper Methods ====================

    private WebPanelDebugger getDebugger() {
        return plugin.getWebPanelDebugger();
    }

    private void debugSuccess(DebugCategory category, String title, String detail) {
        WebPanelDebugger debugger = getDebugger();
        if (debugger != null) {
            debugger.success(category, title, detail);
        }
    }

    private void debugError(ErrorCode errorCode, String additionalDetail) {
        WebPanelDebugger debugger = getDebugger();
        if (debugger != null) {
            debugger.error(errorCode, additionalDetail);
        }
    }

    private void debugWarning(DebugCategory category, String title, String detail) {
        WebPanelDebugger debugger = getDebugger();
        if (debugger != null) {
            debugger.warning(category, title, detail);
        }
    }

    private void debugInfo(DebugCategory category, String title, String detail) {
        WebPanelDebugger debugger = getDebugger();
        if (debugger != null) {
            debugger.info(category, title, detail);
        }
    }

    // ==================== Inner Classes ====================

    private static class PendingConnection {
        UUID playerUuid;
        String playerName;
        long createdAt;
        boolean hasPermission;
        String prefix;
        String suffix;
    }

    private static class WebPanelSession {
        UUID playerUuid;
        String playerName;
        String authMethod;
        String authSessionId;
        boolean hasPermission;
        String prefix;
        String suffix;
        long connectedAt;
        long lastActivity;
    }

    private static class UserPanelSettings {
        // Alert visibility settings
        boolean chatAlerts = true;
        boolean soundEnabled = true;
        boolean watchlistToasts = true;
        boolean punishmentAlerts = true;
        boolean automodAlerts = true;
        boolean anticheatAlerts = true;
        boolean staffChatAlerts = true;
        boolean joinQuitAlerts = true;

        // Alert bar settings
        boolean alertBarEnabled = true;
        int alertBarDuration = 5000; // milliseconds
        boolean alertBarPunishments = true;
        boolean alertBarAutomod = false;
        boolean alertBarAnticheat = true;
        boolean alertBarWatchlist = true;
        boolean alertBarStaffChat = false;

        // Display preferences
        boolean compactMode = false;
        String theme = "dark"; // dark, light, system
        String themeColor = "#2d7aed"; // Custom theme color (hex)
        String backgroundPattern = "aurora"; // Background pattern name

        // Notification preferences
        boolean watchlistAlerts = true; // Show watchlist alerts at bottom of panel

        // Security settings
        boolean deviceTrustEnabled = true; // Allow trusted devices to auto-login

        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("chatAlerts", chatAlerts);
            json.addProperty("soundEnabled", soundEnabled);
            json.addProperty("watchlistToasts", watchlistToasts);
            json.addProperty("punishmentAlerts", punishmentAlerts);
            json.addProperty("automodAlerts", automodAlerts);
            json.addProperty("anticheatAlerts", anticheatAlerts);
            json.addProperty("staffChatAlerts", staffChatAlerts);
            json.addProperty("joinQuitAlerts", joinQuitAlerts);

            // Alert bar settings
            JsonObject alertBar = new JsonObject();
            alertBar.addProperty("enabled", alertBarEnabled);
            alertBar.addProperty("duration", alertBarDuration);
            alertBar.addProperty("punishments", alertBarPunishments);
            alertBar.addProperty("automod", alertBarAutomod);
            alertBar.addProperty("anticheat", alertBarAnticheat);
            alertBar.addProperty("watchlist", alertBarWatchlist);
            alertBar.addProperty("staffChat", alertBarStaffChat);
            json.add("alertBar", alertBar);

            json.addProperty("compactMode", compactMode);
            json.addProperty("theme", theme);
            json.addProperty("themeColor", themeColor);
            json.addProperty("backgroundPattern", backgroundPattern);
            json.addProperty("watchlistAlerts", watchlistAlerts);
            json.addProperty("deviceTrustEnabled", deviceTrustEnabled);
            return json;
        }

        void fromJson(JsonObject json) {
            if (json.has("chatAlerts")) chatAlerts = json.get("chatAlerts").getAsBoolean();
            if (json.has("soundEnabled")) soundEnabled = json.get("soundEnabled").getAsBoolean();
            if (json.has("watchlistToasts")) watchlistToasts = json.get("watchlistToasts").getAsBoolean();
            if (json.has("punishmentAlerts")) punishmentAlerts = json.get("punishmentAlerts").getAsBoolean();
            if (json.has("automodAlerts")) automodAlerts = json.get("automodAlerts").getAsBoolean();
            if (json.has("anticheatAlerts")) anticheatAlerts = json.get("anticheatAlerts").getAsBoolean();
            if (json.has("staffChatAlerts")) staffChatAlerts = json.get("staffChatAlerts").getAsBoolean();
            if (json.has("joinQuitAlerts")) joinQuitAlerts = json.get("joinQuitAlerts").getAsBoolean();

            if (json.has("alertBar")) {
                JsonObject alertBar = json.getAsJsonObject("alertBar");
                if (alertBar.has("enabled")) alertBarEnabled = alertBar.get("enabled").getAsBoolean();
                if (alertBar.has("duration")) alertBarDuration = alertBar.get("duration").getAsInt();
                if (alertBar.has("punishments")) alertBarPunishments = alertBar.get("punishments").getAsBoolean();
                if (alertBar.has("automod")) alertBarAutomod = alertBar.get("automod").getAsBoolean();
                if (alertBar.has("anticheat")) alertBarAnticheat = alertBar.get("anticheat").getAsBoolean();
                if (alertBar.has("watchlist")) alertBarWatchlist = alertBar.get("watchlist").getAsBoolean();
                if (alertBar.has("staffChat")) alertBarStaffChat = alertBar.get("staffChat").getAsBoolean();
            }

            if (json.has("compactMode")) compactMode = json.get("compactMode").getAsBoolean();
            if (json.has("theme")) theme = json.get("theme").getAsString();
            if (json.has("themeColor")) themeColor = json.get("themeColor").getAsString();
            if (json.has("backgroundPattern")) backgroundPattern = json.get("backgroundPattern").getAsString();
            if (json.has("watchlistAlerts")) watchlistAlerts = json.get("watchlistAlerts").getAsBoolean();
            if (json.has("deviceTrustEnabled")) deviceTrustEnabled = json.get("deviceTrustEnabled").getAsBoolean();
        }
    }

    private static class WebSocketConnection {
        private final Socket socket;
        private final InputStream in;
        private final OutputStream out;

        WebSocketConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        }

        // Protected constructor for wrappers (same-port connections)
        protected WebSocketConnection() {
            this.socket = null;
            this.in = null;
            this.out = null;
        }

        String readMessage() throws IOException {
            int firstByte = in.read();
            if (firstByte == -1) return null;

            int opcode = firstByte & 0x0F;
            // boolean fin = (firstByte & 0x80) != 0;

            int secondByte = in.read();
            if (secondByte == -1) return null;

            boolean masked = (secondByte & 0x80) != 0;
            int len = secondByte & 0x7F;

            if (len == 126) {
                len = (in.read() << 8) | in.read();
            } else if (len == 127) {
                // Read 8-byte length (we cap at reasonable size)
                long longLen = 0;
                for (int i = 0; i < 8; i++) {
                    longLen = (longLen << 8) | in.read();
                }
                len = (int) Math.min(longLen, 1048576); // Cap at 1MB
            }

            byte[] maskKey = null;
            if (masked) {
                maskKey = new byte[4];
                in.read(maskKey);
            }

            byte[] data = new byte[len];
            int read = 0;
            while (read < len) {
                int r = in.read(data, read, len - read);
                if (r == -1) break;
                read += r;
            }

            if (masked && maskKey != null) {
                for (int i = 0; i < data.length; i++) {
                    data[i] ^= maskKey[i % 4];
                }
            }

            // Handle control frames
            if (opcode == 8) { // Close frame
                return null;
            }
            if (opcode == 9) { // Ping - respond with pong
                sendPong(data);
                return "{\"type\":\"PONG\"}"; // Return a pong message for the handler
            }
            if (opcode == 10) { // Pong - ignore
                return "{\"type\":\"PONG\"}";
            }

            return new String(data, StandardCharsets.UTF_8);
        }

        void sendPong(byte[] data) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(0x8A); // Pong frame, FIN bit set
                if (data.length < 126) {
                    baos.write(data.length);
                } else {
                    baos.write(126);
                    baos.write((data.length >> 8) & 0xFF);
                    baos.write(data.length & 0xFF);
                }
                baos.write(data);
                synchronized (out) {
                    out.write(baos.toByteArray());
                    out.flush();
                }
            } catch (IOException ignored) {}
        }

        void send(String message) {
            sendAsync(message);
        }

        /**
         * Send a message with timeout protection. Returns true if successful, false if failed.
         */
        boolean sendAsync(String message) {
            try {
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                baos.write(0x81); // Text frame, FIN bit set

                if (data.length < 126) {
                    baos.write(data.length);
                } else if (data.length < 65536) {
                    baos.write(126);
                    baos.write((data.length >> 8) & 0xFF);
                    baos.write(data.length & 0xFF);
                } else {
                    baos.write(127);
                    for (int i = 7; i >= 0; i--) {
                        baos.write((data.length >> (8 * i)) & 0xFF);
                    }
                }

                baos.write(data);

                // Set a write timeout to prevent blocking forever
                int originalTimeout = socket.getSoTimeout();
                try {
                    socket.setSoTimeout(15000); // 15 second timeout for writes (increased for slow connections)
                    synchronized (out) {
                        out.write(baos.toByteArray());
                        out.flush();
                    }
                    return true;
                } finally {
                    socket.setSoTimeout(originalTimeout);
                }
            } catch (IOException e) {
                return false; // Connection is dead
            }
        }

        void close() {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }

        String getRemoteAddress() {
            try {
                if (socket != null && socket.getRemoteSocketAddress() instanceof java.net.InetSocketAddress addr) {
                    return addr.getAddress().getHostAddress();
                }
            } catch (Exception ignored) {}
            return null;
        }
    }

    // ==================== Same-Port (Netty) WebSocket Support ====================

    /**
     * Register a same-port WebSocket connection from the Netty handler.
     */
    public void registerSamePortConnection(String connectionId, WebSocketFrameHandler handler) {
        samePortConnections.put(connectionId, handler);
        plugin.logDebug("[SamePort] Registered connection: " + connectionId);
    }

    /**
     * Unregister a same-port WebSocket connection.
     */
    public void unregisterSamePortConnection(String connectionId) {
        samePortConnections.remove(connectionId);
        WebPanelSession session = samePortSessions.remove(connectionId);
        if (session != null) {
            plugin.logDebug("[SamePort] Disconnected: " + session.playerName);
        }
    }

    /**
     * Handle a message from a same-port WebSocket connection.
     */
    public void handleSamePortMessage(String connectionId, String message) {
        WebSocketFrameHandler handler = samePortConnections.get(connectionId);
        if (handler == null) return;

        try {
            JsonObject json = GSON.fromJson(message, JsonObject.class);
            if (json == null || !json.has("type")) return;

            String type = json.get("type").getAsString();
            JsonObject data = json.has("data") ? json.getAsJsonObject("data") : new JsonObject();

            // Handle authentication
            if ("AUTH".equals(type)) {
                handleSamePortAuth(connectionId, handler, data);
                return;
            }

            // Allow GET_SERVER_STATUS before authentication (for connection status display)
            if ("GET_SERVER_STATUS".equals(type)) {
                SamePortConnectionWrapper wrapper = new SamePortConnectionWrapper(handler);
                sendServerStatus(wrapper);
                return;
            }

            // Handle PING/PONG without authentication
            if ("PING".equals(type)) {
                JsonObject pong = new JsonObject();
                pong.addProperty("type", "PONG");
                pong.addProperty("timestamp", System.currentTimeMillis());
                handler.send(GSON.toJson(pong));
                return;
            }
            if ("PONG".equals(type) || "HEARTBEAT".equals(type)) {
                return; // Just ignore, keep connection alive
            }

            // Check if authenticated
            WebPanelSession session = samePortSessions.get(connectionId);
            if (session == null) {
                sendToSamePort(handler, createError("NOT_AUTHENTICATED", "Please authenticate first"));
                return;
            }

            // Update activity
            session.lastActivity = System.currentTimeMillis();

            // Route message to appropriate handler
            handleSamePortRequest(type, data, session, handler);

        } catch (Exception e) {
            plugin.logDebug("[SamePort] Error handling message: " + e.getMessage());
        }
    }

    private void handleSamePortAuth(String connectionId, WebSocketFrameHandler handler, JsonObject data) {
        String code = data.has("code") ? data.get("code").getAsString() : null;

        if (code == null || code.isEmpty()) {
            sendToSamePort(handler, createError("INVALID_CODE", "No authentication code provided"));
            return;
        }

        PendingConnection pending = pendingCodes.remove(code);
        if (pending == null) {
            sendToSamePort(handler, createError("INVALID_CODE", "Invalid or expired code"));
            return;
        }

        // Create session
        WebPanelSession session = new WebPanelSession();
        session.playerUuid = pending.playerUuid;
        session.playerName = pending.playerName;
        session.authMethod = "code";
        session.authSessionId = UUID.randomUUID().toString();
        session.hasPermission = pending.hasPermission;
        session.prefix = pending.prefix;
        session.suffix = pending.suffix;
        session.connectedAt = System.currentTimeMillis();
        session.lastActivity = System.currentTimeMillis();

        samePortSessions.put(connectionId, session);

        // Send success response
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTH_SUCCESS");
        JsonObject authData = new JsonObject();
        authData.addProperty("playerName", session.playerName);
        authData.addProperty("uuid", session.playerUuid.toString());
        authData.addProperty("sessionId", session.authSessionId);
        authData.addProperty("prefix", session.prefix != null ? session.prefix : "");
        authData.addProperty("suffix", session.suffix != null ? session.suffix : "");
        response.add("data", authData);
        sendToSamePort(handler, GSON.toJson(response));

        plugin.getLogger().info("[SamePort] Authenticated: " + session.playerName);
    }

    private void handleSamePortRequest(String type, JsonObject data, WebPanelSession session, WebSocketFrameHandler handler) {
        // Create a wrapper to send responses
        SamePortConnectionWrapper wrapper = new SamePortConnectionWrapper(handler);

        try {
        // Reuse existing handlers by wrapping the connection
        switch (type) {
            case "GET_PLAYERS" -> sendPlayerList(wrapper);
            case "GET_PLAYER_DETAILS" -> sendPlayerDetails(wrapper, data);
            case "GET_PUNISHMENTS" -> sendPunishments(wrapper, data);
            case "GET_COMMAND_HISTORY" -> sendCommandHistory(wrapper, data);
            case "GET_CHAT_LOGS" -> sendChatLogs(wrapper, data);
            case "GET_AUTOMOD_LOGS" -> sendAutomodLogs(wrapper, data);
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(wrapper);
            case "GET_USER_SETTINGS" -> sendUserSettingsForSamePort(wrapper, session);
            case "GET_TEMPLATES" -> sendTemplates(wrapper);
            case "GET_STATS" -> sendStats(wrapper);
            case "GET_CHAT_STATUS" -> sendChatStatus(wrapper);
            case "GET_SERVER_STATUS" -> sendServerStatus(wrapper);
            case "GET_LUCKPERMS_STATUS" -> sendLuckPermsStatus(wrapper);
            case "GET_GEYSER_STATUS" -> sendGeyserStatus(wrapper);
            case "GET_MODERATION_PLUGINS" -> sendModerationPlugins(wrapper);
            case "GET_SERVER_SETTINGS" -> sendServerSettings(wrapper);
            case "GET_DEV_CHECKLIST" -> sendDevChecklist(wrapper);
            case "GET_WATCHLIST" -> sendWatchlist(wrapper);
            case "GET_ANTICHEAT_INFO" -> sendAnticheatInfo(wrapper);
            case "GET_ANTICHEAT_ALERTS" -> sendAnticheatAlerts(wrapper);
            case "GET_ANTICHEAT_CHECKS" -> sendAnticheatChecks(wrapper);
            case "GET_ALERT_PRESETS" -> sendAlertPresets(wrapper);
            case "SEND_STAFFCHAT", "STAFFCHAT_MESSAGE" -> {
                String msg = data.has("message") ? data.get("message").getAsString() : "";
                plugin.getStaffChatManager().broadcastFromWebPanel(session.playerName, msg);
                sendSuccess(wrapper, "Message sent");
            }
            case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(wrapper, data, session);
            case "CREATE_AUTOMOD_RULE" -> createAutomodRule(wrapper, data, session);
            case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(wrapper, data, session);
            case "CREATE_PUNISHMENT" -> createPunishment(wrapper, data, session);
            case "REVOKE_PUNISHMENT" -> revokePunishment(wrapper, data, session);
            case "ADD_TO_WATCHLIST" -> addToWatchlist(wrapper, data, session);
            case "REMOVE_FROM_WATCHLIST" -> removeFromWatchlist(wrapper, data);
            case "DELETE_TEMPLATE" -> deleteTemplate(wrapper, data, session);
            case "UPDATE_USER_SETTINGS" -> updateUserSettings(wrapper, data, session);
            case "MARK_CHANGELOG_READ" -> markChangelogRead(wrapper, data, session);
            case "SET_CHAT_LOCK" -> setChatLock(wrapper, data, session);
            case "SET_SLOWMODE" -> setSlowmode(wrapper, data, session);
            case "DEV_STRESS_CREATE_PLAYERS" -> handleDevStressCreatePlayers(wrapper, data);
            case "DEV_STRESS_CREATE_PUNISHMENTS" -> handleDevStressCreatePunishments(wrapper, data);
            case "DEV_STRESS_CLEANUP" -> handleDevStressCleanup(wrapper);
            case "DEV_STRESS_STOP" -> handleDevStressStop(wrapper);
            default -> sendError(wrapper, "UNKNOWN_TYPE", "Unknown message type: " + type);
        }
        } catch (Exception e) {
            // Catch all exceptions to prevent them from propagating and closing the connection
            plugin.logError("[SamePort] Error handling request type " + type + ": " + e.getMessage(), e);
            try {
                sendError(wrapper, "INTERNAL_ERROR", "An error occurred: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }

    private void sendUserSettingsForSamePort(SamePortConnectionWrapper wrapper, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "USER_SETTINGS_DATA");
        JsonObject data = getUserSettings(session.playerUuid).toJson();

        // Include in-game staff settings
        var staffSettings = plugin.getStaffSettingsManager().getSettings(session.playerUuid);
        if (staffSettings != null) {
            data.addProperty("staffChatEnabled", staffSettings.isStaffChatEnabled());
            data.addProperty("staffChatSound", staffSettings.isStaffChatSound());
            data.addProperty("watchlistJoinAlerts", staffSettings.isWatchlistJoinAlerts());
            data.addProperty("watchlistQuitAlerts", staffSettings.isWatchlistQuitAlerts());
            data.addProperty("watchlistActivityAlerts", staffSettings.isWatchlistActivityAlerts());
            data.addProperty("autoVanishOnJoin", staffSettings.isAutoVanishOnJoin());
            data.addProperty("vanishNightVision", staffSettings.isVanishNightVision());
            data.addProperty("compactMode", staffSettings.isCompactMode());
            data.addProperty("inGameSoundEnabled", staffSettings.isSoundEnabled());
            data.addProperty("actionBarAlerts", staffSettings.isActionBarAlerts());
            data.addProperty("inGameChatAlerts", staffSettings.isChatAlerts());
            data.addProperty("bossBarAlerts", staffSettings.isBossBarAlerts());

            // Punishment alert levels
            data.addProperty("banAlerts", staffSettings.getBanAlerts().name().toLowerCase());
            data.addProperty("kickAlerts", staffSettings.getKickAlerts().name().toLowerCase());
            data.addProperty("muteAlerts", staffSettings.getMuteAlerts().name().toLowerCase());
            data.addProperty("warnAlerts", staffSettings.getWarnAlerts().name().toLowerCase());
            data.addProperty("pardonAlerts", staffSettings.getPardonAlerts().name().toLowerCase());

            // Other alert types
            data.addProperty("automodAlerts", staffSettings.getAutomodAlerts().name().toLowerCase());
            data.addProperty("anticheatAlerts", staffSettings.getAnticheatAlerts().name().toLowerCase());
            data.addProperty("anticheatMinVL", staffSettings.getAnticheatMinVL());
            data.addProperty("nicknameAlerts", staffSettings.getNicknameAlerts().name().toLowerCase());
            data.addProperty("commandAlerts", staffSettings.getCommandAlerts().name().toLowerCase());
            data.addProperty("joinLeaveAlerts", staffSettings.getJoinLeaveAlerts().name().toLowerCase());
            data.addProperty("lagAlerts", staffSettings.isLagAlerts());

            // Web panel notification modes
            data.addProperty("webNotifyPunishments", staffSettings.getWebNotifyPunishments().name().toLowerCase());
            data.addProperty("webNotifyAutomod", staffSettings.getWebNotifyAutomod().name().toLowerCase());
            data.addProperty("webNotifyAnticheat", staffSettings.getWebNotifyAnticheat().name().toLowerCase());
            data.addProperty("webNotifyWatchlist", staffSettings.getWebNotifyWatchlist().name().toLowerCase());
            data.addProperty("webNotifyStaffChat", staffSettings.getWebNotifyStaffChat().name().toLowerCase());
            data.addProperty("webNotifyCommands", staffSettings.getWebNotifyCommands().name().toLowerCase());
            data.addProperty("webNotifyNickname", staffSettings.getWebNotifyNickname().name().toLowerCase());
            data.addProperty("webNotifyLag", staffSettings.getWebNotifyLag().name().toLowerCase());

            // Web panel display settings
            data.addProperty("webToastPosition", staffSettings.getWebToastPosition().getCssClass());
            data.addProperty("webAlertDurationSeconds", staffSettings.getWebAlertDurationSeconds());

            // Web panel sound settings
            data.addProperty("webSoundPunishments", staffSettings.isWebSoundPunishments());
            data.addProperty("webSoundAutomod", staffSettings.isWebSoundAutomod());
            data.addProperty("webSoundAnticheat", staffSettings.isWebSoundAnticheat());
            data.addProperty("webSoundWatchlist", staffSettings.isWebSoundWatchlist());
            data.addProperty("webSoundStaffChat", staffSettings.isWebSoundStaffChat());
            data.addProperty("webSoundCommands", staffSettings.isWebSoundCommands());
            data.addProperty("webSoundNickname", staffSettings.isWebSoundNickname());
            data.addProperty("webSoundLag", staffSettings.isWebSoundLag());
        }

        // Include read changelog builds
        JsonArray readChangelogs = getReadChangelogBuilds(session.playerUuid);
        data.add("readChangelogs", readChangelogs);

        // Include user permissions for alert type checks
        JsonArray permissions = getUserPermissions(session.playerUuid);
        data.add("permissions", permissions);
        plugin.logDebug("[WebPanel] User " + session.playerName + " permissions: " + permissions);

        response.add("data", data);
        wrapper.send(GSON.toJson(response));
        plugin.logDebug("[WebPanel] Sent user settings with " + readChangelogs.size() + " read changelogs to " + session.playerName);
    }

    private void sendToSamePort(WebSocketFrameHandler handler, String message) {
        handler.send(message);
    }

    private String createError(String code, String message) {
        JsonObject error = new JsonObject();
        error.addProperty("type", "ERROR");
        error.addProperty("code", code);
        error.addProperty("message", message);
        return GSON.toJson(error);
    }

    // ==================== Developer Checklist ====================

    private void sendDevChecklist(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "DEV_CHECKLIST");
        JsonArray items = new JsonArray();

        try {
            plugin.getDatabaseManager().query(
                "SELECT * FROM moderex_dev_checklist ORDER BY category, created_at",
                rs -> {
                    while (rs.next()) {
                        JsonObject item = new JsonObject();
                        item.addProperty("id", rs.getString("item_id"));
                        item.addProperty("category", rs.getString("category"));
                        item.addProperty("title", rs.getString("title"));
                        item.addProperty("description", rs.getString("description"));
                        item.addProperty("checked", rs.getBoolean("checked"));
                        item.addProperty("checkedBy", rs.getString("checked_by"));
                        item.addProperty("checkedAt", rs.getLong("checked_at"));
                        items.add(item);
                    }
                    return null;
                }
            );

            // If no items, add default checklist items
            if (items.isEmpty()) {
                addDefaultChecklistItems();
                sendDevChecklist(conn); // Recursively call to send the newly added items
                return;
            }
        } catch (java.sql.SQLException e) {
            plugin.logError("Failed to fetch dev checklist", e);
        }

        response.add("data", items);
        conn.send(GSON.toJson(response));
    }

    private void addDefaultChecklistItems() {
        String[][] defaults = {
            // Punishments
            {"Punishments", "mute-command", "Mute Command", "Test /mute creates mute and blocks chat"},
            {"Punishments", "ban-command", "Ban Command", "Test /ban creates ban and blocks join"},
            {"Punishments", "warn-command", "Warn Command", "Test /warn creates warning notification"},
            {"Punishments", "kick-command", "Kick Command", "Test /kick removes player with message"},
            {"Punishments", "ipban-command", "IP Ban Command", "Test /ipban blocks all accounts on IP"},
            {"Punishments", "unmute-unban", "Unmute/Unban", "Test punishment removal commands"},
            {"Punishments", "viewpunishment", "View Punishment", "Test /viewpunishment with --gui flag"},

            // Commands
            {"Commands", "history-cmd", "History Command", "Test /history with type filters and --gui flag"},
            {"Commands", "staffhistory-cmd", "Staff History", "Test /staffhistory mirrors /modlog functionality"},
            {"Commands", "modlog-cmd", "ModLog Command", "Test /modlog with --gui flag and type filters"},
            {"Commands", "cmdhistory-cmd", "Command History", "Test /cmdhistory pagination and formatting"},
            {"Commands", "check-cmd", "Check Command", "Test /check shows comprehensive info with buttons"},
            {"Commands", "list-commands", "List Commands", "Test /banlist, /mutelist, /warnlist pagination"},

            // Activity Logging
            {"Activity Log", "chat-logging", "Chat Logging", "Test chat messages logged to database"},
            {"Activity Log", "command-logging", "Command Logging", "Test commands logged (sensitive redacted)"},
            {"Activity Log", "session-logging", "Session Logging", "Test join/quit with IP tracking"},
            {"Activity Log", "staff-action-log", "Staff Action Log", "Test punishment/pardon logged as staff actions"},
            {"Activity Log", "automod-logging", "Automod Logging", "Test automod triggers logged to activity log"},

            // Staff Settings
            {"Staff Settings", "settings-gui", "Staff Settings GUI", "Test /mx settings opens and saves correctly"},
            {"Staff Settings", "alert-prefs", "Alert Preferences", "Test per-check alert configuration"},
            {"Staff Settings", "settings-sync", "Settings Sync", "Verify settings sync to web panel"},

            // Automod
            {"Automod", "spam-filter", "Spam Prevention", "Test rapid message blocking"},
            {"Automod", "caps-filter", "Caps Filter", "Test excessive CAPS conversion"},
            {"Automod", "word-filter", "Word Filter", "Test blacklisted word blocking"},
            {"Automod", "anticheat-rules", "Anticheat Rules", "Test auto-punishment at threshold"},

            // Web Panel
            {"Web Panel", "connection", "Panel Connection", "Test /mx connect code authentication"},
            {"Web Panel", "realtime", "Real-time Updates", "Verify punishments appear instantly"},
            {"Web Panel", "settings-sync", "Web Settings Sync", "Test bidirectional settings sync"},
            {"Web Panel", "player-drawer", "Player Drawer", "Test drawer shows IP history, commands, automod"},
            {"Web Panel", "chat-logs", "Chat Logs Modal", "Test chat logs modal with search/filter"},
            {"Web Panel", "automod-logs", "Automod Logs Modal", "Test automod logs modal with search/filter"},

            // Permissions
            {"Permissions", "ip-permission", "IP Permission", "Test moderex.check.ip hides IP info"},
            {"Permissions", "history-ip-perm", "History IP Perm", "Test moderex.history.ip for IP details"},

            // Database
            {"Database", "sqlite-verify", "SQLite Tables", "Verify all tables exist and have data"},
            {"Database", "mysql-verify", "MySQL Support", "Test MySQL connection if configured"},
            {"Database", "activity-log-table", "Activity Log Table", "Verify moderex_activity_log has entries"},

            // Performance
            {"Performance", "gui-responsive", "GUI Responsiveness", "Test GUIs with 1000+ records"},
            {"Performance", "memory-leaks", "Memory Leaks", "Check for leaks after extended use"}
        };

        long now = System.currentTimeMillis();
        for (String[] item : defaults) {
            try {
                plugin.getDatabaseManager().update(
                    "INSERT OR IGNORE INTO moderex_dev_checklist (item_id, category, title, description, created_at) VALUES (?, ?, ?, ?, ?)",
                    item[1], item[0], item[2], item[3], now
                );
            } catch (java.sql.SQLException e) {
                plugin.logDebug("Failed to add default checklist item: " + item[1]);
            }
        }
    }

    private void toggleChecklistItem(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String itemId = data.has("itemId") ? data.get("itemId").getAsString() : null;
        boolean checked = data.has("checked") && data.get("checked").getAsBoolean();

        if (itemId == null) {
            sendError(conn, "MISSING_PARAMETER", "Missing itemId");
            return;
        }

        try {
            plugin.getDatabaseManager().update(
                "UPDATE moderex_dev_checklist SET checked = ?, checked_by = ?, checked_at = ? WHERE item_id = ?",
                checked,
                checked ? session.playerUuid.toString() : null,
                checked ? System.currentTimeMillis() : null,
                itemId
            );

            // Send updated checklist
            sendDevChecklist(conn);
            plugin.logDebug("[Checklist] " + session.playerName + " " + (checked ? "checked" : "unchecked") + " item: " + itemId);
        } catch (java.sql.SQLException e) {
            plugin.logError("Failed to toggle checklist item", e);
            sendError(conn, "DATABASE_ERROR", "Failed to update checklist item");
        }
    }

    private void addChecklistItem(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String category = data.has("category") ? data.get("category").getAsString() : "Custom";
        String title = data.has("title") ? data.get("title").getAsString() : null;
        String description = data.has("description") ? data.get("description").getAsString() : "";

        if (title == null || title.trim().isEmpty()) {
            sendError(conn, "MISSING_PARAMETER", "Missing title");
            return;
        }

        String itemId = "custom-" + System.currentTimeMillis();
        try {
            plugin.getDatabaseManager().update(
                "INSERT INTO moderex_dev_checklist (item_id, category, title, description, created_at) VALUES (?, ?, ?, ?, ?)",
                itemId, category, title.trim(), description.trim(), System.currentTimeMillis()
            );

            sendDevChecklist(conn);
            plugin.logDebug("[Checklist] " + session.playerName + " added item: " + title);
        } catch (java.sql.SQLException e) {
            plugin.logError("Failed to add checklist item", e);
            sendError(conn, "DATABASE_ERROR", "Failed to add checklist item");
        }
    }

    private void deleteChecklistItem(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String itemId = data.has("itemId") ? data.get("itemId").getAsString() : null;

        if (itemId == null) {
            sendError(conn, "MISSING_PARAMETER", "Missing itemId");
            return;
        }

        try {
            plugin.getDatabaseManager().update(
                "DELETE FROM moderex_dev_checklist WHERE item_id = ?",
                itemId
            );

            sendDevChecklist(conn);
            plugin.logDebug("[Checklist] " + session.playerName + " deleted item: " + itemId);
        } catch (java.sql.SQLException e) {
            plugin.logError("Failed to delete checklist item", e);
            sendError(conn, "DATABASE_ERROR", "Failed to delete checklist item");
        }
    }

    private void triggerPluginUpdate(WebSocketConnection conn, WebPanelSession session) {
        plugin.logDebug("[Update] " + session.playerName + " triggered plugin update from web panel");

        // Run update check asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var updater = plugin.getGitHubAutoUpdater();
                if (updater == null) {
                    sendPluginUpdateResult(conn, false, "Auto-updater not available");
                    return;
                }

                // Force download update from GitHub
                String result = updater.forceUpdate();
                boolean success = result != null && result.contains("successfully");

                sendPluginUpdateResult(conn, success, result);

                if (success) {
                    plugin.getLogger().info("[Update] Plugin update downloaded via web panel by " + session.playerName);
                }
            } catch (Exception e) {
                plugin.logError("Failed to trigger plugin update", e);
                sendPluginUpdateResult(conn, false, "Update failed: " + e.getMessage());
            }
        });
    }

    private void sendPluginUpdateResult(WebSocketConnection conn, boolean success, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PLUGIN_UPDATE_RESULT");
        response.addProperty("success", success);
        response.addProperty("message", message);
        conn.send(GSON.toJson(response));
    }

    // ==================== Stress Test Handlers ====================

    private volatile boolean stressTestRunning = false;
    private volatile boolean stressTestCancelled = false;
    private volatile long stressTestStartTime = 0;

    private void handleDevStressCreatePlayers(WebSocketConnection conn, JsonObject data) {
        int count = data.has("count") ? data.get("count").getAsInt() : 100;
        count = Math.min(count, 100000); // Cap at 100k

        if (stressTestRunning) {
            sendStressError(conn, "players", "A stress test is already running");
            return;
        }

        stressTestRunning = true;
        stressTestCancelled = false;
        stressTestStartTime = System.currentTimeMillis();
        final int playerCount = count;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Calculate progress update frequency based on count
                int progressInterval = Math.max(10, playerCount / 100);

                for (int i = 0; i < playerCount && !stressTestCancelled; i++) {
                    UUID uuid = UUID.randomUUID();
                    String name = "StressTest_" + i;
                    String ip = "192.168.100." + (i % 256);

                    plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_players (uuid, username, ip_address, first_join, last_join)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET username = excluded.username
                        """,
                        uuid.toString(), name, ip, System.currentTimeMillis(), System.currentTimeMillis()
                    );

                    // Send progress at intervals
                    if (i % progressInterval == 0 || i == playerCount - 1) {
                        sendStressProgress(conn, "players", i + 1, playerCount);
                    }
                }

                long duration = System.currentTimeMillis() - stressTestStartTime;
                if (stressTestCancelled) {
                    sendStressComplete(conn, "players", 0, duration, "Player creation cancelled");
                } else {
                    sendStressComplete(conn, "players", playerCount, duration, "Created " + playerCount + " test players");
                }
            } catch (Exception e) {
                plugin.logError("Stress test failed", e);
                sendStressError(conn, "players", "Failed: " + e.getMessage());
            } finally {
                stressTestRunning = false;
            }
        });
    }

    private void handleDevStressCreatePunishments(WebSocketConnection conn, JsonObject data) {
        int count = data.has("count") ? data.get("count").getAsInt() : 100;
        count = Math.min(count, 100000); // Cap at 100k

        if (stressTestRunning) {
            sendStressError(conn, "punishments", "A stress test is already running");
            return;
        }

        stressTestRunning = true;
        stressTestCancelled = false;
        stressTestStartTime = System.currentTimeMillis();
        final int punishmentCount = count;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String[] types = {"BAN", "MUTE", "KICK", "WARN"};
                String[] reasons = {"Stress test punishment", "Testing", "Performance test", "Automated test"};
                Random random = new Random();

                // Calculate progress update frequency based on count
                int progressInterval = Math.max(10, punishmentCount / 100);

                for (int i = 0; i < punishmentCount && !stressTestCancelled; i++) {
                    UUID targetUuid = UUID.randomUUID();
                    String targetName = "StressTarget_" + i;
                    UUID staffUuid = UUID.randomUUID();
                    String type = types[random.nextInt(types.length)];
                    String reason = reasons[random.nextInt(reasons.length)];
                    long duration = type.equals("KICK") || type.equals("WARN") ? 0 : random.nextInt(86400000);
                    long timestamp = System.currentTimeMillis() - random.nextInt(86400000 * 30); // Random time in last 30 days

                    plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_punishments
                        (case_id, player_uuid, player_name, staff_uuid, staff_name, type, reason, duration, created_at, expires_at, active, revoked)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        "ST-" + System.currentTimeMillis() + "-" + i,
                        targetUuid.toString(), targetName,
                        staffUuid.toString(), "StressStaff",
                        type, reason, duration, timestamp,
                        duration > 0 ? timestamp + duration : 0,
                        false, false
                    );

                    if (i % progressInterval == 0 || i == punishmentCount - 1) {
                        sendStressProgress(conn, "punishments", i + 1, punishmentCount);
                    }
                }

                long duration = System.currentTimeMillis() - stressTestStartTime;
                if (stressTestCancelled) {
                    sendStressComplete(conn, "punishments", 0, duration, "Punishment creation cancelled");
                } else {
                    sendStressComplete(conn, "punishments", punishmentCount, duration, "Created " + punishmentCount + " test punishments");
                }
            } catch (Exception e) {
                plugin.logError("Stress test failed", e);
                sendStressError(conn, "punishments", "Failed: " + e.getMessage());
            } finally {
                stressTestRunning = false;
            }
        });
    }

    private void handleDevStressCleanup(WebSocketConnection conn) {
        if (stressTestRunning) {
            sendStressError(conn, "cleanup", "A stress test is running. Stop it first.");
            return;
        }

        stressTestRunning = true;
        stressTestCancelled = false;
        stressTestStartTime = System.currentTimeMillis();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                sendStressProgress(conn, "cleanup", 0, 3);

                // Delete stress test players
                plugin.getDatabaseManager().update(
                    "DELETE FROM moderex_players WHERE username LIKE 'StressTest_%'"
                );
                sendStressProgress(conn, "cleanup", 1, 3);

                // Delete stress test punishments
                plugin.getDatabaseManager().update(
                    "DELETE FROM moderex_punishments WHERE case_id LIKE 'ST-%' OR player_name LIKE 'StressTarget_%'"
                );
                sendStressProgress(conn, "cleanup", 2, 3);

                sendStressProgress(conn, "cleanup", 3, 3);

                long duration = System.currentTimeMillis() - stressTestStartTime;
                sendStressComplete(conn, "cleanup", 0, duration, "Stress test data cleaned up");
            } catch (Exception e) {
                plugin.logError("Stress cleanup failed", e);
                sendStressError(conn, "cleanup", "Cleanup failed: " + e.getMessage());
            } finally {
                stressTestRunning = false;
            }
        });
    }

    private void handleDevStressStop(WebSocketConnection conn) {
        if (stressTestRunning) {
            stressTestCancelled = true;
            sendSuccess(conn, "Stopping stress test...");
        } else {
            sendError(conn, "NO_TEST_RUNNING", "No stress test is currently running");
        }
    }

    private void sendStressProgress(WebSocketConnection conn, String testType, int current, int total) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "DEV_STRESS_PROGRESS");
        JsonObject data = new JsonObject();
        data.addProperty("testType", testType);
        data.addProperty("current", current);
        data.addProperty("total", total);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendStressComplete(WebSocketConnection conn, String testType, int total, long duration, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "DEV_STRESS_COMPLETE");
        JsonObject data = new JsonObject();
        data.addProperty("testType", testType);
        data.addProperty("total", total);
        data.addProperty("duration", duration);
        data.addProperty("complete", true);
        data.addProperty("message", message);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendStressError(WebSocketConnection conn, String testType, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "DEV_STRESS_ERROR");
        JsonObject data = new JsonObject();
        data.addProperty("testType", testType);
        data.addProperty("error", true);
        data.addProperty("message", message);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Broadcast a message to all same-port connections.
     */
    public void broadcastToSamePort(String message) {
        for (WebSocketFrameHandler handler : samePortConnections.values()) {
            try {
                handler.send(message);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Wrapper class to make same-port connections compatible with existing handlers.
     */
    private static class SamePortConnectionWrapper extends WebSocketConnection {
        private final WebSocketFrameHandler handler;

        SamePortConnectionWrapper(WebSocketFrameHandler handler) {
            super(); // Use protected no-arg constructor
            this.handler = handler;
        }

        @Override
        void send(String message) {
            handler.send(message);
        }

        @Override
        boolean sendAsync(String message) {
            handler.send(message);
            return true;
        }

        @Override
        void close() {
            handler.close();
        }

        @Override
        String getRemoteAddress() {
            return handler.getRemoteAddress();
        }
    }
}

package com.blockforge.moderex.webpanel;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
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
        pending.hasPermission = player.hasPermission("moderex.webpanel") || player.isOp();

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

        // Check permission
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        boolean hasPermission = false;
        String prefix = "", suffix = "";
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerUuid.toString();

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel") || onlinePlayer.isOp();
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
            hasPermission = offlinePlayer.isOp();
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
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(trustedDevice.playerUuid);
        boolean hasPermission = false;
        String prefix = "", suffix = "";
        String playerName = trustedDevice.playerName;

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel") || onlinePlayer.isOp();
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
            hasPermission = offlinePlayer.isOp();
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

    private String getClientIp(WebSocketConnection conn) {
        try {
            if (conn.socket != null && conn.socket.getRemoteSocketAddress() instanceof InetSocketAddress) {
                return ((InetSocketAddress) conn.socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            }
        } catch (Exception ignored) {}
        return "unknown";
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
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(conn);
            case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(conn, data, session);
            case "CREATE_AUTOMOD_RULE" -> createAutomodRule(conn, data, session);
            case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(conn, data, session);
            case "GET_ANTICHEAT_INFO" -> sendAnticheatInfo(conn);
            case "GET_ANTICHEAT_ALERTS" -> sendAnticheatAlerts(conn);
            case "GET_ANTICHEAT_CHECKS" -> sendAnticheatChecks(conn);
            case "GET_STAFF_ANTICHEAT_SETTINGS" -> sendStaffAnticheatSettings(conn, session);
            case "UPDATE_STAFF_ANTICHEAT_SETTING" -> updateStaffAnticheatSetting(conn, data, session);
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

                // Fetch recent commands from database
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        List<JsonObject> commands = plugin.getDatabaseManager().query("""
                                SELECT command, executed_at FROM moderex_command_history
                                WHERE player_uuid = ?
                                ORDER BY executed_at DESC
                                LIMIT 20
                                """,
                                rs -> {
                                    List<JsonObject> list = new java.util.ArrayList<>();
                                    while (rs.next()) {
                                        JsonObject cmd = new JsonObject();
                                        cmd.addProperty("command", rs.getString("command"));
                                        cmd.addProperty("executedAt", rs.getLong("executed_at"));
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

                        JsonObject response = new JsonObject();
                        response.addProperty("type", "PLAYER_DETAILS");
                        response.add("data", details);
                        conn.send(GSON.toJson(response));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to fetch command history: " + e.getMessage());
                        // Send response without commands if query fails
                        details.add("recentCommands", new JsonArray());
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

            // Save rule
            plugin.getAutomodManager().saveRule(rule);

            // Broadcast update
            broadcastAutomodRules();

            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_UPDATED");
            response.addProperty("id", ruleId);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Automod rule updated: " + rule.getName() + " by " + session.playerName);
            debugSuccess(DebugCategory.AUTOMOD, "Automod rule updated",
                    "Rule: " + rule.getName() + ", By: " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "UPDATE_ERROR", "Failed to update rule: " + e.getMessage());
            plugin.logError("Failed to update automod rule from web panel", e);
            debugError(ErrorCode.AUTOMOD_RULE_UPDATE_FAILED, "Error: " + e.getMessage());
        }
    }

    private void createAutomodRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
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

            // Save rule
            plugin.getAutomodManager().saveRule(rule);

            // Broadcast update
            broadcastAutomodRules();

            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_CREATED");
            response.addProperty("id", rule.getId());
            conn.send(GSON.toJson(response));

            plugin.logDebug("[WebPanel] Automod rule created: " + name + " by " + session.playerName);
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

            plugin.getAutomodManager().deleteRule(ruleId);

            // Broadcast update
            broadcastAutomodRules();

            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_DELETED");
            response.addProperty("id", ruleId);
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
        JsonObject broadcast = new JsonObject();
        broadcast.addProperty("type", "AUTOMOD_RULES_DATA");
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

            com.google.gson.JsonArray blacklist = new com.google.gson.JsonArray();
            for (String word : rule.getBlacklistedWords()) {
                blacklist.add(word);
            }
            r.add("blacklistedWords", blacklist);

            com.google.gson.JsonArray whitelist = new com.google.gson.JsonArray();
            for (String word : rule.getExclusionWords()) {
                whitelist.add(word);
            }
            r.add("exclusionWords", whitelist);
            r.add("whitelist", whitelist);

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
        broadcast.add("data", data);
        String message = GSON.toJson(broadcast);

        for (WebSocketConnection conn : sessions.keySet()) {
            try {
                conn.send(message);
            } catch (Exception e) {
                plugin.logDebug("Failed to broadcast automod rules to connection: " + e.getMessage());
            }
        }
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
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
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

    private void broadcastTemplates() {
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
            if (changed) {
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
            }
        }

        sendSuccess(conn, "Settings saved");
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
                if (staff.hasPermission("moderex.notify.punishments")) {
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
        // Run broadcasts async to prevent main thread blocking on slow/dead connections
        if (executor != null && !executor.isShutdown()) {
            executor.submit(() -> {
                for (WebSocketConnection conn : connections) {
                    if (sessions.containsKey(conn)) {
                        if (!conn.sendAsync(message)) {
                            // Connection failed - remove it
                            connections.remove(conn);
                            sessions.remove(conn);
                            conn.close();
                        }
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

        data.addProperty("geyserAvailable", geyserAvailable);
        data.addProperty("floodgateAvailable", floodgateAvailable);

        if (geyserAvailable) {
            data.addProperty("geyserVersion", hookManager.getGeyserVersion());
        }

        if (floodgateAvailable) {
            data.addProperty("floodgateVersion", hookManager.getFloodgateVersion());
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

        // Reuse existing handlers by wrapping the connection
        switch (type) {
            case "GET_PLAYERS" -> sendPlayerList(wrapper);
            case "GET_PLAYER_DETAILS" -> sendPlayerDetails(wrapper, data);
            case "GET_PUNISHMENTS" -> sendPunishments(wrapper, data);
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(wrapper);
            case "GET_USER_SETTINGS" -> sendUserSettingsForSamePort(wrapper, session);
            case "GET_TEMPLATES" -> sendTemplates(wrapper);
            case "GET_STATS" -> sendStats(wrapper);
            case "GET_CHAT_STATUS" -> sendChatStatus(wrapper);
            case "SEND_STAFFCHAT", "STAFFCHAT_MESSAGE" -> {
                String msg = data.has("message") ? data.get("message").getAsString() : "";
                plugin.getStaffChatManager().broadcastFromWebPanel(session.playerName, msg);
                sendSuccess(wrapper, "Message sent");
            }
            default -> sendError(wrapper, "UNKNOWN_TYPE", "Unknown message type: " + type);
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
        }

        response.add("data", data);
        wrapper.send(GSON.toJson(response));
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

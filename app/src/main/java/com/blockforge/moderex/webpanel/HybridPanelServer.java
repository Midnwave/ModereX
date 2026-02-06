package com.blockforge.moderex.webpanel;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.alert.AlertManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentEvidence;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.evidence.Evidence;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.web.WebAuthManager;
import com.blockforge.moderex.webpanel.debug.DebugCategory;
import com.blockforge.moderex.webpanel.debug.ErrorCode;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
public class HybridPanelServer implements com.blockforge.moderex.gateway.GatewayMessageHandler {

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

    // Gateway-relayed connections (panel.moderex.net via gateway)
    private final Map<String, GatewayConnection> gatewayConnections = new ConcurrentHashMap<>();
    private final Map<String, WebPanelSession> gatewaySessions = new ConcurrentHashMap<>();

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

            // Handle POST requests for evidence upload
            if ("POST".equals(method) && path.equals("/api/evidence/upload")) {
                handleEvidenceUpload(socket, in, out, headers);
                return;
            }

            if (!"GET".equals(method)) {
                sendHttpError(out, 405, "Method Not Allowed");
                return;
            }

            // Player portal routes
            if (path.startsWith("/moderex/punishment/")) {
                handlePlayerPortalRequest(out, path);
                return;
            }
            if (path.startsWith("/moderex/portal/logout/")) {
                handlePortalLogout(out, path);
                return;
            }
            if (path.startsWith("/moderex/portal/")) {
                handlePortalSessionRequest(out, path);
                return;
            }

            // Evidence file retrieval: /api/evidence/{fileId}
            if (path.startsWith("/api/evidence/") && !path.equals("/api/evidence/upload")) {
                handleEvidenceFileRequest(out, path, headers);
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

    /**
     * Handle evidence file upload
     */
    private void handleEvidenceUpload(Socket socket, InputStream socketIn, OutputStream out, Map<String, String> headers) throws IOException {
        // Check authorization
        String authHeader = headers.get("authorization");
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            sendJsonResponse(out, 401, "{\"success\":false,\"error\":\"Authorization required\"}");
            return;
        }

        String sessionId = authHeader.substring(7);

        // Validate session via WebAuthManager
        WebAuthManager authManager = plugin.getWebAuthManager();
        if (authManager == null) {
            sendJsonResponse(out, 401, "{\"success\":false,\"error\":\"Authentication system not available\"}");
            return;
        }

        WebAuthManager.AuthenticatedSession authSession = authManager.validateSession(sessionId);
        if (authSession == null) {
            sendJsonResponse(out, 401, "{\"success\":false,\"error\":\"Invalid or expired session\"}");
            return;
        }

        // Get content length
        int contentLength;
        try {
            contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        } catch (NumberFormatException e) {
            sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"Invalid content length\"}");
            return;
        }

        // Check max file size (default 250MB, configurable)
        long maxSize = com.blockforge.moderex.evidence.Evidence.MAX_FILE_SIZE;
        if (contentLength <= 0 || contentLength > maxSize) {
            sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"File too large. Maximum size is " + (maxSize / 1024 / 1024) + " MB\"}");
            return;
        }

        // Get content type (multipart/form-data; boundary=...)
        String contentType = headers.getOrDefault("content-type", "");
        if (!contentType.startsWith("multipart/form-data")) {
            sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"Expected multipart/form-data\"}");
            return;
        }

        // Extract boundary
        String boundary = null;
        for (String part : contentType.split(";")) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                boundary = part.substring(9);
                if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
                break;
            }
        }

        if (boundary == null) {
            sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"Missing boundary in content type\"}");
            return;
        }

        // Read full body
        byte[] bodyBytes = new byte[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = socketIn.read(bodyBytes, totalRead, contentLength - totalRead);
            if (read < 0) break;
            totalRead += read;
        }

        // Parse multipart data
        String boundaryPrefix = "--" + boundary;
        byte[] boundaryBytes = boundaryPrefix.getBytes(StandardCharsets.UTF_8);

        // Find file data
        int pos = 0;
        String fileName = null;
        byte[] fileData = null;

        while (pos < bodyBytes.length) {
            // Find next boundary
            int boundaryStart = indexOf(bodyBytes, boundaryBytes, pos);
            if (boundaryStart < 0) break;

            pos = boundaryStart + boundaryBytes.length;
            if (pos >= bodyBytes.length) break;

            // Skip CRLF
            if (bodyBytes[pos] == '\r') pos++;
            if (pos < bodyBytes.length && bodyBytes[pos] == '\n') pos++;

            // Check for closing boundary
            if (pos + 1 < bodyBytes.length && bodyBytes[pos] == '-' && bodyBytes[pos + 1] == '-') {
                break;
            }

            // Read headers until empty line
            StringBuilder headerBuilder = new StringBuilder();
            while (pos < bodyBytes.length) {
                int lineEnd = pos;
                while (lineEnd < bodyBytes.length && bodyBytes[lineEnd] != '\n') lineEnd++;

                String line = new String(bodyBytes, pos, lineEnd - pos, StandardCharsets.UTF_8).trim();
                pos = lineEnd + 1;

                if (line.isEmpty()) break;
                headerBuilder.append(line).append("\n");
            }

            String partHeaders = headerBuilder.toString();

            // Extract filename from Content-Disposition
            if (partHeaders.contains("filename=\"")) {
                int fnStart = partHeaders.indexOf("filename=\"") + 10;
                int fnEnd = partHeaders.indexOf("\"", fnStart);
                if (fnEnd > fnStart) {
                    fileName = partHeaders.substring(fnStart, fnEnd);
                }
            }

            // Find end of this part (next boundary)
            int nextBoundary = indexOf(bodyBytes, boundaryBytes, pos);
            if (nextBoundary < 0) nextBoundary = bodyBytes.length;

            // Extract file data (minus trailing CRLF before boundary)
            int dataEnd = nextBoundary;
            if (dataEnd > pos && bodyBytes[dataEnd - 1] == '\n') dataEnd--;
            if (dataEnd > pos && bodyBytes[dataEnd - 1] == '\r') dataEnd--;

            if (fileName != null && dataEnd > pos) {
                fileData = new byte[dataEnd - pos];
                System.arraycopy(bodyBytes, pos, fileData, 0, dataEnd - pos);
                break;
            }

            pos = nextBoundary;
        }

        if (fileName == null || fileData == null || fileData.length == 0) {
            sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"No file found in request\"}");
            return;
        }

        // Upload through evidence manager
        try {
            com.blockforge.moderex.evidence.Evidence evidence = plugin.getEvidenceManager()
                    .uploadEvidence(authSession.playerUuid, authSession.playerName, fileName,
                            new java.io.ByteArrayInputStream(fileData), fileData.length)
                    .get();

            if (evidence == null) {
                sendJsonResponse(out, 400, "{\"success\":false,\"error\":\"Failed to save evidence. Check file type and size.\"}");
                return;
            }

            // Return success with evidence info
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.add("evidence", evidence.toJson());
            sendJsonResponse(out, 200, GSON.toJson(response));

        } catch (Exception e) {
            plugin.logError("Evidence upload failed", e);
            sendJsonResponse(out, 500, "{\"success\":false,\"error\":\"Server error during upload\"}");
        }
    }

    /**
     * Handle evidence file retrieval requests.
     * URL format: /api/evidence/{fileId}
     */
    private void handleEvidenceFileRequest(OutputStream out, String path, Map<String, String> headers) throws IOException {
        // Extract file ID from path
        String fileId = path.substring("/api/evidence/".length());
        if (fileId.isEmpty()) {
            sendJsonResponse(out, 400, "{\"error\":\"Evidence ID required\"}");
            return;
        }

        // Remove any trailing slashes or query params
        if (fileId.contains("/")) {
            fileId = fileId.substring(0, fileId.indexOf("/"));
        }

        // Check authorization - either staff session or player portal session
        String authHeader = headers.get("authorization");
        boolean isAuthorized = false;

        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            String sessionId = authHeader.substring(7);
            WebAuthManager authManager = plugin.getWebAuthManager();
            if (authManager != null) {
                WebAuthManager.AuthenticatedSession authSession = authManager.validateSession(sessionId);
                if (authSession != null) {
                    isAuthorized = true;
                }
            }
        }

        // Also allow cookie-based auth for web panel
        String cookieHeader = headers.get("cookie");
        if (!isAuthorized && cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] parts = cookie.trim().split("=", 2);
                if (parts.length == 2 && "mx_session".equals(parts[0])) {
                    WebAuthManager authManager = plugin.getWebAuthManager();
                    if (authManager != null) {
                        WebAuthManager.AuthenticatedSession authSession = authManager.validateSession(parts[1]);
                        if (authSession != null) {
                            isAuthorized = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!isAuthorized) {
            sendJsonResponse(out, 401, "{\"error\":\"Authorization required\"}");
            return;
        }

        // Get evidence from manager
        com.blockforge.moderex.evidence.Evidence evidence = plugin.getEvidenceManager().getEvidence(fileId);
        if (evidence == null) {
            sendJsonResponse(out, 404, "{\"error\":\"Evidence not found\"}");
            return;
        }

        // Get the file - resolve relative path against evidence directory
        java.nio.file.Path filePath = plugin.getEvidenceManager().getEvidenceFile(fileId);
        if (filePath == null) {
            plugin.logDebug("[Evidence] Could not resolve file path for: " + fileId);
            sendJsonResponse(out, 404, "{\"error\":\"Evidence file path not found\"}");
            return;
        }

        java.io.File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            plugin.logDebug("[Evidence] File not found on disk: " + filePath);
            sendJsonResponse(out, 404, "{\"error\":\"Evidence file not found on disk\"}");
            return;
        }

        // Stream the file with proper content type
        String mimeType = evidence.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) {
            // Fallback based on file extension
            String name = file.getName().toLowerCase();
            if (name.endsWith(".mp4")) mimeType = "video/mp4";
            else if (name.endsWith(".mkv")) mimeType = "video/x-matroska";
            else if (name.endsWith(".mov")) mimeType = "video/quicktime";
            else if (name.endsWith(".png")) mimeType = "image/png";
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) mimeType = "image/jpeg";
            else mimeType = "application/octet-stream";
        }

        // Build HTTP response headers
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: ").append(mimeType).append("\r\n");
        response.append("Content-Length: ").append(file.length()).append("\r\n");
        response.append("Content-Disposition: inline; filename=\"").append(evidence.getFileName()).append("\"\r\n");
        response.append("Access-Control-Allow-Origin: *\r\n");
        response.append("Cache-Control: private, max-age=3600\r\n");
        response.append("\r\n");

        out.write(response.toString().getBytes(StandardCharsets.UTF_8));

        // Stream file content
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.flush();
    }

    /**
     * Handle player punishment portal requests.
     * URL format: /moderex/punishment/{case-id}/{auth-session}
     */
    private void handlePlayerPortalRequest(OutputStream out, String path) throws IOException {
        // Check if portal is enabled
        if (!plugin.getConfigManager().getSettings().isPlayerPortalEnabled()) {
            sendHtmlError(out, 503, "Player Portal Disabled", "The punishment portal is currently disabled.");
            return;
        }

        // Parse path: /moderex/punishment/{case-id}/{auth-session}
        String[] pathParts = path.substring("/moderex/punishment/".length()).split("/");
        if (pathParts.length < 2) {
            sendHtmlError(out, 400, "Invalid URL", "The URL format is invalid. Please use the link provided in your punishment message.");
            return;
        }

        String caseId = pathParts[0];
        String sessionId = pathParts[1];

        // Validate session
        com.blockforge.moderex.portal.PlayerAuthSession authSession = plugin.getAuthSessionManager().validateSession(sessionId);
        if (authSession == null) {
            sendHtmlError(out, 401, "Session Expired", "Your session has expired or is invalid. Please use a new link from your punishment message.");
            return;
        }

        // Load punishment by case ID
        plugin.getPunishmentManager().getPunishmentByCaseId(caseId).thenAccept(punishment -> {
            try {
                if (punishment == null) {
                    sendHtmlError(out, 404, "Punishment Not Found", "The punishment case was not found.");
                    return;
                }

                // Verify the player owns this punishment
                if (!punishment.getPlayerUuid().equals(authSession.getPlayerUuid())) {
                    sendHtmlError(out, 403, "Access Denied", "You do not have permission to view this punishment.");
                    return;
                }

                // Build the portal HTML page
                String html = buildPlayerPortalHtml(punishment, authSession);
                sendHtmlResponse(out, 200, html);

            } catch (java.net.SocketException e) {
                // Client disconnected before response was ready - normal condition
                plugin.logDebug("[Portal] Client disconnected before response: " + e.getMessage());
            } catch (IOException e) {
                plugin.logError("Failed to send portal response", e);
            }
        });
    }

    /**
     * Handle portal session requests.
     * URL format: /moderex/portal/{auth-session}
     */
    private void handlePortalSessionRequest(OutputStream out, String path) throws IOException {
        if (!plugin.getConfigManager().getSettings().isPlayerPortalEnabled()) {
            sendHtmlError(out, 503, "Portal Disabled", "The player portal is currently disabled.");
            return;
        }

        String sessionId = path.substring("/moderex/portal/".length());
        if (sessionId.isEmpty() || sessionId.contains("/")) {
            sendHtmlError(out, 400, "Invalid URL", "Invalid session URL format.");
            return;
        }

        com.blockforge.moderex.portal.PlayerAuthSession authSession = plugin.getAuthSessionManager().validateSession(sessionId);
        if (authSession == null) {
            sendHtmlError(out, 401, "Session Expired", "Your session has expired or is invalid.");
            return;
        }

        // Get the most recent punishment for this player to show
        plugin.getPunishmentManager().getPunishments(authSession.getPlayerUuid()).thenAccept(punishments -> {
            try {
                if (punishments == null || punishments.isEmpty()) {
                    sendHtmlError(out, 404, "No Punishments", "You have no punishments on record.");
                    return;
                }

                // Get the most recent or linked punishment
                com.blockforge.moderex.punishment.Punishment punishment = punishments.get(0);
                if (authSession.getCaseId() != null) {
                    for (com.blockforge.moderex.punishment.Punishment p : punishments) {
                        if (p.getCaseId().equals(authSession.getCaseId())) {
                            punishment = p;
                            break;
                        }
                    }
                }

                String html = buildPlayerPortalHtml(punishment, authSession);
                sendHtmlResponse(out, 200, html);

            } catch (java.net.SocketException e) {
                // Client disconnected before response was ready - normal condition
                plugin.logDebug("[Portal] Client disconnected before response: " + e.getMessage());
            } catch (IOException e) {
                plugin.logError("Failed to send portal response", e);
            }
        });
    }

    /**
     * Handle portal logout requests.
     * URL format: /moderex/portal/logout/{session-id}
     */
    private void handlePortalLogout(OutputStream out, String path) throws IOException {
        String sessionId = path.substring("/moderex/portal/logout/".length());

        if (!sessionId.isEmpty()) {
            plugin.getAuthSessionManager().invalidateSession(sessionId);
        }

        String serverName = plugin.getConfigManager().getSettings().getServerName();
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s - Logged Out</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }
                    .container { text-align: center; padding: 40px; background: #1e293b; border-radius: 12px; border: 1px solid #334155; max-width: 400px; }
                    h1 { color: #22c55e; margin-bottom: 16px; font-size: 24px; }
                    p { color: #94a3b8; margin-bottom: 24px; }
                    .server { font-size: 18px; font-weight: 600; color: #2d7aed; margin-top: 24px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>✓ Logged Out</h1>
                    <p>You have been successfully logged out of the punishment portal.</p>
                    <p style="font-size: 13px;">To access your punishments again, you will need a new link from the disconnect screen.</p>
                    <div class="server">%s</div>
                </div>
            </body>
            </html>
            """.formatted(escapeHtml(serverName), escapeHtml(serverName));

        sendHtmlResponse(out, 200, html);
    }

    /**
     * Build the HTML page for the player punishment portal.
     */
    private String buildPlayerPortalHtml(com.blockforge.moderex.punishment.Punishment punishment, com.blockforge.moderex.portal.PlayerAuthSession session) {
        String serverName = plugin.getConfigManager().getSettings().getServerName();
        UUID playerUuid = session.getPlayerUuid();

        // Get all punishments for this player
        java.util.List<com.blockforge.moderex.punishment.Punishment> allPunishments = new java.util.ArrayList<>();
        try {
            allPunishments = plugin.getPunishmentManager().getPunishments(playerUuid).get();
        } catch (Exception e) {
            plugin.logError("Failed to load punishments for portal", e);
        }

        // Count active punishments
        long activePunishments = allPunishments.stream().filter(p -> p.isActive()).count();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>").append(escapeHtml(serverName)).append(" - Player Portal</title>");
        html.append("<style>");
        html.append(getPortalStyles());
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        // Header
        html.append("<header>");
        html.append("<div class=\"header-left\">");
        html.append("<div class=\"server-name\">").append(escapeHtml(serverName)).append("</div>");
        html.append("</div>");
        html.append("<div class=\"header-right\">");
        html.append("<span class=\"player-info\">").append(escapeHtml(punishment.getPlayerName())).append("</span>");
        html.append("<button class=\"logout-btn\" onclick=\"logout()\">Logout</button>");
        html.append("</div>");
        html.append("</header>");

        // Tab navigation
        html.append("<nav class=\"tabs\">");
        html.append("<button class=\"tab active\" onclick=\"showTab('punishments')\">📋 My Punishments");
        if (activePunishments > 0) {
            html.append("<span class=\"badge-count\">").append(activePunishments).append("</span>");
        }
        html.append("</button>");
        html.append("<button class=\"tab\" onclick=\"showTab('settings')\">⚙️ My Settings</button>");
        html.append("</nav>");

        // Main content
        html.append("<main>");

        // Punishments tab
        html.append("<div id=\"tab-punishments\" class=\"tab-content active\">");
        buildPunishmentsTab(html, allPunishments, punishment);
        html.append("</div>");

        // Settings tab
        html.append("<div id=\"tab-settings\" class=\"tab-content\">");
        buildSettingsTab(html, session);
        html.append("</div>");

        html.append("</main>");

        // JavaScript
        html.append("<script>");
        html.append(getPortalScript(session.getId()));
        html.append("</script>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private void buildPunishmentsTab(StringBuilder html, java.util.List<com.blockforge.moderex.punishment.Punishment> punishments, com.blockforge.moderex.punishment.Punishment currentPunishment) {
        // Timeline view
        html.append("<div class=\"card\">");
        html.append("<div class=\"card-title\">📊 Punishment History</div>");

        if (punishments.isEmpty()) {
            html.append("<div class=\"empty-state\">No punishments on record</div>");
        } else {
            html.append("<div class=\"timeline\">");
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");

            for (com.blockforge.moderex.punishment.Punishment p : punishments) {
                String status = p.isActive() ? "active" : (p.isExpired() ? "expired" : "revoked");
                boolean isCurrent = p.getCaseId().equals(currentPunishment.getCaseId());

                html.append("<div class=\"timeline-item ").append(status);
                if (isCurrent) html.append(" current");
                html.append("\" onclick=\"showPunishmentDetails('").append(p.getCaseId()).append("')\">");

                html.append("<div class=\"timeline-date\">");
                html.append("<span class=\"date\">").append(dateFormat.format(new java.util.Date(p.getCreatedAt()))).append("</span>");
                html.append("<span class=\"time\">").append(timeFormat.format(new java.util.Date(p.getCreatedAt()))).append("</span>");
                html.append("</div>");

                html.append("<div class=\"timeline-dot\"></div>");

                html.append("<div class=\"timeline-content\">");
                html.append("<div class=\"timeline-header\">");
                html.append("<span class=\"badge type-badge\">").append(p.getType().name()).append("</span>");
                html.append("<span class=\"badge status-badge ").append(status).append("\">").append(status.substring(0, 1).toUpperCase()).append(status.substring(1)).append("</span>");
                html.append("</div>");
                html.append("<div class=\"timeline-reason\">").append(escapeHtml(truncate(p.getReason(), 80))).append("</div>");
                html.append("<div class=\"timeline-staff\">by ").append(escapeHtml(p.getStaffName())).append("</div>");
                html.append("</div>");

                html.append("</div>");
            }
            html.append("</div>");
        }
        html.append("</div>");

        // Current punishment details
        html.append("<div id=\"punishment-details\" class=\"card\">");
        buildPunishmentDetails(html, currentPunishment);
        html.append("</div>");
    }

    private void buildPunishmentDetails(StringBuilder html, com.blockforge.moderex.punishment.Punishment punishment) {
        String status = punishment.isActive() ? "Active" : (punishment.isExpired() ? "Expired" : "Revoked");
        String statusClass = punishment.isActive() ? "active" : (punishment.isExpired() ? "expired" : "revoked");

        String expiresAt = punishment.getExpiresAt() > 0
                ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(punishment.getExpiresAt()))
                : "Permanent";
        String issuedAt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(punishment.getCreatedAt()));

        html.append("<div class=\"card-title\"><span class=\"badge type-badge\">").append(punishment.getType().name()).append("</span> Punishment Details</div>");

        html.append("<div class=\"details-grid\">");
        html.append("<div class=\"detail-item\"><span class=\"label\">Case ID</span><span class=\"value\">#").append(escapeHtml(punishment.getCaseId())).append("</span></div>");
        html.append("<div class=\"detail-item\"><span class=\"label\">Status</span><span class=\"value\"><span class=\"badge status-badge ").append(statusClass).append("\">").append(status).append("</span></span></div>");
        html.append("<div class=\"detail-item\"><span class=\"label\">Issued By</span><span class=\"value\">").append(escapeHtml(punishment.getStaffName())).append("</span></div>");
        html.append("<div class=\"detail-item\"><span class=\"label\">Issued At</span><span class=\"value\">").append(issuedAt).append("</span></div>");
        html.append("<div class=\"detail-item\"><span class=\"label\">Expires At</span><span class=\"value\">").append(expiresAt).append("</span></div>");
        html.append("</div>");

        html.append("<div class=\"reason-section\">");
        html.append("<span class=\"label\">Reason</span>");
        html.append("<div class=\"reason-box\">").append(escapeHtml(punishment.getReason())).append("</div>");
        html.append("</div>");

        // Evidence section
        java.util.List<com.blockforge.moderex.punishment.PunishmentEvidence> evidenceList =
                plugin.getPunishmentManager().getPunishmentEvidence(punishment.getCaseId());

        if (!evidenceList.isEmpty()) {
            html.append("<div class=\"evidence-section\">");
            html.append("<span class=\"label\">📎 Evidence (").append(evidenceList.size()).append(")</span>");

            for (com.blockforge.moderex.punishment.PunishmentEvidence evidence : evidenceList) {
                html.append("<div class=\"evidence-item\">");

                if (evidence.isActivityLog()) {
                    com.google.gson.JsonObject snapshot = evidence.getActivityLogAsJson();
                    if (snapshot != null) {
                        String type = snapshot.has("type") ? snapshot.get("type").getAsString() : "UNKNOWN";
                        String content = snapshot.has("content") ? snapshot.get("content").getAsString() : "";
                        long timestamp = snapshot.has("timestamp") ? snapshot.get("timestamp").getAsLong() : 0;
                        String timeStr = timestamp > 0
                                ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp))
                                : "Unknown time";

                        html.append("<div class=\"evidence-header\">");
                        html.append("<span class=\"badge\" style=\"background: rgba(45, 122, 237, 0.2); color: #2d7aed;\">").append(type).append("</span>");
                        html.append("<span class=\"evidence-time\">").append(timeStr).append("</span>");
                        html.append("</div>");
                        html.append("<div class=\"evidence-content\">").append(escapeHtml(content)).append("</div>");
                    }
                } else if (evidence.isFile()) {
                    com.blockforge.moderex.evidence.Evidence fileEvidence = plugin.getEvidenceManager().getEvidence(evidence.getEvidenceId());
                    if (fileEvidence != null) {
                        String fileType = fileEvidence.getFileType().name();
                        boolean isImage = fileType.startsWith("IMAGE");
                        boolean isVideo = fileType.startsWith("VIDEO");

                        html.append("<div class=\"evidence-header\">");
                        html.append("<span class=\"badge\" style=\"background: rgba(168, 85, 247, 0.2); color: #a855f7;\">");
                        html.append(isImage ? "📷 Image" : (isVideo ? "🎬 Video" : "📁 File"));
                        html.append("</span>");
                        html.append("<span class=\"evidence-time\">").append(escapeHtml(fileEvidence.getFileName())).append("</span>");
                        html.append("</div>");

                        if (isImage) {
                            html.append("<div class=\"evidence-media\">");
                            html.append("<img src=\"/api/evidence/").append(fileEvidence.getId()).append("\" onclick=\"window.open(this.src)\" alt=\"Evidence\">");
                            html.append("</div>");
                        } else if (isVideo) {
                            html.append("<div class=\"evidence-media\">");
                            html.append("<video controls><source src=\"/api/evidence/").append(fileEvidence.getId()).append("\" type=\"").append(fileEvidence.getMimeType()).append("\"></video>");
                            html.append("</div>");
                        }
                    }
                }
                html.append("</div>");
            }
            html.append("</div>");
        }
    }

    private void buildSettingsTab(StringBuilder html, com.blockforge.moderex.portal.PlayerAuthSession session) {
        html.append("<div class=\"card\">");
        html.append("<div class=\"card-title\">🎨 Appearance</div>");
        html.append("<div class=\"setting-item\">");
        html.append("<div class=\"setting-info\">");
        html.append("<span class=\"setting-label\">Color Scheme</span>");
        html.append("<span class=\"setting-desc\">Choose your preferred accent color</span>");
        html.append("</div>");
        html.append("<div class=\"color-picker\">");
        html.append("<button class=\"color-option\" style=\"background:#2d7aed\" onclick=\"setColor('#2d7aed')\" title=\"Blue\"></button>");
        html.append("<button class=\"color-option\" style=\"background:#a855f7\" onclick=\"setColor('#a855f7')\" title=\"Purple\"></button>");
        html.append("<button class=\"color-option\" style=\"background:#ec4899\" onclick=\"setColor('#ec4899')\" title=\"Pink\"></button>");
        html.append("<button class=\"color-option\" style=\"background:#22c55e\" onclick=\"setColor('#22c55e')\" title=\"Green\"></button>");
        html.append("<button class=\"color-option\" style=\"background:#f59e0b\" onclick=\"setColor('#f59e0b')\" title=\"Orange\"></button>");
        html.append("<button class=\"color-option\" style=\"background:#ef4444\" onclick=\"setColor('#ef4444')\" title=\"Red\"></button>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class=\"card\">");
        html.append("<div class=\"card-title\">🔔 Notifications</div>");
        html.append("<div class=\"setting-item\">");
        html.append("<div class=\"setting-info\">");
        html.append("<span class=\"setting-label\">Browser Notifications</span>");
        html.append("<span class=\"setting-desc\">Get notified when punishments are revoked or expire</span>");
        html.append("</div>");
        html.append("<label class=\"toggle\">");
        html.append("<input type=\"checkbox\" id=\"notifications-toggle\" onchange=\"toggleNotifications(this.checked)\">");
        html.append("<span class=\"toggle-slider\"></span>");
        html.append("</label>");
        html.append("</div>");
        html.append("<div id=\"notification-status\" class=\"notification-status\"></div>");
        html.append("</div>");

        html.append("<div class=\"card\">");
        html.append("<div class=\"card-title\">🔐 Device Trust</div>");
        html.append("<div class=\"setting-item\">");
        html.append("<div class=\"setting-info\">");
        html.append("<span class=\"setting-label\">Trust This Device</span>");
        html.append("<span class=\"setting-desc\">Skip authentication on future visits from this device</span>");
        html.append("</div>");
        html.append("<label class=\"toggle\">");
        html.append("<input type=\"checkbox\" id=\"device-trust-toggle\" onchange=\"toggleDeviceTrust(this.checked)\">");
        html.append("<span class=\"toggle-slider\"></span>");
        html.append("</label>");
        html.append("</div>");
        html.append("<div class=\"warning-box\" id=\"trust-warning\" style=\"display:none;\">");
        html.append("<strong>⚠️ Warning:</strong> Only enable this on your personal device. Anyone with access to this device will be able to view your punishments.");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class=\"card\">");
        html.append("<div class=\"card-title\">ℹ️ Session Info</div>");
        html.append("<div class=\"session-info\">");
        html.append("<div class=\"detail-item\"><span class=\"label\">Session ID</span><span class=\"value\">").append(session.getId().substring(0, 4)).append("...").append(session.getId().substring(6)).append("</span></div>");
        html.append("<div class=\"detail-item\"><span class=\"label\">Expires</span><span class=\"value\">").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(session.getExpiresAt()))).append("</span></div>");
        html.append("</div>");
        html.append("</div>");
    }

    private String getPortalStyles() {
        return """
            * { margin: 0; padding: 0; box-sizing: border-box; }
            :root { --primary: #2d7aed; --bg: #0f172a; --card: #1e293b; --border: #334155; --text: #e2e8f0; --muted: #94a3b8; }
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: var(--bg); color: var(--text); min-height: 100vh; }
            header { background: var(--card); border-bottom: 1px solid var(--border); padding: 16px 24px; display: flex; justify-content: space-between; align-items: center; }
            .header-left { display: flex; align-items: center; gap: 16px; }
            .header-right { display: flex; align-items: center; gap: 16px; }
            .server-name { font-size: 20px; font-weight: 600; color: var(--primary); }
            .player-info { font-size: 14px; color: var(--muted); }
            .logout-btn { background: transparent; border: 1px solid #ef4444; color: #ef4444; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-size: 13px; transition: all 0.2s; }
            .logout-btn:hover { background: rgba(239, 68, 68, 0.1); }
            .tabs { display: flex; gap: 8px; padding: 16px 24px; background: var(--card); border-bottom: 1px solid var(--border); }
            .tab { background: transparent; border: none; color: var(--muted); padding: 10px 20px; border-radius: 8px; cursor: pointer; font-size: 14px; display: flex; align-items: center; gap: 8px; transition: all 0.2s; }
            .tab:hover { background: rgba(255,255,255,0.05); color: var(--text); }
            .tab.active { background: var(--primary); color: white; }
            .badge-count { background: #ef4444; color: white; font-size: 11px; padding: 2px 6px; border-radius: 10px; }
            main { max-width: 900px; margin: 24px auto; padding: 0 20px; }
            .tab-content { display: none; }
            .tab-content.active { display: block; }
            .card { background: var(--card); border: 1px solid var(--border); border-radius: 12px; padding: 24px; margin-bottom: 20px; }
            .card-title { font-size: 16px; font-weight: 600; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
            .badge { display: inline-block; padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 600; }
            .type-badge { background: rgba(239, 68, 68, 0.2); color: #ef4444; }
            .status-badge { padding: 3px 8px; font-size: 11px; }
            .status-badge.active { background: rgba(239, 68, 68, 0.2); color: #ef4444; }
            .status-badge.expired { background: rgba(107, 114, 128, 0.2); color: #6b7280; }
            .status-badge.revoked { background: rgba(34, 197, 94, 0.2); color: #22c55e; }
            .timeline { position: relative; padding-left: 120px; }
            .timeline-item { position: relative; padding: 16px 0 16px 30px; border-left: 2px solid var(--border); margin-left: 10px; cursor: pointer; transition: all 0.2s; }
            .timeline-item:hover { background: rgba(255,255,255,0.02); }
            .timeline-item.current { background: rgba(45, 122, 237, 0.1); border-left-color: var(--primary); }
            .timeline-item.active .timeline-dot { background: #ef4444; box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.2); }
            .timeline-item.expired .timeline-dot { background: #6b7280; }
            .timeline-item.revoked .timeline-dot { background: #22c55e; }
            .timeline-date { position: absolute; left: -120px; top: 16px; text-align: right; width: 100px; }
            .timeline-date .date { display: block; font-size: 13px; color: var(--text); }
            .timeline-date .time { font-size: 11px; color: var(--muted); }
            .timeline-dot { position: absolute; left: -7px; top: 20px; width: 12px; height: 12px; border-radius: 50%; background: var(--border); }
            .timeline-content { }
            .timeline-header { display: flex; gap: 8px; margin-bottom: 8px; }
            .timeline-reason { font-size: 14px; color: var(--text); margin-bottom: 4px; }
            .timeline-staff { font-size: 12px; color: var(--muted); }
            .details-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
            .detail-item { display: flex; flex-direction: column; gap: 4px; }
            .label { color: var(--muted); font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; }
            .value { font-size: 14px; }
            .reason-section { margin-top: 20px; }
            .reason-box { background: var(--bg); border-radius: 8px; padding: 16px; margin-top: 8px; font-size: 14px; line-height: 1.6; }
            .evidence-section { margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border); }
            .evidence-item { background: var(--bg); border-radius: 8px; padding: 16px; margin-top: 12px; }
            .evidence-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
            .evidence-time { font-size: 12px; color: var(--muted); }
            .evidence-content { font-size: 14px; }
            .evidence-media { margin-top: 12px; text-align: center; }
            .evidence-media img, .evidence-media video { max-width: 100%; max-height: 300px; border-radius: 8px; cursor: pointer; }
            .empty-state { text-align: center; padding: 40px; color: var(--muted); }
            .setting-item { display: flex; justify-content: space-between; align-items: center; padding: 16px 0; border-bottom: 1px solid var(--border); }
            .setting-item:last-child { border-bottom: none; }
            .setting-info { flex: 1; }
            .setting-label { display: block; font-size: 14px; font-weight: 500; margin-bottom: 4px; }
            .setting-desc { font-size: 13px; color: var(--muted); }
            .color-picker { display: flex; gap: 8px; }
            .color-option { width: 32px; height: 32px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; transition: all 0.2s; }
            .color-option:hover { transform: scale(1.1); }
            .color-option.active { border-color: white; box-shadow: 0 0 0 2px var(--bg); }
            .toggle { position: relative; width: 50px; height: 26px; }
            .toggle input { opacity: 0; width: 0; height: 0; }
            .toggle-slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background: var(--border); border-radius: 26px; transition: 0.3s; }
            .toggle-slider:before { position: absolute; content: ""; height: 20px; width: 20px; left: 3px; bottom: 3px; background: white; border-radius: 50%; transition: 0.3s; }
            .toggle input:checked + .toggle-slider { background: var(--primary); }
            .toggle input:checked + .toggle-slider:before { transform: translateX(24px); }
            .warning-box { background: rgba(245, 158, 11, 0.1); border: 1px solid rgba(245, 158, 11, 0.3); border-radius: 8px; padding: 12px; margin-top: 12px; font-size: 13px; color: #f59e0b; }
            .notification-status { font-size: 13px; color: var(--muted); margin-top: 8px; padding: 0 16px; }
            .session-info { }
            """;
    }

    private String getPortalScript(String sessionId) {
        return "const sessionId = '" + sessionId + "';\n" +
            "let currentColor = localStorage.getItem('portalColor') || '#2d7aed';\n" +
            "\n" +
            "function showTab(tabName) {\n" +
            "    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));\n" +
            "    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));\n" +
            "    document.getElementById('tab-' + tabName).classList.add('active');\n" +
            "    event.target.closest('.tab').classList.add('active');\n" +
            "}\n" +
            "\n" +
            "function showPunishmentDetails(caseId) {\n" +
            "    document.querySelectorAll('.timeline-item').forEach(i => i.classList.remove('current'));\n" +
            "    event.target.closest('.timeline-item').classList.add('current');\n" +
            "}\n" +
            "\n" +
            "function logout() {\n" +
            "    if (confirm('Are you sure you want to logout?')) {\n" +
            "        localStorage.removeItem('deviceTrust');\n" +
            "        window.location.href = '/moderex/portal/logout/' + sessionId;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function setColor(color) {\n" +
            "    currentColor = color;\n" +
            "    localStorage.setItem('portalColor', color);\n" +
            "    document.documentElement.style.setProperty('--primary', color);\n" +
            "    document.querySelectorAll('.color-option').forEach(o => o.classList.remove('active'));\n" +
            "    event.target.classList.add('active');\n" +
            "}\n" +
            "\n" +
            "function toggleNotifications(enabled) {\n" +
            "    const status = document.getElementById('notification-status');\n" +
            "    if (enabled) {\n" +
            "        if ('Notification' in window) {\n" +
            "            Notification.requestPermission().then(perm => {\n" +
            "                if (perm === 'granted') {\n" +
            "                    status.textContent = '✓ Notifications enabled';\n" +
            "                    status.style.color = '#22c55e';\n" +
            "                    localStorage.setItem('notifications', 'true');\n" +
            "                } else {\n" +
            "                    document.getElementById('notifications-toggle').checked = false;\n" +
            "                    status.textContent = '✗ Permission denied by browser';\n" +
            "                    status.style.color = '#ef4444';\n" +
            "                }\n" +
            "            });\n" +
            "        } else {\n" +
            "            document.getElementById('notifications-toggle').checked = false;\n" +
            "            status.textContent = '✗ Browser does not support notifications';\n" +
            "            status.style.color = '#ef4444';\n" +
            "        }\n" +
            "    } else {\n" +
            "        localStorage.removeItem('notifications');\n" +
            "        status.textContent = '';\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function toggleDeviceTrust(enabled) {\n" +
            "    const warning = document.getElementById('trust-warning');\n" +
            "    if (enabled) {\n" +
            "        warning.style.display = 'block';\n" +
            "        localStorage.setItem('deviceTrust', sessionId);\n" +
            "    } else {\n" +
            "        warning.style.display = 'none';\n" +
            "        localStorage.removeItem('deviceTrust');\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "document.addEventListener('DOMContentLoaded', () => {\n" +
            "    document.documentElement.style.setProperty('--primary', currentColor);\n" +
            "    document.querySelectorAll('.color-option').forEach(o => {\n" +
            "        if (o.style.background === currentColor) o.classList.add('active');\n" +
            "    });\n" +
            "    if (localStorage.getItem('notifications') === 'true') {\n" +
            "        document.getElementById('notifications-toggle').checked = true;\n" +
            "    }\n" +
            "    if (localStorage.getItem('deviceTrust')) {\n" +
            "        document.getElementById('device-trust-toggle').checked = true;\n" +
            "        document.getElementById('trust-warning').style.display = 'block';\n" +
            "    }\n" +
            "});\n";
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private void sendHtmlResponse(OutputStream out, int statusCode, String html) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendHtmlError(OutputStream out, int statusCode, String title, String message) throws IOException {
        String html = "<!DOCTYPE html><html><head><title>" + title + "</title><style>" +
                "body{font-family:sans-serif;background:#0f172a;color:#e2e8f0;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;}" +
                ".error{text-align:center;padding:40px;background:#1e293b;border-radius:12px;border:1px solid #334155;}" +
                "h1{color:#ef4444;margin-bottom:16px;}" +
                "p{color:#94a3b8;}</style></head><body>" +
                "<div class=\"error\"><h1>" + title + "</h1><p>" + message + "</p></div></body></html>";
        sendHtmlResponse(out, statusCode, html);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    /**
     * Find byte array within another byte array
     */
    private int indexOf(byte[] data, byte[] pattern, int start) {
        outer:
        for (int i = start; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) continue outer;
            }
            return i;
        }
        return -1;
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
                int buildNum = 0;
                try {
                    buildNum = Integer.parseInt(props.getProperty("buildNumber", "0").trim());
                } catch (NumberFormatException ignored) {}
                version.addProperty("buildNumber", buildNum);
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

    /**
     * Send panel version via WebSocket (for gateway mode where HTTP doesn't work).
     */
    private void sendPanelVersionWebSocket(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PANEL_VERSION");

        JsonObject data = new JsonObject();
        // Read version from panel-version.properties file
        try (InputStream in = plugin.getResource("panel-version.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                data.addProperty("version", props.getProperty("version", "UNKNOWN"));
                data.addProperty("buildDate", props.getProperty("buildDate", ""));
                int buildNum = 0;
                try {
                    buildNum = Integer.parseInt(props.getProperty("buildNumber", "0").trim());
                } catch (NumberFormatException ignored) {}
                data.addProperty("buildNumber", buildNum);
                data.addProperty("notes", props.getProperty("notes", ""));
            } else {
                data.addProperty("version", "UNKNOWN");
                data.addProperty("buildDate", "");
                data.addProperty("buildNumber", 0);
                data.addProperty("notes", "Version file not found");
            }
        } catch (Exception e) {
            data.addProperty("version", "ERROR");
            data.addProperty("buildDate", "");
            data.addProperty("buildNumber", 0);
            data.addProperty("notes", "Failed to read version: " + e.getMessage());
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
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
            case "GET_PUNISHMENTS" -> sendPunishments(conn, data, session);
            case "GET_COMMAND_HISTORY" -> sendCommandHistory(conn, data, session);
            case "GET_CHAT_LOGS" -> sendChatLogs(conn, data, session);
            case "GET_AUTOMOD_LOGS" -> sendAutomodLogs(conn, data, session);
            case "GET_ACTIVITY_LOGS" -> sendActivityLogs(conn, data, session);
            case "GET_EVIDENCE_ACTIVITY_LOGS" -> sendEvidenceActivityLogs(conn, data, session);
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(conn);
            case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(conn, data, session);
            case "CREATE_AUTOMOD_RULE" -> createAutomodRule(conn, data, session);
            case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(conn, data, session);
            case "ADD_RULE" -> addServerRule(conn, data, session);
            case "DELETE_RULE" -> deleteServerRule(conn, data, session);
            case "UPDATE_RULES" -> updateServerRules(conn, data, session);
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
            case "GET_TEMPLATES" -> sendTemplates(conn, session);
            case "CREATE_TEMPLATE" -> createTemplate(conn, data, session);
            case "UPDATE_TEMPLATE" -> updateTemplate(conn, data, session);
            case "DELETE_TEMPLATE" -> deleteTemplate(conn, data, session);
            case "TOGGLE_TEMPLATE_FAVORITE" -> toggleTemplateFavorite(conn, data, session);
            case "GET_STATS" -> sendStats(conn);
            case "CREATE_PUNISHMENT" -> createPunishment(conn, data, session);
            case "REVOKE_PUNISHMENT" -> revokePunishment(conn, data, session);
            case "ADD_WATCHLIST", "WATCHLIST_ADD" -> addToWatchlist(conn, data, session);
            case "REMOVE_WATCHLIST", "WATCHLIST_REMOVE" -> removeFromWatchlist(conn, data);
            case "SEND_STAFFCHAT", "STAFFCHAT_MESSAGE" -> sendStaffChatFromPanel(conn, data, session);
            case "GET_STAFFCHAT_HISTORY" -> sendStaffChatHistory(conn, data, session);
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
            case "KICK_ALL", "KICK_ALL_PLAYERS" -> kickAllPlayers(conn, data, session);
            case "KICK_ALL_COUNTDOWN" -> kickAllCountdown(conn, data, session);
            case "KICK_ALL_CANCEL" -> kickAllCancel(conn, session);
            case "SET_LOCKDOWN" -> setLockdown(conn, data, session);
            case "UPDATE_LOCKDOWN_SETTINGS" -> updateLockdownSettings(conn, data, session);
            case "UPDATE_NOTIFICATION_SETTINGS" -> updateNotificationSettings(conn, data, session);
            case "UPDATE_COMMAND_BLACKLIST" -> updateCommandBlacklist(conn, data, session);
            case "GET_CMD_BLACKLIST_ENTRIES" -> sendCmdBlacklistEntries(conn);
            case "ADD_CMD_BLACKLIST_ENTRY" -> addCmdBlacklistEntry(conn, data, session);
            case "REMOVE_CMD_BLACKLIST_ENTRY" -> removeCmdBlacklistEntry(conn, data, session);
            case "GET_REPLAYS" -> sendReplayList(conn);
            case "GET_REPLAY" -> sendReplayData(conn, data);
            case "GET_REPLAY_SETTINGS" -> sendReplaySettings(conn);
            case "GET_SERVER_STATUS" -> sendServerStatus(conn);
            case "TELEPORT_TO_CHUNK" -> teleportToChunk(conn, data, session);
            case "TELEPORT_TO_PLAYER" -> teleportToPlayerByName(conn, data, session);
            case "GET_LUCKPERMS_STATUS" -> sendLuckPermsStatus(conn);
            case "GET_GEYSER_STATUS" -> sendGeyserStatus(conn);
            case "GET_MODERATION_PLUGINS" -> sendModerationPlugins(conn);
            case "GET_SPARK_STATUS" -> sendSparkStatus(conn);
            case "GET_CITIZENS_STATUS" -> sendCitizensStatus(conn);
            case "GET_ESSENTIALS_STATUS" -> sendEssentialsStatus(conn);
            case "GET_PLACEHOLDERAPI_STATUS" -> sendPlaceholderAPIStatus(conn);
            case "GET_VOICECHAT_STATUS" -> sendVoiceChatStatus(conn);

            // Monitoring endpoints
            case "GET_ENTITY_BREAKDOWN" -> sendEntityBreakdown(conn);
            case "GET_CHUNK_BREAKDOWN" -> sendChunkBreakdown(conn);
            case "GET_DIAGNOSTICS" -> sendDiagnostics(conn);
            case "GET_ALERT_HISTORY" -> sendAlertHistory(conn);
            case "UPDATE_ALERT_THRESHOLDS" -> updateAlertThresholds(conn, data, session);
            case "SPARK_PROFILE_START" -> startSparkProfile(conn, session);
            case "SPARK_HEAP_DUMP" -> sparkHeapDump(conn, session);
            case "SPARK_GC" -> sparkTriggerGC(conn, session);

            case "GET_SERVER_SETTINGS" -> sendServerSettings(conn);
            case "UPDATE_MUTE_SETTINGS" -> updateMuteSettings(conn, data, session);
            case "UPDATE_WARN_SETTINGS" -> updateWarnSettings(conn, data, session);
            case "UPDATE_ANTICHEAT_SETTINGS" -> updateAnticheatSettings(conn, data, session);
            case "UPDATE_ACTIVITY_LOG_SETTINGS" -> updateActivityLogSettings(conn, data, session);
            case "UPDATE_EVIDENCE_SETTINGS" -> updateEvidenceSettings(conn, data, session);
            case "GET_DEV_CHECKLIST" -> sendDevChecklist(conn);
            case "TOGGLE_CHECKLIST_ITEM" -> toggleChecklistItem(conn, data, session);
            case "ADD_CHECKLIST_ITEM" -> addChecklistItem(conn, data, session);
            case "DELETE_CHECKLIST_ITEM" -> deleteChecklistItem(conn, data, session);
            case "TRIGGER_PLUGIN_UPDATE" -> triggerPluginUpdate(conn, session);
            case "DEV_STRESS_CREATE_PLAYERS" -> handleDevStressCreatePlayers(conn, data);
            case "DEV_STRESS_CREATE_PUNISHMENTS" -> handleDevStressCreatePunishments(conn, data);
            case "DEV_STRESS_CLEANUP" -> handleDevStressCleanup(conn);
            case "DEV_STRESS_STOP" -> handleDevStressStop(conn);
            case "GET_DATABASE_DEBUG" -> sendDatabaseDebug(conn, data);
            case "IMPORT_MEDAL_CLIP" -> importMedalClip(conn, data, session);
            default -> sendError(conn, "UNKNOWN_TYPE", "Unknown message type: " + type);
        }
    }

    private void sendPlayerList(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PLAYERS_DATA");

        // Get the session to check viewer permissions
        WebPanelSession session = sessions.get(conn);
        UUID viewerUuid = session != null ? session.playerUuid : null;
        boolean canViewIp = hasViewPermission(viewerUuid, "moderex.info.ip");

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
                // Only include IP if viewer has permission
                if (canViewIp) {
                    p.addProperty("ip", onlinePlayer.getAddress() != null ? onlinePlayer.getAddress().getAddress().getHostAddress() : "");
                } else {
                    p.addProperty("ip", ""); // Empty string - no permission
                }
            } else {
                p.addProperty("status", "offline");
                // Only include IP if viewer has permission
                if (canViewIp) {
                    p.addProperty("ip", profile.getIpAddress() != null ? profile.getIpAddress() : "");
                } else {
                    p.addProperty("ip", ""); // Empty string - no permission
                }
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

        // Get the session to check viewer permissions (using new permission names)
        WebPanelSession session = sessions.get(conn);
        UUID viewerUuid = session != null ? session.playerUuid : null;
        boolean canViewIp = hasViewPermission(viewerUuid, "moderex.info.ip");
        boolean canViewNicknames = hasViewPermission(viewerUuid, "moderex.info.nick") ||
                                   hasViewPermission(viewerUuid, "moderex.history.nick");
        boolean canViewCommands = hasViewPermission(viewerUuid, "moderex.history.commands");
        boolean canViewChat = hasViewPermission(viewerUuid, "moderex.history.chat");
        boolean canViewAutomod = hasViewPermission(viewerUuid, "moderex.history.automod");

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

            // Get nickname if player is online and has one (and viewer has permission)
            if (canViewNicknames && offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
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

            // Get player profile info - only include IP if viewer has permission
            var profile = plugin.getPlayerProfileManager().getProfile(playerUuid);
            if (profile != null && canViewIp) {
                details.addProperty("ip", profile.getIpAddress());
            } else if (profile != null) {
                details.addProperty("ip", ""); // Empty - no permission
            }

            // Fetch punishments and recent commands asynchronously
            plugin.getPunishmentManager().getPunishments(playerUuid).thenAccept(punishments -> {
                JsonArray punsArray = new JsonArray();
                for (Punishment p : punishments) {
                    punsArray.add(punishmentToJson(p));
                }
                details.add("punishments", punsArray);

                // Fetch recent commands, chat logs, automod logs, and IP history from database
                // Permission checks are done at the start of this method (canView* flags)
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        // Fetch recent commands (only if viewer has permission)
                        if (canViewCommands) {
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
                        } else {
                            details.add("recentCommands", new JsonArray());
                        }

                        // Fetch activity logs (chat, automod, IP changes) if activity log is enabled
                        if (plugin.getActivityLogManager() != null && plugin.getActivityLogManager().isEnabled()) {
                            int maxChatLogs = plugin.getConfigManager().getSettings().getMaxChatLogs();

                            // Chat logs (only if viewer has permission)
                            if (canViewChat) {
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
                            } else {
                                details.add("chatLogs", new JsonArray());
                            }

                            // Automod logs (only if viewer has permission)
                            if (canViewAutomod) {
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
                            } else {
                                details.add("automodLogs", new JsonArray());
                            }

                            // IP history (only if viewer has permission)
                            if (canViewIp) {
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
                                details.add("ipHistory", new JsonArray());
                            }

                            // Nickname history (only if viewer has permission)
                            if (canViewNicknames) {
                                List<ActivityLogEntry> nickLogs = plugin.getActivityLogManager().getEntries(
                                        playerUuid, List.of(ActivityType.NICKNAME_CHANGE), 0, 1, 20);
                                JsonArray nickArray = new JsonArray();
                                for (ActivityLogEntry entry : nickLogs) {
                                    JsonObject nickEntry = new JsonObject();
                                    nickEntry.addProperty("nick", entry.getContent()); // New nick in content
                                    nickEntry.addProperty("oldNick", entry.getExtra()); // Old nick in extra
                                    nickEntry.addProperty("t", entry.getTimestamp());
                                    nickEntry.addProperty("server", entry.getServer());
                                    nickArray.add(nickEntry);
                                }
                                details.add("nicknameHistory", nickArray);
                            } else {
                                details.add("nicknameHistory", new JsonArray());
                            }
                        } else {
                            details.add("chatLogs", new JsonArray());
                            details.add("automodLogs", new JsonArray());
                            details.add("ipHistory", new JsonArray());
                            details.add("nicknameHistory", new JsonArray());
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

    private void sendPunishments(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 100;
        UUID viewerUuid = session.playerUuid;

        // Check which punishment types the user can view
        boolean canViewBans = hasViewPermission(viewerUuid, "moderex.history.bans");
        boolean canViewMutes = hasViewPermission(viewerUuid, "moderex.history.mutes");
        boolean canViewWarns = hasViewPermission(viewerUuid, "moderex.history.warns");
        boolean canViewKicks = hasViewPermission(viewerUuid, "moderex.history.kicks");

        // If user has no history permissions, send empty list
        if (!canViewBans && !canViewMutes && !canViewWarns && !canViewKicks) {
            plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to view punishment history");
            JsonObject response = new JsonObject();
            response.addProperty("type", "PUNISHMENTS_DATA");
            JsonObject data = new JsonObject();
            data.add("punishments", new JsonArray());
            response.add("data", data);
            conn.send(GSON.toJson(response));
            return;
        }

        plugin.getPunishmentManager().getRecentPunishments(limit).thenAccept(punishments -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "PUNISHMENTS_DATA");

            JsonObject data = new JsonObject();
            JsonArray array = new JsonArray();
            for (Punishment p : punishments) {
                // Filter by permission
                boolean allowed = switch (p.getType()) {
                    case BAN, IPBAN -> canViewBans;
                    case MUTE, IPMUTE -> canViewMutes;
                    case WARN -> canViewWarns;
                    case KICK -> canViewKicks;
                    default -> false;
                };
                if (allowed) {
                    array.add(punishmentToJson(p));
                }
            }
            data.add("punishments", array);
            response.add("data", data);
            conn.send(GSON.toJson(response));
        });
    }

    private void sendCommandHistory(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.history.commands")) {
            plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to view command history");
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to view command history.");
            return;
        }

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

    private void sendChatLogs(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.history.chat")) {
            plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to view chat logs");
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to view chat logs.");
            return;
        }

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

    private void sendAutomodLogs(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.history.automod")) {
            plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to view automod logs");
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to view automod logs.");
            return;
        }

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

    /**
     * Send activity logs from database with permission-based filtering.
     * Only returns log types the user has permission to view.
     */
    private void sendActivityLogs(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (plugin.getActivityLogManager() == null || !plugin.getActivityLogManager().isEnabled()) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "ACTIVITY_LOGS_DATA");
                    JsonObject data = new JsonObject();
                    data.add("logs", new JsonArray());
                    data.addProperty("total", 0);
                    data.addProperty("page", 1);
                    response.add("data", data);
                    conn.send(GSON.toJson(response));
                    return;
                }

                // Build list of allowed activity types based on user permissions
                List<String> allowedTypes = new java.util.ArrayList<>();
                UUID userUuid = session.playerUuid;

                // Chat logs
                if (hasViewPermission(userUuid, "moderex.history.chat")) {
                    allowedTypes.add("CHAT");
                }
                // Command logs
                if (hasViewPermission(userUuid, "moderex.history.commands")) {
                    allowedTypes.add("COMMAND");
                }
                // Ban logs
                if (hasViewPermission(userUuid, "moderex.history.bans")) {
                    allowedTypes.add("PUNISHMENT_BAN");
                    allowedTypes.add("PUNISHMENT_UNBAN");
                    allowedTypes.add("PUNISHMENT_IPBAN");
                }
                // Mute logs
                if (hasViewPermission(userUuid, "moderex.history.mutes")) {
                    allowedTypes.add("PUNISHMENT_MUTE");
                    allowedTypes.add("PUNISHMENT_UNMUTE");
                    allowedTypes.add("PUNISHMENT_IPMUTE");
                }
                // Warn logs
                if (hasViewPermission(userUuid, "moderex.history.warns")) {
                    allowedTypes.add("PUNISHMENT_WARN");
                    allowedTypes.add("PUNISHMENT_UNWARN");
                }
                // Kick logs
                if (hasViewPermission(userUuid, "moderex.history.kicks")) {
                    allowedTypes.add("PUNISHMENT_KICK");
                }
                // Automod logs
                if (hasViewPermission(userUuid, "moderex.history.automod")) {
                    allowedTypes.add("AUTOMOD_TRIGGER");
                    allowedTypes.add("ANTICHEAT_ALERT");
                }
                // Nickname logs
                if (hasViewPermission(userUuid, "moderex.history.nick")) {
                    allowedTypes.add("NICKNAME_CHANGE");
                    allowedTypes.add("USERNAME_CHANGE");
                }
                // Session logs
                if (hasViewPermission(userUuid, "moderex.history.sessions")) {
                    allowedTypes.add("SESSION_JOIN");
                    allowedTypes.add("SESSION_QUIT");
                }

                // If no permissions, return empty
                if (allowedTypes.isEmpty()) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "ACTIVITY_LOGS_DATA");
                    JsonObject data = new JsonObject();
                    data.add("logs", new JsonArray());
                    data.addProperty("total", 0);
                    data.addProperty("page", 1);
                    data.add("allowedTypes", new JsonArray());
                    response.add("data", data);
                    conn.send(GSON.toJson(response));
                    return;
                }

                // Parse filters
                int page = filters.has("page") ? filters.get("page").getAsInt() : 1;
                int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 50;
                String playerFilter = filters.has("player") ? filters.get("player").getAsString().trim() : "";
                String typeFilter = filters.has("type") ? filters.get("type").getAsString().trim().toUpperCase() : "";
                String beforeDate = filters.has("before") && !filters.get("before").isJsonNull() ? filters.get("before").getAsString().trim() : "";
                String afterDate = filters.has("after") && !filters.get("after").isJsonNull() ? filters.get("after").getAsString().trim() : "";
                int offset = (page - 1) * limit;

                // Parse enabledTypes filter (user's toggle preferences)
                List<String> enabledTypesList = new java.util.ArrayList<>();
                if (filters.has("enabledTypes") && !filters.get("enabledTypes").isJsonNull()) {
                    JsonArray enabledTypesArray = filters.getAsJsonArray("enabledTypes");
                    for (int i = 0; i < enabledTypesArray.size(); i++) {
                        String enabledType = enabledTypesArray.get(i).getAsString();
                        // Only include if it's in the allowed types (permission check)
                        if (allowedTypes.contains(enabledType)) {
                            enabledTypesList.add(enabledType);
                        }
                    }
                }

                // Use enabledTypes if provided, otherwise use all allowed types
                List<String> typesToQuery = enabledTypesList.isEmpty() ? allowedTypes : enabledTypesList;

                // If no types to query, return empty
                if (typesToQuery.isEmpty()) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "ACTIVITY_LOGS_DATA");
                    JsonObject data = new JsonObject();
                    data.add("logs", new JsonArray());
                    data.addProperty("total", 0);
                    data.addProperty("page", 1);
                    JsonArray allowedTypesArray = new JsonArray();
                    for (String type : allowedTypes) {
                        allowedTypesArray.add(type);
                    }
                    data.add("allowedTypes", allowedTypesArray);
                    response.add("data", data);
                    conn.send(GSON.toJson(response));
                    return;
                }

                // Build WHERE clause
                StringBuilder whereClause = new StringBuilder("WHERE type IN (");
                List<Object> params = new java.util.ArrayList<>();
                for (int i = 0; i < typesToQuery.size(); i++) {
                    whereClause.append(i > 0 ? ",?" : "?");
                    params.add(typesToQuery.get(i));
                }
                whereClause.append(")");

                // Add player filter
                if (!playerFilter.isEmpty()) {
                    whereClause.append(" AND player_name LIKE ?");
                    params.add("%" + playerFilter + "%");
                }

                // Add before date filter (timestamp < end of that day)
                if (!beforeDate.isEmpty()) {
                    try {
                        java.time.LocalDate date = java.time.LocalDate.parse(beforeDate);
                        // End of day = start of next day
                        long beforeTimestamp = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        whereClause.append(" AND timestamp < ?");
                        params.add(beforeTimestamp);
                    } catch (Exception e) {
                        plugin.logDebug("[ActivityLog] Invalid before date format: " + beforeDate);
                    }
                }

                // Add after date filter (timestamp >= start of that day)
                if (!afterDate.isEmpty()) {
                    try {
                        java.time.LocalDate date = java.time.LocalDate.parse(afterDate);
                        long afterTimestamp = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        whereClause.append(" AND timestamp >= ?");
                        params.add(afterTimestamp);
                    } catch (Exception e) {
                        plugin.logDebug("[ActivityLog] Invalid after date format: " + afterDate);
                    }
                }

                // Add type filter (must be in allowed types)
                if (!typeFilter.isEmpty() && allowedTypes.contains(typeFilter)) {
                    // Override to single type
                    whereClause = new StringBuilder("WHERE type = ?");
                    params.clear();
                    params.add(typeFilter);
                    if (!playerFilter.isEmpty()) {
                        whereClause.append(" AND player_name LIKE ?");
                        params.add("%" + playerFilter + "%");
                    }
                    // Re-add date filters
                    if (!beforeDate.isEmpty()) {
                        try {
                            java.time.LocalDate date = java.time.LocalDate.parse(beforeDate);
                            long beforeTimestamp = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                            whereClause.append(" AND timestamp < ?");
                            params.add(beforeTimestamp);
                        } catch (Exception ignored) {}
                    }
                    if (!afterDate.isEmpty()) {
                        try {
                            java.time.LocalDate date = java.time.LocalDate.parse(afterDate);
                            long afterTimestamp = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                            whereClause.append(" AND timestamp >= ?");
                            params.add(afterTimestamp);
                        } catch (Exception ignored) {}
                    }
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
                        log.addProperty("id", rs.getLong("id"));
                        log.addProperty("timestamp", rs.getLong("timestamp"));
                        log.addProperty("playerUuid", rs.getString("player_uuid"));
                        log.addProperty("playerName", rs.getString("player_name"));
                        log.addProperty("type", rs.getString("type"));
                        log.addProperty("content", rs.getString("content"));
                        log.addProperty("extra", rs.getString("extra"));
                        log.addProperty("server", rs.getString("server"));
                        list.add(log);
                    }
                    return list;
                }, params.toArray());

                // Build response
                JsonObject response = new JsonObject();
                response.addProperty("type", "ACTIVITY_LOGS_DATA");

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

                // Include allowed types so frontend knows what filters to show
                JsonArray allowedTypesArray = new JsonArray();
                for (String type : allowedTypes) {
                    allowedTypesArray.add(type);
                }
                data.add("allowedTypes", allowedTypesArray);

                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to fetch activity logs: " + e.getMessage());
                sendError(conn, "DATABASE_ERROR", "Failed to fetch activity logs");
            }
        });
    }

    /**
     * Send activity logs for evidence selection in punishment form.
     * Fetches logs for specific players with optional date filters.
     */
    private void sendEvidenceActivityLogs(WebSocketConnection conn, JsonObject filters, WebPanelSession session) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (plugin.getActivityLogManager() == null || !plugin.getActivityLogManager().isEnabled()) {
                    sendEmptyEvidenceLogs(conn);
                    return;
                }

                // Build list of allowed activity types based on user permissions
                List<String> allowedTypes = new java.util.ArrayList<>();
                UUID userUuid = session.playerUuid;

                if (hasViewPermission(userUuid, "moderex.history.chat")) {
                    allowedTypes.add("CHAT");
                }
                if (hasViewPermission(userUuid, "moderex.history.commands")) {
                    allowedTypes.add("COMMAND");
                }
                if (hasViewPermission(userUuid, "moderex.history.automod")) {
                    allowedTypes.add("AUTOMOD_TRIGGER");
                    allowedTypes.add("ANTICHEAT_ALERT");
                }

                if (allowedTypes.isEmpty()) {
                    sendEmptyEvidenceLogs(conn);
                    return;
                }

                // Parse filters
                int limit = filters.has("limit") ? filters.get("limit").getAsInt() : 50;
                String beforeDate = filters.has("before") && !filters.get("before").isJsonNull()
                        ? filters.get("before").getAsString().trim() : "";
                String afterDate = filters.has("after") && !filters.get("after").isJsonNull()
                        ? filters.get("after").getAsString().trim() : "";

                // Get player IDs
                List<String> playerIds = new java.util.ArrayList<>();
                if (filters.has("playerIds") && filters.get("playerIds").isJsonArray()) {
                    JsonArray idsArray = filters.getAsJsonArray("playerIds");
                    for (int i = 0; i < idsArray.size(); i++) {
                        playerIds.add(idsArray.get(i).getAsString());
                    }
                }

                if (playerIds.isEmpty()) {
                    sendEmptyEvidenceLogs(conn);
                    return;
                }

                // Build WHERE clause for player UUIDs
                StringBuilder whereClause = new StringBuilder("WHERE type IN (");
                List<Object> params = new java.util.ArrayList<>();

                for (int i = 0; i < allowedTypes.size(); i++) {
                    whereClause.append(i > 0 ? ",?" : "?");
                    params.add(allowedTypes.get(i));
                }
                whereClause.append(") AND player_uuid IN (");

                for (int i = 0; i < playerIds.size(); i++) {
                    whereClause.append(i > 0 ? ",?" : "?");
                    params.add(playerIds.get(i));
                }
                whereClause.append(")");

                // Add date filters
                if (!beforeDate.isEmpty()) {
                    try {
                        java.time.LocalDate date = java.time.LocalDate.parse(beforeDate);
                        long beforeTimestamp = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        whereClause.append(" AND timestamp < ?");
                        params.add(beforeTimestamp);
                    } catch (Exception ignored) {}
                }

                if (!afterDate.isEmpty()) {
                    try {
                        java.time.LocalDate date = java.time.LocalDate.parse(afterDate);
                        long afterTimestamp = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        whereClause.append(" AND timestamp >= ?");
                        params.add(afterTimestamp);
                    } catch (Exception ignored) {}
                }

                String dataQuery = "SELECT * FROM moderex_activity_log " + whereClause +
                        " ORDER BY timestamp DESC LIMIT ?";
                params.add(limit);

                // Get data
                List<JsonObject> logs = plugin.getDatabaseManager().query(dataQuery, rs -> {
                    List<JsonObject> list = new java.util.ArrayList<>();
                    while (rs.next()) {
                        JsonObject log = new JsonObject();
                        log.addProperty("id", rs.getLong("id"));
                        log.addProperty("timestamp", rs.getLong("timestamp"));
                        log.addProperty("playerUuid", rs.getString("player_uuid"));
                        log.addProperty("playerName", rs.getString("player_name"));
                        log.addProperty("type", rs.getString("type"));
                        log.addProperty("content", rs.getString("content"));
                        log.addProperty("extra", rs.getString("extra"));
                        log.addProperty("server", rs.getString("server"));
                        list.add(log);
                    }
                    return list;
                }, params.toArray());

                // Build response
                JsonObject response = new JsonObject();
                response.addProperty("type", "EVIDENCE_ACTIVITY_LOGS_DATA");

                JsonObject data = new JsonObject();
                JsonArray logsArray = new JsonArray();
                for (JsonObject log : logs) {
                    logsArray.add(log);
                }
                data.add("logs", logsArray);
                response.add("data", data);
                conn.send(GSON.toJson(response));

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to fetch evidence activity logs: " + e.getMessage());
                sendEmptyEvidenceLogs(conn);
            }
        });
    }

    private void sendEmptyEvidenceLogs(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "EVIDENCE_ACTIVITY_LOGS_DATA");
        JsonObject data = new JsonObject();
        data.add("logs", new JsonArray());
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    /**
     * Import a video clip from Medal.tv as evidence.
     */
    private void importMedalClip(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String medalUrl = data.has("url") ? data.get("url").getAsString() : null;

        if (medalUrl == null || medalUrl.isEmpty()) {
            sendError(conn, "INVALID_URL", "Medal.tv URL is required");
            return;
        }

        // Validate URL format
        if (!plugin.getEvidenceManager().isMedalUrl(medalUrl)) {
            sendError(conn, "INVALID_URL", "Invalid Medal.tv URL format. Supported: medal.tv/clips/xxx, medal.tv/games/xxx/clips/xxx");
            return;
        }

        // Send starting progress
        JsonObject progressResponse = new JsonObject();
        progressResponse.addProperty("type", "MEDAL_IMPORT_PROGRESS");
        JsonObject progressData = new JsonObject();
        progressData.addProperty("status", "starting");
        progressData.addProperty("progress", 0);
        progressData.addProperty("message", "Fetching Medal clip...");
        progressResponse.add("data", progressData);
        conn.send(GSON.toJson(progressResponse));

        // Import the clip asynchronously
        plugin.getEvidenceManager().importFromMedal(
                medalUrl,
                session.playerUuid,
                session.playerName,
                progress -> {
                    // Send progress updates
                    JsonObject pr = new JsonObject();
                    pr.addProperty("type", "MEDAL_IMPORT_PROGRESS");
                    JsonObject pd = new JsonObject();
                    pd.addProperty("status", "downloading");
                    pd.addProperty("progress", progress);
                    pd.addProperty("message", "Downloading... " + progress + "%");
                    pr.add("data", pd);
                    conn.send(GSON.toJson(pr));
                }
        ).thenAccept(result -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "MEDAL_IMPORT_RESULT");
            JsonObject resultData = new JsonObject();

            if (result.isSuccess()) {
                resultData.addProperty("success", true);
                resultData.add("evidence", result.getEvidence().toJson());
                resultData.addProperty("message", "Medal clip imported successfully!");
            } else {
                resultData.addProperty("success", false);
                resultData.addProperty("error", result.getError());
            }

            response.add("data", resultData);
            conn.send(GSON.toJson(response));
        }).exceptionally(ex -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "MEDAL_IMPORT_RESULT");
            JsonObject resultData = new JsonObject();
            resultData.addProperty("success", false);
            resultData.addProperty("error", "Failed to import clip: " + ex.getMessage());
            response.add("data", resultData);
            conn.send(GSON.toJson(response));
            return null;
        });
    }

    private void sendAutomodRules(WebSocketConnection conn) {
        plugin.logDebug("[WebPanel] sendAutomodRules called");
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTOMOD_RULES_DATA");
        JsonObject data = new JsonObject();
        JsonArray rules = new JsonArray();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            plugin.logDebug("[WebPanel] Sending rule: " + rule.getId() + ", enabled=" + rule.isEnabled() +
                    ", type=" + rule.getType());
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
            // Check permission - requires moderex.automod.edit
            if (!hasViewPermission(session.playerUuid, "moderex.automod.edit")) {
                plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to edit automod rules");
                sendError(conn, "PERMISSION_DENIED", "You do not have permission to edit automod rules.");
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

            // Save rule to database
            plugin.logDebug("[WebPanel] About to save rule: " + rule.getId());
            try {
                plugin.getAutomodManager().saveRule(rule);
                plugin.logDebug("[WebPanel] saveRule completed successfully");
            } catch (Exception saveEx) {
                plugin.logError("[WebPanel] saveRule threw exception", saveEx);
                throw saveEx;
            }

            // Broadcast the updated rule to ALL connected clients (include who updated it so they can skip toast)
            broadcastSingleRuleUpdate(rule, session.playerName);

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
            // Check permission - requires moderex.automod.create
            if (!hasViewPermission(session.playerUuid, "moderex.automod.create")) {
                plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to create automod rules");
                sendError(conn, "PERMISSION_DENIED", "You do not have permission to create automod rules.");
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

            // Save rule to database
            plugin.getAutomodManager().saveRule(rule);

            // Broadcast the new rule creation to ALL connected clients (include who created it so they can skip toast)
            broadcastRuleCreated(rule, session.playerName);

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
            // Check permission - requires moderex.automod.delete
            if (!hasViewPermission(session.playerUuid, "moderex.automod.delete")) {
                plugin.logDebug("[WebPanel] Permission denied for " + session.playerName + " to delete automod rules");
                sendError(conn, "PERMISSION_DENIED", "You do not have permission to delete automod rules.");
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

            // Delete rule from database
            plugin.getAutomodManager().deleteRule(ruleId);

            // Broadcast deletion to ALL connected clients (include who deleted it so they can skip toast)
            broadcastRuleDeleted(ruleId, session.playerName);

            plugin.logDebug("[WebPanel] Automod rule deleted: " + rule.getName() + " by " + session.playerName);
            debugSuccess(DebugCategory.AUTOMOD, "Automod rule deleted",
                    "Rule: " + rule.getName() + ", By: " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "DELETE_ERROR", "Failed to delete rule: " + e.getMessage());
            plugin.logError("Failed to delete automod rule from web panel", e);
            debugError(ErrorCode.AUTOMOD_RULE_DELETE_FAILED, "Error: " + e.getMessage());
        }
    }

    // ===== SERVER RULES HANDLERS =====

    private void addServerRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String title = data.has("title") ? data.get("title").getAsString() : "New Rule";
            String description = data.has("description") ? data.get("description").getAsString() : "";
            String category = data.has("category") ? data.get("category").getAsString() : "General";

            com.blockforge.moderex.rules.Rule rule = new com.blockforge.moderex.rules.Rule();
            rule.setTitle(title);
            rule.setDescription(description);
            rule.setCategory(category);
            rule.setEnabled(true);
            rule.setOrder(plugin.getRulesManager().getRules().size() + 1);
            rule.setCreatedAt(System.currentTimeMillis());
            rule.setUpdatedAt(System.currentTimeMillis());

            plugin.getRulesManager().saveRule(rule).thenAccept(savedRule -> {
                if (savedRule != null) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "RULE_CREATED");
                    JsonObject ruleData = new JsonObject();
                    ruleData.addProperty("id", savedRule.getId());
                    ruleData.addProperty("order", savedRule.getOrder());
                    ruleData.addProperty("title", savedRule.getTitle());
                    ruleData.addProperty("description", savedRule.getDescription());
                    ruleData.addProperty("category", savedRule.getCategory());
                    response.add("data", ruleData);
                    conn.send(GSON.toJson(response));
                    plugin.logDebug("[WebPanel] Server rule created: " + title + " by " + session.playerName);
                }
            });
        } catch (Exception e) {
            sendError(conn, "CREATE_ERROR", "Failed to create server rule: " + e.getMessage());
            plugin.logError("Failed to create server rule from web panel", e);
        }
    }

    private void deleteServerRule(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            int order = data.get("order").getAsInt();

            // Find rule by order
            com.blockforge.moderex.rules.Rule rule = plugin.getRulesManager().getRules().stream()
                    .filter(r -> r.getOrder() == order)
                    .findFirst()
                    .orElse(null);

            if (rule == null) {
                sendError(conn, "NOT_FOUND", "Rule not found with order: " + order);
                return;
            }

            plugin.getRulesManager().deleteRule(rule.getId()).thenAccept(success -> {
                if (success) {
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "RULE_DELETED");
                    JsonObject responseData = new JsonObject();
                    responseData.addProperty("order", order);
                    response.add("data", responseData);
                    conn.send(GSON.toJson(response));
                    plugin.logDebug("[WebPanel] Server rule deleted: " + rule.getTitle() + " by " + session.playerName);
                } else {
                    sendError(conn, "DELETE_ERROR", "Failed to delete rule");
                }
            });
        } catch (Exception e) {
            sendError(conn, "DELETE_ERROR", "Failed to delete server rule: " + e.getMessage());
            plugin.logError("Failed to delete server rule from web panel", e);
        }
    }

    private void updateServerRules(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            if (!data.has("rules")) {
                sendError(conn, "INVALID_DATA", "No rules provided");
                return;
            }

            JsonArray rulesArray = data.getAsJsonArray("rules");
            for (int i = 0; i < rulesArray.size(); i++) {
                JsonObject ruleObj = rulesArray.get(i).getAsJsonObject();
                int order = ruleObj.get("order").getAsInt();

                com.blockforge.moderex.rules.Rule rule = plugin.getRulesManager().getRules().stream()
                        .filter(r -> r.getOrder() == order)
                        .findFirst()
                        .orElse(null);

                if (rule != null) {
                    if (ruleObj.has("title")) rule.setTitle(ruleObj.get("title").getAsString());
                    if (ruleObj.has("description")) rule.setDescription(ruleObj.get("description").getAsString());
                    if (ruleObj.has("category")) rule.setCategory(ruleObj.get("category").getAsString());
                    rule.setUpdatedAt(System.currentTimeMillis());
                    plugin.getRulesManager().saveRule(rule);
                }
            }

            sendSuccess(conn, "Rules updated");
            plugin.logDebug("[WebPanel] Server rules updated by " + session.playerName);
        } catch (Exception e) {
            sendError(conn, "UPDATE_ERROR", "Failed to update server rules: " + e.getMessage());
            plugin.logError("Failed to update server rules from web panel", e);
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
        } catch (Exception e) {
            plugin.logError("Failed to broadcast automod rules: " + e.getMessage(), e);
        }
    }

    /**
     * Broadcast a single rule update to all connected clients.
     * This is more efficient than broadcasting all rules when only one changed.
     * @param rule The updated rule
     * @param by The name of the player who updated it (for toast exclusion)
     */
    public void broadcastSingleRuleUpdate(AutomodRule rule, String by) {
        try {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", "AUTOMOD_RULE_UPDATED");
            JsonObject data = serializeRule(rule);
            data.addProperty("by", by != null ? by : "System");
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

            // Broadcast to gateway connections
            var gatewayClient = plugin.getGatewayClient();
            if (gatewayClient != null && gatewayClient.isConnected()) {
                for (String clientId : gatewaySessions.keySet()) {
                    try {
                        gatewayClient.sendToClient(clientId, broadcast);
                    } catch (Exception e) {
                        plugin.logDebug("Failed to broadcast rule update to gateway client " + clientId + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.logError("Failed to broadcast single rule update: " + e.getMessage(), e);
        }
    }

    /**
     * Broadcast a new rule creation to all connected clients.
     * Sends AUTOMOD_RULE_CREATED so the frontend knows to open the editor.
     * @param rule The created rule
     * @param by The name of the player who created it (for toast exclusion)
     */
    public void broadcastRuleCreated(AutomodRule rule, String by) {
        try {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", "AUTOMOD_RULE_CREATED");
            JsonObject data = serializeRule(rule);
            data.addProperty("by", by != null ? by : "System");
            broadcast.add("data", data);
            String message = GSON.toJson(broadcast);

            plugin.logDebug("[WebPanel] Broadcasting rule creation: " + rule.getId());

            // Broadcast to regular WebSocket connections
            for (WebSocketConnection conn : sessions.keySet()) {
                try {
                    conn.send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast rule creation to connection: " + e.getMessage());
                }
            }

            // Broadcast to gateway connections
            var gatewayClient = plugin.getGatewayClient();
            if (gatewayClient != null && gatewayClient.isConnected()) {
                for (String clientId : gatewaySessions.keySet()) {
                    try {
                        gatewayClient.sendToClient(clientId, broadcast);
                    } catch (Exception e) {
                        plugin.logDebug("Failed to broadcast rule creation to gateway client " + clientId + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.logError("Failed to broadcast rule creation: " + e.getMessage(), e);
        }
    }

    /**
     * Broadcast a rule deletion to all connected clients.
     * @param ruleId The ID of the deleted rule
     * @param by The name of the player who deleted it (for toast exclusion)
     */
    public void broadcastRuleDeleted(String ruleId, String by) {
        try {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", "AUTOMOD_RULE_DELETED");
            JsonObject data = new JsonObject();
            data.addProperty("id", ruleId);
            data.addProperty("by", by != null ? by : "System");
            broadcast.add("data", data);
            String message = GSON.toJson(broadcast);

            plugin.logDebug("[WebPanel] Broadcasting rule deletion: " + ruleId);

            // Broadcast to regular WebSocket connections
            for (WebSocketConnection conn : sessions.keySet()) {
                try {
                    conn.send(message);
                } catch (Exception e) {
                    plugin.logDebug("Failed to broadcast rule deletion to connection: " + e.getMessage());
                }
            }

            // Broadcast to gateway connections
            var gatewayClient = plugin.getGatewayClient();
            if (gatewayClient != null && gatewayClient.isConnected()) {
                for (String clientId : gatewaySessions.keySet()) {
                    try {
                        gatewayClient.sendToClient(clientId, broadcast);
                    } catch (Exception e) {
                        plugin.logDebug("Failed to broadcast rule deletion to gateway client " + clientId + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.logError("Failed to broadcast rule deletion: " + e.getMessage(), e);
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
     * Checks permissions based on the new permission system.
     * Works for both online players (Bukkit) and offline players (LuckPerms).
     */
    private JsonArray getUserPermissions(UUID uuid) {
        JsonArray permissions = new JsonArray();

        // Complete list of permissions to check for web panel functionality
        String[] permissionsToCheck = {
            // Wildcards
            "moderex.*",
            "moderex.command.*",
            "moderex.bypass.*",

            // Punishment permissions
            "moderex.ban",
            "moderex.tempban",
            "moderex.ipban",
            "moderex.mute",
            "moderex.tempmute",
            "moderex.ipmute",
            "moderex.warn",
            "moderex.kick",
            "moderex.punish",

            // Unpunishment permissions
            "moderex.unban",
            "moderex.unmute",
            "moderex.unwarn",
            "moderex.clearwarnings",

            // Punishment modifiers
            "moderex.punish.delete",
            "moderex.punish.modify",

            // Flag permissions
            "moderex.flag.silent",
            "moderex.flag.extrasilent",
            "moderex.flag.public",
            "moderex.flag.global",
            "moderex.flag.hidden",
            "moderex.flag.skip",

            // History permissions
            "moderex.history.*",
            "moderex.history.warns",
            "moderex.history.kicks",
            "moderex.history.bans",
            "moderex.history.mutes",
            "moderex.history.pardons",
            "moderex.history.nick",
            "moderex.history.automod",
            "moderex.history.commands",
            "moderex.history.chat",

            // Player info permissions
            "moderex.info.ip",
            "moderex.info.uuid",
            "moderex.info.nick",
            "moderex.info.joindate",
            "moderex.info.time",
            "moderex.info.namehistory",

            // Commands
            "moderex.command.seen",
            "moderex.command.lastuuid",
            "moderex.command.viewpunishment",
            "moderex.command.staffchat",

            // Staff history
            "moderex.staffhistory",
            "moderex.ipreport",
            "moderex.geoip",
            "moderex.dupeip",

            // Watchlist permissions
            "moderex.watchlist.add",
            "moderex.watchlist.remove",
            "moderex.history.watchlist.*",
            "moderex.history.watchlist.warns",
            "moderex.history.watchlist.kicks",
            "moderex.history.watchlist.bans",
            "moderex.history.watchlist.mutes",
            "moderex.history.watchlist.automod",
            "moderex.history.watchlist.commands",
            "moderex.history.watchlist.chat",

            // Command blacklist permissions
            "moderex.cmdblacklist",
            "moderex.cmdunblacklist",

            // Activity log
            "moderex.log",
            "moderex.log.teleport",

            // Alert permissions
            "moderex.alerts.*",
            "moderex.alerts.punishments",
            "moderex.alerts.automod",
            "moderex.alerts.anticheat",
            "moderex.alerts.staffchat",
            "moderex.alerts.silent",
            "moderex.alerts.joinleave",
            "moderex.alerts.watchlist",
            "moderex.alerts.lag",
            "moderex.alerts.nickname",
            "moderex.alerts.commands",

            // Automod permissions
            "moderex.automod.*",
            "moderex.automod.view",
            "moderex.automod.edit",
            "moderex.automod.create",
            "moderex.automod.delete",

            // Web panel
            "moderex.webpanel",

            // Admin
            "moderex.reload",

            // Staff permission
            "moderex.staff",
            "moderex.staffchat",

            // Template permissions
            "moderex.template.*",
            "moderex.template.create",
            "moderex.template.edit",
            "moderex.template.delete",

            // Mass punishment permissions
            "moderex.mass.*",
            "moderex.masswarn",
            "moderex.massmute",
            "moderex.masskick",
            "moderex.massban",
            "moderex.massunwarn",
            "moderex.massunmute",
            "moderex.massunban"
        };

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // Player is online - check if OP first for direct permission grant
            if (player.isOp()) {
                plugin.logDebug("[WebPanel] Player " + player.getName() + " is OP - granting all permissions");
                for (String perm : permissionsToCheck) {
                    permissions.add(perm);
                }
            } else {
                // Not OP - check each permission individually
                plugin.logDebug("[WebPanel] Checking permissions for online player " + player.getName());
                for (String perm : permissionsToCheck) {
                    if (PermissionUtil.hasPermission(player, perm)) {
                        permissions.add(perm);
                    }
                }
            }
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            // Player is offline - use LuckPerms for permission check
            plugin.logDebug("[WebPanel] Checking permissions via LuckPerms for offline player " + uuid);
            var lpHook = plugin.getHookManager().getLuckPermsHook();
            for (String perm : permissionsToCheck) {
                if (lpHook.hasPermission(uuid, perm)) {
                    permissions.add(perm);
                }
            }
        } else {
            // No way to check permissions for offline player without LuckPerms
            plugin.logDebug("[WebPanel] Cannot check permissions for offline player " + uuid + " - LuckPerms not available");
            // Grant all permissions as fallback for web panel users (they already have webpanel permission)
            plugin.logDebug("[WebPanel] Granting all alert permissions as fallback");
            for (String perm : permissionsToCheck) {
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
            // Player is online - check OP status first, then permissions
            if (player.isOp()) {
                return true;
            }
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
     * Check if user has a specific permission for viewing data.
     * Used to filter data sent to the frontend based on permissions.
     * Supports both new permissions (moderex.info.*, moderex.history.*) and wildcards.
     */
    private boolean hasViewPermission(UUID uuid, String permission) {
        if (uuid == null) return false;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // Online player - check OP or permissions
            if (player.isOp()) return true;
            // Use PermissionUtil which handles wildcards (moderex.*, moderex.info.*, etc.)
            return PermissionUtil.hasPermission(player, permission);
        } else if (plugin.getHookManager().isLuckPermsEnabled()) {
            // Offline player - use LuckPerms with wildcard checking
            var lpHook = plugin.getHookManager().getLuckPermsHook();
            if (lpHook.hasPermission(uuid, permission)) return true;
            if (lpHook.hasPermission(uuid, "moderex.*")) return true;

            // Check category wildcards
            String[] parts = permission.split("\\.");
            StringBuilder wildcardPath = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) wildcardPath.append(".");
                wildcardPath.append(parts[i]);
                if (lpHook.hasPermission(uuid, wildcardPath + ".*")) return true;
            }
            return false;
        } else {
            // Cannot check - allow by default for web panel users
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

    private void sendTemplates(WebSocketConnection conn, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "TEMPLATES");
        JsonArray templates = new JsonArray();

        // Get favorites for this user
        Set<String> favorites = session != null && session.playerUuid != null
                ? plugin.getTemplateManager().getFavorites(session.playerUuid)
                : Set.of();

        // Get templates from database
        for (com.blockforge.moderex.punishment.PunishmentTemplate template : plugin.getTemplateManager().getAllTemplates()) {
            JsonObject json = template.toJson();
            json.addProperty("favorite", favorites.contains(template.getId()));
            templates.add(json);
        }

        response.add("data", templates);
        conn.send(GSON.toJson(response));
    }

    private void sendTemplates(WebSocketConnection conn) {
        sendTemplates(conn, null);
    }

    private void createTemplate(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.template.create")) {
            sendError(conn, "NO_PERMISSION", "You do not have permission to create templates");
            return;
        }

        try {
            String name = data.get("name").getAsString();
            // Accept both 'type' and 'punishmentType' for compatibility
            String typeStr = data.has("punishmentType") ? data.get("punishmentType").getAsString()
                           : data.get("type").getAsString();
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
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.template.edit")) {
            sendError(conn, "NO_PERMISSION", "You do not have permission to edit templates");
            return;
        }

        try {
            String id = data.get("id").getAsString();
            com.blockforge.moderex.punishment.PunishmentTemplate template = plugin.getTemplateManager().getTemplate(id);

            if (template == null) {
                sendError(conn, "NOT_FOUND", "Template not found");
                return;
            }

            // Update fields
            if (data.has("name")) template.setName(data.get("name").getAsString());
            // Accept both 'type' and 'punishmentType' for compatibility
            if (data.has("punishmentType") || data.has("type")) {
                String typeStr = data.has("punishmentType") ? data.get("punishmentType").getAsString()
                               : data.get("type").getAsString();
                template.setType(com.blockforge.moderex.punishment.PunishmentType.valueOf(typeStr.toUpperCase()));
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
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.template.delete")) {
            sendError(conn, "NO_PERMISSION", "You do not have permission to delete templates");
            return;
        }

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

    private void toggleTemplateFavorite(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String templateId = data.has("id") ? data.get("id").getAsString() : null;
            if (templateId == null || templateId.isEmpty()) {
                sendError(conn, "TEMPLATE_ERROR", "Missing template ID");
                return;
            }

            boolean isFavorite = plugin.getTemplateManager().toggleFavorite(session.playerUuid, templateId);

            JsonObject response = new JsonObject();
            response.addProperty("type", "TEMPLATE_FAVORITE_TOGGLED");
            response.addProperty("id", templateId);
            response.addProperty("favorite", isFavorite);
            conn.send(GSON.toJson(response));
        } catch (Exception e) {
            sendError(conn, "TEMPLATE_ERROR", "Failed to toggle favorite: " + e.getMessage());
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

    private void sendDatabaseDebug(WebSocketConnection conn, JsonObject data) {
        String debugType = data.has("type") ? data.get("type").getAsString() : "stats";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "DATABASE_DEBUG_RESPONSE");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("type", debugType);

            try {
                switch (debugType) {
                    case "stats" -> {
                        // Get database file size
                        File dbFile = new File(plugin.getDataFolder(), "moderex.db");
                        String size = dbFile.exists() ? formatFileSize(dbFile.length()) : "Unknown";
                        responseData.addProperty("size", size);
                        responseData.addProperty("dbType", plugin.getConfigManager().getSettings().getDatabaseType());

                        // Get row counts for each table
                        JsonObject tables = new JsonObject();
                        String[] tableNames = {
                            "moderex_punishments", "moderex_warnings", "moderex_activity_log",
                            "moderex_command_blacklist", "moderex_watchlist", "moderex_automod_rules",
                            "moderex_players", "moderex_staff_settings", "moderex_sessions"
                        };
                        for (String table : tableNames) {
                            try {
                                int count = plugin.getDatabaseManager().query(
                                    "SELECT COUNT(*) as cnt FROM " + table,
                                    rs -> rs.next() ? rs.getInt("cnt") : 0
                                );
                                tables.addProperty(table, count);
                            } catch (Exception ignored) {
                                tables.addProperty(table, -1);
                            }
                        }
                        responseData.add("tables", tables);
                    }
                    case "watchlist" -> {
                        JsonArray entries = new JsonArray();
                        // Get watchlist entries from database
                        plugin.getDatabaseManager().query(
                            "SELECT player_uuid, player_name, added_by_name, reason, added_at FROM moderex_watchlist WHERE active = TRUE",
                            rs -> {
                                while (rs.next()) {
                                    JsonObject e = new JsonObject();
                                    e.addProperty("uuid", rs.getString("player_uuid"));
                                    e.addProperty("playerName", rs.getString("player_name"));
                                    e.addProperty("reason", rs.getString("reason"));
                                    e.addProperty("addedBy", rs.getString("added_by_name"));
                                    e.addProperty("addedAt", rs.getLong("added_at"));
                                    entries.add(e);
                                }
                                return null;
                            }
                        );
                        responseData.add("entries", entries);
                    }
                    case "activity_logs" -> {
                        // Get counts by activity type
                        JsonObject counts = new JsonObject();
                        int[] total = {0};
                        for (var type : com.blockforge.moderex.log.ActivityLogEntry.ActivityType.values()) {
                            try {
                                int cnt = plugin.getDatabaseManager().query(
                                    "SELECT COUNT(*) as cnt FROM moderex_activity_log WHERE type = ?",
                                    rs -> rs.next() ? rs.getInt("cnt") : 0,
                                    type.name()
                                );
                                counts.addProperty(type.name(), cnt);
                                total[0] += cnt;
                            } catch (Exception ignored) {}
                        }
                        responseData.add("counts", counts);
                        responseData.addProperty("total", total[0]);
                    }
                    case "automod_alerts" -> {
                        // Get recent automod alerts from database
                        int total = plugin.getDatabaseManager().query(
                            "SELECT COUNT(*) as cnt FROM moderex_activity_log WHERE type = 'AUTOMOD_TRIGGER'",
                            rs -> rs.next() ? rs.getInt("cnt") : 0
                        );
                        responseData.addProperty("total", total);

                        JsonArray entries = plugin.getDatabaseManager().query(
                            "SELECT * FROM moderex_activity_log WHERE type = 'AUTOMOD_TRIGGER' ORDER BY timestamp DESC LIMIT 20",
                            rs -> {
                                JsonArray arr = new JsonArray();
                                while (rs.next()) {
                                    JsonObject entry = new JsonObject();
                                    entry.addProperty("playerUuid", rs.getString("player_uuid"));
                                    entry.addProperty("playerName", rs.getString("player_name"));
                                    entry.addProperty("rule", rs.getString("extra"));
                                    entry.addProperty("content", rs.getString("content"));
                                    entry.addProperty("server", rs.getString("server"));
                                    entry.addProperty("timestamp", rs.getLong("timestamp"));
                                    arr.add(entry);
                                }
                                return arr;
                            }
                        );
                        responseData.add("entries", entries);
                    }
                }
            } catch (Exception e) {
                plugin.logError("Failed to get database debug info", e);
                responseData.addProperty("error", e.getMessage());
            }

            response.add("data", responseData);
            conn.send(GSON.toJson(response));
        });
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private void createPunishment(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String targetName = data.has("playerName") ? data.get("playerName").getAsString() : "";
        String typeStr = data.has("type") ? data.get("type").getAsString().toUpperCase() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "No reason specified";
        String durationStr = data.has("duration") ? data.get("duration").getAsString() : "";

        if (targetName.isEmpty() || typeStr.isEmpty()) {
            sendError(conn, "MISSING_DATA", "Player name and type required");
            return;
        }

        // Parse the duration
        long durationMs = DurationParser.parse(durationStr);
        boolean isPermanent = durationMs <= 0 || durationStr.isEmpty() || durationStr.equalsIgnoreCase("perm") || durationStr.equals("-1");

        // Permission check for punishment type
        UUID staffUuid = session.playerUuid;
        String staffName = session.playerName;
        String requiredPerm = switch (typeStr) {
            case "BAN" -> isPermanent ? "moderex.ban" : "moderex.tempban";
            case "MUTE" -> isPermanent ? "moderex.mute" : "moderex.tempmute";
            case "WARN" -> "moderex.warn";
            case "KICK" -> "moderex.kick";
            case "IPBAN" -> "moderex.ipban";
            default -> null;
        };

        if (requiredPerm == null) {
            sendError(conn, "INVALID_TYPE", "Unknown punishment type: " + typeStr);
            return;
        }

        // Check permission
        if (!hasViewPermission(staffUuid, requiredPerm)) {
            plugin.logDebug("[WebPanel] Permission denied for " + staffName + " to create " + typeStr + " - missing " + requiredPerm);
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to issue " + typeStr.toLowerCase() + " punishments.");
            return;
        }

        // Additional check for temp-only permissions trying to do permanent
        if (isPermanent) {
            if (typeStr.equals("BAN") && !hasViewPermission(staffUuid, "moderex.ban") && hasViewPermission(staffUuid, "moderex.tempban")) {
                plugin.logDebug("[WebPanel] Permission denied for " + staffName + " - has tempban but not permanent ban");
                sendError(conn, "PERMISSION_DENIED", "You only have permission for temporary bans.");
                return;
            }
            if (typeStr.equals("MUTE") && !hasViewPermission(staffUuid, "moderex.mute") && hasViewPermission(staffUuid, "moderex.tempmute")) {
                plugin.logDebug("[WebPanel] Permission denied for " + staffName + " - has tempmute but not permanent mute");
                sendError(conn, "PERMISSION_DENIED", "You only have permission for temporary mutes.");
                return;
            }
        }

        // Get the target player's UUID
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String resolvedName = target.getName() != null ? target.getName() : targetName;

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

        UUID staffUuid = session.playerUuid;
        String staffName = session.playerName;

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

            // Prevent revoking expired punishments
            if (punishment.isExpired()) {
                sendError(conn, "ALREADY_EXPIRED", "Cannot revoke an expired punishment");
                return;
            }

            // Permission check based on punishment type
            String requiredPerm = switch (punishment.getType()) {
                case BAN, IPBAN -> "moderex.unban";
                case MUTE, IPMUTE -> "moderex.unmute";
                case WARN -> "moderex.unwarn";
                default -> null;
            };

            if (requiredPerm == null || !hasViewPermission(staffUuid, requiredPerm)) {
                plugin.logDebug("[WebPanel] Permission denied for " + staffName + " to revoke " +
                    punishment.getType() + " - missing " + requiredPerm);
                sendError(conn, "PERMISSION_DENIED", "You do not have permission to revoke " +
                    punishment.getType().toString().toLowerCase() + " punishments.");
                return;
            }

            // Use removePunishmentByCaseId for proper handling
            plugin.getPunishmentManager().removePunishmentByCaseId(caseId,
                staffUuid, staffName, reason).thenAccept(success -> {

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
        // Support both "uuid" and "playerUuid" field names for backwards compatibility
        String uuid = data.has("uuid") ? data.get("uuid").getAsString() :
                      data.has("playerUuid") ? data.get("playerUuid").getAsString() : "";
        try {
            plugin.getWatchlistManager().removeFromWatchlist(UUID.fromString(uuid));
            sendSuccess(conn, "Removed from watchlist");
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid UUID: " + uuid);
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

    private void sendStaffChatHistory(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Check permission
        if (!hasViewPermission(session.playerUuid, "moderex.staffchat")) {
            sendError(conn, "NO_PERMISSION", "You do not have permission to view staff chat history");
            return;
        }

        int limit = data.has("limit") ? data.get("limit").getAsInt() : 100;
        long beforeTimestamp = data.has("before") && !data.get("before").isJsonNull()
            ? data.get("before").getAsLong() : 0;

        // Cap the limit
        if (limit > 100) limit = 100;
        if (limit < 1) limit = 20;

        final int finalLimit = limit;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                java.util.List<JsonObject> messages = plugin.getDatabaseManager().getStaffChatHistory(finalLimit, beforeTimestamp);

                JsonObject response = new JsonObject();
                response.addProperty("type", "STAFFCHAT_HISTORY");
                com.google.gson.JsonArray messagesArray = new com.google.gson.JsonArray();
                for (JsonObject msg : messages) {
                    messagesArray.add(msg);
                }
                response.add("messages", messagesArray);
                response.addProperty("hasMore", messages.size() == finalLimit);

                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.logError("Failed to get staff chat history", e);
                sendError(conn, "DATABASE_ERROR", "Failed to load staff chat history");
            }
        });
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

        // Server info
        data.addProperty("serverName", settings.getWebPanelServerName());
        data.addProperty("pluginVersion", plugin.getDescription().getVersion());
        data.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        data.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());

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
        muteSettings.addProperty("staffCanSee", settings.isMuteStaffCanSee());
        data.add("muteSettings", muteSettings);

        // Warn settings
        JsonObject warnSettings = new JsonObject();
        warnSettings.addProperty("notify", settings.isWarnNotifyStaff());
        warnSettings.addProperty("autoEscalate", settings.isWarnAutoEscalate());
        warnSettings.addProperty("escalationEnabled", settings.isWarnEscalationEnabled());
        warnSettings.addProperty("escalationWindowDays", settings.getWarnEscalationWindowDays());
        warnSettings.addProperty("resetDays", settings.getWarnResetDays());

        // Warning categories
        JsonArray categoriesArray = new JsonArray();
        for (com.blockforge.moderex.config.Settings.WarnCategory cat : settings.getWarnCategories()) {
            JsonObject catObj = new JsonObject();
            catObj.addProperty("id", cat.getId());
            catObj.addProperty("name", cat.getName());
            catObj.addProperty("points", cat.getPoints());
            categoriesArray.add(catObj);
        }
        warnSettings.add("categories", categoriesArray);

        // Warning escalation tiers
        JsonArray tiersArray = new JsonArray();
        for (com.blockforge.moderex.config.Settings.WarnEscalationTier tier : settings.getWarnEscalationTiers()) {
            JsonObject tierObj = new JsonObject();
            tierObj.addProperty("pointThreshold", tier.getPointThreshold());
            tierObj.addProperty("punishmentType", tier.getPunishmentType());
            tierObj.addProperty("duration", tier.getDuration());
            tierObj.addProperty("reason", tier.getReason());
            tiersArray.add(tierObj);
        }
        warnSettings.add("escalationTiers", tiersArray);

        data.add("warnSettings", warnSettings);

        // Anticheat settings
        JsonObject acSettings = new JsonObject();
        acSettings.addProperty("rebrandAlerts", settings.isAnticheatRebrandAlerts());
        acSettings.addProperty("blockOriginalMessages", settings.isAnticheatBlockOriginalMessages());
        data.add("anticheatSettings", acSettings);

        // Activity log settings
        JsonObject activityLogSettings = new JsonObject();
        activityLogSettings.addProperty("enabled", settings.isActivityLogEnabled());
        activityLogSettings.addProperty("logChat", settings.isActivityLogChat());
        activityLogSettings.addProperty("logCommands", settings.isActivityLogCommands());
        activityLogSettings.addProperty("logSigns", settings.isActivityLogSigns());
        activityLogSettings.addProperty("logItems", settings.isActivityLogItems());
        activityLogSettings.addProperty("logAnvils", settings.isActivityLogAnvils());
        activityLogSettings.addProperty("logSessions", settings.isActivityLogSessions());
        activityLogSettings.addProperty("logUsernames", settings.isActivityLogUsernames());
        activityLogSettings.addProperty("retentionChat", settings.getRetentionChat());
        activityLogSettings.addProperty("retentionCommands", settings.getRetentionCommands());
        activityLogSettings.addProperty("retentionSigns", settings.getRetentionSigns());
        activityLogSettings.addProperty("retentionSessions", settings.getRetentionSessions());
        activityLogSettings.addProperty("retentionItems", settings.getRetentionItems());
        activityLogSettings.addProperty("retentionAnvils", settings.getRetentionAnvils());
        activityLogSettings.addProperty("retentionUsernames", settings.getRetentionUsernames());
        activityLogSettings.addProperty("retentionAutomod", settings.getRetentionAutomod());
        activityLogSettings.addProperty("retentionAnticheat", settings.getRetentionAnticheat());
        data.add("activityLogSettings", activityLogSettings);

        // Evidence settings
        JsonObject evidenceSettings = new JsonObject();
        evidenceSettings.addProperty("maxFileSizeMb", settings.getEvidenceMaxFileSizeMb());
        evidenceSettings.addProperty("maxActivityLogEntries", settings.getEvidenceMaxActivityLogEntries());
        evidenceSettings.addProperty("requireEvidence", settings.isEvidenceRequireEvidence());
        data.add("evidenceSettings", evidenceSettings);

        // Database usage info (for limit tracking)
        var dbManager = plugin.getDatabaseManager();
        var identity = plugin.getServerIdentity();
        data.addProperty("premium", identity != null && identity.isPremium());
        data.addProperty("databaseSizeMb", dbManager.getDatabaseSizeMb());
        data.addProperty("databaseLimitMb", identity != null && identity.isPremium() ? -1 : 25);
        data.addProperty("databaseUsagePercent", dbManager.getUsagePercent() * 100);
        data.addProperty("databaseStatus", dbManager.checkSizeStatus().name());

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void updateMuteSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.mutes")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to configure mute settings.");
            return;
        }

        var settings = plugin.getConfigManager().getSettings();

        // Support both old and new key names
        if (data.has("chat")) settings.setMuteBlocksChat(data.get("chat").getAsBoolean());
        if (data.has("blocksChat")) settings.setMuteBlocksChat(data.get("blocksChat").getAsBoolean());

        if (data.has("msg")) settings.setMuteBlocksMsg(data.get("msg").getAsBoolean());
        if (data.has("blocksMsg")) settings.setMuteBlocksMsg(data.get("blocksMsg").getAsBoolean());

        if (data.has("signs")) settings.setMuteBlocksSigns(data.get("signs").getAsBoolean());
        if (data.has("blocksSigns")) settings.setMuteBlocksSigns(data.get("blocksSigns").getAsBoolean());

        if (data.has("books")) settings.setMuteBlocksBooks(data.get("books").getAsBoolean());
        if (data.has("blocksBooks")) settings.setMuteBlocksBooks(data.get("blocksBooks").getAsBoolean());

        if (data.has("broadcast")) settings.setMuteBlocksBroadcast(data.get("broadcast").getAsBoolean());
        if (data.has("blocksBroadcast")) settings.setMuteBlocksBroadcast(data.get("blocksBroadcast").getAsBoolean());

        if (data.has("voice")) settings.setMuteBlocksVoice(data.get("voice").getAsBoolean());
        if (data.has("blocksVoice")) settings.setMuteBlocksVoice(data.get("blocksVoice").getAsBoolean());

        if (data.has("voiceJoin")) settings.setMuteBlocksVoiceJoin(data.get("voiceJoin").getAsBoolean());

        if (data.has("staffCanSee")) settings.setMuteStaffCanSee(data.get("staffCanSee").getAsBoolean());

        plugin.saveConfig();
        sendSuccess(conn, "Mute settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated mute settings");
    }

    private void updateWarnSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.warnings")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to configure warning settings.");
            return;
        }

        var settings = plugin.getConfigManager().getSettings();

        // Basic settings
        if (data.has("notify")) settings.setWarnNotifyStaff(data.get("notify").getAsBoolean());
        if (data.has("autoEscalate")) settings.setWarnAutoEscalate(data.get("autoEscalate").getAsBoolean());

        // Escalation system settings
        if (data.has("escalationEnabled")) settings.setWarnEscalationEnabled(data.get("escalationEnabled").getAsBoolean());
        if (data.has("escalationWindowDays")) settings.setWarnEscalationWindowDays(data.get("escalationWindowDays").getAsInt());
        if (data.has("resetDays")) settings.setWarnResetDays(data.get("resetDays").getAsInt());

        // Categories
        if (data.has("categories") && data.get("categories").isJsonArray()) {
            java.util.List<com.blockforge.moderex.config.Settings.WarnCategory> categories = new java.util.ArrayList<>();
            for (var elem : data.getAsJsonArray("categories")) {
                if (elem.isJsonObject()) {
                    JsonObject catObj = elem.getAsJsonObject();
                    String id = catObj.has("id") ? catObj.get("id").getAsString() : "";
                    String name = catObj.has("name") ? catObj.get("name").getAsString() : "";
                    int points = catObj.has("points") ? catObj.get("points").getAsInt() : 1;
                    categories.add(new com.blockforge.moderex.config.Settings.WarnCategory(id, name, points));
                }
            }
            settings.setWarnCategories(categories);
        }

        // Escalation tiers
        if (data.has("escalationTiers") && data.get("escalationTiers").isJsonArray()) {
            java.util.List<com.blockforge.moderex.config.Settings.WarnEscalationTier> tiers = new java.util.ArrayList<>();
            for (var elem : data.getAsJsonArray("escalationTiers")) {
                if (elem.isJsonObject()) {
                    JsonObject tierObj = elem.getAsJsonObject();
                    int pointThreshold = tierObj.has("pointThreshold") ? tierObj.get("pointThreshold").getAsInt() : 0;
                    String punishmentType = tierObj.has("punishmentType") ? tierObj.get("punishmentType").getAsString() : "MUTE";
                    String duration = tierObj.has("duration") ? tierObj.get("duration").getAsString() : "1d";
                    String reason = tierObj.has("reason") ? tierObj.get("reason").getAsString() : "Warning threshold reached";
                    tiers.add(new com.blockforge.moderex.config.Settings.WarnEscalationTier(pointThreshold, punishmentType, duration, reason));
                }
            }
            settings.setWarnEscalationTiers(tiers);
        }

        plugin.saveConfig();
        sendSuccess(conn, "Warning settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated warning escalation settings");
    }

    private void updateAnticheatSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        var settings = plugin.getConfigManager().getSettings();

        if (data.has("rebrandAlerts")) settings.setAnticheatRebrandAlerts(data.get("rebrandAlerts").getAsBoolean());
        if (data.has("blockOriginalMessages")) settings.setAnticheatBlockOriginalMessages(data.get("blockOriginalMessages").getAsBoolean());

        sendSuccess(conn, "Anticheat settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated anticheat settings");
    }

    private void updateActivityLogSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.activitylog")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to configure activity log settings.");
            return;
        }

        var settings = plugin.getConfigManager().getSettings();

        // Master enable/disable
        if (data.has("enabled")) settings.setActivityLogEnabled(data.get("enabled").getAsBoolean());

        // Log type toggles
        if (data.has("logChat")) settings.setActivityLogChat(data.get("logChat").getAsBoolean());
        if (data.has("logCommands")) settings.setActivityLogCommands(data.get("logCommands").getAsBoolean());
        if (data.has("logSigns")) settings.setActivityLogSigns(data.get("logSigns").getAsBoolean());
        if (data.has("logItems")) settings.setActivityLogItems(data.get("logItems").getAsBoolean());
        if (data.has("logAnvils")) settings.setActivityLogAnvils(data.get("logAnvils").getAsBoolean());
        if (data.has("logSessions")) settings.setActivityLogSessions(data.get("logSessions").getAsBoolean());
        if (data.has("logUsernames")) settings.setActivityLogUsernames(data.get("logUsernames").getAsBoolean());

        // Retention periods
        if (data.has("retentionChat")) settings.setRetentionChat(data.get("retentionChat").getAsLong());
        if (data.has("retentionCommands")) settings.setRetentionCommands(data.get("retentionCommands").getAsLong());
        if (data.has("retentionSigns")) settings.setRetentionSigns(data.get("retentionSigns").getAsLong());
        if (data.has("retentionSessions")) settings.setRetentionSessions(data.get("retentionSessions").getAsLong());
        if (data.has("retentionItems")) settings.setRetentionItems(data.get("retentionItems").getAsLong());
        if (data.has("retentionAnvils")) settings.setRetentionAnvils(data.get("retentionAnvils").getAsLong());
        if (data.has("retentionUsernames")) settings.setRetentionUsernames(data.get("retentionUsernames").getAsLong());
        if (data.has("retentionAutomod")) settings.setRetentionAutomod(data.get("retentionAutomod").getAsLong());
        if (data.has("retentionAnticheat")) settings.setRetentionAnticheat(data.get("retentionAnticheat").getAsLong());

        plugin.saveConfig();
        sendSuccess(conn, "Activity log settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated activity log settings");
    }

    private void updateEvidenceSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.evidence")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to configure evidence settings.");
            return;
        }

        var settings = plugin.getConfigManager().getSettings();

        if (data.has("maxFileSizeMb")) settings.setEvidenceMaxFileSizeMb(data.get("maxFileSizeMb").getAsInt());
        if (data.has("maxActivityLogEntries")) settings.setEvidenceMaxActivityLogEntries(data.get("maxActivityLogEntries").getAsInt());
        if (data.has("requireEvidence")) settings.setEvidenceRequireEvidence(data.get("requireEvidence").getAsBoolean());

        plugin.saveConfig();
        sendSuccess(conn, "Evidence settings updated");
        broadcastServerSettings();
        plugin.getLogger().info("[WebPanel] " + session.playerName + " updated evidence settings");
    }

    private void broadcastServerSettings() {
        for (WebSocketConnection conn : sessions.keySet()) {
            sendServerSettings(conn);
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

    private void kickAllCountdown(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Server maintenance";
        int seconds = data.has("seconds") ? data.get("seconds").getAsInt() : 10;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component warning = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<dark_gray>[<red><bold>WARNING</bold></red>]</dark_gray> " +
                            "<yellow>All players will be kicked in <red>" + seconds + " seconds</red>!</yellow> " +
                            "<dark_gray>»</dark_gray> <white>" + reason + "</white>"
            );
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(warning);
            }
        });
    }

    private void kickAllCancel(WebSocketConnection conn, WebPanelSession session) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component cancel = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<dark_gray>[<green><bold>CANCELLED</bold></green>]</dark_gray> " +
                            "<gray>Kick all has been cancelled by </gray><gold>" + session.playerName + "</gold>"
            );
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(cancel);
            }
        });
    }

    // ==================== Server Lockdown ====================

    private void setLockdown(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.lockdown")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to manage server lockdown.");
            return;
        }

        boolean enabled = data.has("enabled") && data.get("enabled").getAsBoolean();
        int timer = data.has("timer") ? data.get("timer").getAsInt() : 0;
        String motd = data.has("motd") ? data.get("motd").getAsString() : "";
        String kickMessage = data.has("kickMessage") ? data.get("kickMessage").getAsString() : "Server is under maintenance.";

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var settings = plugin.getConfigManager().getSettings();
            settings.setLockdownEnabled(enabled);

            if (enabled) {
                settings.setLockdownMotd(motd);
                settings.setLockdownKickMessage(kickMessage);

                // Set timer if specified
                if (timer > 0) {
                    long expiresAt = System.currentTimeMillis() + (timer * 60 * 1000L);
                    settings.setLockdownExpiresAt(expiresAt);

                    // Schedule auto-disable
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (settings.isLockdownEnabled() && settings.getLockdownExpiresAt() <= System.currentTimeMillis()) {
                            settings.setLockdownEnabled(false);
                            settings.setLockdownExpiresAt(0);
                            plugin.getConfigManager().saveConfig();
                            broadcastLockdownStatus(false, session.playerName);
                            plugin.getLogger().info("Server lockdown auto-expired");
                        }
                    }, timer * 60 * 20L); // Convert minutes to ticks
                } else {
                    settings.setLockdownExpiresAt(0);
                }
            } else {
                settings.setLockdownExpiresAt(0);
            }

            plugin.getConfigManager().saveConfig();

            // Log activity
            if (plugin.getActivityLogManager() != null) {
                plugin.getActivityLogManager().logLockdown(session.playerUuid, session.playerName, enabled, "server");
            }

            // Broadcast to web panel
            broadcastLockdownStatus(enabled, session.playerName);

            // Notify in-game staff
            String statusText = enabled ? "enabled" : "disabled";
            net.kyori.adventure.text.Component notification = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<dark_gray>[<gold>LOCKDOWN</gold>]</dark_gray> <gray>Server lockdown</gray> " +
                            (enabled ? "<red>enabled</red>" : "<green>disabled</green>") +
                            " <gray>by</gray> <gold>" + session.playerName + "</gold>" +
                            (enabled && timer > 0 ? " <dark_gray>(expires in " + timer + "m)</dark_gray>" : "")
            );

            for (org.bukkit.entity.Player staff : plugin.getServer().getOnlinePlayers()) {
                if (PermissionUtil.hasPermission(staff, "moderex.notify.lockdown")) {
                    staff.sendMessage(notification);
                }
            }

            plugin.getLogger().info(session.playerName + " " + statusText + " server lockdown" +
                    (enabled && timer > 0 ? " for " + timer + " minutes" : ""));

            // Send response
            JsonObject response = new JsonObject();
            response.addProperty("type", "LOCKDOWN_UPDATED");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("enabled", enabled);
            responseData.addProperty("timer", timer);
            response.add("data", responseData);
            conn.send(GSON.toJson(response));
        });
    }

    private void updateLockdownSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.lockdown")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to update lockdown settings.");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var settings = plugin.getConfigManager().getSettings();

            if (data.has("motd")) {
                settings.setLockdownMotd(data.get("motd").getAsString());
            }
            if (data.has("kickMessage")) {
                settings.setLockdownKickMessage(data.get("kickMessage").getAsString());
            }

            plugin.getConfigManager().saveConfig();

            JsonObject response = new JsonObject();
            response.addProperty("type", "LOCKDOWN_SETTINGS_SAVED");
            conn.send(GSON.toJson(response));
        });
    }

    private void broadcastLockdownStatus(boolean enabled, String staffName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOCKDOWN_STATUS");
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("by", staffName);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcast(GSON.toJson(json));
    }

    // ==================== Notification Configuration ====================

    private void updateNotificationSettings(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.notifications")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to update notification settings.");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var settings = plugin.getConfigManager().getSettings();

            if (data.has("joinLeaveVisibility")) {
                String visibility = data.get("joinLeaveVisibility").getAsString();
                settings.setJoinLeaveVisibility(visibility);
            }
            if (data.has("joinLeaveMessages")) {
                settings.setJoinLeaveMessagesEnabled(data.get("joinLeaveMessages").getAsBoolean());
            }
            if (data.has("firstJoinMessages")) {
                settings.setFirstJoinMessagesEnabled(data.get("firstJoinMessages").getAsBoolean());
            }

            plugin.getConfigManager().saveConfig();

            JsonObject response = new JsonObject();
            response.addProperty("type", "NOTIFICATION_SETTINGS_SAVED");
            conn.send(GSON.toJson(response));
        });
    }

    // ==================== Command Blacklist ====================

    private void updateCommandBlacklist(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Permission check
        if (!hasViewPermission(session.playerUuid, "moderex.admin.commandblacklist")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to update command blacklist.");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var settings = plugin.getConfigManager().getSettings();

            if (data.has("commands")) {
                java.util.List<String> commands = new java.util.ArrayList<>();
                data.get("commands").getAsJsonArray().forEach(e -> commands.add(e.getAsString()));
                settings.setBlockedCommands(commands);
            }
            if (data.has("blockMessage")) {
                settings.setCommandBlockMessage(data.get("blockMessage").getAsString());
            }
            if (data.has("enabled")) {
                settings.setCommandBlacklistEnabled(data.get("enabled").getAsBoolean());
            }

            plugin.getConfigManager().saveConfig();

            // Log activity
            if (plugin.getActivityLogManager() != null) {
                plugin.getActivityLogManager().logStaffAction(session.playerUuid, session.playerName,
                        com.blockforge.moderex.log.ActivityLogEntry.ActivityType.STAFF_CMD_BLACKLIST,
                        "Updated command blacklist", null);
            }

            JsonObject response = new JsonObject();
            response.addProperty("type", "COMMAND_BLACKLIST_SAVED");
            JsonObject responseData = new JsonObject();
            responseData.addProperty("count", settings.getBlockedCommands().size());
            response.add("data", responseData);
            conn.send(GSON.toJson(response));

            plugin.getLogger().info(session.playerName + " updated command blacklist (" +
                    settings.getBlockedCommands().size() + " commands)");
        });
    }

    private void sendCmdBlacklistEntries(WebSocketConnection conn) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var entries = plugin.getDatabaseManager().query(
                    "SELECT rowid, player_uuid, command, staff_name, created_at, expires_at, reason FROM moderex_command_blacklist ORDER BY created_at DESC LIMIT 200",
                    rs -> {
                        JsonArray arr = new JsonArray();
                        while (rs.next()) {
                            JsonObject e = new JsonObject();
                            e.addProperty("id", rs.getInt("rowid"));
                            e.addProperty("playerUuid", rs.getString("player_uuid"));
                            e.addProperty("command", rs.getString("command"));
                            e.addProperty("staffName", rs.getString("staff_name"));
                            e.addProperty("createdAt", rs.getLong("created_at"));
                            e.addProperty("expiresAt", rs.getLong("expires_at"));
                            e.addProperty("reason", rs.getString("reason"));
                            arr.add(e);
                        }
                        return arr;
                    }
                );

                // Resolve player names from UUIDs
                JsonObject response = new JsonObject();
                response.addProperty("type", "CMD_BLACKLIST_ENTRIES");
                JsonObject data = new JsonObject();
                data.add("entries", entries);
                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                sendError(conn, "CMD_BLACKLIST_ERROR", "Failed to load command blacklist entries");
                plugin.logError("Failed to load command blacklist entries", e);
            }
        });
    }

    private void addCmdBlacklistEntry(WebSocketConnection conn, JsonObject msgData, WebPanelSession session) {
        if (!hasViewPermission(session.playerUuid, "moderex.cmdblacklist")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to blacklist commands.");
            return;
        }

        String playerName = msgData.has("playerName") ? msgData.get("playerName").getAsString() : null;
        String command = msgData.has("command") ? msgData.get("command").getAsString().toLowerCase() : null;
        long expiresAt = msgData.has("expiresAt") ? msgData.get("expiresAt").getAsLong() : -1;

        if (playerName == null || command == null || command.isEmpty()) {
            sendError(conn, "INVALID_INPUT", "Player name and command are required");
            return;
        }

        if (command.startsWith("/")) command = command.substring(1);
        final String finalCommand = command;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Resolve player UUID
                var offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                if (offlinePlayer.getUniqueId() == null) {
                    sendError(conn, "PLAYER_NOT_FOUND", "Player not found: " + playerName);
                    return;
                }

                plugin.getDatabaseManager().update(
                    "INSERT INTO moderex_command_blacklist (player_uuid, command, staff_uuid, staff_name, created_at, expires_at, reason, server) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    offlinePlayer.getUniqueId().toString(),
                    finalCommand,
                    session.playerUuid != null ? session.playerUuid.toString() : null,
                    session.playerName,
                    System.currentTimeMillis(),
                    expiresAt,
                    "Blacklisted by " + session.playerName + " via web panel",
                    plugin.getServer().getName()
                );

                sendSuccess(conn, "Command /" + finalCommand + " blacklisted for " + playerName);
                sendCmdBlacklistEntries(conn);
            } catch (Exception e) {
                sendError(conn, "CMD_BLACKLIST_ERROR", "Failed to add command blacklist entry");
                plugin.logError("Failed to add command blacklist entry", e);
            }
        });
    }

    private void removeCmdBlacklistEntry(WebSocketConnection conn, JsonObject msgData, WebPanelSession session) {
        if (!hasViewPermission(session.playerUuid, "moderex.cmdunblacklist")) {
            sendError(conn, "PERMISSION_DENIED", "You do not have permission to remove command blacklist entries.");
            return;
        }

        int rowId = msgData.has("id") ? msgData.get("id").getAsInt() : -1;
        if (rowId < 0) {
            sendError(conn, "INVALID_INPUT", "Entry ID is required");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update("DELETE FROM moderex_command_blacklist WHERE rowid = ?", rowId);
                sendSuccess(conn, "Command blacklist entry removed");
                sendCmdBlacklistEntries(conn);
            } catch (Exception e) {
                sendError(conn, "CMD_BLACKLIST_ERROR", "Failed to remove command blacklist entry");
                plugin.logError("Failed to remove command blacklist entry", e);
            }
        });
    }

    private void sendReplaySettings(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "REPLAY_SETTINGS");
        JsonObject data = new JsonObject();

        var replayManager = plugin.getReplayManager();
        var config = plugin.getConfigManager().getSettings();

        data.addProperty("enabled", replayManager != null && config.isReplayEnabled());
        data.addProperty("maxDuration", config.getReplayMaxDurationSeconds());
        data.addProperty("maxStored", config.getReplayMaxStored());
        data.addProperty("triggerOnAnticheat", config.isReplayRecordOnAnticheat());
        data.addProperty("triggerOnWatchlist", config.isReplayRecordWatchlist());
        data.addProperty("nearbyRadius", config.getReplayNearbyRadius());

        // Citizens status
        var hookManager = plugin.getHookManager();
        data.addProperty("citizensAvailable", hookManager != null && hookManager.hasCitizens());

        response.add("data", data);
        conn.send(GSON.toJson(response));
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

            // Include Citizens detection status for the replay tab
            var hookManager = plugin.getHookManager();
            boolean citizensAvailable = hookManager != null && hookManager.hasCitizens();
            data.addProperty("citizensAvailable", citizensAvailable);
            if (citizensAvailable) {
                data.addProperty("citizensVersion", hookManager.getCitizensVersion());
            }

            // Recording stats
            data.addProperty("activeRecordings", plugin.getReplayManager().getActiveRecordingCount());

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

        // Add evidence array
        JsonArray evidenceArray = new JsonArray();

        // Get activity log evidence (PunishmentEvidence)
        List<PunishmentEvidence> activityEvidence = plugin.getPunishmentManager().getPunishmentEvidence(p.getCaseId());
        for (PunishmentEvidence pe : activityEvidence) {
            JsonObject ev = new JsonObject();
            ev.addProperty("id", pe.getId());
            ev.addProperty("type", pe.getEvidenceType().name());
            if (pe.isFile()) {
                ev.addProperty("evidenceId", pe.getEvidenceId());
                // Get file evidence details
                Evidence fileEvidence = plugin.getEvidenceManager().getEvidence(pe.getEvidenceId());
                if (fileEvidence != null) {
                    ev.addProperty("fileName", fileEvidence.getFileName());
                    ev.addProperty("fileType", fileEvidence.getFileType().name());
                    ev.addProperty("fileSize", fileEvidence.getFileSize());
                    ev.addProperty("mimeType", fileEvidence.getMimeType());
                }
            } else if (pe.isActivityLog()) {
                ev.addProperty("activityLogId", pe.getActivityLogId());
                ev.addProperty("snapshot", pe.getActivityLogSnapshot());
            }
            ev.addProperty("addedBy", pe.getAddedByName());
            ev.addProperty("addedAt", pe.getAddedAt());
            evidenceArray.add(ev);
        }

        // Also get file evidence directly linked to punishment (fallback for direct links)
        List<Evidence> fileEvidence = plugin.getEvidenceManager().getEvidenceByPunishment(p.getCaseId());
        for (Evidence fe : fileEvidence) {
            // Check if not already added via PunishmentEvidence
            boolean alreadyAdded = activityEvidence.stream()
                    .anyMatch(pe -> pe.isFile() && fe.getId().equals(pe.getEvidenceId()));
            if (!alreadyAdded) {
                JsonObject ev = new JsonObject();
                ev.addProperty("id", fe.getId());
                ev.addProperty("type", "FILE");
                ev.addProperty("evidenceId", fe.getId());
                ev.addProperty("fileName", fe.getFileName());
                ev.addProperty("fileType", fe.getFileType().name());
                ev.addProperty("fileSize", fe.getFileSize());
                ev.addProperty("mimeType", fe.getMimeType());
                ev.addProperty("addedBy", fe.getUploaderName());
                ev.addProperty("addedAt", fe.getCreatedAt());
                evidenceArray.add(ev);
            }
        }

        json.add("evidence", evidenceArray);

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

    public void broadcastWatchlistAlert(String type, String playerName, String details, String playerUuid) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "WATCHLIST_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("alertType", type);
        data.addProperty("playerName", playerName);
        data.addProperty("details", details);
        data.addProperty("playerUuid", playerUuid);
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
    }

    private void broadcast(String message) {
        // Run broadcasts on dedicated single-thread executor to prevent thread explosion
        // Using a single thread ensures broadcasts are processed sequentially, avoiding thread buildup
        if (broadcastExecutor != null && !broadcastExecutor.isShutdown()) {
            broadcastExecutor.execute(() -> {
                // Broadcast to direct WebSocket connections
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

                // Broadcast to gateway-relayed connections
                var gatewayClient = plugin.getGatewayClient();
                if (gatewayClient != null && gatewayClient.isConnected()) {
                    for (String clientId : gatewaySessions.keySet()) {
                        try {
                            JsonObject jsonMessage = GSON.fromJson(message, JsonObject.class);
                            gatewayClient.sendToClient(clientId, jsonMessage);
                        } catch (Exception e) {
                            plugin.logDebug("[Gateway] Failed to broadcast to client " + clientId + ": " + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    /**
     * Broadcast a JSON message to all connected web panel clients.
     * Implements GatewayMessageHandler interface.
     * Used for admin announcements from the ModereX admin panel.
     *
     * @param message The JSON message to broadcast
     */
    @Override
    public void broadcastToAllClients(JsonObject message) {
        broadcast(GSON.toJson(message));
        plugin.logDebug("[Panel] Broadcast to all clients: " + message.get("type"));
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
        plugin.logDebug("[WebPanel] sendServerStatus called, connection type: " + conn.getClass().getSimpleName());

        var statusManager = plugin.getServerStatusManager();
        if (statusManager == null) {
            JsonObject response = new JsonObject();
            response.addProperty("type", "SERVER_STATUS");
            JsonObject data = new JsonObject();
            data.addProperty("error", "Server status monitoring is not enabled");
            response.add("data", data);
            conn.send(GSON.toJson(response));
            return;
        }

        // Must run on main thread - getStatusJson() accesses Bukkit API
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                JsonObject response = new JsonObject();
                response.addProperty("type", "SERVER_STATUS");
                response.add("data", statusManager.getStatusJson());
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.logDebug("[WebPanel] Error building server status: " + e.getMessage());
                JsonObject response = new JsonObject();
                response.addProperty("type", "SERVER_STATUS");
                JsonObject data = new JsonObject();
                data.addProperty("error", "Failed to collect server status");
                response.add("data", data);
                conn.send(GSON.toJson(response));
            }
        });
    }

    public void broadcastServerStatus(JsonObject statusData) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "SERVER_STATUS");
        json.add("data", statusData);
        broadcast(GSON.toJson(json));
    }

    // ==================== Teleport Actions ====================

    private void teleportToChunk(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Check permission
        if (!hasViewPermission(session.playerUuid, "moderex.command.teleport")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to teleport");
            return;
        }

        String worldName = data.has("world") ? data.get("world").getAsString() : null;
        int chunkX = data.has("x") ? data.get("x").getAsInt() : 0;
        int chunkZ = data.has("z") ? data.get("z").getAsInt() : 0;

        if (worldName == null) {
            sendError(conn, "INVALID_DATA", "Missing world name");
            return;
        }

        // Get online player to teleport
        Player player = Bukkit.getPlayer(session.playerUuid);
        if (player == null) {
            sendError(conn, "NOT_ONLINE", "You must be online to teleport");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sendError(conn, "WORLD_NOT_FOUND", "World not found: " + worldName);
            return;
        }

        // Teleport to chunk center at highest block
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            int blockX = (chunkX << 4) + 8;
            int blockZ = (chunkZ << 4) + 8;
            int blockY = world.getHighestBlockYAt(blockX, blockZ) + 1;
            player.teleport(new org.bukkit.Location(world, blockX + 0.5, blockY, blockZ + 0.5));
        });

        sendSuccess(conn, "Teleporting to chunk " + chunkX + ", " + chunkZ + " in " + worldName);
    }

    private void teleportToPlayerByName(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        // Check permission
        if (!hasViewPermission(session.playerUuid, "moderex.command.teleport")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to teleport");
            return;
        }

        String targetName = data.has("player") ? data.get("player").getAsString() : null;
        if (targetName == null || targetName.isEmpty()) {
            sendError(conn, "INVALID_DATA", "Missing player name");
            return;
        }

        // Get online player to teleport
        Player player = Bukkit.getPlayer(session.playerUuid);
        if (player == null) {
            sendError(conn, "NOT_ONLINE", "You must be online to teleport");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sendError(conn, "PLAYER_NOT_FOUND", "Player not found or offline: " + targetName);
            return;
        }

        // Teleport on main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.teleport(target.getLocation());
        });

        sendSuccess(conn, "Teleporting to " + target.getName());
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

        // Essentials integration
        boolean essentialsAvailable = hookManager != null && hookManager.isEssentialsAvailable();
        data.addProperty("essentialsAvailable", essentialsAvailable);
        if (essentialsAvailable) {
            String essVersion = hookManager.getEssentialsVersion();
            if (essVersion != null) {
                data.addProperty("essentialsVersion", essVersion);
            }
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

    private void sendSparkStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SPARK_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean available = hookManager != null && hookManager.isSparkAvailable();
        data.addProperty("available", available);

        if (available) {
            data.addProperty("version", hookManager.getSparkVersion());
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendCitizensStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "CITIZENS_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean available = hookManager != null && hookManager.hasCitizens();
        data.addProperty("available", available);

        if (available) {
            data.addProperty("version", hookManager.getCitizensVersion());
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendEssentialsStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ESSENTIALS_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean available = hookManager != null && hookManager.isEssentialsAvailable();
        data.addProperty("available", available);

        if (available) {
            String version = hookManager.getEssentialsVersion();
            if (version != null) {
                data.addProperty("version", version);
            }
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendPlaceholderAPIStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PLACEHOLDERAPI_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean available = hookManager != null && hookManager.hasPlaceholderAPI();
        data.addProperty("available", available);

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendVoiceChatStatus(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "VOICECHAT_STATUS");

        JsonObject data = new JsonObject();
        var hookManager = plugin.getHookManager();

        boolean available = hookManager != null && hookManager.isVoiceChatAvailable();
        data.addProperty("available", available);

        if (available) {
            String version = hookManager.getVoiceChatVersion();
            if (version != null) {
                data.addProperty("version", version);
            }
        }

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    // ==================== Monitoring Endpoints ====================

    private void sendEntityBreakdown(WebSocketConnection conn) {
        // Must run on main thread - world.getEntities() is not thread-safe
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                JsonObject response = new JsonObject();
                response.addProperty("type", "ENTITY_BREAKDOWN");

                JsonObject data = new JsonObject();
                JsonArray entities = new JsonArray();
                java.util.Map<String, Integer> entityCounts = new java.util.HashMap<>();
                int totalEntities = 0;

                for (org.bukkit.World world : Bukkit.getWorlds()) {
                    for (org.bukkit.entity.Entity entity : world.getEntities()) {
                        String type = entity.getType().name();
                        entityCounts.merge(type, 1, Integer::sum);
                        totalEntities++;
                    }
                }

                int finalTotal = totalEntities;
                entityCounts.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(25)
                    .forEach(entry -> {
                        JsonObject entityObj = new JsonObject();
                        entityObj.addProperty("type", entry.getKey());
                        entityObj.addProperty("count", entry.getValue());
                        entities.add(entityObj);
                    });

                data.add("entities", entities);
                data.addProperty("total", finalTotal);
                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.logDebug("[WebPanel] Error building entity breakdown: " + e.getMessage());
            }
        });
    }

    private void sendChunkBreakdown(WebSocketConnection conn) {
        // Must run on main thread - world.getLoadedChunks() is not thread-safe
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                JsonObject response = new JsonObject();
                response.addProperty("type", "CHUNK_BREAKDOWN");

                JsonObject data = new JsonObject();
                JsonArray worlds = new JsonArray();

                for (org.bukkit.World world : Bukkit.getWorlds()) {
                    JsonObject worldObj = new JsonObject();
                    worldObj.addProperty("name", world.getName());
                    worldObj.addProperty("chunks", world.getLoadedChunks().length);
                    worldObj.addProperty("entities", world.getEntities().size());
                    worldObj.addProperty("players", world.getPlayers().size());
                    worlds.add(worldObj);
                }

                data.add("worlds", worlds);
                response.add("data", data);
                conn.send(GSON.toJson(response));
            } catch (Exception e) {
                plugin.logDebug("[WebPanel] Error building chunk breakdown: " + e.getMessage());
            }
        });
    }

    private void sendDiagnostics(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "DIAGNOSTICS_DATA");

        JsonObject data = new JsonObject();

        // JVM Arguments
        java.lang.management.RuntimeMXBean runtimeBean = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.util.List<String> jvmArgs = runtimeBean.getInputArguments();
        String argsStr = jvmArgs.stream()
            .filter(arg -> arg.startsWith("-X") || arg.startsWith("-D"))
            .limit(10)
            .collect(java.util.stream.Collectors.joining(" "));
        data.addProperty("jvmArgs", argsStr.isEmpty() ? "Default JVM settings" : argsStr);

        // GC Info
        java.util.List<java.lang.management.GarbageCollectorMXBean> gcBeans =
            java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        String gcType = gcBeans.isEmpty() ? "Unknown" : gcBeans.get(0).getName();
        long totalGcCollections = gcBeans.stream().mapToLong(java.lang.management.GarbageCollectorMXBean::getCollectionCount).sum();
        data.addProperty("gcType", gcType);
        data.addProperty("gcCollections", totalGcCollections);

        // Memory Info
        java.lang.management.MemoryMXBean memoryBean = java.lang.management.ManagementFactory.getMemoryMXBean();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        data.addProperty("heapMax", heapMax + " MB");

        // Thread Info
        java.lang.management.ThreadMXBean threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
        data.addProperty("threadCount", threadBean.getThreadCount());

        // Class Loading
        java.lang.management.ClassLoadingMXBean classBean = java.lang.management.ManagementFactory.getClassLoadingMXBean();
        data.addProperty("loadedClasses", classBean.getLoadedClassCount());

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendAlertHistory(WebSocketConnection conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ALERT_HISTORY");

        JsonObject data = new JsonObject();
        JsonArray alerts = new JsonArray();

        // For now, return empty array - this would be populated from a performance alert manager
        // In a full implementation, this would track TPS drops, memory spikes, etc.
        data.add("alerts", alerts);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void updateAlertThresholds(WebSocketConnection conn, JsonObject msgData, WebPanelSession session) {
        if (session == null || !hasViewPermission(session.playerUuid, "moderex.monitoring.configure.alerts")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to configure alert thresholds");
            return;
        }

        // In a full implementation, these would be saved to config
        double tpsWarning = msgData.has("tpsWarning") ? msgData.get("tpsWarning").getAsDouble() : 18.0;
        double tpsCritical = msgData.has("tpsCritical") ? msgData.get("tpsCritical").getAsDouble() : 15.0;
        double tpsEmergency = msgData.has("tpsEmergency") ? msgData.get("tpsEmergency").getAsDouble() : 10.0;
        int memoryWarning = msgData.has("memoryWarning") ? msgData.get("memoryWarning").getAsInt() : 80;
        int memoryCritical = msgData.has("memoryCritical") ? msgData.get("memoryCritical").getAsInt() : 90;

        // Apply thresholds to the server status manager
        var statusManager = plugin.getServerStatusManager();
        if (statusManager != null) {
            statusManager.setTpsWarningThreshold(tpsWarning);
            statusManager.setTpsCriticalThreshold(tpsCritical);
            statusManager.setTpsEmergencyThreshold(tpsEmergency);
        }

        plugin.logDebug("[Monitoring] Alert thresholds updated - TPS: " + tpsWarning + "/" + tpsCritical + "/" + tpsEmergency +
            " Memory: " + memoryWarning + "/" + memoryCritical);

        sendSuccess(conn, "Alert thresholds updated");
    }

    private void startSparkProfile(WebSocketConnection conn, WebPanelSession session) {
        if (session == null || !hasViewPermission(session.playerUuid, "moderex.monitoring.configure.diagnostics")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to use Spark profiling");
            return;
        }

        var hookManager = plugin.getHookManager();
        if (hookManager == null || !hookManager.isSparkAvailable()) {
            sendError(conn, "SPARK_NOT_AVAILABLE", "Spark is not installed");
            return;
        }

        String result = hookManager.getSparkHook().startProfile();
        if (result != null) {
            sendSuccess(conn, result);
        } else {
            sendError(conn, "SPARK_ERROR", "Failed to start Spark profile");
        }
    }

    private void sparkHeapDump(WebSocketConnection conn, WebPanelSession session) {
        if (session == null || !hasViewPermission(session.playerUuid, "moderex.monitoring.configure.diagnostics")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to use Spark heap dump");
            return;
        }

        var hookManager = plugin.getHookManager();
        if (hookManager == null || !hookManager.isSparkAvailable()) {
            sendError(conn, "SPARK_NOT_AVAILABLE", "Spark is not installed");
            return;
        }

        String result = hookManager.getSparkHook().heapDump();
        if (result != null) {
            sendSuccess(conn, result);
        } else {
            sendError(conn, "SPARK_ERROR", "Failed to generate heap dump");
        }
    }

    private void sparkTriggerGC(WebSocketConnection conn, WebPanelSession session) {
        if (session == null || !hasViewPermission(session.playerUuid, "moderex.monitoring.configure.diagnostics")) {
            sendError(conn, "NO_PERMISSION", "You don't have permission to trigger garbage collection");
            return;
        }

        var hookManager = plugin.getHookManager();
        if (hookManager == null || !hookManager.isSparkAvailable()) {
            sendError(conn, "SPARK_NOT_AVAILABLE", "Spark is not installed");
            return;
        }

        String result = hookManager.getSparkHook().triggerGC();
        if (result != null) {
            sendSuccess(conn, result);
        } else {
            sendError(conn, "SPARK_ERROR", "Failed to trigger garbage collection");
        }
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

        // Protected constructor for wrappers (gateway connections)
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

    // ==================== Gateway Connection Support ====================

    /**
     * Holds information about a gateway-relayed connection.
     */
    private static class GatewayConnection {
        final String clientId;
        final String clientIp;
        final long connectedAt;

        GatewayConnection(String clientId, String clientIp) {
            this.clientId = clientId;
            this.clientIp = clientIp;
            this.connectedAt = System.currentTimeMillis();
        }
    }

    /**
     * Wrapper class to make gateway connections compatible with existing handlers.
     * Sends responses back through the gateway client.
     */
    private class GatewayConnectionWrapper extends WebSocketConnection {
        private final String clientId;
        private final String clientIp;

        GatewayConnectionWrapper(String clientId, String clientIp) {
            super(); // Use protected no-arg constructor
            this.clientId = clientId;
            this.clientIp = clientIp;
        }

        @Override
        void send(String message) {
            sendAsync(message);
        }

        @Override
        boolean sendAsync(String message) {
            var gatewayClient = plugin.getGatewayClient();
            if (gatewayClient != null && gatewayClient.isConnected()) {
                try {
                    JsonObject response = GSON.fromJson(message, JsonObject.class);
                    String responseType = response.has("type") ? response.get("type").getAsString() : "unknown";
                    plugin.getLogger().info("[Gateway] Sending response type '" + responseType + "' to client " + clientId);
                    gatewayClient.sendToClient(clientId, response);
                    return true;
                } catch (Exception e) {
                    plugin.getLogger().warning("[Gateway] Failed to send to client " + clientId + ": " + e.getMessage());
                }
            } else {
                plugin.getLogger().warning("[Gateway] Cannot send to client " + clientId + " - gateway not connected");
            }
            return false;
        }

        @Override
        void close() {
            // Remove from tracking
            gatewayConnections.remove(clientId);
            gatewaySessions.remove(clientId);
        }

        @Override
        String getRemoteAddress() {
            return clientIp;
        }
    }

    /**
     * Implementation of GatewayMessageHandler interface.
     * Handles messages forwarded from the gateway server.
     */
    @Override
    public void handleMessage(String type, com.google.gson.JsonObject message) {
        String clientId = message.has("clientId") ? message.get("clientId").getAsString() : null;
        String clientIp = message.has("clientIp") ? message.get("clientIp").getAsString() : "unknown";

        if (clientId == null) {
            plugin.logDebug("[Gateway] Received message without clientId: " + type);
            return;
        }

        // Create wrapper for sending responses
        GatewayConnectionWrapper wrapper = new GatewayConnectionWrapper(clientId, clientIp);

        // Get or create connection tracking
        gatewayConnections.computeIfAbsent(clientId, id -> new GatewayConnection(id, clientIp));

        // Extract the actual data/payload
        JsonObject data = message.has("data") ? message.getAsJsonObject("data") : new JsonObject();

        try {
            // Handle special gateway-specific types
            if ("browser_connected".equals(type)) {
                plugin.logDebug("[Gateway] Browser connected: " + clientId + " from " + clientIp);
                return;
            }
            if ("browser_disconnected".equals(type)) {
                gatewayConnections.remove(clientId);
                gatewaySessions.remove(clientId);
                plugin.logDebug("[Gateway] Browser disconnected: " + clientId);
                return;
            }

            // Handle authentication types
            if (type.startsWith("AUTH_")) {
                handleGatewayAuth(clientId, wrapper, type, data);
                return;
            }

            // Handle PING/PONG without authentication
            if ("PING".equals(type)) {
                plugin.getLogger().info("[Gateway] Received PING from " + clientId + ", sending PONG");
                JsonObject pong = new JsonObject();
                pong.addProperty("type", "PONG");
                pong.addProperty("timestamp", System.currentTimeMillis());
                wrapper.send(GSON.toJson(pong));
                return;
            }
            if ("PONG".equals(type) || "HEARTBEAT".equals(type)) {
                WebPanelSession session = gatewaySessions.get(clientId);
                if (session != null) {
                    session.lastActivity = System.currentTimeMillis();
                }
                return;
            }

            // Allow GET_SERVER_STATUS before authentication
            if ("GET_SERVER_STATUS".equals(type)) {
                plugin.logDebug("[Gateway] Handling GET_SERVER_STATUS for client: " + clientId);
                sendServerStatus(wrapper);
                return;
            }

            // Allow GET_PANEL_VERSION before authentication
            if ("GET_PANEL_VERSION".equals(type)) {
                plugin.logDebug("[Gateway] Handling GET_PANEL_VERSION for client: " + clientId);
                sendPanelVersionWebSocket(wrapper);
                return;
            }

            // Check if authenticated for all other requests
            WebPanelSession session = gatewaySessions.get(clientId);
            if (session == null) {
                plugin.getLogger().warning("[Gateway] Session not found for client " + clientId + " - request type: " + type);
                sendError(wrapper, "NOT_AUTHENTICATED", "Please authenticate first");
                return;
            }

            // Update activity
            session.lastActivity = System.currentTimeMillis();
            plugin.getLogger().info("[Gateway] Handling request: " + type + " for " + session.playerName);

            // Route to request handler
            handleGatewayRequest(type, data, session, wrapper);

        } catch (Exception e) {
            plugin.logDebug("[Gateway] Error handling message type " + type + ": " + e.getMessage());
            sendError(wrapper, "INTERNAL_ERROR", "An error occurred processing your request");
        }
    }

    /**
     * Handle authentication requests from gateway.
     */
    private void handleGatewayAuth(String clientId, GatewayConnectionWrapper wrapper, String type, JsonObject data) {
        plugin.getLogger().info("[Gateway] Auth request: " + type + " from " + clientId + ", data: " + data.toString());
        switch (type) {
            case "AUTH_CONNECT_CODE" -> {
                String code = data.has("code") ? data.get("code").getAsString().toUpperCase().trim() : "";
                cleanExpiredCodes();

                // Rate limit connect code attempts
                String clientIp = wrapper.getRemoteAddress();
                WebAuthManager authManager = plugin.getWebAuthManager();
                if (authManager != null && authManager.isRateLimited(clientIp)) {
                    long remaining = authManager.getRemainingLockoutSeconds(clientIp);
                    sendAuthFailed(wrapper, "RATE_LIMITED",
                            "Too many failed attempts. Try again in " + remaining + " seconds.");
                    return;
                }

                PendingConnection pending = pendingCodes.remove(code);
                if (pending == null) {
                    if (authManager != null) {
                        authManager.recordFailedAttempt(clientIp);
                    }
                    sendAuthFailed(wrapper, "INVALID_CODE", "Invalid or expired connect code. Use /mx connect in-game.");
                    return;
                }

                if (!pending.hasPermission) {
                    sendAccessDenied(wrapper);
                    return;
                }

                // Create session
                WebPanelSession session = createGatewaySession(pending);
                gatewaySessions.put(clientId, session);
                sendGatewayAuthSuccess(wrapper, session);
                plugin.getLogger().info("[Gateway] Authenticated: " + session.playerName + " via connect code");
            }
            case "AUTH_PERMANENT_TOKEN" -> {
                String token = data.has("token") ? data.get("token").getAsString() : "";
                String clientIp = wrapper.getRemoteAddress();

                if (token.isEmpty()) {
                    sendAuthFailed(wrapper, "INVALID_TOKEN", "No token provided");
                    return;
                }

                WebAuthManager authManager = plugin.getWebAuthManager();
                if (authManager == null) {
                    sendAuthFailed(wrapper, "AUTH_UNAVAILABLE", "Authentication service unavailable");
                    return;
                }

                // Check rate limiting
                if (authManager.isRateLimited(clientIp)) {
                    long remaining = authManager.getRemainingLockoutSeconds(clientIp);
                    sendAuthFailed(wrapper, "RATE_LIMITED",
                            "Too many failed attempts. Try again in " + remaining + " seconds.");
                    return;
                }

                // Validate token - returns player UUID if valid
                UUID playerUuid = authManager.validatePermanentToken(token, clientIp);
                if (playerUuid == null) {
                    sendAuthFailed(wrapper, "INVALID_TOKEN", "Invalid or expired token");
                    return;
                }

                // Check permission
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
                }

                if (!hasPermission) {
                    sendAccessDenied(wrapper);
                    return;
                }

                // Create session
                WebPanelSession session = new WebPanelSession();
                session.playerUuid = playerUuid;
                session.playerName = playerName;
                session.authMethod = "token";
                session.authSessionId = UUID.randomUUID().toString();
                session.hasPermission = true;
                session.prefix = prefix;
                session.suffix = suffix;
                session.connectedAt = System.currentTimeMillis();
                session.lastActivity = System.currentTimeMillis();

                gatewaySessions.put(clientId, session);
                sendGatewayAuthSuccess(wrapper, session);
                plugin.logDebug("[Gateway] Authenticated: " + session.playerName + " via permanent token");
            }
            case "AUTH_SESSION" -> {
                String sessionId = data.has("sessionId") ? data.get("sessionId").getAsString() : "";
                // Look up existing session by ID
                for (Map.Entry<String, WebPanelSession> entry : gatewaySessions.entrySet()) {
                    if (entry.getValue().authSessionId != null && entry.getValue().authSessionId.equals(sessionId)) {
                        WebPanelSession existingSession = entry.getValue();
                        // Transfer to new client ID
                        gatewaySessions.put(clientId, existingSession);
                        sendGatewayAuthSuccess(wrapper, existingSession);
                        plugin.logDebug("[Gateway] Session resumed for: " + existingSession.playerName);
                        return;
                    }
                }
                // Also check regular sessions
                for (WebPanelSession s : sessions.values()) {
                    if (s.authSessionId != null && s.authSessionId.equals(sessionId)) {
                        gatewaySessions.put(clientId, s);
                        sendGatewayAuthSuccess(wrapper, s);
                        plugin.logDebug("[Gateway] Session transferred for: " + s.playerName);
                        return;
                    }
                }
                sendAuthFailed(wrapper, "SESSION_EXPIRED", "Session not found or expired");
            }
            case "AUTH_DEV_UUID_LOGIN" -> {
                // Dev mode: allow login by UUID (for testing)
                String uuidStr = data.has("uuid") ? data.get("uuid").getAsString().trim() : "";

                if (uuidStr.isEmpty()) {
                    sendAuthFailed(wrapper, "INVALID_UUID", "UUID is required");
                    return;
                }

                UUID playerUuid;
                try {
                    playerUuid = UUID.fromString(uuidStr);
                } catch (IllegalArgumentException e) {
                    sendAuthFailed(wrapper, "INVALID_UUID", "Invalid UUID format. Use: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                    return;
                }

                // Get player info
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
                String playerName = offlinePlayer.getName();

                if (playerName == null) {
                    sendAuthFailed(wrapper, "PLAYER_NOT_FOUND", "No player found with this UUID. They must have joined the server at least once.");
                    return;
                }

                // Check if player has webpanel permission
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
                }

                if (!hasPermission) {
                    sendAccessDenied(wrapper);
                    return;
                }

                // Create session
                WebPanelSession session = new WebPanelSession();
                session.playerUuid = playerUuid;
                session.playerName = playerName;
                session.authMethod = "dev_uuid";
                session.authSessionId = UUID.randomUUID().toString();
                session.hasPermission = true;
                session.prefix = prefix;
                session.suffix = suffix;
                session.connectedAt = System.currentTimeMillis();
                session.lastActivity = System.currentTimeMillis();

                gatewaySessions.put(clientId, session);
                sendGatewayAuthSuccess(wrapper, session);
                plugin.getLogger().info("[Gateway] Dev UUID auth for: " + playerName + " (" + playerUuid + ")");
            }
            default -> sendAuthFailed(wrapper, "UNSUPPORTED_AUTH", "Authentication method not supported via gateway: " + type);
        }
    }

    /**
     * Create a session from pending connection data for gateway.
     */
    private WebPanelSession createGatewaySession(PendingConnection pending) {
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
        return session;
    }

    /**
     * Send auth success response for gateway connection.
     */
    private void sendGatewayAuthSuccess(GatewayConnectionWrapper wrapper, WebPanelSession session) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTH_SUCCESS");

        JsonObject authData = new JsonObject();
        authData.addProperty("playerName", session.playerName);
        authData.addProperty("uuid", session.playerUuid.toString());
        authData.addProperty("playerUuid", session.playerUuid.toString());
        authData.addProperty("sessionId", session.authSessionId);
        authData.addProperty("prefix", session.prefix != null ? session.prefix : "");
        authData.addProperty("suffix", session.suffix != null ? session.suffix : "");
        authData.addProperty("authMethod", session.authMethod);

        // Include server info (same as regular auth success)
        authData.addProperty("serverName", plugin.getConfigManager().getSettings().getWebPanelServerName());
        authData.addProperty("pluginVersion", plugin.getDescription().getVersion());
        authData.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        authData.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());

        // Add rank information from LuckPerms (same as regular sendAuth)
        JsonObject rankData = new JsonObject();
        var luckPermsHook = plugin.getHookManager() != null ? plugin.getHookManager().getLuckPermsHook() : null;
        if (luckPermsHook != null) {
            String groupName = luckPermsHook.getPrimaryGroup(session.playerUuid);
            rankData.addProperty("name", groupName != null && !groupName.isEmpty() ? groupName : "default");
            rankData.addProperty("weight", luckPermsHook.getGroupWeight(groupName != null ? groupName : "default"));
            rankData.addProperty("prefix", session.prefix != null ? session.prefix : "");

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
        authData.add("rank", rankData);

        response.add("data", authData);

        wrapper.send(GSON.toJson(response));
    }

    /**
     * Handle authenticated requests from gateway.
     * This should mirror all handlers in the main handleMessage switch.
     */
    private void handleGatewayRequest(String type, JsonObject data, WebPanelSession session, GatewayConnectionWrapper wrapper) {
        try {
            switch (type) {
                // Player data
                case "GET_PLAYERS" -> sendPlayerList(wrapper);
                case "GET_PLAYER_DETAILS" -> sendPlayerDetails(wrapper, data);

                // Punishments
                case "GET_PUNISHMENTS" -> sendPunishments(wrapper, data, session);
                case "CREATE_PUNISHMENT" -> createPunishment(wrapper, data, session);
                case "REVOKE_PUNISHMENT" -> revokePunishment(wrapper, data, session);

                // Logs
                case "GET_COMMAND_HISTORY" -> sendCommandHistory(wrapper, data, session);
                case "GET_CHAT_LOGS" -> sendChatLogs(wrapper, data, session);
                case "GET_AUTOMOD_LOGS" -> sendAutomodLogs(wrapper, data, session);
                case "GET_ACTIVITY_LOGS" -> sendActivityLogs(wrapper, data, session);
                case "GET_EVIDENCE_ACTIVITY_LOGS" -> sendEvidenceActivityLogs(wrapper, data, session);

                // Automod rules
                case "GET_AUTOMOD_RULES" -> sendAutomodRules(wrapper);
                case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(wrapper, data, session);
                case "CREATE_AUTOMOD_RULE" -> createAutomodRule(wrapper, data, session);
                case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(wrapper, data, session);
                case "ADD_RULE" -> addServerRule(wrapper, data, session);
                case "DELETE_RULE" -> deleteServerRule(wrapper, data, session);
                case "UPDATE_RULES" -> updateServerRules(wrapper, data, session);

                // Settings
                case "GET_USER_SETTINGS" -> sendUserSettingsForGateway(wrapper, session);
                case "UPDATE_USER_SETTINGS" -> updateUserSettings(wrapper, data, session);
                case "GET_SETTINGS" -> sendSettings(wrapper);
                case "GET_SERVER_SETTINGS" -> sendServerSettings(wrapper);
                case "UPDATE_MUTE_SETTINGS" -> updateMuteSettings(wrapper, data, session);
                case "UPDATE_WARN_SETTINGS" -> updateWarnSettings(wrapper, data, session);
                case "UPDATE_ANTICHEAT_SETTINGS" -> updateAnticheatSettings(wrapper, data, session);
                case "UPDATE_ACTIVITY_LOG_SETTINGS" -> updateActivityLogSettings(wrapper, data, session);
                case "UPDATE_EVIDENCE_SETTINGS" -> updateEvidenceSettings(wrapper, data, session);
                case "GET_STAFF_ANTICHEAT_SETTINGS" -> sendStaffAnticheatSettings(wrapper, session);
                case "UPDATE_STAFF_ANTICHEAT_SETTING" -> updateStaffAnticheatSetting(wrapper, data, session);
                case "GET_STAFF_ALERT_PREFS" -> sendStaffAlertPrefs(wrapper, session);
                case "UPDATE_STAFF_ALERT_PREF" -> updateStaffAlertPref(wrapper, data, session);
                case "GET_ALERT_PRESETS" -> sendAlertPresets(wrapper);

                // Templates
                case "GET_TEMPLATES" -> sendTemplates(wrapper, session);
                case "CREATE_TEMPLATE" -> createTemplate(wrapper, data, session);
                case "UPDATE_TEMPLATE" -> updateTemplate(wrapper, data, session);
                case "DELETE_TEMPLATE" -> deleteTemplate(wrapper, data, session);
                case "TOGGLE_TEMPLATE_FAVORITE" -> toggleTemplateFavorite(wrapper, data, session);

                // Watchlist - support both old and new naming
                case "GET_WATCHLIST" -> sendWatchlist(wrapper);
                case "ADD_WATCHLIST", "WATCHLIST_ADD", "ADD_TO_WATCHLIST" -> addToWatchlist(wrapper, data, session);
                case "REMOVE_WATCHLIST", "WATCHLIST_REMOVE", "REMOVE_FROM_WATCHLIST" -> removeFromWatchlist(wrapper, data);

                // Staff chat
                case "SEND_STAFFCHAT", "STAFFCHAT_MESSAGE" -> {
                    String msg = data.has("message") ? data.get("message").getAsString() : "";
                    plugin.getStaffChatManager().broadcastFromWebPanel(session.playerName, msg);
                    sendSuccess(wrapper, "Message sent");
                }
                case "GET_STAFFCHAT_HISTORY" -> sendStaffChatHistory(wrapper, data, session);

                // Chat controls
                case "GET_CHAT_STATUS" -> sendChatStatus(wrapper);
                case "SET_CHAT_LOCK" -> setChatLock(wrapper, data, session);
                case "SET_SLOWMODE" -> setSlowmode(wrapper, data, session);
                case "CLEAR_CHAT" -> clearChat(wrapper, session);

                // Player actions
                case "KICK_PLAYER" -> kickPlayer(wrapper, data, session);
                case "KICK_ALL", "KICK_ALL_PLAYERS" -> kickAllPlayers(wrapper, data, session);
                case "KICK_ALL_COUNTDOWN" -> kickAllCountdown(wrapper, data, session);
                case "KICK_ALL_CANCEL" -> kickAllCancel(wrapper, session);

                // Stats and status
                case "GET_STATS" -> sendStats(wrapper);
                case "GET_SERVER_STATUS" -> sendServerStatus(wrapper);
                case "TELEPORT_TO_CHUNK" -> teleportToChunk(wrapper, data, session);
                case "TELEPORT_TO_PLAYER" -> teleportToPlayerByName(wrapper, data, session);
                case "GET_LUCKPERMS_STATUS" -> sendLuckPermsStatus(wrapper);
                case "GET_GEYSER_STATUS" -> sendGeyserStatus(wrapper);
                case "GET_MODERATION_PLUGINS" -> sendModerationPlugins(wrapper);
                case "GET_SPARK_STATUS" -> sendSparkStatus(wrapper);
                case "GET_CITIZENS_STATUS" -> sendCitizensStatus(wrapper);
                case "GET_ESSENTIALS_STATUS" -> sendEssentialsStatus(wrapper);
                case "GET_PLACEHOLDERAPI_STATUS" -> sendPlaceholderAPIStatus(wrapper);
                case "GET_VOICECHAT_STATUS" -> sendVoiceChatStatus(wrapper);

                // Monitoring endpoints
                case "GET_ENTITY_BREAKDOWN" -> sendEntityBreakdown(wrapper);
                case "GET_CHUNK_BREAKDOWN" -> sendChunkBreakdown(wrapper);
                case "GET_DIAGNOSTICS" -> sendDiagnostics(wrapper);
                case "GET_ALERT_HISTORY" -> sendAlertHistory(wrapper);
                case "UPDATE_ALERT_THRESHOLDS" -> updateAlertThresholds(wrapper, data, session);
                case "SPARK_PROFILE_START" -> startSparkProfile(wrapper, session);
                case "SPARK_HEAP_DUMP" -> sparkHeapDump(wrapper, session);
                case "SPARK_GC" -> sparkTriggerGC(wrapper, session);

                // Anticheat
                case "GET_ANTICHEAT_INFO" -> sendAnticheatInfo(wrapper);
                case "GET_ANTICHEAT_ALERTS" -> sendAnticheatAlerts(wrapper);
                case "GET_ANTICHEAT_CHECKS" -> sendAnticheatChecks(wrapper);

                // Dev checklist
                case "GET_DEV_CHECKLIST" -> sendDevChecklist(wrapper);
                case "TOGGLE_CHECKLIST_ITEM" -> toggleChecklistItem(wrapper, data, session);
                case "ADD_CHECKLIST_ITEM" -> addChecklistItem(wrapper, data, session);
                case "DELETE_CHECKLIST_ITEM" -> deleteChecklistItem(wrapper, data, session);

                // Server actions
                case "SET_LOCKDOWN" -> setLockdown(wrapper, data, session);
                case "UPDATE_LOCKDOWN_SETTINGS" -> updateLockdownSettings(wrapper, data, session);
                case "UPDATE_NOTIFICATION_SETTINGS" -> updateNotificationSettings(wrapper, data, session);
                case "UPDATE_COMMAND_BLACKLIST" -> updateCommandBlacklist(wrapper, data, session);
                case "GET_CMD_BLACKLIST_ENTRIES" -> sendCmdBlacklistEntries(wrapper);
                case "ADD_CMD_BLACKLIST_ENTRY" -> addCmdBlacklistEntry(wrapper, data, session);
                case "REMOVE_CMD_BLACKLIST_ENTRY" -> removeCmdBlacklistEntry(wrapper, data, session);

                // Replays
                case "GET_REPLAYS" -> sendReplayList(wrapper);
                case "GET_REPLAY" -> sendReplayData(wrapper, data);
                case "GET_REPLAY_SETTINGS" -> sendReplaySettings(wrapper);

                // Trusted devices
                case "CLEAR_TRUSTED_DEVICES" -> clearTrustedDevices(wrapper, session);
                case "GET_TRUSTED_DEVICE_COUNT" -> sendTrustedDeviceCount(wrapper, session);

                // Changelog
                case "MARK_CHANGELOG_READ" -> markChangelogRead(wrapper, data, session);

                // External imports
                case "GET_EXTERNAL_PUNISHMENTS" -> getExternalPunishments(wrapper, data);
                case "IMPORT_EXTERNAL_PUNISHMENTS" -> importExternalPunishments(wrapper, data, session);
                case "IMPORT_MEDAL_CLIP" -> importMedalClip(wrapper, data, session);

                // Debug
                case "GET_DATABASE_DEBUG" -> sendDatabaseDebug(wrapper, data);

                // Panel version (for gateway mode)
                case "GET_PANEL_VERSION" -> sendPanelVersionWebSocket(wrapper);

                // Evidence (for gateway mode - uses Base64 over WebSocket)
                case "UPLOAD_EVIDENCE_WS" -> handleEvidenceUploadWebSocket(wrapper, data, session);
                case "GET_EVIDENCE_FILE" -> handleGetEvidenceFileWebSocket(wrapper, data);

                default -> sendError(wrapper, "UNKNOWN_TYPE", "Unknown message type: " + type);
            }
        } catch (Exception e) {
            plugin.logError("[Gateway] Error handling request type " + type + ": " + e.getMessage(), e);
            sendError(wrapper, "INTERNAL_ERROR", "An error occurred: " + e.getMessage());
        }
    }

    /**
     * Send user settings for gateway connection.
     */
    private void sendUserSettingsForGateway(GatewayConnectionWrapper wrapper, WebPanelSession session) {
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
        plugin.logDebug("[Gateway] User " + session.playerName + " permissions: " + permissions);

        response.add("data", data);
        wrapper.send(GSON.toJson(response));
        plugin.logDebug("[Gateway] Sent user settings with " + readChangelogs.size() + " read changelogs to " + session.playerName);
    }

    /**
     * Broadcast a message to all gateway-connected clients.
     */
    public void broadcastToGateway(String jsonMessage) {
        if (gatewaySessions.isEmpty()) return;

        var gatewayClient = plugin.getGatewayClient();
        if (gatewayClient == null || !gatewayClient.isConnected()) return;

        try {
            JsonObject data = GSON.fromJson(jsonMessage, JsonObject.class);
            gatewayClient.broadcast(data);
        } catch (Exception e) {
            plugin.logDebug("[Gateway] Failed to broadcast: " + e.getMessage());
        }
    }

    /**
     * Get the number of connected gateway clients.
     */
    public int getGatewayConnectionCount() {
        return gatewayConnections.size();
    }

    // ========== Evidence WebSocket Handlers (for gateway mode) ==========

    /**
     * Handle evidence upload via WebSocket for gateway mode.
     * Uses Base64 encoding to transfer binary data over WebSocket.
     */
    private void handleEvidenceUploadWebSocket(WebSocketConnection conn, JsonObject data, WebPanelSession session) {
        try {
            String fileName = data.has("fileName") ? data.get("fileName").getAsString() : "unknown";
            String base64Data = data.has("data") ? data.get("data").getAsString() : "";
            String punishmentId = data.has("punishmentId") ? data.get("punishmentId").getAsString() : null;

            if (base64Data.isEmpty()) {
                sendError(conn, "INVALID_DATA", "No file data provided");
                return;
            }

            // Decode Base64 data
            byte[] fileBytes;
            try {
                fileBytes = java.util.Base64.getDecoder().decode(base64Data);
            } catch (IllegalArgumentException e) {
                sendError(conn, "INVALID_DATA", "Invalid Base64 encoding");
                return;
            }

            plugin.logDebug("[Evidence] WebSocket upload from " + session.playerName + ": " + fileName + " (" + fileBytes.length + " bytes)");

            // Upload through evidence manager
            com.blockforge.moderex.evidence.Evidence evidence = plugin.getEvidenceManager()
                    .uploadEvidence(session.playerUuid, session.playerName, fileName,
                            new java.io.ByteArrayInputStream(fileBytes), fileBytes.length)
                    .get();

            if (evidence == null) {
                sendError(conn, "UPLOAD_FAILED", "Failed to save evidence. Check file type and size.");
                return;
            }

            // If punishmentId is provided, link evidence to punishment
            if (punishmentId != null && !punishmentId.isEmpty()) {
                evidence.setLinkedPunishmentId(punishmentId);
                plugin.getEvidenceManager().updateEvidence(evidence);
            }

            // Send success response
            JsonObject response = new JsonObject();
            response.addProperty("type", "EVIDENCE_UPLOADED");
            JsonObject respData = new JsonObject();
            respData.addProperty("evidenceId", evidence.getId());
            respData.addProperty("fileName", evidence.getFileName());
            respData.addProperty("fileType", evidence.getFileType().name());
            respData.addProperty("fileSize", evidence.getFileSize());
            if (punishmentId != null) {
                respData.addProperty("punishmentId", punishmentId);
            }
            response.add("data", respData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[Evidence] Upload successful: " + evidence.getId() + " - " + evidence.getFileName());
        } catch (Exception e) {
            plugin.logError("[Evidence] WebSocket upload error: " + e.getMessage(), e);
            sendError(conn, "UPLOAD_FAILED", "Upload failed: " + e.getMessage());
        }
    }

    /**
     * Handle evidence file retrieval via WebSocket for gateway mode.
     * Sends file data as Base64 over WebSocket.
     */
    private void handleGetEvidenceFileWebSocket(WebSocketConnection conn, JsonObject data) {
        try {
            String fileId = data.has("fileId") ? data.get("fileId").getAsString() : "";

            if (fileId.isEmpty()) {
                sendError(conn, "INVALID_REQUEST", "No file ID provided");
                return;
            }

            // Get evidence metadata
            com.blockforge.moderex.evidence.Evidence evidence = plugin.getEvidenceManager().getEvidence(fileId);
            if (evidence == null) {
                sendError(conn, "NOT_FOUND", "Evidence not found");
                return;
            }

            // Get the file path
            java.nio.file.Path filePath = plugin.getEvidenceManager().getEvidenceFile(fileId);
            if (filePath == null || !java.nio.file.Files.exists(filePath)) {
                sendError(conn, "FILE_NOT_FOUND", "Evidence file not found on disk");
                return;
            }

            // Read file and convert to Base64
            byte[] fileBytes = java.nio.file.Files.readAllBytes(filePath);
            String base64Data = java.util.Base64.getEncoder().encodeToString(fileBytes);

            // Determine MIME type from FileType enum
            String mimeType = evidence.getFileType() != null
                    ? evidence.getFileType().getMimeType()
                    : "application/octet-stream";

            // Send response
            JsonObject response = new JsonObject();
            response.addProperty("type", "EVIDENCE_FILE");
            JsonObject respData = new JsonObject();
            respData.addProperty("fileId", fileId);
            respData.addProperty("fileName", evidence.getFileName());
            respData.addProperty("mimeType", mimeType);
            respData.addProperty("fileSize", evidence.getFileSize());
            respData.addProperty("data", base64Data);
            response.add("data", respData);
            conn.send(GSON.toJson(response));

            plugin.logDebug("[Evidence] Sent file via WebSocket: " + fileId + " (" + fileBytes.length + " bytes)");
        } catch (Exception e) {
            plugin.logError("[Evidence] WebSocket file retrieval error: " + e.getMessage(), e);
            sendError(conn, "RETRIEVAL_FAILED", "Failed to retrieve evidence: " + e.getMessage());
        }
    }
}
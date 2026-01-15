package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SamePortPanelHandler implements HttpRequestHandler {

    private static final Gson GSON = new Gson();
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

    public SamePortPanelHandler(ModereX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(ChannelHandlerContext ctx, String method, String path, String headers, ByteBuf body) {
        // Parse and normalize path before lambda (must be effectively final)
        String normalizedPath = path;
        if (normalizedPath == null) normalizedPath = "/";
        int queryIdx = normalizedPath.indexOf('?');
        if (queryIdx > 0) normalizedPath = normalizedPath.substring(0, queryIdx);
        final String requestPath = normalizedPath;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Handle OPTIONS (CORS preflight)
                if ("OPTIONS".equals(method)) {
                    sendCorsResponse(ctx);
                    return;
                }

                // Handle POST /api/ai/chat
                if ("POST".equals(method) && requestPath.equals("/api/ai/chat")) {
                    handleAiChat(ctx, body);
                    return;
                }

                // Handle GET /api/config
                if (requestPath.equals("/api/config")) {
                    handleConfigRequest(ctx, headers);
                    return;
                }

                // Serve static files
                serveStaticFile(ctx, requestPath);

            } catch (Exception e) {
                plugin.logDebug("[SamePort] Error handling request: " + e.getMessage());
                sendError(ctx, 500, "Internal Server Error");
            }
        });
    }

    @Override
    public void handleWebSocketUpgrade(ChannelHandlerContext ctx, String path, String headers, String wsKey) {
        // WebSocket upgrade - send to main WebSocket handler
        // For now, reject and let users use the separate port for WebSocket
        // Full WebSocket support through same-port requires more complex state management
        plugin.logDebug("[SamePort] WebSocket upgrade requested but not supported in same-port mode");
        sendError(ctx, 501, "WebSocket not supported on same port. Use the dedicated panel port.");
    }

    private void handleConfigRequest(ChannelHandlerContext ctx, String headers) {
        JsonObject config = new JsonObject();

        String host = plugin.getConfigManager().getSettings().getWebPanelHost();
        if (host == null || host.isEmpty()) {
            // Try to extract from Host header
            String hostHeader = extractHeader(headers, "host");
            if (hostHeader != null) {
                int idx = hostHeader.indexOf(':');
                host = idx > 0 ? hostHeader.substring(0, idx) : hostHeader;
            } else {
                host = "localhost";
            }
        }

        config.addProperty("host", host);
        config.addProperty("wsPort", plugin.getConfigManager().getSettings().getWebPanelPort());
        config.addProperty("serverName", plugin.getConfigManager().getSettings().getWebPanelServerName());
        config.addProperty("serverVersion", plugin.getDescription().getVersion());
        config.addProperty("samePortMode", true);

        // AI configuration
        config.addProperty("aiEnabled", plugin.getConfigManager().getSettings().isAiEnabled());
        config.addProperty("aiModel", plugin.getConfigManager().getSettings().getAiModel());

        sendJson(ctx, 200, GSON.toJson(config));
    }

    private void handleAiChat(ChannelHandlerContext ctx, ByteBuf body) {
        // Check if AI is enabled
        if (!plugin.getConfigManager().getSettings().isAiEnabled()) {
            sendError(ctx, 503, "AI assistant is disabled");
            return;
        }

        try {
            String requestBody = body != null ? body.toString(StandardCharsets.UTF_8) : "";
            String aiEndpoint = plugin.getConfigManager().getSettings().getAiEndpoint();
            String aiApiKey = plugin.getConfigManager().getSettings().getAiApiKey();

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
            String responseBody = readStream(responseStream);
            responseStream.close();

            if (responseCode >= 200 && responseCode < 300) {
                sendJson(ctx, 200, responseBody);
            } else {
                sendError(ctx, responseCode, "AI API Error");
            }
        } catch (Exception e) {
            plugin.logDebug("[SamePort] AI API error: " + e.getMessage());
            sendError(ctx, 502, "AI service unavailable");
        }
    }

    private void serveStaticFile(ChannelHandlerContext ctx, String path) {
        String filePath = path;
        if (filePath.equals("/") || filePath.isEmpty()) filePath = "/index.html";
        if (filePath.contains("..")) {
            sendError(ctx, 403, "Forbidden");
            return;
        }

        String resourcePath = "panel" + filePath;
        String ext = "";
        int dot = filePath.lastIndexOf('.');
        if (dot > 0) ext = filePath.substring(dot + 1).toLowerCase();
        String contentType = MIME_TYPES.getOrDefault(ext, "application/octet-stream");

        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                byte[] data = readAllBytes(in);
                sendResponse(ctx, 200, contentType, data);
            } else {
                sendError(ctx, 404, "Not Found");
            }
        } catch (Exception e) {
            plugin.logDebug("[SamePort] Error serving file: " + e.getMessage());
            sendError(ctx, 500, "Internal Server Error");
        }
    }

    private void sendResponse(ChannelHandlerContext ctx, int status, String contentType, byte[] body) {
        String statusText = status == 200 ? "OK" : status == 404 ? "Not Found" : "Error";
        String headerStr = "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        byte[] headerBytes = headerStr.getBytes(StandardCharsets.UTF_8);
        // Pre-allocate buffer with correct size to avoid capacity overflow
        ByteBuf buf = Unpooled.buffer(headerBytes.length + body.length);
        buf.writeBytes(headerBytes);
        buf.writeBytes(body);
        ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendJson(ChannelHandlerContext ctx, int status, String json) {
        sendResponse(ctx, status, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
    }

    private void sendError(ChannelHandlerContext ctx, int status, String message) {
        String body = "{\"error\":\"" + message + "\"}";
        sendJson(ctx, status, body);
    }

    private void sendCorsResponse(ChannelHandlerContext ctx) {
        String response = "HTTP/1.1 204 No Content\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(response.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    private String extractHeader(String headers, String name) {
        if (headers == null) return null;
        String lowerHeaders = headers.toLowerCase();
        String lowerName = name.toLowerCase() + ":";
        int idx = lowerHeaders.indexOf(lowerName);
        if (idx < 0) return null;

        int start = idx + lowerName.length();
        int end = headers.indexOf("\r\n", start);
        if (end < 0) end = headers.length();

        return headers.substring(start, end).trim();
    }

    private String readStream(InputStream in) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    private byte[] readAllBytes(InputStream in) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toByteArray();
    }
}

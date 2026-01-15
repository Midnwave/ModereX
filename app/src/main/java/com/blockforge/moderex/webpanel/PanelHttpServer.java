package com.blockforge.moderex.webpanel;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class PanelHttpServer {

    private static final Gson GSON = new Gson();
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html; charset=UTF-8");
        MIME_TYPES.put("css", "text/css; charset=UTF-8");
        MIME_TYPES.put("js", "application/javascript; charset=UTF-8");
        MIME_TYPES.put("json", "application/json; charset=UTF-8");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
    }

    private final ModereX plugin;
    private final int httpPort;
    private final int wsPort;
    private HttpServer server;
    private Path panelDirectory;

    public PanelHttpServer(ModereX plugin, int httpPort, int wsPort) {
        this.plugin = plugin;
        this.httpPort = httpPort;
        this.wsPort = wsPort;
        this.panelDirectory = plugin.getDataFolder().toPath().resolve("panel");
    }

    public void start() {
        try {
            // Ensure panel directory exists and extract default files if needed
            setupPanelDirectory();

            server = HttpServer.create(new InetSocketAddress(httpPort), 0);
            server.setExecutor(Executors.newFixedThreadPool(4));

            // API endpoint for config
            server.createContext("/api/config", new ConfigHandler());

            // Serve static files (must be last as it's a catch-all)
            server.createContext("/", new StaticFileHandler());

            server.start();
            plugin.getLogger().info("Web panel HTTP server started on port " + httpPort);
            plugin.getLogger().info("Access the panel at: http://localhost:" + httpPort + "/");

        } catch (IOException e) {
            plugin.logError("Failed to start HTTP server", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("Web panel HTTP server stopped");
        }
    }

    private void setupPanelDirectory() {
        try {
            if (!Files.exists(panelDirectory)) {
                Files.createDirectories(panelDirectory);
                plugin.getLogger().info("Created panel directory: " + panelDirectory);
            }

            // Create subdirectories
            Files.createDirectories(panelDirectory.resolve("js"));
            Files.createDirectories(panelDirectory.resolve("css"));

            // Extract default panel files from resources if index.html doesn't exist
            Path indexPath = panelDirectory.resolve("index.html");
            if (!Files.exists(indexPath)) {
                extractResource("panel/index.html", indexPath);
                extractResource("panel/css/styles.css", panelDirectory.resolve("css/styles.css"));
                extractResource("panel/js/utils.js", panelDirectory.resolve("js/utils.js"));
                extractResource("panel/js/state.js", panelDirectory.resolve("js/state.js"));
                extractResource("panel/js/websocket.js", panelDirectory.resolve("js/websocket.js"));
                extractResource("panel/js/auth.js", panelDirectory.resolve("js/auth.js"));
                extractResource("panel/js/ui.js", panelDirectory.resolve("js/ui.js"));
                extractResource("panel/js/app.js", panelDirectory.resolve("js/app.js"));
                plugin.getLogger().info("Extracted default panel files to: " + panelDirectory);
            }
        } catch (IOException e) {
            plugin.logError("Failed to setup panel directory", e);
        }
    }

    private void extractResource(String resourcePath, Path targetPath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                Files.createDirectories(targetPath.getParent());
                Files.copy(in, targetPath);
            } else {
                plugin.logDebug("Resource not found in JAR: " + resourcePath);
            }
        } catch (IOException e) {
            plugin.logError("Failed to extract resource: " + resourcePath, e);
        }
    }

    private class ConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            // Build config response
            JsonObject config = new JsonObject();

            // Get host - use configured host, or try to detect from request
            String host = plugin.getConfigManager().getSettings().getWebPanelHost();
            if (host == null || host.isEmpty()) {
                // Try to get from request Host header
                String hostHeader = exchange.getRequestHeaders().getFirst("Host");
                if (hostHeader != null && !hostHeader.isEmpty()) {
                    // Remove port if present
                    int colonIdx = hostHeader.indexOf(':');
                    host = colonIdx > 0 ? hostHeader.substring(0, colonIdx) : hostHeader;
                } else {
                    host = "localhost";
                }
            }

            config.addProperty("host", host);
            config.addProperty("wsPort", wsPort);
            config.addProperty("serverName", plugin.getDescription().getName());
            config.addProperty("serverVersion", plugin.getDescription().getVersion());

            String response = GSON.toJson(config);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String path = exchange.getRequestURI().getPath();

            // Default to index.html
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            // Security: prevent directory traversal
            if (path.contains("..")) {
                sendError(exchange, 403, "Forbidden");
                return;
            }

            // Remove leading slash
            String relativePath = path.substring(1);
            Path filePath = panelDirectory.resolve(relativePath);

            // Check if file exists
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                // Try with .html extension
                Path htmlPath = panelDirectory.resolve(relativePath + ".html");
                if (Files.exists(htmlPath)) {
                    filePath = htmlPath;
                } else {
                    sendError(exchange, 404, "Not found: " + path);
                    return;
                }
            }

            // Get content type
            String extension = getExtension(filePath.getFileName().toString());
            String contentType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

            // Read and send file
            byte[] bytes = Files.readAllBytes(filePath);
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.getResponseHeaders().add("Cache-Control", "public, max-age=3600");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

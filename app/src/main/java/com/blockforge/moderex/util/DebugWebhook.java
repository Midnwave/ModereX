package com.blockforge.moderex.util;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * Utility class for sending debug logs to a Discord webhook.
 * Logs are formatted with diff syntax for color coding:
 * - "-" prefix for error messages (red)
 * - "+" prefix for success messages (green)
 * - No prefix for debug messages (gray)
 */
public class DebugWebhook {

    public enum LogLevel {
        DEBUG,   // No prefix - gray text
        SUCCESS, // + prefix - green text
        ERROR    // - prefix - red text
    }

    // Pattern to match IP addresses (IPv4)
    private static final Pattern IP_PATTERN = Pattern.compile(
        "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"
    );

    // Pattern to match IPv6 addresses
    private static final Pattern IPV6_PATTERN = Pattern.compile(
        "\\b(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\b|" +
        "\\b(?:[0-9a-fA-F]{1,4}:){1,7}:\\b|" +
        "\\b::(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}\\b"
    );

    private final ModereX plugin;
    private final BlockingQueue<QueuedMessage> messageQueue;
    private final AtomicBoolean running;
    private Thread workerThread;
    private String webhookUrl;

    public DebugWebhook(ModereX plugin) {
        this.plugin = plugin;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(false);
        this.webhookUrl = "";
    }

    /**
     * Update the webhook URL from config.
     */
    public void setWebhookUrl(String url) {
        this.webhookUrl = url != null ? url.trim() : "";

        // Start or stop worker thread based on URL
        if (isEnabled() && !running.get()) {
            startWorker();
        } else if (!isEnabled() && running.get()) {
            stopWorker();
        }
    }

    /**
     * Check if webhook is configured and enabled.
     */
    public boolean isEnabled() {
        return webhookUrl != null && !webhookUrl.isEmpty();
    }

    /**
     * Send a debug message (gray text, no prefix).
     */
    public void debug(String message) {
        send(message, LogLevel.DEBUG);
    }

    /**
     * Send a success message (green text, + prefix).
     */
    public void success(String message) {
        send(message, LogLevel.SUCCESS);
    }

    /**
     * Send an error message (red text, - prefix).
     */
    public void error(String message) {
        send(message, LogLevel.ERROR);
    }

    /**
     * Send a message with specified log level.
     */
    public void send(String message, LogLevel level) {
        if (!isEnabled()) {
            return;
        }

        // Skip messages containing IP addresses
        if (containsIpAddress(message)) {
            return;
        }

        // Queue the message for rate-limited sending
        messageQueue.offer(new QueuedMessage(message, level));

        // Ensure worker is running
        if (!running.get()) {
            startWorker();
        }
    }

    /**
     * Check if a message contains an IP address.
     */
    private boolean containsIpAddress(String message) {
        return IP_PATTERN.matcher(message).find() || IPV6_PATTERN.matcher(message).find();
    }

    /**
     * Start the worker thread that processes the message queue.
     */
    private synchronized void startWorker() {
        if (running.get()) {
            return;
        }

        running.set(true);
        workerThread = new Thread(() -> {
            while (running.get()) {
                try {
                    QueuedMessage msg = messageQueue.take();

                    if (!running.get()) {
                        break;
                    }

                    try {
                        String formattedMessage = formatMessage(msg.message, msg.level);
                        sendToWebhook(formattedMessage);
                    } catch (Exception e) {
                        plugin.getLogger().warning("[DebugWebhook] Failed to send: " + e.getMessage());
                    }

                    // Wait 1 second before processing next message to avoid rate limiting
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "ModereX-DebugWebhook");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /**
     * Stop the worker thread.
     */
    private synchronized void stopWorker() {
        running.set(false);
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
    }

    /**
     * Format message with diff syntax for Discord color coding.
     */
    private String formatMessage(String message, LogLevel level) {
        String prefix;
        switch (level) {
            case SUCCESS:
                prefix = "+ ";
                break;
            case ERROR:
                prefix = "- ";
                break;
            case DEBUG:
            default:
                prefix = "  ";
                break;
        }

        // Wrap in diff code block for color syntax
        return "```diff\n" + prefix + message + "\n```";
    }

    /**
     * Send the formatted message to Discord webhook.
     */
    private void sendToWebhook(String content) throws Exception {
        URL url = URI.create(webhookUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "ModereX-Plugin");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Build JSON payload
            JsonObject json = new JsonObject();
            json.addProperty("content", content);
            json.addProperty("username", "ModereX Debug");

            String jsonPayload = json.toString();

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                plugin.getLogger().warning("[DebugWebhook] HTTP " + responseCode + " response from Discord");
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Shutdown the webhook system.
     */
    public void shutdown() {
        stopWorker();
        messageQueue.clear();
    }

    /**
     * Internal class to hold queued messages.
     */
    private static class QueuedMessage {
        final String message;
        final LogLevel level;

        QueuedMessage(String message, LogLevel level) {
            this.message = message;
            this.level = level;
        }
    }
}

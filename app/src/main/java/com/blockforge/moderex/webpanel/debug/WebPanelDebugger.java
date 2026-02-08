package com.blockforge.moderex.webpanel.debug;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonObject;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Centralized debug logging system for the ModereX WebPanel.
 * Features:
 * - Categorized debug messages
 * - Error codes with descriptions
 * - 0.5 second throttling between messages
 * - Formatted output for console and web panel
 * - Success/Error/Warning levels
 */
public class WebPanelDebugger {

    private final ModereX plugin;
    private final BlockingQueue<DebugMessage> messageQueue;
    private final ScheduledExecutorService scheduler;
    private final AtomicLong lastMessageTime;

    private static final long THROTTLE_DELAY_MS = 500; // 0.5 seconds
    private volatile boolean running = true;

    public WebPanelDebugger(ModereX plugin) {
        this.plugin = plugin;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ModereX-DebugLogger");
            t.setDaemon(true);
            return t;
        });
        this.lastMessageTime = new AtomicLong(0);

        // Start the message processor
        startProcessor();
    }

    private void startProcessor() {
        scheduler.scheduleWithFixedDelay(() -> {
            if (!running) return;

            try {
                DebugMessage msg = messageQueue.poll();
                if (msg != null) {
                    long now = System.currentTimeMillis();
                    long elapsed = now - lastMessageTime.get();

                    // Enforce throttle delay
                    if (elapsed < THROTTLE_DELAY_MS) {
                        Thread.sleep(THROTTLE_DELAY_MS - elapsed);
                    }

                    processMessage(msg);
                    lastMessageTime.set(System.currentTimeMillis());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                plugin.getLogger().warning("[DebugLogger] Error processing message: " + e.getMessage());
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void processMessage(DebugMessage msg) {
        if (!plugin.getConfigManager().getSettings().isDebugMode()) {
            return;
        }

        String formattedMessage = formatMessage(msg);

        // Log to console
        switch (msg.level) {
            case SUCCESS -> plugin.getLogger().info("\u001B[32m" + formattedMessage + "\u001B[0m");
            case ERROR -> plugin.getLogger().severe(formattedMessage);
            case WARNING -> plugin.getLogger().warning(formattedMessage);
            default -> plugin.getLogger().info(formattedMessage);
        }

        // Send to Discord webhook if enabled
        if (plugin.getConfigManager().getSettings().getDebugWebhookUrl() != null) {
            try {
                var webhook = getDebugWebhook();
                if (webhook != null && webhook.isEnabled()) {
                    switch (msg.level) {
                        case SUCCESS -> webhook.success(formattedMessage);
                        case ERROR -> webhook.error(formattedMessage);
                        default -> webhook.debug(formattedMessage);
                    }
                }
            } catch (Exception ignored) {}
        }

        // Broadcast to web panel debug log
        broadcastToWebPanel(msg);
    }

    private com.blockforge.moderex.util.DebugWebhook getDebugWebhook() {
        try {
            var field = ModereX.class.getDeclaredField("debugWebhook");
            field.setAccessible(true);
            return (com.blockforge.moderex.util.DebugWebhook) field.get(plugin);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatMessage(DebugMessage msg) {
        StringBuilder sb = new StringBuilder();

        // Timestamp
        sb.append("[").append(java.time.LocalTime.now().toString().substring(0, 8)).append("] ");

        // Level indicator
        switch (msg.level) {
            case SUCCESS -> sb.append("[+] ");
            case ERROR -> sb.append("[-] ");
            case WARNING -> sb.append("[!] ");
            default -> sb.append("[*] ");
        }

        // Category
        sb.append("[").append(msg.category.getTag()).append("] ");

        // Error code if present
        if (msg.errorCode != null) {
            sb.append("[").append(msg.errorCode.getNumericCode()).append("] ");
        }

        // Message
        sb.append(msg.message);

        // Details if present
        if (msg.details != null && !msg.details.isEmpty()) {
            sb.append(" | ").append(msg.details);
        }

        return sb.toString();
    }

    private void broadcastToWebPanel(DebugMessage msg) {
        if (plugin.getWebPanelServer() == null) return;

        try {
            // Map debug level to severity
            String severity = switch (msg.level) {
                case SUCCESS -> "INFO";
                case ERROR -> "ERROR";
                case WARNING -> "WARN";
                default -> "INFO";
            };

            // Build detail string
            StringBuilder detail = new StringBuilder(msg.message);
            if (msg.details != null && !msg.details.isEmpty()) {
                detail.append(" | ").append(msg.details);
            }
            if (msg.errorCode != null) {
                detail.append(" [").append(msg.errorCode.getNumericCode()).append(": ")
                      .append(msg.errorCode.getCode()).append("]");
            }

            // Use the public broadcastLogEvent method
            plugin.getWebPanelServer().broadcastLogEvent(
                severity,
                msg.category.getTag(),
                msg.level.name() + ": " + msg.category.getTag(),
                detail.toString()
            );
        } catch (Exception ignored) {}
    }

    // ==================== PUBLIC LOGGING METHODS ====================

    /**
     * Log an informational debug message.
     */
    public void info(DebugCategory category, String message) {
        queue(new DebugMessage(DebugLevel.INFO, category, message, null, null));
    }

    /**
     * Log an informational debug message with details.
     */
    public void info(DebugCategory category, String message, String details) {
        queue(new DebugMessage(DebugLevel.INFO, category, message, details, null));
    }

    /**
     * Log a success message.
     */
    public void success(DebugCategory category, String message) {
        queue(new DebugMessage(DebugLevel.SUCCESS, category, message, null, null));
    }

    /**
     * Log a success message with details.
     */
    public void success(DebugCategory category, String message, String details) {
        queue(new DebugMessage(DebugLevel.SUCCESS, category, message, details, null));
    }

    /**
     * Log a warning message.
     */
    public void warning(DebugCategory category, String message) {
        queue(new DebugMessage(DebugLevel.WARNING, category, message, null, null));
    }

    /**
     * Log a warning message with details.
     */
    public void warning(DebugCategory category, String message, String details) {
        queue(new DebugMessage(DebugLevel.WARNING, category, message, details, null));
    }

    /**
     * Log an error message.
     */
    public void error(DebugCategory category, String message) {
        queue(new DebugMessage(DebugLevel.ERROR, category, message, null, null));
    }

    /**
     * Log an error message with error code.
     */
    public void error(DebugCategory category, ErrorCode errorCode, String message) {
        queue(new DebugMessage(DebugLevel.ERROR, category, message, errorCode.getDescription(), errorCode));
    }

    /**
     * Log an error message with details.
     */
    public void error(DebugCategory category, String message, String details) {
        queue(new DebugMessage(DebugLevel.ERROR, category, message, details, null));
    }

    /**
     * Log an error message with error code and custom details.
     */
    public void error(DebugCategory category, ErrorCode errorCode, String message, String additionalDetails) {
        String details = errorCode.getDescription() + " | " + additionalDetails;
        queue(new DebugMessage(DebugLevel.ERROR, category, message, details, errorCode));
    }

    /**
     * Log an error message using just the error code (auto-determines category from code).
     */
    public void error(ErrorCode errorCode, String additionalDetails) {
        DebugCategory category = getCategoryForErrorCode(errorCode);
        String details = errorCode.getDescription() + (additionalDetails != null ? " | " + additionalDetails : "");
        queue(new DebugMessage(DebugLevel.ERROR, category, errorCode.getCode(), details, errorCode));
    }

    /**
     * Determine the appropriate category based on error code prefix.
     */
    private DebugCategory getCategoryForErrorCode(ErrorCode errorCode) {
        int numericCode = errorCode.getNumericCode();
        if (numericCode >= 1000 && numericCode < 2000) return DebugCategory.AUTH;
        if (numericCode >= 2000 && numericCode < 3000) return DebugCategory.REQUEST;
        if (numericCode >= 3000 && numericCode < 4000) return DebugCategory.REQUEST;
        if (numericCode >= 4000 && numericCode < 5000) return DebugCategory.WEBPANEL;
        if (numericCode >= 5000 && numericCode < 6000) return DebugCategory.WEBPANEL;
        if (numericCode >= 6000 && numericCode < 7000) return DebugCategory.PLAYER;
        if (numericCode >= 7000 && numericCode < 8000) return DebugCategory.PUNISHMENT;
        if (numericCode >= 8000 && numericCode < 9000) return DebugCategory.AUTOMOD;
        if (numericCode >= 9000 && numericCode < 10000) return DebugCategory.ANTICHEAT;
        return DebugCategory.ERROR;
    }

    // ==================== CONVENIENCE METHODS ====================

    // Connection events
    public void connectionOpened(String address) {
        success(DebugCategory.CONNECTION, "WebSocket connected", "Address: " + address);
    }

    public void connectionClosed(String address, String reason) {
        info(DebugCategory.CONNECTION, "WebSocket disconnected", "Address: " + address + ", Reason: " + reason);
    }

    public void connectionError(String address, String error) {
        error(DebugCategory.CONNECTION, ErrorCode.SYSTEM_CONNECTION_ERROR, "Connection error", "Address: " + address + ", Error: " + error);
    }

    // Authentication events
    public void authSuccess(String playerName, String address) {
        success(DebugCategory.AUTH, "Authentication successful", "Player: " + playerName + ", Address: " + address);
    }

    public void authFailed(String address, String reason) {
        error(DebugCategory.AUTH, ErrorCode.AUTH_FAILED, "Authentication failed", "Address: " + address + ", Reason: " + reason);
    }

    public void sessionCreated(String playerName, String sessionId) {
        info(DebugCategory.AUTH, "Session created", "Player: " + playerName + ", Session: " + sessionId.substring(0, 8) + "...");
    }

    public void sessionExpired(String playerName) {
        warning(DebugCategory.AUTH, "Session expired", "Player: " + playerName);
    }

    // Punishment events
    public void punishmentCreated(String type, String target, String issuer, String reason) {
        success(DebugCategory.PUNISHMENT, type.toUpperCase() + " issued", "Target: " + target + ", Issuer: " + issuer + ", Reason: " + reason);
    }

    public void punishmentRevoked(String type, String target, String revoker) {
        info(DebugCategory.PUNISHMENT, type.toUpperCase() + " revoked", "Target: " + target + ", By: " + revoker);
    }

    public void punishmentExpired(String type, String target) {
        info(DebugCategory.PUNISHMENT, type.toUpperCase() + " expired", "Target: " + target);
    }

    public void punishmentError(ErrorCode code, String target, String details) {
        error(DebugCategory.PUNISHMENT, code, "Punishment operation failed", "Target: " + target + ", " + details);
    }

    // Automod events
    public void automodRuleCreated(String ruleName, String creator) {
        success(DebugCategory.AUTOMOD, "Rule created", "Rule: " + ruleName + ", By: " + creator);
    }

    public void automodRuleUpdated(String ruleName, String editor) {
        info(DebugCategory.AUTOMOD, "Rule updated", "Rule: " + ruleName + ", By: " + editor);
    }

    public void automodRuleDeleted(String ruleName, String deleter) {
        warning(DebugCategory.AUTOMOD, "Rule deleted", "Rule: " + ruleName + ", By: " + deleter);
    }

    public void automodTriggered(String ruleName, String player, String action) {
        info(DebugCategory.AUTOMOD, "Rule triggered", "Rule: " + ruleName + ", Player: " + player + ", Action: " + action);
    }

    public void automodError(ErrorCode code, String ruleName, String details) {
        error(DebugCategory.AUTOMOD, code, "Automod operation failed", "Rule: " + ruleName + ", " + details);
    }

    // Anticheat events
    public void anticheatAlert(String anticheat, String player, String check, int vl) {
        info(DebugCategory.ANTICHEAT, "Alert received", anticheat + " | Player: " + player + ", Check: " + check + ", VL: " + vl);
    }

    public void anticheatRuleUpdated(String checkKey, String editor) {
        info(DebugCategory.ANTICHEAT, "Rule updated", "Check: " + checkKey + ", By: " + editor);
    }

    public void anticheatAutoPunish(String player, String check, String punishment) {
        warning(DebugCategory.ANTICHEAT, "Auto-punishment triggered", "Player: " + player + ", Check: " + check + ", Punishment: " + punishment);
    }

    // Disguise events
    public void disguiseActivated(String player, String disguiseName) {
        success(DebugCategory.DISGUISE, "Disguise activated", "Player: " + player + ", As: " + disguiseName);
    }

    public void disguiseDeactivated(String player) {
        info(DebugCategory.DISGUISE, "Disguise deactivated", "Player: " + player);
    }

    public void disguiseError(String player, String error) {
        error(DebugCategory.DISGUISE, "Disguise operation failed", "Player: " + player + ", Error: " + error);
    }

    // Vanish events
    public void vanishEnabled(String player) {
        success(DebugCategory.VANISH, "Vanish enabled", "Player: " + player);
    }

    public void vanishEnabled(String player, int level) {
        success(DebugCategory.VANISH, "Vanish enabled", "Player: " + player + ", Level: " + level);
    }

    public void vanishDisabled(String player) {
        info(DebugCategory.VANISH, "Vanish disabled", "Player: " + player);
    }

    // Staff mode events
    public void staffModeEnabled(String player) {
        success(DebugCategory.STAFFMODE, "Staff mode enabled", "Player: " + player);
    }

    public void staffModeDisabled(String player) {
        info(DebugCategory.STAFFMODE, "Staff mode disabled", "Player: " + player);
    }

    public void staffModeAction(String player, String action) {
        info(DebugCategory.STAFFMODE, "Staff action", "Player: " + player + ", Action: " + action);
    }

    // Watchlist events
    public void watchlistAdded(String target, String addedBy, String reason) {
        success(DebugCategory.WATCHLIST, "Player added to watchlist", "Target: " + target + ", By: " + addedBy + ", Reason: " + reason);
    }

    public void watchlistRemoved(String target, String removedBy) {
        info(DebugCategory.WATCHLIST, "Player removed from watchlist", "Target: " + target + ", By: " + removedBy);
    }

    public void watchlistAlert(String target, String alertType, String details) {
        warning(DebugCategory.WATCHLIST, "Watchlist alert", "Target: " + target + ", Type: " + alertType + ", Details: " + details);
    }

    // Chat events
    public void chatFiltered(String player, String rule, String action) {
        info(DebugCategory.CHAT, "Message filtered", "Player: " + player + ", Rule: " + rule + ", Action: " + action);
    }

    public void staffChatMessage(String sender, String message) {
        info(DebugCategory.STAFFCHAT, "Staff chat", "From: " + sender + ", Message: " + truncate(message, 50));
    }

    // Database events
    public void databaseQuery(String operation, String table) {
        info(DebugCategory.DATABASE, "Query executed", "Operation: " + operation + ", Table: " + table);
    }

    public void databaseError(String operation, String error) {
        error(DebugCategory.DATABASE, ErrorCode.SYSTEM_DATABASE_ERROR, "Database operation failed", "Operation: " + operation + ", Error: " + error);
    }

    // Plugin events
    public void pluginHookSuccess(String pluginName) {
        success(DebugCategory.PLUGIN, "Plugin hooked", "Plugin: " + pluginName);
    }

    public void pluginHookFailed(String pluginName, String reason) {
        warning(DebugCategory.PLUGIN, "Plugin hook failed", "Plugin: " + pluginName + ", Reason: " + reason);
    }

    // Template events
    public void templateCreated(String templateName, String creator) {
        success(DebugCategory.TEMPLATE, "Template created", "Name: " + templateName + ", By: " + creator);
    }

    public void templateUpdated(String templateName, String editor) {
        info(DebugCategory.TEMPLATE, "Template updated", "Name: " + templateName + ", By: " + editor);
    }

    public void templateDeleted(String templateName, String deleter) {
        warning(DebugCategory.TEMPLATE, "Template deleted", "Name: " + templateName + ", By: " + deleter);
    }

    // Web panel specific
    public void webPanelRequest(String type, String sessionPlayer) {
        info(DebugCategory.WEBPANEL, "Request received", "Type: " + type + ", By: " + sessionPlayer);
    }

    public void webPanelError(ErrorCode code, String type, String details) {
        error(DebugCategory.WEBPANEL, code, "Request failed", "Type: " + type + ", " + details);
    }

    // GeoIP events
    public void geoipLookup(String ip, String country) {
        info(DebugCategory.GEOIP, "Location resolved", "IP: " + maskIp(ip) + ", Country: " + country);
    }

    public void geoipError(String ip, String error) {
        warning(DebugCategory.GEOIP, "Lookup failed", "IP: " + maskIp(ip) + ", Error: " + error);
    }

    // Replay events
    public void replayStarted(String player, String trigger) {
        success(DebugCategory.REPLAY, "Recording started", "Player: " + player + ", Trigger: " + trigger);
    }

    public void replayStopped(String player, String reason) {
        info(DebugCategory.REPLAY, "Recording stopped", "Player: " + player + ", Reason: " + reason);
    }

    // ==================== UTILITY METHODS ====================

    private void queue(DebugMessage message) {
        if (!running) return;
        messageQueue.offer(message);
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "null";
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }

    private String maskIp(String ip) {
        if (ip == null) return "unknown";
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".xxx.xxx";
        }
        return ip.substring(0, Math.min(ip.length(), 10)) + "...";
    }

    /**
     * Shutdown the debug logger gracefully.
     */
    public void shutdown() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    // ==================== INTERNAL CLASSES ====================

    public enum DebugLevel {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }

    private record DebugMessage(
        DebugLevel level,
        DebugCategory category,
        String message,
        String details,
        ErrorCode errorCode
    ) {}
}

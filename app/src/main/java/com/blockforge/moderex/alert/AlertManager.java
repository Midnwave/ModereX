package com.blockforge.moderex.alert;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.WebNotifyMode;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Unified alert system for ModereX.
 * Handles sending alerts to both in-game staff and web panel,
 * respecting each staff member's notification settings.
 */
public class AlertManager {

    private final ModereX plugin;

    public AlertManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Alert types supported by the system.
     */
    public enum AlertType {
        ANTICHEAT("Anticheat", "anticheat", NamedTextColor.RED),
        AUTOMOD("Automod", "automod", NamedTextColor.GOLD),
        PUNISHMENT("Punishment", "punishments", NamedTextColor.RED),
        WATCHLIST("Watchlist", "watchlist", NamedTextColor.YELLOW),
        STAFFCHAT("Staff Chat", "staffChat", NamedTextColor.AQUA),
        CUSTOM("Alert", "custom", NamedTextColor.LIGHT_PURPLE);

        private final String displayName;
        private final String settingKey;
        private final NamedTextColor color;

        AlertType(String displayName, String settingKey, NamedTextColor color) {
            this.displayName = displayName;
            this.settingKey = settingKey;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getSettingKey() { return settingKey; }
        public NamedTextColor getColor() { return color; }

        public static AlertType fromString(String s) {
            if (s == null) return CUSTOM;
            for (AlertType type : values()) {
                if (type.name().equalsIgnoreCase(s) || type.displayName.equalsIgnoreCase(s)) {
                    return type;
                }
            }
            return CUSTOM;
        }
    }

    /**
     * Send a unified alert to all staff members (in-game and web panel).
     *
     * @param playerName The name of the player causing the alert (can be offline)
     * @param playerUuid The UUID of the player (can be null for offline/unknown players)
     * @param type The type of alert
     * @param title The alert title/header
     * @param message The alert message/details
     */
    public void sendAlert(String playerName, UUID playerUuid, AlertType type, String title, String message) {
        // Broadcast to in-game staff
        broadcastToInGameStaff(playerName, playerUuid, type, title, message);

        // Broadcast to web panel
        broadcastToWebPanel(playerName, playerUuid, type, title, message);

        plugin.logDebug("[Alert] Sent " + type.name() + " alert: " + playerName + " - " + message);
    }

    /**
     * Send an alert for a player by name (looks up UUID if online, works for offline too).
     */
    public void sendAlert(String playerName, AlertType type, String title, String message) {
        UUID uuid = null;

        // Try to get UUID from online player
        Player online = Bukkit.getPlayer(playerName);
        if (online != null) {
            uuid = online.getUniqueId();
        } else {
            // Try to get from offline player cache
            @SuppressWarnings("deprecation")
            OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
            if (offline.hasPlayedBefore()) {
                uuid = offline.getUniqueId();
            }
        }

        sendAlert(playerName, uuid, type, title, message);
    }

    /**
     * Send an alert for an online player.
     */
    public void sendAlert(Player player, AlertType type, String title, String message) {
        sendAlert(player.getName(), player.getUniqueId(), type, title, message);
    }

    private void broadcastToInGameStaff(String playerName, UUID playerUuid, AlertType type, String title, String message) {
        Component alertComponent = buildInGameAlert(playerName, type, title, message);

        // Check if target player is on watchlist
        boolean isOnWatchlist = playerUuid != null && plugin.getWatchlistManager().isWatched(playerUuid);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (!staff.hasPermission("moderex.staff")) continue;

            // Check staff settings
            StaffSettings settings = plugin.getStaffSettingsManager().getSettings(staff.getUniqueId());
            if (settings == null) {
                // No settings = use defaults, send alert
                staff.sendMessage(alertComponent);
                continue;
            }

            // Check if chat alerts are enabled globally first
            if (!settings.isChatAlerts()) continue;

            // Get the specific alert level for this type
            StaffSettings.AlertLevel alertLevel = switch (type) {
                case ANTICHEAT -> settings.getAnticheatAlerts();
                case AUTOMOD -> settings.getAutomodAlerts();
                case PUNISHMENT -> settings.getPunishmentAlerts();
                case WATCHLIST -> StaffSettings.AlertLevel.EVERYONE; // Watchlist uses separate boolean settings
                case STAFFCHAT -> settings.isStaffChatEnabled() ? StaffSettings.AlertLevel.EVERYONE : StaffSettings.AlertLevel.OFF;
                case CUSTOM -> StaffSettings.AlertLevel.EVERYONE;
            };

            // Check alert level - OFF means don't send
            if (alertLevel == StaffSettings.AlertLevel.OFF) continue;

            // Check watchlist requirement
            boolean shouldSend = switch (alertLevel) {
                case EVERYONE -> true;
                case WATCHLIST_ONLY -> isOnWatchlist;
                case OFF -> false;
            };

            // Special handling for watchlist type (uses separate boolean settings)
            if (type == AlertType.WATCHLIST) {
                shouldSend = settings.isWatchlistJoinAlerts() || settings.isWatchlistActivityAlerts();
            }

            if (shouldSend) {
                staff.sendMessage(alertComponent);

                // Play sound only if sound is enabled
                if (settings.isSoundEnabled()) {
                    staff.playSound(staff.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                }

                // Show action bar if enabled
                if (settings.isActionBarAlerts()) {
                    staff.sendActionBar(Component.text("[" + type.getDisplayName() + "] ")
                            .color(type.getColor())
                            .append(Component.text(playerName + ": " + truncate(message, 40)).color(NamedTextColor.WHITE)));
                }
            }
        }
    }

    private void broadcastToWebPanel(String playerName, UUID playerUuid, AlertType type, String title, String message) {
        if (plugin.getWebPanelServer() == null) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "CUSTOM_ALERT");

        JsonObject data = new JsonObject();
        data.addProperty("alertType", type.name().toLowerCase());
        data.addProperty("category", type.getSettingKey());
        data.addProperty("playerName", playerName);
        if (playerUuid != null) {
            data.addProperty("playerUuid", playerUuid.toString());
        }
        data.addProperty("title", title);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());

        json.add("data", data);

        // Use the HybridPanelServer's broadcast method
        plugin.getWebPanelServer().broadcastCustomAlert(type.getSettingKey(), playerName, playerUuid, title, message);
    }

    private Component buildInGameAlert(String playerName, AlertType type, String title, String message) {
        return Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ModereX", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                .append(Component.text("[" + type.getDisplayName() + "] ", type.getColor()))
                .append(Component.text(playerName, NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}

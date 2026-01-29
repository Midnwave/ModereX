package com.blockforge.moderex.alert;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel;
import com.blockforge.moderex.util.PermissionUtil;
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
 * respecting each staff member's notification settings and permissions.
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
        // Punishment alerts
        BAN("Ban", "ban", NamedTextColor.RED, "moderex.alerts.ban"),
        KICK("Kick", "kick", NamedTextColor.GOLD, "moderex.alerts.kick"),
        MUTE("Mute", "mute", NamedTextColor.YELLOW, "moderex.alerts.mute"),
        WARN("Warn", "warn", NamedTextColor.AQUA, "moderex.alerts.warn"),
        PARDON("Pardon", "pardon", NamedTextColor.GREEN, "moderex.alerts.pardon"),

        // Detection alerts
        ANTICHEAT("Anticheat", "anticheat", NamedTextColor.RED, "moderex.alerts.anticheat"),
        AUTOMOD("Automod", "automod", NamedTextColor.GOLD, "moderex.alerts.automod"),
        COMMAND("Command", "command", NamedTextColor.GRAY, "moderex.alerts.commands"),
        NICKNAME("Nickname", "nickname", NamedTextColor.LIGHT_PURPLE, "moderex.alerts.nickname"),

        // Other alerts
        JOINLEAVE("Join/Leave", "joinleave", NamedTextColor.GREEN, "moderex.alerts.joinleave"),
        LAG("Server Lag", "lag", NamedTextColor.RED, "moderex.alerts.lag"),
        WATCHLIST("Watchlist", "watchlist", NamedTextColor.YELLOW, "moderex.alerts.watchlist"),
        STAFFCHAT("Staff Chat", "staffchat", NamedTextColor.AQUA, "moderex.alerts.staffchat"),

        // Legacy/generic
        PUNISHMENT("Punishment", "punishments", NamedTextColor.RED, "moderex.alerts.punishments"),
        CUSTOM("Alert", "custom", NamedTextColor.LIGHT_PURPLE, "moderex.alerts.*");

        private final String displayName;
        private final String settingKey;
        private final NamedTextColor color;
        private final String permission;

        AlertType(String displayName, String settingKey, NamedTextColor color, String permission) {
            this.displayName = displayName;
            this.settingKey = settingKey;
            this.color = color;
            this.permission = permission;
        }

        public String getDisplayName() { return displayName; }
        public String getSettingKey() { return settingKey; }
        public NamedTextColor getColor() { return color; }
        public String getPermission() { return permission; }

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
        plugin.logDebug("[AlertManager] Sending " + type.name() + " alert for " + playerName + ": " + message);

        // Broadcast to in-game staff
        broadcastToInGameStaff(playerName, playerUuid, type, title, message);

        // Broadcast to web panel (skip join/leave as per requirements)
        if (type != AlertType.JOINLEAVE) {
            broadcastToWebPanel(playerName, playerUuid, type, title, message);
        }
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

    /**
     * Send a command alert with blacklist flag.
     */
    public void sendCommandAlert(String playerName, UUID playerUuid, String command, boolean isBlacklisted) {
        plugin.logDebug("[AlertManager] Sending command alert for " + playerName + ": " + command + " (blacklisted: " + isBlacklisted + ")");

        boolean isOnWatchlist = playerUuid != null && plugin.getWatchlistManager().isWatched(playerUuid);
        Component alertComponent = buildInGameAlert(playerName, AlertType.COMMAND, "Command", command);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (!hasAlertPermission(staff, AlertType.COMMAND)) continue;

            StaffSettings settings = plugin.getStaffSettingsManager().getSettings(staff.getUniqueId());
            if (settings == null || !settings.isChatAlerts()) continue;

            CommandAlertLevel level = settings.getCommandAlerts();
            boolean shouldSend = settings.shouldShowCommandAlert(level, isOnWatchlist, isBlacklisted);

            plugin.logDebug("[AlertManager] Staff " + staff.getName() + " command alert check: level=" + level +
                    ", watchlist=" + isOnWatchlist + ", blacklisted=" + isBlacklisted + ", shouldSend=" + shouldSend);

            if (shouldSend) {
                sendAlertToStaff(staff, settings, alertComponent, AlertType.COMMAND, playerName, command);
            }
        }

        // Broadcast to web panel
        broadcastToWebPanel(playerName, playerUuid, AlertType.COMMAND, "Command", command);
    }

    private void broadcastToInGameStaff(String playerName, UUID playerUuid, AlertType type, String title, String message) {
        Component alertComponent = buildInGameAlert(playerName, type, title, message);

        // Check if target player is on watchlist
        boolean isOnWatchlist = playerUuid != null && plugin.getWatchlistManager().isWatched(playerUuid);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            // Check permission for this alert type
            if (!hasAlertPermission(staff, type)) {
                plugin.logDebug("[AlertManager] Staff " + staff.getName() + " lacks permission for " + type.name());
                continue;
            }

            // Check staff settings
            StaffSettings settings = plugin.getStaffSettingsManager().getSettings(staff.getUniqueId());
            if (settings == null) {
                // No settings = use defaults, send alert
                staff.sendMessage(alertComponent);
                continue;
            }

            // Check if chat alerts are enabled globally first
            if (!settings.isChatAlerts()) {
                plugin.logDebug("[AlertManager] Staff " + staff.getName() + " has chat alerts disabled");
                continue;
            }

            // Get the specific alert level for this type
            AlertLevel alertLevel = getAlertLevelForType(settings, type);
            plugin.logDebug("[AlertManager] Staff " + staff.getName() + " alert level for " + type.name() + ": " + alertLevel);

            // Check alert level - OFF means don't send
            if (alertLevel == AlertLevel.OFF) continue;

            // Check watchlist requirement
            boolean shouldSend = settings.shouldShowAlert(alertLevel, isOnWatchlist);

            // Special handling for watchlist type (uses separate boolean settings)
            if (type == AlertType.WATCHLIST) {
                shouldSend = settings.isWatchlistJoinAlerts() || settings.isWatchlistActivityAlerts();
            }

            // Special handling for staff chat
            if (type == AlertType.STAFFCHAT) {
                shouldSend = settings.isStaffChatEnabled();
            }

            // Special handling for lag alerts (boolean)
            if (type == AlertType.LAG) {
                shouldSend = settings.isLagAlerts();
            }

            plugin.logDebug("[AlertManager] Staff " + staff.getName() + " shouldSend=" + shouldSend +
                    " (watchlist=" + isOnWatchlist + ")");

            if (shouldSend) {
                sendAlertToStaff(staff, settings, alertComponent, type, playerName, message);
            }
        }
    }

    private void sendAlertToStaff(Player staff, StaffSettings settings, Component alertComponent,
                                   AlertType type, String playerName, String message) {
        // Send chat message
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

    private AlertLevel getAlertLevelForType(StaffSettings settings, AlertType type) {
        return switch (type) {
            case BAN -> settings.getBanAlerts();
            case KICK -> settings.getKickAlerts();
            case MUTE -> settings.getMuteAlerts();
            case WARN -> settings.getWarnAlerts();
            case PARDON -> settings.getPardonAlerts();
            case ANTICHEAT -> settings.getAnticheatAlerts();
            case AUTOMOD -> settings.getAutomodAlerts();
            case NICKNAME -> settings.getNicknameAlerts();
            case JOINLEAVE -> settings.getJoinLeaveAlerts();
            case WATCHLIST -> AlertLevel.EVERYONE; // Uses separate boolean settings
            case STAFFCHAT -> settings.isStaffChatEnabled() ? AlertLevel.EVERYONE : AlertLevel.OFF;
            case LAG -> settings.isLagAlerts() ? AlertLevel.EVERYONE : AlertLevel.OFF;
            case PUNISHMENT -> settings.getPunishmentAlerts();
            case COMMAND -> AlertLevel.EVERYONE; // Uses CommandAlertLevel separately
            case CUSTOM -> AlertLevel.EVERYONE;
        };
    }

    private boolean hasAlertPermission(Player player, AlertType type) {
        // Staff permission is the master permission (OPs bypass)
        if (!PermissionUtil.hasPermission(player, "moderex.staff")) {
            return false;
        }

        // Check for all-alerts permission (OPs bypass)
        if (PermissionUtil.hasPermission(player, "moderex.alerts.*")) {
            return true;
        }

        // Check specific permission (OPs bypass)
        return PermissionUtil.hasPermission(player, type.getPermission());
    }

    private void broadcastToWebPanel(String playerName, UUID playerUuid, AlertType type, String title, String message) {
        if (plugin.getWebPanelServer() == null) return;

        plugin.logDebug("[AlertManager] Broadcasting to web panel: " + type.name() + " - " + playerName);

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

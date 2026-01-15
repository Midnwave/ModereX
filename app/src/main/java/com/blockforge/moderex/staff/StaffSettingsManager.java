package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffSettingsManager {

    private final ModereX plugin;
    private final Map<UUID, StaffSettings> settingsCache = new ConcurrentHashMap<>();

    public StaffSettingsManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public StaffSettings getSettings(UUID uuid) {
        return settingsCache.computeIfAbsent(uuid, this::loadSettings);
    }

    public StaffSettings getSettings(Player player) {
        return getSettings(player.getUniqueId());
    }

    private StaffSettings loadSettings(UUID uuid) {
        try {
            return plugin.getDatabaseManager().query(
                    "SELECT settings FROM moderex_staff_settings WHERE uuid = ?",
                    rs -> {
                        if (rs.next()) {
                            String json = rs.getString("settings");
                            StaffSettings settings = StaffSettings.fromJson(json);
                            settings.setStaffUuid(uuid);
                            return settings;
                        }
                        return createDefaultSettings(uuid);
                    },
                    uuid.toString()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load staff settings for " + uuid, e);
            return createDefaultSettings(uuid);
        }
    }

    private StaffSettings createDefaultSettings(UUID uuid) {
        StaffSettings settings = new StaffSettings(uuid);
        return settings;
    }

    public void saveSettings(StaffSettings settings) {
        settings.setUpdatedAt(System.currentTimeMillis());
        settingsCache.put(settings.getStaffUuid(), settings);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update(
                        """
                        INSERT INTO moderex_staff_settings (uuid, settings, updated_at)
                        VALUES (?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET
                            settings = excluded.settings,
                            updated_at = excluded.updated_at
                        """,
                        settings.getStaffUuid().toString(),
                        settings.toJson(),
                        settings.getUpdatedAt()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to save staff settings", e);
            }
        });
    }

    public void saveSettings(Player player) {
        StaffSettings settings = settingsCache.get(player.getUniqueId());
        if (settings != null) {
            saveSettings(settings);
        }
    }

    public void clearCache(UUID uuid) {
        StaffSettings settings = settingsCache.remove(uuid);
        if (settings != null) {
            // Save before removing
            saveSettings(settings);
        }
    }

    public void clearCache(Player player) {
        clearCache(player.getUniqueId());
    }

    public boolean shouldReceivePunishmentAlert(Player staff, UUID targetUuid, String punishmentType) {
        StaffSettings settings = getSettings(staff);
        boolean isWatched = plugin.getWatchlistManager().isWatched(targetUuid);

        StaffSettings.AlertLevel level = switch (punishmentType.toUpperCase()) {
            case "WARN" -> settings.getWarnAlerts();
            case "MUTE" -> settings.getMuteAlerts();
            case "BAN", "IPBAN" -> settings.getBanAlerts();
            case "KICK" -> settings.getKickAlerts();
            case "UNBAN", "UNMUTE" -> settings.getPardonAlerts();
            default -> settings.getPunishmentAlerts();
        };

        return settings.shouldShowAlert(level, isWatched);
    }

    public boolean shouldReceiveAutomodAlert(Player staff, UUID targetUuid, String alertType) {
        StaffSettings settings = getSettings(staff);
        boolean isWatched = plugin.getWatchlistManager().isWatched(targetUuid);

        StaffSettings.AlertLevel level = switch (alertType.toLowerCase()) {
            case "spam" -> settings.getSpamAlerts();
            case "filter" -> settings.getFilterAlerts();
            default -> settings.getAutomodAlerts();
        };

        return settings.shouldShowAlert(level, isWatched);
    }

    public boolean shouldReceiveAnticheatAlert(Player staff, UUID targetUuid, int violationLevel) {
        StaffSettings settings = getSettings(staff);
        boolean isWatched = plugin.getWatchlistManager().isWatched(targetUuid);

        // Check VL threshold
        if (violationLevel < settings.getAnticheatMinVL()) {
            return false;
        }

        return settings.shouldShowAlert(settings.getAnticheatAlerts(), isWatched);
    }

    public boolean shouldReceiveWatchlistAlert(Player staff, String alertType) {
        StaffSettings settings = getSettings(staff);

        return switch (alertType.toLowerCase()) {
            case "join" -> settings.isWatchlistJoinAlerts();
            case "quit" -> settings.isWatchlistQuitAlerts();
            case "activity" -> settings.isWatchlistActivityAlerts();
            default -> true;
        };
    }

    public boolean shouldReceiveCommandAlert(Player staff, UUID targetUuid) {
        StaffSettings settings = getSettings(staff);
        boolean isWatched = plugin.getWatchlistManager().isWatched(targetUuid);
        return settings.shouldShowAlert(settings.getCommandAlerts(), isWatched);
    }

    public boolean isStaffChatEnabled(Player player) {
        return getSettings(player).isStaffChatEnabled();
    }

    public boolean isStaffChatSoundEnabled(Player player) {
        return getSettings(player).isStaffChatSound();
    }
}

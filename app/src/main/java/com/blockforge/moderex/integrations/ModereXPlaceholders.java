package com.blockforge.moderex.integrations;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * PlaceholderAPI expansion for ModereX.
 * Provides placeholders for vanish, punishments, and other plugin features.
 */
public class ModereXPlaceholders extends PlaceholderExpansion {

    private final ModereX plugin;

    public ModereXPlaceholders(ModereX plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "moderex";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Required to stay loaded
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        // %moderex_<placeholder>%

        // Vanish placeholders
        if (params.equals("vanished")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "false";
            Player player = offlinePlayer.getPlayer();
            return String.valueOf(plugin.getVanishManager().isVanished(player));
        }

        if (params.equals("vanish_level")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "0";
            Player player = offlinePlayer.getPlayer();
            return String.valueOf(plugin.getVanishManager().getVanishLevel(player));
        }

        if (params.equals("vanish_duration")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "0s";
            return plugin.getVanishManager().getVanishDuration(offlinePlayer.getUniqueId());
        }

        if (params.equals("vanished_count")) {
            return String.valueOf(plugin.getVanishManager().getVanishedPlayers().size());
        }

        if (params.equals("visible_players")) {
            return String.valueOf(plugin.getVanishManager().getVisiblePlayerCount());
        }

        // Vanish prefix/suffix (from config or LuckPerms)
        if (params.equals("vanish_prefix")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "";
            Player player = offlinePlayer.getPlayer();
            if (plugin.getVanishManager().isVanished(player)) {
                return plugin.getConfigManager().getSettings().getVanishLuckPermsVanishPrefix();
            }
            return "";
        }

        if (params.equals("vanish_suffix")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "";
            Player player = offlinePlayer.getPlayer();
            if (plugin.getVanishManager().isVanished(player)) {
                return plugin.getConfigManager().getSettings().getVanishLuckPermsVanishSuffix();
            }
            return "";
        }

        // Vanish status for specific player
        // %moderex_vanished_<player>%
        if (params.startsWith("vanished_")) {
            String targetName = params.substring("vanished_".length());
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) return "false";
            return String.valueOf(plugin.getVanishManager().isVanished(target));
        }

        // Punishment placeholders
        if (params.equals("is_muted")) {
            if (offlinePlayer == null) return "false";
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.MUTE);
            return String.valueOf(mute != null && !mute.isExpired());
        }

        if (params.equals("is_banned")) {
            if (offlinePlayer == null) return "false";
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.BAN);
            return String.valueOf(ban != null && !ban.isExpired());
        }

        if (params.equals("mute_reason")) {
            if (offlinePlayer == null) return "";
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.MUTE);
            return (mute != null && !mute.isExpired()) ? mute.getReason() : "";
        }

        if (params.equals("ban_reason")) {
            if (offlinePlayer == null) return "";
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.BAN);
            return (ban != null && !ban.isExpired()) ? ban.getReason() : "";
        }

        if (params.equals("mute_expires")) {
            if (offlinePlayer == null) return "";
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.MUTE);
            if (mute == null || mute.isExpired()) return "";
            return mute.isPermanent() ? "Never" : formatTime(mute.getExpiresAt() - System.currentTimeMillis());
        }

        if (params.equals("ban_expires")) {
            if (offlinePlayer == null) return "";
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(
                    offlinePlayer.getUniqueId(), PunishmentType.BAN);
            if (ban == null || ban.isExpired()) return "";
            return ban.isPermanent() ? "Never" : formatTime(ban.getExpiresAt() - System.currentTimeMillis());
        }

        if (params.equals("warnings")) {
            if (offlinePlayer == null) return "0";
            try {
                int count = plugin.getDatabaseManager().query("""
                        SELECT COUNT(*) as count FROM moderex_warnings
                        WHERE player_uuid = ? AND active = TRUE
                        """,
                        rs -> rs.next() ? rs.getInt("count") : 0,
                        offlinePlayer.getUniqueId().toString()
                );
                return String.valueOf(count);
            } catch (Exception e) {
                return "0";
            }
        }

        // Watchlist placeholders
        if (params.equals("is_watched")) {
            if (offlinePlayer == null) return "false";
            return String.valueOf(plugin.getWatchlistManager().isWatched(offlinePlayer.getUniqueId()));
        }

        // Staff chat placeholder
        if (params.equals("staffchat_enabled")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline()) return "false";
            Player player = offlinePlayer.getPlayer();
            return String.valueOf(plugin.getStaffChatManager().hasStaffChatToggled(player));
        }

        // Server placeholders
        if (params.equals("server_name")) {
            return plugin.getConfigManager().getSettings().getWebPanelServerName();
        }

        if (params.equals("online_staff")) {
            long count = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("moderex.command.vanish"))
                    .count();
            return String.valueOf(count);
        }

        return null; // Placeholder not found
    }

    /**
     * Format milliseconds as human-readable time.
     */
    private String formatTime(long milliseconds) {
        if (milliseconds <= 0) return "0s";

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    /**
     * Register this expansion.
     */
    public boolean register() {
        return super.register();
    }
}

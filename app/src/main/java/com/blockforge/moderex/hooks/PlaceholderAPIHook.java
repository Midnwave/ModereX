package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final ModereX plugin;

    public PlaceholderAPIHook(ModereX plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "moderex";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BlockForge";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // %moderex_is_muted%
        if (params.equalsIgnoreCase("is_muted")) {
            return String.valueOf(plugin.getPunishmentManager().isMuted(player.getUniqueId()));
        }

        // %moderex_is_banned%
        if (params.equalsIgnoreCase("is_banned")) {
            return String.valueOf(plugin.getPunishmentManager().isBanned(player.getUniqueId()));
        }

        // %moderex_is_vanished%
        if (params.equalsIgnoreCase("is_vanished")) {
            return String.valueOf(plugin.getVanishManager().isVanished(player.getUniqueId()));
        }

        // %moderex_mute_reason%
        if (params.equalsIgnoreCase("mute_reason")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.MUTE);
            return mute != null ? mute.getReason() : "";
        }

        // %moderex_mute_duration%
        if (params.equalsIgnoreCase("mute_duration")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.MUTE);
            if (mute == null) return "";
            return DurationParser.formatRemaining(mute.getExpiresAt());
        }

        // %moderex_mute_staff%
        if (params.equalsIgnoreCase("mute_staff")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.MUTE);
            return mute != null ? mute.getStaffName() : "";
        }

        // %moderex_ban_reason%
        if (params.equalsIgnoreCase("ban_reason")) {
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.BAN);
            return ban != null ? ban.getReason() : "";
        }

        // %moderex_ban_duration%
        if (params.equalsIgnoreCase("ban_duration")) {
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.BAN);
            if (ban == null) return "";
            return DurationParser.formatRemaining(ban.getExpiresAt());
        }

        // %moderex_ban_staff%
        if (params.equalsIgnoreCase("ban_staff")) {
            Punishment ban = plugin.getPunishmentManager().getActivePunishment(player.getUniqueId(), PunishmentType.BAN);
            return ban != null ? ban.getStaffName() : "";
        }

        // %moderex_online_staff%
        if (params.equalsIgnoreCase("online_staff")) {
            long count = plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("moderex.command.mute"))
                    .count();
            return String.valueOf(count);
        }

        // %moderex_visible_players%
        if (params.equalsIgnoreCase("visible_players")) {
            return String.valueOf(plugin.getVanishManager().getVisiblePlayerCount());
        }

        // %moderex_vanished_players%
        if (params.equalsIgnoreCase("vanished_players")) {
            return String.valueOf(plugin.getVanishManager().getVanishedPlayers().size());
        }

        // %moderex_prefix%
        if (params.equalsIgnoreCase("prefix")) {
            if (plugin.getHookManager().hasLuckPerms() && player.isOnline()) {
                return plugin.getHookManager().getLuckPermsHook().getPrefix(player.getPlayer());
            }
            return "";
        }

        return null;
    }
}

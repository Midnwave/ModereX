package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.ReplaySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CommandListener implements Listener {

    private final ModereX plugin;
    private final List<String> mutedBlockedCommands;

    public CommandListener(ModereX plugin) {
        this.plugin = plugin;
        this.mutedBlockedCommands = plugin.getConfigManager().getConfig()
                .getStringList("mute.blocked-commands");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String fullCommand = event.getMessage().substring(1); // Remove leading /
        String baseCommand = fullCommand.split(" ")[0].toLowerCase();

        // Check if muted and command is blocked
        if (!player.hasPermission("moderex.bypass.mute")) {
            if (!plugin.getConfigManager().getSettings().isMutedPlayersCanUseCommands()) {
                Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
                if (mute != null && !mute.isExpired()) {
                    // Check if this command is blocked for muted players
                    if (isBlockedForMuted(baseCommand)) {
                        event.setCancelled(true);
                        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                                "duration", com.blockforge.moderex.util.DurationParser.formatRemaining(mute.getExpiresAt()),
                                "reason", mute.getReason()));
                        return;
                    }
                }
            }
        }

        // Check command blacklist
        if (isCommandBlacklisted(uuid, baseCommand) || isCommandBlacklisted(uuid, fullCommand)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.CMD_BLACKLIST_BLOCKED));
            return;
        }

        // Log command to history
        logCommand(player, fullCommand);

        // Notify watchlist
        if (plugin.getWatchlistManager().isWatched(uuid)) {
            plugin.getWatchlistManager().onCommand(player, "/" + fullCommand);
        }

        // Record to replay if active
        if (plugin.getReplayManager() != null) {
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.COMMAND, "/" + fullCommand);
        }
    }

    private boolean isBlockedForMuted(String command) {
        // Check exact match
        if (mutedBlockedCommands.contains(command.toLowerCase())) {
            return true;
        }

        // Check plugin:command format
        for (String blocked : mutedBlockedCommands) {
            if (command.equalsIgnoreCase(blocked) ||
                    command.toLowerCase().endsWith(":" + blocked.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private boolean isCommandBlacklisted(UUID uuid, String command) {
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT 1 FROM moderex_command_blacklist
                    WHERE player_uuid = ? AND (command = ? OR ? LIKE command || '%')
                    AND (expires_at = -1 OR expires_at > ?)
                    LIMIT 1
                    """,
                    rs -> rs.next(),
                    uuid.toString(),
                    command.toLowerCase(),
                    command.toLowerCase(),
                    System.currentTimeMillis()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to check command blacklist", e);
            return false;
        }
    }

    private void logCommand(Player player, String command) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_command_history (player_uuid, player_name, command, executed_at, server)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                        player.getUniqueId().toString(),
                        player.getName(),
                        "/" + command,
                        System.currentTimeMillis(),
                        plugin.getServer().getName()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to log command", e);
            }
        });
    }
}

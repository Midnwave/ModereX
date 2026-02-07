package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.ReplaySnapshot;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
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
                        Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
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
            Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.CMD_BLACKLIST_BLOCKED));
            return;
        }

        // Process message commands through automod (e.g., /msg, /tell, /whisper)
        if (isMessageCommand(baseCommand)) {
            String[] parts = fullCommand.split(" ", 3); // command, target, message
            if (parts.length >= 3) {
                String targetPlayer = parts[1];
                String message = parts[2];
                var result = plugin.getAutomodManager().processCommandMessage(player, baseCommand, message);
                if (result.isBlocked()) {
                    event.setCancelled(true);
                    Msg.send(player, plugin.getLanguageManager().getPrefixed(
                            com.blockforge.moderex.config.lang.MessageKey.AUTOMOD_BLOCKED));
                    return;
                }

                // Broadcast PM to staff based on their settings
                broadcastPrivateMessage(player, targetPlayer, message);
            }
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

        // Broadcast to web panel live logs
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastCommand(player.getName(), uuid, "/" + fullCommand);
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

    /**
     * Check if a command is a message command that should be filtered.
     * This includes commands from any plugin that send messages to other players.
     */
    private boolean isMessageCommand(String command) {
        String cmd = command.toLowerCase();
        // Handle plugin:command format
        if (cmd.contains(":")) {
            cmd = cmd.split(":")[1];
        }

        // Common message commands from various plugins
        return MESSAGE_COMMANDS.contains(cmd);
    }

    // Message commands that should be filtered (from any plugin)
    private static final java.util.Set<String> MESSAGE_COMMANDS = java.util.Set.of(
            "msg", "message", "tell", "whisper", "w", "m", "pm", "dm",
            "r", "reply", "er", "emsg", "etell", "ewhisper", "ew",
            "mail", "helpop", "ac", "adminchat", "socialspy",
            "tpa", "tpahere", "call", "tpask" // Include teleport request messages
    );

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

    /**
     * Broadcasts private messages to staff based on their alert settings.
     * Staff will only see PMs if their privateMessageAlerts setting allows it.
     */
    private void broadcastPrivateMessage(Player sender, String targetName, String message) {
        UUID senderUuid = sender.getUniqueId();
        boolean isWatched = plugin.getWatchlistManager().isWatched(senderUuid);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            // Skip the sender and target
            if (staff.equals(sender) || staff.getName().equalsIgnoreCase(targetName)) {
                continue;
            }

            // Must have permission to see PM alerts
            if (!staff.hasPermission("moderex.notify.pm")) {
                continue;
            }

            // Check staff's privateMessageAlerts setting
            var settings = plugin.getStaffSettingsManager().getSettings(staff);
            var alertLevel = settings.getPrivateMessageAlerts();

            if (!settings.shouldShowAlert(alertLevel, isWatched)) {
                continue;
            }

            // Build and send the alert
            String wlPrefix = isWatched ? "<yellow>[WL] </yellow>" : "";
            String alertMsg = "<dark_gray>[<gradient:#a855f7:#ec4899>PM</gradient>]</dark_gray> " +
                    wlPrefix + "<gray>" + sender.getName() + "</gray> <dark_gray>→</dark_gray> <gray>" + targetName + "</gray>: <white>" + message + "</white>";
            Msg.send(staff, TextUtil.parse(alertMsg));
        }

        // Also send to web panel if enabled
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPrivateMessage(sender.getName(), senderUuid, targetName, message);
        }
    }
}

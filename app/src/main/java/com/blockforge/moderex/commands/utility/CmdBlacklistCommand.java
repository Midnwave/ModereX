package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CmdBlacklistCommand extends BaseCommand {

    public CmdBlacklistCommand(ModereX plugin) {
        super(plugin, "moderex.command.cmdblacklist", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /cmdblacklist <player> <command> [duration]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        String command = args[1].toLowerCase();
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        long duration = -1; // Permanent by default
        if (args.length >= 3 && DurationParser.isValidDuration(args[2])) {
            duration = DurationParser.parse(args[2]);
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        long expiresAt = duration == -1 ? -1 : System.currentTimeMillis() + duration;

        final String finalCommand = command;
        final long finalExpiresAt = expiresAt;

        // Save to database
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_command_blacklist
                        (player_uuid, command, staff_uuid, staff_name, created_at, expires_at, reason, server)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        targetUuid.toString(),
                        finalCommand,
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        System.currentTimeMillis(),
                        finalExpiresAt,
                        "Blacklisted by " + staffName,
                        plugin.getServer().getName()
                );

                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, MessageKey.CMD_BLACKLIST_SUCCESS,
                                "command", "/" + finalCommand,
                                "player", displayName));
            } catch (SQLException e) {
                plugin.logError("Failed to blacklist command", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to blacklist command."));
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 3) {
            return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "permanent"), args[2]);
        }
        return super.tabComplete(sender, args);
    }
}

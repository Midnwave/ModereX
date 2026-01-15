package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CmdUnblacklistCommand extends BaseCommand {

    public CmdUnblacklistCommand(ModereX plugin) {
        super(plugin, "moderex.command.cmdunblacklist", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /cmdunblacklist <player> <command>");
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

        final String finalCommand = command;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int deleted = plugin.getDatabaseManager().update("""
                        DELETE FROM moderex_command_blacklist
                        WHERE player_uuid = ? AND command = ?
                        """,
                        targetUuid.toString(),
                        finalCommand
                );

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (deleted > 0) {
                        sendMessage(sender, MessageKey.CMD_BLACKLIST_REMOVED,
                                "player", displayName);
                    } else {
                        sendMessage(sender, "<yellow>No blacklist found for that command.");
                    }
                });
            } catch (SQLException e) {
                plugin.logError("Failed to remove command blacklist", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to remove command blacklist."));
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}

package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WarnCommand extends BaseCommand {

    public WarnCommand(ModereX plugin) {
        super(plugin, "moderex.command.warn", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /warn <player> [duration] [reason]");
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

        // default 30 day warning expiry
        long duration = DurationParser.parse("30d");
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().warn(targetUuid, displayName, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    sendMessage(sender, MessageKey.WARN_SUCCESS, "player", displayName);
                });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("1d", "7d", "30d", "permanent"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}

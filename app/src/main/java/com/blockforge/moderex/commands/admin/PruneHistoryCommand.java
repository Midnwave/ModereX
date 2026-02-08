package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Remove inactive punishments from history.
 * This also reverts template progression when applicable.
 * If duration specified, only removes punishments older than that duration.
 */
public class PruneHistoryCommand extends BaseCommand {

    public PruneHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.prunehistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /prunehistory <user> [duration]");
            sendMessage(sender, "<gray>Example: /prunehistory Player123 30d (removes inactive punishments older than 30 days)");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer() || target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        boolean hasConfirm = false;
        long duration = -1;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--confirm")) {
                hasConfirm = true;
            } else if (DurationParser.isValidDuration(args[i])) {
                duration = DurationParser.parse(args[i]);
            }
        }

        final long finalDuration = duration;

        if (!hasConfirm) {
            sendMessage(sender, MessageKey.PRUNEHISTORY_CONFIRM,
                    "player", target.getDisplayName());
            if (duration != -1) {
                sendMessage(sender, MessageKey.PRUNEHISTORY_DURATION_INFO,
                        "duration", DurationParser.format(duration));
            }
            sendMessage(sender, "<yellow>Run the command again with <gold>--confirm <yellow>to proceed.");
            return;
        }

        plugin.getPunishmentManager().pruneHistory(target.getUuid(), finalDuration).thenAccept(count -> {
            if (count == 0) {
                sendMessage(sender, MessageKey.PRUNEHISTORY_EMPTY,
                        "player", target.getDisplayName());
            } else {
                sendMessage(sender, MessageKey.PRUNEHISTORY_SUCCESS,
                        "count", String.valueOf(count),
                        "player", target.getDisplayName());
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length >= 2) {
            return filterCompletions(Arrays.asList("7d", "30d", "90d", "1y", "--confirm"), args[args.length - 1]);
        }
        return super.tabComplete(sender, args);
    }
}

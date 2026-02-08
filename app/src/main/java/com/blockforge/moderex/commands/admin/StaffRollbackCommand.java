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
 * Rollback all executed punishments for a given staff member.
 * If duration is specified, only rollback punishments within that timeframe.
 * Console can be rolled back via /staffrollback [CONSOLE]
 * This also reverts template progression when applicable.
 */
public class StaffRollbackCommand extends BaseCommand {

    public StaffRollbackCommand(ModereX plugin) {
        super(plugin, "moderex.staffrollback", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /staffrollback <staff> [duration]");
            sendMessage(sender, "<gray>Example: /staffrollback BadStaff 1d (rollback all punishments from last 24 hours)");
            sendMessage(sender, "<gray>Use [CONSOLE] to rollback console punishments");
            return;
        }

        String targetName = args[0];
        boolean isConsole = targetName.equalsIgnoreCase("[CONSOLE]") || targetName.equalsIgnoreCase("CONSOLE");

        TargetResolver target = null;
        if (!isConsole) {
            target = new TargetResolver(targetName);
            if (!target.isValid() || !target.isPlayer() || target.getUuid() == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
                return;
            }
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

        final String displayName = isConsole ? "CONSOLE" : target.getDisplayName();
        final long finalDuration = duration;
        final TargetResolver finalTarget = target;

        if (!hasConfirm) {
            sendMessage(sender, MessageKey.STAFFROLLBACK_CONFIRM,
                    "staff", displayName);
            if (duration != -1) {
                sendMessage(sender, MessageKey.STAFFROLLBACK_DURATION_INFO,
                        "duration", DurationParser.format(duration));
            }
            sendMessage(sender, "<red><bold>Warning: <yellow>This is a destructive operation!");
            sendMessage(sender, "<yellow>Run the command again with <gold>--confirm <yellow>to proceed.");
            return;
        }

        java.util.UUID staffUuid = isConsole ? null : finalTarget.getUuid();
        plugin.getPunishmentManager().rollbackStaff(staffUuid, finalDuration).thenAccept(count -> {
            if (count == 0) {
                sendMessage(sender, MessageKey.STAFFROLLBACK_EMPTY,
                        "staff", displayName);
            } else {
                sendMessage(sender, MessageKey.STAFFROLLBACK_SUCCESS,
                        "count", String.valueOf(count),
                        "staff", displayName);

                plugin.broadcastToPermission(MessageKey.STAFFROLLBACK_BROADCAST,
                        "moderex.notify.staffrollback",
                        "count", String.valueOf(count),
                        "staff", displayName,
                        "executor", sender.getName());
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new java.util.ArrayList<>(getOnlinePlayerNames(sender));
            completions.add("[CONSOLE]");
            return filterCompletions(completions, args[0]);
        }
        if (args.length >= 2) {
            return filterCompletions(Arrays.asList("1h", "6h", "12h", "1d", "3d", "7d", "30d", "--confirm"), args[args.length - 1]);
        }
        return super.tabComplete(sender, args);
    }
}

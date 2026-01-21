package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Warns a player. Warnings can stack and trigger template actions.
 */
public class WarnCommand extends PunishmentCommandBase {

    public WarnCommand(ModereX plugin) {
        super(plugin, "moderex.warn", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /warn <player> [duration] [reason] [flags]");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null || flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        if (!checkFlagPermissions(sender, flagParser)) {
            return;
        }

        TargetResolver target = new TargetResolver(regularArgs.get(0));

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        if (target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", target.getDisplayName());
            return;
        }

        long duration = DurationParser.parse("30d");
        String reason = "No reason specified";

        if (regularArgs.size() >= 2) {
            if (DurationParser.isValidDuration(regularArgs.get(1))) {
                duration = DurationParser.parse(regularArgs.get(1));
                if (regularArgs.size() >= 3) {
                    reason = flagParser.joinArgs(2);
                }
            } else {
                reason = flagParser.joinArgs(1);
            }
        }

        PunishmentContext context = PunishmentContext.builder(sender)
                .target(target)
                .flags(flagParser)
                .duration(duration)
                .reason(reason)
                .build();

        if (flagParser.isDelete()) {
            handleDelete(context);
            return;
        }

        if (flagParser.isModify()) {
            handleModify(context);
            return;
        }

        executeWarn(context);
    }

    private void handleDelete(PunishmentContext context) {
        String targetStr = context.getTarget().getInput();
        if (!targetStr.startsWith("#")) {
            sendMessage(context.getSender(), "<red>Delete requires a punishment ID (e.g., #123)");
            return;
        }

        try {
            long punishmentId = Long.parseLong(targetStr.substring(1));

            plugin.getPunishmentManager().getPunishmentById(punishmentId).thenAccept(punishment -> {
                if (punishment == null) {
                    sendMessage(context.getSender(), MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(punishmentId));
                    return;
                }

                plugin.getPunishmentManager().deletePunishment(
                    punishmentId,
                    context.getExecutorUuid(),
                    context.getExecutorName()
                ).thenAccept(success -> {
                    if (success) {
                        sendMessage(context.getSender(), MessageKey.DELETE_SUCCESS,
                            "id", String.valueOf(punishmentId));
                        sendMessage(context.getSender(), MessageKey.DELETE_REVERTED_TEMPLATE,
                            "player", punishment.getPlayerName());
                    } else {
                        sendMessage(context.getSender(), "<red>Failed to delete punishment.");
                    }
                });
            });
        } catch (NumberFormatException e) {
            sendMessage(context.getSender(), "<red>Invalid punishment ID format.");
        }
    }

    private void handleModify(PunishmentContext context) {
        String targetStr = context.getTarget().getInput();
        if (!targetStr.startsWith("#")) {
            sendMessage(context.getSender(), "<red>Modify requires a punishment ID (e.g., #123)");
            return;
        }

        try {
            long punishmentId = Long.parseLong(targetStr.substring(1));
            long newDuration = context.getDuration() != null ? context.getDuration() : -1;
            String newReason = context.getReason();

            plugin.getPunishmentManager().modifyPunishment(
                punishmentId,
                newDuration,
                newReason,
                context.getExecutorUuid(),
                context.getExecutorName()
            ).thenAccept(success -> {
                if (success) {
                    sendMessage(context.getSender(), MessageKey.MODIFY_SUCCESS,
                        "id", String.valueOf(punishmentId));
                } else {
                    sendMessage(context.getSender(), "<red>Failed to modify punishment. Checks ID exists.");
                }
            });
        } catch (NumberFormatException e) {
            sendMessage(context.getSender(), "<red>Invalid punishment ID format.");
        }
    }

    @Override
    protected boolean hasIpPermission(CommandSender sender) {
        return sender.hasPermission("moderex.ipwarn");
    }

    private void executeWarn(PunishmentContext context) {
        TargetResolver target = context.getTarget();
        long duration = context.getDuration() != null ? context.getDuration() : DurationParser.parse("30d");
        String reason = context.getReason();

        if (context.isIpBased()) {
            sendMessage(context.getSender(), "<yellow>IP warn functionality not supported. Use regular warn.");
            return;
        }

        plugin.getPunishmentManager().warn(
                target.getUuid(),
                target.getDisplayName(),
                context.getExecutorUuid(),
                context.getExecutorName(),
                duration,
                reason
        ).thenAccept(punishment -> {
            sendMessage(context.getSender(), MessageKey.WARN_SUCCESS,
                    "player", target.getDisplayName());

            broadcastPunishment(context, target.getDisplayName(), reason);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String reason) {
        if (context.isHidden()) {
            return;
        }

        if (context.isExtraSilent()) {
            plugin.getLogger().info(String.format("%s warned %s. Reason: %s",
                    context.getExecutorName(), playerName, reason));
            return;
        }

        if (context.isSilent()) {
            return;
        }

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(MessageKey.WARN_BROADCAST,
                    "staff", context.getExecutorName(),
                    "player", playerName,
                    "reason", reason);

            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.notify.punishments")) {
                    player.sendMessage(message);
                }
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("1d", "7d", "30d", "1y", "permanent"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}

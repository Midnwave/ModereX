package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.log.ActivityLogEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Mutes a player, preventing them from sending chat messages or using certain commands.
 * Supports various flags for customization.
 */
public class MuteCommand extends PunishmentCommandBase {

    public MuteCommand(ModereX plugin) {
        super(plugin, "moderex.mute", false);
    }

    @Override
    protected boolean hasIpPermission(CommandSender sender) {
        return sender.hasPermission("moderex.ipmute");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /mute <player> [duration] [reason] [flags]");
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

        if (!flagParser.isModify() && !flagParser.isDelete()) {
            if (plugin.getPunishmentManager().isMuted(target.getUuid())) {
                sendMessage(sender, MessageKey.MUTE_ALREADY_MUTED, "player", target.getDisplayName());
                return;
            }
        }

        long duration = -1;
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

        // Check if evidence selection should be triggered (in-game players only)
        boolean requiresEvidence = plugin.getConfigManager().getSettings().isEvidenceRequireEvidence();
        boolean wantsEvidence = flagParser.isEvidence();
        final long finalDuration = duration;
        final String finalReason = reason;

        if ((requiresEvidence || wantsEvidence) && sender instanceof org.bukkit.entity.Player player) {
            // Start evidence selection session
            boolean started = plugin.getEvidenceSelectionManager().startSession(
                    player, target, "MUTE", finalDuration, finalReason,
                    result -> {
                        if (result.isConfirmed()) {
                            executeMuteWithEvidence(context, result.getSelectedEntries());
                        }
                    }
            );

            if (started) {
                return; // Wait for evidence selection
            }
        }

        executeMute(context);
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

    private void executeMute(PunishmentContext context) {
        TargetResolver target = context.getTarget();
        long duration = context.getDuration() != null ? context.getDuration() : -1;
        String reason = context.getReason();

        if (context.isIpBased()) {
            org.bukkit.entity.Player onlinePlayer = org.bukkit.Bukkit.getPlayer(target.getUuid());
            if (onlinePlayer == null || onlinePlayer.getAddress() == null) {
                sendMessage(context.getSender(), MessageKey.PLAYER_NOT_ONLINE, "player", target.getDisplayName());
                sendMessage(context.getSender(), "<gray>Use /ipmute <player> for online players only.");
                return;
            }

            String ipAddress = onlinePlayer.getAddress().getAddress().getHostAddress();

            plugin.getPunishmentManager().ipMute(
                    target.getUuid(),
                    target.getDisplayName(),
                    ipAddress,
                    context.getExecutorUuid(),
                    context.getExecutorName(),
                    duration,
                    reason
            ).thenAccept(punishment -> {
                String durationStr = DurationParser.format(duration);
                sendMessage(context.getSender(), MessageKey.IPMUTE_SUCCESS,
                        "player", target.getDisplayName(),
                        "duration", durationStr);

                broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.IPMUTE_BROADCAST);
            });
            return;
        }

        plugin.getPunishmentManager().mute(
                target.getUuid(),
                target.getDisplayName(),
                context.getExecutorUuid(),
                context.getExecutorName(),
                duration,
                reason
        ).thenAccept(punishment -> {
            String durationStr = DurationParser.format(duration);
            sendMessage(context.getSender(), MessageKey.MUTE_SUCCESS,
                    "player", target.getDisplayName(),
                    "duration", durationStr);

            broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.MUTE_BROADCAST);
        });
    }

    private void executeMuteWithEvidence(PunishmentContext context, List<ActivityLogEntry> evidenceEntries) {
        TargetResolver target = context.getTarget();
        long duration = context.getDuration() != null ? context.getDuration() : -1;
        String reason = context.getReason();

        if (context.isIpBased()) {
            org.bukkit.entity.Player onlinePlayer = org.bukkit.Bukkit.getPlayer(target.getUuid());
            if (onlinePlayer == null || onlinePlayer.getAddress() == null) {
                sendMessage(context.getSender(), MessageKey.PLAYER_NOT_ONLINE, "player", target.getDisplayName());
                sendMessage(context.getSender(), "<gray>Use /ipmute <player> for online players only.");
                return;
            }

            String ipAddress = onlinePlayer.getAddress().getAddress().getHostAddress();

            plugin.getPunishmentManager().ipMute(
                    target.getUuid(),
                    target.getDisplayName(),
                    ipAddress,
                    context.getExecutorUuid(),
                    context.getExecutorName(),
                    duration,
                    reason
            ).thenAccept(punishment -> {
                // Link evidence to punishment
                if (punishment != null && !evidenceEntries.isEmpty()) {
                    plugin.getPunishmentManager().linkActivityLogEvidence(
                            punishment.getCaseId(),
                            evidenceEntries,
                            context.getExecutorUuid() != null ? context.getExecutorUuid().toString() : "CONSOLE",
                            context.getExecutorName()
                    );
                }

                String durationStr = DurationParser.format(duration);
                sendMessage(context.getSender(), MessageKey.IPMUTE_SUCCESS,
                        "player", target.getDisplayName(),
                        "duration", durationStr);

                broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.IPMUTE_BROADCAST);
            });
            return;
        }

        plugin.getPunishmentManager().mute(
                target.getUuid(),
                target.getDisplayName(),
                context.getExecutorUuid(),
                context.getExecutorName(),
                duration,
                reason
        ).thenAccept(punishment -> {
            // Link evidence to punishment
            if (punishment != null && !evidenceEntries.isEmpty()) {
                plugin.getPunishmentManager().linkActivityLogEvidence(
                        punishment.getCaseId(),
                        evidenceEntries,
                        context.getExecutorUuid() != null ? context.getExecutorUuid().toString() : "CONSOLE",
                        context.getExecutorName()
                );
            }

            String durationStr = DurationParser.format(duration);
            sendMessage(context.getSender(), MessageKey.MUTE_SUCCESS,
                    "player", target.getDisplayName(),
                    "duration", durationStr);

            broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.MUTE_BROADCAST);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String duration, String reason, MessageKey broadcastKey) {
        if (context.isHidden()) {
            return;
        }

        if (context.isExtraSilent()) {
            plugin.getLogger().info(String.format("%s muted %s for %s. Reason: %s",
                    context.getExecutorName(), playerName, duration, reason));
            return;
        }

        if (context.isSilent()) {
            return;
        }

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(broadcastKey,
                    "staff", context.getExecutorName(),
                    "player", playerName,
                    "duration", duration,
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
            return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "1y", "permanent"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}

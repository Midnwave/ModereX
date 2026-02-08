package com.blockforge.moderex.commands.moderation.check;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Checks whether a player has active warnings and display warning information.
 * Can use player name, UUID, or punishment ID.
 */
public class CheckWarnCommand extends BaseCommand {

    public CheckWarnCommand(ModereX plugin) {
        super(plugin, "moderex.checkwarn", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /checkwarn <user|id>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        if (target.isPunishmentId()) {
            plugin.getPunishmentManager().getPunishmentById(target.getPunishmentId()).thenAccept(punishment -> {
                if (punishment == null || punishment.getType() != com.blockforge.moderex.punishment.PunishmentType.WARN) {
                    sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(target.getPunishmentId()));
                    return;
                }

                sendMessage(sender, MessageKey.CHECKWARN_HEADER, "player", punishment.getPlayerName());
                displayWarningDetails(sender, punishment);
            });
            return;
        }

        if (target.isPlayer() && target.getUuid() != null) {
            plugin.getPunishmentManager().getActiveWarnings(target.getUuid()).thenAccept(warnings -> {
                sendMessage(sender, MessageKey.CHECKWARN_HEADER, "player", target.getDisplayName());

                if (warnings.isEmpty()) {
                    sendMessage(sender, MessageKey.CHECKWARN_NO_WARNINGS, "player", target.getDisplayName());
                } else {
                    sendMessage(sender, MessageKey.CHECKWARN_HAS_WARNINGS,
                            "player", target.getDisplayName(),
                            "count", String.valueOf(warnings.size()));

                    // Sort: active first, then expired/revoked; newest first within each group
                    List<com.blockforge.moderex.punishment.Punishment> sorted = new ArrayList<>(warnings);
                    sorted.sort(Comparator
                            .<com.blockforge.moderex.punishment.Punishment, Boolean>comparing(
                                    p -> !(p.isActive() && !p.isExpired()))
                            .thenComparing(Comparator.comparingLong(
                                    com.blockforge.moderex.punishment.Punishment::getCreatedAt).reversed()));

                    for (com.blockforge.moderex.punishment.Punishment warning : sorted) {
                        displayWarningDetails(sender, warning);
                    }
                }
            });
        } else {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
        }
    }

    private void displayWarningDetails(CommandSender sender, com.blockforge.moderex.punishment.Punishment punishment) {
        String expiry = punishment.isPermanent() ? "Never" :
            com.blockforge.moderex.util.DurationParser.formatRemaining(punishment.getExpiresAt());
        String date = com.blockforge.moderex.util.TimeUtil.formatFull(punishment.getCreatedAt());

        sendMessage(sender, MessageKey.CHECKWARN_WARNING_ENTRY,
                "date", date,
                "reason", punishment.getReason(),
                "staff", punishment.getStaffName(),
                "expiry", expiry,
                "case_id", punishment.getCaseId());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}

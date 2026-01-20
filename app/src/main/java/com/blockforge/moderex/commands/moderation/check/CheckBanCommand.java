package com.blockforge.moderex.commands.moderation.check;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * /checkban <user>
 *
 * Check whether a player is banned and display active ban information.
 * Can use player name, UUID, or punishment ID.
 */
public class CheckBanCommand extends BaseCommand {

    public CheckBanCommand(ModereX plugin) {
        super(plugin, "moderex.checkban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /checkban <user|id>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        // Handle by punishment ID
        if (target.isPunishmentId()) {
            plugin.getPunishmentManager().getPunishmentById(target.getPunishmentId()).thenAccept(punishment -> {
                if (punishment == null || punishment.getType() != com.blockforge.moderex.punishment.PunishmentType.BAN) {
                    sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(target.getPunishmentId()));
                    return;
                }

                displayBanDetails(sender, punishment);
            });
            return;
        }

        // Handle by player
        if (target.isPlayer() && target.getUuid() != null) {
            com.blockforge.moderex.punishment.Punishment punishment =
                plugin.getPunishmentManager().getActivePunishment(target.getUuid(), com.blockforge.moderex.punishment.PunishmentType.BAN);

            if (punishment != null) {
                sendMessage(sender, MessageKey.CHECKBAN_HEADER, "player", target.getDisplayName());
                sendMessage(sender, MessageKey.CHECKBAN_BANNED, "player", target.getDisplayName());
                displayBanDetails(sender, punishment);
            } else {
                sendMessage(sender, MessageKey.CHECKBAN_HEADER, "player", target.getDisplayName());
                sendMessage(sender, MessageKey.CHECKBAN_NOT_BANNED, "player", target.getDisplayName());
            }
        } else {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
        }
    }

    private void displayBanDetails(CommandSender sender, com.blockforge.moderex.punishment.Punishment punishment) {
        String duration = punishment.isPermanent() ? "Permanent" :
            com.blockforge.moderex.util.DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt());
        String expiry = punishment.isPermanent() ? "Never" :
            com.blockforge.moderex.util.DurationParser.formatRemaining(punishment.getExpiresAt());
        String date = com.blockforge.moderex.util.TimeUtil.formatFull(punishment.getCreatedAt());

        sendMessage(sender, MessageKey.CHECKBAN_DETAILS,
                "staff", punishment.getStaffName(),
                "date", date,
                "duration", duration,
                "reason", punishment.getReason(),
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

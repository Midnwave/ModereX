package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * /staffhistory <user> [type]
 * Alias: /staffhist
 *
 * Shows punishments executed by a staff member.
 * Optional type filter: bans, mutes, warnings, kicks, all
 */
public class StaffHistoryCommand extends BaseCommand {

    public StaffHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.staffhistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /staffhistory <staff> [type]");
            sendMessage(sender, "<gray>Types: all, bans, mutes, warnings, kicks");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        String type = args.length > 1 ? args[1].toLowerCase() : "all";

        if (!isValidType(type)) {
            sendMessage(sender, "<red>Invalid type. Use: all, bans, mutes, warnings, kicks");
            return;
        }

        PunishmentType punishmentType = null;
        if (!type.equals("all")) {
            punishmentType = switch (type) {
                case "bans" -> PunishmentType.BAN;
                case "mutes" -> PunishmentType.MUTE;
                case "warnings", "warns" -> PunishmentType.WARN;
                case "kicks" -> PunishmentType.KICK;
                default -> null;
            };
        }

        final PunishmentType finalType = punishmentType;

        plugin.getPunishmentManager().getStaffHistory(target.getUuid(), finalType).thenAccept(history -> {
            if (history.isEmpty()) {
                sendMessage(sender, MessageKey.STAFFHISTORY_HEADER,
                        "staff", target.getDisplayName(),
                        "filter", type);
                sendMessage(sender, MessageKey.STAFFHISTORY_EMPTY, "staff", target.getDisplayName());
                return;
            }

            sendMessage(sender, MessageKey.STAFFHISTORY_HEADER,
                    "staff", target.getDisplayName(),
                    "filter", type);

            for (Punishment punishment : history) {
                String status = punishment.isActive() ? "<green>Active</green>" :
                              (punishment.isRemoved() ? "<red>Revoked</red>" : "<yellow>Expired</yellow>");
                String duration = punishment.isPermanent() ? "Permanent" :
                        DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt());
                String date = TimeUtil.formatFull(punishment.getCreatedAt());

                sendMessage(sender, MessageKey.STAFFHISTORY_ENTRY,
                        "id", String.valueOf(punishment.getId()),
                        "type", punishment.getType().getDisplayName(),
                        "player", punishment.getPlayerName(),
                        "reason", punishment.getReason(),
                        "duration", duration,
                        "date", date,
                        "status", status);
            }

            sendMessage(sender, MessageKey.STAFFHISTORY_FOOTER,
                    "count", String.valueOf(history.size()),
                    "page", "1",
                    "total", "1",
                    "staff", target.getDisplayName());
        });
    }

    private boolean isValidType(String type) {
        return Arrays.asList("all", "bans", "mutes", "warnings", "warns", "kicks").contains(type);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("all", "bans", "mutes", "warnings", "kicks"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}

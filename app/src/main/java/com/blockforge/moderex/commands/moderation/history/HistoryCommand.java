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
 * /history <user> [type]
 * Alias: /hist
 *
 * Display punishment history for a player.
 * Optional type filter: bans, mutes, warnings, kicks, all
 */
public class HistoryCommand extends BaseCommand {

    public HistoryCommand(ModereX plugin) {
        super(plugin, "moderex.history", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /history <user> [type]");
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

        // Convert type string to PunishmentType enum
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

        // Fetch and display punishment history from database
        plugin.getPunishmentManager().getHistory(target.getUuid(), finalType).thenAccept(history -> {
            if (history.isEmpty()) {
                sendMessage(sender, MessageKey.HISTORY_HEADER,
                        "player", target.getDisplayName(),
                        "filter", type);
                sendMessage(sender, MessageKey.HISTORY_EMPTY, "player", target.getDisplayName());
                return;
            }

            // Display header
            sendMessage(sender, MessageKey.HISTORY_HEADER,
                    "player", target.getDisplayName(),
                    "filter", type);

            if (!type.equals("all")) {
                sendMessage(sender, MessageKey.HISTORY_TYPE_FILTER, "type", type);
            }

            // Display each punishment
            for (Punishment punishment : history) {
                String status = punishment.isActive() ? "<green>Active</green>" :
                              (punishment.isRemoved() ? "<red>Revoked</red>" : "<yellow>Expired</yellow>");
                String duration = punishment.isPermanent() ? "Permanent" :
                        DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt());
                String date = TimeUtil.formatFull(punishment.getCreatedAt());

                sendMessage(sender, MessageKey.HISTORY_ENTRY,
                        "id", String.valueOf(punishment.getId()),
                        "type", punishment.getType().getDisplayName(),
                        "reason", punishment.getReason(),
                        "duration", duration,
                        "staff", punishment.getStaffName(),
                        "date", date,
                        "status", status);
            }

            // Display footer (no pagination implemented yet, so show count as both page and total)
            sendMessage(sender, MessageKey.HISTORY_FOOTER,
                    "count", String.valueOf(history.size()),
                    "page", "1",
                    "total", "1",
                    "player", target.getDisplayName());
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

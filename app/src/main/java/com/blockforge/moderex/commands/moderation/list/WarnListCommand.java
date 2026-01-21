package com.blockforge.moderex.commands.moderation.list;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Shows a paginated list of active warnings.
 */
public class WarnListCommand extends BaseCommand {

    private static final int WARNINGS_PER_PAGE = 10;

    public WarnListCommand(ModereX plugin) {
        super(plugin, "moderex.warnlist", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                sendMessage(sender, "<red>Invalid page number. Usage: /warnlist [page]");
                return;
            }
        }

        final int finalPage = page;

        plugin.getPunishmentManager().getActivePunishmentsList(PunishmentType.WARN, page, WARNINGS_PER_PAGE).thenAccept(warnings -> {
            plugin.getPunishmentManager().getActivePunishmentsCount(PunishmentType.WARN).thenAccept(totalWarnings -> {
                int totalPages = (int) Math.ceil((double) totalWarnings / WARNINGS_PER_PAGE);

                if (warnings.isEmpty()) {
                    sendMessage(sender, MessageKey.WARNLIST_HEADER);
                    sendMessage(sender, MessageKey.WARNLIST_EMPTY);
                    return;
                }

                sendMessage(sender, MessageKey.WARNLIST_HEADER);

                for (Punishment warning : warnings) {
                    String duration = warning.isPermanent() ? "Permanent" :
                        DurationParser.format(warning.getExpiresAt() - warning.getCreatedAt());

                    sendMessage(sender, MessageKey.WARNLIST_ENTRY,
                            "player", warning.getPlayerName(),
                            "staff", warning.getStaffName(),
                            "reason", warning.getReason(),
                            "duration", duration);
                }

                sendMessage(sender, MessageKey.WARNLIST_FOOTER,
                        "page", String.valueOf(finalPage),
                        "total", String.valueOf(totalPages));
            });
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(List.of("1", "2", "3"), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}

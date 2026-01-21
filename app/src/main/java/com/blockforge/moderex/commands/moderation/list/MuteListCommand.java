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
 * Shows a paginated list of active mutes.
 */
public class MuteListCommand extends BaseCommand {

    private static final int MUTES_PER_PAGE = 10;

    public MuteListCommand(ModereX plugin) {
        super(plugin, "moderex.mutelist", false);
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
                sendMessage(sender, "<red>Invalid page number. Usage: /mutelist [page]");
                return;
            }
        }

        final int finalPage = page;

        plugin.getPunishmentManager().getActivePunishmentsList(PunishmentType.MUTE, page, MUTES_PER_PAGE).thenAccept(mutes -> {
            plugin.getPunishmentManager().getActivePunishmentsCount(PunishmentType.MUTE).thenAccept(totalMutes -> {
                int totalPages = (int) Math.ceil((double) totalMutes / MUTES_PER_PAGE);

                if (mutes.isEmpty()) {
                    sendMessage(sender, MessageKey.MUTELIST_HEADER);
                    sendMessage(sender, MessageKey.MUTELIST_EMPTY);
                    return;
                }

                sendMessage(sender, MessageKey.MUTELIST_HEADER);

                for (Punishment mute : mutes) {
                    String duration = mute.isPermanent() ? "Permanent" :
                        DurationParser.format(mute.getExpiresAt() - mute.getCreatedAt());

                    sendMessage(sender, MessageKey.MUTELIST_ENTRY,
                            "player", mute.getPlayerName(),
                            "staff", mute.getStaffName(),
                            "reason", mute.getReason(),
                            "duration", duration);
                }

                sendMessage(sender, MessageKey.MUTELIST_FOOTER,
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

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
 * /banlist [page]
 *
 * Display a paginated list of active bans.
 */
public class BanListCommand extends BaseCommand {

    private static final int BANS_PER_PAGE = 10;

    public BanListCommand(ModereX plugin) {
        super(plugin, "moderex.banlist", false);
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
                sendMessage(sender, "<red>Invalid page number. Usage: /banlist [page]");
                return;
            }
        }

        final int finalPage = page;

        // Fetch active bans
        plugin.getPunishmentManager().getActivePunishmentsList(PunishmentType.BAN, page, BANS_PER_PAGE).thenAccept(bans -> {
            plugin.getPunishmentManager().getActivePunishmentsCount(PunishmentType.BAN).thenAccept(totalBans -> {
                int totalPages = (int) Math.ceil((double) totalBans / BANS_PER_PAGE);

                if (bans.isEmpty()) {
                    sendMessage(sender, MessageKey.BANLIST_HEADER);
                    sendMessage(sender, MessageKey.BANLIST_EMPTY);
                    return;
                }

                // Display header
                sendMessage(sender, MessageKey.BANLIST_HEADER);

                // Display each ban
                for (Punishment ban : bans) {
                    String duration = ban.isPermanent() ? "Permanent" :
                        DurationParser.format(ban.getExpiresAt() - ban.getCreatedAt());

                    sendMessage(sender, MessageKey.BANLIST_ENTRY,
                            "player", ban.getPlayerName(),
                            "staff", ban.getStaffName(),
                            "reason", ban.getReason(),
                            "duration", duration);
                }

                // Display footer
                sendMessage(sender, MessageKey.BANLIST_FOOTER,
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

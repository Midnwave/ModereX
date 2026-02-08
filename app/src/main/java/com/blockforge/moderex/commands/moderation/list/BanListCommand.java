package com.blockforge.moderex.commands.moderation.list;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Shows a paginated list of active bans.
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

        plugin.getPunishmentManager().getActivePunishmentsList(PunishmentType.BAN, page, BANS_PER_PAGE).thenAccept(bans -> {
            plugin.getPunishmentManager().getActivePunishmentsCount(PunishmentType.BAN).thenAccept(totalBans -> {
                int totalPages = Math.max(1, (int) Math.ceil((double) totalBans / BANS_PER_PAGE));

                // Header with box-drawing
                Msg.send(sender, TextUtil.parse(""));
                Msg.send(sender, TextUtil.parse("<dark_gray>┌──────────────────┐"));
                Msg.send(sender, TextUtil.parse("<dark_gray>│ <red>Active Bans <dark_gray>(<white>" + totalBans + " total<dark_gray>)"));
                Msg.send(sender, TextUtil.parse("<dark_gray>├──────────────────┤"));

                if (bans.isEmpty()) {
                    Msg.send(sender, TextUtil.parse("<dark_gray>│ <gray>No active bans."));
                } else {
                    for (Punishment ban : bans) {
                        String duration = ban.isPermanent() ? "<dark_red>Permanent" :
                            "<white>" + DurationParser.formatRemaining(ban.getExpiresAt());
                        String reason = ban.getReason() != null ? ban.getReason() : "None";
                        if (reason.length() > 30) reason = reason.substring(0, 27) + "...";

                        if (sender instanceof Player) {
                            Component entry = TextUtil.parse("<dark_gray>│ <gray>[<yellow>#" + ban.getCaseId() +
                                    "<gray>] <red>" + ban.getPlayerName() +
                                    " <dark_gray>- <white>" + reason + " <dark_gray>(<white>" + duration + "<dark_gray>)")
                                .clickEvent(ClickEvent.runCommand("/viewpunishment " + ban.getCaseId()))
                                .hoverEvent(HoverEvent.showText(TextUtil.parse(
                                    "<gray>Case: <white>" + ban.getCaseId() + "\n" +
                                    "<gray>Reason: <white>" + (ban.getReason() != null ? ban.getReason() : "None") + "\n" +
                                    "<gray>Staff: <white>" + ban.getStaffName() + "\n" +
                                    "\n<yellow>Click to view details")));
                            Msg.send(sender, entry);
                        } else {
                            Msg.send(sender, TextUtil.parse("<dark_gray>│ <gray>[<yellow>#" + ban.getCaseId() +
                                    "<gray>] <red>" + ban.getPlayerName() +
                                    " <dark_gray>- <white>" + reason + " <dark_gray>(<white>" + duration + "<dark_gray>)"));
                        }
                    }
                }

                Msg.send(sender, TextUtil.parse("<dark_gray>└──────────────────┘"));

                // Navigation footer
                if (sender instanceof Player && totalPages > 1) {
                    Msg.send(sender, buildNavigationFooter(finalPage, totalPages));
                } else if (totalPages > 1) {
                    sendMessage(sender, "<gray>Page " + finalPage + "/" + totalPages + " - Use /banlist <page>");
                }
                Msg.send(sender, TextUtil.parse(""));
            });
        });
    }

    private Component buildNavigationFooter(int currentPage, int totalPages) {
        Component footer = Component.text("  « ", NamedTextColor.DARK_GRAY);

        if (currentPage > 1) {
            footer = footer.append(Component.text("[◀ Prev]", NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/banlist " + (currentPage - 1)))
                    .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage - 1), NamedTextColor.YELLOW))));
        } else {
            footer = footer.append(Component.text("[◀ Prev]", NamedTextColor.DARK_GRAY));
        }

        footer = footer.append(Component.text(" — ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Page ", NamedTextColor.GRAY))
                .append(Component.text(currentPage, NamedTextColor.GOLD))
                .append(Component.text("/", NamedTextColor.GRAY))
                .append(Component.text(totalPages, NamedTextColor.GOLD))
                .append(Component.text(" — ", NamedTextColor.DARK_GRAY));

        if (currentPage < totalPages) {
            footer = footer.append(Component.text("[Next ▶]", NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/banlist " + (currentPage + 1)))
                    .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage + 1), NamedTextColor.YELLOW))));
        } else {
            footer = footer.append(Component.text("[Next ▶]", NamedTextColor.DARK_GRAY));
        }

        return footer.append(Component.text(" »", NamedTextColor.DARK_GRAY));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(List.of("1", "2", "3", "4", "5"), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}

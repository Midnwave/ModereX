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
                int totalPages = Math.max(1, (int) Math.ceil((double) totalMutes / MUTES_PER_PAGE));

                // Header with box-drawing
                Msg.send(sender, TextUtil.parse(""));
                Msg.send(sender, TextUtil.parse("<dark_gray>┌──────────────────┐"));
                Msg.send(sender, TextUtil.parse("<dark_gray>│ <gold>Active Mutes <dark_gray>(<white>" + totalMutes + " total<dark_gray>)"));
                Msg.send(sender, TextUtil.parse("<dark_gray>├──────────────────┤"));

                if (mutes.isEmpty()) {
                    Msg.send(sender, TextUtil.parse("<dark_gray>│ <gray>No active mutes."));
                } else {
                    for (Punishment mute : mutes) {
                        String duration = mute.isPermanent() ? "<dark_red>Permanent" :
                            "<white>" + DurationParser.formatRemaining(mute.getExpiresAt());

                        if (sender instanceof Player) {
                            Component entry = TextUtil.parse("<dark_gray>│ <gold>" + mute.getPlayerName() +
                                    " <dark_gray>- " + duration)
                                .clickEvent(ClickEvent.runCommand("/viewpunishment " + mute.getCaseId()))
                                .hoverEvent(HoverEvent.showText(TextUtil.parse(
                                    "<gray>Case: <white>" + mute.getCaseId() + "\n" +
                                    "<gray>Reason: <white>" + (mute.getReason() != null ? mute.getReason() : "None") + "\n" +
                                    "<gray>Staff: <white>" + mute.getStaffName() + "\n" +
                                    "\n<yellow>Click to view details")));
                            Msg.send(sender, entry);
                        } else {
                            Msg.send(sender, TextUtil.parse("<dark_gray>│ <gold>" + mute.getPlayerName() +
                                    " <dark_gray>- " + duration + " <dark_gray>by <gray>" + mute.getStaffName()));
                        }
                    }
                }

                Msg.send(sender, TextUtil.parse("<dark_gray>└──────────────────┘"));

                // Navigation footer
                if (sender instanceof Player && totalPages > 1) {
                    Msg.send(sender, buildNavigationFooter(finalPage, totalPages));
                } else if (totalPages > 1) {
                    sendMessage(sender, "<gray>Page " + finalPage + "/" + totalPages + " - Use /mutelist <page>");
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
                    .clickEvent(ClickEvent.runCommand("/mutelist " + (currentPage - 1)))
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
                    .clickEvent(ClickEvent.runCommand("/mutelist " + (currentPage + 1)))
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

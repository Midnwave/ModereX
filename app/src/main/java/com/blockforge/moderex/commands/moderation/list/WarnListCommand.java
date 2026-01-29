package com.blockforge.moderex.commands.moderation.list;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                int totalPages = Math.max(1, (int) Math.ceil((double) totalWarnings / WARNINGS_PER_PAGE));

                // Header with box-drawing
                sender.sendMessage(TextUtil.parse(""));
                sender.sendMessage(TextUtil.parse("<dark_gray>┌──────────────────┐"));
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <aqua>Active Warnings <dark_gray>(<white>" + totalWarnings + " total<dark_gray>)"));
                sender.sendMessage(TextUtil.parse("<dark_gray>├──────────────────┤"));

                if (warnings.isEmpty()) {
                    sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>No active warnings."));
                } else {
                    for (Punishment warning : warnings) {
                        String date = TimeUtil.formatDate(warning.getCreatedAt());

                        if (sender instanceof Player) {
                            Component entry = TextUtil.parse("<dark_gray>│ <aqua>" + warning.getPlayerName() +
                                    " <dark_gray>- <gray>" + date)
                                .clickEvent(ClickEvent.runCommand("/viewpunishment " + warning.getCaseId()))
                                .hoverEvent(HoverEvent.showText(TextUtil.parse(
                                    "<gray>Case: <white>" + warning.getCaseId() + "\n" +
                                    "<gray>Reason: <white>" + (warning.getReason() != null ? warning.getReason() : "None") + "\n" +
                                    "<gray>Staff: <white>" + warning.getStaffName() + "\n" +
                                    "<gray>Date: <white>" + TimeUtil.formatDateTime(warning.getCreatedAt()) + "\n" +
                                    "\n<yellow>Click to view details")));
                            sender.sendMessage(entry);
                        } else {
                            sender.sendMessage(TextUtil.parse("<dark_gray>│ <aqua>" + warning.getPlayerName() +
                                    " <dark_gray>- <gray>" + date + " <dark_gray>by <gray>" + warning.getStaffName()));
                        }
                    }
                }

                sender.sendMessage(TextUtil.parse("<dark_gray>└──────────────────┘"));

                // Navigation footer
                if (sender instanceof Player && totalPages > 1) {
                    sender.sendMessage(buildNavigationFooter(finalPage, totalPages));
                } else if (totalPages > 1) {
                    sendMessage(sender, "<gray>Page " + finalPage + "/" + totalPages + " - Use /warnlist <page>");
                }
                sender.sendMessage(TextUtil.parse(""));
            });
        });
    }

    private Component buildNavigationFooter(int currentPage, int totalPages) {
        Component footer = Component.text("  « ", NamedTextColor.DARK_GRAY);

        if (currentPage > 1) {
            footer = footer.append(Component.text("[◀ Prev]", NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/warnlist " + (currentPage - 1)))
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
                    .clickEvent(ClickEvent.runCommand("/warnlist " + (currentPage + 1)))
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

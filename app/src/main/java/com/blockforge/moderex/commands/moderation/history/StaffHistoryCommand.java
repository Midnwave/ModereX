package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * /staffhistory <staff> [type] [page] [--gui]
 * Alias: /staffhist
 *
 * Shows punishments executed by a staff member.
 * Features pagination and formatting matching /modlog.
 * Optional type filter: bans, mutes, warnings, kicks, ipbans, ipmutes, all
 */
public class StaffHistoryCommand extends BaseCommand {

    private static final int ENTRIES_PER_PAGE = 10;

    public StaffHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.staffhistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /staffhistory <staff> [type] [page] [--gui]");
            sendMessage(sender, "<gray>Types: all, bans, mutes, warnings, kicks, ipbans, ipmutes");
            sendMessage(sender, "<gray>Flags: <white>--gui <gray>- Show in GUI instead of chat");
            return;
        }

        // Parse arguments
        String targetName = null;
        String type = "all";
        int page = 1;
        boolean useGui = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("--gui") || arg.equalsIgnoreCase("-g")) {
                useGui = true;
            } else if (targetName == null) {
                targetName = arg;
            } else if (isValidType(arg.toLowerCase())) {
                type = arg.toLowerCase();
            } else {
                try {
                    page = Integer.parseInt(arg);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {
                    // Invalid page, ignore
                }
            }
        }

        if (targetName == null) {
            sendMessage(sender, "<red>Please specify a staff member.");
            return;
        }

        TargetResolver target = new TargetResolver(targetName);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        // GUI mode requires player - redirect to modlog with -staff flag
        if (useGui && sender instanceof Player) {
            // Just use modlog with -staff flag for now (it has GUI support in ModLogGui)
            ((Player) sender).performCommand("modlog " + target.getDisplayName() + " " + type + " -staff");
            return;
        }

        // Parse type filter
        List<PunishmentType> typeFilter = parseTypeFilter(type);

        final int finalPage = page;
        final String finalType = type;
        final String displayName = target.getDisplayName();

        plugin.getPunishmentManager().getStaffHistory(target.getUuid(), null).thenAccept(allHistory -> {
            // Filter by type if not "all"
            List<Punishment> history;
            if (typeFilter != null) {
                history = allHistory.stream()
                        .filter(p -> typeFilter.contains(p.getType()))
                        .toList();
            } else {
                history = allHistory;
            }

            int totalPages = (int) Math.ceil((double) history.size() / ENTRIES_PER_PAGE);
            int currentPage = Math.max(1, Math.min(finalPage, Math.max(1, totalPages)));
            int startIndex = (currentPage - 1) * ENTRIES_PER_PAGE;
            int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, history.size());

            final List<Punishment> pagedResults = history.isEmpty() ? List.of() :
                    history.subList(startIndex, endIndex);
            final int finalTotalPages = Math.max(1, totalPages);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                // Header matching modlog style
                sender.sendMessage(Component.text("━━━━━━━━━━━━━ ", NamedTextColor.DARK_GRAY)
                        .append(Component.text("Staff Actions by " + displayName, NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY)));

                if (!finalType.equals("all")) {
                    sender.sendMessage(TextUtil.parse("<gray>Filter: <white>" + finalType +
                            " <dark_gray>(<white>" + history.size() + " total<dark_gray>)"));
                }

                if (pagedResults.isEmpty()) {
                    sendMessage(sender, MessageKey.STAFFHISTORY_EMPTY, "staff", displayName);
                } else {
                    for (Punishment p : pagedResults) {
                        sender.sendMessage(formatEntry(p));
                    }
                }

                // Clickable navigation footer
                if (sender instanceof Player) {
                    sender.sendMessage(buildNavigationFooter(displayName, finalType, currentPage, finalTotalPages));
                } else {
                    sender.sendMessage(TextUtil.parse("<gray>Page " + currentPage + "/" + finalTotalPages +
                            " - Use /staffhistory " + displayName + " " + finalType + " <page>"));
                }
            });
        });
    }

    private Component formatEntry(Punishment p) {
        NamedTextColor typeColor = switch (p.getType()) {
            case BAN, IPBAN -> NamedTextColor.DARK_RED;
            case MUTE, IPMUTE -> NamedTextColor.GOLD;
            case KICK -> NamedTextColor.RED;
            case WARN -> NamedTextColor.YELLOW;
        };

        String duration = p.getType().hasDuration() ?
                DurationParser.format(p.getExpiresAt() == -1 ? -1 : p.getExpiresAt() - p.getCreatedAt()) :
                "N/A";

        String status = p.isActive() ? (p.isExpired() ? "Expired" : "Active") : "Removed";
        NamedTextColor statusColor = p.isActive() && !p.isExpired() ? NamedTextColor.GREEN :
                (p.isExpired() ? NamedTextColor.GRAY : NamedTextColor.RED);

        // Build the entry with case ID as clickable
        Component caseIdComponent = Component.text("[" + p.getCaseId() + "] ", NamedTextColor.AQUA)
                .clickEvent(ClickEvent.runCommand("/viewpunishment " + p.getCaseId()))
                .hoverEvent(HoverEvent.showText(Component.text("Click to view details", NamedTextColor.YELLOW)));

        String reasonText = p.getReason() != null ? p.getReason() : "No reason";
        if (reasonText.length() > 40) {
            reasonText = reasonText.substring(0, 37) + "...";
        }

        return caseIdComponent
                .append(Component.text(TimeUtil.formatDate(p.getCreatedAt()) + " ", NamedTextColor.DARK_GRAY))
                .append(Component.text(p.getType().getDisplayName(), typeColor))
                .append(Component.text(" → " + p.getPlayerName(), NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(reasonText, NamedTextColor.WHITE))
                .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                .append(Component.text(status, statusColor))
                .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    private Component buildNavigationFooter(String staffName, String filter, int currentPage, int totalPages) {
        Component footer = Component.text("« ", NamedTextColor.DARK_GRAY);

        String baseCmd = "/staffhistory " + staffName + (filter.equals("all") ? "" : " " + filter);

        // Previous page button
        if (currentPage > 1) {
            footer = footer.append(
                    Component.text("[◀ Prev]", NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand(baseCmd + " " + (currentPage - 1)))
                            .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage - 1), NamedTextColor.YELLOW)))
            );
        } else {
            footer = footer.append(Component.text("[◀ Prev]", NamedTextColor.DARK_GRAY));
        }

        // Page indicator
        footer = footer.append(Component.text(" — ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Page ", NamedTextColor.GRAY))
                .append(Component.text(currentPage, NamedTextColor.GOLD))
                .append(Component.text("/", NamedTextColor.GRAY))
                .append(Component.text(totalPages, NamedTextColor.GOLD))
                .append(Component.text(" — ", NamedTextColor.DARK_GRAY));

        // Next page button
        if (currentPage < totalPages) {
            footer = footer.append(
                    Component.text("[Next ▶]", NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand(baseCmd + " " + (currentPage + 1)))
                            .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage + 1), NamedTextColor.YELLOW)))
            );
        } else {
            footer = footer.append(Component.text("[Next ▶]", NamedTextColor.DARK_GRAY));
        }

        // GUI button
        footer = footer.append(Component.text(" ", NamedTextColor.DARK_GRAY))
                .append(Component.text("[GUI]", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/staffhistory " + staffName + " " + filter + " --gui"))
                        .hoverEvent(HoverEvent.showText(Component.text("View in GUI", NamedTextColor.GRAY))));

        return footer.append(Component.text(" »", NamedTextColor.DARK_GRAY));
    }

    private List<PunishmentType> parseTypeFilter(String type) {
        if (type.equals("all")) {
            return null; // No filter
        }
        return switch (type) {
            case "bans" -> List.of(PunishmentType.BAN);
            case "mutes" -> List.of(PunishmentType.MUTE);
            case "warnings", "warns" -> List.of(PunishmentType.WARN);
            case "kicks" -> List.of(PunishmentType.KICK);
            case "ipbans" -> List.of(PunishmentType.IPBAN);
            case "ipmutes" -> List.of(PunishmentType.IPMUTE);
            case "ip" -> List.of(PunishmentType.IPBAN, PunishmentType.IPMUTE);
            default -> null;
        };
    }

    private boolean isValidType(String type) {
        return Arrays.asList("all", "bans", "mutes", "warnings", "warns", "kicks", "ipbans", "ipmutes", "ip").contains(type);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            List<String> completions = new ArrayList<>(Arrays.asList("all", "bans", "mutes", "warnings", "kicks", "ipbans", "ipmutes", "--gui"));
            return filterCompletions(completions, args[1]);
        }
        if (args.length == 3) {
            List<String> completions = new ArrayList<>();
            completions.add("1");
            completions.add("2");
            completions.add("3");
            completions.add("--gui");
            return filterCompletions(completions, args[2]);
        }
        if (args.length == 4) {
            return filterCompletions(List.of("--gui"), args[3]);
        }
        return super.tabComplete(sender, args);
    }
}

package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ModLogCommand extends BaseCommand {

    private static final int ENTRIES_PER_PAGE = 10;

    public ModLogCommand(ModereX plugin) {
        super(plugin, "moderex.command.modlog", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /modlog <player> [filter] [page]");
            sendMessage(sender, "<gray>Use <yellow>-staff</yellow> flag to view actions by a staff member");
            return;
        }

        // Check for -staff flag (shows actions BY a staff member, not TO a player)
        boolean staffMode = false;
        String targetName = args[0];
        String filter = "all";
        int page = 1;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-staff") || args[i].equalsIgnoreCase("--staff")) {
                staffMode = true;
            } else if (i == 0) {
                targetName = args[i];
            } else if (isFilter(args[i])) {
                filter = args[i].toLowerCase();
            } else {
                try {
                    page = Integer.parseInt(args[i]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        final String finalFilter = filter;
        final int finalPage = Math.max(1, page);
        final boolean finalStaffMode = staffMode;
        final String finalTargetName = targetName;

        if (staffMode) {
            // Staff mode: show actions BY this staff member
            plugin.getPunishmentManager().getPunishmentsByStaff(targetUuid).thenAccept(punishments -> {
                handlePunishmentResults(sender, punishments, displayName, finalFilter, finalPage, finalStaffMode, finalTargetName);
            });
        } else {
            // Normal mode: show actions TO this player
            plugin.getPunishmentManager().getPunishments(targetUuid).thenAccept(punishments -> {
                handlePunishmentResults(sender, punishments, displayName, finalFilter, finalPage, finalStaffMode, finalTargetName);
            });
        }
    }

    private void handlePunishmentResults(CommandSender sender, List<Punishment> punishments, String displayName,
                                          String filter, int page, boolean staffMode, String targetName) {
        List<Punishment> filtered = punishments;
        if (!filter.equals("all")) {
            PunishmentType filterType = PunishmentType.fromString(filter);
            if (filterType != null) {
                filtered = punishments.stream()
                        .filter(p -> p.getType() == filterType)
                        .toList();
            }
        }

        int totalPages = (int) Math.ceil((double) filtered.size() / ENTRIES_PER_PAGE);
        int startIndex = (page - 1) * ENTRIES_PER_PAGE;
        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, filtered.size());

        final List<Punishment> pagedResults = filtered.subList(
                Math.min(startIndex, filtered.size()),
                Math.min(endIndex, filtered.size())
        );
        final int finalTotalPages = Math.max(1, totalPages);
        final int finalPage = page;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            // Header
            String headerText = staffMode ? "Staff Actions by " + displayName : "Moderation Log for " + displayName;
            sender.sendMessage(Component.text("━━━━━━━━━━━━━ ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(headerText, NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" ━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY)));

            if (pagedResults.isEmpty()) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.MODLOG_EMPTY,
                        "player", displayName));
            } else {
                for (Punishment p : pagedResults) {
                    sender.sendMessage(formatEntry(p, staffMode));
                }
            }

            // Clickable navigation footer
            String staffFlag = staffMode ? " -staff" : "";
            sender.sendMessage(buildNavigationFooter(targetName, filter, staffFlag, finalPage, finalTotalPages));
        });
    }

    private boolean isFilter(String arg) {
        return arg.equalsIgnoreCase("all") ||
                arg.equalsIgnoreCase("bans") ||
                arg.equalsIgnoreCase("mutes") ||
                arg.equalsIgnoreCase("kicks") ||
                arg.equalsIgnoreCase("warns") ||
                PunishmentType.fromString(arg) != null;
    }

    private Component formatEntry(Punishment p, boolean staffMode) {
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

        // Show different info based on mode
        String targetInfo;
        if (staffMode) {
            // In staff mode, show the player who was punished
            targetInfo = " → " + p.getPlayerName();
        } else {
            // In normal mode, show the staff who issued the punishment
            targetInfo = " by " + p.getStaffName();
        }

        String reasonText = p.getReason();
        if (reasonText.length() > 40) {
            reasonText = reasonText.substring(0, 37) + "...";
        }

        return caseIdComponent
                .append(Component.text(TimeUtil.formatDate(p.getCreatedAt()) + " ", NamedTextColor.DARK_GRAY))
                .append(Component.text(p.getType().getDisplayName(), typeColor))
                .append(Component.text(targetInfo, NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(reasonText, NamedTextColor.WHITE))
                .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                .append(Component.text(status, statusColor))
                .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    /**
     * Builds a clickable navigation footer with page navigation buttons.
     */
    private Component buildNavigationFooter(String playerName, String filter, String staffFlag, int currentPage, int totalPages) {
        Component footer = Component.text("« ", NamedTextColor.DARK_GRAY);

        String baseCmd = "/modlog " + playerName + (filter.equals("all") ? "" : " " + filter) + staffFlag;

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

        return footer.append(Component.text(" »", NamedTextColor.DARK_GRAY));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("all", "bans", "mutes", "kicks", "warns", "-staff"), args[1]);
        }
        if (args.length >= 3) {
            return filterCompletions(Arrays.asList("all", "bans", "mutes", "kicks", "warns", "-staff"), args[args.length - 1]);
        }
        return super.tabComplete(sender, args);
    }
}

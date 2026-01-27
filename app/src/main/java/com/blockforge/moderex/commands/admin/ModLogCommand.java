package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.ModLogGui;
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
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
            sendMessage(sender, "<red>Usage: /modlog <player> [filter] [page] [--gui]");
            sendMessage(sender, "<gray>Use <yellow>-staff</yellow> flag to view actions by a staff member");
            sendMessage(sender, "<gray>Filters: all, bans, mutes, kicks, warns, ipbans, ipmutes");
            sendMessage(sender, "<gray>Flags: <white>--gui <gray>- Show in GUI instead of chat");
            return;
        }

        // Check for flags and parse arguments
        boolean staffMode = false;
        boolean useGui = false;
        String targetName = null;
        String filter = "all";
        int page = 1;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-staff") || arg.equalsIgnoreCase("--staff")) {
                staffMode = true;
            } else if (arg.equalsIgnoreCase("--gui") || arg.equalsIgnoreCase("-g")) {
                useGui = true;
            } else if (targetName == null) {
                targetName = arg;
            } else if (isFilter(arg)) {
                filter = arg.toLowerCase();
            } else {
                try {
                    page = Integer.parseInt(arg);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (targetName == null) {
            sendMessage(sender, "<red>Please specify a player.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        // GUI mode requires player
        if (useGui && !(sender instanceof Player)) {
            sendMessage(sender, "<red>GUI mode requires a player.");
            useGui = false;
        }

        // If GUI mode, open the GUI
        if (useGui && sender instanceof Player player) {
            ModLogGui gui = new ModLogGui(plugin, targetUuid, displayName);
            gui.build();
            plugin.getGuiManager().open(player, gui);
            return;
        }

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
            List<PunishmentType> filterTypes = parseFilter(filter);
            if (filterTypes != null && !filterTypes.isEmpty()) {
                filtered = punishments.stream()
                        .filter(p -> filterTypes.contains(p.getType()))
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
                arg.equalsIgnoreCase("ipbans") ||
                arg.equalsIgnoreCase("ipmutes") ||
                arg.equalsIgnoreCase("ip") ||
                PunishmentType.fromString(arg) != null;
    }

    private List<PunishmentType> parseFilter(String filter) {
        return switch (filter.toLowerCase()) {
            case "bans" -> List.of(PunishmentType.BAN);
            case "mutes" -> List.of(PunishmentType.MUTE);
            case "kicks" -> List.of(PunishmentType.KICK);
            case "warns" -> List.of(PunishmentType.WARN);
            case "ipbans" -> List.of(PunishmentType.IPBAN);
            case "ipmutes" -> List.of(PunishmentType.IPMUTE);
            case "ip" -> List.of(PunishmentType.IPBAN, PunishmentType.IPMUTE);
            default -> {
                PunishmentType type = PunishmentType.fromString(filter);
                yield type != null ? List.of(type) : null;
            }
        };
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

        // GUI button
        footer = footer.append(Component.text(" ", NamedTextColor.DARK_GRAY))
                .append(Component.text("[GUI]", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/modlog " + playerName + " " + filter + staffFlag + " --gui"))
                        .hoverEvent(HoverEvent.showText(Component.text("View in GUI", NamedTextColor.GRAY))));

        return footer.append(Component.text(" »", NamedTextColor.DARK_GRAY));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        List<String> completions = new ArrayList<>(Arrays.asList(
                "all", "bans", "mutes", "kicks", "warns", "ipbans", "ipmutes", "-staff", "--gui"));
        if (args.length == 2) {
            return filterCompletions(completions, args[1]);
        }
        if (args.length >= 3) {
            // Add page numbers to suggestions
            List<String> withPages = new ArrayList<>(completions);
            withPages.add("1");
            withPages.add("2");
            withPages.add("3");
            return filterCompletions(withPages, args[args.length - 1]);
        }
        return super.tabComplete(sender, args);
    }
}

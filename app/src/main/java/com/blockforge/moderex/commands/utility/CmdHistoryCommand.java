package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TextUtil;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdHistoryCommand extends BaseCommand {

    private static final int ENTRIES_PER_PAGE = 15;

    public CmdHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.command.cmdhistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /cmdhistory <player> [page]");
            sendMessage(sender, "<gray>Shows recent commands executed by a player.");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {
            }
        }

        final int finalPage = Math.max(1, page);
        final int offset = (finalPage - 1) * ENTRIES_PER_PAGE;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int totalCount = plugin.getDatabaseManager().query("""
                        SELECT COUNT(*) as count FROM moderex_command_history WHERE player_uuid = ?
                        """,
                        rs -> rs.next() ? rs.getInt("count") : 0,
                        targetUuid.toString()
                );

                int totalPages = (int) Math.ceil((double) totalCount / ENTRIES_PER_PAGE);

                List<CommandEntry> entries = plugin.getDatabaseManager().query("""
                        SELECT command, executed_at FROM moderex_command_history
                        WHERE player_uuid = ?
                        ORDER BY executed_at DESC
                        LIMIT ? OFFSET ?
                        """,
                        rs -> {
                            List<CommandEntry> list = new ArrayList<>();
                            while (rs.next()) {
                                list.add(new CommandEntry(
                                        rs.getString("command"),
                                        rs.getLong("executed_at")
                                ));
                            }
                            return list;
                        },
                        targetUuid.toString(),
                        ENTRIES_PER_PAGE,
                        offset
                );

                final int finalTotalPages = Math.max(1, totalPages);
                final int finalTotalCount = totalCount;

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // Header with box-drawing characters
                    sender.sendMessage(TextUtil.parse(""));
                    sender.sendMessage(TextUtil.parse("<dark_gray>┌──────────────────┐"));
                    sender.sendMessage(TextUtil.parse("<dark_gray>│ <white>Command History: <yellow>" + displayName +
                            " <dark_gray>(<white>" + finalTotalCount + " total<dark_gray>)"));
                    sender.sendMessage(TextUtil.parse("<dark_gray>├──────────────────┤"));

                    if (entries.isEmpty()) {
                        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>No commands found for this player."));
                    } else {
                        for (CommandEntry entry : entries) {
                            String cmd = entry.command;
                            // Truncate long commands
                            if (cmd.length() > 40) {
                                cmd = cmd.substring(0, 37) + "...";
                            }

                            if (sender instanceof Player) {
                                Component line = TextUtil.parse("<dark_gray>│ <gray>" + TimeUtil.formatTime(entry.executedAt) + " ")
                                        .append(TextUtil.parse("<white>" + cmd)
                                                .clickEvent(ClickEvent.suggestCommand(entry.command))
                                                .hoverEvent(HoverEvent.showText(TextUtil.parse(
                                                        "<white>" + entry.command + "\n" +
                                                        "<gray>Date: <white>" + TimeUtil.formatDateTime(entry.executedAt) + "\n" +
                                                        "\n<yellow>Click to copy command"))));
                                sender.sendMessage(line);
                            } else {
                                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>" +
                                        TimeUtil.formatTime(entry.executedAt) + " <white>" + cmd));
                            }
                        }
                    }

                    sender.sendMessage(TextUtil.parse("<dark_gray>└──────────────────┘"));

                    // Build clickable navigation footer
                    if (sender instanceof Player && finalTotalPages > 1) {
                        sender.sendMessage(buildNavigationFooter(displayName, finalPage, finalTotalPages));
                    } else if (finalTotalPages > 1) {
                        sendMessage(sender, "<gray>Page " + finalPage + "/" + finalTotalPages +
                                " - Use /cmdhistory " + displayName + " <page>");
                    }
                    sender.sendMessage(TextUtil.parse(""));
                });
            } catch (SQLException e) {
                plugin.logError("Failed to fetch command history", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to fetch command history."));
            }
        });
    }

    /**
     * Builds a clickable navigation footer with page navigation buttons.
     * Similar to WorldGuard's /rg flags command navigation.
     */
    private Component buildNavigationFooter(String playerName, int currentPage, int totalPages) {
        Component footer = Component.text("  « ", NamedTextColor.DARK_GRAY);

        // Previous page button
        if (currentPage > 1) {
            footer = footer.append(
                    Component.text("[◀ Prev]", NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/cmdhistory " + playerName + " " + (currentPage - 1)))
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
                            .clickEvent(ClickEvent.runCommand("/cmdhistory " + playerName + " " + (currentPage + 1)))
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
            // Suggest page numbers
            return filterCompletions(List.of("1", "2", "3", "4", "5"), args[1]);
        }
        return super.tabComplete(sender, args);
    }

    private record CommandEntry(String command, long executedAt) {
    }
}

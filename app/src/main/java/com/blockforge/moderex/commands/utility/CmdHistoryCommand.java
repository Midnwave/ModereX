package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

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
            } catch (NumberFormatException ignored) {
            }
        }

        final int finalPage = Math.max(1, page);
        final int offset = (finalPage - 1) * ENTRIES_PER_PAGE;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Get total count
                int totalCount = plugin.getDatabaseManager().query("""
                        SELECT COUNT(*) as count FROM moderex_command_history WHERE player_uuid = ?
                        """,
                        rs -> rs.next() ? rs.getInt("count") : 0,
                        targetUuid.toString()
                );

                int totalPages = (int) Math.ceil((double) totalCount / ENTRIES_PER_PAGE);

                // Get paginated results
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

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // Header
                    sender.sendMessage(plugin.getLanguageManager().get(MessageKey.CMD_HISTORY_HEADER,
                            "player", displayName));

                    if (entries.isEmpty()) {
                        sender.sendMessage(plugin.getLanguageManager().get(MessageKey.CMD_HISTORY_EMPTY,
                                "player", displayName));
                    } else {
                        for (CommandEntry entry : entries) {
                            sender.sendMessage(Component.text("[" + TimeUtil.formatDateTime(entry.executedAt) + "] ", NamedTextColor.GRAY)
                                    .append(Component.text(entry.command, NamedTextColor.WHITE)));
                        }
                    }

                    // Footer
                    sender.sendMessage(plugin.getLanguageManager().get(MessageKey.CMD_HISTORY_FOOTER,
                            "page", String.valueOf(finalPage),
                            "total", String.valueOf(finalTotalPages)));
                });
            } catch (SQLException e) {
                plugin.logError("Failed to fetch command history", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to fetch command history."));
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }

    private record CommandEntry(String command, long executedAt) {
    }
}

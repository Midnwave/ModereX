package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.NickHistoryGui;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command: /nickhistory <player> [page]
 * View a player's nickname change history.
 */
public class NickHistoryCommand extends BaseCommand {

    public NickHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.log.nicknames", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageKey.GENERAL_INVALID_ARGUMENTS, "usage", "/nickhistory <player> [page]");
            return;
        }

        String playerName = args[0];
        int page = 1;

        // Check for GUI/chat flags
        boolean useGui = false;
        boolean useChat = false;
        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-gui") || args[i].equalsIgnoreCase("--gui")) {
                useGui = true;
            } else if (args[i].equalsIgnoreCase("--chat")) {
                useChat = true;
            } else {
                try {
                    page = Integer.parseInt(args[i]);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {}
            }
        }

        // Resolve player
        OfflinePlayer target = resolvePlayer(playerName);
        if (target == null) {
            sendMessage(sender, MessageKey.GENERAL_PLAYER_NOT_FOUND, "player", playerName);
            return;
        }

        // Auto-detect Bedrock/Geyser players: default to GUI unless --chat is specified
        if (!useGui && !useChat && sender instanceof Player player) {
            if (plugin.getHookManager() != null && plugin.getHookManager().getGeyserHook() != null
                    && plugin.getHookManager().getGeyserHook().isBedrockPlayer(player)) {
                useGui = true;
            }
        }

        // Open GUI if requested and sender is a player
        if (useGui && sender instanceof Player player) {
            plugin.getGuiManager().open(player, new NickHistoryGui(plugin, target));
            return;
        }

        int perPage = plugin.getConfigManager().getSettings().getActivityLogEntriesPerPage();

        // Get count
        int totalCount = plugin.getActivityLogManager().getEntryCount(
                target.getUniqueId(), List.of(ActivityType.NICKNAME_CHANGE), 0, System.currentTimeMillis());

        if (totalCount == 0) {
            Msg.send(sender, TextUtil.parse("<gray>No nickname changes found for <white>" + target.getName()));
            return;
        }

        int totalPages = (int) Math.ceil((double) totalCount / perPage);
        if (page > totalPages) page = totalPages;

        // Get entries
        List<ActivityLogEntry> entries = plugin.getActivityLogManager().getEntries(
                target.getUniqueId(), List.of(ActivityType.NICKNAME_CHANGE), 0, System.currentTimeMillis(), page, perPage);

        // Header
        Msg.send(sender, TextUtil.parse("<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        Msg.send(sender, TextUtil.parse("<gold>Nickname History: <white>" + target.getName() + " <gray>(" + totalCount + " changes)"));
        Msg.send(sender, TextUtil.parse("<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        // Get timezone
        ZoneId timezone = getTimezone();

        // Entries
        for (ActivityLogEntry entry : entries) {
            String relativeTime = formatRelativeTime(entry.getTimestamp());
            String fullDate = entry.getFormattedFullDate(timezone);
            String newNick = entry.getContent() != null ? entry.getContent() : "(cleared)";
            String oldNick = entry.getExtra() != null ? entry.getExtra() : "(none)";

            // Try to parse the new nick with MiniMessage for display
            Component nickComponent;
            try {
                nickComponent = TextUtil.parse(newNick);
            } catch (Exception e) {
                nickComponent = Component.text(newNick);
            }

            Component component = TextUtil.parse("<gray>" + relativeTime + " <dark_gray>| ")
                    .append(nickComponent)
                    .append(TextUtil.parse(" <dark_gray>← <gray>" + oldNick));

            component = component.hoverEvent(HoverEvent.showText(
                    TextUtil.parse("<gray>Full timestamp:\n<white>" + fullDate +
                            "\n\n<gray>New nickname: <gold>" + newNick +
                            "\n<gray>Old nickname: <gold>" + oldNick)));
            Msg.send(sender, component);
        }

        // Pagination
        Component paginator = buildPaginator(target.getName(), page, totalPages);
        Msg.send(sender, paginator);

        // GUI hint
        if (sender instanceof Player) {
            Msg.send(sender, TextUtil.parse("<gray>Use <yellow>/nickhistory " + target.getName() + " -gui<gray> to view in GUI"));
        }
    }

    private Component buildPaginator(String playerName, int currentPage, int totalPages) {
        Component paginator = Component.empty();

        // Left arrow
        if (currentPage > 1) {
            paginator = paginator.append(TextUtil.parse("<yellow><click:run_command:'/nickhistory " + playerName + " " + (currentPage - 1) + "'><hover:show_text:'<gray>Previous page'>←</hover></click></yellow> "));
        } else {
            paginator = paginator.append(TextUtil.parse("<dark_gray>← </dark_gray>"));
        }

        // Page info
        paginator = paginator.append(TextUtil.parse("<gold>Page <yellow>" + currentPage + "</yellow>/<yellow>" + totalPages + "</yellow></gold> "));

        // Right arrow
        if (currentPage < totalPages) {
            paginator = paginator.append(TextUtil.parse("<yellow><click:run_command:'/nickhistory " + playerName + " " + (currentPage + 1) + "'><hover:show_text:'<gray>Next page'>→</hover></click></yellow>"));
        } else {
            paginator = paginator.append(TextUtil.parse("<dark_gray>→</dark_gray>"));
        }

        return paginator;
    }

    private OfflinePlayer resolvePlayer(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        if (offline.hasPlayedBefore() || offline.isOnline()) {
            return offline;
        }
        return null;
    }

    private ZoneId getTimezone() {
        String tzStr = plugin.getConfigManager().getSettings().getTimezone();
        try {
            return ZoneId.of(tzStr);
        } catch (Exception e) {
            return ZoneId.systemDefault();
        }
    }

    private String formatRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        double seconds = diff / 1000.0;
        if (seconds < 60) return String.format("%.0fs ago", seconds);
        double minutes = seconds / 60.0;
        if (minutes < 60) return String.format("%.1fm ago", minutes);
        double hours = minutes / 60.0;
        if (hours < 24) return String.format("%.1fh ago", hours);
        double days = hours / 24.0;
        return String.format("%.1fd ago", days);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(), args[0]);
        }
        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("-gui");
            suggestions.add("--chat");
            suggestions.addAll(List.of("1", "2", "3"));
            return filterCompletions(suggestions, args[1]);
        }
        return Collections.emptyList();
    }
}

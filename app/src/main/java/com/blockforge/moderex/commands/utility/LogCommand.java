package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

/**
 * Command: /log <player> [action:type] [time:duration] [page]
 * View player activity logs with filtering and pagination.
 */
public class LogCommand extends BaseCommand {

    // Pattern for filters: action:chat, a:chat, time:12h, t:12h
    private static final Pattern FILTER_PATTERN = Pattern.compile("(action|a|time|t|type):([^\\s]+)", Pattern.CASE_INSENSITIVE);

    // Internal pattern for snapshot (used in pagination URLs)
    private static final Pattern SNAP_PATTERN = Pattern.compile("_snap:(\\d+)", Pattern.CASE_INSENSITIVE);

    // Cache snapshot times per viewer+query for pagination consistency
    // Key: viewerUUID:targetPlayer:filters, Value: snapshotTime
    private static final Map<String, Long> snapshotCache = new ConcurrentHashMap<>();

    public LogCommand(ModereX plugin) {
        super(plugin, "moderex.log", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageKey.GENERAL_INVALID_ARGUMENTS, "usage", "/log <player> [action:type] [time:duration] [page]");
            return;
        }

        // Parse arguments
        String playerName = args[0];
        List<ActivityType> actionFilter = null;
        long timeFilter = 0;
        long snapshotTime = 0; // Locks results to entries before this timestamp
        int page = 1;
        boolean hasPageArg = false;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            // Check for internal snapshot (hidden from user, used in pagination)
            Matcher snapMatcher = SNAP_PATTERN.matcher(arg);
            if (snapMatcher.matches()) {
                try {
                    snapshotTime = Long.parseLong(snapMatcher.group(1));
                } catch (NumberFormatException ignored) {}
                continue;
            }

            // Check for filters
            Matcher matcher = FILTER_PATTERN.matcher(arg);
            if (matcher.matches()) {
                String filterType = matcher.group(1).toLowerCase();
                String filterValue = matcher.group(2);

                switch (filterType) {
                    case "action", "a", "type" -> {
                        actionFilter = plugin.getActivityLogManager().parseActionFilter(filterValue);
                    }
                    case "time", "t" -> {
                        timeFilter = plugin.getActivityLogManager().parseTimeFilter(filterValue);
                    }
                }
            } else {
                // Try to parse as page number
                try {
                    page = Integer.parseInt(arg);
                    if (page < 1) page = 1;
                    hasPageArg = true;
                } catch (NumberFormatException ignored) {
                    // Not a page number
                }
            }
        }

        // Build cache key for this query
        String viewerId = sender instanceof Player p ? p.getUniqueId().toString() : "console";
        String cacheKey = viewerId + ":" + playerName.toLowerCase() + ":" +
                (actionFilter != null ? actionFilter.toString() : "") + ":" + timeFilter;

        // If no snapshot from URL and this is page 1 (fresh query), create new snapshot
        // If paginating (page > 1 or hasPageArg), try to use cached snapshot
        if (snapshotTime == 0) {
            if (hasPageArg && page > 1) {
                // Try to get cached snapshot
                snapshotTime = snapshotCache.getOrDefault(cacheKey, System.currentTimeMillis());
            } else {
                // Fresh query - new snapshot
                snapshotTime = System.currentTimeMillis();
                snapshotCache.put(cacheKey, snapshotTime);
            }
        }

        // Resolve player
        OfflinePlayer target = resolvePlayer(playerName);
        if (target == null) {
            sendMessage(sender, MessageKey.GENERAL_PLAYER_NOT_FOUND, "player", playerName);
            return;
        }

        // Get entries per page from config
        int perPage = plugin.getConfigManager().getSettings().getActivityLogEntriesPerPage();

        // Get total count for pagination (using snapshot time for consistency)
        int totalCount = plugin.getActivityLogManager().getEntryCount(
                target.getUniqueId(), actionFilter, timeFilter, snapshotTime);

        if (totalCount == 0) {
            sendMessage(sender, MessageKey.LOG_EMPTY, "player", target.getName());
            return;
        }

        int totalPages = (int) Math.ceil((double) totalCount / perPage);
        if (page > totalPages) page = totalPages;

        // Get entries (using snapshot time so pagination is consistent)
        List<ActivityLogEntry> entries = plugin.getActivityLogManager().getEntries(
                target.getUniqueId(), actionFilter, timeFilter, snapshotTime, page, perPage);

        // Build and send output
        ZoneId timezone = getTimezone(sender);
        sendLogOutput(sender, target.getName(), entries, page, totalPages, totalCount,
                actionFilter, timeFilter, snapshotTime, timezone);
    }

    /**
     * Send formatted log output to sender.
     */
    private void sendLogOutput(CommandSender sender, String playerName, List<ActivityLogEntry> entries,
                               int page, int totalPages, int totalCount,
                               List<ActivityType> actionFilter, long timeFilter, long snapshotTime,
                               ZoneId timezone) {

        // Header
        Component header = plugin.getLanguageManager().get(MessageKey.LOG_HEADER,
                "player", playerName, "total", String.valueOf(totalCount));
        sender.sendMessage(header);

        // Filter info if applicable
        if (actionFilter != null || timeFilter > 0) {
            StringBuilder filterInfo = new StringBuilder();
            if (actionFilter != null) {
                filterInfo.append("Actions: ");
                for (int i = 0; i < actionFilter.size(); i++) {
                    if (i > 0) filterInfo.append(", ");
                    filterInfo.append(actionFilter.get(i).getKey());
                }
            }
            if (timeFilter > 0) {
                if (!filterInfo.isEmpty()) filterInfo.append(" | ");
                filterInfo.append("Since: ").append(formatTimeSince(timeFilter));
            }
            sender.sendMessage(TextUtil.parse("<gray>Filters: <white>" + filterInfo));
        }

        // Entries
        for (ActivityLogEntry entry : entries) {
            Component entryComponent = buildEntryComponent(entry, sender, timezone);
            sender.sendMessage(entryComponent);
        }

        // Paginator
        Component paginator = buildPaginator(playerName, page, totalPages, actionFilter, timeFilter, snapshotTime);
        sender.sendMessage(paginator);
    }

    /**
     * Build a component for a single log entry with hover and click events.
     */
    private Component buildEntryComponent(ActivityLogEntry entry, CommandSender sender, ZoneId timezone) {
        String relativeTime = formatRelativeTime(entry.getTimestamp());
        String fullDate = entry.getFormattedFullDate(timezone);
        String typeColor = entry.getTypeColor();
        String content = entry.getContent() != null ? entry.getContent() : "";

        // Truncate long content
        if (content.length() > 50) {
            content = content.substring(0, 47) + "...";
        }

        // Build entry text: "4.55/m ago - <color>(username): <content>"
        String entryFormat = plugin.getLanguageManager().getRaw(MessageKey.LOG_ENTRY);
        String entryText = entryFormat
                .replace("<time>", relativeTime)
                .replace("<type_color>", typeColor)
                .replace("<player>", entry.getPlayerName())
                .replace("<content>", content);

        Component component = TextUtil.parse(entryText);

        // Add hover with full date
        component = component.hoverEvent(HoverEvent.showText(
                TextUtil.parse("<gray>Full timestamp:\n<white>" + fullDate +
                        (entry.getExtra() != null ? "\n\n<gray>Extra: <white>" + entry.getExtra() : ""))));

        // For signs, add click to teleport if sender has permission
        if (entry.getType() == ActivityType.SIGN && entry.getExtra() != null &&
            sender instanceof Player && sender.hasPermission("moderex.log.teleport")) {

            String[] parts = entry.getExtra().split(":");
            if (parts.length >= 2) {
                String world = parts[0];
                String[] coords = parts[1].split(",");
                if (coords.length >= 3) {
                    String tpCommand = "/tp " + coords[0] + " " + coords[1] + " " + coords[2];
                    component = component.clickEvent(ClickEvent.runCommand(tpCommand));
                    component = component.hoverEvent(HoverEvent.showText(
                            TextUtil.parse("<gray>Full timestamp:\n<white>" + fullDate +
                                    "\n\n<yellow>Click to teleport to sign location" +
                                    "\n<gray>Location: " + entry.getExtra())));
                }
            }
        }

        return component;
    }

    /**
     * Build the paginator component.
     */
    private Component buildPaginator(String playerName, int currentPage, int totalPages,
                                     List<ActivityType> actionFilter, long timeFilter, long snapshotTime) {
        // Build the base command for pagination
        StringBuilder baseCmd = new StringBuilder("/log " + playerName);
        if (actionFilter != null && !actionFilter.isEmpty()) {
            baseCmd.append(" a:");
            for (int i = 0; i < actionFilter.size(); i++) {
                if (i > 0) baseCmd.append(",");
                baseCmd.append(actionFilter.get(i).getKey());
            }
        }
        if (timeFilter > 0) {
            baseCmd.append(" t:").append(formatTimeSince(timeFilter));
        }
        // Include snapshot timestamp for pagination consistency (internal parameter)
        baseCmd.append(" _snap:").append(snapshotTime);

        Component paginator = Component.empty();

        // Left arrow
        if (currentPage > 1) {
            paginator = paginator.append(TextUtil.parse("<yellow><click:run_command:'" + baseCmd + " " + (currentPage - 1) + "'><hover:show_text:'<gray>Previous page'>←</hover></click></yellow> "));
        } else {
            paginator = paginator.append(TextUtil.parse("<dark_gray>← </dark_gray>"));
        }

        // Page info with main color
        paginator = paginator.append(TextUtil.parse("<gold>Page <yellow>" + currentPage + "</yellow>/<yellow>" + totalPages + "</yellow></gold> "));

        // Right arrow
        if (currentPage < totalPages) {
            paginator = paginator.append(TextUtil.parse("<yellow><click:run_command:'" + baseCmd + " " + (currentPage + 1) + "'><hover:show_text:'<gray>Next page'>→</hover></click></yellow> "));
        } else {
            paginator = paginator.append(TextUtil.parse("<dark_gray>→</dark_gray> "));
        }

        paginator = paginator.append(TextUtil.parse("<dark_gray>| </dark_gray>"));

        // Page numbers
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        // Show first page with ellipsis if needed
        if (startPage > 1) {
            paginator = paginator.append(createPageButton(1, currentPage, baseCmd.toString()));
            if (startPage > 2) {
                paginator = paginator.append(TextUtil.parse("<dark_gray> ... </dark_gray>"));
            } else {
                paginator = paginator.append(TextUtil.parse("<dark_gray> | </dark_gray>"));
            }
        }

        // Page number buttons
        for (int i = startPage; i <= endPage; i++) {
            if (i > startPage) {
                paginator = paginator.append(TextUtil.parse("<dark_gray> | </dark_gray>"));
            }
            paginator = paginator.append(createPageButton(i, currentPage, baseCmd.toString()));
        }

        // Show last page with ellipsis if needed
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                paginator = paginator.append(TextUtil.parse("<dark_gray> ... </dark_gray>"));
            } else {
                paginator = paginator.append(TextUtil.parse("<dark_gray> | </dark_gray>"));
            }
            paginator = paginator.append(createPageButton(totalPages, currentPage, baseCmd.toString()));
        }

        return paginator;
    }

    /**
     * Create a clickable page number button.
     */
    private Component createPageButton(int pageNum, int currentPage, String baseCmd) {
        if (pageNum == currentPage) {
            // Current page - underlined
            return TextUtil.parse("<yellow><underlined>" + pageNum + "</underlined></yellow>");
        } else {
            // Clickable page
            return TextUtil.parse("<gray><click:run_command:'" + baseCmd + " " + pageNum +
                    "'><hover:show_text:'<gray>Go to page " + pageNum + "'>" + pageNum + "</hover></click></gray>");
        }
    }

    /**
     * Resolve a player by name (online or offline).
     */
    private OfflinePlayer resolvePlayer(String name) {
        // Try online first
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        // Try offline
        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        if (offline.hasPlayedBefore() || offline.isOnline()) {
            return offline;
        }

        return null;
    }

    /**
     * Get the timezone for a sender.
     */
    private ZoneId getTimezone(CommandSender sender) {
        String tzStr = plugin.getConfigManager().getSettings().getTimezone();
        try {
            return ZoneId.of(tzStr);
        } catch (Exception e) {
            return ZoneId.systemDefault();
        }
    }

    /**
     * Format time since a timestamp as a human-readable string.
     */
    private String formatTimeSince(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long hours = diff / (1000 * 60 * 60);
        if (hours < 24) return hours + "h";

        long days = hours / 24;
        if (days < 7) return days + "d";

        long weeks = days / 7;
        if (weeks < 4) return weeks + "w";

        long months = days / 30;
        return months + "mo";
    }

    /**
     * Format a timestamp as relative time like "4.55/m ago", "2.30/h ago", "1.25/d ago".
     */
    private String formatRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;

        // Seconds
        double seconds = diff / 1000.0;
        if (seconds < 60) {
            return String.format("%.0f/s ago", seconds);
        }

        // Minutes
        double minutes = seconds / 60.0;
        if (minutes < 60) {
            return String.format("%.2f/m ago", minutes);
        }

        // Hours
        double hours = minutes / 60.0;
        if (hours < 24) {
            return String.format("%.2f/h ago", hours);
        }

        // Days
        double days = hours / 24.0;
        if (days < 7) {
            return String.format("%.2f/d ago", days);
        }

        // Weeks
        double weeks = days / 7.0;
        if (weeks < 4) {
            return String.format("%.1f/w ago", weeks);
        }

        // Months
        double months = days / 30.0;
        return String.format("%.1f/mo ago", months);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(), args[0]);
        }

        if (args.length >= 2) {
            String current = args[args.length - 1].toLowerCase();

            // Track what filters have already been used
            boolean hasActionFilter = false;
            boolean hasTimeFilter = false;
            Set<String> usedActions = new HashSet<>();

            for (int i = 1; i < args.length - 1; i++) {
                String arg = args[i].toLowerCase();
                if (arg.startsWith("a:") || arg.startsWith("action:") || arg.startsWith("type:")) {
                    hasActionFilter = true;
                    // Extract the action value(s) for exclusion
                    int colonIdx = arg.indexOf(':');
                    if (colonIdx >= 0 && colonIdx < arg.length() - 1) {
                        String actions = arg.substring(colonIdx + 1);
                        for (String action : actions.split(",")) {
                            usedActions.add(action.trim().toLowerCase());
                        }
                    }
                } else if (arg.startsWith("t:") || arg.startsWith("time:")) {
                    hasTimeFilter = true;
                }
            }

            List<String> suggestions = new ArrayList<>();

            // Handle action filter completion
            if (current.startsWith("a:") || current.startsWith("action:") || current.startsWith("type:")) {
                String prefix = current.substring(0, current.indexOf(':') + 1);
                for (ActivityType type : ActivityType.values()) {
                    String key = type.getKey();
                    if (!usedActions.contains(key.toLowerCase())) {
                        suggestions.add(prefix + key);
                    }
                }
            }
            // Handle time filter completion
            else if (current.startsWith("t:") || current.startsWith("time:")) {
                String prefix = current.substring(0, current.indexOf(':') + 1);
                suggestions.addAll(List.of(
                        prefix + "1s", prefix + "1m", prefix + "1h",
                        prefix + "1d", prefix + "1w", prefix + "1mo", prefix + "1y"
                ));
            }
            // Suggest filter prefixes and page numbers if not yet used
            else if (current.isEmpty() || !current.contains(":")) {
                if (!hasActionFilter) {
                    suggestions.add("a:");
                    suggestions.add("action:");
                }
                if (!hasTimeFilter) {
                    suggestions.add("t:");
                    suggestions.add("time:");
                }
                // Page numbers
                suggestions.addAll(List.of("1", "2", "3"));
            }

            return filterCompletions(suggestions, current);
        }

        return Collections.emptyList();
    }
}

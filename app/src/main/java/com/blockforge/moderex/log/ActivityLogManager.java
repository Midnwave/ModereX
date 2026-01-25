package com.blockforge.moderex.log;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.log.storage.ActivityLogStorage;
import com.blockforge.moderex.log.storage.DatabaseActivityLogStorage;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages activity logging for players.
 * Handles logging of chat, commands, signs, items, sessions, and username changes.
 */
public class ActivityLogManager {

    private final ModereX plugin;
    private ActivityLogStorage storage;
    private boolean enabled;

    // Async write queue for performance
    private final Queue<ActivityLogEntry> writeQueue = new ConcurrentLinkedQueue<>();
    private int flushTaskId = -1;

    // Cache for recent username lookups
    private final Map<UUID, String> usernameCache = new HashMap<>();

    // Time filter pattern: 1h, 2d, 3w, 1mo, etc.
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)\\s*(s|m|h|d|w|mo|y)", Pattern.CASE_INSENSITIVE);

    public ActivityLogManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the activity log system.
     */
    public boolean initialize() {
        if (!plugin.getConfigManager().getSettings().isActivityLogEnabled()) {
            plugin.logDebug("[ActivityLog] Activity logging is disabled in config");
            enabled = false;
            return true;
        }

        String storageType = plugin.getConfigManager().getSettings().getActivityLogStorageType();

        // Create appropriate storage backend
        storage = switch (storageType.toLowerCase()) {
            case "database", "db", "sqlite", "mysql" -> new DatabaseActivityLogStorage(plugin);
            // Future: Add H2, YML, JSON, Text implementations
            default -> new DatabaseActivityLogStorage(plugin);
        };

        if (!storage.initialize()) {
            plugin.logError("Failed to initialize activity log storage: " + storageType, null);
            return false;
        }

        enabled = true;

        // Start async flush task (every 5 seconds)
        flushTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::flushQueue, 100L, 100L).getTaskId();

        // Schedule daily purge of old entries
        schedulePurge();

        plugin.getLogger().info("Activity log system initialized (storage: " + storage.getStorageType() + ")");
        return true;
    }

    /**
     * Shutdown the activity log system.
     */
    public void shutdown() {
        if (flushTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(flushTaskId);
            flushTaskId = -1;
        }

        // Flush remaining entries
        flushQueue();

        if (storage != null) {
            storage.shutdown();
        }
    }

    /**
     * Log a player activity.
     */
    public void log(Player player, ActivityType type, String content) {
        log(player.getUniqueId(), player.getName(), type, content, null);
    }

    /**
     * Log a player activity with extra data.
     */
    public void log(Player player, ActivityType type, String content, String extra) {
        log(player.getUniqueId(), player.getName(), type, content, extra);
    }

    /**
     * Log a player activity by UUID and name.
     */
    public void log(UUID playerUuid, String playerName, ActivityType type, String content, String extra) {
        if (!enabled || storage == null) return;

        // Check if this type is enabled
        if (!isTypeEnabled(type)) return;

        ActivityLogEntry entry = new ActivityLogEntry(
                playerUuid,
                playerName,
                type,
                content,
                extra,
                System.currentTimeMillis(),
                plugin.getConfigManager().getSettings().getServerName()
        );

        writeQueue.offer(entry);

        // Update username cache
        usernameCache.put(playerUuid, playerName);
    }

    /**
     * Log a chat message.
     */
    public void logChat(Player player, String message) {
        log(player, ActivityType.CHAT, message);
    }

    /**
     * Log a command execution.
     */
    public void logCommand(Player player, String command) {
        // Filter sensitive commands
        String lowerCmd = command.toLowerCase();
        if (lowerCmd.startsWith("/login") || lowerCmd.startsWith("/register") ||
            lowerCmd.startsWith("/l ") || lowerCmd.startsWith("/reg ") ||
            lowerCmd.contains("password")) {
            log(player, ActivityType.COMMAND, "[REDACTED]");
        } else {
            log(player, ActivityType.COMMAND, command);
        }
    }

    /**
     * Log a sign edit.
     */
    public void logSign(Player player, String[] lines, String location) {
        String content = String.join(" | ", lines);
        log(player, ActivityType.SIGN, content, location);
    }

    /**
     * Log item drop.
     */
    public void logItemDrop(Player player, String itemInfo) {
        log(player, ActivityType.ITEM_DROP, itemInfo);
    }

    /**
     * Log item pickup.
     */
    public void logItemPickup(Player player, String itemInfo) {
        log(player, ActivityType.ITEM_PICKUP, itemInfo);
    }

    /**
     * Log item use.
     */
    public void logItemUse(Player player, String itemInfo) {
        log(player, ActivityType.ITEM_USE, itemInfo);
    }

    /**
     * Log item consume.
     */
    public void logItemConsume(Player player, String itemInfo) {
        log(player, ActivityType.ITEM_CONSUME, itemInfo);
    }

    /**
     * Log projectile launch.
     */
    public void logProjectile(Player player, String projectileInfo) {
        log(player, ActivityType.PROJECTILE, projectileInfo);
    }

    /**
     * Log anvil rename.
     */
    public void logAnvilRename(Player player, String originalName, String newName) {
        String content = newName;
        String extra = originalName;
        log(player, ActivityType.ANVIL_RENAME, content, extra);
    }

    /**
     * Log session join.
     */
    public void logSessionJoin(Player player, String ip) {
        log(player, ActivityType.SESSION_JOIN, "Joined from " + ip);
    }

    /**
     * Log session quit.
     */
    public void logSessionQuit(Player player, long sessionDuration) {
        String duration = formatDuration(sessionDuration);
        log(player, ActivityType.SESSION_QUIT, "Left (online for " + duration + ")");
    }

    /**
     * Log username change.
     */
    public void logUsernameChange(UUID playerUuid, String oldName, String newName) {
        log(playerUuid, newName, ActivityType.USERNAME_CHANGE, newName, oldName);
    }

    /**
     * Get activity log entries with filtering.
     */
    public List<ActivityLogEntry> getEntries(UUID playerUuid, List<ActivityType> types,
                                             long sinceTimestamp, int page, int perPage) {
        return getEntries(playerUuid, types, sinceTimestamp, 0, page, perPage);
    }

    /**
     * Get activity log entries with filtering including a snapshot timestamp.
     * @param beforeTimestamp Only get entries before this timestamp (for pagination consistency)
     */
    public List<ActivityLogEntry> getEntries(UUID playerUuid, List<ActivityType> types,
                                             long sinceTimestamp, long beforeTimestamp,
                                             int page, int perPage) {
        if (storage == null) return Collections.emptyList();

        int offset = (page - 1) * perPage;
        return storage.getEntries(playerUuid, types, sinceTimestamp, beforeTimestamp, perPage, offset);
    }

    /**
     * Get total entry count for pagination.
     */
    public int getEntryCount(UUID playerUuid, List<ActivityType> types, long sinceTimestamp) {
        return getEntryCount(playerUuid, types, sinceTimestamp, 0);
    }

    /**
     * Get total entry count with snapshot timestamp.
     */
    public int getEntryCount(UUID playerUuid, List<ActivityType> types, long sinceTimestamp, long beforeTimestamp) {
        if (storage == null) return 0;
        return storage.getEntryCount(playerUuid, types, sinceTimestamp, beforeTimestamp);
    }

    /**
     * Parse time filter string to timestamp.
     * @param timeStr Time string like "1h", "2d", "1w", "1mo"
     * @return Timestamp representing that time ago, or 0 if invalid
     */
    public long parseTimeFilter(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return 0;

        Matcher matcher = TIME_PATTERN.matcher(timeStr.trim());
        if (!matcher.matches()) return 0;

        int amount = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        long millis = switch (unit) {
            case "s" -> TimeUnit.SECONDS.toMillis(amount);
            case "m" -> TimeUnit.MINUTES.toMillis(amount);
            case "h" -> TimeUnit.HOURS.toMillis(amount);
            case "d" -> TimeUnit.DAYS.toMillis(amount);
            case "w" -> TimeUnit.DAYS.toMillis(amount * 7L);
            case "mo" -> TimeUnit.DAYS.toMillis(amount * 30L);
            case "y" -> TimeUnit.DAYS.toMillis(amount * 365L);
            default -> 0;
        };

        if (millis == 0) return 0;
        return System.currentTimeMillis() - millis;
    }

    /**
     * Parse action filter string to activity types.
     */
    public List<ActivityType> parseActionFilter(String actionStr) {
        if (actionStr == null || actionStr.isEmpty()) return null;

        List<ActivityType> types = new ArrayList<>();
        for (String part : actionStr.split(",")) {
            ActivityType type = ActivityType.fromKey(part.trim());
            if (type != null) {
                types.add(type);
            }
        }

        return types.isEmpty() ? null : types;
    }

    /**
     * Check if a specific activity type is enabled for logging.
     */
    private boolean isTypeEnabled(ActivityType type) {
        return switch (type) {
            case CHAT -> plugin.getConfigManager().getSettings().isActivityLogChat();
            case COMMAND -> plugin.getConfigManager().getSettings().isActivityLogCommands();
            case SIGN -> plugin.getConfigManager().getSettings().isActivityLogSigns();
            case ITEM_DROP, ITEM_PICKUP, ITEM_USE, ITEM_CONSUME, PROJECTILE ->
                    plugin.getConfigManager().getSettings().isActivityLogItems();
            case ANVIL_RENAME -> plugin.getConfigManager().getSettings().isActivityLogAnvils();
            case SESSION_JOIN, SESSION_QUIT -> plugin.getConfigManager().getSettings().isActivityLogSessions();
            case USERNAME_CHANGE -> plugin.getConfigManager().getSettings().isActivityLogUsernames();
        };
    }

    /**
     * Flush the write queue to storage.
     */
    private void flushQueue() {
        List<ActivityLogEntry> toWrite = new ArrayList<>();

        ActivityLogEntry entry;
        while ((entry = writeQueue.poll()) != null) {
            toWrite.add(entry);
        }

        if (!toWrite.isEmpty() && storage != null) {
            storage.saveBatch(toWrite);
        }
    }

    /**
     * Schedule daily purge of old entries.
     */
    private void schedulePurge() {
        long retentionDays = plugin.getConfigManager().getSettings().getActivityLogRetentionDays();
        if (retentionDays <= 0) return;

        // Run purge every 24 hours
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(retentionDays);
            int deleted = storage.purgeOldEntries(cutoff);
            if (deleted > 0) {
                plugin.logDebug("[ActivityLog] Purged " + deleted + " old entries (older than " + retentionDays + " days)");
            }
        }, 20L * 60 * 60, 20L * 60 * 60 * 24); // 1 hour delay, then every 24 hours
    }

    /**
     * Format duration in human-readable form.
     */
    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) return seconds + "s";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m " + (seconds % 60) + "s";

        long hours = minutes / 60;
        if (hours < 24) return hours + "h " + (minutes % 60) + "m";

        long days = hours / 24;
        return days + "d " + (hours % 24) + "h";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCachedUsername(UUID uuid) {
        return usernameCache.get(uuid);
    }
}

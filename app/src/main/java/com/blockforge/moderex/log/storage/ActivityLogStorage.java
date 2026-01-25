package com.blockforge.moderex.log.storage;

import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;

import java.util.List;
import java.util.UUID;

/**
 * Interface for activity log storage backends.
 * Implementations: Database (SQLite/MySQL), H2, YML, JSON, Text
 */
public interface ActivityLogStorage {

    /**
     * Initialize the storage backend.
     * @return true if initialization succeeded
     */
    boolean initialize();

    /**
     * Shutdown the storage backend gracefully.
     */
    void shutdown();

    /**
     * Save an activity log entry.
     * @param entry The entry to save
     * @return true if save succeeded
     */
    boolean save(ActivityLogEntry entry);

    /**
     * Save multiple activity log entries (batch insert).
     * @param entries The entries to save
     * @return true if save succeeded
     */
    boolean saveBatch(List<ActivityLogEntry> entries);

    /**
     * Get activity logs for a player with filtering and pagination.
     * @param playerUuid The player's UUID
     * @param types Activity types to filter by (null for all)
     * @param sinceTimestamp Only get entries after this timestamp (0 for all)
     * @param beforeTimestamp Only get entries before this timestamp (0 for all) - for pagination consistency
     * @param limit Maximum entries to return
     * @param offset Offset for pagination
     * @return List of matching entries
     */
    List<ActivityLogEntry> getEntries(UUID playerUuid, List<ActivityType> types,
                                      long sinceTimestamp, long beforeTimestamp,
                                      int limit, int offset);

    /**
     * Get total count of entries for pagination.
     * @param playerUuid The player's UUID
     * @param types Activity types to filter by (null for all)
     * @param sinceTimestamp Only count entries after this timestamp (0 for all)
     * @param beforeTimestamp Only count entries before this timestamp (0 for all)
     * @return Total count of matching entries
     */
    int getEntryCount(UUID playerUuid, List<ActivityType> types, long sinceTimestamp, long beforeTimestamp);

    /**
     * Delete old entries based on retention policy.
     * @param olderThan Delete entries older than this timestamp
     * @return Number of entries deleted
     */
    int purgeOldEntries(long olderThan);

    /**
     * Get the storage type name.
     */
    String getStorageType();
}

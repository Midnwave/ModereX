package com.blockforge.moderex.replay.block;

import com.blockforge.moderex.ModereX;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Manages block change logging for replay sessions.
 * Tracks block modifications during recording and provides data for playback visualization.
 */
public class BlockLogManager {

    private final ModereX plugin;

    // Per-session block change logs
    private final Map<String, List<BlockLogEntry>> sessionLogs = new ConcurrentHashMap<>();

    // Original block states before any session modified them (for rollback reference)
    private final Map<String, BlockLogEntry> originalStates = new ConcurrentHashMap<>();

    // Maximum entries per session (configurable)
    private int maxEntriesPerSession = 10000;

    public BlockLogManager(ModereX plugin) {
        this.plugin = plugin;
        this.maxEntriesPerSession = plugin.getConfigManager().getSettings().getReplayBlockLogMaxPerSession();
    }

    /**
     * Log a block change for a session.
     *
     * @param sessionId The replay session ID
     * @param entry The block log entry
     */
    public void logBlockChange(String sessionId, BlockLogEntry entry) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        List<BlockLogEntry> logs = sessionLogs.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Enforce max entries limit
        if (logs.size() >= maxEntriesPerSession) {
            plugin.logDebug("[BlockLog] Session " + sessionId + " reached max block log entries (" + maxEntriesPerSession + ")");
            return;
        }

        logs.add(entry);

        // Track original state (only first modification to a location)
        String locationKey = entry.getLocationKey();
        if (!originalStates.containsKey(locationKey)) {
            originalStates.put(locationKey, entry);
        }

        plugin.logDebug("[BlockLog] Logged " + entry.getAction() + " at " + locationKey + " for session " + sessionId);
    }

    /**
     * Get all block logs for a session.
     *
     * @param sessionId The session ID
     * @return List of block log entries, or empty list if none
     */
    public List<BlockLogEntry> getSessionLogs(String sessionId) {
        return sessionLogs.getOrDefault(sessionId, Collections.emptyList());
    }

    /**
     * Get the original state of a block before any session modified it.
     *
     * @param locationKey The location key (worldName:x:y:z)
     * @return The original block entry, or null if never modified
     */
    public BlockLogEntry getOriginalState(String locationKey) {
        return originalStates.get(locationKey);
    }

    /**
     * Save session block logs to disk.
     *
     * @param sessionId The session ID
     * @param sessionDir The session directory
     */
    public void saveSessionLogs(String sessionId, Path sessionDir) throws IOException {
        List<BlockLogEntry> logs = sessionLogs.get(sessionId);
        if (logs == null || logs.isEmpty()) {
            return;
        }

        Path blockLogFile = sessionDir.resolve("blocks.dat.gz");
        try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(blockLogFile));
             DataOutputStream dos = new DataOutputStream(gzip)) {

            dos.writeInt(logs.size());
            for (BlockLogEntry entry : logs) {
                dos.writeUTF(entry.serialize());
            }
        }

        plugin.logDebug("[BlockLog] Saved " + logs.size() + " block logs for session " + sessionId);
    }

    /**
     * Load session block logs from disk.
     *
     * @param sessionId The session ID
     * @param sessionDir The session directory
     * @return List of loaded block log entries
     */
    public List<BlockLogEntry> loadSessionLogs(String sessionId, Path sessionDir) throws IOException {
        Path blockLogFile = sessionDir.resolve("blocks.dat.gz");
        if (!Files.exists(blockLogFile)) {
            return Collections.emptyList();
        }

        List<BlockLogEntry> logs = new ArrayList<>();

        try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(blockLogFile));
             DataInputStream dis = new DataInputStream(gzip)) {

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                String serialized = dis.readUTF();
                BlockLogEntry entry = BlockLogEntry.deserialize(serialized);
                if (entry != null) {
                    logs.add(entry);
                }
            }
        }

        // Cache loaded logs
        sessionLogs.put(sessionId, logs);
        plugin.logDebug("[BlockLog] Loaded " + logs.size() + " block logs for session " + sessionId);

        return logs;
    }

    /**
     * Clear logs for a session (called when session ends or is deleted).
     *
     * @param sessionId The session ID
     */
    public void clearSession(String sessionId) {
        sessionLogs.remove(sessionId);
    }

    /**
     * Check if a session has block logs.
     *
     * @param sessionId The session ID
     * @return true if the session has block logs
     */
    public boolean hasLogs(String sessionId) {
        List<BlockLogEntry> logs = sessionLogs.get(sessionId);
        return logs != null && !logs.isEmpty();
    }

    /**
     * Get block logs for a session within a time range.
     *
     * @param sessionId The session ID
     * @param fromTime Start timestamp
     * @param toTime End timestamp
     * @return List of block log entries within the time range
     */
    public List<BlockLogEntry> getLogsInRange(String sessionId, long fromTime, long toTime) {
        List<BlockLogEntry> logs = sessionLogs.get(sessionId);
        if (logs == null) {
            return Collections.emptyList();
        }

        return logs.stream()
                .filter(e -> e.getTimestamp() >= fromTime && e.getTimestamp() <= toTime)
                .toList();
    }

    /**
     * Get the count of block logs for a session.
     *
     * @param sessionId The session ID
     * @return Number of block log entries
     */
    public int getLogCount(String sessionId) {
        List<BlockLogEntry> logs = sessionLogs.get(sessionId);
        return logs != null ? logs.size() : 0;
    }
}

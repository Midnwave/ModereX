package com.blockforge.moderex.replay.entity;

import com.blockforge.moderex.ModereX;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Manages entity event logs for replay sessions.
 * Tracks entity spawns, deaths, interactions, damage, etc.
 */
public class EntityLogManager {

    private final ModereX plugin;
    private final Map<String, List<EntityLogEntry>> sessionLogs = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> involvedEntities = new ConcurrentHashMap<>();
    private int maxEntriesPerSession = 5000;

    public EntityLogManager(ModereX plugin) {
        this.plugin = plugin;
        this.maxEntriesPerSession = plugin.getConfigManager().getSettings().getReplayEntityLogMaxPerSession();
    }

    /**
     * Log an entity event for a session.
     */
    public void logEntityEvent(String sessionId, EntityLogEntry entry) {
        if (!plugin.getConfigManager().getSettings().isReplayEntityTrackingEnabled()) {
            return;
        }

        List<EntityLogEntry> logs = sessionLogs.computeIfAbsent(sessionId, k -> Collections.synchronizedList(new ArrayList<>()));

        if (logs.size() >= maxEntriesPerSession) {
            plugin.logDebug("[EntityLog] Max entries reached for session " + sessionId);
            return;
        }

        logs.add(entry);

        // Track involved entities
        if (entry.getEntityUuid() != null) {
            involvedEntities.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(entry.getEntityUuid());
        }
    }

    /**
     * Get all entity logs for a session.
     */
    public List<EntityLogEntry> getSessionLogs(String sessionId) {
        return sessionLogs.getOrDefault(sessionId, Collections.emptyList());
    }

    /**
     * Get entities involved in a session.
     */
    public Set<UUID> getInvolvedEntities(String sessionId) {
        return involvedEntities.getOrDefault(sessionId, Collections.emptySet());
    }

    /**
     * Check if an entity is involved in a session.
     */
    public boolean isEntityInvolved(String sessionId, UUID entityUuid) {
        Set<UUID> entities = involvedEntities.get(sessionId);
        return entities != null && entities.contains(entityUuid);
    }

    /**
     * Clear logs for a session.
     */
    public void clearSession(String sessionId) {
        sessionLogs.remove(sessionId);
        involvedEntities.remove(sessionId);
    }

    /**
     * Save entity logs for a session to disk.
     */
    public void saveSessionLogs(String sessionId, Path sessionDir) throws IOException {
        List<EntityLogEntry> logs = sessionLogs.get(sessionId);
        if (logs == null || logs.isEmpty()) {
            return;
        }

        // Optionally drop uninvolved entities
        List<EntityLogEntry> toSave = logs;
        if (plugin.getConfigManager().getSettings().isReplayEntityDropUninvolved()) {
            Set<UUID> involved = involvedEntities.get(sessionId);
            if (involved != null) {
                toSave = new ArrayList<>();
                for (EntityLogEntry entry : logs) {
                    // Keep entries that have a player involved or the entity is in the involved set
                    if (entry.getInvolvedPlayerUuid() != null ||
                        (entry.getEntityUuid() != null && involved.contains(entry.getEntityUuid()))) {
                        toSave.add(entry);
                    }
                }
            }
        }

        if (toSave.isEmpty()) {
            return;
        }

        Path entityLogFile = sessionDir.resolve("entities.dat.gz");
        try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(entityLogFile));
             DataOutputStream dos = new DataOutputStream(gzip)) {

            dos.writeInt(toSave.size());
            for (EntityLogEntry entry : toSave) {
                dos.writeUTF(entry.serialize());
            }
        }

        plugin.logDebug("[EntityLog] Saved " + toSave.size() + " entity logs for session " + sessionId);
    }

    /**
     * Load entity logs for a session from disk.
     */
    public List<EntityLogEntry> loadSessionLogs(String sessionId, Path sessionDir) throws IOException {
        Path entityLogFile = sessionDir.resolve("entities.dat.gz");
        if (!Files.exists(entityLogFile)) {
            return Collections.emptyList();
        }

        List<EntityLogEntry> logs = new ArrayList<>();

        try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(entityLogFile));
             DataInputStream dis = new DataInputStream(gzip)) {

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                String serialized = dis.readUTF();
                EntityLogEntry entry = EntityLogEntry.deserialize(serialized);
                if (entry != null) {
                    logs.add(entry);
                }
            }
        }

        plugin.logDebug("[EntityLog] Loaded " + logs.size() + " entity logs for session " + sessionId);
        return logs;
    }

    /**
     * Reload settings from config.
     */
    public void reloadSettings() {
        this.maxEntriesPerSession = plugin.getConfigManager().getSettings().getReplayEntityLogMaxPerSession();
    }
}

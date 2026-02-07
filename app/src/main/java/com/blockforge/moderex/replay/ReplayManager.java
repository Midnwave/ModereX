package com.blockforge.moderex.replay;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.chunk.ChunkCaptureManager;
import com.blockforge.moderex.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ReplayManager handles recording and playback of player actions for review.
 *
 * TODO: Revamp Replay System with NPCs
 * - Replace current packet-based playback with NPC entities (Citizens2 or custom)
 * - Use packet-based NPCs for smooth movement interpolation
 * - Record more data: hotbar, armor, potion effects, chat messages
 * - Spectator mode for staff watching replays (fly around freely)
 * - Timeline scrubbing (jump to specific timestamp)
 * - Playback speed controls (0.25x, 0.5x, 1x, 2x, 4x)
 * - Multi-player replay support (show all nearby players as NPCs)
 * - Replay markers for anticheat flags (visual indicators)
 * - Export replays to video format or shareable links
 * - Web panel replay viewer (3D rendering in browser)
 */
public class ReplayManager {

    private final ModereX plugin;
    private final Path replaysDirectory;
    private final ChunkCaptureManager chunkCaptureManager;
    private final Map<UUID, ReplaySession> activeSessions = new ConcurrentHashMap<>();
    private final Map<UUID, ReplayPlayback> activePlaybacks = new ConcurrentHashMap<>();
    private final Set<UUID> watchlistAutoRecord = ConcurrentHashMap.newKeySet();
    private BukkitTask recordingTask;

    // Settings
    private boolean enabled = true;
    private boolean recordOnAnticheatAlert = true;
    private boolean recordWatchlistPlayers = true;
    private int nearbyPlayerRadius = 20;
    private int snapshotIntervalTicks = 2; // 100ms at 20 TPS
    private int maxRecordingDurationSeconds = 300; // 5 minutes
    private int maxStoredReplays = 1000;

    public ReplayManager(ModereX plugin) {
        this.plugin = plugin;
        this.replaysDirectory = plugin.getDataFolder().toPath().resolve("replays");
        this.chunkCaptureManager = new ChunkCaptureManager(plugin);

        try {
            Files.createDirectories(replaysDirectory);
        } catch (IOException e) {
            plugin.logError("Failed to create replays directory", e);
        }
    }

    public void start() {
        if (!enabled) return;

        // Start the snapshot capture task
        recordingTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::captureSnapshots,
                snapshotIntervalTicks, snapshotIntervalTicks);

        // Load settings
        loadSettings();

        plugin.getLogger().info("Replay system started");
    }

    public void stop() {
        if (recordingTask != null) {
            recordingTask.cancel();
            recordingTask = null;
        }

        // Save all active sessions
        for (ReplaySession session : activeSessions.values()) {
            try {
                session.save(replaysDirectory);
            } catch (IOException e) {
                plugin.logError("Failed to save replay session " + session.getSessionId(), e);
            }
        }
        activeSessions.clear();

        // Stop all playbacks
        for (ReplayPlayback playback : activePlaybacks.values()) {
            playback.stop();
        }
        activePlaybacks.clear();
    }

    private void loadSettings() {
        // Load from config
        var settings = plugin.getConfigManager().getSettings();
        this.enabled = settings.isReplayEnabled();
        this.recordOnAnticheatAlert = settings.isReplayRecordOnAnticheat();
        this.recordWatchlistPlayers = settings.isReplayRecordWatchlist();
        this.nearbyPlayerRadius = settings.getReplayNearbyRadius();
        this.maxRecordingDurationSeconds = settings.getReplayMaxDurationSeconds();
        this.maxStoredReplays = settings.getReplayMaxStored();
    }

    private void captureSnapshots() {
        long now = System.currentTimeMillis();

        for (Iterator<Map.Entry<UUID, ReplaySession>> it = activeSessions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, ReplaySession> entry = it.next();
            ReplaySession session = entry.getValue();

            // Check if recording should end (max duration)
            if (session.getDuration() > maxRecordingDurationSeconds * 1000L) {
                stopRecording(entry.getKey());
                it.remove();
                continue;
            }

            // Capture snapshots for all recorded players
            for (UUID playerUuid : session.getRecordedPlayerUuids()) {
                Player player = Bukkit.getPlayer(playerUuid);
                if (player != null && player.isOnline()) {
                    session.captureSnapshot(player);
                }
            }
        }

        // Auto-record watchlist players if enabled
        if (recordWatchlistPlayers) {
            for (UUID uuid : plugin.getWatchlistManager().getWatchedPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline() && !activeSessions.containsKey(uuid)) {
                    if (watchlistAutoRecord.contains(uuid)) {
                        startRecording(player, ReplaySession.RecordingReason.WATCHLIST);
                    }
                }
            }
        }
    }

    public ReplaySession startRecording(Player player, ReplaySession.RecordingReason reason) {
        UUID uuid = player.getUniqueId();

        // Check if already recording
        if (activeSessions.containsKey(uuid)) {
            return activeSessions.get(uuid);
        }

        ReplaySession session = new ReplaySession(
                uuid,
                player.getName(),
                player.getWorld().getName(),
                reason
        );

        // Add nearby players if configured
        if (nearbyPlayerRadius > 0) {
            Location loc = player.getLocation();
            for (Player nearby : player.getWorld().getPlayers()) {
                if (!nearby.equals(player) && nearby.getLocation().distance(loc) <= nearbyPlayerRadius) {
                    session.addPlayer(nearby.getUniqueId(), nearby.getName());
                }
            }
        }

        activeSessions.put(uuid, session);
        plugin.logDebug("Started recording " + player.getName() + " - Reason: " + reason);

        // Capture chunk terrain data for 3D web panel viewer
        if (plugin.getConfigManager().getSettings().isReplayChunkCaptureEnabled()) {
            try {
                Path sessionDir = replaysDirectory.resolve(session.getSessionId());
                Files.createDirectories(sessionDir);
                int radius = plugin.getConfigManager().getSettings().getReplayChunkCaptureRadius();
                chunkCaptureManager.captureChunks(player.getLocation(), sessionDir, radius);
            } catch (IOException e) {
                plugin.logError("Failed to create session dir for chunk capture", e);
            }
        }

        return session;
    }

    public boolean isBeingRecorded(UUID uuid) {
        return activeSessions.containsKey(uuid);
    }

    public CompletableFuture<String> stopRecording(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ReplaySession session = activeSessions.remove(uuid);
            if (session != null) {
                session.stopRecording();
                String sessionId = session.getSessionId();

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        session.save(replaysDirectory);
                        plugin.logDebug("Saved replay session " + sessionId);
                        cleanupOldReplays();
                    } catch (IOException e) {
                        plugin.logError("Failed to save replay session", e);
                    }
                });

                return sessionId;
            }
            return null;
        });
    }

    public void onAnticheatAlert(Player player, String checkName, int vl) {
        if (!enabled || !recordOnAnticheatAlert) return;

        // Start recording if not already
        if (!activeSessions.containsKey(player.getUniqueId())) {
            ReplaySession session = startRecording(player, ReplaySession.RecordingReason.ANTICHEAT_ALERT);
            session.captureAction(player, ReplaySnapshot.ActionType.NONE,
                    "Anticheat: " + checkName + " VL:" + vl);
        }
    }

    public void recordAction(Player player, ReplaySnapshot.ActionType action, String data) {
        ReplaySession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.captureAction(player, action, data);
        }
    }

    /**
     * Track combat between two players.
     * When player A attacks player B, ensure both players are recorded in A's session.
     * This allows replays to show the full context of combat encounters.
     *
     * @param attacker The player who initiated the attack
     * @param victim The player who was attacked
     * @param damage The damage dealt
     */
    public void onCombat(Player attacker, Player victim, double damage) {
        if (!enabled) return;

        // Check if attacker is being recorded
        ReplaySession attackerSession = activeSessions.get(attacker.getUniqueId());
        if (attackerSession != null) {
            // Add victim to the recording if not already included
            if (!attackerSession.getRecordedPlayerUuids().contains(victim.getUniqueId())) {
                attackerSession.addPlayer(victim.getUniqueId(), victim.getName());
                plugin.logDebug("[Replay] Added " + victim.getName() + " to " + attacker.getName() + "'s recording (combat)");
            }
            // Record the attack action
            attackerSession.captureAction(attacker, ReplaySnapshot.ActionType.ATTACK,
                    "Attacked " + victim.getName() + " for " + String.format("%.1f", damage) + " damage");
        }

        // Check if victim is being recorded
        ReplaySession victimSession = activeSessions.get(victim.getUniqueId());
        if (victimSession != null) {
            // Add attacker to the recording if not already included
            if (!victimSession.getRecordedPlayerUuids().contains(attacker.getUniqueId())) {
                victimSession.addPlayer(attacker.getUniqueId(), attacker.getName());
                plugin.logDebug("[Replay] Added " + attacker.getName() + " to " + victim.getName() + "'s recording (combat)");
            }
            // Record the damage received
            victimSession.captureAction(victim, ReplaySnapshot.ActionType.DAMAGE_RECEIVED,
                    "Hit by " + attacker.getName() + " for " + String.format("%.1f", damage) + " damage");
        }
    }

    /**
     * Get all active recording sessions.
     * Used for GUI display and tab completion.
     */
    public Collection<ReplaySession> getActiveSessions() {
        return Collections.unmodifiableCollection(activeSessions.values());
    }

    public int getActiveRecordingCount() {
        return activeSessions.size();
    }

    /**
     * Get the active session for a player.
     */
    public ReplaySession getActiveSession(UUID playerUuid) {
        return activeSessions.get(playerUuid);
    }

    public void setWatchlistAutoRecord(UUID uuid, boolean enabled) {
        if (enabled) {
            watchlistAutoRecord.add(uuid);
        } else {
            watchlistAutoRecord.remove(uuid);
            stopRecording(uuid);
        }
    }

    public boolean isWatchlistAutoRecording(UUID uuid) {
        return watchlistAutoRecord.contains(uuid);
    }

    /**
     * Start playback of a replay session.
     * Requires Citizens plugin to be installed.
     *
     * @param viewer The player who will view the replay
     * @param session The replay session to play
     * @return The playback instance, or null if Citizens is not available
     */
    public ReplayPlayback startPlayback(Player viewer, ReplaySession session) {
        // Check if Citizens is available
        if (!ReplayPlayback.isAvailable(plugin)) {
            Msg.send(viewer, net.kyori.adventure.text.Component.text(
                    "Replay playback requires the Citizens plugin. Download from: https://ci.citizensnpcs.co/")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return null;
        }

        // Stop any existing playback
        stopPlayback(viewer.getUniqueId());

        ReplayPlayback playback = new ReplayPlayback(plugin, viewer, session);
        if (playback.start()) {
            activePlaybacks.put(viewer.getUniqueId(), playback);
            return playback;
        }

        return null;
    }

    /**
     * Check if replay playback is available (Citizens installed).
     */
    public boolean isPlaybackAvailable() {
        return ReplayPlayback.isAvailable(plugin);
    }

    public void stopPlayback(UUID viewerUuid) {
        ReplayPlayback playback = activePlaybacks.remove(viewerUuid);
        if (playback != null) {
            playback.stop();
        }
    }

    /**
     * Called by ReplayPlayback when it stops itself.
     * Removes the playback from tracking without calling stop() again.
     */
    public void onPlaybackStopped(UUID viewerUuid) {
        activePlaybacks.remove(viewerUuid);
    }

    public ReplayPlayback getPlayback(UUID viewerUuid) {
        return activePlaybacks.get(viewerUuid);
    }

    public boolean isViewingReplay(UUID uuid) {
        return activePlaybacks.containsKey(uuid);
    }

    public CompletableFuture<List<ReplaySessionInfo>> getSavedReplays() {
        return CompletableFuture.supplyAsync(() -> {
            List<ReplaySessionInfo> replays = new ArrayList<>();

            try (Stream<Path> paths = Files.list(replaysDirectory)) {
                for (Path sessionDir : paths.filter(Files::isDirectory).toList()) {
                    Path metaFile = sessionDir.resolve("meta.properties");
                    if (Files.exists(metaFile)) {
                        try {
                            Properties meta = new Properties();
                            meta.load(Files.newInputStream(metaFile));

                            ReplaySessionInfo info = new ReplaySessionInfo(
                                    meta.getProperty("sessionId"),
                                    UUID.fromString(meta.getProperty("primaryUuid")),
                                    meta.getProperty("primaryName"),
                                    Long.parseLong(meta.getProperty("startTime")),
                                    Long.parseLong(meta.getProperty("endTime")),
                                    meta.getProperty("worldName"),
                                    ReplaySession.RecordingReason.valueOf(meta.getProperty("reason")),
                                    Integer.parseInt(meta.getProperty("playerCount"))
                            );
                            replays.add(info);
                        } catch (Exception e) {
                            plugin.logDebug("Failed to load replay info from " + sessionDir);
                        }
                    }
                }
            } catch (IOException e) {
                plugin.logError("Failed to list replays", e);
            }

            // Sort by start time (newest first)
            replays.sort((a, b) -> Long.compare(b.startTime(), a.startTime()));
            return replays;
        });
    }

    public CompletableFuture<List<ReplaySessionInfo>> searchReplaysByPlayer(UUID playerUuid) {
        return getSavedReplays().thenApply(replays ->
                replays.stream()
                        .filter(r -> r.primaryUuid().equals(playerUuid))
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<List<ReplaySessionInfo>> searchReplaysByDate(long from, long to) {
        return getSavedReplays().thenApply(replays ->
                replays.stream()
                        .filter(r -> r.startTime() >= from && r.startTime() <= to)
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<ReplaySession> loadReplay(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            Path sessionDir = replaysDirectory.resolve(sessionId);
            if (!Files.exists(sessionDir)) {
                return null;
            }

            try {
                return ReplaySession.load(sessionDir);
            } catch (IOException e) {
                plugin.logError("Failed to load replay " + sessionId, e);
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteReplay(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            Path sessionDir = replaysDirectory.resolve(sessionId);
            if (!Files.exists(sessionDir)) {
                return false;
            }

            try {
                // Delete all files in the directory
                try (Stream<Path> files = Files.list(sessionDir)) {
                    for (Path file : files.toList()) {
                        Files.delete(file);
                    }
                }
                Files.delete(sessionDir);
                return true;
            } catch (IOException e) {
                plugin.logError("Failed to delete replay " + sessionId, e);
                return false;
            }
        });
    }

    private void cleanupOldReplays() {
        try (Stream<Path> paths = Files.list(replaysDirectory)) {
            List<Path> sessionDirs = paths.filter(Files::isDirectory)
                    .sorted((a, b) -> {
                        try {
                            return Long.compare(
                                    Files.getLastModifiedTime(b).toMillis(),
                                    Files.getLastModifiedTime(a).toMillis()
                            );
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .toList();

            if (sessionDirs.size() > maxStoredReplays) {
                for (int i = maxStoredReplays; i < sessionDirs.size(); i++) {
                    Path dir = sessionDirs.get(i);
                    try (Stream<Path> files = Files.list(dir)) {
                        for (Path file : files.toList()) {
                            Files.delete(file);
                        }
                    }
                    Files.delete(dir);
                    plugin.logDebug("Deleted old replay: " + dir.getFileName());
                }
            }
        } catch (IOException e) {
            plugin.logError("Failed to cleanup old replays", e);
        }
    }

    /**
     * Get all active sessions that are recording a specific player.
     * This includes sessions where the player is the primary subject or a nearby player.
     *
     * @param playerUuid The player UUID to check
     * @return List of sessions recording this player
     */
    public List<ReplaySession> getActiveSessionsForPlayer(UUID playerUuid) {
        List<ReplaySession> result = new ArrayList<>();
        for (ReplaySession session : activeSessions.values()) {
            if (session.getRecordedPlayerUuids().contains(playerUuid)) {
                result.add(session);
            }
        }
        return result;
    }

    /**
     * Get all active sessions near a location.
     * Used for tracking environmental events like explosions or pistons.
     *
     * @param location The location to check
     * @param radius The search radius
     * @return List of sessions with recorded players near the location
     */
    public List<ReplaySession> getActiveSessionsNearLocation(Location location, int radius) {
        List<ReplaySession> result = new ArrayList<>();
        for (ReplaySession session : activeSessions.values()) {
            for (UUID playerUuid : session.getRecordedPlayerUuids()) {
                Player player = Bukkit.getPlayer(playerUuid);
                if (player != null && player.isOnline() && player.getWorld().equals(location.getWorld())) {
                    if (player.getLocation().distance(location) <= radius) {
                        result.add(session);
                        break; // Only add session once
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the replays directory path.
     */
    public Path getReplaysDirectory() {
        return replaysDirectory;
    }

    /**
     * Get the chunk capture manager.
     */
    public ChunkCaptureManager getChunkCaptureManager() {
        return chunkCaptureManager;
    }

    // Getters and setters for settings
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isRecordOnAnticheatAlert() { return recordOnAnticheatAlert; }
    public void setRecordOnAnticheatAlert(boolean value) { this.recordOnAnticheatAlert = value; }

    public boolean isRecordWatchlistPlayers() { return recordWatchlistPlayers; }
    public void setRecordWatchlistPlayers(boolean value) { this.recordWatchlistPlayers = value; }

    public int getNearbyPlayerRadius() { return nearbyPlayerRadius; }
    public void setNearbyPlayerRadius(int radius) { this.nearbyPlayerRadius = radius; }

    public int getMaxRecordingDurationSeconds() { return maxRecordingDurationSeconds; }
    public void setMaxRecordingDurationSeconds(int seconds) { this.maxRecordingDurationSeconds = seconds; }

    public record ReplaySessionInfo(
            String sessionId,
            UUID primaryUuid,
            String primaryName,
            long startTime,
            long endTime,
            String worldName,
            ReplaySession.RecordingReason reason,
            int playerCount
    ) {
        public long getDuration() {
            return endTime - startTime;
        }
    }
}

/** TODO:
 * - Fix Sounds  (QoL)
 * - Refactor explosions to not include wind* explosions
 * - Fix i18n packet manager
 * - Vanish players on replay
 */
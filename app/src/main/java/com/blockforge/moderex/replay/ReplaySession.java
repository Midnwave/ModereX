package com.blockforge.moderex.replay;

import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ReplaySession {

    private final String sessionId;
    private final UUID primaryPlayerUuid;
    private final String primaryPlayerName;
    private final long startTime;
    private long endTime;
    private final String worldName;
    private final RecordingReason reason;
    private final Map<UUID, String> recordedPlayers; // UUID -> Name
    private final Map<UUID, Queue<ReplaySnapshot>> snapshots;
    private boolean recording;
    private boolean saved;

    public enum RecordingReason {
        ANTICHEAT_ALERT,
        WATCHLIST,
        MANUAL,
        STAFF_REQUEST,
        AUTOMOD_TRIGGER
    }

    public ReplaySession(UUID primaryPlayerUuid, String primaryPlayerName, String worldName, RecordingReason reason) {
        this.sessionId = generateSessionId();
        this.primaryPlayerUuid = primaryPlayerUuid;
        this.primaryPlayerName = primaryPlayerName;
        this.startTime = System.currentTimeMillis();
        this.worldName = worldName;
        this.reason = reason;
        this.recordedPlayers = new HashMap<>();
        this.snapshots = new HashMap<>();
        this.recording = true;
        this.saved = false;

        // Add primary player
        recordedPlayers.put(primaryPlayerUuid, primaryPlayerName);
        snapshots.put(primaryPlayerUuid, new ConcurrentLinkedQueue<>());
    }

    private String generateSessionId() {
        // Format: YYYYMMDD-HHMMSS-XXXX (date-time-random)
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        return String.format("%04d%02d%02d-%02d%02d%02d-%04X",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                new Random().nextInt(0xFFFF));
    }

    public String getSessionId() { return sessionId; }
    public UUID getPrimaryPlayerUuid() { return primaryPlayerUuid; }
    public String getPrimaryPlayerName() { return primaryPlayerName; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public String getWorldName() { return worldName; }
    public RecordingReason getReason() { return reason; }
    public boolean isRecording() { return recording; }
    public boolean isSaved() { return saved; }

    public Set<UUID> getRecordedPlayerUuids() {
        return Collections.unmodifiableSet(recordedPlayers.keySet());
    }

    public String getPlayerName(UUID uuid) {
        return recordedPlayers.get(uuid);
    }

    public long getDuration() {
        return (recording ? System.currentTimeMillis() : endTime) - startTime;
    }

    public int getTotalSnapshots() {
        return snapshots.values().stream().mapToInt(Queue::size).sum();
    }

    public void addPlayer(UUID uuid, String name) {
        if (!recordedPlayers.containsKey(uuid)) {
            recordedPlayers.put(uuid, name);
            snapshots.put(uuid, new ConcurrentLinkedQueue<>());
        }
    }

    public void captureSnapshot(Player player) {
        if (!recording) return;

        UUID uuid = player.getUniqueId();
        Queue<ReplaySnapshot> playerSnapshots = snapshots.get(uuid);
        if (playerSnapshots == null) {
            addPlayer(uuid, player.getName());
            playerSnapshots = snapshots.get(uuid);
        }

        ReplaySnapshot snapshot = new ReplaySnapshot.Builder()
                .position(player.getLocation())
                .sneaking(player.isSneaking())
                .sprinting(player.isSprinting())
                .swimming(player.isSwimming())
                .gliding(player.isGliding())
                .onGround(player.isOnGround())
                .armor(player.getInventory().getArmorContents())
                .mainHand(player.getInventory().getItemInMainHand())
                .offHand(player.getInventory().getItemInOffHand())
                .heldSlot(player.getInventory().getHeldItemSlot())
                .build();

        playerSnapshots.add(snapshot);
    }

    public void captureAction(Player player, ReplaySnapshot.ActionType action, String data) {
        if (!recording) return;

        UUID uuid = player.getUniqueId();
        Queue<ReplaySnapshot> playerSnapshots = snapshots.get(uuid);
        if (playerSnapshots == null) return;

        ReplaySnapshot snapshot = new ReplaySnapshot.Builder()
                .position(player.getLocation())
                .sneaking(player.isSneaking())
                .sprinting(player.isSprinting())
                .swimming(player.isSwimming())
                .gliding(player.isGliding())
                .onGround(player.isOnGround())
                .armor(player.getInventory().getArmorContents())
                .mainHand(player.getInventory().getItemInMainHand())
                .offHand(player.getInventory().getItemInOffHand())
                .heldSlot(player.getInventory().getHeldItemSlot())
                .action(action, data)
                .build();

        playerSnapshots.add(snapshot);
    }

    public void stopRecording() {
        if (recording) {
            recording = false;
            endTime = System.currentTimeMillis();
        }
    }

    public List<ReplaySnapshot> getSnapshots(UUID uuid, long fromTime, long toTime) {
        Queue<ReplaySnapshot> playerSnapshots = snapshots.get(uuid);
        if (playerSnapshots == null) return Collections.emptyList();

        return playerSnapshots.stream()
                .filter(s -> s.getTimestamp() >= fromTime && s.getTimestamp() <= toTime)
                .toList();
    }

    public List<ReplaySnapshot> getSnapshots(UUID uuid) {
        Queue<ReplaySnapshot> playerSnapshots = snapshots.get(uuid);
        if (playerSnapshots == null) return Collections.emptyList();
        return new ArrayList<>(playerSnapshots);
    }

    public void save(Path directory) throws IOException {
        stopRecording();

        Path sessionDir = directory.resolve(sessionId);
        Files.createDirectories(sessionDir);

        // Save metadata
        Properties meta = new Properties();
        meta.setProperty("sessionId", sessionId);
        meta.setProperty("primaryUuid", primaryPlayerUuid.toString());
        meta.setProperty("primaryName", primaryPlayerName);
        meta.setProperty("startTime", String.valueOf(startTime));
        meta.setProperty("endTime", String.valueOf(endTime));
        meta.setProperty("worldName", worldName);
        meta.setProperty("reason", reason.name());
        meta.setProperty("playerCount", String.valueOf(recordedPlayers.size()));

        int i = 0;
        for (Map.Entry<UUID, String> entry : recordedPlayers.entrySet()) {
            meta.setProperty("player." + i + ".uuid", entry.getKey().toString());
            meta.setProperty("player." + i + ".name", entry.getValue());
            i++;
        }

        try (OutputStream out = Files.newOutputStream(sessionDir.resolve("meta.properties"))) {
            meta.store(out, "ModereX Replay Session");
        }

        // Save snapshots for each player (compressed)
        for (Map.Entry<UUID, Queue<ReplaySnapshot>> entry : snapshots.entrySet()) {
            Path snapshotFile = sessionDir.resolve(entry.getKey().toString() + ".dat.gz");
            try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(snapshotFile));
                 DataOutputStream dos = new DataOutputStream(gzip)) {

                Queue<ReplaySnapshot> playerSnapshots = entry.getValue();
                dos.writeInt(playerSnapshots.size());

                for (ReplaySnapshot snapshot : playerSnapshots) {
                    String serialized = snapshot.serialize();
                    dos.writeUTF(serialized);
                }
            }
        }

        saved = true;
    }

    public static ReplaySession load(Path sessionDir) throws IOException {
        Path metaFile = sessionDir.resolve("meta.properties");
        if (!Files.exists(metaFile)) {
            throw new IOException("Missing meta.properties");
        }

        Properties meta = new Properties();
        try (InputStream in = Files.newInputStream(metaFile)) {
            meta.load(in);
        }

        String sessionId = meta.getProperty("sessionId");
        UUID primaryUuid = UUID.fromString(meta.getProperty("primaryUuid"));
        String primaryName = meta.getProperty("primaryName");
        long startTime = Long.parseLong(meta.getProperty("startTime"));
        long endTime = Long.parseLong(meta.getProperty("endTime"));
        String worldName = meta.getProperty("worldName");
        RecordingReason reason = RecordingReason.valueOf(meta.getProperty("reason"));
        int playerCount = Integer.parseInt(meta.getProperty("playerCount"));

        ReplaySession session = new ReplaySession(primaryUuid, primaryName, worldName, reason);
        // Override generated values
        try {
            java.lang.reflect.Field idField = ReplaySession.class.getDeclaredField("sessionId");
            idField.setAccessible(true);
            idField.set(session, sessionId);

            java.lang.reflect.Field startField = ReplaySession.class.getDeclaredField("startTime");
            startField.setAccessible(true);
            startField.set(session, startTime);
        } catch (Exception ignored) {}

        session.endTime = endTime;
        session.recording = false;
        session.saved = true;

        // Load players
        for (int i = 0; i < playerCount; i++) {
            UUID uuid = UUID.fromString(meta.getProperty("player." + i + ".uuid"));
            String name = meta.getProperty("player." + i + ".name");
            if (!uuid.equals(primaryUuid)) {
                session.addPlayer(uuid, name);
            }
        }

        // Load snapshots
        for (UUID uuid : session.recordedPlayers.keySet()) {
            Path snapshotFile = sessionDir.resolve(uuid.toString() + ".dat.gz");
            if (Files.exists(snapshotFile)) {
                try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(snapshotFile));
                     DataInputStream dis = new DataInputStream(gzip)) {

                    int count = dis.readInt();
                    Queue<ReplaySnapshot> playerSnapshots = session.snapshots.get(uuid);

                    for (int i = 0; i < count; i++) {
                        String serialized = dis.readUTF();
                        ReplaySnapshot snapshot = ReplaySnapshot.deserialize(serialized);
                        if (snapshot != null) {
                            playerSnapshots.add(snapshot);
                        }
                    }
                }
            }
        }

        return session;
    }

    public String getSummary() {
        return String.format("%s | %s | %s | %dms | %d players",
                sessionId, primaryPlayerName, reason.name(),
                getDuration(), recordedPlayers.size());
    }
}

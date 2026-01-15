package com.blockforge.moderex.replay;

import com.blockforge.moderex.ModereX;
import org.bukkit.Location;

import java.util.Random;
import java.util.UUID;

public class TestReplayGenerator {

    private static final Random RANDOM = new Random();

    public static ReplaySession generateTestReplay(ModereX plugin, String worldName, double centerX, double centerZ) {
        UUID player1Uuid = UUID.randomUUID();
        UUID player2Uuid = UUID.randomUUID();
        String player1Name = "TestPlayer1";
        String player2Name = "TestPlayer2";

        ReplaySession session = new ReplaySession(
                player1Uuid,
                player1Name,
                worldName,
                ReplaySession.RecordingReason.MANUAL
        );

        // Add second player
        session.addPlayer(player2Uuid, player2Name);

        // Generate 10 seconds of movement (100ms intervals = 100 snapshots)
        long startTime = System.currentTimeMillis();
        double p1X = centerX, p1Z = centerZ;
        double p2X = centerX + 5, p2Z = centerZ + 5;
        float p1Yaw = 0, p2Yaw = 180;

        for (int i = 0; i < 100; i++) {
            long timestamp = startTime + (i * 100); // 100ms intervals

            // Player 1 moves in a circle
            double angle1 = (i * 0.1);
            p1X = centerX + Math.cos(angle1) * 5;
            p1Z = centerZ + Math.sin(angle1) * 5;
            p1Yaw = (float) Math.toDegrees(angle1) + 90;

            // Player 2 moves in opposite circle
            double angle2 = -(i * 0.08);
            p2X = centerX + Math.cos(angle2) * 8;
            p2Z = centerZ + Math.sin(angle2) * 8;
            p2Yaw = (float) Math.toDegrees(angle2) + 90;

            // Determine player states
            boolean p1Sprinting = i >= 20 && i < 40;
            boolean p1Sneaking = i >= 50 && i < 70;
            boolean p2Sprinting = i >= 30 && i < 60;
            boolean p2Sneaking = i >= 80 && i < 100;

            // Create snapshots for both players
            ReplaySnapshot snap1 = new ReplaySnapshot.Builder()
                    .timestamp(timestamp)
                    .position(p1X, 64, p1Z, p1Yaw, 0)
                    .world(worldName)
                    .sneaking(p1Sneaking)
                    .sprinting(p1Sprinting)
                    .onGround(true)
                    .heldSlot(0)
                    .build();

            ReplaySnapshot snap2 = new ReplaySnapshot.Builder()
                    .timestamp(timestamp)
                    .position(p2X, 64, p2Z, p2Yaw, 0)
                    .world(worldName)
                    .sneaking(p2Sneaking)
                    .sprinting(p2Sprinting)
                    .onGround(true)
                    .heldSlot(0)
                    .build();

            // Add snapshots via reflection since we can't directly access queues
            addSnapshotToSession(session, player1Uuid, snap1);
            addSnapshotToSession(session, player2Uuid, snap2);

            // Add some events
            if (i == 25) {
                ReplaySnapshot chatSnap = new ReplaySnapshot.Builder()
                        .timestamp(timestamp)
                        .position(p1X, 64, p1Z, p1Yaw, 0)
                        .world(worldName)
                        .onGround(true)
                        .action(ReplaySnapshot.ActionType.CHAT, "Hello from TestPlayer1!")
                        .build();
                addSnapshotToSession(session, player1Uuid, chatSnap);
            }

            if (i == 50) {
                ReplaySnapshot cmdSnap = new ReplaySnapshot.Builder()
                        .timestamp(timestamp)
                        .position(p2X, 64, p2Z, p2Yaw, 0)
                        .world(worldName)
                        .onGround(true)
                        .action(ReplaySnapshot.ActionType.COMMAND, "/gamemode creative")
                        .build();
                addSnapshotToSession(session, player2Uuid, cmdSnap);
            }

            if (i == 75) {
                ReplaySnapshot attackSnap = new ReplaySnapshot.Builder()
                        .timestamp(timestamp)
                        .position(p1X, 64, p1Z, p1Yaw, 0)
                        .world(worldName)
                        .onGround(true)
                        .action(ReplaySnapshot.ActionType.DAMAGE_DEALT, "Dealt 5.0 dmg to TestPlayer2")
                        .build();
                addSnapshotToSession(session, player1Uuid, attackSnap);
            }
        }

        // Set end time
        session.stopRecording();

        return session;
    }

    private static void addSnapshotToSession(ReplaySession session, UUID playerUuid, ReplaySnapshot snapshot) {
        try {
            java.lang.reflect.Field snapshotsField = ReplaySession.class.getDeclaredField("snapshots");
            snapshotsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<UUID, java.util.Queue<ReplaySnapshot>> snapshots =
                    (java.util.Map<UUID, java.util.Queue<ReplaySnapshot>>) snapshotsField.get(session);

            java.util.Queue<ReplaySnapshot> queue = snapshots.get(playerUuid);
            if (queue != null) {
                queue.add(snapshot);
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
    }
}

package com.blockforge.moderex.replay.chunk;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures chunk terrain data for replay sessions.
 * Takes a snapshot of the surrounding chunks when a recording starts,
 * allowing the web panel to render actual Minecraft terrain in 3D.
 *
 * Capture happens on the main thread (Bukkit requirement for ChunkSnapshot),
 * serialization runs async to avoid server lag.
 */
public class ChunkCaptureManager {

    private final ModereX plugin;

    public ChunkCaptureManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Capture chunk snapshots around a center location.
     * Must be called from the main server thread.
     * Serialization is done asynchronously.
     *
     * @param center The center location (player position)
     * @param sessionDir The directory to save chunk data to
     * @param radius Chunk radius (e.g. 3 = 7x7 = 49 chunks)
     */
    public void captureChunks(Location center, Path sessionDir, int radius) {
        int captureRadius = Math.max(0, Math.min(6, radius));
        if (captureRadius <= 0) return;

        World world = center.getWorld();
        if (world == null) return;

        int centerChunkX = center.getBlockX() >> 4;
        int centerChunkZ = center.getBlockZ() >> 4;

        // Capture snapshots on main thread (required by Bukkit API)
        List<ChunkSnapshot> snapshots = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int dx = -captureRadius; dx <= captureRadius; dx++) {
            for (int dz = -captureRadius; dz <= captureRadius; dz++) {
                int cx = centerChunkX + dx;
                int cz = centerChunkZ + dz;

                // Only snapshot loaded chunks to avoid loading new ones
                if (world.isChunkLoaded(cx, cz)) {
                    Chunk chunk = world.getChunkAt(cx, cz);
                    // includeBiome=false, includeBiomeTemp=false for speed
                    snapshots.add(chunk.getChunkSnapshot(false, false, false));
                }
            }
        }

        long captureTime = System.currentTimeMillis() - startTime;
        plugin.logDebug("[Replay] Captured " + snapshots.size() + " chunk snapshots in " + captureTime + "ms");

        if (snapshots.isEmpty()) return;

        // Serialize asynchronously to avoid blocking the server thread
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                long serializeStart = System.currentTimeMillis();
                Path chunkFile = sessionDir.resolve("chunks.dat.gz");
                ChunkDataSerializer.serialize(snapshots, chunkFile);
                long serializeTime = System.currentTimeMillis() - serializeStart;

                long fileSize = java.nio.file.Files.size(chunkFile);
                plugin.logDebug("[Replay] Serialized " + snapshots.size() + " chunks in " + serializeTime +
                        "ms (" + formatFileSize(fileSize) + ")");
            } catch (IOException e) {
                plugin.logError("[Replay] Failed to serialize chunk data", e);
            }
        });
    }

    /**
     * Check if chunk data exists for a replay session.
     */
    public static boolean hasChunkData(Path sessionDir) {
        return java.nio.file.Files.exists(sessionDir.resolve("chunks.dat.gz"));
    }

    /**
     * Get the chunk data file path for a session.
     */
    public static Path getChunkDataFile(Path sessionDir) {
        return sessionDir.resolve("chunks.dat.gz");
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}

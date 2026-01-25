package com.blockforge.moderex.replay.block;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages client-side-only block changes for replay visualization.
 * Uses Paper's sendBlockChange() to show fake blocks that only the viewer sees.
 */
public class FakeBlockManager {

    private final ModereX plugin;

    // Tracks fake blocks per viewer: viewerUUID -> set of location keys
    private final Map<UUID, Set<String>> viewerFakeBlocks = new ConcurrentHashMap<>();

    // Stores the real server-side state for each fake block: viewerUUID -> (locationKey -> real BlockData)
    private final Map<UUID, Map<String, BlockData>> serverStates = new ConcurrentHashMap<>();

    public FakeBlockManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize fake blocks for a replay viewer.
     * - For PLACE blocks: Show the original (old) state, will be revealed during playback
     * - For BREAK blocks: Show the original (old) state (the block before it was broken), will be broken during playback
     *
     * @param viewer The player watching the replay
     * @param blockLogs The block logs from the session
     */
    public void initializeForViewer(Player viewer, List<BlockLogEntry> blockLogs) {
        if (!plugin.getConfigManager().getSettings().isReplayFakeBlocksEnabled()) {
            return;
        }

        UUID viewerUuid = viewer.getUniqueId();
        Set<String> fakeBlocks = viewerFakeBlocks.computeIfAbsent(viewerUuid, k -> ConcurrentHashMap.newKeySet());
        Map<String, BlockData> realStates = serverStates.computeIfAbsent(viewerUuid, k -> new ConcurrentHashMap<>());

        int placedCount = 0;
        int brokenCount = 0;

        // Process block logs to find blocks that need to be hidden/shown
        for (BlockLogEntry entry : blockLogs) {
            String locationKey = entry.getLocationKey();
            World world = Bukkit.getWorld(entry.getWorldName());
            if (world == null) continue;

            Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());

            // Store the current real block state if not already stored
            if (!realStates.containsKey(locationKey)) {
                realStates.put(locationKey, loc.getBlock().getBlockData().clone());
            }

            if (entry.getAction() == BlockLogEntry.Action.PLACE) {
                // For placed blocks, show the original (old) state - before the block was placed
                fakeBlocks.add(locationKey);

                BlockData originalData = createBlockData(entry.getOldMaterial(), entry.getOldBlockData());
                if (originalData != null) {
                    viewer.sendBlockChange(loc, originalData);
                    placedCount++;
                }
            } else if (entry.getAction() == BlockLogEntry.Action.BREAK ||
                       entry.getAction() == BlockLogEntry.Action.EXPLOSION) {
                // For broken/exploded blocks, show the original (old) state - the block that was there before
                // This way, during playback when BREAK/EXPLOSION action occurs, we show AIR
                fakeBlocks.add(locationKey);

                BlockData originalData = createBlockData(entry.getOldMaterial(), entry.getOldBlockData());
                if (originalData != null) {
                    viewer.sendBlockChange(loc, originalData);
                    brokenCount++;
                }
            }
        }

        plugin.logDebug("[FakeBlock] Initialized " + (placedCount + brokenCount) + " fake blocks for " + viewer.getName() +
                " (" + placedCount + " placed, " + brokenCount + " broken)");
    }

    /**
     * Reveal a block (show the real server state) when it's "placed" in the timeline.
     *
     * @param viewer The replay viewer
     * @param location The block location
     */
    public void revealBlock(Player viewer, Location location) {
        if (!plugin.getConfigManager().getSettings().isReplayFakeBlocksEnabled()) {
            return;
        }

        UUID viewerUuid = viewer.getUniqueId();
        String locationKey = getLocationKey(location);

        Set<String> fakeBlocks = viewerFakeBlocks.get(viewerUuid);
        Map<String, BlockData> realStates = serverStates.get(viewerUuid);

        if (fakeBlocks == null || realStates == null) {
            return;
        }

        if (fakeBlocks.contains(locationKey)) {
            BlockData realData = realStates.get(locationKey);
            if (realData != null) {
                viewer.sendBlockChange(location, realData);
                plugin.logDebug("[FakeBlock] Revealed block at " + locationKey);
            } else {
                // Fallback: send current server state
                viewer.sendBlockChange(location, location.getBlock().getBlockData());
            }
            fakeBlocks.remove(locationKey);
        }
    }

    /**
     * Hide a block (show fake state) when it's "broken" in the timeline.
     *
     * @param viewer The replay viewer
     * @param location The block location
     * @param fakeData The fake block data to show
     */
    public void hideBlock(Player viewer, Location location, BlockData fakeData) {
        if (!plugin.getConfigManager().getSettings().isReplayFakeBlocksEnabled()) {
            return;
        }

        UUID viewerUuid = viewer.getUniqueId();
        String locationKey = getLocationKey(location);

        Set<String> fakeBlocks = viewerFakeBlocks.computeIfAbsent(viewerUuid, k -> ConcurrentHashMap.newKeySet());
        Map<String, BlockData> realStates = serverStates.computeIfAbsent(viewerUuid, k -> new ConcurrentHashMap<>());

        // Store current real state if not already stored
        if (!realStates.containsKey(locationKey)) {
            realStates.put(locationKey, location.getBlock().getBlockData().clone());
        }

        fakeBlocks.add(locationKey);
        viewer.sendBlockChange(location, fakeData);
        plugin.logDebug("[FakeBlock] Hiding block at " + locationKey + " with " + fakeData.getMaterial());
    }

    /**
     * Hide a block with AIR (for block break actions).
     *
     * @param viewer The replay viewer
     * @param location The block location
     */
    public void hideBlockAsAir(Player viewer, Location location) {
        hideBlock(viewer, location, Material.AIR.createBlockData());
    }

    /**
     * Restore all blocks to their real server state when playback ends.
     *
     * @param viewer The replay viewer
     */
    public void cleanupViewer(Player viewer) {
        UUID viewerUuid = viewer.getUniqueId();

        Set<String> fakeBlocks = viewerFakeBlocks.remove(viewerUuid);
        Map<String, BlockData> realStates = serverStates.remove(viewerUuid);

        if (fakeBlocks == null || realStates == null) {
            return;
        }

        int restored = 0;
        for (String locationKey : fakeBlocks) {
            BlockData realData = realStates.get(locationKey);
            Location loc = parseLocationKey(locationKey);

            if (loc != null && realData != null && loc.isWorldLoaded()) {
                viewer.sendBlockChange(loc, realData);
                restored++;
            } else if (loc != null && loc.isWorldLoaded()) {
                // Fallback: send current server state
                viewer.sendBlockChange(loc, loc.getBlock().getBlockData());
                restored++;
            }
        }

        plugin.logDebug("[FakeBlock] Cleaned up " + restored + " fake blocks for " + viewer.getName());
    }

    /**
     * Check if a location is currently showing a fake block for a viewer.
     */
    public boolean isFakeBlock(UUID viewerUuid, Location location) {
        Set<String> fakeBlocks = viewerFakeBlocks.get(viewerUuid);
        if (fakeBlocks == null) return false;
        return fakeBlocks.contains(getLocationKey(location));
    }

    /**
     * Get the number of fake blocks for a viewer.
     */
    public int getFakeBlockCount(UUID viewerUuid) {
        Set<String> fakeBlocks = viewerFakeBlocks.get(viewerUuid);
        return fakeBlocks != null ? fakeBlocks.size() : 0;
    }

    /**
     * Create a location key string.
     */
    private String getLocationKey(Location location) {
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ();
    }

    /**
     * Parse a location key string back to a Location.
     */
    private Location parseLocationKey(String key) {
        String[] parts = key.split(":");
        if (parts.length != 4) return null;

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;

        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Create BlockData from material and serialized data string.
     */
    private BlockData createBlockData(Material material, String serializedData) {
        if (material == null) {
            material = Material.AIR;
        }

        try {
            if (serializedData != null && !serializedData.isEmpty()) {
                return Bukkit.createBlockData(serializedData);
            } else {
                return material.createBlockData();
            }
        } catch (Exception e) {
            plugin.logDebug("[FakeBlock] Failed to create block data: " + e.getMessage());
            return material.createBlockData();
        }
    }

    // ==================== PHYSICAL BLOCK HANDLING ====================
    // These methods physically place/break blocks in the world instead of using fake packets

    // Tracks physical block changes per viewer for restoration: viewerUUID -> (locationKey -> original BlockData)
    private final Map<UUID, Map<String, BlockData>> physicalBlockBackups = new ConcurrentHashMap<>();

    /**
     * Initialize physical blocks for a replay viewer.
     * Stores the current state of all blocks that will be modified during playback
     * and reverts blocks to their original state (before any changes in the recording).
     *
     * @param viewer The player watching the replay
     * @param blockLogs The block logs from the session
     */
    public void initializePhysicalBlocks(Player viewer, List<BlockLogEntry> blockLogs) {
        UUID viewerUuid = viewer.getUniqueId();
        Map<String, BlockData> backups = physicalBlockBackups.computeIfAbsent(viewerUuid, k -> new ConcurrentHashMap<>());

        int placedReverted = 0;
        int brokenRestored = 0;

        for (BlockLogEntry entry : blockLogs) {
            String locationKey = entry.getLocationKey();
            World world = Bukkit.getWorld(entry.getWorldName());
            if (world == null) continue;

            Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
            Block block = loc.getBlock();

            // Store current state if not already stored (this is what we restore to after replay)
            if (!backups.containsKey(locationKey)) {
                backups.put(locationKey, block.getBlockData().clone());
            }

            if (entry.getAction() == BlockLogEntry.Action.PLACE) {
                // For placed blocks, revert to original state (what was there before placement)
                BlockData originalData = createBlockData(entry.getOldMaterial(), entry.getOldBlockData());
                if (originalData != null) {
                    block.setBlockData(originalData, false);
                    placedReverted++;
                }
            } else if (entry.getAction() == BlockLogEntry.Action.BREAK ||
                       entry.getAction() == BlockLogEntry.Action.EXPLOSION) {
                // For broken/exploded blocks, restore the block that was there before
                BlockData originalData = createBlockData(entry.getOldMaterial(), entry.getOldBlockData());
                if (originalData != null) {
                    block.setBlockData(originalData, false);
                    brokenRestored++;
                }
            }
        }

        plugin.logDebug("[PhysicalBlock] Initialized " + (placedReverted + brokenRestored) + " physical blocks for " + viewer.getName() +
                " (" + placedReverted + " placed reverted, " + brokenRestored + " broken restored)");
    }

    /**
     * Physically place a block when a PLACE_BLOCK action occurs in the replay timeline.
     *
     * @param viewer The replay viewer (for tracking)
     * @param location The block location
     * @param material The material to place
     * @param blockDataString The block data string (may be null)
     */
    public void physicallyPlaceBlock(Player viewer, Location location, Material material, String blockDataString) {
        UUID viewerUuid = viewer.getUniqueId();
        String locationKey = getLocationKey(location);
        Map<String, BlockData> backups = physicalBlockBackups.computeIfAbsent(viewerUuid, k -> new ConcurrentHashMap<>());

        Block block = location.getBlock();

        // Store current state if not already stored
        if (!backups.containsKey(locationKey)) {
            backups.put(locationKey, block.getBlockData().clone());
        }

        // Place the block
        BlockData newData = createBlockData(material, blockDataString);
        if (newData != null) {
            block.setBlockData(newData, false);
            plugin.logDebug("[PhysicalBlock] Placed " + material + " at " + locationKey);
        }
    }

    /**
     * Physically break a block when a BREAK_BLOCK action occurs in the replay timeline.
     *
     * @param viewer The replay viewer (for tracking)
     * @param location The block location
     */
    public void physicallyBreakBlock(Player viewer, Location location) {
        UUID viewerUuid = viewer.getUniqueId();
        String locationKey = getLocationKey(location);
        Map<String, BlockData> backups = physicalBlockBackups.computeIfAbsent(viewerUuid, k -> new ConcurrentHashMap<>());

        Block block = location.getBlock();

        // Store current state if not already stored
        if (!backups.containsKey(locationKey)) {
            backups.put(locationKey, block.getBlockData().clone());
        }

        // Break the block (set to air)
        block.setType(Material.AIR, false);
        plugin.logDebug("[PhysicalBlock] Broke block at " + locationKey);
    }

    /**
     * Restore all physically modified blocks to their original state when playback ends.
     *
     * @param viewer The replay viewer
     */
    public void restorePhysicalBlocks(Player viewer) {
        UUID viewerUuid = viewer.getUniqueId();
        Map<String, BlockData> backups = physicalBlockBackups.remove(viewerUuid);

        if (backups == null || backups.isEmpty()) {
            return;
        }

        int restored = 0;
        for (Map.Entry<String, BlockData> entry : backups.entrySet()) {
            Location loc = parseLocationKey(entry.getKey());
            if (loc != null && loc.isWorldLoaded()) {
                Block block = loc.getBlock();
                block.setBlockData(entry.getValue(), false);
                restored++;
            }
        }

        plugin.logDebug("[PhysicalBlock] Restored " + restored + " physical blocks for " + viewer.getName());
    }

    /**
     * Check if using physical blocks mode for a viewer.
     */
    public boolean hasPhysicalBlockBackups(UUID viewerUuid) {
        Map<String, BlockData> backups = physicalBlockBackups.get(viewerUuid);
        return backups != null && !backups.isEmpty();
    }
}

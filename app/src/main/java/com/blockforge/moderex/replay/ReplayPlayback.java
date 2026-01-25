package com.blockforge.moderex.replay;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.CitizensHook;
import com.blockforge.moderex.replay.block.BlockLogEntry;
import com.blockforge.moderex.replay.block.FakeBlockManager;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ReplayPlayback handles playback of recorded replay sessions using Citizens NPCs.
 * REQUIRES Citizens plugin to be installed - playback is disabled without it.
 */
public class ReplayPlayback {

    private final ModereX plugin;
    private final Player viewer;
    private final ReplaySession session;

    // Playback state
    private boolean playing = false;
    private boolean paused = false;
    private long playbackStartTime;
    private long currentPlaybackTime;
    private float playbackSpeed = 1.0f;

    // Viewer state backup
    private Location originalLocation;
    private GameMode originalGameMode;
    private ItemStack[] originalInventory;
    private ItemStack[] originalArmor;
    private ItemStack originalOffHand;
    private int originalHeldSlot;
    private boolean wasFlying;
    private boolean wasAllowFlight;
    private Collection<PotionEffect> originalEffects;

    // Citizens NPCs - Player UUID -> Citizens NPC UUID
    private final Map<UUID, UUID> npcIds = new HashMap<>();
    private BukkitTask playbackTask;

    // Action log tracking - track last shown action index per player
    private final Map<UUID, Integer> lastActionIndex = new HashMap<>();

    // Block log tracking for physical block mode
    private List<BlockLogEntry> blockLogs = new ArrayList<>();
    private final Map<String, BlockLogEntry> blockLogByLocation = new ConcurrentHashMap<>();
    private boolean usingPhysicalBlocks = false;
    private int lastBlockLogIndex = 0;

    // Players teleported away for safety (to restore their location after playback)
    private final Map<UUID, Location> teleportedPlayersOriginalLocations = new ConcurrentHashMap<>();

    // Track projectile actions already processed to avoid duplicate spawns
    private final Set<Long> processedProjectileTimestamps = new HashSet<>();

    // Hotbar control slots
    private static final int SLOT_REWIND_10 = 0;
    private static final int SLOT_REWIND_5 = 1;
    private static final int SLOT_REWIND_1 = 2;
    private static final int SLOT_PAUSE_PLAY = 3;
    private static final int SLOT_STOP = 4;
    private static final int SLOT_FF_1 = 5;
    private static final int SLOT_FF_5 = 6;
    private static final int SLOT_FF_10 = 7;
    private static final int SLOT_SPEED = 8;

    public ReplayPlayback(ModereX plugin, Player viewer, ReplaySession session) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.session = session;
    }

    /**
     * Check if replay playback is available (Citizens is installed).
     */
    public static boolean isAvailable(ModereX plugin) {
        return plugin.getHookManager().hasCitizens();
    }

    /**
     * Start replay playback.
     * @return true if playback started successfully, false if Citizens is not available
     */
    public boolean start() {
        if (playing) return true;

        // Require Citizens for NPC playback
        if (!plugin.getHookManager().hasCitizens()) {
            viewer.sendMessage(TextUtil.parse("<red>Replay playback requires the Citizens plugin to be installed."));
            viewer.sendMessage(TextUtil.parse("<gray>Download Citizens from: <aqua>https://ci.citizensnpcs.co/"));
            return false;
        }

        CitizensHook citizens = plugin.getHookManager().getCitizensHook();
        plugin.logDebug("[Replay] Starting playback with Citizens NPCs");

        // Backup viewer state
        backupViewerState();

        // Setup viewer for spectating
        setupViewer();

        // Create Citizens NPCs for each recorded player
        for (UUID playerUuid : session.getRecordedPlayerUuids()) {
            String name = session.getPlayerName(playerUuid);
            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);

            if (!snapshots.isEmpty()) {
                ReplaySnapshot first = snapshots.get(0);
                World world = Bukkit.getWorld(first.getWorldName());

                if (world != null) {
                    Location spawnLoc = first.toLocation(world);
                    UUID npcId = citizens.createReplayNpc(name, playerUuid, spawnLoc);

                    if (npcId != null) {
                        npcIds.put(playerUuid, npcId);
                        plugin.logDebug("[Replay] Created NPC for " + name + " (ID: " + npcId + ")");
                    } else {
                        plugin.logDebug("[Replay] Failed to create NPC for " + name);
                    }
                }
            }
        }

        if (npcIds.isEmpty()) {
            viewer.sendMessage(TextUtil.parse("<red>Failed to create replay NPCs. Check console for errors."));
            restoreViewerState();
            return false;
        }

        // Initialize playback state
        playing = true;
        paused = false;
        playbackStartTime = System.currentTimeMillis();
        currentPlaybackTime = session.getStartTime();

        // Determine block handling mode
        usingPhysicalBlocks = plugin.getConfigManager().getSettings().isReplayUsePhysicalBlocks();

        // Initialize blocks (fake or physical)
        if (plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            loadBlockLogs();

            if (usingPhysicalBlocks) {
                // Scan for players and teleport them away before modifying blocks
                teleportNearbyPlayersAway();

                // Initialize physical blocks
                plugin.getFakeBlockManager().initializePhysicalBlocks(viewer, blockLogs);
            } else if (plugin.getConfigManager().getSettings().isReplayFakeBlocksEnabled()) {
                // Initialize fake blocks (client-side only)
                initializeFakeBlocks();
            }
        }

        // Start playback task (runs every tick)
        playbackTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);

        // Show intro
        showIntro();

        plugin.logDebug("Started playback of " + session.getSessionId() + " for " + viewer.getName());
        return true;
    }

    /**
     * Stop replay playback and cleanup NPCs.
     */
    public void stop() {
        if (!playing) return;

        playing = false;

        // CRITICAL: Remove from activePlaybacks FIRST so ReplayListener stops cancelling events
        plugin.getReplayManager().onPlaybackStopped(viewer.getUniqueId());

        try {
            // Stop tick task
            if (playbackTask != null) {
                playbackTask.cancel();
                playbackTask = null;
            }

            // Remove all Citizens NPCs
            if (plugin.getHookManager().hasCitizens()) {
                CitizensHook citizens = plugin.getHookManager().getCitizensHook();
                for (UUID npcId : npcIds.values()) {
                    citizens.removeNpc(npcId);
                }
            }
            npcIds.clear();

            viewer.sendMessage(TextUtil.parse("<gray>Replay playback ended."));
            plugin.logDebug("Stopped playback for " + viewer.getName());
        } finally {
            // Cleanup blocks (physical or fake)
            if (plugin.getFakeBlockManager() != null) {
                if (usingPhysicalBlocks) {
                    // Restore physical blocks first
                    plugin.getFakeBlockManager().restorePhysicalBlocks(viewer);
                } else {
                    // Cleanup fake blocks
                    plugin.getFakeBlockManager().cleanupViewer(viewer);
                }
            }

            // Restore teleported players to their original locations
            restoreTeleportedPlayers();

            // Always restore viewer state, even if NPC cleanup fails
            restoreViewerState();
        }
    }

    public void togglePause() {
        paused = !paused;
        updateHotbar();

        if (paused) {
            viewer.sendMessage(TextUtil.parse("<yellow>Replay paused. <gray>Press <white>[" +
                    (SLOT_PAUSE_PLAY + 1) + "] <gray>to resume."));
        } else {
            viewer.sendMessage(TextUtil.parse("<green>Replay resumed."));
        }
    }

    public void skip(int seconds) {
        long skipMs = seconds * 1000L;
        long newTime = currentPlaybackTime + skipMs;

        // Clamp to valid range
        newTime = Math.max(session.getStartTime(), Math.min(session.getEndTime(), newTime));
        currentPlaybackTime = newTime;

        // Reset action log indexes when skipping (to re-display or skip actions appropriately)
        recalculateActionIndexes();

        // Update all NPCs to new positions
        updateNpcs();

        String direction = seconds > 0 ? "forward" : "back";
        viewer.sendMessage(TextUtil.parse("<gray>Skipped " + direction + " <white>" +
                Math.abs(seconds) + "s<gray>."));
    }

    /**
     * Recalculate action log indexes after a time skip.
     * Finds the correct position in each player's action log based on current playback time.
     */
    private void recalculateActionIndexes() {
        for (UUID playerUuid : session.getRecordedPlayerUuids()) {
            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);
            int newIndex = 0;

            // Find the first snapshot after current time (actions before this should be skipped)
            for (int i = 0; i < snapshots.size(); i++) {
                if (snapshots.get(i).getTimestamp() > currentPlaybackTime) {
                    break;
                }
                newIndex = i + 1;
            }

            lastActionIndex.put(playerUuid, newIndex);
        }

        // Also recalculate block log index
        lastBlockLogIndex = 0;
        for (int i = 0; i < blockLogs.size(); i++) {
            if (blockLogs.get(i).getTimestamp() > currentPlaybackTime) {
                break;
            }
            lastBlockLogIndex = i + 1;
        }

        // Clear processed projectiles when seeking (they may need to be replayed)
        processedProjectileTimestamps.clear();
    }

    public void cycleSpeed() {
        if (playbackSpeed == 0.25f) {
            playbackSpeed = 0.5f;
        } else if (playbackSpeed == 0.5f) {
            playbackSpeed = 1.0f;
        } else if (playbackSpeed == 1.0f) {
            playbackSpeed = 2.0f;
        } else if (playbackSpeed == 2.0f) {
            playbackSpeed = 4.0f;
        } else {
            playbackSpeed = 0.25f;
        }

        updateHotbar();
        viewer.sendMessage(TextUtil.parse("<gray>Playback speed: <white>" + playbackSpeed + "x"));
    }

    public void handleHotbarClick(int slot) {
        switch (slot) {
            case SLOT_REWIND_10 -> skip(-10);
            case SLOT_REWIND_5 -> skip(-5);
            case SLOT_REWIND_1 -> skip(-1);
            case SLOT_PAUSE_PLAY -> togglePause();
            case SLOT_STOP -> stop();
            case SLOT_FF_1 -> skip(1);
            case SLOT_FF_5 -> skip(5);
            case SLOT_FF_10 -> skip(10);
            case SLOT_SPEED -> cycleSpeed();
        }
    }

    private void tick() {
        if (!playing) return;

        // Update playback time
        if (!paused) {
            long realTimeDelta = 50; // ~50ms per tick
            currentPlaybackTime += (long) (realTimeDelta * playbackSpeed);

            // Check if reached end
            if (currentPlaybackTime >= session.getEndTime()) {
                viewer.sendMessage(TextUtil.parse("<gray>Replay ended."));
                stop();
                return;
            }
        }

        // Update NPCs
        updateNpcs();

        // Process block logs (place, break, explosions)
        processBlockLogs();

        // Show action logs
        displayActionLogs();

        // Update action bar with time info
        updateActionBar();
    }

    private void updateNpcs() {
        if (!plugin.getHookManager().hasCitizens()) return;

        CitizensHook citizens = plugin.getHookManager().getCitizensHook();

        for (Map.Entry<UUID, UUID> entry : npcIds.entrySet()) {
            UUID playerUuid = entry.getKey();
            UUID npcId = entry.getValue();

            // Check if NPC is still valid
            if (!citizens.isNpcSpawned(npcId)) {
                plugin.logDebug("[Replay] NPC " + npcId + " is no longer spawned");
                continue;
            }

            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);
            ReplaySnapshot targetSnapshot = findClosestSnapshot(snapshots, currentPlaybackTime);

            if (targetSnapshot != null) {
                World world = Bukkit.getWorld(targetSnapshot.getWorldName());
                if (world != null) {
                    Location loc = targetSnapshot.toLocation(world);
                    citizens.updateNpcLocation(npcId, loc);
                    citizens.setNpcSneaking(npcId, targetSnapshot.isSneaking());
                    citizens.setNpcGliding(npcId, targetSnapshot.isGliding());
                    citizens.setNpcSwimming(npcId, targetSnapshot.isSwimming());
                    citizens.setNpcHeldItem(npcId, targetSnapshot.getMainHand());
                    citizens.setNpcOffHandItem(npcId, targetSnapshot.getOffHand());
                    citizens.setNpcArmor(npcId, targetSnapshot.getArmor());

                    // Handle projectile actions - spawn visual projectiles
                    ReplaySnapshot.ActionType action = targetSnapshot.getAction();
                    if (isProjectileAction(action) && !processedProjectileTimestamps.contains(targetSnapshot.getTimestamp())) {
                        processedProjectileTimestamps.add(targetSnapshot.getTimestamp());
                        citizens.spawnVisualProjectile(npcId, action.name());
                    }
                }
            }
        }
    }

    /**
     * Check if an action type is a projectile action.
     */
    private boolean isProjectileAction(ReplaySnapshot.ActionType action) {
        return switch (action) {
            case BOW_SHOOT, CROSSBOW_SHOOT, TRIDENT_THROW, ENDER_PEARL_THROW,
                 EGG_THROW, SNOWBALL_THROW, WIND_CHARGE_THROW -> true;
            default -> false;
        };
    }

    private ReplaySnapshot findClosestSnapshot(List<ReplaySnapshot> snapshots, long targetTime) {
        ReplaySnapshot closest = null;
        long closestDiff = Long.MAX_VALUE;

        for (ReplaySnapshot snapshot : snapshots) {
            long diff = Math.abs(snapshot.getTimestamp() - targetTime);
            if (diff < closestDiff) {
                closestDiff = diff;
                closest = snapshot;
            }
            // Early exit if we've passed the target time
            if (snapshot.getTimestamp() > targetTime && closest != null) {
                break;
            }
        }

        return closest;
    }

    /**
     * Process block logs (place, break, explosions, etc.) based on current playback time.
     * Block logs are processed separately from snapshots because they have precise timestamps.
     */
    private void processBlockLogs() {
        if (blockLogs.isEmpty()) return;

        FakeBlockManager fakeBlockManager = plugin.getFakeBlockManager();
        if (fakeBlockManager == null) return;

        for (int i = lastBlockLogIndex; i < blockLogs.size(); i++) {
            BlockLogEntry entry = blockLogs.get(i);

            // Skip if we haven't reached this point yet
            if (entry.getTimestamp() > currentPlaybackTime) {
                break;
            }

            // Process this block log entry
            processBlockLogEntry(entry);

            // Update last index
            lastBlockLogIndex = i + 1;
        }
    }

    /**
     * Process a single block log entry for playback visualization.
     */
    private void processBlockLogEntry(BlockLogEntry entry) {
        FakeBlockManager fakeBlockManager = plugin.getFakeBlockManager();
        if (fakeBlockManager == null) return;

        World world = Bukkit.getWorld(entry.getWorldName());
        if (world == null) return;

        Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());

        if (usingPhysicalBlocks) {
            // Physical block handling
            switch (entry.getAction()) {
                case PLACE -> {
                    fakeBlockManager.physicallyPlaceBlock(viewer, loc, entry.getNewMaterial(), entry.getNewBlockData());
                    if (plugin.getConfigManager().getSettings().isReplaySoundsEnabled()) {
                        ReplaySoundManager.playBlockPlaceSound(viewer, loc, entry.getNewMaterial());
                    }
                }
                case BREAK, EXPLOSION -> {
                    fakeBlockManager.physicallyBreakBlock(viewer, loc);
                    if (plugin.getConfigManager().getSettings().isReplaySoundsEnabled()) {
                        ReplaySoundManager.playBlockBreakSound(viewer, loc, entry.getOldMaterial());
                    }
                }
                case PISTON_EXTEND, PISTON_RETRACT -> {
                    fakeBlockManager.physicallyPlaceBlock(viewer, loc, entry.getNewMaterial(), entry.getNewBlockData());
                }
                default -> {}
            }
        } else if (plugin.getConfigManager().getSettings().isReplayFakeBlocksEnabled()) {
            // Fake block handling (client-side only)
            switch (entry.getAction()) {
                case PLACE -> {
                    fakeBlockManager.revealBlock(viewer, loc);
                    if (plugin.getConfigManager().getSettings().isReplaySoundsEnabled()) {
                        ReplaySoundManager.playBlockPlaceSound(viewer, loc, entry.getNewMaterial());
                    }
                }
                case BREAK, EXPLOSION -> {
                    fakeBlockManager.hideBlockAsAir(viewer, loc);
                    if (plugin.getConfigManager().getSettings().isReplaySoundsEnabled()) {
                        ReplaySoundManager.playBlockBreakSound(viewer, loc, entry.getOldMaterial());
                    }
                }
                case PISTON_EXTEND, PISTON_RETRACT -> {
                    // For pistons, we reveal the new state
                    fakeBlockManager.revealBlock(viewer, loc);
                }
                default -> {}
            }
        }

        // Show log message for explosions (they're notable events)
        if (entry.getAction() == BlockLogEntry.Action.EXPLOSION) {
            String playerName = entry.getPlayerName() != null ? entry.getPlayerName() : "Unknown";
            long offsetMs = entry.getTimestamp() - session.getStartTime();
            String timeStr = formatDuration(offsetMs);

            if (entry.getPlayerUuid() != null) {
                viewer.sendMessage(TextUtil.parse(
                        "<gray>[" + timeStr + "] <red>" + playerName + " <gray>caused explosion at <white>" +
                        entry.getX() + ", " + entry.getY() + ", " + entry.getZ()));
            }
        }
    }

    /**
     * Display action logs to the viewer as they occur in the replay timeline.
     * Shows player actions like commands, chat, inventory, swinging, etc.
     */
    private void displayActionLogs() {
        for (UUID playerUuid : session.getRecordedPlayerUuids()) {
            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);
            String playerName = session.getPlayerName(playerUuid);

            int lastIdx = lastActionIndex.getOrDefault(playerUuid, 0);

            // Find actions that occurred between last check and now
            for (int i = lastIdx; i < snapshots.size(); i++) {
                ReplaySnapshot snap = snapshots.get(i);

                // Skip if we haven't reached this point yet
                if (snap.getTimestamp() > currentPlaybackTime) {
                    break;
                }

                // Display action if it has one
                // Note: Block actions (PLACE_BLOCK, BREAK_BLOCK) are handled by processBlockLogs()
                // based on BlockLogEntry timestamps for more accurate playback
                if (snap.getAction() != ReplaySnapshot.ActionType.NONE &&
                    snap.getAction() != ReplaySnapshot.ActionType.PLACE_BLOCK &&
                    snap.getAction() != ReplaySnapshot.ActionType.BREAK_BLOCK) {
                    displayAction(playerName, snap, playerUuid);
                }

                // Update last index
                lastActionIndex.put(playerUuid, i + 1);
            }
        }
    }

    /**
     * Handle block actions for block visualization (fake or physical).
     */
    private void handleBlockAction(ReplaySnapshot snap) {
        FakeBlockManager fakeBlockManager = plugin.getFakeBlockManager();
        if (fakeBlockManager == null) return;

        World world = Bukkit.getWorld(snap.getWorldName());
        if (world == null) return;

        // Parse the block location from action data (format: "MATERIAL at x, y, z")
        Location blockLoc = parseBlockLocationFromData(snap.getActionData(), world);
        if (blockLoc == null) {
            // Fallback to player location if we can't parse
            blockLoc = snap.toLocation(world);
        }

        String actionData = snap.getActionData();

        if (usingPhysicalBlocks) {
            // Physical block handling - actually modify the world
            if (snap.getAction() == ReplaySnapshot.ActionType.PLACE_BLOCK) {
                // Parse material and block data from action data
                Material material = parseMaterialFromData(actionData);
                String blockData = parseBlockDataFromLogs(blockLoc);
                fakeBlockManager.physicallyPlaceBlock(viewer, blockLoc, material, blockData);
            } else if (snap.getAction() == ReplaySnapshot.ActionType.BREAK_BLOCK) {
                fakeBlockManager.physicallyBreakBlock(viewer, blockLoc);
            }
        } else {
            // Fake block handling - client-side only
            if (snap.getAction() == ReplaySnapshot.ActionType.PLACE_BLOCK) {
                // Reveal the block when it's "placed" in the timeline
                fakeBlockManager.revealBlock(viewer, blockLoc);
            } else if (snap.getAction() == ReplaySnapshot.ActionType.BREAK_BLOCK) {
                // Hide the block (show as air) when it's "broken" in the timeline
                fakeBlockManager.hideBlockAsAir(viewer, blockLoc);
            }
        }
    }

    /**
     * Parse block location from action data.
     * Format: "MATERIAL at x, y, z" or "MATERIAL_NAME at x, y, z"
     */
    private Location parseBlockLocationFromData(String data, World world) {
        if (data == null || !data.contains(" at ")) {
            return null;
        }
        try {
            String[] parts = data.split(" at ");
            if (parts.length < 2) return null;

            String[] coords = parts[1].split(", ");
            if (coords.length < 3) return null;

            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            int z = Integer.parseInt(coords[2].trim());

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse block data string from block logs for a specific location.
     */
    private String parseBlockDataFromLogs(Location loc) {
        String locationKey = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        BlockLogEntry entry = blockLogByLocation.get(locationKey);
        if (entry != null) {
            return entry.getNewBlockData();
        }
        return null;
    }

    /**
     * Format and display an action to the viewer.
     */
    private void displayAction(String playerName, ReplaySnapshot snapshot, UUID playerUuid) {
        ReplaySnapshot.ActionType action = snapshot.getAction();
        String data = snapshot.getActionData();

        // Format time offset
        long offsetMs = snapshot.getTimestamp() - session.getStartTime();
        String timeStr = formatDuration(offsetMs);

        // Get NPC location for sounds
        Location actionLoc = null;
        World world = Bukkit.getWorld(snapshot.getWorldName());
        if (world != null) {
            actionLoc = snapshot.toLocation(world);
        }

        // Handle SWING_ARM - play animation but don't show text
        if (action == ReplaySnapshot.ActionType.SWING_ARM) {
            if (plugin.getConfigManager().getSettings().isReplayAnimationsEnabled()) {
                UUID npcId = npcIds.get(playerUuid);
                if (npcId != null && plugin.getHookManager().hasCitizens()) {
                    plugin.getHookManager().getCitizensHook().playNpcArmSwing(npcId);
                }
            }
            return;
        }

        // Play sounds for certain actions
        if (plugin.getConfigManager().getSettings().isReplaySoundsEnabled() && actionLoc != null) {
            switch (action) {
                case ATTACK -> ReplaySoundManager.playAttackSound(viewer, actionLoc);
                case DAMAGE_RECEIVED -> {
                    // Play player hurt sound for the victim
                    ReplaySoundManager.playEntityHurtSound(viewer, actionLoc, "PLAYER");
                }
                case DAMAGE_DEALT -> {
                    // Play hit sound based on hit type from action data
                    String hitType = parseHitTypeFromData(data);
                    ReplaySoundManager.playHitSound(viewer, actionLoc, hitType);
                }
                case ITEM_PICKUP -> ReplaySoundManager.playPickupSound(viewer, actionLoc);
                case DROP_ITEM -> ReplaySoundManager.playDropSound(viewer, actionLoc);
                case CONSUME_ITEM -> ReplaySoundManager.playConsumeSound(viewer, actionLoc);
                case BOW_SHOOT -> ReplaySoundManager.playBowSound(viewer, actionLoc);
                case CROSSBOW_SHOOT -> ReplaySoundManager.playCrossbowSound(viewer, actionLoc);
                case DEATH -> ReplaySoundManager.playDeathSound(viewer, actionLoc);
                case TELEPORT -> ReplaySoundManager.playTeleportSound(viewer, actionLoc);
                case PORTAL_ENTER -> ReplaySoundManager.playPortalSound(viewer, actionLoc);
                case FISH_CAST -> ReplaySoundManager.playFishCastSound(viewer, actionLoc);
                case FISH_REEL -> ReplaySoundManager.playFishReelSound(viewer, actionLoc);
                case PLACE_BLOCK -> {
                    Material mat = parseMaterialFromData(data);
                    ReplaySoundManager.playBlockPlaceSound(viewer, actionLoc, mat);
                }
                case BREAK_BLOCK -> {
                    Material mat = parseMaterialFromData(data);
                    ReplaySoundManager.playBlockBreakSound(viewer, actionLoc, mat);
                }
                case SPEAR_JAB -> ReplaySoundManager.playSpearJabSound(viewer, actionLoc);
                case SPEAR_CHARGE -> ReplaySoundManager.playSpearChargeSound(viewer, actionLoc);
                case ARMOR_EQUIP -> ReplaySoundManager.playArmorEquipSound(viewer, actionLoc, data);
                default -> {}
            }
        }

        // Build message based on action type
        String actionText = switch (action) {
            case COMMAND -> "<gray>[" + timeStr + "] <yellow>" + playerName + " <gray>ran command: <white>" + data;
            case CHAT -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>said: <white>" + data;
            case ATTACK -> "<gray>[" + timeStr + "] <red>" + playerName + " <gray>" + (data != null ? data : "attacked");
            case DAMAGE_RECEIVED -> "<gray>[" + timeStr + "] <gold>" + playerName + " <gray>" + (data != null ? data : "took damage");
            case DAMAGE_DEALT -> "<gray>[" + timeStr + "] <red>" + playerName + " <gray>dealt damage" + (data != null ? ": " + data : "");
            case SNEAK_START -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>started sneaking";
            case SNEAK_END -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>stopped sneaking";
            case SPRINT_START -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>started sprinting";
            case SPRINT_END -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>stopped sprinting";
            case INVENTORY_OPEN -> "<gray>[" + timeStr + "] <light_purple>" + playerName + " <gray>opened: <white>" + (data != null ? data : "inventory");
            case INVENTORY_CLOSE -> "<gray>[" + timeStr + "] <light_purple>" + playerName + " <gray>closed inventory";
            case ITEM_PICKUP -> "<gray>[" + timeStr + "] <green>" + playerName + " <gray>picked up: <white>" + data;
            case DROP_ITEM -> "<gray>[" + timeStr + "] <yellow>" + playerName + " <gray>dropped: <white>" + data;
            case ITEM_USE -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>used: <white>" + data;
            case CONSUME_ITEM -> "<gray>[" + timeStr + "] <green>" + playerName + " <gray>consumed: <white>" + data;
            case BOW_SHOOT -> "<gray>[" + timeStr + "] <red>" + playerName + " <gray>shot: <white>" + (data != null ? data : "bow");
            case DEATH -> "<gray>[" + timeStr + "] <dark_red>" + playerName + " <gray>died" + (data != null ? ": " + data : "");
            case RESPAWN -> "<gray>[" + timeStr + "] <green>" + playerName + " <gray>respawned at " + data;
            case TELEPORT -> "<gray>[" + timeStr + "] <light_purple>" + playerName + " <gray>teleported" + (data != null ? " (" + data + ")" : "");
            case PORTAL_ENTER -> "<gray>[" + timeStr + "] <dark_purple>" + playerName + " <gray>entered portal: " + data;
            case PLACE_BLOCK -> "<gray>[" + timeStr + "] <green>" + playerName + " <gray>placed: <white>" + data;
            case BREAK_BLOCK -> "<gray>[" + timeStr + "] <yellow>" + playerName + " <gray>broke: <white>" + data;
            case FISH_CAST -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>cast fishing rod";
            case FISH_REEL -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>" + (data != null ? data : "reeled in");
            // 1.21.11+ Spear actions
            case SPEAR_JAB -> "<gray>[" + timeStr + "] <gold>" + playerName + " <gray>jabbed with: <white>" + (data != null ? data : "spear");
            case SPEAR_CHARGE -> "<gray>[" + timeStr + "] <gold>" + playerName + " <gray>charged with: <white>" + (data != null ? data : "spear");
            // Equipment changes
            case ARMOR_EQUIP -> "<gray>[" + timeStr + "] <blue>" + playerName + " <gray>equipped: <white>" + data;
            case OFFHAND_SWAP -> "<gray>[" + timeStr + "] <light_purple>" + playerName + " <gray>swapped hands: <white>" + data;
            // Crossbow
            case CROSSBOW_SHOOT -> "<gray>[" + timeStr + "] <red>" + playerName + " <gray>fired: <white>" + (data != null ? data : "crossbow");
            // Projectiles
            case WIND_CHARGE_THROW -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>threw wind charge";
            case ENDER_PEARL_THROW -> "<gray>[" + timeStr + "] <dark_purple>" + playerName + " <gray>threw ender pearl";
            case TRIDENT_THROW -> "<gray>[" + timeStr + "] <blue>" + playerName + " <gray>threw trident";
            case EGG_THROW -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>threw egg";
            case SNOWBALL_THROW -> "<gray>[" + timeStr + "] <white>" + playerName + " <gray>threw snowball";
            case POTION_THROW -> "<gray>[" + timeStr + "] <light_purple>" + playerName + " <gray>threw potion";
            case FIREWORK_USE -> "<gray>[" + timeStr + "] <gold>" + playerName + " <gray>used firework";
            case RIPTIDE_USE -> "<gray>[" + timeStr + "] <blue>" + playerName + " <gray>used riptide";
            case SHIELD_BLOCK_START, SHIELD_BLOCK_END, SHIELD_BLOCK -> "<gray>[" + timeStr + "] <aqua>" + playerName + " <gray>" + (data != null ? data : "blocked");
            // Entity interactions
            case ENTITY_SPAWN -> "<gray>[" + timeStr + "] <green>Entity spawned: <white>" + (data != null ? data : "unknown");
            case ENTITY_KILL -> "<gray>[" + timeStr + "] <red>" + playerName + " <gray>" + (data != null ? data : "killed entity");
            case ENTITY_INTERACT -> "<gray>[" + timeStr + "] <yellow>" + playerName + " <gray>interacted with: <white>" + (data != null ? data : "entity");
            case ENTITY_MOUNT -> "<gray>[" + timeStr + "] <green>" + playerName + " <gray>" + (data != null ? data : "mounted entity");
            case ENTITY_DISMOUNT -> "<gray>[" + timeStr + "] <yellow>" + playerName + " <gray>" + (data != null ? data : "dismounted");
            default -> null;
        };

        if (actionText != null) {
            viewer.sendMessage(TextUtil.parse(actionText));
        }
    }

    /**
     * Parse material name from action data.
     */
    private Material parseMaterialFromData(String data) {
        if (data == null || data.isEmpty()) {
            return Material.STONE;
        }
        try {
            // Data format is usually "MATERIAL_NAME" or "MATERIAL_NAME at x,y,z"
            String materialName = data.split(" ")[0];
            Material mat = Material.matchMaterial(materialName);
            return mat != null ? mat : Material.STONE;
        } catch (Exception e) {
            return Material.STONE;
        }
    }

    /**
     * Parse hit type from damage action data.
     * Data format: "hit PlayerName for X.X damage (HIT_TYPE)" or "took X.X damage from PlayerName (HIT_TYPE)"
     */
    private String parseHitTypeFromData(String data) {
        if (data == null || data.isEmpty()) {
            return "STRONG";
        }
        try {
            // Look for hit type in parentheses at the end
            int lastParen = data.lastIndexOf('(');
            int closeParen = data.lastIndexOf(')');
            if (lastParen != -1 && closeParen > lastParen) {
                String hitType = data.substring(lastParen + 1, closeParen);
                // Handle projectile hits
                if (hitType.startsWith("PROJECTILE:")) {
                    return "STRONG"; // Use strong attack sound for projectiles
                }
                // Handle combined types like CRIT_KNOCKBACK
                if (hitType.contains("_")) {
                    // Return the primary hit type (first part)
                    return hitType.split("_")[0];
                }
                return hitType;
            }
        } catch (Exception ignored) {
            // Failed to parse, use default
        }
        return "STRONG";
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void backupViewerState() {
        originalLocation = viewer.getLocation().clone();
        originalGameMode = viewer.getGameMode();

        // Deep clone inventory contents to avoid reference issues
        ItemStack[] contents = viewer.getInventory().getContents();
        originalInventory = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                originalInventory[i] = contents[i].clone();
            }
        }

        // Deep clone armor
        ItemStack[] armor = viewer.getInventory().getArmorContents();
        originalArmor = new ItemStack[armor.length];
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null) {
                originalArmor[i] = armor[i].clone();
            }
        }

        // Backup off-hand and held slot
        ItemStack offHand = viewer.getInventory().getItemInOffHand();
        originalOffHand = offHand != null ? offHand.clone() : null;
        originalHeldSlot = viewer.getInventory().getHeldItemSlot();

        wasAllowFlight = viewer.getAllowFlight();
        wasFlying = viewer.isFlying();
        originalEffects = new ArrayList<>(viewer.getActivePotionEffects());
    }

    private void restoreViewerState() {
        // Clear effects
        for (PotionEffect effect : viewer.getActivePotionEffects()) {
            viewer.removePotionEffect(effect.getType());
        }

        // Restore location first
        viewer.teleport(originalLocation);

        // Restore gamemode
        viewer.setGameMode(originalGameMode);

        // Restore flight state
        viewer.setAllowFlight(wasAllowFlight);
        viewer.setFlying(wasFlying);

        // Restore effects
        for (PotionEffect effect : originalEffects) {
            viewer.addPotionEffect(effect);
        }

        // Make visible again
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            online.showPlayer(plugin, viewer);
            viewer.showPlayer(plugin, online);
        }

        // Restore inventory synchronously, then update multiple times to ensure sync
        restoreInventory();

        // Schedule additional inventory syncs to ensure client is updated
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (viewer.isOnline()) {
                restoreInventory();
                viewer.updateInventory();
            }
        }, 2L);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (viewer.isOnline()) {
                viewer.updateInventory();
            }
        }, 5L);
    }

    /**
     * Restore the viewer's inventory to its original state.
     */
    private void restoreInventory() {
        // Clear inventory completely
        viewer.getInventory().clear();

        // Restore held slot first
        viewer.getInventory().setHeldItemSlot(originalHeldSlot);

        // Restore main inventory contents
        if (originalInventory != null) {
            for (int i = 0; i < originalInventory.length && i < viewer.getInventory().getSize(); i++) {
                ItemStack item = originalInventory[i];
                if (item != null) {
                    viewer.getInventory().setItem(i, item.clone());
                }
            }
        }

        // Restore armor
        if (originalArmor != null) {
            ItemStack[] armorClone = new ItemStack[4];
            for (int i = 0; i < Math.min(originalArmor.length, 4); i++) {
                if (originalArmor[i] != null) {
                    armorClone[i] = originalArmor[i].clone();
                }
            }
            viewer.getInventory().setArmorContents(armorClone);
        }

        // Restore off-hand
        viewer.getInventory().setItemInOffHand(originalOffHand != null ? originalOffHand.clone() : null);

        // Force inventory update
        viewer.updateInventory();
    }

    private void setupViewer() {
        // Set spectator-like mode
        viewer.setGameMode(GameMode.ADVENTURE);
        viewer.setAllowFlight(true);
        viewer.setFlying(true);

        // Add invisibility
        viewer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        // Hide from other players
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (!online.equals(viewer)) {
                online.hidePlayer(plugin, viewer);
                viewer.hidePlayer(plugin, online);
            }
        }

        // Setup control hotbar
        setupHotbar();

        // Teleport to replay location
        List<ReplaySnapshot> primarySnapshots = session.getSnapshots(session.getPrimaryPlayerUuid());
        if (!primarySnapshots.isEmpty()) {
            ReplaySnapshot first = primarySnapshots.get(0);
            World world = Bukkit.getWorld(first.getWorldName());
            if (world != null) {
                viewer.teleport(first.toLocation(world).add(0, 5, 0));
            }
        }
    }

    private void setupHotbar() {
        viewer.getInventory().clear();

        // Rewind buttons
        viewer.getInventory().setItem(SLOT_REWIND_10, createControlItem(
                Material.RED_DYE, "<red>Rewind 10s", "<<< 10s"));
        viewer.getInventory().setItem(SLOT_REWIND_5, createControlItem(
                Material.ORANGE_DYE, "<gold>Rewind 5s", "<< 5s"));
        viewer.getInventory().setItem(SLOT_REWIND_1, createControlItem(
                Material.YELLOW_DYE, "<yellow>Rewind 1s", "< 1s"));

        // Pause/Play
        updatePausePlayButton();

        // Stop
        viewer.getInventory().setItem(SLOT_STOP, createControlItem(
                Material.BARRIER, "<dark_red>Stop Replay", "Exit playback"));

        // Fast forward buttons
        viewer.getInventory().setItem(SLOT_FF_1, createControlItem(
                Material.LIME_DYE, "<green>Forward 1s", "1s >"));
        viewer.getInventory().setItem(SLOT_FF_5, createControlItem(
                Material.GREEN_DYE, "<dark_green>Forward 5s", "5s >>"));
        viewer.getInventory().setItem(SLOT_FF_10, createControlItem(
                Material.CYAN_DYE, "<aqua>Forward 10s", "10s >>>"));

        // Speed control
        updateSpeedButton();
    }

    private void updateHotbar() {
        updatePausePlayButton();
        updateSpeedButton();
    }

    private void updatePausePlayButton() {
        if (paused) {
            viewer.getInventory().setItem(SLOT_PAUSE_PLAY, createControlItem(
                    Material.SLIME_BALL, "<green>Play", "Resume playback"));
        } else {
            viewer.getInventory().setItem(SLOT_PAUSE_PLAY, createControlItem(
                    Material.MAGMA_CREAM, "<gold>Pause", "Pause playback"));
        }
    }

    private void updateSpeedButton() {
        String speedText = playbackSpeed + "x";
        viewer.getInventory().setItem(SLOT_SPEED, createControlItem(
                Material.CLOCK, "<light_purple>Speed: " + speedText, "Click to change speed"));
    }

    private ItemStack createControlItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TextUtil.parse(name));
        meta.lore(List.of(TextUtil.parse("<gray>" + description)));
        item.setItemMeta(meta);
        return item;
    }

    private void updateActionBar() {
        long elapsed = currentPlaybackTime - session.getStartTime();
        long total = session.getDuration();

        String elapsedStr = formatTime(elapsed);
        String totalStr = formatTime(total);
        float percent = (float) elapsed / total * 100;

        String progressBar = createProgressBar(percent, 20);

        viewer.sendActionBar(TextUtil.parse(String.format(
                "<gray>%s <dark_gray>/ <gray>%s  %s  <white>%.0f%%  <gray>(%s)",
                elapsedStr, totalStr, progressBar, percent,
                paused ? "<yellow>PAUSED" : "<green>" + playbackSpeed + "x")));
    }

    private String formatTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String createProgressBar(float percent, int length) {
        int filled = (int) (percent / 100 * length);
        StringBuilder bar = new StringBuilder("<dark_gray>[");
        for (int i = 0; i < length; i++) {
            if (i < filled) {
                bar.append("<green>|");
            } else {
                bar.append("<dark_gray>|");
            }
        }
        bar.append("<dark_gray>]");
        return bar.toString();
    }

    private void showIntro() {
        viewer.showTitle(Title.title(
                TextUtil.parse("<gradient:#a855f7:#ec4899>Replay Playback</gradient>"),
                TextUtil.parse("<gray>" + session.getPrimaryPlayerName() + " - " + session.getReason().name()),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
        ));
    }

    /**
     * Load block logs from the session.
     */
    private void loadBlockLogs() {
        try {
            java.nio.file.Path sessionDir = plugin.getReplayManager().getReplaysDirectory()
                    .resolve(session.getSessionId());

            blockLogs = plugin.getBlockLogManager()
                    .loadSessionLogs(session.getSessionId(), sessionDir);

            // Build location map for quick lookup
            for (BlockLogEntry entry : blockLogs) {
                blockLogByLocation.put(entry.getLocationKey(), entry);
            }

            plugin.logDebug("[Replay] Loaded " + blockLogs.size() + " block logs for playback");
        } catch (Exception e) {
            plugin.logDebug("[Replay] Failed to load block logs: " + e.getMessage());
            blockLogs = new ArrayList<>();
        }
    }

    /**
     * Initialize fake blocks for the replay.
     * Loads block logs and hides placed blocks so they appear as the timeline progresses.
     */
    private void initializeFakeBlocks() {
        if (!blockLogs.isEmpty()) {
            plugin.getFakeBlockManager().initializeForViewer(viewer, blockLogs);
            plugin.logDebug("[Replay] Initialized " + blockLogs.size() + " fake blocks for playback");
        }
    }

    /**
     * Teleport nearby players away from the replay area for safety.
     * Players must not be watching a replay and must not have the bypass permission.
     */
    private void teleportNearbyPlayersAway() {
        int safetyRadius = plugin.getConfigManager().getSettings().getReplayPlayerSafetyRadius();
        if (safetyRadius <= 0) return;

        // Get the center location from first snapshot
        Location centerLocation = null;
        for (UUID playerUuid : session.getRecordedPlayerUuids()) {
            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);
            if (!snapshots.isEmpty()) {
                ReplaySnapshot first = snapshots.get(0);
                World world = Bukkit.getWorld(first.getWorldName());
                if (world != null) {
                    centerLocation = first.toLocation(world);
                    break;
                }
            }
        }

        if (centerLocation == null) return;

        final Location center = centerLocation;
        World world = center.getWorld();

        for (Player player : world.getPlayers()) {
            // Skip the viewer
            if (player.equals(viewer)) continue;

            // Skip players already viewing a replay
            if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) continue;

            // Skip players with bypass permission
            if (player.hasPermission("moderex.replay.bypass-teleport-safety")) continue;

            // Check if within safety radius
            if (player.getLocation().distance(center) <= safetyRadius) {
                // Store original location for restoration
                teleportedPlayersOriginalLocations.put(player.getUniqueId(), player.getLocation().clone());

                // Find a safe location outside the replay area
                Location safeLoc = findSafeLocationOutsideRadius(center, safetyRadius + 10);
                player.teleport(safeLoc);

                player.sendMessage(TextUtil.parse(
                        "<yellow>You have been teleported away from a replay area. " +
                        "<gray>You will be returned when the replay ends."));

                plugin.logDebug("[Replay] Teleported " + player.getName() + " away from replay area");
            }
        }
    }

    /**
     * Find a safe location outside the given radius from center.
     */
    private Location findSafeLocationOutsideRadius(Location center, double radius) {
        // Try to find a safe spot by moving in the +X direction
        Location safeLoc = center.clone().add(radius, 0, 0);

        // Find ground level
        World world = safeLoc.getWorld();
        int x = safeLoc.getBlockX();
        int z = safeLoc.getBlockZ();

        // Search for solid ground
        for (int y = safeLoc.getBlockY() + 10; y > world.getMinHeight(); y--) {
            if (world.getBlockAt(x, y, z).getType().isSolid() &&
                world.getBlockAt(x, y + 1, z).getType().isAir() &&
                world.getBlockAt(x, y + 2, z).getType().isAir()) {
                return new Location(world, x + 0.5, y + 1, z + 0.5);
            }
        }

        // Fallback to spawn
        return world.getSpawnLocation();
    }

    /**
     * Restore teleported players to their original locations.
     */
    private void restoreTeleportedPlayers() {
        for (Map.Entry<UUID, Location> entry : teleportedPlayersOriginalLocations.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                player.teleport(entry.getValue());
                player.sendMessage(TextUtil.parse(
                        "<green>The replay has ended. You have been returned to your original location."));
            }
        }
        teleportedPlayersOriginalLocations.clear();
    }

    // Getters
    public boolean isPlaying() { return playing; }
    public boolean isPaused() { return paused; }
    public long getCurrentPlaybackTime() { return currentPlaybackTime; }
    public float getPlaybackSpeed() { return playbackSpeed; }
    public ReplaySession getSession() { return session; }
}

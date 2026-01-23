package com.blockforge.moderex.replay;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.CitizensHook;
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
    private boolean wasFlying;
    private Collection<PotionEffect> originalEffects;

    // Citizens NPCs - Player UUID -> Citizens NPC UUID
    private final Map<UUID, UUID> npcIds = new HashMap<>();
    private BukkitTask playbackTask;

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

        // Restore viewer state
        restoreViewerState();

        viewer.sendMessage(TextUtil.parse("<gray>Replay playback ended."));
        plugin.logDebug("Stopped playback for " + viewer.getName());
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

        // Update all NPCs to new positions
        updateNpcs();

        String direction = seconds > 0 ? "forward" : "back";
        viewer.sendMessage(TextUtil.parse("<gray>Skipped " + direction + " <white>" +
                Math.abs(seconds) + "s<gray>."));
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

        // Update action bar with time info
        updateActionBar();
    }

    private void updateNpcs() {
        if (!plugin.getHookManager().hasCitizens()) return;

        CitizensHook citizens = plugin.getHookManager().getCitizensHook();

        for (Map.Entry<UUID, UUID> entry : npcIds.entrySet()) {
            UUID playerUuid = entry.getKey();
            UUID npcId = entry.getValue();

            List<ReplaySnapshot> snapshots = session.getSnapshots(playerUuid);
            ReplaySnapshot targetSnapshot = findClosestSnapshot(snapshots, currentPlaybackTime);

            if (targetSnapshot != null) {
                World world = Bukkit.getWorld(targetSnapshot.getWorldName());
                if (world != null) {
                    Location loc = targetSnapshot.toLocation(world);
                    citizens.updateNpcLocation(npcId, loc);
                    citizens.setNpcSneaking(npcId, targetSnapshot.isSneaking());
                    citizens.setNpcHeldItem(npcId, targetSnapshot.getMainHand());
                }
            }
        }
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

    private void backupViewerState() {
        originalLocation = viewer.getLocation().clone();
        originalGameMode = viewer.getGameMode();
        originalInventory = viewer.getInventory().getContents().clone();
        originalArmor = viewer.getInventory().getArmorContents().clone();
        wasFlying = viewer.isFlying();
        originalEffects = new ArrayList<>(viewer.getActivePotionEffects());
    }

    private void restoreViewerState() {
        // Clear effects
        for (PotionEffect effect : viewer.getActivePotionEffects()) {
            viewer.removePotionEffect(effect.getType());
        }

        // Restore state
        viewer.teleport(originalLocation);
        viewer.setGameMode(originalGameMode);
        viewer.getInventory().setContents(originalInventory);
        viewer.getInventory().setArmorContents(originalArmor);
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

    // Getters
    public boolean isPlaying() { return playing; }
    public boolean isPaused() { return paused; }
    public long getCurrentPlaybackTime() { return currentPlaybackTime; }
    public float getPlaybackSpeed() { return playbackSpeed; }
    public ReplaySession getSession() { return session; }
}

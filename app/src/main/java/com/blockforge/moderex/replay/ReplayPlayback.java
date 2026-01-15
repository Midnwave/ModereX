package com.blockforge.moderex.replay;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.*;

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

    // NPCs and visualization
    private final Map<UUID, FakePlayer> fakePlayersMap = new HashMap<>();
    private BukkitTask playbackTask;
    private Team hiddenTeam;

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

    public void start() {
        if (playing) return;

        // Backup viewer state
        backupViewerState();

        // Setup viewer for spectating
        setupViewer();

        // Create fake players for each recorded player
        for (UUID uuid : session.getRecordedPlayerUuids()) {
            String name = session.getPlayerName(uuid);
            FakePlayer fakePlayer = new FakePlayer(uuid, name);
            fakePlayersMap.put(uuid, fakePlayer);
        }

        // Initialize playback state
        playing = true;
        paused = false;
        playbackStartTime = System.currentTimeMillis();
        currentPlaybackTime = session.getStartTime();

        // Start playback task
        playbackTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);

        // Show intro
        showIntro();

        plugin.logDebug("Started playback of " + session.getSessionId() + " for " + viewer.getName());
    }

    public void stop() {
        if (!playing) return;

        playing = false;

        // Stop task
        if (playbackTask != null) {
            playbackTask.cancel();
            playbackTask = null;
        }

        // Remove fake players
        for (FakePlayer fakePlayer : fakePlayersMap.values()) {
            fakePlayer.remove();
        }
        fakePlayersMap.clear();

        // Cleanup team
        if (hiddenTeam != null) {
            hiddenTeam.unregister();
            hiddenTeam = null;
        }

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

        // Update all fake players to new positions
        updateFakePlayers();

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

        // Update fake players
        updateFakePlayers();

        // Update action bar with time info
        updateActionBar();
    }

    private void updateFakePlayers() {
        for (Map.Entry<UUID, FakePlayer> entry : fakePlayersMap.entrySet()) {
            UUID uuid = entry.getKey();
            FakePlayer fakePlayer = entry.getValue();

            // Find the snapshot closest to current time
            List<ReplaySnapshot> snapshots = session.getSnapshots(uuid);
            ReplaySnapshot targetSnapshot = findClosestSnapshot(snapshots, currentPlaybackTime);

            if (targetSnapshot != null) {
                fakePlayer.update(targetSnapshot, viewer.getWorld());
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

    private class FakePlayer {
        private final UUID originalUuid;
        private final String name;
        private org.bukkit.entity.ArmorStand entity;
        private ItemStack playerHead;
        private boolean headCreated = false;

        public FakePlayer(UUID originalUuid, String name) {
            this.originalUuid = originalUuid;
            this.name = name;
            createPlayerHead();
        }

        private void createPlayerHead() {
            // Create player head with skin
            playerHead = new ItemStack(Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();
            if (meta != null) {
                // Try to get the offline player for skin
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(originalUuid);
                meta.setOwningPlayer(offlinePlayer);
                playerHead.setItemMeta(meta);
            }
            headCreated = true;
        }

        public void update(ReplaySnapshot snapshot, World world) {
            Location loc = new Location(world, snapshot.getX(), snapshot.getY(), snapshot.getZ(),
                    snapshot.getYaw(), snapshot.getPitch());

            if (entity == null || !entity.isValid()) {
                // Create armor stand with player appearance
                entity = world.spawn(loc, org.bukkit.entity.ArmorStand.class, stand -> {
                    stand.setCustomName(name);
                    stand.setCustomNameVisible(true);
                    stand.setGravity(false);
                    stand.setVisible(false); // Hide armor stand body
                    stand.setSmall(false);
                    stand.setArms(true);
                    stand.setBasePlate(false);
                    stand.setMarker(false);
                    stand.setInvulnerable(true);
                    stand.setPersistent(false);

                    // Set player head as helmet for skin display
                    var equipment = stand.getEquipment();
                    if (headCreated && playerHead != null) {
                        equipment.setHelmet(playerHead);
                    }

                    // Set armor from snapshot or use leather armor for visibility
                    if (snapshot.getArmor() != null && snapshot.getArmor().length >= 4) {
                        ItemStack[] armor = snapshot.getArmor();
                        // Don't override helmet (player head)
                        equipment.setChestplate(armor[2] != null ? armor[2] : createColoredArmor(Material.LEATHER_CHESTPLATE));
                        equipment.setLeggings(armor[1] != null ? armor[1] : createColoredArmor(Material.LEATHER_LEGGINGS));
                        equipment.setBoots(armor[0] != null ? armor[0] : createColoredArmor(Material.LEATHER_BOOTS));
                    } else {
                        // Default colored leather armor
                        equipment.setChestplate(createColoredArmor(Material.LEATHER_CHESTPLATE));
                        equipment.setLeggings(createColoredArmor(Material.LEATHER_LEGGINGS));
                        equipment.setBoots(createColoredArmor(Material.LEATHER_BOOTS));
                    }

                    if (snapshot.getMainHand() != null) {
                        equipment.setItemInMainHand(snapshot.getMainHand());
                    }
                });
            } else {
                // Update position smoothly
                entity.teleport(loc);

                // Update held item
                var equipment = entity.getEquipment();
                if (snapshot.getMainHand() != null) {
                    equipment.setItemInMainHand(snapshot.getMainHand());
                }
            }

            // Update poses based on state
            updatePose(snapshot);
        }

        private void updatePose(ReplaySnapshot snapshot) {
            if (entity == null) return;

            // Use EulerAngle for pose changes
            org.bukkit.util.EulerAngle headPose;
            org.bukkit.util.EulerAngle bodyPose;

            if (snapshot.isSneaking()) {
                // Sneaking pose - tilt forward
                headPose = new org.bukkit.util.EulerAngle(Math.toRadians(20), 0, 0);
                bodyPose = new org.bukkit.util.EulerAngle(Math.toRadians(15), 0, 0);
                entity.setSmall(false);
            } else if (snapshot.isSwimming()) {
                // Swimming pose - horizontal
                headPose = new org.bukkit.util.EulerAngle(Math.toRadians(80), 0, 0);
                bodyPose = new org.bukkit.util.EulerAngle(Math.toRadians(80), 0, 0);
            } else if (snapshot.isGliding()) {
                // Gliding pose - angled down
                headPose = new org.bukkit.util.EulerAngle(Math.toRadians(60), 0, 0);
                bodyPose = new org.bukkit.util.EulerAngle(Math.toRadians(45), 0, 0);
            } else {
                // Standing pose
                headPose = new org.bukkit.util.EulerAngle(0, 0, 0);
                bodyPose = new org.bukkit.util.EulerAngle(0, 0, 0);
            }

            entity.setHeadPose(headPose);
            entity.setBodyPose(bodyPose);

            // Arm animation for sprinting
            if (snapshot.isSprinting()) {
                double armSwing = Math.sin(System.currentTimeMillis() * 0.01) * 0.5;
                entity.setRightArmPose(new org.bukkit.util.EulerAngle(armSwing, 0, 0));
                entity.setLeftArmPose(new org.bukkit.util.EulerAngle(-armSwing, 0, 0));
            } else {
                entity.setRightArmPose(new org.bukkit.util.EulerAngle(0, 0, 0));
                entity.setLeftArmPose(new org.bukkit.util.EulerAngle(0, 0, 0));
            }
        }

        private ItemStack createColoredArmor(Material material) {
            ItemStack item = new ItemStack(material);
            if (item.getItemMeta() instanceof org.bukkit.inventory.meta.LeatherArmorMeta meta) {
                // Generate color based on player UUID for consistency
                int hash = originalUuid.hashCode();
                int r = (hash & 0xFF0000) >> 16;
                int g = (hash & 0x00FF00) >> 8;
                int b = hash & 0x0000FF;
                meta.setColor(org.bukkit.Color.fromRGB(r, g, b));
                item.setItemMeta(meta);
            }
            return item;
        }

        public void remove() {
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
        }
    }
}

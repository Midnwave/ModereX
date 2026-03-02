package com.blockforge.moderex.monitor;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

/**
 * TPS Throttle System - automatically engages performance throttles when TPS drops.
 * All actions are disabled by default - staff must configure each one via the web panel.
 */
public class TpsThrottleManager {

    private final ModereX plugin;
    private final Map<ThrottleAction, ThrottleConfig> configs = new EnumMap<>(ThrottleAction.class);
    private BukkitTask checkTask;
    private boolean manualOverride = false;

    public TpsThrottleManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Initialize all configs as disabled
        for (ThrottleAction action : ThrottleAction.values()) {
            configs.put(action, new ThrottleConfig(action));
        }

        // Check TPS every 2 seconds (40 ticks)
        checkTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::evaluateThrottles, 40L, 40L);
        plugin.getLogger().info("TPS throttle system started");
    }

    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        // Disengage all active throttles
        for (var entry : configs.entrySet()) {
            if (entry.getValue().isCurrentlyEngaged()) {
                disengage(entry.getKey(), entry.getValue());
            }
        }
    }

    private void evaluateThrottles() {
        if (manualOverride) return;

        double currentTps = plugin.getServerStatusManager().getCurrentTps();

        for (var entry : configs.entrySet()) {
            ThrottleConfig config = entry.getValue();
            if (!config.isEnabled()) continue;

            if (!config.isCurrentlyEngaged() && currentTps < config.getEngageTpsThreshold()) {
                // Check cooldown
                if (System.currentTimeMillis() - config.getLastEngagedAt() > config.getCooldownSeconds() * 1000L) {
                    engage(entry.getKey(), config, "auto");
                }
            } else if (config.isCurrentlyEngaged() && currentTps > config.getDisengageTpsThreshold()) {
                disengage(entry.getKey(), config);
            }
        }
    }

    private void engage(ThrottleAction action, ThrottleConfig config, String triggeredBy) {
        config.setCurrentlyEngaged(true);
        config.setLastEngagedAt(System.currentTimeMillis());

        // Apply the throttle action on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> applyAction(action, config, true));

        notifyStaff("<dark_gray>[<red>THROTTLE</red>]</dark_gray> <yellow>Engaged: " + action.getDisplayName() +
                " (TPS: " + String.format("%.1f", plugin.getServerStatusManager().getCurrentTps()) + ")");
        broadcastThrottleStatus();
        logThrottleEvent(action, true, triggeredBy);
    }

    private void disengage(ThrottleAction action, ThrottleConfig config) {
        config.setCurrentlyEngaged(false);

        // Revert the throttle action on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> applyAction(action, config, false));

        notifyStaff("<dark_gray>[<green>THROTTLE</green>]</dark_gray> <yellow>Disengaged: " + action.getDisplayName() +
                " (TPS: " + String.format("%.1f", plugin.getServerStatusManager().getCurrentTps()) + ")");
        broadcastThrottleStatus();
        logThrottleEvent(action, false, "auto");
    }

    private void applyAction(ThrottleAction action, ThrottleConfig config, boolean engage) {
        double intensity = config.getIntensity();

        switch (action) {
            case REDUCE_MOB_CAP -> {
                int newCap = engage ? (int)(70 * (1.0 - intensity)) : 70;
                for (World world : Bukkit.getWorlds()) {
                    world.setSpawnLimit(SpawnCategory.MONSTER, Math.max(1, newCap));
                }
            }
            case REDUCE_VIEW_DISTANCE -> {
                int newDist = engage ? Math.max(4, (int)(10 * (1.0 - intensity))) : 10;
                for (World world : Bukkit.getWorlds()) {
                    world.setViewDistance(newDist);
                }
            }
            case REDUCE_RANDOM_TICK_SPEED -> {
                int newSpeed = engage ? Math.max(0, (int)(3 * (1.0 - intensity))) : 3;
                for (World world : Bukkit.getWorlds()) {
                    world.setGameRule(GameRule.RANDOM_TICK_SPEED, newSpeed);
                }
            }
            case DISABLE_MOB_SPAWNING -> {
                for (World world : Bukkit.getWorlds()) {
                    if (engage) {
                        world.setSpawnLimit(SpawnCategory.MONSTER, 0);
                        world.setSpawnLimit(SpawnCategory.ANIMAL, 0);
                    } else {
                        world.setSpawnLimit(SpawnCategory.MONSTER, 70);
                        world.setSpawnLimit(SpawnCategory.ANIMAL, 10);
                    }
                }
            }
            case KILL_EXCESS_ENTITIES -> {
                if (engage) {
                    int killed = 0;
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof Mob mob && !(entity instanceof Player)) {
                                if (!mob.isCustomNameVisible() && mob.customName() == null) {
                                    if (mob.getLocation().getNearbyPlayers(32).isEmpty()) {
                                        mob.remove();
                                        killed++;
                                    }
                                }
                            }
                        }
                    }
                    if (killed > 0) {
                        notifyStaff("<dark_gray>[<gold>THROTTLE</gold>]</dark_gray> <yellow>Killed " + killed + " excess entities");
                    }
                }
            }
            case REDUCE_MOB_AI_RANGE -> {
                // Paper-specific: reduce mob activation range
                // This is typically configured in paper-world-defaults.yml
                // For runtime changes, we can adjust spawn limits as a proxy
                if (engage) {
                    for (World world : Bukkit.getWorlds()) {
                        int currentLimit = world.getSpawnLimit(SpawnCategory.MONSTER);
                        world.setSpawnLimit(SpawnCategory.MONSTER, Math.max(1, (int)(currentLimit * (1.0 - intensity))));
                    }
                }
            }
            case SLOW_HOPPER_TRANSFER, REDUCE_CHUNK_LOAD_RATE, INCREASE_ENTITY_DESPAWN_RATES, PAUSE_REDSTONE_UPDATES -> {
                // These require Paper-specific configuration or are handled through other mechanisms
                // Log the engagement for tracking
                plugin.logDebug("[Throttle] " + (engage ? "Engaged" : "Disengaged") + " " + action.name() +
                        " (requires server restart or Paper API for full effect)");
            }
        }
    }

    /**
     * Emergency engage all enabled throttles (called by MemoryManager at level 4).
     */
    public void emergencyEngage() {
        for (var entry : configs.entrySet()) {
            ThrottleConfig config = entry.getValue();
            if (config.isEnabled() && !config.isCurrentlyEngaged()) {
                engage(entry.getKey(), config, "emergency");
            }
        }
    }

    /**
     * Manually engage a specific throttle from the panel.
     */
    public void manualEngage(ThrottleAction action) {
        ThrottleConfig config = configs.get(action);
        if (config != null && !config.isCurrentlyEngaged()) {
            engage(action, config, "manual");
        }
    }

    /**
     * Manually disengage a specific throttle from the panel.
     */
    public void manualDisengage(ThrottleAction action) {
        ThrottleConfig config = configs.get(action);
        if (config != null && config.isCurrentlyEngaged()) {
            disengage(action, config);
        }
    }

    /**
     * Update a throttle config from web panel data.
     */
    public void updateConfig(String actionName, JsonObject data) {
        try {
            ThrottleAction action = ThrottleAction.valueOf(actionName);
            ThrottleConfig config = configs.get(action);
            if (config != null) {
                config.updateFromJson(data);
                broadcastThrottleStatus();
            }
        } catch (IllegalArgumentException e) {
            plugin.logDebug("Unknown throttle action: " + actionName);
        }
    }

    /**
     * Get all throttle statuses as JSON for the web panel.
     */
    public JsonObject getStatusJson() {
        JsonObject status = new JsonObject();
        JsonArray actions = new JsonArray();
        for (var entry : configs.entrySet()) {
            actions.add(entry.getValue().toJson());
        }
        status.add("actions", actions);
        status.addProperty("manualOverride", manualOverride);
        return status;
    }

    private void notifyStaff(String message) {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(staff, "moderex.notify.throttle")) {
                Msg.send(staff, TextUtil.parse(message));
            }
        }
    }

    private void broadcastThrottleStatus() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastMessage("THROTTLE_STATUS_UPDATE", getStatusJson());
        }
    }

    private void logThrottleEvent(ThrottleAction action, boolean engaged, String triggeredBy) {
        try {
            plugin.getDatabaseManager().update("""
                INSERT INTO moderex_throttle_history (action, engaged, tps_at_time, triggered_by, created_at)
                VALUES (?, ?, ?, ?, ?)
            """, action.name(), engaged, plugin.getServerStatusManager().getCurrentTps(),
                    triggeredBy, System.currentTimeMillis());
        } catch (SQLException e) {
            plugin.logDebug("Failed to log throttle event: " + e.getMessage());
        }
    }

    public void createTable() {
        try {
            plugin.getDatabaseManager().update("""
                CREATE TABLE IF NOT EXISTS moderex_throttle_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    action VARCHAR(64) NOT NULL,
                    engaged BOOLEAN NOT NULL,
                    tps_at_time REAL NOT NULL,
                    triggered_by VARCHAR(20),
                    created_at BIGINT NOT NULL
                )
            """);
        } catch (SQLException e) {
            plugin.logError("Failed to create throttle_history table", e);
        }
    }
}

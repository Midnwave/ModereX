package com.blockforge.moderex.monitor;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.Settings;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;

/**
 * Manages configurable performance settings (LagFixer-inspired).
 * Settings can be modified via the web panel and are applied to the server in real-time.
 */
public class PerformanceSettingsManager {

    private final ModereX plugin;

    // Current applied settings (defaults = vanilla values, -1 = use server default)
    private int entityLimitPerChunk = -1;
    private int tileEntityLimitPerChunk = -1;
    private int hopperTransferRate = 8;
    private double mobSpawnMultiplier = 1.0;
    private int randomTickSpeed = 3;
    private int viewDistance = -1;
    private int simulationDistance = -1;
    private int monsterSpawnLimit = 70;
    private int animalSpawnLimit = 10;
    private int waterAnimalSpawnLimit = 5;
    private int ambientSpawnLimit = 15;
    private int itemDespawnRate = 6000; // ticks (5 minutes default)
    private boolean disableIceFormation = false;
    private boolean disableFireSpread = false;
    private int maxAutoSaveChunksPerTick = -1;

    public PerformanceSettingsManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        loadFromSettings();
    }

    private void loadFromSettings() {
        Settings s = plugin.getConfigManager().getSettings();
        // Load values from Settings if they exist
        // These will be populated once Settings.java has the fields
    }

    /**
     * Apply all current performance settings to the server.
     */
    public void applySettings() {
        for (World world : Bukkit.getWorlds()) {
            // View distance
            if (viewDistance > 0) {
                world.setViewDistance(viewDistance);
            }

            // Simulation distance
            if (simulationDistance > 0) {
                world.setSimulationDistance(simulationDistance);
            }

            // Random tick speed
            if (randomTickSpeed >= 0) {
                world.setGameRule(GameRule.RANDOM_TICK_SPEED, randomTickSpeed);
            }

            // Spawn limits
            if (monsterSpawnLimit >= 0) {
                world.setSpawnLimit(SpawnCategory.MONSTER, monsterSpawnLimit);
            }
            if (animalSpawnLimit >= 0) {
                world.setSpawnLimit(SpawnCategory.ANIMAL, animalSpawnLimit);
            }
            if (waterAnimalSpawnLimit >= 0) {
                world.setSpawnLimit(SpawnCategory.WATER_ANIMAL, waterAnimalSpawnLimit);
            }
            if (ambientSpawnLimit >= 0) {
                world.setSpawnLimit(SpawnCategory.AMBIENT, ambientSpawnLimit);
            }

            // Fire spread
            world.setGameRule(GameRule.DO_FIRE_TICK, !disableFireSpread);
        }

        plugin.getLogger().info("Performance settings applied to all worlds");
    }

    /**
     * Reset all settings to vanilla defaults.
     */
    public void resetToDefaults() {
        entityLimitPerChunk = -1;
        tileEntityLimitPerChunk = -1;
        hopperTransferRate = 8;
        mobSpawnMultiplier = 1.0;
        randomTickSpeed = 3;
        viewDistance = -1;
        simulationDistance = -1;
        monsterSpawnLimit = 70;
        animalSpawnLimit = 10;
        waterAnimalSpawnLimit = 5;
        ambientSpawnLimit = 15;
        itemDespawnRate = 6000;
        disableIceFormation = false;
        disableFireSpread = false;
        maxAutoSaveChunksPerTick = -1;
        applySettings();
    }

    /**
     * Update settings from web panel data.
     */
    public void updateFromJson(JsonObject data) {
        if (data.has("viewDistance")) viewDistance = data.get("viewDistance").getAsInt();
        if (data.has("simulationDistance")) simulationDistance = data.get("simulationDistance").getAsInt();
        if (data.has("randomTickSpeed")) randomTickSpeed = data.get("randomTickSpeed").getAsInt();
        if (data.has("monsterSpawnLimit")) monsterSpawnLimit = data.get("monsterSpawnLimit").getAsInt();
        if (data.has("animalSpawnLimit")) animalSpawnLimit = data.get("animalSpawnLimit").getAsInt();
        if (data.has("waterAnimalSpawnLimit")) waterAnimalSpawnLimit = data.get("waterAnimalSpawnLimit").getAsInt();
        if (data.has("ambientSpawnLimit")) ambientSpawnLimit = data.get("ambientSpawnLimit").getAsInt();
        if (data.has("mobSpawnMultiplier")) mobSpawnMultiplier = data.get("mobSpawnMultiplier").getAsDouble();
        if (data.has("hopperTransferRate")) hopperTransferRate = data.get("hopperTransferRate").getAsInt();
        if (data.has("entityLimitPerChunk")) entityLimitPerChunk = data.get("entityLimitPerChunk").getAsInt();
        if (data.has("tileEntityLimitPerChunk")) tileEntityLimitPerChunk = data.get("tileEntityLimitPerChunk").getAsInt();
        if (data.has("itemDespawnRate")) itemDespawnRate = data.get("itemDespawnRate").getAsInt();
        if (data.has("disableFireSpread")) disableFireSpread = data.get("disableFireSpread").getAsBoolean();
        if (data.has("disableIceFormation")) disableIceFormation = data.get("disableIceFormation").getAsBoolean();
        if (data.has("maxAutoSaveChunksPerTick")) maxAutoSaveChunksPerTick = data.get("maxAutoSaveChunksPerTick").getAsInt();

        // Apply changes immediately
        Bukkit.getScheduler().runTask(plugin, this::applySettings);
    }

    /**
     * Get current settings as JSON for the web panel.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("viewDistance", viewDistance);
        json.addProperty("simulationDistance", simulationDistance);
        json.addProperty("randomTickSpeed", randomTickSpeed);
        json.addProperty("monsterSpawnLimit", monsterSpawnLimit);
        json.addProperty("animalSpawnLimit", animalSpawnLimit);
        json.addProperty("waterAnimalSpawnLimit", waterAnimalSpawnLimit);
        json.addProperty("ambientSpawnLimit", ambientSpawnLimit);
        json.addProperty("mobSpawnMultiplier", mobSpawnMultiplier);
        json.addProperty("hopperTransferRate", hopperTransferRate);
        json.addProperty("entityLimitPerChunk", entityLimitPerChunk);
        json.addProperty("tileEntityLimitPerChunk", tileEntityLimitPerChunk);
        json.addProperty("itemDespawnRate", itemDespawnRate);
        json.addProperty("disableFireSpread", disableFireSpread);
        json.addProperty("disableIceFormation", disableIceFormation);
        json.addProperty("maxAutoSaveChunksPerTick", maxAutoSaveChunksPerTick);

        // Include current server values for reference
        World overworld = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().getFirst();
        if (overworld != null) {
            json.addProperty("currentViewDistance", overworld.getViewDistance());
            json.addProperty("currentSimulationDistance", overworld.getSimulationDistance());
            json.addProperty("currentRandomTickSpeed", overworld.getGameRuleValue(GameRule.RANDOM_TICK_SPEED));
        }

        return json;
    }

    /**
     * Reduce chunk loading during memory pressure.
     */
    public void reduceChunkLoading() {
        for (World world : Bukkit.getWorlds()) {
            int current = world.getViewDistance();
            if (current > 4) {
                world.setViewDistance(Math.max(4, current - 2));
            }
        }
    }

    // Getters
    public int getEntityLimitPerChunk() { return entityLimitPerChunk; }
    public int getTileEntityLimitPerChunk() { return tileEntityLimitPerChunk; }
    public int getViewDistance() { return viewDistance; }
    public double getMobSpawnMultiplier() { return mobSpawnMultiplier; }
    public int getRandomTickSpeed() { return randomTickSpeed; }

    public void stop() {
        // Nothing to clean up
    }
}

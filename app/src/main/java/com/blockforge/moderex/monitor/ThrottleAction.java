package com.blockforge.moderex.monitor;

public enum ThrottleAction {
    REDUCE_MOB_CAP("Reduce Mob Cap", "Lowers monster spawn limits"),
    SLOW_HOPPER_TRANSFER("Slow Hoppers", "Reduces hopper transfer speed"),
    REDUCE_RANDOM_TICK_SPEED("Reduce Random Ticks", "Lowers random tick speed"),
    REDUCE_MOB_AI_RANGE("Reduce Mob AI Range", "Decreases mob activation range"),
    REDUCE_CHUNK_LOAD_RATE("Reduce Chunk Loading", "Throttles chunk loading speed"),
    DISABLE_MOB_SPAWNING("Disable Mob Spawning", "Completely stops mob spawning"),
    INCREASE_ENTITY_DESPAWN_RATES("Increase Despawn Rate", "Entities despawn faster"),
    REDUCE_VIEW_DISTANCE("Reduce View Distance", "Lowers server view distance"),
    PAUSE_REDSTONE_UPDATES("Pause Redstone", "Disables redstone tick updates"),
    KILL_EXCESS_ENTITIES("Kill Excess Entities", "Removes far-away non-named mobs");

    private final String displayName;
    private final String description;

    ThrottleAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}

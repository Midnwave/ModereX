package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hook for Citizens2 NPC plugin integration.
 * Provides smoother NPC-based replay playback when Citizens is available.
 * Falls back to ArmorStand-based playback if Citizens is not installed.
 */
public class CitizensHook {

    private final ModereX plugin;
    private boolean available = false;
    private String version = "N/A";
    private ClassLoader citizensClassLoader;

    // Track NPCs created for replays
    private final Map<UUID, Object> replayNpcs = new HashMap<>();

    public CitizensHook(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the Citizens hook.
     * @return true if Citizens is available and hooked successfully
     */
    public boolean initialize() {
        return checkAndInitialize();
    }

    /**
     * Check and initialize Citizens hook.
     * Can be called multiple times - will initialize lazily if Citizens becomes available.
     */
    private boolean checkAndInitialize() {
        if (available) return true;

        Plugin citizensPlugin = Bukkit.getPluginManager().getPlugin("Citizens");
        if (citizensPlugin == null || !citizensPlugin.isEnabled()) {
            plugin.logDebug("[Citizens] Citizens plugin not found or not enabled");
            return false;
        }

        version = citizensPlugin.getDescription().getVersion();
        plugin.logDebug("[Citizens] Found Citizens " + version + ", verifying API access...");

        // Verify Citizens API is accessible using Citizens' classloader
        try {
            citizensClassLoader = citizensPlugin.getClass().getClassLoader();

            // Verify core API classes exist (stable across all Citizens versions)
            Class.forName("net.citizensnpcs.api.CitizensAPI", true, citizensClassLoader);
            Class.forName("net.citizensnpcs.api.npc.NPC", true, citizensClassLoader);
            Class.forName("net.citizensnpcs.api.npc.NPCRegistry", true, citizensClassLoader);

            // Test that we can actually access the registry (validates API is functional)
            Class<?> apiClass = getCitizensClass("net.citizensnpcs.api.CitizensAPI");
            Object registry = apiClass.getMethod("getNPCRegistry").invoke(null);
            if (registry == null) {
                plugin.logDebug("[Citizens] NPCRegistry is null - Citizens may not be fully initialized");
                return false;
            }

            available = true;
            plugin.logDebug("[Citizens] API v" + version + " accessible and functional");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.logDebug("[Citizens] Citizens API classes not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to access Citizens API: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensure Citizens is available, attempting lazy initialization if needed.
     */
    public boolean ensureAvailable() {
        if (!available) {
            return checkAndInitialize();
        }
        return true;
    }

    /**
     * Check if Citizens is available.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Get the Citizens plugin version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Create an NPC for replay playback.
     * Uses reflection to avoid compile-time dependency on Citizens.
     *
     * @param name The NPC name to display
     * @param skinUuid The UUID of the player whose skin to use
     * @param location The spawn location
     * @return A unique identifier for the created NPC, or null if creation failed
     */
    /**
     * Helper to load Citizens classes using the correct classloader.
     */
    private Class<?> getCitizensClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, citizensClassLoader);
    }

    public UUID createReplayNpc(String name, UUID playerUuid, Location location) {
        if (!available || citizensClassLoader == null) return null;

        try {
            // Get CitizensAPI and registry
            Class<?> citizensApiClass = getCitizensClass("net.citizensnpcs.api.CitizensAPI");
            Object npcRegistry = citizensApiClass.getMethod("getNPCRegistry").invoke(null);
            Class<?> npcRegistryClass = getCitizensClass("net.citizensnpcs.api.npc.NPCRegistry");
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");

            // Create NPC - try different method signatures for version compatibility
            Object npc = null;
            try {
                // Standard method: createNPC(EntityType, String)
                npc = npcRegistryClass.getMethod("createNPC", EntityType.class, String.class)
                        .invoke(npcRegistry, EntityType.PLAYER, name);
            } catch (NoSuchMethodException e) {
                // Fallback: try createNPC(EntityType, String, Location) which spawns immediately
                plugin.logDebug("[Citizens] Trying alternative createNPC method...");
                npc = npcRegistryClass.getMethod("createNPC", EntityType.class, String.class, Location.class)
                        .invoke(npcRegistry, EntityType.PLAYER, name, location);
            }

            if (npc == null) {
                plugin.logDebug("[Citizens] Failed to create NPC - registry returned null");
                return null;
            }

            // Set NPC as protected (uses default method in newer versions)
            try {
                npcClass.getMethod("setProtected", boolean.class).invoke(npc, true);
            } catch (NoSuchMethodException e) {
                // Fallback: try accessing data() directly for older versions
                plugin.logDebug("[Citizens] setProtected not found, trying data() approach");
                try {
                    Object data = npcClass.getMethod("data").invoke(npc);
                    data.getClass().getMethod("setPersistent", String.class, Object.class)
                            .invoke(data, "protected", true);
                } catch (Exception e2) {
                    plugin.logDebug("[Citizens] Could not set NPC protected: " + e2.getMessage());
                }
            }

            // Spawn the NPC if not already spawned
            boolean isSpawned = (Boolean) npcClass.getMethod("isSpawned").invoke(npc);
            if (!isSpawned) {
                npcClass.getMethod("spawn", Location.class).invoke(npc, location);
            }

            // Get the NPC's unique ID
            UUID npcId = (UUID) npcClass.getMethod("getUniqueId").invoke(npc);

            // Try to set the skin - pass player name and UUID
            trySetSkin(npc, npcClass, name, playerUuid);

            // Store reference
            replayNpcs.put(npcId, npc);
            plugin.logDebug("[Citizens] Created replay NPC: " + name + " (UUID: " + npcId + ")");

            return npcId;
        } catch (Exception e) {
            plugin.logError("Failed to create Citizens NPC", e);
            return null;
        }
    }

    /**
     * Check if a player UUID belongs to a Bedrock player (Geyser/Floodgate).
     * Bedrock UUIDs start with 00000000-0000-0000.
     */
    private boolean isBedrockPlayer(UUID uuid) {
        // Check via HookManager first
        var hookManager = plugin.getHookManager();
        if (hookManager != null) {
            // Try Floodgate first (more accurate)
            if (hookManager.hasFloodgate()) {
                var floodgate = hookManager.getFloodgateHook();
                if (floodgate != null && floodgate.isFloodgatePlayer(uuid)) {
                    return true;
                }
            }
            // Try Geyser
            if (hookManager.hasGeyser()) {
                var geyser = hookManager.getGeyserHook();
                if (geyser != null && geyser.isBedrockPlayer(uuid)) {
                    return true;
                }
            }
        }
        // Fallback: check UUID pattern (Floodgate UUIDs start with 00000000-0000-0000)
        return uuid.toString().startsWith("00000000-0000-0000");
    }

    /**
     * Try to set the NPC's skin using various methods for version compatibility.
     * Uses player name for skin lookup (required by Citizens).
     * Skips skin setting for Bedrock players as they don't have Mojang skins.
     */
    private void trySetSkin(Object npc, Class<?> npcClass, String playerName, UUID playerUuid) {
        // Check if this is a Bedrock player - skip skin for them
        if (isBedrockPlayer(playerUuid)) {
            plugin.logDebug("[Citizens] Skipping skin for Bedrock player: " + playerName);
            return;
        }

        // For Java players, use their name to look up skin from Mojang
        // Method 1: SkinTrait (most common)
        try {
            Object skinTrait = npcClass.getMethod("getOrAddTrait", Class.class)
                    .invoke(npc, getCitizensClass("net.citizensnpcs.trait.SkinTrait"));
            Class<?> skinTraitClass = getCitizensClass("net.citizensnpcs.trait.SkinTrait");

            // Try setSkinName first (uses player name to fetch from Mojang)
            try {
                skinTraitClass.getMethod("setSkinName", String.class).invoke(skinTrait, playerName);
                plugin.logDebug("[Citizens] Set skin for NPC using player name: " + playerName);
                return;
            } catch (NoSuchMethodException e) {
                // Try setSkinPersistent (newer versions, also accepts player name)
                try {
                    skinTraitClass.getMethod("setSkinPersistent", String.class).invoke(skinTrait, playerName);
                    plugin.logDebug("[Citizens] Set skin for NPC using setSkinPersistent: " + playerName);
                    return;
                } catch (NoSuchMethodException e2) {
                    plugin.logDebug("[Citizens] No compatible skin method found");
                }
            }
        } catch (ClassNotFoundException e) {
            plugin.logDebug("[Citizens] SkinTrait not found - skin feature unavailable");
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Could not set NPC skin: " + e.getMessage());
        }
    }

    /**
     * Update an NPC's location and look direction.
     *
     * @param npcId The NPC's unique ID
     * @param location The new location
     */
    public void updateNpcLocation(UUID npcId, Location location) {
        if (!available || citizensClassLoader == null) return;

        Object npc = replayNpcs.get(npcId);
        if (npc == null) return;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            Object entity = npcClass.getMethod("getEntity").invoke(npc);

            if (entity != null) {
                // Teleport the NPC
                Class<?> entityClass = entity.getClass();
                entityClass.getMethod("teleport", Location.class).invoke(entity, location);
            }
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to update NPC location: " + e.getMessage());
        }
    }

    /**
     * Set whether the NPC is sneaking.
     *
     * @param npcId The NPC's unique ID
     * @param sneaking Whether the NPC should sneak
     */
    public void setNpcSneaking(UUID npcId, boolean sneaking) {
        if (!available || citizensClassLoader == null) return;

        Object npc = replayNpcs.get(npcId);
        if (npc == null) return;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            Object entity = npcClass.getMethod("getEntity").invoke(npc);

            if (entity != null && entity instanceof org.bukkit.entity.Player playerNpc) {
                playerNpc.setSneaking(sneaking);
            }
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to set NPC sneaking: " + e.getMessage());
        }
    }

    /**
     * Set the NPC's held item.
     *
     * @param npcId The NPC's unique ID
     * @param item The item to hold (main hand)
     */
    public void setNpcHeldItem(UUID npcId, org.bukkit.inventory.ItemStack item) {
        if (!available || citizensClassLoader == null) return;

        Object npc = replayNpcs.get(npcId);
        if (npc == null) return;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            Object entity = npcClass.getMethod("getEntity").invoke(npc);

            if (entity != null && entity instanceof org.bukkit.entity.LivingEntity living) {
                var equipment = living.getEquipment();
                if (equipment != null) {
                    equipment.setItemInMainHand(item);
                }
            }
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to set NPC held item: " + e.getMessage());
        }
    }

    /**
     * Set the NPC's armor.
     *
     * @param npcId The NPC's unique ID
     * @param armor The armor to wear (boots, leggings, chestplate, helmet)
     */
    public void setNpcArmor(UUID npcId, org.bukkit.inventory.ItemStack[] armor) {
        if (!available || citizensClassLoader == null || armor == null) return;

        Object npc = replayNpcs.get(npcId);
        if (npc == null) return;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            Object entity = npcClass.getMethod("getEntity").invoke(npc);

            if (entity != null && entity instanceof org.bukkit.entity.LivingEntity living) {
                var equipment = living.getEquipment();
                if (equipment != null) {
                    equipment.setArmorContents(armor);
                }
            }
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to set NPC armor: " + e.getMessage());
        }
    }

    /**
     * Check if an NPC exists and is spawned.
     *
     * @param npcId The NPC's unique ID
     * @return true if the NPC exists and is spawned
     */
    public boolean isNpcSpawned(UUID npcId) {
        if (!available || citizensClassLoader == null) return false;

        Object npc = replayNpcs.get(npcId);
        if (npc == null) return false;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            return (Boolean) npcClass.getMethod("isSpawned").invoke(npc);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove and despawn an NPC.
     *
     * @param npcId The NPC's unique ID
     */
    public void removeNpc(UUID npcId) {
        if (!available || citizensClassLoader == null) return;

        Object npc = replayNpcs.remove(npcId);
        if (npc == null) return;

        try {
            Class<?> npcClass = getCitizensClass("net.citizensnpcs.api.npc.NPC");
            npcClass.getMethod("destroy").invoke(npc);
        } catch (Exception e) {
            plugin.logDebug("[Citizens] Failed to remove NPC: " + e.getMessage());
        }
    }

    /**
     * Remove all replay NPCs created by this hook.
     */
    public void removeAllNpcs() {
        for (UUID npcId : new HashMap<>(replayNpcs).keySet()) {
            removeNpc(npcId);
        }
        replayNpcs.clear();
    }

    /**
     * Shutdown the hook and cleanup all NPCs.
     */
    public void shutdown() {
        removeAllNpcs();
        available = false;
    }
}

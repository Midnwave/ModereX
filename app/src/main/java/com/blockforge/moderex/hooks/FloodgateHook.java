package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Hook for Floodgate - Bedrock authentication plugin.
 * Works alongside Geyser to authenticate Bedrock players.
 */
public class FloodgateHook {

    private final ModereX plugin;
    private Object floodgateApi;
    private Method isFloodgatePlayerMethod;
    private Method getPlayerMethod;
    private String version;

    public FloodgateHook(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            // Try to get the Floodgate API
            Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Method getInstance = floodgateApiClass.getMethod("getInstance");
            floodgateApi = getInstance.invoke(null);

            // Get the isFloodgatePlayer method
            isFloodgatePlayerMethod = floodgateApiClass.getMethod("isFloodgatePlayer", UUID.class);

            // Get the getPlayer method for additional info
            try {
                getPlayerMethod = floodgateApiClass.getMethod("getPlayer", UUID.class);
            } catch (NoSuchMethodException ignored) {
                // Optional method
            }

            // Get version
            Plugin floodgatePlugin = Bukkit.getPluginManager().getPlugin("floodgate");
            version = floodgatePlugin != null ? floodgatePlugin.getPluginMeta().getVersion() : "Unknown";

            plugin.getLogger().info("Hooked into Floodgate v" + version);
            return true;
        } catch (ClassNotFoundException e) {
            // Floodgate API not found - try legacy approach
            return tryLegacyHook();
        } catch (Exception e) {
            plugin.logDebug("Failed to hook into Floodgate API: " + e.getMessage());
            return tryLegacyHook();
        }
    }

    private boolean tryLegacyHook() {
        // Just check if the plugin exists
        Plugin floodgatePlugin = Bukkit.getPluginManager().getPlugin("floodgate");
        if (floodgatePlugin == null) {
            floodgatePlugin = Bukkit.getPluginManager().getPlugin("Floodgate");
        }

        if (floodgatePlugin != null && floodgatePlugin.isEnabled()) {
            version = floodgatePlugin.getPluginMeta().getVersion();
            plugin.getLogger().info("Detected Floodgate v" + version + " (limited API access)");
            return true;
        }

        return false;
    }

    /**
     * Check if a player is a Floodgate (Bedrock) player.
     */
    public boolean isFloodgatePlayer(UUID uuid) {
        if (floodgateApi == null || isFloodgatePlayerMethod == null) {
            // Fallback: check if UUID is a floodgate prefixed UUID
            return isFloodgateUuid(uuid);
        }

        try {
            return (boolean) isFloodgatePlayerMethod.invoke(floodgateApi, uuid);
        } catch (Exception e) {
            return isFloodgateUuid(uuid);
        }
    }

    /**
     * Check if a player is a Floodgate (Bedrock) player.
     */
    public boolean isFloodgatePlayer(Player player) {
        return isFloodgatePlayer(player.getUniqueId());
    }

    /**
     * Check if UUID matches Floodgate pattern.
     * Floodgate UUIDs typically start with 00000000-0000-0000.
     */
    private boolean isFloodgateUuid(UUID uuid) {
        String uuidStr = uuid.toString();
        return uuidStr.startsWith("00000000-0000-0000");
    }

    /**
     * Get the Bedrock username for a Floodgate player.
     * Returns null if not a Floodgate player or API not available.
     */
    public String getBedrockUsername(UUID uuid) {
        if (getPlayerMethod == null || floodgateApi == null) {
            return null;
        }

        try {
            Object floodgatePlayer = getPlayerMethod.invoke(floodgateApi, uuid);
            if (floodgatePlayer != null) {
                Method getUsernameMethod = floodgatePlayer.getClass().getMethod("getUsername");
                return (String) getUsernameMethod.invoke(floodgatePlayer);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String getVersion() {
        return version;
    }

    public boolean isAvailable() {
        return version != null;
    }
}

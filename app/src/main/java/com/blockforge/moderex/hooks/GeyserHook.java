package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Hook for Geyser - Bedrock to Java Edition proxy.
 * Allows detecting Bedrock players and getting their info.
 */
public class GeyserHook {

    private final ModereX plugin;
    private Object geyserApi;
    private Method isBedrockPlayerMethod;
    private String version;

    public GeyserHook(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            // Try to get the Geyser API
            Class<?> geyserApiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Method apiMethod = geyserApiClass.getMethod("api");
            geyserApi = apiMethod.invoke(null);

            // Get the isBedrockPlayer method
            isBedrockPlayerMethod = geyserApiClass.getMethod("isBedrockPlayer", UUID.class);

            // Get version
            Plugin geyserPlugin = Bukkit.getPluginManager().getPlugin("Geyser-Spigot");
            if (geyserPlugin == null) {
                geyserPlugin = Bukkit.getPluginManager().getPlugin("Geyser");
            }
            version = geyserPlugin != null ? geyserPlugin.getDescription().getVersion() : "Unknown";

            plugin.getLogger().info("Hooked into Geyser v" + version);
            return true;
        } catch (ClassNotFoundException e) {
            // Geyser API not found - try legacy approach
            return tryLegacyHook();
        } catch (Exception e) {
            plugin.logDebug("Failed to hook into Geyser API: " + e.getMessage());
            return tryLegacyHook();
        }
    }

    private boolean tryLegacyHook() {
        // Just check if the plugin exists
        Plugin geyserPlugin = Bukkit.getPluginManager().getPlugin("Geyser-Spigot");
        if (geyserPlugin == null) {
            geyserPlugin = Bukkit.getPluginManager().getPlugin("Geyser");
        }

        if (geyserPlugin != null && geyserPlugin.isEnabled()) {
            version = geyserPlugin.getDescription().getVersion();
            plugin.getLogger().info("Detected Geyser v" + version + " (limited API access)");
            return true;
        }

        return false;
    }

    /**
     * Check if a player is connecting from Bedrock Edition.
     */
    public boolean isBedrockPlayer(UUID uuid) {
        if (geyserApi == null || isBedrockPlayerMethod == null) {
            // Fallback: check if UUID is a floodgate prefixed UUID
            return isFloodgateUuid(uuid);
        }

        try {
            return (boolean) isBedrockPlayerMethod.invoke(geyserApi, uuid);
        } catch (Exception e) {
            return isFloodgateUuid(uuid);
        }
    }

    /**
     * Check if a player is connecting from Bedrock Edition.
     */
    public boolean isBedrockPlayer(Player player) {
        return isBedrockPlayer(player.getUniqueId());
    }

    /**
     * Check if UUID matches Floodgate pattern (starts with specific prefix).
     */
    private boolean isFloodgateUuid(UUID uuid) {
        // Floodgate UUIDs start with 00000000-0000-0000
        String uuidStr = uuid.toString();
        return uuidStr.startsWith("00000000-0000-0000");
    }

    public String getVersion() {
        return version;
    }

    public boolean isAvailable() {
        return version != null;
    }
}

package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.anticheat.AnticheatManager;
import org.bukkit.Bukkit;

public class HookManager {

    private final ModereX plugin;
    private LuckPermsHook luckPermsHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private CoreProtectHook coreProtectHook;
    private AnticheatManager anticheatManager;
    private String detectedAnticheat;

    public HookManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        // Hook into LuckPerms
        if (isPluginEnabled("LuckPerms")) {
            try {
                luckPermsHook = new LuckPermsHook();
                plugin.getLogger().info("Hooked into LuckPerms for prefix support.");
            } catch (Exception e) {
                plugin.logError("Failed to hook into LuckPerms", e);
            }
        }

        // Hook into PlaceholderAPI
        if (isPluginEnabled("PlaceholderAPI")) {
            try {
                placeholderAPIHook = new PlaceholderAPIHook(plugin);
                placeholderAPIHook.register();
                plugin.getLogger().info("Hooked into PlaceholderAPI.");
            } catch (Exception e) {
                plugin.logError("Failed to hook into PlaceholderAPI", e);
            }
        }

        // Hook into CoreProtect
        if (isPluginEnabled("CoreProtect")) {
            try {
                coreProtectHook = new CoreProtectHook(plugin);
                if (coreProtectHook.initialize()) {
                    plugin.getLogger().info("Hooked into CoreProtect for block logging.");
                } else {
                    coreProtectHook = null;
                }
            } catch (Exception e) {
                plugin.logError("Failed to hook into CoreProtect", e);
                coreProtectHook = null;
            }
        }

        // Auto-detect anticheat
        if (plugin.getConfigManager().getSettings().isAnticheatAutoDetect()) {
            detectAnticheat();
        }

        // Initialize anticheat manager
        anticheatManager = new AnticheatManager(plugin);
        anticheatManager.initialize();
    }

    private void detectAnticheat() {
        // Map of display names to possible plugin names
        String[][] anticheats = {
            {"Grim", "Grim", "GrimAC"},  // Grim can register under multiple names
            {"Vulcan", "Vulcan"},
            {"Matrix", "Matrix"},
            {"Spartan", "Spartan"},
            {"Intave", "Intave"},
            {"Karhu", "Karhu"},
            {"Polar", "Polar"},
            {"NoCheatPlus", "NoCheatPlus", "NCP"}
        };

        plugin.logDebug("[Anticheat] Starting anticheat detection...");
        plugin.logDebug("[Anticheat] Loaded plugins: " + String.join(", ",
            java.util.Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(p -> p.getName())
                .toArray(String[]::new)));

        for (String[] acNames : anticheats) {
            String displayName = acNames[0];
            for (int i = 1; i < acNames.length; i++) {
                String pluginName = acNames[i];
                if (isPluginEnabled(pluginName)) {
                    detectedAnticheat = displayName;
                    plugin.getLogger().info("Detected anticheat: " + displayName + " (plugin: " + pluginName + ")");
                    return;
                }
            }
        }

        plugin.getLogger().info("No supported anticheat detected.");
    }

    public boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public LuckPermsHook getLuckPermsHook() {
        return luckPermsHook;
    }

    public PlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public String getDetectedAnticheat() {
        return detectedAnticheat;
    }

    public boolean hasLuckPerms() {
        return luckPermsHook != null;
    }

    public boolean isLuckPermsEnabled() {
        return luckPermsHook != null;
    }

    public boolean isLuckPermsAvailable() {
        return luckPermsHook != null;
    }

    public String getLuckPermsVersion() {
        if (luckPermsHook == null) return "N/A";
        var lp = Bukkit.getPluginManager().getPlugin("LuckPerms");
        return lp != null ? lp.getDescription().getVersion() : "N/A";
    }

    public boolean hasPlaceholderAPI() {
        return placeholderAPIHook != null;
    }

    public CoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    public boolean hasCoreProtect() {
        return coreProtectHook != null && coreProtectHook.isEnabled();
    }

    public AnticheatManager getAnticheatManager() {
        return anticheatManager;
    }

    public void shutdown() {
        if (placeholderAPIHook != null) {
            placeholderAPIHook.unregister();
        }
        if (anticheatManager != null) {
            anticheatManager.shutdown();
        }
    }
}

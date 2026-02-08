package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.anticheat.AnticheatManager;
import org.bukkit.Bukkit;

public class HookManager {

    private final ModereX plugin;
    private LuckPermsHook luckPermsHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private CoreProtectHook coreProtectHook;
    private GeyserHook geyserHook;
    private FloodgateHook floodgateHook;
    private CitizensHook citizensHook;
    private SparkHook sparkHook;
    private SimpleVoiceChatHook voiceChatHook;
    private BlueMapHook blueMapHook;
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

        // Hook into Geyser (Bedrock proxy)
        if (isPluginEnabled("Geyser-Spigot") || isPluginEnabled("Geyser")) {
            try {
                geyserHook = new GeyserHook(plugin);
                if (!geyserHook.initialize()) {
                    geyserHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("Failed to hook into Geyser: " + e.getMessage());
                geyserHook = null;
            }
        }

        // Hook into Floodgate (Bedrock authentication)
        if (isPluginEnabled("floodgate") || isPluginEnabled("Floodgate")) {
            try {
                floodgateHook = new FloodgateHook(plugin);
                if (!floodgateHook.initialize()) {
                    floodgateHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("Failed to hook into Floodgate: " + e.getMessage());
                floodgateHook = null;
            }
        }

        // Hook into Citizens (NPC plugin for replay playback)
        // Citizens may not be fully loaded at this point - lazy init will try again when needed
        if (isPluginEnabled("Citizens")) {
            try {
                citizensHook = new CitizensHook(plugin);
                if (citizensHook.initialize()) {
                    plugin.getLogger().info("Hooked into Citizens " + citizensHook.getVersion() + " for replay NPCs.");
                } else {
                    // Don't warn - lazy initialization will try again when replay is used
                    plugin.logDebug("[Citizens] Citizens detected but API not yet accessible - will retry on demand");
                    citizensHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("[Citizens] Exception during hook: " + e.getMessage());
                citizensHook = null;
            }
        }

        // Hook into Spark (profiler)
        if (isPluginEnabled("spark")) {
            try {
                sparkHook = new SparkHook(plugin);
                if (sparkHook.isAvailable()) {
                    plugin.getLogger().info("Hooked into Spark " + sparkHook.getVersion() + " for advanced profiling.");
                } else {
                    sparkHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("[Spark] Exception during hook: " + e.getMessage());
                sparkHook = null;
            }
        }

        // Hook into Simple Voice Chat
        if (isPluginEnabled("voicechat")) {
            try {
                voiceChatHook = new SimpleVoiceChatHook(plugin);
                if (voiceChatHook.isAvailable()) {
                    plugin.getLogger().info("Hooked into Simple Voice Chat " + voiceChatHook.getVersion());
                } else {
                    voiceChatHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("[VoiceChat] Exception during hook: " + e.getMessage());
                voiceChatHook = null;
            }
        }

        // Hook into BlueMap (3D map renderer for replay terrain)
        if (isPluginEnabled("BlueMap")) {
            try {
                blueMapHook = new BlueMapHook(plugin);
                if (!blueMapHook.isAvailable()) {
                    blueMapHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("[BlueMap] Exception during hook: " + e.getMessage());
                blueMapHook = null;
            }
        }

        // Auto-detect anticheat
        if (plugin.getConfigManager().getSettings().isAnticheatAutoDetect()) {
            detectAnticheat();
        }

        // Initialize anticheat manager
        anticheatManager = new AnticheatManager(plugin);
        anticheatManager.initialize();

        // Log enabled anticheats for debugging
        if (!anticheatManager.getEnabledAnticheats().isEmpty()) {
            plugin.getLogger().info("[HookManager] Enabled anticheats: " + String.join(", ", anticheatManager.getEnabledAnticheats()));
        }
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

    public GeyserHook getGeyserHook() {
        return geyserHook;
    }

    public boolean hasGeyser() {
        return geyserHook != null && geyserHook.isAvailable();
    }

    public boolean isGeyserAvailable() {
        return geyserHook != null && geyserHook.isAvailable();
    }

    public String getGeyserVersion() {
        return geyserHook != null ? geyserHook.getVersion() : "N/A";
    }

    public FloodgateHook getFloodgateHook() {
        return floodgateHook;
    }

    public boolean hasFloodgate() {
        return floodgateHook != null && floodgateHook.isAvailable();
    }

    public boolean isFloodgateAvailable() {
        return floodgateHook != null && floodgateHook.isAvailable();
    }

    public String getFloodgateVersion() {
        return floodgateHook != null ? floodgateHook.getVersion() : "N/A";
    }

    public CitizensHook getCitizensHook() {
        // Try lazy initialization if not already hooked
        if (citizensHook == null) {
            tryLazyInitCitizens();
        }
        return citizensHook;
    }

    public boolean hasCitizens() {
        // Try lazy initialization if not already hooked
        if (citizensHook == null) {
            tryLazyInitCitizens();
        }
        return citizensHook != null && citizensHook.isAvailable();
    }

    public boolean isCitizensAvailable() {
        return hasCitizens();
    }

    private void tryLazyInitCitizens() {
        plugin.logDebug("[Citizens] Lazy init called, checking if Citizens is enabled...");
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            plugin.logDebug("[Citizens] Citizens plugin is enabled, attempting hook...");
            try {
                citizensHook = new CitizensHook(plugin);
                if (citizensHook.ensureAvailable()) {
                    plugin.getLogger().info("Hooked into Citizens " + citizensHook.getVersion() + " for replay NPCs.");
                } else {
                    plugin.logDebug("[Citizens] ensureAvailable() returned false");
                    citizensHook = null;
                }
            } catch (Exception e) {
                plugin.logDebug("[Citizens] Lazy init exception: " + e.getMessage());
                e.printStackTrace();
                citizensHook = null;
            }
        } else {
            plugin.logDebug("[Citizens] Citizens plugin not found or not enabled");
            // List all plugins for debugging
            plugin.logDebug("[Citizens] Available plugins: " + String.join(", ",
                java.util.Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .map(p -> p.getName() + (p.isEnabled() ? "(enabled)" : "(disabled)"))
                    .toArray(String[]::new)));
        }
    }

    public String getCitizensVersion() {
        return citizensHook != null ? citizensHook.getVersion() : "N/A";
    }

    public boolean isEssentialsAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("Essentials") ||
               Bukkit.getPluginManager().isPluginEnabled("EssentialsX");
    }

    public String getEssentialsVersion() {
        var essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials == null) {
            essentials = Bukkit.getPluginManager().getPlugin("EssentialsX");
        }
        return essentials != null ? essentials.getDescription().getVersion() : null;
    }

    public boolean isSparkAvailable() {
        return sparkHook != null && sparkHook.isAvailable();
    }

    public String getSparkVersion() {
        return sparkHook != null ? sparkHook.getVersion() : null;
    }

    public SparkHook getSparkHook() {
        return sparkHook;
    }

    public boolean isVoiceChatAvailable() {
        return voiceChatHook != null && voiceChatHook.isAvailable();
    }

    public String getVoiceChatVersion() {
        return voiceChatHook != null ? voiceChatHook.getVersion() : null;
    }

    public SimpleVoiceChatHook getVoiceChatHook() {
        return voiceChatHook;
    }

    public boolean isBlueMapAvailable() {
        return blueMapHook != null && blueMapHook.isAvailable();
    }

    public String getBlueMapVersion() {
        return blueMapHook != null ? blueMapHook.getVersion() : null;
    }

    public BlueMapHook getBlueMapHook() {
        return blueMapHook;
    }

    public void shutdown() {
        if (placeholderAPIHook != null) {
            placeholderAPIHook.unregister();
        }
        if (anticheatManager != null) {
            anticheatManager.shutdown();
        }
        if (citizensHook != null) {
            citizensHook.shutdown();
        }
    }
}

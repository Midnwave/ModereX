package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Hook for Spark profiler integration.
 * Detects Spark as: (1) standalone plugin, (2) Paper bundled API, (3) command fallback.
 */
public class SparkHook {

    private final ModereX plugin;
    private Plugin sparkPlugin;
    private boolean available = false;
    private String version;
    private String detectionMethod = "none"; // "plugin", "api", "command"
    private Object sparkApiInstance;

    public SparkHook(ModereX plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        // Method 1: Check for Spark as a standalone plugin
        try {
            sparkPlugin = Bukkit.getPluginManager().getPlugin("spark");
            if (sparkPlugin != null && sparkPlugin.isEnabled()) {
                available = true;
                version = sparkPlugin.getDescription().getVersion();
                detectionMethod = "plugin";
                plugin.getLogger().info("Spark integration enabled via plugin (v" + version + ")");
                tryInitApi();
                return;
            }
        } catch (Exception e) {
            plugin.logDebug("Spark plugin check failed: " + e.getMessage());
        }

        // Method 2: Check for Spark API class presence (Paper 1.20.4+ bundles Spark)
        try {
            Class<?> sparkClass = Class.forName("me.lucko.spark.api.Spark");
            var registration = Bukkit.getServicesManager().getRegistration(sparkClass);
            if (registration != null) {
                sparkApiInstance = registration.getProvider();
                available = true;
                detectionMethod = "api";
                // Try to get version from API
                try {
                    var platformMethod = sparkApiInstance.getClass().getMethod("platform");
                    var platform = platformMethod.invoke(sparkApiInstance);
                    if (platform != null) {
                        var versionMethod = platform.getClass().getMethod("getVersion");
                        version = String.valueOf(versionMethod.invoke(platform));
                    }
                } catch (Exception ignored) {
                    version = "bundled";
                }
                plugin.getLogger().info("Spark integration enabled via API (Paper bundled" +
                        (version != null ? ", v" + version : "") + ")");
                return;
            }
        } catch (ClassNotFoundException e) {
            plugin.logDebug("Spark API class not on classpath");
        } catch (Exception e) {
            plugin.logDebug("Spark API service check failed: " + e.getMessage());
        }

        // Method 3: Check if spark command exists (last resort fallback)
        try {
            var commandMap = Bukkit.getServer().getCommandMap();
            var sparkCmd = commandMap.getCommand("spark");
            if (sparkCmd != null) {
                available = true;
                detectionMethod = "command";
                version = "unknown";
                plugin.getLogger().info("Spark integration enabled via command fallback");
                return;
            }
        } catch (Exception e) {
            plugin.logDebug("Spark command check failed: " + e.getMessage());
        }

        plugin.logDebug("Spark not detected via any method");
    }

    private void tryInitApi() {
        if (sparkApiInstance != null) return;
        try {
            Class<?> sparkClass = Class.forName("me.lucko.spark.api.Spark");
            var registration = Bukkit.getServicesManager().getRegistration(sparkClass);
            if (registration != null) {
                sparkApiInstance = registration.getProvider();
            }
        } catch (Exception ignored) {}
    }

    public boolean isAvailable() {
        return available;
    }

    public String getVersion() {
        return version;
    }

    public String getDetectionMethod() {
        return detectionMethod;
    }

    public String startProfile() {
        if (!available) return null;
        try {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler --timeout 30");
            });
            return "Profile started - check console for viewer URL";
        } catch (Exception e) {
            plugin.logError("Failed to start Spark profile", e);
            return null;
        }
    }

    public String heapDump() {
        if (!available) return null;
        try {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark heapdump");
            });
            return "Heap dump started - check console for viewer URL";
        } catch (Exception e) {
            plugin.logError("Failed to generate Spark heap dump", e);
            return null;
        }
    }

    public String triggerGC() {
        if (!available) return null;
        try {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark gc");
            });
            return "GC triggered";
        } catch (Exception e) {
            plugin.logError("Failed to trigger Spark GC", e);
            return null;
        }
    }

    /**
     * Get TPS from Spark API if available. Returns null if not accessible.
     * Array: [tps5s, tps10s, tps1m, tps5m, tps15m] (available entries vary)
     */
    public double[] getTps() {
        if (!available || sparkApiInstance == null) return null;
        try {
            var tpsMethod = sparkApiInstance.getClass().getMethod("tps");
            var tpsResult = tpsMethod.invoke(sparkApiInstance);
            if (tpsResult == null) return null;

            // TPS poll returns GenericStatistic<DoubleAverageInfo>
            // Try to get the 10-second average
            var pollMethod = tpsResult.getClass().getMethod("poll",
                    Class.forName("me.lucko.spark.api.statistic.misc.DoubleAverageInfo$Duration"));
            var durationClass = Class.forName("me.lucko.spark.api.statistic.misc.DoubleAverageInfo$Duration");
            var durations = durationClass.getEnumConstants();

            double[] values = new double[durations.length];
            for (int i = 0; i < durations.length; i++) {
                var avg = pollMethod.invoke(tpsResult, durations[i]);
                if (avg != null) {
                    var meanMethod = avg.getClass().getMethod("mean");
                    values[i] = (double) meanMethod.invoke(avg);
                }
            }
            return values;
        } catch (Exception e) {
            plugin.logDebug("Could not get TPS from Spark API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get MSPT from Spark API if available.
     */
    public double getMspt() {
        if (!available || sparkApiInstance == null) return -1;
        try {
            var msptMethod = sparkApiInstance.getClass().getMethod("mspt");
            var msptResult = msptMethod.invoke(sparkApiInstance);
            if (msptResult == null) return -1;

            var pollMethod = msptResult.getClass().getMethod("poll",
                    Class.forName("me.lucko.spark.api.statistic.misc.DoubleAverageInfo$Duration"));
            var durationClass = Class.forName("me.lucko.spark.api.statistic.misc.DoubleAverageInfo$Duration");
            var durations = durationClass.getEnumConstants();

            // Return the first (shortest) duration average
            if (durations.length > 0) {
                var avg = pollMethod.invoke(msptResult, durations[0]);
                if (avg != null) {
                    var meanMethod = avg.getClass().getMethod("mean");
                    return (double) meanMethod.invoke(avg);
                }
            }
        } catch (Exception e) {
            plugin.logDebug("Could not get MSPT from Spark API: " + e.getMessage());
        }
        return -1;
    }
}

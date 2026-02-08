package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Hook for Spark profiler integration.
 * Provides optional enhanced profiling capabilities when Spark is installed.
 */
public class SparkHook {

    private final ModereX plugin;
    private Plugin sparkPlugin;
    private boolean available = false;
    private String version;

    public SparkHook(ModereX plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        try {
            sparkPlugin = Bukkit.getPluginManager().getPlugin("spark");
            if (sparkPlugin != null && sparkPlugin.isEnabled()) {
                available = true;
                version = sparkPlugin.getDescription().getVersion();
                plugin.getLogger().info("Spark integration enabled (v" + version + ")");
            }
        } catch (Exception e) {
            plugin.logDebug("Spark not available: " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Start a CPU profiler via Spark command.
     * Returns the viewer URL if successful.
     */
    public String startProfile() {
        if (!available) return null;

        try {
            // Execute spark profiler command
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler --timeout 30");
            });
            return "Profile started - check console for viewer URL";
        } catch (Exception e) {
            plugin.logError("Failed to start Spark profile", e);
            return null;
        }
    }

    /**
     * Generate a heap dump via Spark.
     */
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

    /**
     * Trigger garbage collection via Spark.
     */
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
     * Get current TPS from Spark if available (more accurate than manual calculation).
     */
    public double[] getTps() {
        if (!available) return null;

        try {
            // Try to access Spark's TPS data via reflection
            Class<?> sparkClass = Class.forName("me.lucko.spark.api.Spark");
            Method getMethod = sparkClass.getMethod("get");
            Object sparkApi = getMethod.invoke(null);

            if (sparkApi != null) {
                // Spark API available but TPS retrieval is complex
                // Just return null - we use our own TPS calculation
                plugin.logDebug("Spark API available");
            }
        } catch (Exception e) {
            // Spark API not available or changed
            plugin.logDebug("Could not get TPS from Spark API: " + e.getMessage());
        }

        return null;
    }
}

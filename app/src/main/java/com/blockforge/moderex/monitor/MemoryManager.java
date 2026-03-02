package com.blockforge.moderex.monitor;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.LinkedList;
import java.util.List;

/**
 * Memory management system with 4-level pressure response.
 * Monitors heap usage, detects memory leaks, and recommends GC tuning.
 */
public class MemoryManager {

    private final ModereX plugin;
    private BukkitTask monitorTask;
    private int currentPressureLevel = 0; // 0=normal, 1-4=pressure levels

    // Memory trend analysis
    private final LinkedList<Long> heapSamples = new LinkedList<>();
    private static final int TREND_WINDOW = 120; // 2 minutes of 1-second samples

    // GC tracking
    private long lastGcTotalTime = 0;
    private final LinkedList<GcPauseEvent> gcPauseHistory = new LinkedList<>();
    private static final int MAX_GC_PAUSE_HISTORY = 60;

    // Configurable thresholds
    private double level1Threshold = 0.70;
    private double level2Threshold = 0.80;
    private double level3Threshold = 0.90;
    private double level4Threshold = 0.95;

    // Track if we've already notified at each level to avoid spam
    private int lastNotifiedLevel = 0;

    public MemoryManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Check memory every second (20 ticks)
        monitorTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkMemory, 20L, 20L);
        plugin.getLogger().info("Memory management system started");
    }

    public void stop() {
        if (monitorTask != null) {
            monitorTask.cancel();
            monitorTask = null;
        }
    }

    private void checkMemory() {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long used = memBean.getHeapMemoryUsage().getUsed();
        long max = memBean.getHeapMemoryUsage().getMax();
        double ratio = max > 0 ? (double) used / max : 0;

        // Track heap samples for trend analysis
        heapSamples.addLast(used);
        while (heapSamples.size() > TREND_WINDOW) heapSamples.removeFirst();

        // Track GC pauses
        trackGcPauses();

        // Determine pressure level
        int newLevel;
        if (ratio >= level4Threshold) newLevel = 4;
        else if (ratio >= level3Threshold) newLevel = 3;
        else if (ratio >= level2Threshold) newLevel = 2;
        else if (ratio >= level1Threshold) newLevel = 1;
        else newLevel = 0;

        if (newLevel != currentPressureLevel) {
            handlePressureChange(currentPressureLevel, newLevel);
            currentPressureLevel = newLevel;
        }
    }

    private void handlePressureChange(int oldLevel, int newLevel) {
        if (newLevel > lastNotifiedLevel) {
            String percent = String.format("%.0f", getHeapPercent()) + "%";
            notifyStaff("<dark_gray>[<red>MEMORY</red>]</dark_gray> <yellow>Pressure level " + newLevel + " (" + percent + " used)");
            lastNotifiedLevel = newLevel;
        }

        if (newLevel < oldLevel) {
            // Recovering - reset notification level
            lastNotifiedLevel = newLevel;
            if (newLevel == 0) {
                notifyStaff("<dark_gray>[<green>MEMORY</green>]</dark_gray> <yellow>Memory pressure returned to normal");
            }
        }

        // Apply pressure response actions (on main thread)
        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (newLevel) {
                case 1 -> {
                    // Gentle: suggest GC
                    System.gc();
                }
                case 2 -> {
                    // Moderate: reduce chunk loading
                    if (plugin.getPerformanceSettingsManager() != null) {
                        plugin.getPerformanceSettingsManager().reduceChunkLoading();
                    }
                }
                case 3 -> {
                    // Severe: force chunk unloads
                    for (World world : Bukkit.getWorlds()) {
                        world.setKeepSpawnInMemory(false);
                    }
                    System.gc();
                }
                case 4 -> {
                    // Emergency: engage all throttles
                    if (plugin.getTpsThrottleManager() != null) {
                        plugin.getTpsThrottleManager().emergencyEngage();
                    }
                    for (World world : Bukkit.getWorlds()) {
                        world.setKeepSpawnInMemory(false);
                    }
                    System.gc();
                }
            }
        });

        broadcastMemoryStatus();
    }

    private void trackGcPauses() {
        long totalGcTime = 0;
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gcBean.getCollectionTime() >= 0) {
                totalGcTime += gcBean.getCollectionTime();
            }
        }
        long pauseMs = totalGcTime - lastGcTotalTime;
        if (pauseMs < 0) pauseMs = 0;
        if (pauseMs > 0) {
            gcPauseHistory.addLast(new GcPauseEvent(System.currentTimeMillis(), pauseMs));
            while (gcPauseHistory.size() > MAX_GC_PAUSE_HISTORY) gcPauseHistory.removeFirst();
        }
        lastGcTotalTime = totalGcTime;
    }

    /**
     * Detect potential memory leak: sustained growth over the trend window.
     */
    public boolean detectMemoryLeak() {
        if (heapSamples.size() < TREND_WINDOW / 2) return false;
        long first = heapSamples.getFirst();
        long last = heapSamples.getLast();
        double growthRate = (double)(last - first) / heapSamples.size(); // bytes per second
        return growthRate > 1_000_000; // > 1MB/sec sustained growth
    }

    /**
     * Get JVM flag recommendations.
     */
    public JsonObject getGcRecommendations() {
        JsonObject recommendations = new JsonObject();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        List<String> args = runtimeBean.getInputArguments();
        String argsStr = String.join(" ", args);

        JsonArray tips = new JsonArray();

        boolean hasAikarFlags = argsStr.contains("-XX:+UseG1GC") && argsStr.contains("MaxGCPauseMillis");
        boolean hasZGC = argsStr.contains("-XX:+UseZGC");
        boolean hasShenandoah = argsStr.contains("-XX:+UseShenandoahGC");

        if (!hasAikarFlags && !hasZGC && !hasShenandoah) {
            tips.add("Consider using Aikar's JVM flags for optimized GC performance");
        }

        if (!argsStr.contains("-XX:+ParallelRefProcEnabled")) {
            tips.add("Add -XX:+ParallelRefProcEnabled for faster reference processing");
        }

        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long maxMb = memBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        if (maxMb < 4096) {
            tips.add("Server has " + maxMb + "MB max heap. Consider 6-10GB for production servers");
        }

        if (hasZGC) {
            tips.add("Using ZGC - good for low-latency. Ensure Java 17+ for best performance");
        } else if (hasShenandoah) {
            tips.add("Using Shenandoah GC - good for low-latency workloads");
        }

        recommendations.add("tips", tips);
        recommendations.addProperty("currentGc", hasZGC ? "ZGC" : hasShenandoah ? "Shenandoah" : hasAikarFlags ? "G1GC (Aikar)" : "Default");
        recommendations.addProperty("maxHeapMb", maxMb);

        // Current JVM flags
        JsonArray flagsArr = new JsonArray();
        for (String arg : args) {
            if (arg.startsWith("-X") || arg.startsWith("-D")) {
                flagsArr.add(arg);
            }
        }
        recommendations.add("jvmFlags", flagsArr);

        return recommendations;
    }

    /**
     * Get full memory status as JSON for the web panel.
     */
    public JsonObject getStatusJson() {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long used = memBean.getHeapMemoryUsage().getUsed();
        long max = memBean.getHeapMemoryUsage().getMax();
        long committed = memBean.getHeapMemoryUsage().getCommitted();

        JsonObject status = new JsonObject();
        status.addProperty("pressureLevel", currentPressureLevel);
        status.addProperty("usedBytes", used);
        status.addProperty("maxBytes", max);
        status.addProperty("committedBytes", committed);
        status.addProperty("usedMb", used / (1024 * 1024));
        status.addProperty("maxMb", max / (1024 * 1024));
        status.addProperty("heapPercent", max > 0 ? Math.round((double) used / max * 100) : 0);
        status.addProperty("leakDetected", detectMemoryLeak());

        // Thresholds
        status.addProperty("level1Threshold", level1Threshold * 100);
        status.addProperty("level2Threshold", level2Threshold * 100);
        status.addProperty("level3Threshold", level3Threshold * 100);
        status.addProperty("level4Threshold", level4Threshold * 100);

        // Memory pools
        JsonArray pools = new JsonArray();
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == java.lang.management.MemoryType.HEAP) {
                JsonObject p = new JsonObject();
                p.addProperty("name", pool.getName());
                p.addProperty("used", pool.getUsage().getUsed() / (1024 * 1024));
                p.addProperty("max", pool.getUsage().getMax() > 0 ? pool.getUsage().getMax() / (1024 * 1024) : -1);
                pools.add(p);
            }
        }
        status.add("memoryPools", pools);

        // Recent GC pauses
        JsonArray pauses = new JsonArray();
        for (GcPauseEvent event : gcPauseHistory) {
            JsonObject e = new JsonObject();
            e.addProperty("timestamp", event.timestamp);
            e.addProperty("pauseMs", event.pauseMs);
            pauses.add(e);
        }
        status.add("gcPauses", pauses);

        // GC bean info
        JsonArray gcBeans = new JsonArray();
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            JsonObject g = new JsonObject();
            g.addProperty("name", gcBean.getName());
            g.addProperty("count", gcBean.getCollectionCount());
            g.addProperty("timeMs", gcBean.getCollectionTime());
            gcBeans.add(g);
        }
        status.add("gcCollectors", gcBeans);

        return status;
    }

    public void updateThresholds(JsonObject data) {
        if (data.has("level1")) level1Threshold = data.get("level1").getAsDouble() / 100.0;
        if (data.has("level2")) level2Threshold = data.get("level2").getAsDouble() / 100.0;
        if (data.has("level3")) level3Threshold = data.get("level3").getAsDouble() / 100.0;
        if (data.has("level4")) level4Threshold = data.get("level4").getAsDouble() / 100.0;
    }

    private double getHeapPercent() {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long used = memBean.getHeapMemoryUsage().getUsed();
        long max = memBean.getHeapMemoryUsage().getMax();
        return max > 0 ? (double) used / max * 100 : 0;
    }

    private void notifyStaff(String message) {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(staff, "moderex.notify.memory")) {
                Msg.send(staff, TextUtil.parse(message));
            }
        }
    }

    private void broadcastMemoryStatus() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastMessage("MEMORY_STATUS_UPDATE", getStatusJson());
        }
    }

    public int getCurrentPressureLevel() { return currentPressureLevel; }

    record GcPauseEvent(long timestamp, long pauseMs) {}
}

package com.blockforge.moderex.monitor;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStatusManager {

    private final ModereX plugin;
    private BukkitTask monitorTask;
    private BukkitTask broadcastTask;

    // TPS calculation
    private final LinkedList<Long> tpsHistory = new LinkedList<>();
    private long lastTickTime = System.currentTimeMillis();
    private double currentTps = 20.0;
    private double averageTps = 20.0;

    // Performance data
    private double cpuUsage = 0.0;
    private long usedMemory = 0;
    private long maxMemory = 0;
    private int loadedChunks = 0;
    private int entityCount = 0;
    private final long startupTime = System.currentTimeMillis();

    // Lag detection
    private final Map<String, LaggyChunk> laggyChunks = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerLagInfo> playerLagInfo = new ConcurrentHashMap<>();
    private final List<LagAlert> recentAlerts = new ArrayList<>();

    // Health score
    private int healthScore = 100;

    // GC tracking
    private long lastGcCount = 0;
    private long lastGcTime = 0;
    private final LinkedList<GcSnapshot> gcHistory = new LinkedList<>();
    private static final int MAX_GC_HISTORY = 60; // 60 data points (1 per second)

    // Memory history for heap graph
    private final LinkedList<MemorySnapshot> memoryHistory = new LinkedList<>();
    private static final int MAX_MEMORY_HISTORY = 60;

    // 3-tier TPS alert thresholds
    private double tpsWarningThreshold = 18.0;
    private double tpsCriticalThreshold = 15.0;
    private double tpsEmergencyThreshold = 10.0;
    private String currentAlertTier = "NONE"; // NONE, MILD, WARNING, CRITICAL

    // Settings
    private boolean enabled = true;
    private int chunkEntityThreshold = 100;
    private int chunkTileEntityThreshold = 50;
    private double tpsAlertThreshold = 18.0;

    public ServerStatusManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!enabled) return;

        // TPS monitoring task - runs every tick
        monitorTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);

        // Performance analysis task - runs every second
        broadcastTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            analyzePerformance();
            detectLaggySources();
            broadcastStatus();
        }, 20L, 20L);

        plugin.getLogger().info("Server status monitor started");
    }

    public void stop() {
        if (monitorTask != null) {
            monitorTask.cancel();
            monitorTask = null;
        }
        if (broadcastTask != null) {
            broadcastTask.cancel();
            broadcastTask = null;
        }
    }

    private void tick() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastTickTime;
        lastTickTime = now;

        // Calculate TPS based on elapsed time
        if (elapsed > 0) {
            double tickTps = 1000.0 / elapsed;
            tpsHistory.addLast(now);

            // Keep only last 20 seconds of history
            while (!tpsHistory.isEmpty() && now - tpsHistory.getFirst() > 20000) {
                tpsHistory.removeFirst();
            }

            // Calculate rolling TPS
            if (tpsHistory.size() >= 2) {
                long oldest = tpsHistory.getFirst();
                long newest = tpsHistory.getLast();
                long duration = newest - oldest;
                if (duration > 0) {
                    currentTps = Math.min(20.0, (tpsHistory.size() - 1) * 1000.0 / duration);
                }
            }
        }
    }

    private void analyzePerformance() {
        // Memory stats
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        maxMemory = memoryBean.getHeapMemoryUsage().getMax();

        // CPU usage
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            cpuUsage = sunBean.getProcessCpuLoad() * 100;
        } else {
            cpuUsage = osBean.getSystemLoadAverage();
        }

        // Count loaded chunks and entities
        loadedChunks = 0;
        entityCount = 0;
        for (World world : Bukkit.getWorlds()) {
            loadedChunks += world.getLoadedChunks().length;
            entityCount += world.getEntities().size();
        }

        // Calculate average TPS over last 5 seconds
        if (!tpsHistory.isEmpty()) {
            long fiveSecondsAgo = System.currentTimeMillis() - 5000;
            long count = tpsHistory.stream().filter(t -> t > fiveSecondsAgo).count();
            averageTps = Math.min(20.0, count / 5.0);
        }

        // Calculate health score (0-100)
        double tpsFactor = Math.min(1.0, currentTps / 20.0);
        double memFactor = maxMemory > 0 ? 1.0 - ((double) usedMemory / maxMemory) : 1.0;
        double entityFactor = entityCount < 5000 ? 1.0 : Math.max(0.0, 1.0 - (entityCount - 5000) / 15000.0);
        double chunkFactor = loadedChunks < 1000 ? 1.0 : Math.max(0.0, 1.0 - (loadedChunks - 1000) / 9000.0);
        healthScore = (int) Math.round(tpsFactor * 40 + memFactor * 30 + entityFactor * 20 + chunkFactor * 10);

        // Track GC metrics
        long now = System.currentTimeMillis();
        long totalGcCount = 0;
        long totalGcTime = 0;
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gcBean.getCollectionCount() >= 0) totalGcCount += gcBean.getCollectionCount();
            if (gcBean.getCollectionTime() >= 0) totalGcTime += gcBean.getCollectionTime();
        }
        long pauseMs = totalGcTime - lastGcTime;
        if (pauseMs < 0) pauseMs = 0;
        gcHistory.addLast(new GcSnapshot(now, totalGcCount, totalGcTime, pauseMs));
        while (gcHistory.size() > MAX_GC_HISTORY) gcHistory.removeFirst();
        lastGcCount = totalGcCount;
        lastGcTime = totalGcTime;

        // Track memory history
        long edenMb = 0, oldGenMb = 0;
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            String name = pool.getName().toLowerCase();
            long used = pool.getUsage().getUsed() / (1024 * 1024);
            if (name.contains("eden")) edenMb = used;
            else if (name.contains("old") || name.contains("tenured")) oldGenMb = used;
        }
        memoryHistory.addLast(new MemorySnapshot(now, usedMemory / (1024 * 1024), maxMemory / (1024 * 1024), edenMb, oldGenMb));
        while (memoryHistory.size() > MAX_MEMORY_HISTORY) memoryHistory.removeFirst();

        // 3-tier TPS alert check
        String newTier;
        if (currentTps < tpsEmergencyThreshold) newTier = "CRITICAL";
        else if (currentTps < tpsCriticalThreshold) newTier = "WARNING";
        else if (currentTps < tpsWarningThreshold) newTier = "MILD";
        else newTier = "NONE";

        if (!newTier.equals(currentAlertTier) && !newTier.equals("NONE")) {
            String alertMsg = switch (newTier) {
                case "MILD" -> "TPS below " + String.format("%.0f", tpsWarningThreshold) + " (" + String.format("%.1f", currentTps) + ")";
                case "WARNING" -> "TPS dropped to " + String.format("%.1f", currentTps) + " - performance degraded";
                case "CRITICAL" -> "CRITICAL: TPS at " + String.format("%.1f", currentTps) + " - severe lag detected";
                default -> "TPS: " + String.format("%.1f", currentTps);
            };
            addAlert(new LagAlert("TPS_" + newTier, alertMsg, null, now));
        }
        currentAlertTier = newTier;
    }

    private void detectLaggySources() {
        laggyChunks.clear();

        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                int entities = chunk.getEntities().length;
                int tileEntities = chunk.getTileEntities().length;

                boolean isLaggy = false;
                List<String> reasons = new ArrayList<>();

                if (entities >= chunkEntityThreshold) {
                    isLaggy = true;
                    reasons.add(entities + " entities");
                }

                if (tileEntities >= chunkTileEntityThreshold) {
                    isLaggy = true;
                    reasons.add(tileEntities + " tile entities");
                }

                if (isLaggy) {
                    String key = world.getName() + ":" + chunk.getX() + ":" + chunk.getZ();
                    laggyChunks.put(key, new LaggyChunk(
                            world.getName(),
                            chunk.getX(),
                            chunk.getZ(),
                            entities,
                            tileEntities,
                            String.join(", ", reasons)
                    ));
                }
            }
        }

        // Check for TPS drops
        if (currentTps < tpsAlertThreshold && currentTps < averageTps - 2) {
            addAlert(new LagAlert(
                    "LOW_TPS",
                    "TPS dropped to " + String.format("%.1f", currentTps),
                    null,
                    System.currentTimeMillis()
            ));
        }

        // Detect potential lag machines per player area
        for (Player player : Bukkit.getOnlinePlayers()) {
            Chunk chunk = player.getLocation().getChunk();
            int nearbyEntities = 0;
            int nearbyRedstone = 0;

            // Check 3x3 chunk area around player
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Chunk nearby = player.getWorld().getChunkAt(chunk.getX() + dx, chunk.getZ() + dz);
                    if (nearby.isLoaded()) {
                        nearbyEntities += nearby.getEntities().length;
                        for (BlockState tile : nearby.getTileEntities()) {
                            if (isRedstoneTile(tile)) {
                                nearbyRedstone++;
                            }
                        }
                    }
                }
            }

            PlayerLagInfo info = new PlayerLagInfo(
                    player.getUniqueId(),
                    player.getName(),
                    nearbyEntities,
                    nearbyRedstone,
                    nearbyEntities > 300 || nearbyRedstone > 100
            );
            playerLagInfo.put(player.getUniqueId(), info);

            if (info.potentialLagMachine) {
                addAlert(new LagAlert(
                        "LAG_MACHINE",
                        "Potential lag machine near " + player.getName() + " (" + nearbyEntities + " entities, " + nearbyRedstone + " redstone)",
                        player.getUniqueId(),
                        System.currentTimeMillis()
                ));
            }
        }
    }

    private boolean isRedstoneTile(BlockState tile) {
        String type = tile.getType().name().toLowerCase();
        return type.contains("piston") || type.contains("hopper") ||
                type.contains("dropper") || type.contains("dispenser") ||
                type.contains("observer") || type.contains("comparator") ||
                type.contains("repeater");
    }

    private void addAlert(LagAlert alert) {
        // Avoid duplicate recent alerts
        long fiveMinutesAgo = System.currentTimeMillis() - 300000;
        recentAlerts.removeIf(a -> a.timestamp < fiveMinutesAgo);

        boolean duplicate = recentAlerts.stream().anyMatch(a ->
                a.type.equals(alert.type) &&
                        Objects.equals(a.playerUuid, alert.playerUuid) &&
                        a.timestamp > System.currentTimeMillis() - 60000);

        if (!duplicate) {
            recentAlerts.add(alert);

            // Notify staff
            if (plugin.getConfigManager().getSettings().isServerStatusAlertsEnabled()) {
                notifyStaff(alert);
            }
        }
    }

    private void notifyStaff(LagAlert alert) {
        String message = switch (alert.type) {
            case "LOW_TPS" -> "<dark_gray>[<red>LAG</red>]</dark_gray> <yellow>" + alert.message;
            case "LAG_MACHINE" -> "<dark_gray>[<gold>WARN</gold>]</dark_gray> <yellow>" + alert.message;
            default -> "<dark_gray>[<gray>STATUS</gray>]</dark_gray> " + alert.message;
        };

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(staff, "moderex.notify.status")) {
                Msg.send(staff, com.blockforge.moderex.util.TextUtil.parse(message));
            }
        }
    }

    private void broadcastStatus() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastServerStatus(getStatusJson());
        }
    }

    public JsonObject getStatusJson() {
        JsonObject status = new JsonObject();

        // TPS
        status.addProperty("tps", Math.round(currentTps * 10) / 10.0);
        status.addProperty("averageTps", Math.round(averageTps * 10) / 10.0);

        // Memory
        status.addProperty("usedMemory", usedMemory / (1024 * 1024)); // MB
        status.addProperty("maxMemory", maxMemory / (1024 * 1024)); // MB
        status.addProperty("memoryPercent", maxMemory > 0 ? Math.round((double) usedMemory / maxMemory * 100) : 0);

        // CPU
        status.addProperty("cpuUsage", Math.round(cpuUsage * 10) / 10.0);

        // Counts
        status.addProperty("loadedChunks", loadedChunks);
        status.addProperty("entityCount", entityCount);
        status.addProperty("onlinePlayers", Bukkit.getOnlinePlayers().size());
        status.addProperty("maxPlayers", Bukkit.getMaxPlayers());

        // Server info
        status.addProperty("serverVersion", Bukkit.getVersion());
        status.addProperty("bukkitVersion", Bukkit.getBukkitVersion());
        status.addProperty("uptime", System.currentTimeMillis() - startupTime);

        // Health score
        status.addProperty("healthScore", healthScore);
        status.addProperty("alertTier", currentAlertTier);

        // Java info
        Runtime runtime = Runtime.getRuntime();
        status.addProperty("javaVersion", System.getProperty("java.version"));
        status.addProperty("availableProcessors", runtime.availableProcessors());

        // GC history
        JsonArray gcArr = new JsonArray();
        for (GcSnapshot gc : gcHistory) {
            JsonObject g = new JsonObject();
            g.addProperty("t", gc.timestamp());
            g.addProperty("count", gc.totalCollections());
            g.addProperty("timeMs", gc.totalTimeMs());
            g.addProperty("pauseMs", gc.pauseMs());
            gcArr.add(g);
        }
        status.add("gcHistory", gcArr);

        // Memory history (for heap graph)
        JsonArray memArr = new JsonArray();
        for (MemorySnapshot ms : memoryHistory) {
            JsonObject m = new JsonObject();
            m.addProperty("t", ms.timestamp());
            m.addProperty("used", ms.usedMb());
            m.addProperty("max", ms.maxMb());
            m.addProperty("eden", ms.edenMb());
            m.addProperty("oldGen", ms.oldGenMb());
            memArr.add(m);
        }
        status.add("memoryHistory", memArr);

        // Database size (in bytes for frontend formatting)
        long dbSizeBytes = plugin.getDatabaseManager().getDatabaseSizeBytes();
        status.addProperty("databaseSizeBytes", dbSizeBytes);
        status.addProperty("databaseSize", Math.round(dbSizeBytes / (1024.0 * 1024.0) * 100) / 100.0); // MB for backwards compat
        status.addProperty("databaseMaxSize", 10.0); // 10MB limit for free tier
        status.addProperty("databaseMaxSizeBytes", 10L * 1024L * 1024L); // 10MB in bytes

        // Player ping statistics
        var onlinePlayers = Bukkit.getOnlinePlayers();
        if (!onlinePlayers.isEmpty()) {
            int totalPing = 0;
            int minPing = Integer.MAX_VALUE;
            int maxPing = 0;
            JsonArray playerList = new JsonArray();

            for (Player player : onlinePlayers) {
                int ping = player.getPing();
                totalPing += ping;
                minPing = Math.min(minPing, ping);
                maxPing = Math.max(maxPing, ping);

                JsonObject p = new JsonObject();
                p.addProperty("uuid", player.getUniqueId().toString());
                p.addProperty("name", player.getName());
                p.addProperty("ping", ping);
                p.addProperty("world", player.getWorld().getName());
                p.addProperty("x", Math.round(player.getLocation().getX()));
                p.addProperty("y", Math.round(player.getLocation().getY()));
                p.addProperty("z", Math.round(player.getLocation().getZ()));
                p.addProperty("gamemode", player.getGameMode().name());
                p.addProperty("health", Math.round(player.getHealth() * 10) / 10.0);
                p.addProperty("food", player.getFoodLevel());
                playerList.add(p);
            }

            status.addProperty("averagePing", totalPing / onlinePlayers.size());
            status.addProperty("minPing", minPing == Integer.MAX_VALUE ? 0 : minPing);
            status.addProperty("maxPing", maxPing);
            status.add("playerDetails", playerList);
        } else {
            status.addProperty("averagePing", 0);
            status.addProperty("minPing", 0);
            status.addProperty("maxPing", 0);
            status.add("playerDetails", new JsonArray());
        }

        // Worlds
        JsonArray worlds = new JsonArray();
        for (World world : Bukkit.getWorlds()) {
            JsonObject w = new JsonObject();
            w.addProperty("name", world.getName());
            w.addProperty("chunks", world.getLoadedChunks().length);
            w.addProperty("entities", world.getEntities().size());
            w.addProperty("players", world.getPlayers().size());
            worlds.add(w);
        }
        status.add("worlds", worlds);

        // Laggy chunks
        JsonArray laggy = new JsonArray();
        for (LaggyChunk chunk : laggyChunks.values()) {
            JsonObject c = new JsonObject();
            c.addProperty("world", chunk.world);
            c.addProperty("x", chunk.x);
            c.addProperty("z", chunk.z);
            c.addProperty("entities", chunk.entities);
            c.addProperty("tileEntities", chunk.tileEntities);
            c.addProperty("reason", chunk.reason);
            laggy.add(c);
        }
        status.add("laggyChunks", laggy);

        // Player lag info
        JsonArray playerLag = new JsonArray();
        for (PlayerLagInfo info : playerLagInfo.values()) {
            if (info.potentialLagMachine) {
                JsonObject p = new JsonObject();
                p.addProperty("uuid", info.playerUuid.toString());
                p.addProperty("name", info.playerName);
                p.addProperty("entities", info.nearbyEntities);
                p.addProperty("redstone", info.nearbyRedstone);
                playerLag.add(p);
            }
        }
        status.add("lagMachines", playerLag);

        // Recent alerts
        JsonArray alerts = new JsonArray();
        long fiveMinutesAgo = System.currentTimeMillis() - 300000;
        for (LagAlert alert : recentAlerts) {
            if (alert.timestamp > fiveMinutesAgo) {
                JsonObject a = new JsonObject();
                a.addProperty("type", alert.type);
                a.addProperty("message", alert.message);
                a.addProperty("timestamp", alert.timestamp);
                if (alert.playerUuid != null) {
                    a.addProperty("playerUuid", alert.playerUuid.toString());
                }
                alerts.add(a);
            }
        }
        status.add("alerts", alerts);

        return status;
    }

    // Getters/Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled && monitorTask == null) {
            start();
        } else if (!enabled && monitorTask != null) {
            stop();
        }
    }

    public double getCurrentTps() { return currentTps; }
    public double getCpuUsage() { return cpuUsage; }
    public long getUsedMemory() { return usedMemory; }
    public long getMaxMemory() { return maxMemory; }
    public int getHealthScore() { return healthScore; }

    public void setTpsWarningThreshold(double val) { this.tpsWarningThreshold = val; }
    public void setTpsCriticalThreshold(double val) { this.tpsCriticalThreshold = val; }
    public void setTpsEmergencyThreshold(double val) { this.tpsEmergencyThreshold = val; }

    // Inner classes
    record LaggyChunk(String world, int x, int z, int entities, int tileEntities, String reason) {}
    record PlayerLagInfo(UUID playerUuid, String playerName, int nearbyEntities, int nearbyRedstone, boolean potentialLagMachine) {}
    record LagAlert(String type, String message, UUID playerUuid, long timestamp) {}
    record GcSnapshot(long timestamp, long totalCollections, long totalTimeMs, long pauseMs) {}
    record MemorySnapshot(long timestamp, long usedMb, long maxMb, long edenMb, long oldGenMb) {}
}

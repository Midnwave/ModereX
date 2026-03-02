package com.blockforge.moderex.security;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Protects against bot raids, VPN joins, and suspicious connection patterns.
 * Integrates with BotDetector and IpReputationManager for comprehensive protection.
 */
public class RaidProtectionManager {

    private static final Gson GSON = new Gson();

    private final ModereX plugin;
    private final BotDetector botDetector;
    private final IpReputationManager ipReputationManager;
    private final HttpClient httpClient;

    // Raid detection state
    private boolean raidActive = false;
    private long raidStartTime = 0;
    private int raidJoinCount = 0;

    // Auto-lockdown
    private boolean autoLockdownEnabled = false;

    // CAPTCHA tracking
    private final Map<UUID, CaptchaChallenge> pendingCaptchas = new ConcurrentHashMap<>();

    // Configuration
    private int joinVelocityThreshold = 15;     // Joins per minute to trigger raid alert
    private int joinVelocityWindow = 60;        // Window in seconds
    private boolean vpnDetectionEnabled = false;
    private boolean captchaEnabled = false;
    private int botScoreThreshold = 60;         // Score above this = blocked
    private int autoLockdownThreshold = 30;     // Joins/min to auto-lockdown
    private long raidCooldownMs = 300000;       // 5 minutes cooldown after raid

    private ScheduledExecutorService scheduler;

    public RaidProtectionManager(ModereX plugin) {
        this.plugin = plugin;
        this.botDetector = new BotDetector(plugin);
        this.ipReputationManager = new IpReputationManager(plugin);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void initialize() {
        ipReputationManager.initialize();
        createTable();
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ModereX-RaidProtection");
            t.setDaemon(true);
            return t;
        });

        // Check for raid status every 10 seconds
        scheduler.scheduleAtFixedRate(this::checkRaidStatus, 10, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void createTable() {
        try {
            String sql;
            if (plugin.getDatabaseManager().isMySQL()) {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_raid_events (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        event_type VARCHAR(32) NOT NULL,
                        ip_address VARCHAR(45),
                        player_name VARCHAR(16),
                        player_uuid VARCHAR(36),
                        details TEXT,
                        created_at BIGINT NOT NULL,
                        INDEX idx_time (created_at)
                    )
                """;
            } else {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_raid_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_type VARCHAR(32) NOT NULL,
                        ip_address VARCHAR(45),
                        player_name VARCHAR(16),
                        player_uuid VARCHAR(36),
                        details TEXT,
                        created_at BIGINT NOT NULL
                    )
                """;
            }
            plugin.getDatabaseManager().update(sql);

            if (!plugin.getDatabaseManager().isMySQL()) {
                plugin.getDatabaseManager().update(
                        "CREATE INDEX IF NOT EXISTS idx_raid_time ON moderex_raid_events(created_at)");
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create raid_events table", e);
        }
    }

    /**
     * Check a pre-login event for raid protection.
     * Called from JoinQuitListener before ban checks.
     */
    public AsyncPlayerPreLoginEvent.Result checkPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        String name = event.getName();
        UUID uuid = event.getUniqueId();

        // Check IP reputation ban
        if (ipReputationManager.isBanned(ip)) {
            logRaidEvent("IP_BLOCKED", ip, name, uuid, "IP reputation ban");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("You are temporarily blocked. Please try again later."));
            return AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
        }

        // Check bot score
        int botScore = botDetector.analyzeJoin(name, ip);
        if (botScore >= botScoreThreshold) {
            ipReputationManager.addScore(ip, IpReputationManager.BOT_PATTERN, "Bot pattern detected (score: " + botScore + ")");
            logRaidEvent("BOT_BLOCKED", ip, name, uuid, "Bot score: " + botScore);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("Connection rejected. Please try again later."));
            return AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
        }

        // During active raid, be more strict
        if (raidActive) {
            if (botScore >= botScoreThreshold / 2) {
                logRaidEvent("RAID_BLOCKED", ip, name, uuid, "Blocked during raid (score: " + botScore + ")");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text("Server is under protection. Please try again in a few minutes."));
                return AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
            }
        }

        // Auto-lockdown check
        if (autoLockdownEnabled && !plugin.isGlobalLockdown()) {
            int velocity = botDetector.getJoinVelocity();
            if (velocity >= autoLockdownThreshold) {
                triggerAutoLockdown();
            }
        }

        // Record join in reputation
        ipReputationManager.recordJoin(ip);

        // VPN detection (async, don't block login)
        if (vpnDetectionEnabled) {
            checkVpnAsync(ip, uuid, name);
        }

        // CAPTCHA check - schedule for after join
        if (captchaEnabled && botScore >= botScoreThreshold / 3) {
            scheduleCaptcha(uuid, name);
        }

        return AsyncPlayerPreLoginEvent.Result.ALLOWED;
    }

    private void checkRaidStatus() {
        int velocity = botDetector.getJoinVelocity();

        if (!raidActive && velocity >= joinVelocityThreshold) {
            // Raid detected
            raidActive = true;
            raidStartTime = System.currentTimeMillis();
            raidJoinCount = velocity;

            logRaidEvent("RAID_STARTED", null, null, null, "Join velocity: " + velocity + "/min");
            notifyStaff("<red>Raid detected! Join velocity: " + velocity + "/min. Protection active.");

            // Broadcast to panel
            broadcastSecurityUpdate();
        } else if (raidActive) {
            if (velocity < joinVelocityThreshold / 2) {
                long elapsed = System.currentTimeMillis() - raidStartTime;
                if (elapsed > raidCooldownMs) {
                    // Raid has subsided
                    raidActive = false;
                    logRaidEvent("RAID_ENDED", null, null, null,
                            "Duration: " + (elapsed / 1000) + "s, joins blocked: " + raidJoinCount);
                    notifyStaff("<green>Raid protection disengaged. Server is safe.");
                    botDetector.reset();
                    broadcastSecurityUpdate();
                }
            } else {
                raidJoinCount += velocity;
            }
        }
    }

    private void checkVpnAsync(String ip, UUID uuid, String name) {
        CompletableFuture.runAsync(() -> {
            try {
                // Use ip-api.com (free, no key needed, 45 req/min)
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://ip-api.com/json/" + ip + "?fields=proxy,hosting"))
                        .timeout(Duration.ofSeconds(3))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject json = com.google.gson.JsonParser.parseString(response.body()).getAsJsonObject();
                    boolean isProxy = json.has("proxy") && json.get("proxy").getAsBoolean();
                    boolean isHosting = json.has("hosting") && json.get("hosting").getAsBoolean();

                    if (isProxy || isHosting) {
                        ipReputationManager.addScore(ip, IpReputationManager.VPN_DETECTED, "VPN/Proxy detected");
                        logRaidEvent("VPN_DETECTED", ip, name, uuid, "proxy=" + isProxy + ", hosting=" + isHosting);

                        // Notify staff
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            notifyStaff("<yellow>VPN/Proxy detected: " + name + " (" + ip + ")");
                        });
                    }
                }
            } catch (Exception ignored) {
                // VPN check failure is non-critical
            }
        });
    }

    private void scheduleCaptcha(UUID uuid, String name) {
        // Generate simple math CAPTCHA
        Random rand = new Random();
        int a = rand.nextInt(20) + 1;
        int b = rand.nextInt(20) + 1;
        int answer = a + b;

        CaptchaChallenge challenge = new CaptchaChallenge(a + " + " + b, String.valueOf(answer),
                System.currentTimeMillis() + 30000); // 30 second timeout
        pendingCaptchas.put(uuid, challenge);

        // Send after join
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                Msg.send(player, TextUtil.parse(
                        "<gradient:#f43f5e:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"));
                Msg.send(player, TextUtil.parse(
                        "<bold><red>Security Check</red></bold>"));
                Msg.send(player, TextUtil.parse(
                        "<gray>Please solve this to verify you're human:"));
                Msg.send(player, TextUtil.parse(
                        "<yellow><bold>What is " + a + " + " + b + "?</bold></yellow>"));
                Msg.send(player, TextUtil.parse(
                        "<gray>Type your answer in chat within 30 seconds."));
                Msg.send(player, TextUtil.parse(
                        "<gradient:#f43f5e:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"));
            }
        }, 40L); // 2 seconds after join
    }

    /**
     * Handle chat input for CAPTCHA verification.
     * Returns true if the message was consumed by CAPTCHA.
     */
    public boolean handleCaptchaInput(Player player, String message) {
        CaptchaChallenge challenge = pendingCaptchas.get(player.getUniqueId());
        if (challenge == null) return false;

        if (System.currentTimeMillis() > challenge.expiresAt) {
            pendingCaptchas.remove(player.getUniqueId());
            player.kick(Component.text("Security check timed out. Please rejoin."));
            return true;
        }

        if (message.trim().equals(challenge.answer)) {
            pendingCaptchas.remove(player.getUniqueId());
            Msg.send(player, TextUtil.parse("<green>Verification successful! Welcome."));
            return true;
        } else {
            ipReputationManager.addScore(
                    player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown",
                    IpReputationManager.FAILED_LOGIN, "Failed CAPTCHA");
            player.kick(Component.text("Incorrect answer. Please rejoin to try again."));
            pendingCaptchas.remove(player.getUniqueId());
            return true;
        }
    }

    public boolean hasPendingCaptcha(UUID uuid) {
        return pendingCaptchas.containsKey(uuid);
    }

    private void triggerAutoLockdown() {
        plugin.setGlobalLockdown(true);
        logRaidEvent("AUTO_LOCKDOWN", null, null, null, "Join velocity exceeded auto-lockdown threshold");
        notifyStaff("<red>Auto-lockdown triggered! Server is now in whitelist mode.");
        broadcastSecurityUpdate();

        // Auto-recovery after cooldown
        scheduler.schedule(() -> {
            int velocity = botDetector.getJoinVelocity();
            if (velocity < joinVelocityThreshold / 2) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.setGlobalLockdown(false);
                    notifyStaff("<green>Auto-lockdown lifted. Server is open.");
                    broadcastSecurityUpdate();
                });
            }
        }, raidCooldownMs, TimeUnit.MILLISECONDS);
    }

    private void notifyStaff(String message) {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(staff, "moderex.notify.raid")) {
                Msg.send(staff, TextUtil.parse("<dark_gray>[<red>Security</red>] " + message));
            }
        }
    }

    private void logRaidEvent(String type, String ip, String name, UUID uuid, String details) {
        CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_raid_events (event_type, ip_address, player_name, player_uuid, details, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                """, type, ip, name, uuid != null ? uuid.toString() : null, details, System.currentTimeMillis());
            } catch (SQLException e) {
                plugin.logError("Failed to log raid event", e);
            }
        });
    }

    private void broadcastSecurityUpdate() {
        if (plugin.getWebPanelServer() != null) {
            JsonObject data = toStatusJson();
            plugin.getWebPanelServer().broadcastMessage("SECURITY_STATUS_UPDATE", data);
        }
    }

    // === Panel Integration ===

    public JsonObject toStatusJson() {
        JsonObject status = new JsonObject();
        status.addProperty("raidActive", raidActive);
        status.addProperty("raidStartTime", raidStartTime);
        status.addProperty("joinVelocity", botDetector.getJoinVelocity());
        status.addProperty("joinVelocityThreshold", joinVelocityThreshold);
        status.addProperty("autoLockdownEnabled", autoLockdownEnabled);
        status.addProperty("autoLockdownThreshold", autoLockdownThreshold);
        status.addProperty("vpnDetectionEnabled", vpnDetectionEnabled);
        status.addProperty("captchaEnabled", captchaEnabled);
        status.addProperty("botScoreThreshold", botScoreThreshold);
        status.addProperty("globalLockdown", plugin.isGlobalLockdown());
        status.addProperty("pendingCaptchas", pendingCaptchas.size());

        // Top flagged IPs
        JsonArray flaggedIps = new JsonArray();
        ipReputationManager.getReputationCache().entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().score, a.getValue().score))
                .limit(20)
                .forEach(entry -> {
                    JsonObject ip = new JsonObject();
                    ip.addProperty("ip", entry.getKey());
                    ip.addProperty("score", entry.getValue().score);
                    ip.addProperty("reason", entry.getValue().lastReason);
                    ip.addProperty("totalJoins", entry.getValue().totalJoins);
                    ip.addProperty("banned", entry.getValue().banned);
                    flaggedIps.add(ip);
                });
        status.add("flaggedIps", flaggedIps);

        return status;
    }

    public void updateFromJson(JsonObject json) {
        if (json.has("joinVelocityThreshold")) joinVelocityThreshold = json.get("joinVelocityThreshold").getAsInt();
        if (json.has("autoLockdownEnabled")) autoLockdownEnabled = json.get("autoLockdownEnabled").getAsBoolean();
        if (json.has("autoLockdownThreshold")) autoLockdownThreshold = json.get("autoLockdownThreshold").getAsInt();
        if (json.has("vpnDetectionEnabled")) vpnDetectionEnabled = json.get("vpnDetectionEnabled").getAsBoolean();
        if (json.has("captchaEnabled")) captchaEnabled = json.get("captchaEnabled").getAsBoolean();
        if (json.has("botScoreThreshold")) botScoreThreshold = json.get("botScoreThreshold").getAsInt();
    }

    public void toggleLockdown(boolean enabled) {
        plugin.setGlobalLockdown(enabled);
        logRaidEvent(enabled ? "MANUAL_LOCKDOWN" : "MANUAL_UNLOCK", null, null, null, "Manual toggle");
        broadcastSecurityUpdate();
    }

    public void unblockIp(String ip) {
        ipReputationManager.unban(ip);
        logRaidEvent("IP_UNBLOCKED", ip, null, null, "Manual unblock");
    }

    public BotDetector getBotDetector() {
        return botDetector;
    }

    public IpReputationManager getIpReputationManager() {
        return ipReputationManager;
    }

    public boolean isRaidActive() {
        return raidActive;
    }

    private record CaptchaChallenge(String question, String answer, long expiresAt) {}
}

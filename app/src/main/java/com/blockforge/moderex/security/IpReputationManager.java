package com.blockforge.moderex.security;

import com.blockforge.moderex.ModereX;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks IP reputation scores for rate limiting and ban decisions.
 * Higher score = more suspicious. Score decays over time.
 */
public class IpReputationManager {

    private final ModereX plugin;
    private final Map<String, IpReputation> reputationCache = new ConcurrentHashMap<>();

    // Score thresholds
    public static final int SCORE_WARN = 50;
    public static final int SCORE_THROTTLE = 100;
    public static final int SCORE_BAN = 200;

    // Score increments
    public static final int FAILED_LOGIN = 10;
    public static final int RAPID_RECONNECT = 15;
    public static final int SUSPICIOUS_NAME = 20;
    public static final int VPN_DETECTED = 30;
    public static final int BOT_PATTERN = 50;
    public static final int RAID_PARTICIPATION = 40;

    public IpReputationManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        createTable();
        loadFromDatabase();
    }

    private void createTable() {
        try {
            String sql;
            if (plugin.getDatabaseManager().isMySQL()) {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ip_reputation (
                        ip_address VARCHAR(45) PRIMARY KEY,
                        score INT NOT NULL DEFAULT 0,
                        last_reason VARCHAR(255),
                        last_updated BIGINT NOT NULL,
                        total_joins INT NOT NULL DEFAULT 0,
                        total_flags INT NOT NULL DEFAULT 0,
                        banned BOOLEAN DEFAULT FALSE
                    )
                """;
            } else {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ip_reputation (
                        ip_address VARCHAR(45) PRIMARY KEY,
                        score INTEGER NOT NULL DEFAULT 0,
                        last_reason VARCHAR(255),
                        last_updated BIGINT NOT NULL,
                        total_joins INTEGER NOT NULL DEFAULT 0,
                        total_flags INTEGER NOT NULL DEFAULT 0,
                        banned BOOLEAN DEFAULT FALSE
                    )
                """;
            }
            plugin.getDatabaseManager().update(sql);
        } catch (SQLException e) {
            plugin.logError("Failed to create ip_reputation table", e);
        }
    }

    private void loadFromDatabase() {
        try {
            plugin.getDatabaseManager().query("""
                SELECT * FROM moderex_ip_reputation WHERE score > 0 OR banned = TRUE
            """, rs -> {
                while (rs.next()) {
                    IpReputation rep = new IpReputation();
                    rep.ip = rs.getString("ip_address");
                    rep.score = rs.getInt("score");
                    rep.lastReason = rs.getString("last_reason");
                    rep.lastUpdated = rs.getLong("last_updated");
                    rep.totalJoins = rs.getInt("total_joins");
                    rep.totalFlags = rs.getInt("total_flags");
                    rep.banned = rs.getBoolean("banned");
                    reputationCache.put(rep.ip, rep);
                }
                return null;
            });
        } catch (SQLException e) {
            plugin.logError("Failed to load IP reputation data", e);
        }
    }

    /**
     * Increment reputation score for an IP.
     */
    public void addScore(String ip, int points, String reason) {
        IpReputation rep = reputationCache.computeIfAbsent(ip, k -> {
            IpReputation r = new IpReputation();
            r.ip = k;
            return r;
        });

        rep.score += points;
        rep.lastReason = reason;
        rep.lastUpdated = System.currentTimeMillis();
        rep.totalFlags++;

        // Auto-ban if threshold exceeded
        if (rep.score >= SCORE_BAN && !rep.banned) {
            rep.banned = true;
            plugin.getLogger().warning("IP " + ip + " auto-banned (score: " + rep.score + ", reason: " + reason + ")");
        }

        saveReputation(rep);
    }

    /**
     * Record a join from this IP.
     */
    public void recordJoin(String ip) {
        IpReputation rep = reputationCache.computeIfAbsent(ip, k -> {
            IpReputation r = new IpReputation();
            r.ip = k;
            return r;
        });
        rep.totalJoins++;
        rep.lastUpdated = System.currentTimeMillis();

        // Decay score over time (1 point per minute since last update)
        long minutesSinceUpdate = (System.currentTimeMillis() - rep.lastUpdated) / 60000;
        if (minutesSinceUpdate > 0 && rep.score > 0) {
            rep.score = Math.max(0, rep.score - (int) minutesSinceUpdate);
        }
    }

    public int getScore(String ip) {
        IpReputation rep = reputationCache.get(ip);
        return rep != null ? rep.score : 0;
    }

    public boolean isBanned(String ip) {
        IpReputation rep = reputationCache.get(ip);
        return rep != null && rep.banned;
    }

    public void unban(String ip) {
        IpReputation rep = reputationCache.get(ip);
        if (rep != null) {
            rep.banned = false;
            rep.score = 0;
            saveReputation(rep);
        }
    }

    public Map<String, IpReputation> getReputationCache() {
        return reputationCache;
    }

    private void saveReputation(IpReputation rep) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (plugin.getDatabaseManager().isMySQL()) {
                    plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_ip_reputation (ip_address, score, last_reason, last_updated, total_joins, total_flags, banned)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE score = VALUES(score), last_reason = VALUES(last_reason),
                        last_updated = VALUES(last_updated), total_joins = VALUES(total_joins),
                        total_flags = VALUES(total_flags), banned = VALUES(banned)
                    """, rep.ip, rep.score, rep.lastReason, rep.lastUpdated, rep.totalJoins, rep.totalFlags, rep.banned);
                } else {
                    plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_ip_reputation (ip_address, score, last_reason, last_updated, total_joins, total_flags, banned)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT(ip_address) DO UPDATE SET score = excluded.score, last_reason = excluded.last_reason,
                        last_updated = excluded.last_updated, total_joins = excluded.total_joins,
                        total_flags = excluded.total_flags, banned = excluded.banned
                    """, rep.ip, rep.score, rep.lastReason, rep.lastUpdated, rep.totalJoins, rep.totalFlags, rep.banned);
                }
            } catch (SQLException e) {
                plugin.logError("Failed to save IP reputation", e);
            }
        });
    }

    public static class IpReputation {
        public String ip;
        public int score = 0;
        public String lastReason;
        public long lastUpdated = System.currentTimeMillis();
        public int totalJoins = 0;
        public int totalFlags = 0;
        public boolean banned = false;
    }
}

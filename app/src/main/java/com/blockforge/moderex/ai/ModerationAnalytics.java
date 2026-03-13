package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Provides analytics and reporting on AI moderation activity
 * by querying the moderex_ai_moderation_log database table.
 */
public class ModerationAnalytics {

    private final ModereX plugin;

    public ModerationAnalytics(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Get a high-level overview of moderation activity.
     * Includes totals for today/week/month, block rate, average confidence, and top category.
     */
    public CompletableFuture<JsonObject> getOverview() {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject overview = new JsonObject();
            long now = System.currentTimeMillis();
            long dayAgo = now - 86400000L;
            long weekAgo = now - 604800000L;
            long monthAgo = now - 2592000000L;

            try {
                Integer today = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at > ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0, dayAgo);
                overview.addProperty("totalToday", today != null ? today : 0);

                Integer week = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at > ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0, weekAgo);
                overview.addProperty("totalWeek", week != null ? week : 0);

                Integer month = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at > ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0, monthAgo);
                overview.addProperty("totalMonth", month != null ? month : 0);

                Integer blocked = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE action = 'BLOCK' AND created_at > ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0, dayAgo);
                int todayVal = today != null ? today : 0;
                int blockedVal = blocked != null ? blocked : 0;
                overview.addProperty("blockRate", todayVal > 0 ? (double) blockedVal / todayVal : 0.0);

                Double avgConf = plugin.getDatabaseManager().query(
                    "SELECT AVG(confidence) as avg FROM moderex_ai_moderation_log WHERE created_at > ?",
                    rs -> rs.next() ? rs.getDouble("avg") : 0.0, dayAgo);
                overview.addProperty("averageConfidence", avgConf != null ? avgConf : 0.0);

                String topCat = plugin.getDatabaseManager().query(
                    "SELECT category, COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at > ? AND category != 'NONE' GROUP BY category ORDER BY cnt DESC LIMIT 1",
                    rs -> rs.next() ? rs.getString("category") : "NONE", dayAgo);
                overview.addProperty("topCategory", topCat != null ? topCat : "NONE");

            } catch (SQLException e) {
                plugin.logError("Failed to get AI analytics overview", e);
            }
            return overview;
        });
    }

    /**
     * Get an hourly breakdown of moderation activity for the last N hours.
     * Each entry contains: hoursAgo, total, blocked, warned.
     */
    public CompletableFuture<JsonArray> getHourlyBreakdown(int hours) {
        return CompletableFuture.supplyAsync(() -> {
            JsonArray result = new JsonArray();
            long now = System.currentTimeMillis();
            try {
                for (int i = hours - 1; i >= 0; i--) {
                    long start = now - ((long)(i + 1) * 3600000L);
                    long end = now - ((long)i * 3600000L);

                    Integer total = plugin.getDatabaseManager().query(
                        "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at BETWEEN ? AND ?",
                        rs -> rs.next() ? rs.getInt("cnt") : 0, start, end);

                    Integer blocked = plugin.getDatabaseManager().query(
                        "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE action = 'BLOCK' AND created_at BETWEEN ? AND ?",
                        rs -> rs.next() ? rs.getInt("cnt") : 0, start, end);

                    Integer warned = plugin.getDatabaseManager().query(
                        "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE action = 'WARN' AND created_at BETWEEN ? AND ?",
                        rs -> rs.next() ? rs.getInt("cnt") : 0, start, end);

                    JsonObject entry = new JsonObject();
                    entry.addProperty("hoursAgo", i);
                    entry.addProperty("total", total != null ? total : 0);
                    entry.addProperty("blocked", blocked != null ? blocked : 0);
                    entry.addProperty("warned", warned != null ? warned : 0);
                    result.add(entry);
                }
            } catch (SQLException e) {
                plugin.logError("Failed to get hourly breakdown", e);
            }
            return result;
        });
    }

    /**
     * Get a breakdown of violation counts per category.
     * Returns a JsonObject where keys are category names and values are counts.
     */
    public CompletableFuture<JsonObject> getCategoryBreakdown() {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject result = new JsonObject();
            try {
                plugin.getDatabaseManager().query(
                    "SELECT category, COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE category != 'NONE' GROUP BY category ORDER BY cnt DESC",
                    rs -> {
                        while (rs.next()) {
                            result.addProperty(rs.getString("category"), rs.getInt("cnt"));
                        }
                        return null;
                    });
            } catch (SQLException e) {
                plugin.logError("Failed to get category breakdown", e);
            }
            return result;
        });
    }

    /**
     * Get the top offending players by violation count.
     * Each entry contains: playerUuid, playerName, violationCount, lastViolation.
     */
    public CompletableFuture<JsonArray> getTopOffenders(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            JsonArray result = new JsonArray();
            try {
                plugin.getDatabaseManager().query(
                    "SELECT player_uuid, player_name, COUNT(*) as cnt, MAX(created_at) as last_at FROM moderex_ai_moderation_log WHERE action != 'ALLOW' GROUP BY player_uuid, player_name ORDER BY cnt DESC LIMIT ?",
                    rs -> {
                        while (rs.next()) {
                            JsonObject entry = new JsonObject();
                            entry.addProperty("playerUuid", rs.getString("player_uuid"));
                            entry.addProperty("playerName", rs.getString("player_name"));
                            entry.addProperty("violationCount", rs.getInt("cnt"));
                            entry.addProperty("lastViolation", rs.getLong("last_at"));
                            result.add(entry);
                        }
                        return null;
                    }, limit);
            } catch (SQLException e) {
                plugin.logError("Failed to get top offenders", e);
            }
            return result;
        });
    }

    /**
     * Get paginated violation history for a specific player.
     * Returns a JsonObject with "total" count and "entries" array.
     */
    public CompletableFuture<JsonObject> getPlayerHistory(UUID playerUuid, int page, int pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject result = new JsonObject();
            int offset = page * pageSize;
            try {
                Integer total = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE player_uuid = ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0, playerUuid.toString());
                result.addProperty("total", total != null ? total : 0);

                JsonArray entries = new JsonArray();
                plugin.getDatabaseManager().query(
                    "SELECT * FROM moderex_ai_moderation_log WHERE player_uuid = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                    rs -> {
                        while (rs.next()) {
                            JsonObject entry = new JsonObject();
                            entry.addProperty("id", rs.getInt("id"));
                            entry.addProperty("contentType", rs.getString("content_type"));
                            entry.addProperty("content", rs.getString("content"));
                            entry.addProperty("action", rs.getString("action"));
                            entry.addProperty("confidence", rs.getDouble("confidence"));
                            entry.addProperty("reason", rs.getString("reason"));
                            entry.addProperty("category", rs.getString("category"));
                            entry.addProperty("createdAt", rs.getLong("created_at"));
                            entries.add(entry);
                        }
                        return null;
                    }, playerUuid.toString(), pageSize, offset);
                result.add("entries", entries);
            } catch (SQLException e) {
                plugin.logError("Failed to get player AI history", e);
            }
            return result;
        });
    }

    /**
     * Get risk scores for players based on recent violations (last 24 hours).
     * Score is weighted: BLOCK=3, FLAG_FOR_REVIEW=2, WARN=1.
     * Returns up to 20 players sorted by risk score descending.
     */
    public CompletableFuture<JsonArray> getRiskScores() {
        return CompletableFuture.supplyAsync(() -> {
            JsonArray result = new JsonArray();
            long dayAgo = System.currentTimeMillis() - 86400000L;
            try {
                plugin.getDatabaseManager().query(
                    "SELECT player_uuid, player_name, COUNT(*) as violations, " +
                    "SUM(CASE WHEN action = 'BLOCK' THEN 3 WHEN action = 'WARN' THEN 1 WHEN action = 'FLAG_FOR_REVIEW' THEN 2 ELSE 0 END) as score " +
                    "FROM moderex_ai_moderation_log WHERE created_at > ? GROUP BY player_uuid, player_name HAVING score > 0 ORDER BY score DESC LIMIT 20",
                    rs -> {
                        while (rs.next()) {
                            JsonObject entry = new JsonObject();
                            entry.addProperty("playerUuid", rs.getString("player_uuid"));
                            entry.addProperty("playerName", rs.getString("player_name"));
                            entry.addProperty("violations", rs.getInt("violations"));
                            entry.addProperty("riskScore", rs.getInt("score"));
                            result.add(entry);
                        }
                        return null;
                    }, dayAgo);
            } catch (SQLException e) {
                plugin.logError("Failed to get risk scores", e);
            }
            return result;
        });
    }

    /**
     * Get trend data over the last N days.
     * Returns daily totals, most common category, and trend direction.
     */
    public CompletableFuture<JsonObject> getTrends(int days) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject result = new JsonObject();
            long now = System.currentTimeMillis();
            try {
                JsonArray dailyTotals = new JsonArray();
                int previousTotal = 0;
                int latestTotal = 0;

                for (int i = days - 1; i >= 0; i--) {
                    long start = now - ((long)(i + 1) * 86400000L);
                    long end = now - ((long)i * 86400000L);

                    Integer count = plugin.getDatabaseManager().query(
                        "SELECT COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at BETWEEN ? AND ?",
                        rs -> rs.next() ? rs.getInt("cnt") : 0, start, end);

                    int countVal = count != null ? count : 0;
                    JsonObject day = new JsonObject();
                    day.addProperty("daysAgo", i);
                    day.addProperty("total", countVal);
                    dailyTotals.add(day);

                    if (i == 1) previousTotal = countVal;
                    if (i == 0) latestTotal = countVal;
                }
                result.add("dailyTotals", dailyTotals);

                // Determine trend direction
                String trend;
                if (latestTotal > previousTotal) {
                    trend = "INCREASING";
                } else if (latestTotal < previousTotal) {
                    trend = "DECREASING";
                } else {
                    trend = "STABLE";
                }
                result.addProperty("trend", trend);

                // Most common category over the period
                long periodStart = now - ((long) days * 86400000L);
                String topCat = plugin.getDatabaseManager().query(
                    "SELECT category, COUNT(*) as cnt FROM moderex_ai_moderation_log WHERE created_at > ? AND category != 'NONE' GROUP BY category ORDER BY cnt DESC LIMIT 1",
                    rs -> rs.next() ? rs.getString("category") : "NONE", periodStart);
                result.addProperty("mostCommonCategory", topCat != null ? topCat : "NONE");

            } catch (SQLException e) {
                plugin.logError("Failed to get AI moderation trends", e);
            }
            return result;
        });
    }
}

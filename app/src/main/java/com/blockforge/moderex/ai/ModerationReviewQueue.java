package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Review queue for AI-flagged content.
 * Stores items that need human review, with quick approve/dismiss/undo actions.
 */
public class ModerationReviewQueue {

    private final ModereX plugin;

    public ModerationReviewQueue(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        createTable();
    }

    private void createTable() {
        try {
            String sql;
            if (plugin.getDatabaseManager().isMySQL()) {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ai_review_queue (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        content_type VARCHAR(32) NOT NULL,
                        content TEXT NOT NULL,
                        ai_action VARCHAR(20) NOT NULL,
                        ai_confidence DOUBLE NOT NULL,
                        ai_reason TEXT,
                        ai_category VARCHAR(64),
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        reviewed_by VARCHAR(36),
                        reviewed_at BIGINT,
                        review_action VARCHAR(20),
                        created_at BIGINT NOT NULL,
                        INDEX idx_status (status),
                        INDEX idx_player (player_uuid)
                    )
                """;
            } else {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_ai_review_queue (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        content_type VARCHAR(32) NOT NULL,
                        content TEXT NOT NULL,
                        ai_action VARCHAR(20) NOT NULL,
                        ai_confidence DOUBLE NOT NULL,
                        ai_reason TEXT,
                        ai_category VARCHAR(64),
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        reviewed_by VARCHAR(36),
                        reviewed_at BIGINT,
                        review_action VARCHAR(20),
                        created_at BIGINT NOT NULL
                    )
                """;
            }
            plugin.getDatabaseManager().update(sql);

            if (!plugin.getDatabaseManager().isMySQL()) {
                plugin.getDatabaseManager().update(
                    "CREATE INDEX IF NOT EXISTS idx_review_status ON moderex_ai_review_queue(status)");
                plugin.getDatabaseManager().update(
                    "CREATE INDEX IF NOT EXISTS idx_review_player ON moderex_ai_review_queue(player_uuid)");
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create review queue table", e);
        }
    }

    /**
     * Add an item to the review queue.
     */
    public CompletableFuture<Void> addToQueue(String playerUuid, String playerName,
                                               ContentType contentType, String content,
                                               ModerationResult result) {
        return CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_ai_review_queue
                    (player_uuid, player_name, content_type, content, ai_action, ai_confidence,
                     ai_reason, ai_category, status, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?)
                """,
                    playerUuid, playerName, contentType.name(), content,
                    result.getAction().name(), result.getConfidence(),
                    result.getReason(), result.getCategory(),
                    System.currentTimeMillis()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to add to review queue", e);
            }
        });
    }

    /**
     * Get pending review items.
     */
    public CompletableFuture<JsonObject> getPending(int page, int pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject result = new JsonObject();
            int offset = page * pageSize;
            try {
                Integer total = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_review_queue WHERE status = 'PENDING'",
                    rs -> rs.next() ? rs.getInt("cnt") : 0);
                result.addProperty("total", total != null ? total : 0);

                JsonArray entries = new JsonArray();
                plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_ai_review_queue
                    WHERE status = 'PENDING' ORDER BY created_at DESC LIMIT ? OFFSET ?
                """, rs -> {
                    while (rs.next()) {
                        JsonObject entry = new JsonObject();
                        entry.addProperty("id", rs.getInt("id"));
                        entry.addProperty("playerUuid", rs.getString("player_uuid"));
                        entry.addProperty("playerName", rs.getString("player_name"));
                        entry.addProperty("contentType", rs.getString("content_type"));
                        entry.addProperty("content", rs.getString("content"));
                        entry.addProperty("aiAction", rs.getString("ai_action"));
                        entry.addProperty("aiConfidence", rs.getDouble("ai_confidence"));
                        entry.addProperty("aiReason", rs.getString("ai_reason"));
                        entry.addProperty("aiCategory", rs.getString("ai_category"));
                        entry.addProperty("createdAt", rs.getLong("created_at"));
                        entries.add(entry);
                    }
                    return null;
                }, pageSize, offset);
                result.add("entries", entries);
            } catch (SQLException e) {
                plugin.logError("Failed to get review queue", e);
            }
            return result;
        });
    }

    /**
     * Approve a review item (confirm AI decision was correct).
     */
    public CompletableFuture<Boolean> approve(int id, String reviewerUuid) {
        return updateStatus(id, "APPROVED", reviewerUuid, "APPROVE");
    }

    /**
     * Dismiss a review item (AI decision was wrong, no action needed).
     */
    public CompletableFuture<Boolean> dismiss(int id, String reviewerUuid) {
        return updateStatus(id, "DISMISSED", reviewerUuid, "DISMISS");
    }

    /**
     * Undo/reverse an AI action on a review item.
     */
    public CompletableFuture<Boolean> undo(int id, String reviewerUuid) {
        return updateStatus(id, "UNDONE", reviewerUuid, "UNDO");
    }

    private CompletableFuture<Boolean> updateStatus(int id, String status, String reviewerUuid, String action) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int rows = plugin.getDatabaseManager().update("""
                    UPDATE moderex_ai_review_queue
                    SET status = ?, reviewed_by = ?, reviewed_at = ?, review_action = ?
                    WHERE id = ? AND status = 'PENDING'
                """, status, reviewerUuid, System.currentTimeMillis(), action, id);
                return rows > 0;
            } catch (SQLException e) {
                plugin.logError("Failed to update review queue item", e);
                return false;
            }
        });
    }

    /**
     * Get count of pending items.
     */
    public CompletableFuture<Integer> getPendingCount() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer count = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_ai_review_queue WHERE status = 'PENDING'",
                    rs -> rs.next() ? rs.getInt("cnt") : 0);
                return count != null ? count : 0;
            } catch (SQLException e) {
                plugin.logError("Failed to get review queue count", e);
                return 0;
            }
        });
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", true);
        return json;
    }
}

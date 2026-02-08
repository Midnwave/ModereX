package com.blockforge.moderex.log.storage;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Database-backed activity log storage (uses the main database connection).
 * Works with both SQLite and MySQL.
 */
public class DatabaseActivityLogStorage implements ActivityLogStorage {

    private final ModereX plugin;
    private boolean isMySQL;

    public DatabaseActivityLogStorage(ModereX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean initialize() {
        isMySQL = plugin.getConfigManager().getSettings().getDatabaseType().equalsIgnoreCase("mysql");

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             Statement stmt = conn.createStatement()) {

            // Create activity log table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS moderex_activity_log (
                    id INTEGER PRIMARY KEY %s,
                    player_uuid VARCHAR(36) NOT NULL,
                    player_name VARCHAR(16) NOT NULL,
                    type VARCHAR(32) NOT NULL,
                    content TEXT,
                    extra TEXT,
                    timestamp BIGINT NOT NULL,
                    server VARCHAR(64),
                    INDEX idx_player_uuid (player_uuid),
                    INDEX idx_timestamp (timestamp),
                    INDEX idx_type (type)
                )
                """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            plugin.logDebug("[ActivityLog] Database storage initialized");
            return true;
        } catch (SQLException e) {
            // Try without INDEX syntax for SQLite
            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_activity_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        type VARCHAR(32) NOT NULL,
                        content TEXT,
                        extra TEXT,
                        timestamp BIGINT NOT NULL,
                        server VARCHAR(64)
                    )
                    """);

                // Create indexes separately for SQLite
                try {
                    stmt.execute("CREATE INDEX IF NOT EXISTS idx_activity_player ON moderex_activity_log(player_uuid)");
                    stmt.execute("CREATE INDEX IF NOT EXISTS idx_activity_timestamp ON moderex_activity_log(timestamp)");
                    stmt.execute("CREATE INDEX IF NOT EXISTS idx_activity_type ON moderex_activity_log(type)");
                } catch (SQLException ignored) {
                    // Indexes might already exist
                }

                plugin.logDebug("[ActivityLog] Database storage initialized (SQLite mode)");
                return true;
            } catch (SQLException e2) {
                plugin.logError("Failed to initialize activity log storage", e2);
                return false;
            }
        }
    }

    @Override
    public void shutdown() {
        // Nothing to do - we use the main database connection pool
    }

    @Override
    public boolean save(ActivityLogEntry entry) {
        String sql = """
            INSERT INTO moderex_activity_log (player_uuid, player_name, type, content, extra, timestamp, server)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entry.getPlayerUuid().toString());
            stmt.setString(2, entry.getPlayerName());
            stmt.setString(3, entry.getType().name());
            stmt.setString(4, entry.getContent());
            stmt.setString(5, entry.getExtra());
            stmt.setLong(6, entry.getTimestamp());
            stmt.setString(7, entry.getServer());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getLong(1));
                    }
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            plugin.logError("Failed to save activity log entry", e);
            return false;
        }
    }

    @Override
    public boolean saveBatch(List<ActivityLogEntry> entries) {
        if (entries.isEmpty()) return true;

        String sql = """
            INSERT INTO moderex_activity_log (player_uuid, player_name, type, content, extra, timestamp, server)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (ActivityLogEntry entry : entries) {
                stmt.setString(1, entry.getPlayerUuid().toString());
                stmt.setString(2, entry.getPlayerName());
                stmt.setString(3, entry.getType().name());
                stmt.setString(4, entry.getContent());
                stmt.setString(5, entry.getExtra());
                stmt.setLong(6, entry.getTimestamp());
                stmt.setString(7, entry.getServer());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            plugin.logError("Failed to save activity log batch", e);
            return false;
        }
    }

    @Override
    public List<ActivityLogEntry> getEntries(UUID playerUuid, List<ActivityType> types,
                                             long sinceTimestamp, long beforeTimestamp,
                                             int limit, int offset) {
        List<ActivityLogEntry> entries = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT id, player_uuid, player_name, type, content, extra, timestamp, server
            FROM moderex_activity_log
            WHERE player_uuid = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(playerUuid.toString());

        if (sinceTimestamp > 0) {
            sql.append(" AND timestamp >= ?");
            params.add(sinceTimestamp);
        }

        if (beforeTimestamp > 0) {
            sql.append(" AND timestamp <= ?");
            params.add(beforeTimestamp);
        }

        if (types != null && !types.isEmpty()) {
            sql.append(" AND type IN (");
            for (int i = 0; i < types.size(); i++) {
                sql.append(i > 0 ? ", ?" : "?");
                params.add(types.get(i).name());
            }
            sql.append(")");
        }

        sql.append(" ORDER BY timestamp DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String s) {
                    stmt.setString(i + 1, s);
                } else if (param instanceof Long l) {
                    stmt.setLong(i + 1, l);
                } else if (param instanceof Integer n) {
                    stmt.setInt(i + 1, n);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActivityType type;
                    try {
                        type = ActivityType.valueOf(rs.getString("type"));
                    } catch (IllegalArgumentException e) {
                        continue; // Skip unknown types
                    }

                    entries.add(new ActivityLogEntry(
                            rs.getLong("id"),
                            UUID.fromString(rs.getString("player_uuid")),
                            rs.getString("player_name"),
                            type,
                            rs.getString("content"),
                            rs.getString("extra"),
                            rs.getLong("timestamp"),
                            rs.getString("server")
                    ));
                }
            }
        } catch (SQLException e) {
            plugin.logError("Failed to get activity log entries", e);
        }

        return entries;
    }

    @Override
    public ActivityLogEntry getEntryById(long id) {
        String sql = "SELECT * FROM moderex_activity_log WHERE id = ?";
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ActivityType type;
                    try {
                        type = ActivityType.valueOf(rs.getString("type"));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                    return new ActivityLogEntry(
                            rs.getLong("id"),
                            UUID.fromString(rs.getString("player_uuid")),
                            rs.getString("player_name"),
                            type,
                            rs.getString("content"),
                            rs.getString("extra"),
                            rs.getLong("timestamp"),
                            rs.getString("server")
                    );
                }
            }
        } catch (SQLException e) {
            plugin.logError("Failed to get activity log entry by ID", e);
        }
        return null;
    }

    @Override
    public int getEntryCount(UUID playerUuid, List<ActivityType> types, long sinceTimestamp, long beforeTimestamp) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM moderex_activity_log WHERE player_uuid = ?");

        List<Object> params = new ArrayList<>();
        params.add(playerUuid.toString());

        if (sinceTimestamp > 0) {
            sql.append(" AND timestamp >= ?");
            params.add(sinceTimestamp);
        }

        if (beforeTimestamp > 0) {
            sql.append(" AND timestamp <= ?");
            params.add(beforeTimestamp);
        }

        if (types != null && !types.isEmpty()) {
            sql.append(" AND type IN (");
            for (int i = 0; i < types.size(); i++) {
                sql.append(i > 0 ? ", ?" : "?");
                params.add(types.get(i).name());
            }
            sql.append(")");
        }

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String s) {
                    stmt.setString(i + 1, s);
                } else if (param instanceof Long l) {
                    stmt.setLong(i + 1, l);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.logError("Failed to get activity log count", e);
        }

        return 0;
    }

    @Override
    public int purgeOldEntries(long olderThan) {
        String sql = "DELETE FROM moderex_activity_log WHERE timestamp < ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, olderThan);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.logError("Failed to purge old activity log entries", e);
            return 0;
        }
    }

    @Override
    public String getStorageType() {
        return "database";
    }
}

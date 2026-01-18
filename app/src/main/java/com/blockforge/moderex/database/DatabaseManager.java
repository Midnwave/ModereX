package com.blockforge.moderex.database;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.Settings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final ModereX plugin;
    private HikariDataSource dataSource;
    private boolean isMySQL;

    public DatabaseManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        Settings settings = plugin.getConfigManager().getSettings();
        isMySQL = settings.getDatabaseType().equalsIgnoreCase("mysql");

        try {
            setupDataSource(settings);
            createTables();
            plugin.getLogger().info("Database initialized successfully (" + (isMySQL ? "MySQL" : "SQLite") + ")");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to initialize database", e);
            return false;
        }
    }

    private void setupDataSource(Settings settings) {
        HikariConfig config = new HikariConfig();

        if (isMySQL) {
            config.setJdbcUrl("jdbc:mysql://" + settings.getMysqlHost() + ":" +
                    settings.getMysqlPort() + "/" + settings.getMysqlDatabase() +
                    "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true");
            config.setUsername(settings.getMysqlUsername());
            config.setPassword(settings.getMysqlPassword());
            config.setMaximumPoolSize(settings.getMysqlPoolSize());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else {
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setMaximumPoolSize(1); // SQLite doesn't support concurrent writes well
            config.setDriverClassName("org.sqlite.JDBC");
        }

        config.setPoolName("ModereX-Pool");
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private void createTables() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Players table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_players (
                        uuid VARCHAR(36) PRIMARY KEY,
                        username VARCHAR(16) NOT NULL,
                        ip_address VARCHAR(45),
                        first_join BIGINT NOT NULL,
                        last_join BIGINT NOT NULL,
                        last_server VARCHAR(64)
                    )
                    """);

            // Punishments table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_punishments (
                        id INTEGER PRIMARY KEY %s,
                        case_id VARCHAR(16) UNIQUE NOT NULL,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        type VARCHAR(16) NOT NULL,
                        reason TEXT,
                        staff_uuid VARCHAR(36),
                        staff_name VARCHAR(16) NOT NULL,
                        created_at BIGINT NOT NULL,
                        expires_at BIGINT NOT NULL,
                        active BOOLEAN DEFAULT TRUE,
                        removed_by_uuid VARCHAR(36),
                        removed_by_name VARCHAR(16),
                        removed_at BIGINT,
                        removed_reason TEXT,
                        ip_address VARCHAR(45),
                        server VARCHAR(64)
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Warnings table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_warnings (
                        id INTEGER PRIMARY KEY %s,
                        case_id VARCHAR(16) UNIQUE NOT NULL,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        reason TEXT,
                        staff_uuid VARCHAR(36),
                        staff_name VARCHAR(16) NOT NULL,
                        created_at BIGINT NOT NULL,
                        expires_at BIGINT NOT NULL,
                        active BOOLEAN DEFAULT TRUE,
                        removed_by_uuid VARCHAR(36),
                        removed_by_name VARCHAR(16),
                        removed_at BIGINT,
                        server VARCHAR(64)
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Command history table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_command_history (
                        id INTEGER PRIMARY KEY %s,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        command TEXT NOT NULL,
                        executed_at BIGINT NOT NULL,
                        server VARCHAR(64)
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Command blacklist table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_command_blacklist (
                        id INTEGER PRIMARY KEY %s,
                        player_uuid VARCHAR(36) NOT NULL,
                        command VARCHAR(256) NOT NULL,
                        staff_uuid VARCHAR(36),
                        staff_name VARCHAR(16) NOT NULL,
                        created_at BIGINT NOT NULL,
                        expires_at BIGINT NOT NULL,
                        reason TEXT,
                        server VARCHAR(64)
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Automod rules table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_automod_rules (
                        id INTEGER PRIMARY KEY %s,
                        name VARCHAR(64) NOT NULL,
                        type VARCHAR(32) NOT NULL,
                        enabled BOOLEAN DEFAULT TRUE,
                        config TEXT NOT NULL,
                        created_at BIGINT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Anticheat rules table (per-check configuration)
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_anticheat_rules (
                        id INTEGER PRIMARY KEY %s,
                        anticheat VARCHAR(32) NOT NULL,
                        check_name VARCHAR(64) NOT NULL,
                        enabled BOOLEAN DEFAULT TRUE,
                        min_vl INTEGER DEFAULT 0,
                        threshold_count INTEGER DEFAULT 0,
                        threshold_duration BIGINT DEFAULT 0,
                        auto_punishment TEXT,
                        UNIQUE(anticheat, check_name)
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Staff settings table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_staff_settings (
                        uuid VARCHAR(36) PRIMARY KEY,
                        settings TEXT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """);

            // Watchlist table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_watchlist (
                        id INTEGER PRIMARY KEY %s,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        added_by_uuid VARCHAR(36),
                        added_by_name VARCHAR(16) NOT NULL,
                        reason TEXT,
                        added_at BIGINT NOT NULL,
                        active BOOLEAN DEFAULT TRUE
                    )
                    """.formatted(isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT"));

            // Web panel settings table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_webpanel_settings (
                        uuid VARCHAR(36) PRIMARY KEY,
                        chat_alerts BOOLEAN DEFAULT TRUE,
                        sound_enabled BOOLEAN DEFAULT TRUE,
                        watchlist_toasts BOOLEAN DEFAULT TRUE,
                        staffchat_notifications BOOLEAN DEFAULT TRUE,
                        punishment_alerts BOOLEAN DEFAULT TRUE,
                        automod_alerts BOOLEAN DEFAULT TRUE,
                        anticheat_alerts BOOLEAN DEFAULT TRUE,
                        compact_mode BOOLEAN DEFAULT FALSE,
                        settings_json TEXT,
                        updated_at BIGINT NOT NULL
                    )
                    """);


            // Templates table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_templates (
                        id VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(64) NOT NULL,
                        type VARCHAR(16) NOT NULL,
                        duration VARCHAR(32),
                        reason TEXT,
                        category VARCHAR(32),
                        priority INTEGER DEFAULT 0,
                        active BOOLEAN DEFAULT TRUE,
                        created_by VARCHAR(36),
                        created_by_name VARCHAR(16),
                        created_at BIGINT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """);

            // Vanish state table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_vanish_state (
                        uuid VARCHAR(36) PRIMARY KEY,
                        vanished BOOLEAN DEFAULT FALSE,
                        vanish_level INTEGER DEFAULT 1,
                        vanish_time BIGINT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """);

            // Replays metadata table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moderex_replays (
                        session_id VARCHAR(64) PRIMARY KEY,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        world VARCHAR(64) NOT NULL,
                        start_time BIGINT NOT NULL,
                        end_time BIGINT,
                        duration_ms BIGINT,
                        recording_reason VARCHAR(32) NOT NULL,
                        snapshot_count INTEGER DEFAULT 0,
                        file_path VARCHAR(256),
                        file_size BIGINT DEFAULT 0,
                        recorded_by VARCHAR(36),
                        recorded_by_name VARCHAR(16)
                    )
                    """);

            // Create indexes for performance
            createIndexes(stmt);
        }
    }

    private void createIndexes(Statement stmt) throws SQLException {
        // Punishments indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_punishments_player ON moderex_punishments(player_uuid)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_punishments_type ON moderex_punishments(type)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_punishments_active ON moderex_punishments(active)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_punishments_ip ON moderex_punishments(ip_address)");

        // Warnings indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_warnings_player ON moderex_warnings(player_uuid)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_warnings_active ON moderex_warnings(active)");

        // Command history indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_cmd_history_player ON moderex_command_history(player_uuid)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_cmd_history_time ON moderex_command_history(executed_at)");

        // Command blacklist indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_cmd_blacklist_player ON moderex_command_blacklist(player_uuid)");

        // Watchlist indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_watchlist_player ON moderex_watchlist(player_uuid)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_watchlist_active ON moderex_watchlist(active)");

        // Templates indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_templates_type ON moderex_templates(type)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_templates_category ON moderex_templates(category)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_templates_active ON moderex_templates(active)");

        // Vanish state indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_vanish_state_vanished ON moderex_vanish_state(vanished)");

        // Replays indexes
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_replays_player ON moderex_replays(player_uuid)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_replays_time ON moderex_replays(start_time)");
        executeIfNotExists(stmt, "CREATE INDEX IF NOT EXISTS idx_replays_reason ON moderex_replays(recording_reason)");
    }

    private void executeIfNotExists(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException ignored) {
            // Index might already exist
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is not available");
        }
        return dataSource.getConnection();
    }

    public String generateCaseId() {
        java.util.Random random = new java.util.Random();
        int maxAttempts = 100;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            StringBuilder letters = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                char letter = (char) ('A' + random.nextInt(26));
                letters.append(letter);
            }
            String caseId = "MX-" + letters.toString();

            // Check if this case ID already exists
            try (Connection conn = getConnection();
                 PreparedStatement check = conn.prepareStatement(
                         "SELECT 1 FROM moderex_punishments WHERE case_id = ? LIMIT 1")) {
                check.setString(1, caseId);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        // ID is unique, return it
                        return caseId;
                    }
                }
            } catch (SQLException e) {
                plugin.logError("Failed to check case ID uniqueness", e);
            }
        }

        // Fallback: use timestamp-based ID with random letters
        String timestamp = Long.toHexString(System.currentTimeMillis()).toUpperCase();
        return "MX-" + timestamp.substring(Math.max(0, timestamp.length() - 7));
    }

    public void executeAsync(String sql, Object... params) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameters(stmt, params);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.logError("Failed to execute async query: " + sql, e);
            }
        });
    }

    public <T> T query(String sql, ResultMapper<T> mapper, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.map(rs);
            }
        }
    }

    public int update(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed.");
        }
    }

    @FunctionalInterface
    public interface ResultMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}

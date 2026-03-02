/**
 * ModereX Gateway Server
 *
 * Routes web panel traffic to the correct Minecraft server based on Server ID.
 *
 * Architecture:
 * - MC servers connect via WebSocket to register themselves
 * - Browsers connect via WebSocket to access panel
 * - Gateway routes messages between browser and correct MC server
 *
 * Server ID Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX (UUID-style, 25 chars + 4 dashes)
 * URL Prefix: Progressive shortening starting with first 5 chars, expanding if needed
 */

const http = require('http');
const { WebSocketServer, WebSocket } = require('ws');
const path = require('path');
const fs = require('fs');
const crypto = require('crypto');
const { spawn } = require('child_process');

// Load .env file if present (no dotenv dependency needed)
try {
    const envPath = path.join(__dirname, '.env');
    if (fs.existsSync(envPath)) {
        const envContent = fs.readFileSync(envPath, 'utf8');
        for (const line of envContent.split('\n')) {
            const trimmed = line.trim();
            if (!trimmed || trimmed.startsWith('#')) continue;
            const eqIdx = trimmed.indexOf('=');
            if (eqIdx === -1) continue;
            const key = trimmed.slice(0, eqIdx).trim();
            const val = trimmed.slice(eqIdx + 1).trim();
            if (!process.env[key]) process.env[key] = val;
        }
    }
} catch (e) { /* .env loading is best-effort */ }

let argon2;
try { argon2 = require('argon2'); } catch (e) { console.warn('[Auth] argon2 not available - password auth disabled'); }

// Current Cloudflare Tunnel host (set when tunnel establishes)
let currentTunnelHost = null;

// Configuration
const CONFIG = {
    port: process.env.PORT || 3000,
    heartbeatInterval: 30000,  // 30 seconds
    serverTimeout: 60000,      // 60 seconds without heartbeat = dead
    adminEmails: ['@blockforge.studio'], // Cloudflare Access allowed email domains
};

// Cloudflare Admin Secret for license API
const CLOUDFLARE_ADMIN_SECRET = process.env.CLOUDFLARE_ADMIN_SECRET || '16a72a240d6934b3ddc1730e16bc83cafbb01912ac2a435b05f95e0e0ac0727d';

// Admin UUIDs (comma-separated Minecraft UUIDs allowed to access admin panel)
const ADMIN_UUIDS = new Set((process.env.ADMIN_UUIDS || '').split(',').map(u => u.trim()).filter(Boolean));

// Message types that browsers are NOT allowed to send (server-internal only)
const BLOCKED_BROWSER_TYPES = new Set([
    'register', 'heartbeat', 'panel_response', 'broadcast',
    'permission_sync', 'permission_update', 'token_register', 'token_revoke',
    'settings_sync', 'server_unregister', 'global_pre_auth_result',
    'browser_connected', 'browser_disconnected', 'link_code_register'
]);

// ============================================================================
// Database Setup (SQLite with fallback chain)
// ============================================================================
let db = null;
const DB_PATH = path.join(__dirname, 'gateway.db');

// In-memory fallback for announcements if SQLite not available
const inMemoryAnnouncements = new Map();

// In-memory fallback for server secrets
const inMemoryServerSecrets = new Map();

// CPU usage tracking - sampled periodically to avoid race conditions
let lastCpuUsage = process.cpuUsage();
let lastCpuCheck = Date.now();
let currentCpuPercent = 0;

// Sample CPU usage every 5 seconds (avoids race condition between health and metrics calls)
setInterval(() => {
    const currentUsage = process.cpuUsage();
    const currentTime = Date.now();
    const elapsedTime = currentTime - lastCpuCheck;
    if (elapsedTime > 0) {
        const userDiff = currentUsage.user - lastCpuUsage.user;
        const systemDiff = currentUsage.system - lastCpuUsage.system;
        const totalCpuTime = userDiff + systemDiff;
        currentCpuPercent = Math.min(Math.round((totalCpuTime / (elapsedTime * 1000)) * 100), 100);
    }
    lastCpuUsage = currentUsage;
    lastCpuCheck = currentTime;
}, 5000);

// ============================================================================
// DDoS Protection
// ============================================================================
const ddosProtection = {
    // Per-IP connection tracking
    connectionsByIp: new Map(),       // ip -> Set of WebSocket connections
    connectionTimestamps: new Map(),  // ip -> [timestamps of recent connections]
    bannedIps: new Set(),
    ipMessageCounts: new Map(),       // ip -> { count, lastReset }

    // Per-/24 subnet tracking
    subnetConnections: new Map(),     // subnet -> connection count

    // Limits
    maxConnectionsPerIp: 5,
    maxConnectionsPerSubnet: 20,
    maxNewConnectionsPerSecond: 50,   // Global velocity limit
    maxMessagesPerSecond: 30,         // Per-connection message rate
    maxMessageSize: 65536,            // 64KB
    banDurationMs: 300000,            // 5 minute ban

    // Global velocity tracking
    recentConnectionTimestamps: [],

    getSubnet(ip) {
        const parts = ip.split('.');
        return parts.length >= 3 ? `${parts[0]}.${parts[1]}.${parts[2]}` : ip;
    },

    checkConnection(ip) {
        // Check if IP is banned
        if (this.bannedIps.has(ip)) return false;

        // Check per-IP limit
        const ipConns = this.connectionsByIp.get(ip);
        if (ipConns && ipConns.size >= this.maxConnectionsPerIp) {
            console.warn(`[DDoS] IP ${ip} exceeded connection limit (${ipConns.size})`);
            return false;
        }

        // Check /24 subnet limit
        const subnet = this.getSubnet(ip);
        const subnetCount = this.subnetConnections.get(subnet) || 0;
        if (subnetCount >= this.maxConnectionsPerSubnet) {
            console.warn(`[DDoS] Subnet ${subnet} exceeded connection limit (${subnetCount})`);
            return false;
        }

        // Check global connection velocity
        const now = Date.now();
        this.recentConnectionTimestamps = this.recentConnectionTimestamps.filter(t => now - t < 1000);
        if (this.recentConnectionTimestamps.length >= this.maxNewConnectionsPerSecond) {
            console.warn(`[DDoS] Global connection velocity exceeded (${this.recentConnectionTimestamps.length}/sec)`);
            return false;
        }
        this.recentConnectionTimestamps.push(now);

        return true;
    },

    trackConnection(ip, ws) {
        if (!this.connectionsByIp.has(ip)) {
            this.connectionsByIp.set(ip, new Set());
        }
        this.connectionsByIp.get(ip).add(ws);

        const subnet = this.getSubnet(ip);
        this.subnetConnections.set(subnet, (this.subnetConnections.get(subnet) || 0) + 1);
    },

    removeConnection(ip, ws) {
        const ipConns = this.connectionsByIp.get(ip);
        if (ipConns) {
            ipConns.delete(ws);
            if (ipConns.size === 0) this.connectionsByIp.delete(ip);
        }

        const subnet = this.getSubnet(ip);
        const count = (this.subnetConnections.get(subnet) || 1) - 1;
        if (count <= 0) this.subnetConnections.delete(subnet);
        else this.subnetConnections.set(subnet, count);
    },

    checkMessageRate(ip) {
        const now = Date.now();
        let tracker = this.ipMessageCounts.get(ip);
        if (!tracker || now - tracker.lastReset > 1000) {
            tracker = { count: 0, lastReset: now };
            this.ipMessageCounts.set(ip, tracker);
        }
        tracker.count++;

        if (tracker.count > this.maxMessagesPerSecond) {
            console.warn(`[DDoS] IP ${ip} exceeded message rate (${tracker.count}/sec)`);
            this.banIp(ip, 'Message rate exceeded');
            return false;
        }
        return true;
    },

    banIp(ip, reason) {
        this.bannedIps.add(ip);
        console.warn(`[DDoS] Banned IP ${ip}: ${reason}`);

        // Auto-unban after duration
        setTimeout(() => {
            this.bannedIps.delete(ip);
            console.log(`[DDoS] Unbanned IP ${ip}`);
        }, this.banDurationMs);
    }
};

// Clean up stale DDoS tracking data every 60 seconds
setInterval(() => {
    const now = Date.now();
    ddosProtection.recentConnectionTimestamps = ddosProtection.recentConnectionTimestamps.filter(t => now - t < 5000);
    // Clean old message rate trackers
    for (const [ip, tracker] of ddosProtection.ipMessageCounts) {
        if (now - tracker.lastReset > 10000) ddosProtection.ipMessageCounts.delete(ip);
    }
}, 60000);

// In-memory fallback for global tokens
const inMemoryGlobalTokens = new Map();

// In-memory fallback for server access
const inMemoryServerAccess = new Map(); // key: `${uuid}:${serverId}`

// In-memory fallback for user settings
const inMemoryUserSettings = new Map();

// In-memory fallback for link codes
const inMemoryLinkCodes = new Map(); // code_hash → { minecraft_uuid, minecraft_username, server_id, created_at, expires_at, used }

// In-memory fallback for user accounts
const inMemoryUserAccounts = new Map(); // minecraft_uuid → { password_hash, minecraft_username, created_at, updated_at, last_login_at, failed_attempts, locked_until, device_fingerprints }

// In-memory fallback for user sessions
const inMemoryUserSessions = new Map(); // session_id_hash → { minecraft_uuid, device_fingerprint_hash, created_at, expires_at, last_active_at, ip_address }

// In-memory fallback for reviews
const inMemoryReviews = new Map(); // minecraft_uuid → { minecraft_uuid, minecraft_username, rating, description, created_at, updated_at }

// In-memory fallback for admin accounts
const inMemoryAdminAccounts = new Map(); // minecraft_uuid → { minecraft_uuid, totp_secret, totp_verified, created_at, last_login }

// In-memory fallback for admin sessions
const inMemoryAdminSessions = new Map(); // session_id_hash → { minecraft_uuid, created_at, expires_at, fully_authenticated, ip_address }

// Rate limiters for auth API
const linkVerifyAttempts = new Map(); // IP → { count, firstAttempt }
const passwordAuthAttempts = new Map(); // minecraft_uuid → { count, lastAttempt }

/**
 * Create a sql.js wrapper that matches better-sqlite3's synchronous API.
 * better-sqlite3: db.prepare(sql).run(v1, v2) / .get(v1, v2) / .all(v1, v2)
 * sql.js: stmt.bind([v1, v2]); stmt.step(); stmt.getAsObject(); stmt.free()
 */
function createSqlJsWrapper(sqlDb) {
    let saveTimer = null;

    function saveToDisk() {
        try {
            const data = sqlDb.export();
            fs.writeFileSync(DB_PATH, Buffer.from(data));
        } catch (e) {
            console.error('[Database] Failed to save to disk:', e.message);
        }
    }

    // Auto-save every 30 seconds
    saveTimer = setInterval(saveToDisk, 30000);

    return {
        exec: (sql) => sqlDb.run(sql),
        prepare: (sql) => ({
            run: (...params) => {
                const stmt = sqlDb.prepare(sql);
                if (params.length > 0) stmt.bind(params);
                stmt.step();
                stmt.free();
                // Schedule a save after writes
                if (!saveTimer._pendingSave) {
                    saveTimer._pendingSave = true;
                    setTimeout(() => { saveToDisk(); saveTimer._pendingSave = false; }, 1000);
                }
                return { changes: sqlDb.getRowsModified() };
            },
            get: (...params) => {
                const stmt = sqlDb.prepare(sql);
                if (params.length > 0) stmt.bind(params);
                const result = stmt.step() ? stmt.getAsObject() : undefined;
                stmt.free();
                return result;
            },
            all: (...params) => {
                const results = [];
                const stmt = sqlDb.prepare(sql);
                if (params.length > 0) stmt.bind(params);
                while (stmt.step()) results.push(stmt.getAsObject());
                stmt.free();
                return results;
            }
        }),
        close: () => {
            clearInterval(saveTimer);
            saveToDisk();
            sqlDb.close();
        }
    };
}

/**
 * Create all required database tables.
 */
function createTables() {
    // Announcements table
    db.exec(`
        CREATE TABLE IF NOT EXISTS admin_announcements (
            id TEXT PRIMARY KEY,
            title TEXT NOT NULL,
            message TEXT NOT NULL,
            type TEXT NOT NULL DEFAULT 'info',
            priority INTEGER DEFAULT 0,
            action_url TEXT,
            action_text TEXT,
            dismissible INTEGER DEFAULT 1,
            created_by TEXT NOT NULL,
            created_at INTEGER NOT NULL,
            scheduled_at INTEGER,
            expires_at INTEGER,
            active INTEGER DEFAULT 1,
            sent_count INTEGER DEFAULT 0
        )
    `);

    // Audit log table
    db.exec(`
        CREATE TABLE IF NOT EXISTS admin_audit_log (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            admin_email TEXT NOT NULL,
            action TEXT NOT NULL,
            details TEXT,
            timestamp INTEGER NOT NULL
        )
    `);

    // Server secrets table for gateway authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS server_secrets (
            server_id TEXT PRIMARY KEY,
            secret_hash TEXT NOT NULL,
            registered_ip TEXT,
            first_registered_at INTEGER NOT NULL,
            last_seen_at INTEGER NOT NULL
        )
    `);

    // Migrate: add registered_ip column if missing (pre-security-fix databases)
    try {
        const tableInfo = db.prepare('PRAGMA table_info(server_secrets)').all();
        const hasRegisteredIp = tableInfo.some(col => col.name === 'registered_ip');
        if (!hasRegisteredIp) {
            db.exec('ALTER TABLE server_secrets ADD COLUMN registered_ip TEXT');
            console.log('[DB] Migrated server_secrets: added registered_ip column');
        }
    } catch (e) {
        console.error('[DB] Failed to migrate server_secrets:', e.message);
    }

    // Global tokens (one per player UUID, shared across all servers)
    db.exec(`
        CREATE TABLE IF NOT EXISTS global_tokens (
            uuid TEXT PRIMARY KEY,
            token_hash TEXT NOT NULL,
            username TEXT,
            created_at INTEGER NOT NULL,
            expires_at INTEGER NOT NULL
        )
    `);

    // Server access (which players can access which servers, from permission sync)
    db.exec(`
        CREATE TABLE IF NOT EXISTS server_access (
            uuid TEXT NOT NULL,
            server_id TEXT NOT NULL,
            username TEXT,
            rank TEXT,
            permissions TEXT,
            updated_at INTEGER NOT NULL,
            PRIMARY KEY (uuid, server_id)
        )
    `);

    // User settings synced across servers (color scheme + device fingerprints + theme color)
    db.exec(`
        CREATE TABLE IF NOT EXISTS user_settings (
            uuid TEXT PRIMARY KEY,
            color_scheme TEXT DEFAULT 'blue',
            theme_color TEXT DEFAULT '#2d7aed',
            device_fingerprints TEXT,
            updated_at INTEGER NOT NULL
        )
    `);

    // Add theme_color column if it doesn't exist (migration for existing DBs)
    try { db.exec(`ALTER TABLE user_settings ADD COLUMN theme_color TEXT DEFAULT '#2d7aed'`); } catch (e) { /* column already exists */ }

    // Dev license builds table
    db.exec(`
        CREATE TABLE IF NOT EXISTS license_builds (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            token TEXT NOT NULL UNIQUE,
            tester_name TEXT,
            build_version TEXT,
            build_timestamp INTEGER NOT NULL,
            jar_filename TEXT,
            created_by TEXT NOT NULL,
            created_at INTEGER NOT NULL,
            active INTEGER DEFAULT 1
        )
    `);

    // Suspended servers table
    db.exec(`
        CREATE TABLE IF NOT EXISTS suspended_servers (
            server_id TEXT PRIMARY KEY,
            suspended_at INTEGER NOT NULL,
            suspended_by TEXT NOT NULL,
            reason TEXT
        )
    `);

    // User reviews (one per user)
    db.exec(`
        CREATE TABLE IF NOT EXISTS reviews (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            minecraft_uuid TEXT NOT NULL UNIQUE,
            minecraft_username TEXT NOT NULL,
            rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
            description TEXT NOT NULL,
            created_at INTEGER NOT NULL,
            updated_at INTEGER
        )
    `);

    // Gateway metrics history for charts
    db.exec(`
        CREATE TABLE IF NOT EXISTS gateway_metrics_history (
            timestamp INTEGER PRIMARY KEY,
            servers INTEGER NOT NULL,
            browsers INTEGER NOT NULL,
            admins INTEGER NOT NULL,
            messages_per_sec REAL,
            cpu_usage REAL,
            memory_usage REAL
        )
    `);

    // Link codes for /mx link authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS link_codes (
            code_hash TEXT PRIMARY KEY,
            minecraft_uuid TEXT NOT NULL,
            minecraft_username TEXT NOT NULL,
            server_id TEXT,
            created_at INTEGER NOT NULL,
            expires_at INTEGER NOT NULL,
            used INTEGER DEFAULT 0
        )
    `);

    // User accounts for password-based authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS user_accounts (
            minecraft_uuid TEXT PRIMARY KEY,
            minecraft_username TEXT NOT NULL,
            password_hash TEXT NOT NULL,
            created_at INTEGER NOT NULL,
            updated_at INTEGER NOT NULL,
            last_login_at INTEGER,
            failed_attempts INTEGER DEFAULT 0,
            locked_until INTEGER DEFAULT 0,
            device_fingerprints TEXT DEFAULT '[]',
            auto_sign_in INTEGER DEFAULT 1
        )
    `);

    // User sessions for session-based authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS user_sessions (
            session_id TEXT PRIMARY KEY,
            minecraft_uuid TEXT NOT NULL,
            device_fingerprint_hash TEXT,
            created_at INTEGER NOT NULL,
            expires_at INTEGER NOT NULL,
            last_active_at INTEGER NOT NULL,
            ip_address TEXT
        )
    `);

    // Admin accounts for admin panel authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS admin_accounts (
            minecraft_uuid TEXT PRIMARY KEY,
            totp_secret TEXT,
            totp_verified INTEGER DEFAULT 0,
            created_at INTEGER NOT NULL,
            last_login INTEGER
        )
    `);

    // Admin sessions for admin panel authentication
    db.exec(`
        CREATE TABLE IF NOT EXISTS admin_sessions (
            session_id TEXT PRIMARY KEY,
            minecraft_uuid TEXT NOT NULL,
            created_at INTEGER NOT NULL,
            expires_at INTEGER NOT NULL,
            fully_authenticated INTEGER DEFAULT 0,
            ip_address TEXT
        )
    `);

    db.exec(`
        CREATE TABLE IF NOT EXISTS known_servers (
            server_id TEXT PRIMARY KEY,
            server_name TEXT NOT NULL,
            version TEXT,
            last_seen INTEGER NOT NULL
        )
    `);
}

/**
 * Initialize database with fallback chain:
 * 1. better-sqlite3 (native, fastest - works on Linux/production)
 * 2. sql.js (pure JavaScript - works everywhere including Windows)
 * 3. In-memory Maps (no persistence)
 */
async function initDatabase() {
    // Try better-sqlite3 first (native C++ bindings - fastest)
    try {
        const Database = require('better-sqlite3');
        db = new Database(DB_PATH);
        createTables();
        console.log('[Database] Initialized (better-sqlite3 - native)');
        return;
    } catch (e) {
        console.log('[Database] better-sqlite3 not available:', e.message?.split('\n')[0]);
    }

    // Fall back to sql.js (pure JavaScript SQLite via WebAssembly)
    try {
        const initSqlJs = require('sql.js');
        const SQL = await initSqlJs();

        let sqlDb;
        if (fs.existsSync(DB_PATH)) {
            const fileData = fs.readFileSync(DB_PATH);
            sqlDb = new SQL.Database(fileData);
            console.log('[Database] Loaded existing database from disk');
        } else {
            sqlDb = new SQL.Database();
            console.log('[Database] Created new database');
        }

        db = createSqlJsWrapper(sqlDb);
        createTables();
        console.log('[Database] Initialized (sql.js - pure JavaScript)');
        return;
    } catch (e) {
        console.error('[Database] sql.js not available:', e.message);
    }

    // Final fallback: in-memory only (no persistence)
    console.warn('[Database] No SQLite engine available - using in-memory storage (data will not persist across restarts)');
    db = null;
}

// Store admin connections
const adminClients = new Map();

// Valid Server ID characters (no 0, O, 1, I, l for readability)
const VALID_CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';

// Store connected MC servers: Map<fullServerId, { ws, info, lastHeartbeat, urlPrefix }>
const mcServers = new Map();

// Store browser connections: Map<clientId, { ws, serverId }>
const browserClients = new Map();

// URL prefix registry: Map<urlPrefix, fullServerId>
// Used to quickly look up which server owns a URL prefix
const urlPrefixRegistry = new Map();

// Rate limiting
const MAX_CONNECTIONS_PER_IP = 3;          // Max browser connections per IP
const MESSAGE_RATE_LIMIT = 100;            // Max messages per second per connection
const ipConnectionCounts = new Map();      // ip -> Set of ws connections
const messageRates = new Map();            // ws -> { count, windowStart }

/**
 * Check if a WebSocket connection has exceeded the message rate limit.
 * Uses a sliding window of 1 second.
 */
function isMessageRateLimited(ws) {
    const now = Date.now();
    let rate = messageRates.get(ws);

    if (!rate || now - rate.windowStart >= 1000) {
        // New window
        messageRates.set(ws, { count: 1, windowStart: now });
        return false;
    }

    rate.count++;
    if (rate.count > MESSAGE_RATE_LIMIT) {
        return true;
    }

    return false;
}

/**
 * Validate Server ID format (XXXXX-XXXXX-XXXXX-XXXXX-XXXXX)
 */
function isValidServerId(id) {
    if (!id || typeof id !== 'string') return false;
    const normalized = id.toUpperCase();

    // Check format: 5 groups of 5 chars separated by dashes
    const pattern = /^[A-HJ-NP-Z2-9]{5}-[A-HJ-NP-Z2-9]{5}-[A-HJ-NP-Z2-9]{5}-[A-HJ-NP-Z2-9]{5}-[A-HJ-NP-Z2-9]{5}$/;
    return pattern.test(normalized);
}

/**
 * Validate a server's gateway secret.
 * On first registration: stores the SHA-256 hash of the secret + connecting IP.
 * On subsequent registrations: verifies the secret matches the stored hash.
 * If secret mismatch but IP matches the registered IP, allows re-registration
 * (handles config reset / plugin folder deletion without manual intervention).
 * Returns true if valid, false if mismatch.
 */
function validateServerSecret(serverId, secret, connectingIp) {
    if (!secret || secret.length === 0) {
        // No secret provided - reject if we already have one stored
        const existing = getStoredSecretRecord(serverId);
        if (existing) {
            return false; // Server previously registered with a secret
        }
        // First registration without secret - allow but warn
        console.log(`[Server] WARNING: ${serverId} registered without a gateway secret`);
        return true;
    }

    const secretHash = crypto.createHash('sha256').update(secret).digest('hex');
    const existing = getStoredSecretRecord(serverId);

    if (!existing) {
        // First registration - store the secret hash + IP
        storeSecretHash(serverId, secretHash, connectingIp);
        console.log(`[Server] ${serverId} first registration - secret hash stored (IP: ${connectingIp})`);
        return true;
    }

    // Subsequent registration - verify secret matches
    if (existing.secret_hash === secretHash) {
        updateSecretLastSeen(serverId, connectingIp);
        return true;
    }

    // Secret mismatch — check if connecting from same or local IP (config was likely reset)
    if (isSameOrLocalIp(connectingIp, existing.registered_ip)) {
        storeSecretHash(serverId, secretHash, connectingIp);
        console.log(`[Server] ${serverId} secret updated — same/local IP (${connectingIp}), config likely reset`);
        addAuditEntry('system', 'secret_reregister', `Server ${serverId} re-registered from same/local IP (config reset detected)`);
        return true;
    }

    return false; // Secret mismatch from different IP
}

/**
 * Check if two IPs are the same or both local (localhost/private network).
 */
function isSameOrLocalIp(connectingIp, registeredIp) {
    if (!connectingIp || !registeredIp) return false;
    if (connectingIp === registeredIp) return true;

    // Normalize localhost variants
    const localIps = ['127.0.0.1', '::1', '::ffff:127.0.0.1', '0:0:0:0:0:0:0:1'];
    if (localIps.includes(connectingIp) && localIps.includes(registeredIp)) return true;

    // Same private subnet (192.168.x.x or 10.x.x.x)
    if (connectingIp.startsWith('192.168.') && registeredIp.startsWith('192.168.')) return true;
    if (connectingIp.startsWith('10.') && registeredIp.startsWith('10.')) return true;

    return false;
}

/**
 * Get stored secret record for a server ID (hash + IP).
 */
function getStoredSecretRecord(serverId) {
    if (db) {
        try {
            const row = db.prepare('SELECT secret_hash, registered_ip FROM server_secrets WHERE server_id = ?').get(serverId);
            return row || null;
        } catch (e) {
            console.error('[Server] Failed to query server secret:', e.message);
        }
    }
    const hash = inMemoryServerSecrets.get(serverId);
    return hash ? { secret_hash: hash, registered_ip: null } : null;
}

/**
 * Store a new secret hash + IP for a server ID.
 */
function storeSecretHash(serverId, secretHash, registeredIp) {
    const now = Date.now();
    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO server_secrets (server_id, secret_hash, registered_ip, first_registered_at, last_seen_at) VALUES (?, ?, ?, ?, ?)'
            ).run(serverId, secretHash, registeredIp || null, now, now);
        } catch (e) {
            console.error('[Server] Failed to store server secret:', e.message);
            inMemoryServerSecrets.set(serverId, secretHash);
        }
    } else {
        inMemoryServerSecrets.set(serverId, secretHash);
    }
}

/**
 * Update the last_seen_at timestamp and IP for a server.
 */
function updateSecretLastSeen(serverId, connectingIp) {
    if (db) {
        try {
            db.prepare('UPDATE server_secrets SET last_seen_at = ?, registered_ip = ? WHERE server_id = ?').run(Date.now(), connectingIp || null, serverId);
        } catch (e) {
            console.error('[Server] Failed to update last_seen_at:', e.message);
        }
    }
}

/**
 * Calculate minimum URL prefix needed for uniqueness
 * Returns number of groups needed (1-5)
 */
function calculateUrlPrefixGroups(serverId, excludeExisting = false) {
    const groups = serverId.toLowerCase().split('-');

    for (let numGroups = 1; numGroups <= 5; numGroups++) {
        const prefix = groups.slice(0, numGroups).join('-');
        const existingOwner = urlPrefixRegistry.get(prefix);

        // If no one owns this prefix, or it's the same server, we can use it
        if (!existingOwner || (excludeExisting && existingOwner === serverId.toLowerCase())) {
            return numGroups;
        }
    }

    return 5; // Full ID required (should never happen with unique IDs)
}

/**
 * Get URL prefix for a server ID
 */
function getUrlPrefix(serverId, numGroups) {
    const groups = serverId.toLowerCase().split('-');
    return groups.slice(0, numGroups).join('-');
}

/**
 * Register URL prefix for a server
 */
function registerUrlPrefix(serverId) {
    const numGroups = calculateUrlPrefixGroups(serverId);
    const prefix = getUrlPrefix(serverId, numGroups);

    urlPrefixRegistry.set(prefix, serverId.toLowerCase());

    return { prefix, numGroups };
}

/**
 * Unregister URL prefix for a server
 */
function unregisterUrlPrefix(serverId) {
    // Find and remove all prefixes owned by this server
    const lowerServerId = serverId.toLowerCase();
    for (const [prefix, owner] of urlPrefixRegistry.entries()) {
        if (owner === lowerServerId) {
            urlPrefixRegistry.delete(prefix);
        }
    }
}

/**
 * Find server by URL prefix (partial or full)
 * Returns the full server ID if found
 */
function findServerByPrefix(prefix) {
    const lowerPrefix = prefix.toLowerCase();

    // Strict match only — check registered prefix registry
    if (urlPrefixRegistry.has(lowerPrefix)) {
        return urlPrefixRegistry.get(lowerPrefix);
    }

    // Also check full server IDs (exact match)
    if (mcServers.has(lowerPrefix)) {
        return lowerPrefix;
    }

    return null;
}

// Create HTTP server
const server = http.createServer((req, res) => {
    const url = new URL(req.url, `http://${req.headers.host}`);

    // CORS headers for API endpoints
    const allowedOrigins = ['https://moderex.net', 'https://www.moderex.net', 'https://panel.moderex.net', 'https://panel-moderex.pages.dev', 'https://moderex.pages.dev'];
    const origin = req.headers.origin;
    if (origin && (allowedOrigins.includes(origin) || origin.endsWith('.trycloudflare.com'))) {
        res.setHeader('Access-Control-Allow-Origin', origin);
    } else if (!origin) {
        res.setHeader('Access-Control-Allow-Origin', '*');
    }
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    // Health check endpoint
    if (url.pathname === '/health') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            status: 'ok',
            servers: mcServers.size,
            clients: browserClients.size,
            uptime: process.uptime()
        }));
        return;
    }

    // API: List connected servers (admin)
    if (url.pathname === '/api/servers') {

        const serverList = [];
        mcServers.forEach((data, id) => {
            serverList.push({
                id: id,
                name: data.info?.serverName || 'Unknown',
                urlPrefix: data.urlPrefix || id,
                players: data.info?.players || 0,
                connectedAt: data.connectedAt,
                lastHeartbeat: data.lastHeartbeat
            });
        });
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ servers: serverList, total: mcServers.size }));
        return;
    }

    // API: Check if server exists by prefix or full ID
    // Only returns exists boolean — no server details to prevent enumeration
    const serverCheckMatch = url.pathname.match(/^\/api\/server\/([A-Za-z0-9-]+)$/);
    if (serverCheckMatch) {
        const query = serverCheckMatch[1].toLowerCase();
        const serverId = findServerByPrefix(query);
        const exists = serverId && mcServers.has(serverId);

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ exists: exists }));
        return;
    }

    // API: Stats for landing page (minimal — no uptime, no details)
    if (url.pathname === '/api/stats') {
        let totalPlayers = 0;
        mcServers.forEach((data) => {
            totalPlayers += data.info?.players || 0;
        });

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            servers: mcServers.size,
            players: totalPlayers
        }));
        return;
    }

    // ================================================================
    // Link & Auth HTTP API Endpoints
    // ================================================================

    // POST /api/link/verify — Verify a 10-digit link code
    if (url.pathname === '/api/link/verify' && req.method === 'POST') {
        readJsonBody(req, (body) => {
            if (!body || !body.code) {
                return jsonResponse(res, 400, { error: 'Code is required' });
            }

            // Rate limit: 5 attempts per IP per 10 minutes
            const attempts = linkVerifyAttempts.get(clientIpFromReq(req)) || { count: 0, firstAttempt: Date.now() };
            if (Date.now() - attempts.firstAttempt > 10 * 60 * 1000) {
                attempts.count = 0;
                attempts.firstAttempt = Date.now();
            }
            attempts.count++;
            linkVerifyAttempts.set(clientIpFromReq(req), attempts);
            if (attempts.count > 5) {
                return jsonResponse(res, 429, { error: 'Too many attempts. Please wait before trying again.' });
            }

            // Hash the code and look up
            const codeHash = crypto.createHash('sha256').update(body.code.replace(/\D/g, '')).digest('hex');
            const linkCode = getLinkCode(codeHash);

            if (!linkCode) {
                return jsonResponse(res, 404, { error: 'Invalid or expired code' });
            }

            if (linkCode.used) {
                return jsonResponse(res, 410, { error: 'This code has already been used' });
            }

            // Check if user already has an account
            const account = getUserAccount(linkCode.minecraft_uuid);
            const hasAccount = !!account;

            jsonResponse(res, 200, {
                uuid: linkCode.minecraft_uuid,
                username: linkCode.minecraft_username,
                hasAccount,
                codeHash // Send back so client can reference it for register/login
            });
        });
        return;
    }

    // POST /api/link/register — Create account with password (new users)
    if (url.pathname === '/api/link/register' && req.method === 'POST') {
        handleLinkRegister(req, res);
        return;
    }

    // POST /api/link/login — Auto-login for existing users (code-verified)
    if (url.pathname === '/api/link/login' && req.method === 'POST') {
        handleLinkLogin(req, res);
        return;
    }

    // POST /api/admin/auth — Admin login (username + password, validates UUID is in ADMIN_UUIDS)
    if (url.pathname === '/api/admin/auth' && req.method === 'POST') {
        handleAdminAuth(req, res);
        return;
    }

    // POST /api/admin/auth/2fa — Verify TOTP code for admin session
    if (url.pathname === '/api/admin/auth/2fa' && req.method === 'POST') {
        handleAdmin2FA(req, res);
        return;
    }

    // POST /api/admin/2fa/setup — Get TOTP setup QR data (requires admin session)
    if (url.pathname === '/api/admin/2fa/setup' && req.method === 'POST') {
        handleAdmin2FASetup(req, res);
        return;
    }

    // POST /api/admin/2fa/verify — Confirm initial 2FA setup with verification code
    if (url.pathname === '/api/admin/2fa/verify' && req.method === 'POST') {
        handleAdmin2FAVerify(req, res);
        return;
    }

    // POST /api/admin/session/validate — Validate admin session
    if (url.pathname === '/api/admin/session/validate' && req.method === 'POST') {
        handleAdminSessionValidate(req, res);
        return;
    }

    // POST /api/auth/login — Password login
    if (url.pathname === '/api/auth/login' && req.method === 'POST') {
        handlePasswordLogin(req, res);
        return;
    }

    // POST /api/auth/fingerprint — Device fingerprint auto-sign-in
    if (url.pathname === '/api/auth/fingerprint' && req.method === 'POST') {
        handleFingerprintAuth(req, res);
        return;
    }

    // POST /api/auth/password/change — Change password
    if (url.pathname === '/api/auth/password/change' && req.method === 'POST') {
        handlePasswordChange(req, res);
        return;
    }

    // POST /api/auth/session/validate — Validate session token
    if (url.pathname === '/api/auth/session/validate' && req.method === 'POST') {
        readJsonBody(req, (body) => {
            if (!body || !body.sessionToken) {
                return jsonResponse(res, 400, { error: 'Session token is required' });
            }
            const session = validateSession(body.sessionToken);
            if (!session) {
                return jsonResponse(res, 401, { error: 'Invalid or expired session' });
            }
            const account = getUserAccount(session.minecraft_uuid);
            jsonResponse(res, 200, {
                valid: true,
                uuid: session.minecraft_uuid,
                username: account?.minecraft_username || null,
                isAdmin: ADMIN_UUIDS.has(session.minecraft_uuid)
            });
        });
        return;
    }

    // POST /api/auth/session/revoke-all — Revoke all sessions for a user
    if (url.pathname === '/api/auth/session/revoke-all' && req.method === 'POST') {
        readJsonBody(req, (body) => {
            if (!body || !body.sessionToken) {
                return jsonResponse(res, 400, { error: 'Session token is required' });
            }
            const session = validateSession(body.sessionToken);
            if (!session) {
                return jsonResponse(res, 401, { error: 'Invalid or expired session' });
            }
            revokeAllSessions(session.minecraft_uuid);
            jsonResponse(res, 200, { success: true, message: 'All sessions revoked' });
        });
        return;
    }

    // GET /api/reviews — Public, returns all reviews
    if (url.pathname === '/api/reviews' && req.method === 'GET') {
        const reviews = getAllReviews();
        const publicReviews = reviews.map(r => ({
            username: r.minecraft_username,
            uuid: r.minecraft_uuid,
            rating: r.rating,
            description: r.description,
            date: r.created_at
        }));
        jsonResponse(res, 200, { reviews: publicReviews });
        return;
    }

    // POST /api/reviews — Submit or update a review (requires auth)
    if (url.pathname === '/api/reviews' && req.method === 'POST') {
        readJsonBody(req, (body) => {
            const authHeader = req.headers['authorization'];
            if (!authHeader || !authHeader.startsWith('Bearer ')) {
                return jsonResponse(res, 401, { error: 'Authentication required' });
            }
            const token = authHeader.substring(7);
            const session = validateSession(token);
            if (!session) {
                return jsonResponse(res, 401, { error: 'Invalid or expired session' });
            }
            if (!body || typeof body.rating !== 'number' || body.rating < 1 || body.rating > 5) {
                return jsonResponse(res, 400, { error: 'Rating must be between 1 and 5' });
            }
            if (!body.description || typeof body.description !== 'string' || body.description.trim().length < 10) {
                return jsonResponse(res, 400, { error: 'Description must be at least 10 characters' });
            }
            if (body.description.length > 500) {
                return jsonResponse(res, 400, { error: 'Description must be under 500 characters' });
            }
            const account = getUserAccount(session.minecraft_uuid);
            const username = account?.minecraft_username || 'Unknown';
            upsertReview(session.minecraft_uuid, username, body.rating, body.description.trim());
            jsonResponse(res, 200, { success: true, message: 'Review submitted' });
        });
        return;
    }

    // Download licensed JAR files (admin-only, uses secret token in query)
    const downloadMatch = url.pathname.match(/^\/download\/(.+\.jar)$/);
    if (downloadMatch) {
        const filename = path.basename(downloadMatch[1]); // Prevent path traversal
        const jarPath = path.resolve(path.join(__dirname, 'licensed-builds', filename));
        const buildsDir = path.resolve(path.join(__dirname, 'licensed-builds'));

        // Ensure path stays within licensed-builds directory
        if (!jarPath.startsWith(buildsDir + path.sep)) {
            res.writeHead(403, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ error: 'Forbidden' }));
            return;
        }

        if (fs.existsSync(jarPath) && fs.statSync(jarPath).isFile()) {
            const stat = fs.statSync(jarPath);
            res.writeHead(200, {
                'Content-Type': 'application/java-archive',
                'Content-Disposition': `attachment; filename="${filename}"`,
                'Content-Length': stat.size
            });
            fs.createReadStream(jarPath).pipe(res);
            return;
        }

        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'File not found' }));
        return;
    }

    // Serve panel files if they exist locally (for development)
    const panelDir = path.resolve(path.join(__dirname, 'panel'));
    const requestedFile = url.pathname === '/' ? 'index.html' : url.pathname;
    const panelPath = path.resolve(path.join(panelDir, requestedFile));
    // Prevent path traversal - resolved path must stay within panel directory
    if (panelPath.startsWith(panelDir + path.sep) || panelPath === panelDir) {
        if (fs.existsSync(panelPath) && fs.statSync(panelPath).isFile()) {
            const ext = path.extname(panelPath);
            const contentTypes = {
                '.html': 'text/html',
                '.css': 'text/css',
                '.js': 'application/javascript',
                '.json': 'application/json',
                '.png': 'image/png',
                '.jpg': 'image/jpeg',
                '.svg': 'image/svg+xml',
                '.ico': 'image/x-icon',
                '.woff': 'font/woff',
                '.woff2': 'font/woff2',
                '.ttf': 'font/ttf'
            };
            res.writeHead(200, { 'Content-Type': contentTypes[ext] || 'text/plain' });
            fs.createReadStream(panelPath).pipe(res);
            return;
        }
    }

    // For any other path, serve index.html (SPA routing)
    const indexPath = path.join(__dirname, 'panel', 'index.html');
    if (fs.existsSync(indexPath)) {
        res.writeHead(200, { 'Content-Type': 'text/html' });
        fs.createReadStream(indexPath).pipe(res);
        return;
    }

    // No panel files - just return info
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(`
        <!DOCTYPE html>
        <html>
        <head><title>ModereX Gateway</title></head>
        <body style="font-family: system-ui; max-width: 600px; margin: 50px auto; padding: 20px;">
            <h1>ModereX Gateway</h1>
            <p>Gateway is running. Connected servers: <strong>${mcServers.size}</strong></p>
            <p>To access a panel, use: <code>/&lt;server-id&gt;/</code></p>
            <p>Server ID format: <code>XXXXX-XXXXX-XXXXX-XXXXX-XXXXX</code></p>
            <p>Short URL: <code>/xxxxx/</code> (first group, expands if needed)</p>
            <hr>
            <p><small>Panel files not found. Deploy panel to Cloudflare Pages or place in ./panel/</small></p>
        </body>
        </html>
    `);
});

// Create WebSocket server
const wss = new WebSocketServer({ server, maxPayload: 100 * 1024 * 1024 }); // 100MB max message size (for evidence uploads)

// Track failed server registration attempts per IP for rate limiting
const failedServerAuthAttempts = new Map(); // IP → { count, firstAttempt }
const failedTokenAuthAttempts = new Map(); // IP → { count, lastAttempt }
const MAX_SERVER_CONNECTIONS_PER_IP = 3;
const SERVER_AUTH_BAN_THRESHOLD = 3;
const SERVER_AUTH_BAN_DURATION = 10 * 60 * 1000; // 10 minutes

// Handle WebSocket connections
wss.on('connection', (ws, req) => {
    const url = new URL(req.url, `http://${req.headers.host}`);

    // Fix 5: Only trust cf-connecting-ip when cf-ray is also present (real Cloudflare)
    const isBehindCF = !!(req.headers['cf-ray'] && req.headers['cf-connecting-ip']);
    const clientIp = isBehindCF
        ? req.headers['cf-connecting-ip']
        : req.socket.remoteAddress;

    // DDoS protection check
    if (!ddosProtection.checkConnection(clientIp)) {
        console.warn(`[DDoS] Rejected connection from ${clientIp}`);
        ws.close(1008, 'Rate limited');
        return;
    }
    ddosProtection.trackConnection(clientIp, ws);

    // Track disconnection for DDoS cleanup
    ws.on('close', () => {
        ddosProtection.removeConnection(clientIp, ws);
    });

    console.log(`[WS] New connection from ${clientIp} - ${url.pathname}`);

    // Determine connection type from URL path
    // /server - MC server connection
    // /panel/{prefix} - Browser panel connection (prefix can be partial or full)

    const isServerConnection = url.pathname === '/server' || url.searchParams.get('type') === 'server';
    const isAdminConnection = url.pathname === '/admin';

    // Fix 11: Origin validation for browser/panel connections
    if (!isServerConnection) {
        const origin = req.headers.origin;
        const allowedOrigins = [
            'https://moderex.net',
            'https://www.moderex.net',
            'https://panel.moderex.net',
            'https://gateway.moderex.net',
            'https://panel-moderex.pages.dev',
            'https://moderex.pages.dev'
        ];
        // Allow connections with no origin (non-browser) or matching origin
        // Also allow any trycloudflare.com origin for development
        if (origin && !allowedOrigins.includes(origin) && !origin.endsWith('.trycloudflare.com')) {
            console.log(`[Security] Rejected connection from invalid origin: ${origin}`);
            ws.close(4403, 'Invalid origin');
            return;
        }
    }

    // Rate limit connections per IP (server connections)
    if (isServerConnection) {
        // Per-IP server connection limit
        const ipConns = ipConnectionCounts.get(clientIp) || new Set();
        for (const existingWs of ipConns) {
            if (existingWs.readyState !== WebSocket.OPEN) ipConns.delete(existingWs);
        }
        if (ipConns.size >= MAX_SERVER_CONNECTIONS_PER_IP) {
            console.log(`[RateLimit] ${clientIp} exceeded max server connections`);
            ws.close(4029, 'Too many server connections from this IP');
            return;
        }
        ipConns.add(ws);
        ipConnectionCounts.set(clientIp, ipConns);
        ws.on('close', () => {
            const conns = ipConnectionCounts.get(clientIp);
            if (conns) {
                conns.delete(ws);
                if (conns.size === 0) ipConnectionCounts.delete(clientIp);
            }
            messageRates.delete(ws);
        });
    } else if (!isAdminConnection) {
        // Browser connection rate limiting (existing logic)
        const ipConns = ipConnectionCounts.get(clientIp) || new Set();
        for (const existingWs of ipConns) {
            if (existingWs.readyState !== WebSocket.OPEN) ipConns.delete(existingWs);
        }
        if (ipConns.size >= MAX_CONNECTIONS_PER_IP) {
            console.log(`[RateLimit] ${clientIp} exceeded max connections (${ipConns.size}/${MAX_CONNECTIONS_PER_IP})`);
            ws.close(4029, 'Too many connections from this IP');
            return;
        }
        ipConns.add(ws);
        ipConnectionCounts.set(clientIp, ipConns);
        ws.on('close', () => {
            const conns = ipConnectionCounts.get(clientIp);
            if (conns) {
                conns.delete(ws);
                if (conns.size === 0) ipConnectionCounts.delete(clientIp);
            }
            messageRates.delete(ws);
        });
    }

    if (url.pathname === '/server') {
        handleMCServerConnection(ws, clientIp);
    } else if (url.pathname === '/panel/' || url.pathname === '/panel') {
        // Global panel connection (no server prefix) — server list page
        handleGlobalPanelConnection(ws, clientIp);
    } else if (url.pathname.startsWith('/panel/')) {
        const prefix = url.pathname.split('/')[2]?.toLowerCase();
        if (prefix && prefix.length >= 5) {
            handleBrowserConnection(ws, prefix, clientIp);
        } else {
            ws.send(JSON.stringify({
                type: 'error',
                code: 'INVALID_PREFIX',
                message: 'Server ID prefix must be at least 5 characters'
            }));
            ws.close(4001, 'Invalid server ID prefix');
        }
    } else if (isAdminConnection) {
        // Admin panel connection - requires Cloudflare Access authentication
        const cfEmail = req.headers['cf-access-authenticated-user-email'];
        handleAdminConnection(ws, clientIp, cfEmail, req);
    } else {
        // Legacy: check query params
        const type = url.searchParams.get('type');
        const serverId = url.searchParams.get('server')?.toLowerCase();

        if (type === 'server') {
            handleMCServerConnection(ws, clientIp);
        } else if (type === 'panel' && serverId) {
            handleBrowserConnection(ws, serverId, clientIp);
        } else {
            ws.close(4000, 'Unknown connection type');
        }
    }
});

/**
 * Handle MC server plugin connection
 */
function handleMCServerConnection(ws, clientIp) {
    let serverId = null;
    let registered = false;

    ws.on('message', (data) => {
        try {
            const message = JSON.parse(data.toString());

            // Registration message from MC server
            if (message.type === 'register') {
                const rawServerId = message.serverId;

                // Validate server ID format
                if (!isValidServerId(rawServerId)) {
                    ws.send(JSON.stringify({
                        type: 'error',
                        code: 'INVALID_SERVER_ID',
                        message: 'Invalid server ID format. Expected: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX'
                    }));
                    ws.close(4002, 'Invalid server ID');
                    return;
                }

                serverId = rawServerId.toLowerCase();

                // Validate gateway secret authentication
                const secret = message.secret || '';
                const secretValid = validateServerSecret(serverId, secret, clientIp);
                if (!secretValid) {
                    console.log(`[Server] ${serverId} rejected: invalid gateway secret from ${clientIp}`);

                    ws.send(JSON.stringify({
                        type: 'error',
                        code: 'INVALID_SECRET',
                        message: 'Gateway authentication failed. Server secret mismatch. If you reset your config, the server will auto-recover if connecting from the same or local IP.'
                    }));
                    ws.close(4005, 'Invalid gateway secret');
                    return;
                }

                // Check if server is suspended
                if (isServerSuspended(serverId)) {
                    console.log(`[Server] ${serverId} rejected: server is suspended`);

                    ws.send(JSON.stringify({
                        type: 'error',
                        code: 'SERVER_SUSPENDED',
                        message: 'This server has been suspended by an administrator. Contact support for more information.'
                    }));
                    ws.close(4006, 'Server suspended');
                    return;
                }

                // Check if server ID already registered
                if (mcServers.has(serverId)) {
                    const existing = mcServers.get(serverId);
                    // Close old connection if exists
                    if (existing.ws.readyState === WebSocket.OPEN) {
                        existing.ws.close(4003, 'Replaced by new connection');
                    }
                    // Unregister old URL prefix
                    unregisterUrlPrefix(serverId);
                    console.log(`[Server] ${serverId} reconnected (replaced old connection)`);
                }

                // Register URL prefix for this server
                const { prefix, numGroups } = registerUrlPrefix(serverId);

                // Register this server
                mcServers.set(serverId, {
                    ws: ws,
                    info: {
                        serverName: message.serverName || 'Unknown Server',
                        version: message.version || 'unknown',
                        players: message.players || 0
                    },
                    urlPrefix: prefix,
                    urlPrefixGroups: numGroups,
                    connectedAt: Date.now(),
                    lastHeartbeat: Date.now(),
                    clientIp: clientIp
                });

                // Persist server info for offline name lookups
                if (db) {
                    try {
                        db.prepare('INSERT INTO known_servers (server_id, server_name, version, last_seen) VALUES (?, ?, ?, ?) ON CONFLICT(server_id) DO UPDATE SET server_name = excluded.server_name, version = excluded.version, last_seen = excluded.last_seen')
                            .run(serverId, message.serverName || 'Unknown', message.version || 'unknown', Date.now());
                    } catch (e) {}
                }

                registered = true;
                console.log(`[Server] ${serverId} registered: ${message.serverName} (URL: /${prefix}/)`);

                ws.send(JSON.stringify({
                    type: 'registered',
                    serverId: serverId,
                    urlPrefix: prefix,
                    urlPrefixGroups: numGroups,
                    panelUrl: `panel.moderex.net/${prefix}/`,
                    message: 'Successfully registered with gateway'
                }));

                // Notify waiting browsers that this server is back online
                browserClients.forEach((client, clientId) => {
                    if (client.serverId === serverId && client.ws.readyState === WebSocket.OPEN) {
                        client.ws.send(JSON.stringify({
                            type: 'server_online',
                            serverId: serverId,
                            serverName: message.serverName || 'Unknown Server'
                        }));
                    }
                });

                return;
            }

            // Heartbeat
            if (message.type === 'heartbeat') {
                if (serverId && mcServers.has(serverId)) {
                    const server = mcServers.get(serverId);
                    server.lastHeartbeat = Date.now();
                    if (message.players !== undefined) {
                        server.info.players = message.players;
                    }
                }
                ws.send(JSON.stringify({ type: 'heartbeat_ack' }));
                return;
            }

            // Forward messages to connected browsers
            if (message.type === 'panel_response' && message.clientId) {
                const client = browserClients.get(message.clientId);
                if (client && client.ws.readyState === WebSocket.OPEN) {
                    // Extract the wrapped response and forward it
                    // Server sends: { type: 'panel_response', clientId: '...', response: { type: 'PLAYERS', data: [...] } }
                    // Browser receives: { type: 'PLAYERS', data: [...] }
                    const responseToSend = message.response || message;
                    if (message.response) {
                        client.ws.send(JSON.stringify(message.response));
                        // Cache SERVER_STATUS for new connections
                        if (message.response.type === 'SERVER_STATUS' && serverId && mcServers.has(serverId)) {
                            mcServers.get(serverId).lastStatus = message.response;
                        }
                    } else {
                        // Fallback for backwards compatibility - remove internal fields
                        delete message.clientId;
                        delete message.type;
                        client.ws.send(JSON.stringify(message));
                    }
                }
                return;
            }

            // Broadcast to all browsers connected to this server
            if (message.type === 'broadcast') {
                // Cache SERVER_STATUS broadcasts for new connections
                if (message.data && message.data.type === 'SERVER_STATUS' && serverId && mcServers.has(serverId)) {
                    mcServers.get(serverId).lastStatus = message.data;
                }
                broadcastToServer(serverId, message.data);
                return;
            }

            // ============================================================
            // Global Token System — MC Server → Gateway messages
            // ============================================================

            // Bulk permission sync (sent on server startup)
            if (message.type === 'permission_sync') {
                if (!serverId || !registered) return;
                handlePermissionSync(serverId, message.players || []);
                return;
            }

            // Single player permission update (LP event)
            if (message.type === 'permission_update') {
                if (!serverId || !registered) return;
                handlePermissionUpdate(serverId, message);
                return;
            }

            // Register link code hash from MC server (/mx link)
            if (message.type === 'link_code_register') {
                if (!serverId || !registered) return;
                const { uuid, username, codeHash, expiresAt } = message;
                if (!uuid || !codeHash) return;
                storeLinkCode(codeHash, uuid, username, serverId, expiresAt || (Date.now() + 10 * 60 * 1000));
                console.log(`[Auth] Link code registered for ${username || uuid} from ${serverId}`);
                return;
            }

            // Register/update global token
            if (message.type === 'token_register') {
                handleTokenRegister(message);
                return;
            }

            // Revoke global token
            if (message.type === 'token_revoke') {
                handleTokenRevoke(message.uuid);
                return;
            }

            // Sync user settings from MC server → gateway
            if (message.type === 'settings_sync') {
                handleSettingsSync(message);
                return;
            }

            // Server unregistering from gateway (gateway disabled in config)
            if (message.type === 'server_unregister') {
                if (!serverId || !registered) return;
                handleServerUnregister(serverId);
                return;
            }

            // Global pre-auth result (MC server created session for global-auth user)
            if (message.type === 'global_pre_auth_result') {
                handleGlobalPreAuthResult(message);
                return;
            }

        } catch (err) {
            console.error(`[Server] Error processing message:`, err.message);
        }
    });

    ws.on('close', () => {
        if (serverId && mcServers.has(serverId)) {
            // Unregister URL prefix
            unregisterUrlPrefix(serverId);
            mcServers.delete(serverId);
            console.log(`[Server] ${serverId} disconnected`);

            // Notify all browsers connected to this server
            browserClients.forEach((client, clientId) => {
                if (client.serverId === serverId && client.ws.readyState === WebSocket.OPEN) {
                    client.ws.send(JSON.stringify({
                        type: 'server_disconnected',
                        message: 'Minecraft server disconnected from gateway'
                    }));
                }
            });
        }
    });

    ws.on('error', (err) => {
        console.error(`[Server] WebSocket error:`, err.message);
    });
}

/**
 * Handle browser panel connection
 */
function handleBrowserConnection(ws, prefix, clientIp) {
    const clientId = generateClientId();

    // Find server by prefix
    const serverId = findServerByPrefix(prefix);

    // Check if server exists
    if (!serverId || !mcServers.has(serverId)) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'SERVER_NOT_FOUND',
            message: 'Server not connected to gateway'
        }));
        ws.close(4004, 'Server not found');
        return;
    }

    // Register browser client
    browserClients.set(clientId, {
        ws: ws,
        serverId: serverId,
        connectedAt: Date.now(),
        clientIp: clientIp
    });

    console.log(`[Browser] ${clientId} connected to server ${serverId} (via prefix: ${prefix})`);

    const serverData = mcServers.get(serverId);

    // Notify browser of successful connection
    ws.send(JSON.stringify({
        type: 'connected',
        serverId: serverId,
        serverName: serverData.info.serverName,
        urlPrefix: serverData.urlPrefix
    }));

    // Send cached server status immediately if available
    if (serverData.lastStatus) {
        ws.send(JSON.stringify(serverData.lastStatus));
    }

    // Notify MC server of new browser connection
    if (serverData.ws.readyState === WebSocket.OPEN) {
        serverData.ws.send(JSON.stringify({
            type: 'browser_connected',
            clientId: clientId,
            clientIp: clientIp
        }));
    }

    ws.on('message', (data) => {
        try {
            // DDoS: Message size check
            if (data.length > ddosProtection.maxMessageSize) {
                ws.send(JSON.stringify({ type: 'error', code: 'MESSAGE_TOO_LARGE', message: 'Message exceeds size limit' }));
                return;
            }

            // DDoS: Per-IP message rate check
            if (!ddosProtection.checkMessageRate(clientIp)) {
                ws.close(1008, 'Rate limited');
                return;
            }

            // Per-connection message rate limiting
            if (isMessageRateLimited(ws)) {
                ws.send(JSON.stringify({
                    type: 'error',
                    code: 'RATE_LIMITED',
                    message: 'Too many messages. Slow down.'
                }));
                return;
            }

            const message = JSON.parse(data.toString());

            // Fix 6: Allowlist browser message types — block server-internal types
            if (message.type && BLOCKED_BROWSER_TYPES.has(message.type)) {
                ws.send(JSON.stringify({ type: 'error', code: 'INVALID_TYPE', message: 'Message type not allowed from browser' }));
                return;
            }

            // Intercept AUTH_PASSWORD — gateway handles password auth (password DB lives here)
            if (message.type === 'AUTH_PASSWORD') {
                handleBrowserPasswordAuth(ws, clientId, serverId, clientIp, message);
                return;
            }

            // Intercept save_settings — store theme color in gateway DB
            if (message.type === 'save_settings') {
                const browserClient = browserClients.get(clientId);
                if (browserClient?.authedUuid) {
                    const existing = getUserSettings(browserClient.authedUuid);
                    saveUserSettings(browserClient.authedUuid, message.colorScheme || existing.colorScheme, existing.deviceFingerprints, message.themeColor || existing.themeColor);
                    ws.send(JSON.stringify({ type: 'settings_saved', success: true }));
                }
                return;
            }

            // Forward to MC server
            const server = mcServers.get(serverId);
            if (server && server.ws.readyState === WebSocket.OPEN) {
                // Strip any client-supplied clientId/clientIp and set authenticated values
                delete message.clientId;
                delete message.clientIp;
                message.clientId = clientId;
                message.clientIp = clientIp;
                server.ws.send(JSON.stringify(message));
            } else {
                ws.send(JSON.stringify({
                    type: 'error',
                    code: 'SERVER_OFFLINE',
                    message: 'Server went offline'
                }));
            }
        } catch (err) {
            console.error(`[Browser] Error processing message:`, err.message);
        }
    });

    ws.on('close', () => {
        browserClients.delete(clientId);
        console.log(`[Browser] ${clientId} disconnected`);

        // Notify MC server
        const server = mcServers.get(serverId);
        if (server && server.ws.readyState === WebSocket.OPEN) {
            server.ws.send(JSON.stringify({
                type: 'browser_disconnected',
                clientId: clientId
            }));
        }
    });

    ws.on('error', (err) => {
        console.error(`[Browser] WebSocket error:`, err.message);
    });
}

/**
 * Handle AUTH_PASSWORD from a browser connected directly to a server.
 * Password DB lives on the gateway, so we validate here then send
 * global_pre_auth to the MC server to create a session.
 */
async function handleBrowserPasswordAuth(ws, clientId, serverId, clientIp, message) {
    const data = message.data || message;
    const username = data.username;
    const password = data.password;
    const deviceFingerprint = data.deviceFingerprint;

    if (!username || !password) {
        ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Username and password required' } }));
        return;
    }

    if (!argon2) {
        ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Password auth not available' } }));
        return;
    }

    // Look up account in gateway DB
    let account = null;
    if (db) {
        try {
            account = db.prepare('SELECT * FROM user_accounts WHERE LOWER(minecraft_username) = LOWER(?)').get(username);
        } catch (e) {}
    } else {
        for (const [, acc] of inMemoryUserAccounts) {
            if (acc.minecraft_username.toLowerCase() === username.toLowerCase()) { account = acc; break; }
        }
    }

    if (!account) {
        ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Invalid username or password' } }));
        return;
    }

    // Check lockout
    if (account.locked_until && account.locked_until > Date.now()) {
        ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Account locked. Please wait.' } }));
        return;
    }

    try {
        const valid = await argon2.verify(account.password_hash, password);
        if (!valid) {
            const failedCount = (account.failed_attempts || 0) + 1;
            const lockUntil = failedCount >= 5 ? Date.now() + 15 * 60 * 1000 : 0;
            if (db) { try { db.prepare('UPDATE user_accounts SET failed_attempts = ?, locked_until = ? WHERE minecraft_uuid = ?').run(failedCount, lockUntil, account.minecraft_uuid); } catch (e) {} }
            ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Invalid username or password' } }));
            return;
        }

        // Password valid — reset failed attempts
        if (db) { try { db.prepare('UPDATE user_accounts SET failed_attempts = 0, locked_until = 0, last_login_at = ? WHERE minecraft_uuid = ?').run(Date.now(), account.minecraft_uuid); } catch (e) {} }

        // Store pending auth info on browser client
        const browserClient = browserClients.get(clientId);
        if (browserClient) {
            browserClient.pendingPasswordAuth = true;
            browserClient.authedUuid = account.minecraft_uuid;
            browserClient.authedUsername = account.minecraft_username;
            browserClient.deviceFingerprint = deviceFingerprint;
        }

        // Save device fingerprint
        if (deviceFingerprint) saveDeviceFingerprint(account.minecraft_uuid, deviceFingerprint);

        // Create gateway session
        const sessionToken = createSession(account.minecraft_uuid, deviceFingerprint || null, clientIp);
        if (browserClient) browserClient.gatewaySessionToken = sessionToken;

        // Get user's permissions for this server
        const userServers = getServersForUser(account.minecraft_uuid);
        const serverAccess = userServers.find(s => s.serverId === serverId);

        // Send pre-auth to MC server to create a session there
        const server = mcServers.get(serverId);
        if (server && server.ws.readyState === WebSocket.OPEN) {
            server.ws.send(JSON.stringify({
                type: 'global_pre_auth',
                clientId: clientId,
                uuid: account.minecraft_uuid,
                username: account.minecraft_username,
                permissions: serverAccess?.permissions || []
            }));
            console.log(`[Browser] ${account.minecraft_username} password auth via gateway for server ${serverId}`);
        } else {
            ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Server went offline' } }));
        }
    } catch (e) {
        console.error('[Browser] Password auth error:', e.message);
        ws.send(JSON.stringify({ type: 'auth_failed', data: { message: 'Authentication failed' } }));
    }
}

/**
 * Broadcast message to all browsers connected to a specific server
 */
function broadcastToServer(serverId, data) {
    browserClients.forEach((client, clientId) => {
        if (client.serverId === serverId && client.ws.readyState === WebSocket.OPEN) {
            client.ws.send(JSON.stringify(data));
        }
    });
}

/**
 * Generate unique client ID
 */
function generateClientId() {
    return 'client_' + crypto.randomBytes(12).toString('hex');
}

// ============================================================================
// Global Token System — Handler Functions
// ============================================================================

/**
 * Handle bulk permission sync from MC server (sent on startup).
 * Upserts all players into server_access, removes players no longer in the list.
 */
function handlePermissionSync(serverId, players) {
    if (!Array.isArray(players)) return;

    console.log(`[Token] Permission sync from ${serverId}: ${players.length} players`);

    if (db) {
        try {
            const upsert = db.prepare(
                'INSERT OR REPLACE INTO server_access (uuid, server_id, username, rank, permissions, updated_at) VALUES (?, ?, ?, ?, ?, ?)'
            );
            const now = Date.now();

            // Upsert all players
            for (const p of players) {
                if (!p.uuid) continue;
                upsert.run(p.uuid, serverId, p.username || null, p.rank || null, JSON.stringify(p.permissions || []), now);
            }

            // Remove players no longer in the list for this server
            const uuids = players.filter(p => p.uuid).map(p => p.uuid);
            if (uuids.length > 0) {
                const placeholders = uuids.map(() => '?').join(',');
                db.prepare(`DELETE FROM server_access WHERE server_id = ? AND uuid NOT IN (${placeholders})`).run(serverId, ...uuids);
            } else {
                // No players — clear all access for this server
                db.prepare('DELETE FROM server_access WHERE server_id = ?').run(serverId);
            }
        } catch (e) {
            console.error('[Token] Permission sync DB error:', e.message);
        }
    } else {
        // In-memory fallback
        // Remove old entries for this server
        for (const [key] of inMemoryServerAccess) {
            if (key.endsWith(':' + serverId)) {
                inMemoryServerAccess.delete(key);
            }
        }
        // Add new entries
        for (const p of players) {
            if (!p.uuid) continue;
            inMemoryServerAccess.set(`${p.uuid}:${serverId}`, {
                uuid: p.uuid, server_id: serverId, username: p.username,
                rank: p.rank, permissions: JSON.stringify(p.permissions || []),
                updated_at: Date.now()
            });
        }
    }
}

/**
 * Handle single player permission update (LP change event).
 */
function handlePermissionUpdate(serverId, data) {
    const { uuid, username, rank, permissions, hasAccess } = data;
    if (!uuid) return;

    console.log(`[Token] Permission update: ${username || uuid} on ${serverId} - access: ${hasAccess}`);

    if (db) {
        try {
            if (hasAccess) {
                db.prepare(
                    'INSERT OR REPLACE INTO server_access (uuid, server_id, username, rank, permissions, updated_at) VALUES (?, ?, ?, ?, ?, ?)'
                ).run(uuid, serverId, username || null, rank || null, JSON.stringify(permissions || []), Date.now());
            } else {
                db.prepare('DELETE FROM server_access WHERE uuid = ? AND server_id = ?').run(uuid, serverId);
            }
        } catch (e) {
            console.error('[Token] Permission update DB error:', e.message);
        }
    } else {
        const key = `${uuid}:${serverId}`;
        if (hasAccess) {
            inMemoryServerAccess.set(key, {
                uuid, server_id: serverId, username, rank,
                permissions: JSON.stringify(permissions || []),
                updated_at: Date.now()
            });
        } else {
            inMemoryServerAccess.delete(key);
        }
    }
}

/**
 * Register or update a global token.
 */
function handleTokenRegister(data) {
    const { uuid, username, tokenHash, expiresAt } = data;
    if (!uuid || !tokenHash) return;

    console.log(`[Token] Token registered for ${username || uuid}`);

    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO global_tokens (uuid, token_hash, username, created_at, expires_at) VALUES (?, ?, ?, ?, ?)'
            ).run(uuid, tokenHash, username || null, Date.now(), expiresAt || (Date.now() + 90 * 24 * 60 * 60 * 1000));
        } catch (e) {
            console.error('[Token] Token register DB error:', e.message);
        }
    } else {
        inMemoryGlobalTokens.set(uuid, {
            uuid, token_hash: tokenHash, username,
            created_at: Date.now(), expires_at: expiresAt || (Date.now() + 90 * 24 * 60 * 60 * 1000)
        });
    }
}

/**
 * Revoke a global token.
 */
function handleTokenRevoke(uuid) {
    if (!uuid) return;

    console.log(`[Token] Token revoked for ${uuid}`);

    if (db) {
        try {
            db.prepare('DELETE FROM global_tokens WHERE uuid = ?').run(uuid);
        } catch (e) {
            console.error('[Token] Token revoke DB error:', e.message);
        }
    } else {
        inMemoryGlobalTokens.delete(uuid);
    }
}

/**
 * Sync user settings from MC server to gateway.
 */
function handleSettingsSync(data) {
    const { uuid, colorScheme, themeColor, deviceFingerprints } = data;
    if (!uuid) return;

    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO user_settings (uuid, color_scheme, theme_color, device_fingerprints, updated_at) VALUES (?, ?, ?, ?, ?)'
            ).run(uuid, colorScheme || 'blue', themeColor || '#2d7aed', JSON.stringify(deviceFingerprints || []), Date.now());
        } catch (e) {
            console.error('[Token] Settings sync DB error:', e.message);
        }
    } else {
        inMemoryUserSettings.set(uuid, {
            uuid, color_scheme: colorScheme || 'blue',
            theme_color: themeColor || '#2d7aed',
            device_fingerprints: JSON.stringify(deviceFingerprints || []),
            updated_at: Date.now()
        });
    }
}

/**
 * Handle server unregistering from gateway (gateway disabled in config).
 * Remove all server_access entries for this server but keep tokens and user settings.
 */
function handleServerUnregister(serverId) {
    console.log(`[Token] Server ${serverId} unregistering — removing access entries`);

    if (db) {
        try {
            db.prepare('DELETE FROM server_access WHERE server_id = ?').run(serverId);
        } catch (e) {
            console.error('[Token] Server unregister DB error:', e.message);
        }
    } else {
        for (const [key] of inMemoryServerAccess) {
            if (key.endsWith(':' + serverId)) {
                inMemoryServerAccess.delete(key);
            }
        }
    }
}

/**
 * Validate a global token hash against the database.
 * Returns { valid, uuid, username } or { valid: false }.
 */
function validateGlobalToken(rawToken) {
    const tokenHash = crypto.createHash('sha256').update(rawToken).digest('hex');

    if (db) {
        try {
            const row = db.prepare('SELECT * FROM global_tokens WHERE token_hash = ?').get(tokenHash);
            if (!row) return { valid: false };
            if (row.expires_at && row.expires_at < Date.now()) {
                // Token expired — clean up
                db.prepare('DELETE FROM global_tokens WHERE uuid = ?').run(row.uuid);
                return { valid: false, reason: 'Token expired' };
            }
            return { valid: true, uuid: row.uuid, username: row.username };
        } catch (e) {
            console.error('[Token] Token validation DB error:', e.message);
            return { valid: false };
        }
    } else {
        for (const [uuid, token] of inMemoryGlobalTokens) {
            if (token.token_hash === tokenHash) {
                if (token.expires_at && token.expires_at < Date.now()) {
                    inMemoryGlobalTokens.delete(uuid);
                    return { valid: false, reason: 'Token expired' };
                }
                return { valid: true, uuid: token.uuid, username: token.username };
            }
        }
        return { valid: false };
    }
}

/**
 * Validate a device fingerprint hash against stored fingerprints for a UUID.
 * Returns { valid, uuid, username, colorScheme } or { valid: false }.
 */
function validateDeviceFingerprint(fingerprintHash) {
    if (db) {
        try {
            const rows = db.prepare('SELECT * FROM user_settings').all();
            for (const row of rows) {
                const fps = JSON.parse(row.device_fingerprints || '[]');
                if (fps.includes(fingerprintHash)) {
                    // Found matching fingerprint — look up token to get username
                    const tokenRow = db.prepare('SELECT username FROM global_tokens WHERE uuid = ?').get(row.uuid);
                    return { valid: true, uuid: row.uuid, username: tokenRow?.username || null, colorScheme: row.color_scheme };
                }
            }
        } catch (e) {
            console.error('[Token] Fingerprint validation DB error:', e.message);
        }
    } else {
        for (const [uuid, settings] of inMemoryUserSettings) {
            const fps = JSON.parse(settings.device_fingerprints || '[]');
            if (fps.includes(fingerprintHash)) {
                const token = inMemoryGlobalTokens.get(uuid);
                return { valid: true, uuid, username: token?.username || null, colorScheme: settings.color_scheme };
            }
        }
    }
    return { valid: false };
}

/**
 * Get server list for a specific UUID (servers they have access to).
 */
function getServersForUser(uuid) {
    const servers = [];

    if (db) {
        try {
            const rows = db.prepare('SELECT * FROM server_access WHERE uuid = ?').all(uuid);
            for (const row of rows) {
                const mcServer = mcServers.get(row.server_id);
                let serverName = mcServer?.info?.serverName;
                if (!serverName) {
                    try {
                        const known = db.prepare('SELECT server_name FROM known_servers WHERE server_id = ?').get(row.server_id);
                        if (known) serverName = known.server_name;
                    } catch (e) {}
                }
                servers.push({
                    serverId: row.server_id,
                    serverName: serverName || 'Unknown Server',
                    rank: row.rank || 'Member',
                    permissions: JSON.parse(row.permissions || '[]'),
                    online: !!mcServer,
                    players: mcServer?.info?.players || 0,
                    urlPrefix: mcServer?.urlPrefix || null
                });
            }
        } catch (e) {
            console.error('[Token] Get servers DB error:', e.message);
        }
    } else {
        for (const [key, access] of inMemoryServerAccess) {
            if (access.uuid === uuid) {
                const mcServer = mcServers.get(access.server_id);
                let serverName = mcServer?.info?.serverName;
                if (!serverName && db) {
                    try {
                        const known = db.prepare('SELECT server_name FROM known_servers WHERE server_id = ?').get(access.server_id);
                        if (known) serverName = known.server_name;
                    } catch (e) {}
                }
                servers.push({
                    serverId: access.server_id,
                    serverName: serverName || 'Unknown Server',
                    rank: access.rank || 'Member',
                    permissions: JSON.parse(access.permissions || '[]'),
                    online: !!mcServer,
                    players: mcServer?.info?.players || 0,
                    urlPrefix: mcServer?.urlPrefix || null
                });
            }
        }
    }

    return servers;
}

/**
 * Get user settings from gateway DB.
 */
function getUserSettings(uuid) {
    if (db) {
        try {
            const row = db.prepare('SELECT * FROM user_settings WHERE uuid = ?').get(uuid);
            if (row) {
                return {
                    colorScheme: row.color_scheme || 'blue',
                    themeColor: row.theme_color || '#2d7aed',
                    deviceFingerprints: JSON.parse(row.device_fingerprints || '[]')
                };
            }
        } catch (e) {
            console.error('[Token] Get settings DB error:', e.message);
        }
    } else {
        const settings = inMemoryUserSettings.get(uuid);
        if (settings) {
            return {
                colorScheme: settings.color_scheme || 'blue',
                themeColor: settings.theme_color || '#2d7aed',
                deviceFingerprints: JSON.parse(settings.device_fingerprints || '[]')
            };
        }
    }
    return { colorScheme: 'blue', themeColor: '#2d7aed', deviceFingerprints: [] };
}

/**
 * Save user settings to gateway DB.
 */
function saveUserSettings(uuid, colorScheme, deviceFingerprints, themeColor) {
    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO user_settings (uuid, color_scheme, theme_color, device_fingerprints, updated_at) VALUES (?, ?, ?, ?, ?)'
            ).run(uuid, colorScheme || 'blue', themeColor || '#2d7aed', JSON.stringify(deviceFingerprints || []), Date.now());
        } catch (e) {
            console.error('[Token] Save settings DB error:', e.message);
        }
    } else {
        inMemoryUserSettings.set(uuid, {
            uuid, color_scheme: colorScheme || 'blue',
            theme_color: themeColor || '#2d7aed',
            device_fingerprints: JSON.stringify(deviceFingerprints || []),
            updated_at: Date.now()
        });
    }
}

/**
 * Handle global pre-auth result from MC server.
 * Routes the result back to the waiting browser.
 */
function handleGlobalPreAuthResult(message) {
    const { clientId, sessionId, success, error } = message;
    if (!clientId) return;

    // Check global panel clients first (switch_server flow)
    const client = globalPanelClients.get(clientId);
    if (client && client.ws.readyState === WebSocket.OPEN) {
        if (success && sessionId) {
            // Pre-auth succeeded — transition this client to a normal browser connection
            const serverId = client.pendingSwitchServerId;
            if (!serverId) return;

            // Fix 8: Check TTL on pending switch (5 minute max)
            const PRE_AUTH_TTL = 5 * 60 * 1000;
            if (client.pendingSwitchAt && (Date.now() - client.pendingSwitchAt) > PRE_AUTH_TTL) {
                client.ws.send(JSON.stringify({ type: 'switch_server_result', success: false, error: 'Pre-auth session expired. Please try again.' }));
                delete client.pendingSwitchServerId;
                delete client.pendingSwitchAt;
                return;
            }

            const serverData = mcServers.get(serverId);
            if (!serverData) {
                client.ws.send(JSON.stringify({ type: 'switch_server_result', success: false, error: 'Server went offline' }));
                return;
            }

            // Move client from global pool to browser pool
            globalPanelClients.delete(clientId);
            browserClients.set(clientId, {
                ws: client.ws,
                serverId: serverId,
                connectedAt: Date.now(),
                clientIp: client.clientIp,
                authedUuid: client.uuid
            });

            // Notify browser of successful switch with pre-auth session
            client.ws.send(JSON.stringify({
                type: 'switch_server_result',
                success: true,
                serverId: serverId,
                serverName: serverData.info.serverName,
                urlPrefix: serverData.urlPrefix,
                sessionId: sessionId
            }));

            // Notify MC server of new browser connection
            if (serverData.ws.readyState === WebSocket.OPEN) {
                serverData.ws.send(JSON.stringify({
                    type: 'browser_connected',
                    clientId: clientId,
                    clientIp: client.clientIp
                }));
            }

            // Send cached server status
            if (serverData.lastStatus) {
                client.ws.send(JSON.stringify(serverData.lastStatus));
            }

            console.log(`[Token] ${client.uuid} switched to server ${serverId}`);

            // Re-attach message handler for normal browser routing
            // (The ws 'message' handler from handleGlobalPanelConnection will be replaced)
            client.ws.removeAllListeners('message');
            client.ws.on('message', (data) => {
                try {
                    if (isMessageRateLimited(client.ws)) {
                        client.ws.send(JSON.stringify({ type: 'error', code: 'RATE_LIMITED', message: 'Too many messages. Slow down.' }));
                        return;
                    }
                    const msg = JSON.parse(data.toString());

                    // Intercept save_settings — store in gateway DB
                    if (msg.type === 'save_settings') {
                        const bc = browserClients.get(clientId);
                        if (bc?.authedUuid) {
                            const existing = getUserSettings(bc.authedUuid);
                            saveUserSettings(bc.authedUuid, msg.colorScheme || existing.colorScheme, existing.deviceFingerprints, msg.themeColor || existing.themeColor);
                            client.ws.send(JSON.stringify({ type: 'settings_saved', success: true }));
                        }
                        return;
                    }

                    const server = mcServers.get(serverId);
                    if (server && server.ws.readyState === WebSocket.OPEN) {
                        msg.clientId = clientId;
                        msg.clientIp = client.clientIp;
                        server.ws.send(JSON.stringify(msg));
                    } else {
                        client.ws.send(JSON.stringify({ type: 'error', code: 'SERVER_OFFLINE', message: 'Server went offline' }));
                    }
                } catch (err) {
                    console.error('[Browser] Error processing message:', err.message);
                }
            });
        } else {
            client.ws.send(JSON.stringify({
                type: 'switch_server_result',
                success: false,
                error: error || 'Pre-authentication failed'
            }));
        }
        return;
    }

    // Check browser clients (AUTH_PASSWORD via gateway flow)
    const browserClient = browserClients.get(clientId);
    if (browserClient && browserClient.pendingPasswordAuth && browserClient.ws.readyState === WebSocket.OPEN) {
        delete browserClient.pendingPasswordAuth;

        if (success && sessionId) {
            browserClient.ws.send(JSON.stringify({
                type: 'auth_success',
                data: {
                    playerName: browserClient.authedUsername,
                    playerUuid: browserClient.authedUuid,
                    username: browserClient.authedUsername,
                    uuid: browserClient.authedUuid,
                    sessionId: sessionId,
                    authMethod: 'password',
                    gatewaySessionToken: browserClient.gatewaySessionToken
                }
            }));
            console.log(`[Browser] Password auth completed for ${browserClient.authedUsername}`);
        } else {
            browserClient.ws.send(JSON.stringify({
                type: 'auth_failed',
                data: { message: error || 'No web panel permission on this server' }
            }));
        }

        // Clean up temp auth data (keep authedUuid for gateway settings)
        delete browserClient.authedUsername;
        delete browserClient.deviceFingerprint;
        delete browserClient.gatewaySessionToken;
    }
}

// ============================================================================
// Global Panel Connection (Server List Page)
// ============================================================================

// Store global panel connections (browsers at /panel/ without a server prefix)
const globalPanelClients = new Map();

/**
 * Handle browser connection to /panel/ (no server prefix).
 * Used for: global authentication, server list, settings management.
 */
function handleGlobalPanelConnection(ws, clientIp) {
    const clientId = generateClientId();
    let authedUuid = null;
    let authedUsername = null;

    globalPanelClients.set(clientId, {
        ws: ws,
        clientIp: clientIp,
        connectedAt: Date.now(),
        uuid: null
    });

    console.log(`[Global] ${clientId} connected from ${clientIp}`);

    // Send connection confirmation
    ws.send(JSON.stringify({ type: 'connected', mode: 'global' }));

    ws.on('message', async (data) => {
        try {
            if (isMessageRateLimited(ws)) {
                ws.send(JSON.stringify({ type: 'error', code: 'RATE_LIMITED', message: 'Too many messages. Slow down.' }));
                return;
            }

            const message = JSON.parse(data.toString());

            // Normalize message format: gateway panel sends UPPERCASE types
            // with data nested in a 'data' field, but handlers expect lowercase
            // types with properties at top level
            if (message.type) {
                message.type = message.type.toLowerCase();
            }
            if (message.data && typeof message.data === 'object') {
                Object.assign(message, message.data);
            }
            // Map field name differences
            if (message.deviceFingerprint && !message.fingerprintHash) {
                message.fingerprintHash = message.deviceFingerprint;
            }

            switch (message.type) {
                case 'ping': {
                    ws.send(JSON.stringify({ type: 'PONG', timestamp: Date.now() }));
                    break;
                }

                case 'heartbeat': {
                    // Keep-alive acknowledgement, no response needed
                    break;
                }

                case 'global_auth': {
                    // Fix 7: Rate limit failed token auth attempts per IP
                    const authAttempts = failedTokenAuthAttempts.get(clientIp);
                    if (authAttempts && authAttempts.count >= 5) {
                        const cooldown = Math.min(300000, 1000 * Math.pow(2, authAttempts.count - 5)); // exponential backoff, max 5min
                        if (Date.now() - authAttempts.lastAttempt < cooldown) {
                            ws.send(JSON.stringify({
                                type: 'global_auth_result',
                                success: false,
                                error: 'Too many failed attempts. Please wait before trying again.'
                            }));
                            break;
                        }
                    }

                    // Authenticate with a global token
                    const result = validateGlobalToken(message.token || '');
                    if (result.valid) {
                        // Clear failed attempts on success
                        failedTokenAuthAttempts.delete(clientIp);

                        authedUuid = result.uuid;
                        authedUsername = result.username;
                        const client = globalPanelClients.get(clientId);
                        if (client) client.uuid = authedUuid;

                        const servers = getServersForUser(authedUuid);
                        const settings = getUserSettings(authedUuid);
                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: true,
                            uuid: authedUuid,
                            username: authedUsername,
                            servers: servers,
                            settings: settings
                        }));
                        console.log(`[Global] ${authedUsername || authedUuid} authenticated (${servers.length} servers)`);
                    } else {
                        // Track failed attempt
                        const attempts = failedTokenAuthAttempts.get(clientIp) || { count: 0, lastAttempt: 0 };
                        attempts.count++;
                        attempts.lastAttempt = Date.now();
                        failedTokenAuthAttempts.set(clientIp, attempts);

                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: false,
                            error: result.reason || 'Invalid token'
                        }));
                    }
                    break;
                }

                case 'global_device_auth': {
                    // Rate limit device fingerprint auth attempts per IP
                    const fpAttempts = failedTokenAuthAttempts.get(clientIp);
                    if (fpAttempts && fpAttempts.count >= 5) {
                        const cooldown = Math.min(300000, 1000 * Math.pow(2, fpAttempts.count - 5));
                        if (Date.now() - fpAttempts.lastAttempt < cooldown) {
                            ws.send(JSON.stringify({
                                type: 'global_auth_result',
                                success: false,
                                error: 'Too many failed attempts. Please wait before trying again.'
                            }));
                            break;
                        }
                    }

                    // Authenticate with device fingerprint
                    const result = validateDeviceFingerprint(message.fingerprintHash || '');
                    if (result.valid) {
                        authedUuid = result.uuid;
                        authedUsername = result.username;
                        const client = globalPanelClients.get(clientId);
                        if (client) client.uuid = authedUuid;

                        const servers = getServersForUser(authedUuid);
                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: true,
                            uuid: authedUuid,
                            username: authedUsername,
                            servers: servers,
                            settings: { colorScheme: result.colorScheme, deviceFingerprints: [] }
                        }));
                        console.log(`[Global] ${authedUsername || authedUuid} authenticated via device fingerprint`);
                    } else {
                        // Track failed device auth attempt
                        const attempts = failedTokenAuthAttempts.get(clientIp) || { count: 0, lastAttempt: 0 };
                        attempts.count++;
                        attempts.lastAttempt = Date.now();
                        failedTokenAuthAttempts.set(clientIp, attempts);

                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: false,
                            error: 'Device not recognized'
                        }));
                    }
                    break;
                }

                case 'password_auth': {
                    // Authenticate with username + password via WebSocket
                    if (!argon2) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Password auth not available' }));
                        break;
                    }

                    const pwUsername = message.username;
                    const pwPassword = message.password;
                    if (!pwUsername || !pwPassword) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Username and password required' }));
                        break;
                    }

                    // Look up account
                    let pwAccount = null;
                    if (db) {
                        try {
                            pwAccount = db.prepare('SELECT * FROM user_accounts WHERE LOWER(minecraft_username) = LOWER(?)').get(pwUsername);
                        } catch (e) {}
                    } else {
                        for (const [, acc] of inMemoryUserAccounts) {
                            if (acc.minecraft_username.toLowerCase() === pwUsername.toLowerCase()) { pwAccount = acc; break; }
                        }
                    }

                    if (!pwAccount) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Invalid username or password' }));
                        break;
                    }

                    // Check lockout
                    if (pwAccount.locked_until && pwAccount.locked_until > Date.now()) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Account locked. Please wait.' }));
                        break;
                    }

                    try {
                        const pwValid = await argon2.verify(pwAccount.password_hash, pwPassword);
                        if (!pwValid) {
                            const failedCount = (pwAccount.failed_attempts || 0) + 1;
                            const lockUntil = failedCount >= 5 ? Date.now() + 15 * 60 * 1000 : 0;
                            if (db) { try { db.prepare('UPDATE user_accounts SET failed_attempts = ?, locked_until = ? WHERE minecraft_uuid = ?').run(failedCount, lockUntil, pwAccount.minecraft_uuid); } catch (e) {} }
                            ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Invalid username or password' }));
                            break;
                        }

                        // Success
                        if (db) { try { db.prepare('UPDATE user_accounts SET failed_attempts = 0, locked_until = 0, last_login_at = ? WHERE minecraft_uuid = ?').run(Date.now(), pwAccount.minecraft_uuid); } catch (e) {} }

                        authedUuid = pwAccount.minecraft_uuid;
                        authedUsername = pwAccount.minecraft_username;
                        const pwClient = globalPanelClients.get(clientId);
                        if (pwClient) pwClient.uuid = authedUuid;

                        // Create session
                        const pwSessionToken = createSession(authedUuid, message.fingerprintHash || null, clientIp);
                        if (message.fingerprintHash) saveDeviceFingerprint(authedUuid, message.fingerprintHash);

                        const pwServers = getServersForUser(authedUuid);
                        const pwSettings = getUserSettings(authedUuid);
                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: true,
                            uuid: authedUuid,
                            username: authedUsername,
                            servers: pwServers,
                            settings: pwSettings,
                            sessionToken: pwSessionToken
                        }));
                        console.log(`[Global] ${authedUsername} authenticated with password`);
                    } catch (e) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Authentication failed' }));
                    }
                    break;
                }

                case 'session_auth': {
                    // Authenticate with session token
                    if (!message.sessionToken) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Session token required' }));
                        break;
                    }
                    const session = validateSession(message.sessionToken);
                    if (!session) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Invalid or expired session' }));
                        break;
                    }
                    const sessAccount = getUserAccount(session.minecraft_uuid);
                    authedUuid = session.minecraft_uuid;
                    authedUsername = sessAccount?.minecraft_username || null;
                    const sessClient = globalPanelClients.get(clientId);
                    if (sessClient) sessClient.uuid = authedUuid;

                    const sessServers = getServersForUser(authedUuid);
                    const sessSettings = getUserSettings(authedUuid);
                    ws.send(JSON.stringify({
                        type: 'global_auth_result',
                        success: true,
                        uuid: authedUuid,
                        username: authedUsername,
                        servers: sessServers,
                        settings: sessSettings
                    }));
                    console.log(`[Global] ${authedUsername || authedUuid} authenticated via session`);
                    break;
                }

                case 'fingerprint_auth': {
                    // Authenticate with device fingerprint (new auth system)
                    if (!message.fingerprintHash) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Fingerprint required' }));
                        break;
                    }

                    let fpMatchedAccount = null;
                    if (db) {
                        try {
                            const rows = db.prepare('SELECT * FROM user_accounts WHERE auto_sign_in = 1').all();
                            for (const row of rows) {
                                const fps = JSON.parse(row.device_fingerprints || '[]');
                                if (fps.includes(message.fingerprintHash)) { fpMatchedAccount = row; break; }
                            }
                        } catch (e) {}
                    } else {
                        for (const [, acc] of inMemoryUserAccounts) {
                            if (!acc.auto_sign_in) continue;
                            const fps = JSON.parse(acc.device_fingerprints || '[]');
                            if (fps.includes(message.fingerprintHash)) { fpMatchedAccount = acc; break; }
                        }
                    }

                    if (!fpMatchedAccount) {
                        ws.send(JSON.stringify({ type: 'global_auth_result', success: false, error: 'Device not recognized' }));
                        break;
                    }

                    authedUuid = fpMatchedAccount.minecraft_uuid;
                    authedUsername = fpMatchedAccount.minecraft_username;
                    const fpClient = globalPanelClients.get(clientId);
                    if (fpClient) fpClient.uuid = authedUuid;

                    const fpSessionToken = createSession(authedUuid, message.fingerprintHash, clientIp);
                    const fpServers = getServersForUser(authedUuid);
                    const fpSettings = getUserSettings(authedUuid);
                    ws.send(JSON.stringify({
                        type: 'global_auth_result',
                        success: true,
                        uuid: authedUuid,
                        username: authedUsername,
                        servers: fpServers,
                        settings: fpSettings,
                        sessionToken: fpSessionToken
                    }));
                    console.log(`[Global] ${authedUsername} authenticated via device fingerprint (new)`);
                    break;
                }

                case 'get_servers': {
                    if (!authedUuid) {
                        ws.send(JSON.stringify({ type: 'error', code: 'NOT_AUTHENTICATED', message: 'Please authenticate first' }));
                        break;
                    }
                    const servers = getServersForUser(authedUuid);
                    ws.send(JSON.stringify({ type: 'server_list', servers: servers }));
                    break;
                }

                case 'get_settings': {
                    if (!authedUuid) {
                        ws.send(JSON.stringify({ type: 'error', code: 'NOT_AUTHENTICATED', message: 'Please authenticate first' }));
                        break;
                    }
                    const settings = getUserSettings(authedUuid);
                    ws.send(JSON.stringify({ type: 'user_settings', settings: settings }));
                    break;
                }

                case 'save_settings': {
                    if (!authedUuid) {
                        ws.send(JSON.stringify({ type: 'error', code: 'NOT_AUTHENTICATED', message: 'Please authenticate first' }));
                        break;
                    }
                    saveUserSettings(authedUuid, message.colorScheme, message.deviceFingerprints, message.themeColor);
                    ws.send(JSON.stringify({ type: 'settings_saved', success: true }));
                    break;
                }

                case 'switch_server': {
                    if (!authedUuid) {
                        ws.send(JSON.stringify({ type: 'error', code: 'NOT_AUTHENTICATED', message: 'Please authenticate first' }));
                        break;
                    }

                    const targetServerId = message.serverId?.toLowerCase();
                    if (!targetServerId) {
                        ws.send(JSON.stringify({ type: 'switch_server_result', success: false, error: 'Server ID required' }));
                        break;
                    }

                    // Verify user has access to this server
                    const userServers = getServersForUser(authedUuid);
                    const hasAccess = userServers.some(s => s.serverId === targetServerId);
                    if (!hasAccess) {
                        ws.send(JSON.stringify({ type: 'switch_server_result', success: false, error: 'No access to this server' }));
                        break;
                    }

                    // Check if MC server is online
                    const targetServer = mcServers.get(targetServerId);
                    if (!targetServer || targetServer.ws.readyState !== WebSocket.OPEN) {
                        ws.send(JSON.stringify({ type: 'switch_server_result', success: false, error: 'Server is offline' }));
                        break;
                    }

                    // Store pending switch info with TTL
                    const client = globalPanelClients.get(clientId);
                    if (client) {
                        client.pendingSwitchServerId = targetServerId;
                        client.pendingSwitchAt = Date.now();
                    }

                    // Get the user's permissions for this server
                    const accessEntry = userServers.find(s => s.serverId === targetServerId);

                    // Send pre-auth request to MC server
                    targetServer.ws.send(JSON.stringify({
                        type: 'global_pre_auth',
                        clientId: clientId,
                        uuid: authedUuid,
                        username: authedUsername,
                        permissions: accessEntry?.permissions || []
                    }));

                    console.log(`[Global] ${authedUsername} requesting switch to ${targetServerId}`);
                    break;
                }

                case 'get_panel_version': {
                    // In global mode, no MC server to query - return gateway info
                    ws.send(JSON.stringify({ type: 'PANEL_VERSION', version: 'gateway', buildNumber: 0 }));
                    break;
                }

                case 'logout': {
                    // Clear device fingerprints on logout so auto-sign-in is disabled
                    if (message.revokeFingerprint && authedUuid) {
                        if (db) {
                            try { db.prepare('UPDATE user_accounts SET device_fingerprints = ? WHERE minecraft_uuid = ?').run('[]', authedUuid); } catch (e) {}
                        } else {
                            const acc = inMemoryUserAccounts.get(authedUuid);
                            if (acc) acc.device_fingerprints = '[]';
                        }
                        console.log(`[Global] Revoked fingerprints for ${authedUsername || authedUuid}`);
                    }
                    break;
                }

                default:
                    // Only log unknown types that aren't common keep-alive messages
                    console.warn(`[Global] Unknown message type: ${message.type}`);
                    break;
            }
        } catch (err) {
            console.error('[Global] Error processing message:', err.message);
        }
    });

    ws.on('close', () => {
        globalPanelClients.delete(clientId);
        console.log(`[Global] ${clientId} disconnected`);
    });

    ws.on('error', (err) => {
        console.error(`[Global] WebSocket error:`, err.message);
    });
}

// ============================================================================
// Admin Panel Connection Handler
// ============================================================================

/**
 * Handle admin panel WebSocket connection
 */
function handleAdminConnection(ws, clientIp, cfEmail, req) {
    const adminId = 'admin_' + crypto.randomBytes(8).toString('hex');

    // Validate admin session token from query params
    const urlParams = new URL(req.url, 'http://localhost').searchParams;
    const adminToken = urlParams.get('token');

    // Allow ADMIN_DEV_KEY bypass for local development
    const devKey = process.env.ADMIN_DEV_KEY;
    if (devKey && adminToken === devKey) {
        // Dev key bypass — skip session validation
        cfEmail = cfEmail || 'dev@localhost';
    } else if (!adminToken) {
        ws.send(JSON.stringify({ type: 'error', code: 'AUTH_REQUIRED', message: 'Admin session token required' }));
        ws.close(4001, 'Authentication required');
        return;
    } else {
        const adminSession = validateAdminSession(adminToken, true);
        if (!adminSession) {
            ws.send(JSON.stringify({ type: 'error', code: 'AUTH_FAILED', message: 'Invalid or expired admin session' }));
            ws.close(4001, 'Authentication failed');
            return;
        }
        // Use the admin UUID for identification instead of email
        cfEmail = adminSession.minecraft_uuid;
    }

    const email = cfEmail;

    // Register admin client
    adminClients.set(adminId, {
        ws: ws,
        email: email,
        connectedAt: Date.now(),
        clientIp: clientIp
    });

    console.log(`[Admin] ${email} connected (${adminId}) from ${clientIp}`);
    logAudit(email, 'admin_connected', { ip: clientIp });

    // Send connection confirmation
    ws.send(JSON.stringify({
        type: 'connected',
        adminId: adminId,
        email: email
    }));

    // Send current active announcements
    sendActiveAnnouncements(ws);

    ws.on('message', (data) => {
        try {
            const message = JSON.parse(data.toString());
            handleAdminMessage(adminId, email, message);
        } catch (err) {
            console.error(`[Admin] Error processing message:`, err.message);
        }
    });

    ws.on('close', () => {
        adminClients.delete(adminId);
        console.log(`[Admin] ${email} disconnected`);
        logAudit(email, 'admin_disconnected', null);
    });

    ws.on('error', (err) => {
        console.error(`[Admin] WebSocket error for ${email}:`, err.message);
    });
}

/**
 * Handle incoming admin messages
 */
function handleAdminMessage(adminId, email, message) {
    const admin = adminClients.get(adminId);
    if (!admin) return;

    const { type, data } = message;

    switch (type) {
        case 'admin_auth':
            // Frontend sends this after WS connect — confirm authentication
            admin.ws.send(JSON.stringify({
                type: 'auth_success',
                email: email
            }));
            break;

        case 'get_dashboard_data':
            sendDashboardData(admin.ws);
            break;

        case 'get_announcements':
        case 'get_announcements_list':
            sendAllAnnouncements(admin.ws);
            break;

        case 'create_announcement':
            createAnnouncement(admin.ws, email, data);
            break;

        case 'update_announcement':
            updateAnnouncement(admin.ws, email, data);
            break;

        case 'delete_announcement':
            deleteAnnouncement(admin.ws, email, data?.id);
            break;

        case 'deactivate_announcement':
            deactivateAnnouncement(admin.ws, email, data?.id);
            break;

        case 'broadcast_announcement':
            broadcastAnnouncementToAll(admin.ws, email, data?.id);
            break;

        case 'get_gateway_health':
            sendGatewayHealth(admin.ws);
            break;

        case 'get_servers':
        case 'get_servers_list':
            sendServerList(admin.ws);
            break;

        case 'get_audit_log':
            sendAuditLog(admin.ws, data?.limit || 100);
            break;

        case 'export_audit_log':
            exportAuditLog(admin.ws);
            break;

        case 'get_analytics_data':
            sendAnalyticsData(admin.ws);
            break;

        case 'get_premium_data':
            sendPremiumData(admin.ws);
            break;

        case 'generate_license_key':
            admin.ws.send(JSON.stringify({
                type: 'error',
                code: 'NOT_IMPLEMENTED',
                message: 'Premium license system is not yet available'
            }));
            break;

        case 'revoke_license_key':
            admin.ws.send(JSON.stringify({
                type: 'error',
                code: 'NOT_IMPLEMENTED',
                message: 'Premium license system is not yet available'
            }));
            break;

        case 'reset_server_secret': {
            const targetServerId = data?.serverId;
            if (!targetServerId) {
                admin.ws.send(JSON.stringify({
                    type: 'error',
                    code: 'INVALID_DATA',
                    message: 'serverId is required'
                }));
                break;
            }
            // Delete from DB
            if (db) {
                try {
                    db.prepare('DELETE FROM server_secrets WHERE server_id = ?').run(targetServerId.toLowerCase());
                } catch (e) {
                    console.error('[Admin] Failed to reset server secret:', e.message);
                }
            }
            // Delete from in-memory fallback
            inMemoryServerSecrets.delete(targetServerId.toLowerCase());
            console.log(`[Admin] ${email} reset gateway secret for server ${targetServerId}`);
            addAuditEntry(email, 'reset_server_secret', `Reset gateway secret for server ${targetServerId}`);
            admin.ws.send(JSON.stringify({
                type: 'server_secret_reset',
                serverId: targetServerId,
                message: `Secret and IP binding reset for ${targetServerId}. Server will re-register on next connection.`
            }));
            break;
        }

        case 'get_licenses':
            sendLicensesList(admin.ws);
            break;

        case 'create_license':
            createDevLicense(admin.ws, email, data);
            break;

        case 'revoke_license':
            revokeDevLicense(admin.ws, email, data);
            break;

        case 'build_licensed_jar':
            buildLicensedJar(admin.ws, email, data);
            break;

        case 'suspend_server':
            suspendServer(admin.ws, email, data);
            break;

        case 'unsuspend_server':
            unsuspendServer(admin.ws, email, data);
            break;

        case 'get_suspended_servers':
            sendSuspendedServers(admin.ws);
            break;

        case 'get_users': {
            // Return user list with session counts
            let users = [];
            if (db) {
                try {
                    const stmt = db.prepare('SELECT minecraft_uuid, minecraft_username, created_at, last_login_at AS last_login FROM user_accounts ORDER BY created_at DESC');
                    users = stmt.all();
                } catch (e) { console.error('[Admin] Get users error:', e.message); }
            } else {
                users = Array.from(inMemoryUserAccounts.values()).map(a => ({
                    minecraft_uuid: a.minecraft_uuid,
                    minecraft_username: a.minecraft_username,
                    created_at: a.created_at,
                    last_login: a.last_login_at
                }));
            }
            for (const user of users) {
                if (db) {
                    try {
                        const count = db.prepare('SELECT COUNT(*) as count FROM user_sessions WHERE minecraft_uuid = ?').get(user.minecraft_uuid);
                        user.sessionCount = count?.count || 0;
                    } catch (e) { user.sessionCount = 0; }
                } else {
                    user.sessionCount = 0;
                }
            }
            admin.ws.send(JSON.stringify({ type: 'users_list', users }));
            break;
        }

        case 'get_user_details': {
            const detailUuid = data?.uuid;
            if (!detailUuid) {
                admin.ws.send(JSON.stringify({ type: 'error', message: 'UUID required' }));
                break;
            }
            let account = null, sessions = [], settings = null;
            if (db) {
                try {
                    account = db.prepare('SELECT minecraft_uuid, minecraft_username, created_at, last_login_at AS last_login FROM user_accounts WHERE minecraft_uuid = ?').get(detailUuid);
                    sessions = db.prepare('SELECT session_id, created_at, expires_at, last_active_at, ip_address FROM user_sessions WHERE minecraft_uuid = ? ORDER BY created_at DESC').all(detailUuid);
                    settings = db.prepare('SELECT * FROM user_settings WHERE uuid = ?').get(detailUuid);
                } catch (e) { console.error('[Admin] Get user details error:', e.message); }
            }
            admin.ws.send(JSON.stringify({ type: 'user_details', account, sessions, settings }));
            break;
        }

        case 'admin_reset_password': {
            const resetUuid = data?.uuid;
            if (!resetUuid) {
                admin.ws.send(JSON.stringify({ type: 'error', message: 'UUID required' }));
                break;
            }
            if (db) {
                try {
                    db.prepare('UPDATE user_accounts SET password_hash = NULL WHERE minecraft_uuid = ?').run(resetUuid);
                    db.prepare('DELETE FROM user_sessions WHERE minecraft_uuid = ?').run(resetUuid);
                } catch (e) { console.error('[Admin] Reset password error:', e.message); }
            }
            logAudit(email, 'admin_reset_password', { uuid: resetUuid });
            admin.ws.send(JSON.stringify({ type: 'password_reset_success', uuid: resetUuid }));
            break;
        }

        case 'admin_delete_account': {
            const deleteUuid = data?.uuid;
            if (!deleteUuid) {
                admin.ws.send(JSON.stringify({ type: 'error', message: 'UUID required' }));
                break;
            }
            if (db) {
                try {
                    db.prepare('DELETE FROM user_accounts WHERE minecraft_uuid = ?').run(deleteUuid);
                    db.prepare('DELETE FROM user_sessions WHERE minecraft_uuid = ?').run(deleteUuid);
                    db.prepare('DELETE FROM user_settings WHERE uuid = ?').run(deleteUuid);
                    db.prepare('DELETE FROM reviews WHERE minecraft_uuid = ?').run(deleteUuid);
                } catch (e) { console.error('[Admin] Delete account error:', e.message); }
            } else {
                inMemoryUserAccounts.delete(deleteUuid);
            }
            logAudit(email, 'admin_delete_account', { uuid: deleteUuid });
            admin.ws.send(JSON.stringify({ type: 'account_deleted', uuid: deleteUuid }));
            break;
        }

        default:
            admin.ws.send(JSON.stringify({
                type: 'error',
                code: 'UNKNOWN_TYPE',
                message: `Unknown message type: ${type}`
            }));
    }
}

// ============================================================================
// Announcement Management
// ============================================================================

function generateAnnouncementId() {
    return 'ann_' + crypto.randomBytes(8).toString('hex');
}

function createAnnouncement(ws, email, data) {
    if (!data || !data.title || !data.message) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'INVALID_DATA',
            message: 'Title and message are required'
        }));
        return;
    }

    const id = generateAnnouncementId();
    const announcement = {
        id: id,
        title: data.title.substring(0, 128),
        message: data.message,
        type: data.announcementType || 'info',
        priority: data.priority || 0,
        actionUrl: data.actionUrl || null,
        actionText: data.actionText || null,
        dismissible: data.dismissible !== false ? 1 : 0,
        createdBy: email,
        createdAt: Date.now(),
        scheduledAt: data.scheduledAt || null,
        expiresAt: data.expiresAt || null,
        active: 1,
        sentCount: 0
    };

    if (db) {
        try {
            const stmt = db.prepare(`
                INSERT INTO admin_announcements
                (id, title, message, type, priority, action_url, action_text, dismissible,
                 created_by, created_at, scheduled_at, expires_at, active, sent_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            `);
            stmt.run(
                announcement.id, announcement.title, announcement.message, announcement.type,
                announcement.priority, announcement.actionUrl, announcement.actionText,
                announcement.dismissible, announcement.createdBy, announcement.createdAt,
                announcement.scheduledAt, announcement.expiresAt, announcement.active, announcement.sentCount
            );
        } catch (e) {
            console.error('[Admin] Failed to save announcement:', e.message);
        }
    } else {
        inMemoryAnnouncements.set(id, announcement);
    }

    logAudit(email, 'announcement_created', { id, title: announcement.title, type: announcement.type });

    ws.send(JSON.stringify({
        type: 'announcement_created',
        announcement: formatAnnouncement(announcement)
    }));

    // If not scheduled, broadcast immediately
    if (!announcement.scheduledAt || announcement.scheduledAt <= Date.now()) {
        broadcastAnnouncementToAll(ws, email, id, true);
    }

    console.log(`[Admin] Announcement created by ${email}: ${announcement.title}`);
}

function updateAnnouncement(ws, email, data) {
    if (!data || !data.id) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'INVALID_DATA',
            message: 'Announcement ID is required'
        }));
        return;
    }

    const existing = getAnnouncement(data.id);
    if (!existing) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'NOT_FOUND',
            message: 'Announcement not found'
        }));
        return;
    }

    if (db) {
        try {
            const updates = [];
            const values = [];

            if (data.title !== undefined) { updates.push('title = ?'); values.push(data.title.substring(0, 128)); }
            if (data.message !== undefined) { updates.push('message = ?'); values.push(data.message); }
            if (data.announcementType !== undefined) { updates.push('type = ?'); values.push(data.announcementType); }
            if (data.priority !== undefined) { updates.push('priority = ?'); values.push(data.priority); }
            if (data.actionUrl !== undefined) { updates.push('action_url = ?'); values.push(data.actionUrl); }
            if (data.actionText !== undefined) { updates.push('action_text = ?'); values.push(data.actionText); }
            if (data.dismissible !== undefined) { updates.push('dismissible = ?'); values.push(data.dismissible ? 1 : 0); }
            if (data.active !== undefined) { updates.push('active = ?'); values.push(data.active ? 1 : 0); }
            if (data.expiresAt !== undefined) { updates.push('expires_at = ?'); values.push(data.expiresAt); }

            if (updates.length > 0) {
                values.push(data.id);
                const stmt = db.prepare(`UPDATE admin_announcements SET ${updates.join(', ')} WHERE id = ?`);
                stmt.run(...values);
            }
        } catch (e) {
            console.error('[Admin] Failed to update announcement:', e.message);
        }
    } else {
        const ann = inMemoryAnnouncements.get(data.id);
        if (ann) {
            Object.assign(ann, data);
        }
    }

    logAudit(email, 'announcement_updated', { id: data.id });

    ws.send(JSON.stringify({
        type: 'announcement_updated',
        id: data.id
    }));
}

function deleteAnnouncement(ws, email, id) {
    if (!id) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'INVALID_DATA',
            message: 'Announcement ID is required'
        }));
        return;
    }

    if (db) {
        try {
            const stmt = db.prepare('DELETE FROM admin_announcements WHERE id = ?');
            stmt.run(id);
        } catch (e) {
            console.error('[Admin] Failed to delete announcement:', e.message);
        }
    } else {
        inMemoryAnnouncements.delete(id);
    }

    logAudit(email, 'announcement_deleted', { id });

    ws.send(JSON.stringify({
        type: 'announcement_deleted',
        id: id
    }));
}

function getAnnouncement(id) {
    if (db) {
        try {
            const stmt = db.prepare('SELECT * FROM admin_announcements WHERE id = ?');
            const row = stmt.get(id);
            return row ? formatAnnouncementFromDb(row) : null;
        } catch (e) {
            return null;
        }
    }
    return inMemoryAnnouncements.get(id) || null;
}

function getAllAnnouncements() {
    if (db) {
        try {
            const stmt = db.prepare('SELECT * FROM admin_announcements ORDER BY created_at DESC');
            return stmt.all().map(formatAnnouncementFromDb);
        } catch (e) {
            return [];
        }
    }
    return Array.from(inMemoryAnnouncements.values());
}

function getActiveAnnouncements() {
    const now = Date.now();
    if (db) {
        try {
            const stmt = db.prepare(`
                SELECT * FROM admin_announcements
                WHERE active = 1
                AND (scheduled_at IS NULL OR scheduled_at <= ?)
                AND (expires_at IS NULL OR expires_at > ?)
                ORDER BY priority DESC, created_at DESC
            `);
            return stmt.all(now, now).map(formatAnnouncementFromDb);
        } catch (e) {
            return [];
        }
    }
    return Array.from(inMemoryAnnouncements.values()).filter(a =>
        a.active &&
        (!a.scheduledAt || a.scheduledAt <= now) &&
        (!a.expiresAt || a.expiresAt > now)
    );
}

function formatAnnouncementFromDb(row) {
    return {
        id: row.id,
        title: row.title,
        message: row.message,
        type: row.type,
        priority: row.priority,
        actionUrl: row.action_url,
        actionText: row.action_text,
        dismissible: row.dismissible === 1,
        createdBy: row.created_by,
        createdAt: row.created_at,
        scheduledAt: row.scheduled_at,
        expiresAt: row.expires_at,
        active: row.active === 1,
        sentCount: row.sent_count
    };
}

function formatAnnouncement(ann) {
    return {
        id: ann.id,
        title: ann.title,
        message: ann.message,
        announcementType: ann.type,
        priority: ann.priority,
        actionUrl: ann.actionUrl,
        actionText: ann.actionText,
        dismissible: ann.dismissible === 1 || ann.dismissible === true,
        createdBy: ann.createdBy,
        createdAt: ann.createdAt,
        scheduledAt: ann.scheduledAt,
        expiresAt: ann.expiresAt,
        active: ann.active === 1 || ann.active === true,
        sentCount: ann.sentCount
    };
}

function sendAllAnnouncements(ws) {
    const announcements = getAllAnnouncements().map(formatAnnouncement);
    ws.send(JSON.stringify({
        type: 'announcements_list',
        announcements: announcements
    }));
}

function sendActiveAnnouncements(ws) {
    const announcements = getActiveAnnouncements().map(formatAnnouncement);
    ws.send(JSON.stringify({
        type: 'active_announcements',
        announcements: announcements
    }));
}

/**
 * Broadcast announcement to all connected MC servers (which forward to web panels)
 */
function broadcastAnnouncementToAll(ws, email, announcementId, silent = false) {
    const announcement = getAnnouncement(announcementId);
    if (!announcement) {
        if (!silent) {
            ws.send(JSON.stringify({
                type: 'error',
                code: 'NOT_FOUND',
                message: 'Announcement not found'
            }));
        }
        return;
    }

    const payload = {
        type: 'ADMIN_ANNOUNCEMENT',
        data: formatAnnouncement(announcement)
    };

    let sentCount = 0;

    // Send to all MC servers
    mcServers.forEach((server, serverId) => {
        if (server.ws.readyState === WebSocket.OPEN) {
            server.ws.send(JSON.stringify(payload));
            sentCount++;
        }
    });

    // Also send directly to all connected browser panel clients
    browserClients.forEach((client) => {
        if (client.ws.readyState === WebSocket.OPEN) {
            client.ws.send(JSON.stringify(payload));
        }
    });

    // Also send to global panel clients (server list page)
    globalPanelClients.forEach((client) => {
        if (client.ws.readyState === WebSocket.OPEN) {
            client.ws.send(JSON.stringify(payload));
        }
    });

    // Update sent count in database
    if (db) {
        try {
            const stmt = db.prepare('UPDATE admin_announcements SET sent_count = sent_count + ? WHERE id = ?');
            stmt.run(sentCount, announcementId);
        } catch (e) {}
    }

    logAudit(email, 'announcement_broadcast', { id: announcementId, servers: sentCount });

    if (!silent) {
        ws.send(JSON.stringify({
            type: 'announcement_broadcast',
            id: announcementId,
            sentCount: sentCount
        }));
    }

    console.log(`[Admin] Announcement ${announcementId} broadcast to ${sentCount} servers by ${email}`);
}

// ============================================================================
// Admin Data Helpers
// ============================================================================

function sendGatewayHealth(ws) {
    const mem = process.memoryUsage();
    const memUsedMB = mem.rss / (1024 * 1024);
    const memTotalMB = require('os').totalmem() / (1024 * 1024);
    const memPercent = Math.round((memUsedMB / memTotalMB) * 100);

    // Calculate messages per second from recent activity
    let totalMsgRate = 0;
    messageRates.forEach(rate => {
        const elapsed = (Date.now() - rate.windowStart) / 1000;
        if (elapsed > 0) totalMsgRate += rate.count / elapsed;
    });

    // Calculate CPU usage percentage
    const cpuPercent = calculateCpuUsage();

    const health = {
        status: 'ok',
        healthy: true,
        uptime: Math.round(process.uptime() * 1000),
        connections: mcServers.size,
        browsers: browserClients.size,
        admins: adminClients.size,
        memoryUsage: memPercent,
        cpuUsage: cpuPercent,
        messagesPerSecond: Math.round(totalMsgRate),
        recentErrors: [],
        timestamp: Date.now()
    };

    ws.send(JSON.stringify({
        type: 'gateway_health',
        data: health
    }));
}

/**
 * Calculate CPU usage percentage.
 */
function calculateCpuUsage() {
    // Returns cached value from periodic sampler (no side effects, no race conditions)
    return currentCpuPercent;
}

function sendServerList(ws) {
    const servers = [];
    mcServers.forEach((data, id) => {
        servers.push({
            id: id,
            name: data.info?.serverName || 'Unknown',
            version: data.info?.version || 'unknown',
            players: data.info?.players || 0,
            urlPrefix: data.urlPrefix || id,
            connectedAt: data.connectedAt,
            lastHeartbeat: data.lastHeartbeat
        });
    });

    ws.send(JSON.stringify({
        type: 'servers_list',
        servers: servers,
        total: servers.length
    }));
}

function sendAuditLog(ws, limit = 100) {
    let logs = [];

    if (db) {
        try {
            const stmt = db.prepare('SELECT * FROM admin_audit_log ORDER BY timestamp DESC LIMIT ?');
            logs = stmt.all(limit);
        } catch (e) {}
    }

    ws.send(JSON.stringify({
        type: 'audit_log',
        entries: logs
    }));
}

function logAudit(email, action, details) {
    const timestamp = Date.now();

    if (db) {
        try {
            const stmt = db.prepare('INSERT INTO admin_audit_log (admin_email, action, details, timestamp) VALUES (?, ?, ?, ?)');
            stmt.run(email, action, details ? JSON.stringify(details) : null, timestamp);
        } catch (e) {
            console.error('[Admin] Failed to log audit:', e.message);
        }
    }

    console.log(`[Audit] ${email}: ${action}${details ? ' - ' + JSON.stringify(details) : ''}`);
}

/**
 * Deactivate an announcement (set active=0, keeps the record).
 */
function deactivateAnnouncement(ws, email, id) {
    if (!id) {
        ws.send(JSON.stringify({ type: 'error', code: 'INVALID_DATA', message: 'Announcement ID is required' }));
        return;
    }

    if (db) {
        try {
            db.prepare('UPDATE admin_announcements SET active = 0 WHERE id = ?').run(id);
        } catch (e) {
            console.error('[Admin] Failed to deactivate announcement:', e.message);
        }
    } else {
        const ann = inMemoryAnnouncements.get(id);
        if (ann) ann.active = false;
    }

    logAudit(email, 'announcement_deactivated', { id });
    ws.send(JSON.stringify({ type: 'announcement_deactivated', id: id }));
}

/**
 * Send dashboard overview data.
 */
function sendDashboardData(ws) {
    let totalPlayers = 0;
    mcServers.forEach(data => { totalPlayers += data.info?.players || 0; });

    const activeAnnouncementCount = getActiveAnnouncements().length;

    // Get recent activity from audit log and map to frontend format
    let recentActivity = [];
    if (db) {
        try {
            const rows = db.prepare('SELECT * FROM admin_audit_log ORDER BY timestamp DESC LIMIT 10').all();
            recentActivity = rows.map(row => {
                // Map audit action to activity type for icon
                let type = 'circle';
                const action = (row.action || '').toLowerCase();
                if (action.includes('connect') || action.includes('login') || action.includes('auth')) type = 'connect';
                else if (action.includes('disconnect') || action.includes('logout')) type = 'disconnect';
                else if (action.includes('announcement')) type = 'announcement';
                else if (action.includes('premium')) type = 'premium';
                else if (action.includes('error') || action.includes('fail')) type = 'error';

                // Build readable text
                let text = row.action || 'Unknown action';
                if (row.admin_email) text = `${row.admin_email}: ${text}`;
                if (row.details) {
                    try {
                        const details = JSON.parse(row.details);
                        if (details.serverName) text += ` (${details.serverName})`;
                        else if (details.id) text += ` #${details.id}`;
                    } catch (e) {}
                }

                return { type, text, timestamp: row.timestamp };
            });
        } catch (e) {}
    }

    // Build version distribution from connected servers
    const versionDistribution = {};
    mcServers.forEach(data => {
        const ver = data.info?.version || 'unknown';
        versionDistribution[ver] = (versionDistribution[ver] || 0) + 1;
    });

    // Build connection history from last 24h from database
    let connectionHistory = [];
    if (db) {
        try {
            const stmt = db.prepare(
                'SELECT timestamp, servers, browsers FROM gateway_metrics_history WHERE timestamp > ? ORDER BY timestamp ASC'
            );
            const dayAgo = Date.now() - (24 * 3600000);
            const rows = stmt.all(dayAgo);
            connectionHistory = rows.map(row => ({
                time: new Date(row.timestamp).toISOString(),
                servers: row.servers,
                browsers: row.browsers
            }));
        } catch (e) {
            console.error('[Dashboard] Failed to fetch connection history:', e.message);
        }
    }

    // Fallback: If no history available, show current state
    if (connectionHistory.length === 0) {
        const now = Date.now();
        for (let i = 23; i >= 0; i--) {
            const hour = new Date(now - i * 3600000);
            connectionHistory.push({
                time: hour.toISOString(),
                servers: mcServers.size,
                browsers: browserClients.size
            });
        }
    }

    ws.send(JSON.stringify({
        type: 'dashboard_data',
        data: {
            servers: mcServers.size,
            players: totalPlayers,
            browsers: browserClients.size,
            announcements: activeAnnouncementCount,
            uptime: Math.round(process.uptime() * 1000),
            activity: recentActivity,
            versionDistribution: Object.keys(versionDistribution).length > 0 ? versionDistribution : null,
            connectionHistory: connectionHistory
        }
    }));
}

/**
 * Export full audit log as JSON.
 */
function exportAuditLog(ws) {
    let logs = [];
    if (db) {
        try {
            logs = db.prepare('SELECT * FROM admin_audit_log ORDER BY timestamp DESC').all();
        } catch (e) {}
    }

    ws.send(JSON.stringify({
        type: 'export_audit_log',
        entries: logs,
        exportedAt: Date.now()
    }));
}

/**
 * Send analytics data (available gateway stats).
 */
function sendAnalyticsData(ws) {
    let totalPlayers = 0;
    mcServers.forEach(data => { totalPlayers += data.info?.players || 0; });

    ws.send(JSON.stringify({
        type: 'analytics_data',
        data: {
            totalServers: mcServers.size,
            totalPlayers: totalPlayers,
            connectedBrowsers: browserClients.size,
            connectedAdmins: adminClients.size,
            uptime: process.uptime(),
            memory: process.memoryUsage()
        }
    }));
}

/**
 * Send premium data (stub — premium system not yet implemented).
 */
function sendPremiumData(ws) {
    ws.send(JSON.stringify({
        type: 'premium_data',
        data: {
            premiumServers: 0,
            totalLicenses: 0,
            activeLicenses: 0,
            message: 'Premium license system coming soon'
        }
    }));
}

// Run scheduled announcement check every minute
setInterval(() => {
    const now = Date.now();
    const announcements = getActiveAnnouncements();

    announcements.forEach(ann => {
        // Check if newly scheduled announcement should be broadcast
        if (ann.scheduledAt && ann.scheduledAt <= now && ann.sentCount === 0) {
            console.log(`[Admin] Broadcasting scheduled announcement: ${ann.id}`);
            broadcastAnnouncementToAll(null, 'system', ann.id, true);
        }
    });
}, 60000);

/**
 * Cleanup dead servers (no heartbeat)
 */
function cleanupDeadServers() {
    const now = Date.now();
    mcServers.forEach((server, serverId) => {
        if (now - server.lastHeartbeat > CONFIG.serverTimeout) {
            console.log(`[Server] ${serverId} timed out (no heartbeat)`);
            unregisterUrlPrefix(serverId);
            server.ws.close(4005, 'Heartbeat timeout');
            mcServers.delete(serverId);
        }
    });
}

// Run cleanup every 30 seconds
setInterval(cleanupDeadServers, CONFIG.heartbeatInterval);

/**
 * Cleanup expired link codes and sessions.
 */
function cleanupExpiredAuthData() {
    const now = Date.now();

    if (db) {
        try {
            const deletedCodes = db.prepare('DELETE FROM link_codes WHERE expires_at < ? OR used = 1').run(now);
            const deletedSessions = db.prepare('DELETE FROM user_sessions WHERE expires_at < ?').run(now);
            if (deletedCodes.changes > 0) console.log(`[Auth] Cleaned up ${deletedCodes.changes} expired/used link codes`);
            if (deletedSessions.changes > 0) console.log(`[Auth] Cleaned up ${deletedSessions.changes} expired sessions`);
        } catch (e) {
            console.error('[Auth] Cleanup error:', e.message);
        }
    } else {
        for (const [hash, code] of inMemoryLinkCodes) {
            if (code.expires_at < now || code.used) inMemoryLinkCodes.delete(hash);
        }
        for (const [id, session] of inMemoryUserSessions) {
            if (session.expires_at < now) inMemoryUserSessions.delete(id);
        }
    }

    // Clean up rate limit maps (entries older than 15 minutes)
    const cutoff = now - 15 * 60 * 1000;
    for (const [ip, data] of linkVerifyAttempts) {
        if (data.firstAttempt < cutoff) linkVerifyAttempts.delete(ip);
    }
    for (const [uuid, data] of passwordAuthAttempts) {
        if (data.lastAttempt < cutoff) passwordAuthAttempts.delete(uuid);
    }
}

// Run auth cleanup every 60 seconds
setInterval(cleanupExpiredAuthData, 60000);

// ============================================================================
// Link & Auth Helper Functions
// ============================================================================

/**
 * Read JSON body from HTTP request.
 */
function readJsonBody(req, callback) {
    let body = '';
    req.on('data', chunk => { body += chunk.toString(); });
    req.on('end', () => {
        try {
            callback(JSON.parse(body));
        } catch (e) {
            callback(null);
        }
    });
}

/**
 * Send JSON HTTP response.
 */
function jsonResponse(res, status, data) {
    res.writeHead(status, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(data));
}

/**
 * Get client IP from HTTP request (Cloudflare-aware).
 */
function clientIpFromReq(req) {
    const isBehindCF = !!(req.headers['cf-ray'] && req.headers['cf-connecting-ip']);
    return isBehindCF ? req.headers['cf-connecting-ip'] : req.socket.remoteAddress;
}

/**
 * Store a link code hash in the database.
 */
function storeLinkCode(codeHash, uuid, username, serverId, expiresAt) {
    const now = Date.now();
    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO link_codes (code_hash, minecraft_uuid, minecraft_username, server_id, created_at, expires_at, used) VALUES (?, ?, ?, ?, ?, ?, 0)'
            ).run(codeHash, uuid, username, serverId || null, now, expiresAt);
        } catch (e) {
            console.error('[Auth] Failed to store link code:', e.message);
            inMemoryLinkCodes.set(codeHash, { minecraft_uuid: uuid, minecraft_username: username, server_id: serverId, created_at: now, expires_at: expiresAt, used: 0 });
        }
    } else {
        inMemoryLinkCodes.set(codeHash, { minecraft_uuid: uuid, minecraft_username: username, server_id: serverId, created_at: now, expires_at: expiresAt, used: 0 });
    }
}

/**
 * Get a link code by hash.
 */
function getLinkCode(codeHash) {
    if (db) {
        try {
            const row = db.prepare('SELECT * FROM link_codes WHERE code_hash = ? AND expires_at > ? AND used = 0').get(codeHash, Date.now());
            return row || null;
        } catch (e) {
            console.error('[Auth] Failed to get link code:', e.message);
        }
    }
    const code = inMemoryLinkCodes.get(codeHash);
    if (code && code.expires_at > Date.now() && !code.used) return code;
    return null;
}

/**
 * Mark a link code as used.
 */
function markLinkCodeUsed(codeHash) {
    if (db) {
        try {
            db.prepare('UPDATE link_codes SET used = 1 WHERE code_hash = ?').run(codeHash);
        } catch (e) {
            console.error('[Auth] Failed to mark link code used:', e.message);
        }
    }
    const code = inMemoryLinkCodes.get(codeHash);
    if (code) code.used = 1;
}

/**
 * Get a user account by UUID.
 */
function getUserAccount(uuid) {
    if (db) {
        try {
            return db.prepare('SELECT * FROM user_accounts WHERE minecraft_uuid = ?').get(uuid) || null;
        } catch (e) {
            console.error('[Auth] Failed to get user account:', e.message);
        }
    }
    return inMemoryUserAccounts.get(uuid) || null;
}

/**
 * Create a new user account.
 */
function createUserAccount(uuid, username, passwordHash) {
    const now = Date.now();
    if (db) {
        try {
            db.prepare(
                'INSERT INTO user_accounts (minecraft_uuid, minecraft_username, password_hash, created_at, updated_at, failed_attempts, locked_until, device_fingerprints, auto_sign_in) VALUES (?, ?, ?, ?, ?, 0, 0, ?, 1)'
            ).run(uuid, username, passwordHash, now, now, '[]');
        } catch (e) {
            console.error('[Auth] Failed to create user account:', e.message);
            inMemoryUserAccounts.set(uuid, { minecraft_uuid: uuid, minecraft_username: username, password_hash: passwordHash, created_at: now, updated_at: now, failed_attempts: 0, locked_until: 0, device_fingerprints: '[]', auto_sign_in: 1 });
        }
    } else {
        inMemoryUserAccounts.set(uuid, { minecraft_uuid: uuid, minecraft_username: username, password_hash: passwordHash, created_at: now, updated_at: now, failed_attempts: 0, locked_until: 0, device_fingerprints: '[]', auto_sign_in: 1 });
    }
}

// Mojang UUID validation cache (24 hour TTL)
const mojangUuidCache = new Map();
const MOJANG_CACHE_TTL = 24 * 60 * 60 * 1000; // 24 hours

/**
 * Validate that a Minecraft UUID is an official Java Edition UUID or a Floodgate (Bedrock) UUID.
 * Cracked UUIDs will fail validation.
 * @returns {boolean} true if valid official UUID, false if cracked/invalid
 */
async function validateMinecraftUuid(username, uuid) {
    if (!username || !uuid) return false;

    // Floodgate (Bedrock) UUIDs start with 00000000-0000-0000-0009-
    const uuidNoDashes = uuid.replace(/-/g, '');
    if (uuidNoDashes.startsWith('00000000000000000009')) {
        return true; // Floodgate UUID — accept without Mojang API check
    }

    // Check cache
    const cacheKey = username.toLowerCase();
    const cached = mojangUuidCache.get(cacheKey);
    if (cached && Date.now() - cached.timestamp < MOJANG_CACHE_TTL) {
        return cached.uuid === uuidNoDashes;
    }

    // Query Mojang API
    try {
        const res = await fetch(`https://api.mojang.com/users/profiles/minecraft/${encodeURIComponent(username)}`);
        if (res.status === 204 || res.status === 404) {
            // No such user — cracked account
            mojangUuidCache.set(cacheKey, { uuid: null, timestamp: Date.now() });
            return false;
        }
        if (!res.ok) {
            // API error — don't block, assume valid
            console.warn('[Auth] Mojang API returned status:', res.status);
            return true;
        }
        const data = await res.json();
        const mojangUuid = (data.id || '').toLowerCase();
        mojangUuidCache.set(cacheKey, { uuid: mojangUuid, timestamp: Date.now() });
        return mojangUuid === uuidNoDashes.toLowerCase();
    } catch (e) {
        console.warn('[Auth] Mojang API error:', e.message);
        return true; // Don't block on API errors
    }
}

/**
 * Create a session and return the raw token.
 */
function createSession(uuid, fingerprintHash, ipAddress) {
    const rawToken = crypto.randomBytes(32).toString('hex'); // 64 hex chars
    const sessionId = crypto.createHash('sha256').update(rawToken).digest('hex');
    const now = Date.now();
    const expiresAt = now + 30 * 24 * 60 * 60 * 1000; // 30 days

    if (db) {
        try {
            db.prepare(
                'INSERT INTO user_sessions (session_id, minecraft_uuid, device_fingerprint_hash, created_at, expires_at, last_active_at, ip_address) VALUES (?, ?, ?, ?, ?, ?, ?)'
            ).run(sessionId, uuid, fingerprintHash || null, now, expiresAt, now, ipAddress || null);
        } catch (e) {
            console.error('[Auth] Failed to create session:', e.message);
            inMemoryUserSessions.set(sessionId, { minecraft_uuid: uuid, device_fingerprint_hash: fingerprintHash, created_at: now, expires_at: expiresAt, last_active_at: now, ip_address: ipAddress });
        }
    } else {
        inMemoryUserSessions.set(sessionId, { minecraft_uuid: uuid, device_fingerprint_hash: fingerprintHash, created_at: now, expires_at: expiresAt, last_active_at: now, ip_address: ipAddress });
    }

    return rawToken;
}

/**
 * Validate a session token and return session data.
 */
function validateSession(rawToken) {
    const sessionId = crypto.createHash('sha256').update(rawToken).digest('hex');
    const now = Date.now();

    if (db) {
        try {
            const row = db.prepare('SELECT * FROM user_sessions WHERE session_id = ? AND expires_at > ?').get(sessionId, now);
            if (row) {
                db.prepare('UPDATE user_sessions SET last_active_at = ? WHERE session_id = ?').run(now, sessionId);
                return row;
            }
        } catch (e) {
            console.error('[Auth] Failed to validate session:', e.message);
        }
    } else {
        const session = inMemoryUserSessions.get(sessionId);
        if (session && session.expires_at > now) {
            session.last_active_at = now;
            return session;
        }
    }
    return null;
}

/**
 * Revoke all sessions for a UUID.
 */
function revokeAllSessions(uuid) {
    if (db) {
        try {
            db.prepare('DELETE FROM user_sessions WHERE minecraft_uuid = ?').run(uuid);
        } catch (e) {
            console.error('[Auth] Failed to revoke sessions:', e.message);
        }
    } else {
        for (const [id, session] of inMemoryUserSessions) {
            if (session.minecraft_uuid === uuid) inMemoryUserSessions.delete(id);
        }
    }
}

// ============================================================================
// TOTP (Time-based One-Time Password) Implementation — RFC 6238
// ============================================================================

/**
 * Generate a random TOTP secret (base32 encoded, 20 bytes).
 */
function generateTotpSecret() {
    const bytes = crypto.randomBytes(20);
    const base32chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ234567';
    let secret = '';
    for (let i = 0; i < bytes.length; i++) {
        secret += base32chars[bytes[i] % 32];
    }
    return secret;
}

/**
 * Decode a base32-encoded string to a Buffer.
 */
function base32Decode(str) {
    const base32chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ234567';
    let bits = '';
    for (const c of str.toUpperCase()) {
        const val = base32chars.indexOf(c);
        if (val === -1) continue;
        bits += val.toString(2).padStart(5, '0');
    }
    const bytes = new Uint8Array(Math.floor(bits.length / 8));
    for (let i = 0; i < bytes.length; i++) {
        bytes[i] = parseInt(bits.slice(i * 8, i * 8 + 8), 2);
    }
    return Buffer.from(bytes);
}

/**
 * Generate a TOTP code for the current time step.
 */
function generateTotp(secret, timeStep = 30, digits = 6) {
    const time = Math.floor(Date.now() / 1000 / timeStep);
    const timeBuffer = Buffer.alloc(8);
    timeBuffer.writeUInt32BE(0, 0);
    timeBuffer.writeUInt32BE(time, 4);
    const key = base32Decode(secret);
    const hmac = crypto.createHmac('sha1', key).update(timeBuffer).digest();
    const offset = hmac[hmac.length - 1] & 0xf;
    const code = ((hmac[offset] & 0x7f) << 24 | hmac[offset + 1] << 16 | hmac[offset + 2] << 8 | hmac[offset + 3]) % Math.pow(10, digits);
    return code.toString().padStart(digits, '0');
}

/**
 * Verify a TOTP code, checking a window of time steps for clock drift.
 */
function verifyTotp(secret, code, window = 1) {
    const timeStep = 30;
    for (let i = -window; i <= window; i++) {
        const time = Math.floor(Date.now() / 1000 / timeStep) + i;
        const timeBuffer = Buffer.alloc(8);
        timeBuffer.writeUInt32BE(0, 0);
        timeBuffer.writeUInt32BE(time, 4);
        const key = base32Decode(secret);
        const hmac = crypto.createHmac('sha1', key).update(timeBuffer).digest();
        const offset = hmac[hmac.length - 1] & 0xf;
        const expected = ((hmac[offset] & 0x7f) << 24 | hmac[offset + 1] << 16 | hmac[offset + 2] << 8 | hmac[offset + 3]) % 1000000;
        if (expected.toString().padStart(6, '0') === code) return true;
    }
    return false;
}

// ============================================================================
// Admin Session Helpers
// ============================================================================

/**
 * Create an admin session and return the raw token.
 */
function createAdminSession(uuid, ipAddress, fullyAuthenticated = false) {
    const rawToken = crypto.randomBytes(32).toString('hex');
    const sessionId = crypto.createHash('sha256').update(rawToken).digest('hex');
    const now = Date.now();
    const expiresAt = now + 8 * 60 * 60 * 1000; // 8 hours

    if (db) {
        try {
            db.prepare('INSERT INTO admin_sessions (session_id, minecraft_uuid, created_at, expires_at, fully_authenticated, ip_address) VALUES (?, ?, ?, ?, ?, ?)')
                .run(sessionId, uuid, now, expiresAt, fullyAuthenticated ? 1 : 0, ipAddress || '');
        } catch (e) { console.error('[Admin] Session create error:', e.message); }
    } else {
        inMemoryAdminSessions.set(sessionId, { minecraft_uuid: uuid, created_at: now, expires_at: expiresAt, fully_authenticated: fullyAuthenticated ? 1 : 0, ip_address: ipAddress || '' });
    }
    return rawToken;
}

/**
 * Validate an admin session token. Returns session data or null.
 */
function validateAdminSession(rawToken, requireFullAuth = true) {
    const sessionId = crypto.createHash('sha256').update(rawToken).digest('hex');
    let session = null;
    if (db) {
        try {
            session = db.prepare('SELECT * FROM admin_sessions WHERE session_id = ? AND expires_at > ?').get(sessionId, Date.now());
        } catch (e) {}
    } else {
        session = inMemoryAdminSessions.get(sessionId);
        if (session && session.expires_at <= Date.now()) session = null;
    }
    if (!session) return null;
    if (requireFullAuth && !session.fully_authenticated) return null;
    // Check UUID is still in admin list
    if (!ADMIN_UUIDS.has(session.minecraft_uuid)) return null;
    return session;
}

/**
 * Upgrade an admin session to fully authenticated (after 2FA).
 */
function upgradeAdminSession(rawToken) {
    const sessionId = crypto.createHash('sha256').update(rawToken).digest('hex');
    if (db) {
        try {
            db.prepare('UPDATE admin_sessions SET fully_authenticated = 1 WHERE session_id = ?').run(sessionId);
        } catch (e) {}
    } else {
        const session = inMemoryAdminSessions.get(sessionId);
        if (session) session.fully_authenticated = 1;
    }
}

/**
 * Get all reviews sorted by newest first.
 */
function getAllReviews() {
    if (db) {
        try {
            return db.prepare('SELECT * FROM reviews ORDER BY created_at DESC').all();
        } catch (e) {
            console.error('[Reviews] Failed to get reviews:', e.message);
        }
    }
    return Array.from(inMemoryReviews.values()).sort((a, b) => b.created_at - a.created_at);
}

/**
 * Insert or update a review (one per user).
 */
function upsertReview(uuid, username, rating, description) {
    const now = Date.now();
    if (db) {
        try {
            const existing = db.prepare('SELECT id FROM reviews WHERE minecraft_uuid = ?').get(uuid);
            if (existing) {
                db.prepare('UPDATE reviews SET minecraft_username = ?, rating = ?, description = ?, updated_at = ? WHERE minecraft_uuid = ?')
                    .run(username, rating, description, now, uuid);
            } else {
                db.prepare('INSERT INTO reviews (minecraft_uuid, minecraft_username, rating, description, created_at) VALUES (?, ?, ?, ?, ?)')
                    .run(uuid, username, rating, description, now);
            }
        } catch (e) {
            console.error('[Reviews] Failed to upsert review:', e.message);
        }
    } else {
        inMemoryReviews.set(uuid, { minecraft_uuid: uuid, minecraft_username: username, rating, description, created_at: inMemoryReviews.get(uuid)?.created_at || now, updated_at: now });
    }
}

/**
 * Save device fingerprint for a user account.
 */
function saveDeviceFingerprint(uuid, fingerprintHash) {
    if (!fingerprintHash) return;

    const account = getUserAccount(uuid);
    if (!account) return;

    let fingerprints;
    try {
        fingerprints = JSON.parse(account.device_fingerprints || '[]');
    } catch (e) {
        fingerprints = [];
    }

    if (!fingerprints.includes(fingerprintHash)) {
        fingerprints.push(fingerprintHash);
        // Keep max 10 fingerprints
        if (fingerprints.length > 10) fingerprints.shift();

        if (db) {
            try {
                db.prepare('UPDATE user_accounts SET device_fingerprints = ?, updated_at = ? WHERE minecraft_uuid = ?')
                    .run(JSON.stringify(fingerprints), Date.now(), uuid);
            } catch (e) {
                console.error('[Auth] Failed to save device fingerprint:', e.message);
            }
        } else {
            const acc = inMemoryUserAccounts.get(uuid);
            if (acc) {
                acc.device_fingerprints = JSON.stringify(fingerprints);
                acc.updated_at = Date.now();
            }
        }
    }
}

/**
 * Handle POST /api/link/register — Create account with password.
 */
async function handleLinkRegister(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.codeHash || !body.password) {
            return jsonResponse(res, 400, { error: 'Code hash and password are required' });
        }

        if (!argon2) {
            return jsonResponse(res, 503, { error: 'Password authentication is not available on this server' });
        }

        // Verify the link code is still valid
        const linkCode = getLinkCode(body.codeHash);
        if (!linkCode) {
            return jsonResponse(res, 404, { error: 'Invalid or expired code' });
        }

        // Check if account already exists
        const existing = getUserAccount(linkCode.minecraft_uuid);
        if (existing) {
            return jsonResponse(res, 409, { error: 'Account already exists. Use /api/link/login instead.' });
        }

        // Validate password strength (minimum 8 chars)
        if (body.password.length < 8) {
            return jsonResponse(res, 400, { error: 'Password must be at least 8 characters' });
        }

        // Validate UUID against Mojang API (reject cracked accounts)
        const isValidUuid = await validateMinecraftUuid(linkCode.minecraft_username, linkCode.minecraft_uuid);
        if (!isValidUuid) {
            return jsonResponse(res, 403, {
                error: 'Cracked accounts are not supported for password authentication. Use legacy token auth instead.'
            });
        }

        try {
            // Hash password with Argon2id
            const passwordHash = await argon2.hash(body.password, {
                type: argon2.argon2id,
                memoryCost: 65536,
                timeCost: 3,
                parallelism: 4,
                hashLength: 32
            });

            // Create account
            createUserAccount(linkCode.minecraft_uuid, linkCode.minecraft_username, passwordHash);

            // Mark link code as used
            markLinkCodeUsed(body.codeHash);

            // Create session
            const ip = clientIpFromReq(req);
            const sessionToken = createSession(linkCode.minecraft_uuid, body.fingerprintHash || null, ip);

            // Save device fingerprint
            if (body.fingerprintHash) {
                saveDeviceFingerprint(linkCode.minecraft_uuid, body.fingerprintHash);
            }

            console.log(`[Auth] Account created for ${linkCode.minecraft_username} (${linkCode.minecraft_uuid})`);

            jsonResponse(res, 201, {
                success: true,
                uuid: linkCode.minecraft_uuid,
                username: linkCode.minecraft_username,
                sessionToken
            });
        } catch (e) {
            console.error('[Auth] Registration failed:', e.message);
            jsonResponse(res, 500, { error: 'Registration failed' });
        }
    });
}

/**
 * Handle POST /api/link/login — Auto-login for existing users with valid link code.
 */
async function handleLinkLogin(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.codeHash) {
            return jsonResponse(res, 400, { error: 'Code hash is required' });
        }

        const linkCode = getLinkCode(body.codeHash);
        if (!linkCode) {
            return jsonResponse(res, 404, { error: 'Invalid or expired code' });
        }

        // Verify account exists
        const account = getUserAccount(linkCode.minecraft_uuid);
        if (!account) {
            return jsonResponse(res, 404, { error: 'No account found. Use /api/link/register instead.' });
        }

        // Mark link code as used
        markLinkCodeUsed(body.codeHash);

        // Create session
        const ip = clientIpFromReq(req);
        const sessionToken = createSession(linkCode.minecraft_uuid, body.fingerprintHash || null, ip);

        // Save device fingerprint
        if (body.fingerprintHash) {
            saveDeviceFingerprint(linkCode.minecraft_uuid, body.fingerprintHash);
        }

        // Update last login
        if (db) {
            try {
                db.prepare('UPDATE user_accounts SET last_login_at = ?, failed_attempts = 0 WHERE minecraft_uuid = ?')
                    .run(Date.now(), linkCode.minecraft_uuid);
            } catch (e) {}
        }

        console.log(`[Auth] ${linkCode.minecraft_username} logged in via link code`);

        jsonResponse(res, 200, {
            success: true,
            uuid: linkCode.minecraft_uuid,
            username: linkCode.minecraft_username,
            sessionToken
        });
    });
}

/**
 * Handle POST /api/auth/login — Password-based login.
 */
async function handlePasswordLogin(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.username || !body.password) {
            return jsonResponse(res, 400, { error: 'Username and password are required' });
        }

        if (!argon2) {
            return jsonResponse(res, 503, { error: 'Password authentication is not available on this server' });
        }

        // Verify Cloudflare Turnstile token (if secret key is configured)
        const turnstileSecret = process.env.TURNSTILE_SECRET_KEY;
        if (turnstileSecret && body.turnstileToken) {
            try {
                const turnstileRes = await fetch('https://challenges.cloudflare.com/turnstile/v0/siteverify', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ secret: turnstileSecret, response: body.turnstileToken })
                });
                const turnstileData = await turnstileRes.json();
                if (!turnstileData.success) {
                    return jsonResponse(res, 403, { error: 'Verification failed. Please try again.' });
                }
            } catch (e) {
                console.error('[Auth] Turnstile verification error:', e.message);
                // Don't block login if Turnstile API is unreachable
            }
        } else if (turnstileSecret && !body.turnstileToken) {
            return jsonResponse(res, 403, { error: 'Verification required.' });
        }

        // Look up account by username (case-insensitive)
        let account = null;
        if (db) {
            try {
                account = db.prepare('SELECT * FROM user_accounts WHERE LOWER(minecraft_username) = LOWER(?)').get(body.username);
            } catch (e) {
                console.error('[Auth] Login lookup error:', e.message);
            }
        } else {
            for (const [uuid, acc] of inMemoryUserAccounts) {
                if (acc.minecraft_username.toLowerCase() === body.username.toLowerCase()) {
                    account = acc;
                    break;
                }
            }
        }

        if (!account) {
            return jsonResponse(res, 401, { error: 'Invalid username or password' });
        }

        // Check lockout
        if (account.locked_until && account.locked_until > Date.now()) {
            const remaining = Math.ceil((account.locked_until - Date.now()) / 60000);
            return jsonResponse(res, 429, { error: `Account locked. Try again in ${remaining} minute(s).` });
        }

        // Rate limit by UUID
        const attempts = passwordAuthAttempts.get(account.minecraft_uuid) || { count: 0, lastAttempt: 0 };
        if (attempts.count >= 5) {
            const cooldown = Math.min(900000, 1000 * Math.pow(2, attempts.count - 5)); // exponential backoff, max 15min
            if (Date.now() - attempts.lastAttempt < cooldown) {
                return jsonResponse(res, 429, { error: 'Too many failed attempts. Please wait before trying again.' });
            }
        }

        try {
            const valid = await argon2.verify(account.password_hash, body.password);
            if (!valid) {
                // Track failed attempt
                attempts.count++;
                attempts.lastAttempt = Date.now();
                passwordAuthAttempts.set(account.minecraft_uuid, attempts);

                // Lock account after 5 failures (15 min lockout)
                const failedCount = (account.failed_attempts || 0) + 1;
                const lockUntil = failedCount >= 5 ? Date.now() + 15 * 60 * 1000 : 0;
                if (db) {
                    try {
                        db.prepare('UPDATE user_accounts SET failed_attempts = ?, locked_until = ? WHERE minecraft_uuid = ?')
                            .run(failedCount, lockUntil, account.minecraft_uuid);
                    } catch (e) {}
                }

                return jsonResponse(res, 401, { error: 'Invalid username or password' });
            }

            // Success — clear failed attempts
            passwordAuthAttempts.delete(account.minecraft_uuid);
            if (db) {
                try {
                    db.prepare('UPDATE user_accounts SET failed_attempts = 0, locked_until = 0, last_login_at = ? WHERE minecraft_uuid = ?')
                        .run(Date.now(), account.minecraft_uuid);
                } catch (e) {}
            }

            // Create session
            const ip = clientIpFromReq(req);
            const sessionToken = createSession(account.minecraft_uuid, body.fingerprintHash || null, ip);

            // Save device fingerprint
            if (body.fingerprintHash) {
                saveDeviceFingerprint(account.minecraft_uuid, body.fingerprintHash);
            }

            console.log(`[Auth] ${account.minecraft_username} logged in with password`);

            jsonResponse(res, 200, {
                success: true,
                uuid: account.minecraft_uuid,
                username: account.minecraft_username,
                sessionToken,
                isAdmin: ADMIN_UUIDS.has(account.minecraft_uuid)
            });
        } catch (e) {
            console.error('[Auth] Password verification error:', e.message);
            jsonResponse(res, 500, { error: 'Authentication failed' });
        }
    });
}

/**
 * Handle POST /api/auth/fingerprint — Device fingerprint auto-sign-in.
 */
function handleFingerprintAuth(req, res) {
    readJsonBody(req, (body) => {
        if (!body || !body.fingerprintHash) {
            return jsonResponse(res, 400, { error: 'Fingerprint hash is required' });
        }

        // Find account with this fingerprint
        let matchedAccount = null;
        if (db) {
            try {
                const rows = db.prepare('SELECT * FROM user_accounts WHERE auto_sign_in = 1').all();
                for (const row of rows) {
                    const fps = JSON.parse(row.device_fingerprints || '[]');
                    if (fps.includes(body.fingerprintHash)) {
                        matchedAccount = row;
                        break;
                    }
                }
            } catch (e) {
                console.error('[Auth] Fingerprint lookup error:', e.message);
            }
        } else {
            for (const [uuid, acc] of inMemoryUserAccounts) {
                if (!acc.auto_sign_in) continue;
                const fps = JSON.parse(acc.device_fingerprints || '[]');
                if (fps.includes(body.fingerprintHash)) {
                    matchedAccount = acc;
                    break;
                }
            }
        }

        if (!matchedAccount) {
            return jsonResponse(res, 401, { error: 'Device not recognized' });
        }

        // Create session
        const ip = clientIpFromReq(req);
        const sessionToken = createSession(matchedAccount.minecraft_uuid, body.fingerprintHash, ip);

        console.log(`[Auth] ${matchedAccount.minecraft_username} auto-signed-in via fingerprint`);

        jsonResponse(res, 200, {
            success: true,
            uuid: matchedAccount.minecraft_uuid,
            username: matchedAccount.minecraft_username,
            sessionToken
        });
    });
}

/**
 * Handle POST /api/auth/password/change — Change password.
 */
async function handlePasswordChange(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.sessionToken || !body.newPassword) {
            return jsonResponse(res, 400, { error: 'Session token and new password are required' });
        }

        if (!argon2) {
            return jsonResponse(res, 503, { error: 'Password authentication is not available on this server' });
        }

        // Validate session
        const session = validateSession(body.sessionToken);
        if (!session) {
            return jsonResponse(res, 401, { error: 'Invalid or expired session' });
        }

        const account = getUserAccount(session.minecraft_uuid);
        if (!account) {
            return jsonResponse(res, 404, { error: 'Account not found' });
        }

        // Verify current password (if provided — not required if using link code)
        if (body.currentPassword) {
            try {
                const valid = await argon2.verify(account.password_hash, body.currentPassword);
                if (!valid) {
                    return jsonResponse(res, 401, { error: 'Current password is incorrect' });
                }
            } catch (e) {
                return jsonResponse(res, 500, { error: 'Password verification failed' });
            }
        }

        // Validate new password
        if (body.newPassword.length < 8) {
            return jsonResponse(res, 400, { error: 'Password must be at least 8 characters' });
        }

        try {
            const newHash = await argon2.hash(body.newPassword, {
                type: argon2.argon2id,
                memoryCost: 65536,
                timeCost: 3,
                parallelism: 4,
                hashLength: 32
            });

            if (db) {
                try {
                    // Update password, clear fingerprints, revoke all sessions except current
                    db.prepare('UPDATE user_accounts SET password_hash = ?, device_fingerprints = ?, updated_at = ? WHERE minecraft_uuid = ?')
                        .run(newHash, '[]', Date.now(), session.minecraft_uuid);
                    // Revoke all sessions except current
                    const currentSessionId = crypto.createHash('sha256').update(body.sessionToken).digest('hex');
                    db.prepare('DELETE FROM user_sessions WHERE minecraft_uuid = ? AND session_id != ?')
                        .run(session.minecraft_uuid, currentSessionId);
                } catch (e) {
                    console.error('[Auth] Password change DB error:', e.message);
                    return jsonResponse(res, 500, { error: 'Failed to update password' });
                }
            } else {
                const acc = inMemoryUserAccounts.get(session.minecraft_uuid);
                if (acc) {
                    acc.password_hash = newHash;
                    acc.device_fingerprints = '[]';
                    acc.updated_at = Date.now();
                }
            }

            console.log(`[Auth] Password changed for ${account.minecraft_username}`);

            jsonResponse(res, 200, { success: true, message: 'Password changed successfully. All other sessions revoked.' });
        } catch (e) {
            console.error('[Auth] Password change error:', e.message);
            jsonResponse(res, 500, { error: 'Failed to change password' });
        }
    });
}

// ============================================================================
// Admin Auth Endpoint Handlers
// ============================================================================

/**
 * Handle POST /api/admin/auth — Admin login (username + password, validates UUID is in ADMIN_UUIDS).
 */
async function handleAdminAuth(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.username || !body.password) {
            return jsonResponse(res, 400, { error: 'Username and password required' });
        }
        if (!argon2) return jsonResponse(res, 503, { error: 'Not available' });

        // Look up account
        let account = null;
        if (db) {
            try { account = db.prepare('SELECT * FROM user_accounts WHERE LOWER(minecraft_username) = LOWER(?)').get(body.username); } catch (e) {}
        } else {
            for (const [, acc] of inMemoryUserAccounts) {
                if (acc.minecraft_username.toLowerCase() === body.username.toLowerCase()) { account = acc; break; }
            }
        }
        if (!account) return jsonResponse(res, 401, { error: 'Invalid credentials' });

        // Verify UUID is admin
        if (!ADMIN_UUIDS.has(account.minecraft_uuid)) {
            return jsonResponse(res, 403, { error: 'Access denied' });
        }

        // Verify password
        try {
            const valid = await argon2.verify(account.password_hash, body.password);
            if (!valid) return jsonResponse(res, 401, { error: 'Invalid credentials' });
        } catch (e) {
            return jsonResponse(res, 500, { error: 'Authentication error' });
        }

        // Check if 2FA is set up
        let adminAccount = null;
        if (db) {
            try { adminAccount = db.prepare('SELECT * FROM admin_accounts WHERE minecraft_uuid = ?').get(account.minecraft_uuid); } catch (e) {}
        } else {
            adminAccount = inMemoryAdminAccounts.get(account.minecraft_uuid);
        }

        const has2FA = adminAccount && adminAccount.totp_verified;
        const clientIp = req.headers['cf-connecting-ip'] || req.headers['x-forwarded-for'] || req.socket?.remoteAddress || '';
        const token = createAdminSession(account.minecraft_uuid, clientIp, !has2FA);

        // Update last login
        if (db) {
            try {
                const now = Date.now();
                db.prepare('INSERT INTO admin_accounts (minecraft_uuid, created_at, last_login) VALUES (?, ?, ?) ON CONFLICT(minecraft_uuid) DO UPDATE SET last_login = ?')
                    .run(account.minecraft_uuid, now, now, now);
            } catch (e) {}
        }

        jsonResponse(res, 200, {
            success: true,
            token,
            uuid: account.minecraft_uuid,
            username: account.minecraft_username,
            requires2FA: !!has2FA,
            has2FASetup: !!adminAccount?.totp_verified
        });
    });
}

/**
 * Handle POST /api/admin/auth/2fa — Verify TOTP code for admin session.
 */
async function handleAdmin2FA(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.token || !body.code) {
            return jsonResponse(res, 400, { error: 'Token and TOTP code required' });
        }

        const session = validateAdminSession(body.token, false);
        if (!session) return jsonResponse(res, 401, { error: 'Invalid or expired session' });

        let adminAccount = null;
        if (db) {
            try { adminAccount = db.prepare('SELECT * FROM admin_accounts WHERE minecraft_uuid = ?').get(session.minecraft_uuid); } catch (e) {}
        } else {
            adminAccount = inMemoryAdminAccounts.get(session.minecraft_uuid);
        }

        if (!adminAccount || !adminAccount.totp_secret) {
            return jsonResponse(res, 400, { error: '2FA not set up' });
        }

        if (!verifyTotp(adminAccount.totp_secret, body.code)) {
            return jsonResponse(res, 401, { error: 'Invalid verification code' });
        }

        upgradeAdminSession(body.token);
        jsonResponse(res, 200, { success: true });
    });
}

/**
 * Handle POST /api/admin/2fa/setup — Get TOTP setup QR data (requires admin session).
 */
async function handleAdmin2FASetup(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.token) return jsonResponse(res, 400, { error: 'Token required' });

        const session = validateAdminSession(body.token, false);
        if (!session) return jsonResponse(res, 401, { error: 'Invalid session' });

        // Generate new TOTP secret
        const secret = generateTotpSecret();

        // Store secret (not yet verified)
        if (db) {
            try {
                db.prepare('INSERT INTO admin_accounts (minecraft_uuid, totp_secret, totp_verified, created_at) VALUES (?, ?, 0, ?) ON CONFLICT(minecraft_uuid) DO UPDATE SET totp_secret = ?, totp_verified = 0')
                    .run(session.minecraft_uuid, secret, Date.now(), secret);
            } catch (e) { console.error('[Admin] 2FA setup error:', e.message); }
        } else {
            inMemoryAdminAccounts.set(session.minecraft_uuid, {
                ...(inMemoryAdminAccounts.get(session.minecraft_uuid) || {}),
                minecraft_uuid: session.minecraft_uuid,
                totp_secret: secret,
                totp_verified: 0,
                created_at: Date.now()
            });
        }

        // Return otpauth URL for QR code generation
        const otpauthUrl = `otpauth://totp/ModereX%20Admin?secret=${secret}&issuer=ModereX&digits=6&period=30`;
        jsonResponse(res, 200, { success: true, secret, otpauthUrl });
    });
}

/**
 * Handle POST /api/admin/2fa/verify — Confirm initial 2FA setup with verification code.
 */
async function handleAdmin2FAVerify(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.token || !body.code) {
            return jsonResponse(res, 400, { error: 'Token and code required' });
        }

        const session = validateAdminSession(body.token, false);
        if (!session) return jsonResponse(res, 401, { error: 'Invalid session' });

        let adminAccount = null;
        if (db) {
            try { adminAccount = db.prepare('SELECT * FROM admin_accounts WHERE minecraft_uuid = ?').get(session.minecraft_uuid); } catch (e) {}
        } else {
            adminAccount = inMemoryAdminAccounts.get(session.minecraft_uuid);
        }

        if (!adminAccount || !adminAccount.totp_secret) {
            return jsonResponse(res, 400, { error: '2FA not set up. Call /api/admin/2fa/setup first.' });
        }

        if (!verifyTotp(adminAccount.totp_secret, body.code)) {
            return jsonResponse(res, 401, { error: 'Invalid code. Please try again.' });
        }

        // Mark 2FA as verified
        if (db) {
            try { db.prepare('UPDATE admin_accounts SET totp_verified = 1 WHERE minecraft_uuid = ?').run(session.minecraft_uuid); } catch (e) {}
        } else {
            adminAccount.totp_verified = 1;
        }

        upgradeAdminSession(body.token);
        jsonResponse(res, 200, { success: true, message: '2FA enabled successfully' });
    });
}

/**
 * Handle POST /api/admin/session/validate — Validate admin session.
 */
async function handleAdminSessionValidate(req, res) {
    readJsonBody(req, async (body) => {
        if (!body || !body.token) return jsonResponse(res, 400, { error: 'Token required' });
        const session = validateAdminSession(body.token, true);
        if (session) {
            jsonResponse(res, 200, { valid: true, uuid: session.minecraft_uuid });
        } else {
            jsonResponse(res, 200, { valid: false });
        }
    });
}

/**
 * Store current metrics snapshot in database.
 */
function storeMetricsSnapshot() {
    if (!db) return;

    const now = Date.now();
    let totalPlayers = 0;
    mcServers.forEach(data => { totalPlayers += data.info?.players || 0; });

    // Calculate messages per second
    let totalMsgRate = 0;
    messageRates.forEach(rate => {
        const elapsed = (Date.now() - rate.windowStart) / 1000;
        if (elapsed > 0) totalMsgRate += rate.count / elapsed;
    });

    const cpuPercent = calculateCpuUsage();
    const mem = process.memoryUsage();
    const memPercent = Math.round((mem.rss / require('os').totalmem()) * 100);

    try {
        const stmt = db.prepare(`
            INSERT INTO gateway_metrics_history
            (timestamp, servers, browsers, admins, messages_per_sec, cpu_usage, memory_usage)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        `);
        stmt.run(now, mcServers.size, browserClients.size, adminClients.size,
                 Math.round(totalMsgRate), cpuPercent, memPercent);

        // Delete metrics older than 7 days to prevent bloat
        const weekAgo = now - (7 * 24 * 3600000);
        db.prepare('DELETE FROM gateway_metrics_history WHERE timestamp < ?').run(weekAgo);

        console.log(`[Metrics] Snapshot stored: ${mcServers.size} servers, ${browserClients.size} browsers, ${cpuPercent}% CPU`);
    } catch (e) {
        console.error('[Metrics] Failed to store snapshot:', e.message);
    }
}

// Store metrics snapshot every hour
setInterval(storeMetricsSnapshot, 3600000); // 1 hour
// Store initial snapshot after 1 minute
setTimeout(storeMetricsSnapshot, 60000);

// ============================================================================
// Cloudflare Tunnel Auto-Launch & Panel URL Updater
// ============================================================================

const PANEL_FILES_TO_UPDATE = [
    path.join(__dirname, 'panel', 'js', 'websocket.js'),
    path.join(__dirname, '..', 'app', 'src', 'main', 'resources', 'panel', 'js', 'websocket.js'),
    path.join(__dirname, '..', 'moderex-panel', 'js', 'websocket.js'),
    path.join(__dirname, '..', 'website', 'admin', 'js', 'admin.js'),
];

function updatePanelUrls(tunnelHost) {
    const wssPattern = /wss:\/\/[a-zA-Z0-9-]+\.trycloudflare\.com/g;
    const newWss = `wss://${tunnelHost}`;
    let updated = 0;

    for (const filePath of PANEL_FILES_TO_UPDATE) {
        try {
            if (!fs.existsSync(filePath)) continue;
            const content = fs.readFileSync(filePath, 'utf8');
            if (!wssPattern.test(content)) continue;
            // Reset regex lastIndex since we're reusing it
            wssPattern.lastIndex = 0;
            const newContent = content.replace(wssPattern, newWss);
            if (newContent !== content) {
                fs.writeFileSync(filePath, newContent, 'utf8');
                updated++;
                console.log(`[Tunnel] Updated URL in ${path.relative(path.join(__dirname, '..'), filePath)}`);
            }
        } catch (err) {
            console.error(`[Tunnel] Failed to update ${filePath}: ${err.message}`);
        }
    }
    if (updated > 0) {
        console.log(`[Tunnel] Updated ${updated} file(s) with new tunnel URL: ${tunnelHost}`);
    }
}

/**
 * Auto-deploy panel files to Cloudflare Pages after tunnel URL changes.
 * Deploys both the staff panel and admin/website projects.
 */
async function deployToCloudflarePages(tunnelHost) {
    // Check if we already deployed this URL
    if (db) {
        try {
            const row = db.prepare('SELECT value FROM gateway_config WHERE key = ?').get('last_deployed_tunnel');
            if (row && row.value === tunnelHost) {
                console.log('[Deploy] Tunnel URL unchanged, skipping deployment');
                return;
            }
        } catch (e) { /* table may not exist yet */ }
    }

    const deployments = [
        { name: 'panel-moderex', dir: path.join(__dirname, '..', 'moderex-panel') },
        { name: 'moderex', dir: path.join(__dirname, '..', 'website') },
    ];

    for (const { name, dir } of deployments) {
        if (!fs.existsSync(dir)) {
            console.log(`[Deploy] Skipping ${name}: directory not found (${dir})`);
            continue;
        }

        try {
            console.log(`[Deploy] Deploying ${name} to Cloudflare Pages...`);
            const result = await new Promise((resolve, reject) => {
                const proc = spawn('npx', ['wrangler', 'pages', 'deploy', dir, '--project-name=' + name, '--commit-dirty=true'], {
                    stdio: ['ignore', 'pipe', 'pipe'],
                    cwd: __dirname
                });

                let stdout = '';
                let stderr = '';
                proc.stdout.on('data', (d) => { stdout += d.toString(); });
                proc.stderr.on('data', (d) => { stderr += d.toString(); });

                proc.on('close', (code) => {
                    if (code === 0) resolve(stdout);
                    else reject(new Error(`wrangler exited with code ${code}: ${stderr}`));
                });

                proc.on('error', (err) => {
                    if (err.code === 'ENOENT') {
                        reject(new Error('npx/wrangler not found. Install with: npm install -g wrangler'));
                    } else {
                        reject(err);
                    }
                });

                // Timeout after 60 seconds
                setTimeout(() => reject(new Error('Deployment timed out')), 60000);
            });

            // Extract deployment URL from output
            const urlMatch = result.match(/https:\/\/[a-f0-9]+\.[a-zA-Z0-9-]+\.pages\.dev/);
            console.log(`[Deploy] ${name} deployed successfully${urlMatch ? ': ' + urlMatch[0] : ''}`);
        } catch (err) {
            if (err.message.includes('not found')) {
                console.log(`[Deploy] Skipping ${name}: Cloudflare Wrangler not installed (optional feature)`);
            } else {
                console.warn(`[Deploy] Failed to deploy ${name}: ${err.message}`);
            }
        }
    }

    // Store last deployed URL
    if (db) {
        try {
            db.prepare('CREATE TABLE IF NOT EXISTS gateway_config (key TEXT PRIMARY KEY, value TEXT)').run();
            db.prepare('INSERT OR REPLACE INTO gateway_config (key, value) VALUES (?, ?)').run('last_deployed_tunnel', tunnelHost);
        } catch (e) {
            console.warn('[Deploy] Failed to store deployment state:', e.message);
        }
    }
}

function startCloudflaredTunnel() {
    return new Promise((resolve) => {
        const port = CONFIG.port;
        console.log('[Tunnel] Starting cloudflared tunnel...');

        const cf = spawn('cloudflared', ['tunnel', '--url', `http://localhost:${port}`], {
            stdio: ['ignore', 'pipe', 'pipe']
        });

        let resolved = false;
        const urlRegex = /https?:\/\/([a-zA-Z0-9-]+\.trycloudflare\.com)/;

        function handleOutput(data) {
            const text = data.toString();
            // cloudflared prints the URL to stderr
            const match = text.match(urlRegex);
            if (match && !resolved) {
                resolved = true;
                const tunnelHost = match[1];
                currentTunnelHost = tunnelHost;
                console.log(`[Tunnel] Tunnel established: https://${tunnelHost}`);
                updatePanelUrls(tunnelHost);
                // Auto-deploy to Cloudflare Pages (non-blocking)
                deployToCloudflarePages(tunnelHost).catch(err => {
                    console.warn('[Deploy] Auto-deploy failed:', err.message);
                });
                resolve(tunnelHost);
            }
        }

        cf.stdout.on('data', handleOutput);
        cf.stderr.on('data', handleOutput);

        cf.on('error', (err) => {
            console.error(`[Tunnel] Failed to start cloudflared: ${err.message}`);
            console.error('[Tunnel] Make sure cloudflared is installed: https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads/');
            if (!resolved) {
                resolved = true;
                resolve(null);
            }
        });

        cf.on('exit', (code) => {
            if (code !== null && code !== 0) {
                console.error(`[Tunnel] cloudflared exited with code ${code}`);
            }
            if (!resolved) {
                resolved = true;
                resolve(null);
            }
        });

        // Timeout after 30 seconds
        setTimeout(() => {
            if (!resolved) {
                console.error('[Tunnel] Timed out waiting for tunnel URL (30s)');
                resolved = true;
                resolve(null);
            }
        }, 30000);
    });
}

// ============================================================================
// Dev License Management
// ============================================================================

function sendLicensesList(ws) {
    if (!db) {
        ws.send(JSON.stringify({
            type: 'licenses_list',
            licenses: []
        }));
        return;
    }

    try {
        const licenses = db.prepare('SELECT * FROM license_builds ORDER BY created_at DESC').all();

        // Map snake_case to camelCase for frontend
        const mappedLicenses = licenses.map(row => ({
            token: row.token,
            testerName: row.tester_name,
            buildVersion: row.build_version,
            buildTimestamp: row.build_timestamp,
            jarFilename: row.jar_filename,
            createdBy: row.created_by,
            createdAt: row.created_at,
            active: row.active,
            expiresAt: row.expires_at || null,
            lastHeartbeat: row.last_heartbeat || null,
            note: row.note || null
        }));

        ws.send(JSON.stringify({
            type: 'licenses_list',
            licenses: mappedLicenses
        }));
    } catch (e) {
        console.error('[Licenses] Failed to fetch licenses:', e);
        ws.send(JSON.stringify({
            type: 'licenses_list',
            licenses: []
        }));
    }
}

async function createDevLicense(ws, email, data) {
    const { testerName, maxServers = 1, expiresAt, note, createdBy } = data;

    if (!testerName) {
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Tester name is required'
        }));
        return;
    }

    // Generate UUID v4 token
    const token = crypto.randomUUID();
    const now = Date.now();

    // Create license in Cloudflare Workers KV
    try {
        const response = await fetch('https://license.moderex.net/admin/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Admin-Secret': CLOUDFLARE_ADMIN_SECRET
            },
            body: JSON.stringify({
                token,
                maxServers,
                expiresAt,
                note,
                createdBy: createdBy || email
            })
        });

        if (!response.ok) {
            const error = await response.text();
            console.error('[Licenses] Cloudflare API error:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: `Failed to create license: ${error}`
            }));
            return;
        }

        console.log('[Licenses] License created in Cloudflare KV:', token.substring(0, 8) + '...');
    } catch (e) {
        console.error('[Licenses] Failed to create license in Cloudflare:', e.message);
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Failed to create license. Is Cloudflare Worker deployed?'
        }));
        return;
    }

    // Store in local gateway database for tracking
    if (db) {
        try {
            db.prepare(`
                INSERT INTO license_builds (token, tester_name, build_version, build_timestamp, jar_filename, created_by, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            `).run(token, testerName, 'pending', now, null, createdBy || email, now);
        } catch (e) {
            console.error('[Licenses] Database error:', e);
        }
    }

    ws.send(JSON.stringify({
        type: 'license_created',
        token,
        testerName
    }));

    logAudit(email, `Generated dev license for ${testerName}`);
}

async function revokeDevLicense(ws, email, data) {
    const { token } = data;

    if (!token) {
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Token is required'
        }));
        return;
    }

    // Revoke in Cloudflare Workers KV (non-blocking - local DB is source of truth)
    try {
        const response = await fetch('https://license.moderex.net/admin/revoke', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Admin-Secret': CLOUDFLARE_ADMIN_SECRET
            },
            body: JSON.stringify({ token })
        });

        if (!response.ok) {
            const error = await response.text();
            console.error('[Licenses] Cloudflare API error (non-fatal):', error);
            // Continue anyway - local DB is source of truth
        } else {
            console.log('[Licenses] License revoked in Cloudflare KV:', token.substring(0, 8) + '...');
        }
    } catch (e) {
        console.error('[Licenses] Failed to revoke license in Cloudflare (non-fatal):', e.message);
    }

    // Update local gateway database
    if (db) {
        try {
            db.prepare('UPDATE license_builds SET active = 0 WHERE token = ?').run(token);
        } catch (e) {
            console.error('[Licenses] Database error:', e);
        }
    }

    ws.send(JSON.stringify({
        type: 'license_revoked',
        token
    }));

    logAudit(email, `Revoked dev license ${token.substring(0, 8)}...`);
}

function buildLicensedJar(ws, email, data) {
    const { token, testerName } = data;

    if (!token) {
        ws.send(JSON.stringify({
            type: 'jar_build_error',
            error: 'Token is required'
        }));
        return;
    }

    ws.send(JSON.stringify({
        type: 'jar_build_started',
        token
    }));

    // Execute build script
    const { spawn } = require('child_process');
    const scriptPath = path.join(__dirname, 'scripts', 'build-licensed.js');

    const buildProcess = spawn('node', [scriptPath, token, testerName || 'Unknown'], {
        cwd: __dirname
    });

    let output = '';
    buildProcess.stdout.on('data', (data) => {
        const text = data.toString();
        output += text;
        console.log(`[Build] ${text.trim()}`);

        // Parse progress messages
        const progressRegex = /__PROGRESS__(.+?)__PROGRESS__/g;
        let match;
        while ((match = progressRegex.exec(text)) !== null) {
            try {
                const progressData = JSON.parse(match[1]);
                ws.send(JSON.stringify({
                    type: 'jar_build_progress',
                    data: progressData
                }));
            } catch (e) {
                console.error('[Build] Failed to parse progress:', e);
            }
        }

        // Send raw output lines to console (strip progress markers)
        const cleanText = text.replace(/__PROGRESS__.*?__PROGRESS__/g, '').trim();
        if (cleanText) {
            cleanText.split('\n').forEach(line => {
                const trimmed = line.trim();
                if (!trimmed) return;
                ws.send(JSON.stringify({
                    type: 'jar_build_output',
                    data: { line: trimmed, level: 'info' }
                }));
            });
        }
    });

    buildProcess.stderr.on('data', (data) => {
        const text = data.toString().trim();
        console.error(`[Build Error] ${text}`);
        if (text) {
            text.split('\n').forEach(line => {
                const trimmed = line.trim();
                if (!trimmed) return;
                ws.send(JSON.stringify({
                    type: 'jar_build_output',
                    data: { line: trimmed, level: 'warn' }
                }));
            });
        }
    });

    buildProcess.on('close', (code) => {
        if (code === 0) {
            const shortToken = token.substring(0, 8);
            const filename = `ModereX-licensed-${shortToken}.jar`;

            // Build download URL using tunnel host (bypasses CF Access)
            const downloadHost = currentTunnelHost || 'localhost:3000';
            const downloadUrl = `https://${downloadHost}/download/${filename}`;

            ws.send(JSON.stringify({
                type: 'jar_build_complete',
                token,
                filename,
                downloadUrl,
                path: `gateway/licensed-builds/${filename}`
            }));

            logAudit(email, `Built licensed JAR for ${testerName || 'Unknown'} (${shortToken}...)`);
        } else {
            ws.send(JSON.stringify({
                type: 'jar_build_error',
                error: `Build failed with exit code ${code}`
            }));
        }
    });
}

// ============================================================================
// Server Suspension
// ============================================================================

function suspendServer(ws, email, data) {
    const { serverId, reason } = data;

    if (!serverId) {
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Server ID is required'
        }));
        return;
    }

    if (db) {
        try {
            db.prepare(`
                INSERT OR REPLACE INTO suspended_servers (server_id, suspended_at, suspended_by, reason)
                VALUES (?, ?, ?, ?)
            `).run(serverId.toLowerCase(), Date.now(), email, reason || 'No reason provided');
        } catch (e) {
            console.error('[Suspension] Database error:', e);
        }
    }

    // Kick existing server connection
    const server = mcServers.get(serverId.toLowerCase());
    if (server) {
        server.ws.send(JSON.stringify({
            type: 'SUSPENDED',
            reason: reason || 'Server suspended by administrator'
        }));
        server.ws.close(1008, 'Server suspended');
        mcServers.delete(serverId.toLowerCase());
    }

    ws.send(JSON.stringify({
        type: 'server_suspended',
        serverId
    }));

    logAudit(email, `Suspended server ${serverId}`);
}

function unsuspendServer(ws, email, data) {
    const { serverId } = data;

    if (!serverId) {
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Server ID is required'
        }));
        return;
    }

    if (db) {
        try {
            db.prepare('DELETE FROM suspended_servers WHERE server_id = ?').run(serverId.toLowerCase());
        } catch (e) {
            console.error('[Suspension] Database error:', e);
        }
    }

    ws.send(JSON.stringify({
        type: 'server_unsuspended',
        serverId
    }));

    logAudit(email, `Unsuspended server ${serverId}`);
}

function sendSuspendedServers(ws) {
    if (!db) {
        ws.send(JSON.stringify({
            type: 'suspended_servers',
            servers: []
        }));
        return;
    }

    try {
        const suspended = db.prepare('SELECT * FROM suspended_servers ORDER BY suspended_at DESC').all();
        ws.send(JSON.stringify({
            type: 'suspended_servers',
            servers: suspended
        }));
    } catch (e) {
        console.error('[Suspension] Failed to fetch suspended servers:', e);
        ws.send(JSON.stringify({
            type: 'suspended_servers',
            servers: []
        }));
    }
}

function isServerSuspended(serverId) {
    if (!db) return false;

    try {
        const result = db.prepare('SELECT 1 FROM suspended_servers WHERE server_id = ?').get(serverId.toLowerCase());
        return !!result;
    } catch (e) {
        return false;
    }
}

// Start gateway (async to support sql.js initialization)
async function startGateway() {
    await initDatabase();

    server.listen(CONFIG.port, async () => {
        const dbStatus = db ? 'SQLite' : 'In-Memory';
        console.log('');
        console.log('+---------------------------------------------------------------+');
        console.log('|              ModereX Gateway Server                            |');
        console.log('+---------------------------------------------------------------+');
        console.log(`|  Status:    Running                                            |`);
        console.log(`|  Port:      ${String(CONFIG.port).padEnd(48)}|`);
        console.log(`|  Database:  ${dbStatus.padEnd(48)}|`);
        console.log(`|  Health:    http://localhost:${CONFIG.port}/health${' '.repeat(Math.max(0, 24 - String(CONFIG.port).length))}|`);
        console.log('+---------------------------------------------------------------+');
        console.log(`|  Server ID Format:  XXXXX-XXXXX-XXXXX-XXXXX-XXXXX              |`);
        console.log(`|  URL Format:        panel.moderex.net/{prefix}/               |`);
        console.log('+---------------------------------------------------------------+');
        console.log('|  MC Server WebSocket:  ws://localhost:' + CONFIG.port + '/server               |');
        console.log('|  Panel WebSocket:      ws://localhost:' + CONFIG.port + '/panel/{prefix}      |');
        console.log('|  Global Panel WS:      ws://localhost:' + CONFIG.port + '/panel/               |');
        console.log('|  Admin WebSocket:      ws://localhost:' + CONFIG.port + '/admin                |');
        console.log('+---------------------------------------------------------------+');
        console.log('');

        // Auto-launch cloudflared tunnel unless GATEWAY_URL env var is set
        if (process.env.GATEWAY_URL) {
            const host = process.env.GATEWAY_URL.replace(/^https?:\/\//, '').replace(/\/$/, '');
            console.log(`[Tunnel] Using GATEWAY_URL from env: ${host}`);
            updatePanelUrls(host);
            // Auto-deploy for env-based URL too
            if (host.endsWith('.trycloudflare.com')) {
                deployToCloudflarePages(host).catch(err => {
                    console.warn('[Deploy] Auto-deploy failed:', err.message);
                });
            }
        } else {
            const tunnelHost = await startCloudflaredTunnel();
            if (!tunnelHost) {
                console.warn('[Tunnel] No tunnel URL available. Panel files not updated.');
                console.warn('[Tunnel] Set GATEWAY_URL env var or install cloudflared.');
            }
        }
    });
}

startGateway().catch(err => {
    console.error('[Gateway] Failed to start:', err);
    process.exit(1);
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('[Gateway] Shutting down...');

    // Close all connections
    mcServers.forEach((server, id) => {
        server.ws.close(1001, 'Gateway shutting down');
    });
    browserClients.forEach((client, id) => {
        client.ws.close(1001, 'Gateway shutting down');
    });
    globalPanelClients.forEach((client, id) => {
        client.ws.close(1001, 'Gateway shutting down');
    });
    adminClients.forEach((client, id) => {
        client.ws.close(1001, 'Gateway shutting down');
    });

    // Close database
    if (db) {
        db.close();
    }

    server.close(() => {
        console.log('[Gateway] Goodbye!');
        process.exit(0);
    });
});

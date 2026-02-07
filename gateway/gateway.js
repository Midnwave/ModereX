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

// Configuration
const CONFIG = {
    port: process.env.PORT || 3000,
    heartbeatInterval: 30000,  // 30 seconds
    serverTimeout: 60000,      // 60 seconds without heartbeat = dead
    adminEmails: ['@blockforge.studio'], // Cloudflare Access allowed email domains
};

// Message types that browsers are NOT allowed to send (server-internal only)
const BLOCKED_BROWSER_TYPES = new Set([
    'register', 'heartbeat', 'panel_response', 'broadcast',
    'permission_sync', 'permission_update', 'token_register', 'token_revoke',
    'settings_sync', 'server_unregister', 'global_pre_auth_result',
    'browser_connected', 'browser_disconnected'
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

// In-memory fallback for global tokens
const inMemoryGlobalTokens = new Map();

// In-memory fallback for server access
const inMemoryServerAccess = new Map(); // key: `${uuid}:${serverId}`

// In-memory fallback for user settings
const inMemoryUserSettings = new Map();

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

    // User settings synced across servers (color scheme + device fingerprints)
    db.exec(`
        CREATE TABLE IF NOT EXISTS user_settings (
            uuid TEXT PRIMARY KEY,
            color_scheme TEXT DEFAULT 'blue',
            device_fingerprints TEXT,
            updated_at INTEGER NOT NULL
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
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

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

    // API: List connected servers (admin only — requires ADMIN_DEV_KEY or CF Access)
    if (url.pathname === '/api/servers') {
        const adminKey = process.env.ADMIN_DEV_KEY;
        const authHeader = req.headers.authorization;
        const cfEmail = req.headers['cf-access-authenticated-user-email'];
        const isAuthed = (adminKey && authHeader === `Bearer ${adminKey}`) || cfEmail;

        if (!isAuthed) {
            res.writeHead(401, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ error: 'Authentication required' }));
            return;
        }

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
const wss = new WebSocketServer({ server, maxPayload: 2 * 1024 * 1024 }); // 2MB max message size

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
            'https://panel-moderex.pages.dev',
            'https://panel.moderex.net',
            'https://moderex.pages.dev',
            'https://moderex.net'
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
    const { uuid, colorScheme, deviceFingerprints } = data;
    if (!uuid) return;

    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO user_settings (uuid, color_scheme, device_fingerprints, updated_at) VALUES (?, ?, ?, ?)'
            ).run(uuid, colorScheme || 'blue', JSON.stringify(deviceFingerprints || []), Date.now());
        } catch (e) {
            console.error('[Token] Settings sync DB error:', e.message);
        }
    } else {
        inMemoryUserSettings.set(uuid, {
            uuid, color_scheme: colorScheme || 'blue',
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
                servers.push({
                    serverId: row.server_id,
                    serverName: mcServer?.info?.serverName || 'Unknown Server',
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
                servers.push({
                    serverId: access.server_id,
                    serverName: mcServer?.info?.serverName || 'Unknown Server',
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
                deviceFingerprints: JSON.parse(settings.device_fingerprints || '[]')
            };
        }
    }
    return { colorScheme: 'blue', deviceFingerprints: [] };
}

/**
 * Save user settings to gateway DB.
 */
function saveUserSettings(uuid, colorScheme, deviceFingerprints) {
    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO user_settings (uuid, color_scheme, device_fingerprints, updated_at) VALUES (?, ?, ?, ?)'
            ).run(uuid, colorScheme || 'blue', JSON.stringify(deviceFingerprints || []), Date.now());
        } catch (e) {
            console.error('[Token] Save settings DB error:', e.message);
        }
    } else {
        inMemoryUserSettings.set(uuid, {
            uuid, color_scheme: colorScheme || 'blue',
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

    const client = globalPanelClients.get(clientId);
    if (!client || client.ws.readyState !== WebSocket.OPEN) return;

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
            clientIp: client.clientIp
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

    ws.on('message', (data) => {
        try {
            if (isMessageRateLimited(ws)) {
                ws.send(JSON.stringify({ type: 'error', code: 'RATE_LIMITED', message: 'Too many messages. Slow down.' }));
                return;
            }

            const message = JSON.parse(data.toString());

            switch (message.type) {
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
                    saveUserSettings(authedUuid, message.colorScheme, message.deviceFingerprints);
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

                default:
                    ws.send(JSON.stringify({ type: 'error', code: 'UNKNOWN_TYPE', message: `Unknown message type: ${message.type}` }));
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

    // Verify authentication
    // Production: Cloudflare Access sets cf-access-authenticated-user-email header
    // Development: Requires ADMIN_DEV_KEY env var + matching x-admin-dev-key header
    const devKey = process.env.ADMIN_DEV_KEY;
    const isDev = devKey && req.headers['x-admin-dev-key'] === devKey;

    if (isDev) {
        cfEmail = 'dev@localhost';
    } else if (!cfEmail) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'UNAUTHORIZED',
            message: 'Authentication required. Set ADMIN_DEV_KEY env var for development access.'
        }));
        ws.close(4003, 'Unauthorized');
        return;
    }

    // Check email domain for authorization (skip for dev)
    // Use exact domain match after @ to prevent suffix attacks (e.g. attacker@evil.blockforge.studio)
    const emailDomain = cfEmail.includes('@') ? cfEmail.split('@')[1] : '';
    if (!isDev && !CONFIG.adminEmails.some(domain => emailDomain === domain || cfEmail === domain)) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'FORBIDDEN',
            message: 'Not authorized to access admin panel'
        }));
        ws.close(4003, 'Forbidden');
        logAudit(cfEmail || 'unknown', 'unauthorized_access_attempt', { ip: clientIp });
        return;
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
        type: 'admin_announcement',
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
    const health = {
        status: 'ok',
        uptime: process.uptime(),
        connections: mcServers.size,
        browsers: browserClients.size,
        admins: adminClients.size,
        memory: process.memoryUsage(),
        timestamp: Date.now()
    };

    ws.send(JSON.stringify({
        type: 'gateway_health',
        data: health
    }));
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

    // Get recent activity from audit log
    let recentActivity = [];
    if (db) {
        try {
            recentActivity = db.prepare('SELECT * FROM admin_audit_log ORDER BY timestamp DESC LIMIT 10').all();
        } catch (e) {}
    }

    ws.send(JSON.stringify({
        type: 'dashboard_data',
        data: {
            servers: mcServers.size,
            players: totalPlayers,
            browsers: browserClients.size,
            announcements: activeAnnouncementCount,
            uptime: process.uptime(),
            activity: recentActivity
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

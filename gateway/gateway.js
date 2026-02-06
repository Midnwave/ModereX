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

// Configuration
const CONFIG = {
    port: process.env.PORT || 3000,
    heartbeatInterval: 30000,  // 30 seconds
    serverTimeout: 60000,      // 60 seconds without heartbeat = dead
    adminEmails: ['@blockforge.studio'], // Cloudflare Access allowed email domains
};

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
            first_registered_at INTEGER NOT NULL,
            last_seen_at INTEGER NOT NULL
        )
    `);

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
 * On first registration: stores the SHA-256 hash of the secret.
 * On subsequent registrations: verifies the secret matches the stored hash.
 * Returns true if valid, false if mismatch.
 */
function validateServerSecret(serverId, secret) {
    if (!secret || secret.length === 0) {
        // No secret provided - reject if we already have one stored
        const existingHash = getStoredSecretHash(serverId);
        if (existingHash) {
            return false; // Server previously registered with a secret
        }
        // First registration without secret - allow but warn
        console.log(`[Server] WARNING: ${serverId} registered without a gateway secret`);
        return true;
    }

    const secretHash = crypto.createHash('sha256').update(secret).digest('hex');
    const existingHash = getStoredSecretHash(serverId);

    if (!existingHash) {
        // First registration - store the secret hash
        storeSecretHash(serverId, secretHash);
        console.log(`[Server] ${serverId} first registration - secret hash stored`);
        return true;
    }

    // Subsequent registration - verify secret matches
    if (existingHash === secretHash) {
        updateSecretLastSeen(serverId);
        return true;
    }

    return false; // Secret mismatch
}

/**
 * Get stored secret hash for a server ID.
 */
function getStoredSecretHash(serverId) {
    if (db) {
        try {
            const row = db.prepare('SELECT secret_hash FROM server_secrets WHERE server_id = ?').get(serverId);
            return row ? row.secret_hash : null;
        } catch (e) {
            console.error('[Server] Failed to query server secret:', e.message);
        }
    }
    return inMemoryServerSecrets.get(serverId) || null;
}

/**
 * Store a new secret hash for a server ID.
 */
function storeSecretHash(serverId, secretHash) {
    const now = Date.now();
    if (db) {
        try {
            db.prepare(
                'INSERT OR REPLACE INTO server_secrets (server_id, secret_hash, first_registered_at, last_seen_at) VALUES (?, ?, ?, ?)'
            ).run(serverId, secretHash, now, now);
        } catch (e) {
            console.error('[Server] Failed to store server secret:', e.message);
            inMemoryServerSecrets.set(serverId, secretHash);
        }
    } else {
        inMemoryServerSecrets.set(serverId, secretHash);
    }
}

/**
 * Update the last_seen_at timestamp for a server.
 */
function updateSecretLastSeen(serverId) {
    if (db) {
        try {
            db.prepare('UPDATE server_secrets SET last_seen_at = ? WHERE server_id = ?').run(Date.now(), serverId);
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

    // First, check if this exact prefix is registered
    if (urlPrefixRegistry.has(lowerPrefix)) {
        return urlPrefixRegistry.get(lowerPrefix);
    }

    // Otherwise, look for servers that start with this prefix
    for (const [fullId, server] of mcServers.entries()) {
        if (fullId.startsWith(lowerPrefix) || fullId.replace(/-/g, '').startsWith(lowerPrefix.replace(/-/g, ''))) {
            return fullId;
        }
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

    // API: List connected servers (for debugging/landing page stats)
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
    // Supports both new format (xxxxx-xxxxx) and legacy 8-char format
    const serverCheckMatch = url.pathname.match(/^\/api\/server\/([A-Za-z0-9-]+)$/);
    if (serverCheckMatch) {
        const query = serverCheckMatch[1].toLowerCase();
        const serverId = findServerByPrefix(query);
        const exists = serverId && mcServers.has(serverId);

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            exists: exists,
            serverId: exists ? serverId : null,
            serverName: exists ? mcServers.get(serverId).info?.serverName : null,
            urlPrefix: exists ? mcServers.get(serverId).urlPrefix : null
        }));
        return;
    }

    // API: Stats for landing page
    if (url.pathname === '/api/stats') {
        let totalPlayers = 0;
        mcServers.forEach((data) => {
            totalPlayers += data.info?.players || 0;
        });

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            servers: mcServers.size,
            players: totalPlayers,
            uptime: process.uptime()
        }));
        return;
    }

    // Serve panel files if they exist locally (for development)
    const panelPath = path.join(__dirname, 'panel', url.pathname === '/' ? 'index.html' : url.pathname);
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
const wss = new WebSocketServer({ server });

// Handle WebSocket connections
wss.on('connection', (ws, req) => {
    const url = new URL(req.url, `http://${req.headers.host}`);
    const clientIp = req.headers['cf-connecting-ip'] || req.headers['x-forwarded-for'] || req.socket.remoteAddress;

    console.log(`[WS] New connection from ${clientIp} - ${url.pathname}`);

    // Determine connection type from URL path
    // /server - MC server connection
    // /panel/{prefix} - Browser panel connection (prefix can be partial or full)

    const isServerConnection = url.pathname === '/server' || url.searchParams.get('type') === 'server';
    const isAdminConnection = url.pathname === '/admin';

    // Rate limit browser connections per IP (server and admin connections exempt)
    if (!isServerConnection && !isAdminConnection) {
        const ipConns = ipConnectionCounts.get(clientIp) || new Set();
        // Clean up closed connections from the set
        for (const existingWs of ipConns) {
            if (existingWs.readyState !== WebSocket.OPEN) {
                ipConns.delete(existingWs);
            }
        }
        if (ipConns.size >= MAX_CONNECTIONS_PER_IP) {
            console.log(`[RateLimit] ${clientIp} exceeded max connections (${ipConns.size}/${MAX_CONNECTIONS_PER_IP})`);
            ws.close(4029, 'Too many connections from this IP');
            return;
        }
        ipConns.add(ws);
        ipConnectionCounts.set(clientIp, ipConns);

        // Clean up on disconnect
        ws.on('close', () => {
            const conns = ipConnectionCounts.get(clientIp);
            if (conns) {
                conns.delete(ws);
                if (conns.size === 0) {
                    ipConnectionCounts.delete(clientIp);
                }
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
                const secretValid = validateServerSecret(serverId, secret);
                if (!secretValid) {
                    console.log(`[Server] ${serverId} rejected: invalid gateway secret`);
                    ws.send(JSON.stringify({
                        type: 'error',
                        code: 'INVALID_SECRET',
                        message: 'Gateway authentication failed. Server secret mismatch. If this server was previously registered with a different secret, contact the gateway administrator.'
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

            // Forward to MC server
            const server = mcServers.get(serverId);
            if (server && server.ws.readyState === WebSocket.OPEN) {
                // Add client ID so server knows who to respond to
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
                    // Authenticate with a global token
                    const result = validateGlobalToken(message.token || '');
                    if (result.valid) {
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
                        ws.send(JSON.stringify({
                            type: 'global_auth_result',
                            success: false,
                            error: result.reason || 'Invalid token'
                        }));
                    }
                    break;
                }

                case 'global_device_auth': {
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

                    // Store pending switch info
                    const client = globalPanelClients.get(clientId);
                    if (client) client.pendingSwitchServerId = targetServerId;

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

    // Verify Cloudflare Access authentication
    // In production, cfEmail is set by Cloudflare Access
    // For development, allow if no CF headers (direct connection)
    const isDev = !req.headers['cf-access-authenticated-user-email'] && !req.headers['cf-access-jwt-assertion'];

    if (!isDev && !cfEmail) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'UNAUTHORIZED',
            message: 'Cloudflare Access authentication required'
        }));
        ws.close(4003, 'Unauthorized');
        return;
    }

    // Check email domain for authorization
    if (cfEmail && !CONFIG.adminEmails.some(domain => cfEmail.endsWith(domain))) {
        ws.send(JSON.stringify({
            type: 'error',
            code: 'FORBIDDEN',
            message: 'Not authorized to access admin panel'
        }));
        ws.close(4003, 'Forbidden');
        logAudit(cfEmail || 'unknown', 'unauthorized_access_attempt', { ip: clientIp });
        return;
    }

    const email = cfEmail || 'dev@localhost';

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

// Start gateway (async to support sql.js initialization)
async function startGateway() {
    await initDatabase();

    server.listen(CONFIG.port, () => {
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

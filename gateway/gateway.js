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
// Admin Database Setup (SQLite)
// ============================================================================
let db;
try {
    const Database = require('better-sqlite3');
    db = new Database(path.join(__dirname, 'gateway.db'));

    // Create announcements table
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

    // Create audit log table
    db.exec(`
        CREATE TABLE IF NOT EXISTS admin_audit_log (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            admin_email TEXT NOT NULL,
            action TEXT NOT NULL,
            details TEXT,
            timestamp INTEGER NOT NULL
        )
    `);

    console.log('[Admin] Database initialized');
} catch (e) {
    console.log('[Admin] SQLite not available, using in-memory storage');
    db = null;
}

// In-memory fallback for announcements if SQLite not available
const inMemoryAnnouncements = new Map();

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

    if (url.pathname === '/server') {
        handleMCServerConnection(ws, clientIp);
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
    } else if (url.pathname === '/admin') {
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
    return 'client_' + Math.random().toString(36).substring(2, 15);
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
        case 'get_announcements':
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

        case 'broadcast_announcement':
            broadcastAnnouncementToAll(admin.ws, email, data?.id);
            break;

        case 'get_gateway_health':
            sendGatewayHealth(admin.ws);
            break;

        case 'get_servers':
            sendServerList(admin.ws);
            break;

        case 'get_audit_log':
            sendAuditLog(admin.ws, data?.limit || 100);
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
        type: 'announcements',
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
        type: 'servers',
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
        logs: logs
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

// Start server
server.listen(CONFIG.port, () => {
    console.log('');
    console.log('╔═══════════════════════════════════════════════════════════════╗');
    console.log('║              ModereX Gateway Server                            ║');
    console.log('╠═══════════════════════════════════════════════════════════════╣');
    console.log(`║  Status:    Running                                            ║`);
    console.log(`║  Port:      ${String(CONFIG.port).padEnd(48)}║`);
    console.log(`║  Health:    http://localhost:${CONFIG.port}/health${' '.repeat(Math.max(0, 24 - String(CONFIG.port).length))}║`);
    console.log('╠═══════════════════════════════════════════════════════════════╣');
    console.log(`║  Server ID Format:  XXXXX-XXXXX-XXXXX-XXXXX-XXXXX              ║`);
    console.log(`║  URL Format:        panel.moderex.net/{prefix}/               ║`);
    console.log('╠═══════════════════════════════════════════════════════════════╣');
    console.log('║  MC Server WebSocket:  ws://localhost:' + CONFIG.port + '/server               ║');
    console.log('║  Panel WebSocket:      ws://localhost:' + CONFIG.port + '/panel/{prefix}      ║');
    console.log('║  Admin WebSocket:      ws://localhost:' + CONFIG.port + '/admin                ║');
    console.log('╚═══════════════════════════════════════════════════════════════╝');
    console.log('');
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

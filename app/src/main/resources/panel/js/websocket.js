/* ============================================
   ModereX Control Panel - WebSocket Manager
   Supports both direct and gateway modes
   ============================================ */
(function() {
  'use strict';

  const WS_RECONNECT_DELAY_MIN = 2000;
  const WS_RECONNECT_DELAY_MAX = 30000;
  const WS_HEARTBEAT_INTERVAL = 30000; // Keep connection alive (30s)
  const WS_PING_INTERVAL = 15000; // Measure latency every 15s
  const WS_PING_TIMEOUT = 45000; // Allow 45s for very high-ping/unstable connections
  const WS_MAX_RECONNECT_ATTEMPTS = 100; // Maximum auto-reconnect attempts before giving up

  // Gateway configuration - domains that indicate we're using gateway mode
  const GATEWAY_DOMAINS = [
    'panel.moderex.net',       // Future custom domain
    'panel-moderex.pages.dev', // Cloudflare Pages subdomain (current)
    'trycloudflare.com'        // Quick Tunnel domain (temporary testing)
  ];
  // Gateway WebSocket URL - update this after deploying your gateway with Cloudflare Tunnel
  const GATEWAY_WS_URL = 'wss://data-kid-satisfied-bars.trycloudflare.com/panel';

  let ws = null;
  let heartbeatTimer = null;
  let reconnectTimer = null;
  let pingTimer = null;
  let pingTimeoutTimer = null;
  let isConnected = false;
  let sessionData = null;
  let currentHost = null;
  let currentPort = null;
  let currentServerId = null; // Server ID when in gateway mode
  let gatewayMode = false; // True if connected via gateway
  let lastPing = 0;
  let lastPingTime = 0;
  let reconnectAttempts = 0;
  let awaitingPong = false;
  let connectionStatus = 'disconnected'; // 'disconnected', 'connecting', 'connected', 'saving'
  let silentReconnect = false; // When true, suppress sounds/toasts during reconnect

  // Message handlers registered by other modules
  const handlers = new Map();

  // Sequential request queue for database operations
  const requestQueue = [];
  let isProcessingQueue = false;
  const pendingRequests = new Map(); // requestId -> { resolve, reject, timeout }
  let requestIdCounter = 0;

  /**
   * Detect if running in gateway mode based on hostname
   */
  function isGatewayDomain() {
    const hostname = window.location.hostname.toLowerCase();
    return GATEWAY_DOMAINS.some(domain => hostname === domain || hostname.endsWith('.' + domain));
  }

  /**
   * Extract server ID from URL path (gateway mode)
   * Path format: /{serverPrefix}/ where serverPrefix is lowercase
   * Examples: /abc12/ or /abc12-def34/
   */
  function getServerIdFromPath() {
    const path = window.location.pathname;
    // Match server ID prefix: 5 chars or 5-5 chars with dash, etc.
    const match = path.match(/^\/([a-z0-9]{5}(?:-[a-z0-9]{5})*)\/?/i);
    if (match) {
      return match[1].toLowerCase();
    }
    return null;
  }

  /**
   * Connect to the WebSocket server
   * Automatically detects gateway mode based on hostname
   * @param {string} host - Server host (e.g., 'localhost') - ignored in gateway mode
   * @param {number} port - Server port - ignored in gateway mode
   */
  function connect(host, port) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      console.log('[WS] Already connected');
      return;
    }

    // Detect gateway mode
    gatewayMode = isGatewayDomain();
    let url;

    if (gatewayMode) {
      // Gateway mode: connect to gateway server with server ID in path
      const serverId = getServerIdFromPath();
      if (!serverId) {
        console.error('[WS] No server ID found in URL path');
        emit('error', { message: 'No server ID found in URL. Expected format: /serverid/' });
        return;
      }
      currentServerId = serverId;
      url = `${GATEWAY_WS_URL}/${serverId}`;
      console.log('[WS] Gateway mode - connecting to', url);
    } else {
      // Direct mode: connect to server's WebSocket port
      currentHost = host;
      currentPort = port;
      url = `ws://${host}:${port}`;
      console.log('[WS] Direct mode - connecting to', url);
    }

    try {
      ws = new WebSocket(url);
      setupWebSocketHandlers();
    } catch (e) {
      console.error('[WS] Failed to create WebSocket:', e);
      if (gatewayMode) {
        scheduleGatewayReconnect();
      } else {
        scheduleReconnect(host, port);
      }
      return;
    }
  }

  /**
   * Disconnect from the WebSocket server
   */
  function disconnect() {
    clearTimeout(reconnectTimer);
    stopHeartbeat();
    if (ws) {
      ws.close(1000, 'Client disconnect');
      ws = null;
    }
    isConnected = false;
    sessionData = null;
  }

  // Request types that should show loading bar (data-fetching requests)
  const LOADING_BAR_REQUESTS = new Set([
    'GET_DATA', 'GET_PLAYERS', 'GET_PUNISHMENTS', 'GET_WATCHLIST', 'GET_RULES',
    'GET_SETTINGS', 'GET_USER_SETTINGS', 'GET_MESSAGES', 'GET_TEMPLATES',
    'GET_LOGS', 'GET_STATS', 'GET_ANTICHEAT_CONFIG', 'GET_ANTICHEAT_INFO',
    'GET_ANTICHEAT_ALERTS', 'GET_STAFF_ALERT_PREFS', 'GET_ALERT_PRESETS',
    'GET_SERVER_SETTINGS', 'GET_CHAT_STATUS', 'GET_PLAYER_DETAILS',
    'GET_COMMAND_HISTORY', 'GET_AUTOMOD_LOGS', 'GET_CHAT_LOGS',
    'GET_DEV_CHECKLIST', 'GET_GEYSER_STATUS', 'GET_TRUSTED_DEVICE_COUNT',
    'GET_STAFFCHAT_HISTORY', 'SYNC_AUTOMOD_RULES', 'UPDATE_SETTINGS',
    'UPDATE_USER_SETTINGS', 'CREATE_PUNISHMENT', 'REVOKE_PUNISHMENT'
  ]);

  // Track pending requests to auto-hide loading bar
  let pendingLoadingRequests = 0;

  function shouldShowLoadingBar(type) {
    return LOADING_BAR_REQUESTS.has(type) || type.startsWith('GET_');
  }

  /**
   * Send a message to the server
   * @param {string} type - Message type
   * @param {object} data - Message data
   */
  function send(type, data = {}) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
      console.warn('[WS] Cannot send, not connected');
      if (window.debugLog) window.debugLog('WS', `Failed to send ${type} - not connected`, 'error');
      return false;
    }

    const message = JSON.stringify({ type, data });
    ws.send(message);

    // Only log non-heartbeat/ping messages to avoid spam
    if (type !== 'HEARTBEAT' && type !== 'PING') {
      if (window.debugLog) window.debugLog('WS', `Sent: ${type}`, 'info');

      // Show loading bar for data-fetching requests
      if (window.showLoadingLine && shouldShowLoadingBar(type)) {
        pendingLoadingRequests++;
        window.showLoadingLine();
      }
    }
    return true;
  }

  /**
   * Send a message and wait for response (Promise-based)
   * Used for sequential operations that need confirmation
   * @param {string} type - Message type
   * @param {object} data - Message data
   * @param {string} responseType - Expected response type
   * @param {number} timeout - Timeout in ms (default 30s)
   */
  function sendAndWait(type, data = {}, responseType = null, timeout = 30000) {
    return new Promise((resolve, reject) => {
      if (!ws || ws.readyState !== WebSocket.OPEN) {
        reject(new Error('Not connected'));
        return;
      }

      // Show loading line for data requests
      if (window.showLoadingLine) window.showLoadingLine();

      const requestId = ++requestIdCounter;
      const expectedResponse = responseType || `${type}_RESPONSE`;

      // Add request ID to data for correlation
      data._requestId = requestId;

      const timeoutId = setTimeout(() => {
        pendingRequests.delete(requestId);
        if (window.hideLoadingLine) window.hideLoadingLine();
        reject(new Error(`Request timeout: ${type}`));
      }, timeout);

      pendingRequests.set(requestId, {
        resolve: (result) => {
          if (window.hideLoadingLine) window.hideLoadingLine();
          resolve(result);
        },
        reject: (error) => {
          if (window.hideLoadingLine) window.hideLoadingLine();
          reject(error);
        },
        timeout: timeoutId,
        expectedType: expectedResponse
      });

      const message = JSON.stringify({ type, data });
      ws.send(message);
      if (window.debugLog) window.debugLog('WS', `Sent (awaiting): ${type}`, 'info');
    });
  }

  /**
   * Queue a database operation for sequential processing
   * Ensures operations are processed one at a time in order
   * @param {string} type - Message type
   * @param {object} data - Message data
   */
  function queueOperation(type, data = {}) {
    return new Promise((resolve, reject) => {
      // Show loading line for queued operations
      if (window.showLoadingLine) window.showLoadingLine();
      requestQueue.push({
        type,
        data,
        resolve: (result) => {
          if (window.hideLoadingLine) window.hideLoadingLine();
          resolve(result);
        },
        reject: (error) => {
          if (window.hideLoadingLine) window.hideLoadingLine();
          reject(error);
        }
      });
      processQueue();
    });
  }

  /**
   * Process the next item in the request queue
   */
  async function processQueue() {
    if (isProcessingQueue || requestQueue.length === 0) return;

    isProcessingQueue = true;

    while (requestQueue.length > 0) {
      const { type, data, resolve, reject } = requestQueue.shift();

      try {
        // Send the request
        const success = send(type, data);
        if (success) {
          // Give a small delay between operations
          await new Promise(r => setTimeout(r, 50));
          resolve(true);
        } else {
          reject(new Error('Failed to send'));
        }
      } catch (err) {
        reject(err);
      }
    }

    isProcessingQueue = false;
  }

  /**
   * Handle response for pending requests
   */
  function handlePendingResponse(type, data) {
    const requestId = data?._requestId;
    if (requestId && pendingRequests.has(requestId)) {
      const pending = pendingRequests.get(requestId);
      clearTimeout(pending.timeout);
      pendingRequests.delete(requestId);
      pending.resolve(data);
      return true;
    }

    // Also check by type for requests without explicit requestId
    for (const [id, pending] of pendingRequests) {
      if (pending.expectedType === type) {
        clearTimeout(pending.timeout);
        pendingRequests.delete(id);
        pending.resolve(data);
        return true;
      }
    }

    return false;
  }

  /**
   * Register a handler for a message type
   * @param {string} type - Message type
   * @param {function} handler - Handler function
   */
  function on(type, handler) {
    if (!handlers.has(type)) {
      handlers.set(type, []);
    }
    handlers.get(type).push(handler);
  }

  /**
   * Remove a handler for a message type
   * @param {string} type - Message type
   * @param {function} handler - Handler function
   */
  function off(type, handler) {
    if (handlers.has(type)) {
      const list = handlers.get(type);
      const idx = list.indexOf(handler);
      if (idx !== -1) list.splice(idx, 1);
    }
  }

  /**
   * Emit an event to all registered handlers
   */
  function emit(type, data) {
    if (handlers.has(type)) {
      handlers.get(type).forEach(h => {
        try {
          h(data);
        } catch (e) {
          console.error('[WS] Handler error:', e);
        }
      });
    }
  }

  // Response types that should hide loading bar
  const LOADING_BAR_RESPONSES = new Set([
    'DATA', 'PLAYERS', 'PUNISHMENTS', 'WATCHLIST', 'RULES', 'SETTINGS',
    'USER_SETTINGS_DATA', 'MESSAGES', 'TEMPLATES', 'LOGS', 'STATS',
    'ANTICHEAT_CONFIG', 'ANTICHEAT_INFO', 'ANTICHEAT_ALERTS',
    'STAFF_ALERT_PREFS', 'ALERT_PRESETS', 'SERVER_SETTINGS', 'CHAT_STATUS',
    'PLAYER_DETAILS', 'COMMAND_HISTORY', 'AUTOMOD_LOGS', 'CHAT_LOGS',
    'DEV_CHECKLIST', 'GEYSER_STATUS', 'TRUSTED_DEVICE_COUNT', 'STAFFCHAT_HISTORY',
    'PUNISHMENT_CREATED', 'PUNISHMENT_REVOKED', 'SETTINGS_UPDATED',
    'ERROR', 'SUCCESS'
  ]);

  function shouldHideLoadingBar(type) {
    return LOADING_BAR_RESPONSES.has(type) || type.endsWith('_DATA') || type.endsWith('_RESPONSE');
  }

  /**
   * Handle incoming messages
   */
  function handleMessage(message) {
    const type = message.type;
    // Gateway messages have data at root level, plugin messages use data property
    const data = message.data !== undefined ? message.data : message;

    // Hide loading bar for data responses
    if (pendingLoadingRequests > 0 && shouldHideLoadingBar(type)) {
      pendingLoadingRequests--;
      if (window.hideLoadingLine) window.hideLoadingLine();
    }

    // Check for pending request responses first
    if (handlePendingResponse(type, data)) {
      // Still emit to handlers for UI updates
      emit(type, data);
      return;
    }

    // Handle ping response
    if (type === 'PONG') {
      awaitingPong = false;
      clearTimeout(pingTimeoutTimer);
      if (lastPingTime > 0) {
        lastPing = Date.now() - lastPingTime;
        emit('ping_update', { ping: lastPing });
      }
      // Update last pong time for disconnect detection and cancel pending disconnect overlay
      if (window.updateLastPong) window.updateLastPong();
      if (window.hideDisconnect) window.hideDisconnect();
      return;
    }

    // Handle gateway-specific messages (in gateway mode)
    if (gatewayMode) {
      if (type === 'connected') {
        // Gateway confirmed connection to MC server
        // Gateway sends: { type, serverId, serverName, urlPrefix } (no data wrapper)
        console.log('[WS] Gateway connected to server:', message.serverName);
        emit('gateway_connected', { serverId: message.serverId, serverName: message.serverName, urlPrefix: message.urlPrefix });
        return;
      }

      if (type === 'server_disconnected') {
        // MC server disconnected from gateway
        console.error('[WS] Server disconnected from gateway:', message.message || data.message);
        emit('server_offline', data);
        if (window.debugLog) window.debugLog('WS', 'Server went offline', 'error');
        return;
      }

      if (type === 'error') {
        // Gateway error (e.g., server not found)
        // Gateway sends: { type, code, message } (no data wrapper)
        const code = message.code || data.code;
        const errorMsg = message.message || data.message;
        console.error('[WS] Gateway error:', code, errorMsg);
        emit('gateway_error', { code, message: errorMsg });
        if (code === 'SERVER_NOT_FOUND') {
          emit('server_not_found', { code, message: errorMsg });
        }
        if (window.debugLog) window.debugLog('WS', `Gateway error: ${errorMsg}`, 'error');
        return;
      }
    }

    // Handle authentication responses
    if (type === 'AUTH_SUCCESS') {
      sessionData = data;
      emit('auth_success', data);
      return;
    }

    if (type === 'AUTH_FAILED') {
      const reason = data?.reason || data?.message || 'Invalid credentials';
      if (window.debugLog) window.debugLog('AUTH', `Authentication failed: ${reason}`, 'error');
      emit('auth_failed', data);
      return;
    }

    if (type === 'ACCESS_DENIED') {
      const reason = data?.reason || data?.message || 'Permission denied';
      if (window.debugLog) window.debugLog('AUTH', `Access denied: ${reason}`, 'error');
      emit('access_denied', data);
      return;
    }

    if (type === 'SESSION_EXPIRED') {
      if (window.debugLog) window.debugLog('AUTH', 'Session expired - please reconnect', 'error');
      emit('session_expired', data);
      return;
    }

    // Emit to registered handlers
    emit(type, data);
  }

  /**
   * Schedule a reconnection attempt with exponential backoff
   */
  function scheduleReconnect(host, port) {
    clearTimeout(reconnectTimer);

    // Auto-reconnect is always silent (no sounds/toasts)
    silentReconnect = true;

    // Check max attempts
    if (reconnectAttempts >= WS_MAX_RECONNECT_ATTEMPTS) {
      console.log('[WS] Max reconnect attempts reached');
      emit('reconnect_failed', { attempts: reconnectAttempts });
      return;
    }

    // Exponential backoff: 2s, 4s, 8s, 16s, max 30s
    const delay = Math.min(WS_RECONNECT_DELAY_MIN * Math.pow(2, reconnectAttempts), WS_RECONNECT_DELAY_MAX);
    reconnectAttempts++;
    console.log(`[WS] Reconnecting in ${delay}ms (attempt ${reconnectAttempts})...`);

    // Emit reconnect status for UI update
    emit('reconnect_attempt', { attempt: reconnectAttempts, delay: delay });

    reconnectTimer = setTimeout(() => {
      console.log('[WS] Attempting reconnect...');
      connect(host, port);
    }, delay);
  }

  /**
   * Schedule a gateway reconnection attempt with exponential backoff
   */
  function scheduleGatewayReconnect() {
    clearTimeout(reconnectTimer);

    // Auto-reconnect is always silent (no sounds/toasts)
    silentReconnect = true;

    // Check max attempts
    if (reconnectAttempts >= WS_MAX_RECONNECT_ATTEMPTS) {
      console.log('[WS] Max gateway reconnect attempts reached');
      emit('reconnect_failed', { attempts: reconnectAttempts });
      return;
    }

    // Exponential backoff: 2s, 4s, 8s, 16s, max 30s
    const delay = Math.min(WS_RECONNECT_DELAY_MIN * Math.pow(2, reconnectAttempts), WS_RECONNECT_DELAY_MAX);
    reconnectAttempts++;
    console.log(`[WS] Gateway reconnecting in ${delay}ms (attempt ${reconnectAttempts})...`);

    // Emit reconnect status for UI update
    emit('reconnect_attempt', { attempt: reconnectAttempts, delay: delay });

    reconnectTimer = setTimeout(() => {
      console.log('[WS] Attempting gateway reconnect...');
      connectGateway(currentServerId);
    }, delay);
  }

  /**
   * Connect to gateway with specific server ID
   * @param {string} serverId - Server ID prefix
   */
  function connectGateway(serverId) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      console.log('[WS] Already connected');
      return;
    }

    gatewayMode = true;
    currentServerId = serverId;
    const url = `${GATEWAY_WS_URL}/${serverId}`;
    console.log('[WS] Gateway mode - connecting to', url);

    try {
      ws = new WebSocket(url);
      setupWebSocketHandlers();
    } catch (e) {
      console.error('[WS] Failed to create WebSocket:', e);
      scheduleGatewayReconnect();
    }
  }

  /**
   * Setup WebSocket event handlers (called after creating new WebSocket)
   */
  function setupWebSocketHandlers() {
    if (!ws) return;

    ws.onopen = () => {
      console.log('[WS] Connected' + (gatewayMode ? ' (gateway mode)' : ''));
      isConnected = true;
      connectionStatus = 'connected';
      reconnectAttempts = 0;
      silentReconnect = false; // Reset silent mode after successful connection
      clearTimeout(reconnectTimer);
      startHeartbeat();
      startPingMeasurement();
      emit('connected', { gatewayMode, serverId: currentServerId, wasReconnect: silentReconnect });
      emit('status_change', { status: connectionStatus, ping: lastPing });
      if (window.debugLog) window.debugLog('WS', 'Connected to server' + (gatewayMode ? ' via gateway' : ''), 'success');
    };

    ws.onclose = (event) => {
      console.log('[WS] Disconnected:', event.code, event.reason);
      isConnected = false;
      connectionStatus = 'disconnected';
      sessionData = null;
      stopHeartbeat();
      stopPingMeasurement();
      emit('disconnected', { code: event.code, reason: event.reason, gatewayMode });
      emit('status_change', { status: connectionStatus, ping: 0 });
      if (window.debugLog) window.debugLog('WS', `Disconnected (${event.code}: ${event.reason || 'No reason'})`, 'warn');

      // Don't reconnect if we were denied access or server not found
      if (event.code !== 4001 && event.code !== 4003 && event.code !== 4004) {
        if (gatewayMode) {
          scheduleGatewayReconnect();
        } else {
          scheduleReconnect(currentHost, currentPort);
        }
      }
    };

    ws.onerror = (error) => {
      console.error('[WS] Error:', error);
      emit('error', error);
      const errorMsg = error.message || error.type || 'Connection error';
      if (window.debugLog) window.debugLog('WS', `Error: ${errorMsg}`, 'error');
    };

    ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        const silentTypes = ['PONG', 'SERVER_STATUS', 'HEARTBEAT_ACK'];
        if (!silentTypes.includes(message.type)) {
          if (message.type === 'ERROR' && message.data) {
            const code = message.data.code || 'UNKNOWN';
            const errorMsg = message.data.message || 'Unknown error';
            if (window.debugLog) window.debugLog('WS', `Error [${code}]: ${errorMsg}`, 'error');
          } else {
            if (window.debugLog) window.debugLog('WS', `Received: ${message.type}`, 'info');
          }
        }
        handleMessage(message);
      } catch (e) {
        console.error('[WS] Failed to parse message:', e);
        if (window.debugLog) window.debugLog('WS', `Parse error: ${e.message}`, 'error');
      }
    };
  }

  /**
   * Start heartbeat to keep connection alive
   */
  function startHeartbeat() {
    stopHeartbeat();
    heartbeatTimer = setInterval(() => {
      if (isConnected) {
        send('HEARTBEAT');
      }
    }, WS_HEARTBEAT_INTERVAL);
  }

  /**
   * Stop heartbeat
   */
  function stopHeartbeat() {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer);
      heartbeatTimer = null;
    }
  }

  /**
   * Start ping measurement
   */
  function startPingMeasurement() {
    stopPingMeasurement();
    // Measure ping immediately and then every WS_PING_INTERVAL
    measurePing();
    pingTimer = setInterval(measurePing, WS_PING_INTERVAL);
  }

  /**
   * Stop ping measurement
   */
  function stopPingMeasurement() {
    if (pingTimer) {
      clearInterval(pingTimer);
      pingTimer = null;
    }
    if (pingTimeoutTimer) {
      clearTimeout(pingTimeoutTimer);
      pingTimeoutTimer = null;
    }
    lastPing = 0;
    lastPingTime = 0;
    awaitingPong = false;
  }

  /**
   * Send ping to measure latency
   */
  function measurePing() {
    if (isConnected && !awaitingPong) {
      awaitingPong = true;
      lastPingTime = Date.now();
      send('PING');

      // Set timeout for PONG response - if no response, connection is likely dead
      clearTimeout(pingTimeoutTimer);
      pingTimeoutTimer = setTimeout(() => {
        if (awaitingPong && isConnected) {
          console.warn('[WS] No PONG received within timeout, connection may be stale');
          // Don't immediately disconnect - just log warning and reset flag
          // This allows high-ping connections to continue working
          awaitingPong = false;
          lastPing = -1; // Indicate connection issues
          emit('ping_update', { ping: -1, warning: 'Connection slow or unstable' });
        }
      }, WS_PING_TIMEOUT);
    }
  }

  /**
   * Set connection status (for external use like saving indicators)
   */
  function setStatus(status) {
    connectionStatus = status;
    emit('status_change', { status: connectionStatus, ping: lastPing });
  }

  /**
   * Generate a device fingerprint for trusted device auth
   */
  function generateDeviceFingerprint() {
    const data = [
      navigator.userAgent,
      navigator.language,
      screen.width + 'x' + screen.height,
      screen.colorDepth,
      new Date().getTimezoneOffset(),
      navigator.hardwareConcurrency || 0,
      navigator.deviceMemory || 0,
      navigator.platform || '',
      navigator.vendor || ''
    ].join('|');

    // Simple hash function
    let hash = 0;
    for (let i = 0; i < data.length; i++) {
      const char = data.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }

    // Convert to hex and pad (stable, no timestamp)
    const fingerprint = Math.abs(hash).toString(16).padStart(32, '0');
    return fingerprint;
  }

  /**
   * Authenticate with a connect code (from in-game /mx connect)
   * @param {string} code - 6-character connect code
   */
  function authWithCode(code) {
    const fingerprint = generateDeviceFingerprint();
    return send('AUTH_CONNECT_CODE', { code: code.toUpperCase(), deviceFingerprint: fingerprint });
  }

  /**
   * Authenticate with a permanent token (from /mx gettoken)
   * @param {string} token - Permanent auth token
   */
  function authWithToken(token) {
    const fingerprint = generateDeviceFingerprint();
    return send('AUTH_PERMANENT_TOKEN', { token, deviceFingerprint: fingerprint });
  }

  /**
   * Authenticate with a URL token (from /mx connect URL)
   * @param {string} token - Temporary URL token
   */
  function authWithUrlToken(token) {
    const fingerprint = generateDeviceFingerprint();
    return send('AUTH_URL_TOKEN', { token, deviceFingerprint: fingerprint });
  }

  /**
   * Authenticate with an existing session
   * @param {string} sessionId - Session ID from previous auth
   */
  function authWithSession(sessionId) {
    return send('AUTH_SESSION', { sessionId });
  }

  /**
   * Authenticate with trusted device
   */
  function authWithTrustedDevice() {
    const fingerprint = generateDeviceFingerprint();
    return send('AUTH_DEVICE_TRUST', { deviceFingerprint: fingerprint });
  }

  /**
   * Legacy console auth - now requires token
   * @deprecated Use authWithToken instead
   */
  function authAsConsole(username) {
    // Legacy: try to use stored token if available
    const savedToken = localStorage.getItem('mx_permanent_token');
    if (savedToken) {
      return authWithToken(savedToken);
    }
    // Fall back to sending username (server will reject without token)
    return send('AUTH_CONSOLE', { username });
  }

  /**
   * Request data from server
   */
  function requestPlayers() {
    return send('GET_PLAYERS');
  }

  function requestPunishments(filters = {}) {
    return send('GET_PUNISHMENTS', filters);
  }

  function requestWatchlist() {
    return send('GET_WATCHLIST');
  }

  function requestAutomodRules() {
    return send('GET_AUTOMOD_RULES');
  }

  function requestTemplates() {
    return send('GET_TEMPLATES');
  }

  function requestSettings() {
    return send('GET_SETTINGS');
  }

  function requestUserSettings() {
    return send('GET_USER_SETTINGS');
  }

  /**
   * Send actions to server
   */
  function createPunishment(punishment) {
    return send('CREATE_PUNISHMENT', punishment);
  }

  function revokePunishment(caseId, reason) {
    return send('REVOKE_PUNISHMENT', { caseId, reason });
  }

  function addToWatchlist(playerUuid, playerName, reason) {
    return send('WATCHLIST_ADD', { playerUuid, playerName, reason });
  }

  function removeFromWatchlist(playerUuid) {
    return send('WATCHLIST_REMOVE', { playerUuid });
  }

  function saveAutomodRule(rule) {
    return send('UPDATE_AUTOMOD_RULE', rule);
  }

  function deleteAutomodRule(ruleId) {
    return send('DELETE_AUTOMOD_RULE', { ruleId });
  }

  function updateUserSettings(settings) {
    return send('UPDATE_USER_SETTINGS', settings);
  }

  function sendStaffChat(message) {
    return send('STAFFCHAT_MESSAGE', { message });
  }

  function executeCommand(command) {
    return send('EXECUTE_COMMAND', { command });
  }

  function requestChatStatus() {
    return send('GET_CHAT_STATUS');
  }

  function requestAnticheatAlerts() {
    return send('GET_ANTICHEAT_ALERTS');
  }

  function requestStaffAlertPrefs() {
    return send('GET_STAFF_ALERT_PREFS');
  }

  function updateStaffAlertPref(anticheat, checkName, alertLevel, thresholdCount, timeWindowSeconds) {
    return send('UPDATE_STAFF_ALERT_PREF', {
      anticheat,
      checkName,
      alertLevel,
      thresholdCount,
      timeWindowSeconds
    });
  }

  function requestAlertPresets() {
    return send('GET_ALERT_PRESETS');
  }

  function applyAlertPreset(presetId) {
    return send('APPLY_ALERT_PRESET', { presetId });
  }

  function setChatLock(locked) {
    return send('SET_CHAT_LOCK', { locked });
  }

  function setSlowmode(seconds) {
    return send('SET_SLOWMODE', { seconds });
  }

  function clearChat() {
    return send('CLEAR_CHAT');
  }

  function requestServerSettings() {
    return send('GET_SERVER_SETTINGS');
  }

  function updateMuteSettings(settings) {
    return send('UPDATE_MUTE_SETTINGS', settings);
  }

  function updateWarnSettings(settings) {
    return send('UPDATE_WARN_SETTINGS', settings);
  }

  function updateAnticheatSettings(settings) {
    return send('UPDATE_ANTICHEAT_SETTINGS', settings);
  }

  function kickPlayer(playerName, reason) {
    return send('KICK_PLAYER', { playerName, reason });
  }

  function clearTrustedDevices() {
    return send('CLEAR_TRUSTED_DEVICES');
  }

  function requestTrustedDeviceCount() {
    return send('GET_TRUSTED_DEVICE_COUNT');
  }

  function requestStaffChatHistory(limit = 50, beforeTimestamp = null) {
    return send('GET_STAFFCHAT_HISTORY', { limit, before: beforeTimestamp });
  }

  function requestServerStatus() {
    return send('GET_SERVER_STATUS');
  }

  // Expose API
  window.MX = window.MX || {};
  window.MX.ws = {
    connect,
    connectGateway,
    disconnect,
    send,
    on,
    off,
    isConnected: () => isConnected,
    isGatewayMode: () => gatewayMode,
    isGatewayDomain,
    getServerIdFromPath,
    getServerId: () => currentServerId,
    getSession: () => sessionData,
    getHost: () => currentHost,
    getPort: () => currentPort,
    getPing: () => lastPing,
    getStatus: () => connectionStatus,
    setStatus,
    isSilentReconnect: () => silentReconnect,
    getReconnectAttempts: () => reconnectAttempts,

    // Auth methods
    authWithCode,
    authWithToken,
    authWithUrlToken,
    authWithSession,
    authWithTrustedDevice,
    authAsConsole, // Legacy
    generateDeviceFingerprint,

    // Requests
    requestPlayers,
    requestPunishments,
    requestWatchlist,
    requestAutomodRules,
    requestTemplates,
    requestSettings,
    requestUserSettings,
    requestChatStatus,
    requestServerSettings,
    requestTrustedDeviceCount,
    requestAnticheatAlerts,
    requestStaffAlertPrefs,
    requestAlertPresets,
    requestStaffChatHistory,
    requestServerStatus,

    // Actions
    createPunishment,
    revokePunishment,
    addToWatchlist,
    removeFromWatchlist,
    saveAutomodRule,
    deleteAutomodRule,
    updateUserSettings,
    sendStaffChat,
    executeCommand,
    setChatLock,
    setSlowmode,
    clearChat,
    kickPlayer,
    clearTrustedDevices,
    updateStaffAlertPref,
    applyAlertPreset,
    updateMuteSettings,
    updateWarnSettings,
    updateAnticheatSettings,

    // Sequential request processing
    sendAndWait,
    queueOperation
  };

})();

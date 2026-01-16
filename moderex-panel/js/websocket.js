/* ============================================
   ModereX Control Panel - WebSocket Manager
   ============================================ */
(function() {
  'use strict';

  const WS_RECONNECT_DELAY = 3000;
  const WS_HEARTBEAT_INTERVAL = 30000;

  let ws = null;
  let heartbeatTimer = null;
  let reconnectTimer = null;
  let isConnected = false;
  let sessionData = null;

  // Message handlers registered by other modules
  const handlers = new Map();

  /**
   * Connect to the WebSocket server
   * @param {string} host - Server host (e.g., 'localhost')
   * @param {number} port - Server port
   */
  function connect(host, port) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      console.log('[WS] Already connected');
      return;
    }

    const url = `ws://${host}:${port}`;
    console.log('[WS] Connecting to', url);

    try {
      ws = new WebSocket(url);
    } catch (e) {
      console.error('[WS] Failed to create WebSocket:', e);
      scheduleReconnect(host, port);
      return;
    }

    ws.onopen = () => {
      console.log('[WS] Connected');
      isConnected = true;
      clearTimeout(reconnectTimer);
      startHeartbeat();
      emit('connected');
    };

    ws.onclose = (event) => {
      console.log('[WS] Disconnected:', event.code, event.reason);
      isConnected = false;
      sessionData = null;
      stopHeartbeat();
      emit('disconnected', { code: event.code, reason: event.reason });

      // Don't reconnect if we were denied access
      if (event.code !== 4001 && event.code !== 4003) {
        scheduleReconnect(host, port);
      }
    };

    ws.onerror = (error) => {
      console.error('[WS] Error:', error);
      emit('error', error);
    };

    ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        handleMessage(message);
      } catch (e) {
        console.error('[WS] Failed to parse message:', e);
      }
    };
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

  /**
   * Send a message to the server
   * @param {string} type - Message type
   * @param {object} data - Message data
   */
  function send(type, data = {}) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
      console.warn('[WS] Cannot send, not connected');
      return false;
    }

    const message = JSON.stringify({ type, data });
    ws.send(message);
    return true;
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

  /**
   * Handle incoming messages
   */
  function handleMessage(message) {
    const { type, data } = message;

    // Handle authentication responses
    if (type === 'AUTH_SUCCESS') {
      sessionData = data;
      emit('auth_success', data);
      return;
    }

    if (type === 'AUTH_FAILED') {
      emit('auth_failed', data);
      return;
    }

    if (type === 'ACCESS_DENIED') {
      emit('access_denied', data);
      return;
    }

    // Emit to registered handlers
    emit(type, data);
  }

  /**
   * Schedule a reconnection attempt
   */
  function scheduleReconnect(host, port) {
    clearTimeout(reconnectTimer);
    reconnectTimer = setTimeout(() => {
      console.log('[WS] Attempting reconnect...');
      connect(host, port);
    }, WS_RECONNECT_DELAY);
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
   * Authenticate with a connect code (from in-game /mx connect)
   * @param {string} code - 6-character connect code
   */
  function authWithCode(code) {
    return send('AUTH_CONNECT_CODE', { code: code.toUpperCase() });
  }

  /**
   * Authenticate as console user with username
   * @param {string} username - Minecraft username
   */
  function authAsConsole(username) {
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
    return send('SAVE_AUTOMOD_RULE', rule);
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

  function setChatLock(locked) {
    return send('SET_CHAT_LOCK', { locked });
  }

  function setSlowmode(seconds) {
    return send('SET_SLOWMODE', { seconds });
  }

  function clearChat() {
    return send('CLEAR_CHAT');
  }

  function kickPlayer(playerName, reason) {
    return send('KICK_PLAYER', { playerName, reason });
  }

  // Expose API
  window.MX = window.MX || {};
  window.MX.ws = {
    connect,
    disconnect,
    send,
    on,
    off,
    isConnected: () => isConnected,
    getSession: () => sessionData,

    // Auth
    authWithCode,
    authAsConsole,

    // Requests
    requestPlayers,
    requestPunishments,
    requestWatchlist,
    requestAutomodRules,
    requestSettings,
    requestUserSettings,
    requestChatStatus,

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
    kickPlayer
  };

})();

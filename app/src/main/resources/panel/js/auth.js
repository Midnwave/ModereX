/* ============================================
   ModereX Control Panel - Authentication
   Revamped with device trust, token validation,
   and secure action verification
   ============================================ */
(function() {
  'use strict';

  const { $ } = window.MX.utils;
  const ws = window.MX.ws;

  // Token validation interval (10 seconds)
  const TOKEN_VALIDATION_INTERVAL = 10000;

  // Auth states
  const AuthStatus = {
    UNAUTHENTICATED: 'unauthenticated',
    PENDING_VERIFICATION: 'pending_verification',
    VERIFIED: 'verified'
  };

  // Auth state
  let authState = {
    status: AuthStatus.UNAUTHENTICATED,
    connected: false,
    authenticated: false,
    accessDenied: false,
    session: null,
    token: null,              // Current auth token
    tokenValid: false,        // Token validation status
    deviceFingerprint: null,  // Unique device identifier
    deviceTrustEnabled: false, // User's device trust setting
    serverHost: null,
    serverPort: null,
    configLoaded: false,
    urlToken: null,
    reconnectTimer: null,
    reconnectAttempts: 0,
    maxReconnectAttempts: 10,
    lastError: null,
    lastValidation: null,     // Timestamp of last token validation
    connectionPhase: 'idle'   // idle, connecting, authenticating, connected
  };

  // Timers
  let tokenValidationTimer = null;

  // DOM Elements
  let dom = {};

  /**
   * Generate a unique device fingerprint
   * This creates a stable identifier for the browser/device
   */
  function generateDeviceFingerprint() {
    const components = [
      navigator.userAgent,
      navigator.language,
      screen.width + 'x' + screen.height,
      screen.colorDepth,
      new Date().getTimezoneOffset(),
      navigator.hardwareConcurrency || 'unknown',
      navigator.platform
    ];

    // Create a hash from components
    let hash = 0;
    const str = components.join('|');
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }

    // Use stored ID if exists, otherwise create new
    const storedId = localStorage.getItem('mx_device_id');
    if (storedId) {
      return storedId;
    }

    const newId = Math.abs(hash).toString(16) + '-' + Date.now().toString(36);
    localStorage.setItem('mx_device_id', newId);
    return newId;
  }

  /**
   * Show connection toast alert
   */
  function showConnectionToast(type, title, message) {
    if (window.MX?.toast) {
      window.MX.toast(type, title, message, { ttl: 6000 });
    }
  }

  /**
   * Initialize auth module
   */
  function init() {
    // Generate device fingerprint
    authState.deviceFingerprint = generateDeviceFingerprint();

    cacheDom();
    checkUrlToken();
    setupEventListeners();
    setupWebSocketHandlers();
    startConnection();
  }

  /**
   * Check for token in URL
   */
  function checkUrlToken() {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if (token) {
      authState.urlToken = token;
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  }

  /**
   * Start the connection process
   */
  async function startConnection() {
    authState.connectionPhase = 'connecting';
    authState.status = AuthStatus.PENDING_VERIFICATION;

    // Check if we're in gateway mode
    const isGateway = ws.isGatewayDomain();

    if (isGateway) {
      // Gateway mode: connect directly to gateway without config fetch
      const serverId = ws.getServerIdFromPath();
      if (!serverId) {
        authState.connectionPhase = 'idle';
        authState.status = AuthStatus.UNAUTHENTICATED;
        authState.lastError = 'No server ID found in URL. Expected format: /serverid/';
        showConnectionToast('bad', 'Invalid URL', authState.lastError);
        showGatewayError('Invalid Server URL', 'Please check the URL and try again.');
        return;
      }

      updateStatus('Connecting...', 'Connecting to server via gateway');
      console.log('[Auth] Gateway mode - connecting to server:', serverId);

      try {
        await connectGatewayWebSocket(serverId);
      } catch (err) {
        console.error('[Auth] Gateway connection failed:', err);
        authState.connectionPhase = 'idle';
        authState.status = AuthStatus.UNAUTHENTICATED;
        authState.lastError = err.message || 'Gateway connection failed';

        showConnectionToast('bad', 'Connection Failed', authState.lastError);
        showGatewayError('Connection Failed', authState.lastError);
      }
      return;
    }

    // Direct mode: fetch config and connect
    updateStatus('Connecting...', 'Loading server configuration');

    try {
      // Step 1: Load server config
      const response = await fetch('/api/config');
      if (!response.ok) {
        throw new Error('Server not reachable');
      }

      const config = await response.json();
      authState.serverHost = config.host || window.location.hostname;
      authState.serverPort = config.wsPort || 8081;
      authState.configLoaded = true;

      // Update AI config if available
      if (window.MX && window.MX.ai) {
        window.MX.ai.enabled = config.aiEnabled !== undefined ? config.aiEnabled : true;
        window.MX.ai.model = config.aiModel || 'devstral-2-123b-cloud';
      }

      // Hide server section since auto-detected
      if (dom.serverSection) {
        dom.serverSection.style.display = 'none';
      }

      // Step 2: Establish WebSocket connection
      updateStatus('Connecting...', 'Establishing WebSocket connection');
      await connectWebSocket();

    } catch (err) {
      console.error('[Auth] Connection failed:', err);
      authState.connectionPhase = 'idle';
      authState.status = AuthStatus.UNAUTHENTICATED;
      authState.lastError = err.message || 'Connection failed';

      showConnectionToast('bad', 'Connection Failed', authState.lastError);
      showManualAuth(authState.lastError);
      scheduleReconnect();
    }
  }

  /**
   * Connect to gateway WebSocket
   */
  function connectGatewayWebSocket(serverId) {
    return new Promise((resolve, reject) => {
      let connectionTimeout = null;
      let resolved = false;

      const cleanup = () => {
        if (connectionTimeout) clearTimeout(connectionTimeout);
        ws.off('connected', onConnected);
        ws.off('gateway_connected', onGatewayConnected);
        ws.off('server_not_found', onServerNotFound);
        ws.off('gateway_error', onGatewayError);
        ws.off('error', onError);
      };

      const onConnected = () => {
        console.log('[Auth] WebSocket connected, waiting for gateway confirmation...');
      };

      const onGatewayConnected = (data) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        authState.connected = true;
        authState.serverName = data.serverName;
        console.log('[Auth] Gateway connected to server:', data.serverName);
        resolve();

        // Now try to authenticate
        tryAuthenticate();
      };

      const onServerNotFound = (data) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error('Server not found. It may be offline or the ID is incorrect.'));
      };

      const onGatewayError = (data) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error(data.message || 'Gateway error'));
      };

      const onError = (err) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error('WebSocket connection failed'));
      };

      ws.on('connected', onConnected);
      ws.on('gateway_connected', onGatewayConnected);
      ws.on('server_not_found', onServerNotFound);
      ws.on('gateway_error', onGatewayError);
      ws.on('error', onError);

      // Set connection timeout
      connectionTimeout = setTimeout(() => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error('Connection timed out'));
      }, 15000); // Longer timeout for gateway

      // Start gateway connection
      ws.connectGateway(serverId);
    });
  }

  /**
   * Show gateway-specific error screen
   */
  function showGatewayError(title, message) {
    authState.connectionPhase = 'idle';
    authState.status = AuthStatus.UNAUTHENTICATED;

    if (dom.authStatusArea) {
      dom.authStatusArea.innerHTML = `
        <div class="auth-error">
          <div class="auth-error-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
          </div>
          <h2>${title}</h2>
          <p>${message}</p>
          <p class="auth-error-hint">
            Check that the server is online and connected to the gateway.<br>
            You can also try accessing the panel directly via the server's IP and port.
          </p>
          <button class="btn btn-primary" onclick="location.reload()">Try Again</button>
        </div>
      `;
      dom.authStatusArea.style.display = 'block';
    }

    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }
  }

  /**
   * Connect to WebSocket server
   */
  function connectWebSocket() {
    return new Promise((resolve, reject) => {
      const host = authState.serverHost;
      const port = authState.serverPort;

      let connectionTimeout = null;
      let resolved = false;

      const cleanup = () => {
        if (connectionTimeout) clearTimeout(connectionTimeout);
        ws.off('connected', onConnected);
        ws.off('error', onError);
      };

      const onConnected = () => {
        if (resolved) return;
        resolved = true;
        cleanup();
        authState.connected = true;
        resolve();

        // Now try to authenticate
        tryAuthenticate();
      };

      const onError = (err) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error('WebSocket connection failed'));
      };

      ws.on('connected', onConnected);
      ws.on('error', onError);

      // Set connection timeout
      connectionTimeout = setTimeout(() => {
        if (resolved) return;
        resolved = true;
        cleanup();
        reject(new Error('Connection timed out'));
      }, 8000);

      // Start connection
      if (!ws.isConnected()) {
        ws.connect(host, port);
      } else {
        resolved = true;
        cleanup();
        authState.connected = true;
        resolve();
        tryAuthenticate();
      }
    });
  }

  /**
   * Try to authenticate using available methods
   * Priority: URL token > Saved token with device trust check > Session
   */
  function tryAuthenticate() {
    authState.connectionPhase = 'authenticating';
    authState.status = AuthStatus.PENDING_VERIFICATION;
    updateStatus('Authenticating...', 'Verifying credentials');

    const urlToken = authState.urlToken;
    const savedToken = localStorage.getItem('mx_permanent_token');
    const savedSession = getSavedSession();
    const savedDeviceFingerprint = localStorage.getItem('mx_token_device');

    if (urlToken) {
      console.log('[Auth] Authenticating with URL token');
      updateStatus('Authenticating...', 'Verifying link token');
      ws.send('AUTH_URL_TOKEN', {
        token: urlToken,
        deviceFingerprint: authState.deviceFingerprint
      });
      authState.urlToken = null;
    } else if (savedToken) {
      // Check if this device previously logged in with this token
      if (savedDeviceFingerprint === authState.deviceFingerprint) {
        console.log('[Auth] Same device - authenticating with saved token');
        updateStatus('Authenticating...', 'Verifying token');
        // Use AUTH_PERMANENT_TOKEN which the server understands
        ws.send('AUTH_PERMANENT_TOKEN', {
          token: savedToken,
          deviceFingerprint: authState.deviceFingerprint
        });
      } else {
        console.log('[Auth] Different device - requiring manual auth');
        // Different device - require re-entering token
        showManualAuth('Please enter your token to continue on this device.');
      }
    } else if (savedSession) {
      console.log('[Auth] Authenticating with session');
      updateStatus('Authenticating...', 'Resuming session');
      ws.send('AUTH_SESSION', {
        sessionId: savedSession,
        deviceFingerprint: authState.deviceFingerprint
      });
    } else {
      console.log('[Auth] No saved credentials - showing manual auth');
      showManualAuth();
    }

    // Set auth timeout
    setTimeout(() => {
      if (authState.connectionPhase === 'authenticating' && !authState.authenticated) {
        console.log('[Auth] Authentication timeout - showing manual auth');
        showManualAuth('Authentication timed out');
      }
    }, 10000);
  }

  /**
   * Show manual authentication form
   */
  function showManualAuth(errorMsg) {
    authState.connectionPhase = 'idle';
    authState.status = AuthStatus.UNAUTHENTICATED;

    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = 'none';
    }

    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'block';
    }

    if (errorMsg) {
      showError(errorMsg);
    }

    // Load saved token into field (but user must re-enter if device trust is off)
    const savedToken = localStorage.getItem('mx_permanent_token');
    if (savedToken && dom.authToken) {
      dom.authToken.value = savedToken;
    }
  }

  /**
   * Cache DOM elements
   */
  function cacheDom() {
    dom = {
      authOverlay: $('#authOverlay'),
      accessDeniedOverlay: $('#accessDeniedOverlay'),
      authStatusArea: $('#authStatusArea'),
      authStatusText: $('#authStatusText'),
      authStatusSub: $('#authStatusSub'),
      authManualSection: $('#authManualSection'),
      serverSection: $('#serverSection'),
      authTokenSection: $('#authTokenSection'),
      authToken: $('#authToken'),
      serverHost: $('#serverHost'),
      serverPort: $('#serverPort'),
      authBtn: $('#authBtn'),
      authError: $('#authError')
    };
  }

  /**
   * Setup event listeners
   */
  function setupEventListeners() {
    dom.authBtn?.addEventListener('click', () => {
      authenticate();
    });

    dom.authToken?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') authenticate();
    });

    dom.serverHost?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') authenticate();
    });

    dom.serverPort?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') authenticate();
    });
  }

  /**
   * Setup WebSocket event handlers
   */
  function setupWebSocketHandlers() {
    ws.on('connected', () => {
      authState.connected = true;
      authState.reconnectAttempts = 0;
      console.log('[Auth] WebSocket connected');
      if (window.devtoolsLog) window.devtoolsLog('WS', 'WebSocket connected', 'success');
    });

    ws.on('disconnected', (data) => {
      const wasAuthenticated = authState.authenticated;
      authState.connected = false;
      authState.authenticated = false;
      authState.tokenValid = false;
      stopTokenValidation();

      console.log('[Auth] Disconnected:', data.code, data.reason);
      if (window.devtoolsLog) window.devtoolsLog('WS', `Disconnected (code: ${data.code}, reason: ${data.reason || 'none'})`, 'warn');

      // Handle access denied - don't reconnect
      if (data.code === 4001 || data.code === 4003) {
        authState.status = AuthStatus.UNAUTHENTICATED;
        authState.token = null;
        showAccessDenied(data.reason || 'Access denied');
        return;
      }

      // Determine disconnect reason for toast
      let disconnectReason = 'Connection lost';
      if (data.code === 1006) {
        disconnectReason = 'Server unreachable';
      } else if (data.code === 1001) {
        disconnectReason = 'Server going away';
      } else if (data.code === 1011) {
        disconnectReason = 'Server error';
      } else if (data.reason) {
        disconnectReason = data.reason;
      }

      // Show toast if was previously authenticated
      if (wasAuthenticated) {
        showConnectionToast('warn', 'Disconnected', disconnectReason);
      }

      // Check if we should auto-reconnect with device trust
      if (authState.deviceTrustEnabled && authState.token) {
        console.log('[Auth] Device trust enabled - attempting auto-reconnect');
        authState.status = AuthStatus.PENDING_VERIFICATION;
        authState.connectionPhase = 'idle';
        scheduleReconnect();
      } else {
        // Fully logout
        authState.status = AuthStatus.UNAUTHENTICATED;
        authState.token = null;
        authState.session = null;
        authState.connectionPhase = 'idle';
        // Keep saved token but user will need to re-authenticate
        scheduleReconnect();
      }
    });

    // Standard auth success (from token authentication)
    ws.on('auth_success', (data) => {
      console.log('[Auth] Authentication successful:', data.playerName || data.username);
      if (window.devtoolsLog) window.devtoolsLog('AUTH', `Authenticated as ${data.playerName || data.username}`, 'success');

      // Store the token
      if (data.token) {
        authState.token = data.token;
      } else if (data.permanentToken) {
        authState.token = data.permanentToken;
      }

      authState.deviceTrustEnabled = data.deviceTrustEnabled || false;

      // Save token with device fingerprint
      if (authState.token) {
        localStorage.setItem('mx_permanent_token', authState.token);
        localStorage.setItem('mx_token_device', authState.deviceFingerprint);
      }

      completeAuthentication(data);
    });

    ws.on('auth_failed', (data) => {
      console.log('[Auth] Authentication failed:', data?.message);
      if (window.devtoolsLog) window.devtoolsLog('AUTH', `Authentication failed: ${data?.message || 'unknown'}`, 'error');

      authState.authenticated = false;
      authState.status = AuthStatus.UNAUTHENTICATED;
      authState.connectionPhase = 'idle';
      authState.lastError = data?.message || 'Authentication failed';
      authState.token = null;

      let errorTitle = 'Authentication Failed';
      let errorMessage = data?.message || 'Invalid credentials';

      if (data?.code === 'INVALID_TOKEN') {
        errorTitle = 'Invalid Token';
        errorMessage = 'The token is invalid or expired. Get a new one with /mx gettoken';
        localStorage.removeItem('mx_permanent_token');
        localStorage.removeItem('mx_token_device');
      } else if (data?.code === 'NO_PERMISSION') {
        errorTitle = 'No Permission';
        errorMessage = 'You need moderex.webpanel permission to access the panel';
      } else if (data?.code === 'SESSION_EXPIRED') {
        errorTitle = 'Session Expired';
        errorMessage = 'Your session has expired. Please authenticate again.';
        clearSavedSession();
      }

      showConnectionToast('bad', errorTitle, errorMessage);
      showManualAuth(errorMessage);
    });

    ws.on('access_denied', (data) => {
      console.log('[Auth] Access denied:', data?.message);
      if (window.devtoolsLog) window.devtoolsLog('AUTH', `Access denied: ${data?.message || 'no permission'}`, 'error');
      authState.accessDenied = true;
      authState.authenticated = false;
      authState.status = AuthStatus.UNAUTHENTICATED;
      authState.connectionPhase = 'idle';
      authState.token = null;

      showConnectionToast('bad', 'Access Denied', data?.message || 'You do not have permission to access the panel');
      showAccessDenied(data?.message);
    });

    ws.on('session_expired', () => {
      console.log('[Auth] Session expired');
      if (window.devtoolsLog) window.devtoolsLog('AUTH', 'Session expired', 'warn');
      forceLogout('Session expired. Please log in again.');
    });

    // Session terminated (e.g., after stress test)
    ws.on('SESSION_TERMINATED', (data) => {
      console.log('[Auth] Session terminated:', data?.reason);
      if (window.devtoolsLog) window.devtoolsLog('AUTH', `Session terminated: ${data?.reason || 'unknown'}`, 'warn');
      // Clear all saved auth data
      localStorage.removeItem('mx_permanent_token');
      localStorage.removeItem('mx_token_device');
      localStorage.removeItem('mx_session');
      forceLogout(data?.reason || 'Session terminated. Please re-authenticate.');
    });

    // Permission check response for action verification
    ws.on('PERMISSION_CHECK', (data) => {
      if (!data.hasPermission) {
        window.MX.toast?.('error', 'Permission Denied', data.message || 'You do not have permission for this action.');
      }
    });

    // Handle ping for connection health
    ws.on('ping_update', (data) => {
      if (data.ping === -1) {
        console.warn('[Auth] Connection unstable');
      }
    });

    // Gateway-specific events (when connected via panel.moderex.net)
    ws.on('server_offline', (data) => {
      console.log('[Auth] Server went offline via gateway');
      if (window.devtoolsLog) window.devtoolsLog('GATEWAY', 'Minecraft server disconnected from gateway', 'warn');

      authState.connected = false;
      authState.authenticated = false;
      authState.tokenValid = false;
      stopTokenValidation();

      // Show server offline overlay
      showServerOffline(data?.lastSeen);
    });

    ws.on('server_online', (data) => {
      console.log('[Auth] Server came back online via gateway');
      if (window.devtoolsLog) window.devtoolsLog('GATEWAY', 'Minecraft server reconnected to gateway', 'success');

      // Hide offline overlay and attempt reconnect
      hideServerOffline();

      // Try to re-authenticate if we have a saved token
      if (authState.token) {
        console.log('[Auth] Attempting re-authentication after server reconnect');
        authenticate();
      }
    });

    ws.on('gateway_error', (data) => {
      console.error('[Auth] Gateway error:', data?.code, data?.message);
      if (window.devtoolsLog) window.devtoolsLog('GATEWAY', `Error: ${data?.message || data?.code}`, 'error');

      if (data?.code === 'SERVER_NOT_FOUND') {
        showGatewayError('Server Not Found', 'The server ID in the URL is invalid or the server has never connected to the gateway.');
      } else if (data?.code === 'SERVER_OFFLINE') {
        showServerOffline(data?.lastSeen);
      } else {
        showGatewayError('Gateway Error', data?.message || 'An error occurred connecting to the server.');
      }
    });
  }

  /**
   * Complete the authentication process - shows green checkmark
   */
  function completeAuthentication(data) {
    authState.authenticated = true;
    authState.status = AuthStatus.VERIFIED;
    authState.tokenValid = true;
    authState.session = data;
    authState.accessDenied = false;
    authState.connectionPhase = 'connected';
    authState.reconnectAttempts = 0;
    authState.lastError = null;

    // Log authenticated user info to console for debugging
    console.log('%c[ModereX] Authenticated User Info', 'color: #4ade80; font-weight: bold;');
    console.log('  Player:', data.playerName || data.username);
    console.log('  UUID:', data.playerUuid || data.uuid || 'N/A');
    console.log('  Auth Method:', data.authMethod || 'Unknown');
    console.log('  Session ID:', data.sessionId || 'N/A');
    console.log('  Platform:', data.platform || (data.isBedrock ? 'Bedrock' : 'Java'));
    if (data.rank) console.log('  Rank:', data.rank);
    if (data.prefix) console.log('  Prefix:', data.prefix);

    // Clear any pending reconnect timer
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
      authState.reconnectTimer = null;
    }

    // Save session
    if (data.sessionId) {
      saveSession(data.sessionId);
    }

    // Start token validation (every 10 seconds)
    startTokenValidation();

    // Update UI with success (green checkmark)
    updateStatus('Connected', `Welcome, ${data.playerName || data.username}`);

    // Re-query elements if cached references are null
    const statusArea = dom.authStatusArea || document.getElementById('authStatusArea');
    const manualSection = dom.authManualSection || document.getElementById('authManualSection');

    if (statusArea) {
      statusArea.style.display = '';
      statusArea.classList.remove('error');
      statusArea.classList.add('success');
      dom.authStatusArea = statusArea;
    }
    if (manualSection) {
      manualSection.style.display = 'none';
      dom.authManualSection = manualSection;
    }

    // Play connection sound
    window.MX.sounds?.connect();

    // Show success toast
    showConnectionToast('ok', 'Connected', `Welcome, ${data.playerName || data.username}`);

    // Hide overlay after animation
    setTimeout(() => {
      hideAuthOverlay();
      window.dispatchEvent(new CustomEvent('mx:authenticated', { detail: data }));
    }, 800);

    // Safety fallback - ensure overlay is hidden after 2 seconds
    setTimeout(() => {
      const overlay = document.getElementById('authOverlay');
      if (overlay && overlay.style.display !== 'none') {
        console.log('[Auth] Safety fallback: forcing overlay hide');
        overlay.classList.add('hide');
        overlay.style.display = 'none';
      }
    }, 2000);
  }

  /**
   * Start periodic connection heartbeat (every 10 seconds)
   * Uses PING/PONG which the server already handles
   */
  function startTokenValidation() {
    stopTokenValidation();

    tokenValidationTimer = setInterval(() => {
      if (authState.connected && authState.status === AuthStatus.VERIFIED) {
        // Send heartbeat - server will disconnect us if session is invalid
        ws.send('HEARTBEAT', {
          timestamp: Date.now()
        });
        authState.lastValidation = Date.now();
      }
    }, TOKEN_VALIDATION_INTERVAL);

    console.log('[Auth] Started connection heartbeat (every 10s)');
  }

  /**
   * Stop token validation
   */
  function stopTokenValidation() {
    if (tokenValidationTimer) {
      clearInterval(tokenValidationTimer);
      tokenValidationTimer = null;
      console.log('[Auth] Stopped token validation');
    }
  }

  /**
   * Verify token before performing an action
   * Call this before any sensitive action (ban, settings change, etc.)
   * Returns true if allowed to proceed, false otherwise
   */
  function verifyBeforeAction(actionName) {
    if (authState.status !== AuthStatus.VERIFIED) {
      console.warn('[Auth] Action blocked - not verified:', actionName);
      window.MX.toast?.('error', 'Not Authenticated', 'Please log in to perform this action.');
      return false;
    }

    if (!authState.token || !authState.tokenValid) {
      console.warn('[Auth] Action blocked - token invalid:', actionName);
      window.MX.toast?.('error', 'Session Invalid', 'Your session has expired. Please log in again.');
      forceLogout('Session expired.');
      return false;
    }

    if (!authState.connected) {
      console.warn('[Auth] Action blocked - not connected:', actionName);
      window.MX.toast?.('error', 'Not Connected', 'Connection lost. Attempting to reconnect...');
      return false;
    }

    return true;
  }

  /**
   * Force logout the user
   */
  function forceLogout(message) {
    stopTokenValidation();
    authState.status = AuthStatus.UNAUTHENTICATED;
    authState.authenticated = false;
    authState.token = null;
    authState.tokenValid = false;
    authState.session = null;
    authState.connectionPhase = 'idle';

    // Don't clear saved token - user can re-authenticate
    clearSavedSession();

    ws.disconnect();
    showAuthOverlay();

    if (message) {
      showConnectionToast('warn', 'Logged Out', message);
      showManualAuth(message);
    }
  }

  /**
   * Schedule auto-reconnect with exponential backoff
   */
  function scheduleReconnect() {
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
    }

    if (authState.reconnectAttempts >= authState.maxReconnectAttempts) {
      console.log('[Auth] Max reconnect attempts reached');
      showConnectionToast('bad', 'Connection Failed', 'Unable to connect after multiple attempts');
      showManualAuth('Unable to connect. Please check your connection and try again.');
      return;
    }

    const delay = Math.min(1000 * Math.pow(2, authState.reconnectAttempts), 30000);
    authState.reconnectAttempts++;

    console.log(`[Auth] Reconnecting in ${delay}ms (attempt ${authState.reconnectAttempts}/${authState.maxReconnectAttempts})`);

    showAuthOverlay();
    updateStatus('Reconnecting...', `Attempt ${authState.reconnectAttempts} of ${authState.maxReconnectAttempts}`);
    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = '';
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }

    authState.reconnectTimer = setTimeout(() => {
      console.log('[Auth] Attempting reconnect...');
      startConnection();
    }, delay);
  }

  /**
   * Update status display
   */
  function updateStatus(text, subtitle = '') {
    if (dom.authStatusText) {
      dom.authStatusText.textContent = text;
    }
    if (dom.authStatusSub) {
      dom.authStatusSub.textContent = subtitle;
    }
  }

  /**
   * Check if input looks like a UUID
   */
  function isUUID(str) {
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
    return uuidRegex.test(str);
  }

  /**
   * Authenticate with permanent token (manual) or UUID (dev mode)
   */
  function authenticate() {
    const token = dom.authToken?.value?.trim();

    if (!token || token.length < 10) {
      showError('Please enter a valid token or UUID');
      showConnectionToast('warn', 'Invalid Input', 'Enter a token or UUID (for dev mode)');
      return;
    }

    clearError();
    setLoading(true);

    // Check if input is a UUID (dev authentication)
    const isDevUuidAuth = isUUID(token);

    if (isDevUuidAuth) {
      console.log('[Auth] Detected UUID input - using dev authentication');
      // Don't save UUID as token
    } else {
      // Save token with device fingerprint
      localStorage.setItem('mx_permanent_token', token);
      localStorage.setItem('mx_token_device', authState.deviceFingerprint);
      authState.token = token;
    }

    // Get host/port
    let host = authState.serverHost;
    let port = authState.serverPort;

    if (!authState.configLoaded) {
      host = dom.serverHost?.value?.trim() || window.location.hostname;
      port = parseInt(dom.serverPort?.value, 10) || 8081;
      authState.serverHost = host;
      authState.serverPort = port;
    }

    // Connect and authenticate
    if (ws.isConnected()) {
      authState.connectionPhase = 'authenticating';
      authState.status = AuthStatus.PENDING_VERIFICATION;

      if (isDevUuidAuth) {
        updateStatus('Authenticating...', 'Dev UUID authentication');
        ws.send('AUTH_DEV_UUID_LOGIN', {
          uuid: token,
          deviceFingerprint: authState.deviceFingerprint
        });
      } else {
        updateStatus('Authenticating...', 'Verifying token');
        ws.send('AUTH_PERMANENT_TOKEN', {
          token: token,
          deviceFingerprint: authState.deviceFingerprint
        });
      }

      setTimeout(() => {
        if (!authState.authenticated) {
          setLoading(false);
          showError('Authentication timed out');
        }
      }, 8000);
    } else {
      authState.connectionPhase = 'connecting';
      authState.status = AuthStatus.PENDING_VERIFICATION;
      updateStatus('Connecting...', 'Establishing connection');

      connectWebSocket().then(() => {
        if (isDevUuidAuth) {
          ws.send('AUTH_DEV_UUID_LOGIN', {
            uuid: token,
            deviceFingerprint: authState.deviceFingerprint
          });
        } else {
          ws.send('AUTH_PERMANENT_TOKEN', {
            token: token,
            deviceFingerprint: authState.deviceFingerprint
          });
        }
      }).catch((err) => {
        setLoading(false);
        authState.status = AuthStatus.UNAUTHENTICATED;
        showError(err.message || 'Connection failed');
        showConnectionToast('bad', 'Connection Failed', err.message || 'Could not connect to server');
      });
    }
  }

  /**
   * Show/hide overlays
   */
  function showAuthOverlay() {
    // Re-query element if cached reference is null
    const overlay = dom.authOverlay || document.getElementById('authOverlay');
    if (overlay) {
      overlay.classList.remove('hide');
      overlay.style.display = '';
      // Update cache
      dom.authOverlay = overlay;
    }
  }

  function hideAuthOverlay() {
    // Re-query element if cached reference is null
    const overlay = dom.authOverlay || document.getElementById('authOverlay');
    if (overlay) {
      overlay.classList.add('hide');
      // Update cache
      dom.authOverlay = overlay;
      setTimeout(() => {
        overlay.style.display = 'none';
      }, 400);
    } else {
      console.warn('[Auth] Could not find auth overlay to hide');
    }
  }

  function showAccessDenied(message) {
    hideAuthOverlay();
    stopTokenValidation();

    if (dom.accessDeniedOverlay) {
      dom.accessDeniedOverlay.classList.add('show');
    }

    clearSavedAuth();
    ws.disconnect();
  }

  /**
   * Gateway-specific UI functions
   */
  function showServerOffline(lastSeen) {
    // Create or show the server offline overlay
    let overlay = document.getElementById('serverOfflineOverlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'serverOfflineOverlay';
      overlay.className = 'auth-overlay show';
      overlay.innerHTML = `
        <div class="auth-modal" style="text-align: center;">
          <div class="status-icon" style="color: var(--warning); margin-bottom: 1rem;">
            <i class="fa-solid fa-plug-circle-xmark fa-3x"></i>
          </div>
          <h2 style="margin-bottom: 0.5rem;">Server Offline</h2>
          <p style="color: var(--text-secondary); margin-bottom: 1rem;">
            The Minecraft server has disconnected from the gateway.
          </p>
          <p id="serverOfflineLastSeen" style="color: var(--text-muted); font-size: 0.9rem;">
            ${lastSeen ? `Last seen: ${formatLastSeen(lastSeen)}` : 'Waiting for server to reconnect...'}
          </p>
          <div class="spinner" style="margin: 1.5rem auto; width: 32px; height: 32px;"></div>
          <p style="color: var(--text-muted); font-size: 0.85rem;">
            The panel will automatically reconnect when the server comes back online.
          </p>
        </div>
      `;
      document.body.appendChild(overlay);
    } else {
      overlay.classList.add('show');
      const lastSeenEl = overlay.querySelector('#serverOfflineLastSeen');
      if (lastSeenEl && lastSeen) {
        lastSeenEl.textContent = `Last seen: ${formatLastSeen(lastSeen)}`;
      }
    }
  }

  function hideServerOffline() {
    const overlay = document.getElementById('serverOfflineOverlay');
    if (overlay) {
      overlay.classList.remove('show');
      setTimeout(() => overlay.remove(), 300);
    }
  }

  function showGatewayError(title, message) {
    // Create or show the gateway error overlay
    let overlay = document.getElementById('gatewayErrorOverlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'gatewayErrorOverlay';
      overlay.className = 'auth-overlay show';
      overlay.innerHTML = `
        <div class="auth-modal" style="text-align: center;">
          <div class="status-icon" style="color: var(--danger); margin-bottom: 1rem;">
            <i class="fa-solid fa-triangle-exclamation fa-3x"></i>
          </div>
          <h2 id="gatewayErrorTitle" style="margin-bottom: 0.5rem;">${escapeHtml(title)}</h2>
          <p id="gatewayErrorMessage" style="color: var(--text-secondary); margin-bottom: 1.5rem;">
            ${escapeHtml(message)}
          </p>
          <a href="/" class="btn btn-primary">
            <i class="fa-solid fa-home"></i> Return Home
          </a>
        </div>
      `;
      document.body.appendChild(overlay);
    } else {
      overlay.classList.add('show');
      const titleEl = overlay.querySelector('#gatewayErrorTitle');
      const msgEl = overlay.querySelector('#gatewayErrorMessage');
      if (titleEl) titleEl.textContent = title;
      if (msgEl) msgEl.textContent = message;
    }
  }

  function formatLastSeen(timestamp) {
    if (!timestamp) return 'Unknown';
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins !== 1 ? 's' : ''} ago`;

    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours} hour${diffHours !== 1 ? 's' : ''} ago`;

    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays} day${diffDays !== 1 ? 's' : ''} ago`;
  }

  function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Error handling
   */
  function showError(message) {
    if (dom.authError) {
      dom.authError.textContent = message;
      dom.authError.style.display = 'block';
    }
  }

  function clearError() {
    if (dom.authError) {
      dom.authError.textContent = '';
      dom.authError.style.display = 'none';
    }
  }

  /**
   * Loading state
   */
  function setLoading(loading) {
    if (dom.authBtn) {
      dom.authBtn.disabled = loading;
      dom.authBtn.innerHTML = loading
        ? '<span class="spinner" style="width:16px;height:16px;border-width:2px"></span> Connecting...'
        : '<i class="fa-solid fa-right-to-bracket"></i> Connect';
    }
  }

  /**
   * Session management
   */
  function saveSession(sessionId) {
    try {
      localStorage.setItem('mx_session', sessionId);
    } catch (e) {}
  }

  function getSavedSession() {
    try {
      return localStorage.getItem('mx_session');
    } catch (e) {
      return null;
    }
  }

  function clearSavedSession() {
    try {
      localStorage.removeItem('mx_session');
    } catch (e) {}
  }

  function clearSavedAuth() {
    try {
      localStorage.removeItem('mx_auth');
      localStorage.removeItem('mx_session');
      localStorage.removeItem('mx_permanent_token');
      localStorage.removeItem('mx_token_device');
    } catch (e) {}
  }

  /**
   * Public API
   */
  function isAuthenticated() {
    return authState.authenticated && authState.status === AuthStatus.VERIFIED;
  }

  function isTokenValid() {
    return authState.tokenValid && authState.token !== null;
  }

  function getSession() {
    return authState.session;
  }

  function getToken() {
    return authState.token;
  }

  function getDeviceFingerprint() {
    return authState.deviceFingerprint;
  }

  function getConnectionPhase() {
    return authState.connectionPhase;
  }

  function getStatus() {
    return authState.status;
  }

  function logout() {
    stopTokenValidation();
    clearSavedAuth();

    // Notify server of logout
    if (authState.connected && authState.token) {
      ws.send('LOGOUT', { token: authState.token });
    }

    ws.disconnect();
    authState.authenticated = false;
    authState.status = AuthStatus.UNAUTHENTICATED;
    authState.token = null;
    authState.tokenValid = false;
    authState.session = null;
    authState.connectionPhase = 'idle';
    authState.reconnectAttempts = 0;

    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
      authState.reconnectTimer = null;
    }

    if (dom.authStatusArea) {
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }

    showAuthOverlay();
    showManualAuth();
  }

  /**
   * Manual reconnect
   */
  function reconnect() {
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
      authState.reconnectTimer = null;
    }

    authState.reconnectAttempts = 0;
    authState.connectionPhase = 'idle';
    authState.status = AuthStatus.UNAUTHENTICATED;

    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = '';
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }
    clearError();

    showAuthOverlay();

    ws.disconnect();
    setTimeout(() => {
      startConnection();
    }, 300);
  }

  // Initialize on DOM ready
  document.addEventListener('DOMContentLoaded', init);

  // Expose API
  window.MX = window.MX || {};
  window.MX.auth = {
    isAuthenticated,
    isTokenValid,
    getSession,
    getToken,
    getDeviceFingerprint,
    getConnectionPhase,
    getStatus,
    logout,
    reconnect,
    authenticate,
    verifyBeforeAction,
    AuthStatus
  };

})();

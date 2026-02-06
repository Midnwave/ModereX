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

  // ========== Secure Token Storage (AES-GCM encryption) ==========

  /**
   * Derive an AES-GCM key from the device fingerprint using Web Crypto API.
   */
  async function deriveEncryptionKey(fingerprint) {
    const encoder = new TextEncoder();
    const keyMaterial = await crypto.subtle.importKey(
      'raw', encoder.encode(fingerprint),
      { name: 'PBKDF2' }, false, ['deriveKey']
    );
    return crypto.subtle.deriveKey(
      { name: 'PBKDF2', salt: encoder.encode('mx_token_salt'), iterations: 100000, hash: 'SHA-256' },
      keyMaterial,
      { name: 'AES-GCM', length: 256 }, false, ['encrypt', 'decrypt']
    );
  }

  /**
   * Encrypt a token and store it in localStorage.
   */
  async function saveEncryptedToken(token, fingerprint) {
    try {
      const key = await deriveEncryptionKey(fingerprint);
      const iv = crypto.getRandomValues(new Uint8Array(12));
      const encoder = new TextEncoder();
      const encrypted = await crypto.subtle.encrypt(
        { name: 'AES-GCM', iv }, key, encoder.encode(token)
      );
      // Store IV + ciphertext as base64
      const combined = new Uint8Array(iv.length + encrypted.byteLength);
      combined.set(iv);
      combined.set(new Uint8Array(encrypted), iv.length);
      localStorage.setItem('mx_permanent_token_enc', btoa(String.fromCharCode(...combined)));
      // Remove old plaintext token if exists
      localStorage.removeItem('mx_permanent_token');
    } catch (e) {
      // Fallback: store plaintext if Web Crypto unavailable (e.g., HTTP without secure context)
      console.warn('[Auth] Web Crypto unavailable, storing token in plaintext');
      localStorage.setItem('mx_permanent_token', token);
    }
  }

  /**
   * Read and decrypt the token from localStorage.
   */
  async function loadEncryptedToken(fingerprint) {
    try {
      // Check for encrypted token first
      const encData = localStorage.getItem('mx_permanent_token_enc');
      if (encData) {
        const key = await deriveEncryptionKey(fingerprint);
        const combined = Uint8Array.from(atob(encData), c => c.charCodeAt(0));
        const iv = combined.slice(0, 12);
        const ciphertext = combined.slice(12);
        const decrypted = await crypto.subtle.decrypt(
          { name: 'AES-GCM', iv }, key, ciphertext
        );
        return new TextDecoder().decode(decrypted);
      }
      // Fallback: read old plaintext token and migrate it
      const plainToken = localStorage.getItem('mx_permanent_token');
      if (plainToken) {
        // Migrate to encrypted storage
        await saveEncryptedToken(plainToken, fingerprint);
        return plainToken;
      }
      return null;
    } catch (e) {
      // Decryption failed (different device/fingerprint changed) - require re-auth
      console.warn('[Auth] Token decryption failed - fingerprint may have changed');
      localStorage.removeItem('mx_permanent_token_enc');
      localStorage.removeItem('mx_permanent_token');
      return null;
    }
  }

  /**
   * Remove encrypted token from storage.
   */
  function removeEncryptedToken() {
    localStorage.removeItem('mx_permanent_token_enc');
    localStorage.removeItem('mx_permanent_token');
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
        showGatewayErrorPage('Invalid Server URL', 'Please check the URL and try again.');
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
        showGatewayErrorPage('Connection Failed', authState.lastError);
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
        showServerNotFound(serverId);
        resolve(); // Don't reject — we've shown the full page
      };

      const onGatewayError = (data) => {
        if (resolved) return;
        resolved = true;
        cleanup();
        if (data.code === 'SERVER_NOT_FOUND') {
          showServerNotFound(serverId);
          resolve();
        } else {
          reject(new Error(data.message || 'Gateway error'));
        }
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
   * Show the Cloudflare-style "Server Not Found" full page.
   */
  function showServerNotFound(serverId) {
    authState.connectionPhase = 'idle';
    authState.status = AuthStatus.UNAUTHENTICATED;

    const page = document.getElementById('serverNotFoundPage');
    if (page) {
      const serverIdEl = document.getElementById('notFoundServerId');
      if (serverIdEl) serverIdEl.textContent = serverId || 'unknown';

      const timestampEl = document.getElementById('notFoundTimestamp');
      if (timestampEl) timestampEl.textContent = new Date().toUTCString();

      page.classList.add('show');
    }

    // Hide auth overlay since we're showing a full page
    const authOverlay = document.getElementById('authOverlay');
    if (authOverlay) authOverlay.style.display = 'none';
  }

  /**
   * Show the Cloudflare-style "Gateway Error" full page.
   */
  function showGatewayErrorPage(title, message) {
    authState.connectionPhase = 'idle';
    authState.status = AuthStatus.UNAUTHENTICATED;

    const page = document.getElementById('gatewayErrorPage');
    if (page) {
      const titleEl = document.getElementById('gatewayErrorTitle');
      if (titleEl) titleEl.textContent = title || 'Gateway Error';

      const msgEl = document.getElementById('gatewayErrorMessage');
      if (msgEl) msgEl.textContent = message || 'The ModereX gateway encountered an error.';

      const timestampEl = document.getElementById('gatewayErrorTimestamp');
      if (timestampEl) timestampEl.textContent = new Date().toUTCString();

      page.classList.add('show');
    }

    // Hide auth overlay since we're showing a full page
    const authOverlay = document.getElementById('authOverlay');
    if (authOverlay) authOverlay.style.display = 'none';
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
  async function tryAuthenticate() {
    authState.connectionPhase = 'authenticating';
    authState.status = AuthStatus.PENDING_VERIFICATION;
    updateStatus('Authenticating...', 'Verifying credentials');

    const urlToken = authState.urlToken;
    const savedToken = await loadEncryptedToken(authState.deviceFingerprint);
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
        ws.send('AUTH_PERMANENT_TOKEN', {
          token: savedToken,
          deviceFingerprint: authState.deviceFingerprint
        });
      } else {
        console.log('[Auth] Different device - requiring manual auth');
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

    // In gateway mode, hide the server connection section (server is determined by URL)
    if (ws.isGatewayMode() && dom.serverSection) {
      dom.serverSection.style.display = 'none';
    }

    if (errorMsg) {
      showError(errorMsg);
    }

    // Load saved token into field (but user must re-enter if device trust is off)
    loadEncryptedToken(authState.deviceFingerprint).then(savedToken => {
      if (savedToken && dom.authToken) {
        dom.authToken.value = savedToken;
      }
    });
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

      // If gateway WS drops while server offline overlay is showing, use fixed 5s retry
      if (ws.isGatewayMode() && document.getElementById('serverOfflineOverlay')?.classList.contains('show')) {
        console.log('[Auth] Gateway WS disconnected while server offline - retrying in 5s');
        ws.scheduleServerOfflineRetry();
        return;
      }

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

      // Save token with device fingerprint (encrypted)
      if (authState.token) {
        saveEncryptedToken(authState.token, authState.deviceFingerprint);
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
        removeEncryptedToken();
        localStorage.removeItem('mx_token_device');
      } else if (data?.code === 'TOKEN_EXPIRED') {
        errorTitle = 'Token Expired';
        errorMessage = 'Your token has expired (90-day limit). Run /mx token in-game to generate a new one.';
        removeEncryptedToken();
        localStorage.removeItem('mx_token_device');
      } else if (data?.code === 'RATE_LIMITED') {
        errorTitle = 'Rate Limited';
        const waitSeconds = data?.waitSeconds || 0;
        errorMessage = waitSeconds > 0
          ? `Too many failed attempts. Please wait ${waitSeconds} seconds before trying again.`
          : 'Too many failed attempts. Please try again later.';
      } else if (data?.code === 'NO_PERMISSION') {
        errorTitle = 'No Permission';
        errorMessage = 'You need moderex.webpanel permission to access the panel';
      } else if (data?.code === 'SESSION_EXPIRED') {
        errorTitle = 'Session Expired';
        errorMessage = 'Your session has expired. Please authenticate again.';
        clearSavedSession();
      }

      setLoading(false);
      showConnectionToast('bad', errorTitle, errorMessage);
      showManualAuth(errorMessage);
    });

    // Auth challenge (CAPTCHA-like math question after failed attempts)
    ws.on('AUTH_CHALLENGE', (data) => {
      console.log('[Auth] Challenge required:', data?.question);
      if (window.devtoolsLog) window.devtoolsLog('AUTH', 'CAPTCHA challenge required', 'warn');

      authState.connectionPhase = 'idle';
      setLoading(false);
      showChallengeModal(data?.question || 'Solve the challenge to continue');
    });

    // Challenge solved - allow retry
    ws.on('AUTH_CHALLENGE_SOLVED', () => {
      console.log('[Auth] Challenge solved - retrying auth');
      if (window.devtoolsLog) window.devtoolsLog('AUTH', 'Challenge solved', 'success');
      hideChallengeModal();
      showConnectionToast('ok', 'Verified', 'Challenge solved. You can now try again.');
      showManualAuth();
    });

    ws.on('AUTH_CHALLENGE_FAILED', (data) => {
      console.log('[Auth] Challenge failed');
      if (window.devtoolsLog) window.devtoolsLog('AUTH', 'Challenge answer incorrect', 'error');
      // Show new challenge
      if (data?.newQuestion) {
        showChallengeModal(data.newQuestion);
        showConnectionToast('bad', 'Incorrect', 'Wrong answer. Try again.');
      }
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
      clearSavedAuth();
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

      // Show server offline overlay (no reconnecting text, silent wait)
      showServerOffline(data?.lastSeen);
    });

    ws.on('server_online', (data) => {
      console.log('[Auth] Server came back online via gateway');
      if (window.devtoolsLog) window.devtoolsLog('GATEWAY', 'Minecraft server reconnected to gateway', 'success');

      // Hide offline overlay
      hideServerOffline();

      // Re-establish connection state
      authState.connected = true;
      authState.connectionPhase = 'authenticating';

      // Try to re-authenticate with saved token
      if (authState.token) {
        console.log('[Auth] Auto-authenticating after server reconnect');
        tryAuthenticate();
      } else {
        // Try loading encrypted token
        loadEncryptedToken(authState.deviceFingerprint).then(token => {
          if (token) {
            authState.token = token;
            console.log('[Auth] Loaded encrypted token, auto-authenticating');
            tryAuthenticate();
          } else {
            // No saved token — show auth screen
            console.log('[Auth] No saved token, showing auth screen');
            showAuthOverlay();
            showManualAuth('Server reconnected. Please authenticate.');
          }
        }).catch(() => {
          showAuthOverlay();
          showManualAuth('Server reconnected. Please authenticate.');
        });
      }
    });

    ws.on('gateway_error', (data) => {
      console.error('[Auth] Gateway error:', data?.code, data?.message);
      if (window.devtoolsLog) window.devtoolsLog('GATEWAY', `Error: ${data?.message || data?.code}`, 'error');

      if (data?.code === 'SERVER_NOT_FOUND') {
        showServerNotFound(ws.getServerId?.() || ws.getServerIdFromPath?.() || '');
      } else if (data?.code === 'SERVER_OFFLINE') {
        showServerOffline(data?.lastSeen);
      } else {
        showGatewayErrorPage('Gateway Error', data?.message || 'An error occurred connecting to the server.');
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
      // Save token with device fingerprint (encrypted)
      saveEncryptedToken(token, authState.deviceFingerprint);
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
   * Gateway-specific UI functions — use static HTML overlays from index.html
   */
  function showServerOffline(lastSeen) {
    const overlay = document.getElementById('serverOfflineOverlay');
    if (overlay) {
      overlay.classList.add('show');

      const statusEl = document.getElementById('offlineReconnectStatus');
      if (statusEl) {
        statusEl.textContent = lastSeen
          ? `Last seen: ${formatLastSeen(lastSeen)}`
          : 'Waiting for server...';
      }
    }

    // Hide other overlays
    const disconnectOverlay = document.getElementById('disconnectOverlay');
    if (disconnectOverlay) disconnectOverlay.classList.remove('show');

    // Hide auth overlay
    const authOverlay = document.getElementById('authOverlay');
    if (authOverlay) authOverlay.style.display = 'none';
  }

  function hideServerOffline() {
    const overlay = document.getElementById('serverOfflineOverlay');
    if (overlay) overlay.classList.remove('show');
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
      sessionStorage.setItem('mx_session', sessionId);
    } catch (e) {}
  }

  function getSavedSession() {
    try {
      return sessionStorage.getItem('mx_session');
    } catch (e) {
      return null;
    }
  }

  function clearSavedSession() {
    try {
      sessionStorage.removeItem('mx_session');
    } catch (e) {}
  }

  function clearSavedAuth() {
    try {
      localStorage.removeItem('mx_auth');
      localStorage.removeItem('mx_token_device');
      removeEncryptedToken();
      clearSavedSession();
    } catch (e) {}
  }

  // ========== Challenge Modal (Anti-brute-force CAPTCHA) ==========

  /**
   * Show the math challenge modal
   */
  function showChallengeModal(question) {
    let modal = document.getElementById('authChallengeModal');
    if (!modal) {
      modal = document.createElement('div');
      modal.id = 'authChallengeModal';
      modal.className = 'auth-challenge-overlay';
      modal.innerHTML = `
        <div class="auth-challenge-modal">
          <div class="auth-challenge-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            </svg>
          </div>
          <h3>Security Verification</h3>
          <p class="auth-challenge-desc">Too many failed attempts. Solve this to continue:</p>
          <p class="auth-challenge-question" id="challengeQuestion"></p>
          <input type="text" id="challengeAnswer" class="auth-challenge-input" placeholder="Your answer" autocomplete="off" />
          <p class="auth-challenge-error" id="challengeError" style="display:none;"></p>
          <button class="btn btn-primary auth-challenge-btn" id="challengeSubmitBtn">
            <i class="fa-solid fa-check"></i> Submit
          </button>
        </div>
      `;
      document.body.appendChild(modal);

      // Event listeners
      document.getElementById('challengeSubmitBtn').addEventListener('click', submitChallenge);
      document.getElementById('challengeAnswer').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') submitChallenge();
      });
    }

    document.getElementById('challengeQuestion').textContent = question;
    document.getElementById('challengeAnswer').value = '';
    document.getElementById('challengeError').style.display = 'none';
    modal.classList.add('show');

    setTimeout(() => document.getElementById('challengeAnswer')?.focus(), 100);
  }

  /**
   * Hide the challenge modal
   */
  function hideChallengeModal() {
    const modal = document.getElementById('authChallengeModal');
    if (modal) {
      modal.classList.remove('show');
      setTimeout(() => modal.remove(), 300);
    }
  }

  /**
   * Submit challenge answer
   */
  function submitChallenge() {
    const answer = document.getElementById('challengeAnswer')?.value?.trim();
    if (!answer) {
      const errorEl = document.getElementById('challengeError');
      if (errorEl) {
        errorEl.textContent = 'Please enter an answer';
        errorEl.style.display = 'block';
      }
      return;
    }

    ws.send('AUTH_CHALLENGE_RESPONSE', { answer });
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

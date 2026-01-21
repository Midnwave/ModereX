/* ============================================
   ModereX Control Panel - Authentication
   ============================================ */
(function() {
  'use strict';

  const { $ } = window.MX.utils;
  const ws = window.MX.ws;

  // Auth state
  let authState = {
    connected: false,
    authenticated: false,
    accessDenied: false,
    session: null,
    serverHost: null,
    serverPort: null,
    configLoaded: false,
    urlToken: null,
    reconnectTimer: null,
    reconnectAttempts: 0,
    maxReconnectAttempts: 10,
    lastError: null,
    connectionPhase: 'idle' // idle, connecting, authenticating, connected
  };

  // DOM Elements
  let dom = {};

  /**
   * Show connection toast alert (watchlist alert style)
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
      authState.lastError = err.message || 'Connection failed';

      showConnectionToast('bad', 'Connection Failed', authState.lastError);
      showManualAuth(authState.lastError);
      scheduleReconnect();
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
   */
  function tryAuthenticate() {
    authState.connectionPhase = 'authenticating';
    updateStatus('Authenticating...', 'Verifying credentials');

    // Priority: URL token > Saved token > Session > Trusted device
    const urlToken = authState.urlToken;
    const savedToken = localStorage.getItem('mx_permanent_token');
    const savedSession = getSavedSession();

    if (urlToken) {
      console.log('[Auth] Authenticating with URL token');
      updateStatus('Authenticating...', 'Verifying link token');
      ws.authWithUrlToken(urlToken);
      authState.urlToken = null; // Clear after use
    } else if (savedToken) {
      console.log('[Auth] Authenticating with saved token');
      updateStatus('Authenticating...', 'Verifying saved token');
      ws.authWithToken(savedToken);
    } else if (savedSession) {
      console.log('[Auth] Authenticating with session');
      updateStatus('Authenticating...', 'Resuming session');
      ws.authWithSession(savedSession);
    } else {
      console.log('[Auth] Trying trusted device auth');
      updateStatus('Authenticating...', 'Checking device trust');
      ws.authWithTrustedDevice();
    }

    // Set auth timeout
    setTimeout(() => {
      if (authState.connectionPhase === 'authenticating' && !authState.authenticated) {
        console.log('[Auth] Authentication timeout - showing manual auth');
        showManualAuth('Authentication timed out');
      }
    }, 5000);
  }

  /**
   * Show manual authentication form
   */
  function showManualAuth(errorMsg) {
    authState.connectionPhase = 'idle';

    // Hide status area and show manual section
    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = 'none';
    }

    // Show manual auth section
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'block';
    }

    // Show error if provided
    if (errorMsg) {
      showError(errorMsg);
    }

    // Load saved token into field
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
    });

    ws.on('disconnected', (data) => {
      const wasAuthenticated = authState.authenticated;
      authState.connected = false;
      authState.authenticated = false;
      authState.connectionPhase = 'idle';

      console.log('[Auth] Disconnected:', data.code, data.reason);

      // Handle access denied - don't reconnect
      if (data.code === 4001 || data.code === 4003) {
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

      // Auto-reconnect
      scheduleReconnect();
    });

    ws.on('auth_success', (data) => {
      console.log('[Auth] Authentication successful:', data.playerName || data.username);

      authState.authenticated = true;
      authState.session = data;
      authState.accessDenied = false;
      authState.connectionPhase = 'connected';
      authState.reconnectAttempts = 0;
      authState.lastError = null;

      // Clear any pending reconnect timer
      if (authState.reconnectTimer) {
        clearTimeout(authState.reconnectTimer);
        authState.reconnectTimer = null;
      }

      // Save session
      if (data.sessionId) {
        saveSession(data.sessionId);
      }

      // Save permanent token if provided
      if (data.permanentToken) {
        localStorage.setItem('mx_permanent_token', data.permanentToken);
      }

      // Update UI with success
      updateStatus('Connected', `Welcome, ${data.playerName || data.username}`);
      if (dom.authStatusArea) {
        dom.authStatusArea.style.display = '';
        dom.authStatusArea.classList.remove('error');
        dom.authStatusArea.classList.add('success');
      }
      if (dom.authManualSection) {
        dom.authManualSection.style.display = 'none';
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
    });

    ws.on('auth_failed', (data) => {
      console.log('[Auth] Authentication failed:', data?.message);

      authState.authenticated = false;
      authState.connectionPhase = 'idle';
      authState.lastError = data?.message || 'Authentication failed';

      // Determine specific error
      let errorTitle = 'Authentication Failed';
      let errorMessage = data?.message || 'Invalid credentials';

      if (data?.code === 'INVALID_TOKEN') {
        errorTitle = 'Invalid Token';
        errorMessage = 'The token is invalid or expired. Get a new one with /mx gettoken';
        localStorage.removeItem('mx_permanent_token');
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
      authState.accessDenied = true;
      authState.authenticated = false;
      authState.connectionPhase = 'idle';

      showConnectionToast('bad', 'Access Denied', data?.message || 'You do not have permission to access the panel');
      showAccessDenied(data?.message);
    });

    ws.on('session_expired', () => {
      console.log('[Auth] Session expired');
      clearSavedSession();
      authState.authenticated = false;
      authState.connectionPhase = 'idle';

      showConnectionToast('warn', 'Session Expired', 'Please log in again');
      showAuthOverlay();
      showManualAuth('Session expired. Please log in again.');
    });

    // Handle ping for connection health
    ws.on('ping_update', (data) => {
      if (data.ping === -1) {
        console.warn('[Auth] Connection unstable');
      }
    });
  }

  /**
   * Schedule auto-reconnect with exponential backoff
   */
  function scheduleReconnect() {
    // Clear any existing timer
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
    }

    // Check max attempts
    if (authState.reconnectAttempts >= authState.maxReconnectAttempts) {
      console.log('[Auth] Max reconnect attempts reached');
      showConnectionToast('bad', 'Connection Failed', 'Unable to connect after multiple attempts');
      showManualAuth('Unable to connect. Please check your connection and try again.');
      return;
    }

    // Exponential backoff: 1s, 2s, 4s, 8s, 16s, max 30s
    const delay = Math.min(1000 * Math.pow(2, authState.reconnectAttempts), 30000);
    authState.reconnectAttempts++;

    console.log(`[Auth] Reconnecting in ${delay}ms (attempt ${authState.reconnectAttempts}/${authState.maxReconnectAttempts})`);

    // Show reconnecting status
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
   * Authenticate with permanent token (manual)
   */
  function authenticate() {
    const token = dom.authToken?.value?.trim();

    if (!token || token.length < 10) {
      showError('Please enter a valid token');
      showConnectionToast('warn', 'Invalid Token', 'Token must be at least 10 characters');
      return;
    }

    clearError();
    setLoading(true);

    // Save token
    localStorage.setItem('mx_permanent_token', token);

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
      updateStatus('Authenticating...', 'Verifying token');
      ws.authWithToken(token);

      // Timeout for manual auth
      setTimeout(() => {
        if (!authState.authenticated) {
          setLoading(false);
          showError('Authentication timed out');
        }
      }, 5000);
    } else {
      authState.connectionPhase = 'connecting';
      updateStatus('Connecting...', 'Establishing connection');

      connectWebSocket().then(() => {
        ws.authWithToken(token);
      }).catch((err) => {
        setLoading(false);
        showError(err.message || 'Connection failed');
        showConnectionToast('bad', 'Connection Failed', err.message || 'Could not connect to server');
      });
    }
  }

  /**
   * Show/hide overlays
   */
  function showAuthOverlay() {
    if (dom.authOverlay) {
      dom.authOverlay.classList.remove('hide');
      dom.authOverlay.style.display = '';
    }
  }

  function hideAuthOverlay() {
    if (dom.authOverlay) {
      dom.authOverlay.classList.add('hide');
      setTimeout(() => {
        dom.authOverlay.style.display = 'none';
      }, 400);
    }
  }

  function showAccessDenied(message) {
    hideAuthOverlay();

    if (dom.accessDeniedOverlay) {
      dom.accessDeniedOverlay.classList.add('show');
    }

    clearSavedAuth();
    ws.disconnect();
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
    } catch (e) {}
  }

  /**
   * Public API
   */
  function isAuthenticated() {
    return authState.authenticated;
  }

  function getSession() {
    return authState.session;
  }

  function getConnectionPhase() {
    return authState.connectionPhase;
  }

  function logout() {
    clearSavedAuth();
    ws.disconnect();
    authState.authenticated = false;
    authState.session = null;
    authState.connectionPhase = 'idle';
    authState.reconnectAttempts = 0;

    // Clear reconnect timer
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
      authState.reconnectTimer = null;
    }

    // Reset UI
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
    // Clear any pending timers
    if (authState.reconnectTimer) {
      clearTimeout(authState.reconnectTimer);
      authState.reconnectTimer = null;
    }

    authState.reconnectAttempts = 0;
    authState.connectionPhase = 'idle';

    // Reset UI
    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = '';
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }
    clearError();

    // Show auth overlay
    showAuthOverlay();

    // Disconnect and restart
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
    getSession,
    getConnectionPhase,
    logout,
    reconnect,
    authenticate
  };

})();

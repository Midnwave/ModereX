/* ============================================
   ModereX Control Panel - Authentication
   ============================================ */
(function() {
  'use strict';

  const { $ } = window.MX.utils;
  const ws = window.MX.ws;

  // Auth state
  let authState = {
    mode: 'minecraft',    // 'minecraft' | 'token'
    connected: false,
    authenticated: false,
    accessDenied: false,
    session: null,
    serverHost: null,
    serverPort: null,
    configLoaded: false,
    urlToken: null,
    autoAuthAttempted: false
  };

  // DOM Elements
  let dom = {};

  /**
   * Initialize auth module
   */
  function init() {
    cacheDom();
    checkUrlToken();
    setupEventListeners();
    setupWebSocketHandlers();
    loadServerConfig();
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
   * Load server configuration
   */
  async function loadServerConfig() {
    updateStatus('Connecting...');

    try {
      const response = await fetch('/api/config');
      if (!response.ok) {
        throw new Error('Config load failed');
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

      // Try auto-authentication
      tryAutoAuth();

    } catch (err) {
      console.error('[Auth] Config load failed:', err);
      authState.configLoaded = false;

      // Show manual auth
      showManualAuth('Could not connect to server');
    }
  }

  /**
   * Try automatic authentication methods
   */
  function tryAutoAuth() {
    const host = authState.serverHost;
    const port = authState.serverPort;

    // 1. URL token (from /mx connect link)
    if (authState.urlToken) {
      updateStatus('Connecting...', 'Preparing secure connection');
      connectAndAuth(() => {
        updateStatus('Authorizing...', 'Verifying your token');
        ws.authWithUrlToken(authState.urlToken);
      }, () => {
        authState.urlToken = null;
        tryNextAuthMethod();
      });
      return;
    }

    // 2. Saved session
    const savedSession = getSavedSession();
    if (savedSession) {
      updateStatus('Connecting...', 'Resuming session');
      connectAndAuth(() => {
        updateStatus('Authorizing...', 'Validating session');
        ws.authWithSession(savedSession);
      }, () => {
        clearSavedSession();
        tryNextAuthMethod();
      });
      return;
    }

    // 3. Saved permanent token
    const savedToken = localStorage.getItem('mx_permanent_token');
    if (savedToken) {
      updateStatus('Connecting...', 'Preparing secure connection');
      connectAndAuth(() => {
        updateStatus('Authorizing...', 'Validating token');
        ws.authWithToken(savedToken);
      }, () => {
        localStorage.removeItem('mx_permanent_token');
        tryNextAuthMethod();
      });
      return;
    }

    // 4. Trusted device
    updateStatus('Connecting...', 'Checking device trust');
    connectAndAuth(() => {
      updateStatus('Authorizing...', 'Verifying device');
      ws.authWithTrustedDevice();
    }, () => {
      // All auto-auth methods failed
      showManualAuth();
    });
  }

  /**
   * Try next auth method after failure
   */
  function tryNextAuthMethod() {
    const savedSession = getSavedSession();
    const savedToken = localStorage.getItem('mx_permanent_token');

    if (!authState.urlToken && savedSession) {
      updateStatus('Connecting...');
      ws.authWithSession(savedSession);
    } else if (!savedSession && savedToken) {
      updateStatus('Authenticating...');
      ws.authWithToken(savedToken);
    } else if (!savedToken) {
      updateStatus('Connecting...');
      ws.authWithTrustedDevice();
    } else {
      showManualAuth();
    }
  }

  /**
   * Connect and authenticate
   */
  function connectAndAuth(authFn, failFn) {
    const host = authState.serverHost;
    const port = authState.serverPort;

    let timeoutId = null;
    let cleanedUp = false;

    const cleanup = () => {
      if (cleanedUp) return;
      cleanedUp = true;
      if (timeoutId) clearTimeout(timeoutId);
      ws.off('connected', onConnected);
      ws.off('auth_failed', onFailed);
      ws.off('error', onError);
    };

    // One-time handlers
    const onConnected = () => {
      cleanup();
      authFn();
      // Set auth-specific timeout after connection
      timeoutId = setTimeout(() => {
        if (!authState.authenticated && failFn) {
          console.log('[Auth] Auth response timeout');
          failFn();
        }
      }, 3000);
    };

    const onFailed = (data) => {
      cleanup();
      console.log('[Auth] Auth failed:', data?.message);
      if (failFn) failFn();
    };

    const onError = () => {
      cleanup();
      console.log('[Auth] Connection error');
      if (failFn) failFn();
    };

    ws.on('connected', onConnected);
    ws.on('auth_failed', onFailed);
    ws.on('error', onError);

    // Set connection timeout
    timeoutId = setTimeout(() => {
      if (!authState.authenticated && !cleanedUp) {
        console.log('[Auth] Connection timeout');
        cleanup();
        if (failFn) failFn();
      }
    }, 4000);

    if (!ws.isConnected()) {
      ws.connect(host, port);
    } else {
      cleanup();
      authFn();
      // Set auth-specific timeout for already connected
      timeoutId = setTimeout(() => {
        if (!authState.authenticated && failFn) {
          console.log('[Auth] Auth response timeout (already connected)');
          failFn();
        }
      }, 3000);
    }
  }

  /**
   * Show manual authentication form
   */
  function showManualAuth(errorMsg) {
    authState.autoAuthAttempted = true;

    // Hide status area completely and show manual section
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

    // Load saved preferences
    loadSavedConnection();
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
      authModeSelect: $('#authModeSelect'),
      authMinecraftSection: $('#authMinecraftSection'),
      authTokenSection: $('#authTokenSection'),
      connectCode: $('#connectCode'),
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
    dom.authModeSelect?.addEventListener('change', (e) => {
      setAuthMode(e.target.value);
    });

    dom.authBtn?.addEventListener('click', () => {
      authenticate();
    });

    dom.connectCode?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') authenticate();
      setTimeout(() => {
        dom.connectCode.value = dom.connectCode.value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 6);
      }, 0);
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

    // Set default mode
    setAuthMode('minecraft');
  }

  /**
   * Setup WebSocket event handlers
   */
  function setupWebSocketHandlers() {
    ws.on('connected', () => {
      authState.connected = true;
    });

    ws.on('disconnected', (data) => {
      authState.connected = false;
      authState.authenticated = false;

      if (data.code === 4001 || data.code === 4003) {
        showAccessDenied();
      }
    });

    ws.on('auth_success', (data) => {
      authState.authenticated = true;
      authState.session = data;
      authState.accessDenied = false;

      // Save session
      if (data.sessionId) {
        saveSession(data.sessionId);
      }

      // Save permanent token if provided
      if (data.permanentToken) {
        localStorage.setItem('mx_permanent_token', data.permanentToken);
      }

      // Update UI with success animation
      updateStatus('Connected', `Welcome back, ${data.playerName || data.username}`);
      if (dom.authStatusArea) {
        dom.authStatusArea.classList.remove('error');
        dom.authStatusArea.classList.add('success');
      }

      // Play connection sound
      window.MX.sounds?.connect();

      // Hide overlay after animation completes
      setTimeout(() => {
        hideAuthOverlay();
        window.dispatchEvent(new CustomEvent('mx:authenticated', { detail: data }));
        window.MX.toast?.('ok', 'Connected', `Welcome, ${data.playerName || data.username}`);
      }, 1200);
    });

    ws.on('auth_failed', (data) => {
      authState.authenticated = false;

      // If manual auth was attempted, show error
      if (authState.autoAuthAttempted && dom.authManualSection?.style.display !== 'none') {
        showError(data.message || 'Authentication failed');
        setLoading(false);
      }
    });

    ws.on('access_denied', (data) => {
      authState.accessDenied = true;
      authState.authenticated = false;
      showAccessDenied(data.message);
    });

    ws.on('session_expired', () => {
      clearSavedSession();
      authState.authenticated = false;
      showAuthOverlay();
      showManualAuth('Session expired. Please log in again.');
    });
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
   * Set authentication mode
   */
  function setAuthMode(mode) {
    authState.mode = mode;

    if (dom.authMinecraftSection) {
      dom.authMinecraftSection.style.display = mode === 'minecraft' ? 'block' : 'none';
    }
    if (dom.authTokenSection) {
      dom.authTokenSection.style.display = mode === 'token' ? 'block' : 'none';
    }

    clearError();
  }

  /**
   * Authenticate based on current mode
   */
  function authenticate() {
    const mode = authState.mode || dom.authModeSelect?.value || 'minecraft';

    let host, port;
    if (authState.configLoaded) {
      host = authState.serverHost;
      port = authState.serverPort;
    } else {
      host = dom.serverHost?.value?.trim() || 'localhost';
      port = parseInt(dom.serverPort?.value, 10) || 8081;
      authState.serverHost = host;
      authState.serverPort = port;
    }

    clearError();
    setLoading(true);

    if (mode === 'minecraft') {
      authenticateMinecraft(host, port);
    } else if (mode === 'token') {
      authenticateToken(host, port);
    }
  }

  /**
   * Authenticate with connect code
   */
  function authenticateMinecraft(host, port) {
    const code = dom.connectCode?.value?.trim().toUpperCase();

    if (!code || code.length !== 6) {
      showError('Please enter a valid 6-character connect code');
      setLoading(false);
      return;
    }

    ws.on('connected', function onConnect() {
      ws.off('connected', onConnect);
      ws.authWithCode(code);
    });

    ws.on('auth_failed', function onFail(data) {
      ws.off('auth_failed', onFail);
      showError(data.message || 'Invalid connect code');
      setLoading(false);
    });

    if (!ws.isConnected()) {
      ws.connect(host, port);
    } else {
      ws.authWithCode(code);
    }
  }

  /**
   * Authenticate with permanent token
   */
  function authenticateToken(host, port) {
    const token = dom.authToken?.value?.trim();

    if (!token || token.length < 10) {
      showError('Please enter a valid token');
      setLoading(false);
      return;
    }

    localStorage.setItem('mx_permanent_token', token);

    ws.on('connected', function onConnect() {
      ws.off('connected', onConnect);
      ws.authWithToken(token);
    });

    ws.on('auth_failed', function onFail(data) {
      ws.off('auth_failed', onFail);
      showError(data.message || 'Invalid token');
      setLoading(false);
    });

    if (!ws.isConnected()) {
      ws.connect(host, port);
    } else {
      ws.authWithToken(token);
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

  function loadSavedConnection() {
    try {
      const saved = localStorage.getItem('mx_auth');
      if (saved) {
        const data = JSON.parse(saved);
        if (dom.serverHost && data.host) dom.serverHost.value = data.host;
        if (dom.serverPort && data.port) dom.serverPort.value = data.port;
        if (dom.authModeSelect && data.mode) {
          dom.authModeSelect.value = data.mode;
          setAuthMode(data.mode);
        }
      }
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

  function logout() {
    clearSavedAuth();
    ws.disconnect();
    authState.authenticated = false;
    authState.session = null;
    authState.autoAuthAttempted = false;

    // Reset UI
    if (dom.authStatusArea) {
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }

    showAuthOverlay();
    loadServerConfig();
  }

  /**
   * Reconnect after disconnect
   */
  function reconnect() {
    authState.autoAuthAttempted = false;

    // Reset UI
    if (dom.authStatusArea) {
      dom.authStatusArea.style.display = '';
      dom.authStatusArea.classList.remove('error', 'success');
    }
    if (dom.authManualSection) {
      dom.authManualSection.style.display = 'none';
    }
    clearError();

    // Show auth overlay with connecting status
    showAuthOverlay();
    updateStatus('Reconnecting...');

    // Disconnect and retry
    ws.disconnect();
    setTimeout(() => {
      loadServerConfig();
    }, 500);
  }

  // Initialize on DOM ready
  document.addEventListener('DOMContentLoaded', init);

  // Expose API
  window.MX = window.MX || {};
  window.MX.auth = {
    isAuthenticated,
    getSession,
    logout,
    reconnect,
    setAuthMode,
    authenticate
  };

})();

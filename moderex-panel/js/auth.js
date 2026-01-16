/* ============================================
   ModereX Control Panel - Authentication
   ============================================ */
(function() {
  'use strict';

  const { $ } = window.MX.utils;
  const ws = window.MX.ws;

  // Auth state
  let authState = {
    mode: null,           // 'minecraft' | 'console' | null
    connected: false,
    authenticated: false,
    accessDenied: false,
    session: null,
    serverHost: null,     // Auto-detected from server
    serverPort: null,     // Auto-detected from server (WebSocket port)
    configLoaded: false
  };

  // DOM Elements (cached after init)
  let dom = {};

  /**
   * Initialize auth module
   */
  function init() {
    cacheDom();
    setupEventListeners();
    setupWebSocketHandlers();
    loadServerConfig();
  }

  /**
   * Load server configuration from /api/config endpoint
   */
  async function loadServerConfig() {
    try {
      // Show loading state
      if (dom.authStatus) {
        dom.authStatus.textContent = 'Loading server config...';
        dom.authStatus.className = 'auth-status pending';
      }

      const response = await fetch('/api/config');
      if (!response.ok) {
        throw new Error('Failed to load config: ' + response.status);
      }

      const config = await response.json();
      authState.serverHost = config.host || window.location.hostname;
      authState.serverPort = config.wsPort || 8081;
      authState.configLoaded = true;

      // Update UI with server info
      if (dom.authStatus) {
        dom.authStatus.textContent = `Server: ${config.serverName || 'ModereX'} v${config.serverVersion || '?'}`;
        dom.authStatus.className = 'auth-status ok';
      }

      // Hide host/port inputs since we auto-detected
      if (dom.serverHost) dom.serverHost.parentElement.style.display = 'none';
      if (dom.serverPort) dom.serverPort.parentElement.style.display = 'none';

      // Load any saved auth preferences
      loadSavedConnection();
      showAuthOverlay();

    } catch (err) {
      console.error('[Auth] Failed to load server config:', err);

      // Fall back to manual entry mode
      authState.configLoaded = false;
      if (dom.authStatus) {
        dom.authStatus.textContent = 'Enter server details manually';
        dom.authStatus.className = 'auth-status warn';
      }

      // Show host/port inputs for manual entry
      if (dom.serverHost) dom.serverHost.parentElement.style.display = '';
      if (dom.serverPort) dom.serverPort.parentElement.style.display = '';

      loadSavedConnection();
      showAuthOverlay();
    }
  }

  /**
   * Cache DOM elements
   */
  function cacheDom() {
    dom = {
      authOverlay: $('#authOverlay'),
      accessDeniedOverlay: $('#accessDeniedOverlay'),
      authModeSelect: $('#authModeSelect'),
      authMinecraftSection: $('#authMinecraftSection'),
      authConsoleSection: $('#authConsoleSection'),
      connectCode: $('#connectCode'),
      consoleUsername: $('#consoleUsername'),
      serverHost: $('#serverHost'),
      serverPort: $('#serverPort'),
      authBtn: $('#authBtn'),
      authError: $('#authError'),
      authStatus: $('#authStatus'),
      connectOverlay: $('#connectOverlay'),
      connectingText: $('#connectingText')
    };
  }

  /**
   * Setup event listeners
   */
  function setupEventListeners() {
    // Mode selector
    dom.authModeSelect?.addEventListener('change', (e) => {
      setAuthMode(e.target.value);
    });

    // Auth button
    dom.authBtn?.addEventListener('click', () => {
      authenticate();
    });

    // Enter key in inputs
    dom.connectCode?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') authenticate();
      // Auto-uppercase connect code
      setTimeout(() => {
        dom.connectCode.value = dom.connectCode.value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 6);
      }, 0);
    });

    dom.consoleUsername?.addEventListener('keydown', (e) => {
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
      updateStatus('Connected to server', 'ok');

      // Auto-authenticate if we have saved credentials
      const saved = getSavedAuth();
      if (saved) {
        if (saved.mode === 'console' && saved.username) {
          ws.authAsConsole(saved.username);
        }
      }
    });

    ws.on('disconnected', (data) => {
      authState.connected = false;
      authState.authenticated = false;

      if (data.code === 4001 || data.code === 4003) {
        // Access denied - don't try to reconnect
        showAccessDenied();
      } else {
        updateStatus('Disconnected - Reconnecting...', 'error');
      }
    });

    ws.on('error', () => {
      updateStatus('Connection error', 'error');
    });

    ws.on('auth_success', (data) => {
      authState.authenticated = true;
      authState.session = data;
      authState.accessDenied = false;

      // Save successful auth
      saveAuth();

      // Hide auth overlay
      hideAuthOverlay();

      // Show connecting overlay briefly
      showConnecting('Loading data...');

      // Notify app of successful auth
      window.dispatchEvent(new CustomEvent('mx:authenticated', { detail: data }));

      setTimeout(() => {
        hideConnecting();
        window.MX.toast?.('ok', 'Authenticated', `Welcome, ${data.playerName || data.username}!`);
      }, 800);
    });

    ws.on('auth_failed', (data) => {
      authState.authenticated = false;
      showError(data.message || 'Authentication failed');
      setLoading(false);
    });

    ws.on('access_denied', (data) => {
      authState.accessDenied = true;
      authState.authenticated = false;
      showAccessDenied(data.message);
    });
  }

  /**
   * Set authentication mode
   */
  function setAuthMode(mode) {
    authState.mode = mode;

    if (dom.authMinecraftSection) {
      dom.authMinecraftSection.style.display = mode === 'minecraft' ? 'block' : 'none';
    }
    if (dom.authConsoleSection) {
      dom.authConsoleSection.style.display = mode === 'console' ? 'block' : 'none';
    }

    clearError();
  }

  /**
   * Authenticate based on current mode
   */
  function authenticate() {
    const mode = authState.mode || dom.authModeSelect?.value;
    if (!mode) {
      showError('Please select an authentication method');
      return;
    }

    // Use auto-detected values if available, otherwise use manual input
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
    } else if (mode === 'console') {
      authenticateConsole(host, port);
    }
  }

  /**
   * Authenticate with Minecraft connect code
   */
  function authenticateMinecraft(host, port) {
    const code = dom.connectCode?.value?.trim().toUpperCase();

    if (!code || code.length !== 6) {
      showError('Please enter a valid 6-character connect code');
      setLoading(false);
      return;
    }

    updateStatus('Connecting...', 'pending');

    // Connect to WebSocket and authenticate
    ws.on('connected', function onConnect() {
      ws.off('connected', onConnect);
      ws.authWithCode(code);
    });

    ws.connect(host, port);
  }

  /**
   * Authenticate as console user
   */
  function authenticateConsole(host, port) {
    const username = dom.consoleUsername?.value?.trim();

    if (!username || username.length < 3 || username.length > 16) {
      showError('Please enter a valid Minecraft username (3-16 characters)');
      setLoading(false);
      return;
    }

    // Validate username format
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      showError('Username can only contain letters, numbers, and underscores');
      setLoading(false);
      return;
    }

    authState.mode = 'console';
    updateStatus('Connecting...', 'pending');

    // Connect to WebSocket and authenticate
    ws.on('connected', function onConnect() {
      ws.off('connected', onConnect);
      ws.authAsConsole(username);
    });

    ws.connect(host, port);
  }

  /**
   * Show/hide auth overlay
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
      }, 300);
    }
  }

  /**
   * Show access denied screen
   */
  function showAccessDenied(message) {
    // Hide auth overlay
    hideAuthOverlay();

    // Show access denied overlay
    if (dom.accessDeniedOverlay) {
      dom.accessDeniedOverlay.classList.add('show');
    }

    // Clear any saved auth
    clearSavedAuth();

    // Disconnect WebSocket
    ws.disconnect();
  }

  /**
   * Show connecting overlay
   */
  function showConnecting(text) {
    if (dom.connectOverlay) {
      dom.connectOverlay.classList.add('show');
    }
    if (dom.connectingText) {
      dom.connectingText.textContent = text || 'Connecting...';
    }
  }

  function hideConnecting() {
    if (dom.connectOverlay) {
      dom.connectOverlay.classList.remove('show');
    }
  }

  /**
   * Show error message
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
   * Update status indicator
   */
  function updateStatus(text, state) {
    if (dom.authStatus) {
      dom.authStatus.textContent = text;
      dom.authStatus.className = 'auth-status ' + (state || '');
    }
  }

  /**
   * Set loading state
   */
  function setLoading(loading) {
    if (dom.authBtn) {
      dom.authBtn.disabled = loading;
      dom.authBtn.innerHTML = loading
        ? '<span class="spinner" style="width:16px;height:16px;border-width:2px"></span> Authenticating...'
        : '<i class="fa-solid fa-right-to-bracket"></i> Connect';
    }
  }

  /**
   * Save/load auth settings
   */
  function saveAuth() {
    try {
      const data = {
        mode: authState.mode,
        host: authState.serverHost,
        port: authState.serverPort,
        username: authState.mode === 'console' ? dom.consoleUsername?.value : null
      };
      localStorage.setItem('mx_auth', JSON.stringify(data));
    } catch (e) {
      console.warn('Failed to save auth:', e);
    }
  }

  function getSavedAuth() {
    try {
      const saved = localStorage.getItem('mx_auth');
      return saved ? JSON.parse(saved) : null;
    } catch (e) {
      return null;
    }
  }

  function clearSavedAuth() {
    try {
      localStorage.removeItem('mx_auth');
    } catch (e) {
      console.warn('Failed to clear auth:', e);
    }
  }

  function loadSavedConnection() {
    const saved = getSavedAuth();
    if (saved) {
      if (dom.serverHost && saved.host) {
        dom.serverHost.value = saved.host;
      }
      if (dom.serverPort && saved.port) {
        dom.serverPort.value = saved.port;
      }
      if (dom.authModeSelect && saved.mode) {
        dom.authModeSelect.value = saved.mode;
        setAuthMode(saved.mode);
      }
      if (dom.consoleUsername && saved.username) {
        dom.consoleUsername.value = saved.username;
      }
    }
  }

  /**
   * Check if authenticated
   */
  function isAuthenticated() {
    return authState.authenticated;
  }

  /**
   * Get current session data
   */
  function getSession() {
    return authState.session;
  }

  /**
   * Logout
   */
  function logout() {
    clearSavedAuth();
    ws.disconnect();
    authState.authenticated = false;
    authState.session = null;
    showAuthOverlay();
  }

  // Initialize on DOM ready
  document.addEventListener('DOMContentLoaded', init);

  // Expose API
  window.MX = window.MX || {};
  window.MX.auth = {
    isAuthenticated,
    getSession,
    logout,
    setAuthMode,
    authenticate
  };

})();

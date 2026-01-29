/* ============================================
   ModereX Control Panel - Main Application
   ============================================ */
(function() {
  const { $, $$, escapeHtml, fmtShort, fmtLong, fmtClock, uid, avatarUrl, now, pick, clamp } = window.MX.utils;
  const state = window.MX.state;
  const { createPunishment, initializeState, loadState, saveState } = window.MX.stateUtils;
  const ui = window.MX.ui;

  // Get DOM helper
  const dom = () => ui.getDom();
  const userKey = () => state.currentUser?.name || 'default';

  function loadUserPrefs() {
    const prefs = state.userPrefs?.[userKey()];
    if (!prefs) return;
    if (prefs.logsFilters) state.logsFilters = { ...state.logsFilters, ...prefs.logsFilters };
    if (typeof prefs.watchToasts === 'boolean') state.settings.watchToasts = prefs.watchToasts;
  }

  function saveUserPrefs() {
    state.userPrefs = state.userPrefs || {};
    state.userPrefs[userKey()] = {
      logsFilters: state.logsFilters,
      watchToasts: state.settings.watchToasts
    };
    saveState();
  }

  const repeatMemory = {};

  function normalizeMessage(msg) {
    return String(msg || '')
      .toLowerCase()
      .replace(/[^a-z0-9\s]/g, '')
      .replace(/\s+/g, ' ')
      .trim();
  }

  function isSimilarMessage(a, b) {
    if (!a || !b) return false;
    if (a === b) return true;
    const shorter = a.length <= b.length ? a : b;
    const longer = a.length <= b.length ? b : a;
    if (shorter.length < 3) return false;
    if (longer.startsWith(shorter) && (longer.length - shorter.length) <= 3) return true;
    let prefix = 0;
    for (let i = 0; i < Math.min(shorter.length, longer.length); i++) {
      if (shorter[i] !== longer[i]) break;
      prefix++;
    }
    return prefix >= Math.min(4, shorter.length);
  }

  function checkRepeat(rule, msg, playerId) {
    const key = `${playerId}:${rule.id}`;
    const mem = repeatMemory[key] || { lastMsg: '', count: 0, lastAt: 0 };
    const windowMs = (rule.threshold?.windowMins || 10) * 60000;
    const clean = normalizeMessage(msg);
    const nowTs = now();
    if (mem.lastAt && (nowTs - mem.lastAt) > windowMs) {
      mem.count = 0;
      mem.lastMsg = '';
    }
    const cond = (rule.conditions || []).find(c => c.kind === 'repeat') || {};
    const similarAllowed = !!cond.similar;
    const isRepeat = mem.lastMsg && (mem.lastMsg === clean || (similarAllowed && isSimilarMessage(mem.lastMsg, clean)));
    mem.count = isRepeat ? mem.count + 1 : 1;
    mem.lastMsg = clean;
    mem.lastAt = nowTs;
    repeatMemory[key] = mem;
    const required = Math.max(2, parseInt(rule.threshold?.hits || '2', 10));
    return mem.count >= required;
  }

  function updatePunishTitle(titleEl, type, playerId) {
    if (!titleEl) return;
    const p = state.players.find(x => x.id === playerId);
    titleEl.textContent = `${type} -= ${p?.name || 'Select Player'}`;
  }

  function renderEvidenceOptions(playerId, selectEl, previewEl) {
    const p = state.players.find(x => x.id === playerId);
    const evs = p ? state.evidence.filter(e => e.playerId === p.id) : [];
    if (selectEl) {
      selectEl.innerHTML = `<option value="">(none)</option>` + evs.map(e => {
        const label = `${fmtShort(e.createdAt)} | ${e.trigger} | ${e.message || 'No message'}`;
        return `<option value="${e.id}">${escapeHtml(label.slice(0, 120))}</option>`;
      }).join('');
    }
    if (previewEl) {
      previewEl.innerHTML = `<span style="color:var(--muted)">No evidence attached.</span>`;
    }
  }

  function updateEvidencePreviewFor(selectEl, previewEl) {
    const evId = selectEl?.value;
    const ev = state.evidence.find(e => e.id === evId);
    if (!previewEl) return;
    previewEl.innerHTML = ev ? `<b>${escapeHtml(ev.trigger)}</b><br><span style="color:var(--text-secondary)">${escapeHtml(ev.message || 'No message')}</span>` : `<span style="color:var(--muted)">No evidence attached.</span>`;
  }

  function executePunishment({ playerId, type, reason, duration, evidenceId }) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    const pun = createPunishment(p.id, type, reason, duration, state.staffName, evidenceId);
    state.punishments.push(pun);
    if (type === 'WARN') p.warnings = (p.warnings || 0) + 1;
    if (type === 'BAN' || type === 'MUTE') p.flags = clamp((p.flags || 0) + 1, 0, 9);
    state.activity.push({ t: now(), actor: state.staffName, action: `${type} (${duration || 'instant'})`, target: p.name });
    logPunishment(p.id, pun);
    maybeWatchAlert(p.id, `${type} executed`, `${p.name} | ${reason}`, type === 'BAN' ? 'ERROR' : 'WARN');
  }

  function evaluateAutomodMessage(playerId, msg) {
    const p = state.players.find(x => x.id === playerId);
    if (!p || !msg) return [];
    const hits = [];
    for (const r of state.rules.filter(x => x.enabled)) {
      for (const c of r.conditions) {
        let triggered = false;
        if (c.kind === 'contains' && c.value) {
          const m = normalizeMessage(msg);
          const parts = String(c.value).split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
          triggered = parts.some(part => c.match === 'exact' ? m === part : m.includes(part));
        }
        if (c.kind === 'caps' && c.value) {
          const upper = (msg.match(/[A-Z]/g) || []).length;
          const total = msg.replace(/\s/g, '').length;
          if (total > 0 && (upper / total) * 100 >= parseInt(c.value, 10)) triggered = true;
        }
        if (c.kind === 'link' && /https?:\/\//i.test(msg)) triggered = true;
        if (c.kind === 'regex' && c.value) {
          try { if (new RegExp(c.value, 'i').test(msg)) triggered = true; } catch {}
        }
        if (c.kind === 'repeat') {
          triggered = checkRepeat(r, msg, playerId);
        }
        if (triggered) { hits.push(r); break; }
      }
    }
    return hits;
  }

  function applyAutomodAction(playerId, rule, message) {
    if (!rule?.action || rule.action.kind === 'none') return;
    const type = rule.action.kind.toUpperCase();
    const reason = rule.action.extra || 'Automod rule triggered';
    const duration = rule.action.duration || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');
    const evidence = state.evidence.find(e => e.playerId === playerId && e.message === message);
    executePunishment({ playerId, type, reason, duration, evidenceId: evidence?.id || null });
    ui.renderPunishments();
    ui.renderPlayers();
  }

  function showConnectOverlay(show) {
    if (!dom().connectOverlay) return;
    dom().connectOverlay.classList.toggle('show', show);
  }

  function setPublishLoading(loading) {
    const btn = dom().publishBtn;
    if (!btn) return;
    btn.disabled = loading;
    btn.innerHTML = loading
      ? `<span class="spinner" style="width:16px;height:16px;border-width:2px"></span> Publishing...`
      : `<i class="fa-solid fa-cloud-arrow-up"></i> Publish`;
  }

  function copyToClipboard(text) {
    if (!text) return;
    if (navigator.clipboard?.writeText) {
      navigator.clipboard.writeText(text).catch(() => {});
      return;
    }
    const temp = document.createElement('input');
    temp.value = text;
    document.body.appendChild(temp);
    temp.select();
    document.execCommand('copy');
    temp.remove();
  }

  // ===== NAVIGATION =====
  window.go = function(page) {
    // Show loading line for page transition
    if (window.showLoadingLine) window.showLoadingLine();

    // Cleanup previous page if needed
    if (window.cleanupServerStatus && state.currentPage === 'status') {
      window.cleanupServerStatus();
    }
    if (window.cleanupReplayViewer && state.currentPage === 'replay') {
      window.cleanupReplayViewer();
    }

    state.currentPage = page;

    $$('.page').forEach(p => p.classList.remove('active'));
    const target = $(`#page-${page}`);
    if (target) target.classList.add('active');
    $$('.sb-item').forEach(item => item.classList.toggle('active', item.dataset.page === page));
    if (page !== 'livelogs') {
      state.manualPaused = false;
      state.autoPaused = false;
      state.logsFilters.page = 1;
    }
    if (page === 'punishments') ui.renderPunishments();
    if (page === 'players') ui.renderPlayers();
    if (page === 'watchlist') ui.renderWatchlist();
    if (page === 'livelogs') ui.renderLogs();
    if (page === 'automod') ui.renderRules();
    if (page === 'cmdblacklist') renderCmdBlacklist();
    if (page === 'anticheat') ui.renderAnticheat();
    if (page === 'templates') ui.renderTemplates();
    if (page === 'messages') ui.renderMessages();
    if (page === 'settings') ui.renderChatToggles();
    if (page === 'mysettings' && window.loadDevChecklist) window.loadDevChecklist();
    if (page === 'status' && window.initServerStatus) window.initServerStatus();
    if (page === 'replay' && window.initReplayViewer) window.initReplayViewer();
    if (page === 'devtools') {
      if (!state.settings?.developerMode) {
        toast('warn', 'Developer Mode Required', 'Enable Developer Mode to access this page.');
        go('dashboard');
        return;
      }
      loadDevChecklist();
    }

    // Hide loading line after a short delay for smooth transition
    setTimeout(() => {
      if (window.hideLoadingLine) window.hideLoadingLine();
    }, 300);
  };

  // ===== TEXT UTILITIES =====
  function truncateText(text, maxLen = 40) {
    if (!text) return '';
    text = String(text);
    return text.length > maxLen ? text.substring(0, maxLen) + '...' : text;
  }

  // Create truncated reason HTML - no click interaction, just displays truncated text with tooltip
  window.expandableReason = function(text, maxLen = 15) {
    if (!text) return '<span class="reason-text">No reason</span>';
    text = String(text);
    if (text.length <= maxLen) {
      return `<span class="reason-text">${escapeHtml(text)}</span>`;
    }
    const truncated = text.substring(0, maxLen);
    // Show truncated with ellipsis, use title attribute for full text on hover
    return `<span class="reason-text" title="${escapeHtml(text)}">${escapeHtml(truncated)}...</span>`;
  };

  // Keep toggleReason for backwards compatibility but it's no longer used
  window.toggleReason = function(id) {};

  // ===== CHARACTER COUNT =====
  window.updateCharCount = function(textarea, counterId, maxLen) {
    const counter = document.getElementById(counterId);
    if (counter) {
      const len = textarea.value.length;
      counter.textContent = `${len}/${maxLen}`;
      counter.style.color = len >= maxLen ? 'var(--bad)' : 'var(--text-secondary)';
    }
  };

  // ===== TOASTS =====
  const MAX_TOASTS = 5;

  window.toast = function(type, title, message, options = {}) {
    const ttl = options.ttl || 5000;
    const iconMap = { ok: 'fa-check', warn: 'fa-triangle-exclamation', bad: 'fa-xmark', info: 'fa-circle-info' };
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.innerHTML = `
      <div class="toast-icon"><i class="fa-solid ${iconMap[type] || iconMap.info}"></i></div>
      <div class="toast-content"><b>${escapeHtml(title)}</b><small>${escapeHtml(message)}</small></div>
      <button class="toast-close"><i class="fa-solid fa-xmark"></i></button>
    `;
    const dismiss = () => { el.classList.add('exit'); setTimeout(() => el.remove(), 200); };
    el.querySelector('.toast-close').onclick = dismiss;
    el.onclick = (e) => {
      if (e.target.closest('.toast-close')) return;
      if (options.playerId) openDrawer(options.playerId);
      dismiss();
    };

    // Get container - try cached first, then direct query as fallback
    const container = dom()?.toastContainer || document.getElementById('toastContainer');
    if (!container) {
      console.warn('[Toast] Toast container not found, skipping toast:', title);
      return;
    }

    // Enforce max toast limit - remove oldest if at capacity
    const existingToasts = container.querySelectorAll('.toast:not(.exit)');
    if (existingToasts.length >= MAX_TOASTS) {
      const oldest = existingToasts[0];
      if (oldest) {
        oldest.classList.add('exit');
        setTimeout(() => oldest.remove(), 200);
      }
    }

    container.appendChild(el);
    setTimeout(dismiss, ttl);

    // Play sound based on toast type (unless silent option is set)
    if (!options.silent && window.MX?.sounds) {
      switch (type) {
        case 'ok': window.MX.sounds.toastSuccess(); break;
        case 'warn': window.MX.sounds.toastWarning(); break;
        case 'bad': window.MX.sounds.toastError(); break;
        case 'info': window.MX.sounds.toastInfo(); break;
      }
    }
  };

  // Make toast available globally
  window.MX = window.MX || {};
  window.MX.toast = window.toast;

  // ===== ALERT TOASTS (Priority Notifications) =====
  const MAX_ALERT_TOASTS = 5;
  const alertIconMap = {
    ban: 'fa-gavel',
    kick: 'fa-shoe-prints',
    mute: 'fa-volume-xmark',
    warn: 'fa-triangle-exclamation',
    pardon: 'fa-hand-peace',
    anticheat: 'fa-shield-halved',
    automod: 'fa-robot',
    command: 'fa-terminal',
    nickname: 'fa-id-badge',
    watchlist: 'fa-eye',
    staffchat: 'fa-comments',
    lag: 'fa-gauge-high',
    punishments: 'fa-gavel',
    custom: 'fa-bell'
  };

  // Create alert toast container if not exists
  function getAlertContainer() {
    let container = document.getElementById('alertToastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'alertToastContainer';
      container.className = 'top-right'; // Default position
      document.body.appendChild(container);
    }
    return container;
  }

  // Update alert toast position based on settings
  window.updateAlertToastPosition = function(position) {
    const container = getAlertContainer();
    container.className = position || 'top-right';
    if (window.MX?.debug) {
      console.log('[AlertToast] Position updated to:', position);
    }
  };

  /**
   * Show an alert toast (priority notification) - AlertBar style
   * @param {string} alertType - Type of alert (ban, kick, mute, warn, pardon, anticheat, automod, etc.)
   * @param {string} title - Alert title (e.g., "Player Banned")
   * @param {string} message - Alert message/details (subtitle)
   * @param {object} options - Additional options { playerId, playerName, silent }
   */
  window.alertToast = function(alertType, title, message, options = {}) {
    console.log('[AlertToast] Called with:', { alertType, title, message, options });

    const settings = window.MX?.state?.staffSettings || {};
    const duration = (settings.webAlertDurationSeconds || 10) * 1000;

    // Check if sound should play
    const soundKey = 'webSound' + alertType.charAt(0).toUpperCase() + alertType.slice(1);
    const shouldPlaySound = settings[soundKey] !== false && !options?.silent; // Default true

    console.log('[AlertToast] Creating alert:', alertType, title, message);
    console.log('[AlertToast] Duration:', duration, 'Sound enabled:', shouldPlaySound);

    const container = getAlertContainer();

    // Enforce max alert limit
    const existingAlerts = container.querySelectorAll('.alert-toast:not(.exit)');
    if (existingAlerts.length >= MAX_ALERT_TOASTS) {
      const oldest = existingAlerts[0];
      if (oldest) {
        oldest.classList.add('exit');
        setTimeout(() => oldest.remove(), 350);
      }
    }

    const el = document.createElement('div');
    el.className = `alert-toast ${alertType}`;

    const icon = alertIconMap[alertType] || alertIconMap.custom;
    const playerId = options?.playerId;
    const playerName = options?.playerName;

    // Build left section with avatar or icon
    let leftContent = '';
    if (playerId) {
      leftContent = `
        <img class="alert-toast-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/32" alt="">
      `;
    } else {
      leftContent = `
        <div class="alert-toast-icon"><i class="fa-solid ${icon}"></i></div>
      `;
    }

    // Build text section
    let textContent = `<div class="alert-toast-title">${escapeHtml(title)}</div>`;
    if (playerName) {
      textContent += `<div class="alert-toast-player">${escapeHtml(playerName)}</div>`;
    }
    if (message) {
      textContent += `<div class="alert-toast-sub">${escapeHtml(message)}</div>`;
    }

    el.innerHTML = `
      <div class="alert-toast-left">
        ${leftContent}
        <div class="alert-toast-text">
          ${textContent}
        </div>
      </div>
      <div class="alert-toast-actions">
        ${playerId ? '<button class="mini" data-action="view"><i class="fa-solid fa-eye"></i></button>' : ''}
        <button class="mini" data-action="dismiss"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="alert-toast-progress" style="animation-duration: ${duration}ms"></div>
    `;

    const dismiss = () => {
      el.classList.add('exit');
      setTimeout(() => el.remove(), 350);
    };

    // View button handler
    const viewBtn = el.querySelector('[data-action="view"]');
    if (viewBtn) {
      viewBtn.onclick = (e) => {
        e.stopPropagation();
        if (playerId) {
          window.openPlayerDrawer?.(playerId);
        }
        dismiss();
      };
    }

    // Dismiss button handler
    el.querySelector('[data-action="dismiss"]').onclick = (e) => {
      e.stopPropagation();
      dismiss();
    };

    // Click anywhere to view player (if playerId) or dismiss
    el.onclick = () => {
      if (playerId) {
        window.openPlayerDrawer?.(playerId);
      }
      dismiss();
    };

    // Insert at top (newest first)
    container.insertBefore(el, container.firstChild);

    // Trigger slide-in animation (need slight delay for CSS transition)
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        el.classList.add('show');
      });
    });

    // Auto-dismiss
    setTimeout(dismiss, duration);

    // Play sound
    if (shouldPlaySound && window.MX?.sounds) {
      window.MX.sounds.alertBar?.();
    }
  };

  window.MX.alertToast = window.alertToast;

  // ===== DEV TOOLS DEBUG CONSOLE =====
  // Always logs to the Developer Tools debug console regardless of debug mode
  window.devtoolsLog = function(category, message, type = 'info') {
    const logEl = document.getElementById('devDebugLogs');
    if (!logEl) return;

    const time = new Date().toLocaleTimeString();
    const typeColors = {
      info: 'var(--primary)',
      success: 'var(--good)',
      warn: 'var(--warn)',
      error: 'var(--bad)'
    };
    const color = typeColors[type] || 'var(--muted)';

    const entry = document.createElement('div');
    entry.style.cssText = 'padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.05);font-size:12px;font-family:var(--font-mono)';
    entry.innerHTML = `
      <span style="color:var(--muted)">${time}</span>
      <span style="color:${color};font-weight:600">[${escapeHtml(category)}]</span>
      <span style="color:var(--text)">${escapeHtml(message)}</span>
    `;
    logEl.appendChild(entry);

    // Auto-scroll to bottom
    logEl.scrollTop = logEl.scrollHeight;

    // Keep only last 200 entries
    while (logEl.children.length > 200) {
      logEl.removeChild(logEl.firstChild);
    }
  };
  window.MX.devtoolsLog = window.devtoolsLog;

  // ===== DEBUG MODE =====
  // Debug log function - shows notifications at bottom when debug mode is enabled
  window.debugLog = function(category, message, type = 'info') {
    // Always log to dev tools console
    window.devtoolsLog(category, message, type);

    // Check if debug mode is enabled in user settings for on-screen display
    if (!state.userSettings?.debugMode) return;

    // Add to debug log container
    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type}`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat">[${escapeHtml(category)}]</span>
      <span class="debug-msg">${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // Auto-remove after 5 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 5000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    // Also log to console
    console.log(`[DEBUG][${category}] ${message}`);
  };

  function createDebugContainer() {
    const container = document.createElement('div');
    container.id = 'debug-log-container';
    container.className = 'debug-log-container';
    document.body.appendChild(container);
    return container;
  }

  window.MX.debugLog = window.debugLog;

  // ===== SYSTEM MESSAGES =====
  // System messages are ALWAYS shown (plugin status, updates, etc.) - 10 second display
  window.systemLog = function(message, type = 'info') {
    // Always log to dev tools console
    window.devtoolsLog('SYSTEM', message, type);

    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type} system-entry`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat system-cat">[SYSTEM]</span>
      <span class="debug-msg">${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // System messages stay for 10 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 10000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    console.log(`[SYSTEM] ${message}`);
  };

  window.MX.systemLog = window.systemLog;

  // ===== WATCHLIST ALERTS =====
  // Watchlist alerts - shown when watchlistAlerts setting is enabled
  window.watchlistLog = function(playerName, message, type = 'warn') {
    // Always log to dev tools console
    window.devtoolsLog('WATCHLIST', `${playerName} ${message}`, type);

    // Check if watchlist alerts are enabled in user settings
    if (!state.userSettings?.watchlistAlerts) return;

    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type} watchlist-entry`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat watchlist-cat">[WATCHLIST]</span>
      <span class="debug-msg"><b>${escapeHtml(playerName)}</b> ${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // Watchlist alerts stay for 8 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 8000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    // Play watchlist alert sound
    window.MX?.sounds?.alert();

    console.log(`[WATCHLIST] ${playerName}: ${message}`);
  };

  window.MX.watchlistLog = window.watchlistLog;

  // ===== STAFF CHAT =====
  const staffChatMessages = [];

  window.sendStaffChat = function() {
    const input = $('#staffchatInput');
    const message = input?.value?.trim();
    if (!message) return;

    const ws = window.MX.ws;
    if (ws?.isConnected()) {
      ws.sendStaffChat(message);
    }

    // Add message locally (will also receive from server if connected)
    addStaffChatMessage({
      sender: state.currentUser?.name || 'You',
      message: message,
      isWeb: true,
      isSelf: true,
      time: now()
    });

    input.value = '';
    input.focus();
  };

  function addStaffChatMessage(data) {
    staffChatMessages.push(data);
    if (staffChatMessages.length > 100) {
      staffChatMessages.shift();
    }
    renderStaffChat();
  }

  function renderStaffChat() {
    const container = $('#staffchatMessages');
    if (!container) return;

    if (staffChatMessages.length === 0) {
      container.innerHTML = `
        <div class="staffchat-empty">
          <i class="fa-solid fa-comments"></i>
          <p>No messages yet. Start the conversation!</p>
        </div>
      `;
      return;
    }

    container.innerHTML = staffChatMessages.map(msg => {
      const classes = ['staffchat-message'];
      if (msg.isSelf) classes.push('self');
      if (msg.isWeb) classes.push('web');

      const avatar = `https://mc-heads.net/avatar/${encodeURIComponent(msg.sender.replace('[Web] ', ''))}/32`;
      const time = typeof msg.time === 'number' ? fmtShort(msg.time) : msg.time;

      return `
        <div class="${classes.join(' ')}">
          <img class="staffchat-avatar" src="${avatar}" alt="">
          <div class="staffchat-content">
            <div class="staffchat-header">
              <span class="staffchat-sender">${escapeHtml(msg.sender)}</span>
              ${msg.isWeb ? '<span class="staffchat-badge">WEB</span>' : ''}
              <span class="staffchat-time">${time}</span>
            </div>
            <div class="staffchat-text">${escapeHtml(msg.message)}</div>
          </div>
        </div>
      `;
    }).join('');

    // Scroll to bottom
    container.scrollTop = container.scrollHeight;
  }

  // Handle staff chat input enter key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && e.target.id === 'staffchatInput') {
      sendStaffChat();
    }
  });

  // ===== AUTHENTICATION =====
  window.login = function() {
    const token = dom().authToken.value.trim();
    if (!token) { toast('warn', 'Required', 'Enter authentication token.'); return; }
    state.authenticated = true;
    dom().authOverlay.classList.add('hide');
    showConnectOverlay(true);
    setTimeout(() => {
      showConnectOverlay(false);
      ui.renderAll();
      startSimulation();
      toast('ok', 'Authenticated', 'Welcome to ModereX Control Panel.');
      logEvent('INFO', 'system', 'Authentication', 'Admin authenticated successfully.');
    }, 1200);
  };


  // ===== DRAWER =====
  // Helper to refresh command section in drawer
  function refreshDrawerCommands(p) {
    const recentCmds = (p.recentCommands || []).slice(-10).reverse();
    const cmdEl = dom().drawerRecent;
    if (cmdEl) {
      cmdEl.innerHTML = recentCmds.length ? `
        ${recentCmds.map(item => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(item.cmd || item)}</b></div></div>`).join('')}
        <div class="drawer-row">
          <div class="meta"><small>${p.recentCommands.length} total commands</small></div>
          <button class="mini" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
        </div>
      ` : `<div class="drawer-row"><div class="meta"><small>No commands.</small></div></div>`;
    }
  }

  window.openDrawer = function(playerId, highlightPunId = null) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    state.selectedPlayerId = playerId;
    state.selectedPunishmentId = highlightPunId;

    // Request player details (command history, automod flags) from server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_PLAYER_DETAILS', { uuid: p.uuid });
    }

    const watching = state.watchlist.has(p.uuid) || state.watchlist.has(playerId);
    dom().watchToggleBtn.classList.toggle('on', watching);
    dom().watchToggleBtn.setAttribute('aria-pressed', watching ? 'true' : 'false');
    dom().watchToggleHint.textContent = watching ? 'Watching player' : 'Not watching';

    dom().drawerAvatar.onerror = () => { dom().drawerAvatar.src = `https://minotar.net/helm/${encodeURIComponent(p.name)}/64.png`; };
    dom().drawerAvatar.src = avatarUrl(p);
    dom().drawerName.textContent = p.name;
    dom().drawerMeta.innerHTML = `${escapeHtml(p.uuid)} | <span class="ip-blur">${escapeHtml(p.ip)}</span> | ${escapeHtml(p.platform)}`;

    // Load external punishments from other moderation plugins
    if (window.showExternalPunishments) {
      window.showExternalPunishments(p.uuid);
    }

    dom().drawerActionBar.innerHTML = `
      <div class="action-cluster">
        <button class="action-btn warn" onclick="openPunishModal('WARN','${p.id}')"><i class="fa-solid fa-triangle-exclamation"></i> Warn</button>
        <button class="action-btn mute" onclick="openPunishModal('MUTE','${p.id}')"><i class="fa-solid fa-volume-xmark"></i> Mute</button>
        <button class="action-btn ban" onclick="openPunishModal('BAN','${p.id}')"><i class="fa-solid fa-ban"></i> Ban</button>
      </div>
      <div class="action-cluster">
        <button class="action-btn" onclick="openChatLogs('${p.id}')"><i class="fa-solid fa-comments"></i> Chat Logs</button>
        <button class="action-btn" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-terminal"></i> Commands</button>
        <button class="action-btn compact" onclick="openAutomodLogs('${p.id}')"><i class="fa-solid fa-robot"></i> Automod</button>
      </div>
    `;

    const active = state.punishments.filter(x => x.playerId === p.id && x.active && !x.revoked);
    dom().drawerActivePun.innerHTML = active.length ? active.map(x => {
      const badgeClass = x.type === 'BAN' ? 'red' : x.type === 'MUTE' ? 'yellow' : x.type === 'KICK' ? 'purple' : 'blue';
      const canRevoke = x.type !== 'KICK'; // Kicks cannot be revoked
      return `
        <div class="drawer-row" style="cursor:pointer" onclick="viewPunishmentDetails('${x.id}')">
          <div class="meta"><b>${escapeHtml(x.type)} | ${escapeHtml(x.duration || 'instant')}</b><small>${escapeHtml(truncateText(x.reason || 'No reason', 40))}<br>Case: <span style="font-family:var(--font-mono)">${escapeHtml(x.id)}</span> | by ${escapeHtml(x.staff)}</small></div>
          <div class="drawer-actions">
            <span class="badge ${badgeClass}"><i class="fa-solid fa-file-lines"></i></span>
            ${canRevoke ? `<button class="mini bad" onclick="event.stopPropagation(); revokePunishmentConfirm('${x.id}')"><i class="fa-solid fa-xmark"></i></button>` : ''}
          </div>
        </div>
      `;
    }).join('') : `<div class="drawer-row"><div class="meta"><small>No active punishments.</small></div></div>`;

    const violations = state.punishments.filter(x => x.playerId === p.id && (!x.active || x.revoked)).sort((a, b) => b.createdAt - a.createdAt);
    dom().drawerViolations.innerHTML = violations.length ? violations.slice(0, 8).map(v => `
      <div class="drawer-row" style="cursor:pointer" onclick="viewPunishmentDetails('${v.id}')">
        <div class="meta"><b>${escapeHtml(v.type)} | <span style="font-family:var(--font-mono)">${escapeHtml(v.id)}</span></b><small>${escapeHtml(fmtLong(v.createdAt))} | ${escapeHtml(truncateText(v.reason || 'No reason', 35))}</small></div>
        <span class="badge ${v.type === 'BAN' ? 'red' : v.type === 'MUTE' ? 'yellow' : 'blue'}"><i class="fa-solid fa-file-lines"></i></span>
      </div>
    `).join('') : `<div class="drawer-row"><div class="meta"><small>No violations.</small></div></div>`;

    const pardons = violations.filter(v => v.revoked && v.revokedBy);
    dom().drawerPardons.innerHTML = pardons.length ? pardons.map(v => `
      <div class="drawer-row">
        <div class="meta"><b>${escapeHtml(v.type)} | <span style="font-family:var(--font-mono)">${escapeHtml(v.id)}</span></b><small>Pardoned by ${escapeHtml(v.revokedBy)} | ${escapeHtml(fmtLong(v.revokedAt || v.createdAt))}</small></div>
        <span class="badge gray"><i class="fa-solid fa-check"></i> Pardon</span>
      </div>
    `).join('') : `<div class="drawer-row"><div class="meta"><small>No pardons.</small></div></div>`;

    // IP History section - show current IP and historical IPs
    const ipHistory = (p.ipHistory || []).slice(0, 5);
    const currentIp = p.ip || (ipHistory.length > 0 ? ipHistory[0].ip : 'Unknown');
    dom().drawerIps.innerHTML = `
      <div class="drawer-row"><div class="meta"><b>Current IP</b><small><span class="ip-blur">${escapeHtml(currentIp)}</span></small></div></div>
      ${ipHistory.length > 1 ? ipHistory.slice(1).map(entry => `
        <div class="drawer-row"><div class="meta"><b>Previous</b><small><span class="ip-blur">${escapeHtml(entry.ip)}</span> | ${escapeHtml(fmtShort(entry.t))}</small></div></div>
      `).join('') : ''}
      ${ipHistory.length > 5 ? `<div class="drawer-row"><div class="meta"><small>${ipHistory.length - 5} more IPs...</small></div></div>` : ''}
    `;

    // Recent Commands section
    const recentCmds = (p.recentCommands || []).slice(0, 10);
    dom().drawerRecent.innerHTML = recentCmds.length ? `
      ${recentCmds.map(item => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(item.cmd || item.command || item)}</b><small>${item.t ? escapeHtml(fmtShort(item.t)) : ''}</small></div></div>`).join('')}
      <div class="drawer-row">
        <div class="meta"><small>${(p.recentCommands || []).length} total commands</small></div>
        <button class="mini" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
      </div>
    ` : `<div class="drawer-row"><div class="meta"><small>No commands.</small></div></div>`;

    // Automod Logs section - use fetched data from player details, fallback to state.logs
    const fetchedAutomod = (p.automodLogs || []).slice(0, 6);
    const liveAutomod = state.logs.filter(l => l.kind === 'automod' && l.playerId === p.id).slice(-6).reverse();
    const automodLogs = fetchedAutomod.length > 0 ? fetchedAutomod : liveAutomod;
    dom().drawerAutomod.innerHTML = automodLogs.length ? `
      ${automodLogs.map(l => {
        // Handle both fetched format and live format
        const title = l.rule ? `Automod | ${l.rule}` : (l.title || 'Automod');
        const detail = l.content || l.detail || '';
        return `<div class="drawer-row"><div class="meta"><b>${escapeHtml(title)}</b><small>${escapeHtml(fmtShort(l.t))} | ${escapeHtml(detail)}</small></div></div>`;
      }).join('')}
      <div class="drawer-row">
        <div class="meta"><small>${automodLogs.length} recent events</small></div>
        <button class="mini" onclick="openAutomodLogs('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
      </div>
    ` : `<div class="drawer-row"><div class="meta"><small>No automod logs.</small></div></div>`;

    dom().drawerOverlay.classList.add('show');
    dom().playerDrawer.classList.add('show');
  };

  window.closeDrawer = function() {
    dom().drawerOverlay.classList.remove('show');
    dom().playerDrawer.classList.remove('show');
  };

  // ===== PUNISHMENT MODAL =====
  window.openPunishModal = function(type = null, playerId = null) {
    const pid = playerId || state.selectedPlayerId;
    if (pid) state.selectedPlayerId = pid;
    const p = pid ? state.players.find(x => x.id === pid) : null;

    dom().punishOverlay.classList.add('show');
    state.pendingPunishType = type || state.pendingPunishType || 'WARN';
    state.punishTargetLocked = !!playerId;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, pid);

    dom().punishTarget.innerHTML = state.players.map(player => `
      <option value="${player.id}" ${player.id === pid ? 'selected' : ''}>${escapeHtml(player.name)} | ${escapeHtml(player.platform)}</option>
    `).join('');
    if (!pid && state.players[0]) {
      dom().punishTarget.value = state.players[0].id;
      state.selectedPlayerId = state.players[0].id;
    }
    dom().punishTarget.disabled = state.punishTargetLocked;
    dom().punishTypeSelect.value = state.pendingPunishType;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);

    const tplOptions = ['<option value="none">(none)</option>'].concat(
      state.templates.filter(t => t.id !== 'none').map(t => `<option value="${t.id}">${escapeHtml(t.name)}</option>`)
    );
    dom().punishTemplate.innerHTML = tplOptions.join('');
    dom().punishTemplate.value = 'none';
    applyTemplateToPunish('none');

    renderEvidenceOptions(state.selectedPlayerId, dom().punishEvidencePick, dom().punishEvidencePreview);
  };

  window.setPunishType = function(type) {
    state.pendingPunishType = type;
    if (dom().punishTypeSelect) dom().punishTypeSelect.value = type;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
  };

  window.openPunishFromList = function() {
    openPunishCreateModal();
  };

  window.openPunishCreateModal = function(type = null, playerId = null, lockPlayer = false) {
    dom().punishCreateOverlay.classList.add('show');
    state.punishCreateLocked = !!lockPlayer;
    state.punishCreatePlayerId = playerId || state.punishCreatePlayerId || state.players[0]?.id || null;
    if (state.punishCreatePlayerId) state.selectedPlayerId = state.punishCreatePlayerId;
    const selected = state.players.find(x => x.id === state.punishCreatePlayerId);
    dom().punishCreatePlayer.value = state.punishCreateLocked ? (selected?.name || '') : '';
    dom().punishCreatePlayer.disabled = state.punishCreateLocked;
    dom().punishCreateType.value = type || state.pendingPunishType || 'WARN';
    state.pendingPunishType = dom().punishCreateType.value;
    updatePunishTitle(dom().punishCreateTitle, dom().punishCreateType.value, state.punishCreatePlayerId);

    const tplOptions = ['<option value="none">(none)</option>'].concat(
      state.templates.filter(t => t.id !== 'none').map(t => `<option value="${t.id}">${escapeHtml(t.name)}</option>`)
    );
    dom().punishCreateTemplate.innerHTML = tplOptions.join('');
    dom().punishCreateTemplate.value = 'none';
    dom().punishCreateDuration.value = '';
    dom().punishCreateReason.value = '';
    renderEvidenceOptions(state.punishCreatePlayerId, dom().punishCreateEvidencePick, dom().punishCreateEvidencePreview);
    if (state.punishCreateLocked) {
      renderPunishCreateList();
    } else {
      const combo = dom().punishCreateList?.closest('.combo');
      if (combo) combo.classList.remove('open');
      dom().punishCreateList.innerHTML = '';
    }
  };

  window.closePunishCreateModal = function() {
    dom().punishCreateOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().punishCreateOverlay.classList.remove('show', 'fade-out');
      state.punishCreateLocked = false;
    }, 220);
  };

  function renderPunishCreateList() {
    const combo = dom().punishCreateList?.closest('.combo');
    if (combo) combo.classList.add('open');
    if (state.punishCreateLocked) {
      const p = state.players.find(x => x.id === state.punishCreatePlayerId);
      dom().punishCreateList.innerHTML = p ? `
        <div class="combo-item" data-player-id="${p.id}">
          <div class="meta"><b>${escapeHtml(p.name)} - ${escapeHtml(p.platform)}</b><small>${escapeHtml(p.uuid)}</small></div>
          <span class="badge gray"><i class="fa-solid fa-lock"></i> Locked</span>
        </div>
      ` : '';
      return;
    }
    const q = (dom().punishCreatePlayer.value || '').trim().toLowerCase();
    const list = state.players.filter(p => !q || p.name.toLowerCase().includes(q)).slice(0, 40);
    dom().punishCreateList.innerHTML = list.length ? list.map(p => `
      <div class="combo-item" data-player-id="${p.id}" onclick="selectPunishCreatePlayer('${p.id}')">
        <div class="meta"><b>${escapeHtml(p.name)} - ${escapeHtml(p.platform)}</b><small>${escapeHtml(p.uuid)}</small></div>
        <span class="badge gray"><i class="fa-solid fa-arrow-right"></i> Select</span>
      </div>
    `).join('') : `<div class="drawer-row"><div class="meta"><small>No players found.</small></div></div>`;
  }

  window.selectPunishCreatePlayer = function(playerId) {
    state.punishCreatePlayerId = playerId;
    state.selectedPlayerId = playerId;
    const p = state.players.find(x => x.id === playerId);
    dom().punishCreatePlayer.value = p?.name || '';
    updatePunishTitle(dom().punishCreateTitle, dom().punishCreateType.value, playerId);
    renderEvidenceOptions(playerId, dom().punishCreateEvidencePick, dom().punishCreateEvidencePreview);
    const combo = dom().punishCreateList?.closest('.combo');
    combo?.classList.remove('open');
  };

  window.applyTemplateToPunishCreate = function(templateId) {
    if (!templateId || templateId === 'none') {
      dom().punishCreateReason.value = '';
      dom().punishCreateDuration.value = '';
      return;
    }
    const t = state.templates.find(x => x.id === templateId);
    if (!t) return;
    dom().punishCreateType.value = t.type || dom().punishCreateType.value;
    state.pendingPunishType = dom().punishCreateType.value;
    dom().punishCreateReason.value = t.reason;
    dom().punishCreateDuration.value = t.duration || '';
    updatePunishTitle(dom().punishCreateTitle, dom().punishCreateType.value, state.punishCreatePlayerId);
  };

  window.submitPunishCreate = function() {
    const pid = state.punishCreatePlayerId;
    if (!pid) { window.MX.sounds?.toastWarning(); toast('warn', 'No Target', 'Select a player first.'); return; }
    const type = dom().punishCreateType.value || 'WARN';
    const reason = dom().punishCreateReason.value.trim() || 'No reason';
    const duration = dom().punishCreateDuration.value.trim() || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');
    const evId = dom().punishCreateEvidencePick.value || null;
    executePunishment({ playerId: pid, type, reason, duration, evidenceId: evId });
    ui.renderPunishments();
    ui.renderPlayers();
    closePunishCreateModal();
    window.MX.sounds?.punishment();
    toast('ok', 'Executed', `${type} applied to ${state.players.find(p => p.id === pid)?.name || 'player'}.`, {silent: true});
  };

  window.closePunishModal = function(e) {
    if (e) e.stopPropagation?.();
    dom().punishOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().punishOverlay.classList.remove('show', 'fade-out');
    }, 220);
  };

  window.applyTemplateToPunish = function(templateId) {
    if (!templateId || templateId === 'none') {
      dom().punishReason.value = '';
      dom().punishDuration.value = '';
      return;
    }
    const t = state.templates.find(x => x.id === templateId);
    if (!t) return;
    state.pendingPunishType = t.type || state.pendingPunishType || 'WARN';
    dom().punishTypeSelect.value = state.pendingPunishType;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
    dom().punishReason.value = t.reason;
    dom().punishDuration.value = t.duration || '';
  };

  window.updateEvidencePreview = function() {
    updateEvidencePreviewFor(dom().punishEvidencePick, dom().punishEvidencePreview);
  };

  function refreshPunishEvidenceFor(playerId) {
    renderEvidenceOptions(playerId, dom().punishEvidencePick, dom().punishEvidencePreview);
  }

  window.submitPunishment = function() {
    const pid = state.selectedPlayerId;
    const p = pid ? state.players.find(x => x.id === pid) : null;
    if (!p) { window.MX.sounds?.toastWarning(); toast('warn', 'No Target', 'Select a player first.'); return; }

    const type = state.pendingPunishType || 'WARN';
    const reason = dom().punishReason.value.trim() || 'No reason';
    const duration = dom().punishDuration.value.trim() || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');
    const evId = dom().punishEvidencePick.value || null;

    executePunishment({ playerId: p.id, type, reason, duration, evidenceId: evId });

    ui.renderPunishments();
    ui.renderPlayers();
    closePunishModal();
    window.MX.sounds?.punishment();
    toast('ok', 'Executed', `${type} applied to ${p.name}.`, {silent: true});
  };

  // ===== PUNISHMENT DETAILS =====
  window.viewPunishmentDetails = function(caseId) {
    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun) return;

    const pl = state.players.find(p => p.id === pun.playerId);
    const ev = pun.evidenceId ? state.evidence.find(e => e.id === pun.evidenceId) : null;

    dom().detailsCaseId.innerHTML = `<i class="fa-solid fa-hashtag"></i> ${escapeHtml(caseId.slice(-8))}`;
    dom().detailsBody.innerHTML = `
      <div class="grid cols-2">
        <div class="card" style="margin:0">
          <h3><i class="fa-solid fa-user" style="color:var(--primary-light)"></i> Player</h3>
          <div style="margin-top:12px" class="pwrap">
            <div class="phead" style="width:48px;height:48px"><img src="${avatarUrl(pl || { name: 'Player' })}" alt="" onerror="this.onerror=null;this.src='https://minotar.net/helm/${encodeURIComponent(pl?.name || 'Player')}/64.png'"></div>
            <div><b style="font-size:15px">${escapeHtml(pl?.name || 'Unknown')}</b><div style="font-size:12px;color:var(--text-secondary)">${escapeHtml(pl?.uuid || 'N/A')}</div></div>
          </div>
        </div>
        <div class="card" style="margin:0">
          <h3><i class="fa-solid fa-gavel" style="color:var(--warn)"></i> Details</h3>
          <div style="margin-top:12px;display:flex;flex-direction:column;gap:6px">
            <div><b>Type:</b> ${escapeHtml(pun.type)}</div>
            <div style="word-break:break-word;white-space:pre-wrap"><b>Reason:</b> ${escapeHtml(pun.reason || 'No reason')}</div>
            <div><b>Duration:</b> ${escapeHtml(pun.duration || 'Instant')}</div>
            <div><b>Staff:</b> ${escapeHtml(pun.staff || 'System')}</div>
            <div><b>Created:</b> ${escapeHtml(fmtShort(pun.createdAt))}</div>
            <div><b>Status:</b> ${pun.active && !pun.revoked ? '<span class="badge red">Active</span>' : '<span class="badge gray">Closed</span>'}</div>
          </div>
        </div>
      </div>
      ${ev ? `<div class="card"><h3><i class="fa-solid fa-paperclip" style="color:var(--accent-light)"></i> Evidence</h3>
        <div style="margin-top:12px"><b>Trigger:</b> ${escapeHtml(ev.trigger)}<br><b>Message:</b> ${escapeHtml(ev.message)}</div></div>` : ''}
    `;

    // Kicks cannot be revoked - they are instant actions
    const canRevoke = !pun.revoked && pun.type !== 'KICK';
    dom().detailsActions.innerHTML = `
      ${canRevoke ? `<button class="btn bad" onclick="revokePunishmentConfirm('${pun.id}')"><i class="fa-solid fa-xmark"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : ''}
      ${pun.type === 'KICK' && !pun.revoked ? `<span class="badge gray"><i class="fa-solid fa-info-circle"></i> Kicks cannot be revoked</span>` : ''}
      <button class="btn ghost" onclick="closeDetailsModal()"><i class="fa-solid fa-xmark"></i> Close</button>
    `;
    dom().detailsOverlay.classList.add('show', 'top');
  };

  window.closeDetailsModal = function(e) {
    if (e) e.stopPropagation?.();
    dom().detailsOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().detailsOverlay.classList.remove('show', 'top', 'fade-out');
    }, 220);
  };

  window.revokePunishment = function(caseId) {
    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun || pun.revoked) return;
    pun.revoked = true;
    pun.active = false;
    pun.revokedBy = state.staffName;
    pun.revokedAt = now();

    const p = state.players.find(x => x.id === pun.playerId);
    if (pun.type === 'WARN' && p) p.warnings = Math.max(0, (p.warnings || 1) - 1);
    state.activity.push({ t: now(), actor: state.staffName, action: `Revoked ${pun.type}`, target: p?.name || 'Unknown' });
    const undoType = pun.type === 'BAN' ? 'UNBAN' : pun.type === 'MUTE' ? 'UNMUTE' : pun.type === 'WARN' ? 'UNWARN' : 'EXPIRE';
    logEvent('INFO', 'punishment', `Revoked ${pun.type}`, `Case ${pun.id} revoked`, { playerId: pun.playerId, caseId: pun.id, kind: 'punishment', type: undoType });

    ui.renderPunishments();
    ui.renderDashboard();
    window.MX.sounds?.pardon();
    toast('info', 'Revoked', `Case ${caseId.slice(-8)} revoked.`, {silent: true});
  };

  window.revokePunishmentConfirm = function(caseId) {
    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun || pun.revoked) return;

    // Kicks cannot be revoked - they are instant actions
    if (pun.type === 'KICK') {
      toast('warn', 'Cannot Revoke', 'Kicks are instant actions and cannot be revoked.');
      return;
    }

    const verb = pun.type === 'WARN' ? 'Remove' : 'Revoke';
    openConfirmPanel({
      title: `${verb} Punishment`,
      body: `${verb} ${pun.type} case ${caseId.slice(-8)} for ${state.players.find(p => p.id === pun.playerId)?.name || 'player'}?`,
      confirmText: verb,
      onConfirm: () => { revokePunishment(caseId); closeDetailsModal(); }
    });
  };

  // ===== TEMPLATES =====
  window.useTemplate = function(tplId) {
    const t = state.templates.find(x => x.id === tplId);
    if (!t) return;
    if (t.id === 'none') { applyTemplateToPunish('none'); return; }
    openPunishModal(t.type);
    dom().punishTemplate.value = tplId;
    applyTemplateToPunish(tplId);
    toast('info', 'Template Applied', t.name);
  };

  window.deleteTemplate = function(tplId) {
    if (tplId === 'none') return;
    state.templates = state.templates.filter(t => t.id !== tplId);
    ui.renderTemplates();
    toast('info', 'Deleted', 'Template removed.');
  };

  window.createTemplateUI = function() {
    openGenericModal({
      title: 'Create Template',
      html: `
        <div class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Name</div><input class="input" id="mTplName" placeholder="e.g. Spam (1d mute)" /></div>
          <div><div class="hintline" style="margin-top:0">Type</div><select class="input" id="mTplType"><option>WARN</option><option>MUTE</option><option>KICK</option><option>BAN</option></select></div>
        </div>
        <div style="margin-top:12px" class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Duration</div><input class="input" id="mTplDur" placeholder="e.g. 7d, 1h, perm" /></div>
          <div><div class="hintline" style="margin-top:0">Reason</div><input class="input" id="mTplReason" placeholder="Reason..." /></div>
        </div>
      `,
      onSubmit: () => {
        const name = ($('#mTplName')?.value || '').trim();
        if (!name) { toast('warn', 'Missing', 'Enter template name.'); return false; }
        state.templates.unshift({ id: uid('tpl'), name, type: $('#mTplType').value, duration: $('#mTplDur').value.trim(), reason: $('#mTplReason').value.trim() || 'No reason', color: 'info' });
        ui.renderTemplates();
        toast('ok', 'Created', name);
        return true;
      }
    });
  };

  window.editTemplateUI = function(tplId) {
    const tpl = state.templates.find(t => t.id === tplId);
    if (!tpl) return;
    openGenericModal({
      title: 'Edit Template',
      html: `
        <div class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Name</div><input class="input" id="mTplName" value="${escapeHtml(tpl.name)}" /></div>
          <div><div class="hintline" style="margin-top:0">Type</div><select class="input" id="mTplType">
            ${['WARN','MUTE','KICK','BAN'].map(t => `<option ${tpl.type === t ? 'selected' : ''}>${t}</option>`).join('')}
          </select></div>
        </div>
        <div style="margin-top:12px" class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Duration</div><input class="input" id="mTplDur" value="${escapeHtml(tpl.duration || '')}" /></div>
          <div><div class="hintline" style="margin-top:0">Reason</div><input class="input" id="mTplReason" value="${escapeHtml(tpl.reason || '')}" /></div>
        </div>
      `,
      onSubmit: () => {
        const name = ($('#mTplName')?.value || '').trim();
        if (!name) { toast('warn', 'Missing', 'Enter template name.'); return false; }
        tpl.name = name;
        tpl.type = $('#mTplType').value;
        tpl.duration = $('#mTplDur').value.trim();
        tpl.reason = $('#mTplReason').value.trim() || 'No reason';
        ui.renderTemplates();
        toast('ok', 'Updated', name);
        return true;
      }
    });
  };

  // ===== GENERIC MODAL =====
  let genericModalEl = null;
  function closeOverlayAnimated(overlay) {
    if (!overlay) return;
    const modal = overlay.querySelector('.modal');
    overlay.classList.add('fade-out');
    modal?.classList.add('fade-out');
    setTimeout(() => overlay.remove(), 220);
  }
  function openGenericModal({ title, html, onSubmit = () => true }) {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top"><div style="display:flex;align-items:center;gap:10px"><i class="fa-solid fa-pen-to-square" style="color:var(--muted)"></i><b>${escapeHtml(title)}</b></div>
          <button class="mini" id="gmClose"><i class="fa-solid fa-xmark"></i></button></div>
        <div class="modal-body">${html}</div>
        <div class="modal-foot"><span class="badge gray"><i class="fa-solid fa-circle-info"></i> Configuration</span>
          <div style="display:flex;gap:10px"><button class="btn ghost" id="gmCancel"><i class="fa-solid fa-xmark"></i> Cancel</button><button class="btn primary" id="gmSubmit"><i class="fa-solid fa-check"></i> Save</button></div></div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#gmClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#gmCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#gmSubmit', overlay).onclick = () => { if (onSubmit()) closeOverlayAnimated(overlay); };
  }

  function openConfirmPanel({ title, body, confirmText = 'Confirm', onConfirm = () => {} }) {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show top';
    overlay.style.zIndex = 8000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-triangle-exclamation" style="color:var(--warn)"></i>
            <b>${escapeHtml(title)}</b>
          </div>
          <button class="mini" id="cpClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="card" style="margin:0">${escapeHtml(body)}</div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Action Required</span>
          <div style="display:flex;gap:10px">
            <button class="btn ghost" id="cpCancel"><i class="fa-solid fa-xmark"></i> Cancel</button>
            <button class="btn bad" id="cpConfirm"><i class="fa-solid fa-xmark"></i> ${escapeHtml(confirmText)}</button>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#cpClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#cpCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#cpConfirm', overlay).onclick = () => { onConfirm(); closeOverlayAnimated(overlay); };
  }

  window.openCommandHistory = function(playerId) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();

    const ws = window.MX?.ws;
    let currentPage = 1;
    let totalPages = 1;
    let totalCount = 0;
    let searchTimeout = null;

    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()" style="max-width:700px">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-terminal" style="color:var(--primary-light)"></i>
            <b>Command History</b>
            <span class="badge gray" id="cmdCount">Loading...</span>
          </div>
          <button class="mini" id="cmdClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="cmdSearch" placeholder="Search commands...">
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="cmdList">
            <div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>
          </div>
          <div class="pagination" style="margin-top:14px;display:flex;justify-content:center;gap:8px;align-items:center" id="cmdPagination"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="cmdCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;

    const close = () => closeOverlayAnimated(overlay);
    const listEl = $('#cmdList', overlay);
    const searchEl = $('#cmdSearch', overlay);
    const countEl = $('#cmdCount', overlay);
    const paginationEl = $('#cmdPagination', overlay);

    const fetchCommands = (page = 1, search = '') => {
      currentPage = page;
      listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>';

      if (ws) ws.send('GET_COMMAND_HISTORY', { uuid: p.id, page, limit: 50, search });
    };

    const renderCommands = (data) => {
      const commands = data.commands || [];
      totalPages = data.totalPages || 1;
      totalCount = data.total || 0;
      currentPage = data.page || 1;

      countEl.textContent = `${totalCount} total`;

      if (commands.length === 0) {
        listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>No commands found.</small></div></div>';
      } else {
        listEl.innerHTML = commands.map(cmd => `
          <div class="drawer-row" data-player-id="${p.id}">
            <div class="meta" style="flex:1">
              <b style="font-family:var(--font-mono);font-size:13px">${escapeHtml(cmd.cmd)}</b>
              <small>${escapeHtml(fmtLong(cmd.t))}${cmd.server ? ' | ' + escapeHtml(cmd.server) : ''}</small>
            </div>
          </div>
        `).join('');
      }

      // Render pagination
      if (totalPages > 1) {
        let paginationHtml = '';
        if (currentPage > 1) {
          paginationHtml += `<button class="mini" onclick="window._cmdPageNav(${currentPage - 1})"><i class="fa-solid fa-chevron-left"></i></button>`;
        }
        paginationHtml += `<span style="color:var(--text-muted)">Page ${currentPage} of ${totalPages}</span>`;
        if (currentPage < totalPages) {
          paginationHtml += `<button class="mini" onclick="window._cmdPageNav(${currentPage + 1})"><i class="fa-solid fa-chevron-right"></i></button>`;
        }
        paginationEl.innerHTML = paginationHtml;
        paginationEl.style.display = 'flex';
      } else {
        paginationEl.style.display = 'none';
      }
    };

    // Store handler for pagination navigation
    window._cmdPageNav = (page) => fetchCommands(page, searchEl.value.trim());

    // Handle WebSocket response - ws.on receives data directly (not event)
    const handleResponse = (data) => {
      if (data && data.uuid === p.id) {
        renderCommands(data);
      }
    };

    if (ws) ws.on('COMMAND_HISTORY_DATA', handleResponse);

    const closeWithCleanup = () => {
      if (ws) ws.off('COMMAND_HISTORY_DATA', handleResponse);
      delete window._cmdPageNav;
      close();
    };

    $('#cmdClose', overlay).onclick = closeWithCleanup;
    $('#cmdCloseBtn', overlay).onclick = closeWithCleanup;

    searchEl.addEventListener('input', () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => fetchCommands(1, searchEl.value.trim()), 300);
    });

    // Initial fetch
    fetchCommands(1, '');
  }

  window.openChatLogs = function(playerId) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-comments" style="color:var(--accent-light)"></i>
            <b>Chat Logs</b>
          </div>
          <button class="mini" id="chatClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="toolbar" style="margin-top:0">
            <div class="left" style="gap:10px">
              <div class="gsearch" style="width:100%;max-width:420px">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="text" id="chatSearch" placeholder="Search chat logs...">
              </div>
              <input type="date" class="input" id="chatFrom" style="max-width:160px">
              <input type="date" class="input" id="chatTo" style="max-width:160px">
            </div>
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="chatList"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="chatCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#chatClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#chatCloseBtn', overlay).onclick = () => closeOverlayAnimated(overlay);
    const listEl = $('#chatList', overlay);
    const searchEl = $('#chatSearch', overlay);
    const fromEl = $('#chatFrom', overlay);
    const toEl = $('#chatTo', overlay);
    // Use fetched chat logs from player details, with fallback to state.logs
    const fetchedChatLogs = (p.chatLogs || []).map(l => ({
      t: l.t,
      title: `Chat | ${p.name}`,
      detail: l.content,
      playerId: p.id
    }));
    const liveChatLogs = state.logs.filter(l => l.channel === 'chat' && (l.playerId === p.id || (l.title || '').includes(p.name)));
    const allLogs = fetchedChatLogs.length > 0 ? fetchedChatLogs : liveChatLogs;
    const render = () => {
      const q = (searchEl.value || '').trim().toLowerCase();
      const fromVal = fromEl.value ? new Date(fromEl.value).getTime() : null;
      const toVal = toEl.value ? new Date(toEl.value).getTime() + 86400000 : null;
      const filtered = allLogs.filter(l => {
        if (q && !`${l.title} ${l.detail}`.toLowerCase().includes(q)) return false;
        if (fromVal && l.t < fromVal) return false;
        if (toVal && l.t > toVal) return false;
        return true;
      }).slice(-200);
      listEl.innerHTML = filtered.length ? filtered.map(l => `
        <div class="drawer-row" data-player-id="${p.id}"><div class="meta"><b>${escapeHtml(fmtLong(l.t))}</b><small>${escapeHtml(l.detail || l.content)}</small></div></div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No chat logs found.</small></div></div>`;
    };
    searchEl.addEventListener('input', render);
    fromEl.addEventListener('change', render);
    toEl.addEventListener('change', render);
    render();
  };

  window.openAutomodLogs = function(playerId) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();

    const ws = window.MX?.ws;
    let currentPage = 1;
    let totalPages = 1;
    let totalCount = 0;
    let searchTimeout = null;

    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()" style="max-width:700px">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-robot" style="color:var(--primary-light)"></i>
            <b>Automod Logs</b>
            <span class="badge gray" id="autoCount">Loading...</span>
          </div>
          <button class="mini" id="autoClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="autoSearch" placeholder="Search automod logs...">
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="autoList">
            <div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>
          </div>
          <div class="pagination" style="margin-top:14px;display:flex;justify-content:center;gap:8px;align-items:center" id="autoPagination"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="autoCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;

    const close = () => closeOverlayAnimated(overlay);
    const listEl = $('#autoList', overlay);
    const searchEl = $('#autoSearch', overlay);
    const countEl = $('#autoCount', overlay);
    const paginationEl = $('#autoPagination', overlay);

    const fetchLogs = (page = 1, search = '') => {
      currentPage = page;
      listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>';

      if (ws) ws.send('GET_AUTOMOD_LOGS', { uuid: p.id, page, limit: 50, search });
    };

    const renderLogs = (data) => {
      const logs = data.logs || [];
      totalPages = data.totalPages || 1;
      totalCount = data.total || 0;
      currentPage = data.page || 1;

      countEl.textContent = `${totalCount} total`;

      if (logs.length === 0) {
        listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>No automod logs found.</small></div></div>';
      } else {
        listEl.innerHTML = logs.map(l => `
          <div class="drawer-row" data-player-id="${p.id}">
            <div class="meta" style="flex:1">
              <b>${escapeHtml(l.rule || 'Automod')}</b>
              <small>${escapeHtml(fmtLong(l.t))}${l.server ? ' | ' + escapeHtml(l.server) : ''}</small>
            </div>
            <div class="meta" style="flex:2">
              <small style="color:var(--text-muted)">${escapeHtml(l.content || '')}</small>
            </div>
          </div>
        `).join('');
      }

      // Render pagination
      if (totalPages > 1) {
        let paginationHtml = '';
        if (currentPage > 1) {
          paginationHtml += `<button class="mini" onclick="window._autoPageNav(${currentPage - 1})"><i class="fa-solid fa-chevron-left"></i></button>`;
        }
        paginationHtml += `<span style="color:var(--text-muted)">Page ${currentPage} of ${totalPages}</span>`;
        if (currentPage < totalPages) {
          paginationHtml += `<button class="mini" onclick="window._autoPageNav(${currentPage + 1})"><i class="fa-solid fa-chevron-right"></i></button>`;
        }
        paginationEl.innerHTML = paginationHtml;
        paginationEl.style.display = 'flex';
      } else {
        paginationEl.style.display = 'none';
      }
    };

    // Store handler for pagination navigation
    window._autoPageNav = (page) => fetchLogs(page, searchEl.value.trim());

    // Handle WebSocket response - ws.on receives data directly (not event)
    const handleResponse = (data) => {
      if (data) {
        renderLogs(data);
      }
    };

    if (ws) ws.on('AUTOMOD_LOGS_DATA', handleResponse);

    const closeWithCleanup = () => {
      if (ws) ws.off('AUTOMOD_LOGS_DATA', handleResponse);
      delete window._autoPageNav;
      close();
    };

    $('#autoClose', overlay).onclick = closeWithCleanup;
    $('#autoCloseBtn', overlay).onclick = closeWithCleanup;

    searchEl.addEventListener('input', () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => fetchLogs(1, searchEl.value.trim()), 300);
    });

    // Initial fetch
    fetchLogs(1, '');
  };

  // ===== COMMAND BLACKLIST =====
  state.cmdBlacklist = state.cmdBlacklist || [];

  window.filterCmdBlacklist = function() {
    renderCmdBlacklist();
  };

  window.renderCmdBlacklist = function() {
    const container = document.getElementById('cmdblList');
    const countBadge = document.getElementById('cmdblCount');
    if (!container) return;

    const search = (document.getElementById('cmdblSearch')?.value || '').toLowerCase();
    const statusFilter = document.getElementById('cmdblStatusFilter')?.value || 'all';
    const now = Date.now();

    let filtered = state.cmdBlacklist.filter(entry => {
      if (search && !entry.playerName?.toLowerCase().includes(search) && !entry.command?.toLowerCase().includes(search)) {
        return false;
      }
      const isActive = entry.expiresAt === -1 || entry.expiresAt > now;
      if (statusFilter === 'active' && !isActive) return false;
      if (statusFilter === 'expired' && isActive) return false;
      return true;
    });

    if (countBadge) {
      countBadge.textContent = `${filtered.length} entr${filtered.length === 1 ? 'y' : 'ies'}`;
    }

    if (filtered.length === 0) {
      container.innerHTML = `
        <div class="empty-state" style="text-align:center;padding:40px;color:var(--text-secondary)">
          <i class="fa-solid fa-ban" style="font-size:48px;opacity:0.3;margin-bottom:16px"></i>
          <p>No command blacklist entries${search ? ' match your search' : ''}</p>
        </div>
      `;
      return;
    }

    container.innerHTML = filtered.map(entry => {
      const isActive = entry.expiresAt === -1 || entry.expiresAt > now;
      const expiresText = entry.expiresAt === -1 ? 'Permanent' : (isActive ? fmtDuration(entry.expiresAt - now) + ' left' : 'Expired');
      const statusBadge = isActive ? '<span class="badge red">Active</span>' : '<span class="badge gray">Expired</span>';

      return `
        <div class="cmdbl-entry ${isActive ? '' : 'expired'}">
          <div class="cmdbl-main">
            <div class="cmdbl-player">
              <img src="https://minotar.net/helm/${escapeHtml(entry.playerName || 'Steve')}/32.png" alt="" class="cmdbl-avatar">
              <div>
                <b>${escapeHtml(entry.playerName || 'Unknown')}</b>
                <small style="color:var(--muted);display:block;font-size:11px">${escapeHtml(entry.playerUuid?.substring(0, 8) || '')}</small>
              </div>
            </div>
            <div class="cmdbl-command">
              <span class="badge purple"><i class="fa-solid fa-terminal"></i> /${escapeHtml(entry.command || 'unknown')}</span>
            </div>
            <div class="cmdbl-info">
              ${statusBadge}
              <span class="badge gray"><i class="fa-solid fa-clock"></i> ${expiresText}</span>
            </div>
            <div class="cmdbl-actions">
              ${isActive ? `<button class="mini bad" onclick="removeCmdBlacklist('${entry.id}')"><i class="fa-solid fa-xmark"></i> Remove</button>` : ''}
            </div>
          </div>
          <div class="cmdbl-meta">
            <small><i class="fa-solid fa-user-shield"></i> ${escapeHtml(entry.staffName || 'Console')} | <i class="fa-solid fa-clock"></i> ${fmtLong(entry.createdAt)} | ${escapeHtml(entry.reason || 'No reason')}</small>
          </div>
        </div>
      `;
    }).join('');
  };

  window.openCmdBlacklistModal = function() {
    // TODO: Implement add blacklist modal
    toast('info', 'Coming Soon', 'Command blacklist management will be available soon. Use /cmdblacklist in-game.');
  };

  window.removeCmdBlacklist = function(id) {
    // TODO: Implement remove via WebSocket
    toast('info', 'Coming Soon', 'Command blacklist removal will be available soon. Use /cmdunblacklist in-game.');
  };

  // ===== RULES =====
  // State for automod filtering and pagination
  state.rulesPage = state.rulesPage || 1;
  state.rulesPageSize = state.rulesPageSize || 10;

  window.filterRules = function() {
    state.rulesPage = 1; // Reset to page 1 when filtering
    ui.renderRules();
  };

  window.rulesPage = function(delta) {
    const search = (document.getElementById('ruleSearch')?.value || '').toLowerCase();
    const typeFilter = document.getElementById('ruleTypeFilter')?.value || 'all';
    const statusFilter = document.getElementById('ruleStatusFilter')?.value || 'all';

    const filtered = state.rules.filter(r => {
      if (search && !r.name.toLowerCase().includes(search)) return false;
      if (typeFilter !== 'all' && r.type !== typeFilter) return false;
      if (statusFilter === 'enabled' && !r.enabled) return false;
      if (statusFilter === 'disabled' && r.enabled) return false;
      return true;
    });

    const totalPages = Math.max(1, Math.ceil(filtered.length / state.rulesPageSize));
    state.rulesPage = Math.max(1, Math.min(totalPages, state.rulesPage + delta));
    ui.renderRules();
  };

  window.addRuleUI = function() {
    state.rules.unshift({ id: uid('rule'), name: `New Rule ${state.rules.length + 1}`, enabled: true, block: true, conditions: [{ kind: 'contains', value: '', match: 'contains' }], action: { kind: 'none', extra: '' }, threshold: { hits: 1, windowMins: 10 }, notes: 'Configure conditions', type: 'WORD_FILTER' });
    ui.markUnsaved('rules', true);
    state.rulesPage = 1; // Go to first page to see new rule
    ui.renderRules();
    toast('ok', 'Created', 'New rule added.');
  };

  window.deleteRule = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r?.locked) return;
    openConfirmPanel({
      title: 'Delete Rule',
      body: `Delete rule "${r.name}"? This cannot be undone.`,
      confirmText: 'Delete',
      onConfirm: () => {
        state.rules = state.rules.filter(x => x.id !== ruleId);
        ui.markUnsaved('rules', true);
        ui.renderRules();
        toast('info', 'Deleted', 'Rule removed.');
      }
    });
  };

  window.toggleRule = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.enabled = !r.enabled;
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.addCondition = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.conditions) r.conditions = [];
      r.conditions.push({ kind: 'contains', value: '', match: 'contains' });
      ui.markUnsaved('rules', true);
      ui.renderRules();
    }
  };

  window.removeCondition = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.conditions.splice(idx, 1); ui.markUnsaved('rules', true); ui.renderRules(); }
  };

  window.setConditionKind = function(ruleId, idx, kind) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.conditions[idx].kind = kind;
      if (kind === 'link') r.conditions[idx].value = '';
      if (kind === 'contains') r.conditions[idx].match = r.conditions[idx].match || 'contains';
      if (kind === 'repeat') r.conditions[idx].similar = !!r.conditions[idx].similar;
      ui.markUnsaved('rules', true);
      ui.renderRules();
    }
  };

  window.setConditionValue = function(ruleId, idx, val) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.conditions[idx].value = val; ui.markUnsaved('rules', true); }
  };

  window.toggleConditionExact = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;
    const c = r.conditions[idx];
    if (!c) return;
    c.match = c.match === 'exact' ? 'contains' : 'exact';
    ui.markUnsaved('rules', true);
    ui.renderRules();
  };

  window.toggleConditionSimilar = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;
    const c = r.conditions[idx];
    if (!c) return;
    c.similar = !c.similar;
    ui.markUnsaved('rules', true);
    ui.renderRules();
  };

  window.setRuleAction = function(ruleId, kind) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.kind = kind;
      if (kind === 'none') { r.action.extra = ''; r.action.duration = ''; }
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.setRuleActionExtra = function(ruleId, extra) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.extra = extra;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
    }
  };

  window.setRuleActionDuration = function(ruleId, duration) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.duration = duration;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
    }
  };

  window.setRuleName = function(ruleId, name) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r && !r.locked) { r.name = name; ui.markUnsaved('rules', true); }
  };

  window.toggleRuleBlock = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.block = !r.block;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.setRuleThreshold = function(ruleId, field, v) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.threshold) r.threshold = {};
      r.threshold[field] = Math.max(1, parseInt(v || '1', 10));
      autoSaveRule(r);
    }
  };

  // Set a specific setting on a rule (for built-in rule config)
  window.setRuleSetting = function(ruleId, setting, value) {
    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;

    // Parse numeric values
    if (['spamMessageCount', 'spamTimeWindowSeconds', 'capsMaxPercentage', 'capsMinLength', 'afkTimeoutMinutes', 'anticheatAlertThreshold', 'anticheatTimeWindowSeconds'].includes(setting)) {
      r[setting] = Math.max(1, parseInt(value || '1', 10));
    } else if (['spamDetectSimilar', 'afkKickEnabled'].includes(setting)) {
      r[setting] = value === true || value === 'true';
    } else {
      r[setting] = value;
    }

    autoSaveRule(r);
    ui.renderRules();
  };

  // Auto-save a rule to the server
  function autoSaveRule(rule) {
    if (!rule) return;

    // Mark as unsaved for visual feedback
    ui.markUnsaved('rules', true);

    // Auto-save built-in rules and anticheat rules immediately
    if (rule.builtIn || ['spam_protection', 'caps_filter', 'link_filter', 'afk_kick'].includes(rule.id) || rule.id.startsWith('ac_')) {
      MX.ws.send('UPDATE_AUTOMOD_RULE', {
        ruleId: rule.id,
        rule: rule
      });
      window.debugLog('SYNC', `Saved rule: ${rule.name}`, 'success');
    }
  }

  window.setRuleExceptions = function(ruleId, value) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r && !r.locked) {
      // Split by newlines, trim, filter empty - these are word/phrase exceptions
      r.exceptions = value.split('\n').map(s => s.trim()).filter(Boolean);
      ui.markUnsaved('rules', true);
    }
  };

  window.saveRules = function() {
    ui.markUnsaved('rules', true);
    toast('ok', 'Saved', 'Rules saved locally. Publish to apply.');
  };

  window.seedTestMsg = function() {
    dom().testMessage.value = pick(['THIS SERVER IS TERRIBLE!!!!', 'check https://example.com', 'kys lol', 'spam spam spam']);
  };

  window.runRuleTest = function() {
    const msg = dom().testMessage.value;
    if (!msg.trim()) {
      dom().testResult.innerHTML = '<span style="color:var(--muted)">Enter a message to test.</span>';
      return;
    }

    const enabledRules = state.rules.filter(x => x.enabled);
    if (enabledRules.length === 0) {
      dom().testResult.innerHTML = '<span style="color:var(--warn)"><i class="fa-solid fa-triangle-exclamation"></i> No enabled automod rules to test against.</span>';
      return;
    }

    const hits = [];
    const normalizedMsg = normalizeMessage(msg);

    for (const r of enabledRules) {
      let triggered = false;
      let triggerReason = '';

      // Check blacklisted words/phrases (main filter)
      const blacklist = r.blacklistedWords || r.blacklistedPhrases || [];
      if (blacklist.length > 0) {
        for (const word of blacklist) {
          const w = word.toLowerCase().trim();
          if (w && (r.exactMatch ? normalizedMsg === w : normalizedMsg.includes(w))) {
            // Check if in exceptions/whitelist
            const exceptions = r.exceptions || r.exclusionWords || r.whitelist || [];
            const isExcepted = exceptions.some(e => normalizedMsg.includes(e.toLowerCase()));
            if (!isExcepted) {
              triggered = true;
              triggerReason = `Blacklisted: "${word}"`;
              break;
            }
          }
        }
      }

      // Check conditions array
      if (!triggered) {
        for (const c of (r.conditions || [])) {
          if (c.kind === 'contains' && c.value) {
            const parts = String(c.value).split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
            const match = parts.find(part => c.match === 'exact' ? normalizedMsg === part : normalizedMsg.includes(part));
            if (match) { triggered = true; triggerReason = `Contains: "${match}"`; break; }
          }
          if (c.kind === 'regex' && c.value) {
            try {
              if (new RegExp(c.value, 'i').test(msg)) {
                triggered = true; triggerReason = `Regex: ${c.value}`; break;
              }
            } catch {}
          }
        }
      }

      // Check caps filter (rule type or condition)
      if (!triggered && (r.type === 'CAPS_FILTER' || r.id === 'caps_filter')) {
        const upper = (msg.match(/[A-Z]/g) || []).length;
        const total = msg.replace(/\s/g, '').length;
        const capsMinLen = r.capsMinLength || 5;
        const capsMaxPct = r.capsMaxPercentage || 50;
        if (total >= capsMinLen && (upper / total) * 100 >= capsMaxPct) {
          triggered = true;
          triggerReason = `Caps: ${Math.round((upper / total) * 100)}% (max ${capsMaxPct}%)`;
        }
      }

      // Check link filter
      if (!triggered && (r.type === 'LINK_FILTER' || r.id === 'link_filter')) {
        if (/https?:\/\/|www\./i.test(msg)) {
          triggered = true;
          triggerReason = 'Contains link';
        }
      }

      // Check spam (simplified - would need message history for real check)
      if (!triggered && (r.type === 'SPAM_PROTECTION' || r.id === 'spam_protection')) {
        // Can't fully test spam without message history, show note
        triggerReason = '(Spam detection requires message history)';
      }

      if (triggered) {
        hits.push({ rule: r, reason: triggerReason });
      }
    }

    if (hits.length) {
      const hitsList = hits.map(h => `
        <div class="drawer-row" style="margin-top:8px">
          <div class="meta" style="flex:1">
            <b style="color:var(--bad)">${escapeHtml(h.rule.name)}</b>
            <small>${escapeHtml(h.reason)}</small>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <span class="badge ${h.rule.block !== false ? 'red' : 'yellow'}">${h.rule.block !== false ? 'BLOCKED' : 'FLAGGED'}</span>
            ${h.rule.action?.kind && h.rule.action.kind !== 'none' ? `<span class="badge gray">${escapeHtml(h.rule.action.kind.toUpperCase())}</span>` : ''}
          </div>
        </div>
      `).join('');
      dom().testResult.innerHTML = `
        <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px">
          <span class="badge red"><i class="fa-solid fa-shield-halved"></i> TRIGGERED</span>
          <small style="color:var(--text-muted)">${hits.length} rule${hits.length > 1 ? 's' : ''} matched</small>
        </div>
        ${hitsList}
      `;
    } else {
      dom().testResult.innerHTML = `
        <div style="display:flex;align-items:center;gap:10px">
          <span class="badge green"><i class="fa-solid fa-check"></i> PASSED</span>
          <small style="color:var(--text-muted)">Tested against ${enabledRules.length} enabled rules</small>
        </div>
      `;
    }
  };

  // ===== WATCHLIST =====
  window.toggleWatchToasts = function() {
    state.settings.watchToasts = !state.settings.watchToasts;
    ui.renderWatchToastsToggle();
    saveUserPrefs();

    // Sync with server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send(JSON.stringify({
        type: 'UPDATE_USER_SETTINGS',
        data: { watchlistToasts: state.settings.watchToasts }
      }));
    }

    window.MX.sounds?.toggle();
    toast('info', 'Notifications', state.settings.watchToasts ? 'Watchlist toasts enabled.' : 'Watchlist toasts disabled.', {silent: true});
  };

  // Show a watchlist alert (called when watched player does something)
  function watchlistAlert(playerId, title, detail, severity = 'INFO') {
    const player = state.players.find(p => p.id === playerId || p.uuid === playerId);
    const playerName = player?.name || 'Unknown Player';
    const settings = loadMySettings();
    const style = settings.watchlistStyle || 'bar';
    const playerData = { playerId, playerName };

    // Add to alerts list
    state.watchAlerts.unshift({ t: Date.now(), playerId, title, detail, sev: severity });
    if (state.watchAlerts.length > 50) state.watchAlerts.pop();

    // Show alert based on user preference
    if (style === 'bar' || style === 'both') {
      showAlertBar('watchlist', title, `${playerName}: ${detail}`, playerData);
    }
    if (style === 'toast' || style === 'both') {
      toast(severity === 'WARN' ? 'warn' : 'info', title, `${playerName}: ${detail}`, playerData);
    }

    ui.renderDashboard();
  }
  window.watchlistAlert = watchlistAlert;

  window.testWatchlistAlert = function() {
    // Get a test player (first from watchlist or first online player)
    const watchedIds = [...state.watchlist];
    const testPlayer = watchedIds.length > 0
      ? state.players.find(p => p.id === watchedIds[0])
      : state.players.find(p => p.status === 'online') || state.players[0];

    const playerName = testPlayer?.name || 'TestPlayer';
    const playerId = testPlayer?.id || 'test-uuid';

    // Simulate watchlist alert
    const alertTypes = [
      { title: 'Player Joined', detail: `${playerName} joined the server`, sev: 'INFO' },
      { title: 'Suspicious Activity', detail: `${playerName} triggered anticheat alert (Speed)`, sev: 'WARN' },
      { title: 'Chat Violation', detail: `${playerName} flagged for spam`, sev: 'WARN' },
      { title: 'Command Executed', detail: `${playerName} ran /gamemode creative`, sev: 'INFO' }
    ];

    const alert = pick(alertTypes);
    watchlistAlert(playerId, alert.title, alert.detail, alert.sev);
    window.MX.sounds?.notification();
  };

  window.addWatchlistFromInput = function() {
    const name = (dom().watchAdd.value || '').trim().toLowerCase();
    if (!name) return;
    const p = state.players.find(x => x.name.toLowerCase().includes(name));
    if (!p) { toast('warn', 'Not Found', 'No player matches.'); return; }
    state.watchlist.add(p.id);

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.addToWatchlist(p.id, p.name, 'Added via web panel');
    }

    dom().watchAdd.value = '';
    toast('ok', 'Added', `${p.name} added to watchlist.`);
    ui.renderWatchlist();
    ui.renderPlayers();
  };

  window.openWatchlistPicker = function() {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-eye" style="color:var(--warn)"></i>
            <b>Add to Watchlist</b>
          </div>
          <button class="mini" id="wlClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="wlSearch" placeholder="Search players...">
          </div>
          <div class="card" style="margin-top:14px" id="wlList"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Choose a player</span>
          <button class="btn ghost" id="wlCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#wlClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#wlCloseBtn', overlay).onclick = () => closeOverlayAnimated(overlay);
    const listEl = $('#wlList', overlay);
    const searchEl = $('#wlSearch', overlay);
    const render = () => {
      const q = (searchEl.value || '').trim().toLowerCase();
      const filtered = state.players.filter(p => !q || p.name.toLowerCase().includes(q)).slice(0, 60);
      listEl.innerHTML = filtered.length ? filtered.map(p => {
        const isWatching = state.watchlist.has(p.uuid) || state.watchlist.has(p.id);
        return `
        <div class="drawer-row" data-player-id="${p.id}" style="cursor:pointer" onclick="addWatchlistById('${p.id}'); this.closest('.overlay').remove();">
          <div class="meta"><b>${escapeHtml(p.name)}</b><small>${escapeHtml(p.platform)} | ${escapeHtml(p.uuid.slice(0, 8))}...</small></div>
          <span class="badge ${isWatching ? 'yellow' : 'gray'}"><i class="fa-solid fa-eye"></i> ${isWatching ? 'Watching' : 'Add'}</span>
        </div>
      `;}).join('') : `<div class="drawer-row"><div class="meta"><small>No players found.</small></div></div>`;
    };
    searchEl.addEventListener('input', render);
    render();
  };

  window.addWatchlistById = function(pid) {
    const p = state.players.find(x => x.id === pid);
    if (!p) return;
    // Use UUID for consistent storage and server sync
    state.watchlist.add(p.uuid);

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.addToWatchlist(p.uuid, p.name, 'Added via web panel');
    }

    window.MX.sounds?.watchlist();
    toast('ok', 'Added', `${p.name} added to watchlist.`, {silent: true});
    ui.renderWatchlist();
    ui.renderPlayers();
  };

  window.toggleWatchlistSelected = function() {
    const pid = state.selectedPlayerId;
    if (!pid) return;
    const p = state.players.find(x => x.id === pid);
    if (!p) return;
    const ws = window.MX?.ws;
    const isWatching = state.watchlist.has(p.uuid) || state.watchlist.has(pid);

    window.MX.sounds?.toggle();
    if (isWatching) {
      state.watchlist.delete(p.uuid);
      state.watchlist.delete(pid); // Clean up any legacy internal ID entries
      // Sync to server
      if (ws && ws.isConnected()) {
        ws.removeFromWatchlist(p.uuid);
      }
      toast('info', 'Removed', 'Player removed from watchlist.');
    } else {
      state.watchlist.add(p.uuid);
      // Sync to server
      if (ws && ws.isConnected()) {
        ws.addToWatchlist(p.uuid, p.name, 'Added via web panel');
      }
      toast('ok', 'Added', 'Player added to watchlist.');
    }
    ui.renderWatchlist();
    ui.renderPlayers();
    const watching = state.watchlist.has(p.uuid);
    dom().watchToggleBtn.classList.toggle('on', watching);
    dom().watchToggleBtn.setAttribute('aria-pressed', watching ? 'true' : 'false');
    dom().watchToggleHint.textContent = watching ? 'Watching player' : 'Not watching';
  };

  window.removeWatch = function(pid) {
    const p = state.players.find(x => x.id === pid);
    // Remove both UUID and internal ID to ensure cleanup
    if (p) state.watchlist.delete(p.uuid);
    state.watchlist.delete(pid);

    // Sync to server using UUID
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.removeFromWatchlist(p ? p.uuid : pid);
    }

    ui.renderWatchlist();
    ui.renderPlayers();
    window.MX.sounds?.click();
    toast('info', 'Removed', 'Player removed.');
  };

  window.clearWatchAlerts = function() {
    state.watchAlerts = [];
    ui.renderWatchlist();
    toast('info', 'Cleared', 'Alerts cleared.');
  };

  // ===== ANTICHEAT =====
  window.toggleAnticheatAlert = function(alertId, field) {
    const alert = (state.anticheat?.alerts || []).find(a => a.id === alertId);
    if (!alert) return;
    alert[field] = !alert[field];
    ui.markUnsaved('anticheat', true);
    ui.renderAnticheat();
  };

  window.setAnticheatAlert = function(alertId, field, value) {
    const alert = (state.anticheat?.alerts || []).find(a => a.id === alertId);
    if (!alert) return;
    if (field === 'threshold' || field === 'windowMins') {
      alert[field] = Math.max(1, parseInt(value || '1', 10));
    } else {
      alert[field] = value;
    }
    ui.markUnsaved('anticheat', true);
  };

  window.toggleAnticheatRule = function() {
    state.anticheatRule.enabled = !state.anticheatRule.enabled;
    ui.markUnsaved('anticheat', true);
    ui.renderRules();
  };

  window.setAnticheatRule = function(field, value) {
    if (field === 'threshold' || field === 'windowMins') {
      state.anticheatRule[field] = Math.max(1, parseInt(value || '1', 10));
    } else {
      state.anticheatRule[field] = value;
    }
    ui.markUnsaved('anticheat', true);
  };

  // ===== CONTEXT MENU =====
  let contextMenuEl = null;

  function ensureContextMenu() {
    if (contextMenuEl) return contextMenuEl;
    contextMenuEl = document.createElement('div');
    contextMenuEl.className = 'context-menu';
    contextMenuEl.innerHTML = `
      <div class="context-title" id="contextTitle">Player</div>
      <div class="context-item" id="ctxProfile"><i class="fa-solid fa-id-card-clip"></i> View Profile</div>
      <div class="context-item" id="ctxCopy"><i class="fa-solid fa-copy"></i> Copy Username</div>
      <div class="context-item" id="ctxPunish"><i class="fa-solid fa-gavel"></i> Punish Player</div>
      <div class="context-item" id="ctxWatch"><i class="fa-solid fa-eye"></i> Watchlist</div>
    `;
    document.body.appendChild(contextMenuEl);
    return contextMenuEl;
  }

  function openContextMenu(playerId, x, y) {
    const p = state.players.find(pl => pl.id === playerId);
    if (!p) return;
    const menu = ensureContextMenu();
    const title = menu.querySelector('#contextTitle');
    const watch = menu.querySelector('#ctxWatch');
    title.textContent = p.name;
    watch.textContent = state.watchlist.has(p.id) ? 'Remove from Watchlist' : 'Add to Watchlist';

    menu.classList.add('show');
    const pad = 12;
    const rect = menu.getBoundingClientRect();
    const maxX = window.innerWidth - rect.width - pad;
    const maxY = window.innerHeight - rect.height - pad;
    menu.style.left = `${Math.max(pad, Math.min(x, maxX))}px`;
    menu.style.top = `${Math.max(pad, Math.min(y, maxY))}px`;

    menu.querySelector('#ctxProfile').onclick = () => { openDrawer(p.id); closeContextMenu(); };
    menu.querySelector('#ctxCopy').onclick = () => { copyToClipboard(p.name); toast('info', 'Copied', p.name); closeContextMenu(); };
    menu.querySelector('#ctxPunish').onclick = () => { openPunishModal(null, p.id); closeContextMenu(); };
    menu.querySelector('#ctxWatch').onclick = () => {
      if (state.watchlist.has(p.id)) state.watchlist.delete(p.id);
      else state.watchlist.add(p.id);
      ui.renderWatchlist();
      ui.renderPlayers();
      toast('info', 'Watchlist', state.watchlist.has(p.id) ? 'Player added.' : 'Player removed.');
      closeContextMenu();
    };
  }

  function closeContextMenu() {
    if (!contextMenuEl) return;
    contextMenuEl.classList.remove('show');
  }

  function maybeWatchAlert(playerId, title, detail, sev = 'INFO') {
    if (!state.watchlist.has(playerId)) return;
    state.watchAlerts.unshift({ t: now(), playerId, title, detail, sev });
    ui.renderDashboard();
    ui.renderWatchlist();
    if (state.settings.watchToasts) toast(sev === 'ERROR' ? 'bad' : sev === 'WARN' ? 'warn' : 'info', `Watchlist: ${title}`, detail, { ttl: 7000, playerId });
  }

  // ===== FILTERS =====
  window.togglePunishFilter = function(type) {
    state.punishFilters[type] = !state.punishFilters[type];
    const btn = $(`#filter${type.charAt(0) + type.slice(1).toLowerCase()}`);
    if (btn) btn.classList.toggle('active', state.punishFilters[type]);
    ui.renderPunishments();
  };

  window.toggleSeverity = function(sev) {
    state.logsFilters.sev[sev] = !state.logsFilters.sev[sev];
    dom()[`sev${sev}`]?.classList.toggle('active', state.logsFilters.sev[sev]);
    ui.renderLogs();
  };

  window.toggleMxOnly = function() {
    state.logsFilters.mxOnly = !state.logsFilters.mxOnly;
    dom().mxOnly.classList.toggle('active', state.logsFilters.mxOnly);
    ui.renderLogs();
  };

  window.toggleLogs = function() {
    state.manualPaused = !state.manualPaused;
    ui.renderLogs();
  };

  window.clearLogs = function() {
    state.logs = [];
    ui.renderLogs();
    toast('info', 'Cleared', 'Logs cleared.');
  };

  window.logsPrevPage = function() {
    state.logsFilters.page = Math.max(1, (state.logsFilters.page || 1) - 1);
    ui.renderLogs();
  };

  window.logsNextPage = function() {
    state.logsFilters.page = (state.logsFilters.page || 1) + 1;
    ui.renderLogs();
  };

  window.openLogsFilterPanel = function() {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    const typeKeys = Object.keys(state.logsFilters.types || {});
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-sliders" style="color:var(--primary-light)"></i>
            <b>Log Filters</b>
          </div>
          <button class="mini" id="lfClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="grid cols-2" style="margin-top:0">
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.INFO ? 'on' : ''}" id="lfInfo"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Info</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.WARN ? 'on' : ''}" id="lfWarn"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Warn</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.ERROR ? 'on' : ''}" id="lfErr"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Error</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.mxOnly ? 'on' : ''}" id="lfMx"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">ModereX Only</div></div>
            </div>
          </div>
          <div class="grid cols-2" style="margin-top:16px">
            ${typeKeys.map(t => `
              <div class="toggle-wrap">
                <button class="toggle ${state.logsFilters.types[t] ? 'on' : ''}" data-type="${t}"><span class="toggle-thumb"></span></button>
                <div class="toggle-meta"><div class="toggle-title">${t.replace('IPBAN','IP-Ban').replace('UNWARN','Unwarn').replace('UNMUTE','Unmute').replace('UNBAN','Unban').replace('EXPIRE','Expires')}</div></div>
              </div>
            `).join('')}
          </div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Saved per account</span>
          <div style="display:flex;gap:10px">
            <button class="btn ghost" id="lfCancel"><i class="fa-solid fa-xmark"></i> Close</button>
            <button class="btn primary" id="lfApply"><i class="fa-solid fa-check"></i> Apply</button>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    const toggleBtn = (btn, key, group) => {
      group[key] = !group[key];
      btn.classList.toggle('on', group[key]);
    };
    $('#lfClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#lfCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#lfInfo', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'INFO', state.logsFilters.sev);
    $('#lfWarn', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'WARN', state.logsFilters.sev);
    $('#lfErr', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'ERROR', state.logsFilters.sev);
    $('#lfMx', overlay).onclick = (e) => { state.logsFilters.mxOnly = !state.logsFilters.mxOnly; e.currentTarget.classList.toggle('on', state.logsFilters.mxOnly); };
    overlay.querySelectorAll('[data-type]').forEach(btn => {
      btn.addEventListener('click', () => {
        const t = btn.dataset.type;
        state.logsFilters.types[t] = !state.logsFilters.types[t];
        btn.classList.toggle('on', state.logsFilters.types[t]);
      });
    });
    $('#lfApply', overlay).onclick = () => { ui.renderLogs(); closeOverlayAnimated(overlay); saveUserPrefs(); };
  };

  // ===== LOGGING =====
  function logEvent(sev, channel, title, detail, meta = {}) {
    const kind = meta.kind || 'event';
    const type = meta.type || (
      kind === 'automod' ? 'AUTOMOD' :
      kind === 'anticheat' ? 'ANTICHEAT' :
      channel === 'anticheat' ? 'ANTICHEAT' :
      channel === 'chat' ? 'CHAT' :
      channel === 'punishment' ? (meta.punType || 'WARN') :
      channel === 'system' ? 'SYSTEM' :
      'SYSTEM'
    );
    state.logs.push({ id: uid('log'), t: now(), sev, channel, title, detail, mx: meta.mx ?? true, playerId: meta.playerId, caseId: meta.caseId, kind, type });
    if (state.logs.length > 300) state.logs.splice(0, state.logs.length - 300);
    ui.renderLogs();
  }

  function logPunishment(playerId, pun) {
    const p = state.players.find(x => x.id === playerId);
    logEvent(pun.type === 'BAN' ? 'ERROR' : 'WARN', 'punishment', `${pun.type} | ${p?.name}`, `${pun.reason} | case ${pun.id.slice(-8)}`, { playerId, caseId: pun.id, kind: 'punishment', punType: pun.type, type: pun.type });
  }

  // ===== SETTINGS =====
  window.toggleSetting = function(key) {
    const ws = window.MX?.ws;
    state.settings[key] = !state.settings[key];

    // Immediately send to server for action settings
    if (ws && ws.isConnected()) {
      if (key === 'chatDisabled') {
        ws.setChatLock(state.settings.chatDisabled);
        window.MX.sounds?.toggle();
        toast('ok', state.settings.chatDisabled ? 'Chat Locked' : 'Chat Unlocked',
          state.settings.chatDisabled ? 'Chat is now disabled for non-staff' : 'Chat is now enabled', {silent: true});
      } else if (key === 'slowEnabled') {
        const seconds = state.settings.slowEnabled ? (parseInt(dom().slowSeconds?.value || '3', 10)) : 0;
        ws.setSlowmode(seconds);
        window.MX.sounds?.toggle();
        toast('ok', state.settings.slowEnabled ? 'Slowmode Enabled' : 'Slowmode Disabled',
          state.settings.slowEnabled ? `Players must wait ${seconds}s between messages` : 'Players can chat freely', {silent: true});
      } else if (key.startsWith('mute')) {
        // Mute settings - send to server
        const muteKey = key.replace('mute', '').toLowerCase();
        const muteKeyMap = { chat: 'chat', msg: 'msg', signs: 'signs', books: 'books', broadcast: 'broadcast', voice: 'voice', voicejoin: 'voiceJoin' };
        const serverKey = muteKeyMap[muteKey] || muteKey;
        ws.updateMuteSettings({ [serverKey]: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Mute Setting Updated', `${key} is now ${state.settings[key] ? 'blocked' : 'allowed'}`, { silent: true });
      } else if (key === 'warnNotify' || key === 'warnAutoEscalate') {
        // Warn settings - send to server
        const warnKeyMap = { warnNotify: 'notify', warnAutoEscalate: 'autoEscalate' };
        ws.updateWarnSettings({ [warnKeyMap[key]]: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Warn Setting Updated', `${key} is now ${state.settings[key] ? 'enabled' : 'disabled'}`, { silent: true });
      } else if (key === 'anticheatReplace') {
        // Anticheat settings - send to server
        ws.updateAnticheatSettings({ rebrandAlerts: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Anticheat Setting Updated', `Alert rebranding is now ${state.settings[key] ? 'enabled' : 'disabled'}`, { silent: true });
      } else {
        // For other settings, mark as unsaved
        ui.markUnsaved('settings', true);
      }
    } else {
      ui.markUnsaved('settings', true);
    }

    ui.renderChatToggles();
    ui.renderIntegrations();
    ui.renderAnticheat();
  };

  // Update staff notification settings (synced with in-game)
  window.updateStaffSetting = function(key, value) {
    const ws = window.MX?.ws;
    state.staffSettings = state.staffSettings || {};
    state.staffSettings[key] = value;

    // Send to server immediately
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { [key]: value });
      window.debugLog('SETTINGS', `Updated setting: ${key} = ${value}`, 'info');
      toast('ok', 'Setting Saved', `${key} updated`, { silent: true });
    }

    ui.renderStaffSettings();
  };

  // ===== ANTICHEAT ALERT CUSTOMIZATION =====
  window.refreshAnticheatData = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      // Request anticheat alerts (detected checks)
      ws.requestAnticheatAlerts();
      // Request saved staff alert preferences from database
      ws.requestStaffAlertPrefs();
      // Request alert presets
      ws.requestAlertPresets();
      window.debugLog('ANTICHEAT', 'Refreshing anticheat data (alerts, prefs, presets)...', 'info');
      toast('info', 'Refreshing', 'Loading anticheat data...');
    } else {
      toast('warn', 'Not Connected', 'Cannot refresh - not connected to server.');
    }
  };

  window.applyPreset = function(presetId) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.applyAlertPreset(presetId);
      toast('ok', 'Applied', `Preset "${presetId}" applied to all checks.`);
    } else {
      toast('warn', 'Not Connected', 'Cannot apply preset - not connected to server.');
    }
  };

  window.applySelectedPreset = function() {
    const select = document.getElementById('acPresetSelect');
    if (select && select.value) {
      window.applyPreset(select.value);
      select.value = '';
    }
  };

  window.updateCheckAlertLevel = function(anticheat, checkName, alertLevel) {
    const ws = window.MX?.ws;
    const prefKey = `${anticheat.toLowerCase()}.${checkName}`;
    const currentPref = state.anticheat.alertPrefs[prefKey] || { thresholdCount: 1, timeWindowSeconds: 60 };

    if (ws && ws.isConnected()) {
      window.debugLog('ANTICHEAT', `Updating ${anticheat}:${checkName} level -> ${alertLevel}`, 'info');
      ws.updateStaffAlertPref(anticheat, checkName, alertLevel, currentPref.thresholdCount, currentPref.timeWindowSeconds);
      // Update local state optimistically
      state.anticheat.alertPrefs[prefKey] = {
        ...currentPref,
        alertLevel: alertLevel
      };
      ui.renderAnticheat();
    } else {
      toast('warn', 'Not Connected', 'Cannot update - not connected to server.');
      window.debugLog('ANTICHEAT', 'Cannot update - not connected', 'error');
    }
  };

  window.updateCheckThreshold = function(anticheat, checkName, thresholdCount, timeWindowSeconds) {
    const ws = window.MX?.ws;
    const prefKey = `${anticheat.toLowerCase()}.${checkName}`;
    const currentPref = state.anticheat.alertPrefs[prefKey] || { alertLevel: 'EVERYONE' };
    const newThreshold = parseInt(thresholdCount, 10) || 1;
    const newWindow = parseInt(timeWindowSeconds, 10) || 60;

    if (ws && ws.isConnected()) {
      window.debugLog('ANTICHEAT', `Updating ${anticheat}:${checkName} threshold -> ${newThreshold} in ${newWindow}s`, 'info');
      ws.updateStaffAlertPref(
        anticheat,
        checkName,
        currentPref.alertLevel,
        newThreshold,
        newWindow
      );
      // Update local state optimistically
      state.anticheat.alertPrefs[prefKey] = {
        ...currentPref,
        thresholdCount: newThreshold,
        timeWindowSeconds: newWindow
      };
    } else {
      toast('warn', 'Not Connected', 'Cannot update - not connected to server.');
      window.debugLog('ANTICHEAT', 'Cannot update - not connected', 'error');
    }
  };

  window.filterAnticheatChecks = function() {
    ui.renderAnticheat();
  };

  window.saveChatSettings = function() {
    const ws = window.MX?.ws;
    const seconds = parseInt(dom().slowSeconds.value || '0', 10);
    state.settings.slowSeconds = seconds;

    if (ws && ws.isConnected() && state.settings.slowEnabled) {
      ws.setSlowmode(seconds);
      toast('ok', 'Saved', `Slowmode set to ${seconds} seconds.`);
    } else {
      ui.markUnsaved('chat', true);
      toast('ok', 'Saved', 'Chat settings saved. Publish to apply.');
    }
  };

  window.clearChatNow = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.clearChat();
      window.MX.sounds?.success();
      toast('ok', 'Cleared', 'Chat cleared on server.', {silent: true});
    } else {
      toast('warn', 'Not Connected', 'Cannot clear chat - not connected to server.');
    }
    logEvent('WARN', 'chat', 'Chat cleared', 'Chat cleared via panel.');
  };

  window.saveIntegrations = function() {
    state.settings.discordWebhook = dom().discordWebhook.value;
    ui.markUnsaved('integrations', true);
    toast('ok', 'Saved', 'Integration settings saved.');
  };

  window.testWebhook = function() {
    if (!state.settings.discordWebhook) { toast('warn', 'No Webhook', 'Configure webhook URL first.'); return; }
    const staffName = state.staffName || 'Staff';
    const payload = {
      username: 'ModereX',
      content: `Test from ModereX (${staffName})`,
      embeds: [
        {
          title: 'ModereX Webhook Test',
          description: 'If you can see this, webhook delivery is working.',
          color: 0x2d7aed
        }
      ]
    };
    fetch(state.settings.discordWebhook, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    }).then(res => {
      if (!res.ok) throw new Error('bad response');
      toast('ok', 'Test Sent', 'Webhook delivered.');
    }).catch(err => {
      console.error('Webhook error:', err);
      const reason = err.message || 'Network error';
      toast('warn', 'Delivery Failed', 'Webhook request failed. Check URL or CORS.');
      if (window.debugLog) window.debugLog('WEBHOOK', `Test failed: ${reason}`, 'error');
    });
  };

  window.setLanguage = function(lang) {
    state.lang = lang;
    ui.markUnsaved('messages', true);
    ui.renderMessages();
  };

  window.resetLang = function() {
    state.lang = 'en_US';
    ui.markUnsaved('messages', false);
    ui.renderMessages();
    toast('info', 'Reset', 'Language reset to English.');
  };

  window.publishSettings = function() {
    setPublishLoading(true);

    // In live mode, send changes to server
    if (isLiveMode && state.authenticated) {
      const promises = [];

      // Sync automod rules if changed
      if (state.unsaved.rules) {
        promises.push(new Promise((resolve) => {
          ws.send('SYNC_AUTOMOD_RULES', { rules: state.rules });
          setTimeout(resolve, 200); // Wait for server acknowledgment
        }));
      }

      // Sync settings if changed
      if (state.unsaved.settings || state.unsaved.chat) {
        promises.push(new Promise((resolve) => {
          ws.send('UPDATE_SETTINGS', {
            chatEnabled: !state.settings.chatDisabled,
            defaultSlowmode: state.settings.slowEnabled ? state.settings.slowSeconds : 0
          });
          setTimeout(resolve, 200);
        }));
      }

      // Wait for all sync operations
      Promise.all(promises).then(() => {
        state.unsaved = {};
        ui.refreshUnsavedUI();
        setPublishLoading(false);
        toast('ok', 'Published', 'All changes synced to server.');
        logEvent('INFO', 'system', 'Settings published', 'Configuration applied to server.');
      }).catch((err) => {
        setPublishLoading(false);
        const reason = err?.message || 'Sync failed';
        toast('error', 'Error', 'Failed to publish some changes.');
        if (window.debugLog) window.debugLog('SYNC', `Publish failed: ${reason}`, 'error');
      });
    } else {
      // Demo mode - just clear unsaved state
      setTimeout(() => {
        state.unsaved = {};
        ui.refreshUnsavedUI();
        setPublishLoading(false);
        toast('ok', 'Published', 'All changes published (demo mode).');
        logEvent('INFO', 'system', 'Settings published', 'Configuration applied.');
      }, 1200);
    }
  };

  // ===== WIZARD =====
  window.openWizard = function() {
    dom().wizardOverlay.classList.add('show');
    state.wizard.step = 0;
    renderWizard();
  };

  window.closeWizard = function(e) {
    if (e) e.stopPropagation?.();
    dom().wizardOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().wizardOverlay.classList.remove('show', 'fade-out');
    }, 220);
  };

  window.wizardBack = function() {
    state.wizard.step = Math.max(0, state.wizard.step - 1);
    renderWizard();
  };

  window.wizardNext = function() {
    if (state.wizard.step === 4) { ui.markUnsaved('wizard', true); toast('ok', 'Complete', 'Review and publish.'); closeWizard(); ui.renderAll(); return; }
    state.wizard.step = Math.min(4, state.wizard.step + 1);
    renderWizard();
  };

  function renderWizard() {
    dom().wizStepChip.innerHTML = `<i class="fa-solid fa-list-check"></i> Step ${state.wizard.step + 1}/5`;
    const steps = [
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-plug" style="color:var(--primary-light)"></i> Plugin Detection</h3><p>Detecting installed plugins and dependencies.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-clock" style="color:var(--warn)"></i> Timezone</h3><p>Set server timezone for timestamps.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-key" style="color:var(--accent-light)"></i> Permissions</h3><p>Configure staff permissions.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-robot" style="color:var(--ok)"></i> Automod</h3><p>Create default automod rules.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-server" style="color:var(--primary-light)"></i> Connectivity</h3><p>Verifying service connections.</p></div>`
    ];
    dom().wizardBody.innerHTML = steps[state.wizard.step];
  }

  // ===== SUPPORT BUTTON =====
  window.openSupport = function() {
    // Support functionality - to be implemented
    toast('info', 'Support', 'Support feature coming soon.');
  };

  window.toggleTesterPanel = function() {
    dom().testerPanel?.classList.toggle('show');
  };

  // ===== BACKGROUND ANIMATION =====
  function setupBackgroundAnimation() {
    const canvas = document.getElementById('bgCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let w, h;
    const particles = [];
    const maxDist = 180;

    function resize() { w = canvas.width = window.innerWidth; h = canvas.height = window.innerHeight; }
    function createParticle() {
      return {
        x: Math.random() * w,
        y: Math.random() * h,
        vx: (Math.random() - 0.5) * 0.45,
        vy: (Math.random() - 0.5) * 0.45,
        r: Math.random() * 2 + 1,
        a: Math.random() * 0.35 + 0.12,
        tw: Math.random() * 1.2
      };
    }

    // Get theme color RGB values for canvas drawing
    function getThemeRGB() {
      const color = state.userSettings?.themeColor || '#2d7aed';
      const r = parseInt(color.slice(1, 3), 16);
      const g = parseInt(color.slice(3, 5), 16);
      const b = parseInt(color.slice(5, 7), 16);
      // Create a lighter version for particles (add 80 to each, clamped to 255)
      const lr = Math.min(r + 90, 255);
      const lg = Math.min(g + 90, 255);
      const lb = Math.min(b + 90, 255);
      return { r, g, b, lr, lg, lb };
    }

    function draw() {
      ctx.clearRect(0, 0, w, h);
      const theme = getThemeRGB();

      for (const p of particles) {
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0) p.x = w; if (p.x > w) p.x = 0; if (p.y < 0) p.y = h; if (p.y > h) p.y = 0;
        p.tw += 0.01;
      }

      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          const a = particles[i];
          const b = particles[j];
          const dx = a.x - b.x;
          const dy = a.y - b.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < maxDist) {
            const alpha = (1 - dist / maxDist) * 0.5;
            ctx.strokeStyle = `rgba(${theme.r}, ${theme.g}, ${theme.b}, ${alpha})`;
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(a.x, a.y);
            ctx.lineTo(b.x, b.y);
            ctx.stroke();
          }
        }
      }

      for (const p of particles) {
        const glow = p.a + Math.sin(p.tw) * 0.05;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(${theme.lr}, ${theme.lg}, ${theme.lb}, ${glow})`;
        ctx.fill();
      }
      requestAnimationFrame(draw);
    }

    resize();
    window.addEventListener('resize', resize);
    for (let i = 0; i < 120; i++) particles.push(createParticle());
    draw();
  }

  // ===== CLOCK & TIMERS =====
  function startClock() {
    const update = () => { dom().timeChip.innerHTML = `<i class="fa-regular fa-clock"></i> ${fmtClock()}`; };
    update();
    setInterval(update, 1000);
  }

  // Duration countdown - updates every second
  function startDurationCountdown() {
    setInterval(() => {
      // Update punishment durations in state
      const nowMs = Date.now();
      let needsUpdate = false;

      state.punishments.forEach(pun => {
        if (pun.expiresAt && pun.expiresAt > 0 && pun.expiresAt !== -1) {
          const remaining = pun.expiresAt - nowMs;
          if (remaining > 0) {
            pun.remainingMs = remaining;
            pun.remainingDisplay = formatDurationShort(remaining);
            needsUpdate = true;
          } else if (pun.active) {
            // Expired - mark as inactive
            pun.active = false;
            pun.remainingDisplay = 'Expired';
            needsUpdate = true;
          }
        }
      });

      // Re-render if any durations changed
      if (needsUpdate) {
        ui.renderPunishments();
      }
    }, 1000);
  }

  // Format duration in short form (e.g., "2d 5h 30m")
  function formatDurationShort(ms) {
    if (ms <= 0) return 'Expired';
    if (ms === -1) return 'Permanent';

    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30);

    if (months > 0) {
      const remainingDays = days % 30;
      return remainingDays > 0 ? `${months}mo ${remainingDays}d` : `${months}mo`;
    }
    if (days > 0) {
      const remainingHours = hours % 24;
      return remainingHours > 0 ? `${days}d ${remainingHours}h` : `${days}d`;
    }
    if (hours > 0) {
      const remainingMinutes = minutes % 60;
      return remainingMinutes > 0 ? `${hours}h ${remainingMinutes}m` : `${hours}h`;
    }
    if (minutes > 0) {
      const remainingSecs = seconds % 60;
      return remainingSecs > 0 ? `${minutes}m ${remainingSecs}s` : `${minutes}m`;
    }
    return `${seconds}s`;
  }

  // Status indicator updates with ping-based color coding
  function updateStatusIndicator(status, ping) {
    const statusChip = document.getElementById('statusChip');
    const pingText = document.getElementById('pingText');
    const statusDot = document.getElementById('statusDot');

    if (!statusChip || !pingText) return;

    // Update ping text
    pingText.textContent = ping > 0 ? ping : '--';

    // Determine status based on connection and ping
    let statusClass = 'chip';
    let dotClass = '';
    let statusLabel = 'Disconnected';

    if (status === 'connected' && ping > 0) {
      if (ping < 100) {
        // Green: Excellent (<100ms)
        statusClass = 'chip ok';
        dotClass = 'ok';
        statusLabel = 'Excellent';
      } else if (ping < 300) {
        // Yellow: Good (100-300ms)
        statusClass = 'chip warn';
        dotClass = 'warn';
        statusLabel = 'Good';
      } else {
        // Red: Poor (>300ms)
        statusClass = 'chip bad';
        dotClass = 'bad';
        statusLabel = 'Poor';
      }
    } else if (status === 'connected') {
      statusClass = 'chip ok';
      dotClass = 'ok';
      statusLabel = 'Connected';
    } else if (status === 'saving') {
      statusClass = 'chip warn';
      dotClass = 'warn';
      statusLabel = 'Saving...';
    } else {
      statusClass = 'chip bad';
      dotClass = 'bad';
      statusLabel = 'Disconnected';
    }

    statusChip.className = statusClass;
    statusChip.title = `${statusLabel}${ping > 0 ? ' - ' + ping + 'ms latency' : ''}`;

    // Also update sidebar status dot
    if (statusDot) {
      statusDot.className = 'dot' + (dotClass ? ' ' + dotClass : '');
    }
  }

  function showSavingIndicator() {
    const savingChip = document.getElementById('savingChip');
    if (savingChip) savingChip.style.display = '';
  }

  function hideSavingIndicator() {
    const savingChip = document.getElementById('savingChip');
    if (savingChip) savingChip.style.display = 'none';
  }

  // ===== SIMULATION =====
  function startSimulation() {
    const chatSamples = [
      'gg', 'hello', 'anyone trading?', 'looking for diamonds', 'who wants to duel', 'lmao', 'nice base', 'brb',
      'selling iron', 'need help at spawn', 'tp me?', 'grats', 'that was close', 'wow', 'lag?', 'server is smooth',
      'anyone online?', 'who built this', 'party up', 'join my town', 'lol', 'ok', 'sure', 'thanks', 'ggs',
      'caps test', 'WOW THATS HUGE', 'check this link', 'kys', 'hey', 'heya', 'heyy', 'spam spam spam',
      'this is awesome', 'trade at /warp shop', 'any mods on?', 'help', 'new player here', 'testing', 'anyone want to mine',
      'lets go', 'free items', 'not really', 'haha', 'ok ok', 'gg again', 'dm me', 'invite me', 'nope', 'lolol',
      'what time is it', 'server restart soon?', 'can you unban', 'why muted', 'oops', 'my bad', 'forgive me',
      'party chat', 'where is end', 'nether?', 'portal coords', 'meet at spawn', 'be right back', 'afk',
      'this is a long message to test caps', 'BUY NOW', 'HELLO EVERYONE', 'visit example.com', 'discord.gg/abc',
      'I love this', 'clutch', 'speedrun time', 'nice pvp', 'gg wp', 'banned?', 'moderator?', 'help me',
      'trade key for crate', 'anyone have elytra', 'mending book', 'enchanting', 'villager trading',
      'hello hello', 'repeat repeat', 'repeating', 'ok done', 'gg bye'
    ];
    const chatNouns = ['spawn', 'market', 'base', 'farm', 'mine', 'nether', 'end', 'village', 'arena', 'shop'];
    const chatVerbs = ['looking for', 'selling', 'buying', 'trading', 'need help with', 'anyone seen'];
    const chatAdj = ['rare', 'epic', 'tiny', 'huge', 'fast', 'slow', 'broken', 'new'];
    for (const noun of chatNouns) {
      for (const verb of chatVerbs) {
        chatSamples.push(`${verb} ${noun}`);
      }
    }
    for (const adj of chatAdj) {
      for (const noun of chatNouns) {
        chatSamples.push(`${adj} ${noun}`);
      }
    }
    while (chatSamples.length < 220) {
      chatSamples.push(`message ${chatSamples.length + 1}`);
    }
    const commandSamples = [
      '/spawn','/home','/warp shop','/tpa Admin','/msg Player hey','/balance','/sell','/buy','/kit starter','/pay Admin 50',
      '/sethome base','/home base','/warp pvp','/warp nether','/warp end','/spawn','/msg ModA hello','/ignore Player',
      '/claim','/unclaim','/tpaccept','/tpdeny','/trade Player','/ah sell','/ah list','/r ok','/nick CoolGuy',
      '/mail send','/msg Helper help','/rules','/help','/report Player','/warp crates','/vote','/menu','/discord',
      '/warp market','/warp arena','/warp village','/warp endcity','/warp farms','/warp bank','/warp boss',
      '/kit daily','/kit tools','/kit food','/shop','/trade accept','/tpa ModB','/tpahere Helper',
      '/party create','/party invite','/party leave','/home main','/home farm','/home mine',
      '/bal top','/pay ModA 25','/msg Admin hi','/ignore list','/seen Player','/ping','/vote claim'
    ];
    const cmdBases = ['/warp', '/kit', '/home', '/msg', '/pay', '/tpa'];
    const cmdArgs = ['alpha', 'beta', 'gamma', 'delta', 'omega', 'spawn', 'shop', 'pvp', 'nether', 'end'];
    for (const base of cmdBases) {
      for (const arg of cmdArgs) {
        commandSamples.push(`${base} ${arg}`);
      }
    }

    for (let i = 0; i < 8; i++) {
      const p = pick(state.players.filter(x => x.status === 'online')) || pick(state.players);
      logEvent('INFO', 'chat', `Chat | ${p.name}`, pick(['hey', 'gg', 'lol', 'trading?']), { playerId: p.id, kind: 'chat' });
    }

    for (let i = 0; i < 220; i++) {
      const p = pick(state.players);
      const msg = pick(chatSamples);
      const hits = evaluateAutomodMessage(p.id, msg);
      logEvent(hits.length ? 'WARN' : 'INFO', 'chat', `Chat | ${p.name}`, msg, { playerId: p.id, kind: 'chat', type: 'CHAT' });
      hits.forEach(rule => {
        logEvent('WARN', 'automod', `Automod | ${rule.name}`, `${p.name} triggered`, { playerId: p.id, kind: 'automod', type: 'AUTOMOD' });
        applyAutomodAction(p.id, rule, msg);
      });
    }

    for (let i = 0; i < 260; i++) {
      const p = pick(state.players);
      const cmd = pick(commandSamples);
      if (!p.recentCommands) p.recentCommands = [];
      p.recentCommands.push({ cmd, t: now() - Math.floor(Math.random() * 86400000 * 5) });
      logEvent('INFO', 'system', `Command | ${p.name}`, cmd, { playerId: p.id, kind: 'command', type: 'COMMAND' });
    }

    simulateConnect();

    setInterval(() => {
      if (state.autoPaused) return;
      const actor = pick(state.players.filter(p => p.status === 'online')) || pick(state.players);
      const roll = Math.random();
      if (roll < 0.6) {
        const msg = pick(chatSamples);
        const hits = evaluateAutomodMessage(actor.id, msg);
        logEvent(hits.length ? 'WARN' : 'INFO', 'chat', `Chat | ${actor.name}`, msg, { playerId: actor.id, kind: 'chat', type: 'CHAT' });
        hits.forEach(rule => {
          logEvent('WARN', 'automod', `Automod | ${rule.name}`, `${actor.name} triggered`, { playerId: actor.id, kind: 'automod', type: 'AUTOMOD' });
          applyAutomodAction(actor.id, rule, msg);
        });
      } else if (roll < 0.8) {
        logEvent('INFO', 'system', 'System', 'Background task completed');
      } else {
        const staffer = pick(state.staff);
        const target = pick(state.players);
        const types = ['WARN', 'MUTE', 'BAN'];
        const type = pick(types);
        executePunishment({ playerId: target.id, type, reason: 'Staff action', duration: type === 'BAN' ? '7d' : type === 'MUTE' ? '2h' : '' });
        logEvent('WARN', 'system', `${staffer.name} action`, `${type} issued to ${target.name}`, { kind: 'system' });
        ui.renderPunishments();
        ui.renderPlayers();
      }
      ui.renderDashboard();
    }, 2500);
  }

  function simulateConnect() {
    const username = 'YaBoiCameronYT';
    const isGeyser = false;
    state.currentUser = {
      name: username,
      platform: 'Java',
      geyser: isGeyser,
      connectedAt: now()
    };
    ui.renderTopUser();
    loadUserPrefs();
    logEvent('INFO', 'system', 'Connect command', `${username} connected (${isGeyser ? 'Geyser' : 'Java'})`, { kind: 'system' });
  }

  // ===== EVENT SETUP =====
  function setupEventListeners() {
    $$('.sb-item').forEach(item => item.addEventListener('click', () => { if (item.dataset.page) go(item.dataset.page); }));

    dom().playerSearch?.addEventListener('input', ui.renderPlayers);
    dom().punishSearch?.addEventListener('input', ui.renderPunishments);
    dom().templateSearch?.addEventListener('input', ui.renderTemplates);
    dom().watchSearch?.addEventListener('input', ui.renderWatchlist);
    dom().msgSearch?.addEventListener('input', ui.renderMessages);
    dom().logsSearch?.addEventListener('input', ui.renderLogs);
    dom().anticheatSearch?.addEventListener('input', ui.renderAnticheat);
    dom().logsPageSize?.addEventListener('change', (e) => {
      state.logsFilters.pageSize = parseInt(e.target.value, 10) || 100;
      state.logsFilters.page = 1;
      saveUserPrefs();
      ui.renderLogs();
    });
    dom().slowSeconds?.addEventListener('input', (e) => {
      state.settings.slowSeconds = parseInt(e.target.value || '0', 10);
      ui.markUnsaved('settings', true);
    });

    let lastAutoPaused = state.autoPaused;
    dom().logsBox?.addEventListener('scroll', () => {
      const box = dom().logsBox;
      if (!box) return;
      const atBottom = box.scrollTop + box.clientHeight >= box.scrollHeight - 8;
      state.autoPaused = !atBottom;
      if (state.autoPaused !== lastAutoPaused) {
        lastAutoPaused = state.autoPaused;
        ui.renderLogs();
      }
    });

    dom().punishTemplate?.addEventListener('change', (e) => applyTemplateToPunish(e.target.value));
    dom().punishTypeSelect?.addEventListener('change', (e) => setPunishType(e.target.value));
    dom().punishTarget?.addEventListener('change', (e) => {
      state.selectedPlayerId = e.target.value;
      renderEvidenceOptions(e.target.value, dom().punishEvidencePick, dom().punishEvidencePreview);
      updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
    });
    dom().punishEvidencePick?.addEventListener('change', () => updateEvidencePreviewFor(dom().punishEvidencePick, dom().punishEvidencePreview));

    dom().punishCreatePlayer?.addEventListener('input', renderPunishCreateList);
    dom().punishCreatePlayer?.addEventListener('focus', renderPunishCreateList);
    dom().punishCreatePlayer?.addEventListener('blur', () => {
      const combo = dom().punishCreateList?.closest('.combo');
      setTimeout(() => combo?.classList.remove('open'), 150);
    });
    dom().punishCreateType?.addEventListener('change', (e) => {
      state.pendingPunishType = e.target.value;
      updatePunishTitle(dom().punishCreateTitle, e.target.value, state.punishCreatePlayerId);
    });
    dom().punishCreateTemplate?.addEventListener('change', (e) => applyTemplateToPunishCreate(e.target.value));
    dom().punishCreateEvidencePick?.addEventListener('change', () => updateEvidencePreviewFor(dom().punishCreateEvidencePick, dom().punishCreateEvidencePreview));
    dom().authToken?.addEventListener('keydown', (e) => { if (e.key === 'Enter') login(); });

    dom().globalSearch?.addEventListener('input', (e) => {
      const query = e.target.value.trim();
      if (query.length >= 1) {
        performLiveSearch(query);
      } else {
        hideSearchDropdown();
      }
    });

    dom().globalSearch?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        if (searchState.selectedIndex >= 0 && searchState.results.length > 0) {
          selectSearchResult(searchState.results[searchState.selectedIndex]);
          hideSearchDropdown();
        } else if (e.target.value.trim()) {
          performGlobalSearch(e.target.value.trim());
        }
      } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        navigateSearchResults(1);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        navigateSearchResults(-1);
      } else if (e.key === 'Escape') {
        hideSearchDropdown();
        e.target.blur();
      }
    });

    dom().globalSearch?.addEventListener('blur', () => {
      setTimeout(() => hideSearchDropdown(), 200);
    });

    dom().globalSearch?.addEventListener('focus', (e) => {
      const query = e.target.value.trim();
      if (query.length >= 1) {
        performLiveSearch(query);
      }
    });

    document.addEventListener('keydown', (e) => {
      if (e.ctrlKey && e.key === 'k') { e.preventDefault(); dom().globalSearch?.focus(); }
      if (e.key === 'Escape') { closeDrawer(); closePunishModal(); closePunishCreateModal(); closeDetailsModal(); closeWizard(); closeContextMenu(); hideSearchDropdown(); }
    });

    document.addEventListener('click', (e) => {
      if (contextMenuEl && !e.target.closest('.context-menu')) closeContextMenu();
    });

    document.addEventListener('contextmenu', (e) => {
      const row = e.target.closest('[data-player-id]');
      if (!row) return;
      e.preventDefault();
      openContextMenu(row.dataset.playerId, e.clientX, e.clientY);
    });

    window.addEventListener('scroll', closeContextMenu, true);
  }

  // ===== GLOBAL SEARCH =====

  // Search state
  const searchState = {
    results: [],
    selectedIndex: -1,
    debounceTimer: null
  };

  // Pages that can be searched
  const searchablePages = [
    { id: 'dashboard', name: 'Dashboard', icon: 'fa-gauge-high', keywords: ['home', 'overview', 'stats', 'activity'] },
    { id: 'players', name: 'Player Management', icon: 'fa-users', keywords: ['users', 'list', 'online'] },
    { id: 'punishments', name: 'Punishments', icon: 'fa-gavel', keywords: ['bans', 'mutes', 'kicks', 'warns', 'cases'] },
    { id: 'templates', name: 'Templates', icon: 'fa-bookmark', keywords: ['presets', 'quick', 'saved'] },
    { id: 'automod', name: 'Automod Rules', icon: 'fa-robot', keywords: ['filter', 'chat', 'spam', 'swear'] },
    { id: 'anticheat', name: 'Anticheat', icon: 'fa-shield-halved', keywords: ['hacks', 'cheats', 'alerts'] },
    { id: 'watchlist', name: 'Watchlist', icon: 'fa-eye', keywords: ['monitor', 'watch', 'track'] },
    { id: 'replay', name: 'Replays', icon: 'fa-film', keywords: ['recording', 'playback', 'session'] },
    { id: 'livelogs', name: 'Live Logs', icon: 'fa-terminal', keywords: ['console', 'events', 'stream'] },
    { id: 'staffchat', name: 'Staff Chat', icon: 'fa-comments', keywords: ['team', 'message', 'communicate'] },
    { id: 'mysettings', name: 'My Settings', icon: 'fa-user-gear', keywords: ['preferences', 'sounds', 'notifications'] },
    { id: 'messages', name: 'Messages', icon: 'fa-language', keywords: ['lang', 'text', 'translate'] },
    { id: 'actions', name: 'Actions', icon: 'fa-bolt', keywords: ['quick', 'chat', 'kick all'] },
    { id: 'integrations', name: 'Integrations', icon: 'fa-plug', keywords: ['luckperms', 'plugins', 'hooks'] }
  ];

  // Settings that can be searched (with element IDs for auto-scroll)
  const searchableSettings = [
    { name: 'Sound Effects', page: 'mysettings', keywords: ['audio', 'notification', 'mute', 'volume'], elementId: 'soundsEnabled' },
    { name: 'Volume Control', page: 'mysettings', keywords: ['audio', 'volume', 'slider'], elementId: 'volumeSlider' },
    { name: 'Auto Sign-in', page: 'mysettings', keywords: ['device', 'trust', 'remember', 'login'], elementId: 'deviceTrustEnabled' },
    { name: 'Debug Mode', page: 'mysettings', keywords: ['developer', 'debug', 'logging', 'console'], elementId: 'debugModeEnabled' },
    { name: 'Theme Color', page: 'mysettings', keywords: ['color', 'appearance', 'theme', 'blue', 'red', 'green'], elementId: 'themePresets' },
    { name: 'Background Pattern', page: 'mysettings', keywords: ['pattern', 'background', 'aurora', 'stars', 'waves'], elementId: 'patternGrid' },
    { name: 'Automod Alerts', page: 'mysettings', keywords: ['automod', 'filter', 'notify'], elementId: 'alertAutomod' },
    { name: 'Command Alerts', page: 'mysettings', keywords: ['command', 'notify'], elementId: 'alertCommands' },
    { name: 'Punishment Alerts', page: 'mysettings', keywords: ['ban', 'mute', 'kick', 'warn'], elementId: 'alertPunishments' },
    // Join/Leave alerts removed - now a global config setting
    { name: 'Watchlist Alerts', page: 'mysettings', keywords: ['watchlist', 'monitor', 'notify'], elementId: 'acModeWatchlist' },
    { name: 'Anticheat Alert Mode', page: 'mysettings', keywords: ['anticheat', 'hack', 'cheat', 'mode'], elementId: 'acModeAll' },
    { name: 'Chat Lock', page: 'actions', keywords: ['disable', 'mute all', 'lock chat'] },
    { name: 'Slowmode', page: 'actions', keywords: ['rate limit', 'spam', 'slow'] },
    { name: 'Kick All', page: 'actions', keywords: ['clear', 'server', 'disconnect'] }
  ];

  function performLiveSearch(query) {
    clearTimeout(searchState.debounceTimer);
    searchState.debounceTimer = setTimeout(() => {
      const q = query.toLowerCase();
      const results = [];

      // Search players (limit 5)
      const playerMatches = state.players
        .filter(p => p.name.toLowerCase().includes(q))
        .slice(0, 5)
        .map(p => ({
          type: 'player',
          id: p.id,
          title: p.name,
          subtitle: p.online ? 'Online' : 'Offline',
          icon: 'fa-user',
          data: p
        }));
      if (playerMatches.length > 0) {
        results.push({ category: 'Players', items: playerMatches });
      }

      // Search punishments by case ID (limit 5)
      const caseMatches = state.punishments
        .filter(p => p.caseId && p.caseId.toLowerCase().includes(q))
        .slice(0, 5)
        .map(p => ({
          type: 'case',
          id: p.id,
          title: p.caseId,
          subtitle: `${p.type} - ${p.playerName}`,
          icon: 'fa-gavel',
          data: p
        }));
      if (caseMatches.length > 0) {
        results.push({ category: 'Punishments', items: caseMatches });
      }

      // Search templates (limit 3)
      const templateMatches = (state.templates || [])
        .filter(t => t.name.toLowerCase().includes(q))
        .slice(0, 3)
        .map(t => ({
          type: 'template',
          id: t.id,
          title: t.name,
          subtitle: t.type,
          icon: 'fa-bookmark',
          data: t
        }));
      if (templateMatches.length > 0) {
        results.push({ category: 'Templates', items: templateMatches });
      }

      // Search pages (limit 3)
      const pageMatches = searchablePages
        .filter(p => p.name.toLowerCase().includes(q) || p.keywords.some(k => k.includes(q)))
        .slice(0, 3)
        .map(p => ({
          type: 'page',
          id: p.id,
          title: p.name,
          subtitle: 'Page',
          icon: p.icon,
          data: p
        }));
      if (pageMatches.length > 0) {
        results.push({ category: 'Pages', items: pageMatches });
      }

      // Search settings (limit 3)
      const settingMatches = searchableSettings
        .filter(s => s.name.toLowerCase().includes(q) || s.keywords.some(k => k.includes(q)))
        .slice(0, 3)
        .map(s => ({
          type: 'setting',
          id: s.page,
          title: s.name,
          subtitle: `In ${s.page}`,
          icon: 'fa-gear',
          data: s
        }));
      if (settingMatches.length > 0) {
        results.push({ category: 'Settings', items: settingMatches });
      }

      // Search automod rules (limit 4)
      const automodMatches = (state.automodRules || [])
        .filter(r => r.name?.toLowerCase().includes(q) || r.id?.toLowerCase().includes(q))
        .slice(0, 4)
        .map(r => ({
          type: 'automod',
          id: r.id,
          title: r.name || r.id,
          subtitle: r.enabled ? 'Enabled' : 'Disabled',
          icon: 'fa-robot',
          data: r
        }));
      if (automodMatches.length > 0) {
        results.push({ category: 'Automod Rules', items: automodMatches });
      }

      // Search anticheat checks (limit 4)
      const anticheatChecks = [];
      (state.anticheat?.anticheats || []).forEach(ac => {
        (ac.checks || []).forEach(check => {
          if (check.name?.toLowerCase().includes(q) || check.displayName?.toLowerCase().includes(q)) {
            anticheatChecks.push({
              type: 'anticheat_check',
              id: `${ac.name}:${check.name}`,
              title: check.displayName || check.name,
              subtitle: `${ac.name} - ${check.category || 'Check'}`,
              icon: 'fa-shield-halved',
              data: { anticheat: ac.name, check }
            });
          }
        });
      });
      if (anticheatChecks.length > 0) {
        results.push({ category: 'Anticheat Checks', items: anticheatChecks.slice(0, 4) });
      }

      // Search developer checklist items (limit 5)
      const checklistMatches = (state.devChecklist || [])
        .filter(item => {
          const title = (item.title || '').toLowerCase();
          const desc = (item.description || '').toLowerCase();
          const category = (item.category || '').toLowerCase();
          return title.includes(q) || desc.includes(q) || category.includes(q);
        })
        .slice(0, 5)
        .map(item => ({
          type: 'checklist',
          id: item.id,
          title: item.title,
          subtitle: `${item.category || 'Uncategorized'} - ${item.checked ? 'Completed' : 'Pending'}`,
          icon: item.checked ? 'fa-check-circle' : 'fa-circle',
          data: item
        }));
      if (checklistMatches.length > 0) {
        results.push({ category: 'Developer Checklist', items: checklistMatches });
      }

      // Flatten results for keyboard navigation
      searchState.results = results.flatMap(r => r.items);
      searchState.selectedIndex = searchState.results.length > 0 ? 0 : -1;

      renderSearchResults(results, query);
    }, 100);
  }

  function renderSearchResults(results, query) {
    const dropdown = document.getElementById('searchDropdown');
    const container = document.getElementById('searchResults');
    if (!dropdown || !container) return;

    if (results.length === 0) {
      container.innerHTML = `
        <div class="gsearch-empty">
          <i class="fa-solid fa-magnifying-glass"></i>
          <p>No results found for "${escapeHtml(query)}"</p>
        </div>
      `;
      dropdown.style.display = 'block';
      return;
    }

    let html = '';
    let globalIndex = 0;

    for (const category of results) {
      html += `<div class="gsearch-category">
        <div class="gsearch-category-title">${category.category}</div>`;

      for (const item of category.items) {
        const isSelected = globalIndex === searchState.selectedIndex;
        const highlightedTitle = highlightMatch(item.title, query);

        html += `
          <div class="gsearch-item ${isSelected ? 'selected' : ''}"
               data-index="${globalIndex}"
               onclick="selectSearchResultByIndex(${globalIndex})">
            <div class="gsearch-item-icon ${item.type}">
              <i class="fa-solid ${item.icon}"></i>
            </div>
            <div class="gsearch-item-content">
              <div class="gsearch-item-title">${highlightedTitle}</div>
              <div class="gsearch-item-subtitle">${escapeHtml(item.subtitle)}</div>
            </div>
          </div>
        `;
        globalIndex++;
      }

      html += '</div>';
    }

    container.innerHTML = html;
    dropdown.style.display = 'block';
  }

  function highlightMatch(text, query) {
    const escaped = escapeHtml(text);
    const q = query.toLowerCase();
    const idx = text.toLowerCase().indexOf(q);
    if (idx === -1) return escaped;

    const before = escapeHtml(text.substring(0, idx));
    const match = escapeHtml(text.substring(idx, idx + query.length));
    const after = escapeHtml(text.substring(idx + query.length));
    return `${before}<mark>${match}</mark>${after}`;
  }

  function navigateSearchResults(direction) {
    if (searchState.results.length === 0) return;

    searchState.selectedIndex += direction;
    if (searchState.selectedIndex < 0) {
      searchState.selectedIndex = searchState.results.length - 1;
    } else if (searchState.selectedIndex >= searchState.results.length) {
      searchState.selectedIndex = 0;
    }

    // Update UI
    const container = document.getElementById('searchResults');
    if (!container) return;

    container.querySelectorAll('.gsearch-item').forEach((el, idx) => {
      el.classList.toggle('selected', idx === searchState.selectedIndex);
    });

    // Scroll into view
    const selected = container.querySelector('.gsearch-item.selected');
    if (selected) {
      selected.scrollIntoView({ block: 'nearest' });
    }
  }

  window.selectSearchResultByIndex = function(index) {
    if (index >= 0 && index < searchState.results.length) {
      selectSearchResult(searchState.results[index]);
      hideSearchDropdown();
    }
  };

  function selectSearchResult(result) {
    if (!result) return;

    switch (result.type) {
      case 'player':
        go('players');
        setTimeout(() => openDrawer(result.id), 100);
        break;
      case 'case':
        go('punishments');
        setTimeout(() => openPunishmentDetails(result.id), 100);
        break;
      case 'template':
        go('templates');
        setTimeout(() => {
          // Try to scroll to the template
          const templateEl = document.querySelector(`[data-template-id="${result.id}"]`);
          if (templateEl) {
            templateEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            templateEl.classList.add('highlight-flash');
            setTimeout(() => templateEl.classList.remove('highlight-flash'), 2000);
          }
        }, 150);
        break;
      case 'page':
        go(result.id);
        break;
      case 'setting':
        go(result.data.page);
        setTimeout(() => {
          // Try to scroll to the setting element
          const elementId = result.data.elementId;
          if (elementId) {
            const el = document.getElementById(elementId);
            if (el) {
              el.scrollIntoView({ behavior: 'smooth', block: 'center' });
              el.classList.add('highlight-flash');
              setTimeout(() => el.classList.remove('highlight-flash'), 2000);
            }
          }
        }, 150);
        break;
      case 'automod':
        go('automod');
        setTimeout(() => {
          // Try to scroll to the rule
          const ruleEl = document.querySelector(`[data-rule-id="${result.id}"]`);
          if (ruleEl) {
            ruleEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            ruleEl.classList.add('highlight-flash');
            setTimeout(() => ruleEl.classList.remove('highlight-flash'), 2000);
          }
        }, 150);
        break;
      case 'anticheat_check':
        go('anticheat');
        setTimeout(() => {
          // Try to scroll to the check
          const checkName = result.data?.check?.name;
          const acName = result.data?.anticheat;
          if (checkName && acName) {
            const checkEl = document.querySelector(`[data-check="${checkName}"][data-ac="${acName}"]`);
            if (checkEl) {
              checkEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
              checkEl.classList.add('highlight-flash');
              setTimeout(() => checkEl.classList.remove('highlight-flash'), 2000);
            }
          }
        }, 150);
        break;
      case 'checklist':
        // Navigate to developer tools page and highlight the checklist item
        if (state.settings?.developerMode) {
          go('devtools');
          setTimeout(() => {
            const itemEl = document.querySelector(`[data-checklist-id="${result.id}"]`);
            if (itemEl) {
              itemEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
              itemEl.classList.add('highlight-flash');
              setTimeout(() => itemEl.classList.remove('highlight-flash'), 2000);
            }
          }, 150);
        } else {
          toast('info', 'Developer Mode Required', 'Enable Developer Mode in Settings to view checklist');
        }
        break;
    }

    // Clear search
    const searchInput = document.getElementById('globalSearch');
    if (searchInput) searchInput.value = '';
  }

  function hideSearchDropdown() {
    const dropdown = document.getElementById('searchDropdown');
    if (dropdown) dropdown.style.display = 'none';
    searchState.results = [];
    searchState.selectedIndex = -1;
  }

  function performGlobalSearch(query) {
    if (!query) return;
    const q = query.toLowerCase();

    // Check for filter prefixes
    if (q.startsWith('punishment:') || q.startsWith('case:')) {
      const caseId = q.split(':')[1].trim().toUpperCase();
      const pun = state.punishments.find(p => p.caseId && p.caseId.toUpperCase().includes(caseId));
      if (pun) {
        go('punishments');
        setTimeout(() => openPunishmentDetails(pun.id), 100);
      } else {
        toast('info', 'Not Found', 'No punishment found with that case ID');
      }
      return;
    }

    if (q.startsWith('player:')) {
      const playerName = q.split(':')[1].trim();
      const p = state.players.find(x => x.name.toLowerCase().includes(playerName));
      if (p) {
        go('players');
        setTimeout(() => openDrawer(p.id), 100);
      } else {
        toast('info', 'Not Found', 'No player found with that name');
      }
      return;
    }

    if (q.startsWith('setting:') || q.startsWith('action:')) {
      const settingName = q.split(':')[1].trim();
      if (settingName.includes('sound') || settingName.includes('audio')) {
        go('my-settings');
        toast('info', 'Sound Settings', 'Scroll to Sound Settings section');
      } else if (settingName.includes('sign') || settingName.includes('device') || settingName.includes('trust')) {
        go('my-settings');
        toast('info', 'Device Trust', 'Scroll to Auto Sign-in section');
      } else {
        go('actions');
      }
      return;
    }

    if (q.startsWith('template:')) {
      const templateName = q.split(':')[1].trim();
      const t = state.templates.find(x => x.name.toLowerCase().includes(templateName));
      if (t) {
        go('templates');
        toast('info', 'Template Found', t.name);
      } else {
        toast('info', 'Not Found', 'No template found with that name');
      }
      return;
    }

    // No filter - search everything
    // Try players first
    const player = state.players.find(x => x.name.toLowerCase().includes(q));
    if (player) {
      go('players');
      setTimeout(() => openDrawer(player.id), 100);
      return;
    }

    // Try punishments by case ID
    const punishment = state.punishments.find(p => p.caseId && p.caseId.toLowerCase().includes(q));
    if (punishment) {
      go('punishments');
      setTimeout(() => openPunishmentDetails(punishment.id), 100);
      return;
    }

    // Try templates
    const template = state.templates.find(t => t.name.toLowerCase().includes(q));
    if (template) {
      go('templates');
      toast('info', 'Template Found', template.name);
      return;
    }

    // Nothing found
    toast('info', 'Not Found', 'No results found for: ' + query);
  }

  // ===== WEBSOCKET INTEGRATION =====
  let isLiveMode = false;

  function setupWebSocketHandlers() {
    const ws = window.MX.ws;
    if (!ws) return;

    // Handle authenticated event from auth.js
    window.addEventListener('mx:authenticated', (e) => {
      isLiveMode = true;
      state.authenticated = true;

      const session = e.detail;
      state.currentUser = {
        name: session.playerName || session.username,
        uuid: session.playerUuid,
        platform: 'Java',
        prefix: session.prefix || '',
        suffix: session.suffix || '',
        connectedAt: now(),
        rank: session.rank || null  // LuckPerms rank info: { name, weight, prefix, color }
      };
      state.staffName = session.playerName || session.username;
      state.notifications = state.notifications || [];
      ui.renderTopUser();
      updateNotificationCount();

      // Update server name in sidebar
      const serverNameText = document.getElementById('serverNameText');
      if (serverNameText && session.serverName) {
        serverNameText.textContent = session.serverName;
      }

      // Request initial data from server
      ws.requestPlayers();
      ws.requestPunishments();
      ws.requestWatchlist();
      ws.requestAutomodRules();
      ws.requestUserSettings();
      ws.requestChatStatus();
      ws.requestServerSettings();
      ws.requestAnticheatAlerts();
      // Also request integration info for Settings page
      ws.send('GET_ANTICHEAT_INFO');
      ws.send('GET_GEYSER_STATUS');

      ui.renderAll();
      hideDisconnect();
    });

    // Handle disconnect
    ws.on('disconnected', (data) => {
      if (isLiveMode && state.authenticated) {
        // Show disconnect overlay only if we were previously authenticated
        const serverName = document.getElementById('serverNameText')?.textContent || 'Server';
        showDisconnect(serverName);
      }
    });

    // Handle player data
    ws.on('PLAYERS_DATA', (data) => {
      if (!isLiveMode) return;
      state.players = (data.players || []).map(p => ({
        id: p.uuid || uid('p'),
        uuid: p.uuid,
        name: p.name,
        ip: p.ip || '',
        platform: p.geyser ? 'Bedrock' : 'Java',
        geyser: p.geyser || false,
        status: p.online ? 'online' : 'offline',
        lastSeen: p.lastJoin || now(),
        flags: 0,
        warnings: p.warnings || 0,
        recentCommands: p.recentCommands || [],
        notes: ''
      }));
      ui.renderPlayers();
      ui.renderDashboard();
    });

    // Handle player details (command history, automod flags)
    ws.on('PLAYER_DETAILS', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.uuid || p.id === data.uuid);
      if (player) {
        // Update recent commands if provided
        if (data.recentCommands && data.recentCommands.length > 0) {
          player.recentCommands = data.recentCommands;
        }
        // Store automod flags for this player
        if (data.automodFlags) {
          player.automodFlags = data.automodFlags;
        }
        // Re-render drawer if it's currently showing this player
        if (state.selectedPlayerId === player.id) {
          refreshDrawerCommands(player);
        }
      }
    });

    // Handle punishments data
    ws.on('PUNISHMENTS_DATA', (data) => {
      if (!isLiveMode) return;
      state.punishments = (data.punishments || []).map(p => ({
        id: p.caseId,
        playerId: p.playerUuid,
        type: p.type,
        reason: p.reason,
        duration: p.duration || '',
        staff: p.staffName,
        createdAt: p.createdAt,
        expiresAt: p.expiresAt,
        active: p.active,
        revoked: !!p.removedAt
      }));
      ui.renderPunishments();
      ui.renderDashboard();
    });

    // Handle watchlist data
    ws.on('WATCHLIST_DATA', (data) => {
      if (!isLiveMode) return;
      state.watchlist = new Set((data.watchlist || []).map(w => w.playerUuid));
      ui.renderWatchlist();
      ui.renderDashboard();
    });

    // Handle watchlist updates (real-time sync when watchlist changes)
    ws.on('WATCHLIST_UPDATE', (data) => {
      if (!isLiveMode) return;
      state.watchlist = new Set((data.watchlist || []).map(w => w.playerUuid));
      ui.renderWatchlist();
      ui.renderDashboard();
      // Also re-render players if on that page to show updated watchlist status
      if (state.currentPage === 'players') ui.renderPlayers();
    });

    // Handle automod rules
    ws.on('AUTOMOD_RULES_DATA', (data) => {
      if (!isLiveMode) return;
      // Only replace rules if server sent actual rules, otherwise keep defaults
      if (data.rules && data.rules.length > 0) {
        state.rules = data.rules;
      }
      ui.renderRules();
    });

    // Handle single rule update (real-time sync)
    ws.on('AUTOMOD_RULE_UPDATED', (data) => {
      if (!isLiveMode) return;
      const idx = state.rules.findIndex(r => r.id === data.id);
      if (idx !== -1) {
        state.rules[idx] = { ...state.rules[idx], ...data };
      } else {
        state.rules.push(data);
      }
      ui.renderRules();
    });

    // Handle new rule created (real-time sync)
    ws.on('AUTOMOD_RULE_CREATED', (data) => {
      if (!isLiveMode) return;
      // Only add if not already present
      if (!state.rules.find(r => r.id === data.id)) {
        state.rules.push(data);
        ui.renderRules();
        toast('info', 'Automod', 'New rule created: ' + data.name);
      }
    });

    // Handle rule deleted (real-time sync)
    ws.on('AUTOMOD_RULE_DELETED', (data) => {
      if (!isLiveMode) return;
      // Backend sends either ruleId or id
      const deletedId = data.ruleId || data.id;
      state.rules = state.rules.filter(r => r.id !== deletedId);
      ui.renderRules();
      toast('info', 'Automod', 'Rule deleted');
    });

    // Handle user settings
    ws.on('USER_SETTINGS_DATA', (data) => {
      if (!isLiveMode) return;
      state.settings.watchToasts = data.watchlistToasts ?? true;
      state.settings.soundEnabled = data.soundEnabled ?? true;
      state.settings.deviceTrustEnabled = data.deviceTrustEnabled ?? false;

      // Store staff notification settings for sync
      state.staffSettings = state.staffSettings || {};
      state.staffSettings.commandAlerts = data.commandAlerts ?? 'WATCHLIST_ONLY';
      state.staffSettings.showBlacklistedCommands = data.showBlacklistedCommands ?? true;
      state.staffSettings.anticheatAlertsLevel = data.anticheatAlertsLevel ?? 'EVERYONE';
      state.staffSettings.anticheatMinVL = data.anticheatMinVL ?? 10;
      state.staffSettings.automodAlertsLevel = data.automodAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.spamAlertsLevel = data.spamAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.filterAlertsLevel = data.filterAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.watchlistJoinAlerts = data.watchlistJoinAlerts ?? true;
      state.staffSettings.watchlistQuitAlerts = data.watchlistQuitAlerts ?? true;
      state.staffSettings.watchlistActivityAlerts = data.watchlistActivityAlerts ?? true;
      // joinLeaveMessages removed - now a global config setting
      state.staffSettings.staffChatEnabled = data.staffChatEnabled ?? true;
      state.staffSettings.staffChatSound = data.staffChatSound ?? true;
      state.staffSettings.banAlertsLevel = data.banAlertsLevel ?? 'EVERYONE';
      state.staffSettings.muteAlertsLevel = data.muteAlertsLevel ?? 'EVERYONE';
      state.staffSettings.kickAlertsLevel = data.kickAlertsLevel ?? 'EVERYONE';
      state.staffSettings.warnAlertsLevel = data.warnAlertsLevel ?? 'EVERYONE';

      // Punishment alert levels (new system)
      state.staffSettings.banAlerts = data.banAlerts ?? 'everyone';
      state.staffSettings.kickAlerts = data.kickAlerts ?? 'everyone';
      state.staffSettings.muteAlerts = data.muteAlerts ?? 'everyone';
      state.staffSettings.warnAlerts = data.warnAlerts ?? 'everyone';
      state.staffSettings.pardonAlerts = data.pardonAlerts ?? 'everyone';

      // Other alert types
      state.staffSettings.automodAlerts = data.automodAlerts ?? 'everyone';
      state.staffSettings.anticheatAlerts = data.anticheatAlerts ?? 'everyone';
      state.staffSettings.nicknameAlerts = data.nicknameAlerts ?? 'everyone';
      state.staffSettings.commandAlerts = data.commandAlerts ?? 'blacklisted_only';
      state.staffSettings.joinLeaveAlerts = data.joinLeaveAlerts ?? 'everyone';
      state.staffSettings.lagAlerts = data.lagAlerts ?? true;

      // Web panel notification modes
      state.staffSettings.webNotifyPunishments = data.webNotifyPunishments ?? 'toast';
      state.staffSettings.webNotifyAutomod = data.webNotifyAutomod ?? 'toast';
      state.staffSettings.webNotifyAnticheat = data.webNotifyAnticheat ?? 'toast';
      state.staffSettings.webNotifyWatchlist = data.webNotifyWatchlist ?? 'toast';
      state.staffSettings.webNotifyStaffChat = data.webNotifyStaffChat ?? 'toast';
      state.staffSettings.webNotifyCommands = data.webNotifyCommands ?? 'toast';
      state.staffSettings.webNotifyNickname = data.webNotifyNickname ?? 'toast';
      state.staffSettings.webNotifyLag = data.webNotifyLag ?? 'toast';

      // Web panel display settings
      state.staffSettings.webToastPosition = data.webToastPosition ?? 'top-right';
      state.staffSettings.webAlertDurationSeconds = data.webAlertDurationSeconds ?? 10;

      // Web panel sound settings per alert type
      state.staffSettings.webSoundPunishments = data.webSoundPunishments ?? true;
      state.staffSettings.webSoundAutomod = data.webSoundAutomod ?? true;
      state.staffSettings.webSoundAnticheat = data.webSoundAnticheat ?? true;
      state.staffSettings.webSoundWatchlist = data.webSoundWatchlist ?? true;
      state.staffSettings.webSoundStaffChat = data.webSoundStaffChat ?? true;
      state.staffSettings.webSoundCommands = data.webSoundCommands ?? true;
      state.staffSettings.webSoundNickname = data.webSoundNickname ?? true;
      state.staffSettings.webSoundLag = data.webSoundLag ?? true;

      // Update toast position
      if (window.updateAlertToastPosition) {
        window.updateAlertToastPosition(data.webToastPosition);
      }

      // Apply sound settings globally
      if (window.MX.sounds) {
        window.MX.sounds.setEnabled(data.soundEnabled ?? true);
      }

      // Handle changelog read status
      if (data.readChangelogs) {
        window.setReadChangelogs(data.readChangelogs);
        // Check for unread changelogs after a brief delay
        setTimeout(() => window.checkUnreadChangelogs(), 500);
      }

      // Store permissions array for UI permission checks
      if (data.permissions) {
        state.permissions = data.permissions;
        console.log('[USER_SETTINGS] Received permissions:', state.permissions);
        window.devtoolsLog('PERMISSIONS', `Received ${state.permissions.length} permissions from server`, 'success');
      } else {
        console.warn('[USER_SETTINGS] No permissions array received from server!');
        window.devtoolsLog('PERMISSIONS', 'WARNING: No permissions array in USER_SETTINGS_DATA', 'warn');
      }

      // Update all UI elements that depend on these settings
      updateSettingsUI();
      ui.renderWatchToastsToggle();
      ui.renderStaffSettings();
    });

    // Handle anticheat alerts data (list of all checks)
    ws.on('ANTICHEAT_ALERTS', (data) => {
      if (!isLiveMode) return;
      state.anticheat.anticheats = data.anticheats || [];
      // Also update integrations state so Settings page shows detected anticheats
      state.integrations = state.integrations || {};
      state.integrations.hookedAnticheats = (data.anticheats || []).map(ac => ({
        name: ac.name,
        alertsEnabled: true
      }));
      ui.renderAnticheat();
      ui.renderIntegrations();
    });

    // Handle staff alert preferences
    ws.on('STAFF_ALERT_PREFS', (data) => {
      if (!isLiveMode) return;
      // Convert prefs to flat map: "anticheat.checkName" -> pref
      const prefs = {};
      if (data.preferences) {
        Object.entries(data.preferences).forEach(([acName, acPrefs]) => {
          Object.entries(acPrefs).forEach(([checkName, pref]) => {
            prefs[`${acName}.${checkName}`] = pref;
          });
        });
      }
      state.anticheat.alertPrefs = prefs;
      ui.renderAnticheat();
    });

    // Handle alert presets
    ws.on('ALERT_PRESETS', (data) => {
      if (!isLiveMode) return;
      state.anticheat.presets = data.presets || [];
      ui.renderAnticheat();
    });

    // Handle alert pref update confirmation
    ws.on('STAFF_ALERT_PREF_UPDATED', (data) => {
      if (!isLiveMode) return;
      const prefKey = `${data.anticheat?.toLowerCase()}.${data.checkName}`;
      state.anticheat.alertPrefs[prefKey] = {
        alertLevel: data.alertLevel,
        thresholdCount: data.thresholdCount,
        timeWindowSeconds: data.timeWindowSeconds
      };
      window.debugLog('ANTICHEAT', `Saved: ${data.anticheat}:${data.checkName} (${data.alertLevel}, ${data.thresholdCount}/${data.timeWindowSeconds}s)`, 'success');
    });

    // Real-time broadcasts
    ws.on('PUNISHMENT_CREATED', (data) => {
      if (!isLiveMode) return;
      const pun = {
        id: data.caseId,
        playerId: data.playerUuid,
        type: data.type,
        reason: data.reason,
        duration: data.duration || '',
        staff: data.staffName,
        createdAt: data.createdAt,
        expiresAt: data.expiresAt,
        active: data.active,
        revoked: false
      };
      state.punishments.unshift(pun);
      state.activity.unshift({ t: now(), actor: data.staffName, action: `${data.type} issued`, target: data.playerName });
      ui.renderPunishments();
      ui.renderDashboard();
      showPanelAlert('punishments', 'Punishment', `${data.playerName} was ${data.type.toLowerCase()}ed by ${data.staffName}`, { playerId: data.playerUuid, playerName: data.playerName });
      window.MX.sounds?.punishment();
    });

    ws.on('PUNISHMENT_REVOKED', (data) => {
      if (!isLiveMode) return;
      const pun = state.punishments.find(p => p.id === data.caseId);
      if (pun) {
        pun.active = false;
        pun.revoked = true;
      }
      ui.renderPunishments();
      showPanelAlert('punishments', 'Revoked', `Punishment ${data.caseId} was revoked`);
    });

    // Rate limiting state for watchlist alerts (by player/IP)
    const watchlistRateLimit = {
      alerts: new Map(), // playerUuid/IP -> { count, lastTime, timer }
      windowMs: 3000,    // 3 second window for aggregation
      updateIntervalMs: 2000 // Update UI every 2 seconds during high activity
    };

    ws.on('WATCHLIST_ALERT', (data) => {
      if (!isLiveMode) return;
      // Backend sends alertType, playerName, details, playerUuid, playerIp
      const alertType = data.alertType || data.type || 'Activity';
      const playerName = data.playerName || 'Unknown';
      const details = data.details || '';
      const playerKey = data.playerIp || data.playerUuid || playerName; // Use IP if available for rate limiting

      state.watchAlerts.push({
        type: alertType,
        details: details,
        playerName: playerName,
        playerUuid: data.playerUuid,
        playerIp: data.playerIp,
        t: now()
      });

      // Rate limiting logic - aggregate rapid alerts from same player/IP
      const currentTime = Date.now();
      let rateLimitEntry = watchlistRateLimit.alerts.get(playerKey);

      if (rateLimitEntry && (currentTime - rateLimitEntry.lastTime) < watchlistRateLimit.windowMs) {
        // Within rate limit window - increment count
        rateLimitEntry.count++;
        rateLimitEntry.lastTime = currentTime;
        rateLimitEntry.latestDetails = details;
        rateLimitEntry.latestType = alertType;

        // Clear existing timer and set new one
        if (rateLimitEntry.timer) clearTimeout(rateLimitEntry.timer);
        rateLimitEntry.timer = setTimeout(() => {
          // Show aggregated alert after window expires
          const entry = watchlistRateLimit.alerts.get(playerKey);
          if (entry && entry.count > 1) {
            const aggregatedDetails = `${entry.count} alerts in ${Math.round(watchlistRateLimit.windowMs / 1000)}s (latest: ${entry.latestDetails})`;
            showWatchlistAlertUI(entry.latestType, aggregatedDetails, playerName, data.playerUuid);
          }
          watchlistRateLimit.alerts.delete(playerKey);
        }, watchlistRateLimit.updateIntervalMs);

        // Don't show individual alert - will be aggregated
        ui.renderDashboard();
        return;
      }

      // First alert or outside window - show immediately
      watchlistRateLimit.alerts.set(playerKey, {
        count: 1,
        lastTime: currentTime,
        latestDetails: details,
        latestType: alertType,
        timer: setTimeout(() => {
          watchlistRateLimit.alerts.delete(playerKey);
        }, watchlistRateLimit.windowMs)
      });

      showWatchlistAlertUI(alertType, details, playerName, data.playerUuid);
      ui.renderDashboard();
    });

    // Helper to show watchlist alert UI (bar/toast based on settings)
    function showWatchlistAlertUI(alertType, details, playerName, playerUuid) {
      const settings = loadMySettings();
      const style = settings.watchlistStyle || 'bar';
      const playerData = { playerId: playerUuid, playerName: playerName };

      if (style === 'bar' || style === 'both') {
        showAlertBar('watchlist', alertType, details, playerData);
      }
      if (style === 'toast' || style === 'both') {
        showPanelAlert('watchlist', 'Watchlist', `${playerName}: ${details}`, { playerId: playerUuid, playerName, severity: 'warn' });
      }
      window.MX.sounds?.watchlist();
    }

    ws.on('STAFFCHAT_MESSAGE', (data) => {
      if (!isLiveMode) return;
      logEvent('INFO', 'staffchat', `Staff | ${data.sender}`, data.message, { kind: 'staffchat' });

      // Add to staff chat panel (avoid duplicates for self messages)
      const isSelf = data.sender === state.currentUser?.name;
      if (!isSelf) {
        addStaffChatMessage({
          sender: data.sender,
          message: data.message,
          isWeb: data.sender.includes('[Web]'),
          isSelf: false,
          time: now()
        });
        showPanelAlert('staffChat', 'Staff Chat', `${data.sender}: ${data.message}`);
        window.MX.sounds?.staffChat();
      }
    });

    ws.on('PLAYER_JOIN', (data) => {
      if (!isLiveMode) return;
      const existing = state.players.find(p => p.uuid === data.uuid);
      if (existing) {
        existing.status = 'online';
        existing.lastSeen = now();
      } else {
        state.players.unshift({
          id: data.uuid,
          uuid: data.uuid,
          name: data.name,
          ip: data.ip || '',
          platform: data.geyser ? 'Bedrock' : 'Java',
          geyser: data.geyser || false,
          status: 'online',
          lastSeen: now(),
          flags: 0,
          warnings: 0,
          recentCommands: [],
          notes: ''
        });
      }
      logEvent('INFO', 'join', 'Player Join', `${data.name} joined the server`, { kind: 'join', type: 'JOIN' });
      ui.renderPlayers();
      ui.renderDashboard();
    });

    ws.on('PLAYER_QUIT', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.uuid);
      if (player) {
        player.status = 'offline';
        player.lastSeen = now();
      }
      logEvent('INFO', 'leave', 'Player Quit', `${data.name} left the server`, { kind: 'leave', type: 'LEAVE' });
      ui.renderPlayers();
      ui.renderDashboard();
    });

    ws.on('CHAT_MESSAGE', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid || p.uuid === data.uuid);
      logEvent('INFO', 'chat', `Chat | ${data.playerName}`, data.message, { playerId: player?.id, kind: 'chat', type: 'CHAT' });
    });

    ws.on('COMMAND_EXECUTED', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      if (player) {
        // Add to player's recent commands for drawer display
        if (!player.recentCommands) player.recentCommands = [];
        player.recentCommands.push({ cmd: data.command, t: data.timestamp || Date.now() });
        // Keep only last 100 commands in memory
        if (player.recentCommands.length > 100) player.recentCommands.shift();
      }
      logEvent('INFO', 'command', `Command | ${data.playerName}`, data.command, { playerId: player?.id, kind: 'command', type: 'COMMAND' });
    });

    ws.on('LOG_EVENT', (data) => {
      if (!isLiveMode) return;
      const player = data.playerUuid ? state.players.find(p => p.uuid === data.playerUuid) : null;
      logEvent(
        data.severity || 'INFO',
        data.logType || data.category || 'system',
        data.title,
        data.detail,
        {
          kind: data.logType || data.category || 'system',
          type: data.logType || 'SYSTEM',
          playerId: player?.id || data.playerUuid,
          caseId: data.caseId
        }
      );

      // Also show in debug log panel with proper formatting
      if (window.debugLog) {
        const category = data.category || data.logType || 'SYSTEM';
        const severity = data.severity || 'INFO';
        const debugType = severity === 'ERROR' ? 'error' : severity === 'WARN' ? 'warn' : severity === 'SUCCESS' ? 'success' : 'info';
        const message = data.detail || data.title || 'Unknown event';
        window.debugLog(category.toUpperCase(), message, debugType);
      }
    });

    ws.on('AUTOMOD_TRIGGER', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      showPanelAlert('automod', 'Automod', `${data.playerName} triggered ${data.rule}`, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
    });

    ws.on('AUTOMOD_TRIGGERED', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      showPanelAlert('automod', 'Automod', `${data.playerName} triggered ${data.rule}`, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
    });

    ws.on('PRIVATE_MESSAGE', (data) => {
      if (!isLiveMode) return;
      const settings = loadMySettings();
      const player = state.players.find(p => p.uuid === data.senderUuid);
      const isWatchlisted = player && (state.watchlist?.has(player.id) || state.watchlist?.has(player.uuid));

      // Check PM alert level setting
      const pmLevel = settings.privateMessageAlerts || 'OFF';
      if (pmLevel === 'OFF') return;
      if (pmLevel === 'WATCHLIST_ONLY' && !isWatchlisted) return;

      // Log to activity feed
      const prefix = isWatchlisted ? '[WL] ' : '';
      logEvent('INFO', 'chat', `PM | ${prefix}${data.senderName} → ${data.targetName}`, data.message, { playerId: player?.id, kind: 'pm', type: 'CHAT' });

      // Show toast for watchlisted players
      if (isWatchlisted) {
        toast('warn', 'Private Message', `${data.senderName} → ${data.targetName}: ${data.message.substring(0, 50)}...`);
      }
    });

    ws.on('ANTICHEAT_ALERT', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.name === data.playerName || p.uuid === data.playerUuid);
      const settings = loadMySettings();

      // Always log the event with explicit type for filtering
      logEvent('WARN', 'anticheat', `Anticheat | ${data.check || 'Unknown'}`, `${data.playerName} (VL: ${data.vl || 0})`, { playerId: player?.id, kind: 'anticheat', type: 'ANTICHEAT' });

      // Get per-check alert preference from staff settings
      const anticheat = (data.anticheat || 'grim').toLowerCase();
      const checkName = data.check || 'Unknown';
      const prefKey = `${anticheat}.${checkName}`;
      const checkPref = state.anticheat?.alertPrefs?.[prefKey] || { alertLevel: 'EVERYONE', thresholdCount: 1, timeWindowSeconds: 60 };

      // Determine if we should show this alert based on per-check preference
      const alertLevel = checkPref.alertLevel || 'EVERYONE';
      if (alertLevel === 'OFF') return;

      // Check if player is on watchlist
      const isWatchlisted = player && (state.watchlist?.has(player.id) || state.watchlist?.has(player.uuid));

      // If watchlist only, skip non-watchlisted players
      if (alertLevel === 'WATCHLIST_ONLY' && !isWatchlisted) return;

      // Show alert using panel notification settings
      const title = `Anticheat: ${checkName}`;
      const sub = `${data.playerName} - VL: ${data.vl || 0}`;
      showPanelAlert('anticheat', title, sub, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
      window.MX.sounds?.anticheat();
    });

    // Custom alerts from /mx sendalert command
    ws.on('CUSTOM_ALERT', (data) => {
      console.log('[CUSTOM_ALERT] Received:', data);
      console.log('[CUSTOM_ALERT] isLiveMode:', isLiveMode);

      if (!isLiveMode) {
        console.log('[CUSTOM_ALERT] Not in live mode, ignoring');
        return;
      }

      // Try to find the player in state.players - use UUID as playerId for avatar
      const player = data.playerUuid ? state.players.find(p => p.uuid === data.playerUuid || p.id === data.playerUuid) : null;
      const playerId = data.playerUuid || player?.uuid || player?.id;

      console.log('[CUSTOM_ALERT] Player lookup:', { playerUuid: data.playerUuid, foundPlayer: player, playerId });

      // Log the event
      const category = data.category || 'custom';
      const eventType = category.toUpperCase();
      logEvent('WARN', category, data.title || 'Alert', `${data.playerName}: ${data.message}`, { playerId: playerId, kind: category, type: eventType });

      // Show alert using panel notification settings
      showPanelAlert(category, data.title || 'Alert', `${data.playerName}: ${data.message}`, {
        playerId: playerId,
        playerName: data.playerName,
        severity: 'warn'
      });

      // Sound is played in alertToast based on settings
    });

    ws.on('SERVER_STATUS', (data) => {
      if (!isLiveMode) return;
      const dot = dom().statusDot;
      const text = dom().statusText;
      if (dot) dot.className = 'dot ' + (data.online ? 'ok' : 'error');
      if (text) text.textContent = data.online ? 'Online' : 'Offline';
      // Server name is set on auth, don't override with version
      if (data.serverName) {
        const serverNameText = document.getElementById('serverNameText');
        if (serverNameText) serverNameText.textContent = data.serverName;
      }
    });

    ws.on('CHAT_STATUS', (data) => {
      if (!isLiveMode) return;
      state.settings.chatDisabled = !data.chatEnabled;
      state.settings.slowSeconds = data.slowmodeSeconds || 0;
      state.settings.slowEnabled = data.slowmodeSeconds > 0;
      ui.renderDashboard();
    });

    ws.on('SERVER_SETTINGS', (data) => {
      if (!isLiveMode) return;
      // Chat settings
      if (typeof data.chatEnabled !== 'undefined') state.settings.chatDisabled = !data.chatEnabled;
      if (typeof data.slowmodeSeconds !== 'undefined') {
        state.settings.slowSeconds = data.slowmodeSeconds || 0;
        state.settings.slowEnabled = data.slowmodeSeconds > 0;
      }
      // Mute settings
      if (data.muteSettings) {
        state.settings.muteChat = data.muteSettings.chat ?? true;
        state.settings.muteMsg = data.muteSettings.msg ?? true;
        state.settings.muteSigns = data.muteSettings.signs ?? true;
        state.settings.muteBooks = data.muteSettings.books ?? true;
        state.settings.muteBroadcast = data.muteSettings.broadcast ?? false;
        state.settings.muteVoice = data.muteSettings.voice ?? true;
        state.settings.muteVoiceJoin = data.muteSettings.voiceJoin ?? true;
      }
      // Warn settings
      if (data.warnSettings) {
        state.settings.warnNotify = data.warnSettings.notify ?? true;
        state.settings.warnAutoEscalate = data.warnSettings.autoEscalate ?? false;
      }
      // Anticheat settings
      if (data.anticheatSettings) {
        state.settings.anticheatReplace = data.anticheatSettings.rebrandAlerts ?? false;
      }
      ui.renderDashboard();
      ui.renderChatToggles();
    });

    // Integration status (Geyser, Floodgate, Citizens)
    ws.on('GEYSER_STATUS', (data) => {
      if (!isLiveMode) return;
      state.integrations = state.integrations || {};
      state.integrations.geyserDetected = data.geyserAvailable;
      state.integrations.floodgateDetected = data.floodgateAvailable;
      state.integrations.citizensDetected = data.citizensAvailable;
      state.integrations.geyserVersion = data.geyserVersion;
      state.integrations.floodgateVersion = data.floodgateVersion;
      state.integrations.citizensVersion = data.citizensVersion;
      ui.renderIntegrations();
    });

    // Developer Checklist
    ws.on('DEV_CHECKLIST', (data) => {
      state.devChecklist = data || [];
      renderDevChecklist();
    });

    ws.on('PLUGIN_UPDATE_RESULT', (data) => {
      const banner = document.getElementById('updateBanner');
      if (data.success) {
        toast('success', 'Update Downloaded', data.message || 'Restart the server to apply the update.');
        if (banner) {
          const btn = banner.querySelector('.btn.primary');
          if (btn) {
            btn.innerHTML = '<i class="fa-solid fa-check"></i> Restart Required';
            btn.disabled = true;
            btn.classList.remove('primary');
            btn.classList.add('success');
          }
          const titleEl = banner.querySelector('.update-title');
          if (titleEl) titleEl.textContent = 'Update Downloaded!';
          const notesEl = banner.querySelector('.update-notes');
          if (notesEl) notesEl.textContent = 'Restart the server to apply';
        }
      } else {
        toast('error', 'Update Failed', data.message || 'Failed to download update.');
        if (banner) {
          const btn = banner.querySelector('.btn.primary');
          if (btn) {
            btn.disabled = false;
            btn.innerHTML = '<i class="fa-solid fa-rotate"></i> Retry';
          }
        }
      }
    });
  }

  // Override functions to use WebSocket in live mode
  function wrapWithWebSocket() {
    const ws = window.MX.ws;
    if (!ws) return;

    // Override executePunishment to send to server
    const originalExecutePunishment = executePunishment;
    executePunishment = function(opts) {
      if (isLiveMode && ws.isConnected()) {
        const p = state.players.find(x => x.id === opts.playerId);
        ws.createPunishment({
          playerUuid: p?.uuid || opts.playerId,
          playerName: p?.name,
          type: opts.type,
          reason: opts.reason,
          duration: opts.duration
        });
        toast('info', 'Sending', `Creating ${opts.type.toLowerCase()}...`);
      } else {
        originalExecutePunishment(opts);
      }
    };
  }

  // ===== DISCONNECT OVERLAY =====
  let serverNameForDisconnect = 'Server';
  let disconnectTimeout = null;
  let reconnectCooldown = false;
  let lastPongTime = Date.now();

  function showDisconnect(serverName) {
    serverNameForDisconnect = serverName || 'Server';

    // Clear any existing timeout
    if (disconnectTimeout) {
      clearTimeout(disconnectTimeout);
      disconnectTimeout = null;
    }

    // Delay showing disconnect overlay by 5 seconds
    // This gives time for brief network hiccups to resolve
    disconnectTimeout = setTimeout(() => {
      const overlay = document.getElementById('disconnectOverlay');
      const nameEl = document.getElementById('disconnectServerName');
      if (nameEl) nameEl.textContent = serverNameForDisconnect;
      if (overlay) overlay.classList.add('show');
      window.MX.sounds?.disconnect();
      disconnectTimeout = null;
    }, 5000);
  }

  function hideDisconnect() {
    // Clear the delayed show timeout if connection restored
    if (disconnectTimeout) {
      clearTimeout(disconnectTimeout);
      disconnectTimeout = null;
    }
    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function attemptReconnect() {
    // Check cooldown - prevent rapid reconnect attempts
    if (reconnectCooldown) {
      window.MX.toast?.('warn', 'Please Wait', 'Reconnect cooldown active. Try again in a few seconds.');
      return;
    }

    // Start 5 second cooldown
    reconnectCooldown = true;
    const reconnectBtn = document.querySelector('#disconnectOverlay .btn-primary');
    if (reconnectBtn) {
      reconnectBtn.disabled = true;
      reconnectBtn.textContent = 'Reconnecting...';

      // Update button with countdown
      let countdown = 5;
      const countdownInterval = setInterval(() => {
        countdown--;
        if (countdown > 0) {
          reconnectBtn.textContent = `Wait ${countdown}s...`;
        } else {
          clearInterval(countdownInterval);
          reconnectBtn.disabled = false;
          reconnectBtn.textContent = 'Reconnect';
          reconnectCooldown = false;
        }
      }, 1000);
    } else {
      // Reset cooldown after 5 seconds if button not found
      setTimeout(() => { reconnectCooldown = false; }, 5000);
    }

    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
    window.MX.sounds?.reconnecting();
    if (window.MX.auth?.reconnect) {
      window.MX.auth.reconnect();
    } else {
      location.reload();
    }
  }

  // Track last pong time for disconnect detection
  function updateLastPong() {
    lastPongTime = Date.now();
  }

  // ===== LOADING LINE BAR (Progress-based) =====
  let loadingLineCount = 0;
  let loadingLineTimeout = null;
  let loadingLineFadeTimeout = null;
  let loadingAnimationFrame = null;
  let loadingStartTime = 0;
  let currentProgress = 0;

  function getLoadingElements() {
    return {
      line: document.getElementById('loadingLine'),
      fill: document.getElementById('loadingLineFill')
    };
  }

  function updateLoadingProgress(progress) {
    const { fill } = getLoadingElements();
    if (fill) {
      currentProgress = Math.min(100, Math.max(0, progress));
      fill.style.width = currentProgress + '%';
    }
  }

  // Animate progress smoothly toward target over time
  function animateProgress() {
    if (loadingLineCount === 0) return;

    const elapsed = Date.now() - loadingStartTime;
    // Asymptotic progress: quickly to 60%, then slow down to max 90%
    // Formula: 90 * (1 - e^(-elapsed/3000))
    const targetProgress = 90 * (1 - Math.exp(-elapsed / 3000));

    if (currentProgress < targetProgress) {
      updateLoadingProgress(targetProgress);
    }

    if (loadingLineCount > 0 && currentProgress < 90) {
      loadingAnimationFrame = requestAnimationFrame(animateProgress);
    }
  }

  function showLoadingLine() {
    const wasActive = loadingLineCount > 0;
    loadingLineCount++;

    const { line } = getLoadingElements();

    // Clear any pending fade-out
    if (loadingLineFadeTimeout) {
      clearTimeout(loadingLineFadeTimeout);
      loadingLineFadeTimeout = null;
    }

    if (line) {
      line.classList.remove('fade-out', 'complete');
      line.classList.add('active');
    }

    // Start animation if this is the first request
    if (!wasActive) {
      loadingStartTime = Date.now();
      currentProgress = 0;
      updateLoadingProgress(5); // Start with small initial progress
      animateProgress();
    }

    // Auto-hide after 30 seconds as a safety measure
    if (loadingLineTimeout) clearTimeout(loadingLineTimeout);
    loadingLineTimeout = setTimeout(() => {
      forceHideLoadingLine();
    }, 30000);
  }

  function hideLoadingLine() {
    loadingLineCount = Math.max(0, loadingLineCount - 1);

    if (loadingLineCount === 0) {
      // Cancel animation
      if (loadingAnimationFrame) {
        cancelAnimationFrame(loadingAnimationFrame);
        loadingAnimationFrame = null;
      }

      // All requests complete - animate to 100%
      updateLoadingProgress(100);
      const { line } = getLoadingElements();
      if (line) {
        line.classList.add('complete');
      }

      // Wait 0.75 seconds then fade out
      loadingLineFadeTimeout = setTimeout(() => {
        const { line, fill } = getLoadingElements();
        if (line) {
          line.classList.add('fade-out');
          // After fade animation, reset everything
          setTimeout(() => {
            line.classList.remove('active', 'fade-out', 'complete');
            if (fill) fill.style.width = '0%';
            currentProgress = 0;
          }, 300);
        }
      }, 750);

      if (loadingLineTimeout) {
        clearTimeout(loadingLineTimeout);
        loadingLineTimeout = null;
      }
    }
  }

  // Set progress directly (0-100)
  function setLoadingProgress(progress) {
    const { line } = getLoadingElements();
    if (line && !line.classList.contains('active')) {
      line.classList.remove('fade-out', 'complete');
      line.classList.add('active');
    }
    // Cancel any animation when setting progress directly
    if (loadingAnimationFrame) {
      cancelAnimationFrame(loadingAnimationFrame);
      loadingAnimationFrame = null;
    }
    updateLoadingProgress(progress);
  }

  // Force hide regardless of count (for page transitions)
  function forceHideLoadingLine() {
    loadingLineCount = 0;
    currentProgress = 0;

    if (loadingAnimationFrame) {
      cancelAnimationFrame(loadingAnimationFrame);
      loadingAnimationFrame = null;
    }

    if (loadingLineFadeTimeout) {
      clearTimeout(loadingLineFadeTimeout);
      loadingLineFadeTimeout = null;
    }

    const { line, fill } = getLoadingElements();
    if (line) {
      line.classList.remove('active', 'fade-out', 'complete');
    }
    if (fill) {
      fill.style.width = '0%';
    }
    if (loadingLineTimeout) {
      clearTimeout(loadingLineTimeout);
      loadingLineTimeout = null;
    }
  }

  // Expose loading line functions globally
  window.showLoadingLine = showLoadingLine;
  window.hideLoadingLine = hideLoadingLine;
  window.setLoadingProgress = setLoadingProgress;
  window.forceHideLoadingLine = forceHideLoadingLine;
  window.MX.loadingLine = {
    show: showLoadingLine,
    hide: hideLoadingLine,
    setProgress: setLoadingProgress,
    forceHide: forceHideLoadingLine
  };

  // ===== SIDEBAR TOGGLE (Mobile) =====
  function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    sidebar?.classList.toggle('show');
    overlay?.classList.toggle('show');
  }

  // ===== ALERT BAR =====
  let alertBarData = null;
  let alertBarTimeout = null;

  function showAlertBar(type, title, sub, playerData) {
    alertBarData = { type, title, sub, playerData };
    const bar = document.getElementById('alertBar');
    const content = document.getElementById('alertBarContent');
    const icon = document.getElementById('alertBarIcon');
    const avatar = document.getElementById('alertBarAvatar');
    const titleEl = document.getElementById('alertBarTitle');
    const playerEl = document.getElementById('alertBarPlayer');
    const subEl = document.getElementById('alertBarSub');

    if (content) content.className = 'alertBar-content' + (type === 'anticheat' ? ' anticheat' : '');

    // Show player avatar if we have player data
    const player = playerData?.playerId ? state.players.find(p => p.uuid === playerData.playerId || p.id === playerData.playerId) : null;
    const playerName = playerData?.playerName || player?.name;

    if (avatar && playerData?.playerId) {
      avatar.src = `https://mc-heads.net/avatar/${playerData.playerId}/32`;
      avatar.style.display = 'block';
      if (icon) icon.style.display = 'none';
    } else {
      if (avatar) avatar.style.display = 'none';
      if (icon) {
        icon.style.display = 'flex';
        icon.innerHTML = type === 'anticheat'
          ? '<i class="fa-solid fa-shield-halved"></i>'
          : '<i class="fa-solid fa-eye"></i>';
      }
    }

    if (titleEl) titleEl.textContent = title;
    if (playerEl && playerName) {
      playerEl.textContent = playerName;
      playerEl.style.display = 'block';
    } else if (playerEl) {
      playerEl.style.display = 'none';
    }
    if (subEl) subEl.textContent = sub;
    if (bar) bar.classList.add('show');

    window.MX.sounds?.alertBar();

    if (alertBarTimeout) clearTimeout(alertBarTimeout);
    alertBarTimeout = setTimeout(dismissAlertBar, 8000);
  }

  function dismissAlertBar() {
    const bar = document.getElementById('alertBar');
    if (bar) bar.classList.remove('show');
    alertBarData = null;
    if (alertBarTimeout) clearTimeout(alertBarTimeout);
  }

  function viewAlertPlayer() {
    if (alertBarData?.playerData?.playerId) {
      openPlayerDrawer(alertBarData.playerData.playerId);
    }
    dismissAlertBar();
  }

  // ===== MY SETTINGS (Player Preferences) =====
  const myAlertDefaults = {
    automod: true,
    commands: false,
    punishments: true,
    pardons: true,
    joins: false,
    anticheatMode: 'watchlist',
    watchlistStyle: 'bar'
  };

  /**
   * Show a panel alert based on staff notification settings.
   * Uses the new alertToast system for better visuals and animations.
   * @param {string} category - 'punishments', 'automod', 'anticheat', 'watchlist', 'staffchat', 'ban', 'kick', etc.
   * @param {string} title - Alert title
   * @param {string} message - Alert message/subtitle
   * @param {object} options - { playerId, playerName, severity: 'info'|'warn'|'error' }
   */
  function showPanelAlert(category, title, message, options = {}) {
    const staffSettings = state.staffSettings || {};
    const settingKey = 'webNotify' + category.charAt(0).toUpperCase() + category.slice(1);
    const mode = staffSettings[settingKey] || 'toast'; // Default to toast if not set

    console.log('[showPanelAlert] category:', category, 'settingKey:', settingKey, 'mode:', mode, 'staffSettings:', staffSettings);

    if (mode === 'off') {
      console.log('[showPanelAlert] Mode is off, not showing alert');
      return;
    }

    // Only include playerData if we have a valid playerId (not undefined/null)
    const playerData = options.playerId ? { playerId: options.playerId, playerName: options.playerName } : { playerName: options.playerName };

    console.log('[showPanelAlert] Showing alert toast:', { category, title, message, playerData });

    if (mode === 'toast' || mode === 'both') {
      // Use the new alertToast for alert notifications
      window.alertToast(category, title, message, playerData);
    }

    if (mode === 'browser' || mode === 'both') {
      // Request browser notification permission if not granted
      if (Notification.permission === 'granted') {
        new Notification(title, { body: message, icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      } else if (Notification.permission !== 'denied') {
        Notification.requestPermission().then(permission => {
          if (permission === 'granted') {
            new Notification(title, { body: message, icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
          }
        });
      }
      // Also show alert toast as fallback if browser-only mode
      if (mode === 'browser') {
        window.alertToast(category, title, message, { ...playerData, silent: true });
      }
    }
  }

  window.showPanelAlert = showPanelAlert;

  function loadMySettings() {
    try {
      const saved = localStorage.getItem('mx_my_settings');
      return saved ? { ...myAlertDefaults, ...JSON.parse(saved) } : { ...myAlertDefaults };
    } catch (e) {
      return { ...myAlertDefaults };
    }
  }

  function saveMySettings(settings) {
    try {
      localStorage.setItem('mx_my_settings', JSON.stringify(settings));
    } catch (e) {}
  }

  function applyMySettingsUI() {
    const settings = loadMySettings();

    ['automod', 'commands', 'punishments', 'pardons', 'joins'].forEach(key => {
      const btn = document.getElementById('alert' + key.charAt(0).toUpperCase() + key.slice(1));
      if (btn) btn.classList.toggle('on', settings[key]);
    });

    ['Off', 'Watchlist', 'All'].forEach(mode => {
      const btn = document.getElementById('acMode' + mode);
      if (btn) btn.classList.toggle('active', settings.anticheatMode === mode.toLowerCase());
    });

    ['Bar', 'Toast', 'Both'].forEach(style => {
      const btn = document.getElementById('watchStyle' + style);
      if (btn) btn.classList.toggle('active', settings.watchlistStyle === style.toLowerCase());
    });

    const soundsBtn = document.getElementById('soundsEnabled');
    if (soundsBtn) soundsBtn.classList.toggle('on', window.MX.sounds?.isEnabled() ?? true);

    const volumeSlider = document.getElementById('volumeSlider');
    const volumeHint = document.getElementById('volumeHint');
    if (volumeSlider) {
      const vol = Math.round((window.MX.sounds?.getVolume() ?? 0.5) * 100);
      volumeSlider.value = vol;
      if (volumeHint) volumeHint.textContent = vol + '%';
    }

    // Apply device trust setting from state
    const deviceBtn = document.getElementById('deviceTrustEnabled');
    if (deviceBtn) {
      deviceBtn.classList.toggle('on', state.settings.deviceTrustEnabled ?? false);
    }
  }

  function toggleMyAlert(key) {
    const settings = loadMySettings();
    settings[key] = !settings[key];
    saveMySettings(settings);
    const btn = document.getElementById('alert' + key.charAt(0).toUpperCase() + key.slice(1));
    if (btn) btn.classList.toggle('on', settings[key]);
    window.MX.sounds?.toggle();
  }

  function setAnticheatMode(mode) {
    const settings = loadMySettings();
    settings.anticheatMode = mode;
    saveMySettings(settings);
    ['Off', 'Watchlist', 'All'].forEach(m => {
      const btn = document.getElementById('acMode' + m);
      if (btn) btn.classList.toggle('active', mode === m.toLowerCase());
    });
    window.MX.sounds?.click();
  }

  function setWatchlistStyle(style) {
    const settings = loadMySettings();
    settings.watchlistStyle = style;
    saveMySettings(settings);
    ['Bar', 'Toast', 'Both'].forEach(s => {
      const btn = document.getElementById('watchStyle' + s);
      if (btn) btn.classList.toggle('active', style === s.toLowerCase());
    });
    window.MX.sounds?.click();
  }

  function togglePanelSounds() {
    if (window.MX.sounds) {
      const enabled = window.MX.sounds.toggle();
      const btn = document.getElementById('soundsEnabled');
      if (btn) btn.classList.toggle('on', enabled);

      // Sync to server
      if (ws.connected) {
        ws.send('UPDATE_USER_SETTINGS', {
          soundEnabled: enabled
        });
      }

      // Update state
      state.settings.soundEnabled = enabled;
    }
  }

  function toggleDeviceTrust() {
    const currentValue = state.settings.deviceTrustEnabled ?? false;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('deviceTrustEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Sync to server
    if (ws.connected) {
      ws.send('UPDATE_USER_SETTINGS', {
        deviceTrustEnabled: newValue
      });
    }

    // Update state
    state.settings.deviceTrustEnabled = newValue;

    // Play click sound
    window.MX.sounds?.click();
  }

  function toggleDebugMode() {
    // Initialize userSettings if needed
    if (!state.userSettings) state.userSettings = {};

    const currentValue = state.userSettings.debugMode ?? false;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('debugModeEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Show/hide info box
    const infoBox = document.getElementById('debugModeInfo');
    if (infoBox) infoBox.style.display = newValue ? 'flex' : 'none';

    // Show/hide version badge
    const versionBadge = document.getElementById('versionBadge');
    if (versionBadge) versionBadge.style.display = newValue ? 'flex' : 'none';

    // Update state
    state.userSettings.debugMode = newValue;

    // Save to localStorage
    saveState();

    // Play click sound
    window.MX.sounds?.click();

    // Show confirmation debug message
    if (newValue) {
      debugLog('SYSTEM', 'Debug mode enabled - you will now see sync and error notifications', 'success');
    }
  }

  function toggleWatchlistAlerts() {
    // Initialize userSettings if needed
    if (!state.userSettings) state.userSettings = {};

    const currentValue = state.userSettings.watchlistAlerts ?? true;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('watchlistAlertsEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Update state
    state.userSettings.watchlistAlerts = newValue;

    // Save to localStorage
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { watchlistAlerts: newValue });
    }

    // Play click sound
    window.MX.sounds?.click();

    // Show system message confirming the change
    systemLog(`Watchlist alerts ${newValue ? 'enabled' : 'disabled'}`, newValue ? 'success' : 'info');
  }

  function updateSettingsUI() {
    // Update sound toggle button state
    const soundsBtn = document.getElementById('soundsEnabled');
    if (soundsBtn) {
      soundsBtn.classList.toggle('on', state.settings.soundEnabled ?? true);
    }

    // Update device trust toggle button state
    const deviceBtn = document.getElementById('deviceTrustEnabled');
    if (deviceBtn) {
      deviceBtn.classList.toggle('on', state.settings.deviceTrustEnabled ?? false);
    }

    // Update debug mode toggle button state
    const debugBtn = document.getElementById('debugModeEnabled');
    if (debugBtn) {
      const debugEnabled = state.userSettings?.debugMode ?? false;
      debugBtn.classList.toggle('on', debugEnabled);
      // Show/hide info box
      const infoBox = document.getElementById('debugModeInfo');
      if (infoBox) infoBox.style.display = debugEnabled ? 'flex' : 'none';
      // Show/hide version badge
      const versionBadge = document.getElementById('versionBadge');
      if (versionBadge) {
        versionBadge.style.display = debugEnabled ? 'flex' : 'none';
        // Set version text from loaded panel version
        const versionText = document.getElementById('versionText');
        if (versionText && panelVersionInfo?.version) {
          versionText.textContent = panelVersionInfo.version;
        }
      }
    }

    // Update watchlist alerts toggle button state
    const watchlistBtn = document.getElementById('watchlistAlertsEnabled');
    if (watchlistBtn) {
      watchlistBtn.classList.toggle('on', state.userSettings?.watchlistAlerts ?? true);
    }

    // Update volume if the slider exists
    const volumeSlider = document.getElementById('volumeSlider');
    const volumeHint = document.getElementById('volumeHint');
    if (volumeSlider && window.MX.sounds) {
      const vol = Math.round((window.MX.sounds.getVolume() ?? 0.5) * 100);
      volumeSlider.value = vol;
      if (volumeHint) volumeHint.textContent = vol + '%';
    }
  }

  function setVolume(value) {
    const vol = parseInt(value, 10) / 100;
    if (window.MX.sounds) {
      window.MX.sounds.setVolume(vol);
    }
    const hint = document.getElementById('volumeHint');
    if (hint) hint.textContent = value + '%';
  }

  // ===== THEME CUSTOMIZATION =====

  /**
   * Convert hex color to HSL values
   */
  function hexToHSL(hex) {
    let r = parseInt(hex.slice(1, 3), 16) / 255;
    let g = parseInt(hex.slice(3, 5), 16) / 255;
    let b = parseInt(hex.slice(5, 7), 16) / 255;

    const max = Math.max(r, g, b), min = Math.min(r, g, b);
    let h, s, l = (max + min) / 2;

    if (max === min) {
      h = s = 0;
    } else {
      const d = max - min;
      s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
      switch (max) {
        case r: h = ((g - b) / d + (g < b ? 6 : 0)) / 6; break;
        case g: h = ((b - r) / d + 2) / 6; break;
        case b: h = ((r - g) / d + 4) / 6; break;
      }
    }
    return { h: h * 360, s: s * 100, l: l * 100 };
  }

  /**
   * Convert HSL to hex color
   */
  function hslToHex(h, s, l) {
    s /= 100;
    l /= 100;
    const a = s * Math.min(l, 1 - l);
    const f = n => {
      const k = (n + h / 30) % 12;
      const color = l - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
      return Math.round(255 * color).toString(16).padStart(2, '0');
    };
    return `#${f(0)}${f(8)}${f(4)}`;
  }

  /**
   * Set the theme color and apply it to the entire panel
   */
  function setThemeColor(color) {
    const root = document.documentElement;
    const hsl = hexToHSL(color);

    // Generate color variants
    const colorLight = hslToHex(hsl.h, Math.min(hsl.s + 15, 100), Math.min(hsl.l + 15, 85));
    const colorDark = hslToHex(hsl.h, hsl.s, Math.max(hsl.l - 15, 15));

    // Extract RGB for glow and opacity variations
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);

    // Extract RGB for light variant
    const rL = parseInt(colorLight.slice(1, 3), 16);
    const gL = parseInt(colorLight.slice(3, 5), 16);
    const bL = parseInt(colorLight.slice(5, 7), 16);

    // Apply theme hue - this shifts ALL derived colors (backgrounds, borders, muted text)
    root.style.setProperty('--theme-h', Math.round(hsl.h));

    // Apply main CSS variables
    root.style.setProperty('--primary', color);
    root.style.setProperty('--primary-light', colorLight);
    root.style.setProperty('--primary-dark', colorDark);
    root.style.setProperty('--primary-rgb', `${r}, ${g}, ${b}`);
    root.style.setProperty('--primary-light-rgb', `${rL}, ${gL}, ${bL}`);

    // Apply opacity variations for all UI elements
    root.style.setProperty('--primary-glow', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-05', `rgba(${r}, ${g}, ${b}, 0.05)`);
    root.style.setProperty('--primary-08', `rgba(${r}, ${g}, ${b}, 0.08)`);
    root.style.setProperty('--primary-10', `rgba(${r}, ${g}, ${b}, 0.10)`);
    root.style.setProperty('--primary-12', `rgba(${r}, ${g}, ${b}, 0.12)`);
    root.style.setProperty('--primary-15', `rgba(${r}, ${g}, ${b}, 0.15)`);
    root.style.setProperty('--primary-18', `rgba(${r}, ${g}, ${b}, 0.18)`);
    root.style.setProperty('--primary-20', `rgba(${r}, ${g}, ${b}, 0.20)`);
    root.style.setProperty('--primary-22', `rgba(${r}, ${g}, ${b}, 0.22)`);
    root.style.setProperty('--primary-25', `rgba(${r}, ${g}, ${b}, 0.25)`);
    root.style.setProperty('--primary-30', `rgba(${r}, ${g}, ${b}, 0.30)`);
    root.style.setProperty('--primary-35', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-40', `rgba(${r}, ${g}, ${b}, 0.40)`);
    root.style.setProperty('--primary-45', `rgba(${r}, ${g}, ${b}, 0.45)`);
    root.style.setProperty('--primary-50', `rgba(${r}, ${g}, ${b}, 0.50)`);
    root.style.setProperty('--primary-60', `rgba(${r}, ${g}, ${b}, 0.60)`);
    root.style.setProperty('--line-glow', `rgba(${r}, ${g}, ${b}, 0.22)`);

    // Check if it's a light color - add text shadows for readability
    const isLight = hsl.l > 60;
    document.body.classList.toggle('light-theme-text', isLight);

    // Update preset buttons
    document.querySelectorAll('.theme-preset').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.color === color);
    });

    // Update custom color picker
    const picker = document.getElementById('customColorPicker');
    if (picker) picker.value = color;

    // Save to state
    if (!state.userSettings) state.userSettings = {};
    state.userSettings.themeColor = color;
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { themeColor: color });
    }

    // Update background pattern colors if applicable
    applyBackgroundPattern(state.userSettings.backgroundPattern || 'aurora');

    window.MX.sounds?.click();
  }

  /**
   * Set the background pattern
   */
  function setBackgroundPattern(pattern) {
    // Update button states
    document.querySelectorAll('.pattern-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.pattern === pattern);
    });

    // Apply the pattern
    applyBackgroundPattern(pattern);

    // Save to state
    if (!state.userSettings) state.userSettings = {};
    state.userSettings.backgroundPattern = pattern;
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { backgroundPattern: pattern });
    }

    window.MX.sounds?.click();
  }

  /**
   * Apply background pattern to the body
   */
  function applyBackgroundPattern(pattern) {
    const body = document.body;
    const color = state.userSettings?.themeColor || '#2d7aed';
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);
    const accent = '#7c5cff';
    const ar = 124, ag = 92, ab = 255;

    let bg;
    switch (pattern) {
      case 'aurora':
      default:
        bg = `
          radial-gradient(ellipse 1400px 900px at 10% 15%, rgba(${r}, ${g}, ${b}, 0.18), transparent 55%),
          radial-gradient(ellipse 1200px 800px at 90% 80%, rgba(${ar}, ${ag}, ${ab}, 0.12), transparent 50%),
          radial-gradient(ellipse 800px 600px at 50% 100%, rgba(16, 185, 129, 0.08), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'waves':
        bg = `
          repeating-linear-gradient(
            -45deg,
            transparent,
            transparent 8px,
            rgba(${r}, ${g}, ${b}, 0.08) 8px,
            rgba(${r}, ${g}, ${b}, 0.08) 16px
          ),
          repeating-linear-gradient(
            45deg,
            transparent,
            transparent 8px,
            rgba(${ar}, ${ag}, ${ab}, 0.05) 8px,
            rgba(${ar}, ${ag}, ${ab}, 0.05) 16px
          ),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'stars':
        bg = `
          radial-gradient(1.5px 1.5px at 10% 20%, rgba(255,255,255,0.8), transparent),
          radial-gradient(1px 1px at 30% 60%, rgba(255,255,255,0.6), transparent),
          radial-gradient(1.5px 1.5px at 50% 30%, rgba(255,255,255,0.7), transparent),
          radial-gradient(1px 1px at 70% 70%, rgba(255,255,255,0.5), transparent),
          radial-gradient(2px 2px at 90% 40%, rgba(${r}, ${g}, ${b}, 0.8), transparent),
          radial-gradient(1px 1px at 15% 85%, rgba(255,255,255,0.6), transparent),
          radial-gradient(2px 2px at 25% 15%, rgba(${ar}, ${ag}, ${ab}, 0.7), transparent),
          radial-gradient(1px 1px at 85% 85%, rgba(255,255,255,0.5), transparent),
          radial-gradient(1.5px 1.5px at 45% 45%, rgba(255,255,255,0.6), transparent),
          radial-gradient(1px 1px at 65% 15%, rgba(255,255,255,0.4), transparent),
          radial-gradient(ellipse 800px 600px at 50% 100%, rgba(${r}, ${g}, ${b}, 0.05), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'grid':
        bg = `
          linear-gradient(rgba(${r}, ${g}, ${b}, 0.06) 1px, transparent 1px),
          linear-gradient(90deg, rgba(${r}, ${g}, ${b}, 0.06) 1px, transparent 1px),
          radial-gradient(ellipse 800px 600px at 50% 0%, rgba(${r}, ${g}, ${b}, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '40px 40px, 40px 40px, 100% 100%, 100% 100%';
        break;
      case 'circuits':
        bg = `
          linear-gradient(90deg, transparent 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 50.5%, transparent 50.5%),
          linear-gradient(transparent 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 50.5%, transparent 50.5%),
          radial-gradient(circle at 25% 25%, rgba(${r}, ${g}, ${b}, 0.2) 2px, transparent 2px),
          radial-gradient(circle at 75% 75%, rgba(${ar}, ${ag}, ${ab}, 0.15) 2px, transparent 2px),
          radial-gradient(ellipse 600px 400px at 20% 80%, rgba(${r}, ${g}, ${b}, 0.08), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '60px 60px, 60px 60px, 60px 60px, 60px 60px, 100% 100%, 100% 100%';
        break;
      case 'hexagons':
        bg = `
          radial-gradient(circle at 0% 50%, rgba(${r}, ${g}, ${b}, 0.1) 2px, transparent 2px),
          radial-gradient(circle at 100% 50%, rgba(${r}, ${g}, ${b}, 0.1) 2px, transparent 2px),
          radial-gradient(circle at 50% 0%, rgba(${ar}, ${ag}, ${ab}, 0.08) 2px, transparent 2px),
          radial-gradient(circle at 50% 100%, rgba(${ar}, ${ag}, ${ab}, 0.08) 2px, transparent 2px),
          radial-gradient(ellipse 600px 400px at 80% 20%, rgba(${r}, ${g}, ${b}, 0.1), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '50px 50px, 50px 50px, 50px 50px, 50px 50px, 100% 100%, 100% 100%';
        break;
      case 'particles':
        bg = `
          radial-gradient(circle at 15% 25%, rgba(${r}, ${g}, ${b}, 0.5) 2px, transparent 2px),
          radial-gradient(circle at 45% 65%, rgba(${ar}, ${ag}, ${ab}, 0.4) 2px, transparent 2px),
          radial-gradient(circle at 75% 35%, rgba(16, 185, 129, 0.4) 2px, transparent 2px),
          radial-gradient(circle at 85% 85%, rgba(${r}, ${g}, ${b}, 0.3) 3px, transparent 3px),
          radial-gradient(circle at 25% 75%, rgba(${ar}, ${ag}, ${ab}, 0.3) 3px, transparent 3px),
          radial-gradient(circle at 55% 15%, rgba(${r}, ${g}, ${b}, 0.2) 2px, transparent 2px),
          radial-gradient(circle at 35% 45%, rgba(16, 185, 129, 0.25) 2px, transparent 2px),
          radial-gradient(circle at 65% 90%, rgba(${ar}, ${ag}, ${ab}, 0.2) 2px, transparent 2px),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'nebula':
        bg = `
          radial-gradient(ellipse 600px 500px at 30% 40%, rgba(${ar}, ${ag}, ${ab}, 0.25), transparent 50%),
          radial-gradient(ellipse 500px 400px at 70% 60%, rgba(236, 72, 153, 0.18), transparent 50%),
          radial-gradient(ellipse 700px 500px at 50% 50%, rgba(${r}, ${g}, ${b}, 0.12), transparent 60%),
          radial-gradient(ellipse 300px 200px at 20% 80%, rgba(16, 185, 129, 0.1), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'matrix':
        bg = `
          repeating-linear-gradient(
            180deg,
            transparent,
            transparent 3px,
            rgba(16, 185, 129, 0.04) 3px,
            rgba(16, 185, 129, 0.04) 6px
          ),
          linear-gradient(180deg, rgba(16, 185, 129, 0.15) 0%, transparent 70%),
          radial-gradient(ellipse 600px 400px at 50% 0%, rgba(16, 185, 129, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'rain':
        bg = `
          repeating-linear-gradient(
            180deg,
            transparent,
            transparent 20px,
            rgba(${r}, ${g}, ${b}, 0.08) 20px,
            rgba(${r}, ${g}, ${b}, 0.08) 22px
          ),
          repeating-linear-gradient(
            180deg,
            transparent 10px,
            transparent 30px,
            rgba(${r}, ${g}, ${b}, 0.05) 30px,
            rgba(${r}, ${g}, ${b}, 0.05) 32px
          ),
          radial-gradient(ellipse 800px 400px at 50% 100%, rgba(${r}, ${g}, ${b}, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '4px 100%, 9px 100%, 100% 100%, 100% 100%';
        break;
      case 'geometric':
        bg = `
          linear-gradient(135deg, transparent 46%, rgba(${r}, ${g}, ${b}, 0.06) 46%, rgba(${r}, ${g}, ${b}, 0.06) 54%, transparent 54%),
          linear-gradient(-135deg, transparent 46%, rgba(${ar}, ${ag}, ${ab}, 0.04) 46%, rgba(${ar}, ${ag}, ${ab}, 0.04) 54%, transparent 54%),
          radial-gradient(ellipse 600px 400px at 50% 50%, rgba(${r}, ${g}, ${b}, 0.08), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '60px 60px, 60px 60px, 100% 100%, 100% 100%';
        break;
      case 'gradient':
        bg = `
          linear-gradient(135deg, var(--bg0) 0%, rgba(${r}, ${g}, ${b}, 0.08) 50%, var(--bg0) 100%)
        `;
        break;
      case 'minimal':
        bg = `linear-gradient(180deg, var(--bg0), var(--bg1))`;
        break;
    }

    body.style.background = bg;

    // Reset background-size for patterns that don't need custom sizes
    if (!['grid', 'circuits', 'hexagons', 'rain', 'geometric'].includes(pattern)) {
      body.style.backgroundSize = '';
    }
  }

  /**
   * Apply theme settings from saved state on page load
   */
  function applyThemeFromState() {
    const color = state.userSettings?.themeColor || '#2d7aed';
    const pattern = state.userSettings?.backgroundPattern || 'aurora';

    // Apply color without triggering save
    const root = document.documentElement;
    const hsl = hexToHSL(color);
    const colorLight = hslToHex(hsl.h, Math.min(hsl.s + 15, 100), Math.min(hsl.l + 15, 85));
    const colorDark = hslToHex(hsl.h, hsl.s, Math.max(hsl.l - 15, 15));
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);

    // Extract RGB for light variant
    const rL = parseInt(colorLight.slice(1, 3), 16);
    const gL = parseInt(colorLight.slice(3, 5), 16);
    const bL = parseInt(colorLight.slice(5, 7), 16);

    // Apply theme hue - this shifts ALL derived colors (backgrounds, borders, muted text)
    root.style.setProperty('--theme-h', Math.round(hsl.h));

    // Apply main CSS variables
    root.style.setProperty('--primary', color);
    root.style.setProperty('--primary-light', colorLight);
    root.style.setProperty('--primary-dark', colorDark);
    root.style.setProperty('--primary-rgb', `${r}, ${g}, ${b}`);
    root.style.setProperty('--primary-light-rgb', `${rL}, ${gL}, ${bL}`);

    // Apply all opacity variations for UI elements
    root.style.setProperty('--primary-glow', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-04', `rgba(${r}, ${g}, ${b}, 0.04)`);
    root.style.setProperty('--primary-05', `rgba(${r}, ${g}, ${b}, 0.05)`);
    root.style.setProperty('--primary-06', `rgba(${r}, ${g}, ${b}, 0.06)`);
    root.style.setProperty('--primary-08', `rgba(${r}, ${g}, ${b}, 0.08)`);
    root.style.setProperty('--primary-10', `rgba(${r}, ${g}, ${b}, 0.10)`);
    root.style.setProperty('--primary-12', `rgba(${r}, ${g}, ${b}, 0.12)`);
    root.style.setProperty('--primary-15', `rgba(${r}, ${g}, ${b}, 0.15)`);
    root.style.setProperty('--primary-18', `rgba(${r}, ${g}, ${b}, 0.18)`);
    root.style.setProperty('--primary-20', `rgba(${r}, ${g}, ${b}, 0.20)`);
    root.style.setProperty('--primary-22', `rgba(${r}, ${g}, ${b}, 0.22)`);
    root.style.setProperty('--primary-25', `rgba(${r}, ${g}, ${b}, 0.25)`);
    root.style.setProperty('--primary-28', `rgba(${r}, ${g}, ${b}, 0.28)`);
    root.style.setProperty('--primary-30', `rgba(${r}, ${g}, ${b}, 0.30)`);
    root.style.setProperty('--primary-35', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-40', `rgba(${r}, ${g}, ${b}, 0.40)`);
    root.style.setProperty('--primary-45', `rgba(${r}, ${g}, ${b}, 0.45)`);
    root.style.setProperty('--primary-50', `rgba(${r}, ${g}, ${b}, 0.50)`);
    root.style.setProperty('--primary-60', `rgba(${r}, ${g}, ${b}, 0.60)`);
    root.style.setProperty('--line-glow', `rgba(${r}, ${g}, ${b}, 0.22)`);

    const isLight = hsl.l > 60;
    document.body.classList.toggle('light-theme-text', isLight);

    // Update preset buttons
    document.querySelectorAll('.theme-preset').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.color === color);
    });

    // Update pattern buttons
    document.querySelectorAll('.pattern-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.pattern === pattern);
    });

    // Update custom color picker
    const picker = document.getElementById('customColorPicker');
    if (picker) picker.value = color;

    // Apply background pattern
    applyBackgroundPattern(pattern);
  }

  // ===== DEVELOPER CHECKLIST =====
  state.devChecklist = state.devChecklist || [];

  function renderDevChecklist() {
    const container = document.getElementById('checklistContainer');
    if (!container) return;

    const items = state.devChecklist || [];
    if (items.length === 0) {
      container.innerHTML = '<div class="loading-text">No checklist items. Click "Add Item" to create one.</div>';
      updateChecklistProgress();
      return;
    }

    // Group by category
    const categories = {};
    items.forEach(item => {
      const cat = item.category || 'Uncategorized';
      if (!categories[cat]) categories[cat] = [];
      categories[cat].push(item);
    });

    let html = '';
    for (const [category, catItems] of Object.entries(categories)) {
      const checkedCount = catItems.filter(i => i.checked).length;
      const catProgress = catItems.length > 0 ? Math.round((checkedCount / catItems.length) * 100) : 0;

      html += `<div class="checklist-category" style="margin-bottom:16px">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <span style="font-weight:600;color:var(--text-primary)">${escapeHtml(category)}</span>
          <span style="font-size:12px;color:var(--text-secondary)">${checkedCount}/${catItems.length} (${catProgress}%)</span>
        </div>`;

      for (const item of catItems) {
        const checkedClass = item.checked ? 'checked' : '';
        html += `<div class="checklist-item ${checkedClass}" style="display:flex;align-items:flex-start;gap:10px;padding:8px 12px;background:rgba(0,0,0,.2);border-radius:6px;margin-bottom:6px;cursor:pointer" onclick="toggleChecklistItem('${item.id}', ${!item.checked})">
          <div style="width:20px;height:20px;border:2px solid var(--border);border-radius:4px;display:flex;align-items:center;justify-content:center;flex-shrink:0;${item.checked ? 'background:var(--good);border-color:var(--good)' : ''}">
            ${item.checked ? '<i class="fa-solid fa-check" style="color:#fff;font-size:11px"></i>' : ''}
          </div>
          <div style="flex:1;min-width:0">
            <div style="color:var(--text-primary);word-wrap:break-word;overflow-wrap:break-word;${item.checked ? 'text-decoration:line-through;opacity:0.7' : ''}">${escapeHtml(item.title)}</div>
            ${item.description ? `<div style="font-size:12px;color:var(--text-secondary);margin-top:4px;white-space:pre-wrap;word-wrap:break-word;overflow-wrap:break-word">${escapeHtml(item.description)}</div>` : ''}
          </div>
          ${item.id.startsWith('custom-') ? `<button class="btn tiny danger" onclick="event.stopPropagation();deleteChecklistItem('${item.id}')" title="Delete"><i class="fa-solid fa-trash"></i></button>` : ''}
        </div>`;
      }
      html += '</div>';
    }

    container.innerHTML = html;
    updateChecklistProgress();
  }

  function updateChecklistProgress() {
    const el = document.getElementById('checklistProgress');
    if (!el) return;
    const items = state.devChecklist || [];
    const checked = items.filter(i => i.checked).length;
    const total = items.length;
    const pct = total > 0 ? Math.round((checked / total) * 100) : 0;
    el.textContent = `${checked}/${total} complete (${pct}%)`;
  }

  function toggleChecklistItem(itemId, checked) {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('TOGGLE_CHECKLIST_ITEM', { itemId, checked });
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
  }

  // Checklist modal state
  let pendingDeleteItemId = null;

  function addChecklistItem() {
    // Open the modal instead of using prompt()
    const overlay = document.getElementById('checklistAddOverlay');
    if (overlay) {
      // Clear previous values
      const titleEl = document.getElementById('checklistItemTitle');
      const categoryEl = document.getElementById('checklistItemCategory');
      const descEl = document.getElementById('checklistItemDesc');
      if (titleEl) titleEl.value = '';
      if (categoryEl) categoryEl.value = 'Custom';
      if (descEl) descEl.value = '';
      overlay.classList.add('show');
      // Focus on title input
      setTimeout(() => titleEl?.focus(), 100);
    }
  }

  function closeChecklistModal() {
    const overlay = document.getElementById('checklistAddOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function submitChecklistItem() {
    const titleEl = document.getElementById('checklistItemTitle');
    const categoryEl = document.getElementById('checklistItemCategory');
    const descEl = document.getElementById('checklistItemDesc');

    const title = titleEl?.value?.trim();
    const category = categoryEl?.value?.trim() || 'Custom';
    const description = descEl?.value?.trim() || '';

    if (!title) {
      toast('error', 'Error', 'Title is required');
      titleEl?.focus();
      return;
    }

    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('ADD_CHECKLIST_ITEM', { title, category, description });
      toast('info', 'Adding', 'Creating checklist item...');
      closeChecklistModal();
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
  }

  function deleteChecklistItem(itemId) {
    // Find the item to show its name in the confirmation modal
    const item = (state.devChecklist || []).find(i => i.id === itemId);
    pendingDeleteItemId = itemId;

    const overlay = document.getElementById('checklistDeleteOverlay');
    const nameEl = document.getElementById('checklistDeleteItemName');
    if (nameEl && item) {
      nameEl.textContent = item.title || 'Unknown item';
    }
    if (overlay) overlay.classList.add('show');
  }

  function closeChecklistDeleteModal() {
    const overlay = document.getElementById('checklistDeleteOverlay');
    if (overlay) overlay.classList.remove('show');
    pendingDeleteItemId = null;
  }

  function confirmDeleteChecklistItem() {
    if (!pendingDeleteItemId) return;

    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('DELETE_CHECKLIST_ITEM', { itemId: pendingDeleteItemId });
      toast('info', 'Deleting', 'Removing checklist item...');
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
    closeChecklistDeleteModal();
  }

  function resetChecklist() {
    // Open the reset confirmation modal
    const overlay = document.getElementById('checklistResetOverlay');
    if (overlay) overlay.classList.add('show');
  }

  function closeChecklistResetModal() {
    const overlay = document.getElementById('checklistResetOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function confirmResetChecklist() {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      // Uncheck all items
      for (const item of state.devChecklist || []) {
        if (item.checked) {
          ws.send('TOGGLE_CHECKLIST_ITEM', { itemId: item.id, checked: false });
        }
      }
      toast('info', 'Resetting', 'Clearing all checkmarks...');
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
    closeChecklistResetModal();
  }

  function loadDevChecklist() {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('GET_DEV_CHECKLIST');
    }
  }

  // Expose new functions globally
  window.attemptReconnect = attemptReconnect;
  window.toggleSidebar = toggleSidebar;
  window.showAlertBar = showAlertBar;
  window.dismissAlertBar = dismissAlertBar;
  window.viewAlertPlayer = viewAlertPlayer;
  window.toggleMyAlert = toggleMyAlert;
  window.setAnticheatMode = setAnticheatMode;
  window.setWatchlistStyle = setWatchlistStyle;
  window.togglePanelSounds = togglePanelSounds;
  window.toggleDeviceTrust = toggleDeviceTrust;
  window.toggleDebugMode = toggleDebugMode;
  window.toggleWatchlistAlerts = toggleWatchlistAlerts;
  window.setVolume = setVolume;
  window.setThemeColor = setThemeColor;
  window.setBackgroundPattern = setBackgroundPattern;
  window.applyThemeFromState = applyThemeFromState;
  window.showDisconnect = showDisconnect;
  window.hideDisconnect = hideDisconnect;
  window.updateLastPong = updateLastPong;
  window.toggleChecklistItem = toggleChecklistItem;
  window.addChecklistItem = addChecklistItem;
  window.closeChecklistModal = closeChecklistModal;
  window.submitChecklistItem = submitChecklistItem;
  window.deleteChecklistItem = deleteChecklistItem;
  window.closeChecklistDeleteModal = closeChecklistDeleteModal;
  window.confirmDeleteChecklistItem = confirmDeleteChecklistItem;
  window.resetChecklist = resetChecklist;
  window.closeChecklistResetModal = closeChecklistResetModal;
  window.confirmResetChecklist = confirmResetChecklist;
  window.loadDevChecklist = loadDevChecklist;

  // ===== DEVELOPER MODE =====
  function toggleDevMode() {
    state.settings = state.settings || {};
    state.settings.developerMode = !state.settings.developerMode;

    const toggle = document.getElementById('devModeToggle');
    const devSection = document.getElementById('sbDevSection');

    if (toggle) toggle.classList.toggle('active', state.settings.developerMode);
    if (devSection) devSection.style.display = state.settings.developerMode ? 'block' : 'none';

    saveState();
    toast('info', 'Developer Mode', state.settings.developerMode ? 'Enabled' : 'Disabled');

    // Load checklist when enabling
    if (state.settings.developerMode) {
      loadDevChecklist();
    }
  }

  function applyDevModeUI() {
    const toggle = document.getElementById('devModeToggle');
    const devSection = document.getElementById('sbDevSection');

    if (toggle) toggle.classList.toggle('active', state.settings?.developerMode || false);
    if (devSection) devSection.style.display = state.settings?.developerMode ? 'block' : 'none';
  }

  window.toggleDevMode = toggleDevMode;
  window.applyDevModeUI = applyDevModeUI;

  // ===== STRESS TESTING =====
  // Now uses server-side data generation for realistic testing
  let activeStressTests = new Map();
  let stressTestCleanupTimers = [];

  /**
   * Create test players via server
   * Server will add actual players to the database
   */
  function startCreateTestPlayers() {
    const count = Math.min(10000, Math.max(1, parseInt(document.getElementById('spoofPlayerCount')?.value || '100', 10)));

    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server for stress testing');
      return;
    }

    if (!confirm(`This will create ${count.toLocaleString()} TEST players in the database.\n\nThese players will have names like "TestPlayer_1234" and can be cleaned up later.\n\nContinue?`)) {
      return;
    }

    const progressEl = document.getElementById('spoofPlayerProgress');
    const fillEl = document.getElementById('spoofPlayerFill');
    const logEl = document.getElementById('spoofPlayerLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = '<div class="stress-log-entry">Requesting server to create test players...</div>';

    // Send request to server
    window.MX.ws.send('DEV_STRESS_CREATE_PLAYERS', {
      count: count,
      timestamp: Date.now()
    });

    toast('info', 'Stress Test', `Requesting server to create ${count.toLocaleString()} test players...`);
  }

  /**
   * Create test punishments via server
   * Server will add actual punishments to the database
   */
  function startCreateTestPunishments() {
    const count = Math.min(5000, Math.max(1, parseInt(document.getElementById('spoofPunishmentCount')?.value || '500', 10)));

    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server for stress testing');
      return;
    }

    if (!confirm(`This will create ${count.toLocaleString()} TEST punishments in the database.\n\nThese punishments will be marked as test data and can be cleaned up later.\n\nContinue?`)) {
      return;
    }

    const progressEl = document.getElementById('spoofPunishmentProgress');
    const fillEl = document.getElementById('spoofPunishmentFill');
    const logEl = document.getElementById('spoofPunishmentLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = '<div class="stress-log-entry">Requesting server to create test punishments...</div>';

    // Send request to server
    window.MX.ws.send('DEV_STRESS_CREATE_PUNISHMENTS', {
      count: count,
      timestamp: Date.now()
    });

    toast('info', 'Stress Test', `Requesting server to create ${count.toLocaleString()} test punishments...`);
  }

  /**
   * Request server to clean up all test data
   */
  function cleanupTestData() {
    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server to clean up test data');
      return;
    }

    if (!confirm('This will remove ALL test data created by stress tests from the database.\n\nThis includes test players, test punishments, and test tokens.\n\nContinue?')) {
      return;
    }

    window.MX.ws.send('DEV_STRESS_CLEANUP', {
      timestamp: Date.now()
    });

    toast('info', 'Cleanup', 'Requesting server to clean up test data...');
  }

  // Handler for stress test progress updates from server
  function handleStressTestProgress(data) {
    const { testType, current, total, complete, duration, error, message } = data;

    // Map test types to element IDs
    const elementMap = {
      'players': 'spoofPlayer',
      'punishments': 'spoofPunishment',
      'tokens': 'tokenStress'
    };

    const testId = elementMap[testType] || testType;
    const fillEl = document.getElementById(`${testId}Fill`);
    const logEl = document.getElementById(`${testId}Log`);

    if (error) {
      if (logEl) logEl.innerHTML = `<div class="stress-log-entry error">${escapeHtml(message || error)}</div>`;
      toast('bad', 'Stress Test Error', message || error);
      return;
    }

    if (complete) {
      if (fillEl) fillEl.style.width = '100%';
      if (logEl) {
        const perSec = duration > 0 ? Math.round(total / (duration / 1000)) : total;
        logEl.innerHTML = `<div class="stress-log-entry success">Complete! Created ${total.toLocaleString()} items in ${duration}ms (${perSec.toLocaleString()}/sec)</div>`;
      }
      toast('ok', 'Stress Test Complete', `Created ${total.toLocaleString()} test ${testType}`);

      // Refresh data from server
      window.MX.ws.send('GET_DATA', {});
    } else {
      const pct = Math.round((current / total) * 100);
      if (fillEl) fillEl.style.width = `${pct}%`;
      if (logEl) {
        logEl.innerHTML = `<div class="stress-log-entry">Creating ${current.toLocaleString()} / ${total.toLocaleString()} (${pct}%)</div>`;
      }
    }
  }

  // Register the handler
  if (window.MX.ws) {
    window.MX.ws.on('DEV_STRESS_PROGRESS', handleStressTestProgress);
    window.MX.ws.on('DEV_STRESS_COMPLETE', handleStressTestProgress);
    window.MX.ws.on('DEV_STRESS_ERROR', handleStressTestProgress);
  }

  function stopAllStressTests() {
    if (window.MX.ws?.isConnected()) {
      window.MX.ws.send('DEV_STRESS_STOP', {});
    }
    activeStressTests.clear();
    toast('info', 'Stress Tests', 'Stop request sent to server.');
  }

  // Legacy function names for compatibility
  function startSpoofPlayers() { startCreateTestPlayers(); }
  function startSpoofPunishments() { startCreateTestPunishments(); }
  function clearAllSpoofedData() { cleanupTestData(); }

  // Staff spoofing is local-only (no database table)
  function startSpoofStaff() {
    toast('info', 'Info', 'Staff stress testing only affects local UI display.');
    const count = Math.min(50000, Math.max(1, parseInt(document.getElementById('spoofStaffCount')?.value || '50', 10)));

    if (!state.staffList) state.staffList = [];
    for (let i = 0; i < count; i++) {
      state.staffList.push({
        id: `test-staff-${i}`,
        name: `TestStaff${i}`,
        rank: ['Admin', 'Moderator', 'Helper'][Math.floor(Math.random() * 3)],
        online: Math.random() > 0.5,
        _test: true
      });
    }
    ui.renderDashboard();
    toast('ok', 'Complete', `Added ${count} test staff to local display.`);
  }

  // Automod alerts are local-only (temporary events)
  function startSpoofAutomod() {
    toast('info', 'Info', 'Automod stress testing only affects local UI display.');
    const count = Math.min(5000, Math.max(1, parseInt(document.getElementById('spoofAutomodCount')?.value || '200', 10)));
    const triggers = ['Spam detected', 'Caps lock', 'Advertising', 'Swearing', 'Repeating messages'];

    for (let i = 0; i < count; i++) {
      state.watchAlerts.push({
        type: 'Automod',
        details: triggers[Math.floor(Math.random() * triggers.length)],
        playerName: `TestPlayer${Math.floor(Math.random() * 1000)}`,
        playerUuid: `00000000-0000-0000-0000-${String(Math.floor(Math.random() * 1000)).padStart(12, '0')}`,
        t: now() - Math.floor(Math.random() * 3600000),
        _test: true
      });
    }
    ui.renderDashboard();
    toast('ok', 'Complete', `Added ${count} test automod alerts to local display.`);
  }

  // Clear local test data (for staff and automod which are local-only)
  function clearLocalTestData() {
    if (state.staffList) state.staffList = state.staffList.filter(s => !s._test);
    state.watchAlerts = state.watchAlerts.filter(a => !a._test);
    ui.renderAll();
    toast('ok', 'Cleared', 'Local test data has been removed.');
  }

  // Debug log functions
  function clearDebugLogs() {
    const logEl = document.getElementById('devDebugLogs');
    if (logEl) logEl.innerHTML = '<div style="color:var(--muted)">[Debug console cleared]</div>';
  }

  function copyDebugLogs() {
    const logEl = document.getElementById('devDebugLogs');
    if (logEl) {
      navigator.clipboard.writeText(logEl.innerText).then(() => {
        toast('ok', 'Copied', 'Debug logs copied to clipboard.');
      });
    }
  }

  window.startSpoofPlayers = startSpoofPlayers;
  window.startSpoofStaff = startSpoofStaff;
  window.startSpoofPunishments = startSpoofPunishments;
  window.startSpoofAutomod = startSpoofAutomod;
  window.startCreateTestPlayers = startCreateTestPlayers;
  window.startCreateTestPunishments = startCreateTestPunishments;
  window.stopAllStressTests = stopAllStressTests;
  window.clearAllSpoofedData = clearAllSpoofedData;
  window.cleanupTestData = cleanupTestData;
  window.clearLocalTestData = clearLocalTestData;
  window.clearDebugLogs = clearDebugLogs;
  window.copyDebugLogs = copyDebugLogs;

  // ===== NOTIFICATION TESTING =====
  function testNotification(category) {
    const testMessages = {
      punishments: { title: 'Punishment', message: 'TestPlayer was banned by TestAdmin' },
      automod: { title: 'Automod', message: 'TestPlayer triggered Spam Filter', severity: 'warn' },
      anticheat: { title: 'Anticheat', message: 'TestPlayer flagged for Reach (VL: 15)', severity: 'warn' },
      watchlist: { title: 'Watchlist', message: 'TestPlayer: Suspicious activity detected', severity: 'warn' },
      staffChat: { title: 'Staff Chat', message: 'TestAdmin: This is a test message' }
    };

    const test = testMessages[category];
    if (!test) {
      toast('error', 'Error', `Unknown category: ${category}`);
      return;
    }

    // Show the alert using the showPanelAlert function which respects settings
    showPanelAlert(category, test.title, test.message, {
      severity: test.severity || 'info',
      playerId: 'test-player-uuid',
      playerName: 'TestPlayer'
    });

    devtoolsLog('NOTIFY', `Tested ${category} notification (mode: ${state.staffSettings?.['webNotify' + category.charAt(0).toUpperCase() + category.slice(1)] || 'toast'})`, 'info');
  }

  function requestBrowserPermission() {
    if (!('Notification' in window)) {
      toast('error', 'Not Supported', 'Browser notifications are not supported in this browser.');
      return;
    }

    if (Notification.permission === 'granted') {
      toast('ok', 'Already Granted', 'Browser notification permission is already granted.');
      new Notification('ModereX', { body: 'Browser notifications are working!', icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      return;
    }

    if (Notification.permission === 'denied') {
      toast('error', 'Permission Denied', 'Browser notifications were previously denied. Reset in browser settings.');
      return;
    }

    Notification.requestPermission().then(permission => {
      if (permission === 'granted') {
        toast('ok', 'Permission Granted', 'Browser notifications are now enabled.');
        new Notification('ModereX', { body: 'Browser notifications are working!', icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      } else {
        toast('warn', 'Permission Denied', 'Browser notifications were denied.');
      }
    });
  }

  window.testNotification = testNotification;
  window.requestBrowserPermission = requestBrowserPermission;

  // ===== DEBUG PERMISSIONS =====
  function debugCheckPermissions() {
    const outputEl = document.getElementById('debugPermissionsOutput');
    if (outputEl) {
      outputEl.style.display = 'block';
      outputEl.innerHTML = '<div style="color:#06b6d4">Checking permissions...</div>';
    }

    // Get current state
    const permissions = state.permissions || [];
    const staffSettings = state.staffSettings || {};
    const user = state.currentUser || {};

    let html = '<div style="margin-bottom:12px;color:#22c55e;font-weight:600">Current Permissions State:</div>';

    // User info
    html += `<div style="margin-bottom:8px"><span style="color:#a78bfa">User:</span> ${user.name || 'Unknown'} (${user.uuid || 'No UUID'})</div>`;
    html += `<div style="margin-bottom:8px"><span style="color:#a78bfa">Authenticated:</span> ${user.name ? 'Yes' : 'No'}</div>`;

    // Permissions array
    html += '<div style="margin-bottom:8px"><span style="color:#a78bfa">Permissions Array:</span></div>';
    if (permissions.length === 0) {
      html += '<div style="color:#ef4444;margin-left:12px">⚠ No permissions received from server!</div>';
      html += '<div style="color:#f59e0b;margin-left:12px;font-size:11px">This is why settings show "No Permission"</div>';
    } else {
      permissions.forEach(p => {
        html += `<div style="margin-left:12px;color:#22c55e">✓ ${p}</div>`;
      });
    }

    // Alert-related settings
    html += '<div style="margin-top:12px;margin-bottom:8px"><span style="color:#a78bfa">Staff Settings (alert-related):</span></div>';
    const alertKeys = ['banAlerts', 'kickAlerts', 'muteAlerts', 'warnAlerts', 'pardonAlerts', 'automodAlerts', 'anticheatAlerts', 'nicknameAlerts', 'commandAlerts'];
    alertKeys.forEach(key => {
      const value = staffSettings[key];
      html += `<div style="margin-left:12px">${key}: <span style="color:${value ? '#22c55e' : '#6b7280'}">${value || 'not set'}</span></div>`;
    });

    // Show in output
    if (outputEl) {
      outputEl.innerHTML = html;
    }

    // Also log to system messages
    window.systemLog(`Permissions check: ${permissions.length} permissions found`, permissions.length > 0 ? 'success' : 'error');
    window.devtoolsLog('PERMISSIONS', `Found ${permissions.length} permissions: ${permissions.join(', ') || 'NONE'}`, permissions.length > 0 ? 'success' : 'error');

    // Log any errors
    if (permissions.length === 0) {
      window.systemLog('ERROR: No permissions received from backend!', 'error');
      window.devtoolsLog('PERMISSIONS', 'Backend is not sending permissions array in USER_SETTINGS_DATA response', 'error');
    }
  }

  function debugRefreshPermissions() {
    const outputEl = document.getElementById('debugPermissionsOutput');
    if (outputEl) {
      outputEl.style.display = 'block';
      outputEl.innerHTML = '<div style="color:#f59e0b">Requesting fresh permissions from server...</div>';
    }

    window.systemLog('Requesting permissions refresh from server...', 'info');
    window.devtoolsLog('PERMISSIONS', 'Sending GET_USER_SETTINGS request', 'info');

    // Request fresh settings from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_USER_SETTINGS', {});

      // Wait a moment and check again
      setTimeout(() => {
        debugCheckPermissions();
      }, 1000);
    } else {
      window.systemLog('ERROR: WebSocket not connected', 'error');
      if (outputEl) {
        outputEl.innerHTML = '<div style="color:#ef4444">WebSocket not connected!</div>';
      }
    }
  }

  window.debugCheckPermissions = debugCheckPermissions;
  window.debugRefreshPermissions = debugRefreshPermissions;

  // ===== TOKEN STRESS TEST =====
  function startTokenStressTest() {
    const count = Math.min(50000, Math.max(100, parseInt(document.getElementById('tokenStressCount')?.value || '1000', 10)));

    // Show warning modal first
    if (!confirm(`This will generate ${count.toLocaleString()} tokens and test authentication speed.\n\nYou will be LOGGED OUT after this test completes and must re-authenticate with your permanent token.\n\nDevice fingerprint bypass will be enabled - you MUST enter your token manually.\n\nContinue?`)) {
      return;
    }

    toast('info', 'Token Stress Test', `Generating ${count.toLocaleString()} tokens...`);

    // Send request to server to start token stress test
    window.MX.ws.send('DEV_TOKEN_STRESS_TEST', {
      count: count,
      timestamp: Date.now()
    });

    // Show progress
    const progressEl = document.getElementById('tokenStressProgress');
    const fillEl = document.getElementById('tokenStressFill');
    const logEl = document.getElementById('tokenStressLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = 'Starting token generation...';

    // Listen for progress updates
    const progressHandler = (data) => {
      if (data.type === 'TOKEN_STRESS_PROGRESS') {
        const pct = Math.round((data.current / data.total) * 100);
        if (fillEl) fillEl.style.width = pct + '%';
        if (logEl) logEl.innerHTML = `Generated ${data.current.toLocaleString()} / ${data.total.toLocaleString()} tokens (${pct}%)`;
      } else if (data.type === 'TOKEN_STRESS_COMPLETE') {
        if (fillEl) fillEl.style.width = '100%';
        if (logEl) logEl.innerHTML = `Complete! ${data.total.toLocaleString()} tokens in ${data.duration}ms (${data.tokensPerSecond} tokens/sec)`;
        toast('ok', 'Stress Test Complete', `Validated ${data.total.toLocaleString()} tokens in ${data.duration}ms`);

        // Force logout after 2 seconds
        setTimeout(() => {
          toast('warn', 'Logging Out', 'Re-authenticate with your permanent token.');
          localStorage.removeItem('mx_permanent_token');
          localStorage.removeItem('mx_token_device');
          localStorage.removeItem('mx_session');
          window.MX.auth.logout();
        }, 2000);
      }
    };

    window.MX.ws.on('message', progressHandler);
  }

  // ===== UUID DEV AUTHENTICATION =====
  function devUuidAuth() {
    const uuidInput = document.getElementById('devUuidInput');
    const uuid = uuidInput?.value?.trim();

    if (!uuid) {
      toast('bad', 'UUID Required', 'Please enter a valid UUID.');
      return;
    }

    // Basic UUID format validation
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
    if (!uuidRegex.test(uuid)) {
      toast('bad', 'Invalid UUID', 'UUID must be in format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx');
      return;
    }

    toast('info', 'Dev Auth', `Authenticating as UUID: ${uuid.substring(0, 8)}...`);

    // Send dev auth request
    window.MX.ws.send('AUTH_DEV_UUID', {
      uuid: uuid,
      timestamp: Date.now()
    });
  }

  window.startTokenStressTest = startTokenStressTest;
  window.devUuidAuth = devUuidAuth;

  // ===== WEB PANEL VERSION & GITHUB UPDATE CHECKER =====
  let panelVersionInfo = null;
  let currentPluginVersion = null;
  let latestGitHubVersion = null;
  let updateCheckInterval = null;
  let updateDismissedUntil = 0; // Timestamp when dismiss expires
  let currentBuildNumber = null;

  function loadPanelVersion() {
    // Fetch version from server API (reads from panel-version.properties)
    fetch('/api/panel-version?_=' + Date.now())
      .then(res => res.json())
      .then(data => {
        panelVersionInfo = data;
        // Update sidebar version display
        const versionEl = document.getElementById('panelVersion');
        if (versionEl && data.version) {
          versionEl.textContent = data.version;
        }
        // Store build number for comparison
        if (data.buildNumber) {
          currentBuildNumber = parseInt(data.buildNumber, 10);
        }
      })
      .catch(() => {
        // Silent fail - version display is optional
      });
  }

  function loadCurrentPluginVersion() {
    // Get current plugin version from server
    fetch('/api/plugin-version?_=' + Date.now())
      .then(res => res.json())
      .then(data => {
        console.log('[Update] Plugin version response:', data);
        if (data.version) {
          currentPluginVersion = data.version;
          // Store build number from plugin version
          if (data.buildNumber) {
            currentBuildNumber = parseInt(data.buildNumber, 10);
          }
          console.log('[Update] Current plugin version:', currentPluginVersion, 'build:', currentBuildNumber);
          // Start checking GitHub for updates
          checkGitHubForUpdates();
          // Check every 10 seconds
          if (!updateCheckInterval) {
            updateCheckInterval = setInterval(checkGitHubForUpdates, 10000);
          }
        }
      })
      .catch(err => {
        console.log('[Update] Failed to get plugin version:', err.message);
      });
  }

  function checkGitHubForUpdates() {
    // Check if dismissed (10 minute cooldown)
    if (Date.now() < updateDismissedUntil) return;

    // Fetch build-info.txt from GitHub releases
    fetch('https://raw.githubusercontent.com/Midnwave/ModereX/main/releases/build-info.txt?_=' + Date.now())
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch');
        return res.text();
      })
      .then(text => {
        // Parse build-info.txt (format: timestamp, Version: X, Build: Y, Commit: Z)
        const lines = text.split('\n');
        let version = null;
        let buildNumber = null;
        let buildDate = null;

        for (const line of lines) {
          const trimmed = line.trim();
          // First line is timestamp
          if (trimmed.match(/^\d{4}-\d{2}-\d{2}T/)) {
            buildDate = trimmed;
          } else if (trimmed.startsWith('Version:')) {
            version = trimmed.substring(8).trim();
          } else if (trimmed.startsWith('Build:')) {
            buildNumber = parseInt(trimmed.substring(6).trim(), 10);
          }
        }

        console.log('[Update] GitHub build info:', { version, buildNumber, buildDate });
        console.log('[Update] Current version:', currentPluginVersion, 'Current build:', currentBuildNumber);

        // Compare build numbers if available, otherwise fall back to version string
        const hasUpdate = buildNumber && currentBuildNumber
          ? buildNumber > currentBuildNumber
          : (version && version !== currentPluginVersion);

        if (hasUpdate) {
          latestGitHubVersion = version;
          showPluginUpdateBanner(version, buildDate, buildNumber);
        }
      })
      .catch(err => {
        console.log('[Update] Failed to check GitHub:', err.message);
      });
  }

  function showPluginUpdateBanner(newVersion, buildDate, buildNumber) {
    const banner = document.getElementById('updateBanner');
    if (!banner) return;

    const titleEl = banner.querySelector('.update-title');
    const versionEl = banner.querySelector('.update-version');
    const notesEl = banner.querySelector('.update-notes');

    if (titleEl) titleEl.textContent = 'Plugin Update Available';
    if (versionEl) {
      let versionText = newVersion || 'New Version';
      if (buildNumber) versionText += ` (Build ${buildNumber})`;
      if (buildDate) versionText += ' • ' + buildDate.split('T')[0]; // Just the date part
      versionEl.textContent = versionText;
    }
    if (notesEl) notesEl.textContent = 'New version available on GitHub';

    // Reset the update button in case it was disabled from a previous attempt
    const btn = banner.querySelector('.btn.primary');
    if (btn) {
      btn.disabled = false;
      btn.innerHTML = '<i class="fa-solid fa-download"></i> Update Now';
    }

    banner.classList.add('show');
  }

  function dismissUpdateBanner() {
    const banner = document.getElementById('updateBanner');
    if (banner) banner.classList.remove('show');
    // Dismiss for 10 minutes
    updateDismissedUntil = Date.now() + (10 * 60 * 1000);
    console.log('[Update] Update banner dismissed for 10 minutes');
  }

  function applyPanelUpdate() {
    // Trigger plugin update via WebSocket
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      toast('info', 'Updating Plugin', 'Downloading latest version from GitHub...');
      ws.send('TRIGGER_PLUGIN_UPDATE', {});

      // Disable the button to prevent double-clicks
      const banner = document.getElementById('updateBanner');
      if (banner) {
        const btn = banner.querySelector('.btn.primary');
        if (btn) {
          btn.disabled = true;
          btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Downloading...';
        }
      }
    } else {
      toast('error', 'Not Connected', 'Cannot update - not connected to server');
    }
  }

  window.dismissUpdateBanner = dismissUpdateBanner;
  window.applyPanelUpdate = applyPanelUpdate;
  window.loadPanelVersion = loadPanelVersion;
  window.loadCurrentPluginVersion = loadCurrentPluginVersion;
  window.checkGitHubForUpdates = checkGitHubForUpdates;

  // ===== PROFILE DROPDOWN =====
  function toggleProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    const profile = document.getElementById('topProfile');
    if (!dropdown || !profile) return;

    const isOpen = dropdown.classList.contains('show');
    if (isOpen) {
      closeProfileDropdown();
    } else {
      dropdown.classList.add('show');
      profile.classList.add('open');
      // Close when clicking outside
      setTimeout(() => {
        document.addEventListener('click', handleProfileClickOutside);
      }, 0);
    }
  }

  function closeProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    const profile = document.getElementById('topProfile');
    if (dropdown) dropdown.classList.remove('show');
    if (profile) profile.classList.remove('open');
    document.removeEventListener('click', handleProfileClickOutside);
  }

  function handleProfileClickOutside(e) {
    const profile = document.getElementById('topProfile');
    if (profile && !profile.contains(e.target)) {
      closeProfileDropdown();
    }
  }

  function logout() {
    closeProfileDropdown();
    state.authenticated = false;
    state.currentUser = null;
    state.staffName = '';
    state.notifications = [];

    // Disconnect WebSocket
    const ws = window.MX?.ws;
    if (ws) ws.disconnect();

    // Clear saved auth
    try {
      localStorage.removeItem('mx_auth');
      localStorage.removeItem('mx_token');
    } catch (e) {}

    // Reload to show auth screen
    window.location.reload();
  }

  // ===== NOTIFICATIONS =====
  function updateNotificationCount() {
    const countEl = document.getElementById('notificationCount');
    if (!countEl) return;

    const count = (state.notifications || []).filter(n => !n.read).length;
    if (count > 0) {
      countEl.textContent = count > 99 ? '99+' : count;
      countEl.style.display = 'flex';
    } else {
      countEl.style.display = 'none';
    }
  }

  function openNotificationsPanel() {
    closeProfileDropdown();
    // For now, just show a toast - notifications panel can be added later
    toast('info', 'Notifications', `You have ${(state.notifications || []).filter(n => !n.read).length} unread notifications`);
  }

  function addNotification(notification) {
    if (!state.notifications) state.notifications = [];
    state.notifications.unshift({
      id: uid('notif'),
      ...notification,
      read: false,
      timestamp: now()
    });
    // Keep only last 50 notifications
    if (state.notifications.length > 50) {
      state.notifications = state.notifications.slice(0, 50);
    }
    updateNotificationCount();
  }

  window.toggleProfileDropdown = toggleProfileDropdown;
  window.closeProfileDropdown = closeProfileDropdown;
  window.logout = logout;
  window.openNotificationsPanel = openNotificationsPanel;
  window.addNotification = addNotification;
  window.updateNotificationCount = updateNotificationCount;

  // ===== CHANGELOG SYSTEM =====
  let changelogState = {
    unreadChangelogs: [],
    currentIndex: 0,
    readBuilds: []
  };

  /**
   * Show the changelog modal if there are unread changelogs
   */
  window.showChangelogModal = function() {
    const changelogs = window.MX_CHANGELOGS || [];
    const readBuilds = changelogState.readBuilds || [];
    const unread = changelogs.filter(log => !readBuilds.includes(log.build));

    if (unread.length === 0) {
      if (window.MX?.debug) console.log('[Changelog] No unread changelogs');
      return;
    }

    changelogState.unreadChangelogs = unread;
    changelogState.currentIndex = 0;
    renderChangelogModal();
  };

  /**
   * Render the changelog modal for the current changelog
   */
  function renderChangelogModal() {
    const { unreadChangelogs, currentIndex } = changelogState;
    if (!unreadChangelogs.length) return;

    const log = unreadChangelogs[currentIndex];
    const isMultiple = unreadChangelogs.length > 1;
    const isLast = currentIndex === unreadChangelogs.length - 1;
    const isFirst = currentIndex === 0;

    // Remove existing modal
    const existing = document.getElementById('changelogOverlay');
    if (existing) existing.remove();

    // Section icon map
    const sectionIcons = {
      new: 'fa-sparkles',
      improved: 'fa-arrow-up',
      fixed: 'fa-wrench',
      technical: 'fa-code',
      permissions: 'fa-key',
      config: 'fa-sliders'
    };

    // Build sections HTML
    const sectionsHtml = log.sections.map(section => `
      <div class="changelog-section">
        <div class="changelog-section-header">
          <div class="changelog-section-icon ${section.type}">
            <i class="fa-solid ${sectionIcons[section.type] || 'fa-circle'}"></i>
          </div>
          <h3>${escapeHtml(section.title)}</h3>
        </div>
        <ul class="changelog-items">
          ${section.items.map(item => `<li>${parseMarkdownBold(escapeHtml(item))}</li>`).join('')}
        </ul>
      </div>
    `).join('');

    // Pagination dots
    const dotsHtml = isMultiple ? `
      <div class="changelog-pagination">
        <span>${currentIndex + 1} of ${unreadChangelogs.length}</span>
        <div class="changelog-pagination-dots">
          ${unreadChangelogs.map((_, i) => `
            <div class="changelog-pagination-dot ${i === currentIndex ? 'active' : ''}"
                 onclick="window.goToChangelog(${i})"></div>
          `).join('')}
        </div>
      </div>
    ` : '<div></div>';

    // Navigation buttons
    const navHtml = isMultiple ? `
      <button class="changelog-nav-btn" onclick="window.prevChangelog()" ${isFirst ? 'disabled' : ''}>
        <i class="fa-solid fa-chevron-left"></i>
      </button>
      <button class="changelog-nav-btn" onclick="window.nextChangelog()" ${isLast ? 'disabled' : ''}>
        <i class="fa-solid fa-chevron-right"></i>
      </button>
    ` : '';

    const overlay = document.createElement('div');
    overlay.id = 'changelogOverlay';
    overlay.className = 'changelog-overlay';
    overlay.innerHTML = `
      <div class="changelog-modal">
        <div class="changelog-header">
          <div class="changelog-header-icon">
            <img src="537154108207028818e303ef9465c1f66717660d_96.png" alt="ModereX" class="changelog-logo">
          </div>
          <div class="changelog-header-text">
            <h2>${escapeHtml(log.title)}</h2>
            <div class="changelog-meta">
              <span class="changelog-version">v${escapeHtml(log.version)}</span>
              <span class="changelog-date">${escapeHtml(log.date)}</span>
            </div>
          </div>
        </div>
        <div class="changelog-body">
          ${sectionsHtml}
        </div>
        <div class="changelog-footer">
          ${dotsHtml}
          <div class="changelog-actions">
            ${navHtml}
            <button class="changelog-btn changelog-btn-primary" onclick="window.markChangelogRead()">
              ${isLast ? 'Got it!' : 'Mark as Read'}
            </button>
          </div>
        </div>
      </div>
    `;

    document.body.appendChild(overlay);
  }

  /**
   * Parse markdown bold (**text**) to HTML
   */
  function parseMarkdownBold(text) {
    return text.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
               .replace(/`([^`]+)`/g, '<code>$1</code>');
  }

  /**
   * Go to specific changelog by index
   */
  window.goToChangelog = function(index) {
    changelogState.currentIndex = index;
    renderChangelogModal();
  };

  /**
   * Go to previous changelog
   */
  window.prevChangelog = function() {
    if (changelogState.currentIndex > 0) {
      changelogState.currentIndex--;
      renderChangelogModal();
    }
  };

  /**
   * Go to next changelog
   */
  window.nextChangelog = function() {
    if (changelogState.currentIndex < changelogState.unreadChangelogs.length - 1) {
      changelogState.currentIndex++;
      renderChangelogModal();
    }
  };

  /**
   * Mark current changelog as read and move to next or close
   */
  window.markChangelogRead = function() {
    const { unreadChangelogs, currentIndex } = changelogState;
    const log = unreadChangelogs[currentIndex];

    // Send to server to mark as read
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('MARK_CHANGELOG_READ', { build: log.build });
      window.debugLog('CHANGELOG', `Marked build ${log.build} as read`, 'info');
    }

    // Add to local read list
    if (!changelogState.readBuilds.includes(log.build)) {
      changelogState.readBuilds.push(log.build);
    }

    // If last changelog, close modal
    if (currentIndex >= unreadChangelogs.length - 1) {
      closeChangelogModal();
    } else {
      // Move to next
      changelogState.currentIndex++;
      renderChangelogModal();
    }
  };

  /**
   * Close the changelog modal
   */
  window.closeChangelogModal = function() {
    const overlay = document.getElementById('changelogOverlay');
    if (overlay) {
      overlay.style.animation = 'changelogFadeIn 0.2s ease reverse';
      setTimeout(() => overlay.remove(), 200);
    }
  };
  const closeChangelogModal = window.closeChangelogModal;

  /**
   * Set read builds from server
   */
  window.setReadChangelogs = function(builds) {
    changelogState.readBuilds = builds || [];
    if (window.MX?.debug) {
      console.log('[Changelog] Read builds:', builds);
    }
  };

  /**
   * Check and show changelog after login
   */
  window.checkUnreadChangelogs = function() {
    setTimeout(() => {
      if (changelogState.readBuilds !== undefined) {
        window.showChangelogModal();
      }
    }, 1500); // Delay to let UI settle
  };

  // ===== INITIALIZATION =====
  document.addEventListener('DOMContentLoaded', () => {
    ui.initDom();
    if (!loadState()) initializeState();
    setupEventListeners();
    setupWebSocketHandlers();
    setupStatusIndicator();
    wrapWithWebSocket();
    setupBackgroundAnimation();
    startClock();
    startDurationCountdown();
    ui.refreshUnsavedUI();
    go('dashboard');
    applyMySettingsUI();
    applyThemeFromState();
    applyDevModeUI();
    setInterval(saveState, 4000);
    window.addEventListener('beforeunload', saveState);

    // Load panel version from server
    setTimeout(loadPanelVersion, 1000);

    // Start GitHub update checker after 3 seconds (to let WebSocket connect first)
    setTimeout(loadCurrentPluginVersion, 3000);

    // Setup profile dropdown click handler
    const topProfile = document.getElementById('topProfile');
    if (topProfile) {
      topProfile.addEventListener('click', (e) => {
        // Don't toggle if clicking on dropdown item
        if (e.target.closest('.profile-dropdown-item')) return;
        toggleProfileDropdown();
      });
    }
  });

  // Setup status indicator handlers
  function setupStatusIndicator() {
    const ws = window.MX?.ws;
    if (!ws) return;

    ws.on('ping_update', (data) => {
      updateStatusIndicator('connected', data.ping);
    });

    ws.on('status_change', (data) => {
      updateStatusIndicator(data.status, data.ping);
    });
  }
})();

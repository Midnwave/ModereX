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
    if (page === 'anticheat') ui.renderAnticheat();
    if (page === 'templates') ui.renderTemplates();
    if (page === 'messages') ui.renderMessages();
    if (page === 'settings') ui.renderChatToggles();
    if (page === 'status' && window.initServerStatus) window.initServerStatus();
    if (page === 'replay' && window.initReplayViewer) window.initReplayViewer();
  };

  // ===== TOASTS =====
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
    dom().toastContainer.appendChild(el);
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
  window.openDrawer = function(playerId, highlightPunId = null) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    state.selectedPlayerId = playerId;
    state.selectedPunishmentId = highlightPunId;

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
      return `
        <div class="drawer-row" style="cursor:pointer" onclick="viewPunishmentDetails('${x.id}')">
          <div class="meta"><b>${escapeHtml(x.type)} | ${escapeHtml(x.duration || 'instant')}</b><small>${escapeHtml(x.reason || 'No reason')}<br>Case: <span style="font-family:var(--font-mono)">${escapeHtml(x.id)}</span> | by ${escapeHtml(x.staff)}</small></div>
          <div class="drawer-actions">
            <span class="badge ${badgeClass}"><i class="fa-solid fa-file-lines"></i></span>
            <button class="mini bad" onclick="event.stopPropagation(); revokePunishmentConfirm('${x.id}')"><i class="fa-solid fa-xmark"></i></button>
          </div>
        </div>
      `;
    }).join('') : `<div class="drawer-row"><div class="meta"><small>No active punishments.</small></div></div>`;

    const violations = state.punishments.filter(x => x.playerId === p.id && (!x.active || x.revoked)).sort((a, b) => b.createdAt - a.createdAt);
    dom().drawerViolations.innerHTML = violations.length ? violations.slice(0, 8).map(v => `
      <div class="drawer-row" style="cursor:pointer" onclick="viewPunishmentDetails('${v.id}')">
        <div class="meta"><b>${escapeHtml(v.type)} | <span style="font-family:var(--font-mono)">${escapeHtml(v.id)}</span></b><small>${escapeHtml(fmtLong(v.createdAt))} | ${escapeHtml(v.reason || 'No reason')}</small></div>
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

    dom().drawerIps.innerHTML = `<div class="drawer-row"><div class="meta"><b>Current IP</b><small><span class="ip-blur">${escapeHtml(p.ip)}</span></small></div></div>`;
    const recentCmds = (p.recentCommands || []).slice(-10).reverse();
    dom().drawerRecent.innerHTML = recentCmds.length ? `
      ${recentCmds.map(item => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(item.cmd || item)}</b></div></div>`).join('')}
      <div class="drawer-row">
        <div class="meta"><small>${p.recentCommands.length} total commands</small></div>
        <button class="mini" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
      </div>
    ` : `<div class="drawer-row"><div class="meta"><small>No commands.</small></div></div>`;

    const automodLogs = state.logs.filter(l => l.kind === 'automod' && l.playerId === p.id).slice(-6).reverse();
    dom().drawerAutomod.innerHTML = automodLogs.length ? `
      ${automodLogs.map(l => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(l.title)}</b><small>${escapeHtml(fmtShort(l.t))} | ${escapeHtml(l.detail)}</small></div></div>`).join('')}
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
            <div><b>Reason:</b> ${escapeHtml(pun.reason || 'No reason')}</div>
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

    const canRevoke = !pun.revoked;
    dom().detailsActions.innerHTML = `
      ${canRevoke ? `<button class="btn bad" onclick="revokePunishmentConfirm('${pun.id}')"><i class="fa-solid fa-xmark"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : ''}
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
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-terminal" style="color:var(--primary-light)"></i>
            <b>Command History</b>
          </div>
          <button class="mini" id="cmdClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="cmdSearch" placeholder="Search commands...">
          </div>
          <div class="card" style="margin-top:14px" id="cmdList"></div>
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
    $('#cmdClose', overlay).onclick = close;
    $('#cmdCloseBtn', overlay).onclick = close;
    const listEl = $('#cmdList', overlay);
    const searchEl = $('#cmdSearch', overlay);
    const render = () => {
      const allCmds = (p.recentCommands || []).map(item => {
        if (typeof item === 'string') return { cmd: item, t: p.lastSeen || now() };
        return item;
      }).sort((a, b) => (b.t || 0) - (a.t || 0));
      const q = (searchEl.value || '').trim().toLowerCase();
      const filtered = allCmds.filter(item => !q || (item.cmd || '').toLowerCase().includes(q));
      listEl.innerHTML = filtered.length ? filtered.map(cmd => `
        <div class="drawer-row" data-player-id="${p.id}"><div class="meta"><b>${escapeHtml(cmd.cmd)}</b><small>${escapeHtml(fmtLong(cmd.t))}</small></div></div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No commands found.</small></div></div>`;
    };
    const refreshTimer = setInterval(render, 3000);
    const closeWithCleanup = () => { clearInterval(refreshTimer); close(); };
    $('#cmdClose', overlay).onclick = closeWithCleanup;
    $('#cmdCloseBtn', overlay).onclick = closeWithCleanup;
    searchEl.addEventListener('input', render);
    render();
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
          <div class="card" style="margin-top:14px" id="chatList"></div>
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
    const allLogs = state.logs.filter(l => l.channel === 'chat' && (l.playerId === p.id || (l.title || '').includes(p.name)));
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
        <div class="drawer-row" data-player-id="${p.id}"><div class="meta"><b>${escapeHtml(fmtLong(l.t))}</b><small>${escapeHtml(l.detail)}</small></div></div>
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
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-robot" style="color:var(--primary-light)"></i>
            <b>Automod Logs</b>
          </div>
          <button class="mini" id="autoClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="toolbar" style="margin-top:0">
            <div class="left" style="gap:10px">
              <div class="gsearch" style="width:100%;max-width:420px">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="text" id="autoSearch" placeholder="Search automod logs...">
              </div>
              <input type="date" class="input" id="autoFrom" style="max-width:160px">
              <input type="date" class="input" id="autoTo" style="max-width:160px">
            </div>
          </div>
          <div class="card" style="margin-top:14px" id="autoList"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="autoCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#autoClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#autoCloseBtn', overlay).onclick = () => closeOverlayAnimated(overlay);
    const listEl = $('#autoList', overlay);
    const searchEl = $('#autoSearch', overlay);
    const fromEl = $('#autoFrom', overlay);
    const toEl = $('#autoTo', overlay);
    const allLogs = state.logs.filter(l => l.kind === 'automod' && l.playerId === p.id);
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
        <div class="drawer-row" data-player-id="${p.id}"><div class="meta"><b>${escapeHtml(fmtLong(l.t))}</b><small>${escapeHtml(l.title)} | ${escapeHtml(l.detail)}</small></div></div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No automod logs found.</small></div></div>`;
    };
    searchEl.addEventListener('input', render);
    fromEl.addEventListener('change', render);
    toEl.addEventListener('change', render);
    render();
  };

  // ===== RULES =====
  window.addRuleUI = function() {
    state.rules.unshift({ id: uid('rule'), name: `New Rule ${state.rules.length + 1}`, enabled: true, block: true, conditions: [{ kind: 'contains', value: '', match: 'contains' }], action: { kind: 'none', extra: '' }, threshold: { hits: 1, windowMins: 10 }, notes: 'Configure conditions' });
    ui.markUnsaved('rules', true);
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
    if (r) { r.enabled = !r.enabled; ui.markUnsaved('rules', true); ui.renderRules(); }
  };

  window.addCondition = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.conditions.push({ kind: 'contains', value: '', match: 'contains' }); ui.markUnsaved('rules', true); ui.renderRules(); }
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
      r.action.kind = kind;
      if (kind === 'none') { r.action.extra = ''; r.action.duration = ''; }
      ui.markUnsaved('rules', true);
      ui.renderRules();
    }
  };

  window.setRuleActionExtra = function(ruleId, extra) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.action.extra = extra; ui.markUnsaved('rules', true); }
  };

  window.setRuleActionDuration = function(ruleId, duration) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.action.duration = duration; ui.markUnsaved('rules', true); }
  };

  window.setRuleName = function(ruleId, name) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r && !r.locked) { r.name = name; ui.markUnsaved('rules', true); }
  };

  window.toggleRuleBlock = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.block = !r.block; ui.markUnsaved('rules', true); ui.renderRules(); }
  };

  window.setRuleThreshold = function(ruleId, field, v) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.threshold[field] = Math.max(1, parseInt(v || '1', 10)); ui.markUnsaved('rules', true); }
  };

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
    for (const r of enabledRules) {
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
          if (total > 0 && (upper / total) * 100 >= parseInt(c.value)) triggered = true;
        }
        if (c.kind === 'link' && /https?:\/\//i.test(msg)) triggered = true;
        if (c.kind === 'regex' && c.value) {
          try { if (new RegExp(c.value, 'i').test(msg)) triggered = true; } catch {}
        }
        if (c.kind === 'repeat') {
          triggered = checkRepeat(r, msg, 'test');
        }
        if (triggered) { hits.push(r); break; }
      }
    }

    dom().testResult.innerHTML = hits.length ?
      `<span class="badge red"><i class="fa-solid fa-circle-xmark"></i> TRIGGERED</span><div style="margin-top:10px">${hits.map(h => `<div><b>${escapeHtml(h.name)}</b> - Action: ${escapeHtml(h.action.kind)}</div>`).join('')}</div>` :
      `<span class="badge green"><i class="fa-solid fa-check"></i> PASSED</span><div style="margin-top:10px">No rules triggered (tested ${enabledRules.length} rules).</div>`;
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
      toast('warn', 'Delivery Failed', 'Webhook request failed. Check URL or CORS.');
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
      }).catch(() => {
        setPublishLoading(false);
        toast('error', 'Error', 'Failed to publish some changes.');
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

    function draw() {
      ctx.clearRect(0, 0, w, h);
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
            ctx.strokeStyle = `rgba(90, 156, 255, ${alpha})`;
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
        ctx.fillStyle = `rgba(180, 210, 255, ${glow})`;
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

  // Status indicator updates
  function updateStatusIndicator(status, ping) {
    const statusChip = document.getElementById('statusChip');
    const pingText = document.getElementById('pingText');

    if (!statusChip || !pingText) return;

    if (status === 'connected') {
      statusChip.className = 'chip ok';
      pingText.textContent = ping > 0 ? ping : '--';
    } else if (status === 'saving') {
      statusChip.className = 'chip warn';
      pingText.textContent = ping > 0 ? ping : '--';
    } else {
      statusChip.className = 'chip bad';
      pingText.textContent = '--';
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

  // Settings that can be searched
  const searchableSettings = [
    { name: 'Sound Effects', page: 'mysettings', keywords: ['audio', 'notification', 'mute'] },
    { name: 'Auto Sign-in', page: 'mysettings', keywords: ['device', 'trust', 'remember'] },
    { name: 'Watchlist Alerts', page: 'mysettings', keywords: ['monitor', 'notify'] },
    { name: 'Anticheat Alerts', page: 'mysettings', keywords: ['hack', 'cheat'] },
    { name: 'Chat Lock', page: 'actions', keywords: ['disable', 'mute all'] },
    { name: 'Slowmode', page: 'actions', keywords: ['rate limit', 'spam'] },
    { name: 'Kick All', page: 'actions', keywords: ['clear', 'server'] }
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
        toast('info', 'Template', result.title);
        break;
      case 'page':
        go(result.id);
        break;
      case 'setting':
        go(result.data.page);
        toast('info', 'Setting', result.title);
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
        connectedAt: now()
      };
      state.staffName = session.playerName || session.username;
      ui.renderTopUser();

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
        recentCommands: [],
        notes: ''
      }));
      ui.renderPlayers();
      ui.renderDashboard();
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
      state.rules = state.rules.filter(r => r.id !== data.id);
      ui.renderRules();
      toast('info', 'Automod', 'Rule deleted');
    });

    // Handle user settings
    ws.on('USER_SETTINGS_DATA', (data) => {
      if (!isLiveMode) return;
      state.settings.watchToasts = data.watchlistToasts ?? true;
      state.settings.soundEnabled = data.soundEnabled ?? true;
      state.settings.deviceTrustEnabled = data.deviceTrustEnabled ?? false;

      // Apply sound settings globally
      if (window.MX.sounds) {
        window.MX.sounds.setEnabled(data.soundEnabled ?? true);
      }

      // Update all UI elements that depend on these settings
      updateSettingsUI();
      ui.renderWatchToastsToggle();
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
      toast('info', 'Punishment', `${data.playerName} was ${data.type.toLowerCase()}ed by ${data.staffName}`);
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
      toast('info', 'Revoked', `Punishment ${data.caseId} was revoked`);
    });

    ws.on('WATCHLIST_ALERT', (data) => {
      if (!isLiveMode) return;
      state.watchAlerts.push({ type: data.type, details: data.details, t: now() });

      const settings = loadMySettings();
      const style = settings.watchlistStyle || 'bar';
      const playerData = { playerId: data.playerUuid };

      if (style === 'bar' || style === 'both') {
        showAlertBar('watchlist', 'Watchlist Alert', data.details, playerData);
      }
      if (style === 'toast' || style === 'both') {
        toast('warn', 'Watchlist', data.details, playerData);
      }

      ui.renderDashboard();
      window.MX.sounds?.watchlist();
    });

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
      logEvent('INFO', 'system', 'Player Join', `${data.name} joined the server`, { kind: 'system' });
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
      logEvent('INFO', 'system', 'Player Quit', `${data.name} left the server`, { kind: 'system' });
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
    });

    ws.on('AUTOMOD_TRIGGER', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      toast('warn', 'Automod', `${data.playerName} triggered ${data.rule}`);
    });

    ws.on('AUTOMOD_TRIGGERED', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      toast('warn', 'Automod', `${data.playerName} triggered ${data.rule}`);
    });

    ws.on('ANTICHEAT_ALERT', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.name === data.playerName);
      const settings = loadMySettings();

      // Always log the event
      logEvent('WARN', 'anticheat', `Anticheat | ${data.check}`, `${data.playerName} (VL: ${data.vl})`, { playerId: player?.id, kind: 'anticheat' });

      // Check user's anticheat mode setting
      const mode = settings.anticheatMode || 'watchlist';
      if (mode === 'off') return;

      // Check if player is on watchlist (for watchlist-only mode)
      const isWatchlisted = player && state.watchlist?.has(player.id);
      if (mode === 'watchlist' && !isWatchlisted) return;

      // Show alert based on user's preferred style
      const style = settings.watchlistStyle || 'bar';
      const title = `Anticheat: ${data.check}`;
      const sub = `${data.playerName} - VL: ${data.vl}`;
      const playerData = { playerId: player?.id };

      if (style === 'bar' || style === 'both') {
        showAlertBar('anticheat', title, sub, playerData);
      }
      if (style === 'toast' || style === 'both') {
        toast('warn', title, sub, playerData);
      }

      window.MX.sounds?.anticheat();
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

  function showDisconnect(serverName) {
    serverNameForDisconnect = serverName || 'Server';
    const overlay = document.getElementById('disconnectOverlay');
    const nameEl = document.getElementById('disconnectServerName');
    if (nameEl) nameEl.textContent = serverNameForDisconnect;
    if (overlay) overlay.classList.add('show');
    window.MX.sounds?.disconnect();
  }

  function hideDisconnect() {
    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function attemptReconnect() {
    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
    window.MX.sounds?.reconnecting();
    if (window.MX.auth?.reconnect) {
      window.MX.auth.reconnect();
    } else {
      location.reload();
    }
  }

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
    const titleEl = document.getElementById('alertBarTitle');
    const subEl = document.getElementById('alertBarSub');

    if (content) content.className = 'alertBar-content' + (type === 'anticheat' ? ' anticheat' : '');
    if (icon) icon.innerHTML = type === 'anticheat'
      ? '<i class="fa-solid fa-shield-halved"></i>'
      : '<i class="fa-solid fa-eye"></i>';
    if (titleEl) titleEl.textContent = title;
    if (subEl) subEl.textContent = sub;
    if (bar) bar.classList.add('show');

    window.MX.sounds?.alertBar();

    if (alertBarTimeout) clearTimeout(alertBarTimeout);
    alertBarTimeout = setTimeout(dismissAlertBar, 10000);
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
  window.setVolume = setVolume;
  window.showDisconnect = showDisconnect;
  window.hideDisconnect = hideDisconnect;

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
    setInterval(saveState, 4000);
    window.addEventListener('beforeunload', saveState);
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

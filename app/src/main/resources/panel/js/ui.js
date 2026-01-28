/* ============================================
   ModereX Control Panel - UI Rendering
   ============================================ */
(function() {
  const { $, $$, escapeHtml, fmtShort, fmtLong, fmtClock, avatarUrl } = window.MX.utils;
  const state = window.MX.state;

  // Format remaining time in short form (e.g., "2d 5h", "30m 15s")
  function formatRemainingTime(ms) {
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

  // Truncate text with ellipsis
  function truncate(text, maxLen = 40) {
    if (!text) return '';
    text = String(text);
    return text.length > maxLen ? text.substring(0, maxLen) + '...' : text;
  }

  // Pagination helper - renders pagination controls
  function renderPagination(stateKey, currentPage, totalPages, totalItems, pageSize, targetId) {
    const container = document.getElementById(targetId + 'Pagination');
    if (!container) return;

    const startItem = (currentPage - 1) * pageSize + 1;
    const endItem = Math.min(currentPage * pageSize, totalItems);

    // Page size options
    const pageSizes = [10, 25, 50, 100];
    const pageSizeOptions = pageSizes.map(size =>
      `<div class="pag-dropdown-item ${size === pageSize ? 'active' : ''}" onclick="setPaginationSize('${stateKey}', ${size})">${size}</div>`
    ).join('');

    // Generate page numbers with ellipsis
    let pageNumbers = '';
    const maxVisible = 5;
    let pages = [];

    if (totalPages <= maxVisible + 2) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      if (currentPage > 3) pages.push('...');
      for (let i = Math.max(2, currentPage - 1); i <= Math.min(totalPages - 1, currentPage + 1); i++) {
        pages.push(i);
      }
      if (currentPage < totalPages - 2) pages.push('...');
      pages.push(totalPages);
    }

    pageNumbers = pages.map(p => {
      if (p === '...') return '<span class="pag-ellipsis">...</span>';
      return `<button class="pag-num ${p === currentPage ? 'active' : ''}" onclick="setPaginationPage('${stateKey}', ${p})">${p}</button>`;
    }).join('');

    container.innerHTML = `
      <div class="pagination-controls">
        <div class="pag-info">
          Showing <b>${startItem}</b>-<b>${endItem}</b> of <b>${totalItems}</b>
        </div>
        <div class="pag-size">
          <span>Per page:</span>
          <div class="pag-dropdown" onclick="this.classList.toggle('open')">
            <span>${pageSize}</span>
            <i class="fa-solid fa-chevron-down"></i>
            <div class="pag-dropdown-menu">${pageSizeOptions}</div>
          </div>
        </div>
        <div class="pag-nav">
          <button class="pag-btn" onclick="setPaginationPage('${stateKey}', 1)" ${currentPage === 1 ? 'disabled' : ''} title="First">
            <i class="fa-solid fa-angles-left"></i>
          </button>
          <button class="pag-btn" onclick="setPaginationPage('${stateKey}', ${currentPage - 1})" ${currentPage === 1 ? 'disabled' : ''} title="Previous">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          ${pageNumbers}
          <button class="pag-btn" onclick="setPaginationPage('${stateKey}', ${currentPage + 1})" ${currentPage === totalPages ? 'disabled' : ''} title="Next">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
          <button class="pag-btn" onclick="setPaginationPage('${stateKey}', ${totalPages})" ${currentPage === totalPages ? 'disabled' : ''} title="Last">
            <i class="fa-solid fa-angles-right"></i>
          </button>
        </div>
      </div>
    `;
  }

  // Pagination navigation functions (exposed globally)
  window.setPaginationPage = function(stateKey, page) {
    if (state[stateKey]) {
      state[stateKey].page = page;
      if (stateKey === 'punishPagination') window.MX.ui.renderPunishments();
      else if (stateKey === 'playerPagination') window.MX.ui.renderPlayers();
    }
  };

  window.setPaginationSize = function(stateKey, size) {
    if (state[stateKey]) {
      state[stateKey].pageSize = size;
      state[stateKey].page = 1; // Reset to first page
      if (stateKey === 'punishPagination') window.MX.ui.renderPunishments();
      else if (stateKey === 'playerPagination') window.MX.ui.renderPlayers();
    }
  };

  // DOM References
  let dom = {};

  function initDom() {
    dom = {
      authOverlay: $('#authOverlay'),
      authToken: $('#authToken'),
      globalSearch: $('#globalSearch'),
      timeChip: $('#timeChip'),
      unsavedChip: $('#unsavedChip'),
      publishBtn: $('#publishBtn'),
      statActivePun: $('#statActivePun'),
      statPunHint: $('#statPunHint'),
      statAuto: $('#statAuto'),
      statAutoHint: $('#statAutoHint'),
      statWatch: $('#statWatch'),
      statWatchHint: $('#statWatchHint'),
      statOnline: $('#statOnline'),
      statOnlineHint: $('#statOnlineHint'),
      statBans: $('#statBans'),
      statMutes: $('#statMutes'),
      statWarns24h: $('#statWarns24h'),
      statStaff: $('#statStaff'),
      dashServerStatus: $('#dashServerStatus'),
      dashUptime: $('#dashUptime'),
      recentPunishments: $('#recentPunishments'),
      activityRows: $('#activityRows'),
      watchlistHighlights: $('#watchlistHighlights'),
      playerSearch: $('#playerSearch'),
      playerRows: $('#playerRows'),
      playersOnlineChip: $('#playersOnlineChip'),
      punishSearch: $('#punishSearch'),
      punishRows: $('#punishRows'),
      templateSearch: $('#templateSearch'),
      templateRows: $('#templateRows'),
      rulesList: $('#rulesList'),
      testChannel: $('#testChannel'),
      testMessage: $('#testMessage'),
      testResult: $('#testResult'),
      watchSearch: $('#watchSearch'),
      watchPlayers: $('#watchPlayers'),
      watchAlerts: $('#watchAlerts'),
      watchToastsToggle: $('#watchToastsToggle'),
      watchToastsHint: $('#watchToastsHint'),
      warnNotifyToggle: $('#warnNotifyToggle'),
      warnNotifyHint: $('#warnNotifyHint'),
      warnEscalateToggle: $('#warnEscalateToggle'),
      warnEscalateHint: $('#warnEscalateHint'),
      muteChatToggle: $('#muteChatToggle'),
      muteMsgToggle: $('#muteMsgToggle'),
      muteSignsToggle: $('#muteSignsToggle'),
      muteBooksToggle: $('#muteBooksToggle'),
      muteBroadcastToggle: $('#muteBroadcastToggle'),
      muteVoiceToggle: $('#muteVoiceToggle'),
      muteVoiceJoinToggle: $('#muteVoiceJoinToggle'),
      anticheatReplaceToggle: $('#anticheatReplaceToggle'),
      anticheatReplaceHint: $('#anticheatReplaceHint'),
      langSelect: $('#langSelect'),
      msgSearch: $('#msgSearch'),
      msgRows: $('#msgRows'),
      banPreview: $('#banPreview'),
      togChat: $('#togChat'),
      togChatHint: $('#togChatHint'),
      togSlow: $('#togSlow'),
      togSlowHint: $('#togSlowHint'),
      slowSeconds: $('#slowSeconds'),
      discordWebhook: $('#discordWebhook'),
      webhookToggles: $('#webhookToggles'),
      voiceChatStatus: $('#voiceChatStatus'),
      luckPermsStatus: $('#luckPermsStatus'),
      anticheatList: $('#anticheatList'),
      webhookPreview: $('#webhookPreview'),
      logsBox: $('#logsBox'),
      logsSearch: $('#logsSearch'),
      logsStatus: $('#logsStatus'),
      logsBtn: $('#logsBtn'),
      logsPageSize: $('#logsPageSize'),
      logsPrev: $('#logsPrev'),
      logsNext: $('#logsNext'),
      logsPageInfo: $('#logsPageInfo'),
      logsFilterBtn: $('#logsFilterBtn'),
      sevINFO: $('#sevINFO'),
      sevWARN: $('#sevWARN'),
      sevERROR: $('#sevERROR'),
      mxOnly: $('#mxOnly'),
      drawerOverlay: $('#drawerOverlay'),
      playerDrawer: $('#playerDrawer'),
      drawerAvatar: $('#drawerAvatar'),
      drawerName: $('#drawerName'),
      drawerMeta: $('#drawerMeta'),
      drawerActivePun: $('#drawerActivePun'),
      drawerIps: $('#drawerIps'),
      drawerRecent: $('#drawerRecent'),
      drawerAutomod: $('#drawerAutomod'),
      drawerViolations: $('#drawerViolations'),
      drawerPardons: $('#drawerPardons'),
      drawerActionBar: $('#drawerActionBar'),
      watchToggleHint: $('#watchToggleHint'),
      watchToggleBtn: $('#watchToggleBtn'),
      punishOverlay: $('#punishOverlay'),
      punishTitle: $('#punishTitle'),
      punishTarget: $('#punishTarget'),
      punishTemplate: $('#punishTemplate'),
      punishTypeSelect: $('#punishTypeSelect'),
      punishReason: $('#punishReason'),
      punishDuration: $('#punishDuration'),
      punishEvidencePick: $('#punishEvidencePick'),
      punishEvidencePreview: $('#punishEvidencePreview'),
      punishCreateOverlay: $('#punishCreateOverlay'),
      punishCreateTitle: $('#punishCreateTitle'),
      punishCreatePlayer: $('#punishCreatePlayer'),
      punishCreateList: $('#punishCreateList'),
      punishCreateType: $('#punishCreateType'),
      punishCreateTemplate: $('#punishCreateTemplate'),
      punishCreateDuration: $('#punishCreateDuration'),
      punishCreateReason: $('#punishCreateReason'),
      punishCreateEvidencePick: $('#punishCreateEvidencePick'),
      punishCreateEvidencePreview: $('#punishCreateEvidencePreview'),
      detailsOverlay: $('#detailsOverlay'),
      detailsCaseId: $('#detailsCaseId'),
      detailsBody: $('#detailsBody'),
      detailsActions: $('#detailsActions'),
      wizardOverlay: $('#wizardOverlay'),
      wizardBody: $('#wizardBody'),
      wizStepChip: $('#wizStepChip'),
      supportFab: $('#supportFab'),
      testerPanel: $('#testerPanel'),
      toastContainer: $('#toastContainer'),
      connectOverlay: $('#connectOverlay'),
      topProfile: $('#topProfile'),
      topAvatar: $('#topAvatar'),
      topName: $('#topName'),
      topPlatform: $('#topPlatform'),
      anticheatSearch: $('#anticheatSearch'),
      anticheatRows: $('#anticheatRows'),
      anticheatConfig: $('#anticheatConfig'),
      anticheatDisabledCard: $('#anticheatDisabledCard'),
      ruleSearch: $('#ruleSearch'),
      ruleTypeFilter: $('#ruleTypeFilter'),
      ruleStatusFilter: $('#ruleStatusFilter'),
      rulesCount: $('#rulesCount'),
      rulesPageInfo: $('#rulesPageInfo'),
      rulesPrevBtn: $('#rulesPrevBtn'),
      rulesNextBtn: $('#rulesNextBtn')
    };
  }

  function getDom() { return dom; }

  function renderAll() {
    refreshUnsavedUI();
    renderTopUser();
    renderDashboard();
    renderPlayers();
    renderPunishments();
    renderTemplates();
    renderRules();
    renderMessages();
    renderIntegrations();
    renderAnticheat();
    renderWatchlist();
    renderWatchToastsToggle();
    renderLogs();
    renderChatToggles();
  }

  function renderDashboard() {
    if (!dom.statActivePun) return;

    // Filter punishments
    const activePun = state.punishments.filter(p => p.active && !p.revoked);
    const activeBans = activePun.filter(p => p.type === 'BAN' || p.type === 'IPBAN');
    const activeMutes = activePun.filter(p => p.type === 'MUTE');
    const now = Date.now();
    const oneDayAgo = now - 24 * 60 * 60 * 1000;
    const recentWarns = state.punishments.filter(p => p.type === 'WARN' && p.createdAt >= oneDayAgo);

    // Primary stats
    dom.statActivePun.textContent = activePun.length;
    dom.statPunHint.textContent = activePun.length ? `${activePun.length} active` : 'No active cases';

    const auto = state.logs.filter(l => l.kind === 'automod').length;
    dom.statAuto.textContent = auto;
    dom.statAutoHint.textContent = auto ? `${auto} recent` : 'No events';

    dom.statWatch.textContent = state.watchAlerts.length;
    dom.statWatchHint.textContent = state.watchAlerts.length ? `${state.watchAlerts.length} alerts` : 'None';

    const online = state.players.filter(p => p.status === 'online' || p.status === 'afk').length;
    const staffOnline = (state.staff || []).filter(s => s.status === 'online' || s.status === 'afk').length;
    dom.statOnline.textContent = online;
    dom.statOnlineHint.textContent = `${online}/${state.players.length} total • ${staffOnline} staff`;

    // Secondary stats
    if (dom.statBans) dom.statBans.textContent = activeBans.length;
    if (dom.statMutes) dom.statMutes.textContent = activeMutes.length;
    if (dom.statWarns24h) dom.statWarns24h.textContent = recentWarns.length;
    if (dom.statStaff) dom.statStaff.textContent = staffOnline;

    // Server status
    if (dom.dashServerStatus) {
      const isConnected = window.MX?.ws?.isConnected?.() || false;
      dom.dashServerStatus.className = `badge ${isConnected ? 'green' : 'red'}`;
      dom.dashServerStatus.innerHTML = isConnected
        ? '<i class="fa-solid fa-circle"></i> Connected'
        : '<i class="fa-solid fa-circle"></i> Disconnected';
    }

    // Server uptime (if available)
    if (dom.dashUptime && state.serverInfo?.uptime) {
      dom.dashUptime.textContent = formatRemainingTime(state.serverInfo.uptime);
    }

    // Activity rows
    const rows = state.activity.slice().sort((a, b) => b.t - a.t).slice(0, 7).map(a => `
      <tr><td>${escapeHtml(fmtShort(a.t))}</td><td>${escapeHtml(a.actor)}</td><td>${escapeHtml(a.action)}</td><td>${escapeHtml(a.target)}</td></tr>
    `).join('');
    dom.activityRows.innerHTML = rows || `<tr><td colspan="4" style="color:var(--muted)">No activity recorded.</td></tr>`;

    // Recent punishments
    if (dom.recentPunishments) {
      const recentPuns = state.punishments.slice().sort((a, b) => b.createdAt - a.createdAt).slice(0, 5);
      dom.recentPunishments.innerHTML = recentPuns.map(pun => {
        const pl = state.players.find(p => p.id === pun.playerId);
        const name = pl?.name || 'Unknown';
        const avatarFallback = `https://minotar.net/helm/${encodeURIComponent(name)}/64.png`;
        const typeBadge = pun.type === 'BAN' ? `<span class="badge red"><i class="fa-solid fa-ban"></i></span>` :
          pun.type === 'MUTE' ? `<span class="badge yellow"><i class="fa-solid fa-volume-xmark"></i></span>` :
          pun.type === 'KICK' ? `<span class="badge purple"><i class="fa-solid fa-person-walking-arrow-right"></i></span>` :
          `<span class="badge blue"><i class="fa-solid fa-triangle-exclamation"></i></span>`;
        const statusBadge = pun.revoked
          ? `<span class="badge gray"><i class="fa-solid fa-xmark"></i> Revoked</span>`
          : pun.active === false
            ? `<span class="badge gray"><i class="fa-solid fa-check"></i> Expired</span>`
            : `<span class="badge green"><i class="fa-solid fa-check"></i> Active</span>`;

        return `
          <div class="drawer-row" style="border-radius:var(--radius);cursor:pointer" onclick="viewPunishmentDetails('${pun.id}')">
            <div style="display:flex;align-items:center;gap:10px">
              <div class="phead" style="width:32px;height:32px"><img src="${avatarUrl(pl || { name: name })}" alt="" onerror="this.onerror=null;this.src='${avatarFallback}'" style="width:100%;height:100%;border-radius:4px"></div>
              <div class="meta" style="flex:1">
                <b>${escapeHtml(name)}</b>
                <small>${escapeHtml(truncate(pun.reason || 'No reason', 35))} • ${escapeHtml(fmtShort(pun.createdAt))}</small>
              </div>
            </div>
            <div style="display:flex;gap:6px;align-items:center">
              ${typeBadge}
              ${statusBadge}
            </div>
          </div>
        `;
      }).join('') || `<div class="hintline">No recent punishments.</div>`;
    }

    // Match by UUID (from server) instead of internal ID
    const wlPlayers = [...state.watchlist].map(uuid =>
      state.players.find(p => p.uuid === uuid || p.id === uuid)
    ).filter(Boolean);
    dom.watchlistHighlights.innerHTML = wlPlayers.slice(0, 6).map(p => `
      <div class="drawer-row watchlist-item watching watchlist-row" data-player-id="${p.id}" style="border-radius:var(--radius);cursor:pointer" onclick="openDrawer('${p.id}')">
        <div class="meta"><b>${escapeHtml(p.name)}</b><small>${p.platform} | ${p.status.toUpperCase()} | ${p.flags} flags</small></div>
        <span class="badge ${p.flags >= 4 ? 'red' : 'yellow'}"><i class="fa-solid fa-eye"></i> Monitored</span>
      </div>
    `).join('') || `<div class="hintline">No players on watchlist.</div>`;
  }

  function renderPlayers() {
    if (!dom.playerRows) return;
    const q = (dom.playerSearch?.value || '').trim().toLowerCase();
    const filtered = state.players.filter(p => !q || `${p.name} ${p.uuid} ${p.ip}`.toLowerCase().includes(q));

    const onlineCount = state.players.filter(p => p.status === 'online' || p.status === 'afk').length;
    dom.playersOnlineChip.innerHTML = `<i class="fa-solid fa-users"></i> ${onlineCount} online`;

    // Pagination
    const pag = state.playerPagination || { page: 1, pageSize: 25 };
    const totalItems = filtered.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pag.pageSize));
    if (pag.page > totalPages) pag.page = totalPages;
    if (pag.page < 1) pag.page = 1;
    const startIdx = (pag.page - 1) * pag.pageSize;
    const pageItems = filtered.slice(startIdx, startIdx + pag.pageSize);

    dom.playerRows.innerHTML = pageItems.map(p => {
      const statusBadge = p.status === 'online' ? `<span class="badge green"><i class="fa-solid fa-circle"></i> Online</span>` :
        p.status === 'afk' ? `<span class="badge yellow"><i class="fa-solid fa-moon"></i> AFK</span>` :
        `<span class="badge gray"><i class="fa-regular fa-circle"></i> Offline</span>`;
      const platformBadge = p.platform === 'Bedrock' ? `<span class="badge blue"><i class="fa-solid fa-mobile-screen"></i> Bedrock</span>` : `<span class="badge gray"><i class="fa-solid fa-desktop"></i> Java</span>`;
      const flagsBadge = p.flags >= 4 ? `<span class="badge red"><i class="fa-solid fa-fire"></i> ${p.flags}</span>` : p.flags >= 2 ? `<span class="badge yellow"><i class="fa-solid fa-triangle-exclamation"></i> ${p.flags}</span>` : `<span class="badge gray"><i class="fa-solid fa-shield"></i> ${p.flags}</span>`;
      const watching = state.watchlist.has(p.uuid) || state.watchlist.has(p.id);

      const avatarFallback = `https://minotar.net/helm/${encodeURIComponent(p.name)}/64.png`;
      return `
        <tr class="${watching ? 'watchlist-row' : ''}" data-player-id="${p.id}" style="cursor:pointer" onclick="openDrawer('${p.id}')">
          <td><div class="pwrap"><div class="phead"><img src="${avatarUrl(p)}" alt="" onerror="this.onerror=null;this.src='${avatarFallback}'"></div><div><b style="font-size:13px">${escapeHtml(p.name)} ${watching ? '<span class="watchlist-indicator"></span>' : ''}</b><small style="color:var(--text-secondary);font-size:12px">${escapeHtml(p.uuid.slice(0, 8))}...</small></div></div></td>
          <td>${platformBadge}</td>
          <td>${statusBadge}</td>
          <td>${escapeHtml(fmtLong(p.lastSeen))}</td>
          <td class="flag-cell">${flagsBadge}</td>
          <td style="text-align:right">
            <button class="mini primary" onclick="event.stopPropagation(); openDrawer('${p.id}')"><i class="fa-solid fa-id-card-clip"></i> Profile</button>
            <button class="mini" onclick="event.stopPropagation(); openPunishModal(null,'${p.id}')"><i class="fa-solid fa-bolt"></i> Action</button>
          </td>
        </tr>
      `;
    }).join('') || `<tr><td colspan="6" style="color:var(--muted)">No players match criteria.</td></tr>`;

    // Render pagination controls
    renderPagination('playerPagination', pag.page, totalPages, totalItems, pag.pageSize, 'players');
  }

  function renderPunishments() {
    if (!dom.punishRows) return;
    const q = (dom.punishSearch?.value || '').trim().toLowerCase();
    const filters = state.punishFilters;

    // Filter and sort all punishments
    const allFiltered = state.punishments.filter(pun => {
      if (!filters[pun.type]) return false;
      if (q) {
        const pl = state.players.find(p => p.id === pun.playerId);
        return `${pl?.name || ''} ${pun.reason || ''} ${pun.staff || ''} ${pun.id}`.toLowerCase().includes(q);
      }
      return true;
    }).sort((a, b) => b.createdAt - a.createdAt);

    // Pagination
    const pag = state.punishPagination || { page: 1, pageSize: 25 };
    const totalItems = allFiltered.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pag.pageSize));
    if (pag.page > totalPages) pag.page = totalPages;
    if (pag.page < 1) pag.page = 1;
    const startIdx = (pag.page - 1) * pag.pageSize;
    const pageItems = allFiltered.slice(startIdx, startIdx + pag.pageSize);

    const rows = pageItems.map(pun => {
      const pl = state.players.find(p => p.id === pun.playerId);
      const name = pl?.name || 'Player';
      const avatarFallback = `https://minotar.net/helm/${encodeURIComponent(name)}/64.png`;
      const typeBadge = pun.type === 'BAN' ? `<span class="badge red"><i class="fa-solid fa-ban"></i> BAN</span>` :
        pun.type === 'MUTE' ? `<span class="badge yellow"><i class="fa-solid fa-volume-xmark"></i> MUTE</span>` :
        pun.type === 'KICK' ? `<span class="badge purple"><i class="fa-solid fa-person-walking-arrow-right"></i> KICK</span>` :
        `<span class="badge blue"><i class="fa-solid fa-triangle-exclamation"></i> WARN</span>`;

      // Calculate remaining time display
      let durDisplay = pun.duration || 'instant';
      let durClass = '';
      if (pun.expiresAt === -1) {
        durDisplay = 'Permanent';
        durClass = 'red';
      } else if (pun.expiresAt && pun.expiresAt > 0) {
        const remaining = pun.expiresAt - Date.now();
        if (remaining > 0 && pun.active !== false) {
          durDisplay = formatRemainingTime(remaining);
          durClass = remaining < 3600000 ? 'yellow' : 'blue'; // Yellow if < 1 hour
        } else {
          durDisplay = 'Expired';
          durClass = 'gray';
        }
      }

      const canRevoke = !pun.revoked && pun.active !== false;
      // Check both UUID (from server) and internal ID for watchlist status
      const watching = state.watchlist.has(pun.playerUuid || pun.playerId) || (pl && state.watchlist.has(pl.uuid));

      return `
        <tr class="${watching ? 'watchlist-row' : ''}" data-player-id="${pun.playerId}" style="cursor:pointer">
          <td onclick="openDrawer('${pun.playerId}','${pun.id}')"><div class="pwrap"><div class="phead"><img src="${avatarUrl(pl || { name: name })}" alt="" onerror="this.onerror=null;this.src='${avatarFallback}'"></div><div><b style="font-size:13px">${escapeHtml(pl?.name || 'Unknown')}</b></div></div></td>
          <td><span class="badge gray" style="font-family:var(--font-mono);font-size:11px">${escapeHtml(pun.id)}</span></td>
          <td>${typeBadge}</td>
          <td class="reason-cell">${window.expandableReason ? expandableReason(pun.reason, 15) : escapeHtml(truncate(pun.reason || 'No reason', 15))}</td>
          <td>${escapeHtml(fmtLong(pun.createdAt))}</td>
          <td><span class="badge ${durClass}" title="Time remaining">${escapeHtml(durDisplay)}</span></td>
          <td>${escapeHtml(pun.staff || 'Console')}</td>
          <td style="text-align:right">
            <button class="mini" onclick="event.stopPropagation(); viewPunishmentDetails('${pun.id}')"><i class="fa-solid fa-eye"></i> Details</button>
            ${canRevoke ? `<button class="mini bad" onclick="event.stopPropagation(); revokePunishmentConfirm('${pun.id}')"><i class="fa-solid fa-xmark"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : `<span class="badge gray"><i class="fa-solid fa-check"></i> Closed</span>`}
          </td>
        </tr>
      `;
    }).join('');

    dom.punishRows.innerHTML = rows || `<tr><td colspan="8" style="color:var(--muted)">No punishments match filters.</td></tr>`;

    // Render pagination controls
    renderPagination('punishPagination', pag.page, totalPages, totalItems, pag.pageSize, 'punishments');
  }

  function renderTemplates() {
    if (!dom.templateRows) return;
    const q = (dom.templateSearch?.value || '').trim().toLowerCase();
    const arr = state.templates.filter(t => t.id !== 'none').filter(t => !q || `${t.name} ${t.type} ${t.reason}`.toLowerCase().includes(q));

    dom.templateRows.innerHTML = arr.map(t => `
      <tr>
        <td><b>${escapeHtml(t.name)}</b></td>
        <td>${escapeHtml(t.type)}</td>
        <td>${escapeHtml(t.duration || 'instant')}</td>
        <td class="reason-cell">${window.expandableReason ? expandableReason(t.reason, 15) : escapeHtml(truncate(t.reason || 'No reason', 15))}</td>
        <td style="text-align:right">
          <button class="mini primary" onclick="editTemplateUI('${t.id}')"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
          <button class="mini bad" onclick="deleteTemplate('${t.id}')"><i class="fa-solid fa-trash"></i></button>
        </td>
      </tr>
    `).join('') || `<tr><td colspan="5" style="color:var(--muted)">No templates.</td></tr>`;
  }

  function renderRules() {
    if (!dom.rulesList) return;

    // Get filter values
    const searchQ = (dom.ruleSearch?.value || '').toLowerCase().trim();
    const typeFilter = dom.ruleTypeFilter?.value || 'all';
    const statusFilter = dom.ruleStatusFilter?.value || 'all';

    // Filter rules
    let filteredRules = state.rules.filter(r => {
      // Search filter
      if (searchQ) {
        const searchableText = `${r.name} ${r.notes || ''} ${(r.conditions || []).map(c => c.value || '').join(' ')}`.toLowerCase();
        if (!searchableText.includes(searchQ)) return false;
      }

      // Type filter
      if (typeFilter !== 'all') {
        const ruleType = getRuleType(r);
        if (ruleType.toUpperCase() !== typeFilter.toUpperCase()) return false;
      }

      // Status filter
      if (statusFilter === 'enabled' && !r.enabled) return false;
      if (statusFilter === 'disabled' && r.enabled) return false;

      return true;
    });

    // Sort: built-in rules first, then custom rules, then anticheat rules at the end
    filteredRules.sort((a, b) => {
      const aIsAnticheat = a.id?.startsWith('ac_') || a.type === 'ANTICHEAT';
      const bIsAnticheat = b.id?.startsWith('ac_') || b.type === 'ANTICHEAT';
      const aIsBuiltIn = a.builtIn && !aIsAnticheat;
      const bIsBuiltIn = b.builtIn && !bIsAnticheat;

      // Built-in first
      if (aIsBuiltIn && !bIsBuiltIn) return -1;
      if (!aIsBuiltIn && bIsBuiltIn) return 1;
      // Anticheat last
      if (aIsAnticheat && !bIsAnticheat) return 1;
      if (!aIsAnticheat && bIsAnticheat) return -1;
      // Alphabetical within groups
      return (a.name || '').localeCompare(b.name || '');
    });

    // Pagination
    const pageSize = state.rulesPageSize || 10;
    const totalItems = filteredRules.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
    state.rulesPage = Math.max(1, Math.min(totalPages, state.rulesPage || 1));

    const ruleStart = (state.rulesPage - 1) * pageSize;
    const ruleEnd = ruleStart + pageSize;
    const paginatedRules = filteredRules.slice(ruleStart, ruleEnd);

    // Update pagination UI
    if (dom.rulesCount) {
      dom.rulesCount.textContent = `${totalItems} rule${totalItems !== 1 ? 's' : ''}`;
    }
    if (dom.rulesPageInfo) {
      dom.rulesPageInfo.textContent = `Page ${state.rulesPage} of ${totalPages}`;
    }
    if (dom.rulesPrevBtn) {
      dom.rulesPrevBtn.disabled = state.rulesPage <= 1;
    }
    if (dom.rulesNextBtn) {
      dom.rulesNextBtn.disabled = state.rulesPage >= totalPages;
    }

    // Render rules - different layout for built-in vs custom rules
    const rulesHtml = paginatedRules.map(r => {
      const thr = r.threshold || { hits: 1, windowMins: 10 };
      const isBuiltIn = r.builtIn || r.locked || ['spam_prevention', 'caps_filter', 'link_filter'].includes(r.id) || r.id.startsWith('ac_');

      // Determine rule icon based on type/conditions
      const ruleType = getRuleType(r);
      const icons = {
        SPAM: 'fa-solid fa-message',
        SPAM_PROTECTION: 'fa-solid fa-message',
        CAPS: 'fa-solid fa-font',
        CAPS_FILTER: 'fa-solid fa-font',
        WORD_FILTER: 'fa-solid fa-filter',
        LINK_FILTER: 'fa-solid fa-link',
        AFK_KICK: 'fa-solid fa-clock',
        ANTICHEAT: 'fa-solid fa-shield-halved',
        link: 'fa-solid fa-link',
        custom: 'fa-solid fa-robot'
      };
      const ruleIcon = icons[ruleType] || icons.custom;
      const typeColors = {
        SPAM: 'var(--warn)',
        SPAM_PROTECTION: 'var(--warn)',
        CAPS: 'var(--accent-light)',
        CAPS_FILTER: 'var(--accent-light)',
        WORD_FILTER: 'var(--bad)',
        LINK_FILTER: 'var(--primary-light)',
        AFK_KICK: 'var(--muted)',
        ANTICHEAT: 'var(--purple)',
        link: 'var(--primary-light)',
        custom: 'var(--text-secondary)'
      };
      const iconColor = typeColors[ruleType] || typeColors.custom;

      // Built-in rules get a simplified card
      if (isBuiltIn) {
        return renderBuiltInRuleCard(r, thr, ruleIcon, iconColor, ruleType);
      }

      // Custom rules get the full editor
      return renderCustomRuleCard(r, thr, ruleIcon, iconColor);
    }).join('');

    // Render rules list
    dom.rulesList.innerHTML = rulesHtml || (totalItems === 0 ? `<div class="hintline">No rules match your search. Click "Add Rule" to create one.</div>` : '');
  }

  // Render a built-in rule card (spam, caps, links, afk) - simplified, no condition editing
  function renderBuiltInRuleCard(r, thr, ruleIcon, iconColor, ruleType) {
    // Get rule-specific configuration
    const spamMessageCount = r.spamMessageCount || 3;
    const spamTimeWindow = r.spamTimeWindowSeconds || 5;
    const spamDetectSimilar = r.spamDetectSimilar !== false;
    const capsMaxPercent = r.capsMaxPercentage || 70;
    const capsMinLength = r.capsMinLength || 10;
    const afkTimeout = r.afkTimeoutMinutes || 15;
    const afkKickEnabled = r.afkKickEnabled || false;
    const description = r.description || r.notes || getBuiltInDescription(r.id, r);

    // Rule-specific configuration section
    let configSection = '';

    if (r.id === 'spam_protection' || ruleType === 'SPAM_PROTECTION') {
      configSection = `
        <div class="card" style="margin:10px 0 0 0;background:var(--bg-secondary);padding:12px">
          <div class="hintline" style="margin:0 0 10px 0"><b>Spam Detection Settings</b></div>
          <div class="block" style="gap:8px;flex-wrap:wrap">
            <span class="badge gray">Block after</span>
            <input class="input" type="number" min="2" max="20" value="${spamMessageCount}"
              oninput="setRuleSetting('${r.id}', 'spamMessageCount', this.value)" style="width:60px"/>
            <span class="badge gray">messages in</span>
            <input class="input" type="number" min="1" max="60" value="${spamTimeWindow}"
              oninput="setRuleSetting('${r.id}', 'spamTimeWindowSeconds', this.value)" style="width:60px"/>
            <span class="badge gray">seconds</span>
          </div>
          <div class="toggle-wrap" style="margin-top:10px">
            <button class="toggle tiny ${spamDetectSimilar ? 'on' : ''}" onclick="setRuleSetting('${r.id}', 'spamDetectSimilar', ${!spamDetectSimilar})"><span class="toggle-thumb"></span></button>
            <div class="toggle-meta"><div class="toggle-title">Detect similar messages</div><div class="toggle-hint">Uses Levenshtein distance to detect near-duplicate messages</div></div>
          </div>
        </div>
      `;
    } else if (r.id === 'caps_filter' || ruleType === 'CAPS_FILTER') {
      configSection = `
        <div class="card" style="margin:10px 0 0 0;background:var(--bg-secondary);padding:12px">
          <div class="hintline" style="margin:0 0 10px 0"><b>Caps Filter Settings</b></div>
          <div class="block" style="gap:8px;flex-wrap:wrap">
            <span class="badge gray">Max caps allowed:</span>
            <input class="input" type="number" min="10" max="100" value="${capsMaxPercent}"
              oninput="setRuleSetting('${r.id}', 'capsMaxPercentage', this.value)" style="width:70px"/>
            <span class="badge gray">%</span>
          </div>
          <div class="block" style="gap:8px;margin-top:8px;flex-wrap:wrap">
            <span class="badge gray">Min message length:</span>
            <input class="input" type="number" min="1" max="100" value="${capsMinLength}"
              oninput="setRuleSetting('${r.id}', 'capsMinLength', this.value)" style="width:70px"/>
            <span class="badge gray">characters</span>
          </div>
        </div>
      `;
    } else if (r.id === 'link_filter' || ruleType === 'LINK_FILTER') {
      configSection = `
        <div class="card" style="margin:10px 0 0 0;background:var(--bg-secondary);padding:12px">
          <div class="hintline" style="margin:0"><b>Link Filter</b></div>
          <div class="hintline" style="margin:6px 0 0 0">Automatically detects and blocks URLs, IP addresses, and server advertisements.</div>
        </div>
      `;
    } else if (r.id === 'afk_kick' || ruleType === 'AFK_KICK') {
      configSection = `
        <div class="card" style="margin:10px 0 0 0;background:var(--bg-secondary);padding:12px">
          <div class="hintline" style="margin:0 0 10px 0"><b>AFK Kick Settings</b></div>
          <div class="toggle-wrap" style="margin-bottom:10px">
            <button class="toggle tiny ${afkKickEnabled ? 'on' : ''}" onclick="setRuleSetting('${r.id}', 'afkKickEnabled', ${!afkKickEnabled})"><span class="toggle-thumb"></span></button>
            <div class="toggle-meta"><div class="toggle-title">Enable AFK Kick</div><div class="toggle-hint">Automatically kick players who are inactive</div></div>
          </div>
          <div class="block" style="gap:8px;flex-wrap:wrap">
            <span class="badge gray">Kick after</span>
            <input class="input" type="number" min="1" max="120" value="${afkTimeout}"
              oninput="setRuleSetting('${r.id}', 'afkTimeoutMinutes', this.value)" style="width:70px"/>
            <span class="badge gray">minutes of inactivity</span>
          </div>
        </div>
      `;
    } else if (r.id.startsWith('ac_') || ruleType === 'ANTICHEAT') {
      const acThreshold = r.anticheatAlertThreshold || 5;
      const acWindow = r.anticheatTimeWindowSeconds || 60;
      const acName = r.anticheatName || '';
      const checkName = r.checkName || '';
      configSection = `
        <div class="card" style="margin:10px 0 0 0;background:var(--bg-secondary);padding:12px">
          <div class="hintline" style="margin:0 0 10px 0"><b>Anticheat Check Settings</b></div>
          <div style="display:flex;gap:8px;margin-bottom:10px;flex-wrap:wrap">
            <span class="badge purple"><i class="fa-solid fa-shield-halved"></i> ${escapeHtml(acName)}</span>
            <span class="badge blue"><i class="fa-solid fa-crosshairs"></i> ${escapeHtml(checkName)}</span>
          </div>
          <div class="block" style="gap:8px;flex-wrap:wrap">
            <span class="badge gray">Trigger after</span>
            <input class="input" type="number" min="1" max="100" value="${acThreshold}"
              oninput="setRuleSetting('${r.id}', 'anticheatAlertThreshold', this.value)" style="width:70px"/>
            <span class="badge gray">alerts in</span>
            <input class="input" type="number" min="10" max="600" value="${acWindow}"
              oninput="setRuleSetting('${r.id}', 'anticheatTimeWindowSeconds', this.value)" style="width:80px"/>
            <span class="badge gray">seconds</span>
          </div>
        </div>
      `;
    }

    // Auto punishment section (not for AFK)
    const showPunishment = r.id !== 'afk_kick';
    const punishSection = showPunishment ? `
      <div style="margin-top:12px">
        <div class="block" style="gap:10px;flex-wrap:wrap">
          <b style="font-size:12px">Auto Punish</b>
          <select class="input" style="width:140px" onchange="setRuleAction('${r.id}', this.value)">
            ${['none', 'warn', 'mute', 'kick', 'ban'].map(k => `<option value="${k}" ${r.action?.kind === k ? 'selected' : ''}>${k}</option>`).join('')}
          </select>
          ${r.action?.kind && r.action.kind !== 'none' ? `<input class="input" style="flex:1;min-width:150px" value="${escapeHtml(r.action?.extra || '')}" oninput="setRuleActionExtra('${r.id}', this.value)" placeholder="Reason"/>` : ''}
          ${['warn','mute','ban'].includes(r.action?.kind) ? `<input class="input" style="width:120px" value="${escapeHtml(r.action?.duration || '')}" oninput="setRuleActionDuration('${r.id}', this.value)" placeholder="Duration"/>` : ''}
        </div>
      </div>
      <div class="block" style="margin-top:12px">
        <span class="badge gray">Punish after:</span>
        <input class="input" type="number" min="1" value="${thr.hits || 3}" style="width:70px" oninput="setRuleThreshold('${r.id}', 'hits', this.value)">
        <span class="badge gray">violations in</span>
        <input class="input" type="number" min="1" value="${thr.windowMins || 5}" style="width:70px" oninput="setRuleThreshold('${r.id}', 'windowMins', this.value)">
        <span class="badge gray">minutes</span>
      </div>
    ` : '';

    return `
      <div class="card" style="margin:0">
        <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:10px">
          <div>
            <div style="display:flex;align-items:center;gap:10px">
              <i class="${ruleIcon}" style="font-size:18px;color:${iconColor}"></i>
              <b style="font-size:14px">${escapeHtml(r.name)}</b>
              ${r.enabled ? `<span class="badge green"><i class="fa-solid fa-check"></i> Active</span>` : `<span class="badge gray"><i class="fa-solid fa-pause"></i> Inactive</span>`}
              ${ruleType === 'ANTICHEAT' ? `<span class="badge purple"><i class="fa-solid fa-shield-halved"></i> Anticheat</span>` : `<span class="badge blue"><i class="fa-solid fa-lock"></i> Built-in</span>`}
            </div>
            <div class="hintline">${escapeHtml(description)}</div>
          </div>
          <button class="toggle ${r.enabled ? 'on' : ''}" onclick="toggleRule('${r.id}')"><span class="toggle-thumb"></span></button>
        </div>
        ${configSection}
        ${punishSection}
      </div>
    `;
  }

  // Get description for built-in rules
  function getBuiltInDescription(ruleId, rule) {
    const descriptions = {
      'spam_protection': 'Blocks rapid or repetitive messages automatically.',
      'caps_filter': 'Converts excessive caps to lowercase.',
      'link_filter': 'Blocks URLs and IP addresses in chat.',
      'afk_kick': 'Kicks players after a period of inactivity.'
    };
    // Anticheat rules have dynamic descriptions
    if (ruleId.startsWith('ac_') && rule) {
      const acName = rule.anticheatName || 'Anticheat';
      const checkName = rule.checkName || 'Unknown';
      return `Triggers when ${acName} flags a player for ${checkName}.`;
    }
    return descriptions[ruleId] || 'Built-in automod filter.';
  }

  // Render a custom rule card - simplified, click to edit
  function renderCustomRuleCard(r, thr, ruleIcon, iconColor) {
    // Build a summary of what the rule filters
    const conditions = r.conditions || [];
    const phrases = conditions
      .filter(c => c.kind === 'contains' || c.kind === 'regex')
      .map(c => c.value || '')
      .filter(v => v)
      .join(', ');
    const phraseSummary = phrases ? phrases.slice(0, 80) + (phrases.length > 80 ? '...' : '') : 'No filters configured';

    // Action summary
    const action = r.action?.kind && r.action.kind !== 'none'
      ? `<span class="badge ${r.action.kind === 'ban' ? 'bad' : r.action.kind === 'kick' ? 'warn' : 'blue'}">${r.action.kind.toUpperCase()}</span>`
      : '<span class="badge gray">No action</span>';

    return `
      <div class="card rule-card-clickable" style="margin:0;cursor:pointer" onclick="openAutomodRuleEditor('${r.id}')">
        <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:10px">
          <div style="flex:1;min-width:0">
            <div style="display:flex;align-items:center;gap:10px;flex-wrap:wrap">
              <i class="${ruleIcon}" style="font-size:18px;color:${iconColor}"></i>
              <b style="font-size:14px">${escapeHtml(r.name)}</b>
              ${r.enabled ? `<span class="badge green"><i class="fa-solid fa-check"></i> Active</span>` : `<span class="badge gray"><i class="fa-solid fa-pause"></i> Inactive</span>`}
              ${action}
            </div>
            <div class="hintline" style="margin-top:6px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
              <i class="fa-solid fa-filter" style="margin-right:6px;color:var(--muted)"></i>${escapeHtml(phraseSummary)}
            </div>
            <div class="hintline" style="margin-top:4px">
              <span class="badge gray" style="font-size:10px">${thr.hits} trigger${thr.hits !== 1 ? 's' : ''} in ${thr.windowMins} min</span>
              ${(r.exceptions || []).length > 0 ? `<span class="badge gray" style="font-size:10px">${r.exceptions.length} exception${r.exceptions.length !== 1 ? 's' : ''}</span>` : ''}
            </div>
          </div>
          <div style="display:flex;gap:10px;align-items:center" onclick="event.stopPropagation()">
            <button class="toggle ${r.enabled ? 'on' : ''}" onclick="toggleRule('${r.id}')"><span class="toggle-thumb"></span></button>
            <button class="mini bad delete" onclick="deleteRule('${r.id}')" title="Delete rule"><i class="fa-solid fa-trash"></i></button>
          </div>
        </div>
      </div>
    `;
  }

  // Open the automod rule editor popup
  function openAutomodRuleEditor(ruleId) {
    const r = state.rules.find(rule => rule.id === ruleId);
    if (!r) return;

    const thr = r.threshold || { hits: 1, windowMins: 10 };
    const conditions = r.conditions || [];

    // Get phrases from conditions
    const phrases = conditions
      .filter(c => c.kind === 'contains')
      .map(c => c.value || '')
      .filter(v => v)
      .join('\n');

    const regexPatterns = conditions
      .filter(c => c.kind === 'regex')
      .map(c => c.value || '')
      .filter(v => v)
      .join('\n');

    const exceptions = (r.exceptions || []).join('\n');

    // Create modal
    const modal = document.createElement('div');
    modal.className = 'overlay';
    modal.id = 'automodRuleEditorOverlay';
    modal.style.display = 'flex';
    modal.innerHTML = `
      <div class="modal" style="max-width:600px;max-height:90vh;overflow-y:auto" onclick="event.stopPropagation()">
        <div class="modal-top">
          <b><i class="fa-solid fa-filter" style="margin-right:8px"></i>Edit Filter Rule</b>
          <button class="mini" onclick="closeAutomodRuleEditor()"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="hintline" style="margin-top:0">Rule Name</div>
          <input type="text" class="input" id="automodRuleName" value="${escapeHtml(r.name)}" placeholder="Rule name..." style="width:100%">

          <div class="hintline">Blocked Phrases <span style="color:var(--text-secondary);font-weight:normal">(one per line)</span></div>
          <textarea class="input" id="automodRulePhrases" rows="4" placeholder="Enter words or phrases to block (one per line)..." style="font-family:var(--font-mono);font-size:12px">${escapeHtml(phrases)}</textarea>

          <div class="hintline">Regex Patterns <span style="color:var(--text-secondary);font-weight:normal">(advanced, one per line)</span></div>
          <textarea class="input" id="automodRuleRegex" rows="2" placeholder="Enter regex patterns (one per line)..." style="font-family:var(--font-mono);font-size:12px">${escapeHtml(regexPatterns)}</textarea>

          <div class="hintline">Exceptions <span style="color:var(--text-secondary);font-weight:normal">(words/phrases that won't trigger, one per line)</span></div>
          <textarea class="input" id="automodRuleExceptions" rows="2" placeholder="Enter exceptions (one per line)..." style="font-family:var(--font-mono);font-size:12px">${escapeHtml(exceptions)}</textarea>

          <div class="hintline">Applies To</div>
          <div class="block" style="gap:10px">
            <select class="input" id="automodRuleAppliesTo" style="width:200px">
              <option value="chat" ${!r.applyToNicknames && !r.nicknameOnly ? 'selected' : ''}>Chat Only</option>
              <option value="nicknames" ${r.nicknameOnly ? 'selected' : ''}>Nicknames Only</option>
              <option value="both" ${r.applyToNicknames && !r.nicknameOnly ? 'selected' : ''}>Both Chat & Nicknames</option>
            </select>
            <span class="badge gray" style="font-size:11px">Where this filter applies</span>
          </div>

          <div class="hintline">Auto Punishment</div>
          <div class="block" style="gap:10px;flex-wrap:wrap">
            <select class="input" id="automodRuleActionKind" style="width:140px" onchange="updateAutomodActionFields()">
              ${['none', 'warn', 'mute', 'kick', 'ban'].map(k => `<option value="${k}" ${r.action?.kind === k ? 'selected' : ''}>${k.charAt(0).toUpperCase() + k.slice(1)}</option>`).join('')}
            </select>
            <input class="input" id="automodRuleActionReason" value="${escapeHtml(r.action?.extra || '')}" placeholder="Reason for punishment..." style="flex:1;min-width:180px;display:${r.action?.kind && r.action.kind !== 'none' ? 'block' : 'none'}">
            <input class="input" id="automodRuleActionDuration" value="${escapeHtml(r.action?.duration || '')}" placeholder="Duration (e.g., 1h, 1d)" style="width:140px;display:${['warn','mute','ban'].includes(r.action?.kind) ? 'block' : 'none'}">
          </div>

          <div class="hintline">Trigger Threshold</div>
          <div class="block" style="gap:10px;flex-wrap:wrap;align-items:center">
            <span class="badge gray">Punish after</span>
            <input class="input" type="number" id="automodRuleThresholdHits" min="1" value="${thr.hits}" style="width:70px">
            <span class="badge gray">violations in</span>
            <input class="input" type="number" id="automodRuleThresholdWindow" min="1" value="${thr.windowMins}" style="width:70px">
            <span class="badge gray">minutes</span>
          </div>

          <div class="block" style="margin-top:24px;gap:10px">
            <button class="btn primary" onclick="saveAutomodRuleFromEditor('${r.id}')"><i class="fa-solid fa-check"></i> Save</button>
            <button class="btn" onclick="closeAutomodRuleEditor()">Cancel</button>
          </div>
        </div>
      </div>
    `;
    modal.onclick = closeAutomodRuleEditor;
    document.body.appendChild(modal);
  }

  // Expose openAutomodRuleEditor to window
  window.openAutomodRuleEditor = openAutomodRuleEditor;

  window.closeAutomodRuleEditor = function() {
    const overlay = document.getElementById('automodRuleEditorOverlay');
    if (overlay) overlay.remove();
  };

  window.updateAutomodActionFields = function() {
    const kind = document.getElementById('automodRuleActionKind')?.value;
    const reasonInput = document.getElementById('automodRuleActionReason');
    const durationInput = document.getElementById('automodRuleActionDuration');

    if (reasonInput) {
      reasonInput.style.display = kind && kind !== 'none' ? 'block' : 'none';
    }
    if (durationInput) {
      durationInput.style.display = ['warn', 'mute', 'ban'].includes(kind) ? 'block' : 'none';
    }
  };

  window.saveAutomodRuleFromEditor = function(ruleId) {
    const r = state.rules.find(rule => rule.id === ruleId);
    if (!r) return;

    // Get values from form
    const name = document.getElementById('automodRuleName')?.value?.trim() || 'Unnamed Rule';
    const phrasesText = document.getElementById('automodRulePhrases')?.value || '';
    const regexText = document.getElementById('automodRuleRegex')?.value || '';
    const exceptionsText = document.getElementById('automodRuleExceptions')?.value || '';
    const appliesTo = document.getElementById('automodRuleAppliesTo')?.value || 'chat';
    const actionKind = document.getElementById('automodRuleActionKind')?.value || 'none';
    const actionReason = document.getElementById('automodRuleActionReason')?.value || '';
    const actionDuration = document.getElementById('automodRuleActionDuration')?.value || '';
    const thresholdHits = parseInt(document.getElementById('automodRuleThresholdHits')?.value || '1', 10);
    const thresholdWindow = parseInt(document.getElementById('automodRuleThresholdWindow')?.value || '10', 10);

    // Parse phrases and regex into conditions
    const phrases = phrasesText.split('\n').map(p => p.trim()).filter(p => p);
    const regexes = regexText.split('\n').map(p => p.trim()).filter(p => p);
    const exceptions = exceptionsText.split('\n').map(p => p.trim()).filter(p => p);

    // Build conditions
    const conditions = [];
    if (phrases.length > 0) {
      conditions.push({ kind: 'contains', value: phrases.join(', ') });
    }
    regexes.forEach(regex => {
      conditions.push({ kind: 'regex', value: regex });
    });

    // Update rule
    r.name = name;
    r.conditions = conditions;
    r.exceptions = exceptions;

    // Set applies to flags
    r.applyToNicknames = appliesTo === 'both';
    r.nicknameOnly = appliesTo === 'nicknames';

    r.action = {
      kind: actionKind,
      extra: actionReason,
      duration: actionDuration
    };
    r.threshold = {
      hits: Math.max(1, thresholdHits),
      windowMins: Math.max(1, thresholdWindow)
    };

    // Mark unsaved and re-render
    window.MX.ui.markUnsaved('rules', true);
    window.MX.ui.renderAutomod();
    closeAutomodRuleEditor();

    window.toast('info', 'Updated', 'Rule updated. Click "Save Changes" to sync.');
  };

  // Helper function to determine rule type for filtering
  function getRuleType(rule) {
    // Use explicit type from rule if available
    if (rule.type) return rule.type.toUpperCase();
    // Check if it's an anticheat rule by ID prefix
    if (rule.id && rule.id.startsWith('ac_')) return 'ANTICHEAT';
    // Infer from id for built-in rules
    if (rule.id === 'spam_protection' || rule.id === 'spam_prevention') return 'SPAM_PROTECTION';
    if (rule.id === 'caps_filter') return 'CAPS_FILTER';
    if (rule.id === 'link_filter') return 'LINK_FILTER';
    if (rule.id === 'afk_kick') return 'AFK_KICK';
    // Infer from conditions
    const conditions = rule.conditions || [];
    if (conditions.some(c => c.kind === 'contains' || c.kind === 'regex')) return 'WORD_FILTER';
    if (conditions.some(c => c.kind === 'caps')) return 'CAPS_FILTER';
    if (conditions.some(c => c.kind === 'repeat')) return 'SPAM_PROTECTION';
    if (conditions.some(c => c.kind === 'link')) return 'LINK_FILTER';
    return 'WORD_FILTER';
  }

  function renderMessages() {
    if (!dom.msgRows) return;
    dom.langSelect.value = state.lang;
    const q = (dom.msgSearch?.value || '').trim().toLowerCase();
    const dict = state.messages[state.lang] || {};

    dom.msgRows.innerHTML = Object.keys(dict).sort().filter(k => !q || k.toLowerCase().includes(q)).map(k => `
      <tr><td><b>${escapeHtml(k)}</b></td><td style="color:var(--text-secondary)">${escapeHtml(String(dict[k]).slice(0, 60))}</td></tr>
    `).join('') || `<tr><td colspan="2" style="color:var(--muted)">No messages.</td></tr>`;

    const title = dict['ban.screen.title'] || '&cYou are banned!';
    const body = dict['ban.screen.body'] || '';
    dom.banPreview.textContent = `${title}\n\n${body}`.replace(/<Reason>/g, 'Violation').replace(/<Moderator>/g, 'Admin').replace(/&[0-9a-fk-or]/gi, '').replace(/\\n/g, '\n');
  }

  function renderIntegrations() {
    if (!dom.discordWebhook) return;
    dom.discordWebhook.value = state.settings.discordWebhook || '';
    if (dom.webhookToggles) {
      const items = [
        { key: 'whBan', label: 'Ban' },
        { key: 'whMute', label: 'Mute' },
        { key: 'whWarn', label: 'Warn' },
        { key: 'whAutomod', label: 'Automod' },
        { key: 'whCaseCreated', label: 'Case Created' },
        { key: 'whCaseUpdated', label: 'Case Updated' },
        { key: 'whPunishRemoved', label: 'Punishment Removed' },
        { key: 'whWatchlist', label: 'Watchlist' }
      ];
      dom.webhookToggles.innerHTML = items.map(item => `
        <div class="check-toggle ${state.settings[item.key] ? 'on' : ''}" onclick="toggleSetting('${item.key}')">
          <span class="check-icon"><i class="fa-solid fa-check"></i></span>
          <span>${escapeHtml(item.label)}</span>
        </div>
      `).join('');
    }

    if (dom.voiceChatStatus) dom.voiceChatStatus.textContent = state.integrations?.voiceChatDetected ? 'Detected' : 'Not detected';
    if (dom.luckPermsStatus) dom.luckPermsStatus.textContent = state.integrations?.luckPermsDetected ? 'Detected' : 'Not detected';

    // Geyser/Floodgate status
    const geyserStatus = document.getElementById('geyserStatus');
    const floodgateStatus = document.getElementById('floodgateStatus');
    const geyserDetails = document.getElementById('geyserDetails');
    const floodgateDetails = document.getElementById('floodgateDetails');

    if (geyserStatus) {
      const geyserAvail = state.integrations?.geyserDetected;
      geyserStatus.textContent = geyserAvail ? 'Detected' : 'Not detected';
      geyserStatus.className = 'badge ' + (geyserAvail ? 'good' : 'gray');
      if (geyserDetails && state.integrations?.geyserVersion) {
        geyserDetails.textContent = 'v' + state.integrations.geyserVersion;
      }
    }

    if (floodgateStatus) {
      const floodgateAvail = state.integrations?.floodgateDetected;
      floodgateStatus.textContent = floodgateAvail ? 'Detected' : 'Not detected';
      floodgateStatus.className = 'badge ' + (floodgateAvail ? 'good' : 'gray');
      if (floodgateDetails && state.integrations?.floodgateVersion) {
        floodgateDetails.textContent = 'v' + state.integrations.floodgateVersion;
      }
    }

    // Citizens status
    const citizensStatus = document.getElementById('citizensStatus');
    const citizensDetails = document.getElementById('citizensDetails');
    if (citizensStatus) {
      const citizensAvail = state.integrations?.citizensDetected;
      citizensStatus.textContent = citizensAvail ? 'Detected' : 'Not detected';
      citizensStatus.className = 'badge ' + (citizensAvail ? 'good' : 'gray');
      if (citizensDetails) {
        if (citizensAvail && state.integrations?.citizensVersion) {
          citizensDetails.textContent = 'v' + state.integrations.citizensVersion + ' - Used for replay playback';
        } else {
          citizensDetails.textContent = 'Install Citizens for NPC-based replay playback';
        }
      }
    }

    // Anticheat display - show all known anticheats but highlight detected/hooked ones
    if (dom.anticheatList) {
      const hookedAcs = state.integrations?.hookedAnticheats || [];
      const allAcs = state.integrations?.anticheats || ['Grim', 'Vulcan', 'Matrix', 'Spartan', 'AAC', 'NoCheatPlus', 'Intave', 'NCP', 'Kauri', 'Verus', 'Negativity', 'AntiCheatReloaded', 'Themis', 'Astra', 'Polar', 'Warden', 'Flappy', 'Karhu'];

      if (hookedAcs.length > 0) {
        // Show hooked anticheats prominently
        dom.anticheatList.innerHTML = hookedAcs.map(ac => `
          <div class="integration-item" style="display:flex;align-items:center;justify-content:space-between;padding:12px;border:1px solid var(--good);border-radius:var(--radius);background:rgba(0,200,100,0.1);margin-bottom:8px">
            <div style="display:flex;align-items:center;gap:12px">
              <div style="width:36px;height:36px;border-radius:var(--radius);background:linear-gradient(135deg,var(--good),var(--accent));display:flex;align-items:center;justify-content:center">
                <i class="fa-solid fa-shield-halved" style="color:#fff"></i>
              </div>
              <div>
                <div style="font-weight:600">${escapeHtml(ac.name || ac)}</div>
                <div style="font-size:12px;color:var(--text-secondary)">${ac.alertsEnabled ? 'Alerts enabled' : 'Monitoring active'}</div>
              </div>
            </div>
            <span class="badge good"><i class="fa-solid fa-link"></i> Hooked</span>
          </div>
        `).join('');
      } else {
        // No anticheats hooked - show available ones as gray
        dom.anticheatList.innerHTML = `
          <div style="color:var(--text-secondary);font-size:13px;margin-bottom:12px">No anticheat plugins detected. Supported anticheats:</div>
          <div style="display:flex;flex-wrap:wrap;gap:6px">
            ${allAcs.map(a => `<span class="badge gray">${escapeHtml(a)}</span>`).join('')}
          </div>
        `;
      }
    }

    if (dom.webhookPreview) {
      dom.webhookPreview.innerHTML = escapeHtml(`ModereX Case Created\nPlayer: ${state.players[0]?.name || 'Player'}\nAction: BAN\nReason: Violation\nStaff: Admin`);
    }
  }

  function renderStaffSettings() {
    const container = $('#staffNotificationSettings');
    if (!container) return;

    const settings = state.staffSettings || {};

    // Generate alert level dropdown HTML
    const levelOptions = (current, key) => `
      <select class="input small" onchange="updateStaffSetting('${key}', this.value)">
        <option value="EVERYONE" ${current === 'EVERYONE' ? 'selected' : ''}>Everyone</option>
        <option value="WATCHLIST_ONLY" ${current === 'WATCHLIST_ONLY' ? 'selected' : ''}>Watchlist Only</option>
        <option value="OFF" ${current === 'OFF' ? 'selected' : ''}>Off</option>
      </select>
    `;

    // Generate web notification mode dropdown
    const notifyModeOptions = (current, key) => `
      <select class="input small" onchange="updateStaffSetting('${key}', this.value)">
        <option value="toast" ${current === 'toast' ? 'selected' : ''}>Toast</option>
        <option value="browser" ${current === 'browser' ? 'selected' : ''}>Browser Notification</option>
        <option value="off" ${current === 'off' ? 'selected' : ''}>Off</option>
      </select>
    `;

    const toggleHtml = (key, label) => `
      <div class="check-toggle ${settings[key] ? 'on' : ''}" onclick="updateStaffSetting('${key}', ${!settings[key]})">
        <span class="check-icon"><i class="fa-solid fa-check"></i></span>
        <span>${escapeHtml(label)}</span>
      </div>
    `;

    container.innerHTML = `
      <div class="setting-group">
        <h4><i class="fa-solid fa-gavel"></i> Punishment Alerts</h4>
        <div class="setting-row">
          <span>Punishment Alerts</span>
          ${levelOptions(settings.banAlertsLevel, 'banAlertsLevel')}
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-robot"></i> Automod Alerts</h4>
        <div class="setting-row">
          <span>Automod Alerts</span>
          ${levelOptions(settings.automodAlertsLevel, 'automodAlertsLevel')}
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-shield-halved"></i> Anticheat Alerts</h4>
        <div class="setting-row">
          <span>Anticheat Alerts</span>
          ${levelOptions(settings.anticheatAlertsLevel, 'anticheatAlertsLevel')}
        </div>
        <div class="setting-row">
          <span>Minimum VL</span>
          <input type="number" class="input tiny" min="1" max="100" value="${settings.anticheatMinVL || 10}"
            onchange="updateStaffSetting('anticheatMinVL', parseInt(this.value))">
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-eye"></i> Watchlist Alerts</h4>
        <div class="setting-row toggles-row">
          ${toggleHtml('watchlistJoinAlerts', 'Join')}
          ${toggleHtml('watchlistQuitAlerts', 'Quit')}
          ${toggleHtml('watchlistActivityAlerts', 'Activity')}
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-terminal"></i> Commands</h4>
        <div class="setting-row">
          <span>Command Alerts</span>
          ${levelOptions(settings.commandAlerts, 'commandAlerts')}
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-comments"></i> Staff Chat</h4>
        <div class="setting-row toggles-row">
          ${toggleHtml('staffChatEnabled', 'Enabled')}
          ${toggleHtml('staffChatSound', 'Sound')}
        </div>
      </div>

      <div class="setting-group">
        <h4><i class="fa-solid fa-bell"></i> Web Panel Notifications</h4>
        <p class="hintline" style="margin-top:0;margin-bottom:12px">How alerts appear in this panel</p>
        <div class="setting-row">
          <span>Punishments</span>
          ${notifyModeOptions(settings.webNotifyPunishments || 'toast', 'webNotifyPunishments')}
        </div>
        <div class="setting-row">
          <span>Automod</span>
          ${notifyModeOptions(settings.webNotifyAutomod || 'toast', 'webNotifyAutomod')}
        </div>
        <div class="setting-row">
          <span>Anticheat</span>
          ${notifyModeOptions(settings.webNotifyAnticheat || 'toast', 'webNotifyAnticheat')}
        </div>
        <div class="setting-row">
          <span>Watchlist</span>
          ${notifyModeOptions(settings.webNotifyWatchlist || 'toast', 'webNotifyWatchlist')}
        </div>
        <div class="setting-row">
          <span>Staff Chat</span>
          ${notifyModeOptions(settings.webNotifyStaffChat || 'toast', 'webNotifyStaffChat')}
        </div>
      </div>
    `;
  }

  function renderAnticheat() {
    const categoriesContainer = $('#anticheatCategories');
    const categoryFilter = $('#acCategoryFilter');
    const detectedBadge = $('#acDetectedBadge');
    const presetSelect = $('#acPresetSelect');
    const anticheatConfig = $('#anticheatConfig');
    const anticheatDisabledCard = $('#anticheatDisabledCard');

    const enabled = !!state.settings.anticheatReplace;
    if (anticheatConfig) anticheatConfig.style.display = enabled ? 'block' : 'none';
    if (anticheatDisabledCard) anticheatDisabledCard.style.display = enabled ? 'none' : 'block';
    if (!enabled || !categoriesContainer) return;

    const anticheats = state.anticheat?.anticheats || [];
    const prefs = state.anticheat?.alertPrefs || {};
    const presets = state.anticheat?.presets || [];

    // Update detected badge
    if (detectedBadge) {
      if (anticheats.length > 0) {
        const names = anticheats.map(ac => ac.name).join(', ');
        detectedBadge.className = 'badge purple';
        detectedBadge.innerHTML = `<i class="fa-solid fa-shield-halved"></i> ${names}`;
      } else {
        detectedBadge.className = 'badge gray';
        detectedBadge.innerHTML = `<i class="fa-solid fa-shield-halved"></i> No anticheat detected`;
      }
    }

    // Populate preset dropdown
    if (presetSelect && presets.length > 0) {
      presetSelect.innerHTML = `<option value="">Apply Preset...</option>` +
        presets.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join('');
    }

    // Build category filter options
    const allCategories = new Set();
    anticheats.forEach(ac => {
      if (ac.categories) {
        Object.keys(ac.categories).forEach(cat => allCategories.add(cat));
      }
    });

    if (categoryFilter) {
      const currentVal = categoryFilter.value;
      categoryFilter.innerHTML = `<option value="">All Categories</option>` +
        [...allCategories].sort().map(cat => {
          const displayName = cat.charAt(0).toUpperCase() + cat.slice(1).replace(/_/g, ' ');
          return `<option value="${cat}">${displayName}</option>`;
        }).join('');
      categoryFilter.value = currentVal;
    }

    // Get filter values
    const searchQ = ($('#anticheatSearch')?.value || '').trim().toLowerCase();
    const catFilter = categoryFilter?.value || '';

    // Render checks grouped by category
    let html = '';

    anticheats.forEach(ac => {
      const acNameLower = ac.name.toLowerCase();
      const categories = ac.categories || {};

      Object.entries(categories).forEach(([catName, checks]) => {
        if (catFilter && catName !== catFilter) return;

        const filteredChecks = checks.filter(check =>
          !searchQ || check.name.toLowerCase().includes(searchQ) || check.displayName.toLowerCase().includes(searchQ)
        );

        if (filteredChecks.length === 0) return;

        const catDisplayName = catName.charAt(0).toUpperCase() + catName.slice(1).replace(/_/g, ' ');
        const catIcon = getCategoryIcon(catName);

        html += `
          <div class="card" style="margin:0">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:12px">
              <h3 style="margin:0"><i class="${catIcon}" style="color:var(--primary-light);margin-right:8px"></i>${catDisplayName}</h3>
              <span class="badge gray">${filteredChecks.length} checks</span>
            </div>
            <div class="ac-checks-grid">
              ${filteredChecks.map(check => {
                const prefKey = `${acNameLower}.${check.name}`;
                const pref = prefs[prefKey] || { alertLevel: 'EVERYONE', thresholdCount: 1, timeWindowSeconds: 60 };
                const isOff = pref.alertLevel === 'OFF';
                const isWatchlist = pref.alertLevel === 'WATCHLIST_ONLY';

                return `
                  <div class="ac-check-item ${isOff ? 'disabled' : ''}" data-check="${check.name}" data-ac="${ac.name}">
                    <div class="ac-check-header">
                      <div class="ac-check-name">
                        <b>${escapeHtml(check.displayName)}</b>
                        <span class="ac-check-desc">${escapeHtml(check.description || '')}</span>
                      </div>
                      <div class="ac-check-level">
                        <select class="input small" onchange="updateCheckAlertLevel('${ac.name}','${check.name}',this.value)">
                          <option value="EVERYONE" ${pref.alertLevel === 'EVERYONE' ? 'selected' : ''}>Everyone</option>
                          <option value="WATCHLIST_ONLY" ${pref.alertLevel === 'WATCHLIST_ONLY' ? 'selected' : ''}>Watchlist</option>
                          <option value="OFF" ${pref.alertLevel === 'OFF' ? 'selected' : ''}>Off</option>
                        </select>
                      </div>
                    </div>
                    <div class="ac-check-settings ${isOff ? 'hidden' : ''}">
                      <div class="ac-threshold">
                        <span class="label">Threshold:</span>
                        <input type="number" class="input tiny" min="1" max="100" value="${pref.thresholdCount}"
                          onchange="updateCheckThreshold('${ac.name}','${check.name}',this.value,'${pref.timeWindowSeconds}')">
                        <span class="label">alert in</span>
                        <input type="number" class="input tiny" min="10" max="3600" value="${pref.timeWindowSeconds}"
                          onchange="updateCheckThreshold('${ac.name}','${check.name}','${pref.thresholdCount}',this.value)">
                        <span class="label">seconds</span>
                      </div>
                    </div>
                  </div>
                `;
              }).join('')}
            </div>
          </div>
        `;
      });
    });

    if (!html) {
      html = `
        <div class="empty-state" style="text-align:center;padding:40px;color:var(--text-secondary)">
          <i class="fa-solid fa-shield-halved" style="font-size:48px;opacity:0.3;margin-bottom:16px"></i>
          <p>${anticheats.length === 0 ? 'No anticheat plugins detected' : 'No checks match your search'}</p>
        </div>
      `;
    }

    categoriesContainer.innerHTML = html;
  }

  function getCategoryIcon(category) {
    const icons = {
      combat: 'fa-solid fa-swords',
      movement: 'fa-solid fa-person-running',
      packet: 'fa-solid fa-network-wired',
      player: 'fa-solid fa-user',
      world: 'fa-solid fa-globe',
      misc: 'fa-solid fa-shapes',
      aim: 'fa-solid fa-crosshairs',
      velocity: 'fa-solid fa-wind',
      badpackets: 'fa-solid fa-bug',
      breaking: 'fa-solid fa-hammer',
      chat: 'fa-solid fa-comment',
      crash: 'fa-solid fa-bomb',
      elytra: 'fa-solid fa-feather',
      exploit: 'fa-solid fa-skull',
      groundspoof: 'fa-solid fa-shoe-prints',
      multiactions: 'fa-solid fa-layer-group',
      packetorder: 'fa-solid fa-list-ol',
      prediction: 'fa-solid fa-chart-line',
      scaffolding: 'fa-solid fa-cubes-stacked',
      sprint: 'fa-solid fa-bolt',
      timer: 'fa-solid fa-stopwatch',
      vehicle: 'fa-solid fa-car'
    };
    return icons[category.toLowerCase()] || 'fa-solid fa-shield-halved';
  }

  function renderWatchlist() {
    if (!dom.watchPlayers) return;
    const q = (dom.watchSearch?.value || '').trim().toLowerCase();
    // Match by UUID (from server) instead of internal ID
    const wlPlayers = [...state.watchlist].map(uuid =>
      state.players.find(p => p.uuid === uuid || p.id === uuid)
    ).filter(Boolean);
    const filtered = wlPlayers.filter(p => !q || `${p.name} ${p.platform}`.toLowerCase().includes(q));

    dom.watchPlayers.innerHTML = filtered.map(p => `
      <div class="drawer-row watchlist-item watching watchlist-row" data-player-id="${p.id}" style="border-radius:var(--radius);cursor:pointer" onclick="openDrawer('${p.id}')">
        <div class="meta"><b>${escapeHtml(p.name)}</b><small>${p.platform} | ${p.flags} flags</small></div>
        <div class="drawer-actions">
          <button class="mini" onclick="event.stopPropagation(); openPunishModal(null,'${p.id}')"><i class="fa-solid fa-bolt"></i></button>
          <button class="mini bad" onclick="event.stopPropagation(); removeWatch('${p.id}')"><i class="fa-solid fa-xmark"></i></button>
        </div>
      </div>
    `).join('') || `<div class="hintline">No players on watchlist.</div>`;

    dom.watchAlerts.innerHTML = state.watchAlerts.slice().sort((a, b) => b.t - a.t).slice(0, 20).map(a => `
      <div class="drawer-row" style="border-radius:var(--radius)">
        <div class="meta"><b>${escapeHtml(a.title)}</b><small>${escapeHtml(fmtShort(a.t))} | ${escapeHtml(a.detail)}</small></div>
        <span class="badge ${a.sev === 'ERROR' ? 'red' : a.sev === 'WARN' ? 'yellow' : 'blue'}"><i class="fa-solid fa-bell"></i></span>
      </div>
    `).join('') || `<div class="hintline">No alerts.</div>`;

    renderDashboard();
  }

  function renderWatchToastsToggle() {
    if (!dom.watchToastsToggle) return;
    const on = !!state.settings.watchToasts;
    dom.watchToastsToggle.classList.toggle('on', on);
    dom.watchToastsToggle.setAttribute('aria-pressed', on ? 'true' : 'false');
    if (dom.watchToastsHint) dom.watchToastsHint.textContent = on ? 'On' : 'Off';
  }

  function renderLogs() {
    if (!dom.logsBox) return;
    const q = (dom.logsSearch?.value || '').trim().toLowerCase();

    let flt = state.logs.filter(l => {
      if (state.logsFilters.mxOnly && !l.mx) return false;
      if (!state.logsFilters.sev[l.sev]) return false;
      if (state.logsFilters.types && l.type && !state.logsFilters.types[l.type]) return false;
      if (q && !`${l.sev} ${l.title} ${l.detail}`.toLowerCase().includes(q)) return false;
      return true;
    });

    flt = flt.sort((a, b) => b.t - a.t);
    const pageSize = state.logsFilters.pageSize || 100;
    const totalPages = Math.max(1, Math.ceil(flt.length / pageSize));
    state.logsFilters.page = Math.min(Math.max(1, state.logsFilters.page || 1), totalPages);
    const start = (state.logsFilters.page - 1) * pageSize;
    const slice = flt.slice(start, start + pageSize);

    if (dom.logsPageInfo) dom.logsPageInfo.textContent = `${state.logsFilters.page} / ${totalPages}`;
    if (dom.logsPageSize && String(dom.logsPageSize.value) !== String(pageSize)) dom.logsPageSize.value = String(pageSize);

    dom.logsBox.innerHTML = slice.map(l => {
      const pill = l.sev === 'ERROR' ? `<span class="pill err"><i class="fa-solid fa-circle-xmark"></i> ERROR</span>` :
        l.sev === 'WARN' ? `<span class="pill warn"><i class="fa-solid fa-triangle-exclamation"></i> WARN</span>` :
        `<span class="pill info"><i class="fa-solid fa-circle-info"></i> INFO</span>`;
      const dataAttr = l.playerId ? `data-player-id="${l.playerId}"` : '';
      return `
        <div class="logitem" ${dataAttr}><div class="logleft"><b>${escapeHtml(fmtClock(l.t))} | ${escapeHtml(l.title)}</b><small>${escapeHtml(l.detail)}</small></div>
          <div class="logright">${pill}<span class="pill mx"><i class="fa-solid fa-shield-halved"></i> MX</span></div>
        </div>
      `;
    }).join('') || `<div class="hintline" style="padding:14px">No logs.</div>`;

    if (!state.manualPaused && !state.autoPaused) {
      dom.logsBox.scrollTop = dom.logsBox.scrollHeight;
    }

    const paused = state.manualPaused || state.autoPaused;
    if (dom.logsStatus) {
      dom.logsStatus.className = `badge ${paused ? 'yellow' : 'green'}`;
      dom.logsStatus.innerHTML = paused ? `<i class="fa-solid fa-pause"></i> Paused` : `<i class="fa-solid fa-play"></i> Running`;
    }
    if (dom.logsBtn) {
      dom.logsBtn.innerHTML = paused ? `<i class="fa-solid fa-play"></i> Resume` : `<i class="fa-solid fa-pause"></i> Pause`;
    }
  }

  function renderTopUser() {
    if (!dom.topProfile) return;
    const user = state.currentUser;
    if (!user) {
      dom.topProfile.style.display = 'none';
      const rankContainer = document.getElementById('rankBadgeContainer');
      if (rankContainer) rankContainer.style.display = 'none';
      return;
    }
    dom.topProfile.style.display = 'flex';
    dom.topAvatar.onerror = () => { dom.topAvatar.src = `https://minotar.net/helm/${encodeURIComponent(user.name)}/64.png`; };
    dom.topAvatar.src = avatarUrl(user);
    dom.topName.textContent = user.name;
    dom.topPlatform.textContent = user.geyser || user.platform === 'Bedrock' ? 'Geyser' : 'Java';

    // Update dropdown elements
    const dropdownAvatar = document.getElementById('dropdownAvatar');
    const dropdownName = document.getElementById('dropdownName');
    const dropdownRank = document.getElementById('dropdownRank');
    if (dropdownAvatar) {
      dropdownAvatar.onerror = () => { dropdownAvatar.src = `https://minotar.net/helm/${encodeURIComponent(user.name)}/64.png`; };
      dropdownAvatar.src = avatarUrl(user);
    }
    if (dropdownName) dropdownName.textContent = user.name;

    // Update rank badge
    const rankContainer = document.getElementById('rankBadgeContainer');
    const rankBadge = document.getElementById('rankBadge');
    const rankName = document.getElementById('rankName');
    const rankTooltipRank = document.getElementById('rankTooltipRank');
    const rankTooltipWeight = document.getElementById('rankTooltipWeight');
    const rankTooltipPrefix = document.getElementById('rankTooltipPrefix');

    if (user.rank && rankContainer) {
      rankContainer.style.display = 'block';
      if (rankName) rankName.textContent = user.rank.name || 'Member';
      if (rankTooltipRank) rankTooltipRank.textContent = user.rank.name || 'Member';
      if (rankTooltipWeight) rankTooltipWeight.textContent = user.rank.weight || '0';
      if (rankTooltipPrefix) rankTooltipPrefix.textContent = user.rank.prefix || 'None';
      if (dropdownRank) dropdownRank.textContent = user.rank.name || 'Member';

      // Apply rank colors from config
      if (user.rank.color && rankBadge) {
        const hexColor = user.rank.color;
        const rgb = hexToRgb(hexColor);
        if (rgb) {
          rankBadge.style.setProperty('--rank-color', hexColor);
          rankBadge.style.setProperty('--rank-bg', `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.15)`);
          rankBadge.style.setProperty('--rank-border', `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.3)`);
          rankBadge.style.setProperty('--rank-glow', `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.25)`);
        }
      }
    } else if (rankContainer) {
      rankContainer.style.display = 'none';
    }
  }

  // Helper to convert hex color to RGB
  function hexToRgb(hex) {
    if (!hex) return null;
    hex = hex.replace('#', '');
    if (hex.length === 3) {
      hex = hex.split('').map(c => c + c).join('');
    }
    const result = /^([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : null;
  }

  function renderChatToggles() {
    if (!dom.togChat) return;
    const setToggle = (el, on) => el && el.classList.toggle('on', !!on);
    setToggle(dom.togChat, state.settings.chatDisabled);
    setToggle(dom.togSlow, state.settings.slowEnabled);
    if (dom.togChatHint) dom.togChatHint.textContent = state.settings.chatDisabled ? 'On' : 'Off';
    if (dom.togSlowHint) dom.togSlowHint.textContent = state.settings.slowEnabled ? 'On' : 'Off';
    if (dom.warnNotifyToggle) setToggle(dom.warnNotifyToggle, state.settings.warnNotify);
    if (dom.warnEscalateToggle) setToggle(dom.warnEscalateToggle, state.settings.warnAutoEscalate);
    if (dom.warnNotifyHint) dom.warnNotifyHint.textContent = state.settings.warnNotify ? 'On' : 'Off';
    if (dom.warnEscalateHint) dom.warnEscalateHint.textContent = state.settings.warnAutoEscalate ? 'On' : 'Off';
    setToggle(dom.muteChatToggle, state.settings.muteChat);
    setToggle(dom.muteMsgToggle, state.settings.muteMsg);
    setToggle(dom.muteSignsToggle, state.settings.muteSigns);
    setToggle(dom.muteBooksToggle, state.settings.muteBooks);
    setToggle(dom.muteBroadcastToggle, state.settings.muteBroadcast);
    setToggle(dom.muteVoiceToggle, state.settings.muteVoice);
    setToggle(dom.muteVoiceJoinToggle, state.settings.muteVoiceJoin);
    if (dom.anticheatReplaceToggle) setToggle(dom.anticheatReplaceToggle, state.settings.anticheatReplace);
    if (dom.anticheatReplaceHint) dom.anticheatReplaceHint.textContent = state.settings.anticheatReplace ? 'On' : 'Off';
    if (dom.slowSeconds) dom.slowSeconds.value = state.settings.slowSeconds;
  }

  function refreshUnsavedUI() {
    const hasUnsaved = Object.values(state.unsaved).some(Boolean);
    if (dom.unsavedChip) dom.unsavedChip.style.display = hasUnsaved ? 'flex' : 'none';
    if (dom.publishBtn) dom.publishBtn.disabled = !state.authenticated;
  }

  function markUnsaved(key, val) {
    state.unsaved[key] = val;
    refreshUnsavedUI();
  }

  // Export to window
  window.MX = window.MX || {};
  window.MX.ui = {
    initDom, getDom, renderAll, renderDashboard, renderPlayers, renderPunishments,
    renderTemplates, renderRules, renderMessages, renderIntegrations, renderAnticheat, renderWatchlist,
    renderLogs, renderChatToggles, renderTopUser, renderWatchToastsToggle, refreshUnsavedUI, markUnsaved,
    renderStaffSettings
  };
})();

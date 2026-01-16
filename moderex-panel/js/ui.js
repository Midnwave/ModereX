/* ============================================
   ModereX Control Panel - UI Rendering
   ============================================ */
(function() {
  const { $, $$, escapeHtml, fmtShort, fmtLong, fmtClock, avatarUrl } = window.MX.utils;
  const state = window.MX.state;

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
      aiFab: $('#aiFab'),
      aiPanel: $('#aiPanel'),
      aiBody: $('#aiBody'),
      aiInput: $('#aiInput'),
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
      anticheatNoHooksCard: $('#anticheatNoHooksCard'),
      anticheatBadges: $('#anticheatBadges'),
      anticheatSubtitle: $('#anticheatSubtitle')
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
    const activePun = state.punishments.filter(p => p.active && !p.revoked);
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
    dom.statOnlineHint.textContent = `${online}/${state.players.length} total • ${staffOnline} staff online`;

    const rows = state.activity.slice().sort((a, b) => b.t - a.t).slice(0, 7).map(a => `
      <tr><td>${escapeHtml(fmtShort(a.t))}</td><td>${escapeHtml(a.actor)}</td><td>${escapeHtml(a.action)}</td><td>${escapeHtml(a.target)}</td></tr>
    `).join('');
    dom.activityRows.innerHTML = rows || `<tr><td colspan="4" style="color:var(--muted)">No activity recorded.</td></tr>`;

    const wlPlayers = [...state.watchlist].map(id => state.players.find(p => p.id === id)).filter(Boolean);
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

    dom.playerRows.innerHTML = filtered.map(p => {
      const statusBadge = p.status === 'online' ? `<span class="badge green"><i class="fa-solid fa-circle"></i> Online</span>` :
        p.status === 'afk' ? `<span class="badge yellow"><i class="fa-solid fa-moon"></i> AFK</span>` :
        `<span class="badge gray"><i class="fa-regular fa-circle"></i> Offline</span>`;
      const platformBadge = p.platform === 'Bedrock' ? `<span class="badge blue"><i class="fa-solid fa-mobile-screen"></i> Bedrock</span>` : `<span class="badge gray"><i class="fa-solid fa-desktop"></i> Java</span>`;
      const flagsBadge = p.flags >= 4 ? `<span class="badge red"><i class="fa-solid fa-fire"></i> ${p.flags}</span>` : p.flags >= 2 ? `<span class="badge yellow"><i class="fa-solid fa-triangle-exclamation"></i> ${p.flags}</span>` : `<span class="badge gray"><i class="fa-solid fa-shield"></i> ${p.flags}</span>`;
      const watching = state.watchlist.has(p.id);

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
  }

  function renderPunishments() {
    if (!dom.punishRows) return;
    const q = (dom.punishSearch?.value || '').trim().toLowerCase();
    const filters = state.punishFilters;

    const rows = state.punishments.filter(pun => {
      if (!filters[pun.type]) return false;
      if (q) {
        const pl = state.players.find(p => p.id === pun.playerId);
        return `${pl?.name || ''} ${pun.reason || ''} ${pun.staff || ''} ${pun.id}`.toLowerCase().includes(q);
      }
      return true;
    }).sort((a, b) => b.createdAt - a.createdAt).slice(0, 100).map(pun => {
      const pl = state.players.find(p => p.id === pun.playerId);
      const name = pl?.name || 'Player';
      const avatarFallback = `https://minotar.net/helm/${encodeURIComponent(name)}/64.png`;
      const typeBadge = pun.type === 'BAN' ? `<span class="badge red"><i class="fa-solid fa-ban"></i> BAN</span>` :
        pun.type === 'MUTE' ? `<span class="badge yellow"><i class="fa-solid fa-volume-xmark"></i> MUTE</span>` :
        pun.type === 'KICK' ? `<span class="badge purple"><i class="fa-solid fa-person-walking-arrow-right"></i> KICK</span>` :
        `<span class="badge blue"><i class="fa-solid fa-triangle-exclamation"></i> WARN</span>`;
      const dur = pun.duration || 'instant';
      const canRevoke = !pun.revoked;
      const watching = state.watchlist.has(pun.playerId);

      return `
        <tr class="${watching ? 'watchlist-row' : ''}" data-player-id="${pun.playerId}" style="cursor:pointer">
          <td onclick="openDrawer('${pun.playerId}','${pun.id}')"><div class="pwrap"><div class="phead"><img src="${avatarUrl(pl || { name: name })}" alt="" onerror="this.onerror=null;this.src='${avatarFallback}'"></div><div><b style="font-size:13px">${escapeHtml(pl?.name || 'Unknown')}</b></div></div></td>
          <td>${escapeHtml(pun.id.slice(-8))}</td>
          <td>${typeBadge}</td>
          <td>${escapeHtml(pun.reason || 'No reason')}</td>
          <td>${escapeHtml(fmtLong(pun.createdAt))}</td>
          <td>${escapeHtml(dur)}</td>
          <td>${escapeHtml(pun.staff || 'System')}</td>
          <td style="text-align:right">
            <button class="mini" onclick="event.stopPropagation(); viewPunishmentDetails('${pun.id}')"><i class="fa-solid fa-eye"></i> Details</button>
            ${canRevoke ? `<button class="mini bad" onclick="event.stopPropagation(); revokePunishmentConfirm('${pun.id}')"><i class="fa-solid fa-xmark"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : `<span class="badge gray"><i class="fa-solid fa-check"></i> Closed</span>`}
          </td>
        </tr>
      `;
    }).join('');

    dom.punishRows.innerHTML = rows || `<tr><td colspan="8" style="color:var(--muted)">No punishments match filters.</td></tr>`;
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
        <td>${escapeHtml(t.reason)}</td>
        <td style="text-align:right">
          <button class="mini primary" onclick="editTemplateUI('${t.id}')"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
          <button class="mini bad" onclick="deleteTemplate('${t.id}')"><i class="fa-solid fa-trash"></i></button>
        </td>
      </tr>
    `).join('') || `<tr><td colspan="5" style="color:var(--muted)">No templates.</td></tr>`;
  }

  function renderRules() {
    if (!dom.rulesList) return;
    const anticheatRule = (state.settings.anticheatReplace && state.anticheat?.enabled) ? `
      <div class="card" style="margin:0">
        <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:10px">
          <div>
            <div style="display:flex;align-items:center;gap:10px">
              <b style="font-size:14px">Anticheat Alert Threshold</b>
              <span class="badge purple"><i class="fa-solid fa-shield-halved"></i> Grim</span>
            </div>
            <div class="hintline">Auto punish when an alert exceeds the threshold window.</div>
          </div>
          <button class="toggle ${state.anticheatRule?.enabled ? 'on' : ''}" onclick="toggleAnticheatRule()"><span class="toggle-thumb"></span></button>
        </div>
        <div class="block" style="margin-top:12px">
          <span class="badge gray">alerts</span>
          <input class="input" style="width:80px" type="number" min="1" value="${state.anticheatRule?.threshold || 6}" oninput="setAnticheatRule('threshold', this.value)">
          <span class="badge gray">in</span>
          <input class="input" style="width:70px" type="number" min="1" value="${state.anticheatRule?.windowMins || 2}" oninput="setAnticheatRule('windowMins', this.value)">
          <span class="badge gray">mins</span>
          <select class="input" style="width:120px" onchange="setAnticheatRule('action', this.value)">
            ${['none','warn','mute','ban'].map(opt => `<option value="${opt}" ${state.anticheatRule?.action === opt ? 'selected' : ''}>${opt}</option>`).join('')}
          </select>
        </div>
      </div>
    ` : '';

    dom.rulesList.innerHTML = anticheatRule + state.rules.map(r => {
      const thr = r.threshold || { hits: 1, windowMins: 10 };
      const descMap = {
        contains: 'Flags when a phrase appears in a message (comma separated).',
        regex: 'Uses a regular expression to detect a match.',
        caps: 'Flags messages with caps above the set percentage.',
        repeat: 'Detects repeated or similar messages across a window.',
        link: 'Detects external links in chat.'
      };
      const conds = (r.conditions || []).map((c, idx) => {
        const exactToggle = c.kind === 'contains' ? `
          <div class="toggle-wrap">
            <button class="toggle tiny ${c.match === 'exact' ? 'on' : ''}" onclick="toggleConditionExact('${r.id}', ${idx})"><span class="toggle-thumb"></span></button>
            <div class="toggle-meta"><div class="toggle-title">Exact only</div></div>
          </div>
        ` : '';
        const similarToggle = c.kind === 'repeat' ? `
          <div class="toggle-wrap spacer">
            <button class="toggle tiny ${c.similar ? 'on' : ''}" onclick="toggleConditionSimilar('${r.id}', ${idx})"><span class="toggle-thumb"></span></button>
            <div class="toggle-meta"><div class="toggle-title">Include similar</div></div>
          </div>
        ` : '';
        const inputField = c.kind === 'caps'
          ? `<input class="input" type="number" min="1" max="100" step="1" value="${escapeHtml(c.value || '65')}" oninput="setConditionValue('${r.id}', ${idx}, this.value)" style="max-width:140px"/>`
          : c.kind === 'repeat'
            ? `<span class="badge gray">Applies to all messages</span>`
            : `<input class="input" style="flex:1;min-width:220px" value="${escapeHtml(c.value || '')}" oninput="setConditionValue('${r.id}', ${idx}, this.value)" placeholder="Phrase(s), comma separated" ${c.kind === 'link' ? 'disabled' : ''}/>`;
        return `
          <div class="card" style="margin:0">
            <div class="condition-row" style="margin-top:0">
              <span class="badge blue"><i class="fa-solid fa-code-branch"></i> IF</span>
              <select class="input" style="width:160px" onchange="setConditionKind('${r.id}', ${idx}, this.value)">
                ${[['contains', 'Contains Phrase'], ['regex', 'Regex'], ['caps', 'Caps %'], ['repeat', 'Repeated Messages'], ['link', 'Link']].map(([k, l]) => `<option value="${k}" ${c.kind === k ? 'selected' : ''}>${l}</option>`).join('')}
              </select>
              ${inputField}
              ${exactToggle}
              ${similarToggle}
              <button class="mini bad delete" ${r.locked ? 'disabled' : ''} onclick="removeCondition('${r.id}', ${idx})"><i class="fa-solid fa-trash"></i></button>
            </div>
            <div class="hintline" style="margin-top:6px">${escapeHtml(descMap[c.kind] || 'Rule condition')}</div>
          </div>
        `;
      }).join('');

      return `
        <div class="card" style="margin:0">
          <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:10px">
            <div>
              <div style="display:flex;align-items:center;gap:10px">
                <input class="input" style="max-width:240px" value="${escapeHtml(r.name)}" oninput="setRuleName('${r.id}', this.value)" ${r.locked ? 'disabled' : ''}/>
                ${r.enabled ? `<span class="badge green"><i class="fa-solid fa-check"></i> Active</span>` : `<span class="badge gray"><i class="fa-solid fa-pause"></i> Inactive</span>`}
                ${r.locked ? `<span class="badge gray"><i class="fa-solid fa-lock"></i> Default</span>` : ''}
              </div>
              <div class="hintline">${escapeHtml(r.notes || 'Applies before the message is sent.')}</div>
            </div>
            <div style="display:flex;gap:10px;align-items:center">
              <button class="toggle ${r.enabled ? 'on' : ''}" onclick="toggleRule('${r.id}')"><span class="toggle-thumb"></span></button>
              <button class="mini bad delete" ${r.locked ? 'disabled' : ''} onclick="deleteRule('${r.id}')"><i class="fa-solid fa-trash"></i></button>
            </div>
          </div>
          <div style="margin-top:12px" class="grid cols-2">
            <div class="block">
              <b style="font-size:12px">Auto Punish</b>
              <select class="input" style="width:140px" onchange="setRuleAction('${r.id}', this.value)">
                ${['none', 'warn', 'mute', 'kick', 'ban'].map(k => `<option value="${k}" ${r.action?.kind === k ? 'selected' : ''}>${k}</option>`).join('')}
              </select>
              ${r.action?.kind && r.action.kind !== 'none' ? `<input class="input" style="flex:1" value="${escapeHtml(r.action?.extra || '')}" oninput="setRuleActionExtra('${r.id}', this.value)" placeholder="Reason"/>` : ''}
              ${['warn','mute','ban'].includes(r.action?.kind) ? `<input class="input" style="max-width:120px" value="${escapeHtml(r.action?.duration || '')}" oninput="setRuleActionDuration('${r.id}', this.value)" placeholder="Duration"/>` : ''}
            </div>
            <div class="block">
              <b style="font-size:12px">Block Message</b>
              <button class="toggle ${r.block ? 'on' : ''}" onclick="toggleRuleBlock('${r.id}')"><span class="toggle-thumb"></span></button>
              <div class="hintline" style="margin-top:0">Prevents the message from sending.</div>
            </div>
          </div>
          <div class="hintline" style="margin-top:6px">Auto punish applies when the rule triggers.</div>
          <div style="margin-top:12px">
            <div class="hintline" style="margin-top:0"><b>Conditions</b></div>
            <div style="margin-top:8px;display:flex;flex-direction:column;gap:10px">
              ${conds || `<div class="hintline">No conditions.</div>`}
              <button class="mini primary" ${r.locked ? 'disabled' : ''} onclick="addCondition('${r.id}')"><i class="fa-solid fa-plus"></i> Add Condition</button>
            </div>
          </div>
          <div style="margin-top:12px" class="block">
            <span class="badge gray">hits</span><input class="input" type="number" min="1" value="${thr.hits}" style="width:70px" oninput="setRuleThreshold('${r.id}', 'hits', this.value)">
            <span class="badge gray">mins</span><input class="input" type="number" min="1" value="${thr.windowMins}" style="width:70px" oninput="setRuleThreshold('${r.id}', 'windowMins', this.value)">
          </div>
        </div>
      `;
    }).join('') || `<div class="hintline">No rules. Click "Add Rule" to create one.</div>`;
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
    if (dom.anticheatList) {
      dom.anticheatList.innerHTML = (state.integrations?.anticheats || []).map(a => `<span class="badge gray">${escapeHtml(a)}</span>`).join('');
    }

    dom.webhookPreview.innerHTML = escapeHtml(`ModereX Case Created\nPlayer: ${state.players[0]?.name || 'Player'}\nAction: BAN\nReason: Violation\nStaff: Admin`);
  }

  function renderAnticheat() {
    if (!dom.anticheatRows) return;

    const checks = state.anticheatChecks || [];
    const prefs = state.anticheatPreferences || {};
    const enabledAnticheats = state.enabledAnticheats || [];

    // Show/hide no hooks card
    const hasHooks = enabledAnticheats.length > 0;
    if (dom.anticheatConfig) dom.anticheatConfig.style.display = hasHooks ? 'block' : 'none';
    if (dom.anticheatNoHooksCard) dom.anticheatNoHooksCard.style.display = hasHooks ? 'none' : 'block';

    // Update subtitle with detected anticheat
    if (dom.anticheatSubtitle && enabledAnticheats.length > 0) {
      dom.anticheatSubtitle.textContent = `Configure ${enabledAnticheats.join(', ')} alert routing and ModereX thresholds.`;
    }

    // Render anticheat badges
    if (dom.anticheatBadges) {
      const colors = { 'Grim': 'purple', 'Vulcan': 'red', 'Matrix': 'blue', 'Spartan': 'orange', 'NCP': 'green', 'Themis': 'cyan', 'FoxAddition': 'yellow', 'LightAC': 'pink' };
      dom.anticheatBadges.innerHTML = enabledAnticheats.map(ac =>
        `<span class="badge ${colors[ac] || 'gray'}"><i class="fa-solid fa-shield-halved"></i> ${escapeHtml(ac)}</span>`
      ).join('');
    }

    if (!hasHooks) return;

    const q = (dom.anticheatSearch?.value || '').trim().toLowerCase();
    const filtered = checks.filter(c => !q || c.checkName.toLowerCase().includes(q) || c.anticheat.toLowerCase().includes(q));

    dom.anticheatRows.innerHTML = filtered.map(c => {
      const key = c.key || `${c.anticheat.toLowerCase()}:${c.checkName.toLowerCase()}`;
      const pref = prefs[key] || { chatAlerts: true, toastAlerts: true, thresholdCount: 6, timeWindowMins: 2, autoPunish: 'none' };

      return `
        <tr>
          <td><b>${escapeHtml(c.checkName)}</b></td>
          <td>
            <button class="toggle ${pref.chatAlerts ? 'on' : ''}" onclick="updateAnticheatPref('${escapeHtml(key)}', 'chatAlerts', ${!pref.chatAlerts})">
              <span class="toggle-thumb"></span>
            </button>
          </td>
          <td>
            <button class="toggle ${pref.toastAlerts ? 'on' : ''}" onclick="updateAnticheatPref('${escapeHtml(key)}', 'toastAlerts', ${!pref.toastAlerts})">
              <span class="toggle-thumb"></span>
            </button>
          </td>
          <td>
            <div class="block" style="margin-top:0;gap:6px">
              <input class="input" style="width:60px" type="number" min="1" max="100" value="${pref.thresholdCount || 6}"
                onchange="updateAnticheatPref('${escapeHtml(key)}', 'thresholdCount', parseInt(this.value) || 6)">
              <span style="font-size:12px;color:var(--text-secondary)">in</span>
              <input class="input" style="width:60px" type="number" min="1" max="60" value="${pref.timeWindowMins || 2}"
                onchange="updateAnticheatPref('${escapeHtml(key)}', 'timeWindowMins', parseInt(this.value) || 2)">
              <span style="font-size:12px;color:var(--text-secondary)">mins</span>
            </div>
          </td>
          <td>
            <select class="input" style="width:100px" onchange="updateAnticheatPref('${escapeHtml(key)}', 'autoPunish', this.value)">
              ${['none', 'warn', 'mute', 'kick', 'ban'].map(opt =>
                `<option value="${opt}" ${(pref.autoPunish || 'none') === opt ? 'selected' : ''}>${opt}</option>`
              ).join('')}
            </select>
          </td>
        </tr>
      `;
    }).join('') || `<tr><td colspan="5" style="color:var(--muted)">No checks detected. Run the server with an anticheat to see checks.</td></tr>`;
  }

  function renderWatchlist() {
    if (!dom.watchPlayers) return;
    const q = (dom.watchSearch?.value || '').trim().toLowerCase();
    const wlPlayers = [...state.watchlist].map(id => state.players.find(p => p.id === id)).filter(Boolean);
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
      return;
    }
    dom.topProfile.style.display = 'flex';
    dom.topAvatar.onerror = () => { dom.topAvatar.src = `https://minotar.net/helm/${encodeURIComponent(user.name)}/64.png`; };
    dom.topAvatar.src = avatarUrl(user);
    dom.topName.textContent = user.name;
    dom.topPlatform.textContent = user.geyser || user.platform === 'Bedrock' ? 'Geyser' : 'Java';
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
    dom.slowSeconds.value = state.settings.slowSeconds;
  }

  function refreshUnsavedUI() {
    const hasUnsaved = Object.values(state.unsaved).some(Boolean);
    dom.unsavedChip.style.display = hasUnsaved ? 'flex' : 'none';
    dom.publishBtn.disabled = !state.authenticated;
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
    renderLogs, renderChatToggles, renderTopUser, renderWatchToastsToggle, refreshUnsavedUI, markUnsaved
  };
})();

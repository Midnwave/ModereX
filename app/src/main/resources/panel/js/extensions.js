/* ============================================
   ModereX Control Panel - Feature Extensions
   ============================================
   Adds support for new backend features:
   - Integrations (LuckPerms, Anticheat, Moderation plugins)
   - Kick All functionality
   - External punishments
   - Device trust management
   */

(function() {
  'use strict';

  // ===== INTEGRATIONS =====

  window.renderIntegrations = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      // Request integration status from backend
      ws.send('GET_ANTICHEAT_INFO');
      ws.send('GET_LUCKPERMS_STATUS');
      ws.send('GET_GEYSER_STATUS');
      ws.send('GET_MODERATION_PLUGINS');
      ws.send('GET_SPARK_STATUS');
      ws.send('GET_CITIZENS_STATUS');
      ws.send('GET_ESSENTIALS_STATUS');
      ws.send('GET_PLACEHOLDERAPI_STATUS');
    }
  };

  window.renderAnticheatIntegrations = function(plugins) {
    const container = document.getElementById('anticheatList');
    if (!container) return;

    if (!plugins || plugins.length === 0) {
      container.innerHTML = '<div style="color:var(--text-secondary);font-size:13px">No anticheat plugins detected</div>';
      return;
    }

    container.innerHTML = plugins.map(plugin => `
      <div class="integration-item" style="display:flex;align-items:center;justify-content:space-between;padding:12px;border:1px solid var(--border);border-radius:var(--radius);background:rgba(0,0,0,0.2)">
        <div style="display:flex;align-items:center;gap:12px">
          <div style="width:36px;height:36px;border-radius:var(--radius);background:linear-gradient(135deg,var(--warn),var(--bad));display:flex;align-items:center;justify-content:center">
            <i class="fa-solid fa-shield-halved" style="color:#fff"></i>
          </div>
          <div>
            <div style="font-weight:600">${escapeHtml(plugin.name)}</div>
            <div style="font-size:12px;color:var(--text-secondary)">${plugin.alertsEnabled ? 'Alerts enabled' : 'No alerts'}</div>
          </div>
        </div>
        <span class="badge good"><i class="fa-solid fa-check"></i> Active</span>
      </div>
    `).join('');
  };

  window.renderModerationPlugins = function(plugins) {
    const container = document.getElementById('moderationPluginsList');
    if (!container) return;

    if (!plugins || plugins.length === 0) {
      container.innerHTML = '<div style="color:var(--text-secondary);font-size:13px">No external moderation plugins detected</div>';
      return;
    }

    container.innerHTML = plugins.map(plugin => `
      <div class="integration-item" style="display:flex;align-items:center;justify-content:space-between;padding:12px;border:1px solid var(--border);border-radius:var(--radius);background:rgba(0,0,0,0.2);margin-bottom:12px">
        <div style="display:flex;align-items:center;gap:12px;flex:1">
          <div style="width:36px;height:36px;border-radius:var(--radius);background:linear-gradient(135deg,var(--primary),var(--accent));display:flex;align-items:center;justify-content:center">
            <i class="fa-solid fa-gavel" style="color:#fff"></i>
          </div>
          <div style="flex:1">
            <div style="font-weight:600">${escapeHtml(plugin.name)}</div>
            <div style="font-size:12px;color:var(--text-secondary)">
              ${plugin.punishmentCount > 0 ? `${plugin.punishmentCount} punishment(s) available` : 'No punishment history available'}
            </div>
          </div>
        </div>
        <div style="display:flex;gap:8px;align-items:center">
          <span class="badge good"><i class="fa-solid fa-check"></i> Detected</span>
          ${plugin.punishmentCount > 0 ? `<button class="btn mini" onclick="importFromPlugin('${escapeHtml(plugin.name)}')"><i class="fa-solid fa-download"></i> Import History</button>` : ''}
        </div>
      </div>
    `).join('');
  };

  window.renderLuckPermsStatus = function(status) {
    const badge = document.getElementById('luckPermsStatus');
    const details = document.getElementById('luckPermsDetails');
    if (!badge) return;

    if (status && status.available) {
      badge.className = 'badge good';
      badge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
      if (details) {
        details.innerHTML = `
          <div><i class="fa-solid fa-check" style="color:var(--good)"></i> Prefix/suffix support enabled</div>
          <div style="margin-top:4px"><i class="fa-solid fa-check" style="color:var(--good)"></i> Permission checks integrated</div>
        `;
      }
    } else {
      badge.className = 'badge gray';
      badge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
      if (details) {
        details.innerHTML = 'LuckPerms not found on this server';
      }
    }
  };

  window.renderGeyserStatus = function(status) {
    const geyserBadge = document.getElementById('geyserStatus');
    const geyserDetails = document.getElementById('geyserDetails');
    const floodgateBadge = document.getElementById('floodgateStatus');
    const floodgateDetails = document.getElementById('floodgateDetails');

    // Update state
    const state = window.MX?.state;
    if (state) {
      state.integrations = state.integrations || {};
      state.integrations.geyserDetected = status?.geyserAvailable || false;
      state.integrations.floodgateDetected = status?.floodgateAvailable || false;
      state.integrations.geyserVersion = status?.geyserVersion || null;
      state.integrations.floodgateVersion = status?.floodgateVersion || null;
    }

    // Update Geyser badge
    if (geyserBadge) {
      if (status?.geyserAvailable) {
        geyserBadge.className = 'badge good';
        geyserBadge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
        if (geyserDetails) {
          geyserDetails.textContent = `v${status.geyserVersion || 'Unknown'}`;
        }
      } else {
        geyserBadge.className = 'badge gray';
        geyserBadge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
        if (geyserDetails) {
          geyserDetails.textContent = 'Bedrock-Java proxy';
        }
      }
    }

    // Update Floodgate badge
    if (floodgateBadge) {
      if (status?.floodgateAvailable) {
        floodgateBadge.className = 'badge good';
        floodgateBadge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
        if (floodgateDetails) {
          floodgateDetails.textContent = `v${status.floodgateVersion || 'Unknown'}`;
        }
      } else {
        floodgateBadge.className = 'badge gray';
        floodgateBadge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
        if (floodgateDetails) {
          floodgateDetails.textContent = 'Bedrock authentication';
        }
      }
    }
  };

  window.renderSparkStatus = function(status) {
    const badge = document.getElementById('sparkStatus');
    const details = document.getElementById('sparkDetails');

    // Update state
    const state = window.MX?.state;
    if (state) {
      state.integrations = state.integrations || {};
      state.integrations.sparkDetected = status?.available || false;
      state.integrations.sparkVersion = status?.version || null;
    }

    if (badge) {
      if (status?.available) {
        badge.className = 'badge good';
        badge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
        if (details) {
          details.textContent = `v${status.version || 'Unknown'}`;
        }
      } else {
        badge.className = 'badge gray';
        badge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
        if (details) {
          details.textContent = 'Performance profiler for diagnostics';
        }
      }
    }
  };

  window.renderCitizensStatus = function(status) {
    const badge = document.getElementById('citizensStatus');
    const details = document.getElementById('citizensDetails');

    // Update state
    const state = window.MX?.state;
    if (state) {
      state.integrations = state.integrations || {};
      state.integrations.citizensDetected = status?.available || false;
      state.integrations.citizensVersion = status?.version || null;
    }

    if (badge) {
      if (status?.available) {
        badge.className = 'badge good';
        badge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
        if (details) {
          details.textContent = `v${status.version || 'Unknown'} - Replay system enabled`;
        }
      } else {
        badge.className = 'badge gray';
        badge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
        if (details) {
          details.textContent = 'Install Citizens to enable replay playback';
        }
      }
    }
  };

  window.renderEssentialsStatus = function(status) {
    const badge = document.getElementById('essentialsStatus');
    const details = document.getElementById('essentialsDetails');
    const nickSettings = document.getElementById('essentialsNickSettings');

    // Update state
    const state = window.MX?.state;
    if (state) {
      state.integrations = state.integrations || {};
      state.integrations.essentialsDetected = status?.available || false;
      state.integrations.essentialsVersion = status?.version || null;
    }

    if (badge) {
      if (status?.available) {
        badge.className = 'badge good';
        badge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
        if (details) {
          details.textContent = `v${status.version || 'Unknown'}`;
        }
        if (nickSettings) {
          nickSettings.style.display = 'block';
        }
      } else {
        badge.className = 'badge gray';
        badge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
        if (details) {
          details.textContent = 'Nickname integration not available';
        }
        if (nickSettings) {
          nickSettings.style.display = 'none';
        }
      }
    }
  };

  window.renderPlaceholderAPIStatus = function(status) {
    const badge = document.getElementById('placeholderAPIStatus');

    // Update state
    const state = window.MX?.state;
    if (state) {
      state.integrations = state.integrations || {};
      state.integrations.placeholderAPIDetected = status?.available || false;
    }

    if (badge) {
      if (status?.available) {
        badge.className = 'badge good';
        badge.innerHTML = '<i class="fa-solid fa-check"></i> Active';
      } else {
        badge.className = 'badge gray';
        badge.innerHTML = '<i class="fa-solid fa-xmark"></i> Not detected';
      }
    }
  };

  window.importFromPlugin = function(pluginName) {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot import - not connected to server');
      return;
    }

    // Show confirmation dialog (you could add a proper modal here)
    if (!confirm(`Import punishment history from ${pluginName}? This will add all punishments to ModereX.`)) {
      return;
    }

    ws.send('IMPORT_EXTERNAL_PUNISHMENTS', {
      plugin: pluginName,
      playerUuid: null // null means import for all players
    });

    window.toast('info', 'Import Started', `Importing punishments from ${pluginName}...`);
  };

  // ===== KICK ALL =====

  window.kickAllPlayers = function() {
    const reasonInput = document.getElementById('kickAllReason');
    const reason = reasonInput?.value?.trim() || 'Server maintenance';

    if (!confirm(`Kick ALL players from the server?\n\nReason: ${reason}\n\nThis action cannot be undone.`)) {
      return;
    }

    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot kick players - not connected to server');
      return;
    }

    ws.send('KICK_ALL', { reason });

    window.toast('info', 'Kick All Executed', `Kicking all players: ${reason}`);
  };

  // ===== DEVICE TRUST =====

  window.toggleDeviceTrust = function() {
    const toggle = document.getElementById('deviceTrustEnabled');
    if (!toggle) return;

    const isEnabled = toggle.classList.contains('on');
    const newState = !isEnabled;

    toggle.classList.toggle('on', newState);

    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { deviceTrustEnabled: newState });
    }

    // Save locally
    try {
      const settings = JSON.parse(localStorage.getItem('mx_user_settings') || '{}');
      settings.deviceTrustEnabled = newState;
      localStorage.setItem('mx_user_settings', JSON.stringify(settings));
    } catch (e) {}

    window.toast('ok', 'Device Trust', newState ? 'Auto sign-in enabled' : 'Auto sign-in disabled');
  };

  // ===== EXTERNAL PUNISHMENTS IN PLAYER DRAWER =====

  window.showExternalPunishments = function(playerUuid) {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;

    ws.send('GET_EXTERNAL_PUNISHMENTS', { playerUuid });
  };

  window.renderExternalPunishments = function(punishments) {
    // Add a new section to the player drawer if it doesn't exist
    const drawerBody = document.querySelector('.drawer-body');
    if (!drawerBody) return;

    let extSection = document.querySelector('.drawer-sec.external-punishments');
    if (!extSection) {
      extSection = document.createElement('div');
      extSection.className = 'drawer-sec external-punishments';
      extSection.innerHTML = '<h3>External Punishments</h3><div class="box" id="drawerExternal"></div>';
      drawerBody.insertBefore(extSection, drawerBody.firstChild);
    }

    const container = document.getElementById('drawerExternal');
    if (!container) return;

    if (!punishments || Object.keys(punishments).length === 0) {
      container.innerHTML = '<div style="color:var(--muted);font-size:13px">No external punishment history found</div>';
      return;
    }

    let html = '';
    for (const [plugin, puns] of Object.entries(punishments)) {
      html += `<div style="margin-bottom:16px">
        <div style="font-weight:600;color:var(--primary-light);margin-bottom:8px">
          <i class="fa-solid fa-plug"></i> ${escapeHtml(plugin)}
        </div>`;

      puns.forEach(pun => {
        const typeColor = {
          'BAN': 'var(--bad)',
          'MUTE': 'var(--warn)',
          'KICK': 'var(--primary-light)',
          'WARN': 'var(--warn)'
        }[pun.type] || 'var(--text-secondary)';

        html += `<div style="padding:8px;border-left:3px solid ${typeColor};background:rgba(0,0,0,0.2);margin-bottom:8px;border-radius:4px">
          <div style="display:flex;justify-content:space-between;margin-bottom:4px">
            <span style="font-weight:600;color:${typeColor}">${pun.type}</span>
            <span style="font-size:12px;color:var(--text-secondary)">${pun.active ? '<span class="badge warn">Active</span>' : '<span class="badge gray">Expired</span>'}</span>
          </div>
          <div style="font-size:13px;color:var(--text-secondary)">
            <div>${escapeHtml(pun.reason)}</div>
            <div style="margin-top:4px;font-size:11px">By: ${escapeHtml(pun.staff)} | ${new Date(pun.createdAt).toLocaleString()}</div>
          </div>
        </div>`;
      });

      html += '</div>';
    }

    container.innerHTML = html;
  };

  // ===== NAV FIX FOR ACTIONS PAGE =====

  const originalGo = window.go;
  window.go = function(page) {
    // Map 'settings' to 'actions' for backward compatibility
    if (page === 'settings') page = 'actions';

    // Call original navigation function
    if (originalGo) {
      originalGo(page);
    }

    // Handle integrations page
    if (page === 'integrations') {
      renderIntegrations();
    }
  };

  // ===== WEBSOCKET MESSAGE HANDLERS =====

  // Register handlers using the proper WebSocket API
  function registerHandlers() {
    const ws = window.MX?.ws;
    if (!ws) {
      // Wait for WebSocket module to be ready
      setTimeout(registerHandlers, 100);
      return;
    }

    ws.on('ANTICHEAT_INFO', (data) => {
      if (data && data.plugins) {
        // Store hooked anticheats in state for UI rendering
        const state = window.MX?.state;
        if (state) {
          state.integrations = state.integrations || {};
          state.integrations.hookedAnticheats = data.plugins;
          // Also update anticheat state for consistency with anticheat page
          state.anticheat = state.anticheat || {};
          if (!state.anticheat.anticheats || state.anticheat.anticheats.length === 0) {
            // Only update if we don't already have detailed data from ANTICHEAT_ALERTS
            state.anticheat.anticheats = data.plugins.map(p => ({
              name: p.name,
              checks: [],
              categories: {}
            }));
          }
        }
        renderAnticheatIntegrations(data.plugins);
        // Also update the integrations page UI
        if (window.MX?.ui?.renderIntegrations) {
          window.MX.ui.renderIntegrations();
        }
        if (window.MX?.ui?.renderAnticheat) {
          window.MX.ui.renderAnticheat();
        }
      }
    });

    ws.on('LUCKPERMS_STATUS', (data) => {
      if (data) {
        // Store LuckPerms status in state
        const state = window.MX?.state;
        if (state) {
          state.integrations = state.integrations || {};
          state.integrations.luckPermsDetected = data.available;
        }
        renderLuckPermsStatus(data);
      }
    });

    ws.on('GEYSER_STATUS', (data) => {
      if (data) {
        renderGeyserStatus(data);
      }
    });

    ws.on('SPARK_STATUS', (data) => {
      if (data) {
        renderSparkStatus(data);
      }
    });

    ws.on('CITIZENS_STATUS', (data) => {
      if (data) {
        renderCitizensStatus(data);
      }
    });

    ws.on('ESSENTIALS_STATUS', (data) => {
      if (data) {
        renderEssentialsStatus(data);
      }
    });

    ws.on('PLACEHOLDERAPI_STATUS', (data) => {
      if (data) {
        renderPlaceholderAPIStatus(data);
      }
    });

    ws.on('MODERATION_PLUGINS', (data) => {
      if (data && data.plugins) {
        renderModerationPlugins(data.plugins);
      }
    });

    ws.on('EXTERNAL_PUNISHMENTS', (data) => {
      if (data && data.punishments) {
        renderExternalPunishments(data.punishments);
      }
    });

    ws.on('KICK_ALL', (data) => {
      window.MX.sounds?.notification();
      window.toast('info', 'Players Kicked', `${data.count} player(s) kicked by ${data.staffName}`);
    });

    ws.on('IMPORT_RESULT', (data) => {
      if (data && data.count !== undefined) {
        window.MX.sounds?.success();
        window.toast('ok', 'Import Complete', `Imported ${data.count} punishment(s) from ${data.plugin}`);
      }
    });

    console.log('[ModereX Extensions] Message handlers registered');
  }

  // Register handlers when module loads
  registerHandlers();

  // Utility function for escaping HTML
  function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  // ===== SERVER RULES =====

  // State for rules
  let rulesData = {
    rules: [],
    requireAcceptance: false,
    version: '1.0'
  };

  window.renderRules = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_RULES');
    }
  };

  window.renderRulesUI = function(data) {
    rulesData = data || rulesData;

    // Update settings UI
    const versionInput = document.getElementById('rulesVersion');
    const requireToggle = document.getElementById('requireAcceptanceToggle');

    if (versionInput) versionInput.value = rulesData.version || '1.0';
    if (requireToggle) {
      requireToggle.classList.toggle('on', rulesData.requireAcceptance);
      requireToggle.setAttribute('aria-pressed', rulesData.requireAcceptance);
    }

    // Render rules list
    const container = document.getElementById('rulesListContainer');
    if (!container) return;

    if (!rulesData.rules || rulesData.rules.length === 0) {
      container.innerHTML = '<div style="color:var(--text-secondary);font-size:13px;padding:20px;text-align:center">No rules configured. Click "Add Rule" to create your first rule.</div>';
      return;
    }

    container.innerHTML = rulesData.rules.map((rule, index) => {
      const categoryColor = getCategoryColor(rule.category);
      return `
        <div class="rule-item" data-order="${rule.order}" style="display:flex;align-items:flex-start;gap:12px;padding:14px;border:1px solid var(--border);border-radius:var(--radius);background:rgba(0,0,0,0.2);margin-bottom:10px;cursor:pointer" onclick="editRule(${rule.order})">
          <div style="min-width:32px;height:32px;border-radius:var(--radius);background:linear-gradient(135deg,${categoryColor}40,${categoryColor}20);display:flex;align-items:center;justify-content:center;font-weight:700;color:${categoryColor}">${rule.order}</div>
          <div style="flex:1">
            <div style="font-weight:600;margin-bottom:4px">${escapeHtml(rule.title)}</div>
            <div style="font-size:13px;color:var(--text-secondary)">${escapeHtml(rule.description)}</div>
            <div style="margin-top:8px">
              <span class="badge" style="background:${categoryColor}20;color:${categoryColor};font-size:10px">${escapeHtml(rule.category || 'general')}</span>
            </div>
          </div>
          <button class="btn mini bad" onclick="event.stopPropagation();deleteServerRule(${rule.order})" title="Delete rule"><i class="fa-solid fa-trash"></i></button>
        </div>
      `;
    }).join('');
  };

  function getCategoryColor(category) {
    if (!category) return '#ffc107';
    switch (category.toLowerCase()) {
      case 'general': return '#ffc107';
      case 'chat': return '#00bcd4';
      case 'gameplay': return '#4caf50';
      case 'pvp': return '#f44336';
      case 'building': return '#ff9800';
      default: return '#9e9e9e';
    }
  }

  window.addNewRule = function() {
    const newRule = {
      order: (rulesData.rules?.length || 0) + 1,
      title: 'New Rule',
      description: 'Enter rule description...',
      category: 'general'
    };

    openRuleEditor(newRule, true);
  };

  window.editRule = function(order) {
    const rule = rulesData.rules.find(r => r.order === order);
    if (rule) {
      openRuleEditor(rule, false);
    }
  };

  function openRuleEditor(rule, isNew) {
    const modal = document.createElement('div');
    modal.className = 'overlay';
    modal.id = 'ruleEditorOverlay';
    modal.style.display = 'flex';
    modal.innerHTML = `
      <div class="modal" style="max-width:500px" onclick="event.stopPropagation()">
        <div class="modal-top">
          <b>${isNew ? 'Add Rule' : 'Edit Rule'}</b>
          <button class="mini" onclick="closeRuleEditor()"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="hintline" style="margin-top:0">Rule Title</div>
          <input type="text" class="input" id="ruleTitle" value="${escapeHtml(rule.title)}" placeholder="Rule title...">

          <div class="hintline">Description</div>
          <textarea class="input" id="ruleDescription" rows="3" placeholder="Describe the rule...">${escapeHtml(rule.description)}</textarea>

          <div class="hintline">Category</div>
          <select class="input" id="ruleCategory">
            <option value="general" ${rule.category === 'general' ? 'selected' : ''}>General</option>
            <option value="chat" ${rule.category === 'chat' ? 'selected' : ''}>Chat</option>
            <option value="gameplay" ${rule.category === 'gameplay' ? 'selected' : ''}>Gameplay</option>
            <option value="pvp" ${rule.category === 'pvp' ? 'selected' : ''}>PvP</option>
            <option value="building" ${rule.category === 'building' ? 'selected' : ''}>Building</option>
          </select>

          <div class="block" style="margin-top:20px">
            <button class="btn primary" onclick="saveRuleFromEditor(${rule.order}, ${isNew})"><i class="fa-solid fa-check"></i> ${isNew ? 'Add' : 'Save'}</button>
            <button class="btn" onclick="closeRuleEditor()">Cancel</button>
          </div>
        </div>
      </div>
    `;
    modal.onclick = closeRuleEditor;
    document.body.appendChild(modal);
  }

  window.closeRuleEditor = function() {
    const overlay = document.getElementById('ruleEditorOverlay');
    if (overlay) overlay.remove();
  };

  window.saveRuleFromEditor = function(order, isNew) {
    const title = document.getElementById('ruleTitle')?.value?.trim();
    const description = document.getElementById('ruleDescription')?.value?.trim();
    const category = document.getElementById('ruleCategory')?.value || 'general';

    if (!title) {
      window.toast('warn', 'Invalid', 'Rule title is required');
      return;
    }

    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot save rule - not connected to server');
      return;
    }

    if (isNew) {
      ws.send('ADD_RULE', { title, description, category });
    } else {
      // Update existing rule in local state
      const ruleIndex = rulesData.rules.findIndex(r => r.order === order);
      if (ruleIndex >= 0) {
        rulesData.rules[ruleIndex] = { order, title, description, category };
        ws.send('UPDATE_RULES', { rules: rulesData.rules });
      }
    }

    closeRuleEditor();
  };

  window.deleteServerRule = function(order) {
    window.openConfirmPanel({
      title: 'Delete Server Rule',
      body: 'Are you sure you want to delete this rule? This cannot be undone.',
      confirmText: 'Delete',
      onConfirm: () => {
        const ws = window.MX?.ws;
        if (!ws || !ws.isConnected()) {
          window.toast('warn', 'Not Connected', 'Cannot delete rule - not connected to server');
          return;
        }
        ws.send('DELETE_RULE', { order });
      }
    });
  };

  window.saveRules = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot save rules - not connected to server');
      return;
    }

    const version = document.getElementById('rulesVersion')?.value?.trim() || '1.0';

    // Save settings
    ws.send('UPDATE_RULES_SETTINGS', {
      requireAcceptance: rulesData.requireAcceptance,
      version: version
    });

    // Save rules
    ws.send('UPDATE_RULES', { rules: rulesData.rules });

    window.toast('ok', 'Saved', 'Rules saved successfully');
  };

  window.toggleRequireAcceptance = function() {
    rulesData.requireAcceptance = !rulesData.requireAcceptance;
    const toggle = document.getElementById('requireAcceptanceToggle');
    if (toggle) {
      toggle.classList.toggle('on', rulesData.requireAcceptance);
      toggle.setAttribute('aria-pressed', rulesData.requireAcceptance);
    }
  };

  // Override go function to handle rules page
  const originalGoExt = window.go;
  window.go = function(page) {
    if (originalGoExt) {
      originalGoExt(page);
    }

    if (page === 'rules') {
      renderRules();
    }
  };

  // Add WebSocket handlers for rules
  function registerRulesHandlers() {
    const ws = window.MX?.ws;
    if (!ws) {
      setTimeout(registerRulesHandlers, 100);
      return;
    }

    ws.on('RULES_DATA', (data) => {
      if (data) {
        renderRulesUI(data);
      }
    });

    ws.on('RULES_UPDATE', (data) => {
      if (data) {
        renderRulesUI(data);
        window.toast('info', 'Rules Updated', 'Server rules have been updated');
      }
    });

    ws.on('RULES_ACCEPTED', (data) => {
      if (data) {
        window.toast('info', 'Rules Accepted', `A player has accepted the server rules`);
      }
    });
  }

  registerRulesHandlers();

  console.log('[ModereX Extensions] Feature extensions loaded');
})();

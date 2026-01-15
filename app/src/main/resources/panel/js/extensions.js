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
      ws.send('GET_MODERATION_PLUGINS');
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
        }
        renderAnticheatIntegrations(data.plugins);
        // Also update the integrations page UI
        if (window.MX?.ui?.renderIntegrations) {
          window.MX.ui.renderIntegrations();
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

  console.log('[ModereX Extensions] Feature extensions loaded');
})();

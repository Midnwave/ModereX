/**
 * Security page handler for ModereX Web Panel.
 * Manages raid protection, IP reputation, lockdown controls.
 */
(function() {
  'use strict';

  let securityData = null;
  let securityRefreshInterval = null;
  let raidSettings = {
    autoLockdown: false,
    vpnDetection: false,
    captcha: false,
    geoBlock: false
  };

  function getWs() {
    return window.MX?.ws;
  }

  window.initSecurityPage = function() {
    requestSecurityStatus();
    securityRefreshInterval = setInterval(requestSecurityStatus, 5000);
  };

  window.cleanupSecurityPage = function() {
    if (securityRefreshInterval) {
      clearInterval(securityRefreshInterval);
      securityRefreshInterval = null;
    }
  };

  function requestSecurityStatus() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('GET_SECURITY_STATUS', {});
    }
  }

  function handleSecurityStatus(data) {
    if (!data) return;
    securityData = data;

    // Update raid status badge
    const raidBadge = document.getElementById('raidStatusBadge');
    if (raidBadge) {
      raidBadge.textContent = data.raidActive ? 'RAID ACTIVE' : 'Normal';
      raidBadge.className = 'badge ' + (data.raidActive ? 'danger' : 'ok');
    }

    // Update raid status card highlight
    const raidCard = document.getElementById('raidStatusCard');
    if (raidCard) {
      raidCard.style.borderColor = data.raidActive ? 'var(--danger)' : '';
    }

    // Update lockdown badge
    const lockdownBadge = document.getElementById('lockdownBadge');
    if (lockdownBadge) {
      lockdownBadge.textContent = data.globalLockdown ? 'ACTIVE' : 'Disabled';
      lockdownBadge.className = 'badge ' + (data.globalLockdown ? 'danger' : '');
    }

    // Update lockdown button
    const lockdownBtn = document.getElementById('lockdownToggleBtn');
    if (lockdownBtn) {
      if (data.globalLockdown) {
        lockdownBtn.classList.add('danger');
        lockdownBtn.innerHTML = '<i class="fa-solid fa-lock-open"></i> Disable Lockdown';
      } else {
        lockdownBtn.classList.remove('danger');
        lockdownBtn.innerHTML = '<i class="fa-solid fa-lock"></i> Lockdown';
      }
    }

    // Update recent joins count
    const joinCount = document.getElementById('recentJoinCount');
    if (joinCount) joinCount.textContent = data.joinVelocity || 0;

    // Update blocked IPs count
    const blockedCount = document.getElementById('blockedIpCount');
    if (blockedCount) {
      const blocked = (data.flaggedIps || []).filter(ip => ip.banned).length;
      blockedCount.textContent = blocked;
    }

    // Update raid settings inputs
    if (data.joinVelocityThreshold !== undefined) {
      const input = document.getElementById('raidJoinThreshold');
      if (input && document.activeElement !== input) input.value = data.joinVelocityThreshold;
    }

    // Update toggles
    raidSettings.autoLockdown = data.autoLockdownEnabled || false;
    raidSettings.vpnDetection = data.vpnDetectionEnabled || false;
    raidSettings.captcha = data.captchaEnabled || false;
    raidSettings.geoBlock = data.geoBlockEnabled || false;

    updateToggle('raidAutoLockdownToggle', raidSettings.autoLockdown);
    updateToggle('raidVpnDetectionToggle', raidSettings.vpnDetection);
    updateToggle('raidCaptchaToggle', raidSettings.captcha);
    updateToggle('raidGeoBlockToggle', raidSettings.geoBlock);

    // Render IP table
    renderIpTable(data.flaggedIps || []);
  }

  function updateToggle(id, active) {
    const btn = document.getElementById(id);
    if (!btn) return;
    btn.setAttribute('aria-pressed', active ? 'true' : 'false');
    if (active) {
      btn.classList.add('on');
    } else {
      btn.classList.remove('on');
    }
  }

  window.toggleRaidSetting = function(setting) {
    raidSettings[setting] = !raidSettings[setting];
    const toggleMap = {
      autoLockdown: 'raidAutoLockdownToggle',
      vpnDetection: 'raidVpnDetectionToggle',
      captcha: 'raidCaptchaToggle',
      geoBlock: 'raidGeoBlockToggle'
    };
    updateToggle(toggleMap[setting], raidSettings[setting]);
  };

  window.toggleLockdown = function() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('MANUAL_LOCKDOWN_TOGGLE', {});
    }
  };

  window.saveRaidSettings = function() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_RAID_SETTINGS', {
        joinVelocityThreshold: parseInt(document.getElementById('raidJoinThreshold')?.value || '10'),
        detectionWindowSeconds: parseInt(document.getElementById('raidWindowSeconds')?.value || '30'),
        minAccountAgeDays: parseInt(document.getElementById('raidMinAccountAge')?.value || '0'),
        autoLockdownEnabled: raidSettings.autoLockdown,
        vpnDetectionEnabled: raidSettings.vpnDetection,
        captchaEnabled: raidSettings.captcha,
        geoBlockEnabled: raidSettings.geoBlock
      });
      if (window.toast) window.toast('success', 'Saved', 'Raid protection settings updated.');
    }
  };

  window.unblockIp = function(ip) {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('UNBLOCK_IP', { ip: ip });
      if (window.toast) window.toast('success', 'Unblocked', 'IP ' + ip + ' has been unblocked.');
    }
  };

  window.refreshIpList = function() {
    requestSecurityStatus();
  };

  window.filterIpList = function() {
    if (securityData) renderIpTable(securityData.flaggedIps || []);
  };

  function renderIpTable(ips) {
    const container = document.getElementById('ipReputationTable');
    if (!container) return;

    const filter = (document.getElementById('ipSearchInput')?.value || '').toLowerCase();
    const filtered = filter ? ips.filter(ip => ip.ip.includes(filter) || (ip.reason || '').toLowerCase().includes(filter)) : ips;

    if (!filtered || filtered.length === 0) {
      container.innerHTML = '<div style="color:var(--text-secondary);font-size:13px">No flagged IPs.</div>';
      return;
    }

    let html = '<table class="data-table" style="width:100%"><thead><tr><th>IP</th><th>Score</th><th>Reason</th><th>Joins</th><th>Status</th><th>Action</th></tr></thead><tbody>';
    for (const ip of filtered) {
      const scoreColor = ip.score >= 200 ? 'var(--danger)' : ip.score >= 100 ? 'var(--warn)' : '';
      html += `<tr>
        <td><code>${escapeHtml(ip.ip)}</code></td>
        <td style="${scoreColor ? 'color:' + scoreColor : ''}">${ip.score}</td>
        <td>${escapeHtml(ip.reason || '-')}</td>
        <td>${ip.totalJoins || 0}</td>
        <td>${ip.banned ? '<span class="badge danger">Banned</span>' : '<span class="badge warn">Flagged</span>'}</td>
        <td>${ip.banned ? `<button class="btn mini" onclick="unblockIp('${escapeHtml(ip.ip)}')"><i class="fa-solid fa-unlock"></i> Unblock</button>` : ''}</td>
      </tr>`;
    }
    html += '</tbody></table>';
    container.innerHTML = html;
  }

  function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  // Register message handlers
  function registerHandlers() {
    const ws = getWs();
    if (!ws) return;
    ws.on('SECURITY_STATUS_DATA', (data) => handleSecurityStatus(data));
    ws.on('SECURITY_STATUS_UPDATE', (data) => handleSecurityStatus(data));
  }

  if (getWs()) {
    registerHandlers();
  } else {
    const checkWs = setInterval(() => {
      if (getWs()) {
        clearInterval(checkWs);
        registerHandlers();
      }
    }, 100);
    setTimeout(() => clearInterval(checkWs), 10000);
  }
})();

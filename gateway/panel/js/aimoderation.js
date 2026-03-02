/**
 * AI Moderation page handler for ModereX Web Panel.
 * Manages AI moderation presets, content type toggles, and moderation log.
 */
(function() {
  'use strict';

  let aiModData = null;
  let aiModRefreshInterval = null;
  let aiContentTypes = {
    chat: true, pm: false, command: false, sign: false, book: false, nickname: false, anvil: false
  };
  let aiFilters = {
    profanity: true, hateSpeech: true, sexualContent: true, violence: false,
    personalInfo: true, advertising: true, spam: true, threats: true,
    discrimination: true, selfHarm: true
  };

  function getWs() {
    return window.MX?.ws;
  }

  window.initAiModerationPage = function() {
    requestAiModerationStatus();
    requestAiModerationLog();
    aiModRefreshInterval = setInterval(requestAiModerationStatus, 10000);
  };

  window.cleanupAiModerationPage = function() {
    if (aiModRefreshInterval) {
      clearInterval(aiModRefreshInterval);
      aiModRefreshInterval = null;
    }
  };

  function requestAiModerationStatus() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('GET_AI_MODERATION_STATUS', {});
    }
  }

  function requestAiModerationLog() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('GET_AI_MODERATION_LOG', { page: 0, pageSize: 50 });
    }
  }

  function handleAiModerationStatus(data) {
    if (!data) return;
    aiModData = data;
    const stats = data.stats || {};

    // Update AI status badge
    const badge = document.getElementById('aiStatusBadge');
    if (badge) {
      badge.textContent = stats.aiAvailable ? 'Online' : 'Offline';
      badge.className = 'badge ' + (stats.aiAvailable ? 'ok' : 'danger');
    }

    // Update model name
    const modelEl = document.getElementById('aiModelName');
    if (modelEl) modelEl.textContent = data.model || stats.model || '-';

    // Update calls per minute
    const callsEl = document.getElementById('aiCallsMinute');
    if (callsEl) callsEl.textContent = `${stats.moderationCallsUsed || 0} / ${stats.moderationCallsMax || 30}`;

    // Update total moderated
    const totalEl = document.getElementById('aiTotalModerated');
    if (totalEl) totalEl.textContent = stats.totalChecked || 0;

    // Update preset select
    const presetSelect = document.getElementById('aiPresetSelect');
    if (presetSelect && data.preset && document.activeElement !== presetSelect) {
      presetSelect.value = data.preset.toLowerCase();
    }

    // Update confidence slider
    if (data.confidenceThreshold !== undefined) {
      const slider = document.getElementById('aiConfidenceSlider');
      const valueEl = document.getElementById('aiConfidenceValue');
      if (slider && document.activeElement !== slider) {
        slider.value = Math.round(data.confidenceThreshold * 100);
      }
      if (valueEl) valueEl.textContent = Math.round(data.confidenceThreshold * 100);
    }

    // Update content type toggles
    if (data.contentTypes) {
      aiContentTypes = { ...aiContentTypes, ...data.contentTypes };
      updateToggle('aiChatToggle', aiContentTypes.chat);
      updateToggle('aiPmToggle', aiContentTypes.pm);
      updateToggle('aiCommandToggle', aiContentTypes.command);
      updateToggle('aiSignToggle', aiContentTypes.sign);
      updateToggle('aiBookToggle', aiContentTypes.book);
      updateToggle('aiNicknameToggle', aiContentTypes.nickname);
      updateToggle('aiAnvilToggle', aiContentTypes.anvil);
    }

    // Update filter toggles
    if (data.filters) {
      aiFilters = { ...aiFilters, ...data.filters };
      updateToggle('aiFilterProfanity', aiFilters.profanity);
      updateToggle('aiFilterHate', aiFilters.hateSpeech);
      updateToggle('aiFilterSexual', aiFilters.sexualContent);
      updateToggle('aiFilterViolence', aiFilters.violence);
      updateToggle('aiFilterPersonalInfo', aiFilters.personalInfo);
      updateToggle('aiFilterAdvertising', aiFilters.advertising);
      updateToggle('aiFilterSpam', aiFilters.spam);
      updateToggle('aiFilterThreats', aiFilters.threats);
      updateToggle('aiFilterDiscrimination', aiFilters.discrimination);
      updateToggle('aiFilterSelfHarm', aiFilters.selfHarm);
    }
  }

  function handleAiModerationLog(data) {
    if (!data) return;
    const container = document.getElementById('aiModerationLog');
    if (!container) return;

    const entries = data.entries || [];
    if (entries.length === 0) {
      container.innerHTML = '<div style="color:var(--text-secondary);font-size:13px">No moderation decisions yet.</div>';
      return;
    }

    let html = '';
    for (const e of entries) {
      const actionColor = e.action === 'BLOCK' ? 'var(--danger)' : e.action === 'WARN' ? 'var(--warn)' : e.action === 'FLAG_FOR_REVIEW' ? 'var(--primary-light)' : 'var(--success)';
      html += `<div style="padding:8px 0;border-bottom:1px solid var(--border);display:flex;align-items:center;gap:12px">
        <span class="badge" style="background:${actionColor};min-width:60px;text-align:center">${e.action || 'ALLOW'}</span>
        <div style="flex:1;min-width:0">
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:2px">
            <strong>${escapeHtml(e.playerName || 'Unknown')}</strong>
            <span style="font-size:11px;color:var(--text-secondary)">${e.contentType || ''}</span>
            <span style="font-size:11px;color:var(--text-secondary);margin-left:auto">${new Date(e.createdAt).toLocaleString()}</span>
          </div>
          <div style="font-size:13px;color:var(--text-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis" title="${escapeHtml(e.content || '')}">${escapeHtml(e.content || '')}</div>
          ${e.reason ? `<div style="font-size:11px;color:var(--text-secondary);margin-top:2px">${escapeHtml(e.reason)}</div>` : ''}
        </div>
        <span style="font-size:12px;color:var(--text-secondary);white-space:nowrap">${e.confidence ? Math.round(e.confidence * 100) + '%' : ''}</span>
      </div>`;
    }
    container.innerHTML = html;
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

  window.toggleAiContentType = function(type) {
    aiContentTypes[type] = !aiContentTypes[type];
    const toggleMap = {
      chat: 'aiChatToggle', pm: 'aiPmToggle', command: 'aiCommandToggle',
      sign: 'aiSignToggle', book: 'aiBookToggle', nickname: 'aiNicknameToggle', anvil: 'aiAnvilToggle'
    };
    updateToggle(toggleMap[type], aiContentTypes[type]);
  };

  window.toggleAiFilter = function(filter) {
    aiFilters[filter] = !aiFilters[filter];
    const toggleMap = {
      profanity: 'aiFilterProfanity', hateSpeech: 'aiFilterHate', sexualContent: 'aiFilterSexual',
      violence: 'aiFilterViolence', personalInfo: 'aiFilterPersonalInfo', advertising: 'aiFilterAdvertising',
      spam: 'aiFilterSpam', threats: 'aiFilterThreats', discrimination: 'aiFilterDiscrimination', selfHarm: 'aiFilterSelfHarm'
    };
    updateToggle(toggleMap[filter], aiFilters[filter]);
  };

  window.changeAiPreset = function() {
    const preset = document.getElementById('aiPresetSelect')?.value;
    if (!preset) return;
    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_AI_MODERATION_PRESET', { preset: preset.toUpperCase() });
    }
  };

  window.saveAiModerationSettings = function() {
    const ws = getWs();
    if (ws && ws.isConnected()) {
      const threshold = parseInt(document.getElementById('aiConfidenceSlider')?.value || '70') / 100;
      const preset = document.getElementById('aiPresetSelect')?.value || 'custom';

      ws.send('SAVE_AI_MODERATION_SETTINGS', {
        preset: preset.toUpperCase(),
        confidenceThreshold: threshold,
        contentTypes: aiContentTypes,
        filters: aiFilters
      });
      if (window.toast) window.toast('success', 'Saved', 'AI moderation settings updated.');
    }
  };

  window.testAiModeration = function() {
    // Show test dialog
    if (window.showModal) {
      const html = `
        <div style="padding:16px">
          <div class="hintline" style="margin-top:0">Enter text to test AI moderation:</div>
          <textarea id="aiTestInput" class="input" rows="4" placeholder="Type a message to analyze..." style="width:100%;resize:vertical"></textarea>
          <div id="aiTestResult" style="margin-top:12px"></div>
          <div style="margin-top:16px;display:flex;gap:8px;justify-content:flex-end">
            <button class="btn" onclick="closeModal()">Close</button>
            <button class="btn primary" onclick="submitAiTest()"><i class="fa-solid fa-flask"></i> Analyze</button>
          </div>
        </div>
      `;
      window.showModal('Test AI Moderation', html);
    }
  };

  window.submitAiTest = function() {
    const content = document.getElementById('aiTestInput')?.value;
    if (!content) return;

    const resultDiv = document.getElementById('aiTestResult');
    if (resultDiv) resultDiv.innerHTML = '<div style="color:var(--text-secondary)">Analyzing...</div>';

    const ws = getWs();
    if (ws && ws.isConnected()) {
      ws.send('TEST_AI_MODERATION', { content: content });
    }
  };

  function handleAiTestResult(data) {
    const resultDiv = document.getElementById('aiTestResult');
    if (!resultDiv || !data) return;

    const actionColor = data.action === 'BLOCK' ? 'var(--danger)' : data.action === 'WARN' ? 'var(--warn)' :
      data.action === 'FLAG_FOR_REVIEW' ? 'var(--primary-light)' : 'var(--success)';

    resultDiv.innerHTML = `
      <div style="padding:12px;border:1px solid var(--border);border-radius:var(--radius);background:rgba(0,0,0,0.1)">
        <div class="stat-row"><span class="stat-label">Action</span><span class="badge" style="background:${actionColor}">${data.action || 'ALLOW'}</span></div>
        <div class="stat-row"><span class="stat-label">Confidence</span><span>${data.confidence ? Math.round(data.confidence * 100) + '%' : '-'}</span></div>
        <div class="stat-row"><span class="stat-label">Category</span><span>${escapeHtml(data.category || '-')}</span></div>
        <div class="stat-row"><span class="stat-label">Reason</span><span>${escapeHtml(data.reason || '-')}</span></div>
      </div>
    `;
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
    ws.on('AI_MODERATION_STATUS_DATA', (data) => handleAiModerationStatus(data));
    ws.on('AI_MODERATION_LOG_DATA', (data) => handleAiModerationLog(data));
    ws.on('AI_MODERATION_TEST_RESULT', (data) => handleAiTestResult(data));
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

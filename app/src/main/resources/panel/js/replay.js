/* ============================================
   ModereX Control Panel - Replay Viewer
   ============================================
   2D canvas-based replay viewer with terrain rendering
   */

(function() {
  'use strict';

  // ===== REPLAY STATE =====
  const replayState = {
    replays: [],
    currentReplay: null,
    snapshots: [],
    events: [],
    playing: false,
    currentTime: 0,
    totalDuration: 0,
    playbackSpeed: 1,
    animationFrame: null,
    lastFrameTime: 0,
    page: 1,
    pageSize: 20,
    searchQuery: '',
    reasonFilter: ''
  };

  // ===== CANVAS RENDERER =====
  class ReplayRenderer {
    constructor(canvas) {
      this.canvas = canvas;
      this.ctx = canvas.getContext('2d');
      this.scale = 4; // pixels per block
      this.offsetX = 0;
      this.offsetY = 0;
      this.isDragging = false;
      this.dragStart = { x: 0, y: 0 };
      this.playerColors = {};
      this.colorPalette = [
        '#5a9cff', '#ff6b6b', '#51cf66', '#ffc078', '#cc5de8',
        '#20c997', '#ff8787', '#748ffc', '#ffd43b', '#69db7c'
      ];

      // Block colors for terrain
      this.blockColors = {
        'GRASS_BLOCK': '#5b8c3b',
        'DIRT': '#8b5a2b',
        'STONE': '#7a7a7a',
        'COBBLESTONE': '#6a6a6a',
        'OAK_LOG': '#6b4423',
        'OAK_PLANKS': '#b8945f',
        'OAK_LEAVES': '#3a5a2a',
        'WATER': '#3f76e4',
        'SAND': '#dbd3a5',
        'GRAVEL': '#8a8a8a',
        'BEDROCK': '#333333',
        'NETHERRACK': '#6e3434',
        'END_STONE': '#dbd6a0',
        'DEFAULT': '#5a5a5a'
      };

      this.setupEventListeners();
      this.resize();
    }

    setupEventListeners() {
      // Pan controls
      this.canvas.addEventListener('mousedown', (e) => {
        if (e.button === 0 || e.button === 2) {
          this.isDragging = true;
          this.dragStart = { x: e.clientX - this.offsetX, y: e.clientY - this.offsetY };
        }
      });

      this.canvas.addEventListener('mousemove', (e) => {
        if (this.isDragging) {
          this.offsetX = e.clientX - this.dragStart.x;
          this.offsetY = e.clientY - this.dragStart.y;
          this.render();
        }
      });

      this.canvas.addEventListener('mouseup', () => {
        this.isDragging = false;
      });

      this.canvas.addEventListener('mouseleave', () => {
        this.isDragging = false;
      });

      // Zoom controls
      this.canvas.addEventListener('wheel', (e) => {
        e.preventDefault();
        const delta = e.deltaY > 0 ? -1 : 1;
        const newScale = Math.max(1, Math.min(20, this.scale + delta));

        // Zoom towards mouse position
        const rect = this.canvas.getBoundingClientRect();
        const mouseX = e.clientX - rect.left;
        const mouseY = e.clientY - rect.top;

        const ratio = newScale / this.scale;
        this.offsetX = mouseX - (mouseX - this.offsetX) * ratio;
        this.offsetY = mouseY - (mouseY - this.offsetY) * ratio;
        this.scale = newScale;

        this.render();
      });

      // Prevent context menu
      this.canvas.addEventListener('contextmenu', (e) => e.preventDefault());

      // Resize handler
      window.addEventListener('resize', () => this.resize());
    }

    resize() {
      const container = this.canvas.parentElement;
      if (!container) return;

      const rect = container.getBoundingClientRect();
      // Only resize if we have valid dimensions (container is visible)
      if (rect.width > 0 && rect.height > 0) {
        this.canvas.width = rect.width;
        this.canvas.height = rect.height;
        this.render();
      }
    }

    getPlayerColor(uuid) {
      if (!this.playerColors[uuid]) {
        const index = Object.keys(this.playerColors).length % this.colorPalette.length;
        this.playerColors[uuid] = this.colorPalette[index];
      }
      return this.playerColors[uuid];
    }

    render() {
      const ctx = this.ctx;
      const w = this.canvas.width;
      const h = this.canvas.height;

      // Clear canvas
      ctx.fillStyle = '#0a1018';
      ctx.fillRect(0, 0, w, h);

      // Draw grid
      this.drawGrid();

      // Draw player trails and positions
      if (replayState.snapshots.length > 0) {
        this.drawPlayerTrails();
        this.drawPlayers();
      }

      // Draw compass
      this.drawCompass();

      // Draw scale indicator
      this.drawScaleIndicator();
    }

    drawGrid() {
      const ctx = this.ctx;
      const w = this.canvas.width;
      const h = this.canvas.height;
      const gridSize = this.scale * 16; // 16 blocks per chunk

      ctx.strokeStyle = 'rgba(90, 156, 255, 0.1)';
      ctx.lineWidth = 1;

      // Calculate grid offset
      const startX = this.offsetX % gridSize;
      const startY = this.offsetY % gridSize;

      // Vertical lines
      ctx.beginPath();
      for (let x = startX; x < w; x += gridSize) {
        ctx.moveTo(x, 0);
        ctx.lineTo(x, h);
      }
      ctx.stroke();

      // Horizontal lines
      ctx.beginPath();
      for (let y = startY; y < h; y += gridSize) {
        ctx.moveTo(0, y);
        ctx.lineTo(w, y);
      }
      ctx.stroke();
    }

    drawPlayerTrails() {
      const ctx = this.ctx;
      const snapshots = replayState.snapshots;
      const currentTime = replayState.currentTime;
      const startTime = replayState.currentReplay?.startTime || 0;

      // Group snapshots by player
      const playerSnapshots = {};
      for (const snapshot of snapshots) {
        const relativeTime = snapshot.timestamp - startTime;
        if (relativeTime <= currentTime) {
          if (!playerSnapshots[snapshot.playerUuid]) {
            playerSnapshots[snapshot.playerUuid] = [];
          }
          playerSnapshots[snapshot.playerUuid].push(snapshot);
        }
      }

      // Draw trail for each player
      for (const [uuid, snaps] of Object.entries(playerSnapshots)) {
        if (snaps.length < 2) continue;

        const color = this.getPlayerColor(uuid);
        ctx.strokeStyle = color;
        ctx.lineWidth = 2;
        ctx.globalAlpha = 0.3;

        ctx.beginPath();
        for (let i = 0; i < snaps.length; i++) {
          const snap = snaps[i];
          const screenX = this.worldToScreenX(snap.x);
          const screenY = this.worldToScreenY(snap.z); // Use Z for top-down view

          if (i === 0) {
            ctx.moveTo(screenX, screenY);
          } else {
            ctx.lineTo(screenX, screenY);
          }
        }
        ctx.stroke();
        ctx.globalAlpha = 1;
      }
    }

    drawPlayers() {
      const ctx = this.ctx;
      const snapshots = replayState.snapshots;
      const currentTime = replayState.currentTime;
      const startTime = replayState.currentReplay?.startTime || 0;

      // Find current position for each player
      const currentPositions = {};
      for (const snapshot of snapshots) {
        const relativeTime = snapshot.timestamp - startTime;
        if (relativeTime <= currentTime) {
          currentPositions[snapshot.playerUuid] = snapshot;
        }
      }

      // Draw each player
      for (const [uuid, snap] of Object.entries(currentPositions)) {
        const screenX = this.worldToScreenX(snap.x);
        const screenY = this.worldToScreenY(snap.z);
        const color = this.getPlayerColor(uuid);

        // Player circle
        ctx.beginPath();
        ctx.arc(screenX, screenY, this.scale * 0.6, 0, Math.PI * 2);
        ctx.fillStyle = color;
        ctx.fill();
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 2;
        ctx.stroke();

        // Direction indicator (using yaw)
        const yawRad = (snap.yaw - 90) * (Math.PI / 180);
        const dirLen = this.scale * 1.2;
        ctx.beginPath();
        ctx.moveTo(screenX, screenY);
        ctx.lineTo(
          screenX + Math.cos(yawRad) * dirLen,
          screenY + Math.sin(yawRad) * dirLen
        );
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 3;
        ctx.stroke();

        // Player name
        ctx.font = '12px "Plus Jakarta Sans", sans-serif';
        ctx.fillStyle = '#fff';
        ctx.textAlign = 'center';
        ctx.fillText(snap.playerName || 'Player', screenX, screenY - this.scale - 5);

        // State indicators
        let stateText = '';
        if (snap.sneaking) stateText = 'Sneaking';
        else if (snap.sprinting) stateText = 'Sprinting';
        else if (snap.swimming) stateText = 'Swimming';
        else if (snap.gliding) stateText = 'Gliding';

        if (stateText) {
          ctx.font = '10px "Plus Jakarta Sans", sans-serif';
          ctx.fillStyle = 'rgba(255, 255, 255, 0.7)';
          ctx.fillText(stateText, screenX, screenY + this.scale + 12);
        }

        // Update info panel
        updateInfoPanel(snap);
      }
    }

    drawCompass() {
      const ctx = this.ctx;
      const size = 40;
      const x = this.canvas.width - size - 20;
      const y = size + 20;

      // Background
      ctx.beginPath();
      ctx.arc(x, y, size, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(0, 0, 0, 0.5)';
      ctx.fill();
      ctx.strokeStyle = 'rgba(90, 156, 255, 0.3)';
      ctx.lineWidth = 2;
      ctx.stroke();

      // North indicator
      ctx.font = 'bold 14px "Plus Jakarta Sans", sans-serif';
      ctx.fillStyle = '#ef4444';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText('N', x, y - size + 14);

      // South
      ctx.fillStyle = '#fff';
      ctx.fillText('S', x, y + size - 14);

      // East
      ctx.fillText('E', x + size - 14, y);

      // West
      ctx.fillText('W', x - size + 14, y);
    }

    drawScaleIndicator() {
      const ctx = this.ctx;
      const blocksPerSegment = 10;
      const pixelWidth = blocksPerSegment * this.scale;
      const x = 20;
      const y = this.canvas.height - 30;

      ctx.strokeStyle = '#fff';
      ctx.lineWidth = 2;

      // Scale bar
      ctx.beginPath();
      ctx.moveTo(x, y);
      ctx.lineTo(x + pixelWidth, y);
      ctx.moveTo(x, y - 5);
      ctx.lineTo(x, y + 5);
      ctx.moveTo(x + pixelWidth, y - 5);
      ctx.lineTo(x + pixelWidth, y + 5);
      ctx.stroke();

      // Label
      ctx.font = '11px "Plus Jakarta Sans", sans-serif';
      ctx.fillStyle = '#fff';
      ctx.textAlign = 'center';
      ctx.fillText(`${blocksPerSegment} blocks`, x + pixelWidth / 2, y - 10);
    }

    worldToScreenX(worldX) {
      return (worldX * this.scale) + this.offsetX + this.canvas.width / 2;
    }

    worldToScreenY(worldZ) {
      return (worldZ * this.scale) + this.offsetY + this.canvas.height / 2;
    }

    centerOnPlayer(x, z) {
      this.offsetX = -x * this.scale;
      this.offsetY = -z * this.scale;
      this.render();
    }
  }

  let renderer = null;

  // ===== PLAYBACK FUNCTIONS =====

  function updateInfoPanel(snapshot) {
    const posInfo = document.getElementById('replayPosInfo');
    const actionInfo = document.getElementById('replayActionInfo');
    const stateInfo = document.getElementById('replayStateInfo');

    if (posInfo) {
      posInfo.textContent = `X: ${snapshot.x.toFixed(1)} Y: ${snapshot.y.toFixed(1)} Z: ${snapshot.z.toFixed(1)}`;
    }

    if (actionInfo) {
      const action = snapshot.action || 'NONE';
      const data = snapshot.actionData || '';
      actionInfo.textContent = action !== 'NONE' ? `${action}: ${data}` : 'None';
    }

    if (stateInfo) {
      let state = 'Standing';
      if (snapshot.sneaking) state = 'Sneaking';
      else if (snapshot.sprinting) state = 'Sprinting';
      else if (snapshot.swimming) state = 'Swimming';
      else if (snapshot.gliding) state = 'Gliding';
      else if (!snapshot.onGround) state = 'Airborne';
      stateInfo.textContent = state;
    }
  }

  function formatTime(ms) {
    const seconds = Math.floor(ms / 1000);
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  function updateTimeDisplay() {
    const currentTimeEl = document.getElementById('replayCurrentTime');
    const totalTimeEl = document.getElementById('replayTotalTime');
    const timeline = document.getElementById('replayTimeline');

    if (currentTimeEl) currentTimeEl.textContent = formatTime(replayState.currentTime);
    if (totalTimeEl) totalTimeEl.textContent = formatTime(replayState.totalDuration);
    if (timeline && replayState.totalDuration > 0) {
      timeline.value = (replayState.currentTime / replayState.totalDuration) * 100;
    }
  }

  function playbackLoop(timestamp) {
    if (!replayState.playing) return;

    if (replayState.lastFrameTime === 0) {
      replayState.lastFrameTime = timestamp;
    }

    const delta = (timestamp - replayState.lastFrameTime) * replayState.playbackSpeed;
    replayState.lastFrameTime = timestamp;

    replayState.currentTime += delta;

    if (replayState.currentTime >= replayState.totalDuration) {
      replayState.currentTime = replayState.totalDuration;
      pauseReplay();
    }

    updateTimeDisplay();

    // Update 3D renderer if available
    if (window.MX?.replay3D) {
      window.MX.replay3D.updatePlayers(
        replayState.snapshots,
        replayState.currentTime,
        replayState.currentReplay?.startTime || 0
      );
    }

    // Fall back to 2D renderer
    if (renderer) renderer.render();

    if (replayState.playing) {
      replayState.animationFrame = requestAnimationFrame(playbackLoop);
    }
  }

  function playReplay() {
    if (!replayState.currentReplay) return;

    replayState.playing = true;
    replayState.lastFrameTime = 0;

    const playBtn = document.getElementById('replayPlayBtn');
    if (playBtn) {
      playBtn.innerHTML = '<i class="fa-solid fa-pause"></i>';
    }

    replayState.animationFrame = requestAnimationFrame(playbackLoop);
  }

  function pauseReplay() {
    replayState.playing = false;
    if (replayState.animationFrame) {
      cancelAnimationFrame(replayState.animationFrame);
    }

    const playBtn = document.getElementById('replayPlayBtn');
    if (playBtn) {
      playBtn.innerHTML = '<i class="fa-solid fa-play"></i>';
    }
  }

  window.toggleReplayPlayback = function() {
    if (replayState.playing) {
      pauseReplay();
    } else {
      playReplay();
    }
  };

  window.replaySkip = function(seconds) {
    replayState.currentTime = Math.max(0, Math.min(
      replayState.totalDuration,
      replayState.currentTime + (seconds * 1000)
    ));
    updateTimeDisplay();

    // Update 3D renderer
    if (window.MX?.replay3D) {
      window.MX.replay3D.updatePlayers(
        replayState.snapshots,
        replayState.currentTime,
        replayState.currentReplay?.startTime || 0
      );
    }

    if (renderer) renderer.render();
  };

  window.setReplaySpeed = function(speed) {
    replayState.playbackSpeed = parseFloat(speed);
  };

  // ===== REPLAY LIST FUNCTIONS =====

  function renderReplayList() {
    const container = document.getElementById('replayList');
    if (!container) return;

    let filtered = replayState.replays;

    // Apply search filter
    if (replayState.searchQuery) {
      const query = replayState.searchQuery.toLowerCase();
      filtered = filtered.filter(r =>
        r.primaryName.toLowerCase().includes(query) ||
        r.sessionId.toLowerCase().includes(query)
      );
    }

    // Apply reason filter
    if (replayState.reasonFilter) {
      filtered = filtered.filter(r => r.reason === replayState.reasonFilter);
    }

    // Pagination
    const totalPages = Math.ceil(filtered.length / replayState.pageSize) || 1;
    const start = (replayState.page - 1) * replayState.pageSize;
    const paged = filtered.slice(start, start + replayState.pageSize);

    // Update page info
    const pageInfo = document.getElementById('replayPageInfo');
    if (pageInfo) pageInfo.textContent = `${replayState.page} / ${totalPages}`;

    if (paged.length === 0) {
      container.innerHTML = `
        <div class="replay-empty" style="text-align:center;padding:40px;color:var(--text-secondary)">
          <i class="fa-solid fa-film" style="font-size:32px;margin-bottom:12px;opacity:0.5"></i>
          <p>No replays found</p>
        </div>
      `;
      return;
    }

    container.innerHTML = paged.map(replay => {
      const isActive = replayState.currentReplay?.sessionId === replay.sessionId;
      const iconClass = {
        'ANTICHEAT_ALERT': 'anticheat',
        'WATCHLIST': 'watchlist',
        'MANUAL': 'manual',
        'STAFF_REQUEST': 'manual',
        'AUTOMOD_TRIGGER': 'automod'
      }[replay.reason] || 'manual';

      const iconSymbol = {
        'ANTICHEAT_ALERT': 'fa-shield-halved',
        'WATCHLIST': 'fa-eye',
        'MANUAL': 'fa-hand-pointer',
        'STAFF_REQUEST': 'fa-user-shield',
        'AUTOMOD_TRIGGER': 'fa-robot'
      }[replay.reason] || 'fa-film';

      const date = new Date(replay.startTime);
      const dateStr = date.toLocaleDateString();
      const timeStr = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

      return `
        <div class="replay-item ${isActive ? 'active' : ''}" onclick="loadReplay('${replay.sessionId}')">
          <div class="replay-item-icon ${iconClass}">
            <i class="fa-solid ${iconSymbol}"></i>
          </div>
          <div class="replay-item-meta">
            <div class="replay-item-name">${escapeHtml(replay.primaryName)}</div>
            <div class="replay-item-info">
              <span>${dateStr} ${timeStr}</span>
              <span>${replay.reason.replace(/_/g, ' ')}</span>
            </div>
          </div>
          <div class="replay-item-duration">${formatTime(replay.endTime - replay.startTime)}</div>
        </div>
      `;
    }).join('');
  }

  window.loadReplay = function(sessionId) {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot load replay - not connected to server');
      return;
    }

    // Show loading state
    const overlay = document.getElementById('replayOverlay');
    if (overlay) {
      overlay.classList.remove('hidden');
      overlay.querySelector('.replay-overlay-content').innerHTML = `
        <div class="spinner"></div>
        <p style="margin-top:16px">Loading replay...</p>
      `;
    }

    // Request replay data from server
    ws.send('GET_REPLAY', { sessionId });
  };

  window.refreshReplays = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      window.toast('warn', 'Not Connected', 'Cannot refresh - not connected to server');
      return;
    }

    ws.send('GET_REPLAYS');
    window.toast('info', 'Refreshing', 'Loading replays from server...');
  };

  window.replayPrevPage = function() {
    if (replayState.page > 1) {
      replayState.page--;
      renderReplayList();
    }
  };

  window.replayNextPage = function() {
    const totalPages = Math.ceil(replayState.replays.length / replayState.pageSize) || 1;
    if (replayState.page < totalPages) {
      replayState.page++;
      renderReplayList();
    }
  };

  // ===== EVENT TIMELINE =====

  function renderEventTimeline() {
    const container = document.getElementById('replayEventTimeline');
    if (!container) return;

    const events = replayState.events;
    if (events.length === 0) {
      container.innerHTML = `
        <div class="timeline-empty" style="text-align:center;padding:20px;color:var(--text-secondary)">
          <p>No events in this replay</p>
        </div>
      `;
      return;
    }

    const startTime = replayState.currentReplay?.startTime || 0;

    container.innerHTML = events.map(event => {
      const relativeTime = event.timestamp - startTime;
      const typeClass = {
        'CHAT': 'chat',
        'COMMAND': 'command',
        'DAMAGE_RECEIVED': 'damage',
        'DAMAGE_DEALT': 'damage',
        'BREAK_BLOCK': 'block',
        'PLACE_BLOCK': 'block',
        'INTERACT': 'block',
        'DROP_ITEM': 'block'
      }[event.action] || '';

      const typeLabel = {
        'CHAT': 'Chat',
        'COMMAND': 'Command',
        'DAMAGE_RECEIVED': 'Damage Taken',
        'DAMAGE_DEALT': 'Damage Dealt',
        'BREAK_BLOCK': 'Block Broken',
        'PLACE_BLOCK': 'Block Placed',
        'INTERACT': 'Interaction',
        'DROP_ITEM': 'Item Dropped'
      }[event.action] || event.action;

      return `
        <div class="replay-event" onclick="replaySeekTo(${relativeTime})">
          <div class="replay-event-time">${formatTime(relativeTime)}</div>
          <div class="replay-event-content">
            <div class="replay-event-type ${typeClass}">${typeLabel}</div>
            <div class="replay-event-data">${escapeHtml(event.actionData || '')}</div>
          </div>
        </div>
      `;
    }).join('');
  }

  window.replaySeekTo = function(timeMs) {
    replayState.currentTime = Math.max(0, Math.min(replayState.totalDuration, timeMs));
    updateTimeDisplay();

    // Update 3D renderer
    if (window.MX?.replay3D) {
      window.MX.replay3D.updatePlayers(
        replayState.snapshots,
        replayState.currentTime,
        replayState.currentReplay?.startTime || 0
      );
    }

    // Update 2D renderer
    if (renderer) renderer.render();
  };

  // ===== WEBSOCKET HANDLERS =====

  function handleReplayList(data) {
    replayState.replays = data.replays || [];
    replayState.page = 1;
    renderReplayList();
  }

  function handleReplayData(data) {
    if (!data.replay) {
      window.toast('bad', 'Error', 'Failed to load replay');
      return;
    }

    const replay = data.replay;
    replayState.currentReplay = replay;
    replayState.snapshots = data.snapshots || [];
    replayState.events = (data.snapshots || []).filter(s => s.action && s.action !== 'NONE');
    replayState.totalDuration = replay.endTime - replay.startTime;
    replayState.currentTime = 0;
    replayState.playing = false;

    // Update UI
    const overlay = document.getElementById('replayOverlay');
    if (overlay) overlay.classList.add('hidden');

    const sessionInfo = document.getElementById('replaySessionInfo');
    if (sessionInfo) sessionInfo.style.display = 'flex';

    const infoPanel = document.getElementById('replayInfoPanel');
    if (infoPanel) infoPanel.style.display = 'block';

    const playerName = document.getElementById('replayPlayerName');
    if (playerName) playerName.textContent = replay.primaryName;

    const duration = document.getElementById('replayDuration');
    if (duration) duration.textContent = formatTime(replayState.totalDuration);

    const status = document.getElementById('replayStatus');
    if (status) {
      status.textContent = 'Loaded';
      status.className = 'chip ok';
    }

    updateTimeDisplay();
    renderEventTimeline();
    renderReplayList();

    // Initialize 3D renderer if available
    if (window.MX?.replay3D) {
      requestAnimationFrame(() => {
        window.MX.replay3D.init();
        window.MX.replay3D.resize();

        // Clear previous players and update with initial positions
        window.MX.replay3D.clear();

        if (replayState.snapshots.length > 0) {
          window.MX.replay3D.updatePlayers(
            replayState.snapshots,
            0, // Start at time 0
            replayState.currentReplay?.startTime || 0
          );
        }
      });
    }

    // Initialize 2D renderer as fallback
    if (!renderer && !window.MX?.replay3D) {
      const canvas = document.getElementById('replayCanvas');
      if (canvas) {
        renderer = new ReplayRenderer(canvas);
      }
    }

    if (renderer) {
      // Use requestAnimationFrame to ensure DOM has updated before sizing
      requestAnimationFrame(() => {
        renderer.resize();

        if (replayState.snapshots.length > 0) {
          const firstSnap = replayState.snapshots[0];
          renderer.centerOnPlayer(firstSnap.x, firstSnap.z);
          renderer.render();
        }
      });
    }

    window.toast('ok', 'Replay Loaded', `${replay.primaryName} - ${formatTime(replayState.totalDuration)}`);
  }

  // Register WebSocket handlers
  function registerHandlers() {
    const ws = window.MX?.ws;
    if (!ws) {
      setTimeout(registerHandlers, 100);
      return;
    }

    ws.on('REPLAY_LIST', handleReplayList);
    ws.on('REPLAY_DATA', handleReplayData);

    console.log('[ModereX Replay] Handlers registered');
  }

  // ===== INITIALIZATION =====

  function initReplayPage() {
    // Timeline slider
    const timeline = document.getElementById('replayTimeline');
    if (timeline) {
      timeline.addEventListener('input', (e) => {
        const percent = parseFloat(e.target.value);
        replayState.currentTime = (percent / 100) * replayState.totalDuration;
        updateTimeDisplay();
        if (renderer) renderer.render();
      });
    }

    // Search input
    const searchInput = document.getElementById('replaySearch');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        replayState.searchQuery = e.target.value;
        replayState.page = 1;
        renderReplayList();
      });
    }

    // Reason filter
    const reasonFilter = document.getElementById('replayReasonFilter');
    if (reasonFilter) {
      reasonFilter.addEventListener('change', (e) => {
        replayState.reasonFilter = e.target.value;
        replayState.page = 1;
        renderReplayList();
      });
    }

    // Initialize 3D renderer (preferred) or fall back to 2D canvas
    const canvas = document.getElementById('replayCanvas');
    if (window.MX?.replay3D) {
      // 3D renderer will be initialized when page becomes visible
      console.log('[ModereX Replay] 3D renderer available');
    } else if (canvas) {
      renderer = new ReplayRenderer(canvas);
    }

    // Load replays on page visit
    const pageObserver = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        if (mutation.target.classList.contains('active')) {
          // Initialize 3D renderer when page becomes visible
          if (window.MX?.replay3D) {
            setTimeout(() => {
              window.MX.replay3D.init();
              window.MX.replay3D.resize();
            }, 50);
          }
          // Resize 2D canvas when page becomes visible
          if (renderer) {
            setTimeout(() => {
              renderer.resize();
            }, 50);
          }
          refreshReplays();
        }
      }
    });

    const replayPage = document.getElementById('page-replay');
    if (replayPage) {
      pageObserver.observe(replayPage, { attributes: true, attributeFilter: ['class'] });
    }
  }

  // Utility
  function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  // Run on load
  registerHandlers();
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initReplayPage);
  } else {
    initReplayPage();
  }

  console.log('[ModereX Replay] Replay viewer loaded');
})();

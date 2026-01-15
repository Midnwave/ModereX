/* ============================================
   ModereX Control Panel - Server Status
   ============================================
   Real-time performance monitoring, TPS tracking,
   and lag detection visualization.
   */

(function() {
  'use strict';

  // TPS History for graph
  const tpsHistory = [];
  const MAX_TPS_HISTORY = 60; // 60 data points

  // TPS Graph canvas context
  let tpsGraphCtx = null;
  let statusUpdateInterval = null;

  // Initialize on page load
  window.initServerStatus = function() {
    const canvas = document.getElementById('tpsGraph');
    if (canvas) {
      tpsGraphCtx = canvas.getContext('2d');
      // Set canvas size
      const container = canvas.parentElement;
      canvas.width = container.offsetWidth;
      canvas.height = 200;
    }

    // Request initial status
    refreshServerStatus();

    // Set up periodic refresh (every 2 seconds)
    if (statusUpdateInterval) {
      clearInterval(statusUpdateInterval);
    }
    statusUpdateInterval = setInterval(refreshServerStatus, 2000);
  };

  // Cleanup when leaving page
  window.cleanupServerStatus = function() {
    if (statusUpdateInterval) {
      clearInterval(statusUpdateInterval);
      statusUpdateInterval = null;
    }
  };

  // Refresh server status from backend
  window.refreshServerStatus = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send(JSON.stringify({ type: 'GET_SERVER_STATUS' }));
    }
  };

  // Handle incoming server status data
  window.handleServerStatus = function(data) {
    if (!data) return;

    // Update TPS
    updateTps(data.tps, data.averageTps);

    // Update Memory
    updateMemory(data.usedMemory, data.maxMemory, data.memoryPercent);

    // Update CPU
    updateCpu(data.cpuUsage);

    // Update Players
    updatePlayers(data.onlinePlayers, data.maxPlayers);

    // Update Ping Stats
    updatePingStats(data.averagePing, data.minPing, data.maxPing);

    // Update Server Info
    updateServerInfo(data.serverVersion, data.uptime, data.javaVersion, data.availableProcessors);

    // Update Worlds
    updateWorlds(data.worlds);

    // Update Player Details
    updatePlayerDetails(data.playerDetails);

    // Update Laggy Chunks
    updateLaggyChunks(data.laggyChunks);

    // Update Alerts
    updateAlerts(data.alerts);

    // Update Lag Machines
    updateLagMachines(data.lagMachines);

    // Update TPS Graph
    addTpsDataPoint(data.tps);
    drawTpsGraph();
  };

  function updateTps(tps, avgTps) {
    const tpsEl = document.getElementById('statusTps');
    const trendEl = document.getElementById('statusTpsTrend');

    if (tpsEl) {
      tpsEl.textContent = (tps || 20.0).toFixed(1);

      // Color based on TPS value
      if (tps >= 18) {
        tpsEl.style.color = 'var(--ok)';
      } else if (tps >= 15) {
        tpsEl.style.color = 'var(--warn)';
      } else {
        tpsEl.style.color = 'var(--bad)';
      }
    }

    if (trendEl) {
      const diff = tps - (avgTps || 20);
      if (diff > 0.5) {
        trendEl.innerHTML = '<i class="fa-solid fa-arrow-up"></i>';
        trendEl.className = 'stat-trend up';
      } else if (diff < -0.5) {
        trendEl.innerHTML = '<i class="fa-solid fa-arrow-down"></i>';
        trendEl.className = 'stat-trend down';
      } else {
        trendEl.innerHTML = '<i class="fa-solid fa-minus"></i>';
        trendEl.className = 'stat-trend';
      }
    }
  }

  function updateMemory(used, max, percent) {
    const memEl = document.getElementById('statusMemory');
    const barEl = document.getElementById('statusMemoryBar');

    if (memEl) {
      memEl.textContent = `${used || 0} / ${max || 0} MB`;
    }

    if (barEl) {
      barEl.style.width = `${percent || 0}%`;

      // Color based on usage
      barEl.classList.remove('warn', 'bad');
      if (percent >= 90) {
        barEl.classList.add('bad');
      } else if (percent >= 75) {
        barEl.classList.add('warn');
      }
    }
  }

  function updateCpu(usage) {
    const cpuEl = document.getElementById('statusCpu');
    const barEl = document.getElementById('statusCpuBar');

    if (cpuEl) {
      cpuEl.textContent = `${(usage || 0).toFixed(1)}%`;
    }

    if (barEl) {
      barEl.style.width = `${Math.min(100, usage || 0)}%`;

      barEl.classList.remove('warn', 'bad');
      if (usage >= 90) {
        barEl.classList.add('bad');
      } else if (usage >= 70) {
        barEl.classList.add('warn');
      }
    }
  }

  function updatePlayers(online, max) {
    const onlineEl = document.getElementById('statusOnline');
    const maxEl = document.getElementById('statusMax');

    if (onlineEl) onlineEl.textContent = online || 0;
    if (maxEl) maxEl.textContent = max || 20;
  }

  function updatePingStats(avg, min, max) {
    const avgEl = document.getElementById('statusAvgPing');
    const minEl = document.getElementById('statusMinPing');
    const maxEl = document.getElementById('statusMaxPing');

    if (avgEl) {
      avgEl.textContent = `${avg || 0}ms`;
      // Color code based on ping
      if (avg <= 50) {
        avgEl.style.color = 'var(--ok)';
      } else if (avg <= 100) {
        avgEl.style.color = 'var(--warn)';
      } else {
        avgEl.style.color = 'var(--bad)';
      }
    }
    if (minEl) minEl.textContent = `${min || 0}ms`;
    if (maxEl) maxEl.textContent = `${max || 0}ms`;
  }

  function updateServerInfo(version, uptime, javaVersion, processors) {
    const versionEl = document.getElementById('statusVersion');
    const uptimeEl = document.getElementById('statusUptime');
    const javaEl = document.getElementById('statusJava');
    const processorsEl = document.getElementById('statusProcessors');

    if (versionEl) versionEl.textContent = version || 'Unknown';
    if (uptimeEl) uptimeEl.textContent = formatUptime(uptime || 0);
    if (javaEl) javaEl.textContent = javaVersion || 'Unknown';
    if (processorsEl) processorsEl.textContent = processors || 0;
  }

  function formatUptime(ms) {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days}d ${hours % 24}h ${minutes % 60}m`;
    if (hours > 0) return `${hours}h ${minutes % 60}m`;
    if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
    return `${seconds}s`;
  }

  function updatePlayerDetails(players) {
    const container = document.getElementById('statusPlayerList');
    const countEl = document.getElementById('playerListCount');
    if (!container) return;

    if (countEl) countEl.textContent = players?.length || 0;

    if (!players || players.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-users-slash" style="color:var(--muted)"></i>
          <p>No players online</p>
        </div>
      `;
      return;
    }

    // Sort by ping (worst first)
    const sorted = [...players].sort((a, b) => (b.ping || 0) - (a.ping || 0));

    container.innerHTML = sorted.map(player => {
      const pingColor = player.ping <= 50 ? 'var(--ok)' : (player.ping <= 100 ? 'var(--warn)' : 'var(--bad)');
      return `
        <div class="player-status-item">
          <img class="player-avatar" src="https://mc-heads.net/avatar/${player.uuid}/32" alt="${escapeHtml(player.name)}">
          <div class="player-info">
            <div class="player-name">${escapeHtml(player.name)}</div>
            <div class="player-location">${escapeHtml(player.world)} (${player.x}, ${player.y}, ${player.z})</div>
          </div>
          <div class="player-stats">
            <span class="ping" style="color:${pingColor}"><i class="fa-solid fa-signal"></i> ${player.ping}ms</span>
            <span class="health"><i class="fa-solid fa-heart" style="color:var(--bad)"></i> ${player.health}</span>
            <span class="food"><i class="fa-solid fa-drumstick-bite" style="color:var(--warn)"></i> ${player.food}</span>
          </div>
          <span class="badge ${player.gamemode === 'SURVIVAL' ? 'good' : 'gray'}">${player.gamemode}</span>
        </div>
      `;
    }).join('');
  }

  function updateWorlds(worlds) {
    const container = document.getElementById('statusWorlds');
    if (!container) return;

    if (!worlds || worlds.length === 0) {
      container.innerHTML = '<div class="world-loading"><div class="spinner"></div> Loading...</div>';
      return;
    }

    // Calculate totals for the stat cards
    let totalChunks = 0;
    let totalEntities = 0;
    worlds.forEach(w => {
      totalChunks += w.chunks || 0;
      totalEntities += w.entities || 0;
    });

    // Update stat card displays
    const chunksEl = document.getElementById('statusChunks');
    const entitiesEl = document.getElementById('statusEntities');
    if (chunksEl) chunksEl.textContent = totalChunks.toLocaleString();
    if (entitiesEl) entitiesEl.textContent = totalEntities.toLocaleString();

    container.innerHTML = worlds.map(world => {
      const iconClass = getWorldIconClass(world.name);
      return `
        <div class="world-stat-item">
          <div class="world-icon ${iconClass}">
            <i class="fa-solid fa-${getWorldIcon(world.name)}"></i>
          </div>
          <div class="world-info">
            <div class="world-name">${escapeHtml(world.name)}</div>
            <div class="world-details">
              ${world.chunks} chunks | ${world.entities} entities
            </div>
          </div>
          <div class="world-players">
            <i class="fa-solid fa-users"></i> ${world.players}
          </div>
        </div>
      `;
    }).join('');
  }

  function getWorldIconClass(name) {
    const lower = name.toLowerCase();
    if (lower.includes('nether')) return 'nether';
    if (lower.includes('end')) return 'end';
    return '';
  }

  function getWorldIcon(name) {
    const lower = name.toLowerCase();
    if (lower.includes('nether')) return 'fire';
    if (lower.includes('end')) return 'dragon';
    return 'globe';
  }

  function updateLaggyChunks(chunks) {
    const container = document.getElementById('laggyChunksList');
    const countEl = document.getElementById('laggyChunkCount');

    if (countEl) {
      countEl.textContent = chunks?.length || 0;
    }

    if (!container) return;

    if (!chunks || chunks.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-check-circle" style="color:var(--ok)"></i>
          <p>No laggy chunks detected</p>
        </div>
      `;
      return;
    }

    container.innerHTML = chunks.map(chunk => `
      <div class="laggy-chunk-item">
        <div class="chunk-coords">
          ${chunk.x}, ${chunk.z}
        </div>
        <div class="chunk-world">${escapeHtml(chunk.world)}</div>
        <div class="chunk-stats">
          <div class="chunk-stat">
            <i class="fa-solid fa-ghost"></i> ${chunk.entities}
          </div>
          <div class="chunk-stat">
            <i class="fa-solid fa-cube"></i> ${chunk.tileEntities}
          </div>
        </div>
        <button class="btn mini" onclick="teleportToChunk('${escapeHtml(chunk.world)}', ${chunk.x}, ${chunk.z})">
          <i class="fa-solid fa-location-arrow"></i>
        </button>
      </div>
    `).join('');
  }

  function updateAlerts(alerts) {
    const container = document.getElementById('statusAlertsList');
    const countEl = document.getElementById('alertCount');

    if (countEl) {
      countEl.textContent = alerts?.length || 0;
    }

    if (!container) return;

    if (!alerts || alerts.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-check-circle" style="color:var(--ok)"></i>
          <p>No recent performance alerts</p>
        </div>
      `;
      return;
    }

    container.innerHTML = alerts.slice(0, 10).map(alert => {
      const isLowTps = alert.type === 'LOW_TPS';
      const timeAgo = formatTimeAgo(alert.timestamp);
      return `
        <div class="status-alert-item ${isLowTps ? 'low-tps' : ''}">
          <div class="alert-icon">
            <i class="fa-solid fa-${isLowTps ? 'gauge-simple-low' : 'triangle-exclamation'}"></i>
          </div>
          <div class="alert-content">
            <div class="alert-message">${escapeHtml(alert.message)}</div>
            <div class="alert-time">${timeAgo}</div>
          </div>
        </div>
      `;
    }).join('');
  }

  function updateLagMachines(lagMachines) {
    const container = document.getElementById('lagMachinesList');
    const countEl = document.getElementById('lagMachineCount');

    if (countEl) {
      countEl.textContent = lagMachines?.length || 0;
    }

    if (!container) return;

    if (!lagMachines || lagMachines.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-check-circle" style="color:var(--ok)"></i>
          <p>No potential lag machines detected</p>
        </div>
      `;
      return;
    }

    container.innerHTML = lagMachines.map(machine => `
      <div class="lag-machine-item">
        <img class="player-avatar" src="https://mc-heads.net/avatar/${machine.uuid}/40" alt="${escapeHtml(machine.name)}">
        <div class="player-info">
          <div class="player-name">${escapeHtml(machine.name)}</div>
          <div class="player-stats">
            <span><i class="fa-solid fa-ghost"></i> ${machine.entities} entities</span>
            <span><i class="fa-solid fa-bolt"></i> ${machine.redstone} redstone</span>
          </div>
        </div>
        <div class="actions">
          <button class="btn mini" onclick="teleportToPlayer('${escapeHtml(machine.name)}')">
            <i class="fa-solid fa-location-arrow"></i>
          </button>
          <button class="btn mini" onclick="openPlayerDrawer('${machine.uuid}')">
            <i class="fa-solid fa-user"></i>
          </button>
        </div>
      </div>
    `).join('');
  }

  // TPS Graph rendering
  function addTpsDataPoint(tps) {
    tpsHistory.push({
      time: Date.now(),
      value: tps || 20
    });

    // Keep only last MAX_TPS_HISTORY points
    while (tpsHistory.length > MAX_TPS_HISTORY) {
      tpsHistory.shift();
    }
  }

  function drawTpsGraph() {
    if (!tpsGraphCtx || tpsHistory.length < 2) return;

    const canvas = tpsGraphCtx.canvas;
    const width = canvas.width;
    const height = canvas.height;
    const padding = { top: 20, right: 20, bottom: 30, left: 45 };
    const graphWidth = width - padding.left - padding.right;
    const graphHeight = height - padding.top - padding.bottom;

    // Clear canvas
    tpsGraphCtx.clearRect(0, 0, width, height);

    // Draw background
    tpsGraphCtx.fillStyle = 'rgba(0, 0, 0, 0.2)';
    tpsGraphCtx.fillRect(0, 0, width, height);

    // Draw grid lines
    tpsGraphCtx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
    tpsGraphCtx.lineWidth = 1;

    // Horizontal grid lines at TPS 5, 10, 15, 20
    [5, 10, 15, 20].forEach(tps => {
      const y = padding.top + graphHeight - (tps / 20) * graphHeight;
      tpsGraphCtx.beginPath();
      tpsGraphCtx.moveTo(padding.left, y);
      tpsGraphCtx.lineTo(width - padding.right, y);
      tpsGraphCtx.stroke();

      // Label
      tpsGraphCtx.fillStyle = 'rgba(168, 184, 216, 0.6)';
      tpsGraphCtx.font = '11px "Plus Jakarta Sans", sans-serif';
      tpsGraphCtx.textAlign = 'right';
      tpsGraphCtx.fillText(tps.toString(), padding.left - 8, y + 4);
    });

    // Draw threshold zones
    // Good zone (18-20)
    const goodTop = padding.top + graphHeight - (20 / 20) * graphHeight;
    const goodBottom = padding.top + graphHeight - (18 / 20) * graphHeight;
    tpsGraphCtx.fillStyle = 'rgba(16, 185, 129, 0.08)';
    tpsGraphCtx.fillRect(padding.left, goodTop, graphWidth, goodBottom - goodTop);

    // Warning zone (15-18)
    const warnTop = goodBottom;
    const warnBottom = padding.top + graphHeight - (15 / 20) * graphHeight;
    tpsGraphCtx.fillStyle = 'rgba(245, 158, 11, 0.08)';
    tpsGraphCtx.fillRect(padding.left, warnTop, graphWidth, warnBottom - warnTop);

    // Critical zone (0-15)
    tpsGraphCtx.fillStyle = 'rgba(239, 68, 68, 0.08)';
    tpsGraphCtx.fillRect(padding.left, warnBottom, graphWidth, height - padding.bottom - warnBottom);

    // Draw TPS line
    if (tpsHistory.length >= 2) {
      tpsGraphCtx.beginPath();

      // Create gradient for line
      const gradient = tpsGraphCtx.createLinearGradient(0, padding.top, 0, height - padding.bottom);
      gradient.addColorStop(0, 'rgba(16, 185, 129, 0.9)');
      gradient.addColorStop(0.5, 'rgba(245, 158, 11, 0.9)');
      gradient.addColorStop(1, 'rgba(239, 68, 68, 0.9)');

      tpsHistory.forEach((point, index) => {
        const x = padding.left + (index / (MAX_TPS_HISTORY - 1)) * graphWidth;
        const y = padding.top + graphHeight - (Math.min(20, point.value) / 20) * graphHeight;

        if (index === 0) {
          tpsGraphCtx.moveTo(x, y);
        } else {
          tpsGraphCtx.lineTo(x, y);
        }
      });

      // Stroke line with color based on current TPS
      const currentTps = tpsHistory[tpsHistory.length - 1].value;
      if (currentTps >= 18) {
        tpsGraphCtx.strokeStyle = 'rgba(16, 185, 129, 0.9)';
      } else if (currentTps >= 15) {
        tpsGraphCtx.strokeStyle = 'rgba(245, 158, 11, 0.9)';
      } else {
        tpsGraphCtx.strokeStyle = 'rgba(239, 68, 68, 0.9)';
      }
      tpsGraphCtx.lineWidth = 2;
      tpsGraphCtx.stroke();

      // Fill area under line
      const lastX = padding.left + ((tpsHistory.length - 1) / (MAX_TPS_HISTORY - 1)) * graphWidth;
      tpsGraphCtx.lineTo(lastX, height - padding.bottom);
      tpsGraphCtx.lineTo(padding.left, height - padding.bottom);
      tpsGraphCtx.closePath();

      if (currentTps >= 18) {
        tpsGraphCtx.fillStyle = 'rgba(16, 185, 129, 0.15)';
      } else if (currentTps >= 15) {
        tpsGraphCtx.fillStyle = 'rgba(245, 158, 11, 0.15)';
      } else {
        tpsGraphCtx.fillStyle = 'rgba(239, 68, 68, 0.15)';
      }
      tpsGraphCtx.fill();

      // Draw current TPS marker
      const currentX = lastX;
      const currentY = padding.top + graphHeight - (Math.min(20, currentTps) / 20) * graphHeight;

      tpsGraphCtx.beginPath();
      tpsGraphCtx.arc(currentX, currentY, 5, 0, Math.PI * 2);
      tpsGraphCtx.fillStyle = currentTps >= 18 ? '#10b981' : (currentTps >= 15 ? '#f59e0b' : '#ef4444');
      tpsGraphCtx.fill();
      tpsGraphCtx.strokeStyle = 'rgba(255, 255, 255, 0.5)';
      tpsGraphCtx.lineWidth = 2;
      tpsGraphCtx.stroke();
    }

    // Draw time labels
    tpsGraphCtx.fillStyle = 'rgba(168, 184, 216, 0.6)';
    tpsGraphCtx.font = '10px "Plus Jakarta Sans", sans-serif';
    tpsGraphCtx.textAlign = 'center';

    const labels = ['Now', '30s', '60s'];
    labels.forEach((label, i) => {
      const x = padding.left + graphWidth - (i / 2) * graphWidth;
      tpsGraphCtx.fillText(label, x, height - 10);
    });
  }

  // Helper: Format time ago
  function formatTimeAgo(timestamp) {
    const seconds = Math.floor((Date.now() - timestamp) / 1000);

    if (seconds < 60) return `${seconds}s ago`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
    return `${Math.floor(seconds / 86400)}d ago`;
  }

  // Helper: Escape HTML
  function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  // Actions
  window.teleportToChunk = function(world, x, z) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send(JSON.stringify({
        type: 'TELEPORT_TO_CHUNK',
        data: { world, x, z }
      }));
      window.toast('info', 'Teleport', `Teleporting to chunk ${x}, ${z} in ${world}`);
    }
  };

  window.teleportToPlayer = function(playerName) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send(JSON.stringify({
        type: 'TELEPORT_TO_PLAYER',
        data: { player: playerName }
      }));
      window.toast('info', 'Teleport', `Teleporting to ${playerName}`);
    }
  };

  // Register WebSocket handler
  function registerHandlers() {
    const ws = window.MX?.ws;
    if (!ws) {
      setTimeout(registerHandlers, 100);
      return;
    }

    ws.on('SERVER_STATUS', (data) => {
      handleServerStatus(data);
    });

    console.log('[ModereX Status] Handlers registered');
  }

  registerHandlers();
  console.log('[ModereX Status] Module loaded');
})();

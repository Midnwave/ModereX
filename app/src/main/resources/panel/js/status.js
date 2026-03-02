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

  // Cached status data for instant rendering
  let cachedStatusData = null;

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

    // Show cached data immediately (no blocking wait)
    if (cachedStatusData) {
      window.handleServerStatus(cachedStatusData);
    }

    // Request fresh data in background
    refreshServerStatus();

    // Set up periodic refresh (every 1 second)
    if (statusUpdateInterval) {
      clearInterval(statusUpdateInterval);
    }
    statusUpdateInterval = setInterval(refreshServerStatus, 1000);
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
      ws.requestServerStatus();
    }
  };

  // Handle incoming server status data
  window.handleServerStatus = function(data) {
    if (!data) return;

    // Cache for instant rendering on next page visit
    cachedStatusData = data;

    // Update TPS & MSPT
    updateTps(data.tps, data.averageTps, data.mspt, data.averageMspt);

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

    // Update Database Size
    updateDatabaseSize(data.databaseSize, data.databaseMaxSize, data.databaseSizeBytes, data.databaseMaxSizeBytes);

    // Update TPS Graph
    addTpsDataPoint(data.tps);
    drawTpsGraph();

    // Update Health Score
    if (data.healthScore !== undefined) {
      updateHealthScore(data.healthScore);
    } else {
      // Calculate health score if not provided
      const healthScore = calculateHealthScore(data);
      updateHealthScore(healthScore);
    }

    // Update Heap Memory Graph
    if (data.memoryHistory) {
      drawHeapGraph(data.memoryHistory);
    }

    // Update GC Activity Graph
    if (data.gcHistory) {
      drawGcGraph(data.gcHistory);
    }

    // Update alert tier badge
    if (data.alertTier) {
      updateAlertTier(data.alertTier);
    }

    // Update Heat Map (if on resources tab)
    if (data.laggyChunks && document.getElementById('tab-resources')?.classList.contains('active')) {
      drawChunkHeatMap(data.laggyChunks);
    }
  };

  // Calculate health score locally if not provided by server
  function calculateHealthScore(data) {
    let score = 100;

    // TPS factor (40% weight)
    const tps = data.tps || 20;
    const tpsFactor = Math.min(tps / 20, 1);
    score -= (1 - tpsFactor) * 40;

    // Memory factor (30% weight)
    const memPercent = data.memoryPercent || 0;
    score -= (memPercent / 100) * 30;

    // Entity factor (20% weight) - penalize if over 5000
    const entities = data.entityCount || 0;
    if (entities > 5000) {
      const entityFactor = Math.max(0, 1 - ((entities - 5000) / 10000));
      score -= (1 - entityFactor) * 20;
    }

    // Chunk factor (10% weight) - penalize if over 1000
    const chunks = data.loadedChunks || 0;
    if (chunks > 1000) {
      const chunkFactor = Math.max(0, 1 - ((chunks - 1000) / 5000));
      score -= (1 - chunkFactor) * 10;
    }

    return Math.max(0, Math.min(100, score));
  }

  function updateTps(tps, avgTps, mspt, avgMspt) {
    const tpsEl = document.getElementById('statusTps');
    const trendEl = document.getElementById('statusTpsTrend');
    const tpsBarEl = document.getElementById('statusTpsBar');

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

    // Update MSPT display
    const msptEl = document.getElementById('statusMspt');
    if (msptEl && mspt !== undefined) {
      msptEl.textContent = (mspt || 50.0).toFixed(1) + 'ms';
      if (mspt <= 50) {
        msptEl.style.color = 'var(--ok)';
      } else if (mspt <= 75) {
        msptEl.style.color = 'var(--warn)';
      } else {
        msptEl.style.color = 'var(--bad)';
      }
    }
    const avgMsptEl = document.getElementById('statusAvgMspt');
    if (avgMsptEl && avgMspt !== undefined) {
      avgMsptEl.textContent = (avgMspt || 50.0).toFixed(1) + 'ms';
    }

    // Update TPS progress bar
    if (tpsBarEl) {
      // TPS bar width: percentage of 20 TPS (capped at 100%)
      const tpsPercent = Math.min(100, (tps / 20) * 100);
      tpsBarEl.style.width = tpsPercent + '%';

      // Color coding for the bar
      if (tps >= 18) {
        tpsBarEl.style.background = 'linear-gradient(90deg, var(--ok), rgba(16, 185, 129, 0.7))';
      } else if (tps >= 15) {
        tpsBarEl.style.background = 'linear-gradient(90deg, var(--warn), rgba(245, 158, 11, 0.7))';
      } else {
        tpsBarEl.style.background = 'linear-gradient(90deg, var(--bad), rgba(239, 68, 68, 0.7))';
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

  // Database size limit constants
  const DB_SIZE_LIMIT_BYTES = 10 * 1024 * 1024; // 10MB limit for free tier
  const DB_SIZE_WARN_THRESHOLD = 0.8; // Warn at 80%
  let dbSizeAlertShown = false;

  // Format bytes to human readable (KB until 1MB, then MB)
  function formatDatabaseSize(bytes) {
    if (bytes < 1024) {
      return bytes + ' B';
    } else if (bytes < 1024 * 1024) {
      return (bytes / 1024).toFixed(2) + ' KB';
    } else {
      return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
    }
  }

  function updateDatabaseSize(sizeMb, maxSizeMb, sizeBytes, maxSizeBytes) {
    const sizeEl = document.getElementById('statusDbSize');
    const labelEl = document.getElementById('statusDbLabel');
    const barEl = document.getElementById('statusDbBar');

    // Use bytes if available, otherwise fall back to MB
    const bytes = sizeBytes || (sizeMb ? sizeMb * 1024 * 1024 : 0);
    const maxBytes = maxSizeBytes || (maxSizeMb ? maxSizeMb * 1024 * 1024 : DB_SIZE_LIMIT_BYTES);
    const percent = Math.min(100, (bytes / maxBytes) * 100);
    const maxMb = (maxBytes / (1024 * 1024)).toFixed(0);

    if (sizeEl) {
      // Show "X / 10 MB" format
      sizeEl.textContent = formatDatabaseSize(bytes) + ' / ' + maxMb + ' MB';

      // Color based on usage
      if (percent >= 90) {
        sizeEl.style.color = 'var(--bad)';
      } else if (percent >= 70) {
        sizeEl.style.color = 'var(--warn)';
      } else {
        sizeEl.style.color = '';
      }
    }

    // Update label with percentage
    if (labelEl) {
      labelEl.textContent = 'Database (' + percent.toFixed(0) + '%)';
    }

    if (barEl) {
      barEl.style.width = `${percent}%`;

      // Color the bar based on usage
      if (percent >= 90) {
        barEl.style.background = 'linear-gradient(90deg, #f87171, #ef4444)';
      } else if (percent >= 70) {
        barEl.style.background = 'linear-gradient(90deg, #fbbf24, #f59e0b)';
      } else {
        barEl.style.background = 'linear-gradient(90deg, #a78bfa, #8b5cf6)';
      }
    }

    // Show low storage alert once when crossing threshold
    if (percent >= DB_SIZE_WARN_THRESHOLD * 100 && !dbSizeAlertShown && window.toast) {
      dbSizeAlertShown = true;
      const maxMb = (maxBytes / (1024 * 1024)).toFixed(0);
      window.toast('warn', 'Low Storage', `Database is at ${percent.toFixed(0)}% capacity (${formatDatabaseSize(bytes)}/${maxMb}MB).`);
    } else if (percent < DB_SIZE_WARN_THRESHOLD * 100) {
      dbSizeAlertShown = false;
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
      ws.send('TELEPORT_TO_CHUNK', { world, x, z });
      window.toast('info', 'Teleport', `Teleporting to chunk ${x}, ${z} in ${world}`);
    }
  };

  window.teleportToPlayer = function(playerName) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('TELEPORT_TO_PLAYER', { player: playerName });
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

    ws.on('ENTITY_BREAKDOWN', (data) => {
      updateEntityBreakdown(data);
    });

    ws.on('CHUNK_BREAKDOWN', (data) => {
      updateChunkBreakdown(data);
    });

    ws.on('SPARK_STATUS', (data) => {
      updateSparkStatus(data);
    });

    ws.on('DIAGNOSTICS_DATA', (data) => {
      updateDiagnostics(data);
    });

    ws.on('ALERT_HISTORY', (data) => {
      updateAlertHistory(data);
    });

    // New performance handlers
    ws.on('THROTTLE_STATUS_DATA', (data) => updateThrottleStatus(data));
    ws.on('THROTTLE_STATUS_UPDATE', (data) => updateThrottleStatus(data));
    ws.on('MEMORY_STATUS_DATA', (data) => updateMemoryStatus(data));
    ws.on('MEMORY_STATUS_UPDATE', (data) => updateMemoryStatus(data));
    ws.on('GC_RECOMMENDATIONS_DATA', (data) => updateGcRecommendations(data));
    ws.on('GC_TRIGGERED', () => { if (window.toast) window.toast('success', 'GC Triggered', 'Garbage collection requested.'); });
    ws.on('PERFORMANCE_SETTINGS_DATA', (data) => updatePerformanceSettingsUI(data));

    console.log('[ModereX Status] Handlers registered');
  }

  // ====== MONITORING TAB SWITCHING ======

  window.switchMonitoringTab = function(tabName) {
    // Update tab buttons
    document.querySelectorAll('.sub-tab').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.tab === tabName);
    });

    // Update tab content
    document.querySelectorAll('.monitoring-tab-content').forEach(content => {
      content.classList.toggle('active', content.id === 'tab-' + tabName);
    });

    // Load tab-specific data
    if (tabName === 'resources') {
      requestEntityBreakdown();
      requestChunkBreakdown();
    } else if (tabName === 'diagnostics') {
      requestSparkStatus();
      requestDiagnostics();
    } else if (tabName === 'alerts') {
      requestAlertHistory();
    } else if (tabName === 'throttle') {
      requestThrottleStatus();
    } else if (tabName === 'memory') {
      requestMemoryStatus();
      requestGcRecommendations();
    } else if (tabName === 'tuning') {
      requestPerformanceSettings();
    }
  };

  // ====== HEALTH SCORE ======

  window.updateHealthScore = function(score) {
    const badge = document.getElementById('healthScoreBadge');
    const ring = document.getElementById('healthRingFill');
    const value = document.getElementById('healthScoreValue');

    if (!badge || !ring || !value) return;

    // Update value
    value.textContent = Math.round(score);

    // Update ring (stroke-dasharray based on percentage)
    ring.style.strokeDasharray = `${score}, 100`;

    // Update colors based on score
    badge.classList.remove('warning', 'critical');
    if (score < 50) {
      badge.classList.add('critical');
    } else if (score < 75) {
      badge.classList.add('warning');
    }
  };

  // ====== ENTITY BREAKDOWN ======

  function requestEntityBreakdown() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_ENTITY_BREAKDOWN');
    }
  }

  function updateEntityBreakdown(data) {
    const container = document.getElementById('entityBreakdownList');
    const totalEl = document.getElementById('totalEntities');
    if (!container) return;

    const entities = data.entities || [];
    const total = data.total || 0;

    if (totalEl) totalEl.textContent = total.toLocaleString();

    if (entities.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-ghost" style="color:var(--muted)"></i>
          <p>No entities loaded</p>
        </div>
      `;
      return;
    }

    const maxCount = Math.max(...entities.map(e => e.count));

    container.innerHTML = entities.slice(0, 20).map(entity => {
      const percent = (entity.count / maxCount) * 100;
      return `
        <div class="entity-item">
          <div class="entity-item-name">
            <div class="entity-item-icon"><i class="fa-solid fa-ghost"></i></div>
            <span>${escapeHtml(entity.type)}</span>
          </div>
          <div class="entity-item-count">${entity.count.toLocaleString()}</div>
        </div>
      `;
    }).join('');
  }

  // ====== CHUNK BREAKDOWN ======

  function requestChunkBreakdown() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_CHUNK_BREAKDOWN');
    }
  }

  function updateChunkBreakdown(data) {
    const container = document.getElementById('chunkBreakdownList');
    if (!container) return;

    const worlds = data.worlds || [];

    if (worlds.length === 0) {
      container.innerHTML = '<div class="spinner"></div>';
      return;
    }

    container.innerHTML = worlds.map(world => {
      const iconClass = world.name.toLowerCase().includes('nether') ? 'fa-fire' :
                        world.name.toLowerCase().includes('end') ? 'fa-dragon' : 'fa-globe';
      return `
        <div class="chunk-world-item">
          <div class="chunk-world-header">
            <div class="chunk-world-name">
              <i class="fa-solid ${iconClass}"></i>
              ${escapeHtml(world.name)}
            </div>
            <span class="badge gray">${world.chunks} chunks</span>
          </div>
          <div class="chunk-world-stats">
            <span><i class="fa-solid fa-ghost"></i> ${world.entities} entities</span>
            <span><i class="fa-solid fa-users"></i> ${world.players} players</span>
          </div>
        </div>
      `;
    }).join('');
  }

  // ====== HEAT MAP ======

  window.drawChunkHeatMap = function(laggyChunks) {
    const canvas = document.getElementById('chunkHeatMap');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;

    // Clear canvas
    ctx.fillStyle = 'rgba(0, 0, 0, 0.3)';
    ctx.fillRect(0, 0, width, height);

    if (!laggyChunks || laggyChunks.length === 0) {
      ctx.fillStyle = 'rgba(255, 255, 255, 0.3)';
      ctx.font = '14px "Plus Jakarta Sans", sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('No laggy chunks to display', width / 2, height / 2);
      return;
    }

    // Find bounds
    const chunks = laggyChunks.slice(0, 50);
    const minX = Math.min(...chunks.map(c => c.x));
    const maxX = Math.max(...chunks.map(c => c.x));
    const minZ = Math.min(...chunks.map(c => c.z));
    const maxZ = Math.max(...chunks.map(c => c.z));

    const rangeX = Math.max(maxX - minX + 1, 10);
    const rangeZ = Math.max(maxZ - minZ + 1, 10);
    const cellWidth = (width - 40) / rangeX;
    const cellHeight = (height - 40) / rangeZ;
    const cellSize = Math.min(cellWidth, cellHeight, 30);

    const offsetX = (width - rangeX * cellSize) / 2;
    const offsetY = (height - rangeZ * cellSize) / 2;

    // Find max severity for color scaling
    const maxSeverity = Math.max(...chunks.map(c => c.entities + c.tileEntities));

    // Draw chunks
    chunks.forEach(chunk => {
      const x = offsetX + (chunk.x - minX) * cellSize;
      const y = offsetY + (chunk.z - minZ) * cellSize;
      const severity = (chunk.entities + chunk.tileEntities) / maxSeverity;

      // Color based on severity
      let color;
      if (severity > 0.7) {
        color = 'rgba(239, 68, 68, 0.8)';
      } else if (severity > 0.4) {
        color = 'rgba(245, 158, 11, 0.8)';
      } else {
        color = 'rgba(16, 185, 129, 0.6)';
      }

      ctx.fillStyle = color;
      ctx.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);

      // Add border
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.2)';
      ctx.strokeRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
    });

    // Draw axis labels
    ctx.fillStyle = 'rgba(255, 255, 255, 0.5)';
    ctx.font = '10px "Plus Jakarta Sans", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('X →', width - 15, height / 2);
    ctx.save();
    ctx.translate(15, height / 2);
    ctx.rotate(-Math.PI / 2);
    ctx.fillText('Z →', 0, 0);
    ctx.restore();
  };

  // ====== SPARK INTEGRATION ======

  function requestSparkStatus() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_SPARK_STATUS');
    }
  }

  function updateSparkStatus(data) {
    const statusEl = document.getElementById('sparkStatus');
    const contentEl = document.getElementById('sparkContent');
    const actionsEl = document.getElementById('sparkActionsPanel');

    if (!statusEl) return;

    if (data.available) {
      statusEl.innerHTML = `<span class="badge good"><i class="fa-solid fa-check"></i> Spark v${escapeHtml(data.version || 'Unknown')}</span>`;
      contentEl.innerHTML = `
        <div style="color:var(--text-secondary)">
          <p><i class="fa-solid fa-check-circle" style="color:var(--ok)"></i> Spark is installed and ready for profiling.</p>
          <p style="margin-top:8px">Use the actions below to profile your server performance.</p>
        </div>
      `;
      if (actionsEl) actionsEl.style.display = 'block';
    } else {
      statusEl.innerHTML = `<span class="badge gray"><i class="fa-solid fa-times"></i> Not Installed</span>`;
      contentEl.innerHTML = `
        <div class="empty-state">
          <i class="fa-solid fa-plug" style="color:var(--muted)"></i>
          <p>Spark is not installed</p>
          <p style="font-size:13px;color:var(--text-secondary);margin-top:8px">
            Install <a href="https://spark.lucko.me/" target="_blank" style="color:var(--primary-light)">Spark</a> for advanced profiling capabilities.
          </p>
        </div>
      `;
      if (actionsEl) actionsEl.style.display = 'none';
    }
  }

  window.startSparkProfile = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('SPARK_PROFILE_START');
      window.toast('info', 'Spark', 'Starting CPU profile... This may take a moment.');
    }
  };

  window.sparkHeapDump = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('SPARK_HEAP_DUMP');
      window.toast('info', 'Spark', 'Generating heap dump...');
    }
  };

  window.sparkGC = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('SPARK_GC');
      window.toast('info', 'Spark', 'Triggering garbage collection...');
    }
  };

  // ====== DIAGNOSTICS ======

  function requestDiagnostics() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_DIAGNOSTICS');
    }
  }

  function updateDiagnostics(data) {
    const els = {
      jvmArgs: document.getElementById('jvmArgs'),
      gcType: document.getElementById('gcType'),
      heapMax: document.getElementById('heapMax'),
      threadCount: document.getElementById('threadCount'),
      loadedClasses: document.getElementById('loadedClasses'),
      gcCollections: document.getElementById('gcCollections')
    };

    if (els.jvmArgs) els.jvmArgs.textContent = data.jvmArgs || 'N/A';
    if (els.gcType) els.gcType.textContent = data.gcType || 'Unknown';
    if (els.heapMax) els.heapMax.textContent = data.heapMax || 'Unknown';
    if (els.threadCount) els.threadCount.textContent = data.threadCount || '0';
    if (els.loadedClasses) els.loadedClasses.textContent = data.loadedClasses || '0';
    if (els.gcCollections) els.gcCollections.textContent = data.gcCollections || '0';
  }

  // ====== ALERT THRESHOLDS ======

  window.saveAlertThresholds = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;

    const thresholds = {
      tpsWarning: parseFloat(document.getElementById('tpsWarningThreshold')?.value) || 18,
      tpsCritical: parseFloat(document.getElementById('tpsCriticalThreshold')?.value) || 15,
      tpsEmergency: parseFloat(document.getElementById('tpsEmergencyThreshold')?.value) || 10,
      memoryWarning: parseInt(document.getElementById('memoryWarningThreshold')?.value) || 80,
      memoryCritical: parseInt(document.getElementById('memoryCriticalThreshold')?.value) || 90
    };

    ws.send('UPDATE_ALERT_THRESHOLDS', thresholds);
    window.toast('success', 'Saved', 'Alert thresholds updated');
  };

  // ====== ALERT HISTORY ======

  function requestAlertHistory() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_ALERT_HISTORY');
    }
  }

  function updateAlertHistory(data) {
    const container = document.getElementById('alertHistoryList');
    if (!container) return;

    const alerts = data.alerts || [];

    if (alerts.length === 0) {
      container.innerHTML = `
        <div class="empty-state small">
          <i class="fa-solid fa-check-circle" style="color:var(--ok)"></i>
          <p>No alerts in the last 24 hours</p>
        </div>
      `;
      return;
    }

    container.innerHTML = alerts.map(alert => {
      const levelClass = alert.level === 'EMERGENCY' ? 'emergency' :
                         alert.level === 'CRITICAL' ? 'critical' : 'warning';
      const icon = alert.level === 'EMERGENCY' ? 'fa-skull' :
                   alert.level === 'CRITICAL' ? 'fa-exclamation-triangle' : 'fa-exclamation';
      return `
        <div class="alert-history-item ${levelClass}">
          <div class="alert-history-icon">
            <i class="fa-solid ${icon}"></i>
          </div>
          <div class="alert-history-content">
            <div class="alert-history-message">${escapeHtml(alert.message)}</div>
            <div class="alert-history-time">${formatTimeAgo(alert.timestamp)}</div>
          </div>
        </div>
      `;
    }).join('');
  }

  // ====== HEAP MEMORY GRAPH ======

  function drawHeapGraph(memoryHistory) {
    const canvas = document.getElementById('heapGraph');
    if (!canvas || !memoryHistory || memoryHistory.length < 2) return;

    const ctx = canvas.getContext('2d');
    const container = canvas.parentElement;
    canvas.width = container.offsetWidth;
    canvas.height = 200;

    const width = canvas.width;
    const height = canvas.height;
    const padding = { top: 10, right: 10, bottom: 25, left: 45 };
    const graphW = width - padding.left - padding.right;
    const graphH = height - padding.top - padding.bottom;

    // Clear
    ctx.clearRect(0, 0, width, height);

    const maxMb = memoryHistory[0].max || 1;
    const points = memoryHistory.length;

    // Background
    ctx.fillStyle = 'rgba(0,0,0,0.2)';
    ctx.fillRect(padding.left, padding.top, graphW, graphH);

    // Grid lines
    ctx.strokeStyle = 'rgba(255,255,255,0.08)';
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (graphH / 4) * i;
      ctx.beginPath();
      ctx.moveTo(padding.left, y);
      ctx.lineTo(width - padding.right, y);
      ctx.stroke();

      ctx.fillStyle = 'rgba(255,255,255,0.4)';
      ctx.font = '10px "Plus Jakarta Sans", sans-serif';
      ctx.textAlign = 'right';
      ctx.fillText(Math.round(maxMb * (1 - i / 4)) + 'MB', padding.left - 5, y + 4);
    }

    // Draw old gen area fill
    ctx.beginPath();
    ctx.moveTo(padding.left, padding.top + graphH);
    for (let i = 0; i < points; i++) {
      const x = padding.left + (i / (points - 1)) * graphW;
      const y = padding.top + graphH - (memoryHistory[i].oldGen / maxMb) * graphH;
      ctx.lineTo(x, y);
    }
    ctx.lineTo(padding.left + graphW, padding.top + graphH);
    ctx.closePath();
    ctx.fillStyle = 'rgba(231, 76, 60, 0.2)';
    ctx.fill();

    // Draw old gen line
    ctx.beginPath();
    for (let i = 0; i < points; i++) {
      const x = padding.left + (i / (points - 1)) * graphW;
      const y = padding.top + graphH - (memoryHistory[i].oldGen / maxMb) * graphH;
      i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
    }
    ctx.strokeStyle = '#e74c3c';
    ctx.lineWidth = 1.5;
    ctx.stroke();

    // Draw used memory area fill
    ctx.beginPath();
    ctx.moveTo(padding.left, padding.top + graphH);
    for (let i = 0; i < points; i++) {
      const x = padding.left + (i / (points - 1)) * graphW;
      const y = padding.top + graphH - (memoryHistory[i].used / maxMb) * graphH;
      ctx.lineTo(x, y);
    }
    ctx.lineTo(padding.left + graphW, padding.top + graphH);
    ctx.closePath();
    ctx.fillStyle = 'rgba(var(--primary-rgb), 0.15)';
    ctx.fill();

    // Draw used memory line
    ctx.beginPath();
    for (let i = 0; i < points; i++) {
      const x = padding.left + (i / (points - 1)) * graphW;
      const y = padding.top + graphH - (memoryHistory[i].used / maxMb) * graphH;
      i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
    }
    ctx.strokeStyle = 'rgb(var(--primary-rgb))';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Current value label
    const last = memoryHistory[memoryHistory.length - 1];
    ctx.fillStyle = 'rgba(255,255,255,0.6)';
    ctx.font = '10px "Plus Jakarta Sans", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(last.used + '/' + last.max + ' MB', width / 2, height - 5);
  }

  // ====== GC ACTIVITY GRAPH ======

  function drawGcGraph(gcHistory) {
    const canvas = document.getElementById('gcGraph');
    if (!canvas || !gcHistory || gcHistory.length < 2) return;

    const ctx = canvas.getContext('2d');
    const container = canvas.parentElement;
    canvas.width = container.offsetWidth;
    canvas.height = 200;

    const width = canvas.width;
    const height = canvas.height;
    const padding = { top: 10, right: 10, bottom: 25, left: 45 };
    const graphW = width - padding.left - padding.right;
    const graphH = height - padding.top - padding.bottom;

    // Clear
    ctx.clearRect(0, 0, width, height);

    const points = gcHistory.length;
    const maxPause = Math.max(10, ...gcHistory.map(g => g.pauseMs));

    // Background
    ctx.fillStyle = 'rgba(0,0,0,0.2)';
    ctx.fillRect(padding.left, padding.top, graphW, graphH);

    // Grid lines
    ctx.strokeStyle = 'rgba(255,255,255,0.08)';
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (graphH / 4) * i;
      ctx.beginPath();
      ctx.moveTo(padding.left, y);
      ctx.lineTo(width - padding.right, y);
      ctx.stroke();

      ctx.fillStyle = 'rgba(255,255,255,0.4)';
      ctx.font = '10px "Plus Jakarta Sans", sans-serif';
      ctx.textAlign = 'right';
      ctx.fillText(Math.round(maxPause * (1 - i / 4)) + 'ms', padding.left - 5, y + 4);
    }

    // Draw pause time bars
    const barWidth = Math.max(2, graphW / points - 1);
    for (let i = 0; i < points; i++) {
      const x = padding.left + (i / (points - 1)) * graphW - barWidth / 2;
      const barH = (gcHistory[i].pauseMs / maxPause) * graphH;
      const y = padding.top + graphH - barH;

      // Color based on pause time severity
      if (gcHistory[i].pauseMs > 50) {
        ctx.fillStyle = 'rgba(231, 76, 60, 0.8)';
      } else if (gcHistory[i].pauseMs > 20) {
        ctx.fillStyle = 'rgba(245, 158, 11, 0.8)';
      } else {
        ctx.fillStyle = 'rgba(52, 152, 219, 0.6)';
      }
      ctx.fillRect(x, y, barWidth, barH);
    }

    // Total collections label
    const last = gcHistory[gcHistory.length - 1];
    ctx.fillStyle = 'rgba(255,255,255,0.6)';
    ctx.font = '10px "Plus Jakarta Sans", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('Total: ' + last.count + ' collections | ' + last.timeMs + 'ms total pause', width / 2, height - 5);
  }

  // ====== ALERT TIER BADGE ======

  function updateAlertTier(tier) {
    const badge = document.getElementById('alertTierBadge');
    if (!badge) return;

    if (tier === 'NONE') {
      badge.style.display = 'none';
    } else {
      badge.style.display = 'inline-flex';
      const cls = tier === 'CRITICAL' ? 'bad' : tier === 'WARNING' ? 'warn' : 'gray';
      badge.className = 'badge ' + cls;
      badge.textContent = tier;
    }
  }

  // ====== TPS THROTTLE ======

  function requestThrottleStatus() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) ws.send('GET_THROTTLE_STATUS', {});
  }

  const THROTTLE_DESCRIPTIONS = {
    REDUCE_MOB_CAP: 'Lowers monster spawn limits',
    SLOW_HOPPER_TRANSFER: 'Reduces hopper transfer speed',
    REDUCE_RANDOM_TICK_SPEED: 'Lowers crop/grass growth speed',
    REDUCE_MOB_AI_RANGE: 'Decreases mob activation range',
    REDUCE_CHUNK_LOAD_RATE: 'Throttles chunk loading speed',
    DISABLE_MOB_SPAWNING: 'Completely stops mob spawning',
    INCREASE_ENTITY_DESPAWN_RATES: 'Entities despawn faster',
    REDUCE_VIEW_DISTANCE: 'Lowers server view distance',
    PAUSE_REDSTONE_UPDATES: 'Disables redstone tick updates',
    KILL_EXCESS_ENTITIES: 'Removes distant non-named mobs'
  };

  function updateThrottleStatus(data) {
    const container = document.getElementById('throttleActionCards');
    const overrideBadge = document.getElementById('throttleOverrideBadge');
    if (!container) return;

    if (overrideBadge) {
      overrideBadge.style.display = data.manualOverride ? 'inline-flex' : 'none';
    }

    const actions = data.actions || [];
    if (!actions.length) {
      container.innerHTML = '<div style="color:var(--text-secondary)">No throttle actions configured.</div>';
      return;
    }

    let html = '';
    for (const a of actions) {
      const name = a.name || a.action || '';
      const displayName = name.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
      const desc = THROTTLE_DESCRIPTIONS[name] || '';
      const engaged = a.currentlyEngaged || false;
      const statusColor = engaged ? 'var(--danger)' : 'var(--ok)';
      const statusText = engaged ? 'ENGAGED' : 'Idle';

      html += `<div class="panel pad" data-throttle="${name}">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <div>
            <h3 style="margin:0;font-size:14px">${displayName}</h3>
            <small style="color:var(--text-secondary)">${desc}</small>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <span class="badge" style="background:${statusColor}">${statusText}</span>
            <button class="toggle-btn ${a.enabled ? 'on' : ''}" id="throttleEnable_${name}" onclick="toggleThrottleEnabled('${name}')" aria-pressed="${a.enabled}">${a.enabled ? 'ON' : 'OFF'}</button>
          </div>
        </div>
        <div class="grid cols-2" style="gap:8px">
          <div class="config-item">
            <label>Engage TPS <span style="float:right" id="throttleEngage_${name}_val">${a.engageTpsThreshold || 15}</span></label>
            <input type="range" class="slider" id="throttleEngage_${name}" min="1" max="20" step="0.5" value="${a.engageTpsThreshold || 15}" oninput="document.getElementById('throttleEngage_${name}_val').textContent=this.value">
          </div>
          <div class="config-item">
            <label>Disengage TPS <span style="float:right" id="throttleDisengage_${name}_val">${a.disengageTpsThreshold || 18}</span></label>
            <input type="range" class="slider" id="throttleDisengage_${name}" min="1" max="20" step="0.5" value="${a.disengageTpsThreshold || 18}" oninput="document.getElementById('throttleDisengage_${name}_val').textContent=this.value">
          </div>
          <div class="config-item">
            <label>Cooldown (sec)</label>
            <input type="number" class="input" id="throttleCooldown_${name}" value="${a.cooldownSeconds || 60}" min="5" max="600" style="width:100%">
          </div>
          <div class="config-item">
            <label>Intensity <span style="float:right" id="throttleIntensity_${name}_val">${Math.round((a.intensity || 0.5) * 100)}%</span></label>
            <input type="range" class="slider" id="throttleIntensity_${name}" min="0" max="100" value="${Math.round((a.intensity || 0.5) * 100)}" oninput="document.getElementById('throttleIntensity_${name}_val').textContent=this.value+'%'">
          </div>
        </div>
        <div style="display:flex;gap:8px;margin-top:12px">
          <button class="btn mini primary" onclick="saveThrottleConfig('${name}')"><i class="fa-solid fa-save"></i> Save</button>
          ${engaged
            ? `<button class="btn mini danger" onclick="disengageThrottle('${name}')"><i class="fa-solid fa-stop"></i> Disengage</button>`
            : `<button class="btn mini" onclick="engageThrottle('${name}')"><i class="fa-solid fa-play"></i> Engage</button>`
          }
        </div>
      </div>`;
    }
    container.innerHTML = html;
  }

  window.toggleThrottleEnabled = function(name) {
    const btn = document.getElementById('throttleEnable_' + name);
    if (!btn) return;
    const on = btn.getAttribute('aria-pressed') !== 'true';
    btn.setAttribute('aria-pressed', on);
    btn.classList.toggle('on', on);
    btn.textContent = on ? 'ON' : 'OFF';
  };

  window.saveThrottleConfig = function(name) {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;
    const enableBtn = document.getElementById('throttleEnable_' + name);
    ws.send('UPDATE_THROTTLE_CONFIG', {
      action: name,
      enabled: enableBtn?.getAttribute('aria-pressed') === 'true',
      engageTpsThreshold: parseFloat(document.getElementById('throttleEngage_' + name)?.value || '15'),
      disengageTpsThreshold: parseFloat(document.getElementById('throttleDisengage_' + name)?.value || '18'),
      cooldownSeconds: parseInt(document.getElementById('throttleCooldown_' + name)?.value || '60'),
      intensity: parseInt(document.getElementById('throttleIntensity_' + name)?.value || '50') / 100
    });
    if (window.toast) window.toast('success', 'Saved', displayName(name) + ' config updated.');
  };

  window.engageThrottle = function(name) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('MANUAL_THROTTLE_ENGAGE', { action: name });
      if (window.toast) window.toast('info', 'Engaging', displayName(name));
    }
  };

  window.disengageThrottle = function(name) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('MANUAL_THROTTLE_DISENGAGE', { action: name });
      if (window.toast) window.toast('info', 'Disengaging', displayName(name));
    }
  };

  function displayName(name) {
    return (name || '').replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
  }

  // ====== MEMORY MANAGEMENT ======

  function requestMemoryStatus() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) ws.send('GET_MEMORY_STATUS', {});
  }

  function requestGcRecommendations() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) ws.send('GET_GC_RECOMMENDATIONS', {});
  }

  function updateMemoryStatus(data) {
    if (!data) return;

    // Pressure level
    const levelEl = document.getElementById('memPressureLevel');
    const iconEl = document.getElementById('pressureIcon');
    if (levelEl) {
      const level = data.pressureLevel || 0;
      levelEl.textContent = 'Level ' + level;
      const colors = ['var(--ok)', 'var(--ok)', 'var(--warn)', 'var(--bad)', 'var(--danger)'];
      if (iconEl) iconEl.style.color = colors[level] || 'var(--ok)';
    }

    // Heap stats
    const usedMax = document.getElementById('memUsedMax');
    if (usedMax) usedMax.textContent = (data.usedMb || 0) + ' / ' + (data.maxMb || 0) + ' MB';

    const heapBar = document.getElementById('memHeapBar');
    if (heapBar) heapBar.style.width = (data.heapPercent || 0) + '%';

    const heapPct = document.getElementById('memHeapPercent');
    if (heapPct) heapPct.textContent = Math.round(data.heapPercent || 0) + '%';

    // Leak detection
    const leakEl = document.getElementById('memLeakStatus');
    if (leakEl) {
      leakEl.textContent = data.leakDetected ? 'Leak Detected!' : 'No Leak';
      leakEl.style.color = data.leakDetected ? 'var(--danger)' : '';
    }

    // Pressure thresholds
    if (data.level1Threshold !== undefined) setSlider('memThresh1', 'memThresh1Val', Math.round(data.level1Threshold));
    if (data.level2Threshold !== undefined) setSlider('memThresh2', 'memThresh2Val', Math.round(data.level2Threshold));
    if (data.level3Threshold !== undefined) setSlider('memThresh3', 'memThresh3Val', Math.round(data.level3Threshold));
    if (data.level4Threshold !== undefined) setSlider('memThresh4', 'memThresh4Val', Math.round(data.level4Threshold));

    // Memory pools
    const poolList = document.getElementById('memPoolList');
    if (poolList && data.memoryPools) {
      let html = '<table class="data-table" style="width:100%"><thead><tr><th>Pool</th><th>Used</th><th>Max</th><th>%</th></tr></thead><tbody>';
      for (const p of data.memoryPools) {
        const pct = p.max > 0 ? Math.round(p.used / p.max * 100) : 0;
        const color = pct > 90 ? 'var(--danger)' : pct > 70 ? 'var(--warn)' : '';
        html += `<tr><td>${p.name}</td><td>${p.used} MB</td><td>${p.max} MB</td><td style="color:${color}">${pct}%</td></tr>`;
      }
      html += '</tbody></table>';
      poolList.innerHTML = html;
    }

    // GC collectors
    const gcList = document.getElementById('memGcCollectors');
    if (gcList && data.gcCollectors) {
      let html = '<table class="data-table" style="width:100%"><thead><tr><th>Collector</th><th>Collections</th><th>Total Pause</th></tr></thead><tbody>';
      for (const gc of data.gcCollectors) {
        html += `<tr><td>${gc.name}</td><td>${gc.count}</td><td>${gc.timeMs} ms</td></tr>`;
      }
      html += '</tbody></table>';
      gcList.innerHTML = html;
    }
  }

  function setSlider(sliderId, labelId, value) {
    const slider = document.getElementById(sliderId);
    const label = document.getElementById(labelId);
    if (slider && document.activeElement !== slider) slider.value = value;
    if (label) label.textContent = value + '%';
  }

  function updateGcRecommendations(data) {
    const container = document.getElementById('memGcRecommendations');
    if (!container || !data) return;

    let html = '';
    html += `<div class="stat-row"><span class="stat-label">Current GC</span><span><code>${data.currentGc || 'Unknown'}</code></span></div>`;
    html += `<div class="stat-row"><span class="stat-label">Max Heap</span><span>${data.maxHeapMb || 0} MB</span></div>`;

    if (data.jvmFlags && data.jvmFlags.length) {
      html += `<div style="margin:12px 0 8px;font-weight:600;font-size:13px">JVM Flags</div>`;
      html += `<code style="font-size:11px;word-break:break-all;display:block;padding:8px;background:rgba(0,0,0,0.2);border-radius:var(--radius)">${data.jvmFlags.join(' ')}</code>`;
    }

    if (data.tips && data.tips.length) {
      html += `<div style="margin:16px 0 8px;font-weight:600;font-size:13px">Recommendations</div>`;
      for (const tip of data.tips) {
        html += `<div style="padding:6px 0;border-bottom:1px solid var(--border);font-size:13px;color:var(--text-secondary)"><i class="fa-solid fa-lightbulb" style="color:var(--warn);margin-right:8px"></i>${tip}</div>`;
      }
    } else {
      html += `<div style="margin-top:12px;color:var(--ok);font-size:13px"><i class="fa-solid fa-check-circle"></i> GC configuration looks good!</div>`;
    }

    container.innerHTML = html;
  }

  window.saveMemoryThresholds = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;
    ws.send('UPDATE_MEMORY_THRESHOLDS', {
      level1Threshold: parseInt(document.getElementById('memThresh1')?.value || '70'),
      level2Threshold: parseInt(document.getElementById('memThresh2')?.value || '80'),
      level3Threshold: parseInt(document.getElementById('memThresh3')?.value || '90'),
      level4Threshold: parseInt(document.getElementById('memThresh4')?.value || '95')
    });
    if (window.toast) window.toast('success', 'Saved', 'Memory pressure thresholds updated.');
  };

  window.triggerGc = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) ws.send('TRIGGER_GC', {});
  };

  // ====== PERFORMANCE TUNING ======

  function requestPerformanceSettings() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) ws.send('GET_PERFORMANCE_SETTINGS', {});
  }

  function updatePerformanceSettingsUI(data) {
    if (!data) return;

    function setVal(id, labelId, val, defaultVal, defaultLabel) {
      const slider = document.getElementById(id);
      const label = document.getElementById(labelId);
      if (slider && document.activeElement !== slider) slider.value = val;
      if (label) {
        if (val == defaultVal) {
          label.textContent = val + ' (' + (defaultLabel || 'default') + ')';
        } else {
          label.textContent = val;
        }
      }
    }

    setVal('tuneViewDist', 'tuneViewDistVal', data.viewDistance ?? -1, -1, 'default');
    setVal('tuneSimDist', 'tuneSimDistVal', data.simulationDistance ?? -1, -1, 'default');
    if (document.getElementById('tuneMonsterVal')) document.getElementById('tuneMonsterVal').textContent = data.monsterSpawnLimit ?? 70;
    if (document.getElementById('tuneMonster') && document.activeElement !== document.getElementById('tuneMonster')) document.getElementById('tuneMonster').value = data.monsterSpawnLimit ?? 70;
    if (document.getElementById('tuneAnimalVal')) document.getElementById('tuneAnimalVal').textContent = data.animalSpawnLimit ?? 10;
    if (document.getElementById('tuneAnimal') && document.activeElement !== document.getElementById('tuneAnimal')) document.getElementById('tuneAnimal').value = data.animalSpawnLimit ?? 10;
    if (document.getElementById('tuneWaterVal')) document.getElementById('tuneWaterVal').textContent = data.waterAnimalSpawnLimit ?? 5;
    if (document.getElementById('tuneWater') && document.activeElement !== document.getElementById('tuneWater')) document.getElementById('tuneWater').value = data.waterAnimalSpawnLimit ?? 5;
    if (document.getElementById('tuneAmbientVal')) document.getElementById('tuneAmbientVal').textContent = data.ambientSpawnLimit ?? 15;
    if (document.getElementById('tuneAmbient') && document.activeElement !== document.getElementById('tuneAmbient')) document.getElementById('tuneAmbient').value = data.ambientSpawnLimit ?? 15;

    const mobMult = data.mobSpawnMultiplier ?? 1.0;
    if (document.getElementById('tuneMobMultVal')) document.getElementById('tuneMobMultVal').textContent = mobMult.toFixed(1) + 'x';
    if (document.getElementById('tuneMobMult') && document.activeElement !== document.getElementById('tuneMobMult')) document.getElementById('tuneMobMult').value = Math.round(mobMult * 100);

    if (document.getElementById('tuneRtickVal')) document.getElementById('tuneRtickVal').textContent = data.randomTickSpeed ?? 3;
    if (document.getElementById('tuneRtick') && document.activeElement !== document.getElementById('tuneRtick')) document.getElementById('tuneRtick').value = data.randomTickSpeed ?? 3;
    if (document.getElementById('tuneHopperVal')) document.getElementById('tuneHopperVal').textContent = data.hopperTransferRate ?? 8;
    if (document.getElementById('tuneHopper') && document.activeElement !== document.getElementById('tuneHopper')) document.getElementById('tuneHopper').value = data.hopperTransferRate ?? 8;
    if (document.getElementById('tuneDespawnVal')) document.getElementById('tuneDespawnVal').textContent = data.itemDespawnRate ?? 6000;
    if (document.getElementById('tuneDespawn') && document.activeElement !== document.getElementById('tuneDespawn')) document.getElementById('tuneDespawn').value = data.itemDespawnRate ?? 6000;
    setVal('tuneAutosave', 'tuneAutosaveVal', data.maxAutoSaveChunksPerTick ?? -1, -1, 'default');
    setVal('tuneEntLimit', 'tuneEntLimitVal', data.entityLimitPerChunk ?? -1, -1, 'unlimited');
    setVal('tuneTileLimit', 'tuneTileLimitVal', data.tileEntityLimitPerChunk ?? -1, -1, 'unlimited');

    // Toggles
    updateTuneToggle('tuneFireSpread', data.disableFireSpread || false);
    updateTuneToggle('tuneIceFormation', data.disableIceFormation || false);
  }

  function updateTuneToggle(id, active) {
    const btn = document.getElementById(id);
    if (!btn) return;
    btn.setAttribute('aria-pressed', active ? 'true' : 'false');
    btn.classList.toggle('on', active);
    btn.textContent = active ? 'ON' : 'OFF';
  }

  window.toggleTuneBool = function(id) {
    const btn = document.getElementById(id);
    if (!btn) return;
    const on = btn.getAttribute('aria-pressed') !== 'true';
    btn.setAttribute('aria-pressed', on);
    btn.classList.toggle('on', on);
    btn.textContent = on ? 'ON' : 'OFF';
  };

  window.updateTuneLabel = function(labelId, value, defaultVal, defaultLabel) {
    const el = document.getElementById(labelId);
    if (!el) return;
    if (parseInt(value) === defaultVal) {
      el.textContent = value + ' (' + (defaultLabel || 'default') + ')';
    } else {
      el.textContent = value;
    }
  };

  window.savePerformanceSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;
    ws.send('UPDATE_PERFORMANCE_SETTINGS', {
      viewDistance: parseInt(document.getElementById('tuneViewDist')?.value || '-1'),
      simulationDistance: parseInt(document.getElementById('tuneSimDist')?.value || '-1'),
      monsterSpawnLimit: parseInt(document.getElementById('tuneMonster')?.value || '70'),
      animalSpawnLimit: parseInt(document.getElementById('tuneAnimal')?.value || '10'),
      waterAnimalSpawnLimit: parseInt(document.getElementById('tuneWater')?.value || '5'),
      ambientSpawnLimit: parseInt(document.getElementById('tuneAmbient')?.value || '15'),
      mobSpawnMultiplier: parseInt(document.getElementById('tuneMobMult')?.value || '100') / 100,
      randomTickSpeed: parseInt(document.getElementById('tuneRtick')?.value || '3'),
      hopperTransferRate: parseInt(document.getElementById('tuneHopper')?.value || '8'),
      itemDespawnRate: parseInt(document.getElementById('tuneDespawn')?.value || '6000'),
      maxAutoSaveChunksPerTick: parseInt(document.getElementById('tuneAutosave')?.value || '-1'),
      entityLimitPerChunk: parseInt(document.getElementById('tuneEntLimit')?.value || '-1'),
      tileEntityLimitPerChunk: parseInt(document.getElementById('tuneTileLimit')?.value || '-1'),
      disableFireSpread: document.getElementById('tuneFireSpread')?.getAttribute('aria-pressed') === 'true',
      disableIceFormation: document.getElementById('tuneIceFormation')?.getAttribute('aria-pressed') === 'true'
    });
    if (window.toast) window.toast('success', 'Applied', 'Performance settings applied to server.');
  };

  window.resetPerformanceDefaults = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;
    ws.send('UPDATE_PERFORMANCE_SETTINGS', {
      viewDistance: -1, simulationDistance: -1, randomTickSpeed: 3,
      monsterSpawnLimit: 70, animalSpawnLimit: 10, waterAnimalSpawnLimit: 5,
      ambientSpawnLimit: 15, mobSpawnMultiplier: 1.0, hopperTransferRate: 8,
      entityLimitPerChunk: -1, tileEntityLimitPerChunk: -1, itemDespawnRate: 6000,
      disableFireSpread: false, disableIceFormation: false, maxAutoSaveChunksPerTick: -1
    });
    if (window.toast) window.toast('success', 'Reset', 'Performance settings reset to vanilla defaults.');
  };

  registerHandlers();
  console.log('[ModereX Status] Module loaded');
})();


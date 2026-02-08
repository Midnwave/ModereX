/**
 * ModereX Staff Panel - Simulation Engine
 */

const state = {
  currentPage: 'dashboard',
  search: '',
  filters: { punishmentType: 'all' },
  pagination: {
    players: { page: 1, size: 25 },
    punishments: { page: 1, size: 25 },
    chatlog: { page: 1, size: 50 }
  },
  watchlist: new Set(),
  authenticated: false
};

// === INIT ===
document.addEventListener('DOMContentLoaded', () => {
  const tokenInput = document.getElementById('authToken');
  if (tokenInput) tokenInput.value = window.DEMO_DATA.generateRandomToken();
  updateTime();
  setInterval(updateTime, 1000);
  // Simulate ping jitter
  setInterval(() => {
    const ping = document.getElementById('pingValue');
    if (ping) ping.textContent = (8 + Math.floor(Math.random() * 20)) + 'ms';
  }, 3000);
});

// === AUTH ===
function simulateAuth() {
  const authForm = document.getElementById('authForm');
  const authStatus = document.getElementById('authStatusArea');

  authForm.style.display = 'none';
  authStatus.style.display = 'flex';

  document.getElementById('authStatusText').textContent = 'Authenticating...';
  document.getElementById('authStatusSub').textContent = 'Verifying token';
  document.getElementById('authSpinner').style.display = 'block';
  document.getElementById('authSuccessCheck').style.display = 'none';

  setTimeout(() => {
    document.getElementById('authStatusText').textContent = 'Loading server data...';
    document.getElementById('authStatusSub').textContent = 'Syncing punishments and player data';
  }, 800);

  setTimeout(() => {
    document.getElementById('authSpinner').style.display = 'none';
    document.getElementById('authSuccessCheck').style.display = 'block';
    document.getElementById('authStatusText').textContent = 'Connected';
    document.getElementById('authStatusText').style.color = '#10b981';
    document.getElementById('authStatusSub').textContent = 'Authenticated as Admin';
    document.getElementById('authStatusArea').classList.add('success');
  }, 1800);

  setTimeout(() => {
    const overlay = document.getElementById('authOverlay');
    overlay.classList.add('hide');
    document.getElementById('mainLayout').style.display = 'flex';
    state.authenticated = true;
    renderCurrentPage();
    toast('ok', 'Connected', 'WebSocket connection established');

    // Start live simulation
    startLiveSimulation();
  }, 2600);
}

// === LIVE SIMULATION ===
function startLiveSimulation() {
  // Simulate random events
  setInterval(() => {
    if (!state.authenticated) return;
    const D = window.DEMO_DATA;
    const events = [
      () => {
        const p = D.players[Math.floor(Math.random() * D.players.length)];
        if (p.status === 'offline') {
          p.status = 'online';
          p.lastSeen = Date.now();
        }
      },
      () => {
        const online = D.players.filter(p => p.status === 'online');
        if (online.length > 10) {
          const p = online[Math.floor(Math.random() * online.length)];
          p.status = 'offline';
          p.lastSeen = Date.now();
        }
      }
    ];
    events[Math.floor(Math.random() * events.length)]();

    // Update online count in topbar
    const onlineCount = D.players.filter(p => p.status === 'online').length;
    const el = document.getElementById('onlineCount');
    if (el) el.textContent = onlineCount + ' Online';

    // Refresh if on dashboard
    if (state.currentPage === 'dashboard') renderDashboard();
  }, 8000);
}

// === NAVIGATION ===
function go(page) {
  state.currentPage = page;
  document.querySelectorAll('.sb-item').forEach(i => i.classList.toggle('active', i.dataset.page === page));
  document.querySelectorAll('.page').forEach(p => p.classList.toggle('active', p.id === `page-${page}`));
  renderCurrentPage();
}

function renderCurrentPage() {
  const D = window.DEMO_DATA;
  switch(state.currentPage) {
    case 'dashboard': renderDashboard(); break;
    case 'players': renderPlayers(); break;
    case 'punishments': renderPunishments(); break;
    case 'automod': renderAutomod(); break;
    case 'watchlist': renderWatchlist(); break;
    case 'activity': renderActivity(); break;
    case 'chatlog': renderChatlog(); break;
  }
}

function toggleSidebar() { document.getElementById('sidebar').classList.toggle('show'); }

// === RENDER: DASHBOARD ===
function renderDashboard() {
  const D = window.DEMO_DATA;
  const onlinePlayers = D.players.filter(p => p.status === 'online').length;
  const activeBans = D.punishments.filter(p => p.type === 'BAN' && p.active).length;
  const warns24h = D.punishments.filter(p => p.type === 'WARN' && p.createdAt > Date.now() - 86400000).length;
  const automodCount = D.automodEvents ? D.automodEvents.length : 0;

  document.getElementById('stat-online').textContent = onlinePlayers;
  document.getElementById('stat-bans').textContent = activeBans;
  document.getElementById('stat-warns').textContent = warns24h;
  document.getElementById('stat-automod').textContent = automodCount;
  document.getElementById('onlineCount').textContent = onlinePlayers + ' Online';

  // Recent punishments
  const recent = D.punishments.slice(0, 10);
  document.getElementById('recentPunishmentsTable').innerHTML = recent.map(p => `
    <tr onclick="openDrawer('${p.playerId}')" style="cursor:pointer">
      <td>${playerCell(p.playerId, p.playerName)}</td>
      <td>${typeBadge(p.type)}</td>
      <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${esc(p.reason)}</td>
      <td>${esc(p.staff)}</td>
      <td>${fmtTime(p.createdAt)}</td>
    </tr>
  `).join('');

  // Recent chat
  if (D.chatMessages) {
    const recentChat = D.chatMessages.slice(0, 20);
    document.getElementById('recentChatTable').innerHTML = recentChat.map(m => `
      <tr>
        <td style="white-space:nowrap;color:var(--muted);font-size:12px;">${fmtTime(m.t)}</td>
        <td style="font-weight:500;">${esc(m.player)}</td>
        <td style="color:var(--text-secondary);">${esc(m.message)}</td>
      </tr>
    `).join('');
  }
}

// === RENDER: PLAYERS ===
function renderPlayers() {
  const D = window.DEMO_DATA;
  let filtered = D.players;
  if (state.search) {
    const s = state.search.toLowerCase();
    filtered = filtered.filter(p => p.name.toLowerCase().includes(s));
  }
  const { page, size } = state.pagination.players;
  const paginated = filtered.slice((page-1)*size, page*size);

  document.getElementById('playersTable').innerHTML = paginated.map(p => {
    const statusCls = p.status === 'online' ? 'green' : p.status === 'afk' ? 'yellow' : 'gray';
    const flagCls = p.flags > 3 ? 'red' : p.flags > 0 ? 'yellow' : 'gray';
    return `
      <tr onclick="openDrawer('${p.id}')" style="cursor:pointer">
        <td>${playerCell(p.id, p.name)}</td>
        <td><span class="badge ${p.platform === 'Java' ? 'blue' : 'purple'}">${p.platform}</span></td>
        <td><span class="badge ${statusCls}">${p.status.charAt(0).toUpperCase() + p.status.slice(1)}</span></td>
        <td>${fmtTime(p.lastSeen)}</td>
        <td><span class="badge ${flagCls}">${p.flags} flags</span></td>
        <td>
          <button class="btn ghost" style="padding:6px 10px;font-size:12px;" onclick="event.stopPropagation();openPunishToast('${p.name}')">
            <i class="fas fa-gavel"></i>
          </button>
        </td>
      </tr>
    `;
  }).join('');
  renderPagination('players', filtered.length);
}

// === RENDER: PUNISHMENTS ===
function renderPunishments() {
  const D = window.DEMO_DATA;
  let filtered = D.punishments;
  if (state.filters.punishmentType !== 'all') filtered = filtered.filter(p => p.type === state.filters.punishmentType);
  if (state.search) {
    const s = state.search.toLowerCase();
    filtered = filtered.filter(p => p.playerName.toLowerCase().includes(s) || p.reason.toLowerCase().includes(s));
  }
  const { page, size } = state.pagination.punishments;
  const paginated = filtered.slice((page-1)*size, page*size);

  document.getElementById('punishmentsTable').innerHTML = paginated.map(p => `
    <tr onclick="openDrawer('${p.playerId}')" style="cursor:pointer">
      <td>${playerCell(p.playerId, p.playerName)}</td>
      <td style="font-family:'JetBrains Mono',monospace;font-size:12px;color:var(--muted);">${p.id}</td>
      <td>${typeBadge(p.type)}</td>
      <td style="max-width:180px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${esc(p.reason)}</td>
      <td>${esc(p.staff)}</td>
      <td>${p.duration || '-'}</td>
      <td>${fmtTime(p.createdAt)}</td>
    </tr>
  `).join('');
  renderPagination('punishments', filtered.length);
}

// === RENDER: AUTOMOD ===
function renderAutomod() {
  const D = window.DEMO_DATA;
  if (!D.automodEvents) return;

  document.getElementById('automodTable').innerHTML = D.automodEvents.slice(0, 50).map(e => `
    <tr>
      <td style="white-space:nowrap;">${fmtTime(e.t)}</td>
      <td style="font-weight:500;">${esc(e.player)}</td>
      <td><span class="badge blue">${esc(e.rule)}</span></td>
      <td style="max-width:250px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--text-secondary);">${esc(e.message)}</td>
      <td><span class="badge ${e.action === 'Muted' ? 'orange' : e.action === 'Warned' ? 'yellow' : 'gray'}">${e.action}</span></td>
    </tr>
  `).join('');
}

// === RENDER: WATCHLIST ===
function renderWatchlist() {
  const D = window.DEMO_DATA;
  if (!D.watchlist) return;

  document.getElementById('watchlistTable').innerHTML = D.watchlist.map(w => `
    <tr onclick="openDrawer('${w.playerId}')" style="cursor:pointer">
      <td>${playerCell(w.playerId, w.playerName)}</td>
      <td>${esc(w.reason)}</td>
      <td>${esc(w.addedBy)}</td>
      <td>${fmtTime(w.addedAt)}</td>
      <td><span class="badge ${w.priority === 'high' ? 'red' : w.priority === 'medium' ? 'yellow' : 'gray'}">${w.priority}</span></td>
    </tr>
  `).join('');
}

// === RENDER: ACTIVITY ===
function renderActivity() {
  const D = window.DEMO_DATA;
  document.getElementById('activityTable').innerHTML = D.activity.slice(0, 50).map(a => `
    <tr>
      <td style="white-space:nowrap;">${fmtTime(a.t)}</td>
      <td style="font-weight:500;">${esc(a.actor)}</td>
      <td>${esc(a.action)}</td>
      <td>${esc(a.target)}</td>
      <td style="color:var(--muted);font-size:12px;">${esc(a.detail || '')}</td>
    </tr>
  `).join('');
}

// === RENDER: CHAT LOG ===
function renderChatlog() {
  const D = window.DEMO_DATA;
  if (!D.chatMessages) return;

  let filtered = D.chatMessages;
  if (state.search) {
    const s = state.search.toLowerCase();
    filtered = filtered.filter(m => m.player.toLowerCase().includes(s) || m.message.toLowerCase().includes(s));
  }
  const { page, size } = state.pagination.chatlog;
  const paginated = filtered.slice((page-1)*size, page*size);

  document.getElementById('chatlogTable').innerHTML = paginated.map(m => `
    <tr>
      <td style="white-space:nowrap;color:var(--muted);font-size:12px;">${fmtTime(m.t)}</td>
      <td style="font-weight:500;">${esc(m.player)}</td>
      <td style="color:var(--text-secondary);">${esc(m.message)}</td>
    </tr>
  `).join('');
  renderPagination('chatlog', filtered.length);
}

// === PAGINATION ===
function renderPagination(type, total) {
  const pag = state.pagination[type];
  if (!pag) return;
  const totalPages = Math.ceil(total / pag.size);
  const container = document.getElementById(`${type}Pagination`);
  if (!container || totalPages <= 1) { if (container) container.innerHTML = ''; return; }

  let btns = '';
  const start = Math.max(1, pag.page - 2);
  const end = Math.min(totalPages, start + 4);
  for (let i = start; i <= end; i++) {
    btns += `<button class="pag-btn ${i === pag.page ? 'active' : ''}" onclick="changePage('${type}',${i})">${i}</button>`;
  }

  container.innerHTML = `<div class="pag-controls">
    <span style="font-size:12px;color:var(--muted);margin-right:12px;">${total} results</span>
    <button class="pag-btn" onclick="changePage('${type}',${pag.page-1})" ${pag.page===1?'disabled':''}><i class="fas fa-chevron-left"></i></button>
    ${btns}
    <button class="pag-btn" onclick="changePage('${type}',${pag.page+1})" ${pag.page===totalPages?'disabled':''}><i class="fas fa-chevron-right"></i></button>
  </div>`;
}

function changePage(type, page) {
  if (page < 1) return;
  state.pagination[type].page = page;
  renderCurrentPage();
}

// === FILTERS & SEARCH ===
function filterPunishments(type) {
  state.filters.punishmentType = type;
  state.pagination.punishments.page = 1;
  document.querySelectorAll('.chip-btn').forEach(b => b.classList.toggle('active', b.dataset.type === type));
  renderPunishments();
}

function handleSearch(value) {
  state.search = value;
  Object.keys(state.pagination).forEach(k => state.pagination[k].page = 1);
  renderCurrentPage();
}

// === DRAWER ===
function openDrawer(playerId) {
  const D = window.DEMO_DATA;
  const player = D.players.find(p => p.id === playerId);
  if (!player) return;
  const puns = D.punishments.filter(p => p.playerId === playerId);
  const active = puns.filter(p => p.active);
  const past = puns.filter(p => !p.active);

  const statusCls = player.status === 'online' ? 'green' : player.status === 'afk' ? 'yellow' : 'gray';

  document.getElementById('playerDrawer').innerHTML = `
    <div style="padding:24px;border-bottom:1px solid var(--border);display:flex;align-items:center;gap:16px;">
      <img src="https://crafatar.com/avatars/${player.uuid}?size=64&overlay" style="width:56px;height:56px;border-radius:12px;" onerror="this.style.background='var(--bg2)';this.style.width='56px';this.style.height='56px';">
      <div style="flex:1;">
        <h3 style="margin:0;">${esc(player.name)}</h3>
        <div style="display:flex;gap:6px;margin-top:6px;">
          <span class="badge ${player.platform==='Java'?'blue':'purple'}">${player.platform}</span>
          <span class="badge ${statusCls}">${player.status}</span>
          ${player.flags > 0 ? `<span class="badge ${player.flags > 3 ? 'red' : 'yellow'}">${player.flags} flags</span>` : ''}
        </div>
      </div>
      <button class="btn ghost" onclick="closeDrawer()" style="padding:8px;"><i class="fas fa-times"></i></button>
    </div>

    <div style="padding:20px;overflow-y:auto;max-height:calc(100vh - 120px);">
      <div style="margin-bottom:24px;">
        <h4 style="margin-bottom:12px;display:flex;align-items:center;gap:8px;"><i class="fas fa-gavel" style="opacity:0.5;"></i>Active Punishments (${active.length})</h4>
        ${active.length === 0 ? '<p style="color:var(--muted);font-size:13px;">No active punishments</p>' : active.map(p => `
          <div style="padding:12px;border:1px solid var(--border);border-radius:8px;margin-bottom:8px;background:rgba(0,0,0,0.15);">
            ${typeBadge(p.type)} <span style="margin-left:8px;">${esc(p.reason)}</span>
            <p style="color:var(--muted);font-size:11px;margin-top:6px;">By ${esc(p.staff)} &middot; ${fmtTime(p.createdAt)} ${p.duration ? '&middot; ' + p.duration : ''}</p>
          </div>
        `).join('')}
      </div>

      <div style="margin-bottom:24px;">
        <h4 style="margin-bottom:12px;display:flex;align-items:center;gap:8px;"><i class="fas fa-history" style="opacity:0.5;"></i>History (${past.length})</h4>
        ${past.slice(0, 8).map(p => `
          <div style="padding:8px 0;border-bottom:1px solid var(--border);font-size:13px;">
            ${typeBadge(p.type)} <span style="margin-left:8px;color:var(--text-secondary);">${esc(p.reason)}</span>
            <span style="float:right;color:var(--muted);font-size:11px;">${fmtTime(p.createdAt)}</span>
          </div>
        `).join('')}
      </div>

      <div style="margin-bottom:24px;">
        <h4 style="margin-bottom:12px;display:flex;align-items:center;gap:8px;"><i class="fas fa-info-circle" style="opacity:0.5;"></i>Info</h4>
        <div style="font-size:13px;display:flex;flex-direction:column;gap:6px;">
          <div><strong>UUID:</strong> <code style="font-size:11px;">${player.uuid}</code></div>
          <div><strong>IP:</strong> <code style="font-size:11px;">${player.ip}</code></div>
          <div><strong>First Seen:</strong> ${fmtTime(player.firstJoin || player.lastSeen - 86400000*30)}</div>
          <div><strong>Last Seen:</strong> ${fmtTime(player.lastSeen)}</div>
        </div>
      </div>

      <button class="btn primary" onclick="openPunishToast('${esc(player.name)}');closeDrawer();" style="width:100%;justify-content:center;">
        <i class="fas fa-gavel"></i> Punish Player
      </button>
    </div>
  `;

  document.getElementById('drawerOverlay').classList.add('show');
}

function closeDrawer() { document.getElementById('drawerOverlay').classList.remove('show'); }

function openPunishToast(name) {
  toast('info', 'Punish ' + name, 'Select a punishment type and provide a reason');
}

// === TOAST ===
let toastId = 0;
function toast(type, title, msg, opts = {}) {
  const id = ++toastId;
  const container = document.getElementById('toastContainer');
  const icons = { ok: 'check-circle', warn: 'exclamation-triangle', bad: 'times-circle', info: 'info-circle' };
  const colors = { ok: '16,185,129', warn: '245,158,11', bad: '239,68,68', info: '45,122,237' };
  const hex = { ok: '#10b981', warn: '#f59e0b', bad: '#ef4444', info: '#2d7aed' };

  const el = document.createElement('div');
  el.className = 'toast ' + type;
  el.dataset.id = id;
  el.innerHTML = `
    <div style="width:36px;height:36px;border-radius:8px;display:flex;align-items:center;justify-content:center;background:rgba(${colors[type]},0.15);flex-shrink:0;">
      <i class="fas fa-${icons[type]}" style="color:${hex[type]};"></i>
    </div>
    <div style="flex:1;min-width:0;">
      <div style="font-weight:600;font-size:13px;">${esc(title)}</div>
      <div style="font-size:12px;color:var(--text-secondary);margin-top:2px;">${esc(msg)}</div>
    </div>
    <button onclick="closeToast(${id})" style="background:none;border:none;color:var(--muted);cursor:pointer;padding:4px;"><i class="fas fa-times"></i></button>
  `;
  container.appendChild(el);
  setTimeout(() => closeToast(id), opts.ttl || 5000);
  while (container.children.length > 5) container.children[0].remove();
}

function closeToast(id) {
  const el = document.querySelector(`.toast[data-id="${id}"]`);
  if (el) { el.style.opacity = '0'; el.style.transform = 'translateX(20px)'; setTimeout(() => el.remove(), 300); }
}

// === HELPERS ===
function updateTime() {
  const el = document.getElementById('timeChip');
  if (el) el.textContent = new Date().toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit', hour12: true });
}

function fmtTime(ts) {
  const diff = Date.now() - ts;
  if (diff < 60000) return 'Just now';
  if (diff < 3600000) return Math.floor(diff / 60000) + 'm ago';
  if (diff < 86400000) return Math.floor(diff / 3600000) + 'h ago';
  if (diff < 604800000) return Math.floor(diff / 86400000) + 'd ago';
  return new Date(ts).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
}

function esc(t) { if (!t) return ''; const d = document.createElement('div'); d.textContent = t; return d.innerHTML; }

function playerCell(id, name) {
  return `<div style="display:flex;align-items:center;gap:10px;">
    <img src="https://crafatar.com/avatars/${id}?size=28&overlay" style="width:28px;height:28px;border-radius:6px;" onerror="this.style.background='var(--bg2)';this.style.width='28px';this.style.height='28px';">
    <span style="font-weight:500;">${esc(name)}</span>
  </div>`;
}

function typeBadge(type) {
  const cls = type === 'BAN' ? 'red' : type === 'MUTE' ? 'orange' : type === 'WARN' ? 'yellow' : 'gray';
  return `<span class="badge ${cls}">${type}</span>`;
}

// === INJECTED STYLES ===
const s = document.createElement('style');
s.textContent = `
  @keyframes toastIn { from { opacity:0; transform:translateX(20px); } to { opacity:1; transform:translateX(0); } }
  .toast { display:flex;align-items:center;gap:12px;padding:14px 16px;border-radius:10px;border:1px solid var(--border);background:rgba(16,24,40,0.95);backdrop-filter:blur(12px);box-shadow:0 8px 32px rgba(0,0,0,0.4);animation:toastIn 0.3s ease;transition:opacity 0.3s,transform 0.3s; }
  .layout { display:flex;min-height:100vh; }
  .drawer { position:fixed;right:0;top:0;width:480px;max-width:90vw;height:100vh;background:var(--drawer-bg, linear-gradient(180deg,rgba(16,24,40,0.98),rgba(8,12,20,0.98)));border-left:1px solid var(--border);z-index:5001; }
  .pag-controls { display:flex;gap:8px;justify-content:center;align-items:center;margin-top:16px; }
  .pag-btn { padding:8px 12px;border-radius:6px;border:1px solid var(--border);background:rgba(255,255,255,0.05);color:var(--text);cursor:pointer;font-size:13px;transition:all 0.2s; }
  .pag-btn:hover { background:rgba(255,255,255,0.08); }
  .pag-btn.active { background:var(--primary);color:white;border-color:var(--primary); }
  .pag-btn:disabled { opacity:0.4;cursor:not-allowed; }
  .chip-btn { padding:8px 14px;border-radius:8px;border:1px solid var(--border);background:rgba(255,255,255,0.04);color:var(--text);cursor:pointer;transition:all 0.2s;font-size:13px; }
  .chip-btn.active { background:rgba(45,122,237,0.15);border-color:rgba(45,122,237,0.4); }
  .chip-btn:hover { background:rgba(255,255,255,0.06); }
  .sb-label { font-size:11px;font-weight:600;text-transform:uppercase;letter-spacing:0.05em;color:var(--muted);padding:12px 20px 6px;opacity:0.7; }
  .gsearch { display:flex;align-items:center;gap:10px;padding:8px 14px;border-radius:8px;border:1px solid var(--border);background:rgba(0,0,0,0.3);max-width:400px;width:100%; }
  .page-head-right { display:flex;gap:8px; }
  code { font-family:'JetBrains Mono',monospace;font-size:12px;padding:2px 6px;background:rgba(0,0,0,0.3);border-radius:4px; }
`;
document.head.appendChild(s);

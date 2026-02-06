/* ============================================
   ModereX Control Panel - Main Application
   ============================================ */
(function() {
  const { $, $$, escapeHtml, fmtShort, fmtLong, fmtClock, uid, avatarUrl, now, pick, clamp } = window.MX.utils;
  const state = window.MX.state;
  const { createPunishment, initializeState, loadState, saveState } = window.MX.stateUtils;
  const ui = window.MX.ui;

  // Get DOM helper
  const dom = () => ui.getDom();
  const userKey = () => state.currentUser?.name || 'default';

  function loadUserPrefs() {
    const prefs = state.userPrefs?.[userKey()];
    if (!prefs) return;
    if (prefs.logsFilters) state.logsFilters = { ...state.logsFilters, ...prefs.logsFilters };
    if (typeof prefs.watchToasts === 'boolean') state.settings.watchToasts = prefs.watchToasts;
  }

  function saveUserPrefs() {
    state.userPrefs = state.userPrefs || {};
    state.userPrefs[userKey()] = {
      logsFilters: state.logsFilters,
      watchToasts: state.settings.watchToasts
    };
    saveState();
  }

  /**
   * Check if the current user has a specific permission.
   * @param {string} perm - Permission node to check (e.g., 'moderex.admin.automod')
   * @returns {boolean} true if user has the permission
   */
  function hasPermission(perm) {
    const permissions = state.permissions || [];
    // Direct match
    if (permissions.includes(perm)) return true;
    // Check wildcard permissions (e.g., moderex.admin.* includes moderex.admin.automod)
    const parts = perm.split('.');
    for (let i = parts.length - 1; i >= 1; i--) {
      const wildcard = parts.slice(0, i).join('.') + '.*';
      if (permissions.includes(wildcard)) return true;
    }
    // Check parent permission (e.g., moderex.admin includes moderex.admin.automod)
    if (parts.length > 2) {
      const parent = parts.slice(0, -1).join('.');
      if (permissions.includes(parent)) return true;
    }
    return false;
  }

  // Expose hasPermission globally
  window.hasPermission = hasPermission;

  /**
   * Apply permission restriction to an element. If user lacks permission,
   * element is disabled with visual indicators.
   * @param {HTMLElement} element - The element to restrict
   * @param {string} permission - The required permission
   * @param {string} [tooltip] - Custom tooltip message (default: "No permission")
   * @returns {boolean} true if user has permission, false if restricted
   */
  function requirePermission(element, permission, tooltip) {
    if (!element) return true;
    const hasPerm = hasPermission(permission);
    if (!hasPerm) {
      element.classList.add('no-permission', 'no-permission-tooltip');
      element.setAttribute('data-permission-tooltip', tooltip || 'You lack sufficient permissions');
      // Remove click handlers for buttons
      if (element.tagName === 'BUTTON' || element.classList.contains('btn')) {
        const clone = element.cloneNode(true);
        clone.classList.add('no-permission', 'no-permission-tooltip');
        clone.setAttribute('data-permission-tooltip', tooltip || 'You lack sufficient permissions');
        element.parentNode?.replaceChild(clone, element);
      }
    }
    return hasPerm;
  }

  /**
   * Check if user can view a specific data type based on new permission structure.
   * Maps old view types to new permission names:
   * - ip -> moderex.info.ip
   * - uuid -> moderex.info.uuid
   * - nicknames -> moderex.info.nick or moderex.history.nick
   * - commandhistory -> moderex.history.commands
   * - chathistory -> moderex.history.chat
   * - automod -> moderex.history.automod
   * - punishments -> moderex.history.* (or specific history types)
   * @param {string} viewType - The type of data to check
   * @returns {boolean} true if user can view this data type
   */
  function canView(viewType) {
    // Map old view types to new permission structure
    const permissionMap = {
      'ip': 'moderex.info.ip',
      'uuid': 'moderex.info.uuid',
      'nicknames': ['moderex.info.nick', 'moderex.history.nick'],
      'nick': ['moderex.info.nick', 'moderex.history.nick'],
      'commandhistory': 'moderex.history.commands',
      'commands': 'moderex.history.commands',
      'chathistory': 'moderex.history.chat',
      'chat': 'moderex.history.chat',
      'automod': 'moderex.history.automod',
      'punishments': 'moderex.history.*',
      'bans': 'moderex.history.bans',
      'mutes': 'moderex.history.mutes',
      'warns': 'moderex.history.warns',
      'kicks': 'moderex.history.kicks',
      'joindate': 'moderex.info.joindate',
      'time': 'moderex.info.time',
      'namehistory': 'moderex.info.namehistory'
    };

    const permission = permissionMap[viewType];
    if (!permission) return true; // Unknown type, allow by default

    // Handle array of permissions (any of them grants access)
    if (Array.isArray(permission)) {
      return permission.some(p => hasPermission(p));
    }

    return hasPermission(permission);
  }

  /**
   * Create a locked section overlay for areas user cannot access.
   * @param {HTMLElement} container - The container to lock
   * @param {string} message - Message to display
   */
  function lockSection(container, message) {
    if (!container) return;
    container.classList.add('section-locked');
    const overlay = document.createElement('div');
    overlay.className = 'section-locked-overlay';
    overlay.innerHTML = `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
        <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
      </svg>
      <span>${message || 'No Permission'}</span>
    `;
    container.appendChild(overlay);
  }

  // Expose permission helpers globally
  window.requirePermission = requirePermission;
  window.canView = canView;
  window.lockSection = lockSection;

  /**
   * Check if user can issue a specific punishment type.
   * @param {string} type - Punishment type (BAN, MUTE, WARN, KICK)
   * @returns {boolean} true if user can issue this punishment type
   */
  function canIssuePunishment(type) {
    switch (type?.toUpperCase()) {
      case 'BAN': return hasPermission('moderex.ban') || hasPermission('moderex.tempban');
      case 'MUTE': return hasPermission('moderex.mute') || hasPermission('moderex.tempmute');
      case 'WARN': return hasPermission('moderex.warn');
      case 'KICK': return hasPermission('moderex.kick');
      default: return false;
    }
  }

  /**
   * Check if user can issue PERMANENT punishment of a specific type.
   * @param {string} type - Punishment type (BAN, MUTE)
   * @returns {boolean} true if user can issue permanent punishment
   */
  function canIssuePermanent(type) {
    switch (type?.toUpperCase()) {
      case 'BAN': return hasPermission('moderex.ban');
      case 'MUTE': return hasPermission('moderex.mute');
      default: return true; // WARN and KICK don't have duration
    }
  }

  /**
   * Check if user can revoke a specific punishment type.
   * @param {string} type - Punishment type (BAN, MUTE, WARN)
   * @returns {boolean} true if user can revoke this punishment type
   */
  function canRevokePunishment(type) {
    switch (type?.toUpperCase()) {
      case 'BAN': return hasPermission('moderex.unban');
      case 'MUTE': return hasPermission('moderex.unmute');
      case 'WARN': return hasPermission('moderex.unwarn');
      default: return false; // KICK cannot be revoked
    }
  }

  // ===== ADMIN ANNOUNCEMENT SYSTEM =====
  const DISMISSED_ANNOUNCEMENTS_KEY = 'moderex_dismissed_announcements';

  /**
   * Get list of dismissed announcement IDs from localStorage.
   */
  function getDismissedAnnouncements() {
    try {
      return JSON.parse(localStorage.getItem(DISMISSED_ANNOUNCEMENTS_KEY) || '[]');
    } catch {
      return [];
    }
  }

  /**
   * Add announcement ID to dismissed list.
   */
  function markAnnouncementDismissed(id) {
    const dismissed = getDismissedAnnouncements();
    if (!dismissed.includes(id)) {
      dismissed.push(id);
      // Keep only last 50 dismissed IDs to prevent localStorage bloat
      if (dismissed.length > 50) dismissed.shift();
      localStorage.setItem(DISMISSED_ANNOUNCEMENTS_KEY, JSON.stringify(dismissed));
    }
  }

  /**
   * Show an admin announcement banner.
   * @param {object} data - Announcement data from gateway
   */
  function showAdminAnnouncement(data) {
    if (!data || !data.id) return;

    // Check if already dismissed
    const dismissed = getDismissedAnnouncements();
    if (data.dismissible !== false && dismissed.includes(data.id)) {
      console.log('[Announcement] Already dismissed:', data.id);
      return;
    }

    // Check if expired
    if (data.expiresAt && data.expiresAt < Date.now()) {
      console.log('[Announcement] Expired:', data.id);
      return;
    }

    // Store current announcement for dismiss handler
    window.currentAnnouncement = data;

    const banner = document.getElementById('announcementBanner');
    if (!banner) {
      console.warn('[Announcement] Banner element not found');
      return;
    }

    // Set content
    const titleEl = document.getElementById('announcementTitle');
    const messageEl = document.getElementById('announcementMessage');
    const iconEl = document.getElementById('announcementIcon');
    const actionBtn = document.getElementById('announcementAction');
    const dismissBtn = document.getElementById('announcementDismiss');

    if (titleEl) titleEl.textContent = data.title || 'Announcement';
    if (messageEl) messageEl.textContent = data.message || '';

    // Set icon based on type
    const iconMap = {
      info: 'fa-circle-info',
      feature: 'fa-sparkles',
      warning: 'fa-triangle-exclamation',
      maintenance: 'fa-wrench',
      critical: 'fa-circle-exclamation'
    };
    if (iconEl) {
      iconEl.className = 'fa-solid ' + (iconMap[data.announcementType] || 'fa-bullhorn');
    }

    // Set action button
    if (actionBtn) {
      if (data.actionUrl && data.actionText) {
        actionBtn.href = data.actionUrl;
        actionBtn.textContent = data.actionText;
        actionBtn.style.display = '';
      } else {
        actionBtn.style.display = 'none';
      }
    }

    // Set dismiss button visibility
    if (dismissBtn) {
      dismissBtn.style.display = data.dismissible === false ? 'none' : '';
    }

    // Set type class for styling
    banner.className = 'announcementBanner';
    banner.classList.add('type-' + (data.announcementType || 'info'));
    if (data.dismissible === false) {
      banner.classList.add('non-dismissible');
    }

    // Show the banner
    banner.classList.add('show');
    console.log('[Announcement] Showing:', data.title);
  }

  /**
   * Dismiss the current announcement banner.
   */
  window.dismissAnnouncement = function() {
    const banner = document.getElementById('announcementBanner');
    if (banner) {
      banner.classList.remove('show');
    }

    // Mark as dismissed in localStorage
    if (window.currentAnnouncement?.id) {
      markAnnouncementDismissed(window.currentAnnouncement.id);
    }
  };

  // Expose for external use
  window.showAdminAnnouncement = showAdminAnnouncement;

  /**
   * Check if user can view a specific punishment type in history.
   * @param {string} type - Punishment type (BAN, MUTE, WARN, KICK)
   * @returns {boolean} true if user can view this punishment type
   */
  function canViewHistoryType(type) {
    // Check for wildcard first
    if (hasPermission('moderex.history.*')) return true;

    switch (type?.toUpperCase()) {
      case 'BAN': return hasPermission('moderex.history.bans');
      case 'MUTE': return hasPermission('moderex.history.mutes');
      case 'WARN': return hasPermission('moderex.history.warns');
      case 'KICK': return hasPermission('moderex.history.kicks');
      case 'PARDON': return hasPermission('moderex.history.pardons');
      default: return false;
    }
  }

  /**
   * Check if user has permission to issue ANY punishment.
   * @returns {boolean} true if user can issue at least one punishment type
   */
  function canIssueAnyPunishment() {
    return hasPermission('moderex.punish') ||
      hasPermission('moderex.ban') || hasPermission('moderex.tempban') ||
      hasPermission('moderex.mute') || hasPermission('moderex.tempmute') ||
      hasPermission('moderex.warn') || hasPermission('moderex.kick');
  }

  /**
   * Check if user can view ANY punishment history.
   * @returns {boolean} true if user can view at least one history type
   */
  function canViewAnyHistory() {
    return hasPermission('moderex.history.*') ||
      hasPermission('moderex.history.bans') ||
      hasPermission('moderex.history.mutes') ||
      hasPermission('moderex.history.warns') ||
      hasPermission('moderex.history.kicks');
  }

  /**
   * Show no permission overlay on a modal.
   * @param {string} overlayId - The ID of the overlay element
   * @param {string} message - Message to display
   */
  function showNoPermissionOverlay(overlayId, message) {
    const overlay = document.getElementById(overlayId);
    if (!overlay) {
      console.debug('[Permission] Overlay not found:', overlayId);
      return;
    }
    const modal = overlay.querySelector('.modal, .punish-modal');
    if (!modal) {
      console.debug('[Permission] Modal not found in overlay:', overlayId);
      return;
    }

    // Remove existing no-permission overlay if any
    const existing = modal.querySelector('.no-permission-overlay');
    if (existing) existing.remove();

    const noPermDiv = document.createElement('div');
    noPermDiv.className = 'no-permission-overlay';
    noPermDiv.innerHTML = `
      <i class="fa-solid fa-ban"></i>
      <span>${message}</span>
      <button onclick="closeOverlay('${overlayId}')">Close</button>
    `;
    modal.style.position = 'relative';
    modal.appendChild(noPermDiv);

    overlay.classList.add('show', 'top');
    console.debug('[Permission] Denied access to', overlayId, '-', message);
  }

  /**
   * Close any overlay by ID.
   * @param {string} overlayId - The ID of the overlay to close
   */
  function closeOverlay(overlayId) {
    const overlay = document.getElementById(overlayId);
    if (!overlay) return;
    overlay.classList.add('fade-out');
    setTimeout(() => {
      overlay.classList.remove('show', 'top', 'fade-out');
      // Also remove any no-permission overlay inside
      const noPermOverlay = overlay.querySelector('.no-permission-overlay');
      if (noPermOverlay) noPermOverlay.remove();
    }, 220);
  }

  /**
   * Render no permission state for punishment history table.
   * @param {string} message - Message to display
   */
  function renderNoPermissionTable(message) {
    const tbody = document.getElementById('punishmentsList');
    if (!tbody) return;
    tbody.innerHTML = `
      <tr>
        <td colspan="8">
          <div class="table-no-permission">
            <i class="fa-solid fa-ban"></i>
            <span>${message}</span>
          </div>
        </td>
      </tr>
    `;
    // Also hide pagination if no permission
    const pagination = document.querySelector('.punishments-pagination');
    if (pagination) pagination.style.display = 'none';
  }

  // Expose new permission helpers globally
  window.canIssuePunishment = canIssuePunishment;
  window.canIssuePermanent = canIssuePermanent;
  window.canRevokePunishment = canRevokePunishment;
  window.canViewHistoryType = canViewHistoryType;
  window.canIssueAnyPunishment = canIssueAnyPunishment;
  window.canViewAnyHistory = canViewAnyHistory;
  window.showNoPermissionOverlay = showNoPermissionOverlay;
  window.closeOverlay = closeOverlay;
  window.renderNoPermissionTable = renderNoPermissionTable;

  // ===== PERMISSION AUTO-REFRESH SYSTEM =====
  let previousPermissions = [];
  let permissionRefreshInterval = null;
  const PERMISSION_REFRESH_MS = 15000; // 15 seconds

  /**
   * Detect permission changes between old and new arrays.
   * @param {string[]} oldPerms - Previous permissions array
   * @param {string[]} newPerms - New permissions array
   * @returns {string[]} Array of changes (e.g., ['+moderex.ban', '-moderex.mute'])
   */
  function detectPermissionChanges(oldPerms, newPerms) {
    const added = newPerms.filter(p => !oldPerms.includes(p)).map(p => `+${p}`);
    const removed = oldPerms.filter(p => !newPerms.includes(p)).map(p => `-${p}`);
    return [...added, ...removed];
  }

  /**
   * Start auto-refresh of permissions every 15 seconds.
   */
  function startPermissionRefresh() {
    if (permissionRefreshInterval) return;

    permissionRefreshInterval = setInterval(() => {
      if (window.MX?.ws?.connected) {
        window.MX.ws.send('GET_USER_SETTINGS', {});
      }
    }, PERMISSION_REFRESH_MS);

    console.debug('[Permissions] Started auto-refresh (15s interval)');
  }

  /**
   * Stop auto-refresh of permissions.
   */
  function stopPermissionRefresh() {
    if (permissionRefreshInterval) {
      clearInterval(permissionRefreshInterval);
      permissionRefreshInterval = null;
      console.debug('[Permissions] Stopped auto-refresh');
    }
  }

  /**
   * Handle updated permissions - detect changes and update UI.
   * @param {string[]} newPermissions - New permissions array from server
   */
  function handlePermissionsUpdate(newPermissions) {
    const changed = detectPermissionChanges(previousPermissions, newPermissions);

    if (changed.length > 0) {
      console.debug('[Permissions] Changed:', changed);
      window.devtoolsLog?.('PERMISSIONS', `Permissions changed: ${changed.join(', ')}`, 'info');

      // Update state first
      state.permissions = newPermissions;
      previousPermissions = [...newPermissions];

      // Update all permission-dependent UI elements
      updatePunishPlayerButtons();
      updatePunishFilterButtons();
      updateAnticheatPermissionOverlay();
      updateOnlineStaffPermission();
      if (window.updateStaffChatPermission) updateStaffChatPermission();
      // Show/hide permissions tab based on moderex.admin.permissions
      const permTab = document.getElementById('sbPermissions');
      if (permTab) permTab.style.display = hasPermission('moderex.admin.permissions') ? '' : 'none';

      // Re-render current page if needed (all permission-gated pages)
      switch (state.currentPage) {
        case 'punishments':
          ui.renderPunishments();
          break;
        case 'dashboard':
          ui.renderDashboard();
          break;
        case 'staffchat':
          updateStaffChatPermission();
          break;
        case 'automod':
          ui.renderRules();
          break;
        case 'watchlist':
          ui.renderWatchlist();
          break;
        case 'players':
          ui.renderPlayers();
          break;
        case 'templates':
          ui.renderTemplates();
          break;
        case 'cmdblacklist':
          if (window.renderCmdBlacklist) renderCmdBlacklist();
          break;
        case 'anticheat':
          ui.renderAnticheat();
          updateAnticheatPermissionOverlay();
          break;
      }
    } else {
      // Just update state silently
      state.permissions = newPermissions;
      previousPermissions = [...newPermissions];
    }
    // Always update permissions sidebar visibility
    const permTab = document.getElementById('sbPermissions');
    if (permTab) permTab.style.display = hasPermission('moderex.admin.permissions') ? '' : 'none';
  }

  /**
   * Update "Punish Player" buttons based on permissions.
   */
  function updatePunishPlayerButtons() {
    const canPunish = canIssueAnyPunishment();

    // Dashboard quick actions button
    const dashboardBtn = document.getElementById('dashboardPunishBtn');
    if (dashboardBtn) {
      if (canPunish) {
        dashboardBtn.disabled = false;
        dashboardBtn.classList.remove('no-permission');
        dashboardBtn.title = '';
      } else {
        dashboardBtn.disabled = true;
        dashboardBtn.classList.add('no-permission');
        dashboardBtn.title = 'No permission to issue punishments';
      }
    }

    // Punishments page button
    const punishmentsBtn = document.getElementById('punishmentsPagePunishBtn');
    if (punishmentsBtn) {
      if (canPunish) {
        punishmentsBtn.disabled = false;
        punishmentsBtn.classList.remove('no-permission');
        punishmentsBtn.title = '';
      } else {
        punishmentsBtn.disabled = true;
        punishmentsBtn.classList.add('no-permission');
        punishmentsBtn.title = 'No permission to issue punishments';
      }
    }
  }

  /**
   * Update punishment filter buttons based on permissions.
   */
  function updatePunishFilterButtons() {
    const filterBan = document.getElementById('filterBan');
    const filterMute = document.getElementById('filterMute');
    const filterWarn = document.getElementById('filterWarn');
    const filterKick = document.getElementById('filterKick');
    const filterGroup = document.querySelector('.filter-group');

    // Show/hide each filter button based on permission
    if (filterBan) filterBan.style.display = canViewHistoryType('BAN') ? '' : 'none';
    if (filterMute) filterMute.style.display = canViewHistoryType('MUTE') ? '' : 'none';
    if (filterWarn) filterWarn.style.display = canViewHistoryType('WARN') ? '' : 'none';
    if (filterKick) filterKick.style.display = canViewHistoryType('KICK') ? '' : 'none';

    // If NO filters visible, hide entire filter group
    if (filterGroup) {
      const hasAnyFilter = canViewHistoryType('BAN') || canViewHistoryType('MUTE') ||
        canViewHistoryType('WARN') || canViewHistoryType('KICK');
      filterGroup.style.display = hasAnyFilter ? '' : 'none';
    }
  }

  /**
   * Update anticheat integration card permission overlay.
   * Shows overlay if user lacks moderex.alerts.anticheat permission.
   */
  function updateAnticheatPermissionOverlay() {
    const card = document.getElementById('anticheatIntegrationCard');
    if (!card) return;

    const hasPermission = window.hasPermission ? window.hasPermission('moderex.alerts.anticheat') : true;
    // Hide entire card if no permission
    card.style.display = hasPermission ? '' : 'none';
  }

  /**
   * Update online staff section visibility.
   * Hides card if user lacks moderex.staff permission.
   */
  function updateOnlineStaffPermission() {
    const card = document.getElementById('onlineStaffCard');
    if (!card) return;

    const canViewStaff = window.hasPermission ? window.hasPermission('moderex.staff') : true;
    // Hide entire card if no permission
    card.style.display = canViewStaff ? '' : 'none';
  }

  /**
   * Render empty table state (no results, as opposed to no permission).
   * @param {string} message - Message to display
   */
  function renderEmptyTable(message) {
    const tbody = document.getElementById('punishmentsList');
    if (!tbody) return;
    tbody.innerHTML = `
      <tr>
        <td colspan="8">
          <div class="table-empty">
            <i class="fa-solid fa-inbox"></i>
            <span>${message}</span>
          </div>
        </td>
      </tr>
    `;
    // Also hide pagination
    const pagination = document.querySelector('.punishments-pagination');
    if (pagination) pagination.style.display = 'none';
  }

  /* ===== CUSTOM TOOLTIP MODULE ===== */
  const tooltip = {
    element: null,
    activeTarget: null,

    init() {
      if (this.element) return;
      this.element = document.createElement('div');
      this.element.className = 'mx-tooltip';
      document.body.appendChild(this.element);
    },

    show(text, e) {
      if (!this.element) this.init();
      this.element.textContent = text;
      this.element.classList.add('visible');
      this.move(e);
    },

    move(e) {
      if (!this.element) return;
      const x = e.clientX + 12;
      const y = e.clientY + 12;
      // Keep tooltip within viewport
      const rect = this.element.getBoundingClientRect();
      const maxX = window.innerWidth - rect.width - 10;
      const maxY = window.innerHeight - rect.height - 10;
      this.element.style.left = Math.min(x, maxX) + 'px';
      this.element.style.top = Math.min(y, maxY) + 'px';
    },

    hide() {
      if (!this.element) return;
      this.element.classList.remove('visible');
      this.activeTarget = null;
    }
  };

  // Initialize tooltip on DOM ready
  document.addEventListener('DOMContentLoaded', () => tooltip.init());

  // Expose tooltip module
  window.MX.tooltip = tooltip;

  /**
   * Apply no-permission tooltip to an element with custom message.
   * Uses custom tooltip that follows mouse cursor.
   * @param {HTMLElement} element - Element to apply tooltip to
   * @param {string} message - Custom message (default: generic no permission message)
   */
  function applyNoPermTooltip(element, message = "You lack sufficient permissions") {
    if (!element) return;
    element.dataset.noPermTooltip = message;
    element.disabled = true;
    element.classList.add('no-permission');

    // Add event listeners for custom tooltip
    element.addEventListener('mouseenter', handleTooltipEnter);
    element.addEventListener('mousemove', handleTooltipMove);
    element.addEventListener('mouseleave', handleTooltipLeave);
  }

  /**
   * Remove no-permission tooltip from an element.
   * @param {HTMLElement} element - Element to remove tooltip from
   */
  function removeNoPermTooltip(element) {
    if (!element) return;
    delete element.dataset.noPermTooltip;
    element.disabled = false;
    element.classList.remove('no-permission');

    // Remove event listeners
    element.removeEventListener('mouseenter', handleTooltipEnter);
    element.removeEventListener('mousemove', handleTooltipMove);
    element.removeEventListener('mouseleave', handleTooltipLeave);
  }

  function handleTooltipEnter(e) {
    const msg = e.currentTarget.dataset.noPermTooltip;
    if (msg) {
      tooltip.activeTarget = e.currentTarget;
      tooltip.show(msg, e);
    }
  }

  function handleTooltipMove(e) {
    if (tooltip.activeTarget === e.currentTarget) {
      tooltip.move(e);
    }
  }

  function handleTooltipLeave(e) {
    if (tooltip.activeTarget === e.currentTarget) {
      tooltip.hide();
    }
  }

  // Expose new functions globally
  window.startPermissionRefresh = startPermissionRefresh;
  window.stopPermissionRefresh = stopPermissionRefresh;
  window.updatePunishPlayerButtons = updatePunishPlayerButtons;
  window.updatePunishFilterButtons = updatePunishFilterButtons;
  window.renderEmptyTable = renderEmptyTable;
  window.applyNoPermTooltip = applyNoPermTooltip;
  window.removeNoPermTooltip = removeNoPermTooltip;

  const repeatMemory = {};

  function normalizeMessage(msg) {
    return String(msg || '')
      .toLowerCase()
      .replace(/[^a-z0-9\s]/g, '')
      .replace(/\s+/g, ' ')
      .trim();
  }

  function isSimilarMessage(a, b) {
    if (!a || !b) return false;
    if (a === b) return true;
    const shorter = a.length <= b.length ? a : b;
    const longer = a.length <= b.length ? b : a;
    if (shorter.length < 3) return false;
    if (longer.startsWith(shorter) && (longer.length - shorter.length) <= 3) return true;
    let prefix = 0;
    for (let i = 0; i < Math.min(shorter.length, longer.length); i++) {
      if (shorter[i] !== longer[i]) break;
      prefix++;
    }
    return prefix >= Math.min(4, shorter.length);
  }

  function checkRepeat(rule, msg, playerId) {
    const key = `${playerId}:${rule.id}`;
    const mem = repeatMemory[key] || { lastMsg: '', count: 0, lastAt: 0 };
    const windowMs = (rule.threshold?.windowMins || 10) * 60000;
    const clean = normalizeMessage(msg);
    const nowTs = now();
    if (mem.lastAt && (nowTs - mem.lastAt) > windowMs) {
      mem.count = 0;
      mem.lastMsg = '';
    }
    const cond = (rule.conditions || []).find(c => c.kind === 'repeat') || {};
    const similarAllowed = !!cond.similar;
    const isRepeat = mem.lastMsg && (mem.lastMsg === clean || (similarAllowed && isSimilarMessage(mem.lastMsg, clean)));
    mem.count = isRepeat ? mem.count + 1 : 1;
    mem.lastMsg = clean;
    mem.lastAt = nowTs;
    repeatMemory[key] = mem;
    const required = Math.max(2, parseInt(rule.threshold?.hits || '2', 10));
    return mem.count >= required;
  }

  function updatePunishTitle(titleEl, type, playerId) {
    if (!titleEl) return;
    const p = state.players.find(x => x.id === playerId);
    titleEl.textContent = `${type} · ${p?.name || 'Select Player'}`;
  }

  // New dynamic title for punishment create modal
  function updatePunishCreateTitle() {
    const titleEl = dom().punishCreateTitle;
    if (!titleEl) return;

    const type = dom().punishCreateType?.value || state.pendingPunishType || '';
    const playerCount = state.massPlayerIds.length;

    // Format type name (WARN -> Warn, BAN -> Ban, etc.)
    const typeLabel = type ? type.charAt(0) + type.slice(1).toLowerCase() : '';

    if (playerCount === 0) {
      // No players selected
      if (type) {
        titleEl.textContent = `New ${typeLabel}`;
      } else {
        titleEl.textContent = 'New Punishment';
      }
    } else if (playerCount === 1) {
      // Single player
      const playerName = state.massPlayerNames[0] || 'Player';
      if (type) {
        titleEl.textContent = `${typeLabel} - ${playerName}`;
      } else {
        titleEl.textContent = `Punish - ${playerName}`;
      }
    } else {
      // Multiple players (mass punishment)
      if (type) {
        titleEl.textContent = `Mass ${typeLabel} - ${playerCount} Players`;
      } else {
        titleEl.textContent = `Mass Punishment - ${playerCount} Players`;
      }
    }
  }

  function renderEvidenceOptions(playerId, selectEl, previewEl) {
    const p = state.players.find(x => x.id === playerId);
    const evs = p ? state.evidence.filter(e => e.playerId === p.id) : [];
    if (selectEl) {
      selectEl.innerHTML = `<option value="">(none)</option>` + evs.map(e => {
        const label = `${fmtShort(e.createdAt)} | ${e.trigger} | ${e.message || 'No message'}`;
        return `<option value="${e.id}">${escapeHtml(label.slice(0, 120))}</option>`;
      }).join('');
    }
    if (previewEl) {
      previewEl.innerHTML = `<span style="color:var(--muted)">No evidence attached.</span>`;
    }
  }

  function updateEvidencePreviewFor(selectEl, previewEl) {
    const evId = selectEl?.value;
    const ev = state.evidence.find(e => e.id === evId);
    if (!previewEl) return;
    previewEl.innerHTML = ev ? `<b>${escapeHtml(ev.trigger)}</b><br><span style="color:var(--text-secondary)">${escapeHtml(ev.message || 'No message')}</span>` : `<span style="color:var(--muted)">No evidence attached.</span>`;
  }

  function executePunishment({ playerId, type, reason, duration, evidenceId }) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    const pun = createPunishment(p.id, type, reason, duration, state.staffName, evidenceId);
    state.punishments.push(pun);
    if (type === 'WARN') p.warnings = (p.warnings || 0) + 1;
    if (type === 'BAN' || type === 'MUTE') p.flags = clamp((p.flags || 0) + 1, 0, 9);
    state.activity.push({ t: now(), actor: state.staffName, action: `${type} (${duration || 'instant'})`, target: p.name });
    logPunishment(p.id, pun);
    maybeWatchAlert(p.id, `${type} executed`, `${p.name} | ${reason}`, type === 'BAN' ? 'ERROR' : 'WARN');
  }

  function evaluateAutomodMessage(playerId, msg) {
    const p = state.players.find(x => x.id === playerId);
    if (!p || !msg) return [];
    const hits = [];
    for (const r of state.rules.filter(x => x.enabled)) {
      for (const c of r.conditions) {
        let triggered = false;
        if (c.kind === 'contains' && c.value) {
          const m = normalizeMessage(msg);
          const parts = String(c.value).split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
          triggered = parts.some(part => c.match === 'exact' ? m === part : m.includes(part));
        }
        if (c.kind === 'caps' && c.value) {
          const upper = (msg.match(/[A-Z]/g) || []).length;
          const total = msg.replace(/\s/g, '').length;
          if (total > 0 && (upper / total) * 100 >= parseInt(c.value, 10)) triggered = true;
        }
        if (c.kind === 'link' && /https?:\/\//i.test(msg)) triggered = true;
        if (c.kind === 'repeat') {
          triggered = checkRepeat(r, msg, playerId);
        }
        if (triggered) { hits.push(r); break; }
      }
    }
    return hits;
  }

  function applyAutomodAction(playerId, rule, message) {
    if (!rule?.action || rule.action.kind === 'none') return;
    const type = rule.action.kind.toUpperCase();
    const reason = rule.action.extra || 'Automod rule triggered';
    const duration = rule.action.duration || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');
    const evidence = state.evidence.find(e => e.playerId === playerId && e.message === message);
    executePunishment({ playerId, type, reason, duration, evidenceId: evidence?.id || null });
    ui.renderPunishments();
    ui.renderPlayers();
  }

  function showConnectOverlay(show) {
    if (!dom().connectOverlay) return;
    dom().connectOverlay.classList.toggle('show', show);
  }

  function setPublishLoading(loading) {
    const btn = dom().publishBtn;
    if (!btn) return;
    btn.disabled = loading;
    btn.innerHTML = loading
      ? `<span class="spinner" style="width:16px;height:16px;border-width:2px"></span> Publishing...`
      : `<i class="fa-solid fa-cloud-arrow-up"></i> Publish`;
  }

  function copyToClipboard(text) {
    if (!text) return;
    if (navigator.clipboard?.writeText) {
      navigator.clipboard.writeText(text).catch(() => {});
      return;
    }
    const temp = document.createElement('input');
    temp.value = text;
    document.body.appendChild(temp);
    temp.select();
    document.execCommand('copy');
    temp.remove();
  }

  // ===== NAVIGATION =====
  window.go = function(page) {
    // Show loading line for page transition
    if (window.showLoadingLine) window.showLoadingLine();

    // Cleanup previous page if needed
    if (window.cleanupServerStatus && state.currentPage === 'status') {
      window.cleanupServerStatus();
    }
    if (window.cleanupReplayViewer && state.currentPage === 'replay') {
      window.cleanupReplayViewer();
    }

    state.currentPage = page;

    $$('.page').forEach(p => p.classList.remove('active'));
    const target = $(`#page-${page}`);
    if (target) target.classList.add('active');
    $$('.sb-item').forEach(item => item.classList.toggle('active', item.dataset.page === page));
    if (page === 'punishments') ui.renderPunishments();
    if (page === 'players') ui.renderPlayers();
    if (page === 'watchlist') ui.renderWatchlist();
    if (page === 'activitylog') {
      // Fetch activity logs from database when opening page
      fetchActivityLogs(1);
    }
    if (page === 'automod') {
      // Request fresh automod rules from server when opening automod page
      const ws = window.MX?.ws;
      if (ws && ws.isConnected()) {
        console.log('[Automod] Requesting fresh rules from server...');
        // Show loading bar (will be hidden when AUTOMOD_RULES_DATA is received)
        if (window.showLoadingLine) window.showLoadingLine();
        ws.send('GET_AUTOMOD_RULES', {});
      }
      ui.renderRules();
    }
    if (page === 'cmdblacklist') {
      const ws = window.MX?.ws;
      if (ws && ws.isConnected()) ws.send('GET_CMD_BLACKLIST_ENTRIES');
      renderCmdBlacklist();
    }
    if (page === 'permissions') {
      const ws = window.MX?.ws;
      if (ws && ws.isConnected()) ws.send('GET_RANKS', {});
    }
    if (page === 'anticheat') ui.renderAnticheat();
    if (page === 'templates') ui.renderTemplates();
    if (page === 'messages') ui.renderMessages();
    if (page === 'settings') ui.renderChatToggles();
    if (page === 'mysettings') {
      // Refresh user settings from server when opening My Settings page
      const ws = window.MX?.ws;
      if (ws && ws.isConnected()) {
        console.log('[MySettings] Refreshing settings from server...');
        ws.send('GET_USER_SETTINGS', {});
      }
      if (window.loadDevChecklist) window.loadDevChecklist();
    }
    if (page === 'status' && window.initServerStatus) window.initServerStatus();
    if (page === 'replay') {
      // Request replays from server
      const ws = window.MX?.ws;
      if (ws && ws.isConnected()) {
        ws.send('GET_REPLAYS', {});
        ws.send('GET_REPLAY_SETTINGS', {});
      }
      renderReplayList();
      updateReplayStats();
      // Check permissions
      const canView = window.hasPermission ? window.hasPermission('moderex.replays.view') : true;
      const canConfigure = window.hasPermission ? window.hasPermission('moderex.replays.configure') : true;
      const overlay = document.getElementById('replayNoPermissionOverlay');
      const content = document.getElementById('replayContent');
      const settingsCard = document.getElementById('replaySettingsCard');
      if (!canView) {
        if (overlay) overlay.style.display = 'flex';
        if (content) content.style.display = 'none';
      } else {
        if (overlay) overlay.style.display = 'none';
        if (content) content.style.display = '';
      }
      if (settingsCard) settingsCard.style.display = canConfigure ? '' : 'none';
    }
    if (page === 'staffchat') {
      // Check permission and update overlay
      updateStaffChatPermission();
      // Load staff chat history if not already loaded
      loadStaffChatHistory(false);
    }
    if (page === 'devtools') {
      if (!state.settings?.developerMode) {
        toast('warn', 'Developer Mode Required', 'Enable Developer Mode to access this page.');
        go('dashboard');
        return;
      }
      loadDevChecklist();
    }

    // Hide loading line after a short delay for smooth transition
    setTimeout(() => {
      if (window.hideLoadingLine) window.hideLoadingLine();
    }, 300);
  };

  // ===== TEXT UTILITIES =====
  function truncateText(text, maxLen = 40) {
    if (!text) return '';
    text = String(text);
    return text.length > maxLen ? text.substring(0, maxLen) + '...' : text;
  }

  // Create truncated reason HTML - no click interaction, just displays truncated text with tooltip
  window.expandableReason = function(text, maxLen = 15) {
    if (!text) return '<span class="reason-text">No reason</span>';
    text = String(text);
    if (text.length <= maxLen) {
      return `<span class="reason-text">${escapeHtml(text)}</span>`;
    }
    const truncated = text.substring(0, maxLen);
    // Show truncated with ellipsis, use title attribute for full text on hover
    return `<span class="reason-text" title="${escapeHtml(text)}">${escapeHtml(truncated)}...</span>`;
  };

  // Keep toggleReason for backwards compatibility but it's no longer used
  window.toggleReason = function(id) {};

  // ===== CHARACTER COUNT =====
  window.updateCharCount = function(textarea, counterId, maxLen) {
    const counter = document.getElementById(counterId);
    if (counter) {
      const len = textarea.value.length;
      counter.textContent = `${len}/${maxLen}`;
      counter.style.color = len >= maxLen ? 'var(--bad)' : 'var(--text-secondary)';
    }
  };

  // ===== TOASTS =====
  const MAX_TOASTS = 5;

  window.toast = function(type, title, message, options = {}) {
    const ttl = options.ttl || 5000;
    const iconMap = { ok: 'fa-check', warn: 'fa-triangle-exclamation', bad: 'fa-xmark', info: 'fa-circle-info' };
    const el = document.createElement('div');
    el.className = `toast ${type}`;

    // Add clickable hint if there's an onClick callback
    const clickHint = options.onClick ? '<span class="toast-click-hint">Click to view</span>' : '';

    el.innerHTML = `
      <div class="toast-icon"><i class="fa-solid ${iconMap[type] || iconMap.info}"></i></div>
      <div class="toast-content"><b>${escapeHtml(title)}</b><small>${escapeHtml(message)}${clickHint}</small></div>
      <button class="toast-close"><i class="fa-solid fa-xmark"></i></button>
    `;
    const dismiss = () => { el.classList.add('exit'); setTimeout(() => el.remove(), 200); };
    el.querySelector('.toast-close').onclick = dismiss;
    el.onclick = (e) => {
      if (e.target.closest('.toast-close')) return;
      if (options.onClick) {
        options.onClick();
      } else if (options.playerId) {
        openDrawer(options.playerId);
      }
      dismiss();
    };

    // Add clickable cursor style if there's a callback
    if (options.onClick || options.playerId) {
      el.style.cursor = 'pointer';
    }

    // Get container - try cached first, then direct query as fallback
    const container = dom()?.toastContainer || document.getElementById('toastContainer');
    if (!container) {
      console.warn('[Toast] Toast container not found, skipping toast:', title);
      return;
    }

    // Enforce max toast limit - remove oldest if at capacity
    const existingToasts = container.querySelectorAll('.toast:not(.exit)');
    if (existingToasts.length >= MAX_TOASTS) {
      const oldest = existingToasts[0];
      if (oldest) {
        oldest.classList.add('exit');
        setTimeout(() => oldest.remove(), 200);
      }
    }

    container.appendChild(el);
    setTimeout(dismiss, ttl);

    // Play sound based on toast type (unless silent option is set)
    if (!options.silent && window.MX?.sounds) {
      switch (type) {
        case 'ok': window.MX.sounds.toastSuccess(); break;
        case 'warn': window.MX.sounds.toastWarning(); break;
        case 'bad': window.MX.sounds.toastError(); break;
        case 'info': window.MX.sounds.toastInfo(); break;
      }
    }
  };

  // Make toast available globally
  window.MX = window.MX || {};
  window.MX.toast = window.toast;

  // ===== ALERT TOASTS (Priority Notifications) =====
  const MAX_ALERT_TOASTS = 5;
  const alertIconMap = {
    ban: 'fa-gavel',
    kick: 'fa-shoe-prints',
    mute: 'fa-volume-xmark',
    warn: 'fa-triangle-exclamation',
    pardon: 'fa-hand-peace',
    anticheat: 'fa-shield-halved',
    automod: 'fa-robot',
    command: 'fa-terminal',
    nickname: 'fa-id-badge',
    watchlist: 'fa-eye',
    staffchat: 'fa-comments',
    lag: 'fa-gauge-high',
    punishments: 'fa-gavel',
    custom: 'fa-bell'
  };

  // Create alert toast container if not exists
  function getAlertContainer() {
    let container = document.getElementById('alertToastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'alertToastContainer';
      container.className = 'top-right'; // Default position
      document.body.appendChild(container);
    }
    return container;
  }

  // Update alert toast position based on settings
  window.updateAlertToastPosition = function(position) {
    const container = getAlertContainer();
    container.className = position || 'top-right';
    if (window.MX?.debug) {
      console.log('[AlertToast] Position updated to:', position);
    }
  };

  // Rate limiting tracker for alert toasts
  const alertRateLimiter = {
    // Map of playerId -> { count, windowStart, timer }
    players: new Map(),

    checkAndTrack(playerId, settings) {
      if (!playerId) return true; // No rate limiting without player ID

      const cooldownSeconds = settings.alertRateLimitSeconds ?? 5;
      const maxAlerts = settings.alertRateLimitMax ?? 3;

      // Rate limiting disabled
      if (cooldownSeconds === 0) return true;

      const now = Date.now();
      const entry = this.players.get(playerId);

      if (!entry) {
        // First alert from this player
        this.players.set(playerId, {
          count: 1,
          windowStart: now,
          timer: setTimeout(() => this.players.delete(playerId), cooldownSeconds * 1000)
        });
        return true;
      }

      // Check if still in cooldown window
      if (now - entry.windowStart < cooldownSeconds * 1000) {
        entry.count++;
        if (entry.count > maxAlerts) {
          console.log(`[AlertToast] Rate limited: ${playerId} (${entry.count}/${maxAlerts} in ${cooldownSeconds}s)`);
          return false; // Rate limited
        }
        return true;
      }

      // Window expired, reset
      clearTimeout(entry.timer);
      this.players.set(playerId, {
        count: 1,
        windowStart: now,
        timer: setTimeout(() => this.players.delete(playerId), cooldownSeconds * 1000)
      });
      return true;
    }
  };

  /**
   * Show an alert toast (priority notification) - AlertBar style
   * @param {string} alertType - Type of alert (ban, kick, mute, warn, pardon, anticheat, automod, etc.)
   * @param {string} title - Alert title (e.g., "Player Banned")
   * @param {string} message - Alert message/details (subtitle)
   * @param {object} options - Additional options { playerId, playerName, silent }
   */
  window.alertToast = function(alertType, title, message, options = {}) {
    console.log('[AlertToast] Called with:', { alertType, title, message, options });

    const settings = window.MX?.state?.staffSettings || {};
    const duration = (settings.webAlertDurationSeconds || 10) * 1000;

    // Rate limiting check (for alerts with playerId)
    const playerId = options?.playerId;
    if (playerId && !alertRateLimiter.checkAndTrack(playerId, settings)) {
      console.log('[AlertToast] Alert suppressed due to rate limit');
      return; // Rate limited, don't show
    }

    // Check if sound should play
    const soundKey = 'webSound' + alertType.charAt(0).toUpperCase() + alertType.slice(1);
    const shouldPlaySound = settings[soundKey] !== false && !options?.silent; // Default true

    console.log('[AlertToast] Creating alert:', alertType, title, message);
    console.log('[AlertToast] Duration:', duration, 'Sound enabled:', shouldPlaySound);

    const container = getAlertContainer();

    // Enforce max alert limit
    const existingAlerts = container.querySelectorAll('.alert-toast:not(.exit)');
    if (existingAlerts.length >= MAX_ALERT_TOASTS) {
      const oldest = existingAlerts[0];
      if (oldest) {
        oldest.classList.add('exit');
        setTimeout(() => oldest.remove(), 350);
      }
    }

    const el = document.createElement('div');
    el.className = `alert-toast ${alertType}`;

    const icon = alertIconMap[alertType] || alertIconMap.custom;
    const playerName = options?.playerName;

    // Build left section with avatar or icon
    let leftContent = '';
    if (playerId) {
      leftContent = `
        <img class="alert-toast-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/32" alt="">
      `;
    } else {
      leftContent = `
        <div class="alert-toast-icon"><i class="fa-solid ${icon}"></i></div>
      `;
    }

    // Build text section
    let textContent = `<div class="alert-toast-title">${escapeHtml(title)}</div>`;
    if (playerName) {
      textContent += `<div class="alert-toast-player">${escapeHtml(playerName)}</div>`;
    }
    if (message) {
      textContent += `<div class="alert-toast-sub">${escapeHtml(message)}</div>`;
    }

    // Store extra data for view action
    const punishmentData = options?.punishmentData;
    const caseId = options?.caseId;

    // Check if this is an alert type that should have action buttons
    const hasActionButtons = ['anticheat', 'automod', 'command', 'nickname', 'watchlist'].includes(alertType) && playerId;

    // Build action buttons HTML
    let actionsHtml = '';
    if (hasActionButtons) {
      actionsHtml = `
        <button class="mini" data-action="punish" title="Punish"><i class="fa-solid fa-gavel"></i></button>
        <button class="mini" data-action="watchlist" title="Add to Watchlist"><i class="fa-solid fa-eye"></i></button>
      `;
    } else if (playerId || punishmentData || caseId) {
      actionsHtml = '<button class="mini" data-action="view" title="View Details"><i class="fa-solid fa-info-circle"></i></button>';
    }

    el.innerHTML = `
      <div class="alert-toast-left">
        ${leftContent}
        <div class="alert-toast-text">
          ${textContent}
        </div>
      </div>
      <div class="alert-toast-actions">
        ${actionsHtml}
        <button class="mini" data-action="dismiss" title="Dismiss"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="alert-toast-progress" style="animation-duration: ${duration}ms"></div>
    `;

    const dismiss = () => {
      el.classList.add('exit');
      setTimeout(() => el.remove(), 350);
    };

    // Punish button handler - opens punish action form
    const punishBtn = el.querySelector('[data-action="punish"]');
    if (punishBtn) {
      punishBtn.onclick = (e) => {
        e.stopPropagation();
        showAlertActionModal('punish', alertType, playerName, playerId, message);
        dismiss();
      };
    }

    // Watchlist button handler - directly adds to watchlist
    const watchlistBtn = el.querySelector('[data-action="watchlist"]');
    if (watchlistBtn) {
      watchlistBtn.onclick = (e) => {
        e.stopPropagation();
        // Build reason from alert info
        const reason = `${alertType} alert: ${playerName} - ${message.substring(0, 100)}`;
        // Add directly to watchlist
        const ws = window.MX?.ws;
        if (ws && ws.addToWatchlist) {
          ws.addToWatchlist(playerId, playerName, reason);
          toast('ok', 'Watchlist', `Added ${playerName} to watchlist`);
        } else {
          toast('error', 'Error', 'Not connected to server');
        }
        dismiss();
      };
    }

    // View button handler - opens detail modal or player drawer
    const viewBtn = el.querySelector('[data-action="view"]');
    if (viewBtn) {
      viewBtn.onclick = (e) => {
        e.stopPropagation();
        // If we have punishment data, show detailed alert modal
        if (punishmentData) {
          showAlertDetailModal(alertType, title, message, {
            playerId,
            playerName,
            punishmentData,
            caseId: punishmentData.caseId || caseId
          });
        } else if (playerId) {
          // Otherwise just open player drawer
          window.openPlayerDrawer?.(playerId);
        }
        dismiss();
      };
    }

    // Dismiss button handler - ONLY way to manually dismiss
    el.querySelector('[data-action="dismiss"]').onclick = (e) => {
      e.stopPropagation();
      dismiss();
    };

    // Clicking the alert itself does nothing (no auto-dismiss on click)

    // Insert at top (newest first)
    container.insertBefore(el, container.firstChild);

    // Trigger slide-in animation (need slight delay for CSS transition)
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        el.classList.add('show');
      });
    });

    // Auto-dismiss
    setTimeout(dismiss, duration);

    // Play appropriate sound based on alert type
    if (shouldPlaySound && window.MX?.sounds) {
      // Use specific sound for alert type if available, fallback to alertBar
      const soundFn = window.MX.sounds[alertType] || window.MX.sounds.alertBar;
      soundFn?.();
    }
  };

  window.MX.alertToast = window.alertToast;

  /**
   * Show detailed alert modal with punishment/player info
   */
  function showAlertDetailModal(alertType, title, message, options = {}) {
    const { playerId, playerName, punishmentData, caseId } = options;

    // Build modal content
    let content = '';

    if (punishmentData) {
      // Format time
      const createdAt = punishmentData.createdAt ? new Date(punishmentData.createdAt).toLocaleString() : 'Unknown';
      const expiresAt = punishmentData.expiresAt === -1 ? 'Never (Permanent)' :
                        punishmentData.expiresAt ? new Date(punishmentData.expiresAt).toLocaleString() : 'Unknown';

      content = `
        <div class="alert-detail-section">
          <div class="alert-detail-header">
            ${playerId ? `<img class="alert-detail-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/64" alt="">` : ''}
            <div>
              <h3 style="margin:0;color:var(--text)">${escapeHtml(playerName || 'Unknown')}</h3>
              <span class="badge ${alertType}">${escapeHtml(punishmentData.type || alertType)}</span>
            </div>
          </div>
        </div>

        <div class="alert-detail-section">
          <div class="alert-detail-row">
            <span class="alert-detail-label">Case ID</span>
            <span class="alert-detail-value" style="font-family:var(--font-mono)">${escapeHtml(punishmentData.caseId || caseId || 'N/A')}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Reason</span>
            <span class="alert-detail-value">${escapeHtml(punishmentData.reason || 'No reason provided')}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Duration</span>
            <span class="alert-detail-value">${escapeHtml(punishmentData.duration || 'Unknown')}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Staff</span>
            <span class="alert-detail-value">${escapeHtml(punishmentData.staffName || 'Console')}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Issued</span>
            <span class="alert-detail-value">${escapeHtml(createdAt)}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Expires</span>
            <span class="alert-detail-value">${escapeHtml(expiresAt)}</span>
          </div>
          <div class="alert-detail-row">
            <span class="alert-detail-label">Status</span>
            <span class="alert-detail-value">
              <span class="badge ${punishmentData.active ? 'red' : 'green'}">${punishmentData.active ? 'Active' : 'Expired'}</span>
            </span>
          </div>
        </div>
      `;
    } else {
      // Generic alert detail
      content = `
        <div class="alert-detail-section">
          <div class="alert-detail-header">
            ${playerId ? `<img class="alert-detail-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/64" alt="">` : ''}
            <div>
              <h3 style="margin:0;color:var(--text)">${escapeHtml(title)}</h3>
              <span class="badge ${alertType}">${escapeHtml(alertType)}</span>
            </div>
          </div>
        </div>
        <div class="alert-detail-section">
          <p style="color:var(--text-muted);margin:0">${escapeHtml(message)}</p>
        </div>
      `;
    }

    // Create modal
    const modalId = 'alertDetailModal';
    let modal = document.getElementById(modalId);

    if (!modal) {
      modal = document.createElement('div');
      modal.id = modalId;
      modal.className = 'modal';
      document.body.appendChild(modal);
    }

    modal.innerHTML = `
      <div class="modal-bg" onclick="closeAlertDetailModal()"></div>
      <div class="modal-content" style="max-width:450px">
        <div class="modal-header">
          <h2><i class="fa-solid fa-circle-info" style="margin-right:8px"></i>Alert Details</h2>
          <button class="btn ghost" onclick="closeAlertDetailModal()"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body" style="padding:16px">
          ${content}
        </div>
        <div class="modal-footer">
          ${playerId ? `<button class="btn secondary" onclick="closeAlertDetailModal(); window.openPlayerDrawer?.('${escapeHtml(playerId)}')"><i class="fa-solid fa-user"></i> View Player</button>` : ''}
          <button class="btn primary" onclick="closeAlertDetailModal()">Close</button>
        </div>
      </div>
    `;

    modal.classList.add('show');
    window.MX?.sounds?.modal?.();
  }

  window.showAlertDetailModal = showAlertDetailModal;

  function closeAlertDetailModal() {
    const modal = document.getElementById('alertDetailModal');
    if (modal) {
      modal.classList.remove('show');
    }
  }

  window.closeAlertDetailModal = closeAlertDetailModal;

  /**
   * Show alert action modal for punish/watchlist actions
   */
  function showAlertActionModal(action, alertType, playerName, playerId, details) {
    // For punish action, skip the modal and open the regular punishment form directly
    if (action === 'punish') {
      window.openPunishForm?.(playerId, 'warn', details);
      return;
    }

    const modalId = 'alertActionModal';
    let modal = document.getElementById(modalId);

    if (!modal) {
      modal = document.createElement('div');
      modal.id = modalId;
      modal.className = 'modal';
      document.body.appendChild(modal);
    }

    let content = '';
    let title = '';
    let footerButtons = '';

    if (action === 'watchlist') {
      title = '<i class="fa-solid fa-eye" style="margin-right:8px;color:var(--warn)"></i>Add to Watchlist';
      content = `
        <div class="alert-detail-section">
          <div class="alert-detail-header">
            ${playerId ? `<img class="alert-detail-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/64" alt="">` : ''}
            <div>
              <h3 style="margin:0;color:var(--text)">${escapeHtml(playerName)}</h3>
              <span class="badge ${alertType}">${escapeHtml(alertType)} Alert</span>
            </div>
          </div>
        </div>
        <div class="alert-detail-section">
          <div class="form-group">
            <label style="color:var(--muted);font-size:13px;margin-bottom:6px;display:block">Reason for watchlist:</label>
            <input type="text" id="watchlistReason" class="input" placeholder="Enter reason..." value="${escapeHtml(alertType)} alert: ${escapeHtml(details).substring(0, 50)}" style="width:100%">
          </div>
        </div>
      `;
      footerButtons = `
        <button class="btn ghost" onclick="closeAlertActionModal(); window.openPlayerDrawer?.('${escapeHtml(playerId)}')"><i class="fa-solid fa-user"></i> View Player</button>
        <button class="btn secondary" onclick="closeAlertActionModal()">Cancel</button>
        <button class="btn primary" onclick="addToWatchlistFromAlert('${escapeHtml(playerId)}', '${escapeHtml(playerName)}')"><i class="fa-solid fa-plus"></i> Add to Watchlist</button>
      `;
    }

    modal.innerHTML = `
      <div class="modal-bg" onclick="closeAlertActionModal()"></div>
      <div class="modal-content" style="max-width:450px">
        <div class="modal-header">
          <h2>${title}</h2>
          <button class="btn ghost" onclick="closeAlertActionModal()"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body" style="padding:16px">
          ${content}
        </div>
        <div class="modal-footer">
          ${footerButtons}
        </div>
      </div>
    `;

    modal.classList.add('show');
    window.MX?.sounds?.modal?.();
  }

  window.showAlertActionModal = showAlertActionModal;

  function closeAlertActionModal() {
    const modal = document.getElementById('alertActionModal');
    if (modal) {
      modal.classList.remove('show');
    }
  }

  window.closeAlertActionModal = closeAlertActionModal;

  function addToWatchlistFromAlert(playerId, playerName) {
    const reason = document.getElementById('watchlistReason')?.value || 'Alert triggered';

    // Send watchlist add request
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('ADD_WATCHLIST', {
        playerUuid: playerId,
        playerName: playerName,
        reason: reason
      });
      window.toast?.('success', 'Watchlist', `Added ${playerName} to watchlist`);
    } else {
      window.toast?.('error', 'Error', 'Not connected to server');
    }

    closeAlertActionModal();
  }

  window.addToWatchlistFromAlert = addToWatchlistFromAlert;

  /**
   * Open the punishment form with a player pre-selected and reason pre-filled.
   * This is used by alert action modals.
   * @param {string} playerId - The player's UUID
   * @param {string} type - Punishment type (warn, mute, kick, ban)
   * @param {string} reason - Pre-fill reason
   */
  function openPunishForm(playerId, type, reason) {
    // Convert type to uppercase for the modal
    const punishType = (type || 'warn').toUpperCase();

    // Open the punishment modal with the player selected
    if (window.openPunishModal) {
      window.openPunishModal(punishType, playerId);

      // Pre-fill the reason after a short delay to ensure modal is rendered
      setTimeout(() => {
        const reasonInput = document.getElementById('punReason');
        if (reasonInput && reason) {
          reasonInput.value = reason;
        }
      }, 50);
    }
  }

  window.openPunishForm = openPunishForm;

  /**
   * Open the player drawer/profile for a specific player.
   * This is an alias for openDrawer.
   * @param {string} playerId - The player's UUID
   */
  function openPlayerDrawer(playerId) {
    if (window.openDrawer) {
      window.openDrawer(playerId);
    }
  }

  window.openPlayerDrawer = openPlayerDrawer;

  // ===== DEV TOOLS DEBUG CONSOLE =====
  // Always logs to the Developer Tools debug console regardless of debug mode
  window.devtoolsLog = function(category, message, type = 'info') {
    const logEl = document.getElementById('devDebugLogs');
    if (!logEl) return;

    const time = new Date().toLocaleTimeString();
    const typeColors = {
      info: 'var(--primary)',
      success: 'var(--good)',
      warn: 'var(--warn)',
      error: 'var(--bad)'
    };
    const color = typeColors[type] || 'var(--muted)';

    const entry = document.createElement('div');
    entry.style.cssText = 'padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.05);font-size:12px;font-family:var(--font-mono)';
    entry.innerHTML = `
      <span style="color:var(--muted)">${time}</span>
      <span style="color:${color};font-weight:600">[${escapeHtml(category)}]</span>
      <span style="color:var(--text)">${escapeHtml(message)}</span>
    `;
    logEl.appendChild(entry);

    // Auto-scroll to bottom
    logEl.scrollTop = logEl.scrollHeight;

    // Keep only last 200 entries
    while (logEl.children.length > 200) {
      logEl.removeChild(logEl.firstChild);
    }
  };
  window.MX.devtoolsLog = window.devtoolsLog;

  // ===== DEBUG MODE =====
  // Debug log function - shows notifications at bottom when debug mode is enabled
  window.debugLog = function(category, message, type = 'info') {
    // Always log to dev tools console
    window.devtoolsLog(category, message, type);

    // Check if debug mode is enabled in user settings for on-screen display
    if (!state.userSettings?.debugMode) return;

    // Add to debug log container
    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type}`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat">[${escapeHtml(category)}]</span>
      <span class="debug-msg">${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // Auto-remove after 5 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 5000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    // Also log to console
    console.log(`[DEBUG][${category}] ${message}`);
  };

  function createDebugContainer() {
    const container = document.createElement('div');
    container.id = 'debug-log-container';
    container.className = 'debug-log-container';
    document.body.appendChild(container);
    return container;
  }

  window.MX.debugLog = window.debugLog;

  // ===== SYSTEM MESSAGES =====
  // System messages are ALWAYS shown (plugin status, updates, etc.) - 10 second display
  window.systemLog = function(message, type = 'info') {
    // Always log to dev tools console
    window.devtoolsLog('SYSTEM', message, type);

    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type} system-entry`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat system-cat">[SYSTEM]</span>
      <span class="debug-msg">${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // System messages stay for 10 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 10000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    console.log(`[SYSTEM] ${message}`);
  };

  window.MX.systemLog = window.systemLog;

  // ===== WATCHLIST ALERTS =====
  // Watchlist alerts - shown when watchlistAlerts setting is enabled
  window.watchlistLog = function(playerName, message, type = 'warn') {
    // Always log to dev tools console
    window.devtoolsLog('WATCHLIST', `${playerName} ${message}`, type);

    // Check if watchlist alerts are enabled in user settings
    if (!state.userSettings?.watchlistAlerts) return;

    const container = document.getElementById('debug-log-container') || createDebugContainer();
    const entry = document.createElement('div');
    entry.className = `debug-entry debug-${type} watchlist-entry`;
    entry.innerHTML = `
      <span class="debug-time">${new Date().toLocaleTimeString()}</span>
      <span class="debug-cat watchlist-cat">[WATCHLIST]</span>
      <span class="debug-msg"><b>${escapeHtml(playerName)}</b> ${escapeHtml(message)}</span>
    `;
    container.appendChild(entry);

    // Watchlist alerts stay for 8 seconds
    setTimeout(() => {
      entry.classList.add('fade-out');
      setTimeout(() => entry.remove(), 300);
    }, 8000);

    // Keep only last 20 entries
    while (container.children.length > 20) {
      container.removeChild(container.firstChild);
    }

    // Play watchlist alert sound
    window.MX?.sounds?.alert();

    console.log(`[WATCHLIST] ${playerName}: ${message}`);
  };

  window.MX.watchlistLog = window.watchlistLog;

  // ===== STAFF CHAT =====
  const staffChatMessages = [];
  let staffChatLoading = false;
  let staffChatHasMore = true;
  let staffChatOldestTimestamp = null;
  let staffChatInitialized = false;

  window.sendStaffChat = function() {
    const input = $('#staffchatInput');
    const message = input?.value?.trim();
    if (!message) return;

    // Check permission
    if (window.hasPermission && !window.hasPermission('moderex.staffchat')) {
      toast('error', 'Permission Denied', 'You do not have permission to send staff chat messages.');
      return;
    }

    const ws = window.MX.ws;
    if (ws?.isConnected()) {
      ws.sendStaffChat(message);
    }

    // Add message locally (will also receive from server if connected)
    addStaffChatMessage({
      sender: state.currentUser?.name || 'You',
      message: message,
      isWeb: true,
      isSelf: true,
      time: now()
    }, false); // false = don't scroll to bottom initially, let renderStaffChat handle it

    input.value = '';
    input.focus();
  };

  /**
   * Load staff chat history from database
   */
  function loadStaffChatHistory(loadMore = false) {
    if (staffChatLoading || (!loadMore && staffChatInitialized)) return;
    if (loadMore && !staffChatHasMore) return;

    const ws = window.MX.ws;
    if (!ws?.isConnected()) return;

    staffChatLoading = true;
    showStaffChatLoading(true);

    const beforeTimestamp = loadMore ? staffChatOldestTimestamp : null;
    ws.requestStaffChatHistory(50, beforeTimestamp);
  }

  /**
   * Show/hide staff chat loading indicator
   */
  function showStaffChatLoading(show) {
    const loadingEl = $('#staffchatLoading');
    if (loadingEl) {
      loadingEl.style.display = show ? 'flex' : 'none';
    }
  }

  /**
   * Update staff chat permission overlay
   */
  function updateStaffChatPermission() {
    const overlay = $('#staffchatNoPermOverlay');
    const content = $('#staffchatContent');
    const input = $('#staffchatInput');
    const sendBtn = $('#staffchatSend');

    if (!overlay || !content) return;

    const hasPermission = window.hasPermission ? window.hasPermission('moderex.staffchat') : true;

    if (hasPermission) {
      overlay.style.display = 'none';
      content.style.display = 'block';
      if (input) input.disabled = false;
      if (sendBtn) sendBtn.disabled = false;
    } else {
      overlay.style.display = 'flex';
      content.style.display = 'none';
      if (input) input.disabled = true;
      if (sendBtn) sendBtn.disabled = true;
    }
  }

  // Export permission update function
  window.updateStaffChatPermission = updateStaffChatPermission;

  function addStaffChatMessage(data, scrollToBottom = true) {
    // Check for duplicates (same sender + message + time within 2 seconds)
    const isDuplicate = staffChatMessages.some(msg =>
      msg.sender === data.sender &&
      msg.message === data.message &&
      Math.abs((msg.time || 0) - (data.time || 0)) < 2000
    );

    if (isDuplicate) return;

    staffChatMessages.push(data);

    // Sort by timestamp (oldest first)
    staffChatMessages.sort((a, b) => (a.time || 0) - (b.time || 0));

    // Keep max 200 messages
    while (staffChatMessages.length > 200) {
      staffChatMessages.shift();
    }

    renderStaffChat(scrollToBottom);
  }

  /**
   * Prepend historical messages (for lazy loading)
   */
  function prependStaffChatMessages(messages) {
    if (!messages || messages.length === 0) {
      staffChatHasMore = false;
      return;
    }

    // Convert server format to local format
    const converted = messages.map(msg => ({
      sender: msg.senderName || msg.sender,
      message: msg.message,
      isWeb: (msg.source || '').toUpperCase() === 'WEB',
      isSelf: msg.senderName === state.currentUser?.name,
      time: msg.timestamp || msg.time
    }));

    // Update oldest timestamp for pagination
    const oldest = converted.reduce((min, msg) => Math.min(min, msg.time || Infinity), Infinity);
    if (oldest < Infinity) {
      staffChatOldestTimestamp = oldest;
    }

    // Prepend messages (avoiding duplicates)
    for (const msg of converted) {
      const isDuplicate = staffChatMessages.some(existing =>
        existing.sender === msg.sender &&
        existing.message === msg.message &&
        Math.abs((existing.time || 0) - (msg.time || 0)) < 2000
      );
      if (!isDuplicate) {
        staffChatMessages.unshift(msg);
      }
    }

    // Sort by timestamp
    staffChatMessages.sort((a, b) => (a.time || 0) - (b.time || 0));

    // Keep max 200 messages
    while (staffChatMessages.length > 200) {
      staffChatMessages.shift();
    }

    // If we got fewer than requested, no more history
    if (messages.length < 50) {
      staffChatHasMore = false;
    }

    renderStaffChat(false); // Don't scroll to bottom when loading history
  }

  function renderStaffChat(scrollToBottom = true) {
    const container = $('#staffchatMessages');
    if (!container) return;

    // Preserve scroll position if loading more history
    const scrollTop = container.scrollTop;
    const scrollHeight = container.scrollHeight;

    if (staffChatMessages.length === 0) {
      container.innerHTML = `
        <div class="staffchat-loading" id="staffchatLoading" style="display:none">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <span>Loading messages...</span>
        </div>
        <div class="staffchat-empty">
          <i class="fa-solid fa-comments"></i>
          <p>No messages yet. Start the conversation!</p>
        </div>
      `;
      return;
    }

    // Group messages by date for date dividers
    let lastDate = null;
    let html = `
      <div class="staffchat-loading" id="staffchatLoading" style="display:none">
        <i class="fa-solid fa-spinner fa-spin"></i>
        <span>Loading messages...</span>
      </div>
    `;

    // Show "Load more" if there's more history
    if (staffChatHasMore && !staffChatLoading) {
      html += `
        <div class="staffchat-load-more" onclick="loadStaffChatHistory(true)">
          <i class="fa-solid fa-arrow-up"></i>&nbsp; Load older messages
        </div>
      `;
    }

    staffChatMessages.forEach(msg => {
      const msgDate = new Date(msg.time || Date.now()).toDateString();
      if (msgDate !== lastDate) {
        const dateStr = formatDateDivider(msg.time);
        html += `<div class="staffchat-date-divider">${dateStr}</div>`;
        lastDate = msgDate;
      }

      const classes = ['staffchat-message'];
      if (msg.isSelf) classes.push('self');
      if (msg.isWeb) classes.push('web');

      const senderName = (msg.sender || '').replace('[Web] ', '');
      const avatar = `https://mc-heads.net/avatar/${encodeURIComponent(senderName)}/32`;
      const time = typeof msg.time === 'number' ? fmtShort(msg.time) : msg.time;
      const badge = msg.isWeb
        ? '<span class="staffchat-badge">WEB</span>'
        : '<span class="staffchat-badge game">GAME</span>';

      html += `
        <div class="${classes.join(' ')}">
          <img class="staffchat-avatar" src="${avatar}" alt="">
          <div class="staffchat-content">
            <div class="staffchat-header">
              <span class="staffchat-sender">${escapeHtml(msg.sender)}</span>
              ${badge}
              <span class="staffchat-time">${time}</span>
            </div>
            <div class="staffchat-text">${escapeHtml(msg.message)}</div>
          </div>
        </div>
      `;
    });

    container.innerHTML = html;

    // Restore/adjust scroll position
    if (scrollToBottom) {
      container.scrollTop = container.scrollHeight;
    } else {
      // When prepending, maintain visual position
      const newScrollHeight = container.scrollHeight;
      container.scrollTop = scrollTop + (newScrollHeight - scrollHeight);
    }
  }

  /**
   * Format date for date divider
   */
  function formatDateDivider(timestamp) {
    const date = new Date(timestamp);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return date.toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' });
    }
  }

  // Expose loadStaffChatHistory for button click
  window.loadStaffChatHistory = loadStaffChatHistory;

  // Handle staff chat input enter key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && e.target.id === 'staffchatInput') {
      sendStaffChat();
    }
  });

  // Lazy loading: Load more when scrolling to top
  document.addEventListener('scroll', (e) => {
    if (e.target.id === 'staffchatMessages') {
      const container = e.target;
      // If near top (within 50px), load more history
      if (container.scrollTop < 50 && staffChatHasMore && !staffChatLoading) {
        loadStaffChatHistory(true);
      }
    }
  }, true);

  // ===== AUTHENTICATION =====
  window.login = function() {
    const token = dom().authToken.value.trim();
    if (!token) { toast('warn', 'Required', 'Enter authentication token.'); return; }
    state.authenticated = true;
    dom().authOverlay.classList.add('hide');
    showConnectOverlay(true);
    setTimeout(() => {
      showConnectOverlay(false);
      ui.renderAll();
      startSimulation();
      toast('ok', 'Authenticated', 'Welcome to ModereX Control Panel.');
      logEvent('INFO', 'system', 'Authentication', 'Admin authenticated successfully.');
    }, 1200);
  };


  // ===== DRAWER =====
  // Helper to refresh command section in drawer
  function refreshDrawerCommands(p) {
    const cmdEl = dom().drawerRecent;
    if (!cmdEl) return;

    // Check permission first
    const canViewCommands = window.hasPermission ? window.hasPermission('moderex.alerts.commands') : true;
    if (!canViewCommands) {
      cmdEl.innerHTML = `<div class="drawer-row"><div class="meta"><small><i class="fa-solid fa-lock"></i> No permission to view commands</small></div></div>`;
      return;
    }

    const recentCmds = (p.recentCommands || []).slice(-10).reverse();
    cmdEl.innerHTML = recentCmds.length ? `
      ${recentCmds.map(item => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(item.cmd || item)}</b></div></div>`).join('')}
      <div class="drawer-row">
        <div class="meta"><small>${p.recentCommands.length} total commands</small></div>
        <button class="mini" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
      </div>
    ` : `<div class="drawer-row"><div class="meta"><small>No commands.</small></div></div>`;
  }

  window.openDrawer = function(playerId, highlightPunId = null) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    state.selectedPlayerId = playerId;
    state.selectedPunishmentId = highlightPunId;

    // Request player details (command history, automod flags) from server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_PLAYER_DETAILS', { uuid: p.uuid });
    }

    const watching = state.watchlist.has(p.uuid) || state.watchlist.has(playerId);
    const canAdd = hasPermission('moderex.watchlist.add');
    const canRemove = hasPermission('moderex.watchlist.remove');

    // Determine toggle state and interactability
    dom().watchToggleBtn.classList.toggle('on', watching);
    dom().watchToggleBtn.setAttribute('aria-pressed', watching ? 'true' : 'false');

    // Permission-based toggle behavior:
    // - Has both: fully interactable
    // - Has add only: can turn ON, can't turn OFF (disable if currently ON)
    // - Has remove only: can turn OFF, can't turn ON (disable if currently OFF)
    // - Has neither: completely disabled
    let canToggle = false;
    let tooltipMsg = '';

    if (canAdd && canRemove) {
      canToggle = true;
    } else if (canAdd && !canRemove) {
      canToggle = !watching; // Can only add (turn ON)
      if (!canToggle) tooltipMsg = 'You lack permission to remove players from watchlist';
    } else if (!canAdd && canRemove) {
      canToggle = watching; // Can only remove (turn OFF)
      if (!canToggle) tooltipMsg = 'You lack permission to add players to watchlist';
    } else {
      // Neither permission
      tooltipMsg = 'You lack permission to manage watchlist';
    }

    dom().watchToggleBtn.disabled = !canToggle;
    dom().watchToggleBtn.classList.toggle('no-permission', !canToggle);
    if (!canToggle && tooltipMsg) {
      dom().watchToggleBtn.setAttribute('title', tooltipMsg);
    } else {
      dom().watchToggleBtn.removeAttribute('title');
    }

    // Update hint text
    if (!canAdd && !canRemove) {
      dom().watchToggleHint.textContent = 'No watchlist permission';
    } else {
      dom().watchToggleHint.textContent = watching ? 'Watching player' : 'Not watching';
    }

    dom().drawerAvatar.onerror = () => { dom().drawerAvatar.src = `https://minotar.net/helm/${encodeURIComponent(p.name)}/64.png`; };
    dom().drawerAvatar.src = avatarUrl(p);
    dom().drawerName.textContent = p.name;
    // Build meta line based on permissions
    const metaParts = [];
    if (canView('uuid')) metaParts.push(escapeHtml(p.uuid));
    if (canView('ip') && p.ip) metaParts.push(`<span class="ip-blur">${escapeHtml(p.ip)}</span>`);
    metaParts.push(escapeHtml(p.platform));
    dom().drawerMeta.innerHTML = metaParts.join(' | ');

    // Load external punishments from other moderation plugins
    if (window.showExternalPunishments) {
      window.showExternalPunishments(p.uuid);
    }

    // Build quick punishment buttons based on permissions
    const warnBtn = canIssuePunishment('WARN')
      ? `<button class="action-btn warn" onclick="openPunishModal('WARN','${p.id}')"><i class="fa-solid fa-triangle-exclamation"></i> Warn</button>`
      : `<button class="action-btn warn btn-disabled" disabled title="You lack permission to warn players"><i class="fa-solid fa-lock"></i> Warn</button>`;
    const muteBtn = canIssuePunishment('MUTE')
      ? `<button class="action-btn mute" onclick="openPunishModal('MUTE','${p.id}')"><i class="fa-solid fa-volume-xmark"></i> Mute</button>`
      : `<button class="action-btn mute btn-disabled" disabled title="You lack permission to mute players"><i class="fa-solid fa-lock"></i> Mute</button>`;
    const banBtn = canIssuePunishment('BAN')
      ? `<button class="action-btn ban" onclick="openPunishModal('BAN','${p.id}')"><i class="fa-solid fa-ban"></i> Ban</button>`
      : `<button class="action-btn ban btn-disabled" disabled title="You lack permission to ban players"><i class="fa-solid fa-lock"></i> Ban</button>`;

    dom().drawerActionBar.innerHTML = `
      <div class="action-cluster">
        ${warnBtn}
        ${muteBtn}
        ${banBtn}
      </div>
      <div class="action-cluster">
        ${hasPermission('moderex.history.chat')
          ? `<button class="action-btn" onclick="openChatLogs('${p.id}')"><i class="fa-solid fa-comments"></i> Chat Logs</button>`
          : `<button class="action-btn btn-disabled" disabled title="You lack permission to view chat logs"><i class="fa-solid fa-lock"></i> Chat Logs</button>`}
        ${hasPermission('moderex.history.commands')
          ? `<button class="action-btn" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-terminal"></i> Commands</button>`
          : `<button class="action-btn btn-disabled" disabled title="You lack permission to view command history"><i class="fa-solid fa-lock"></i> Commands</button>`}
        ${hasPermission('moderex.history.automod')
          ? `<button class="action-btn compact" onclick="openAutomodLogs('${p.id}')"><i class="fa-solid fa-robot"></i> Automod</button>`
          : `<button class="action-btn compact btn-disabled" disabled title="You lack permission to view automod logs"><i class="fa-solid fa-lock"></i> Automod</button>`}
      </div>
    `;

    // Filter active punishments by type permission
    const activeAll = state.punishments.filter(x => x.playerId === p.id && x.active && !x.revoked);
    const active = activeAll.filter(x => canViewHistoryType(x.type));
    const hasAnyActiveViewPerm = canViewAnyHistory();

    if (!hasAnyActiveViewPerm) {
      dom().drawerActivePun.innerHTML = `<div class="drawer-row"><div class="meta"><small style="color:var(--muted)"><i class="fa-solid fa-lock"></i> No permission to view punishments</small></div></div>`;
    } else {
      dom().drawerActivePun.innerHTML = active.length ? active.map(x => {
        const badgeClass = x.type === 'BAN' ? 'red' : x.type === 'MUTE' ? 'yellow' : x.type === 'KICK' ? 'purple' : 'blue';
        const xExpired = x.expiresAt && x.expiresAt !== -1 && x.expiresAt < Date.now();
        const canRevokeType = x.type !== 'KICK' && !xExpired && canRevokePunishment(x.type);
        const canViewDetails = hasPermission('moderex.command.viewpunishment');
        return `
          <div class="drawer-row" style="cursor:${canViewDetails ? 'pointer' : 'default'}" ${canViewDetails ? `onclick="viewPunishmentDetails('${x.id}')"` : ''}>
            <div class="meta"><b>${escapeHtml(x.type)} | ${escapeHtml(x.duration || 'instant')}</b><small>${escapeHtml(truncateText(x.reason || 'No reason', 40))}<br>Case: <span style="font-family:var(--font-mono)">${escapeHtml(x.id)}</span> | by ${escapeHtml(x.staff)}${xExpired ? ' | <span style="color:var(--warn)">Expired</span>' : ''}</small></div>
            <div class="drawer-actions">
              <span class="badge ${xExpired ? 'orange' : badgeClass}"><i class="fa-solid fa-${xExpired ? 'clock' : 'file-lines'}"></i></span>
              ${canRevokeType ? `<button class="mini bad" onclick="event.stopPropagation(); revokePunishmentConfirm('${x.id}')"><i class="fa-solid fa-xmark"></i></button>` : x.type !== 'KICK' && !xExpired ? `<span class="badge gray" title="No revoke permission"><i class="fa-solid fa-lock"></i></span>` : ''}
            </div>
          </div>
        `;
      }).join('') : `<div class="drawer-row"><div class="meta"><small>No active punishments.</small></div></div>`;
    }

    // Filter past violations by type permission
    const violationsAll = state.punishments.filter(x => x.playerId === p.id && (!x.active || x.revoked)).sort((a, b) => b.createdAt - a.createdAt);
    const violations = violationsAll.filter(x => canViewHistoryType(x.type));
    const canViewDetails = hasPermission('moderex.command.viewpunishment');

    if (!hasAnyActiveViewPerm) {
      dom().drawerViolations.innerHTML = `<div class="drawer-row"><div class="meta"><small style="color:var(--muted)"><i class="fa-solid fa-lock"></i> No permission to view violations</small></div></div>`;
    } else {
      dom().drawerViolations.innerHTML = violations.length ? violations.slice(0, 8).map(v => `
        <div class="drawer-row" style="cursor:${canViewDetails ? 'pointer' : 'default'}" ${canViewDetails ? `onclick="viewPunishmentDetails('${v.id}')"` : ''}>
          <div class="meta"><b>${escapeHtml(v.type)} | <span style="font-family:var(--font-mono)">${escapeHtml(v.id)}</span></b><small>${escapeHtml(fmtLong(v.createdAt))} | ${escapeHtml(truncateText(v.reason || 'No reason', 35))}</small></div>
          <span class="badge ${v.type === 'BAN' ? 'red' : v.type === 'MUTE' ? 'yellow' : 'blue'}"><i class="fa-solid fa-file-lines"></i></span>
        </div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No violations.</small></div></div>`;
    }

    // Pardons section - requires moderex.history.pardons permission
    const pardonsAll = violationsAll.filter(v => v.revoked && v.revokedBy);
    const pardons = pardonsAll.filter(v => canViewHistoryType(v.type)); // Also check type permission

    if (!hasPermission('moderex.history.pardons')) {
      dom().drawerPardons.innerHTML = `<div class="drawer-row"><div class="meta"><small style="color:var(--muted)"><i class="fa-solid fa-lock"></i> No permission to view pardons</small></div></div>`;
    } else {
      dom().drawerPardons.innerHTML = pardons.length ? pardons.map(v => `
        <div class="drawer-row">
          <div class="meta"><b>${escapeHtml(v.type)} | <span style="font-family:var(--font-mono)">${escapeHtml(v.id)}</span></b><small>Pardoned by ${escapeHtml(v.revokedBy)} | ${escapeHtml(fmtLong(v.revokedAt || v.createdAt))}</small></div>
          <span class="badge gray"><i class="fa-solid fa-check"></i> Pardon</span>
        </div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No pardons.</small></div></div>`;
    }

    // IP History section - show current IP and historical IPs (with permission check)
    const ipSection = document.getElementById('drawerIpSection');
    const ipContainer = dom().drawerIps;
    if (canView('ip') && (p.ip || (p.ipHistory && p.ipHistory.length > 0))) {
      if (ipSection) ipSection.style.display = '';
      const ipHistory = (p.ipHistory || []).slice(0, 5);
      const currentIp = p.ip || (ipHistory.length > 0 ? ipHistory[0].ip : 'Unknown');
      ipContainer.innerHTML = `
        <div class="drawer-row"><div class="meta"><b>Current IP</b><small><span class="ip-blur">${escapeHtml(currentIp)}</span></small></div></div>
        ${ipHistory.length > 1 ? ipHistory.slice(1).map(entry => `
          <div class="drawer-row"><div class="meta"><b>Previous</b><small><span class="ip-blur">${escapeHtml(entry.ip)}</span> | ${escapeHtml(fmtShort(entry.t))}</small></div></div>
        `).join('') : ''}
        ${ipHistory.length > 5 ? `<div class="drawer-row"><div class="meta"><small>${ipHistory.length - 5} more IPs...</small></div></div>` : ''}
      `;
    } else if (ipSection) {
      // No permission or no IP data
      if (!canView('ip')) {
        ipSection.style.display = '';
        ipContainer.innerHTML = `<div class="drawer-row"><div class="meta"><small style="color:var(--muted)"><i class="fa-solid fa-lock"></i> No permission to view IP</small></div></div>`;
      } else {
        ipSection.style.display = '';
        ipContainer.innerHTML = `<div class="drawer-row"><div class="meta"><small>No IP data available.</small></div></div>`;
      }
    }

    // Nickname History section - always show (with permission check)
    const nickHistory = (p.nicknameHistory || []).slice(0, 5);
    const nickSection = document.getElementById('drawerNickSection');
    const nickContainer = document.getElementById('drawerNicks');
    if (nickSection) {
      nickSection.style.display = ''; // Always show the section
      if (!canView('nicknames')) {
        // No permission
        nickContainer.innerHTML = `<div class="drawer-row"><div class="meta"><small style="color:var(--muted)"><i class="fa-solid fa-lock"></i> No permission to view nicknames</small></div></div>`;
      } else if (nickHistory.length > 0) {
        // Has nickname history
        nickContainer.innerHTML = nickHistory.map(entry => `
          <div class="drawer-row">
            <div class="meta">
              <b style="color:var(--primary-light)">${escapeHtml(entry.nick || 'Unknown')}</b>
              <small>from ${escapeHtml(entry.oldNick || 'none')} | ${escapeHtml(fmtShort(entry.t))}</small>
            </div>
          </div>
        `).join('') + (nickHistory.length > 5 ? `<div class="drawer-row"><div class="meta"><small>${(p.nicknameHistory || []).length - 5} more nicknames...</small></div></div>` : '');
      } else {
        // No nickname history
        nickContainer.innerHTML = `<div class="drawer-row"><div class="meta"><small>No nickname changes recorded.</small></div></div>`;
      }
    }

    // Recent Commands section - requires moderex.alerts.commands permission
    const canViewCommands = window.hasPermission ? window.hasPermission('moderex.alerts.commands') : true;
    if (canViewCommands) {
      const recentCmds = (p.recentCommands || []).slice(0, 10);
      dom().drawerRecent.innerHTML = recentCmds.length ? `
        ${recentCmds.map(item => `<div class="drawer-row"><div class="meta"><b>${escapeHtml(item.cmd || item.command || item)}</b><small>${item.t ? escapeHtml(fmtShort(item.t)) : ''}</small></div></div>`).join('')}
        <div class="drawer-row">
          <div class="meta"><small>${(p.recentCommands || []).length} total commands</small></div>
          <button class="mini" onclick="openCommandHistory('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
        </div>
      ` : `<div class="drawer-row"><div class="meta"><small>No commands.</small></div></div>`;
    } else {
      dom().drawerRecent.innerHTML = `<div class="drawer-row"><div class="meta"><small><i class="fa-solid fa-lock"></i> No permission to view commands</small></div></div>`;
    }

    // Automod Logs section - requires moderex.history.automod permission
    const canViewAutomod = window.hasPermission ? window.hasPermission('moderex.history.automod') : true;
    if (canViewAutomod) {
      const fetchedAutomod = (p.automodLogs || []).slice(0, 6);
      const liveAutomod = state.logs.filter(l => l.kind === 'automod' && l.playerId === p.id).slice(-6).reverse();
      const automodLogs = fetchedAutomod.length > 0 ? fetchedAutomod : liveAutomod;
      dom().drawerAutomod.innerHTML = automodLogs.length ? `
        ${automodLogs.map(l => {
          // Handle both fetched format and live format
          const title = l.rule ? `Automod | ${l.rule}` : (l.title || 'Automod');
          const detail = l.content || l.detail || '';
          return `<div class="drawer-row"><div class="meta"><b>${escapeHtml(title)}</b><small>${escapeHtml(fmtShort(l.t))} | ${escapeHtml(detail)}</small></div></div>`;
        }).join('')}
        <div class="drawer-row">
          <div class="meta"><small>${automodLogs.length} recent events</small></div>
          <button class="mini" onclick="openAutomodLogs('${p.id}')"><i class="fa-solid fa-up-right-from-square"></i> Expand</button>
        </div>
      ` : `<div class="drawer-row"><div class="meta"><small>No automod logs.</small></div></div>`;
    } else {
      dom().drawerAutomod.innerHTML = `<div class="drawer-row"><div class="meta"><small><i class="fa-solid fa-lock"></i> No permission to view automod logs</small></div></div>`;
    }

    dom().drawerOverlay.classList.add('show');
    dom().playerDrawer.classList.add('show');
  };

  window.closeDrawer = function() {
    dom().drawerOverlay.classList.remove('show');
    dom().playerDrawer.classList.remove('show');
  };

  // ===== PUNISHMENT MODAL =====
  window.openPunishModal = function(type = null, playerId = null) {
    // Permission check - user must have at least one punishment permission
    if (!canIssueAnyPunishment()) {
      showNoPermissionOverlay('punishOverlay', 'You do not have permission to issue punishments.');
      return;
    }

    const pid = playerId || state.selectedPlayerId;
    if (pid) state.selectedPlayerId = pid;
    const p = pid ? state.players.find(x => x.id === pid) : null;

    dom().punishOverlay.classList.add('show');
    state.pendingPunishType = type || state.pendingPunishType || 'WARN';
    state.punishTargetLocked = !!playerId;

    // If requested type is not allowed, default to first allowed type
    if (!canIssuePunishment(state.pendingPunishType)) {
      state.pendingPunishType = getFirstAllowedPunishType() || 'WARN';
    }

    updatePunishTitle(dom().punishTitle, state.pendingPunishType, pid);

    dom().punishTarget.innerHTML = state.players.map(player => `
      <option value="${player.id}" ${player.id === pid ? 'selected' : ''}>${escapeHtml(player.name)} | ${escapeHtml(player.platform)}</option>
    `).join('');
    if (!pid && state.players[0]) {
      dom().punishTarget.value = state.players[0].id;
      state.selectedPlayerId = state.players[0].id;
    }
    dom().punishTarget.disabled = state.punishTargetLocked;

    // Update type dropdown to only show allowed types
    updatePunishTypeDropdown(dom().punishTypeSelect);
    dom().punishTypeSelect.value = state.pendingPunishType;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);

    updateTemplateDropdown(dom().punishTemplate, state.pendingPunishType);
    dom().punishTemplate.value = 'none';
    applyTemplateToPunish('none');

    renderEvidenceOptions(state.selectedPlayerId, dom().punishEvidencePick, dom().punishEvidencePreview);
  };

  window.setPunishType = function(type) {
    state.pendingPunishType = type;
    if (dom().punishTypeSelect) dom().punishTypeSelect.value = type;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
    // Re-filter template dropdown by selected type
    updateTemplateDropdown(dom().punishTemplate, type);
  };

  /**
   * Update template dropdown, filtering by punishment type and sorting favorites first.
   */
  function updateTemplateDropdown(selectEl, filterType) {
    if (!selectEl) return;
    const current = selectEl.value;
    let templates = state.templates.filter(t => t.id !== 'none' && canIssuePunishment(t.type));
    // Filter by type if specified
    if (filterType) {
      templates = templates.filter(t => t.type === filterType);
    }
    // Sort: favorites first, then by name
    templates.sort((a, b) => {
      if (a.favorite && !b.favorite) return -1;
      if (!a.favorite && b.favorite) return 1;
      return (a.name || '').localeCompare(b.name || '');
    });
    const options = ['<option value="none">(none)</option>'].concat(
      templates.map(t => `<option value="${t.id}">${t.favorite ? '\u2605 ' : ''}${escapeHtml(t.name)}</option>`)
    );
    selectEl.innerHTML = options.join('');
    // Restore selection if still valid
    if (templates.some(t => t.id === current)) {
      selectEl.value = current;
    } else {
      selectEl.value = 'none';
    }
  }

  window.openPunishFromList = function() {
    openPunishCreateModal();
  };

  window.openPunishCreateModal = function(type = null, playerId = null, lockPlayer = false) {
    // Permission check - user must have at least one punishment permission
    if (!canIssueAnyPunishment()) {
      showNoPermissionOverlay('punishCreateOverlay', 'You do not have permission to issue punishments.');
      return;
    }

    dom().punishCreateOverlay.classList.add('show');
    state.punishCreateLocked = !!lockPlayer;

    // Reset player selection
    state.massPlayerIds = [];
    state.massPlayerNames = [];

    // Reset evidence selection
    resetEvidenceSelection();

    // If a specific player is provided, add them to selection
    if (playerId) {
      const player = state.players.find(x => x.id === playerId);
      if (player) {
        state.massPlayerIds.push(playerId);
        state.massPlayerNames.push(player.name);
        state.selectedPlayerId = playerId;
      }
    }

    dom().punishCreatePlayer.value = '';
    dom().punishCreatePlayer.disabled = state.punishCreateLocked;

    // Update type dropdown to only show allowed types
    updatePunishTypeDropdown(dom().punishCreateType);

    // Set type - if requested type is not allowed, default to first allowed
    let selectedType = type || state.pendingPunishType || 'WARN';
    if (!canIssuePunishment(selectedType)) {
      selectedType = getFirstAllowedPunishType() || 'WARN';
    }
    dom().punishCreateType.value = selectedType;
    state.pendingPunishType = selectedType;

    const tplOptions = ['<option value="none">(none)</option>'].concat(
      state.templates.filter(t => t.id !== 'none' && canIssuePunishment(t.type)).map(t => `<option value="${t.id}">${escapeHtml(t.name)}</option>`)
    );
    dom().punishCreateTemplate.innerHTML = tplOptions.join('');
    dom().punishCreateTemplate.value = 'none';
    dom().punishCreateDuration.value = '';
    dom().punishCreateReason.value = '';

    // Update reason character count
    const reasonCountEl = document.getElementById('punishCreateReasonCount');
    if (reasonCountEl) reasonCountEl.textContent = '0/100';

    // Close the dropdown initially
    const combo = dom().punishCreateList?.closest('.combo');
    if (combo) combo.classList.remove('open');
    dom().punishCreateList.innerHTML = '';

    // Render player tags and update title
    renderPunishPlayerTags();
    updatePunishCreateTitle();
    updatePunishExecuteButton();
  };

  window.closePunishCreateModal = function() {
    dom().punishCreateOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().punishCreateOverlay.classList.remove('show', 'fade-out');
      state.punishCreateLocked = false;
    }, 220);
  };

  /**
   * Update a punishment type dropdown to only show types the user has permission for.
   * @param {HTMLSelectElement} selectEl - The select element to update
   */
  function updatePunishTypeDropdown(selectEl) {
    if (!selectEl) return;
    const currentValue = selectEl.value;
    selectEl.innerHTML = '';

    const types = [
      { value: 'WARN', label: 'Warn', icon: 'fa-triangle-exclamation' },
      { value: 'MUTE', label: 'Mute', icon: 'fa-volume-xmark' },
      { value: 'BAN', label: 'Ban', icon: 'fa-ban' },
      { value: 'KICK', label: 'Kick', icon: 'fa-person-walking-arrow-right' }
    ];

    let hasOptions = false;
    types.forEach(t => {
      if (canIssuePunishment(t.value)) {
        const opt = document.createElement('option');
        opt.value = t.value;
        opt.textContent = t.label;
        selectEl.appendChild(opt);
        hasOptions = true;
      }
    });

    if (!hasOptions) {
      const opt = document.createElement('option');
      opt.value = '';
      opt.textContent = 'No permissions';
      opt.disabled = true;
      selectEl.appendChild(opt);
      selectEl.disabled = true;
    } else {
      selectEl.disabled = false;
      // Restore previous value if still valid
      if (canIssuePunishment(currentValue)) {
        selectEl.value = currentValue;
      }
    }
  }

  /**
   * Get the first punishment type the user has permission for.
   * @returns {string|null} First allowed type or null
   */
  function getFirstAllowedPunishType() {
    if (canIssuePunishment('WARN')) return 'WARN';
    if (canIssuePunishment('MUTE')) return 'MUTE';
    if (canIssuePunishment('BAN')) return 'BAN';
    if (canIssuePunishment('KICK')) return 'KICK';
    return null;
  }

  /**
   * Validate duration for permanent punishment permissions.
   * @param {string} type - Punishment type
   * @param {string} duration - Duration string
   * @returns {{valid: boolean, error?: string}}
   */
  function validatePunishDuration(type, duration) {
    const d = (duration || '').trim().toLowerCase();
    const isPerm = !d || d === 'perm' || d === '-1' || d === 'permanent';

    if (isPerm) {
      if (type === 'BAN' && !canIssuePermanent('BAN')) {
        return { valid: false, error: 'You only have permission for temporary bans.' };
      }
      if (type === 'MUTE' && !canIssuePermanent('MUTE')) {
        return { valid: false, error: 'You only have permission for temporary mutes.' };
      }
    }
    return { valid: true };
  }

  function renderPunishCreateList() {
    const combo = dom().punishCreateList?.closest('.combo');
    const q = (dom().punishCreatePlayer.value || '').trim().toLowerCase();

    // Only show results after 2+ characters
    if (q.length < 2) {
      if (combo) combo.classList.remove('open');
      dom().punishCreateList.innerHTML = '';
      return;
    }

    if (state.punishCreateLocked) {
      if (combo) combo.classList.remove('open');
      return;
    }

    // Filter out already selected players
    const list = state.players
      .filter(p => p.name.toLowerCase().includes(q) && !state.massPlayerIds.includes(p.id))
      .slice(0, 8);

    if (list.length === 0) {
      dom().punishCreateList.innerHTML = `<div class="punish-player-result empty"><span>No players found</span></div>`;
      if (combo) combo.classList.add('open');
      return;
    }

    dom().punishCreateList.innerHTML = list.map(p => `
      <div class="punish-player-result" data-player-id="${p.id}" onclick="selectPunishCreatePlayer('${p.id}')">
        <img class="player-avatar" src="${avatarUrl(p)}" onerror="this.onerror=null;this.src='https://minotar.net/helm/${encodeURIComponent(p.name)}/32.png'">
        <div class="player-info">
          <span class="player-name">${escapeHtml(p.name)}</span>
          <span class="player-platform">${escapeHtml(p.platform)}</span>
        </div>
        <span class="badge ${p.status === 'online' ? 'green' : 'gray'}">${p.status}</span>
      </div>
    `).join('');

    if (combo) combo.classList.add('open');
  }

  function renderPunishPlayerTags() {
    const container = document.getElementById('punishPlayerTags');
    const countBadge = document.getElementById('punishPlayerCount');
    if (!container) return;

    if (state.massPlayerIds.length === 0) {
      container.innerHTML = '';
      if (countBadge) countBadge.style.display = 'none';
      return;
    }

    container.innerHTML = state.massPlayerNames.map((name, idx) => {
      const playerId = state.massPlayerIds[idx];
      const player = state.players.find(p => p.id === playerId);
      return `
        <span class="punish-player-tag">
          <img class="tag-avatar" src="${avatarUrl(player)}" onerror="this.onerror=null;this.src='https://minotar.net/helm/${encodeURIComponent(name)}/16.png'">
          <span class="tag-name">${escapeHtml(name)}</span>
          ${state.punishCreateLocked ? '' : `<span class="tag-remove" onclick="removePunishPlayer(${idx})"><i class="fa-solid fa-xmark"></i></span>`}
        </span>
      `;
    }).join('');

    if (countBadge) {
      countBadge.textContent = `${state.massPlayerIds.length} selected`;
      countBadge.style.display = state.massPlayerIds.length > 1 ? 'inline-flex' : 'none';
    }
  }

  window.removePunishPlayer = function(idx) {
    state.massPlayerIds.splice(idx, 1);
    state.massPlayerNames.splice(idx, 1);
    renderPunishPlayerTags();
    updatePunishCreateTitle();
    updatePunishExecuteButton();

    // Refresh evidence logs if section is expanded
    if (state.punishEvidence.expanded) {
      if (state.massPlayerIds.length > 0) {
        fetchEvidenceActivityLogs();
      } else {
        // No players left, clear evidence logs
        state.punishEvidence.logs = [];
        renderEvidenceLogList([]);
      }
    }
  };

  window.selectPunishCreatePlayer = function(playerId) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;

    // Check if already selected
    if (state.massPlayerIds.includes(playerId)) {
      toast('warn', 'Already Added', `${p.name} is already selected.`);
      return;
    }

    // Add to selection
    state.massPlayerIds.push(playerId);
    state.massPlayerNames.push(p.name);
    state.selectedPlayerId = playerId;

    // Clear input and close dropdown
    dom().punishCreatePlayer.value = '';
    const combo = dom().punishCreateList?.closest('.combo');
    combo?.classList.remove('open');
    dom().punishCreateList.innerHTML = '';

    // Update UI
    renderPunishPlayerTags();
    updatePunishCreateTitle();
    updatePunishExecuteButton();

    // Refresh evidence logs if section is expanded
    if (state.punishEvidence.expanded) {
      fetchEvidenceActivityLogs();
    }
  };

  window.applyTemplateToPunishCreate = function(templateId) {
    if (!templateId || templateId === 'none') {
      dom().punishCreateReason.value = '';
      dom().punishCreateDuration.value = '';
      return;
    }
    const t = state.templates.find(x => x.id === templateId);
    if (!t) return;
    dom().punishCreateType.value = t.type || dom().punishCreateType.value;
    state.pendingPunishType = dom().punishCreateType.value;
    dom().punishCreateReason.value = t.reason;
    dom().punishCreateDuration.value = t.duration || '';
    updatePunishCreateTitle();
  };

  window.submitPunishCreate = function() {
    const type = dom().punishCreateType.value || 'WARN';
    const reason = dom().punishCreateReason.value.trim() || 'No reason';
    const duration = dom().punishCreateDuration.value.trim() || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');
    const playerCount = state.massPlayerIds.length;

    // Check if any players selected
    if (playerCount === 0) {
      window.MX.sounds?.toastWarning();
      toast('warn', 'No Target', 'Select at least one player first.');
      return;
    }

    // Permission check for punishment type
    if (!canIssuePunishment(type)) {
      console.debug('[Permission] Denied CREATE punishment type:', type);
      window.MX.sounds?.toastWarning();
      toast('bad', 'No Permission', `You do not have permission to issue ${type.toLowerCase()} punishments.`);
      return;
    }

    // Validate duration for permanent punishment permissions
    const durationCheck = validatePunishDuration(type, duration);
    if (!durationCheck.valid) {
      console.debug('[Permission] Denied permanent duration for:', type);
      window.MX.sounds?.toastWarning();
      toast('bad', 'No Permission', durationCheck.error);
      return;
    }

    // Handle mass punishment (2+ players)
    if (playerCount > 1) {
      // Check mass punishment permission
      const massPermMap = {
        'WARN': 'moderex.masswarn',
        'MUTE': 'moderex.massmute',
        'BAN': 'moderex.massban',
        'KICK': 'moderex.masskick'
      };
      const massPerm = massPermMap[type] || 'moderex.mass.*';

      if (!hasPermission(massPerm) && !hasPermission('moderex.mass.*')) {
        console.debug('[Permission] Denied mass punishment type:', type);
        window.MX.sounds?.toastWarning();
        toast('bad', 'No Permission', `You do not have permission to issue mass ${type.toLowerCase()} punishments.`);
        return;
      }

      // Send mass punishment via WebSocket
      const playerNames = state.massPlayerNames.join(',');
      const cmdType = type.toLowerCase();
      const command = `mass${cmdType} ${playerNames} ${duration} ${reason}`;

      sendMessage({ type: 'RUN_COMMAND', command });
      closePunishCreateModal();
      window.MX.sounds?.punishment();
      toast('ok', 'Mass Punishment Sent', `${type} queued for ${playerCount} players.`, {silent: true});

      // Reset state
      state.massPlayerIds = [];
      state.massPlayerNames = [];
      return;
    }

    // Single player mode
    const pid = state.massPlayerIds[0];
    const evId = dom().punishCreateEvidencePick?.value || null;
    executePunishment({ playerId: pid, type, reason, duration, evidenceId: evId });
    ui.renderPunishments();
    ui.renderPlayers();
    closePunishCreateModal();
    window.MX.sounds?.punishment();
    toast('ok', 'Executed', `${type} applied to ${state.massPlayerNames[0] || 'player'}.`, {silent: true});

    // Reset state
    state.massPlayerIds = [];
    state.massPlayerNames = [];
  };

  window.closePunishModal = function(e) {
    if (e) e.stopPropagation?.();
    dom().punishOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().punishOverlay.classList.remove('show', 'fade-out');
    }, 220);
  };


  function updatePunishExecuteButton() {
    const btn = document.getElementById('punishCreateExecuteBtn');
    const statusEl = document.getElementById('punishCreateStatus');
    if (!btn) return;

    let valid = true;
    let message = 'Review before submitting';

    // Check if at least one player is selected
    if (state.massPlayerIds.length === 0) {
      valid = false;
      message = 'Select at least one player';
    }

    btn.disabled = !valid;
    if (statusEl) {
      statusEl.innerHTML = `<i class="fa-solid fa-${valid ? 'circle-info' : 'triangle-exclamation'}"></i> ${message}`;
    }
  }

  // ===== EVIDENCE ATTACHMENT FUNCTIONS =====

  /**
   * Toggle the evidence section expand/collapse
   */
  window.toggleEvidenceSection = function() {
    const section = document.getElementById('punishEvidenceSection');
    const content = document.getElementById('punishEvidenceContent');
    if (!section || !content) return;

    state.punishEvidence.expanded = !state.punishEvidence.expanded;
    section.classList.toggle('expanded', state.punishEvidence.expanded);
    content.style.display = state.punishEvidence.expanded ? 'block' : 'none';

    // Load activity logs for selected players when expanding
    if (state.punishEvidence.expanded && state.massPlayerIds.length > 0) {
      fetchEvidenceActivityLogs();
    }
  };

  /**
   * Reset evidence selection state
   */
  function resetEvidenceSelection() {
    state.punishEvidence = {
      selectedLogs: [],
      uploadedFiles: [],
      expanded: false,
      logs: [],
      loading: false
    };
    const section = document.getElementById('punishEvidenceSection');
    const content = document.getElementById('punishEvidenceContent');
    const countBadge = document.getElementById('punishEvidenceCount');
    const selectedContainer = document.getElementById('punishEvidenceSelected');
    const logList = document.getElementById('punishEvidenceLogList');

    if (section) section.classList.remove('expanded');
    if (content) content.style.display = 'none';
    if (countBadge) countBadge.style.display = 'none';
    if (selectedContainer) {
      selectedContainer.style.display = 'none';
      selectedContainer.innerHTML = '';
    }
    if (logList) {
      logList.innerHTML = `
        <div class="evidence-log-empty">
          <i class="fa-solid fa-scroll"></i>
          <p>Select player(s) to see their activity log</p>
        </div>
      `;
    }
  }

  /**
   * Fetch activity logs for evidence selection
   */
  function fetchEvidenceActivityLogs() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) return;

    if (state.massPlayerIds.length === 0) {
      renderEvidenceLogList([]);
      return;
    }

    const searchInput = document.getElementById('punishEvidenceSearch')?.value || '';
    let beforeDate = '';
    let afterDate = '';

    // Parse date filters from search
    const parts = searchInput.split(/\s+/).filter(p => p);
    for (const part of parts) {
      const lowerPart = part.toLowerCase();
      if (lowerPart.startsWith('before:')) {
        beforeDate = part.substring(7);
      } else if (lowerPart.startsWith('after:')) {
        afterDate = part.substring(6);
      }
    }

    // Validate dates
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    const validBefore = beforeDate && dateRegex.test(beforeDate) ? beforeDate : null;
    const validAfter = afterDate && dateRegex.test(afterDate) ? afterDate : null;

    state.punishEvidence.loading = true;
    const logList = document.getElementById('punishEvidenceLogList');
    if (logList) logList.classList.add('loading');

    // Request logs for all selected players
    ws.send('GET_EVIDENCE_ACTIVITY_LOGS', {
      playerIds: state.massPlayerIds,
      before: validBefore,
      after: validAfter,
      limit: 50
    });
  }

  /**
   * Handle evidence activity logs response
   */
  window.handleEvidenceActivityLogsData = function(data) {
    state.punishEvidence.loading = false;
    state.punishEvidence.logs = data.logs || [];
    renderEvidenceLogList(state.punishEvidence.logs);
  };

  /**
   * Render the evidence log list
   */
  function renderEvidenceLogList(logs) {
    const logList = document.getElementById('punishEvidenceLogList');
    if (!logList) return;

    logList.classList.remove('loading');

    if (!logs || logs.length === 0) {
      logList.innerHTML = `
        <div class="evidence-log-empty">
          <i class="fa-solid fa-inbox"></i>
          <p>No activity logs found for selected player(s)</p>
        </div>
      `;
      return;
    }

    const typeLabels = {
      'CHAT': 'Chat',
      'COMMAND': 'Cmd',
      'AUTOMOD_TRIGGER': 'Automod',
      'ANTICHEAT_ALERT': 'AC',
      'PUNISHMENT_BAN': 'Ban',
      'PUNISHMENT_MUTE': 'Mute',
      'PUNISHMENT_WARN': 'Warn',
      'PUNISHMENT_KICK': 'Kick'
    };

    logList.innerHTML = logs.map(log => {
      const isSelected = state.punishEvidence.selectedLogs.includes(log.id);
      const typeClass = log.type.toLowerCase().includes('chat') ? 'chat' :
                        log.type.toLowerCase().includes('command') ? 'command' :
                        log.type.toLowerCase().includes('automod') ? 'automod' : '';
      const typeLabel = typeLabels[log.type] || log.type;
      const time = formatRelativeTime(log.timestamp);

      return `
        <div class="evidence-log-item ${isSelected ? 'selected' : ''}" data-log-id="${log.id}" onclick="toggleEvidenceLog(${log.id})">
          <div class="evidence-checkbox">
            <i class="fa-solid fa-check"></i>
          </div>
          <div class="evidence-log-content">
            <div class="evidence-log-header">
              <span class="evidence-log-type ${typeClass}">${escapeHtml(typeLabel)}</span>
              <span class="evidence-log-time">${escapeHtml(time)} · ${escapeHtml(log.playerName || 'Unknown')}</span>
            </div>
            <div class="evidence-log-text">${escapeHtml(log.content || '')}</div>
          </div>
        </div>
      `;
    }).join('');
  }

  /**
   * Format timestamp to relative time
   */
  function formatRelativeTime(timestamp) {
    const diff = Date.now() - timestamp;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;
    return new Date(timestamp).toLocaleDateString();
  }

  /**
   * Toggle selection of an activity log for evidence
   */
  window.toggleEvidenceLog = function(logId) {
    const maxLogs = 5;
    const idx = state.punishEvidence.selectedLogs.indexOf(logId);

    if (idx >= 0) {
      // Deselect
      state.punishEvidence.selectedLogs.splice(idx, 1);
    } else {
      // Check max limit
      if (state.punishEvidence.selectedLogs.length >= maxLogs) {
        toast('warn', 'Maximum Reached', `You can select up to ${maxLogs} activity log entries.`);
        return;
      }
      // Select
      state.punishEvidence.selectedLogs.push(logId);
    }

    // Update UI
    updateEvidenceLogSelection(logId);
    renderEvidenceSelectedPills();
    updateEvidenceCount();
  };

  /**
   * Update the visual selection state of a log item
   */
  function updateEvidenceLogSelection(logId) {
    const item = document.querySelector(`.evidence-log-item[data-log-id="${logId}"]`);
    if (!item) return;

    const isSelected = state.punishEvidence.selectedLogs.includes(logId);
    item.classList.toggle('selected', isSelected);
  }

  /**
   * Render selected evidence pills
   */
  function renderEvidenceSelectedPills() {
    const container = document.getElementById('punishEvidenceSelected');
    if (!container) return;

    const selectedLogs = state.punishEvidence.selectedLogs;
    const uploadedFiles = state.punishEvidence.uploadedFiles;
    const totalSelected = selectedLogs.length + uploadedFiles.length;

    if (totalSelected === 0) {
      container.style.display = 'none';
      container.innerHTML = '';
      return;
    }

    container.style.display = 'flex';

    const logPills = selectedLogs.map(logId => {
      const log = state.punishEvidence.logs.find(l => l.id === logId);
      const label = log ? (log.type.split('_').pop().toLowerCase() + ': ' + (log.content || '').substring(0, 20)) : `Log #${logId}`;
      return `
        <span class="evidence-pill" data-log-id="${logId}">
          <i class="fa-solid fa-scroll"></i>
          <span>${escapeHtml(label)}${(log?.content?.length || 0) > 20 ? '...' : ''}</span>
          <span class="pill-remove" onclick="event.stopPropagation(); removeEvidenceLog(${logId})">
            <i class="fa-solid fa-xmark"></i>
          </span>
        </span>
      `;
    });

    const filePills = uploadedFiles.map(file => `
      <span class="evidence-pill file" data-file-id="${file.id}">
        <i class="fa-solid fa-${file.type?.startsWith('VIDEO') ? 'video' : 'image'}"></i>
        <span>${escapeHtml(file.name || 'File')}</span>
        <span class="pill-remove" onclick="event.stopPropagation(); removeEvidenceFile('${file.id}')">
          <i class="fa-solid fa-xmark"></i>
        </span>
      </span>
    `);

    container.innerHTML = [...logPills, ...filePills].join('');
  }

  /**
   * Remove an activity log from evidence selection
   */
  window.removeEvidenceLog = function(logId) {
    const idx = state.punishEvidence.selectedLogs.indexOf(logId);
    if (idx >= 0) {
      state.punishEvidence.selectedLogs.splice(idx, 1);
      updateEvidenceLogSelection(logId);
      renderEvidenceSelectedPills();
      updateEvidenceCount();
    }
  };

  /**
   * Remove an uploaded file from evidence selection
   */
  window.removeEvidenceFile = function(fileId) {
    const idx = state.punishEvidence.uploadedFiles.findIndex(f => f.id === fileId);
    if (idx >= 0) {
      state.punishEvidence.uploadedFiles.splice(idx, 1);
      renderEvidenceSelectedPills();
      updateEvidenceCount();
    }
  };

  /**
   * Update the evidence count badge
   */
  function updateEvidenceCount() {
    const countBadge = document.getElementById('punishEvidenceCount');
    if (!countBadge) return;

    const total = state.punishEvidence.selectedLogs.length + state.punishEvidence.uploadedFiles.length;
    if (total > 0) {
      countBadge.textContent = total;
      countBadge.style.display = 'inline-flex';
    } else {
      countBadge.style.display = 'none';
    }
  }

  /**
   * Open evidence upload modal
   */
  window.openEvidenceUploadModal = function() {
    const overlay = document.getElementById('evidenceUploadOverlay');
    if (!overlay) return;

    // Reset state
    clearEvidenceFile();
    document.getElementById('evidenceUploadStatus').style.display = 'none';
    document.getElementById('evidenceUploadProgress').style.display = 'none';

    overlay.classList.add('show');

    // Setup drag and drop
    setupEvidenceDropzone();
  };

  window.closeEvidenceUploadModal = function() {
    const overlay = document.getElementById('evidenceUploadOverlay');
    if (!overlay) return;

    overlay.classList.add('fade-out');
    setTimeout(() => {
      overlay.classList.remove('show', 'fade-out');
    }, 220);
  };

  /**
   * Setup dropzone event listeners
   */
  function setupEvidenceDropzone() {
    const dropzone = document.getElementById('evidenceDropzone');
    const fileInput = document.getElementById('evidenceFileInput');
    if (!dropzone || !fileInput) return;

    // Remove old listeners by replacing the element
    const newDropzone = dropzone.cloneNode(true);
    dropzone.parentNode.replaceChild(newDropzone, dropzone);

    const newFileInput = newDropzone.querySelector('input[type="file"]') || document.getElementById('evidenceFileInput');

    // Click to browse
    newDropzone.addEventListener('click', () => {
      newFileInput.click();
    });

    // File selected via input
    newFileInput.addEventListener('change', (e) => {
      if (e.target.files.length > 0) {
        handleEvidenceFileSelect(e.target.files[0]);
      }
    });

    // Drag events
    newDropzone.addEventListener('dragover', (e) => {
      e.preventDefault();
      newDropzone.classList.add('dragover');
    });

    newDropzone.addEventListener('dragleave', (e) => {
      e.preventDefault();
      newDropzone.classList.remove('dragover');
    });

    newDropzone.addEventListener('drop', (e) => {
      e.preventDefault();
      newDropzone.classList.remove('dragover');
      if (e.dataTransfer.files.length > 0) {
        handleEvidenceFileSelect(e.dataTransfer.files[0]);
      }
    });
  }

  /**
   * Handle file selection
   */
  let selectedEvidenceFile = null;

  function handleEvidenceFileSelect(file) {
    // Validate file type
    const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'video/mp4', 'video/x-matroska', 'video/quicktime'];
    const allowedExtensions = ['png', 'jpg', 'jpeg', 'mp4', 'mkv', 'mov'];

    const ext = file.name.split('.').pop().toLowerCase();
    if (!allowedTypes.includes(file.type) && !allowedExtensions.includes(ext)) {
      toast('warn', 'Invalid File Type', 'Only PNG, JPG, MP4, MKV, and MOV files are allowed.');
      return;
    }

    // Validate file size (250 MB default, could be configurable)
    const maxSize = 250 * 1024 * 1024;
    if (file.size > maxSize) {
      toast('warn', 'File Too Large', `Maximum file size is 250 MB. Your file is ${formatFileSize(file.size)}.`);
      return;
    }

    selectedEvidenceFile = file;

    // Show preview
    const dropzone = document.getElementById('evidenceDropzone');
    const preview = document.getElementById('evidenceFilePreview');
    const icon = document.getElementById('evidenceFileIcon');
    const fileName = document.getElementById('evidenceFileName');
    const fileSize = document.getElementById('evidenceFileSize');
    const uploadBtn = document.getElementById('evidenceUploadBtn');

    if (dropzone) dropzone.style.display = 'none';
    if (preview) preview.style.display = 'block';

    // Set icon based on type
    const isVideo = file.type.startsWith('video') || ['mp4', 'mkv', 'mov'].includes(ext);
    if (icon) {
      icon.className = isVideo ? 'fa-solid fa-video video' : 'fa-solid fa-image';
    }

    if (fileName) fileName.textContent = file.name;
    if (fileSize) fileSize.textContent = formatFileSize(file.size);
    if (uploadBtn) uploadBtn.disabled = false;

    // Hide any previous status
    document.getElementById('evidenceUploadStatus').style.display = 'none';
    document.getElementById('evidenceUploadProgress').style.display = 'none';
  }

  /**
   * Clear selected file
   */
  window.clearEvidenceFile = function() {
    selectedEvidenceFile = null;

    const dropzone = document.getElementById('evidenceDropzone');
    const preview = document.getElementById('evidenceFilePreview');
    const uploadBtn = document.getElementById('evidenceUploadBtn');
    const fileInput = document.getElementById('evidenceFileInput');

    if (dropzone) dropzone.style.display = 'flex';
    if (preview) preview.style.display = 'none';
    if (uploadBtn) uploadBtn.disabled = true;
    if (fileInput) fileInput.value = '';
  };

  /**
   * Format file size
   */
  function formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }

  /**
   * Upload the selected evidence file
   */
  window.uploadEvidenceFile = async function() {
    if (!selectedEvidenceFile) {
      toast('warn', 'No File', 'Please select a file first.');
      return;
    }

    const progressContainer = document.getElementById('evidenceUploadProgress');
    const progressFill = document.getElementById('evidenceProgressFill');
    const progressText = document.getElementById('evidenceProgressText');
    const uploadBtn = document.getElementById('evidenceUploadBtn');
    const statusEl = document.getElementById('evidenceUploadStatus');

    // Show progress
    if (progressContainer) progressContainer.style.display = 'block';
    if (progressFill) progressFill.style.width = '0%';
    if (progressText) progressText.textContent = '0%';
    if (uploadBtn) uploadBtn.disabled = true;
    if (statusEl) statusEl.style.display = 'none';

    try {
      // In gateway mode, use WebSocket with Base64 encoding
      if (window.MX?.ws?.isGatewayMode && window.MX.ws.isGatewayMode()) {
        await uploadEvidenceViaWebSocket(selectedEvidenceFile, progressFill, progressText);
        return;
      }

      // Get session ID for upload authentication
      // Priority: MX.auth session > localStorage mx_session
      const token = (window.MX?.auth?.getSession()?.sessionId) || localStorage.getItem('mx_session');
      if (!token) {
        showUploadError('Authentication required. Please log in again.');
        return;
      }

      // Create FormData
      const formData = new FormData();
      formData.append('file', selectedEvidenceFile);

      // Create XMLHttpRequest for progress tracking
      const xhr = new XMLHttpRequest();

      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
          const percent = Math.round((e.loaded / e.total) * 100);
          if (progressFill) progressFill.style.width = percent + '%';
          if (progressText) progressText.textContent = percent + '%';
        }
      });

      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          try {
            const response = JSON.parse(xhr.responseText);
            if (response.success && response.evidence) {
              showUploadSuccess('File uploaded successfully!');

              // Add to evidence selection
              state.punishEvidence.uploadedFiles.push({
                id: response.evidence.id,
                name: selectedEvidenceFile.name,
                type: response.evidence.fileType
              });
              renderEvidenceSelectedPills();
              updateEvidenceCount();

              // Close modal after brief delay
              setTimeout(() => {
                closeEvidenceUploadModal();
              }, 1000);
            } else {
              showUploadError(response.error || 'Upload failed.');
            }
          } catch (e) {
            showUploadError('Invalid response from server.');
          }
        } else {
          showUploadError('Upload failed. Status: ' + xhr.status);
        }
      });

      xhr.addEventListener('error', () => {
        showUploadError('Network error. Please check your connection.');
      });

      xhr.addEventListener('abort', () => {
        showUploadError('Upload was cancelled.');
      });

      // Get the panel URL
      const wsUrl = window.MX?.ws?.url || '';
      const httpUrl = wsUrl.replace('ws://', 'http://').replace('wss://', 'https://').replace(/\/ws\/?$/, '');

      xhr.open('POST', httpUrl + '/api/evidence/upload');
      xhr.setRequestHeader('Authorization', 'Bearer ' + token);
      xhr.send(formData);

    } catch (error) {
      showUploadError('Upload error: ' + error.message);
    }
  };

  /**
   * Upload evidence via WebSocket (for gateway mode)
   * Uses Base64 encoding to transfer file over WebSocket
   */
  async function uploadEvidenceViaWebSocket(file, progressFill, progressText) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onprogress = (e) => {
        if (e.lengthComputable) {
          const percent = Math.round((e.loaded / e.total) * 50); // Reading is 50% of progress
          if (progressFill) progressFill.style.width = percent + '%';
          if (progressText) progressText.textContent = percent + '%';
        }
      };

      reader.onload = () => {
        // Update progress to 50% (file read complete)
        if (progressFill) progressFill.style.width = '50%';
        if (progressText) progressText.textContent = '50%';

        // Extract Base64 data (remove data URL prefix)
        const base64Data = reader.result.split(',')[1];

        // Set up one-time handler for response
        const handleResponse = (data) => {
          window.removeEventListener('mx:evidence_uploaded', handleResponse);

          if (progressFill) progressFill.style.width = '100%';
          if (progressText) progressText.textContent = '100%';

          showUploadSuccess('File uploaded successfully!');

          // Add to evidence selection
          state.punishEvidence.uploadedFiles.push({
            id: data.detail.evidenceId,
            name: file.name,
            type: data.detail.fileType
          });
          renderEvidenceSelectedPills();
          updateEvidenceCount();

          // Close modal after brief delay
          setTimeout(() => {
            closeEvidenceUploadModal();
          }, 1000);

          resolve(data.detail);
        };

        window.addEventListener('mx:evidence_uploaded', handleResponse);

        // Set timeout for response
        setTimeout(() => {
          window.removeEventListener('mx:evidence_uploaded', handleResponse);
          showUploadError('Upload timed out. Please try again.');
          reject(new Error('Upload timeout'));
        }, 60000); // 60 second timeout

        // Send via WebSocket
        window.MX.ws.send('UPLOAD_EVIDENCE_WS', { fileName: file.name, data: base64Data });

        // Update progress to 75% (data sent)
        if (progressFill) progressFill.style.width = '75%';
        if (progressText) progressText.textContent = '75%';
      };

      reader.onerror = () => {
        showUploadError('Failed to read file.');
        reject(new Error('File read error'));
      };

      reader.readAsDataURL(file);
    });
  }

  function showUploadSuccess(message) {
    const statusEl = document.getElementById('evidenceUploadStatus');
    if (statusEl) {
      statusEl.className = 'evidence-upload-status success';
      statusEl.innerHTML = `<i class="fa-solid fa-check-circle"></i> ${escapeHtml(message)}`;
      statusEl.style.display = 'flex';
    }
    document.getElementById('evidenceUploadBtn').disabled = true;
  }

  function showUploadError(message) {
    const statusEl = document.getElementById('evidenceUploadStatus');
    if (statusEl) {
      statusEl.className = 'evidence-upload-status error';
      statusEl.innerHTML = `<i class="fa-solid fa-exclamation-circle"></i> ${escapeHtml(message)}`;
      statusEl.style.display = 'flex';
    }
    document.getElementById('evidenceUploadBtn').disabled = false;
  };

  /**
   * Fetch evidence file via WebSocket (for gateway mode)
   * Returns a blob URL that can be used as img/video src
   */
  async function fetchEvidenceViaWebSocket(fileId) {
    return new Promise((resolve, reject) => {
      // Set up one-time handler for response
      const handleResponse = (event) => {
        const data = event.detail;
        if (data.fileId === fileId) {
          window.removeEventListener('mx:evidence_file', handleResponse);

          try {
            // Decode Base64 and create blob URL
            const byteCharacters = atob(data.data);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: data.mimeType });
            const blobUrl = URL.createObjectURL(blob);
            resolve(blobUrl);
          } catch (e) {
            reject(new Error('Failed to decode evidence data'));
          }
        }
      };

      window.addEventListener('mx:evidence_file', handleResponse);

      // Set timeout for response
      setTimeout(() => {
        window.removeEventListener('mx:evidence_file', handleResponse);
        reject(new Error('Evidence fetch timeout'));
      }, 30000); // 30 second timeout

      // Request file via WebSocket
      window.MX.ws.send('GET_EVIDENCE_FILE', { fileId });
    });
  }

  // ===== END EVIDENCE ATTACHMENT FUNCTIONS =====

  // ===== IMAGE LIGHTBOX =====
  window.openImageLightbox = async function(imageUrl) {
    // Create lightbox overlay
    let overlay = document.getElementById('imageLightboxOverlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'imageLightboxOverlay';
      overlay.className = 'overlay image-lightbox-overlay';
      overlay.innerHTML = `
        <div class="image-lightbox" onclick="event.stopPropagation()">
          <button class="lightbox-close" onclick="closeImageLightbox()"><i class="fa-solid fa-xmark"></i></button>
          <img id="lightboxImage" src="" alt="Evidence">
          <div class="lightbox-controls">
            <button class="btn ghost" onclick="downloadLightboxImage()"><i class="fa-solid fa-download"></i> Download</button>
          </div>
        </div>
      `;
      overlay.onclick = () => closeImageLightbox();
      document.body.appendChild(overlay);
    }

    const img = document.getElementById('lightboxImage');
    overlay.classList.add('show');

    // In gateway mode, fetch via WebSocket
    if (window.MX?.ws?.isGatewayMode && window.MX.ws.isGatewayMode() && imageUrl.includes('/api/evidence/')) {
      const fileId = imageUrl.split('/api/evidence/')[1];
      if (img) img.src = ''; // Clear while loading
      try {
        const blobUrl = await fetchEvidenceViaWebSocket(fileId);
        if (img) img.src = blobUrl;
      } catch (e) {
        console.error('[Evidence] Failed to load image:', e);
        toast('error', 'Error', 'Failed to load image');
      }
    } else {
      if (img) img.src = imageUrl;
    }
  };

  window.closeImageLightbox = function() {
    const overlay = document.getElementById('imageLightboxOverlay');
    if (overlay) {
      overlay.classList.add('fade-out');
      setTimeout(() => {
        overlay.classList.remove('show', 'fade-out');
      }, 220);
    }
  };

  window.downloadLightboxImage = function() {
    const img = document.getElementById('lightboxImage');
    if (img && img.src) {
      const a = document.createElement('a');
      a.href = img.src;
      a.download = 'evidence.png';
      a.click();
    }
  };

  // ===== VIDEO PLAYER =====
  window.openVideoPlayer = async function(videoUrl) {
    // Create video player overlay
    let overlay = document.getElementById('videoPlayerOverlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'videoPlayerOverlay';
      overlay.className = 'overlay video-player-overlay';
      overlay.innerHTML = `
        <div class="video-player-container" onclick="event.stopPropagation()">
          <div class="video-player-header">
            <span>Evidence Video</span>
            <button class="mini" onclick="closeVideoPlayer()"><i class="fa-solid fa-xmark"></i></button>
          </div>
          <div class="video-player-wrapper">
            <video id="evidenceVideo" controls>
              <source src="" type="video/mp4">
              Your browser does not support video playback.
            </video>
          </div>
          <div class="video-player-controls">
            <div class="video-speed-controls">
              <span>Speed:</span>
              <button class="mini" onclick="setVideoSpeed(0.25)">0.25x</button>
              <button class="mini" onclick="setVideoSpeed(0.5)">0.5x</button>
              <button class="mini" onclick="setVideoSpeed(0.75)">0.75x</button>
              <button class="mini active" onclick="setVideoSpeed(1)">1x</button>
              <button class="mini" onclick="setVideoSpeed(1.25)">1.25x</button>
              <button class="mini" onclick="setVideoSpeed(1.5)">1.5x</button>
              <button class="mini" onclick="setVideoSpeed(1.75)">1.75x</button>
              <button class="mini" onclick="setVideoSpeed(2)">2x</button>
            </div>
            <button class="btn ghost" onclick="downloadVideo()"><i class="fa-solid fa-download"></i> Download</button>
          </div>
        </div>
      `;
      overlay.onclick = () => closeVideoPlayer();
      document.body.appendChild(overlay);
    }

    const video = document.getElementById('evidenceVideo');
    overlay.classList.add('show');

    // Reset speed button states
    overlay.querySelectorAll('.video-speed-controls .mini').forEach(btn => {
      btn.classList.toggle('active', btn.textContent === '1x');
    });

    // In gateway mode, fetch via WebSocket
    if (window.MX?.ws?.isGatewayMode && window.MX.ws.isGatewayMode() && videoUrl.includes('/api/evidence/')) {
      const fileId = videoUrl.split('/api/evidence/')[1];
      if (video) video.src = ''; // Clear while loading
      try {
        const blobUrl = await fetchEvidenceViaWebSocket(fileId);
        if (video) {
          video.src = blobUrl;
          video.playbackRate = 1;
        }
      } catch (e) {
        console.error('[Evidence] Failed to load video:', e);
        toast('error', 'Error', 'Failed to load video');
      }
    } else {
      if (video) {
        video.src = videoUrl;
        video.playbackRate = 1;
      }
    }
  };

  window.closeVideoPlayer = function() {
    const overlay = document.getElementById('videoPlayerOverlay');
    const video = document.getElementById('evidenceVideo');
    if (video) {
      video.pause();
      video.src = '';
    }
    if (overlay) {
      overlay.classList.add('fade-out');
      setTimeout(() => {
        overlay.classList.remove('show', 'fade-out');
      }, 220);
    }
  };

  window.setVideoSpeed = function(speed) {
    const video = document.getElementById('evidenceVideo');
    if (video) video.playbackRate = speed;

    // Update button states
    const overlay = document.getElementById('videoPlayerOverlay');
    if (overlay) {
      overlay.querySelectorAll('.video-speed-controls .mini').forEach(btn => {
        btn.classList.toggle('active', btn.textContent === speed + 'x');
      });
    }
  };

  window.downloadVideo = function() {
    const video = document.getElementById('evidenceVideo');
    if (video && video.src) {
      const a = document.createElement('a');
      a.href = video.src;
      a.download = 'evidence.mp4';
      a.click();
    }
  };

  // ===== END VIDEO PLAYER =====

  window.applyTemplateToPunish = function(templateId) {
    if (!templateId || templateId === 'none') {
      dom().punishReason.value = '';
      dom().punishDuration.value = '';
      return;
    }
    const t = state.templates.find(x => x.id === templateId);
    if (!t) return;
    state.pendingPunishType = t.type || state.pendingPunishType || 'WARN';
    dom().punishTypeSelect.value = state.pendingPunishType;
    updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
    dom().punishReason.value = t.reason;
    dom().punishDuration.value = t.duration || '';
  };

  window.updateEvidencePreview = function() {
    updateEvidencePreviewFor(dom().punishEvidencePick, dom().punishEvidencePreview);
  };

  function refreshPunishEvidenceFor(playerId) {
    renderEvidenceOptions(playerId, dom().punishEvidencePick, dom().punishEvidencePreview);
  }

  window.submitPunishment = function() {
    const pid = state.selectedPlayerId;
    const p = pid ? state.players.find(x => x.id === pid) : null;
    if (!p) { window.MX.sounds?.toastWarning(); toast('warn', 'No Target', 'Select a player first.'); return; }

    const type = state.pendingPunishType || 'WARN';

    // Permission check for punishment type
    if (!canIssuePunishment(type)) {
      console.debug('[Permission] Denied SUBMIT punishment type:', type);
      window.MX.sounds?.toastWarning();
      toast('bad', 'No Permission', `You do not have permission to issue ${type.toLowerCase()} punishments.`);
      return;
    }

    const reason = dom().punishReason.value.trim() || 'No reason';
    const duration = dom().punishDuration.value.trim() || (type === 'BAN' ? 'perm' : type === 'MUTE' ? '7d' : '');

    // Validate duration for permanent punishment permissions
    const durationCheck = validatePunishDuration(type, duration);
    if (!durationCheck.valid) {
      console.debug('[Permission] Denied permanent duration for:', type);
      window.MX.sounds?.toastWarning();
      toast('bad', 'No Permission', durationCheck.error);
      return;
    }

    const evId = dom().punishEvidencePick.value || null;

    executePunishment({ playerId: p.id, type, reason, duration, evidenceId: evId });

    ui.renderPunishments();
    ui.renderPlayers();
    closePunishModal();
    window.MX.sounds?.punishment();
    toast('ok', 'Executed', `${type} applied to ${p.name}.`, {silent: true});
  };

  // ===== PUNISHMENT DETAILS =====
  window.viewPunishmentDetails = function(caseId) {
    // Permission check - user must have viewpunishment permission
    if (!hasPermission('moderex.command.viewpunishment')) {
      showNoPermissionOverlay('detailsOverlay', 'You do not have permission to view punishment details.');
      console.debug('[Permission] Denied viewPunishmentDetails - missing moderex.command.viewpunishment');
      return;
    }

    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun) return;

    const pl = state.players.find(p => p.id === pun.playerId);
    const ev = pun.evidenceId ? state.evidence.find(e => e.id === pun.evidenceId) : null;

    dom().detailsCaseId.innerHTML = `<i class="fa-solid fa-hashtag"></i> ${escapeHtml(caseId.slice(-8))}`;

    // Get evidence attached to this punishment
    const punishmentEvidence = pun.evidence || [];
    const hasEvidence = punishmentEvidence.length > 0 || ev;

    // Build evidence HTML
    let evidenceHtml = '';
    if (hasEvidence) {
      evidenceHtml = `<div class="card"><h3><i class="fa-solid fa-paperclip" style="color:var(--accent-light)"></i> Evidence</h3>
        <div class="evidence-list" style="margin-top:12px;display:flex;flex-direction:column;gap:12px">`;

      // Legacy evidence
      if (ev) {
        evidenceHtml += `<div class="evidence-item">
          <div class="evidence-item-header"><span class="badge gray"><i class="fa-solid fa-robot"></i> Automod</span></div>
          <div class="evidence-item-content" style="margin-top:8px">
            <div><b>Trigger:</b> ${escapeHtml(ev.trigger)}</div>
            <div><b>Message:</b> ${escapeHtml(ev.message)}</div>
          </div>
        </div>`;
      }

      // New evidence (activity logs and files)
      for (const evidence of punishmentEvidence) {
        if (evidence.type === 'ACTIVITY_LOG') {
          // Parse the snapshot JSON to get activity log details
          let snapshot = {};
          try {
            if (evidence.snapshot) {
              snapshot = typeof evidence.snapshot === 'string' ? JSON.parse(evidence.snapshot) : evidence.snapshot;
            }
          } catch (e) { console.warn('Failed to parse evidence snapshot:', e); }

          const logType = snapshot.type || 'LOG';
          const content = snapshot.content || '';
          const playerName = snapshot.playerName || evidence.addedBy || 'Unknown';
          const timestamp = snapshot.timestamp || evidence.addedAt;

          evidenceHtml += `<div class="evidence-item">
            <div class="evidence-item-header">
              <span class="badge gray"><i class="fa-solid fa-scroll"></i> ${escapeHtml(logType)}</span>
              <span class="evidence-item-time">${escapeHtml(fmtShort(timestamp))}</span>
            </div>
            <div class="evidence-item-content" style="margin-top:8px;padding:10px;background:rgba(0,0,0,0.2);border-radius:var(--radius-sm)">
              <div style="font-size:12px;color:var(--muted)">Player: ${escapeHtml(playerName)}</div>
              <div style="margin-top:4px">${escapeHtml(content)}</div>
            </div>
          </div>`;
        } else if (evidence.type === 'FILE') {
          const fileType = evidence.fileType || '';
          const fileMissing = evidence.fileMissing === true;
          // Use backend boolean flags with string parsing fallback
          const isVideo = evidence.isVideo === true || fileType.startsWith('VIDEO') || ['MP4', 'MKV', 'MOV'].includes(fileType);
          const isImage = evidence.isImage === true || fileType.startsWith('IMAGE') || ['PNG', 'JPG', 'JPEG'].includes(fileType);
          const fileId = evidence.evidenceId || evidence.id;
          const fileSize = evidence.fileSize ? (evidence.fileSize < 1024*1024 ? (evidence.fileSize/1024).toFixed(1) + ' KB' : (evidence.fileSize/(1024*1024)).toFixed(1) + ' MB') : '';

          evidenceHtml += `<div class="evidence-item">
            <div class="evidence-item-header">
              <span class="badge ${fileMissing ? 'gray' : isVideo ? 'purple' : 'blue'}">
                <i class="fa-solid fa-${fileMissing ? 'file-circle-exclamation' : isVideo ? 'video' : 'image'}"></i>
                ${fileMissing ? 'Missing File' : isVideo ? 'Video' : 'Image'}
              </span>
              <span class="evidence-item-time">${escapeHtml(evidence.fileName || 'file')}${fileSize ? ' (' + fileSize + ')' : ''}</span>
            </div>
            <div class="evidence-item-content" style="margin-top:8px">
              ${fileMissing ? `<div style="padding:16px;background:rgba(239,68,68,0.1);border:1px solid rgba(239,68,68,0.2);border-radius:var(--radius-sm);text-align:center">
                <i class="fa-solid fa-file-circle-exclamation" style="font-size:24px;color:var(--bad)"></i>
                <div style="margin-top:8px;font-size:12px;color:var(--muted)">Evidence file not found on disk</div>
              </div>` : ''}
              ${!fileMissing && isImage ? `<img src="/api/evidence/${fileId}" alt="Evidence" class="evidence-image" onclick="openImageLightbox('/api/evidence/${fileId}')" style="max-width:100%;max-height:200px;border-radius:var(--radius-sm);cursor:pointer" onerror="this.style.display='none';this.nextElementSibling.style.display='block'" loading="lazy">
              <div style="display:none;padding:16px;background:rgba(239,68,68,0.1);border-radius:var(--radius-sm);text-align:center">
                <i class="fa-solid fa-image" style="font-size:24px;color:var(--muted)"></i>
                <div style="margin-top:8px;font-size:12px;color:var(--muted)">Failed to load image</div>
              </div>` : ''}
              ${!fileMissing && isVideo ? `<div class="evidence-video-preview" onclick="openVideoPlayer('/api/evidence/${fileId}')" style="cursor:pointer;padding:20px;background:rgba(0,0,0,0.3);border-radius:var(--radius-sm);text-align:center;transition:background var(--transition)" onmouseover="this.style.background='rgba(0,0,0,0.5)'" onmouseout="this.style.background='rgba(0,0,0,0.3)'">
                <i class="fa-solid fa-play-circle" style="font-size:36px;color:var(--primary-light)"></i>
                <div style="margin-top:8px;font-size:12px;color:var(--muted)">Click to play video${fileSize ? ' (' + fileSize + ')' : ''}</div>
              </div>` : ''}
              ${!fileMissing && !isVideo && !isImage ? `<div style="padding:16px;background:rgba(0,0,0,0.2);border-radius:var(--radius-sm);text-align:center">
                <i class="fa-solid fa-file" style="font-size:24px;color:var(--muted)"></i>
                <div style="margin-top:8px;font-size:12px;color:var(--muted)">Unknown file type: ${escapeHtml(fileType)}</div>
              </div>` : ''}
            </div>
          </div>`;
        }
      }

      evidenceHtml += '</div></div>';
    }

    // Build list of players involved (for mass punishments, there could be multiple)
    const involvedPlayers = pun.players || (pl ? [pl] : [{ name: pun.playerName || 'Unknown', uuid: pun.playerId || 'N/A' }]);
    const playersHtml = involvedPlayers.map(p => `
      <div class="pwrap" style="padding:8px;background:rgba(0,0,0,0.2);border-radius:var(--radius-sm);margin-top:8px">
        <div class="phead" style="width:40px;height:40px"><img src="${avatarUrl(p)}" alt="" onerror="this.onerror=null;this.src='https://minotar.net/helm/${encodeURIComponent(p.name || 'Player')}/64.png'"></div>
        <div><b style="font-size:14px">${escapeHtml(p.name || 'Unknown')}</b><div style="font-size:11px;color:var(--text-secondary)">${escapeHtml(p.uuid || p.id || 'N/A')}</div></div>
      </div>
    `).join('');

    dom().detailsBody.innerHTML = `
      <div style="display:flex;flex-direction:column;gap:16px;max-height:60vh;overflow-y:auto;padding-right:8px">
        <!-- Section 1: Players -->
        <div class="card" style="margin:0">
          <h3><i class="fa-solid fa-users" style="color:var(--primary-light)"></i> Players (${involvedPlayers.length})</h3>
          <div style="margin-top:8px">${playersHtml}</div>
        </div>

        <!-- Section 2: Information -->
        <div class="card" style="margin:0">
          <h3><i class="fa-solid fa-gavel" style="color:var(--warn)"></i> Information</h3>
          <div style="margin-top:12px;display:flex;flex-direction:column;gap:6px">
            <div><b>Type:</b> ${escapeHtml(pun.type)}</div>
            <div style="word-break:break-word;white-space:pre-wrap"><b>Reason:</b> ${escapeHtml(pun.reason || 'No reason')}</div>
            <div><b>Duration:</b> ${escapeHtml(pun.duration || 'Instant')}</div>
            <div><b>Staff:</b> ${escapeHtml(pun.staff || 'System')}</div>
            <div><b>Created:</b> ${escapeHtml(fmtShort(pun.createdAt))}</div>
            <div><b>Status:</b> ${(() => {
              const isExpired = pun.expiresAt && pun.expiresAt !== -1 && pun.expiresAt < Date.now();
              if (pun.revoked) return '<span class="badge gray">Revoked</span>';
              if (isExpired) return '<span class="badge orange">Expired</span>';
              if (pun.active) return '<span class="badge red">Active</span>';
              return '<span class="badge gray">Closed</span>';
            })()}</div>
          </div>
        </div>

        <!-- Section 3: Evidence -->
        ${hasEvidence ? evidenceHtml.replace('<div class="card">', '<div class="card" style="margin:0">') : ''}
      </div>
    `;

    // Kicks cannot be revoked - they are instant actions
    // Expired punishments cannot be revoked - they naturally ended
    const isExpired = pun.expiresAt && pun.expiresAt !== -1 && pun.expiresAt < Date.now();
    const canRevoke = !pun.revoked && !isExpired && pun.type !== 'KICK';
    const hasRevokePerm = canRevokePunishment(pun.type);
    dom().detailsActions.innerHTML = `
      <button class="btn ghost" onclick="openCaseInformation('${pun.id}')"><i class="fa-solid fa-info-circle"></i> Case Information</button>
      ${canRevoke && hasRevokePerm ? `<button class="btn bad" onclick="revokePunishmentConfirm('${pun.id}')"><i class="fa-solid fa-xmark"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : ''}
      ${canRevoke && !hasRevokePerm ? `<button class="btn bad btn-disabled" disabled title="You lack permission to ${pun.type === 'WARN' ? 'remove warnings' : 'revoke this punishment'}"><i class="fa-solid fa-lock"></i> ${pun.type === 'WARN' ? 'Remove' : 'Revoke'}</button>` : ''}
      ${pun.type === 'KICK' && !pun.revoked ? `<span class="badge gray"><i class="fa-solid fa-info-circle"></i> Kicks cannot be revoked</span>` : ''}
      ${isExpired && !pun.revoked ? `<span class="badge gray"><i class="fa-solid fa-clock"></i> Expired punishments cannot be revoked</span>` : ''}
      <button class="btn ghost" onclick="closeDetailsModal()"><i class="fa-solid fa-xmark"></i> Close</button>
    `;
    dom().detailsOverlay.classList.add('show', 'top');

    // In gateway mode, inline evidence images use HTTP URLs that don't work
    // Fetch them via WebSocket and replace with blob URLs
    if (window.MX?.ws?.isGatewayMode && window.MX.ws.isGatewayMode()) {
      const evidenceImages = dom().detailsBody.querySelectorAll('img.evidence-image[src*="/api/evidence/"]');
      evidenceImages.forEach(async (img) => {
        const fileId = img.src.split('/api/evidence/')[1];
        if (!fileId) return;
        try {
          img.src = ''; // Clear while loading
          img.alt = 'Loading...';
          const blobUrl = await fetchEvidenceViaWebSocket(fileId);
          img.src = blobUrl;
          img.alt = 'Evidence';
          // Update the onclick to use blob URL for lightbox too
          img.onclick = () => openImageLightbox(blobUrl);
        } catch (e) {
          console.error('[Evidence] Gateway fetch failed for', fileId, e);
          img.style.display = 'none';
          if (img.nextElementSibling) img.nextElementSibling.style.display = 'block';
        }
      });

      // Also fix video preview onclick URLs for gateway mode
      const videoPreviews = dom().detailsBody.querySelectorAll('.evidence-video-preview');
      videoPreviews.forEach((preview) => {
        const originalOnclick = preview.getAttribute('onclick');
        if (originalOnclick && originalOnclick.includes('/api/evidence/')) {
          const match = originalOnclick.match(/openVideoPlayer\('\/api\/evidence\/([^']+)'\)/);
          if (match) {
            const fileId = match[1];
            preview.onclick = () => openVideoPlayer('/api/evidence/' + fileId);
          }
        }
      });
    }
  };

  window.closeDetailsModal = function(e) {
    if (e) e.stopPropagation?.();
    dom().detailsOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().detailsOverlay.classList.remove('show', 'top', 'fade-out');
    }, 220);
  };

  window.revokePunishment = function(caseId) {
    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun || pun.revoked) return;
    pun.revoked = true;
    pun.active = false;
    pun.revokedBy = state.staffName;
    pun.revokedAt = now();

    const p = state.players.find(x => x.id === pun.playerId);
    if (pun.type === 'WARN' && p) p.warnings = Math.max(0, (p.warnings || 1) - 1);
    state.activity.push({ t: now(), actor: state.staffName, action: `Revoked ${pun.type}`, target: p?.name || 'Unknown' });
    const undoType = pun.type === 'BAN' ? 'UNBAN' : pun.type === 'MUTE' ? 'UNMUTE' : pun.type === 'WARN' ? 'UNWARN' : 'EXPIRE';
    logEvent('INFO', 'punishment', `Revoked ${pun.type}`, `Case ${pun.id} revoked`, { playerId: pun.playerId, caseId: pun.id, kind: 'punishment', type: undoType });

    ui.renderPunishments();
    ui.renderDashboard();
    window.MX.sounds?.pardon();
    toast('info', 'Revoked', `Case ${caseId.slice(-8)} revoked.`, {silent: true});
  };

  window.revokePunishmentConfirm = function(caseId) {
    const pun = state.punishments.find(x => x.id === caseId);
    if (!pun || pun.revoked) return;

    // Kicks cannot be revoked - they are instant actions
    if (pun.type === 'KICK') {
      toast('warn', 'Cannot Revoke', 'Kicks are instant actions and cannot be revoked.');
      return;
    }

    // Expired punishments cannot be revoked
    const isExpired = pun.expiresAt && pun.expiresAt !== -1 && pun.expiresAt < Date.now();
    if (isExpired) {
      toast('warn', 'Cannot Revoke', 'Expired punishments cannot be revoked. The punishment has already naturally ended.');
      return;
    }

    // Permission check for revocation
    if (!canRevokePunishment(pun.type)) {
      const permName = pun.type === 'BAN' ? 'moderex.unban' : pun.type === 'MUTE' ? 'moderex.unmute' : 'moderex.unwarn';
      console.debug('[Permission] Denied revoke punishment - missing', permName);
      window.MX.sounds?.toastWarning();
      toast('bad', 'No Permission', `You do not have permission to revoke ${pun.type.toLowerCase()} punishments.`);
      return;
    }

    const verb = pun.type === 'WARN' ? 'Remove' : 'Revoke';
    openConfirmPanel({
      title: `${verb} Punishment`,
      body: `${verb} ${pun.type} case ${caseId.slice(-8)} for ${state.players.find(p => p.id === pun.playerId)?.name || 'player'}?`,
      confirmText: verb,
      onConfirm: () => { revokePunishment(caseId); closeDetailsModal(); }
    });
  };

  // ===== TEMPLATES =====
  window.useTemplate = function(tplId) {
    const t = state.templates.find(x => x.id === tplId);
    if (!t) return;
    if (t.id === 'none') { applyTemplateToPunish('none'); return; }
    openPunishModal(t.type);
    dom().punishTemplate.value = tplId;
    applyTemplateToPunish(tplId);
    toast('info', 'Template Applied', t.name);
  };

  window.toggleTemplateFavorite = function(tplId) {
    if (!tplId || tplId === 'none') return;
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('TOGGLE_TEMPLATE_FAVORITE', { id: tplId });
    }
  };

  window.deleteTemplate = function(tplId) {
    if (tplId === 'none') {
      toast('warn', 'Cannot Delete', 'The "None" template cannot be deleted.');
      return;
    }
    // Permission check
    if (window.hasPermission && !window.hasPermission('moderex.template.delete')) {
      toast('warn', 'No Permission', 'You do not have permission to delete templates');
      return;
    }

    // Confirm before deleting
    const tpl = state.templates.find(t => t.id === tplId);
    const name = tpl?.name || tplId;

    openConfirmPanel({
      title: 'Delete Template',
      body: `Are you sure you want to delete the template "${name}"? This cannot be undone.`,
      confirmText: 'Delete',
      onConfirm: () => {
        // Send to backend via WebSocket
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('DELETE_TEMPLATE', { id: tplId });
          // Optimistically remove from local state
          state.templates = state.templates.filter(t => t.id !== tplId);
          ui.renderTemplates();
        } else {
          toast('warn', 'Not Connected', 'WebSocket not connected');
        }
      }
    });
  };

  window.createTemplateUI = function() {
    // Permission check
    if (window.hasPermission && !window.hasPermission('moderex.template.create')) {
      toast('warn', 'No Permission', 'You do not have permission to create templates');
      return;
    }
    openGenericModal({
      title: 'Create Template',
      html: `
        <div class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Name</div><input class="input" id="mTplName" placeholder="e.g. Spam (1d mute)" /></div>
          <div><div class="hintline" style="margin-top:0">Type</div><select class="input" id="mTplType"><option>WARN</option><option>MUTE</option><option>KICK</option><option>BAN</option><option>IPBAN</option><option>IPMUTE</option></select></div>
        </div>
        <div style="margin-top:12px" class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Duration</div><input class="input" id="mTplDur" placeholder="e.g. 7d, 1h, permanent" /></div>
          <div><div class="hintline" style="margin-top:0">Category</div><input class="input" id="mTplCategory" placeholder="e.g. Chat, Cheating, Behavior" /></div>
        </div>
        <div style="margin-top:12px">
          <div class="hintline" style="margin-top:0">Reason</div>
          <input class="input" id="mTplReason" placeholder="Reason for this punishment..." style="width:100%" />
        </div>
      `,
      onSubmit: () => {
        const name = ($('#mTplName')?.value || '').trim();
        if (!name) { toast('warn', 'Missing', 'Enter template name.'); return false; }
        const type = $('#mTplType').value;
        const duration = $('#mTplDur').value.trim();
        const category = $('#mTplCategory').value.trim() || 'General';
        const reason = $('#mTplReason').value.trim() || 'No reason';

        // Send to backend via WebSocket
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          // Note: message 'type' is 'CREATE_TEMPLATE', punishment type goes in separate field
          ws.send('CREATE_TEMPLATE', { name, punishmentType: type, duration, reason, category });
        } else {
          toast('warn', 'Not Connected', 'WebSocket not connected');
          return false;
        }
        return true;
      }
    });
  };

  window.editTemplateUI = function(tplId) {
    // Permission check
    if (window.hasPermission && !window.hasPermission('moderex.template.edit')) {
      toast('warn', 'No Permission', 'You do not have permission to edit templates');
      return;
    }
    const tpl = state.templates.find(t => t.id === tplId);
    if (!tpl) return;
    if (tplId === 'none') {
      toast('warn', 'Cannot Edit', 'The "None" template cannot be edited.');
      return;
    }
    openGenericModal({
      title: 'Edit Template',
      html: `
        <div class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Name</div><input class="input" id="mTplName" value="${escapeHtml(tpl.name)}" /></div>
          <div><div class="hintline" style="margin-top:0">Type</div><select class="input" id="mTplType">
            ${['WARN','MUTE','KICK','BAN','IPBAN','IPMUTE'].map(t => `<option ${tpl.type === t ? 'selected' : ''}>${t}</option>`).join('')}
          </select></div>
        </div>
        <div style="margin-top:12px" class="grid cols-2">
          <div><div class="hintline" style="margin-top:0">Duration</div><input class="input" id="mTplDur" value="${escapeHtml(tpl.duration || '')}" /></div>
          <div><div class="hintline" style="margin-top:0">Category</div><input class="input" id="mTplCategory" value="${escapeHtml(tpl.category || 'General')}" /></div>
        </div>
        <div style="margin-top:12px">
          <div class="hintline" style="margin-top:0">Reason</div>
          <input class="input" id="mTplReason" value="${escapeHtml(tpl.reason || '')}" style="width:100%" />
        </div>
      `,
      onSubmit: () => {
        const name = ($('#mTplName')?.value || '').trim();
        if (!name) { toast('warn', 'Missing', 'Enter template name.'); return false; }
        const type = $('#mTplType').value;
        const duration = $('#mTplDur').value.trim();
        const category = $('#mTplCategory').value.trim() || 'General';
        const reason = $('#mTplReason').value.trim() || 'No reason';

        // Send to backend via WebSocket
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('UPDATE_TEMPLATE', { id: tplId, name, punishmentType: type, duration, reason, category });
        } else {
          toast('warn', 'Not Connected', 'WebSocket not connected');
          return false;
        }
        return true;
      }
    });
  };

  // ===== REPLAYS =====
  function renderReplayList() {
    const container = document.getElementById('replayRows');
    const emptyState = document.getElementById('replayEmpty');
    if (!container) return;

    const search = (document.getElementById('replaySearch')?.value || '').toLowerCase();
    const filtered = (state.replays || []).filter(r => {
      if (!search) return true;
      const name = (r.name || r.primaryName || '').toLowerCase();
      const playerNames = (r.playerNames || [r.primaryName]).join(' ').toLowerCase();
      const tags = (r.tags || []).join(' ').toLowerCase();
      return name.includes(search) || playerNames.includes(search) || tags.includes(search);
    });

    if (filtered.length === 0) {
      container.innerHTML = '';
      if (emptyState) emptyState.style.display = 'flex';
      return;
    }

    if (emptyState) emptyState.style.display = 'none';

    container.innerHTML = filtered.map(r => {
      const id = r.sessionId || r.id;
      const name = r.name || r.primaryName || 'Unnamed Replay';
      const players = r.playerNames || [r.primaryName] || [];
      const playerCount = r.playerCount || players.length || 1;
      const duration = r.duration ? formatDuration(r.duration) : (r.formattedDuration || '0:00');
      const status = r.status || (r.endTime ? 'COMPLETE' : 'RECORDING');
      const createdAt = r.createdAt || r.startTime;
      const statusClass = status === 'RECORDING' ? 'bad' : status === 'COMPLETE' ? 'ok' : 'gray';
      const statusIcon = status === 'RECORDING' ? 'fa-circle-dot' : status === 'COMPLETE' ? 'fa-check-circle' : 'fa-clock';

      const canManage = window.hasPermission ? window.hasPermission('moderex.replays.manage') : true;

      return `
        <tr>
          <td><b>${escapeHtml(name)}</b></td>
          <td>
            <span class="badge gray">${playerCount} player${playerCount !== 1 ? 's' : ''}</span>
            ${players.slice(0, 2).map(p => `<span style="margin-left:4px;font-size:12px;color:var(--muted)">${escapeHtml(p)}</span>`).join('')}
            ${players.length > 2 ? `<span style="font-size:11px;color:var(--muted)">+${players.length - 2} more</span>` : ''}
          </td>
          <td>${duration}</td>
          <td><span class="badge ${statusClass}"><i class="fa-solid ${statusIcon}"></i> ${status}</span></td>
          <td>${createdAt ? fmtShort(createdAt) : 'Unknown'}</td>
          <td style="text-align:right">
            <button class="mini primary" onclick="viewReplay('${id}')"><i class="fa-solid fa-play"></i> View</button>
            ${canManage ? `
              <button class="mini" onclick="renameReplay('${id}', '${escapeHtml(name)}')"><i class="fa-solid fa-pen"></i></button>
              <button class="mini bad" onclick="deleteReplay('${id}')"><i class="fa-solid fa-trash"></i></button>
            ` : ''}
          </td>
        </tr>
      `;
    }).join('');
  }

  function formatDuration(seconds) {
    if (typeof seconds !== 'number') return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  function updateReplayStats() {
    const replays = state.replays || [];
    const total = replays.length;
    const recording = replays.filter(r => r.status === 'RECORDING' || (!r.endTime && !r.status)).length;
    const complete = replays.filter(r => r.status === 'COMPLETE' || r.endTime).length;
    const totalSize = replays.reduce((sum, r) => sum + (r.fileSize || 0), 0);

    const totalEl = document.getElementById('replayTotalCount');
    const recordingEl = document.getElementById('replayRecordingCount');
    const completeEl = document.getElementById('replayCompleteCount');
    const storageEl = document.getElementById('replayStorageUsed');

    if (totalEl) totalEl.textContent = total.toLocaleString();
    if (recordingEl) recordingEl.textContent = recording.toLocaleString();
    if (completeEl) completeEl.textContent = complete.toLocaleString();
    if (storageEl) storageEl.textContent = formatFileSize(totalSize);
  }

  function formatFileSize(bytes) {
    if (!bytes || bytes < 1024) return (bytes || 0) + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
  }

  function loadReplaySettings(data) {
    const enabledEl = document.getElementById('replayEnabled');
    const maxDurationEl = document.getElementById('replayMaxDuration');
    const retentionEl = document.getElementById('replayRetentionDays');
    const maxConcurrentEl = document.getElementById('replayMaxConcurrent');
    const triggerAcEl = document.getElementById('replayTriggerAnticheat');
    const triggerPunEl = document.getElementById('replayTriggerPunishment');

    if (enabledEl) enabledEl.checked = data.enabled !== false;
    if (maxDurationEl) maxDurationEl.value = data.maxDuration || 300;
    if (retentionEl) retentionEl.value = data.retentionDays || 30;
    if (maxConcurrentEl) maxConcurrentEl.value = data.maxConcurrent || 10;
    if (triggerAcEl) triggerAcEl.checked = !!data.triggerOnAnticheat;
    if (triggerPunEl) triggerPunEl.checked = !!data.triggerOnPunishment;
  }

  window.refreshReplays = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_REPLAYS');
    }
  };

  window.viewReplay = function(replayId) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('GET_REPLAY', { sessionId: replayId });
    }
  };

  window.renameReplay = function(replayId, currentName) {
    openGenericModal({
      title: 'Rename Replay',
      html: `
        <div class="hintline" style="margin-top:0">New Name</div>
        <input class="input" id="renameReplayInput" value="${escapeHtml(currentName)}" style="width:100%" />
      `,
      onSubmit: () => {
        const newName = document.getElementById('renameReplayInput')?.value?.trim();
        if (!newName) {
          toast('warn', 'Missing', 'Enter a name');
          return false;
        }
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('RENAME_REPLAY', { sessionId: replayId, name: newName });
          toast('ok', 'Renamed', 'Replay renamed');
        }
        return true;
      }
    });
  };

  window.deleteReplay = function(replayId) {
    openConfirmPanel({
      title: 'Delete Replay',
      body: 'Are you sure you want to delete this replay? This cannot be undone.',
      confirmText: 'Delete',
      onConfirm: () => {
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('DELETE_REPLAY', { sessionId: replayId });
        }
      }
    });
  };

  window.openRecordReplayModal = function() {
    if (window.hasPermission && !window.hasPermission('moderex.replays.record')) {
      toast('warn', 'No Permission', 'You do not have permission to start recordings');
      return;
    }

    openGenericModal({
      title: 'Start Recording',
      html: `
        <div class="hintline" style="margin-top:0">Recording Name</div>
        <input class="input" id="recordReplayName" placeholder="e.g. Suspicious Activity - Player123" style="width:100%" />
        <div class="hintline" style="margin-top:12px">Select Player(s) to Record</div>
        <div class="gsearch" style="margin-bottom:8px">
          <i class="fa-solid fa-magnifying-glass"></i>
          <input type="text" id="recordPlayerSearch" placeholder="Search players..." oninput="filterRecordPlayerList()">
        </div>
        <div id="recordPlayerList" style="max-height:200px;overflow:auto;border:1px solid var(--border);border-radius:var(--radius);padding:8px"></div>
      `,
      onSubmit: () => {
        const name = document.getElementById('recordReplayName')?.value?.trim();
        if (!name) {
          toast('warn', 'Missing', 'Enter a recording name');
          return false;
        }
        const selected = Array.from(document.querySelectorAll('#recordPlayerList input:checked')).map(cb => cb.value);
        if (selected.length === 0) {
          toast('warn', 'Missing', 'Select at least one player');
          return false;
        }
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('START_REPLAY', { name, playerUuids: selected });
          toast('ok', 'Recording Started', `Recording ${selected.length} player(s)`);
        }
        return true;
      }
    });

    // Populate player list
    setTimeout(() => {
      const container = document.getElementById('recordPlayerList');
      if (!container) return;
      const onlinePlayers = state.players.filter(p => p.online || p.status === 'online');
      if (onlinePlayers.length === 0) {
        container.innerHTML = '<div style="color:var(--muted);text-align:center;padding:20px">No online players</div>';
        return;
      }
      container.innerHTML = onlinePlayers.map(p => `
        <label style="display:flex;align-items:center;gap:8px;padding:6px;cursor:pointer;border-radius:4px" class="hover-highlight">
          <input type="checkbox" value="${p.uuid || p.id}">
          <img src="https://mc-heads.net/avatar/${p.uuid || p.id}/24" style="width:24px;height:24px;border-radius:4px">
          <span>${escapeHtml(p.name)}</span>
        </label>
      `).join('');
    }, 50);
  };

  window.filterRecordPlayerList = function() {
    const search = document.getElementById('recordPlayerSearch')?.value?.toLowerCase() || '';
    const labels = document.querySelectorAll('#recordPlayerList label');
    labels.forEach(label => {
      const name = label.querySelector('span')?.textContent?.toLowerCase() || '';
      label.style.display = name.includes(search) ? '' : 'none';
    });
  };

  window.updateReplaySettings = function() {
    if (window.hasPermission && !window.hasPermission('moderex.replays.configure')) {
      toast('warn', 'No Permission', 'You do not have permission to configure replays');
      return;
    }

    const settings = {
      enabled: document.getElementById('replayEnabled')?.checked ?? true,
      maxDuration: parseInt(document.getElementById('replayMaxDuration')?.value) || 300,
      retentionDays: parseInt(document.getElementById('replayRetentionDays')?.value) || 30,
      maxConcurrent: parseInt(document.getElementById('replayMaxConcurrent')?.value) || 10,
      triggerOnAnticheat: document.getElementById('replayTriggerAnticheat')?.checked ?? false,
      triggerOnPunishment: document.getElementById('replayTriggerPunishment')?.checked ?? false
    };

    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_REPLAY_SETTINGS', settings);
      toast('ok', 'Saved', 'Replay settings updated');
    }
  };

  // ===== 3D REPLAY VIEWER MODAL =====
  let activeReplay3DViewer = null;
  let pendingReplaySessionId = null;

  function formatReplayTime(ms) {
    const seconds = Math.floor(ms / 1000);
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  function openReplayDetailsModal(replay, snapshots, blockLogs) {
    const name = replay.name || replay.primaryName || 'Unnamed';
    const duration = (replay.endTime || 0) - (replay.startTime || 0);
    const hasChunkData = replay.hasChunkData || false;
    const has3D = typeof THREE !== 'undefined' && window.MX?.Replay3DViewer;

    // Store session ID for incoming REPLAY_CHUNKS message
    pendingReplaySessionId = replay.sessionId;

    // Clean up previous viewer
    if (activeReplay3DViewer) {
      activeReplay3DViewer.dispose();
      activeReplay3DViewer = null;
    }

    // Create fullscreen overlay
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.id = 'replay3DOverlay';
    overlay.style.cssText = 'z-index:8000;display:flex;align-items:stretch;justify-content:stretch;padding:0;';
    overlay.innerHTML = `
      <div style="width:100%;height:100%;display:flex;flex-direction:column;background:var(--bg-surface)" onclick="event.stopPropagation()">
        <!-- Header -->
        <div style="display:flex;align-items:center;justify-content:space-between;padding:12px 20px;background:var(--bg-card);border-bottom:1px solid var(--border);flex-shrink:0">
          <div style="display:flex;align-items:center;gap:12px">
            <i class="fa-solid fa-cube" style="color:var(--primary-light);font-size:18px"></i>
            <div>
              <b style="font-size:15px">${escapeHtml(name)}</b>
              <div style="font-size:12px;color:var(--muted)">${escapeHtml(replay.primaryName || '')} &bull; ${formatReplayTime(duration)} &bull; ${escapeHtml(replay.reason || 'Manual')}</div>
            </div>
          </div>
          <div style="display:flex;align-items:center;gap:10px">
            ${has3D && hasChunkData ? '<span class="badge ok" id="r3dTerrainBadge"><i class="fa-solid fa-mountain"></i> Terrain</span>' : ''}
            ${has3D && !hasChunkData ? '<span class="badge gray"><i class="fa-solid fa-cube"></i> No terrain data</span>' : ''}
            <span class="badge gray" id="r3dSnapshotBadge">${snapshots.length} snapshots</span>
            <button class="mini" id="r3dClose" style="font-size:16px"><i class="fa-solid fa-xmark"></i></button>
          </div>
        </div>

        <!-- 3D Viewer Area -->
        <div style="flex:1;position:relative;overflow:hidden;min-height:0">
          <div id="r3dContainer" style="width:100%;height:100%;background:#1a1a2e"></div>

          <!-- Loading overlay -->
          <div id="r3dLoading" style="position:absolute;inset:0;display:flex;flex-direction:column;align-items:center;justify-content:center;background:rgba(10,16,24,0.9);z-index:10">
            <div class="spinner" style="width:40px;height:40px;margin-bottom:16px"></div>
            <div style="color:#fff;font-size:14px" id="r3dLoadingText">Initializing 3D viewer...</div>
          </div>

          <!-- Camera mode indicator -->
          <div style="position:absolute;top:12px;left:12px;display:flex;gap:6px;z-index:5" id="r3dCameraModes">
            <button class="mini" id="r3dCamOrbit" title="Orbit Camera (drag to rotate, scroll to zoom)" style="background:var(--primary);color:#fff"><i class="fa-solid fa-arrows-rotate"></i></button>
            <button class="mini" id="r3dCamFollow" title="Follow Player"><i class="fa-solid fa-user"></i></button>
            <button class="mini" id="r3dCamFree" title="Free Camera (WASD + Mouse)"><i class="fa-solid fa-gamepad"></i></button>
          </div>

          <!-- Info panel -->
          <div style="position:absolute;top:12px;right:12px;background:rgba(0,0,0,0.7);padding:10px 14px;border-radius:var(--radius);font-size:12px;color:#ccc;z-index:5;min-width:140px" id="r3dInfoPanel">
            <div><span style="color:var(--muted)">Position:</span> <span id="r3dPosInfo">-</span></div>
            <div style="margin-top:4px"><span style="color:var(--muted)">Action:</span> <span id="r3dActionInfo">-</span></div>
            <div style="margin-top:4px"><span style="color:var(--muted)">State:</span> <span id="r3dStateInfo">-</span></div>
          </div>
        </div>

        <!-- Controls Bar -->
        <div style="display:flex;align-items:center;gap:12px;padding:12px 20px;background:var(--bg-card);border-top:1px solid var(--border);flex-shrink:0">
          <!-- Skip back -->
          <button class="mini" id="r3dSkipBack" title="Skip back 5s"><i class="fa-solid fa-backward"></i></button>

          <!-- Play/Pause -->
          <button class="mini primary" id="r3dPlayBtn" style="width:36px;height:36px;font-size:16px" title="Play/Pause"><i class="fa-solid fa-play"></i></button>

          <!-- Skip forward -->
          <button class="mini" id="r3dSkipFwd" title="Skip forward 5s"><i class="fa-solid fa-forward"></i></button>

          <!-- Current time -->
          <span style="font-family:var(--font-mono);font-size:13px;min-width:50px;text-align:right" id="r3dTimeDisplay">0:00</span>

          <!-- Timeline slider -->
          <input type="range" id="r3dTimeline" min="0" max="1000" value="0" style="flex:1;accent-color:var(--primary)">

          <!-- Total time -->
          <span style="font-family:var(--font-mono);font-size:13px;min-width:50px;color:var(--muted)" id="r3dTotalTime">${formatReplayTime(duration)}</span>

          <!-- Speed control -->
          <select id="r3dSpeed" style="background:var(--bg-input);color:var(--text);border:1px solid var(--border);border-radius:var(--radius);padding:4px 8px;font-size:12px">
            <option value="0.25">0.25x</option>
            <option value="0.5">0.5x</option>
            <option value="1" selected>1x</option>
            <option value="2">2x</option>
            <option value="4">4x</option>
          </select>
        </div>
      </div>
    `;

    document.body.appendChild(overlay);

    // Wire up close button
    const closeViewer = () => {
      if (activeReplay3DViewer) {
        activeReplay3DViewer.dispose();
        activeReplay3DViewer = null;
      }
      pendingReplaySessionId = null;
      overlay.classList.add('fade-out');
      setTimeout(() => overlay.remove(), 220);
    };

    document.getElementById('r3dClose').onclick = closeViewer;

    // Handle Escape key
    const escHandler = (e) => {
      if (e.key === 'Escape') {
        document.removeEventListener('keydown', escHandler);
        closeViewer();
      }
    };
    document.addEventListener('keydown', escHandler);

    // Initialize 3D viewer
    if (has3D) {
      requestAnimationFrame(() => {
        const container = document.getElementById('r3dContainer');
        if (!container) return;

        try {
          const viewer = new window.MX.Replay3DViewer(container);
          activeReplay3DViewer = viewer;

          // Set replay data
          viewer.setReplayData(replay, snapshots, blockLogs || []);

          // Show fallback ground if no terrain
          if (!hasChunkData) {
            viewer.showFallbackGround();
          }

          // Time update callback
          viewer.onTimeUpdate((currentMs, totalMs) => {
            const display = document.getElementById('r3dTimeDisplay');
            const timeline = document.getElementById('r3dTimeline');
            if (display) display.textContent = formatReplayTime(currentMs);
            if (timeline && totalMs > 0) {
              timeline.value = Math.round((currentMs / totalMs) * 1000);
            }

            // Update info panel
            updateReplay3DInfoPanel(snapshots, replay.startTime + currentMs);
          });

          viewer.onPlaybackEnd(() => {
            const btn = document.getElementById('r3dPlayBtn');
            if (btn) btn.innerHTML = '<i class="fa-solid fa-play"></i>';
          });

          // Wire up controls
          document.getElementById('r3dPlayBtn').onclick = () => {
            const playing = viewer.togglePlayback();
            document.getElementById('r3dPlayBtn').innerHTML = playing
              ? '<i class="fa-solid fa-pause"></i>'
              : '<i class="fa-solid fa-play"></i>';
          };

          document.getElementById('r3dSkipBack').onclick = () => viewer.skip(-5);
          document.getElementById('r3dSkipFwd').onclick = () => viewer.skip(5);

          document.getElementById('r3dTimeline').addEventListener('input', (e) => {
            const pct = parseInt(e.target.value) / 1000;
            viewer.seek(pct * viewer.getTotalDuration());
          });

          document.getElementById('r3dSpeed').addEventListener('change', (e) => {
            viewer.setSpeed(parseFloat(e.target.value));
          });

          // Camera mode buttons
          const camButtons = { orbit: 'r3dCamOrbit', follow: 'r3dCamFollow', free: 'r3dCamFree' };
          const updateCamButtons = (mode) => {
            Object.entries(camButtons).forEach(([m, id]) => {
              const btn = document.getElementById(id);
              if (btn) {
                btn.style.background = m === mode ? 'var(--primary)' : '';
                btn.style.color = m === mode ? '#fff' : '';
              }
            });
          };

          document.getElementById('r3dCamOrbit').onclick = () => { viewer.setCameraMode('orbit'); updateCamButtons('orbit'); };
          document.getElementById('r3dCamFollow').onclick = () => { viewer.setCameraMode('follow'); updateCamButtons('follow'); };
          document.getElementById('r3dCamFree').onclick = () => { viewer.setCameraMode('free'); updateCamButtons('free'); };

          // Hide loading
          const loadingEl = document.getElementById('r3dLoading');
          if (hasChunkData) {
            const loadText = document.getElementById('r3dLoadingText');
            if (loadText) loadText.textContent = 'Loading terrain data...';
            // REPLAY_CHUNKS will arrive shortly and hide this
          } else {
            if (loadingEl) loadingEl.style.display = 'none';
          }

          console.log('[Replay3D] Viewer initialized');
        } catch (e) {
          console.error('[Replay3D] Failed to initialize:', e);
          const loadingEl = document.getElementById('r3dLoading');
          if (loadingEl) {
            loadingEl.innerHTML = `
              <i class="fa-solid fa-exclamation-triangle" style="font-size:32px;color:var(--warn);margin-bottom:12px"></i>
              <div style="color:#fff">Failed to initialize 3D viewer</div>
              <div style="color:var(--muted);font-size:12px;margin-top:8px">${escapeHtml(e.message)}</div>
            `;
          }
        }
      });
    } else {
      // No Three.js - show info-only modal
      const loadingEl = document.getElementById('r3dLoading');
      if (loadingEl) {
        loadingEl.innerHTML = `
          <i class="fa-solid fa-cube" style="font-size:40px;color:var(--muted);margin-bottom:16px"></i>
          <div style="color:#fff;font-size:15px">3D Viewer Not Available</div>
          <div style="color:var(--muted);margin-top:8px;max-width:360px;text-align:center">
            Three.js library could not be loaded. The 3D replay viewer requires an internet connection for the initial load.
          </div>
          <div style="margin-top:20px;padding:12px 16px;background:rgba(6,182,212,0.1);border-radius:var(--radius);border:1px solid rgba(6,182,212,0.2)">
            <i class="fa-solid fa-info-circle" style="color:#06b6d4;margin-right:8px"></i>
            <span style="color:#ccc">Use <code style="color:#06b6d4">/replay play ${escapeHtml(replay.sessionId)}</code> in-game instead.</span>
          </div>
        `;
      }
    }
  }

  function updateReplay3DInfoPanel(snapshots, absoluteTime) {
    const posEl = document.getElementById('r3dPosInfo');
    const actionEl = document.getElementById('r3dActionInfo');
    const stateEl = document.getElementById('r3dStateInfo');
    if (!posEl) return;

    // Find latest snapshot before this time
    let latest = null;
    for (const snap of snapshots) {
      if (snap.timestamp <= absoluteTime) latest = snap;
    }

    if (!latest) return;

    posEl.textContent = `${latest.x?.toFixed(1) || 0} ${latest.y?.toFixed(1) || 0} ${latest.z?.toFixed(1) || 0}`;

    const action = latest.action || latest.actionType || 'NONE';
    const actionData = latest.actionData || '';
    actionEl.textContent = action !== 'NONE' ? `${action}${actionData ? ': ' + actionData : ''}` : 'None';

    let stateTxt = 'Standing';
    if (latest.sneaking) stateTxt = 'Sneaking';
    else if (latest.sprinting) stateTxt = 'Sprinting';
    else if (latest.swimming) stateTxt = 'Swimming';
    else if (latest.gliding) stateTxt = 'Gliding';
    else if (!latest.onGround) stateTxt = 'Airborne';
    stateEl.textContent = stateTxt;
  }

  function handleReplayChunks(data) {
    if (!activeReplay3DViewer || !data.chunkData) return;

    // Verify it's for the current replay
    if (data.sessionId && data.sessionId !== pendingReplaySessionId) {
      console.log('[Replay3D] Ignoring chunks for different session:', data.sessionId);
      return;
    }

    const loadText = document.getElementById('r3dLoadingText');
    if (loadText) loadText.textContent = `Loading terrain (${(data.sizeBytes / 1024).toFixed(0)} KB)...`;

    activeReplay3DViewer.loadChunkData(data.chunkData).then((count) => {
      console.log(`[Replay3D] Terrain loaded: ${count} chunks`);

      // Remove fallback ground if it was shown
      activeReplay3DViewer.removeFallbackGround();

      // Update badge
      const badge = document.getElementById('r3dTerrainBadge');
      if (badge) badge.innerHTML = `<i class="fa-solid fa-mountain"></i> ${count} chunks`;

      // Hide loading
      const loadingEl = document.getElementById('r3dLoading');
      if (loadingEl) loadingEl.style.display = 'none';
    }).catch((err) => {
      console.error('[Replay3D] Failed to load terrain:', err);
      activeReplay3DViewer.showFallbackGround();

      const loadingEl = document.getElementById('r3dLoading');
      if (loadingEl) loadingEl.style.display = 'none';

      toast('warn', 'Terrain Error', 'Failed to load terrain data. Showing flat ground.');
    });
  }

  // Initialize replays state
  if (!state.replays) state.replays = [];

  // ===== GENERIC MODAL =====
  let genericModalEl = null;
  function closeOverlayAnimated(overlay) {
    if (!overlay) return;
    const modal = overlay.querySelector('.modal');
    overlay.classList.add('fade-out');
    modal?.classList.add('fade-out');
    setTimeout(() => overlay.remove(), 220);
  }
  function openGenericModal({ title, html, onSubmit = () => true }) {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top"><div style="display:flex;align-items:center;gap:10px"><i class="fa-solid fa-pen-to-square" style="color:var(--muted)"></i><b>${escapeHtml(title)}</b></div>
          <button class="mini" id="gmClose"><i class="fa-solid fa-xmark"></i></button></div>
        <div class="modal-body">${html}</div>
        <div class="modal-foot"><span class="badge gray"><i class="fa-solid fa-circle-info"></i> Configuration</span>
          <div style="display:flex;gap:10px"><button class="btn ghost" id="gmCancel"><i class="fa-solid fa-xmark"></i> Cancel</button><button class="btn primary" id="gmSubmit"><i class="fa-solid fa-check"></i> Save</button></div></div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#gmClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#gmCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#gmSubmit', overlay).onclick = () => { if (onSubmit()) closeOverlayAnimated(overlay); };
  }

  function openConfirmPanel({ title, body, confirmText = 'Confirm', onConfirm = () => {} }) {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show top';
    overlay.style.zIndex = 8000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-triangle-exclamation" style="color:var(--warn)"></i>
            <b>${escapeHtml(title)}</b>
          </div>
          <button class="mini" id="cpClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="card" style="margin:0">${escapeHtml(body)}</div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Action Required</span>
          <div style="display:flex;gap:10px">
            <button class="btn ghost" id="cpCancel"><i class="fa-solid fa-xmark"></i> Cancel</button>
            <button class="btn bad" id="cpConfirm"><i class="fa-solid fa-xmark"></i> ${escapeHtml(confirmText)}</button>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#cpClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#cpCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#cpConfirm', overlay).onclick = () => { onConfirm(); closeOverlayAnimated(overlay); };
  }

  window.openCommandHistory = function(playerId) {
    // Check permission first
    if (window.hasPermission && !window.hasPermission('moderex.alerts.commands')) {
      toast('error', 'Permission Denied', 'You do not have permission to view command history.');
      return;
    }

    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();

    const ws = window.MX?.ws;
    let currentPage = 1;
    let totalPages = 1;
    let totalCount = 0;
    let searchTimeout = null;

    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()" style="max-width:700px">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-terminal" style="color:var(--primary-light)"></i>
            <b>Command History</b>
            <span class="badge gray" id="cmdCount">Loading...</span>
          </div>
          <button class="mini" id="cmdClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="cmdSearch" placeholder="Search commands...">
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="cmdList">
            <div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>
          </div>
          <div class="pagination" style="margin-top:14px;display:flex;justify-content:center;gap:8px;align-items:center" id="cmdPagination"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="cmdCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;

    const close = () => closeOverlayAnimated(overlay);
    const listEl = $('#cmdList', overlay);
    const searchEl = $('#cmdSearch', overlay);
    const countEl = $('#cmdCount', overlay);
    const paginationEl = $('#cmdPagination', overlay);

    const fetchCommands = (page = 1, search = '') => {
      currentPage = page;
      listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>';

      if (ws) ws.send('GET_COMMAND_HISTORY', { uuid: p.id, page, limit: 50, search });
    };

    const renderCommands = (data) => {
      const commands = data.commands || [];
      totalPages = data.totalPages || 1;
      totalCount = data.total || 0;
      currentPage = data.page || 1;

      countEl.textContent = `${totalCount} total`;

      if (commands.length === 0) {
        listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>No commands found.</small></div></div>';
      } else {
        listEl.innerHTML = commands.map(cmd => `
          <div class="drawer-row" data-player-id="${p.id}">
            <div class="meta" style="flex:1">
              <b style="font-family:var(--font-mono);font-size:13px">${escapeHtml(cmd.cmd)}</b>
              <small>${escapeHtml(fmtLong(cmd.t))}${cmd.server ? ' | ' + escapeHtml(cmd.server) : ''}</small>
            </div>
          </div>
        `).join('');
      }

      // Render pagination
      if (totalPages > 1) {
        let paginationHtml = '';
        if (currentPage > 1) {
          paginationHtml += `<button class="mini" onclick="window._cmdPageNav(${currentPage - 1})"><i class="fa-solid fa-chevron-left"></i></button>`;
        }
        paginationHtml += `<span style="color:var(--text-muted)">Page ${currentPage} of ${totalPages}</span>`;
        if (currentPage < totalPages) {
          paginationHtml += `<button class="mini" onclick="window._cmdPageNav(${currentPage + 1})"><i class="fa-solid fa-chevron-right"></i></button>`;
        }
        paginationEl.innerHTML = paginationHtml;
        paginationEl.style.display = 'flex';
      } else {
        paginationEl.style.display = 'none';
      }
    };

    // Store handler for pagination navigation
    window._cmdPageNav = (page) => fetchCommands(page, searchEl.value.trim());

    // Handle WebSocket response - ws.on receives data directly (not event)
    const handleResponse = (data) => {
      if (data && data.uuid === p.id) {
        renderCommands(data);
      }
    };

    if (ws) ws.on('COMMAND_HISTORY_DATA', handleResponse);

    const closeWithCleanup = () => {
      if (ws) ws.off('COMMAND_HISTORY_DATA', handleResponse);
      delete window._cmdPageNav;
      close();
    };

    $('#cmdClose', overlay).onclick = closeWithCleanup;
    $('#cmdCloseBtn', overlay).onclick = closeWithCleanup;

    searchEl.addEventListener('input', () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => fetchCommands(1, searchEl.value.trim()), 300);
    });

    // Initial fetch
    fetchCommands(1, '');
  }

  window.openChatLogs = function(playerId) {
    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-comments" style="color:var(--accent-light)"></i>
            <b>Chat Logs</b>
          </div>
          <button class="mini" id="chatClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="toolbar" style="margin-top:0">
            <div class="left" style="gap:10px">
              <div class="gsearch" style="width:100%;max-width:420px">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="text" id="chatSearch" placeholder="Search chat logs...">
              </div>
              <input type="date" class="input" id="chatFrom" style="max-width:160px">
              <input type="date" class="input" id="chatTo" style="max-width:160px">
            </div>
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="chatList"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="chatCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#chatClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#chatCloseBtn', overlay).onclick = () => closeOverlayAnimated(overlay);
    const listEl = $('#chatList', overlay);
    const searchEl = $('#chatSearch', overlay);
    const fromEl = $('#chatFrom', overlay);
    const toEl = $('#chatTo', overlay);
    // Use fetched chat logs from player details, with fallback to state.logs
    const fetchedChatLogs = (p.chatLogs || []).map(l => ({
      t: l.t,
      title: `Chat | ${p.name}`,
      detail: l.content,
      playerId: p.id
    }));
    const liveChatLogs = state.logs.filter(l => l.channel === 'chat' && (l.playerId === p.id || (l.title || '').includes(p.name)));
    const allLogs = fetchedChatLogs.length > 0 ? fetchedChatLogs : liveChatLogs;
    const render = () => {
      const q = (searchEl.value || '').trim().toLowerCase();
      const fromVal = fromEl.value ? new Date(fromEl.value).getTime() : null;
      const toVal = toEl.value ? new Date(toEl.value).getTime() + 86400000 : null;
      const filtered = allLogs.filter(l => {
        if (q && !`${l.title} ${l.detail}`.toLowerCase().includes(q)) return false;
        if (fromVal && l.t < fromVal) return false;
        if (toVal && l.t > toVal) return false;
        return true;
      }).slice(-200);
      listEl.innerHTML = filtered.length ? filtered.map(l => `
        <div class="drawer-row" data-player-id="${p.id}"><div class="meta"><b>${escapeHtml(fmtLong(l.t))}</b><small>${escapeHtml(l.detail || l.content)}</small></div></div>
      `).join('') : `<div class="drawer-row"><div class="meta"><small>No chat logs found.</small></div></div>`;
    };
    searchEl.addEventListener('input', render);
    fromEl.addEventListener('change', render);
    toEl.addEventListener('change', render);
    render();
  };

  window.openAutomodLogs = function(playerId) {
    // Check permission first - must match backend (moderex.history.automod)
    if (window.hasPermission && !window.hasPermission('moderex.history.automod')) {
      toast('error', 'Permission Denied', 'You do not have permission to view automod logs.');
      return;
    }

    const p = state.players.find(x => x.id === playerId);
    if (!p) return;
    if (genericModalEl) genericModalEl.remove();

    const ws = window.MX?.ws;
    let currentPage = 1;
    let totalPages = 1;
    let totalCount = 0;
    let searchTimeout = null;

    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()" style="max-width:700px">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-robot" style="color:var(--primary-light)"></i>
            <b>Automod Logs</b>
            <span class="badge gray" id="autoCount">Loading...</span>
          </div>
          <button class="mini" id="autoClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="autoSearch" placeholder="Search automod logs...">
          </div>
          <div class="card" style="margin-top:14px;max-height:400px;overflow-y:auto" id="autoList">
            <div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>
          </div>
          <div class="pagination" style="margin-top:14px;display:flex;justify-content:center;gap:8px;align-items:center" id="autoPagination"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(p.name)}</span>
          <button class="btn ghost" id="autoCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;

    const close = () => closeOverlayAnimated(overlay);
    const listEl = $('#autoList', overlay);
    const searchEl = $('#autoSearch', overlay);
    const countEl = $('#autoCount', overlay);
    const paginationEl = $('#autoPagination', overlay);

    const fetchLogs = (page = 1, search = '') => {
      currentPage = page;
      listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>Loading...</small></div></div>';

      if (ws) ws.send('GET_AUTOMOD_LOGS', { uuid: p.id, page, limit: 50, search });
    };

    const renderLogs = (data) => {
      const logs = data.logs || [];
      totalPages = data.totalPages || 1;
      totalCount = data.total || 0;
      currentPage = data.page || 1;

      countEl.textContent = `${totalCount} total`;

      if (logs.length === 0) {
        listEl.innerHTML = '<div class="drawer-row"><div class="meta"><small>No automod logs found.</small></div></div>';
      } else {
        listEl.innerHTML = logs.map(l => `
          <div class="drawer-row" data-player-id="${p.id}">
            <div class="meta" style="flex:1">
              <b>${escapeHtml(l.rule || 'Automod')}</b>
              <small>${escapeHtml(fmtLong(l.t))}${l.server ? ' | ' + escapeHtml(l.server) : ''}</small>
            </div>
            <div class="meta" style="flex:2">
              <small style="color:var(--text-muted)">${escapeHtml(l.content || '')}</small>
            </div>
          </div>
        `).join('');
      }

      // Render pagination
      if (totalPages > 1) {
        let paginationHtml = '';
        if (currentPage > 1) {
          paginationHtml += `<button class="mini" onclick="window._autoPageNav(${currentPage - 1})"><i class="fa-solid fa-chevron-left"></i></button>`;
        }
        paginationHtml += `<span style="color:var(--text-muted)">Page ${currentPage} of ${totalPages}</span>`;
        if (currentPage < totalPages) {
          paginationHtml += `<button class="mini" onclick="window._autoPageNav(${currentPage + 1})"><i class="fa-solid fa-chevron-right"></i></button>`;
        }
        paginationEl.innerHTML = paginationHtml;
        paginationEl.style.display = 'flex';
      } else {
        paginationEl.style.display = 'none';
      }
    };

    // Store handler for pagination navigation
    window._autoPageNav = (page) => fetchLogs(page, searchEl.value.trim());

    // Handle WebSocket response - ws.on receives data directly (not event)
    const handleResponse = (data) => {
      if (data) {
        renderLogs(data);
      }
    };

    if (ws) ws.on('AUTOMOD_LOGS_DATA', handleResponse);

    const closeWithCleanup = () => {
      if (ws) ws.off('AUTOMOD_LOGS_DATA', handleResponse);
      delete window._autoPageNav;
      close();
    };

    $('#autoClose', overlay).onclick = closeWithCleanup;
    $('#autoCloseBtn', overlay).onclick = closeWithCleanup;

    searchEl.addEventListener('input', () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => fetchLogs(1, searchEl.value.trim()), 300);
    });

    // Initial fetch
    fetchLogs(1, '');
  };

  // ===== COMMAND BLACKLIST =====
  state.cmdBlacklist = state.cmdBlacklist || [];

  window.filterCmdBlacklist = function() {
    renderCmdBlacklist();
  };

  window.renderCmdBlacklist = function() {
    const container = document.getElementById('cmdblList');
    const countBadge = document.getElementById('cmdblCount');
    const addBtn = document.getElementById('cmdblAddBtn');
    const overlay = document.getElementById('cmdblNoPermissionOverlay');
    const content = document.getElementById('cmdblContent');
    if (!container) return;

    // Check permission for viewing command blacklist
    const hasCmdBlacklistPerm = hasPermission('moderex.cmdblacklist') || hasPermission('moderex.cmdunblacklist');

    if (!hasCmdBlacklistPerm) {
      // Show overlay, hide content
      if (overlay) overlay.style.display = '';
      if (content) content.style.display = 'none';
      if (countBadge) countBadge.textContent = 'No access';
      if (addBtn) {
        addBtn.disabled = true;
        addBtn.title = 'No permission';
      }
      return;
    }

    // Hide overlay, show content
    if (overlay) overlay.style.display = 'none';
    if (content) content.style.display = '';

    // Re-enable add button if permission is granted
    if (addBtn) {
      const canAdd = hasPermission('moderex.cmdblacklist');
      addBtn.disabled = !canAdd;
      addBtn.title = canAdd ? '' : 'You lack permission to add command blacklist entries';
    }

    const search = (document.getElementById('cmdblSearch')?.value || '').toLowerCase();
    const statusFilter = document.getElementById('cmdblStatusFilter')?.value || 'all';
    const now = Date.now();

    let filtered = state.cmdBlacklist.filter(entry => {
      if (search && !entry.playerName?.toLowerCase().includes(search) && !entry.command?.toLowerCase().includes(search)) {
        return false;
      }
      const isActive = entry.expiresAt === -1 || entry.expiresAt > now;
      if (statusFilter === 'active' && !isActive) return false;
      if (statusFilter === 'expired' && isActive) return false;
      return true;
    });

    if (countBadge) {
      countBadge.textContent = `${filtered.length} entr${filtered.length === 1 ? 'y' : 'ies'}`;
    }

    if (filtered.length === 0) {
      container.innerHTML = `
        <div class="empty-state" style="text-align:center;padding:40px;color:var(--text-secondary)">
          <i class="fa-solid fa-ban" style="font-size:48px;opacity:0.3;margin-bottom:16px"></i>
          <p>No command blacklist entries${search ? ' match your search' : ''}</p>
        </div>
      `;
      return;
    }

    container.innerHTML = filtered.map(entry => {
      const isActive = entry.expiresAt === -1 || entry.expiresAt > now;
      const expiresText = entry.expiresAt === -1 ? 'Permanent' : (isActive ? fmtDuration(entry.expiresAt - now) + ' left' : 'Expired');
      const statusBadge = isActive ? '<span class="badge red">Active</span>' : '<span class="badge gray">Expired</span>';

      return `
        <div class="cmdbl-entry ${isActive ? '' : 'expired'}">
          <div class="cmdbl-main">
            <div class="cmdbl-player">
              <img src="https://minotar.net/helm/${escapeHtml(entry.playerName || 'Steve')}/32.png" alt="" class="cmdbl-avatar">
              <div>
                <b>${escapeHtml(entry.playerName || 'Unknown')}</b>
                <small style="color:var(--muted);display:block;font-size:11px">${escapeHtml(entry.playerUuid?.substring(0, 8) || '')}</small>
              </div>
            </div>
            <div class="cmdbl-command">
              <span class="badge purple"><i class="fa-solid fa-terminal"></i> /${escapeHtml(entry.command || 'unknown')}</span>
            </div>
            <div class="cmdbl-info">
              ${statusBadge}
              <span class="badge gray"><i class="fa-solid fa-clock"></i> ${expiresText}</span>
            </div>
            <div class="cmdbl-actions">
              ${isActive ? `<button class="mini bad" onclick="removeCmdBlacklist('${entry.id}')"><i class="fa-solid fa-xmark"></i> Remove</button>` : ''}
            </div>
          </div>
          <div class="cmdbl-meta">
            <small><i class="fa-solid fa-user-shield"></i> ${escapeHtml(entry.staffName || 'Console')} | <i class="fa-solid fa-clock"></i> ${fmtLong(entry.createdAt)} | ${escapeHtml(entry.reason || 'No reason')}</small>
          </div>
        </div>
      `;
    }).join('');
  };

  window.openCmdBlacklistModal = function() {
    if (!hasPermission('moderex.cmdblacklist')) {
      toast('error', 'No Permission', 'You do not have permission to add command blacklist entries.');
      return;
    }

    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show top';
    overlay.style.zIndex = 8000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()" style="max-width:420px">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-ban" style="color:var(--bad)"></i>
            <b>Blacklist Command</b>
          </div>
          <button class="mini" id="cblClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body" style="display:flex;flex-direction:column;gap:12px">
          <div>
            <label style="display:block;margin-bottom:4px;font-size:13px;color:var(--text-secondary)">Player Name</label>
            <input type="text" class="input" id="cblPlayerName" placeholder="Enter player name...">
          </div>
          <div>
            <label style="display:block;margin-bottom:4px;font-size:13px;color:var(--text-secondary)">Command</label>
            <input type="text" class="input" id="cblCommand" placeholder="e.g. home, tpa, spawn">
          </div>
          <div>
            <label style="display:block;margin-bottom:4px;font-size:13px;color:var(--text-secondary)">Duration</label>
            <select class="input" id="cblDuration">
              <option value="-1">Permanent</option>
              <option value="3600000">1 Hour</option>
              <option value="86400000">1 Day</option>
              <option value="604800000">7 Days</option>
              <option value="2592000000">30 Days</option>
            </select>
          </div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> This blacklists a command for a specific player</span>
          <div style="display:flex;gap:10px">
            <button class="btn ghost" id="cblCancel"><i class="fa-solid fa-xmark"></i> Cancel</button>
            <button class="btn bad" id="cblConfirm"><i class="fa-solid fa-ban"></i> Blacklist</button>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    const $ = (sel) => overlay.querySelector(sel);
    $('#cblClose').onclick = () => closeOverlayAnimated(overlay);
    $('#cblCancel').onclick = () => closeOverlayAnimated(overlay);
    $('#cblConfirm').onclick = () => {
      const playerName = $('#cblPlayerName').value.trim();
      const command = $('#cblCommand').value.trim();
      const duration = parseInt($('#cblDuration').value);
      if (!playerName || !command) {
        toast('warn', 'Missing Fields', 'Player name and command are required.');
        return;
      }
      const expiresAt = duration === -1 ? -1 : Date.now() + duration;
      ws.send('ADD_CMD_BLACKLIST_ENTRY', { playerName, command, expiresAt });
      closeOverlayAnimated(overlay);
    };
  };

  window.removeCmdBlacklist = function(id) {
    if (!hasPermission('moderex.cmdunblacklist')) {
      toast('error', 'No Permission', 'You do not have permission to remove command blacklist entries.');
      return;
    }
    openConfirmPanel({
      title: 'Remove Command Blacklist',
      body: 'Are you sure you want to remove this command blacklist entry?',
      confirmText: 'Remove',
      onConfirm: () => {
        ws.send('REMOVE_CMD_BLACKLIST_ENTRY', { id: parseInt(id) });
      }
    });
  };

  // ===== RULES =====
  // State for automod filtering and pagination
  state.rulesPage = state.rulesPage || 1;
  state.rulesPageSize = state.rulesPageSize || 10;

  window.filterRules = function() {
    state.rulesPage = 1; // Reset to page 1 when filtering
    ui.renderRules();
  };

  window.rulesPage = function(delta) {
    const search = (document.getElementById('ruleSearch')?.value || '').toLowerCase();
    const typeFilter = document.getElementById('ruleTypeFilter')?.value || 'all';
    const statusFilter = document.getElementById('ruleStatusFilter')?.value || 'all';

    const filtered = state.rules.filter(r => {
      if (search && !r.name.toLowerCase().includes(search)) return false;
      if (typeFilter !== 'all' && r.type !== typeFilter) return false;
      if (statusFilter === 'enabled' && !r.enabled) return false;
      if (statusFilter === 'disabled' && r.enabled) return false;
      return true;
    });

    const totalPages = Math.max(1, Math.ceil(filtered.length / state.rulesPageSize));
    state.rulesPage = Math.max(1, Math.min(totalPages, state.rulesPage + delta));
    ui.renderRules();
  };

  window.addRuleUI = function() {
    // Check permission - button should be disabled, but double-check here
    if (!hasPermission('moderex.automod.create')) {
      // Don't show toast - button is disabled, this is just a safety check
      return;
    }

    // Show loading bar while creating rule on server
    if (window.showLoadingLine) window.showLoadingLine();
    if (window.debugLog) window.debugLog('DB', 'Creating new automod rule on server...', 'info');

    const ruleName = `New Rule ${state.rules.length + 1}`;

    // Store callback to open editor after creation
    window._pendingNewRuleEditor = true;

    // Create rule on server first
    MX.ws.send('CREATE_AUTOMOD_RULE', {
      name: ruleName,
      exactMatch: false,
      blacklistedWords: [],
      exclusionWords: []
    });
  };

  window.deleteRule = function(ruleId) {
    // Check permission - button should be hidden, but double-check here
    if (!hasPermission('moderex.automod.delete')) {
      // Don't show toast - button is hidden, this is just a safety check
      return;
    }

    const r = state.rules.find(r => r.id === ruleId);
    if (r?.locked) return;
    openConfirmPanel({
      title: 'Delete Rule',
      body: `Delete rule "${r.name}"? This cannot be undone.`,
      confirmText: 'Delete',
      onConfirm: () => {
        if (window.showLoadingLine) window.showLoadingLine();
        if (window.debugLog) window.debugLog('DB', 'Deleting rule ' + ruleId + ' from database...', 'info');
        MX.ws.send('DELETE_AUTOMOD_RULE', { id: ruleId });
        state.rules = state.rules.filter(x => x.id !== ruleId);
        ui.renderRules();
      }
    });
  };

  window.toggleRule = function(ruleId) {
    // Check edit permission - toggle should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) {
      return;
    }
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.enabled = !r.enabled;
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.addCondition = function(ruleId) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.conditions) r.conditions = [];
      r.conditions.push({ kind: 'contains', value: '', match: 'contains' });
      ui.markUnsaved('rules', true);
      ui.renderRules();
    }
  };

  window.removeCondition = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.conditions.splice(idx, 1); ui.markUnsaved('rules', true); ui.renderRules(); }
  };

  window.setConditionKind = function(ruleId, idx, kind) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.conditions[idx].kind = kind;
      if (kind === 'link') r.conditions[idx].value = '';
      if (kind === 'contains') r.conditions[idx].match = r.conditions[idx].match || 'contains';
      if (kind === 'repeat') r.conditions[idx].similar = !!r.conditions[idx].similar;
      ui.markUnsaved('rules', true);
      ui.renderRules();
    }
  };

  window.setConditionValue = function(ruleId, idx, val) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r) { r.conditions[idx].value = val; ui.markUnsaved('rules', true); }
  };

  window.toggleConditionExact = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;
    const c = r.conditions[idx];
    if (!c) return;
    c.match = c.match === 'exact' ? 'contains' : 'exact';
    ui.markUnsaved('rules', true);
    ui.renderRules();
  };

  window.toggleConditionSimilar = function(ruleId, idx) {
    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;
    const c = r.conditions[idx];
    if (!c) return;
    c.similar = !c.similar;
    ui.markUnsaved('rules', true);
    ui.renderRules();
  };

  window.setRuleAction = function(ruleId, kind) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.kind = kind;
      if (kind === 'none') { r.action.extra = ''; r.action.duration = ''; }
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.setRuleActionExtra = function(ruleId, extra) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.extra = extra;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
    }
  };

  window.setRuleActionDuration = function(ruleId, duration) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.action) r.action = {};
      r.action.duration = duration;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
    }
  };

  window.setRuleName = function(ruleId, name) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r && !r.locked) { r.name = name; ui.markUnsaved('rules', true); }
  };

  window.toggleRuleBlock = function(ruleId) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      r.block = !r.block;
      ui.markUnsaved('rules', true);
      autoSaveRule(r);
      ui.renderRules();
    }
  };

  window.setRuleThreshold = function(ruleId, field, v) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;
    const r = state.rules.find(r => r.id === ruleId);
    if (r) {
      if (!r.threshold) r.threshold = {};
      r.threshold[field] = Math.max(1, parseInt(v || '1', 10));
      autoSaveRule(r);
    }
  };

  // Set a specific setting on a rule (for built-in rule config)
  window.setRuleSetting = function(ruleId, setting, value) {
    // Check edit permission - inputs should be disabled, but double-check here
    if (!hasPermission('moderex.automod.edit')) return;

    const r = state.rules.find(r => r.id === ruleId);
    if (!r) return;

    // Parse numeric values
    if (['spamMessageCount', 'spamTimeWindowSeconds', 'capsMaxPercentage', 'capsMinLength', 'afkTimeoutMinutes', 'anticheatAlertThreshold', 'anticheatTimeWindowSeconds'].includes(setting)) {
      r[setting] = Math.max(1, parseInt(value || '1', 10));
    } else if (['spamDetectSimilar', 'afkKickEnabled'].includes(setting)) {
      r[setting] = value === true || value === 'true';
    } else {
      r[setting] = value;
    }

    autoSaveRule(r);
    ui.renderRules();
  };

  // Debounce timers for auto-save per rule
  const autoSaveTimers = {};

  // Auto-save a rule to the server (debounced to prevent rapid-fire updates)
  function autoSaveRule(rule) {
    if (!rule) return;

    console.log('[autoSaveRule] Called for rule:', rule.id, rule.name);

    // Mark as unsaved for visual feedback
    ui.markUnsaved('rules', true);

    // Skip rules with temp IDs (not yet created on server)
    if (rule.id && rule.id.startsWith('rule_')) {
      console.log('[autoSaveRule] Skipping temp rule (not yet on server):', rule.id);
      return;
    }

    // Clear any existing timer for this rule to debounce rapid changes
    if (autoSaveTimers[rule.id]) {
      clearTimeout(autoSaveTimers[rule.id]);
    }

    // Debounce: wait 300ms before actually sending to prevent duplicate saves
    autoSaveTimers[rule.id] = setTimeout(() => {
      delete autoSaveTimers[rule.id];

      // Show loading bar and debug message
      if (window.showLoadingLine) window.showLoadingLine();
      if (window.debugLog) window.debugLog('DB', 'Syncing rule ' + rule.id + ' to database...', 'info');

      // Auto-save all rules that have a server ID
      console.log('[autoSaveRule] Sending UPDATE_AUTOMOD_RULE for:', rule.id);
      MX.ws.send('UPDATE_AUTOMOD_RULE', {
        ruleId: rule.id,
        rule: rule
      });
    }, 300);
  }

  window.setRuleExceptions = function(ruleId, value) {
    const r = state.rules.find(r => r.id === ruleId);
    if (r && !r.locked) {
      // Split by newlines, trim, filter empty - these are word/phrase exceptions
      r.exceptions = value.split('\n').map(s => s.trim()).filter(Boolean);
      ui.markUnsaved('rules', true);
    }
  };

  window.saveRules = function() {
    ui.markUnsaved('rules', true);
    toast('ok', 'Saved', 'Rules saved locally. Publish to apply.');
  };

  window.seedTestMsg = function() {
    dom().testMessage.value = pick(['THIS SERVER IS TERRIBLE!!!!', 'check https://example.com', 'kys lol', 'spam spam spam']);
  };

  window.runRuleTest = function() {
    const msg = dom().testMessage.value;
    if (!msg.trim()) {
      dom().testResult.innerHTML = '<span style="color:var(--muted)">Enter a message to test.</span>';
      return;
    }

    const enabledRules = state.rules.filter(x => x.enabled);
    if (enabledRules.length === 0) {
      dom().testResult.innerHTML = '<span style="color:var(--warn)"><i class="fa-solid fa-triangle-exclamation"></i> No enabled automod rules to test against.</span>';
      return;
    }

    const hits = [];
    const normalizedMsg = normalizeMessage(msg);

    for (const r of enabledRules) {
      let triggered = false;
      let triggerReason = '';

      // Check blacklisted words/phrases (main filter)
      const blacklist = r.blacklistedWords || r.blacklistedPhrases || [];
      if (blacklist.length > 0) {
        for (const word of blacklist) {
          const w = word.toLowerCase().trim();
          if (w && (r.exactMatch ? normalizedMsg === w : normalizedMsg.includes(w))) {
            // Check if in exceptions/whitelist - use word boundary matching for exact phrases
            const exceptions = r.exceptions || r.exclusionWords || r.whitelist || [];
            const isExcepted = exceptions.some(e => {
              const escaped = e.toLowerCase().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
              const pattern = new RegExp('\\b' + escaped + '\\b', 'i');
              return pattern.test(normalizedMsg);
            });
            if (!isExcepted) {
              triggered = true;
              triggerReason = `Blacklisted: "${word}"`;
              break;
            }
          }
        }
      }

      // Check conditions array
      if (!triggered) {
        for (const c of (r.conditions || [])) {
          if (c.kind === 'contains' && c.value) {
            const parts = String(c.value).split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
            const match = parts.find(part => c.match === 'exact' ? normalizedMsg === part : normalizedMsg.includes(part));
            if (match) { triggered = true; triggerReason = `Contains: "${match}"`; break; }
          }
        }
      }

      // Check caps filter (rule type or condition)
      if (!triggered && (r.type === 'CAPS_FILTER' || r.id === 'caps_filter')) {
        const upper = (msg.match(/[A-Z]/g) || []).length;
        const total = msg.replace(/\s/g, '').length;
        const capsMinLen = r.capsMinLength || 5;
        const capsMaxPct = r.capsMaxPercentage || 50;
        if (total >= capsMinLen && (upper / total) * 100 >= capsMaxPct) {
          triggered = true;
          triggerReason = `Caps: ${Math.round((upper / total) * 100)}% (max ${capsMaxPct}%)`;
        }
      }

      // Check link filter
      if (!triggered && (r.type === 'LINK_FILTER' || r.id === 'link_filter')) {
        if (/https?:\/\/|www\./i.test(msg)) {
          triggered = true;
          triggerReason = 'Contains link';
        }
      }

      // Check spam (simplified - would need message history for real check)
      if (!triggered && (r.type === 'SPAM_PROTECTION' || r.id === 'spam_protection')) {
        // Can't fully test spam without message history, show note
        triggerReason = '(Spam detection requires message history)';
      }

      if (triggered) {
        hits.push({ rule: r, reason: triggerReason });
      }
    }

    if (hits.length) {
      const hitsList = hits.map(h => `
        <div class="drawer-row" style="margin-top:8px">
          <div class="meta" style="flex:1">
            <b style="color:var(--bad)">${escapeHtml(h.rule.name)}</b>
            <small>${escapeHtml(h.reason)}</small>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <span class="badge ${h.rule.block !== false ? 'red' : 'yellow'}">${h.rule.block !== false ? 'BLOCKED' : 'FLAGGED'}</span>
            ${h.rule.action?.kind && h.rule.action.kind !== 'none' ? `<span class="badge gray">${escapeHtml(h.rule.action.kind.toUpperCase())}</span>` : ''}
          </div>
        </div>
      `).join('');
      dom().testResult.innerHTML = `
        <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px">
          <span class="badge red"><i class="fa-solid fa-shield-halved"></i> TRIGGERED</span>
          <small style="color:var(--text-muted)">${hits.length} rule${hits.length > 1 ? 's' : ''} matched</small>
        </div>
        ${hitsList}
      `;
    } else {
      dom().testResult.innerHTML = `
        <div style="display:flex;align-items:center;gap:10px">
          <span class="badge green"><i class="fa-solid fa-check"></i> PASSED</span>
          <small style="color:var(--text-muted)">Tested against ${enabledRules.length} enabled rules</small>
        </div>
      `;
    }
  };

  // ===== WATCHLIST =====
  window.toggleWatchToasts = function() {
    state.settings.watchToasts = !state.settings.watchToasts;
    ui.renderWatchToastsToggle();
    saveUserPrefs();

    // Sync with server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { watchlistToasts: state.settings.watchToasts });
    }

    window.MX.sounds?.toggle();
    toast('info', 'Notifications', state.settings.watchToasts ? 'Watchlist toasts enabled.' : 'Watchlist toasts disabled.', {silent: true});
  };

  // Show a watchlist alert (called when watched player does something)
  function watchlistAlert(playerId, title, detail, severity = 'INFO') {
    const player = state.players.find(p => p.id === playerId || p.uuid === playerId);
    const playerName = player?.name || 'Unknown Player';
    const settings = loadMySettings();
    const style = settings.watchlistStyle || 'bar';
    const playerData = { playerId, playerName };

    // Add to alerts list
    state.watchAlerts.unshift({ t: Date.now(), playerId, title, detail, sev: severity });
    if (state.watchAlerts.length > 50) state.watchAlerts.pop();

    // Show alert based on user preference
    if (style === 'bar' || style === 'both') {
      showAlertBar('watchlist', title, `${playerName}: ${detail}`, playerData);
    }
    if (style === 'toast' || style === 'both') {
      toast(severity === 'WARN' ? 'warn' : 'info', title, `${playerName}: ${detail}`, playerData);
    }

    ui.renderDashboard();
  }
  window.watchlistAlert = watchlistAlert;

  window.testWatchlistAlert = function() {
    // Get a test player (first from watchlist or first online player)
    const watchedIds = [...state.watchlist];
    const testPlayer = watchedIds.length > 0
      ? state.players.find(p => p.id === watchedIds[0])
      : state.players.find(p => p.status === 'online') || state.players[0];

    const playerName = testPlayer?.name || 'TestPlayer';
    const playerId = testPlayer?.id || 'test-uuid';

    // Simulate watchlist alert
    const alertTypes = [
      { title: 'Player Joined', detail: `${playerName} joined the server`, sev: 'INFO' },
      { title: 'Suspicious Activity', detail: `${playerName} triggered anticheat alert (Speed)`, sev: 'WARN' },
      { title: 'Chat Violation', detail: `${playerName} flagged for spam`, sev: 'WARN' },
      { title: 'Command Executed', detail: `${playerName} ran /gamemode creative`, sev: 'INFO' }
    ];

    const alert = pick(alertTypes);
    watchlistAlert(playerId, alert.title, alert.detail, alert.sev);
    window.MX.sounds?.notification();
  };

  window.addWatchlistFromInput = function() {
    // Check permission
    if (!hasPermission('moderex.watchlist.add')) return;

    const name = (dom().watchAdd.value || '').trim().toLowerCase();
    if (!name) return;
    const p = state.players.find(x => x.name.toLowerCase().includes(name));
    if (!p) { toast('warn', 'Not Found', 'No player matches.'); return; }
    state.watchlist.add(p.id);

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.addToWatchlist(p.id, p.name, 'Added via web panel');
    }

    dom().watchAdd.value = '';
    toast('ok', 'Added', `${p.name} added to watchlist.`);
    ui.renderWatchlist();
    ui.renderPlayers();
  };

  window.openWatchlistPicker = function() {
    // Check permission - button should be disabled, but double-check here
    if (!hasPermission('moderex.watchlist.add')) return;
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-eye" style="color:var(--warn)"></i>
            <b>Add to Watchlist</b>
          </div>
          <button class="mini" id="wlClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="gsearch" style="width:100%;max-width:520px">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" id="wlSearch" placeholder="Search players...">
          </div>
          <div class="card" style="margin-top:14px" id="wlList"></div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Choose a player</span>
          <button class="btn ghost" id="wlCloseBtn"><i class="fa-solid fa-xmark"></i> Close</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    $('#wlClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#wlCloseBtn', overlay).onclick = () => closeOverlayAnimated(overlay);
    const listEl = $('#wlList', overlay);
    const searchEl = $('#wlSearch', overlay);
    const render = () => {
      const q = (searchEl.value || '').trim().toLowerCase();
      const filtered = state.players.filter(p => !q || p.name.toLowerCase().includes(q)).slice(0, 60);
      listEl.innerHTML = filtered.length ? filtered.map(p => {
        const isWatching = state.watchlist.has(p.uuid) || state.watchlist.has(p.id);
        const uuidDisplay = hasPermission('moderex.info.uuid') ? ` | ${escapeHtml(p.uuid.slice(0, 8))}...` : '';
        return `
        <div class="drawer-row" data-player-id="${p.id}" style="cursor:pointer" onclick="addWatchlistById('${p.id}'); this.closest('.overlay').remove();">
          <div class="meta"><b>${escapeHtml(p.name)}</b><small>${escapeHtml(p.platform)}${uuidDisplay}</small></div>
          <span class="badge ${isWatching ? 'yellow' : 'gray'}"><i class="fa-solid fa-eye"></i> ${isWatching ? 'Watching' : 'Add'}</span>
        </div>
      `;}).join('') : `<div class="drawer-row"><div class="meta"><small>No players found.</small></div></div>`;
    };
    searchEl.addEventListener('input', render);
    render();
  };

  window.addWatchlistById = function(pid) {
    // Check permission
    if (!hasPermission('moderex.watchlist.add')) return;

    const p = state.players.find(x => x.id === pid);
    if (!p) return;
    // Use UUID for consistent storage and server sync
    state.watchlist.add(p.uuid);

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.addToWatchlist(p.uuid, p.name, 'Added via web panel');
    }

    window.MX.sounds?.watchlist();
    toast('ok', 'Added', `${p.name} added to watchlist.`, {silent: true});
    ui.renderWatchlist();
    ui.renderPlayers();
  };

  window.toggleWatchlistSelected = function() {
    const pid = state.selectedPlayerId;
    if (!pid) return;
    const p = state.players.find(x => x.id === pid);
    if (!p) return;
    const ws = window.MX?.ws;
    const isWatching = state.watchlist.has(p.uuid) || state.watchlist.has(pid);

    // Check permissions based on current state
    if (isWatching && !hasPermission('moderex.watchlist.remove')) {
      return; // Can't remove without permission
    }
    if (!isWatching && !hasPermission('moderex.watchlist.add')) {
      return; // Can't add without permission
    }

    window.MX.sounds?.toggle();
    if (isWatching) {
      state.watchlist.delete(p.uuid);
      state.watchlist.delete(pid); // Clean up any legacy internal ID entries
      // Sync to server
      if (ws && ws.isConnected()) {
        ws.removeFromWatchlist(p.uuid);
      }
      toast('info', 'Removed', 'Player removed from watchlist.');
    } else {
      state.watchlist.add(p.uuid);
      // Sync to server
      if (ws && ws.isConnected()) {
        ws.addToWatchlist(p.uuid, p.name, 'Added via web panel');
      }
      toast('ok', 'Added', 'Player added to watchlist.');
    }
    ui.renderWatchlist();
    ui.renderPlayers();
    const nowWatching = state.watchlist.has(p.uuid);
    dom().watchToggleBtn.classList.toggle('on', nowWatching);
    dom().watchToggleBtn.setAttribute('aria-pressed', nowWatching ? 'true' : 'false');

    // Update toggle interactability based on new state
    const canAdd = hasPermission('moderex.watchlist.add');
    const canRemove = hasPermission('moderex.watchlist.remove');
    let canToggle = false;
    let tooltipMsg = '';

    if (canAdd && canRemove) {
      canToggle = true;
    } else if (canAdd && !canRemove) {
      canToggle = !nowWatching; // Can only add
      if (!canToggle) tooltipMsg = 'You lack permission to remove players from watchlist';
    } else if (!canAdd && canRemove) {
      canToggle = nowWatching; // Can only remove
      if (!canToggle) tooltipMsg = 'You lack permission to add players to watchlist';
    } else {
      tooltipMsg = 'You lack permission to manage watchlist';
    }

    dom().watchToggleBtn.disabled = !canToggle;
    dom().watchToggleBtn.classList.toggle('no-permission', !canToggle);
    if (!canToggle && tooltipMsg) {
      dom().watchToggleBtn.setAttribute('title', tooltipMsg);
    } else {
      dom().watchToggleBtn.removeAttribute('title');
    }

    dom().watchToggleHint.textContent = nowWatching ? 'Watching player' : 'Not watching';
  };

  window.removeWatch = function(pid) {
    // Check permission - button should be hidden, but double-check here
    if (!hasPermission('moderex.watchlist.remove')) return;

    const p = state.players.find(x => x.id === pid);
    // Remove both UUID and internal ID to ensure cleanup
    if (p) state.watchlist.delete(p.uuid);
    state.watchlist.delete(pid);

    // Sync to server using UUID
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.removeFromWatchlist(p ? p.uuid : pid);
    }

    ui.renderWatchlist();
    ui.renderPlayers();
    window.MX.sounds?.click();
    toast('info', 'Removed', 'Player removed.');
  };

  window.clearWatchAlerts = function() {
    state.watchAlerts = [];
    ui.renderWatchlist();
    toast('info', 'Cleared', 'Alerts cleared.');
  };

  // ===== ANTICHEAT =====
  window.toggleAnticheatAlert = function(alertId, field) {
    const alert = (state.anticheat?.alerts || []).find(a => a.id === alertId);
    if (!alert) return;
    alert[field] = !alert[field];
    ui.markUnsaved('anticheat', true);
    ui.renderAnticheat();
  };

  window.setAnticheatAlert = function(alertId, field, value) {
    const alert = (state.anticheat?.alerts || []).find(a => a.id === alertId);
    if (!alert) return;
    if (field === 'threshold' || field === 'windowMins') {
      alert[field] = Math.max(1, parseInt(value || '1', 10));
    } else {
      alert[field] = value;
    }
    ui.markUnsaved('anticheat', true);
  };

  window.toggleAnticheatRule = function() {
    state.anticheatRule.enabled = !state.anticheatRule.enabled;
    ui.markUnsaved('anticheat', true);
    ui.renderRules();
  };

  window.setAnticheatRule = function(field, value) {
    if (field === 'threshold' || field === 'windowMins') {
      state.anticheatRule[field] = Math.max(1, parseInt(value || '1', 10));
    } else {
      state.anticheatRule[field] = value;
    }
    ui.markUnsaved('anticheat', true);
  };

  // ===== CONTEXT MENU =====
  let contextMenuEl = null;

  function ensureContextMenu() {
    if (contextMenuEl) return contextMenuEl;
    contextMenuEl = document.createElement('div');
    contextMenuEl.className = 'context-menu';
    contextMenuEl.innerHTML = `
      <div class="context-title" id="contextTitle">Player</div>
      <div class="context-item" id="ctxProfile"><i class="fa-solid fa-id-card-clip"></i> View Profile</div>
      <div class="context-item" id="ctxCopy"><i class="fa-solid fa-copy"></i> Copy Username</div>
      <div class="context-item" id="ctxPunish"><i class="fa-solid fa-gavel"></i> Punish Player</div>
      <div class="context-item" id="ctxWatch"><i class="fa-solid fa-eye"></i> Watchlist</div>
    `;
    document.body.appendChild(contextMenuEl);
    return contextMenuEl;
  }

  function openContextMenu(playerId, x, y) {
    const p = state.players.find(pl => pl.id === playerId);
    if (!p) return;
    const menu = ensureContextMenu();
    const title = menu.querySelector('#contextTitle');
    const watch = menu.querySelector('#ctxWatch');
    title.textContent = p.name;
    watch.textContent = state.watchlist.has(p.id) ? 'Remove from Watchlist' : 'Add to Watchlist';

    menu.classList.add('show');
    const pad = 12;
    const rect = menu.getBoundingClientRect();
    const maxX = window.innerWidth - rect.width - pad;
    const maxY = window.innerHeight - rect.height - pad;
    menu.style.left = `${Math.max(pad, Math.min(x, maxX))}px`;
    menu.style.top = `${Math.max(pad, Math.min(y, maxY))}px`;

    menu.querySelector('#ctxProfile').onclick = () => { openDrawer(p.id); closeContextMenu(); };
    menu.querySelector('#ctxCopy').onclick = () => { copyToClipboard(p.name); toast('info', 'Copied', p.name); closeContextMenu(); };
    menu.querySelector('#ctxPunish').onclick = () => { openPunishModal(null, p.id); closeContextMenu(); };
    menu.querySelector('#ctxWatch').onclick = () => {
      if (state.watchlist.has(p.id)) state.watchlist.delete(p.id);
      else state.watchlist.add(p.id);
      ui.renderWatchlist();
      ui.renderPlayers();
      toast('info', 'Watchlist', state.watchlist.has(p.id) ? 'Player added.' : 'Player removed.');
      closeContextMenu();
    };
  }

  function closeContextMenu() {
    if (!contextMenuEl) return;
    contextMenuEl.classList.remove('show');
  }

  function maybeWatchAlert(playerId, title, detail, sev = 'INFO') {
    if (!state.watchlist.has(playerId)) return;
    state.watchAlerts.unshift({ t: now(), playerId, title, detail, sev });
    ui.renderDashboard();
    ui.renderWatchlist();
    if (state.settings.watchToasts) toast(sev === 'ERROR' ? 'bad' : sev === 'WARN' ? 'warn' : 'info', `Watchlist: ${title}`, detail, { ttl: 7000, playerId });
  }

  // ===== FILTERS =====
  window.togglePunishFilter = function(type) {
    state.punishFilters[type] = !state.punishFilters[type];
    const btn = $(`#filter${type.charAt(0) + type.slice(1).toLowerCase()}`);
    if (btn) btn.classList.toggle('active', state.punishFilters[type]);
    ui.renderPunishments();
  };

  window.toggleSeverity = function(sev) {
    state.logsFilters.sev[sev] = !state.logsFilters.sev[sev];
    dom()[`sev${sev}`]?.classList.toggle('active', state.logsFilters.sev[sev]);
    ui.renderLogs();
  };

  window.toggleMxOnly = function() {
    state.logsFilters.mxOnly = !state.logsFilters.mxOnly;
    dom().mxOnly.classList.toggle('active', state.logsFilters.mxOnly);
    ui.renderLogs();
  };

  window.toggleLogs = function() {
    state.manualPaused = !state.manualPaused;
    ui.renderLogs();
  };

  window.clearLogs = function() {
    state.logs = [];
    ui.renderLogs();
    toast('info', 'Cleared', 'Logs cleared.');
  };

  window.logsPrevPage = function() {
    state.logsFilters.page = Math.max(1, (state.logsFilters.page || 1) - 1);
    ui.renderLogs();
  };

  window.logsNextPage = function() {
    state.logsFilters.page = (state.logsFilters.page || 1) + 1;
    ui.renderLogs();
  };

  window.openLogsFilterPanel = function() {
    if (genericModalEl) genericModalEl.remove();
    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 7000;
    const typeKeys = Object.keys(state.logsFilters.types || {});
    overlay.innerHTML = `
      <div class="modal" onclick="event.stopPropagation()">
        <div class="modal-top">
          <div style="display:flex;align-items:center;gap:10px">
            <i class="fa-solid fa-sliders" style="color:var(--primary-light)"></i>
            <b>Log Filters</b>
          </div>
          <button class="mini" id="lfClose"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="grid cols-2" style="margin-top:0">
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.INFO ? 'on' : ''}" id="lfInfo"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Info</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.WARN ? 'on' : ''}" id="lfWarn"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Warn</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.sev.ERROR ? 'on' : ''}" id="lfErr"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">Error</div></div>
            </div>
            <div class="toggle-wrap">
              <button class="toggle ${state.logsFilters.mxOnly ? 'on' : ''}" id="lfMx"><span class="toggle-thumb"></span></button>
              <div class="toggle-meta"><div class="toggle-title">ModereX Only</div></div>
            </div>
          </div>
          <div class="grid cols-2" style="margin-top:16px">
            ${typeKeys.map(t => `
              <div class="toggle-wrap">
                <button class="toggle ${state.logsFilters.types[t] ? 'on' : ''}" data-type="${t}"><span class="toggle-thumb"></span></button>
                <div class="toggle-meta"><div class="toggle-title">${t.replace('IPBAN','IP-Ban').replace('UNWARN','Unwarn').replace('UNMUTE','Unmute').replace('UNBAN','Unban').replace('EXPIRE','Expires')}</div></div>
              </div>
            `).join('')}
          </div>
        </div>
        <div class="modal-foot">
          <span class="badge gray"><i class="fa-solid fa-circle-info"></i> Saved per account</span>
          <div style="display:flex;gap:10px">
            <button class="btn ghost" id="lfCancel"><i class="fa-solid fa-xmark"></i> Close</button>
            <button class="btn primary" id="lfApply"><i class="fa-solid fa-check"></i> Apply</button>
          </div>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    genericModalEl = overlay;
    const toggleBtn = (btn, key, group) => {
      group[key] = !group[key];
      btn.classList.toggle('on', group[key]);
    };
    $('#lfClose', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#lfCancel', overlay).onclick = () => closeOverlayAnimated(overlay);
    $('#lfInfo', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'INFO', state.logsFilters.sev);
    $('#lfWarn', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'WARN', state.logsFilters.sev);
    $('#lfErr', overlay).onclick = (e) => toggleBtn(e.currentTarget, 'ERROR', state.logsFilters.sev);
    $('#lfMx', overlay).onclick = (e) => { state.logsFilters.mxOnly = !state.logsFilters.mxOnly; e.currentTarget.classList.toggle('on', state.logsFilters.mxOnly); };
    overlay.querySelectorAll('[data-type]').forEach(btn => {
      btn.addEventListener('click', () => {
        const t = btn.dataset.type;
        state.logsFilters.types[t] = !state.logsFilters.types[t];
        btn.classList.toggle('on', state.logsFilters.types[t]);
      });
    });
    $('#lfApply', overlay).onclick = () => { ui.renderLogs(); closeOverlayAnimated(overlay); saveUserPrefs(); };
  };

  // ===== LOGGING =====
  function logEvent(sev, channel, title, detail, meta = {}) {
    const kind = meta.kind || 'event';
    const type = meta.type || (
      kind === 'automod' ? 'AUTOMOD' :
      kind === 'anticheat' ? 'ANTICHEAT' :
      channel === 'anticheat' ? 'ANTICHEAT' :
      channel === 'chat' ? 'CHAT' :
      channel === 'punishment' ? (meta.punType || 'WARN') :
      channel === 'system' ? 'SYSTEM' :
      'SYSTEM'
    );
    state.logs.push({ id: uid('log'), t: now(), sev, channel, title, detail, mx: meta.mx ?? true, playerId: meta.playerId, caseId: meta.caseId, kind, type });
    if (state.logs.length > 300) state.logs.splice(0, state.logs.length - 300);
    ui.renderLogs();
  }

  function logPunishment(playerId, pun) {
    const p = state.players.find(x => x.id === playerId);
    logEvent(pun.type === 'BAN' ? 'ERROR' : 'WARN', 'punishment', `${pun.type} | ${p?.name}`, `${pun.reason} | case ${pun.id.slice(-8)}`, { playerId, caseId: pun.id, kind: 'punishment', punType: pun.type, type: pun.type });
  }

  // ===== ACTIVITY LOG (Database-backed) =====

  /**
   * Fetch activity logs from database with current filters
   */
  window.fetchActivityLogs = function(page = 1) {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      console.warn('[ActivityLog] WebSocket not connected');
      return;
    }

    const pageSize = parseInt(document.getElementById('activityPageSize')?.value || '100', 10);
    const searchInput = document.getElementById('activitySearch')?.value || '';

    // Parse search input for filters
    let playerFilter = '';
    let typeFilter = '';
    let beforeDate = '';
    let afterDate = '';
    const parts = searchInput.split(/\s+/).filter(p => p);
    const freeText = [];

    for (const part of parts) {
      const lowerPart = part.toLowerCase();
      if (lowerPart.startsWith('player:')) {
        playerFilter = part.substring(7);
      } else if (lowerPart.startsWith('type:')) {
        typeFilter = part.substring(5).toUpperCase();
      } else if (lowerPart.startsWith('before:')) {
        beforeDate = part.substring(7);
      } else if (lowerPart.startsWith('after:')) {
        afterDate = part.substring(6);
      } else {
        freeText.push(part);
      }
    }

    // If there's free text without prefix, treat it as player search
    if (freeText.length > 0 && !playerFilter) {
      playerFilter = freeText.join(' ');
    }

    // Validate date formats (YYYY-MM-DD)
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    const validBefore = beforeDate && dateRegex.test(beforeDate) ? beforeDate : null;
    const validAfter = afterDate && dateRegex.test(afterDate) ? afterDate : null;

    // Get enabled types from filter toggles (only include types that are enabled)
    const enabledTypes = state.activityLogs.filters.enabledTypes || {};
    const enabledTypesList = Object.keys(enabledTypes).filter(type => enabledTypes[type] !== false);

    ws.send('GET_ACTIVITY_LOGS', {
      page,
      limit: pageSize,
      player: playerFilter,
      type: typeFilter,
      before: validBefore,
      after: validAfter,
      enabledTypes: enabledTypesList.length > 0 ? enabledTypesList : null
    });
  };

  window.refreshActivityLogs = function() {
    fetchActivityLogs(1);
  };

  window.activityPrevPage = function() {
    const currentPage = state.activityLogs.page || 1;
    if (currentPage > 1) {
      fetchActivityLogs(currentPage - 1);
    }
  };

  window.activityNextPage = function() {
    const currentPage = state.activityLogs.page || 1;
    const totalPages = state.activityLogs.totalPages || 1;
    if (currentPage < totalPages) {
      fetchActivityLogs(currentPage + 1);
    }
  };

  /**
   * Render activity logs to the page
   */
  function renderActivityLogs() {
    const box = document.getElementById('activityLogBox');
    const pageInfo = document.getElementById('activityPageInfo');
    if (!box) return;

    const logs = state.activityLogs.logs || [];
    const total = state.activityLogs.total || 0;
    const page = state.activityLogs.page || 1;
    const totalPages = state.activityLogs.totalPages || 1;

    if (pageInfo) {
      pageInfo.textContent = `${page} / ${totalPages}`;
    }

    if (logs.length === 0) {
      box.innerHTML = `
        <div class="activity-log-item" style="text-align:center;padding:40px;color:var(--text-secondary)">
          <i class="fa-solid fa-inbox" style="font-size:32px;margin-bottom:12px;opacity:0.5"></i>
          <p style="margin:0">No activity logs found</p>
          <p style="margin:8px 0 0 0;font-size:12px">Try adjusting your filters or search criteria</p>
        </div>
      `;
      return;
    }

    box.innerHTML = logs.map(log => {
      const typeClass = getActivityTypeClass(log.type);
      const typeLabel = getActivityTypeLabel(log.type);
      const timestamp = fmtLong(log.timestamp);

      return `
        <div class="activity-log-item" data-player-id="${log.playerUuid}">
          <div class="activity-log-left">
            <b>${escapeHtml(log.playerName || 'Unknown')}</b>
            <small>${escapeHtml(log.content || '')}${log.extra ? ' | ' + escapeHtml(log.extra) : ''}</small>
          </div>
          <div class="activity-log-right">
            <span class="activity-type ${typeClass}">${typeLabel}</span>
            <span style="font-size:11px;color:var(--text-secondary)">${timestamp}</span>
          </div>
        </div>
      `;
    }).join('');

    // Make items clickable to open player drawer
    box.querySelectorAll('.activity-log-item[data-player-id]').forEach(item => {
      item.onclick = () => {
        const uuid = item.dataset.playerId;
        const player = state.players.find(p => p.uuid === uuid || p.id === uuid);
        if (player) {
          openDrawer(player.id);
        }
      };
    });
  }

  function getActivityTypeClass(type) {
    if (!type) return '';
    if (type.includes('CHAT')) return 'chat';
    if (type.includes('COMMAND')) return 'command';
    if (type.includes('BAN') || type.includes('IPBAN')) return 'ban';
    if (type.includes('MUTE') || type.includes('IPMUTE')) return 'mute';
    if (type.includes('WARN')) return 'warn';
    if (type.includes('KICK')) return 'kick';
    if (type.includes('AUTOMOD') || type.includes('ANTICHEAT')) return 'automod';
    if (type.includes('SESSION')) return 'session';
    if (type.includes('NICK') || type.includes('USERNAME')) return 'nick';
    return '';
  }

  function getActivityTypeLabel(type) {
    if (!type) return 'Unknown';
    const labels = {
      'CHAT': 'Chat',
      'COMMAND': 'Command',
      'PUNISHMENT_BAN': 'Ban',
      'PUNISHMENT_UNBAN': 'Unban',
      'PUNISHMENT_IPBAN': 'IP Ban',
      'PUNISHMENT_MUTE': 'Mute',
      'PUNISHMENT_UNMUTE': 'Unmute',
      'PUNISHMENT_IPMUTE': 'IP Mute',
      'PUNISHMENT_WARN': 'Warn',
      'PUNISHMENT_UNWARN': 'Unwarn',
      'PUNISHMENT_KICK': 'Kick',
      'AUTOMOD_TRIGGER': 'Automod',
      'ANTICHEAT_ALERT': 'Anticheat',
      'SESSION_JOIN': 'Join',
      'SESSION_QUIT': 'Leave',
      'NICKNAME_CHANGE': 'Nick',
      'USERNAME_CHANGE': 'Username'
    };
    return labels[type] || type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  /**
   * Show search suggestions dropdown
   */
  function showActivitySearchSuggestions(query) {
    const suggestionBox = document.getElementById('activitySearchSuggestions');
    if (!suggestionBox) return;

    const lowerQuery = query.toLowerCase();
    const suggestions = [];

    // Player filter suggestion
    if (!query.includes('player:') && (lowerQuery.startsWith('p') || lowerQuery.startsWith('pla'))) {
      suggestions.push({
        icon: 'fa-user',
        text: '<b>player:</b>name',
        hint: 'Filter by player name',
        value: 'player:'
      });
    }

    // Type filter suggestions
    if (!query.includes('type:') && (lowerQuery.startsWith('t') || lowerQuery.startsWith('typ'))) {
      suggestions.push({
        icon: 'fa-filter',
        text: '<b>type:</b>chat',
        hint: 'Filter by activity type',
        value: 'type:'
      });
    }

    // Date filter suggestions
    const today = new Date().toISOString().split('T')[0];
    if (!query.includes('before:') && (lowerQuery.startsWith('b') || lowerQuery.startsWith('bef'))) {
      suggestions.push({
        icon: 'fa-calendar-minus',
        text: '<b>before:</b>YYYY-MM-DD',
        hint: 'Show entries before date',
        value: `before:${today}`
      });
    }
    if (!query.includes('after:') && (lowerQuery.startsWith('a') || lowerQuery.startsWith('aft'))) {
      suggestions.push({
        icon: 'fa-calendar-plus',
        text: '<b>after:</b>YYYY-MM-DD',
        hint: 'Show entries after date',
        value: `after:${today}`
      });
    }

    // Type-specific suggestions
    const allowedTypes = state.activityLogs.allowedTypes || [];
    const typeMap = {
      'CHAT': { label: 'chat', icon: 'fa-message' },
      'COMMAND': { label: 'command', icon: 'fa-terminal' },
      'PUNISHMENT_BAN': { label: 'ban', icon: 'fa-hammer' },
      'PUNISHMENT_MUTE': { label: 'mute', icon: 'fa-volume-xmark' },
      'PUNISHMENT_WARN': { label: 'warn', icon: 'fa-triangle-exclamation' },
      'PUNISHMENT_KICK': { label: 'kick', icon: 'fa-door-open' },
      'AUTOMOD_TRIGGER': { label: 'automod', icon: 'fa-robot' },
      'SESSION_JOIN': { label: 'session', icon: 'fa-right-to-bracket' },
      'NICKNAME_CHANGE': { label: 'nick', icon: 'fa-signature' }
    };

    if (query.toLowerCase().startsWith('type:')) {
      const typeQuery = query.substring(5).toLowerCase();
      for (const [type, info] of Object.entries(typeMap)) {
        if (allowedTypes.includes(type) && info.label.includes(typeQuery)) {
          suggestions.push({
            icon: info.icon,
            text: `<b>type:</b>${info.label}`,
            hint: `Show ${info.label} logs`,
            value: `type:${info.label}`
          });
        }
      }
    }

    if (suggestions.length === 0) {
      suggestionBox.style.display = 'none';
      return;
    }

    suggestionBox.innerHTML = suggestions.map(s => `
      <div class="search-suggestion" data-value="${escapeHtml(s.value)}">
        <i class="fa-solid ${s.icon}"></i>
        <div class="suggestion-text">${s.text}</div>
        <div class="suggestion-hint">${s.hint}</div>
      </div>
    `).join('');

    suggestionBox.querySelectorAll('.search-suggestion').forEach(el => {
      el.onclick = () => {
        const searchInput = document.getElementById('activitySearch');
        if (searchInput) {
          // Replace or append the filter
          const currentValue = searchInput.value;
          const filterPrefix = el.dataset.value.split(':')[0] + ':';
          if (currentValue.includes(filterPrefix)) {
            searchInput.value = currentValue.replace(new RegExp(filterPrefix + '\\S*'), el.dataset.value);
          } else {
            searchInput.value = (currentValue + ' ' + el.dataset.value).trim();
          }
          searchInput.focus();
        }
        suggestionBox.style.display = 'none';
      };
    });

    suggestionBox.style.display = 'block';
  }

  /**
   * Update filter highlighting overlay for activity search
   * Shows recognized filter keywords in blue, invalid dates in red
   */
  function updateActivitySearchHighlight(inputEl) {
    if (!inputEl) return;

    const container = inputEl.closest('.activity-search');
    if (!container) return;

    let overlay = container.querySelector('.filter-highlight-overlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.className = 'filter-highlight-overlay';
      container.appendChild(overlay);
    }

    const value = inputEl.value;
    if (!value) {
      overlay.innerHTML = '';
      inputEl.classList.remove('has-filters');
      return;
    }

    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    const validFilters = ['player:', 'type:', 'before:', 'after:'];
    const parts = value.split(/(\s+)/); // Keep spaces
    let hasFilters = false;
    let highlighted = '';

    for (const part of parts) {
      if (/^\s+$/.test(part)) {
        highlighted += part;
        continue;
      }

      const lowerPart = part.toLowerCase();
      let matched = false;

      for (const filter of validFilters) {
        if (lowerPart.startsWith(filter)) {
          hasFilters = true;
          const keyword = part.substring(0, filter.length);
          const filterValue = part.substring(filter.length);

          // Check if date filters have valid format
          if ((filter === 'before:' || filter === 'after:') && filterValue) {
            if (dateRegex.test(filterValue)) {
              highlighted += `<span class="filter-keyword">${escapeHtml(keyword)}</span><span class="filter-value">${escapeHtml(filterValue)}</span>`;
            } else {
              highlighted += `<span class="filter-keyword">${escapeHtml(keyword)}</span><span class="filter-invalid">${escapeHtml(filterValue)}</span>`;
            }
          } else {
            highlighted += `<span class="filter-keyword">${escapeHtml(keyword)}</span><span class="filter-value">${escapeHtml(filterValue)}</span>`;
          }
          matched = true;
          break;
        }
      }

      if (!matched) {
        highlighted += `<span class="filter-value">${escapeHtml(part)}</span>`;
      }
    }

    overlay.innerHTML = highlighted;
    if (hasFilters) {
      inputEl.classList.add('has-filters');
    } else {
      inputEl.classList.remove('has-filters');
    }
  }

  /**
   * Open activity filter panel
   */
  window.openActivityFilterPanel = function() {
    const allowedTypes = state.activityLogs.allowedTypes || [];
    if (allowedTypes.length === 0) {
      toast('info', 'No Permissions', 'You do not have permission to view any activity types');
      return;
    }

    const enabledTypes = state.activityLogs.filters.enabledTypes || {};

    // Initialize all allowed types as enabled if not set
    for (const type of allowedTypes) {
      if (enabledTypes[type] === undefined) {
        enabledTypes[type] = true;
      }
    }

    const typeLabels = {
      'CHAT': { label: 'Chat', icon: 'fa-message' },
      'COMMAND': { label: 'Commands', icon: 'fa-terminal' },
      'PUNISHMENT_BAN': { label: 'Bans', icon: 'fa-hammer' },
      'PUNISHMENT_UNBAN': { label: 'Unbans', icon: 'fa-unlock' },
      'PUNISHMENT_MUTE': { label: 'Mutes', icon: 'fa-volume-xmark' },
      'PUNISHMENT_UNMUTE': { label: 'Unmutes', icon: 'fa-volume-high' },
      'PUNISHMENT_WARN': { label: 'Warns', icon: 'fa-triangle-exclamation' },
      'PUNISHMENT_UNWARN': { label: 'Unwarns', icon: 'fa-check' },
      'PUNISHMENT_KICK': { label: 'Kicks', icon: 'fa-door-open' },
      'AUTOMOD_TRIGGER': { label: 'Automod', icon: 'fa-robot' },
      'ANTICHEAT_ALERT': { label: 'Anticheat', icon: 'fa-shield' },
      'SESSION_JOIN': { label: 'Joins', icon: 'fa-right-to-bracket' },
      'SESSION_QUIT': { label: 'Leaves', icon: 'fa-right-from-bracket' },
      'NICKNAME_CHANGE': { label: 'Nicknames', icon: 'fa-signature' },
      'USERNAME_CHANGE': { label: 'Usernames', icon: 'fa-user-pen' }
    };

    const overlay = document.createElement('div');
    overlay.className = 'overlay show';
    overlay.style.zIndex = 5000;
    overlay.innerHTML = `
      <div class="modal" style="max-width:500px" onclick="event.stopPropagation()">
        <div class="modal-top">
          <b><i class="fa-solid fa-sliders" style="margin-right:8px"></i>Activity Filters</b>
          <button class="mini" onclick="this.closest('.overlay').remove()"><i class="fa-solid fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <p style="margin-bottom:16px;color:var(--text-secondary);font-size:13px">
            Toggle which activity types to show. Only types you have permission to view are listed.
          </p>
          <div class="grid cols-2" style="gap:12px">
            ${allowedTypes.map(type => {
              const info = typeLabels[type] || { label: type, icon: 'fa-circle' };
              const checked = enabledTypes[type] !== false;
              return `
                <label class="toggle-wrap" style="cursor:pointer">
                  <button class="toggle ${checked ? 'on' : ''}" data-type="${type}" onclick="this.classList.toggle('on')">
                    <span class="toggle-thumb"></span>
                  </button>
                  <div class="toggle-meta">
                    <div class="toggle-title"><i class="fa-solid ${info.icon}" style="margin-right:6px;opacity:0.7"></i>${info.label}</div>
                  </div>
                </label>
              `;
            }).join('')}
          </div>
        </div>
        <div class="modal-foot">
          <button class="btn ghost" onclick="this.closest('.overlay').remove()"><i class="fa-solid fa-xmark"></i> Close</button>
          <button class="btn primary" id="activityFilterApply"><i class="fa-solid fa-check"></i> Apply</button>
        </div>
      </div>
    `;

    overlay.onclick = () => overlay.remove();
    document.body.appendChild(overlay);

    document.getElementById('activityFilterApply').onclick = () => {
      // Save filter state
      overlay.querySelectorAll('.toggle[data-type]').forEach(toggle => {
        const type = toggle.dataset.type;
        state.activityLogs.filters.enabledTypes[type] = toggle.classList.contains('on');
      });
      overlay.remove();
      fetchActivityLogs(1);
    };
  };

  // ===== SETTINGS =====
  window.toggleSetting = function(key) {
    const ws = window.MX?.ws;
    state.settings[key] = !state.settings[key];

    // Immediately send to server for action settings
    if (ws && ws.isConnected()) {
      if (key === 'chatDisabled') {
        ws.setChatLock(state.settings.chatDisabled);
        window.MX.sounds?.toggle();
        toast('ok', state.settings.chatDisabled ? 'Chat Locked' : 'Chat Unlocked',
          state.settings.chatDisabled ? 'Chat is now disabled for non-staff' : 'Chat is now enabled', {silent: true});
      } else if (key === 'slowEnabled') {
        const seconds = state.settings.slowEnabled ? (parseInt(dom().slowSeconds?.value || '3', 10)) : 0;
        ws.setSlowmode(seconds);
        window.MX.sounds?.toggle();
        toast('ok', state.settings.slowEnabled ? 'Slowmode Enabled' : 'Slowmode Disabled',
          state.settings.slowEnabled ? `Players must wait ${seconds}s between messages` : 'Players can chat freely', {silent: true});
      } else if (key.startsWith('mute')) {
        // Mute settings - send to server
        const muteKey = key.replace('mute', '').toLowerCase();
        const muteKeyMap = { chat: 'chat', msg: 'msg', signs: 'signs', books: 'books', broadcast: 'broadcast', voice: 'voice', voicejoin: 'voiceJoin' };
        const serverKey = muteKeyMap[muteKey] || muteKey;
        ws.updateMuteSettings({ [serverKey]: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Mute Setting Updated', `${key} is now ${state.settings[key] ? 'blocked' : 'allowed'}`, { silent: true });
      } else if (key === 'warnNotify' || key === 'warnAutoEscalate') {
        // Warn settings - send to server
        const warnKeyMap = { warnNotify: 'notify', warnAutoEscalate: 'autoEscalate' };
        ws.updateWarnSettings({ [warnKeyMap[key]]: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Warn Setting Updated', `${key} is now ${state.settings[key] ? 'enabled' : 'disabled'}`, { silent: true });
      } else if (key === 'anticheatReplace') {
        // Anticheat settings - send to server
        ws.updateAnticheatSettings({ rebrandAlerts: state.settings[key] });
        window.MX.sounds?.toggle();
        toast('ok', 'Anticheat Setting Updated', `Alert rebranding is now ${state.settings[key] ? 'enabled' : 'disabled'}`, { silent: true });
      } else {
        // For other settings, mark as unsaved
        ui.markUnsaved('settings', true);
      }
    } else {
      ui.markUnsaved('settings', true);
    }

    ui.renderChatToggles();
    ui.renderIntegrations();
    ui.renderAnticheat();
  };

  // Update staff notification settings (synced with in-game)
  window.updateStaffSetting = function(key, value) {
    const ws = window.MX?.ws;
    state.staffSettings = state.staffSettings || {};
    state.staffSettings[key] = value;
    console.log('[updateStaffSetting] Set', key, '=', value, 'state.staffSettings:', JSON.stringify(state.staffSettings));

    // Send to server immediately
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { [key]: value });
      window.debugLog('SETTINGS', `Updated setting: ${key} = ${value}`, 'info');
      toast('ok', 'Setting Saved', `${key} updated`, { silent: true });
    }

    ui.renderStaffSettings();
  };

  // ===== ANTICHEAT ALERT CUSTOMIZATION =====
  window.refreshAnticheatData = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      // Request anticheat alerts (detected checks)
      ws.requestAnticheatAlerts();
      // Request saved staff alert preferences from database
      ws.requestStaffAlertPrefs();
      // Request alert presets
      ws.requestAlertPresets();
      window.debugLog('ANTICHEAT', 'Refreshing anticheat data (alerts, prefs, presets)...', 'info');
      toast('info', 'Refreshing', 'Loading anticheat data...');
    } else {
      toast('warn', 'Not Connected', 'Cannot refresh - not connected to server.');
    }
  };

  window.applyPreset = function(presetId) {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.applyAlertPreset(presetId);
      toast('ok', 'Applied', `Preset "${presetId}" applied to all checks.`);
    } else {
      toast('warn', 'Not Connected', 'Cannot apply preset - not connected to server.');
    }
  };

  window.applySelectedPreset = function() {
    const select = document.getElementById('acPresetSelect');
    if (select && select.value) {
      window.applyPreset(select.value);
      select.value = '';
    }
  };

  window.updateCheckAlertLevel = function(anticheat, checkName, alertLevel) {
    const ws = window.MX?.ws;
    const prefKey = `${anticheat.toLowerCase()}.${checkName}`;
    const currentPref = state.anticheat.alertPrefs[prefKey] || { thresholdCount: 1, timeWindowSeconds: 60 };

    if (ws && ws.isConnected()) {
      window.debugLog('ANTICHEAT', `Updating ${anticheat}:${checkName} level -> ${alertLevel}`, 'info');
      ws.updateStaffAlertPref(anticheat, checkName, alertLevel, currentPref.thresholdCount, currentPref.timeWindowSeconds);
      // Update local state optimistically
      state.anticheat.alertPrefs[prefKey] = {
        ...currentPref,
        alertLevel: alertLevel
      };
      ui.renderAnticheat();
    } else {
      toast('warn', 'Not Connected', 'Cannot update - not connected to server.');
      window.debugLog('ANTICHEAT', 'Cannot update - not connected', 'error');
    }
  };

  window.updateCheckThreshold = function(anticheat, checkName, thresholdCount, timeWindowSeconds) {
    const ws = window.MX?.ws;
    const prefKey = `${anticheat.toLowerCase()}.${checkName}`;
    const currentPref = state.anticheat.alertPrefs[prefKey] || { alertLevel: 'EVERYONE' };
    const newThreshold = parseInt(thresholdCount, 10) || 1;
    const newWindow = parseInt(timeWindowSeconds, 10) || 60;

    if (ws && ws.isConnected()) {
      window.debugLog('ANTICHEAT', `Updating ${anticheat}:${checkName} threshold -> ${newThreshold} in ${newWindow}s`, 'info');
      ws.updateStaffAlertPref(
        anticheat,
        checkName,
        currentPref.alertLevel,
        newThreshold,
        newWindow
      );
      // Update local state optimistically
      state.anticheat.alertPrefs[prefKey] = {
        ...currentPref,
        thresholdCount: newThreshold,
        timeWindowSeconds: newWindow
      };
    } else {
      toast('warn', 'Not Connected', 'Cannot update - not connected to server.');
      window.debugLog('ANTICHEAT', 'Cannot update - not connected', 'error');
    }
  };

  window.filterAnticheatChecks = function() {
    ui.renderAnticheat();
  };

  window.saveChatSettings = function() {
    const ws = window.MX?.ws;
    const seconds = parseInt(dom().slowSeconds.value || '0', 10);
    state.settings.slowSeconds = seconds;

    if (ws && ws.isConnected() && state.settings.slowEnabled) {
      ws.setSlowmode(seconds);
      toast('ok', 'Saved', `Slowmode set to ${seconds} seconds.`);
    } else {
      ui.markUnsaved('chat', true);
      toast('ok', 'Saved', 'Chat settings saved. Publish to apply.');
    }
  };

  window.clearChatNow = function() {
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.clearChat();
      window.MX.sounds?.success();
      toast('ok', 'Cleared', 'Chat cleared on server.', {silent: true});
    } else {
      toast('warn', 'Not Connected', 'Cannot clear chat - not connected to server.');
    }
    logEvent('WARN', 'chat', 'Chat cleared', 'Chat cleared via panel.');
  };

  // ===== KICK ALL PLAYERS (with 10-second countdown) =====
  let kickAllCountdownTimer = null;
  let kickAllCountdownRemaining = 0;

  window.kickAllPlayers = function() {
    const ws = window.MX?.ws;
    const reason = document.getElementById('kickAllReason')?.value || 'Server maintenance';

    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot kick players - not connected to server.');
      return;
    }

    // Show countdown popup
    let overlay = document.getElementById('kickAllCountdownOverlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'kickAllCountdownOverlay';
      overlay.className = 'overlay';
      overlay.innerHTML = `
        <div class="card" style="max-width:420px;margin:auto;margin-top:20vh;padding:32px;text-align:center" onclick="event.stopPropagation()">
          <i class="fa-solid fa-people-group" style="font-size:40px;color:var(--bad);margin-bottom:16px"></i>
          <h2 style="margin-bottom:8px">Kick All Players</h2>
          <p style="color:var(--muted);margin-bottom:16px" id="kickAllCountdownReason"></p>
          <div id="kickAllCountdownDisplay" style="font-size:48px;font-weight:bold;color:var(--bad);margin:16px 0"></div>
          <p style="color:var(--muted);font-size:12px;margin-bottom:20px">All players will be kicked when countdown reaches 0</p>
          <button class="btn ghost" onclick="cancelKickAll()" style="width:100%"><i class="fa-solid fa-xmark"></i> Cancel</button>
        </div>
      `;
      document.body.appendChild(overlay);
    }

    document.getElementById('kickAllCountdownReason').textContent = `Reason: "${reason}"`;
    overlay.classList.add('show');
    kickAllCountdownRemaining = 10;
    document.getElementById('kickAllCountdownDisplay').textContent = kickAllCountdownRemaining;

    // Notify server to warn players in chat
    ws.send('KICK_ALL_COUNTDOWN', { reason, seconds: 10 });

    // Start countdown
    clearInterval(kickAllCountdownTimer);
    kickAllCountdownTimer = setInterval(() => {
      kickAllCountdownRemaining--;
      const display = document.getElementById('kickAllCountdownDisplay');
      if (display) display.textContent = kickAllCountdownRemaining;

      if (kickAllCountdownRemaining <= 0) {
        clearInterval(kickAllCountdownTimer);
        kickAllCountdownTimer = null;
        overlay.classList.remove('show');

        // Execute kick
        ws.send('KICK_ALL_PLAYERS', { reason });
        window.MX.sounds?.success();
        toast('ok', 'Kicked', 'All players have been kicked from the server.', {silent: true});
        logEvent('WARN', 'action', 'Kick All Players', `All players kicked: ${reason}`);
      }
    }, 1000);
  };

  window.cancelKickAll = function() {
    clearInterval(kickAllCountdownTimer);
    kickAllCountdownTimer = null;
    const overlay = document.getElementById('kickAllCountdownOverlay');
    if (overlay) overlay.classList.remove('show');
    // Notify server to cancel countdown chat messages
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('KICK_ALL_CANCEL', {});
    }
    toast('info', 'Cancelled', 'Kick all cancelled.');
  };

  // ===== WARNING ESCALATION SETTINGS =====
  let warnCategories = [];
  let warnEscalationTiers = [];

  window.toggleWarnEscalation = function() {
    const toggle = document.getElementById('togWarnEscalation');
    const optionsDiv = document.getElementById('warnEscalationOptions');
    const isEnabled = toggle?.classList.contains('on');

    if (isEnabled) {
      toggle.classList.remove('on');
      document.getElementById('togWarnEscalationHint').textContent = 'Off';
      optionsDiv.style.display = 'none';
    } else {
      toggle.classList.add('on');
      document.getElementById('togWarnEscalationHint').textContent = 'On';
      optionsDiv.style.display = 'block';
    }
    window.MX.sounds?.toggle();
  };

  window.addWarnCategory = function() {
    const id = 'cat_' + Date.now();
    warnCategories.push({ id, name: '', points: 1 });
    renderWarnCategories();
  };

  window.removeWarnCategory = function(id) {
    warnCategories = warnCategories.filter(c => c.id !== id);
    renderWarnCategories();
  };

  window.updateWarnCategory = function(id, field, value) {
    const cat = warnCategories.find(c => c.id === id);
    if (cat) {
      cat[field] = field === 'points' ? parseInt(value, 10) || 1 : value;
    }
  };

  function renderWarnCategories() {
    const container = document.getElementById('warnCategoriesList');
    if (!container) return;

    if (warnCategories.length === 0) {
      container.innerHTML = '<div style="color:var(--muted);font-size:13px;padding:10px">No categories defined. Add a category to get started.</div>';
      return;
    }

    container.innerHTML = warnCategories.map(cat => `
      <div class="grid cols-3" style="gap:10px;align-items:center;padding:10px;background:rgba(0,0,0,0.2);border-radius:var(--radius-sm)">
        <input type="text" class="input" placeholder="Category name (e.g., Minor)" value="${escapeHtml(cat.name)}"
          onchange="updateWarnCategory('${cat.id}', 'name', this.value)" style="width:100%">
        <div style="display:flex;align-items:center;gap:8px">
          <span style="font-size:13px;color:var(--text-secondary)">Points:</span>
          <input type="number" class="input" value="${cat.points}" min="1" max="100"
            onchange="updateWarnCategory('${cat.id}', 'points', this.value)" style="width:80px">
        </div>
        <button class="btn ghost bad" onclick="removeWarnCategory('${cat.id}')" style="width:auto;padding:6px 12px">
          <i class="fa-solid fa-trash"></i>
        </button>
      </div>
    `).join('');
  }

  window.addWarnEscalationTier = function() {
    const id = 'tier_' + Date.now();
    warnEscalationTiers.push({ id, pointThreshold: 3, punishmentType: 'MUTE', duration: '1h', reason: 'Accumulated warning points' });
    renderWarnEscalationTiers();
  };

  window.removeWarnEscalationTier = function(id) {
    warnEscalationTiers = warnEscalationTiers.filter(t => t.id !== id);
    renderWarnEscalationTiers();
  };

  window.updateWarnEscalationTier = function(id, field, value) {
    const tier = warnEscalationTiers.find(t => t.id === id);
    if (tier) {
      tier[field] = field === 'pointThreshold' ? parseInt(value, 10) || 1 : value;
    }
  };

  function renderWarnEscalationTiers() {
    const container = document.getElementById('warnEscalationTiersList');
    if (!container) return;

    if (warnEscalationTiers.length === 0) {
      container.innerHTML = '<div style="color:var(--muted);font-size:13px;padding:10px">No escalation tiers defined. Add a tier to configure automatic punishments.</div>';
      return;
    }

    container.innerHTML = warnEscalationTiers.map(tier => `
      <div style="padding:12px;background:rgba(0,0,0,0.2);border-radius:var(--radius-sm)">
        <div class="grid cols-4" style="gap:10px;align-items:center">
          <div>
            <div class="hintline" style="margin:0 0 4px 0;font-size:11px">Point Threshold</div>
            <input type="number" class="input" value="${tier.pointThreshold}" min="1" max="1000"
              onchange="updateWarnEscalationTier('${tier.id}', 'pointThreshold', this.value)" style="width:100%">
          </div>
          <div>
            <div class="hintline" style="margin:0 0 4px 0;font-size:11px">Punishment</div>
            <select class="input" onchange="updateWarnEscalationTier('${tier.id}', 'punishmentType', this.value)" style="width:100%">
              <option value="MUTE" ${tier.punishmentType === 'MUTE' ? 'selected' : ''}>Mute</option>
              <option value="KICK" ${tier.punishmentType === 'KICK' ? 'selected' : ''}>Kick</option>
              <option value="BAN" ${tier.punishmentType === 'BAN' ? 'selected' : ''}>Ban</option>
            </select>
          </div>
          <div>
            <div class="hintline" style="margin:0 0 4px 0;font-size:11px">Duration</div>
            <input type="text" class="input" placeholder="1h, 1d, 7d, permanent" value="${escapeHtml(tier.duration)}"
              onchange="updateWarnEscalationTier('${tier.id}', 'duration', this.value)" style="width:100%">
          </div>
          <button class="btn ghost bad" onclick="removeWarnEscalationTier('${tier.id}')" style="width:auto;padding:6px 12px;align-self:end">
            <i class="fa-solid fa-trash"></i>
          </button>
        </div>
        <div style="margin-top:10px">
          <div class="hintline" style="margin:0 0 4px 0;font-size:11px">Punishment Reason</div>
          <input type="text" class="input" placeholder="Reason for automatic punishment" value="${escapeHtml(tier.reason)}"
            onchange="updateWarnEscalationTier('${tier.id}', 'reason', this.value)" style="width:100%">
        </div>
      </div>
    `).join('');
  }

  window.saveWarnSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save settings - not connected to server.');
      return;
    }

    const escalationEnabled = document.getElementById('togWarnEscalation')?.classList.contains('on') || false;
    const escalationWindowDays = parseInt(document.getElementById('warnEscalationWindow')?.value || '30', 10);
    const resetDays = parseInt(document.getElementById('warnResetDays')?.value || '90', 10);

    ws.send('UPDATE_WARN_SETTINGS', {
      escalationEnabled,
      escalationWindowDays,
      resetDays,
      categories: warnCategories.map(c => ({ id: c.id, name: c.name, points: c.points })),
      escalationTiers: warnEscalationTiers.map(t => ({
        pointThreshold: t.pointThreshold,
        punishmentType: t.punishmentType,
        duration: t.duration,
        reason: t.reason
      }))
    });

    window.MX.sounds?.success();
    toast('ok', 'Saved', 'Warning escalation settings saved.', {silent: true});
  };

  function loadWarnSettings(data) {
    if (!data) return;

    const toggle = document.getElementById('togWarnEscalation');
    const optionsDiv = document.getElementById('warnEscalationOptions');

    if (data.escalationEnabled) {
      toggle?.classList.add('on');
      document.getElementById('togWarnEscalationHint').textContent = 'On';
      if (optionsDiv) optionsDiv.style.display = 'block';
    } else {
      toggle?.classList.remove('on');
      document.getElementById('togWarnEscalationHint').textContent = 'Off';
      if (optionsDiv) optionsDiv.style.display = 'none';
    }

    if (data.escalationWindowDays) document.getElementById('warnEscalationWindow').value = data.escalationWindowDays;
    if (data.resetDays) document.getElementById('warnResetDays').value = data.resetDays;

    if (data.categories && data.categories.length > 0) {
      warnCategories = data.categories.map(c => ({ id: c.id || 'cat_' + Date.now(), name: c.name, points: c.points }));
      renderWarnCategories();
    }

    if (data.escalationTiers && data.escalationTiers.length > 0) {
      warnEscalationTiers = data.escalationTiers.map((t, i) => ({
        id: 'tier_' + i,
        pointThreshold: t.pointThreshold,
        punishmentType: t.punishmentType,
        duration: t.duration,
        reason: t.reason
      }));
      renderWarnEscalationTiers();
    }
  }

  // ===== MUTE SETTINGS =====
  window.toggleMuteSetting = function(setting) {
    const toggleId = 'togMute' + setting.charAt(0).toUpperCase() + setting.slice(1);
    const toggle = document.getElementById(toggleId);
    if (!toggle) return;

    const isEnabled = toggle.classList.contains('on');
    if (isEnabled) {
      toggle.classList.remove('on');
    } else {
      toggle.classList.add('on');
    }
    window.MX.sounds?.toggle();
  };

  window.saveMuteSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save settings - not connected to server.');
      return;
    }

    ws.send('UPDATE_MUTE_SETTINGS', {
      blocksChat: document.getElementById('togMuteChat')?.classList.contains('on') || false,
      blocksMsg: document.getElementById('togMuteMsg')?.classList.contains('on') || false,
      blocksSigns: document.getElementById('togMuteSigns')?.classList.contains('on') || false,
      blocksBooks: document.getElementById('togMuteBooks')?.classList.contains('on') || false,
      blocksBroadcast: document.getElementById('togMuteBroadcast')?.classList.contains('on') || false,
      blocksVoice: document.getElementById('togMuteVoice')?.classList.contains('on') || false,
      staffCanSee: document.getElementById('togMuteStaffSee')?.classList.contains('on') || false
    });

    window.MX.sounds?.success();
    toast('ok', 'Saved', 'Mute restriction settings saved.', {silent: true});
  };

  function loadMuteSettings(data) {
    if (!data) return;

    // Backend sends short keys: chat, msg, signs, books, broadcast, voice, voiceJoin, staffCanSee
    const settings = [
      { id: 'togMuteChat', key: 'chat' },
      { id: 'togMuteMsg', key: 'msg' },
      { id: 'togMuteSigns', key: 'signs' },
      { id: 'togMuteBooks', key: 'books' },
      { id: 'togMuteBroadcast', key: 'broadcast' },
      { id: 'togMuteVoice', key: 'voice' },
      { id: 'togMuteStaffSee', key: 'staffCanSee' }
    ];

    for (const s of settings) {
      const toggle = document.getElementById(s.id);
      if (toggle && data[s.key] !== undefined) {
        if (data[s.key]) {
          toggle.classList.add('on');
        } else {
          toggle.classList.remove('on');
        }
      }
    }
  }

  // ===== ACTIVITY LOG CONFIGURATION =====
  window.toggleActivityLogEnabled = function() {
    const toggle = document.getElementById('togActivityLogEnabled');
    const optionsDiv = document.getElementById('activityLogOptions');
    const isEnabled = toggle?.classList.contains('on');

    if (isEnabled) {
      toggle.classList.remove('on');
      document.getElementById('togActivityLogEnabledHint').textContent = 'Off';
      if (optionsDiv) optionsDiv.style.display = 'none';
    } else {
      toggle.classList.add('on');
      document.getElementById('togActivityLogEnabledHint').textContent = 'On';
      if (optionsDiv) optionsDiv.style.display = 'block';
    }
    window.MX.sounds?.toggle();
  };

  window.toggleActivityLogType = function(type) {
    const toggleId = 'togLog' + type.charAt(0).toUpperCase() + type.slice(1);
    const toggle = document.getElementById(toggleId);
    if (!toggle) return;

    if (toggle.classList.contains('on')) {
      toggle.classList.remove('on');
    } else {
      toggle.classList.add('on');
    }
    window.MX.sounds?.toggle();
  };

  window.saveActivityLogSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save settings - not connected to server.');
      return;
    }

    ws.send('UPDATE_ACTIVITY_LOG_SETTINGS', {
      enabled: document.getElementById('togActivityLogEnabled')?.classList.contains('on') || false,
      logChat: document.getElementById('togLogChat')?.classList.contains('on') || false,
      logCommands: document.getElementById('togLogCommands')?.classList.contains('on') || false,
      logSigns: document.getElementById('togLogSigns')?.classList.contains('on') || false,
      logItems: document.getElementById('togLogItems')?.classList.contains('on') || false,
      logAnvils: document.getElementById('togLogAnvils')?.classList.contains('on') || false,
      logSessions: document.getElementById('togLogSessions')?.classList.contains('on') || false,
      logUsernames: document.getElementById('togLogUsernames')?.classList.contains('on') || false,
      retentionChat: parseInt(document.getElementById('retentionChat')?.value || '30', 10),
      retentionCommands: parseInt(document.getElementById('retentionCommands')?.value || '30', 10),
      retentionSigns: parseInt(document.getElementById('retentionSigns')?.value || '30', 10),
      retentionSessions: parseInt(document.getElementById('retentionSessions')?.value || '30', 10),
      retentionItems: parseInt(document.getElementById('retentionItems')?.value || '30', 10),
      retentionAnvils: parseInt(document.getElementById('retentionAnvils')?.value || '30', 10),
      retentionUsernames: parseInt(document.getElementById('retentionUsernames')?.value || '-1', 10),
      retentionAutomod: parseInt(document.getElementById('retentionAutomod')?.value || '30', 10),
      retentionAnticheat: parseInt(document.getElementById('retentionAnticheat')?.value || '30', 10)
    });

    window.MX.sounds?.success();
    toast('ok', 'Saved', 'Activity log settings saved.', {silent: true});
  };

  function loadActivityLogSettings(data) {
    if (!data) return;

    // Master toggle
    const toggle = document.getElementById('togActivityLogEnabled');
    const optionsDiv = document.getElementById('activityLogOptions');
    if (data.enabled) {
      toggle?.classList.add('on');
      document.getElementById('togActivityLogEnabledHint').textContent = 'On';
      if (optionsDiv) optionsDiv.style.display = 'block';
    } else {
      toggle?.classList.remove('on');
      document.getElementById('togActivityLogEnabledHint').textContent = 'Off';
      if (optionsDiv) optionsDiv.style.display = 'none';
    }

    // Log type toggles
    const logTypes = ['chat', 'commands', 'signs', 'items', 'anvils', 'sessions', 'usernames'];
    for (const type of logTypes) {
      const toggleId = 'togLog' + type.charAt(0).toUpperCase() + type.slice(1);
      const typeToggle = document.getElementById(toggleId);
      const key = 'log' + type.charAt(0).toUpperCase() + type.slice(1);
      if (typeToggle && data[key] !== undefined) {
        if (data[key]) {
          typeToggle.classList.add('on');
        } else {
          typeToggle.classList.remove('on');
        }
      }
    }

    // Retention fields
    const retentionFields = ['Chat', 'Commands', 'Signs', 'Sessions', 'Items', 'Anvils', 'Usernames', 'Automod', 'Anticheat'];
    for (const field of retentionFields) {
      const input = document.getElementById('retention' + field);
      const key = 'retention' + field;
      if (input && data[key] !== undefined) {
        input.value = data[key];
      }
    }
  }

  // ===== EVIDENCE CONFIGURATION =====
  window.toggleEvidenceRequired = function() {
    const toggle = document.getElementById('togEvidenceRequired');
    if (!toggle) return;

    if (toggle.classList.contains('on')) {
      toggle.classList.remove('on');
      document.getElementById('togEvidenceRequiredHint').textContent = 'Off';
    } else {
      toggle.classList.add('on');
      document.getElementById('togEvidenceRequiredHint').textContent = 'On';
    }
    window.MX.sounds?.toggle();
  };

  window.saveEvidenceSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save settings - not connected to server.');
      return;
    }

    ws.send('UPDATE_EVIDENCE_SETTINGS', {
      maxFileSizeMb: parseInt(document.getElementById('evidenceMaxFileSizeMb')?.value || '250', 10),
      maxActivityLogEntries: parseInt(document.getElementById('evidenceMaxActivityLogEntries')?.value || '5', 10),
      requireEvidence: document.getElementById('togEvidenceRequired')?.classList.contains('on') || false
    });

    window.MX.sounds?.success();
    toast('ok', 'Saved', 'Evidence settings saved.', {silent: true});
  };

  function loadEvidenceSettings(data) {
    if (!data) return;

    if (data.maxFileSizeMb !== undefined) {
      document.getElementById('evidenceMaxFileSizeMb').value = data.maxFileSizeMb;
    }
    if (data.maxActivityLogEntries !== undefined) {
      document.getElementById('evidenceMaxActivityLogEntries').value = data.maxActivityLogEntries;
    }

    const toggle = document.getElementById('togEvidenceRequired');
    if (toggle && data.requireEvidence !== undefined) {
      if (data.requireEvidence) {
        toggle.classList.add('on');
        document.getElementById('togEvidenceRequiredHint').textContent = 'On';
      } else {
        toggle.classList.remove('on');
        document.getElementById('togEvidenceRequiredHint').textContent = 'Off';
      }
    }
  }

  // ===== SERVER LOCKDOWN =====
  let lockdownTimerInterval = null;

  window.toggleLockdown = function() {
    const ws = window.MX?.ws;
    const toggle = document.getElementById('togLockdown');
    const optionsDiv = document.getElementById('lockdownOptions');
    const isEnabled = toggle?.classList.contains('on');

    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot toggle lockdown - not connected to server.');
      return;
    }

    if (isEnabled) {
      // Disable lockdown
      ws.send('SET_LOCKDOWN', { enabled: false });
      toggle.classList.remove('on');
      document.getElementById('togLockdownHint').textContent = 'Off';
      optionsDiv.style.display = 'none';
      window.MX.sounds?.toggle();
      toast('ok', 'Lockdown Disabled', 'Server is now accepting new players.', {silent: true});
      logEvent('INFO', 'action', 'Lockdown Disabled', 'Server lockdown disabled');
      if (lockdownTimerInterval) {
        clearInterval(lockdownTimerInterval);
        lockdownTimerInterval = null;
      }
    } else {
      // Enable lockdown with settings
      const timer = parseInt(document.getElementById('lockdownTimer')?.value || '0', 10);
      const motd = document.getElementById('lockdownMotd')?.value || '';
      const kickMessage = document.getElementById('lockdownKickMessage')?.value || 'Server is under maintenance.';

      ws.send('SET_LOCKDOWN', {
        enabled: true,
        timer: timer,
        motd: motd,
        kickMessage: kickMessage
      });

      toggle.classList.add('on');
      document.getElementById('togLockdownHint').textContent = 'On';
      optionsDiv.style.display = 'block';
      window.MX.sounds?.toggle();
      toast('ok', 'Lockdown Enabled', timer > 0 ? `Server locked for ${timer} minutes.` : 'Server locked indefinitely.', {silent: true});
      logEvent('WARN', 'action', 'Lockdown Enabled', `Server lockdown enabled${timer > 0 ? ` for ${timer} minutes` : ''}`);

      // Start countdown timer if timer is set
      if (timer > 0) {
        startLockdownCountdown(timer * 60);
      }
    }
  };

  function startLockdownCountdown(seconds) {
    const remainingEl = document.getElementById('lockdownTimeRemaining');
    if (lockdownTimerInterval) clearInterval(lockdownTimerInterval);

    let remaining = seconds;
    const updateDisplay = () => {
      const mins = Math.floor(remaining / 60);
      const secs = remaining % 60;
      if (remainingEl) {
        remainingEl.textContent = `${mins}m ${secs}s remaining`;
        remainingEl.style.color = remaining < 60 ? 'var(--bad)' : 'var(--ok)';
      }
    };

    updateDisplay();
    lockdownTimerInterval = setInterval(() => {
      remaining--;
      if (remaining <= 0) {
        clearInterval(lockdownTimerInterval);
        lockdownTimerInterval = null;
        if (remainingEl) {
          remainingEl.textContent = 'Expired';
          remainingEl.style.color = 'var(--text-secondary)';
        }
        // Server will auto-disable, so update UI
        const toggle = document.getElementById('togLockdown');
        if (toggle) toggle.classList.remove('on');
        document.getElementById('togLockdownHint').textContent = 'Off';
        toast('info', 'Lockdown Expired', 'Server lockdown has automatically ended.');
      } else {
        updateDisplay();
      }
    }, 1000);
  }

  window.updateLockdownSettings = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save settings - not connected to server.');
      return;
    }

    const timer = parseInt(document.getElementById('lockdownTimer')?.value || '0', 10);
    const motd = document.getElementById('lockdownMotd')?.value || '';
    const kickMessage = document.getElementById('lockdownKickMessage')?.value || 'Server is under maintenance.';

    ws.send('UPDATE_LOCKDOWN_SETTINGS', { timer, motd, kickMessage });
    window.MX.sounds?.success();
    toast('ok', 'Saved', 'Lockdown settings updated.');
  };

  // Update lockdown timer hint
  document.getElementById('lockdownTimer')?.addEventListener('input', function() {
    const hint = document.getElementById('lockdownTimerHint');
    const value = parseInt(this.value || '0', 10);
    if (hint) {
      hint.textContent = value === 0 ? 'No auto-expire' : `Auto-expires in ${value} min`;
    }
  });

  // ===== NOTIFICATION CONFIGURATION =====
  window.updateJoinLeaveVisibility = function() {
    const ws = window.MX?.ws;
    const visibility = document.getElementById('joinLeaveVisibility')?.value || 'all';

    if (ws && ws.isConnected()) {
      ws.send('UPDATE_NOTIFICATION_SETTINGS', { joinLeaveVisibility: visibility });
      window.MX.sounds?.success();
      toast('ok', 'Saved', `Join/leave messages now visible to ${visibility === 'all' ? 'everyone' : 'staff only'}.`, {silent: true});
    }
  };

  // ===== COMMAND BLACKLIST =====
  window.saveCommandBlacklist = function() {
    const ws = window.MX?.ws;
    if (!ws || !ws.isConnected()) {
      toast('warn', 'Not Connected', 'Cannot save - not connected to server.');
      return;
    }

    const commands = (document.getElementById('blockedCommands')?.value || '')
      .split('\n')
      .map(c => c.trim().toLowerCase().replace(/^\//, ''))
      .filter(c => c.length > 0);
    const blockMessage = document.getElementById('cmdBlockMessage')?.value || 'You cannot use this command.';

    ws.send('UPDATE_COMMAND_BLACKLIST', {
      commands,
      blockMessage
    });

    window.MX.sounds?.success();
    toast('ok', 'Saved', `Command blacklist updated (${commands.length} commands).`);
    logEvent('INFO', 'action', 'Command Blacklist Updated', `${commands.length} commands blacklisted`);
  };

  window.saveIntegrations = function() {
    state.settings.discordWebhook = dom().discordWebhook.value;
    ui.markUnsaved('integrations', true);
    toast('ok', 'Saved', 'Integration settings saved.');
  };

  window.testWebhook = function() {
    if (!state.settings.discordWebhook) { toast('warn', 'No Webhook', 'Configure webhook URL first.'); return; }
    const staffName = state.staffName || 'Staff';
    const payload = {
      username: 'ModereX',
      content: `Test from ModereX (${staffName})`,
      embeds: [
        {
          title: 'ModereX Webhook Test',
          description: 'If you can see this, webhook delivery is working.',
          color: 0x2d7aed
        }
      ]
    };
    fetch(state.settings.discordWebhook, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    }).then(res => {
      if (!res.ok) throw new Error('bad response');
      toast('ok', 'Test Sent', 'Webhook delivered.');
    }).catch(err => {
      console.error('Webhook error:', err);
      const reason = err.message || 'Network error';
      toast('warn', 'Delivery Failed', 'Webhook request failed. Check URL or CORS.');
      if (window.debugLog) window.debugLog('WEBHOOK', `Test failed: ${reason}`, 'error');
    });
  };

  window.setLanguage = function(lang) {
    state.lang = lang;
    ui.markUnsaved('messages', true);
    ui.renderMessages();
  };

  window.resetLang = function() {
    state.lang = 'en_US';
    ui.markUnsaved('messages', false);
    ui.renderMessages();
    toast('info', 'Reset', 'Language reset to English.');
  };

  window.publishSettings = function() {
    setPublishLoading(true);

    // In live mode, send changes to server
    if (isLiveMode && state.authenticated) {
      const promises = [];

      // Sync automod rules if changed
      if (state.unsaved.rules) {
        promises.push(new Promise((resolve) => {
          ws.send('SYNC_AUTOMOD_RULES', { rules: state.rules });
          setTimeout(resolve, 200); // Wait for server acknowledgment
        }));
      }

      // Sync settings if changed
      if (state.unsaved.settings || state.unsaved.chat) {
        promises.push(new Promise((resolve) => {
          ws.send('UPDATE_SETTINGS', {
            chatEnabled: !state.settings.chatDisabled,
            defaultSlowmode: state.settings.slowEnabled ? state.settings.slowSeconds : 0
          });
          setTimeout(resolve, 200);
        }));
      }

      // Wait for all sync operations
      Promise.all(promises).then(() => {
        state.unsaved = {};
        ui.refreshUnsavedUI();
        setPublishLoading(false);
        toast('ok', 'Published', 'All changes synced to server.');
        logEvent('INFO', 'system', 'Settings published', 'Configuration applied to server.');
      }).catch((err) => {
        setPublishLoading(false);
        const reason = err?.message || 'Sync failed';
        toast('error', 'Error', 'Failed to publish some changes.');
        if (window.debugLog) window.debugLog('SYNC', `Publish failed: ${reason}`, 'error');
      });
    } else {
      // Demo mode - just clear unsaved state
      setTimeout(() => {
        state.unsaved = {};
        ui.refreshUnsavedUI();
        setPublishLoading(false);
        toast('ok', 'Published', 'All changes published (demo mode).');
        logEvent('INFO', 'system', 'Settings published', 'Configuration applied.');
      }, 1200);
    }
  };

  // ===== WIZARD =====
  window.openWizard = function() {
    dom().wizardOverlay.classList.add('show');
    state.wizard.step = 0;
    renderWizard();
  };

  window.closeWizard = function(e) {
    if (e) e.stopPropagation?.();
    dom().wizardOverlay.classList.add('fade-out');
    setTimeout(() => {
      dom().wizardOverlay.classList.remove('show', 'fade-out');
    }, 220);
  };

  window.wizardBack = function() {
    state.wizard.step = Math.max(0, state.wizard.step - 1);
    renderWizard();
  };

  window.wizardNext = function() {
    if (state.wizard.step === 4) { ui.markUnsaved('wizard', true); toast('ok', 'Complete', 'Review and publish.'); closeWizard(); ui.renderAll(); return; }
    state.wizard.step = Math.min(4, state.wizard.step + 1);
    renderWizard();
  };

  function renderWizard() {
    dom().wizStepChip.innerHTML = `<i class="fa-solid fa-list-check"></i> Step ${state.wizard.step + 1}/5`;
    const steps = [
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-plug" style="color:var(--primary-light)"></i> Plugin Detection</h3><p>Detecting installed plugins and dependencies.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-clock" style="color:var(--warn)"></i> Timezone</h3><p>Set server timezone for timestamps.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-key" style="color:var(--accent-light)"></i> Permissions</h3><p>Configure staff permissions.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-robot" style="color:var(--ok)"></i> Automod</h3><p>Create default automod rules.</p></div>`,
      `<div class="card" style="margin:0"><h3><i class="fa-solid fa-server" style="color:var(--primary-light)"></i> Connectivity</h3><p>Verifying service connections.</p></div>`
    ];
    dom().wizardBody.innerHTML = steps[state.wizard.step];
  }

  // ===== DISCORD SUPPORT =====
  window.openDiscordSupport = function() {
    const overlay = document.getElementById('discordSupportOverlay');
    if (overlay) {
      overlay.classList.add('show');
      window.MX.sounds?.modalOpen();
    }
  };

  window.closeDiscordSupport = function() {
    const overlay = document.getElementById('discordSupportOverlay');
    if (overlay) {
      overlay.classList.add('fade-out');
      setTimeout(() => {
        overlay.classList.remove('show', 'fade-out');
      }, 220);
      window.MX.sounds?.modalClose();
    }
  };

  // Close on overlay click
  document.getElementById('discordSupportOverlay')?.addEventListener('click', closeDiscordSupport);

  window.toggleTesterPanel = function() {
    dom().testerPanel?.classList.toggle('show');
  };

  // ===== GETTING STARTED GUIDE =====
  window.scrollToGuide = function(sectionId) {
    const section = document.getElementById('guide-' + sectionId);
    if (section) {
      // Expand section if collapsed
      section.classList.remove('collapsed');

      // Scroll into view
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });

      // Update active TOC item
      document.querySelectorAll('.toc-item').forEach(item => {
        item.classList.toggle('active', item.getAttribute('onclick')?.includes(sectionId));
      });

      // Highlight effect
      section.classList.add('search-match');
      setTimeout(() => section.classList.remove('search-match'), 1000);
    }
    return false; // Prevent default link behavior
  };

  window.toggleGuideAccordion = function(titleEl) {
    const section = titleEl.closest('.guide-section');
    if (section) {
      section.classList.toggle('collapsed');
    }
  };

  window.toggleGuideSection = function() {
    const sections = document.querySelectorAll('.guide-section');
    const anyExpanded = Array.from(sections).some(s => !s.classList.contains('collapsed'));

    sections.forEach(section => {
      section.classList.toggle('collapsed', anyExpanded);
    });
  };

  window.searchGuide = function(query) {
    const sections = document.querySelectorAll('.guide-section');
    const q = query.toLowerCase().trim();

    if (!q) {
      sections.forEach(s => s.classList.remove('hidden'));
      return;
    }

    sections.forEach(section => {
      const keywords = section.getAttribute('data-keywords') || '';
      const content = section.textContent.toLowerCase();
      const matches = keywords.includes(q) || content.includes(q);
      section.classList.toggle('hidden', !matches);
    });
  };

  window.toggleHideGuide = function() {
    const toggle = document.getElementById('hideGuideToggle');
    const isHidden = toggle?.classList.contains('on');

    if (isHidden) {
      // Show guide
      toggle.classList.remove('on');
      document.getElementById('gettingStartedNav')?.style.setProperty('display', 'block');
    } else {
      // Hide guide
      toggle.classList.add('on');
      document.getElementById('gettingStartedNav')?.style.setProperty('display', 'none');
      go('dashboard'); // Navigate away from guide
    }

    // Save setting
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { hideGettingStarted: !isHidden });
    }

    // Save to local storage as fallback
    try {
      localStorage.setItem('mx_hide_getting_started', !isHidden);
    } catch (e) {}
  };

  // Initialize guide visibility from settings
  function initGuideVisibility() {
    let hidden = false;
    try {
      hidden = localStorage.getItem('mx_hide_getting_started') === 'true';
    } catch (e) {}

    if (hidden) {
      document.getElementById('hideGuideToggle')?.classList.add('on');
      document.getElementById('gettingStartedNav')?.style.setProperty('display', 'none');
    }
  }

  // Call on page load
  document.addEventListener('DOMContentLoaded', initGuideVisibility);

  // ===== BACKGROUND ANIMATION =====
  function setupBackgroundAnimation() {
    const canvas = document.getElementById('bgCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let w, h;
    const particles = [];
    const maxDist = 180;

    function resize() { w = canvas.width = window.innerWidth; h = canvas.height = window.innerHeight; }
    function createParticle() {
      return {
        x: Math.random() * w,
        y: Math.random() * h,
        vx: (Math.random() - 0.5) * 0.45,
        vy: (Math.random() - 0.5) * 0.45,
        r: Math.random() * 2 + 1,
        a: Math.random() * 0.35 + 0.12,
        tw: Math.random() * 1.2
      };
    }

    // Get theme color RGB values for canvas drawing
    function getThemeRGB() {
      const color = state.userSettings?.themeColor || '#2d7aed';
      const r = parseInt(color.slice(1, 3), 16);
      const g = parseInt(color.slice(3, 5), 16);
      const b = parseInt(color.slice(5, 7), 16);
      // Create a lighter version for particles (add 80 to each, clamped to 255)
      const lr = Math.min(r + 90, 255);
      const lg = Math.min(g + 90, 255);
      const lb = Math.min(b + 90, 255);
      return { r, g, b, lr, lg, lb };
    }

    function draw() {
      ctx.clearRect(0, 0, w, h);
      const theme = getThemeRGB();

      for (const p of particles) {
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0) p.x = w; if (p.x > w) p.x = 0; if (p.y < 0) p.y = h; if (p.y > h) p.y = 0;
        p.tw += 0.01;
      }

      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          const a = particles[i];
          const b = particles[j];
          const dx = a.x - b.x;
          const dy = a.y - b.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < maxDist) {
            const alpha = (1 - dist / maxDist) * 0.5;
            ctx.strokeStyle = `rgba(${theme.r}, ${theme.g}, ${theme.b}, ${alpha})`;
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(a.x, a.y);
            ctx.lineTo(b.x, b.y);
            ctx.stroke();
          }
        }
      }

      for (const p of particles) {
        const glow = p.a + Math.sin(p.tw) * 0.05;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(${theme.lr}, ${theme.lg}, ${theme.lb}, ${glow})`;
        ctx.fill();
      }
      requestAnimationFrame(draw);
    }

    resize();
    window.addEventListener('resize', resize);
    for (let i = 0; i < 120; i++) particles.push(createParticle());
    draw();
  }

  // ===== CLOCK & TIMERS =====
  function startClock() {
    const update = () => { dom().timeChip.innerHTML = `<i class="fa-regular fa-clock"></i> ${fmtClock()}`; };
    update();
    setInterval(update, 1000);
  }

  // Duration countdown - updates every second
  function startDurationCountdown() {
    setInterval(() => {
      // Update punishment durations in state
      const nowMs = Date.now();
      let needsUpdate = false;

      state.punishments.forEach(pun => {
        if (pun.expiresAt && pun.expiresAt > 0 && pun.expiresAt !== -1) {
          const remaining = pun.expiresAt - nowMs;
          if (remaining > 0) {
            pun.remainingMs = remaining;
            pun.remainingDisplay = formatDurationShort(remaining);
            needsUpdate = true;
          } else if (pun.active) {
            // Expired - mark as inactive
            pun.active = false;
            pun.remainingDisplay = 'Expired';
            needsUpdate = true;
          }
        }
      });

      // Re-render if any durations changed
      if (needsUpdate) {
        ui.renderPunishments();
      }
    }, 1000);
  }

  // Format duration in short form (e.g., "2d 5h 30m")
  function formatDurationShort(ms) {
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

  // Status indicator updates with ping-based color coding
  function updateStatusIndicator(status, ping) {
    const statusChip = document.getElementById('statusChip');
    const pingText = document.getElementById('pingText');
    const statusDot = document.getElementById('statusDot');

    if (!statusChip || !pingText) return;

    // Update ping text
    pingText.textContent = ping > 0 ? ping : '--';

    // Determine status based on connection and ping
    let statusClass = 'chip';
    let dotClass = '';
    let statusLabel = 'Disconnected';

    if (status === 'connected' && ping > 0) {
      if (ping < 100) {
        // Green: Excellent (<100ms)
        statusClass = 'chip ok';
        dotClass = 'ok';
        statusLabel = 'Excellent';
      } else if (ping < 300) {
        // Yellow: Good (100-300ms)
        statusClass = 'chip warn';
        dotClass = 'warn';
        statusLabel = 'Good';
      } else {
        // Red: Poor (>300ms)
        statusClass = 'chip bad';
        dotClass = 'bad';
        statusLabel = 'Poor';
      }
    } else if (status === 'connected') {
      statusClass = 'chip ok';
      dotClass = 'ok';
      statusLabel = 'Connected';
    } else if (status === 'saving') {
      statusClass = 'chip warn';
      dotClass = 'warn';
      statusLabel = 'Saving...';
    } else {
      statusClass = 'chip bad';
      dotClass = 'bad';
      statusLabel = 'Disconnected';
    }

    statusChip.className = statusClass;
    statusChip.title = `${statusLabel}${ping > 0 ? ' - ' + ping + 'ms latency' : ''}`;

    // Also update sidebar status dot
    if (statusDot) {
      statusDot.className = 'dot' + (dotClass ? ' ' + dotClass : '');
    }
  }

  function showSavingIndicator() {
    const savingChip = document.getElementById('savingChip');
    if (savingChip) savingChip.style.display = '';
  }

  function hideSavingIndicator() {
    const savingChip = document.getElementById('savingChip');
    if (savingChip) savingChip.style.display = 'none';
  }

  // ===== SIMULATION =====
  function startSimulation() {
    const chatSamples = [
      'gg', 'hello', 'hey everyone', 'looking for diamonds', 'who wants to duel', 'lmao', 'nice base', 'brb',
      'need help at spawn', 'tp me?', 'grats', 'that was close', 'wow', 'server is smooth',
      'anyone online?', 'who built this', 'party up', 'join my town', 'lol', 'ok', 'sure', 'thanks', 'ggs',
      'caps test', 'WOW THATS HUGE', 'check this out', 'hey', 'heya', 'heyy',
      'this is awesome', 'any mods on?', 'help', 'new player here', 'testing', 'anyone want to mine',
      'lets go', 'not really', 'haha', 'ok ok', 'gg again', 'dm me', 'invite me', 'nope', 'lolol',
      'what time is it', 'server restart soon?', 'oops', 'my bad', 'forgive me',
      'party chat', 'where is end', 'nether?', 'portal coords', 'meet at spawn', 'be right back', 'afk',
      'BUY NOW', 'HELLO EVERYONE',
      'I love this', 'clutch', 'speedrun time', 'nice pvp', 'gg wp', 'help me',
      'anyone have elytra', 'mending book', 'enchanting',
      'hello hello', 'ok done', 'gg bye'
    ];
    const chatNouns = ['spawn', 'base', 'farm', 'mine', 'nether', 'end', 'village', 'arena'];
    const chatVerbs = ['looking for', 'need help with', 'anyone seen', 'going to'];
    const chatAdj = ['rare', 'epic', 'huge', 'fast', 'new', 'cool'];
    for (const noun of chatNouns) {
      for (const verb of chatVerbs) {
        chatSamples.push(`${verb} ${noun}`);
      }
    }
    for (const adj of chatAdj) {
      for (const noun of chatNouns) {
        chatSamples.push(`${adj} ${noun}`);
      }
    }
    while (chatSamples.length < 180) {
      chatSamples.push(`message ${chatSamples.length + 1}`);
    }
    const commandSamples = [
      '/spawn','/home','/warp shop','/tpa Admin','/msg Player hey','/balance','/sell','/buy','/kit starter','/pay Admin 50',
      '/sethome base','/home base','/warp pvp','/warp nether','/warp end','/spawn','/msg ModA hello','/ignore Player',
      '/claim','/unclaim','/tpaccept','/tpdeny','/trade Player','/ah sell','/ah list','/r ok','/nick CoolGuy',
      '/mail send','/msg Helper help','/rules','/help','/report Player','/warp crates','/vote','/menu','/discord',
      '/warp market','/warp arena','/warp village','/warp endcity','/warp farms','/warp bank','/warp boss',
      '/kit daily','/kit tools','/kit food','/shop','/trade accept','/tpa ModB','/tpahere Helper',
      '/party create','/party invite','/party leave','/home main','/home farm','/home mine',
      '/bal top','/pay ModA 25','/msg Admin hi','/ignore list','/seen Player','/ping','/vote claim'
    ];
    const cmdBases = ['/warp', '/kit', '/home', '/msg', '/pay', '/tpa'];
    const cmdArgs = ['alpha', 'beta', 'gamma', 'delta', 'omega', 'spawn', 'shop', 'pvp', 'nether', 'end'];
    for (const base of cmdBases) {
      for (const arg of cmdArgs) {
        commandSamples.push(`${base} ${arg}`);
      }
    }

    for (let i = 0; i < 8; i++) {
      const p = pick(state.players.filter(x => x.status === 'online')) || pick(state.players);
      logEvent('INFO', 'chat', `Chat | ${p.name}`, pick(['hey', 'gg', 'lol', 'nice']), { playerId: p.id, kind: 'chat' });
    }

    for (let i = 0; i < 220; i++) {
      const p = pick(state.players);
      const msg = pick(chatSamples);
      const hits = evaluateAutomodMessage(p.id, msg);
      logEvent(hits.length ? 'WARN' : 'INFO', 'chat', `Chat | ${p.name}`, msg, { playerId: p.id, kind: 'chat', type: 'CHAT' });
      hits.forEach(rule => {
        logEvent('WARN', 'automod', `Automod | ${rule.name}`, `${p.name} triggered`, { playerId: p.id, kind: 'automod', type: 'AUTOMOD' });
        applyAutomodAction(p.id, rule, msg);
      });
    }

    for (let i = 0; i < 260; i++) {
      const p = pick(state.players);
      const cmd = pick(commandSamples);
      if (!p.recentCommands) p.recentCommands = [];
      p.recentCommands.push({ cmd, t: now() - Math.floor(Math.random() * 86400000 * 5) });
      logEvent('INFO', 'system', `Command | ${p.name}`, cmd, { playerId: p.id, kind: 'command', type: 'COMMAND' });
    }

    simulateConnect();

    setInterval(() => {
      if (state.autoPaused) return;
      const actor = pick(state.players.filter(p => p.status === 'online')) || pick(state.players);
      const roll = Math.random();
      if (roll < 0.6) {
        const msg = pick(chatSamples);
        const hits = evaluateAutomodMessage(actor.id, msg);
        logEvent(hits.length ? 'WARN' : 'INFO', 'chat', `Chat | ${actor.name}`, msg, { playerId: actor.id, kind: 'chat', type: 'CHAT' });
        hits.forEach(rule => {
          logEvent('WARN', 'automod', `Automod | ${rule.name}`, `${actor.name} triggered`, { playerId: actor.id, kind: 'automod', type: 'AUTOMOD' });
          applyAutomodAction(actor.id, rule, msg);
        });
      } else if (roll < 0.8) {
        logEvent('INFO', 'system', 'System', 'Background task completed');
      } else {
        const staffer = pick(state.staff);
        const target = pick(state.players);
        const types = ['WARN', 'MUTE', 'BAN'];
        const type = pick(types);
        executePunishment({ playerId: target.id, type, reason: 'Staff action', duration: type === 'BAN' ? '7d' : type === 'MUTE' ? '2h' : '' });
        logEvent('WARN', 'system', `${staffer.name} action`, `${type} issued to ${target.name}`, { kind: 'system' });
        ui.renderPunishments();
        ui.renderPlayers();
      }
      ui.renderDashboard();
    }, 2500);
  }

  function simulateConnect() {
    const username = 'YaBoiCameronYT';
    const isGeyser = false;
    state.currentUser = {
      name: username,
      platform: 'Java',
      geyser: isGeyser,
      connectedAt: now()
    };
    ui.renderTopUser();
    loadUserPrefs();
    logEvent('INFO', 'system', 'Connect command', `${username} connected (${isGeyser ? 'Geyser' : 'Java'})`, { kind: 'system' });
  }

  // ===== EVENT SETUP =====
  function setupEventListeners() {
    $$('.sb-item').forEach(item => item.addEventListener('click', () => { if (item.dataset.page) go(item.dataset.page); }));

    // Logo click: if in gateway mode, navigate to server list
    const sbHead = document.querySelector('.sb-head');
    if (sbHead) {
      sbHead.style.cursor = 'pointer';
      sbHead.addEventListener('click', () => {
        if (ws.isGatewayMode() && window.goToServerList) {
          window.goToServerList();
        } else {
          go('dashboard');
        }
      });
    }

    dom().playerSearch?.addEventListener('input', ui.renderPlayers);
    dom().punishSearch?.addEventListener('input', ui.renderPunishments);
    dom().templateSearch?.addEventListener('input', ui.renderTemplates);
    dom().watchSearch?.addEventListener('input', ui.renderWatchlist);
    document.getElementById('replaySearch')?.addEventListener('input', renderReplayList);
    dom().msgSearch?.addEventListener('input', ui.renderMessages);
    dom().logsSearch?.addEventListener('input', ui.renderLogs);
    dom().anticheatSearch?.addEventListener('input', ui.renderAnticheat);
    dom().logsPageSize?.addEventListener('change', (e) => {
      state.logsFilters.pageSize = parseInt(e.target.value, 10) || 100;
      state.logsFilters.page = 1;
      saveUserPrefs();
      ui.renderLogs();
    });

    // Activity Log search event listeners
    const activitySearchEl = document.getElementById('activitySearch');
    if (activitySearchEl) {
      let searchTimeout = null;
      activitySearchEl.addEventListener('input', (e) => {
        // Update filter highlighting
        updateActivitySearchHighlight(activitySearchEl);
        // Show suggestions while typing
        showActivitySearchSuggestions(e.target.value);
        // Debounce the actual search
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => fetchActivityLogs(1), 500);
      });
      activitySearchEl.addEventListener('focus', () => {
        updateActivitySearchHighlight(activitySearchEl);
        if (activitySearchEl.value) {
          showActivitySearchSuggestions(activitySearchEl.value);
        }
      });
      activitySearchEl.addEventListener('blur', () => {
        // Hide suggestions after a delay (allow click on suggestion)
        setTimeout(() => {
          const suggestionBox = document.getElementById('activitySearchSuggestions');
          if (suggestionBox) suggestionBox.style.display = 'none';
        }, 200);
      });
      // Initialize highlight on page load
      updateActivitySearchHighlight(activitySearchEl);
    }

    // Evidence search event listener (in punishment form)
    const evidenceSearchEl = document.getElementById('punishEvidenceSearch');
    if (evidenceSearchEl) {
      let evidenceSearchTimeout = null;
      evidenceSearchEl.addEventListener('input', (e) => {
        // Update filter highlighting
        updateActivitySearchHighlight(evidenceSearchEl);
        // Debounce the search
        clearTimeout(evidenceSearchTimeout);
        evidenceSearchTimeout = setTimeout(() => {
          if (state.punishEvidence.expanded && state.massPlayerIds.length > 0) {
            fetchEvidenceActivityLogs();
          }
        }, 500);
      });
      evidenceSearchEl.addEventListener('focus', () => {
        updateActivitySearchHighlight(evidenceSearchEl);
      });
    }

    dom().slowSeconds?.addEventListener('input', (e) => {
      state.settings.slowSeconds = parseInt(e.target.value || '0', 10);
      ui.markUnsaved('settings', true);
    });

    let lastAutoPaused = state.autoPaused;
    dom().logsBox?.addEventListener('scroll', () => {
      const box = dom().logsBox;
      if (!box) return;
      const atBottom = box.scrollTop + box.clientHeight >= box.scrollHeight - 8;
      state.autoPaused = !atBottom;
      if (state.autoPaused !== lastAutoPaused) {
        lastAutoPaused = state.autoPaused;
        ui.renderLogs();
      }
    });

    dom().punishTemplate?.addEventListener('change', (e) => applyTemplateToPunish(e.target.value));
    dom().punishTypeSelect?.addEventListener('change', (e) => setPunishType(e.target.value));
    dom().punishTarget?.addEventListener('change', (e) => {
      state.selectedPlayerId = e.target.value;
      renderEvidenceOptions(e.target.value, dom().punishEvidencePick, dom().punishEvidencePreview);
      updatePunishTitle(dom().punishTitle, state.pendingPunishType, state.selectedPlayerId);
    });
    dom().punishEvidencePick?.addEventListener('change', () => updateEvidencePreviewFor(dom().punishEvidencePick, dom().punishEvidencePreview));

    dom().punishCreatePlayer?.addEventListener('input', renderPunishCreateList);
    dom().punishCreatePlayer?.addEventListener('focus', renderPunishCreateList);
    dom().punishCreatePlayer?.addEventListener('blur', () => {
      const combo = dom().punishCreateList?.closest('.combo');
      setTimeout(() => combo?.classList.remove('open'), 150);
    });
    dom().punishCreateType?.addEventListener('change', (e) => {
      state.pendingPunishType = e.target.value;
      updatePunishCreateTitle();
    });
    dom().punishCreateTemplate?.addEventListener('change', (e) => applyTemplateToPunishCreate(e.target.value));
    dom().punishCreateEvidencePick?.addEventListener('change', () => updateEvidencePreviewFor(dom().punishCreateEvidencePick, dom().punishCreateEvidencePreview));
    dom().authToken?.addEventListener('keydown', (e) => { if (e.key === 'Enter') login(); });

    dom().globalSearch?.addEventListener('input', (e) => {
      const query = e.target.value.trim();
      if (query.length >= 1) {
        performLiveSearch(query);
      } else {
        hideSearchDropdown();
      }
    });

    dom().globalSearch?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        if (searchState.selectedIndex >= 0 && searchState.results.length > 0) {
          selectSearchResult(searchState.results[searchState.selectedIndex]);
          hideSearchDropdown();
        } else if (e.target.value.trim()) {
          performGlobalSearch(e.target.value.trim());
        }
      } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        navigateSearchResults(1);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        navigateSearchResults(-1);
      } else if (e.key === 'Escape') {
        hideSearchDropdown();
        e.target.blur();
      }
    });

    dom().globalSearch?.addEventListener('blur', () => {
      setTimeout(() => hideSearchDropdown(), 200);
    });

    dom().globalSearch?.addEventListener('focus', (e) => {
      const query = e.target.value.trim();
      if (query.length >= 1) {
        performLiveSearch(query);
      }
    });

    document.addEventListener('keydown', (e) => {
      if (e.ctrlKey && e.key === 'k') { e.preventDefault(); dom().globalSearch?.focus(); }
      if (e.key === 'Escape') { closeDrawer(); closePunishModal(); closePunishCreateModal(); closeDetailsModal(); closeWizard(); closeContextMenu(); hideSearchDropdown(); }
    });

    document.addEventListener('click', (e) => {
      if (contextMenuEl && !e.target.closest('.context-menu')) closeContextMenu();
    });

    document.addEventListener('contextmenu', (e) => {
      const row = e.target.closest('[data-player-id]');
      if (!row) return;
      e.preventDefault();
      openContextMenu(row.dataset.playerId, e.clientX, e.clientY);
    });

    window.addEventListener('scroll', closeContextMenu, true);
  }

  // ===== GLOBAL SEARCH =====

  // Search state
  const searchState = {
    results: [],
    selectedIndex: -1,
    debounceTimer: null
  };

  // Pages that can be searched (with optional permission requirements)
  const searchablePages = [
    { id: 'dashboard', name: 'Dashboard', icon: 'fa-gauge-high', keywords: ['home', 'overview', 'stats', 'activity'] },
    { id: 'players', name: 'Player Management', icon: 'fa-users', keywords: ['users', 'list', 'online'] },
    { id: 'punishments', name: 'Punishments', icon: 'fa-gavel', keywords: ['bans', 'mutes', 'kicks', 'warns', 'cases'] },
    { id: 'templates', name: 'Templates', icon: 'fa-bookmark', keywords: ['presets', 'quick', 'saved'] },
    { id: 'automod', name: 'Automod Rules', icon: 'fa-robot', keywords: ['filter', 'chat', 'spam', 'swear'], permission: 'moderex.automod' },
    { id: 'anticheat', name: 'Anticheat', icon: 'fa-shield-halved', keywords: ['hacks', 'cheats', 'alerts'], permission: 'moderex.anticheat' },
    { id: 'watchlist', name: 'Watchlist', icon: 'fa-eye', keywords: ['monitor', 'watch', 'track'], permission: 'moderex.watchlist' },
    { id: 'replay', name: 'Replays', icon: 'fa-film', keywords: ['recording', 'playback', 'session'], permission: 'moderex.replays.view' },
    { id: 'activitylog', name: 'Activity Log', icon: 'fa-scroll', keywords: ['history', 'events', 'database', 'chat', 'commands'], permission: 'moderex.activitylog' },
    { id: 'staffchat', name: 'Staff Chat', icon: 'fa-comments', keywords: ['team', 'message', 'communicate'], permission: 'moderex.staffchat' },
    { id: 'mysettings', name: 'My Settings', icon: 'fa-user-gear', keywords: ['preferences', 'sounds', 'notifications'] },
    { id: 'messages', name: 'Messages', icon: 'fa-language', keywords: ['lang', 'text', 'translate'] },
    { id: 'actions', name: 'Configuration', icon: 'fa-bolt', keywords: ['quick', 'chat', 'kick all', 'config', 'settings'] },
    { id: 'integrations', name: 'Integrations', icon: 'fa-plug', keywords: ['luckperms', 'plugins', 'hooks'] },
    { id: 'devtools', name: 'Developer Tools', icon: 'fa-code', keywords: ['dev', 'debug', 'test', 'stress', 'developer'] }
  ];

  // Settings that can be searched (with element IDs for auto-scroll)
  const searchableSettings = [
    // Panel sounds & display
    { name: 'Sound Effects', page: 'mysettings', keywords: ['audio', 'notification', 'mute', 'volume'], elementId: 'soundsEnabled' },
    { name: 'Volume Control', page: 'mysettings', keywords: ['audio', 'volume', 'slider'], elementId: 'volumeSlider' },
    { name: 'Auto Sign-in', page: 'mysettings', keywords: ['device', 'trust', 'remember', 'login'], elementId: 'deviceTrustEnabled' },
    { name: 'Debug Mode', page: 'mysettings', keywords: ['developer', 'debug', 'logging', 'console'], elementId: 'debugModeEnabled' },
    { name: 'Theme Color', page: 'mysettings', keywords: ['color', 'appearance', 'theme', 'blue', 'red', 'green'], elementId: 'themePresets' },
    { name: 'Background Pattern', page: 'mysettings', keywords: ['pattern', 'background', 'aurora', 'stars', 'waves'], elementId: 'patternGrid' },
    // Alert configuration (staff settings)
    { name: 'Ban Alerts', page: 'mysettings', keywords: ['ban', 'punishment', 'alert', 'notify'], elementId: 'staffSettingsContainer' },
    { name: 'Kick Alerts', page: 'mysettings', keywords: ['kick', 'punishment', 'alert', 'notify'], elementId: 'staffSettingsContainer' },
    { name: 'Mute Alerts', page: 'mysettings', keywords: ['mute', 'punishment', 'alert', 'notify'], elementId: 'staffSettingsContainer' },
    { name: 'Warn Alerts', page: 'mysettings', keywords: ['warn', 'warning', 'alert', 'notify'], elementId: 'staffSettingsContainer' },
    { name: 'Pardon Alerts', page: 'mysettings', keywords: ['pardon', 'unban', 'unmute', 'alert'], elementId: 'staffSettingsContainer' },
    { name: 'Automod Alerts', page: 'mysettings', keywords: ['automod', 'filter', 'chat', 'alert'], elementId: 'staffSettingsContainer' },
    { name: 'Anticheat Alerts', page: 'mysettings', keywords: ['anticheat', 'hack', 'cheat', 'alert'], elementId: 'staffSettingsContainer' },
    { name: 'Watchlist Alerts', page: 'mysettings', keywords: ['watchlist', 'monitor', 'alert', 'join'], elementId: 'staffSettingsContainer' },
    { name: 'Staff Chat', page: 'mysettings', keywords: ['staff', 'chat', 'message', 'team'], elementId: 'staffSettingsContainer' },
    { name: 'Toast Position', page: 'mysettings', keywords: ['toast', 'position', 'alert', 'location'], elementId: 'staffSettingsContainer' },
    { name: 'Alert Duration', page: 'mysettings', keywords: ['duration', 'time', 'alert', 'seconds'], elementId: 'staffSettingsContainer' },
    { name: 'Alert Sounds', page: 'mysettings', keywords: ['sound', 'audio', 'alert', 'notification'], elementId: 'staffSettingsContainer' },
    { name: 'Alert Rate Limiting', page: 'mysettings', keywords: ['rate', 'limit', 'spam', 'cooldown'], elementId: 'staffSettingsContainer' },
    // Actions - require admin permissions
    { name: 'Chat Lock', page: 'actions', keywords: ['disable', 'mute all', 'lock chat'], permission: 'moderex.admin.chat' },
    { name: 'Slowmode', page: 'actions', keywords: ['rate limit', 'spam', 'slow'], permission: 'moderex.admin.chat' },
    { name: 'Kick All', page: 'actions', keywords: ['clear', 'server', 'disconnect'], permission: 'moderex.admin.kickall' },
    // Developer Tools
    { name: 'Debug Permissions', page: 'devtools', keywords: ['permission', 'check', 'debug', 'perms'], elementId: 'devDebugPermissions' },
    { name: 'Test Notifications', page: 'devtools', keywords: ['test', 'alert', 'notification', 'toast'], elementId: 'devNotificationTest' },
    { name: 'Stress Test Players', page: 'devtools', keywords: ['stress', 'test', 'spoof', 'players', 'fake'], elementId: 'devStressPlayers' },
    { name: 'Stress Test Punishments', page: 'devtools', keywords: ['stress', 'test', 'spoof', 'punishments', 'fake'], elementId: 'devStressPunishments' },
    { name: 'Token Stress Test', page: 'devtools', keywords: ['token', 'auth', 'stress', 'test'], elementId: 'devTokenStress' },
    { name: 'UUID Auth', page: 'devtools', keywords: ['uuid', 'auth', 'authenticate', 'dev'], elementId: 'devUuidAuth' },
    { name: 'Development Checklist', page: 'devtools', keywords: ['checklist', 'todo', 'tasks', 'dev'], elementId: 'devChecklist' },
    { name: 'Debug Console', page: 'devtools', keywords: ['console', 'log', 'debug', 'messages'], elementId: 'devDebugConsole' }
  ];

  function performLiveSearch(query) {
    clearTimeout(searchState.debounceTimer);
    searchState.debounceTimer = setTimeout(() => {
      const q = query.toLowerCase();
      const results = [];

      // Search players (limit 5)
      const playerMatches = state.players
        .filter(p => p.name.toLowerCase().includes(q))
        .slice(0, 5)
        .map(p => ({
          type: 'player',
          id: p.id,
          title: p.name,
          subtitle: p.online ? 'Online' : 'Offline',
          icon: 'fa-user',
          data: p
        }));
      if (playerMatches.length > 0) {
        results.push({ category: 'Players', items: playerMatches });
      }

      // Search punishments by case ID or MX-<number> format (limit 5)
      const mxPrefix = q.match(/^mx-?(\d+)/i);
      const caseMatches = state.punishments
        .filter(p => {
          if (!p.caseId) return false;
          const cid = p.caseId.toLowerCase();
          if (mxPrefix) return cid.includes('mx-' + mxPrefix[1]);
          return cid.includes(q);
        })
        .slice(0, 5)
        .map(p => ({
          type: 'case',
          id: p.id,
          title: p.caseId,
          subtitle: `${p.type} - ${p.playerName}`,
          icon: 'fa-gavel',
          data: p
        }));
      if (caseMatches.length > 0) {
        results.push({ category: 'Punishments', items: caseMatches });
      }

      // Search templates (limit 3)
      const templateMatches = (state.templates || [])
        .filter(t => t.name.toLowerCase().includes(q))
        .slice(0, 3)
        .map(t => ({
          type: 'template',
          id: t.id,
          title: t.name,
          subtitle: t.type,
          icon: 'fa-bookmark',
          data: t
        }));
      if (templateMatches.length > 0) {
        results.push({ category: 'Templates', items: templateMatches });
      }

      // Search pages (limit 3) - filter by permission
      const pageMatches = searchablePages
        .filter(p => {
          // Check permission if required
          if (p.permission && !hasPermission(p.permission)) return false;
          return p.name.toLowerCase().includes(q) || p.keywords.some(k => k.includes(q));
        })
        .slice(0, 3)
        .map(p => ({
          type: 'page',
          id: p.id,
          title: p.name,
          subtitle: 'Page',
          icon: p.icon,
          data: p
        }));
      if (pageMatches.length > 0) {
        results.push({ category: 'Pages', items: pageMatches });
      }

      // Search settings (limit 3) - filter by permission
      const settingMatches = searchableSettings
        .filter(s => {
          if (s.permission && !hasPermission(s.permission)) return false;
          return s.name.toLowerCase().includes(q) || s.keywords.some(k => k.includes(q));
        })
        .slice(0, 3)
        .map(s => ({
          type: 'setting',
          id: s.page,
          title: s.name,
          subtitle: `In ${s.page}`,
          icon: 'fa-gear',
          data: s
        }));
      if (settingMatches.length > 0) {
        results.push({ category: 'Settings', items: settingMatches });
      }

      // Search automod rules (limit 4) - only if user has permission
      if (hasPermission('moderex.automod') || hasPermission('moderex.automod.view')) {
        const automodMatches = (state.automodRules || [])
          .filter(r => r.name?.toLowerCase().includes(q) || r.id?.toLowerCase().includes(q))
          .slice(0, 4)
          .map(r => ({
            type: 'automod',
            id: r.id,
            title: r.name || r.id,
            subtitle: r.enabled ? 'Enabled' : 'Disabled',
            icon: 'fa-robot',
            data: r
          }));
        if (automodMatches.length > 0) {
          results.push({ category: 'Automod Rules', items: automodMatches });
        }
      }

      // Search anticheat checks (limit 4) - only if user has permission
      if (hasPermission('moderex.anticheat.configure') || hasPermission('moderex.anticheat.view')) {
        const anticheatChecks = [];
        (state.anticheat?.anticheats || []).forEach(ac => {
          (ac.checks || []).forEach(check => {
            if (check.name?.toLowerCase().includes(q) || check.displayName?.toLowerCase().includes(q)) {
              anticheatChecks.push({
                type: 'anticheat_check',
                id: `${ac.name}:${check.name}`,
                title: check.displayName || check.name,
                subtitle: `${ac.name} - ${check.category || 'Check'}`,
                icon: 'fa-shield-halved',
                data: { anticheat: ac.name, check }
              });
            }
          });
        });
        if (anticheatChecks.length > 0) {
          results.push({ category: 'Anticheat Checks', items: anticheatChecks.slice(0, 4) });
        }
      }

      // Search developer checklist items (limit 5)
      const checklistMatches = (state.devChecklist || [])
        .filter(item => {
          const title = (item.title || '').toLowerCase();
          const desc = (item.description || '').toLowerCase();
          const category = (item.category || '').toLowerCase();
          return title.includes(q) || desc.includes(q) || category.includes(q);
        })
        .slice(0, 5)
        .map(item => ({
          type: 'checklist',
          id: item.id,
          title: item.title,
          subtitle: `${item.category || 'Uncategorized'} - ${item.checked ? 'Completed' : 'Pending'}`,
          icon: item.checked ? 'fa-check-circle' : 'fa-circle',
          data: item
        }));
      if (checklistMatches.length > 0) {
        results.push({ category: 'Developer Checklist', items: checklistMatches });
      }

      // Flatten results for keyboard navigation
      searchState.results = results.flatMap(r => r.items);
      searchState.selectedIndex = searchState.results.length > 0 ? 0 : -1;

      renderSearchResults(results, query);
    }, 100);
  }

  function renderSearchResults(results, query) {
    const dropdown = document.getElementById('searchDropdown');
    const container = document.getElementById('searchResults');
    if (!dropdown || !container) return;

    if (results.length === 0) {
      container.innerHTML = `
        <div class="gsearch-empty">
          <i class="fa-solid fa-magnifying-glass"></i>
          <p>No results found for "${escapeHtml(query)}"</p>
        </div>
      `;
      dropdown.style.display = 'block';
      return;
    }

    let html = '';
    let globalIndex = 0;

    for (const category of results) {
      html += `<div class="gsearch-category">
        <div class="gsearch-category-title">${category.category}</div>`;

      for (const item of category.items) {
        const isSelected = globalIndex === searchState.selectedIndex;
        const highlightedTitle = highlightMatch(item.title, query);

        html += `
          <div class="gsearch-item ${isSelected ? 'selected' : ''}"
               data-index="${globalIndex}"
               onclick="selectSearchResultByIndex(${globalIndex})">
            <div class="gsearch-item-icon ${item.type}">
              <i class="fa-solid ${item.icon}"></i>
            </div>
            <div class="gsearch-item-content">
              <div class="gsearch-item-title">${highlightedTitle}</div>
              <div class="gsearch-item-subtitle">${escapeHtml(item.subtitle)}</div>
            </div>
          </div>
        `;
        globalIndex++;
      }

      html += '</div>';
    }

    container.innerHTML = html;
    dropdown.style.display = 'block';
  }

  function highlightMatch(text, query) {
    const escaped = escapeHtml(text);
    const q = query.toLowerCase();
    const idx = text.toLowerCase().indexOf(q);
    if (idx === -1) return escaped;

    const before = escapeHtml(text.substring(0, idx));
    const match = escapeHtml(text.substring(idx, idx + query.length));
    const after = escapeHtml(text.substring(idx + query.length));
    return `${before}<mark>${match}</mark>${after}`;
  }

  function navigateSearchResults(direction) {
    if (searchState.results.length === 0) return;

    searchState.selectedIndex += direction;
    if (searchState.selectedIndex < 0) {
      searchState.selectedIndex = searchState.results.length - 1;
    } else if (searchState.selectedIndex >= searchState.results.length) {
      searchState.selectedIndex = 0;
    }

    // Update UI
    const container = document.getElementById('searchResults');
    if (!container) return;

    container.querySelectorAll('.gsearch-item').forEach((el, idx) => {
      el.classList.toggle('selected', idx === searchState.selectedIndex);
    });

    // Scroll into view
    const selected = container.querySelector('.gsearch-item.selected');
    if (selected) {
      selected.scrollIntoView({ block: 'nearest' });
    }
  }

  window.selectSearchResultByIndex = function(index) {
    if (index >= 0 && index < searchState.results.length) {
      selectSearchResult(searchState.results[index]);
      hideSearchDropdown();
    }
  };

  function selectSearchResult(result) {
    if (!result) return;

    switch (result.type) {
      case 'player':
        go('players');
        setTimeout(() => openDrawer(result.id), 100);
        break;
      case 'case':
        go('punishments');
        setTimeout(() => openPunishmentDetails(result.id), 100);
        break;
      case 'template':
        go('templates');
        setTimeout(() => {
          // Try to scroll to the template
          const templateEl = document.querySelector(`[data-template-id="${result.id}"]`);
          if (templateEl) {
            templateEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            templateEl.classList.add('highlight-flash');
            setTimeout(() => templateEl.classList.remove('highlight-flash'), 2000);
          }
        }, 150);
        break;
      case 'page':
        go(result.id);
        break;
      case 'setting':
        go(result.data.page);
        setTimeout(() => {
          // Try to scroll to the setting element
          const elementId = result.data.elementId;
          if (elementId) {
            const el = document.getElementById(elementId);
            if (el) {
              el.scrollIntoView({ behavior: 'smooth', block: 'center' });
              el.classList.add('highlight-flash');
              setTimeout(() => el.classList.remove('highlight-flash'), 2000);
            }
          }
        }, 150);
        break;
      case 'automod':
        go('automod');
        setTimeout(() => {
          // Try to scroll to the rule
          const ruleEl = document.querySelector(`[data-rule-id="${result.id}"]`);
          if (ruleEl) {
            ruleEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            // Wait for scroll to complete before adding glow
            setTimeout(() => {
              ruleEl.classList.add('highlight-flash');
              setTimeout(() => ruleEl.classList.remove('highlight-flash'), 3500);
            }, 400);
          }
        }, 150);
        break;
      case 'anticheat_check':
        go('anticheat');
        setTimeout(() => {
          // Try to scroll to the check
          const checkName = result.data?.check?.name;
          const acName = result.data?.anticheat;
          if (checkName && acName) {
            const checkEl = document.querySelector(`[data-check="${checkName}"][data-ac="${acName}"]`);
            if (checkEl) {
              checkEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
              // Wait for scroll to complete before adding glow
              setTimeout(() => {
                checkEl.classList.add('highlight-flash');
                setTimeout(() => checkEl.classList.remove('highlight-flash'), 3500);
              }, 400);
            }
          }
        }, 150);
        break;
      case 'checklist':
        // Navigate to developer tools page and highlight the checklist item
        if (state.settings?.developerMode) {
          go('devtools');
          setTimeout(() => {
            const itemEl = document.querySelector(`[data-checklist-id="${result.id}"]`);
            if (itemEl) {
              itemEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
              // Wait for scroll to complete before adding glow
              setTimeout(() => {
                itemEl.classList.add('highlight-flash');
                setTimeout(() => itemEl.classList.remove('highlight-flash'), 3500);
              }, 400);
            }
          }, 150);
        } else {
          toast('info', 'Developer Mode Required', 'Enable Developer Mode in Settings to view checklist');
        }
        break;
    }

    // Clear search
    const searchInput = document.getElementById('globalSearch');
    if (searchInput) searchInput.value = '';
  }

  function hideSearchDropdown() {
    const dropdown = document.getElementById('searchDropdown');
    if (dropdown) dropdown.style.display = 'none';
    searchState.results = [];
    searchState.selectedIndex = -1;
  }

  function performGlobalSearch(query) {
    if (!query) return;
    const q = query.toLowerCase();

    // Check for MX-<caseId> format (e.g., MX-123)
    const mxMatch = query.match(/^mx-?(\d+)$/i);
    if (mxMatch) {
      const caseNum = mxMatch[1];
      const pun = state.punishments.find(p => p.caseId && p.caseId.toUpperCase().includes('MX-' + caseNum));
      if (pun) {
        go('punishments');
        setTimeout(() => openPunishmentDetails(pun.id), 100);
      } else {
        toast('info', 'Not Found', 'No punishment found with case ID MX-' + caseNum);
      }
      return;
    }

    // Check for filter prefixes
    if (q.startsWith('punishment:') || q.startsWith('case:')) {
      const caseId = q.split(':')[1].trim().toUpperCase();
      const pun = state.punishments.find(p => p.caseId && p.caseId.toUpperCase().includes(caseId));
      if (pun) {
        go('punishments');
        setTimeout(() => openPunishmentDetails(pun.id), 100);
      } else {
        toast('info', 'Not Found', 'No punishment found with that case ID');
      }
      return;
    }

    if (q.startsWith('player:')) {
      const playerName = q.split(':')[1].trim();
      const p = state.players.find(x => x.name.toLowerCase().includes(playerName));
      if (p) {
        go('players');
        setTimeout(() => openDrawer(p.id), 100);
      } else {
        toast('info', 'Not Found', 'No player found with that name');
      }
      return;
    }

    if (q.startsWith('setting:') || q.startsWith('action:')) {
      const settingName = q.split(':')[1].trim();
      if (settingName.includes('sound') || settingName.includes('audio')) {
        go('my-settings');
        toast('info', 'Sound Settings', 'Scroll to Sound Settings section');
      } else if (settingName.includes('sign') || settingName.includes('device') || settingName.includes('trust')) {
        go('my-settings');
        toast('info', 'Device Trust', 'Scroll to Auto Sign-in section');
      } else {
        go('actions');
      }
      return;
    }

    if (q.startsWith('template:')) {
      const templateName = q.split(':')[1].trim();
      const t = state.templates.find(x => x.name.toLowerCase().includes(templateName));
      if (t) {
        go('templates');
        toast('info', 'Template Found', t.name);
      } else {
        toast('info', 'Not Found', 'No template found with that name');
      }
      return;
    }

    // No filter - search everything
    // Try players first
    const player = state.players.find(x => x.name.toLowerCase().includes(q));
    if (player) {
      go('players');
      setTimeout(() => openDrawer(player.id), 100);
      return;
    }

    // Try punishments by case ID
    const punishment = state.punishments.find(p => p.caseId && p.caseId.toLowerCase().includes(q));
    if (punishment) {
      go('punishments');
      setTimeout(() => openPunishmentDetails(punishment.id), 100);
      return;
    }

    // Try templates
    const template = state.templates.find(t => t.name.toLowerCase().includes(q));
    if (template) {
      go('templates');
      toast('info', 'Template Found', template.name);
      return;
    }

    // Nothing found
    toast('info', 'Not Found', 'No results found for: ' + query);
  }

  // ===== WEBSOCKET INTEGRATION =====
  let isLiveMode = false;

  function setupWebSocketHandlers() {
    const ws = window.MX.ws;
    if (!ws) return;

    // Handle authenticated event from auth.js
    window.addEventListener('mx:authenticated', (e) => {
      isLiveMode = true;
      state.authenticated = true;

      const session = e.detail;
      state.currentUser = {
        name: session.playerName || session.username,
        uuid: session.playerUuid,
        platform: 'Java',
        prefix: session.prefix || '',
        suffix: session.suffix || '',
        connectedAt: now(),
        rank: session.rank || null  // LuckPerms rank info: { name, weight, prefix, color }
      };
      state.staffName = session.playerName || session.username;
      state.notifications = state.notifications || [];
      ui.renderTopUser();
      updateNotificationCount();

      // Update server name in sidebar
      const serverNameText = document.getElementById('serverNameText');
      if (serverNameText && session.serverName) {
        serverNameText.textContent = session.serverName;
      }

      // Request initial data from server
      ws.requestPlayers();
      ws.requestPunishments();
      ws.requestWatchlist();
      ws.requestAutomodRules();
      ws.requestTemplates();
      ws.requestUserSettings();
      ws.requestChatStatus();
      ws.requestServerSettings();
      ws.requestAnticheatAlerts();
      // Also request integration info for Settings page
      ws.send('GET_ANTICHEAT_INFO');
      ws.send('GET_GEYSER_STATUS');

      ui.renderAll();
      hideDisconnect();

      // Start update checker after authentication
      setTimeout(() => {
        if (window.loadCurrentPluginVersion) {
          console.log('[Update] Starting update checker after authentication');
          window.loadCurrentPluginVersion();
        }
      }, 1000);

      // Start permission auto-refresh (15 second interval)
      startPermissionRefresh();

      // Show "Servers" dropdown button if in gateway mode
      if (ws.isGatewayMode()) {
        const serversBtn = document.getElementById('serversDropdownItem');
        if (serversBtn) serversBtn.style.display = '';
      }
    });

    // Handle disconnect
    ws.on('disconnected', (data) => {
      // Stop permission refresh on disconnect
      stopPermissionRefresh();

      if (isLiveMode && state.authenticated) {
        // Show disconnect overlay only if we were previously authenticated
        const serverName = document.getElementById('serverNameText')?.textContent || 'Server';
        showDisconnect(serverName);
      }
    });

    // Handle successful connection (including reconnects)
    ws.on('connected', (data) => {
      // Hide server offline overlay if showing
      hideServerOffline();

      // Hide disconnect overlay if showing
      const disconnectOverlay = document.getElementById('disconnectOverlay');
      if (disconnectOverlay) disconnectOverlay.classList.remove('show');

      // Clear any pending disconnect timeout
      if (disconnectTimeout) {
        clearTimeout(disconnectTimeout);
        disconnectTimeout = null;
      }
    });

    // Handle server coming back online via gateway
    ws.on('server_online', () => {
      hideServerOffline();
      hideDisconnect();
    });

    // Handle reconnect attempt updates (for status display)
    ws.on('reconnect_attempt', (data) => {
      const seconds = Math.ceil(data.delay / 1000);
      updateOfflineStatus(`Attempt ${data.attempt} - retrying in ${seconds}s...`);
    });

    // Handle reconnect failure (max attempts reached)
    ws.on('reconnect_failed', (data) => {
      updateOfflineStatus(`Failed after ${data.attempts} attempts`);
      // Optionally show the regular disconnect overlay with reconnect button
      hideServerOffline();
      const overlay = document.getElementById('disconnectOverlay');
      const nameEl = document.getElementById('disconnectServerName');
      if (nameEl) nameEl.textContent = serverNameForDisconnect || 'Server';
      if (overlay) overlay.classList.add('show');
    });

    // Handle panel version response (for gateway mode)
    ws.on('PANEL_VERSION', (data) => {
      if (typeof handlePanelVersionData === 'function') {
        handlePanelVersionData(data);
      }
    });

    // Handle evidence upload response (for gateway mode)
    ws.on('EVIDENCE_UPLOADED', (data) => {
      console.log('[Evidence] Upload successful:', data.evidenceId, data.fileName);
      // Emit event for any listeners
      window.dispatchEvent(new CustomEvent('mx:evidence_uploaded', { detail: data }));
    });

    // Handle evidence file response (for gateway mode)
    ws.on('EVIDENCE_FILE', (data) => {
      console.log('[Evidence] Received file:', data.fileId, data.mimeType, data.fileSize, 'bytes');
      // Emit event for any listeners
      window.dispatchEvent(new CustomEvent('mx:evidence_file', { detail: data }));
    });

    // Handle player data
    ws.on('PLAYERS_DATA', (data) => {
      if (!isLiveMode) return;
      state.players = (data.players || []).map(p => ({
        id: p.uuid || uid('p'),
        uuid: p.uuid,
        name: p.name,
        ip: p.ip || '',
        platform: p.geyser ? 'Bedrock' : 'Java',
        geyser: p.geyser || false,
        status: p.online ? 'online' : 'offline',
        lastSeen: p.lastJoin || now(),
        flags: 0,
        warnings: p.warnings || 0,
        recentCommands: p.recentCommands || [],
        notes: ''
      }));
      ui.renderPlayers();
      ui.renderDashboard();
    });

    // Handle player details (command history, automod flags)
    ws.on('PLAYER_DETAILS', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.uuid || p.id === data.uuid);
      if (player) {
        // Update recent commands if provided
        if (data.recentCommands && data.recentCommands.length > 0) {
          player.recentCommands = data.recentCommands;
        }
        // Store automod flags for this player
        if (data.automodFlags) {
          player.automodFlags = data.automodFlags;
        }
        // Re-render drawer if it's currently showing this player
        if (state.selectedPlayerId === player.id) {
          refreshDrawerCommands(player);
        }
      }
    });

    // Handle punishments data
    ws.on('PUNISHMENTS_DATA', (data) => {
      if (!isLiveMode) return;
      state.punishments = (data.punishments || []).map(p => ({
        id: p.caseId,
        playerId: p.playerUuid,
        type: p.type,
        reason: p.reason,
        duration: p.duration || '',
        staff: p.staffName,
        createdAt: p.createdAt,
        expiresAt: p.expiresAt,
        active: p.active,
        revoked: !!p.removedAt,
        evidence: p.evidence || []
      }));
      ui.renderPunishments();
      ui.renderDashboard();
    });

    // Handle watchlist data
    ws.on('WATCHLIST_DATA', (data) => {
      if (!isLiveMode) return;
      state.watchlist = new Set((data.watchlist || []).map(w => w.playerUuid));
      ui.renderWatchlist();
      ui.renderDashboard();
    });

    // Handle watchlist updates (real-time sync when watchlist changes)
    ws.on('WATCHLIST_UPDATE', (data) => {
      if (!isLiveMode) return;
      state.watchlist = new Set((data.watchlist || []).map(w => w.playerUuid));
      ui.renderWatchlist();
      ui.renderDashboard();
      // Also re-render players if on that page to show updated watchlist status
      if (state.currentPage === 'players') ui.renderPlayers();
    });

    // Handle admin announcements from ModereX admin panel
    ws.on('ADMIN_ANNOUNCEMENT', (data) => {
      console.log('[App] Received admin announcement:', data);
      showAdminAnnouncement(data);
    });

    // Handle database debug responses
    ws.on('DATABASE_DEBUG_RESPONSE', (data) => {
      if (!isLiveMode) return;
      if (window.handleDatabaseDebugResponse) {
        window.handleDatabaseDebugResponse(data);
      }
    });

    // Handle templates from server
    ws.on('TEMPLATES', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Templates] Received TEMPLATES:', data);
      // Keep the "none" option and add server templates
      const noneTemplate = { id: 'none', name: 'None', type: 'WARN', duration: '', reason: '' };
      // Backend sends templates as array directly (via data property extraction in websocket.js)
      // or as { templates: [...] } object
      let templates = [];
      if (Array.isArray(data)) {
        templates = data;
      } else if (data.templates && Array.isArray(data.templates)) {
        templates = data.templates;
      } else if (data.data && Array.isArray(data.data)) {
        templates = data.data;
      }
      state.templates = [noneTemplate, ...templates];
      ui.renderTemplates();
    });

    // Handle template created confirmation
    ws.on('TEMPLATE_CREATED', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Templates] Template created:', data);
      toast('ok', 'Created', data.name || 'Template created');
    });

    // Handle template updated confirmation
    ws.on('TEMPLATE_UPDATED', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Templates] Template updated:', data);
      toast('ok', 'Updated', data.name || 'Template updated');
    });

    // Handle template deleted confirmation
    ws.on('TEMPLATE_DELETED', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Templates] Template deleted:', data);
      toast('ok', 'Deleted', 'Template removed');
    });

    // Handle template favorite toggle
    ws.on('TEMPLATE_FAVORITE_TOGGLED', (data) => {
      const t = state.templates.find(x => x.id === data.id);
      if (t) {
        t.favorite = data.favorite;
        ui.renderTemplates();
      }
    });

    // Handle template errors
    ws.on('TEMPLATE_ERROR', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.error('[Templates] Error:', data);
      toast('warn', 'Error', data.message || 'Template operation failed');
    });

    // Handle command blacklist entries from server
    ws.on('CMD_BLACKLIST_ENTRIES', (data) => {
      if (!isLiveMode) return;
      state.cmdBlacklist = data.entries || [];
      if (window.renderCmdBlacklist) renderCmdBlacklist();
    });

    // Handle replay list from server
    ws.on('REPLAY_LIST', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Replays] Received REPLAY_LIST:', data);
      state.replays = data.replays || [];
      renderReplayList();
      updateReplayStats();
    });

    // Handle single replay data (opens 3D viewer modal)
    ws.on('REPLAY_DATA', (data) => {
      if (!isLiveMode) return;
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Replays] Received REPLAY_DATA:', data);
      if (data.replay) {
        // Attach hasChunkData to replay object for the viewer
        data.replay.hasChunkData = !!data.hasChunkData;
        openReplayDetailsModal(data.replay, data.snapshots || [], data.blockLogs || []);
      }
    });

    // Handle chunk terrain data for 3D replay viewer
    ws.on('REPLAY_CHUNKS', (data) => {
      if (!isLiveMode) return;
      console.log('[Replays] Received REPLAY_CHUNKS:', data.sessionId, 'size:', data.sizeBytes);
      handleReplayChunks(data);
    });

    // Handle replay update (real-time sync)
    ws.on('REPLAY_UPDATE', (data) => {
      if (!isLiveMode) return;
      console.log('[Replays] Replay updated:', data);
      // Update or add replay in state
      const idx = state.replays.findIndex(r => r.sessionId === data.sessionId || r.id === data.id);
      if (idx >= 0) {
        state.replays[idx] = { ...state.replays[idx], ...data };
      } else {
        state.replays.unshift(data);
      }
      renderReplayList();
      updateReplayStats();
    });

    // Handle replay deleted
    ws.on('REPLAY_DELETED', (data) => {
      if (!isLiveMode) return;
      console.log('[Replays] Replay deleted:', data);
      state.replays = state.replays.filter(r => r.sessionId !== data.sessionId && r.id !== data.id);
      renderReplayList();
      updateReplayStats();
      toast('ok', 'Deleted', 'Replay removed');
    });

    // Handle replay settings
    ws.on('REPLAY_SETTINGS', (data) => {
      if (!isLiveMode) return;
      console.log('[Replays] Settings received:', data);
      loadReplaySettings(data);
    });

    // Handle automod rules
    ws.on('AUTOMOD_RULES_DATA', (data) => {
      if (!isLiveMode) return;
      // Hide loading bar when rules data received
      if (window.hideLoadingLine) window.hideLoadingLine();
      console.log('[Automod] Received AUTOMOD_RULES_DATA:', data);
      // Only replace rules if server sent actual rules, otherwise keep defaults
      if (data.rules && data.rules.length > 0) {
        // Process rules to populate anticheatName and checkName for anticheat rules
        state.rules = data.rules.map(rule => {
          if (rule.id && rule.id.startsWith('ac_') && (!rule.anticheatName || !rule.checkName)) {
            // Parse anticheat name and check name from ID: ac_grim_badpacketsa -> grim, badpacketsa
            const parts = rule.id.substring(3).split('_');
            if (parts.length >= 2) {
              const acName = parts[0];
              const checkName = parts.slice(1).join('_');
              return {
                ...rule,
                anticheatName: rule.anticheatName || acName.charAt(0).toUpperCase() + acName.slice(1),
                checkName: rule.checkName || checkName
              };
            }
          }
          return rule;
        });
        console.log('[Automod] Updated state.rules with', state.rules.length, 'rules');
        // Debug: Log first custom rule to check data structure
        const customRule = state.rules.find(r => r.type === 'WORD_FILTER');
        if (customRule) {
          console.log('[Automod] Sample WORD_FILTER rule:', JSON.stringify(customRule, null, 2));
        }
      }
      ui.renderRules();
    });

    // Handle single rule update (real-time sync)
    ws.on('AUTOMOD_RULE_UPDATED', (data) => {
      if (!isLiveMode) return;
      // Hide loading bar when rule update confirmed
      if (window.hideLoadingLine) window.hideLoadingLine();
      if (window.debugLog) window.debugLog('DB', 'Rule updated in database: ' + data.id, 'success');
      console.log('[Automod] Received AUTOMOD_RULE_UPDATED:', data);

      // Populate anticheatName and checkName if this is an anticheat rule
      let updatedData = data;
      if (data.id && data.id.startsWith('ac_') && (!data.anticheatName || !data.checkName)) {
        const parts = data.id.substring(3).split('_');
        if (parts.length >= 2) {
          const acName = parts[0];
          const checkName = parts.slice(1).join('_');
          updatedData = {
            ...data,
            anticheatName: data.anticheatName || acName.charAt(0).toUpperCase() + acName.slice(1),
            checkName: data.checkName || checkName
          };
        }
      }

      const idx = state.rules.findIndex(r => r.id === updatedData.id);
      if (idx !== -1) {
        state.rules[idx] = { ...state.rules[idx], ...updatedData };
      } else {
        state.rules.push(updatedData);
      }
      ui.renderRules();
    });

    // Handle new rule created (real-time sync)
    ws.on('AUTOMOD_RULE_CREATED', (data) => {
      if (!isLiveMode) return;
      // Hide loading bar
      if (window.hideLoadingLine) window.hideLoadingLine();
      if (window.debugLog) window.debugLog('DB', 'Rule created in database: ' + data.id, 'success');
      console.log('[Automod] Received AUTOMOD_RULE_CREATED:', data);

      // Check if we should open editor for new rule
      const shouldOpenEditor = window._pendingNewRuleEditor;
      window._pendingNewRuleEditor = false;

      // Check if we have a pending rule create with a temp ID (legacy path)
      if (window._pendingRuleCreate) {
        const { tempId, rule } = window._pendingRuleCreate;
        console.log('[Automod] Mapping temp ID', tempId, 'to server ID', data.id);
        const localRule = state.rules.find(r => r.id === tempId);
        if (localRule) {
          localRule.id = data.id;
          console.log('[Automod] Updated local rule ID from', tempId, 'to', data.id);
        }
        window._pendingRuleCreate = null;
        ui.renderRules();
        return;
      }

      // Build full rule object with defaults
      const newRule = {
        id: data.id,
        name: data.name || 'New Rule',
        type: data.type || 'WORD_FILTER',
        enabled: data.enabled !== false,
        builtIn: false,
        priority: data.priority || 100,
        exactMatch: false,
        conditions: [{ kind: 'contains', value: '', match: 'contains' }],
        action: { kind: 'none', extra: '', duration: '' },
        threshold: { hits: 1, windowMins: 10 },
        blacklistedPhrases: [],
        blacklistedWords: [],
        exclusionWords: [],
        exceptions: [],
        ...data
      };

      // Add to state if not already present
      if (!state.rules.find(r => r.id === data.id)) {
        state.rules.unshift(newRule); // Add to beginning
        state.rulesPage = 1; // Go to first page to see new rule
      }

      ui.renderRules();

      // Check if this was created by us using the 'by' field from server
      const createdByMe = data.by && data.by === state.staffName;

      // Open editor if this was a new rule creation from addRuleUI
      if (shouldOpenEditor || createdByMe) {
        // Open editor after a short delay to ensure state is fully updated
        setTimeout(() => {
          console.log('[Automod] Opening editor for new rule:', data.id);
          console.log('[Automod] Rule in state:', state.rules.find(r => r.id === data.id));
          if (window.openAutomodRuleEditor) {
            window.openAutomodRuleEditor(data.id);
          } else {
            console.error('[Automod] openAutomodRuleEditor not available');
            toast('error', 'Error', 'Could not open rule editor');
          }
        }, 100);
        // Show toast only for our own creations
        toast('ok', 'Rule Created', `${data.name || 'New rule'} created`, {
          ttl: 6000,
          onClick: () => {
            if (window.openAutomodRuleEditor) {
              window.openAutomodRuleEditor(data.id);
            }
          }
        });
      }
      // For rules created by others, silently update UI (no toast to avoid spam)
    });

    // Handle rule deleted (real-time sync)
    ws.on('AUTOMOD_RULE_DELETED', (data) => {
      if (!isLiveMode) return;
      // Backend sends either ruleId or id
      const deletedId = data.ruleId || data.id;
      state.rules = state.rules.filter(r => r.id !== deletedId);
      ui.renderRules();
      // Only show toast if deleted by someone else (not us)
      if (data.by && data.by !== state.staffName) {
        toast('info', 'Automod', `Rule deleted by ${data.by}`);
      }
    });

    // Handle user settings
    ws.on('USER_SETTINGS_DATA', (data) => {
      if (!isLiveMode) return;
      state.settings.watchToasts = data.watchlistToasts ?? true;
      state.settings.soundEnabled = data.soundEnabled ?? true;
      state.settings.deviceTrustEnabled = data.deviceTrustEnabled ?? false;

      // Store staff notification settings for sync
      state.staffSettings = state.staffSettings || {};
      state.staffSettings.commandAlerts = data.commandAlerts ?? 'WATCHLIST_ONLY';
      state.staffSettings.showBlacklistedCommands = data.showBlacklistedCommands ?? true;
      state.staffSettings.anticheatAlertsLevel = data.anticheatAlertsLevel ?? 'EVERYONE';
      state.staffSettings.anticheatMinVL = data.anticheatMinVL ?? 10;
      state.staffSettings.automodAlertsLevel = data.automodAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.spamAlertsLevel = data.spamAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.filterAlertsLevel = data.filterAlertsLevel ?? 'WATCHLIST_ONLY';
      state.staffSettings.watchlistJoinAlerts = data.watchlistJoinAlerts ?? true;
      state.staffSettings.watchlistQuitAlerts = data.watchlistQuitAlerts ?? true;
      state.staffSettings.watchlistActivityAlerts = data.watchlistActivityAlerts ?? true;
      // joinLeaveMessages removed - now a global config setting
      state.staffSettings.staffChatEnabled = data.staffChatEnabled ?? true;
      state.staffSettings.staffChatSound = data.staffChatSound ?? true;
      state.staffSettings.banAlertsLevel = data.banAlertsLevel ?? 'EVERYONE';
      state.staffSettings.muteAlertsLevel = data.muteAlertsLevel ?? 'EVERYONE';
      state.staffSettings.kickAlertsLevel = data.kickAlertsLevel ?? 'EVERYONE';
      state.staffSettings.warnAlertsLevel = data.warnAlertsLevel ?? 'EVERYONE';

      // Helper to convert alert level to uppercase (DB may store as lowercase)
      // Handle cases where value might not be a string (gateway may send objects)
      const toUpper = (val, def) => {
        const v = val || def;
        if (typeof v === 'string') return v.toUpperCase().replace(/-/g, '_');
        if (v && typeof v === 'object' && v.value) return String(v.value).toUpperCase().replace(/-/g, '_');
        return String(def).toUpperCase().replace(/-/g, '_');
      };

      // Punishment alert levels (new system) - ensure uppercase for UI display
      state.staffSettings.banAlerts = toUpper(data.banAlerts, 'EVERYONE');
      state.staffSettings.kickAlerts = toUpper(data.kickAlerts, 'EVERYONE');
      state.staffSettings.muteAlerts = toUpper(data.muteAlerts, 'EVERYONE');
      state.staffSettings.warnAlerts = toUpper(data.warnAlerts, 'EVERYONE');
      state.staffSettings.pardonAlerts = toUpper(data.pardonAlerts, 'EVERYONE');

      // Other alert types
      state.staffSettings.automodAlerts = toUpper(data.automodAlerts, 'EVERYONE');
      state.staffSettings.anticheatAlerts = toUpper(data.anticheatAlerts, 'EVERYONE');
      state.staffSettings.nicknameAlerts = toUpper(data.nicknameAlerts, 'EVERYONE');
      state.staffSettings.commandAlerts = toUpper(data.commandAlerts, 'BLACKLISTED_ONLY');
      state.staffSettings.joinLeaveAlerts = toUpper(data.joinLeaveAlerts, 'EVERYONE');
      state.staffSettings.lagAlerts = data.lagAlerts ?? true;

      // Web panel notification modes
      state.staffSettings.webNotifyPunishments = data.webNotifyPunishments ?? 'toast';
      state.staffSettings.webNotifyAutomod = data.webNotifyAutomod ?? 'toast';
      state.staffSettings.webNotifyAnticheat = data.webNotifyAnticheat ?? 'toast';
      state.staffSettings.webNotifyWatchlist = data.webNotifyWatchlist ?? 'toast';
      state.staffSettings.webNotifyStaffChat = data.webNotifyStaffChat ?? 'toast';
      state.staffSettings.webNotifyCommands = data.webNotifyCommands ?? 'toast';
      state.staffSettings.webNotifyNickname = data.webNotifyNickname ?? 'toast';
      state.staffSettings.webNotifyLag = data.webNotifyLag ?? 'toast';

      // Web panel display settings - ensure uppercase for UI display
      const rawPosition = data.webToastPosition || 'TOP_RIGHT';
      state.staffSettings.webToastPosition = rawPosition.toUpperCase().replace(/-/g, '_');
      state.staffSettings.webAlertDurationSeconds = data.webAlertDurationSeconds ?? 10;

      // Web panel sound settings per alert type
      state.staffSettings.webSoundPunishments = data.webSoundPunishments ?? true;
      state.staffSettings.webSoundAutomod = data.webSoundAutomod ?? true;
      state.staffSettings.webSoundAnticheat = data.webSoundAnticheat ?? true;
      state.staffSettings.webSoundWatchlist = data.webSoundWatchlist ?? true;
      state.staffSettings.webSoundStaffChat = data.webSoundStaffChat ?? true;
      state.staffSettings.webSoundCommands = data.webSoundCommands ?? true;
      state.staffSettings.webSoundNickname = data.webSoundNickname ?? true;
      state.staffSettings.webSoundLag = data.webSoundLag ?? true;

      // Rate limiting settings
      state.staffSettings.alertRateLimitSeconds = data.alertRateLimitSeconds ?? 5;
      state.staffSettings.alertRateLimitMax = data.alertRateLimitMax ?? 3;

      // Update toast position (convert from DB format TOP_RIGHT to CSS format top-right)
      if (window.updateAlertToastPosition && data.webToastPosition) {
        const cssPosition = data.webToastPosition.toLowerCase().replace(/_/g, '-');
        window.updateAlertToastPosition(cssPosition);
        console.log('[USER_SETTINGS] Applied toast position:', data.webToastPosition, '->', cssPosition);
      }

      // Apply sound settings globally
      if (window.MX.sounds) {
        window.MX.sounds.setEnabled(data.soundEnabled ?? true);
      }

      // Handle changelog read status
      if (data.readChangelogs) {
        window.setReadChangelogs(data.readChangelogs);
        // Check for unread changelogs after a brief delay
        setTimeout(() => window.checkUnreadChangelogs(), 500);
      }

      // Store permissions array for UI permission checks
      if (data.permissions) {
        handlePermissionsUpdate(data.permissions);
        console.log('[USER_SETTINGS] Received permissions:', state.permissions);
        window.devtoolsLog('PERMISSIONS', `Received ${state.permissions.length} permissions from server`, 'success');
      } else {
        console.warn('[USER_SETTINGS] No permissions array received from server!');
        window.devtoolsLog('PERMISSIONS', 'WARNING: No permissions array in USER_SETTINGS_DATA', 'warn');
      }

      // Update all UI elements that depend on these settings
      updateSettingsUI();
      ui.renderWatchToastsToggle();
      ui.renderStaffSettings();
    });

    // Handle activity logs data (database-backed activity logs)
    ws.on('ACTIVITY_LOGS_DATA', (data) => {
      if (!isLiveMode) return;
      state.activityLogs.logs = data.logs || [];
      state.activityLogs.total = data.total || 0;
      state.activityLogs.page = data.page || 1;
      state.activityLogs.totalPages = data.totalPages || 1;
      state.activityLogs.allowedTypes = data.allowedTypes || [];

      // Initialize enabled types if not set
      if (Object.keys(state.activityLogs.filters.enabledTypes).length === 0) {
        for (const type of state.activityLogs.allowedTypes) {
          state.activityLogs.filters.enabledTypes[type] = true;
        }
      }

      renderActivityLogs();
    });

    // Handle evidence activity logs data (for punishment form evidence selector)
    ws.on('EVIDENCE_ACTIVITY_LOGS_DATA', (data) => {
      if (!isLiveMode) return;
      handleEvidenceActivityLogsData(data);
    });

    // Handle anticheat alerts data (list of all checks)
    ws.on('ANTICHEAT_ALERTS', (data) => {
      if (!isLiveMode) return;
      state.anticheat.anticheats = data.anticheats || [];
      // Also update integrations state so Settings page shows detected anticheats
      state.integrations = state.integrations || {};
      state.integrations.hookedAnticheats = (data.anticheats || []).map(ac => ({
        name: ac.name,
        alertsEnabled: true
      }));
      ui.renderAnticheat();
      ui.renderIntegrations();
    });

    // Handle staff alert preferences
    ws.on('STAFF_ALERT_PREFS', (data) => {
      if (!isLiveMode) return;
      // Convert prefs to flat map: "anticheat.checkName" -> pref
      const prefs = {};
      if (data.preferences) {
        Object.entries(data.preferences).forEach(([acName, acPrefs]) => {
          Object.entries(acPrefs).forEach(([checkName, pref]) => {
            prefs[`${acName}.${checkName}`] = pref;
          });
        });
      }
      state.anticheat.alertPrefs = prefs;
      ui.renderAnticheat();
    });

    // Handle alert presets
    ws.on('ALERT_PRESETS', (data) => {
      if (!isLiveMode) return;
      state.anticheat.presets = data.presets || [];
      ui.renderAnticheat();
    });

    // Handle alert pref update confirmation
    ws.on('STAFF_ALERT_PREF_UPDATED', (data) => {
      if (!isLiveMode) return;
      const prefKey = `${data.anticheat?.toLowerCase()}.${data.checkName}`;
      state.anticheat.alertPrefs[prefKey] = {
        alertLevel: data.alertLevel,
        thresholdCount: data.thresholdCount,
        timeWindowSeconds: data.timeWindowSeconds
      };
      window.debugLog('ANTICHEAT', `Saved: ${data.anticheat}:${data.checkName} (${data.alertLevel}, ${data.thresholdCount}/${data.timeWindowSeconds}s)`, 'success');
    });

    // Real-time broadcasts
    ws.on('PUNISHMENT_CREATED', (data) => {
      if (!isLiveMode) return;
      const pun = {
        id: data.caseId,
        playerId: data.playerUuid,
        type: data.type,
        reason: data.reason,
        duration: data.duration || '',
        staff: data.staffName,
        createdAt: data.createdAt,
        expiresAt: data.expiresAt,
        active: data.active,
        revoked: false
      };
      state.punishments.unshift(pun);
      state.activity.unshift({ t: now(), actor: data.staffName, action: `${data.type} issued`, target: data.playerName });
      ui.renderPunishments();
      ui.renderDashboard();

      // Check permission for alert notification based on punishment type
      const alertPermission = {
        'BAN': 'moderex.alerts.ban',
        'IPBAN': 'moderex.alerts.ban',
        'MUTE': 'moderex.alerts.mute',
        'KICK': 'moderex.alerts.kick',
        'WARN': 'moderex.alerts.warn'
      }[data.type];

      if (alertPermission && window.hasPermission && !window.hasPermission(alertPermission)) {
        return; // Skip alert if no permission, but still update UI above
      }

      // Format punishment type for display
      const typeDisplay = {
        'BAN': 'Banned',
        'MUTE': 'Muted',
        'KICK': 'Kicked',
        'WARN': 'Warned',
        'IPBAN': 'IP Banned'
      }[data.type] || data.type;

      // Build alert subtitle with reason and duration
      const duration = data.duration && data.duration !== 'Permanent' ? ` (${data.duration})` : data.duration === 'Permanent' ? ' (Permanent)' : '';
      const subtitle = `${data.reason || 'No reason'}${duration}`;

      showPanelAlert('punishments', `Player ${typeDisplay}: ${data.playerName}`, subtitle, {
        playerId: data.playerUuid,
        playerName: data.playerName,
        caseId: data.caseId,
        punishmentData: data
      });
      window.MX.sounds?.punishment();
    });

    ws.on('PUNISHMENT_REVOKED', (data) => {
      if (!isLiveMode) return;
      const pun = state.punishments.find(p => p.id === data.caseId);
      if (pun) {
        pun.active = false;
        pun.revoked = true;
      }
      ui.renderPunishments();

      // Check permission for pardon alerts
      if (window.hasPermission && !window.hasPermission('moderex.alerts.pardon')) return;

      showPanelAlert('punishments', `Punishment Revoked: ${data.caseId}`, `${pun?.type || 'Unknown'} for ${pun?.playerName || 'Unknown'} was revoked`, { caseId: data.caseId });
    });

    // Rate limiting state for watchlist alerts (by player/IP)
    const watchlistRateLimit = {
      alerts: new Map(), // playerUuid/IP -> { count, lastTime, timer }
      windowMs: 3000,    // 3 second window for aggregation
      updateIntervalMs: 2000 // Update UI every 2 seconds during high activity
    };

    ws.on('WATCHLIST_ALERT', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.alerts.watchlist permission
      if (window.hasPermission && !window.hasPermission('moderex.alerts.watchlist')) return;
      // Backend sends alertType, playerName, details, playerUuid, playerIp
      const alertType = data.alertType || data.type || 'Activity';
      const playerName = data.playerName || 'Unknown';
      const details = data.details || '';
      const playerKey = data.playerIp || data.playerUuid || playerName; // Use IP if available for rate limiting

      state.watchAlerts.push({
        type: alertType,
        details: details,
        playerName: playerName,
        playerUuid: data.playerUuid,
        playerIp: data.playerIp,
        t: now()
      });

      // Rate limiting logic - aggregate rapid alerts from same player/IP
      const currentTime = Date.now();
      let rateLimitEntry = watchlistRateLimit.alerts.get(playerKey);

      if (rateLimitEntry && (currentTime - rateLimitEntry.lastTime) < watchlistRateLimit.windowMs) {
        // Within rate limit window - increment count
        rateLimitEntry.count++;
        rateLimitEntry.lastTime = currentTime;
        rateLimitEntry.latestDetails = details;
        rateLimitEntry.latestType = alertType;

        // Clear existing timer and set new one
        if (rateLimitEntry.timer) clearTimeout(rateLimitEntry.timer);
        rateLimitEntry.timer = setTimeout(() => {
          // Show aggregated alert after window expires
          const entry = watchlistRateLimit.alerts.get(playerKey);
          if (entry && entry.count > 1) {
            const aggregatedDetails = `${entry.count} alerts in ${Math.round(watchlistRateLimit.windowMs / 1000)}s (latest: ${entry.latestDetails})`;
            showWatchlistAlertUI(entry.latestType, aggregatedDetails, playerName, data.playerUuid);
          }
          watchlistRateLimit.alerts.delete(playerKey);
        }, watchlistRateLimit.updateIntervalMs);

        // Don't show individual alert - will be aggregated
        ui.renderDashboard();
        return;
      }

      // First alert or outside window - show immediately
      watchlistRateLimit.alerts.set(playerKey, {
        count: 1,
        lastTime: currentTime,
        latestDetails: details,
        latestType: alertType,
        timer: setTimeout(() => {
          watchlistRateLimit.alerts.delete(playerKey);
        }, watchlistRateLimit.windowMs)
      });

      showWatchlistAlertUI(alertType, details, playerName, data.playerUuid);
      ui.renderDashboard();
    });

    // Helper to show watchlist alert UI (bar/toast based on settings)
    function showWatchlistAlertUI(alertType, details, playerName, playerUuid) {
      const settings = loadMySettings();
      const style = settings.watchlistStyle || 'bar';
      const playerData = { playerId: playerUuid, playerName: playerName };

      if (style === 'bar' || style === 'both') {
        showAlertBar('watchlist', alertType, details, playerData);
      }
      if (style === 'toast' || style === 'both') {
        showPanelAlert('watchlist', `Watchlist Alert: ${playerName}`, details, { playerId: playerUuid, playerName, severity: 'warn' });
      }
      window.MX.sounds?.watchlist();
    }

    ws.on('STAFFCHAT_MESSAGE', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.staffchat permission
      if (window.hasPermission && !window.hasPermission('moderex.staffchat')) return;

      logEvent('INFO', 'staffchat', `Staff | ${data.sender}`, data.message, { kind: 'staffchat' });

      // Add to staff chat panel (avoid duplicates for self messages)
      const isSelf = data.sender === state.currentUser?.name;
      if (!isSelf) {
        addStaffChatMessage({
          sender: data.sender,
          message: data.message,
          isWeb: data.sender.includes('[Web]'),
          isSelf: false,
          time: now()
        });
        showPanelAlert('staffChat', `Staff Chat: ${data.sender}`, data.message);
        window.MX.sounds?.staffChat();
      }
    });

    ws.on('STAFFCHAT_HISTORY', (data) => {
      // Handle staff chat history response
      staffChatLoading = false;
      staffChatInitialized = true;
      showStaffChatLoading(false);

      if (data.messages && Array.isArray(data.messages)) {
        prependStaffChatMessages(data.messages);
      }
    });

    ws.on('PLAYER_JOIN', (data) => {
      if (!isLiveMode) return;
      const existing = state.players.find(p => p.uuid === data.uuid);
      if (existing) {
        existing.status = 'online';
        existing.lastSeen = now();
      } else {
        state.players.unshift({
          id: data.uuid,
          uuid: data.uuid,
          name: data.name,
          ip: data.ip || '',
          platform: data.geyser ? 'Bedrock' : 'Java',
          geyser: data.geyser || false,
          status: 'online',
          lastSeen: now(),
          flags: 0,
          warnings: 0,
          recentCommands: [],
          notes: ''
        });
      }
      logEvent('INFO', 'join', 'Player Join', `${data.name} joined the server`, { kind: 'join', type: 'JOIN' });
      ui.renderPlayers();
      ui.renderDashboard();
    });

    ws.on('PLAYER_QUIT', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.uuid);
      if (player) {
        player.status = 'offline';
        player.lastSeen = now();
      }
      logEvent('INFO', 'leave', 'Player Quit', `${data.name} left the server`, { kind: 'leave', type: 'LEAVE' });
      ui.renderPlayers();
      ui.renderDashboard();
    });

    ws.on('CHAT_MESSAGE', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid || p.uuid === data.uuid);
      logEvent('INFO', 'chat', `Chat | ${data.playerName}`, data.message, { playerId: player?.id, kind: 'chat', type: 'CHAT' });
    });

    ws.on('COMMAND_EXECUTED', (data) => {
      if (!isLiveMode) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      if (player) {
        // Add to player's recent commands for drawer display
        if (!player.recentCommands) player.recentCommands = [];
        player.recentCommands.push({ cmd: data.command, t: data.timestamp || Date.now() });
        // Keep only last 100 commands in memory
        if (player.recentCommands.length > 100) player.recentCommands.shift();
      }
      logEvent('INFO', 'command', `Command | ${data.playerName}`, data.command, { playerId: player?.id, kind: 'command', type: 'COMMAND' });
    });

    ws.on('LOG_EVENT', (data) => {
      if (!isLiveMode) return;
      const player = data.playerUuid ? state.players.find(p => p.uuid === data.playerUuid) : null;
      logEvent(
        data.severity || 'INFO',
        data.logType || data.category || 'system',
        data.title,
        data.detail,
        {
          kind: data.logType || data.category || 'system',
          type: data.logType || 'SYSTEM',
          playerId: player?.id || data.playerUuid,
          caseId: data.caseId
        }
      );

      // Also show in debug log panel with proper formatting
      if (window.debugLog) {
        const category = data.category || data.logType || 'SYSTEM';
        const severity = data.severity || 'INFO';
        const debugType = severity === 'ERROR' ? 'error' : severity === 'WARN' ? 'warn' : severity === 'SUCCESS' ? 'success' : 'info';
        const message = data.detail || data.title || 'Unknown event';
        window.debugLog(category.toUpperCase(), message, debugType);
      }
    });

    ws.on('AUTOMOD_TRIGGER', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.history.automod permission
      if (window.hasPermission && !window.hasPermission('moderex.history.automod')) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      showPanelAlert('automod', `Automod Alert: ${data.playerName}`, `Triggered: ${data.rule} | "${data.message}"`, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
    });

    ws.on('AUTOMOD_TRIGGERED', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.history.automod permission
      if (window.hasPermission && !window.hasPermission('moderex.history.automod')) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      showPanelAlert('automod', `Automod Alert: ${data.playerName}`, `Triggered: ${data.rule} | "${data.message}"`, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
    });

    // Handle AUTOMOD_ALERT (from broadcastAutomodAlert in Java backend)
    ws.on('AUTOMOD_ALERT', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.history.automod permission
      if (window.hasPermission && !window.hasPermission('moderex.history.automod')) return;
      const player = state.players.find(p => p.uuid === data.playerUuid);
      logEvent('WARN', 'automod', `Automod | ${data.rule}`, `${data.playerName}: ${data.message}`, { playerId: player?.id, kind: 'automod', type: 'AUTOMOD' });
      showPanelAlert('automod', `Automod Alert: ${data.playerName}`, `Triggered: ${data.rule} | "${data.message}"`, { playerId: player?.id, playerName: data.playerName, severity: 'warn' });
    });

    ws.on('PRIVATE_MESSAGE', (data) => {
      if (!isLiveMode) return;
      const settings = loadMySettings();
      const player = state.players.find(p => p.uuid === data.senderUuid);
      const isWatchlisted = player && (state.watchlist?.has(player.id) || state.watchlist?.has(player.uuid));

      // Check PM alert level setting
      const pmLevel = settings.privateMessageAlerts || 'OFF';
      if (pmLevel === 'OFF') return;
      if (pmLevel === 'WATCHLIST_ONLY' && !isWatchlisted) return;

      // Log to activity feed
      const prefix = isWatchlisted ? '[WL] ' : '';
      logEvent('INFO', 'chat', `PM | ${prefix}${data.senderName} → ${data.targetName}`, data.message, { playerId: player?.id, kind: 'pm', type: 'CHAT' });

      // Show toast for watchlisted players
      if (isWatchlisted) {
        toast('warn', 'Private Message', `${data.senderName} → ${data.targetName}: ${data.message.substring(0, 50)}...`);
      }
    });

    ws.on('ANTICHEAT_ALERT', (data) => {
      if (!isLiveMode) return;
      // Check for moderex.alerts.anticheat permission
      if (window.hasPermission && !window.hasPermission('moderex.alerts.anticheat')) return;
      const player = state.players.find(p => p.name === data.playerName || p.uuid === data.playerUuid);

      // Get check name from various possible fields
      const checkName = data.checkName || data.check || data.checkType || 'Unknown';
      const vlLevel = data.vlLevel || data.vl || data.violations || 0;
      const anticheatName = (data.anticheat || 'Anticheat').charAt(0).toUpperCase() + (data.anticheat || 'anticheat').slice(1);

      // Always log the event with explicit type for filtering
      logEvent('WARN', 'anticheat', `${anticheatName} | ${checkName}`, `${data.playerName} (VL: ${vlLevel})`, { playerId: player?.id, kind: 'anticheat', type: 'ANTICHEAT' });

      // Check global anticheat alert setting FIRST
      const rawSetting = state.staffSettings?.anticheatAlerts;
      const globalAlertLevel = (rawSetting || 'EVERYONE').toUpperCase();
      console.log('[ANTICHEAT_ALERT] raw setting:', rawSetting, 'parsed:', globalAlertLevel, 'staffSettings:', state.staffSettings);
      if (globalAlertLevel === 'OFF') {
        console.log('[ANTICHEAT_ALERT] Blocked - global setting is OFF');
        return;
      }

      // Check if player is on watchlist
      const isWatchlisted = player && (state.watchlist?.has(player.id) || state.watchlist?.has(player.uuid));

      // If global setting is watchlist only, skip non-watchlisted players
      if (globalAlertLevel === 'WATCHLIST_ONLY' && !isWatchlisted) return;

      // Get per-check alert preference from staff settings (optional additional filtering)
      const anticheat = (data.anticheat || 'grim').toLowerCase();
      const prefKey = `${anticheat}.${checkName}`;
      const checkPref = state.anticheat?.alertPrefs?.[prefKey];

      // If per-check preference exists and is OFF, skip
      if (checkPref?.alertLevel === 'OFF') return;

      // If per-check is watchlist only and player not on watchlist, skip
      if (checkPref?.alertLevel === 'WATCHLIST_ONLY' && !isWatchlisted) return;

      // Show alert with live VL updates using alertToast with updateKey
      const alertKey = `ac_${data.playerUuid || data.playerName}_${checkName}`;
      showAnticheatAlertWithUpdates(alertKey, {
        playerName: data.playerName,
        playerId: player?.id || data.playerUuid,
        checkName,
        anticheat: anticheatName,
        vl: vlLevel
      });
    });

    // Custom alerts from /mx sendalert command
    ws.on('CUSTOM_ALERT', (data) => {
      console.log('[CUSTOM_ALERT] Received:', data);
      console.log('[CUSTOM_ALERT] isLiveMode:', isLiveMode);

      if (!isLiveMode) {
        console.log('[CUSTOM_ALERT] Not in live mode, ignoring');
        return;
      }

      // Try to find the player in state.players - use UUID as playerId for avatar
      const player = data.playerUuid ? state.players.find(p => p.uuid === data.playerUuid || p.id === data.playerUuid) : null;
      const playerId = data.playerUuid || player?.uuid || player?.id;

      console.log('[CUSTOM_ALERT] Player lookup:', { playerUuid: data.playerUuid, foundPlayer: player, playerId });

      // Log the event
      const category = data.category || 'custom';
      const eventType = category.toUpperCase();

      // Format title based on category
      const categoryTitles = {
        'ban': `Player Banned: ${data.playerName}`,
        'kick': `Player Kicked: ${data.playerName}`,
        'mute': `Player Muted: ${data.playerName}`,
        'warn': `Player Warned: ${data.playerName}`,
        'pardon': `Player Pardoned: ${data.playerName}`,
        'anticheat': `Anticheat Alert: ${data.playerName}`,
        'automod': `Automod Alert: ${data.playerName}`,
        'command': `Command Alert: ${data.playerName}`,
        'nickname': `Nickname Alert: ${data.playerName}`,
        'watchlist': `Watchlist Alert: ${data.playerName}`,
        'staffchat': `Staff Chat: ${data.playerName}`,
        'lag': 'Server Lag Alert'
      };

      const alertTitle = categoryTitles[category] || data.title || `${category.charAt(0).toUpperCase() + category.slice(1)} Alert: ${data.playerName}`;

      logEvent('WARN', category, alertTitle, data.message, { playerId: playerId, kind: category, type: eventType });

      // Show alert using panel notification settings
      showPanelAlert(category, alertTitle, data.message, {
        playerId: playerId,
        playerName: data.playerName,
        severity: 'warn'
      });

      // Sound is played in alertToast based on settings
    });

    ws.on('SERVER_STATUS', (data) => {
      if (!isLiveMode) return;
      const dot = dom().statusDot;
      const text = dom().statusText;
      if (dot) dot.className = 'dot ' + (data.online ? 'ok' : 'error');
      if (text) text.textContent = data.online ? 'Online' : 'Offline';
      // Server name is set on auth, don't override with version
      if (data.serverName) {
        const serverNameText = document.getElementById('serverNameText');
        if (serverNameText) serverNameText.textContent = data.serverName;
      }
    });

    ws.on('CHAT_STATUS', (data) => {
      if (!isLiveMode) return;
      state.settings.chatDisabled = !data.chatEnabled;
      state.settings.slowSeconds = data.slowmodeSeconds || 0;
      state.settings.slowEnabled = data.slowmodeSeconds > 0;
      ui.renderDashboard();
    });

    ws.on('SERVER_SETTINGS', (data) => {
      if (!isLiveMode) return;
      // Chat settings
      if (typeof data.chatEnabled !== 'undefined') state.settings.chatDisabled = !data.chatEnabled;
      if (typeof data.slowmodeSeconds !== 'undefined') {
        state.settings.slowSeconds = data.slowmodeSeconds || 0;
        state.settings.slowEnabled = data.slowmodeSeconds > 0;
      }
      // Mute settings
      if (data.muteSettings) {
        state.settings.muteChat = data.muteSettings.chat ?? true;
        state.settings.muteMsg = data.muteSettings.msg ?? true;
        state.settings.muteSigns = data.muteSettings.signs ?? true;
        state.settings.muteBooks = data.muteSettings.books ?? true;
        state.settings.muteBroadcast = data.muteSettings.broadcast ?? false;
        state.settings.muteVoice = data.muteSettings.voice ?? true;
        state.settings.muteVoiceJoin = data.muteSettings.voiceJoin ?? true;
        state.settings.muteStaffCanSee = data.muteSettings.staffCanSee ?? true;
        loadMuteSettings(data.muteSettings);
      }
      // Warn settings
      if (data.warnSettings) {
        state.settings.warnNotify = data.warnSettings.notify ?? true;
        state.settings.warnAutoEscalate = data.warnSettings.autoEscalate ?? false;
        loadWarnSettings(data.warnSettings);
      }
      // Anticheat settings
      if (data.anticheatSettings) {
        state.settings.anticheatReplace = data.anticheatSettings.rebrandAlerts ?? false;
      }
      // Activity log settings
      if (data.activityLogSettings) {
        loadActivityLogSettings(data.activityLogSettings);
      }
      // Evidence settings
      if (data.evidenceSettings) {
        loadEvidenceSettings(data.evidenceSettings);
      }
      ui.renderDashboard();
      ui.renderChatToggles();
    });

    // Integration status (Geyser, Floodgate, Citizens, Essentials)
    ws.on('GEYSER_STATUS', (data) => {
      if (!isLiveMode) return;
      state.integrations = state.integrations || {};
      state.integrations.geyserDetected = data.geyserAvailable;
      state.integrations.floodgateDetected = data.floodgateAvailable;
      state.integrations.citizensDetected = data.citizensAvailable;
      state.integrations.essentialsDetected = data.essentialsAvailable;
      state.integrations.geyserVersion = data.geyserVersion;
      state.integrations.floodgateVersion = data.floodgateVersion;
      state.integrations.citizensVersion = data.citizensVersion;
      state.integrations.essentialsVersion = data.essentialsVersion;
      ui.renderIntegrations();
    });

    // Developer Checklist
    ws.on('DEV_CHECKLIST', (data) => {
      state.devChecklist = data || [];
      renderDevChecklist();
    });

    ws.on('PLUGIN_UPDATE_RESULT', (data) => {
      const banner = document.getElementById('updateBanner');
      if (data.success) {
        toast('success', 'Update Downloaded', data.message || 'Restart the server to apply the update.');
        if (banner) {
          const btn = banner.querySelector('.btn.primary');
          if (btn) {
            btn.innerHTML = '<i class="fa-solid fa-check"></i> Restart Required';
            btn.disabled = true;
            btn.classList.remove('primary');
            btn.classList.add('success');
          }
          const titleEl = banner.querySelector('.update-title');
          if (titleEl) titleEl.textContent = 'Update Downloaded!';
          const notesEl = banner.querySelector('.update-notes');
          if (notesEl) notesEl.textContent = 'Restart the server to apply';
        }
      } else {
        toast('error', 'Update Failed', data.message || 'Failed to download update.');
        if (banner) {
          const btn = banner.querySelector('.btn.primary');
          if (btn) {
            btn.disabled = false;
            btn.innerHTML = '<i class="fa-solid fa-rotate"></i> Retry';
          }
        }
      }
    });
  }

  // Override functions to use WebSocket in live mode
  function wrapWithWebSocket() {
    const ws = window.MX.ws;
    if (!ws) return;

    // Override executePunishment to send to server
    const originalExecutePunishment = executePunishment;
    executePunishment = function(opts) {
      if (isLiveMode && ws.isConnected()) {
        const p = state.players.find(x => x.id === opts.playerId);
        ws.createPunishment({
          playerUuid: p?.uuid || opts.playerId,
          playerName: p?.name,
          type: opts.type,
          reason: opts.reason,
          duration: opts.duration
        });
        toast('info', 'Sending', `Creating ${opts.type.toLowerCase()}...`);
      } else {
        originalExecutePunishment(opts);
      }
    };
  }

  // ===== DISCONNECT OVERLAY =====
  let serverNameForDisconnect = 'Server';
  let disconnectTimeout = null;
  let reconnectCooldown = false;
  let lastPongTime = Date.now();

  function showDisconnect(serverName) {
    // In gateway mode, server offline is handled by auth.js — don't show disconnect overlay
    if (window.MX?.ws?.isGatewayMode?.()) return;

    serverNameForDisconnect = serverName || 'Server';

    // Clear any existing timeout
    if (disconnectTimeout) {
      clearTimeout(disconnectTimeout);
      disconnectTimeout = null;
    }

    // Delay showing disconnect/offline overlay by 5 seconds
    // This gives time for brief network hiccups to resolve
    disconnectTimeout = setTimeout(() => {
      // Check if this is a silent reconnect (auto-reconnect in progress)
      if (window.MX?.ws?.isSilentReconnect && window.MX.ws.isSilentReconnect()) {
        // Show server offline overlay (non-dismissable) instead of disconnect
        showServerOffline();
        // Don't play disconnect sound during silent reconnect
      } else {
        // Show normal disconnect overlay with reconnect button
        const overlay = document.getElementById('disconnectOverlay');
        const nameEl = document.getElementById('disconnectServerName');
        if (nameEl) nameEl.textContent = serverNameForDisconnect;
        if (overlay) overlay.classList.add('show');
        window.MX.sounds?.disconnect();
      }
      disconnectTimeout = null;
    }, 5000);
  }

  /**
   * Show the server offline overlay (non-dismissable)
   */
  function showServerOffline() {
    const offlineOverlay = document.getElementById('serverOfflineOverlay');
    const disconnectOverlay = document.getElementById('disconnectOverlay');

    // Hide disconnect overlay if showing
    if (disconnectOverlay) disconnectOverlay.classList.remove('show');

    // Show server offline overlay
    if (offlineOverlay) offlineOverlay.classList.add('show');
  }

  /**
   * Hide the server offline overlay
   */
  function hideServerOffline() {
    const offlineOverlay = document.getElementById('serverOfflineOverlay');
    if (offlineOverlay) offlineOverlay.classList.remove('show');
  }

  /**
   * Update the offline status text
   */
  function updateOfflineStatus(text) {
    const statusEl = document.getElementById('offlineReconnectStatus');
    if (statusEl) statusEl.textContent = text;
  }

  function hideDisconnect() {
    // Clear the delayed show timeout if connection restored
    if (disconnectTimeout) {
      clearTimeout(disconnectTimeout);
      disconnectTimeout = null;
    }
    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function attemptReconnect() {
    // Check cooldown - prevent rapid reconnect attempts
    if (reconnectCooldown) {
      window.MX.toast?.('warn', 'Please Wait', 'Reconnect cooldown active. Try again in a few seconds.');
      return;
    }

    // Start 5 second cooldown
    reconnectCooldown = true;
    const reconnectBtn = document.querySelector('#disconnectOverlay .btn-primary');
    if (reconnectBtn) {
      reconnectBtn.disabled = true;
      reconnectBtn.textContent = 'Reconnecting...';

      // Update button with countdown
      let countdown = 5;
      const countdownInterval = setInterval(() => {
        countdown--;
        if (countdown > 0) {
          reconnectBtn.textContent = `Wait ${countdown}s...`;
        } else {
          clearInterval(countdownInterval);
          reconnectBtn.disabled = false;
          reconnectBtn.textContent = 'Reconnect';
          reconnectCooldown = false;
        }
      }, 1000);
    } else {
      // Reset cooldown after 5 seconds if button not found
      setTimeout(() => { reconnectCooldown = false; }, 5000);
    }

    const overlay = document.getElementById('disconnectOverlay');
    if (overlay) overlay.classList.remove('show');
    window.MX.sounds?.reconnecting();
    if (window.MX.auth?.reconnect) {
      window.MX.auth.reconnect();
    } else {
      location.reload();
    }
  }

  // Track last pong time for disconnect detection
  function updateLastPong() {
    lastPongTime = Date.now();
  }

  // ===== LOADING LINE BAR (Progress-based) =====
  let loadingLineCount = 0;
  let loadingLineTimeout = null;
  let loadingLineFadeTimeout = null;
  let loadingAnimationFrame = null;
  let loadingStartTime = 0;
  let currentProgress = 0;

  function getLoadingElements() {
    return {
      line: document.getElementById('loadingLine'),
      fill: document.getElementById('loadingLineFill')
    };
  }

  function updateLoadingProgress(progress) {
    const { fill } = getLoadingElements();
    if (fill) {
      currentProgress = Math.min(100, Math.max(0, progress));
      fill.style.width = currentProgress + '%';
    }
  }

  // Animate progress smoothly toward target over time
  function animateProgress() {
    if (loadingLineCount === 0) return;

    const elapsed = Date.now() - loadingStartTime;
    // Asymptotic progress: quickly to 60%, then slow down to max 90%
    // Formula: 90 * (1 - e^(-elapsed/3000))
    const targetProgress = 90 * (1 - Math.exp(-elapsed / 3000));

    if (currentProgress < targetProgress) {
      updateLoadingProgress(targetProgress);
    }

    if (loadingLineCount > 0 && currentProgress < 90) {
      loadingAnimationFrame = requestAnimationFrame(animateProgress);
    }
  }

  function showLoadingLine() {
    const { line, fill } = getLoadingElements();

    // Clear ALL pending timeouts and animations - completely cancel previous state
    if (loadingLineFadeTimeout) {
      clearTimeout(loadingLineFadeTimeout);
      loadingLineFadeTimeout = null;
    }
    if (loadingCleanupTimeout) {
      clearTimeout(loadingCleanupTimeout);
      loadingCleanupTimeout = null;
    }
    if (loadingAnimationFrame) {
      cancelAnimationFrame(loadingAnimationFrame);
      loadingAnimationFrame = null;
    }
    if (loadingLineTimeout) {
      clearTimeout(loadingLineTimeout);
      loadingLineTimeout = null;
    }

    // Reset count to 1 (not increment) - this is a fresh start
    loadingLineCount = 1;

    // Remove ALL classes and reset state completely
    if (line) {
      line.classList.remove('fade-out', 'complete', 'active');
      // Force reflow to ensure class changes take effect
      line.offsetHeight;
      line.classList.add('active');
    }

    // Reset progress bar width without transition
    if (fill) {
      fill.classList.add('no-transition');
      fill.style.width = '0%';
      fill.style.height = '';
      fill.style.top = '';
      fill.offsetHeight; // Force reflow
      fill.classList.remove('no-transition');
    }

    loadingStartTime = Date.now();
    currentProgress = 0;
    updateLoadingProgress(5); // Start with small initial progress
    animateProgress();

    // Auto-hide after 30 seconds as a safety measure
    loadingLineTimeout = setTimeout(() => {
      forceHideLoadingLine();
    }, 30000);
  }

  // Track the cleanup timeout separately
  let loadingCleanupTimeout = null;

  function hideLoadingLine() {
    loadingLineCount = Math.max(0, loadingLineCount - 1);

    if (loadingLineCount === 0) {
      // Cancel animation
      if (loadingAnimationFrame) {
        cancelAnimationFrame(loadingAnimationFrame);
        loadingAnimationFrame = null;
      }

      // Clear auto-hide timeout
      if (loadingLineTimeout) {
        clearTimeout(loadingLineTimeout);
        loadingLineTimeout = null;
      }

      // All requests complete - animate to 100%
      updateLoadingProgress(100);
      const { line } = getLoadingElements();
      if (line) {
        line.classList.add('complete');
      }

      // Wait for completion flash (0.5s), then add fade-out class
      loadingLineFadeTimeout = setTimeout(() => {
        // Check if a new request started - if so, abort fade
        if (loadingLineCount > 0) return;

        const { line } = getLoadingElements();
        if (line) {
          line.classList.add('fade-out');
        }
      }, 500);

      // After fade completes (1.5s total), clean up
      if (loadingCleanupTimeout) clearTimeout(loadingCleanupTimeout);
      loadingCleanupTimeout = setTimeout(() => {
        // Check if a new request started - if so, abort cleanup
        if (loadingLineCount > 0) return;

        const { line, fill } = getLoadingElements();
        if (line) {
          line.classList.remove('active', 'fade-out', 'complete');
        }
        if (fill) {
          fill.classList.add('no-transition');
          fill.style.width = '0%';
          fill.style.height = '';
          fill.style.top = '';
          fill.offsetHeight;
          fill.classList.remove('no-transition');
        }
        currentProgress = 0;
      }, 1500);
    }
  }

  // Set progress directly (0-100)
  function setLoadingProgress(progress) {
    const { line } = getLoadingElements();
    if (line && !line.classList.contains('active')) {
      line.classList.remove('fade-out', 'complete');
      line.classList.add('active');
    }
    // Cancel any animation when setting progress directly
    if (loadingAnimationFrame) {
      cancelAnimationFrame(loadingAnimationFrame);
      loadingAnimationFrame = null;
    }
    updateLoadingProgress(progress);
  }

  // Force hide regardless of count (for page transitions)
  function forceHideLoadingLine() {
    loadingLineCount = 0;
    currentProgress = 0;

    if (loadingAnimationFrame) {
      cancelAnimationFrame(loadingAnimationFrame);
      loadingAnimationFrame = null;
    }

    if (loadingLineFadeTimeout) {
      clearTimeout(loadingLineFadeTimeout);
      loadingLineFadeTimeout = null;
    }

    if (loadingCleanupTimeout) {
      clearTimeout(loadingCleanupTimeout);
      loadingCleanupTimeout = null;
    }

    if (loadingLineTimeout) {
      clearTimeout(loadingLineTimeout);
      loadingLineTimeout = null;
    }

    const { line, fill } = getLoadingElements();
    if (line) {
      line.classList.remove('active', 'fade-out', 'complete');
    }
    if (fill) {
      // Disable transition to prevent slide-back animation
      fill.classList.add('no-transition');
      fill.style.width = '0%';
      fill.style.height = '';
      fill.style.top = '';
      fill.offsetHeight; // Trigger reflow
      fill.classList.remove('no-transition');
    }
  }

  // Expose loading line functions globally
  window.showLoadingLine = showLoadingLine;
  window.hideLoadingLine = hideLoadingLine;
  window.setLoadingProgress = setLoadingProgress;
  window.forceHideLoadingLine = forceHideLoadingLine;
  window.MX.loadingLine = {
    show: showLoadingLine,
    hide: hideLoadingLine,
    setProgress: setLoadingProgress,
    forceHide: forceHideLoadingLine
  };

  // ===== SIDEBAR TOGGLE (Mobile) =====
  function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    sidebar?.classList.toggle('show');
    overlay?.classList.toggle('show');
  }

  // ===== ALERT BAR =====
  let alertBarData = null;
  let alertBarTimeout = null;

  function showAlertBar(type, title, sub, playerData) {
    alertBarData = { type, title, sub, playerData };
    const bar = document.getElementById('alertBar');
    const content = document.getElementById('alertBarContent');
    const icon = document.getElementById('alertBarIcon');
    const avatar = document.getElementById('alertBarAvatar');
    const titleEl = document.getElementById('alertBarTitle');
    const playerEl = document.getElementById('alertBarPlayer');
    const subEl = document.getElementById('alertBarSub');

    if (content) content.className = 'alertBar-content' + (type === 'anticheat' ? ' anticheat' : '');

    // Show player avatar if we have player data
    const player = playerData?.playerId ? state.players.find(p => p.uuid === playerData.playerId || p.id === playerData.playerId) : null;
    const playerName = playerData?.playerName || player?.name;

    if (avatar && playerData?.playerId) {
      avatar.src = `https://mc-heads.net/avatar/${playerData.playerId}/32`;
      avatar.style.display = 'block';
      if (icon) icon.style.display = 'none';
    } else {
      if (avatar) avatar.style.display = 'none';
      if (icon) {
        icon.style.display = 'flex';
        icon.innerHTML = type === 'anticheat'
          ? '<i class="fa-solid fa-shield-halved"></i>'
          : '<i class="fa-solid fa-eye"></i>';
      }
    }

    if (titleEl) titleEl.textContent = title;
    if (playerEl && playerName) {
      playerEl.textContent = playerName;
      playerEl.style.display = 'block';
    } else if (playerEl) {
      playerEl.style.display = 'none';
    }
    if (subEl) subEl.textContent = sub;
    if (bar) bar.classList.add('show');

    window.MX.sounds?.alertBar();

    if (alertBarTimeout) clearTimeout(alertBarTimeout);
    alertBarTimeout = setTimeout(dismissAlertBar, 8000);
  }

  function dismissAlertBar() {
    const bar = document.getElementById('alertBar');
    if (bar) bar.classList.remove('show');
    alertBarData = null;
    if (alertBarTimeout) clearTimeout(alertBarTimeout);
  }

  function viewAlertPlayer() {
    if (alertBarData?.playerData?.playerId) {
      openPlayerDrawer(alertBarData.playerData.playerId);
    }
    dismissAlertBar();
  }

  // ===== MY SETTINGS (Player Preferences) =====
  const myAlertDefaults = {
    automod: true,
    commands: false,
    punishments: true,
    pardons: true,
    joins: false,
    anticheatMode: 'watchlist',
    watchlistStyle: 'bar'
  };

  /**
   * Show a panel alert based on staff notification settings.
   * Uses the new alertToast system for better visuals and animations.
   * @param {string} category - 'punishments', 'automod', 'anticheat', 'watchlist', 'staffchat', 'ban', 'kick', etc.
   * @param {string} title - Alert title
   * @param {string} message - Alert message/subtitle
   * @param {object} options - { playerId, playerName, severity: 'info'|'warn'|'error' }
   */
  function showPanelAlert(category, title, message, options = {}) {
    const staffSettings = state.staffSettings || {};

    // Map category to alert level setting key
    const alertLevelKey = category + 'Alerts'; // e.g., 'anticheatAlerts', 'automodAlerts'
    const alertLevel = staffSettings[alertLevelKey] || 'everyone';

    // Check if alerts for this category are disabled by level
    if (alertLevel === 'off' || alertLevel === 'OFF') {
      console.log('[showPanelAlert] Alert level is off for', category);
      return;
    }

    // Get notification mode - default to 'toast' if not set or 'off'
    const notifyKey = 'webNotify' + category.charAt(0).toUpperCase() + category.slice(1);
    let mode = staffSettings[notifyKey] || 'toast';

    // If notification mode is 'off' but alert level is enabled, default to toast
    if (mode === 'off' || mode === 'OFF') {
      mode = 'toast';
    }

    console.log('[showPanelAlert] category:', category, 'alertLevel:', alertLevel, 'mode:', mode);

    // Only include playerData if we have a valid playerId (not undefined/null)
    const playerData = options.playerId ? { playerId: options.playerId, playerName: options.playerName } : { playerName: options.playerName };

    console.log('[showPanelAlert] Showing alert toast:', { category, title, message, playerData });

    if (mode === 'toast' || mode === 'both') {
      // Use the new alertToast for alert notifications
      window.alertToast(category, title, message, playerData);
    }

    if (mode === 'browser' || mode === 'both') {
      // Request browser notification permission if not granted
      if (Notification.permission === 'granted') {
        new Notification(title, { body: message, icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      } else if (Notification.permission !== 'denied') {
        Notification.requestPermission().then(permission => {
          if (permission === 'granted') {
            new Notification(title, { body: message, icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
          }
        });
      }
      // Also show alert toast as fallback if browser-only mode
      if (mode === 'browser') {
        window.alertToast(category, title, message, { ...playerData, silent: true });
      }
    }
  }

  window.showPanelAlert = showPanelAlert;

  // Track active anticheat alerts for live VL updates
  const activeAnticheatAlerts = new Map();

  /**
   * Show an anticheat alert with live VL updates
   * If an alert for the same player+check already exists, update its VL instead of creating a new one
   */
  function showAnticheatAlertWithUpdates(alertKey, data) {
    const { playerName, playerId, checkName, anticheat, vl } = data;

    // Check if there's an existing alert for this key
    const existing = activeAnticheatAlerts.get(alertKey);
    if (existing && existing.element && document.body.contains(existing.element)) {
      // Update VL in existing alert without playing sound
      const vlEl = existing.element.querySelector('.alert-vl-value');
      if (vlEl) {
        const oldVl = parseInt(vlEl.textContent) || 0;
        vlEl.textContent = vl;
        // Add visual feedback for VL increase
        if (vl > oldVl) {
          vlEl.classList.add('vl-increased');
          setTimeout(() => vlEl.classList.remove('vl-increased'), 300);
        }
      }
      existing.vl = vl;
      return;
    }

    // Create new alert
    const staffSettings = state.staffSettings || {};
    const duration = (staffSettings.webAlertDurationSeconds || 10) * 1000;

    // Check notification mode
    const mode = staffSettings.webNotifyAnticheat || 'toast';
    if (mode === 'off' || mode === 'OFF') return;

    const container = document.getElementById('alertToastContainer') || createAlertContainer();

    const el = document.createElement('div');
    el.className = 'alert-toast anticheat';

    el.innerHTML = `
      <div class="alert-toast-left">
        ${playerId ? `<img class="alert-toast-avatar" src="https://mc-heads.net/avatar/${escapeHtml(playerId)}/32" alt="">` : '<div class="alert-toast-icon"><i class="fa-solid fa-shield-halved"></i></div>'}
        <div class="alert-toast-text">
          <div class="alert-toast-title">${escapeHtml(anticheat)}: ${escapeHtml(playerName)}</div>
          <div class="alert-toast-sub">
            <span>${escapeHtml(checkName)}</span>
            <span class="alert-vl-badge">VL: <span class="alert-vl-value">${vl}</span></span>
          </div>
        </div>
      </div>
      <div class="alert-toast-actions">
        <button class="mini" data-action="punish" title="Punish"><i class="fa-solid fa-gavel"></i></button>
        <button class="mini" data-action="watchlist" title="Add to Watchlist"><i class="fa-solid fa-eye"></i></button>
        <button class="mini" data-action="dismiss" title="Dismiss"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="alert-toast-progress" style="animation-duration: ${duration}ms"></div>
    `;

    const dismiss = () => {
      activeAnticheatAlerts.delete(alertKey);
      el.classList.add('exit');
      setTimeout(() => el.remove(), 350);
    };

    // Punish button handler
    el.querySelector('[data-action="punish"]').onclick = (e) => {
      e.stopPropagation();
      showAlertActionModal('punish', 'anticheat', playerName, playerId, `${checkName} VL: ${vl}`);
      dismiss();
    };

    // Watchlist button handler
    el.querySelector('[data-action="watchlist"]').onclick = (e) => {
      e.stopPropagation();
      const reason = `${anticheat} ${checkName} alert (VL: ${vl})`;
      const ws = window.MX?.ws;
      if (ws && ws.addToWatchlist) {
        ws.addToWatchlist(playerId, playerName, reason);
        toast('ok', 'Watchlist', `Added ${playerName} to watchlist`);
      }
      dismiss();
    };

    // Dismiss button handler
    el.querySelector('[data-action="dismiss"]').onclick = (e) => {
      e.stopPropagation();
      dismiss();
    };

    // Insert at top
    container.insertBefore(el, container.firstChild);

    // Track this alert
    activeAnticheatAlerts.set(alertKey, { element: el, vl, timeout: null });

    // Trigger slide-in animation
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        el.classList.add('show');
      });
    });

    // Auto-dismiss
    const timeout = setTimeout(dismiss, duration);
    activeAnticheatAlerts.get(alertKey).timeout = timeout;

    // Play sound (only for new alerts, not updates)
    const shouldPlaySound = staffSettings.webSoundAnticheat !== false;
    if (shouldPlaySound && window.MX?.sounds?.alertBar) {
      window.MX.sounds.alertBar();
    }
  }

  function createAlertContainer() {
    let container = document.getElementById('alertToastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'alertToastContainer';
      container.className = 'top-right';
      document.body.appendChild(container);
    }
    return container;
  }

  window.showAnticheatAlertWithUpdates = showAnticheatAlertWithUpdates;

  function loadMySettings() {
    try {
      const saved = localStorage.getItem('mx_my_settings');
      return saved ? { ...myAlertDefaults, ...JSON.parse(saved) } : { ...myAlertDefaults };
    } catch (e) {
      return { ...myAlertDefaults };
    }
  }

  function saveMySettings(settings) {
    try {
      localStorage.setItem('mx_my_settings', JSON.stringify(settings));
    } catch (e) {}
  }

  function applyMySettingsUI() {
    const soundsBtn = document.getElementById('soundsEnabled');
    if (soundsBtn) soundsBtn.classList.toggle('on', window.MX.sounds?.isEnabled() ?? true);

    const volumeSlider = document.getElementById('volumeSlider');
    const volumeHint = document.getElementById('volumeHint');
    if (volumeSlider) {
      const vol = Math.round((window.MX.sounds?.getVolume() ?? 0.5) * 100);
      volumeSlider.value = vol;
      if (volumeHint) volumeHint.textContent = vol + '%';
    }

    // Apply device trust setting from state
    const deviceBtn = document.getElementById('deviceTrustEnabled');
    if (deviceBtn) {
      deviceBtn.classList.toggle('on', state.settings.deviceTrustEnabled ?? false);
    }
  }

  function togglePanelSounds() {
    if (window.MX.sounds) {
      const enabled = window.MX.sounds.toggle();
      const btn = document.getElementById('soundsEnabled');
      if (btn) btn.classList.toggle('on', enabled);

      // Sync to server
      if (ws.connected) {
        ws.send('UPDATE_USER_SETTINGS', {
          soundEnabled: enabled
        });
      }

      // Update state
      state.settings.soundEnabled = enabled;
    }
  }

  function refreshMySettings() {
    if (!ws.connected) {
      toast('error', 'Not Connected', 'Cannot refresh settings while disconnected');
      return;
    }

    toast('info', 'Refreshing', 'Fetching your settings from the server...');
    showLoadingLine();

    // Request fresh settings from server
    ws.send('GET_USER_SETTINGS', {});

    // The USER_SETTINGS_DATA handler will update the UI automatically
  }
  window.refreshMySettings = refreshMySettings;

  function toggleDeviceTrust() {
    const currentValue = state.settings.deviceTrustEnabled ?? false;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('deviceTrustEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Sync to server
    if (ws.connected) {
      ws.send('UPDATE_USER_SETTINGS', {
        deviceTrustEnabled: newValue
      });
    }

    // Update state
    state.settings.deviceTrustEnabled = newValue;

    // Play click sound
    window.MX.sounds?.click();
  }

  function toggleDebugMode() {
    // Initialize userSettings if needed
    if (!state.userSettings) state.userSettings = {};

    const currentValue = state.userSettings.debugMode ?? false;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('debugModeEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Show/hide info box
    const infoBox = document.getElementById('debugModeInfo');
    if (infoBox) infoBox.style.display = newValue ? 'flex' : 'none';

    // Show/hide version badge
    const versionBadge = document.getElementById('versionBadge');
    if (versionBadge) versionBadge.style.display = newValue ? 'flex' : 'none';

    // Update state
    state.userSettings.debugMode = newValue;

    // Save to localStorage
    saveState();

    // Play click sound
    window.MX.sounds?.click();

    // Show confirmation debug message
    if (newValue) {
      debugLog('SYSTEM', 'Debug mode enabled - you will now see sync and error notifications', 'success');
    }
  }

  function toggleWatchlistAlerts() {
    // Initialize userSettings if needed
    if (!state.userSettings) state.userSettings = {};

    const currentValue = state.userSettings.watchlistAlerts ?? true;
    const newValue = !currentValue;

    // Update UI
    const btn = document.getElementById('watchlistAlertsEnabled');
    if (btn) btn.classList.toggle('on', newValue);

    // Update state
    state.userSettings.watchlistAlerts = newValue;

    // Save to localStorage
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { watchlistAlerts: newValue });
    }

    // Play click sound
    window.MX.sounds?.click();

    // Show system message confirming the change
    systemLog(`Watchlist alerts ${newValue ? 'enabled' : 'disabled'}`, newValue ? 'success' : 'info');
  }

  function updateSettingsUI() {
    // Update sound toggle button state
    const soundsBtn = document.getElementById('soundsEnabled');
    if (soundsBtn) {
      soundsBtn.classList.toggle('on', state.settings.soundEnabled ?? true);
    }

    // Update device trust toggle button state
    const deviceBtn = document.getElementById('deviceTrustEnabled');
    if (deviceBtn) {
      deviceBtn.classList.toggle('on', state.settings.deviceTrustEnabled ?? false);
    }

    // Update debug mode toggle button state
    const debugBtn = document.getElementById('debugModeEnabled');
    if (debugBtn) {
      const debugEnabled = state.userSettings?.debugMode ?? false;
      debugBtn.classList.toggle('on', debugEnabled);
      // Show/hide info box
      const infoBox = document.getElementById('debugModeInfo');
      if (infoBox) infoBox.style.display = debugEnabled ? 'flex' : 'none';
      // Show/hide version badge
      const versionBadge = document.getElementById('versionBadge');
      if (versionBadge) {
        versionBadge.style.display = debugEnabled ? 'flex' : 'none';
        // Set version text from loaded panel version
        const versionText = document.getElementById('versionText');
        if (versionText && panelVersionInfo?.version) {
          versionText.textContent = panelVersionInfo.version;
        }
      }
    }

    // Update watchlist alerts toggle button state
    const watchlistBtn = document.getElementById('watchlistAlertsEnabled');
    if (watchlistBtn) {
      watchlistBtn.classList.toggle('on', state.userSettings?.watchlistAlerts ?? true);
    }

    // Update volume if the slider exists
    const volumeSlider = document.getElementById('volumeSlider');
    const volumeHint = document.getElementById('volumeHint');
    if (volumeSlider && window.MX.sounds) {
      const vol = Math.round((window.MX.sounds.getVolume() ?? 0.5) * 100);
      volumeSlider.value = vol;
      if (volumeHint) volumeHint.textContent = vol + '%';
    }
  }

  function setVolume(value) {
    const vol = parseInt(value, 10) / 100;
    if (window.MX.sounds) {
      window.MX.sounds.setVolume(vol);
    }
    const hint = document.getElementById('volumeHint');
    if (hint) hint.textContent = value + '%';
  }

  // ===== THEME CUSTOMIZATION =====

  /**
   * Convert hex color to HSL values
   */
  function hexToHSL(hex) {
    let r = parseInt(hex.slice(1, 3), 16) / 255;
    let g = parseInt(hex.slice(3, 5), 16) / 255;
    let b = parseInt(hex.slice(5, 7), 16) / 255;

    const max = Math.max(r, g, b), min = Math.min(r, g, b);
    let h, s, l = (max + min) / 2;

    if (max === min) {
      h = s = 0;
    } else {
      const d = max - min;
      s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
      switch (max) {
        case r: h = ((g - b) / d + (g < b ? 6 : 0)) / 6; break;
        case g: h = ((b - r) / d + 2) / 6; break;
        case b: h = ((r - g) / d + 4) / 6; break;
      }
    }
    return { h: h * 360, s: s * 100, l: l * 100 };
  }

  /**
   * Convert HSL to hex color
   */
  function hslToHex(h, s, l) {
    s /= 100;
    l /= 100;
    const a = s * Math.min(l, 1 - l);
    const f = n => {
      const k = (n + h / 30) % 12;
      const color = l - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
      return Math.round(255 * color).toString(16).padStart(2, '0');
    };
    return `#${f(0)}${f(8)}${f(4)}`;
  }

  /**
   * Set the theme color and apply it to the entire panel
   */
  function setThemeColor(color) {
    const root = document.documentElement;
    const hsl = hexToHSL(color);

    // Generate color variants
    const colorLight = hslToHex(hsl.h, Math.min(hsl.s + 15, 100), Math.min(hsl.l + 15, 85));
    const colorDark = hslToHex(hsl.h, hsl.s, Math.max(hsl.l - 15, 15));

    // Extract RGB for glow and opacity variations
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);

    // Extract RGB for light variant
    const rL = parseInt(colorLight.slice(1, 3), 16);
    const gL = parseInt(colorLight.slice(3, 5), 16);
    const bL = parseInt(colorLight.slice(5, 7), 16);

    // Apply theme hue - this shifts ALL derived colors (backgrounds, borders, muted text)
    root.style.setProperty('--theme-h', Math.round(hsl.h));

    // Apply main CSS variables
    root.style.setProperty('--primary', color);
    root.style.setProperty('--primary-light', colorLight);
    root.style.setProperty('--primary-dark', colorDark);
    root.style.setProperty('--primary-rgb', `${r}, ${g}, ${b}`);
    root.style.setProperty('--primary-light-rgb', `${rL}, ${gL}, ${bL}`);

    // Apply opacity variations for all UI elements
    root.style.setProperty('--primary-glow', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-05', `rgba(${r}, ${g}, ${b}, 0.05)`);
    root.style.setProperty('--primary-08', `rgba(${r}, ${g}, ${b}, 0.08)`);
    root.style.setProperty('--primary-10', `rgba(${r}, ${g}, ${b}, 0.10)`);
    root.style.setProperty('--primary-12', `rgba(${r}, ${g}, ${b}, 0.12)`);
    root.style.setProperty('--primary-15', `rgba(${r}, ${g}, ${b}, 0.15)`);
    root.style.setProperty('--primary-18', `rgba(${r}, ${g}, ${b}, 0.18)`);
    root.style.setProperty('--primary-20', `rgba(${r}, ${g}, ${b}, 0.20)`);
    root.style.setProperty('--primary-22', `rgba(${r}, ${g}, ${b}, 0.22)`);
    root.style.setProperty('--primary-25', `rgba(${r}, ${g}, ${b}, 0.25)`);
    root.style.setProperty('--primary-30', `rgba(${r}, ${g}, ${b}, 0.30)`);
    root.style.setProperty('--primary-35', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-40', `rgba(${r}, ${g}, ${b}, 0.40)`);
    root.style.setProperty('--primary-45', `rgba(${r}, ${g}, ${b}, 0.45)`);
    root.style.setProperty('--primary-50', `rgba(${r}, ${g}, ${b}, 0.50)`);
    root.style.setProperty('--primary-60', `rgba(${r}, ${g}, ${b}, 0.60)`);
    root.style.setProperty('--line-glow', `rgba(${r}, ${g}, ${b}, 0.22)`);

    // Check if it's a light color - add text shadows for readability
    const isLight = hsl.l > 60;
    document.body.classList.toggle('light-theme-text', isLight);

    // Update preset buttons
    document.querySelectorAll('.theme-preset').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.color === color);
    });

    // Update custom color picker
    const picker = document.getElementById('customColorPicker');
    if (picker) picker.value = color;

    // Save to state
    if (!state.userSettings) state.userSettings = {};
    state.userSettings.themeColor = color;
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { themeColor: color });
    }

    // Update background pattern colors if applicable
    applyBackgroundPattern(state.userSettings.backgroundPattern || 'aurora');

    window.MX.sounds?.click();
  }

  /**
   * Set the background pattern
   */
  function setBackgroundPattern(pattern) {
    // Update button states
    document.querySelectorAll('.pattern-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.pattern === pattern);
    });

    // Apply the pattern
    applyBackgroundPattern(pattern);

    // Save to state
    if (!state.userSettings) state.userSettings = {};
    state.userSettings.backgroundPattern = pattern;
    saveState();

    // Sync to server
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('UPDATE_USER_SETTINGS', { backgroundPattern: pattern });
    }

    window.MX.sounds?.click();
  }

  /**
   * Apply background pattern to the body
   */
  function applyBackgroundPattern(pattern) {
    const body = document.body;
    const color = state.userSettings?.themeColor || '#2d7aed';
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);
    const accent = '#7c5cff';
    const ar = 124, ag = 92, ab = 255;

    let bg;
    switch (pattern) {
      case 'aurora':
      default:
        bg = `
          radial-gradient(ellipse 1400px 900px at 10% 15%, rgba(${r}, ${g}, ${b}, 0.18), transparent 55%),
          radial-gradient(ellipse 1200px 800px at 90% 80%, rgba(${ar}, ${ag}, ${ab}, 0.12), transparent 50%),
          radial-gradient(ellipse 800px 600px at 50% 100%, rgba(16, 185, 129, 0.08), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'waves':
        bg = `
          repeating-linear-gradient(
            -45deg,
            transparent,
            transparent 8px,
            rgba(${r}, ${g}, ${b}, 0.08) 8px,
            rgba(${r}, ${g}, ${b}, 0.08) 16px
          ),
          repeating-linear-gradient(
            45deg,
            transparent,
            transparent 8px,
            rgba(${ar}, ${ag}, ${ab}, 0.05) 8px,
            rgba(${ar}, ${ag}, ${ab}, 0.05) 16px
          ),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'stars':
        bg = `
          radial-gradient(1.5px 1.5px at 10% 20%, rgba(255,255,255,0.8), transparent),
          radial-gradient(1px 1px at 30% 60%, rgba(255,255,255,0.6), transparent),
          radial-gradient(1.5px 1.5px at 50% 30%, rgba(255,255,255,0.7), transparent),
          radial-gradient(1px 1px at 70% 70%, rgba(255,255,255,0.5), transparent),
          radial-gradient(2px 2px at 90% 40%, rgba(${r}, ${g}, ${b}, 0.8), transparent),
          radial-gradient(1px 1px at 15% 85%, rgba(255,255,255,0.6), transparent),
          radial-gradient(2px 2px at 25% 15%, rgba(${ar}, ${ag}, ${ab}, 0.7), transparent),
          radial-gradient(1px 1px at 85% 85%, rgba(255,255,255,0.5), transparent),
          radial-gradient(1.5px 1.5px at 45% 45%, rgba(255,255,255,0.6), transparent),
          radial-gradient(1px 1px at 65% 15%, rgba(255,255,255,0.4), transparent),
          radial-gradient(ellipse 800px 600px at 50% 100%, rgba(${r}, ${g}, ${b}, 0.05), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'grid':
        bg = `
          linear-gradient(rgba(${r}, ${g}, ${b}, 0.06) 1px, transparent 1px),
          linear-gradient(90deg, rgba(${r}, ${g}, ${b}, 0.06) 1px, transparent 1px),
          radial-gradient(ellipse 800px 600px at 50% 0%, rgba(${r}, ${g}, ${b}, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '40px 40px, 40px 40px, 100% 100%, 100% 100%';
        break;
      case 'circuits':
        bg = `
          linear-gradient(90deg, transparent 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 50.5%, transparent 50.5%),
          linear-gradient(transparent 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 49.5%, rgba(${r}, ${g}, ${b}, 0.08) 50.5%, transparent 50.5%),
          radial-gradient(circle at 25% 25%, rgba(${r}, ${g}, ${b}, 0.2) 2px, transparent 2px),
          radial-gradient(circle at 75% 75%, rgba(${ar}, ${ag}, ${ab}, 0.15) 2px, transparent 2px),
          radial-gradient(ellipse 600px 400px at 20% 80%, rgba(${r}, ${g}, ${b}, 0.08), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '60px 60px, 60px 60px, 60px 60px, 60px 60px, 100% 100%, 100% 100%';
        break;
      case 'hexagons':
        bg = `
          radial-gradient(circle at 0% 50%, rgba(${r}, ${g}, ${b}, 0.1) 2px, transparent 2px),
          radial-gradient(circle at 100% 50%, rgba(${r}, ${g}, ${b}, 0.1) 2px, transparent 2px),
          radial-gradient(circle at 50% 0%, rgba(${ar}, ${ag}, ${ab}, 0.08) 2px, transparent 2px),
          radial-gradient(circle at 50% 100%, rgba(${ar}, ${ag}, ${ab}, 0.08) 2px, transparent 2px),
          radial-gradient(ellipse 600px 400px at 80% 20%, rgba(${r}, ${g}, ${b}, 0.1), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '50px 50px, 50px 50px, 50px 50px, 50px 50px, 100% 100%, 100% 100%';
        break;
      case 'particles':
        bg = `
          radial-gradient(circle at 15% 25%, rgba(${r}, ${g}, ${b}, 0.5) 2px, transparent 2px),
          radial-gradient(circle at 45% 65%, rgba(${ar}, ${ag}, ${ab}, 0.4) 2px, transparent 2px),
          radial-gradient(circle at 75% 35%, rgba(16, 185, 129, 0.4) 2px, transparent 2px),
          radial-gradient(circle at 85% 85%, rgba(${r}, ${g}, ${b}, 0.3) 3px, transparent 3px),
          radial-gradient(circle at 25% 75%, rgba(${ar}, ${ag}, ${ab}, 0.3) 3px, transparent 3px),
          radial-gradient(circle at 55% 15%, rgba(${r}, ${g}, ${b}, 0.2) 2px, transparent 2px),
          radial-gradient(circle at 35% 45%, rgba(16, 185, 129, 0.25) 2px, transparent 2px),
          radial-gradient(circle at 65% 90%, rgba(${ar}, ${ag}, ${ab}, 0.2) 2px, transparent 2px),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'nebula':
        bg = `
          radial-gradient(ellipse 600px 500px at 30% 40%, rgba(${ar}, ${ag}, ${ab}, 0.25), transparent 50%),
          radial-gradient(ellipse 500px 400px at 70% 60%, rgba(236, 72, 153, 0.18), transparent 50%),
          radial-gradient(ellipse 700px 500px at 50% 50%, rgba(${r}, ${g}, ${b}, 0.12), transparent 60%),
          radial-gradient(ellipse 300px 200px at 20% 80%, rgba(16, 185, 129, 0.1), transparent 50%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'matrix':
        bg = `
          repeating-linear-gradient(
            180deg,
            transparent,
            transparent 3px,
            rgba(16, 185, 129, 0.04) 3px,
            rgba(16, 185, 129, 0.04) 6px
          ),
          linear-gradient(180deg, rgba(16, 185, 129, 0.15) 0%, transparent 70%),
          radial-gradient(ellipse 600px 400px at 50% 0%, rgba(16, 185, 129, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        break;
      case 'rain':
        bg = `
          repeating-linear-gradient(
            180deg,
            transparent,
            transparent 20px,
            rgba(${r}, ${g}, ${b}, 0.08) 20px,
            rgba(${r}, ${g}, ${b}, 0.08) 22px
          ),
          repeating-linear-gradient(
            180deg,
            transparent 10px,
            transparent 30px,
            rgba(${r}, ${g}, ${b}, 0.05) 30px,
            rgba(${r}, ${g}, ${b}, 0.05) 32px
          ),
          radial-gradient(ellipse 800px 400px at 50% 100%, rgba(${r}, ${g}, ${b}, 0.1), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '4px 100%, 9px 100%, 100% 100%, 100% 100%';
        break;
      case 'geometric':
        bg = `
          linear-gradient(135deg, transparent 46%, rgba(${r}, ${g}, ${b}, 0.06) 46%, rgba(${r}, ${g}, ${b}, 0.06) 54%, transparent 54%),
          linear-gradient(-135deg, transparent 46%, rgba(${ar}, ${ag}, ${ab}, 0.04) 46%, rgba(${ar}, ${ag}, ${ab}, 0.04) 54%, transparent 54%),
          radial-gradient(ellipse 600px 400px at 50% 50%, rgba(${r}, ${g}, ${b}, 0.08), transparent 60%),
          linear-gradient(180deg, var(--bg0), var(--bg1))
        `;
        body.style.backgroundSize = '60px 60px, 60px 60px, 100% 100%, 100% 100%';
        break;
      case 'gradient':
        bg = `
          linear-gradient(135deg, var(--bg0) 0%, rgba(${r}, ${g}, ${b}, 0.08) 50%, var(--bg0) 100%)
        `;
        break;
      case 'minimal':
        bg = `linear-gradient(180deg, var(--bg0), var(--bg1))`;
        break;
    }

    body.style.background = bg;

    // Reset background-size for patterns that don't need custom sizes
    if (!['grid', 'circuits', 'hexagons', 'rain', 'geometric'].includes(pattern)) {
      body.style.backgroundSize = '';
    }
  }

  /**
   * Apply theme settings from saved state on page load
   */
  function applyThemeFromState() {
    const color = state.userSettings?.themeColor || '#2d7aed';
    const pattern = state.userSettings?.backgroundPattern || 'aurora';

    // Apply color without triggering save
    const root = document.documentElement;
    const hsl = hexToHSL(color);
    const colorLight = hslToHex(hsl.h, Math.min(hsl.s + 15, 100), Math.min(hsl.l + 15, 85));
    const colorDark = hslToHex(hsl.h, hsl.s, Math.max(hsl.l - 15, 15));
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);

    // Extract RGB for light variant
    const rL = parseInt(colorLight.slice(1, 3), 16);
    const gL = parseInt(colorLight.slice(3, 5), 16);
    const bL = parseInt(colorLight.slice(5, 7), 16);

    // Apply theme hue - this shifts ALL derived colors (backgrounds, borders, muted text)
    root.style.setProperty('--theme-h', Math.round(hsl.h));

    // Apply main CSS variables
    root.style.setProperty('--primary', color);
    root.style.setProperty('--primary-light', colorLight);
    root.style.setProperty('--primary-dark', colorDark);
    root.style.setProperty('--primary-rgb', `${r}, ${g}, ${b}`);
    root.style.setProperty('--primary-light-rgb', `${rL}, ${gL}, ${bL}`);

    // Apply all opacity variations for UI elements
    root.style.setProperty('--primary-glow', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-04', `rgba(${r}, ${g}, ${b}, 0.04)`);
    root.style.setProperty('--primary-05', `rgba(${r}, ${g}, ${b}, 0.05)`);
    root.style.setProperty('--primary-06', `rgba(${r}, ${g}, ${b}, 0.06)`);
    root.style.setProperty('--primary-08', `rgba(${r}, ${g}, ${b}, 0.08)`);
    root.style.setProperty('--primary-10', `rgba(${r}, ${g}, ${b}, 0.10)`);
    root.style.setProperty('--primary-12', `rgba(${r}, ${g}, ${b}, 0.12)`);
    root.style.setProperty('--primary-15', `rgba(${r}, ${g}, ${b}, 0.15)`);
    root.style.setProperty('--primary-18', `rgba(${r}, ${g}, ${b}, 0.18)`);
    root.style.setProperty('--primary-20', `rgba(${r}, ${g}, ${b}, 0.20)`);
    root.style.setProperty('--primary-22', `rgba(${r}, ${g}, ${b}, 0.22)`);
    root.style.setProperty('--primary-25', `rgba(${r}, ${g}, ${b}, 0.25)`);
    root.style.setProperty('--primary-28', `rgba(${r}, ${g}, ${b}, 0.28)`);
    root.style.setProperty('--primary-30', `rgba(${r}, ${g}, ${b}, 0.30)`);
    root.style.setProperty('--primary-35', `rgba(${r}, ${g}, ${b}, 0.35)`);
    root.style.setProperty('--primary-40', `rgba(${r}, ${g}, ${b}, 0.40)`);
    root.style.setProperty('--primary-45', `rgba(${r}, ${g}, ${b}, 0.45)`);
    root.style.setProperty('--primary-50', `rgba(${r}, ${g}, ${b}, 0.50)`);
    root.style.setProperty('--primary-60', `rgba(${r}, ${g}, ${b}, 0.60)`);
    root.style.setProperty('--line-glow', `rgba(${r}, ${g}, ${b}, 0.22)`);

    const isLight = hsl.l > 60;
    document.body.classList.toggle('light-theme-text', isLight);

    // Update preset buttons
    document.querySelectorAll('.theme-preset').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.color === color);
    });

    // Update pattern buttons
    document.querySelectorAll('.pattern-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.pattern === pattern);
    });

    // Update custom color picker
    const picker = document.getElementById('customColorPicker');
    if (picker) picker.value = color;

    // Apply background pattern
    applyBackgroundPattern(pattern);
  }

  // ===== DEVELOPER CHECKLIST =====
  state.devChecklist = state.devChecklist || [];

  function renderDevChecklist() {
    const container = document.getElementById('checklistContainer');
    if (!container) return;

    const items = state.devChecklist || [];
    if (items.length === 0) {
      container.innerHTML = '<div class="loading-text">No checklist items. Click "Add Item" to create one.</div>';
      updateChecklistProgress();
      return;
    }

    // Group by category
    const categories = {};
    items.forEach(item => {
      const cat = item.category || 'Uncategorized';
      if (!categories[cat]) categories[cat] = [];
      categories[cat].push(item);
    });

    let html = '';
    for (const [category, catItems] of Object.entries(categories)) {
      const checkedCount = catItems.filter(i => i.checked).length;
      const catProgress = catItems.length > 0 ? Math.round((checkedCount / catItems.length) * 100) : 0;

      html += `<div class="checklist-category" style="margin-bottom:16px">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <span style="font-weight:600;color:var(--text-primary)">${escapeHtml(category)}</span>
          <span style="font-size:12px;color:var(--text-secondary)">${checkedCount}/${catItems.length} (${catProgress}%)</span>
        </div>`;

      for (const item of catItems) {
        const checkedClass = item.checked ? 'checked' : '';
        html += `<div class="checklist-item ${checkedClass}" data-item-id="${item.id}" style="display:flex;align-items:flex-start;gap:10px;padding:8px 12px;background:rgba(0,0,0,.2);border-radius:6px;margin-bottom:6px;cursor:pointer" onclick="toggleChecklistItemAnimated('${item.id}', ${!item.checked}, this)">
          <div class="checklist-checkbox" style="width:20px;height:20px;border:2px solid var(--border);border-radius:4px;display:flex;align-items:center;justify-content:center;flex-shrink:0;transition:all 0.3s ease;${item.checked ? 'background:var(--good);border-color:var(--good)' : ''}">
            ${item.checked ? '<i class="fa-solid fa-check checklist-checkmark" style="color:#fff;font-size:11px"></i>' : ''}
          </div>
          <div style="flex:1;min-width:0">
            <div class="checklist-text" style="color:var(--text-primary);word-wrap:break-word;overflow-wrap:break-word;transition:all 0.3s ease;${item.checked ? 'text-decoration:line-through;text-decoration-color:var(--good);opacity:0.7' : ''}">${escapeHtml(item.title)}</div>
            ${item.description ? `<div style="font-size:12px;color:var(--text-secondary);margin-top:4px;white-space:pre-wrap;word-wrap:break-word;overflow-wrap:break-word">${escapeHtml(item.description)}</div>` : ''}
          </div>
          ${item.id.startsWith('custom-') ? `<button class="btn tiny danger" onclick="event.stopPropagation();deleteChecklistItem('${item.id}')" title="Delete"><i class="fa-solid fa-trash"></i></button>` : ''}
        </div>`;
      }
      html += '</div>';
    }

    container.innerHTML = html;
    updateChecklistProgress();
  }

  function updateChecklistProgress() {
    const el = document.getElementById('checklistProgress');
    if (!el) return;
    const items = state.devChecklist || [];
    const checked = items.filter(i => i.checked).length;
    const total = items.length;
    const pct = total > 0 ? Math.round((checked / total) * 100) : 0;
    el.textContent = `${checked}/${total} complete (${pct}%)`;
  }

  function toggleChecklistItem(itemId, checked) {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('TOGGLE_CHECKLIST_ITEM', { itemId, checked });
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
  }

  // Animated version of toggleChecklistItem with visual feedback
  function toggleChecklistItemAnimated(itemId, checked, element) {
    // Add animation class
    if (checked) {
      element.classList.add('checking');
      element.classList.add('checked');

      // Update checkbox visually immediately for responsive feel
      const checkbox = element.querySelector('.checklist-checkbox');
      if (checkbox) {
        checkbox.style.background = 'var(--good)';
        checkbox.style.borderColor = 'var(--good)';
        checkbox.innerHTML = '<i class="fa-solid fa-check checklist-checkmark" style="color:#fff;font-size:11px"></i>';
      }

      // Update text with strikethrough
      const text = element.querySelector('.checklist-text');
      if (text) {
        text.style.textDecoration = 'line-through';
        text.style.textDecorationColor = 'var(--good)';
        text.style.opacity = '0.7';
      }

      // Play satisfying sound
      window.MX?.sounds?.success?.();
    } else {
      element.classList.add('unchecking');
      element.classList.remove('checked');

      // Update checkbox visually
      const checkbox = element.querySelector('.checklist-checkbox');
      if (checkbox) {
        checkbox.style.background = 'transparent';
        checkbox.style.borderColor = 'var(--border)';
        checkbox.innerHTML = '';
      }

      // Remove strikethrough
      const text = element.querySelector('.checklist-text');
      if (text) {
        text.style.textDecoration = 'none';
        text.style.opacity = '1';
      }
    }

    // Remove animation classes after completion
    setTimeout(() => {
      element.classList.remove('checking', 'unchecking');
    }, 500);

    // Send to server
    toggleChecklistItem(itemId, checked);
  }

  window.toggleChecklistItemAnimated = toggleChecklistItemAnimated;

  // Checklist modal state
  let pendingDeleteItemId = null;

  function addChecklistItem() {
    // Open the modal instead of using prompt()
    const overlay = document.getElementById('checklistAddOverlay');
    if (overlay) {
      // Clear previous values
      const titleEl = document.getElementById('checklistItemTitle');
      const categoryEl = document.getElementById('checklistItemCategory');
      const descEl = document.getElementById('checklistItemDesc');
      if (titleEl) titleEl.value = '';
      if (categoryEl) categoryEl.value = 'Custom';
      if (descEl) descEl.value = '';
      overlay.classList.add('show');
      // Focus on title input
      setTimeout(() => titleEl?.focus(), 100);
    }
  }

  function closeChecklistModal() {
    const overlay = document.getElementById('checklistAddOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function submitChecklistItem() {
    const titleEl = document.getElementById('checklistItemTitle');
    const categoryEl = document.getElementById('checklistItemCategory');
    const descEl = document.getElementById('checklistItemDesc');

    const title = titleEl?.value?.trim();
    const category = categoryEl?.value?.trim() || 'Custom';
    const description = descEl?.value?.trim() || '';

    if (!title) {
      toast('error', 'Error', 'Title is required');
      titleEl?.focus();
      return;
    }

    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('ADD_CHECKLIST_ITEM', { title, category, description });
      toast('info', 'Adding', 'Creating checklist item...');
      closeChecklistModal();
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
  }

  function deleteChecklistItem(itemId) {
    // Find the item to show its name in the confirmation modal
    const item = (state.devChecklist || []).find(i => i.id === itemId);
    pendingDeleteItemId = itemId;

    const overlay = document.getElementById('checklistDeleteOverlay');
    const nameEl = document.getElementById('checklistDeleteItemName');
    if (nameEl && item) {
      nameEl.textContent = item.title || 'Unknown item';
    }
    if (overlay) overlay.classList.add('show');
  }

  function closeChecklistDeleteModal() {
    const overlay = document.getElementById('checklistDeleteOverlay');
    if (overlay) overlay.classList.remove('show');
    pendingDeleteItemId = null;
  }

  function confirmDeleteChecklistItem() {
    if (!pendingDeleteItemId) return;

    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('DELETE_CHECKLIST_ITEM', { itemId: pendingDeleteItemId });
      toast('info', 'Deleting', 'Removing checklist item...');
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
    closeChecklistDeleteModal();
  }

  function resetChecklist() {
    // Open the reset confirmation modal
    const overlay = document.getElementById('checklistResetOverlay');
    if (overlay) overlay.classList.add('show');
  }

  function closeChecklistResetModal() {
    const overlay = document.getElementById('checklistResetOverlay');
    if (overlay) overlay.classList.remove('show');
  }

  function confirmResetChecklist() {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      // Uncheck all items
      for (const item of state.devChecklist || []) {
        if (item.checked) {
          ws.send('TOGGLE_CHECKLIST_ITEM', { itemId: item.id, checked: false });
        }
      }
      toast('info', 'Resetting', 'Clearing all checkmarks...');
    } else {
      toast('error', 'Error', 'Not connected to server');
    }
    closeChecklistResetModal();
  }

  function loadDevChecklist() {
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      ws.send('GET_DEV_CHECKLIST');
    }
  }

  // Expose new functions globally
  window.attemptReconnect = attemptReconnect;
  window.toggleSidebar = toggleSidebar;
  window.showAlertBar = showAlertBar;
  window.dismissAlertBar = dismissAlertBar;
  window.viewAlertPlayer = viewAlertPlayer;
  window.togglePanelSounds = togglePanelSounds;
  window.toggleDeviceTrust = toggleDeviceTrust;
  window.toggleDebugMode = toggleDebugMode;
  window.toggleWatchlistAlerts = toggleWatchlistAlerts;
  window.setVolume = setVolume;
  window.setThemeColor = setThemeColor;
  window.setBackgroundPattern = setBackgroundPattern;
  window.applyThemeFromState = applyThemeFromState;
  window.showDisconnect = showDisconnect;
  window.hideDisconnect = hideDisconnect;
  window.updateLastPong = updateLastPong;
  window.toggleChecklistItem = toggleChecklistItem;
  window.addChecklistItem = addChecklistItem;
  window.closeChecklistModal = closeChecklistModal;
  window.submitChecklistItem = submitChecklistItem;
  window.deleteChecklistItem = deleteChecklistItem;
  window.closeChecklistDeleteModal = closeChecklistDeleteModal;
  window.confirmDeleteChecklistItem = confirmDeleteChecklistItem;
  window.resetChecklist = resetChecklist;
  window.closeChecklistResetModal = closeChecklistResetModal;
  window.confirmResetChecklist = confirmResetChecklist;
  window.loadDevChecklist = loadDevChecklist;

  // ===== DEVELOPER MODE =====
  function toggleDevMode() {
    state.settings = state.settings || {};
    state.settings.developerMode = !state.settings.developerMode;

    const toggle = document.getElementById('devModeToggle');
    const devSection = document.getElementById('sbDevSection');

    if (toggle) toggle.classList.toggle('active', state.settings.developerMode);
    if (devSection) devSection.style.display = state.settings.developerMode ? 'block' : 'none';

    saveState();
    toast('info', 'Developer Mode', state.settings.developerMode ? 'Enabled' : 'Disabled');

    // Load checklist when enabling
    if (state.settings.developerMode) {
      loadDevChecklist();
    }
  }

  function applyDevModeUI() {
    const toggle = document.getElementById('devModeToggle');
    const devSection = document.getElementById('sbDevSection');

    if (toggle) toggle.classList.toggle('active', state.settings?.developerMode || false);
    if (devSection) devSection.style.display = state.settings?.developerMode ? 'block' : 'none';
  }

  window.toggleDevMode = toggleDevMode;
  window.applyDevModeUI = applyDevModeUI;

  // ===== STRESS TESTING =====
  // Now uses server-side data generation for realistic testing
  let activeStressTests = new Map();
  let stressTestCleanupTimers = [];

  /**
   * Create test players via server
   * Server will add actual players to the database
   */
  function startCreateTestPlayers() {
    const count = Math.min(10000, Math.max(1, parseInt(document.getElementById('spoofPlayerCount')?.value || '100', 10)));

    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server for stress testing');
      return;
    }

    if (!confirm(`This will create ${count.toLocaleString()} TEST players in the database.\n\nThese players will have names like "TestPlayer_1234" and can be cleaned up later.\n\nContinue?`)) {
      return;
    }

    const progressEl = document.getElementById('spoofPlayerProgress');
    const fillEl = document.getElementById('spoofPlayerFill');
    const logEl = document.getElementById('spoofPlayerLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = '<div class="stress-log-entry">Requesting server to create test players...</div>';

    // Send request to server
    window.MX.ws.send('DEV_STRESS_CREATE_PLAYERS', {
      count: count,
      timestamp: Date.now()
    });

    toast('info', 'Stress Test', `Requesting server to create ${count.toLocaleString()} test players...`);
  }

  /**
   * Create test punishments via server
   * Server will add actual punishments to the database
   */
  function startCreateTestPunishments() {
    const count = Math.min(5000, Math.max(1, parseInt(document.getElementById('spoofPunishmentCount')?.value || '500', 10)));

    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server for stress testing');
      return;
    }

    if (!confirm(`This will create ${count.toLocaleString()} TEST punishments in the database.\n\nThese punishments will be marked as test data and can be cleaned up later.\n\nContinue?`)) {
      return;
    }

    const progressEl = document.getElementById('spoofPunishmentProgress');
    const fillEl = document.getElementById('spoofPunishmentFill');
    const logEl = document.getElementById('spoofPunishmentLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = '<div class="stress-log-entry">Requesting server to create test punishments...</div>';

    // Send request to server
    window.MX.ws.send('DEV_STRESS_CREATE_PUNISHMENTS', {
      count: count,
      timestamp: Date.now()
    });

    toast('info', 'Stress Test', `Requesting server to create ${count.toLocaleString()} test punishments...`);
  }

  /**
   * Request server to clean up all test data
   */
  function cleanupTestData() {
    if (!window.MX.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server to clean up test data');
      return;
    }

    if (!confirm('This will remove ALL test data created by stress tests from the database.\n\nThis includes test players, test punishments, and test tokens.\n\nContinue?')) {
      return;
    }

    window.MX.ws.send('DEV_STRESS_CLEANUP', {
      timestamp: Date.now()
    });

    toast('info', 'Cleanup', 'Requesting server to clean up test data...');
  }

  // Handler for stress test progress updates from server
  function handleStressTestProgress(data) {
    const { testType, current, total, complete, duration, error, message } = data;

    // Map test types to element IDs
    const elementMap = {
      'players': 'spoofPlayer',
      'punishments': 'spoofPunishment',
      'tokens': 'tokenStress'
    };

    const testId = elementMap[testType] || testType;
    const fillEl = document.getElementById(`${testId}Fill`);
    const logEl = document.getElementById(`${testId}Log`);

    if (error) {
      if (logEl) logEl.innerHTML = `<div class="stress-log-entry error">${escapeHtml(message || error)}</div>`;
      toast('bad', 'Stress Test Error', message || error);
      return;
    }

    if (complete) {
      if (fillEl) fillEl.style.width = '100%';
      if (logEl) {
        const perSec = duration > 0 ? Math.round(total / (duration / 1000)) : total;
        logEl.innerHTML = `<div class="stress-log-entry success">Complete! Created ${total.toLocaleString()} items in ${duration}ms (${perSec.toLocaleString()}/sec)</div>`;
      }
      toast('ok', 'Stress Test Complete', `Created ${total.toLocaleString()} test ${testType}`);

      // Refresh data from server
      window.MX.ws.send('GET_DATA', {});
    } else {
      const pct = Math.round((current / total) * 100);
      if (fillEl) fillEl.style.width = `${pct}%`;
      if (logEl) {
        logEl.innerHTML = `<div class="stress-log-entry">Creating ${current.toLocaleString()} / ${total.toLocaleString()} (${pct}%)</div>`;
      }
    }
  }

  // Register the handler
  if (window.MX.ws) {
    window.MX.ws.on('DEV_STRESS_PROGRESS', handleStressTestProgress);
    window.MX.ws.on('DEV_STRESS_COMPLETE', handleStressTestProgress);
    window.MX.ws.on('DEV_STRESS_ERROR', handleStressTestProgress);
  }

  function stopAllStressTests() {
    if (window.MX.ws?.isConnected()) {
      window.MX.ws.send('DEV_STRESS_STOP', {});
    }
    activeStressTests.clear();
    toast('info', 'Stress Tests', 'Stop request sent to server.');
  }

  // Legacy function names for compatibility
  function startSpoofPlayers() { startCreateTestPlayers(); }
  function startSpoofPunishments() { startCreateTestPunishments(); }
  function clearAllSpoofedData() { cleanupTestData(); }

  // Staff spoofing is local-only (no database table)
  function startSpoofStaff() {
    toast('info', 'Info', 'Staff stress testing only affects local UI display.');
    const count = Math.min(50000, Math.max(1, parseInt(document.getElementById('spoofStaffCount')?.value || '50', 10)));

    if (!state.staffList) state.staffList = [];
    for (let i = 0; i < count; i++) {
      state.staffList.push({
        id: `test-staff-${i}`,
        name: `TestStaff${i}`,
        rank: ['Admin', 'Moderator', 'Helper'][Math.floor(Math.random() * 3)],
        online: Math.random() > 0.5,
        _test: true
      });
    }
    ui.renderDashboard();
    toast('ok', 'Complete', `Added ${count} test staff to local display.`);
  }

  // Automod Rules Stress Test - Creates actual rules via API
  let automodStressTestActive = false;
  let automodStressTestCreated = [];

  function generateUniqueWords(count, prefix) {
    const words = [];
    const adjectives = ['bad', 'toxic', 'spam', 'evil', 'mean', 'rude', 'awful', 'nasty', 'vile', 'crude'];
    const nouns = ['word', 'phrase', 'term', 'text', 'msg', 'chat', 'speak', 'talk', 'say', 'type'];
    const suffixes = ['er', 'ing', 'ed', 'ly', 'ish', 'ness', 'ment', 'tion', 'able', 'ful'];

    for (let i = 0; i < count; i++) {
      const adj = adjectives[Math.floor(Math.random() * adjectives.length)];
      const noun = nouns[Math.floor(Math.random() * nouns.length)];
      const suffix = suffixes[Math.floor(Math.random() * suffixes.length)];
      const num = Math.floor(Math.random() * 9999);
      words.push(`${prefix}_${adj}${noun}${suffix}${num}`);
    }
    return words;
  }

  function generateExclusions(count) {
    const words = [];
    const safePrefixes = ['admin', 'mod', 'helper', 'staff', 'owner', 'vip', 'donor', 'member', 'player', 'user'];
    const safeWords = ['allowed', 'safe', 'ok', 'fine', 'good', 'nice', 'cool', 'great', 'awesome', 'valid'];

    for (let i = 0; i < count; i++) {
      const prefix = safePrefixes[Math.floor(Math.random() * safePrefixes.length)];
      const word = safeWords[Math.floor(Math.random() * safeWords.length)];
      const num = Math.floor(Math.random() * 999);
      words.push(`${prefix}_${word}_${num}`);
    }
    return words;
  }

  async function startAutomodRulesStressTest() {
    if (!window.MX?.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server for stress testing');
      return;
    }

    if (automodStressTestActive) {
      toast('warn', 'In Progress', 'Stress test already running');
      return;
    }

    const ruleCount = Math.min(100, Math.max(1, parseInt(document.getElementById('stressAutomodRuleCount')?.value || '10', 10)));
    const triggersPerRule = 200;
    const exclusionsPerRule = 35;

    automodStressTestActive = true;
    automodStressTestCreated = [];

    const progressEl = document.getElementById('stressAutomodProgress');
    const fillEl = document.getElementById('stressAutomodFill');
    const logEl = document.getElementById('stressAutomodLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = `<div class="stress-log-entry">Creating ${ruleCount} rules with ${triggersPerRule} triggers and ${exclusionsPerRule} exclusions each...</div>`;

    toast('info', 'Stress Test', `Creating ${ruleCount} automod rules...`);

    for (let i = 0; i < ruleCount; i++) {
      if (!automodStressTestActive) break;

      const ruleName = `StressTest_Rule_${Date.now()}_${i}`;
      const triggers = generateUniqueWords(triggersPerRule, `trig${i}`);
      const exclusions = generateExclusions(exclusionsPerRule);

      // Create rule via API
      window.MX.ws.send('CREATE_AUTOMOD_RULE', {
        name: ruleName,
        exactMatch: false,
        blacklistedWords: triggers,
        exclusionWords: exclusions,
        _stressTest: true
      });

      automodStressTestCreated.push(ruleName);

      // Update progress
      const progress = Math.round(((i + 1) / ruleCount) * 100);
      if (fillEl) fillEl.style.width = `${progress}%`;
      if (logEl) logEl.innerHTML = `<div class="stress-log-entry">Created rule ${i + 1}/${ruleCount}: ${ruleName}</div>`;

      // Small delay to prevent overwhelming the server
      await new Promise(resolve => setTimeout(resolve, 100));
    }

    automodStressTestActive = false;
    if (fillEl) fillEl.style.width = '100%';
    if (logEl) logEl.innerHTML += `<div class="stress-log-entry" style="color:var(--ok)">Complete! Created ${automodStressTestCreated.length} rules.</div>`;
    toast('ok', 'Complete', `Created ${automodStressTestCreated.length} automod rules with ${triggersPerRule * automodStressTestCreated.length} total triggers.`);
  }

  function cleanupAutomodStressTest() {
    if (!window.MX?.ws?.isConnected()) {
      toast('warn', 'Not Connected', 'Must be connected to server');
      return;
    }

    // Find all stress test rules in state
    const stressRules = state.rules.filter(r => r.name && r.name.startsWith('StressTest_Rule_'));

    if (stressRules.length === 0) {
      toast('info', 'No Rules', 'No stress test rules found to delete.');
      return;
    }

    if (!confirm(`This will delete ${stressRules.length} stress test rules. Continue?`)) {
      return;
    }

    const logEl = document.getElementById('stressAutomodLog');
    if (logEl) logEl.innerHTML = `<div class="stress-log-entry">Deleting ${stressRules.length} stress test rules...</div>`;

    let deleted = 0;
    stressRules.forEach(rule => {
      window.MX.ws.send('DELETE_AUTOMOD_RULE', { id: rule.id });
      deleted++;
    });

    toast('ok', 'Cleanup', `Sent delete requests for ${deleted} stress test rules.`);
    automodStressTestCreated = [];
  }

  window.startAutomodRulesStressTest = startAutomodRulesStressTest;
  window.cleanupAutomodStressTest = cleanupAutomodStressTest;

  // Clear local test data (for staff and automod which are local-only)
  function clearLocalTestData() {
    if (state.staffList) state.staffList = state.staffList.filter(s => !s._test);
    state.watchAlerts = state.watchAlerts.filter(a => !a._test);
    ui.renderAll();
    toast('ok', 'Cleared', 'Local test data has been removed.');
  }

  // Debug log functions
  function clearDebugLogs() {
    const logEl = document.getElementById('devDebugLogs');
    if (logEl) logEl.innerHTML = '<div style="color:var(--muted)">[Debug console cleared]</div>';
  }

  function copyDebugLogs() {
    const logEl = document.getElementById('devDebugLogs');
    if (logEl) {
      navigator.clipboard.writeText(logEl.innerText).then(() => {
        toast('ok', 'Copied', 'Debug logs copied to clipboard.');
      });
    }
  }

  window.startSpoofPlayers = startSpoofPlayers;
  window.startSpoofStaff = startSpoofStaff;
  window.startSpoofPunishments = startSpoofPunishments;
  window.startCreateTestPlayers = startCreateTestPlayers;
  window.startCreateTestPunishments = startCreateTestPunishments;
  window.stopAllStressTests = stopAllStressTests;
  window.clearAllSpoofedData = clearAllSpoofedData;
  window.cleanupTestData = cleanupTestData;
  window.clearLocalTestData = clearLocalTestData;
  window.clearDebugLogs = clearDebugLogs;
  window.copyDebugLogs = copyDebugLogs;

  // ===== NOTIFICATION TESTING =====
  function testNotification(category) {
    const testMessages = {
      punishments: { title: 'Punishment', message: 'TestPlayer was banned by TestAdmin' },
      automod: { title: 'Automod', message: 'TestPlayer triggered Spam Filter', severity: 'warn' },
      anticheat: { title: 'Anticheat', message: 'TestPlayer flagged for Reach (VL: 15)', severity: 'warn' },
      watchlist: { title: 'Watchlist', message: 'TestPlayer: Suspicious activity detected', severity: 'warn' },
      staffChat: { title: 'Staff Chat', message: 'TestAdmin: This is a test message' }
    };

    const test = testMessages[category];
    if (!test) {
      toast('error', 'Error', `Unknown category: ${category}`);
      return;
    }

    // Show the alert using the showPanelAlert function which respects settings
    showPanelAlert(category, test.title, test.message, {
      severity: test.severity || 'info',
      playerId: 'test-player-uuid',
      playerName: 'TestPlayer'
    });

    devtoolsLog('NOTIFY', `Tested ${category} notification (mode: ${state.staffSettings?.['webNotify' + category.charAt(0).toUpperCase() + category.slice(1)] || 'toast'})`, 'info');
  }

  function requestBrowserPermission() {
    if (!('Notification' in window)) {
      toast('error', 'Not Supported', 'Browser notifications are not supported in this browser.');
      return;
    }

    if (Notification.permission === 'granted') {
      toast('ok', 'Already Granted', 'Browser notification permission is already granted.');
      new Notification('ModereX', { body: 'Browser notifications are working!', icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      return;
    }

    if (Notification.permission === 'denied') {
      toast('error', 'Permission Denied', 'Browser notifications were previously denied. Reset in browser settings.');
      return;
    }

    Notification.requestPermission().then(permission => {
      if (permission === 'granted') {
        toast('ok', 'Permission Granted', 'Browser notifications are now enabled.');
        new Notification('ModereX', { body: 'Browser notifications are working!', icon: '/537154108207028818e303ef9465c1f66717660d_96.png' });
      } else {
        toast('warn', 'Permission Denied', 'Browser notifications were denied.');
      }
    });
  }

  window.testNotification = testNotification;
  window.requestBrowserPermission = requestBrowserPermission;

  // ===== DEBUG PERMISSIONS =====
  function debugCheckPermissions() {
    const outputEl = document.getElementById('debugPermissionsOutput');
    if (outputEl) {
      outputEl.style.display = 'block';
      outputEl.innerHTML = '<div style="color:#06b6d4">Checking permissions...</div>';
    }

    // Get current state
    const permissions = state.permissions || [];
    const staffSettings = state.staffSettings || {};
    const user = state.currentUser || {};

    let html = '<div style="margin-bottom:12px;color:#22c55e;font-weight:600">Current Permissions State:</div>';

    // User info
    html += `<div style="margin-bottom:8px"><span style="color:#a78bfa">User:</span> ${user.name || 'Unknown'} (${user.uuid || 'No UUID'})</div>`;
    html += `<div style="margin-bottom:8px"><span style="color:#a78bfa">Authenticated:</span> ${user.name ? 'Yes' : 'No'}</div>`;

    // Permissions array
    html += '<div style="margin-bottom:8px"><span style="color:#a78bfa">Permissions Array:</span></div>';
    if (permissions.length === 0) {
      html += '<div style="color:#ef4444;margin-left:12px">⚠ No permissions received from server!</div>';
      html += '<div style="color:#f59e0b;margin-left:12px;font-size:11px">This is why settings show "No Permission"</div>';
    } else {
      permissions.forEach(p => {
        html += `<div style="margin-left:12px;color:#22c55e">✓ ${p}</div>`;
      });
    }

    // Alert-related settings
    html += '<div style="margin-top:12px;margin-bottom:8px"><span style="color:#a78bfa">Staff Settings (alert-related):</span></div>';
    const alertKeys = ['banAlerts', 'kickAlerts', 'muteAlerts', 'warnAlerts', 'pardonAlerts', 'automodAlerts', 'anticheatAlerts', 'nicknameAlerts', 'commandAlerts'];
    alertKeys.forEach(key => {
      const value = staffSettings[key];
      html += `<div style="margin-left:12px">${key}: <span style="color:${value ? '#22c55e' : '#6b7280'}">${value || 'not set'}</span></div>`;
    });

    // Show in output
    if (outputEl) {
      outputEl.innerHTML = html;
    }

    // Also log to system messages
    window.systemLog(`Permissions check: ${permissions.length} permissions found`, permissions.length > 0 ? 'success' : 'error');
    window.devtoolsLog('PERMISSIONS', `Found ${permissions.length} permissions: ${permissions.join(', ') || 'NONE'}`, permissions.length > 0 ? 'success' : 'error');

    // Log any errors
    if (permissions.length === 0) {
      window.systemLog('ERROR: No permissions received from backend!', 'error');
      window.devtoolsLog('PERMISSIONS', 'Backend is not sending permissions array in USER_SETTINGS_DATA response', 'error');
    }
  }

  function debugRefreshPermissions() {
    const outputEl = document.getElementById('debugPermissionsOutput');
    if (outputEl) {
      outputEl.style.display = 'block';
      outputEl.innerHTML = '<div style="color:#f59e0b">Requesting fresh permissions from server...</div>';
    }

    window.systemLog('Requesting permissions refresh from server...', 'info');
    window.devtoolsLog('PERMISSIONS', 'Sending GET_USER_SETTINGS request', 'info');

    // Request fresh settings from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_USER_SETTINGS', {});

      // Wait a moment and check again
      setTimeout(() => {
        debugCheckPermissions();
      }, 1000);
    } else {
      window.systemLog('ERROR: WebSocket not connected', 'error');
      if (outputEl) {
        outputEl.innerHTML = '<div style="color:#ef4444">WebSocket not connected!</div>';
      }
    }
  }

  window.debugCheckPermissions = debugCheckPermissions;
  window.debugRefreshPermissions = debugRefreshPermissions;

  // ===== DATABASE DEBUG =====
  function showDatabaseOutput(html) {
    const outputEl = document.getElementById('debugDatabaseOutput');
    if (outputEl) {
      outputEl.style.display = 'block';
      outputEl.innerHTML = html;
    }
  }

  function debugLoadDatabaseStats() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading database statistics...</div>');

    // Request database stats from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_DATABASE_DEBUG', { type: 'stats' });
    } else {
      showDatabaseOutput('<div style="color:#ef4444">WebSocket not connected!</div>');
    }
  }

  function debugLoadAutomodRules() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading automod rules...</div>');

    // Use existing state data
    const rules = state.rules || [];
    let html = `<div style="margin-bottom:8px;color:#22c55e;font-weight:600">Automod Rules (${rules.length}):</div>`;

    if (rules.length === 0) {
      html += '<div style="color:#f59e0b;margin-left:12px">No automod rules found</div>';
    } else {
      rules.forEach((rule, i) => {
        html += `<div style="margin-left:12px;padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.1)">`;
        html += `<span style="color:#a78bfa">[${i + 1}]</span> `;
        html += `<span style="color:${rule.enabled ? '#22c55e' : '#ef4444'}">${rule.enabled ? '✓' : '✗'}</span> `;
        html += `<span style="color:#fff">${escapeHtml(rule.name || 'Unnamed')}</span> `;
        html += `<span style="color:#64748b">(${rule.type || 'unknown'})</span>`;
        html += `</div>`;
      });
    }
    showDatabaseOutput(html);
  }

  function debugLoadWatchlist() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading watchlist...</div>');

    // Request watchlist data from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_DATABASE_DEBUG', { type: 'watchlist' });
    } else {
      showDatabaseOutput('<div style="color:#ef4444">WebSocket not connected!</div>');
    }
  }

  function debugLoadPunishments() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading punishments...</div>');

    const punishments = state.punishments || [];
    let html = `<div style="margin-bottom:8px;color:#22c55e;font-weight:600">Punishments (${punishments.length} loaded):</div>`;

    const recent = punishments.slice(0, 20);
    if (recent.length === 0) {
      html += '<div style="color:#f59e0b;margin-left:12px">No punishments found</div>';
    } else {
      recent.forEach((p, i) => {
        html += `<div style="margin-left:12px;padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.1)">`;
        html += `<span style="color:#a78bfa">[${i + 1}]</span> `;
        const typeColors = { BAN: '#ef4444', MUTE: '#f59e0b', WARN: '#eab308', KICK: '#3b82f6' };
        html += `<span style="color:${typeColors[p.type] || '#64748b'}">${p.type}</span> `;
        html += `<span style="color:#fff">${escapeHtml(p.targetName || 'Unknown')}</span> `;
        html += `<span style="color:#64748b">by ${escapeHtml(p.staffName || 'Console')}</span>`;
        html += `</div>`;
      });
      if (punishments.length > 20) {
        html += `<div style="color:#64748b;margin-top:8px;margin-left:12px">...and ${punishments.length - 20} more</div>`;
      }
    }
    showDatabaseOutput(html);
  }

  function debugLoadCmdBlacklist() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading command blacklist...</div>');

    const blacklist = state.cmdBlacklist || [];
    let html = `<div style="margin-bottom:8px;color:#22c55e;font-weight:600">Command Blacklist (${blacklist.length}):</div>`;

    if (blacklist.length === 0) {
      html += '<div style="color:#f59e0b;margin-left:12px">No command blacklist entries found</div>';
    } else {
      blacklist.forEach((entry, i) => {
        const isActive = entry.expiresAt === -1 || entry.expiresAt > Date.now();
        html += `<div style="margin-left:12px;padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.1)">`;
        html += `<span style="color:#a78bfa">[${i + 1}]</span> `;
        html += `<span style="color:${isActive ? '#ef4444' : '#64748b'}">${isActive ? '⬤' : '○'}</span> `;
        html += `<span style="color:#fff">${escapeHtml(entry.playerName || 'Unknown')}</span> `;
        html += `<span style="color:#8b5cf6">/${escapeHtml(entry.command || 'unknown')}</span>`;
        html += `</div>`;
      });
    }
    showDatabaseOutput(html);
  }

  function debugLoadActivityLogs() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading activity logs summary...</div>');

    // Request activity log stats from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_DATABASE_DEBUG', { type: 'activity_logs' });
    } else {
      showDatabaseOutput('<div style="color:#ef4444">WebSocket not connected!</div>');
    }
  }

  function debugLoadAutomodAlerts() {
    showDatabaseOutput('<div style="color:#06b6d4">Loading automod alerts from database...</div>');

    // Request automod alerts from server
    if (window.MX?.ws?.send) {
      window.MX.ws.send('GET_DATABASE_DEBUG', { type: 'automod_alerts' });
    } else {
      showDatabaseOutput('<div style="color:#ef4444">WebSocket not connected!</div>');
    }
  }

  // Handle database debug responses
  window.handleDatabaseDebugResponse = function(data) {
    if (data.type === 'stats') {
      let html = '<div style="margin-bottom:12px;color:#22c55e;font-weight:600">Database Statistics:</div>';
      html += `<div style="margin-left:12px"><span style="color:#a78bfa">Database Size:</span> ${data.size || 'Unknown'}</div>`;
      html += `<div style="margin-left:12px"><span style="color:#a78bfa">Type:</span> ${data.dbType || 'SQLite'}</div>`;
      html += '<div style="margin-top:12px;margin-bottom:8px;color:#22c55e;font-weight:600">Table Row Counts:</div>';
      if (data.tables) {
        Object.entries(data.tables).forEach(([table, count]) => {
          html += `<div style="margin-left:12px"><span style="color:#64748b">${escapeHtml(table)}:</span> <span style="color:#fff">${count.toLocaleString()}</span></div>`;
        });
      }
      showDatabaseOutput(html);
    } else if (data.type === 'watchlist') {
      let html = `<div style="margin-bottom:8px;color:#22c55e;font-weight:600">Watchlist (${data.entries?.length || 0}):</div>`;
      if (!data.entries || data.entries.length === 0) {
        html += '<div style="color:#f59e0b;margin-left:12px">No watchlist entries found</div>';
      } else {
        data.entries.forEach((entry, i) => {
          html += `<div style="margin-left:12px;padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.1)">`;
          html += `<span style="color:#a78bfa">[${i + 1}]</span> `;
          html += `<span style="color:#fff">${escapeHtml(entry.playerName || 'Unknown')}</span> `;
          html += `<span style="color:#64748b">(${entry.uuid?.substring(0, 8) || '?'})</span>`;
          if (entry.reason) html += ` <span style="color:#f59e0b">- ${escapeHtml(entry.reason)}</span>`;
          html += `</div>`;
        });
      }
      showDatabaseOutput(html);
    } else if (data.type === 'activity_logs') {
      let html = '<div style="margin-bottom:12px;color:#22c55e;font-weight:600">Activity Log Summary:</div>';
      if (data.counts) {
        Object.entries(data.counts).forEach(([type, count]) => {
          html += `<div style="margin-left:12px"><span style="color:#64748b">${escapeHtml(type)}:</span> <span style="color:#fff">${count.toLocaleString()}</span></div>`;
        });
      }
      html += `<div style="margin-top:8px;margin-left:12px;color:#64748b">Total: ${data.total?.toLocaleString() || 0} entries</div>`;
      showDatabaseOutput(html);
    } else if (data.type === 'automod_alerts') {
      let html = `<div style="margin-bottom:12px;color:#22c55e;font-weight:600">Automod Alerts (${data.total || 0} total, showing ${data.entries?.length || 0}):</div>`;
      if (!data.entries || data.entries.length === 0) {
        html += '<div style="color:#f59e0b;margin-left:12px">No automod alerts found in database</div>';
      } else {
        data.entries.forEach((entry, i) => {
          html += `<div style="margin-left:12px;padding:6px 0;border-bottom:1px solid rgba(255,255,255,0.1)">`;
          html += `<div><span style="color:#a78bfa">[${i + 1}]</span> `;
          html += `<span style="color:#fff">${escapeHtml(entry.playerName || 'Unknown')}</span> `;
          html += `<span style="color:#64748b">(${entry.playerUuid?.substring(0, 8) || '?'})</span></div>`;
          html += `<div style="margin-left:24px;color:#06b6d4">Rule: ${escapeHtml(entry.rule || 'N/A')}</div>`;
          html += `<div style="margin-left:24px;color:#94a3b8">Content: ${escapeHtml(entry.content || 'N/A')}</div>`;
          html += `<div style="margin-left:24px;color:#64748b">Time: ${fmtLong(entry.timestamp)}</div>`;
          html += `</div>`;
        });
      }
      showDatabaseOutput(html);
    }
  };

  window.debugLoadDatabaseStats = debugLoadDatabaseStats;
  window.debugLoadAutomodRules = debugLoadAutomodRules;
  window.debugLoadWatchlist = debugLoadWatchlist;
  window.debugLoadPunishments = debugLoadPunishments;
  window.debugLoadCmdBlacklist = debugLoadCmdBlacklist;
  window.debugLoadActivityLogs = debugLoadActivityLogs;
  window.debugLoadAutomodAlerts = debugLoadAutomodAlerts;

  // ===== TOKEN STRESS TEST =====
  function startTokenStressTest() {
    const count = Math.min(50000, Math.max(100, parseInt(document.getElementById('tokenStressCount')?.value || '1000', 10)));

    // Show warning modal first
    if (!confirm(`This will generate ${count.toLocaleString()} tokens and test authentication speed.\n\nYou will be LOGGED OUT after this test completes and must re-authenticate with your permanent token.\n\nDevice fingerprint bypass will be enabled - you MUST enter your token manually.\n\nContinue?`)) {
      return;
    }

    toast('info', 'Token Stress Test', `Generating ${count.toLocaleString()} tokens...`);

    // Send request to server to start token stress test
    window.MX.ws.send('DEV_TOKEN_STRESS_TEST', {
      count: count,
      timestamp: Date.now()
    });

    // Show progress
    const progressEl = document.getElementById('tokenStressProgress');
    const fillEl = document.getElementById('tokenStressFill');
    const logEl = document.getElementById('tokenStressLog');

    if (progressEl) progressEl.style.display = 'block';
    if (fillEl) fillEl.style.width = '0%';
    if (logEl) logEl.innerHTML = 'Starting token generation...';

    // Listen for progress updates
    const progressHandler = (data) => {
      if (data.type === 'TOKEN_STRESS_PROGRESS') {
        const pct = Math.round((data.current / data.total) * 100);
        if (fillEl) fillEl.style.width = pct + '%';
        if (logEl) logEl.innerHTML = `Generated ${data.current.toLocaleString()} / ${data.total.toLocaleString()} tokens (${pct}%)`;
      } else if (data.type === 'TOKEN_STRESS_COMPLETE') {
        if (fillEl) fillEl.style.width = '100%';
        if (logEl) logEl.innerHTML = `Complete! ${data.total.toLocaleString()} tokens in ${data.duration}ms (${data.tokensPerSecond} tokens/sec)`;
        toast('ok', 'Stress Test Complete', `Validated ${data.total.toLocaleString()} tokens in ${data.duration}ms`);

        // Force logout after 2 seconds
        setTimeout(() => {
          toast('warn', 'Logging Out', 'Re-authenticate with your permanent token.');
          localStorage.removeItem('mx_permanent_token');
          localStorage.removeItem('mx_token_device');
          localStorage.removeItem('mx_session');
          window.MX.auth.logout();
        }, 2000);
      }
    };

    window.MX.ws.on('message', progressHandler);
  }

  // ===== UUID DEV AUTHENTICATION =====
  function devUuidAuth() {
    const uuidInput = document.getElementById('devUuidInput');
    const uuid = uuidInput?.value?.trim();

    if (!uuid) {
      toast('bad', 'UUID Required', 'Please enter a valid UUID.');
      return;
    }

    // Basic UUID format validation
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
    if (!uuidRegex.test(uuid)) {
      toast('bad', 'Invalid UUID', 'UUID must be in format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx');
      return;
    }

    toast('info', 'Dev Auth', `Authenticating as UUID: ${uuid.substring(0, 8)}...`);

    // Send dev auth request
    window.MX.ws.send('AUTH_DEV_UUID', {
      uuid: uuid,
      timestamp: Date.now()
    });
  }

  window.startTokenStressTest = startTokenStressTest;
  window.devUuidAuth = devUuidAuth;

  // ===== WEB PANEL VERSION & GITHUB UPDATE CHECKER =====
  let panelVersionInfo = null;
  let currentPluginVersion = null;
  let latestGitHubVersion = null;
  let updateCheckInterval = null;
  let updateDismissedUntil = 0; // Timestamp when dismiss expires
  let currentBuildNumber = null;

  function loadPanelVersion() {
    // In gateway mode, use WebSocket since HTTP fetch doesn't work
    if (window.MX?.ws?.isGatewayMode && window.MX.ws.isGatewayMode()) {
      // Check if WebSocket is actually connected before sending
      if (!window.MX.ws.isConnected()) {
        // Not connected yet - wait for gateway_connected event
        const onGatewayReady = () => {
          window.MX.ws.off('gateway_connected', onGatewayReady);
          // Small delay to ensure connection is stable
          setTimeout(() => {
            if (window.MX.ws.isConnected()) {
              window.MX.ws.send('GET_PANEL_VERSION');
            }
          }, 500);
        };
        window.MX.ws.on('gateway_connected', onGatewayReady);
        return;
      }
      // Request version via WebSocket
      window.MX.ws.send('GET_PANEL_VERSION');
      // Response is handled by the PANEL_VERSION handler below
      return;
    }

    // Direct mode: Fetch version from server API (reads from panel-version.properties)
    fetch('/api/panel-version?_=' + Date.now())
      .then(res => res.json())
      .then(data => {
        handlePanelVersionData(data);
      })
      .catch(() => {
        // Silent fail - version display is optional
      });
  }

  // Handle panel version data (used by both HTTP and WebSocket responses)
  function handlePanelVersionData(data) {
    panelVersionInfo = data;
    // Update sidebar version display
    const versionEl = document.getElementById('panelVersion');
    if (versionEl && data.version) {
      versionEl.textContent = data.version;
    }
    // Store build number for comparison
    if (data.buildNumber) {
      currentBuildNumber = parseInt(data.buildNumber, 10);
    }
  }

  function loadCurrentPluginVersion() {
    // Get current plugin version from server
    fetch('/api/plugin-version?_=' + Date.now())
      .then(res => res.json())
      .then(data => {
        console.log('[Update] Plugin version response:', data);
        if (data.version) {
          currentPluginVersion = data.version;
          // Store build number from plugin version
          if (data.buildNumber) {
            currentBuildNumber = parseInt(data.buildNumber, 10);
          }
          console.log('[Update] Current plugin version:', currentPluginVersion, 'build:', currentBuildNumber);
          // Start checking GitHub for updates
          checkGitHubForUpdates();
          // Check every 10 seconds
          if (!updateCheckInterval) {
            updateCheckInterval = setInterval(checkGitHubForUpdates, 10000);
          }
        }
      })
      .catch(err => {
        console.log('[Update] Failed to get plugin version:', err.message);
      });
  }

  function checkGitHubForUpdates() {
    // Check if dismissed (10 minute cooldown)
    if (Date.now() < updateDismissedUntil) return;

    // Fetch build-info.txt from GitHub releases
    fetch('https://raw.githubusercontent.com/Midnwave/ModereX/main/releases/build-info.txt?_=' + Date.now())
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch');
        return res.text();
      })
      .then(text => {
        // Parse build-info.txt (format: timestamp, Version: X, Build: Y, Commit: Z)
        const lines = text.split('\n');
        let version = null;
        let buildNumber = null;
        let buildDate = null;

        for (const line of lines) {
          const trimmed = line.trim();
          // First line is timestamp
          if (trimmed.match(/^\d{4}-\d{2}-\d{2}T/)) {
            buildDate = trimmed;
          } else if (trimmed.startsWith('Version:')) {
            version = trimmed.substring(8).trim();
          } else if (trimmed.startsWith('Build:')) {
            buildNumber = parseInt(trimmed.substring(6).trim(), 10);
          }
        }

        console.log('[Update] GitHub build info:', { version, buildNumber, buildDate });
        console.log('[Update] Current version:', currentPluginVersion, 'Current build:', currentBuildNumber);

        // Compare build numbers if available, otherwise fall back to version string
        const hasUpdate = buildNumber && currentBuildNumber
          ? buildNumber > currentBuildNumber
          : (version && version !== currentPluginVersion);

        if (hasUpdate) {
          latestGitHubVersion = version;
          showPluginUpdateBanner(version, buildDate, buildNumber);
        }
      })
      .catch(err => {
        console.log('[Update] Failed to check GitHub:', err.message);
      });
  }

  function showPluginUpdateBanner(newVersion, buildDate, buildNumber) {
    const banner = document.getElementById('updateBanner');
    if (!banner) return;

    const titleEl = banner.querySelector('.update-title');
    const versionEl = banner.querySelector('.update-version');
    const notesEl = banner.querySelector('.update-notes');

    if (titleEl) titleEl.textContent = 'Plugin Update Available';
    if (versionEl) {
      let versionText = newVersion || 'New Version';
      if (buildNumber) versionText += ` (Build ${buildNumber})`;
      if (buildDate) versionText += ' • ' + buildDate.split('T')[0]; // Just the date part
      versionEl.textContent = versionText;
    }
    if (notesEl) notesEl.textContent = 'New version available on GitHub';

    // Reset the update button in case it was disabled from a previous attempt
    const btn = banner.querySelector('.btn.primary');
    if (btn) {
      btn.disabled = false;
      btn.innerHTML = '<i class="fa-solid fa-download"></i> Update Now';
    }

    banner.classList.add('show');
  }

  function dismissUpdateBanner() {
    const banner = document.getElementById('updateBanner');
    if (banner) banner.classList.remove('show');
    // Dismiss for 10 minutes
    updateDismissedUntil = Date.now() + (10 * 60 * 1000);
    console.log('[Update] Update banner dismissed for 10 minutes');
  }

  function applyPanelUpdate() {
    // Trigger plugin update via WebSocket
    const ws = window.MX?.ws;
    if (ws?.isConnected()) {
      toast('info', 'Updating Plugin', 'Downloading latest version from GitHub...');
      ws.send('TRIGGER_PLUGIN_UPDATE', {});

      // Disable the button to prevent double-clicks
      const banner = document.getElementById('updateBanner');
      if (banner) {
        const btn = banner.querySelector('.btn.primary');
        if (btn) {
          btn.disabled = true;
          btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Downloading...';
        }
      }
    } else {
      toast('error', 'Not Connected', 'Cannot update - not connected to server');
    }
  }

  window.dismissUpdateBanner = dismissUpdateBanner;
  window.applyPanelUpdate = applyPanelUpdate;
  window.loadPanelVersion = loadPanelVersion;
  window.loadCurrentPluginVersion = loadCurrentPluginVersion;
  window.checkGitHubForUpdates = checkGitHubForUpdates;

  // ===== PROFILE DROPDOWN =====
  function toggleProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    const profile = document.getElementById('topProfile');
    if (!dropdown || !profile) return;

    const isOpen = dropdown.classList.contains('show');
    if (isOpen) {
      closeProfileDropdown();
    } else {
      dropdown.classList.add('show');
      profile.classList.add('open');
      // Close when clicking outside
      setTimeout(() => {
        document.addEventListener('click', handleProfileClickOutside);
      }, 0);
    }
  }

  function closeProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    const profile = document.getElementById('topProfile');
    if (dropdown) dropdown.classList.remove('show');
    if (profile) profile.classList.remove('open');
    document.removeEventListener('click', handleProfileClickOutside);
  }

  function handleProfileClickOutside(e) {
    const profile = document.getElementById('topProfile');
    if (profile && !profile.contains(e.target)) {
      closeProfileDropdown();
    }
  }

  function logout() {
    closeProfileDropdown();
    state.authenticated = false;
    state.currentUser = null;
    state.staffName = '';
    state.notifications = [];

    // Disconnect WebSocket
    const ws = window.MX?.ws;
    if (ws) ws.disconnect();

    // Clear saved auth
    try {
      localStorage.removeItem('mx_auth');
      localStorage.removeItem('mx_token');
    } catch (e) {}

    // Reload to show auth screen
    window.location.reload();
  }

  // ===== NOTIFICATIONS =====
  function updateNotificationCount() {
    const countEl = document.getElementById('notificationCount');
    if (!countEl) return;

    const count = (state.notifications || []).filter(n => !n.read).length;
    if (count > 0) {
      countEl.textContent = count > 99 ? '99+' : count;
      countEl.style.display = 'flex';
    } else {
      countEl.style.display = 'none';
    }
  }

  function openNotificationsPanel() {
    closeProfileDropdown();
    // For now, just show a toast - notifications panel can be added later
    toast('info', 'Notifications', `You have ${(state.notifications || []).filter(n => !n.read).length} unread notifications`);
  }

  function addNotification(notification) {
    if (!state.notifications) state.notifications = [];
    state.notifications.unshift({
      id: uid('notif'),
      ...notification,
      read: false,
      timestamp: now()
    });
    // Keep only last 50 notifications
    if (state.notifications.length > 50) {
      state.notifications = state.notifications.slice(0, 50);
    }
    updateNotificationCount();
  }

  window.toggleProfileDropdown = toggleProfileDropdown;
  window.closeProfileDropdown = closeProfileDropdown;
  window.logout = logout;
  window.openNotificationsPanel = openNotificationsPanel;
  window.addNotification = addNotification;
  window.updateNotificationCount = updateNotificationCount;

  // ===== CHANGELOG SYSTEM =====
  let changelogState = {
    unreadChangelogs: [],
    currentIndex: 0,
    readBuilds: []
  };

  /**
   * Show the changelog modal if there are unread changelogs
   */
  window.showChangelogModal = function() {
    const changelogs = window.MX_CHANGELOGS || [];
    const readBuilds = changelogState.readBuilds || [];
    const unread = changelogs.filter(log => !readBuilds.includes(log.build));

    if (unread.length === 0) {
      if (window.MX?.debug) console.log('[Changelog] No unread changelogs');
      return;
    }

    // Sort by build number ascending (earliest/oldest versions first)
    unread.sort((a, b) => a.build - b.build);

    changelogState.unreadChangelogs = unread;
    changelogState.currentIndex = 0;
    renderChangelogModal();
  };

  /**
   * Render the changelog modal for the current changelog
   */
  function renderChangelogModal() {
    const { unreadChangelogs, currentIndex } = changelogState;
    if (!unreadChangelogs.length) return;

    const log = unreadChangelogs[currentIndex];
    const isMultiple = unreadChangelogs.length > 1;
    const isLast = currentIndex === unreadChangelogs.length - 1;
    const isFirst = currentIndex === 0;

    // Remove existing modal
    const existing = document.getElementById('changelogOverlay');
    if (existing) existing.remove();

    // Section icon map
    const sectionIcons = {
      new: 'fa-sparkles',
      improved: 'fa-arrow-up',
      fixed: 'fa-wrench',
      technical: 'fa-code',
      permissions: 'fa-key',
      config: 'fa-sliders'
    };

    // Build sections HTML
    const sectionsHtml = log.sections.map(section => `
      <div class="changelog-section">
        <div class="changelog-section-header">
          <div class="changelog-section-icon ${section.type}">
            <i class="fa-solid ${sectionIcons[section.type] || 'fa-circle'}"></i>
          </div>
          <h3>${escapeHtml(section.title)}</h3>
        </div>
        <ul class="changelog-items">
          ${section.items.map(item => `<li>${parseMarkdownBold(escapeHtml(item))}</li>`).join('')}
        </ul>
      </div>
    `).join('');

    // Pagination dots
    const dotsHtml = isMultiple ? `
      <div class="changelog-pagination">
        <span>${currentIndex + 1} of ${unreadChangelogs.length}</span>
        <div class="changelog-pagination-dots">
          ${unreadChangelogs.map((_, i) => `
            <div class="changelog-pagination-dot ${i === currentIndex ? 'active' : ''}"
                 onclick="window.goToChangelog(${i})"></div>
          `).join('')}
        </div>
      </div>
    ` : '<div></div>';

    // Navigation buttons
    const navHtml = isMultiple ? `
      <button class="changelog-nav-btn" onclick="window.prevChangelog()" ${isFirst ? 'disabled' : ''}>
        <i class="fa-solid fa-chevron-left"></i>
      </button>
      <button class="changelog-nav-btn" onclick="window.nextChangelog()" ${isLast ? 'disabled' : ''}>
        <i class="fa-solid fa-chevron-right"></i>
      </button>
    ` : '';

    const overlay = document.createElement('div');
    overlay.id = 'changelogOverlay';
    overlay.className = 'changelog-overlay';
    overlay.innerHTML = `
      <div class="changelog-modal">
        <div class="changelog-header">
          <div class="changelog-header-icon">
            <img src="537154108207028818e303ef9465c1f66717660d_96.png" alt="ModereX" class="changelog-logo">
          </div>
          <div class="changelog-header-text">
            <h2>${escapeHtml(log.title)}</h2>
            <div class="changelog-meta">
              <span class="changelog-version">v${escapeHtml(log.version)}</span>
              <span class="changelog-date">${escapeHtml(log.date)}</span>
            </div>
          </div>
        </div>
        <div class="changelog-body">
          ${sectionsHtml}
        </div>
        <div class="changelog-footer">
          ${dotsHtml}
          <div class="changelog-actions">
            ${navHtml}
            <button class="changelog-btn changelog-btn-primary" onclick="window.markChangelogRead()">
              ${isLast ? 'Got it!' : 'Mark as Read'}
            </button>
          </div>
        </div>
      </div>
    `;

    document.body.appendChild(overlay);
  }

  /**
   * Parse markdown bold (**text**) to HTML
   */
  function parseMarkdownBold(text) {
    return text.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
               .replace(/`([^`]+)`/g, '<code>$1</code>');
  }

  /**
   * Go to specific changelog by index
   */
  window.goToChangelog = function(index) {
    changelogState.currentIndex = index;
    renderChangelogModal();
  };

  /**
   * Go to previous changelog
   */
  window.prevChangelog = function() {
    if (changelogState.currentIndex > 0) {
      changelogState.currentIndex--;
      renderChangelogModal();
    }
  };

  /**
   * Go to next changelog
   */
  window.nextChangelog = function() {
    if (changelogState.currentIndex < changelogState.unreadChangelogs.length - 1) {
      changelogState.currentIndex++;
      renderChangelogModal();
    }
  };

  /**
   * Mark current changelog as read and move to next or close
   */
  window.markChangelogRead = function() {
    const { unreadChangelogs, currentIndex } = changelogState;
    const log = unreadChangelogs[currentIndex];

    // Send to server to mark as read
    const ws = window.MX?.ws;
    if (ws && ws.isConnected()) {
      ws.send('MARK_CHANGELOG_READ', { build: log.build });
      window.debugLog('CHANGELOG', `Marked build ${log.build} as read`, 'info');
    }

    // Add to local read list
    if (!changelogState.readBuilds.includes(log.build)) {
      changelogState.readBuilds.push(log.build);
    }

    // If last changelog, close modal
    if (currentIndex >= unreadChangelogs.length - 1) {
      closeChangelogModal();
    } else {
      // Move to next
      changelogState.currentIndex++;
      renderChangelogModal();
    }
  };

  /**
   * Close the changelog modal
   */
  window.closeChangelogModal = function() {
    const overlay = document.getElementById('changelogOverlay');
    if (overlay) {
      overlay.style.animation = 'changelogFadeIn 0.2s ease reverse';
      setTimeout(() => overlay.remove(), 200);
    }
  };
  const closeChangelogModal = window.closeChangelogModal;

  /**
   * Set read builds from server
   */
  window.setReadChangelogs = function(builds) {
    changelogState.readBuilds = builds || [];
    if (window.MX?.debug) {
      console.log('[Changelog] Read builds:', builds);
    }
  };

  /**
   * Check and show changelog after login
   */
  window.checkUnreadChangelogs = function() {
    setTimeout(() => {
      if (changelogState.readBuilds !== undefined) {
        window.showChangelogModal();
      }
    }, 1500); // Delay to let UI settle
  };

  // ===== INITIALIZATION =====
  document.addEventListener('DOMContentLoaded', () => {
    ui.initDom();
    if (!loadState()) initializeState();
    setupEventListeners();
    setupWebSocketHandlers();
    setupStatusIndicator();
    wrapWithWebSocket();
    setupBackgroundAnimation();
    startClock();
    startDurationCountdown();
    ui.refreshUnsavedUI();
    go('dashboard');
    applyMySettingsUI();
    applyThemeFromState();
    applyDevModeUI();
    setInterval(saveState, 4000);
    window.addEventListener('beforeunload', saveState);

    // Load panel version from server
    setTimeout(loadPanelVersion, 1000);

    // Note: Update checker is now started after authentication in mx:authenticated event handler

    // Setup profile dropdown click handler
    const topProfile = document.getElementById('topProfile');
    if (topProfile) {
      topProfile.addEventListener('click', (e) => {
        // Don't toggle if clicking on dropdown item
        if (e.target.closest('.profile-dropdown-item')) return;
        toggleProfileDropdown();
      });
    }
  });

  // Setup status indicator handlers
  function setupStatusIndicator() {
    const ws = window.MX?.ws;
    if (!ws) return;

    ws.on('ping_update', (data) => {
      updateStatusIndicator('connected', data.ping);
    });

    ws.on('status_change', (data) => {
      updateStatusIndicator(data.status, data.ping);
    });
  }

  // ==================== PERMISSIONS MODULE ====================
  (function initPermissions() {
    const ws = window.MX?.ws;
    if (!ws) return;

    let ranksData = [];
    let selectedRankId = null;
    let luckPermsAvailable = false;

    // Permission categories for the editor
    const PERM_CATEGORIES = [
      { name: 'General', permissions: ['moderex.webpanel', 'moderex.staff', 'moderex.admin', 'moderex.info.basic', 'moderex.info.ip', 'moderex.info.nick', 'moderex.info.alts'] },
      { name: 'Moderation', permissions: ['moderex.command.ban', 'moderex.command.tempban', 'moderex.command.mute', 'moderex.command.tempmute', 'moderex.command.warn', 'moderex.command.kick', 'moderex.command.unban', 'moderex.command.unmute', 'moderex.command.unwarn'] },
      { name: 'History', permissions: ['moderex.history.view', 'moderex.history.chat', 'moderex.history.commands', 'moderex.history.nick', 'moderex.history.sessions', 'moderex.history.items', 'moderex.history.signs'] },
      { name: 'Automod', permissions: ['moderex.automod.*', 'moderex.automod.manage', 'moderex.automod.toggle', 'moderex.automod.bypass'] },
      { name: 'Anticheat', permissions: ['moderex.anticheat.*', 'moderex.anticheat.alerts', 'moderex.anticheat.manage'] },
      { name: 'Staff Tools', permissions: ['moderex.staffchat', 'moderex.vanish', 'moderex.disguise', 'moderex.staffmode', 'moderex.watchlist', 'moderex.replays.view', 'moderex.replays.configure'] },
      { name: 'Admin', permissions: ['moderex.*', 'moderex.command.*', 'moderex.admin.*', 'moderex.admin.permissions', 'moderex.admin.reload', 'moderex.admin.gateway'] },
      { name: 'Monitoring', permissions: ['moderex.monitoring.*', 'moderex.monitoring.tps', 'moderex.monitoring.memory', 'moderex.monitoring.entities'] },
      { name: 'Bypass', permissions: ['moderex.bypass.automod', 'moderex.bypass.mute', 'moderex.bypass.lockdown', 'moderex.bypass.slowmode', 'moderex.bypass.chatdisable'] },
      { name: 'Alerts', permissions: ['moderex.alerts.*', 'moderex.alerts.punishments', 'moderex.alerts.automod', 'moderex.alerts.anticheat', 'moderex.alerts.staffchat'] }
    ];

    ws.on('RANKS', (data) => {
      ranksData = data.ranks || [];
      luckPermsAvailable = data.luckPermsAvailable || false;
      renderRankList();
      // Show/hide export button
      const btn = document.getElementById('btnExportLP');
      if (btn) btn.style.display = luckPermsAvailable ? '' : 'none';
    });

    ws.on('RANK_CREATED', (data) => {
      ranksData.push(data.rank);
      renderRankList();
      selectRank(data.rank.id);
      window.MX?.toast?.('ok', 'Rank Created', data.rank.displayName);
    });

    ws.on('RANK_UPDATED', (data) => {
      const idx = ranksData.findIndex(r => r.id === data.rank.id);
      if (idx >= 0) ranksData[idx] = data.rank;
      renderRankList();
      if (selectedRankId === data.rank.id) renderRankEditor(data.rank);
      window.MX?.toast?.('ok', 'Rank Updated', data.rank.displayName);
    });

    ws.on('RANK_DELETED', (data) => {
      ranksData = ranksData.filter(r => r.id !== data.id);
      renderRankList();
      if (selectedRankId === data.id) {
        selectedRankId = null;
        document.getElementById('rankEditor').style.display = 'none';
        document.getElementById('rankEditorEmpty').style.display = '';
      }
      window.MX?.toast?.('ok', 'Rank Deleted', '');
    });

    ws.on('RANKS_REORDERED', (data) => {
      ranksData = data.ranks || [];
      renderRankList();
    });

    ws.on('RANK_PERMISSION_SET', (data) => {
      const idx = ranksData.findIndex(r => r.id === data.rank.id);
      if (idx >= 0) ranksData[idx] = data.rank;
      if (selectedRankId === data.rank.id) renderPermissions(data.rank);
    });

    ws.on('RANK_PERMISSION_REMOVED', (data) => {
      const idx = ranksData.findIndex(r => r.id === data.rank.id);
      if (idx >= 0) ranksData[idx] = data.rank;
      if (selectedRankId === data.rank.id) renderPermissions(data.rank);
    });

    ws.on('RANK_INHERITANCE_SET', (data) => {
      const idx = ranksData.findIndex(r => r.id === data.rank.id);
      if (idx >= 0) ranksData[idx] = data.rank;
      if (selectedRankId === data.rank.id) renderInheritance(data.rank);
    });

    ws.on('PLAYER_SEARCH_RESULTS', (data) => {
      renderPlayerSearchResults(data.players || []);
    });

    ws.on('PLAYER_RANKS', (data) => {
      renderPlayerRanks(data.uuid, data.ranks || []);
    });

    ws.on('PLAYER_RANK_SET', (data) => {
      renderPlayerRanks(data.uuid, data.ranks || []);
      window.MX?.toast?.('ok', 'Rank Assigned', '');
    });

    ws.on('PLAYER_RANK_REMOVED', (data) => {
      renderPlayerRanks(data.uuid, data.ranks || []);
      window.MX?.toast?.('ok', 'Rank Removed', '');
    });

    ws.on('LUCKPERMS_EXPORT_RESULT', (data) => {
      if (data.success) {
        window.MX?.toast?.('ok', 'Export Complete', `${data.ranksExported} ranks, ${data.playersExported} players exported`);
      } else {
        window.MX?.toast?.('bad', 'Export Failed', data.error || 'Unknown error');
      }
    });

    // Button handlers
    document.getElementById('btnCreateRank')?.addEventListener('click', () => {
      const name = prompt('Enter rank name (lowercase, no spaces):');
      if (!name) return;
      ws.send('CREATE_RANK', { name: name.toLowerCase().replace(/\s+/g, '_'), displayName: name, weight: (ranksData.length + 1) * 10 });
    });

    document.getElementById('btnExportLP')?.addEventListener('click', () => {
      if (confirm('Export all ranks and player assignments to LuckPerms? This will create/update LuckPerms groups.')) {
        ws.send('EXPORT_TO_LUCKPERMS', {});
      }
    });

    document.getElementById('btnSaveRank')?.addEventListener('click', () => {
      if (!selectedRankId) return;
      ws.send('UPDATE_RANK', {
        id: selectedRankId,
        displayName: document.getElementById('rankDisplayName')?.value || '',
        prefix: document.getElementById('rankPrefix')?.value || '',
        suffix: document.getElementById('rankSuffix')?.value || '',
        weight: parseInt(document.getElementById('rankWeight')?.value) || 0,
        isDefault: document.getElementById('rankIsDefault')?.checked || false
      });
    });

    document.getElementById('btnDeleteRank')?.addEventListener('click', () => {
      if (!selectedRankId) return;
      const rank = ranksData.find(r => r.id === selectedRankId);
      if (confirm(`Delete rank "${rank?.displayName}"? Players will be reassigned to the default rank.`)) {
        ws.send('DELETE_RANK', { id: selectedRankId });
      }
    });

    // Prefix live preview
    document.getElementById('rankPrefix')?.addEventListener('input', (e) => {
      const preview = document.getElementById('prefixPreview');
      if (preview) preview.innerHTML = parsePrefixPreview(e.target.value);
    });

    // Player search
    let searchTimeout;
    document.getElementById('playerSearchInput')?.addEventListener('input', (e) => {
      clearTimeout(searchTimeout);
      const query = e.target.value.trim();
      if (query.length < 2) {
        document.getElementById('playerSearchResults').style.display = 'none';
        return;
      }
      searchTimeout = setTimeout(() => ws.send('SEARCH_PLAYERS_FOR_RANK', { query }), 300);
    });

    document.getElementById('btnAssignRank')?.addEventListener('click', () => {
      const uuid = document.getElementById('playerRankAssignment')?.dataset.uuid;
      const playerName = document.getElementById('playerRankAssignment')?.dataset.playerName;
      const rankId = parseInt(document.getElementById('assignRankSelect')?.value);
      if (uuid && rankId) {
        ws.send('SET_PLAYER_RANK', { uuid, playerName, rankId, primary: false });
      }
    });

    function renderRankList() {
      const container = document.getElementById('rankListContainer');
      if (!container) return;
      if (ranksData.length === 0) {
        container.innerHTML = '<div class="empty-state">No ranks created yet</div>';
        return;
      }
      container.innerHTML = ranksData.map(rank => `
        <div class="rank-list-item ${rank.id === selectedRankId ? 'active' : ''}" data-rank-id="${rank.id}">
          <div class="rank-color-dot" style="background:${extractColor(rank.prefix)}"></div>
          <div class="rank-item-name">${escHtml(rank.displayName)}</div>
          ${rank.isDefault ? '<span class="rank-item-default">Default</span>' : ''}
          <span class="rank-item-weight">#${rank.weight}</span>
        </div>
      `).join('');

      container.querySelectorAll('.rank-list-item').forEach(item => {
        item.addEventListener('click', () => selectRank(parseInt(item.dataset.rankId)));
      });
    }

    function selectRank(id) {
      selectedRankId = id;
      const rank = ranksData.find(r => r.id === id);
      if (!rank) return;
      document.getElementById('rankEditorEmpty').style.display = 'none';
      document.getElementById('rankEditor').style.display = '';
      renderRankEditor(rank);
      renderRankList(); // Update active state
    }

    function renderRankEditor(rank) {
      document.getElementById('rankEditorTitle').innerHTML = `<i class="fa-solid fa-pen"></i> ${escHtml(rank.displayName)}`;
      document.getElementById('rankDisplayName').value = rank.displayName;
      document.getElementById('rankPrefix').value = rank.prefix || '';
      document.getElementById('rankSuffix').value = rank.suffix || '';
      document.getElementById('rankWeight').value = rank.weight;
      document.getElementById('rankIsDefault').checked = rank.isDefault;
      document.getElementById('prefixPreview').innerHTML = parsePrefixPreview(rank.prefix || '');
      renderInheritance(rank);
      renderPermissions(rank);
    }

    function renderInheritance(rank) {
      const container = document.getElementById('rankInheritance');
      if (!container) return;
      container.innerHTML = ranksData.filter(r => r.id !== rank.id).map(r => {
        const active = rank.parentRankIds?.includes(r.id);
        return `<div class="rank-inheritance-chip ${active ? 'active' : ''}" data-parent-id="${r.id}">${escHtml(r.displayName)}</div>`;
      }).join('');

      container.querySelectorAll('.rank-inheritance-chip').forEach(chip => {
        chip.addEventListener('click', () => {
          const parentId = parseInt(chip.dataset.parentId);
          let parents = rank.parentRankIds ? [...rank.parentRankIds] : [];
          if (parents.includes(parentId)) {
            parents = parents.filter(id => id !== parentId);
          } else {
            parents.push(parentId);
          }
          ws.send('SET_RANK_INHERITANCE', { rankId: rank.id, parentIds: parents });
        });
      });
    }

    function renderPermissions(rank) {
      const container = document.getElementById('rankPermissions');
      if (!container) return;
      const perms = rank.permissions || {};

      container.innerHTML = PERM_CATEGORIES.map(cat => `
        <div class="perm-category">
          <div class="perm-category-header" onclick="this.parentElement.classList.toggle('collapsed')">
            ${cat.name}
            <i class="fa-solid fa-chevron-down"></i>
          </div>
          <div class="perm-category-items">
            ${cat.permissions.map(p => {
              const state = perms[p] === true ? 'allow' : perms[p] === false ? 'deny' : 'inherit';
              return `
                <div class="perm-item">
                  <span class="perm-item-name">${p}</span>
                  <div class="perm-toggle-group">
                    <button class="perm-toggle-btn ${state === 'allow' ? 'active-allow' : ''}" data-perm="${p}" data-action="allow">Allow</button>
                    <button class="perm-toggle-btn ${state === 'inherit' ? 'active-inherit' : ''}" data-perm="${p}" data-action="inherit">Inherit</button>
                    <button class="perm-toggle-btn ${state === 'deny' ? 'active-deny' : ''}" data-perm="${p}" data-action="deny">Deny</button>
                  </div>
                </div>`;
            }).join('')}
          </div>
        </div>
      `).join('');

      container.querySelectorAll('.perm-toggle-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          const perm = btn.dataset.perm;
          const action = btn.dataset.action;
          if (action === 'inherit') {
            ws.send('REMOVE_RANK_PERMISSION', { rankId: rank.id, permission: perm });
          } else {
            ws.send('SET_RANK_PERMISSION', { rankId: rank.id, permission: perm, granted: action === 'allow' });
          }
        });
      });
    }

    function renderPlayerSearchResults(players) {
      const container = document.getElementById('playerSearchResults');
      if (!container) return;
      if (players.length === 0) {
        container.innerHTML = '<div style="padding:8px;color:var(--text-muted);font-size:0.85rem">No players found</div>';
        container.style.display = '';
        return;
      }
      container.style.display = '';
      container.innerHTML = players.map(p => `
        <div class="player-search-item" data-uuid="${p.uuid}" data-name="${escHtml(p.name)}">
          <img src="https://mc-heads.net/avatar/${p.uuid}/24" alt="">
          <span>${escHtml(p.name)}</span>
        </div>
      `).join('');

      container.querySelectorAll('.player-search-item').forEach(item => {
        item.addEventListener('click', () => {
          const uuid = item.dataset.uuid;
          const name = item.dataset.name;
          container.style.display = 'none';
          document.getElementById('playerSearchInput').value = name;
          const assignment = document.getElementById('playerRankAssignment');
          assignment.style.display = '';
          assignment.dataset.uuid = uuid;
          assignment.dataset.playerName = name;
          document.getElementById('playerRankHeader').innerHTML = `<img src="https://mc-heads.net/avatar/${uuid}/24" style="border-radius:4px"> ${escHtml(name)}`;
          // Populate rank select
          const sel = document.getElementById('assignRankSelect');
          sel.innerHTML = ranksData.map(r => `<option value="${r.id}">${escHtml(r.displayName)}</option>`).join('');
          // Load current ranks
          ws.send('GET_PLAYER_RANKS', { uuid });
        });
      });
    }

    function renderPlayerRanks(uuid, ranks) {
      const container = document.getElementById('playerCurrentRanks');
      if (!container) return;
      if (ranks.length === 0) {
        container.innerHTML = '<div style="color:var(--text-muted);font-size:0.85rem;padding:4px 0">No ranks assigned</div>';
        return;
      }
      container.innerHTML = ranks.map(r => `
        <span class="player-rank-chip">
          ${escHtml(r.displayName)} ${r.isPrimary ? '<i class="fa-solid fa-star" style="color:var(--warning);font-size:0.7rem"></i>' : ''}
          <i class="fa-solid fa-xmark remove-rank" data-uuid="${uuid}" data-rank-id="${r.rankId}"></i>
        </span>
      `).join('');

      container.querySelectorAll('.remove-rank').forEach(btn => {
        btn.addEventListener('click', () => {
          ws.send('REMOVE_PLAYER_RANK', { uuid: btn.dataset.uuid, rankId: parseInt(btn.dataset.rankId) });
        });
      });
    }

    function extractColor(prefix) {
      if (!prefix) return '#8b5cf6';
      // Extract hex color from prefix like <#ff5555> or &#ff5555
      const hexMatch = prefix.match(/#([0-9a-fA-F]{6})/);
      if (hexMatch) return '#' + hexMatch[1];
      // Legacy color code mapping
      const legacyColors = { '0':'#000','1':'#0000AA','2':'#00AA00','3':'#00AAAA','4':'#AA0000','5':'#AA00AA','6':'#FFAA00','7':'#AAAAAA','8':'#555555','9':'#5555FF','a':'#55FF55','b':'#55FFFF','c':'#FF5555','d':'#FF55FF','e':'#FFFF55','f':'#FFFFFF' };
      const legacyMatch = prefix.match(/&([0-9a-fA-F])/);
      if (legacyMatch) return legacyColors[legacyMatch[1].toLowerCase()] || '#8b5cf6';
      return '#8b5cf6';
    }

    function parsePrefixPreview(prefix) {
      if (!prefix) return '<span style="color:var(--text-muted)">No prefix</span>';
      // Parse hex colors like <#ff5555>
      let html = escHtml(prefix);
      html = html.replace(/&lt;#([0-9a-fA-F]{6})&gt;/g, '<span style="color:#$1">');
      html = html.replace(/&lt;\/[^&]*&gt;/g, '</span>');
      // Parse legacy & codes
      const legacyColors = { '0':'#000','1':'#0000AA','2':'#00AA00','3':'#00AAAA','4':'#AA0000','5':'#AA00AA','6':'#FFAA00','7':'#AAAAAA','8':'#555555','9':'#5555FF','a':'#55FF55','b':'#55FFFF','c':'#FF5555','d':'#FF55FF','e':'#FFFF55','f':'#FFFFFF' };
      html = html.replace(/&amp;([0-9a-fA-F])/g, (_, code) => {
        const color = legacyColors[code.toLowerCase()] || '#fff';
        return `<span style="color:${color}">`;
      });
      return html;
    }

    function escHtml(str) {
      if (!str) return '';
      return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }
  })();

  // ==================== LICENSE ACCEPTANCE MODULE ====================
  (function initLicense() {
    const LICENSE_TEXT = `
<h2>ModereX Software License Agreement</h2>

<p>This license agreement ("Agreement") governs the use of ModereX software ("Software"). By using this Software, you agree to the terms below.</p>

<h2>1. Grant of License</h2>
<p>You are granted a non-exclusive, non-transferable license to use the Software for managing Minecraft servers. You may install and use the Software on servers you own or operate.</p>

<h2>2. Restrictions</h2>
<p>You may not: (a) reverse engineer, decompile, or disassemble the Software; (b) redistribute, sell, lease, or sublicense the Software; (c) remove or alter any proprietary notices or labels; (d) use the Software for any unlawful purpose.</p>

<h2>3. Intellectual Property</h2>
<p>The Software and all associated intellectual property rights are owned by BlockForge Studio. This Agreement does not transfer ownership of any intellectual property.</p>

<h2>4. Data Collection</h2>
<p>The Software collects server metrics (player count, version, server ID) for gateway connectivity and functionality purposes. No personal player data is transmitted to external services beyond what is required for core functionality.</p>

<h2>5. Disclaimer of Warranties</h2>
<p>THE SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. BLOCKFORGE STUDIO DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.</p>

<h2>6. Limitation of Liability</h2>
<p>IN NO EVENT SHALL BLOCKFORGE STUDIO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING FROM THE USE OF THE SOFTWARE.</p>

<h2>7. Termination</h2>
<p>This license is effective until terminated. It will terminate automatically if you fail to comply with any term. Upon termination, you must destroy all copies of the Software.</p>

<h2>8. Modifications</h2>
<p>BlockForge Studio reserves the right to modify this Agreement at any time. Continued use of the Software after modifications constitutes acceptance of the updated terms.</p>

<p class="license-date">Last Updated: February 2026 &mdash; License Version 1</p>
    `;

    function showLicenseModal() {
      let overlay = document.getElementById('licenseOverlay');
      if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'licenseOverlay';
        overlay.className = 'license-overlay';
        overlay.innerHTML = `
          <div class="license-modal">
            <div class="license-modal-header">
              <i class="fa-solid fa-scale-balanced"></i>
              <span>Software License Agreement</span>
            </div>
            <div class="license-modal-body">
              ${LICENSE_TEXT}
            </div>
            <div class="license-modal-footer">
              <button class="license-btn license-btn-decline" id="licenseDeclineBtn">I DO NOT ACCEPT</button>
              <button class="license-btn license-btn-accept" id="licenseAcceptBtn">I ACCEPT</button>
            </div>
          </div>
        `;
        document.body.appendChild(overlay);

        document.getElementById('licenseAcceptBtn').addEventListener('click', () => {
          const ws = window.MX?.ws;
          if (ws) ws.send('ACCEPT_LICENSE', {});
          overlay.classList.remove('show');
          setTimeout(() => overlay.remove(), 300);
        });

        document.getElementById('licenseDeclineBtn').addEventListener('click', () => {
          // Show inaccessible state
          overlay.querySelector('.license-modal-body').innerHTML = `
            <div style="text-align:center;padding:40px 0">
              <i class="fa-solid fa-ban" style="font-size:3rem;color:#da3633;margin-bottom:16px"></i>
              <h2 style="border:none;margin:0 0 8px 0">Access Denied</h2>
              <p>You must accept the license agreement to use the ModereX web panel.</p>
              <p style="margin-top:16px"><button class="license-btn license-btn-accept" onclick="location.reload()">TRY AGAIN</button></p>
            </div>
          `;
          overlay.querySelector('.license-modal-footer').style.display = 'none';
          // Disconnect
          const ws = window.MX?.ws;
          if (ws) ws.disconnect();
        });
      }
      overlay.classList.add('show');
    }

    const ws = window.MX?.ws;
    if (!ws) return;

    ws.on('LICENSE_STATUS', (data) => {
      if (!data.accepted) {
        showLicenseModal();
      }
    });

    ws.on('LICENSE_ACCEPTED', () => {
      // License accepted, nothing more to do
    });

    // Check license on authentication
    window.addEventListener('mx:authenticated', () => {
      setTimeout(() => {
        const ws = window.MX?.ws;
        if (ws && ws.isConnected()) {
          ws.send('CHECK_LICENSE_ACCEPTED', {});
        }
      }, 500);
    });
  })();
})();

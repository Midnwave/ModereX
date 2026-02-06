/**
 * ModereX Admin Panel
 * Internal administration dashboard for BlockForge Studios
 */

(function() {
    'use strict';

    // Gateway WebSocket URL
    const GATEWAY_WS_URL = 'wss://phillips-cache-northeast-photographer.trycloudflare.com/admin';

    // State
    let ws = null;
    let adminEmail = null;
    let currentPage = 'dashboard';
    let reconnectAttempts = 0;
    const MAX_RECONNECT_ATTEMPTS = 10;

    // DOM Ready
    document.addEventListener('DOMContentLoaded', () => {
        initializeAdmin();
    });

    function initializeAdmin() {
        // Check Cloudflare Access JWT
        checkAuth();

        // Initialize navigation
        initNavigation();

        // Initialize tabs
        initTabs();

        // Initialize mobile menu
        initMobileMenu();

        // Connect to gateway
        connectToGateway();

        // Load initial page from hash
        const hash = window.location.hash.replace('#', '');
        if (hash) {
            navigateToPage(hash);
        }
    }

    // ========================================
    // Authentication
    // ========================================

    function checkAuth() {
        // In production, Cloudflare Access handles auth
        // The gateway will verify the CF-Access-JWT-Assertion header

        // For now, try to get user info from Cloudflare Access cookie
        // This will be set by Cloudflare Access after successful auth

        // Simulate admin user for development
        const email = getCFAccessEmail() || 'developer@blockforge.studio';
        adminEmail = email;

        document.getElementById('adminName').textContent = email.split('@')[0];
    }

    function getCFAccessEmail() {
        // In production, this would parse the CF-Access-JWT-Assertion header
        // or be provided by the gateway after verification
        return null;
    }

    // ========================================
    // WebSocket Connection
    // ========================================

    function connectToGateway() {
        updateGatewayStatus('connecting');

        try {
            ws = new WebSocket(GATEWAY_WS_URL);

            ws.onopen = () => {
                console.log('[Admin] Connected to gateway');
                updateGatewayStatus('connected');
                reconnectAttempts = 0;

                // Authenticate
                send('admin_auth', {
                    email: adminEmail
                });

                // Request initial data
                requestDashboardData();
            };

            ws.onclose = (event) => {
                console.log('[Admin] Disconnected from gateway:', event.code);
                updateGatewayStatus('disconnected');
                scheduleReconnect();
            };

            ws.onerror = (error) => {
                console.error('[Admin] WebSocket error:', error);
                updateGatewayStatus('error');
            };

            ws.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data);
                    handleMessage(message);
                } catch (e) {
                    console.error('[Admin] Failed to parse message:', e);
                }
            };
        } catch (e) {
            console.error('[Admin] Failed to connect:', e);
            updateGatewayStatus('error');
            scheduleReconnect();
        }
    }

    function scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            console.log('[Admin] Max reconnect attempts reached');
            return;
        }

        reconnectAttempts++;
        const delay = Math.min(1000 * Math.pow(2, reconnectAttempts), 30000);

        console.log(`[Admin] Reconnecting in ${delay}ms (attempt ${reconnectAttempts})`);
        setTimeout(connectToGateway, delay);
    }

    function send(type, data = {}) {
        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify({ type, data }));
        }
    }

    function updateGatewayStatus(status) {
        const el = document.getElementById('gatewayStatus');
        const textEl = el.querySelector('.status-text');

        el.className = 'gateway-status ' + status;

        switch (status) {
            case 'connecting':
                textEl.textContent = 'Connecting...';
                break;
            case 'connected':
                textEl.textContent = 'Connected';
                break;
            case 'disconnected':
                textEl.textContent = 'Disconnected';
                break;
            case 'error':
                textEl.textContent = 'Error';
                break;
        }
    }

    // ========================================
    // Message Handling
    // ========================================

    function handleMessage(message) {
        const type = message.type;
        // Gateway sends some responses with a 'data' wrapper, some without.
        // Fall back to the message itself so fields like servers, entries, etc. are accessible.
        const data = message.data || message;

        switch (type) {
            case 'connected':
                // Gateway sends this on initial WebSocket connection
                console.log('[Admin] Connected to gateway');
                break;

            case 'auth_success':
                console.log('[Admin] Authenticated successfully');
                break;

            case 'auth_error':
                console.error('[Admin] Authentication failed:', data.error);
                toast('error', 'Authentication Failed', data.error);
                break;

            case 'dashboard_data':
                updateDashboard(data);
                break;

            case 'servers_list':
                updateServersList(data.servers || []);
                break;

            case 'active_announcements':
            case 'announcements_list': {
                // Gateway sends flat array as 'announcements' — sort into active/scheduled/history
                const announcements = data.announcements || [];
                const now = Date.now();
                const sorted = {
                    active: announcements.filter(a => a.active && (!a.scheduledAt || a.scheduledAt <= now)),
                    scheduled: announcements.filter(a => a.scheduledAt && a.scheduledAt > now),
                    history: announcements.filter(a => !a.active)
                };
                updateAnnouncementsList(sorted);
                break;
            }

            case 'gateway_health':
                updateGatewayHealth(data);
                break;

            case 'analytics_data':
                updateAnalytics(data);
                break;

            case 'premium_data':
                updatePremium(data);
                break;

            case 'audit_log':
                updateAuditLog(data.entries || []);
                break;

            case 'announcement_created':
                toast('success', 'Announcement Created', 'Announcement has been sent to all connected panels');
                hideAnnouncementModal();
                requestAnnouncementsList();
                break;

            case 'announcement_error':
                toast('error', 'Error', data.error || data.message || 'Unknown error');
                break;

            case 'license_key_created':
                toast('success', 'License Key Generated', `Key: ${data.key}`);
                hideLicenseKeyModal();
                requestPremiumData();
                break;

            case 'error':
                console.error('[Admin] Gateway error:', data.message || data.error);
                toast('error', 'Error', data.message || data.error || 'Unknown error');
                break;

            default:
                console.log('[Admin] Unknown message type:', type);
        }
    }

    // ========================================
    // Data Requests
    // ========================================

    function requestDashboardData() {
        send('get_dashboard_data');
        send('get_servers_list');
        send('get_announcements_list');
    }

    function requestAnnouncementsList() {
        send('get_announcements_list');
    }

    function requestServersList() {
        send('get_servers_list');
    }

    function requestGatewayHealth() {
        send('get_gateway_health');
    }

    function requestAnalyticsData() {
        send('get_analytics_data');
    }

    function requestPremiumData() {
        send('get_premium_data');
    }

    function requestAuditLog() {
        send('get_audit_log');
    }

    // ========================================
    // UI Updates
    // ========================================

    function updateDashboard(data) {
        document.getElementById('totalServers').textContent = data.totalServers || data.servers || 0;
        document.getElementById('totalPlayers').textContent = data.totalPlayers || data.players || 0;
        document.getElementById('premiumServers').textContent = data.premiumServers || 0;
        document.getElementById('activeAnnouncements').textContent = data.activeAnnouncements || data.announcements || 0;

        // Update recent activity
        const activity = data.recentActivity || data.activity;
        if (activity && activity.length > 0) {
            const activityList = document.getElementById('recentActivity');
            activityList.innerHTML = activity.map(activity => `
                <div class="activity-item">
                    <div class="activity-icon"><i class="fas fa-${getActivityIcon(activity.type)}"></i></div>
                    <div class="activity-info">
                        <span class="activity-text">${escapeHtml(activity.text)}</span>
                        <span class="activity-time">${formatTime(activity.timestamp)}</span>
                    </div>
                </div>
            `).join('');
        }

        // Update version chart
        if (data.versionDistribution) {
            updateVersionChart(data.versionDistribution);
        }
    }

    function updateServersList(servers) {
        const tbody = document.getElementById('serversTableBody');

        if (!servers || servers.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="table-loading">
                        <i class="fas fa-server"></i> No servers connected
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = servers.map(server => `
            <tr>
                <td><code>${escapeHtml(server.id.substring(0, 12))}</code></td>
                <td>${escapeHtml(server.name)}</td>
                <td><span class="version-badge">${escapeHtml(server.version)}</span></td>
                <td>${server.players}</td>
                <td>
                    <span class="status-badge ${server.premium ? 'premium' : 'free'}">
                        ${server.premium ? 'Premium' : 'Free'}
                    </span>
                </td>
                <td>${formatTime(server.connectedAt)}</td>
            </tr>
        `).join('');
    }

    function updateAnnouncementsList(data) {
        // Active announcements
        updateAnnouncementsTab('activeAnnouncementsList', data.active, 'No Active Announcements');

        // Scheduled announcements
        updateAnnouncementsTab('scheduledAnnouncementsList', data.scheduled, 'No Scheduled Announcements');

        // History
        updateAnnouncementsTab('historyAnnouncementsList', data.history, 'No Announcement History');
    }

    function updateAnnouncementsTab(elementId, announcements, emptyMessage) {
        const container = document.getElementById(elementId);

        if (!announcements || announcements.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-bullhorn"></i>
                    <h4>${emptyMessage}</h4>
                </div>
            `;
            return;
        }

        container.innerHTML = announcements.map(ann => `
            <div class="announcement-card ${ann.level || ann.type || 'info'}">
                <div class="announcement-header">
                    <span class="announcement-type ${ann.level || ann.type || 'info'}">${(ann.level || ann.type || 'info').toUpperCase()}</span>
                    <span class="announcement-time">${formatTime(ann.createdAt)}</span>
                </div>
                <h4>${escapeHtml(ann.title || 'Announcement')}</h4>
                <p>${escapeHtml(ann.content || ann.message || '')}</p>
                <div class="announcement-footer">
                    <span class="announcement-stats">
                        <i class="fas fa-paper-plane"></i> Sent to ${ann.sentCount || 0} servers
                    </span>
                    <div class="announcement-actions">
                        ${ann.active ? `<button class="btn btn-sm btn-outline" onclick="deactivateAnnouncement('${ann.id}')">Deactivate</button>` : ''}
                        <button class="btn btn-sm btn-outline" onclick="deleteAnnouncement('${ann.id}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    function updateGatewayHealth(data) {
        document.getElementById('gatewayUptime').textContent = formatUptime(data.uptime);
        document.getElementById('gatewayConnections').textContent = data.connections || 0;
        document.getElementById('gatewayMsgRate').textContent = data.messagesPerSecond || 0;

        // Update health badge
        const badge = document.getElementById('gatewayHealthBadge');
        badge.className = 'health-badge ' + (data.healthy ? 'good' : 'warning');
        badge.textContent = data.healthy ? 'Healthy' : 'Degraded';

        // Update resource bars
        updateResourceBar('cpu', data.cpuUsage || 0);
        updateResourceBar('memory', data.memoryUsage || 0);

        // Update error list
        if (data.recentErrors && data.recentErrors.length > 0) {
            document.getElementById('errorList').innerHTML = data.recentErrors.map(err => `
                <div class="error-item">
                    <span class="error-time">${formatTime(err.timestamp)}</span>
                    <span class="error-message">${escapeHtml(err.message)}</span>
                </div>
            `).join('');
        }
    }

    function updateResourceBar(type, value) {
        const bar = document.getElementById(`${type}Bar`);
        const valueEl = document.getElementById(`${type}Value`);

        bar.style.width = `${value}%`;
        valueEl.textContent = `${Math.round(value)}%`;

        // Color based on value
        if (value > 80) {
            bar.style.background = 'var(--error)';
        } else if (value > 60) {
            bar.style.background = 'var(--warning)';
        } else {
            bar.style.background = 'var(--primary)';
        }
    }

    function updateAnalytics(data) {
        document.getElementById('totalPunishments').textContent = formatNumber(data.totalPunishments || 0);
        document.getElementById('automodBlocks').textContent = formatNumber(data.automodBlocksToday || 0);
    }

    function updatePremium(data) {
        document.getElementById('activePremium').textContent = data.activePremium || 0;
        document.getElementById('unusedKeys').textContent = data.unusedKeys || 0;

        // Update license keys table
        const tbody = document.getElementById('licenseTableBody');

        if (!data.licenseKeys || data.licenseKeys.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="table-loading">No license keys</td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = data.licenseKeys.map(key => `
            <tr>
                <td><code>${key.key}</code></td>
                <td>
                    <span class="status-badge ${key.redeemed ? 'redeemed' : 'available'}">
                        ${key.redeemed ? 'Redeemed' : 'Available'}
                    </span>
                </td>
                <td>${key.duration === -1 ? 'Lifetime' : `${key.duration} days`}</td>
                <td>${key.redeemedBy || '-'}</td>
                <td>${formatTime(key.createdAt)}</td>
                <td>
                    ${!key.redeemed ? `<button class="btn btn-sm btn-outline" onclick="revokeKey('${key.key}')">Revoke</button>` : ''}
                </td>
            </tr>
        `).join('');
    }

    function updateAuditLog(entries) {
        const container = document.getElementById('auditLog');

        if (!entries || entries.length === 0) {
            container.innerHTML = `
                <div class="audit-entry">
                    <div class="audit-time">--</div>
                    <div class="audit-info">
                        <span class="audit-action">No audit log entries</span>
                    </div>
                </div>
            `;
            return;
        }

        container.innerHTML = entries.map(entry => `
            <div class="audit-entry">
                <div class="audit-time">${formatTime(entry.timestamp)}</div>
                <div class="audit-info">
                    <span class="audit-admin">${escapeHtml(entry.adminEmail)}</span>
                    <span class="audit-action">${escapeHtml(entry.action)}</span>
                </div>
            </div>
        `).join('');
    }

    function updateVersionChart(distribution) {
        const container = document.getElementById('versionChart');

        const total = Object.values(distribution).reduce((a, b) => a + b, 0);
        if (total === 0) {
            container.innerHTML = `
                <div class="chart-placeholder">
                    <i class="fas fa-chart-pie"></i>
                    <span>No version data available</span>
                </div>
            `;
            return;
        }

        // Simple bar chart representation
        container.innerHTML = Object.entries(distribution)
            .sort((a, b) => b[1] - a[1])
            .map(([version, count]) => {
                const percentage = Math.round((count / total) * 100);
                return `
                    <div class="version-bar">
                        <span class="version-label">${escapeHtml(version)}</span>
                        <div class="version-bar-container">
                            <div class="version-bar-fill" style="width: ${percentage}%"></div>
                        </div>
                        <span class="version-count">${count} (${percentage}%)</span>
                    </div>
                `;
            }).join('');
    }

    // ========================================
    // Navigation
    // ========================================

    function initNavigation() {
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const page = item.dataset.page;
                navigateToPage(page);
            });
        });
    }

    function navigateToPage(page) {
        currentPage = page;

        // Update nav
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.toggle('active', item.dataset.page === page);
        });

        // Update page title
        const titles = {
            dashboard: 'Dashboard',
            announcements: 'Announcements',
            servers: 'Connected Servers',
            gateway: 'Gateway Health',
            analytics: 'Analytics',
            premium: 'Premium Management',
            features: 'Feature Flags',
            audit: 'Audit Log'
        };
        document.getElementById('pageTitle').textContent = titles[page] || 'Dashboard';

        // Show page
        document.querySelectorAll('.page').forEach(p => {
            p.classList.toggle('hidden', p.id !== `page-${page}`);
        });

        // Request page-specific data
        switch (page) {
            case 'servers':
                requestServersList();
                break;
            case 'gateway':
                requestGatewayHealth();
                break;
            case 'analytics':
                requestAnalyticsData();
                break;
            case 'premium':
                requestPremiumData();
                break;
            case 'audit':
                requestAuditLog();
                break;
        }

        // Update URL hash
        window.location.hash = page;

        // Close mobile menu
        document.querySelector('.sidebar').classList.remove('open');
    }

    // ========================================
    // Tabs
    // ========================================

    function initTabs() {
        document.querySelectorAll('.tab').forEach(tab => {
            tab.addEventListener('click', () => {
                const tabId = tab.dataset.tab;

                // Update tab buttons
                tab.parentElement.querySelectorAll('.tab').forEach(t => {
                    t.classList.toggle('active', t === tab);
                });

                // Update tab content
                document.querySelectorAll('.tab-content').forEach(content => {
                    content.classList.toggle('hidden', content.id !== `tab-${tabId}`);
                });
            });
        });
    }

    // ========================================
    // Mobile Menu
    // ========================================

    function initMobileMenu() {
        document.getElementById('mobileMenuBtn').addEventListener('click', () => {
            document.querySelector('.sidebar').classList.toggle('open');
        });

        // Close on outside click
        document.addEventListener('click', (e) => {
            const sidebar = document.querySelector('.sidebar');
            const menuBtn = document.getElementById('mobileMenuBtn');

            if (!sidebar.contains(e.target) && !menuBtn.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }

    // ========================================
    // Modals
    // ========================================

    window.showAnnouncementModal = function() {
        document.getElementById('announcementModal').classList.remove('hidden');
    };

    window.hideAnnouncementModal = function() {
        document.getElementById('announcementModal').classList.add('hidden');
        // Reset form
        document.getElementById('annTitle').value = '';
        document.getElementById('annMessage').value = '';
        document.getElementById('annType').value = 'info';
        document.getElementById('annPriority').value = '0';
        document.getElementById('annActionUrl').value = '';
        document.getElementById('annActionText').value = '';
        document.getElementById('annSchedule').value = '';
        document.getElementById('annExpires').value = '';
        document.getElementById('annDismissible').checked = true;
    };

    window.showEmergencyModal = function() {
        document.getElementById('emergencyModal').classList.remove('hidden');
    };

    window.hideEmergencyModal = function() {
        document.getElementById('emergencyModal').classList.add('hidden');
        document.getElementById('emergencyTitle').value = '';
        document.getElementById('emergencyMessage').value = '';
    };

    window.showLicenseKeyModal = function() {
        document.getElementById('licenseKeyModal').classList.remove('hidden');
    };

    window.hideLicenseKeyModal = function() {
        document.getElementById('licenseKeyModal').classList.add('hidden');
    };

    // ========================================
    // Actions
    // ========================================

    window.createAnnouncement = function() {
        const title = document.getElementById('annTitle').value.trim();
        const message = document.getElementById('annMessage').value.trim();
        const type = document.getElementById('annType').value;
        const priority = parseInt(document.getElementById('annPriority').value);
        const actionUrl = document.getElementById('annActionUrl').value.trim();
        const actionText = document.getElementById('annActionText').value.trim();
        const schedule = document.getElementById('annSchedule').value;
        const expires = document.getElementById('annExpires').value;
        const dismissible = document.getElementById('annDismissible').checked;

        if (!title || !message) {
            toast('error', 'Validation Error', 'Title and message are required');
            return;
        }

        send('create_announcement', {
            title,
            message,
            type,
            priority,
            actionUrl: actionUrl || null,
            actionText: actionText || null,
            scheduledAt: schedule ? new Date(schedule).getTime() : null,
            expiresAt: expires ? new Date(expires).getTime() : null,
            dismissible
        });
    };

    window.sendEmergencyBroadcast = function() {
        const title = document.getElementById('emergencyTitle').value.trim();
        const message = document.getElementById('emergencyMessage').value.trim();

        if (!title || !message) {
            toast('error', 'Validation Error', 'Title and message are required');
            return;
        }

        if (!confirm('Are you sure you want to send this emergency broadcast? This cannot be dismissed by users.')) {
            return;
        }

        send('create_announcement', {
            title,
            message,
            type: 'critical',
            priority: 3,
            dismissible: false
        });

        hideEmergencyModal();
    };

    window.deactivateAnnouncement = function(id) {
        if (confirm('Deactivate this announcement?')) {
            send('deactivate_announcement', { id });
        }
    };

    window.deleteAnnouncement = function(id) {
        if (confirm('Delete this announcement permanently?')) {
            send('delete_announcement', { id });
        }
    };

    window.generateLicenseKey = function() {
        const duration = parseInt(document.getElementById('keyDuration').value);
        const note = document.getElementById('keyNote').value.trim();

        send('generate_license_key', { duration, note });
    };

    window.revokeKey = function(key) {
        if (confirm('Revoke this license key?')) {
            send('revoke_license_key', { key });
        }
    };

    window.exportAuditLog = function() {
        send('export_audit_log');
        toast('info', 'Export Started', 'Audit log export will download shortly');
    };

    // ========================================
    // Utilities
    // ========================================

    function toast(type, title, message) {
        const container = document.getElementById('toastContainer');
        const icons = {
            success: 'fa-check-circle',
            error: 'fa-times-circle',
            warning: 'fa-exclamation-triangle',
            info: 'fa-info-circle'
        };

        const toastEl = document.createElement('div');
        toastEl.className = `toast ${type}`;
        toastEl.innerHTML = `
            <i class="fas ${icons[type]} toast-icon"></i>
            <div class="toast-content">
                <div class="toast-title">${escapeHtml(title)}</div>
                <div class="toast-message">${escapeHtml(message)}</div>
            </div>
        `;

        container.appendChild(toastEl);

        setTimeout(() => {
            toastEl.style.animation = 'toastOut 0.3s ease forwards';
            setTimeout(() => toastEl.remove(), 300);
        }, 5000);
    }

    function escapeHtml(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    function formatTime(timestamp) {
        if (!timestamp) return '--';
        const date = new Date(timestamp);
        const now = new Date();
        const diff = now - date;

        if (diff < 60000) return 'Just now';
        if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;

        return date.toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatUptime(ms) {
        if (!ms) return '--';

        const days = Math.floor(ms / 86400000);
        const hours = Math.floor((ms % 86400000) / 3600000);
        const minutes = Math.floor((ms % 3600000) / 60000);

        if (days > 0) return `${days}d ${hours}h`;
        if (hours > 0) return `${hours}h ${minutes}m`;
        return `${minutes}m`;
    }

    function formatNumber(num) {
        if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
        if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
        return num.toString();
    }

    function getActivityIcon(type) {
        const icons = {
            connect: 'plug',
            disconnect: 'plug-circle-xmark',
            announcement: 'bullhorn',
            premium: 'crown',
            error: 'exclamation-triangle'
        };
        return icons[type] || 'circle';
    }

    // Add CSS for toastOut animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes toastOut {
            to { opacity: 0; transform: translateX(100px); }
        }

        .version-bar {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 10px;
        }

        .version-label {
            font-size: 13px;
            color: var(--text-secondary);
            width: 80px;
        }

        .version-bar-container {
            flex: 1;
            height: 8px;
            background: var(--bg-secondary);
            border-radius: 4px;
            overflow: hidden;
        }

        .version-bar-fill {
            height: 100%;
            background: var(--primary);
            border-radius: 4px;
        }

        .version-count {
            font-size: 12px;
            color: var(--text-muted);
            width: 80px;
            text-align: right;
        }

        .announcement-card {
            background: var(--bg-card);
            border: 1px solid var(--border);
            border-radius: var(--radius-lg);
            padding: 20px;
            margin-bottom: 12px;
        }

        .announcement-card.critical {
            border-color: hsla(0, 84%, 60%, 0.3);
        }

        .announcement-card.warning {
            border-color: hsla(38, 92%, 50%, 0.3);
        }

        .announcement-card.maintenance {
            border-color: hsla(260, 70%, 60%, 0.3);
        }

        .announcement-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 12px;
        }

        .announcement-type {
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
        }

        .announcement-type.info { background: hsla(215, 85%, 55%, 0.2); color: var(--primary); }
        .announcement-type.feature { background: hsla(142, 70%, 50%, 0.2); color: var(--success); }
        .announcement-type.warning { background: hsla(38, 92%, 50%, 0.2); color: var(--warning); }
        .announcement-type.maintenance { background: hsla(260, 70%, 60%, 0.2); color: var(--purple); }
        .announcement-type.critical { background: hsla(0, 84%, 60%, 0.2); color: var(--error); }

        .announcement-time {
            font-size: 12px;
            color: var(--text-muted);
        }

        .announcement-card h4 {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .announcement-card p {
            font-size: 14px;
            color: var(--text-secondary);
            margin-bottom: 16px;
        }

        .announcement-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .announcement-stats {
            font-size: 12px;
            color: var(--text-muted);
        }

        .announcement-stats i {
            margin-right: 6px;
        }

        .announcement-actions {
            display: flex;
            gap: 8px;
        }

        .status-badge {
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
        }

        .status-badge.premium { background: hsla(260, 70%, 60%, 0.2); color: var(--purple); }
        .status-badge.free { background: hsla(215, 20%, 20%, 0.5); color: var(--text-secondary); }
        .status-badge.redeemed { background: hsla(142, 70%, 50%, 0.2); color: var(--success); }
        .status-badge.available { background: hsla(215, 85%, 55%, 0.2); color: var(--primary); }

        .version-badge {
            padding: 2px 8px;
            background: var(--bg-secondary);
            border-radius: 4px;
            font-size: 12px;
            font-family: monospace;
        }

        .error-item {
            display: flex;
            gap: 12px;
            padding: 10px;
            background: hsla(0, 84%, 60%, 0.05);
            border-radius: var(--radius);
            margin-bottom: 8px;
        }

        .error-time {
            font-size: 12px;
            color: var(--text-muted);
            white-space: nowrap;
        }

        .error-message {
            font-size: 13px;
            color: var(--error);
        }
    `;
    document.head.appendChild(style);

})();

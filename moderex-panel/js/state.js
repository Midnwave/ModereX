/* ============================================
   ModereX Control Panel - State Management
   ============================================ */
(function() {
  const { uid, rnd, pick, clamp, now, parseDurationToMs } = window.MX.utils;

  // Main Application State
  const state = {
    authenticated: false,
    staffName: 'Admin',
    staff: [],
    currentUser: null,
    lang: 'en_US',
    players: [],
    punishments: [],
    templates: [],
    rules: [],
    evidence: [],
    logs: [],
    messages: {},
    activity: [],
    watchlist: new Set(),
    watchAlerts: [],
    selectedPlayerId: null,
    selectedPunishmentId: null,
    selectedEvidenceId: null,
    pendingPunishType: 'WARN',
    punishTargetLocked: false,
    punishFilters: { BAN: true, MUTE: true, WARN: true, KICK: true },
    logsFilters: {
      search: '',
      mxOnly: true,
      sev: { INFO: true, WARN: true, ERROR: true },
      types: {
        WARN: true, MUTE: true, BAN: true, IPBAN: true, UNBAN: true, UNMUTE: true, UNWARN: true, EXPIRE: true,
        KICK: true, COMMAND: true, CHAT: true, JOIN: true, LEAVE: true, AUTOMOD: true, SYSTEM: true
      },
      pageSize: 100,
      page: 1
    },
    logsRun: true,
    manualPaused: false,
    autoPaused: false,
    settings: {
      chatDisabled: false,
      slowEnabled: true,
      slowSeconds: 3,
      discordWebhook: '',
      whBan: true,
      whMute: true,
      whAutomod: true,
      whWarn: true,
      whCaseCreated: true,
      whCaseUpdated: false,
      whPunishRemoved: true,
      whWatchlist: true,
      warnNotify: true,
      warnAutoEscalate: false,
      muteChat: true,
      muteMsg: true,
      muteSigns: true,
      muteBooks: true,
      muteBroadcast: false,
      muteVoice: true,
      muteVoiceJoin: true,
      anticheatReplace: false,
      watchToasts: true
    },
    unsaved: {},
    wizard: { step: 0 },
    userPrefs: {},
    integrations: {
      voiceChatDetected: false,
      luckPermsDetected: false,
      anticheats: ['Grim', 'Vulcan', 'Matrix', 'Spartan', 'AAC', 'NoCheatPlus', 'Intave', 'NCP', 'Kauri', 'Verus', 'Negativity', 'AntiCheatReloaded', 'Themis', 'Astra', 'Polar', 'Warden', 'Flappy', 'Karhu']
    },
    anticheat: {
      provider: 'Grim',
      enabled: true,
      alerts: []
    },
    anticheatRule: {
      enabled: true,
      threshold: 6,
      windowMins: 2,
      action: 'mute'
    },
    // Per-staff anticheat settings
    anticheatChecks: [],           // All detected checks from server
    anticheatPreferences: {},      // Staff's per-check preferences { 'grim:simulation': { alertLevel: 'EVERYONE', thresholdCount: 5, timeWindowSeconds: 60 } }
    enabledAnticheats: []          // List of hooked anticheat names
  };

  // Generate Players (kept for backwards compatibility, but not used in initializeState)
  function generatePlayers(n = 36) {
    const baseNames = [
      'AquaWarden', 'SkyFletcher', 'IronMango', 'NovaCinder', 'RuneWalker', 'CobaltFox', 'PixelHawk', 'NeonPanda',
      'CrimsonOak', 'LunarScribe', 'QuartzViper', 'EchoKnight', 'Frostbyte', 'MagmaMint', 'ShadowKoi', 'SaffronJade',
      'BambooRift', 'StormBiscuit', 'KiteMonk', 'VantaRook', 'CloudBard', 'MintCrusader', 'FlareDusk', 'CitrusGolem',
      'RavenSpire', 'WispTundra', 'HazelQuill', 'TaffyDrake', 'ObsidianPip', 'DuneRanger', 'NimbleOtter', 'WoolWizard',
      'GildedGuppy', 'CandleMoth', 'ZenithNoodle', 'IvyGrit', 'MarbleMoss', 'CopperCrow', 'ArcticLoom', 'TulipTalon'
    ];
    const statuses = ['online', 'online', 'online', 'online', 'afk', 'offline'];
    const out = [];

    for (let i = 0; i < n; i++) {
      const name = baseNames[i % baseNames.length] + (i >= baseNames.length ? rnd(2, 99) : '');
      const bedrock = Math.random() < 0.22;
      const status = pick(statuses);
      const flags = clamp(rnd(0, 4), 0, 6);
      const uuidish = `${rnd(10000000, 99999999).toString(16)}-${rnd(1000, 9999)}-${rnd(1000, 9999)}-${rnd(1000, 9999)}-${rnd(100000000000, 999999999999).toString(16).slice(0, 12)}`;
      const ip = `192.168.${rnd(0, 255)}.${rnd(2, 254)}`;
      const last = status === 'online' ? now() - rnd(5000, 120000) : now() - rnd(600000, 86400000 * 6);

      out.push({
        id: uid('p'), name, platform: bedrock ? 'Bedrock' : 'Java', geyser: bedrock, status, uuid: uuidish, ip,
        alts: Math.random() < 0.20 ? [`192.168.${rnd(0, 255)}.${rnd(2, 254)}`] : [],
        lastSeen: last, flags, warnings: rnd(0, 3),
        recentCommands: ['/msg Player hey', '/home', '/spawn', '/warp hub', '/tpa Admin']
          .slice(0, rnd(3, 6))
          .map(cmd => ({ cmd, t: now() - rnd(60000, 86400000 * 3) })),
        notes: flags >= 4 ? 'Recurring automod violations' : ''
      });
    }
    return out;
  }

  function createPunishment(playerId, type, reason, duration, staff, evidenceId = null) {
    const id = uid('case');
    const createdAt = now();
    const dur = (duration || '').trim().toLowerCase();
    const expiresAt = parseDurationToMs(dur, createdAt);
    return {
      id, playerId, type, reason, duration: dur, staff: staff || state.staffName, evidenceId, createdAt, expiresAt,
      active: type !== 'WARN' && type !== 'KICK' ? (dur !== '0' && dur !== 'instant') : false,
      revoked: false
    };
  }

  function createEvidence(playerId, data) {
    return {
      id: uid('ev'), playerId, createdAt: now(),
      channel: data.channel || 'global', trigger: data.trigger || 'Automod',
      message: data.message || '', context: data.context || [],
      slowmode: data.slowmode ?? 'off', chatLocked: data.chatLocked ?? 'off'
    };
  }

  function initializeState() {
    // Start with empty arrays - real data comes from WebSocket
    state.players = [];
    state.staff = [];
    state.punishments = [];
    state.evidence = [];
    state.activity = [];

    // Default templates for punishment creation
    state.templates = [
      { id: 'none', name: 'None', type: 'WARN', duration: '', reason: '' },
      { id: uid('tpl'), name: 'Toxic Language', type: 'MUTE', duration: '7d', reason: 'Violation of chat conduct policy', color: 'warn' },
      { id: uid('tpl'), name: 'Exploiting', type: 'BAN', duration: 'perm', reason: 'Use of unauthorized game modifications', color: 'bad' },
      { id: uid('tpl'), name: 'Advertising', type: 'MUTE', duration: '1d', reason: 'Unauthorized promotional content', color: 'info' },
      { id: uid('tpl'), name: 'Harassment', type: 'BAN', duration: '30d', reason: 'Targeted harassment of other players', color: 'bad' },
      { id: uid('tpl'), name: 'First Offense', type: 'WARN', duration: '', reason: 'Initial rule violation warning', color: 'info' },
      { id: uid('tpl'), name: 'AFK Farming', type: 'KICK', duration: '', reason: 'Automated resource collection', color: 'warn' }
    ];

    // Default automod rules - will be replaced with server data if connected
    state.rules = [
      { id: uid('rule'), name: 'Spam/Gibberish Guard', enabled: true, locked: true, block: true, conditions: [{ kind: 'regex', value: '([a-zA-Z])\\1{4,}|[a-z]{10,}', match: 'contains' }], action: { kind: 'none', extra: '' }, threshold: { hits: 1, windowMins: 10 }, notes: 'Detects random spam strings' },
      { id: uid('rule'), name: 'Link Filter', enabled: true, block: true, conditions: [{ kind: 'link', value: '' }], action: { kind: 'none', extra: '' }, threshold: { hits: 1, windowMins: 10 }, notes: 'Blocks unauthorized external links' },
      { id: uid('rule'), name: 'Excessive Caps', enabled: true, block: true, conditions: [{ kind: 'caps', value: '65' }], action: { kind: 'warn', extra: 'Excessive capitalization' }, threshold: { hits: 2, windowMins: 10 }, notes: 'Warn on repeated caps messages' },
      { id: uid('rule'), name: 'Repeated Messages', enabled: true, block: true, conditions: [{ kind: 'repeat', value: '3', similar: true }], action: { kind: 'mute', extra: 'Repetitive messaging' }, threshold: { hits: 2, windowMins: 10 }, notes: 'Detects repeated or similar messages' },
      { id: uid('rule'), name: 'Prohibited Language', enabled: true, block: true, conditions: [{ kind: 'contains', value: 'kys', match: 'contains' }], action: { kind: 'mute', extra: 'Prohibited language' }, threshold: { hits: 1, windowMins: 10 }, notes: 'Immediate mute' }
    ];

    // Default anticheat alert settings
    const grimAlerts = [
      'Simulation', 'Reach', 'Aim', 'Velocity', 'NoFall', 'Scaffold', 'Speed', 'Fly', 'Step', 'FastBreak',
      'FastPlace', 'Criticals', 'Killaura', 'AutoClicker', 'Packet', 'BadPackets', 'Timer', 'InvalidMove',
      'Jesus', 'Elytra', 'Phase', 'BlockPlace', 'Inventory'
    ];
    state.anticheat.alerts = grimAlerts.map(name => ({
      id: uid('ac'),
      name,
      chat: true,
      toast: true,
      threshold: 6,
      windowMins: 2,
      autoPunish: 'none'
    }));

    // Default message templates
    state.messages = {
      en_US: {
        'ban.screen.title': '&cYou are banned!',
        'ban.screen.body': '&7Reason: &f<Reason>\\n&7By: &f<Moderator>\\n\\n&7Appeal: &fexample.com/appeal',
        'punish.mute.broadcast': '&e<Player> &7was muted for &f<Duration>',
        'punish.ban.broadcast': '&c<Player> &7was banned'
      },
      es_ES: {
        'ban.screen.title': '&c¡Estás baneado!',
        'ban.screen.body': '&7Razón: &f<Reason>\\n&7Por: &f<Moderator>',
        'punish.mute.broadcast': '&e<Player> &7fue silenciado',
        'punish.ban.broadcast': '&c<Player> &7fue baneado'
      }
    };
  }

  function loadState() {
    try {
      const raw = localStorage.getItem('mx_state_v1');
      if (!raw) return false;
      const data = JSON.parse(raw);
      Object.assign(state, data);
      state.watchlist = new Set(data.watchlist || []);
      return true;
    } catch (err) {
      return false;
    }
  }

  function saveState() {
    try {
      const out = { ...state, watchlist: [...state.watchlist] };
      localStorage.setItem('mx_state_v1', JSON.stringify(out));
    } catch (err) {
      // ignore persistence failures
    }
  }

  // Export to window
  window.MX = window.MX || {};
  window.MX.state = state;
  window.MX.stateUtils = { generatePlayers, createPunishment, createEvidence, initializeState, loadState, saveState };
})();

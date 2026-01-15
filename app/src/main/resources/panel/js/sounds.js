/* ============================================
   ModereX Control Panel - Sound System
   ============================================ */
(function() {
  'use strict';

  // Audio context for generating sounds
  let audioContext = null;

  // Sound settings
  let settings = {
    enabled: true,
    volume: 0.5
  };

  // Load settings from localStorage
  function loadSettings() {
    try {
      const saved = localStorage.getItem('mx_sound_settings');
      if (saved) {
        const parsed = JSON.parse(saved);
        settings = { ...settings, ...parsed };
      }
    } catch (e) {}
  }

  // Save settings to localStorage
  function saveSettings() {
    try {
      localStorage.setItem('mx_sound_settings', JSON.stringify(settings));
    } catch (e) {}
  }

  // Initialize audio context (must be called after user interaction)
  function initAudio() {
    if (audioContext) return;
    try {
      audioContext = new (window.AudioContext || window.webkitAudioContext)();
    } catch (e) {
      console.warn('[Sounds] Web Audio API not available');
    }
  }

  // Play a tone with specified frequency and duration
  function playTone(frequency, duration, type = 'sine', volume = 1) {
    if (!settings.enabled || !audioContext) return;

    try {
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);

      oscillator.type = type;
      oscillator.frequency.setValueAtTime(frequency, audioContext.currentTime);

      const actualVolume = settings.volume * volume * 0.3;
      gainNode.gain.setValueAtTime(actualVolume, audioContext.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + duration);

      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + duration);
    } catch (e) {}
  }

  // Play a sequence of tones
  function playSequence(notes, interval = 0.1) {
    if (!settings.enabled || !audioContext) return;

    // Resume audio context if suspended (browser requirement)
    if (audioContext.state === 'suspended') {
      audioContext.resume();
    }

    notes.forEach((note, i) => {
      setTimeout(() => {
        playTone(note.freq, note.dur || 0.15, note.type || 'sine', note.vol || 1);
      }, i * interval * 1000);
    });
  }

  // === Connection Sounds ===

  // Connection established - pleasant ascending chime
  function playConnect() {
    playSequence([
      { freq: 523.25, dur: 0.1 },  // C5
      { freq: 659.25, dur: 0.1 },  // E5
      { freq: 783.99, dur: 0.2 }   // G5
    ], 0.08);
  }

  // Disconnected - descending tone
  function playDisconnect() {
    playSequence([
      { freq: 440, dur: 0.15 },    // A4
      { freq: 349.23, dur: 0.2 }   // F4
    ], 0.12);
  }

  // Reconnecting attempt
  function playReconnecting() {
    playSequence([
      { freq: 392, dur: 0.1, vol: 0.5 },   // G4
      { freq: 440, dur: 0.1, vol: 0.5 }    // A4
    ], 0.15);
  }

  // === Toast Sounds ===

  // Toast success - bright positive
  function playToastSuccess() {
    playSequence([
      { freq: 659.25, dur: 0.06 },  // E5
      { freq: 783.99, dur: 0.1 }    // G5
    ], 0.05);
  }

  // Toast info - neutral ping
  function playToastInfo() {
    playTone(698.46, 0.1, 'sine', 0.5); // F5
  }

  // Toast warning - attention
  function playToastWarning() {
    playSequence([
      { freq: 493.88, dur: 0.08, type: 'triangle' },  // B4
      { freq: 493.88, dur: 0.1, type: 'triangle' }    // B4
    ], 0.1);
  }

  // Toast error - low alert
  function playToastError() {
    playSequence([
      { freq: 293.66, dur: 0.12, type: 'triangle' },  // D4
      { freq: 261.63, dur: 0.15, type: 'triangle' }   // C4
    ], 0.1);
  }

  // === Alert Sounds ===

  // General notification
  function playNotification() {
    playSequence([
      { freq: 880, dur: 0.08, vol: 0.6 },
      { freq: 1046.5, dur: 0.12, vol: 0.4 }
    ], 0.06);
  }

  // Slide-down alert bar
  function playAlertBar() {
    playSequence([
      { freq: 523.25, dur: 0.05 },  // C5
      { freq: 659.25, dur: 0.05 },  // E5
      { freq: 783.99, dur: 0.08 },  // G5
      { freq: 1046.5, dur: 0.12 }   // C6
    ], 0.04);
  }

  // Urgent alert
  function playUrgentAlert() {
    playSequence([
      { freq: 880, dur: 0.08 },     // A5
      { freq: 880, dur: 0.08 },     // A5
      { freq: 1046.5, dur: 0.12 }   // C6
    ], 0.08);
  }

  // Warning alert
  function playWarning() {
    playSequence([
      { freq: 440, dur: 0.12, type: 'triangle' },
      { freq: 440, dur: 0.12, type: 'triangle' }
    ], 0.15);
  }

  // Error alert
  function playError() {
    playSequence([
      { freq: 220, dur: 0.2, type: 'triangle' },
      { freq: 196, dur: 0.25, type: 'triangle' }
    ], 0.15);
  }

  // Success
  function playSuccess() {
    playSequence([
      { freq: 523.25, dur: 0.08 },
      { freq: 659.25, dur: 0.12 }
    ], 0.06);
  }

  // === Event Sounds ===

  // Staff chat message
  function playStaffChat() {
    playSequence([
      { freq: 659.25, dur: 0.06, vol: 0.7 },
      { freq: 783.99, dur: 0.1, vol: 0.5 }
    ], 0.05);
  }

  // Punishment created
  function playPunishment() {
    playSequence([
      { freq: 392, dur: 0.1 },
      { freq: 493.88, dur: 0.15 }
    ], 0.08);
  }

  // Punishment revoked/pardon
  function playPardon() {
    playSequence([
      { freq: 523.25, dur: 0.08 },
      { freq: 659.25, dur: 0.08 },
      { freq: 783.99, dur: 0.12 }
    ], 0.06);
  }

  // Watchlist alert
  function playWatchlist() {
    playSequence([
      { freq: 554.37, dur: 0.08 },
      { freq: 659.25, dur: 0.08 },
      { freq: 554.37, dur: 0.12 }
    ], 0.07);
  }

  // Anticheat alert
  function playAnticheat() {
    playSequence([
      { freq: 587.33, dur: 0.06 },  // D5
      { freq: 698.46, dur: 0.06 },  // F5
      { freq: 880, dur: 0.1 }       // A5
    ], 0.05);
  }

  // Automod trigger
  function playAutomod() {
    playSequence([
      { freq: 493.88, dur: 0.08, vol: 0.6 },  // B4
      { freq: 587.33, dur: 0.1, vol: 0.5 }    // D5
    ], 0.06);
  }

  // Player join
  function playPlayerJoin() {
    playTone(783.99, 0.1, 'sine', 0.4); // G5
  }

  // Player leave
  function playPlayerLeave() {
    playTone(523.25, 0.12, 'sine', 0.3); // C5
  }

  // Command executed
  function playCommand() {
    playTone(880, 0.06, 'sine', 0.3); // A5
  }

  // === UI Sounds ===

  // Button click
  function playClick() {
    playTone(1000, 0.03, 'sine', 0.3);
  }

  // Toggle switch
  function playToggle() {
    playTone(800, 0.04, 'sine', 0.35);
  }

  // Modal open
  function playModalOpen() {
    playSequence([
      { freq: 440, dur: 0.04, vol: 0.4 },
      { freq: 523.25, dur: 0.06, vol: 0.3 }
    ], 0.03);
  }

  // Modal close
  function playModalClose() {
    playTone(392, 0.06, 'sine', 0.3); // G4
  }

  // Tab switch
  function playTabSwitch() {
    playTone(698.46, 0.04, 'sine', 0.25); // F5
  }

  // Dropdown open
  function playDropdown() {
    playTone(880, 0.03, 'sine', 0.2);
  }

  // Text input focus
  function playFocus() {
    playTone(1046.5, 0.02, 'sine', 0.15);
  }

  // Action complete
  function playComplete() {
    playSequence([
      { freq: 523.25, dur: 0.05 },
      { freq: 659.25, dur: 0.05 },
      { freq: 783.99, dur: 0.08 }
    ], 0.04);
  }

  // Public API
  window.MX = window.MX || {};
  window.MX.sounds = {
    init: initAudio,

    // Settings
    isEnabled: () => settings.enabled,
    setEnabled: (enabled) => {
      settings.enabled = enabled;
      saveSettings();
    },
    getVolume: () => settings.volume,
    setVolume: (volume) => {
      settings.volume = Math.max(0, Math.min(1, volume));
      saveSettings();
    },
    toggle: () => {
      settings.enabled = !settings.enabled;
      saveSettings();
      return settings.enabled;
    },

    // Connection
    connect: playConnect,
    disconnect: playDisconnect,
    reconnecting: playReconnecting,

    // Toasts
    toastSuccess: playToastSuccess,
    toastInfo: playToastInfo,
    toastWarning: playToastWarning,
    toastError: playToastError,

    // Alerts
    notification: playNotification,
    alertBar: playAlertBar,
    urgentAlert: playUrgentAlert,
    warning: playWarning,
    error: playError,
    success: playSuccess,

    // Events
    staffChat: playStaffChat,
    punishment: playPunishment,
    pardon: playPardon,
    watchlist: playWatchlist,
    anticheat: playAnticheat,
    automod: playAutomod,
    playerJoin: playPlayerJoin,
    playerLeave: playPlayerLeave,
    command: playCommand,

    // UI
    click: playClick,
    toggle: playToggle,
    modalOpen: playModalOpen,
    modalClose: playModalClose,
    tabSwitch: playTabSwitch,
    dropdown: playDropdown,
    focus: playFocus,
    complete: playComplete,

    // Generic/custom
    alert: playUrgentAlert,
    message: playNotification,
    tone: playTone,
    sequence: playSequence
  };

  // Load settings on init
  loadSettings();

  // Cross-tab sync: listen for storage changes
  window.addEventListener('storage', (e) => {
    if (e.key === 'mx_sound_settings' && e.newValue) {
      try {
        const newSettings = JSON.parse(e.newValue);
        settings = { ...settings, ...newSettings };
        console.log('[Sounds] Settings synced from another tab:', settings);
      } catch (err) {}
    }
  });

  // WebSocket sync: listen for settings updates
  window.addEventListener('mx:settings_updated', (e) => {
    if (e.detail && e.detail.soundEnabled !== undefined) {
      settings.enabled = e.detail.soundEnabled;
      saveSettings();
      console.log('[Sounds] Settings synced from server:', settings);
    }
  });

  // Initialize audio context on first user interaction
  const initOnInteraction = () => {
    initAudio();
    document.removeEventListener('click', initOnInteraction);
    document.removeEventListener('keydown', initOnInteraction);
  };
  document.addEventListener('click', initOnInteraction);
  document.addEventListener('keydown', initOnInteraction);

})();

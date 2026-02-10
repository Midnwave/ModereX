/* ============================================
   ModereX Control Panel - 3D Replay Viewer
   ============================================
   Three.js 3D replay viewer with terrain rendering
   and Minecraft player models. Integrates with the
   terrain engine from replay3d-terrain.js.
*/

(function() {
  'use strict';

  if (typeof THREE === 'undefined') {
    console.warn('[Replay3D] Three.js not loaded, 3D viewer disabled');
    return;
  }

  // ===== REPLAY SOUND MANAGER =====
  // Realistic Minecraft-style sound effects using multi-grain noise synthesis.
  // Uses filtered white noise buffers with overlapping grains to produce
  // sounds that closely approximate real Minecraft audio. No external files needed.

  class ReplaySoundManager {
    constructor() {
      this._ctx = null;
      this._masterGain = null;
      this._noiseBuffer = null;
      this._volume = 0.3;
      this._muted = false;
      this._enabled = true;
      this._playbackSpeed = 1;

      // Footstep state
      this._lastFootstepTime = 0;
      this._footstepInterval = 380; // ms between footsteps at normal walk speed
      this._footstepAlternate = false; // alternate left/right foot

      // Throttle: prevent sound spam
      this._lastSoundTimes = {};
      this._minInterval = 60; // ms minimum between same sound type

      // Load settings from localStorage
      this._loadSettings();
    }

    // --- Initialization ---

    /**
     * Initialize the AudioContext and pre-generate the white noise buffer.
     * Must be called after a user gesture.
     */
    init() {
      if (this._ctx) return;
      try {
        this._ctx = new (window.AudioContext || window.webkitAudioContext)();
        this._masterGain = this._ctx.createGain();
        this._masterGain.gain.setValueAtTime(this._muted ? 0 : this._volume, this._ctx.currentTime);
        this._masterGain.connect(this._ctx.destination);

        // Pre-generate 1 second of white noise and reuse for all sounds
        this._noiseBuffer = this._createNoiseBuffer(1.0);
      } catch (e) {
        console.warn('[ReplaySounds] Web Audio API not available:', e.message);
      }
    }

    /**
     * Create a white noise AudioBuffer of the given duration (seconds).
     * This buffer is reused by all grain-based sound effects.
     */
    _createNoiseBuffer(duration) {
      const sampleRate = this._ctx.sampleRate;
      const length = Math.floor(sampleRate * duration);
      const buffer = this._ctx.createBuffer(1, length, sampleRate);
      const data = buffer.getChannelData(0);
      for (let i = 0; i < length; i++) {
        data[i] = Math.random() * 2 - 1;
      }
      return buffer;
    }

    /**
     * Resume AudioContext if suspended (browser autoplay policy).
     */
    _resume() {
      if (this._ctx && this._ctx.state === 'suspended') {
        this._ctx.resume();
      }
    }

    // --- Settings persistence ---

    _loadSettings() {
      try {
        const saved = localStorage.getItem('mx_replay_sound_settings');
        if (saved) {
          const parsed = JSON.parse(saved);
          if (typeof parsed.volume === 'number') this._volume = parsed.volume;
          if (typeof parsed.muted === 'boolean') this._muted = parsed.muted;
        }
      } catch (e) { /* ignore */ }
    }

    _saveSettings() {
      try {
        localStorage.setItem('mx_replay_sound_settings', JSON.stringify({
          volume: this._volume,
          muted: this._muted
        }));
      } catch (e) { /* ignore */ }
    }

    // --- Controls ---

    setVolume(vol) {
      this._volume = Math.max(0, Math.min(1, vol));
      if (this._masterGain && !this._muted) {
        this._masterGain.gain.setTargetAtTime(this._volume, this._ctx.currentTime, 0.02);
      }
      this._saveSettings();
    }

    getVolume() {
      return this._volume;
    }

    setMuted(muted) {
      this._muted = muted;
      if (this._masterGain) {
        this._masterGain.gain.setTargetAtTime(muted ? 0 : this._volume, this._ctx.currentTime, 0.02);
      }
      this._saveSettings();
    }

    isMuted() {
      return this._muted;
    }

    toggleMute() {
      this.setMuted(!this._muted);
      return this._muted;
    }

    setPlaybackSpeed(speed) {
      this._playbackSpeed = speed;
    }

    // --- Throttle helper ---

    _canPlay(type) {
      if (!this._ctx || !this._enabled || this._muted) return false;
      const now = performance.now();
      const last = this._lastSoundTimes[type] || 0;
      if (now - last < this._minInterval) return false;
      this._lastSoundTimes[type] = now;
      return true;
    }

    // --- Core grain engine ---

    /**
     * Play a single filtered noise grain. Multiple overlapping grains
     * create the textured, realistic sound of Minecraft audio.
     * Uses the pre-generated 1s white noise buffer, starting at a random
     * offset each time for natural variation.
     *
     * @param {number} startTime   - AudioContext time to start
     * @param {number} duration    - Grain length in seconds
     * @param {number} filterFreq  - BiquadFilter frequency (Hz)
     * @param {number} filterQ     - BiquadFilter Q factor
     * @param {string} filterType  - 'bandpass', 'lowpass', 'highpass'
     * @param {number} volume      - Gain level (0-1, keep subtle: 0.03-0.1)
     * @param {number} [attack]    - Attack time in seconds (default 0.003)
     */
    _playGrain(startTime, duration, filterFreq, filterQ, filterType, volume, attack) {
      if (!this._noiseBuffer) return;
      const atk = attack || 0.003;
      const source = this._ctx.createBufferSource();
      source.buffer = this._noiseBuffer;
      // Start at a random offset within the noise buffer for variety
      const maxOffset = Math.max(0, this._noiseBuffer.duration - duration - 0.01);
      const offset = Math.random() * maxOffset;

      const filter = this._ctx.createBiquadFilter();
      filter.type = filterType;
      filter.frequency.setValueAtTime(filterFreq, startTime);
      filter.Q.setValueAtTime(filterQ, startTime);

      const gain = this._ctx.createGain();
      gain.gain.setValueAtTime(0, startTime);
      gain.gain.linearRampToValueAtTime(volume, startTime + atk);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + duration);

      source.connect(filter);
      filter.connect(gain);
      gain.connect(this._masterGain);
      source.start(startTime, offset, duration + 0.01);
    }

    /**
     * Play a sine/triangle tone grain (for tonal sounds like bow twang).
     *
     * @param {number} startTime  - AudioContext time to start
     * @param {number} duration   - Tone length in seconds
     * @param {number} freqStart  - Starting frequency (Hz)
     * @param {number} freqEnd    - Ending frequency (Hz)
     * @param {string} waveType   - Oscillator type ('sine', 'triangle')
     * @param {number} volume     - Gain level (0-1)
     */
    _playTone(startTime, duration, freqStart, freqEnd, waveType, volume) {
      const osc = this._ctx.createOscillator();
      const gain = this._ctx.createGain();
      osc.type = waveType;
      osc.frequency.setValueAtTime(freqStart, startTime);
      osc.frequency.exponentialRampToValueAtTime(freqEnd, startTime + duration);
      gain.gain.setValueAtTime(volume, startTime);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + duration);
      osc.connect(gain);
      gain.connect(this._masterGain);
      osc.start(startTime);
      osc.stop(startTime + duration + 0.01);
    }

    /**
     * Play a sine chirp (frequency sweep) - used for item pickup pop sound.
     * The Minecraft item pickup is a clean rising tone, so an oscillator is
     * the right tool here rather than noise grains.
     *
     * @param {number} startTime  - AudioContext time to start
     * @param {number} duration   - Chirp length in seconds
     * @param {number} freqStart  - Starting frequency (Hz)
     * @param {number} freqEnd    - Ending frequency (Hz)
     * @param {number} volume     - Gain level (0-1)
     */
    _playChirp(startTime, duration, freqStart, freqEnd, volume) {
      const osc = this._ctx.createOscillator();
      const gain = this._ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(freqStart, startTime);
      osc.frequency.linearRampToValueAtTime(freqEnd, startTime + duration);
      gain.gain.setValueAtTime(0, startTime);
      gain.gain.linearRampToValueAtTime(volume, startTime + 0.002);
      gain.gain.setValueAtTime(volume, startTime + duration * 0.6);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + duration);
      osc.connect(gain);
      gain.connect(this._masterGain);
      osc.start(startTime);
      osc.stop(startTime + duration + 0.01);
    }

    // --- Sound generators (multi-grain noise synthesis) ---

    /**
     * Block break: crunchy stone/dirt breaking sound.
     * 4 overlapping noise grains at slightly randomized filter frequencies
     * plus a low-frequency thud for impact. The overlapping grains at
     * different center frequencies create the textured crunch that makes
     * this sound like actual stone breaking rather than a single beep.
     */
    // Material-specific sound parameters for Minecraft-accurate audio
    static SOUND_PARAMS = {
      stone:  { freq: [600, 800, 400, 1200], q: [2, 1.5, 3, 1], dur: [0.12, 0.10, 0.08, 0.06], vol: 1.0, thud: 200 },
      wood:   { freq: [400, 550, 300, 800],  q: [1.5, 1.2, 2, 0.8], dur: [0.14, 0.12, 0.10, 0.07], vol: 0.9, thud: 150 },
      grass:  { freq: [800, 1000, 600, 1400], q: [1, 0.8, 1.5, 0.6], dur: [0.08, 0.07, 0.06, 0.05], vol: 0.7, thud: 100 },
      sand:   { freq: [300, 400, 200, 500],  q: [0.8, 0.6, 1, 0.5], dur: [0.15, 0.12, 0.10, 0.08], vol: 0.6, thud: 120 },
      gravel: { freq: [500, 700, 350, 900],  q: [1.5, 1, 2, 0.8], dur: [0.10, 0.09, 0.07, 0.06], vol: 0.8, thud: 160 },
      glass:  { freq: [1500, 2000, 1200, 2500], q: [3, 2.5, 4, 2], dur: [0.06, 0.05, 0.04, 0.03], vol: 0.5, thud: 80 },
      metal:  { freq: [800, 1200, 600, 1600], q: [4, 3, 5, 2.5], dur: [0.18, 0.15, 0.12, 0.08], vol: 0.8, thud: 250 },
      cloth:  { freq: [600, 800, 400, 1000], q: [0.6, 0.5, 0.8, 0.4], dur: [0.10, 0.08, 0.07, 0.05], vol: 0.5, thud: 80 },
      snow:   { freq: [1000, 1200, 800, 1500], q: [0.5, 0.4, 0.6, 0.3], dur: [0.06, 0.05, 0.04, 0.03], vol: 0.4, thud: 60 },
    };

    static BLOCK_SOUND_CATEGORIES = {
      stone: /stone|cobblestone|andesite|granite|diorite|deepslate|obsidian|bricks|prismarine|purpur|quartz|basalt|blackstone|calcite|tuff|end_stone|netherrack|terracotta|concrete(?!_powder)/,
      wood: /oak|spruce|birch|jungle|acacia|dark_oak|cherry|mangrove|bamboo|crimson|warped|planks|log|stem|bookshelf|crafting/,
      grass: /grass|dirt|podzol|mycelium|farmland|mud|moss|rooted_dirt|dirt_path/,
      sand: /sand(?!stone)|concrete_powder/,
      gravel: /gravel/,
      glass: /glass/,
      metal: /iron_block|gold_block|copper|anvil|chain|netherite_block|raw_iron|raw_gold|raw_copper|lantern|hopper/,
      cloth: /wool|carpet|banner|bed/,
      snow: /snow|powder_snow|ice/,
    };

    _getSoundCategory(blockName) {
      if (!blockName) return 'stone';
      const name = blockName.replace('minecraft:', '').toLowerCase();
      for (const [cat, regex] of Object.entries(ReplaySoundManager.BLOCK_SOUND_CATEGORIES)) {
        if (regex.test(name)) return cat;
      }
      return 'stone';
    }

    playBlockBreak(blockName) {
      if (!this._canPlay('block_break')) return;
      this._resume();
      const t = this._ctx.currentTime;
      const cat = this._getSoundCategory(blockName);
      const p = ReplaySoundManager.SOUND_PARAMS[cat] || ReplaySoundManager.SOUND_PARAMS.stone;
      const v = p.vol;
      this._playGrain(t, p.dur[0], p.freq[0] + Math.random() * 400, p.q[0], 'bandpass', 0.08 * v);
      this._playGrain(t + 0.008, p.dur[1], p.freq[1] + Math.random() * 300, p.q[1], 'bandpass', 0.06 * v);
      this._playGrain(t + 0.015, p.dur[2], p.freq[2] + Math.random() * 200, p.q[2], 'bandpass', 0.05 * v);
      this._playGrain(t + 0.005, p.dur[3], p.freq[3] + Math.random() * 500, p.q[3], 'bandpass', 0.03 * v);
      this._playGrain(t, 0.05, p.thud, 1, 'lowpass', 0.04 * v);
    }

    playBlockPlace(blockName) {
      if (!this._canPlay('block_place')) return;
      this._resume();
      const t = this._ctx.currentTime;
      const cat = this._getSoundCategory(blockName);
      const p = ReplaySoundManager.SOUND_PARAMS[cat] || ReplaySoundManager.SOUND_PARAMS.stone;
      const v = p.vol * 0.8;
      this._playGrain(t, p.dur[0] * 0.7, p.freq[0] + Math.random() * 300, p.q[0] * 1.2, 'bandpass', 0.06 * v);
      this._playGrain(t + 0.005, p.dur[1] * 0.7, p.freq[1] + Math.random() * 200, p.q[1], 'bandpass', 0.05 * v);
      this._playGrain(t + 0.01, p.dur[2] * 0.7, p.freq[2] + Math.random() * 150, p.q[2], 'bandpass', 0.04 * v);
      this._playGrain(t, 0.04, p.thud * 0.9, 1, 'lowpass', 0.035 * v);
    }

    playFootstep(currentTimeMs, blockName) {
      const interval = this._footstepInterval / Math.max(this._playbackSpeed, 0.25);
      if (currentTimeMs - this._lastFootstepTime < interval) return false;
      if (!this._canPlay('footstep')) return false;
      this._resume();
      this._lastFootstepTime = currentTimeMs;
      this._footstepAlternate = !this._footstepAlternate;

      const t = this._ctx.currentTime;
      const cat = this._getSoundCategory(blockName);
      const p = ReplaySoundManager.SOUND_PARAMS[cat] || ReplaySoundManager.SOUND_PARAMS.stone;
      const freqOffset = this._footstepAlternate ? 0 : 150;
      const v = p.vol * 0.5;
      this._playGrain(t, p.dur[0] * 0.35, p.freq[0] * 0.5 + freqOffset + Math.random() * 200, p.q[0], 'bandpass', 0.04 * v);
      this._playGrain(t + 0.003, p.dur[1] * 0.35, p.freq[1] + freqOffset + Math.random() * 300, p.q[1], 'bandpass', 0.03 * v);
      this._playGrain(t, 0.025, p.thud * 0.75, 1, 'lowpass', 0.025 * v);
      return true;
    }

    /**
     * Attack/swing: swooshing whoosh sound.
     * Noise through a bandpass filter that sweeps from high to low
     * frequency over the duration, simulating an arm swing through air.
     * Uses manual filter frequency automation for the sweep effect.
     */
    playAttack() {
      if (!this._canPlay('attack')) return;
      this._resume();
      const t = this._ctx.currentTime;
      const dur = 0.12;

      // Sweeping bandpass whoosh - create manually for frequency automation
      const source = this._ctx.createBufferSource();
      source.buffer = this._noiseBuffer;
      const maxOffset = Math.max(0, this._noiseBuffer.duration - dur - 0.01);

      const filter = this._ctx.createBiquadFilter();
      filter.type = 'bandpass';
      filter.Q.setValueAtTime(1.5, t);
      filter.frequency.setValueAtTime(2000 + Math.random() * 500, t);
      filter.frequency.exponentialRampToValueAtTime(300, t + dur);

      const gain = this._ctx.createGain();
      gain.gain.setValueAtTime(0, t);
      gain.gain.linearRampToValueAtTime(0.07, t + 0.008);
      gain.gain.setValueAtTime(0.07, t + dur * 0.3);
      gain.gain.exponentialRampToValueAtTime(0.001, t + dur);

      source.connect(filter);
      filter.connect(gain);
      gain.connect(this._masterGain);
      source.start(t, Math.random() * maxOffset, dur + 0.01);

      // Second layer for depth
      this._playGrain(t + 0.005, 0.08, 1500 + Math.random() * 400, 1, 'bandpass', 0.04);
      // Subtle low punch at impact
      this._playGrain(t, 0.04, 250, 1.5, 'lowpass', 0.03);
    }

    /**
     * Damage received: Minecraft "oof" hurt sound.
     * Three formant bands of filtered noise to simulate the vocal quality
     * of the classic Minecraft hurt sound. The high Q values on the bandpass
     * filters create resonant peaks that approximate human vocal formants.
     */
    playDamage() {
      if (!this._canPlay('damage')) return;
      this._resume();
      const t = this._ctx.currentTime;
      // Formant 1: low vocal band (~400 Hz)
      this._playGrain(t, 0.15, 400 + Math.random() * 50, 5, 'bandpass', 0.07);
      // Formant 2: mid vocal band (~800 Hz)
      this._playGrain(t, 0.12, 800 + Math.random() * 80, 3, 'bandpass', 0.05);
      // Formant 3: higher presence (~1200 Hz)
      this._playGrain(t + 0.01, 0.10, 1200 + Math.random() * 100, 2, 'bandpass', 0.03);
      // Low body thud
      this._playGrain(t, 0.06, 120, 1, 'lowpass', 0.04);
    }

    /**
     * Bow shoot: string release twang with arrow whoosh.
     * Combination of a sharp tonal transient (triangle wave for string
     * vibration) and a trailing noise sweep for the arrow release.
     */
    playBowShoot() {
      if (!this._canPlay('bow')) return;
      this._resume();
      const t = this._ctx.currentTime;

      // Sharp string twang - short tonal ping
      this._playTone(t, 0.06, 800 + Math.random() * 200, 400, 'triangle', 0.06);

      // Arrow whoosh - noise sweep from mid to high frequency
      const source = this._ctx.createBufferSource();
      source.buffer = this._noiseBuffer;
      const maxOffset = Math.max(0, this._noiseBuffer.duration - 0.16);

      const filter = this._ctx.createBiquadFilter();
      filter.type = 'bandpass';
      filter.Q.setValueAtTime(2, t);
      filter.frequency.setValueAtTime(400, t + 0.02);
      filter.frequency.exponentialRampToValueAtTime(1800, t + 0.12);

      const gain = this._ctx.createGain();
      gain.gain.setValueAtTime(0, t + 0.02);
      gain.gain.linearRampToValueAtTime(0.05, t + 0.04);
      gain.gain.exponentialRampToValueAtTime(0.001, t + 0.15);

      source.connect(filter);
      filter.connect(gain);
      gain.connect(this._masterGain);
      source.start(t + 0.02, Math.random() * maxOffset, 0.14);

      // High-frequency string rattle
      this._playGrain(t, 0.04, 2000 + Math.random() * 500, 2, 'bandpass', 0.03);
    }

    /**
     * Death: descending impact sequence.
     * Series of heavy thuds that descend in pitch, mimicking
     * the Minecraft death sound's falling quality. Each thud uses
     * multiple grains for weight, and a damage formant overlay
     * on the initial hit adds the vocal "oof" quality.
     */
    playDeath() {
      if (!this._canPlay('death')) return;
      this._resume();
      const t = this._ctx.currentTime;

      // Initial heavy impact
      this._playGrain(t, 0.15, 600 + Math.random() * 100, 2, 'bandpass', 0.09);
      this._playGrain(t, 0.10, 300, 1.5, 'lowpass', 0.07);

      // Descending sequence of thuds
      this._playGrain(t + 0.10, 0.12, 450 + Math.random() * 80, 2, 'bandpass', 0.07);
      this._playGrain(t + 0.10, 0.08, 200, 1.5, 'lowpass', 0.05);

      this._playGrain(t + 0.20, 0.14, 300 + Math.random() * 60, 2, 'bandpass', 0.06);
      this._playGrain(t + 0.20, 0.10, 150, 1, 'lowpass', 0.04);

      // Trailing fade - lowest thud (body fall)
      this._playGrain(t + 0.30, 0.18, 200 + Math.random() * 40, 1.5, 'bandpass', 0.04);
      this._playGrain(t + 0.30, 0.12, 100, 1, 'lowpass', 0.03);

      // Damage formant overlay on the initial hit for "oof" quality
      this._playGrain(t, 0.12, 400, 5, 'bandpass', 0.05);
      this._playGrain(t, 0.10, 800, 3, 'bandpass', 0.03);
    }

    /**
     * Item pickup: clean sine chirp (rising pop).
     * This is one of the few sounds that actually works well as a pure
     * oscillator - the Minecraft item pickup is a clean rising tone.
     */
    playItemPickup() {
      if (!this._canPlay('pickup')) return;
      this._resume();
      const t = this._ctx.currentTime;
      // Clean rising chirp 800->1400 Hz
      this._playChirp(t, 0.08, 800 + Math.random() * 50, 1400 + Math.random() * 100, 0.06);
    }

    /**
     * Drop item: shorter, lower-pitched falling pop (reverse of pickup).
     */
    playDropItem() {
      if (!this._canPlay('drop')) return;
      this._resume();
      const t = this._ctx.currentTime;
      // Falling chirp 1000->600 Hz
      this._playChirp(t, 0.06, 1000, 600 + Math.random() * 80, 0.05);
    }

    /**
     * No-op: ambient drone removed.
     * Minecraft does not have an ambient drone during normal gameplay.
     */
    startAmbient() {
      // Intentionally empty - no ambient drone
    }

    /**
     * No-op: ambient drone removed.
     */
    stopAmbient() {
      // Intentionally empty - no ambient drone
    }

    // --- Event dispatcher ---

    /**
     * Process an action event from the replay and play the corresponding sound.
     * @param {string} action - The ActionType string (e.g. 'ATTACK', 'BREAK_BLOCK')
     */
    playActionSound(action, actionData) {
      switch (action) {
        case 'ATTACK':
        case 'DAMAGE_DEALT':
        case 'SWING_ARM':
        case 'SPEAR_JAB':
        case 'SPEAR_CHARGE':
          this.playAttack();
          break;
        case 'DAMAGE_RECEIVED':
          this.playDamage();
          break;
        case 'BREAK_BLOCK':
          this.playBlockBreak(actionData);
          break;
        case 'PLACE_BLOCK':
          this.playBlockPlace(actionData);
          break;
        case 'BOW_SHOOT':
        case 'CROSSBOW_SHOOT':
          this.playBowShoot();
          break;
        case 'DEATH':
          this.playDeath();
          break;
        case 'ITEM_PICKUP':
          this.playItemPickup();
          break;
        case 'DROP_ITEM':
          this.playDropItem();
          break;
        // Silently ignore non-sound actions
        default:
          break;
      }
    }

    // --- Cleanup ---

    dispose() {
      if (this._ctx) {
        try { this._ctx.close(); } catch (e) {}
        this._ctx = null;
      }
      this._masterGain = null;
      this._noiseBuffer = null;
    }
  }


  const PLAYER_SCALE = 1.8;

  // ===== EQUIPMENT COLOR MAPS =====
  const ARMOR_MATERIAL_COLORS = {
    leather: 0x8B4513, chainmail: 0x9A9A9A, iron: 0xC8C8C8,
    golden: 0xFFD700, diamond: 0x5CE8E8, netherite: 0x3A3A3E, turtle: 0x3B7A3E,
  };

  const ITEM_COLORS = {
    wooden_sword: 0xB8945F, stone_sword: 0x7A7A7A, iron_sword: 0xC8C8C8,
    golden_sword: 0xFFD700, diamond_sword: 0x5CE8E8, netherite_sword: 0x3A3A3E,
    wooden_pickaxe: 0xB8945F, stone_pickaxe: 0x7A7A7A, iron_pickaxe: 0xC8C8C8,
    golden_pickaxe: 0xFFD700, diamond_pickaxe: 0x5CE8E8, netherite_pickaxe: 0x3A3A3E,
    wooden_axe: 0xB8945F, stone_axe: 0x7A7A7A, iron_axe: 0xC8C8C8,
    golden_axe: 0xFFD700, diamond_axe: 0x5CE8E8, netherite_axe: 0x3A3A3E,
    wooden_shovel: 0xB8945F, stone_shovel: 0x7A7A7A, iron_shovel: 0xC8C8C8,
    golden_shovel: 0xFFD700, diamond_shovel: 0x5CE8E8, netherite_shovel: 0x3A3A3E,
    wooden_hoe: 0xB8945F, stone_hoe: 0x7A7A7A, iron_hoe: 0xC8C8C8,
    golden_hoe: 0xFFD700, diamond_hoe: 0x5CE8E8, netherite_hoe: 0x3A3A3E,
    bow: 0x6B4226, crossbow: 0x5A4A3A, trident: 0x4AC8C8, shield: 0x8B4513,
    fishing_rod: 0x6B4226, flint_and_steel: 0x888888, shears: 0xAAAAAA,
    torch: 0xFFA500, ender_pearl: 0x2A6A4A, snowball: 0xEEEEEE,
    egg: 0xF5E6C8, bucket: 0xAAAAAA, water_bucket: 0x3366CC, lava_bucket: 0xFF6600,
    mace: 0x6A5A8A, wind_charge: 0xAACCFF,
  };

  function getItemColor(materialName) {
    if (!materialName) return 0xCCCCCC;
    const name = materialName.replace('minecraft:', '');
    if (ITEM_COLORS[name]) return ITEM_COLORS[name];
    // Try matching by suffix
    if (name.includes('sword')) return 0xCCCCCC;
    if (name.includes('pickaxe') || name.includes('axe') || name.includes('shovel') || name.includes('hoe')) return 0xBBBBBB;
    // Default: block-like item, use brown-ish
    return 0xAA8855;
  }

  function getArmorColor(materialName) {
    if (!materialName) return null;
    const name = materialName.replace('minecraft:', '');
    for (const [mat, color] of Object.entries(ARMOR_MATERIAL_COLORS)) {
      if (name.startsWith(mat + '_')) return color;
    }
    return 0xAAAAAA;
  }

  // ===== SKIN TEXTURE CACHE =====
  // Global cache to avoid re-fetching skins across viewer instances.
  // Stores THREE.Texture objects keyed by UUID.
  const _skinCache = new Map(); // uuid -> { texture, isLegacy }
  const _skinLoadingPromises = new Map(); // uuid -> Promise (deduplicates in-flight requests)

  // Skin proxy endpoints in priority order (first working one wins)
  const SKIN_PROXIES = [
    uuid => `https://crafatar.com/skins/${uuid}?default=MHF_Steve`,
    uuid => `https://mc-heads.net/skin/${uuid}`,
    uuid => `https://visage.surgeplay.com/skin/64/${uuid}`,
  ];

  /**
   * Load a skin texture for a given UUID. Returns a Promise that resolves
   * to { texture: THREE.Texture, isLegacy: boolean } or null on failure.
   * Results are cached globally.
   */
  function loadSkinTexture(uuid) {
    // Return cached result immediately
    if (_skinCache.has(uuid)) {
      return Promise.resolve(_skinCache.get(uuid));
    }

    // Deduplicate in-flight requests for the same UUID
    if (_skinLoadingPromises.has(uuid)) {
      return _skinLoadingPromises.get(uuid);
    }

    const promise = _tryLoadSkin(uuid, 0).then(result => {
      _skinLoadingPromises.delete(uuid);
      if (result) {
        _skinCache.set(uuid, result);
      }
      return result;
    }).catch(err => {
      _skinLoadingPromises.delete(uuid);
      console.warn(`[Replay3D] All skin proxies failed for ${uuid}:`, err.message);
      return null;
    });

    _skinLoadingPromises.set(uuid, promise);
    return promise;
  }

  /**
   * Try loading skin from proxy at given index, falling back to next on failure.
   */
  function _tryLoadSkin(uuid, proxyIndex) {
    if (proxyIndex >= SKIN_PROXIES.length) {
      return Promise.resolve(null);
    }

    const url = SKIN_PROXIES[proxyIndex](uuid);

    return new Promise((resolve, reject) => {
      const img = new Image();
      img.crossOrigin = 'anonymous';

      // 8-second timeout per proxy attempt
      const timeout = setTimeout(() => {
        img.src = '';
        resolve(_tryLoadSkin(uuid, proxyIndex + 1));
      }, 8000);

      img.onload = () => {
        clearTimeout(timeout);
        const texture = new THREE.Texture(img);
        texture.magFilter = THREE.NearestFilter;
        texture.minFilter = THREE.NearestFilter;
        texture.generateMipmaps = false;
        texture.needsUpdate = true;

        const isLegacy = img.height === 32; // 64x32 = old skin format
        resolve({ texture, isLegacy });
      };

      img.onerror = () => {
        clearTimeout(timeout);
        // Try next proxy
        resolve(_tryLoadSkin(uuid, proxyIndex + 1));
      };

      img.src = url;
    });
  }


  class Replay3DViewer {
    constructor(container) {
      this.container = container;
      this.scene = null;
      this.camera = null;
      this.renderer = null;
      this.clock = new THREE.Clock();
      this.players = new Map();
      this.playerColors = [
        0x5a9cff, 0xff6b6b, 0x51cf66, 0xffc078, 0xcc5de8,
        0x20c997, 0xff8787, 0x748ffc, 0xffd43b, 0x69db7c
      ];
      this.colorIndex = 0;

      // Terrain
      this.chunkManager = null;
      this.blockApplicator = null;
      this.terrainLoaded = false;

      // BlueMap integration
      this.blueMapConfig = null;
      this.blueMapTiles = null; // THREE.Group for tile planes

      // Camera
      this.freeCamera = null;
      this.orbitTarget = new THREE.Vector3(0, 70, 0);
      this.orbitDistance = 40;
      this.orbitAngleX = Math.PI / 5;
      this.orbitAngleY = Math.PI / 4;
      this.cameraMode = 'orbit';
      this.isDragging = false;
      this.lastMouseX = 0;
      this.lastMouseY = 0;

      // Playback
      this.snapshots = [];
      this.blockLogs = [];
      this.startTime = 0;
      this.totalDuration = 0;
      this.currentTime = 0;
      this.playing = false;
      this.playbackSpeed = 1;

      // Pre-indexed player data
      this._playerSnapshots = new Map(); // uuid -> sorted snapshot array
      this._actionEvents = []; // snapshots where action !== 'NONE'

      // Callbacks
      this._onTimeUpdate = null;
      this._onPlaybackEnd = null;

      // Animation
      this.animationId = null;

      // Performance: dirty flag to skip redundant renders when paused/idle
      this._needsRender = true;
      this._lastCameraMatrix = new THREE.Matrix4();

      // Frustum for culling
      this._frustum = new THREE.Frustum();
      this._frustumMatrix = new THREE.Matrix4();

      // Sound manager
      this.soundManager = new ReplaySoundManager();
      this._lastSoundActionTime = 0;

      this._init();
    }

    _init() {
      // Scene with sky blue background
      this.scene = new THREE.Scene();
      this.scene.background = new THREE.Color(0x78b9e2);
      this.scene.fog = new THREE.FogExp2(0x9ec8e0, 0.002);

      // Camera
      const w = this.container.clientWidth || 800;
      const h = this.container.clientHeight || 500;
      this.camera = new THREE.PerspectiveCamera(70, w / h, 0.1, 2000);
      this.camera.position.set(0, 80, 40);

      // Renderer
      this.renderer = new THREE.WebGLRenderer({ antialias: true, powerPreference: 'high-performance' });
      this.renderer.setSize(w, h);
      this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      this.renderer.shadowMap.enabled = true;
      this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
      this.container.appendChild(this.renderer.domElement);

      this._setupLights();
      this._setupOrbitControls();

      // Free camera controls from terrain engine
      const terrain = window.MX?.terrain;
      if (terrain?.FreeCameraControls) {
        this.freeCamera = new terrain.FreeCameraControls(this.camera, this.renderer.domElement);
      }

      // Terrain manager from terrain engine
      if (terrain?.ChunkColumnManager) {
        this.chunkManager = new terrain.ChunkColumnManager(this.scene);
      }

      // Block change applicator
      if (this.chunkManager && terrain?.BlockChangeApplicator) {
        this.blockApplicator = new terrain.BlockChangeApplicator(this.chunkManager);
      }

      // Resize observer
      this._resizeObserver = new ResizeObserver(() => this.resize());
      this._resizeObserver.observe(this.container);

      // Start render loop
      this._animate();
    }

    _setupLights() {
      // Ambient
      const ambient = new THREE.AmbientLight(0x8899bb, 0.7);
      this.scene.add(ambient);

      // Sun
      const sun = new THREE.DirectionalLight(0xfff4e0, 1.2);
      sun.position.set(100, 200, 80);
      sun.castShadow = true;
      sun.shadow.mapSize.width = 2048;
      sun.shadow.mapSize.height = 2048;
      sun.shadow.camera.near = 10;
      sun.shadow.camera.far = 500;
      sun.shadow.camera.left = -120;
      sun.shadow.camera.right = 120;
      sun.shadow.camera.top = 120;
      sun.shadow.camera.bottom = -120;
      this.scene.add(sun);

      // Fill light
      const fill = new THREE.DirectionalLight(0x6080ff, 0.3);
      fill.position.set(-60, 40, -60);
      this.scene.add(fill);

      // Hemisphere
      const hemi = new THREE.HemisphereLight(0x87ceeb, 0x3d5c35, 0.5);
      this.scene.add(hemi);
    }

    _setupOrbitControls() {
      const canvas = this.renderer.domElement;

      canvas.addEventListener('mousedown', (e) => {
        if (this.cameraMode !== 'orbit' && this.cameraMode !== 'follow') return;
        if (e.button === 0 || e.button === 2) {
          this.isDragging = true;
          this.lastMouseX = e.clientX;
          this.lastMouseY = e.clientY;
        }
      });

      canvas.addEventListener('mousemove', (e) => {
        if (!this.isDragging || (this.cameraMode !== 'orbit' && this.cameraMode !== 'follow')) return;
        const dx = e.clientX - this.lastMouseX;
        const dy = e.clientY - this.lastMouseY;
        this.orbitAngleY += dx * 0.01;
        this.orbitAngleX = Math.max(0.05, Math.min(Math.PI / 2 - 0.05, this.orbitAngleX + dy * 0.01));
        this._updateOrbitCamera();
        this._needsRender = true;
        this.lastMouseX = e.clientX;
        this.lastMouseY = e.clientY;
      });

      canvas.addEventListener('mouseup', () => { this.isDragging = false; });
      canvas.addEventListener('mouseleave', () => { this.isDragging = false; });

      canvas.addEventListener('wheel', (e) => {
        if (this.cameraMode !== 'orbit' && this.cameraMode !== 'follow') return;
        e.preventDefault();
        this.orbitDistance = Math.max(5, Math.min(200, this.orbitDistance + e.deltaY * 0.1));
        this._updateOrbitCamera();
        this._needsRender = true;
      });

      canvas.addEventListener('contextmenu', (e) => e.preventDefault());
    }

    _updateOrbitCamera() {
      const t = this.orbitTarget;
      this.camera.position.set(
        t.x + this.orbitDistance * Math.cos(this.orbitAngleX) * Math.sin(this.orbitAngleY),
        t.y + this.orbitDistance * Math.sin(this.orbitAngleX),
        t.z + this.orbitDistance * Math.cos(this.orbitAngleX) * Math.cos(this.orbitAngleY)
      );
      this.camera.lookAt(t);
    }

    // ===== DATA LOADING =====

    setReplayData(replay, snapshots, blockLogs) {
      this.snapshots = snapshots || [];
      this.blockLogs = blockLogs || [];
      this.startTime = replay.startTime || 0;
      this.totalDuration = (replay.endTime || 0) - this.startTime;
      this.currentTime = 0;
      this.playing = false;

      // Pre-index snapshots by player UUID for efficient binary search
      this._playerSnapshots = new Map();
      this._actionEvents = [];

      for (const snap of this.snapshots) {
        const uuid = snap.playerUuid;
        if (!this._playerSnapshots.has(uuid)) {
          this._playerSnapshots.set(uuid, []);
        }
        this._playerSnapshots.get(uuid).push(snap);

        if (snap.action && snap.action !== 'NONE') {
          this._actionEvents.push(snap);
        }
      }

      // Sort each player's snapshots by timestamp
      for (const [, snaps] of this._playerSnapshots) {
        snaps.sort((a, b) => a.timestamp - b.timestamp);
      }

      // Sort action events by timestamp
      this._actionEvents.sort((a, b) => a.timestamp - b.timestamp);

      // Set block logs on applicator
      if (this.blockApplicator && this.blockLogs.length > 0) {
        this.blockApplicator.setBlockLogs(this.blockLogs);
      }

      // Center camera on first snapshot
      if (this.snapshots.length > 0) {
        const first = this.snapshots[0];
        this.orbitTarget.set(first.x, first.y, first.z);
        this._updateOrbitCamera();
      }

      // Show initial player positions
      this._updatePlayersAtTime(0);
      this._needsRender = true;
    }

    async loadChunkData(base64Data) {
      const terrain = window.MX?.terrain;
      if (!this.chunkManager || !terrain?.ChunkDataParser) {
        console.warn('[Replay3D] Terrain engine not available');
        return 0;
      }

      // Progress bar elements
      const progressBar = document.getElementById('r3dProgressBar');
      const progressFill = document.getElementById('r3dProgressFill');
      const loadText = document.getElementById('r3dLoadingText');

      const onProgress = (phase, current, total) => {
        if (!total) return;
        const pct = Math.round((current / total) * 100);
        if (progressBar) progressBar.style.display = '';
        if (phase === 'textures') {
          if (loadText) loadText.textContent = `Loading textures (${current}/${total})...`;
          if (progressFill) progressFill.style.width = `${Math.round(pct * 0.4)}%`;
        } else if (phase === 'meshing') {
          if (loadText) loadText.textContent = `Building terrain (${current}/${total} chunks)...`;
          if (progressFill) progressFill.style.width = `${Math.round(40 + pct * 0.6)}%`;
        }
      };

      try {
        // Try to load texture atlas before building meshes (non-blocking fallback)
        await this._initTextureAtlas(onProgress);

        if (loadText) loadText.textContent = 'Parsing chunk data...';
        console.log('[Replay3D] Parsing chunk data...');
        const columns = await terrain.ChunkDataParser.parse(base64Data);
        console.log(`[Replay3D] Loaded ${columns.length} chunk columns`);

        await this.chunkManager.loadColumns(columns, onProgress);
        this.terrainLoaded = true;
        this._needsRender = true;

        // If no snapshots, center on terrain
        if (this.snapshots.length === 0) {
          const center = this.chunkManager.getCenter();
          this.orbitTarget.set(center.x, center.y + 20, center.z);
          this._updateOrbitCamera();
        }

        // Try to load BlueMap tiles for far terrain
        if (this.blueMapConfig?.available) {
          const center = this.chunkManager.getCenter();
          this._loadBlueMapTiles(center.x, center.z, 256);
        }

        return columns.length;
      } catch (e) {
        console.error('[Replay3D] Failed to load chunk data:', e);
        throw e;
      }
    }

    /**
     * Set BlueMap configuration from server state.
     */
    setBlueMapConfig(config) {
      this.blueMapConfig = config;
      console.log('[Replay3D] BlueMap config:', config?.available ? 'available' : 'not available');
    }

    /**
     * Initialize texture atlas (Mode 2 fallback).
     * Non-blocking - if it fails, terrain renders with vertex colors.
     */
    async _initTextureAtlas(onProgress) {
      const terrain = window.MX?.terrain;
      if (!terrain?.loadTextureAtlas) return;
      if (terrain.TEXTURE_ATLAS?.loaded || terrain.TEXTURE_ATLAS?.loading) return;

      try {
        await terrain.loadTextureAtlas(onProgress);
      } catch (e) {
        console.warn('[Replay3D] Texture atlas failed, using vertex colors:', e.message);
      }
    }

    /**
     * Load BlueMap tiles as textured planes for far-range terrain (Mode 1).
     * Tiles are fetched through the plugin's CORS proxy.
     */
    async _loadBlueMapTiles(centerX, centerZ, radius) {
      if (!this.blueMapConfig?.available || !this.blueMapConfig.mapIds?.length) return;

      const mapId = this.blueMapConfig.mapIds[0]; // Use first available map
      console.log(`[Replay3D] Loading BlueMap tiles for map "${mapId}" around (${centerX}, ${centerZ})...`);

      // BlueMap uses 500x500 block tiles at low-res
      const TILE_SIZE = 500;
      const tileMinX = Math.floor((centerX - radius) / TILE_SIZE);
      const tileMaxX = Math.floor((centerX + radius) / TILE_SIZE);
      const tileMinZ = Math.floor((centerZ - radius) / TILE_SIZE);
      const tileMaxZ = Math.floor((centerZ + radius) / TILE_SIZE);

      // Create group for tiles
      if (this.blueMapTiles) {
        this.scene.remove(this.blueMapTiles);
        this.blueMapTiles.traverse(child => {
          if (child.geometry) child.geometry.dispose();
          if (child.material?.map) child.material.map.dispose();
          if (child.material) child.material.dispose();
        });
      }
      this.blueMapTiles = new THREE.Group();
      this.blueMapTiles.name = 'blueMapTiles';
      this.scene.add(this.blueMapTiles);

      const loader = new THREE.TextureLoader();
      let loadedCount = 0;

      for (let tx = tileMinX; tx <= tileMaxX; tx++) {
        for (let tz = tileMinZ; tz <= tileMaxZ; tz++) {
          try {
            // BlueMap low-res tile path format
            const tilePath = `${mapId}/tiles/0/x${tx}/z${tz}.png`;
            const proxyUrl = `/api/bluemap/tiles/${tilePath}`;

            const texture = await new Promise((resolve, reject) => {
              loader.load(proxyUrl, resolve, undefined, reject);
            });

            texture.magFilter = THREE.NearestFilter;
            texture.minFilter = THREE.LinearFilter;

            const geometry = new THREE.PlaneGeometry(TILE_SIZE, TILE_SIZE);
            const material = new THREE.MeshBasicMaterial({
              map: texture,
              side: THREE.DoubleSide,
              transparent: false,
            });

            const plane = new THREE.Mesh(geometry, material);
            // Position tile flat on the ground at y=62 (sea level)
            plane.rotation.x = -Math.PI / 2;
            plane.position.set(
              tx * TILE_SIZE + TILE_SIZE / 2,
              62, // Sea level
              tz * TILE_SIZE + TILE_SIZE / 2
            );
            plane.renderOrder = -1; // Render behind voxel terrain

            this.blueMapTiles.add(plane);
            loadedCount++;
          } catch (e) {
            // Tile doesn't exist or failed to load - skip silently
          }
        }
      }

      if (loadedCount > 0) {
        console.log(`[Replay3D] Loaded ${loadedCount} BlueMap tiles`);
        this._needsRender = true;
      }
    }

    // ===== CAMERA MODES =====

    setCameraMode(mode) {
      if (this.cameraMode === mode) return;

      // Disable previous
      if (this.cameraMode === 'free' && this.freeCamera) {
        this.freeCamera.disable();
      }

      this.cameraMode = mode;

      if (mode === 'orbit' || mode === 'follow') {
        this._updateOrbitCamera();
      } else if (mode === 'free' && this.freeCamera) {
        this.freeCamera.enable();
      }
      this._needsRender = true;
    }

    getCameraMode() {
      return this.cameraMode;
    }

    // ===== PLAYBACK =====

    play() {
      this.playing = true;
      this._needsRender = true;
    }

    pause() {
      this.playing = false;
    }

    isPlaying() {
      return this.playing;
    }

    togglePlayback() {
      if (this.playing) this.pause();
      else this.play();
      return this.playing;
    }

    seek(timeMs) {
      this.currentTime = Math.max(0, Math.min(this.totalDuration, timeMs));
      this._updatePlayersAtTime(this.currentTime);
      if (this.blockApplicator) {
        this.blockApplicator.seekTo(this.startTime + this.currentTime);
      }
      // Reset sound action tracking to avoid replaying old events
      this._lastSoundActionTime = this.currentTime;
      this._needsRender = true;
    }

    skip(seconds) {
      this.seek(this.currentTime + seconds * 1000);
    }

    setSpeed(speed) {
      this.playbackSpeed = speed;
      if (this.soundManager) {
        this.soundManager.setPlaybackSpeed(speed);
      }
    }

    getCurrentTime() {
      return this.currentTime;
    }

    getTotalDuration() {
      return this.totalDuration;
    }

    // ===== PLAYER MODELS (Minecraft Skin Rendering) =====

    // Skin UV regions: [x, y, width, height] in pixel coords on 64x64 skin
    // Keys use Minecraft wiki naming (right/left/front/back from the player's own perspective).
    // The _applySkinUVs method maps these to Three.js BoxGeometry face indices accounting
    // for the model facing -Z (group rotation PI): +X=left, -X=right, +Z=back, -Z=front.
    static SKIN_UV = {
      head:     { right:[0,8,8,8],   left:[16,8,8,8],  top:[8,0,8,8],   bottom:[16,0,8,8],  front:[8,8,8,8],   back:[24,8,8,8] },
      body:     { right:[16,20,4,12], left:[28,20,4,12], top:[20,16,8,4], bottom:[28,16,8,4], front:[20,20,8,12], back:[32,20,8,12] },
      rightArm: { right:[40,20,4,12], left:[48,20,4,12], top:[44,16,4,4], bottom:[48,16,4,4], front:[44,20,4,12], back:[52,20,4,12] },
      leftArm:  { right:[32,52,4,12], left:[40,52,4,12], top:[36,48,4,4], bottom:[40,48,4,4], front:[36,52,4,12], back:[44,52,4,12] },
      rightLeg: { right:[0,20,4,12],  left:[8,20,4,12],  top:[4,16,4,4],  bottom:[8,16,4,4],  front:[4,20,4,12],  back:[12,20,4,12] },
      leftLeg:  { right:[16,52,4,12], left:[24,52,4,12], top:[20,48,4,4], bottom:[24,48,4,4], front:[20,52,4,12], back:[28,52,4,12] },
      // Overlay layers (hat, jacket, sleeves, pants)
      headOverlay:     { right:[32,8,8,8],   left:[48,8,8,8],  top:[40,0,8,8],   bottom:[48,0,8,8],  front:[40,8,8,8],   back:[56,8,8,8] },
      bodyOverlay:     { right:[16,36,4,12], left:[28,36,4,12], top:[20,32,8,4], bottom:[28,32,8,4], front:[20,36,8,12], back:[32,36,8,12] },
      rightArmOverlay: { right:[40,36,4,12], left:[48,36,4,12], top:[44,32,4,4], bottom:[48,32,4,4], front:[44,36,4,12], back:[52,36,4,12] },
      leftArmOverlay:  { right:[48,52,4,12], left:[56,52,4,12], top:[52,48,4,4], bottom:[56,48,4,4], front:[52,52,4,12], back:[60,52,4,12] },
      rightLegOverlay: { right:[0,36,4,12],  left:[8,36,4,12],  top:[4,32,4,4],  bottom:[8,32,4,4],  front:[4,36,4,12],  back:[12,36,4,12] },
      leftLegOverlay:  { right:[0,52,4,12],  left:[8,52,4,12],  top:[4,48,4,4],  bottom:[8,48,4,4],  front:[4,52,4,12],  back:[12,52,4,12] },
    };

    _applySkinUVs(geometry, partName, skinWidth, skinHeight) {
      const regions = Replay3DViewer.SKIN_UV[partName];
      if (!regions) return;
      const uvAttr = geometry.getAttribute('uv');
      const tw = skinWidth || 64;
      const th = skinHeight || 64;
      // Three.js BoxGeometry face order: +X, -X, +Y, -Y, +Z, -Z
      // When the model faces -Z (via group rotation PI), these map to:
      // +X = player's left, -X = player's right, +Z = player's back, -Z = player's front
      const faceOrder = ['left', 'right', 'top', 'bottom', 'back', 'front'];
      for (let f = 0; f < 6; f++) {
        const [x, y, w, h] = regions[faceOrder[f]];
        const u0 = x / tw, u1 = (x + w) / tw;
        const v0 = 1 - (y + h) / th, v1 = 1 - y / th;
        const base = f * 4;
        uvAttr.setXY(base + 0, u0, v1);
        uvAttr.setXY(base + 1, u1, v1);
        uvAttr.setXY(base + 2, u0, v0);
        uvAttr.setXY(base + 3, u1, v0);
      }
      uvAttr.needsUpdate = true;
    }

    /**
     * Build a Minecraft player model with correct Steve proportions and pivot points.
     *
     * Minecraft Steve dimensions (pixels):
     *   Head  8x8x8   (y = 24..32)
     *   Body  8x12x4  (y = 12..24)
     *   Arms  4x12x4  shoulder at y=24, hanging to y=12
     *   Legs  4x12x4  hip at y=12, feet at y=0
     *   Total height = 32 px = 1.8 blocks (PLAYER_SCALE)
     *
     * Pivot hierarchy:
     *   group                          -- positioned in world, rotated by body yaw
     *     bodyPivot                    -- tilts forward when sneaking
     *       body mesh (y=18*s)
     *       headPivot (y=24*s)         -- independent yaw + pitch
     *         head mesh (y=4*s above pivot)
     *       rightArmPivot (y=24*s)     -- shoulder pivot
     *         rightArm mesh (y=-6*s)
     *       leftArmPivot (y=24*s)
     *         leftArm mesh (y=-6*s)
     *       rightLegPivot (y=12*s)     -- hip pivot
     *         rightLeg mesh (y=-6*s)
     *       leftLegPivot (y=12*s)
     *         leftLeg mesh (y=-6*s)
     *     nameTag sprite
     */
    _getOrCreatePlayer(uuid, name) {
      if (this.players.has(uuid)) return this.players.get(uuid);

      const group = new THREE.Group();
      group.userData = { uuid, name, skinLoaded: false, walkPhase: 0 };

      const color = this.playerColors[this.colorIndex++ % this.playerColors.length];
      const placeholderMat = new THREE.MeshStandardMaterial({ color, roughness: 0.8, metalness: 0.1 });
      const s = PLAYER_SCALE / 32; // 1 pixel = 0.05625 world units

      // Body pivot group - everything except nameTag lives here.
      // Tilts forward for sneaking; pivot point at hip height (y=12*s).
      const bodyPivot = new THREE.Group();
      bodyPivot.name = 'bodyPivot';
      group.add(bodyPivot);

      // Head (8x8x8) - pivot at neck (bottom of head, y=24)
      const headPivot = new THREE.Group();
      headPivot.position.y = 24 * s;
      headPivot.name = 'headPivot';
      const head = new THREE.Mesh(new THREE.BoxGeometry(8*s, 8*s, 8*s), placeholderMat.clone());
      head.position.y = 4 * s; // Center of head is 4px above neck
      head.castShadow = true;
      head.name = 'head';
      headPivot.add(head);
      bodyPivot.add(headPivot);

      // Body (8x12x4) - center at y=18 (12 + 12/2)
      const body = new THREE.Mesh(new THREE.BoxGeometry(8*s, 12*s, 4*s), placeholderMat.clone());
      body.position.y = 18 * s;
      body.castShadow = true;
      body.name = 'body';
      bodyPivot.add(body);

      // Right Arm (4x12x4) - pivot at shoulder (y=24), offset -6px in X
      const rArmPivot = new THREE.Group();
      rArmPivot.position.set(-6*s, 24*s, 0);
      rArmPivot.name = 'rightArmPivot';
      const rArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rArm.position.y = -6 * s; // Hangs 6px below shoulder pivot
      rArm.castShadow = true;
      rArm.name = 'rightArm';
      rArmPivot.add(rArm);
      bodyPivot.add(rArmPivot);

      // Left Arm (4x12x4) - pivot at shoulder (y=24), offset +6px in X
      const lArmPivot = new THREE.Group();
      lArmPivot.position.set(6*s, 24*s, 0);
      lArmPivot.name = 'leftArmPivot';
      const lArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lArm.position.y = -6 * s;
      lArm.castShadow = true;
      lArm.name = 'leftArm';
      lArmPivot.add(lArm);
      bodyPivot.add(lArmPivot);

      // Right Leg (4x12x4) - pivot at hip (y=12), offset -2px in X
      const rLegPivot = new THREE.Group();
      rLegPivot.position.set(-2*s, 12*s, 0);
      rLegPivot.name = 'rightLegPivot';
      const rLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rLeg.position.y = -6 * s; // Hangs 6px below hip pivot
      rLeg.castShadow = true;
      rLeg.name = 'rightLeg';
      rLegPivot.add(rLeg);
      bodyPivot.add(rLegPivot);

      // Left Leg (4x12x4) - pivot at hip (y=12), offset +2px in X
      const lLegPivot = new THREE.Group();
      lLegPivot.position.set(2*s, 12*s, 0);
      lLegPivot.name = 'leftLegPivot';
      const lLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lLeg.position.y = -6 * s;
      lLeg.castShadow = true;
      lLeg.name = 'leftLeg';
      lLegPivot.add(lLeg);
      bodyPivot.add(lLegPivot);

      // Name tag sprite (white text on dark background, Minecraft style)
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 64;
      ctx.fillStyle = 'rgba(0,0,0,0.55)';
      ctx.fillRect(4, 4, 248, 56);
      ctx.font = 'bold 30px "Minecraft", "Courier New", monospace';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillStyle = '#3f3f3f';
      ctx.fillText(name || 'Player', 130, 34);
      ctx.fillStyle = '#ffffff';
      ctx.fillText(name || 'Player', 128, 32);
      const tex = new THREE.CanvasTexture(canvas);
      tex.magFilter = THREE.NearestFilter;
      const sprite = new THREE.Sprite(new THREE.SpriteMaterial({ map: tex, transparent: true }));
      sprite.scale.set(2.5, 0.625, 1);
      sprite.position.y = PLAYER_SCALE + 0.6;
      sprite.name = 'nameTag';
      group.add(sprite); // Directly on group so it doesn't tilt with bodyPivot

      this.players.set(uuid, group);
      this.scene.add(group);

      // Load real Minecraft skin asynchronously (uses cache + fallback proxies)
      this._loadPlayerSkin(uuid, group);

      return group;
    }

    /**
     * Load a Minecraft skin for a player model. Uses the global cached loader
     * with multiple proxy fallbacks.
     */
    _loadPlayerSkin(uuid, group) {
      loadSkinTexture(uuid).then(result => {
        if (!result) {
          console.warn(`[Replay3D] Could not load skin for ${uuid}, keeping fallback color`);
          return;
        }

        const { texture, isLegacy } = result;
        const tw = 64, th = isLegacy ? 32 : 64;

        // Clone the texture for this player so we can dispose independently
        const skinMat = new THREE.MeshStandardMaterial({
          map: texture.clone(),
          roughness: 0.8,
          metalness: 0.1,
        });
        skinMat.map.needsUpdate = true;

        const parts = [
          { name: 'head',     uv: 'head' },
          { name: 'body',     uv: 'body' },
          { name: 'rightArm', uv: 'rightArm' },
          { name: 'leftArm',  uv: isLegacy ? 'rightArm' : 'leftArm' },
          { name: 'rightLeg', uv: 'rightLeg' },
          { name: 'leftLeg',  uv: isLegacy ? 'rightLeg' : 'leftLeg' },
        ];

        for (const { name, uv } of parts) {
          const mesh = group.getObjectByName(name);
          if (!mesh) continue;
          this._applySkinUVs(mesh.geometry, uv, tw, th);
          if (mesh.material) mesh.material.dispose();
          mesh.material = skinMat.clone();
          mesh.material.map.needsUpdate = true;
        }

        // Add overlay layers (hat, jacket, sleeves, pants) if 64x64 skin
        if (!isLegacy) {
          this._addOverlayLayers(group, skinMat, tw, th);
        }

        group.userData.skinLoaded = true;
        this._needsRender = true;
      });
    }

    _addOverlayLayers(group, baseMat, tw, th) {
      const s = PLAYER_SCALE / 32;
      const overlayMat = baseMat.clone();
      overlayMat.transparent = true;
      overlayMat.alphaTest = 0.1;
      overlayMat.side = THREE.DoubleSide;
      overlayMat.depthWrite = false; // Prevent z-fighting with base layer

      // Overlay is 0.5px larger on each side (in skin pixels) for the classic
      // Minecraft hat/jacket puffiness effect
      const inflate = 0.5 * s; // Half a skin pixel in world units
      const overlays = [
        { name: 'headOverlay',     parent: 'head',     size: [8,8,8] },
        { name: 'bodyOverlay',     parent: 'body',     size: [8,12,4] },
        { name: 'rightArmOverlay', parent: 'rightArm', size: [4,12,4] },
        { name: 'leftArmOverlay',  parent: 'leftArm',  size: [4,12,4] },
        { name: 'rightLegOverlay', parent: 'rightLeg', size: [4,12,4] },
        { name: 'leftLegOverlay',  parent: 'leftLeg',  size: [4,12,4] },
      ];

      for (const ol of overlays) {
        const parentMesh = group.getObjectByName(ol.parent);
        if (!parentMesh) continue;
        const [w, h, d] = ol.size;
        // Each dimension gets +1px total (0.5px each side) for overlay inflation
        const geom = new THREE.BoxGeometry(
          w * s + inflate * 2,
          h * s + inflate * 2,
          d * s + inflate * 2
        );
        this._applySkinUVs(geom, ol.name, tw, th);
        const mesh = new THREE.Mesh(geom, overlayMat.clone());
        mesh.name = ol.name;
        mesh.castShadow = false;
        mesh.renderOrder = 1; // Render after base layer
        // Add as child of parent mesh so it inherits position and rotation
        parentMesh.add(mesh);
      }
    }

    /**
     * Binary search: find index of last snapshot with timestamp <= target.
     * Returns -1 if no snapshot is at or before target time.
     */
    _binarySearch(snaps, targetTime) {
      let lo = 0, hi = snaps.length - 1;
      while (lo <= hi) {
        const mid = (lo + hi) >>> 1;
        if (snaps[mid].timestamp <= targetTime) lo = mid + 1;
        else hi = mid - 1;
      }
      return hi;
    }

    _lerp(a, b, t) {
      return a + (b - a) * t;
    }

    _lerpAngle(a, b, t) {
      let diff = b - a;
      while (diff > Math.PI) diff -= Math.PI * 2;
      while (diff < -Math.PI) diff += Math.PI * 2;
      return a + diff * t;
    }

    /**
     * Get action events within a time window (relative ms from replay start).
     */
    getActionsInRange(startMs, endMs) {
      const absStart = this.startTime + startMs;
      const absEnd = this.startTime + endMs;
      const results = [];
      for (const snap of this._actionEvents) {
        if (snap.timestamp > absEnd) break;
        if (snap.timestamp >= absStart) {
          results.push(snap);
        }
      }
      return results;
    }

    /**
     * Apply sneaking pose to a player model.
     * In Minecraft, sneaking lowers the player ~0.3 blocks and tilts the upper body forward.
     * We tilt the bodyPivot around the hip line and shift it down.
     */
    _applySneakPose(group, sneaking) {
      const bodyPivot = group.getObjectByName('bodyPivot');
      if (!bodyPivot) return;

      const s = PLAYER_SCALE / 32;
      if (sneaking) {
        // Minecraft sneaking: body tilts ~30 degrees, eye height drops from 1.62 to 1.27 blocks
        bodyPivot.rotation.x = 0.524; // ~30 degrees
        bodyPivot.position.y = -6.2 * s;
      } else {
        bodyPivot.rotation.x = 0;
        bodyPivot.position.y = 0;
      }
    }

    _updatePlayersAtTime(timeMs) {
      const absoluteTime = this.startTime + timeMs;
      const activeUuids = new Set();
      let movingCount = 0;

      for (const [uuid, snaps] of this._playerSnapshots) {
        const idx = this._binarySearch(snaps, absoluteTime);
        if (idx < 0) continue; // No snapshot at or before this time

        const snap1 = snaps[idx];
        const snap2 = (idx + 1 < snaps.length) ? snaps[idx + 1] : null;

        // Hide player if >2s past their last snapshot with no next one
        if (!snap2 && (absoluteTime - snap1.timestamp) > 2000) continue;

        activeUuids.add(uuid);
        const group = this._getOrCreatePlayer(uuid, snap1.playerName);
        group.visible = true;

        // Get pivot groups for animation
        const bodyPivot = group.getObjectByName('bodyPivot');
        const headPivot = group.getObjectByName('headPivot');
        const rArmPivot = group.getObjectByName('rightArmPivot');
        const lArmPivot = group.getObjectByName('leftArmPivot');
        const rLegPivot = group.getObjectByName('rightLegPivot');
        const lLegPivot = group.getObjectByName('leftLegPivot');

        if (snap2 && snap2.timestamp > snap1.timestamp) {
          // Interpolate between snap1 and snap2
          const t = Math.min(1, (absoluteTime - snap1.timestamp) / (snap2.timestamp - snap1.timestamp));

          // Position lerp
          const x = this._lerp(snap1.x, snap2.x, t);
          const y = this._lerp(snap1.y, snap2.y, t);
          const z = this._lerp(snap1.z, snap2.z, t);
          group.position.set(x, y, z);

          // Body yaw interpolation (shortest path around 360)
          const bodyYaw1 = -snap1.yaw * (Math.PI / 180) + Math.PI;
          const bodyYaw2 = -snap2.yaw * (Math.PI / 180) + Math.PI;
          group.rotation.y = this._lerpAngle(bodyYaw1, bodyYaw2, t);

          // Head pitch (up/down look) interpolated on headPivot.rotation.x
          // Clamp to roughly -90..+90 degrees
          if (headPivot) {
            const pitch1 = THREE.MathUtils.clamp(snap1.pitch, -90, 90) * (Math.PI / 180);
            const pitch2 = THREE.MathUtils.clamp(snap2.pitch, -90, 90) * (Math.PI / 180);
            headPivot.rotation.x = this._lerp(pitch1, pitch2, t);
            // Head yaw is already handled by body rotation since head yaw ~= body yaw
            // in Minecraft snapshot data (head faces same direction as body yaw)
            headPivot.rotation.y = 0;
          }

          // ----- Velocity-based walk animation (Minecraft-accurate) -----
          const dx = snap2.x - snap1.x;
          const dz = snap2.z - snap1.z;
          const dt = (snap2.timestamp - snap1.timestamp) / 1000;
          const speed = Math.sqrt(dx * dx + dz * dz) / Math.max(dt, 0.01);

          const isMoving = speed > 0.5;
          if (isMoving) movingCount++;

          // Minecraft walk speed = 4.317 b/s, sprint = 5.612 b/s
          const walkCycleSpeed = 2.0; // Full cycles per second at walk speed
          const normalizedSpeed = speed / 4.317;
          const cycleFreq = walkCycleSpeed * Math.min(normalizedSpeed, 2.0);

          const phase = absoluteTime * 0.001 * cycleFreq * Math.PI * 2;

          // Minecraft uses ~1 radian (57.3deg) peak amplitude at walk speed
          const maxSwing = isMoving
            ? Math.min(normalizedSpeed, 1.5) * 1.0  // 1.0 radian base
            : 0;
          const swing = Math.sin(phase) * maxSwing;

          // Arms swing opposite to legs
          if (rArmPivot) rArmPivot.rotation.x = swing;
          if (lArmPivot) lArmPivot.rotation.x = -swing;
          if (rLegPivot) rLegPivot.rotation.x = -swing;
          if (lLegPivot) lLegPivot.rotation.x = swing;

          // Attack/swing arm animation
          const snapForAction = t < 0.5 ? snap1 : snap2;
          if (snapForAction.action === 'ATTACK' || snapForAction.action === 'SWING_ARM' || snapForAction.action === 'DAMAGE_DEALT') {
            if (!group.userData.swingStart) group.userData.swingStart = absoluteTime;
          }
          if (group.userData.swingStart) {
            const swingElapsed = absoluteTime - group.userData.swingStart;
            const SWING_DURATION = 250; // ~6 Minecraft ticks
            if (swingElapsed < SWING_DURATION) {
              const st = swingElapsed / SWING_DURATION;
              const swingAngle = Math.sin(st * Math.PI) * -1.5; // ~86deg forward sweep
              if (rArmPivot) rArmPivot.rotation.x = swingAngle;
            } else {
              group.userData.swingStart = null;
            }
          }

          // Sneaking state (use nearer snapshot)
          const isSneaking = (t < 0.5 ? snap1.sneaking : snap2.sneaking);
          this._applySneakPose(group, isSneaking);

          // Equipment (use nearer snapshot)
          const equipSnap = t < 0.5 ? snap1 : snap2;
          this._updatePlayerEquipment(group, equipSnap);

        } else {
          // No interpolation target - hold at snap1
          group.position.set(snap1.x, snap1.y, snap1.z);
          group.rotation.y = -snap1.yaw * (Math.PI / 180) + Math.PI;

          // Head pitch
          if (headPivot) {
            headPivot.rotation.x = THREE.MathUtils.clamp(snap1.pitch, -90, 90) * (Math.PI / 180);
            headPivot.rotation.y = 0;
          }

          // Idle animation - subtle arm bob (~2 degrees, Minecraft-like idle sway)
          const idlePhase = absoluteTime * 0.001 * 0.5 * Math.PI * 2;
          const idleSwing = Math.sin(idlePhase) * 0.035; // ~2 degrees
          if (rArmPivot) { rArmPivot.rotation.x = idleSwing; rArmPivot.rotation.z = Math.sin(idlePhase * 0.7) * 0.02; }
          if (lArmPivot) { lArmPivot.rotation.x = -idleSwing * 0.7; lArmPivot.rotation.z = -Math.sin(idlePhase * 0.7) * 0.02; }
          if (rLegPivot) rLegPivot.rotation.x = 0;
          if (lLegPivot) lLegPivot.rotation.x = 0;

          // Attack swing in idle
          if (snap1.action === 'ATTACK' || snap1.action === 'SWING_ARM' || snap1.action === 'DAMAGE_DEALT') {
            if (!group.userData.swingStart) group.userData.swingStart = absoluteTime;
          }
          if (group.userData.swingStart) {
            const swingElapsed = absoluteTime - group.userData.swingStart;
            if (swingElapsed < 250) {
              const st = swingElapsed / 250;
              if (rArmPivot) rArmPivot.rotation.x = Math.sin(st * Math.PI) * -1.5;
            } else {
              group.userData.swingStart = null;
            }
          }

          // Sneaking
          this._applySneakPose(group, snap1.sneaking);

          // Equipment
          this._updatePlayerEquipment(group, snap1);
        }
      }

      // Hide inactive players
      for (const [uuid, group] of this.players) {
        if (!activeUuids.has(uuid)) {
          group.visible = false;
        }
      }

      // Follow mode - smooth camera tracking
      if (this.cameraMode === 'follow' && activeUuids.size > 0) {
        const primaryUuid = activeUuids.values().next().value;
        const group = this.players.get(primaryUuid);
        if (group) {
          const target = new THREE.Vector3(group.position.x, group.position.y + 1, group.position.z);
          this.orbitTarget.lerp(target, 0.1);
          this._updateOrbitCamera();
        }
      }

      // Track moving player count for footstep sounds
      this._movingPlayerCount = movingCount;
    }

    // ===== EQUIPMENT RENDERING =====

    _updatePlayerEquipment(group, snap) {
      if (!snap) return;
      const s = PLAYER_SCALE / 32;
      const rArmPivot = group.getObjectByName('rightArmPivot');

      // Main hand item - small colored box in right hand
      let heldItem = rArmPivot?.getObjectByName('heldItem');
      if (snap.mainHand) {
        if (!heldItem && rArmPivot) {
          const geo = new THREE.BoxGeometry(2 * s, 10 * s, 2 * s);
          const mat = new THREE.MeshStandardMaterial({ color: 0xcccccc, roughness: 0.6 });
          heldItem = new THREE.Mesh(geo, mat);
          heldItem.name = 'heldItem';
          heldItem.position.set(0, -10 * s, -3 * s);
          heldItem.rotation.x = -Math.PI / 6;
          rArmPivot.add(heldItem);
        }
        if (heldItem) {
          heldItem.visible = true;
          heldItem.material.color.setHex(getItemColor(snap.mainHand));
        }
      } else if (heldItem) {
        heldItem.visible = false;
      }

      // Armor overlays
      if (snap.armor) {
        this._updateArmorVisuals(group, snap.armor);
      }
    }

    _updateArmorVisuals(group, armor) {
      // armor is [boots, leggings, chestplate, helmet]
      if (!armor || !Array.isArray(armor)) return;
      const s = PLAYER_SCALE / 32;
      const bodyPivot = group.getObjectByName('bodyPivot');
      if (!bodyPivot) return;

      const INFLATE = 0.5 * s; // Slight inflation over body part

      // Helmet (index 3) → head
      const helmetData = armor[3];
      let helmet = bodyPivot.getObjectByName('armorHelmet');
      if (helmetData && helmetData.type) {
        if (!helmet) {
          const geo = new THREE.BoxGeometry(8 * s + INFLATE * 2, 8 * s + INFLATE * 2, 8 * s + INFLATE * 2);
          const mat = new THREE.MeshStandardMaterial({ color: 0xCCCCCC, roughness: 0.4, metalness: 0.3, transparent: true, opacity: 0.85 });
          helmet = new THREE.Mesh(geo, mat);
          helmet.name = 'armorHelmet';
          helmet.position.set(0, 24 * s, 0);
          bodyPivot.add(helmet);
        }
        helmet.visible = true;
        const color = getArmorColor(helmetData.type);
        if (color !== null) helmet.material.color.setHex(helmetData.color || color);
      } else if (helmet) {
        helmet.visible = false;
      }

      // Chestplate (index 2) → body + upper arms
      const chestData = armor[2];
      let chest = bodyPivot.getObjectByName('armorChest');
      if (chestData && chestData.type) {
        if (!chest) {
          const geo = new THREE.BoxGeometry(8 * s + INFLATE * 2, 12 * s + INFLATE * 2, 4 * s + INFLATE * 2);
          const mat = new THREE.MeshStandardMaterial({ color: 0xCCCCCC, roughness: 0.4, metalness: 0.3, transparent: true, opacity: 0.85 });
          chest = new THREE.Mesh(geo, mat);
          chest.name = 'armorChest';
          chest.position.set(0, 18 * s, 0);
          bodyPivot.add(chest);
        }
        chest.visible = true;
        const color = getArmorColor(chestData.type);
        if (color !== null) chest.material.color.setHex(chestData.color || color);
      } else if (chest) {
        chest.visible = false;
      }

      // Leggings (index 1) → legs
      const legsData = armor[1];
      let legs = bodyPivot.getObjectByName('armorLegs');
      if (legsData && legsData.type) {
        if (!legs) {
          const geo = new THREE.BoxGeometry(8 * s + INFLATE, 12 * s + INFLATE, 4 * s + INFLATE);
          const mat = new THREE.MeshStandardMaterial({ color: 0xCCCCCC, roughness: 0.4, metalness: 0.3, transparent: true, opacity: 0.85 });
          legs = new THREE.Mesh(geo, mat);
          legs.name = 'armorLegs';
          legs.position.set(0, 6 * s, 0);
          bodyPivot.add(legs);
        }
        legs.visible = true;
        const color = getArmorColor(legsData.type);
        if (color !== null) legs.material.color.setHex(legsData.color || color);
      } else if (legs) {
        legs.visible = false;
      }

      // Boots (index 0) → feet
      const bootsData = armor[0];
      let boots = bodyPivot.getObjectByName('armorBoots');
      if (bootsData && bootsData.type) {
        if (!boots) {
          const geo = new THREE.BoxGeometry(8 * s + INFLATE, 4 * s + INFLATE, 4 * s + INFLATE);
          const mat = new THREE.MeshStandardMaterial({ color: 0xCCCCCC, roughness: 0.4, metalness: 0.3, transparent: true, opacity: 0.85 });
          boots = new THREE.Mesh(geo, mat);
          boots.name = 'armorBoots';
          boots.position.set(0, 0, 0);
          bodyPivot.add(boots);
        }
        boots.visible = true;
        const color = getArmorColor(bootsData.type);
        if (color !== null) boots.material.color.setHex(bootsData.color || color);
      } else if (boots) {
        boots.visible = false;
      }
    }

    // ===== FALLBACK GROUND =====
    // Shown when no terrain data is available

    showFallbackGround() {
      if (this._fallbackGround) return;
      const geo = new THREE.PlaneGeometry(400, 400);
      const mat = new THREE.MeshStandardMaterial({ color: 0x4a7c3f, roughness: 0.9 });
      this._fallbackGround = new THREE.Mesh(geo, mat);
      this._fallbackGround.rotation.x = -Math.PI / 2;
      this._fallbackGround.position.y = 63;
      this._fallbackGround.receiveShadow = true;
      this._fallbackGround.name = 'fallbackGround';
      this.scene.add(this._fallbackGround);

      // Grid
      const grid = new THREE.GridHelper(400, 400, 0x2a5a2a, 0x2a5a2a);
      grid.position.y = 63.01;
      grid.material.opacity = 0.2;
      grid.material.transparent = true;
      grid.name = 'fallbackGrid';
      this._fallbackGrid = grid;
      this.scene.add(grid);
      this._needsRender = true;
    }

    removeFallbackGround() {
      if (this._fallbackGround) {
        this.scene.remove(this._fallbackGround);
        this._fallbackGround.geometry.dispose();
        this._fallbackGround.material.dispose();
        this._fallbackGround = null;
      }
      if (this._fallbackGrid) {
        this.scene.remove(this._fallbackGrid);
        this._fallbackGrid.geometry.dispose();
        this._fallbackGrid.material.dispose();
        this._fallbackGrid = null;
      }
      this._needsRender = true;
    }

    // ===== RENDER LOOP =====

    _animate() {
      this.animationId = requestAnimationFrame(() => this._animate());

      const delta = this.clock.getDelta();

      // Playback tick
      if (this.playing) {
        this.currentTime += delta * 1000 * this.playbackSpeed;

        if (this.currentTime >= this.totalDuration) {
          this.currentTime = this.totalDuration;
          this.playing = false;
          if (this._onPlaybackEnd) this._onPlaybackEnd();
        }

        this._updatePlayersAtTime(this.currentTime);

        // Block changes
        if (this.blockApplicator) {
          this.blockApplicator.applyUpTo(this.startTime + this.currentTime);
        }

        // Time update callback
        if (this._onTimeUpdate) {
          this._onTimeUpdate(this.currentTime, this.totalDuration);
        }

        // Sound: process action events since last sound check
        if (this.soundManager && this.currentTime > this._lastSoundActionTime) {
          const actions = this.getActionsInRange(this._lastSoundActionTime, this.currentTime);
          for (const a of actions) {
            const act = a.action || a.actionType;
            if (act && act !== 'NONE') {
              this.soundManager.playActionSound(act, a.actionData);
            }
          }
          this._lastSoundActionTime = this.currentTime;
        }

        // Sound: footsteps for moving players
        if (this.soundManager && this._movingPlayerCount > 0) {
          let groundBlock = null;
          if (this.chunkManager) {
            // Get ground block under the primary (first) player for material-specific footstep sounds
            for (const [, group] of this.players) {
              if (group.visible) {
                const px = Math.floor(group.position.x);
                const py = Math.floor(group.position.y) - 1;
                const pz = Math.floor(group.position.z);
                groundBlock = this.chunkManager.getBlockAt(px, py, pz);
                break;
              }
            }
          }
          this.soundManager.playFootstep(this.currentTime, groundBlock);
        }

        this._needsRender = true;
      }

      // Free camera
      if (this.cameraMode === 'free' && this.freeCamera) {
        this.freeCamera.update(delta);
        this._needsRender = true; // Free camera always needs render when active
      }

      // Only render if something changed (dirty flag optimization)
      // Always render during dragging since camera is moving
      if (this._needsRender || this.isDragging) {
        // Update frustum for terrain culling
        this._frustumMatrix.multiplyMatrices(
          this.camera.projectionMatrix,
          this.camera.matrixWorldInverse
        );
        this._frustum.setFromProjectionMatrix(this._frustumMatrix);

        // Cull terrain chunks that are outside the frustum
        if (this.chunkManager) {
          this._cullTerrainChunks();
        }

        this.renderer.render(this.scene, this.camera);
        this._needsRender = false;
      }
    }

    /**
     * Frustum-cull terrain chunk groups to avoid rendering off-screen geometry.
     * Each chunk group gets a bounding sphere check against the camera frustum.
     */
    _cullTerrainChunks() {
      if (!this.chunkManager?.columns) return;

      const _box = new THREE.Box3();
      const _sphere = new THREE.Sphere();

      for (const [, col] of this.chunkManager.columns) {
        if (!col.group) continue;

        // Compute a rough bounding box for the chunk column (16x256x16)
        const wx = col.data.chunkX * 16;
        const wz = col.data.chunkZ * 16;
        _box.min.set(wx, -64, wz);
        _box.max.set(wx + 16, 320, wz + 16);
        _box.getBoundingSphere(_sphere);

        col.group.visible = this._frustum.intersectsSphere(_sphere);
      }
    }

    // ===== CALLBACKS =====

    onTimeUpdate(fn) { this._onTimeUpdate = fn; }
    onPlaybackEnd(fn) { this._onPlaybackEnd = fn; }

    // ===== LIFECYCLE =====

    resize() {
      const w = this.container.clientWidth;
      const h = this.container.clientHeight;
      if (w > 0 && h > 0) {
        this.camera.aspect = w / h;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(w, h);
        this._needsRender = true;
      }
    }

    dispose() {
      if (this.animationId) cancelAnimationFrame(this.animationId);
      if (this._resizeObserver) this._resizeObserver.disconnect();
      if (this.freeCamera) this.freeCamera.dispose();
      if (this.chunkManager) this.chunkManager.dispose();

      // Dispose sound manager
      if (this.soundManager) {
        this.soundManager.dispose();
        this.soundManager = null;
      }

      // Dispose BlueMap tiles
      if (this.blueMapTiles) {
        this.scene.remove(this.blueMapTiles);
        this.blueMapTiles.traverse(child => {
          if (child.geometry) child.geometry.dispose();
          if (child.material?.map) child.material.map.dispose();
          if (child.material) child.material.dispose();
        });
        this.blueMapTiles = null;
      }

      // Dispose fallback ground
      this.removeFallbackGround();

      // Dispose all player models and their textures
      this.players.forEach(group => {
        group.traverse(child => {
          if (child.geometry) child.geometry.dispose();
          if (child.material) {
            if (child.material.map) child.material.map.dispose();
            child.material.dispose();
          }
        });
        this.scene.remove(group);
      });
      this.players.clear();

      // Dispose renderer
      if (this.renderer) {
        this.renderer.dispose();
        this.renderer.forceContextLoss();
        if (this.renderer.domElement?.parentNode) {
          this.renderer.domElement.parentNode.removeChild(this.renderer.domElement);
        }
        this.renderer = null;
      }

      // Clear scene references
      this.scene = null;
      this.camera = null;
    }
  }

  // Expose
  window.MX = window.MX || {};
  window.MX.Replay3DViewer = Replay3DViewer;
  window.MX.ReplaySoundManager = ReplaySoundManager;

  console.log('[Replay3D] Module loaded');
})();

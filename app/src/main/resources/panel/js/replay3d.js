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

  const PLAYER_SCALE = 1.8;

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

      try {
        // Try to load texture atlas before building meshes (non-blocking fallback)
        await this._initTextureAtlas();

        console.log('[Replay3D] Parsing chunk data...');
        const columns = await terrain.ChunkDataParser.parse(base64Data);
        console.log(`[Replay3D] Loaded ${columns.length} chunk columns`);

        this.chunkManager.loadColumns(columns);
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
    async _initTextureAtlas() {
      const terrain = window.MX?.terrain;
      if (!terrain?.loadTextureAtlas) return;
      if (terrain.TEXTURE_ATLAS?.loaded || terrain.TEXTURE_ATLAS?.loading) return;

      try {
        await terrain.loadTextureAtlas();
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
      this._needsRender = true;
    }

    skip(seconds) {
      this.seek(this.currentTime + seconds * 1000);
    }

    setSpeed(speed) {
      this.playbackSpeed = speed;
    }

    getCurrentTime() {
      return this.currentTime;
    }

    getTotalDuration() {
      return this.totalDuration;
    }

    // ===== PLAYER MODELS (Minecraft Skin Rendering) =====

    // Skin UV regions: [x, y, width, height] in pixel coords on 64x64 skin
    // Face order matches Three.js BoxGeometry: +x(right), -x(left), +y(top), -y(bottom), +z(front), -z(back)
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
      const faceOrder = ['right', 'left', 'top', 'bottom', 'front', 'back'];
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
     * Build a Minecraft player model with correct pivot points for limb animation.
     * Arms and legs are wrapped in pivot groups so rotation happens at the shoulder/hip
     * rather than at the center of the limb geometry.
     */
    _getOrCreatePlayer(uuid, name) {
      if (this.players.has(uuid)) return this.players.get(uuid);

      const group = new THREE.Group();
      group.userData = { uuid, name, skinLoaded: false };

      const color = this.playerColors[this.colorIndex++ % this.playerColors.length];
      const placeholderMat = new THREE.MeshStandardMaterial({ color, roughness: 0.8, metalness: 0.1 });
      const s = PLAYER_SCALE / 32;

      // Head (8x8x8) - pivot at neck (bottom of head)
      const headPivot = new THREE.Group();
      headPivot.position.y = 24 * s; // Neck position
      headPivot.name = 'headPivot';
      const head = new THREE.Mesh(new THREE.BoxGeometry(8*s, 8*s, 8*s), placeholderMat.clone());
      head.position.y = 4 * s; // Offset so pivot is at bottom of head
      head.castShadow = true;
      head.name = 'head';
      headPivot.add(head);
      group.add(headPivot);

      // Body (8x12x4) - no pivot needed, static
      const body = new THREE.Mesh(new THREE.BoxGeometry(8*s, 12*s, 4*s), placeholderMat.clone());
      body.position.y = 18 * s;
      body.castShadow = true;
      body.name = 'body';
      group.add(body);

      // Right Arm (4x12x4) - pivot at shoulder (top of arm)
      const rArmPivot = new THREE.Group();
      rArmPivot.position.set(-6*s, 24*s, 0); // Shoulder position
      rArmPivot.name = 'rightArmPivot';
      const rArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rArm.position.y = -6 * s; // Offset so pivot is at top
      rArm.castShadow = true;
      rArm.name = 'rightArm';
      rArmPivot.add(rArm);
      group.add(rArmPivot);

      // Left Arm (4x12x4) - pivot at shoulder
      const lArmPivot = new THREE.Group();
      lArmPivot.position.set(6*s, 24*s, 0);
      lArmPivot.name = 'leftArmPivot';
      const lArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lArm.position.y = -6 * s;
      lArm.castShadow = true;
      lArm.name = 'leftArm';
      lArmPivot.add(lArm);
      group.add(lArmPivot);

      // Right Leg (4x12x4) - pivot at hip (top of leg)
      const rLegPivot = new THREE.Group();
      rLegPivot.position.set(-2*s, 12*s, 0); // Hip position
      rLegPivot.name = 'rightLegPivot';
      const rLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rLeg.position.y = -6 * s; // Offset so pivot is at top
      rLeg.castShadow = true;
      rLeg.name = 'rightLeg';
      rLegPivot.add(rLeg);
      group.add(rLegPivot);

      // Left Leg (4x12x4) - pivot at hip
      const lLegPivot = new THREE.Group();
      lLegPivot.position.set(2*s, 12*s, 0);
      lLegPivot.name = 'leftLegPivot';
      const lLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lLeg.position.y = -6 * s;
      lLeg.castShadow = true;
      lLeg.name = 'leftLeg';
      lLegPivot.add(lLeg);
      group.add(lLegPivot);

      // Name tag sprite (white text, Minecraft style)
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 64;
      ctx.fillStyle = 'rgba(0,0,0,0.55)';
      ctx.fillRect(4, 4, 248, 56);
      ctx.font = 'bold 30px "Minecraft", "Courier New", monospace';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      // White text with dark shadow like Minecraft
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
      group.add(sprite);

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
      overlayMat.alphaTest = 0.01;
      overlayMat.side = THREE.DoubleSide;

      const overlayScale = 1.1; // Slightly larger than base layer
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
        const geom = new THREE.BoxGeometry(w*s*overlayScale, h*s*overlayScale, d*s*overlayScale);
        this._applySkinUVs(geom, ol.name, tw, th);
        const mesh = new THREE.Mesh(geom, overlayMat.clone());
        mesh.name = ol.name;
        mesh.castShadow = false;
        // Add as child of parent mesh so it rotates with it (head pitch, arm/leg swing)
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

    _updatePlayersAtTime(timeMs) {
      const absoluteTime = this.startTime + timeMs;
      const activeUuids = new Set();

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

        // Get pivot groups for limb animation
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

          // Yaw interpolation (shortest path around 360)
          const yawRad1 = -snap1.yaw * (Math.PI / 180) + Math.PI;
          const yawRad2 = -snap2.yaw * (Math.PI / 180) + Math.PI;
          group.rotation.y = this._lerpAngle(yawRad1, yawRad2, t);

          // Head pitch interpolation (on pivot group)
          if (headPivot) {
            const pitchRad1 = snap1.pitch * (Math.PI / 180);
            const pitchRad2 = snap2.pitch * (Math.PI / 180);
            headPivot.rotation.x = this._lerp(pitchRad1, pitchRad2, t);
          }

          // Velocity-based walk animation
          const dx = snap2.x - snap1.x;
          const dz = snap2.z - snap1.z;
          const dt = (snap2.timestamp - snap1.timestamp) / 1000;
          const speed = Math.sqrt(dx * dx + dz * dz) / Math.max(dt, 0.01);

          const isMoving = speed > 0.5;
          const animTime = absoluteTime * 0.006;
          const swingFreq = Math.min(speed * 0.8, 4);
          const swing = isMoving ? Math.sin(animTime * swingFreq) * Math.min(speed * 0.15, 0.7) : 0;

          // Animate pivot groups (rotation at shoulder/hip)
          if (rArmPivot) rArmPivot.rotation.x = swing;
          if (lArmPivot) lArmPivot.rotation.x = -swing;
          if (rLegPivot) rLegPivot.rotation.x = -swing;
          if (lLegPivot) lLegPivot.rotation.x = swing;

          // Sneaking state (use nearer snapshot)
          group.scale.y = (t < 0.5 ? snap1.sneaking : snap2.sneaking) ? 0.85 : 1;

        } else {
          // No interpolation target - hold at snap1
          group.position.set(snap1.x, snap1.y, snap1.z);
          group.rotation.y = -snap1.yaw * (Math.PI / 180) + Math.PI;

          if (headPivot) headPivot.rotation.x = snap1.pitch * (Math.PI / 180);

          // Static pose (no walking)
          if (rArmPivot) rArmPivot.rotation.x = 0;
          if (lArmPivot) lArmPivot.rotation.x = 0;
          if (rLegPivot) rLegPivot.rotation.x = 0;
          if (lLegPivot) lLegPivot.rotation.x = 0;

          group.scale.y = snap1.sneaking ? 0.85 : 1;
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

  console.log('[Replay3D] Module loaded');
})();

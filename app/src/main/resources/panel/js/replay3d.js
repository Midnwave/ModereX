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

      // Callbacks
      this._onTimeUpdate = null;
      this._onPlaybackEnd = null;

      // Animation
      this.animationId = null;

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
      this.renderer = new THREE.WebGLRenderer({ antialias: true });
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
    }

    getCameraMode() {
      return this.cameraMode;
    }

    // ===== PLAYBACK =====

    play() {
      this.playing = true;
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

    _getOrCreatePlayer(uuid, name) {
      if (this.players.has(uuid)) return this.players.get(uuid);

      const group = new THREE.Group();
      group.userData = { uuid, name };

      const color = this.playerColors[this.colorIndex++ % this.playerColors.length];
      const placeholderMat = new THREE.MeshStandardMaterial({ color, roughness: 0.8, metalness: 0.1 });
      const s = PLAYER_SCALE / 32;

      // Head (8x8x8)
      const head = new THREE.Mesh(new THREE.BoxGeometry(8*s, 8*s, 8*s), placeholderMat.clone());
      head.position.y = 28 * s;
      head.castShadow = true;
      head.name = 'head';
      group.add(head);

      // Body (8x12x4)
      const body = new THREE.Mesh(new THREE.BoxGeometry(8*s, 12*s, 4*s), placeholderMat.clone());
      body.position.y = 18 * s;
      body.castShadow = true;
      body.name = 'body';
      group.add(body);

      // Right Arm (4x12x4)
      const rArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rArm.position.set(-6*s, 18*s, 0);
      rArm.castShadow = true;
      rArm.name = 'rightArm';
      group.add(rArm);

      // Left Arm (4x12x4)
      const lArm = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lArm.position.set(6*s, 18*s, 0);
      lArm.castShadow = true;
      lArm.name = 'leftArm';
      group.add(lArm);

      // Right Leg (4x12x4)
      const rLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      rLeg.position.set(-2*s, 6*s, 0);
      rLeg.castShadow = true;
      rLeg.name = 'rightLeg';
      group.add(rLeg);

      // Left Leg (4x12x4)
      const lLeg = new THREE.Mesh(new THREE.BoxGeometry(4*s, 12*s, 4*s), placeholderMat.clone());
      lLeg.position.set(2*s, 6*s, 0);
      lLeg.castShadow = true;
      lLeg.name = 'leftLeg';
      group.add(lLeg);

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

      // Load real Minecraft skin asynchronously
      this._loadPlayerSkin(uuid, group);

      return group;
    }

    _loadPlayerSkin(uuid, group) {
      // Crafatar API: free, CORS-enabled, returns Minecraft skin PNGs
      const skinUrl = `https://crafatar.com/skins/${uuid}?default=MHF_Steve`;

      const img = new Image();
      img.crossOrigin = 'anonymous';
      img.onload = () => {
        const texture = new THREE.Texture(img);
        texture.magFilter = THREE.NearestFilter;
        texture.minFilter = THREE.NearestFilter;
        texture.needsUpdate = true;

        const skinMat = new THREE.MeshStandardMaterial({
          map: texture, roughness: 0.8, metalness: 0.1
        });

        const isLegacy = img.height === 32; // 64x32 = old skin format
        const tw = 64, th = isLegacy ? 32 : 64;

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
          mesh.material.dispose();
          mesh.material = skinMat.clone();
        }

        // Add overlay layers (hat, jacket, sleeves, pants) if 64x64 skin
        if (!isLegacy) {
          this._addOverlayLayers(group, skinMat, tw, th);
        }
      };
      img.onerror = () => {
        console.warn(`[Replay3D] Could not load skin for ${uuid}, keeping fallback color`);
      };
      img.src = skinUrl;
    }

    _addOverlayLayers(group, baseMat, tw, th) {
      const s = PLAYER_SCALE / 32;
      const overlayMat = baseMat.clone();
      overlayMat.transparent = true;
      overlayMat.alphaTest = 0.01;
      overlayMat.side = THREE.DoubleSide;

      const overlayScale = 1.1; // Slightly larger than base layer
      const overlays = [
        { name: 'headOverlay',     parent: 'head',     size: [8,8,8],   pos: null },
        { name: 'bodyOverlay',     parent: 'body',     size: [8,12,4],  pos: null },
        { name: 'rightArmOverlay', parent: 'rightArm', size: [4,12,4],  pos: null },
        { name: 'leftArmOverlay',  parent: 'leftArm',  size: [4,12,4],  pos: null },
        { name: 'rightLegOverlay', parent: 'rightLeg', size: [4,12,4],  pos: null },
        { name: 'leftLegOverlay',  parent: 'leftLeg',  size: [4,12,4],  pos: null },
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

    _updatePlayersAtTime(timeMs) {
      const absoluteTime = this.startTime + timeMs;
      const currentPositions = new Map();

      for (const snap of this.snapshots) {
        if (snap.timestamp <= absoluteTime) {
          currentPositions.set(snap.playerUuid, snap);
        }
      }

      // Hide all, then show matching
      for (const [uuid, group] of this.players) {
        group.visible = currentPositions.has(uuid);
      }

      for (const [uuid, snap] of currentPositions) {
        const group = this._getOrCreatePlayer(uuid, snap.playerName);
        group.visible = true;
        group.position.set(snap.x, snap.y, snap.z);
        group.rotation.y = -snap.yaw * (Math.PI / 180) + Math.PI;

        // Head pitch
        const head = group.getObjectByName('head');
        if (head) head.rotation.x = snap.pitch * (Math.PI / 180);

        // Walk animation
        const time = Date.now() * 0.003;
        const walking = snap.sprinting || (!snap.sneaking && snap.onGround);
        const speed = snap.sprinting ? 2 : 1;
        const swing = walking ? Math.sin(time * speed) * 0.5 : 0;

        const rArm = group.getObjectByName('rightArm');
        const lArm = group.getObjectByName('leftArm');
        const rLeg = group.getObjectByName('rightLeg');
        const lLeg = group.getObjectByName('leftLeg');
        if (rArm) rArm.rotation.x = swing;
        if (lArm) lArm.rotation.x = -swing;
        if (rLeg) rLeg.rotation.x = -swing;
        if (lLeg) lLeg.rotation.x = swing;

        // Sneaking
        if (snap.sneaking) {
          group.scale.y = 0.85;
        } else {
          group.scale.y = 1;
        }
      }

      // Follow mode
      if (this.cameraMode === 'follow' && currentPositions.size > 0) {
        const primary = currentPositions.values().next().value;
        this.orbitTarget.set(primary.x, primary.y + 1, primary.z);
        this._updateOrbitCamera();
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
      }

      // Free camera
      if (this.cameraMode === 'free' && this.freeCamera) {
        this.freeCamera.update(delta);
      }

      this.renderer.render(this.scene, this.camera);
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
      }
    }

    dispose() {
      if (this.animationId) cancelAnimationFrame(this.animationId);
      if (this._resizeObserver) this._resizeObserver.disconnect();
      if (this.freeCamera) this.freeCamera.dispose();
      if (this.chunkManager) this.chunkManager.dispose();
      if (this.blueMapTiles) {
        this.scene.remove(this.blueMapTiles);
        this.blueMapTiles.traverse(child => {
          if (child.geometry) child.geometry.dispose();
          if (child.material?.map) child.material.map.dispose();
          if (child.material) child.material.dispose();
        });
      }

      this.players.forEach(group => {
        group.traverse(child => {
          if (child.geometry) child.geometry.dispose();
          if (child.material) {
            if (child.material.map) child.material.map.dispose();
            child.material.dispose();
          }
        });
      });
      this.players.clear();

      if (this.renderer) {
        this.renderer.dispose();
        if (this.renderer.domElement?.parentNode) {
          this.renderer.domElement.parentNode.removeChild(this.renderer.domElement);
        }
      }
    }
  }

  // Expose
  window.MX = window.MX || {};
  window.MX.Replay3DViewer = Replay3DViewer;

  console.log('[Replay3D] Module loaded');
})();

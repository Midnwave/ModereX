/* ============================================
   ModereX Control Panel - 3D Replay Viewer
   ============================================
   Three.js based 3D replay viewer with Minecraft player models
   */

(function() {
  'use strict';

  // Only initialize if Three.js is available
  if (typeof THREE === 'undefined') {
    console.warn('[ModereX Replay3D] Three.js not loaded, 3D viewer disabled');
    return;
  }

  // ===== CONSTANTS =====
  const PLAYER_SCALE = 1.8; // Minecraft player height in blocks
  const SKIN_API = 'https://crafatar.com/skins/';
  const AVATAR_API = 'https://mc-heads.net/avatar/';

  // ===== 3D REPLAY RENDERER =====
  class Replay3DRenderer {
    constructor(container) {
      this.container = container;
      this.scene = null;
      this.camera = null;
      this.renderer = null;
      this.players = new Map(); // uuid -> player mesh group
      this.playerColors = {};
      this.colorPalette = [
        0x5a9cff, 0xff6b6b, 0x51cf66, 0xffc078, 0xcc5de8,
        0x20c997, 0xff8787, 0x748ffc, 0xffd43b, 0x69db7c
      ];

      // Camera controls
      this.cameraTarget = new THREE.Vector3(0, 1, 0);
      this.cameraDistance = 30;
      this.cameraAngleX = Math.PI / 6; // Vertical angle
      this.cameraAngleY = Math.PI / 4; // Horizontal angle
      this.isDragging = false;
      this.lastMouseX = 0;
      this.lastMouseY = 0;

      // Animation
      this.animationId = null;
      this.lastRenderTime = 0;

      this.init();
    }

    init() {
      // Create scene
      this.scene = new THREE.Scene();
      this.scene.background = new THREE.Color(0x0a1018);
      this.scene.fog = new THREE.Fog(0x0a1018, 50, 150);

      // Create camera
      this.camera = new THREE.PerspectiveCamera(
        60, // FOV
        this.container.clientWidth / this.container.clientHeight,
        0.1,
        1000
      );
      this.updateCameraPosition();

      // Create renderer
      this.renderer = new THREE.WebGLRenderer({
        antialias: true,
        alpha: true
      });
      this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
      this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      this.renderer.shadowMap.enabled = true;
      this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
      this.container.appendChild(this.renderer.domElement);

      // Add lights
      this.setupLights();

      // Add ground
      this.setupGround();

      // Add grid helper
      this.setupGrid();

      // Setup controls
      this.setupControls();

      // Handle resize
      window.addEventListener('resize', () => this.resize());

      // Start render loop
      this.animate();
    }

    setupLights() {
      // Ambient light
      const ambient = new THREE.AmbientLight(0x404060, 0.6);
      this.scene.add(ambient);

      // Main directional light (sun)
      const sun = new THREE.DirectionalLight(0xffffff, 1.0);
      sun.position.set(50, 100, 50);
      sun.castShadow = true;
      sun.shadow.mapSize.width = 2048;
      sun.shadow.mapSize.height = 2048;
      sun.shadow.camera.near = 10;
      sun.shadow.camera.far = 200;
      sun.shadow.camera.left = -50;
      sun.shadow.camera.right = 50;
      sun.shadow.camera.top = 50;
      sun.shadow.camera.bottom = -50;
      this.scene.add(sun);

      // Fill light
      const fill = new THREE.DirectionalLight(0x6080ff, 0.3);
      fill.position.set(-30, 30, -30);
      this.scene.add(fill);

      // Hemisphere light for sky/ground color
      const hemi = new THREE.HemisphereLight(0x87ceeb, 0x3d5c35, 0.4);
      this.scene.add(hemi);
    }

    setupGround() {
      // Create grass-colored ground plane
      const groundGeometry = new THREE.PlaneGeometry(200, 200);
      const groundMaterial = new THREE.MeshStandardMaterial({
        color: 0x4a7c3f,
        roughness: 0.9,
        metalness: 0.0
      });
      const ground = new THREE.Mesh(groundGeometry, groundMaterial);
      ground.rotation.x = -Math.PI / 2;
      ground.position.y = 0;
      ground.receiveShadow = true;
      this.scene.add(ground);

      // Add some grass variation with a darker layer
      const grassGeometry = new THREE.PlaneGeometry(200, 200);
      const grassMaterial = new THREE.MeshStandardMaterial({
        color: 0x5b8c4a,
        roughness: 1.0,
        metalness: 0.0,
        transparent: true,
        opacity: 0.5
      });
      const grass = new THREE.Mesh(grassGeometry, grassMaterial);
      grass.rotation.x = -Math.PI / 2;
      grass.position.y = 0.01;
      this.scene.add(grass);
    }

    setupGrid() {
      // Add a subtle grid
      const gridHelper = new THREE.GridHelper(100, 100, 0x1a3a2a, 0x1a3a2a);
      gridHelper.position.y = 0.02;
      gridHelper.material.opacity = 0.3;
      gridHelper.material.transparent = true;
      this.scene.add(gridHelper);
    }

    setupControls() {
      const canvas = this.renderer.domElement;

      // Mouse drag for rotation
      canvas.addEventListener('mousedown', (e) => {
        if (e.button === 0 || e.button === 2) {
          this.isDragging = true;
          this.lastMouseX = e.clientX;
          this.lastMouseY = e.clientY;
        }
      });

      canvas.addEventListener('mousemove', (e) => {
        if (this.isDragging) {
          const deltaX = e.clientX - this.lastMouseX;
          const deltaY = e.clientY - this.lastMouseY;

          this.cameraAngleY += deltaX * 0.01;
          this.cameraAngleX = Math.max(0.1, Math.min(Math.PI / 2 - 0.1,
            this.cameraAngleX + deltaY * 0.01));

          this.updateCameraPosition();
          this.lastMouseX = e.clientX;
          this.lastMouseY = e.clientY;
        }
      });

      canvas.addEventListener('mouseup', () => {
        this.isDragging = false;
      });

      canvas.addEventListener('mouseleave', () => {
        this.isDragging = false;
      });

      // Wheel for zoom
      canvas.addEventListener('wheel', (e) => {
        e.preventDefault();
        this.cameraDistance = Math.max(5, Math.min(100,
          this.cameraDistance + e.deltaY * 0.05));
        this.updateCameraPosition();
      });

      // Touch support
      let lastTouchDistance = 0;
      let lastTouchX = 0;
      let lastTouchY = 0;

      canvas.addEventListener('touchstart', (e) => {
        if (e.touches.length === 1) {
          this.isDragging = true;
          lastTouchX = e.touches[0].clientX;
          lastTouchY = e.touches[0].clientY;
        } else if (e.touches.length === 2) {
          const dx = e.touches[0].clientX - e.touches[1].clientX;
          const dy = e.touches[0].clientY - e.touches[1].clientY;
          lastTouchDistance = Math.sqrt(dx * dx + dy * dy);
        }
      });

      canvas.addEventListener('touchmove', (e) => {
        e.preventDefault();
        if (e.touches.length === 1 && this.isDragging) {
          const deltaX = e.touches[0].clientX - lastTouchX;
          const deltaY = e.touches[0].clientY - lastTouchY;

          this.cameraAngleY += deltaX * 0.01;
          this.cameraAngleX = Math.max(0.1, Math.min(Math.PI / 2 - 0.1,
            this.cameraAngleX + deltaY * 0.01));

          this.updateCameraPosition();
          lastTouchX = e.touches[0].clientX;
          lastTouchY = e.touches[0].clientY;
        } else if (e.touches.length === 2) {
          const dx = e.touches[0].clientX - e.touches[1].clientX;
          const dy = e.touches[0].clientY - e.touches[1].clientY;
          const distance = Math.sqrt(dx * dx + dy * dy);

          if (lastTouchDistance > 0) {
            const delta = lastTouchDistance - distance;
            this.cameraDistance = Math.max(5, Math.min(100,
              this.cameraDistance + delta * 0.1));
            this.updateCameraPosition();
          }
          lastTouchDistance = distance;
        }
      }, { passive: false });

      canvas.addEventListener('touchend', () => {
        this.isDragging = false;
        lastTouchDistance = 0;
      });

      // Prevent context menu
      canvas.addEventListener('contextmenu', (e) => e.preventDefault());
    }

    updateCameraPosition() {
      const x = this.cameraTarget.x + this.cameraDistance * Math.cos(this.cameraAngleX) * Math.sin(this.cameraAngleY);
      const y = this.cameraTarget.y + this.cameraDistance * Math.sin(this.cameraAngleX);
      const z = this.cameraTarget.z + this.cameraDistance * Math.cos(this.cameraAngleX) * Math.cos(this.cameraAngleY);

      this.camera.position.set(x, y, z);
      this.camera.lookAt(this.cameraTarget);
    }

    // Create a Minecraft player model
    createPlayerModel(uuid, name) {
      const group = new THREE.Group();
      group.userData = { uuid, name };

      // Get player color
      const colorIndex = this.players.size % this.colorPalette.length;
      const color = this.colorPalette[colorIndex];

      // Create materials (will be updated with skin texture later)
      const skinMaterial = new THREE.MeshStandardMaterial({
        color: color,
        roughness: 0.8,
        metalness: 0.1
      });

      // Scale factor (1 unit = 1 block, player is ~1.8 blocks tall)
      const s = PLAYER_SCALE / 32; // Minecraft model is 32 pixels tall

      // Head (8x8x8 pixels)
      const headGeometry = new THREE.BoxGeometry(8 * s, 8 * s, 8 * s);
      const head = new THREE.Mesh(headGeometry, skinMaterial.clone());
      head.position.y = 24 * s + 4 * s; // On top of body
      head.castShadow = true;
      head.name = 'head';
      group.add(head);

      // Body (8x12x4 pixels)
      const bodyGeometry = new THREE.BoxGeometry(8 * s, 12 * s, 4 * s);
      const body = new THREE.Mesh(bodyGeometry, skinMaterial.clone());
      body.position.y = 18 * s; // Center of body
      body.castShadow = true;
      body.name = 'body';
      group.add(body);

      // Right Arm (4x12x4 pixels)
      const rightArmGeometry = new THREE.BoxGeometry(4 * s, 12 * s, 4 * s);
      const rightArm = new THREE.Mesh(rightArmGeometry, skinMaterial.clone());
      rightArm.position.set(-6 * s, 18 * s, 0);
      rightArm.castShadow = true;
      rightArm.name = 'rightArm';
      group.add(rightArm);

      // Left Arm (4x12x4 pixels)
      const leftArmGeometry = new THREE.BoxGeometry(4 * s, 12 * s, 4 * s);
      const leftArm = new THREE.Mesh(leftArmGeometry, skinMaterial.clone());
      leftArm.position.set(6 * s, 18 * s, 0);
      leftArm.castShadow = true;
      leftArm.name = 'leftArm';
      group.add(leftArm);

      // Right Leg (4x12x4 pixels)
      const rightLegGeometry = new THREE.BoxGeometry(4 * s, 12 * s, 4 * s);
      const rightLeg = new THREE.Mesh(rightLegGeometry, skinMaterial.clone());
      rightLeg.position.set(-2 * s, 6 * s, 0);
      rightLeg.castShadow = true;
      rightLeg.name = 'rightLeg';
      group.add(rightLeg);

      // Left Leg (4x12x4 pixels)
      const leftLegGeometry = new THREE.BoxGeometry(4 * s, 12 * s, 4 * s);
      const leftLeg = new THREE.Mesh(leftLegGeometry, skinMaterial.clone());
      leftLeg.position.set(2 * s, 6 * s, 0);
      leftLeg.castShadow = true;
      leftLeg.name = 'leftLeg';
      group.add(leftLeg);

      // Name tag
      this.addNameTag(group, name, color);

      // Load skin texture
      this.loadPlayerSkin(group, uuid);

      return group;
    }

    addNameTag(group, name, color) {
      // Create a sprite for the name tag
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 64;

      // Background
      ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
      ctx.roundRect(0, 0, canvas.width, canvas.height, 8);
      ctx.fill();

      // Text
      ctx.font = 'bold 28px "Plus Jakarta Sans", sans-serif';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillStyle = '#' + color.toString(16).padStart(6, '0');
      ctx.fillText(name, canvas.width / 2, canvas.height / 2);

      // Create sprite
      const texture = new THREE.CanvasTexture(canvas);
      const spriteMaterial = new THREE.SpriteMaterial({
        map: texture,
        transparent: true
      });
      const sprite = new THREE.Sprite(spriteMaterial);
      sprite.scale.set(2, 0.5, 1);
      sprite.position.y = PLAYER_SCALE + 0.5;
      sprite.name = 'nameTag';
      group.add(sprite);
    }

    loadPlayerSkin(group, uuid) {
      // Use Crafatar for skins - note: CORS might be an issue
      // For now, we'll use the colored model
      // In a production environment, you'd proxy this through your server

      const loader = new THREE.TextureLoader();
      loader.crossOrigin = 'anonymous';

      // Try to load skin (this may fail due to CORS)
      const skinUrl = `https://crafatar.com/skins/${uuid}`;

      loader.load(
        skinUrl,
        (texture) => {
          texture.magFilter = THREE.NearestFilter;
          texture.minFilter = THREE.NearestFilter;

          // Apply skin to body parts
          group.children.forEach(child => {
            if (child.isMesh) {
              child.material.map = texture;
              child.material.needsUpdate = true;
            }
          });
        },
        undefined,
        (error) => {
          // Skin loading failed, keep the colored model
          console.log('[Replay3D] Could not load skin for', uuid);
        }
      );
    }

    // Update player position and rotation
    updatePlayer(uuid, name, x, y, z, yaw, pitch, state = {}) {
      let playerGroup = this.players.get(uuid);

      if (!playerGroup) {
        playerGroup = this.createPlayerModel(uuid, name);
        this.players.set(uuid, playerGroup);
        this.scene.add(playerGroup);
      }

      // Update position (Y is up in Three.js)
      playerGroup.position.set(x, y, z);

      // Update rotation (Minecraft yaw: 0 = South, 90 = West, etc.)
      playerGroup.rotation.y = -yaw * (Math.PI / 180) + Math.PI;

      // Animate based on state
      const time = Date.now() * 0.003;
      const head = playerGroup.getObjectByName('head');
      const rightArm = playerGroup.getObjectByName('rightArm');
      const leftArm = playerGroup.getObjectByName('leftArm');
      const rightLeg = playerGroup.getObjectByName('rightLeg');
      const leftLeg = playerGroup.getObjectByName('leftLeg');

      // Head pitch
      if (head) {
        head.rotation.x = pitch * (Math.PI / 180);
      }

      // Walking animation
      if (state.sprinting || state.walking) {
        const speed = state.sprinting ? 2 : 1;
        const swing = Math.sin(time * speed) * 0.5;

        if (rightArm) rightArm.rotation.x = swing;
        if (leftArm) leftArm.rotation.x = -swing;
        if (rightLeg) rightLeg.rotation.x = -swing;
        if (leftLeg) leftLeg.rotation.x = swing;
      } else {
        // Reset to idle
        if (rightArm) rightArm.rotation.x = 0;
        if (leftArm) leftArm.rotation.x = 0;
        if (rightLeg) rightLeg.rotation.x = 0;
        if (leftLeg) leftLeg.rotation.x = 0;
      }

      // Sneaking - lower the model
      if (state.sneaking) {
        playerGroup.scale.y = 0.85;
        playerGroup.position.y = y - 0.15;
      } else {
        playerGroup.scale.y = 1;
      }
    }

    // Remove a player from the scene
    removePlayer(uuid) {
      const playerGroup = this.players.get(uuid);
      if (playerGroup) {
        this.scene.remove(playerGroup);
        this.players.delete(uuid);
      }
    }

    // Clear all players
    clearPlayers() {
      this.players.forEach((group, uuid) => {
        this.scene.remove(group);
      });
      this.players.clear();
    }

    // Focus camera on position
    focusOn(x, y, z) {
      this.cameraTarget.set(x, y + 1, z);
      this.updateCameraPosition();
    }

    // Resize handler
    resize() {
      const width = this.container.clientWidth;
      const height = this.container.clientHeight;

      if (width > 0 && height > 0) {
        this.camera.aspect = width / height;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(width, height);
      }
    }

    // Animation loop
    animate() {
      this.animationId = requestAnimationFrame(() => this.animate());
      this.renderer.render(this.scene, this.camera);
    }

    // Cleanup
    dispose() {
      if (this.animationId) {
        cancelAnimationFrame(this.animationId);
      }

      this.clearPlayers();

      if (this.renderer) {
        this.renderer.dispose();
        if (this.renderer.domElement && this.renderer.domElement.parentNode) {
          this.renderer.domElement.parentNode.removeChild(this.renderer.domElement);
        }
      }
    }
  }

  // ===== INTEGRATION WITH REPLAY SYSTEM =====
  let renderer3D = null;

  // Initialize 3D renderer when replay page is shown
  function init3DReplay() {
    const container = document.getElementById('replayCanvasContainer');
    const canvas2d = document.getElementById('replayCanvas');

    if (!container) return;

    // Hide the 2D canvas
    if (canvas2d) {
      canvas2d.style.display = 'none';
    }

    // Create 3D renderer if not already created
    if (!renderer3D) {
      renderer3D = new Replay3DRenderer(container);
      console.log('[ModereX Replay3D] 3D renderer initialized');
    }
  }

  // Update players from replay data
  function update3DPlayers(snapshots, currentTime, startTime) {
    if (!renderer3D) return;

    // Find current position for each player at this time
    const playerPositions = new Map();

    for (const snapshot of snapshots) {
      const relativeTime = snapshot.timestamp - startTime;
      if (relativeTime <= currentTime) {
        playerPositions.set(snapshot.playerUuid, snapshot);
      }
    }

    // Update each player
    playerPositions.forEach((snap, uuid) => {
      const state = {
        sneaking: snap.sneaking,
        sprinting: snap.sprinting,
        swimming: snap.swimming,
        gliding: snap.gliding,
        walking: !snap.sneaking && !snap.sprinting && snap.onGround
      };

      renderer3D.updatePlayer(
        uuid,
        snap.playerName,
        snap.x,
        snap.y - 64, // Adjust Y relative to ground level
        snap.z,
        snap.yaw,
        snap.pitch,
        state
      );
    });

    // Focus on first player if available
    if (playerPositions.size > 0) {
      const firstPlayer = playerPositions.values().next().value;
      renderer3D.focusOn(firstPlayer.x, firstPlayer.y - 64, firstPlayer.z);
    }
  }

  // Clear 3D scene
  function clear3DScene() {
    if (renderer3D) {
      renderer3D.clearPlayers();
    }
  }

  // Resize 3D renderer
  function resize3D() {
    if (renderer3D) {
      renderer3D.resize();
    }
  }

  // Expose API
  window.MX = window.MX || {};
  window.MX.replay3D = {
    init: init3DReplay,
    updatePlayers: update3DPlayers,
    clear: clear3DScene,
    resize: resize3D,
    getRenderer: () => renderer3D
  };

  console.log('[ModereX Replay3D] Module loaded');
})();

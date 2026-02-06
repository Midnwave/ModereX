/* ============================================
   ModereX Control Panel - 3D Terrain Renderer
   ============================================
   Voxel terrain rendering engine for the replay viewer.
   Parses chunk data from the server and renders Minecraft
   terrain using Three.js with greedy meshing optimization.
*/

(function() {
  'use strict';

  // ===== BLOCK REGISTRY =====
  // Maps Minecraft block state strings to rendering properties.
  // color: hex color, topColor: optional different top face color,
  // opacity: 0-1, shape: 'full'|'slab_bottom'|'slab_top'|'stairs'|'cross'|'fluid'|'thin'

  const BLOCK_COLORS = {
    // Stone variants
    'minecraft:stone':              { color: 0x7a7a7a },
    'minecraft:granite':            { color: 0x9a6b55 },
    'minecraft:polished_granite':   { color: 0x9a6b55 },
    'minecraft:diorite':            { color: 0xbcb8b3 },
    'minecraft:polished_diorite':   { color: 0xbcb8b3 },
    'minecraft:andesite':           { color: 0x868686 },
    'minecraft:polished_andesite':  { color: 0x868686 },
    'minecraft:deepslate':          { color: 0x505050 },
    'minecraft:cobblestone':        { color: 0x7a7a7a },
    'minecraft:cobbled_deepslate':  { color: 0x4a4a4a },
    'minecraft:mossy_cobblestone':  { color: 0x6a7a5a },
    'minecraft:stone_bricks':       { color: 0x737373 },
    'minecraft:mossy_stone_bricks': { color: 0x637a53 },
    'minecraft:cracked_stone_bricks': { color: 0x6a6a6a },
    'minecraft:smooth_stone':       { color: 0x9a9a9a },
    'minecraft:bedrock':            { color: 0x3a3a3a },
    'minecraft:obsidian':           { color: 0x1a0a2a },
    'minecraft:crying_obsidian':    { color: 0x2a0a4a },
    'minecraft:tuff':               { color: 0x6a6a60 },
    'minecraft:calcite':            { color: 0xdbd9d3 },
    'minecraft:dripstone_block':    { color: 0x866a56 },

    // Dirt/Grass
    'minecraft:grass_block':        { color: 0x6b8c3f, topColor: 0x7ec850 },
    'minecraft:dirt':               { color: 0x866043 },
    'minecraft:coarse_dirt':        { color: 0x77553b },
    'minecraft:rooted_dirt':        { color: 0x8b6e4e },
    'minecraft:podzol':             { color: 0x6b5335, topColor: 0x6b5335 },
    'minecraft:mycelium':           { color: 0x6b6168, topColor: 0x8b7b82 },
    'minecraft:mud':                { color: 0x3c3837 },
    'minecraft:packed_mud':         { color: 0x8e7a5e },
    'minecraft:mud_bricks':         { color: 0x8a7456 },
    'minecraft:farmland':           { color: 0x6e4a2a },
    'minecraft:dirt_path':          { color: 0x9b8254 },
    'minecraft:clay':               { color: 0x9ea4b1 },
    'minecraft:gravel':             { color: 0x837e7a },

    // Sand
    'minecraft:sand':               { color: 0xdbd3a0 },
    'minecraft:red_sand':           { color: 0xbe6621 },
    'minecraft:sandstone':          { color: 0xd8cc82 },
    'minecraft:red_sandstone':      { color: 0xba6422 },
    'minecraft:smooth_sandstone':   { color: 0xd8cc82 },
    'minecraft:cut_sandstone':      { color: 0xd1c578 },

    // Ores
    'minecraft:coal_ore':           { color: 0x636363 },
    'minecraft:iron_ore':           { color: 0x887666 },
    'minecraft:copper_ore':         { color: 0x7c7c6a },
    'minecraft:gold_ore':           { color: 0x8a8252 },
    'minecraft:diamond_ore':        { color: 0x6fb8a8 },
    'minecraft:lapis_ore':          { color: 0x5a6e8a },
    'minecraft:redstone_ore':       { color: 0x8a4a42 },
    'minecraft:emerald_ore':        { color: 0x5a8a4a },
    'minecraft:deepslate_coal_ore':     { color: 0x4a4a4a },
    'minecraft:deepslate_iron_ore':     { color: 0x6a5e56 },
    'minecraft:deepslate_gold_ore':     { color: 0x6a6642 },
    'minecraft:deepslate_diamond_ore':  { color: 0x4a8a7a },
    'minecraft:deepslate_redstone_ore': { color: 0x6a3a32 },
    'minecraft:deepslate_lapis_ore':    { color: 0x3a4e6a },
    'minecraft:deepslate_emerald_ore':  { color: 0x4a6a3a },
    'minecraft:deepslate_copper_ore':   { color: 0x5c5c4a },

    // Mineral blocks
    'minecraft:iron_block':         { color: 0xd8d8d8 },
    'minecraft:gold_block':         { color: 0xf6d63c },
    'minecraft:diamond_block':      { color: 0x62ede8 },
    'minecraft:emerald_block':      { color: 0x2bbb3e },
    'minecraft:lapis_block':        { color: 0x1e3b96 },
    'minecraft:redstone_block':     { color: 0xaa0000 },
    'minecraft:coal_block':         { color: 0x1a1a1a },
    'minecraft:copper_block':       { color: 0xc06840 },
    'minecraft:netherite_block':    { color: 0x3a3a3e },
    'minecraft:amethyst_block':     { color: 0x8464b8 },

    // Wood - Oak
    'minecraft:oak_log':            { color: 0x6b5839 },
    'minecraft:oak_planks':         { color: 0xb8945f },
    'minecraft:oak_leaves':         { color: 0x4a7a2e, opacity: 0.85 },
    'minecraft:stripped_oak_log':   { color: 0xb29157 },

    // Wood - Spruce
    'minecraft:spruce_log':         { color: 0x3b2712 },
    'minecraft:spruce_planks':      { color: 0x6b5433 },
    'minecraft:spruce_leaves':      { color: 0x3a5a2a, opacity: 0.85 },
    'minecraft:stripped_spruce_log': { color: 0x6b5839 },

    // Wood - Birch
    'minecraft:birch_log':          { color: 0xd5cba4 },
    'minecraft:birch_planks':       { color: 0xc8b77a },
    'minecraft:birch_leaves':       { color: 0x6a8a3a, opacity: 0.85 },
    'minecraft:stripped_birch_log':  { color: 0xc5b88a },

    // Wood - Jungle
    'minecraft:jungle_log':         { color: 0x564a28 },
    'minecraft:jungle_planks':      { color: 0xb88858 },
    'minecraft:jungle_leaves':      { color: 0x3a7a1e, opacity: 0.85 },

    // Wood - Acacia
    'minecraft:acacia_log':         { color: 0x676157 },
    'minecraft:acacia_planks':      { color: 0xad5d32 },
    'minecraft:acacia_leaves':      { color: 0x6a8a2e, opacity: 0.85 },

    // Wood - Dark Oak
    'minecraft:dark_oak_log':       { color: 0x3e2912 },
    'minecraft:dark_oak_planks':    { color: 0x42321a },
    'minecraft:dark_oak_leaves':    { color: 0x2a5a1a, opacity: 0.85 },

    // Wood - Mangrove
    'minecraft:mangrove_log':       { color: 0x6a3520 },
    'minecraft:mangrove_planks':    { color: 0x763636 },
    'minecraft:mangrove_leaves':    { color: 0x4a7a2a, opacity: 0.85 },

    // Wood - Cherry
    'minecraft:cherry_log':         { color: 0x3a2028 },
    'minecraft:cherry_planks':      { color: 0xdf8e8e },
    'minecraft:cherry_leaves':      { color: 0xe8b0c8, opacity: 0.85 },

    // Nether blocks
    'minecraft:netherrack':         { color: 0x6b2020 },
    'minecraft:nether_bricks':      { color: 0x2c1418 },
    'minecraft:soul_sand':          { color: 0x513e2f },
    'minecraft:soul_soil':          { color: 0x4b3a2b },
    'minecraft:basalt':             { color: 0x4a4a4e },
    'minecraft:smooth_basalt':      { color: 0x4a4a50 },
    'minecraft:blackstone':         { color: 0x2a2a30 },
    'minecraft:glowstone':          { color: 0xc9a35a, emissive: 0xc9a35a },
    'minecraft:nether_quartz_ore':  { color: 0x7a4040 },
    'minecraft:nether_gold_ore':    { color: 0x7a4a3a },
    'minecraft:ancient_debris':     { color: 0x6a4a3a },
    'minecraft:crimson_nylium':     { color: 0x832323, topColor: 0xb72828 },
    'minecraft:warped_nylium':      { color: 0x2a6a5a, topColor: 0x28a288 },
    'minecraft:shroomlight':        { color: 0xd89848, emissive: 0xd89848 },
    'minecraft:magma_block':        { color: 0x8a3a0a, emissive: 0x4a1a00 },

    // End blocks
    'minecraft:end_stone':          { color: 0xdbd6a0 },
    'minecraft:end_stone_bricks':   { color: 0xdbd6a0 },
    'minecraft:purpur_block':       { color: 0xa87ca8 },
    'minecraft:purpur_pillar':      { color: 0xab7eab },

    // Water/Lava
    'minecraft:water':              { color: 0x3f76e4, opacity: 0.6 },
    'minecraft:lava':               { color: 0xcf4a0a, emissive: 0xcf4a0a },

    // Ice/Snow
    'minecraft:ice':                { color: 0x8abaff, opacity: 0.7 },
    'minecraft:packed_ice':         { color: 0x7aaaf8 },
    'minecraft:blue_ice':           { color: 0x74a8f8 },
    'minecraft:snow_block':         { color: 0xf0f0f0 },
    'minecraft:snow':               { color: 0xf0f0f0 },
    'minecraft:powder_snow':        { color: 0xf8f8f8 },

    // Glass
    'minecraft:glass':              { color: 0xc0d8f0, opacity: 0.3 },
    'minecraft:tinted_glass':       { color: 0x2a2028, opacity: 0.6 },
    'minecraft:white_stained_glass':  { color: 0xffffff, opacity: 0.4 },
    'minecraft:orange_stained_glass': { color: 0xd87f33, opacity: 0.4 },
    'minecraft:light_blue_stained_glass': { color: 0x6699d8, opacity: 0.4 },
    'minecraft:yellow_stained_glass': { color: 0xe5e533, opacity: 0.4 },
    'minecraft:lime_stained_glass':   { color: 0x7fcc19, opacity: 0.4 },
    'minecraft:red_stained_glass':    { color: 0x993333, opacity: 0.4 },
    'minecraft:black_stained_glass':  { color: 0x191919, opacity: 0.4 },

    // Wool
    'minecraft:white_wool':         { color: 0xe9e9e9 },
    'minecraft:orange_wool':        { color: 0xd87f33 },
    'minecraft:magenta_wool':       { color: 0xb24cd8 },
    'minecraft:light_blue_wool':    { color: 0x6699d8 },
    'minecraft:yellow_wool':        { color: 0xe5e533 },
    'minecraft:lime_wool':          { color: 0x7fcc19 },
    'minecraft:pink_wool':          { color: 0xf27fa5 },
    'minecraft:gray_wool':          { color: 0x4c4c4c },
    'minecraft:light_gray_wool':    { color: 0x999999 },
    'minecraft:cyan_wool':          { color: 0x4c7f99 },
    'minecraft:purple_wool':        { color: 0x7f3fb2 },
    'minecraft:blue_wool':          { color: 0x334cb2 },
    'minecraft:brown_wool':         { color: 0x664c33 },
    'minecraft:green_wool':         { color: 0x667f33 },
    'minecraft:red_wool':           { color: 0x993333 },
    'minecraft:black_wool':         { color: 0x191919 },

    // Terracotta
    'minecraft:terracotta':         { color: 0x985e43 },
    'minecraft:white_terracotta':   { color: 0xd1b1a1 },
    'minecraft:orange_terracotta':  { color: 0xa15325 },
    'minecraft:brown_terracotta':   { color: 0x4d3224 },
    'minecraft:red_terracotta':     { color: 0x8e3c2e },
    'minecraft:yellow_terracotta':  { color: 0xba8523 },
    'minecraft:cyan_terracotta':    { color: 0x565b5b },
    'minecraft:light_gray_terracotta': { color: 0x876a61 },

    // Concrete
    'minecraft:white_concrete':     { color: 0xcfd5d6 },
    'minecraft:orange_concrete':    { color: 0xe06101 },
    'minecraft:light_blue_concrete':{ color: 0x2389c7 },
    'minecraft:yellow_concrete':    { color: 0xf0af15 },
    'minecraft:lime_concrete':      { color: 0x5ea919 },
    'minecraft:red_concrete':       { color: 0x8e2121 },
    'minecraft:black_concrete':     { color: 0x080a0f },
    'minecraft:gray_concrete':      { color: 0x36393d },
    'minecraft:cyan_concrete':      { color: 0x157788 },
    'minecraft:blue_concrete':      { color: 0x2c2e8f },
    'minecraft:green_concrete':     { color: 0x495b24 },

    // Building blocks
    'minecraft:bricks':             { color: 0x966153 },
    'minecraft:prismarine':         { color: 0x5b9b84 },
    'minecraft:dark_prismarine':    { color: 0x335c4c },
    'minecraft:prismarine_bricks':  { color: 0x63ab9a },
    'minecraft:sea_lantern':        { color: 0xacd4d4, emissive: 0xacd4d4 },
    'minecraft:bookshelf':          { color: 0x6b4e34 },
    'minecraft:crafting_table':     { color: 0x9c6d3a, topColor: 0xb38a56 },
    'minecraft:furnace':            { color: 0x7a7a7a },
    'minecraft:chest':              { color: 0x9c6d3a },
    'minecraft:barrel':             { color: 0x826644 },
    'minecraft:hay_block':          { color: 0xa89332 },
    'minecraft:bone_block':         { color: 0xd2ccb0 },
    'minecraft:melon':              { color: 0x6a9a2a },
    'minecraft:pumpkin':            { color: 0xc87614 },
    'minecraft:jack_o_lantern':     { color: 0xc87614, emissive: 0x885010 },
    'minecraft:sponge':             { color: 0xc2b83e },
    'minecraft:wet_sponge':         { color: 0xa8ad32 },
    'minecraft:dried_kelp_block':   { color: 0x3a3e1e },
    'minecraft:moss_block':         { color: 0x5a7a3a },
    'minecraft:sculk':              { color: 0x0a2a3a },

    // Light sources
    'minecraft:torch':              { color: 0xf0c840, emissive: 0xf0c840 },
    'minecraft:lantern':            { color: 0xd89848, emissive: 0xd89848 },
    'minecraft:soul_lantern':       { color: 0x48c8d8, emissive: 0x48c8d8 },
    'minecraft:redstone_lamp':      { color: 0x8a5a3a },
    'minecraft:beacon':             { color: 0x78dbd5, emissive: 0x78dbd5 },
  };

  // Block shapes for non-full-block rendering
  const BLOCK_SHAPES = {};

  // Helper: parse block state string to extract base name and properties
  function parseBlockState(stateStr) {
    if (!stateStr) return { name: 'minecraft:air', props: {} };
    const bracketIdx = stateStr.indexOf('[');
    if (bracketIdx === -1) return { name: stateStr, props: {} };

    const name = stateStr.substring(0, bracketIdx);
    const propStr = stateStr.substring(bracketIdx + 1, stateStr.length - 1);
    const props = {};
    for (const pair of propStr.split(',')) {
      const [k, v] = pair.split('=');
      if (k && v) props[k] = v;
    }
    return { name, props };
  }

  // Get rendering info for a block state
  function getBlockInfo(stateStr) {
    const { name, props } = parseBlockState(stateStr);

    // Direct lookup
    let info = BLOCK_COLORS[name];
    if (!info) {
      // Try stripping prefixes for stained/colored variants
      for (const prefix of ['white_', 'orange_', 'magenta_', 'light_blue_', 'yellow_',
        'lime_', 'pink_', 'gray_', 'light_gray_', 'cyan_', 'purple_', 'blue_',
        'brown_', 'green_', 'red_', 'black_']) {
        const stripped = name.replace('minecraft:' + prefix, 'minecraft:');
        if (BLOCK_COLORS[stripped]) {
          info = BLOCK_COLORS[stripped];
          break;
        }
      }
    }

    if (!info) {
      // Check for log/planks/leaves/slab/stairs/fence/wall variants
      if (name.includes('_log') || name.includes('_wood') || name.includes('_stem') || name.includes('_hyphae')) {
        info = { color: 0x6b5839 };
      } else if (name.includes('_planks')) {
        info = { color: 0xb8945f };
      } else if (name.includes('_leaves') || name.includes('_wart_block')) {
        info = { color: 0x4a7a2e, opacity: 0.85 };
      } else if (name.includes('_concrete_powder')) {
        info = { color: 0xb0a080 };
      } else if (name.includes('_carpet') || name.includes('_bed') || name.includes('_banner')) {
        info = { color: 0x999999 };
      } else if (name.includes('_ore')) {
        info = { color: 0x7a7a7a };
      }
    }

    // Default: unknown block = magenta (easy to spot)
    if (!info) info = { color: 0xff00ff };

    // Determine shape from block state properties
    let shape = 'full';
    if (name.includes('_slab')) {
      shape = props.type === 'top' ? 'slab_top' : (props.type === 'double' ? 'full' : 'slab_bottom');
    } else if (name.includes('_stairs')) {
      shape = 'stairs';
    } else if (name.includes('_fence') && !name.includes('_gate')) {
      shape = 'thin';
    } else if (name.includes('_wall')) {
      shape = 'thin';
    } else if (name.includes('_pane') || name === 'minecraft:iron_bars') {
      shape = 'thin';
    } else if (name.includes('_door') || name.includes('_trapdoor')) {
      shape = 'thin';
    } else if (name === 'minecraft:snow') {
      shape = 'snow_layer';
    }

    return { ...info, shape, props, name };
  }

  // Check if a block is considered transparent (doesn't cull adjacent faces)
  function isTransparent(stateStr) {
    if (!stateStr || stateStr === 'minecraft:air' || stateStr === 'minecraft:void_air' || stateStr === 'minecraft:cave_air') return true;
    const { name } = parseBlockState(stateStr);
    const info = BLOCK_COLORS[name];
    if (info && info.opacity !== undefined && info.opacity < 1) return true;
    if (name.includes('_leaves') || name.includes('glass') || name.includes('_pane') ||
        name === 'minecraft:water' || name === 'minecraft:lava' ||
        name === 'minecraft:ice' || name.includes('_door') || name.includes('_trapdoor') ||
        name.includes('_fence') || name.includes('_wall') || name.includes('_slab') ||
        name.includes('_stairs') || name === 'minecraft:iron_bars' ||
        name.includes('torch') || name.includes('lantern') || name === 'minecraft:snow') return true;
    return false;
  }

  // Check if a block is air
  function isAir(stateStr) {
    return !stateStr || stateStr === 'minecraft:air' || stateStr === 'minecraft:void_air' || stateStr === 'minecraft:cave_air';
  }

  // ===== CHUNK DATA PARSER =====

  class ChunkDataParser {
    /**
     * Parse binary chunk data from base64-encoded gzipped binary.
     * Returns array of { chunkX, chunkZ, sections: [{ sectionY, palette, blocks }] }
     */
    static async parse(base64Data) {
      // Decode base64
      const binaryString = atob(base64Data);
      const bytes = new Uint8Array(binaryString.length);
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }

      // Decompress GZIP
      let decompressed;
      try {
        const ds = new DecompressionStream('gzip');
        const writer = ds.writable.getWriter();
        writer.write(bytes);
        writer.close();
        const reader = ds.readable.getReader();
        const chunks = [];
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          chunks.push(value);
        }
        // Concatenate chunks
        let totalLen = 0;
        for (const c of chunks) totalLen += c.length;
        decompressed = new Uint8Array(totalLen);
        let offset = 0;
        for (const c of chunks) {
          decompressed.set(c, offset);
          offset += c.length;
        }
      } catch (e) {
        console.error('[Terrain] GZIP decompression failed:', e);
        throw e;
      }

      // Parse binary format
      const view = new DataView(decompressed.buffer);
      let pos = 0;

      // Read magic "MXCHUNK" (7 bytes)
      const magic = String.fromCharCode(...decompressed.slice(0, 7));
      if (magic !== 'MXCHUNK') throw new Error('Invalid chunk data: bad magic');
      pos = 7;

      // Version
      const version = view.getUint8(pos++);
      if (version !== 1) throw new Error('Unsupported chunk format version: ' + version);

      // Chunk count
      const chunkCount = view.getInt32(pos); pos += 4;

      const columns = [];
      for (let i = 0; i < chunkCount; i++) {
        const col = ChunkDataParser._parseColumn(view, decompressed, pos);
        columns.push(col.data);
        pos = col.pos;
      }

      return columns;
    }

    static _parseColumn(view, bytes, pos) {
      const chunkX = view.getInt32(pos); pos += 4;
      const chunkZ = view.getInt32(pos); pos += 4;
      const sectionCount = bytes[pos++];

      const sections = [];
      for (let i = 0; i < sectionCount; i++) {
        const sec = ChunkDataParser._parseSection(view, bytes, pos);
        sections.push(sec.data);
        pos = sec.pos;
      }

      return { data: { chunkX, chunkZ, sections }, pos };
    }

    static _parseSection(view, bytes, pos) {
      const sectionY = view.getInt8(pos++);

      // Palette
      const paletteSize = view.getInt16(pos) & 0xFFFF; pos += 2;
      const palette = [];
      for (let i = 0; i < paletteSize; i++) {
        // Read UTF: 2 bytes length + string bytes
        const strLen = view.getUint16(pos); pos += 2;
        let str = '';
        for (let j = 0; j < strLen; j++) {
          str += String.fromCharCode(bytes[pos++]);
        }
        palette.push(str);
      }

      // Block data: 4096 indices
      const useShort = paletteSize > 256;
      const blocks = new Uint16Array(4096);
      for (let i = 0; i < 4096; i++) {
        if (useShort) {
          blocks[i] = view.getInt16(pos) & 0xFFFF; pos += 2;
        } else {
          blocks[i] = bytes[pos++];
        }
      }

      return { data: { sectionY, palette, blocks }, pos };
    }
  }

  // ===== GREEDY MESHING ENGINE =====

  // Face directions: [dx, dy, dz, axis, positive]
  const FACES = [
    { dir: [1, 0, 0],  axis: 0, positive: true,  name: 'east' },   // +X
    { dir: [-1, 0, 0], axis: 0, positive: false, name: 'west' },   // -X
    { dir: [0, 1, 0],  axis: 1, positive: true,  name: 'up' },     // +Y
    { dir: [0, -1, 0], axis: 1, positive: false, name: 'down' },   // -Y
    { dir: [0, 0, 1],  axis: 2, positive: true,  name: 'south' },  // +Z
    { dir: [0, 0, -1], axis: 2, positive: false, name: 'north' },  // -Z
  ];

  /**
   * Build geometry for a chunk section using greedy meshing.
   * Returns { positions: Float32Array, normals: Float32Array, colors: Float32Array, indices: Uint32Array }
   */
  function buildSectionMesh(section, chunkX, chunkZ, getNeighborBlock) {
    const positions = [];
    const normals = [];
    const colors = [];
    const indices = [];
    let vertexCount = 0;

    const { palette, blocks, sectionY } = section;
    const baseWorldX = chunkX * 16;
    const baseWorldY = sectionY * 16;
    const baseWorldZ = chunkZ * 16;

    // Helper to get block at local section coords
    function getBlock(lx, ly, lz) {
      if (lx < 0 || lx >= 16 || ly < 0 || ly >= 16 || lz < 0 || lz >= 16) {
        // Cross-section/cross-chunk boundary lookup
        const wx = baseWorldX + lx;
        const wy = baseWorldY + ly;
        const wz = baseWorldZ + lz;
        return getNeighborBlock(wx, wy, wz);
      }
      const idx = ly * 256 + lz * 16 + lx;
      return palette[blocks[idx]];
    }

    // For each face direction, sweep and do greedy meshing
    for (const face of FACES) {
      const [dx, dy, dz] = face.dir;
      const axis = face.axis;

      // The two axes perpendicular to the face normal
      const u = (axis + 1) % 3; // horizontal sweep
      const v = (axis + 2) % 3; // vertical sweep

      // Iterate through slices along the face axis
      for (let d = 0; d < 16; d++) {
        // Build a 16x16 mask of faces to render
        const mask = new Array(256).fill(null); // 16x16
        const maskColors = new Array(256).fill(null);

        for (let j = 0; j < 16; j++) {
          for (let i = 0; i < 16; i++) {
            // Compute local coords based on axis
            const pos = [0, 0, 0];
            pos[axis] = d;
            pos[u] = i;
            pos[v] = j;

            const lx = pos[0], ly = pos[1], lz = pos[2];
            const blockState = getBlock(lx, ly, lz);

            if (isAir(blockState)) continue;

            // Check neighbor in face direction
            const nlx = lx + dx;
            const nly = ly + dy;
            const nlz = lz + dz;
            const neighborState = getBlock(nlx, nly, nlz);

            // Render face if neighbor is transparent (or different transparent block)
            if (isTransparent(neighborState) && (isAir(neighborState) || neighborState !== blockState)) {
              const info = getBlockInfo(blockState);
              let faceColor = info.color;
              // Use topColor for top face of grass-like blocks
              if (face.name === 'up' && info.topColor) {
                faceColor = info.topColor;
              }
              mask[j * 16 + i] = blockState;
              maskColors[j * 16 + i] = faceColor;
            }
          }
        }

        // Greedy merge: scan the mask and merge rectangles of identical blocks
        const visited = new Array(256).fill(false);

        for (let j = 0; j < 16; j++) {
          for (let i = 0; i < 16; i++) {
            const idx = j * 16 + i;
            if (visited[idx] || !mask[idx]) continue;

            const color = maskColors[idx];
            const state = mask[idx];

            // Expand width (i direction)
            let width = 1;
            while (i + width < 16) {
              const ni = j * 16 + (i + width);
              if (!visited[ni] && mask[ni] === state && maskColors[ni] === color) {
                width++;
              } else break;
            }

            // Expand height (j direction)
            let height = 1;
            outer:
            while (j + height < 16) {
              for (let wi = 0; wi < width; wi++) {
                const ni = (j + height) * 16 + (i + wi);
                if (visited[ni] || mask[ni] !== state || maskColors[ni] !== color) {
                  break outer;
                }
              }
              height++;
            }

            // Mark visited
            for (let dj = 0; dj < height; dj++) {
              for (let di = 0; di < width; di++) {
                visited[(j + dj) * 16 + (i + di)] = true;
              }
            }

            // Emit quad
            const p = [0, 0, 0];
            p[axis] = d + (face.positive ? 1 : 0);
            p[u] = i;
            p[v] = j;

            const du = [0, 0, 0]; du[u] = width;
            const dv = [0, 0, 0]; dv[v] = height;

            // World position offset
            const wx = baseWorldX + p[0];
            const wy = baseWorldY + p[1];
            const wz = baseWorldZ + p[2];

            // Normal
            const nx = dx, ny = dy, nz = dz;

            // Convert color int to RGB floats with face-based AO shading
            // Top faces brightest, bottom darkest, sides in between
            let aoFactor = 1.0;
            if (face.name === 'down') aoFactor = 0.6;
            else if (face.name === 'up') aoFactor = 1.0;
            else if (face.name === 'north' || face.name === 'south') aoFactor = 0.8;
            else aoFactor = 0.75; // east/west

            const r = (((color >> 16) & 0xFF) / 255) * aoFactor;
            const g = (((color >> 8) & 0xFF) / 255) * aoFactor;
            const b = ((color & 0xFF) / 255) * aoFactor;

            // 4 vertices for the quad
            const v0 = [wx, wy, wz];
            const v1 = [wx + du[0], wy + du[1], wz + du[2]];
            const v2 = [wx + du[0] + dv[0], wy + du[1] + dv[1], wz + du[2] + dv[2]];
            const v3 = [wx + dv[0], wy + dv[1], wz + dv[2]];

            // Wind faces correctly based on face direction
            let quad;
            if (face.positive) {
              quad = [v0, v1, v2, v3]; // CCW when viewed from outside
            } else {
              quad = [v0, v3, v2, v1]; // Reversed for negative faces
            }

            for (const vert of quad) {
              positions.push(vert[0], vert[1], vert[2]);
              normals.push(nx, ny, nz);
              colors.push(r, g, b);
            }

            // Two triangles
            indices.push(
              vertexCount, vertexCount + 1, vertexCount + 2,
              vertexCount, vertexCount + 2, vertexCount + 3
            );
            vertexCount += 4;
          }
        }
      }
    }

    return {
      positions: new Float32Array(positions),
      normals: new Float32Array(normals),
      colors: new Float32Array(colors),
      indices: new Uint32Array(indices)
    };
  }

  // ===== CHUNK COLUMN MANAGER =====

  class ChunkColumnManager {
    constructor(scene) {
      this.scene = scene;
      this.columns = new Map(); // "cx,cz" -> { group, sectionData, sectionMeshes }
      this.terrainGroup = new THREE.Group();
      this.terrainGroup.name = 'terrain';
      scene.add(this.terrainGroup);

      // Shared material for opaque terrain
      this.opaqueMaterial = new THREE.MeshStandardMaterial({
        vertexColors: true,
        roughness: 0.85,
        metalness: 0.05,
        side: THREE.FrontSide,
      });

      // Shared material for transparent terrain
      this.transparentMaterial = new THREE.MeshStandardMaterial({
        vertexColors: true,
        roughness: 0.85,
        metalness: 0.05,
        side: THREE.DoubleSide,
        transparent: true,
        opacity: 0.7,
        depthWrite: false,
      });
    }

    /**
     * Load parsed chunk columns into the scene.
     * @param {Array} columns - Array of { chunkX, chunkZ, sections }
     */
    loadColumns(columns) {
      console.log(`[Terrain] Loading ${columns.length} chunk columns...`);
      const startTime = performance.now();

      // Store all column data first (for cross-chunk neighbor lookups)
      for (const col of columns) {
        const key = `${col.chunkX},${col.chunkZ}`;
        this.columns.set(key, {
          data: col,
          group: null,
          sectionMeshes: new Map(),
        });
      }

      // Build meshes
      for (const col of columns) {
        this._buildColumnMeshes(col.chunkX, col.chunkZ);
      }

      const elapsed = (performance.now() - startTime).toFixed(1);
      console.log(`[Terrain] Built meshes for ${columns.length} columns in ${elapsed}ms`);
    }

    /**
     * Get block state at a world coordinate.
     */
    getBlockAt(wx, wy, wz) {
      const cx = Math.floor(wx / 16);
      const cz = Math.floor(wz / 16);
      const key = `${cx},${cz}`;
      const column = this.columns.get(key);
      if (!column) return 'minecraft:air';

      const sy = Math.floor(wy / 16);
      const section = column.data.sections.find(s => s.sectionY === sy);
      if (!section) return 'minecraft:air';

      const lx = ((wx % 16) + 16) % 16;
      const ly = ((wy % 16) + 16) % 16;
      const lz = ((wz % 16) + 16) % 16;
      const idx = ly * 256 + lz * 16 + lx;
      return section.palette[section.blocks[idx]];
    }

    /**
     * Update a single block and re-mesh the affected section.
     */
    setBlock(wx, wy, wz, blockState) {
      const cx = Math.floor(wx / 16);
      const cz = Math.floor(wz / 16);
      const key = `${cx},${cz}`;
      const column = this.columns.get(key);
      if (!column) return;

      const sy = Math.floor(wy / 16);
      let section = column.data.sections.find(s => s.sectionY === sy);

      if (!section) {
        // Create a new air section and add the block
        const palette = ['minecraft:air', blockState];
        const blocks = new Uint16Array(4096).fill(0);
        const lx = ((wx % 16) + 16) % 16;
        const ly = ((wy % 16) + 16) % 16;
        const lz = ((wz % 16) + 16) % 16;
        blocks[ly * 256 + lz * 16 + lx] = 1;
        section = { sectionY: sy, palette, blocks };
        column.data.sections.push(section);
      } else {
        // Update existing section
        const lx = ((wx % 16) + 16) % 16;
        const ly = ((wy % 16) + 16) % 16;
        const lz = ((wz % 16) + 16) % 16;
        let paletteIdx = section.palette.indexOf(blockState);
        if (paletteIdx === -1) {
          paletteIdx = section.palette.length;
          section.palette.push(blockState);
        }
        section.blocks[ly * 256 + lz * 16 + lx] = paletteIdx;
      }

      // Re-mesh this section
      this._remeshSection(cx, cz, sy);

      // Also re-mesh neighbor sections if the block is on a boundary
      const lx = ((wx % 16) + 16) % 16;
      const ly = ((wy % 16) + 16) % 16;
      const lz = ((wz % 16) + 16) % 16;
      if (lx === 0) this._remeshSection(cx - 1, cz, sy);
      if (lx === 15) this._remeshSection(cx + 1, cz, sy);
      if (ly === 0) this._remeshSection(cx, cz, sy - 1);
      if (ly === 15) this._remeshSection(cx, cz, sy + 1);
      if (lz === 0) this._remeshSection(cx, cz - 1, sy);
      if (lz === 15) this._remeshSection(cx, cz + 1, sy);
    }

    _buildColumnMeshes(cx, cz) {
      const key = `${cx},${cz}`;
      const column = this.columns.get(key);
      if (!column) return;

      const group = new THREE.Group();
      group.name = `chunk_${cx}_${cz}`;

      for (const section of column.data.sections) {
        const mesh = this._buildSectionMesh(cx, cz, section);
        if (mesh) {
          group.add(mesh);
          column.sectionMeshes.set(section.sectionY, mesh);
        }
      }

      column.group = group;
      this.terrainGroup.add(group);
    }

    _buildSectionMesh(cx, cz, section) {
      const getNeighborBlock = (wx, wy, wz) => this.getBlockAt(wx, wy, wz);
      const meshData = buildSectionMesh(section, cx, cz, getNeighborBlock);

      if (meshData.positions.length === 0) return null;

      const geometry = new THREE.BufferGeometry();
      geometry.setAttribute('position', new THREE.BufferAttribute(meshData.positions, 3));
      geometry.setAttribute('normal', new THREE.BufferAttribute(meshData.normals, 3));
      geometry.setAttribute('color', new THREE.BufferAttribute(meshData.colors, 3));
      geometry.setIndex(new THREE.BufferAttribute(meshData.indices, 1));

      const mesh = new THREE.Mesh(geometry, this.opaqueMaterial);
      mesh.name = `section_${section.sectionY}`;
      mesh.castShadow = true;
      mesh.receiveShadow = true;
      return mesh;
    }

    _remeshSection(cx, cz, sectionY) {
      const key = `${cx},${cz}`;
      const column = this.columns.get(key);
      if (!column || !column.group) return;

      const section = column.data.sections.find(s => s.sectionY === sectionY);
      if (!section) return;

      // Remove old mesh
      const oldMesh = column.sectionMeshes.get(sectionY);
      if (oldMesh) {
        column.group.remove(oldMesh);
        oldMesh.geometry.dispose();
      }

      // Build new mesh
      const newMesh = this._buildSectionMesh(cx, cz, section);
      if (newMesh) {
        column.group.add(newMesh);
        column.sectionMeshes.set(sectionY, newMesh);
      }
    }

    /**
     * Get the terrain bounding box center (for camera positioning).
     */
    getCenter() {
      let minX = Infinity, maxX = -Infinity;
      let minZ = Infinity, maxZ = -Infinity;
      let avgY = 64;

      for (const [key, col] of this.columns) {
        const wx = col.data.chunkX * 16 + 8;
        const wz = col.data.chunkZ * 16 + 8;
        minX = Math.min(minX, wx);
        maxX = Math.max(maxX, wx);
        minZ = Math.min(minZ, wz);
        maxZ = Math.max(maxZ, wz);
      }

      return {
        x: (minX + maxX) / 2,
        y: avgY,
        z: (minZ + maxZ) / 2
      };
    }

    /**
     * Dispose all meshes and remove from scene.
     */
    dispose() {
      for (const [key, col] of this.columns) {
        if (col.group) {
          this.terrainGroup.remove(col.group);
          col.group.traverse(child => {
            if (child.geometry) child.geometry.dispose();
          });
        }
      }
      this.columns.clear();
      this.scene.remove(this.terrainGroup);
      this.opaqueMaterial.dispose();
      this.transparentMaterial.dispose();
    }
  }

  // ===== FREE CAMERA CONTROLS =====

  class FreeCameraControls {
    constructor(camera, domElement) {
      this.camera = camera;
      this.domElement = domElement;
      this.speed = 15; // blocks per second
      this.sensitivity = 0.002;
      this.keys = {};
      this.euler = new THREE.Euler(0, 0, 0, 'YXZ');
      this.locked = false;
      this.enabled = false;

      this._onPointerLockChange = () => {
        this.locked = document.pointerLockElement === domElement;
      };
      this._onMouseMove = (e) => {
        if (!this.locked || !this.enabled) return;
        this.euler.y -= e.movementX * this.sensitivity;
        this.euler.x = Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01,
          this.euler.x - e.movementY * this.sensitivity));
        this.camera.quaternion.setFromEuler(this.euler);
      };
      this._onKeyDown = (e) => { this.keys[e.code] = true; };
      this._onKeyUp = (e) => { this.keys[e.code] = false; };
      this._onClick = () => {
        if (this.enabled && !this.locked) {
          domElement.requestPointerLock();
        }
      };

      document.addEventListener('pointerlockchange', this._onPointerLockChange);
      document.addEventListener('mousemove', this._onMouseMove);
      document.addEventListener('keydown', this._onKeyDown);
      document.addEventListener('keyup', this._onKeyUp);
      domElement.addEventListener('click', this._onClick);
    }

    enable() {
      this.enabled = true;
      // Set euler from current camera rotation
      this.euler.setFromQuaternion(this.camera.quaternion);
    }

    disable() {
      this.enabled = false;
      if (this.locked) document.exitPointerLock();
      this.keys = {};
    }

    update(delta) {
      if (!this.enabled || !this.locked) return;

      const dir = new THREE.Vector3();
      const right = new THREE.Vector3();
      this.camera.getWorldDirection(dir);
      right.crossVectors(dir, new THREE.Vector3(0, 1, 0)).normalize();

      // Speed boost with Ctrl
      const moveSpeed = this.speed * delta * (this.keys['ControlLeft'] ? 3 : 1);

      if (this.keys['KeyW']) this.camera.position.addScaledVector(dir, moveSpeed);
      if (this.keys['KeyS']) this.camera.position.addScaledVector(dir, -moveSpeed);
      if (this.keys['KeyA']) this.camera.position.addScaledVector(right, -moveSpeed);
      if (this.keys['KeyD']) this.camera.position.addScaledVector(right, moveSpeed);
      if (this.keys['Space']) this.camera.position.y += moveSpeed;
      if (this.keys['ShiftLeft']) this.camera.position.y -= moveSpeed;
    }

    dispose() {
      document.removeEventListener('pointerlockchange', this._onPointerLockChange);
      document.removeEventListener('mousemove', this._onMouseMove);
      document.removeEventListener('keydown', this._onKeyDown);
      document.removeEventListener('keyup', this._onKeyUp);
      this.domElement.removeEventListener('click', this._onClick);
      if (this.locked) document.exitPointerLock();
    }
  }

  // ===== BLOCK CHANGE APPLICATOR =====

  class BlockChangeApplicator {
    constructor(chunkManager) {
      this.chunkManager = chunkManager;
      this.blockLogs = [];
      this.appliedIndex = 0;
      this.originalStates = new Map(); // "x,y,z" -> original block state
    }

    /**
     * Set block logs sorted by timestamp.
     */
    setBlockLogs(logs) {
      this.blockLogs = logs.sort((a, b) => a.timestamp - b.timestamp);
      this.appliedIndex = 0;
      this.originalStates.clear();
    }

    /**
     * Apply block changes up to the given absolute timestamp.
     */
    applyUpTo(absoluteTime) {
      while (this.appliedIndex < this.blockLogs.length) {
        const log = this.blockLogs[this.appliedIndex];
        if (log.timestamp > absoluteTime) break;

        // Save original state for rewind
        const key = `${log.x},${log.y},${log.z}`;
        if (!this.originalStates.has(key)) {
          this.originalStates.set(key, this.chunkManager.getBlockAt(log.x, log.y, log.z));
        }

        // Map Bukkit material name to Minecraft block state
        const newState = this._materialToBlockState(log.newMaterial, log.newBlockData);
        this.chunkManager.setBlock(log.x, log.y, log.z, newState);
        this.appliedIndex++;
      }
    }

    /**
     * Reset all block changes (for seeking/rewinding).
     */
    reset() {
      // Restore original block states
      for (const [key, state] of this.originalStates) {
        const [x, y, z] = key.split(',').map(Number);
        this.chunkManager.setBlock(x, y, z, state);
      }
      this.appliedIndex = 0;
      this.originalStates.clear();
    }

    /**
     * Seek to a specific time (reset then apply).
     */
    seekTo(absoluteTime) {
      this.reset();
      this.applyUpTo(absoluteTime);
    }

    /**
     * Convert Bukkit Material name to Minecraft block state string.
     */
    _materialToBlockState(materialName, blockDataStr) {
      if (!materialName || materialName === 'AIR') return 'minecraft:air';

      // If full block data string is provided, use it directly
      if (blockDataStr && blockDataStr.startsWith('minecraft:')) {
        return blockDataStr;
      }

      // Convert Bukkit material name to Minecraft namespace
      return 'minecraft:' + materialName.toLowerCase();
    }
  }

  // ===== PUBLIC API =====

  window.MX = window.MX || {};
  window.MX.terrain = {
    ChunkDataParser,
    ChunkColumnManager,
    FreeCameraControls,
    BlockChangeApplicator,
    getBlockInfo,
    isAir,
    isTransparent,
  };

})();

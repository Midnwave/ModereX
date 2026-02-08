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

    // ===== ALL 16 WOOL COLORS =====
    'minecraft:orange_wool':        { color: 0xf07613 },
    'minecraft:magenta_wool':       { color: 0xbd44b3 },
    'minecraft:light_blue_wool':    { color: 0x3ab3da },
    'minecraft:yellow_wool':        { color: 0xf8c527 },
    'minecraft:lime_wool':          { color: 0x70b919 },
    'minecraft:pink_wool':          { color: 0xed8dac },
    'minecraft:gray_wool':          { color: 0x474f52 },
    'minecraft:light_gray_wool':    { color: 0x9c9c94 },
    'minecraft:cyan_wool':          { color: 0x158991 },
    'minecraft:purple_wool':        { color: 0x7b2fbe },
    'minecraft:blue_wool':          { color: 0x353a9e },
    'minecraft:brown_wool':         { color: 0x724728 },
    'minecraft:green_wool':         { color: 0x546d1b },
    'minecraft:red_wool':           { color: 0xa02722 },
    'minecraft:black_wool':         { color: 0x1d1c21 },

    // ===== ALL 16 CONCRETE COLORS =====
    'minecraft:orange_concrete':       { color: 0xe06101 },
    'minecraft:magenta_concrete':      { color: 0xa9309f },
    'minecraft:light_blue_concrete':   { color: 0x2389c7 },
    'minecraft:yellow_concrete':       { color: 0xf0af15 },
    'minecraft:lime_concrete':         { color: 0x5ea818 },
    'minecraft:pink_concrete':         { color: 0xd6658f },
    'minecraft:purple_concrete':       { color: 0x641f9c },
    'minecraft:cyan_concrete':         { color: 0x157788 },
    'minecraft:blue_concrete':         { color: 0x2c2e8f },
    'minecraft:brown_concrete':        { color: 0x603b1f },
    'minecraft:green_concrete':        { color: 0x495b24 },
    'minecraft:red_concrete':          { color: 0x8e2121 },
    'minecraft:light_gray_concrete':   { color: 0x7d7d73 },

    // ===== ALL 16 CONCRETE POWDER =====
    'minecraft:white_concrete_powder':      { color: 0xe1e1dd },
    'minecraft:orange_concrete_powder':     { color: 0xe38422 },
    'minecraft:magenta_concrete_powder':    { color: 0xc052b7 },
    'minecraft:light_blue_concrete_powder': { color: 0x6eaed1 },
    'minecraft:yellow_concrete_powder':     { color: 0xe8c736 },
    'minecraft:lime_concrete_powder':       { color: 0x7dbb26 },
    'minecraft:pink_concrete_powder':       { color: 0xe39ab0 },
    'minecraft:gray_concrete_powder':       { color: 0x4d4f4d },
    'minecraft:light_gray_concrete_powder': { color: 0x9a9a94 },
    'minecraft:cyan_concrete_powder':       { color: 0x24939d },
    'minecraft:purple_concrete_powder':     { color: 0x7b2fac },
    'minecraft:blue_concrete_powder':       { color: 0x4648ac },
    'minecraft:brown_concrete_powder':      { color: 0x7e5535 },
    'minecraft:green_concrete_powder':      { color: 0x61772d },
    'minecraft:red_concrete_powder':        { color: 0xa83632 },
    'minecraft:black_concrete_powder':      { color: 0x19171b },

    // ===== TERRACOTTA COLORS (missing ones) =====
    'minecraft:magenta_terracotta':      { color: 0x95586c },
    'minecraft:light_blue_terracotta':   { color: 0x706c8a },
    'minecraft:lime_terracotta':         { color: 0x677534 },
    'minecraft:pink_terracotta':         { color: 0xa14e4e },
    'minecraft:gray_terracotta':         { color: 0x3a2a24 },
    'minecraft:purple_terracotta':       { color: 0x764556 },
    'minecraft:blue_terracotta':         { color: 0x4a3b5b },
    'minecraft:green_terracotta':        { color: 0x4c532a },
    'minecraft:black_terracotta':        { color: 0x251610 },
    'minecraft:cyan_terracotta':         { color: 0x565b5b },
    'minecraft:light_gray_terracotta':   { color: 0x876b62 },

    // ===== ALL 16 GLAZED TERRACOTTA =====
    'minecraft:white_glazed_terracotta':      { color: 0xd6ddd4 },
    'minecraft:orange_glazed_terracotta':     { color: 0x9d5023 },
    'minecraft:magenta_glazed_terracotta':    { color: 0xd24ea1 },
    'minecraft:light_blue_glazed_terracotta': { color: 0x4faac0 },
    'minecraft:yellow_glazed_terracotta':     { color: 0xeac058 },
    'minecraft:lime_glazed_terracotta':       { color: 0x96c93a },
    'minecraft:pink_glazed_terracotta':       { color: 0xeb9ab3 },
    'minecraft:gray_glazed_terracotta':       { color: 0x535a5e },
    'minecraft:light_gray_glazed_terracotta': { color: 0x5d8a8e },
    'minecraft:cyan_glazed_terracotta':       { color: 0x34767e },
    'minecraft:purple_glazed_terracotta':     { color: 0x6a2f82 },
    'minecraft:blue_glazed_terracotta':       { color: 0x2e388d },
    'minecraft:brown_glazed_terracotta':      { color: 0x7b4a30 },
    'minecraft:green_glazed_terracotta':      { color: 0x748432 },
    'minecraft:red_glazed_terracotta':        { color: 0xb53b32 },
    'minecraft:black_glazed_terracotta':      { color: 0x43191e },

    // ===== STAINED GLASS (missing colors) =====
    'minecraft:magenta_stained_glass':     { color: 0xb24cd8, opacity: 0.4 },
    'minecraft:pink_stained_glass':        { color: 0xf27fa5, opacity: 0.4 },
    'minecraft:gray_stained_glass':        { color: 0x4c4c4c, opacity: 0.4 },
    'minecraft:light_gray_stained_glass':  { color: 0x999999, opacity: 0.4 },
    'minecraft:cyan_stained_glass':        { color: 0x4c7f99, opacity: 0.4 },
    'minecraft:purple_stained_glass':      { color: 0x7f3fb2, opacity: 0.4 },
    'minecraft:blue_stained_glass':        { color: 0x334cb2, opacity: 0.4 },
    'minecraft:brown_stained_glass':       { color: 0x664c33, opacity: 0.4 },
    'minecraft:green_stained_glass':       { color: 0x667f33, opacity: 0.4 },
    'minecraft:black_stained_glass':       { color: 0x191919, opacity: 0.4 },

    // ===== FLOWERS & PLANTS =====
    'minecraft:dandelion':            { color: 0xf5e042 },
    'minecraft:poppy':                { color: 0xed302c },
    'minecraft:blue_orchid':          { color: 0x2abffd },
    'minecraft:allium':               { color: 0xb878ed },
    'minecraft:azure_bluet':          { color: 0xf7f7e4 },
    'minecraft:red_tulip':            { color: 0xeb4e32 },
    'minecraft:orange_tulip':         { color: 0xeb7b32 },
    'minecraft:white_tulip':          { color: 0xd6e8e8 },
    'minecraft:pink_tulip':           { color: 0xeb9ec6 },
    'minecraft:oxeye_daisy':          { color: 0xd6e4c0 },
    'minecraft:cornflower':           { color: 0x466aeb },
    'minecraft:lily_of_the_valley':   { color: 0xf0f0f0 },
    'minecraft:wither_rose':          { color: 0x2a2a2a },
    'minecraft:torchflower':          { color: 0xe5a028 },
    'minecraft:short_grass':          { color: 0x5ba839, opacity: 0.8 },
    'minecraft:tall_grass':           { color: 0x5ba839, opacity: 0.8 },
    'minecraft:fern':                 { color: 0x4a8a2e, opacity: 0.8 },
    'minecraft:large_fern':           { color: 0x4a8a2e, opacity: 0.8 },
    'minecraft:dead_bush':            { color: 0x946428 },
    'minecraft:seagrass':             { color: 0x3a8a5a, opacity: 0.7 },
    'minecraft:kelp':                 { color: 0x3a7a4a, opacity: 0.7 },
    'minecraft:sweet_berry_bush':     { color: 0x3a6a2a },
    'minecraft:bamboo':               { color: 0x5a8a2a },
    'minecraft:sugar_cane':           { color: 0x6aaa4a, opacity: 0.8 },
    'minecraft:cactus':               { color: 0x0a7a0a },

    // ===== CORAL BLOCKS =====
    'minecraft:tube_coral_block':        { color: 0x3152a6 },
    'minecraft:brain_coral_block':       { color: 0xcc529b },
    'minecraft:bubble_coral_block':      { color: 0xa50aa1 },
    'minecraft:fire_coral_block':        { color: 0xa32c2e },
    'minecraft:horn_coral_block':        { color: 0xd9c63b },
    'minecraft:dead_tube_coral_block':   { color: 0x7c7c7c },
    'minecraft:dead_brain_coral_block':  { color: 0x7c7070 },
    'minecraft:dead_bubble_coral_block': { color: 0x7c6a6a },
    'minecraft:dead_fire_coral_block':   { color: 0x7c6060 },
    'minecraft:dead_horn_coral_block':   { color: 0x7c7a60 },

    // ===== COPPER VARIANTS =====
    'minecraft:exposed_copper':       { color: 0xa27d5e },
    'minecraft:weathered_copper':     { color: 0x6d916a },
    'minecraft:oxidized_copper':      { color: 0x52a384 },
    'minecraft:waxed_copper_block':   { color: 0xc06840 },
    'minecraft:cut_copper':           { color: 0xbf6536 },
    'minecraft:exposed_cut_copper':   { color: 0x9a7a5a },
    'minecraft:weathered_cut_copper': { color: 0x6a8a6a },
    'minecraft:oxidized_cut_copper':  { color: 0x4a9a7a },

    // ===== DEEPSLATE VARIANTS =====
    'minecraft:chiseled_deepslate':   { color: 0x363636 },
    'minecraft:polished_deepslate':   { color: 0x484848 },
    'minecraft:deepslate_bricks':     { color: 0x464646 },
    'minecraft:deepslate_tiles':      { color: 0x363636 },
    'minecraft:reinforced_deepslate': { color: 0x555555 },

    // ===== QUARTZ VARIANTS =====
    'minecraft:quartz_block':         { color: 0xeae5de },
    'minecraft:quartz_pillar':        { color: 0xeae5de },
    'minecraft:quartz_bricks':        { color: 0xe8e3da },

    // ===== MISC BUILDING BLOCKS =====
    'minecraft:polished_blackstone':        { color: 0x353038 },
    'minecraft:polished_blackstone_bricks': { color: 0x2f2c32 },
    'minecraft:mangrove_roots':       { color: 0x4a3a28 },
    'minecraft:muddy_mangrove_roots': { color: 0x4a3a28 },
    'minecraft:packed_mud':           { color: 0x8e7a5e },
    'minecraft:mud_bricks':           { color: 0x89785e },
    'minecraft:bamboo_planks':        { color: 0xc6b54a },
    'minecraft:bamboo_mosaic':        { color: 0xc0b048 },
    'minecraft:cherry_planks':        { color: 0xe0a8a8 },
    'minecraft:cherry_leaves':        { color: 0xf0b0c0, opacity: 0.85 },

    // ===== STRIPPED LOGS =====
    'minecraft:stripped_oak_log':       { color: 0xb08c4f },
    'minecraft:stripped_spruce_log':    { color: 0x6b5838 },
    'minecraft:stripped_birch_log':     { color: 0xc8b77a },
    'minecraft:stripped_jungle_log':    { color: 0xa47a48 },
    'minecraft:stripped_acacia_log':    { color: 0xb06a3a },
    'minecraft:stripped_dark_oak_log':  { color: 0x604a2a },
    'minecraft:stripped_mangrove_log':  { color: 0x6a3520 },
    'minecraft:stripped_cherry_log':    { color: 0xd9a4a4 },
    'minecraft:stripped_bamboo_block':  { color: 0xc6b54a },
    'minecraft:stripped_crimson_stem':  { color: 0x893e5a },
    'minecraft:stripped_warped_stem':   { color: 0x3a8e7e },

    // ===== NETHER BLOCKS =====
    'minecraft:nether_wart_block':    { color: 0x730000 },
    'minecraft:warped_wart_block':    { color: 0x167a7a },
    'minecraft:shroomlight':          { color: 0xf09848, emissive: 0xf09848 },
    'minecraft:crimson_nylium':       { color: 0xa02020, topColor: 0xa02020 },
    'minecraft:warped_nylium':        { color: 0x2a7a6a, topColor: 0x2a7a6a },
    'minecraft:lodestone':            { color: 0x939393 },
    'minecraft:respawn_anchor':       { color: 0x3a0a4a },
    'minecraft:ancient_debris':       { color: 0x634e3c },
    'minecraft:netherite_block':      { color: 0x3c3c3c },
    'minecraft:raw_iron_block':       { color: 0xbfa98c },
    'minecraft:raw_gold_block':       { color: 0xe8a92e },
    'minecraft:raw_copper_block':     { color: 0xbf6846 },
    'minecraft:amethyst_block':       { color: 0x8c5fbf },
    'minecraft:budding_amethyst':     { color: 0x9468c8 },
    'minecraft:tuff':                 { color: 0x6a6862 },
    'minecraft:calcite':              { color: 0xd8d5ca },
    'minecraft:dripstone_block':      { color: 0x866a5a },
  };

  // Block shapes for non-full-block rendering
  const BLOCK_SHAPES = {};

  // ===== TEXTURE ATLAS SYSTEM =====
  // Loads Minecraft block textures from prismarine-minecraft-data CDN
  // Falls back to vertex colors if loading fails

  const TEXTURE_ATLAS = {
    loaded: false,
    loading: false,
    texture: null,
    material: null,
    atlasWidth: 0,
    atlasHeight: 0,
    blockUVs: {}, // blockName -> { top: [u0,v0,u1,v1], side: [...], bottom: [...] }
  };

  // Map Minecraft block names to their texture file names
  // This covers the most common blocks - unknown blocks fall back to vertex colors
  const BLOCK_TEXTURE_MAP = {
    'minecraft:stone': { all: 'stone' },
    'minecraft:granite': { all: 'granite' },
    'minecraft:polished_granite': { all: 'polished_granite' },
    'minecraft:diorite': { all: 'diorite' },
    'minecraft:polished_diorite': { all: 'polished_diorite' },
    'minecraft:andesite': { all: 'andesite' },
    'minecraft:polished_andesite': { all: 'polished_andesite' },
    'minecraft:deepslate': { top: 'deepslate_top', side: 'deepslate' },
    'minecraft:cobblestone': { all: 'cobblestone' },
    'minecraft:cobbled_deepslate': { all: 'cobbled_deepslate' },
    'minecraft:mossy_cobblestone': { all: 'mossy_cobblestone' },
    'minecraft:stone_bricks': { all: 'stone_bricks' },
    'minecraft:mossy_stone_bricks': { all: 'mossy_stone_bricks' },
    'minecraft:cracked_stone_bricks': { all: 'cracked_stone_bricks' },
    'minecraft:grass_block': { top: 'grass_block_top', side: 'grass_block_side', bottom: 'dirt' },
    'minecraft:dirt': { all: 'dirt' },
    'minecraft:coarse_dirt': { all: 'coarse_dirt' },
    'minecraft:podzol': { top: 'podzol_top', side: 'podzol_side', bottom: 'dirt' },
    'minecraft:rooted_dirt': { all: 'rooted_dirt' },
    'minecraft:mud': { all: 'mud' },
    'minecraft:sand': { all: 'sand' },
    'minecraft:red_sand': { all: 'red_sand' },
    'minecraft:gravel': { all: 'gravel' },
    'minecraft:clay': { all: 'clay' },
    'minecraft:bedrock': { all: 'bedrock' },
    'minecraft:oak_log': { top: 'oak_log_top', side: 'oak_log' },
    'minecraft:spruce_log': { top: 'spruce_log_top', side: 'spruce_log' },
    'minecraft:birch_log': { top: 'birch_log_top', side: 'birch_log' },
    'minecraft:jungle_log': { top: 'jungle_log_top', side: 'jungle_log' },
    'minecraft:acacia_log': { top: 'acacia_log_top', side: 'acacia_log' },
    'minecraft:dark_oak_log': { top: 'dark_oak_log_top', side: 'dark_oak_log' },
    'minecraft:cherry_log': { top: 'cherry_log_top', side: 'cherry_log' },
    'minecraft:mangrove_log': { top: 'mangrove_log_top', side: 'mangrove_log' },
    'minecraft:oak_planks': { all: 'oak_planks' },
    'minecraft:spruce_planks': { all: 'spruce_planks' },
    'minecraft:birch_planks': { all: 'birch_planks' },
    'minecraft:jungle_planks': { all: 'jungle_planks' },
    'minecraft:acacia_planks': { all: 'acacia_planks' },
    'minecraft:dark_oak_planks': { all: 'dark_oak_planks' },
    'minecraft:cherry_planks': { all: 'cherry_planks' },
    'minecraft:mangrove_planks': { all: 'mangrove_planks' },
    'minecraft:oak_leaves': { all: 'oak_leaves' },
    'minecraft:spruce_leaves': { all: 'spruce_leaves' },
    'minecraft:birch_leaves': { all: 'birch_leaves' },
    'minecraft:jungle_leaves': { all: 'jungle_leaves' },
    'minecraft:acacia_leaves': { all: 'acacia_leaves' },
    'minecraft:dark_oak_leaves': { all: 'dark_oak_leaves' },
    'minecraft:cherry_leaves': { all: 'cherry_leaves' },
    'minecraft:azalea_leaves': { all: 'azalea_leaves' },
    'minecraft:glass': { all: 'glass' },
    'minecraft:sandstone': { top: 'sandstone_top', side: 'sandstone', bottom: 'sandstone_bottom' },
    'minecraft:red_sandstone': { top: 'red_sandstone_top', side: 'red_sandstone', bottom: 'red_sandstone_bottom' },
    'minecraft:bricks': { all: 'bricks' },
    'minecraft:bookshelf': { top: 'oak_planks', side: 'bookshelf', bottom: 'oak_planks' },
    'minecraft:obsidian': { all: 'obsidian' },
    'minecraft:crying_obsidian': { all: 'crying_obsidian' },
    'minecraft:netherrack': { all: 'netherrack' },
    'minecraft:soul_sand': { all: 'soul_sand' },
    'minecraft:soul_soil': { all: 'soul_soil' },
    'minecraft:glowstone': { all: 'glowstone' },
    'minecraft:end_stone': { all: 'end_stone' },
    'minecraft:end_stone_bricks': { all: 'end_stone_bricks' },
    'minecraft:purpur_block': { all: 'purpur_block' },
    'minecraft:prismarine': { all: 'prismarine' },
    'minecraft:dark_prismarine': { all: 'dark_prismarine' },
    'minecraft:sea_lantern': { all: 'sea_lantern' },
    'minecraft:iron_block': { all: 'iron_block' },
    'minecraft:gold_block': { all: 'gold_block' },
    'minecraft:diamond_block': { all: 'diamond_block' },
    'minecraft:emerald_block': { all: 'emerald_block' },
    'minecraft:lapis_block': { all: 'lapis_block' },
    'minecraft:redstone_block': { all: 'redstone_block' },
    'minecraft:coal_block': { all: 'coal_block' },
    'minecraft:copper_block': { all: 'copper_block' },
    'minecraft:iron_ore': { all: 'iron_ore' },
    'minecraft:gold_ore': { all: 'gold_ore' },
    'minecraft:diamond_ore': { all: 'diamond_ore' },
    'minecraft:emerald_ore': { all: 'emerald_ore' },
    'minecraft:lapis_ore': { all: 'lapis_ore' },
    'minecraft:redstone_ore': { all: 'redstone_ore' },
    'minecraft:coal_ore': { all: 'coal_ore' },
    'minecraft:copper_ore': { all: 'copper_ore' },
    'minecraft:deepslate_iron_ore': { all: 'deepslate_iron_ore' },
    'minecraft:deepslate_gold_ore': { all: 'deepslate_gold_ore' },
    'minecraft:deepslate_diamond_ore': { all: 'deepslate_diamond_ore' },
    'minecraft:deepslate_emerald_ore': { all: 'deepslate_emerald_ore' },
    'minecraft:deepslate_lapis_ore': { all: 'deepslate_lapis_ore' },
    'minecraft:deepslate_redstone_ore': { all: 'deepslate_redstone_ore' },
    'minecraft:deepslate_coal_ore': { all: 'deepslate_coal_ore' },
    'minecraft:deepslate_copper_ore': { all: 'deepslate_copper_ore' },
    'minecraft:tuff': { all: 'tuff' },
    'minecraft:calcite': { all: 'calcite' },
    'minecraft:amethyst_block': { all: 'amethyst_block' },
    'minecraft:dripstone_block': { all: 'dripstone_block' },
    'minecraft:snow_block': { all: 'snow' },
    'minecraft:ice': { all: 'ice' },
    'minecraft:packed_ice': { all: 'packed_ice' },
    'minecraft:blue_ice': { all: 'blue_ice' },
    'minecraft:mycelium': { top: 'mycelium_top', side: 'mycelium_side', bottom: 'dirt' },
    'minecraft:terracotta': { all: 'terracotta' },
    'minecraft:white_terracotta': { all: 'white_terracotta' },
    'minecraft:orange_terracotta': { all: 'orange_terracotta' },
    'minecraft:brown_terracotta': { all: 'brown_terracotta' },
    'minecraft:red_terracotta': { all: 'red_terracotta' },
    'minecraft:yellow_terracotta': { all: 'yellow_terracotta' },
    'minecraft:white_concrete': { all: 'white_concrete' },
    'minecraft:gray_concrete': { all: 'gray_concrete' },
    'minecraft:black_concrete': { all: 'black_concrete' },
    'minecraft:white_wool': { all: 'white_wool' },
    'minecraft:crafting_table': { top: 'crafting_table_top', side: 'crafting_table_side', bottom: 'oak_planks' },
    'minecraft:furnace': { top: 'furnace_top', side: 'furnace_side', front: 'furnace_front' },
    'minecraft:tnt': { top: 'tnt_top', side: 'tnt_side', bottom: 'tnt_bottom' },
    'minecraft:melon': { top: 'melon_top', side: 'melon_side' },
    'minecraft:pumpkin': { top: 'pumpkin_top', side: 'pumpkin_side' },
    'minecraft:hay_block': { top: 'hay_block_top', side: 'hay_block_side' },
    'minecraft:bone_block': { top: 'bone_block_top', side: 'bone_block_side' },
    'minecraft:quartz_block': { top: 'quartz_block_top', side: 'quartz_block_side', bottom: 'quartz_block_bottom' },
    'minecraft:smooth_quartz': { all: 'quartz_block_bottom' },
    'minecraft:nether_bricks': { all: 'nether_bricks' },
    'minecraft:red_nether_bricks': { all: 'red_nether_bricks' },
    'minecraft:basalt': { top: 'basalt_top', side: 'basalt_side' },
    'minecraft:polished_basalt': { top: 'polished_basalt_top', side: 'polished_basalt_side' },
    'minecraft:blackstone': { top: 'blackstone_top', side: 'blackstone' },
    'minecraft:polished_blackstone': { all: 'polished_blackstone' },
    'minecraft:polished_blackstone_bricks': { all: 'polished_blackstone_bricks' },
    'minecraft:warped_nylium': { top: 'warped_nylium', side: 'warped_nylium_side', bottom: 'netherrack' },
    'minecraft:crimson_nylium': { top: 'crimson_nylium', side: 'crimson_nylium_side', bottom: 'netherrack' },
    'minecraft:warped_stem': { top: 'warped_stem_top', side: 'warped_stem' },
    'minecraft:crimson_stem': { top: 'crimson_stem_top', side: 'crimson_stem' },
    'minecraft:warped_planks': { all: 'warped_planks' },
    'minecraft:crimson_planks': { all: 'crimson_planks' },
    'minecraft:moss_block': { all: 'moss_block' },
    'minecraft:sculk': { all: 'sculk' },
    'minecraft:mud_bricks': { all: 'mud_bricks' },
    'minecraft:bamboo_planks': { all: 'bamboo_planks' },
    // Wool colors
    'minecraft:white_wool': { all: 'white_wool' },
    'minecraft:orange_wool': { all: 'orange_wool' },
    'minecraft:magenta_wool': { all: 'magenta_wool' },
    'minecraft:light_blue_wool': { all: 'light_blue_wool' },
    'minecraft:yellow_wool': { all: 'yellow_wool' },
    'minecraft:lime_wool': { all: 'lime_wool' },
    'minecraft:pink_wool': { all: 'pink_wool' },
    'minecraft:gray_wool': { all: 'gray_wool' },
    'minecraft:light_gray_wool': { all: 'light_gray_wool' },
    'minecraft:cyan_wool': { all: 'cyan_wool' },
    'minecraft:purple_wool': { all: 'purple_wool' },
    'minecraft:blue_wool': { all: 'blue_wool' },
    'minecraft:brown_wool': { all: 'brown_wool' },
    'minecraft:green_wool': { all: 'green_wool' },
    'minecraft:red_wool': { all: 'red_wool' },
    'minecraft:black_wool': { all: 'black_wool' },
    // Concrete colors
    'minecraft:white_concrete': { all: 'white_concrete' },
    'minecraft:orange_concrete': { all: 'orange_concrete' },
    'minecraft:magenta_concrete': { all: 'magenta_concrete' },
    'minecraft:light_blue_concrete': { all: 'light_blue_concrete' },
    'minecraft:yellow_concrete': { all: 'yellow_concrete' },
    'minecraft:lime_concrete': { all: 'lime_concrete' },
    'minecraft:pink_concrete': { all: 'pink_concrete' },
    'minecraft:gray_concrete': { all: 'gray_concrete' },
    'minecraft:light_gray_concrete': { all: 'light_gray_concrete' },
    'minecraft:cyan_concrete': { all: 'cyan_concrete' },
    'minecraft:purple_concrete': { all: 'purple_concrete' },
    'minecraft:blue_concrete': { all: 'blue_concrete' },
    'minecraft:brown_concrete': { all: 'brown_concrete' },
    'minecraft:green_concrete': { all: 'green_concrete' },
    'minecraft:red_concrete': { all: 'red_concrete' },
    'minecraft:black_concrete': { all: 'black_concrete' },
    // Terracotta colors
    'minecraft:terracotta': { all: 'terracotta' },
    'minecraft:white_terracotta': { all: 'white_terracotta' },
    'minecraft:orange_terracotta': { all: 'orange_terracotta' },
    'minecraft:magenta_terracotta': { all: 'magenta_terracotta' },
    'minecraft:light_blue_terracotta': { all: 'light_blue_terracotta' },
    'minecraft:yellow_terracotta': { all: 'yellow_terracotta' },
    'minecraft:lime_terracotta': { all: 'lime_terracotta' },
    'minecraft:pink_terracotta': { all: 'pink_terracotta' },
    'minecraft:gray_terracotta': { all: 'gray_terracotta' },
    'minecraft:light_gray_terracotta': { all: 'light_gray_terracotta' },
    'minecraft:cyan_terracotta': { all: 'cyan_terracotta' },
    'minecraft:purple_terracotta': { all: 'purple_terracotta' },
    'minecraft:blue_terracotta': { all: 'blue_terracotta' },
    'minecraft:brown_terracotta': { all: 'brown_terracotta' },
    'minecraft:green_terracotta': { all: 'green_terracotta' },
    'minecraft:red_terracotta': { all: 'red_terracotta' },
    'minecraft:black_terracotta': { all: 'black_terracotta' },
    // Deepslate variants
    'minecraft:polished_deepslate': { all: 'polished_deepslate' },
    'minecraft:deepslate_bricks': { all: 'deepslate_bricks' },
    'minecraft:deepslate_tiles': { all: 'deepslate_tiles' },
    'minecraft:chiseled_deepslate': { all: 'chiseled_deepslate' },
    // Additional common blocks
    'minecraft:tuff': { all: 'tuff' },
    'minecraft:calcite': { all: 'calcite' },
    'minecraft:amethyst_block': { all: 'amethyst_block' },
    'minecraft:dripstone_block': { all: 'dripstone_block' },
    'minecraft:nether_wart_block': { all: 'nether_wart_block' },
    'minecraft:warped_wart_block': { all: 'warped_wart_block' },
    'minecraft:shroomlight': { all: 'shroomlight' },
    'minecraft:ancient_debris': { top: 'ancient_debris_top', side: 'ancient_debris_side' },
    'minecraft:netherite_block': { all: 'netherite_block' },
    'minecraft:raw_iron_block': { all: 'raw_iron_block' },
    'minecraft:raw_gold_block': { all: 'raw_gold_block' },
    'minecraft:raw_copper_block': { all: 'raw_copper_block' },
    'minecraft:quartz_bricks': { all: 'quartz_bricks' },
    'minecraft:cherry_planks': { all: 'cherry_planks' },
  };

  /**
   * Load texture atlas from prismarine-minecraft-data CDN.
   * Creates individual block textures and builds a combined atlas.
   * @returns {Promise<boolean>} true if loaded successfully
   */
  async function loadTextureAtlas() {
    if (TEXTURE_ATLAS.loaded || TEXTURE_ATLAS.loading) return TEXTURE_ATLAS.loaded;
    TEXTURE_ATLAS.loading = true;

    console.log('[TextureAtlas] Loading Minecraft block textures from CDN...');
    const startTime = performance.now();

    try {
      // Collect all unique texture names we need
      const textureNames = new Set();
      for (const mapping of Object.values(BLOCK_TEXTURE_MAP)) {
        if (mapping.all) textureNames.add(mapping.all);
        if (mapping.top) textureNames.add(mapping.top);
        if (mapping.side) textureNames.add(mapping.side);
        if (mapping.bottom) textureNames.add(mapping.bottom);
        if (mapping.front) textureNames.add(mapping.front);
      }

      const texNames = [...textureNames];
      const TILE_SIZE = 16; // Each MC texture is 16x16
      const ATLAS_COLS = Math.ceil(Math.sqrt(texNames.length));
      const ATLAS_ROWS = Math.ceil(texNames.length / ATLAS_COLS);
      const atlasW = ATLAS_COLS * TILE_SIZE;
      const atlasH = ATLAS_ROWS * TILE_SIZE;

      // Create canvas for atlas
      const canvas = document.createElement('canvas');
      canvas.width = atlasW;
      canvas.height = atlasH;
      const ctx = canvas.getContext('2d');

      // Load textures from prismarine-minecraft-data CDN
      const CDN_BASE = 'https://cdn.jsdelivr.net/npm/minecraft-assets@latest/data/1.20.4/blocks';
      const texCoords = {}; // texName -> { col, row }
      let loadedCount = 0;

      // Load textures in batches
      const BATCH_SIZE = 20;
      for (let b = 0; b < texNames.length; b += BATCH_SIZE) {
        const batch = texNames.slice(b, b + BATCH_SIZE);
        await Promise.all(batch.map((name, batchIdx) => {
          const globalIdx = b + batchIdx;
          const col = globalIdx % ATLAS_COLS;
          const row = Math.floor(globalIdx / ATLAS_COLS);
          texCoords[name] = { col, row };

          return new Promise((resolve) => {
            const img = new Image();
            img.crossOrigin = 'anonymous';
            img.onload = () => {
              ctx.drawImage(img, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
              loadedCount++;
              resolve();
            };
            img.onerror = () => {
              // Draw a solid color fallback from BLOCK_COLORS
              ctx.fillStyle = '#808080';
              ctx.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
              resolve();
            };
            img.src = `${CDN_BASE}/${name}.png`;
          });
        }));
      }

      console.log(`[TextureAtlas] Loaded ${loadedCount}/${texNames.length} textures`);

      // Build UV map for each block
      for (const [blockName, mapping] of Object.entries(BLOCK_TEXTURE_MAP)) {
        const uvs = {};
        const getUV = (texName) => {
          const tc = texCoords[texName];
          if (!tc) return [0, 0, 1/atlasW * TILE_SIZE, 1/atlasH * TILE_SIZE];
          return [
            tc.col * TILE_SIZE / atlasW,
            1.0 - (tc.row + 1) * TILE_SIZE / atlasH, // flip Y for WebGL
            (tc.col + 1) * TILE_SIZE / atlasW,
            1.0 - tc.row * TILE_SIZE / atlasH
          ];
        };

        if (mapping.all) {
          const uv = getUV(mapping.all);
          uvs.top = uv; uvs.bottom = uv; uvs.north = uv;
          uvs.south = uv; uvs.east = uv; uvs.west = uv;
        } else {
          uvs.top = getUV(mapping.top || mapping.side || 'stone');
          uvs.bottom = getUV(mapping.bottom || mapping.top || mapping.side || 'stone');
          const sideUV = getUV(mapping.side || mapping.all || 'stone');
          uvs.north = mapping.front ? getUV(mapping.front) : sideUV;
          uvs.south = sideUV; uvs.east = sideUV; uvs.west = sideUV;
        }
        TEXTURE_ATLAS.blockUVs[blockName] = uvs;
      }

      // Create Three.js texture
      const texture = new THREE.CanvasTexture(canvas);
      texture.magFilter = THREE.NearestFilter;
      texture.minFilter = THREE.NearestFilter;
      texture.colorSpace = THREE.SRGBColorSpace;

      // Create textured material
      const material = new THREE.MeshStandardMaterial({
        map: texture,
        vertexColors: true, // Keep vertex colors for AO shading
        roughness: 0.85,
        metalness: 0.05,
        side: THREE.FrontSide,
      });

      TEXTURE_ATLAS.texture = texture;
      TEXTURE_ATLAS.material = material;
      TEXTURE_ATLAS.atlasWidth = atlasW;
      TEXTURE_ATLAS.atlasHeight = atlasH;
      TEXTURE_ATLAS.loaded = true;
      TEXTURE_ATLAS.loading = false;

      const elapsed = (performance.now() - startTime).toFixed(0);
      console.log(`[TextureAtlas] Atlas ready: ${atlasW}x${atlasH}, ${Object.keys(TEXTURE_ATLAS.blockUVs).length} blocks mapped in ${elapsed}ms`);
      return true;
    } catch (err) {
      console.warn('[TextureAtlas] Failed to load:', err);
      TEXTURE_ATLAS.loading = false;
      return false;
    }
  }

  /**
   * Get UV coordinates for a block face.
   * Returns [u0, v0, u1, v1] or null if not in atlas.
   */
  function getBlockFaceUV(blockState, faceName) {
    if (!TEXTURE_ATLAS.loaded) return null;
    const { name } = parseBlockState(blockState);
    const uvs = TEXTURE_ATLAS.blockUVs[name];
    if (!uvs) return null;
    return uvs[faceName] || uvs.north || null;
  }

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
      } else if (name.includes('_wall')) {
        info = { color: 0x7a7a7a };
      } else if (name.includes('_stairs')) {
        info = { color: 0x7a7a7a };
      } else if (name.includes('_slab')) {
        info = { color: 0x7a7a7a };
      } else if (name.includes('_fence_gate')) {
        info = { color: 0xb8945f };
      } else if (name.includes('_glazed_terracotta')) {
        info = { color: 0x985e43 };
      } else if (name.includes('_terracotta')) {
        info = { color: 0x985e43 };
      } else if (name.includes('_concrete')) {
        info = { color: 0x808080 };
      } else if (name.includes('_wool')) {
        info = { color: 0xe9e9e9 };
      } else if (name.includes('_glass') || name.includes('_glass_pane')) {
        info = { color: 0xc0d8f0, opacity: 0.4 };
      } else if (name.includes('_candle') || name === 'minecraft:candle') {
        info = { color: 0xd8c080 };
      } else if (name.includes('_button') || name.includes('_pressure_plate') || name.includes('_sign') || name.includes('_hanging_sign')) {
        info = { color: 0xb8945f };
      } else if (name.includes('_coral') && !name.includes('block')) {
        info = { color: 0xcc529b, opacity: 0.8 };
      } else if (name.includes('_shulker_box')) {
        info = { color: 0x9a5bba };
      }
    }

    // Default: unknown block = neutral gray
    if (!info) info = { color: 0x808080 };

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
   * When texture atlas is loaded, also generates UV coordinates and disables
   * greedy merge across different block types to preserve per-block texturing.
   * Returns { positions, normals, colors, indices, uvs? }
   */
  function buildSectionMesh(section, chunkX, chunkZ, getNeighborBlock) {
    const positions = [];
    const normals = [];
    const colors = [];
    const uvs = [];
    const indices = [];
    let vertexCount = 0;
    const useAtlas = TEXTURE_ATLAS.loaded;

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
        // When texture atlas is loaded, only merge same block types (1x1 if textured)
        const visited = new Array(256).fill(false);

        for (let j = 0; j < 16; j++) {
          for (let i = 0; i < 16; i++) {
            const idx = j * 16 + i;
            if (visited[idx] || !mask[idx]) continue;

            const color = maskColors[idx];
            const state = mask[idx];
            const faceUV = useAtlas ? getBlockFaceUV(state, face.name) : null;

            // When textured, don't merge (each block needs its own UVs)
            let width = 1;
            let height = 1;
            if (!faceUV) {
              // Vertex-color mode: greedy merge across same-color blocks
              while (i + width < 16) {
                const ni = j * 16 + (i + width);
                if (!visited[ni] && mask[ni] === state && maskColors[ni] === color) {
                  width++;
                } else break;
              }
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

            // Generate UV coordinates if atlas is loaded and block has texture
            if (faceUV) {
              const [u0, v0t, u1, v1t] = faceUV;
              // Map quad corners to UV (4 vertices, same winding)
              uvs.push(u0, v0t, u1, v0t, u1, v1t, u0, v1t);
            } else if (useAtlas) {
              // No texture for this block - use white pixel region (0,0)
              uvs.push(0, 0, 0, 0, 0, 0, 0, 0);
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

    const result = {
      positions: new Float32Array(positions),
      normals: new Float32Array(normals),
      colors: new Float32Array(colors),
      indices: new Uint32Array(indices)
    };
    if (useAtlas && uvs.length > 0) {
      result.uvs = new Float32Array(uvs);
    }
    return result;
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

      // Use textured material when UV data is available
      let material = this.opaqueMaterial;
      if (meshData.uvs && TEXTURE_ATLAS.material) {
        geometry.setAttribute('uv', new THREE.BufferAttribute(meshData.uvs, 2));
        material = TEXTURE_ATLAS.material;
      }

      const mesh = new THREE.Mesh(geometry, material);
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
    loadTextureAtlas,
    TEXTURE_ATLAS,
  };

})();

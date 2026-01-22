/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ public final class Materials {
/*     */   @Generated
/*     */   private Materials() {
/*  20 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  21 */   } private static final Set<StateType> NO_PLACE_LIQUIDS = new HashSet<>();
/*     */   
/*  23 */   private static final Set<StateType> PANES = new HashSet<>();
/*  24 */   private static final Set<StateType> WATER_LIQUIDS = new HashSet<>();
/*  25 */   private static final Set<StateType> WATER_LIQUIDS_LEGACY = new HashSet<>();
/*  26 */   private static final Set<StateType> WATER_SOURCES = new HashSet<>();
/*  27 */   private static final Set<StateType> WATER_SOURCES_LEGACY = new HashSet<>();
/*     */   
/*  29 */   private static final Set<StateType> COPPER_DOORS = new HashSet<>();
/*  30 */   private static final Set<StateType> COPPER_TRAPDOORS = new HashSet<>();
/*     */   
/*  32 */   private static final Set<StateType> CLIENT_SIDE = new HashSet<>();
/*     */ 
/*     */   
/*     */   static {
/*  36 */     WATER_LIQUIDS.add(StateTypes.WATER);
/*  37 */     WATER_LIQUIDS_LEGACY.add(StateTypes.WATER);
/*     */ 
/*     */     
/*  40 */     WATER_LIQUIDS.add(StateTypes.KELP);
/*  41 */     WATER_SOURCES.add(StateTypes.KELP);
/*  42 */     WATER_LIQUIDS.add(StateTypes.KELP_PLANT);
/*  43 */     WATER_SOURCES.add(StateTypes.KELP_PLANT);
/*     */ 
/*     */     
/*  46 */     WATER_SOURCES.add(StateTypes.BUBBLE_COLUMN);
/*  47 */     WATER_LIQUIDS_LEGACY.add(StateTypes.BUBBLE_COLUMN);
/*  48 */     WATER_LIQUIDS.add(StateTypes.BUBBLE_COLUMN);
/*  49 */     WATER_SOURCES_LEGACY.add(StateTypes.BUBBLE_COLUMN);
/*     */ 
/*     */     
/*  52 */     WATER_SOURCES.add(StateTypes.SEAGRASS);
/*  53 */     WATER_LIQUIDS.add(StateTypes.SEAGRASS);
/*     */ 
/*     */     
/*  56 */     WATER_SOURCES.add(StateTypes.TALL_SEAGRASS);
/*  57 */     WATER_LIQUIDS.add(StateTypes.TALL_SEAGRASS);
/*     */     
/*  59 */     NO_PLACE_LIQUIDS.add(StateTypes.WATER);
/*  60 */     NO_PLACE_LIQUIDS.add(StateTypes.LAVA);
/*     */     
/*  62 */     COPPER_DOORS.add(StateTypes.COPPER_DOOR);
/*  63 */     COPPER_DOORS.add(StateTypes.EXPOSED_COPPER_DOOR);
/*  64 */     COPPER_DOORS.add(StateTypes.WEATHERED_COPPER_DOOR);
/*  65 */     COPPER_DOORS.add(StateTypes.OXIDIZED_COPPER_DOOR);
/*  66 */     COPPER_DOORS.add(StateTypes.WAXED_COPPER_DOOR);
/*  67 */     COPPER_DOORS.add(StateTypes.WAXED_EXPOSED_COPPER_DOOR);
/*  68 */     COPPER_DOORS.add(StateTypes.WAXED_WEATHERED_COPPER_DOOR);
/*  69 */     COPPER_DOORS.add(StateTypes.WAXED_OXIDIZED_COPPER_DOOR);
/*     */     
/*  71 */     COPPER_TRAPDOORS.add(StateTypes.COPPER_TRAPDOOR);
/*  72 */     COPPER_TRAPDOORS.add(StateTypes.EXPOSED_COPPER_TRAPDOOR);
/*  73 */     COPPER_TRAPDOORS.add(StateTypes.WEATHERED_COPPER_TRAPDOOR);
/*  74 */     COPPER_TRAPDOORS.add(StateTypes.OXIDIZED_COPPER_TRAPDOOR);
/*  75 */     COPPER_TRAPDOORS.add(StateTypes.WAXED_COPPER_TRAPDOOR);
/*  76 */     COPPER_TRAPDOORS.add(StateTypes.WAXED_EXPOSED_COPPER_TRAPDOOR);
/*  77 */     COPPER_TRAPDOORS.add(StateTypes.WAXED_WEATHERED_COPPER_TRAPDOOR);
/*  78 */     COPPER_TRAPDOORS.add(StateTypes.WAXED_OXIDIZED_COPPER_TRAPDOOR);
/*     */ 
/*     */ 
/*     */     
/*  82 */     CLIENT_SIDE.add(StateTypes.BARREL);
/*  83 */     CLIENT_SIDE.add(StateTypes.BEACON);
/*  84 */     CLIENT_SIDE.add(StateTypes.BREWING_STAND);
/*  85 */     CLIENT_SIDE.add(StateTypes.CARTOGRAPHY_TABLE);
/*  86 */     CLIENT_SIDE.add(StateTypes.CHEST);
/*  87 */     CLIENT_SIDE.add(StateTypes.TRAPPED_CHEST);
/*  88 */     CLIENT_SIDE.add(StateTypes.COMPARATOR);
/*  89 */     CLIENT_SIDE.add(StateTypes.CRAFTING_TABLE);
/*  90 */     CLIENT_SIDE.add(StateTypes.DAYLIGHT_DETECTOR);
/*  91 */     CLIENT_SIDE.add(StateTypes.DISPENSER);
/*  92 */     CLIENT_SIDE.add(StateTypes.DRAGON_EGG);
/*  93 */     CLIENT_SIDE.add(StateTypes.ENCHANTING_TABLE);
/*  94 */     CLIENT_SIDE.add(StateTypes.ENDER_CHEST);
/*  95 */     CLIENT_SIDE.add(StateTypes.GRINDSTONE);
/*  96 */     CLIENT_SIDE.add(StateTypes.HOPPER);
/*  97 */     CLIENT_SIDE.add(StateTypes.LEVER);
/*  98 */     CLIENT_SIDE.add(StateTypes.LIGHT);
/*  99 */     CLIENT_SIDE.add(StateTypes.LOOM);
/* 100 */     CLIENT_SIDE.add(StateTypes.NOTE_BLOCK);
/* 101 */     CLIENT_SIDE.add(StateTypes.REPEATER);
/* 102 */     CLIENT_SIDE.add(StateTypes.SMITHING_TABLE);
/* 103 */     CLIENT_SIDE.add(StateTypes.STONECUTTER);
/* 104 */     CLIENT_SIDE.add(StateTypes.LECTERN);
/* 105 */     CLIENT_SIDE.add(StateTypes.FURNACE);
/* 106 */     CLIENT_SIDE.add(StateTypes.BLAST_FURNACE);
/*     */     
/* 108 */     CLIENT_SIDE.addAll(BlockTags.FENCE_GATES.getStates());
/* 109 */     CLIENT_SIDE.addAll(BlockTags.ANVIL.getStates());
/* 110 */     CLIENT_SIDE.addAll(BlockTags.BEDS.getStates());
/* 111 */     CLIENT_SIDE.addAll(BlockTags.BUTTONS.getStates());
/* 112 */     CLIENT_SIDE.addAll(BlockTags.SHULKER_BOXES.getStates());
/* 113 */     CLIENT_SIDE.addAll(BlockTags.SIGNS.getStates());
/* 114 */     CLIENT_SIDE.addAll(BlockTags.FLOWER_POTS.getStates());
/* 115 */     CLIENT_SIDE.addAll((Collection<? extends StateType>)BlockTags.TRAPDOORS.getStates().stream().filter(type -> (type != StateTypes.IRON_TRAPDOOR)).collect(Collectors.toSet()));
/* 116 */     CLIENT_SIDE.addAll(BlockTags.MOB_INTERACTABLE_DOORS.getStates());
/*     */     
/* 118 */     PANES.addAll(BlockTags.GLASS_PANES.getStates());
/* 119 */     PANES.add(StateTypes.IRON_BARS);
/*     */   }
/*     */   
/*     */   public static boolean isStairs(StateType type) {
/* 123 */     return BlockTags.STAIRS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isSlab(StateType type) {
/* 127 */     return BlockTags.SLABS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isWall(StateType type) {
/* 131 */     return BlockTags.WALLS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isButton(StateType type) {
/* 135 */     return BlockTags.BUTTONS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isFence(StateType type) {
/* 139 */     return BlockTags.FENCES.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isGate(StateType type) {
/* 143 */     return BlockTags.FENCE_GATES.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isBed(StateType type) {
/* 147 */     return BlockTags.BEDS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isAir(StateType type) {
/* 151 */     return type.isAir();
/*     */   }
/*     */   
/*     */   public static boolean isLeaves(StateType type) {
/* 155 */     return BlockTags.LEAVES.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isDoor(StateType type) {
/* 159 */     return BlockTags.DOORS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isShulker(StateType type) {
/* 163 */     return BlockTags.SHULKER_BOXES.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isGlassBlock(StateType type) {
/* 167 */     return BlockTags.GLASS_BLOCKS.contains(type);
/*     */   }
/*     */   
/*     */   public static Set<StateType> getPanes() {
/* 171 */     return new HashSet<>(PANES);
/*     */   }
/*     */   
/*     */   public static boolean isGlassPane(StateType type) {
/* 175 */     return PANES.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isCauldron(StateType type) {
/* 179 */     return BlockTags.CAULDRONS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isWaterModern(StateType type) {
/* 183 */     return WATER_LIQUIDS.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isWaterLegacy(StateType type) {
/* 187 */     return WATER_LIQUIDS_LEGACY.contains(type);
/*     */   }
/*     */   
/*     */   public static boolean isShapeExceedsCube(StateType type) {
/* 191 */     return type.exceedsCube();
/*     */   }
/*     */   
/*     */   public static boolean isUsable(ItemType material) {
/* 195 */     return (material != null && (material.hasAttribute(ItemTypes.ItemAttribute.EDIBLE) || material == ItemTypes.POTION || material == ItemTypes.MILK_BUCKET || material == ItemTypes.CROSSBOW || material == ItemTypes.BOW || material
/* 196 */       .toString().endsWith("SWORD") || material == ItemTypes.TRIDENT || material == ItemTypes.SHIELD));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isWater(ClientVersion clientVersion, WrappedBlockState state) {
/* 201 */     boolean modern = clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13);
/*     */     
/* 203 */     if (modern && isWaterModern(state.getType())) {
/* 204 */       return true;
/*     */     }
/*     */     
/* 207 */     if (!modern && isWaterLegacy(state.getType())) {
/* 208 */       return true;
/*     */     }
/*     */     
/* 211 */     return isWaterlogged(clientVersion, state);
/*     */   }
/*     */   
/*     */   public static boolean isWaterSource(ClientVersion clientVersion, WrappedBlockState state) {
/* 215 */     if (isWaterlogged(clientVersion, state)) {
/* 216 */       return true;
/*     */     }
/* 218 */     if (state.getType() == StateTypes.WATER && state.getLevel() == 0) {
/* 219 */       return true;
/*     */     }
/* 221 */     boolean modern = clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13);
/* 222 */     return modern ? WATER_SOURCES.contains(state.getType()) : WATER_SOURCES_LEGACY.contains(state.getType());
/*     */   }
/*     */   
/*     */   public static boolean isWaterlogged(ClientVersion clientVersion, WrappedBlockState state) {
/* 226 */     if (clientVersion.isOlderThanOrEquals(ClientVersion.V_1_12_2)) return false; 
/* 227 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_13)) {
/* 228 */       return false;
/*     */     }
/* 230 */     StateType type = state.getType();
/*     */ 
/*     */     
/* 233 */     if (clientVersion.isOlderThan(ClientVersion.V_1_16_2) && (type == StateTypes.LANTERN || type == StateTypes.SOUL_LANTERN)) {
/* 234 */       return false;
/*     */     }
/* 236 */     if (clientVersion.isOlderThan(ClientVersion.V_1_17) && type == StateTypes.SMALL_DRIPLEAF) {
/* 237 */       return false;
/*     */     }
/* 239 */     if (clientVersion.isOlderThan(ClientVersion.V_1_17) && BlockTags.RAILS.contains(type)) {
/* 240 */       return false;
/*     */     }
/* 242 */     return ((Boolean)state.getInternalData().getOrDefault(StateValue.WATERLOGGED, Boolean.valueOf(false))).booleanValue();
/*     */   }
/*     */   
/*     */   public static boolean isPlaceableWaterBucket(ItemType mat) {
/* 246 */     return (mat == ItemTypes.AXOLOTL_BUCKET || mat == ItemTypes.COD_BUCKET || mat == ItemTypes.PUFFERFISH_BUCKET || mat == ItemTypes.SALMON_BUCKET || mat == ItemTypes.TROPICAL_FISH_BUCKET || mat == ItemTypes.WATER_BUCKET || mat == ItemTypes.TADPOLE_BUCKET);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StateType transformBucketMaterial(ItemType mat) {
/* 252 */     if (mat == ItemTypes.LAVA_BUCKET) return StateTypes.LAVA; 
/* 253 */     if (isPlaceableWaterBucket(mat)) return StateTypes.WATER; 
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSolidBlockingBlacklist(StateType mat, ClientVersion ver) {
/* 269 */     if (!mat.isBlocking()) return true;
/*     */ 
/*     */     
/* 272 */     if (BlockTags.BANNERS.contains(mat)) {
/* 273 */       return (ver.isNewerThanOrEquals(ClientVersion.V_1_13) && ver.isOlderThan(ClientVersion.V_1_16));
/*     */     }
/* 275 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isAnvil(StateType mat) {
/* 279 */     return BlockTags.ANVIL.contains(mat);
/*     */   }
/*     */   
/*     */   public static boolean isWoodenChest(StateType mat) {
/* 283 */     return (mat == StateTypes.CHEST || mat == StateTypes.TRAPPED_CHEST);
/*     */   }
/*     */   
/*     */   public static boolean isNoPlaceLiquid(StateType material) {
/* 287 */     return NO_PLACE_LIQUIDS.contains(material);
/*     */   }
/*     */   
/*     */   public static boolean isWaterIgnoringWaterlogged(ClientVersion clientVersion, WrappedBlockState state) {
/* 291 */     if (clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13))
/* 292 */       return isWaterModern(state.getType()); 
/* 293 */     return isWaterLegacy(state.getType());
/*     */   }
/*     */   
/*     */   public static boolean isClientSideInteractable(StateType material) {
/* 297 */     return CLIENT_SIDE.contains(material);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isClientSideOpenableDoor(StateType mat, ClientVersion ver) {
/* 302 */     if (!BlockTags.MOB_INTERACTABLE_DOORS.contains(mat)) {
/* 303 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 307 */     if (COPPER_DOORS.contains(mat)) {
/* 308 */       return ver.isNewerThanOrEquals(ClientVersion.V_1_20_3);
/*     */     }
/*     */ 
/*     */     
/* 312 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isClientSideOpenableTrapdoor(StateType mat, ClientVersion ver) {
/* 317 */     if (!BlockTags.TRAPDOORS.contains(mat)) {
/* 318 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 322 */     if (ver.isOlderThan(ClientVersion.V_1_8)) {
/* 323 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 327 */     if (COPPER_TRAPDOORS.contains(mat)) {
/* 328 */       return ver.isNewerThanOrEquals(ClientVersion.V_1_20_3);
/*     */     }
/*     */ 
/*     */     
/* 332 */     return (mat != StateTypes.IRON_TRAPDOOR);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isCompostable(ItemType material) {
/* 337 */     return (ItemTypes.JUNGLE_LEAVES.equals(material) || ItemTypes.OAK_LEAVES.equals(material) || ItemTypes.SPRUCE_LEAVES.equals(material) || ItemTypes.DARK_OAK_LEAVES.equals(material) || ItemTypes.ACACIA_LEAVES.equals(material) || ItemTypes.BIRCH_LEAVES.equals(material) || ItemTypes.AZALEA_LEAVES.equals(material) || ItemTypes.OAK_SAPLING.equals(material) || ItemTypes.SPRUCE_SAPLING.equals(material) || ItemTypes.BIRCH_SAPLING.equals(material) || ItemTypes.JUNGLE_SAPLING.equals(material) || ItemTypes.ACACIA_SAPLING.equals(material) || ItemTypes.DARK_OAK_SAPLING.equals(material) || ItemTypes.BEETROOT_SEEDS.equals(material) || ItemTypes.DRIED_KELP.equals(material) || ItemTypes.SHORT_GRASS.equals(material) || ItemTypes.KELP.equals(material) || ItemTypes.MELON_SEEDS.equals(material) || ItemTypes.PUMPKIN_SEEDS.equals(material) || ItemTypes.SEAGRASS.equals(material) || ItemTypes.SWEET_BERRIES.equals(material) || ItemTypes.GLOW_BERRIES.equals(material) || ItemTypes.WHEAT_SEEDS.equals(material) || ItemTypes.MOSS_CARPET.equals(material) || ItemTypes.SMALL_DRIPLEAF.equals(material) || ItemTypes.HANGING_ROOTS.equals(material) || ItemTypes.DRIED_KELP_BLOCK.equals(material) || ItemTypes.TALL_GRASS.equals(material) || ItemTypes.AZALEA.equals(material) || ItemTypes.CACTUS.equals(material) || ItemTypes.SUGAR_CANE.equals(material) || ItemTypes.VINE.equals(material) || ItemTypes.NETHER_SPROUTS.equals(material) || ItemTypes.WEEPING_VINES.equals(material) || ItemTypes.TWISTING_VINES.equals(material) || ItemTypes.MELON_SLICE.equals(material) || ItemTypes.GLOW_LICHEN.equals(material) || ItemTypes.SEA_PICKLE.equals(material) || ItemTypes.LILY_PAD.equals(material) || ItemTypes.PUMPKIN.equals(material) || ItemTypes.CARVED_PUMPKIN.equals(material) || ItemTypes.MELON.equals(material) || ItemTypes.APPLE.equals(material) || ItemTypes.BEETROOT.equals(material) || ItemTypes.CARROT.equals(material) || ItemTypes.COCOA_BEANS.equals(material) || ItemTypes.POTATO.equals(material) || ItemTypes.WHEAT.equals(material) || ItemTypes.BROWN_MUSHROOM.equals(material) || ItemTypes.RED_MUSHROOM.equals(material) || ItemTypes.MUSHROOM_STEM.equals(material) || ItemTypes.CRIMSON_FUNGUS.equals(material) || ItemTypes.WARPED_FUNGUS.equals(material) || ItemTypes.NETHER_WART.equals(material) || ItemTypes.CRIMSON_ROOTS.equals(material) || ItemTypes.WARPED_ROOTS.equals(material) || ItemTypes.SHROOMLIGHT.equals(material) || ItemTypes.DANDELION.equals(material) || ItemTypes.POPPY.equals(material) || ItemTypes.BLUE_ORCHID.equals(material) || ItemTypes.ALLIUM.equals(material) || ItemTypes.AZURE_BLUET.equals(material) || ItemTypes.RED_TULIP.equals(material) || ItemTypes.ORANGE_TULIP.equals(material) || ItemTypes.WHITE_TULIP.equals(material) || ItemTypes.PINK_TULIP.equals(material) || ItemTypes.OXEYE_DAISY.equals(material) || ItemTypes.CORNFLOWER.equals(material) || ItemTypes.LILY_OF_THE_VALLEY.equals(material) || ItemTypes.WITHER_ROSE.equals(material) || ItemTypes.FERN.equals(material) || ItemTypes.SUNFLOWER.equals(material) || ItemTypes.LILAC.equals(material) || ItemTypes.ROSE_BUSH.equals(material) || ItemTypes.PEONY.equals(material) || ItemTypes.LARGE_FERN.equals(material) || ItemTypes.SPORE_BLOSSOM.equals(material) || ItemTypes.MOSS_BLOCK.equals(material) || ItemTypes.BIG_DRIPLEAF.equals(material) || ItemTypes.HAY_BLOCK.equals(material) || ItemTypes.BROWN_MUSHROOM_BLOCK.equals(material) || ItemTypes.RED_MUSHROOM_BLOCK.equals(material) || ItemTypes.NETHER_WART_BLOCK.equals(material) || ItemTypes.WARPED_WART_BLOCK.equals(material) || ItemTypes.FLOWERING_AZALEA.equals(material) || ItemTypes.BREAD.equals(material) || ItemTypes.BAKED_POTATO.equals(material) || ItemTypes.COOKIE.equals(material) || ItemTypes.CAKE.equals(material) || ItemTypes.PUMPKIN_PIE.equals(material));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\Materials.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
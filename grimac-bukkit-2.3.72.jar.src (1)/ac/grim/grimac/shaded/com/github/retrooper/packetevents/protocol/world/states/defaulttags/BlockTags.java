/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ public class BlockTags
/*     */ {
/*  41 */   private static final HashMap<String, BlockTags> byName = new HashMap<>();
/*     */   
/*  43 */   public static final BlockTags WOOL = bind("wool");
/*  44 */   public static final BlockTags PLANKS = bind("planks");
/*  45 */   public static final BlockTags STONE_BRICKS = bind("stone_bricks");
/*  46 */   public static final BlockTags WOODEN_BUTTONS = bind("wooden_buttons");
/*  47 */   public static final BlockTags STONE_BUTTONS = bind("stone_buttons");
/*  48 */   public static final BlockTags BUTTONS = bind("buttons");
/*  49 */   public static final BlockTags WOOL_CARPETS = bind("wool_carpets");
/*  50 */   public static final BlockTags WOODEN_DOORS = bind("wooden_doors");
/*  51 */   public static final BlockTags WOODEN_STAIRS = bind("wooden_stairs");
/*  52 */   public static final BlockTags WOODEN_SLABS = bind("wooden_slabs");
/*  53 */   public static final BlockTags WOODEN_FENCES = bind("wooden_fences");
/*  54 */   public static final BlockTags PRESSURE_PLATES = bind("pressure_plates");
/*  55 */   public static final BlockTags WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
/*  56 */   public static final BlockTags STONE_PRESSURE_PLATES = bind("stone_pressure_plates");
/*  57 */   public static final BlockTags WOODEN_TRAPDOORS = bind("wooden_trapdoors");
/*  58 */   public static final BlockTags DOORS = bind("doors");
/*  59 */   public static final BlockTags SAPLINGS = bind("saplings");
/*  60 */   public static final BlockTags LOGS_THAT_BURN = bind("logs_that_burn");
/*  61 */   public static final BlockTags OVERWORLD_NATURAL_LOGS = bind("overworld_natural_logs");
/*  62 */   public static final BlockTags LOGS = bind("logs");
/*  63 */   public static final BlockTags DARK_OAK_LOGS = bind("dark_oak_logs");
/*  64 */   public static final BlockTags OAK_LOGS = bind("oak_logs");
/*  65 */   public static final BlockTags BIRCH_LOGS = bind("birch_logs");
/*  66 */   public static final BlockTags ACACIA_LOGS = bind("acacia_logs");
/*  67 */   public static final BlockTags CHERRY_LOGS = bind("cherry_logs");
/*  68 */   public static final BlockTags JUNGLE_LOGS = bind("jungle_logs");
/*  69 */   public static final BlockTags SPRUCE_LOGS = bind("spruce_logs");
/*  70 */   public static final BlockTags MANGROVE_LOGS = bind("mangrove_logs");
/*  71 */   public static final BlockTags CRIMSON_STEMS = bind("crimson_stems");
/*  72 */   public static final BlockTags WARPED_STEMS = bind("warped_stems");
/*  73 */   public static final BlockTags BAMBOO_BLOCKS = bind("bamboo_blocks");
/*  74 */   public static final BlockTags WART_BLOCKS = bind("wart_blocks");
/*  75 */   public static final BlockTags BANNERS = bind("banners");
/*  76 */   public static final BlockTags SAND = bind("sand");
/*  77 */   public static final BlockTags SMELTS_TO_GLASS = bind("smelts_to_glass");
/*  78 */   public static final BlockTags STAIRS = bind("stairs");
/*  79 */   public static final BlockTags SLABS = bind("slabs");
/*  80 */   public static final BlockTags WALLS = bind("walls");
/*  81 */   public static final BlockTags ANVIL = bind("anvil");
/*  82 */   public static final BlockTags RAILS = bind("rails");
/*  83 */   public static final BlockTags LEAVES = bind("leaves");
/*  84 */   public static final BlockTags TRAPDOORS = bind("trapdoors");
/*  85 */   public static final BlockTags SMALL_FLOWERS = bind("small_flowers");
/*  86 */   public static final BlockTags BEDS = bind("beds");
/*  87 */   public static final BlockTags FENCES = bind("fences");
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*  92 */   public static final BlockTags TALL_FLOWERS = bind("tall_flowers");
/*  93 */   public static final BlockTags FLOWERS = bind("flowers");
/*  94 */   public static final BlockTags PIGLIN_REPELLENTS = bind("piglin_repellents");
/*  95 */   public static final BlockTags GOLD_ORES = bind("gold_ores");
/*  96 */   public static final BlockTags IRON_ORES = bind("iron_ores");
/*  97 */   public static final BlockTags DIAMOND_ORES = bind("diamond_ores");
/*  98 */   public static final BlockTags REDSTONE_ORES = bind("redstone_ores");
/*  99 */   public static final BlockTags LAPIS_ORES = bind("lapis_ores");
/* 100 */   public static final BlockTags COAL_ORES = bind("coal_ores");
/* 101 */   public static final BlockTags EMERALD_ORES = bind("emerald_ores");
/* 102 */   public static final BlockTags COPPER_ORES = bind("copper_ores");
/* 103 */   public static final BlockTags CANDLES = bind("candles");
/* 104 */   public static final BlockTags DIRT = bind("dirt");
/* 105 */   public static final BlockTags TERRACOTTA = bind("terracotta");
/* 106 */   public static final BlockTags CONCRETE_POWDER = bind("concrete_powder");
/* 107 */   public static final BlockTags COMPLETES_FIND_TREE_TUTORIAL = bind("completes_find_tree_tutorial");
/* 108 */   public static final BlockTags FLOWER_POTS = bind("flower_pots");
/* 109 */   public static final BlockTags ENDERMAN_HOLDABLE = bind("enderman_holdable");
/* 110 */   public static final BlockTags ICE = bind("ice");
/* 111 */   public static final BlockTags VALID_SPAWN = bind("valid_spawn");
/* 112 */   public static final BlockTags IMPERMEABLE = bind("impermeable");
/* 113 */   public static final BlockTags UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
/* 114 */   public static final BlockTags CORAL_BLOCKS = bind("coral_blocks");
/* 115 */   public static final BlockTags WALL_CORALS = bind("wall_corals");
/* 116 */   public static final BlockTags CORAL_PLANTS = bind("coral_plants");
/* 117 */   public static final BlockTags CORALS = bind("corals");
/* 118 */   public static final BlockTags BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
/* 119 */   public static final BlockTags STANDING_SIGNS = bind("standing_signs");
/* 120 */   public static final BlockTags WALL_SIGNS = bind("wall_signs");
/* 121 */   public static final BlockTags SIGNS = bind("signs");
/* 122 */   public static final BlockTags CEILING_HANGING_SIGNS = bind("ceiling_hanging_signs");
/* 123 */   public static final BlockTags WALL_HANGING_SIGNS = bind("wall_hanging_signs");
/* 124 */   public static final BlockTags ALL_HANGING_SIGNS = bind("all_hanging_signs");
/* 125 */   public static final BlockTags ALL_SIGNS = bind("all_signs");
/* 126 */   public static final BlockTags DRAGON_IMMUNE = bind("dragon_immune");
/* 127 */   public static final BlockTags DRAGON_TRANSPARENT = bind("dragon_transparent");
/* 128 */   public static final BlockTags WITHER_IMMUNE = bind("wither_immune");
/* 129 */   public static final BlockTags WITHER_SUMMON_BASE_BLOCKS = bind("wither_summon_base_blocks");
/* 130 */   public static final BlockTags BEEHIVES = bind("beehives");
/* 131 */   public static final BlockTags CROPS = bind("crops");
/* 132 */   public static final BlockTags BEE_GROWABLES = bind("bee_growables");
/* 133 */   public static final BlockTags PORTALS = bind("portals");
/* 134 */   public static final BlockTags FIRE = bind("fire");
/* 135 */   public static final BlockTags NYLIUM = bind("nylium");
/* 136 */   public static final BlockTags BEACON_BASE_BLOCKS = bind("beacon_base_blocks");
/* 137 */   public static final BlockTags SOUL_SPEED_BLOCKS = bind("soul_speed_blocks");
/* 138 */   public static final BlockTags WALL_POST_OVERRIDE = bind("wall_post_override");
/* 139 */   public static final BlockTags CLIMBABLE = bind("climbable");
/* 140 */   public static final BlockTags FALL_DAMAGE_RESETTING = bind("fall_damage_resetting");
/* 141 */   public static final BlockTags SHULKER_BOXES = bind("shulker_boxes");
/* 142 */   public static final BlockTags HOGLIN_REPELLENTS = bind("hoglin_repellents");
/* 143 */   public static final BlockTags SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
/* 144 */   public static final BlockTags STRIDER_WARM_BLOCKS = bind("strider_warm_blocks");
/* 145 */   public static final BlockTags CAMPFIRES = bind("campfires");
/* 146 */   public static final BlockTags GUARDED_BY_PIGLINS = bind("guarded_by_piglins");
/* 147 */   public static final BlockTags PREVENT_MOB_SPAWNING_INSIDE = bind("prevent_mob_spawning_inside");
/* 148 */   public static final BlockTags FENCE_GATES = bind("fence_gates");
/* 149 */   public static final BlockTags UNSTABLE_BOTTOM_CENTER = bind("unstable_bottom_center");
/* 150 */   public static final BlockTags MUSHROOM_GROW_BLOCK = bind("mushroom_grow_block");
/* 151 */   public static final BlockTags INFINIBURN_OVERWORLD = bind("infiniburn_overworld");
/* 152 */   public static final BlockTags INFINIBURN_NETHER = bind("infiniburn_nether");
/* 153 */   public static final BlockTags INFINIBURN_END = bind("infiniburn_end");
/* 154 */   public static final BlockTags BASE_STONE_OVERWORLD = bind("base_stone_overworld");
/* 155 */   public static final BlockTags STONE_ORE_REPLACEABLES = bind("stone_ore_replaceables");
/* 156 */   public static final BlockTags DEEPSLATE_ORE_REPLACEABLES = bind("deepslate_ore_replaceables");
/* 157 */   public static final BlockTags BASE_STONE_NETHER = bind("base_stone_nether");
/* 158 */   public static final BlockTags OVERWORLD_CARVER_REPLACEABLES = bind("overworld_carver_replaceables");
/* 159 */   public static final BlockTags NETHER_CARVER_REPLACEABLES = bind("nether_carver_replaceables");
/* 160 */   public static final BlockTags CANDLE_CAKES = bind("candle_cakes");
/* 161 */   public static final BlockTags CAULDRONS = bind("cauldrons");
/* 162 */   public static final BlockTags CRYSTAL_SOUND_BLOCKS = bind("crystal_sound_blocks");
/* 163 */   public static final BlockTags INSIDE_STEP_SOUND_BLOCKS = bind("inside_step_sound_blocks");
/* 164 */   public static final BlockTags COMBINATION_STEP_SOUND_BLOCKS = bind("combination_step_sound_blocks");
/* 165 */   public static final BlockTags CAMEL_SAND_STEP_SOUND_BLOCKS = bind("camel_sand_step_sound_blocks");
/* 166 */   public static final BlockTags OCCLUDES_VIBRATION_SIGNALS = bind("occludes_vibration_signals");
/* 167 */   public static final BlockTags DAMPENS_VIBRATIONS = bind("dampens_vibrations");
/* 168 */   public static final BlockTags DRIPSTONE_REPLACEABLE_BLOCKS = bind("dripstone_replaceable_blocks");
/*     */   @Deprecated
/* 170 */   public static final BlockTags DRIPSTONE_REPLACEABLE = DRIPSTONE_REPLACEABLE_BLOCKS;
/* 171 */   public static final BlockTags CAVE_VINES = bind("cave_vines");
/* 172 */   public static final BlockTags MOSS_REPLACEABLE = bind("moss_replaceable");
/* 173 */   public static final BlockTags LUSH_GROUND_REPLACEABLE = bind("lush_ground_replaceable");
/* 174 */   public static final BlockTags AZALEA_ROOT_REPLACEABLE = bind("azalea_root_replaceable");
/* 175 */   public static final BlockTags SMALL_DRIPLEAF_PLACEABLE = bind("small_dripleaf_placeable");
/* 176 */   public static final BlockTags BIG_DRIPLEAF_PLACEABLE = bind("big_dripleaf_placeable");
/* 177 */   public static final BlockTags SNOW = bind("snow");
/* 178 */   public static final BlockTags MINEABLE_AXE = bind("mineable/axe");
/* 179 */   public static final BlockTags MINEABLE_HOE = bind("mineable/hoe");
/* 180 */   public static final BlockTags MINEABLE_PICKAXE = bind("mineable/pickaxe");
/* 181 */   public static final BlockTags MINEABLE_SHOVEL = bind("mineable/shovel");
/*     */   @Deprecated
/* 183 */   public static final BlockTags MINEABLE_WITH_AXE = MINEABLE_AXE;
/*     */   @Deprecated
/* 185 */   public static final BlockTags MINEABLE_WITH_HOE = MINEABLE_HOE;
/*     */   @Deprecated
/* 187 */   public static final BlockTags MINEABLE_WITH_PICKAXE = MINEABLE_PICKAXE;
/*     */   @Deprecated
/* 189 */   public static final BlockTags MINEABLE_WITH_SHOVEL = MINEABLE_SHOVEL;
/* 190 */   public static final BlockTags SWORD_EFFICIENT = bind("sword_efficient");
/* 191 */   public static final BlockTags NEEDS_DIAMOND_TOOL = bind("needs_diamond_tool");
/* 192 */   public static final BlockTags NEEDS_IRON_TOOL = bind("needs_iron_tool");
/* 193 */   public static final BlockTags NEEDS_STONE_TOOL = bind("needs_stone_tool");
/* 194 */   public static final BlockTags FEATURES_CANNOT_REPLACE = bind("features_cannot_replace");
/* 195 */   public static final BlockTags LAVA_POOL_STONE_CANNOT_REPLACE = bind("lava_pool_stone_cannot_replace");
/* 196 */   public static final BlockTags GEODE_INVALID_BLOCKS = bind("geode_invalid_blocks");
/* 197 */   public static final BlockTags FROG_PREFER_JUMP_TO = bind("frog_prefer_jump_to");
/* 198 */   public static final BlockTags SCULK_REPLACEABLE = bind("sculk_replaceable");
/* 199 */   public static final BlockTags SCULK_REPLACEABLE_WORLD_GEN = bind("sculk_replaceable_world_gen");
/* 200 */   public static final BlockTags ANCIENT_CITY_REPLACEABLE = bind("ancient_city_replaceable");
/* 201 */   public static final BlockTags VIBRATION_RESONATORS = bind("vibration_resonators");
/* 202 */   public static final BlockTags ANIMALS_SPAWNABLE_ON = bind("animals_spawnable_on");
/* 203 */   public static final BlockTags AXOLOTLS_SPAWNABLE_ON = bind("axolotls_spawnable_on");
/* 204 */   public static final BlockTags GOATS_SPAWNABLE_ON = bind("goats_spawnable_on");
/* 205 */   public static final BlockTags MOOSHROOMS_SPAWNABLE_ON = bind("mooshrooms_spawnable_on");
/* 206 */   public static final BlockTags PARROTS_SPAWNABLE_ON = bind("parrots_spawnable_on");
/* 207 */   public static final BlockTags POLAR_BEARS_SPAWNABLE_ON_ALTERNATE = bind("polar_bears_spawnable_on_alternate");
/* 208 */   public static final BlockTags RABBITS_SPAWNABLE_ON = bind("rabbits_spawnable_on");
/* 209 */   public static final BlockTags FOXES_SPAWNABLE_ON = bind("foxes_spawnable_on");
/* 210 */   public static final BlockTags WOLVES_SPAWNABLE_ON = bind("wolves_spawnable_on");
/* 211 */   public static final BlockTags FROGS_SPAWNABLE_ON = bind("frogs_spawnable_on");
/* 212 */   public static final BlockTags AZALEA_GROWS_ON = bind("azalea_grows_on");
/*     */   @Deprecated
/* 214 */   public static final BlockTags REPLACEABLE_PLANTS = bind("replaceable_plants");
/* 215 */   public static final BlockTags CONVERTABLE_TO_MUD = bind("convertable_to_mud");
/* 216 */   public static final BlockTags MANGROVE_LOGS_CAN_GROW_THROUGH = bind("mangrove_logs_can_grow_through");
/* 217 */   public static final BlockTags MANGROVE_ROOTS_CAN_GROW_THROUGH = bind("mangrove_roots_can_grow_through");
/* 218 */   public static final BlockTags DRY_VEGETATION_MAY_PLACE_ON = bind("dry_vegetation_may_place_on");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 223 */   public static final BlockTags DEAD_BUSH_MAY_PLACE_ON = DRY_VEGETATION_MAY_PLACE_ON;
/* 224 */   public static final BlockTags SNAPS_GOAT_HORN = bind("snaps_goat_horn");
/* 225 */   public static final BlockTags REPLACEABLE_BY_TREES = bind("replaceable_by_trees");
/* 226 */   public static final BlockTags SNOW_LAYER_CANNOT_SURVIVE_ON = bind("snow_layer_cannot_survive_on");
/* 227 */   public static final BlockTags SNOW_LAYER_CAN_SURVIVE_ON = bind("snow_layer_can_survive_on");
/* 228 */   public static final BlockTags INVALID_SPAWN_INSIDE = bind("invalid_spawn_inside");
/* 229 */   public static final BlockTags SNIFFER_DIGGABLE_BLOCK = bind("sniffer_diggable_block");
/* 230 */   public static final BlockTags SNIFFER_EGG_HATCH_BOOST = bind("sniffer_egg_hatch_boost");
/* 231 */   public static final BlockTags TRAIL_RUINS_REPLACEABLE = bind("trail_ruins_replaceable");
/* 232 */   public static final BlockTags REPLACEABLE = bind("replaceable");
/* 233 */   public static final BlockTags ENCHANTMENT_POWER_PROVIDER = bind("enchantment_power_provider");
/* 234 */   public static final BlockTags ENCHANTMENT_POWER_TRANSMITTER = bind("enchantment_power_transmitter");
/* 235 */   public static final BlockTags MAINTAINS_FARMLAND = bind("maintains_farmland");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 240 */   public static final BlockTags ARMADILLO_SPAWNABLE_ON = bind("armadillo_spawnable_on");
/*     */ 
/*     */ 
/*     */   
/* 244 */   public static final BlockTags BADLANDS_TERRACOTTA = bind("badlands_terracotta");
/*     */ 
/*     */ 
/*     */   
/* 248 */   public static final BlockTags BLOCKS_WIND_CHARGE_EXPLOSIONS = bind("blocks_wind_charge_explosions");
/*     */ 
/*     */ 
/*     */   
/* 252 */   public static final BlockTags DOES_NOT_BLOCK_HOPPERS = bind("does_not_block_hoppers");
/*     */ 
/*     */ 
/*     */   
/* 256 */   public static final BlockTags INCORRECT_FOR_DIAMOND_TOOL = bind("incorrect_for_diamond_tool");
/*     */ 
/*     */ 
/*     */   
/* 260 */   public static final BlockTags INCORRECT_FOR_GOLD_TOOL = bind("incorrect_for_gold_tool");
/*     */ 
/*     */ 
/*     */   
/* 264 */   public static final BlockTags INCORRECT_FOR_IRON_TOOL = bind("incorrect_for_iron_tool");
/*     */ 
/*     */ 
/*     */   
/* 268 */   public static final BlockTags INCORRECT_FOR_NETHERITE_TOOL = bind("incorrect_for_netherite_tool");
/*     */ 
/*     */ 
/*     */   
/* 272 */   public static final BlockTags INCORRECT_FOR_STONE_TOOL = bind("incorrect_for_stone_tool");
/*     */ 
/*     */ 
/*     */   
/* 276 */   public static final BlockTags INCORRECT_FOR_WOODEN_TOOL = bind("incorrect_for_wooden_tool");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 281 */   public static final BlockTags PALE_OAK_LOGS = bind("pale_oak_logs");
/*     */ 
/*     */ 
/*     */   
/* 285 */   public static final BlockTags AIR = bind("air");
/*     */ 
/*     */ 
/*     */   
/* 289 */   public static final BlockTags MOB_INTERACTABLE_DOORS = bind("mob_interactable_doors");
/*     */ 
/*     */ 
/*     */   
/* 293 */   public static final BlockTags BATS_SPAWNABLE_ON = bind("bats_spawnable_on");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 298 */   public static final BlockTags BEE_ATTRACTIVE = bind("bats_spawnable_on");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 303 */   public static final BlockTags EDIBLE_FOR_SHEEP = bind("edible_for_sheep");
/*     */ 
/*     */ 
/*     */   
/* 307 */   public static final BlockTags SWORD_INSTANTLY_MINES = bind("sword_instantly_mines");
/*     */ 
/*     */ 
/*     */   
/* 311 */   public static final BlockTags CAMELS_SPAWNABLE_ON = bind("camels_spawnable_on");
/*     */ 
/*     */ 
/*     */   
/* 315 */   public static final BlockTags REPLACEABLE_BY_MUSHROOMS = bind("replaceable_by_mushrooms");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 320 */   public static final BlockTags HAPPY_GHAST_AVOIDS = bind("happy_ghast_avoids");
/*     */ 
/*     */ 
/*     */   
/* 324 */   public static final BlockTags TRIGGERS_AMBIENT_DESERT_SAND_BLOCK_SOUNDS = bind("triggers_ambient_desert_sand_block_sounds");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 329 */   public static final BlockTags PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS = TRIGGERS_AMBIENT_DESERT_SAND_BLOCK_SOUNDS;
/*     */ 
/*     */ 
/*     */   
/* 333 */   public static final BlockTags TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS = bind("triggers_ambient_dried_ghast_block_sounds");
/*     */ 
/*     */ 
/*     */   
/* 337 */   public static final BlockTags TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS = bind("triggers_ambient_desert_dry_vegetation_block_sounds");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 342 */   public static final BlockTags GLASS_BLOCKS = bind("glass_blocks");
/*     */ 
/*     */ 
/*     */   
/* 346 */   public static final BlockTags GLASS_PANES = bind("glass_panes");
/*     */ 
/*     */ 
/*     */   
/* 350 */   public static final BlockTags ALL_CORAL_PLANTS = bind("alive_coral_plants");
/*     */ 
/*     */ 
/*     */   
/* 354 */   public static final BlockTags DEAD_CORAL_PLANTS = bind("dead_coral_plants");
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/* 360 */   public static final BlockTags V_1_20_5 = bind("V_1_20_5");
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/* 366 */   public static final BlockTags V_1_21_2 = bind("V_1_21_2");
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/* 372 */   public static final BlockTags V_1_21_4 = bind("V_1_21_4");
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/* 378 */   public static final BlockTags V_1_21_5 = bind("V_1_21_5");
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @Internal
/* 384 */   public static final BlockTags V_1_21_6 = bind("V_1_21_6");
/*     */   
/*     */   static {
/* 387 */     WOOL.add(new StateType[] { StateTypes.WHITE_WOOL, StateTypes.ORANGE_WOOL, StateTypes.MAGENTA_WOOL, StateTypes.LIGHT_BLUE_WOOL, StateTypes.YELLOW_WOOL, StateTypes.LIME_WOOL, StateTypes.PINK_WOOL, StateTypes.GRAY_WOOL, StateTypes.LIGHT_GRAY_WOOL, StateTypes.CYAN_WOOL, StateTypes.PURPLE_WOOL, StateTypes.BLUE_WOOL, StateTypes.BROWN_WOOL, StateTypes.GREEN_WOOL, StateTypes.RED_WOOL, StateTypes.BLACK_WOOL });
/* 388 */     PLANKS.add(new StateType[] { StateTypes.OAK_PLANKS, StateTypes.SPRUCE_PLANKS, StateTypes.BIRCH_PLANKS, StateTypes.JUNGLE_PLANKS, StateTypes.ACACIA_PLANKS, StateTypes.DARK_OAK_PLANKS, StateTypes.PALE_OAK_PLANKS, StateTypes.CRIMSON_PLANKS, StateTypes.WARPED_PLANKS, StateTypes.MANGROVE_PLANKS, StateTypes.BAMBOO_PLANKS, StateTypes.CHERRY_PLANKS });
/* 389 */     STONE_BRICKS.add(new StateType[] { StateTypes.STONE_BRICKS, StateTypes.MOSSY_STONE_BRICKS, StateTypes.CRACKED_STONE_BRICKS, StateTypes.CHISELED_STONE_BRICKS });
/* 390 */     WOODEN_BUTTONS.add(new StateType[] { StateTypes.OAK_BUTTON, StateTypes.SPRUCE_BUTTON, StateTypes.BIRCH_BUTTON, StateTypes.JUNGLE_BUTTON, StateTypes.ACACIA_BUTTON, StateTypes.DARK_OAK_BUTTON, StateTypes.PALE_OAK_BUTTON, StateTypes.CRIMSON_BUTTON, StateTypes.WARPED_BUTTON, StateTypes.MANGROVE_BUTTON, StateTypes.BAMBOO_BUTTON, StateTypes.CHERRY_BUTTON });
/* 391 */     STONE_BUTTONS.add(new StateType[] { StateTypes.STONE_BUTTON, StateTypes.POLISHED_BLACKSTONE_BUTTON });
/* 392 */     WOOL_CARPETS.add(new StateType[] { StateTypes.WHITE_CARPET, StateTypes.ORANGE_CARPET, StateTypes.MAGENTA_CARPET, StateTypes.LIGHT_BLUE_CARPET, StateTypes.YELLOW_CARPET, StateTypes.LIME_CARPET, StateTypes.PINK_CARPET, StateTypes.GRAY_CARPET, StateTypes.LIGHT_GRAY_CARPET, StateTypes.CYAN_CARPET, StateTypes.PURPLE_CARPET, StateTypes.BLUE_CARPET, StateTypes.BROWN_CARPET, StateTypes.GREEN_CARPET, StateTypes.RED_CARPET, StateTypes.BLACK_CARPET });
/* 393 */     WOODEN_DOORS.add(new StateType[] { StateTypes.OAK_DOOR, StateTypes.SPRUCE_DOOR, StateTypes.BIRCH_DOOR, StateTypes.JUNGLE_DOOR, StateTypes.ACACIA_DOOR, StateTypes.DARK_OAK_DOOR, StateTypes.PALE_OAK_DOOR, StateTypes.CRIMSON_DOOR, StateTypes.WARPED_DOOR, StateTypes.MANGROVE_DOOR, StateTypes.BAMBOO_DOOR, StateTypes.CHERRY_DOOR });
/* 394 */     WOODEN_STAIRS.add(new StateType[] { StateTypes.OAK_STAIRS, StateTypes.SPRUCE_STAIRS, StateTypes.BIRCH_STAIRS, StateTypes.JUNGLE_STAIRS, StateTypes.ACACIA_STAIRS, StateTypes.DARK_OAK_STAIRS, StateTypes.PALE_OAK_STAIRS, StateTypes.CRIMSON_STAIRS, StateTypes.WARPED_STAIRS, StateTypes.MANGROVE_STAIRS, StateTypes.BAMBOO_STAIRS, StateTypes.CHERRY_STAIRS });
/* 395 */     WOODEN_SLABS.add(new StateType[] { StateTypes.OAK_SLAB, StateTypes.SPRUCE_SLAB, StateTypes.BIRCH_SLAB, StateTypes.JUNGLE_SLAB, StateTypes.ACACIA_SLAB, StateTypes.DARK_OAK_SLAB, StateTypes.PALE_OAK_SLAB, StateTypes.CRIMSON_SLAB, StateTypes.WARPED_SLAB, StateTypes.MANGROVE_SLAB, StateTypes.BAMBOO_SLAB, StateTypes.CHERRY_SLAB });
/* 396 */     WOODEN_FENCES.add(new StateType[] { StateTypes.OAK_FENCE, StateTypes.ACACIA_FENCE, StateTypes.DARK_OAK_FENCE, StateTypes.PALE_OAK_FENCE, StateTypes.SPRUCE_FENCE, StateTypes.BIRCH_FENCE, StateTypes.JUNGLE_FENCE, StateTypes.CRIMSON_FENCE, StateTypes.WARPED_FENCE, StateTypes.MANGROVE_FENCE, StateTypes.BAMBOO_FENCE, StateTypes.CHERRY_FENCE });
/* 397 */     FENCE_GATES.add(new StateType[] { StateTypes.ACACIA_FENCE_GATE, StateTypes.BIRCH_FENCE_GATE, StateTypes.DARK_OAK_FENCE_GATE, StateTypes.PALE_OAK_FENCE_GATE, StateTypes.JUNGLE_FENCE_GATE, StateTypes.OAK_FENCE_GATE, StateTypes.SPRUCE_FENCE_GATE, StateTypes.CRIMSON_FENCE_GATE, StateTypes.WARPED_FENCE_GATE, StateTypes.MANGROVE_FENCE_GATE, StateTypes.BAMBOO_FENCE_GATE, StateTypes.CHERRY_FENCE_GATE });
/* 398 */     WOODEN_PRESSURE_PLATES.add(new StateType[] { StateTypes.OAK_PRESSURE_PLATE, StateTypes.SPRUCE_PRESSURE_PLATE, StateTypes.BIRCH_PRESSURE_PLATE, StateTypes.JUNGLE_PRESSURE_PLATE, StateTypes.ACACIA_PRESSURE_PLATE, StateTypes.DARK_OAK_PRESSURE_PLATE, StateTypes.PALE_OAK_PRESSURE_PLATE, StateTypes.CRIMSON_PRESSURE_PLATE, StateTypes.WARPED_PRESSURE_PLATE, StateTypes.MANGROVE_PRESSURE_PLATE, StateTypes.BAMBOO_PRESSURE_PLATE, StateTypes.CHERRY_PRESSURE_PLATE });
/* 399 */     SAPLINGS.add(new StateType[] { StateTypes.OAK_SAPLING, StateTypes.SPRUCE_SAPLING, StateTypes.BIRCH_SAPLING, StateTypes.JUNGLE_SAPLING, StateTypes.ACACIA_SAPLING, StateTypes.DARK_OAK_SAPLING, StateTypes.PALE_OAK_SAPLING, StateTypes.AZALEA, StateTypes.FLOWERING_AZALEA, StateTypes.MANGROVE_PROPAGULE, StateTypes.CHERRY_SAPLING });
/* 400 */     BAMBOO_BLOCKS.add(new StateType[] { StateTypes.BAMBOO_BLOCK, StateTypes.STRIPPED_BAMBOO_BLOCK });
/* 401 */     OAK_LOGS.add(new StateType[] { StateTypes.OAK_LOG, StateTypes.OAK_WOOD, StateTypes.STRIPPED_OAK_LOG, StateTypes.STRIPPED_OAK_WOOD });
/* 402 */     DARK_OAK_LOGS.add(new StateType[] { StateTypes.DARK_OAK_LOG, StateTypes.DARK_OAK_WOOD, StateTypes.STRIPPED_DARK_OAK_LOG, StateTypes.STRIPPED_DARK_OAK_WOOD });
/* 403 */     PALE_OAK_LOGS.add(new StateType[] { StateTypes.PALE_OAK_LOG, StateTypes.PALE_OAK_WOOD, StateTypes.STRIPPED_PALE_OAK_LOG, StateTypes.STRIPPED_PALE_OAK_WOOD });
/* 404 */     BIRCH_LOGS.add(new StateType[] { StateTypes.BIRCH_LOG, StateTypes.BIRCH_WOOD, StateTypes.STRIPPED_BIRCH_LOG, StateTypes.STRIPPED_BIRCH_WOOD });
/* 405 */     ACACIA_LOGS.add(new StateType[] { StateTypes.ACACIA_LOG, StateTypes.ACACIA_WOOD, StateTypes.STRIPPED_ACACIA_LOG, StateTypes.STRIPPED_ACACIA_WOOD });
/* 406 */     SPRUCE_LOGS.add(new StateType[] { StateTypes.SPRUCE_LOG, StateTypes.SPRUCE_WOOD, StateTypes.STRIPPED_SPRUCE_LOG, StateTypes.STRIPPED_SPRUCE_WOOD });
/* 407 */     MANGROVE_LOGS.add(new StateType[] { StateTypes.MANGROVE_LOG, StateTypes.MANGROVE_WOOD, StateTypes.STRIPPED_MANGROVE_LOG, StateTypes.STRIPPED_MANGROVE_WOOD });
/* 408 */     JUNGLE_LOGS.add(new StateType[] { StateTypes.JUNGLE_LOG, StateTypes.JUNGLE_WOOD, StateTypes.STRIPPED_JUNGLE_LOG, StateTypes.STRIPPED_JUNGLE_WOOD });
/* 409 */     CHERRY_LOGS.add(new StateType[] { StateTypes.CHERRY_LOG, StateTypes.CHERRY_WOOD, StateTypes.STRIPPED_CHERRY_LOG, StateTypes.STRIPPED_CHERRY_WOOD });
/* 410 */     CRIMSON_STEMS.add(new StateType[] { StateTypes.CRIMSON_STEM, StateTypes.STRIPPED_CRIMSON_STEM, StateTypes.CRIMSON_HYPHAE, StateTypes.STRIPPED_CRIMSON_HYPHAE });
/* 411 */     WARPED_STEMS.add(new StateType[] { StateTypes.WARPED_STEM, StateTypes.STRIPPED_WARPED_STEM, StateTypes.WARPED_HYPHAE, StateTypes.STRIPPED_WARPED_HYPHAE });
/* 412 */     WART_BLOCKS.add(new StateType[] { StateTypes.NETHER_WART_BLOCK, StateTypes.WARPED_WART_BLOCK });
/* 413 */     SAND.add(new StateType[] { StateTypes.SAND, StateTypes.RED_SAND, StateTypes.SUSPICIOUS_SAND });
/* 414 */     SMELTS_TO_GLASS.add(new StateType[] { StateTypes.SAND, StateTypes.RED_SAND });
/* 415 */     WALLS.add(new StateType[] { StateTypes.COBBLESTONE_WALL, StateTypes.MOSSY_COBBLESTONE_WALL, StateTypes.BRICK_WALL, StateTypes.PRISMARINE_WALL, StateTypes.RED_SANDSTONE_WALL, StateTypes.MOSSY_STONE_BRICK_WALL, StateTypes.GRANITE_WALL, StateTypes.STONE_BRICK_WALL, StateTypes.NETHER_BRICK_WALL, StateTypes.ANDESITE_WALL, StateTypes.RED_NETHER_BRICK_WALL, StateTypes.SANDSTONE_WALL, StateTypes.END_STONE_BRICK_WALL, StateTypes.DIORITE_WALL, StateTypes.BLACKSTONE_WALL, StateTypes.POLISHED_BLACKSTONE_BRICK_WALL, StateTypes.POLISHED_BLACKSTONE_WALL, StateTypes.COBBLED_DEEPSLATE_WALL, StateTypes.POLISHED_DEEPSLATE_WALL, StateTypes.DEEPSLATE_TILE_WALL, StateTypes.DEEPSLATE_BRICK_WALL, StateTypes.MUD_BRICK_WALL, StateTypes.TUFF_WALL, StateTypes.POLISHED_TUFF_WALL, StateTypes.TUFF_BRICK_WALL, StateTypes.RESIN_BRICK_WALL });
/* 416 */     ANVIL.add(new StateType[] { StateTypes.ANVIL, StateTypes.CHIPPED_ANVIL, StateTypes.DAMAGED_ANVIL });
/* 417 */     RAILS.add(new StateType[] { StateTypes.RAIL, StateTypes.POWERED_RAIL, StateTypes.DETECTOR_RAIL, StateTypes.ACTIVATOR_RAIL });
/* 418 */     LEAVES.add(new StateType[] { StateTypes.JUNGLE_LEAVES, StateTypes.OAK_LEAVES, StateTypes.SPRUCE_LEAVES, StateTypes.PALE_OAK_LEAVES, StateTypes.DARK_OAK_LEAVES, StateTypes.ACACIA_LEAVES, StateTypes.BIRCH_LEAVES, StateTypes.AZALEA_LEAVES, StateTypes.FLOWERING_AZALEA_LEAVES, StateTypes.MANGROVE_LEAVES, StateTypes.CHERRY_LEAVES });
/* 419 */     WOODEN_TRAPDOORS.add(new StateType[] { StateTypes.ACACIA_TRAPDOOR, StateTypes.BIRCH_TRAPDOOR, StateTypes.DARK_OAK_TRAPDOOR, StateTypes.PALE_OAK_TRAPDOOR, StateTypes.JUNGLE_TRAPDOOR, StateTypes.OAK_TRAPDOOR, StateTypes.SPRUCE_TRAPDOOR, StateTypes.CRIMSON_TRAPDOOR, StateTypes.WARPED_TRAPDOOR, StateTypes.MANGROVE_TRAPDOOR, StateTypes.BAMBOO_TRAPDOOR, StateTypes.CHERRY_TRAPDOOR });
/* 420 */     SMALL_FLOWERS.add(new StateType[] { StateTypes.DANDELION, StateTypes.OPEN_EYEBLOSSOM, StateTypes.POPPY, StateTypes.BLUE_ORCHID, StateTypes.ALLIUM, StateTypes.AZURE_BLUET, StateTypes.RED_TULIP, StateTypes.ORANGE_TULIP, StateTypes.WHITE_TULIP, StateTypes.PINK_TULIP, StateTypes.OXEYE_DAISY, StateTypes.CORNFLOWER, StateTypes.LILY_OF_THE_VALLEY, StateTypes.WITHER_ROSE, StateTypes.TORCHFLOWER, StateTypes.CLOSED_EYEBLOSSOM });
/* 421 */     BEDS.add(new StateType[] { StateTypes.RED_BED, StateTypes.BLACK_BED, StateTypes.BLUE_BED, StateTypes.BROWN_BED, StateTypes.CYAN_BED, StateTypes.GRAY_BED, StateTypes.GREEN_BED, StateTypes.LIGHT_BLUE_BED, StateTypes.LIGHT_GRAY_BED, StateTypes.LIME_BED, StateTypes.MAGENTA_BED, StateTypes.ORANGE_BED, StateTypes.PINK_BED, StateTypes.PURPLE_BED, StateTypes.WHITE_BED, StateTypes.YELLOW_BED });
/* 422 */     SOUL_FIRE_BASE_BLOCKS.add(new StateType[] { StateTypes.SOUL_SAND, StateTypes.SOUL_SOIL });
/* 423 */     CANDLES.add(new StateType[] { StateTypes.CANDLE, StateTypes.WHITE_CANDLE, StateTypes.ORANGE_CANDLE, StateTypes.MAGENTA_CANDLE, StateTypes.LIGHT_BLUE_CANDLE, StateTypes.YELLOW_CANDLE, StateTypes.LIME_CANDLE, StateTypes.PINK_CANDLE, StateTypes.GRAY_CANDLE, StateTypes.LIGHT_GRAY_CANDLE, StateTypes.CYAN_CANDLE, StateTypes.PURPLE_CANDLE, StateTypes.BLUE_CANDLE, StateTypes.BROWN_CANDLE, StateTypes.GREEN_CANDLE, StateTypes.RED_CANDLE, StateTypes.BLACK_CANDLE });
/* 424 */     GOLD_ORES.add(new StateType[] { StateTypes.GOLD_ORE, StateTypes.NETHER_GOLD_ORE, StateTypes.DEEPSLATE_GOLD_ORE });
/* 425 */     IRON_ORES.add(new StateType[] { StateTypes.IRON_ORE, StateTypes.DEEPSLATE_IRON_ORE });
/* 426 */     DIAMOND_ORES.add(new StateType[] { StateTypes.DIAMOND_ORE, StateTypes.DEEPSLATE_DIAMOND_ORE });
/* 427 */     REDSTONE_ORES.add(new StateType[] { StateTypes.REDSTONE_ORE, StateTypes.DEEPSLATE_REDSTONE_ORE });
/* 428 */     LAPIS_ORES.add(new StateType[] { StateTypes.LAPIS_ORE, StateTypes.DEEPSLATE_LAPIS_ORE });
/* 429 */     COAL_ORES.add(new StateType[] { StateTypes.COAL_ORE, StateTypes.DEEPSLATE_COAL_ORE });
/* 430 */     EMERALD_ORES.add(new StateType[] { StateTypes.EMERALD_ORE, StateTypes.DEEPSLATE_EMERALD_ORE });
/* 431 */     COPPER_ORES.add(new StateType[] { StateTypes.COPPER_ORE, StateTypes.DEEPSLATE_COPPER_ORE });
/* 432 */     DIRT.add(new StateType[] { StateTypes.DIRT, StateTypes.GRASS_BLOCK, StateTypes.PODZOL, StateTypes.COARSE_DIRT, StateTypes.MYCELIUM, StateTypes.ROOTED_DIRT, StateTypes.MOSS_BLOCK, StateTypes.PALE_MOSS_BLOCK, StateTypes.MUD, StateTypes.MUDDY_MANGROVE_ROOTS });
/* 433 */     TERRACOTTA.add(new StateType[] { StateTypes.TERRACOTTA, StateTypes.WHITE_TERRACOTTA, StateTypes.ORANGE_TERRACOTTA, StateTypes.MAGENTA_TERRACOTTA, StateTypes.LIGHT_BLUE_TERRACOTTA, StateTypes.YELLOW_TERRACOTTA, StateTypes.LIME_TERRACOTTA, StateTypes.PINK_TERRACOTTA, StateTypes.GRAY_TERRACOTTA, StateTypes.LIGHT_GRAY_TERRACOTTA, StateTypes.CYAN_TERRACOTTA, StateTypes.PURPLE_TERRACOTTA, StateTypes.BLUE_TERRACOTTA, StateTypes.BROWN_TERRACOTTA, StateTypes.GREEN_TERRACOTTA, StateTypes.RED_TERRACOTTA, StateTypes.BLACK_TERRACOTTA });
/* 434 */     SHULKER_BOXES.add(new StateType[] { StateTypes.SHULKER_BOX, StateTypes.BLACK_SHULKER_BOX, StateTypes.BLUE_SHULKER_BOX, StateTypes.BROWN_SHULKER_BOX, StateTypes.CYAN_SHULKER_BOX, StateTypes.GRAY_SHULKER_BOX, StateTypes.GREEN_SHULKER_BOX, StateTypes.LIGHT_BLUE_SHULKER_BOX, StateTypes.LIGHT_GRAY_SHULKER_BOX, StateTypes.LIME_SHULKER_BOX, StateTypes.MAGENTA_SHULKER_BOX, StateTypes.ORANGE_SHULKER_BOX, StateTypes.PINK_SHULKER_BOX, StateTypes.PURPLE_SHULKER_BOX, StateTypes.RED_SHULKER_BOX, StateTypes.WHITE_SHULKER_BOX, StateTypes.YELLOW_SHULKER_BOX });
/* 435 */     CEILING_HANGING_SIGNS.add(new StateType[] { StateTypes.OAK_HANGING_SIGN, StateTypes.SPRUCE_HANGING_SIGN, StateTypes.BIRCH_HANGING_SIGN, StateTypes.ACACIA_HANGING_SIGN, StateTypes.CHERRY_HANGING_SIGN, StateTypes.JUNGLE_HANGING_SIGN, StateTypes.DARK_OAK_HANGING_SIGN, StateTypes.PALE_OAK_HANGING_SIGN, StateTypes.CRIMSON_HANGING_SIGN, StateTypes.WARPED_HANGING_SIGN, StateTypes.MANGROVE_HANGING_SIGN, StateTypes.BAMBOO_HANGING_SIGN });
/* 436 */     STANDING_SIGNS.add(new StateType[] { StateTypes.OAK_SIGN, StateTypes.SPRUCE_SIGN, StateTypes.BIRCH_SIGN, StateTypes.ACACIA_SIGN, StateTypes.JUNGLE_SIGN, StateTypes.DARK_OAK_SIGN, StateTypes.PALE_OAK_SIGN, StateTypes.CRIMSON_SIGN, StateTypes.WARPED_SIGN, StateTypes.MANGROVE_SIGN, StateTypes.BAMBOO_SIGN, StateTypes.CHERRY_SIGN });
/* 437 */     BEE_ATTRACTIVE.add(new StateType[] { StateTypes.DANDELION, StateTypes.OPEN_EYEBLOSSOM, StateTypes.POPPY, StateTypes.BLUE_ORCHID, StateTypes.ALLIUM, StateTypes.AZURE_BLUET, StateTypes.RED_TULIP, StateTypes.ORANGE_TULIP, StateTypes.WHITE_TULIP, StateTypes.PINK_TULIP, StateTypes.OXEYE_DAISY, StateTypes.CORNFLOWER, StateTypes.LILY_OF_THE_VALLEY, StateTypes.WITHER_ROSE, StateTypes.TORCHFLOWER, StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.PEONY, StateTypes.ROSE_BUSH, StateTypes.PITCHER_PLANT, StateTypes.FLOWERING_AZALEA_LEAVES, StateTypes.FLOWERING_AZALEA, StateTypes.MANGROVE_PROPAGULE, StateTypes.CHERRY_LEAVES, StateTypes.PINK_PETALS, StateTypes.WILDFLOWERS, StateTypes.CHORUS_FLOWER, StateTypes.SPORE_BLOSSOM, StateTypes.CACTUS_FLOWER });
/* 438 */     STONE_PRESSURE_PLATES.add(new StateType[] { StateTypes.STONE_PRESSURE_PLATE, StateTypes.POLISHED_BLACKSTONE_PRESSURE_PLATE });
/* 439 */     OVERWORLD_NATURAL_LOGS.add(new StateType[] { StateTypes.ACACIA_LOG, StateTypes.BIRCH_LOG, StateTypes.OAK_LOG, StateTypes.JUNGLE_LOG, StateTypes.SPRUCE_LOG, StateTypes.DARK_OAK_LOG, StateTypes.PALE_OAK_LOG, StateTypes.MANGROVE_LOG, StateTypes.CHERRY_LOG });
/* 440 */     BANNERS.add(new StateType[] { StateTypes.WHITE_BANNER, StateTypes.ORANGE_BANNER, StateTypes.MAGENTA_BANNER, StateTypes.LIGHT_BLUE_BANNER, StateTypes.YELLOW_BANNER, StateTypes.LIME_BANNER, StateTypes.PINK_BANNER, StateTypes.GRAY_BANNER, StateTypes.LIGHT_GRAY_BANNER, StateTypes.CYAN_BANNER, StateTypes.PURPLE_BANNER, StateTypes.BLUE_BANNER, StateTypes.BROWN_BANNER, StateTypes.GREEN_BANNER, StateTypes.RED_BANNER, StateTypes.BLACK_BANNER, StateTypes.WHITE_WALL_BANNER, StateTypes.ORANGE_WALL_BANNER, StateTypes.MAGENTA_WALL_BANNER, StateTypes.LIGHT_BLUE_WALL_BANNER, StateTypes.YELLOW_WALL_BANNER, StateTypes.LIME_WALL_BANNER, StateTypes.PINK_WALL_BANNER, StateTypes.GRAY_WALL_BANNER, StateTypes.LIGHT_GRAY_WALL_BANNER, StateTypes.CYAN_WALL_BANNER, StateTypes.PURPLE_WALL_BANNER, StateTypes.BLUE_WALL_BANNER, StateTypes.BROWN_WALL_BANNER, StateTypes.GREEN_WALL_BANNER, StateTypes.RED_WALL_BANNER, StateTypes.BLACK_WALL_BANNER });
/* 441 */     PIGLIN_REPELLENTS.add(new StateType[] { StateTypes.SOUL_FIRE, StateTypes.SOUL_TORCH, StateTypes.SOUL_LANTERN, StateTypes.SOUL_WALL_TORCH, StateTypes.SOUL_CAMPFIRE });
/* 442 */     BADLANDS_TERRACOTTA.add(new StateType[] { StateTypes.TERRACOTTA, StateTypes.WHITE_TERRACOTTA, StateTypes.YELLOW_TERRACOTTA, StateTypes.ORANGE_TERRACOTTA, StateTypes.RED_TERRACOTTA, StateTypes.BROWN_TERRACOTTA, StateTypes.LIGHT_GRAY_TERRACOTTA });
/* 443 */     CONCRETE_POWDER.add(new StateType[] { StateTypes.WHITE_CONCRETE_POWDER, StateTypes.ORANGE_CONCRETE_POWDER, StateTypes.MAGENTA_CONCRETE_POWDER, StateTypes.LIGHT_BLUE_CONCRETE_POWDER, StateTypes.YELLOW_CONCRETE_POWDER, StateTypes.LIME_CONCRETE_POWDER, StateTypes.PINK_CONCRETE_POWDER, StateTypes.GRAY_CONCRETE_POWDER, StateTypes.LIGHT_GRAY_CONCRETE_POWDER, StateTypes.CYAN_CONCRETE_POWDER, StateTypes.PURPLE_CONCRETE_POWDER, StateTypes.BLUE_CONCRETE_POWDER, StateTypes.BROWN_CONCRETE_POWDER, StateTypes.GREEN_CONCRETE_POWDER, StateTypes.RED_CONCRETE_POWDER, StateTypes.BLACK_CONCRETE_POWDER });
/* 444 */     FLOWER_POTS.add(new StateType[] { StateTypes.FLOWER_POT, StateTypes.POTTED_OPEN_EYEBLOSSOM, StateTypes.POTTED_CLOSED_EYEBLOSSOM, StateTypes.POTTED_POPPY, StateTypes.POTTED_BLUE_ORCHID, StateTypes.POTTED_ALLIUM, StateTypes.POTTED_AZURE_BLUET, StateTypes.POTTED_RED_TULIP, StateTypes.POTTED_ORANGE_TULIP, StateTypes.POTTED_WHITE_TULIP, StateTypes.POTTED_PINK_TULIP, StateTypes.POTTED_OXEYE_DAISY, StateTypes.POTTED_DANDELION, StateTypes.POTTED_OAK_SAPLING, StateTypes.POTTED_SPRUCE_SAPLING, StateTypes.POTTED_BIRCH_SAPLING, StateTypes.POTTED_JUNGLE_SAPLING, StateTypes.POTTED_ACACIA_SAPLING, StateTypes.POTTED_DARK_OAK_SAPLING, StateTypes.POTTED_PALE_OAK_SAPLING, StateTypes.POTTED_RED_MUSHROOM, StateTypes.POTTED_BROWN_MUSHROOM, StateTypes.POTTED_DEAD_BUSH, StateTypes.POTTED_FERN, StateTypes.POTTED_CACTUS, StateTypes.POTTED_CORNFLOWER, StateTypes.POTTED_LILY_OF_THE_VALLEY, StateTypes.POTTED_WITHER_ROSE, StateTypes.POTTED_BAMBOO, StateTypes.POTTED_CRIMSON_FUNGUS, StateTypes.POTTED_WARPED_FUNGUS, StateTypes.POTTED_CRIMSON_ROOTS, StateTypes.POTTED_WARPED_ROOTS, StateTypes.POTTED_AZALEA_BUSH, StateTypes.POTTED_FLOWERING_AZALEA_BUSH, StateTypes.POTTED_MANGROVE_PROPAGULE, StateTypes.POTTED_CHERRY_SAPLING, StateTypes.POTTED_TORCHFLOWER });
/* 445 */     ICE.add(new StateType[] { StateTypes.ICE, StateTypes.PACKED_ICE, StateTypes.BLUE_ICE, StateTypes.FROSTED_ICE });
/* 446 */     VALID_SPAWN.add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.PODZOL });
/* 447 */     IMPERMEABLE.add(new StateType[] { StateTypes.GLASS, StateTypes.WHITE_STAINED_GLASS, StateTypes.ORANGE_STAINED_GLASS, StateTypes.MAGENTA_STAINED_GLASS, StateTypes.LIGHT_BLUE_STAINED_GLASS, StateTypes.YELLOW_STAINED_GLASS, StateTypes.LIME_STAINED_GLASS, StateTypes.PINK_STAINED_GLASS, StateTypes.GRAY_STAINED_GLASS, StateTypes.LIGHT_GRAY_STAINED_GLASS, StateTypes.CYAN_STAINED_GLASS, StateTypes.PURPLE_STAINED_GLASS, StateTypes.BLUE_STAINED_GLASS, StateTypes.BROWN_STAINED_GLASS, StateTypes.GREEN_STAINED_GLASS, StateTypes.RED_STAINED_GLASS, StateTypes.BLACK_STAINED_GLASS, StateTypes.TINTED_GLASS });
/* 448 */     CORAL_BLOCKS.add(new StateType[] { StateTypes.TUBE_CORAL_BLOCK, StateTypes.BRAIN_CORAL_BLOCK, StateTypes.BUBBLE_CORAL_BLOCK, StateTypes.FIRE_CORAL_BLOCK, StateTypes.HORN_CORAL_BLOCK });
/* 449 */     WALL_CORALS.add(new StateType[] { StateTypes.TUBE_CORAL_WALL_FAN, StateTypes.BRAIN_CORAL_WALL_FAN, StateTypes.BUBBLE_CORAL_WALL_FAN, StateTypes.FIRE_CORAL_WALL_FAN, StateTypes.HORN_CORAL_WALL_FAN });
/* 450 */     CORAL_PLANTS.add(new StateType[] { StateTypes.TUBE_CORAL, StateTypes.BRAIN_CORAL, StateTypes.BUBBLE_CORAL, StateTypes.FIRE_CORAL, StateTypes.HORN_CORAL });
/* 451 */     WALL_SIGNS.add(new StateType[] { StateTypes.OAK_WALL_SIGN, StateTypes.SPRUCE_WALL_SIGN, StateTypes.BIRCH_WALL_SIGN, StateTypes.ACACIA_WALL_SIGN, StateTypes.JUNGLE_WALL_SIGN, StateTypes.DARK_OAK_WALL_SIGN, StateTypes.PALE_OAK_WALL_SIGN, StateTypes.CRIMSON_WALL_SIGN, StateTypes.WARPED_WALL_SIGN, StateTypes.MANGROVE_WALL_SIGN, StateTypes.BAMBOO_WALL_SIGN, StateTypes.CHERRY_WALL_SIGN });
/* 452 */     WALL_HANGING_SIGNS.add(new StateType[] { StateTypes.OAK_WALL_HANGING_SIGN, StateTypes.SPRUCE_WALL_HANGING_SIGN, StateTypes.BIRCH_WALL_HANGING_SIGN, StateTypes.ACACIA_WALL_HANGING_SIGN, StateTypes.CHERRY_WALL_HANGING_SIGN, StateTypes.JUNGLE_WALL_HANGING_SIGN, StateTypes.DARK_OAK_WALL_HANGING_SIGN, StateTypes.PALE_OAK_WALL_HANGING_SIGN, StateTypes.CRIMSON_WALL_HANGING_SIGN, StateTypes.WARPED_WALL_HANGING_SIGN, StateTypes.MANGROVE_WALL_HANGING_SIGN, StateTypes.BAMBOO_WALL_HANGING_SIGN });
/* 453 */     DRAGON_IMMUNE.add(new StateType[] { StateTypes.BARRIER, StateTypes.BEDROCK, StateTypes.END_PORTAL, StateTypes.END_PORTAL_FRAME, StateTypes.END_GATEWAY, StateTypes.COMMAND_BLOCK, StateTypes.REPEATING_COMMAND_BLOCK, StateTypes.CHAIN_COMMAND_BLOCK, StateTypes.STRUCTURE_BLOCK, StateTypes.JIGSAW, StateTypes.MOVING_PISTON, StateTypes.OBSIDIAN, StateTypes.CRYING_OBSIDIAN, StateTypes.END_STONE, StateTypes.IRON_BARS, StateTypes.RESPAWN_ANCHOR, StateTypes.REINFORCED_DEEPSLATE, StateTypes.TEST_BLOCK, StateTypes.TEST_INSTANCE_BLOCK });
/* 454 */     WITHER_IMMUNE.add(new StateType[] { StateTypes.BARRIER, StateTypes.BEDROCK, StateTypes.END_PORTAL, StateTypes.END_PORTAL_FRAME, StateTypes.END_GATEWAY, StateTypes.COMMAND_BLOCK, StateTypes.REPEATING_COMMAND_BLOCK, StateTypes.CHAIN_COMMAND_BLOCK, StateTypes.STRUCTURE_BLOCK, StateTypes.JIGSAW, StateTypes.MOVING_PISTON, StateTypes.LIGHT, StateTypes.REINFORCED_DEEPSLATE, StateTypes.TEST_BLOCK, StateTypes.TEST_INSTANCE_BLOCK });
/* 455 */     copy(SOUL_FIRE_BASE_BLOCKS, WITHER_SUMMON_BASE_BLOCKS);
/* 456 */     BEEHIVES.add(new StateType[] { StateTypes.BEE_NEST, StateTypes.BEEHIVE });
/* 457 */     CROPS.add(new StateType[] { StateTypes.BEETROOTS, StateTypes.CARROTS, StateTypes.POTATOES, StateTypes.WHEAT, StateTypes.MELON_STEM, StateTypes.PUMPKIN_STEM, StateTypes.TORCHFLOWER_CROP, StateTypes.PITCHER_CROP });
/* 458 */     PORTALS.add(new StateType[] { StateTypes.NETHER_PORTAL, StateTypes.END_PORTAL, StateTypes.END_GATEWAY });
/* 459 */     FIRE.add(new StateType[] { StateTypes.FIRE, StateTypes.SOUL_FIRE });
/* 460 */     NYLIUM.add(new StateType[] { StateTypes.CRIMSON_NYLIUM, StateTypes.WARPED_NYLIUM });
/* 461 */     BEACON_BASE_BLOCKS.add(new StateType[] { StateTypes.NETHERITE_BLOCK, StateTypes.EMERALD_BLOCK, StateTypes.DIAMOND_BLOCK, StateTypes.GOLD_BLOCK, StateTypes.IRON_BLOCK });
/* 462 */     copy(SOUL_FIRE_BASE_BLOCKS, SOUL_SPEED_BLOCKS);
/* 463 */     CLIMBABLE.add(new StateType[] { StateTypes.LADDER, StateTypes.VINE, StateTypes.SCAFFOLDING, StateTypes.WEEPING_VINES, StateTypes.WEEPING_VINES_PLANT, StateTypes.TWISTING_VINES, StateTypes.TWISTING_VINES_PLANT, StateTypes.CAVE_VINES, StateTypes.CAVE_VINES_PLANT });
/* 464 */     HOGLIN_REPELLENTS.add(new StateType[] { StateTypes.WARPED_FUNGUS, StateTypes.POTTED_WARPED_FUNGUS, StateTypes.NETHER_PORTAL, StateTypes.RESPAWN_ANCHOR });
/* 465 */     STRIDER_WARM_BLOCKS.add(new StateType[] { StateTypes.LAVA });
/* 466 */     CAMPFIRES.add(new StateType[] { StateTypes.CAMPFIRE, StateTypes.SOUL_CAMPFIRE });
/* 467 */     MUSHROOM_GROW_BLOCK.add(new StateType[] { StateTypes.MYCELIUM, StateTypes.PODZOL, StateTypes.CRIMSON_NYLIUM, StateTypes.WARPED_NYLIUM });
/* 468 */     EDIBLE_FOR_SHEEP.add(new StateType[] { StateTypes.SHORT_GRASS, StateTypes.SHORT_DRY_GRASS, StateTypes.TALL_DRY_GRASS, StateTypes.FERN });
/* 469 */     INFINIBURN_OVERWORLD.add(new StateType[] { StateTypes.NETHERRACK, StateTypes.MAGMA_BLOCK });
/* 470 */     BASE_STONE_OVERWORLD.add(new StateType[] { StateTypes.STONE, StateTypes.GRANITE, StateTypes.DIORITE, StateTypes.ANDESITE, StateTypes.TUFF, StateTypes.DEEPSLATE });
/* 471 */     STONE_ORE_REPLACEABLES.add(new StateType[] { StateTypes.STONE, StateTypes.GRANITE, StateTypes.DIORITE, StateTypes.ANDESITE });
/* 472 */     DEEPSLATE_ORE_REPLACEABLES.add(new StateType[] { StateTypes.DEEPSLATE, StateTypes.TUFF });
/* 473 */     BASE_STONE_NETHER.add(new StateType[] { StateTypes.NETHERRACK, StateTypes.BASALT, StateTypes.BLACKSTONE });
/* 474 */     CANDLE_CAKES.add(new StateType[] { StateTypes.CANDLE_CAKE, StateTypes.WHITE_CANDLE_CAKE, StateTypes.ORANGE_CANDLE_CAKE, StateTypes.MAGENTA_CANDLE_CAKE, StateTypes.LIGHT_BLUE_CANDLE_CAKE, StateTypes.YELLOW_CANDLE_CAKE, StateTypes.LIME_CANDLE_CAKE, StateTypes.PINK_CANDLE_CAKE, StateTypes.GRAY_CANDLE_CAKE, StateTypes.LIGHT_GRAY_CANDLE_CAKE, StateTypes.CYAN_CANDLE_CAKE, StateTypes.PURPLE_CANDLE_CAKE, StateTypes.BLUE_CANDLE_CAKE, StateTypes.BROWN_CANDLE_CAKE, StateTypes.GREEN_CANDLE_CAKE, StateTypes.RED_CANDLE_CAKE, StateTypes.BLACK_CANDLE_CAKE });
/* 475 */     CAULDRONS.add(new StateType[] { StateTypes.CAULDRON, StateTypes.WATER_CAULDRON, StateTypes.LAVA_CAULDRON, StateTypes.POWDER_SNOW_CAULDRON });
/* 476 */     CRYSTAL_SOUND_BLOCKS.add(new StateType[] { StateTypes.AMETHYST_BLOCK, StateTypes.BUDDING_AMETHYST });
/* 477 */     INSIDE_STEP_SOUND_BLOCKS.add(new StateType[] { StateTypes.POWDER_SNOW, StateTypes.SCULK_VEIN, StateTypes.GLOW_LICHEN, StateTypes.LILY_PAD, StateTypes.SMALL_AMETHYST_BUD, StateTypes.PINK_PETALS, StateTypes.WILDFLOWERS, StateTypes.LEAF_LITTER });
/* 478 */     HAPPY_GHAST_AVOIDS.add(new StateType[] { StateTypes.SWEET_BERRY_BUSH, StateTypes.CACTUS, StateTypes.WITHER_ROSE, StateTypes.MAGMA_BLOCK, StateTypes.FIRE, StateTypes.POINTED_DRIPSTONE });
/* 479 */     CAVE_VINES.add(new StateType[] { StateTypes.CAVE_VINES_PLANT, StateTypes.CAVE_VINES });
/* 480 */     SMALL_DRIPLEAF_PLACEABLE.add(new StateType[] { StateTypes.CLAY, StateTypes.MOSS_BLOCK });
/* 481 */     SNOW.add(new StateType[] { StateTypes.SNOW, StateTypes.SNOW_BLOCK, StateTypes.POWDER_SNOW });
/* 482 */     SWORD_INSTANTLY_MINES.add(new StateType[] { StateTypes.BAMBOO, StateTypes.BAMBOO_SAPLING });
/* 483 */     NEEDS_DIAMOND_TOOL.add(new StateType[] { StateTypes.OBSIDIAN, StateTypes.CRYING_OBSIDIAN, StateTypes.NETHERITE_BLOCK, StateTypes.RESPAWN_ANCHOR, StateTypes.ANCIENT_DEBRIS });
/* 484 */     NEEDS_IRON_TOOL.add(new StateType[] { StateTypes.DIAMOND_BLOCK, StateTypes.DIAMOND_ORE, StateTypes.DEEPSLATE_DIAMOND_ORE, StateTypes.EMERALD_ORE, StateTypes.DEEPSLATE_EMERALD_ORE, StateTypes.EMERALD_BLOCK, StateTypes.GOLD_BLOCK, StateTypes.RAW_GOLD_BLOCK, StateTypes.GOLD_ORE, StateTypes.DEEPSLATE_GOLD_ORE, StateTypes.REDSTONE_ORE, StateTypes.DEEPSLATE_REDSTONE_ORE });
/* 485 */     NEEDS_STONE_TOOL.add(new StateType[] { StateTypes.IRON_BLOCK, StateTypes.RAW_IRON_BLOCK, StateTypes.IRON_ORE, StateTypes.DEEPSLATE_IRON_ORE, StateTypes.LAPIS_BLOCK, StateTypes.LAPIS_ORE, StateTypes.DEEPSLATE_LAPIS_ORE, StateTypes.COPPER_BLOCK, StateTypes.RAW_COPPER_BLOCK, StateTypes.COPPER_ORE, StateTypes.DEEPSLATE_COPPER_ORE, StateTypes.CUT_COPPER_SLAB, StateTypes.CUT_COPPER_STAIRS, StateTypes.CUT_COPPER, StateTypes.WEATHERED_COPPER, StateTypes.WEATHERED_CUT_COPPER_SLAB, StateTypes.WEATHERED_CUT_COPPER_STAIRS, StateTypes.WEATHERED_CUT_COPPER, StateTypes.OXIDIZED_COPPER, StateTypes.OXIDIZED_CUT_COPPER_SLAB, StateTypes.OXIDIZED_CUT_COPPER_STAIRS, StateTypes.OXIDIZED_CUT_COPPER, StateTypes.EXPOSED_COPPER, StateTypes.EXPOSED_CUT_COPPER_SLAB, StateTypes.EXPOSED_CUT_COPPER_STAIRS, StateTypes.EXPOSED_CUT_COPPER, StateTypes.WAXED_COPPER_BLOCK, StateTypes.WAXED_CUT_COPPER_SLAB, StateTypes.WAXED_CUT_COPPER_STAIRS, StateTypes.WAXED_CUT_COPPER, StateTypes.WAXED_WEATHERED_COPPER, StateTypes.WAXED_WEATHERED_CUT_COPPER_SLAB, StateTypes.WAXED_WEATHERED_CUT_COPPER_STAIRS, StateTypes.WAXED_WEATHERED_CUT_COPPER, StateTypes.WAXED_EXPOSED_COPPER, StateTypes.WAXED_EXPOSED_CUT_COPPER_SLAB, StateTypes.WAXED_EXPOSED_CUT_COPPER_STAIRS, StateTypes.WAXED_EXPOSED_CUT_COPPER, StateTypes.WAXED_OXIDIZED_COPPER, StateTypes.WAXED_OXIDIZED_CUT_COPPER_SLAB, StateTypes.WAXED_OXIDIZED_CUT_COPPER_STAIRS, StateTypes.WAXED_OXIDIZED_CUT_COPPER, StateTypes.LIGHTNING_ROD, StateTypes.CRAFTER, StateTypes.CHISELED_COPPER, StateTypes.EXPOSED_CHISELED_COPPER, StateTypes.WEATHERED_CHISELED_COPPER, StateTypes.OXIDIZED_CHISELED_COPPER, StateTypes.WAXED_CHISELED_COPPER, StateTypes.WAXED_EXPOSED_CHISELED_COPPER, StateTypes.WAXED_WEATHERED_CHISELED_COPPER, StateTypes.WAXED_OXIDIZED_CHISELED_COPPER, StateTypes.COPPER_GRATE, StateTypes.EXPOSED_COPPER_GRATE, StateTypes.WEATHERED_COPPER_GRATE, StateTypes.OXIDIZED_COPPER_GRATE, StateTypes.WAXED_COPPER_GRATE, StateTypes.WAXED_EXPOSED_COPPER_GRATE, StateTypes.WAXED_WEATHERED_COPPER_GRATE, StateTypes.WAXED_OXIDIZED_COPPER_GRATE, StateTypes.COPPER_BULB, StateTypes.EXPOSED_COPPER_BULB, StateTypes.WEATHERED_COPPER_BULB, StateTypes.OXIDIZED_COPPER_BULB, StateTypes.WAXED_COPPER_BULB, StateTypes.WAXED_EXPOSED_COPPER_BULB, StateTypes.WAXED_WEATHERED_COPPER_BULB, StateTypes.WAXED_OXIDIZED_COPPER_BULB, StateTypes.COPPER_TRAPDOOR, StateTypes.EXPOSED_COPPER_TRAPDOOR, StateTypes.WEATHERED_COPPER_TRAPDOOR, StateTypes.OXIDIZED_COPPER_TRAPDOOR, StateTypes.WAXED_COPPER_TRAPDOOR, StateTypes.WAXED_EXPOSED_COPPER_TRAPDOOR, StateTypes.WAXED_WEATHERED_COPPER_TRAPDOOR, StateTypes.WAXED_OXIDIZED_COPPER_TRAPDOOR });
/* 486 */     copy(null, INCORRECT_FOR_NETHERITE_TOOL);
/* 487 */     copy(null, INCORRECT_FOR_DIAMOND_TOOL);
/* 488 */     FEATURES_CANNOT_REPLACE.add(new StateType[] { StateTypes.BEDROCK, StateTypes.SPAWNER, StateTypes.CHEST, StateTypes.END_PORTAL_FRAME, StateTypes.REINFORCED_DEEPSLATE, StateTypes.TRIAL_SPAWNER, StateTypes.VAULT });
/* 489 */     GEODE_INVALID_BLOCKS.add(new StateType[] { StateTypes.BEDROCK, StateTypes.WATER, StateTypes.LAVA, StateTypes.ICE, StateTypes.PACKED_ICE, StateTypes.BLUE_ICE });
/* 490 */     FROG_PREFER_JUMP_TO.add(new StateType[] { StateTypes.LILY_PAD, StateTypes.BIG_DRIPLEAF });
/* 491 */     ANCIENT_CITY_REPLACEABLE.add(new StateType[] { StateTypes.DEEPSLATE, StateTypes.DEEPSLATE_BRICKS, StateTypes.DEEPSLATE_TILES, StateTypes.DEEPSLATE_BRICK_SLAB, StateTypes.DEEPSLATE_TILE_SLAB, StateTypes.DEEPSLATE_BRICK_STAIRS, StateTypes.DEEPSLATE_TILE_WALL, StateTypes.DEEPSLATE_BRICK_WALL, StateTypes.COBBLED_DEEPSLATE, StateTypes.CRACKED_DEEPSLATE_BRICKS, StateTypes.CRACKED_DEEPSLATE_TILES, StateTypes.GRAY_WOOL });
/* 492 */     VIBRATION_RESONATORS.add(new StateType[] { StateTypes.AMETHYST_BLOCK });
/* 493 */     ANIMALS_SPAWNABLE_ON.add(new StateType[] { StateTypes.GRASS_BLOCK });
/* 494 */     AXOLOTLS_SPAWNABLE_ON.add(new StateType[] { StateTypes.CLAY });
/* 495 */     MOOSHROOMS_SPAWNABLE_ON.add(new StateType[] { StateTypes.MYCELIUM });
/* 496 */     POLAR_BEARS_SPAWNABLE_ON_ALTERNATE.add(new StateType[] { StateTypes.ICE });
/* 497 */     RABBITS_SPAWNABLE_ON.add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.SNOW, StateTypes.SNOW_BLOCK, StateTypes.SAND });
/* 498 */     FOXES_SPAWNABLE_ON.add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.SNOW, StateTypes.SNOW_BLOCK, StateTypes.PODZOL, StateTypes.COARSE_DIRT });
/* 499 */     WOLVES_SPAWNABLE_ON.add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.SNOW, StateTypes.SNOW_BLOCK, StateTypes.COARSE_DIRT, StateTypes.PODZOL });
/* 500 */     FROGS_SPAWNABLE_ON.add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.MUD, StateTypes.MANGROVE_ROOTS, StateTypes.MUDDY_MANGROVE_ROOTS });
/* 501 */     CONVERTABLE_TO_MUD.add(new StateType[] { StateTypes.DIRT, StateTypes.COARSE_DIRT, StateTypes.ROOTED_DIRT });
/* 502 */     MANGROVE_LOGS_CAN_GROW_THROUGH.add(new StateType[] { StateTypes.MUD, StateTypes.MUDDY_MANGROVE_ROOTS, StateTypes.MANGROVE_ROOTS, StateTypes.MANGROVE_LEAVES, StateTypes.MANGROVE_LOG, StateTypes.MANGROVE_PROPAGULE, StateTypes.MOSS_CARPET, StateTypes.VINE });
/* 503 */     MANGROVE_ROOTS_CAN_GROW_THROUGH.add(new StateType[] { StateTypes.MUD, StateTypes.MUDDY_MANGROVE_ROOTS, StateTypes.MANGROVE_ROOTS, StateTypes.MOSS_CARPET, StateTypes.VINE, StateTypes.MANGROVE_PROPAGULE, StateTypes.SNOW });
/* 504 */     SNOW_LAYER_CANNOT_SURVIVE_ON.add(new StateType[] { StateTypes.ICE, StateTypes.PACKED_ICE, StateTypes.BARRIER });
/* 505 */     SNOW_LAYER_CAN_SURVIVE_ON.add(new StateType[] { StateTypes.HONEY_BLOCK, StateTypes.SOUL_SAND, StateTypes.MUD });
/* 506 */     INVALID_SPAWN_INSIDE.add(new StateType[] { StateTypes.END_PORTAL, StateTypes.END_GATEWAY });
/* 507 */     SNIFFER_DIGGABLE_BLOCK.add(new StateType[] { StateTypes.DIRT, StateTypes.GRASS_BLOCK, StateTypes.PODZOL, StateTypes.COARSE_DIRT, StateTypes.ROOTED_DIRT, StateTypes.MOSS_BLOCK, StateTypes.PALE_MOSS_BLOCK, StateTypes.MUD, StateTypes.MUDDY_MANGROVE_ROOTS });
/* 508 */     SNIFFER_EGG_HATCH_BOOST.add(new StateType[] { StateTypes.MOSS_BLOCK });
/* 509 */     TRAIL_RUINS_REPLACEABLE.add(new StateType[] { StateTypes.GRAVEL });
/* 510 */     REPLACEABLE.add(new StateType[] { StateTypes.AIR, StateTypes.WATER, StateTypes.LAVA, StateTypes.SHORT_GRASS, StateTypes.FERN, StateTypes.DEAD_BUSH, StateTypes.BUSH, StateTypes.SHORT_DRY_GRASS, StateTypes.TALL_DRY_GRASS, StateTypes.SEAGRASS, StateTypes.TALL_SEAGRASS, StateTypes.FIRE, StateTypes.SOUL_FIRE, StateTypes.SNOW, StateTypes.VINE, StateTypes.GLOW_LICHEN, StateTypes.RESIN_CLUMP, StateTypes.LIGHT, StateTypes.TALL_GRASS, StateTypes.LARGE_FERN, StateTypes.STRUCTURE_VOID, StateTypes.VOID_AIR, StateTypes.CAVE_AIR, StateTypes.BUBBLE_COLUMN, StateTypes.WARPED_ROOTS, StateTypes.NETHER_SPROUTS, StateTypes.CRIMSON_ROOTS, StateTypes.LEAF_LITTER, StateTypes.HANGING_ROOTS });
/* 511 */     ENCHANTMENT_POWER_PROVIDER.add(new StateType[] { StateTypes.BOOKSHELF });
/* 512 */     MAINTAINS_FARMLAND.add(new StateType[] { StateTypes.PUMPKIN_STEM, StateTypes.ATTACHED_PUMPKIN_STEM, StateTypes.MELON_STEM, StateTypes.ATTACHED_MELON_STEM, StateTypes.BEETROOTS, StateTypes.CARROTS, StateTypes.POTATOES, StateTypes.TORCHFLOWER_CROP, StateTypes.TORCHFLOWER, StateTypes.PITCHER_CROP, StateTypes.WHEAT });
/* 513 */     BLOCKS_WIND_CHARGE_EXPLOSIONS.add(new StateType[] { StateTypes.BARRIER, StateTypes.BEDROCK });
/* 514 */     copy(SMELTS_TO_GLASS, TRIGGERS_AMBIENT_DESERT_SAND_BLOCK_SOUNDS);
/* 515 */     copy(SOUL_FIRE_BASE_BLOCKS, TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS);
/* 516 */     AIR.add(new StateType[] { StateTypes.AIR, StateTypes.VOID_AIR, StateTypes.CAVE_AIR });
/* 517 */     BUTTONS.addTag(WOODEN_BUTTONS).addTag(STONE_BUTTONS);
/* 518 */     DOORS.addTag(WOODEN_DOORS).add(new StateType[] { StateTypes.COPPER_DOOR, StateTypes.EXPOSED_COPPER_DOOR, StateTypes.WEATHERED_COPPER_DOOR, StateTypes.OXIDIZED_COPPER_DOOR, StateTypes.WAXED_COPPER_DOOR, StateTypes.WAXED_EXPOSED_COPPER_DOOR, StateTypes.WAXED_WEATHERED_COPPER_DOOR, StateTypes.WAXED_OXIDIZED_COPPER_DOOR, StateTypes.IRON_DOOR });
/* 519 */     LOGS_THAT_BURN.addTag(DARK_OAK_LOGS).addTag(PALE_OAK_LOGS).addTag(OAK_LOGS).addTag(ACACIA_LOGS).addTag(BIRCH_LOGS).addTag(JUNGLE_LOGS).addTag(SPRUCE_LOGS).addTag(MANGROVE_LOGS).addTag(CHERRY_LOGS);
/* 520 */     SLABS.addTag(WOODEN_SLABS).add(new StateType[] { StateTypes.BAMBOO_MOSAIC_SLAB, StateTypes.STONE_SLAB, StateTypes.SMOOTH_STONE_SLAB, StateTypes.STONE_BRICK_SLAB, StateTypes.SANDSTONE_SLAB, StateTypes.PURPUR_SLAB, StateTypes.QUARTZ_SLAB, StateTypes.RED_SANDSTONE_SLAB, StateTypes.BRICK_SLAB, StateTypes.COBBLESTONE_SLAB, StateTypes.NETHER_BRICK_SLAB, StateTypes.PETRIFIED_OAK_SLAB, StateTypes.PRISMARINE_SLAB, StateTypes.PRISMARINE_BRICK_SLAB, StateTypes.DARK_PRISMARINE_SLAB, StateTypes.POLISHED_GRANITE_SLAB, StateTypes.SMOOTH_RED_SANDSTONE_SLAB, StateTypes.MOSSY_STONE_BRICK_SLAB, StateTypes.POLISHED_DIORITE_SLAB, StateTypes.MOSSY_COBBLESTONE_SLAB, StateTypes.END_STONE_BRICK_SLAB, StateTypes.SMOOTH_SANDSTONE_SLAB, StateTypes.SMOOTH_QUARTZ_SLAB, StateTypes.GRANITE_SLAB, StateTypes.ANDESITE_SLAB, StateTypes.RED_NETHER_BRICK_SLAB, StateTypes.POLISHED_ANDESITE_SLAB, StateTypes.DIORITE_SLAB, StateTypes.CUT_SANDSTONE_SLAB, StateTypes.CUT_RED_SANDSTONE_SLAB, StateTypes.BLACKSTONE_SLAB, StateTypes.POLISHED_BLACKSTONE_BRICK_SLAB, StateTypes.POLISHED_BLACKSTONE_SLAB, StateTypes.COBBLED_DEEPSLATE_SLAB, StateTypes.POLISHED_DEEPSLATE_SLAB, StateTypes.DEEPSLATE_TILE_SLAB, StateTypes.DEEPSLATE_BRICK_SLAB, StateTypes.WAXED_WEATHERED_CUT_COPPER_SLAB, StateTypes.WAXED_EXPOSED_CUT_COPPER_SLAB, StateTypes.WAXED_CUT_COPPER_SLAB, StateTypes.OXIDIZED_CUT_COPPER_SLAB, StateTypes.WEATHERED_CUT_COPPER_SLAB, StateTypes.EXPOSED_CUT_COPPER_SLAB, StateTypes.CUT_COPPER_SLAB, StateTypes.WAXED_OXIDIZED_CUT_COPPER_SLAB, StateTypes.MUD_BRICK_SLAB, StateTypes.TUFF_SLAB, StateTypes.POLISHED_TUFF_SLAB, StateTypes.TUFF_BRICK_SLAB, StateTypes.RESIN_BRICK_SLAB });
/* 521 */     STAIRS.addTag(WOODEN_STAIRS).add(new StateType[] { StateTypes.BAMBOO_MOSAIC_STAIRS, StateTypes.COBBLESTONE_STAIRS, StateTypes.SANDSTONE_STAIRS, StateTypes.NETHER_BRICK_STAIRS, StateTypes.STONE_BRICK_STAIRS, StateTypes.BRICK_STAIRS, StateTypes.PURPUR_STAIRS, StateTypes.QUARTZ_STAIRS, StateTypes.RED_SANDSTONE_STAIRS, StateTypes.PRISMARINE_BRICK_STAIRS, StateTypes.PRISMARINE_STAIRS, StateTypes.DARK_PRISMARINE_STAIRS, StateTypes.POLISHED_GRANITE_STAIRS, StateTypes.SMOOTH_RED_SANDSTONE_STAIRS, StateTypes.MOSSY_STONE_BRICK_STAIRS, StateTypes.POLISHED_DIORITE_STAIRS, StateTypes.MOSSY_COBBLESTONE_STAIRS, StateTypes.END_STONE_BRICK_STAIRS, StateTypes.STONE_STAIRS, StateTypes.SMOOTH_SANDSTONE_STAIRS, StateTypes.SMOOTH_QUARTZ_STAIRS, StateTypes.GRANITE_STAIRS, StateTypes.ANDESITE_STAIRS, StateTypes.RED_NETHER_BRICK_STAIRS, StateTypes.POLISHED_ANDESITE_STAIRS, StateTypes.DIORITE_STAIRS, StateTypes.BLACKSTONE_STAIRS, StateTypes.POLISHED_BLACKSTONE_BRICK_STAIRS, StateTypes.POLISHED_BLACKSTONE_STAIRS, StateTypes.COBBLED_DEEPSLATE_STAIRS, StateTypes.POLISHED_DEEPSLATE_STAIRS, StateTypes.DEEPSLATE_TILE_STAIRS, StateTypes.DEEPSLATE_BRICK_STAIRS, StateTypes.OXIDIZED_CUT_COPPER_STAIRS, StateTypes.WEATHERED_CUT_COPPER_STAIRS, StateTypes.EXPOSED_CUT_COPPER_STAIRS, StateTypes.CUT_COPPER_STAIRS, StateTypes.WAXED_WEATHERED_CUT_COPPER_STAIRS, StateTypes.WAXED_EXPOSED_CUT_COPPER_STAIRS, StateTypes.WAXED_CUT_COPPER_STAIRS, StateTypes.WAXED_OXIDIZED_CUT_COPPER_STAIRS, StateTypes.MUD_BRICK_STAIRS, StateTypes.TUFF_STAIRS, StateTypes.POLISHED_TUFF_STAIRS, StateTypes.TUFF_BRICK_STAIRS, StateTypes.RESIN_BRICK_STAIRS });
/* 522 */     TRAPDOORS.addTag(WOODEN_TRAPDOORS).add(new StateType[] { StateTypes.IRON_TRAPDOOR, StateTypes.COPPER_TRAPDOOR, StateTypes.EXPOSED_COPPER_TRAPDOOR, StateTypes.WEATHERED_COPPER_TRAPDOOR, StateTypes.OXIDIZED_COPPER_TRAPDOOR, StateTypes.WAXED_COPPER_TRAPDOOR, StateTypes.WAXED_EXPOSED_COPPER_TRAPDOOR, StateTypes.WAXED_WEATHERED_COPPER_TRAPDOOR, StateTypes.WAXED_OXIDIZED_COPPER_TRAPDOOR });
/* 523 */     FLOWERS.addTag(SMALL_FLOWERS).add(new StateType[] { StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.PEONY, StateTypes.ROSE_BUSH, StateTypes.PITCHER_PLANT, StateTypes.FLOWERING_AZALEA_LEAVES, StateTypes.FLOWERING_AZALEA, StateTypes.MANGROVE_PROPAGULE, StateTypes.CHERRY_LEAVES, StateTypes.PINK_PETALS, StateTypes.WILDFLOWERS, StateTypes.CHORUS_FLOWER, StateTypes.SPORE_BLOSSOM, StateTypes.CACTUS_FLOWER });
/* 524 */     FENCES.addTag(WOODEN_FENCES).add(new StateType[] { StateTypes.NETHER_BRICK_FENCE });
/* 525 */     DAMPENS_VIBRATIONS.addTag(WOOL).addTag(WOOL_CARPETS);
/* 526 */     MOB_INTERACTABLE_DOORS.addTag(WOODEN_DOORS).add(new StateType[] { StateTypes.COPPER_DOOR, StateTypes.EXPOSED_COPPER_DOOR, StateTypes.WEATHERED_COPPER_DOOR, StateTypes.OXIDIZED_COPPER_DOOR, StateTypes.WAXED_COPPER_DOOR, StateTypes.WAXED_EXPOSED_COPPER_DOOR, StateTypes.WAXED_WEATHERED_COPPER_DOOR, StateTypes.WAXED_OXIDIZED_COPPER_DOOR });
/* 527 */     PRESSURE_PLATES.addTag(WOODEN_PRESSURE_PLATES).addTag(STONE_PRESSURE_PLATES).add(new StateType[] { StateTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, StateTypes.HEAVY_WEIGHTED_PRESSURE_PLATE });
/* 528 */     ENDERMAN_HOLDABLE.addTag(SMALL_FLOWERS).addTag(DIRT).add(new StateType[] { StateTypes.SAND, StateTypes.RED_SAND, StateTypes.GRAVEL, StateTypes.BROWN_MUSHROOM, StateTypes.RED_MUSHROOM, StateTypes.TNT, StateTypes.CACTUS, StateTypes.CLAY, StateTypes.PUMPKIN, StateTypes.CARVED_PUMPKIN, StateTypes.MELON, StateTypes.CRIMSON_FUNGUS, StateTypes.CRIMSON_NYLIUM, StateTypes.CRIMSON_ROOTS, StateTypes.WARPED_FUNGUS, StateTypes.WARPED_NYLIUM, StateTypes.WARPED_ROOTS, StateTypes.CACTUS_FLOWER });
/* 529 */     CORALS.addTag(CORAL_PLANTS).add(new StateType[] { StateTypes.TUBE_CORAL_FAN, StateTypes.BRAIN_CORAL_FAN, StateTypes.BUBBLE_CORAL_FAN, StateTypes.FIRE_CORAL_FAN, StateTypes.HORN_CORAL_FAN });
/* 530 */     BAMBOO_PLANTABLE_ON.addTag(SAND).addTag(DIRT).add(new StateType[] { StateTypes.BAMBOO, StateTypes.BAMBOO_SAPLING, StateTypes.GRAVEL, StateTypes.SUSPICIOUS_GRAVEL });
/* 531 */     SIGNS.addTag(STANDING_SIGNS).addTag(WALL_SIGNS);
/* 532 */     ALL_HANGING_SIGNS.addTag(CEILING_HANGING_SIGNS).addTag(WALL_HANGING_SIGNS);
/* 533 */     DRAGON_TRANSPARENT.addTag(FIRE).add(new StateType[] { StateTypes.LIGHT });
/* 534 */     BEE_GROWABLES.addTag(CROPS).add(new StateType[] { StateTypes.SWEET_BERRY_BUSH, StateTypes.CAVE_VINES, StateTypes.CAVE_VINES_PLANT });
/* 535 */     FALL_DAMAGE_RESETTING.addTag(CLIMBABLE).add(new StateType[] { StateTypes.SWEET_BERRY_BUSH, StateTypes.COBWEB });
/* 536 */     GUARDED_BY_PIGLINS.addTag(SHULKER_BOXES).addTag(GOLD_ORES).add(new StateType[] { StateTypes.GOLD_BLOCK, StateTypes.BARREL, StateTypes.CHEST, StateTypes.ENDER_CHEST, StateTypes.GILDED_BLACKSTONE, StateTypes.TRAPPED_CHEST, StateTypes.RAW_GOLD_BLOCK });
/* 537 */     PREVENT_MOB_SPAWNING_INSIDE.addTag(RAILS);
/* 538 */     UNSTABLE_BOTTOM_CENTER.addTag(FENCE_GATES);
/* 539 */     INFINIBURN_NETHER.addTag(INFINIBURN_OVERWORLD);
/* 540 */     INFINIBURN_END.addTag(INFINIBURN_OVERWORLD).add(new StateType[] { StateTypes.BEDROCK });
/* 541 */     OVERWORLD_CARVER_REPLACEABLES.addTag(BASE_STONE_OVERWORLD).addTag(DIRT).addTag(SAND).addTag(TERRACOTTA).addTag(IRON_ORES).addTag(COPPER_ORES).addTag(SNOW).add(new StateType[] { StateTypes.WATER, StateTypes.GRAVEL, StateTypes.SUSPICIOUS_GRAVEL, StateTypes.SANDSTONE, StateTypes.RED_SANDSTONE, StateTypes.CALCITE, StateTypes.PACKED_ICE, StateTypes.RAW_IRON_BLOCK, StateTypes.RAW_COPPER_BLOCK });
/* 542 */     NETHER_CARVER_REPLACEABLES.addTag(BASE_STONE_OVERWORLD).addTag(BASE_STONE_NETHER).addTag(DIRT).addTag(NYLIUM).addTag(WART_BLOCKS).add(new StateType[] { StateTypes.SOUL_SAND, StateTypes.SOUL_SOIL });
/* 543 */     COMBINATION_STEP_SOUND_BLOCKS.addTag(WOOL_CARPETS).add(new StateType[] { StateTypes.MOSS_CARPET, StateTypes.PALE_MOSS_CARPET, StateTypes.SNOW, StateTypes.NETHER_SPROUTS, StateTypes.WARPED_ROOTS, StateTypes.CRIMSON_ROOTS, StateTypes.RESIN_CLUMP });
/* 544 */     CAMEL_SAND_STEP_SOUND_BLOCKS.addTag(SAND).addTag(CONCRETE_POWDER);
/* 545 */     OCCLUDES_VIBRATION_SIGNALS.addTag(WOOL);
/* 546 */     DRIPSTONE_REPLACEABLE_BLOCKS.addTag(BASE_STONE_OVERWORLD);
/* 547 */     MOSS_REPLACEABLE.addTag(BASE_STONE_OVERWORLD).addTag(CAVE_VINES).addTag(DIRT);
/* 548 */     AZALEA_ROOT_REPLACEABLE.addTag(BASE_STONE_OVERWORLD).addTag(DIRT).addTag(TERRACOTTA).add(new StateType[] { StateTypes.RED_SAND, StateTypes.CLAY, StateTypes.GRAVEL, StateTypes.SAND, StateTypes.SNOW_BLOCK, StateTypes.POWDER_SNOW });
/* 549 */     BIG_DRIPLEAF_PLACEABLE.addTag(SMALL_DRIPLEAF_PLACEABLE).add(new StateType[] { StateTypes.DIRT, StateTypes.GRASS_BLOCK, StateTypes.PODZOL, StateTypes.COARSE_DIRT, StateTypes.MYCELIUM, StateTypes.ROOTED_DIRT, StateTypes.MOSS_BLOCK, StateTypes.MUD, StateTypes.MUDDY_MANGROVE_ROOTS, StateTypes.FARMLAND });
/* 550 */     MINEABLE_HOE.addTag(LEAVES).add(new StateType[] { StateTypes.NETHER_WART_BLOCK, StateTypes.WARPED_WART_BLOCK, StateTypes.HAY_BLOCK, StateTypes.DRIED_KELP_BLOCK, StateTypes.TARGET, StateTypes.SHROOMLIGHT, StateTypes.SPONGE, StateTypes.WET_SPONGE, StateTypes.SCULK_SENSOR, StateTypes.CALIBRATED_SCULK_SENSOR, StateTypes.MOSS_BLOCK, StateTypes.MOSS_CARPET, StateTypes.PALE_MOSS_BLOCK, StateTypes.PALE_MOSS_CARPET, StateTypes.SCULK, StateTypes.SCULK_CATALYST, StateTypes.SCULK_VEIN, StateTypes.SCULK_SHRIEKER });
/* 551 */     MINEABLE_PICKAXE.addTag(STONE_BUTTONS).addTag(WALLS).addTag(SHULKER_BOXES).addTag(ANVIL).addTag(CAULDRONS).addTag(RAILS).add(new StateType[] { StateTypes.STONE, StateTypes.GRANITE, StateTypes.POLISHED_GRANITE, StateTypes.DIORITE, StateTypes.POLISHED_DIORITE, StateTypes.ANDESITE, StateTypes.POLISHED_ANDESITE, StateTypes.COBBLESTONE, StateTypes.GOLD_ORE, StateTypes.DEEPSLATE_GOLD_ORE, StateTypes.IRON_ORE, StateTypes.DEEPSLATE_IRON_ORE, StateTypes.COAL_ORE, StateTypes.DEEPSLATE_COAL_ORE, StateTypes.NETHER_GOLD_ORE, StateTypes.LAPIS_ORE, StateTypes.DEEPSLATE_LAPIS_ORE, StateTypes.LAPIS_BLOCK, StateTypes.DISPENSER, StateTypes.SANDSTONE, StateTypes.CHISELED_SANDSTONE, StateTypes.CUT_SANDSTONE, StateTypes.GOLD_BLOCK, StateTypes.IRON_BLOCK, StateTypes.BRICKS, StateTypes.MOSSY_COBBLESTONE, StateTypes.OBSIDIAN, StateTypes.SPAWNER, StateTypes.DIAMOND_ORE, StateTypes.DEEPSLATE_DIAMOND_ORE, StateTypes.DIAMOND_BLOCK, StateTypes.FURNACE, StateTypes.COBBLESTONE_STAIRS, StateTypes.STONE_PRESSURE_PLATE, StateTypes.IRON_DOOR, StateTypes.REDSTONE_ORE, StateTypes.DEEPSLATE_REDSTONE_ORE, StateTypes.NETHERRACK, StateTypes.BASALT, StateTypes.POLISHED_BASALT, StateTypes.STONE_BRICKS, StateTypes.MOSSY_STONE_BRICKS, StateTypes.CRACKED_STONE_BRICKS, StateTypes.CHISELED_STONE_BRICKS, StateTypes.IRON_BARS, StateTypes.CHAIN, StateTypes.BRICK_STAIRS, StateTypes.STONE_BRICK_STAIRS, StateTypes.NETHER_BRICKS, StateTypes.NETHER_BRICK_FENCE, StateTypes.NETHER_BRICK_STAIRS, StateTypes.ENCHANTING_TABLE, StateTypes.BREWING_STAND, StateTypes.END_STONE, StateTypes.SANDSTONE_STAIRS, StateTypes.EMERALD_ORE, StateTypes.DEEPSLATE_EMERALD_ORE, StateTypes.ENDER_CHEST, StateTypes.EMERALD_BLOCK, StateTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, StateTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, StateTypes.REDSTONE_BLOCK, StateTypes.NETHER_QUARTZ_ORE, StateTypes.HOPPER, StateTypes.QUARTZ_BLOCK, StateTypes.CHISELED_QUARTZ_BLOCK, StateTypes.QUARTZ_PILLAR, StateTypes.QUARTZ_STAIRS, StateTypes.DROPPER, StateTypes.WHITE_TERRACOTTA, StateTypes.ORANGE_TERRACOTTA, StateTypes.MAGENTA_TERRACOTTA, StateTypes.LIGHT_BLUE_TERRACOTTA, StateTypes.YELLOW_TERRACOTTA, StateTypes.LIME_TERRACOTTA, StateTypes.PINK_TERRACOTTA, StateTypes.GRAY_TERRACOTTA, StateTypes.LIGHT_GRAY_TERRACOTTA, StateTypes.CYAN_TERRACOTTA, StateTypes.PURPLE_TERRACOTTA, StateTypes.BLUE_TERRACOTTA, StateTypes.BROWN_TERRACOTTA, StateTypes.GREEN_TERRACOTTA, StateTypes.RED_TERRACOTTA, StateTypes.BLACK_TERRACOTTA, StateTypes.IRON_TRAPDOOR, StateTypes.PRISMARINE, StateTypes.PRISMARINE_BRICKS, StateTypes.DARK_PRISMARINE, StateTypes.PRISMARINE_STAIRS, StateTypes.PRISMARINE_BRICK_STAIRS, StateTypes.DARK_PRISMARINE_STAIRS, StateTypes.PRISMARINE_SLAB, StateTypes.PRISMARINE_BRICK_SLAB, StateTypes.DARK_PRISMARINE_SLAB, StateTypes.TERRACOTTA, StateTypes.COAL_BLOCK, StateTypes.RED_SANDSTONE, StateTypes.CHISELED_RED_SANDSTONE, StateTypes.CUT_RED_SANDSTONE, StateTypes.RED_SANDSTONE_STAIRS, StateTypes.STONE_SLAB, StateTypes.SMOOTH_STONE_SLAB, StateTypes.SANDSTONE_SLAB, StateTypes.CUT_SANDSTONE_SLAB, StateTypes.PETRIFIED_OAK_SLAB, StateTypes.COBBLESTONE_SLAB, StateTypes.BRICK_SLAB, StateTypes.STONE_BRICK_SLAB, StateTypes.NETHER_BRICK_SLAB, StateTypes.QUARTZ_SLAB, StateTypes.RED_SANDSTONE_SLAB, StateTypes.CUT_RED_SANDSTONE_SLAB, StateTypes.PURPUR_SLAB, StateTypes.SMOOTH_STONE, StateTypes.SMOOTH_SANDSTONE, StateTypes.SMOOTH_QUARTZ, StateTypes.SMOOTH_RED_SANDSTONE, StateTypes.PURPUR_BLOCK, StateTypes.PURPUR_PILLAR, StateTypes.PURPUR_STAIRS, StateTypes.END_STONE_BRICKS, StateTypes.MAGMA_BLOCK, StateTypes.RED_NETHER_BRICKS, StateTypes.BONE_BLOCK, StateTypes.OBSERVER, StateTypes.WHITE_GLAZED_TERRACOTTA, StateTypes.ORANGE_GLAZED_TERRACOTTA, StateTypes.MAGENTA_GLAZED_TERRACOTTA, StateTypes.LIGHT_BLUE_GLAZED_TERRACOTTA, StateTypes.YELLOW_GLAZED_TERRACOTTA, StateTypes.LIME_GLAZED_TERRACOTTA, StateTypes.PINK_GLAZED_TERRACOTTA, StateTypes.GRAY_GLAZED_TERRACOTTA, StateTypes.LIGHT_GRAY_GLAZED_TERRACOTTA, StateTypes.CYAN_GLAZED_TERRACOTTA, StateTypes.PURPLE_GLAZED_TERRACOTTA, StateTypes.BLUE_GLAZED_TERRACOTTA, StateTypes.BROWN_GLAZED_TERRACOTTA, StateTypes.GREEN_GLAZED_TERRACOTTA, StateTypes.RED_GLAZED_TERRACOTTA, StateTypes.BLACK_GLAZED_TERRACOTTA, StateTypes.WHITE_CONCRETE, StateTypes.ORANGE_CONCRETE, StateTypes.MAGENTA_CONCRETE, StateTypes.LIGHT_BLUE_CONCRETE, StateTypes.YELLOW_CONCRETE, StateTypes.LIME_CONCRETE, StateTypes.PINK_CONCRETE, StateTypes.GRAY_CONCRETE, StateTypes.LIGHT_GRAY_CONCRETE, StateTypes.CYAN_CONCRETE, StateTypes.PURPLE_CONCRETE, StateTypes.BLUE_CONCRETE, StateTypes.BROWN_CONCRETE, StateTypes.GREEN_CONCRETE, StateTypes.RED_CONCRETE, StateTypes.BLACK_CONCRETE, StateTypes.DEAD_TUBE_CORAL_BLOCK, StateTypes.DEAD_BRAIN_CORAL_BLOCK, StateTypes.DEAD_BUBBLE_CORAL_BLOCK, StateTypes.DEAD_FIRE_CORAL_BLOCK, StateTypes.DEAD_HORN_CORAL_BLOCK, StateTypes.TUBE_CORAL_BLOCK, StateTypes.BRAIN_CORAL_BLOCK, StateTypes.BUBBLE_CORAL_BLOCK, StateTypes.FIRE_CORAL_BLOCK, StateTypes.HORN_CORAL_BLOCK, StateTypes.DEAD_TUBE_CORAL, StateTypes.DEAD_BRAIN_CORAL, StateTypes.DEAD_BUBBLE_CORAL, StateTypes.DEAD_FIRE_CORAL, StateTypes.DEAD_HORN_CORAL, StateTypes.DEAD_TUBE_CORAL_FAN, StateTypes.DEAD_BRAIN_CORAL_FAN, StateTypes.DEAD_BUBBLE_CORAL_FAN, StateTypes.DEAD_FIRE_CORAL_FAN, StateTypes.DEAD_HORN_CORAL_FAN, StateTypes.DEAD_TUBE_CORAL_WALL_FAN, StateTypes.DEAD_BRAIN_CORAL_WALL_FAN, StateTypes.DEAD_BUBBLE_CORAL_WALL_FAN, StateTypes.DEAD_FIRE_CORAL_WALL_FAN, StateTypes.DEAD_HORN_CORAL_WALL_FAN, StateTypes.POLISHED_GRANITE_STAIRS, StateTypes.SMOOTH_RED_SANDSTONE_STAIRS, StateTypes.MOSSY_STONE_BRICK_STAIRS, StateTypes.POLISHED_DIORITE_STAIRS, StateTypes.MOSSY_COBBLESTONE_STAIRS, StateTypes.END_STONE_BRICK_STAIRS, StateTypes.STONE_STAIRS, StateTypes.SMOOTH_SANDSTONE_STAIRS, StateTypes.SMOOTH_QUARTZ_STAIRS, StateTypes.GRANITE_STAIRS, StateTypes.ANDESITE_STAIRS, StateTypes.RED_NETHER_BRICK_STAIRS, StateTypes.POLISHED_ANDESITE_STAIRS, StateTypes.DIORITE_STAIRS, StateTypes.POLISHED_GRANITE_SLAB, StateTypes.SMOOTH_RED_SANDSTONE_SLAB, StateTypes.MOSSY_STONE_BRICK_SLAB, StateTypes.POLISHED_DIORITE_SLAB, StateTypes.MOSSY_COBBLESTONE_SLAB, StateTypes.END_STONE_BRICK_SLAB, StateTypes.SMOOTH_SANDSTONE_SLAB, StateTypes.SMOOTH_QUARTZ_SLAB, StateTypes.GRANITE_SLAB, StateTypes.ANDESITE_SLAB, StateTypes.RED_NETHER_BRICK_SLAB, StateTypes.POLISHED_ANDESITE_SLAB, StateTypes.DIORITE_SLAB, StateTypes.SMOKER, StateTypes.BLAST_FURNACE, StateTypes.GRINDSTONE, StateTypes.STONECUTTER, StateTypes.BELL, StateTypes.LANTERN, StateTypes.SOUL_LANTERN, StateTypes.WARPED_NYLIUM, StateTypes.CRIMSON_NYLIUM, StateTypes.NETHERITE_BLOCK, StateTypes.ANCIENT_DEBRIS, StateTypes.CRYING_OBSIDIAN, StateTypes.RESPAWN_ANCHOR, StateTypes.LODESTONE, StateTypes.BLACKSTONE, StateTypes.BLACKSTONE_STAIRS, StateTypes.BLACKSTONE_SLAB, StateTypes.POLISHED_BLACKSTONE, StateTypes.POLISHED_BLACKSTONE_BRICKS, StateTypes.CRACKED_POLISHED_BLACKSTONE_BRICKS, StateTypes.CHISELED_POLISHED_BLACKSTONE, StateTypes.POLISHED_BLACKSTONE_BRICK_SLAB, StateTypes.POLISHED_BLACKSTONE_BRICK_STAIRS, StateTypes.GILDED_BLACKSTONE, StateTypes.POLISHED_BLACKSTONE_STAIRS, StateTypes.POLISHED_BLACKSTONE_SLAB, StateTypes.POLISHED_BLACKSTONE_PRESSURE_PLATE, StateTypes.CHISELED_NETHER_BRICKS, StateTypes.CRACKED_NETHER_BRICKS, StateTypes.QUARTZ_BRICKS, StateTypes.TUFF, StateTypes.CALCITE, StateTypes.OXIDIZED_COPPER, StateTypes.WEATHERED_COPPER, StateTypes.EXPOSED_COPPER, StateTypes.COPPER_BLOCK, StateTypes.COPPER_ORE, StateTypes.DEEPSLATE_COPPER_ORE, StateTypes.OXIDIZED_CUT_COPPER, StateTypes.WEATHERED_CUT_COPPER, StateTypes.EXPOSED_CUT_COPPER, StateTypes.CUT_COPPER, StateTypes.OXIDIZED_CUT_COPPER_STAIRS, StateTypes.WEATHERED_CUT_COPPER_STAIRS, StateTypes.EXPOSED_CUT_COPPER_STAIRS, StateTypes.CUT_COPPER_STAIRS, StateTypes.OXIDIZED_CUT_COPPER_SLAB, StateTypes.WEATHERED_CUT_COPPER_SLAB, StateTypes.EXPOSED_CUT_COPPER_SLAB, StateTypes.CUT_COPPER_SLAB, StateTypes.WAXED_COPPER_BLOCK, StateTypes.WAXED_WEATHERED_COPPER, StateTypes.WAXED_EXPOSED_COPPER, StateTypes.WAXED_OXIDIZED_COPPER, StateTypes.WAXED_OXIDIZED_CUT_COPPER, StateTypes.WAXED_WEATHERED_CUT_COPPER, StateTypes.WAXED_EXPOSED_CUT_COPPER, StateTypes.WAXED_CUT_COPPER, StateTypes.WAXED_OXIDIZED_CUT_COPPER_STAIRS, StateTypes.WAXED_WEATHERED_CUT_COPPER_STAIRS, StateTypes.WAXED_EXPOSED_CUT_COPPER_STAIRS, StateTypes.WAXED_CUT_COPPER_STAIRS, StateTypes.WAXED_OXIDIZED_CUT_COPPER_SLAB, StateTypes.WAXED_WEATHERED_CUT_COPPER_SLAB, StateTypes.WAXED_EXPOSED_CUT_COPPER_SLAB, StateTypes.WAXED_CUT_COPPER_SLAB, StateTypes.LIGHTNING_ROD, StateTypes.POINTED_DRIPSTONE, StateTypes.DRIPSTONE_BLOCK, StateTypes.DEEPSLATE, StateTypes.COBBLED_DEEPSLATE, StateTypes.COBBLED_DEEPSLATE_STAIRS, StateTypes.COBBLED_DEEPSLATE_SLAB, StateTypes.POLISHED_DEEPSLATE, StateTypes.POLISHED_DEEPSLATE_STAIRS, StateTypes.POLISHED_DEEPSLATE_SLAB, StateTypes.DEEPSLATE_TILES, StateTypes.DEEPSLATE_TILE_STAIRS, StateTypes.DEEPSLATE_TILE_SLAB, StateTypes.DEEPSLATE_BRICKS, StateTypes.DEEPSLATE_BRICK_STAIRS, StateTypes.DEEPSLATE_BRICK_SLAB, StateTypes.CHISELED_DEEPSLATE, StateTypes.CRACKED_DEEPSLATE_BRICKS, StateTypes.CRACKED_DEEPSLATE_TILES, StateTypes.SMOOTH_BASALT, StateTypes.RAW_IRON_BLOCK, StateTypes.RAW_COPPER_BLOCK, StateTypes.RAW_GOLD_BLOCK, StateTypes.ICE, StateTypes.PACKED_ICE, StateTypes.BLUE_ICE, StateTypes.PISTON, StateTypes.STICKY_PISTON, StateTypes.PISTON_HEAD, StateTypes.AMETHYST_CLUSTER, StateTypes.SMALL_AMETHYST_BUD, StateTypes.MEDIUM_AMETHYST_BUD, StateTypes.LARGE_AMETHYST_BUD, StateTypes.AMETHYST_BLOCK, StateTypes.BUDDING_AMETHYST, StateTypes.INFESTED_COBBLESTONE, StateTypes.INFESTED_CHISELED_STONE_BRICKS, StateTypes.INFESTED_CRACKED_STONE_BRICKS, StateTypes.INFESTED_DEEPSLATE, StateTypes.INFESTED_STONE, StateTypes.INFESTED_MOSSY_STONE_BRICKS, StateTypes.INFESTED_STONE_BRICKS, StateTypes.CONDUIT, StateTypes.MUD_BRICKS, StateTypes.MUD_BRICK_STAIRS, StateTypes.MUD_BRICK_SLAB, StateTypes.PACKED_MUD, StateTypes.CRAFTER, StateTypes.TUFF_SLAB, StateTypes.TUFF_STAIRS, StateTypes.TUFF_WALL, StateTypes.CHISELED_TUFF, StateTypes.POLISHED_TUFF, StateTypes.POLISHED_TUFF_SLAB, StateTypes.POLISHED_TUFF_STAIRS, StateTypes.POLISHED_TUFF_WALL, StateTypes.TUFF_BRICKS, StateTypes.TUFF_BRICK_SLAB, StateTypes.TUFF_BRICK_STAIRS, StateTypes.TUFF_BRICK_WALL, StateTypes.CHISELED_TUFF_BRICKS, StateTypes.CHISELED_COPPER, StateTypes.EXPOSED_CHISELED_COPPER, StateTypes.WEATHERED_CHISELED_COPPER, StateTypes.OXIDIZED_CHISELED_COPPER, StateTypes.WAXED_CHISELED_COPPER, StateTypes.WAXED_EXPOSED_CHISELED_COPPER, StateTypes.WAXED_WEATHERED_CHISELED_COPPER, StateTypes.WAXED_OXIDIZED_CHISELED_COPPER, StateTypes.COPPER_GRATE, StateTypes.EXPOSED_COPPER_GRATE, StateTypes.WEATHERED_COPPER_GRATE, StateTypes.OXIDIZED_COPPER_GRATE, StateTypes.WAXED_COPPER_GRATE, StateTypes.WAXED_EXPOSED_COPPER_GRATE, StateTypes.WAXED_WEATHERED_COPPER_GRATE, StateTypes.WAXED_OXIDIZED_COPPER_GRATE, StateTypes.COPPER_BULB, StateTypes.EXPOSED_COPPER_BULB, StateTypes.WEATHERED_COPPER_BULB, StateTypes.OXIDIZED_COPPER_BULB, StateTypes.WAXED_COPPER_BULB, StateTypes.WAXED_EXPOSED_COPPER_BULB, StateTypes.WAXED_WEATHERED_COPPER_BULB, StateTypes.WAXED_OXIDIZED_COPPER_BULB, StateTypes.COPPER_DOOR, StateTypes.EXPOSED_COPPER_DOOR, StateTypes.WEATHERED_COPPER_DOOR, StateTypes.OXIDIZED_COPPER_DOOR, StateTypes.WAXED_COPPER_DOOR, StateTypes.WAXED_EXPOSED_COPPER_DOOR, StateTypes.WAXED_WEATHERED_COPPER_DOOR, StateTypes.WAXED_OXIDIZED_COPPER_DOOR, StateTypes.COPPER_TRAPDOOR, StateTypes.EXPOSED_COPPER_TRAPDOOR, StateTypes.WEATHERED_COPPER_TRAPDOOR, StateTypes.OXIDIZED_COPPER_TRAPDOOR, StateTypes.WAXED_COPPER_TRAPDOOR, StateTypes.WAXED_EXPOSED_COPPER_TRAPDOOR, StateTypes.WAXED_WEATHERED_COPPER_TRAPDOOR, StateTypes.WAXED_OXIDIZED_COPPER_TRAPDOOR, StateTypes.HEAVY_CORE, StateTypes.RESIN_BRICKS, StateTypes.RESIN_BRICK_SLAB, StateTypes.RESIN_BRICK_WALL, StateTypes.RESIN_BRICK_STAIRS, StateTypes.CHISELED_RESIN_BRICKS });
/* 552 */     MINEABLE_SHOVEL.addTag(CONCRETE_POWDER).add(new StateType[] { StateTypes.CLAY, StateTypes.DIRT, StateTypes.COARSE_DIRT, StateTypes.PODZOL, StateTypes.FARMLAND, StateTypes.GRASS_BLOCK, StateTypes.GRAVEL, StateTypes.MYCELIUM, StateTypes.SAND, StateTypes.RED_SAND, StateTypes.SNOW_BLOCK, StateTypes.SNOW, StateTypes.SOUL_SAND, StateTypes.DIRT_PATH, StateTypes.SOUL_SOIL, StateTypes.ROOTED_DIRT, StateTypes.MUDDY_MANGROVE_ROOTS, StateTypes.MUD, StateTypes.SUSPICIOUS_SAND, StateTypes.SUSPICIOUS_GRAVEL });
/* 553 */     SWORD_EFFICIENT.addTag(LEAVES).add(new StateType[] { StateTypes.VINE, StateTypes.GLOW_LICHEN, StateTypes.PUMPKIN, StateTypes.CARVED_PUMPKIN, StateTypes.JACK_O_LANTERN, StateTypes.MELON, StateTypes.COCOA, StateTypes.BIG_DRIPLEAF, StateTypes.BIG_DRIPLEAF_STEM, StateTypes.CHORUS_PLANT, StateTypes.CHORUS_FLOWER });
/* 554 */     INCORRECT_FOR_IRON_TOOL.addTag(NEEDS_DIAMOND_TOOL);
/* 555 */     INCORRECT_FOR_STONE_TOOL.addTag(NEEDS_DIAMOND_TOOL).addTag(NEEDS_IRON_TOOL);
/* 556 */     INCORRECT_FOR_GOLD_TOOL.addTag(NEEDS_DIAMOND_TOOL).addTag(NEEDS_IRON_TOOL).addTag(NEEDS_STONE_TOOL);
/* 557 */     copy(INCORRECT_FOR_GOLD_TOOL, INCORRECT_FOR_WOODEN_TOOL);
/* 558 */     SCULK_REPLACEABLE.addTag(BASE_STONE_OVERWORLD).addTag(DIRT).addTag(TERRACOTTA).addTag(NYLIUM).addTag(BASE_STONE_NETHER).add(new StateType[] { StateTypes.SAND, StateTypes.RED_SAND, StateTypes.GRAVEL, StateTypes.SOUL_SAND, StateTypes.SOUL_SOIL, StateTypes.CALCITE, StateTypes.SMOOTH_BASALT, StateTypes.CLAY, StateTypes.DRIPSTONE_BLOCK, StateTypes.END_STONE, StateTypes.RED_SANDSTONE, StateTypes.SANDSTONE });
/* 559 */     ARMADILLO_SPAWNABLE_ON.addTag(ANIMALS_SPAWNABLE_ON).addTag(BADLANDS_TERRACOTTA).add(new StateType[] { StateTypes.RED_SAND, StateTypes.COARSE_DIRT });
/* 560 */     GOATS_SPAWNABLE_ON.addTag(ANIMALS_SPAWNABLE_ON).add(new StateType[] { StateTypes.STONE, StateTypes.SNOW, StateTypes.SNOW_BLOCK, StateTypes.PACKED_ICE, StateTypes.GRAVEL });
/* 561 */     copy(DRIPSTONE_REPLACEABLE_BLOCKS, BATS_SPAWNABLE_ON);
/* 562 */     CAMELS_SPAWNABLE_ON.addTag(SAND);
/* 563 */     AZALEA_GROWS_ON.addTag(DIRT).addTag(SAND).addTag(TERRACOTTA).add(new StateType[] { StateTypes.SNOW_BLOCK, StateTypes.POWDER_SNOW });
/* 564 */     DRY_VEGETATION_MAY_PLACE_ON.addTag(SAND).addTag(TERRACOTTA).addTag(DIRT).add(new StateType[] { StateTypes.FARMLAND });
/* 565 */     SNAPS_GOAT_HORN.addTag(OVERWORLD_NATURAL_LOGS).add(new StateType[] { StateTypes.STONE, StateTypes.PACKED_ICE, StateTypes.IRON_ORE, StateTypes.COAL_ORE, StateTypes.COPPER_ORE, StateTypes.EMERALD_ORE });
/* 566 */     REPLACEABLE_BY_TREES.addTag(LEAVES).addTag(SMALL_FLOWERS).add(new StateType[] { StateTypes.PALE_MOSS_CARPET, StateTypes.SHORT_GRASS, StateTypes.FERN, StateTypes.DEAD_BUSH, StateTypes.VINE, StateTypes.GLOW_LICHEN, StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.ROSE_BUSH, StateTypes.PEONY, StateTypes.TALL_GRASS, StateTypes.LARGE_FERN, StateTypes.HANGING_ROOTS, StateTypes.PITCHER_PLANT, StateTypes.WATER, StateTypes.SEAGRASS, StateTypes.TALL_SEAGRASS, StateTypes.BUSH, StateTypes.FIREFLY_BUSH, StateTypes.WARPED_ROOTS, StateTypes.NETHER_SPROUTS, StateTypes.CRIMSON_ROOTS, StateTypes.LEAF_LITTER, StateTypes.SHORT_DRY_GRASS, StateTypes.TALL_DRY_GRASS });
/* 567 */     REPLACEABLE_BY_MUSHROOMS.addTag(LEAVES).addTag(SMALL_FLOWERS).add(new StateType[] { StateTypes.PALE_MOSS_CARPET, StateTypes.SHORT_GRASS, StateTypes.FERN, StateTypes.DEAD_BUSH, StateTypes.VINE, StateTypes.GLOW_LICHEN, StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.ROSE_BUSH, StateTypes.PEONY, StateTypes.TALL_GRASS, StateTypes.LARGE_FERN, StateTypes.HANGING_ROOTS, StateTypes.PITCHER_PLANT, StateTypes.WATER, StateTypes.SEAGRASS, StateTypes.TALL_SEAGRASS, StateTypes.BROWN_MUSHROOM, StateTypes.RED_MUSHROOM, StateTypes.BROWN_MUSHROOM_BLOCK, StateTypes.RED_MUSHROOM_BLOCK, StateTypes.WARPED_ROOTS, StateTypes.NETHER_SPROUTS, StateTypes.CRIMSON_ROOTS, StateTypes.LEAF_LITTER, StateTypes.SHORT_DRY_GRASS, StateTypes.TALL_DRY_GRASS, StateTypes.BUSH, StateTypes.FIREFLY_BUSH });
/* 568 */     ENCHANTMENT_POWER_TRANSMITTER.addTag(REPLACEABLE);
/* 569 */     DOES_NOT_BLOCK_HOPPERS.addTag(BEEHIVES);
/* 570 */     TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS.addTag(TERRACOTTA).add(new StateType[] { StateTypes.SAND, StateTypes.RED_SAND });
/* 571 */     LOGS.addTag(LOGS_THAT_BURN).addTag(CRIMSON_STEMS).addTag(WARPED_STEMS);
/* 572 */     UNDERWATER_BONEMEALS.addTag(CORALS).addTag(WALL_CORALS).add(new StateType[] { StateTypes.SEAGRASS });
/* 573 */     ALL_SIGNS.addTag(SIGNS).addTag(ALL_HANGING_SIGNS);
/* 574 */     WALL_POST_OVERRIDE.addTag(SIGNS).addTag(BANNERS).addTag(PRESSURE_PLATES).add(new StateType[] { StateTypes.TORCH, StateTypes.SOUL_TORCH, StateTypes.REDSTONE_TORCH, StateTypes.TRIPWIRE, StateTypes.CACTUS_FLOWER });
/* 575 */     LUSH_GROUND_REPLACEABLE.addTag(MOSS_REPLACEABLE).add(new StateType[] { StateTypes.CLAY, StateTypes.GRAVEL, StateTypes.SAND });
/* 576 */     SCULK_REPLACEABLE_WORLD_GEN.addTag(SCULK_REPLACEABLE).add(new StateType[] { StateTypes.DEEPSLATE_BRICKS, StateTypes.DEEPSLATE_TILES, StateTypes.COBBLED_DEEPSLATE, StateTypes.CRACKED_DEEPSLATE_BRICKS, StateTypes.CRACKED_DEEPSLATE_TILES, StateTypes.POLISHED_DEEPSLATE });
/* 577 */     COMPLETES_FIND_TREE_TUTORIAL.addTag(LOGS).addTag(LEAVES).addTag(WART_BLOCKS);
/* 578 */     MINEABLE_AXE.addTag(BANNERS).addTag(FENCE_GATES).addTag(LOGS).addTag(PLANKS).addTag(SIGNS).addTag(WOODEN_BUTTONS).addTag(WOODEN_DOORS).addTag(WOODEN_FENCES).addTag(WOODEN_PRESSURE_PLATES).addTag(WOODEN_SLABS).addTag(WOODEN_STAIRS).addTag(WOODEN_TRAPDOORS).addTag(ALL_HANGING_SIGNS).addTag(BAMBOO_BLOCKS).add(new StateType[] { StateTypes.NOTE_BLOCK, StateTypes.BAMBOO, StateTypes.BARREL, StateTypes.BEE_NEST, StateTypes.BEEHIVE, StateTypes.BIG_DRIPLEAF_STEM, StateTypes.BIG_DRIPLEAF, StateTypes.BOOKSHELF, StateTypes.BROWN_MUSHROOM_BLOCK, StateTypes.CAMPFIRE, StateTypes.CARTOGRAPHY_TABLE, StateTypes.CARVED_PUMPKIN, StateTypes.CHEST, StateTypes.CHORUS_FLOWER, StateTypes.CHORUS_PLANT, StateTypes.COCOA, StateTypes.COMPOSTER, StateTypes.CRAFTING_TABLE, StateTypes.DAYLIGHT_DETECTOR, StateTypes.FLETCHING_TABLE, StateTypes.GLOW_LICHEN, StateTypes.JACK_O_LANTERN, StateTypes.JUKEBOX, StateTypes.LADDER, StateTypes.LECTERN, StateTypes.LOOM, StateTypes.MELON, StateTypes.MUSHROOM_STEM, StateTypes.PUMPKIN, StateTypes.RED_MUSHROOM_BLOCK, StateTypes.SMITHING_TABLE, StateTypes.SOUL_CAMPFIRE, StateTypes.TRAPPED_CHEST, StateTypes.VINE, StateTypes.MANGROVE_ROOTS, StateTypes.BAMBOO_MOSAIC, StateTypes.BAMBOO_MOSAIC_SLAB, StateTypes.BAMBOO_MOSAIC_STAIRS, StateTypes.CHISELED_BOOKSHELF, StateTypes.CREAKING_HEART });
/* 579 */     LAVA_POOL_STONE_CANNOT_REPLACE.addTag(FEATURES_CANNOT_REPLACE).addTag(LEAVES).addTag(LOGS);
/* 580 */     PARROTS_SPAWNABLE_ON.addTag(LEAVES).addTag(LOGS).add(new StateType[] { StateTypes.GRASS_BLOCK, StateTypes.AIR });
/*     */     
/* 582 */     TALL_FLOWERS.add(new StateType[] { StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.PEONY, StateTypes.ROSE_BUSH, StateTypes.PITCHER_PLANT });
/* 583 */     AZALEA_GROWS_ON.addTag(DIRT).addTag(SAND).addTag(TERRACOTTA).add(new StateType[] { StateTypes.SNOW_BLOCK, StateTypes.POWDER_SNOW });
/* 584 */     REPLACEABLE_PLANTS.add(new StateType[] { StateTypes.SHORT_GRASS, StateTypes.FERN, StateTypes.DEAD_BUSH, StateTypes.VINE, StateTypes.GLOW_LICHEN, StateTypes.SUNFLOWER, StateTypes.LILAC, StateTypes.ROSE_BUSH, StateTypes.PEONY, StateTypes.TALL_GRASS, StateTypes.LARGE_FERN, StateTypes.HANGING_ROOTS, StateTypes.PITCHER_PLANT });
/*     */     
/* 586 */     GLASS_BLOCKS.add(new StateType[] { StateTypes.GLASS, StateTypes.WHITE_STAINED_GLASS, StateTypes.ORANGE_STAINED_GLASS, StateTypes.MAGENTA_STAINED_GLASS, StateTypes.LIGHT_BLUE_STAINED_GLASS, StateTypes.YELLOW_STAINED_GLASS, StateTypes.LIME_STAINED_GLASS, StateTypes.PINK_STAINED_GLASS, StateTypes.GRAY_STAINED_GLASS, StateTypes.LIGHT_GRAY_STAINED_GLASS, StateTypes.CYAN_STAINED_GLASS, StateTypes.PURPLE_STAINED_GLASS, StateTypes.BLUE_STAINED_GLASS, StateTypes.BROWN_STAINED_GLASS, StateTypes.GREEN_STAINED_GLASS, StateTypes.RED_STAINED_GLASS, StateTypes.BLACK_STAINED_GLASS, StateTypes.TINTED_GLASS });
/* 587 */     GLASS_PANES.add(new StateType[] { StateTypes.GLASS_PANE, StateTypes.WHITE_STAINED_GLASS_PANE, StateTypes.ORANGE_STAINED_GLASS_PANE, StateTypes.MAGENTA_STAINED_GLASS_PANE, StateTypes.LIGHT_BLUE_STAINED_GLASS_PANE, StateTypes.YELLOW_STAINED_GLASS_PANE, StateTypes.LIME_STAINED_GLASS_PANE, StateTypes.PINK_STAINED_GLASS_PANE, StateTypes.GRAY_STAINED_GLASS_PANE, StateTypes.LIGHT_GRAY_STAINED_GLASS_PANE, StateTypes.CYAN_STAINED_GLASS_PANE, StateTypes.PURPLE_STAINED_GLASS_PANE, StateTypes.BLUE_STAINED_GLASS_PANE, StateTypes.BROWN_STAINED_GLASS_PANE, StateTypes.GREEN_STAINED_GLASS_PANE, StateTypes.RED_STAINED_GLASS_PANE, StateTypes.BLACK_STAINED_GLASS_PANE });
/* 588 */     ALL_CORAL_PLANTS.add(new StateType[] { StateTypes.TUBE_CORAL, StateTypes.BRAIN_CORAL, StateTypes.BUBBLE_CORAL, StateTypes.FIRE_CORAL, StateTypes.HORN_CORAL, StateTypes.DEAD_TUBE_CORAL, StateTypes.DEAD_BRAIN_CORAL, StateTypes.DEAD_BUBBLE_CORAL, StateTypes.DEAD_FIRE_CORAL, StateTypes.DEAD_HORN_CORAL });
/* 589 */     DEAD_CORAL_PLANTS.add(new StateType[] { StateTypes.DEAD_TUBE_CORAL, StateTypes.DEAD_BRAIN_CORAL, StateTypes.DEAD_BUBBLE_CORAL, StateTypes.DEAD_FIRE_CORAL, StateTypes.DEAD_HORN_CORAL });
/* 590 */     V_1_20_5.add(new StateType[] { StateTypes.VAULT, StateTypes.HEAVY_CORE });
/* 591 */     V_1_21_2.add(new StateType[] { StateTypes.PALE_OAK_WOOD, StateTypes.PALE_OAK_PLANKS, StateTypes.PALE_OAK_SAPLING, StateTypes.PALE_OAK_LOG, StateTypes.STRIPPED_PALE_OAK_LOG, StateTypes.STRIPPED_PALE_OAK_WOOD, StateTypes.PALE_OAK_LEAVES, StateTypes.CREAKING_HEART, StateTypes.PALE_OAK_SIGN, StateTypes.PALE_OAK_WALL_SIGN, StateTypes.PALE_OAK_HANGING_SIGN, StateTypes.PALE_OAK_WALL_HANGING_SIGN, StateTypes.PALE_OAK_PRESSURE_PLATE, StateTypes.PALE_OAK_TRAPDOOR, StateTypes.POTTED_PALE_OAK_SAPLING, StateTypes.PALE_OAK_BUTTON, StateTypes.PALE_OAK_STAIRS, StateTypes.PALE_OAK_SLAB, StateTypes.PALE_OAK_FENCE_GATE, StateTypes.PALE_OAK_FENCE, StateTypes.PALE_OAK_DOOR, StateTypes.PALE_MOSS_BLOCK, StateTypes.PALE_MOSS_CARPET, StateTypes.PALE_HANGING_MOSS });
/* 592 */     V_1_21_4.add(new StateType[] { StateTypes.RESIN_CLUMP, StateTypes.RESIN_BLOCK, StateTypes.RESIN_BRICKS, StateTypes.RESIN_BRICK_STAIRS, StateTypes.RESIN_BRICK_SLAB, StateTypes.RESIN_BRICK_WALL, StateTypes.CHISELED_RESIN_BRICKS, StateTypes.OPEN_EYEBLOSSOM, StateTypes.CLOSED_EYEBLOSSOM, StateTypes.POTTED_OPEN_EYEBLOSSOM, StateTypes.POTTED_CLOSED_EYEBLOSSOM });
/* 593 */     V_1_21_5.add(new StateType[] { StateTypes.BUSH, StateTypes.FIREFLY_BUSH, StateTypes.SHORT_DRY_GRASS, StateTypes.TALL_DRY_GRASS, StateTypes.WILDFLOWERS, StateTypes.LEAF_LITTER, StateTypes.CACTUS_FLOWER, StateTypes.TEST_BLOCK, StateTypes.TEST_INSTANCE_BLOCK });
/* 594 */     V_1_21_6.add(new StateType[] { StateTypes.DRIED_GHAST });
/*     */   }
/*     */   
/*     */   String name;
/* 598 */   Set<StateType> states = new HashSet<>();
/*     */   boolean reallyEmpty;
/*     */   
/*     */   public BlockTags(String name) {
/* 602 */     byName.put(name, this);
/* 603 */     this.name = name;
/*     */   }
/*     */   
/*     */   private static BlockTags bind(String s) {
/* 607 */     return new BlockTags(s);
/*     */   }
/*     */   
/*     */   private static void copy(@Nullable BlockTags src, BlockTags dst) {
/* 611 */     if (src != null) {
/* 612 */       dst.states.addAll(src.states);
/*     */     } else {
/* 614 */       dst.reallyEmpty = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private BlockTags add(StateType... state) {
/* 619 */     Collections.addAll(this.states, state);
/* 620 */     return this;
/*     */   }
/*     */   
/*     */   private BlockTags addTag(BlockTags tags) {
/* 624 */     if (tags.states.isEmpty()) {
/* 625 */       throw new IllegalArgumentException("Tag " + tags.name + " is empty when adding to " + this.name + ", you (packetevents updater) probably messed up the block tags order!!");
/*     */     }
/* 627 */     this.states.addAll(tags.states);
/* 628 */     return this;
/*     */   }
/*     */   
/*     */   public boolean contains(StateType state) {
/* 632 */     return this.states.contains(state);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 636 */     return this.name;
/*     */   }
/*     */   
/*     */   public static BlockTags getByName(String name) {
/* 640 */     return byName.get(name);
/*     */   }
/*     */   
/*     */   public Set<StateType> getStates() {
/* 644 */     return this.states;
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   public boolean isReallyEmpty() {
/* 649 */     return this.reallyEmpty;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\states\defaulttags\BlockTags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
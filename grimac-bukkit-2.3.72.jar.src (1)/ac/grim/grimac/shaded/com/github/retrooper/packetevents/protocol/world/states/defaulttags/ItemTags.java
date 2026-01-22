/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
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
/*     */ public class ItemTags
/*     */ {
/*  33 */   private static final HashMap<String, ItemTags> byName = new HashMap<>();
/*     */   
/*  35 */   public static final ItemTags WOOL = bind("wool");
/*  36 */   public static final ItemTags PLANKS = bind("planks");
/*  37 */   public static final ItemTags STONE_BRICKS = bind("stone_bricks");
/*  38 */   public static final ItemTags WOODEN_BUTTONS = bind("wooden_buttons");
/*  39 */   public static final ItemTags STONE_BUTTONS = bind("stone_buttons");
/*  40 */   public static final ItemTags BUTTONS = bind("buttons");
/*  41 */   public static final ItemTags WOOL_CARPETS = bind("carpets");
/*  42 */   public static final ItemTags WOODEN_DOORS = bind("wooden_doors");
/*  43 */   public static final ItemTags WOODEN_STAIRS = bind("wooden_stairs");
/*  44 */   public static final ItemTags WOODEN_SLABS = bind("wooden_slabs");
/*  45 */   public static final ItemTags WOODEN_FENCES = bind("wooden_fences");
/*  46 */   public static final ItemTags FENCE_GATES = bind("fence_gates");
/*  47 */   public static final ItemTags WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
/*  48 */   public static final ItemTags WOODEN_TRAPDOORS = bind("wooden_trapdoors");
/*  49 */   public static final ItemTags DOORS = bind("doors");
/*  50 */   public static final ItemTags SAPLINGS = bind("saplings");
/*  51 */   public static final ItemTags LOGS_THAT_BURN = bind("logs_that_burn");
/*  52 */   public static final ItemTags LOGS = bind("logs");
/*  53 */   public static final ItemTags DARK_OAK_LOGS = bind("dark_oak_logs");
/*  54 */   public static final ItemTags OAK_LOGS = bind("oak_logs");
/*  55 */   public static final ItemTags BIRCH_LOGS = bind("birch_logs");
/*  56 */   public static final ItemTags ACACIA_LOGS = bind("acacia_logs");
/*  57 */   public static final ItemTags CHERRY_LOGS = bind("cherry_logs");
/*  58 */   public static final ItemTags JUNGLE_LOGS = bind("jungle_logs");
/*  59 */   public static final ItemTags SPRUCE_LOGS = bind("spruce_logs");
/*  60 */   public static final ItemTags MANGROVE_LOGS = bind("mangrove_logs");
/*  61 */   public static final ItemTags CRIMSON_STEMS = bind("crimson_stems");
/*  62 */   public static final ItemTags WARPED_STEMS = bind("warped_stems");
/*  63 */   public static final ItemTags BAMBOO_BLOCKS = bind("bamboo_blocks");
/*  64 */   public static final ItemTags WART_BLOCKS = bind("wart_blocks");
/*  65 */   public static final ItemTags BANNERS = bind("banners");
/*  66 */   public static final ItemTags SAND = bind("sand");
/*  67 */   public static final ItemTags SMELTS_TO_GLASS = bind("smelts_to_glass");
/*  68 */   public static final ItemTags STAIRS = bind("stairs");
/*  69 */   public static final ItemTags SLABS = bind("slabs");
/*  70 */   public static final ItemTags WALLS = bind("walls");
/*  71 */   public static final ItemTags ANVIL = bind("anvil");
/*  72 */   public static final ItemTags RAILS = bind("rails");
/*  73 */   public static final ItemTags LEAVES = bind("leaves");
/*  74 */   public static final ItemTags TRAPDOORS = bind("trapdoors");
/*  75 */   public static final ItemTags SMALL_FLOWERS = bind("small_flowers");
/*  76 */   public static final ItemTags BEDS = bind("beds");
/*  77 */   public static final ItemTags FENCES = bind("fences");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  82 */   public static final ItemTags TALL_FLOWERS = bind("tall_flowers");
/*     */ 
/*     */ 
/*     */   
/*  86 */   public static final ItemTags FLOWERS = bind("flowers");
/*  87 */   public static final ItemTags PIGLIN_REPELLENTS = bind("piglin_repellents");
/*  88 */   public static final ItemTags PIGLIN_LOVED = bind("piglin_loved");
/*  89 */   public static final ItemTags IGNORED_BY_PIGLIN_BABIES = bind("ignored_by_piglin_babies");
/*  90 */   public static final ItemTags PIGLIN_FOOD = bind("piglin_food");
/*  91 */   public static final ItemTags FOX_FOOD = bind("fox_food");
/*  92 */   public static final ItemTags GOLD_ORES = bind("gold_ores");
/*  93 */   public static final ItemTags IRON_ORES = bind("iron_ores");
/*  94 */   public static final ItemTags DIAMOND_ORES = bind("diamond_ores");
/*  95 */   public static final ItemTags REDSTONE_ORES = bind("redstone_ores");
/*  96 */   public static final ItemTags LAPIS_ORES = bind("lapis_ores");
/*  97 */   public static final ItemTags COAL_ORES = bind("coal_ores");
/*  98 */   public static final ItemTags EMERALD_ORES = bind("emerald_ores");
/*  99 */   public static final ItemTags COPPER_ORES = bind("copper_ores");
/* 100 */   public static final ItemTags NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
/* 101 */   public static final ItemTags SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
/* 102 */   public static final ItemTags CANDLES = bind("candles");
/* 103 */   public static final ItemTags DIRT = bind("dirt");
/* 104 */   public static final ItemTags TERRACOTTA = bind("terracotta");
/* 105 */   public static final ItemTags COMPLETES_FIND_TREE_TUTORIAL = bind("completes_find_tree_tutorial");
/* 106 */   public static final ItemTags BOATS = bind("boats");
/* 107 */   public static final ItemTags CHEST_BOATS = bind("chest_boats");
/* 108 */   public static final ItemTags FISHES = bind("fishes");
/* 109 */   public static final ItemTags SIGNS = bind("signs");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 114 */   public static final ItemTags MUSIC_DISCS = bind("music_discs");
/* 115 */   public static final ItemTags CREEPER_DROP_MUSIC_DISCS = bind("creeper_drop_music_discs");
/* 116 */   public static final ItemTags COALS = bind("coals");
/* 117 */   public static final ItemTags ARROWS = bind("arrows");
/* 118 */   public static final ItemTags LECTERN_BOOKS = bind("lectern_books");
/* 119 */   public static final ItemTags BOOKSHELF_BOOKS = bind("bookshelf_books");
/* 120 */   public static final ItemTags BEACON_PAYMENT_ITEMS = bind("beacon_payment_items");
/* 121 */   public static final ItemTags STONE_TOOL_MATERIALS = bind("stone_tool_materials");
/* 122 */   public static final ItemTags STONE_CRAFTING_MATERIALS = bind("stone_crafting_materials");
/* 123 */   public static final ItemTags FREEZE_IMMUNE_WEARABLES = bind("freeze_immune_wearables");
/* 124 */   public static final ItemTags DAMPENS_VIBRATIONS = bind("dampens_vibrations");
/* 125 */   public static final ItemTags CLUSTER_MAX_HARVESTABLES = bind("cluster_max_harvestables");
/* 126 */   public static final ItemTags COMPASSES = bind("compasses");
/* 127 */   public static final ItemTags HANGING_SIGNS = bind("hanging_signs");
/* 128 */   public static final ItemTags CREEPER_IGNITERS = bind("creeper_igniters");
/* 129 */   public static final ItemTags NOTEBLOCK_TOP_INSTRUMENTS = bind("noteblock_top_instruments");
/* 130 */   public static final ItemTags TRIMMABLE_ARMOR = bind("trimmable_armor");
/* 131 */   public static final ItemTags TRIM_MATERIALS = bind("trim_materials");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 136 */   public static final ItemTags TRIM_TEMPLATES = bind("trim_templates");
/* 137 */   public static final ItemTags SNIFFER_FOOD = bind("sniffer_food");
/* 138 */   public static final ItemTags DECORATED_POT_SHERDS = bind("decorated_pot_sherds");
/* 139 */   public static final ItemTags DECORATED_POT_INGREDIENTS = bind("decorated_pot_ingredients");
/* 140 */   public static final ItemTags SWORDS = bind("swords");
/* 141 */   public static final ItemTags AXES = bind("axes");
/* 142 */   public static final ItemTags HOES = bind("hoes");
/* 143 */   public static final ItemTags PICKAXES = bind("pickaxes");
/* 144 */   public static final ItemTags SHOVELS = bind("shovels");
/* 145 */   public static final ItemTags BREAKS_DECORATED_POTS = bind("breaks_decorated_pots");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 150 */   public static final ItemTags TOOLS = BREAKS_DECORATED_POTS;
/* 151 */   public static final ItemTags VILLAGER_PLANTABLE_SEEDS = bind("villager_plantable_seeds");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 156 */   public static final ItemTags ARMADILLO_FOOD = bind("armadillo_food");
/*     */ 
/*     */ 
/*     */   
/* 160 */   public static final ItemTags AXOLOTL_FOOD = bind("axolotl_food");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 165 */   public static final ItemTags AXOLOTL_TEMPT_ITEMS = AXOLOTL_FOOD;
/*     */ 
/*     */ 
/*     */   
/* 169 */   public static final ItemTags BEE_FOOD = bind("bee_food");
/*     */ 
/*     */ 
/*     */   
/* 173 */   public static final ItemTags CAMEL_FOOD = bind("camel_food");
/*     */ 
/*     */ 
/*     */   
/* 177 */   public static final ItemTags CAT_FOOD = bind("cat_food");
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final ItemTags CHEST_ARMOR = bind("chest_armor");
/*     */ 
/*     */ 
/*     */   
/* 185 */   public static final ItemTags CHICKEN_FOOD = bind("chicken_food");
/*     */ 
/*     */ 
/*     */   
/* 189 */   public static final ItemTags COW_FOOD = bind("cow_food");
/*     */ 
/*     */ 
/*     */   
/* 193 */   public static final ItemTags DYEABLE = bind("dyeable");
/*     */ 
/*     */ 
/*     */   
/* 197 */   public static final ItemTags ENCHANTABLE_ARMOR = bind("enchantable/armor");
/*     */ 
/*     */ 
/*     */   
/* 201 */   public static final ItemTags ENCHANTABLE_BOW = bind("enchantable/bow");
/*     */ 
/*     */ 
/*     */   
/* 205 */   public static final ItemTags ENCHANTABLE_CHEST_ARMOR = bind("enchantable/chest_armor");
/*     */ 
/*     */ 
/*     */   
/* 209 */   public static final ItemTags ENCHANTABLE_CROSSBOW = bind("enchantable/crossbow");
/*     */ 
/*     */ 
/*     */   
/* 213 */   public static final ItemTags ENCHANTABLE_DURABILITY = bind("enchantable/durability");
/*     */ 
/*     */ 
/*     */   
/* 217 */   public static final ItemTags ENCHANTABLE_EQUIPPABLE = bind("enchantable/equippable");
/*     */ 
/*     */ 
/*     */   
/* 221 */   public static final ItemTags ENCHANTABLE_FIRE_ASPECT = bind("enchantable/fire_aspect");
/*     */ 
/*     */ 
/*     */   
/* 225 */   public static final ItemTags ENCHANTABLE_FISHING = bind("enchantable/fishing");
/*     */ 
/*     */ 
/*     */   
/* 229 */   public static final ItemTags ENCHANTABLE_FOOT_ARMOR = bind("enchantable/foot_armor");
/*     */ 
/*     */ 
/*     */   
/* 233 */   public static final ItemTags ENCHANTABLE_HEAD_ARMOR = bind("enchantable/head_armor");
/*     */ 
/*     */ 
/*     */   
/* 237 */   public static final ItemTags ENCHANTABLE_LEG_ARMOR = bind("enchantable/leg_armor");
/*     */ 
/*     */ 
/*     */   
/* 241 */   public static final ItemTags ENCHANTABLE_MACE = bind("enchantable/mace");
/*     */ 
/*     */ 
/*     */   
/* 245 */   public static final ItemTags ENCHANTABLE_MINING = bind("enchantable/mining");
/*     */ 
/*     */ 
/*     */   
/* 249 */   public static final ItemTags ENCHANTABLE_MINING_LOOT = bind("enchantable/mining_loot");
/*     */ 
/*     */ 
/*     */   
/* 253 */   public static final ItemTags ENCHANTABLE_SHARP_WEAPON = bind("enchantable/sharp_weapon");
/*     */ 
/*     */ 
/*     */   
/* 257 */   public static final ItemTags ENCHANTABLE_SWORD = bind("enchantable/sword");
/*     */ 
/*     */ 
/*     */   
/* 261 */   public static final ItemTags ENCHANTABLE_TRIDENT = bind("enchantable/trident");
/*     */ 
/*     */ 
/*     */   
/* 265 */   public static final ItemTags ENCHANTABLE_VANISHING = bind("enchantable/vanishing");
/*     */ 
/*     */ 
/*     */   
/* 269 */   public static final ItemTags ENCHANTABLE_WEAPON = bind("enchantable/weapon");
/*     */ 
/*     */ 
/*     */   
/* 273 */   public static final ItemTags FOOT_ARMOR = bind("foot_armor");
/*     */ 
/*     */ 
/*     */   
/* 277 */   public static final ItemTags FROG_FOOD = bind("frog_food");
/*     */ 
/*     */ 
/*     */   
/* 281 */   public static final ItemTags GOAT_FOOD = bind("goat_food");
/*     */ 
/*     */ 
/*     */   
/* 285 */   public static final ItemTags HEAD_ARMOR = bind("head_armor");
/*     */ 
/*     */ 
/*     */   
/* 289 */   public static final ItemTags HOGLIN_FOOD = bind("hoglin_food");
/*     */ 
/*     */ 
/*     */   
/* 293 */   public static final ItemTags HORSE_FOOD = bind("horse_food");
/*     */ 
/*     */ 
/*     */   
/* 297 */   public static final ItemTags HORSE_TEMPT_ITEMS = bind("horse_tempt_items");
/*     */ 
/*     */ 
/*     */   
/* 301 */   public static final ItemTags LEG_ARMOR = bind("leg_armor");
/*     */ 
/*     */ 
/*     */   
/* 305 */   public static final ItemTags LLAMA_FOOD = bind("llama_food");
/*     */ 
/*     */ 
/*     */   
/* 309 */   public static final ItemTags LLAMA_TEMPT_ITEMS = bind("llama_tempt_items");
/*     */ 
/*     */ 
/*     */   
/* 313 */   public static final ItemTags MEAT = bind("meat");
/*     */ 
/*     */ 
/*     */   
/* 317 */   public static final ItemTags OCELOT_FOOD = bind("ocelot_food");
/*     */ 
/*     */ 
/*     */   
/* 321 */   public static final ItemTags PANDA_FOOD = bind("panda_food");
/*     */ 
/*     */ 
/*     */   
/* 325 */   public static final ItemTags PARROT_FOOD = bind("parrot_food");
/*     */ 
/*     */ 
/*     */   
/* 329 */   public static final ItemTags PARROT_POISONOUS_FOOD = bind("parrot_poisonous_food");
/*     */ 
/*     */ 
/*     */   
/* 333 */   public static final ItemTags PIG_FOOD = bind("pig_food");
/*     */ 
/*     */ 
/*     */   
/* 337 */   public static final ItemTags RABBIT_FOOD = bind("rabbit_food");
/*     */ 
/*     */ 
/*     */   
/* 341 */   public static final ItemTags SHEEP_FOOD = bind("sheep_food");
/*     */ 
/*     */ 
/*     */   
/* 345 */   public static final ItemTags SKULLS = bind("skulls");
/*     */ 
/*     */ 
/*     */   
/* 349 */   public static final ItemTags STRIDER_FOOD = bind("strider_food");
/*     */ 
/*     */ 
/*     */   
/* 353 */   public static final ItemTags STRIDER_TEMPT_ITEMS = bind("strider_tempt_items");
/*     */ 
/*     */ 
/*     */   
/* 357 */   public static final ItemTags TURTLE_FOOD = bind("turtle_food");
/*     */ 
/*     */ 
/*     */   
/* 361 */   public static final ItemTags WOLF_FOOD = bind("wolf_food");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 366 */   public static final ItemTags PALE_OAK_LOGS = bind("pale_oak_logs");
/*     */ 
/*     */ 
/*     */   
/* 370 */   public static final ItemTags PIGLIN_SAFE_ARMOR = bind("piglin_safe_armor");
/*     */ 
/*     */ 
/*     */   
/* 374 */   public static final ItemTags DUPLICATES_ALLAYS = bind("duplicates_allays");
/*     */ 
/*     */ 
/*     */   
/* 378 */   public static final ItemTags BREWING_FUEL = bind("brewing_fuel");
/*     */ 
/*     */ 
/*     */   
/* 382 */   public static final ItemTags SHULKER_BOXES = bind("shulker_boxes");
/*     */ 
/*     */ 
/*     */   
/* 386 */   public static final ItemTags IRON_TOOL_MATERIALS = bind("iron_tool_materials");
/*     */ 
/*     */ 
/*     */   
/* 390 */   public static final ItemTags GOLD_TOOL_MATERIALS = bind("gold_tool_materials");
/*     */ 
/*     */ 
/*     */   
/* 394 */   public static final ItemTags DIAMOND_TOOL_MATERIALS = bind("diamond_tool_materials");
/*     */ 
/*     */ 
/*     */   
/* 398 */   public static final ItemTags NETHERITE_TOOL_MATERIALS = bind("netherite_tool_materials");
/*     */ 
/*     */ 
/*     */   
/* 402 */   public static final ItemTags REPAIRS_LEATHER_ARMOR = bind("repairs_leather_armor");
/*     */ 
/*     */ 
/*     */   
/* 406 */   public static final ItemTags REPAIRS_CHAIN_ARMOR = bind("repairs_chain_armor");
/*     */ 
/*     */ 
/*     */   
/* 410 */   public static final ItemTags REPAIRS_IRON_ARMOR = bind("repairs_iron_armor");
/*     */ 
/*     */ 
/*     */   
/* 414 */   public static final ItemTags REPAIRS_GOLD_ARMOR = bind("repairs_gold_armor");
/*     */ 
/*     */ 
/*     */   
/* 418 */   public static final ItemTags REPAIRS_DIAMOND_ARMOR = bind("repairs_diamond_armor");
/*     */ 
/*     */ 
/*     */   
/* 422 */   public static final ItemTags REPAIRS_NETHERITE_ARMOR = bind("repairs_netherite_armor");
/*     */ 
/*     */ 
/*     */   
/* 426 */   public static final ItemTags REPAIRS_TURTLE_HELMET = bind("repairs_turtle_helmet");
/*     */ 
/*     */ 
/*     */   
/* 430 */   public static final ItemTags REPAIRS_WOLF_ARMOR = bind("repairs_wolf_armor");
/*     */ 
/*     */ 
/*     */   
/* 434 */   public static final ItemTags FURNACE_MINECART_FUEL = bind("furnace_minecart_fuel");
/*     */ 
/*     */ 
/*     */   
/* 438 */   public static final ItemTags BUNDLES = bind("bundles");
/*     */ 
/*     */ 
/*     */   
/* 442 */   public static final ItemTags MAP_INVISIBILITY_EQUIPMENT = bind("map_invisibility_equipment");
/*     */ 
/*     */ 
/*     */   
/* 446 */   public static final ItemTags GAZE_DISGUISE_EQUIPMENT = bind("gaze_disguise_equipment");
/*     */ 
/*     */ 
/*     */   
/* 450 */   public static final ItemTags PANDA_EATS_FROM_GROUND = bind("panda_eats_from_ground");
/*     */ 
/*     */ 
/*     */   
/* 454 */   public static final ItemTags WOODEN_TOOL_MATERIALS = bind("wooden_tool_materials");
/*     */ 
/*     */ 
/*     */   
/* 458 */   public static final ItemTags VILLAGER_PICKS_UP = bind("villager_picks_up");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 463 */   public static final ItemTags SKELETON_PREFERRED_WEAPONS = bind("skeleton_preferred_weapons");
/*     */ 
/*     */ 
/*     */   
/* 467 */   public static final ItemTags DROWNED_PREFERRED_WEAPONS = bind("drowned_preferred_weapons");
/*     */ 
/*     */ 
/*     */   
/* 471 */   public static final ItemTags PIGLIN_PREFERRED_WEAPONS = bind("piglin_preferred_weapons");
/*     */ 
/*     */ 
/*     */   
/* 475 */   public static final ItemTags PILLAGER_PREFERRED_WEAPONS = bind("pillager_preferred_weapons");
/*     */ 
/*     */ 
/*     */   
/* 479 */   public static final ItemTags WITHER_SKELETON_DISLIKED_WEAPONS = bind("wither_skeleton_disliked_weapons");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 484 */   public static final ItemTags EGGS = bind("eggs");
/*     */ 
/*     */ 
/*     */   
/* 488 */   public static final ItemTags BOOK_CLONING_TARGET = bind("book_cloning_target");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 493 */   public static final ItemTags HARNESSES = bind("harnesses");
/*     */ 
/*     */ 
/*     */   
/* 497 */   public static final ItemTags HAPPY_GHAST_FOOD = bind("happy_ghast_food");
/*     */ 
/*     */ 
/*     */   
/* 501 */   public static final ItemTags HAPPY_GHAST_TEMPT_ITEMS = bind("happy_ghast_tempt_items");
/*     */   
/*     */   static {
/* 504 */     copy(BlockTags.WOOL, WOOL);
/* 505 */     copy(BlockTags.PLANKS, PLANKS);
/* 506 */     copy(BlockTags.STONE_BRICKS, STONE_BRICKS);
/* 507 */     copy(BlockTags.WOODEN_BUTTONS, WOODEN_BUTTONS);
/* 508 */     copy(BlockTags.STONE_BUTTONS, STONE_BUTTONS);
/* 509 */     copy(BlockTags.WOOL_CARPETS, WOOL_CARPETS);
/* 510 */     copy(BlockTags.WOODEN_DOORS, WOODEN_DOORS);
/* 511 */     copy(BlockTags.WOODEN_STAIRS, WOODEN_STAIRS);
/* 512 */     copy(BlockTags.WOODEN_SLABS, WOODEN_SLABS);
/* 513 */     copy(BlockTags.WOODEN_FENCES, WOODEN_FENCES);
/* 514 */     copy(BlockTags.FENCE_GATES, FENCE_GATES);
/* 515 */     copy(BlockTags.WOODEN_PRESSURE_PLATES, WOODEN_PRESSURE_PLATES);
/* 516 */     copy(BlockTags.SAPLINGS, SAPLINGS);
/* 517 */     copy(BlockTags.BAMBOO_BLOCKS, BAMBOO_BLOCKS);
/* 518 */     copy(BlockTags.OAK_LOGS, OAK_LOGS);
/* 519 */     copy(BlockTags.DARK_OAK_LOGS, DARK_OAK_LOGS);
/* 520 */     copy(BlockTags.PALE_OAK_LOGS, PALE_OAK_LOGS);
/* 521 */     copy(BlockTags.BIRCH_LOGS, BIRCH_LOGS);
/* 522 */     copy(BlockTags.ACACIA_LOGS, ACACIA_LOGS);
/* 523 */     copy(BlockTags.SPRUCE_LOGS, SPRUCE_LOGS);
/* 524 */     copy(BlockTags.MANGROVE_LOGS, MANGROVE_LOGS);
/* 525 */     copy(BlockTags.JUNGLE_LOGS, JUNGLE_LOGS);
/* 526 */     copy(BlockTags.CHERRY_LOGS, CHERRY_LOGS);
/* 527 */     copy(BlockTags.CRIMSON_STEMS, CRIMSON_STEMS);
/* 528 */     copy(BlockTags.WARPED_STEMS, WARPED_STEMS);
/* 529 */     copy(BlockTags.WART_BLOCKS, WART_BLOCKS);
/* 530 */     copy(BlockTags.SAND, SAND);
/* 531 */     copy(BlockTags.SMELTS_TO_GLASS, SMELTS_TO_GLASS);
/* 532 */     copy(BlockTags.WALLS, WALLS);
/* 533 */     copy(BlockTags.ANVIL, ANVIL);
/* 534 */     copy(BlockTags.RAILS, RAILS);
/* 535 */     copy(BlockTags.LEAVES, LEAVES);
/* 536 */     copy(BlockTags.WOODEN_TRAPDOORS, WOODEN_TRAPDOORS);
/* 537 */     copy(BlockTags.SMALL_FLOWERS, SMALL_FLOWERS);
/* 538 */     copy(BlockTags.BEDS, BEDS);
/* 539 */     copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, SOUL_FIRE_BASE_BLOCKS);
/* 540 */     copy(BlockTags.CANDLES, CANDLES);
/* 541 */     copy(BlockTags.GOLD_ORES, GOLD_ORES);
/* 542 */     copy(BlockTags.IRON_ORES, IRON_ORES);
/* 543 */     copy(BlockTags.DIAMOND_ORES, DIAMOND_ORES);
/* 544 */     copy(BlockTags.REDSTONE_ORES, REDSTONE_ORES);
/* 545 */     copy(BlockTags.LAPIS_ORES, LAPIS_ORES);
/* 546 */     copy(BlockTags.COAL_ORES, COAL_ORES);
/* 547 */     copy(BlockTags.EMERALD_ORES, EMERALD_ORES);
/* 548 */     copy(BlockTags.COPPER_ORES, COPPER_ORES);
/* 549 */     copy(BlockTags.DIRT, DIRT);
/* 550 */     copy(BlockTags.TERRACOTTA, TERRACOTTA);
/* 551 */     copy(BlockTags.SHULKER_BOXES, SHULKER_BOXES);
/* 552 */     copy(BlockTags.STANDING_SIGNS, SIGNS);
/* 553 */     copy(BlockTags.CEILING_HANGING_SIGNS, HANGING_SIGNS);
/* 554 */     copy(BlockTags.BEE_ATTRACTIVE, BEE_FOOD);
/* 555 */     BANNERS.add(new ItemType[] { ItemTypes.WHITE_BANNER, ItemTypes.ORANGE_BANNER, ItemTypes.MAGENTA_BANNER, ItemTypes.LIGHT_BLUE_BANNER, ItemTypes.YELLOW_BANNER, ItemTypes.LIME_BANNER, ItemTypes.PINK_BANNER, ItemTypes.GRAY_BANNER, ItemTypes.LIGHT_GRAY_BANNER, ItemTypes.CYAN_BANNER, ItemTypes.PURPLE_BANNER, ItemTypes.BLUE_BANNER, ItemTypes.BROWN_BANNER, ItemTypes.GREEN_BANNER, ItemTypes.RED_BANNER, ItemTypes.BLACK_BANNER });
/* 556 */     PIGLIN_REPELLENTS.add(new ItemType[] { ItemTypes.SOUL_TORCH, ItemTypes.SOUL_LANTERN, ItemTypes.SOUL_CAMPFIRE });
/* 557 */     IGNORED_BY_PIGLIN_BABIES.add(new ItemType[] { ItemTypes.LEATHER });
/* 558 */     PIGLIN_SAFE_ARMOR.add(new ItemType[] { ItemTypes.GOLDEN_HELMET, ItemTypes.GOLDEN_CHESTPLATE, ItemTypes.GOLDEN_LEGGINGS, ItemTypes.GOLDEN_BOOTS });
/* 559 */     DUPLICATES_ALLAYS.add(new ItemType[] { ItemTypes.AMETHYST_SHARD });
/* 560 */     BREWING_FUEL.add(new ItemType[] { ItemTypes.BLAZE_POWDER });
/* 561 */     EGGS.add(new ItemType[] { ItemTypes.EGG, ItemTypes.BLUE_EGG, ItemTypes.BROWN_EGG });
/* 562 */     MEAT.add(new ItemType[] { ItemTypes.BEEF, ItemTypes.CHICKEN, ItemTypes.COOKED_BEEF, ItemTypes.COOKED_CHICKEN, ItemTypes.COOKED_MUTTON, ItemTypes.COOKED_PORKCHOP, ItemTypes.COOKED_RABBIT, ItemTypes.MUTTON, ItemTypes.PORKCHOP, ItemTypes.RABBIT, ItemTypes.ROTTEN_FLESH });
/* 563 */     SNIFFER_FOOD.add(new ItemType[] { ItemTypes.TORCHFLOWER_SEEDS });
/* 564 */     PIGLIN_FOOD.add(new ItemType[] { ItemTypes.PORKCHOP, ItemTypes.COOKED_PORKCHOP });
/* 565 */     FOX_FOOD.add(new ItemType[] { ItemTypes.SWEET_BERRIES, ItemTypes.GLOW_BERRIES });
/* 566 */     COW_FOOD.add(new ItemType[] { ItemTypes.WHEAT });
/* 567 */     copy(COW_FOOD, GOAT_FOOD);
/* 568 */     copy(COW_FOOD, SHEEP_FOOD);
/* 569 */     CAT_FOOD.add(new ItemType[] { ItemTypes.COD, ItemTypes.SALMON });
/* 570 */     HORSE_FOOD.add(new ItemType[] { ItemTypes.WHEAT, ItemTypes.SUGAR, ItemTypes.HAY_BLOCK, ItemTypes.APPLE, ItemTypes.CARROT, ItemTypes.GOLDEN_CARROT, ItemTypes.GOLDEN_APPLE, ItemTypes.ENCHANTED_GOLDEN_APPLE });
/* 571 */     HORSE_TEMPT_ITEMS.add(new ItemType[] { ItemTypes.GOLDEN_CARROT, ItemTypes.GOLDEN_APPLE, ItemTypes.ENCHANTED_GOLDEN_APPLE });
/* 572 */     HARNESSES.add(new ItemType[] { ItemTypes.WHITE_HARNESS, ItemTypes.ORANGE_HARNESS, ItemTypes.MAGENTA_HARNESS, ItemTypes.LIGHT_BLUE_HARNESS, ItemTypes.YELLOW_HARNESS, ItemTypes.LIME_HARNESS, ItemTypes.PINK_HARNESS, ItemTypes.GRAY_HARNESS, ItemTypes.LIGHT_GRAY_HARNESS, ItemTypes.CYAN_HARNESS, ItemTypes.PURPLE_HARNESS, ItemTypes.BLUE_HARNESS, ItemTypes.BROWN_HARNESS, ItemTypes.GREEN_HARNESS, ItemTypes.RED_HARNESS, ItemTypes.BLACK_HARNESS });
/* 573 */     HAPPY_GHAST_FOOD.add(new ItemType[] { ItemTypes.SNOWBALL });
/* 574 */     CAMEL_FOOD.add(new ItemType[] { ItemTypes.CACTUS });
/* 575 */     ARMADILLO_FOOD.add(new ItemType[] { ItemTypes.SPIDER_EYE });
/* 576 */     CHICKEN_FOOD.add(new ItemType[] { ItemTypes.WHEAT_SEEDS, ItemTypes.MELON_SEEDS, ItemTypes.PUMPKIN_SEEDS, ItemTypes.BEETROOT_SEEDS, ItemTypes.TORCHFLOWER_SEEDS, ItemTypes.PITCHER_POD });
/* 577 */     FROG_FOOD.add(new ItemType[] { ItemTypes.SLIME_BALL });
/* 578 */     HOGLIN_FOOD.add(new ItemType[] { ItemTypes.CRIMSON_FUNGUS });
/* 579 */     LLAMA_FOOD.add(new ItemType[] { ItemTypes.WHEAT, ItemTypes.HAY_BLOCK });
/* 580 */     LLAMA_TEMPT_ITEMS.add(new ItemType[] { ItemTypes.HAY_BLOCK });
/* 581 */     copy(CAT_FOOD, OCELOT_FOOD);
/* 582 */     PANDA_FOOD.add(new ItemType[] { ItemTypes.BAMBOO });
/* 583 */     PIG_FOOD.add(new ItemType[] { ItemTypes.CARROT, ItemTypes.POTATO, ItemTypes.BEETROOT });
/* 584 */     RABBIT_FOOD.add(new ItemType[] { ItemTypes.CARROT, ItemTypes.GOLDEN_CARROT, ItemTypes.DANDELION });
/* 585 */     STRIDER_FOOD.add(new ItemType[] { ItemTypes.WARPED_FUNGUS });
/* 586 */     TURTLE_FOOD.add(new ItemType[] { ItemTypes.SEAGRASS });
/* 587 */     copy(CHICKEN_FOOD, PARROT_FOOD);
/* 588 */     PARROT_POISONOUS_FOOD.add(new ItemType[] { ItemTypes.COOKIE });
/* 589 */     AXOLOTL_FOOD.add(new ItemType[] { ItemTypes.TROPICAL_FISH_BUCKET });
/* 590 */     NON_FLAMMABLE_WOOD.add(new ItemType[] { ItemTypes.WARPED_STEM, ItemTypes.STRIPPED_WARPED_STEM, ItemTypes.WARPED_HYPHAE, ItemTypes.STRIPPED_WARPED_HYPHAE, ItemTypes.CRIMSON_STEM, ItemTypes.STRIPPED_CRIMSON_STEM, ItemTypes.CRIMSON_HYPHAE, ItemTypes.STRIPPED_CRIMSON_HYPHAE, ItemTypes.CRIMSON_PLANKS, ItemTypes.WARPED_PLANKS, ItemTypes.CRIMSON_SLAB, ItemTypes.WARPED_SLAB, ItemTypes.CRIMSON_PRESSURE_PLATE, ItemTypes.WARPED_PRESSURE_PLATE, ItemTypes.CRIMSON_FENCE, ItemTypes.WARPED_FENCE, ItemTypes.CRIMSON_TRAPDOOR, ItemTypes.WARPED_TRAPDOOR, ItemTypes.CRIMSON_FENCE_GATE, ItemTypes.WARPED_FENCE_GATE, ItemTypes.CRIMSON_STAIRS, ItemTypes.WARPED_STAIRS, ItemTypes.CRIMSON_BUTTON, ItemTypes.WARPED_BUTTON, ItemTypes.CRIMSON_DOOR, ItemTypes.WARPED_DOOR, ItemTypes.CRIMSON_SIGN, ItemTypes.WARPED_SIGN, ItemTypes.WARPED_HANGING_SIGN, ItemTypes.CRIMSON_HANGING_SIGN });
/* 591 */     CHEST_BOATS.add(new ItemType[] { ItemTypes.OAK_CHEST_BOAT, ItemTypes.SPRUCE_CHEST_BOAT, ItemTypes.BIRCH_CHEST_BOAT, ItemTypes.JUNGLE_CHEST_BOAT, ItemTypes.ACACIA_CHEST_BOAT, ItemTypes.DARK_OAK_CHEST_BOAT, ItemTypes.PALE_OAK_CHEST_BOAT, ItemTypes.MANGROVE_CHEST_BOAT, ItemTypes.BAMBOO_CHEST_RAFT, ItemTypes.CHERRY_CHEST_BOAT });
/* 592 */     FISHES.add(new ItemType[] { ItemTypes.COD, ItemTypes.COOKED_COD, ItemTypes.SALMON, ItemTypes.COOKED_SALMON, ItemTypes.PUFFERFISH, ItemTypes.TROPICAL_FISH });
/* 593 */     CREEPER_DROP_MUSIC_DISCS.add(new ItemType[] { ItemTypes.MUSIC_DISC_13, ItemTypes.MUSIC_DISC_CAT, ItemTypes.MUSIC_DISC_BLOCKS, ItemTypes.MUSIC_DISC_CHIRP, ItemTypes.MUSIC_DISC_FAR, ItemTypes.MUSIC_DISC_MALL, ItemTypes.MUSIC_DISC_MELLOHI, ItemTypes.MUSIC_DISC_STAL, ItemTypes.MUSIC_DISC_STRAD, ItemTypes.MUSIC_DISC_WARD, ItemTypes.MUSIC_DISC_11, ItemTypes.MUSIC_DISC_WAIT });
/* 594 */     COALS.add(new ItemType[] { ItemTypes.COAL, ItemTypes.CHARCOAL });
/* 595 */     ARROWS.add(new ItemType[] { ItemTypes.ARROW, ItemTypes.TIPPED_ARROW, ItemTypes.SPECTRAL_ARROW });
/* 596 */     LECTERN_BOOKS.add(new ItemType[] { ItemTypes.WRITTEN_BOOK, ItemTypes.WRITABLE_BOOK });
/* 597 */     BOOKSHELF_BOOKS.add(new ItemType[] { ItemTypes.BOOK, ItemTypes.WRITTEN_BOOK, ItemTypes.ENCHANTED_BOOK, ItemTypes.WRITABLE_BOOK, ItemTypes.KNOWLEDGE_BOOK });
/* 598 */     BEACON_PAYMENT_ITEMS.add(new ItemType[] { ItemTypes.NETHERITE_INGOT, ItemTypes.EMERALD, ItemTypes.DIAMOND, ItemTypes.GOLD_INGOT, ItemTypes.IRON_INGOT });
/* 599 */     STONE_TOOL_MATERIALS.add(new ItemType[] { ItemTypes.COBBLESTONE, ItemTypes.BLACKSTONE, ItemTypes.COBBLED_DEEPSLATE });
/* 600 */     IRON_TOOL_MATERIALS.add(new ItemType[] { ItemTypes.IRON_INGOT });
/* 601 */     GOLD_TOOL_MATERIALS.add(new ItemType[] { ItemTypes.GOLD_INGOT });
/* 602 */     DIAMOND_TOOL_MATERIALS.add(new ItemType[] { ItemTypes.DIAMOND });
/* 603 */     NETHERITE_TOOL_MATERIALS.add(new ItemType[] { ItemTypes.NETHERITE_INGOT });
/* 604 */     copy(IGNORED_BY_PIGLIN_BABIES, REPAIRS_LEATHER_ARMOR);
/* 605 */     copy(IRON_TOOL_MATERIALS, REPAIRS_CHAIN_ARMOR);
/* 606 */     copy(IRON_TOOL_MATERIALS, REPAIRS_IRON_ARMOR);
/* 607 */     copy(GOLD_TOOL_MATERIALS, REPAIRS_GOLD_ARMOR);
/* 608 */     copy(DIAMOND_TOOL_MATERIALS, REPAIRS_DIAMOND_ARMOR);
/* 609 */     copy(NETHERITE_TOOL_MATERIALS, REPAIRS_NETHERITE_ARMOR);
/* 610 */     REPAIRS_TURTLE_HELMET.add(new ItemType[] { ItemTypes.TURTLE_SCUTE });
/* 611 */     REPAIRS_WOLF_ARMOR.add(new ItemType[] { ItemTypes.ARMADILLO_SCUTE });
/* 612 */     copy(STONE_TOOL_MATERIALS, STONE_CRAFTING_MATERIALS);
/* 613 */     FREEZE_IMMUNE_WEARABLES.add(new ItemType[] { ItemTypes.LEATHER_BOOTS, ItemTypes.LEATHER_LEGGINGS, ItemTypes.LEATHER_CHESTPLATE, ItemTypes.LEATHER_HELMET, ItemTypes.LEATHER_HORSE_ARMOR });
/* 614 */     CLUSTER_MAX_HARVESTABLES.add(new ItemType[] { ItemTypes.DIAMOND_PICKAXE, ItemTypes.GOLDEN_PICKAXE, ItemTypes.IRON_PICKAXE, ItemTypes.NETHERITE_PICKAXE, ItemTypes.STONE_PICKAXE, ItemTypes.WOODEN_PICKAXE });
/* 615 */     COMPASSES.add(new ItemType[] { ItemTypes.COMPASS, ItemTypes.RECOVERY_COMPASS });
/* 616 */     CREEPER_IGNITERS.add(new ItemType[] { ItemTypes.FLINT_AND_STEEL, ItemTypes.FIRE_CHARGE });
/* 617 */     NOTEBLOCK_TOP_INSTRUMENTS.add(new ItemType[] { ItemTypes.ZOMBIE_HEAD, ItemTypes.SKELETON_SKULL, ItemTypes.CREEPER_HEAD, ItemTypes.DRAGON_HEAD, ItemTypes.WITHER_SKELETON_SKULL, ItemTypes.PIGLIN_HEAD, ItemTypes.PLAYER_HEAD });
/* 618 */     FOOT_ARMOR.add(new ItemType[] { ItemTypes.LEATHER_BOOTS, ItemTypes.CHAINMAIL_BOOTS, ItemTypes.GOLDEN_BOOTS, ItemTypes.IRON_BOOTS, ItemTypes.DIAMOND_BOOTS, ItemTypes.NETHERITE_BOOTS });
/* 619 */     LEG_ARMOR.add(new ItemType[] { ItemTypes.LEATHER_LEGGINGS, ItemTypes.CHAINMAIL_LEGGINGS, ItemTypes.GOLDEN_LEGGINGS, ItemTypes.IRON_LEGGINGS, ItemTypes.DIAMOND_LEGGINGS, ItemTypes.NETHERITE_LEGGINGS });
/* 620 */     CHEST_ARMOR.add(new ItemType[] { ItemTypes.LEATHER_CHESTPLATE, ItemTypes.CHAINMAIL_CHESTPLATE, ItemTypes.GOLDEN_CHESTPLATE, ItemTypes.IRON_CHESTPLATE, ItemTypes.DIAMOND_CHESTPLATE, ItemTypes.NETHERITE_CHESTPLATE });
/* 621 */     HEAD_ARMOR.add(new ItemType[] { ItemTypes.LEATHER_HELMET, ItemTypes.CHAINMAIL_HELMET, ItemTypes.GOLDEN_HELMET, ItemTypes.IRON_HELMET, ItemTypes.DIAMOND_HELMET, ItemTypes.NETHERITE_HELMET, ItemTypes.TURTLE_HELMET });
/* 622 */     SKULLS.add(new ItemType[] { ItemTypes.PLAYER_HEAD, ItemTypes.CREEPER_HEAD, ItemTypes.ZOMBIE_HEAD, ItemTypes.SKELETON_SKULL, ItemTypes.WITHER_SKELETON_SKULL, ItemTypes.DRAGON_HEAD, ItemTypes.PIGLIN_HEAD });
/* 623 */     TRIM_MATERIALS.add(new ItemType[] { ItemTypes.AMETHYST_SHARD, ItemTypes.COPPER_INGOT, ItemTypes.DIAMOND, ItemTypes.EMERALD, ItemTypes.GOLD_INGOT, ItemTypes.IRON_INGOT, ItemTypes.LAPIS_LAZULI, ItemTypes.NETHERITE_INGOT, ItemTypes.QUARTZ, ItemTypes.REDSTONE, ItemTypes.RESIN_BRICK });
/* 624 */     DECORATED_POT_SHERDS.add(new ItemType[] { ItemTypes.ANGLER_POTTERY_SHERD, ItemTypes.ARCHER_POTTERY_SHERD, ItemTypes.ARMS_UP_POTTERY_SHERD, ItemTypes.BLADE_POTTERY_SHERD, ItemTypes.BREWER_POTTERY_SHERD, ItemTypes.BURN_POTTERY_SHERD, ItemTypes.DANGER_POTTERY_SHERD, ItemTypes.EXPLORER_POTTERY_SHERD, ItemTypes.FRIEND_POTTERY_SHERD, ItemTypes.HEART_POTTERY_SHERD, ItemTypes.HEARTBREAK_POTTERY_SHERD, ItemTypes.HOWL_POTTERY_SHERD, ItemTypes.MINER_POTTERY_SHERD, ItemTypes.MOURNER_POTTERY_SHERD, ItemTypes.PLENTY_POTTERY_SHERD, ItemTypes.PRIZE_POTTERY_SHERD, ItemTypes.SHEAF_POTTERY_SHERD, ItemTypes.SHELTER_POTTERY_SHERD, ItemTypes.SKULL_POTTERY_SHERD, ItemTypes.SNORT_POTTERY_SHERD, ItemTypes.FLOW_POTTERY_SHERD, ItemTypes.GUSTER_POTTERY_SHERD, ItemTypes.SCRAPE_POTTERY_SHERD });
/* 625 */     SWORDS.add(new ItemType[] { ItemTypes.DIAMOND_SWORD, ItemTypes.STONE_SWORD, ItemTypes.GOLDEN_SWORD, ItemTypes.NETHERITE_SWORD, ItemTypes.WOODEN_SWORD, ItemTypes.IRON_SWORD });
/* 626 */     AXES.add(new ItemType[] { ItemTypes.DIAMOND_AXE, ItemTypes.STONE_AXE, ItemTypes.GOLDEN_AXE, ItemTypes.NETHERITE_AXE, ItemTypes.WOODEN_AXE, ItemTypes.IRON_AXE });
/* 627 */     HOES.add(new ItemType[] { ItemTypes.DIAMOND_HOE, ItemTypes.STONE_HOE, ItemTypes.GOLDEN_HOE, ItemTypes.NETHERITE_HOE, ItemTypes.WOODEN_HOE, ItemTypes.IRON_HOE });
/* 628 */     PICKAXES.add(new ItemType[] { ItemTypes.DIAMOND_PICKAXE, ItemTypes.STONE_PICKAXE, ItemTypes.GOLDEN_PICKAXE, ItemTypes.NETHERITE_PICKAXE, ItemTypes.WOODEN_PICKAXE, ItemTypes.IRON_PICKAXE });
/* 629 */     SHOVELS.add(new ItemType[] { ItemTypes.DIAMOND_SHOVEL, ItemTypes.STONE_SHOVEL, ItemTypes.GOLDEN_SHOVEL, ItemTypes.NETHERITE_SHOVEL, ItemTypes.WOODEN_SHOVEL, ItemTypes.IRON_SHOVEL });
/* 630 */     VILLAGER_PLANTABLE_SEEDS.add(new ItemType[] { ItemTypes.WHEAT_SEEDS, ItemTypes.POTATO, ItemTypes.CARROT, ItemTypes.BEETROOT_SEEDS, ItemTypes.TORCHFLOWER_SEEDS, ItemTypes.PITCHER_POD });
/* 631 */     DYEABLE.add(new ItemType[] { ItemTypes.LEATHER_HELMET, ItemTypes.LEATHER_CHESTPLATE, ItemTypes.LEATHER_LEGGINGS, ItemTypes.LEATHER_BOOTS, ItemTypes.LEATHER_HORSE_ARMOR, ItemTypes.WOLF_ARMOR });
/* 632 */     copy(COALS, FURNACE_MINECART_FUEL);
/* 633 */     BUNDLES.add(new ItemType[] { ItemTypes.BUNDLE, ItemTypes.BLACK_BUNDLE, ItemTypes.BLUE_BUNDLE, ItemTypes.BROWN_BUNDLE, ItemTypes.CYAN_BUNDLE, ItemTypes.GRAY_BUNDLE, ItemTypes.GREEN_BUNDLE, ItemTypes.LIGHT_BLUE_BUNDLE, ItemTypes.LIGHT_GRAY_BUNDLE, ItemTypes.LIME_BUNDLE, ItemTypes.MAGENTA_BUNDLE, ItemTypes.ORANGE_BUNDLE, ItemTypes.PINK_BUNDLE, ItemTypes.PURPLE_BUNDLE, ItemTypes.RED_BUNDLE, ItemTypes.YELLOW_BUNDLE, ItemTypes.WHITE_BUNDLE });
/* 634 */     BOOK_CLONING_TARGET.add(new ItemType[] { ItemTypes.WRITABLE_BOOK });
/* 635 */     SKELETON_PREFERRED_WEAPONS.add(new ItemType[] { ItemTypes.BOW });
/* 636 */     DROWNED_PREFERRED_WEAPONS.add(new ItemType[] { ItemTypes.TRIDENT });
/* 637 */     PIGLIN_PREFERRED_WEAPONS.add(new ItemType[] { ItemTypes.CROSSBOW });
/* 638 */     copy(PIGLIN_PREFERRED_WEAPONS, PILLAGER_PREFERRED_WEAPONS);
/* 639 */     WITHER_SKELETON_DISLIKED_WEAPONS.add(new ItemType[] { ItemTypes.BOW, ItemTypes.CROSSBOW });
/* 640 */     ENCHANTABLE_FISHING.add(new ItemType[] { ItemTypes.FISHING_ROD });
/* 641 */     copy(DROWNED_PREFERRED_WEAPONS, ENCHANTABLE_TRIDENT);
/* 642 */     copy(SKELETON_PREFERRED_WEAPONS, ENCHANTABLE_BOW);
/* 643 */     copy(PIGLIN_PREFERRED_WEAPONS, ENCHANTABLE_CROSSBOW);
/* 644 */     ENCHANTABLE_MACE.add(new ItemType[] { ItemTypes.MACE });
/* 645 */     MAP_INVISIBILITY_EQUIPMENT.add(new ItemType[] { ItemTypes.CARVED_PUMPKIN });
/* 646 */     copy(MAP_INVISIBILITY_EQUIPMENT, GAZE_DISGUISE_EQUIPMENT);
/* 647 */     copy(BlockTags.BUTTONS, BUTTONS);
/* 648 */     copy(BlockTags.DOORS, DOORS);
/* 649 */     copy(BlockTags.LOGS_THAT_BURN, LOGS_THAT_BURN);
/* 650 */     copy(BlockTags.SLABS, SLABS);
/* 651 */     copy(BlockTags.STAIRS, STAIRS);
/* 652 */     copy(BlockTags.TRAPDOORS, TRAPDOORS);
/* 653 */     copy(BlockTags.FLOWERS, FLOWERS);
/* 654 */     copy(BlockTags.FENCES, FENCES);
/* 655 */     copy(BlockTags.DAMPENS_VIBRATIONS, DAMPENS_VIBRATIONS);
/* 656 */     PIGLIN_LOVED.addTag(GOLD_ORES).add(new ItemType[] { ItemTypes.GOLD_BLOCK, ItemTypes.GILDED_BLACKSTONE, ItemTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemTypes.GOLD_INGOT, ItemTypes.BELL, ItemTypes.CLOCK, ItemTypes.GOLDEN_CARROT, ItemTypes.GLISTERING_MELON_SLICE, ItemTypes.GOLDEN_APPLE, ItemTypes.ENCHANTED_GOLDEN_APPLE, ItemTypes.GOLDEN_HELMET, ItemTypes.GOLDEN_CHESTPLATE, ItemTypes.GOLDEN_LEGGINGS, ItemTypes.GOLDEN_BOOTS, ItemTypes.GOLDEN_HORSE_ARMOR, ItemTypes.GOLDEN_SWORD, ItemTypes.GOLDEN_PICKAXE, ItemTypes.GOLDEN_SHOVEL, ItemTypes.GOLDEN_AXE, ItemTypes.GOLDEN_HOE, ItemTypes.RAW_GOLD, ItemTypes.RAW_GOLD_BLOCK });
/* 657 */     WOLF_FOOD.addTag(MEAT).add(new ItemType[] { ItemTypes.COD, ItemTypes.COOKED_COD, ItemTypes.SALMON, ItemTypes.COOKED_SALMON, ItemTypes.TROPICAL_FISH, ItemTypes.PUFFERFISH, ItemTypes.RABBIT_STEW });
/* 658 */     HAPPY_GHAST_TEMPT_ITEMS.addTag(HAPPY_GHAST_FOOD).addTag(HARNESSES);
/* 659 */     PANDA_EATS_FROM_GROUND.addTag(PANDA_FOOD).add(new ItemType[] { ItemTypes.CAKE });
/* 660 */     STRIDER_TEMPT_ITEMS.addTag(STRIDER_FOOD).add(new ItemType[] { ItemTypes.WARPED_FUNGUS_ON_A_STICK });
/* 661 */     BOATS.addTag(CHEST_BOATS).add(new ItemType[] { ItemTypes.OAK_BOAT, ItemTypes.SPRUCE_BOAT, ItemTypes.BIRCH_BOAT, ItemTypes.JUNGLE_BOAT, ItemTypes.ACACIA_BOAT, ItemTypes.DARK_OAK_BOAT, ItemTypes.PALE_OAK_BOAT, ItemTypes.MANGROVE_BOAT, ItemTypes.BAMBOO_RAFT, ItemTypes.CHERRY_BOAT });
/* 662 */     WOODEN_TOOL_MATERIALS.addTag(PLANKS);
/* 663 */     TRIMMABLE_ARMOR.addTag(FOOT_ARMOR).addTag(LEG_ARMOR).addTag(CHEST_ARMOR).addTag(HEAD_ARMOR);
/* 664 */     DECORATED_POT_INGREDIENTS.addTag(DECORATED_POT_SHERDS).add(new ItemType[] { ItemTypes.BRICK });
/* 665 */     BREAKS_DECORATED_POTS.addTag(SWORDS).addTag(AXES).addTag(PICKAXES).addTag(SHOVELS).addTag(HOES).add(new ItemType[] { ItemTypes.TRIDENT, ItemTypes.MACE });
/* 666 */     VILLAGER_PICKS_UP.addTag(VILLAGER_PLANTABLE_SEEDS).add(new ItemType[] { ItemTypes.BREAD, ItemTypes.WHEAT, ItemTypes.BEETROOT });
/* 667 */     ENCHANTABLE_FOOT_ARMOR.addTag(FOOT_ARMOR);
/* 668 */     ENCHANTABLE_LEG_ARMOR.addTag(LEG_ARMOR);
/* 669 */     ENCHANTABLE_CHEST_ARMOR.addTag(CHEST_ARMOR);
/* 670 */     ENCHANTABLE_HEAD_ARMOR.addTag(HEAD_ARMOR);
/* 671 */     ENCHANTABLE_SWORD.addTag(SWORDS);
/* 672 */     ENCHANTABLE_SHARP_WEAPON.addTag(SWORDS).addTag(AXES);
/* 673 */     ENCHANTABLE_MINING.addTag(AXES).addTag(PICKAXES).addTag(SHOVELS).addTag(HOES).add(new ItemType[] { ItemTypes.SHEARS });
/* 674 */     ENCHANTABLE_MINING_LOOT.addTag(AXES).addTag(PICKAXES).addTag(SHOVELS).addTag(HOES);
/* 675 */     ENCHANTABLE_DURABILITY.addTag(FOOT_ARMOR).addTag(LEG_ARMOR).addTag(CHEST_ARMOR).addTag(HEAD_ARMOR).addTag(SWORDS).addTag(AXES).addTag(PICKAXES).addTag(SHOVELS).addTag(HOES).add(new ItemType[] { ItemTypes.ELYTRA, ItemTypes.SHIELD, ItemTypes.BOW, ItemTypes.CROSSBOW, ItemTypes.TRIDENT, ItemTypes.FLINT_AND_STEEL, ItemTypes.SHEARS, ItemTypes.BRUSH, ItemTypes.FISHING_ROD, ItemTypes.CARROT_ON_A_STICK, ItemTypes.WARPED_FUNGUS_ON_A_STICK, ItemTypes.MACE });
/* 676 */     ENCHANTABLE_EQUIPPABLE.addTag(FOOT_ARMOR).addTag(LEG_ARMOR).addTag(CHEST_ARMOR).addTag(HEAD_ARMOR).addTag(SKULLS).add(new ItemType[] { ItemTypes.ELYTRA, ItemTypes.CARVED_PUMPKIN });
/* 677 */     copy(BlockTags.LOGS, LOGS);
/* 678 */     ENCHANTABLE_ARMOR.addTag(ENCHANTABLE_FOOT_ARMOR).addTag(ENCHANTABLE_LEG_ARMOR).addTag(ENCHANTABLE_CHEST_ARMOR).addTag(ENCHANTABLE_HEAD_ARMOR);
/* 679 */     ENCHANTABLE_FIRE_ASPECT.addTag(ENCHANTABLE_SWORD).add(new ItemType[] { ItemTypes.MACE });
/* 680 */     ENCHANTABLE_WEAPON.addTag(ENCHANTABLE_SHARP_WEAPON).add(new ItemType[] { ItemTypes.MACE });
/* 681 */     ENCHANTABLE_VANISHING.addTag(ENCHANTABLE_DURABILITY).addTag(SKULLS).add(new ItemType[] { ItemTypes.COMPASS, ItemTypes.CARVED_PUMPKIN });
/* 682 */     copy(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, COMPLETES_FIND_TREE_TUTORIAL);
/*     */ 
/*     */     
/* 685 */     for (ItemType type : ItemTypes.getRegistry().getEntries()) {
/* 686 */       if (type.getComponents().has(ComponentTypes.JUKEBOX_PLAYABLE)) {
/* 687 */         MUSIC_DISCS.add(new ItemType[] { type });
/*     */       }
/*     */     } 
/* 690 */     copy(BlockTags.TALL_FLOWERS, TALL_FLOWERS);
/* 691 */     copy(BlockTags.FLOWERS, FLOWERS);
/* 692 */     TRIM_TEMPLATES.add(new ItemType[] { ItemTypes.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTypes.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE });
/*     */   }
/*     */   
/*     */   String name;
/* 696 */   Set<ItemType> states = new HashSet<>();
/*     */   boolean reallyEmpty;
/*     */   
/*     */   public ItemTags(String name) {
/* 700 */     byName.put(name, this);
/* 701 */     this.name = name;
/*     */   }
/*     */   
/*     */   private static ItemTags bind(String s) {
/* 705 */     return new ItemTags(s);
/*     */   }
/*     */   
/*     */   private static void copy(ItemTags src, ItemTags dst) {
/* 709 */     dst.states.addAll(src.states);
/*     */   }
/*     */   
/*     */   private static void copy(BlockTags tag, ItemTags itemTag) {
/* 713 */     for (StateType state : tag.getStates()) {
/* 714 */       itemTag.states.add(ItemTypes.getTypePlacingState(state));
/*     */     }
/* 716 */     itemTag.states.remove(null);
/*     */   }
/*     */   
/*     */   private ItemTags add(ItemType... state) {
/* 720 */     Collections.addAll(this.states, state);
/* 721 */     return this;
/*     */   }
/*     */   
/*     */   private ItemTags addTag(ItemTags tags) {
/* 725 */     if (tags.states.isEmpty()) {
/* 726 */       throw new IllegalArgumentException("Tag " + tags.name + " is empty when adding to " + this.name + ", you (packetevents updater) probably messed up the item tags order!!");
/*     */     }
/* 728 */     this.states.addAll(tags.states);
/* 729 */     return this;
/*     */   }
/*     */   
/*     */   public boolean contains(ItemType state) {
/* 733 */     return this.states.contains(state);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 737 */     return this.name;
/*     */   }
/*     */   
/*     */   public ItemTags getByName(String name) {
/* 741 */     return byName.get(name);
/*     */   }
/*     */   
/*     */   public Set<ItemType> getStates() {
/* 745 */     return this.states;
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   public boolean isReallyEmpty() {
/* 750 */     return this.reallyEmpty;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\states\defaulttags\ItemTags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*      */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type;
/*      */ 
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.annotations.RuntimeObsolete;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.MaterialType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class StateTypes
/*      */ {
/*   37 */   private static final VersionedRegistry<StateType.Mapped> REGISTRY = new VersionedRegistry("block");
/*      */ 
/*      */   
/*      */   private static final List<StateType> STATE_TYPE_VALUES;
/*      */ 
/*      */   
/*      */   public static VersionedRegistry<StateType.Mapped> getRegistry() {
/*   44 */     return REGISTRY;
/*      */   }
/*      */   
/*      */   public static Collection<StateType> values() {
/*   48 */     return STATE_TYPE_VALUES;
/*      */   }
/*      */   @Nullable
/*      */   public static StateType getByName(String blockString) {
/*   52 */     StateType.Mapped mapped = getMappedByName(blockString);
/*   53 */     return (mapped == null) ? null : mapped.getStateType();
/*      */   }
/*      */   
/*      */   public static StateType.Mapped getMappedByName(String blockString) {
/*   57 */     return (StateType.Mapped)REGISTRY.getByName(blockString);
/*      */   }
/*      */   @Nullable
/*      */   public static StateType getByName(ResourceLocation blockKey) {
/*   61 */     StateType.Mapped mapped = getMappedByName(blockKey);
/*   62 */     return (mapped == null) ? null : mapped.getStateType();
/*      */   }
/*      */   
/*      */   public static StateType.Mapped getMappedByName(ResourceLocation blockKey) {
/*   66 */     return (StateType.Mapped)REGISTRY.getByName(blockKey);
/*      */   }
/*      */   
/*      */   public static StateType getById(ClientVersion version, int id) {
/*   70 */     return getMappedById(version, id).getStateType();
/*      */   }
/*      */   
/*      */   public static StateType.Mapped getMappedById(ClientVersion version, int id) {
/*   74 */     return (StateType.Mapped)REGISTRY.getById(version, id);
/*      */   }
/*      */ 
/*      */   
/*   78 */   public static StateType AIR = builder().name("AIR").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).isAir(true).setMaterial(MaterialType.AIR).build();
/*   79 */   public static StateType STONE = builder().name("STONE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   80 */   public static StateType GRANITE = builder().name("GRANITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   81 */   public static StateType POLISHED_GRANITE = builder().name("POLISHED_GRANITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   82 */   public static StateType DIORITE = builder().name("DIORITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   83 */   public static StateType POLISHED_DIORITE = builder().name("POLISHED_DIORITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   84 */   public static StateType ANDESITE = builder().name("ANDESITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   85 */   public static StateType POLISHED_ANDESITE = builder().name("POLISHED_ANDESITE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   86 */   public static StateType DEEPSLATE = builder().name("DEEPSLATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   87 */   public static StateType COBBLED_DEEPSLATE = builder().name("COBBLED_DEEPSLATE").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   88 */   public static StateType POLISHED_DEEPSLATE = builder().name("POLISHED_DEEPSLATE").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   89 */   public static StateType CALCITE = builder().name("CALCITE").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   90 */   public static StateType TUFF = builder().name("TUFF").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   91 */   public static StateType DRIPSTONE_BLOCK = builder().name("DRIPSTONE_BLOCK").blastResistance(1.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   92 */   public static StateType GRASS_BLOCK = builder().name("GRASS_BLOCK").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*   93 */   public static StateType DIRT = builder().name("DIRT").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*   94 */   public static StateType COARSE_DIRT = builder().name("COARSE_DIRT").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*   95 */   public static StateType PODZOL = builder().name("PODZOL").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*   96 */   public static StateType ROOTED_DIRT = builder().name("ROOTED_DIRT").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*   97 */   public static StateType MUD = builder().name("MUD").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*   98 */   public static StateType CRIMSON_NYLIUM = builder().name("CRIMSON_NYLIUM").blastResistance(0.4F).hardness(0.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*   99 */   public static StateType WARPED_NYLIUM = builder().name("WARPED_NYLIUM").blastResistance(0.4F).hardness(0.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  100 */   public static StateType COBBLESTONE = builder().name("COBBLESTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  101 */   public static StateType OAK_PLANKS = builder().name("OAK_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  102 */   public static StateType SPRUCE_PLANKS = builder().name("SPRUCE_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  103 */   public static StateType BIRCH_PLANKS = builder().name("BIRCH_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  104 */   public static StateType JUNGLE_PLANKS = builder().name("JUNGLE_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  105 */   public static StateType ACACIA_PLANKS = builder().name("ACACIA_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  106 */   public static StateType CHERRY_PLANKS = builder().name("CHERRY_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  107 */   public static StateType DARK_OAK_PLANKS = builder().name("DARK_OAK_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  108 */   public static StateType MANGROVE_PLANKS = builder().name("MANGROVE_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  109 */   public static StateType BAMBOO_PLANKS = builder().name("BAMBOO_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  110 */   public static StateType CRIMSON_PLANKS = builder().name("CRIMSON_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  111 */   public static StateType WARPED_PLANKS = builder().name("WARPED_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  112 */   public static StateType BAMBOO_MOSAIC = builder().name("BAMBOO_MOSAIC").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  113 */   public static StateType OAK_SAPLING = builder().name("OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  114 */   public static StateType SPRUCE_SAPLING = builder().name("SPRUCE_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  115 */   public static StateType BIRCH_SAPLING = builder().name("BIRCH_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  116 */   public static StateType JUNGLE_SAPLING = builder().name("JUNGLE_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  117 */   public static StateType ACACIA_SAPLING = builder().name("ACACIA_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  118 */   public static StateType CHERRY_SAPLING = builder().name("CHERRY_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  119 */   public static StateType DARK_OAK_SAPLING = builder().name("DARK_OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  120 */   public static StateType MANGROVE_PROPAGULE = builder().name("MANGROVE_PROPAGULE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  121 */   public static StateType BEDROCK = builder().name("BEDROCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  122 */   public static StateType SAND = builder().name("SAND").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  123 */   public static StateType SUSPICIOUS_SAND = builder().name("SUSPICIOUS_SAND").blastResistance(0.25F).hardness(0.25F)
/*  124 */     .isBlocking(true)
/*  125 */     .requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  126 */   public static StateType RED_SAND = builder().name("RED_SAND").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  127 */   public static StateType GRAVEL = builder().name("GRAVEL").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  128 */   public static StateType COAL_ORE = builder().name("COAL_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  129 */   public static StateType DEEPSLATE_COAL_ORE = builder().name("DEEPSLATE_COAL_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  130 */   public static StateType IRON_ORE = builder().name("IRON_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  131 */   public static StateType DEEPSLATE_IRON_ORE = builder().name("DEEPSLATE_IRON_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  132 */   public static StateType COPPER_ORE = builder().name("COPPER_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  133 */   public static StateType DEEPSLATE_COPPER_ORE = builder().name("DEEPSLATE_COPPER_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  134 */   public static StateType GOLD_ORE = builder().name("GOLD_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  135 */   public static StateType DEEPSLATE_GOLD_ORE = builder().name("DEEPSLATE_GOLD_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  136 */   public static StateType REDSTONE_ORE = builder().name("REDSTONE_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  137 */   public static StateType DEEPSLATE_REDSTONE_ORE = builder().name("DEEPSLATE_REDSTONE_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  138 */   public static StateType EMERALD_ORE = builder().name("EMERALD_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  139 */   public static StateType DEEPSLATE_EMERALD_ORE = builder().name("DEEPSLATE_EMERALD_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  140 */   public static StateType LAPIS_ORE = builder().name("LAPIS_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  141 */   public static StateType DEEPSLATE_LAPIS_ORE = builder().name("DEEPSLATE_LAPIS_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  142 */   public static StateType DIAMOND_ORE = builder().name("DIAMOND_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  143 */   public static StateType DEEPSLATE_DIAMOND_ORE = builder().name("DEEPSLATE_DIAMOND_ORE").blastResistance(3.0F).hardness(4.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  144 */   public static StateType NETHER_GOLD_ORE = builder().name("NETHER_GOLD_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  145 */   public static StateType NETHER_QUARTZ_ORE = builder().name("NETHER_QUARTZ_ORE").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  146 */   public static StateType ANCIENT_DEBRIS = builder().name("ANCIENT_DEBRIS").blastResistance(1200.0F).hardness(30.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  147 */   public static StateType COAL_BLOCK = builder().name("COAL_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  148 */   public static StateType RAW_IRON_BLOCK = builder().name("RAW_IRON_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  149 */   public static StateType RAW_COPPER_BLOCK = builder().name("RAW_COPPER_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  150 */   public static StateType RAW_GOLD_BLOCK = builder().name("RAW_GOLD_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  151 */   public static StateType AMETHYST_BLOCK = builder().name("AMETHYST_BLOCK").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  152 */   public static StateType BUDDING_AMETHYST = builder().name("BUDDING_AMETHYST").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  153 */   public static StateType IRON_BLOCK = builder().name("IRON_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  154 */   public static StateType COPPER_BLOCK = builder().name("COPPER_BLOCK").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  155 */   public static StateType GOLD_BLOCK = builder().name("GOLD_BLOCK").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  156 */   public static StateType DIAMOND_BLOCK = builder().name("DIAMOND_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  157 */   public static StateType NETHERITE_BLOCK = builder().name("NETHERITE_BLOCK").blastResistance(1200.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  158 */   public static StateType EXPOSED_COPPER = builder().name("EXPOSED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  159 */   public static StateType WEATHERED_COPPER = builder().name("WEATHERED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  160 */   public static StateType OXIDIZED_COPPER = builder().name("OXIDIZED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  161 */   public static StateType CUT_COPPER = builder().name("CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  162 */   public static StateType EXPOSED_CUT_COPPER = builder().name("EXPOSED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  163 */   public static StateType WEATHERED_CUT_COPPER = builder().name("WEATHERED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  164 */   public static StateType OXIDIZED_CUT_COPPER = builder().name("OXIDIZED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  165 */   public static StateType CUT_COPPER_STAIRS = builder().name("CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  166 */   public static StateType EXPOSED_CUT_COPPER_STAIRS = builder().name("EXPOSED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  167 */   public static StateType WEATHERED_CUT_COPPER_STAIRS = builder().name("WEATHERED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  168 */   public static StateType OXIDIZED_CUT_COPPER_STAIRS = builder().name("OXIDIZED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  169 */   public static StateType CUT_COPPER_SLAB = builder().name("CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  170 */   public static StateType EXPOSED_CUT_COPPER_SLAB = builder().name("EXPOSED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  171 */   public static StateType WEATHERED_CUT_COPPER_SLAB = builder().name("WEATHERED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  172 */   public static StateType OXIDIZED_CUT_COPPER_SLAB = builder().name("OXIDIZED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  173 */   public static StateType WAXED_COPPER_BLOCK = builder().name("WAXED_COPPER_BLOCK").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  174 */   public static StateType WAXED_EXPOSED_COPPER = builder().name("WAXED_EXPOSED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  175 */   public static StateType WAXED_WEATHERED_COPPER = builder().name("WAXED_WEATHERED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  176 */   public static StateType WAXED_OXIDIZED_COPPER = builder().name("WAXED_OXIDIZED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  177 */   public static StateType WAXED_CUT_COPPER = builder().name("WAXED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  178 */   public static StateType WAXED_EXPOSED_CUT_COPPER = builder().name("WAXED_EXPOSED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  179 */   public static StateType WAXED_WEATHERED_CUT_COPPER = builder().name("WAXED_WEATHERED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  180 */   public static StateType WAXED_OXIDIZED_CUT_COPPER = builder().name("WAXED_OXIDIZED_CUT_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  181 */   public static StateType WAXED_CUT_COPPER_STAIRS = builder().name("WAXED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  182 */   public static StateType WAXED_EXPOSED_CUT_COPPER_STAIRS = builder().name("WAXED_EXPOSED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  183 */   public static StateType WAXED_WEATHERED_CUT_COPPER_STAIRS = builder().name("WAXED_WEATHERED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  184 */   public static StateType WAXED_OXIDIZED_CUT_COPPER_STAIRS = builder().name("WAXED_OXIDIZED_CUT_COPPER_STAIRS").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  185 */   public static StateType WAXED_CUT_COPPER_SLAB = builder().name("WAXED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  186 */   public static StateType WAXED_EXPOSED_CUT_COPPER_SLAB = builder().name("WAXED_EXPOSED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  187 */   public static StateType WAXED_WEATHERED_CUT_COPPER_SLAB = builder().name("WAXED_WEATHERED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  188 */   public static StateType WAXED_OXIDIZED_CUT_COPPER_SLAB = builder().name("WAXED_OXIDIZED_CUT_COPPER_SLAB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  189 */   public static StateType OAK_LOG = builder().name("OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  190 */   public static StateType SPRUCE_LOG = builder().name("SPRUCE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  191 */   public static StateType BIRCH_LOG = builder().name("BIRCH_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  192 */   public static StateType JUNGLE_LOG = builder().name("JUNGLE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  193 */   public static StateType ACACIA_LOG = builder().name("ACACIA_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  194 */   public static StateType CHERRY_LOG = builder().name("CHERRY_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  195 */   public static StateType DARK_OAK_LOG = builder().name("DARK_OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  196 */   public static StateType MANGROVE_LOG = builder().name("MANGROVE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  197 */   public static StateType MANGROVE_ROOTS = builder().name("MANGROVE_ROOTS").blastResistance(0.7F).hardness(0.7F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  198 */   public static StateType MUDDY_MANGROVE_ROOTS = builder().name("MUDDY_MANGROVE_ROOTS").blastResistance(0.7F).hardness(0.7F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*  199 */   public static StateType CRIMSON_STEM = builder().name("CRIMSON_STEM").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  200 */   public static StateType WARPED_STEM = builder().name("WARPED_STEM").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  201 */   public static StateType BAMBOO_BLOCK = builder().name("BAMBOO_BLOCK").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  202 */   public static StateType STRIPPED_OAK_LOG = builder().name("STRIPPED_OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  203 */   public static StateType STRIPPED_SPRUCE_LOG = builder().name("STRIPPED_SPRUCE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  204 */   public static StateType STRIPPED_BIRCH_LOG = builder().name("STRIPPED_BIRCH_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  205 */   public static StateType STRIPPED_JUNGLE_LOG = builder().name("STRIPPED_JUNGLE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  206 */   public static StateType STRIPPED_ACACIA_LOG = builder().name("STRIPPED_ACACIA_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  207 */   public static StateType STRIPPED_CHERRY_LOG = builder().name("STRIPPED_CHERRY_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  208 */   public static StateType STRIPPED_DARK_OAK_LOG = builder().name("STRIPPED_DARK_OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  209 */   public static StateType STRIPPED_MANGROVE_LOG = builder().name("STRIPPED_MANGROVE_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  210 */   public static StateType STRIPPED_CRIMSON_STEM = builder().name("STRIPPED_CRIMSON_STEM").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  211 */   public static StateType STRIPPED_WARPED_STEM = builder().name("STRIPPED_WARPED_STEM").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  212 */   public static StateType STRIPPED_OAK_WOOD = builder().name("STRIPPED_OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  213 */   public static StateType STRIPPED_SPRUCE_WOOD = builder().name("STRIPPED_SPRUCE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  214 */   public static StateType STRIPPED_BIRCH_WOOD = builder().name("STRIPPED_BIRCH_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  215 */   public static StateType STRIPPED_JUNGLE_WOOD = builder().name("STRIPPED_JUNGLE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  216 */   public static StateType STRIPPED_ACACIA_WOOD = builder().name("STRIPPED_ACACIA_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  217 */   public static StateType STRIPPED_CHERRY_WOOD = builder().name("STRIPPED_CHERRY_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  218 */   public static StateType STRIPPED_DARK_OAK_WOOD = builder().name("STRIPPED_DARK_OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  219 */   public static StateType STRIPPED_MANGROVE_WOOD = builder().name("STRIPPED_MANGROVE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  220 */   public static StateType STRIPPED_CRIMSON_HYPHAE = builder().name("STRIPPED_CRIMSON_HYPHAE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  221 */   public static StateType STRIPPED_WARPED_HYPHAE = builder().name("STRIPPED_WARPED_HYPHAE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  222 */   public static StateType STRIPPED_BAMBOO_BLOCK = builder().name("STRIPPED_BAMBOO_BLOCK").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  223 */   public static StateType OAK_WOOD = builder().name("OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  224 */   public static StateType SPRUCE_WOOD = builder().name("SPRUCE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  225 */   public static StateType BIRCH_WOOD = builder().name("BIRCH_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  226 */   public static StateType JUNGLE_WOOD = builder().name("JUNGLE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  227 */   public static StateType ACACIA_WOOD = builder().name("ACACIA_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  228 */   public static StateType CHERRY_WOOD = builder().name("CHERRY_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  229 */   public static StateType DARK_OAK_WOOD = builder().name("DARK_OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  230 */   public static StateType MANGROVE_WOOD = builder().name("MANGROVE_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  231 */   public static StateType CRIMSON_HYPHAE = builder().name("CRIMSON_HYPHAE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  232 */   public static StateType WARPED_HYPHAE = builder().name("WARPED_HYPHAE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  233 */   public static StateType OAK_LEAVES = builder().name("OAK_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  234 */   public static StateType SPRUCE_LEAVES = builder().name("SPRUCE_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  235 */   public static StateType BIRCH_LEAVES = builder().name("BIRCH_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  236 */   public static StateType JUNGLE_LEAVES = builder().name("JUNGLE_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  237 */   public static StateType ACACIA_LEAVES = builder().name("ACACIA_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  238 */   public static StateType CHERRY_LEAVES = builder().name("CHERRY_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  239 */   public static StateType DARK_OAK_LEAVES = builder().name("DARK_OAK_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  240 */   public static StateType MANGROVE_LEAVES = builder().name("MANGROVE_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  241 */   public static StateType AZALEA_LEAVES = builder().name("AZALEA_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  242 */   public static StateType FLOWERING_AZALEA_LEAVES = builder().name("FLOWERING_AZALEA_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/*  243 */   public static StateType SPONGE = builder().name("SPONGE").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SPONGE).build();
/*  244 */   public static StateType WET_SPONGE = builder().name("WET_SPONGE").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SPONGE).build();
/*  245 */   public static StateType GLASS = builder().name("GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  246 */   public static StateType TINTED_GLASS = builder().name("TINTED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  247 */   public static StateType LAPIS_BLOCK = builder().name("LAPIS_BLOCK").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  248 */   public static StateType SANDSTONE = builder().name("SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  249 */   public static StateType CHISELED_SANDSTONE = builder().name("CHISELED_SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  250 */   public static StateType CUT_SANDSTONE = builder().name("CUT_SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  251 */   public static StateType COBWEB = builder().name("COBWEB").blastResistance(4.0F).hardness(4.0F).isBlocking(false).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.WEB).build();
/*  252 */   public static StateType SHORT_GRASS = builder().name("SHORT_GRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*  260 */   public static StateType GRASS = SHORT_GRASS;
/*      */   
/*  262 */   public static StateType FERN = builder().name("FERN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  263 */   public static StateType AZALEA = builder().name("AZALEA").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  264 */   public static StateType FLOWERING_AZALEA = builder().name("FLOWERING_AZALEA").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  265 */   public static StateType DEAD_BUSH = builder().name("DEAD_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  266 */   public static StateType SEAGRASS = builder().name("SEAGRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_WATER_PLANT).build();
/*  267 */   public static StateType SEA_PICKLE = builder().name("SEA_PICKLE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WATER_PLANT).build();
/*  268 */   public static StateType WHITE_WOOL = builder().name("WHITE_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  269 */   public static StateType ORANGE_WOOL = builder().name("ORANGE_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  270 */   public static StateType MAGENTA_WOOL = builder().name("MAGENTA_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  271 */   public static StateType LIGHT_BLUE_WOOL = builder().name("LIGHT_BLUE_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  272 */   public static StateType YELLOW_WOOL = builder().name("YELLOW_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  273 */   public static StateType LIME_WOOL = builder().name("LIME_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  274 */   public static StateType PINK_WOOL = builder().name("PINK_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  275 */   public static StateType GRAY_WOOL = builder().name("GRAY_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  276 */   public static StateType LIGHT_GRAY_WOOL = builder().name("LIGHT_GRAY_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  277 */   public static StateType CYAN_WOOL = builder().name("CYAN_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  278 */   public static StateType PURPLE_WOOL = builder().name("PURPLE_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  279 */   public static StateType BLUE_WOOL = builder().name("BLUE_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  280 */   public static StateType BROWN_WOOL = builder().name("BROWN_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  281 */   public static StateType GREEN_WOOL = builder().name("GREEN_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  282 */   public static StateType RED_WOOL = builder().name("RED_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  283 */   public static StateType BLACK_WOOL = builder().name("BLACK_WOOL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  284 */   public static StateType DANDELION = builder().name("DANDELION").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  285 */   public static StateType POPPY = builder().name("POPPY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  286 */   public static StateType BLUE_ORCHID = builder().name("BLUE_ORCHID").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  287 */   public static StateType ALLIUM = builder().name("ALLIUM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  288 */   public static StateType AZURE_BLUET = builder().name("AZURE_BLUET").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  289 */   public static StateType RED_TULIP = builder().name("RED_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  290 */   public static StateType ORANGE_TULIP = builder().name("ORANGE_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  291 */   public static StateType WHITE_TULIP = builder().name("WHITE_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  292 */   public static StateType PINK_TULIP = builder().name("PINK_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  293 */   public static StateType OXEYE_DAISY = builder().name("OXEYE_DAISY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  294 */   public static StateType CORNFLOWER = builder().name("CORNFLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  295 */   public static StateType LILY_OF_THE_VALLEY = builder().name("LILY_OF_THE_VALLEY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  296 */   public static StateType WITHER_ROSE = builder().name("WITHER_ROSE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  297 */   public static StateType TORCHFLOWER = builder().name("TORCHFLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  298 */   public static StateType SPORE_BLOSSOM = builder().name("SPORE_BLOSSOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  299 */   public static StateType BROWN_MUSHROOM = builder().name("BROWN_MUSHROOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  300 */   public static StateType RED_MUSHROOM = builder().name("RED_MUSHROOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  301 */   public static StateType CRIMSON_FUNGUS = builder().name("CRIMSON_FUNGUS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  302 */   public static StateType WARPED_FUNGUS = builder().name("WARPED_FUNGUS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  303 */   public static StateType CRIMSON_ROOTS = builder().name("CRIMSON_ROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_FIREPROOF_PLANT).build();
/*  304 */   public static StateType WARPED_ROOTS = builder().name("WARPED_ROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_FIREPROOF_PLANT).build();
/*  305 */   public static StateType NETHER_SPROUTS = builder().name("NETHER_SPROUTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_FIREPROOF_PLANT).build();
/*  306 */   public static StateType WEEPING_VINES = builder().name("WEEPING_VINES").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  307 */   public static StateType TWISTING_VINES = builder().name("TWISTING_VINES").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  308 */   public static StateType SUGAR_CANE = builder().name("SUGAR_CANE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  309 */   public static StateType KELP = builder().name("KELP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  310 */   public static StateType MOSS_CARPET = builder().name("MOSS_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  311 */   public static StateType PINK_PETALS = builder().name("PINK_PETALS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  312 */   public static StateType MOSS_BLOCK = builder().name("MOSS_BLOCK").blastResistance(0.1F).hardness(0.1F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.MOSS).build();
/*  313 */   public static StateType HANGING_ROOTS = builder().name("HANGING_ROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  314 */   public static StateType BIG_DRIPLEAF = builder().name("BIG_DRIPLEAF").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  315 */   public static StateType SMALL_DRIPLEAF = builder().name("SMALL_DRIPLEAF").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  316 */   public static StateType BAMBOO = builder().name("BAMBOO").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.BAMBOO).build();
/*  317 */   public static StateType OAK_SLAB = builder().name("OAK_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  318 */   public static StateType SPRUCE_SLAB = builder().name("SPRUCE_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  319 */   public static StateType BIRCH_SLAB = builder().name("BIRCH_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  320 */   public static StateType JUNGLE_SLAB = builder().name("JUNGLE_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  321 */   public static StateType ACACIA_SLAB = builder().name("ACACIA_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  322 */   public static StateType CHERRY_SLAB = builder().name("CHERRY_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  323 */   public static StateType DARK_OAK_SLAB = builder().name("DARK_OAK_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  324 */   public static StateType MANGROVE_SLAB = builder().name("MANGROVE_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  325 */   public static StateType BAMBOO_SLAB = builder().name("BAMBOO_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  326 */   public static StateType BAMBOO_MOSAIC_SLAB = builder().name("BAMBOO_MOSAIC_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  327 */   public static StateType CRIMSON_SLAB = builder().name("CRIMSON_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  328 */   public static StateType WARPED_SLAB = builder().name("WARPED_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  329 */   public static StateType STONE_SLAB = builder().name("STONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  330 */   public static StateType SMOOTH_STONE_SLAB = builder().name("SMOOTH_STONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  331 */   public static StateType SANDSTONE_SLAB = builder().name("SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  332 */   public static StateType CUT_SANDSTONE_SLAB = builder().name("CUT_SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  333 */   public static StateType PETRIFIED_OAK_SLAB = builder().name("PETRIFIED_OAK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  334 */   public static StateType COBBLESTONE_SLAB = builder().name("COBBLESTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  335 */   public static StateType BRICK_SLAB = builder().name("BRICK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  336 */   public static StateType STONE_BRICK_SLAB = builder().name("STONE_BRICK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  337 */   public static StateType MUD_BRICK_SLAB = builder().name("MUD_BRICK_SLAB").blastResistance(3.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  338 */   public static StateType NETHER_BRICK_SLAB = builder().name("NETHER_BRICK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  339 */   public static StateType QUARTZ_SLAB = builder().name("QUARTZ_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  340 */   public static StateType RED_SANDSTONE_SLAB = builder().name("RED_SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  341 */   public static StateType CUT_RED_SANDSTONE_SLAB = builder().name("CUT_RED_SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  342 */   public static StateType PURPUR_SLAB = builder().name("PURPUR_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  343 */   public static StateType PRISMARINE_SLAB = builder().name("PRISMARINE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  344 */   public static StateType PRISMARINE_BRICK_SLAB = builder().name("PRISMARINE_BRICK_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  345 */   public static StateType DARK_PRISMARINE_SLAB = builder().name("DARK_PRISMARINE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  346 */   public static StateType SMOOTH_QUARTZ = builder().name("SMOOTH_QUARTZ").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  347 */   public static StateType SMOOTH_RED_SANDSTONE = builder().name("SMOOTH_RED_SANDSTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  348 */   public static StateType SMOOTH_SANDSTONE = builder().name("SMOOTH_SANDSTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  349 */   public static StateType SMOOTH_STONE = builder().name("SMOOTH_STONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  350 */   public static StateType BRICKS = builder().name("BRICKS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  351 */   public static StateType BOOKSHELF = builder().name("BOOKSHELF").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  352 */   public static StateType CHISELED_BOOKSHELF = builder().name("CHISELED_BOOKSHELF").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  353 */   public static StateType DECORATED_POT = builder().name("DECORATED_POT").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATED_POT).build();
/*  354 */   public static StateType MOSSY_COBBLESTONE = builder().name("MOSSY_COBBLESTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  355 */   public static StateType OBSIDIAN = builder().name("OBSIDIAN").blastResistance(1200.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  356 */   public static StateType TORCH = builder().name("TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  357 */   public static StateType END_ROD = builder().name("END_ROD").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  358 */   public static StateType CHORUS_PLANT = builder().name("CHORUS_PLANT").blastResistance(0.4F).hardness(0.4F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  359 */   public static StateType CHORUS_FLOWER = builder().name("CHORUS_FLOWER").blastResistance(0.4F).hardness(0.4F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  360 */   public static StateType PURPUR_BLOCK = builder().name("PURPUR_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  361 */   public static StateType PURPUR_PILLAR = builder().name("PURPUR_PILLAR").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  362 */   public static StateType PURPUR_STAIRS = builder().name("PURPUR_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  363 */   public static StateType SPAWNER = builder().name("SPAWNER").blastResistance(5.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  364 */   public static StateType CHEST = builder().name("CHEST").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  365 */   public static StateType CRAFTING_TABLE = builder().name("CRAFTING_TABLE").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  366 */   public static StateType FARMLAND = builder().name("FARMLAND").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*  367 */   public static StateType FURNACE = builder().name("FURNACE").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  368 */   public static StateType LADDER = builder().name("LADDER").blastResistance(0.4F).hardness(0.4F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  369 */   public static StateType COBBLESTONE_STAIRS = builder().name("COBBLESTONE_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  370 */   public static StateType SNOW = builder().name("SNOW").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.TOP_SNOW).build();
/*  371 */   public static StateType ICE = builder().name("ICE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.ICE).build();
/*  372 */   public static StateType SNOW_BLOCK = builder().name("SNOW_BLOCK").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.SNOW).build();
/*  373 */   public static StateType CACTUS = builder().name("CACTUS").blastResistance(0.4F).hardness(0.4F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CACTUS).build();
/*  374 */   public static StateType CLAY = builder().name("CLAY").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  375 */   public static StateType JUKEBOX = builder().name("JUKEBOX").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  376 */   public static StateType OAK_FENCE = builder().name("OAK_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  377 */   public static StateType SPRUCE_FENCE = builder().name("SPRUCE_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  378 */   public static StateType BIRCH_FENCE = builder().name("BIRCH_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  379 */   public static StateType JUNGLE_FENCE = builder().name("JUNGLE_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  380 */   public static StateType ACACIA_FENCE = builder().name("ACACIA_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  381 */   public static StateType CHERRY_FENCE = builder().name("CHERRY_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  382 */   public static StateType DARK_OAK_FENCE = builder().name("DARK_OAK_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  383 */   public static StateType MANGROVE_FENCE = builder().name("MANGROVE_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  384 */   public static StateType BAMBOO_FENCE = builder().name("BAMBOO_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  385 */   public static StateType CRIMSON_FENCE = builder().name("CRIMSON_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  386 */   public static StateType WARPED_FENCE = builder().name("WARPED_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  387 */   public static StateType PUMPKIN = builder().name("PUMPKIN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.VEGETABLE).build();
/*  388 */   public static StateType CARVED_PUMPKIN = builder().name("CARVED_PUMPKIN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.VEGETABLE).build();
/*  389 */   public static StateType JACK_O_LANTERN = builder().name("JACK_O_LANTERN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.VEGETABLE).build();
/*  390 */   public static StateType NETHERRACK = builder().name("NETHERRACK").blastResistance(0.4F).hardness(0.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  391 */   public static StateType SOUL_SAND = builder().name("SOUL_SAND").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  392 */   public static StateType SOUL_SOIL = builder().name("SOUL_SOIL").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*  393 */   public static StateType BASALT = builder().name("BASALT").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  394 */   public static StateType POLISHED_BASALT = builder().name("POLISHED_BASALT").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  395 */   public static StateType SMOOTH_BASALT = builder().name("SMOOTH_BASALT").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  396 */   public static StateType SOUL_TORCH = builder().name("SOUL_TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  397 */   public static StateType GLOWSTONE = builder().name("GLOWSTONE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  398 */   public static StateType INFESTED_STONE = builder().name("INFESTED_STONE").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  399 */   public static StateType INFESTED_COBBLESTONE = builder().name("INFESTED_COBBLESTONE").blastResistance(0.75F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  400 */   public static StateType INFESTED_STONE_BRICKS = builder().name("INFESTED_STONE_BRICKS").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  401 */   public static StateType INFESTED_MOSSY_STONE_BRICKS = builder().name("INFESTED_MOSSY_STONE_BRICKS").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  402 */   public static StateType INFESTED_CRACKED_STONE_BRICKS = builder().name("INFESTED_CRACKED_STONE_BRICKS").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  403 */   public static StateType INFESTED_CHISELED_STONE_BRICKS = builder().name("INFESTED_CHISELED_STONE_BRICKS").blastResistance(0.75F).hardness(0.75F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  404 */   public static StateType INFESTED_DEEPSLATE = builder().name("INFESTED_DEEPSLATE").blastResistance(0.75F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  405 */   public static StateType STONE_BRICKS = builder().name("STONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  406 */   public static StateType MOSSY_STONE_BRICKS = builder().name("MOSSY_STONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  407 */   public static StateType CRACKED_STONE_BRICKS = builder().name("CRACKED_STONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  408 */   public static StateType CHISELED_STONE_BRICKS = builder().name("CHISELED_STONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  409 */   public static StateType PACKED_MUD = builder().name("PACKED_MUD").blastResistance(3.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*  410 */   public static StateType MUD_BRICKS = builder().name("MUD_BRICKS").blastResistance(3.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  411 */   public static StateType DEEPSLATE_BRICKS = builder().name("DEEPSLATE_BRICKS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  412 */   public static StateType CRACKED_DEEPSLATE_BRICKS = builder().name("CRACKED_DEEPSLATE_BRICKS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  413 */   public static StateType DEEPSLATE_TILES = builder().name("DEEPSLATE_TILES").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  414 */   public static StateType CRACKED_DEEPSLATE_TILES = builder().name("CRACKED_DEEPSLATE_TILES").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  415 */   public static StateType CHISELED_DEEPSLATE = builder().name("CHISELED_DEEPSLATE").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  416 */   public static StateType REINFORCED_DEEPSLATE = builder().name("REINFORCED_DEEPSLATE").blastResistance(1200.0F).hardness(55.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  417 */   public static StateType BROWN_MUSHROOM_BLOCK = builder().name("BROWN_MUSHROOM_BLOCK").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  418 */   public static StateType RED_MUSHROOM_BLOCK = builder().name("RED_MUSHROOM_BLOCK").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  419 */   public static StateType MUSHROOM_STEM = builder().name("MUSHROOM_STEM").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  420 */   public static StateType IRON_BARS = builder().name("IRON_BARS").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  421 */   public static StateType CHAIN = builder().name("CHAIN").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  422 */   public static StateType GLASS_PANE = builder().name("GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  423 */   public static StateType MELON = builder().name("MELON").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.VEGETABLE).build();
/*  424 */   public static StateType VINE = builder().name("VINE").blastResistance(0.2F).hardness(0.2F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  425 */   public static StateType GLOW_LICHEN = builder().name("GLOW_LICHEN").blastResistance(0.2F).hardness(0.2F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  426 */   public static StateType BRICK_STAIRS = builder().name("BRICK_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  427 */   public static StateType STONE_BRICK_STAIRS = builder().name("STONE_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  428 */   public static StateType MUD_BRICK_STAIRS = builder().name("MUD_BRICK_STAIRS").blastResistance(3.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  429 */   public static StateType MYCELIUM = builder().name("MYCELIUM").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  430 */   public static StateType LILY_PAD = builder().name("LILY_PAD").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  431 */   public static StateType NETHER_BRICKS = builder().name("NETHER_BRICKS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  432 */   public static StateType CRACKED_NETHER_BRICKS = builder().name("CRACKED_NETHER_BRICKS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  433 */   public static StateType CHISELED_NETHER_BRICKS = builder().name("CHISELED_NETHER_BRICKS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  434 */   public static StateType NETHER_BRICK_FENCE = builder().name("NETHER_BRICK_FENCE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  435 */   public static StateType NETHER_BRICK_STAIRS = builder().name("NETHER_BRICK_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  436 */   public static StateType SCULK = builder().name("SCULK").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SCULK).build();
/*  437 */   public static StateType SCULK_VEIN = builder().name("SCULK_VEIN").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.SCULK).build();
/*  438 */   public static StateType SCULK_CATALYST = builder().name("SCULK_CATALYST").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SCULK).build();
/*  439 */   public static StateType SCULK_SHRIEKER = builder().name("SCULK_SHRIEKER").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SCULK).build();
/*  440 */   public static StateType ENCHANTING_TABLE = builder().name("ENCHANTING_TABLE").blastResistance(1200.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  441 */   public static StateType END_PORTAL_FRAME = builder().name("END_PORTAL_FRAME").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  442 */   public static StateType END_STONE = builder().name("END_STONE").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  443 */   public static StateType END_STONE_BRICKS = builder().name("END_STONE_BRICKS").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  444 */   public static StateType DRAGON_EGG = builder().name("DRAGON_EGG").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.EGG).build();
/*  445 */   public static StateType SANDSTONE_STAIRS = builder().name("SANDSTONE_STAIRS").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  446 */   public static StateType ENDER_CHEST = builder().name("ENDER_CHEST").blastResistance(600.0F).hardness(22.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  447 */   public static StateType EMERALD_BLOCK = builder().name("EMERALD_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  448 */   public static StateType OAK_STAIRS = builder().name("OAK_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  449 */   public static StateType SPRUCE_STAIRS = builder().name("SPRUCE_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  450 */   public static StateType BIRCH_STAIRS = builder().name("BIRCH_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  451 */   public static StateType JUNGLE_STAIRS = builder().name("JUNGLE_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  452 */   public static StateType ACACIA_STAIRS = builder().name("ACACIA_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  453 */   public static StateType CHERRY_STAIRS = builder().name("CHERRY_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  454 */   public static StateType DARK_OAK_STAIRS = builder().name("DARK_OAK_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  455 */   public static StateType MANGROVE_STAIRS = builder().name("MANGROVE_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  456 */   public static StateType BAMBOO_STAIRS = builder().name("BAMBOO_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  457 */   public static StateType BAMBOO_MOSAIC_STAIRS = builder().name("BAMBOO_MOSAIC_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  458 */   public static StateType CRIMSON_STAIRS = builder().name("CRIMSON_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  459 */   public static StateType WARPED_STAIRS = builder().name("WARPED_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  460 */   public static StateType COMMAND_BLOCK = builder().name("COMMAND_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  461 */   public static StateType BEACON = builder().name("BEACON").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  462 */   public static StateType COBBLESTONE_WALL = builder().name("COBBLESTONE_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  463 */   public static StateType MOSSY_COBBLESTONE_WALL = builder().name("MOSSY_COBBLESTONE_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  464 */   public static StateType BRICK_WALL = builder().name("BRICK_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  465 */   public static StateType PRISMARINE_WALL = builder().name("PRISMARINE_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  466 */   public static StateType RED_SANDSTONE_WALL = builder().name("RED_SANDSTONE_WALL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  467 */   public static StateType MOSSY_STONE_BRICK_WALL = builder().name("MOSSY_STONE_BRICK_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  468 */   public static StateType GRANITE_WALL = builder().name("GRANITE_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  469 */   public static StateType STONE_BRICK_WALL = builder().name("STONE_BRICK_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  470 */   public static StateType MUD_BRICK_WALL = builder().name("MUD_BRICK_WALL").blastResistance(3.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  471 */   public static StateType NETHER_BRICK_WALL = builder().name("NETHER_BRICK_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  472 */   public static StateType ANDESITE_WALL = builder().name("ANDESITE_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  473 */   public static StateType RED_NETHER_BRICK_WALL = builder().name("RED_NETHER_BRICK_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  474 */   public static StateType SANDSTONE_WALL = builder().name("SANDSTONE_WALL").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  475 */   public static StateType END_STONE_BRICK_WALL = builder().name("END_STONE_BRICK_WALL").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  476 */   public static StateType DIORITE_WALL = builder().name("DIORITE_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  477 */   public static StateType BLACKSTONE_WALL = builder().name("BLACKSTONE_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  478 */   public static StateType POLISHED_BLACKSTONE_WALL = builder().name("POLISHED_BLACKSTONE_WALL").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  479 */   public static StateType POLISHED_BLACKSTONE_BRICK_WALL = builder().name("POLISHED_BLACKSTONE_BRICK_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  480 */   public static StateType COBBLED_DEEPSLATE_WALL = builder().name("COBBLED_DEEPSLATE_WALL").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  481 */   public static StateType POLISHED_DEEPSLATE_WALL = builder().name("POLISHED_DEEPSLATE_WALL").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  482 */   public static StateType DEEPSLATE_BRICK_WALL = builder().name("DEEPSLATE_BRICK_WALL").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  483 */   public static StateType DEEPSLATE_TILE_WALL = builder().name("DEEPSLATE_TILE_WALL").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  484 */   public static StateType ANVIL = builder().name("ANVIL").blastResistance(1200.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.HEAVY_METAL).build();
/*  485 */   public static StateType CHIPPED_ANVIL = builder().name("CHIPPED_ANVIL").blastResistance(1200.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.HEAVY_METAL).build();
/*  486 */   public static StateType DAMAGED_ANVIL = builder().name("DAMAGED_ANVIL").blastResistance(1200.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.HEAVY_METAL).build();
/*  487 */   public static StateType CHISELED_QUARTZ_BLOCK = builder().name("CHISELED_QUARTZ_BLOCK").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  488 */   public static StateType QUARTZ_BLOCK = builder().name("QUARTZ_BLOCK").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  489 */   public static StateType QUARTZ_BRICKS = builder().name("QUARTZ_BRICKS").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  490 */   public static StateType QUARTZ_PILLAR = builder().name("QUARTZ_PILLAR").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  491 */   public static StateType QUARTZ_STAIRS = builder().name("QUARTZ_STAIRS").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  492 */   public static StateType WHITE_TERRACOTTA = builder().name("WHITE_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  493 */   public static StateType ORANGE_TERRACOTTA = builder().name("ORANGE_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  494 */   public static StateType MAGENTA_TERRACOTTA = builder().name("MAGENTA_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  495 */   public static StateType LIGHT_BLUE_TERRACOTTA = builder().name("LIGHT_BLUE_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  496 */   public static StateType YELLOW_TERRACOTTA = builder().name("YELLOW_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  497 */   public static StateType LIME_TERRACOTTA = builder().name("LIME_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  498 */   public static StateType PINK_TERRACOTTA = builder().name("PINK_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  499 */   public static StateType GRAY_TERRACOTTA = builder().name("GRAY_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  500 */   public static StateType LIGHT_GRAY_TERRACOTTA = builder().name("LIGHT_GRAY_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  501 */   public static StateType CYAN_TERRACOTTA = builder().name("CYAN_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  502 */   public static StateType PURPLE_TERRACOTTA = builder().name("PURPLE_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  503 */   public static StateType BLUE_TERRACOTTA = builder().name("BLUE_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  504 */   public static StateType BROWN_TERRACOTTA = builder().name("BROWN_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  505 */   public static StateType GREEN_TERRACOTTA = builder().name("GREEN_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  506 */   public static StateType RED_TERRACOTTA = builder().name("RED_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  507 */   public static StateType BLACK_TERRACOTTA = builder().name("BLACK_TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  508 */   public static StateType BARRIER = builder().name("BARRIER").blastResistance(3600000.8F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.BARRIER).build();
/*  509 */   public static StateType LIGHT = builder().name("LIGHT").blastResistance(3600000.8F).hardness(-1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.AIR).build();
/*  510 */   public static StateType HAY_BLOCK = builder().name("HAY_BLOCK").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  511 */   public static StateType WHITE_CARPET = builder().name("WHITE_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  512 */   public static StateType ORANGE_CARPET = builder().name("ORANGE_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  513 */   public static StateType MAGENTA_CARPET = builder().name("MAGENTA_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  514 */   public static StateType LIGHT_BLUE_CARPET = builder().name("LIGHT_BLUE_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  515 */   public static StateType YELLOW_CARPET = builder().name("YELLOW_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  516 */   public static StateType LIME_CARPET = builder().name("LIME_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  517 */   public static StateType PINK_CARPET = builder().name("PINK_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  518 */   public static StateType GRAY_CARPET = builder().name("GRAY_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  519 */   public static StateType LIGHT_GRAY_CARPET = builder().name("LIGHT_GRAY_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  520 */   public static StateType CYAN_CARPET = builder().name("CYAN_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  521 */   public static StateType PURPLE_CARPET = builder().name("PURPLE_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  522 */   public static StateType BLUE_CARPET = builder().name("BLUE_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  523 */   public static StateType BROWN_CARPET = builder().name("BROWN_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  524 */   public static StateType GREEN_CARPET = builder().name("GREEN_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  525 */   public static StateType RED_CARPET = builder().name("RED_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  526 */   public static StateType BLACK_CARPET = builder().name("BLACK_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLOTH_DECORATION).build();
/*  527 */   public static StateType TERRACOTTA = builder().name("TERRACOTTA").blastResistance(4.2F).hardness(1.25F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  528 */   public static StateType PACKED_ICE = builder().name("PACKED_ICE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.ICE_SOLID).build();
/*  529 */   public static StateType DIRT_PATH = builder().name("DIRT_PATH").blastResistance(0.65F).hardness(0.65F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build(); @RuntimeObsolete
/*      */   @Obsolete
/*  531 */   public static StateType GRASS_PATH = builder().name("GRASS_PATH").blastResistance(0.65F).hardness(0.65F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DIRT).build();
/*  532 */   public static StateType SUNFLOWER = builder().name("SUNFLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  533 */   public static StateType LILAC = builder().name("LILAC").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  534 */   public static StateType ROSE_BUSH = builder().name("ROSE_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  535 */   public static StateType PEONY = builder().name("PEONY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  536 */   public static StateType TALL_GRASS = builder().name("TALL_GRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  537 */   public static StateType LARGE_FERN = builder().name("LARGE_FERN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/*  538 */   public static StateType WHITE_STAINED_GLASS = builder().name("WHITE_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  539 */   public static StateType ORANGE_STAINED_GLASS = builder().name("ORANGE_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  540 */   public static StateType MAGENTA_STAINED_GLASS = builder().name("MAGENTA_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  541 */   public static StateType LIGHT_BLUE_STAINED_GLASS = builder().name("LIGHT_BLUE_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  542 */   public static StateType YELLOW_STAINED_GLASS = builder().name("YELLOW_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  543 */   public static StateType LIME_STAINED_GLASS = builder().name("LIME_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  544 */   public static StateType PINK_STAINED_GLASS = builder().name("PINK_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  545 */   public static StateType GRAY_STAINED_GLASS = builder().name("GRAY_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  546 */   public static StateType LIGHT_GRAY_STAINED_GLASS = builder().name("LIGHT_GRAY_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  547 */   public static StateType CYAN_STAINED_GLASS = builder().name("CYAN_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  548 */   public static StateType PURPLE_STAINED_GLASS = builder().name("PURPLE_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  549 */   public static StateType BLUE_STAINED_GLASS = builder().name("BLUE_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  550 */   public static StateType BROWN_STAINED_GLASS = builder().name("BROWN_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  551 */   public static StateType GREEN_STAINED_GLASS = builder().name("GREEN_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  552 */   public static StateType RED_STAINED_GLASS = builder().name("RED_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  553 */   public static StateType BLACK_STAINED_GLASS = builder().name("BLACK_STAINED_GLASS").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  554 */   public static StateType WHITE_STAINED_GLASS_PANE = builder().name("WHITE_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  555 */   public static StateType ORANGE_STAINED_GLASS_PANE = builder().name("ORANGE_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  556 */   public static StateType MAGENTA_STAINED_GLASS_PANE = builder().name("MAGENTA_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  557 */   public static StateType LIGHT_BLUE_STAINED_GLASS_PANE = builder().name("LIGHT_BLUE_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  558 */   public static StateType YELLOW_STAINED_GLASS_PANE = builder().name("YELLOW_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  559 */   public static StateType LIME_STAINED_GLASS_PANE = builder().name("LIME_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  560 */   public static StateType PINK_STAINED_GLASS_PANE = builder().name("PINK_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  561 */   public static StateType GRAY_STAINED_GLASS_PANE = builder().name("GRAY_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  562 */   public static StateType LIGHT_GRAY_STAINED_GLASS_PANE = builder().name("LIGHT_GRAY_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  563 */   public static StateType CYAN_STAINED_GLASS_PANE = builder().name("CYAN_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  564 */   public static StateType PURPLE_STAINED_GLASS_PANE = builder().name("PURPLE_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  565 */   public static StateType BLUE_STAINED_GLASS_PANE = builder().name("BLUE_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  566 */   public static StateType BROWN_STAINED_GLASS_PANE = builder().name("BROWN_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  567 */   public static StateType GREEN_STAINED_GLASS_PANE = builder().name("GREEN_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  568 */   public static StateType RED_STAINED_GLASS_PANE = builder().name("RED_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  569 */   public static StateType BLACK_STAINED_GLASS_PANE = builder().name("BLACK_STAINED_GLASS_PANE").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  570 */   public static StateType PRISMARINE = builder().name("PRISMARINE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  571 */   public static StateType PRISMARINE_BRICKS = builder().name("PRISMARINE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  572 */   public static StateType DARK_PRISMARINE = builder().name("DARK_PRISMARINE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  573 */   public static StateType PRISMARINE_STAIRS = builder().name("PRISMARINE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  574 */   public static StateType PRISMARINE_BRICK_STAIRS = builder().name("PRISMARINE_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  575 */   public static StateType DARK_PRISMARINE_STAIRS = builder().name("DARK_PRISMARINE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  576 */   public static StateType SEA_LANTERN = builder().name("SEA_LANTERN").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  577 */   public static StateType RED_SANDSTONE = builder().name("RED_SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  578 */   public static StateType CHISELED_RED_SANDSTONE = builder().name("CHISELED_RED_SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  579 */   public static StateType CUT_RED_SANDSTONE = builder().name("CUT_RED_SANDSTONE").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  580 */   public static StateType RED_SANDSTONE_STAIRS = builder().name("RED_SANDSTONE_STAIRS").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  581 */   public static StateType REPEATING_COMMAND_BLOCK = builder().name("REPEATING_COMMAND_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  582 */   public static StateType CHAIN_COMMAND_BLOCK = builder().name("CHAIN_COMMAND_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  583 */   public static StateType MAGMA_BLOCK = builder().name("MAGMA_BLOCK").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  584 */   public static StateType NETHER_WART_BLOCK = builder().name("NETHER_WART_BLOCK").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  585 */   public static StateType WARPED_WART_BLOCK = builder().name("WARPED_WART_BLOCK").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  586 */   public static StateType RED_NETHER_BRICKS = builder().name("RED_NETHER_BRICKS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  587 */   public static StateType BONE_BLOCK = builder().name("BONE_BLOCK").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  588 */   public static StateType STRUCTURE_VOID = builder().name("STRUCTURE_VOID").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.STRUCTURAL_AIR).build();
/*  589 */   public static StateType SHULKER_BOX = builder().name("SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  590 */   public static StateType WHITE_SHULKER_BOX = builder().name("WHITE_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  591 */   public static StateType ORANGE_SHULKER_BOX = builder().name("ORANGE_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  592 */   public static StateType MAGENTA_SHULKER_BOX = builder().name("MAGENTA_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  593 */   public static StateType LIGHT_BLUE_SHULKER_BOX = builder().name("LIGHT_BLUE_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  594 */   public static StateType YELLOW_SHULKER_BOX = builder().name("YELLOW_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  595 */   public static StateType LIME_SHULKER_BOX = builder().name("LIME_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  596 */   public static StateType PINK_SHULKER_BOX = builder().name("PINK_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  597 */   public static StateType GRAY_SHULKER_BOX = builder().name("GRAY_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  598 */   public static StateType LIGHT_GRAY_SHULKER_BOX = builder().name("LIGHT_GRAY_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  599 */   public static StateType CYAN_SHULKER_BOX = builder().name("CYAN_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  600 */   public static StateType PURPLE_SHULKER_BOX = builder().name("PURPLE_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  601 */   public static StateType BLUE_SHULKER_BOX = builder().name("BLUE_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  602 */   public static StateType BROWN_SHULKER_BOX = builder().name("BROWN_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  603 */   public static StateType GREEN_SHULKER_BOX = builder().name("GREEN_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  604 */   public static StateType RED_SHULKER_BOX = builder().name("RED_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  605 */   public static StateType BLACK_SHULKER_BOX = builder().name("BLACK_SHULKER_BOX").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.SHULKER_SHELL).build();
/*  606 */   public static StateType WHITE_GLAZED_TERRACOTTA = builder().name("WHITE_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  607 */   public static StateType ORANGE_GLAZED_TERRACOTTA = builder().name("ORANGE_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  608 */   public static StateType MAGENTA_GLAZED_TERRACOTTA = builder().name("MAGENTA_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  609 */   public static StateType LIGHT_BLUE_GLAZED_TERRACOTTA = builder().name("LIGHT_BLUE_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  610 */   public static StateType YELLOW_GLAZED_TERRACOTTA = builder().name("YELLOW_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  611 */   public static StateType LIME_GLAZED_TERRACOTTA = builder().name("LIME_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  612 */   public static StateType PINK_GLAZED_TERRACOTTA = builder().name("PINK_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  613 */   public static StateType GRAY_GLAZED_TERRACOTTA = builder().name("GRAY_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  614 */   public static StateType LIGHT_GRAY_GLAZED_TERRACOTTA = builder().name("LIGHT_GRAY_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  615 */   public static StateType CYAN_GLAZED_TERRACOTTA = builder().name("CYAN_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  616 */   public static StateType PURPLE_GLAZED_TERRACOTTA = builder().name("PURPLE_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  617 */   public static StateType BLUE_GLAZED_TERRACOTTA = builder().name("BLUE_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  618 */   public static StateType BROWN_GLAZED_TERRACOTTA = builder().name("BROWN_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  619 */   public static StateType GREEN_GLAZED_TERRACOTTA = builder().name("GREEN_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  620 */   public static StateType RED_GLAZED_TERRACOTTA = builder().name("RED_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  621 */   public static StateType BLACK_GLAZED_TERRACOTTA = builder().name("BLACK_GLAZED_TERRACOTTA").blastResistance(1.4F).hardness(1.4F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  622 */   public static StateType WHITE_CONCRETE = builder().name("WHITE_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  623 */   public static StateType ORANGE_CONCRETE = builder().name("ORANGE_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  624 */   public static StateType MAGENTA_CONCRETE = builder().name("MAGENTA_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  625 */   public static StateType LIGHT_BLUE_CONCRETE = builder().name("LIGHT_BLUE_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  626 */   public static StateType YELLOW_CONCRETE = builder().name("YELLOW_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  627 */   public static StateType LIME_CONCRETE = builder().name("LIME_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  628 */   public static StateType PINK_CONCRETE = builder().name("PINK_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  629 */   public static StateType GRAY_CONCRETE = builder().name("GRAY_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  630 */   public static StateType LIGHT_GRAY_CONCRETE = builder().name("LIGHT_GRAY_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  631 */   public static StateType CYAN_CONCRETE = builder().name("CYAN_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  632 */   public static StateType PURPLE_CONCRETE = builder().name("PURPLE_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  633 */   public static StateType BLUE_CONCRETE = builder().name("BLUE_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  634 */   public static StateType BROWN_CONCRETE = builder().name("BROWN_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  635 */   public static StateType GREEN_CONCRETE = builder().name("GREEN_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  636 */   public static StateType RED_CONCRETE = builder().name("RED_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  637 */   public static StateType BLACK_CONCRETE = builder().name("BLACK_CONCRETE").blastResistance(1.8F).hardness(1.8F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  638 */   public static StateType WHITE_CONCRETE_POWDER = builder().name("WHITE_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  639 */   public static StateType ORANGE_CONCRETE_POWDER = builder().name("ORANGE_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  640 */   public static StateType MAGENTA_CONCRETE_POWDER = builder().name("MAGENTA_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  641 */   public static StateType LIGHT_BLUE_CONCRETE_POWDER = builder().name("LIGHT_BLUE_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  642 */   public static StateType YELLOW_CONCRETE_POWDER = builder().name("YELLOW_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  643 */   public static StateType LIME_CONCRETE_POWDER = builder().name("LIME_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  644 */   public static StateType PINK_CONCRETE_POWDER = builder().name("PINK_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  645 */   public static StateType GRAY_CONCRETE_POWDER = builder().name("GRAY_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  646 */   public static StateType LIGHT_GRAY_CONCRETE_POWDER = builder().name("LIGHT_GRAY_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  647 */   public static StateType CYAN_CONCRETE_POWDER = builder().name("CYAN_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  648 */   public static StateType PURPLE_CONCRETE_POWDER = builder().name("PURPLE_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  649 */   public static StateType BLUE_CONCRETE_POWDER = builder().name("BLUE_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  650 */   public static StateType BROWN_CONCRETE_POWDER = builder().name("BROWN_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  651 */   public static StateType GREEN_CONCRETE_POWDER = builder().name("GREEN_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  652 */   public static StateType RED_CONCRETE_POWDER = builder().name("RED_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  653 */   public static StateType BLACK_CONCRETE_POWDER = builder().name("BLACK_CONCRETE_POWDER").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SAND).build();
/*  654 */   public static StateType TURTLE_EGG = builder().name("TURTLE_EGG").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.EGG).build();
/*  655 */   public static StateType DEAD_TUBE_CORAL_BLOCK = builder().name("DEAD_TUBE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  656 */   public static StateType DEAD_BRAIN_CORAL_BLOCK = builder().name("DEAD_BRAIN_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  657 */   public static StateType DEAD_BUBBLE_CORAL_BLOCK = builder().name("DEAD_BUBBLE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  658 */   public static StateType DEAD_FIRE_CORAL_BLOCK = builder().name("DEAD_FIRE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  659 */   public static StateType DEAD_HORN_CORAL_BLOCK = builder().name("DEAD_HORN_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  660 */   public static StateType TUBE_CORAL_BLOCK = builder().name("TUBE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  661 */   public static StateType BRAIN_CORAL_BLOCK = builder().name("BRAIN_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  662 */   public static StateType BUBBLE_CORAL_BLOCK = builder().name("BUBBLE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  663 */   public static StateType FIRE_CORAL_BLOCK = builder().name("FIRE_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  664 */   public static StateType HORN_CORAL_BLOCK = builder().name("HORN_CORAL_BLOCK").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  665 */   public static StateType TUBE_CORAL = builder().name("TUBE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  666 */   public static StateType BRAIN_CORAL = builder().name("BRAIN_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  667 */   public static StateType BUBBLE_CORAL = builder().name("BUBBLE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  668 */   public static StateType FIRE_CORAL = builder().name("FIRE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  669 */   public static StateType HORN_CORAL = builder().name("HORN_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  670 */   public static StateType DEAD_BRAIN_CORAL = builder().name("DEAD_BRAIN_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  671 */   public static StateType DEAD_BUBBLE_CORAL = builder().name("DEAD_BUBBLE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  672 */   public static StateType DEAD_FIRE_CORAL = builder().name("DEAD_FIRE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  673 */   public static StateType DEAD_HORN_CORAL = builder().name("DEAD_HORN_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  674 */   public static StateType DEAD_TUBE_CORAL = builder().name("DEAD_TUBE_CORAL").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  675 */   public static StateType TUBE_CORAL_FAN = builder().name("TUBE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  676 */   public static StateType BRAIN_CORAL_FAN = builder().name("BRAIN_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  677 */   public static StateType BUBBLE_CORAL_FAN = builder().name("BUBBLE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  678 */   public static StateType FIRE_CORAL_FAN = builder().name("FIRE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  679 */   public static StateType HORN_CORAL_FAN = builder().name("HORN_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/*  680 */   public static StateType DEAD_TUBE_CORAL_FAN = builder().name("DEAD_TUBE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  681 */   public static StateType DEAD_BRAIN_CORAL_FAN = builder().name("DEAD_BRAIN_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  682 */   public static StateType DEAD_BUBBLE_CORAL_FAN = builder().name("DEAD_BUBBLE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  683 */   public static StateType DEAD_FIRE_CORAL_FAN = builder().name("DEAD_FIRE_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  684 */   public static StateType DEAD_HORN_CORAL_FAN = builder().name("DEAD_HORN_CORAL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  685 */   public static StateType BLUE_ICE = builder().name("BLUE_ICE").blastResistance(2.8F).hardness(2.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.ICE_SOLID).build();
/*  686 */   public static StateType CONDUIT = builder().name("CONDUIT").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GLASS).build();
/*  687 */   public static StateType POLISHED_GRANITE_STAIRS = builder().name("POLISHED_GRANITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  688 */   public static StateType SMOOTH_RED_SANDSTONE_STAIRS = builder().name("SMOOTH_RED_SANDSTONE_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  689 */   public static StateType MOSSY_STONE_BRICK_STAIRS = builder().name("MOSSY_STONE_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  690 */   public static StateType POLISHED_DIORITE_STAIRS = builder().name("POLISHED_DIORITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  691 */   public static StateType MOSSY_COBBLESTONE_STAIRS = builder().name("MOSSY_COBBLESTONE_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  692 */   public static StateType END_STONE_BRICK_STAIRS = builder().name("END_STONE_BRICK_STAIRS").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  693 */   public static StateType STONE_STAIRS = builder().name("STONE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  694 */   public static StateType SMOOTH_SANDSTONE_STAIRS = builder().name("SMOOTH_SANDSTONE_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  695 */   public static StateType SMOOTH_QUARTZ_STAIRS = builder().name("SMOOTH_QUARTZ_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  696 */   public static StateType GRANITE_STAIRS = builder().name("GRANITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  697 */   public static StateType ANDESITE_STAIRS = builder().name("ANDESITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  698 */   public static StateType RED_NETHER_BRICK_STAIRS = builder().name("RED_NETHER_BRICK_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  699 */   public static StateType POLISHED_ANDESITE_STAIRS = builder().name("POLISHED_ANDESITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  700 */   public static StateType DIORITE_STAIRS = builder().name("DIORITE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  701 */   public static StateType COBBLED_DEEPSLATE_STAIRS = builder().name("COBBLED_DEEPSLATE_STAIRS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  702 */   public static StateType POLISHED_DEEPSLATE_STAIRS = builder().name("POLISHED_DEEPSLATE_STAIRS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  703 */   public static StateType DEEPSLATE_BRICK_STAIRS = builder().name("DEEPSLATE_BRICK_STAIRS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  704 */   public static StateType DEEPSLATE_TILE_STAIRS = builder().name("DEEPSLATE_TILE_STAIRS").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  705 */   public static StateType POLISHED_GRANITE_SLAB = builder().name("POLISHED_GRANITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  706 */   public static StateType SMOOTH_RED_SANDSTONE_SLAB = builder().name("SMOOTH_RED_SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  707 */   public static StateType MOSSY_STONE_BRICK_SLAB = builder().name("MOSSY_STONE_BRICK_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  708 */   public static StateType POLISHED_DIORITE_SLAB = builder().name("POLISHED_DIORITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  709 */   public static StateType MOSSY_COBBLESTONE_SLAB = builder().name("MOSSY_COBBLESTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  710 */   public static StateType END_STONE_BRICK_SLAB = builder().name("END_STONE_BRICK_SLAB").blastResistance(9.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  711 */   public static StateType SMOOTH_SANDSTONE_SLAB = builder().name("SMOOTH_SANDSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  712 */   public static StateType SMOOTH_QUARTZ_SLAB = builder().name("SMOOTH_QUARTZ_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  713 */   public static StateType GRANITE_SLAB = builder().name("GRANITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  714 */   public static StateType ANDESITE_SLAB = builder().name("ANDESITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  715 */   public static StateType RED_NETHER_BRICK_SLAB = builder().name("RED_NETHER_BRICK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  716 */   public static StateType POLISHED_ANDESITE_SLAB = builder().name("POLISHED_ANDESITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  717 */   public static StateType DIORITE_SLAB = builder().name("DIORITE_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  718 */   public static StateType COBBLED_DEEPSLATE_SLAB = builder().name("COBBLED_DEEPSLATE_SLAB").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  719 */   public static StateType POLISHED_DEEPSLATE_SLAB = builder().name("POLISHED_DEEPSLATE_SLAB").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  720 */   public static StateType DEEPSLATE_BRICK_SLAB = builder().name("DEEPSLATE_BRICK_SLAB").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  721 */   public static StateType DEEPSLATE_TILE_SLAB = builder().name("DEEPSLATE_TILE_SLAB").blastResistance(6.0F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  722 */   public static StateType SCAFFOLDING = builder().name("SCAFFOLDING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).isShapeExceedsCube(true).setMaterial(MaterialType.DECORATION).build();
/*  723 */   public static StateType REDSTONE_TORCH = builder().name("REDSTONE_TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  724 */   public static StateType REDSTONE_BLOCK = builder().name("REDSTONE_BLOCK").blastResistance(6.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  725 */   public static StateType REPEATER = builder().name("REPEATER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  726 */   public static StateType COMPARATOR = builder().name("COMPARATOR").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  727 */   public static StateType PISTON = builder().name("PISTON").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PISTON).build();
/*  728 */   public static StateType STICKY_PISTON = builder().name("STICKY_PISTON").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PISTON).build();
/*  729 */   public static StateType SLIME_BLOCK = builder().name("SLIME_BLOCK").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  730 */   public static StateType HONEY_BLOCK = builder().name("HONEY_BLOCK").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  731 */   public static StateType OBSERVER = builder().name("OBSERVER").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  732 */   public static StateType HOPPER = builder().name("HOPPER").blastResistance(4.8F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  733 */   public static StateType DISPENSER = builder().name("DISPENSER").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  734 */   public static StateType DROPPER = builder().name("DROPPER").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  735 */   public static StateType LECTERN = builder().name("LECTERN").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  736 */   public static StateType TARGET = builder().name("TARGET").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  737 */   public static StateType LEVER = builder().name("LEVER").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  738 */   public static StateType LIGHTNING_ROD = builder().name("LIGHTNING_ROD").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  739 */   public static StateType DAYLIGHT_DETECTOR = builder().name("DAYLIGHT_DETECTOR").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  740 */   public static StateType SCULK_SENSOR = builder().name("SCULK_SENSOR").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.SCULK).build();
/*  741 */   public static StateType TRIPWIRE_HOOK = builder().name("TRIPWIRE_HOOK").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  742 */   public static StateType TRAPPED_CHEST = builder().name("TRAPPED_CHEST").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  743 */   public static StateType TNT = builder().name("TNT").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.EXPLOSIVE).build();
/*  744 */   public static StateType REDSTONE_LAMP = builder().name("REDSTONE_LAMP").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.BUILDABLE_GLASS).build();
/*  745 */   public static StateType NOTE_BLOCK = builder().name("NOTE_BLOCK").blastResistance(0.8F).hardness(0.8F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  746 */   public static StateType STONE_BUTTON = builder().name("STONE_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  747 */   public static StateType POLISHED_BLACKSTONE_BUTTON = builder().name("POLISHED_BLACKSTONE_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  748 */   public static StateType OAK_BUTTON = builder().name("OAK_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  749 */   public static StateType SPRUCE_BUTTON = builder().name("SPRUCE_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  750 */   public static StateType BIRCH_BUTTON = builder().name("BIRCH_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  751 */   public static StateType JUNGLE_BUTTON = builder().name("JUNGLE_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  752 */   public static StateType ACACIA_BUTTON = builder().name("ACACIA_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  753 */   public static StateType CHERRY_BUTTON = builder().name("CHERRY_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  754 */   public static StateType DARK_OAK_BUTTON = builder().name("DARK_OAK_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  755 */   public static StateType MANGROVE_BUTTON = builder().name("MANGROVE_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  756 */   public static StateType BAMBOO_BUTTON = builder().name("BAMBOO_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  757 */   public static StateType CRIMSON_BUTTON = builder().name("CRIMSON_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  758 */   public static StateType WARPED_BUTTON = builder().name("WARPED_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  759 */   public static StateType STONE_PRESSURE_PLATE = builder().name("STONE_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  760 */   public static StateType POLISHED_BLACKSTONE_PRESSURE_PLATE = builder().name("POLISHED_BLACKSTONE_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/*  761 */   public static StateType LIGHT_WEIGHTED_PRESSURE_PLATE = builder().name("LIGHT_WEIGHTED_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.METAL).build();
/*  762 */   public static StateType HEAVY_WEIGHTED_PRESSURE_PLATE = builder().name("HEAVY_WEIGHTED_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.METAL).build();
/*  763 */   public static StateType OAK_PRESSURE_PLATE = builder().name("OAK_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  764 */   public static StateType SPRUCE_PRESSURE_PLATE = builder().name("SPRUCE_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  765 */   public static StateType BIRCH_PRESSURE_PLATE = builder().name("BIRCH_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  766 */   public static StateType JUNGLE_PRESSURE_PLATE = builder().name("JUNGLE_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  767 */   public static StateType ACACIA_PRESSURE_PLATE = builder().name("ACACIA_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  768 */   public static StateType CHERRY_PRESSURE_PLATE = builder().name("CHERRY_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  769 */   public static StateType DARK_OAK_PRESSURE_PLATE = builder().name("DARK_OAK_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  770 */   public static StateType MANGROVE_PRESSURE_PLATE = builder().name("MANGROVE_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  771 */   public static StateType BAMBOO_PRESSURE_PLATE = builder().name("BAMBOO_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  772 */   public static StateType CRIMSON_PRESSURE_PLATE = builder().name("CRIMSON_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/*  773 */   public static StateType WARPED_PRESSURE_PLATE = builder().name("WARPED_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/*  774 */   public static StateType IRON_DOOR = builder().name("IRON_DOOR").blastResistance(5.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  775 */   public static StateType OAK_DOOR = builder().name("OAK_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  776 */   public static StateType SPRUCE_DOOR = builder().name("SPRUCE_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  777 */   public static StateType BIRCH_DOOR = builder().name("BIRCH_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  778 */   public static StateType JUNGLE_DOOR = builder().name("JUNGLE_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  779 */   public static StateType ACACIA_DOOR = builder().name("ACACIA_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  780 */   public static StateType CHERRY_DOOR = builder().name("CHERRY_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  781 */   public static StateType DARK_OAK_DOOR = builder().name("DARK_OAK_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  782 */   public static StateType MANGROVE_DOOR = builder().name("MANGROVE_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  783 */   public static StateType BAMBOO_DOOR = builder().name("BAMBOO_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  784 */   public static StateType CRIMSON_DOOR = builder().name("CRIMSON_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  785 */   public static StateType WARPED_DOOR = builder().name("WARPED_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  786 */   public static StateType IRON_TRAPDOOR = builder().name("IRON_TRAPDOOR").blastResistance(5.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  787 */   public static StateType OAK_TRAPDOOR = builder().name("OAK_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  788 */   public static StateType SPRUCE_TRAPDOOR = builder().name("SPRUCE_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  789 */   public static StateType BIRCH_TRAPDOOR = builder().name("BIRCH_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  790 */   public static StateType JUNGLE_TRAPDOOR = builder().name("JUNGLE_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  791 */   public static StateType ACACIA_TRAPDOOR = builder().name("ACACIA_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  792 */   public static StateType CHERRY_TRAPDOOR = builder().name("CHERRY_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  793 */   public static StateType DARK_OAK_TRAPDOOR = builder().name("DARK_OAK_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  794 */   public static StateType MANGROVE_TRAPDOOR = builder().name("MANGROVE_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  795 */   public static StateType BAMBOO_TRAPDOOR = builder().name("BAMBOO_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  796 */   public static StateType CRIMSON_TRAPDOOR = builder().name("CRIMSON_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  797 */   public static StateType WARPED_TRAPDOOR = builder().name("WARPED_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  798 */   public static StateType OAK_FENCE_GATE = builder().name("OAK_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  799 */   public static StateType SPRUCE_FENCE_GATE = builder().name("SPRUCE_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  800 */   public static StateType BIRCH_FENCE_GATE = builder().name("BIRCH_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  801 */   public static StateType JUNGLE_FENCE_GATE = builder().name("JUNGLE_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  802 */   public static StateType ACACIA_FENCE_GATE = builder().name("ACACIA_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  803 */   public static StateType CHERRY_FENCE_GATE = builder().name("CHERRY_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  804 */   public static StateType DARK_OAK_FENCE_GATE = builder().name("DARK_OAK_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  805 */   public static StateType MANGROVE_FENCE_GATE = builder().name("MANGROVE_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  806 */   public static StateType BAMBOO_FENCE_GATE = builder().name("BAMBOO_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/*  807 */   public static StateType CRIMSON_FENCE_GATE = builder().name("CRIMSON_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  808 */   public static StateType WARPED_FENCE_GATE = builder().name("WARPED_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.NETHER_WOOD).build();
/*  809 */   public static StateType POWERED_RAIL = builder().name("POWERED_RAIL").blastResistance(0.7F).hardness(0.7F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  810 */   public static StateType DETECTOR_RAIL = builder().name("DETECTOR_RAIL").blastResistance(0.7F).hardness(0.7F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  811 */   public static StateType RAIL = builder().name("RAIL").blastResistance(0.7F).hardness(0.7F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  812 */   public static StateType ACTIVATOR_RAIL = builder().name("ACTIVATOR_RAIL").blastResistance(0.7F).hardness(0.7F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  813 */   public static StateType STRUCTURE_BLOCK = builder().name("STRUCTURE_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  814 */   public static StateType JIGSAW = builder().name("JIGSAW").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  815 */   public static StateType WHEAT = builder().name("WHEAT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  816 */   public static StateType OAK_SIGN = builder().name("OAK_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  817 */   public static StateType SPRUCE_SIGN = builder().name("SPRUCE_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  818 */   public static StateType BIRCH_SIGN = builder().name("BIRCH_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  819 */   public static StateType JUNGLE_SIGN = builder().name("JUNGLE_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  820 */   public static StateType ACACIA_SIGN = builder().name("ACACIA_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  821 */   public static StateType CHERRY_SIGN = builder().name("CHERRY_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  822 */   public static StateType DARK_OAK_SIGN = builder().name("DARK_OAK_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  823 */   public static StateType MANGROVE_SIGN = builder().name("MANGROVE_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  824 */   public static StateType BAMBOO_SIGN = builder().name("BAMBOO_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  825 */   public static StateType CRIMSON_SIGN = builder().name("CRIMSON_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/*  826 */   public static StateType WARPED_SIGN = builder().name("WARPED_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/*  827 */   public static StateType OAK_HANGING_SIGN = builder().name("OAK_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  828 */   public static StateType SPRUCE_HANGING_SIGN = builder().name("SPRUCE_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  829 */   public static StateType BIRCH_HANGING_SIGN = builder().name("BIRCH_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  830 */   public static StateType JUNGLE_HANGING_SIGN = builder().name("JUNGLE_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  831 */   public static StateType ACACIA_HANGING_SIGN = builder().name("ACACIA_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  832 */   public static StateType CHERRY_HANGING_SIGN = builder().name("CHERRY_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  833 */   public static StateType DARK_OAK_HANGING_SIGN = builder().name("DARK_OAK_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  834 */   public static StateType MANGROVE_HANGING_SIGN = builder().name("MANGROVE_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  835 */   public static StateType BAMBOO_HANGING_SIGN = builder().name("BAMBOO_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  836 */   public static StateType CRIMSON_HANGING_SIGN = builder().name("CRIMSON_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  837 */   public static StateType WARPED_HANGING_SIGN = builder().name("WARPED_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  838 */   public static StateType DRIED_KELP_BLOCK = builder().name("DRIED_KELP_BLOCK").blastResistance(2.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  839 */   public static StateType CAKE = builder().name("CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/*  840 */   public static StateType WHITE_BED = builder().name("WHITE_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  841 */   public static StateType ORANGE_BED = builder().name("ORANGE_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  842 */   public static StateType MAGENTA_BED = builder().name("MAGENTA_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  843 */   public static StateType LIGHT_BLUE_BED = builder().name("LIGHT_BLUE_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  844 */   public static StateType YELLOW_BED = builder().name("YELLOW_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  845 */   public static StateType LIME_BED = builder().name("LIME_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  846 */   public static StateType PINK_BED = builder().name("PINK_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  847 */   public static StateType GRAY_BED = builder().name("GRAY_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  848 */   public static StateType LIGHT_GRAY_BED = builder().name("LIGHT_GRAY_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  849 */   public static StateType CYAN_BED = builder().name("CYAN_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  850 */   public static StateType PURPLE_BED = builder().name("PURPLE_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  851 */   public static StateType BLUE_BED = builder().name("BLUE_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  852 */   public static StateType BROWN_BED = builder().name("BROWN_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  853 */   public static StateType GREEN_BED = builder().name("GREEN_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  854 */   public static StateType RED_BED = builder().name("RED_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  855 */   public static StateType BLACK_BED = builder().name("BLACK_BED").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOL).build();
/*  856 */   public static StateType NETHER_WART = builder().name("NETHER_WART").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  857 */   public static StateType BREWING_STAND = builder().name("BREWING_STAND").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  858 */   public static StateType CAULDRON = builder().name("CAULDRON").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  859 */   public static StateType FLOWER_POT = builder().name("FLOWER_POT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  860 */   public static StateType SKELETON_SKULL = builder().name("SKELETON_SKULL").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  861 */   public static StateType WITHER_SKELETON_SKULL = builder().name("WITHER_SKELETON_SKULL").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  862 */   public static StateType PLAYER_HEAD = builder().name("PLAYER_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  863 */   public static StateType ZOMBIE_HEAD = builder().name("ZOMBIE_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  864 */   public static StateType CREEPER_HEAD = builder().name("CREEPER_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  865 */   public static StateType DRAGON_HEAD = builder().name("DRAGON_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  866 */   public static StateType PIGLIN_HEAD = builder().name("PIGLIN_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  867 */   public static StateType WHITE_BANNER = builder().name("WHITE_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  868 */   public static StateType ORANGE_BANNER = builder().name("ORANGE_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  869 */   public static StateType MAGENTA_BANNER = builder().name("MAGENTA_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  870 */   public static StateType LIGHT_BLUE_BANNER = builder().name("LIGHT_BLUE_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  871 */   public static StateType YELLOW_BANNER = builder().name("YELLOW_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  872 */   public static StateType LIME_BANNER = builder().name("LIME_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  873 */   public static StateType PINK_BANNER = builder().name("PINK_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  874 */   public static StateType GRAY_BANNER = builder().name("GRAY_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  875 */   public static StateType LIGHT_GRAY_BANNER = builder().name("LIGHT_GRAY_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  876 */   public static StateType CYAN_BANNER = builder().name("CYAN_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  877 */   public static StateType PURPLE_BANNER = builder().name("PURPLE_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  878 */   public static StateType BLUE_BANNER = builder().name("BLUE_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  879 */   public static StateType BROWN_BANNER = builder().name("BROWN_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  880 */   public static StateType GREEN_BANNER = builder().name("GREEN_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  881 */   public static StateType RED_BANNER = builder().name("RED_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  882 */   public static StateType BLACK_BANNER = builder().name("BLACK_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  883 */   public static StateType LOOM = builder().name("LOOM").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  884 */   public static StateType COMPOSTER = builder().name("COMPOSTER").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  885 */   public static StateType BARREL = builder().name("BARREL").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  886 */   public static StateType SMOKER = builder().name("SMOKER").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  887 */   public static StateType BLAST_FURNACE = builder().name("BLAST_FURNACE").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  888 */   public static StateType CARTOGRAPHY_TABLE = builder().name("CARTOGRAPHY_TABLE").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  889 */   public static StateType FLETCHING_TABLE = builder().name("FLETCHING_TABLE").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  890 */   public static StateType GRINDSTONE = builder().name("GRINDSTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.HEAVY_METAL).build();
/*  891 */   public static StateType SMITHING_TABLE = builder().name("SMITHING_TABLE").blastResistance(2.5F).hardness(2.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  892 */   public static StateType STONECUTTER = builder().name("STONECUTTER").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  893 */   public static StateType BELL = builder().name("BELL").blastResistance(5.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  894 */   public static StateType LANTERN = builder().name("LANTERN").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  895 */   public static StateType SOUL_LANTERN = builder().name("SOUL_LANTERN").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  896 */   public static StateType CAMPFIRE = builder().name("CAMPFIRE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  897 */   public static StateType SOUL_CAMPFIRE = builder().name("SOUL_CAMPFIRE").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  898 */   public static StateType SHROOMLIGHT = builder().name("SHROOMLIGHT").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.GRASS).build();
/*  899 */   public static StateType BEE_NEST = builder().name("BEE_NEST").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  900 */   public static StateType BEEHIVE = builder().name("BEEHIVE").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/*  901 */   public static StateType HONEYCOMB_BLOCK = builder().name("HONEYCOMB_BLOCK").blastResistance(0.6F).hardness(0.6F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CLAY).build();
/*  902 */   public static StateType LODESTONE = builder().name("LODESTONE").blastResistance(3.5F).hardness(3.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.HEAVY_METAL).build();
/*  903 */   public static StateType CRYING_OBSIDIAN = builder().name("CRYING_OBSIDIAN").blastResistance(1200.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  904 */   public static StateType BLACKSTONE = builder().name("BLACKSTONE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  905 */   public static StateType BLACKSTONE_SLAB = builder().name("BLACKSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  906 */   public static StateType BLACKSTONE_STAIRS = builder().name("BLACKSTONE_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  907 */   public static StateType GILDED_BLACKSTONE = builder().name("GILDED_BLACKSTONE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  908 */   public static StateType POLISHED_BLACKSTONE = builder().name("POLISHED_BLACKSTONE").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  909 */   public static StateType POLISHED_BLACKSTONE_SLAB = builder().name("POLISHED_BLACKSTONE_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  910 */   public static StateType POLISHED_BLACKSTONE_STAIRS = builder().name("POLISHED_BLACKSTONE_STAIRS").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  911 */   public static StateType CHISELED_POLISHED_BLACKSTONE = builder().name("CHISELED_POLISHED_BLACKSTONE").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  912 */   public static StateType POLISHED_BLACKSTONE_BRICKS = builder().name("POLISHED_BLACKSTONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  913 */   public static StateType POLISHED_BLACKSTONE_BRICK_SLAB = builder().name("POLISHED_BLACKSTONE_BRICK_SLAB").blastResistance(6.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  914 */   public static StateType POLISHED_BLACKSTONE_BRICK_STAIRS = builder().name("POLISHED_BLACKSTONE_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  915 */   public static StateType CRACKED_POLISHED_BLACKSTONE_BRICKS = builder().name("CRACKED_POLISHED_BLACKSTONE_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  916 */   public static StateType RESPAWN_ANCHOR = builder().name("RESPAWN_ANCHOR").blastResistance(1200.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/*  917 */   public static StateType CANDLE = builder().name("CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  918 */   public static StateType WHITE_CANDLE = builder().name("WHITE_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  919 */   public static StateType ORANGE_CANDLE = builder().name("ORANGE_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  920 */   public static StateType MAGENTA_CANDLE = builder().name("MAGENTA_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  921 */   public static StateType LIGHT_BLUE_CANDLE = builder().name("LIGHT_BLUE_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  922 */   public static StateType YELLOW_CANDLE = builder().name("YELLOW_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  923 */   public static StateType LIME_CANDLE = builder().name("LIME_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  924 */   public static StateType PINK_CANDLE = builder().name("PINK_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  925 */   public static StateType GRAY_CANDLE = builder().name("GRAY_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  926 */   public static StateType LIGHT_GRAY_CANDLE = builder().name("LIGHT_GRAY_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  927 */   public static StateType CYAN_CANDLE = builder().name("CYAN_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  928 */   public static StateType PURPLE_CANDLE = builder().name("PURPLE_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  929 */   public static StateType BLUE_CANDLE = builder().name("BLUE_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  930 */   public static StateType BROWN_CANDLE = builder().name("BROWN_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  931 */   public static StateType GREEN_CANDLE = builder().name("GREEN_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  932 */   public static StateType RED_CANDLE = builder().name("RED_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  933 */   public static StateType BLACK_CANDLE = builder().name("BLACK_CANDLE").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  934 */   public static StateType SMALL_AMETHYST_BUD = builder().name("SMALL_AMETHYST_BUD").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  935 */   public static StateType MEDIUM_AMETHYST_BUD = builder().name("MEDIUM_AMETHYST_BUD").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  936 */   public static StateType LARGE_AMETHYST_BUD = builder().name("LARGE_AMETHYST_BUD").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  937 */   public static StateType AMETHYST_CLUSTER = builder().name("AMETHYST_CLUSTER").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.AMETHYST).build();
/*  938 */   public static StateType POINTED_DRIPSTONE = builder().name("POINTED_DRIPSTONE").blastResistance(3.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/*  939 */   public static StateType OCHRE_FROGLIGHT = builder().name("OCHRE_FROGLIGHT").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.FROGLIGHT).build();
/*  940 */   public static StateType VERDANT_FROGLIGHT = builder().name("VERDANT_FROGLIGHT").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.FROGLIGHT).build();
/*  941 */   public static StateType PEARLESCENT_FROGLIGHT = builder().name("PEARLESCENT_FROGLIGHT").blastResistance(0.3F).hardness(0.3F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.FROGLIGHT).build();
/*  942 */   public static StateType FROGSPAWN = builder().name("FROGSPAWN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.FROGSPAWN).build();
/*  943 */   public static StateType WATER = builder().name("WATER").blastResistance(100.0F).hardness(100.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER).build();
/*  944 */   public static StateType LAVA = builder().name("LAVA").blastResistance(100.0F).hardness(100.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.LAVA).build();
/*  945 */   public static StateType TALL_SEAGRASS = builder().name("TALL_SEAGRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_WATER_PLANT).build();
/*  946 */   public static StateType PISTON_HEAD = builder().name("PISTON_HEAD").blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.PISTON).build();
/*  947 */   public static StateType MOVING_PISTON = builder().name("MOVING_PISTON").blastResistance(0.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.PISTON).build();
/*  948 */   public static StateType WALL_TORCH = builder().name("WALL_TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  949 */   public static StateType FIRE = builder().name("FIRE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.FIRE).build();
/*  950 */   public static StateType SOUL_FIRE = builder().name("SOUL_FIRE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.FIRE).build();
/*  951 */   public static StateType REDSTONE_WIRE = builder().name("REDSTONE_WIRE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  952 */   public static StateType OAK_WALL_SIGN = builder().name("OAK_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  953 */   public static StateType SPRUCE_WALL_SIGN = builder().name("SPRUCE_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  954 */   public static StateType BIRCH_WALL_SIGN = builder().name("BIRCH_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  955 */   public static StateType ACACIA_WALL_SIGN = builder().name("ACACIA_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  956 */   public static StateType CHERRY_WALL_SIGN = builder().name("CHERRY_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  957 */   public static StateType JUNGLE_WALL_SIGN = builder().name("JUNGLE_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  958 */   public static StateType DARK_OAK_WALL_SIGN = builder().name("DARK_OAK_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  959 */   public static StateType MANGROVE_WALL_SIGN = builder().name("MANGROVE_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  960 */   public static StateType BAMBOO_WALL_SIGN = builder().name("BAMBOO_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  961 */   public static StateType OAK_WALL_HANGING_SIGN = builder().name("OAK_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  962 */   public static StateType SPRUCE_WALL_HANGING_SIGN = builder().name("SPRUCE_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  963 */   public static StateType BIRCH_WALL_HANGING_SIGN = builder().name("BIRCH_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.SAND).build();
/*  964 */   public static StateType ACACIA_WALL_HANGING_SIGN = builder().name("ACACIA_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  965 */   public static StateType CHERRY_WALL_HANGING_SIGN = builder().name("CHERRY_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  966 */   public static StateType JUNGLE_WALL_HANGING_SIGN = builder().name("JUNGLE_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  967 */   public static StateType DARK_OAK_WALL_HANGING_SIGN = builder().name("DARK_OAK_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  968 */   public static StateType MANGROVE_WALL_HANGING_SIGN = builder().name("MANGROVE_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  969 */   public static StateType CRIMSON_WALL_HANGING_SIGN = builder().name("CRIMSON_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  970 */   public static StateType WARPED_WALL_HANGING_SIGN = builder().name("WARPED_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  971 */   public static StateType BAMBOO_WALL_HANGING_SIGN = builder().name("BAMBOO_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/*  972 */   public static StateType REDSTONE_WALL_TORCH = builder().name("REDSTONE_WALL_TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  973 */   public static StateType SOUL_WALL_TORCH = builder().name("SOUL_WALL_TORCH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  974 */   public static StateType NETHER_PORTAL = builder().name("NETHER_PORTAL").blastResistance(0.0F).hardness(-1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PORTAL).build();
/*  975 */   public static StateType ATTACHED_PUMPKIN_STEM = builder().name("ATTACHED_PUMPKIN_STEM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  976 */   public static StateType ATTACHED_MELON_STEM = builder().name("ATTACHED_MELON_STEM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  977 */   public static StateType PUMPKIN_STEM = builder().name("PUMPKIN_STEM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  978 */   public static StateType MELON_STEM = builder().name("MELON_STEM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*  979 */   public static StateType WATER_CAULDRON = builder().name("WATER_CAULDRON").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  980 */   public static StateType LAVA_CAULDRON = builder().name("LAVA_CAULDRON").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  981 */   public static StateType POWDER_SNOW_CAULDRON = builder().name("POWDER_SNOW_CAULDRON").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/*  982 */   public static StateType END_PORTAL = builder().name("END_PORTAL").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PORTAL).build();
/*  983 */   public static StateType COCOA = builder().name("COCOA").blastResistance(3.0F).hardness(0.2F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/*  984 */   public static StateType TRIPWIRE = builder().name("TRIPWIRE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/*  985 */   public static StateType POTTED_TORCHFLOWER = builder().name("POTTED_TORCHFLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  986 */   public static StateType POTTED_OAK_SAPLING = builder().name("POTTED_OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  987 */   public static StateType POTTED_SPRUCE_SAPLING = builder().name("POTTED_SPRUCE_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  988 */   public static StateType POTTED_BIRCH_SAPLING = builder().name("POTTED_BIRCH_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  989 */   public static StateType POTTED_JUNGLE_SAPLING = builder().name("POTTED_JUNGLE_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  990 */   public static StateType POTTED_ACACIA_SAPLING = builder().name("POTTED_ACACIA_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  991 */   public static StateType POTTED_CHERRY_SAPLING = builder().name("POTTED_CHERRY_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  992 */   public static StateType POTTED_DARK_OAK_SAPLING = builder().name("POTTED_DARK_OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  993 */   public static StateType POTTED_MANGROVE_PROPAGULE = builder().name("POTTED_MANGROVE_PROPAGULE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  994 */   public static StateType POTTED_FERN = builder().name("POTTED_FERN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  995 */   public static StateType POTTED_DANDELION = builder().name("POTTED_DANDELION").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  996 */   public static StateType POTTED_POPPY = builder().name("POTTED_POPPY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  997 */   public static StateType POTTED_BLUE_ORCHID = builder().name("POTTED_BLUE_ORCHID").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  998 */   public static StateType POTTED_ALLIUM = builder().name("POTTED_ALLIUM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*  999 */   public static StateType POTTED_AZURE_BLUET = builder().name("POTTED_AZURE_BLUET").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1000 */   public static StateType POTTED_RED_TULIP = builder().name("POTTED_RED_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1001 */   public static StateType POTTED_ORANGE_TULIP = builder().name("POTTED_ORANGE_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1002 */   public static StateType POTTED_WHITE_TULIP = builder().name("POTTED_WHITE_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1003 */   public static StateType POTTED_PINK_TULIP = builder().name("POTTED_PINK_TULIP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1004 */   public static StateType POTTED_OXEYE_DAISY = builder().name("POTTED_OXEYE_DAISY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1005 */   public static StateType POTTED_CORNFLOWER = builder().name("POTTED_CORNFLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1006 */   public static StateType POTTED_LILY_OF_THE_VALLEY = builder().name("POTTED_LILY_OF_THE_VALLEY").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1007 */   public static StateType POTTED_WITHER_ROSE = builder().name("POTTED_WITHER_ROSE").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1008 */   public static StateType POTTED_RED_MUSHROOM = builder().name("POTTED_RED_MUSHROOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1009 */   public static StateType POTTED_BROWN_MUSHROOM = builder().name("POTTED_BROWN_MUSHROOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1010 */   public static StateType POTTED_DEAD_BUSH = builder().name("POTTED_DEAD_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1011 */   public static StateType POTTED_CACTUS = builder().name("POTTED_CACTUS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1012 */   public static StateType CARROTS = builder().name("CARROTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1013 */   public static StateType POTATOES = builder().name("POTATOES").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1014 */   public static StateType SKELETON_WALL_SKULL = builder().name("SKELETON_WALL_SKULL").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1015 */   public static StateType WITHER_SKELETON_WALL_SKULL = builder().name("WITHER_SKELETON_WALL_SKULL").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1016 */   public static StateType ZOMBIE_WALL_HEAD = builder().name("ZOMBIE_WALL_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1017 */   public static StateType PLAYER_WALL_HEAD = builder().name("PLAYER_WALL_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1018 */   public static StateType CREEPER_WALL_HEAD = builder().name("CREEPER_WALL_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1019 */   public static StateType DRAGON_WALL_HEAD = builder().name("DRAGON_WALL_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1020 */   public static StateType PIGLIN_WALL_HEAD = builder().name("PIGLIN_WALL_HEAD").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1021 */   public static StateType WHITE_WALL_BANNER = builder().name("WHITE_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1022 */   public static StateType ORANGE_WALL_BANNER = builder().name("ORANGE_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1023 */   public static StateType MAGENTA_WALL_BANNER = builder().name("MAGENTA_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1024 */   public static StateType LIGHT_BLUE_WALL_BANNER = builder().name("LIGHT_BLUE_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1025 */   public static StateType YELLOW_WALL_BANNER = builder().name("YELLOW_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1026 */   public static StateType LIME_WALL_BANNER = builder().name("LIME_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1027 */   public static StateType PINK_WALL_BANNER = builder().name("PINK_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1028 */   public static StateType GRAY_WALL_BANNER = builder().name("GRAY_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1029 */   public static StateType LIGHT_GRAY_WALL_BANNER = builder().name("LIGHT_GRAY_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1030 */   public static StateType CYAN_WALL_BANNER = builder().name("CYAN_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1031 */   public static StateType PURPLE_WALL_BANNER = builder().name("PURPLE_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1032 */   public static StateType BLUE_WALL_BANNER = builder().name("BLUE_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1033 */   public static StateType BROWN_WALL_BANNER = builder().name("BROWN_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1034 */   public static StateType GREEN_WALL_BANNER = builder().name("GREEN_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1035 */   public static StateType RED_WALL_BANNER = builder().name("RED_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1036 */   public static StateType BLACK_WALL_BANNER = builder().name("BLACK_WALL_BANNER").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1037 */   public static StateType TORCHFLOWER_CROP = builder().name("TORCHFLOWER_CROP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1038 */   public static StateType BEETROOTS = builder().name("BEETROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1039 */   public static StateType END_GATEWAY = builder().name("END_GATEWAY").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PORTAL).build();
/* 1040 */   public static StateType FROSTED_ICE = builder().name("FROSTED_ICE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PORTAL).build();
/* 1041 */   public static StateType KELP_PLANT = builder().name("KELP_PLANT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1042 */   public static StateType DEAD_TUBE_CORAL_WALL_FAN = builder().name("DEAD_TUBE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/* 1043 */   public static StateType DEAD_BRAIN_CORAL_WALL_FAN = builder().name("DEAD_BRAIN_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/* 1044 */   public static StateType DEAD_BUBBLE_CORAL_WALL_FAN = builder().name("DEAD_BUBBLE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/* 1045 */   public static StateType DEAD_FIRE_CORAL_WALL_FAN = builder().name("DEAD_FIRE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/* 1046 */   public static StateType DEAD_HORN_CORAL_WALL_FAN = builder().name("DEAD_HORN_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(true).isSolid(false).setMaterial(MaterialType.STONE).build();
/* 1047 */   public static StateType TUBE_CORAL_WALL_FAN = builder().name("TUBE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1048 */   public static StateType BRAIN_CORAL_WALL_FAN = builder().name("BRAIN_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1049 */   public static StateType BUBBLE_CORAL_WALL_FAN = builder().name("BUBBLE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1050 */   public static StateType FIRE_CORAL_WALL_FAN = builder().name("FIRE_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1051 */   public static StateType HORN_CORAL_WALL_FAN = builder().name("HORN_CORAL_WALL_FAN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WATER_PLANT).build();
/* 1052 */   public static StateType BAMBOO_SAPLING = builder().name("BAMBOO_SAPLING").blastResistance(1.0F).hardness(1.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.BAMBOO_SAPLING).build();
/* 1053 */   public static StateType POTTED_BAMBOO = builder().name("POTTED_BAMBOO").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1054 */   public static StateType VOID_AIR = builder().name("VOID_AIR").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).isAir(true).setMaterial(MaterialType.AIR).build();
/* 1055 */   public static StateType CAVE_AIR = builder().name("CAVE_AIR").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).isAir(true).setMaterial(MaterialType.AIR).build();
/* 1056 */   public static StateType BUBBLE_COLUMN = builder().name("BUBBLE_COLUMN").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.BUBBLE_COLUMN).build();
/* 1057 */   public static StateType SWEET_BERRY_BUSH = builder().name("SWEET_BERRY_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1058 */   public static StateType WEEPING_VINES_PLANT = builder().name("WEEPING_VINES_PLANT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1059 */   public static StateType TWISTING_VINES_PLANT = builder().name("TWISTING_VINES_PLANT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1060 */   public static StateType CRIMSON_WALL_SIGN = builder().name("CRIMSON_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/* 1061 */   public static StateType WARPED_WALL_SIGN = builder().name("WARPED_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.NETHER_WOOD).build();
/* 1062 */   public static StateType POTTED_CRIMSON_FUNGUS = builder().name("POTTED_CRIMSON_FUNGUS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1063 */   public static StateType POTTED_WARPED_FUNGUS = builder().name("POTTED_WARPED_FUNGUS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1064 */   public static StateType POTTED_CRIMSON_ROOTS = builder().name("POTTED_CRIMSON_ROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1065 */   public static StateType POTTED_WARPED_ROOTS = builder().name("POTTED_WARPED_ROOTS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1066 */   public static StateType CANDLE_CAKE = builder().name("CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1067 */   public static StateType WHITE_CANDLE_CAKE = builder().name("WHITE_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1068 */   public static StateType ORANGE_CANDLE_CAKE = builder().name("ORANGE_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1069 */   public static StateType MAGENTA_CANDLE_CAKE = builder().name("MAGENTA_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1070 */   public static StateType LIGHT_BLUE_CANDLE_CAKE = builder().name("LIGHT_BLUE_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1071 */   public static StateType YELLOW_CANDLE_CAKE = builder().name("YELLOW_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1072 */   public static StateType LIME_CANDLE_CAKE = builder().name("LIME_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1073 */   public static StateType PINK_CANDLE_CAKE = builder().name("PINK_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1074 */   public static StateType GRAY_CANDLE_CAKE = builder().name("GRAY_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1075 */   public static StateType LIGHT_GRAY_CANDLE_CAKE = builder().name("LIGHT_GRAY_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1076 */   public static StateType CYAN_CANDLE_CAKE = builder().name("CYAN_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1077 */   public static StateType PURPLE_CANDLE_CAKE = builder().name("PURPLE_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1078 */   public static StateType BLUE_CANDLE_CAKE = builder().name("BLUE_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1079 */   public static StateType BROWN_CANDLE_CAKE = builder().name("BROWN_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1080 */   public static StateType GREEN_CANDLE_CAKE = builder().name("GREEN_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1081 */   public static StateType RED_CANDLE_CAKE = builder().name("RED_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1082 */   public static StateType BLACK_CANDLE_CAKE = builder().name("BLACK_CANDLE_CAKE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.CAKE).build();
/* 1083 */   public static StateType POWDER_SNOW = builder().name("POWDER_SNOW").blastResistance(0.25F).hardness(0.25F).isBlocking(false).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.POWDER_SNOW).build();
/* 1084 */   public static StateType CAVE_VINES = builder().name("CAVE_VINES").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1085 */   public static StateType CAVE_VINES_PLANT = builder().name("CAVE_VINES_PLANT").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1086 */   public static StateType BIG_DRIPLEAF_STEM = builder().name("BIG_DRIPLEAF_STEM").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1087 */   public static StateType POTTED_AZALEA_BUSH = builder().name("POTTED_AZALEA_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1088 */   public static StateType POTTED_FLOWERING_AZALEA_BUSH = builder().name("POTTED_FLOWERING_AZALEA_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*      */   
/* 1090 */   public static StateType SUSPICIOUS_GRAVEL = builder().name("SUSPICIOUS_GRAVEL").blastResistance(0.25F)
/* 1091 */     .hardness(0.25F).isBlocking(true).requiresCorrectTool(false)
/* 1092 */     .isSolid(true).setMaterial(MaterialType.SAND).build();
/* 1093 */   public static StateType PITCHER_CROP = builder().name("PITCHER_CROP").blastResistance(0.0F).hardness(0.0F)
/* 1094 */     .isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1095 */   public static StateType PITCHER_PLANT = builder().name("PITCHER_PLANT").blastResistance(0.0F).hardness(0.0F)
/* 1096 */     .isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1097 */   public static StateType SNIFFER_EGG = builder().name("SNIFFER_EGG")
/* 1098 */     .blastResistance(0.5F).hardness(0.5F).isBlocking(true)
/* 1099 */     .requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.EGG).build();
/* 1100 */   public static StateType CALIBRATED_SCULK_SENSOR = builder().name("CALIBRATED_SCULK_SENSOR")
/* 1101 */     .blastResistance(1.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false)
/* 1102 */     .isSolid(true).setMaterial(MaterialType.SCULK).build();
/*      */ 
/*      */   
/* 1105 */   public static StateType TUFF_SLAB = builder().name("TUFF_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1106 */   public static StateType TUFF_STAIRS = builder().name("TUFF_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1107 */   public static StateType TUFF_WALL = builder().name("TUFF_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/* 1108 */   public static StateType POLISHED_TUFF = builder().name("POLISHED_TUFF").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1109 */   public static StateType POLISHED_TUFF_SLAB = builder().name("POLISHED_TUFF_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1110 */   public static StateType POLISHED_TUFF_STAIRS = builder().name("POLISHED_TUFF_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1111 */   public static StateType POLISHED_TUFF_WALL = builder().name("POLISHED_TUFF_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/* 1112 */   public static StateType CHISELED_TUFF = builder().name("CHISELED_TUFF").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1113 */   public static StateType TUFF_BRICKS = builder().name("TUFF_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1114 */   public static StateType TUFF_BRICK_SLAB = builder().name("TUFF_BRICK_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1115 */   public static StateType TUFF_BRICK_STAIRS = builder().name("TUFF_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1116 */   public static StateType TUFF_BRICK_WALL = builder().name("TUFF_BRICK_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.STONE).build();
/* 1117 */   public static StateType CHISELED_TUFF_BRICKS = builder().name("CHISELED_TUFF_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1118 */   public static StateType OXIDIZED_CHISELED_COPPER = builder().name("OXIDIZED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1119 */   public static StateType WEATHERED_CHISELED_COPPER = builder().name("WEATHERED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1120 */   public static StateType EXPOSED_CHISELED_COPPER = builder().name("EXPOSED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1121 */   public static StateType CHISELED_COPPER = builder().name("CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1122 */   public static StateType WAXED_OXIDIZED_CHISELED_COPPER = builder().name("WAXED_OXIDIZED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1123 */   public static StateType WAXED_WEATHERED_CHISELED_COPPER = builder().name("WAXED_WEATHERED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1124 */   public static StateType WAXED_EXPOSED_CHISELED_COPPER = builder().name("WAXED_EXPOSED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1125 */   public static StateType WAXED_CHISELED_COPPER = builder().name("WAXED_CHISELED_COPPER").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1126 */   public static StateType COPPER_DOOR = builder().name("COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1127 */   public static StateType EXPOSED_COPPER_DOOR = builder().name("EXPOSED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1128 */   public static StateType OXIDIZED_COPPER_DOOR = builder().name("OXIDIZED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1129 */   public static StateType WEATHERED_COPPER_DOOR = builder().name("WEATHERED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1130 */   public static StateType WAXED_COPPER_DOOR = builder().name("WAXED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1131 */   public static StateType WAXED_EXPOSED_COPPER_DOOR = builder().name("WAXED_EXPOSED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1132 */   public static StateType WAXED_OXIDIZED_COPPER_DOOR = builder().name("WAXED_OXIDIZED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1133 */   public static StateType WAXED_WEATHERED_COPPER_DOOR = builder().name("WAXED_WEATHERED_COPPER_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1134 */   public static StateType COPPER_TRAPDOOR = builder().name("COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1135 */   public static StateType EXPOSED_COPPER_TRAPDOOR = builder().name("EXPOSED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1136 */   public static StateType OXIDIZED_COPPER_TRAPDOOR = builder().name("OXIDIZED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1137 */   public static StateType WEATHERED_COPPER_TRAPDOOR = builder().name("WEATHERED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1138 */   public static StateType WAXED_COPPER_TRAPDOOR = builder().name("WAXED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1139 */   public static StateType WAXED_EXPOSED_COPPER_TRAPDOOR = builder().name("WAXED_EXPOSED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1140 */   public static StateType WAXED_OXIDIZED_COPPER_TRAPDOOR = builder().name("WAXED_OXIDIZED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1141 */   public static StateType WAXED_WEATHERED_COPPER_TRAPDOOR = builder().name("WAXED_WEATHERED_COPPER_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1142 */   public static StateType COPPER_GRATE = builder().name("COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1143 */   public static StateType EXPOSED_COPPER_GRATE = builder().name("EXPOSED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1144 */   public static StateType WEATHERED_COPPER_GRATE = builder().name("WEATHERED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1145 */   public static StateType OXIDIZED_COPPER_GRATE = builder().name("OXIDIZED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1146 */   public static StateType WAXED_COPPER_GRATE = builder().name("WAXED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1147 */   public static StateType WAXED_EXPOSED_COPPER_GRATE = builder().name("WAXED_EXPOSED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1148 */   public static StateType WAXED_WEATHERED_COPPER_GRATE = builder().name("WAXED_WEATHERED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1149 */   public static StateType WAXED_OXIDIZED_COPPER_GRATE = builder().name("WAXED_OXIDIZED_COPPER_GRATE").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1150 */   public static StateType COPPER_BULB = builder().name("COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1151 */   public static StateType EXPOSED_COPPER_BULB = builder().name("EXPOSED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1152 */   public static StateType WEATHERED_COPPER_BULB = builder().name("WEATHERED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1153 */   public static StateType OXIDIZED_COPPER_BULB = builder().name("OXIDIZED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1154 */   public static StateType WAXED_COPPER_BULB = builder().name("WAXED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1155 */   public static StateType WAXED_EXPOSED_COPPER_BULB = builder().name("WAXED_EXPOSED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1156 */   public static StateType WAXED_WEATHERED_COPPER_BULB = builder().name("WAXED_WEATHERED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1157 */   public static StateType WAXED_OXIDIZED_COPPER_BULB = builder().name("WAXED_OXIDIZED_COPPER_BULB").blastResistance(6.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1158 */   public static StateType CRAFTER = builder().name("CRAFTER").blastResistance(3.5F).hardness(1.5F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1159 */   public static StateType TRIAL_SPAWNER = builder().name("TRIAL_SPAWNER").blastResistance(50.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/*      */ 
/*      */   
/* 1162 */   public static StateType VAULT = builder().name("VAULT").blastResistance(50.0F).hardness(50.0F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.STONE).build();
/* 1163 */   public static StateType HEAVY_CORE = builder().name("HEAVY_CORE").blastResistance(1200.0F).hardness(10.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.METAL).build();
/*      */ 
/*      */   
/* 1166 */   public static StateType PALE_OAK_WOOD = builder().name("PALE_OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1167 */   public static StateType PALE_OAK_PLANKS = builder().name("PALE_OAK_PLANKS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1168 */   public static StateType PALE_OAK_SAPLING = builder().name("PALE_OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1169 */   public static StateType PALE_OAK_LOG = builder().name("PALE_OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1170 */   public static StateType STRIPPED_PALE_OAK_LOG = builder().name("STRIPPED_PALE_OAK_LOG").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1171 */   public static StateType STRIPPED_PALE_OAK_WOOD = builder().name("STRIPPED_PALE_OAK_WOOD").blastResistance(2.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1172 */   public static StateType PALE_OAK_LEAVES = builder().name("PALE_OAK_LEAVES").blastResistance(0.2F).hardness(0.2F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.LEAVES).build();
/* 1173 */   public static StateType CREAKING_HEART = builder().name("CREAKING_HEART").blastResistance(5.0F).hardness(5.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1174 */   public static StateType PALE_OAK_SIGN = builder().name("PALE_OAK_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1175 */   public static StateType PALE_OAK_WALL_SIGN = builder().name("PALE_OAK_WALL_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1176 */   public static StateType PALE_OAK_HANGING_SIGN = builder().name("PALE_OAK_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1177 */   public static StateType PALE_OAK_WALL_HANGING_SIGN = builder().name("PALE_OAK_WALL_HANGING_SIGN").blastResistance(1.0F).hardness(1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1178 */   public static StateType PALE_OAK_PRESSURE_PLATE = builder().name("PALE_OAK_PRESSURE_PLATE").blastResistance(0.5F).hardness(0.5F).isBlocking(true).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.WOOD).build();
/* 1179 */   public static StateType PALE_OAK_TRAPDOOR = builder().name("PALE_OAK_TRAPDOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1180 */   public static StateType POTTED_PALE_OAK_SAPLING = builder().name("POTTED_PALE_OAK_SAPLING").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1181 */   public static StateType PALE_OAK_BUTTON = builder().name("PALE_OAK_BUTTON").blastResistance(0.5F).hardness(0.5F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.DECORATION).build();
/* 1182 */   public static StateType PALE_OAK_STAIRS = builder().name("PALE_OAK_STAIRS").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1183 */   public static StateType PALE_OAK_SLAB = builder().name("PALE_OAK_SLAB").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1184 */   public static StateType PALE_OAK_FENCE_GATE = builder().name("PALE_OAK_FENCE_GATE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/* 1185 */   public static StateType PALE_OAK_FENCE = builder().name("PALE_OAK_FENCE").blastResistance(3.0F).hardness(2.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.WOOD).build();
/* 1186 */   public static StateType PALE_OAK_DOOR = builder().name("PALE_OAK_DOOR").blastResistance(3.0F).hardness(3.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.WOOD).build();
/* 1187 */   public static StateType PALE_MOSS_BLOCK = builder().name("PALE_MOSS_BLOCK").blastResistance(0.1F).hardness(0.1F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.MOSS).build();
/* 1188 */   public static StateType PALE_MOSS_CARPET = builder().name("PALE_MOSS_CARPET").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.PLANT).build();
/* 1189 */   public static StateType PALE_HANGING_MOSS = builder().name("PALE_HANGING_MOSS").blastResistance(0.1F).hardness(0.1F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*      */ 
/*      */   
/* 1192 */   public static StateType RESIN_CLUMP = builder().name("RESIN_CLUMP").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.RESIN).build();
/* 1193 */   public static StateType RESIN_BLOCK = builder().name("RESIN_BLOCK").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.RESIN).build();
/* 1194 */   public static StateType RESIN_BRICKS = builder().name("RESIN_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.RESIN).build();
/* 1195 */   public static StateType RESIN_BRICK_STAIRS = builder().name("RESIN_BRICK_STAIRS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.RESIN).build();
/* 1196 */   public static StateType RESIN_BRICK_SLAB = builder().name("RESIN_BRICK_SLAB").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.RESIN).build();
/* 1197 */   public static StateType RESIN_BRICK_WALL = builder().name("RESIN_BRICK_WALL").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).isShapeExceedsCube(true).setMaterial(MaterialType.RESIN).build();
/* 1198 */   public static StateType CHISELED_RESIN_BRICKS = builder().name("CHISELED_RESIN_BRICKS").blastResistance(6.0F).hardness(1.5F).isBlocking(true).requiresCorrectTool(true).isSolid(true).setMaterial(MaterialType.RESIN).build();
/* 1199 */   public static StateType OPEN_EYEBLOSSOM = builder().name("OPEN_EYEBLOSSOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1200 */   public static StateType CLOSED_EYEBLOSSOM = builder().name("CLOSED_EYEBLOSSOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1201 */   public static StateType POTTED_OPEN_EYEBLOSSOM = builder().name("POTTED_OPEN_EYEBLOSSOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/* 1202 */   public static StateType POTTED_CLOSED_EYEBLOSSOM = builder().name("POTTED_CLOSED_EYEBLOSSOM").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.DECORATION).build();
/*      */ 
/*      */   
/* 1205 */   public static StateType BUSH = builder().name("BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/* 1206 */   public static StateType SHORT_DRY_GRASS = builder().name("SHORT_DRY_GRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/* 1207 */   public static StateType TALL_DRY_GRASS = builder().name("TALL_DRY_GRASS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/* 1208 */   public static StateType CACTUS_FLOWER = builder().name("CACTUS_FLOWER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1209 */   public static StateType TEST_BLOCK = builder().name("TEST_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1210 */   public static StateType TEST_INSTANCE_BLOCK = builder().name("TEST_INSTANCE_BLOCK").blastResistance(3600000.0F).hardness(-1.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.METAL).build();
/* 1211 */   public static StateType WILDFLOWERS = builder().name("WILDFLOWERS").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/* 1212 */   public static StateType LEAF_LITTER = builder().name("LEAF_LITTER").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.REPLACEABLE_PLANT).build();
/* 1213 */   public static StateType FIREFLY_BUSH = builder().name("FIREFLY_BUSH").blastResistance(0.0F).hardness(0.0F).isBlocking(false).requiresCorrectTool(false).isSolid(false).setMaterial(MaterialType.PLANT).build();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1218 */   public static StateType DRIED_GHAST = builder().name("DRIED_GHAST").blastResistance(0.0F).hardness(0.0F).isBlocking(true).requiresCorrectTool(false).isSolid(true).setMaterial(MaterialType.STONE).build();
/*      */ 
/*      */   
/*      */   static {
/* 1222 */     REGISTRY.unloadMappings();
/*      */     
/* 1224 */     List<StateType> stateTypes = new ArrayList<>(REGISTRY.size());
/* 1225 */     for (StateType.Mapped type : REGISTRY.getEntries()) {
/* 1226 */       stateTypes.add(type.getStateType());
/*      */     }
/* 1228 */     STATE_TYPE_VALUES = Collections.unmodifiableList(stateTypes);
/*      */   }
/*      */   
/*      */   public static Builder builder() {
/* 1232 */     return new Builder();
/*      */   }
/*      */   
/*      */   public static class Builder {
/*      */     ResourceLocation name;
/* 1237 */     float blastResistance = 0.0F;
/* 1238 */     float hardness = 0.0F;
/*      */     boolean isSolid;
/*      */     boolean isBlocking;
/*      */     boolean isAir;
/*      */     boolean requiresCorrectTool = false;
/*      */     boolean isShapeExceedsCube;
/*      */     MaterialType materialType;
/*      */     
/*      */     public Builder name(String name) {
/* 1247 */       this.name = new ResourceLocation(name);
/* 1248 */       return this;
/*      */     }
/*      */     
/*      */     public Builder blastResistance(float blastResistance) {
/* 1252 */       this.blastResistance = blastResistance;
/* 1253 */       return this;
/*      */     }
/*      */     
/*      */     public Builder hardness(float hardness) {
/* 1257 */       this.hardness = hardness;
/* 1258 */       return this;
/*      */     }
/*      */     
/*      */     public Builder isSolid(boolean isSolid) {
/* 1262 */       this.isSolid = isSolid;
/* 1263 */       return this;
/*      */     }
/*      */     
/*      */     public Builder isBlocking(boolean isBlocking) {
/* 1267 */       this.isBlocking = isBlocking;
/* 1268 */       return this;
/*      */     }
/*      */     
/*      */     public Builder isAir(boolean isAir) {
/* 1272 */       this.isAir = isAir;
/* 1273 */       return this;
/*      */     }
/*      */     
/*      */     public Builder requiresCorrectTool(boolean requiresCorrectTool) {
/* 1277 */       this.requiresCorrectTool = requiresCorrectTool;
/* 1278 */       return this;
/*      */     }
/*      */     
/*      */     public Builder setMaterial(MaterialType materialType) {
/* 1282 */       this.materialType = materialType;
/* 1283 */       return this;
/*      */     }
/*      */     
/*      */     public Builder isShapeExceedsCube(boolean b) {
/* 1287 */       this.isShapeExceedsCube = b;
/* 1288 */       return this;
/*      */     }
/*      */     
/*      */     public StateType build() {
/* 1292 */       String definedName = this.name.getKey().toLowerCase(Locale.ROOT);
/* 1293 */       return ((StateType.Mapped)StateTypes.REGISTRY.define(definedName, data -> (new StateType(data, this.blastResistance, this.hardness, this.isSolid, this.isBlocking, this.isAir, this.requiresCorrectTool, this.isShapeExceedsCube, this.materialType)).getMapped()))
/*      */ 
/*      */         
/* 1296 */         .getStateType();
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\states\type\StateTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
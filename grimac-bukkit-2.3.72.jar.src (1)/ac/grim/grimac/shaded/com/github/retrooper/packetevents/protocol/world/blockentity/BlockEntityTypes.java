/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.blockentity;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Function;
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
/*     */ public final class BlockEntityTypes
/*     */ {
/*  29 */   private static final VersionedRegistry<BlockEntityType> REGISTRY = new VersionedRegistry("block_entity_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BlockEntityType define(String key) {
/*  35 */     return (BlockEntityType)REGISTRY.define(key, StaticBlockEntityType::new);
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<BlockEntityType> getRegistry() {
/*  39 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static BlockEntityType getByName(String name) {
/*  43 */     return (BlockEntityType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static BlockEntityType getById(ClientVersion version, int id) {
/*  47 */     return (BlockEntityType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  50 */   public static final BlockEntityType FURNACE = define("furnace");
/*  51 */   public static final BlockEntityType CHEST = define("chest");
/*  52 */   public static final BlockEntityType TRAPPED_CHEST = define("trapped_chest");
/*  53 */   public static final BlockEntityType ENDER_CHEST = define("ender_chest");
/*  54 */   public static final BlockEntityType JUKEBOX = define("jukebox");
/*  55 */   public static final BlockEntityType DISPENSER = define("dispenser");
/*  56 */   public static final BlockEntityType DROPPER = define("dropper");
/*  57 */   public static final BlockEntityType SIGN = define("sign");
/*  58 */   public static final BlockEntityType HANGING_SIGN = define("hanging_sign");
/*  59 */   public static final BlockEntityType MOB_SPAWNER = define("mob_spawner");
/*  60 */   public static final BlockEntityType PISTON = define("piston");
/*  61 */   public static final BlockEntityType BREWING_STAND = define("brewing_stand");
/*  62 */   public static final BlockEntityType ENCHANTING_TABLE = define("enchanting_table");
/*  63 */   public static final BlockEntityType END_PORTAL = define("end_portal");
/*  64 */   public static final BlockEntityType BEACON = define("beacon");
/*  65 */   public static final BlockEntityType SKULL = define("skull");
/*  66 */   public static final BlockEntityType DAYLIGHT_DETECTOR = define("daylight_detector");
/*  67 */   public static final BlockEntityType HOPPER = define("hopper");
/*  68 */   public static final BlockEntityType COMPARATOR = define("comparator");
/*  69 */   public static final BlockEntityType BANNER = define("banner");
/*  70 */   public static final BlockEntityType STRUCTURE_BLOCK = define("structure_block");
/*  71 */   public static final BlockEntityType END_GATEWAY = define("end_gateway");
/*  72 */   public static final BlockEntityType COMMAND_BLOCK = define("command_block");
/*  73 */   public static final BlockEntityType SHULKER_BOX = define("shulker_box");
/*  74 */   public static final BlockEntityType BED = define("bed");
/*  75 */   public static final BlockEntityType CONDUIT = define("conduit");
/*  76 */   public static final BlockEntityType BARREL = define("barrel");
/*  77 */   public static final BlockEntityType SMOKER = define("smoker");
/*  78 */   public static final BlockEntityType BLAST_FURNACE = define("blast_furnace");
/*  79 */   public static final BlockEntityType LECTERN = define("lectern");
/*  80 */   public static final BlockEntityType BELL = define("bell");
/*  81 */   public static final BlockEntityType JIGSAW = define("jigsaw");
/*  82 */   public static final BlockEntityType CAMPFIRE = define("campfire");
/*  83 */   public static final BlockEntityType BEEHIVE = define("beehive");
/*  84 */   public static final BlockEntityType SCULK_SENSOR = define("sculk_sensor");
/*  85 */   public static final BlockEntityType CALIBRATED_SCULK_SENSOR = define("calibrated_sculk_sensor");
/*  86 */   public static final BlockEntityType SCULK_CATALYST = define("sculk_catalyst");
/*  87 */   public static final BlockEntityType SCULK_SHRIEKER = define("sculk_shrieker");
/*  88 */   public static final BlockEntityType CHISELED_BOOKSHELF = define("chiseled_bookshelf");
/*     */   @Obsolete
/*  90 */   public static final BlockEntityType SUSPICIOUS_SAND = define("suspicious_sand");
/*  91 */   public static final BlockEntityType BRUSHABLE_BLOCK = define("brushable_block");
/*  92 */   public static final BlockEntityType DECORATED_POT = define("decorated_pot");
/*  93 */   public static final BlockEntityType CRAFTER = define("crafter");
/*  94 */   public static final BlockEntityType TRIAL_SPAWNER = define("trial_spawner");
/*  95 */   public static final BlockEntityType VAULT = define("vault");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final BlockEntityType CREAKING_HEART = define("creaking_heart");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   public static final BlockEntityType TEST_BLOCK = define("test_block");
/*     */ 
/*     */ 
/*     */   
/* 109 */   public static final BlockEntityType TEST_INSTANCE_BLOCK = define("test_instance_block");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<BlockEntityType> values() {
/* 117 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 121 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\blockentity\BlockEntityTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
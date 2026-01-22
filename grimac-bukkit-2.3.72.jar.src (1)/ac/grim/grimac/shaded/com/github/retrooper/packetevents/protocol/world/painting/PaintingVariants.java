/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public final class PaintingVariants
/*     */ {
/*  29 */   private static final VersionedRegistry<PaintingVariant> REGISTRY = new VersionedRegistry("painting_variant");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static PaintingVariant define(String key, int width, int height) {
/*  36 */     ResourceLocation assetId = ResourceLocation.minecraft(key);
/*  37 */     return define(key, width, height, assetId);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static PaintingVariant define(String key, int width, int height, ResourceLocation assetId) {
/*  42 */     return (PaintingVariant)REGISTRY.define(key, data -> new StaticPaintingVariant(data, width, height, assetId));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<PaintingVariant> getRegistry() {
/*  47 */     return REGISTRY;
/*     */   }
/*     */   @Nullable
/*     */   public static PaintingVariant getByName(String name) {
/*  51 */     return (PaintingVariant)REGISTRY.getByName(name);
/*     */   }
/*     */   @Nullable
/*     */   public static PaintingVariant getById(ClientVersion version, int id) {
/*  55 */     return (PaintingVariant)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  58 */   public static final PaintingVariant POINTER = define("pointer", 4, 4);
/*  59 */   public static final PaintingVariant CREEBET = define("creebet", 2, 1);
/*  60 */   public static final PaintingVariant PRAIRIE_RIDE = define("prairie_ride", 1, 2);
/*  61 */   public static final PaintingVariant POOL = define("pool", 2, 1);
/*  62 */   public static final PaintingVariant EARTH = define("earth", 2, 2);
/*  63 */   public static final PaintingVariant SKELETON = define("skeleton", 4, 3);
/*  64 */   public static final PaintingVariant MATCH = define("match", 2, 2);
/*  65 */   public static final PaintingVariant POND = define("pond", 3, 4);
/*  66 */   public static final PaintingVariant HUMBLE = define("humble", 2, 2);
/*  67 */   public static final PaintingVariant PIGSCENE = define("pigscene", 4, 4);
/*  68 */   public static final PaintingVariant WATER = define("water", 2, 2);
/*  69 */   public static final PaintingVariant ALBAN = define("alban", 1, 1);
/*  70 */   public static final PaintingVariant FINDING = define("finding", 4, 2);
/*  71 */   public static final PaintingVariant AZTEC2 = define("aztec2", 1, 1);
/*  72 */   public static final PaintingVariant TIDES = define("tides", 3, 3);
/*  73 */   public static final PaintingVariant FIGHTERS = define("fighters", 4, 2);
/*  74 */   public static final PaintingVariant FIRE = define("fire", 2, 2);
/*  75 */   public static final PaintingVariant CHANGING = define("changing", 4, 2);
/*  76 */   public static final PaintingVariant BURNING_SKULL = define("burning_skull", 4, 4);
/*  77 */   public static final PaintingVariant COTAN = define("cotan", 3, 3);
/*  78 */   public static final PaintingVariant WANDERER = define("wanderer", 1, 2);
/*  79 */   public static final PaintingVariant UNPACKED = define("unpacked", 4, 4);
/*  80 */   public static final PaintingVariant SUNSET = define("sunset", 2, 1);
/*  81 */   public static final PaintingVariant FERN = define("fern", 3, 3);
/*  82 */   public static final PaintingVariant BUST = define("bust", 2, 2);
/*  83 */   public static final PaintingVariant WIND = define("wind", 2, 2);
/*  84 */   public static final PaintingVariant LOWMIST = define("lowmist", 4, 2);
/*  85 */   public static final PaintingVariant PASSAGE = define("passage", 4, 2);
/*  86 */   public static final PaintingVariant SUNFLOWERS = define("sunflowers", 3, 3);
/*  87 */   public static final PaintingVariant GRAHAM = define("graham", 1, 2);
/*  88 */   public static final PaintingVariant WASTELAND = define("wasteland", 1, 1);
/*  89 */   public static final PaintingVariant SKULL_AND_ROSES = define("skull_and_roses", 2, 2);
/*  90 */   public static final PaintingVariant BOUQUET = define("bouquet", 3, 3);
/*  91 */   public static final PaintingVariant ORB = define("orb", 4, 4);
/*  92 */   public static final PaintingVariant BOMB = define("bomb", 1, 1);
/*  93 */   public static final PaintingVariant WITHER = define("wither", 2, 2);
/*  94 */   public static final PaintingVariant BACKYARD = define("backyard", 3, 4);
/*  95 */   public static final PaintingVariant ENDBOSS = define("endboss", 3, 3);
/*  96 */   public static final PaintingVariant MEDITATIVE = define("meditative", 1, 1);
/*  97 */   public static final PaintingVariant VOID = define("void", 2, 2);
/*  98 */   public static final PaintingVariant KEBAB = define("kebab", 1, 1);
/*  99 */   public static final PaintingVariant SEA = define("sea", 2, 1);
/* 100 */   public static final PaintingVariant DONKEY_KONG = define("donkey_kong", 4, 3);
/* 101 */   public static final PaintingVariant BAROQUE = define("baroque", 2, 2);
/* 102 */   public static final PaintingVariant STAGE = define("stage", 2, 2);
/* 103 */   public static final PaintingVariant AZTEC = define("aztec", 1, 1);
/* 104 */   public static final PaintingVariant PLANT = define("plant", 1, 1);
/* 105 */   public static final PaintingVariant CAVEBIRD = define("cavebird", 3, 3);
/* 106 */   public static final PaintingVariant COURBET = define("courbet", 2, 1);
/* 107 */   public static final PaintingVariant OWLEMONS = define("owlemons", 3, 3);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   public static final PaintingVariant DENNIS = define("dennis", 3, 3);
/*     */   
/*     */   static {
/* 115 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\painting\PaintingVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
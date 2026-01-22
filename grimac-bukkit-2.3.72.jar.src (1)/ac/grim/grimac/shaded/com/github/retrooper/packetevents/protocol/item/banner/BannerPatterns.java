/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.util.Collection;
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
/*     */ public final class BannerPatterns
/*     */ {
/*  30 */   private static final VersionedRegistry<BannerPattern> REGISTRY = new VersionedRegistry("banner_pattern");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static BannerPattern define(String key) {
/*  37 */     ResourceLocation assetId = ResourceLocation.minecraft(key);
/*  38 */     String translationKey = "block.minecraft.banner." + key;
/*  39 */     return define(key, assetId, translationKey);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static BannerPattern define(String key, ResourceLocation assetId, String translationKey) {
/*  44 */     return (BannerPattern)REGISTRY.define(key, data -> new StaticBannerPattern(data, assetId, translationKey));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<BannerPattern> getRegistry() {
/*  49 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static BannerPattern getByName(String name) {
/*  53 */     return (BannerPattern)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static BannerPattern getById(ClientVersion version, int id) {
/*  57 */     return (BannerPattern)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  60 */   public static final BannerPattern SQUARE_BOTTOM_LEFT = define("square_bottom_left");
/*  61 */   public static final BannerPattern STRIPE_BOTTOM = define("stripe_bottom");
/*  62 */   public static final BannerPattern CREEPER = define("creeper");
/*  63 */   public static final BannerPattern HALF_HORIZONTAL = define("half_horizontal");
/*  64 */   public static final BannerPattern STRIPE_MIDDLE = define("stripe_middle");
/*  65 */   public static final BannerPattern BASE = define("base");
/*  66 */   public static final BannerPattern DIAGONAL_UP_RIGHT = define("diagonal_up_right");
/*  67 */   public static final BannerPattern HALF_HORIZONTAL_BOTTOM = define("half_horizontal_bottom");
/*  68 */   public static final BannerPattern SMALL_STRIPES = define("small_stripes");
/*  69 */   public static final BannerPattern GRADIENT_UP = define("gradient_up");
/*  70 */   public static final BannerPattern CIRCLE = define("circle");
/*  71 */   public static final BannerPattern STRIPE_DOWNLEFT = define("stripe_downleft");
/*  72 */   public static final BannerPattern RHOMBUS = define("rhombus");
/*  73 */   public static final BannerPattern TRIANGLES_BOTTOM = define("triangles_bottom");
/*  74 */   public static final BannerPattern STRIPE_CENTER = define("stripe_center");
/*  75 */   public static final BannerPattern SQUARE_BOTTOM_RIGHT = define("square_bottom_right");
/*  76 */   public static final BannerPattern DIAGONAL_RIGHT = define("diagonal_right");
/*  77 */   public static final BannerPattern MOJANG = define("mojang");
/*  78 */   public static final BannerPattern STRIPE_LEFT = define("stripe_left");
/*  79 */   public static final BannerPattern SQUARE_TOP_LEFT = define("square_top_left");
/*  80 */   public static final BannerPattern TRIANGLE_BOTTOM = define("triangle_bottom");
/*  81 */   public static final BannerPattern SKULL = define("skull");
/*  82 */   public static final BannerPattern SQUARE_TOP_RIGHT = define("square_top_right");
/*  83 */   public static final BannerPattern GLOBE = define("globe");
/*  84 */   public static final BannerPattern STRIPE_TOP = define("stripe_top");
/*  85 */   public static final BannerPattern CROSS = define("cross");
/*  86 */   public static final BannerPattern BRICKS = define("bricks");
/*  87 */   public static final BannerPattern HALF_VERTICAL = define("half_vertical");
/*  88 */   public static final BannerPattern STRIPE_DOWNRIGHT = define("stripe_downright");
/*  89 */   public static final BannerPattern TRIANGLES_TOP = define("triangles_top");
/*  90 */   public static final BannerPattern STRIPE_RIGHT = define("stripe_right");
/*  91 */   public static final BannerPattern DIAGONAL_UP_LEFT = define("diagonal_up_left");
/*  92 */   public static final BannerPattern HALF_VERTICAL_RIGHT = define("half_vertical_right");
/*  93 */   public static final BannerPattern TRIANGLE_TOP = define("triangle_top");
/*  94 */   public static final BannerPattern FLOWER = define("flower");
/*  95 */   public static final BannerPattern STRAIGHT_CROSS = define("straight_cross");
/*  96 */   public static final BannerPattern GRADIENT = define("gradient");
/*  97 */   public static final BannerPattern CURLY_BORDER = define("curly_border");
/*  98 */   public static final BannerPattern BORDER = define("border");
/*  99 */   public static final BannerPattern PIGLIN = define("piglin");
/* 100 */   public static final BannerPattern DIAGONAL_LEFT = define("diagonal_left");
/*     */ 
/*     */   
/* 103 */   public static final BannerPattern FLOW = define("flow");
/* 104 */   public static final BannerPattern GUSTER = define("guster");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<BannerPattern> values() {
/* 112 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 116 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\banner\BannerPatterns.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
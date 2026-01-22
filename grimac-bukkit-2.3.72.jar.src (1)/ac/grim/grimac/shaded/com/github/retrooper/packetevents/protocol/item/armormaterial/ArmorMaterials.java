/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.armormaterial;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArmorMaterials
/*     */ {
/*  34 */   private static final Map<String, String> DFU = new HashMap<>();
/*     */   
/*     */   static {
/*  37 */     DFU.put("turtle", "minecraft:turtle_scute");
/*  38 */     DFU.put("minecraft:turtle", "minecraft:turtle_scute");
/*  39 */     DFU.put("armadillo", "minecraft:armadillo_scute");
/*  40 */     DFU.put("minecraft:armadillo", "minecraft:armadillo_scute");
/*     */   }
/*     */   
/*  43 */   private static final VersionedRegistry<ArmorMaterial> REGISTRY = new VersionedRegistry("equipment_asset");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ArmorMaterial define(String name) {
/*  49 */     return (ArmorMaterial)REGISTRY.define(name, StaticArmorMaterial::new);
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<ArmorMaterial> getRegistry() {
/*  53 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static ArmorMaterial getByName(String name) {
/*  57 */     return (ArmorMaterial)REGISTRY.getByName(DFU.getOrDefault(name, name));
/*     */   }
/*     */   
/*     */   public static ArmorMaterial getById(ClientVersion version, int id) {
/*  61 */     return (ArmorMaterial)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  64 */   public static final ArmorMaterial LEATHER = define("leather");
/*  65 */   public static final ArmorMaterial CHAINMAIL = define("chainmail");
/*  66 */   public static final ArmorMaterial IRON = define("iron");
/*  67 */   public static final ArmorMaterial GOLD = define("gold");
/*  68 */   public static final ArmorMaterial DIAMOND = define("diamond");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final ArmorMaterial TURTLE_SCUTE = define("turtle_scute");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  78 */   public static final ArmorMaterial TURTLE = TURTLE_SCUTE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   public static final ArmorMaterial NETHERITE = define("netherite");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static final ArmorMaterial ARMADILLO_SCUTE = define("armadillo_scute");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  93 */   public static final ArmorMaterial ARMADILLO = ARMADILLO_SCUTE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   public static final ArmorMaterial ELYTRA = define("elytra");
/*     */ 
/*     */ 
/*     */   
/* 102 */   public static final ArmorMaterial WHITE_CARPET = define("white_carpet");
/*     */ 
/*     */ 
/*     */   
/* 106 */   public static final ArmorMaterial ORANGE_CARPET = define("orange_carpet");
/*     */ 
/*     */ 
/*     */   
/* 110 */   public static final ArmorMaterial MAGENTA_CARPET = define("magenta_carpet");
/*     */ 
/*     */ 
/*     */   
/* 114 */   public static final ArmorMaterial LIGHT_BLUE_CARPET = define("light_blue_carpet");
/*     */ 
/*     */ 
/*     */   
/* 118 */   public static final ArmorMaterial YELLOW_CARPET = define("yellow_carpet");
/*     */ 
/*     */ 
/*     */   
/* 122 */   public static final ArmorMaterial LIME_CARPET = define("lime_carpet");
/*     */ 
/*     */ 
/*     */   
/* 126 */   public static final ArmorMaterial PINK_CARPET = define("pink_carpet");
/*     */ 
/*     */ 
/*     */   
/* 130 */   public static final ArmorMaterial GRAY_CARPET = define("gray_carpet");
/*     */ 
/*     */ 
/*     */   
/* 134 */   public static final ArmorMaterial LIGHT_GRAY_CARPET = define("light_gray_carpet");
/*     */ 
/*     */ 
/*     */   
/* 138 */   public static final ArmorMaterial CYAN_CARPET = define("cyan_carpet");
/*     */ 
/*     */ 
/*     */   
/* 142 */   public static final ArmorMaterial PURPLE_CARPET = define("purple_carpet");
/*     */ 
/*     */ 
/*     */   
/* 146 */   public static final ArmorMaterial BLUE_CARPET = define("blue_carpet");
/*     */ 
/*     */ 
/*     */   
/* 150 */   public static final ArmorMaterial BROWN_CARPET = define("brown_carpet");
/*     */ 
/*     */ 
/*     */   
/* 154 */   public static final ArmorMaterial GREEN_CARPET = define("green_carpet");
/*     */ 
/*     */ 
/*     */   
/* 158 */   public static final ArmorMaterial RED_CARPET = define("red_carpet");
/*     */ 
/*     */ 
/*     */   
/* 162 */   public static final ArmorMaterial BLACK_CARPET = define("black_carpet");
/*     */ 
/*     */ 
/*     */   
/* 166 */   public static final ArmorMaterial TRADER_LLAMA = define("trader_llama");
/*     */   
/*     */   static {
/* 169 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\armormaterial\ArmorMaterials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
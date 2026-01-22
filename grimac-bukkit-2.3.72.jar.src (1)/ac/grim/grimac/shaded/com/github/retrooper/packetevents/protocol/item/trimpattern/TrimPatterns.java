/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimpattern;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TrimPatterns
/*    */ {
/* 32 */   private static final VersionedRegistry<TrimPattern> REGISTRY = new VersionedRegistry("trim_pattern");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static TrimPattern define(String name) {
/* 39 */     ResourceLocation assetId = ResourceLocation.minecraft(name);
/* 40 */     ItemType templateItem = ItemTypes.getByName(assetId + "_armor_trim_smithing_template");
/* 41 */     TranslatableComponent translatableComponent = Component.translatable("trim_pattern.minecraft." + name);
/* 42 */     boolean decal = false;
/* 43 */     return define(name, assetId, templateItem, (Component)translatableComponent, decal);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static TrimPattern define(String name, ResourceLocation assetId, ItemType templateItem, Component description, boolean decal) {
/* 51 */     return (TrimPattern)REGISTRY.define(name, data -> new StaticTrimPattern(data, assetId, templateItem, description, decal));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<TrimPattern> getRegistry() {
/* 56 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   public static TrimPattern getByName(String name) {
/* 60 */     return (TrimPattern)REGISTRY.getByName(name);
/*    */   }
/*    */   
/*    */   public static TrimPattern getById(ClientVersion version, int id) {
/* 64 */     return (TrimPattern)REGISTRY.getById(version, id);
/*    */   }
/*    */ 
/*    */   
/* 68 */   public static final TrimPattern COAST = define("coast");
/* 69 */   public static final TrimPattern DUNE = define("dune");
/* 70 */   public static final TrimPattern EYE = define("eye");
/* 71 */   public static final TrimPattern RIB = define("rib");
/* 72 */   public static final TrimPattern SENTRY = define("sentry");
/* 73 */   public static final TrimPattern SNOUT = define("snout");
/* 74 */   public static final TrimPattern SPIRE = define("spire");
/* 75 */   public static final TrimPattern TIDE = define("tide");
/* 76 */   public static final TrimPattern VEX = define("vex");
/* 77 */   public static final TrimPattern WARD = define("ward");
/* 78 */   public static final TrimPattern WILD = define("wild");
/*    */ 
/*    */   
/* 81 */   public static final TrimPattern RAISER = define("raiser");
/* 82 */   public static final TrimPattern HOST = define("host");
/* 83 */   public static final TrimPattern SILENCE = define("silence");
/* 84 */   public static final TrimPattern SHAPER = define("shaper");
/* 85 */   public static final TrimPattern WAYFINDER = define("wayfinder");
/*    */ 
/*    */   
/* 88 */   public static final TrimPattern BOLT = define("bolt");
/* 89 */   public static final TrimPattern FLOW = define("flow");
/*    */   
/*    */   static {
/* 92 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\trimpattern\TrimPatterns.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
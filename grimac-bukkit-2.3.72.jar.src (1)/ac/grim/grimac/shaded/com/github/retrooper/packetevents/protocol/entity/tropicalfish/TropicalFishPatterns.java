/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.tropicalfish;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.Collection;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TropicalFishPatterns
/*    */ {
/* 30 */   private static final VersionedRegistry<TropicalFishPattern> REGISTRY = new VersionedRegistry("tropical_fish_pattern");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static TropicalFishPattern define(String name, TropicalFishPattern.Base base) {
/* 38 */     return (TropicalFishPattern)REGISTRY.define(name, typesBuilderData -> new StaticTropicalFishPattern(typesBuilderData, base));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<TropicalFishPattern> getRegistry() {
/* 43 */     return REGISTRY;
/*    */   }
/*    */   
/* 46 */   public static final TropicalFishPattern KOB = define("kob", TropicalFishPattern.Base.SMALL);
/* 47 */   public static final TropicalFishPattern SUNSTREAK = define("sunstreak", TropicalFishPattern.Base.SMALL);
/* 48 */   public static final TropicalFishPattern SNOOPER = define("snooper", TropicalFishPattern.Base.SMALL);
/* 49 */   public static final TropicalFishPattern DASHER = define("dasher", TropicalFishPattern.Base.SMALL);
/* 50 */   public static final TropicalFishPattern BRINELY = define("brinely", TropicalFishPattern.Base.SMALL);
/* 51 */   public static final TropicalFishPattern SPOTTY = define("spotty", TropicalFishPattern.Base.SMALL);
/* 52 */   public static final TropicalFishPattern FLOPPER = define("flopper", TropicalFishPattern.Base.LARGE);
/* 53 */   public static final TropicalFishPattern STRIPEY = define("stripey", TropicalFishPattern.Base.LARGE);
/* 54 */   public static final TropicalFishPattern GLITTER = define("glitter", TropicalFishPattern.Base.LARGE);
/* 55 */   public static final TropicalFishPattern BLOCKFISH = define("blockfish", TropicalFishPattern.Base.LARGE);
/* 56 */   public static final TropicalFishPattern BETTY = define("betty", TropicalFishPattern.Base.LARGE);
/* 57 */   public static final TropicalFishPattern CLAYFISH = define("clayfish", TropicalFishPattern.Base.LARGE);
/*    */   
/*    */   public static Collection<TropicalFishPattern> values() {
/* 60 */     return REGISTRY.getEntries();
/*    */   }
/*    */   
/*    */   static {
/* 64 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\tropicalfish\TropicalFishPatterns.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
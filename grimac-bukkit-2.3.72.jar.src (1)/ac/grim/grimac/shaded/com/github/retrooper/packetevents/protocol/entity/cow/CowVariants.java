/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ public final class CowVariants
/*    */ {
/* 28 */   private static final VersionedRegistry<CowVariant> REGISTRY = new VersionedRegistry("cow_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static CowVariant define(String name, CowVariant.ModelType modelType, String texture) {
/* 36 */     ResourceLocation assetId = new ResourceLocation("entity/cow/" + texture);
/* 37 */     return (CowVariant)REGISTRY.define(name, data -> new StaticCowVariant(data, modelType, assetId));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<CowVariant> getRegistry() {
/* 42 */     return REGISTRY;
/*    */   }
/*    */   
/* 45 */   public static final CowVariant COLD = define("cold", CowVariant.ModelType.COLD, "cold_cow");
/* 46 */   public static final CowVariant TEMPERATE = define("temperate", CowVariant.ModelType.NORMAL, "temperate_cow");
/* 47 */   public static final CowVariant WARM = define("warm", CowVariant.ModelType.WARM, "warm_cow");
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\cow\CowVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
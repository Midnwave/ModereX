/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pig;
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
/*    */ public final class PigVariants
/*    */ {
/* 28 */   private static final VersionedRegistry<PigVariant> REGISTRY = new VersionedRegistry("pig_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static PigVariant define(String name, PigVariant.ModelType modelType, String texture) {
/* 36 */     ResourceLocation assetId = new ResourceLocation("entity/pig/" + texture);
/* 37 */     return (PigVariant)REGISTRY.define(name, data -> new StaticPigVariant(data, modelType, assetId));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<PigVariant> getRegistry() {
/* 42 */     return REGISTRY;
/*    */   }
/*    */   
/* 45 */   public static final PigVariant COLD = define("cold", PigVariant.ModelType.COLD, "cold_pig");
/* 46 */   public static final PigVariant TEMPERATE = define("temperate", PigVariant.ModelType.NORMAL, "temperate_pig");
/* 47 */   public static final PigVariant WARM = define("warm", PigVariant.ModelType.NORMAL, "warm_pig");
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\pig\PigVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
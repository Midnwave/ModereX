/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken;
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
/*    */ public final class ChickenVariants
/*    */ {
/* 28 */   private static final VersionedRegistry<ChickenVariant> REGISTRY = new VersionedRegistry("chicken_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static ChickenVariant define(String name, ChickenVariant.ModelType modelType, String texture) {
/* 36 */     ResourceLocation assetId = new ResourceLocation("entity/chicken/" + texture);
/* 37 */     return (ChickenVariant)REGISTRY.define(name, data -> new StaticChickenVariant(data, modelType, assetId));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<ChickenVariant> getRegistry() {
/* 42 */     return REGISTRY;
/*    */   }
/*    */   
/* 45 */   public static final ChickenVariant COLD = define("cold", ChickenVariant.ModelType.COLD, "cold_chicken");
/* 46 */   public static final ChickenVariant TEMPERATE = define("temperate", ChickenVariant.ModelType.NORMAL, "temperate_chicken");
/* 47 */   public static final ChickenVariant WARM = define("warm", ChickenVariant.ModelType.NORMAL, "warm_chicken");
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\chicken\ChickenVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.frog;
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
/*    */ public final class FrogVariants
/*    */ {
/* 27 */   private static final VersionedRegistry<FrogVariant> REGISTRY = new VersionedRegistry("frog_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static FrogVariant define(String name, String texture) {
/* 35 */     ResourceLocation assetId = new ResourceLocation("entity/frog/" + texture);
/* 36 */     return (FrogVariant)REGISTRY.define(name, data -> new StaticFrogVariant(data, assetId));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<FrogVariant> getRegistry() {
/* 41 */     return REGISTRY;
/*    */   }
/*    */   
/* 44 */   public static final FrogVariant COLD = define("cold", "cold_frog");
/* 45 */   public static final FrogVariant TEMPERATE = define("temperate", "temperate_frog");
/* 46 */   public static final FrogVariant WARM = define("warm", "warm_frog");
/*    */   
/*    */   static {
/* 49 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\frog\FrogVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
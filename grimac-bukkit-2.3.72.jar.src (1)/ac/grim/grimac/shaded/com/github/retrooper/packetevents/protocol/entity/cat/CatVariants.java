/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat;
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
/*    */ public final class CatVariants
/*    */ {
/* 27 */   private static final VersionedRegistry<CatVariant> REGISTRY = new VersionedRegistry("cat_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static CatVariant define(String name) {
/* 35 */     ResourceLocation assetId = new ResourceLocation("entity/cat/" + name);
/* 36 */     return (CatVariant)REGISTRY.define(name, data -> new StaticCatVariant(data, assetId));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<CatVariant> getRegistry() {
/* 41 */     return REGISTRY;
/*    */   }
/*    */   
/* 44 */   public static final CatVariant ALL_BLACK = define("all_black");
/* 45 */   public static final CatVariant BLACK = define("black");
/* 46 */   public static final CatVariant BRITISH_SHORTHAIR = define("british_shorthair");
/* 47 */   public static final CatVariant CALICO = define("calico");
/* 48 */   public static final CatVariant JELLIE = define("jellie");
/* 49 */   public static final CatVariant PERSIAN = define("persian");
/* 50 */   public static final CatVariant RAGDOLL = define("ragdoll");
/* 51 */   public static final CatVariant RED = define("red");
/* 52 */   public static final CatVariant SIAMESE = define("siamese");
/* 53 */   public static final CatVariant TABBY = define("tabby");
/* 54 */   public static final CatVariant WHITE = define("white");
/*    */   
/*    */   static {
/* 57 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\cat\CatVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
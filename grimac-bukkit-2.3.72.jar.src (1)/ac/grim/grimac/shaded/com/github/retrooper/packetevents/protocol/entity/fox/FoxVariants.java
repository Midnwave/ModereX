/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.fox;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.function.Function;
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
/*    */ public final class FoxVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<FoxVariant> REGISTRY = new VersionedRegistry("fox_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static FoxVariant define(String name) {
/* 34 */     return (FoxVariant)REGISTRY.define(name, StaticFoxVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<FoxVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final FoxVariant RED = define("red");
/* 42 */   public static final FoxVariant SNOW = define("snow");
/*    */   
/*    */   static {
/* 45 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\fox\FoxVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
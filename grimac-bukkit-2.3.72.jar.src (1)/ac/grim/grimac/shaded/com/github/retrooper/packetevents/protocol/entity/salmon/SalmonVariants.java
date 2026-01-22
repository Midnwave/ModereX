/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.salmon;
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
/*    */ public final class SalmonVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<SalmonVariant> REGISTRY = new VersionedRegistry("salmon_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static SalmonVariant define(String name) {
/* 34 */     return (SalmonVariant)REGISTRY.define(name, StaticSalmonVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<SalmonVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final SalmonVariant SMALL = define("small");
/* 42 */   public static final SalmonVariant MEDIUM = define("medium");
/* 43 */   public static final SalmonVariant LARGE = define("large");
/*    */   
/*    */   static {
/* 46 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\salmon\SalmonVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
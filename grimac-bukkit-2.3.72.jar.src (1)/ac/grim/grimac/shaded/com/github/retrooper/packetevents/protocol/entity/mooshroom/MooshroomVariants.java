/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.mooshroom;
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
/*    */ public final class MooshroomVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<MooshroomVariant> REGISTRY = new VersionedRegistry("mooshroom_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static MooshroomVariant define(String name) {
/* 34 */     return (MooshroomVariant)REGISTRY.define(name, StaticMooshroomVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<MooshroomVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final MooshroomVariant RED = define("red");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 46 */   public static final MooshroomVariant BROWN = define("brown");
/*    */   
/*    */   static {
/* 49 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\mooshroom\MooshroomVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
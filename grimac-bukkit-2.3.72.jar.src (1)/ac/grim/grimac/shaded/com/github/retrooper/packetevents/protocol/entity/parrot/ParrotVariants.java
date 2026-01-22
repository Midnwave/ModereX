/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.parrot;
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
/*    */ public final class ParrotVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<ParrotVariant> REGISTRY = new VersionedRegistry("parrot_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static ParrotVariant define(String name) {
/* 34 */     return (ParrotVariant)REGISTRY.define(name, StaticParrotVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<ParrotVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final ParrotVariant RED_BLUE = define("red_blue");
/* 42 */   public static final ParrotVariant BLUE = define("blue");
/* 43 */   public static final ParrotVariant GREEN = define("green");
/* 44 */   public static final ParrotVariant YELLOW_BLUE = define("yellow_blue");
/* 45 */   public static final ParrotVariant GRAY = define("gray");
/*    */   
/*    */   static {
/* 48 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\parrot\ParrotVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.rabbit;
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
/*    */ public final class RabbitVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<RabbitVariant> REGISTRY = new VersionedRegistry("rabbit_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static RabbitVariant define(String name) {
/* 34 */     return (RabbitVariant)REGISTRY.define(name, StaticRabbitVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<RabbitVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final RabbitVariant BROWN = define("brown");
/* 42 */   public static final RabbitVariant WHITE = define("white");
/* 43 */   public static final RabbitVariant BLACK = define("black");
/* 44 */   public static final RabbitVariant WHITE_SPLOTCHED = define("white_splotched");
/* 45 */   public static final RabbitVariant GOLD = define("gold");
/* 46 */   public static final RabbitVariant SALT = define("salt");
/* 47 */   public static final RabbitVariant EVIL = define("evil");
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\rabbit\RabbitVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.horse;
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
/*    */ public final class HorseVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<HorseVariant> REGISTRY = new VersionedRegistry("horse_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static HorseVariant define(String name) {
/* 34 */     return (HorseVariant)REGISTRY.define(name, StaticHorseVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<HorseVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final HorseVariant WHITE = define("white");
/* 42 */   public static final HorseVariant CREAMY = define("creamy");
/* 43 */   public static final HorseVariant CHESTNUT = define("chestnut");
/* 44 */   public static final HorseVariant BROWN = define("brown");
/* 45 */   public static final HorseVariant BLACK = define("black");
/* 46 */   public static final HorseVariant GRAY = define("gray");
/* 47 */   public static final HorseVariant DARK_BROWN = define("dark_brown");
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\horse\HorseVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
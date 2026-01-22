/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.axolotl;
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
/*    */ public final class AxolotlVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<AxolotlVariant> REGISTRY = new VersionedRegistry("axolotl_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static AxolotlVariant define(String name) {
/* 34 */     return (AxolotlVariant)REGISTRY.define(name, StaticAxolotlVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<AxolotlVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final AxolotlVariant LUCY = define("lucy");
/* 42 */   public static final AxolotlVariant WILD = define("wild");
/* 43 */   public static final AxolotlVariant GOLD = define("gold");
/* 44 */   public static final AxolotlVariant CYAN = define("cyan");
/* 45 */   public static final AxolotlVariant BLUE = define("blue");
/*    */   
/*    */   static {
/* 48 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\axolotl\AxolotlVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
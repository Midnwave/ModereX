/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.llama;
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
/*    */ public final class LlamaVariants
/*    */ {
/* 26 */   private static final VersionedRegistry<LlamaVariant> REGISTRY = new VersionedRegistry("llama_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static LlamaVariant define(String name) {
/* 34 */     return (LlamaVariant)REGISTRY.define(name, StaticLlamaVariant::new);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<LlamaVariant> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final LlamaVariant CREAMY = define("creamy");
/* 42 */   public static final LlamaVariant WHITE = define("white");
/* 43 */   public static final LlamaVariant BROWN = define("brown");
/* 44 */   public static final LlamaVariant GRAY = define("gray");
/*    */   
/*    */   static {
/* 47 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\llama\LlamaVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
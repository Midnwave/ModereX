/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;
/*    */ 
/*    */ import org.apiguardian.api.API;
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
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ public interface BrigadierMappings<C, S>
/*    */ {
/*    */   static <C, S> BrigadierMappings<C, S> create() {
/* 42 */     return new BrigadierMappingsImpl<>();
/*    */   }
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
/*    */   <T, K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, T>> BrigadierMapping<C, K, S> mapping(Class<K> paramClass);
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
/*    */   default <T, K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, T>> void registerMapping(Class<K> parserType, BrigadierMapping<?, K, S> mapping) {
/* 67 */     registerMappingUnsafe(parserType, mapping);
/*    */   }
/*    */   
/*    */   <K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, ?>> void registerMappingUnsafe(Class<K> paramClass, BrigadierMapping<?, ?, S> paramBrigadierMapping);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\BrigadierMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
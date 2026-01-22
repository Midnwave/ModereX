/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ final class BrigadierMappingsImpl<C, S>
/*    */   implements BrigadierMappings<C, S>
/*    */ {
/* 35 */   private final Map<Class<?>, BrigadierMapping<?, ?, S>> mappers = new HashMap<>();
/*    */ 
/*    */   
/*    */   public <T, K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, T>> BrigadierMapping<C, K, S> mapping(Class<K> parserType) {
/* 39 */     BrigadierMapping<?, ?, S> mapper = this.mappers.get(parserType);
/* 40 */     if (mapper == null) {
/* 41 */       return null;
/*    */     }
/* 43 */     return (BrigadierMapping)mapper;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, ?>> void registerMappingUnsafe(Class<K> parserType, BrigadierMapping<?, ?, S> mapping) {
/* 51 */     this.mappers.put(parserType, mapping);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\BrigadierMappingsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
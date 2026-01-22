/*    */ package ac.grim.grimac.shaded.configuralize.mapping;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.weak.Dynamic;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MappingFunction<T>
/*    */ {
/*    */   private final String key;
/*    */   private final Function<Dynamic, T> function;
/*    */   
/*    */   public MappingFunction(String key, Function<Dynamic, T> function) {
/* 17 */     this.key = key;
/* 18 */     this.function = function;
/*    */   }
/*    */   
/*    */   public String getKey() {
/* 22 */     return this.key;
/*    */   }
/*    */   public Function<Dynamic, T> getFunction() {
/* 25 */     return this.function;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\mapping\MappingFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
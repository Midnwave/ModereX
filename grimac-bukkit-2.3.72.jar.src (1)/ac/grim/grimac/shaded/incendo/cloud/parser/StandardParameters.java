/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public final class StandardParameters
/*    */ {
/* 40 */   public static final ParserParameter<Number> RANGE_MIN = create("min", TypeToken.get(Number.class));
/*    */ 
/*    */ 
/*    */   
/* 44 */   public static final ParserParameter<Number> RANGE_MAX = create("max", TypeToken.get(Number.class));
/*    */ 
/*    */ 
/*    */   
/* 48 */   public static final ParserParameter<Boolean> GREEDY = create("greedy", TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/* 55 */   public static final ParserParameter<Boolean> FLAG_YIELDING = create("flag_yielding", 
/*    */       
/* 57 */       TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/* 64 */   public static final ParserParameter<Boolean> QUOTED = create("quoted", TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/* 70 */   public static final ParserParameter<Boolean> LIBERAL = create("liberal", TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static <T> ParserParameter<T> create(String key, TypeToken<T> expectedType) {
/* 79 */     return new ParserParameter<>(key, expectedType);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\StandardParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
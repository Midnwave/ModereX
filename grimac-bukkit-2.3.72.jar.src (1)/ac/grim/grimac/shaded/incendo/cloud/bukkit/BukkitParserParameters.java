/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserParameter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE, since = "1.7.0")
/*    */ public final class BukkitParserParameters
/*    */ {
/*    */   @API(status = API.Status.STABLE, since = "1.8.0")
/* 51 */   public static final ParserParameter<Boolean> ALLOW_EMPTY_SELECTOR_RESULT = create("allow_empty_selector_result", TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 60 */   public static final ParserParameter<Boolean> REQUIRE_EXPLICIT_NAMESPACE = create("require_explicit_namespace", TypeToken.get(Boolean.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 69 */   public static final ParserParameter<String> DEFAULT_NAMESPACE = create("default_namespace", TypeToken.get(String.class));
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static <T> ParserParameter<T> create(String key, TypeToken<T> expectedType) {
/* 75 */     return new ParserParameter(key, expectedType);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitParserParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface Suggestion
/*    */ {
/*    */   static Suggestion suggestion(String suggestion) {
/* 39 */     return new SimpleSuggestion(suggestion);
/*    */   }
/*    */   
/*    */   String suggestion();
/*    */   
/*    */   Suggestion withSuggestion(String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\Suggestion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
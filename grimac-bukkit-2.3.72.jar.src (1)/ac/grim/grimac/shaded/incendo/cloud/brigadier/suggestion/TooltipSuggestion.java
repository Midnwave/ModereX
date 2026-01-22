/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*    */ import com.mojang.brigadier.Message;
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
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
/*    */ 
/*    */ @API(status = API.Status.STABLE, since = "2.0.0")
/*    */ @Immutable
/*    */ public interface TooltipSuggestion
/*    */   extends Suggestion
/*    */ {
/*    */   static TooltipSuggestion suggestion(String suggestion, Message tooltip) {
/* 55 */     return TooltipSuggestionImpl.of(suggestion, tooltip);
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
/*    */   static TooltipSuggestion tooltipSuggestion(Suggestion suggestion) {
/* 67 */     if (suggestion instanceof TooltipSuggestion) {
/* 68 */       return (TooltipSuggestion)suggestion;
/*    */     }
/* 70 */     return suggestion(suggestion.suggestion(), null);
/*    */   }
/*    */   
/*    */   String suggestion();
/*    */   
/*    */   Message tooltip();
/*    */   
/*    */   TooltipSuggestion withSuggestion(String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\suggestion\TooltipSuggestion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
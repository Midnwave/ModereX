/*    */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ @Immutable
/*    */ public interface MultipleCommandResult<C>
/*    */   extends HelpQueryResult<C>
/*    */ {
/*    */   static <C> MultipleCommandResult<C> of(HelpQuery<C> query, String longestPath, List<String> childSuggestions) {
/* 63 */     return MultipleCommandResultImpl.of(query, longestPath, childSuggestions);
/*    */   }
/*    */   
/*    */   HelpQuery<C> query();
/*    */   
/*    */   String longestPath();
/*    */   
/*    */   List<String> childSuggestions();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\MultipleCommandResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
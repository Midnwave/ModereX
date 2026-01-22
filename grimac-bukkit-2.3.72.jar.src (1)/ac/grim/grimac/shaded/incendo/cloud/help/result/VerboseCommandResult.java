/*    */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ @Immutable
/*    */ public interface VerboseCommandResult<C>
/*    */   extends HelpQueryResult<C>
/*    */ {
/*    */   static <C> VerboseCommandResult<C> of(HelpQuery<C> query, CommandEntry<C> entry) {
/* 56 */     return VerboseCommandResultImpl.of(query, entry);
/*    */   }
/*    */   
/*    */   HelpQuery<C> query();
/*    */   
/*    */   CommandEntry<C> entry();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\VerboseCommandResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
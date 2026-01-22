/*    */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.result.HelpQueryResult;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.result.IndexCommandResult;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface HelpHandler<C>
/*    */ {
/*    */   HelpQueryResult<C> query(HelpQuery<C> paramHelpQuery);
/*    */   
/*    */   default IndexCommandResult<C> queryRootIndex(C sender) {
/* 52 */     return (IndexCommandResult<C>)query(HelpQuery.of(sender, ""));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\HelpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
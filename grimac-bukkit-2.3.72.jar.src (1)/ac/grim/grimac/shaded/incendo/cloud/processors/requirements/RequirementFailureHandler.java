/*    */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE, since = "1.0.0")
/*    */ public interface RequirementFailureHandler<C, R extends Requirement<C, R>>
/*    */ {
/*    */   static <C, R extends Requirement<C, R>> RequirementFailureHandler<C, R> noOp() {
/* 49 */     return (requirement, context) -> {
/*    */       
/*    */       };
/*    */   }
/*    */   
/*    */   void handleFailure(CommandContext<C> paramCommandContext, R paramR);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\RequirementFailureHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
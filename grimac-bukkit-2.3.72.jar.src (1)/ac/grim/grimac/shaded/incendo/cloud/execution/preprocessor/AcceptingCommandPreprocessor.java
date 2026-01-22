/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ public final class AcceptingCommandPreprocessor<C>
/*    */   implements CommandPreprocessor<C>
/*    */ {
/*    */   public static final String PROCESSED_INDICATOR_KEY = "__COMMAND_PRE_PROCESSED__";
/*    */   
/*    */   public void accept(CommandPreprocessingContext<C> context) {
/* 45 */     context.commandContext().store("__COMMAND_PRE_PROCESSED__", "true");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\preprocessor\AcceptingCommandPreprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
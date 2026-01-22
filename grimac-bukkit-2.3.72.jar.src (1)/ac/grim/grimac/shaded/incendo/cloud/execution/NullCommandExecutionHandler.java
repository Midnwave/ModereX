/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ final class NullCommandExecutionHandler<C>
/*    */   implements CommandExecutionHandler<C>
/*    */ {
/* 38 */   static final CommandExecutionHandler<?> INSTANCE = new NullCommandExecutionHandler();
/*    */   
/*    */   public void execute(CommandContext<C> commandContext) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\NullCommandExecutionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
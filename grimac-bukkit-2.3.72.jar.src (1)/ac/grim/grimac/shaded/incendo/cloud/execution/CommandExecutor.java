/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.function.Consumer;
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
/*    */ 
/*    */ 
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
/*    */ public interface CommandExecutor<C>
/*    */ {
/*    */   default CompletableFuture<CommandResult<C>> executeCommand(C commandSender, String input) {
/* 63 */     return executeCommand(commandSender, input, context -> {
/*    */         
/*    */         });
/*    */   }
/*    */   
/*    */   CompletableFuture<CommandResult<C>> executeCommand(C paramC, String paramString, Consumer<CommandContext<C>> paramConsumer);
/*    */   
/*    */   ExecutionCoordinator<C> executionCoordinator();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\CommandExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
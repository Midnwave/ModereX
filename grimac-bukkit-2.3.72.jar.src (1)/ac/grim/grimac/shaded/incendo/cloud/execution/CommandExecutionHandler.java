/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @FunctionalInterface
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface CommandExecutionHandler<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   static <C> CommandExecutionHandler<C> noOpCommandExecutionHandler() {
/*  53 */     return (CommandExecutionHandler)NullCommandExecutionHandler.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   static <C> CommandExecutionHandler<C> delegatingExecutionHandler(List<CommandExecutionHandler<C>> handlers) {
/*  71 */     return new MulticastDelegateFutureCommandExecutionHandler<>(handlers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void execute(CommandContext<C> paramCommandContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default CompletableFuture<Void> executeFuture(CommandContext<C> commandContext) {
/*  89 */     CompletableFuture<Void> future = new CompletableFuture<>();
/*     */     try {
/*  91 */       execute(commandContext);
/*     */       
/*  93 */       future.complete(null);
/*  94 */     } catch (Throwable throwable) {
/*  95 */       future.completeExceptionally(throwable);
/*     */     } 
/*  97 */     return future;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @FunctionalInterface
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface FutureCommandExecutionHandler<C>
/*     */     extends CommandExecutionHandler<C>
/*     */   {
/*     */     default void execute(CommandContext<C> commandContext) {
/* 115 */       throw new UnsupportedOperationException("execute should not be called on FutureCommandExecutionHandlers, call executeFuture instead.");
/*     */     }
/*     */     
/*     */     CompletableFuture<Void> executeFuture(CommandContext<C> param1CommandContext);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\CommandExecutionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
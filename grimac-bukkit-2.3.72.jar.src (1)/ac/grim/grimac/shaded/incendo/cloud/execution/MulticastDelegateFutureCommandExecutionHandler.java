/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.CompletionStage;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ final class MulticastDelegateFutureCommandExecutionHandler<C>
/*    */   implements CommandExecutionHandler.FutureCommandExecutionHandler<C>
/*    */ {
/*    */   private final List<CommandExecutionHandler<C>> handlers;
/*    */   
/*    */   MulticastDelegateFutureCommandExecutionHandler(List<CommandExecutionHandler<C>> handlers) {
/* 51 */     List<CommandExecutionHandler<C>> unwrappedHandlers = new ArrayList<>();
/* 52 */     for (CommandExecutionHandler<C> handler : handlers) {
/* 53 */       if (handler instanceof MulticastDelegateFutureCommandExecutionHandler) {
/* 54 */         unwrappedHandlers.addAll(((MulticastDelegateFutureCommandExecutionHandler)handler).handlers); continue;
/*    */       } 
/* 56 */       unwrappedHandlers.add(handler);
/*    */     } 
/*    */     
/* 59 */     this.handlers = Collections.unmodifiableList(unwrappedHandlers);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<Void> executeFuture(CommandContext<C> commandContext) {
/* 66 */     CompletableFuture<Void> composedHandler = null;
/*    */     
/* 68 */     if (this.handlers.isEmpty()) {
/* 69 */       composedHandler = CompletableFuture.completedFuture(null);
/*    */     } else {
/* 71 */       for (CommandExecutionHandler<C> handler : this.handlers) {
/* 72 */         if (composedHandler == null) {
/* 73 */           composedHandler = handler.executeFuture(commandContext); continue;
/*    */         } 
/* 75 */         composedHandler = composedHandler.thenCompose(ignore -> handler.executeFuture(commandContext));
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 82 */     return composedHandler;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\MulticastDelegateFutureCommandExecutionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
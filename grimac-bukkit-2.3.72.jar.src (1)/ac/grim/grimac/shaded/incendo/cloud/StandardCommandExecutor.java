/*     */ package ac.grim.grimac.shaded.incendo.cloud;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContextFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionController;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.CommandExecutor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.CommandResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.CompletableFutures;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionException;
/*     */ import java.util.function.Consumer;
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
/*     */ final class StandardCommandExecutor<C>
/*     */   implements CommandExecutor<C>
/*     */ {
/*     */   private final CommandManager<C> commandManager;
/*     */   private final ExecutionCoordinator<C> executionCoordinator;
/*     */   private final CommandContextFactory<C> commandContextFactory;
/*     */   
/*     */   StandardCommandExecutor(CommandManager<C> commandManager, ExecutionCoordinator<C> executionCoordinator, CommandContextFactory<C> commandContextFactory) {
/*  51 */     this.commandManager = commandManager;
/*  52 */     this.executionCoordinator = executionCoordinator;
/*  53 */     this.commandContextFactory = commandContextFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<CommandResult<C>> executeCommand(C commandSender, String input, Consumer<CommandContext<C>> contextConsumer) {
/*  62 */     CommandContext<C> context = this.commandContextFactory.create(false, commandSender);
/*  63 */     contextConsumer.accept(context);
/*  64 */     CommandInput commandInput = CommandInput.of(input);
/*  65 */     return executeCommand(context, commandInput).whenComplete((result, throwable) -> {
/*     */           if (throwable == null) {
/*     */             return;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/*     */             this.commandManager.exceptionController().handleException(context, ExceptionController.unwrapCompletionException(throwable));
/*  74 */           } catch (RuntimeException runtimeException) {
/*     */             throw runtimeException;
/*  76 */           } catch (Throwable e) {
/*     */             throw new CompletionException(e);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CompletableFuture<CommandResult<C>> executeCommand(CommandContext<C> context, CommandInput commandInput) {
/*  87 */     context.store("__raw_input__", commandInput.copy());
/*     */     try {
/*  89 */       if (this.commandManager.preprocessContext(context, commandInput) == State.ACCEPTED) {
/*  90 */         return executionCoordinator()
/*  91 */           .coordinateExecution(this.commandManager.commandTree(), context, commandInput);
/*     */       }
/*  93 */     } catch (Exception e) {
/*  94 */       return CompletableFutures.failedFuture(e);
/*     */     } 
/*     */     
/*  97 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public ExecutionCoordinator<C> executionCoordinator() {
/* 102 */     return this.executionCoordinator;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\StandardCommandExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
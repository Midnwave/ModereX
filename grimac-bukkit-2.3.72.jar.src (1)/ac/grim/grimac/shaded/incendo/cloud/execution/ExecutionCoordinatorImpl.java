/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandTree;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.CommandExecutionException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.CommandParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Semaphore;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ final class ExecutionCoordinatorImpl<C>
/*     */   implements ExecutionCoordinator<C>
/*     */ {
/*     */   private final Executor parsingExecutor;
/*     */   private final Executor suggestionsExecutor;
/*  47 */   static final Executor NON_SCHEDULING_EXECUTOR = new NonSchedulingExecutor();
/*     */   private final Executor defaultExecutionExecutor;
/*     */   private final Semaphore executionLock;
/*     */   
/*     */   private static final class NonSchedulingExecutor implements Executor {
/*     */     public void execute(Runnable command) {
/*  53 */       command.run();
/*     */     }
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
/*     */     private NonSchedulingExecutor() {}
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
/*     */   ExecutionCoordinatorImpl(Executor parsingExecutor, Executor suggestionsExecutor, Executor defaultExecutionExecutor, boolean syncExecution) {
/*  82 */     this.parsingExecutor = orRunNow(parsingExecutor);
/*  83 */     this.suggestionsExecutor = orRunNow(suggestionsExecutor);
/*  84 */     this.defaultExecutionExecutor = orRunNow(defaultExecutionExecutor);
/*  85 */     this.executionLock = syncExecution ? new Semaphore(1) : null;
/*     */   }
/*     */   
/*     */   private static Executor orRunNow(Executor e) {
/*  89 */     return (e == null) ? ExecutionCoordinator.nonSchedulingExecutor() : e;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<CommandResult<C>> coordinateExecution(CommandTree<C> commandTree, CommandContext<C> commandContext, CommandInput commandInput) {
/*  98 */     return commandTree.parse(commandContext, commandInput, this.parsingExecutor)
/*  99 */       .thenApplyAsync(command -> {
/*     */           boolean passedPostprocessing = (commandTree.commandManager().postprocessContext(commandContext, command) == State.ACCEPTED);
/*     */ 
/*     */           
/*     */           return Pair.of(command, Boolean.valueOf(passedPostprocessing));
/* 104 */         }this.parsingExecutor).thenComposeAsync(preprocessResult -> {
/*     */           if (!((Boolean)preprocessResult.second()).booleanValue()) {
/*     */             return CompletableFuture.completedFuture(CommandResult.of(commandContext));
/*     */           }
/*     */           
/*     */           if (this.executionLock != null) {
/*     */             try {
/*     */               this.executionLock.acquire();
/* 112 */             } catch (InterruptedException e) {
/*     */               Thread.currentThread().interrupt();
/*     */             } 
/*     */           }
/*     */           CompletableFuture<CommandResult<C>> commandResultFuture = null;
/*     */           try {
/*     */             commandResultFuture = ((Command)preprocessResult.first()).commandExecutionHandler().executeFuture(commandContext).exceptionally(()).thenApply(());
/*     */           } finally {
/*     */             if (this.executionLock != null) {
/*     */               if (commandResultFuture != null) {
/*     */                 commandResultFuture.whenComplete(());
/*     */               } else {
/*     */                 this.executionLock.release();
/*     */               } 
/*     */             }
/*     */           } 
/*     */           return commandResultFuture;
/*     */         }this.defaultExecutionExecutor);
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
/*     */   public <S extends ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion> CompletableFuture<Suggestions<C, S>> coordinateSuggestions(CommandTree<C> commandTree, CommandContext<C> context, CommandInput commandInput, SuggestionMapper<S> mapper) {
/* 160 */     return commandTree.getSuggestions(context, commandInput, mapper, this.suggestionsExecutor);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\ExecutionCoordinatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
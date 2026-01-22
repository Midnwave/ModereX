/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandTree;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ForkJoinPool;
/*     */ import org.apiguardian.api.API;
/*     */ import org.checkerframework.dataflow.qual.Pure;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface ExecutionCoordinator<C>
/*     */ {
/*     */   @Pure
/*     */   static <C> Builder<C> builder() {
/*  57 */     return new ExecutionCoordinatorBuilderImpl<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Pure
/*     */   static <C> ExecutionCoordinator<C> simpleCoordinator() {
/*  69 */     return builder().build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Pure
/*     */   static <C> ExecutionCoordinator<C> coordinatorFor(Executor executor) {
/*  80 */     return builder().executor(executor).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Pure
/*     */   static <C> ExecutionCoordinator<C> asyncCoordinator() {
/*  91 */     return builder().commonPoolExecutor().build();
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
/*     */   CompletableFuture<CommandResult<C>> coordinateExecution(CommandTree<C> paramCommandTree, CommandContext<C> paramCommandContext, CommandInput paramCommandInput);
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
/*     */   <S extends ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion> CompletableFuture<Suggestions<C, S>> coordinateSuggestions(CommandTree<C> paramCommandTree, CommandContext<C> paramCommandContext, CommandInput paramCommandInput, SuggestionMapper<S> paramSuggestionMapper);
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
/*     */   @Pure
/*     */   static Executor nonSchedulingExecutor() {
/* 132 */     return ExecutionCoordinatorImpl.NON_SCHEDULING_EXECUTOR;
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface Builder<C>
/*     */   {
/*     */     default Builder<C> executor(Executor executor) {
/* 160 */       return parsingExecutor(executor)
/* 161 */         .suggestionsExecutor(executor)
/* 162 */         .executionSchedulingExecutor(executor);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default Builder<C> commonPoolExecutor() {
/* 171 */       return executor(ForkJoinPool.commonPool());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Builder<C> parsingExecutor(Executor param1Executor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Builder<C> suggestionsExecutor(Executor param1Executor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Builder<C> executionSchedulingExecutor(Executor param1Executor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default Builder<C> synchronizeExecution() {
/* 204 */       return synchronizeExecution(true);
/*     */     }
/*     */     
/*     */     Builder<C> synchronizeExecution(boolean param1Boolean);
/*     */     
/*     */     ExecutionCoordinator<C> build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\ExecutionCoordinator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
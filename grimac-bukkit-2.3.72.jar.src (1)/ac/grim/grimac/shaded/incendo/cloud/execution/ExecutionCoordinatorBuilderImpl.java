/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.Executor;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ final class ExecutionCoordinatorBuilderImpl<C>
/*    */   implements ExecutionCoordinator.Builder<C>
/*    */ {
/*    */   private Executor parsingExecutor;
/*    */   private Executor suggestionsExecutor;
/*    */   private Executor executionSchedulingExecutor;
/*    */   private boolean synchronizeExecution = false;
/*    */   
/*    */   public ExecutionCoordinator.Builder<C> parsingExecutor(Executor executor) {
/* 42 */     Objects.requireNonNull(executor, "executor");
/* 43 */     this.parsingExecutor = executor;
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ExecutionCoordinator.Builder<C> suggestionsExecutor(Executor executor) {
/* 49 */     Objects.requireNonNull(executor, "executor");
/* 50 */     this.suggestionsExecutor = executor;
/* 51 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ExecutionCoordinator.Builder<C> executionSchedulingExecutor(Executor executor) {
/* 56 */     Objects.requireNonNull(executor, "executor");
/* 57 */     this.executionSchedulingExecutor = executor;
/* 58 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ExecutionCoordinator.Builder<C> synchronizeExecution(boolean synchronizeExecution) {
/* 63 */     this.synchronizeExecution = synchronizeExecution;
/* 64 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ExecutionCoordinator<C> build() {
/* 69 */     return new ExecutionCoordinatorImpl<>(this.parsingExecutor, this.suggestionsExecutor, this.executionSchedulingExecutor, this.synchronizeExecution);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\ExecutionCoordinatorBuilderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
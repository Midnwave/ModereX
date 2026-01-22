/*    */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.Executors;
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
/*    */ public final class ServicePipelineBuilder
/*    */ {
/* 37 */   private Executor executor = Executors.newSingleThreadExecutor();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServicePipeline build() {
/* 48 */     return new ServicePipeline(this.executor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServicePipelineBuilder withExecutor(Executor executor) {
/* 59 */     this.executor = Objects.<Executor>requireNonNull(executor, "Executor may not be null");
/* 60 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServicePipelineBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.BiConsumer;
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
/*     */ public final class ServiceSpigot<Context, Result>
/*     */ {
/*     */   private final Context context;
/*     */   private final ServicePipeline pipeline;
/*     */   private final ServiceRepository<Context, Result> repository;
/*     */   
/*     */   ServiceSpigot(ServicePipeline pipeline, Context context, TypeToken<? extends Service<Context, Result>> type) {
/*  52 */     this.context = context;
/*  53 */     this.pipeline = pipeline;
/*  54 */     this.repository = pipeline.getRepository(type);
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
/*     */   public Result complete() throws IllegalStateException, PipelineException {
/*  83 */     LinkedList<? extends ServiceRepository<Context, Result>.ServiceWrapper<? extends Service<Context, Result>>> queue = this.repository.queue();
/*  84 */     queue.sort(null);
/*     */ 
/*     */     
/*  87 */     boolean consumerService = false; ServiceRepository<Context, Result>.ServiceWrapper<? extends Service<Context, Result>> wrapper;
/*  88 */     while ((wrapper = queue.pollLast()) != null) {
/*  89 */       Result result; consumerService = wrapper.implementation() instanceof ac.grim.grimac.shaded.incendo.cloud.services.type.ConsumerService;
/*  90 */       if (!ServiceFilterHandler.INSTANCE.passes(wrapper, this.context)) {
/*     */         continue;
/*     */       }
/*     */       
/*     */       try {
/*  95 */         result = (Result)wrapper.implementation().handle(this.context);
/*  96 */       } catch (Exception e) {
/*  97 */         throw new PipelineException(String.format("Failed to retrieve result from %s", new Object[] { wrapper }), e);
/*     */       } 
/*  99 */       if (wrapper.implementation() instanceof ac.grim.grimac.shaded.incendo.cloud.services.type.SideEffectService) {
/* 100 */         if (result == null)
/* 101 */           throw new IllegalStateException(String.format("SideEffectService '%s' returned null", new Object[] { wrapper })); 
/* 102 */         if (result == State.ACCEPTED)
/* 103 */           return result;  continue;
/*     */       } 
/* 105 */       if (result != null) {
/* 106 */         return result;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 111 */     if (consumerService) {
/* 112 */       return (Result)State.ACCEPTED;
/*     */     }
/* 114 */     throw new IllegalStateException("No service consumed the context. This means that the pipeline was not constructed properly.");
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
/*     */   public void complete(BiConsumer<Result, Throwable> consumer) {
/*     */     try {
/* 135 */       consumer.accept(complete(), null);
/* 136 */     } catch (PipelineException pipelineException) {
/* 137 */       consumer.accept(null, pipelineException.getCause());
/* 138 */     } catch (Exception e) {
/* 139 */       consumer.accept(null, e);
/*     */     } 
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
/*     */   public CompletableFuture<Result> completeAsynchronously() {
/* 152 */     return CompletableFuture.supplyAsync(this::complete, this.pipeline.executor());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServicePump<Result> forward() {
/* 161 */     return this.pipeline.pump(complete());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<ServicePump<Result>> forwardAsynchronously() {
/* 170 */     Objects.requireNonNull(this.pipeline); return completeAsynchronously().thenApply(this.pipeline::pump);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServiceSpigot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
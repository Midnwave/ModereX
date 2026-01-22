/*    */ package ac.grim.grimac.shaded.incendo.cloud.util;
/*    */ 
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Supplier;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ public final class CompletableFutures
/*    */ {
/*    */   public static <T> CompletableFuture<T> failedFuture(Throwable throwable) {
/* 53 */     CompletableFuture<T> future = new CompletableFuture<>();
/* 54 */     future.completeExceptionally(throwable);
/* 55 */     return future;
/*    */   }
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
/*    */   public static <T> CompletableFuture<T> scheduleOn(Executor executor, Supplier<CompletableFuture<T>> futureSupplier) {
/* 70 */     return CompletableFuture.<CompletableFuture<T>>supplyAsync(futureSupplier, executor).thenCompose(Function.identity());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\CompletableFutures.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
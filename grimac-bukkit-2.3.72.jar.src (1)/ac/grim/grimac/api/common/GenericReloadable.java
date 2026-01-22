/*    */ package ac.grim.grimac.api.common;
/*    */ 
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ public interface GenericReloadable<T>
/*    */ {
/*    */   void reload(T paramT);
/*    */   
/*    */   default CompletableFuture<Boolean> reloadAsync(T config) {
/*    */     try {
/* 23 */       reload(config);
/* 24 */       return CompletableFuture.completedFuture(Boolean.valueOf(true));
/* 25 */     } catch (Exception e) {
/* 26 */       CompletableFuture<Boolean> future = new CompletableFuture<>();
/* 27 */       future.completeExceptionally(e);
/* 28 */       return future;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\common\GenericReloadable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
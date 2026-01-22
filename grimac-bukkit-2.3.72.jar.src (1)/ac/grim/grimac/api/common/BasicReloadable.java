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
/*    */ public interface BasicReloadable
/*    */ {
/*    */   void reload();
/*    */   
/*    */   default boolean isLoadedAsync() {
/* 17 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default CompletableFuture<Boolean> reloadAsync() {
/*    */     try {
/* 27 */       reload();
/* 28 */       return CompletableFuture.completedFuture(Boolean.valueOf(true));
/* 29 */     } catch (Exception e) {
/* 30 */       CompletableFuture<Boolean> future = new CompletableFuture<>();
/* 31 */       future.completeExceptionally(e);
/* 32 */       return future;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\common\BasicReloadable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
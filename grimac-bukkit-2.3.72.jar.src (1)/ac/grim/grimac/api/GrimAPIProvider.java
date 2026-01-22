/*    */ package ac.grim.grimac.api;
/*    */ 
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ public final class GrimAPIProvider {
/*    */   private static GrimAbstractAPI instance;
/*  7 */   private static final CompletableFuture<GrimAbstractAPI> futureInstance = new CompletableFuture<>();
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
/*    */   public static void init(GrimAbstractAPI api) {
/* 21 */     if (instance != null || futureInstance.isDone()) {
/* 22 */       throw new IllegalStateException("GrimAPI is already initialized");
/*    */     }
/* 24 */     instance = api;
/* 25 */     futureInstance.complete(api);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static GrimAbstractAPI get() {
/* 35 */     if (instance == null) {
/* 36 */       throw new IllegalStateException("GrimAPI is not loaded. Ensure the Grim mod is installed and initialized.");
/*    */     }
/* 38 */     return instance;
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
/*    */   public static CompletableFuture<GrimAbstractAPI> getAsync() {
/* 50 */     if (instance != null)
/*    */     {
/* 52 */       return CompletableFuture.completedFuture(instance);
/*    */     }
/* 54 */     return futureInstance;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\GrimAPIProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
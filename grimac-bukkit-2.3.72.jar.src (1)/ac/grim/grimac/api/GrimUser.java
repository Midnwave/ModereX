/*    */ package ac.grim.grimac.api;
/*    */ 
/*    */ import ac.grim.grimac.api.common.BasicReloadable;
/*    */ import ac.grim.grimac.api.config.ConfigReloadable;
/*    */ import ac.grim.grimac.api.feature.FeatureManager;
/*    */ import ac.grim.grimac.api.handler.UserHandlerHolder;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Collection;
/*    */ import java.util.UUID;
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
/*    */ public interface GrimUser
/*    */   extends ConfigReloadable, BasicReloadable, UserHandlerHolder, GrimIdentity
/*    */ {
/*    */   String getName();
/*    */   
/*    */   String getBrand();
/*    */   
/*    */   @Nullable
/*    */   String getWorldName();
/*    */   
/*    */   @Nullable
/*    */   UUID getWorldUID();
/*    */   
/*    */   int getTransactionPing();
/*    */   
/*    */   int getKeepAlivePing();
/*    */   
/*    */   String getVersionName();
/*    */   
/*    */   double getHorizontalSensitivity();
/*    */   
/*    */   double getVerticalSensitivity();
/*    */   
/*    */   boolean isVanillaMath();
/*    */   
/*    */   void updatePermissions();
/*    */   
/*    */   Collection<? extends AbstractCheck> getChecks();
/*    */   
/*    */   void runSafely(Runnable paramRunnable);
/*    */   
/*    */   int getLastTransactionReceived();
/*    */   
/*    */   int getLastTransactionSent();
/*    */   
/*    */   void addRealTimeTask(int paramInt, Runnable paramRunnable);
/*    */   
/*    */   default void addRealTimeTaskNow(Runnable runnable) {
/* 67 */     addRealTimeTask(getLastTransactionSent(), runnable);
/*    */   }
/*    */   
/*    */   default void addRealTimeTaskNext(Runnable runnable) {
/* 71 */     addRealTimeTask(getLastTransactionSent() + 1, runnable);
/*    */   }
/*    */   
/*    */   FeatureManager getFeatureManager();
/*    */   
/*    */   void sendMessage(String paramString);
/*    */   
/*    */   boolean hasPermission(String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\GrimUser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
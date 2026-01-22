/*    */ package ac.grim.grimac.api;
/*    */ 
/*    */ import ac.grim.grimac.api.alerts.AlertManager;
/*    */ import ac.grim.grimac.api.common.BasicReloadable;
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.config.ConfigReloadable;
/*    */ import ac.grim.grimac.api.event.EventBus;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.function.Function;
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
/*    */ public interface GrimAbstractAPI
/*    */   extends ConfigReloadable, BasicReloadable
/*    */ {
/*    */   @NotNull
/*    */   EventBus getEventBus();
/*    */   
/*    */   @Nullable
/*    */   GrimUser getGrimUser(UUID paramUUID);
/*    */   
/*    */   void registerVariable(String paramString, @Nullable Function<GrimUser, String> paramFunction);
/*    */   
/*    */   void registerVariable(String paramString1, @Nullable String paramString2);
/*    */   
/*    */   String getGrimVersion();
/*    */   
/*    */   void registerFunction(String paramString, @Nullable Function<Object, Object> paramFunction);
/*    */   
/*    */   @Nullable
/*    */   Function<Object, Object> getFunction(String paramString);
/*    */   
/*    */   AlertManager getAlertManager();
/*    */   
/*    */   ConfigManager getConfigManager();
/*    */   
/*    */   default void reload() {
/* 77 */     reload(getConfigManager());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default CompletableFuture<Boolean> reloadAsync() {
/* 85 */     return reloadAsync(getConfigManager());
/*    */   }
/*    */   
/*    */   boolean hasStarted();
/*    */   
/*    */   int getCurrentTick();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\GrimAbstractAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
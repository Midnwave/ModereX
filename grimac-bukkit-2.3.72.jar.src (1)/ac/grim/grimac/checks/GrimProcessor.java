/*    */ package ac.grim.grimac.checks;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.api.AbstractProcessor;
/*    */ import ac.grim.grimac.api.config.ConfigReloadable;
/*    */ import ac.grim.grimac.utils.common.ConfigReloadObserver;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class GrimProcessor
/*    */   implements AbstractProcessor, ConfigReloadable, ConfigReloadObserver
/*    */ {
/*    */   public void reload() {
/* 14 */     reload(GrimAPI.INSTANCE.getConfigManager().getConfig());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\GrimProcessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
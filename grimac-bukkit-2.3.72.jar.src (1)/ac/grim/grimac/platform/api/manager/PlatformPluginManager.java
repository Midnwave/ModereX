/*    */ package ac.grim.grimac.platform.api.manager;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.PlatformPlugin;
/*    */ 
/*    */ public interface PlatformPluginManager
/*    */ {
/*    */   PlatformPlugin[] getPlugins();
/*    */   
/*    */   PlatformPlugin getPlugin(String paramString);
/*    */   
/*    */   default boolean isPluginEnabled(String pluginName) {
/* 12 */     PlatformPlugin plugin = getPlugin(pluginName);
/* 13 */     return (plugin != null && plugin.isEnabled());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\manager\PlatformPluginManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
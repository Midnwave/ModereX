/*    */ package ac.grim.grimac.platform.bukkit.manager;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.PlatformPlugin;
/*    */ import ac.grim.grimac.platform.api.manager.PlatformPluginManager;
/*    */ import ac.grim.grimac.platform.bukkit.BukkitPlatformPlugin;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ 
/*    */ public class BukkitPlatformPluginManager
/*    */   implements PlatformPluginManager
/*    */ {
/*    */   public PlatformPlugin[] getPlugins() {
/* 14 */     Plugin[] bukkitPlugins = Bukkit.getPluginManager().getPlugins();
/* 15 */     PlatformPlugin[] plugins = new PlatformPlugin[bukkitPlugins.length];
/*    */     
/* 17 */     for (int i = 0; i < bukkitPlugins.length; i++) {
/* 18 */       plugins[i] = (PlatformPlugin)new BukkitPlatformPlugin(bukkitPlugins[i]);
/*    */     }
/*    */     
/* 21 */     return plugins;
/*    */   }
/*    */ 
/*    */   
/*    */   public PlatformPlugin getPlugin(String pluginName) {
/* 26 */     Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
/* 27 */     return (bukkitPlugin == null) ? null : (PlatformPlugin)new BukkitPlatformPlugin(bukkitPlugin);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\manager\BukkitPlatformPluginManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
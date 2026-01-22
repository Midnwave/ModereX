/*    */ package ac.grim.grimac.platform.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.PlatformPlugin;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class BukkitPlatformPlugin implements PlatformPlugin {
/*    */   private final Plugin plugin;
/*    */   
/*    */   public BukkitPlatformPlugin(Plugin plugin) {
/* 10 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabled() {
/* 15 */     return this.plugin.isEnabled();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 20 */     return this.plugin.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 25 */     return this.plugin.getDescription().getVersion();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\BukkitPlatformPlugin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
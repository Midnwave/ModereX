/*    */ package ac.grim.grimac.platform.bukkit.initables;
/*    */ 
/*    */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.bukkit.Metrics;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class BukkitBStats implements StartableInitable {
/*    */   public void start() {
/* 10 */     int pluginId = 12820;
/*    */     try {
/* 12 */       new Metrics((Plugin)GrimACBukkitLoaderPlugin.LOADER, pluginId);
/* 13 */     } catch (Exception exception) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\initables\BukkitBStats.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
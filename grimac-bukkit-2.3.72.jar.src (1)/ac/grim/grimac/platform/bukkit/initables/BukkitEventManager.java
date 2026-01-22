/*    */ package ac.grim.grimac.platform.bukkit.initables;
/*    */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*    */ import ac.grim.grimac.platform.bukkit.events.PistonEvent;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class BukkitEventManager implements StartableInitable {
/*    */   public void start() {
/* 11 */     LogUtil.info("Registering singular bukkit event... (PistonEvent)");
/*    */     
/* 13 */     Bukkit.getPluginManager().registerEvents((Listener)new PistonEvent(), (Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\initables\BukkitEventManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
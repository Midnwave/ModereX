/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.GlobalRegionScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class BukkitGlobalRegionScheduler implements GlobalRegionScheduler {
/* 13 */   private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 17 */     this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 22 */     return new BukkitTaskHandle(this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay) {
/* 27 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskLater((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, delay));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 32 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskTimer((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public void cancel(@NotNull GrimPlugin plugin) {
/* 37 */     this.bukkitScheduler.cancelTasks((Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitGlobalRegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
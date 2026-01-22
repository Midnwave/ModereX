/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.PlatformScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class BukkitAsyncScheduler
/*    */   implements AsyncScheduler {
/* 16 */   private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
/*    */ 
/*    */   
/*    */   public TaskHandle runNow(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 20 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskAsynchronously((Plugin)GrimACBukkitLoaderPlugin.LOADER, task));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
/* 25 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskLaterAsynchronously((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, 
/*    */ 
/*    */           
/* 28 */           PlatformScheduler.convertTimeToTicks(delay, timeUnit)));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
/* 34 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskTimerAsynchronously((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, 
/*    */ 
/*    */           
/* 37 */           PlatformScheduler.convertTimeToTicks(delay, timeUnit), 
/* 38 */           PlatformScheduler.convertTimeToTicks(period, timeUnit)));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 44 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskTimerAsynchronously((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void cancel(@NotNull GrimPlugin plugin) {
/* 54 */     this.bukkitScheduler.cancelTasks((Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitAsyncScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class FoliaAsyncScheduler implements AsyncScheduler {
/* 14 */   private final AsyncScheduler scheduler = Bukkit.getAsyncScheduler();
/*    */ 
/*    */   
/*    */   public TaskHandle runNow(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 18 */     return new FoliaTaskHandle(this.scheduler.runNow((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run()));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
/* 23 */     return new FoliaTaskHandle(this.scheduler.runDelayed((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), delay, timeUnit));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
/* 33 */     return new FoliaTaskHandle(this.scheduler.runAtFixedRate((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), delay, period, timeUnit));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 43 */     return new FoliaTaskHandle(this.scheduler.runAtFixedRate((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), initialDelayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void cancel(@NotNull GrimPlugin plugin) {
/* 54 */     this.scheduler.cancelTasks((Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaAsyncScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class FoliaGlobalRegionScheduler implements GlobalRegionScheduler {
/* 12 */   private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 16 */     this.globalRegionScheduler.execute((Plugin)GrimACBukkitLoaderPlugin.LOADER, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
/* 21 */     return new FoliaTaskHandle(this.globalRegionScheduler.run((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run()));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay) {
/* 26 */     return new FoliaTaskHandle(this.globalRegionScheduler.runDelayed((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), delay));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 31 */     return new FoliaTaskHandle(this.globalRegionScheduler.runAtFixedRate((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), initialDelayTicks, periodTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public void cancel(@NotNull GrimPlugin plugin) {
/* 36 */     this.globalRegionScheduler.cancelTasks((Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaGlobalRegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
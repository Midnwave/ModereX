/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.RegionScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.utils.math.Location;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class BukkitRegionScheduler implements RegionScheduler {
/* 15 */   private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
/* 19 */     this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
/* 24 */     this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
/* 29 */     return new BukkitTaskHandle(this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
/* 34 */     return new BukkitTaskHandle(this.bukkitScheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long delayTicks) {
/* 39 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskLater((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, delayTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long delayTicks) {
/* 44 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskLater((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, delayTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 49 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskTimer((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 54 */     return new BukkitTaskHandle(this.bukkitScheduler.runTaskTimer((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitRegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
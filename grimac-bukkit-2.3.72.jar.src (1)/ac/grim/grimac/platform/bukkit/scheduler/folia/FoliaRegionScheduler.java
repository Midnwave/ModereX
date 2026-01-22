/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.platform.bukkit.world.BukkitPlatformWorld;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.utils.math.Location;
/*    */ import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class FoliaRegionScheduler implements RegionScheduler {
/* 15 */   private final RegionScheduler regionScheduler = Bukkit.getRegionScheduler();
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
/* 19 */     this.regionScheduler.execute((Plugin)GrimACBukkitLoaderPlugin.LOADER, ((BukkitPlatformWorld)world).getBukkitWorld(), chunkX, chunkZ, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
/* 24 */     execute(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
/* 29 */     return new FoliaTaskHandle(this.regionScheduler.run((Plugin)GrimACBukkitLoaderPlugin.LOADER, ((BukkitPlatformWorld)world)
/*    */           
/* 31 */           .getBukkitWorld(), chunkX, chunkZ, ignored -> task.run()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
/* 40 */     return run(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long delayTicks) {
/* 45 */     return new FoliaTaskHandle(this.regionScheduler.runDelayed((Plugin)GrimACBukkitLoaderPlugin.LOADER, ((BukkitPlatformWorld)world)
/*    */           
/* 47 */           .getBukkitWorld(), chunkX, chunkZ, ignored -> task.run(), delayTicks));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long delayTicks) {
/* 57 */     return runDelayed(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delayTicks);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 62 */     return new FoliaTaskHandle(this.regionScheduler.runAtFixedRate((Plugin)GrimACBukkitLoaderPlugin.LOADER, ((BukkitPlatformWorld)world)
/*    */           
/* 64 */           .getBukkitWorld(), chunkX, chunkZ, ignored -> task.run(), initialDelayTicks, periodTicks));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
/* 75 */     return runAtFixedRate(plugin, location
/*    */         
/* 77 */         .getWorld(), location
/* 78 */         .getBlockX() >> 4, location
/* 79 */         .getBlockZ() >> 4, task, initialDelayTicks, periodTicks);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaRegionScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*    */ import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class BukkitEntityScheduler implements EntityScheduler {
/* 14 */   private final BukkitScheduler scheduler = Bukkit.getScheduler();
/*    */ 
/*    */   
/*    */   public void execute(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
/* 18 */     this.scheduler.runTaskLater((Plugin)GrimACBukkitLoaderPlugin.LOADER, run, delay);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
/* 23 */     return new BukkitTaskHandle(this.scheduler.runTask((Plugin)GrimACBukkitLoaderPlugin.LOADER, task));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
/* 28 */     return new BukkitTaskHandle(this.scheduler.runTaskLater((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, delayTicks));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
/* 33 */     return new BukkitTaskHandle(this.scheduler.runTaskTimer((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitEntityScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
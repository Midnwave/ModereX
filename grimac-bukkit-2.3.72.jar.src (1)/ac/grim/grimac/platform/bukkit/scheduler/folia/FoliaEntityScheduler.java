/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ 
/*    */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*    */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*    */ import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.platform.bukkit.entity.BukkitGrimEntity;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class FoliaEntityScheduler implements EntityScheduler {
/*    */   public void execute(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
/* 16 */     ((BukkitGrimEntity)entity).getBukkitEntity().getScheduler().execute((Plugin)GrimACBukkitLoaderPlugin.LOADER, task, retired, delay);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle run(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
/* 21 */     return new FoliaTaskHandle(((BukkitGrimEntity)entity).getBukkitEntity().getScheduler().run((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), retired));
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskHandle runDelayed(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
/* 26 */     return new FoliaTaskHandle(((BukkitGrimEntity)entity)
/* 27 */         .getBukkitEntity().getScheduler().runDelayed((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), retired, delayTicks));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskHandle runAtFixedRate(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
/* 33 */     return new FoliaTaskHandle(((BukkitGrimEntity)entity).getBukkitEntity().getScheduler().runAtFixedRate((Plugin)GrimACBukkitLoaderPlugin.LOADER, ignored -> task.run(), retired, initialDelayTicks, periodTicks));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaEntityScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
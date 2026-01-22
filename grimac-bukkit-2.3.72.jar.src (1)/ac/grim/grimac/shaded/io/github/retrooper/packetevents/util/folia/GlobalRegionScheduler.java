/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*     */ import java.util.function.Consumer;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GlobalRegionScheduler
/*     */ {
/*     */   private BukkitScheduler bukkitScheduler;
/*     */   private io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler globalRegionScheduler;
/*     */   
/*     */   protected GlobalRegionScheduler() {
/*  37 */     if (FoliaScheduler.isFolia) {
/*  38 */       this.globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
/*     */     } else {
/*  40 */       this.bukkitScheduler = Bukkit.getScheduler();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(@NotNull Plugin plugin, @NotNull Runnable run) {
/*  51 */     if (!FoliaScheduler.isFolia) {
/*  52 */       this.bukkitScheduler.runTask(plugin, run);
/*     */       
/*     */       return;
/*     */     } 
/*  56 */     this.globalRegionScheduler.execute(plugin, run);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper run(@NotNull Plugin plugin, @NotNull Consumer<Object> task) {
/*  67 */     if (!FoliaScheduler.isFolia) {
/*  68 */       return new TaskWrapper(this.bukkitScheduler.runTask(plugin, () -> task.accept(null)));
/*     */     }
/*     */     
/*  71 */     return new TaskWrapper(this.globalRegionScheduler.run(plugin, o -> task.accept(null)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay) {
/*  83 */     if (delay < 1L) delay = 1L;
/*     */     
/*  85 */     if (!FoliaScheduler.isFolia) {
/*  86 */       return new TaskWrapper(this.bukkitScheduler.runTaskLater(plugin, () -> task.accept(null), delay));
/*     */     }
/*     */     
/*  89 */     return new TaskWrapper(this.globalRegionScheduler.runDelayed(plugin, o -> task.accept(null), delay));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
/* 102 */     if (initialDelayTicks < 1L) initialDelayTicks = 1L; 
/* 103 */     if (periodTicks < 1L) periodTicks = 1L;
/*     */     
/* 105 */     if (!FoliaScheduler.isFolia) {
/* 106 */       return new TaskWrapper(this.bukkitScheduler.runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
/*     */     }
/*     */     
/* 109 */     return new TaskWrapper(this.globalRegionScheduler.runAtFixedRate(plugin, o -> task.accept(null), initialDelayTicks, periodTicks));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel(@NotNull Plugin plugin) {
/* 118 */     if (!FoliaScheduler.isFolia) {
/* 119 */       Bukkit.getScheduler().cancelTasks(plugin);
/*     */       
/*     */       return;
/*     */     } 
/* 123 */     this.globalRegionScheduler.cancelTasks(plugin);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\GlobalRegionScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
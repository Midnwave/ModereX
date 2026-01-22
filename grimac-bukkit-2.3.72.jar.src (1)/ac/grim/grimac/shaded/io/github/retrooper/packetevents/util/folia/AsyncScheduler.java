/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class AsyncScheduler
/*     */ {
/*     */   private BukkitScheduler bukkitScheduler;
/*     */   private io.papermc.paper.threadedregions.scheduler.AsyncScheduler asyncScheduler;
/*     */   
/*     */   protected AsyncScheduler() {
/*  38 */     if (FoliaScheduler.isFolia) {
/*  39 */       this.asyncScheduler = Bukkit.getAsyncScheduler();
/*     */     } else {
/*  41 */       this.bukkitScheduler = Bukkit.getScheduler();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper runNow(@NotNull Plugin plugin, @NotNull Consumer<Object> task) {
/*  53 */     if (!FoliaScheduler.isFolia) {
/*  54 */       return new TaskWrapper(this.bukkitScheduler.runTaskAsynchronously(plugin, () -> task.accept(null)));
/*     */     }
/*     */     
/*  57 */     return new TaskWrapper(this.asyncScheduler.runNow(plugin, o -> task.accept(null)));
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
/*     */   public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
/*  70 */     if (!FoliaScheduler.isFolia) {
/*  71 */       return new TaskWrapper(this.bukkitScheduler.runTaskLaterAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit)));
/*     */     }
/*     */     
/*  74 */     return new TaskWrapper(this.asyncScheduler.runDelayed(plugin, o -> task.accept(null), delay, timeUnit));
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
/*     */   
/*     */   public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
/*  88 */     if (period < 1L) period = 1L;
/*     */     
/*  90 */     if (!FoliaScheduler.isFolia) {
/*  91 */       return new TaskWrapper(this.bukkitScheduler.runTaskTimerAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit), convertTimeToTicks(period, timeUnit)));
/*     */     }
/*     */     
/*  94 */     return new TaskWrapper(this.asyncScheduler.runAtFixedRate(plugin, o -> task.accept(null), delay, period, timeUnit));
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
/* 107 */     if (periodTicks < 1L) periodTicks = 1L;
/*     */     
/* 109 */     if (!FoliaScheduler.isFolia) {
/* 110 */       return new TaskWrapper(this.bukkitScheduler.runTaskTimerAsynchronously(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
/*     */     }
/*     */     
/* 113 */     return new TaskWrapper(this.asyncScheduler.runAtFixedRate(plugin, o -> task.accept(null), initialDelayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel(@NotNull Plugin plugin) {
/* 122 */     if (!FoliaScheduler.isFolia) {
/* 123 */       this.bukkitScheduler.cancelTasks(plugin);
/*     */       
/*     */       return;
/*     */     } 
/* 127 */     this.asyncScheduler.cancelTasks(plugin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long convertTimeToTicks(long time, TimeUnit timeUnit) {
/* 138 */     return timeUnit.toMillis(time) / 50L;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\AsyncScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*     */ import java.util.function.Consumer;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Entity;
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
/*     */ public class EntityScheduler
/*     */ {
/*     */   private BukkitScheduler bukkitScheduler;
/*     */   
/*     */   protected EntityScheduler() {
/*  37 */     if (!FoliaScheduler.isFolia) {
/*  38 */       this.bukkitScheduler = Bukkit.getScheduler();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
/*  56 */     if (!FoliaScheduler.isFolia) {
/*  57 */       this.bukkitScheduler.runTaskLater(plugin, run, delay);
/*     */       
/*     */       return;
/*     */     } 
/*  61 */     entity.getScheduler().execute(plugin, run, retired, delay);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper run(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired) {
/*  79 */     if (!FoliaScheduler.isFolia) {
/*  80 */       return new TaskWrapper(this.bukkitScheduler.runTask(plugin, () -> task.accept(null)));
/*     */     }
/*     */     
/*  83 */     return new TaskWrapper(entity.getScheduler().run(plugin, o -> task.accept(null), retired));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper runDelayed(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired, long delayTicks) {
/* 101 */     if (delayTicks < 1L) delayTicks = 1L;
/*     */     
/* 103 */     if (!FoliaScheduler.isFolia) {
/* 104 */       return new TaskWrapper(this.bukkitScheduler.runTaskLater(plugin, () -> task.accept(null), delayTicks));
/*     */     }
/*     */     
/* 107 */     return new TaskWrapper(entity.getScheduler().runDelayed(plugin, o -> task.accept(null), retired, delayTicks));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskWrapper runAtFixedRate(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
/* 126 */     if (initialDelayTicks < 1L) initialDelayTicks = 1L; 
/* 127 */     if (periodTicks < 1L) periodTicks = 1L;
/*     */     
/* 129 */     if (!FoliaScheduler.isFolia) {
/* 130 */       return new TaskWrapper(this.bukkitScheduler.runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
/*     */     }
/*     */     
/* 133 */     return new TaskWrapper(entity.getScheduler().runAtFixedRate(plugin, o -> task.accept(null), retired, initialDelayTicks, periodTicks));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\EntityScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
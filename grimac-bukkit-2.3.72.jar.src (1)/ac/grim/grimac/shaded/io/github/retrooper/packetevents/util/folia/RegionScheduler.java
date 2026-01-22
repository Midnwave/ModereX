/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*     */ import java.util.function.Consumer;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
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
/*     */ public class RegionScheduler
/*     */ {
/*     */   private BukkitScheduler bukkitScheduler;
/*     */   private io.papermc.paper.threadedregions.scheduler.RegionScheduler regionScheduler;
/*     */   
/*     */   protected RegionScheduler() {
/*  39 */     if (FoliaScheduler.isFolia) {
/*  40 */       this.regionScheduler = Bukkit.getRegionScheduler();
/*     */     } else {
/*  42 */       this.bukkitScheduler = Bukkit.getScheduler();
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
/*     */   public void execute(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Runnable run) {
/*  56 */     if (!FoliaScheduler.isFolia) {
/*  57 */       this.bukkitScheduler.runTask(plugin, run);
/*     */       
/*     */       return;
/*     */     } 
/*  61 */     this.regionScheduler.execute(plugin, world, chunkX, chunkZ, run);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable run) {
/*  72 */     if (!FoliaScheduler.isFolia) {
/*  73 */       Bukkit.getScheduler().runTask(plugin, run);
/*     */       
/*     */       return;
/*     */     } 
/*  77 */     this.regionScheduler.execute(plugin, location, run);
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
/*     */   public TaskWrapper run(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task) {
/*  91 */     if (!FoliaScheduler.isFolia) {
/*  92 */       return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
/*     */     }
/*     */     
/*  95 */     return new TaskWrapper(this.regionScheduler.run(plugin, world, chunkX, chunkZ, o -> task.accept(null)));
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
/*     */   public TaskWrapper run(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task) {
/* 107 */     if (!FoliaScheduler.isFolia) {
/* 108 */       return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
/*     */     }
/*     */     
/* 111 */     return new TaskWrapper(this.regionScheduler.run(plugin, location, o -> task.accept(null)));
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
/*     */   public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task, long delayTicks) {
/* 126 */     if (delayTicks < 1L) delayTicks = 1L;
/*     */     
/* 128 */     if (!FoliaScheduler.isFolia) {
/* 129 */       return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
/*     */     }
/*     */     
/* 132 */     return new TaskWrapper(this.regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, o -> task.accept(null), delayTicks));
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
/*     */   public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long delayTicks) {
/* 145 */     if (delayTicks < 1L) delayTicks = 1L;
/*     */     
/* 147 */     if (!FoliaScheduler.isFolia) {
/* 148 */       return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
/*     */     }
/*     */     
/* 151 */     return new TaskWrapper(this.regionScheduler.runDelayed(plugin, location, o -> task.accept(null), delayTicks));
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
/*     */   public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
/* 167 */     if (initialDelayTicks < 1L) initialDelayTicks = 1L; 
/* 168 */     if (periodTicks < 1L) periodTicks = 1L;
/*     */     
/* 170 */     if (!FoliaScheduler.isFolia) {
/* 171 */       return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
/*     */     }
/*     */     
/* 174 */     return new TaskWrapper(this.regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, o -> task.accept(null), initialDelayTicks, periodTicks));
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
/*     */   public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
/* 188 */     if (initialDelayTicks < 1L) initialDelayTicks = 1L; 
/* 189 */     if (periodTicks < 1L) periodTicks = 1L;
/*     */     
/* 191 */     if (!FoliaScheduler.isFolia) {
/* 192 */       return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
/*     */     }
/*     */     
/* 195 */     return new TaskWrapper(this.regionScheduler.runAtFixedRate(plugin, location, o -> task.accept(null), initialDelayTicks, periodTicks));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\RegionScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
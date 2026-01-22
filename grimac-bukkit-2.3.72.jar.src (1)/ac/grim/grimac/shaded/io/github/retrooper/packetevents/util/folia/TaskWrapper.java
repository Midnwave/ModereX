/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitTask;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TaskWrapper
/*    */ {
/*    */   private BukkitTask bukkitTask;
/*    */   private ScheduledTask scheduledTask;
/*    */   
/*    */   public TaskWrapper(@NotNull BukkitTask bukkitTask) {
/* 42 */     this.bukkitTask = bukkitTask;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskWrapper(@NotNull ScheduledTask scheduledTask) {
/* 51 */     this.scheduledTask = scheduledTask;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Plugin getOwner() {
/* 60 */     return (this.bukkitTask != null) ? this.bukkitTask.getOwner() : this.scheduledTask.getOwningPlugin();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 69 */     return (this.bukkitTask != null) ? this.bukkitTask.isCancelled() : this.scheduledTask.isCancelled();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void cancel() {
/* 76 */     if (this.bukkitTask != null) {
/* 77 */       this.bukkitTask.cancel();
/*    */     } else {
/* 79 */       this.scheduledTask.cancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\TaskWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
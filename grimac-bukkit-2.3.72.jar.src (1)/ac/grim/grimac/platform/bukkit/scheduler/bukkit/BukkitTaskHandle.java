/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Objects;
/*    */ import org.bukkit.scheduler.BukkitTask;
/*    */ 
/*    */ public class BukkitTaskHandle
/*    */   implements TaskHandle {
/*    */   @NotNull
/*    */   private final BukkitTask task;
/*    */   
/*    */   @Contract(pure = true)
/*    */   public BukkitTaskHandle(@NotNull BukkitTask task) {
/* 16 */     this.task = Objects.<BukkitTask>requireNonNull(task);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSync() {
/* 21 */     return this.task.isSync();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 26 */     return this.task.isCancelled();
/*    */   }
/*    */ 
/*    */   
/*    */   public void cancel() {
/* 31 */     this.task.cancel();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitTaskHandle.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
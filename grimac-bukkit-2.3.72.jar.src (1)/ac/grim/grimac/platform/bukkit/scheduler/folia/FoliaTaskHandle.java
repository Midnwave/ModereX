/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public class FoliaTaskHandle
/*    */   implements TaskHandle {
/*    */   @NotNull
/*    */   private final ScheduledTask task;
/*    */   
/*    */   @Contract(pure = true)
/*    */   public FoliaTaskHandle(@NotNull ScheduledTask task) {
/* 16 */     this.task = Objects.<ScheduledTask>requireNonNull(task);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSync() {
/* 21 */     return false;
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


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaTaskHandle.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
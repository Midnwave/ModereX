/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.scheduler.TaskHandle;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia.TaskWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public class TaskWrapperHandle
/*    */   implements TaskHandle {
/*    */   @NotNull
/*    */   private final TaskWrapper task;
/*    */   
/*    */   @Contract(pure = true)
/*    */   public TaskWrapperHandle(TaskWrapper task) {
/* 16 */     this.task = Objects.<TaskWrapper>requireNonNull(task);
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


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\TaskWrapperHandle.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
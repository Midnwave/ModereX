/*    */ package ac.grim.grimac.shaded.zaxxer.hikari.pool;
/*    */ 
/*    */ import ac.grim.grimac.shaded.slf4j.Logger;
/*    */ import ac.grim.grimac.shaded.slf4j.LoggerFactory;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.ScheduledFuture;
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ class ProxyLeakTask
/*    */   implements Runnable
/*    */ {
/* 34 */   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyLeakTask.class);
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
/* 45 */   static final ProxyLeakTask NO_LEAK = new ProxyLeakTask()
/*    */     {
/*    */       void schedule(ScheduledExecutorService executorService, long leakDetectionThreshold) {}
/*    */ 
/*    */       
/*    */       public void run() {}
/*    */       
/*    */       public void cancel() {}
/*    */     };
/*    */   
/*    */   private ScheduledFuture<?> scheduledFuture;
/*    */   private String connectionName;
/*    */   
/*    */   ProxyLeakTask(PoolEntry poolEntry) {
/* 59 */     this.exception = new Exception("Apparent connection leak detected");
/* 60 */     this.threadName = Thread.currentThread().getName();
/* 61 */     this.connectionName = poolEntry.connection.toString();
/*    */   }
/*    */   private Exception exception;
/*    */   private String threadName;
/*    */   private boolean isLeaked;
/*    */   
/*    */   private ProxyLeakTask() {}
/*    */   
/*    */   void schedule(ScheduledExecutorService executorService, long leakDetectionThreshold) {
/* 70 */     this.scheduledFuture = executorService.schedule(this, leakDetectionThreshold, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 77 */     this.isLeaked = true;
/*    */     
/* 79 */     StackTraceElement[] stackTrace = this.exception.getStackTrace();
/* 80 */     StackTraceElement[] trace = new StackTraceElement[stackTrace.length - 5];
/* 81 */     System.arraycopy(stackTrace, 5, trace, 0, trace.length);
/*    */     
/* 83 */     this.exception.setStackTrace(trace);
/* 84 */     LOGGER.warn("Connection leak detection triggered for {} on thread {}, stack trace follows", new Object[] { this.connectionName, this.threadName, this.exception });
/*    */   }
/*    */ 
/*    */   
/*    */   void cancel() {
/* 89 */     this.scheduledFuture.cancel(false);
/* 90 */     if (this.isLeaked)
/* 91 */       LOGGER.info("Previously reported leaked connection {} on thread {} was returned to the pool (unleaked)", this.connectionName, this.threadName); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\pool\ProxyLeakTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
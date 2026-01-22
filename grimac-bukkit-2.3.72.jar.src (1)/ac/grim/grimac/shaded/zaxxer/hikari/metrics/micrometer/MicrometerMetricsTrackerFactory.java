/*    */ package ac.grim.grimac.shaded.zaxxer.hikari.metrics.micrometer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.IMetricsTracker;
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.PoolStats;
/*    */ import io.micrometer.core.instrument.MeterRegistry;
/*    */ 
/*    */ 
/*    */ public class MicrometerMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/*    */   private final MeterRegistry registry;
/*    */   
/*    */   public MicrometerMetricsTrackerFactory(MeterRegistry registry) {
/* 15 */     this.registry = registry;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 21 */     return new MicrometerMetricsTracker(poolName, poolStats, this.registry);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\metrics\micrometer\MicrometerMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
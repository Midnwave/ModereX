/*    */ package ac.grim.grimac.shaded.zaxxer.hikari.metrics.dropwizard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.IMetricsTracker;
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*    */ import ac.grim.grimac.shaded.zaxxer.hikari.metrics.PoolStats;
/*    */ import com.codahale.metrics.MetricRegistry;
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
/*    */ public final class CodahaleMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/*    */   private final MetricRegistry registry;
/*    */   
/*    */   public CodahaleMetricsTrackerFactory(MetricRegistry registry) {
/* 30 */     this.registry = registry;
/*    */   }
/*    */ 
/*    */   
/*    */   public MetricRegistry getRegistry() {
/* 35 */     return this.registry;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 41 */     return new CodaHaleMetricsTracker(poolName, poolStats, this.registry);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\metrics\dropwizard\CodahaleMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
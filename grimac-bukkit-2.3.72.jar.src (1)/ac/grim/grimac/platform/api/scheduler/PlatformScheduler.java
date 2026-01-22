/*     */ package ac.grim.grimac.platform.api.scheduler;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface PlatformScheduler
/*     */ {
/*     */   static long convertTimeToTicks(long time, TimeUnit timeUnit) {
/*  80 */     return timeUnit.toMillis(time) / 50L;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long convertTicksToTime(long ticks, TimeUnit timeUnit) {
/* 106 */     long millis = ticks * 50L;
/* 107 */     return timeUnit.convert(millis, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   AsyncScheduler getAsyncScheduler();
/*     */   
/*     */   GlobalRegionScheduler getGlobalRegionScheduler();
/*     */   
/*     */   EntityScheduler getEntityScheduler();
/*     */   
/*     */   RegionScheduler getRegionScheduler();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\PlatformScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
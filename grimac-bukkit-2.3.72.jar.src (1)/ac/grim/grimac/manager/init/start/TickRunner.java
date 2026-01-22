/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.platform.api.Platform;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ 
/*    */ public class TickRunner
/*    */   implements StartableInitable {
/*    */   public void start() {
/* 10 */     LogUtil.info("Registering tick schedulers...");
/*    */     
/* 12 */     if (GrimAPI.INSTANCE.getPlatform() == Platform.FOLIA) {
/* 13 */       GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getGrimPlugin(), () -> { GrimAPI.INSTANCE.getTickManager().tickSync(); GrimAPI.INSTANCE.getTickManager().tickAsync(); }1L, 1L);
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 18 */       GrimAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().runAtFixedRate(GrimAPI.INSTANCE.getGrimPlugin(), () -> GrimAPI.INSTANCE.getTickManager().tickSync(), 0L, 1L);
/* 19 */       GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getGrimPlugin(), () -> GrimAPI.INSTANCE.getTickManager().tickAsync(), 0L, 1L);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\TickRunner.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.checks.impl.timer;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ 
/*    */ 
/*    */ @CheckData(name = "TimerLimit", setback = 10.0D)
/*    */ public class TimerLimit
/*    */   extends Timer
/*    */ {
/*    */   private long limitAbuseOverPing;
/*    */   
/*    */   public TimerLimit(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void doCheck(PacketReceiveEvent event) {
/* 22 */     if (this.timerBalanceRealTime > System.nanoTime()) {
/*    */       
/* 24 */       if (!event.isCancelled() && 
/* 25 */         flagAndAlert() && shouldSetback()) {
/* 26 */         this.player.getSetbackTeleportUtil().executeNonSimulatingSetback();
/*    */       }
/*    */ 
/*    */ 
/*    */       
/* 31 */       this.timerBalanceRealTime = (long)(this.timerBalanceRealTime - 5.0E7D);
/*    */     } 
/*    */     
/* 34 */     limitFallBehind();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void limitFallBehind() {
/* 40 */     long playerClock = this.lastMovementPlayerClock;
/* 41 */     if (this.limitAbuseOverPing != -1L && System.nanoTime() - playerClock > this.limitAbuseOverPing) {
/* 42 */       playerClock = System.nanoTime() - this.limitAbuseOverPing;
/*    */     }
/* 44 */     this.timerBalanceRealTime = Math.max(this.timerBalanceRealTime, playerClock - this.clockDrift);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 49 */     super.onReload(config);
/* 50 */     this.limitAbuseOverPing = config.getLongElse(getConfigName() + ".ping-abuse-limit-threshold", 1000L);
/* 51 */     if (this.limitAbuseOverPing != -1L)
/* 52 */       this.limitAbuseOverPing *= 1000000L; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\timer\TimerLimit.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
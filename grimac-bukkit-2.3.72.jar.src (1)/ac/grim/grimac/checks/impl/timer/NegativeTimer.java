/*    */ package ac.grim.grimac.checks.impl.timer;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "NegativeTimer", setback = -1.0D, experimental = true)
/*    */ public class NegativeTimer
/*    */   extends Timer implements PostPredictionCheck {
/*    */   public NegativeTimer(GrimPlayer player) {
/* 14 */     super(player);
/* 15 */     this.timerBalanceRealTime = System.nanoTime() + this.clockDrift;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 21 */     if (this.player.uncertaintyHandler.lastPointThree.hasOccurredSince(2) || !predictionComplete.isChecked()) {
/* 22 */       this.timerBalanceRealTime = System.nanoTime() + this.clockDrift;
/*    */     }
/*    */     
/* 25 */     if (this.timerBalanceRealTime < this.lastMovementPlayerClock - this.clockDrift) {
/* 26 */       int lostMS = (int)((System.nanoTime() - this.timerBalanceRealTime) / 1000000.0D);
/* 27 */       flagAndAlertWithSetback("-" + lostMS);
/* 28 */       this.timerBalanceRealTime = (long)(this.timerBalanceRealTime + 5.0E7D);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void doCheck(PacketReceiveEvent event) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 40 */     this.clockDrift = (long)(config.getDoubleElse(getConfigName() + ".drift", 1200.0D) * 1000000.0D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\timer\NegativeTimer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
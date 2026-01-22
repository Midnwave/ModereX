/*    */ package ac.grim.grimac.checks.impl.movement;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "NoSlow", description = "Was not slowed while using an item", setback = 5.0D)
/*    */ public class NoSlow
/*    */   extends Check
/*    */   implements PostPredictionCheck {
/*    */   public boolean didSlotChangeLastTick = false;
/*    */   public boolean flaggedLastTick = false;
/*    */   double offsetToFlag;
/* 18 */   double bestOffset = 1.0D;
/*    */   
/*    */   public NoSlow(GrimPlayer player) {
/* 21 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 26 */     if (!predictionComplete.isChecked()) {
/*    */       return;
/*    */     }
/* 29 */     if (this.player.packetStateData.isSlowedByUsingItem()) {
/*    */       
/* 31 */       if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) && this.didSlotChangeLastTick) {
/* 32 */         this.didSlotChangeLastTick = false;
/* 33 */         this.flaggedLastTick = false;
/*    */       } 
/*    */       
/* 36 */       if (this.bestOffset > this.offsetToFlag) {
/* 37 */         if (this.flaggedLastTick) {
/* 38 */           flagAndAlertWithSetback();
/*    */         }
/* 40 */         this.flaggedLastTick = true;
/*    */       } else {
/* 42 */         reward();
/* 43 */         this.flaggedLastTick = false;
/*    */       } 
/*    */     } 
/* 46 */     this.bestOffset = 1.0D;
/*    */   }
/*    */   
/*    */   public void handlePredictionAnalysis(double offset) {
/* 50 */     this.bestOffset = Math.min(this.bestOffset, offset);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 55 */     this.offsetToFlag = config.getDoubleElse(getConfigName() + ".threshold", 0.001D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\movement\NoSlow.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineLava;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineWater;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineWaterLegacy;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*    */ 
/*    */ public class MovementTickerPlayer extends MovementTicker {
/*    */   public MovementTickerPlayer(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {
/* 18 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 19 */       (new PredictionEngineWater()).guessBestMovement(swimSpeed, this.player, isFalling, this.player.gravity, swimFriction);
/*    */     } else {
/* 21 */       (new PredictionEngineWaterLegacy()).guessBestMovement(swimSpeed, this.player, swimFriction);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void doLavaMove() {
/* 27 */     (new PredictionEngineLava()).guessBestMovement(0.02F, this.player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doNormalMove(float blockFriction) {
/* 32 */     (new PredictionEngineNormal()).guessBestMovement(BlockProperties.getFrictionInfluencedSpeed(blockFriction, this.player), this.player);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerPlayer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
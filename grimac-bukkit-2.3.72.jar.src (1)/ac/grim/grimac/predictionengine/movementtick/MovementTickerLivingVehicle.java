/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineRideableLava;
/*    */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineRideableNormal;
/*    */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineRideableWater;
/*    */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineRideableWaterLegacy;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*    */ 
/*    */ public class MovementTickerLivingVehicle extends MovementTicker {
/* 13 */   Vector3dm movementInput = new Vector3dm();
/*    */   
/*    */   public MovementTickerLivingVehicle(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {
/* 21 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 22 */       (new PredictionEngineRideableWater(this.movementInput)).guessBestMovement(swimSpeed, this.player, isFalling, this.player.gravity, swimFriction);
/*    */     } else {
/* 24 */       (new PredictionEngineRideableWaterLegacy(this.movementInput)).guessBestMovement(swimSpeed, this.player, swimFriction);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void doLavaMove() {
/* 30 */     (new PredictionEngineRideableLava(this.movementInput)).guessBestMovement(0.02F, this.player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doNormalMove(float blockFriction) {
/* 35 */     (new PredictionEngineRideableNormal(this.movementInput)).guessBestMovement(BlockProperties.getFrictionInfluencedSpeed(blockFriction, this.player), this.player);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerLivingVehicle.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
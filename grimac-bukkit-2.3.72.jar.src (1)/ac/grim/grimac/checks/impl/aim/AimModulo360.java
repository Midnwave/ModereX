/*    */ package ac.grim.grimac.checks.impl.aim;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.RotationCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*    */ 
/*    */ 
/*    */ @CheckData(name = "AimModulo360", decay = 0.005D)
/*    */ public class AimModulo360
/*    */   extends Check
/*    */   implements RotationCheck
/*    */ {
/*    */   private float lastDeltaYaw;
/*    */   
/*    */   public AimModulo360(GrimPlayer playerData) {
/* 18 */     super(playerData);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(RotationUpdate rotationUpdate) {
/* 25 */     if (this.player.packetStateData.lastPacketWasTeleport || this.player.vehicleData.wasVehicleSwitch || this.player.packetStateData.horseInteractCausedForcedRotation) {
/*    */       
/* 27 */       this.lastDeltaYaw = rotationUpdate.getDeltaXRot();
/*    */       
/*    */       return;
/*    */     } 
/* 31 */     if (this.player.xRot < 360.0F && this.player.xRot > -360.0F && Math.abs(rotationUpdate.getDeltaXRot()) > 320.0F && Math.abs(this.lastDeltaYaw) < 30.0F) {
/* 32 */       flagAndAlert();
/*    */     } else {
/* 34 */       reward();
/*    */     } 
/*    */     
/* 37 */     this.lastDeltaYaw = rotationUpdate.getDeltaXRot();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\aim\AimModulo360.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
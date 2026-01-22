/*    */ package ac.grim.grimac.checks.impl.movement;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.VehicleCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*    */ import ac.grim.grimac.utils.anticheat.update.VehiclePositionUpdate;
/*    */ 
/*    */ public class VehiclePredictionRunner extends Check implements VehicleCheck {
/*    */   public VehiclePredictionRunner(GrimPlayer playerData) {
/* 11 */     super(playerData);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(VehiclePositionUpdate vehicleUpdate) {
/* 18 */     this.player.movementCheckRunner.processAndCheckMovementPacket(new PositionUpdate(vehicleUpdate.getFrom(), vehicleUpdate.getTo(), false, null, null, vehicleUpdate.isTeleport()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\movement\VehiclePredictionRunner.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
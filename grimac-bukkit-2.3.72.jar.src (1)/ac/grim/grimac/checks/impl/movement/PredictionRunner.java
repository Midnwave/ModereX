/*    */ package ac.grim.grimac.checks.impl.movement;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PositionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*    */ 
/*    */ public class PredictionRunner extends Check implements PositionCheck {
/*    */   public PredictionRunner(GrimPlayer playerData) {
/* 10 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPositionUpdate(PositionUpdate positionUpdate) {
/* 15 */     if (!this.player.inVehicle())
/* 16 */       this.player.movementCheckRunner.processAndCheckMovementPacket(positionUpdate); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\movement\PredictionRunner.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
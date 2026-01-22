/*    */ package ac.grim.grimac.checks.impl.aim;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.RotationCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*    */ 
/*    */ @CheckData(name = "AimDuplicateLook")
/*    */ public class AimDuplicateLook extends Check implements RotationCheck {
/*    */   private boolean exempt;
/*    */   
/*    */   public AimDuplicateLook(GrimPlayer playerData) {
/* 14 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void process(RotationUpdate rotationUpdate) {
/* 19 */     if (this.player.packetStateData.lastPacketWasTeleport || this.player.packetStateData.lastPacketWasOnePointSeventeenDuplicate || this.player.compensatedEntities.self.getRiding() != null) {
/* 20 */       this.exempt = true;
/*    */       
/*    */       return;
/*    */     } 
/* 24 */     if (this.exempt) {
/* 25 */       this.exempt = false;
/*    */       
/*    */       return;
/*    */     } 
/* 29 */     if (rotationUpdate.getFrom().equals(rotationUpdate.getTo()))
/* 30 */       flagAndAlert(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\aim\AimDuplicateLook.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
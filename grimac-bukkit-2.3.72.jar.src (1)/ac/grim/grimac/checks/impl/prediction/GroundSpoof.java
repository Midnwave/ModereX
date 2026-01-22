/*    */ package ac.grim.grimac.checks.impl.prediction;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "GroundSpoof", setback = 10.0D, decay = 0.01D)
/*    */ public class GroundSpoof
/*    */   extends Check implements PostPredictionCheck {
/*    */   public GroundSpoof(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 23 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && this.player.gamemode == GameMode.SPECTATOR) {
/*    */       return;
/*    */     }
/* 26 */     if (this.player.exemptOnGround() || !predictionComplete.isChecked())
/*    */       return; 
/* 28 */     if ((this.player.getSetbackTeleportUtil()).blockOffsets)
/*    */       return; 
/* 30 */     if (this.player.packetStateData.lastPacketWasTeleport)
/*    */       return; 
/* 32 */     if (this.player.clientClaimsLastOnGround != this.player.onGround) {
/* 33 */       flagAndAlertWithSetback("claimed " + this.player.clientClaimsLastOnGround);
/* 34 */       (this.player.checkManager.getNoFall()).flipPlayerGroundStatus = true;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\prediction\GroundSpoof.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
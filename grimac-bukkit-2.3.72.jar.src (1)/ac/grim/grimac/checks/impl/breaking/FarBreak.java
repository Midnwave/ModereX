/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.math.VectorUtils;
/*    */ 
/*    */ @CheckData(name = "FarBreak", description = "Breaking blocks too far away", experimental = true)
/*    */ public class FarBreak extends Check implements BlockBreakCheck {
/*    */   public FarBreak(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 23 */     if (this.player.gamemode == GameMode.SPECTATOR || this.player.inVehicle() || blockBreak.action == DiggingAction.CANCELLED_DIGGING) {
/*    */       return;
/*    */     }
/* 26 */     double min = Double.MAX_VALUE;
/* 27 */     for (double d : this.player.getPossibleEyeHeights()) {
/* 28 */       SimpleCollisionBox box = new SimpleCollisionBox(blockBreak.position);
/* 29 */       Vector3dm eyes = new Vector3dm(this.player.x, this.player.y + d, this.player.z);
/* 30 */       Vector3dm best = VectorUtils.cutBoxToVector(eyes, box);
/* 31 */       min = Math.min(min, eyes.distanceSquared(best));
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 36 */     double maxReach = this.player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
/* 37 */     if (this.player.packetStateData.didLastMovementIncludePosition || this.player.canSkipTicks()) {
/* 38 */       double threshold = this.player.getMovementThreshold();
/* 39 */       maxReach += Math.hypot(threshold, threshold);
/*    */     } 
/*    */     
/* 42 */     if (min > maxReach * maxReach && flagAndAlert(String.format("distance=%.2f", new Object[] { Double.valueOf(Math.sqrt(min)) })) && shouldModifyPackets())
/* 43 */       blockBreak.cancel(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\FarBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ @CheckData(name = "PositionBreakA")
/*    */ public class PositionBreakA extends Check implements BlockBreakCheck {
/*    */   public PositionBreakA(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 20 */     if (this.player.inVehicle() || blockBreak.action == DiggingAction.CANCELLED_DIGGING || blockBreak.block
/*    */       
/* 22 */       .getType() == StateTypes.REDSTONE_WIRE) {
/*    */       return;
/*    */     }
/* 25 */     SimpleCollisionBox combined = blockBreak.getCombinedBox();
/*    */     
/* 27 */     double[] possibleEyeHeights = this.player.getPossibleEyeHeights();
/* 28 */     double minEyeHeight = Double.MAX_VALUE;
/* 29 */     double maxEyeHeight = Double.MIN_VALUE;
/* 30 */     for (double height : possibleEyeHeights) {
/* 31 */       minEyeHeight = Math.min(minEyeHeight, height);
/* 32 */       maxEyeHeight = Math.max(maxEyeHeight, height);
/*    */     } 
/*    */     
/* 35 */     SimpleCollisionBox eyePositions = new SimpleCollisionBox(this.player.x, this.player.y + minEyeHeight, this.player.z, this.player.x, this.player.y + maxEyeHeight, this.player.z);
/* 36 */     if (!this.player.packetStateData.didLastMovementIncludePosition || this.player.canSkipTicks()) {
/* 37 */       eyePositions.expand(this.player.getMovementThreshold());
/*    */     }
/*    */ 
/*    */     
/* 41 */     if (eyePositions.isIntersected(combined)) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 47 */     switch (blockBreak.face) { case NORTH:
/* 48 */         if (eyePositions.minZ > combined.minZ);
/* 49 */       case SOUTH: if (eyePositions.maxZ < combined.maxZ);
/* 50 */       case EAST: if (eyePositions.maxX < combined.maxX);
/* 51 */       case WEST: if (eyePositions.minX > combined.minX);
/* 52 */       case UP: if (eyePositions.maxY < combined.maxY);
/* 53 */       case DOWN: if (eyePositions.minY > combined.minY);
/* 54 */       default: break; }  boolean flag = false;
/*    */ 
/*    */     
/* 57 */     if (flag && flagAndAlert("action=" + String.valueOf(blockBreak.action) + ", face=" + String.valueOf(blockBreak.face)) && shouldModifyPackets())
/* 58 */       blockBreak.cancel(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\PositionBreakA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
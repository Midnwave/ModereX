/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ @CheckData(name = "PositionPlace", description = "Placed a block against a hidden face")
/*    */ public class PositionPlace extends BlockPlaceCheck {
/*    */   public PositionPlace(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 19 */     if (place.material == StateTypes.SCAFFOLDING || this.player.inVehicle())
/*    */       return; 
/* 21 */     SimpleCollisionBox combined = getCombinedBox(place);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 28 */     double[] possibleEyeHeights = this.player.getPossibleEyeHeights();
/* 29 */     double minEyeHeight = Double.MAX_VALUE;
/* 30 */     double maxEyeHeight = Double.MIN_VALUE;
/* 31 */     for (double height : possibleEyeHeights) {
/* 32 */       minEyeHeight = Math.min(minEyeHeight, height);
/* 33 */       maxEyeHeight = Math.max(maxEyeHeight, height);
/*    */     } 
/*    */ 
/*    */     
/* 37 */     double movementThreshold = (!this.player.packetStateData.didLastMovementIncludePosition || this.player.canSkipTicks()) ? this.player.getMovementThreshold() : 0.0D;
/*    */     
/* 39 */     SimpleCollisionBox eyePositions = new SimpleCollisionBox(this.player.x, this.player.y + minEyeHeight, this.player.z, this.player.x, this.player.y + maxEyeHeight, this.player.z);
/* 40 */     eyePositions.expand(movementThreshold);
/*    */ 
/*    */     
/* 43 */     if (eyePositions.isIntersected(combined)) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 49 */     switch (place.getFace()) { case NORTH:
/* 50 */         if (eyePositions.minZ > combined.minZ);
/* 51 */       case SOUTH: if (eyePositions.maxZ < combined.maxZ);
/* 52 */       case EAST: if (eyePositions.maxX < combined.maxX);
/* 53 */       case WEST: if (eyePositions.minX > combined.minX);
/* 54 */       case UP: if (eyePositions.maxY < combined.maxY);
/* 55 */       case DOWN: if (eyePositions.minY > combined.minY);
/* 56 */       default: break; }  boolean flag = false;
/*    */ 
/*    */     
/* 59 */     if (flag && flagAndAlert() && shouldModifyPackets() && shouldCancel())
/* 60 */       place.resync(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\PositionPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
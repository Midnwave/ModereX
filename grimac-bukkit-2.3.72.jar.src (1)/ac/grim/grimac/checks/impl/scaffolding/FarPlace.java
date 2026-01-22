/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.math.VectorUtils;
/*    */ 
/*    */ @CheckData(name = "FarPlace", description = "Placing blocks from too far away")
/*    */ public class FarPlace extends BlockPlaceCheck {
/*    */   public FarPlace(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 23 */     if (this.player.gamemode == GameMode.SPECTATOR || this.player.inVehicle())
/*    */       return; 
/* 25 */     Vector3i blockPos = place.position;
/*    */     
/* 27 */     if (place.material == StateTypes.SCAFFOLDING)
/*    */       return; 
/* 29 */     double min = Double.MAX_VALUE;
/* 30 */     double[] possibleEyeHeights = this.player.getPossibleEyeHeights();
/* 31 */     for (double d : possibleEyeHeights) {
/* 32 */       SimpleCollisionBox box = new SimpleCollisionBox(blockPos);
/* 33 */       Vector3dm eyes = new Vector3dm(this.player.x, this.player.y + d, this.player.z);
/* 34 */       Vector3dm best = VectorUtils.cutBoxToVector(eyes, box);
/* 35 */       min = Math.min(min, eyes.distanceSquared(best));
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 40 */     double maxReach = this.player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
/* 41 */     double threshold = this.player.getMovementThreshold();
/* 42 */     maxReach += Math.hypot(threshold, threshold);
/*    */     
/* 44 */     if (min > maxReach * maxReach && 
/* 45 */       flagAndAlert() && shouldModifyPackets() && shouldCancel())
/* 46 */       place.resync(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\FarPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
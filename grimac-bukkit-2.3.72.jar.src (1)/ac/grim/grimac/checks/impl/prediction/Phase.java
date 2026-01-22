/*    */ package ac.grim.grimac.checks.impl.prediction;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ @CheckData(name = "Phase", setback = 1.0D, decay = 0.005D)
/*    */ public class Phase
/*    */   extends Check implements PostPredictionCheck {
/*    */   SimpleCollisionBox oldBB;
/*    */   
/*    */   public Phase(GrimPlayer player) {
/* 23 */     super(player);
/* 24 */     this.oldBB = player.boundingBox;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 29 */     if (!(this.player.getSetbackTeleportUtil()).blockOffsets && !predictionComplete.getData().isTeleport() && predictionComplete.isChecked()) {
/* 30 */       SimpleCollisionBox newBB = this.player.boundingBox;
/*    */       
/* 32 */       List<SimpleCollisionBox> boxes = new ArrayList<>();
/* 33 */       Collisions.getCollisionBoxes(this.player, newBB, boxes, false);
/*    */       
/* 35 */       for (SimpleCollisionBox box : boxes) {
/* 36 */         if (newBB.isIntersected(box) && !this.oldBB.isIntersected(box)) {
/* 37 */           if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*    */             
/* 39 */             WrappedBlockState state = this.player.compensatedWorld.getBlock((box.minX + box.maxX) / 2.0D, (box.minY + box.maxY) / 2.0D, (box.minZ + box.maxZ) / 2.0D);
/* 40 */             if (BlockTags.ANVIL.contains(state.getType()) || state.getType() == StateTypes.CHEST || state.getType() == StateTypes.TRAPPED_CHEST) {
/*    */               continue;
/*    */             }
/*    */           } 
/* 44 */           flagAndAlertWithSetback();
/*    */           
/*    */           return;
/*    */         } 
/*    */       } 
/*    */     } 
/* 50 */     this.oldBB = this.player.boundingBox;
/* 51 */     reward();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\prediction\Phase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
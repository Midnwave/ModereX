/*    */ package ac.grim.grimac.predictionengine;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ public class GhostBlockDetector extends Check implements PostPredictionCheck {
/*    */   public GhostBlockDetector(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean isGhostBlock(GrimPlayer player) {
/* 19 */     if (player.uncertaintyHandler.isOrWasNearGlitchyBlock) {
/* 20 */       return true;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 27 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
/* 28 */       SimpleCollisionBox largeExpandedBB = player.boundingBox.copy().expand(12.0D, 0.5D, 12.0D);
/*    */       
/* 30 */       for (ObjectIterator<PacketEntity> objectIterator = player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 31 */         if (entity.isBoat && 
/* 32 */           entity.getPossibleCollisionBoxes().isIntersected(largeExpandedBB)) {
/* 33 */           return true;
/*    */         } }
/*    */     
/*    */     } 
/*    */ 
/*    */     
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 46 */     if (predictionComplete.getOffset() < 0.001D && (this.player.clientClaimsLastOnGround == this.player.onGround || this.player.inVehicle())) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 51 */     boolean shouldResync = isGhostBlock(this.player);
/*    */     
/* 53 */     if (shouldResync) {
/*    */       
/* 55 */       if (this.player.clientClaimsLastOnGround != this.player.onGround)
/*    */       {
/*    */         
/* 58 */         this.player.onGround = this.player.clientClaimsLastOnGround;
/*    */       }
/*    */       
/* 61 */       predictionComplete.setOffset(0.0D);
/* 62 */       this.player.getSetbackTeleportUtil().executeForceResync();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\GhostBlockDetector.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
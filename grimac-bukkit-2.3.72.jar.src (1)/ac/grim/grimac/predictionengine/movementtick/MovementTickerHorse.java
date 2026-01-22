/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*    */ 
/*    */ public class MovementTickerHorse
/*    */   extends MovementTickerLivingVehicle {
/*    */   public MovementTickerHorse(GrimPlayer player) {
/* 13 */     super(player);
/*    */     
/* 15 */     PacketEntityHorse horsePacket = (PacketEntityHorse)player.compensatedEntities.self.getRiding();
/*    */     
/* 17 */     if (!horsePacket.hasSaddle())
/*    */       return; 
/* 19 */     player.speed = horsePacket.getAttributeValue(Attributes.MOVEMENT_SPEED) + getExtraSpeed();
/*    */ 
/*    */     
/* 22 */     float horizInput = player.vehicleData.vehicleHorizontal * 0.5F;
/* 23 */     float forwardsInput = player.vehicleData.vehicleForward;
/*    */     
/* 25 */     if (forwardsInput <= 0.0F) {
/* 26 */       forwardsInput *= 0.25F;
/*    */     }
/*    */     
/* 29 */     this.movementInput = new Vector3dm(horizInput, 0.0F, forwardsInput);
/* 30 */     if (this.movementInput.lengthSquared() > 1.0D) this.movementInput.normalize();
/*    */   
/*    */   }
/*    */   
/*    */   public void livingEntityAIStep() {
/* 35 */     super.livingEntityAIStep();
/* 36 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17))
/* 37 */       Collisions.handleInsideBlocks(this.player); 
/*    */   }
/*    */   
/*    */   public float getExtraSpeed() {
/* 41 */     return 0.0F;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerHorse.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
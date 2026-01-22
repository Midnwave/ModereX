/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.rideable.PredictionEngineHappyGhast;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHappyGhast;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public class MovementTickerHappyGhast
/*    */   extends MovementTickerLivingVehicle {
/*    */   public MovementTickerHappyGhast(GrimPlayer player) {
/* 12 */     super(player);
/*    */     
/* 14 */     PacketEntityHappyGhast happyGhastPacket = (PacketEntityHappyGhast)player.compensatedEntities.self.getRiding();
/* 15 */     if (!happyGhastPacket.isControllingPassenger())
/*    */       return; 
/* 17 */     player.speed = ((float)happyGhastPacket.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F);
/*    */ 
/*    */     
/* 20 */     float sideways = player.vehicleData.vehicleHorizontal;
/* 21 */     float forward = 0.0F;
/* 22 */     float upAndDown = 0.0F;
/* 23 */     if (player.vehicleData.vehicleForward != 0.0F) {
/* 24 */       float xRot = player.yRot * 2.0F;
/* 25 */       float calcForward = player.trigHandler.cos(xRot * 0.017453292F);
/* 26 */       float calcUpAndDown = -player.trigHandler.sin(xRot * 0.017453292F);
/* 27 */       if (player.vehicleData.vehicleForward < 0.0F) {
/* 28 */         calcForward *= -0.5F;
/* 29 */         calcUpAndDown *= -0.5F;
/*    */       } 
/*    */       
/* 32 */       upAndDown = calcUpAndDown;
/* 33 */       forward = calcForward;
/*    */     } 
/*    */     
/* 36 */     if (player.lastJumping) {
/* 37 */       upAndDown += 0.5F;
/*    */     }
/*    */     
/* 40 */     this.movementInput = (new Vector3dm(sideways, upAndDown, forward)).multiply(3.9000000953674316D * happyGhastPacket.getAttributeValue(Attributes.FLYING_SPEED));
/*    */   }
/*    */ 
/*    */   
/*    */   public void doNormalMove(float blockFriction) {
/* 45 */     PacketEntityHappyGhast happyGhastPacket = (PacketEntityHappyGhast)this.player.compensatedEntities.self.getRiding();
/* 46 */     float flyingSpeed = (float)happyGhastPacket.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F;
/* 47 */     (new PredictionEngineHappyGhast(this.movementInput, 0.9100000262260437D)).guessBestMovement(flyingSpeed, this.player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doLavaMove() {
/* 52 */     PacketEntityHappyGhast happyGhastPacket = (PacketEntityHappyGhast)this.player.compensatedEntities.self.getRiding();
/* 53 */     float flyingSpeed = (float)happyGhastPacket.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F;
/* 54 */     (new PredictionEngineHappyGhast(this.movementInput, 0.5D)).guessBestMovement(flyingSpeed, this.player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {
/* 59 */     PacketEntityHappyGhast happyGhastPacket = (PacketEntityHappyGhast)this.player.compensatedEntities.self.getRiding();
/* 60 */     float flyingSpeed = (float)happyGhastPacket.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F;
/* 61 */     (new PredictionEngineHappyGhast(this.movementInput, 0.800000011920929D)).guessBestMovement(flyingSpeed, this.player);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerHappyGhast.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
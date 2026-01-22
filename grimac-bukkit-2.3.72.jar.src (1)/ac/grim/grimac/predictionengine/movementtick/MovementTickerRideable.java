/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
/*    */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*    */ 
/*    */ public class MovementTickerRideable
/*    */   extends MovementTickerLivingVehicle {
/*    */   public MovementTickerRideable(GrimPlayer player) {
/* 11 */     super(player);
/*    */ 
/*    */     
/* 14 */     float f = getSteeringSpeed();
/*    */     
/* 16 */     PacketEntityRideable boost = (PacketEntityRideable)player.compensatedEntities.self.getRiding();
/*    */ 
/*    */     
/* 19 */     if (boost.currentBoostTime++ < boost.boostTimeMax)
/*    */     {
/* 21 */       f += f * 1.15F * player.trigHandler.sin(boost.currentBoostTime / boost.boostTimeMax * 3.1415927F);
/*    */     }
/*    */     
/* 24 */     player.speed = f;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public float getSteeringSpeed() {
/* 30 */     throw new IllegalStateException("Not implemented");
/*    */   }
/*    */ 
/*    */   
/*    */   public void livingEntityTravel() {
/* 35 */     super.livingEntityTravel();
/* 36 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17))
/* 37 */       Collisions.handleInsideBlocks(this.player); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerRideable.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
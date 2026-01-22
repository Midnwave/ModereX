/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityCamel;
/*    */ 
/*    */ public class MovementTickerCamel
/*    */   extends MovementTickerHorse {
/*    */   public MovementTickerCamel(GrimPlayer player) {
/*  9 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public float getExtraSpeed() {
/* 14 */     PacketEntityCamel camel = (PacketEntityCamel)this.player.compensatedEntities.self.getRiding();
/*    */ 
/*    */ 
/*    */     
/* 18 */     boolean wantsToJump = (this.player.vehicleData.horseJump > 0.0F && !this.player.vehicleData.horseJumping && this.player.lastOnGround);
/* 19 */     if (wantsToJump) return 0.0F;
/*    */     
/* 21 */     return (this.player.isSprinting && this.player.vehicleData.camelDashCooldown <= 0 && !camel.dashing) ? 0.1F : 0.0F;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerCamel.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
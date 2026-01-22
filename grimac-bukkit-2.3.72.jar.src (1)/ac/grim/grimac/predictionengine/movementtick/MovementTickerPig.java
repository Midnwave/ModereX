/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public class MovementTickerPig extends MovementTickerRideable {
/*    */   public MovementTickerPig(GrimPlayer player) {
/* 10 */     super(player);
/* 11 */     this.movementInput = new Vector3dm(0, 0, 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public float getSteeringSpeed() {
/* 16 */     PacketEntityRideable pig = (PacketEntityRideable)this.player.compensatedEntities.self.getRiding();
/* 17 */     return (float)pig.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225F;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerPig.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
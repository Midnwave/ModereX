/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityTrackXRot
/*    */   extends PacketEntity
/*    */ {
/*    */   public float packetYaw;
/*    */   public float interpYaw;
/* 12 */   public int steps = 0;
/*    */   
/*    */   public PacketEntityTrackXRot(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, float xRot) {
/* 15 */     super(player, uuid, type, x, y, z);
/* 16 */     this.packetYaw = xRot;
/* 17 */     this.interpYaw = xRot;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onMovement(boolean highBound) {
/* 22 */     super.onMovement(highBound);
/* 23 */     if (this.steps > 0)
/* 24 */       this.interpYaw += (this.packetYaw - this.interpYaw) / this.steps--; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityTrackXRot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
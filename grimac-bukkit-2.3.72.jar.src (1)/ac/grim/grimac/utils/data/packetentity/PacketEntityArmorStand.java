/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityArmorStand
/*    */   extends PacketEntity
/*    */ {
/*    */   public boolean isMarker = false;
/*    */   
/*    */   public PacketEntityArmorStand(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, int extraData) {
/* 13 */     super(player, uuid, type, x, y, z);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canHit() {
/* 18 */     return (!this.isMarker && super.canHit());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityArmorStand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
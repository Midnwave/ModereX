/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityStrider
/*    */   extends PacketEntityRideable {
/*    */   public boolean isShaking = false;
/*    */   
/*    */   public PacketEntityStrider(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
/* 12 */     super(player, uuid, type, x, y, z);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityStrider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
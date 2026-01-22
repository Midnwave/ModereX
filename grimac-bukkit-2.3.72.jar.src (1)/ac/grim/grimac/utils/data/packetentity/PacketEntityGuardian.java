/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ public class PacketEntityGuardian
/*    */   extends PacketEntity
/*    */ {
/*    */   public boolean isElder;
/*    */   
/*    */   public PacketEntityGuardian(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, boolean isElder) {
/* 14 */     super(player, uuid, type, x, y, z);
/* 15 */     this.isElder = isElder;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityGuardian.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
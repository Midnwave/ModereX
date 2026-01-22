/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityHook
/*    */   extends PacketEntityUnHittable {
/*    */   public int owner;
/* 10 */   public int attached = -1;
/*    */   
/*    */   public PacketEntityHook(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, int owner) {
/* 13 */     super(player, uuid, type, x, y, z);
/* 14 */     this.owner = owner;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityHook.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
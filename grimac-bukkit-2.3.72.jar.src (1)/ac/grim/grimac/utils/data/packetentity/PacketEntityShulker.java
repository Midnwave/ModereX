/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityShulker
/*    */   extends PacketEntity {
/* 10 */   public BlockFace facing = BlockFace.DOWN;
/*    */   
/*    */   public PacketEntityShulker(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
/* 13 */     super(player, uuid, type, x, y, z);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityShulker.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
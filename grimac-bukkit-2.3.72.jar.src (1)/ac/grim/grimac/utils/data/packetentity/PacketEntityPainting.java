/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Direction;
/*    */ import java.util.UUID;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class PacketEntityPainting
/*    */   extends PacketEntity {
/*    */   @Generated
/*    */   public Direction getDirection() {
/* 13 */     return this.direction;
/*    */   } private final Direction direction;
/*    */   public PacketEntityPainting(GrimPlayer player, UUID uuid, double x, double y, double z, Direction direction) {
/* 16 */     super(player, uuid, EntityTypes.PAINTING, x, y, z);
/* 17 */     this.direction = direction;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityPainting.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
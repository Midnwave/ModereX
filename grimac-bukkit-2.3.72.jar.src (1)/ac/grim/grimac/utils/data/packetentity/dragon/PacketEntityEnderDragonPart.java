/*    */ package ac.grim.grimac.utils.data.packetentity.dragon;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ public final class PacketEntityEnderDragonPart
/*    */   extends PacketEntity {
/*    */   public final DragonPart part;
/*    */   
/*    */   public PacketEntityEnderDragonPart(GrimPlayer player, DragonPart part, double x, double y, double z, float width, float height) {
/* 12 */     super(player, null, EntityTypes.ENDER_DRAGON, x, y, z);
/* 13 */     this.part = part;
/* 14 */     this.width = width;
/* 15 */     this.height = height;
/*    */   }
/*    */   
/*    */   public final float width;
/*    */   public final float height;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\dragon\PacketEntityEnderDragonPart.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
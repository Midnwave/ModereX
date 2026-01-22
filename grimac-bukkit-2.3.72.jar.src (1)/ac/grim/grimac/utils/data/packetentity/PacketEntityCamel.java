/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityCamel
/*    */   extends PacketEntityHorse
/*    */ {
/*    */   public boolean dashing = false;
/*    */   
/*    */   public PacketEntityCamel(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, float xRot) {
/* 14 */     super(player, uuid, type, x, y, z, xRot);
/*    */     
/* 16 */     setAttribute(Attributes.JUMP_STRENGTH, 0.41999998688697815D);
/* 17 */     setAttribute(Attributes.MOVEMENT_SPEED, 0.09000000357627869D);
/* 18 */     setAttribute(Attributes.STEP_HEIGHT, 1.5D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityCamel.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
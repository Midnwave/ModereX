/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
/*    */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityHappyGhast
/*    */   extends PacketEntityTrackXRot
/*    */ {
/*    */   public PacketEntityHappyGhast(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, float xRot) {
/* 14 */     super(player, uuid, type, x, y, z, xRot);
/* 15 */     this.trackEntityEquipment = true;
/*    */     
/* 17 */     trackAttribute(ValuedAttribute.ranged(Attributes.FLYING_SPEED, 0.05D, 0.0D, 1024.0D));
/* 18 */     trackAttribute(ValuedAttribute.ranged(Attributes.MOVEMENT_SPEED, 0.05D, 0.0D, 1024.0D));
/*    */   }
/*    */   
/*    */   public boolean isControllingPassenger() {
/* 22 */     return (isWearingBodyArmor() && getFirstPassenger() instanceof PacketEntitySelf);
/*    */   }
/*    */   
/*    */   public boolean isWearingBodyArmor() {
/* 26 */     return hasItemInSlot(EquipmentSlot.BODY);
/*    */   }
/*    */   
/*    */   public PacketEntity getFirstPassenger() {
/* 30 */     return this.passengers.isEmpty() ? null : this.passengers.get(0);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityHappyGhast.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
/*    */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class PacketEntityHorse
/*    */   extends PacketEntityTrackXRot
/*    */ {
/*    */   public boolean isRearing = false;
/*    */   public boolean hasSaddle = false;
/*    */   public boolean isTame = false;
/*    */   
/*    */   public PacketEntityHorse(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z, float xRot) {
/* 22 */     super(player, uuid, type, x, y, z, xRot);
/* 23 */     this.trackEntityEquipment = true;
/* 24 */     setAttribute(Attributes.STEP_HEIGHT, 1.0D);
/*    */     
/* 26 */     boolean preAttribute = player.getClientVersion().isOlderThan(ClientVersion.V_1_20_5);
/*    */     
/* 28 */     trackAttribute(ValuedAttribute.ranged(Attributes.JUMP_STRENGTH, 0.7D, 0.0D, preAttribute ? 2.0D : 32.0D)
/* 29 */         .withSetRewriter((oldValue, newValue) -> 
/*    */           
/* 31 */           (preAttribute && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) ? oldValue : newValue));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 37 */     trackAttribute(ValuedAttribute.ranged(Attributes.MOVEMENT_SPEED, 0.22499999403953552D, 0.0D, 1024.0D));
/*    */     
/* 39 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.CHESTED_HORSE)) {
/* 40 */       setAttribute(Attributes.JUMP_STRENGTH, 0.5D);
/* 41 */       setAttribute(Attributes.MOVEMENT_SPEED, 0.17499999701976776D);
/*    */     } 
/*    */     
/* 44 */     if (type == EntityTypes.ZOMBIE_HORSE || type == EntityTypes.SKELETON_HORSE) {
/* 45 */       setAttribute(Attributes.MOVEMENT_SPEED, 0.20000000298023224D);
/*    */     }
/*    */   }
/*    */   
/* 49 */   private static final boolean HAS_SADDLE_SENT_BY_SERVER = PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_21_4);
/*    */   public boolean hasSaddle() {
/* 51 */     if (HAS_SADDLE_SENT_BY_SERVER) {
/* 52 */       return this.hasSaddle;
/*    */     }
/*    */     
/* 55 */     return hasItemInSlot(EquipmentSlot.SADDLE);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntityHorse.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
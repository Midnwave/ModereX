/*    */ package ac.grim.grimac.utils.data.packetentity;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ 
/*    */ public abstract class TypedPacketEntity {
/*    */   public final EntityType type;
/*    */   public final boolean isLivingEntity;
/*    */   
/*    */   public TypedPacketEntity(EntityType type) {
/* 11 */     this.type = type;
/* 12 */     this.isLivingEntity = EntityTypes.isTypeInstanceOf(type, EntityTypes.LIVINGENTITY);
/* 13 */     this.isMinecart = EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT);
/* 14 */     this.isHorse = EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_HORSE);
/*    */     
/* 16 */     this
/*    */ 
/*    */       
/* 19 */       .isAgeable = ((EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_AGEABLE) && !EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_PARROT) && type != EntityTypes.FROG) || EntityTypes.isTypeInstanceOf(type, EntityTypes.ZOMBIE) || EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_PIGLIN) || type == EntityTypes.ZOGLIN);
/*    */     
/* 21 */     this.isAnimal = EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_ANIMAL);
/* 22 */     this.isBoat = EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT);
/* 23 */     this.isHappyGhast = EntityTypes.HAPPY_GHAST.equals(type);
/*    */   }
/*    */   public final boolean isMinecart; public final boolean isHorse; public final boolean isAgeable; public final boolean isAnimal;
/*    */   public final boolean isBoat;
/*    */   public final boolean isHappyGhast;
/*    */   
/*    */   public boolean isPushable() {
/* 30 */     if (this.type == EntityTypes.ARMOR_STAND || this.type == EntityTypes.BAT || this.type == EntityTypes.PARROT)
/* 31 */       return false; 
/* 32 */     return (this.isLivingEntity || this.isBoat || this.isMinecart);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\TypedPacketEntity.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
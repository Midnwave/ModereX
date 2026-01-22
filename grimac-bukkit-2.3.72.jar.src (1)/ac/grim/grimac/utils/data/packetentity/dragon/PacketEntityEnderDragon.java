/*    */ package ac.grim.grimac.utils.data.packetentity.dragon;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.fastutil.ints.Int2ObjectOpenHashMap;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.UUID;
/*    */ import lombok.Generated;
/*    */ 
/*    */ 
/*    */ public final class PacketEntityEnderDragon
/*    */   extends PacketEntity
/*    */ {
/* 16 */   private final List<PacketEntityEnderDragonPart> parts = new ArrayList<>(); @Generated public List<PacketEntityEnderDragonPart> getParts() { return this.parts; }
/*    */   
/*    */   public PacketEntityEnderDragon(GrimPlayer player, UUID uuid, int entityID, double x, double y, double z) {
/* 19 */     super(player, uuid, EntityTypes.ENDER_DRAGON, x, y, z);
/* 20 */     Int2ObjectOpenHashMap<PacketEntity> entityMap = player.compensatedEntities.entityMap;
/* 21 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.HEAD, x, y, z, 1.0F, 1.0F));
/* 22 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.NECK, x, y, z, 3.0F, 3.0F));
/* 23 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.BODY, x, y, z, 5.0F, 3.0F));
/* 24 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
/* 25 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
/* 26 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
/* 27 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.WING, x, y, z, 4.0F, 2.0F));
/* 28 */     this.parts.add(new PacketEntityEnderDragonPart(player, DragonPart.WING, x, y, z, 4.0F, 2.0F));
/* 29 */     for (int i = 1; i < this.parts.size() + 1; i++)
/* 30 */       entityMap.put(entityID + i, this.parts.get(i - 1)); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\dragon\PacketEntityEnderDragon.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
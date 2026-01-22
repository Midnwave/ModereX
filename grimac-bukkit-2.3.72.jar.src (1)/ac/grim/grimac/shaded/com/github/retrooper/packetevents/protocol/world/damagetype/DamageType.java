/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.damagetype;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureIndexUtil;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Optional;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface DamageType
/*    */   extends MappedEntity, CopyableEntity<DamageType>, DeepComparableEntity
/*    */ {
/*    */   static DamageType decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 48 */     NBTCompound compound = (NBTCompound)nbt;
/* 49 */     String messageId = ((NBTCompound)nbt).getStringTagValueOrThrow("message_id");
/* 50 */     DamageScaling scaling = (DamageScaling)AdventureIndexUtil.indexValueOrThrow(DamageScaling.ID_INDEX, ((NBTCompound)nbt)
/* 51 */         .getStringTagValueOrThrow("scaling"));
/* 52 */     float exhaustion = ((NBTCompound)nbt).getNumberTagOrThrow("exhaustion").getAsFloat();
/*    */     
/* 54 */     DamageEffects effects = Optional.<String>ofNullable(compound.getStringTagValueOrNull("effects")).map(id -> (DamageEffects)AdventureIndexUtil.indexValueOrThrow(DamageEffects.ID_INDEX, id)).orElse(DamageEffects.HURT);
/*    */     
/* 56 */     DeathMessageType deathMessageType = Optional.<String>ofNullable(compound.getStringTagValueOrNull("death_message_type")).map(id -> (DeathMessageType)AdventureIndexUtil.indexValueOrThrow(DeathMessageType.ID_INDEX, id)).orElse(DeathMessageType.DEFAULT);
/*    */     
/* 58 */     return new StaticDamageType(data, messageId, scaling, exhaustion, effects, deathMessageType);
/*    */   }
/*    */   
/*    */   static NBT encode(DamageType damageType, ClientVersion version) {
/* 62 */     NBTCompound compound = new NBTCompound();
/* 63 */     compound.setTag("message_id", (NBT)new NBTString(damageType.getMessageId()));
/* 64 */     compound.setTag("scaling", (NBT)new NBTString(damageType.getScaling().getId()));
/* 65 */     compound.setTag("exhaustion", (NBT)new NBTFloat(damageType.getExhaustion()));
/*    */     
/* 67 */     if (damageType.getEffects() != DamageEffects.HURT) {
/* 68 */       compound.setTag("effects", (NBT)new NBTString(damageType.getEffects().getId()));
/*    */     }
/*    */     
/* 71 */     if (damageType.getDeathMessageType() != DeathMessageType.DEFAULT) {
/* 72 */       compound.setTag("death_message_type", (NBT)new NBTString(damageType.getDeathMessageType().getId()));
/*    */     }
/*    */     
/* 75 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   String getMessageId();
/*    */   
/*    */   DamageScaling getScaling();
/*    */   
/*    */   float getExhaustion();
/*    */   
/*    */   DamageEffects getEffects();
/*    */   
/*    */   DeathMessageType getDeathMessageType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\damagetype\DamageType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
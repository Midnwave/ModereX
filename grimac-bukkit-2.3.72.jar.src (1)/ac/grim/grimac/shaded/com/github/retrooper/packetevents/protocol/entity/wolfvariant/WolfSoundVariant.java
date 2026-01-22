/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public interface WolfSoundVariant
/*    */   extends MappedEntity, CopyableEntity<WolfSoundVariant>, DeepComparableEntity
/*    */ {
/*    */   Sound getAmbientSound();
/*    */   
/*    */   Sound getDeathSound();
/*    */   
/*    */   Sound getGrowlSound();
/*    */   
/*    */   Sound getHurtSound();
/*    */   
/*    */   Sound getPantSound();
/*    */   
/*    */   Sound getWhineSound();
/*    */   
/*    */   static WolfSoundVariant read(PacketWrapper<?> wrapper) {
/* 47 */     return (WolfSoundVariant)wrapper.readMappedEntity((IRegistry)WolfSoundVariants.getRegistry());
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, WolfSoundVariant variant) {
/* 51 */     wrapper.writeMappedEntity(variant);
/*    */   }
/*    */   
/*    */   static WolfSoundVariant decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 55 */     NBTCompound compound = (NBTCompound)nbt;
/* 56 */     Sound ambientSound = Sound.decode(compound.getTagOrThrow("ambient_sound"), version);
/* 57 */     Sound deathSound = Sound.decode(compound.getTagOrThrow("death_sound"), version);
/* 58 */     Sound growlSound = Sound.decode(compound.getTagOrThrow("growl_sound"), version);
/* 59 */     Sound hurtSound = Sound.decode(compound.getTagOrThrow("hurt_sound"), version);
/* 60 */     Sound pantSound = Sound.decode(compound.getTagOrThrow("pant_sound"), version);
/* 61 */     Sound whineSound = Sound.decode(compound.getTagOrThrow("whine_sound"), version);
/* 62 */     return new StaticWolfSoundVariant(data, ambientSound, deathSound, growlSound, hurtSound, pantSound, whineSound);
/*    */   }
/*    */   
/*    */   static NBT encode(WolfSoundVariant variant, ClientVersion version) {
/* 66 */     NBTCompound compound = new NBTCompound();
/* 67 */     compound.setTag("ambient_sound", Sound.encode(variant.getAmbientSound(), version));
/* 68 */     compound.setTag("death_sound", Sound.encode(variant.getDeathSound(), version));
/* 69 */     compound.setTag("growl_sound", Sound.encode(variant.getGrowlSound(), version));
/* 70 */     compound.setTag("hurt_sound", Sound.encode(variant.getHurtSound(), version));
/* 71 */     compound.setTag("pant_sound", Sound.encode(variant.getPantSound(), version));
/* 72 */     compound.setTag("whine_sound", Sound.encode(variant.getWhineSound(), version));
/* 73 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\wolfvariant\WolfSoundVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
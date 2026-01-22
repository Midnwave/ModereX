/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public interface Sound
/*    */   extends MappedEntity
/*    */ {
/*    */   ResourceLocation getSoundId();
/*    */   
/*    */   @Nullable
/*    */   Float getRange();
/*    */   
/*    */   static Sound read(PacketWrapper<?> wrapper) {
/* 42 */     return (Sound)wrapper.readMappedEntityOrDirect(Sounds::getById, Sound::readDirect);
/*    */   }
/*    */   
/*    */   static Sound readDirect(PacketWrapper<?> wrapper) {
/* 46 */     ResourceLocation soundId = wrapper.readIdentifier();
/* 47 */     Float range = (Float)wrapper.readOptional(PacketWrapper::readFloat);
/* 48 */     return new StaticSound(soundId, range);
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, Sound sound) {
/* 52 */     wrapper.writeMappedEntityOrDirect(sound, Sound::writeDirect);
/*    */   }
/*    */   
/*    */   static void writeDirect(PacketWrapper<?> wrapper, Sound sound) {
/* 56 */     wrapper.writeIdentifier(sound.getSoundId());
/* 57 */     wrapper.writeOptional(sound.getRange(), PacketWrapper::writeFloat);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   static Sound decode(NBT nbt, ClientVersion version) {
/* 62 */     return decode(nbt, PacketWrapper.createDummyWrapper(version));
/*    */   }
/*    */   
/*    */   static Sound decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 66 */     if (nbt instanceof NBTString) {
/* 67 */       return Sounds.getByNameOrCreate(((NBTString)nbt).getValue());
/*    */     }
/* 69 */     NBTCompound compound = (NBTCompound)nbt;
/* 70 */     ResourceLocation soundId = new ResourceLocation(((NBTCompound)nbt).getStringTagValueOrThrow("sound_id"));
/*    */     
/* 72 */     Float range = Optional.<NBTNumber>ofNullable(compound.getNumberTagOrNull("range")).map(NBTNumber::getAsFloat).orElse(null);
/* 73 */     return new StaticSound(soundId, range);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   static NBT encode(Sound sound, ClientVersion version) {
/* 78 */     return encode(PacketWrapper.createDummyWrapper(version), sound);
/*    */   }
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, Sound sound) {
/* 82 */     if (sound.isRegistered()) {
/* 83 */       return (NBT)new NBTString(sound.getName().toString());
/*    */     }
/* 85 */     NBTCompound compound = new NBTCompound();
/* 86 */     compound.setTag("sound_id", (NBT)new NBTString(sound.getSoundId().toString()));
/* 87 */     if (sound.getRange() != null) {
/* 88 */       compound.setTag("range", (NBT)new NBTFloat(sound.getRange().floatValue()));
/*    */     }
/* 90 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\sound\Sound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
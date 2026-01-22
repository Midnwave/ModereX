/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ public interface IJukeboxSong
/*    */   extends MappedEntity, CopyableEntity<IJukeboxSong>, DeepComparableEntity
/*    */ {
/*    */   Sound getSound();
/*    */   
/*    */   Component getDescription();
/*    */   
/*    */   float getLengthInSeconds();
/*    */   
/*    */   int getComparatorOutput();
/*    */   
/*    */   @Deprecated
/*    */   static IJukeboxSong decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 47 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*    */   }
/*    */   
/*    */   static IJukeboxSong decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/* 51 */     NBTCompound compound = (NBTCompound)nbt;
/* 52 */     Sound sound = (Sound)compound.getOrThrow("sound_event", Sound::decode, wrapper);
/* 53 */     Component description = (Component)compound.getOrThrow("description", (NbtDecoder)wrapper.getSerializers(), wrapper);
/* 54 */     float length = compound.getNumberTagOrThrow("length_in_seconds").getAsFloat();
/* 55 */     int comparator_output = compound.getNumberTagOrThrow("comparator_output").getAsInt();
/* 56 */     return new JukeboxSong(data, sound, description, length, comparator_output);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   static NBT encode(IJukeboxSong jukeboxSong, ClientVersion version) {
/* 61 */     return encode(PacketWrapper.createDummyWrapper(version), jukeboxSong);
/*    */   }
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, IJukeboxSong song) {
/* 65 */     NBTCompound compound = new NBTCompound();
/* 66 */     compound.set("sound_event", song.getSound(), Sound::encode, wrapper);
/* 67 */     compound.set("description", song.getDescription(), (NbtEncoder)wrapper.getSerializers(), wrapper);
/* 68 */     compound.setTag("length_in_seconds", (NBT)new NBTFloat(song.getLengthInSeconds()));
/* 69 */     compound.setTag("comparator_output", (NBT)new NBTInt(song.getComparatorOutput()));
/* 70 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   static IJukeboxSong read(PacketWrapper<?> wrapper) {
/* 74 */     return (IJukeboxSong)wrapper.readMappedEntityOrDirect((IRegistry)JukeboxSongs.getRegistry(), IJukeboxSong::readDirect);
/*    */   }
/*    */   
/*    */   static IJukeboxSong readDirect(PacketWrapper<?> wrapper) {
/* 78 */     Sound sound = Sound.read(wrapper);
/* 79 */     Component description = wrapper.readComponent();
/* 80 */     float lengthInSeconds = wrapper.readFloat();
/* 81 */     int comparatorOutput = wrapper.readVarInt();
/*    */     
/* 83 */     return new JukeboxSong(null, sound, description, lengthInSeconds, comparatorOutput);
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, IJukeboxSong song) {
/* 87 */     wrapper.writeMappedEntityOrDirect(song, IJukeboxSong::writeDirect);
/*    */   }
/*    */   
/*    */   static void writeDirect(PacketWrapper<?> wrapper, IJukeboxSong song) {
/* 91 */     Sound.write(wrapper, song.getSound());
/* 92 */     wrapper.writeComponent(song.getDescription());
/* 93 */     wrapper.writeFloat(song.getLengthInSeconds());
/* 94 */     wrapper.writeVarInt(song.getComparatorOutput());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\jukebox\IJukeboxSong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
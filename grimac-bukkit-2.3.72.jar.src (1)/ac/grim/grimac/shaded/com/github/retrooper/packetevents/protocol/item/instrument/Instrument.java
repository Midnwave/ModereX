/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.MathUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Instrument
/*     */   extends MappedEntity, CopyableEntity<Instrument>, DeepComparableEntity
/*     */ {
/*     */   Sound getSound();
/*     */   
/*     */   float getUseSeconds();
/*     */   
/*     */   default int getUseDuration() {
/*  46 */     return MathUtil.floor(getUseSeconds() * 20.0F);
/*     */   }
/*     */   
/*     */   float getRange();
/*     */   
/*     */   Component getDescription();
/*     */   
/*     */   static Instrument read(PacketWrapper<?> wrapper) {
/*  54 */     return (Instrument)wrapper.readMappedEntityOrDirect((IRegistry)Instruments.getRegistry(), Instrument::readDirect);
/*     */   }
/*     */   
/*     */   static Instrument readDirect(PacketWrapper<?> wrapper) {
/*  58 */     Sound sound = Sound.read(wrapper);
/*     */     
/*  60 */     float useSeconds = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2) ? wrapper.readFloat() : (wrapper.readVarInt() * 20.0F);
/*  61 */     float range = wrapper.readFloat();
/*     */     
/*  63 */     Component description = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2) ? wrapper.readComponent() : (Component)Component.empty();
/*  64 */     return new StaticInstrument(sound, useSeconds, range, description);
/*     */   }
/*     */   
/*     */   static void write(PacketWrapper<?> wrapper, Instrument instrument) {
/*  68 */     wrapper.writeMappedEntityOrDirect(instrument, Instrument::writeDirect);
/*     */   }
/*     */   
/*     */   static void writeDirect(PacketWrapper<?> wrapper, Instrument instrument) {
/*  72 */     Sound.write(wrapper, instrument.getSound());
/*  73 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  74 */       wrapper.writeFloat(instrument.getUseSeconds());
/*     */     } else {
/*  76 */       wrapper.writeVarInt(instrument.getUseDuration());
/*     */     } 
/*  78 */     wrapper.writeFloat(instrument.getRange());
/*  79 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  80 */       wrapper.writeComponent(instrument.getDescription());
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static Instrument decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/*  86 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*     */   }
/*     */   
/*     */   static Instrument decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/*  90 */     NBTCompound compound = (NBTCompound)nbt;
/*  91 */     Sound sound = (Sound)compound.getOrThrow("sound_event", Sound::decode, wrapper);
/*  92 */     float useSeconds = compound.getNumberTagOrThrow("use_duration").getAsFloat();
/*  93 */     float range = compound.getNumberTagOrThrow("range").getAsFloat();
/*  94 */     Component description = (Component)compound.getOrThrow("description", (NbtDecoder)wrapper.getSerializers(), wrapper);
/*  95 */     return new StaticInstrument(data, sound, useSeconds, range, description);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static NBT encode(Instrument instrument, ClientVersion version) {
/* 100 */     return encode(PacketWrapper.createDummyWrapper(version), instrument);
/*     */   }
/*     */   
/*     */   static NBT encode(PacketWrapper<?> wrapper, Instrument instrument) {
/* 104 */     NBTCompound compound = new NBTCompound();
/* 105 */     compound.set("sound_event", instrument.getSound(), Sound::encode, wrapper);
/* 106 */     compound.setTag("use_duration", (NBT)new NBTFloat(instrument.getUseSeconds()));
/* 107 */     compound.setTag("range", (NBT)new NBTFloat(instrument.getRange()));
/* 108 */     compound.set("description", instrument.getDescription(), (NbtEncoder)wrapper.getSerializers(), wrapper);
/* 109 */     return (NBT)compound;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\instrument\Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
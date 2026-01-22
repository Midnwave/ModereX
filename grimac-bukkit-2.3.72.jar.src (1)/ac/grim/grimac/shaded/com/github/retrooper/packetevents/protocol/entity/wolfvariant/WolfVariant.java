/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biome;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.biome.Biomes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.function.BiFunction;
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
/*     */ public interface WolfVariant
/*     */   extends MappedEntity, CopyableEntity<WolfVariant>, DeepComparableEntity
/*     */ {
/*     */   static WolfVariant read(PacketWrapper<?> wrapper) {
/*  53 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper
/*  54 */       .getServerVersion().isOlderThan(ServerVersion.V_1_21)) {
/*  55 */       return (WolfVariant)wrapper.readMappedEntity((IRegistry)WolfVariants.getRegistry());
/*     */     }
/*  57 */     return (WolfVariant)wrapper.readMappedEntityOrDirect((IRegistry)WolfVariants.getRegistry(), WolfVariant::readDirect);
/*     */   }
/*     */   
/*     */   static void write(PacketWrapper<?> wrapper, WolfVariant variant) {
/*  61 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper
/*  62 */       .getServerVersion().isOlderThan(ServerVersion.V_1_21)) {
/*  63 */       wrapper.writeMappedEntity(variant);
/*     */     } else {
/*  65 */       wrapper.writeMappedEntityOrDirect(variant, WolfVariant::writeDirect);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   static WolfVariant readDirect(PacketWrapper<?> wrapper) {
/*  74 */     ResourceLocation wildTexture = wrapper.readIdentifier();
/*  75 */     ResourceLocation tameTexture = wrapper.readIdentifier();
/*  76 */     ResourceLocation angryTexture = wrapper.readIdentifier();
/*  77 */     MappedEntitySet<Biome> biomes = MappedEntitySet.read(wrapper, (BiFunction)Biomes.getRegistry());
/*  78 */     return new StaticWolfVariant(wildTexture, tameTexture, angryTexture, biomes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   static void writeDirect(PacketWrapper<?> wrapper, WolfVariant variant) {
/*  86 */     wrapper.writeIdentifier(variant.getWildTexture());
/*  87 */     wrapper.writeIdentifier(variant.getTameTexture());
/*  88 */     wrapper.writeIdentifier(variant.getAngryTexture());
/*  89 */     MappedEntitySet.write(wrapper, variant.getBiomes());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static WolfVariant decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/*  94 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*     */   }
/*     */   
/*     */   static WolfVariant decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/*  98 */     NBTCompound compound = (NBTCompound)nbt;
/*  99 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 100 */       ResourceLocation resourceLocation1 = new ResourceLocation(compound.getStringTagValueOrThrow("wild_texture"));
/* 101 */       ResourceLocation resourceLocation2 = new ResourceLocation(compound.getStringTagValueOrThrow("tame_texture"));
/* 102 */       ResourceLocation resourceLocation3 = new ResourceLocation(compound.getStringTagValueOrThrow("angry_texture"));
/* 103 */       MappedEntitySet<Biome> biomes = (MappedEntitySet<Biome>)compound.getOrThrow("biomes", (tag, ew) -> MappedEntitySet.decode(tag, ew, (IRegistry)Biomes.getRegistry()), wrapper);
/*     */       
/* 105 */       return new StaticWolfVariant(data, resourceLocation1, resourceLocation2, resourceLocation3, biomes);
/*     */     } 
/* 107 */     NBTCompound assets = compound.getCompoundTagOrThrow("assets");
/* 108 */     ResourceLocation wildTexture = new ResourceLocation(assets.getStringTagValueOrThrow("wild"));
/* 109 */     ResourceLocation tameTexture = new ResourceLocation(assets.getStringTagValueOrThrow("tame"));
/* 110 */     ResourceLocation angryTexture = new ResourceLocation(assets.getStringTagValueOrThrow("angry"));
/* 111 */     return new StaticWolfVariant(data, wildTexture, tameTexture, angryTexture, 
/* 112 */         MappedEntitySet.createEmpty());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static NBT encode(WolfVariant variant, ClientVersion version) {
/* 117 */     return encode(PacketWrapper.createDummyWrapper(version), variant);
/*     */   }
/*     */   
/*     */   static NBT encode(PacketWrapper<?> wrapper, WolfVariant variant) {
/* 121 */     NBTCompound compound = new NBTCompound();
/* 122 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 123 */       compound.setTag("wild_texture", (NBT)new NBTString(variant.getWildTexture().toString()));
/* 124 */       compound.setTag("tame_texture", (NBT)new NBTString(variant.getTameTexture().toString()));
/* 125 */       compound.setTag("angry_texture", (NBT)new NBTString(variant.getAngryTexture().toString()));
/* 126 */       compound.set("biomes", variant.getBiomes(), MappedEntitySet::encode, wrapper);
/*     */     } else {
/* 128 */       NBTCompound assets = new NBTCompound();
/* 129 */       assets.setTag("wild", (NBT)new NBTString(variant.getWildTexture().toString()));
/* 130 */       assets.setTag("tame", (NBT)new NBTString(variant.getWildTexture().toString()));
/* 131 */       assets.setTag("angry", (NBT)new NBTString(variant.getWildTexture().toString()));
/* 132 */       compound.setTag("assets", (NBT)assets);
/*     */     } 
/* 134 */     return (NBT)compound;
/*     */   }
/*     */   
/*     */   ResourceLocation getWildTexture();
/*     */   
/*     */   ResourceLocation getTameTexture();
/*     */   
/*     */   ResourceLocation getAngryTexture();
/*     */   
/*     */   @Obsolete
/*     */   MappedEntitySet<Biome> getBiomes();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\wolfvariant\WolfVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
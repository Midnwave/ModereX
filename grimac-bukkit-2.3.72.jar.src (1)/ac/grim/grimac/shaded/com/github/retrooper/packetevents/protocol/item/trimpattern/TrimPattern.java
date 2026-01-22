/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimpattern;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*     */ public interface TrimPattern
/*     */   extends MappedEntity, CopyableEntity<TrimPattern>, DeepComparableEntity
/*     */ {
/*     */   ResourceLocation getAssetId();
/*     */   
/*     */   @Obsolete
/*     */   ItemType getTemplateItem();
/*     */   
/*     */   Component getDescription();
/*     */   
/*     */   boolean isDecal();
/*     */   
/*     */   static TrimPattern read(PacketWrapper<?> wrapper) {
/*  54 */     return (TrimPattern)wrapper.readMappedEntityOrDirect((IRegistry)TrimPatterns.getRegistry(), TrimPattern::readDirect);
/*     */   }
/*     */   
/*     */   static TrimPattern readDirect(PacketWrapper<?> wrapper) {
/*  58 */     ResourceLocation assetId = wrapper.readIdentifier();
/*     */     
/*  60 */     ItemType templateItem = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? null : (ItemType)wrapper.readMappedEntity(ItemTypes::getById);
/*  61 */     Component description = wrapper.readComponent();
/*  62 */     boolean decal = wrapper.readBoolean();
/*  63 */     return new StaticTrimPattern(assetId, templateItem, description, decal);
/*     */   }
/*     */   
/*     */   static void write(PacketWrapper<?> wrapper, TrimPattern pattern) {
/*  67 */     wrapper.writeMappedEntityOrDirect(pattern, TrimPattern::writeDirect);
/*     */   }
/*     */   
/*     */   static void writeDirect(PacketWrapper<?> wrapper, TrimPattern pattern) {
/*  71 */     wrapper.writeIdentifier(pattern.getAssetId());
/*  72 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  73 */       wrapper.writeMappedEntity((MappedEntity)pattern.getTemplateItem());
/*     */     }
/*  75 */     wrapper.writeComponent(pattern.getDescription());
/*  76 */     wrapper.writeBoolean(pattern.isDecal());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static TrimPattern decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/*  81 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*     */   }
/*     */   
/*     */   static TrimPattern decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/*  85 */     NBTCompound compound = (NBTCompound)nbt;
/*  86 */     ResourceLocation assetId = new ResourceLocation(compound.getStringTagValueOrThrow("asset_id"));
/*     */     
/*  88 */     ItemType templateItem = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? null : ItemTypes.getByName(compound.getStringTagValueOrThrow("template_item"));
/*  89 */     Component description = (Component)compound.getOrThrow("description", (NbtDecoder)wrapper.getSerializers(), wrapper);
/*  90 */     boolean decal = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2) && compound.getBoolean("decal"));
/*  91 */     return new StaticTrimPattern(data, assetId, templateItem, description, decal);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static NBT encode(TrimPattern pattern, ClientVersion version) {
/*  96 */     return encode(PacketWrapper.createDummyWrapper(version), pattern);
/*     */   }
/*     */   
/*     */   static NBT encode(PacketWrapper<?> wrapper, TrimPattern pattern) {
/* 100 */     NBTCompound compound = new NBTCompound();
/* 101 */     compound.setTag("asset_id", (NBT)new NBTString(pattern.getAssetId().toString()));
/* 102 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 103 */       compound.setTag("template_item", (NBT)new NBTString(pattern.getTemplateItem().getName().toString()));
/*     */     }
/* 105 */     compound.set("description", pattern.getDescription(), (NbtEncoder)wrapper.getSerializers(), wrapper);
/* 106 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
/* 107 */       compound.setTag("decal", (NBT)new NBTByte(pattern.isDecal()));
/*     */     }
/* 109 */     return (NBT)compound;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\trimpattern\TrimPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
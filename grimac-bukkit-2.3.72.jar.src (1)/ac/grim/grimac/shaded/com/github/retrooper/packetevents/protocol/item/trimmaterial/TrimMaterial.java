/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.armormaterial.ArmorMaterial;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.armormaterial.ArmorMaterials;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ public interface TrimMaterial
/*     */   extends MappedEntity, CopyableEntity<TrimMaterial>, DeepComparableEntity
/*     */ {
/*     */   public static final float FALLBACK_ITEM_MODEL_INDEX = 0.0F;
/*     */   
/*     */   @Nullable
/*     */   default String getArmorMaterialOverride(ArmorMaterial armorMaterial) {
/*  63 */     return getOverrideArmorMaterials().get(armorMaterial);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static TrimMaterial read(PacketWrapper<?> wrapper) {
/*  71 */     return (TrimMaterial)wrapper.readMappedEntityOrDirect((IRegistry)TrimMaterials.getRegistry(), TrimMaterial::readDirect);
/*     */   }
/*     */   
/*     */   static TrimMaterial readDirect(PacketWrapper<?> wrapper) {
/*  75 */     String assetName = wrapper.readString();
/*     */     
/*  77 */     ItemType ingredient = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? null : (ItemType)wrapper.readMappedEntity(ItemTypes::getById);
/*     */     
/*  79 */     float itemModelIndex = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4) ? 0.0F : wrapper.readFloat();
/*  80 */     Map<ArmorMaterial, String> overrideArmorMaterials = wrapper.readMap(ew -> (ArmorMaterial)ew.readMappedEntity(ArmorMaterials::getById), PacketWrapper::readString);
/*     */ 
/*     */     
/*  83 */     Component description = wrapper.readComponent();
/*  84 */     return new StaticTrimMaterial(assetName, ingredient, itemModelIndex, overrideArmorMaterials, description);
/*     */   }
/*     */   
/*     */   static void write(PacketWrapper<?> wrapper, TrimMaterial material) {
/*  88 */     wrapper.writeMappedEntityOrDirect(material, TrimMaterial::writeDirect);
/*     */   }
/*     */   
/*     */   static void writeDirect(PacketWrapper<?> wrapper, TrimMaterial material) {
/*  92 */     wrapper.writeString(material.getAssetName());
/*  93 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  94 */       wrapper.writeMappedEntity((MappedEntity)material.getIngredient());
/*     */     }
/*  96 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_4)) {
/*  97 */       wrapper.writeFloat(material.getItemModelIndex());
/*     */     }
/*  99 */     wrapper.writeMap(material.getOverrideArmorMaterials(), PacketWrapper::writeMappedEntity, PacketWrapper::writeString);
/*     */     
/* 101 */     wrapper.writeComponent(material.getDescription());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static TrimMaterial decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 106 */     return decode(nbt, PacketWrapper.createDummyWrapper(version), data);
/*     */   }
/*     */   static TrimMaterial decode(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/*     */     Map<ArmorMaterial, String> overrideArmorMaterials;
/* 110 */     NBTCompound compound = (NBTCompound)nbt;
/* 111 */     String assetName = compound.getStringTagValueOrThrow("asset_name");
/*     */     
/* 113 */     ItemType ingredient = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? null : ItemTypes.getByName(compound.getStringTagValueOrThrow("ingredient"));
/*     */     
/* 115 */     float itemModelIndex = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4) ? 0.0F : compound.getNumberTagOrThrow("item_model_index").getAsFloat();
/* 116 */     NBTCompound overrideArmorMaterialsTag = compound.getCompoundTagOrNull("override_armor_materials");
/*     */     
/* 118 */     if (overrideArmorMaterialsTag != null) {
/* 119 */       overrideArmorMaterials = new HashMap<>();
/* 120 */       for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)overrideArmorMaterialsTag.getTags().entrySet()) {
/* 121 */         ArmorMaterial material = ArmorMaterials.getByName(entry.getKey());
/* 122 */         String override = ((NBTString)entry.getValue()).getValue();
/* 123 */         overrideArmorMaterials.put(material, override);
/*     */       } 
/*     */     } else {
/* 126 */       overrideArmorMaterials = Collections.emptyMap();
/*     */     } 
/* 128 */     Component description = (Component)((NBTCompound)nbt).getOrThrow("description", (NbtDecoder)wrapper.getSerializers(), wrapper);
/* 129 */     return new StaticTrimMaterial(data, assetName, ingredient, itemModelIndex, overrideArmorMaterials, description);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static NBT encode(TrimMaterial material, ClientVersion version) {
/* 134 */     return encode(PacketWrapper.createDummyWrapper(version), material);
/*     */   }
/*     */   
/*     */   static NBT encode(PacketWrapper<?> wrapper, TrimMaterial material) {
/*     */     NBTCompound overrideArmorMaterialsTag;
/* 139 */     if (!material.getOverrideArmorMaterials().isEmpty()) {
/* 140 */       overrideArmorMaterialsTag = new NBTCompound();
/* 141 */       for (Map.Entry<ArmorMaterial, String> entry : material.getOverrideArmorMaterials().entrySet()) {
/* 142 */         String materialName = ((ArmorMaterial)entry.getKey()).getName().toString();
/* 143 */         NBTString overrideTag = new NBTString(entry.getValue());
/* 144 */         overrideArmorMaterialsTag.setTag(materialName, (NBT)overrideTag);
/*     */       } 
/*     */     } else {
/* 147 */       overrideArmorMaterialsTag = null;
/*     */     } 
/*     */     
/* 150 */     NBTCompound compound = new NBTCompound();
/* 151 */     compound.setTag("asset_name", (NBT)new NBTString(material.getAssetName()));
/* 152 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 153 */       compound.setTag("ingredient", (NBT)new NBTString(material.getIngredient().getName().toString()));
/*     */     }
/* 155 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_4)) {
/* 156 */       compound.setTag("item_model_index", (NBT)new NBTFloat(material.getItemModelIndex()));
/*     */     }
/* 158 */     if (overrideArmorMaterialsTag != null) {
/* 159 */       compound.setTag("override_armor_materials", (NBT)overrideArmorMaterialsTag);
/*     */     }
/* 161 */     compound.set("description", material.getDescription(), (NbtEncoder)wrapper.getSerializers(), wrapper);
/* 162 */     return (NBT)compound;
/*     */   }
/*     */   
/*     */   String getAssetName();
/*     */   
/*     */   @Obsolete
/*     */   ItemType getIngredient();
/*     */   
/*     */   @Obsolete
/*     */   float getItemModelIndex();
/*     */   
/*     */   Map<ArmorMaterial, String> getOverrideArmorMaterials();
/*     */   
/*     */   Component getDescription();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\trimmaterial\TrimMaterial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
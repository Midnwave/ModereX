/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
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
/*    */ public interface PaintingVariant
/*    */   extends MappedEntity, CopyableEntity<PaintingVariant>, DeepComparableEntity
/*    */ {
/*    */   int getWidth();
/*    */   
/*    */   int getHeight();
/*    */   
/*    */   ResourceLocation getAssetId();
/*    */   
/*    */   static PaintingVariant read(PacketWrapper<?> wrapper) {
/* 44 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 45 */       return (PaintingVariant)wrapper.readMappedEntityOrDirect((IRegistry)PaintingVariants.getRegistry(), PaintingVariant::readDirect);
/*    */     }
/* 47 */     return (PaintingVariant)wrapper.readMappedEntity((IRegistry)PaintingVariants.getRegistry());
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, PaintingVariant variant) {
/* 51 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 52 */       wrapper.writeMappedEntityOrDirect(variant, PaintingVariant::writeDirect);
/*    */     } else {
/* 54 */       wrapper.writeMappedEntity(variant);
/*    */     } 
/*    */   }
/*    */   
/*    */   static PaintingVariant readDirect(PacketWrapper<?> wrapper) {
/* 59 */     int width = wrapper.readVarInt();
/* 60 */     int height = wrapper.readVarInt();
/* 61 */     ResourceLocation assetId = wrapper.readIdentifier();
/* 62 */     return new StaticPaintingVariant(width, height, assetId);
/*    */   }
/*    */   
/*    */   static void writeDirect(PacketWrapper<?> wrapper, PaintingVariant variant) {
/* 66 */     wrapper.writeVarInt(variant.getWidth());
/* 67 */     wrapper.writeVarInt(variant.getHeight());
/* 68 */     wrapper.writeIdentifier(variant.getAssetId());
/*    */   }
/*    */   
/*    */   static PaintingVariant decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 72 */     NBTCompound compound = (NBTCompound)nbt;
/* 73 */     int width = compound.getNumberTagOrThrow("width").getAsInt();
/* 74 */     int height = compound.getNumberTagOrThrow("height").getAsInt();
/* 75 */     ResourceLocation assetId = new ResourceLocation(compound.getStringTagValueOrThrow("asset_id"));
/* 76 */     return new StaticPaintingVariant(data, width, height, assetId);
/*    */   }
/*    */   
/*    */   static NBT encode(PaintingVariant variant, ClientVersion version) {
/* 80 */     NBTCompound compound = new NBTCompound();
/* 81 */     compound.setTag("width", (NBT)new NBTInt(variant.getWidth()));
/* 82 */     compound.setTag("height", (NBT)new NBTInt(variant.getHeight()));
/* 83 */     compound.setTag("asset_id", (NBT)new NBTString(variant.getAssetId().toString()));
/* 84 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\painting\PaintingVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
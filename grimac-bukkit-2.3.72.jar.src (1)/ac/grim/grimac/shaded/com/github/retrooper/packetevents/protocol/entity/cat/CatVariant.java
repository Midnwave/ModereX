/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
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
/*    */ public interface CatVariant
/*    */   extends MappedEntity, CopyableEntity<CatVariant>, DeepComparableEntity
/*    */ {
/*    */   ResourceLocation getAssetId();
/*    */   
/*    */   static CatVariant read(PacketWrapper<?> wrapper) {
/* 38 */     return (CatVariant)wrapper.readMappedEntity((IRegistry)CatVariants.getRegistry());
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, CatVariant variant) {
/* 42 */     wrapper.writeMappedEntity(variant);
/*    */   }
/*    */   
/*    */   static CatVariant decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 46 */     NBTCompound compound = (NBTCompound)nbt;
/* 47 */     ResourceLocation assetId = new ResourceLocation(compound.getStringTagValueOrThrow("asset_id"));
/* 48 */     return new StaticCatVariant(data, assetId);
/*    */   }
/*    */   
/*    */   static NBT encode(CatVariant variant, ClientVersion version) {
/* 52 */     NBTCompound compound = new NBTCompound();
/* 53 */     compound.setTag("asset_id", (NBT)new NBTString(variant.getAssetId().toString()));
/* 54 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\cat\CatVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
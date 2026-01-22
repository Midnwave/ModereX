/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.frog;
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
/*    */ public interface FrogVariant
/*    */   extends MappedEntity, CopyableEntity<FrogVariant>, DeepComparableEntity
/*    */ {
/*    */   ResourceLocation getAssetId();
/*    */   
/*    */   static FrogVariant read(PacketWrapper<?> wrapper) {
/* 38 */     return (FrogVariant)wrapper.readMappedEntity((IRegistry)FrogVariants.getRegistry());
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, FrogVariant variant) {
/* 42 */     wrapper.writeMappedEntity(variant);
/*    */   }
/*    */   
/*    */   static FrogVariant decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 46 */     NBTCompound compound = (NBTCompound)nbt;
/* 47 */     ResourceLocation assetId = new ResourceLocation(compound.getStringTagValueOrThrow("asset_id"));
/* 48 */     return new StaticFrogVariant(data, assetId);
/*    */   }
/*    */   
/*    */   static NBT encode(FrogVariant variant, ClientVersion version) {
/* 52 */     NBTCompound compound = new NBTCompound();
/* 53 */     compound.setTag("asset_id", (NBT)new NBTString(variant.getAssetId().toString()));
/* 54 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\frog\FrogVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
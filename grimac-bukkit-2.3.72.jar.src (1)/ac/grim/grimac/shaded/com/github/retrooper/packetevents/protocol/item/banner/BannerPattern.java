/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.banner;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
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
/*    */ 
/*    */ public interface BannerPattern
/*    */   extends MappedEntity, CopyableEntity<BannerPattern>, DeepComparableEntity
/*    */ {
/*    */   ResourceLocation getAssetId();
/*    */   
/*    */   String getTranslationKey();
/*    */   
/*    */   static BannerPattern readDirect(PacketWrapper<?> wrapper) {
/* 40 */     ResourceLocation assetId = wrapper.readIdentifier();
/* 41 */     String translationKey = wrapper.readString();
/* 42 */     return new StaticBannerPattern(assetId, translationKey);
/*    */   }
/*    */   
/*    */   static void writeDirect(PacketWrapper<?> wrapper, BannerPattern pattern) {
/* 46 */     wrapper.writeIdentifier(pattern.getAssetId());
/* 47 */     wrapper.writeString(pattern.getTranslationKey());
/*    */   }
/*    */   
/*    */   static BannerPattern decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/* 51 */     NBTCompound compound = (NBTCompound)nbt;
/* 52 */     ResourceLocation assetId = new ResourceLocation(compound.getStringTagValueOrThrow("asset_id"));
/* 53 */     String translationKey = compound.getStringTagValueOrThrow("translation_key");
/* 54 */     return new StaticBannerPattern(data, assetId, translationKey);
/*    */   }
/*    */   
/*    */   static NBT encode(BannerPattern bannerPattern, ClientVersion version) {
/* 58 */     NBTCompound compound = new NBTCompound();
/* 59 */     compound.setTag("asset_id", (NBT)new NBTString(bannerPattern.getAssetId().toString()));
/* 60 */     compound.setTag("translation_key", (NBT)new NBTString(bannerPattern.getTranslationKey()));
/* 61 */     return (NBT)compound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\banner\BannerPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
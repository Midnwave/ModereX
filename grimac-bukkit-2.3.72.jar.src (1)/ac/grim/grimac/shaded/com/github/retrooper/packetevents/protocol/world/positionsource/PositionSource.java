/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*    */ public abstract class PositionSource
/*    */ {
/*    */   protected final PositionSourceType<?> type;
/*    */   
/*    */   public PositionSource(PositionSourceType<?> type) {
/* 31 */     this.type = type;
/*    */   }
/*    */   
/*    */   public static PositionSource decode(NBT nbt, ClientVersion version) {
/* 35 */     NBTCompound compound = (NBTCompound)nbt;
/* 36 */     String typeId = compound.getStringTagValueOrThrow("type");
/* 37 */     PositionSourceType<?> sourceType = PositionSourceTypes.getByName(typeId);
/* 38 */     if (sourceType == null) {
/* 39 */       throw new IllegalStateException("Can't find position source type with id " + typeId);
/*    */     }
/* 41 */     return (PositionSource)sourceType.decode(compound, version);
/*    */   }
/*    */ 
/*    */   
/*    */   public static NBT encode(PositionSource source, ClientVersion version) {
/* 46 */     return encode(source, source.getType(), version);
/*    */   }
/*    */   
/*    */   public static <T extends PositionSource> NBT encode(T source, PositionSourceType<T> type, ClientVersion version) {
/* 50 */     NBTCompound compound = new NBTCompound();
/* 51 */     compound.setTag("type", (NBT)new NBTString(type.getName().toString()));
/* 52 */     type.encode(source, version, compound);
/* 53 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public PositionSourceType<?> getType() {
/* 57 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\PositionSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
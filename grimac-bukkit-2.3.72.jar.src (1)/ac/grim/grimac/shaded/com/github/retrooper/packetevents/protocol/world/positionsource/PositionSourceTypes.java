/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.builtin.BlockPositionSource;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.builtin.EntityPositionSource;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ 
/*    */ public final class PositionSourceTypes
/*    */ {
/* 33 */   private static final VersionedRegistry<PositionSourceType<?>> REGISTRY = new VersionedRegistry("position_source_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<PositionSourceType<?>> getRegistry() {
/* 39 */     return REGISTRY;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends PositionSource> PositionSourceType<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer, Decoder<T> decoder, Encoder<T> encoder) {
/* 48 */     return (PositionSourceType<T>)REGISTRY.define(name, data -> new StaticPositionSourceType<>(data, reader, writer, decoder, encoder));
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static PositionSourceType<?> getByName(String name) {
/* 53 */     return (PositionSourceType)REGISTRY.getByName(name);
/*    */   }
/*    */   
/*    */   public static PositionSourceType<?> getById(ClientVersion version, int id) {
/* 57 */     return (PositionSourceType)REGISTRY.getById(version, id);
/*    */   }
/*    */   
/* 60 */   public static final PositionSourceType<BlockPositionSource> BLOCK = define("block", BlockPositionSource::read, BlockPositionSource::write, BlockPositionSource::decodeSource, BlockPositionSource::encodeSource);
/*    */ 
/*    */   
/* 63 */   public static final PositionSourceType<EntityPositionSource> ENTITY = define("entity", EntityPositionSource::read, EntityPositionSource::write, EntityPositionSource::decodeSource, EntityPositionSource::encodeSource);
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 68 */     REGISTRY.unloadMappings();
/*    */   }
/*    */   
/*    */   @FunctionalInterface
/*    */   public static interface Decoder<T> {
/*    */     T decode(NBTCompound param1NBTCompound, ClientVersion param1ClientVersion);
/*    */   }
/*    */   
/*    */   @FunctionalInterface
/*    */   public static interface Encoder<T> {
/*    */     void encode(T param1T, ClientVersion param1ClientVersion, NBTCompound param1NBTCompound);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\PositionSourceTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
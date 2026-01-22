/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticPositionSourceType<T extends PositionSource>
/*    */   extends AbstractMappedEntity
/*    */   implements PositionSourceType<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   private final PositionSourceTypes.Decoder<T> decoder;
/*    */   private final PositionSourceTypes.Encoder<T> encoder;
/*    */   
/*    */   @Internal
/*    */   public StaticPositionSourceType(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer, PositionSourceTypes.Decoder<T> decoder, PositionSourceTypes.Encoder<T> encoder) {
/* 44 */     super(data);
/* 45 */     this.reader = reader;
/* 46 */     this.writer = writer;
/* 47 */     this.decoder = decoder;
/* 48 */     this.encoder = encoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(PacketWrapper<?> wrapper) {
/* 53 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(PacketWrapper<?> wrapper, T source) {
/* 58 */     this.writer.accept(wrapper, source);
/*    */   }
/*    */ 
/*    */   
/*    */   public T decode(NBTCompound compound, ClientVersion version) {
/* 63 */     return this.decoder.decode(compound, version);
/*    */   }
/*    */ 
/*    */   
/*    */   public void encode(T source, ClientVersion version, NBTCompound compound) {
/* 68 */     this.encoder.encode(source, version, compound);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\StaticPositionSourceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
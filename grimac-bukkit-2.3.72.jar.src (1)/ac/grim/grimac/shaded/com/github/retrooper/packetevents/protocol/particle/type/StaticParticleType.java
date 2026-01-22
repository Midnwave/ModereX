/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleData;
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
/*    */ public class StaticParticleType<T extends ParticleData>
/*    */   extends AbstractMappedEntity
/*    */   implements ParticleType<T>
/*    */ {
/*    */   private final PacketWrapper.Reader<T> reader;
/*    */   private final PacketWrapper.Writer<T> writer;
/*    */   private final ParticleTypes.Decoder<T> decoder;
/*    */   private final ParticleTypes.Encoder<T> encoder;
/*    */   
/*    */   @Internal
/*    */   public StaticParticleType(@Nullable TypesBuilderData data, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer, ParticleTypes.Decoder<T> decoder, ParticleTypes.Encoder<T> encoder) {
/* 45 */     super(data);
/* 46 */     this.reader = reader;
/* 47 */     this.writer = writer;
/* 48 */     this.decoder = decoder;
/* 49 */     this.encoder = encoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public T readData(PacketWrapper<?> wrapper) {
/* 54 */     return (T)this.reader.apply(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeData(PacketWrapper<?> wrapper, T data) {
/* 59 */     if (this.writer != null) {
/* 60 */       this.writer.accept(wrapper, data);
/* 61 */     } else if (!data.isEmpty()) {
/* 62 */       throw new UnsupportedOperationException("Trying to write non-empty data for " + getName());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public T decodeData(NBTCompound compound, ClientVersion version) {
/* 68 */     return this.decoder.decode(compound, version);
/*    */   }
/*    */ 
/*    */   
/*    */   public void encodeData(T value, ClientVersion version, NBTCompound compound) {
/* 73 */     if (this.encoder != null) {
/* 74 */       this.encoder.encode(value, version, compound);
/* 75 */     } else if (!value.isEmpty()) {
/* 76 */       throw new UnsupportedOperationException("Trying to encode non-empty data for " + getName());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\type\StaticParticleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
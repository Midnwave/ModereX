/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.function.Function;
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
/*     */ public class StaticComponentType<T>
/*     */   extends AbstractMappedEntity
/*     */   implements ComponentType<T>
/*     */ {
/*     */   @Nullable
/*     */   private final PacketWrapper.Reader<T> reader;
/*     */   @Nullable
/*     */   private final PacketWrapper.Writer<T> writer;
/*     */   @Nullable
/*     */   private final ComponentType.Decoder<T> decoder;
/*     */   @Nullable
/*     */   private final ComponentType.Encoder<T> encoder;
/*     */   
/*     */   @Internal
/*     */   public StaticComponentType(@Nullable TypesBuilderData data, @Nullable PacketWrapper.Reader<T> reader, @Nullable PacketWrapper.Writer<T> writer) {
/*  46 */     this(data, reader, writer, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public StaticComponentType(@Nullable TypesBuilderData data, @Nullable ComponentType.Decoder<T> decoder, @Nullable ComponentType.Encoder<T> encoder) {
/*  55 */     this(data, null, null, decoder, encoder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public StaticComponentType(@Nullable TypesBuilderData data, @Nullable PacketWrapper.Reader<T> reader, @Nullable PacketWrapper.Writer<T> writer, @Nullable ComponentType.Decoder<T> decoder, @Nullable ComponentType.Encoder<T> encoder) {
/*  66 */     super(data);
/*  67 */     this.reader = reader;
/*  68 */     this.writer = writer;
/*  69 */     this.decoder = decoder;
/*  70 */     this.encoder = encoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public T read(PacketWrapper<?> wrapper) {
/*  75 */     return (this.reader != null) ? (T)this.reader.apply(wrapper) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(PacketWrapper<?> wrapper, T content) {
/*  80 */     if (this.writer != null) {
/*  81 */       this.writer.accept(wrapper, content);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public T decode(NBT nbt, ClientVersion version) {
/*  87 */     if (this.decoder != null) {
/*  88 */       return this.decoder.decode(nbt, version);
/*     */     }
/*  90 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public NBT encode(T value, ClientVersion version) {
/*  95 */     if (this.encoder != null) {
/*  96 */       return this.encoder.encode(value, version);
/*     */     }
/*  98 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public <Z> ComponentType<Z> legacyMap(Function<T, Z> mapper, Function<Z, T> unmapper) {
/* 103 */     PacketWrapper.Reader<Z> reader = (this.reader != null) ? (wrapper -> mapper.apply(this.reader.apply(wrapper))) : null;
/* 104 */     PacketWrapper.Writer<Z> writer = (this.writer != null) ? ((wrapper, value) -> this.writer.accept(wrapper, unmapper.apply(value))) : null;
/* 105 */     ComponentType.Decoder<Z> decoder = (this.decoder != null) ? ((nbt, version) -> mapper.apply(this.decoder.decode(nbt, version))) : null;
/* 106 */     ComponentType.Encoder<Z> encoder = (this.encoder != null) ? ((value, version) -> this.encoder.encode(unmapper.apply(value), version)) : null;
/* 107 */     return new StaticComponentType(this.data, reader, writer, decoder, encoder);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\StaticComponentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
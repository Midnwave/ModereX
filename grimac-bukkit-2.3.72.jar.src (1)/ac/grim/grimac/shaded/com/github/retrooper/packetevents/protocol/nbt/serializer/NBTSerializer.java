/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
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
/*     */ @NonExtendable
/*     */ public class NBTSerializer<IN, OUT>
/*     */   implements NBTReader<NBT, IN>, NBTWriter<NBT, OUT>
/*     */ {
/*     */   protected final IdReader<IN> idReader;
/*     */   protected final IdWriter<OUT> idWriter;
/*     */   protected final NameReader<IN> nameReader;
/*     */   protected final NameWriter<OUT> nameWriter;
/*  38 */   protected final Map<Integer, NBTType<? extends NBT>> idToType = new HashMap<>();
/*  39 */   protected final Map<NBTType<? extends NBT>, Integer> typeToId = new HashMap<>();
/*  40 */   protected final Map<NBTType<? extends NBT>, TagReader<IN, ? extends NBT>> typeReaders = new HashMap<>();
/*  41 */   protected final Map<NBTType<? extends NBT>, TagWriter<OUT, ? extends NBT>> typeWriters = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NBTSerializer(IdReader<IN> idReader, IdWriter<OUT> idWriter, NameReader<IN> nameReader, NameWriter<OUT> nameWriter) {
/*  47 */     this.idReader = idReader;
/*  48 */     this.idWriter = idWriter;
/*  49 */     this.nameReader = nameReader;
/*  50 */     this.nameWriter = nameWriter;
/*     */   }
/*     */ 
/*     */   
/*     */   public NBT deserializeTag(NBTLimiter limiter, IN from, boolean named) throws IOException {
/*  55 */     NBTType<?> type = readTagType(limiter, from);
/*  56 */     if (type == NBTType.END) {
/*  57 */       return null;
/*     */     }
/*  59 */     if (named) {
/*  60 */       readTagName(limiter, from);
/*     */     }
/*  62 */     return readTag(limiter, from, type);
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeTag(OUT to, NBT tag, boolean named) throws IOException {
/*  67 */     NBTType<?> type = tag.getType();
/*  68 */     writeTagType(to, type);
/*  69 */     if (tag.getType() == NBTType.END) {
/*     */       return;
/*     */     }
/*  72 */     if (named) {
/*  73 */       writeTagName(to, "");
/*     */     }
/*  75 */     writeTag(to, tag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <T extends NBT> void registerType(NBTType<T> type, int id, TagReader<IN, T> typeReader, TagWriter<OUT, T> typeWriter) {
/*  83 */     if (this.typeToId.containsKey(type)) {
/*  84 */       throw new IllegalArgumentException(MessageFormat.format("Nbt type {0} is already registered", new Object[] { type }));
/*     */     }
/*  86 */     if (this.idToType.containsKey(Integer.valueOf(id))) {
/*  87 */       throw new IllegalArgumentException(MessageFormat.format("Nbt type id {0} is already registered", new Object[] { Integer.valueOf(id) }));
/*     */     }
/*  89 */     this.idToType.put(Integer.valueOf(id), type);
/*  90 */     this.typeToId.put(type, Integer.valueOf(id));
/*  91 */     this.typeReaders.put(type, typeReader);
/*  92 */     this.typeWriters.put(type, typeWriter);
/*     */   }
/*     */   
/*     */   NBTType<?> readTagType(NBTLimiter limiter, IN from) throws IOException {
/*  96 */     int id = this.idReader.readId(limiter, from);
/*  97 */     NBTType<?> type = this.idToType.get(Integer.valueOf(id));
/*  98 */     if (type == null) {
/*  99 */       throw new IOException(MessageFormat.format("Unknown nbt type id {0}", new Object[] { Integer.valueOf(id) }));
/*     */     }
/* 101 */     return type;
/*     */   }
/*     */   
/*     */   @Internal
/*     */   String readTagName(NBTLimiter limiter, IN from) throws IOException {
/* 106 */     return this.nameReader.readName(limiter, from);
/*     */   }
/*     */   
/*     */   NBT readTag(NBTLimiter limiter, IN from, NBTType<?> type) throws IOException {
/* 110 */     TagReader<IN, ? extends NBT> f = this.typeReaders.get(type);
/* 111 */     if (f == null) {
/* 112 */       throw new IOException(MessageFormat.format("No reader registered for nbt type {0}", new Object[] { type }));
/*     */     }
/* 114 */     return f.readTag(limiter, from);
/*     */   }
/*     */   
/*     */   void writeTagType(OUT stream, NBTType<?> type) throws IOException {
/* 118 */     int id = ((Integer)this.typeToId.getOrDefault(type, Integer.valueOf(-1))).intValue();
/* 119 */     if (id == -1) {
/* 120 */       throw new IOException(MessageFormat.format("Unknown nbt type {0}", new Object[] { type }));
/*     */     }
/* 122 */     this.idWriter.writeId(stream, id);
/*     */   }
/*     */   
/*     */   void writeTagName(OUT stream, String name) throws IOException {
/* 126 */     this.nameWriter.writeName(stream, name);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTag(OUT stream, NBT tag) throws IOException {
/* 131 */     TagWriter<OUT, NBT> f = (TagWriter<OUT, NBT>)this.typeWriters.get(tag.getType());
/* 132 */     if (f == null) {
/* 133 */       throw new IOException(MessageFormat.format("No writer registered for nbt type {0}", new Object[] { tag.getType() }));
/*     */     }
/* 135 */     f.writeTag(stream, tag);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   protected static interface IdReader<T> {
/*     */     int readId(NBTLimiter param1NBTLimiter, T param1T) throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   protected static interface IdWriter<T> {
/*     */     void writeId(T param1T, int param1Int) throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   protected static interface NameReader<T> {
/*     */     String readName(NBTLimiter param1NBTLimiter, T param1T) throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   protected static interface NameWriter<T> {
/*     */     void writeName(T param1T, String param1String) throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   protected static interface TagReader<IN, T extends NBT> {
/*     */     T readTag(NBTLimiter param1NBTLimiter, IN param1IN) throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface TagWriter<OUT, T extends NBT> {
/*     */     void writeTag(OUT param1OUT, T param1T) throws IOException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\serializer\NBTSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
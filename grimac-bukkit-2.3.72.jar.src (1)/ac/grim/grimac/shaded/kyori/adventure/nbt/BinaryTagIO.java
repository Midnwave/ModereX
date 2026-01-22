/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Map;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import java.util.zip.InflaterInputStream;
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
/*     */ public final class BinaryTagIO
/*     */ {
/*     */   static {
/*  49 */     BinaryTagTypes.COMPOUND.id();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Reader unlimitedReader() {
/*  61 */     return BinaryTagReaderImpl.UNLIMITED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Reader reader() {
/*  73 */     return BinaryTagReaderImpl.DEFAULT_LIMIT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Reader reader(long sizeLimitBytes) {
/*  86 */     if (sizeLimitBytes <= 0L) {
/*  87 */       throw new IllegalArgumentException("The size limit must be greater than zero");
/*     */     }
/*  89 */     return new BinaryTagReaderImpl(sizeLimitBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Writer writer() {
/*  99 */     return BinaryTagWriterImpl.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static CompoundBinaryTag readPath(@NotNull Path path) throws IOException {
/* 113 */     return reader().read(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static CompoundBinaryTag readInputStream(@NotNull InputStream input) throws IOException {
/* 127 */     return reader().read(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static CompoundBinaryTag readCompressedPath(@NotNull Path path) throws IOException {
/* 141 */     return reader().read(path, Compression.GZIP);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static CompoundBinaryTag readCompressedInputStream(@NotNull InputStream input) throws IOException {
/* 155 */     return reader().read(input, Compression.GZIP);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static CompoundBinaryTag readDataInput(@NotNull DataInput input) throws IOException {
/* 169 */     return reader().read(input);
/*     */   }
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
/*     */   @Deprecated
/*     */   public static void writePath(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
/* 183 */     writer().write(tag, path);
/*     */   }
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
/*     */   @Deprecated
/*     */   public static void writeOutputStream(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
/* 197 */     writer().write(tag, output);
/*     */   }
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
/*     */   @Deprecated
/*     */   public static void writeCompressedPath(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
/* 211 */     writer().write(tag, path, Compression.GZIP);
/*     */   }
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
/*     */   @Deprecated
/*     */   public static void writeCompressedOutputStream(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
/* 225 */     writer().write(tag, output, Compression.GZIP);
/*     */   }
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
/*     */   @Deprecated
/*     */   public static void writeDataOutput(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output) throws IOException {
/* 239 */     writer().write(tag, output);
/*     */   }
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
/*     */   public static interface Reader
/*     */   {
/*     */     @NotNull
/*     */     default CompoundBinaryTag read(@NotNull Path path) throws IOException {
/* 261 */       return read(path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     @NotNull
/*     */     CompoundBinaryTag read(@NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     @NotNull
/*     */     default CompoundBinaryTag read(@NotNull InputStream input) throws IOException {
/* 290 */       return read(input, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     @NotNull
/*     */     CompoundBinaryTag read(@NotNull InputStream param1InputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     @NotNull
/*     */     CompoundBinaryTag read(@NotNull DataInput param1DataInput) throws IOException;
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
/*     */     @NotNull
/*     */     default CompoundBinaryTag readNameless(@NotNull Path path) throws IOException {
/* 332 */       return readNameless(path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     @NotNull
/*     */     CompoundBinaryTag readNameless(@NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     @NotNull
/*     */     default CompoundBinaryTag readNameless(@NotNull InputStream input) throws IOException {
/* 363 */       return readNameless(input, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     @NotNull
/*     */     CompoundBinaryTag readNameless(@NotNull InputStream param1InputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     @NotNull
/*     */     CompoundBinaryTag readNameless(@NotNull DataInput param1DataInput) throws IOException;
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
/*     */     default Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path path) throws IOException {
/* 404 */       return readNamed(path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     default Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream input) throws IOException {
/* 429 */       return readNamed(input, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream param1InputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull DataInput param1DataInput) throws IOException;
/*     */   }
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
/*     */   public static interface Writer
/*     */   {
/*     */     default void write(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
/* 473 */       write(tag, path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void write(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     default void write(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
/* 502 */       write(tag, output, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void write(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull OutputStream param1OutputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     void write(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull DataOutput param1DataOutput) throws IOException;
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
/*     */     default void writeNameless(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
/* 544 */       writeNameless(tag, path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void writeNameless(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     default void writeNameless(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
/* 575 */       writeNameless(tag, output, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void writeNameless(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull OutputStream param1OutputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     void writeNameless(@NotNull CompoundBinaryTag param1CompoundBinaryTag, @NotNull DataOutput param1DataOutput) throws IOException;
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
/*     */     default void writeNamed(Map.Entry<String, CompoundBinaryTag> tag, @NotNull Path path) throws IOException {
/* 616 */       writeNamed(tag, path, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void writeNamed(Map.Entry<String, CompoundBinaryTag> param1Entry, @NotNull Path param1Path, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     default void writeNamed(Map.Entry<String, CompoundBinaryTag> tag, @NotNull OutputStream output) throws IOException {
/* 641 */       writeNamed(tag, output, BinaryTagIO.Compression.NONE);
/*     */     }
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
/*     */     void writeNamed(Map.Entry<String, CompoundBinaryTag> param1Entry, @NotNull OutputStream param1OutputStream, @NotNull BinaryTagIO.Compression param1Compression) throws IOException;
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
/*     */     void writeNamed(Map.Entry<String, CompoundBinaryTag> param1Entry, @NotNull DataOutput param1DataOutput) throws IOException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class Compression
/*     */   {
/* 677 */     public static final Compression NONE = new Compression() {
/*     */         @NotNull
/*     */         InputStream decompress(@NotNull InputStream is) {
/* 680 */           return is;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         OutputStream compress(@NotNull OutputStream os) {
/* 685 */           return os;
/*     */         }
/*     */ 
/*     */         
/*     */         public String toString() {
/* 690 */           return "Compression.NONE";
/*     */         }
/*     */       };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 698 */     public static final Compression GZIP = new Compression() {
/*     */         @NotNull
/*     */         InputStream decompress(@NotNull InputStream is) throws IOException {
/* 701 */           return new GZIPInputStream(is);
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         OutputStream compress(@NotNull OutputStream os) throws IOException {
/* 706 */           return new GZIPOutputStream(os);
/*     */         }
/*     */ 
/*     */         
/*     */         public String toString() {
/* 711 */           return "Compression.GZIP";
/*     */         }
/*     */       };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 719 */     public static final Compression ZLIB = new Compression() {
/*     */         @NotNull
/*     */         InputStream decompress(@NotNull InputStream is) {
/* 722 */           return new InflaterInputStream(is);
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         OutputStream compress(@NotNull OutputStream os) {
/* 727 */           return new DeflaterOutputStream(os);
/*     */         }
/*     */ 
/*     */         
/*     */         public String toString() {
/* 732 */           return "Compression.ZLIB";
/*     */         }
/*     */       };
/*     */     
/*     */     @NotNull
/*     */     abstract InputStream decompress(@NotNull InputStream param1InputStream) throws IOException;
/*     */     
/*     */     @NotNull
/*     */     abstract OutputStream compress(@NotNull OutputStream param1OutputStream) throws IOException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\BinaryTagIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
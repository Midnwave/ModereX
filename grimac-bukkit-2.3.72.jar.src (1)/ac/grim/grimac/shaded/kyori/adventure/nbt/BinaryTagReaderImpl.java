/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.AbstractMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class BinaryTagReaderImpl
/*     */   implements BinaryTagIO.Reader
/*     */ {
/*     */   private final long maxBytes;
/*  42 */   static final BinaryTagIO.Reader UNLIMITED = new BinaryTagReaderImpl(-1L);
/*  43 */   static final BinaryTagIO.Reader DEFAULT_LIMIT = new BinaryTagReaderImpl(131082L);
/*     */   
/*     */   BinaryTagReaderImpl(long maxBytes) {
/*  46 */     this.maxBytes = maxBytes;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag read(@NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/*  51 */     InputStream is = Files.newInputStream(path, new java.nio.file.OpenOption[0]); 
/*  52 */     try { CompoundBinaryTag compoundBinaryTag = read(is, compression);
/*  53 */       if (is != null) is.close();  return compoundBinaryTag; }
/*     */     catch (Throwable throwable) { if (is != null)
/*     */         try { is.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  58 */      } @NotNull public CompoundBinaryTag read(@NotNull InputStream input, BinaryTagIO.Compression compression) throws IOException { DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(IOStreamUtil.closeShield(input)))); 
/*  59 */     try { CompoundBinaryTag compoundBinaryTag = read(dis);
/*  60 */       dis.close(); return compoundBinaryTag; }
/*     */     catch (Throwable throwable) { try { dis.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/*  65 */      } @NotNull public CompoundBinaryTag read(@NotNull DataInput input) throws IOException { return read(input, true); }
/*     */   
/*     */   @NotNull
/*     */   private CompoundBinaryTag read(@NotNull DataInput input, boolean named) throws IOException {
/*  69 */     if (!(input instanceof TrackingDataInput)) {
/*  70 */       input = new TrackingDataInput(input, this.maxBytes);
/*     */     }
/*     */     
/*  73 */     BinaryTagType<? extends BinaryTag> type = BinaryTagType.binaryTagType(input.readByte());
/*  74 */     requireCompound(type);
/*  75 */     if (named) {
/*  76 */       input.skipBytes(input.readUnsignedShort());
/*     */     }
/*  78 */     return BinaryTagTypes.COMPOUND.read(input);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag readNameless(@NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/*  83 */     InputStream is = Files.newInputStream(path, new java.nio.file.OpenOption[0]); 
/*  84 */     try { CompoundBinaryTag compoundBinaryTag = readNameless(is, compression);
/*  85 */       if (is != null) is.close();  return compoundBinaryTag; }
/*     */     catch (Throwable throwable) { if (is != null)
/*     */         try { is.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  90 */      } @NotNull public CompoundBinaryTag readNameless(@NotNull InputStream input, BinaryTagIO.Compression compression) throws IOException { DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(IOStreamUtil.closeShield(input)))); 
/*  91 */     try { CompoundBinaryTag compoundBinaryTag = readNameless(dis);
/*  92 */       dis.close(); return compoundBinaryTag; }
/*     */     catch (Throwable throwable) { try { dis.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/*  97 */      } @NotNull public CompoundBinaryTag readNameless(@NotNull DataInput input) throws IOException { return read(input, false); }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/* 102 */     InputStream is = Files.newInputStream(path, new java.nio.file.OpenOption[0]); 
/* 103 */     try { Map.Entry<String, CompoundBinaryTag> entry = readNamed(is, compression);
/* 104 */       if (is != null) is.close();  return entry; }
/*     */     catch (Throwable throwable) { if (is != null)
/*     */         try { is.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/* 109 */      } public Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream input, BinaryTagIO.Compression compression) throws IOException { DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(IOStreamUtil.closeShield(input)))); 
/* 110 */     try { Map.Entry<String, CompoundBinaryTag> entry = readNamed(dis);
/* 111 */       dis.close(); return entry; }
/*     */     catch (Throwable throwable) { try { dis.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 116 */      } public Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull DataInput input) throws IOException { BinaryTagType<? extends BinaryTag> type = BinaryTagType.binaryTagType(input.readByte());
/* 117 */     requireCompound(type);
/* 118 */     String name = input.readUTF();
/* 119 */     return new AbstractMap.SimpleImmutableEntry<>(name, BinaryTagTypes.COMPOUND.read(input)); }
/*     */ 
/*     */   
/*     */   private static void requireCompound(BinaryTagType<? extends BinaryTag> type) throws IOException {
/* 123 */     if (type != BinaryTagTypes.COMPOUND)
/* 124 */       throw new IOException(String.format("Expected root tag to be a %s, was %s", new Object[] { BinaryTagTypes.COMPOUND, type })); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\BinaryTagReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
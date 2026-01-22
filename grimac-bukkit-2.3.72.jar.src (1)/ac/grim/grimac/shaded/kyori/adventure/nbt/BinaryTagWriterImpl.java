/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
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
/*     */ final class BinaryTagWriterImpl
/*     */   implements BinaryTagIO.Writer
/*     */ {
/*  39 */   static final BinaryTagIO.Writer INSTANCE = new BinaryTagWriterImpl();
/*     */ 
/*     */   
/*     */   public void write(@NotNull CompoundBinaryTag tag, @NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/*  43 */     OutputStream os = Files.newOutputStream(path, new java.nio.file.OpenOption[0]); 
/*  44 */     try { write(tag, os, compression);
/*  45 */       if (os != null) os.close();  }
/*     */     catch (Throwable throwable) { if (os != null)
/*     */         try { os.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  50 */      } public void write(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output, BinaryTagIO.Compression compression) throws IOException { DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(IOStreamUtil.closeShield(output)))); 
/*  51 */     try { write(tag, dos);
/*  52 */       dos.close(); }
/*     */     catch (Throwable throwable) { try { dos.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/*  57 */      } public void write(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output) throws IOException { write(tag, output, true); }
/*     */ 
/*     */   
/*     */   private void write(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output, boolean named) throws IOException {
/*  61 */     output.writeByte(BinaryTagTypes.COMPOUND.id());
/*  62 */     if (named) {
/*  63 */       output.writeUTF("");
/*     */     }
/*  65 */     BinaryTagTypes.COMPOUND.write(tag, output);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNameless(@NotNull CompoundBinaryTag tag, @NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/*  70 */     OutputStream os = Files.newOutputStream(path, new java.nio.file.OpenOption[0]); 
/*  71 */     try { writeNameless(tag, os, compression);
/*  72 */       if (os != null) os.close();  }
/*     */     catch (Throwable throwable) { if (os != null)
/*     */         try { os.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  77 */      } public void writeNameless(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output, BinaryTagIO.Compression compression) throws IOException { DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(IOStreamUtil.closeShield(output)))); 
/*  78 */     try { writeNameless(tag, dos);
/*  79 */       dos.close(); }
/*     */     catch (Throwable throwable) { try { dos.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/*  84 */      } public void writeNameless(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output) throws IOException { write(tag, output, false); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeNamed(Map.Entry<String, CompoundBinaryTag> tag, @NotNull Path path, BinaryTagIO.Compression compression) throws IOException {
/*  89 */     OutputStream os = Files.newOutputStream(path, new java.nio.file.OpenOption[0]); 
/*  90 */     try { writeNamed(tag, os, compression);
/*  91 */       if (os != null) os.close();  }
/*     */     catch (Throwable throwable) { if (os != null)
/*     */         try { os.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  96 */      } public void writeNamed(Map.Entry<String, CompoundBinaryTag> tag, @NotNull OutputStream output, BinaryTagIO.Compression compression) throws IOException { DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(IOStreamUtil.closeShield(output)))); 
/*  97 */     try { writeNamed(tag, dos);
/*  98 */       dos.close(); }
/*     */     catch (Throwable throwable) { try { dos.close(); }
/*     */       catch (Throwable throwable1)
/*     */       { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 103 */      } public void writeNamed(Map.Entry<String, CompoundBinaryTag> tag, @NotNull DataOutput output) throws IOException { output.writeByte(BinaryTagTypes.COMPOUND.id());
/* 104 */     output.writeUTF(tag.getKey());
/* 105 */     BinaryTagTypes.COMPOUND.write(tag.getValue(), output); }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\BinaryTagWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;
/*     */ 
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public class ByteBufOutputStream
/*     */   extends OutputStream
/*     */   implements DataOutput
/*     */ {
/*     */   private final Object buffer;
/*     */   private final int startIndex;
/*  31 */   private final DataOutputStream utf8out = new DataOutputStream(this);
/*     */   
/*     */   public ByteBufOutputStream(Object buffer) {
/*  34 */     if (buffer == null) {
/*  35 */       throw new NullPointerException("buffer");
/*     */     }
/*  37 */     this.buffer = buffer;
/*  38 */     this.startIndex = ByteBufHelper.writerIndex(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writtenBytes() {
/*  43 */     return ByteBufHelper.writerIndex(this.buffer) - this.startIndex;
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  47 */     if (len != 0) {
/*  48 */       ByteBufHelper.writeBytes(this.buffer, b, off, len);
/*     */     }
/*     */   }
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  53 */     ByteBufHelper.writeBytes(this.buffer, b);
/*     */   }
/*     */   
/*     */   public void write(int b) throws IOException {
/*  57 */     ByteBufHelper.writeByte(this.buffer, b);
/*     */   }
/*     */   
/*     */   public void writeBoolean(boolean v) throws IOException {
/*  61 */     ByteBufHelper.writeBoolean(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeByte(int v) throws IOException {
/*  65 */     ByteBufHelper.writeByte(this.buffer, v);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void writeBytes(String s) throws IOException {
/*  70 */     throw new IllegalStateException("This operation is not supported!");
/*     */   }
/*     */   
/*     */   public void writeChar(int v) throws IOException {
/*  74 */     ByteBufHelper.writeChar(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeChars(String s) throws IOException {
/*  78 */     int len = s.length();
/*     */     
/*  80 */     for (int i = 0; i < len; i++) {
/*  81 */       ByteBufHelper.writeChar(this.buffer, s.charAt(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDouble(double v) throws IOException {
/*  87 */     ByteBufHelper.writeDouble(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeFloat(float v) throws IOException {
/*  91 */     ByteBufHelper.writeFloat(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeInt(int v) throws IOException {
/*  95 */     ByteBufHelper.writeInt(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeLong(long v) throws IOException {
/*  99 */     ByteBufHelper.writeLong(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeShort(int v) throws IOException {
/* 103 */     ByteBufHelper.writeShort(this.buffer, v);
/*     */   }
/*     */   
/*     */   public void writeUTF(String s) throws IOException {
/* 107 */     this.utf8out.writeUTF(s);
/*     */   }
/*     */   
/*     */   public Object buffer() {
/* 111 */     return this.buffer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\ByteBufOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
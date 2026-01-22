/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class ByteBufInputStream
/*     */   extends InputStream
/*     */   implements DataInput
/*     */ {
/*     */   private final Object buffer;
/*     */   private final int startIndex;
/*     */   private final int endIndex;
/*     */   private final boolean releaseOnClose;
/*     */   
/*     */   public ByteBufInputStream(Object buffer) {
/*  34 */     this(buffer, ByteBufHelper.readableBytes(buffer));
/*     */   }
/*     */   
/*     */   public ByteBufInputStream(Object buffer, int length) {
/*  38 */     this(buffer, length, false);
/*     */   }
/*     */   
/*     */   public ByteBufInputStream(Object buffer, boolean releaseOnClose) {
/*  42 */     this(buffer, ByteBufHelper.readableBytes(buffer), releaseOnClose);
/*     */   }
/*     */ 
/*     */   
/*  46 */   private final StringBuilder lineBuf = new StringBuilder(); public ByteBufInputStream(Object buffer, int maxLength, boolean releaseOnClose) {
/*  47 */     if (buffer == null)
/*  48 */       throw new NullPointerException("buffer"); 
/*  49 */     if (maxLength < 0) {
/*  50 */       if (releaseOnClose) {
/*  51 */         ByteBufHelper.release(buffer);
/*     */       }
/*     */       
/*  54 */       throw new IllegalArgumentException("maxLength: " + maxLength);
/*  55 */     }  if (ByteBufHelper.readableBytes(buffer) > maxLength) {
/*  56 */       if (releaseOnClose) {
/*  57 */         ByteBufHelper.release(buffer);
/*     */       }
/*     */       
/*  60 */       throw new IndexOutOfBoundsException("Too many bytes to be read - Found " + ByteBufHelper.readableBytes(buffer) + ", maximum is " + maxLength);
/*     */     } 
/*  62 */     this.releaseOnClose = releaseOnClose;
/*  63 */     this.buffer = buffer;
/*  64 */     this.startIndex = ByteBufHelper.readerIndex(buffer);
/*  65 */     this.endIndex = this.startIndex + ByteBufHelper.readableBytes(buffer);
/*  66 */     ByteBufHelper.markReaderIndex(buffer);
/*     */   }
/*     */   private boolean closed;
/*     */   
/*     */   public int readBytes() {
/*  71 */     return ByteBufHelper.readerIndex(this.buffer) - this.startIndex;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/*  76 */       super.close();
/*     */     } finally {
/*  78 */       if (this.releaseOnClose && !this.closed) {
/*  79 */         this.closed = true;
/*  80 */         ByteBufHelper.release(this.buffer);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  88 */     return this.endIndex - ByteBufHelper.readerIndex(this.buffer);
/*     */   }
/*     */   
/*     */   public void mark(int readlimit) {
/*  92 */     ByteBufHelper.markReaderIndex(this.buffer);
/*     */   }
/*     */   
/*     */   public boolean markSupported() {
/*  96 */     return true;
/*     */   }
/*     */   
/*     */   public int read() throws IOException {
/* 100 */     return !ByteBufHelper.isReadable(this.buffer) ? -1 : (ByteBufHelper.readByte(this.buffer) & 0xFF);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 105 */     int available = available();
/* 106 */     if (available == 0) {
/* 107 */       return -1;
/*     */     }
/* 109 */     len = Math.min(available, len);
/* 110 */     ByteBufHelper.readBytes(this.buffer, b, off, len);
/* 111 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 116 */     ByteBufHelper.resetReaderIndex(this.buffer);
/*     */   }
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 120 */     return (n > 2147483647L) ? skipBytes(2147483647) : skipBytes((int)n);
/*     */   }
/*     */   
/*     */   public boolean readBoolean() throws IOException {
/* 124 */     checkAvailable(1);
/* 125 */     return (read() != 0);
/*     */   }
/*     */   
/*     */   public byte[] readBytes(int len) {
/* 129 */     byte[] bytes = new byte[len];
/* 130 */     ByteBufHelper.readBytes(this.buffer, bytes);
/* 131 */     return bytes;
/*     */   }
/*     */   
/*     */   public byte readByte() throws IOException {
/* 135 */     if (!ByteBufHelper.isReadable(this.buffer)) {
/* 136 */       throw new EOFException();
/*     */     }
/* 138 */     return ByteBufHelper.readByte(this.buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public char readChar() throws IOException {
/* 143 */     return (char)readShort();
/*     */   }
/*     */   
/*     */   public double readDouble() throws IOException {
/* 147 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */   
/*     */   public float readFloat() throws IOException {
/* 151 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */   
/*     */   public void readFully(byte[] b) throws IOException {
/* 155 */     readFully(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public void readFully(byte[] b, int off, int len) throws IOException {
/* 159 */     checkAvailable(len);
/* 160 */     ByteBufHelper.readBytes(this.buffer, b, off, len);
/*     */   }
/*     */   
/*     */   public int readInt() throws IOException {
/* 164 */     checkAvailable(4);
/* 165 */     return ByteBufHelper.readInt(this.buffer);
/*     */   }
/*     */   
/*     */   public String readLine() throws IOException {
/* 169 */     this.lineBuf.setLength(0);
/*     */     
/* 171 */     while (ByteBufHelper.isReadable(this.buffer)) {
/* 172 */       int c = ByteBufHelper.readUnsignedByte(this.buffer);
/* 173 */       switch (c) {
/*     */         case 13:
/* 175 */           if (ByteBufHelper.isReadable(this.buffer) && 
/* 176 */             (char)ByteBufHelper.getUnsignedByte(this.buffer, ByteBufHelper.readerIndex(this.buffer)) == '\n') {
/* 177 */             ByteBufHelper.skipBytes(this.buffer, 1);
/*     */           }
/*     */         case 10:
/* 180 */           return this.lineBuf.toString();
/*     */       } 
/* 182 */       this.lineBuf.append((char)c);
/*     */     } 
/*     */ 
/*     */     
/* 186 */     return (this.lineBuf.length() > 0) ? this.lineBuf.toString() : null;
/*     */   }
/*     */   
/*     */   public long readLong() throws IOException {
/* 190 */     checkAvailable(8);
/* 191 */     return ByteBufHelper.readLong(this.buffer);
/*     */   }
/*     */   
/*     */   public long[] readLongs(int size) throws IOException {
/* 195 */     long[] array = new long[size];
/*     */     
/* 197 */     for (int i = 0; i < array.length; i++) {
/* 198 */       array[i] = readLong();
/*     */     }
/* 200 */     return array;
/*     */   }
/*     */   
/*     */   public short readShort() throws IOException {
/* 204 */     checkAvailable(2);
/* 205 */     return ByteBufHelper.readShort(this.buffer);
/*     */   }
/*     */   
/*     */   public String readUTF() throws IOException {
/* 209 */     String text = DataInputStream.readUTF(this);
/* 210 */     return text;
/*     */   }
/*     */   
/*     */   public int readUnsignedByte() throws IOException {
/* 214 */     return readByte() & 0xFF;
/*     */   }
/*     */   
/*     */   public int readUnsignedShort() throws IOException {
/* 218 */     return readShort() & 0xFFFF;
/*     */   }
/*     */   
/*     */   public int skipBytes(int n) throws IOException {
/* 222 */     int nBytes = Math.min(available(), n);
/* 223 */     ByteBufHelper.skipBytes(this.buffer, nBytes);
/* 224 */     return nBytes;
/*     */   }
/*     */   
/*     */   private void checkAvailable(int fieldSize) throws IOException {
/* 228 */     if (fieldSize < 0)
/* 229 */       throw new IndexOutOfBoundsException("fieldSize cannot be a negative number"); 
/* 230 */     if (fieldSize > available()) {
/* 231 */       int value = available();
/* 232 */       String msg = "fieldSize is too long! Length is " + fieldSize + ", but maximum is " + value;
/* 233 */       if (value == 0) {
/* 234 */         throw new PacketProcessException(msg);
/*     */       }
/*     */       
/* 237 */       throw new EOFException(msg);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\ByteBufInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
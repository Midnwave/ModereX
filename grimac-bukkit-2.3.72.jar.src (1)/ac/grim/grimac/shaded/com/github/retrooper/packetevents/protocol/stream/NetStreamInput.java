/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.UUID;
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
/*     */ @Deprecated
/*     */ public class NetStreamInput
/*     */   extends FilterInputStream
/*     */ {
/*     */   public NetStreamInput(InputStream in) {
/*  41 */     super(in);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readBoolean() {
/*  46 */     return (readByte() == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte() {
/*  51 */     return (byte)readUnsignedByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedByte() {
/*  56 */     int b = 0;
/*     */     try {
/*  58 */       b = read();
/*  59 */     } catch (IOException e) {
/*  60 */       e.printStackTrace();
/*     */     } 
/*  62 */     if (b < 0) {
/*  63 */       throw new IllegalStateException();
/*     */     }
/*     */     
/*  66 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort() {
/*  71 */     return (short)readUnsignedShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedShort() {
/*  76 */     int ch1 = readUnsignedByte();
/*  77 */     int ch2 = readUnsignedByte();
/*  78 */     return (ch1 << 8) + ch2;
/*     */   }
/*     */ 
/*     */   
/*     */   public char readChar() {
/*  83 */     return (char)readUnsignedShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt() {
/*  88 */     int ch1 = readUnsignedByte();
/*  89 */     int ch2 = readUnsignedByte();
/*  90 */     int ch3 = readUnsignedByte();
/*  91 */     int ch4 = readUnsignedByte();
/*  92 */     return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readVarInt() {
/*  97 */     int value = 0;
/*  98 */     int size = 0;
/*     */     int b;
/* 100 */     while (((b = readByte()) & 0x80) == 128) {
/* 101 */       value |= (b & 0x7F) << size++ * 7;
/* 102 */       if (size > 5) {
/* 103 */         throw new IllegalStateException("VarInt too long (length must be <= 5)");
/*     */       }
/*     */     } 
/*     */     
/* 107 */     return value | (b & 0x7F) << size * 7;
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong() {
/* 112 */     byte[] read = readBytes(8);
/* 113 */     return (read[0] << 56L) + ((read[1] & 0xFF) << 48L) + ((read[2] & 0xFF) << 40L) + ((read[3] & 0xFF) << 32L) + ((read[4] & 0xFF) << 24L) + ((read[5] & 0xFF) << 16) + ((read[6] & 0xFF) << 8) + ((read[7] & 0xFF) << 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long readVarLong() {
/* 118 */     long value = 0L;
/* 119 */     int size = 0;
/*     */     int b;
/* 121 */     while (((b = readByte()) & 0x80) == 128) {
/* 122 */       value |= (b & 0x7F) << size++ * 7;
/* 123 */       if (size > 10) {
/* 124 */         throw new IllegalStateException("VarLong too long (length must be <= 10)");
/*     */       }
/*     */     } 
/*     */     
/* 128 */     return value | (b & 0x7F) << size * 7;
/*     */   }
/*     */ 
/*     */   
/*     */   public float readFloat() {
/* 133 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public double readDouble() {
/* 138 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] readBytes(int length) {
/* 143 */     if (length < 0) {
/* 144 */       throw new IllegalArgumentException("Array cannot have length less than 0.");
/*     */     }
/*     */     
/* 147 */     byte[] b = new byte[length];
/* 148 */     int n = 0;
/* 149 */     while (n < length) {
/* 150 */       int count = 0;
/*     */       try {
/* 152 */         count = read(b, n, length - n);
/* 153 */       } catch (IOException e) {
/* 154 */         e.printStackTrace();
/*     */       } 
/* 156 */       if (count < 0) {
/* 157 */         throw new IllegalStateException();
/*     */       }
/*     */       
/* 160 */       n += count;
/*     */     } 
/*     */     
/* 163 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(byte[] b) {
/*     */     try {
/* 169 */       return read(b);
/* 170 */     } catch (IOException e) {
/* 171 */       e.printStackTrace();
/*     */       
/* 173 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int readBytes(byte[] b, int offset, int length) {
/*     */     try {
/* 179 */       return read(b, offset, length);
/* 180 */     } catch (IOException e) {
/* 181 */       e.printStackTrace();
/*     */       
/* 183 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public short[] readShorts(int length) {
/* 188 */     if (length < 0) {
/* 189 */       throw new IllegalArgumentException("Array cannot have length less than 0.");
/*     */     }
/*     */     
/* 192 */     short[] s = new short[length];
/* 193 */     int read = readShorts(s);
/* 194 */     if (read < length) {
/* 195 */       throw new IllegalStateException();
/*     */     }
/*     */     
/* 198 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readShorts(short[] s) {
/* 203 */     return readShorts(s, 0, s.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readShorts(short[] s, int offset, int length) {
/* 208 */     for (int index = offset; index < offset + length; index++) {
/*     */       try {
/* 210 */         s[index] = readShort();
/* 211 */       } catch (Exception e) {
/* 212 */         return index - offset;
/*     */       } 
/*     */     } 
/*     */     
/* 216 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] readInts(int length) {
/* 221 */     if (length < 0) {
/* 222 */       throw new IllegalArgumentException("Array cannot have length less than 0.");
/*     */     }
/*     */     
/* 225 */     int[] i = new int[length];
/* 226 */     int read = readInts(i);
/* 227 */     if (read < length) {
/* 228 */       throw new IllegalStateException();
/*     */     }
/*     */     
/* 231 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInts(int[] i) {
/* 236 */     return readInts(i, 0, i.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInts(int[] i, int offset, int length) {
/* 241 */     for (int index = offset; index < offset + length; index++) {
/*     */       try {
/* 243 */         i[index] = readInt();
/* 244 */       } catch (Exception e) {
/* 245 */         return index - offset;
/*     */       } 
/*     */     } 
/*     */     
/* 249 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] readLongs(int length) {
/* 254 */     if (length < 0) {
/* 255 */       throw new IllegalArgumentException("Array cannot have length less than 0.");
/*     */     }
/*     */     
/* 258 */     long[] l = new long[length];
/* 259 */     int read = readLongs(l);
/* 260 */     if (read < length) {
/* 261 */       throw new IllegalStateException();
/*     */     }
/*     */     
/* 264 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readLongs(long[] l) {
/* 269 */     return readLongs(l, 0, l.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readLongs(long[] l, int offset, int length) {
/* 274 */     for (int index = offset; index < offset + length; index++) {
/*     */       try {
/* 276 */         l[index] = readLong();
/* 277 */       } catch (Exception e) {
/* 278 */         return index - offset;
/*     */       } 
/*     */     } 
/*     */     
/* 282 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readString() {
/* 287 */     int length = readVarInt();
/* 288 */     byte[] bytes = readBytes(length);
/* 289 */     return new String(bytes, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */   
/*     */   public UUID readUUID() {
/* 294 */     return new UUID(readLong(), readLong());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\stream\NetStreamInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
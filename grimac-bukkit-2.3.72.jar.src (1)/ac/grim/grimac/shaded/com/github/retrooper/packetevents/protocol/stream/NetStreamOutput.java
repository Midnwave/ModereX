/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ @Deprecated
/*     */ public class NetStreamOutput
/*     */   extends FilterOutputStream
/*     */ {
/*     */   public NetStreamOutput(OutputStream out) {
/*  36 */     super(out);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBoolean(boolean b) {
/*  41 */     writeByte(b ? 1 : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeByte(int b) {
/*     */     try {
/*  47 */       write(b);
/*  48 */     } catch (IOException e) {
/*  49 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShort(int s) {
/*  55 */     writeByte((byte)(s >>> 8 & 0xFF));
/*  56 */     writeByte((byte)(s >>> 0 & 0xFF));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeChar(int c) {
/*  61 */     writeShort(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt(int i) {
/*  66 */     writeByte((byte)(i >>> 24 & 0xFF));
/*  67 */     writeByte((byte)(i >>> 16 & 0xFF));
/*  68 */     writeByte((byte)(i >>> 8 & 0xFF));
/*  69 */     writeByte((byte)(i >>> 0 & 0xFF));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeVarInt(int i) {
/*  74 */     while ((i & 0xFFFFFF80) != 0) {
/*  75 */       writeByte(i & 0x7F | 0x80);
/*  76 */       i >>>= 7;
/*     */     } 
/*     */     
/*  79 */     writeByte(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLong(long l) {
/*  84 */     writeByte((byte)(int)(l >>> 56L));
/*  85 */     writeByte((byte)(int)(l >>> 48L));
/*  86 */     writeByte((byte)(int)(l >>> 40L));
/*  87 */     writeByte((byte)(int)(l >>> 32L));
/*  88 */     writeByte((byte)(int)(l >>> 24L));
/*  89 */     writeByte((byte)(int)(l >>> 16L));
/*  90 */     writeByte((byte)(int)(l >>> 8L));
/*  91 */     writeByte((byte)(int)(l >>> 0L));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeVarLong(long l) {
/*  96 */     while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
/*  97 */       writeByte((int)(l & 0x7FL) | 0x80);
/*  98 */       l >>>= 7L;
/*     */     } 
/*     */     
/* 101 */     writeByte((int)l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFloat(float f) {
/* 106 */     writeInt(Float.floatToIntBits(f));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDouble(double d) {
/* 111 */     writeLong(Double.doubleToLongBits(d));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBytes(byte[] b) {
/* 116 */     writeBytes(b, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBytes(byte[] b, int length) {
/*     */     try {
/* 122 */       write(b, 0, length);
/* 123 */     } catch (IOException e) {
/* 124 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShorts(short[] s) {
/* 130 */     writeShorts(s, s.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShorts(short[] s, int length) {
/* 135 */     for (int index = 0; index < length; index++) {
/* 136 */       writeShort(s[index]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInts(int[] i) {
/* 142 */     writeInts(i, i.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInts(int[] i, int length) {
/* 147 */     for (int index = 0; index < length; index++) {
/* 148 */       writeInt(i[index]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLongs(long[] l) {
/* 154 */     writeLongs(l, l.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLongs(long[] l, int length) {
/* 159 */     for (int index = 0; index < length; index++) {
/* 160 */       writeLong(l[index]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(String s) {
/* 166 */     if (s == null) {
/* 167 */       throw new IllegalArgumentException("String cannot be null!");
/*     */     }
/*     */     
/* 170 */     byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
/* 171 */     if (bytes.length > 32767) {
/*     */       try {
/* 173 */         throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + '翿' + ")");
/* 174 */       } catch (IOException e) {
/* 175 */         e.printStackTrace();
/*     */       } 
/*     */     } else {
/* 178 */       writeVarInt(bytes.length);
/* 179 */       writeBytes(bytes);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeUUID(UUID uuid) {
/* 185 */     writeLong(uuid.getMostSignificantBits());
/* 186 */     writeLong(uuid.getLeastSignificantBits());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\stream\NetStreamOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
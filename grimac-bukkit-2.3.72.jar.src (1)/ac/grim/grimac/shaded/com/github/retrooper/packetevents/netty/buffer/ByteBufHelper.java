/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class ByteBufHelper
/*     */ {
/*     */   public static int capacity(Object buffer) {
/*  27 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().capacity(buffer);
/*     */   }
/*     */   
/*     */   public static Object capacity(Object buffer, int capacity) {
/*  31 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().capacity(buffer, capacity);
/*     */   }
/*     */   
/*     */   public static int readerIndex(Object buffer) {
/*  35 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readerIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object readerIndex(Object buffer, int readerIndex) {
/*  39 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readerIndex(buffer, readerIndex);
/*     */   }
/*     */   
/*     */   public static int writerIndex(Object buffer) {
/*  43 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().writerIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object writerIndex(Object buffer, int writerIndex) {
/*  47 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().writerIndex(buffer, writerIndex);
/*     */   }
/*     */   
/*     */   public static int readableBytes(Object buffer) {
/*  51 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readableBytes(buffer);
/*     */   }
/*     */   
/*     */   public static int writableBytes(Object buffer) {
/*  55 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().writableBytes(buffer);
/*     */   }
/*     */   
/*     */   public static Object clear(Object buffer) {
/*  59 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().clear(buffer);
/*     */   }
/*     */   
/*     */   public static String toString(Object buffer, int index, int length, Charset charset) {
/*  63 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().toString(buffer, index, length, charset);
/*     */   }
/*     */   
/*     */   public static byte readByte(Object buffer) {
/*  67 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readByte(buffer);
/*     */   }
/*     */   
/*     */   public static void writeByte(Object buffer, int value) {
/*  71 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeByte(buffer, value);
/*     */   }
/*     */   
/*     */   public static boolean readBoolean(Object buffer) {
/*  75 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readBoolean(buffer);
/*     */   }
/*     */   
/*     */   public static void writeBoolean(Object buffer, boolean value) {
/*  79 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeBoolean(buffer, value);
/*     */   }
/*     */   
/*     */   public static short readUnsignedByte(Object buffer) {
/*  83 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readUnsignedByte(buffer);
/*     */   }
/*     */   
/*     */   public static char readChar(Object buffer) {
/*  87 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readChar(buffer);
/*     */   }
/*     */   
/*     */   public static void writeChar(Object buffer, int value) {
/*  91 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeChar(buffer, value);
/*     */   }
/*     */   
/*     */   public static short readShort(Object buffer) {
/*  95 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readShort(buffer);
/*     */   }
/*     */   
/*     */   public static int readUnsignedShort(Object buffer) {
/*  99 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readUnsignedShort(buffer);
/*     */   }
/*     */   
/*     */   public static void writeShort(Object buffer, int value) {
/* 103 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeShort(buffer, value);
/*     */   }
/*     */   
/*     */   public static int readMedium(Object buffer) {
/* 107 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readMedium(buffer);
/*     */   }
/*     */   
/*     */   public static void writeMedium(Object buffer, int value) {
/* 111 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeMedium(buffer, value);
/*     */   }
/*     */   
/*     */   public static int readInt(Object buffer) {
/* 115 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readInt(buffer);
/*     */   }
/*     */   
/*     */   public static void writeInt(Object buffer, int value) {
/* 119 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeInt(buffer, value);
/*     */   }
/*     */   
/*     */   public static long readUnsignedInt(Object buffer) {
/* 123 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readUnsignedInt(buffer);
/*     */   }
/*     */   
/*     */   public static long readLong(Object buffer) {
/* 127 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readLong(buffer);
/*     */   }
/*     */   
/*     */   public static void writeLong(Object buffer, long value) {
/* 131 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeLong(buffer, value);
/*     */   }
/*     */   
/*     */   public static float readFloat(Object buffer) {
/* 135 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readFloat(buffer);
/*     */   }
/*     */   
/*     */   public static void writeFloat(Object buffer, float value) {
/* 139 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeFloat(buffer, value);
/*     */   }
/*     */   
/*     */   public static double readDouble(Object buffer) {
/* 143 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readDouble(buffer);
/*     */   }
/*     */   
/*     */   public static void writeDouble(Object buffer, double value) {
/* 147 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeDouble(buffer, value);
/*     */   }
/*     */   
/*     */   public static Object getBytes(Object buffer, int index, byte[] destination) {
/* 151 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().getBytes(buffer, index, destination);
/*     */   }
/*     */   
/*     */   public static short getUnsignedByte(Object buffer, int index) {
/* 155 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().getUnsignedByte(buffer, index);
/*     */   }
/*     */   
/*     */   public static boolean isReadable(Object buffer) {
/* 159 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().isReadable(buffer);
/*     */   }
/*     */   
/*     */   public static Object copy(Object buffer) {
/* 163 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().copy(buffer);
/*     */   }
/*     */   
/*     */   public static Object duplicate(Object buffer) {
/* 167 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().duplicate(buffer);
/*     */   }
/*     */   
/*     */   public static boolean hasArray(Object buffer) {
/* 171 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().hasArray(buffer);
/*     */   }
/*     */   
/*     */   public static byte[] array(Object buffer) {
/* 175 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().array(buffer);
/*     */   }
/*     */   
/*     */   public static Object retain(Object buffer) {
/* 179 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().retain(buffer);
/*     */   }
/*     */   
/*     */   public static Object retainedDuplicate(Object buffer) {
/* 183 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().retainedDuplicate(buffer);
/*     */   }
/*     */   
/*     */   public static Object readSlice(Object buffer, int length) {
/* 187 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readSlice(buffer, length);
/*     */   }
/*     */   
/*     */   public static Object readBytes(Object buffer, byte[] destination, int destinationIndex, int length) {
/* 191 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readBytes(buffer, destination, destinationIndex, length);
/*     */   }
/*     */   
/*     */   public static Object readBytes(Object buffer, int length) {
/* 195 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().readBytes(buffer, length);
/*     */   }
/*     */   
/*     */   public static Object writeBytes(Object buffer, Object src) {
/* 199 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeBytes(buffer, src);
/*     */   }
/*     */   
/*     */   public static void readBytes(Object buffer, byte[] bytes) {
/* 203 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().readBytes(buffer, bytes);
/*     */   }
/*     */   
/*     */   public static void writeBytes(Object buffer, byte[] bytes) {
/* 207 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeBytes(buffer, bytes);
/*     */   }
/*     */   
/*     */   public static void writeBytes(Object buffer, byte[] bytes, int offset, int length) {
/* 211 */     PacketEvents.getAPI().getNettyManager().getByteBufOperator().writeBytes(buffer, bytes, offset, length);
/*     */   }
/*     */   
/*     */   public static boolean release(Object buffer) {
/* 215 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().release(buffer);
/*     */   }
/*     */   
/*     */   public static int refCnt(Object buffer) {
/* 219 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().refCnt(buffer);
/*     */   }
/*     */   
/*     */   public static Object skipBytes(Object buffer, int length) {
/* 223 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().skipBytes(buffer, length);
/*     */   }
/*     */   
/*     */   public static Object markReaderIndex(Object buffer) {
/* 227 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().markReaderIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object resetReaderIndex(Object buffer) {
/* 231 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().resetReaderIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object markWriterIndex(Object buffer) {
/* 235 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().markWriterIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object resetWriterIndex(Object buffer) {
/* 239 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().resetWriterIndex(buffer);
/*     */   }
/*     */   
/*     */   public static Object allocateNewBuffer(Object buffer) {
/* 243 */     return PacketEvents.getAPI().getNettyManager().getByteBufOperator().allocateNewBuffer(buffer);
/*     */   }
/*     */   
/*     */   public static int getByteSize(int value) {
/* 247 */     for (int i = 1; i < 5; i++) {
/* 248 */       if ((value & -1 << i * 7) == 0) {
/* 249 */         return i;
/*     */       }
/*     */     } 
/* 252 */     return 5;
/*     */   }
/*     */   
/*     */   public static int readVarInt(Object buffer) {
/* 256 */     int value = 0;
/* 257 */     int length = 0;
/*     */     
/*     */     while (true) {
/* 260 */       byte currentByte = readByte(buffer);
/* 261 */       value |= (currentByte & Byte.MAX_VALUE) << length * 7;
/* 262 */       length++;
/* 263 */       if (length > 5) {
/* 264 */         throw new RuntimeException("VarInt is too large. Must be smaller than 5 bytes.");
/*     */       }
/* 266 */       if ((currentByte & 0x80) != 128)
/* 267 */         return value; 
/*     */     } 
/*     */   }
/*     */   public static void writeVarInt(Object buffer, int value) {
/*     */     while (true) {
/* 272 */       if ((value & 0xFFFFFF80) == 0) {
/* 273 */         writeByte(buffer, value);
/*     */         return;
/*     */       } 
/* 276 */       writeByte(buffer, value & 0x7F | 0x80);
/* 277 */       value >>>= 7;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static byte[] copyBytes(Object buffer) {
/* 282 */     byte[] bytes = new byte[readableBytes(buffer)];
/* 283 */     getBytes(buffer, readerIndex(buffer), bytes);
/* 284 */     return bytes;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\ByteBufHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
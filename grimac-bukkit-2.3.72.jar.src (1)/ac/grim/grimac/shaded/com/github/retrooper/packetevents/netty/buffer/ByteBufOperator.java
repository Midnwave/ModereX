/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;
/*     */ 
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
/*     */ public interface ByteBufOperator
/*     */ {
/*     */   int capacity(Object paramObject);
/*     */   
/*     */   Object capacity(Object paramObject, int paramInt);
/*     */   
/*     */   int readerIndex(Object paramObject);
/*     */   
/*     */   Object readerIndex(Object paramObject, int paramInt);
/*     */   
/*     */   int writerIndex(Object paramObject);
/*     */   
/*     */   Object writerIndex(Object paramObject, int paramInt);
/*     */   
/*     */   int readableBytes(Object paramObject);
/*     */   
/*     */   int writableBytes(Object paramObject);
/*     */   
/*     */   Object clear(Object paramObject);
/*     */   
/*     */   byte readByte(Object paramObject);
/*     */   
/*     */   short readShort(Object paramObject);
/*     */   
/*     */   int readMedium(Object paramObject);
/*     */   
/*     */   int readInt(Object paramObject);
/*     */   
/*     */   long readUnsignedInt(Object paramObject);
/*     */   
/*     */   long readLong(Object paramObject);
/*     */   
/*     */   void writeByte(Object paramObject, int paramInt);
/*     */   
/*     */   void writeShort(Object paramObject, int paramInt);
/*     */   
/*     */   void writeMedium(Object paramObject, int paramInt);
/*     */   
/*     */   void writeInt(Object paramObject, int paramInt);
/*     */   
/*     */   void writeLong(Object paramObject, long paramLong);
/*     */   
/*     */   Object getBytes(Object paramObject, int paramInt, byte[] paramArrayOfbyte);
/*     */   
/*     */   short getUnsignedByte(Object paramObject, int paramInt);
/*     */   
/*     */   default float readFloat(Object buffer) {
/*  80 */     return Float.intBitsToFloat(readInt(buffer));
/*     */   } boolean isReadable(Object paramObject); Object copy(Object paramObject); Object duplicate(Object paramObject); boolean hasArray(Object paramObject); byte[] array(Object paramObject); Object retain(Object paramObject); Object retainedDuplicate(Object paramObject); Object readSlice(Object paramObject, int paramInt); Object readBytes(Object paramObject, byte[] paramArrayOfbyte, int paramInt1, int paramInt2); Object readBytes(Object paramObject, int paramInt); void readBytes(Object paramObject, byte[] paramArrayOfbyte); Object writeBytes(Object paramObject1, Object paramObject2); Object writeBytes(Object paramObject, byte[] paramArrayOfbyte); Object writeBytes(Object paramObject, byte[] paramArrayOfbyte, int paramInt1, int paramInt2); boolean release(Object paramObject); int refCnt(Object paramObject); Object skipBytes(Object paramObject, int paramInt); String toString(Object paramObject, int paramInt1, int paramInt2, Charset paramCharset); Object markReaderIndex(Object paramObject); Object resetReaderIndex(Object paramObject); Object markWriterIndex(Object paramObject); Object resetWriterIndex(Object paramObject);
/*     */   Object allocateNewBuffer(Object paramObject);
/*     */   default void writeFloat(Object buffer, float value) {
/*  84 */     writeInt(buffer, Float.floatToIntBits(value));
/*     */   }
/*     */   
/*     */   default double readDouble(Object buffer) {
/*  88 */     return Double.longBitsToDouble(readLong(buffer));
/*     */   }
/*     */   
/*     */   default void writeDouble(Object buffer, double value) {
/*  92 */     writeLong(buffer, Double.doubleToLongBits(value));
/*     */   }
/*     */   
/*     */   default char readChar(Object buffer) {
/*  96 */     return (char)readShort(buffer);
/*     */   }
/*     */   
/*     */   default void writeChar(Object buffer, int value) {
/* 100 */     writeShort(buffer, value);
/*     */   }
/*     */ 
/*     */   
/*     */   default int readUnsignedShort(Object buffer) {
/* 105 */     return readShort(buffer) & 0xFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   default short readUnsignedByte(Object buffer) {
/* 110 */     return (short)(readByte(buffer) & 0xFF);
/*     */   }
/*     */   
/*     */   default boolean readBoolean(Object buffer) {
/* 114 */     return (readByte(buffer) != 0);
/*     */   }
/*     */   
/*     */   default void writeBoolean(Object buffer, boolean value) {
/* 118 */     writeByte(buffer, value ? 1 : 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\ByteBufOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
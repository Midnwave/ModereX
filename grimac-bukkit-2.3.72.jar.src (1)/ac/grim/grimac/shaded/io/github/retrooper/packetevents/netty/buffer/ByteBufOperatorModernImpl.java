/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.buffer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufOperator;
/*     */ import io.netty.buffer.ByteBuf;
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
/*     */ public class ByteBufOperatorModernImpl
/*     */   implements ByteBufOperator
/*     */ {
/*     */   public int capacity(Object buffer) {
/*  29 */     return ((ByteBuf)buffer).capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object capacity(Object buffer, int capacity) {
/*  34 */     return ((ByteBuf)buffer).capacity(capacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readerIndex(Object buffer) {
/*  39 */     return ((ByteBuf)buffer).readerIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readerIndex(Object buffer, int readerIndex) {
/*  44 */     return ((ByteBuf)buffer).readerIndex(readerIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writerIndex(Object buffer) {
/*  49 */     return ((ByteBuf)buffer).writerIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writerIndex(Object buffer, int writerIndex) {
/*  54 */     return ((ByteBuf)buffer).writerIndex(writerIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readableBytes(Object buffer) {
/*  59 */     return ((ByteBuf)buffer).readableBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public int writableBytes(Object buffer) {
/*  64 */     return ((ByteBuf)buffer).writableBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clear(Object buffer) {
/*  69 */     return ((ByteBuf)buffer).clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte(Object buffer) {
/*  74 */     return ((ByteBuf)buffer).readByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort(Object buffer) {
/*  79 */     return ((ByteBuf)buffer).readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readMedium(Object buffer) {
/*  84 */     return ((ByteBuf)buffer).readMedium();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt(Object buffer) {
/*  89 */     return ((ByteBuf)buffer).readInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readUnsignedInt(Object buffer) {
/*  94 */     return ((ByteBuf)buffer).readUnsignedInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong(Object buffer) {
/*  99 */     return ((ByteBuf)buffer).readLong();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeByte(Object buffer, int value) {
/* 105 */     ((ByteBuf)buffer).writeByte(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShort(Object buffer, int value) {
/* 110 */     ((ByteBuf)buffer).writeShort(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeMedium(Object buffer, int value) {
/* 116 */     ((ByteBuf)buffer).writeMedium(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt(Object buffer, int value) {
/* 121 */     ((ByteBuf)buffer).writeInt(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLong(Object buffer, long value) {
/* 126 */     ((ByteBuf)buffer).writeLong(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getBytes(Object buffer, int index, byte[] destination) {
/* 131 */     return ((ByteBuf)buffer).getBytes(index, destination);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getUnsignedByte(Object buffer, int index) {
/* 136 */     return ((ByteBuf)buffer).getUnsignedByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadable(Object buffer) {
/* 141 */     return ((ByteBuf)buffer).isReadable();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object copy(Object buffer) {
/* 146 */     return ((ByteBuf)buffer).copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object duplicate(Object buffer) {
/* 151 */     return ((ByteBuf)buffer).duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray(Object buffer) {
/* 156 */     return ((ByteBuf)buffer).hasArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array(Object buffer) {
/* 161 */     return ((ByteBuf)buffer).array();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object retain(Object buffer) {
/* 166 */     return ((ByteBuf)buffer).retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object retainedDuplicate(Object buffer) {
/* 171 */     return ((ByteBuf)buffer).duplicate().retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readSlice(Object buffer, int length) {
/* 176 */     return ((ByteBuf)buffer).readSlice(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readBytes(Object buffer, byte[] destination, int destinationIndex, int length) {
/* 181 */     return ((ByteBuf)buffer).readBytes(destination, destinationIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readBytes(Object buffer, int length) {
/* 186 */     return ((ByteBuf)buffer).readBytes(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeBytes(Object buffer, Object src) {
/* 191 */     return ((ByteBuf)buffer).writeBytes((ByteBuf)src);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeBytes(Object buffer, byte[] bytes) {
/* 196 */     return ((ByteBuf)buffer).writeBytes(bytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeBytes(Object buffer, byte[] bytes, int offset, int length) {
/* 201 */     return ((ByteBuf)buffer).writeBytes(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readBytes(Object buffer, byte[] bytes) {
/* 206 */     ((ByteBuf)buffer).readBytes(bytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(Object buffer) {
/* 211 */     return ((ByteBuf)buffer).release();
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt(Object buffer) {
/* 216 */     return ((ByteBuf)buffer).refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object skipBytes(Object buffer, int length) {
/* 221 */     return ((ByteBuf)buffer).skipBytes(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString(Object buffer, int index, int length, Charset charset) {
/* 226 */     return ((ByteBuf)buffer).toString(index, length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object markReaderIndex(Object buffer) {
/* 231 */     return ((ByteBuf)buffer).markReaderIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object resetReaderIndex(Object buffer) {
/* 236 */     return ((ByteBuf)buffer).resetReaderIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object markWriterIndex(Object buffer) {
/* 241 */     return ((ByteBuf)buffer).markWriterIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object resetWriterIndex(Object buffer) {
/* 246 */     return ((ByteBuf)buffer).resetWriterIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object allocateNewBuffer(Object buffer) {
/* 251 */     return ((ByteBuf)buffer).alloc().buffer();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\netty\buffer\ByteBufOperatorModernImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
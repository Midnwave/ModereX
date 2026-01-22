/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Internal
/*    */ public class NetStreamInputWrapper
/*    */   extends NetStreamInput
/*    */ {
/*    */   private final PacketWrapper<?> wrapper;
/*    */   
/*    */   public NetStreamInputWrapper(PacketWrapper<?> wrapper) {
/* 31 */     super(null);
/* 32 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() {
/* 37 */     return this.wrapper.readUnsignedByte();
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b) {
/* 42 */     return read(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) {
/* 47 */     int ri = ByteBufHelper.readerIndex(this.wrapper.buffer);
/* 48 */     ByteBufHelper.readBytes(this.wrapper.buffer, b, off, len);
/* 49 */     return ByteBufHelper.readerIndex(this.wrapper.buffer) - ri;
/*    */   }
/*    */ 
/*    */   
/*    */   public long skip(long n) {
/* 54 */     int ri = ByteBufHelper.readerIndex(this.wrapper.buffer);
/* 55 */     ByteBufHelper.skipBytes(this.wrapper.buffer, (int)n);
/* 56 */     return (ByteBufHelper.readerIndex(this.wrapper.buffer) - ri);
/*    */   }
/*    */ 
/*    */   
/*    */   public int available() {
/* 61 */     return ByteBufHelper.readableBytes(this.wrapper.buffer);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void mark(int readlimit) {
/* 71 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 76 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean markSupported() {
/* 81 */     return false;
/*    */   }
/*    */   
/*    */   public PacketWrapper<?> getWrapper() {
/* 85 */     return this.wrapper;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\stream\NetStreamInputWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
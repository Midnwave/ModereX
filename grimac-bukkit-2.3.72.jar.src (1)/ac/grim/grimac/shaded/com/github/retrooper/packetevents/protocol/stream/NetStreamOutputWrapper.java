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
/*    */ public class NetStreamOutputWrapper
/*    */   extends NetStreamOutput
/*    */ {
/*    */   private final PacketWrapper<?> wrapper;
/*    */   
/*    */   public NetStreamOutputWrapper(PacketWrapper<?> wrapper) {
/* 31 */     super(null);
/* 32 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) {
/* 37 */     this.wrapper.writeByte(b);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b) {
/* 42 */     write(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) {
/* 47 */     ByteBufHelper.writeBytes(this.wrapper.buffer, b, off, len);
/*    */   }
/*    */   
/*    */   public void flush() {}
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\stream\NetStreamOutputWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
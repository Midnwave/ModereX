/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.buffer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufAllocationOperator;
/*    */ import io.netty.buffer.Unpooled;
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
/*    */ public class ByteBufAllocationOperatorModernImpl
/*    */   implements ByteBufAllocationOperator
/*    */ {
/*    */   public Object wrappedBuffer(byte[] bytes) {
/* 27 */     return Unpooled.wrappedBuffer(bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object copiedBuffer(byte[] bytes) {
/* 32 */     return Unpooled.copiedBuffer(bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object buffer() {
/* 37 */     return Unpooled.buffer();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object buffer(int initialCapacity) {
/* 42 */     return Unpooled.buffer(initialCapacity);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object directBuffer() {
/* 47 */     return Unpooled.directBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object directBuffer(int initialCapacity) {
/* 52 */     return Unpooled.directBuffer(initialCapacity);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object compositeBuffer() {
/* 57 */     return Unpooled.compositeBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object compositeBuffer(int maxNumComponents) {
/* 62 */     return Unpooled.compositeBuffer(maxNumComponents);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object emptyBuffer() {
/* 67 */     return Unpooled.EMPTY_BUFFER;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\netty\buffer\ByteBufAllocationOperatorModernImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
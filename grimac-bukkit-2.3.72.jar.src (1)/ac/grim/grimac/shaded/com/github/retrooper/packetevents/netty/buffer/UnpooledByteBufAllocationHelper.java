/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
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
/*    */ public class UnpooledByteBufAllocationHelper
/*    */ {
/*    */   public static Object wrappedBuffer(byte[] bytes) {
/* 25 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().wrappedBuffer(bytes);
/*    */   }
/*    */   
/*    */   public static Object copiedBuffer(byte[] bytes) {
/* 29 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().copiedBuffer(bytes);
/*    */   }
/*    */   
/*    */   public static Object buffer() {
/* 33 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
/*    */   }
/*    */   
/*    */   public static Object directBuffer() {
/* 37 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().directBuffer();
/*    */   }
/*    */   
/*    */   public static Object compositeBuffer() {
/* 41 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().compositeBuffer();
/*    */   }
/*    */   
/*    */   public static Object buffer(int initialCapacity) {
/* 45 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer(initialCapacity);
/*    */   }
/*    */   
/*    */   public static Object directBuffer(int initialCapacity) {
/* 49 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().directBuffer(initialCapacity);
/*    */   }
/*    */   
/*    */   public static Object compositeBuffer(int maxNumComponents) {
/* 53 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().compositeBuffer(maxNumComponents);
/*    */   }
/*    */   
/*    */   public static Object emptyBuffer() {
/* 57 */     return PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().emptyBuffer();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\UnpooledByteBufAllocationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
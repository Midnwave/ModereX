/*    */ package ac.grim.grimac.utils.payload;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PayloadCodec<P>
/*    */ {
/*    */   private final Function<PacketWrapper<?>, P> reader;
/*    */   private final BiConsumer<PacketWrapper<?>, P> writer;
/*    */   
/*    */   @Contract(pure = true)
/*    */   public PayloadCodec(Function<PacketWrapper<?>, P> reader, BiConsumer<PacketWrapper<?>, P> writer) {
/* 21 */     this.reader = reader;
/* 22 */     this.writer = writer;
/*    */   }
/*    */   
/*    */   public P read(byte[] data) {
/* 26 */     Object buffer = UnpooledByteBufAllocationHelper.copiedBuffer(data);
/* 27 */     P payload = this.reader.apply(PacketWrapper.createUniversalPacketWrapper(buffer));
/* 28 */     ByteBufHelper.release(buffer);
/* 29 */     return payload;
/*    */   }
/*    */   
/*    */   public byte[] write(P payload) {
/* 33 */     Object buffer = UnpooledByteBufAllocationHelper.buffer();
/* 34 */     this.writer.accept(PacketWrapper.createUniversalPacketWrapper(buffer), payload);
/* 35 */     byte[] bytes = ByteBufHelper.array(buffer);
/* 36 */     ByteBufHelper.release(buffer);
/* 37 */     return bytes;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\payload\PayloadCodec.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
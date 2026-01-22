/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.NettyManager;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufAllocationOperator;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufOperator;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelOperator;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.buffer.ByteBufAllocationOperatorModernImpl;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.buffer.ByteBufOperatorModernImpl;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.channel.ChannelOperatorModernImpl;
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
/*    */ public class NettyManagerImpl
/*    */   implements NettyManager
/*    */ {
/* 30 */   private static final ByteBufOperator BYTE_BUF_OPERATOR = (ByteBufOperator)new ByteBufOperatorModernImpl();
/* 31 */   private static final ByteBufAllocationOperator BYTE_BUF_ALLOCATION_OPERATOR = (ByteBufAllocationOperator)new ByteBufAllocationOperatorModernImpl();
/* 32 */   private static final ChannelOperator CHANNEL_OPERATOR = (ChannelOperator)new ChannelOperatorModernImpl();
/*    */ 
/*    */   
/*    */   public ChannelOperator getChannelOperator() {
/* 36 */     return CHANNEL_OPERATOR;
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBufOperator getByteBufOperator() {
/* 41 */     return BYTE_BUF_OPERATOR;
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBufAllocationOperator getByteBufAllocationOperator() {
/* 46 */     return BYTE_BUF_ALLOCATION_OPERATOR;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\netty\NettyManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
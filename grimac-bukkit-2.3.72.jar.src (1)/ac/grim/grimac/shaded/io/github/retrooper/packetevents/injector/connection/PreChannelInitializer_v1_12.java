/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*    */ import io.netty.channel.ChannelHandler;
/*    */ import io.netty.channel.ChannelHandlerContext;
/*    */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*    */ import io.netty.channel.ChannelInitializer;
/*    */ import io.netty.channel.ChannelPipeline;
/*    */ import io.netty.util.internal.logging.InternalLogger;
/*    */ import io.netty.util.internal.logging.InternalLoggerFactory;
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
/*    */ public class PreChannelInitializer_v1_12
/*    */   extends ChannelInboundHandlerAdapter
/*    */ {
/* 29 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
/*    */ 
/*    */   
/*    */   public void channelRegistered(ChannelHandlerContext ctx) {
/*    */     try {
/* 34 */       ServerConnectionInitializer.initChannel(ctx.channel(), ConnectionState.HANDSHAKING);
/* 35 */     } catch (Throwable t) {
/* 36 */       exceptionCaught(ctx, t);
/*    */     } finally {
/* 38 */       ChannelPipeline pipeline = ctx.pipeline();
/* 39 */       if (pipeline.context((ChannelHandler)this) != null) {
/* 40 */         pipeline.remove((ChannelHandler)this);
/*    */       }
/*    */     } 
/*    */     
/* 44 */     ctx.pipeline().fireChannelRegistered();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
/* 50 */     logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), t);
/* 51 */     ctx.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\injector\connection\PreChannelInitializer_v1_12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
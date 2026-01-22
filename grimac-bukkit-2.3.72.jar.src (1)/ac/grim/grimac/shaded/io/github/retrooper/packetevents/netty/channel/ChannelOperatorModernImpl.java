/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.netty.channel;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelOperator;
/*     */ import io.netty.channel.Channel;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
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
/*     */ public class ChannelOperatorModernImpl
/*     */   implements ChannelOperator
/*     */ {
/*     */   public SocketAddress remoteAddress(Object channel) {
/*  30 */     return ((Channel)channel).remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress localAddress(Object channel) {
/*  35 */     return ((Channel)channel).localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen(Object channel) {
/*  40 */     return ((Channel)channel).isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object close(Object channel) {
/*  45 */     return ((Channel)channel).close();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object write(Object channel, Object buffer) {
/*  50 */     return ((Channel)channel).write(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object flush(Object channel) {
/*  55 */     return ((Channel)channel).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeAndFlush(Object channel, Object buffer) {
/*  60 */     return ((Channel)channel).writeAndFlush(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object fireChannelRead(Object channel, Object buffer) {
/*  65 */     return ((Channel)channel).pipeline().fireChannelRead(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeInContext(Object channel, String ctx, Object buffer) {
/*  70 */     return ((Channel)channel).pipeline().context(ctx).write(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object flushInContext(Object channel, String ctx) {
/*  75 */     return ((Channel)channel).pipeline().context(ctx).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object writeAndFlushInContext(Object channel, String ctx, Object buffer) {
/*  80 */     return ((Channel)channel).pipeline().context(ctx).writeAndFlush(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getPipeline(Object channel) {
/*  85 */     return ((Channel)channel).pipeline();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object fireChannelReadInContext(Object channel, String ctx, Object buffer) {
/*  90 */     return ((Channel)channel).pipeline().context(ctx).fireChannelRead(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> pipelineHandlerNames(Object channel) {
/*  95 */     return ((Channel)channel).pipeline().names();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getPipelineHandler(Object channel, String name) {
/* 100 */     return ((Channel)channel).pipeline().get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getPipelineContext(Object channel, String name) {
/* 105 */     return ((Channel)channel).pipeline().context(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runInEventLoop(Object channel, Runnable runnable) {
/* 110 */     ((Channel)channel).eventLoop().execute(runnable);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object pooledByteBuf(Object o) {
/* 115 */     return ((Channel)o).alloc().buffer();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\netty\channel\ChannelOperatorModernImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
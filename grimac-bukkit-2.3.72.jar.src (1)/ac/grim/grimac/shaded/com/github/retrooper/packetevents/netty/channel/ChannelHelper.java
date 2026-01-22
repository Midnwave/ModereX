/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Arrays;
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
/*     */ public class ChannelHelper
/*     */ {
/*     */   public static SocketAddress remoteAddress(Object channel) {
/*  29 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().remoteAddress(channel);
/*     */   }
/*     */   
/*     */   public static SocketAddress localAddress(Object channel) {
/*  33 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().localAddress(channel);
/*     */   }
/*     */   
/*     */   public static boolean isOpen(Object channel) {
/*  37 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().isOpen(channel);
/*     */   }
/*     */   
/*     */   public static Object close(Object channel) {
/*  41 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().close(channel);
/*     */   }
/*     */   
/*     */   public static Object write(Object channel, Object buffer) {
/*  45 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().write(channel, buffer);
/*     */   }
/*     */   
/*     */   public static Object flush(Object channel) {
/*  49 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().flush(channel);
/*     */   }
/*     */   
/*     */   public static Object writeAndFlush(Object channel, Object buffer) {
/*  53 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().writeAndFlush(channel, buffer);
/*     */   }
/*     */   
/*     */   public static Object fireChannelRead(Object channel, Object buffer) {
/*  57 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().fireChannelRead(channel, buffer);
/*     */   }
/*     */   
/*     */   public static Object writeInContext(Object channel, String ctx, Object buffer) {
/*  61 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().writeInContext(channel, ctx, buffer);
/*     */   }
/*     */   
/*     */   public static Object flushInContext(Object channel, String ctx) {
/*  65 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().flushInContext(channel, ctx);
/*     */   }
/*     */   
/*     */   public static Object writeAndFlushInContext(Object channel, String ctx, Object buffer) {
/*  69 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().writeAndFlushInContext(channel, ctx, buffer);
/*     */   }
/*     */   
/*     */   public static Object fireChannelReadInContext(Object channel, String ctx, Object buffer) {
/*  73 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().fireChannelReadInContext(channel, ctx, buffer);
/*     */   }
/*     */   
/*     */   public static List<String> pipelineHandlerNames(Object channel) {
/*  77 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().pipelineHandlerNames(channel);
/*     */   }
/*     */   
/*     */   public static String pipelineHandlerNamesAsString(Object channel) {
/*  81 */     return Arrays.toString(pipelineHandlerNames(channel).toArray((Object[])new String[0]));
/*     */   }
/*     */   
/*     */   public static Object getPipeline(Object channel) {
/*  85 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().getPipeline(channel);
/*     */   }
/*     */   
/*     */   public static Object getPipelineHandler(Object channel, String name) {
/*  89 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().getPipelineHandler(channel, name);
/*     */   }
/*     */   
/*     */   public static Object getPipelineContext(Object channel, String handlerName) {
/*  93 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().getPipelineContext(channel, handlerName);
/*     */   }
/*     */   
/*     */   public static Object pooledByteBuf(Object channel) {
/*  97 */     return PacketEvents.getAPI().getNettyManager().getChannelOperator().pooledByteBuf(channel);
/*     */   }
/*     */   
/*     */   public static void runInEventLoop(Object channel, Runnable runnable) {
/* 101 */     PacketEvents.getAPI().getNettyManager().getChannelOperator().runInEventLoop(channel, runnable);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\channel\ChannelHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
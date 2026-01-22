/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.handlers;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.InvalidDisconnectPacketSend;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.ExceptionUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.PacketEventsImplHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection.ServerConnectionInitializer;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia.FoliaScheduler;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.CustomPipelineUtil;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelOutboundHandlerAdapter;
/*     */ import io.netty.channel.ChannelPromise;
/*     */ import io.netty.util.ReferenceCountUtil;
/*     */ import io.netty.util.concurrent.Future;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
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
/*     */ public class PacketEventsEncoder
/*     */   extends ChannelOutboundHandlerAdapter
/*     */ {
/*     */   private static final boolean NETTY_4_1_0;
/*     */   public User user;
/*     */   public Player player;
/*     */   
/*     */   static {
/*  57 */     boolean netty410 = false;
/*     */     try {
/*  59 */       ChannelPromise.class.getDeclaredMethod("unvoid", new Class[0]);
/*  60 */       netty410 = true;
/*  61 */     } catch (NoSuchMethodException noSuchMethodException) {}
/*     */     
/*  63 */     NETTY_4_1_0 = netty410;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  68 */   private boolean handledCompression = (COMPRESSION_ENABLED_EVENT != null);
/*     */   private ChannelPromise promise;
/*  70 */   public static final Object COMPRESSION_ENABLED_EVENT = paperCompressionEnabledEvent();
/*     */   private boolean preVia;
/*     */   
/*     */   public PacketEventsEncoder(User user, boolean preVia) {
/*  74 */     this.user = user;
/*  75 */     this.preVia = preVia;
/*     */   }
/*     */   
/*     */   public PacketEventsEncoder(ChannelHandler encoder) {
/*  79 */     this.user = ((PacketEventsEncoder)encoder).user;
/*  80 */     this.player = ((PacketEventsEncoder)encoder).player;
/*  81 */     this.handledCompression = ((PacketEventsEncoder)encoder).handledCompression;
/*  82 */     this.promise = ((PacketEventsEncoder)encoder).promise;
/*  83 */     this.preVia = ((PacketEventsEncoder)encoder).preVia;
/*     */   }
/*     */   
/*     */   private PacketSendEvent handleClientBoundPacket(Channel channel, User user, Object player, ByteBuf buffer, ChannelPromise promise, boolean preVia) throws Exception {
/*  87 */     PacketSendEvent packetSendEvent = PacketEventsImplHelper.handleClientBoundPacket(channel, user, player, buffer, !preVia);
/*  88 */     if (packetSendEvent != null && packetSendEvent.hasTasksAfterSend()) {
/*  89 */       promise.addListener(p -> {
/*     */             for (Runnable task : packetSendEvent.getTasksAfterSend()) {
/*     */               task.run();
/*     */             }
/*     */           });
/*     */     }
/*  95 */     return packetSendEvent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 102 */     ChannelPromise oldPromise = (this.promise != null && !this.promise.isSuccess()) ? this.promise : null;
/* 103 */     if (NETTY_4_1_0)
/*     */     {
/*     */       
/* 106 */       promise = promise.unvoid();
/*     */     }
/* 108 */     promise.addListener(p -> this.promise = oldPromise);
/* 109 */     this.promise = promise;
/*     */     
/* 111 */     if (msg instanceof ByteBuf) {
/* 112 */       boolean needsRecompression = (!this.handledCompression && handleCompression(ctx, (ByteBuf)msg));
/* 113 */       handleClientBoundPacket(ctx.channel(), this.user, this.player, (ByteBuf)msg, this.promise, this.preVia);
/*     */ 
/*     */       
/* 116 */       if (!this.preVia && PacketEvents.getAPI().getSettings().isPreViaInjection() && !ViaVersionUtil.isAvailable()) {
/* 117 */         handleClientBoundPacket(ctx.channel(), this.user, this.player, (ByteBuf)msg, this.promise, !this.preVia);
/*     */       }
/*     */       
/* 120 */       if (!((ByteBuf)msg).isReadable()) {
/* 121 */         ReferenceCountUtil.release(msg);
/* 122 */         promise.trySuccess();
/*     */         
/*     */         return;
/*     */       } 
/* 126 */       if (needsRecompression) {
/* 127 */         compress(ctx, (ByteBuf)msg);
/*     */       }
/*     */     } 
/*     */     
/* 131 */     ctx.write(msg, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 137 */     if (ExceptionUtil.isException(cause, InvalidDisconnectPacketSend.class)) {
/*     */       return;
/*     */     }
/*     */     
/* 141 */     boolean didWeCauseThis = ExceptionUtil.isException(cause, PacketProcessException.class);
/* 142 */     if (didWeCauseThis && (this.user == null || this.user
/* 143 */       .getEncoderState() != ConnectionState.HANDSHAKING)) {
/* 144 */       if (!SpigotReflectionUtil.isMinecraftServerInstanceDebugging()) {
/* 145 */         if (PacketEvents.getAPI().getSettings().isFullStackTraceEnabled()) {
/* 146 */           cause.printStackTrace();
/*     */         } else {
/* 148 */           PacketEvents.getAPI().getLogManager().warn(cause.getMessage());
/*     */         } 
/*     */       }
/*     */       
/* 152 */       if (PacketEvents.getAPI().getSettings().isKickOnPacketExceptionEnabled()) {
/*     */         try {
/* 154 */           if (this.user != null) {
/* 155 */             this.user.sendPacket((PacketWrapper)new WrapperPlayServerDisconnect((Component)Component.text("Invalid packet")));
/*     */           }
/* 157 */         } catch (Exception exception) {}
/*     */ 
/*     */         
/* 160 */         ctx.channel().close();
/* 161 */         if (this.player != null) {
/* 162 */           FoliaScheduler.getEntityScheduler().runDelayed((Entity)this.player, (Plugin)PacketEvents.getAPI().getPlugin(), o -> this.player.kickPlayer("Invalid packet"), null, 1L);
/*     */         }
/*     */         
/* 165 */         if (this.user != null && this.user.getProfile().getName() != null) {
/* 166 */           PacketEvents.getAPI().getLogManager().warn("Disconnected " + this.user.getProfile().getName() + " due to an invalid packet!");
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 171 */     super.exceptionCaught(ctx, cause);
/*     */   }
/*     */   
/*     */   private static Object paperCompressionEnabledEvent() {
/*     */     try {
/* 176 */       Class<?> eventClass = Class.forName("io.papermc.paper.network.ConnectionEvent");
/* 177 */       return eventClass.getDeclaredField("COMPRESSION_THRESHOLD_SET").get(null);
/* 178 */     } catch (ReflectiveOperationException e) {
/* 179 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void compress(ChannelHandlerContext ctx, ByteBuf input) throws InvocationTargetException {
/* 184 */     ChannelHandler compressor = ctx.pipeline().get("compress");
/* 185 */     ByteBuf temp = ctx.alloc().buffer();
/*     */     try {
/* 187 */       if (compressor != null) {
/* 188 */         CustomPipelineUtil.callEncode(compressor, ctx, input, temp);
/*     */       }
/*     */     } finally {
/* 191 */       input.clear().writeBytes(temp);
/* 192 */       temp.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void decompress(ChannelHandlerContext ctx, ByteBuf input, ByteBuf output) throws InvocationTargetException {
/* 197 */     ChannelHandler decompressor = ctx.pipeline().get("decompress");
/* 198 */     if (decompressor != null) {
/* 199 */       ByteBuf temp = CustomPipelineUtil.callDecode(decompressor, ctx, input).get(0);
/*     */       try {
/* 201 */         output.clear().writeBytes(temp);
/*     */       } finally {
/* 203 */         temp.release();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean handleCompression(ChannelHandlerContext ctx, ByteBuf buffer) throws InvocationTargetException {
/* 209 */     if (this.handledCompression) return false; 
/* 210 */     int compressIndex = ctx.pipeline().names().indexOf("compress");
/* 211 */     if (compressIndex == -1) return false; 
/* 212 */     this.handledCompression = true;
/* 213 */     int peEncoderIndex = ctx.pipeline().names().indexOf((this.preVia ? "pre-" : "") + PacketEvents.ENCODER_NAME);
/* 214 */     if (peEncoderIndex == -1) return false;
/*     */     
/* 216 */     if (compressIndex <= peEncoderIndex) return false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 221 */     boolean decompress = false;
/*     */ 
/*     */     
/* 224 */     if (!this.preVia || 
/* 225 */       !this.user.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
/* 226 */       decompress(ctx, buffer, buffer);
/* 227 */       decompress = true;
/*     */     } 
/*     */ 
/*     */     
/* 231 */     ServerConnectionInitializer.relocateHandlers(ctx.channel(), this.user, this.preVia, false);
/* 232 */     return decompress;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\injector\handlers\PacketEventsEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
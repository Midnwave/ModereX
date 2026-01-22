/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.protocol;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ProtocolVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
/*     */ import io.netty.buffer.ByteBuf;
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
/*     */ public class ProtocolManagerImpl
/*     */   implements ProtocolManager
/*     */ {
/*     */   private ProtocolVersion platformVersion;
/*     */   
/*     */   private ProtocolVersion resolveVersionNoCache() {
/*  37 */     return ProtocolVersion.UNKNOWN;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getPlatformVersion() {
/*  42 */     if (this.platformVersion == null) {
/*  43 */       this.platformVersion = resolveVersionNoCache();
/*     */     }
/*  45 */     return this.platformVersion;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPacket(Object channel, Object byteBuf) {
/*  50 */     if (ChannelHelper.isOpen(channel)) {
/*     */ 
/*     */       
/*  53 */       if (ProtocolSupportUtil.isAvailable() && byteBuf instanceof ByteBuf) {
/*  54 */         ((ByteBuf)byteBuf).retain();
/*     */       }
/*  56 */       ChannelHelper.writeAndFlush(channel, byteBuf);
/*     */     } else {
/*  58 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPacketSilently(Object channel, Object byteBuf) {
/*  64 */     if (ChannelHelper.isOpen(channel)) {
/*     */ 
/*     */       
/*  67 */       ChannelHelper.writeAndFlushInContext(channel, PacketEvents.ENCODER_NAME, byteBuf);
/*     */     } else {
/*  69 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writePacket(Object channel, Object byteBuf) {
/*  75 */     if (ChannelHelper.isOpen(channel)) {
/*     */ 
/*     */ 
/*     */       
/*  79 */       if (ProtocolSupportUtil.isAvailable() && byteBuf instanceof ByteBuf) {
/*  80 */         ((ByteBuf)byteBuf).retain();
/*     */       }
/*  82 */       ChannelHelper.write(channel, byteBuf);
/*     */     } else {
/*  84 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writePacketSilently(Object channel, Object byteBuf) {
/*  90 */     if (ChannelHelper.isOpen(channel)) {
/*     */ 
/*     */       
/*  93 */       ChannelHelper.writeInContext(channel, PacketEvents.ENCODER_NAME, byteBuf);
/*     */     } else {
/*  95 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePacket(Object channel, Object byteBuf) {
/* 101 */     if (ChannelHelper.isOpen(channel)) {
/* 102 */       List<String> handlerNames = ChannelHelper.pipelineHandlerNames(channel);
/*     */       
/* 104 */       if (handlerNames.contains("via-encoder")) {
/* 105 */         ChannelHelper.fireChannelReadInContext(channel, "via-decoder", byteBuf);
/*     */       
/*     */       }
/* 108 */       else if (handlerNames.contains("ps_decoder_transformer")) {
/*     */ 
/*     */         
/* 111 */         ChannelHelper.fireChannelReadInContext(channel, "ps_decoder_transformer", byteBuf);
/* 112 */       } else if (handlerNames.contains("decompress")) {
/*     */         
/* 114 */         ChannelHelper.fireChannelReadInContext(channel, "decompress", byteBuf);
/*     */       }
/* 116 */       else if (handlerNames.contains("decrypt")) {
/*     */ 
/*     */         
/* 119 */         ChannelHelper.fireChannelReadInContext(channel, "decrypt", byteBuf);
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 124 */         ChannelHelper.fireChannelReadInContext(channel, "splitter", byteBuf);
/*     */       } 
/*     */     } else {
/*     */       
/* 128 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePacketSilently(Object channel, Object byteBuf) {
/* 134 */     if (ChannelHelper.isOpen(channel)) {
/* 135 */       ChannelHelper.fireChannelReadInContext(channel, PacketEvents.DECODER_NAME, byteBuf);
/*     */     } else {
/* 137 */       ((ByteBuf)byteBuf).release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientVersion getClientVersion(Object channel) {
/* 143 */     User user = getUser(channel);
/* 144 */     if (user.getClientVersion() == null) {
/* 145 */       return ClientVersion.UNKNOWN;
/*     */     }
/* 147 */     return user.getClientVersion();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\manager\protocol\ProtocolManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
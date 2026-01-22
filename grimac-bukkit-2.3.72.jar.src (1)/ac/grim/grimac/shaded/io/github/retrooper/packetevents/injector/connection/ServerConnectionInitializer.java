/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserConnectEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.UserProfile;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.FakeChannelUtil;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.PacketEventsImplHelper;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.handlers.PacketEventsDecoder;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.handlers.PacketEventsEncoder;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.util.concurrent.GenericFutureListener;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class ServerConnectionInitializer
/*     */ {
/*     */   public static void initChannel(Object ch, ConnectionState connectionState) {
/*  42 */     Channel channel = (Channel)ch;
/*  43 */     if (FakeChannelUtil.isFakeChannel(channel)) {
/*     */       return;
/*     */     }
/*  46 */     User user = new User(channel, connectionState, null, new UserProfile(null, null));
/*     */     
/*  48 */     if (connectionState == ConnectionState.PLAY) {
/*     */       
/*  50 */       user.setClientVersion(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*  51 */       PacketEvents.getAPI().getLogManager().warn("Late injection detected, we missed packets so some functionality may break!");
/*     */     } 
/*     */     
/*  54 */     synchronized (channel) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  62 */       if (channel.pipeline().get("splitter") == null) {
/*  63 */         channel.close();
/*     */         
/*     */         return;
/*     */       } 
/*  67 */       UserConnectEvent connectEvent = new UserConnectEvent(user);
/*  68 */       PacketEvents.getAPI().getEventManager().callEvent((PacketEvent)connectEvent);
/*  69 */       if (connectEvent.isCancelled()) {
/*  70 */         channel.unsafe().closeForcibly();
/*     */         
/*     */         return;
/*     */       } 
/*  74 */       relocateHandlers(channel, user, false, false);
/*  75 */       if (PacketEvents.getAPI().getSettings().isPreViaInjection() && ViaVersionUtil.isAvailable()) relocateHandlers(channel, user, true, false);
/*     */       
/*  77 */       channel.closeFuture().addListener((GenericFutureListener)(future -> PacketEventsImplHelper.handleDisconnection(user.getChannel(), user.getUUID())));
/*  78 */       PacketEvents.getAPI().getProtocolManager().setUser(channel, user);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void destroyHandlers(Object ch) {
/*  83 */     Channel channel = (Channel)ch;
/*  84 */     if (channel.pipeline().get(PacketEvents.DECODER_NAME) != null) {
/*  85 */       channel.pipeline().remove(PacketEvents.DECODER_NAME);
/*     */     } else {
/*  87 */       PacketEvents.getAPI().getLogger().warning("Could not find decoder handler in channel pipeline!");
/*     */     } 
/*     */     
/*  90 */     if (channel.pipeline().get(PacketEvents.ENCODER_NAME) != null) {
/*  91 */       channel.pipeline().remove(PacketEvents.ENCODER_NAME);
/*     */     } else {
/*  93 */       PacketEvents.getAPI().getLogger().warning("Could not find encoder handler in channel pipeline!");
/*     */     } 
/*     */   } public static void relocateHandlers(Channel ctx, User user, boolean preVia, boolean force) {
/*     */     try {
/*     */       PacketEventsEncoder packetEventsEncoder;
/*     */       PacketEventsDecoder decoder;
/*  99 */       if (PacketEvents.getAPI().getSettings().isDebugEnabled()) {
/* 100 */         PacketEvents.getAPI().getLogManager().debug("Pre relocate, preVia: " + preVia + ", " + ChannelHelper.pipelineHandlerNamesAsString(ctx));
/*     */       }
/* 102 */       String encoderName = preVia ? ("pre-" + PacketEvents.ENCODER_NAME) : PacketEvents.ENCODER_NAME;
/* 103 */       String decoderName = preVia ? ("pre-" + PacketEvents.DECODER_NAME) : PacketEvents.DECODER_NAME;
/*     */       
/* 105 */       PacketEventsDecoder existingDecoder = (PacketEventsDecoder)ctx.pipeline().get(decoderName);
/*     */ 
/*     */ 
/*     */       
/* 109 */       if (existingDecoder != null) {
/* 110 */         if (existingDecoder.hasBeenRelocated && !force)
/* 111 */           return;  existingDecoder.hasBeenRelocated = true;
/*     */         
/* 113 */         decoder = new PacketEventsDecoder((PacketEventsDecoder)ctx.pipeline().remove(decoderName));
/* 114 */         packetEventsEncoder = new PacketEventsEncoder(ctx.pipeline().remove(encoderName));
/*     */       } else {
/* 116 */         packetEventsEncoder = new PacketEventsEncoder(user, preVia);
/* 117 */         decoder = new PacketEventsDecoder(user, preVia);
/*     */       } 
/*     */       
/* 120 */       if (PacketEvents.getAPI().getSettings().isDebugEnabled()) {
/* 121 */         PacketEvents.getAPI().getLogManager().debug("After remove, preVia: " + preVia + ", " + ChannelHelper.pipelineHandlerNamesAsString(ctx));
/*     */       }
/* 123 */       if (preVia) {
/* 124 */         ctx.pipeline()
/* 125 */           .addBefore("via-encoder", encoderName, (ChannelHandler)packetEventsEncoder)
/* 126 */           .addBefore("via-decoder", decoderName, (ChannelHandler)decoder);
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 133 */         String decoderTarget = ctx.pipeline().names().contains("inbound_config") ? "inbound_config" : "decoder";
/*     */         
/* 135 */         String encoderTarget = ctx.pipeline().names().contains("outbound_config") ? "outbound_config" : "encoder";
/*     */         
/* 137 */         ctx.pipeline()
/* 138 */           .addBefore(decoderTarget, decoderName, (ChannelHandler)decoder)
/* 139 */           .addBefore(encoderTarget, encoderName, (ChannelHandler)packetEventsEncoder);
/*     */       } 
/*     */       
/* 142 */       if (PacketEvents.getAPI().getSettings().isDebugEnabled())
/* 143 */         PacketEvents.getAPI().getLogManager().debug("After add, preVia: " + preVia + ", " + ChannelHelper.pipelineHandlerNamesAsString(ctx)); 
/* 144 */     } catch (NoSuchElementException ex) {
/* 145 */       String handlers = ChannelHelper.pipelineHandlerNamesAsString(ctx);
/* 146 */       throw new IllegalStateException("PacketEvents failed to add a decoder to the netty pipeline. Pipeline handlers: " + handlers, ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\injector\connection\ServerConnectionInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.ProtocolPacketEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.UserDisconnectEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.UUID;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PacketEventsImplHelper
/*     */ {
/*     */   @Nullable
/*     */   public static ProtocolPacketEvent handlePacket(Object channel, User user, Object player, Object buffer, boolean autoProtocolTranslation, PacketSide side) throws Exception {
/*  43 */     if (side == PacketSide.SERVER) {
/*  44 */       return (ProtocolPacketEvent)handleClientBoundPacket(channel, user, player, buffer, autoProtocolTranslation);
/*     */     }
/*  46 */     return (ProtocolPacketEvent)handleServerBoundPacket(channel, user, player, buffer, autoProtocolTranslation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static PacketSendEvent handleClientBoundPacket(Object channel, User user, Object player, Object buffer, boolean autoProtocolTranslation) throws Exception {
/*  54 */     if (!ByteBufHelper.isReadable(buffer)) {
/*  55 */       return null;
/*     */     }
/*     */     
/*  58 */     int preProcessIndex = ByteBufHelper.readerIndex(buffer);
/*  59 */     PacketSendEvent packetSendEvent = EventCreationUtil.createSendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*  60 */     int processIndex = ByteBufHelper.readerIndex(buffer);
/*  61 */     PacketEvents.getAPI().getEventManager().callEvent((PacketEvent)packetSendEvent, () -> ByteBufHelper.readerIndex(buffer, processIndex), !autoProtocolTranslation);
/*     */ 
/*     */     
/*  64 */     if (!packetSendEvent.isCancelled()) {
/*     */       
/*  66 */       if (packetSendEvent.getLastUsedWrapper() != null) {
/*     */         
/*  68 */         ByteBufHelper.clear(buffer);
/*  69 */         packetSendEvent.getLastUsedWrapper().writeVarInt(packetSendEvent.getPacketId());
/*  70 */         packetSendEvent.getLastUsedWrapper().write();
/*     */       }
/*     */       else {
/*     */         
/*  74 */         ByteBufHelper.readerIndex(buffer, preProcessIndex);
/*     */       } 
/*     */     } else {
/*     */       
/*  78 */       ByteBufHelper.clear(buffer);
/*     */     } 
/*     */     
/*  81 */     if (packetSendEvent.hasPostTasks()) {
/*  82 */       for (Runnable task : packetSendEvent.getPostTasks()) {
/*  83 */         task.run();
/*     */       }
/*     */     }
/*     */     
/*  87 */     return packetSendEvent;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static PacketReceiveEvent handleServerBoundPacket(Object channel, User user, Object player, Object buffer, boolean autoProtocolTranslation) throws Exception {
/*  94 */     if (!ByteBufHelper.isReadable(buffer)) {
/*  95 */       return null;
/*     */     }
/*     */     
/*  98 */     int preProcessIndex = ByteBufHelper.readerIndex(buffer);
/*  99 */     PacketReceiveEvent packetReceiveEvent = EventCreationUtil.createReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/* 100 */     int processIndex = ByteBufHelper.readerIndex(buffer);
/* 101 */     PacketEvents.getAPI().getEventManager().callEvent((PacketEvent)packetReceiveEvent, () -> ByteBufHelper.readerIndex(buffer, processIndex), !autoProtocolTranslation);
/*     */ 
/*     */     
/* 104 */     if (!packetReceiveEvent.isCancelled()) {
/*     */       
/* 106 */       if (packetReceiveEvent.getLastUsedWrapper() != null) {
/*     */         
/* 108 */         ByteBufHelper.clear(buffer);
/* 109 */         packetReceiveEvent.getLastUsedWrapper().writeVarInt(packetReceiveEvent.getPacketId());
/* 110 */         packetReceiveEvent.getLastUsedWrapper().write();
/*     */       }
/*     */       else {
/*     */         
/* 114 */         ByteBufHelper.readerIndex(buffer, preProcessIndex);
/*     */       } 
/*     */     } else {
/*     */       
/* 118 */       ByteBufHelper.clear(buffer);
/*     */     } 
/* 120 */     if (packetReceiveEvent.hasPostTasks()) {
/* 121 */       for (Runnable task : packetReceiveEvent.getPostTasks()) {
/* 122 */         task.run();
/*     */       }
/*     */     }
/* 125 */     return packetReceiveEvent;
/*     */   }
/*     */   
/*     */   public static void handleDisconnection(Object channel, @Nullable UUID uuid) {
/* 129 */     synchronized (channel) {
/* 130 */       ProtocolManager protocolManager = PacketEvents.getAPI().getProtocolManager();
/* 131 */       User user = protocolManager.getUser(channel);
/*     */       
/* 133 */       if (user != null) {
/* 134 */         UserDisconnectEvent disconnectEvent = new UserDisconnectEvent(user);
/* 135 */         PacketEvents.getAPI().getEventManager().callEvent((PacketEvent)disconnectEvent);
/* 136 */         protocolManager.removeUser(user.getChannel());
/*     */       } 
/*     */       
/* 139 */       if (uuid == null) {
/* 140 */         protocolManager.removeChannel(channel);
/*     */       } else {
/* 142 */         protocolManager.removeChannelById(uuid);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\PacketEventsImplHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
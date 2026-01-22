/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketConfigReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketConfigSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketHandshakeReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketHandshakeSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketLoginReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketLoginSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketStatusReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple.PacketStatusSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EventCreationUtil
/*    */ {
/*    */   public static PacketReceiveEvent createReceiveEvent(Object channel, User user, Object player, Object buffer, boolean autoProtocolTranslation) throws PacketProcessException {
/* 30 */     switch (user.getDecoderState()) {
/*    */       case HANDSHAKING:
/* 32 */         return (PacketReceiveEvent)new PacketHandshakeReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case STATUS:
/* 34 */         return (PacketReceiveEvent)new PacketStatusReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case LOGIN:
/* 36 */         return (PacketReceiveEvent)new PacketLoginReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case PLAY:
/* 38 */         return (PacketReceiveEvent)new PacketPlayReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case CONFIGURATION:
/* 40 */         return (PacketReceiveEvent)new PacketConfigReceiveEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */     } 
/* 42 */     throw new RuntimeException("Unknown connection state " + user.getDecoderState() + "!");
/*    */   }
/*    */ 
/*    */   
/*    */   public static PacketSendEvent createSendEvent(Object channel, User user, Object player, Object buffer, boolean autoProtocolTranslation) throws PacketProcessException {
/* 47 */     switch (user.getEncoderState()) {
/*    */       case HANDSHAKING:
/* 49 */         return (PacketSendEvent)new PacketHandshakeSendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case STATUS:
/* 51 */         return (PacketSendEvent)new PacketStatusSendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case LOGIN:
/* 53 */         return (PacketSendEvent)new PacketLoginSendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case PLAY:
/* 55 */         return (PacketSendEvent)new PacketPlaySendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */       case CONFIGURATION:
/* 57 */         return (PacketSendEvent)new PacketConfigSendEvent(channel, user, player, buffer, autoProtocolTranslation);
/*    */     } 
/* 59 */     throw new RuntimeException("Unknown connection state " + user.getEncoderState() + "!");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\EventCreationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
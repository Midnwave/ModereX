/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.ProtocolPacketEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ 
/*    */ public class PacketHandshakeSendEvent
/*    */   extends PacketSendEvent {
/*    */   public PacketHandshakeSendEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 15 */     super(channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketHandshakeSendEvent(int packetId, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 22 */     super(packetId, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketHandshakeSendEvent clone() {
/*    */     try {
/* 28 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 29 */       return new PacketHandshakeSendEvent(getPacketId(), (PacketTypeCommon)getPacketType(), getServerVersion(), 
/* 30 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 31 */     } catch (PacketProcessException e) {
/* 32 */       e.printStackTrace();
/*    */       
/* 34 */       return null;
/*    */     } 
/*    */   }
/*    */   public PacketType.Handshaking.Client getPacketType() {
/* 38 */     return (PacketType.Handshaking.Client)super.getPacketType();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\simple\PacketHandshakeSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
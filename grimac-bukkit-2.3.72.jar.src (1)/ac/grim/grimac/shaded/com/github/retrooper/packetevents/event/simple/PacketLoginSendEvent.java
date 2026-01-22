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
/*    */ public class PacketLoginSendEvent
/*    */   extends PacketSendEvent
/*    */ {
/*    */   public PacketLoginSendEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 32 */     super(channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketLoginSendEvent(int packetId, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 39 */     super(packetId, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketLoginSendEvent clone() {
/*    */     try {
/* 45 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 46 */       return new PacketLoginSendEvent(getPacketId(), (PacketTypeCommon)getPacketType(), getServerVersion(), 
/* 47 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 48 */     } catch (PacketProcessException e) {
/* 49 */       e.printStackTrace();
/*    */       
/* 51 */       return null;
/*    */     } 
/*    */   }
/*    */   public PacketType.Login.Server getPacketType() {
/* 55 */     return (PacketType.Login.Server)super.getPacketType();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\simple\PacketLoginSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
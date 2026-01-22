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
/*    */ public class PacketStatusSendEvent
/*    */   extends PacketSendEvent
/*    */ {
/*    */   public PacketStatusSendEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 32 */     super(channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketStatusSendEvent(int packetId, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 39 */     super(packetId, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketStatusSendEvent clone() {
/*    */     try {
/* 45 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 46 */       return new PacketStatusSendEvent(getPacketId(), (PacketTypeCommon)getPacketType(), getServerVersion(), 
/* 47 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 48 */     } catch (PacketProcessException e) {
/* 49 */       e.printStackTrace();
/*    */       
/* 51 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public PacketType.Status.Server getPacketType() {
/* 56 */     return (PacketType.Status.Server)super.getPacketType();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\simple\PacketStatusSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
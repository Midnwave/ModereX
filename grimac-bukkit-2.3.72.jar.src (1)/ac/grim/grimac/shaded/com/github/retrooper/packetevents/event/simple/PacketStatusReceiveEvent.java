/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.simple;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
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
/*    */ 
/*    */ public class PacketStatusReceiveEvent
/*    */   extends PacketReceiveEvent
/*    */ {
/*    */   public PacketStatusReceiveEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 33 */     super(channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketStatusReceiveEvent(int packetId, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 40 */     super(packetId, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketStatusReceiveEvent clone() {
/*    */     try {
/* 46 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 47 */       return new PacketStatusReceiveEvent(getPacketId(), (PacketTypeCommon)getPacketType(), getServerVersion(), 
/* 48 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 49 */     } catch (PacketProcessException e) {
/* 50 */       e.printStackTrace();
/*    */       
/* 52 */       return null;
/*    */     } 
/*    */   }
/*    */   public PacketType.Status.Client getPacketType() {
/* 56 */     return (PacketType.Status.Client)super.getPacketType();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\simple\PacketStatusReceiveEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
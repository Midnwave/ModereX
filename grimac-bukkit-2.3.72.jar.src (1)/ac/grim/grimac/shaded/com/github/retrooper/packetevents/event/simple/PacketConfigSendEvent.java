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
/*    */ 
/*    */ 
/*    */ public class PacketConfigSendEvent
/*    */   extends PacketSendEvent
/*    */ {
/*    */   public PacketConfigSendEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 34 */     super(channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketConfigSendEvent(int packetId, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 41 */     super(packetId, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketConfigSendEvent clone() {
/*    */     try {
/* 47 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 48 */       return new PacketConfigSendEvent(getPacketId(), (PacketTypeCommon)getPacketType(), getServerVersion(), 
/* 49 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 50 */     } catch (PacketProcessException e) {
/* 51 */       e.printStackTrace();
/*    */       
/* 53 */       return null;
/*    */     } 
/*    */   }
/*    */   public PacketType.Configuration.Server getPacketType() {
/* 57 */     return (PacketType.Configuration.Server)super.getPacketType();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\simple\PacketConfigSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
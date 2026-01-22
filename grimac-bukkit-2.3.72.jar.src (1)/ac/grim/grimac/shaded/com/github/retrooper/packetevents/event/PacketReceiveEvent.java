/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
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
/*    */ public class PacketReceiveEvent
/*    */   extends ProtocolPacketEvent
/*    */ {
/*    */   protected PacketReceiveEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 31 */     super(PacketSide.CLIENT, channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketReceiveEvent(int packetID, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 38 */     super(packetID, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void call(PacketListenerCommon listener) {
/* 44 */     listener.onPacketReceive(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketReceiveEvent clone() {
/*    */     try {
/* 50 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 51 */       return new PacketReceiveEvent(getPacketId(), getPacketType(), getServerVersion(), 
/* 52 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 53 */     } catch (PacketProcessException e) {
/* 54 */       e.printStackTrace();
/*    */       
/* 56 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\PacketReceiveEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
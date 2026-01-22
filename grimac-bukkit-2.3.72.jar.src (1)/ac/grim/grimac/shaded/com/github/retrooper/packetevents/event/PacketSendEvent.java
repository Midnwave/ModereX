/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.PacketProcessException;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class PacketSendEvent
/*    */   extends ProtocolPacketEvent
/*    */ {
/* 33 */   private List<Runnable> tasksAfterSend = null;
/*    */ 
/*    */   
/*    */   protected PacketSendEvent(Object channel, User user, Object player, Object rawByteBuf, boolean autoProtocolTranslation) throws PacketProcessException {
/* 37 */     super(PacketSide.SERVER, channel, user, player, rawByteBuf, autoProtocolTranslation);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PacketSendEvent(int packetID, PacketTypeCommon packetType, ServerVersion serverVersion, Object channel, User user, Object player, Object byteBuf) throws PacketProcessException {
/* 44 */     super(packetID, packetType, serverVersion, channel, user, player, byteBuf);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void call(PacketListenerCommon listener) {
/* 51 */     listener.onPacketSend(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Runnable> getTasksAfterSend() {
/* 56 */     if (this.tasksAfterSend == null) {
/* 57 */       this.tasksAfterSend = new ArrayList<>();
/*    */     }
/* 59 */     return this.tasksAfterSend;
/*    */   }
/*    */   
/*    */   public boolean hasTasksAfterSend() {
/* 63 */     return (this.tasksAfterSend != null && !this.tasksAfterSend.isEmpty());
/*    */   }
/*    */ 
/*    */   
/*    */   public PacketSendEvent clone() {
/*    */     try {
/* 69 */       Object clonedBuffer = ByteBufHelper.retainedDuplicate(getByteBuf());
/* 70 */       return new PacketSendEvent(getPacketId(), getPacketType(), getServerVersion(), 
/* 71 */           getChannel(), getUser(), getPlayer(), clonedBuffer);
/* 72 */     } catch (PacketProcessException e) {
/* 73 */       e.printStackTrace();
/*    */       
/* 75 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\PacketSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
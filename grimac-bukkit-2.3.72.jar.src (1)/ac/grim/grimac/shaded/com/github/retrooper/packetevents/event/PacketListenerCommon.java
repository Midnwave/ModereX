/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class PacketListenerCommon
/*    */ {
/*    */   private final PacketListenerPriority priority;
/*    */   
/*    */   public PacketListenerCommon(PacketListenerPriority priority) {
/* 35 */     this.priority = priority;
/*    */   }
/*    */   
/*    */   public PacketListenerCommon() {
/* 39 */     this.priority = PacketListenerPriority.NORMAL;
/*    */   }
/*    */   
/*    */   public PacketListenerPriority getPriority() {
/* 43 */     return this.priority;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUserConnect(UserConnectEvent event) {}
/*    */ 
/*    */   
/*    */   public void onUserLogin(UserLoginEvent event) {}
/*    */ 
/*    */   
/*    */   public void onUserDisconnect(UserDisconnectEvent event) {}
/*    */ 
/*    */   
/*    */   void onPacketReceive(PacketReceiveEvent event) {}
/*    */ 
/*    */   
/*    */   void onPacketSend(PacketSendEvent event) {}
/*    */ 
/*    */   
/*    */   public void onPacketEventExternal(PacketEvent event) {}
/*    */   
/*    */   public boolean isPreVia() {
/* 65 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\PacketListenerCommon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
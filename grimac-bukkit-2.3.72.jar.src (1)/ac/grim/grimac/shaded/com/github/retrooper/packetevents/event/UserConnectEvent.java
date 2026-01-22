/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*    */ 
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
/*    */ public class UserConnectEvent
/*    */   extends PacketEvent
/*    */   implements CancellableEvent, UserEvent
/*    */ {
/*    */   private final User user;
/*    */   private boolean cancelled;
/*    */   
/*    */   public UserConnectEvent(User user) {
/* 28 */     this.user = user;
/*    */   }
/*    */ 
/*    */   
/*    */   public User getUser() {
/* 33 */     return this.user;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 38 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCancelled(boolean cancelled) {
/* 43 */     this.cancelled = cancelled;
/*    */   }
/*    */ 
/*    */   
/*    */   public void call(PacketListenerCommon listener) {
/* 48 */     listener.onUserConnect(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\UserConnectEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
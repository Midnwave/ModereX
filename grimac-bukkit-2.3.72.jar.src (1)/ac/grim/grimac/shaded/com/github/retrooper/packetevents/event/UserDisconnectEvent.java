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
/*    */ public class UserDisconnectEvent
/*    */   extends PacketEvent
/*    */   implements UserEvent
/*    */ {
/*    */   private final User user;
/*    */   
/*    */   public UserDisconnectEvent(User user) {
/* 27 */     this.user = user;
/*    */   }
/*    */ 
/*    */   
/*    */   public User getUser() {
/* 32 */     return this.user;
/*    */   }
/*    */ 
/*    */   
/*    */   public void call(PacketListenerCommon listener) {
/* 37 */     listener.onUserDisconnect(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\UserDisconnectEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
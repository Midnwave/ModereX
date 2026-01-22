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
/*    */ public class UserLoginEvent
/*    */   extends PacketEvent
/*    */   implements CallableEvent, UserEvent, PlayerEvent
/*    */ {
/*    */   private final User user;
/*    */   private final Object player;
/*    */   
/*    */   public UserLoginEvent(User user, Object player) {
/* 28 */     this.user = user;
/* 29 */     this.player = player;
/*    */   }
/*    */ 
/*    */   
/*    */   public User getUser() {
/* 34 */     return this.user;
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> T getPlayer() {
/* 39 */     return (T)this.player;
/*    */   }
/*    */ 
/*    */   
/*    */   public void call(PacketListenerCommon listener) {
/* 44 */     listener.onUserLogin(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\UserLoginEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ import ac.grim.grimac.api.event.GrimEvent;
/*    */ 
/*    */ public class GrimQuitEvent extends GrimEvent implements GrimUserEvent {
/*    */   private final GrimUser user;
/*    */   
/*    */   public GrimQuitEvent(GrimUser user) {
/* 10 */     super(true);
/* 11 */     this.user = user;
/*    */   }
/*    */ 
/*    */   
/*    */   public GrimUser getUser() {
/* 16 */     return this.user;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\GrimQuitEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
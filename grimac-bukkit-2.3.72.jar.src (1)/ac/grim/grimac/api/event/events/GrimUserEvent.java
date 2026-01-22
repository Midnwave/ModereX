/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ 
/*    */ 
/*    */ public interface GrimUserEvent
/*    */ {
/*    */   GrimUser getUser();
/*    */   
/*    */   default GrimUser getPlayer() {
/* 11 */     return getUser();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\GrimUserEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
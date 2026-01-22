/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.event.GrimEvent;
/*    */ 
/*    */ public class GrimReloadEvent extends GrimEvent {
/*    */   private final boolean success;
/*    */   
/*    */   public GrimReloadEvent(boolean success) {
/*  9 */     super(true);
/* 10 */     this.success = success;
/*    */   }
/*    */   
/*    */   public boolean isSuccess() {
/* 14 */     return this.success;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\GrimReloadEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
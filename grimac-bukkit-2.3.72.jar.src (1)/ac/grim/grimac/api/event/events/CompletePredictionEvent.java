/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.AbstractCheck;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ 
/*    */ public class CompletePredictionEvent extends GrimCheckEvent {
/*    */   private final double offset;
/*    */   
/*    */   public CompletePredictionEvent(GrimUser player, AbstractCheck check, double offset) {
/* 10 */     super(player, check);
/* 11 */     this.offset = offset;
/*    */   }
/*    */   
/*    */   public double getOffset() {
/* 15 */     return this.offset;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\CompletePredictionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
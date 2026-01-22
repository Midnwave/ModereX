/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.AbstractCheck;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FlagEvent
/*    */   extends GrimVerboseCheckEvent
/*    */ {
/*    */   public FlagEvent(GrimUser user, AbstractCheck check, String verbose) {
/* 13 */     super(user, check, verbose);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\FlagEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
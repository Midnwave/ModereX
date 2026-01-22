/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.AbstractCheck;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ 
/*    */ 
/*    */ public abstract class GrimVerboseCheckEvent
/*    */   extends GrimCheckEvent
/*    */ {
/*    */   private final String verbose;
/*    */   
/*    */   public GrimVerboseCheckEvent(GrimUser user, AbstractCheck check, String verbose) {
/* 13 */     super(user, check);
/* 14 */     this.verbose = verbose;
/*    */   }
/*    */   
/*    */   public String getVerbose() {
/* 18 */     return this.verbose;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\GrimVerboseCheckEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
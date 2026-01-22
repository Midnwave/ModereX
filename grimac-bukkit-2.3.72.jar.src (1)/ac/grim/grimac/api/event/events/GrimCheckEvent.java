/*    */ package ac.grim.grimac.api.event.events;
/*    */ import ac.grim.grimac.api.AbstractCheck;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ import ac.grim.grimac.api.event.GrimEvent;
/*    */ 
/*    */ public abstract class GrimCheckEvent extends GrimEvent implements GrimUserEvent, Cancellable {
/*    */   private final GrimUser user;
/*    */   
/*    */   @Generated
/*    */   public AbstractCheck getCheck() {
/* 11 */     return this.check;
/*    */   }
/*    */   protected final AbstractCheck check; private boolean cancelled;
/*    */   
/*    */   public GrimCheckEvent(GrimUser user, AbstractCheck check) {
/* 16 */     super(true);
/* 17 */     this.user = user;
/* 18 */     this.check = check;
/*    */   }
/*    */ 
/*    */   
/*    */   public GrimUser getUser() {
/* 23 */     return this.user;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 28 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCancelled(boolean cancelled) {
/* 33 */     this.cancelled = cancelled;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancellable() {
/* 38 */     return true;
/*    */   }
/*    */   
/*    */   public double getViolations() {
/* 42 */     return this.check.getViolations();
/*    */   }
/*    */   
/*    */   public boolean isSetback() {
/* 46 */     return (this.check.getViolations() > this.check.getSetbackVL());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\GrimCheckEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
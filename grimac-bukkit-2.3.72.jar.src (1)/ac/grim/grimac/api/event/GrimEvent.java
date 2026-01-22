/*    */ package ac.grim.grimac.api.event;public abstract class GrimEvent {
/*    */   private boolean cancelled = false;
/*    */   
/*    */   @Generated
/*    */   public boolean isCancelled() {
/*  6 */     return this.cancelled;
/*    */   }
/*    */   private final boolean async;
/*    */   protected GrimEvent() {
/* 10 */     this(false);
/*    */   }
/*    */   
/*    */   protected GrimEvent(boolean async) {
/* 14 */     this.async = async;
/*    */   }
/*    */   
/*    */   public void setCancelled(boolean cancelled) {
/* 18 */     if (!isCancellable()) {
/* 19 */       throw new IllegalStateException("Event " + getEventName() + " is not cancellable");
/*    */     }
/* 21 */     this.cancelled = cancelled;
/*    */   }
/*    */   
/*    */   public boolean isCancellable() {
/* 25 */     return false;
/*    */   }
/*    */   
/*    */   public String getEventName() {
/* 29 */     return getClass().getSimpleName();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\GrimEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.snakeyaml.events;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
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
/*    */ public final class DocumentEndEvent
/*    */   extends Event
/*    */ {
/*    */   private final boolean explicit;
/*    */   
/*    */   public DocumentEndEvent(Mark startMark, Mark endMark, boolean explicit) {
/* 36 */     super(startMark, endMark);
/* 37 */     this.explicit = explicit;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getExplicit() {
/* 46 */     return this.explicit;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Event.ID getEventId() {
/* 56 */     return Event.ID.DocumentEnd;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\events\DocumentEndEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
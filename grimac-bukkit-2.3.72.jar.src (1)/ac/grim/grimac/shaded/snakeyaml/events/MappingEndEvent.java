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
/*    */ public final class MappingEndEvent
/*    */   extends CollectionEndEvent
/*    */ {
/*    */   public MappingEndEvent(Mark startMark, Mark endMark) {
/* 32 */     super(startMark, endMark);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Event.ID getEventId() {
/* 42 */     return Event.ID.MappingEnd;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\events\MappingEndEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
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
/*    */ public abstract class NodeEvent
/*    */   extends Event
/*    */ {
/*    */   private final String anchor;
/*    */   
/*    */   public NodeEvent(String anchor, Mark startMark, Mark endMark) {
/* 26 */     super(startMark, endMark);
/* 27 */     this.anchor = anchor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAnchor() {
/* 39 */     return this.anchor;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getArguments() {
/* 44 */     return "anchor=" + this.anchor;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\events\NodeEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
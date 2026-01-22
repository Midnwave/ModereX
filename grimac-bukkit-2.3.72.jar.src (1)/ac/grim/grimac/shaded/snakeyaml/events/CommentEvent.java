/*    */ package ac.grim.grimac.shaded.snakeyaml.events;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
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
/*    */ public final class CommentEvent
/*    */   extends Event
/*    */ {
/*    */   private final CommentType type;
/*    */   private final String value;
/*    */   
/*    */   public CommentEvent(CommentType type, String value, Mark startMark, Mark endMark) {
/* 36 */     super(startMark, endMark);
/* 37 */     if (type == null) {
/* 38 */       throw new NullPointerException("Event Type must be provided.");
/*    */     }
/* 40 */     this.type = type;
/* 41 */     if (value == null) {
/* 42 */       throw new NullPointerException("Value must be provided.");
/*    */     }
/* 44 */     this.value = value;
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
/*    */   public String getValue() {
/* 56 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommentType getCommentType() {
/* 65 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getArguments() {
/* 70 */     return super.getArguments() + "type=" + this.type + ", value=" + this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Event.ID getEventId() {
/* 75 */     return Event.ID.Comment;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\events\CommentEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
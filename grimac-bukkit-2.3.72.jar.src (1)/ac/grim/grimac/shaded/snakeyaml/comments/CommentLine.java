/*    */ package ac.grim.grimac.shaded.snakeyaml.comments;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*    */ import ac.grim.grimac.shaded.snakeyaml.events.CommentEvent;
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
/*    */ public class CommentLine
/*    */ {
/*    */   private final Mark startMark;
/*    */   private final Mark endMark;
/*    */   private final String value;
/*    */   private final CommentType commentType;
/*    */   
/*    */   public CommentLine(CommentEvent event) {
/* 35 */     this(event.getStartMark(), event.getEndMark(), event.getValue(), event.getCommentType());
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
/*    */   public CommentLine(Mark startMark, Mark endMark, String value, CommentType commentType) {
/* 47 */     this.startMark = startMark;
/* 48 */     this.endMark = endMark;
/* 49 */     this.value = value;
/* 50 */     this.commentType = commentType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Mark getEndMark() {
/* 59 */     return this.endMark;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Mark getStartMark() {
/* 68 */     return this.startMark;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommentType getCommentType() {
/* 77 */     return this.commentType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 86 */     return this.value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 90 */     return "<" + getClass().getName() + " (type=" + getCommentType() + ", value=" + getValue() + ")>";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\comments\CommentLine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
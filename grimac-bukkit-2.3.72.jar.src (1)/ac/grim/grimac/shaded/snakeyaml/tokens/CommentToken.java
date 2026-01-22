/*    */ package ac.grim.grimac.shaded.snakeyaml.tokens;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*    */ import java.util.Objects;
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
/*    */ public final class CommentToken
/*    */   extends Token
/*    */ {
/*    */   private final CommentType type;
/*    */   private final String value;
/*    */   
/*    */   public CommentToken(CommentType type, String value, Mark startMark, Mark endMark) {
/* 37 */     super(startMark, endMark);
/* 38 */     Objects.requireNonNull(type);
/* 39 */     this.type = type;
/* 40 */     Objects.requireNonNull(value);
/* 41 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommentType getCommentType() {
/* 50 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 59 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 64 */     return Token.ID.Comment;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\CommentToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
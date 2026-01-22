/*    */ package ac.grim.grimac.shaded.snakeyaml.tokens;
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
/*    */ public final class TagToken
/*    */   extends Token
/*    */ {
/*    */   private final TagTuple value;
/*    */   
/*    */   public TagToken(TagTuple value, Mark startMark, Mark endMark) {
/* 23 */     super(startMark, endMark);
/* 24 */     this.value = value;
/*    */   }
/*    */   
/*    */   public TagTuple getValue() {
/* 28 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 33 */     return Token.ID.Tag;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\TagToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
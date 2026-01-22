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
/*    */ public final class AnchorToken
/*    */   extends Token
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   public AnchorToken(String value, Mark startMark, Mark endMark) {
/* 33 */     super(startMark, endMark);
/* 34 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 43 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 53 */     return Token.ID.Anchor;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\AnchorToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
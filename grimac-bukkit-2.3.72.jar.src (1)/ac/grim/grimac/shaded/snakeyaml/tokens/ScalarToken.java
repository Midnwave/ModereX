/*    */ package ac.grim.grimac.shaded.snakeyaml.tokens;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
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
/*    */ public final class ScalarToken
/*    */   extends Token
/*    */ {
/*    */   private final String value;
/*    */   private final boolean plain;
/*    */   private final DumperOptions.ScalarStyle style;
/*    */   
/*    */   public ScalarToken(String value, Mark startMark, Mark endMark, boolean plain) {
/* 26 */     this(value, plain, startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
/*    */   }
/*    */ 
/*    */   
/*    */   public ScalarToken(String value, boolean plain, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
/* 31 */     super(startMark, endMark);
/* 32 */     this.value = value;
/* 33 */     this.plain = plain;
/* 34 */     if (style == null) {
/* 35 */       throw new NullPointerException("Style must be provided.");
/*    */     }
/* 37 */     this.style = style;
/*    */   }
/*    */   
/*    */   public boolean getPlain() {
/* 41 */     return this.plain;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 45 */     return this.value;
/*    */   }
/*    */   
/*    */   public DumperOptions.ScalarStyle getStyle() {
/* 49 */     return this.style;
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 54 */     return Token.ID.Scalar;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\ScalarToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
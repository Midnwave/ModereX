/*    */ package ac.grim.grimac.shaded.snakeyaml.tokens;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*    */ import java.util.List;
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
/*    */ public final class DirectiveToken<T>
/*    */   extends Token
/*    */ {
/*    */   private final String name;
/*    */   private final List<T> value;
/*    */   
/*    */   public DirectiveToken(String name, List<T> value, Mark startMark, Mark endMark) {
/* 39 */     super(startMark, endMark);
/* 40 */     this.name = name;
/* 41 */     if (value != null && value.size() != 2) {
/* 42 */       throw new YAMLException("Two strings must be provided instead of " + value.size());
/*    */     }
/* 44 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 53 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<T> getValue() {
/* 62 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 72 */     return Token.ID.Directive;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\DirectiveToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
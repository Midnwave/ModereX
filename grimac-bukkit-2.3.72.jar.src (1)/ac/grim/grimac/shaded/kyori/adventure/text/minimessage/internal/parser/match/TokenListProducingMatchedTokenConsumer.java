/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
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
/*    */ 
/*    */ public final class TokenListProducingMatchedTokenConsumer
/*    */   extends MatchedTokenConsumer<List<Token>>
/*    */ {
/* 39 */   private List<Token> result = null;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TokenListProducingMatchedTokenConsumer(@NotNull String input) {
/* 48 */     super(input);
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(int start, int end, @NotNull TokenType tokenType) {
/* 53 */     super.accept(start, end, tokenType);
/*    */     
/* 55 */     if (this.result == null) {
/* 56 */       this.result = new ArrayList<>();
/*    */     }
/*    */     
/* 59 */     this.result.add(new Token(start, end, tokenType));
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public List<Token> result() {
/* 64 */     return (this.result == null) ? Collections.<Token>emptyList() : this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\match\TokenListProducingMatchedTokenConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
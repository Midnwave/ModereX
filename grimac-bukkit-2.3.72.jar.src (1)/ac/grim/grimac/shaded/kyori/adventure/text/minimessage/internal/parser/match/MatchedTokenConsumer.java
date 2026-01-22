/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.MustBeInvokedByOverriders;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenType;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class MatchedTokenConsumer<T>
/*    */ {
/*    */   protected final String input;
/* 40 */   private int lastIndex = -1;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MatchedTokenConsumer(@NotNull String input) {
/* 49 */     this.input = input;
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
/*    */   @MustBeInvokedByOverriders
/*    */   public void accept(int start, int end, @NotNull TokenType tokenType) {
/* 62 */     this.lastIndex = end;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract T result();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final int lastEndIndex() {
/* 80 */     return this.lastIndex;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\match\MatchedTokenConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenParser;
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
/*    */ public final class TextNode
/*    */   extends ValueNode
/*    */ {
/*    */   private static boolean isEscape(int escape) {
/* 38 */     return (escape == 60 || escape == 92);
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TextNode(@Nullable ElementNode parent, @NotNull Token token, @NotNull String sourceMessage) {
/* 54 */     super(parent, token, sourceMessage, TokenParser.unescape(sourceMessage, token.startIndex(), token.endIndex(), TextNode::isEscape));
/*    */   }
/*    */ 
/*    */   
/*    */   String valueName() {
/* 59 */     return "TextNode";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\TextNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
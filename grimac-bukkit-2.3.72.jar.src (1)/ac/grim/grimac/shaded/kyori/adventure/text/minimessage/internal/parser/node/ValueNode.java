/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
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
/*    */ public abstract class ValueNode
/*    */   extends ElementNode
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   ValueNode(@Nullable ElementNode parent, @Nullable Token token, @NotNull String sourceMessage, @NotNull String value) {
/* 50 */     super(parent, token, sourceMessage);
/* 51 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   abstract String valueName();
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String value() {
/* 63 */     return this.value;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Token token() {
/* 68 */     return Objects.<Token>requireNonNull(super.token(), "token is not set");
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public StringBuilder buildToString(@NotNull StringBuilder sb, int indent) {
/* 73 */     char[] in = ident(indent);
/* 74 */     sb.append(in).append(valueName()).append("('").append(this.value).append("')\n");
/* 75 */     return sb;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\ValueNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
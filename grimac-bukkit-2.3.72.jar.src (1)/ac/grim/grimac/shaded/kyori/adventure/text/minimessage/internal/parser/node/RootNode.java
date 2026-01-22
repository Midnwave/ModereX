/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
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
/*    */ public final class RootNode
/*    */   extends ElementNode
/*    */   implements Node.Root
/*    */ {
/*    */   private final String beforePreprocessing;
/*    */   
/*    */   public RootNode(@NotNull String sourceMessage, @NotNull String beforePreprocessing) {
/* 45 */     super(null, null, sourceMessage);
/* 46 */     this.beforePreprocessing = beforePreprocessing;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String input() {
/* 51 */     return this.beforePreprocessing;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\RootNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
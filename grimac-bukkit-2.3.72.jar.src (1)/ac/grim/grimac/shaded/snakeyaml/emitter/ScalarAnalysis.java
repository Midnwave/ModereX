/*    */ package ac.grim.grimac.shaded.snakeyaml.emitter;
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
/*    */ public final class ScalarAnalysis
/*    */ {
/*    */   private final String scalar;
/*    */   private final boolean empty;
/*    */   private final boolean multiline;
/*    */   private final boolean allowFlowPlain;
/*    */   private final boolean allowBlockPlain;
/*    */   private final boolean allowSingleQuoted;
/*    */   private final boolean allowBlock;
/*    */   
/*    */   public ScalarAnalysis(String scalar, boolean empty, boolean multiline, boolean allowFlowPlain, boolean allowBlockPlain, boolean allowSingleQuoted, boolean allowBlock) {
/* 32 */     this.scalar = scalar;
/* 33 */     this.empty = empty;
/* 34 */     this.multiline = multiline;
/* 35 */     this.allowFlowPlain = allowFlowPlain;
/* 36 */     this.allowBlockPlain = allowBlockPlain;
/* 37 */     this.allowSingleQuoted = allowSingleQuoted;
/* 38 */     this.allowBlock = allowBlock;
/*    */   }
/*    */   
/*    */   public String getScalar() {
/* 42 */     return this.scalar;
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 46 */     return this.empty;
/*    */   }
/*    */   
/*    */   public boolean isMultiline() {
/* 50 */     return this.multiline;
/*    */   }
/*    */   
/*    */   public boolean isAllowFlowPlain() {
/* 54 */     return this.allowFlowPlain;
/*    */   }
/*    */   
/*    */   public boolean isAllowBlockPlain() {
/* 58 */     return this.allowBlockPlain;
/*    */   }
/*    */   
/*    */   public boolean isAllowSingleQuoted() {
/* 62 */     return this.allowSingleQuoted;
/*    */   }
/*    */   
/*    */   public boolean isAllowBlock() {
/* 66 */     return this.allowBlock;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\emitter\ScalarAnalysis.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
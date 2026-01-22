/*    */ package ac.grim.grimac.shaded.snakeyaml.scanner;
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
/*    */ final class SimpleKey
/*    */ {
/*    */   private final int tokenNumber;
/*    */   private final boolean required;
/*    */   private final int index;
/*    */   private final int line;
/*    */   private final int column;
/*    */   private final Mark mark;
/*    */   
/*    */   public SimpleKey(int tokenNumber, boolean required, int index, int line, int column, Mark mark) {
/* 36 */     this.tokenNumber = tokenNumber;
/* 37 */     this.required = required;
/* 38 */     this.index = index;
/* 39 */     this.line = line;
/* 40 */     this.column = column;
/* 41 */     this.mark = mark;
/*    */   }
/*    */   
/*    */   public int getTokenNumber() {
/* 45 */     return this.tokenNumber;
/*    */   }
/*    */   
/*    */   public int getColumn() {
/* 49 */     return this.column;
/*    */   }
/*    */   
/*    */   public Mark getMark() {
/* 53 */     return this.mark;
/*    */   }
/*    */   
/*    */   public int getIndex() {
/* 57 */     return this.index;
/*    */   }
/*    */   
/*    */   public int getLine() {
/* 61 */     return this.line;
/*    */   }
/*    */   
/*    */   public boolean isRequired() {
/* 65 */     return this.required;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return "SimpleKey - tokenNumber=" + this.tokenNumber + " required=" + this.required + " index=" + this.index + " line=" + this.line + " column=" + this.column;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\scanner\SimpleKey.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
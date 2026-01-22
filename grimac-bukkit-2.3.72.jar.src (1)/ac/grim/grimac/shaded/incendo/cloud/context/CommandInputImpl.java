/*    */ package ac.grim.grimac.shaded.incendo.cloud.context;
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
/*    */ final class CommandInputImpl
/*    */   implements CommandInput
/*    */ {
/*    */   private final String input;
/*    */   private int cursor;
/*    */   
/*    */   CommandInputImpl(String input) {
/* 36 */     this(input, 0);
/*    */   }
/*    */   
/*    */   CommandInputImpl(String input, int cursor) {
/* 40 */     this.input = input;
/* 41 */     this.cursor = cursor;
/*    */   }
/*    */ 
/*    */   
/*    */   public String input() {
/* 46 */     return this.input;
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandInput appendString(String string) {
/* 51 */     if (hasRemainingInput() && !remainingInput().endsWith(" ")) {
/* 52 */       return new CommandInputImpl(String.format("%s %s", new Object[] { this.input, string }), this.cursor);
/*    */     }
/* 54 */     return new CommandInputImpl(this.input + string, this.cursor);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int cursor() {
/* 60 */     return this.cursor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void moveCursor(int chars) {
/* 65 */     if (cursor() + chars > length()) {
/* 66 */       throw new CommandInput.CursorOutOfBoundsException(cursor() + chars, length());
/*    */     }
/* 68 */     this.cursor += chars;
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandInput cursor(int cursor) {
/* 73 */     if (cursor < 0 || cursor > length()) {
/* 74 */       throw new CommandInput.CursorOutOfBoundsException(cursor, length());
/*    */     }
/* 76 */     this.cursor = cursor;
/* 77 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandInput copy() {
/* 82 */     return new CommandInputImpl(this.input, this.cursor);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\CommandInputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
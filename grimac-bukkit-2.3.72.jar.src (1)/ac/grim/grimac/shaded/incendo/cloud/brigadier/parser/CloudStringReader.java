/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.parser;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import com.mojang.brigadier.StringReader;
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
/*    */ final class CloudStringReader
/*    */   extends StringReader
/*    */ {
/*    */   private final CommandInput commandInput;
/*    */   
/*    */   static CloudStringReader of(CommandInput commandInput) {
/* 35 */     return new CloudStringReader(commandInput);
/*    */   }
/*    */   
/*    */   private CloudStringReader(CommandInput commandInput) {
/* 39 */     super(commandInput.input());
/* 40 */     this.commandInput = commandInput;
/* 41 */     super.setCursor(commandInput.cursor());
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCursor(int cursor) {
/* 46 */     super.setCursor(cursor);
/* 47 */     this.commandInput.cursor(cursor);
/*    */   }
/*    */ 
/*    */   
/*    */   public char read() {
/* 52 */     super.read();
/* 53 */     return this.commandInput.read();
/*    */   }
/*    */ 
/*    */   
/*    */   public void skip() {
/* 58 */     super.skip();
/* 59 */     this.commandInput.moveCursor(1);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\parser\CloudStringReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
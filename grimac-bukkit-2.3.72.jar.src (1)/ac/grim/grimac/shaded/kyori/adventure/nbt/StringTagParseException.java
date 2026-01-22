/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ class StringTagParseException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = -3001637554903912905L;
/*    */   private final CharSequence buffer;
/*    */   private final int position;
/*    */   
/*    */   StringTagParseException(String message, CharSequence buffer, int position) {
/* 37 */     super(message);
/* 38 */     this.buffer = buffer;
/* 39 */     this.position = position;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 46 */     return super.getMessage() + "(at position " + this.position + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\StringTagParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
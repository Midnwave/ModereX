/*    */ package ac.grim.grimac.shaded.snakeyaml.reader;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
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
/*    */ public class ReaderException
/*    */   extends YAMLException
/*    */ {
/*    */   private static final long serialVersionUID = 8710781187529689083L;
/*    */   private final String name;
/*    */   private final int codePoint;
/*    */   private final int position;
/*    */   
/*    */   public ReaderException(String name, int position, int codePoint, String message) {
/* 26 */     super(message);
/* 27 */     this.name = name;
/* 28 */     this.codePoint = codePoint;
/* 29 */     this.position = position;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 33 */     return this.name;
/*    */   }
/*    */   
/*    */   public int getCodePoint() {
/* 37 */     return this.codePoint;
/*    */   }
/*    */   
/*    */   public int getPosition() {
/* 41 */     return this.position;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     String s = new String(Character.toChars(this.codePoint));
/* 47 */     return "unacceptable code point '" + s + "' (0x" + Integer.toHexString(this.codePoint).toUpperCase() + ") " + 
/* 48 */       getMessage() + "\nin \"" + this.name + "\", position " + this.position;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\reader\ReaderException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
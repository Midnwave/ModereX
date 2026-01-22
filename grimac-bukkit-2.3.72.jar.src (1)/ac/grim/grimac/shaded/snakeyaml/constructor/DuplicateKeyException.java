/*    */ package ac.grim.grimac.shaded.snakeyaml.constructor;
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
/*    */ 
/*    */ public class DuplicateKeyException
/*    */   extends ConstructorException
/*    */ {
/*    */   protected DuplicateKeyException(Mark contextMark, Object key, Mark problemMark) {
/* 31 */     super("while constructing a mapping", contextMark, "found duplicate key " + key, problemMark);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\DuplicateKeyException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
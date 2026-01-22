/*    */ package ac.grim.grimac.shaded.snakeyaml.constructor;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.MarkedYAMLException;
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
/*    */ public class ConstructorException
/*    */   extends MarkedYAMLException
/*    */ {
/*    */   private static final long serialVersionUID = -8816339931365239910L;
/*    */   
/*    */   protected ConstructorException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
/* 37 */     super(context, contextMark, problem, problemMark, cause);
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
/*    */   protected ConstructorException(String context, Mark contextMark, String problem, Mark problemMark) {
/* 50 */     this(context, contextMark, problem, problemMark, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\ConstructorException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
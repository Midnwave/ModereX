/*    */ package ac.grim.grimac.shaded.snakeyaml.parser;
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
/*    */ public class ParserException
/*    */   extends MarkedYAMLException
/*    */ {
/*    */   private static final long serialVersionUID = -2349253802798398038L;
/*    */   
/*    */   public ParserException(String context, Mark contextMark, String problem, Mark problemMark) {
/* 35 */     super(context, contextMark, problem, problemMark, null, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\parser\ParserException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
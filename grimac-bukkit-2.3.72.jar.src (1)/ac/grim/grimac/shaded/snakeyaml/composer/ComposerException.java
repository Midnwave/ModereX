/*    */ package ac.grim.grimac.shaded.snakeyaml.composer;
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
/*    */ public class ComposerException
/*    */   extends MarkedYAMLException
/*    */ {
/*    */   private static final long serialVersionUID = 2146314636913113935L;
/*    */   
/*    */   protected ComposerException(String context, Mark contextMark, String problem, Mark problemMark) {
/* 35 */     super(context, contextMark, problem, problemMark);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\composer\ComposerException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
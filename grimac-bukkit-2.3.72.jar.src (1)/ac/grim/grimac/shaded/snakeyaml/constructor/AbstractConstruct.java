/*    */ package ac.grim.grimac.shaded.snakeyaml.constructor;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*    */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
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
/*    */ public abstract class AbstractConstruct
/*    */   implements Construct
/*    */ {
/*    */   public void construct2ndStep(Node node, Object data) {
/* 32 */     if (node.isTwoStepsConstruction()) {
/* 33 */       throw new IllegalStateException("Not Implemented in " + getClass().getName());
/*    */     }
/* 35 */     throw new YAMLException("Unexpected recursive structure for Node: " + node);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\AbstractConstruct.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
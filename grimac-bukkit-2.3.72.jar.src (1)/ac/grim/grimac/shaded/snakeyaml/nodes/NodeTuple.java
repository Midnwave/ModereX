/*    */ package ac.grim.grimac.shaded.snakeyaml.nodes;
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
/*    */ public final class NodeTuple
/*    */ {
/*    */   private final Node keyNode;
/*    */   private final Node valueNode;
/*    */   
/*    */   public NodeTuple(Node keyNode, Node valueNode) {
/* 25 */     if (keyNode == null || valueNode == null) {
/* 26 */       throw new NullPointerException("Nodes must be provided.");
/*    */     }
/* 28 */     this.keyNode = keyNode;
/* 29 */     this.valueNode = valueNode;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Node getKeyNode() {
/* 38 */     return this.keyNode;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Node getValueNode() {
/* 47 */     return this.valueNode;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 52 */     return "<NodeTuple keyNode=" + this.keyNode + "; valueNode=" + this.valueNode + ">";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\nodes\NodeTuple.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.snakeyaml.nodes;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
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
/*    */ public class ScalarNode
/*    */   extends Node
/*    */ {
/*    */   private final DumperOptions.ScalarStyle style;
/*    */   private final String value;
/*    */   
/*    */   public ScalarNode(Tag tag, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
/* 32 */     this(tag, true, value, startMark, endMark, style);
/*    */   }
/*    */ 
/*    */   
/*    */   public ScalarNode(Tag tag, boolean resolved, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
/* 37 */     super(tag, startMark, endMark);
/* 38 */     if (value == null) {
/* 39 */       throw new NullPointerException("value in a Node is required.");
/*    */     }
/* 41 */     this.value = value;
/* 42 */     if (style == null) {
/* 43 */       throw new NullPointerException("Scalar style must be provided.");
/*    */     }
/* 45 */     this.style = style;
/* 46 */     this.resolved = resolved;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DumperOptions.ScalarStyle getScalarStyle() {
/* 57 */     return this.style;
/*    */   }
/*    */ 
/*    */   
/*    */   public NodeId getNodeId() {
/* 62 */     return NodeId.scalar;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 71 */     return this.value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 75 */     return "<" + getClass().getName() + " (tag=" + getTag() + ", value=" + getValue() + ")>";
/*    */   }
/*    */   
/*    */   public boolean isPlain() {
/* 79 */     return (this.style == DumperOptions.ScalarStyle.PLAIN);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\nodes\ScalarNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
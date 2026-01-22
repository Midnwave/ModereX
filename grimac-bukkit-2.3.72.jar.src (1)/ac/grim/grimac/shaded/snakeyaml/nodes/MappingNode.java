/*     */ package ac.grim.grimac.shaded.snakeyaml.nodes;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MappingNode
/*     */   extends CollectionNode<NodeTuple>
/*     */ {
/*     */   private List<NodeTuple> value;
/*     */   private boolean merged = false;
/*     */   
/*     */   public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
/*  33 */     super(tag, startMark, endMark, flowStyle);
/*  34 */     if (value == null) {
/*  35 */       throw new NullPointerException("value in a Node is required.");
/*     */     }
/*  37 */     this.value = value;
/*  38 */     this.resolved = resolved;
/*     */   }
/*     */   
/*     */   public MappingNode(Tag tag, List<NodeTuple> value, DumperOptions.FlowStyle flowStyle) {
/*  42 */     this(tag, true, value, (Mark)null, (Mark)null, flowStyle);
/*     */   }
/*     */ 
/*     */   
/*     */   public NodeId getNodeId() {
/*  47 */     return NodeId.mapping;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<NodeTuple> getValue() {
/*  56 */     return this.value;
/*     */   }
/*     */   
/*     */   public void setValue(List<NodeTuple> mergedValue) {
/*  60 */     this.value = mergedValue;
/*     */   }
/*     */   
/*     */   public void setOnlyKeyType(Class<? extends Object> keyType) {
/*  64 */     for (NodeTuple nodes : this.value) {
/*  65 */       nodes.getKeyNode().setType(keyType);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTypes(Class<? extends Object> keyType, Class<? extends Object> valueType) {
/*  70 */     for (NodeTuple nodes : this.value) {
/*  71 */       nodes.getValueNode().setType(valueType);
/*  72 */       nodes.getKeyNode().setType(keyType);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  79 */     StringBuilder buf = new StringBuilder();
/*  80 */     for (NodeTuple node : getValue()) {
/*  81 */       buf.append("{ key=");
/*  82 */       buf.append(node.getKeyNode());
/*  83 */       buf.append("; value=");
/*  84 */       if (node.getValueNode() instanceof CollectionNode) {
/*     */         
/*  86 */         buf.append(System.identityHashCode(node.getValueNode()));
/*     */       } else {
/*  88 */         buf.append(node);
/*     */       } 
/*  90 */       buf.append(" }");
/*     */     } 
/*  92 */     String values = buf.toString();
/*  93 */     return "<" + getClass().getName() + " (tag=" + getTag() + ", values=" + values + ")>";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMerged(boolean merged) {
/* 100 */     this.merged = merged;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMerged() {
/* 107 */     return this.merged;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\nodes\MappingNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apiguardian.api.API;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class AmbiguousNodeException
/*     */   extends IllegalStateException
/*     */ {
/*     */   private final CommandNode<?> parentNode;
/*     */   private final CommandNode<?> ambiguousNode;
/*     */   private final List<CommandNode<?>> children;
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public AmbiguousNodeException(CommandNode<?> parentNode, CommandNode<?> ambiguousNode, List<CommandNode<?>> children) {
/*  62 */     this.parentNode = parentNode;
/*  63 */     this.ambiguousNode = ambiguousNode;
/*  64 */     this.children = children;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandNode<?> parentNode() {
/*  73 */     return this.parentNode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandNode<?> ambiguousNode() {
/*  82 */     return this.ambiguousNode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommandNode<?>> children() {
/*  91 */     return Collections.unmodifiableList(this.children);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 100 */     StringBuilder stringBuilder = (new StringBuilder("Ambiguous Node: ")).append(this.ambiguousNode.component().name()).append(" cannot be added as a child to ").append((this.parentNode == null) ? "<root>" : this.parentNode.component().name()).append(" (All children: ");
/* 101 */     Iterator<CommandNode<?>> childIterator = this.children.iterator();
/* 102 */     while (childIterator.hasNext()) {
/* 103 */       stringBuilder.append(((CommandNode)childIterator.next()).component().name());
/* 104 */       if (childIterator.hasNext()) {
/* 105 */         stringBuilder.append(", ");
/*     */       }
/*     */     } 
/* 108 */     return stringBuilder.append(")")
/* 109 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\AmbiguousNodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
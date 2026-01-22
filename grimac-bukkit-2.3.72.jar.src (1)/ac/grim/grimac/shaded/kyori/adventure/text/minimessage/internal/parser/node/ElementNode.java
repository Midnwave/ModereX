/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ElementNode
/*     */   implements Node
/*     */ {
/*     */   @Nullable
/*     */   private final ElementNode parent;
/*     */   @Nullable
/*     */   private final Token token;
/*     */   private final String sourceMessage;
/*  45 */   private final List<ElementNode> children = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ElementNode(@Nullable ElementNode parent, @Nullable Token token, @NotNull String sourceMessage) {
/*  56 */     this.parent = parent;
/*  57 */     this.token = token;
/*  58 */     this.sourceMessage = sourceMessage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ElementNode parent() {
/*  69 */     return this.parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Token token() {
/*  79 */     return this.token;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String sourceMessage() {
/*  89 */     return this.sourceMessage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<ElementNode> children() {
/* 100 */     return Collections.unmodifiableList(this.children);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<ElementNode> unsafeChildren() {
/* 110 */     return this.children;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addChild(@NotNull ElementNode childNode) {
/* 122 */     int last = this.children.size() - 1;
/* 123 */     if (!(childNode instanceof TextNode) || this.children.isEmpty() || !(this.children.get(last) instanceof TextNode)) {
/* 124 */       this.children.add(childNode);
/*     */     } else {
/* 126 */       TextNode lastNode = (TextNode)this.children.remove(last);
/* 127 */       if (lastNode.token().endIndex() == childNode.token().startIndex()) {
/* 128 */         Token replace = new Token(lastNode.token().startIndex(), childNode.token().endIndex(), TokenType.TEXT);
/* 129 */         this.children.add(new TextNode(this, replace, lastNode.sourceMessage()));
/*     */       } else {
/*     */         
/* 132 */         this.children.add(lastNode);
/* 133 */         this.children.add(childNode);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public StringBuilder buildToString(@NotNull StringBuilder sb, int indent) {
/* 147 */     char[] in = ident(indent);
/* 148 */     sb.append(in).append("Node {\n");
/* 149 */     for (ElementNode child : this.children) {
/* 150 */       child.buildToString(sb, indent + 1);
/*     */     }
/* 152 */     sb.append(in).append("}\n");
/* 153 */     return sb;
/*     */   }
/*     */   
/*     */   char[] ident(int indent) {
/* 157 */     char[] c = new char[indent * 2];
/* 158 */     Arrays.fill(c, ' ');
/* 159 */     return c;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/* 164 */     return buildToString(new StringBuilder(), 0).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\ElementNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
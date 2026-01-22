/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenParser;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public final class TagNode
/*     */   extends ElementNode
/*     */ {
/*     */   private final List<TagPart> parts;
/*     */   @Nullable
/*  43 */   private Tag tag = null;
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
/*     */   public TagNode(@NotNull ElementNode parent, @NotNull Token token, @NotNull String sourceMessage, TokenParser.TagProvider tagProvider) {
/*  60 */     super(parent, token, sourceMessage);
/*  61 */     this.parts = genParts(token, sourceMessage, tagProvider);
/*     */ 
/*     */     
/*  64 */     if (this.parts.isEmpty()) {
/*  65 */       throw new ParsingExceptionImpl("Tag has no parts? " + this, sourceMessage(), new Token[] { token() });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private static List<TagPart> genParts(@NotNull Token token, @NotNull String sourceMessage, TokenParser.TagProvider tagProvider) {
/*  74 */     ArrayList<TagPart> parts = new ArrayList<>();
/*     */     
/*  76 */     if (token.childTokens() != null) {
/*  77 */       for (Token childToken : token.childTokens()) {
/*  78 */         parts.add(new TagPart(sourceMessage, childToken, tagProvider));
/*     */       }
/*     */     }
/*     */     
/*  82 */     return parts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<TagPart> parts() {
/*  92 */     return this.parts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String name() {
/* 102 */     return ((TagPart)this.parts.get(0)).value();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Token token() {
/* 107 */     return Objects.<Token>requireNonNull(super.token(), "token is not set");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Tag tag() {
/* 117 */     return Objects.<Tag>requireNonNull(this.tag, "no tag set");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void tag(@NotNull Tag tag) {
/* 127 */     this.tag = tag;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StringBuilder buildToString(@NotNull StringBuilder sb, int indent) {
/* 132 */     char[] in = ident(indent);
/* 133 */     sb.append(in).append("TagNode(");
/*     */     
/* 135 */     int size = this.parts.size();
/* 136 */     for (int i = 0; i < size; i++) {
/* 137 */       TagPart part = this.parts.get(i);
/* 138 */       sb.append('\'').append(part.value()).append('\'');
/* 139 */       if (i != size - 1) {
/* 140 */         sb.append(", ");
/*     */       }
/*     */     } 
/*     */     
/* 144 */     sb.append(") {\n");
/*     */     
/* 146 */     for (ElementNode child : children()) {
/* 147 */       child.buildToString(sb, indent + 1);
/*     */     }
/* 149 */     sb.append(in).append("}\n");
/* 150 */     return sb;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\TagNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenParser;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
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
/*     */ public final class TagPart
/*     */   implements Tag.Argument
/*     */ {
/*     */   private final String value;
/*     */   private final Token token;
/*     */   
/*     */   public TagPart(@NotNull String sourceMessage, @NotNull Token token, TokenParser.TagProvider tagResolver) {
/*  53 */     String v = unquoteAndEscape(sourceMessage, token.startIndex(), token.endIndex());
/*  54 */     v = TokenParser.resolvePreProcessTags(v, tagResolver);
/*     */     
/*  56 */     this.value = v;
/*  57 */     this.token = token;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String value() {
/*  68 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Token token() {
/*  78 */     return this.token;
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
/*     */   @NotNull
/*     */   public static String unquoteAndEscape(@NotNull String text, int start, int end) {
/*  91 */     if (start == end) {
/*  92 */       return "";
/*     */     }
/*     */     
/*  95 */     int startIndex = start;
/*  96 */     int endIndex = end;
/*     */     
/*  98 */     char firstChar = text.charAt(startIndex);
/*  99 */     char lastChar = text.charAt(endIndex - 1);
/* 100 */     if (firstChar == '\'' || firstChar == '"') {
/* 101 */       startIndex++;
/*     */     } else {
/* 103 */       return text.substring(startIndex, endIndex);
/*     */     } 
/* 105 */     if (lastChar == '\'' || lastChar == '"') {
/* 106 */       endIndex--;
/*     */     }
/*     */     
/* 109 */     if (startIndex > endIndex)
/*     */     {
/* 111 */       return text.substring(start, end);
/*     */     }
/*     */     
/* 114 */     return TokenParser.unescape(text, startIndex, endIndex, i -> (i == firstChar || i == 92));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 119 */     return this.value;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\node\TagPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
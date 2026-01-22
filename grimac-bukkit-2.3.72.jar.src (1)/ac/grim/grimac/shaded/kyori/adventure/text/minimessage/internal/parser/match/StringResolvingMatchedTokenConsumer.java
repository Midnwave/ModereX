/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.TagInternals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenParser;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.PreProcess;
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
/*     */ public final class StringResolvingMatchedTokenConsumer
/*     */   extends MatchedTokenConsumer<String>
/*     */ {
/*     */   private final StringBuilder builder;
/*     */   private final TokenParser.TagProvider tagProvider;
/*     */   
/*     */   public StringResolvingMatchedTokenConsumer(@NotNull String input, @NotNull TokenParser.TagProvider tagProvider) {
/*  63 */     super(input);
/*  64 */     this.builder = new StringBuilder(input.length());
/*  65 */     this.tagProvider = tagProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(int start, int end, @NotNull TokenType tokenType) {
/*  70 */     super.accept(start, end, tokenType);
/*     */     
/*  72 */     if (tokenType != TokenType.OPEN_TAG) {
/*     */       
/*  74 */       this.builder.append(this.input, start, end);
/*     */     } else {
/*     */       
/*  77 */       String match = this.input.substring(start, end);
/*  78 */       String cleanup = this.input.substring(start + 1, end - 1);
/*     */       
/*  80 */       int index = cleanup.indexOf(':');
/*  81 */       String tag = (index == -1) ? cleanup : cleanup.substring(0, index);
/*     */ 
/*     */       
/*  84 */       if (TagInternals.sanitizeAndCheckValidTagName(tag)) {
/*  85 */         List<Token> tokens = TokenParser.tokenize(match, false);
/*  86 */         List<TagPart> parts = new ArrayList<>();
/*  87 */         List<Token> childs = tokens.isEmpty() ? null : ((Token)tokens.get(0)).childTokens();
/*  88 */         if (childs != null) {
/*  89 */           for (int i = 1; i < childs.size(); i++) {
/*  90 */             parts.add(new TagPart(match, childs.get(i), this.tagProvider));
/*     */           }
/*     */         }
/*     */         
/*  94 */         Tag replacement = this.tagProvider.resolve(TokenParser.TagProvider.sanitizePlaceholderName(tag), parts, tokens.get(0));
/*     */         
/*  96 */         if (replacement instanceof PreProcess) {
/*  97 */           this.builder.append(Objects.<String>requireNonNull(((PreProcess)replacement).value(), "PreProcess replacements cannot return null"));
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */       
/* 103 */       this.builder.append(match);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String result() {
/* 109 */     return this.builder.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\match\StringResolvingMatchedTokenConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
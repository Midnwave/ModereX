/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Stream;
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
/*     */ public final class Token
/*     */   implements Examinable
/*     */ {
/*     */   private final int startIndex;
/*     */   private final int endIndex;
/*     */   private final TokenType type;
/*  44 */   private List<Token> childTokens = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token(int startIndex, int endIndex, TokenType type) {
/*  55 */     this.startIndex = startIndex;
/*  56 */     this.endIndex = endIndex;
/*  57 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int startIndex() {
/*  67 */     return this.startIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int endIndex() {
/*  77 */     return this.endIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenType type() {
/*  87 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> childTokens() {
/*  97 */     return this.childTokens;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void childTokens(List<Token> childTokens) {
/* 107 */     this.childTokens = childTokens;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence get(CharSequence message) {
/* 118 */     return message.subSequence(this.startIndex, this.endIndex);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 123 */     return Stream.of(new ExaminableProperty[] {
/* 124 */           ExaminableProperty.of("startIndex", this.startIndex), 
/* 125 */           ExaminableProperty.of("endIndex", this.endIndex), 
/* 126 */           ExaminableProperty.of("type", this.type)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 132 */     if (this == other) return true; 
/* 133 */     if (!(other instanceof Token)) return false; 
/* 134 */     Token that = (Token)other;
/* 135 */     return (this.startIndex == that.startIndex && this.endIndex == that.endIndex && this.type == that.type);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 140 */     return Objects.hash(new Object[] { Integer.valueOf(this.startIndex), Integer.valueOf(this.endIndex), this.type });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 145 */     return Internals.toString(this);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
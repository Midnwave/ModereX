/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import java.util.Arrays;
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
/*     */ @Internal
/*     */ public class ParsingExceptionImpl
/*     */   extends ParsingException
/*     */ {
/*     */   private static final long serialVersionUID = 2507190809441787202L;
/*     */   private final String originalText;
/*     */   private Token[] tokens;
/*     */   
/*     */   public ParsingExceptionImpl(String message, @Nullable String originalText, @NotNull Token... tokens) {
/*  57 */     super(message, null, true, false);
/*  58 */     this.tokens = tokens;
/*  59 */     this.originalText = originalText;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParsingExceptionImpl(String message, @Nullable String originalText, @Nullable Throwable cause, boolean withStackTrace, @NotNull Token... tokens) {
/*  79 */     super(message, cause, true, withStackTrace);
/*  80 */     this.tokens = tokens;
/*  81 */     this.originalText = originalText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  88 */     String arrowInfo = ((tokens()).length != 0) ? ("\n\t" + arrow()) : "";
/*     */ 
/*     */     
/*  91 */     String messageInfo = (originalText() != null) ? ("\n\t" + originalText() + arrowInfo) : "";
/*  92 */     return super.getMessage() + messageInfo;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String detailMessage() {
/*  97 */     return super.getMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String originalText() {
/* 108 */     return this.originalText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Token[] tokens() {
/* 118 */     return this.tokens;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void tokens(@NotNull Token[] tokens) {
/* 128 */     this.tokens = tokens;
/*     */   }
/*     */   
/*     */   private String arrow() {
/* 132 */     Token[] ts = tokens();
/* 133 */     char[] chars = new char[ts[ts.length - 1].endIndex()];
/*     */     
/* 135 */     int i = 0;
/* 136 */     for (Token t : ts) {
/* 137 */       Arrays.fill(chars, i, t.startIndex(), ' ');
/* 138 */       chars[t.startIndex()] = '^';
/* 139 */       if (Math.abs(t.startIndex() - t.endIndex()) > 1) {
/* 140 */         Arrays.fill(chars, t.startIndex() + 1, t.endIndex() - 1, '~');
/*     */       }
/* 142 */       chars[t.endIndex() - 1] = '^';
/* 143 */       i = t.endIndex();
/*     */     } 
/* 145 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   
/*     */   public int startIndex() {
/* 150 */     if (this.tokens.length == 0) return -1; 
/* 151 */     return this.tokens[0].startIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public int endIndex() {
/* 156 */     if (this.tokens.length == 0) return -1; 
/* 157 */     return this.tokens[this.tokens.length - 1].endIndex();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\ParsingExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
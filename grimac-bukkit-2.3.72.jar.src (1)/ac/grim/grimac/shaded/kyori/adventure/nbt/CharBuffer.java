/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
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
/*     */ final class CharBuffer
/*     */ {
/*     */   private final CharSequence sequence;
/*     */   private int index;
/*     */   
/*     */   CharBuffer(CharSequence sequence) {
/*  34 */     this.sequence = sequence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char peek() {
/*  43 */     return this.sequence.charAt(this.index);
/*     */   }
/*     */   
/*     */   public char peek(int offset) {
/*  47 */     return this.sequence.charAt(this.index + offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char take() {
/*  56 */     return this.sequence.charAt(this.index++);
/*     */   }
/*     */   
/*     */   public boolean advance() {
/*  60 */     this.index++;
/*  61 */     return hasMore();
/*     */   }
/*     */   
/*     */   public boolean hasMore() {
/*  65 */     return (this.index < this.sequence.length());
/*     */   }
/*     */   
/*     */   public boolean hasMore(int offset) {
/*  69 */     return (this.index + offset < this.sequence.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence takeUntil(char until) throws StringTagParseException {
/*  80 */     until = Character.toLowerCase(until);
/*  81 */     int endIdx = -1;
/*  82 */     for (int idx = this.index; idx < this.sequence.length(); idx++) {
/*  83 */       if (this.sequence.charAt(idx) == '\\') {
/*  84 */         idx++;
/*  85 */       } else if (Character.toLowerCase(this.sequence.charAt(idx)) == until) {
/*  86 */         endIdx = idx;
/*     */         break;
/*     */       } 
/*     */     } 
/*  90 */     if (endIdx == -1) {
/*  91 */       throw makeError("No occurrence of " + until + " was found");
/*     */     }
/*     */     
/*  94 */     CharSequence result = this.sequence.subSequence(this.index, endIdx);
/*  95 */     this.index = endIdx + 1;
/*  96 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence takeRest() {
/* 105 */     int length = this.sequence.length();
/* 106 */     CharSequence result = this.sequence.subSequence(this.index, length);
/* 107 */     this.index = length;
/* 108 */     return result;
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
/*     */   public CharBuffer expect(char expectedChar) throws StringTagParseException {
/* 121 */     skipWhitespace();
/* 122 */     if (!hasMore()) {
/* 123 */       throw makeError("Expected character '" + expectedChar + "' but got EOF");
/*     */     }
/* 125 */     if (peek() != expectedChar) {
/* 126 */       throw makeError("Expected character '" + expectedChar + "' but got '" + peek() + "'");
/*     */     }
/* 128 */     take();
/* 129 */     return this;
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
/*     */   public boolean takeIf(char token) {
/* 141 */     skipWhitespace();
/* 142 */     if (hasMore() && peek() == token) {
/* 143 */       advance();
/* 144 */       return true;
/*     */     } 
/* 146 */     return false;
/*     */   }
/*     */   
/*     */   public CharBuffer skipWhitespace() {
/* 150 */     for (; hasMore() && Character.isWhitespace(peek()); advance());
/* 151 */     return this;
/*     */   }
/*     */   
/*     */   public StringTagParseException makeError(String message) {
/* 155 */     return new StringTagParseException(message, this.sequence, this.index);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\CharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
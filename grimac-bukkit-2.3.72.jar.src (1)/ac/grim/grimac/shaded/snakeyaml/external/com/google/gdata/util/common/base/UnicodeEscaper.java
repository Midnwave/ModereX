/*     */ package ac.grim.grimac.shaded.snakeyaml.external.com.google.gdata.util.common.base;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class UnicodeEscaper
/*     */   implements Escaper
/*     */ {
/*     */   private static final int DEST_PAD = 32;
/*     */   
/*     */   protected abstract char[] escape(int paramInt);
/*     */   
/*     */   protected int nextEscapeIndex(CharSequence csq, int start, int end) {
/* 106 */     int index = start;
/* 107 */     while (index < end) {
/* 108 */       int cp = codePointAt(csq, index, end);
/* 109 */       if (cp < 0 || escape(cp) != null) {
/*     */         break;
/*     */       }
/* 112 */       index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
/*     */     } 
/* 114 */     return index;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String escape(String string) {
/* 140 */     int end = string.length();
/* 141 */     int index = nextEscapeIndex(string, 0, end);
/* 142 */     return (index == end) ? string : escapeSlow(string, index);
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
/*     */   protected final String escapeSlow(String s, int index) {
/* 162 */     int end = s.length();
/*     */ 
/*     */     
/* 165 */     char[] dest = DEST_TL.get();
/* 166 */     int destIndex = 0;
/* 167 */     int unescapedChunkStart = 0;
/*     */     
/* 169 */     while (index < end) {
/* 170 */       int cp = codePointAt(s, index, end);
/* 171 */       if (cp < 0) {
/* 172 */         throw new IllegalArgumentException("Trailing high surrogate at end of input");
/*     */       }
/* 174 */       char[] escaped = escape(cp);
/* 175 */       if (escaped != null) {
/* 176 */         int i = index - unescapedChunkStart;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 181 */         int sizeNeeded = destIndex + i + escaped.length;
/* 182 */         if (dest.length < sizeNeeded) {
/* 183 */           int destLength = sizeNeeded + end - index + 32;
/* 184 */           dest = growBuffer(dest, destIndex, destLength);
/*     */         } 
/*     */         
/* 187 */         if (i > 0) {
/* 188 */           s.getChars(unescapedChunkStart, index, dest, destIndex);
/* 189 */           destIndex += i;
/*     */         } 
/* 191 */         if (escaped.length > 0) {
/* 192 */           System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
/* 193 */           destIndex += escaped.length;
/*     */         } 
/*     */       } 
/* 196 */       unescapedChunkStart = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
/* 197 */       index = nextEscapeIndex(s, unescapedChunkStart, end);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 203 */     int charsSkipped = end - unescapedChunkStart;
/* 204 */     if (charsSkipped > 0) {
/* 205 */       int endIndex = destIndex + charsSkipped;
/* 206 */       if (dest.length < endIndex) {
/* 207 */         dest = growBuffer(dest, destIndex, endIndex);
/*     */       }
/* 209 */       s.getChars(unescapedChunkStart, end, dest, destIndex);
/* 210 */       destIndex = endIndex;
/*     */     } 
/* 212 */     return new String(dest, 0, destIndex);
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
/*     */   public Appendable escape(final Appendable out) {
/* 252 */     assert out != null;
/*     */     
/* 254 */     return new Appendable() {
/* 255 */         int pendingHighSurrogate = -1;
/* 256 */         final char[] decodedChars = new char[2];
/*     */         
/*     */         public Appendable append(CharSequence csq) throws IOException {
/* 259 */           return append(csq, 0, csq.length());
/*     */         }
/*     */         
/*     */         public Appendable append(CharSequence csq, int start, int end) throws IOException {
/* 263 */           int index = start;
/* 264 */           if (index < end) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 271 */             int unescapedChunkStart = index;
/* 272 */             if (this.pendingHighSurrogate != -1) {
/*     */ 
/*     */ 
/*     */               
/* 276 */               char c = csq.charAt(index++);
/* 277 */               if (!Character.isLowSurrogate(c)) {
/* 278 */                 throw new IllegalArgumentException("Expected low surrogate character but got " + c);
/*     */               }
/* 280 */               char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, c));
/* 281 */               if (escaped != null) {
/*     */ 
/*     */ 
/*     */                 
/* 285 */                 outputChars(escaped, escaped.length);
/* 286 */                 unescapedChunkStart++;
/*     */               
/*     */               }
/*     */               else {
/*     */ 
/*     */                 
/* 292 */                 out.append((char)this.pendingHighSurrogate);
/*     */               } 
/* 294 */               this.pendingHighSurrogate = -1;
/*     */             } 
/*     */ 
/*     */             
/*     */             while (true) {
/* 299 */               index = UnicodeEscaper.this.nextEscapeIndex(csq, index, end);
/* 300 */               if (index > unescapedChunkStart) {
/* 301 */                 out.append(csq, unescapedChunkStart, index);
/*     */               }
/* 303 */               if (index == end) {
/*     */                 break;
/*     */               }
/*     */ 
/*     */               
/* 308 */               int cp = UnicodeEscaper.codePointAt(csq, index, end);
/* 309 */               if (cp < 0) {
/*     */ 
/*     */ 
/*     */                 
/* 313 */                 this.pendingHighSurrogate = -cp;
/*     */                 
/*     */                 break;
/*     */               } 
/* 317 */               char[] escaped = UnicodeEscaper.this.escape(cp);
/* 318 */               if (escaped != null) {
/* 319 */                 outputChars(escaped, escaped.length);
/*     */               
/*     */               }
/*     */               else {
/*     */                 
/* 324 */                 int len = Character.toChars(cp, this.decodedChars, 0);
/* 325 */                 outputChars(this.decodedChars, len);
/*     */               } 
/*     */ 
/*     */               
/* 329 */               index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
/* 330 */               unescapedChunkStart = index;
/*     */             } 
/*     */           } 
/* 333 */           return this;
/*     */         }
/*     */         
/*     */         public Appendable append(char c) throws IOException {
/* 337 */           if (this.pendingHighSurrogate != -1) {
/*     */ 
/*     */ 
/*     */             
/* 341 */             if (!Character.isLowSurrogate(c)) {
/* 342 */               throw new IllegalArgumentException("Expected low surrogate character but got '" + c + "' with value " + c);
/*     */             }
/*     */             
/* 345 */             char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, c));
/* 346 */             if (escaped != null) {
/* 347 */               outputChars(escaped, escaped.length);
/*     */             } else {
/* 349 */               out.append((char)this.pendingHighSurrogate);
/* 350 */               out.append(c);
/*     */             } 
/* 352 */             this.pendingHighSurrogate = -1;
/* 353 */           } else if (Character.isHighSurrogate(c)) {
/*     */             
/* 355 */             this.pendingHighSurrogate = c;
/*     */           } else {
/* 357 */             if (Character.isLowSurrogate(c)) {
/* 358 */               throw new IllegalArgumentException("Unexpected low surrogate character '" + c + "' with value " + c);
/*     */             }
/*     */ 
/*     */             
/* 362 */             char[] escaped = UnicodeEscaper.this.escape(c);
/* 363 */             if (escaped != null) {
/* 364 */               outputChars(escaped, escaped.length);
/*     */             } else {
/* 366 */               out.append(c);
/*     */             } 
/*     */           } 
/* 369 */           return this;
/*     */         }
/*     */         
/*     */         private void outputChars(char[] chars, int len) throws IOException {
/* 373 */           for (int n = 0; n < len; n++) {
/* 374 */             out.append(chars[n]);
/*     */           }
/*     */         }
/*     */       };
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
/*     */   protected static final int codePointAt(CharSequence seq, int index, int end) {
/* 413 */     if (index < end) {
/* 414 */       char c1 = seq.charAt(index++);
/* 415 */       if (c1 < '?' || c1 > '?')
/*     */       {
/* 417 */         return c1; } 
/* 418 */       if (c1 <= '?') {
/*     */ 
/*     */         
/* 421 */         if (index == end) {
/* 422 */           return -c1;
/*     */         }
/*     */         
/* 425 */         char c2 = seq.charAt(index);
/* 426 */         if (Character.isLowSurrogate(c2)) {
/* 427 */           return Character.toCodePoint(c1, c2);
/*     */         }
/* 429 */         throw new IllegalArgumentException("Expected low surrogate but got char '" + c2 + "' with value " + c2 + " at index " + index);
/*     */       } 
/*     */       
/* 432 */       throw new IllegalArgumentException("Unexpected low surrogate character '" + c1 + "' with value " + c1 + " at index " + (index - 1));
/*     */     } 
/*     */ 
/*     */     
/* 436 */     throw new IndexOutOfBoundsException("Index exceeds specified range");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final char[] growBuffer(char[] dest, int index, int size) {
/* 444 */     char[] copy = new char[size];
/* 445 */     if (index > 0) {
/* 446 */       System.arraycopy(dest, 0, copy, 0, index);
/*     */     }
/* 448 */     return copy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 456 */   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>()
/*     */     {
/*     */       protected char[] initialValue() {
/* 459 */         return new char[1024];
/*     */       }
/*     */     };
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\external\com\google\gdat\\util\common\base\UnicodeEscaper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
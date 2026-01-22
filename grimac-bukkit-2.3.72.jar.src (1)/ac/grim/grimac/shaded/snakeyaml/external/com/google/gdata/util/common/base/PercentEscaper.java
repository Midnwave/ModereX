/*     */ package ac.grim.grimac.shaded.snakeyaml.external.com.google.gdata.util.common.base;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PercentEscaper
/*     */   extends UnicodeEscaper
/*     */ {
/*     */   public static final String SAFECHARS_URLENCODER = "-_.*";
/*     */   public static final String SAFEPATHCHARS_URLENCODER = "-_.!~*'()@:$&,;=";
/*     */   public static final String SAFEQUERYSTRINGCHARS_URLENCODER = "-_.!~*'()@:$,;/?:";
/*  84 */   private static final char[] URI_ESCAPED_SPACE = new char[] { '+' };
/*     */   
/*  86 */   private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean plusForSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean[] safeOctets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentEscaper(String safeChars, boolean plusForSpace) {
/* 111 */     if (safeChars.matches(".*[0-9A-Za-z].*")) {
/* 112 */       throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 117 */     if (plusForSpace && safeChars.contains(" ")) {
/* 118 */       throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
/*     */     }
/*     */     
/* 121 */     if (safeChars.contains("%")) {
/* 122 */       throw new IllegalArgumentException("The '%' character cannot be specified as 'safe'");
/*     */     }
/* 124 */     this.plusForSpace = plusForSpace;
/* 125 */     this.safeOctets = createSafeOctets(safeChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean[] createSafeOctets(String safeChars) {
/* 134 */     int maxChar = 122;
/* 135 */     char[] safeCharArray = safeChars.toCharArray();
/* 136 */     for (char c1 : safeCharArray) {
/* 137 */       maxChar = Math.max(c1, maxChar);
/*     */     }
/* 139 */     boolean[] octets = new boolean[maxChar + 1]; int c;
/* 140 */     for (c = 48; c <= 57; c++) {
/* 141 */       octets[c] = true;
/*     */     }
/* 143 */     for (c = 65; c <= 90; c++) {
/* 144 */       octets[c] = true;
/*     */     }
/* 146 */     for (c = 97; c <= 122; c++) {
/* 147 */       octets[c] = true;
/*     */     }
/* 149 */     for (char c1 : safeCharArray) {
/* 150 */       octets[c1] = true;
/*     */     }
/* 152 */     return octets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int nextEscapeIndex(CharSequence csq, int index, int end) {
/* 161 */     for (; index < end; index++) {
/* 162 */       char c = csq.charAt(index);
/* 163 */       if (c >= this.safeOctets.length || !this.safeOctets[c]) {
/*     */         break;
/*     */       }
/*     */     } 
/* 167 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String escape(String s) {
/* 176 */     int slen = s.length();
/* 177 */     for (int index = 0; index < slen; index++) {
/* 178 */       char c = s.charAt(index);
/* 179 */       if (c >= this.safeOctets.length || !this.safeOctets[c]) {
/* 180 */         return escapeSlow(s, index);
/*     */       }
/*     */     } 
/* 183 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected char[] escape(int cp) {
/* 194 */     if (cp < this.safeOctets.length && this.safeOctets[cp])
/* 195 */       return null; 
/* 196 */     if (cp == 32 && this.plusForSpace)
/* 197 */       return URI_ESCAPED_SPACE; 
/* 198 */     if (cp <= 127) {
/*     */ 
/*     */       
/* 201 */       char[] dest = new char[3];
/* 202 */       dest[0] = '%';
/* 203 */       dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
/* 204 */       dest[1] = UPPER_HEX_DIGITS[cp >>> 4];
/* 205 */       return dest;
/* 206 */     }  if (cp <= 2047) {
/*     */ 
/*     */       
/* 209 */       char[] dest = new char[6];
/* 210 */       dest[0] = '%';
/* 211 */       dest[3] = '%';
/* 212 */       dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
/* 213 */       cp >>>= 4;
/* 214 */       dest[4] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 215 */       cp >>>= 2;
/* 216 */       dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
/* 217 */       cp >>>= 4;
/* 218 */       dest[1] = UPPER_HEX_DIGITS[0xC | cp];
/* 219 */       return dest;
/* 220 */     }  if (cp <= 65535) {
/*     */ 
/*     */       
/* 223 */       char[] dest = new char[9];
/* 224 */       dest[0] = '%';
/* 225 */       dest[1] = 'E';
/* 226 */       dest[3] = '%';
/* 227 */       dest[6] = '%';
/* 228 */       dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
/* 229 */       cp >>>= 4;
/* 230 */       dest[7] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 231 */       cp >>>= 2;
/* 232 */       dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
/* 233 */       cp >>>= 4;
/* 234 */       dest[4] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 235 */       cp >>>= 2;
/* 236 */       dest[2] = UPPER_HEX_DIGITS[cp];
/* 237 */       return dest;
/* 238 */     }  if (cp <= 1114111) {
/* 239 */       char[] dest = new char[12];
/*     */ 
/*     */       
/* 242 */       dest[0] = '%';
/* 243 */       dest[1] = 'F';
/* 244 */       dest[3] = '%';
/* 245 */       dest[6] = '%';
/* 246 */       dest[9] = '%';
/* 247 */       dest[11] = UPPER_HEX_DIGITS[cp & 0xF];
/* 248 */       cp >>>= 4;
/* 249 */       dest[10] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 250 */       cp >>>= 2;
/* 251 */       dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
/* 252 */       cp >>>= 4;
/* 253 */       dest[7] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 254 */       cp >>>= 2;
/* 255 */       dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
/* 256 */       cp >>>= 4;
/* 257 */       dest[4] = UPPER_HEX_DIGITS[0x8 | cp & 0x3];
/* 258 */       cp >>>= 2;
/* 259 */       dest[2] = UPPER_HEX_DIGITS[cp & 0x7];
/* 260 */       return dest;
/*     */     } 
/*     */ 
/*     */     
/* 264 */     throw new IllegalArgumentException("Invalid unicode character value " + cp);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\external\com\google\gdat\\util\common\base\PercentEscaper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
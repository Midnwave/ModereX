/*     */ package ac.grim.grimac.shaded.snakeyaml.external.biz.base64Coder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base64Coder
/*     */ {
/*  33 */   private static final String systemLineSeparator = System.getProperty("line.separator");
/*     */ 
/*     */   
/*  36 */   private static final char[] map1 = new char[64];
/*     */   
/*     */   static {
/*  39 */     int i = 0; char c;
/*  40 */     for (c = 'A'; c <= 'Z'; c = (char)(c + 1)) {
/*  41 */       map1[i++] = c;
/*     */     }
/*  43 */     for (c = 'a'; c <= 'z'; c = (char)(c + 1)) {
/*  44 */       map1[i++] = c;
/*     */     }
/*  46 */     for (c = '0'; c <= '9'; c = (char)(c + 1)) {
/*  47 */       map1[i++] = c;
/*     */     }
/*  49 */     map1[i++] = '+';
/*  50 */     map1[i++] = '/';
/*     */   }
/*     */ 
/*     */   
/*  54 */   private static final byte[] map2 = new byte[128];
/*     */   
/*     */   static {
/*  57 */     for (i = 0; i < map2.length; i++) {
/*  58 */       map2[i] = -1;
/*     */     }
/*  60 */     for (i = 0; i < 64; i++) {
/*  61 */       map2[map1[i]] = (byte)i;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeString(String s) {
/*  72 */     return new String(encode(s.getBytes()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeLines(byte[] in) {
/*  83 */     return encodeLines(in, 0, in.length, 76, systemLineSeparator);
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
/*     */   public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator) {
/*  98 */     int blockLen = lineLen * 3 / 4;
/*  99 */     if (blockLen <= 0) {
/* 100 */       throw new IllegalArgumentException();
/*     */     }
/* 102 */     int lines = (iLen + blockLen - 1) / blockLen;
/* 103 */     int bufLen = (iLen + 2) / 3 * 4 + lines * lineSeparator.length();
/* 104 */     StringBuilder buf = new StringBuilder(bufLen);
/* 105 */     int ip = 0;
/* 106 */     while (ip < iLen) {
/* 107 */       int l = Math.min(iLen - ip, blockLen);
/* 108 */       buf.append(encode(in, iOff + ip, l));
/* 109 */       buf.append(lineSeparator);
/* 110 */       ip += l;
/*     */     } 
/* 112 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char[] encode(byte[] in) {
/* 122 */     return encode(in, 0, in.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char[] encode(byte[] in, int iLen) {
/* 133 */     return encode(in, 0, iLen);
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
/*     */   public static char[] encode(byte[] in, int iOff, int iLen) {
/* 145 */     int oDataLen = (iLen * 4 + 2) / 3;
/* 146 */     int oLen = (iLen + 2) / 3 * 4;
/* 147 */     char[] out = new char[oLen];
/* 148 */     int ip = iOff;
/* 149 */     int iEnd = iOff + iLen;
/* 150 */     int op = 0;
/* 151 */     while (ip < iEnd) {
/* 152 */       int i0 = in[ip++] & 0xFF;
/* 153 */       int i1 = (ip < iEnd) ? (in[ip++] & 0xFF) : 0;
/* 154 */       int i2 = (ip < iEnd) ? (in[ip++] & 0xFF) : 0;
/* 155 */       int o0 = i0 >>> 2;
/* 156 */       int o1 = (i0 & 0x3) << 4 | i1 >>> 4;
/* 157 */       int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
/* 158 */       int o3 = i2 & 0x3F;
/* 159 */       out[op++] = map1[o0];
/* 160 */       out[op++] = map1[o1];
/* 161 */       out[op] = (op < oDataLen) ? map1[o2] : '=';
/* 162 */       op++;
/* 163 */       out[op] = (op < oDataLen) ? map1[o3] : '=';
/* 164 */       op++;
/*     */     } 
/* 166 */     return out;
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
/*     */   public static String decodeString(String s) {
/* 178 */     return new String(decode(s));
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
/*     */   public static byte[] decodeLines(String s) {
/* 191 */     char[] buf = new char[s.length()];
/* 192 */     int p = 0;
/* 193 */     for (int ip = 0; ip < s.length(); ip++) {
/* 194 */       char c = s.charAt(ip);
/* 195 */       if (c != ' ' && c != '\r' && c != '\n' && c != '\t') {
/* 196 */         buf[p++] = c;
/*     */       }
/*     */     } 
/* 199 */     return decode(buf, 0, p);
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
/*     */   public static byte[] decode(String s) {
/* 211 */     return decode(s.toCharArray());
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
/*     */   public static byte[] decode(char[] in) {
/* 223 */     return decode(in, 0, in.length);
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
/*     */   public static byte[] decode(char[] in, int iOff, int iLen) {
/* 237 */     if (iLen % 4 != 0) {
/* 238 */       throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
/*     */     }
/*     */     
/* 241 */     while (iLen > 0 && in[iOff + iLen - 1] == '=') {
/* 242 */       iLen--;
/*     */     }
/* 244 */     int oLen = iLen * 3 / 4;
/* 245 */     byte[] out = new byte[oLen];
/* 246 */     int ip = iOff;
/* 247 */     int iEnd = iOff + iLen;
/* 248 */     int op = 0;
/* 249 */     while (ip < iEnd) {
/* 250 */       int i0 = in[ip++];
/* 251 */       int i1 = in[ip++];
/* 252 */       int i2 = (ip < iEnd) ? in[ip++] : 65;
/* 253 */       int i3 = (ip < iEnd) ? in[ip++] : 65;
/* 254 */       if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
/* 255 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 257 */       int b0 = map2[i0];
/* 258 */       int b1 = map2[i1];
/* 259 */       int b2 = map2[i2];
/* 260 */       int b3 = map2[i3];
/* 261 */       if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
/* 262 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 264 */       int o0 = b0 << 2 | b1 >>> 4;
/* 265 */       int o1 = (b1 & 0xF) << 4 | b2 >>> 2;
/* 266 */       int o2 = (b2 & 0x3) << 6 | b3;
/* 267 */       out[op++] = (byte)o0;
/* 268 */       if (op < oLen) {
/* 269 */         out[op++] = (byte)o1;
/*     */       }
/* 271 */       if (op < oLen) {
/* 272 */         out[op++] = (byte)o2;
/*     */       }
/*     */     } 
/* 275 */     return out;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\external\biz\base64Coder\Base64Coder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.LongStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class TagStringReader
/*     */ {
/*     */   private static final int MAX_DEPTH = 512;
/*     */   private static final int HEX_RADIX = 16;
/*     */   private static final int BINARY_RADIX = 2;
/*     */   private static final int DECIMAL_RADIX = 10;
/*  37 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*  38 */   private static final int[] EMPTY_INT_ARRAY = new int[0];
/*  39 */   private static final long[] EMPTY_LONG_ARRAY = new long[0];
/*     */   
/*     */   private final CharBuffer buffer;
/*     */   private boolean acceptLegacy;
/*     */   private boolean acceptHeterogeneousLists;
/*     */   private int depth;
/*     */   
/*     */   TagStringReader(CharBuffer buffer) {
/*  47 */     this.buffer = buffer;
/*     */   }
/*     */   
/*     */   public CompoundBinaryTag compound() throws StringTagParseException {
/*  51 */     this.buffer.expect('{');
/*  52 */     if (this.buffer.takeIf('}')) {
/*  53 */       return CompoundBinaryTag.empty();
/*     */     }
/*     */     
/*  56 */     CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
/*  57 */     while (this.buffer.hasMore()) {
/*  58 */       builder.put(key(), tag());
/*  59 */       if (separatorOrCompleteWith('}')) {
/*  60 */         return builder.build();
/*     */       }
/*     */     } 
/*  63 */     throw this.buffer.makeError("Unterminated compound tag!");
/*     */   }
/*     */ 
/*     */   
/*     */   public ListBinaryTag list() throws StringTagParseException {
/*  68 */     ListBinaryTag.Builder<BinaryTag> builder = this.acceptHeterogeneousLists ? ListBinaryTag.heterogeneousListBinaryTag() : ListBinaryTag.builder();
/*  69 */     this.buffer.expect('[');
/*  70 */     boolean prefixedIndex = (this.acceptLegacy && this.buffer.peek() == '0' && this.buffer.peek(1) == ':');
/*  71 */     if (!prefixedIndex && this.buffer.takeIf(']')) {
/*  72 */       return ListBinaryTag.empty();
/*     */     }
/*  74 */     while (this.buffer.hasMore()) {
/*  75 */       if (prefixedIndex) {
/*  76 */         this.buffer.takeUntil(':');
/*     */       }
/*     */       
/*  79 */       BinaryTag next = tag();
/*     */       
/*  81 */       builder.add(next);
/*  82 */       if (separatorOrCompleteWith(']')) {
/*  83 */         return builder.build();
/*     */       }
/*     */     } 
/*  86 */     throw this.buffer.makeError("Reached end of file without end of list tag!");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryTag array(char elementType) throws StringTagParseException {
/*  97 */     this.buffer.expect('[')
/*  98 */       .expect(elementType)
/*  99 */       .expect(';');
/*     */     
/* 101 */     elementType = Character.toLowerCase(elementType);
/* 102 */     if (elementType == 'b')
/* 103 */       return ByteArrayBinaryTag.byteArrayBinaryTag(byteArray()); 
/* 104 */     if (elementType == 'i')
/* 105 */       return IntArrayBinaryTag.intArrayBinaryTag(intArray()); 
/* 106 */     if (elementType == 'l') {
/* 107 */       return LongArrayBinaryTag.longArrayBinaryTag(longArray());
/*     */     }
/* 109 */     throw this.buffer.makeError("Type " + elementType + " is not a valid element type in an array!");
/*     */   }
/*     */ 
/*     */   
/*     */   private byte[] byteArray() throws StringTagParseException {
/* 114 */     if (this.buffer.takeIf(']')) {
/* 115 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 118 */     List<Byte> bytes = new ArrayList<>();
/* 119 */     while (this.buffer.hasMore()) {
/* 120 */       CharSequence value = this.buffer.skipWhitespace().takeUntil('b');
/*     */       try {
/* 122 */         bytes.add(Byte.valueOf(value.toString()));
/* 123 */       } catch (NumberFormatException ex) {
/* 124 */         throw this.buffer.makeError("All elements of a byte array must be bytes!");
/*     */       } 
/*     */       
/* 127 */       if (separatorOrCompleteWith(']')) {
/* 128 */         byte[] result = new byte[bytes.size()];
/* 129 */         for (int i = 0; i < bytes.size(); i++) {
/* 130 */           result[i] = ((Byte)bytes.get(i)).byteValue();
/*     */         }
/* 132 */         return result;
/*     */       } 
/*     */     } 
/* 135 */     throw this.buffer.makeError("Reached end of document without array close");
/*     */   }
/*     */   
/*     */   private int[] intArray() throws StringTagParseException {
/* 139 */     if (this.buffer.takeIf(']')) {
/* 140 */       return EMPTY_INT_ARRAY;
/*     */     }
/*     */     
/* 143 */     IntStream.Builder builder = IntStream.builder();
/* 144 */     while (this.buffer.hasMore()) {
/* 145 */       BinaryTag value = tag();
/* 146 */       if (!(value instanceof IntBinaryTag)) {
/* 147 */         throw this.buffer.makeError("All elements of an int array must be ints!");
/*     */       }
/* 149 */       builder.add(((IntBinaryTag)value).intValue());
/* 150 */       if (separatorOrCompleteWith(']')) {
/* 151 */         return builder.build().toArray();
/*     */       }
/*     */     } 
/* 154 */     throw this.buffer.makeError("Reached end of document without array close");
/*     */   }
/*     */   
/*     */   private long[] longArray() throws StringTagParseException {
/* 158 */     if (this.buffer.takeIf(']')) {
/* 159 */       return EMPTY_LONG_ARRAY;
/*     */     }
/*     */     
/* 162 */     LongStream.Builder longs = LongStream.builder();
/* 163 */     while (this.buffer.hasMore()) {
/* 164 */       CharSequence value = this.buffer.skipWhitespace().takeUntil('l');
/*     */       try {
/* 166 */         longs.add(Long.parseLong(value.toString()));
/* 167 */       } catch (NumberFormatException ex) {
/* 168 */         throw this.buffer.makeError("All elements of a long array must be longs!");
/*     */       } 
/*     */       
/* 171 */       if (separatorOrCompleteWith(']')) {
/* 172 */         return longs.build().toArray();
/*     */       }
/*     */     } 
/* 175 */     throw this.buffer.makeError("Reached end of document without array close");
/*     */   }
/*     */   
/*     */   public String key() throws StringTagParseException {
/* 179 */     this.buffer.skipWhitespace();
/* 180 */     char starChar = this.buffer.peek();
/*     */     try {
/* 182 */       if (starChar == '\'' || starChar == '"') {
/* 183 */         return unescape(this.buffer.takeUntil(this.buffer.take()).toString());
/*     */       }
/*     */       
/* 186 */       StringBuilder builder = new StringBuilder();
/* 187 */       while (this.buffer.hasMore()) {
/* 188 */         char peek = this.buffer.peek();
/* 189 */         if (!Tokens.id(peek)) {
/* 190 */           if (this.acceptLegacy) {
/*     */             
/* 192 */             if (peek == '\\') {
/* 193 */               this.buffer.take(); continue;
/*     */             } 
/* 195 */             if (peek != ':') {
/* 196 */               builder.append(this.buffer.take());
/*     */               continue;
/*     */             } 
/*     */           } 
/*     */           break;
/*     */         } 
/* 202 */         builder.append(this.buffer.take());
/*     */       } 
/* 204 */       return builder.toString();
/*     */     } finally {
/* 206 */       this.buffer.expect(':');
/*     */     } 
/*     */   }
/*     */   
/*     */   public BinaryTag tag() throws StringTagParseException {
/* 211 */     if (this.depth++ > 512)
/* 212 */       throw this.buffer.makeError("Exceeded maximum allowed depth of 512 when reading tag");  try {
/*     */       CompoundBinaryTag compoundBinaryTag;
/*     */       ListBinaryTag listBinaryTag;
/* 215 */       char startToken = this.buffer.skipWhitespace().peek();
/* 216 */       switch (startToken) {
/*     */         case '{':
/* 218 */           return compound();
/*     */ 
/*     */         
/*     */         case '[':
/* 222 */           if (this.buffer.hasMore(2) && this.buffer.peek(2) == ';') {
/* 223 */             return array(this.buffer.peek(1));
/*     */           }
/* 225 */           return list();
/*     */ 
/*     */         
/*     */         case '"':
/*     */         case '\'':
/* 230 */           this.buffer.advance();
/* 231 */           return StringBinaryTag.stringBinaryTag(unescape(this.buffer.takeUntil(startToken).toString()));
/*     */       } 
/* 233 */       return scalar();
/*     */     } finally {
/*     */       
/* 236 */       this.depth--;
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
/*     */   private BinaryTag scalar() throws StringTagParseException {
/* 248 */     StringBuilder builder = new StringBuilder();
/* 249 */     while (this.buffer.hasMore()) {
/* 250 */       char current = this.buffer.peek();
/* 251 */       if (current == '\\') {
/* 252 */         this.buffer.advance();
/* 253 */         current = this.buffer.take();
/* 254 */       } else if (Tokens.id(current)) {
/* 255 */         this.buffer.advance();
/*     */       } else {
/*     */         break;
/*     */       } 
/* 259 */       builder.append(current);
/*     */     } 
/* 261 */     if (builder.length() == 0) {
/* 262 */       throw this.buffer.makeError("Expected a value but got nothing");
/*     */     }
/* 264 */     String original = builder.toString();
/*     */ 
/*     */ 
/*     */     
/* 268 */     int radix = extractRadix(builder, original);
/*     */ 
/*     */     
/* 271 */     char last = builder.charAt(builder.length() - 1);
/* 272 */     boolean hasSignToken = false;
/* 273 */     boolean signed = (radix != 16);
/* 274 */     if (builder.length() > 2) {
/* 275 */       char signChar = builder.charAt(builder.length() - 2);
/* 276 */       if (signChar == 's' || signChar == 'u') {
/* 277 */         hasSignToken = true;
/* 278 */         signed = (signChar == 's');
/* 279 */         builder.deleteCharAt(builder.length() - 2);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 284 */     boolean hasTypeToken = false;
/* 285 */     char typeToken = 'i';
/* 286 */     if (Tokens.numericType(last) && (hasSignToken || radix != 16)) {
/* 287 */       hasTypeToken = true;
/* 288 */       typeToken = Character.toLowerCase(last);
/* 289 */       builder.deleteCharAt(builder.length() - 1);
/*     */     } 
/*     */     
/* 292 */     if (!signed && (typeToken == 'f' || typeToken == 'd')) {
/* 293 */       throw this.buffer.makeError("Cannot create unsigned floating point numbers");
/*     */     }
/*     */     
/* 296 */     String strippedString = builder.toString().replace("_", "");
/* 297 */     if (hasTypeToken) {
/*     */       try {
/* 299 */         NumberBinaryTag tag = parseNumberTag(strippedString, typeToken, radix, signed);
/* 300 */         if (tag != null) {
/* 301 */           return tag;
/*     */         }
/* 303 */       } catch (NumberFormatException numberFormatException) {}
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/* 308 */         return IntBinaryTag.intBinaryTag(parseInt(strippedString, radix, signed));
/* 309 */       } catch (NumberFormatException ex) {
/* 310 */         if (strippedString.indexOf('.') != -1) {
/*     */           try {
/* 312 */             return DoubleBinaryTag.doubleBinaryTag(Double.parseDouble(strippedString));
/* 313 */           } catch (NumberFormatException numberFormatException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 320 */     if (original.equalsIgnoreCase("true"))
/* 321 */       return ByteBinaryTag.ONE; 
/* 322 */     if (original.equalsIgnoreCase("false")) {
/* 323 */       return ByteBinaryTag.ZERO;
/*     */     }
/* 325 */     return StringBinaryTag.stringBinaryTag(original);
/*     */   }
/*     */   
/*     */   private int extractRadix(StringBuilder builder, String original) {
/* 329 */     int radix, radixPrefixOffset = 0;
/*     */     
/* 331 */     char first = builder.charAt(0);
/* 332 */     if (first == '+' || first == '-') {
/* 333 */       radixPrefixOffset = 1;
/*     */     }
/* 335 */     if (original.startsWith("0b", radixPrefixOffset) || original.startsWith("0B", radixPrefixOffset)) {
/* 336 */       radix = 2;
/* 337 */     } else if (original.startsWith("0x", radixPrefixOffset) || original.startsWith("0X", radixPrefixOffset)) {
/* 338 */       radix = 16;
/*     */     } else {
/* 340 */       radix = 10;
/*     */     } 
/* 342 */     if (radix != 10) {
/* 343 */       builder.delete(radixPrefixOffset, 2 + radixPrefixOffset);
/*     */     }
/* 345 */     return radix; } @Nullable
/*     */   private NumberBinaryTag parseNumberTag(String s, char typeToken, int radix, boolean signed) {
/*     */     float floatValue;
/*     */     double doubleValue;
/* 349 */     switch (typeToken) {
/*     */       case 'b':
/* 351 */         return ByteBinaryTag.byteBinaryTag(parseByte(s, radix, signed));
/*     */       case 's':
/* 353 */         return ShortBinaryTag.shortBinaryTag(parseShort(s, radix, signed));
/*     */       case 'i':
/* 355 */         return IntBinaryTag.intBinaryTag(parseInt(s, radix, signed));
/*     */       case 'l':
/* 357 */         return LongBinaryTag.longBinaryTag(parseLong(s, radix, signed));
/*     */       case 'f':
/* 359 */         floatValue = Float.parseFloat(s);
/* 360 */         if (Float.isFinite(floatValue)) {
/* 361 */           return FloatBinaryTag.floatBinaryTag(floatValue);
/*     */         }
/*     */         break;
/*     */       case 'd':
/* 365 */         doubleValue = Double.parseDouble(s);
/* 366 */         if (Double.isFinite(doubleValue)) {
/* 367 */           return DoubleBinaryTag.doubleBinaryTag(doubleValue);
/*     */         }
/*     */         break;
/*     */     } 
/* 371 */     return null;
/*     */   }
/*     */   
/*     */   private byte parseByte(String s, int radix, boolean signed) {
/* 375 */     if (signed) {
/* 376 */       return Byte.parseByte(s, radix);
/*     */     }
/* 378 */     int parsedInt = Integer.parseInt(s, radix);
/* 379 */     if (parsedInt >> 8 == 0) {
/* 380 */       return (byte)parsedInt;
/*     */     }
/* 382 */     throw new NumberFormatException();
/*     */   }
/*     */   
/*     */   private short parseShort(String s, int radix, boolean signed) {
/* 386 */     if (signed) {
/* 387 */       return Short.parseShort(s, radix);
/*     */     }
/* 389 */     int parsedInt = Integer.parseInt(s, radix);
/* 390 */     if (parsedInt >> 16 == 0) {
/* 391 */       return (short)parsedInt;
/*     */     }
/* 393 */     throw new NumberFormatException();
/*     */   }
/*     */   
/*     */   private int parseInt(String s, int radix, boolean signed) {
/* 397 */     return signed ? Integer.parseInt(s, radix) : Integer.parseUnsignedInt(s, radix);
/*     */   }
/*     */   
/*     */   private long parseLong(String s, int radix, boolean signed) {
/* 401 */     return signed ? Long.parseLong(s, radix) : Long.parseUnsignedLong(s, radix);
/*     */   }
/*     */   
/*     */   private boolean separatorOrCompleteWith(char endCharacter) throws StringTagParseException {
/* 405 */     if (this.buffer.takeIf(endCharacter)) {
/* 406 */       return true;
/*     */     }
/* 408 */     this.buffer.expect(',');
/* 409 */     return this.buffer.takeIf(endCharacter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String unescape(String withEscapes) {
/* 419 */     int escapeIdx = withEscapes.indexOf('\\');
/* 420 */     if (escapeIdx == -1) {
/* 421 */       return withEscapes;
/*     */     }
/* 423 */     int lastEscape = 0;
/* 424 */     StringBuilder output = new StringBuilder(withEscapes.length());
/*     */     do {
/* 426 */       output.append(withEscapes, lastEscape, escapeIdx);
/* 427 */       lastEscape = escapeIdx + 1;
/* 428 */     } while ((escapeIdx = withEscapes.indexOf('\\', lastEscape + 1)) != -1);
/* 429 */     output.append(withEscapes.substring(lastEscape));
/* 430 */     return output.toString();
/*     */   }
/*     */   
/*     */   public void legacy(boolean acceptLegacy) {
/* 434 */     this.acceptLegacy = acceptLegacy;
/*     */   }
/*     */   
/*     */   public void heterogeneousLists(boolean acceptHeterogeneousLists) {
/* 438 */     this.acceptHeterogeneousLists = acceptHeterogeneousLists;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\TagStringReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class TagStringWriter
/*     */   implements AutoCloseable
/*     */ {
/*     */   private final Appendable out;
/*     */   private final String indent;
/*     */   private int level;
/*     */   private boolean needsSeparator;
/*     */   private boolean legacy;
/*     */   private boolean heterogeneousLists;
/*     */   
/*     */   TagStringWriter(Appendable out, String indent) {
/*  47 */     this.out = out;
/*  48 */     this.indent = indent;
/*     */   }
/*     */   
/*     */   public TagStringWriter legacy(boolean legacy) {
/*  52 */     this.legacy = legacy;
/*  53 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter heterogeneousLists(boolean emitHeterogeneousLists) {
/*  57 */     this.heterogeneousLists = emitHeterogeneousLists;
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TagStringWriter writeTag(BinaryTag tag) throws IOException {
/*  64 */     BinaryTagType<?> type = tag.type();
/*  65 */     if (type == BinaryTagTypes.COMPOUND)
/*  66 */       return writeCompound((CompoundBinaryTag)tag); 
/*  67 */     if (type == BinaryTagTypes.LIST)
/*  68 */       return writeList((ListBinaryTag)tag); 
/*  69 */     if (type == BinaryTagTypes.BYTE_ARRAY)
/*  70 */       return writeByteArray((ByteArrayBinaryTag)tag); 
/*  71 */     if (type == BinaryTagTypes.INT_ARRAY)
/*  72 */       return writeIntArray((IntArrayBinaryTag)tag); 
/*  73 */     if (type == BinaryTagTypes.LONG_ARRAY)
/*  74 */       return writeLongArray((LongArrayBinaryTag)tag); 
/*  75 */     if (type == BinaryTagTypes.STRING)
/*  76 */       return value(((StringBinaryTag)tag).value(), false); 
/*  77 */     if (type == BinaryTagTypes.BYTE)
/*  78 */       return value(Byte.toString(((ByteBinaryTag)tag).value()), 'b'); 
/*  79 */     if (type == BinaryTagTypes.SHORT)
/*  80 */       return value(Short.toString(((ShortBinaryTag)tag).value()), 's'); 
/*  81 */     if (type == BinaryTagTypes.INT)
/*  82 */       return value(Integer.toString(((IntBinaryTag)tag).value()), 'i'); 
/*  83 */     if (type == BinaryTagTypes.LONG)
/*  84 */       return value(Long.toString(((LongBinaryTag)tag).value()), Character.toUpperCase('l')); 
/*  85 */     if (type == BinaryTagTypes.FLOAT)
/*  86 */       return value(Float.toString(((FloatBinaryTag)tag).value()), 'f'); 
/*  87 */     if (type == BinaryTagTypes.DOUBLE) {
/*  88 */       return value(Double.toString(((DoubleBinaryTag)tag).value()), 'd');
/*     */     }
/*  90 */     throw new IOException("Unknown tag type: " + type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private TagStringWriter writeCompound(CompoundBinaryTag tag) throws IOException {
/*  96 */     beginCompound();
/*  97 */     for (Map.Entry<String, ? extends BinaryTag> entry : (Iterable<Map.Entry<String, ? extends BinaryTag>>)tag) {
/*  98 */       key(entry.getKey());
/*  99 */       writeTag(entry.getValue());
/*     */     } 
/* 101 */     endCompound();
/* 102 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter writeList(ListBinaryTag rawTag) throws IOException {
/* 106 */     ListBinaryTag tag = this.heterogeneousLists ? rawTag.unwrapHeterogeneity() : rawTag.wrapHeterogeneity();
/* 107 */     beginList();
/* 108 */     int idx = 0;
/* 109 */     boolean lineBreaks = (prettyPrinting() && breakListElement(tag.elementType()));
/* 110 */     for (BinaryTag el : tag) {
/* 111 */       printAndResetSeparator(!lineBreaks);
/* 112 */       if (lineBreaks) {
/* 113 */         newlineIndent();
/*     */       }
/* 115 */       if (this.legacy) {
/* 116 */         this.out.append(String.valueOf(idx++));
/* 117 */         appendSeparator(':');
/*     */       } 
/*     */       
/* 120 */       writeTag(el);
/*     */     } 
/* 122 */     endList(lineBreaks);
/* 123 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter writeByteArray(ByteArrayBinaryTag tag) throws IOException {
/* 127 */     if (this.legacy) {
/* 128 */       throw new IOException("Legacy Mojangson only supports integer arrays!");
/*     */     }
/* 130 */     beginArray('b');
/*     */     
/* 132 */     char byteArrayType = Character.toUpperCase('b');
/* 133 */     byte[] value = ByteArrayBinaryTagImpl.value(tag);
/* 134 */     for (int i = 0, length = value.length; i < length; i++) {
/* 135 */       printAndResetSeparator(true);
/* 136 */       value(Byte.toString(value[i]), byteArrayType);
/*     */     } 
/* 138 */     endArray();
/* 139 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter writeIntArray(IntArrayBinaryTag tag) throws IOException {
/* 143 */     if (this.legacy) {
/* 144 */       beginList();
/*     */     } else {
/* 146 */       beginArray('i');
/*     */     } 
/*     */     
/* 149 */     int[] value = IntArrayBinaryTagImpl.value(tag);
/* 150 */     for (int i = 0, length = value.length; i < length; i++) {
/* 151 */       printAndResetSeparator(true);
/* 152 */       value(Integer.toString(value[i]), 'i');
/*     */     } 
/* 154 */     endArray();
/* 155 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter writeLongArray(LongArrayBinaryTag tag) throws IOException {
/* 159 */     if (this.legacy) {
/* 160 */       throw new IOException("Legacy Mojangson only supports integer arrays!");
/*     */     }
/* 162 */     beginArray('l');
/*     */     
/* 164 */     long[] value = LongArrayBinaryTagImpl.value(tag);
/* 165 */     for (int i = 0, length = value.length; i < length; i++) {
/* 166 */       printAndResetSeparator(true);
/* 167 */       value(Long.toString(value[i]), 'l');
/*     */     } 
/* 169 */     endArray();
/* 170 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TagStringWriter beginCompound() throws IOException {
/* 176 */     printAndResetSeparator(false);
/* 177 */     this.level++;
/* 178 */     this.out.append('{');
/* 179 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter endCompound() throws IOException {
/* 183 */     this.level--;
/* 184 */     newlineIndent();
/* 185 */     this.out.append('}');
/* 186 */     this.needsSeparator = true;
/* 187 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter key(String key) throws IOException {
/* 191 */     printAndResetSeparator(false);
/* 192 */     newlineIndent();
/* 193 */     writeMaybeQuoted(key, false);
/* 194 */     appendSeparator(':');
/* 195 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter value(String value, char valueType) throws IOException {
/* 199 */     if (valueType == '\000') {
/* 200 */       writeMaybeQuoted(value, true);
/*     */     } else {
/* 202 */       this.out.append(value);
/* 203 */       if (valueType != 'i') {
/* 204 */         this.out.append(valueType);
/*     */       }
/*     */     } 
/* 207 */     this.needsSeparator = true;
/* 208 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter beginList() throws IOException {
/* 212 */     printAndResetSeparator(false);
/* 213 */     this.level++;
/* 214 */     this.out.append('[');
/* 215 */     return this;
/*     */   }
/*     */   
/*     */   public TagStringWriter endList(boolean lineBreak) throws IOException {
/* 219 */     this.level--;
/* 220 */     if (lineBreak) {
/* 221 */       newlineIndent();
/*     */     }
/* 223 */     this.out.append(']');
/* 224 */     this.needsSeparator = true;
/* 225 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter beginArray(char type) throws IOException {
/* 229 */     (beginList()).out
/* 230 */       .append(Character.toUpperCase(type))
/* 231 */       .append(';');
/*     */     
/* 233 */     if (prettyPrinting()) {
/* 234 */       this.out.append(' ');
/*     */     }
/*     */     
/* 237 */     return this;
/*     */   }
/*     */   
/*     */   private TagStringWriter endArray() throws IOException {
/* 241 */     return endList(false);
/*     */   }
/*     */   
/*     */   private void writeMaybeQuoted(String content, boolean requireQuotes) throws IOException {
/* 245 */     if (!requireQuotes) {
/* 246 */       for (int i = 0; i < content.length(); i++) {
/* 247 */         if (!Tokens.id(content.charAt(i))) {
/* 248 */           requireQuotes = true;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 253 */     if (requireQuotes) {
/* 254 */       this.out.append('"');
/* 255 */       this.out.append(escape(content, '"'));
/* 256 */       this.out.append('"');
/*     */     } else {
/* 258 */       this.out.append(content);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String escape(String content, char quoteChar) {
/* 263 */     StringBuilder output = new StringBuilder(content.length());
/* 264 */     for (int i = 0; i < content.length(); i++) {
/* 265 */       char c = content.charAt(i);
/* 266 */       if (c == quoteChar || c == '\\') {
/* 267 */         output.append('\\');
/*     */       }
/* 269 */       output.append(c);
/*     */     } 
/* 271 */     return output.toString();
/*     */   }
/*     */   
/*     */   private void printAndResetSeparator(boolean pad) throws IOException {
/* 275 */     if (this.needsSeparator) {
/* 276 */       this.out.append(',');
/* 277 */       if (pad && prettyPrinting()) {
/* 278 */         this.out.append(' ');
/*     */       }
/* 280 */       this.needsSeparator = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean breakListElement(BinaryTagType<?> type) {
/* 288 */     return (type == BinaryTagTypes.COMPOUND || type == BinaryTagTypes.LIST || type == BinaryTagTypes.BYTE_ARRAY || type == BinaryTagTypes.INT_ARRAY || type == BinaryTagTypes.LONG_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean prettyPrinting() {
/* 296 */     return (this.indent.length() > 0);
/*     */   }
/*     */   
/*     */   private void newlineIndent() throws IOException {
/* 300 */     if (prettyPrinting()) {
/* 301 */       this.out.append(Tokens.NEWLINE);
/* 302 */       for (int i = 0; i < this.level; i++) {
/* 303 */         this.out.append(this.indent);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private Appendable appendSeparator(char separatorChar) throws IOException {
/* 309 */     this.out.append(separatorChar);
/* 310 */     if (prettyPrinting()) {
/* 311 */       this.out.append(' ');
/*     */     }
/* 313 */     return this.out;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 318 */     if (this.level != 0) {
/* 319 */       throw new IllegalStateException("Document finished with unbalanced start and end objects");
/*     */     }
/* 321 */     if (this.out instanceof Writer)
/* 322 */       ((Writer)this.out).flush(); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\TagStringWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
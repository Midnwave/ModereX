/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Arrays;
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
/*     */ public final class TagStringIO
/*     */ {
/*  38 */   private static final TagStringIO INSTANCE = new TagStringIO(new Builder());
/*     */   
/*     */   private final boolean acceptLegacy;
/*     */   private final boolean emitLegacy;
/*     */   private final boolean acceptHeterogeneousLists;
/*     */   private final boolean emitHeterogeneousLists;
/*     */   private final String indent;
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static TagStringIO get() {
/*  49 */     return tagStringIO();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static TagStringIO tagStringIO() {
/*  59 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Builder builder() {
/*  69 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TagStringIO(@NotNull Builder builder) {
/*  79 */     this.acceptLegacy = builder.acceptLegacy;
/*  80 */     this.emitLegacy = builder.emitLegacy;
/*  81 */     this.acceptHeterogeneousLists = builder.acceptHeterogeneousLists;
/*  82 */     this.emitHeterogeneousLists = builder.emitHeterogeneousLists;
/*  83 */     this.indent = builder.indent;
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
/*     */   @NotNull
/*     */   public CompoundBinaryTag asCompound(@NotNull String input) throws IOException {
/*  98 */     Objects.requireNonNull(input, "input");
/*     */     try {
/* 100 */       CharBuffer buffer = new CharBuffer(input);
/* 101 */       TagStringReader parser = new TagStringReader(buffer);
/* 102 */       parser.legacy(this.acceptLegacy);
/* 103 */       parser.heterogeneousLists(this.acceptHeterogeneousLists);
/* 104 */       CompoundBinaryTag tag = parser.compound();
/* 105 */       if (buffer.skipWhitespace().hasMore()) {
/* 106 */         throw new IOException("Document had trailing content after first CompoundTag");
/*     */       }
/* 108 */       return tag;
/* 109 */     } catch (StringTagParseException ex) {
/* 110 */       throw new IOException(ex);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public BinaryTag asTag(@NotNull String input) throws IOException {
/* 126 */     Objects.requireNonNull(input, "input");
/*     */     try {
/* 128 */       CharBuffer buffer = new CharBuffer(input);
/* 129 */       TagStringReader parser = new TagStringReader(buffer);
/* 130 */       parser.legacy(this.acceptLegacy);
/* 131 */       parser.heterogeneousLists(this.acceptHeterogeneousLists);
/* 132 */       BinaryTag tag = parser.tag();
/* 133 */       if (buffer.skipWhitespace().hasMore()) {
/* 134 */         throw new IOException("Document had trailing content after first Tag");
/*     */       }
/* 136 */       return tag;
/* 137 */     } catch (StringTagParseException ex) {
/* 138 */       throw new IOException(ex);
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
/*     */   
/*     */   @NotNull
/*     */   public CompoundBinaryTag asCompound(@NotNull String input, @NotNull Appendable remainder) throws IOException {
/* 152 */     Objects.requireNonNull(input, "input");
/* 153 */     Objects.requireNonNull(remainder, "remainder");
/*     */     try {
/* 155 */       CharBuffer buffer = new CharBuffer(input);
/* 156 */       TagStringReader parser = new TagStringReader(buffer);
/* 157 */       parser.legacy(this.acceptLegacy);
/* 158 */       parser.heterogeneousLists(this.acceptHeterogeneousLists);
/* 159 */       CompoundBinaryTag tag = parser.compound();
/* 160 */       remainder.append(buffer.takeRest());
/* 161 */       return tag;
/* 162 */     } catch (StringTagParseException ex) {
/* 163 */       throw new IOException(ex);
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
/*     */   
/*     */   @NotNull
/*     */   public BinaryTag asTag(@NotNull String input, @NotNull Appendable remainder) throws IOException {
/* 177 */     Objects.requireNonNull(input, "input");
/* 178 */     Objects.requireNonNull(remainder, "remainder");
/*     */     try {
/* 180 */       CharBuffer buffer = new CharBuffer(input);
/* 181 */       TagStringReader parser = new TagStringReader(buffer);
/* 182 */       parser.legacy(this.acceptLegacy);
/* 183 */       parser.heterogeneousLists(this.acceptHeterogeneousLists);
/* 184 */       BinaryTag tag = parser.tag();
/* 185 */       remainder.append(buffer.takeRest());
/* 186 */       return tag;
/* 187 */     } catch (StringTagParseException ex) {
/* 188 */       throw new IOException(ex);
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
/*     */   @NotNull
/*     */   public String asString(@NotNull CompoundBinaryTag input) throws IOException {
/* 201 */     return asString(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String asString(@NotNull BinaryTag input) throws IOException {
/* 213 */     Objects.requireNonNull(input, "input");
/* 214 */     StringBuilder sb = new StringBuilder();
/* 215 */     TagStringWriter emit = new TagStringWriter(sb, this.indent); 
/* 216 */     try { emit.legacy(this.emitLegacy);
/* 217 */       emit.heterogeneousLists(this.emitHeterogeneousLists);
/* 218 */       emit.writeTag(input);
/* 219 */       emit.close(); } catch (Throwable throwable) { try { emit.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/* 220 */      return sb.toString();
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
/*     */   public void toWriter(@NotNull CompoundBinaryTag input, @NotNull Writer dest) throws IOException {
/* 234 */     toWriter(input, dest);
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
/*     */   public void toWriter(@NotNull BinaryTag input, @NotNull Writer dest) throws IOException {
/* 248 */     Objects.requireNonNull(input, "input");
/* 249 */     Objects.requireNonNull(dest, "dest");
/* 250 */     TagStringWriter emit = new TagStringWriter(dest, this.indent); try {
/* 251 */       emit.legacy(this.emitLegacy);
/* 252 */       emit.heterogeneousLists(this.emitHeterogeneousLists);
/* 253 */       emit.writeTag(input);
/* 254 */       emit.close();
/*     */     } catch (Throwable throwable) {
/*     */       try {
/*     */         emit.close();
/*     */       } catch (Throwable throwable1) {
/*     */         throwable.addSuppressed(throwable1);
/*     */       } 
/*     */       throw throwable;
/*     */     } 
/*     */   }
/*     */   public static class Builder { private boolean acceptLegacy = true; private boolean emitLegacy = false;
/*     */     private boolean acceptHeterogeneousLists = false;
/*     */     private boolean emitHeterogeneousLists = false;
/* 267 */     private String indent = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder indent(int spaces) {
/* 282 */       if (spaces == 0) {
/* 283 */         this.indent = "";
/* 284 */       } else if ((!this.indent.isEmpty() && this.indent.charAt(0) != ' ') || spaces != this.indent.length()) {
/* 285 */         char[] indent = new char[spaces];
/* 286 */         Arrays.fill(indent, ' ');
/* 287 */         this.indent = String.copyValueOf(indent);
/*     */       } 
/* 289 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder indentTab(int tabs) {
/* 302 */       if (tabs == 0) {
/* 303 */         this.indent = "";
/* 304 */       } else if ((!this.indent.isEmpty() && this.indent.charAt(0) != '\t') || tabs != this.indent.length()) {
/* 305 */         char[] indent = new char[tabs];
/* 306 */         Arrays.fill(indent, '\t');
/* 307 */         this.indent = String.copyValueOf(indent);
/*     */       } 
/* 309 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder acceptLegacy(boolean legacy) {
/* 325 */       this.acceptLegacy = legacy;
/* 326 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder emitLegacy(boolean legacy) {
/* 337 */       this.emitLegacy = legacy;
/* 338 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder acceptHeterogeneousLists(boolean heterogeneous) {
/* 352 */       this.acceptHeterogeneousLists = heterogeneous;
/* 353 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Builder emitHeterogeneousLists(boolean heterogeneous) {
/* 367 */       this.emitHeterogeneousLists = heterogeneous;
/* 368 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public TagStringIO build() {
/* 378 */       return new TagStringIO(this);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\TagStringIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
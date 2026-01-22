/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ final class CharacterAndFormatImpl
/*     */   implements CharacterAndFormat
/*     */ {
/*     */   private final char character;
/*     */   private final TextFormat format;
/*     */   private final boolean caseInsensitive;
/*     */   
/*     */   CharacterAndFormatImpl(char character, @NotNull TextFormat format, boolean caseInsensitive) {
/*  42 */     this.character = character;
/*  43 */     this.format = Objects.<TextFormat>requireNonNull(format, "format");
/*  44 */     this.caseInsensitive = caseInsensitive;
/*     */   }
/*     */ 
/*     */   
/*     */   public char character() {
/*  49 */     return this.character;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextFormat format() {
/*  54 */     return this.format;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean caseInsensitive() {
/*  59 */     return this.caseInsensitive;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  64 */     if (this == other) return true; 
/*  65 */     if (!(other instanceof CharacterAndFormatImpl)) return false; 
/*  66 */     CharacterAndFormatImpl that = (CharacterAndFormatImpl)other;
/*  67 */     return (this.character == that.character && this.format
/*  68 */       .equals(that.format) && this.caseInsensitive == that.caseInsensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  74 */     int result = this.character;
/*  75 */     result = 31 * result + this.format.hashCode();
/*  76 */     result = 31 * result + Boolean.hashCode(this.caseInsensitive);
/*  77 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/*  82 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static final class Defaults {
/*  86 */     static final List<CharacterAndFormat> DEFAULTS = createDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static List<CharacterAndFormat> createDefaults() {
/*  93 */       List<CharacterAndFormat> formats = new ArrayList<>(22);
/*     */       
/*  95 */       formats.add(CharacterAndFormat.BLACK);
/*  96 */       formats.add(CharacterAndFormat.DARK_BLUE);
/*  97 */       formats.add(CharacterAndFormat.DARK_GREEN);
/*  98 */       formats.add(CharacterAndFormat.DARK_AQUA);
/*  99 */       formats.add(CharacterAndFormat.DARK_RED);
/* 100 */       formats.add(CharacterAndFormat.DARK_PURPLE);
/* 101 */       formats.add(CharacterAndFormat.GOLD);
/* 102 */       formats.add(CharacterAndFormat.GRAY);
/* 103 */       formats.add(CharacterAndFormat.DARK_GRAY);
/* 104 */       formats.add(CharacterAndFormat.BLUE);
/* 105 */       formats.add(CharacterAndFormat.GREEN);
/* 106 */       formats.add(CharacterAndFormat.AQUA);
/* 107 */       formats.add(CharacterAndFormat.RED);
/* 108 */       formats.add(CharacterAndFormat.LIGHT_PURPLE);
/* 109 */       formats.add(CharacterAndFormat.YELLOW);
/* 110 */       formats.add(CharacterAndFormat.WHITE);
/*     */       
/* 112 */       formats.add(CharacterAndFormat.OBFUSCATED);
/* 113 */       formats.add(CharacterAndFormat.BOLD);
/* 114 */       formats.add(CharacterAndFormat.STRIKETHROUGH);
/* 115 */       formats.add(CharacterAndFormat.UNDERLINED);
/* 116 */       formats.add(CharacterAndFormat.ITALIC);
/*     */       
/* 118 */       formats.add(CharacterAndFormat.RESET);
/*     */       
/* 120 */       return Collections.unmodifiableList(formats);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\legacy\CharacterAndFormatImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextFormat;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NonExtendable
/*     */ public interface CharacterAndFormat
/*     */   extends Examinable
/*     */ {
/*  49 */   public static final CharacterAndFormat BLACK = characterAndFormat('0', (TextFormat)NamedTextColor.BLACK, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   public static final CharacterAndFormat DARK_BLUE = characterAndFormat('1', (TextFormat)NamedTextColor.DARK_BLUE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final CharacterAndFormat DARK_GREEN = characterAndFormat('2', (TextFormat)NamedTextColor.DARK_GREEN, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final CharacterAndFormat DARK_AQUA = characterAndFormat('3', (TextFormat)NamedTextColor.DARK_AQUA, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final CharacterAndFormat DARK_RED = characterAndFormat('4', (TextFormat)NamedTextColor.DARK_RED, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final CharacterAndFormat DARK_PURPLE = characterAndFormat('5', (TextFormat)NamedTextColor.DARK_PURPLE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final CharacterAndFormat GOLD = characterAndFormat('6', (TextFormat)NamedTextColor.GOLD, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   public static final CharacterAndFormat GRAY = characterAndFormat('7', (TextFormat)NamedTextColor.GRAY, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final CharacterAndFormat DARK_GRAY = characterAndFormat('8', (TextFormat)NamedTextColor.DARK_GRAY, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   public static final CharacterAndFormat BLUE = characterAndFormat('9', (TextFormat)NamedTextColor.BLUE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   public static final CharacterAndFormat GREEN = characterAndFormat('a', (TextFormat)NamedTextColor.GREEN, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final CharacterAndFormat AQUA = characterAndFormat('b', (TextFormat)NamedTextColor.AQUA, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   public static final CharacterAndFormat RED = characterAndFormat('c', (TextFormat)NamedTextColor.RED, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 127 */   public static final CharacterAndFormat LIGHT_PURPLE = characterAndFormat('d', (TextFormat)NamedTextColor.LIGHT_PURPLE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 133 */   public static final CharacterAndFormat YELLOW = characterAndFormat('e', (TextFormat)NamedTextColor.YELLOW, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 139 */   public static final CharacterAndFormat WHITE = characterAndFormat('f', (TextFormat)NamedTextColor.WHITE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   public static final CharacterAndFormat OBFUSCATED = characterAndFormat('k', (TextFormat)TextDecoration.OBFUSCATED, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   public static final CharacterAndFormat BOLD = characterAndFormat('l', (TextFormat)TextDecoration.BOLD, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 158 */   public static final CharacterAndFormat STRIKETHROUGH = characterAndFormat('m', (TextFormat)TextDecoration.STRIKETHROUGH, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 164 */   public static final CharacterAndFormat UNDERLINED = characterAndFormat('n', (TextFormat)TextDecoration.UNDERLINED, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 170 */   public static final CharacterAndFormat ITALIC = characterAndFormat('o', (TextFormat)TextDecoration.ITALIC, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 177 */   public static final CharacterAndFormat RESET = characterAndFormat('r', Reset.INSTANCE, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static CharacterAndFormat characterAndFormat(char character, @NotNull TextFormat format) {
/* 188 */     return characterAndFormat(character, format, false);
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
/*     */   static CharacterAndFormat characterAndFormat(char character, @NotNull TextFormat format, boolean caseInsensitive) {
/* 201 */     return new CharacterAndFormatImpl(character, format, caseInsensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static List<CharacterAndFormat> defaults() {
/* 212 */     return CharacterAndFormatImpl.Defaults.DEFAULTS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   char character();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   TextFormat format();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean caseInsensitive();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 241 */     return Stream.of(new ExaminableProperty[] {
/* 242 */           ExaminableProperty.of("character", character()), 
/* 243 */           ExaminableProperty.of("format", format()), 
/* 244 */           ExaminableProperty.of("caseInsensitive", caseInsensitive())
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\legacy\CharacterAndFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.HSVLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ public final class NamedTextColor
/*     */   implements TextColor
/*     */ {
/*     */   private static final int BLACK_VALUE = 0;
/*     */   private static final int DARK_BLUE_VALUE = 170;
/*     */   private static final int DARK_GREEN_VALUE = 43520;
/*     */   private static final int DARK_AQUA_VALUE = 43690;
/*     */   private static final int DARK_RED_VALUE = 11141120;
/*     */   private static final int DARK_PURPLE_VALUE = 11141290;
/*     */   private static final int GOLD_VALUE = 16755200;
/*     */   private static final int GRAY_VALUE = 11184810;
/*     */   private static final int DARK_GRAY_VALUE = 5592405;
/*     */   private static final int BLUE_VALUE = 5592575;
/*     */   private static final int GREEN_VALUE = 5635925;
/*     */   private static final int AQUA_VALUE = 5636095;
/*     */   private static final int RED_VALUE = 16733525;
/*     */   private static final int LIGHT_PURPLE_VALUE = 16733695;
/*     */   private static final int YELLOW_VALUE = 16777045;
/*     */   private static final int WHITE_VALUE = 16777215;
/*  65 */   public static final NamedTextColor BLACK = new NamedTextColor("black", 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final NamedTextColor DARK_BLUE = new NamedTextColor("dark_blue", 170);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final NamedTextColor DARK_GREEN = new NamedTextColor("dark_green", 43520);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   public static final NamedTextColor DARK_AQUA = new NamedTextColor("dark_aqua", 43690);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final NamedTextColor DARK_RED = new NamedTextColor("dark_red", 11141120);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   public static final NamedTextColor DARK_PURPLE = new NamedTextColor("dark_purple", 11141290);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   public static final NamedTextColor GOLD = new NamedTextColor("gold", 16755200);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   public static final NamedTextColor GRAY = new NamedTextColor("gray", 11184810);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 113 */   public static final NamedTextColor DARK_GRAY = new NamedTextColor("dark_gray", 5592405);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 119 */   public static final NamedTextColor BLUE = new NamedTextColor("blue", 5592575);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 125 */   public static final NamedTextColor GREEN = new NamedTextColor("green", 5635925);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 131 */   public static final NamedTextColor AQUA = new NamedTextColor("aqua", 5636095);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 137 */   public static final NamedTextColor RED = new NamedTextColor("red", 16733525);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 143 */   public static final NamedTextColor LIGHT_PURPLE = new NamedTextColor("light_purple", 16733695);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 149 */   public static final NamedTextColor YELLOW = new NamedTextColor("yellow", 16777045);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   public static final NamedTextColor WHITE = new NamedTextColor("white", 16777215);
/*     */   
/* 157 */   private static final List<NamedTextColor> VALUES = Collections.unmodifiableList(Arrays.asList(new NamedTextColor[] { BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE })); public static final Index<String, NamedTextColor> NAMES;
/*     */   private final String name;
/*     */   private final int value;
/*     */   private final HSVLike hsv;
/*     */   
/*     */   static {
/* 163 */     NAMES = Index.create(constant -> constant.name, VALUES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static NamedTextColor namedColor(int value) {
/* 173 */     switch (value) { case 0:
/* 174 */         return BLACK;
/* 175 */       case 170: return DARK_BLUE;
/* 176 */       case 43520: return DARK_GREEN;
/* 177 */       case 43690: return DARK_AQUA;
/* 178 */       case 11141120: return DARK_RED;
/* 179 */       case 11141290: return DARK_PURPLE;
/* 180 */       case 16755200: return GOLD;
/* 181 */       case 11184810: return GRAY;
/* 182 */       case 5592405: return DARK_GRAY;
/* 183 */       case 5592575: return BLUE;
/* 184 */       case 5635925: return GREEN;
/* 185 */       case 5636095: return AQUA;
/* 186 */       case 16733525: return RED;
/* 187 */       case 16733695: return LIGHT_PURPLE;
/* 188 */       case 16777045: return YELLOW;
/* 189 */       case 16777215: return WHITE; }
/* 190 */      return null;
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
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   @Nullable
/*     */   public static NamedTextColor ofExact(int value) {
/* 205 */     switch (value) { case 0:
/* 206 */         return BLACK;
/* 207 */       case 170: return DARK_BLUE;
/* 208 */       case 43520: return DARK_GREEN;
/* 209 */       case 43690: return DARK_AQUA;
/* 210 */       case 11141120: return DARK_RED;
/* 211 */       case 11141290: return DARK_PURPLE;
/* 212 */       case 16755200: return GOLD;
/* 213 */       case 11184810: return GRAY;
/* 214 */       case 5592405: return DARK_GRAY;
/* 215 */       case 5592575: return BLUE;
/* 216 */       case 5635925: return GREEN;
/* 217 */       case 5636095: return AQUA;
/* 218 */       case 16733525: return RED;
/* 219 */       case 16733695: return LIGHT_PURPLE;
/* 220 */       case 16777045: return YELLOW;
/* 221 */       case 16777215: return WHITE; }
/* 222 */      return null;
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
/*     */   public static NamedTextColor nearestTo(@NotNull TextColor any) {
/* 234 */     if (any instanceof NamedTextColor) {
/* 235 */       return (NamedTextColor)any;
/*     */     }
/*     */     
/* 238 */     return TextColor.<NamedTextColor>nearestColorTo(VALUES, any);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NamedTextColor(String name, int value) {
/* 246 */     this.name = name;
/* 247 */     this.value = value;
/* 248 */     this.hsv = HSVLike.fromRGB(red(), green(), blue());
/*     */   }
/*     */ 
/*     */   
/*     */   public int value() {
/* 253 */     return this.value;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public HSVLike asHSV() {
/* 258 */     return this.hsv;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/* 263 */     return this.name;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 268 */     return Stream.concat(
/* 269 */         Stream.of(ExaminableProperty.of("name", this.name)), super
/* 270 */         .examinableProperties());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\NamedTextColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
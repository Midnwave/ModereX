/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.HSVLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface TextColor
/*     */   extends Comparable<TextColor>, Examinable, RGBLike, StyleBuilderApplicable, TextFormat
/*     */ {
/*     */   public static final char HEX_CHARACTER = '#';
/*     */   public static final String HEX_PREFIX = "#";
/*     */   
/*     */   @NotNull
/*     */   static TextColor color(int value) {
/*  71 */     int truncatedValue = value & 0xFFFFFF;
/*  72 */     NamedTextColor named = NamedTextColor.namedColor(truncatedValue);
/*  73 */     return (named != null) ? named : new TextColorImpl(truncatedValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TextColor color(@NotNull RGBLike rgb) {
/*  84 */     if (rgb instanceof TextColor) return (TextColor)rgb; 
/*  85 */     return color(rgb.red(), rgb.green(), rgb.blue());
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
/*     */   static TextColor color(@NotNull HSVLike hsv) {
/*  97 */     float s = hsv.s();
/*  98 */     float v = hsv.v();
/*  99 */     if (s == 0.0F)
/*     */     {
/* 101 */       return color(v, v, v);
/*     */     }
/*     */     
/* 104 */     float h = hsv.h() * 6.0F;
/* 105 */     int i = (int)Math.floor(h);
/* 106 */     float f = h - i;
/* 107 */     float p = v * (1.0F - s);
/* 108 */     float q = v * (1.0F - s * f);
/* 109 */     float t = v * (1.0F - s * (1.0F - f));
/*     */     
/* 111 */     if (i == 0)
/* 112 */       return color(v, t, p); 
/* 113 */     if (i == 1)
/* 114 */       return color(q, v, p); 
/* 115 */     if (i == 2)
/* 116 */       return color(p, v, t); 
/* 117 */     if (i == 3)
/* 118 */       return color(p, q, v); 
/* 119 */     if (i == 4) {
/* 120 */       return color(t, p, v);
/*     */     }
/* 122 */     return color(v, p, q);
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
/*     */   @NotNull
/*     */   static TextColor color(int r, int g, int b) {
/* 136 */     return color((r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
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
/*     */   static TextColor color(float r, float g, float b) {
/* 149 */     return color((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static TextColor fromHexString(@NotNull String string) {
/* 160 */     if (string.startsWith("#")) {
/*     */       try {
/* 162 */         int hex = Integer.parseInt(string.substring(1), 16);
/* 163 */         return color(hex);
/* 164 */       } catch (NumberFormatException e) {
/* 165 */         return null;
/*     */       } 
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static TextColor fromCSSHexString(@NotNull String string) {
/* 179 */     if (string.startsWith("#")) {
/* 180 */       int hex; String hexString = string.substring(1);
/* 181 */       if (hexString.length() != 3 && hexString.length() != 6) {
/* 182 */         return null;
/*     */       }
/*     */       
/*     */       try {
/* 186 */         hex = Integer.parseInt(hexString, 16);
/* 187 */       } catch (NumberFormatException e) {
/* 188 */         return null;
/*     */       } 
/*     */       
/* 191 */       if (hexString.length() == 6) {
/* 192 */         return color(hex);
/*     */       }
/* 194 */       int red = (hex & 0xF00) >> 8 | (hex & 0xF00) >> 4;
/* 195 */       int green = (hex & 0xF0) >> 4 | hex & 0xF0;
/* 196 */       int blue = (hex & 0xF) << 4 | hex & 0xF;
/* 197 */       return color(red, green, blue);
/*     */     } 
/*     */     
/* 200 */     return null;
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
/*     */   @NotNull
/*     */   default String asHexString() {
/* 218 */     StringBuilder result = new StringBuilder();
/* 219 */     result.append("#");
/* 220 */     String hex = Integer.toHexString(value());
/* 221 */     for (int i = 0; i < 6 - hex.length(); i++) {
/* 222 */       result.append('0');
/*     */     }
/* 224 */     result.append(hex);
/* 225 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int red() {
/* 236 */     return value() >> 16 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int green() {
/* 247 */     return value() >> 8 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int blue() {
/* 258 */     return value() & 0xFF;
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
/*     */   static TextColor lerp(float t, @NotNull RGBLike a, @NotNull RGBLike b) {
/* 273 */     float clampedT = Math.min(1.0F, Math.max(0.0F, t));
/* 274 */     int ar = a.red();
/* 275 */     int br = b.red();
/* 276 */     int ag = a.green();
/* 277 */     int bg = b.green();
/* 278 */     int ab = a.blue();
/* 279 */     int bb = b.blue();
/* 280 */     return color(
/* 281 */         Math.round(ar + clampedT * (br - ar)), 
/* 282 */         Math.round(ag + clampedT * (bg - ag)), 
/* 283 */         Math.round(ab + clampedT * (bb - ab)));
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
/*     */   @NotNull
/*     */   static <C extends TextColor> C nearestColorTo(@NotNull List<C> values, @NotNull TextColor any) {
/* 297 */     Objects.requireNonNull(any, "color");
/*     */     
/* 299 */     float matchedDistance = Float.MAX_VALUE;
/* 300 */     TextColor textColor = (TextColor)values.get(0);
/* 301 */     for (int i = 0, length = values.size(); i < length; i++) {
/* 302 */       TextColor textColor1 = (TextColor)values.get(i);
/* 303 */       float distance = TextColorImpl.distance(any.asHSV(), textColor1.asHSV());
/* 304 */       if (distance < matchedDistance) {
/* 305 */         textColor = textColor1;
/* 306 */         matchedDistance = distance;
/*     */       } 
/* 308 */       if (distance == 0.0F) {
/*     */         break;
/*     */       }
/*     */     } 
/* 312 */     return (C)textColor;
/*     */   }
/*     */ 
/*     */   
/*     */   default void styleApply(Style.Builder style) {
/* 317 */     style.color(this);
/*     */   }
/*     */ 
/*     */   
/*     */   default int compareTo(TextColor that) {
/* 322 */     return Integer.compare(value(), that.value());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 327 */     return Stream.of(ExaminableProperty.of("value", asHexString()));
/*     */   }
/*     */   
/*     */   int value();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\TextColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.intellij.lang.annotations.Pattern;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface ShadowColor
/*     */   extends StyleBuilderApplicable, ARGBLike
/*     */ {
/*     */   @NotNull
/*     */   static ShadowColor lerp(float t, @NotNull ARGBLike a, @NotNull ARGBLike b) {
/*  55 */     float clampedT = Math.min(1.0F, Math.max(0.0F, t));
/*  56 */     int ar = a.red();
/*  57 */     int br = b.red();
/*  58 */     int ag = a.green();
/*  59 */     int bg = b.green();
/*  60 */     int ab = a.blue();
/*  61 */     int bb = b.blue();
/*  62 */     int aa = a.alpha();
/*  63 */     int ba = b.alpha();
/*  64 */     return shadowColor(
/*  65 */         Math.round(ar + clampedT * (br - ar)), 
/*  66 */         Math.round(ag + clampedT * (bg - ag)), 
/*  67 */         Math.round(ab + clampedT * (bb - ab)), 
/*  68 */         Math.round(aa + clampedT * (ba - aa)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static ShadowColor none() {
/*  79 */     return ShadowColorImpl.NONE;
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
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static ShadowColor shadowColor(int argb) {
/*  93 */     if (argb == 0) return none();
/*     */     
/*  95 */     return new ShadowColorImpl(argb);
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
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static ShadowColor shadowColor(int red, int green, int blue, int alpha) {
/* 115 */     int value = (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     if (value == 0) return none(); 
/* 122 */     return new ShadowColorImpl(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static ShadowColor shadowColor(@NotNull RGBLike rgb, int alpha) {
/* 135 */     return shadowColor(rgb.red(), rgb.green(), rgb.blue(), alpha);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static ShadowColor shadowColor(@NotNull ARGBLike argb) {
/* 146 */     if (argb instanceof ShadowColor) {
/* 147 */       return (ShadowColor)argb;
/*     */     }
/*     */     
/* 150 */     return shadowColor(argb.red(), argb.green(), argb.blue(), argb.alpha());
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
/*     */   @Contract(pure = true)
/*     */   @Nullable
/*     */   static ShadowColor fromHexString(@Pattern("#[0-9a-fA-F]{8}") @NotNull String hex) {
/* 164 */     if (hex.length() != 9) return null; 
/* 165 */     if (!hex.startsWith("#")) return null;
/*     */     
/*     */     try {
/* 168 */       int r = Integer.parseInt(hex.substring(1, 3), 16);
/* 169 */       int g = Integer.parseInt(hex.substring(3, 5), 16);
/* 170 */       int b = Integer.parseInt(hex.substring(5, 7), 16);
/* 171 */       int a = Integer.parseInt(hex.substring(7, 9), 16);
/* 172 */       return new ShadowColorImpl(a << 24 | r << 16 | g << 8 | b);
/* 173 */     } catch (NumberFormatException ignored) {
/* 174 */       return null;
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
/*     */   default String asHexString() {
/* 187 */     int argb = value();
/* 188 */     int a = argb >> 24 & 0xFF;
/* 189 */     int r = argb >> 16 & 0xFF;
/* 190 */     int g = argb >> 8 & 0xFF;
/* 191 */     int b = argb & 0xFF;
/* 192 */     return String.format("#%02X%02X%02X%02X", new Object[] { Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b), Integer.valueOf(a) });
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
/* 203 */     return value() >> 16 & 0xFF;
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
/* 214 */     return value() >> 8 & 0xFF;
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
/* 225 */     return value() & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int alpha() {
/* 236 */     return value() >> 24 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int value();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void styleApply(Style.Builder style) {
/* 249 */     style.shadowColor(this);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\ShadowColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
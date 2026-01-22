/*     */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Translator
/*     */ {
/*     */   @Nullable
/*     */   static Locale parseLocale(@NotNull String string) {
/*  61 */     String[] segments = string.split("_", 3);
/*  62 */     int length = segments.length;
/*  63 */     if (length == 1)
/*  64 */       return new Locale(string); 
/*  65 */     if (length == 2)
/*  66 */       return new Locale(segments[0], segments[1]); 
/*  67 */     if (length == 3) {
/*  68 */       return new Locale(segments[0], segments[1], segments[2]);
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Key name();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default TriState hasAnyTranslations() {
/*  90 */     return TriState.NOT_SET;
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
/*     */   default boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
/* 103 */     Component translatedValue = translate(Component.translatable(Objects.<String>requireNonNull(key, "key")), Objects.<Locale>requireNonNull(locale, "locale"));
/* 104 */     if (translatedValue != null) return true; 
/* 105 */     return (translate(key, locale) != null);
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
/*     */   @Nullable
/*     */   MessageFormat translate(@NotNull String paramString, @NotNull Locale paramLocale);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   default Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
/* 140 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\Translator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.nio.file.Path;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface TranslationStore<T>
/*     */   extends Translator
/*     */ {
/*     */   @NotNull
/*     */   static TranslationStore<Component> component(@NotNull Key name) {
/*  61 */     return new ComponentTranslationStore(Objects.<Key>requireNonNull(name, "name"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static StringBased<MessageFormat> messageFormat(@NotNull Key name) {
/*  72 */     return new MessageFormatTranslationStore(Objects.<Key>requireNonNull(name, "name"));
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
/*     */   boolean contains(@NotNull String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean contains(@NotNull String paramString, @NotNull Locale paramLocale);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 119 */     return super.canTranslate(key, locale);
/*     */   }
/*     */   
/*     */   void defaultLocale(@NotNull Locale paramLocale);
/*     */   
/*     */   void register(@NotNull String paramString, @NotNull Locale paramLocale, T paramT);
/*     */   
/*     */   void registerAll(@NotNull Locale paramLocale, @NotNull Map<String, T> paramMap);
/*     */   
/*     */   void registerAll(@NotNull Locale paramLocale, @NotNull Set<String> paramSet, Function<String, T> paramFunction);
/*     */   
/*     */   void unregister(@NotNull String paramString);
/*     */   
/*     */   public static interface StringBased<T> extends TranslationStore<T> {
/*     */     void registerAll(@NotNull Locale param1Locale, @NotNull Path param1Path, boolean param1Boolean);
/*     */     
/*     */     void registerAll(@NotNull Locale param1Locale, @NotNull ResourceBundle param1ResourceBundle, boolean param1Boolean);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\TranslationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
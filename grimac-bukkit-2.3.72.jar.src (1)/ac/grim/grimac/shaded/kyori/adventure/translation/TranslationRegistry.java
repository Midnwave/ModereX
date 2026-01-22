/*     */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.PropertyResourceBundle;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public interface TranslationRegistry
/*     */   extends Translator, TranslationStore.StringBased<MessageFormat>
/*     */ {
/*     */   @Deprecated
/*  64 */   public static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   static TranslationRegistry create(Key name) {
/*  76 */     return new MessageFormatTranslationStore(Objects.<Key>requireNonNull(name, "name"));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   default void registerAll(@NotNull Locale locale, @NotNull Map<String, MessageFormat> formats) {
/* 159 */     Objects.requireNonNull(formats); registerAll(locale, formats.keySet(), formats::get);
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
/*     */   @Deprecated
/*     */   default void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
/*     */     
/* 176 */     try { BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); 
/* 177 */       try { registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
/* 178 */         if (reader != null) reader.close();  } catch (Throwable throwable) { if (reader != null) try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException iOException) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
/* 204 */     registerAll(locale, bundle.keySet(), key -> {
/*     */           String format = bundle.getString(key);
/*     */           return new MessageFormat(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
/*     */         });
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
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, MessageFormat> function) {
/* 229 */     IllegalArgumentException firstError = null;
/* 230 */     int errorCount = 0;
/* 231 */     for (String key : keys) {
/*     */       try {
/* 233 */         register(key, locale, function.apply(key));
/* 234 */       } catch (IllegalArgumentException e) {
/* 235 */         if (firstError == null) {
/* 236 */           firstError = e;
/*     */         }
/* 238 */         errorCount++;
/*     */       } 
/*     */     } 
/* 241 */     if (firstError != null) {
/* 242 */       if (errorCount == 1)
/* 243 */         throw firstError; 
/* 244 */       if (errorCount > 1)
/* 245 */         throw new IllegalArgumentException(String.format("Invalid key (and %d more)", new Object[] { Integer.valueOf(errorCount - 1) }), firstError); 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   boolean contains(@NotNull String paramString);
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   MessageFormat translate(@NotNull String paramString, @NotNull Locale paramLocale);
/*     */   
/*     */   @Deprecated
/*     */   void defaultLocale(@NotNull Locale paramLocale);
/*     */   
/*     */   @Deprecated
/*     */   void register(@NotNull String paramString, @NotNull Locale paramLocale, @NotNull MessageFormat paramMessageFormat);
/*     */   
/*     */   @Deprecated
/*     */   void unregister(@NotNull String paramString);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\TranslationRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
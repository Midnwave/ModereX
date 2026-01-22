/*     */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.PropertyResourceBundle;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.Pattern;
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
/*     */ public abstract class AbstractTranslationStore<T>
/*     */   implements Examinable, TranslationStore<T>
/*     */ {
/*     */   @NotNull
/*     */   private final Key name;
/*  59 */   private final Map<String, Translation> translations = new ConcurrentHashMap<>(); @NotNull
/*  60 */   private volatile Locale defaultLocale = Locale.US;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractTranslationStore(@NotNull Key name) {
/*  69 */     this.name = Objects.<Key>requireNonNull(name, "name");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected T translationValue(@NotNull String key, @NotNull Locale locale) {
/*  81 */     Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
/*  82 */     if (translation == null) return null; 
/*  83 */     return translation.translate(Objects.<Locale>requireNonNull(locale, "locale"));
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean contains(@NotNull String key) {
/*  88 */     return this.translations.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean contains(@NotNull String key, @NotNull Locale locale) {
/*  93 */     Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
/*  94 */     if (translation == null) return false; 
/*  95 */     return (translation.translations.get(Objects.requireNonNull(locale, "locale")) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
/* 100 */     Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
/* 101 */     if (translation == null) return false; 
/* 102 */     return (translation.translate(Objects.<Locale>requireNonNull(locale, "locale")) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void defaultLocale(@NotNull Locale locale) {
/* 107 */     this.defaultLocale = Objects.<Locale>requireNonNull(locale, "locale");
/*     */   }
/*     */ 
/*     */   
/*     */   public final void register(@NotNull String key, @NotNull Locale locale, @NotNull T translation) {
/* 112 */     ((Translation)this.translations.computeIfAbsent(key, x$0 -> new Translation(x$0))).register(locale, translation);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void registerAll(@NotNull Locale locale, @NotNull Map<String, T> translations) {
/* 117 */     Objects.requireNonNull(translations); registerAll(locale, translations.keySet(), translations::get);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, T> function) {
/* 122 */     IllegalArgumentException firstError = null;
/* 123 */     int errorCount = 0;
/* 124 */     for (String key : keys) {
/*     */       try {
/* 126 */         register(key, locale, function.apply(key));
/* 127 */       } catch (IllegalArgumentException e) {
/* 128 */         if (firstError == null) {
/* 129 */           firstError = e;
/*     */         }
/* 131 */         errorCount++;
/*     */       } 
/*     */     } 
/* 134 */     if (firstError != null) {
/* 135 */       if (errorCount == 1)
/* 136 */         throw firstError; 
/* 137 */       if (errorCount > 1) {
/* 138 */         throw new IllegalArgumentException(String.format("Invalid key (and %d more)", new Object[] { Integer.valueOf(errorCount - 1) }), firstError);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final void unregister(@NotNull String key) {
/* 145 */     this.translations.remove(key);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final Key name() {
/* 150 */     return this.name;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final TriState hasAnyTranslations() {
/* 155 */     return TriState.byBoolean(!this.translations.isEmpty());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final Stream<? extends ExaminableProperty> examinableProperties() {
/* 160 */     return Stream.of(ExaminableProperty.of("translations", this.translations));
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/* 165 */     if (this == other) return true; 
/* 166 */     if (!(other instanceof AbstractTranslationStore)) return false;
/*     */     
/* 168 */     AbstractTranslationStore<?> that = (AbstractTranslationStore)other;
/*     */     
/* 170 */     return (this.name.equals(that.name) && this.translations
/* 171 */       .equals(that.translations) && this.defaultLocale
/* 172 */       .equals(that.defaultLocale));
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 177 */     return Objects.hash(new Object[] { this.name, this.translations, this.defaultLocale });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final String toString() {
/* 182 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   private final class Translation implements Examinable {
/*     */     private final String key;
/*     */     private final Map<Locale, T> translations;
/*     */     
/*     */     private Translation(String key) {
/* 190 */       this.key = Objects.<String>requireNonNull(key, "key");
/* 191 */       this.translations = new ConcurrentHashMap<>();
/*     */     }
/*     */     @Nullable
/*     */     private T translate(@NotNull Locale locale) {
/* 195 */       T format = this.translations.get(Objects.requireNonNull(locale, "locale"));
/* 196 */       if (format == null) {
/* 197 */         format = this.translations.get(new Locale(locale.getLanguage()));
/* 198 */         if (format == null) {
/* 199 */           format = this.translations.get(AbstractTranslationStore.this.defaultLocale);
/* 200 */           if (format == null) {
/* 201 */             format = this.translations.get(TranslationLocales.global());
/*     */           }
/*     */         } 
/*     */       } 
/* 205 */       return format;
/*     */     }
/*     */     
/*     */     private void register(@NotNull Locale locale, @NotNull T translation) {
/* 209 */       if (this.translations.putIfAbsent(Objects.<Locale>requireNonNull(locale, "locale"), Objects.requireNonNull(translation, "translation")) != null) {
/* 210 */         throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", new Object[] { this.key, locale }));
/*     */       }
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 216 */       return Stream.of(new ExaminableProperty[] {
/* 217 */             ExaminableProperty.of("key", this.key), 
/* 218 */             ExaminableProperty.of("translations", this.translations)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 224 */       if (this == other) return true; 
/* 225 */       if (!(other instanceof Translation)) return false; 
/* 226 */       Translation that = (Translation)other;
/* 227 */       return (this.key.equals(that.key) && this.translations
/* 228 */         .equals(that.translations));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 233 */       return Objects.hash(new Object[] { this.key, this.translations });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 238 */       return Internals.toString(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class StringBased<T>
/*     */     extends AbstractTranslationStore<T>
/*     */     implements TranslationStore.StringBased<T>
/*     */   {
/* 252 */     private static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected StringBased(@NotNull Key name) {
/* 261 */       super(name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     protected abstract T parse(@NotNull String param1String, @NotNull Locale param1Locale);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
/*     */       
/* 276 */       try { BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); 
/* 277 */         try { registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
/* 278 */           if (reader != null) reader.close();  } catch (Throwable throwable) { if (reader != null) try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
/* 285 */       registerAll(locale, bundle.keySet(), key -> {
/*     */             String format = bundle.getString(key);
/*     */             return (Function)parse(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\AbstractTranslationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.shaded.kyori.adventure.translation;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.TranslatableComponentRenderer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Collections;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ final class GlobalTranslatorImpl
/*     */   implements GlobalTranslator
/*     */ {
/*  44 */   private static final Key NAME = Key.key("adventure", "global");
/*  45 */   static final GlobalTranslatorImpl INSTANCE = new GlobalTranslatorImpl();
/*  46 */   final TranslatableComponentRenderer<Locale> renderer = TranslatableComponentRenderer.usingTranslationSource(this);
/*  47 */   private final Set<Translator> sources = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Key name() {
/*  54 */     return NAME;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Iterable<? extends Translator> sources() {
/*  59 */     return Collections.unmodifiableSet(this.sources);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addSource(@NotNull Translator source) {
/*  64 */     Objects.requireNonNull(source, "source");
/*  65 */     if (source == this) throw new IllegalArgumentException("GlobalTranslationSource"); 
/*  66 */     return this.sources.add(source);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeSource(@NotNull Translator source) {
/*  71 */     Objects.requireNonNull(source, "source");
/*  72 */     return this.sources.remove(source);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TriState hasAnyTranslations() {
/*  77 */     if (!this.sources.isEmpty()) {
/*  78 */       return TriState.TRUE;
/*     */     }
/*  80 */     return TriState.FALSE;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
/*  85 */     Objects.requireNonNull(key, "key");
/*  86 */     Objects.requireNonNull(locale, "locale");
/*  87 */     for (Translator source : this.sources) {
/*  88 */       MessageFormat translation = source.translate(key, locale);
/*  89 */       if (translation != null) return translation; 
/*     */     } 
/*  91 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
/*  96 */     Objects.requireNonNull(component, "component");
/*  97 */     Objects.requireNonNull(locale, "locale");
/*  98 */     for (Translator source : this.sources) {
/*  99 */       Component translation = source.translate(component, locale);
/* 100 */       if (translation != null) return translation; 
/*     */     } 
/* 102 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 107 */     return Stream.of(ExaminableProperty.of("sources", this.sources));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\translation\GlobalTranslatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
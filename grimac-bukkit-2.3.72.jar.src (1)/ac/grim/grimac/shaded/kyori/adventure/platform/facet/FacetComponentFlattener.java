/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.GlobalTranslator;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.TranslationRegistry;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.regex.Matcher;
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
/*     */ @Internal
/*     */ public final class FacetComponentFlattener
/*     */ {
/*  47 */   private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <V> ComponentFlattener get(V instance, Collection<? extends Translator<V>> candidates) {
/*  64 */     Translator<V> translator = Facet.<V, Translator<V>>of((Collection)candidates, instance);
/*  65 */     ComponentFlattener.Builder flattenerBuilder = (ComponentFlattener.Builder)ComponentFlattener.basic().toBuilder();
/*  66 */     flattenerBuilder.complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
/*     */           String key = translatable.key();
/*     */           
/*     */           for (ac.grim.grimac.shaded.kyori.adventure.translation.Translator registry : GlobalTranslator.translator().sources()) {
/*     */             if (registry instanceof TranslationRegistry && ((TranslationRegistry)registry).contains(key)) {
/*     */               consumer.accept(GlobalTranslator.render((Component)translatable, Locale.getDefault()));
/*     */               
/*     */               return;
/*     */             } 
/*     */           } 
/*     */           String translated = (translator == null) ? key : translator.valueOrDefault(instance, key);
/*     */           Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
/*     */           List<Component> args = translatable.args();
/*     */           int argPosition = 0;
/*     */           int lastIdx = 0;
/*     */           while (matcher.find()) {
/*     */             if (lastIdx < matcher.start()) {
/*     */               consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
/*     */             }
/*     */             lastIdx = matcher.end();
/*     */             String argIdx = matcher.group(1);
/*     */             if (argIdx != null) {
/*     */               try {
/*     */                 int i = Integer.parseInt(argIdx) - 1;
/*     */                 if (i < args.size()) {
/*     */                   consumer.accept(args.get(i));
/*     */                 }
/*  93 */               } catch (NumberFormatException numberFormatException) {}
/*     */               
/*     */               continue;
/*     */             } 
/*     */             
/*     */             int idx = argPosition++;
/*     */             
/*     */             if (idx < args.size()) {
/*     */               consumer.accept(args.get(idx));
/*     */             }
/*     */           } 
/*     */           
/*     */           if (lastIdx < translated.length()) {
/*     */             consumer.accept(Component.text(translated.substring(lastIdx)));
/*     */           }
/*     */         });
/*     */     
/* 110 */     return (ComponentFlattener)flattenerBuilder.build();
/*     */   }
/*     */   
/*     */   public static interface Translator<V> extends Facet<V> {
/*     */     @NotNull
/*     */     String valueOrDefault(@NotNull V param1V, @NotNull String param1String);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetComponentFlattener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
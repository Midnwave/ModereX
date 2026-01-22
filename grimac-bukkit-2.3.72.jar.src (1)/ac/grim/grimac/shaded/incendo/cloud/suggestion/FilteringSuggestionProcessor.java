/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandInputTokenizer;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.BiPredicate;
/*     */ import java.util.stream.Stream;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class FilteringSuggestionProcessor<C>
/*     */   implements SuggestionProcessor<C>
/*     */ {
/*     */   private final Filter<C> filter;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public FilteringSuggestionProcessor() {
/*  56 */     this(Filter.partialTokenMatches(true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public FilteringSuggestionProcessor(Filter<C> filter) {
/*  66 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Stream<Suggestion> process(CommandPreprocessingContext<C> context, Stream<Suggestion> suggestions) {
/*     */     String input;
/*  75 */     if (context.commandInput().isEmpty(true)) {
/*  76 */       input = "";
/*     */     } else {
/*  78 */       input = context.commandInput().skipWhitespace().remainingInput();
/*     */     } 
/*  80 */     return suggestions.<Suggestion>map(suggestion -> {
/*     */           String filtered = this.filter.filter(context, suggestion.suggestion(), input);
/*     */ 
/*     */ 
/*     */           
/*     */           return (filtered == null) ? null : suggestion.withSuggestion(filtered);
/*  86 */         }).filter(Objects::nonNull);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   @FunctionalInterface
/*     */   public static interface Filter<C>
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     default Filter<C> and(Filter<C> and) {
/* 122 */       return (ctx, suggestion, input) -> {
/*     */           String filtered = filter(ctx, suggestion, input);
/*     */           return (filtered == null) ? null : and.filter(ctx, filtered, input);
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     static <C> Simple<C> startsWith(boolean ignoreCase) {
/* 142 */       BiPredicate<String, String> test = ignoreCase ? ((suggestion, input) -> suggestion.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT))) : String::startsWith;
/* 143 */       return Simple.contextFree(test);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     static <C> Simple<C> contains(boolean ignoreCase) {
/* 157 */       BiPredicate<String, String> test = ignoreCase ? ((suggestion, input) -> suggestion.toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT))) : String::contains;
/* 158 */       return Simple.contextFree(test);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     static <C> Simple<C> partialTokenMatches(boolean ignoreCase) {
/* 171 */       return Simple.contextFree((suggestion, input) -> {
/*     */             List<String> suggestionTokens = (new CommandInputTokenizer(suggestion)).tokenize();
/*     */             List<String> inputTokens = (new CommandInputTokenizer(input)).tokenize();
/*     */             boolean passed = true;
/*     */             for (String inputToken : inputTokens) {
/*     */               if (ignoreCase) {
/*     */                 inputToken = inputToken.toLowerCase(Locale.ROOT);
/*     */               }
/*     */               boolean foundMatch = false;
/*     */               Iterator<String> iterator = suggestionTokens.iterator();
/*     */               while (iterator.hasNext()) {
/*     */                 String suggestionToken = iterator.next();
/*     */                 String suggestionTokenLower = ignoreCase ? suggestionToken.toLowerCase(Locale.ROOT) : suggestionToken;
/*     */                 if (suggestionTokenLower.contains(inputToken)) {
/*     */                   iterator.remove();
/*     */                   foundMatch = true;
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */               if (!foundMatch) {
/*     */                 passed = false;
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */             return passed;
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     static <C> Filter<C> contextFree(BiFunction<String, String, String> function) {
/* 214 */       return (ctx, suggestion, input) -> (String)function.apply(suggestion, input);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     static <C> Simple<C> simple(Simple<C> filter) {
/* 228 */       return filter;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     String filter(CommandPreprocessingContext<C> param1CommandPreprocessingContext, String param1String1, String param1String2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     @FunctionalInterface
/*     */     public static interface Simple<C>
/*     */       extends Filter<C>
/*     */     {
/*     */       @API(status = API.Status.STABLE)
/*     */       boolean test(CommandPreprocessingContext<C> param2CommandPreprocessingContext, String param2String1, String param2String2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       default String filter(CommandPreprocessingContext<C> context, String suggestion, String input) {
/* 264 */         if (test(context, suggestion, input)) {
/* 265 */           return suggestion;
/*     */         }
/* 267 */         return null;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @API(status = API.Status.STABLE)
/*     */       static <C> Simple<C> contextFree(BiPredicate<String, String> test)
/*     */       {
/* 279 */         return (ctx, suggestion, input) -> test.test(suggestion, input); } } } @API(status = API.Status.STABLE) @FunctionalInterface public static interface Simple<C> extends Filter<C> { @API(status = API.Status.STABLE) static <C> Simple<C> contextFree(BiPredicate<String, String> test) { return (ctx, suggestion, input) -> test.test(suggestion, input); }
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     boolean test(CommandPreprocessingContext<C> param1CommandPreprocessingContext, String param1String1, String param1String2);
/*     */     
/*     */     default String filter(CommandPreprocessingContext<C> context, String suggestion, String input) {
/*     */       if (test(context, suggestion, input))
/*     */         return suggestion; 
/*     */       return null;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\FilteringSuggestionProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
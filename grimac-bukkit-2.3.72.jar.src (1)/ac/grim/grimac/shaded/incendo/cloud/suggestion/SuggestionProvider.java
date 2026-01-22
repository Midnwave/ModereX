/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.CompletableFuture;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ @FunctionalInterface
/*     */ public interface SuggestionProvider<C>
/*     */ {
/*     */   static <C> SuggestionProvider<C> noSuggestions() {
/*  69 */     return NoSuggestions.instance();
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
/*     */   static <C> SuggestionProvider<C> blocking(BlockingSuggestionProvider<C> blockingSuggestionProvider) {
/*  83 */     return blockingSuggestionProvider;
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
/*     */   static <C> SuggestionProvider<C> blockingStrings(BlockingSuggestionProvider.Strings<C> blockingStringsSuggestionProvider) {
/*  97 */     return blockingStringsSuggestionProvider;
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
/*     */   static <C> SuggestionProvider<C> suggesting(Suggestion... suggestions) {
/* 110 */     return suggesting(Arrays.asList(suggestions));
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
/*     */   static <C> SuggestionProvider<C> suggestingStrings(String... suggestions) {
/* 123 */     return suggestingStrings(Arrays.asList(suggestions));
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
/*     */   static <C> SuggestionProvider<C> suggesting(Iterable<? extends Suggestion> suggestions) {
/* 136 */     return blocking((ctx, input) -> suggestions);
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
/*     */   static <C> SuggestionProvider<C> suggestingStrings(Iterable<String> suggestions) {
/* 149 */     return blockingStrings((ctx, input) -> suggestions);
/*     */   }
/*     */   
/*     */   CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> paramCommandContext, CommandInput paramCommandInput);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SuggestionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
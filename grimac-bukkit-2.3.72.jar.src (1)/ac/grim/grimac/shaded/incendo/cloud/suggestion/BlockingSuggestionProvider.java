/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.StreamSupport;
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
/*     */ @FunctionalInterface
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface BlockingSuggestionProvider<C>
/*     */   extends SuggestionProvider<C>
/*     */ {
/*     */   Iterable<? extends Suggestion> suggestions(CommandContext<C> paramCommandContext, CommandInput paramCommandInput);
/*     */   
/*     */   default CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> context, CommandInput input) {
/*  67 */     return CompletableFuture.completedFuture(suggestions(context, input));
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
/*     */   @FunctionalInterface
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface Strings<C>
/*     */     extends BlockingSuggestionProvider<C>
/*     */   {
/*     */     Iterable<String> stringSuggestions(CommandContext<C> param1CommandContext, CommandInput param1CommandInput);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default Iterable<Suggestion> suggestions(CommandContext<C> context, CommandInput input) {
/* 106 */       return (Iterable<Suggestion>)StreamSupport.stream(stringSuggestions(context, input).spliterator(), false)
/* 107 */         .map(Suggestion::suggestion)
/* 108 */         .collect(Collectors.toList());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\BlockingSuggestionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
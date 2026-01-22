/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface SuggestionFactory<C, S extends Suggestion>
/*     */ {
/*     */   CompletableFuture<Suggestions<C, S>> suggest(CommandContext<C> paramCommandContext, String paramString);
/*     */   
/*     */   CompletableFuture<Suggestions<C, S>> suggest(C paramC, String paramString);
/*     */   
/*     */   default Suggestions<C, S> suggestImmediately(C sender, String input) {
/*     */     try {
/*  80 */       return suggest(sender, input).join();
/*  81 */     } catch (CompletionException completionException) {
/*  82 */       Throwable cause = completionException.getCause();
/*     */       
/*  84 */       if (cause instanceof RuntimeException) {
/*  85 */         throw (RuntimeException)cause;
/*     */       }
/*  87 */       throw completionException;
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
/*     */   
/*     */   default <S2 extends Suggestion> SuggestionFactory<C, S2> mapped(SuggestionMapper<S2> mapper) {
/* 100 */     return new MappingSuggestionFactory<>(this, mapper);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SuggestionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
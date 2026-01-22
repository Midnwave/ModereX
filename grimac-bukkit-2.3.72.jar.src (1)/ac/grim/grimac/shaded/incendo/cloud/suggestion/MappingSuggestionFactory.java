/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.stream.Collectors;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class MappingSuggestionFactory<C, S extends Suggestion>
/*    */   implements SuggestionFactory<C, S>
/*    */ {
/*    */   private final SuggestionFactory<C, ?> other;
/*    */   private final SuggestionMapper<S> suggestionMapper;
/*    */   
/*    */   MappingSuggestionFactory(SuggestionFactory<C, ?> other, SuggestionMapper<S> suggestionMapper) {
/* 40 */     this.other = other;
/* 41 */     this.suggestionMapper = suggestionMapper;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<Suggestions<C, S>> suggest(CommandContext<C> context, String input) {
/* 49 */     return map((CompletableFuture)this.other.suggest(context, input));
/*    */   }
/*    */ 
/*    */   
/*    */   public CompletableFuture<Suggestions<C, S>> suggest(C sender, String input) {
/* 54 */     return map((CompletableFuture)this.other.suggest(sender, input));
/*    */   }
/*    */ 
/*    */   
/*    */   public <S2 extends Suggestion> SuggestionFactory<C, S2> mapped(SuggestionMapper<S2> mapper) {
/* 59 */     return new MappingSuggestionFactory(this.other, this.suggestionMapper.then(mapper));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private <S1 extends Suggestion> CompletableFuture<Suggestions<C, S>> map(CompletableFuture<Suggestions<C, S1>> future) {
/* 65 */     return future.thenApply(suggestions -> {
/*    */           Objects.requireNonNull(this.suggestionMapper);
/*    */           return Suggestions.create(suggestions.commandContext(), (List<Suggestion>)suggestions.list().stream().map(this.suggestionMapper::map).collect(Collectors.toList()), suggestions.commandInput());
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\MappingSuggestionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
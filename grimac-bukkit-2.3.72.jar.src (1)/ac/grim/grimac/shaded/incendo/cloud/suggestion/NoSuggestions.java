/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import java.util.Collections;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ final class NoSuggestions
/*    */   implements SuggestionProvider<Object>
/*    */ {
/* 34 */   private static final SuggestionProvider<?> INSTANCE = new NoSuggestions();
/*    */ 
/*    */   
/* 37 */   private final CompletableFuture<? extends Iterable<? extends Suggestion>> result = CompletableFuture.completedFuture(Collections.emptyList());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<Object> context, CommandInput input) {
/* 47 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   static <C> SuggestionProvider<C> instance() {
/* 52 */     return (SuggestionProvider)INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\NoSuggestions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
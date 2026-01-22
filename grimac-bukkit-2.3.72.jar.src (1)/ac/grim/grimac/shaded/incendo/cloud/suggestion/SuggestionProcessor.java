/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*    */ import java.util.Arrays;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Stream;
/*    */ import org.apiguardian.api.API;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface SuggestionProcessor<C>
/*    */ {
/*    */   static <C> SuggestionProcessor<C> passThrough() {
/* 50 */     return (ctx, suggestions) -> suggestions;
/*    */   }
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
/*    */   default SuggestionProcessor<C> then(SuggestionProcessor<C> nextProcessor) {
/* 73 */     Objects.requireNonNull(nextProcessor, "nextProcessor");
/* 74 */     return new ChainedSuggestionProcessor<>(Arrays.asList((SuggestionProcessor<C>[])new SuggestionProcessor[] { this, nextProcessor }));
/*    */   }
/*    */   
/*    */   Stream<Suggestion> process(CommandPreprocessingContext<C> paramCommandPreprocessingContext, Stream<Suggestion> paramStream);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SuggestionProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
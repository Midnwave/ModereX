/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ final class ChainedSuggestionProcessor<C>
/*    */   implements SuggestionProcessor<C>
/*    */ {
/*    */   private final List<SuggestionProcessor<C>> links;
/*    */   
/*    */   ChainedSuggestionProcessor(List<SuggestionProcessor<C>> links) {
/* 47 */     List<SuggestionProcessor<C>> list = new ArrayList<>();
/* 48 */     flattenChain(list, links);
/* 49 */     this.links = Collections.unmodifiableList(list);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static <C> void flattenChain(List<SuggestionProcessor<C>> into, Collection<SuggestionProcessor<C>> links) {
/* 56 */     for (SuggestionProcessor<C> link : links) {
/* 57 */       if (link instanceof ChainedSuggestionProcessor) {
/* 58 */         flattenChain(into, ((ChainedSuggestionProcessor)link).links); continue;
/*    */       } 
/* 60 */       into.add(link);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Stream<Suggestion> process(CommandPreprocessingContext<C> context, Stream<Suggestion> suggestions) {
/* 70 */     Stream<Suggestion> currentLink = suggestions;
/* 71 */     for (SuggestionProcessor<C> link : this.links) {
/* 72 */       currentLink = link.process(context, currentLink);
/*    */     }
/* 74 */     return currentLink;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\ChainedSuggestionProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
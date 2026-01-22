/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface SuggestionMapper<S extends Suggestion>
/*    */ {
/*    */   static SuggestionMapper<Suggestion> identity() {
/* 46 */     return suggestion -> suggestion;
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
/*    */   default <S1 extends Suggestion> SuggestionMapper<S1> then(SuggestionMapper<S1> mapper) {
/* 65 */     Objects.requireNonNull(mapper, "mapper");
/* 66 */     return suggestion -> mapper.map((Suggestion)map(suggestion));
/*    */   }
/*    */   
/*    */   S map(Suggestion paramSuggestion);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SuggestionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ final class SimpleSuggestion
/*    */   implements Suggestion
/*    */ {
/*    */   private final String suggestion;
/*    */   
/*    */   SimpleSuggestion(String suggestion) {
/* 34 */     this.suggestion = suggestion;
/*    */   }
/*    */ 
/*    */   
/*    */   public String suggestion() {
/* 39 */     return this.suggestion;
/*    */   }
/*    */ 
/*    */   
/*    */   public Suggestion withSuggestion(String suggestion) {
/* 44 */     return new SimpleSuggestion(suggestion);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 49 */     if (this == o) {
/* 50 */       return true;
/*    */     }
/* 52 */     if (o == null || getClass() != o.getClass()) {
/* 53 */       return false;
/*    */     }
/* 55 */     SimpleSuggestion that = (SimpleSuggestion)o;
/* 56 */     return Objects.equals(this.suggestion, that.suggestion);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hash(new Object[] { this.suggestion });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return this.suggestion;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\SimpleSuggestion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
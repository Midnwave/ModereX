/*    */ package ac.grim.grimac.shaded.kyori.option.value;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.option.Option;
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
/*    */ public interface ValueSource
/*    */ {
/*    */   static ValueSource environmentVariable() {
/* 45 */     return environmentVariable("");
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
/*    */   static ValueSource environmentVariable(String prefix) {
/* 59 */     return new ValueSources.EnvironmentVariable(prefix);
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
/*    */   static ValueSource systemProperty() {
/* 71 */     return systemProperty("");
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
/*    */   static ValueSource systemProperty(String prefix) {
/* 85 */     return new ValueSources.SystemProperty(prefix);
/*    */   }
/*    */   
/*    */   <T> T value(Option<T> paramOption);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\option\value\ValueSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
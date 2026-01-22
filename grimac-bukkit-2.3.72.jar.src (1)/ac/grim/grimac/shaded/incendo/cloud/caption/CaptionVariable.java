/*    */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*    */ 
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ @Immutable
/*    */ public interface CaptionVariable
/*    */ {
/*    */   static CaptionVariable of(String key, String value) {
/* 47 */     return CaptionVariableImpl.of(key, value);
/*    */   }
/*    */   
/*    */   String key();
/*    */   
/*    */   String value();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
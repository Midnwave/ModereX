/*    */ package ac.grim.grimac.api;
/*    */ 
/*    */ import ac.grim.grimac.api.common.BasicStatus;
/*    */ 
/*    */ public interface AbstractCheck
/*    */   extends AbstractProcessor, BasicStatus {
/*    */   String getCheckName();
/*    */   
/*    */   default String getAlternativeName() {
/* 10 */     return getCheckName();
/*    */   }
/*    */   
/*    */   double getViolations();
/*    */   
/*    */   long getLastViolationTime();
/*    */   
/*    */   double getDecay();
/*    */   
/*    */   double getSetbackVL();
/*    */   
/*    */   boolean isExperimental();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\AbstractCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.api.common;
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
/*    */ public interface BasicStatus
/*    */ {
/*    */   boolean isEnabled();
/*    */   
/*    */   void setEnabled(boolean paramBoolean);
/*    */   
/*    */   default void toggle() {
/* 21 */     setEnabled(!isEnabled());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default boolean isDisabled() {
/* 29 */     return !isEnabled();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\common\BasicStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
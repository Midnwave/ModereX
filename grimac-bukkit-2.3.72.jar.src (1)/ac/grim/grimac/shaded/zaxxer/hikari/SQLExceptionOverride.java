/*    */ package ac.grim.grimac.shaded.zaxxer.hikari;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface SQLExceptionOverride
/*    */ {
/*    */   public enum Override
/*    */   {
/* 15 */     CONTINUE_EVICT,
/* 16 */     DO_NOT_EVICT;
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
/*    */   default Override adjudicate(SQLException sqlException) {
/* 28 */     return Override.CONTINUE_EVICT;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\SQLExceptionOverride.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
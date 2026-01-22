/*    */ package ac.grim.grimac.manager.violationdatabase.mysql;
/*    */ 
/*    */ import ac.grim.grimac.manager.violationdatabase.DatabaseDialect;
/*    */ 
/*    */ 
/*    */ public class MySQLDialect
/*    */   implements DatabaseDialect
/*    */ {
/*    */   public String getUuidColumnType() {
/* 10 */     return "BINARY(16)";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAutoIncrementPrimaryKeySyntax() {
/* 15 */     return "BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInsertOrIgnoreSyntax(String tableName, String columnNames) {
/* 20 */     return "INSERT IGNORE INTO " + tableName + " (" + columnNames + ") VALUES (?)";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUniqueConstraintViolationSQLState() {
/* 25 */     return "23000";
/*    */   }
/*    */ 
/*    */   
/*    */   public int getUniqueConstraintViolationErrorCode() {
/* 30 */     return 1062;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\mysql\MySQLDialect.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
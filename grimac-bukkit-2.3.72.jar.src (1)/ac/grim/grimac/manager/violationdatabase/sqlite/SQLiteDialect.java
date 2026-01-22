/*    */ package ac.grim.grimac.manager.violationdatabase.sqlite;
/*    */ 
/*    */ import ac.grim.grimac.manager.violationdatabase.DatabaseDialect;
/*    */ 
/*    */ 
/*    */ public class SQLiteDialect
/*    */   implements DatabaseDialect
/*    */ {
/*    */   public String getUuidColumnType() {
/* 10 */     return "BLOB";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAutoIncrementPrimaryKeySyntax() {
/* 15 */     return "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInsertOrIgnoreSyntax(String tableName, String columnNames) {
/* 20 */     return "INSERT OR IGNORE INTO " + tableName + " (" + columnNames + ") VALUES (?)";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUniqueConstraintViolationSQLState() {
/* 25 */     return "23000";
/*    */   }
/*    */ 
/*    */   
/*    */   public int getUniqueConstraintViolationErrorCode() {
/* 30 */     return 19;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\sqlite\SQLiteDialect.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
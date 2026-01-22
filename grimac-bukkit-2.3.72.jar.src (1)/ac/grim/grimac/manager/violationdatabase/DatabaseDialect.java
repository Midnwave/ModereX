package ac.grim.grimac.manager.violationdatabase;

public interface DatabaseDialect {
  String getUuidColumnType();
  
  String getAutoIncrementPrimaryKeySyntax();
  
  String getInsertOrIgnoreSyntax(String paramString1, String paramString2);
  
  String getUniqueConstraintViolationSQLState();
  
  int getUniqueConstraintViolationErrorCode();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\DatabaseDialect.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
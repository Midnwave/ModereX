/*   */ package ac.grim.grimac.utils.common;
/*   */ 
/*   */ public class GrimArguments
/*   */ {
/* 5 */   public static final boolean TRANSACTION_KICKS = !Boolean.getBoolean("grim.disable-transaction-kick");
/* 6 */   public static final String API_URL = System.getProperty("grim.api-url", "https://api.grim.ac/v1/server/");
/* 7 */   public static final String PASTE_URL = System.getProperty("grim.paste-url", "https://paste.grim.ac/");
/* 8 */   public static final String PLATFORM_OVERRIDE = System.getProperty("grim.platform-override", "");
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\common\GrimArguments.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
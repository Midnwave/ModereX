/*   */ package ac.grim.grimac.shaded.configuralize;
/*   */ 
/*   */ public class ParseException
/*   */   extends Exception {
/*   */   public ParseException(Source source, Throwable cause) {
/* 6 */     super("Error parsing config file " + source.getFile().getName() + ": " + cause.getMessage(), cause);
/*   */   }
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
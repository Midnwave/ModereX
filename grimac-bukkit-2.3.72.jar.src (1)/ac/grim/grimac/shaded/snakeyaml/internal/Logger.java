/*    */ package ac.grim.grimac.shaded.snakeyaml.internal;
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
/*    */ public class Logger
/*    */ {
/*    */   private final java.util.logging.Logger logger;
/*    */   
/*    */   public enum Level
/*    */   {
/* 18 */     WARNING((String)java.util.logging.Level.FINE);
/*    */     
/*    */     private final java.util.logging.Level level;
/*    */     
/*    */     Level(java.util.logging.Level level) {
/* 23 */       this.level = level;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private Logger(String name) {
/* 30 */     this.logger = java.util.logging.Logger.getLogger(name);
/*    */   }
/*    */   
/*    */   public static Logger getLogger(String name) {
/* 34 */     return new Logger(name);
/*    */   }
/*    */   
/*    */   public boolean isLoggable(Level level) {
/* 38 */     return this.logger.isLoggable(level.level);
/*    */   }
/*    */   
/*    */   public void warn(String msg) {
/* 42 */     this.logger.log(Level.WARNING.level, msg);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\internal\Logger.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
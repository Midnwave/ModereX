/*    */ package META-INF.versions.9.ac.grim.grimac.shaded.snakeyaml.internal;
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
/*    */ public class Logger
/*    */ {
/*    */   private final System.Logger logger;
/*    */   
/*    */   private Logger(String name) {
/* 27 */     this.logger = System.getLogger(name);
/*    */   }
/*    */   public static ac.grim.grimac.shaded.snakeyaml.internal.Logger getLogger(String name) {
/* 30 */     return new ac.grim.grimac.shaded.snakeyaml.internal.Logger(name);
/*    */   }
/*    */   
/*    */   public boolean isLoggable(Level level) {
/* 34 */     return this.logger.isLoggable(Level.access$000(level));
/*    */   }
/*    */   
/*    */   public void warn(String msg) {
/* 38 */     this.logger.log(Level.access$000(Level.WARNING), msg);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\META-INF\versions\9\ac\grim\grimac\shaded\snakeyaml\internal\Logger.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */
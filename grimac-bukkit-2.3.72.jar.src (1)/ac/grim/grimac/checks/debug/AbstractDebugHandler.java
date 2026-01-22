/*   */ package ac.grim.grimac.checks.debug;
/*   */ 
/*   */ import ac.grim.grimac.checks.Check;
/*   */ import ac.grim.grimac.player.GrimPlayer;
/*   */ 
/*   */ public abstract class AbstractDebugHandler extends Check {
/*   */   public AbstractDebugHandler(GrimPlayer player) {
/* 8 */     super(player);
/*   */   }
/*   */   
/*   */   public abstract void toggleListener(GrimPlayer paramGrimPlayer);
/*   */   
/*   */   public abstract boolean toggleConsoleOutput();
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\debug\AbstractDebugHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
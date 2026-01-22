/*    */ package ac.grim.grimac.api.event.events;
/*    */ 
/*    */ import ac.grim.grimac.api.AbstractCheck;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ 
/*    */ public class CommandExecuteEvent extends GrimVerboseCheckEvent {
/*    */   private final String command;
/*    */   
/*    */   public CommandExecuteEvent(GrimUser player, AbstractCheck check, String verbose, String command) {
/* 10 */     super(player, check, verbose);
/* 11 */     this.command = command;
/*    */   }
/*    */   
/*    */   public String getCommand() {
/* 15 */     return this.command;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\events\CommandExecuteEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
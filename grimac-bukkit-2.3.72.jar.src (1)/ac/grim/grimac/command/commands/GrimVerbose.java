/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ 
/*    */ public class GrimVerbose
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 13 */     commandManager.command(commandManager
/* 14 */         .commandBuilder("grim", new String[] { "grimac"
/* 15 */           }).literal("verbose", new String[0])
/* 16 */         .permission("grim.verbose")
/* 17 */         .handler(this::handleVerbose));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleVerbose(CommandContext<Sender> context) {
/* 22 */     Sender sender = (Sender)context.sender();
/* 23 */     if (sender.isPlayer()) {
/* 24 */       GrimAPI.INSTANCE.getAlertManager().toggleVerbose(((Sender)context.sender()).getPlatformPlayer(), false);
/* 25 */     } else if (sender.isConsole()) {
/* 26 */       GrimAPI.INSTANCE.getAlertManager().toggleConsoleVerbose();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimVerbose.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
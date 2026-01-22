/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public class GrimAlerts
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 16 */     commandManager.command(commandManager
/* 17 */         .commandBuilder("grim", new String[] { "grimac"
/* 18 */           }).literal("alerts", Description.of("Toggle alerts for the sender"), new String[0])
/* 19 */         .permission("grim.alerts")
/* 20 */         .handler(this::handleAlerts));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private void handleAlerts(CommandContext<Sender> context) {
/* 26 */     Sender sender = (Sender)context.sender();
/* 27 */     if (sender.isPlayer()) {
/* 28 */       GrimAPI.INSTANCE.getAlertManager().toggleAlerts(Objects.<PlatformPlayer>requireNonNull(((Sender)context.sender()).getPlatformPlayer()), false);
/* 29 */     } else if (sender.isConsole()) {
/* 30 */       GrimAPI.INSTANCE.getAlertManager().toggleConsoleAlerts();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimAlerts.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
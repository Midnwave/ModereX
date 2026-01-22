/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ 
/*    */ public class GrimSendAlert
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 16 */     commandManager.command(commandManager
/* 17 */         .commandBuilder("grim", new String[] { "grimac"
/* 18 */           }).literal("sendalert", new String[0])
/* 19 */         .permission("grim.sendalert")
/* 20 */         .required("message", StringParser.greedyStringParser())
/* 21 */         .handler(this::handleSendAlert));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleSendAlert(CommandContext<Sender> context) {
/* 26 */     String string = (String)context.get("message");
/* 27 */     string = MessageUtil.replacePlaceholders((Sender)null, string);
/* 28 */     Component message = MessageUtil.miniMessage(string);
/* 29 */     GrimAPI.INSTANCE.getAlertManager().sendAlert(message, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimSendAlert.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ 
/*    */ public class GrimHelp
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 15 */     commandManager.command(commandManager
/* 16 */         .commandBuilder("grim", new String[] { "grimac"
/* 17 */           }).literal("help", Description.of("Display help information"), new String[0])
/* 18 */         .permission("grim.help")
/* 19 */         .handler(this::handleHelp));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleHelp(CommandContext<Sender> context) {
/* 24 */     Sender sender = (Sender)context.sender();
/*    */     
/* 26 */     for (String string : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
/* 27 */       if (string == null)
/* 28 */         continue;  string = MessageUtil.replacePlaceholders(sender, string);
/* 29 */       sender.sendMessage(MessageUtil.miniMessage(string));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimHelp.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
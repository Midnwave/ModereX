/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ 
/*    */ public class GrimReload
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 15 */     commandManager.command(commandManager
/* 16 */         .commandBuilder("grim", new String[] { "grimac"
/* 17 */           }).literal("reload", new String[0])
/* 18 */         .permission("grim.reload")
/* 19 */         .handler(this::handleReload));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleReload(CommandContext<Sender> context) {
/* 24 */     Sender sender = (Sender)context.sender();
/*    */ 
/*    */     
/* 27 */     sender.sendMessage(MessageUtil.getParsedComponent(sender, "reloading", "%prefix% &7Reloading config..."));
/*    */     
/* 29 */     GrimAPI.INSTANCE.getExternalAPI().reloadAsync().exceptionally(throwable -> Boolean.valueOf(false))
/* 30 */       .thenAccept(bool -> {
/*    */           Component message = bool.booleanValue() ? MessageUtil.getParsedComponent(sender, "reloaded", "%prefix% &fConfig has been reloaded.") : MessageUtil.getParsedComponent(sender, "reload-failed", "%prefix% &cFailed to reload config.");
/*    */           sender.sendMessage(message);
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimReload.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
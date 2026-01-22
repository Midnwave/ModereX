/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ public class GrimProfile
/*    */   implements BuildableCommand
/*    */ {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 20 */     commandManager.command(commandManager
/* 21 */         .commandBuilder("grim", new String[] { "grimac"
/* 22 */           }).literal("profile", new String[0])
/* 23 */         .permission("grim.profile")
/* 24 */         .required("target", GrimAPI.INSTANCE.getCommandAdapter().singlePlayerSelectorParser())
/* 25 */         .handler(this::handleProfile));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleProfile(CommandContext<Sender> context) {
/* 30 */     Sender sender = (Sender)context.sender();
/* 31 */     PlayerSelector target = (PlayerSelector)context.get("target");
/*    */     
/* 33 */     PlatformPlayer targetPlatformPlayer = target.getSinglePlayer().getPlatformPlayer();
/* 34 */     if (((PlatformPlayer)Objects.<PlatformPlayer>requireNonNull(targetPlatformPlayer)).isExternalPlayer()) {
/* 35 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-this-server", "%prefix% &cThis player isn't on this server!"));
/*    */       
/*    */       return;
/*    */     } 
/* 39 */     GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(targetPlatformPlayer.getUniqueId());
/* 40 */     if (grimPlayer == null) {
/* 41 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-found", "%prefix% &cPlayer is exempt or offline!"));
/*    */       
/*    */       return;
/*    */     } 
/* 45 */     for (String message : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringList("profile")) {
/* 46 */       Component component = MessageUtil.miniMessage(message);
/* 47 */       sender.sendMessage(MessageUtil.replacePlaceholders(grimPlayer, component));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimProfile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
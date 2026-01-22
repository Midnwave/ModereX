/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.command.SenderRequirement;
/*    */ import ac.grim.grimac.command.requirements.PlayerSenderRequirement;
/*    */ import ac.grim.grimac.manager.init.start.CommandRegister;
/*    */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.Requirement;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public class GrimSpectate implements BuildableCommand {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 21 */     commandManager.command(commandManager
/* 22 */         .commandBuilder("grim", new String[] { "grimac"
/* 23 */           }).literal("spectate", new String[0])
/* 24 */         .permission("grim.spectate")
/* 25 */         .required("target", GrimAPI.INSTANCE.getCommandAdapter().singlePlayerSelectorParser())
/* 26 */         .handler(this::handleSpectate)
/* 27 */         .apply((Command.Builder.Applicable)CommandRegister.REQUIREMENT_FACTORY.create((Requirement[])new SenderRequirement[] { (SenderRequirement)PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT })));
/*    */   }
/*    */ 
/*    */   
/*    */   private void handleSpectate(CommandContext<Sender> context) {
/* 32 */     Sender sender = (Sender)context.sender();
/* 33 */     PlayerSelector targetSelectorResults = (PlayerSelector)context.getOrDefault("target", null);
/* 34 */     if (targetSelectorResults == null)
/*    */       return; 
/* 36 */     PlatformPlayer targetPlatformPlayer = targetSelectorResults.getSinglePlayer().getPlatformPlayer();
/*    */     
/* 38 */     if (targetPlatformPlayer != null && targetPlatformPlayer.getUniqueId().equals(sender.getUniqueId())) {
/* 39 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "cannot-run-on-self", "%prefix% &cYou cannot use this command on yourself!"));
/*    */       
/*    */       return;
/*    */     } 
/* 43 */     if (targetPlatformPlayer != null && targetPlatformPlayer.isExternalPlayer()) {
/* 44 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-this-server", "%prefix% &cThis player isn't on this server!"));
/*    */       
/*    */       return;
/*    */     } 
/* 48 */     PlatformPlayer platformPlayer = Objects.<PlatformPlayer>requireNonNull(sender.getPlatformPlayer());
/*    */ 
/*    */     
/* 51 */     if (GrimAPI.INSTANCE.getSpectateManager().enable(platformPlayer)) {
/* 52 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "spectate-return", "<click:run_command:/grim stopspectating><hover:show_text:\"/grim stopspectating\">\n%prefix% &fClick here to return to previous location\n</hover></click>"));
/*    */     }
/*    */     
/* 55 */     platformPlayer.setGameMode(GameMode.SPECTATOR);
/* 56 */     platformPlayer.teleportAsync(((PlatformPlayer)Objects.<PlatformPlayer>requireNonNull(targetPlatformPlayer)).getLocation());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimSpectate.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
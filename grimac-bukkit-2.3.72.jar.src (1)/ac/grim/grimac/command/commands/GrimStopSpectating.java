/*    */ package ac.grim.grimac.command.commands;
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.command.SenderRequirement;
/*    */ import ac.grim.grimac.command.requirements.PlayerSenderRequirement;
/*    */ import ac.grim.grimac.manager.init.start.CommandRegister;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.Requirement;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import java.util.List;
/*    */ 
/*    */ public class GrimStopSpectating implements BuildableCommand {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 22 */     commandManager.command(commandManager
/* 23 */         .commandBuilder("grim", new String[] { "grimac"
/* 24 */           }).literal("stopspectating", new String[0])
/* 25 */         .permission("grim.spectate")
/* 26 */         .optional("here", StringParser.stringParser(), SuggestionProvider.blocking((ctx, in) -> ((Sender)ctx.sender()).hasPermission("grim.spectate.stophere") ? List.of(Suggestion.suggestion("here")) : List.of()))
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 32 */         .handler(this::onStopSpectate)
/* 33 */         .apply((Command.Builder.Applicable)CommandRegister.REQUIREMENT_FACTORY.create((Requirement[])new SenderRequirement[] { (SenderRequirement)PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT })));
/*    */   }
/*    */ 
/*    */   
/*    */   public void onStopSpectate(CommandContext<Sender> commandContext) {
/* 38 */     Sender sender = (Sender)commandContext.sender();
/* 39 */     String string = (String)commandContext.getOrDefault("here", null);
/* 40 */     if (GrimAPI.INSTANCE.getSpectateManager().isSpectating(sender.getUniqueId())) {
/* 41 */       boolean teleportBack = (string == null || !string.equalsIgnoreCase("here") || !sender.hasPermission("grim.spectate.stophere"));
/* 42 */       GrimAPI.INSTANCE.getSpectateManager().disable(Objects.<PlatformPlayer>requireNonNull(sender.getPlatformPlayer()), teleportBack);
/*    */     } else {
/* 44 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "cannot-spectate-return", "%prefix% &cYou can only do this after spectating a player."));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimStopSpectating.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
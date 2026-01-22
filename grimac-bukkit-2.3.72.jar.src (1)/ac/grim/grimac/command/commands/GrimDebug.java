/*     */ package ac.grim.grimac.command.commands;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.command.BuildableCommand;
/*     */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BuildableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*     */ 
/*     */ public class GrimDebug implements BuildableCommand {
/*     */   public void register(CommandManager<Sender> commandManager) {
/*  23 */     Command.Builder<Sender> grimCommand = commandManager.commandBuilder("grim", new String[] { "grimac" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  30 */     Command.Builder<Sender> debugCommand = grimCommand.literal("debug", Description.of("Toggle debug output for a player"), new String[0]).permission("grim.debug").optional("target", GrimAPI.INSTANCE.getCommandAdapter().singlePlayerSelectorParser()).handler(this::handleDebug);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  37 */     Command.Builder<Sender> consoleDebugCommand = grimCommand.literal("consoledebug", Description.of("Toggle console debug output for a player"), new String[0]).permission("grim.consoledebug").required("target", GrimAPI.INSTANCE.getCommandAdapter().singlePlayerSelectorParser()).handler(this::handleConsoleDebug);
/*     */ 
/*     */     
/*  40 */     commandManager.command(debugCommand);
/*  41 */     commandManager.command(consoleDebugCommand);
/*     */   }
/*     */   
/*     */   private void handleDebug(CommandContext<Sender> context) {
/*  45 */     Sender sender = (Sender)context.sender();
/*  46 */     PlayerSelector playerSelector = (PlayerSelector)context.getOrDefault("target", null);
/*     */     
/*  48 */     GrimPlayer targetGrimPlayer = parseTarget(sender, (playerSelector == null) ? sender : playerSelector.getSinglePlayer());
/*  49 */     if (targetGrimPlayer == null)
/*     */       return; 
/*  51 */     if (sender.isConsole()) {
/*  52 */       targetGrimPlayer.checkManager.getDebugHandler().toggleConsoleOutput();
/*  53 */     } else if (sender.isPlayer()) {
/*  54 */       GrimPlayer senderGrimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(sender.getUniqueId());
/*  55 */       targetGrimPlayer.checkManager.getDebugHandler().toggleListener(senderGrimPlayer);
/*     */     } else {
/*  57 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "run-as-player-or-console", "%prefix% &cThis command can only be used by players or the console!"));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleConsoleDebug(CommandContext<Sender> context) {
/*  65 */     Sender sender = (Sender)context.sender();
/*  66 */     PlayerSelector targetName = (PlayerSelector)context.getOrDefault("target", null);
/*     */     
/*  68 */     GrimPlayer grimPlayer = parseTarget(sender, targetName.getSinglePlayer());
/*  69 */     if (grimPlayer == null)
/*     */       return; 
/*  71 */     boolean isOutput = grimPlayer.checkManager.getDebugHandler().toggleConsoleOutput();
/*  72 */     String playerName = grimPlayer.user.getProfile().getName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     BuildableComponent buildableComponent = ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("Console output for ", (TextColor)NamedTextColor.GRAY))).append((Component)Component.text(playerName, (TextColor)NamedTextColor.WHITE))).append((Component)Component.text(" is now ", (TextColor)NamedTextColor.GRAY))).append((Component)Component.text(isOutput ? "enabled" : "disabled", (TextColor)NamedTextColor.WHITE))).build();
/*     */     
/*  81 */     sender.sendMessage((Component)buildableComponent);
/*     */   }
/*     */   
/*     */   private GrimPlayer parseTarget(Sender sender, Sender t) {
/*  85 */     if (sender.isConsole() && t == null) {
/*  86 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "console-specify-target", "%prefix% &cYou must specify a target as the console!"));
/*  87 */       return null;
/*     */     } 
/*  89 */     Sender target = (t == null) ? sender : t;
/*     */     
/*  91 */     GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(target.getUniqueId());
/*  92 */     if (grimPlayer == null) {
/*  93 */       User user = PacketEvents.getAPI().getPlayerManager().getUser(sender.getPlatformPlayer().getNative());
/*  94 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-found", "%prefix% &cPlayer is exempt or offline!"));
/*     */       
/*  96 */       if (user == null) {
/*  97 */         sender.sendMessage((Component)Component.text("Unknown PacketEvents user", (TextColor)NamedTextColor.RED));
/*     */       } else {
/*  99 */         boolean isExempt = GrimAPI.INSTANCE.getPlayerDataManager().shouldCheck(user);
/* 100 */         if (!isExempt) {
/* 101 */           sender.sendMessage((Component)Component.text("User connection state: " + String.valueOf(user.getConnectionState()), (TextColor)NamedTextColor.RED));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 106 */     return grimPlayer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimDebug.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
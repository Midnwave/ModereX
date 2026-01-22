/*     */ package ac.grim.grimac.command.commands;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.GrimIdentity;
/*     */ import ac.grim.grimac.command.BuildableCommand;
/*     */ import ac.grim.grimac.command.CommandUtils;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ public class GrimList
/*     */   implements BuildableCommand
/*     */ {
/*     */   public void register(CommandManager<Sender> commandManager) {
/*  32 */     commandManager.command(commandManager.commandBuilder("grim", new String[] { "grimac"
/*  33 */           }).literal("list", new String[0])
/*  34 */         .permission("grim.list")
/*  35 */         .required("list", StringParser.stringParser(), this.SUGGESTIONS)
/*  36 */         .handler(commandContext -> handleList((Sender)commandContext.sender(), ((String)commandContext.getOrDefault("list", "?")).toLowerCase()))
/*  37 */         .build());
/*     */   }
/*     */   
/*  40 */   private final SuggestionProvider<Sender> SUGGESTIONS = CommandUtils.fromStrings(new String[] { "players" });
/*     */   
/*     */   private void handleList(Sender sender, String id) {
/*  43 */     switch (id) { case "players":
/*  44 */         handleListPlayers(sender); return; }
/*  45 */      sender.sendMessage((Component)((TextComponent.Builder)((TextComponent.Builder)Component.text()
/*  46 */         .append((Component)Component.text("Invalid argument: ", (TextColor)NamedTextColor.GRAY)))
/*  47 */         .append((Component)Component.text(id, (TextColor)NamedTextColor.RED)))
/*  48 */         .build());
/*     */   }
/*     */ 
/*     */   
/*     */   private Component playerComponent(String name, UUID uuid, boolean online, boolean exempt) {
/*  53 */     return ((TextComponent)((TextComponent)Component.text(name)
/*  54 */       .color(exempt ? (online ? (TextColor)NamedTextColor.GRAY : (TextColor)NamedTextColor.DARK_GRAY) : (
/*  55 */         online ? (TextColor)NamedTextColor.WHITE : (TextColor)NamedTextColor.RED)))
/*  56 */       .clickEvent(ClickEvent.copyToClipboard(name)))
/*  57 */       .hoverEvent((HoverEventSource)HoverEvent.showText(playerHoverComponent(uuid, online, exempt, true)));
/*     */   }
/*     */   
/*     */   private Component playerHoverComponent(UUID uuid, boolean online, boolean exempt, boolean registered) {
/*  61 */     TextComponent.Builder builder = Component.text();
/*  62 */     builder.append(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text()
/*  63 */         .append(Component.text("UUID: ").color((TextColor)NamedTextColor.GRAY)))
/*  64 */         .append(Component.text(String.valueOf(uuid)).color((TextColor)NamedTextColor.WHITE)))
/*  65 */         .append((Component)Component.newline()))
/*  66 */         .append(Component.text("Status: ").color((TextColor)NamedTextColor.GRAY)))
/*  67 */         .append(online ? Component.text("Online").color((TextColor)NamedTextColor.GREEN) : 
/*  68 */           Component.text("Offline").color((TextColor)NamedTextColor.RED)));
/*  69 */     if (exempt) {
/*  70 */       builder.append((Component)Component.newline());
/*  71 */       builder.append(Component.text("Is Exempt").color((TextColor)NamedTextColor.LIGHT_PURPLE));
/*     */     } 
/*  73 */     if (!registered) {
/*  74 */       builder.append((Component)Component.newline());
/*  75 */       builder.append(Component.text("Not Registered").color((TextColor)NamedTextColor.RED));
/*     */     } 
/*  77 */     return (Component)builder.build();
/*     */   }
/*     */   
/*     */   private void handleListPlayers(Sender sender) {
/*  81 */     TextComponent.Builder builder = Component.text();
/*     */ 
/*     */     
/*  84 */     Map<UUID, PlatformPlayer> onlinePlayers = (Map<UUID, PlatformPlayer>)GrimAPI.INSTANCE.getPlatformPlayerFactory().getOnlinePlayers().stream().collect(Collectors.toMap(GrimIdentity::getUniqueId, Function.identity()));
/*     */     
/*  86 */     Set<PlatformPlayer> unregisteredPlayers = new HashSet<>(onlinePlayers.values());
/*     */     
/*  88 */     boolean after = false;
/*  89 */     builder.append((Component)Component.text("Players = [", (TextColor)NamedTextColor.GRAY));
/*     */     
/*  91 */     for (GrimPlayer entry : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
/*  92 */       if (after) {
/*  93 */         builder.append(Component.text(", ").color((TextColor)NamedTextColor.GRAY));
/*     */       } else {
/*  95 */         after = true;
/*     */       } 
/*  97 */       PlatformPlayer platformPlayer = onlinePlayers.get(entry.getUniqueId());
/*  98 */       if (platformPlayer != null) unregisteredPlayers.remove(platformPlayer); 
/*  99 */       boolean online = (platformPlayer != null && platformPlayer.isOnline());
/* 100 */       boolean exempt = !GrimAPI.INSTANCE.getPlayerDataManager().shouldCheck(entry.user);
/* 101 */       builder.append(playerComponent(entry.getName(), entry.getUniqueId(), online, exempt));
/*     */     } 
/*     */     
/* 104 */     for (PlatformPlayer platformPlayer : unregisteredPlayers) {
/* 105 */       if (after) {
/* 106 */         builder.append(Component.text(", ").color((TextColor)NamedTextColor.GRAY));
/*     */       } else {
/* 108 */         after = true;
/*     */       } 
/* 110 */       builder.append(((TextComponent)((TextComponent)Component.text(platformPlayer.getName()).color((TextColor)NamedTextColor.LIGHT_PURPLE))
/* 111 */           .clickEvent(ClickEvent.suggestCommand(platformPlayer.getName())))
/* 112 */           .hoverEvent((HoverEventSource)HoverEvent.showText(playerHoverComponent(platformPlayer.getUniqueId(), platformPlayer.isOnline(), false, false))));
/*     */     } 
/*     */ 
/*     */     
/* 116 */     builder.append((Component)Component.text("]", (TextColor)NamedTextColor.GRAY));
/* 117 */     sender.sendMessage((Component)builder.build());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimList.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
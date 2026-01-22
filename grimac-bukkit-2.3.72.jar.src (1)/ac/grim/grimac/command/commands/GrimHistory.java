/*     */ package ac.grim.grimac.command.commands;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.command.BuildableCommand;
/*     */ import ac.grim.grimac.manager.violationdatabase.Violation;
/*     */ import ac.grim.grimac.manager.violationdatabase.ViolationDatabaseManager;
/*     */ import ac.grim.grimac.platform.api.player.OfflinePlatformPlayer;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.IntegerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*     */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ public class GrimHistory
/*     */   implements BuildableCommand
/*     */ {
/*     */   public void register(CommandManager<Sender> commandManager) {
/*  22 */     commandManager.command(commandManager
/*  23 */         .commandBuilder("grim", new String[] { "grimac"
/*  24 */           }).literal("history", new String[] { "hist"
/*  25 */           }).permission("grim.help")
/*  26 */         .required("target", StringParser.stringParser(), GrimAPI.INSTANCE.getCommandAdapter().onlinePlayerSuggestions())
/*  27 */         .optional("page", IntegerParser.integerParser())
/*  28 */         .permission("grim.history")
/*  29 */         .handler(this::handleHistory));
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleHistory(CommandContext<Sender> context) {
/*  34 */     Sender sender = (Sender)context.sender();
/*  35 */     String target = (String)context.get("target");
/*  36 */     Integer page = (Integer)context.getOrDefault("page", Integer.valueOf(1));
/*     */     
/*  38 */     if (!GrimAPI.INSTANCE.getViolationDatabaseManager().isEnabled()) {
/*     */       
/*  40 */       String msg = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("grim-history-disabled", "%prefix% &cHistory subsystem is disabled!");
/*     */       
/*  42 */       sender.sendMessage(MessageUtil.miniMessage(msg)); return;
/*     */     } 
/*  44 */     if (!GrimAPI.INSTANCE.getViolationDatabaseManager().isLoaded()) {
/*     */       
/*  46 */       String msg = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("grim-history-load-failure", "%prefix% &cHistory subsystem failed to load! Check server console for errors.");
/*     */       
/*  48 */       sender.sendMessage(MessageUtil.miniMessage(msg));
/*     */       
/*     */       return;
/*     */     } 
/*  52 */     GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(GrimAPI.INSTANCE.getGrimPlugin(), () -> {
/*     */           int entriesPerPage = GrimAPI.INSTANCE.getConfigManager().getConfig().getIntElse("history.entries-per-page", 15);
/*     */           String header = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("grim-history-header", "%prefix% &bShowing logs for &f%player% (&f%page%&b/&f%maxPages%&f)");
/*     */           String logFormat = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("grim-history-entry", "%prefix% &8[&f%server%&8] &bFailed &f%check% (x&c%vl%&f) &7%verbose% (&b%timeago% ago&7)");
/*     */           OfflinePlatformPlayer targetPlayer = GrimAPI.INSTANCE.getPlatformPlayerFactory().getOfflineFromName(target);
/*     */           ViolationDatabaseManager violations = GrimAPI.INSTANCE.getViolationDatabaseManager();
/*     */           int logCount = violations.getLogCount(targetPlayer.getUniqueId());
/*     */           List<Violation> logs = violations.getViolations(targetPlayer.getUniqueId(), page.intValue(), entriesPerPage);
/*     */           int maxPages = (int)Math.ceil((logCount / entriesPerPage));
/*     */           sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, header.replace("%player%", targetPlayer.getName()).replace("%page%", String.valueOf(page)).replace("%maxPages%", String.valueOf(maxPages)))));
/*     */           for (int i = logs.size() - 1; i >= 0; i--) {
/*     */             Violation log = logs.get(i);
/*     */             sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, logFormat.replace("%player%", targetPlayer.getName()).replace("%grim_version%", log.grimVersion()).replace("%client_brand%", log.clientBrand()).replace("%client_version%", log.clientVersion()).replace("%server_version%", log.serverVersion()).replace("%check%", log.checkName()).replace("%verbose%", log.verbose()).replace("%vl%", String.valueOf(log.vl())).replace("%timeago%", getTimeAgo(log.createdAt())).replace("%server%", log.server()))));
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getTimeAgo(long timestamp) {
/*  98 */     long durationMillis = System.currentTimeMillis() - timestamp;
/*     */ 
/*     */     
/* 101 */     if (durationMillis < 0L) {
/* 102 */       return "0s";
/*     */     }
/*     */     
/* 105 */     long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
/* 106 */     durationMillis -= TimeUnit.DAYS.toMillis(days);
/*     */     
/* 108 */     long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
/* 109 */     durationMillis -= TimeUnit.HOURS.toMillis(hours);
/*     */     
/* 111 */     long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
/* 112 */     durationMillis -= TimeUnit.MINUTES.toMillis(minutes);
/*     */     
/* 114 */     long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
/*     */     
/* 116 */     StringBuilder result = new StringBuilder();
/* 117 */     if (days > 0L) result.append(days).append("d "); 
/* 118 */     if (hours > 0L) result.append(hours).append("h "); 
/* 119 */     if (minutes > 0L) result.append(minutes).append("m "); 
/* 120 */     if (seconds > 0L || result.length() == 0) result.append(seconds).append("s");
/*     */     
/* 122 */     return result.toString().trim();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimHistory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
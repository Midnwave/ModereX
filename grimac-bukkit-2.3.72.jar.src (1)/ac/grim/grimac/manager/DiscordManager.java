/*     */ package ac.grim.grimac.manager;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.manager.init.ReloadableInitable;
/*     */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*     */ import ac.grim.grimac.utils.webhook.Embed;
/*     */ import ac.grim.grimac.utils.webhook.EmbedField;
/*     */ import ac.grim.grimac.utils.webhook.EmbedFooter;
/*     */ import ac.grim.grimac.utils.webhook.WebhookMessage;
/*     */ import java.awt.Color;
/*     */ import java.net.URI;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class DiscordManager
/*     */   implements StartableInitable, ReloadableInitable {
/*  29 */   public static final Predicate<String> WEBHOOK_REGEX = Pattern.compile("https://discord.com/api/webhooks/\\d+/[\\w-]+").asMatchPredicate();
/*  30 */   public static final Duration timeout = Duration.ofSeconds(15L);
/*  31 */   private static final HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).build();
/*  32 */   private static final ConcurrentLinkedDeque<HttpRequest> requests = new ConcurrentLinkedDeque<>();
/*  33 */   private static final AtomicBoolean taskStarted = new AtomicBoolean();
/*  34 */   private static final AtomicBoolean sending = new AtomicBoolean();
/*     */   private static long rateLimitedUntil;
/*     */   private URI url;
/*     */   private int embedColor;
/*  38 */   private String staticContent = "";
/*  39 */   private String embedTitle = "";
/*     */   
/*     */   private boolean includeTimestamp;
/*     */   
/*     */   public void start() {
/*  44 */     reload();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reload() {
/*     */     try {
/*  50 */       if (!GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("enabled", false)) {
/*  51 */         this.url = null;
/*     */         
/*     */         return;
/*     */       } 
/*  55 */       String webhook = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("webhook", "");
/*     */       
/*  57 */       if (!WEBHOOK_REGEX.test(webhook)) {
/*  58 */         LogUtil.error("Discord webhook url does not follow expected format (https://discord.com/api/webhooks/<id>/<token>): " + webhook);
/*  59 */         this.url = null;
/*     */       } else {
/*  61 */         this.url = new URI(webhook);
/*     */       } 
/*     */       
/*  64 */       this.embedTitle = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("embed-title", "**Grim Alert**");
/*     */       
/*     */       try {
/*  67 */         this.embedColor = Color.decode(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("embed-color", "#00FFFF")).getRGB();
/*  68 */       } catch (NumberFormatException e) {
/*  69 */         LogUtil.warn("Discord embed color is invalid");
/*     */       } 
/*     */       
/*  72 */       StringBuilder sb = new StringBuilder();
/*  73 */       for (String string : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringListElse("violation-content", getDefaultContents())) {
/*  74 */         sb.append(string).append("\n");
/*     */       }
/*  76 */       this.staticContent = sb.toString();
/*  77 */       this.includeTimestamp = GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("include-timestamp", true);
/*  78 */     } catch (Exception e) {
/*  79 */       LogUtil.error("Failed to load Discord webhook configuration", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private List<String> getDefaultContents() {
/*  84 */     List<String> list = new ArrayList<>();
/*  85 */     list.add("**Player**: %player%");
/*  86 */     list.add("**Check**: %check%");
/*  87 */     list.add("**Violations**: %violations%");
/*  88 */     list.add("**Client Version**: %version%");
/*  89 */     list.add("**Brand**: %brand%");
/*  90 */     list.add("**Ping**: %ping%");
/*  91 */     list.add("**TPS**: %tps%");
/*  92 */     return list;
/*     */   }
/*     */   
/*     */   public void sendAlert(GrimPlayer player, String verbose, String checkName, int violations) {
/*  96 */     if (this.url == null) {
/*     */       return;
/*     */     }
/*     */     
/* 100 */     String content = this.staticContent;
/* 101 */     content = content.replace("%check%", checkName.replace("_", "\\_"));
/* 102 */     content = content.replace("%violations%", Integer.toString(violations));
/* 103 */     content = MessageUtil.replacePlaceholders(player, content, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     Embed embed = (new Embed(content)).imageURL("https://i.stack.imgur.com/Fzh0w.png").thumbnailURL("https://crafthead.net/helm/" + String.valueOf(player.user.getProfile().getUUID())).color(Integer.valueOf(this.embedColor)).title(this.embedTitle).footer(new EmbedFooter("", "https://grim.ac/images/grim.png"));
/*     */     
/* 112 */     if (this.includeTimestamp) {
/* 113 */       embed.timestamp(Instant.now());
/*     */     }
/*     */     
/* 116 */     if (!verbose.isEmpty()) {
/* 117 */       embed.addFields(new EmbedField[] { new EmbedField("Verbose", MessageUtil.filterDiscordText(verbose), true) });
/*     */     }
/*     */     
/* 120 */     sendWebhookMessage((new WebhookMessage()).addEmbeds(new Embed[] { embed }));
/*     */   }
/*     */   
/*     */   public void sendWebhookMessage(WebhookMessage message) {
/* 124 */     requests.add(HttpRequest.newBuilder()
/* 125 */         .uri(this.url)
/* 126 */         .header("Content-Type", "application/json")
/* 127 */         .POST(HttpRequest.BodyPublishers.ofString(message.toJson().toString()))
/* 128 */         .timeout(timeout)
/* 129 */         .build());
/*     */     
/* 131 */     if (!taskStarted.getAndSet(true))
/*     */     {
/* 133 */       GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getGrimPlugin(), DiscordManager::tick, 0L, 1L);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void tick() {
/* 138 */     HttpRequest request = requests.peek();
/* 139 */     if (request != null && rateLimitedUntil < System.currentTimeMillis() && !sending.getAndSet(true))
/* 140 */       client.<String>sendAsync(request, HttpResponse.BodyHandlers.ofString()).whenComplete((response, throwable) -> {
/*     */             if (throwable != null) {
/*     */               sending.set(false);
/*     */               LogUtil.error("Exception caught while sending a Discord webhook alert", throwable);
/*     */               return;
/*     */             } 
/*     */             if (response != null && response.statusCode() == 429) {
/*     */               sending.set(false);
/*     */               rateLimitedUntil = Math.max(response.headers().firstValueAsLong("X-RateLimit-Reset").getAsLong() * 1000L, rateLimitedUntil);
/*     */               return;
/*     */             } 
/*     */             requests.remove(request);
/*     */             sending.set(false);
/*     */             if (response != null && response.statusCode() >= 400)
/*     */               LogUtil.error("Encountered status code " + response.statusCode() + " with body " + (String)response.body() + " and headers " + String.valueOf(response.headers().map()) + " while sending a Discord webhook alert."); 
/*     */           }); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\DiscordManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
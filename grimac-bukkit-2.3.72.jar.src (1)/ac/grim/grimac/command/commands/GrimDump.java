/*     */ package ac.grim.grimac.command.commands;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.command.BuildableCommand;
/*     */ import ac.grim.grimac.platform.api.PlatformPlugin;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*     */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*     */ import ac.grim.grimac.utils.common.PropertiesUtil;
/*     */ import ac.grim.grimac.utils.reflection.ReflectionUtils;
/*     */ import ac.grim.grimac.utils.reflection.ViaVersionUtil;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.GsonBuilder;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class GrimDump
/*     */   implements BuildableCommand
/*     */ {
/*  26 */   private static final boolean PAPER = (ReflectionUtils.hasClass("com.destroystokyo.paper.PaperConfig") || 
/*  27 */     ReflectionUtils.hasClass("io.papermc.paper.configuration.Configuration"));
/*  28 */   private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
/*  29 */   private String link = null;
/*     */ 
/*     */   
/*     */   public void register(CommandManager<Sender> commandManager) {
/*  33 */     commandManager.command(commandManager
/*  34 */         .commandBuilder("grim", new String[] { "grimac"
/*  35 */           }).literal("dump", Description.of("Generate a debug dump"), new String[0])
/*  36 */         .permission("grim.dump")
/*  37 */         .handler(this::handleDump));
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleDump(CommandContext<Sender> context) {
/*  42 */     Sender sender = (Sender)context.sender();
/*     */     
/*  44 */     if (this.link != null) {
/*  45 */       sender.sendMessage(MessageUtil.miniMessage(GrimAPI.INSTANCE.getConfigManager().getConfig()
/*  46 */             .getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%")
/*  47 */             .replace("%url%", this.link)));
/*     */       
/*     */       return;
/*     */     } 
/*  51 */     GrimLog.sendLogAsync(sender, generateDump(), string -> this.link = string, "text/yaml");
/*     */   }
/*     */   
/*     */   public static JsonObject getBasicInfo(String type) {
/*  55 */     JsonObject base = new JsonObject();
/*  56 */     base.addProperty("type", type);
/*  57 */     base.addProperty("timestamp", Long.valueOf(System.currentTimeMillis()));
/*     */     
/*  59 */     JsonObject versions = new JsonObject();
/*  60 */     base.add("versions", (JsonElement)versions);
/*  61 */     versions.addProperty("grim", GrimAPI.INSTANCE.getExternalAPI().getGrimVersion());
/*  62 */     versions.addProperty("packetevents", PacketEvents.getAPI().getVersion().toString());
/*  63 */     versions.addProperty("server", PacketEvents.getAPI().getServerManager().getVersion().getReleaseName());
/*  64 */     versions.addProperty("implementation", GrimAPI.INSTANCE.getPlatformServer().getPlatformImplementationString());
/*     */     
/*  66 */     JsonObject states = new JsonObject();
/*  67 */     base.add("states", (JsonElement)states);
/*  68 */     if (GrimAPI.INSTANCE.isInitialized()) states.addProperty("platform", GrimAPI.INSTANCE.getPlatform().toString()); 
/*  69 */     if (ViaVersionUtil.isAvailable) states.addProperty("has_viaversion", Boolean.valueOf(true)); 
/*  70 */     if (PAPER) states.addProperty("has_paper", Boolean.valueOf(true));
/*     */     
/*  72 */     JsonObject system = new JsonObject();
/*  73 */     base.add("system", (JsonElement)system);
/*  74 */     system.addProperty("os_name", System.getProperty("os.name"));
/*  75 */     system.addProperty("java_version", System.getProperty("java.version"));
/*  76 */     system.addProperty("user_language", System.getProperty("user.language"));
/*     */     
/*  78 */     JsonObject build = new JsonObject();
/*  79 */     base.add("build", (JsonElement)getBuildInfo());
/*  80 */     return base;
/*     */   }
/*     */   
/*     */   private static JsonObject getBuildInfo() {
/*  84 */     JsonObject object = new JsonObject();
/*     */     try {
/*  86 */       Properties properties = PropertiesUtil.readProperties(GrimAPI.INSTANCE.getClass(), "grimac.properties");
/*  87 */       for (Map.Entry<Object, Object> entry : properties.entrySet()) {
/*  88 */         object.addProperty(entry.getKey().toString(), entry.getValue().toString());
/*     */       }
/*  90 */     } catch (Exception exception) {}
/*  91 */     return object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String generateDump() {
/* 101 */     JsonObject base = getBasicInfo("dump");
/*     */     
/* 103 */     JsonArray plugins = new JsonArray();
/* 104 */     base.add("plugins", (JsonElement)plugins);
/* 105 */     for (PlatformPlugin plugin : GrimAPI.INSTANCE.getPluginManager().getPlugins()) {
/* 106 */       JsonObject pluginJson = new JsonObject();
/* 107 */       pluginJson.addProperty("enabled", Boolean.valueOf(plugin.isEnabled()));
/* 108 */       pluginJson.addProperty("name", plugin.getName());
/* 109 */       pluginJson.addProperty("version", plugin.getVersion());
/* 110 */       plugins.add((JsonElement)pluginJson);
/*     */     } 
/* 112 */     return this.gson.toJson((JsonElement)base);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimDump.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
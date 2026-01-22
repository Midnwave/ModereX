/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.MetricsBase;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.charts.CustomChart;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.json.JsonObjectBuilder;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Metrics
/*     */ {
/*     */   private final Plugin plugin;
/*     */   private final MetricsBase metricsBase;
/*     */   
/*     */   public Metrics(Plugin plugin, int serviceId) {
/*  31 */     this.plugin = plugin;
/*     */ 
/*     */     
/*  34 */     File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
/*  35 */     File configFile = new File(bStatsFolder, "config.yml");
/*  36 */     YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
/*     */     
/*  38 */     if (!config.isSet("serverUuid")) {
/*  39 */       config.addDefault("enabled", Boolean.valueOf(true));
/*  40 */       config.addDefault("serverUuid", UUID.randomUUID().toString());
/*  41 */       config.addDefault("logFailedRequests", Boolean.valueOf(false));
/*  42 */       config.addDefault("logSentData", Boolean.valueOf(false));
/*  43 */       config.addDefault("logResponseStatusText", Boolean.valueOf(false));
/*     */ 
/*     */       
/*  46 */       config.options().header("bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous.")
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  52 */         .copyDefaults(true);
/*     */       try {
/*  54 */         config.save(configFile);
/*  55 */       } catch (IOException iOException) {}
/*     */     } 
/*     */ 
/*     */     
/*  59 */     boolean enabled = config.getBoolean("enabled", true);
/*  60 */     String serverUUID = config.getString("serverUuid");
/*  61 */     boolean logErrors = config.getBoolean("logFailedRequests", false);
/*  62 */     boolean logSentData = config.getBoolean("logSentData", false);
/*  63 */     boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);
/*     */     
/*  65 */     boolean isFolia = false;
/*     */     try {
/*  67 */       isFolia = (Class.forName("io.papermc.paper.threadedregions.RegionizedServer") != null);
/*  68 */     } catch (Exception exception) {}
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
/*  79 */     Objects.requireNonNull(plugin); this.metricsBase = new MetricsBase("bukkit", serverUUID, serviceId, enabled, this::appendPlatformData, this::appendServiceData, isFolia ? null : (submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask)), plugin::isEnabled, (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error), message -> this.plugin.getLogger().log(Level.INFO, message), logErrors, logSentData, logResponseStatusText, false);
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
/*     */   public void shutdown() {
/*  94 */     this.metricsBase.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCustomChart(CustomChart chart) {
/* 103 */     this.metricsBase.addCustomChart(chart);
/*     */   }
/*     */   
/*     */   private void appendPlatformData(JsonObjectBuilder builder) {
/* 107 */     builder.appendField("playerAmount", getPlayerAmount());
/* 108 */     builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
/* 109 */     builder.appendField("bukkitVersion", Bukkit.getVersion());
/* 110 */     builder.appendField("bukkitName", Bukkit.getName());
/*     */     
/* 112 */     builder.appendField("javaVersion", System.getProperty("java.version"));
/* 113 */     builder.appendField("osName", System.getProperty("os.name"));
/* 114 */     builder.appendField("osArch", System.getProperty("os.arch"));
/* 115 */     builder.appendField("osVersion", System.getProperty("os.version"));
/* 116 */     builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
/*     */   }
/*     */   
/*     */   private void appendServiceData(JsonObjectBuilder builder) {
/* 120 */     builder.appendField("pluginVersion", this.plugin.getDescription().getVersion());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int getPlayerAmount() {
/*     */     try {
/* 127 */       Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", new Class[0]);
/* 128 */       return onlinePlayersMethod.getReturnType().equals(Collection.class) ? (
/* 129 */         (Collection)onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).size() : (
/* 130 */         (Player[])onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).length;
/* 131 */     } catch (Exception e) {
/* 132 */       return Bukkit.getOnlinePlayers().size();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bstats\bukkit\Metrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
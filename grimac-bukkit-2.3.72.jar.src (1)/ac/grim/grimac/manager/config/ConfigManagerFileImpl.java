/*     */ package ac.grim.grimac.manager.config;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.common.BasicReloadable;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.shaded.configuralize.DynamicConfig;
/*     */ import ac.grim.grimac.shaded.configuralize.Language;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ConfigManagerFileImpl
/*     */   implements ConfigManager, BasicReloadable
/*     */ {
/*     */   private final DynamicConfig config;
/*     */   private boolean initialized = false;
/*     */   
/*     */   public ConfigManagerFileImpl() {
/*  23 */     this.config = new DynamicConfig();
/*     */   }
/*     */   
/*     */   private File getConfigFile(String path) {
/*  27 */     return new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), path);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reload() {
/*  32 */     GrimAPI.INSTANCE.getGrimPlugin().getDataFolder().mkdirs();
/*  33 */     if (!this.initialized) {
/*  34 */       this.initialized = true;
/*  35 */       this.config.addSource(GrimAPI.class, "config", getConfigFile("config.yml"));
/*  36 */       this.config.addSource(GrimAPI.class, "messages", getConfigFile("messages.yml"));
/*  37 */       this.config.addSource(GrimAPI.class, "discord", getConfigFile("discord.yml"));
/*  38 */       this.config.addSource(GrimAPI.class, "punishments", getConfigFile("punishments.yml"));
/*     */     } 
/*     */     
/*  41 */     String languageCode = System.getProperty("user.language").toUpperCase();
/*     */     
/*     */     try {
/*  44 */       this.config.setLanguage(Language.valueOf(languageCode));
/*  45 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*     */ 
/*     */ 
/*     */     
/*  49 */     if (!this.config.isLanguageAvailable(this.config.getLanguage())) {
/*  50 */       String lang = languageCode.toUpperCase();
/*  51 */       LogUtil.info("Unknown user language " + lang + ".");
/*  52 */       LogUtil.info("If you fluently speak " + lang + " as well as English, see the GitHub repo to translate it!");
/*  53 */       this.config.setLanguage(Language.EN);
/*     */     } 
/*     */     
/*     */     try {
/*  57 */       this.config.saveAllDefaults(false);
/*  58 */     } catch (IOException e) {
/*  59 */       throw new RuntimeException("Failed to save default config files", e);
/*     */     } 
/*     */     
/*     */     try {
/*  63 */       this.config.loadAll();
/*  64 */     } catch (Exception e) {
/*  65 */       throw new RuntimeException("Failed to load config", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void upgrade() {
/*  70 */     File config = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "config.yml");
/*  71 */     if (config.exists()) {
/*     */       try {
/*  73 */         String configString = new String(Files.readAllBytes(config.toPath()));
/*     */         
/*  75 */         int configVersion = configString.indexOf("config-version: ");
/*     */         
/*  77 */         if (configVersion != -1) {
/*  78 */           String configStringVersion = configString.substring(configVersion + "config-version: ".length());
/*  79 */           configStringVersion = configStringVersion.substring(0, !configStringVersion.contains("\n") ? configStringVersion.length() : configStringVersion.indexOf("\n"));
/*  80 */           configStringVersion = configStringVersion.replaceAll("\\D", "");
/*     */           
/*  82 */           configVersion = Integer.parseInt(configStringVersion);
/*     */           
/*  84 */           configString = configString.replaceAll("config-version: " + configStringVersion, "config-version: 9");
/*  85 */           Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */           
/*  87 */           upgradeModernConfig(config, configString, configVersion);
/*     */         } else {
/*  89 */           removeLegacyTwoPointOne(config);
/*     */         }
/*     */       
/*  92 */       } catch (IOException e) {
/*  93 */         LogUtil.error("Failed to upgrade config file", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void upgradeModernConfig(File config, String configString, int configVersion) throws IOException {
/*  99 */     if (configVersion < 1) {
/* 100 */       addMaxPing(config, configString);
/*     */     }
/* 102 */     if (configVersion < 2) {
/* 103 */       addMissingPunishments();
/*     */     }
/* 105 */     if (configVersion < 3) {
/* 106 */       addBaritoneCheck();
/*     */     }
/* 108 */     if (configVersion < 4) {
/* 109 */       newOffsetNewDiscordConf(config, configString);
/*     */     }
/* 111 */     if (configVersion < 5) {
/* 112 */       fixBadPacketsAndAdjustPingConfig(config, configString);
/*     */     }
/* 114 */     if (configVersion < 6) {
/* 115 */       addSuperDebug(config, configString);
/*     */     }
/* 117 */     if (configVersion < 7) {
/* 118 */       removeAlertsOnJoin(config, configString);
/*     */     }
/* 120 */     if (configVersion < 8) {
/* 121 */       addPacketSpamThreshold(config, configString);
/*     */     }
/* 123 */     if (configVersion < 9) {
/* 124 */       newOffsetHandlingAntiKB(config, configString);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeLegacyTwoPointOne(File config) throws IOException {
/* 130 */     Files.move(config.toPath(), (new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "config-2.1.old.yml")).toPath(), new java.nio.file.CopyOption[0]);
/*     */   }
/*     */   
/*     */   private void addMaxPing(File config, String configString) throws IOException {
/* 134 */     configString = configString + "\n\n\n# How long should players have until we keep them for timing out? Default = 2 minutes\nmax-ping: 120";
/*     */ 
/*     */ 
/*     */     
/* 138 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   private void addMissingPunishments() {
/* 143 */     File config = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "punishments.yml");
/*     */     
/* 145 */     if (config.exists()) {
/*     */       try {
/* 147 */         String configString = new String(Files.readAllBytes(config.toPath()));
/*     */ 
/*     */         
/* 150 */         int commentIndex = configString.indexOf("  # As of 2.2.2 these are just placeholders, there are no Killaura/Aim/Autoclicker checks other than those that");
/* 151 */         if (commentIndex != -1) {
/*     */           
/* 153 */           configString = configString.substring(0, commentIndex);
/* 154 */           configString = configString + "  Combat:\n    remove-violations-after: 300\n    checks:\n      - \"Killaura\"\n      - \"Aim\"\n    commands:\n      - \"20:40 [alert]\"\n  # As of 2.2.10, there are no AutoClicker checks and this is a placeholder. 2.3 will include AutoClicker checks.\n  Autoclicker:\n    remove-violations-after: 300\n    checks:\n      - \"Autoclicker\"\n    commands:\n      - \"20:40 [alert]\"\n";
/*     */         } 
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
/* 170 */         Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/* 171 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void fixBadPacketsAndAdjustPingConfig(File config, String configString) {
/*     */     try {
/* 178 */       configString = configString.replaceAll("max-ping: \\d+", "max-transaction-time: 60");
/* 179 */       Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/* 180 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 183 */     File punishConfig = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "punishments.yml");
/*     */     
/* 185 */     if (punishConfig.exists()) {
/*     */       try {
/* 187 */         String punishConfigString = new String(Files.readAllBytes(punishConfig.toPath()));
/* 188 */         punishConfigString = punishConfigString.replace("commands:", "commands:");
/* 189 */         Files.write(punishConfig.toPath(), punishConfigString.getBytes(), new java.nio.file.OpenOption[0]);
/* 190 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void addBaritoneCheck() {
/* 196 */     File config = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "punishments.yml");
/*     */     
/* 198 */     if (config.exists()) {
/*     */       try {
/* 200 */         String configString = new String(Files.readAllBytes(config.toPath()));
/* 201 */         configString = configString.replace("      - \"EntityControl\"\n", "      - \"EntityControl\"\n      - \"Baritone\"\n      - \"FastBreak\"\n");
/* 202 */         Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/* 203 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void newOffsetNewDiscordConf(File config, String configString) throws IOException {
/* 209 */     configString = configString.replace("threshold: 0.0001", "threshold: 0.001");
/* 210 */     configString = configString.replace("threshold: 0.00001", "threshold: 0.001");
/* 211 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */     
/* 213 */     File discordFile = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "discord.yml");
/*     */     
/* 215 */     if (discordFile.exists()) {
/*     */       try {
/* 217 */         String discordString = new String(Files.readAllBytes(discordFile.toPath()));
/* 218 */         discordString = discordString + "\nembed-color: \"#00FFFF\"\nviolation-content:\n  - \"**Player**: %player%\"\n  - \"**Check**: %check%\"\n  - \"**Violations**: %violations%\"\n  - \"**Client Version**: %version%\"\n  - \"**Brand**: %brand%\"\n  - \"**Ping**: %ping%\"\n  - \"**TPS**: %tps%\"\n";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 227 */         Files.write(discordFile.toPath(), discordString.getBytes(), new java.nio.file.OpenOption[0]);
/* 228 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addSuperDebug(File config, String configString) throws IOException {
/* 235 */     configString = configString.replace("threshold: 0.0001", "threshold: 0.001");
/* 236 */     if (!configString.contains("experimental-checks")) {
/* 237 */       configString = configString + "\n\n# Enables experimental checks\nexperimental-checks: false\n\n";
/*     */     }
/*     */     
/* 240 */     configString = configString + "\nverbose:\n  print-to-console: false\n";
/*     */     
/* 242 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */     
/* 244 */     File messageFile = new File(GrimAPI.INSTANCE.getGrimPlugin().getDataFolder(), "messages.yml");
/* 245 */     if (messageFile.exists()) {
/*     */       try {
/* 247 */         String messagesString = new String(Files.readAllBytes(messageFile.toPath()));
/* 248 */         messagesString = messagesString + "\n\nupload-log: \"%prefix% &fUploaded debug to: %url%\"\nupload-log-start: \"%prefix% &fUploading log... please wait\"\nupload-log-not-found: \"%prefix% &cUnable to find that log\"\nupload-log-upload-failure: \"%prefix% &cSomething went wrong while uploading this log, see console for more info\"\n";
/*     */ 
/*     */ 
/*     */         
/* 252 */         Files.write(messageFile.toPath(), messagesString.getBytes(), new java.nio.file.OpenOption[0]);
/* 253 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeAlertsOnJoin(File config, String configString) throws IOException {
/* 259 */     configString = configString.replaceAll("  # Should players with grim\\.alerts permission automatically enable alerts on join\\?\r?\n  enable-on-join: (?:true|false)\r?\n", "");
/* 260 */     configString = configString.replaceAll("  # 管理员进入时是否自动开启警告？\r?\n  enable-on-join: (?:true|false)\r?\n", "");
/* 261 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */   }
/*     */   
/*     */   private void addPacketSpamThreshold(File config, String configString) throws IOException {
/* 265 */     configString = configString + "\n# Grim sometimes cancels illegal packets such as with timer, after X packets in a second cancelled, when should\n# we simply kick the player? This is required as some packet limiters don't count packets cancelled by grim.\npacket-spam-threshold: 150\n";
/*     */ 
/*     */     
/* 268 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */   }
/*     */   
/*     */   private void newOffsetHandlingAntiKB(File config, String configString) throws IOException {
/* 272 */     configString = configString.replaceAll("  # How much of an offset is \"cheating\"\r?\n  # By default this is 1e-5, which is safe and sane\r?\n  # Measured in blocks from the correct movement\r?\n  threshold: 0.001\r?\n  setbackvl: 3", "  # How much should we multiply total advantage by when the player is legit\n  setback-decay-multiplier: 0.999\n  # How large of an offset from the player's velocity should we create a violation for?\n  # Measured in blocks from the possible velocity\n  threshold: 0.001\n  # How large of a violation in a tick before the player gets immediately setback?\n  # -1 to disable\n  immediate-setback-threshold: 0.1\n  # How large of an advantage over all ticks before we start to setback?\n  # -1 to disable\n  max-advantage: 1\n  # This is to stop the player from gathering too many violations and never being able to clear them all\n  max-ceiling: 4");
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
/* 287 */     Files.write(config.toPath(), configString.getBytes(), new java.nio.file.OpenOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringElse(String key, String otherwise) {
/* 292 */     return this.config.getStringElse(key, otherwise);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getString(String key) {
/* 297 */     return this.config.getString(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getStringList(String key) {
/* 302 */     return this.config.getStringList(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getStringListElse(String key, List<String> otherwise) {
/* 307 */     return this.config.getStringListElse(key, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntElse(String key, int other) {
/* 312 */     return this.config.getIntElse(key, other);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongElse(String key, long otherwise) {
/* 317 */     return this.config.getLongElse(key, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDoubleElse(String key, double otherwise) {
/* 322 */     return this.config.getDoubleElse(key, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBooleanElse(String key, boolean otherwise) {
/* 327 */     return this.config.getBooleanElse(key, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T get(String key) {
/* 332 */     return (T)this.config.get(key);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T> T getElse(String key, T otherwise) {
/* 337 */     return (T)this.config.getElse(key, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public <K, V> Map<K, V> getMap(String key) {
/* 342 */     return this.config.getMap(key);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <K, V> Map<K, V> getMapElse(String s, Map<K, V> map) {
/* 347 */     return this.config.getMapElse(s, map);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T> List<T> getList(String path) {
/* 352 */     return this.config.getList(path);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public <T> List<T> getListElse(String path, List<T> otherwise) {
/* 357 */     return this.config.getListElse(path, otherwise);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasLoaded() {
/* 362 */     return this.initialized;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\config\ConfigManagerFileImpl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.manager.violationdatabase;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.manager.init.ReloadableInitable;
/*     */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*     */ import ac.grim.grimac.manager.violationdatabase.mysql.MySQLViolationDatabase;
/*     */ import ac.grim.grimac.manager.violationdatabase.sqlite.SQLiteViolationDatabase;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class ViolationDatabaseManager implements StartableInitable, ReloadableInitable {
/*     */   private final GrimPlugin plugin;
/*     */   private boolean enabled = false;
/*     */   
/*     */   @Generated
/*  22 */   public boolean isEnabled() { return this.enabled; } private boolean loaded = false; @Generated
/*  23 */   public boolean isLoaded() { return this.loaded; }
/*     */   
/*     */   private ViolationDatabase database;
/*     */   
/*     */   public ViolationDatabaseManager(GrimPlugin plugin) {
/*  28 */     this.plugin = plugin;
/*  29 */     this.database = NoOpViolationDatabase.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/*  34 */     load();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reload() {
/*  39 */     load();
/*     */   } public void load() {
/*     */     String host, db, user, pwd;
/*     */     ViolationDatabase violationDatabase;
/*  43 */     ConfigManager cfg = GrimAPI.INSTANCE.getConfigManager().getConfig();
/*  44 */     this.enabled = cfg.getBooleanElse("history.enabled", false);
/*  45 */     String rawType = this.enabled ? cfg.getStringElse("history.database.type", "SQLITE").toUpperCase() : "NOOP";
/*     */     
/*  47 */     switch (rawType) {
/*     */       case "SQLITE":
/*  49 */         if (!(this.database instanceof SQLiteViolationDatabase)) {
/*  50 */           this.database.disconnect();
/*     */           
/*     */           try {
/*  53 */             Class.forName("org.sqlite.JDBC");
/*  54 */             this.database = (ViolationDatabase)new SQLiteViolationDatabase(this.plugin);
/*  55 */             this.database.connect();
/*  56 */             this.loaded = true;
/*  57 */           } catch (ClassNotFoundException e) {
/*  58 */             LogUtil.error("Could not load SQLite driver for /grim history database.\nDownload the minecraft-sqlite-jdbc mod/plugin for SQLite support, or change history.database.type\nAlternatively set history.enabled=false to remove this message if /grim history support is not desired");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  64 */             this.database = NoOpViolationDatabase.INSTANCE;
/*  65 */             this.loaded = false;
/*  66 */           } catch (SQLException e) {
/*  67 */             LogUtil.error(e);
/*  68 */             this.database = NoOpViolationDatabase.INSTANCE;
/*  69 */             this.loaded = false;
/*     */           } 
/*     */         } 
/*     */         return;
/*     */       
/*     */       case "MYSQL":
/*  75 */         host = cfg.getStringElse("history.database.host", "localhost:3306");
/*  76 */         db = cfg.getStringElse("history.database.database", "grimac");
/*  77 */         user = cfg.getStringElse("history.database.username", "root");
/*  78 */         pwd = cfg.getStringElse("history.database.password", "password");
/*     */         
/*  80 */         violationDatabase = this.database; if (violationDatabase instanceof MySQLViolationDatabase) { MySQLViolationDatabase mysql = (MySQLViolationDatabase)violationDatabase; if (mysql
/*  81 */             .sameConfig(host, db, user, pwd))
/*     */             return;  }
/*     */         
/*  84 */         this.database.disconnect();
/*  85 */         this.database = (ViolationDatabase)new MySQLViolationDatabase(this.plugin, host, db, user, pwd);
/*     */         try {
/*  87 */           this.database.connect();
/*  88 */           this.loaded = true;
/*  89 */         } catch (SQLException e) {
/*  90 */           LogUtil.error(e);
/*  91 */           this.database = NoOpViolationDatabase.INSTANCE;
/*  92 */           this.loaded = false;
/*     */         } 
/*     */         return;
/*     */     } 
/*     */     
/*  97 */     if (!(this.database instanceof NoOpViolationDatabase)) {
/*  98 */       this.database.disconnect();
/*  99 */       this.database = NoOpViolationDatabase.INSTANCE;
/* 100 */       this.loaded = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void logAlert(GrimPlayer player, String verbose, String checkName, int vls) {
/* 107 */     String grimVersion = GrimAPI.INSTANCE.getExternalAPI().getGrimVersion();
/* 108 */     GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(this.plugin, () -> this.database.logAlert(player, grimVersion, verbose, checkName, vls));
/*     */   }
/*     */   
/*     */   public int getLogCount(UUID player) {
/* 112 */     return this.database.getLogCount(player);
/*     */   }
/*     */   
/*     */   public List<Violation> getViolations(UUID player, int page, int limit) {
/* 116 */     return this.database.getViolations(player, page, limit);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\ViolationDatabaseManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
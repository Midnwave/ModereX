/*     */ package ac.grim.grimac.manager.violationdatabase.mysql;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.manager.violationdatabase.DatabaseDialect;
/*     */ import ac.grim.grimac.manager.violationdatabase.DatabaseUtils;
/*     */ import ac.grim.grimac.manager.violationdatabase.Violation;
/*     */ import ac.grim.grimac.manager.violationdatabase.ViolationDatabase;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.zaxxer.hikari.HikariConfig;
/*     */ import ac.grim.grimac.shaded.zaxxer.hikari.HikariDataSource;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class MySQLViolationDatabase
/*     */   implements ViolationDatabase {
/*     */   private final GrimPlugin plugin;
/*     */   private HikariDataSource dataSource;
/*     */   private final DatabaseDialect dialect;
/*     */   
/*     */   public MySQLViolationDatabase(GrimPlugin plugin, String url, String database, String username, String password) {
/*  28 */     this.plugin = plugin;
/*  29 */     this.dialect = new MySQLDialect();
/*  30 */     setupDataSource(url, database, username, password);
/*     */   }
/*     */   
/*     */   private void setupDataSource(String url, String database, String username, String password) {
/*  34 */     HikariConfig config = new HikariConfig();
/*  35 */     config.setJdbcUrl("jdbc:mysql://" + url + "/" + database);
/*  36 */     config.setUsername(username);
/*  37 */     config.setPassword(password);
/*  38 */     config.addDataSourceProperty("cachePrepStmts", "true");
/*  39 */     config.addDataSourceProperty("prepStmtCacheSize", "250");
/*  40 */     config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
/*  41 */     config.setMaximumPoolSize(10);
/*  42 */     config.setAutoCommit(true);
/*  43 */     this.dataSource = new HikariDataSource(config);
/*     */   }
/*     */   
/*     */   public void connect() throws SQLException {
/*     */     
/*  48 */     try { Connection connection = this.dataSource.getConnection(); 
/*  49 */       try { String pkSyntax = this.dialect.getAutoIncrementPrimaryKeySyntax();
/*  50 */         String uuidType = this.dialect.getUuidColumnType();
/*     */ 
/*     */         
/*  53 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_servers(id " + pkSyntax + ", server_name VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  58 */           .execute();
/*  59 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_servers_name ON grim_history_servers(server_name);")
/*     */           
/*  61 */           .execute();
/*     */ 
/*     */         
/*  64 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_check_names(id " + pkSyntax + ", check_name_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  69 */           .execute();
/*  70 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_check_names_string ON grim_history_check_names(check_name_string);")
/*     */           
/*  72 */           .execute();
/*     */ 
/*     */ 
/*     */         
/*  76 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_versions(id " + pkSyntax + ", grim_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  81 */           .execute();
/*  82 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_versions_string ON grim_history_versions(grim_version_string);")
/*     */           
/*  84 */           .execute();
/*     */ 
/*     */         
/*  87 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_client_brands(id " + pkSyntax + ", client_brand_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  92 */           .execute();
/*  93 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_client_brands_string ON grim_history_client_brands(client_brand_string);")
/*     */           
/*  95 */           .execute();
/*     */ 
/*     */         
/*  98 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_client_versions(id " + pkSyntax + ", client_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 103 */           .execute();
/* 104 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_client_versions_string ON grim_history_client_versions(client_version_string);")
/*     */           
/* 106 */           .execute();
/*     */ 
/*     */         
/* 109 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_server_versions(id " + pkSyntax + ", server_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 114 */           .execute();
/* 115 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_server_versions_string ON grim_history_server_versions(server_version_string);")
/*     */           
/* 117 */           .execute();
/*     */ 
/*     */ 
/*     */         
/* 121 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_violations(id " + pkSyntax + ", server_id BIGINT NOT NULL, uuid " + uuidType + " NOT NULL, check_name_id BIGINT NOT NULL, verbose TEXT NOT NULL, vl INT NOT NULL, created_at BIGINT NOT NULL, grim_version_id BIGINT NOT NULL, client_brand_id BIGINT NOT NULL, client_version_id BIGINT NOT NULL, server_version_id BIGINT NOT NULL, FOREIGN KEY (server_id) REFERENCES grim_history_servers(id), FOREIGN KEY (check_name_id) REFERENCES grim_history_check_names(id), FOREIGN KEY (grim_version_id) REFERENCES grim_history_versions(id), FOREIGN KEY (client_brand_id) REFERENCES grim_history_client_brands(id), FOREIGN KEY (client_version_id) REFERENCES grim_history_client_versions(id), FOREIGN KEY (server_version_id) REFERENCES grim_history_server_versions(id))")
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
/* 141 */           .execute();
/*     */ 
/*     */         
/* 144 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_uuid ON grim_history_violations(uuid);")
/*     */           
/* 146 */           .execute();
/* 147 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_created_at ON grim_history_violations(created_at);")
/*     */           
/* 149 */           .execute();
/* 150 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_server_id ON grim_history_violations(server_id);")
/*     */           
/* 152 */           .execute();
/* 153 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_check_name_id ON grim_history_violations(check_name_id);")
/*     */           
/* 155 */           .execute();
/* 156 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_grim_version_id ON grim_history_violations(grim_version_id);")
/*     */           
/* 158 */           .execute();
/* 159 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_client_brand_id ON grim_history_violations(client_brand_id);")
/*     */           
/* 161 */           .execute();
/* 162 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_client_version_id ON grim_history_violations(client_version_id);")
/*     */           
/* 164 */           .execute();
/* 165 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_server_version_id ON grim_history_violations(server_version_id);")
/*     */           
/* 167 */           .execute();
/*     */         
/* 169 */         if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 170 */     { LogUtil.error("Failed to generate violations database:", ex);
/* 171 */       throw ex; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void logAlert(GrimPlayer player, String grimVersion, String verbose, String checkName, int vls) {
/*     */     
/* 178 */     try { Connection connection = this.dataSource.getConnection();
/*     */       
/* 180 */       try { PreparedStatement insertAlert = connection.prepareStatement("INSERT INTO grim_history_violations (server_id, uuid, check_name_id, verbose, vl, created_at, grim_version_id, client_brand_id, client_version_id, server_version_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
/* 196 */         try { String serverName = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("history.server-name", "Prison");
/* 197 */           long serverId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_servers", "server_name", serverName);
/* 198 */           long checkNameId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_check_names", "check_name_string", checkName);
/* 199 */           long grimVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_versions", "grim_version_string", grimVersion);
/* 200 */           long clientBrandId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_client_brands", "client_brand_string", player.getBrand());
/* 201 */           long clientVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_client_versions", "client_version_string", player.getClientVersion().getReleaseName());
/* 202 */           long serverVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_server_versions", "server_version_string", PacketEvents.getAPI().getServerManager().getVersion().toString());
/*     */ 
/*     */           
/* 205 */           insertAlert.setLong(1, serverId);
/* 206 */           insertAlert.setBytes(2, DatabaseUtils.uuidToBytes(player.getUniqueId()));
/* 207 */           insertAlert.setLong(3, checkNameId);
/* 208 */           insertAlert.setString(4, verbose);
/* 209 */           insertAlert.setInt(5, vls);
/* 210 */           insertAlert.setLong(6, System.currentTimeMillis());
/* 211 */           insertAlert.setLong(7, grimVersionId);
/* 212 */           insertAlert.setLong(8, clientBrandId);
/* 213 */           insertAlert.setLong(9, clientVersionId);
/* 214 */           insertAlert.setLong(10, serverVersionId);
/*     */           
/* 216 */           insertAlert.execute();
/* 217 */           if (insertAlert != null) insertAlert.close();  } catch (Throwable throwable) { if (insertAlert != null) try { insertAlert.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 218 */     { LogUtil.error("Failed to log alert", ex); }
/*     */   
/*     */   }
/*     */   
/*     */   public synchronized int getLogCount(UUID player) {
/*     */     
/* 224 */     try { Connection connection = this.dataSource.getConnection(); 
/* 225 */       try { PreparedStatement countLogs = connection.prepareStatement("SELECT COUNT(*) FROM grim_history_violations WHERE uuid = ?");
/*     */ 
/*     */ 
/*     */         
/* 229 */         try { countLogs.setBytes(1, DatabaseUtils.uuidToBytes(player));
/* 230 */           ResultSet result = countLogs.executeQuery();
/* 231 */           if (result.next())
/* 232 */           { int i = result.getInt(1);
/*     */             
/* 234 */             if (countLogs != null) countLogs.close();  if (connection != null) connection.close();  return i; }  if (countLogs != null) countLogs.close();  } catch (Throwable throwable) { if (countLogs != null) try { countLogs.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 235 */     { LogUtil.error("Failed to count logs", ex); }
/*     */     
/* 237 */     return 0;
/*     */   }
/*     */   
/*     */   public synchronized List<Violation> getViolations(UUID player, int page, int limit) {
/*     */     
/* 242 */     try { Connection connection = this.dataSource.getConnection();
/*     */       
/* 244 */       try { PreparedStatement fetchLogs = connection.prepareStatement("SELECT v.id, s.server_name, v.uuid, cn.check_name_string, v.verbose, v.vl, v.created_at, gv.grim_version_string, cb.client_brand_string, clv.client_version_string, srv.server_version_string FROM grim_history_violations v JOIN grim_history_servers s ON v.server_id = s.id JOIN grim_history_check_names cn ON v.check_name_id = cn.id JOIN grim_history_versions gv ON v.grim_version_id = gv.id JOIN grim_history_client_brands cb ON v.client_brand_id = cb.id JOIN grim_history_client_versions clv ON v.client_version_id = clv.id JOIN grim_history_server_versions srv ON v.server_version_id = srv.id WHERE v.uuid = ? ORDER BY v.created_at DESC LIMIT ? OFFSET ?");
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
/* 267 */         try { fetchLogs.setBytes(1, DatabaseUtils.uuidToBytes(player));
/* 268 */           fetchLogs.setInt(2, limit);
/* 269 */           fetchLogs.setInt(3, (page - 1) * limit);
/* 270 */           List<Violation> list = Violation.fromResultSet(fetchLogs.executeQuery());
/* 271 */           if (fetchLogs != null) fetchLogs.close();  if (connection != null) connection.close();  return list; } catch (Throwable throwable) { if (fetchLogs != null) try { fetchLogs.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 272 */     { LogUtil.error("Failed to fetch logs", ex);
/* 273 */       return null; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect() {
/* 279 */     if (this.dataSource != null && !this.dataSource.isClosed()) {
/* 280 */       this.dataSource.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean sameConfig(String host, String db, String user, String pwd) {
/* 285 */     String wantUrl = "jdbc:mysql://" + host + "/" + db;
/* 286 */     return (wantUrl.equalsIgnoreCase(this.dataSource.getJdbcUrl()) && user
/* 287 */       .equals(this.dataSource.getUsername()) && pwd
/* 288 */       .equals(this.dataSource.getPassword()));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\mysql\MySQLViolationDatabase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
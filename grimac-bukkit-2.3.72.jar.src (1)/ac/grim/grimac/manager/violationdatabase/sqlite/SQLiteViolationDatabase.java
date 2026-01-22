/*     */ package ac.grim.grimac.manager.violationdatabase.sqlite;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.manager.violationdatabase.DatabaseDialect;
/*     */ import ac.grim.grimac.manager.violationdatabase.DatabaseUtils;
/*     */ import ac.grim.grimac.manager.violationdatabase.Violation;
/*     */ import ac.grim.grimac.manager.violationdatabase.ViolationDatabase;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import java.io.File;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class SQLiteViolationDatabase implements ViolationDatabase {
/*     */   private final GrimPlugin plugin;
/*     */   private Connection openConnection;
/*     */   private final DatabaseDialect dialect;
/*     */   
/*     */   public SQLiteViolationDatabase(@NotNull GrimPlugin plugin) {
/*  29 */     this.plugin = plugin;
/*  30 */     this.dialect = new SQLiteDialect();
/*     */   }
/*     */   
/*     */   public void connect() throws SQLException {
/*     */     
/*  35 */     try { Connection connection = getConnection(); 
/*  36 */       try { Statement stmt = connection.createStatement(); 
/*  37 */         try { stmt.execute("PRAGMA foreign_keys = ON;");
/*  38 */           if (stmt != null) stmt.close();  } catch (Throwable throwable) { if (stmt != null)
/*     */             try { stmt.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
/*  40 */          String pkSyntax = this.dialect.getAutoIncrementPrimaryKeySyntax();
/*  41 */         String uuidType = this.dialect.getUuidColumnType();
/*     */ 
/*     */         
/*  44 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_servers(id " + pkSyntax + ", server_name VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  49 */           .execute();
/*  50 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_servers_name ON grim_history_servers(server_name)")
/*     */           
/*  52 */           .execute();
/*     */ 
/*     */         
/*  55 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_check_names(id " + pkSyntax + ", check_name_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  60 */           .execute();
/*  61 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_check_names_string ON grim_history_check_names(check_name_string)")
/*     */           
/*  63 */           .execute();
/*     */ 
/*     */         
/*  66 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_versions(id " + pkSyntax + ", grim_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  71 */           .execute();
/*  72 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_versions_string ON grim_history_versions(grim_version_string)")
/*     */           
/*  74 */           .execute();
/*     */ 
/*     */         
/*  77 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_client_brands(id " + pkSyntax + ", client_brand_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  82 */           .execute();
/*  83 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_client_brands_string ON grim_history_client_brands(client_brand_string)")
/*     */           
/*  85 */           .execute();
/*     */ 
/*     */         
/*  88 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_client_versions(id " + pkSyntax + ", client_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  93 */           .execute();
/*  94 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_client_versions_string ON grim_history_client_versions(client_version_string)")
/*     */           
/*  96 */           .execute();
/*     */ 
/*     */         
/*  99 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_server_versions(id " + pkSyntax + ", server_version_string VARCHAR(255) NOT NULL UNIQUE)")
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 104 */           .execute();
/* 105 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_server_versions_string ON grim_history_server_versions(server_version_string)")
/*     */           
/* 107 */           .execute();
/*     */ 
/*     */ 
/*     */         
/* 111 */         connection.prepareStatement("CREATE TABLE IF NOT EXISTS grim_history_violations(id " + pkSyntax + ", server_id INTEGER NOT NULL, uuid " + uuidType + " NOT NULL, check_name_id INTEGER NOT NULL, verbose TEXT NOT NULL, vl INTEGER NOT NULL, created_at BIGINT NOT NULL, grim_version_id INTEGER NOT NULL, client_brand_id INTEGER NOT NULL, client_version_id INTEGER NOT NULL, server_version_id INTEGER NOT NULL, FOREIGN KEY (server_id) REFERENCES grim_history_servers(id), FOREIGN KEY (check_name_id) REFERENCES grim_history_check_names(id), FOREIGN KEY (grim_version_id) REFERENCES grim_history_versions(id), FOREIGN KEY (client_brand_id) REFERENCES grim_history_client_brands(id), FOREIGN KEY (client_version_id) REFERENCES grim_history_client_versions(id), FOREIGN KEY (server_version_id) REFERENCES grim_history_server_versions(id))")
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
/* 131 */           .execute();
/*     */ 
/*     */         
/* 134 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_uuid ON grim_history_violations(uuid)")
/*     */           
/* 136 */           .execute();
/* 137 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_created_at ON grim_history_violations(created_at)")
/*     */           
/* 139 */           .execute();
/* 140 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_server_id ON grim_history_violations(server_id)")
/*     */           
/* 142 */           .execute();
/* 143 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_check_name_id ON grim_history_violations(check_name_id)")
/*     */           
/* 145 */           .execute();
/* 146 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_grim_version_id ON grim_history_violations(grim_version_id)")
/*     */           
/* 148 */           .execute();
/* 149 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_client_brand_id ON grim_history_violations(client_brand_id)")
/*     */           
/* 151 */           .execute();
/* 152 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_client_version_id ON grim_history_violations(client_version_id)")
/*     */           
/* 154 */           .execute();
/* 155 */         connection.prepareStatement("CREATE INDEX IF NOT EXISTS idx_grim_history_violations_server_version_id ON grim_history_violations(server_version_id)")
/*     */           
/* 157 */           .execute();
/*     */         
/* 159 */         if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 160 */     { LogUtil.error("Failed to generate violations database:", ex);
/* 161 */       throw ex; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void logAlert(GrimPlayer player, String grimVersion, String verbose, String checkName, int vls) {
/*     */     
/* 169 */     try { Connection connection = getConnection(); 
/* 170 */       try { PreparedStatement insertLog = connection.prepareStatement("INSERT INTO grim_history_violations (server_id, uuid, check_name_id, verbose, vl, created_at, grim_version_id, client_brand_id, client_version_id, server_version_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
/* 185 */         try { String serverName = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("history.server-name", "Prison");
/* 186 */           long serverId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_servers", "server_name", serverName);
/* 187 */           long checkNameId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_check_names", "check_name_string", checkName);
/* 188 */           long grimVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_versions", "grim_version_string", grimVersion);
/* 189 */           long clientBrandId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_client_brands", "client_brand_string", player.getBrand());
/* 190 */           long clientVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_client_versions", "client_version_string", player.getClientVersion().getReleaseName());
/* 191 */           long serverVersionId = DatabaseUtils.getOrCreateId(connection, this.dialect, "grim_history_server_versions", "server_version_string", PacketEvents.getAPI().getServerManager().getVersion().toString());
/*     */ 
/*     */           
/* 194 */           insertLog.setLong(1, serverId);
/* 195 */           insertLog.setBytes(2, DatabaseUtils.uuidToBytes(player.getUniqueId()));
/* 196 */           insertLog.setLong(3, checkNameId);
/* 197 */           insertLog.setString(4, verbose);
/* 198 */           insertLog.setInt(5, vls);
/* 199 */           insertLog.setLong(6, System.currentTimeMillis());
/* 200 */           insertLog.setLong(7, grimVersionId);
/* 201 */           insertLog.setLong(8, clientBrandId);
/* 202 */           insertLog.setLong(9, clientVersionId);
/* 203 */           insertLog.setLong(10, serverVersionId);
/*     */           
/* 205 */           insertLog.executeUpdate();
/* 206 */           if (insertLog != null) insertLog.close();  } catch (Throwable throwable) { if (insertLog != null) try { insertLog.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 207 */     { LogUtil.error("Failed to insert violation:", ex); }
/*     */   
/*     */   }
/*     */   
/*     */   public synchronized int getLogCount(UUID player) {
/*     */     
/* 213 */     try { Connection connection = getConnection(); 
/* 214 */       try { PreparedStatement fetchLogs = connection.prepareStatement("SELECT COUNT(*) FROM grim_history_violations WHERE uuid = ?");
/*     */ 
/*     */ 
/*     */         
/* 218 */         try { fetchLogs.setBytes(1, DatabaseUtils.uuidToBytes(player));
/* 219 */           ResultSet resultSet = fetchLogs.executeQuery();
/* 220 */           if (resultSet.next())
/* 221 */           { int i = resultSet.getInt(1);
/*     */             
/* 223 */             if (fetchLogs != null) fetchLogs.close();  if (connection != null) connection.close();  return i; }  if (fetchLogs != null) fetchLogs.close();  } catch (Throwable throwable) { if (fetchLogs != null) try { fetchLogs.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (connection != null) connection.close();  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 224 */     { LogUtil.error("Failed to fetch number of violations:", ex); }
/*     */     
/* 226 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized List<Violation> getViolations(UUID player, int page, int limit) {
/* 231 */     List<Violation> violations = new ArrayList<>();
/*     */     
/* 233 */     try { Connection connection = getConnection(); 
/* 234 */       try { PreparedStatement fetchLogs = connection.prepareStatement("SELECT v.id, s.server_name, v.uuid, cn.check_name_string, v.verbose, v.vl, v.created_at, gv.grim_version_string, cb.client_brand_string, clv.client_version_string, srv.server_version_string FROM grim_history_violations v JOIN grim_history_servers s ON v.server_id = s.id JOIN grim_history_check_names cn ON v.check_name_id = cn.id JOIN grim_history_versions gv ON v.grim_version_id = gv.id JOIN grim_history_client_brands cb ON v.client_brand_id = cb.id JOIN grim_history_client_versions clv ON v.client_version_id = clv.id JOIN grim_history_server_versions srv ON v.server_version_id = srv.id WHERE v.uuid = ? ORDER BY v.created_at DESC LIMIT ? OFFSET ?");
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
/* 257 */         try { fetchLogs.setBytes(1, DatabaseUtils.uuidToBytes(player));
/* 258 */           fetchLogs.setInt(2, limit);
/* 259 */           fetchLogs.setInt(3, (page - 1) * limit);
/*     */           
/* 261 */           List<Violation> list = Violation.fromResultSet(fetchLogs.executeQuery());
/* 262 */           if (fetchLogs != null) fetchLogs.close();  if (connection != null) connection.close();  return list; } catch (Throwable throwable) { if (fetchLogs != null) try { fetchLogs.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (Throwable throwable) { if (connection != null) try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException ex)
/* 263 */     { LogUtil.error("Failed to fetch violations:", ex);
/*     */       
/* 265 */       return violations; }
/*     */   
/*     */   }
/*     */   
/*     */   public void disconnect() {
/*     */     try {
/* 271 */       if (this.openConnection != null && !this.openConnection.isClosed()) {
/* 272 */         this.openConnection.close();
/*     */       }
/* 274 */     } catch (SQLException ex) {
/* 275 */       LogUtil.error("Failed to close connection", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected synchronized Connection getConnection() throws SQLException {
/* 280 */     if (this.openConnection == null || this.openConnection.isClosed()) {
/* 281 */       this.openConnection = openConnection();
/*     */     }
/* 283 */     return this.openConnection;
/*     */   }
/*     */   
/*     */   protected Connection openConnection() throws SQLException {
/* 287 */     return DriverManager.getConnection("jdbc:sqlite:" + this.plugin.getDataFolder().getAbsolutePath() + File.separator + "violations.sqlite");
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\sqlite\SQLiteViolationDatabase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
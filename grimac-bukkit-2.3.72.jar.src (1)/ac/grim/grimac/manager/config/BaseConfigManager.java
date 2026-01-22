/*    */ package ac.grim.grimac.manager.config;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.regex.Pattern;
/*    */ import java.util.regex.PatternSyntaxException;
/*    */ import lombok.Generated;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BaseConfigManager
/*    */ {
/* 19 */   private final List<Pattern> ignoredClientPatterns = new ArrayList<>();
/* 20 */   private ConfigManager config = null; @Generated public ConfigManager getConfig() { return this.config; } private boolean printAlertsToConsole = false; @Generated
/*    */   public boolean isPrintAlertsToConsole() {
/* 22 */     return this.printAlertsToConsole;
/*    */   }
/* 24 */   private String disconnectTimeout; private String disconnectClosed; private String disconnectPacketError; private String prefix = "&bGrim &8»"; private String disconnectBlacklistedForge; private boolean blockBlacklistedForgeClients; private boolean disablePongCancelling; @Generated public String getPrefix() { return this.prefix; }
/*    */   @Generated
/* 26 */   public String getDisconnectTimeout() { return this.disconnectTimeout; }
/*    */   @Generated
/* 28 */   public String getDisconnectClosed() { return this.disconnectClosed; }
/*    */   @Generated
/* 30 */   public String getDisconnectPacketError() { return this.disconnectPacketError; }
/*    */   @Generated
/* 32 */   public String getDisconnectBlacklistedForge() { return this.disconnectBlacklistedForge; } @Generated
/*    */   public boolean isBlockBlacklistedForgeClients() {
/* 34 */     return this.blockBlacklistedForgeClients;
/*    */   } @Generated
/*    */   public boolean isDisablePongCancelling() {
/* 37 */     return this.disablePongCancelling;
/*    */   }
/*    */ 
/*    */   
/*    */   public void load(ConfigManager config) {
/* 42 */     this.config = config;
/*    */     
/* 44 */     int configuredMaxTransactionTime = config.getIntElse("max-transaction-time", 60);
/* 45 */     if (configuredMaxTransactionTime > 180 || configuredMaxTransactionTime < 1) {
/* 46 */       LogUtil.warn("Detected invalid max-transaction-time! This setting is clamped between 1 and 180 to prevent issues. Attempting to disable or set this too high can result in memory usage issues.");
/*    */     }
/*    */     
/* 49 */     this.ignoredClientPatterns.clear();
/* 50 */     for (String string : config.getStringList("client-brand.ignored-clients")) {
/*    */       try {
/* 52 */         this.ignoredClientPatterns.add(Pattern.compile(string));
/* 53 */       } catch (PatternSyntaxException e) {
/* 54 */         throw new RuntimeException("Failed to compile client pattern", e);
/*    */       } 
/*    */     } 
/*    */     
/* 58 */     this.printAlertsToConsole = config.getBooleanElse("alerts.print-to-console", true);
/* 59 */     this.prefix = config.getStringElse("prefix", "&bGrim &8»");
/*    */     
/* 61 */     this.disconnectTimeout = config.getStringElse("disconnect.timeout", "<lang:disconnect.timeout>");
/* 62 */     this.disconnectClosed = config.getStringElse("disconnect.closed", "<lang:disconnect.timeout>");
/* 63 */     this.disconnectPacketError = config.getStringElse("disconnect.error", "<red>An error occurred whilst processing packets. Please contact the administrators.");
/* 64 */     this.blockBlacklistedForgeClients = config.getBooleanElse("client-brand.disconnect-blacklisted-forge-versions", true);
/* 65 */     this.disconnectBlacklistedForge = config.getStringElse("disconnect.blacklisted-forge", "<red>Your forge version is blacklisted due to inbuilt reach hacks.<newline><gold>Versions affected: 1.18.2-1.19.3<newline><newline><red>Please see https://github.com/MinecraftForge/MinecraftForge/issues/9309.");
/*    */ 
/*    */     
/* 68 */     this.disablePongCancelling = config.getBooleanElse("disable-pong-cancelling", false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void start() {}
/*    */ 
/*    */   
/*    */   public boolean isIgnoredClient(String brand) {
/* 76 */     for (Pattern pattern : this.ignoredClientPatterns) {
/* 77 */       if (pattern.matcher(brand).find()) return true; 
/*    */     } 
/* 79 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\config\BaseConfigManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
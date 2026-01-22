/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ public class JavaVersion
/*    */   implements StartableInitable
/*    */ {
/*    */   public void start() {
/*    */     int version;
/* 13 */     String javaVersion = System.getProperty("java.version");
/* 14 */     Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);
/* 15 */     if (!matcher.find()) {
/* 16 */       LogUtil.error("Failed to determine Java version; could not parse: " + javaVersion);
/*    */       
/*    */       return;
/*    */     } 
/* 20 */     String versionString = matcher.group(1);
/*    */     
/*    */     try {
/* 23 */       version = Integer.parseInt(versionString);
/* 24 */     } catch (NumberFormatException e) {
/* 25 */       LogUtil.error("Failed to determine Java version; could not parse: " + versionString, e);
/*    */       
/*    */       return;
/*    */     } 
/* 29 */     if (version < 17) {
/* 30 */       LogUtil.warn("You are running an outdated Java version, please update it to at least Java 17 (your version is " + javaVersion + ").");
/* 31 */       LogUtil.warn("GrimAC will no longer support this version of Java in a future release.");
/* 32 */       LogUtil.warn("See https://github.com/GrimAnticheat/Grim/wiki/Updating-to-Java-17 for more information.");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\JavaVersion.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
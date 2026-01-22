/*    */ package ac.grim.grimac.utils.anticheat;
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
/*    */ import java.util.logging.Logger;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class LogUtil {
/*    */   @Generated
/*    */   private LogUtil() {
/* 12 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static void info(String info) {
/* 14 */     getLogger().info(info);
/*    */   }
/*    */   
/*    */   public static void warn(String warn) {
/* 18 */     getLogger().warning(warn);
/*    */   }
/*    */   
/*    */   public static void error(String error) {
/* 22 */     getLogger().severe(error);
/*    */   }
/*    */   
/*    */   public static void error(String description, Throwable throwable) {
/* 26 */     Logger logger = getLogger();
/* 27 */     if (logger != null) {
/* 28 */       logger.severe(description + ": " + description);
/*    */     } else {
/* 30 */       throwable.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void error(Throwable throwable) {
/* 35 */     Logger logger = getLogger();
/* 36 */     if (logger != null) {
/* 37 */       logger.severe(getStackTrace(throwable));
/*    */     } else {
/* 39 */       throwable.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Logger getLogger() {
/* 44 */     return GrimAPI.INSTANCE.getGrimPlugin().getLogger();
/*    */   }
/*    */   
/*    */   public static void console(String info) {
/* 48 */     GrimAPI.INSTANCE.getPlatformServer().getConsoleSender().sendMessage(MessageUtil.translateAlternateColorCodes('&', info));
/*    */   }
/*    */   
/*    */   public static void console(Component info) {
/* 52 */     GrimAPI.INSTANCE.getPlatformServer().getConsoleSender().sendMessage(info);
/*    */   }
/*    */   
/*    */   private static String getStackTrace(Throwable throwable) {
/* 56 */     String message = throwable.getMessage(); 
/* 57 */     try { StringWriter sw = new StringWriter(); 
/* 58 */       try { PrintWriter pw = new PrintWriter(sw); 
/* 59 */         try { throwable.printStackTrace(pw);
/* 60 */           message = sw.toString();
/* 61 */           pw.close(); } catch (Throwable throwable1) { try { pw.close(); } catch (Throwable throwable2) { throwable1.addSuppressed(throwable2); }  throw throwable1; }
/* 62 */          sw.close(); } catch (Throwable throwable1) { try { sw.close(); } catch (Throwable throwable2) { throwable1.addSuppressed(throwable2); }  throw throwable1; }  } catch (Exception exception) {}
/*    */     
/* 64 */     return message;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\anticheat\LogUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
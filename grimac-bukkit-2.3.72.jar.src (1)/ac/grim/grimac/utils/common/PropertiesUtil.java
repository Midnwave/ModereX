/*    */ package ac.grim.grimac.utils.common;
/*    */ 
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.io.InputStream;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ public class PropertiesUtil
/*    */ {
/*    */   public static Properties readProperties(Class<?> clazz, String path) {
/* 11 */     Properties properties = new Properties(); 
/* 12 */     try { InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path); 
/* 13 */       try { if (inputStream != null) {
/* 14 */           properties.load(inputStream);
/*    */         } else {
/* 16 */           throw new RuntimeException("Cannot find properties file: " + path);
/*    */         } 
/* 18 */         if (inputStream != null) inputStream.close();  } catch (Throwable throwable) { if (inputStream != null) try { inputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (Exception e)
/* 19 */     { LogUtil.error(e); }
/*    */     
/* 21 */     return properties;
/*    */   }
/*    */   
/*    */   public static String getPropertyOrElse(Properties properties, String key, String defaultValue) {
/* 25 */     return properties.getProperty(key, defaultValue);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\common\PropertiesUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
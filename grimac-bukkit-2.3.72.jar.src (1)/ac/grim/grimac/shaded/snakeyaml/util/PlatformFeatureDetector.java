/*    */ package ac.grim.grimac.shaded.snakeyaml.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlatformFeatureDetector
/*    */ {
/* 18 */   private Boolean isRunningOnAndroid = null;
/*    */   
/*    */   public boolean isRunningOnAndroid() {
/* 21 */     if (this.isRunningOnAndroid == null) {
/* 22 */       String name = System.getProperty("java.runtime.name");
/* 23 */       this.isRunningOnAndroid = Boolean.valueOf((name != null && name.startsWith("Android Runtime")));
/*    */     } 
/* 25 */     return this.isRunningOnAndroid.booleanValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyam\\util\PlatformFeatureDetector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
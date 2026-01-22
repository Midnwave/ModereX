/*    */ package ac.grim.grimac.shaded.configuralize;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class Source
/*    */ {
/*    */   private final DynamicConfig config;
/*    */   private final Class<?> clazz;
/*    */   private final String resource;
/*    */   private final File file;
/*    */   
/*    */   public Source(DynamicConfig config, Class<?> clazz, String resource, File file) {
/* 15 */     this.config = config;
/* 16 */     this.clazz = clazz;
/* 17 */     this.resource = resource;
/* 18 */     this.file = file.getAbsoluteFile();
/*    */   }
/*    */   
/*    */   public String getResourcePath() {
/* 22 */     return getResourcePath(this.config.getLanguage());
/*    */   }
/*    */   
/*    */   public String getResourcePath(Language language) {
/* 26 */     return "/" + this.resource + "/" + language.getCode().toLowerCase() + "." + this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
/*    */   }
/*    */   
/*    */   public URL getResource() {
/* 30 */     return getResource(this.config.getLanguage());
/*    */   }
/*    */   
/*    */   public URL getResource(Language language) {
/* 34 */     return this.clazz.getResource(getResourcePath(language));
/*    */   }
/*    */   
/*    */   public String getResourceName() {
/* 38 */     return this.resource;
/*    */   }
/*    */   
/*    */   public boolean isLanguageAvailable() {
/* 42 */     return isLanguageAvailable(this.config.getLanguage());
/*    */   }
/*    */   
/*    */   public boolean isLanguageAvailable(Language language) {
/*    */     try {
/* 47 */       InputStream stream = getResource(language).openStream();
/* 48 */       stream.close();
/* 49 */       return true;
/* 50 */     } catch (Exception e) {
/* 51 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   public File getFile() {
/* 56 */     return this.file;
/*    */   }
/*    */   
/*    */   public Class<?> getClazz() {
/* 60 */     return this.clazz;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\Source.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
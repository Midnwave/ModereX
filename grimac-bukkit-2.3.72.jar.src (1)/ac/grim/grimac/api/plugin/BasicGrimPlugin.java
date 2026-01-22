/*    */ package ac.grim.grimac.api.plugin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.io.File;
/*    */ import java.util.Collection;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class BasicGrimPlugin
/*    */   implements GrimPlugin {
/*    */   private final Logger logger;
/*    */   private final File dataFolder;
/*    */   private final BasicGrimPluginDescription description;
/*    */   
/*    */   public BasicGrimPlugin(Logger logger, File dataFolder, String version, String description, Collection<String> authors) {
/* 15 */     this.logger = logger;
/* 16 */     this.dataFolder = dataFolder;
/* 17 */     this.description = new BasicGrimPluginDescription(version, description, authors);
/*    */   }
/*    */ 
/*    */   
/*    */   public GrimPluginDescription getDescription() {
/* 22 */     return this.description;
/*    */   }
/*    */ 
/*    */   
/*    */   public Logger getLogger() {
/* 27 */     return this.logger;
/*    */   }
/*    */ 
/*    */   
/*    */   public File getDataFolder() {
/* 32 */     return this.dataFolder;
/*    */   }
/*    */   
/*    */   private static class BasicGrimPluginDescription
/*    */     implements GrimPluginDescription {
/*    */     private final String version;
/*    */     private final String description;
/*    */     private final Collection<String> authors;
/*    */     
/*    */     public BasicGrimPluginDescription(String version, String description, Collection<String> authors) {
/* 42 */       this.version = version;
/* 43 */       this.description = description;
/* 44 */       this.authors = authors;
/*    */     }
/*    */ 
/*    */     
/*    */     public String getVersion() {
/* 49 */       return this.version;
/*    */     }
/*    */ 
/*    */     
/*    */     public String getDescription() {
/* 54 */       return this.description;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public Collection<String> getAuthors() {
/* 59 */       return this.authors;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\plugin\BasicGrimPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
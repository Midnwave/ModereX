/*    */ package ac.grim.grimac.shaded.snakeyaml.parser;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ class VersionTagsTuple
/*    */ {
/*    */   private final DumperOptions.Version version;
/*    */   private final Map<String, String> tags;
/*    */   
/*    */   public VersionTagsTuple(DumperOptions.Version version, Map<String, String> tags) {
/* 28 */     this.version = version;
/* 29 */     this.tags = tags;
/*    */   }
/*    */   
/*    */   public DumperOptions.Version getVersion() {
/* 33 */     return this.version;
/*    */   }
/*    */   
/*    */   public Map<String, String> getTags() {
/* 37 */     return this.tags;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     return String.format("VersionTagsTuple<%s, %s>", new Object[] { this.version, this.tags });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\parser\VersionTagsTuple.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
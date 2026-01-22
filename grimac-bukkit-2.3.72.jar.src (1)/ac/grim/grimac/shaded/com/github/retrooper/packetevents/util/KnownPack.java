/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
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
/*    */ 
/*    */ 
/*    */ public class KnownPack
/*    */ {
/*    */   private final String namespace;
/*    */   private final String id;
/*    */   private final String version;
/*    */   
/*    */   public KnownPack(String namespace, String id, String version) {
/* 28 */     this.namespace = namespace;
/* 29 */     this.id = id;
/* 30 */     this.version = version;
/*    */   }
/*    */   
/*    */   public String getNamespace() {
/* 34 */     return this.namespace;
/*    */   }
/*    */   
/*    */   public String getId() {
/* 38 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getVersion() {
/* 42 */     return this.version;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 47 */     if (this == obj) return true; 
/* 48 */     if (!(obj instanceof KnownPack)) return false; 
/* 49 */     KnownPack knownPack = (KnownPack)obj;
/* 50 */     if (!this.namespace.equals(knownPack.namespace)) return false; 
/* 51 */     if (!this.id.equals(knownPack.id)) return false; 
/* 52 */     return this.version.equals(knownPack.version);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     int result = this.namespace.hashCode();
/* 58 */     result = 31 * result + this.id.hashCode();
/* 59 */     result = 31 * result + this.version.hashCode();
/* 60 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "KnownPack{namespace='" + this.namespace + '\'' + ", id='" + this.id + '\'' + ", version='" + this.version + '\'' + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\KnownPack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
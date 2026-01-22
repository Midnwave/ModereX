/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*    */ public class VersionMapper
/*    */ {
/*    */   private final ClientVersion[] versions;
/*    */   private final ClientVersion[] reversedVersions;
/*    */   
/*    */   public VersionMapper(ClientVersion... versions) {
/* 28 */     this.versions = versions;
/* 29 */     this.reversedVersions = new ClientVersion[versions.length];
/* 30 */     int index = 0;
/* 31 */     for (int i = versions.length - 1; i >= 0; i--) {
/* 32 */       this.reversedVersions[index] = versions[i];
/* 33 */       index++;
/*    */     } 
/*    */   }
/*    */   
/*    */   public ClientVersion[] getVersions() {
/* 38 */     return this.versions;
/*    */   }
/*    */   
/*    */   public ClientVersion[] getReversedVersions() {
/* 42 */     return this.reversedVersions;
/*    */   }
/*    */   
/*    */   public int getIndex(ClientVersion version) {
/* 46 */     int index = this.reversedVersions.length - 1;
/* 47 */     for (ClientVersion v : this.reversedVersions) {
/* 48 */       if (version.isNewerThanOrEquals(v)) {
/* 49 */         return index;
/*    */       }
/* 51 */       index--;
/*    */     } 
/*    */     
/* 54 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\VersionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
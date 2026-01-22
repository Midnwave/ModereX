/*     */ package ac.grim.grimac.command.commands;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SemVer
/*     */ {
/*     */   public static GrimVersion.Status getVersionStatus(String current, String latest) {
/*     */     try {
/* 175 */       int cmp = compareSemver(current, latest);
/* 176 */       if (cmp == 0) {
/* 177 */         return GrimVersion.Status.UPDATED;
/*     */       }
/* 179 */       if (cmp < 0) {
/* 180 */         return GrimVersion.Status.OUTDATED;
/*     */       }
/* 182 */       return GrimVersion.Status.AHEAD;
/* 183 */     } catch (Exception exception) {
/*     */       
/* 185 */       return GrimVersion.Status.UNKNOWN;
/*     */     } 
/*     */   }
/*     */   public static String normalizeCoreVersion(String version) {
/* 189 */     String trimmed = version.trim();
/* 190 */     String[] dashParts = trimmed.split("-");
/* 191 */     String[] plusParts = dashParts[0].split("\\+");
/* 192 */     return plusParts[0];
/*     */   }
/*     */   
/*     */   public static int[] parseVersion(String version) {
/* 196 */     String core = normalizeCoreVersion(version);
/* 197 */     if (core.isEmpty()) return null; 
/* 198 */     String[] parts = core.split("\\.");
/* 199 */     if (parts.length < 1) return null;
/*     */     
/* 201 */     int major = parseInt(parts[0]);
/* 202 */     int minor = (parts.length > 1) ? parseInt(parts[1]) : 0;
/* 203 */     int patch = (parts.length > 2) ? parseInt(parts[2]) : 0;
/*     */     
/* 205 */     if (major < 0 || minor < 0 || patch < 0) {
/* 206 */       return null;
/*     */     }
/*     */     
/* 209 */     return new int[] { major, minor, patch };
/*     */   }
/*     */   
/*     */   private static int parseInt(String str) {
/*     */     try {
/* 214 */       return Integer.parseInt(str);
/* 215 */     } catch (NumberFormatException e) {
/* 216 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static int compareSemver(String a, String b) {
/* 221 */     int[] pa = parseVersion(a);
/* 222 */     int[] pb = parseVersion(b);
/* 223 */     if (pa == null || pb == null) return 0;
/*     */     
/* 225 */     for (int i = 0; i < 3; i++) {
/* 226 */       if (pa[i] < pb[i]) return -1; 
/* 227 */       if (pa[i] > pb[i]) return 1; 
/*     */     } 
/* 229 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimVersion$Status$SemVer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
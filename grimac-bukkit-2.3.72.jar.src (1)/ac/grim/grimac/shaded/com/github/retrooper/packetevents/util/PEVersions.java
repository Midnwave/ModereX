/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import java.time.Instant;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PEVersions
/*    */ {
/*    */   public static final String RAW = "2.9.6+4519ccba2-SNAPSHOT";
/* 11 */   public static final Instant BUILD_TIMESTAMP = Instant.ofEpochMilli(1758014823706L);
/* 12 */   public static final PEVersion CURRENT = new PEVersion(2, 9, 6, "4519ccba2");
/* 13 */   public static final PEVersion UNKNOWN = new PEVersion(0, 0, 0);
/*    */   
/*    */   private PEVersions() {
/* 16 */     throw new IllegalStateException();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\PEVersions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
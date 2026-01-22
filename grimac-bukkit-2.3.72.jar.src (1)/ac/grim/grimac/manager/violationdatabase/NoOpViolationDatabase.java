/*    */ package ac.grim.grimac.manager.violationdatabase;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public final class NoOpViolationDatabase implements ViolationDatabase {
/*    */   public void connect() {}
/*    */   
/*    */   public void disconnect() {}
/*    */   
/*  9 */   public static final NoOpViolationDatabase INSTANCE = new NoOpViolationDatabase();
/*    */ 
/*    */   
/*    */   public void logAlert(GrimPlayer p, String grimVersion, String v, String c, int vl) {}
/*    */   
/*    */   public int getLogCount(UUID player) {
/* 15 */     return 0; } public List<Violation> getViolations(UUID p, int page, int lim) {
/* 16 */     return List.of();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\NoOpViolationDatabase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.platform.api.player;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class PlatformPlayerCache {
/*  8 */   private static final PlatformPlayerCache INSTANCE = new PlatformPlayerCache();
/*  9 */   private final Map<UUID, PlatformPlayer> playerCache = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static PlatformPlayerCache getInstance() {
/* 16 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PlatformPlayer addOrGetPlayer(UUID uuid, PlatformPlayer player) {
/* 27 */     return this.playerCache.compute(uuid, (key, existing) -> (existing != null) ? existing : player);
/*    */   }
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
/*    */   public void removePlayer(UUID uuid) {
/* 41 */     this.playerCache.remove(uuid);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PlatformPlayer getPlayer(UUID uuid) {
/* 51 */     return this.playerCache.get(uuid);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\player\PlatformPlayerCache.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
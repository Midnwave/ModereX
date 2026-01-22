/*    */ package ac.grim.grimac.platform.api.player;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ public abstract class AbstractPlatformPlayerFactory<T>
/*    */   implements PlatformPlayerFactory
/*    */ {
/* 13 */   protected final PlatformPlayerCache cache = PlatformPlayerCache.getInstance();
/*    */ 
/*    */ 
/*    */   
/*    */   public final PlatformPlayer getFromUUID(UUID uuid) {
/* 18 */     PlatformPlayer cachedPlayer = this.cache.getPlayer(uuid);
/* 19 */     if (cachedPlayer != null) {
/* 20 */       return cachedPlayer;
/*    */     }
/*    */ 
/*    */     
/* 24 */     T nativePlayer = getNativePlayer(uuid);
/* 25 */     if (nativePlayer == null) {
/* 26 */       return null;
/*    */     }
/*    */ 
/*    */     
/* 30 */     PlatformPlayer platformPlayer = createPlatformPlayer(nativePlayer);
/* 31 */     return this.cache.addOrGetPlayer(uuid, platformPlayer);
/*    */   }
/*    */ 
/*    */   
/*    */   public PlatformPlayer getFromName(String name) {
/* 36 */     T nativePlayer = getNativePlayer(name);
/* 37 */     if (nativePlayer == null) {
/* 38 */       return null;
/*    */     }
/*    */ 
/*    */     
/* 42 */     PlatformPlayer platformPlayer = createPlatformPlayer(nativePlayer);
/* 43 */     return this.cache.addOrGetPlayer(platformPlayer.getUniqueId(), platformPlayer);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public final PlatformPlayer getFromNativePlayerType(Object playerObject) {
/* 49 */     T nativePlayer = Objects.requireNonNull((T)playerObject);
/* 50 */     UUID uuid = getPlayerUUID(nativePlayer);
/*    */ 
/*    */     
/* 53 */     PlatformPlayer cachedPlayer = this.cache.getPlayer(uuid);
/* 54 */     if (cachedPlayer != null) {
/* 55 */       return cachedPlayer;
/*    */     }
/*    */ 
/*    */     
/* 59 */     PlatformPlayer platformPlayer = createPlatformPlayer(nativePlayer);
/* 60 */     return this.cache.addOrGetPlayer(uuid, platformPlayer);
/*    */   }
/*    */ 
/*    */   
/*    */   public final void invalidatePlayer(UUID uuid) {
/* 65 */     this.cache.removePlayer(uuid);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<PlatformPlayer> getOnlinePlayers() {
/* 70 */     Collection<T> nativePlayers = getNativeOnlinePlayers();
/*    */ 
/*    */     
/* 73 */     List<PlatformPlayer> platformPlayers = new ArrayList<>(nativePlayers.size());
/*    */     
/* 75 */     for (T nativePlayer : nativePlayers) {
/* 76 */       platformPlayers.add(getFromNativePlayerType(nativePlayer));
/*    */     }
/*    */     
/* 79 */     return platformPlayers;
/*    */   }
/*    */   
/*    */   public void replaceNativePlayer(UUID uuid, T player) {}
/*    */   
/*    */   protected abstract T getNativePlayer(UUID paramUUID);
/*    */   
/*    */   protected abstract T getNativePlayer(String paramString);
/*    */   
/*    */   protected abstract PlatformPlayer createPlatformPlayer(T paramT);
/*    */   
/*    */   protected abstract UUID getPlayerUUID(T paramT);
/*    */   
/*    */   protected abstract Collection<T> getNativeOnlinePlayers();
/*    */   
/*    */   public abstract OfflinePlatformPlayer getOfflineFromUUID(UUID paramUUID);
/*    */   
/*    */   public abstract OfflinePlatformPlayer getOfflineFromName(String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\player\AbstractPlatformPlayerFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
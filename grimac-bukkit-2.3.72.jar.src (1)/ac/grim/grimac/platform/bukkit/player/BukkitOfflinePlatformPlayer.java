/*    */ package ac.grim.grimac.platform.bukkit.player;
/*    */ import ac.grim.grimac.platform.api.player.OfflinePlatformPlayer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Objects;
/*    */ import org.bukkit.OfflinePlayer;
/*    */ 
/*    */ public class BukkitOfflinePlatformPlayer implements OfflinePlatformPlayer {
/*    */   @Generated
/*    */   public BukkitOfflinePlatformPlayer(OfflinePlayer offlinePlayer) {
/* 10 */     this.offlinePlayer = offlinePlayer;
/*    */   }
/*    */   
/*    */   private final OfflinePlayer offlinePlayer;
/*    */   
/*    */   public boolean isOnline() {
/* 16 */     return this.offlinePlayer.isOnline();
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getName() {
/* 21 */     return Objects.<String>requireNonNull(this.offlinePlayer.getName());
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public UUID getUniqueId() {
/* 26 */     return this.offlinePlayer.getUniqueId();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 31 */     if (o instanceof OfflinePlatformPlayer) { OfflinePlatformPlayer player = (OfflinePlatformPlayer)o; if (getUniqueId().equals(player.getUniqueId())); }  return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\player\BukkitOfflinePlatformPlayer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
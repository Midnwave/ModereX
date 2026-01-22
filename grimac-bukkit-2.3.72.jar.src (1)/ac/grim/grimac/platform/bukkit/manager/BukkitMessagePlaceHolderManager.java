/*    */ package ac.grim.grimac.platform.bukkit.manager;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.manager.MessagePlaceHolderManager;
/*    */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*    */ import ac.grim.grimac.platform.bukkit.player.BukkitPlatformPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.Reflection;
/*    */ import me.clip.placeholderapi.PlaceholderAPI;
/*    */ 
/*    */ public class BukkitMessagePlaceHolderManager
/*    */   implements MessagePlaceHolderManager
/*    */ {
/* 12 */   public static final boolean hasPlaceholderAPI = (Reflection.getClassByNameWithoutException("me.clip.placeholderapi.PlaceholderAPI") != null);
/*    */ 
/*    */   
/*    */   public String replacePlaceholders(PlatformPlayer player, String string) {
/* 16 */     if (!hasPlaceholderAPI) return string; 
/* 17 */     BukkitPlatformPlayer bukkitPlatformPlayer = (BukkitPlatformPlayer)player; return PlaceholderAPI.setPlaceholders((player instanceof BukkitPlatformPlayer) ? bukkitPlatformPlayer.getBukkitPlayer() : null, string);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\manager\BukkitMessagePlaceHolderManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
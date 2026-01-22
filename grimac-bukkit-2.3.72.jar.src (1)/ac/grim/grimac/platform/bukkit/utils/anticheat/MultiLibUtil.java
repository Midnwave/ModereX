/*    */ package ac.grim.grimac.platform.bukkit.utils.anticheat;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.reflection.ReflectionUtils;
/*    */ import java.lang.reflect.Method;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ public class MultiLibUtil
/*    */ {
/* 13 */   public static final Method externalPlayerMethod = ReflectionUtils.getMethod(Player.class, "isExternalPlayer", new Class[0]);
/* 14 */   private static final boolean IS_PRE_1_18 = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_18);
/*    */ 
/*    */   
/*    */   public static boolean isExternalPlayer(Player player) {
/* 18 */     if (externalPlayerMethod == null || IS_PRE_1_18) return false; 
/*    */     try {
/* 20 */       return ((Boolean)externalPlayerMethod.invoke(player, new Object[0])).booleanValue();
/* 21 */     } catch (Exception e) {
/* 22 */       LogUtil.error("Failed to invoke external player method", e);
/* 23 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukki\\utils\anticheat\MultiLibUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
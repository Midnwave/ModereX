/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.protocolsupport;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import java.net.SocketAddress;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ import protocolsupport.api.ProtocolSupportAPI;
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
/*    */ public class ProtocolSupportUtil
/*    */ {
/* 29 */   private static ProtocolSupportState available = ProtocolSupportState.UNKNOWN;
/*    */   
/*    */   public static boolean isAvailable() {
/* 32 */     if (available == ProtocolSupportState.UNKNOWN) {
/*    */       try {
/* 34 */         ClassLoader classLoader = PacketEvents.getAPI().getPlugin().getClass().getClassLoader();
/* 35 */         classLoader.loadClass("protocolsupport.api.ProtocolSupportAPI");
/* 36 */         available = ProtocolSupportState.ENABLED;
/* 37 */         return true;
/* 38 */       } catch (Exception e) {
/* 39 */         available = ProtocolSupportState.DISABLED;
/* 40 */         return false;
/*    */       } 
/*    */     }
/* 43 */     return (available == ProtocolSupportState.ENABLED);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void checkIfProtocolSupportIsPresent() {
/* 48 */     boolean present = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
/* 49 */     available = present ? ProtocolSupportState.ENABLED : ProtocolSupportState.DISABLED;
/*    */   }
/*    */   
/*    */   public static int getProtocolVersion(SocketAddress address) {
/* 53 */     return ProtocolSupportAPI.getProtocolVersion(address).getId();
/*    */   }
/*    */   
/*    */   public static int getProtocolVersion(Player player) {
/* 57 */     return ProtocolSupportAPI.getProtocolVersion(player).getId();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\protocolsupport\ProtocolSupportUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
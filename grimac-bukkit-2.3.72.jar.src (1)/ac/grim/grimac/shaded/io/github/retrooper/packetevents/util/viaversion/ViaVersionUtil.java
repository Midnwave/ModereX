/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
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
/*    */ public class ViaVersionUtil
/*    */ {
/* 27 */   private static ViaState available = ViaState.UNKNOWN;
/*    */ 
/*    */   
/*    */   private static ViaVersionAccessor viaVersionAccessor;
/*    */ 
/*    */   
/*    */   private static void load() {
/* 34 */     if (viaVersionAccessor == null) {
/* 35 */       ClassLoader classLoader = PacketEvents.getAPI().getPlugin().getClass().getClassLoader();
/*    */       try {
/* 37 */         classLoader.loadClass("com.viaversion.viaversion.api.Via");
/* 38 */         viaVersionAccessor = new ViaVersionAccessorImpl();
/* 39 */       } catch (Exception e) {
/*    */         try {
/* 41 */           classLoader.loadClass("us.myles.ViaVersion.api.Via");
/* 42 */           viaVersionAccessor = new ViaVersionAccessorImplLegacy();
/* 43 */         } catch (ClassNotFoundException ex) {
/* 44 */           viaVersionAccessor = null;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void checkIfViaIsPresent() {
/* 51 */     boolean present = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
/* 52 */     available = present ? ViaState.ENABLED : ViaState.DISABLED;
/*    */   }
/*    */   
/*    */   public static boolean isAvailable() {
/* 56 */     if (available == ViaState.UNKNOWN) {
/* 57 */       return (getViaVersionAccessor() != null);
/*    */     }
/* 59 */     return (available == ViaState.ENABLED);
/*    */   }
/*    */   
/*    */   public static ViaVersionAccessor getViaVersionAccessor() {
/* 63 */     load();
/* 64 */     return viaVersionAccessor;
/*    */   }
/*    */   
/*    */   public static int getProtocolVersion(User user) {
/* 68 */     return getViaVersionAccessor().getProtocolVersion(user);
/*    */   }
/*    */   
/*    */   public static int getProtocolVersion(Player player) {
/* 72 */     return getViaVersionAccessor().getProtocolVersion(player);
/*    */   }
/*    */   
/*    */   public static Class<?> getUserConnectionClass() {
/* 76 */     return getViaVersionAccessor().getUserConnectionClass();
/*    */   }
/*    */   
/*    */   public static Class<?> getBukkitDecodeHandlerClass() {
/* 80 */     return getViaVersionAccessor().getBukkitDecodeHandlerClass();
/*    */   }
/*    */   
/*    */   public static Class<?> getBukkitEncodeHandlerClass() {
/* 84 */     return getViaVersionAccessor().getBukkitEncodeHandlerClass();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\viaversion\ViaVersionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
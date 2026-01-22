/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.reflection.ViaVersionUtil;
/*    */ import com.viaversion.viaversion.api.Via;
/*    */ 
/*    */ public class ViaVersion
/*    */   implements StartableInitable
/*    */ {
/*    */   public void start() {
/* 14 */     if (!ViaVersionUtil.isAvailable)
/*    */       return; 
/* 16 */     ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/*    */     
/* 18 */     if (Via.getConfig().getValues().containsKey("fix-1_21-placement-rotation") && Via.getConfig().fix1_21PlacementRotation() && serverVersion.isOlderThan(ServerVersion.V_1_21)) {
/* 19 */       LogUtil.error("GrimAC has detected that you are using ViaVersion with the `fix-1_21-placement-rotation` option enabled.");
/* 20 */       LogUtil.error("This option is known to cause issues with GrimAC and may result in false positives and bypasses.");
/* 21 */       LogUtil.error("Please disable this option in your ViaVersion configuration to prevent these issues.");
/*    */     } 
/*    */     
/* 24 */     if (GrimAPI.INSTANCE.getPluginManager().getPlugin("ViaBackwards") != null && serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 25 */       LogUtil.warn("GrimAC has detected that you have installed ViaBackwards on a 1.21.2+ server.");
/* 26 */       LogUtil.warn("This setup is currently unsupported and you will experience issues with older clients using vehicles.");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\ViaVersion.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
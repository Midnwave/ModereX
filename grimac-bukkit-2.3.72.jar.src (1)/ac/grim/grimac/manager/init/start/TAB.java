/*    */ package ac.grim.grimac.manager.init.start;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.reflection.ViaVersionUtil;
/*    */ 
/*    */ public class TAB
/*    */   implements StartableInitable
/*    */ {
/*    */   public void start() {
/* 13 */     if (GrimAPI.INSTANCE.getPluginManager().getPlugin("TAB") == null)
/* 14 */       return;  if (!ViaVersionUtil.isAvailable)
/*    */       return; 
/* 16 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/*    */       return;
/*    */     }
/* 19 */     LogUtil.warn("GrimAC has detected that you have installed TAB with ViaVersion.");
/* 20 */     LogUtil.warn("Please note that currently, TAB is incompatible as it sends illegal packets to players using versions newer than your server version.");
/* 21 */     LogUtil.warn("You may be able to remedy this by setting `compensate-for-packetevents-bug` to true in the TAB config.");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\TAB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
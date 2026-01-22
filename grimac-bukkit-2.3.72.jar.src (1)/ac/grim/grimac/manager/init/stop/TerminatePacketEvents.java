/*    */ package ac.grim.grimac.manager.init.stop;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ 
/*    */ public class TerminatePacketEvents
/*    */   implements StoppableInitable {
/*    */   public void stop() {
/*  9 */     LogUtil.info("Terminating PacketEvents...");
/* 10 */     PacketEvents.getAPI().terminate();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\stop\TerminatePacketEvents.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
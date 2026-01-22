/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
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
/*    */ public abstract class PacketListenerAbstract
/*    */   extends PacketListenerCommon
/*    */ {
/*    */   public PacketListenerAbstract(PacketListenerPriority priority) {
/* 23 */     super(priority);
/*    */   }
/*    */   
/*    */   public PacketListenerAbstract() {}
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {}
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\PacketListenerAbstract.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
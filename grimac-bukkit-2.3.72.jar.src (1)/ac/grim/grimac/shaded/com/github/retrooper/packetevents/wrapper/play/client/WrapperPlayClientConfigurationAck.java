/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class WrapperPlayClientConfigurationAck
/*    */   extends PacketWrapper<WrapperPlayClientConfigurationAck>
/*    */ {
/*    */   public WrapperPlayClientConfigurationAck(PacketReceiveEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientConfigurationAck() {
/* 32 */     super((PacketTypeCommon)PacketType.Play.Client.CONFIGURATION_ACK);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientConfigurationAck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
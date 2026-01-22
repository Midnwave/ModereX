/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.client;
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
/*    */ public class WrapperConfigClientConfigurationEndAck
/*    */   extends PacketWrapper<WrapperConfigClientConfigurationEndAck>
/*    */ {
/*    */   public WrapperConfigClientConfigurationEndAck(PacketReceiveEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigClientConfigurationEndAck() {
/* 32 */     super((PacketTypeCommon)PacketType.Configuration.Client.CONFIGURATION_END_ACK);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\client\WrapperConfigClientConfigurationEndAck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
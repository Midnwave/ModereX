/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
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
/*    */ public class WrapperPlayServerConfigurationStart
/*    */   extends PacketWrapper<WrapperPlayServerConfigurationStart>
/*    */ {
/*    */   public WrapperPlayServerConfigurationStart(PacketSendEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerConfigurationStart() {
/* 32 */     super((PacketTypeCommon)PacketType.Play.Server.CONFIGURATION_START);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerConfigurationStart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
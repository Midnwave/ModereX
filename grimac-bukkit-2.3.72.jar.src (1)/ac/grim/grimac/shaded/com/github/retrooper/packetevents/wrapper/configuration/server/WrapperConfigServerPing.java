/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerPing;
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
/*    */ 
/*    */ public class WrapperConfigServerPing
/*    */   extends WrapperCommonServerPing<WrapperConfigServerPing>
/*    */ {
/*    */   public WrapperConfigServerPing(PacketSendEvent event) {
/* 31 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigServerPing(int id) {
/* 35 */     super((PacketTypeCommon)PacketType.Configuration.Server.PING, id);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\server\WrapperConfigServerPing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
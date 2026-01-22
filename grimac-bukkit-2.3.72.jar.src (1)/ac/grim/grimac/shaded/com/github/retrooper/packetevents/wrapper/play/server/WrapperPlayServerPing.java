/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
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
/*    */ public class WrapperPlayServerPing
/*    */   extends WrapperCommonServerPing<WrapperPlayServerPing>
/*    */ {
/*    */   public WrapperPlayServerPing(PacketSendEvent event) {
/* 31 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerPing(int id) {
/* 35 */     super((PacketTypeCommon)PacketType.Play.Server.PING, id);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerPing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerServerLinks;
/*    */ import java.util.List;
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
/*    */ public class WrapperPlayServerServerLinks
/*    */   extends WrapperCommonServerServerLinks<WrapperPlayServerServerLinks>
/*    */ {
/*    */   public WrapperPlayServerServerLinks(PacketSendEvent event) {
/* 30 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerServerLinks(List<WrapperCommonServerServerLinks.ServerLink> links) {
/* 34 */     super((PacketTypeCommon)PacketType.Play.Server.SERVER_LINKS, links);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerServerLinks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
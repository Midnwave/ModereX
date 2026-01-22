/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server;
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
/*    */ public class WrapperConfigServerServerLinks
/*    */   extends WrapperCommonServerServerLinks<WrapperConfigServerServerLinks>
/*    */ {
/*    */   public WrapperConfigServerServerLinks(PacketSendEvent event) {
/* 30 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigServerServerLinks(List<WrapperCommonServerServerLinks.ServerLink> links) {
/* 34 */     super((PacketTypeCommon)PacketType.Configuration.Server.SERVER_LINKS, links);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\server\WrapperConfigServerServerLinks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerCustomReportDetails;
/*    */ import java.util.Map;
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
/*    */ public class WrapperPlayServerCustomReportDetails
/*    */   extends WrapperCommonServerCustomReportDetails<WrapperPlayServerCustomReportDetails>
/*    */ {
/*    */   public WrapperPlayServerCustomReportDetails(PacketSendEvent event) {
/* 30 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerCustomReportDetails(Map<String, String> details) {
/* 34 */     super((PacketTypeCommon)PacketType.Play.Server.CUSTOM_REPORT_DETAILS, details);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerCustomReportDetails.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
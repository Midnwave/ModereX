/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server;
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
/*    */ public class WrapperConfigServerCustomReportDetails
/*    */   extends WrapperCommonServerCustomReportDetails<WrapperConfigServerCustomReportDetails>
/*    */ {
/*    */   public WrapperConfigServerCustomReportDetails(PacketSendEvent event) {
/* 30 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigServerCustomReportDetails(Map<String, String> details) {
/* 34 */     super((PacketTypeCommon)PacketType.Configuration.Server.CUSTOM_REPORT_DETAILS, details);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\server\WrapperConfigServerCustomReportDetails.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
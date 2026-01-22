/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerClearDialog;
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
/*    */ public class WrapperConfigServerClearDialog
/*    */   extends WrapperCommonServerClearDialog<WrapperConfigServerClearDialog>
/*    */ {
/*    */   public WrapperConfigServerClearDialog(PacketSendEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigServerClearDialog() {
/* 32 */     super((PacketTypeCommon)PacketType.Configuration.Server.CLEAR_DIALOG);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\server\WrapperConfigServerClearDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
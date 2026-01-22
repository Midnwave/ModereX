/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialog;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerShowDialog;
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
/*    */ public class WrapperConfigServerShowDialog
/*    */   extends WrapperCommonServerShowDialog<WrapperConfigServerShowDialog>
/*    */ {
/*    */   public WrapperConfigServerShowDialog(PacketSendEvent event) {
/* 29 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperConfigServerShowDialog(Dialog dialog) {
/* 33 */     super((PacketTypeCommon)PacketType.Configuration.Server.SHOW_DIALOG, dialog);
/*    */   }
/*    */ 
/*    */   
/*    */   public void read() {
/* 38 */     this.dialog = Dialog.readDirect((PacketWrapper)this);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write() {
/* 43 */     Dialog.writeDirect((PacketWrapper)this, this.dialog);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\configuration\server\WrapperConfigServerShowDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
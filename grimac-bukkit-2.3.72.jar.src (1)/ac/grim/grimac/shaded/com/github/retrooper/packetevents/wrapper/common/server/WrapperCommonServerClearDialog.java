/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
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
/*    */ 
/*    */ public abstract class WrapperCommonServerClearDialog<T extends WrapperCommonServerClearDialog<T>>
/*    */   extends PacketWrapper<T>
/*    */ {
/*    */   public WrapperCommonServerClearDialog(PacketSendEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperCommonServerClearDialog(PacketTypeCommon packetType) {
/* 32 */     super(packetType);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\common\server\WrapperCommonServerClearDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
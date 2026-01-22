/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.login.client;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
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
/*    */ public class WrapperLoginClientLoginSuccessAck
/*    */   extends PacketWrapper<WrapperLoginClientLoginSuccessAck>
/*    */ {
/*    */   public WrapperLoginClientLoginSuccessAck(PacketReceiveEvent event) {
/* 28 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperLoginClientLoginSuccessAck() {
/* 32 */     super((PacketTypeCommon)PacketType.Login.Client.LOGIN_SUCCESS_ACK);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\login\client\WrapperLoginClientLoginSuccessAck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
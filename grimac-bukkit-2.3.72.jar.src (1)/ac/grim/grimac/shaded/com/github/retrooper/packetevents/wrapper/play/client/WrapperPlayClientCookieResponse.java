/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.common.client.WrapperCommonCookieResponse;
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
/*    */ public class WrapperPlayClientCookieResponse
/*    */   extends WrapperCommonCookieResponse<WrapperPlayClientCookieResponse>
/*    */ {
/*    */   @Deprecated
/*    */   public WrapperPlayClientCookieResponse(PacketSendEvent event) {
/* 32 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientCookieResponse(PacketReceiveEvent event) {
/* 36 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientCookieResponse(ResourceLocation key, byte[] payload) {
/* 40 */     super((PacketTypeCommon)PacketType.Play.Client.COOKIE_RESPONSE, key, payload);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientCookieResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
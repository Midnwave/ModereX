/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WrapperPlayClientPlayerLoaded
/*    */   extends PacketWrapper<WrapperPlayClientPlayerLoaded>
/*    */ {
/*    */   public WrapperPlayClientPlayerLoaded(PacketReceiveEvent event) {
/* 31 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientPlayerLoaded() {
/* 35 */     super((PacketTypeCommon)PacketType.Play.Client.PLAYER_LOADED);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientPlayerLoaded.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
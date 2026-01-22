/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
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
/*    */ public class WrapperPlayServerBundle
/*    */   extends PacketWrapper<WrapperPlayServerBundle>
/*    */ {
/*    */   public WrapperPlayServerBundle(PacketSendEvent event) {
/* 27 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerBundle() {
/* 31 */     super((PacketTypeCommon)PacketType.Play.Server.BUNDLE);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
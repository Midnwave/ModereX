/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Location;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
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
/*    */ public class WrapperPlayClientPlayerPosition
/*    */   extends WrapperPlayClientPlayerFlying
/*    */ {
/*    */   public WrapperPlayClientPlayerPosition(PacketReceiveEvent event) {
/* 27 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientPlayerPosition(Vector3d position, boolean onGround) {
/* 31 */     super(true, false, onGround, new Location(position, 0.0F, 0.0F));
/*    */   }
/*    */   
/*    */   public Vector3d getPosition() {
/* 35 */     return getLocation().getPosition();
/*    */   }
/*    */   
/*    */   public void setPosition(Vector3d position) {
/* 39 */     getLocation().setPosition(position);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientPlayerPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
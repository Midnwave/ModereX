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
/*    */ public class WrapperPlayClientPlayerPositionAndRotation
/*    */   extends WrapperPlayClientPlayerFlying
/*    */ {
/*    */   public WrapperPlayClientPlayerPositionAndRotation(PacketReceiveEvent event) {
/* 27 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientPlayerPositionAndRotation(Vector3d position, float yaw, float pitch, boolean onGround) {
/* 31 */     super(true, true, onGround, new Location(position, yaw, pitch));
/*    */   }
/*    */   
/*    */   public WrapperPlayClientPlayerPositionAndRotation(Location location, boolean onGround) {
/* 35 */     super(true, true, onGround, location);
/*    */   }
/*    */   
/*    */   public Vector3d getPosition() {
/* 39 */     return getLocation().getPosition();
/*    */   }
/*    */   
/*    */   public void setPosition(Vector3d position) {
/* 43 */     getLocation().setPosition(position);
/*    */   }
/*    */   
/*    */   public float getYaw() {
/* 47 */     return getLocation().getYaw();
/*    */   }
/*    */   
/*    */   public void setYaw(float yaw) {
/* 51 */     getLocation().setYaw(yaw);
/*    */   }
/*    */   
/*    */   public float getPitch() {
/* 55 */     return getLocation().getPitch();
/*    */   }
/*    */   
/*    */   public void setPitch(float pitch) {
/* 59 */     getLocation().setPitch(pitch);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientPlayerPositionAndRotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
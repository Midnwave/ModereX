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
/*    */ public class WrapperPlayClientPlayerRotation
/*    */   extends WrapperPlayClientPlayerFlying
/*    */ {
/*    */   public WrapperPlayClientPlayerRotation(PacketReceiveEvent event) {
/* 27 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayClientPlayerRotation(float yaw, float pitch, boolean onGround) {
/* 31 */     super(false, true, onGround, new Location(new Vector3d(), yaw, pitch));
/*    */   }
/*    */   
/*    */   public float getYaw() {
/* 35 */     return getLocation().getYaw();
/*    */   }
/*    */   
/*    */   public void setYaw(float yaw) {
/* 39 */     getLocation().setYaw(yaw);
/*    */   }
/*    */   
/*    */   public float getPitch() {
/* 43 */     return getLocation().getPitch();
/*    */   }
/*    */   
/*    */   public void setPitch(float pitch) {
/* 47 */     getLocation().setPitch(pitch);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\client\WrapperPlayClientPlayerRotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
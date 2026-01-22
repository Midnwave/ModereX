/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.waypoint;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public final class AzimuthWaypointInfo
/*    */   implements WaypointInfo
/*    */ {
/*    */   private final float angle;
/*    */   
/*    */   public AzimuthWaypointInfo(float angle) {
/* 31 */     this.angle = angle;
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static AzimuthWaypointInfo read(PacketWrapper<?> wrapper) {
/* 36 */     return new AzimuthWaypointInfo(wrapper.readFloat());
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static void write(PacketWrapper<?> wrapper, WaypointInfo info) {
/* 41 */     wrapper.writeFloat(((AzimuthWaypointInfo)info).angle);
/*    */   }
/*    */ 
/*    */   
/*    */   public WaypointInfo.Type getType() {
/* 46 */     return WaypointInfo.Type.AZIMUTH;
/*    */   }
/*    */   
/*    */   public float getAngle() {
/* 50 */     return this.angle;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\AzimuthWaypointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.waypoint;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
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
/*    */ public final class Vec3iWaypointInfo
/*    */   implements WaypointInfo
/*    */ {
/*    */   private final Vector3i position;
/*    */   
/*    */   public Vec3iWaypointInfo(Vector3i position) {
/* 32 */     this.position = position;
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static Vec3iWaypointInfo read(PacketWrapper<?> wrapper) {
/* 37 */     return new Vec3iWaypointInfo(Vector3i.read(wrapper));
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static void write(PacketWrapper<?> wrapper, WaypointInfo info) {
/* 42 */     Vector3i.write(wrapper, ((Vec3iWaypointInfo)info).position);
/*    */   }
/*    */ 
/*    */   
/*    */   public WaypointInfo.Type getType() {
/* 47 */     return WaypointInfo.Type.VEC3I;
/*    */   }
/*    */   
/*    */   public Vector3i getPosition() {
/* 51 */     return this.position;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\Vec3iWaypointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
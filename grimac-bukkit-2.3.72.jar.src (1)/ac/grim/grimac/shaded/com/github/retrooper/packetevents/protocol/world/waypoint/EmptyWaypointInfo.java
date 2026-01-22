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
/*    */ public final class EmptyWaypointInfo
/*    */   implements WaypointInfo
/*    */ {
/* 28 */   public static final EmptyWaypointInfo EMPTY = new EmptyWaypointInfo();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static EmptyWaypointInfo read(PacketWrapper<?> wrapper) {
/* 35 */     return EMPTY;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static void write(PacketWrapper<?> wrapper, WaypointInfo info) {}
/*    */ 
/*    */   
/*    */   public WaypointInfo.Type getType() {
/* 45 */     return WaypointInfo.Type.EMPTY;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\EmptyWaypointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
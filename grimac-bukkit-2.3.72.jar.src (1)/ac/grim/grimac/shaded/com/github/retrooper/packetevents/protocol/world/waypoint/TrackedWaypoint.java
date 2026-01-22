/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.waypoint;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Either;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.UUID;
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
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public final class TrackedWaypoint
/*    */ {
/*    */   private final Either<UUID, String> identifier;
/*    */   private final WaypointIcon icon;
/*    */   private final WaypointInfo info;
/*    */   
/*    */   public TrackedWaypoint(Either<UUID, String> identifier, WaypointIcon icon, WaypointInfo info) {
/* 35 */     this.identifier = identifier;
/* 36 */     this.icon = icon;
/* 37 */     this.info = info;
/*    */   }
/*    */   
/*    */   public static TrackedWaypoint read(PacketWrapper<?> wrapper) {
/* 41 */     Either<UUID, String> identifier = wrapper.readEither(PacketWrapper::readUUID, PacketWrapper::readString);
/* 42 */     WaypointIcon icon = WaypointIcon.read(wrapper);
/* 43 */     WaypointInfo info = ((WaypointInfo.Type)wrapper.readEnum(WaypointInfo.Type.class)).read(wrapper);
/* 44 */     return new TrackedWaypoint(identifier, icon, info);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, TrackedWaypoint waypoint) {
/* 48 */     wrapper.writeEither(waypoint.identifier, PacketWrapper::writeUUID, PacketWrapper::writeString);
/* 49 */     WaypointIcon.write(wrapper, waypoint.icon);
/* 50 */     wrapper.writeEnum(waypoint.info.getType());
/* 51 */     waypoint.info.getType().write(wrapper, waypoint.info);
/*    */   }
/*    */   
/*    */   public Either<UUID, String> getIdentifier() {
/* 55 */     return this.identifier;
/*    */   }
/*    */   
/*    */   public WaypointIcon getIcon() {
/* 59 */     return this.icon;
/*    */   }
/*    */   
/*    */   public WaypointInfo getInfo() {
/* 63 */     return this.info;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\TrackedWaypoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
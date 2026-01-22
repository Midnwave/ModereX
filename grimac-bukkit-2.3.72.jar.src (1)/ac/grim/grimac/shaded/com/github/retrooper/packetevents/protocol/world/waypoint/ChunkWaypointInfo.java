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
/*    */ public final class ChunkWaypointInfo
/*    */   implements WaypointInfo
/*    */ {
/*    */   private final int chunkX;
/*    */   private final int chunkZ;
/*    */   
/*    */   public ChunkWaypointInfo(int chunkX, int chunkZ) {
/* 32 */     this.chunkX = chunkX;
/* 33 */     this.chunkZ = chunkZ;
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static ChunkWaypointInfo read(PacketWrapper<?> wrapper) {
/* 38 */     return new ChunkWaypointInfo(wrapper.readVarInt(), wrapper.readVarInt());
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static void write(PacketWrapper<?> wrapper, WaypointInfo info) {
/* 43 */     ChunkWaypointInfo chunkInfo = (ChunkWaypointInfo)info;
/* 44 */     wrapper.writeVarInt(chunkInfo.chunkX);
/* 45 */     wrapper.writeVarInt(chunkInfo.chunkZ);
/*    */   }
/*    */ 
/*    */   
/*    */   public WaypointInfo.Type getType() {
/* 50 */     return WaypointInfo.Type.CHUNK;
/*    */   }
/*    */   
/*    */   public int getChunkX() {
/* 54 */     return this.chunkX;
/*    */   }
/*    */   
/*    */   public int getChunkZ() {
/* 58 */     return this.chunkZ;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\waypoint\ChunkWaypointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
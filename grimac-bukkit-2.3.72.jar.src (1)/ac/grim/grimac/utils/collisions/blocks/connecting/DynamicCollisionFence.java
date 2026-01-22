/*    */ package ac.grim.grimac.utils.collisions.blocks.connecting;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.utils.collisions.CollisionData;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ 
/*    */ public class DynamicCollisionFence extends DynamicConnecting implements CollisionFactory {
/* 21 */   private static final CollisionBox[] COLLISION_BOXES = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F, true, 1);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/*    */     boolean east;
/*    */     boolean north;
/*    */     boolean south;
/*    */     boolean west;
/* 31 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && version
/* 32 */       .isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 33 */       east = (block.getEast() != East.FALSE);
/* 34 */       north = (block.getNorth() != North.FALSE);
/* 35 */       south = (block.getSouth() != South.FALSE);
/* 36 */       west = (block.getWest() != West.FALSE);
/*    */     } else {
/* 38 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST);
/* 39 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH);
/* 40 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH);
/* 41 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST);
/*    */     } 
/*    */     
/* 44 */     return COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 49 */     if (BlockTags.FENCES.contains(one)) {
/* 50 */       return (one != StateTypes.NETHER_BRICK_FENCE && two != StateTypes.NETHER_BRICK_FENCE);
/*    */     }
/* 52 */     return (BlockTags.FENCES.contains(one) || CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicCollisionFence.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
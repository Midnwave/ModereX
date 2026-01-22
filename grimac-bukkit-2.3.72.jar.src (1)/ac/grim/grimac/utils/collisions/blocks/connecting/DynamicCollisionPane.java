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
/*    */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ public class DynamicCollisionPane
/*    */   extends DynamicConnecting implements CollisionFactory {
/* 24 */   private static final CollisionBox[] COLLISION_BOXES = makeShapes(1.0F, 1.0F, 16.0F, 0.0F, 16.0F, true, 1);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/*    */     boolean east, north, south, west;
/* 34 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && version
/* 35 */       .isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 36 */       east = (block.getEast() != East.FALSE);
/* 37 */       north = (block.getNorth() != North.FALSE);
/* 38 */       south = (block.getSouth() != South.FALSE);
/* 39 */       west = (block.getWest() != West.FALSE);
/*    */     } else {
/* 41 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST);
/* 42 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH);
/* 43 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH);
/* 44 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST);
/*    */     } 
/*    */ 
/*    */     
/* 48 */     if (!north && !south && !east && !west && (version.isOlderThanOrEquals(ClientVersion.V_1_8) || (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8) && version.isNewerThanOrEquals(ClientVersion.V_1_13)))) {
/* 49 */       north = south = east = west = true;
/*    */     }
/*    */     
/* 52 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 53 */       return COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy();
/*    */     }
/* 55 */     ComplexCollisionBox boxes = new ComplexCollisionBox(2);
/* 56 */     if ((!west || !east) && (west || east || north || south)) {
/* 57 */       if (west) {
/* 58 */         boxes.add(new SimpleCollisionBox(0.0D, 0.0D, 0.4375D, 0.5D, 1.0D, 0.5625D));
/* 59 */       } else if (east) {
/* 60 */         boxes.add(new SimpleCollisionBox(0.5D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D));
/*    */       } 
/*    */     } else {
/* 63 */       boxes.add(new SimpleCollisionBox(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D));
/*    */     } 
/*    */     
/* 66 */     if ((!north || !south) && (west || east || north || south)) {
/* 67 */       if (north) {
/* 68 */         boxes.add(new SimpleCollisionBox(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5D));
/* 69 */       } else if (south) {
/* 70 */         boxes.add(new SimpleCollisionBox(0.4375D, 0.0D, 0.5D, 0.5625D, 1.0D, 1.0D));
/*    */       } 
/*    */     } else {
/* 73 */       boxes.add(new SimpleCollisionBox(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D));
/*    */     } 
/*    */     
/* 76 */     return (CollisionBox)boxes;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean canConnectToGlassBlock() {
/* 82 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 87 */     if (BlockTags.GLASS_PANES.contains(one) || one == StateTypes.IRON_BARS || (one == StateTypes.CHAIN && player.getClientVersion().isOlderThan(ClientVersion.V_1_16))) {
/* 88 */       return true;
/*    */     }
/* 90 */     return CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicCollisionPane.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
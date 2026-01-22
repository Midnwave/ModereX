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
/*    */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.HitBoxFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ public class DynamicHitboxFence extends DynamicConnecting implements HitBoxFactory {
/* 23 */   private static final CollisionBox[] MODERN_HITBOXES = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F, true, 1);
/*    */   
/*    */   private static final int MAX_MODERN_HITBOX_COMPLEX_COLLISION_BOX_SIZE = 5;
/* 26 */   public static final SimpleCollisionBox[] LEGACY_HITBOXES = new SimpleCollisionBox[] { new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D) };
/*    */   
/*    */   static {
/* 29 */     SimpleCollisionBox[] boxes = new SimpleCollisionBox[5];
/*    */ 
/*    */     
/* 32 */     for (int i = 1; i < MODERN_HITBOXES.length; i++) {
/* 33 */       CollisionBox collisionBox = MODERN_HITBOXES[i];
/* 34 */       int size = collisionBox.downCast(boxes);
/*    */       
/* 36 */       for (int j = 0; j < size; j++) {
/* 37 */         if ((boxes[j]).maxY > 1.0D) {
/* 38 */           (boxes[j]).maxY = 1.0D;
/*    */         }
/*    */       } 
/*    */       
/* 42 */       MODERN_HITBOXES[i] = (size == 1) ? (CollisionBox)boxes[0] : (CollisionBox)new ComplexCollisionBox(size, boxes);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollisionBox fetch(GrimPlayer player, StateType heldItem, ClientVersion version, WrappedBlockState block, boolean isTargetBlock, int x, int y, int z) {
/*    */     boolean east;
/*    */     boolean north;
/*    */     boolean south;
/*    */     boolean west;
/* 54 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && version
/* 55 */       .isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 56 */       east = (block.getEast() != East.FALSE);
/* 57 */       north = (block.getNorth() != North.FALSE);
/* 58 */       south = (block.getSouth() != South.FALSE);
/* 59 */       west = (block.getWest() != West.FALSE);
/*    */     } else {
/* 61 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST);
/* 62 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH);
/* 63 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH);
/* 64 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST);
/*    */     } 
/*    */     
/* 67 */     return version.isNewerThanOrEquals(ClientVersion.V_1_12_2) ? 
/* 68 */       getModernCollisionBox(north, east, south, west) : 
/* 69 */       getLegacyCollisionBox(north, east, south, west);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private CollisionBox getLegacyCollisionBox(boolean north, boolean east, boolean south, boolean west) {
/* 75 */     return (CollisionBox)LEGACY_HITBOXES[getAABBIndex(north, east, south, west)].copy();
/*    */   }
/*    */   
/*    */   private CollisionBox getModernCollisionBox(boolean north, boolean east, boolean south, boolean west) {
/* 79 */     return MODERN_HITBOXES[getAABBIndex(north, east, south, west)].copy();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 84 */     if (BlockTags.FENCES.contains(one)) {
/* 85 */       return (one != StateTypes.NETHER_BRICK_FENCE && two != StateTypes.NETHER_BRICK_FENCE);
/*    */     }
/* 87 */     return (BlockTags.FENCES.contains(one) || CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicHitboxFence.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
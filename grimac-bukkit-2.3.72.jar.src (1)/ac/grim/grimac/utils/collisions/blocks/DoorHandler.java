/*    */ package ac.grim.grimac.utils.collisions.blocks;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Half;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Hinge;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
/*    */ 
/*    */ public class DoorHandler implements CollisionFactory {
/* 17 */   protected static final CollisionBox SOUTH_AABB = (CollisionBox)new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
/* 18 */   protected static final CollisionBox NORTH_AABB = (CollisionBox)new HexCollisionBox(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
/* 19 */   protected static final CollisionBox WEST_AABB = (CollisionBox)new HexCollisionBox(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
/* 20 */   protected static final CollisionBox EAST_AABB = (CollisionBox)new HexCollisionBox(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
/*    */ 
/*    */   
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/* 24 */     switch (fetchDirection(player, version, block, x, y, z)) { case NORTH: case SOUTH: case EAST: case WEST:  }  return 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 29 */       (CollisionBox)NoCollisionBox.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BlockFace fetchDirection(GrimPlayer player, ClientVersion version, WrappedBlockState door, int x, int y, int z) {
/*    */     BlockFace facingDirection;
/*    */     boolean isClosed;
/*    */     boolean isRightHinge;
/* 44 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2) || version
/* 45 */       .isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
/* 46 */       if (door.getHalf() == Half.LOWER) {
/* 47 */         WrappedBlockState above = player.compensatedWorld.getBlock(x, y + 1, z);
/*    */         
/* 49 */         facingDirection = door.getFacing();
/* 50 */         isClosed = !door.isOpen();
/*    */ 
/*    */ 
/*    */         
/* 54 */         if (above.getType() == door.getType()) {
/* 55 */           isRightHinge = (above.getHinge() == Hinge.RIGHT);
/*    */         } else {
/*    */           
/* 58 */           isRightHinge = false;
/*    */         } 
/*    */       } else {
/* 61 */         WrappedBlockState below = player.compensatedWorld.getBlock(x, y - 1, z);
/*    */         
/* 63 */         if (below.getType() == door.getType() && below.getHalf() == Half.LOWER) {
/* 64 */           isClosed = !below.isOpen();
/* 65 */           facingDirection = below.getFacing();
/* 66 */           isRightHinge = (door.getHinge() == Hinge.RIGHT);
/*    */         } else {
/* 68 */           facingDirection = BlockFace.EAST;
/* 69 */           isClosed = true;
/* 70 */           isRightHinge = false;
/*    */         } 
/*    */       } 
/*    */     } else {
/* 74 */       facingDirection = door.getFacing();
/* 75 */       isClosed = !door.isOpen();
/* 76 */       isRightHinge = (door.getHinge() == Hinge.RIGHT);
/*    */     } 
/*    */     
/* 79 */     switch (facingDirection) { case SOUTH: return 
/*    */           
/* 81 */           isClosed ? BlockFace.SOUTH : (isRightHinge ? BlockFace.EAST : BlockFace.WEST);
/*    */       case WEST:
/* 83 */         return isClosed ? BlockFace.WEST : (isRightHinge ? BlockFace.SOUTH : BlockFace.NORTH);
/*    */       case NORTH:
/* 85 */         return isClosed ? BlockFace.NORTH : (isRightHinge ? BlockFace.WEST : BlockFace.EAST); }
/*    */     
/* 87 */     return isClosed ? BlockFace.EAST : (isRightHinge ? BlockFace.NORTH : BlockFace.SOUTH);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\DoorHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
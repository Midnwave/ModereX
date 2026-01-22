/*     */ package ac.grim.grimac.utils.collisions.blocks.connecting;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.HitBoxFactory;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ 
/*     */ public class DynamicHitboxWall
/*     */   extends DynamicConnecting implements HitBoxFactory {
/*     */   public CollisionBox fetch(GrimPlayer player, StateType heldItem, ClientVersion version, WrappedBlockState state, boolean isTargetBlock, int x, int y, int z) {
/*  25 */     int[] connections = getConnections(player, version, state, x, y, z);
/*  26 */     int north = connections[0], south = connections[1], west = connections[2], east = connections[3], up = connections[4];
/*     */     
/*  28 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/*  29 */       return getModernHitBox(north, south, west, east, up);
/*     */     }
/*  31 */     return getLegacyHitBox(north, south, west, east);
/*     */   } private int[] getConnections(GrimPlayer player, ClientVersion version, WrappedBlockState state, int x, int y, int z) {
/*     */     int north;
/*     */     int south;
/*     */     int west;
/*     */     int east;
/*     */     int up;
/*  38 */     if (isModernServer()) {
/*  39 */       boolean sixteen = PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_16);
/*  40 */       north = getConnectionValue((Enum<?>)state.getNorth(), sixteen);
/*  41 */       east = getConnectionValue((Enum<?>)state.getEast(), sixteen);
/*  42 */       south = getConnectionValue((Enum<?>)state.getSouth(), sixteen);
/*  43 */       west = getConnectionValue((Enum<?>)state.getWest(), sixteen);
/*  44 */       up = state.isUp() ? 1 : 0;
/*     */     } else {
/*  46 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH) ? 1 : 0;
/*  47 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH) ? 1 : 0;
/*  48 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST) ? 1 : 0;
/*  49 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST) ? 1 : 0;
/*  50 */       up = 1;
/*     */     } 
/*     */     
/*  53 */     return new int[] { north, south, west, east, up };
/*     */   }
/*     */   
/*     */   private boolean isModernServer() {
/*  57 */     return PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_12_2);
/*     */   }
/*     */   
/*     */   private int getConnectionValue(Enum<?> direction, boolean sixteen) {
/*  61 */     if (direction == North.NONE || direction == East.NONE || direction == South.NONE || direction == West.NONE) {
/*  62 */       return 0;
/*     */     }
/*  64 */     return (direction == North.LOW || direction == East.LOW || direction == South.LOW || direction == West.LOW || sixteen) ? 1 : 2;
/*     */   }
/*     */   
/*     */   private CollisionBox getModernHitBox(int north, int south, int west, int east, int up) {
/*  68 */     ComplexCollisionBox box = new ComplexCollisionBox(5);
/*  69 */     if (up == 1) {
/*  70 */       box.add((SimpleCollisionBox)new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D));
/*     */     }
/*     */     
/*  73 */     addDirectionalBox(box, north, 5.0D, 0.0D, 0.0D, 11.0D, 14.0D, 11.0D);
/*  74 */     addDirectionalBox(box, south, 5.0D, 0.0D, 5.0D, 11.0D, 14.0D, 16.0D);
/*  75 */     addDirectionalBox(box, west, 0.0D, 0.0D, 5.0D, 11.0D, 14.0D, 11.0D);
/*  76 */     addDirectionalBox(box, east, 5.0D, 0.0D, 5.0D, 16.0D, 14.0D, 11.0D);
/*     */     
/*  78 */     return (CollisionBox)box;
/*     */   }
/*     */   
/*     */   private void addDirectionalBox(ComplexCollisionBox box, int direction, double x1, double y1, double z1, double x2, double y2, double z2) {
/*  82 */     if (direction == 1) {
/*  83 */       box.add((SimpleCollisionBox)new HexCollisionBox(x1, y1, z1, x2, y2, z2));
/*  84 */     } else if (direction == 2) {
/*  85 */       box.add((SimpleCollisionBox)new HexCollisionBox(x1, y1, z1, x2, 16.0D, z2));
/*     */     } 
/*     */   }
/*     */   
/*     */   private CollisionBox getLegacyHitBox(int north, int south, int west, int east) {
/*  90 */     float minX = 0.25F, maxX = 0.75F, minZ = 0.25F, maxZ = 0.75F;
/*  91 */     float maxY = 1.0F;
/*     */     
/*  93 */     if (north == 1) minZ = 0.0F; 
/*  94 */     if (south == 1) maxZ = 1.0F; 
/*  95 */     if (west == 1) minX = 0.0F; 
/*  96 */     if (east == 1) maxX = 1.0F;
/*     */     
/*  98 */     if (north == 1 && south == 1 && west == 0 && east == 0) {
/*  99 */       maxY = 0.8125F;
/* 100 */       minX = 0.3125F;
/* 101 */       maxX = 0.6875F;
/* 102 */     } else if (west == 1 && east == 1 && north == 0 && south == 0) {
/* 103 */       maxY = 0.8125F;
/* 104 */       minZ = 0.3125F;
/* 105 */       maxZ = 0.6875F;
/*     */     } 
/*     */     
/* 108 */     return (CollisionBox)new SimpleCollisionBox(minX, 0.0D, minZ, maxX, maxY, maxZ);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 113 */     return (BlockTags.WALLS.contains(one) || 
/* 114 */       CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicHitboxWall.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
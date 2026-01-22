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
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ 
/*     */ public class DynamicCollisionWall
/*     */   extends DynamicConnecting
/*     */   implements CollisionFactory {
/*  25 */   private static final CollisionBox[] COLLISION_BOXES = makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, false, 1);
/*  26 */   private static final boolean isNewServer = PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_12_2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public CollisionBox fetchRegularBox(GrimPlayer player, WrappedBlockState state, ClientVersion version, int x, int y, int z) {
/*  35 */     int up = 0, east = up, west = east, south = west, north = south;
/*     */     
/*  37 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_12_2)) {
/*  38 */       boolean sixteen = PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_16);
/*     */       
/*  40 */       if (state.getNorth() != North.NONE)
/*  41 */         north += (state.getNorth() == North.LOW || sixteen) ? 1 : 2; 
/*  42 */       if (state.getEast() != East.NONE)
/*  43 */         east += (state.getEast() == East.LOW || sixteen) ? 1 : 2; 
/*  44 */       if (state.getSouth() != South.NONE)
/*  45 */         south += (state.getSouth() == South.LOW || sixteen) ? 1 : 2; 
/*  46 */       if (state.getWest() != West.NONE) {
/*  47 */         west += (state.getWest() == West.LOW || sixteen) ? 1 : 2;
/*     */       }
/*  49 */       if (state.isUp())
/*  50 */         up = 1; 
/*     */     } else {
/*  52 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH) ? 1 : 0;
/*  53 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH) ? 1 : 0;
/*  54 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST) ? 1 : 0;
/*  55 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST) ? 1 : 0;
/*  56 */       up = 1;
/*     */     } 
/*     */ 
/*     */     
/*  60 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
/*  61 */       ComplexCollisionBox box = new ComplexCollisionBox(5);
/*     */ 
/*     */       
/*  64 */       if (up == 1) {
/*  65 */         box.add((SimpleCollisionBox)new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D));
/*     */       }
/*     */       
/*  68 */       if (north == 1) {
/*  69 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 0.0D, 11.0D, 14.0D, 11.0D));
/*  70 */       } else if (north == 2) {
/*  71 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 11.0D));
/*     */       } 
/*  73 */       if (south == 1) {
/*  74 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 14.0D, 16.0D));
/*  75 */       } else if (south == 2) {
/*  76 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 16.0D));
/*     */       } 
/*  78 */       if (west == 1) {
/*  79 */         box.add((SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 5.0D, 11.0D, 14.0D, 11.0D));
/*  80 */       } else if (west == 2) {
/*  81 */         box.add((SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D));
/*     */       } 
/*  83 */       if (east == 1) {
/*  84 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 5.0D, 16.0D, 14.0D, 11.0D));
/*  85 */       } else if (east == 2) {
/*  86 */         box.add((SimpleCollisionBox)new HexCollisionBox(5.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D));
/*     */       } 
/*  88 */       return (CollisionBox)box;
/*     */     } 
/*     */ 
/*     */     
/*  92 */     float f = 0.25F;
/*  93 */     float f1 = 0.75F;
/*  94 */     float f2 = 0.25F;
/*  95 */     float f3 = 0.75F;
/*     */     
/*  97 */     if (north == 1) {
/*  98 */       f2 = 0.0F;
/*     */     }
/*     */     
/* 101 */     if (south == 1) {
/* 102 */       f3 = 1.0F;
/*     */     }
/*     */     
/* 105 */     if (west == 1) {
/* 106 */       f = 0.0F;
/*     */     }
/*     */     
/* 109 */     if (east == 1) {
/* 110 */       f1 = 1.0F;
/*     */     }
/*     */     
/* 113 */     if (north == 1 && south == 1 && west != 0 && east != 0) {
/* 114 */       f = 0.3125F;
/* 115 */       f1 = 0.6875F;
/* 116 */     } else if (north != 1 && south != 1 && west == 0 && east == 0) {
/* 117 */       f2 = 0.3125F;
/* 118 */       f3 = 0.6875F;
/*     */     } 
/*     */     
/* 121 */     return (CollisionBox)new SimpleCollisionBox(f, 0.0D, f2, f1, 1.0D, f3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/* 131 */     boolean isNewClient = version.isNewerThan(ClientVersion.V_1_12_2);
/*     */ 
/*     */     
/* 134 */     if (isNewServer && isNewClient) {
/* 135 */       boolean bool1 = (block.getNorth() != North.NONE);
/* 136 */       boolean bool2 = (block.getSouth() != South.NONE);
/* 137 */       boolean bool3 = (block.getWest() != West.NONE);
/* 138 */       boolean bool4 = (block.getEast() != East.NONE);
/*     */       
/* 140 */       return block.isUp() ? 
/* 141 */         COLLISION_BOXES[getAABBIndex(bool1, bool4, bool2, bool3)].copy().union((SimpleCollisionBox)new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 24.0D, 12.0D)) : 
/* 142 */         COLLISION_BOXES[getAABBIndex(bool1, bool4, bool2, bool3)].copy();
/*     */     } 
/*     */ 
/*     */     
/* 146 */     boolean north = isNewServer ? ((block.getNorth() != North.NONE)) : connectsTo(player, version, x, y, z, BlockFace.NORTH);
/* 147 */     boolean south = isNewServer ? ((block.getSouth() != South.NONE)) : connectsTo(player, version, x, y, z, BlockFace.SOUTH);
/* 148 */     boolean west = isNewServer ? ((block.getWest() != West.NONE)) : connectsTo(player, version, x, y, z, BlockFace.WEST);
/* 149 */     boolean east = isNewServer ? ((block.getEast() != East.NONE)) : connectsTo(player, version, x, y, z, BlockFace.EAST);
/*     */ 
/*     */     
/* 152 */     if (!isNewServer && isNewClient) {
/* 153 */       boolean up = connectsTo(player, version, x, y, z, BlockFace.UP);
/*     */       
/* 155 */       if (!up) {
/* 156 */         WrappedBlockState currBlock = player.compensatedWorld.getBlock(x, y, z);
/* 157 */         StateType currType = currBlock.getType();
/*     */         
/* 159 */         boolean selfNorth = (currType == player.compensatedWorld.getBlock(x, y, z + 1).getType());
/* 160 */         boolean selfSouth = (currType == player.compensatedWorld.getBlock(x, y, z - 1).getType());
/* 161 */         boolean selfWest = (currType == player.compensatedWorld.getBlock(x - 1, y, z).getType());
/* 162 */         boolean selfEast = (currType == player.compensatedWorld.getBlock(x + 1, y, z).getType());
/*     */         
/* 164 */         up = ((!selfNorth || !selfSouth || selfWest || selfEast) && (!selfWest || !selfEast || selfNorth || selfSouth));
/*     */         
/* 166 */         return up ? 
/* 167 */           COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy().union((SimpleCollisionBox)new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 24.0D, 12.0D)) : 
/* 168 */           COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 173 */     float f = 0.25F;
/* 174 */     float f1 = 0.75F;
/* 175 */     float f2 = 0.25F;
/* 176 */     float f3 = 0.75F;
/*     */     
/* 178 */     if (north) f2 = 0.0F; 
/* 179 */     if (south) f3 = 1.0F; 
/* 180 */     if (west) f = 0.0F; 
/* 181 */     if (east) f1 = 1.0F;
/*     */     
/* 183 */     if (north && south && !west && !east) {
/* 184 */       f = 0.3125F;
/* 185 */       f1 = 0.6875F;
/* 186 */     } else if (!north && !south && west && east) {
/* 187 */       f2 = 0.3125F;
/* 188 */       f3 = 0.6875F;
/*     */     } 
/*     */     
/* 191 */     return (CollisionBox)new SimpleCollisionBox(f, 0.0D, f2, f1, 1.5D, f3);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 196 */     return (BlockTags.WALLS.contains(one) || CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicCollisionWall.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
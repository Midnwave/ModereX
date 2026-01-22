/*     */ package ac.grim.grimac.utils.collisions.blocks;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DynamicChorusPlant
/*     */   implements CollisionFactory
/*     */ {
/*  27 */   private static final BlockFace[] directions = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };
/*  28 */   private static final CollisionBox[] modernShapes = makeShapes();
/*     */   
/*     */   private static CollisionBox[] makeShapes() {
/*  31 */     float f = 0.1875F;
/*  32 */     float f1 = 0.8125F;
/*  33 */     SimpleCollisionBox baseShape = new SimpleCollisionBox(f, f, f, f1, f1, f1, false);
/*  34 */     SimpleCollisionBox[] avoxelshape = new SimpleCollisionBox[directions.length];
/*     */     
/*  36 */     for (int i = 0; i < directions.length; i++) {
/*  37 */       BlockFace direction = directions[i];
/*  38 */       avoxelshape[i] = new SimpleCollisionBox(0.5D + Math.min(-0.3125D, direction.getModX() * 0.5D), 0.5D + Math.min(-0.3125D, direction.getModY() * 0.5D), 0.5D + Math.min(-0.3125D, direction.getModZ() * 0.5D), 0.5D + Math.max(0.3125D, direction.getModX() * 0.5D), 0.5D + Math.max(0.3125D, direction.getModY() * 0.5D), 0.5D + Math.max(0.3125D, direction.getModZ() * 0.5D), false);
/*     */     } 
/*     */     
/*  41 */     CollisionBox[] avoxelshape1 = new CollisionBox[64];
/*     */     
/*  43 */     for (int k = 0; k < 64; k++) {
/*  44 */       ComplexCollisionBox directionalShape = new ComplexCollisionBox(7, new SimpleCollisionBox[] { baseShape });
/*     */       
/*  46 */       for (int j = 0; j < directions.length; j++) {
/*  47 */         if ((k & 1 << j) != 0) {
/*  48 */           directionalShape.add(avoxelshape[j]);
/*     */         }
/*     */       } 
/*     */       
/*  52 */       avoxelshape1[k] = (CollisionBox)directionalShape;
/*     */     } 
/*     */     
/*  55 */     return avoxelshape1;
/*     */   }
/*     */ 
/*     */   
/*     */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/*     */     Set<BlockFace> directions;
/*  61 */     if (version.isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*  62 */       return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true);
/*     */     }
/*     */ 
/*     */     
/*  66 */     if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
/*  67 */       return getLegacyBoundingBox(player, version, x, y, z);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/*     */       
/*  74 */       directions = new HashSet<>();
/*  75 */       if (block.getWest() == West.TRUE) directions.add(BlockFace.WEST); 
/*  76 */       if (block.getEast() == East.TRUE) directions.add(BlockFace.EAST); 
/*  77 */       if (block.getNorth() == North.TRUE) directions.add(BlockFace.NORTH); 
/*  78 */       if (block.getSouth() == South.TRUE) directions.add(BlockFace.SOUTH); 
/*  79 */       if (block.isUp()) directions.add(BlockFace.UP); 
/*  80 */       if (block.isDown()) directions.add(BlockFace.DOWN);
/*     */     
/*     */     } else {
/*  83 */       directions = getLegacyStates(player, version, x, y, z);
/*     */     } 
/*     */     
/*  86 */     return modernShapes[getAABBIndex(directions)].copy();
/*     */   }
/*     */   
/*     */   public CollisionBox getLegacyBoundingBox(GrimPlayer player, ClientVersion version, int x, int y, int z) {
/*  90 */     Set<BlockFace> faces = getLegacyStates(player, version, x, y, z);
/*     */     
/*  92 */     float f1 = faces.contains(BlockFace.WEST) ? 0.0F : 0.1875F;
/*  93 */     float f2 = faces.contains(BlockFace.DOWN) ? 0.0F : 0.1875F;
/*  94 */     float f3 = faces.contains(BlockFace.NORTH) ? 0.0F : 0.1875F;
/*  95 */     float f4 = faces.contains(BlockFace.EAST) ? 1.0F : 0.8125F;
/*  96 */     float f5 = faces.contains(BlockFace.UP) ? 1.0F : 0.8125F;
/*  97 */     float f6 = faces.contains(BlockFace.SOUTH) ? 1.0F : 0.8125F;
/*     */     
/*  99 */     return (CollisionBox)new SimpleCollisionBox(f1, f2, f3, f4, f5, f6);
/*     */   }
/*     */   
/*     */   public Set<BlockFace> getLegacyStates(GrimPlayer player, ClientVersion version, int x, int y, int z) {
/* 103 */     Set<BlockFace> faces = new HashSet<>();
/*     */ 
/*     */     
/* 106 */     StateType versionFlower = version.isOlderThanOrEquals(ClientVersion.V_1_12_2) ? StateTypes.CHORUS_FLOWER : null;
/*     */     
/* 108 */     StateType downBlock = player.compensatedWorld.getBlockType(x, (y - 1), z);
/* 109 */     StateType upBlock = player.compensatedWorld.getBlockType(x, (y + 1), z);
/* 110 */     StateType northBlock = player.compensatedWorld.getBlockType(x, y, (z - 1));
/* 111 */     StateType eastBlock = player.compensatedWorld.getBlockType((x + 1), y, z);
/* 112 */     StateType southBlock = player.compensatedWorld.getBlockType(x, y, (z + 1));
/* 113 */     StateType westBlock = player.compensatedWorld.getBlockType((x - 1), y, z);
/*     */     
/* 115 */     if (downBlock == StateTypes.CHORUS_PLANT || downBlock == versionFlower || downBlock == StateTypes.END_STONE) {
/* 116 */       faces.add(BlockFace.DOWN);
/*     */     }
/*     */     
/* 119 */     if (upBlock == StateTypes.CHORUS_PLANT || upBlock == versionFlower) {
/* 120 */       faces.add(BlockFace.UP);
/*     */     }
/* 122 */     if (northBlock == StateTypes.CHORUS_PLANT || northBlock == versionFlower) {
/* 123 */       faces.add(BlockFace.EAST);
/*     */     }
/* 125 */     if (eastBlock == StateTypes.CHORUS_PLANT || eastBlock == versionFlower) {
/* 126 */       faces.add(BlockFace.EAST);
/*     */     }
/* 128 */     if (southBlock == StateTypes.CHORUS_PLANT || southBlock == versionFlower) {
/* 129 */       faces.add(BlockFace.NORTH);
/*     */     }
/* 131 */     if (westBlock == StateTypes.CHORUS_PLANT || westBlock == versionFlower) {
/* 132 */       faces.add(BlockFace.NORTH);
/*     */     }
/*     */     
/* 135 */     return faces;
/*     */   }
/*     */   
/*     */   protected int getAABBIndex(Set<BlockFace> p_196486_1_) {
/* 139 */     int i = 0;
/*     */     
/* 141 */     for (int j = 0; j < directions.length; j++) {
/* 142 */       if (p_196486_1_.contains(directions[j])) {
/* 143 */         i |= 1 << j;
/*     */       }
/*     */     } 
/*     */     
/* 147 */     return i;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\DynamicChorusPlant.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
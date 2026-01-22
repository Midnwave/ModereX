/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.blocks.DoorHandler;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ 
/*     */ public final class FluidTypeFlowing {
/*     */   @Generated
/*     */   private FluidTypeFlowing() {
/*  16 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   } public static Vector3dm getFlow(GrimPlayer player, int originalX, int originalY, int originalZ) {
/*  18 */     float fluidLevel = (float)Math.min(player.compensatedWorld.getFluidLevelAt(originalX, originalY, originalZ), 0.8888888888888888D);
/*  19 */     ClientVersion version = player.getClientVersion();
/*     */     
/*  21 */     if (fluidLevel == 0.0F) return new Vector3dm();
/*     */     
/*  23 */     double d0 = 0.0D;
/*  24 */     double d1 = 0.0D;
/*  25 */     for (BlockFace enumdirection : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST }) {
/*  26 */       int modifiedX = originalX + enumdirection.getModX();
/*  27 */       int modifiedZ = originalZ + enumdirection.getModZ();
/*     */       
/*  29 */       if (affectsFlow(player, originalX, originalY, originalZ, modifiedX, originalY, modifiedZ)) {
/*  30 */         float f = (float)Math.min(player.compensatedWorld.getFluidLevelAt(modifiedX, originalY, modifiedZ), 0.8888888888888888D);
/*  31 */         float f1 = 0.0F;
/*  32 */         if (f == 0.0F) {
/*  33 */           StateType mat = player.compensatedWorld.getBlockType(modifiedX, originalY, modifiedZ);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  38 */           if (Materials.isSolidBlockingBlacklist(mat, version) && 
/*  39 */             affectsFlow(player, originalX, originalY, originalZ, modifiedX, originalY - 1, modifiedZ)) {
/*  40 */             f = (float)Math.min(player.compensatedWorld.getFluidLevelAt(modifiedX, originalY - 1, modifiedZ), 0.8888888888888888D);
/*  41 */             if (f > 0.0F) {
/*  42 */               f1 = fluidLevel - f - 0.8888889F;
/*     */             }
/*     */           }
/*     */         
/*     */         }
/*  47 */         else if (f > 0.0F) {
/*  48 */           f1 = fluidLevel - f;
/*     */         } 
/*     */         
/*  51 */         if (f1 != 0.0F) {
/*  52 */           d0 += (enumdirection.getModX() * f1);
/*  53 */           d1 += (enumdirection.getModZ() * f1);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  58 */     Vector3dm vec3d = new Vector3dm(d0, 0.0D, d1);
/*     */ 
/*     */ 
/*     */     
/*  62 */     WrappedBlockState state = player.compensatedWorld.getBlock(originalX, originalY, originalZ);
/*  63 */     if ((state.getType() == StateTypes.WATER || state.getType() == StateTypes.LAVA) && state.getLevel() >= 8) {
/*  64 */       for (BlockFace enumdirection : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST }) {
/*  65 */         if (isSolidFace(player, originalX, originalY, originalZ, enumdirection) || isSolidFace(player, originalX, originalY + 1, originalZ, enumdirection)) {
/*  66 */           vec3d = normalizeVectorWithoutNaN(vec3d).add(new Vector3dm(0.0D, -6.0D, 0.0D));
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/*  71 */     return normalizeVectorWithoutNaN(vec3d);
/*     */   }
/*     */   
/*     */   private static boolean affectsFlow(GrimPlayer player, int originalX, int originalY, int originalZ, int x2, int y2, int z2) {
/*  75 */     return (isEmpty(player, x2, y2, z2) || isSame(player, originalX, originalY, originalZ, x2, y2, z2));
/*     */   }
/*     */   
/*     */   private static boolean isSolidFace(GrimPlayer player, int originalX, int y, int originalZ, BlockFace direction) {
/*  79 */     int x = originalX + direction.getModX();
/*  80 */     int z = originalZ + direction.getModZ();
/*     */     
/*  82 */     WrappedBlockState data = player.compensatedWorld.getBlock(x, y, z);
/*  83 */     StateType type = data.getType();
/*     */     
/*  85 */     if (isSame(player, x, y, z, originalX, y, originalZ)) return false; 
/*  86 */     if (type == StateTypes.ICE) return false;
/*     */ 
/*     */     
/*  89 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12)) {
/*  90 */       if (type == StateTypes.PISTON || type == StateTypes.STICKY_PISTON)
/*  91 */         return (data.getFacing().getOppositeFace() == direction || 
/*  92 */           CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, 0, 0, 0).isFullBlock()); 
/*  93 */       if (type == StateTypes.PISTON_HEAD) {
/*  94 */         return (data.getFacing() == direction);
/*     */       }
/*     */     } 
/*     */     
/*  98 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_12))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 105 */       return !Materials.isSolidBlockingBlacklist(type, player.getClientVersion()); } 
/* 106 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_13_2)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 112 */       if (Materials.isStairs(type) || Materials.isLeaves(type) || 
/* 113 */         Materials.isShulker(type) || Materials.isGlassBlock(type) || BlockTags.TRAPDOORS
/* 114 */         .contains(type)) {
/* 115 */         return false;
/*     */       }
/* 117 */       if (type == StateTypes.BEACON || BlockTags.CAULDRONS.contains(type) || type == StateTypes.GLOWSTONE || type == StateTypes.SEA_LANTERN || type == StateTypes.CONDUIT)
/*     */       {
/* 119 */         return false;
/*     */       }
/* 121 */       if (type == StateTypes.PISTON || type == StateTypes.STICKY_PISTON || type == StateTypes.PISTON_HEAD) {
/* 122 */         return false;
/*     */       }
/* 124 */       return (type == StateTypes.SOUL_SAND || CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, x, y, z).isFullBlock());
/*     */     } 
/* 126 */     if (Materials.isLeaves(type))
/*     */     {
/* 128 */       return (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2)); } 
/* 129 */     if (type == StateTypes.SNOW)
/* 130 */       return (data.getLayers() == 8); 
/* 131 */     if (Materials.isStairs(type))
/* 132 */       return (data.getFacing() == direction); 
/* 133 */     if (type == StateTypes.COMPOSTER)
/* 134 */       return true; 
/* 135 */     if (type == StateTypes.SOUL_SAND)
/* 136 */       return (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2) || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16)); 
/* 137 */     if (type == StateTypes.LADDER)
/* 138 */       return (data.getFacing().getOppositeFace() == direction); 
/* 139 */     if (BlockTags.TRAPDOORS.contains(type))
/* 140 */       return (data.getFacing().getOppositeFace() == direction && data.isOpen()); 
/* 141 */     if (BlockTags.DOORS.contains(type)) {
/* 142 */       CollisionData collisionData = CollisionData.getData(type);
/*     */       
/* 144 */       if (collisionData.dynamic instanceof DoorHandler) {
/* 145 */         BlockFace dir = ((DoorHandler)collisionData.dynamic).fetchDirection(player, player.getClientVersion(), data, x, y, z);
/* 146 */         return (dir.getOppositeFace() == direction);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 151 */     return CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, x, y, z).isFullBlock();
/*     */   }
/*     */ 
/*     */   
/*     */   private static Vector3dm normalizeVectorWithoutNaN(Vector3dm vector) {
/* 156 */     double var0 = vector.length();
/* 157 */     return (var0 < 1.0E-4D) ? new Vector3dm() : vector.multiply(1.0D / var0);
/*     */   }
/*     */   
/*     */   public static boolean isEmpty(GrimPlayer player, int x, int y, int z) {
/* 161 */     return (player.compensatedWorld.getFluidLevelAt(x, y, z) == 0.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSame(GrimPlayer player, int x1, int y1, int z1, int x2, int y2, int z2) {
/* 167 */     return ((player.compensatedWorld.getWaterFluidLevelAt(x1, y1, z1) > 0.0D && player.compensatedWorld
/* 168 */       .getWaterFluidLevelAt(x2, y2, z2) > 0.0D) || (player.compensatedWorld
/* 169 */       .getLavaFluidLevelAt(x1, y1, z1) > 0.0D && player.compensatedWorld
/* 170 */       .getLavaFluidLevelAt(x2, y2, z2) > 0.0D));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\FluidTypeFlowing.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.utils.anticheat.update;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Half;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Type;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.utils.collisions.AxisSelect;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.blocks.DoorHandler;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.HitData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.latency.CompensatedWorld;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class BlockPlace {
/*  43 */   private static final BlockFace[] BY_3D = new BlockFace[] { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST };
/*  44 */   public static final BlockFace[] BY_2D = new BlockFace[] { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST }; public final boolean isBlock; public Vector3i position; public final InteractionHand hand; public boolean replaceClicked; private boolean isCancelled; private final GrimPlayer player;
/*     */   public final ItemStack itemStack;
/*     */   public final StateType material;
/*  47 */   private final SimpleCollisionBox[] collisions = new SimpleCollisionBox[15]; @Nullable
/*     */   public final HitData hitData; private int faceId; private BlockFace face; public boolean isInside; public Vector3f cursor; public final int sequence;
/*     */   @Generated
/*     */   public boolean isCancelled() {
/*  51 */     return this.isCancelled;
/*     */   }
/*     */   
/*     */   @Generated
/*     */   public int getFaceId() {
/*  56 */     return this.faceId; } @Generated
/*  57 */   public BlockFace getFace() { return this.face; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockPlace(GrimPlayer player, InteractionHand hand, Vector3i position, int faceId, BlockFace face, ItemStack itemStack, @Nullable HitData hitData, int sequence) {
/*  63 */     this.player = player;
/*  64 */     this.hand = hand;
/*  65 */     this.position = position;
/*  66 */     this.faceId = faceId;
/*  67 */     this.face = face;
/*  68 */     this.itemStack = itemStack;
/*  69 */     if (itemStack.getType().getPlacedType() == null) {
/*  70 */       this.material = StateTypes.FIRE;
/*  71 */       this.isBlock = false;
/*     */     } else {
/*  73 */       this.material = itemStack.getType().getPlacedType();
/*  74 */       this.isBlock = true;
/*     */     } 
/*  76 */     this.hitData = hitData;
/*     */     
/*  78 */     WrappedBlockState state = player.compensatedWorld.getBlock(position);
/*  79 */     this.replaceClicked = canBeReplaced(this.material, state, face);
/*  80 */     this.sequence = sequence;
/*     */   }
/*     */   
/*     */   public WrappedBlockState getExistingBlockData() {
/*  84 */     return this.player.compensatedWorld.getBlock(getPlacedBlockPos());
/*     */   }
/*     */   
/*     */   public StateType getPlacedAgainstMaterial() {
/*  88 */     return this.player.compensatedWorld.getBlock(this.position).getType();
/*     */   }
/*     */   
/*     */   public WrappedBlockState getBelowState() {
/*  92 */     Vector3i pos = getPlacedBlockPos();
/*  93 */     pos = pos.withY(pos.getY() - 1);
/*  94 */     return this.player.compensatedWorld.getBlock(pos);
/*     */   }
/*     */   
/*     */   public WrappedBlockState getAboveState() {
/*  98 */     Vector3i pos = getPlacedBlockPos();
/*  99 */     pos = pos.withY(pos.getY() + 1);
/* 100 */     return this.player.compensatedWorld.getBlock(pos);
/*     */   }
/*     */   
/*     */   public WrappedBlockState getDirectionalState(BlockFace facing) {
/* 104 */     Vector3i pos = getPlacedBlockPos();
/* 105 */     pos = pos.add(facing.getModX(), facing.getModY(), facing.getModZ());
/* 106 */     return this.player.compensatedWorld.getBlock(pos);
/*     */   }
/*     */   
/*     */   public boolean isSolidBlocking(BlockFace relative) {
/* 110 */     WrappedBlockState state = getDirectionalState(relative);
/* 111 */     return state.getType().isBlocking();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean canBeReplaced(StateType heldItem, WrappedBlockState state, BlockFace face) {
/* 116 */     boolean baseReplaceable = (state.getType() != heldItem && state.getType().isReplaceable());
/*     */     
/* 118 */     if (BlockTags.CANDLES.contains(state.getType())) {
/* 119 */       return (heldItem == state.getType() && state.getCandles() < 4 && !isSecondaryUse());
/*     */     }
/* 121 */     if (state.getType() == StateTypes.SEA_PICKLE) {
/* 122 */       return (heldItem == state.getType() && state.getPickles() < 4 && !isSecondaryUse());
/*     */     }
/* 124 */     if (state.getType() == StateTypes.TURTLE_EGG) {
/* 125 */       return (heldItem == state.getType() && state.getEggs() < 4 && !isSecondaryUse());
/*     */     }
/*     */     
/* 128 */     if (state.getType() == StateTypes.GLOW_LICHEN) {
/* 129 */       if (heldItem != StateTypes.GLOW_LICHEN) {
/* 130 */         return true;
/*     */       }
/* 132 */       if (!state.isUp()) return true; 
/* 133 */       if (!state.isDown()) return true; 
/* 134 */       if (state.getNorth() == North.FALSE) return true; 
/* 135 */       if (state.getSouth() == South.FALSE) return true; 
/* 136 */       if (state.getEast() == East.FALSE) return true; 
/* 137 */       return (state.getWest() == West.FALSE);
/*     */     } 
/* 139 */     if (state.getType() == StateTypes.SCAFFOLDING) {
/* 140 */       return (heldItem == StateTypes.SCAFFOLDING);
/*     */     }
/* 142 */     if (BlockTags.SLABS.contains(state.getType())) {
/* 143 */       if (state.getTypeData() == Type.DOUBLE || state.getType() != heldItem) return false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 149 */       boolean flag = (getClickedLocation().getY() > 0.5D);
/* 150 */       BlockFace clickedFace = getFace();
/* 151 */       if (state.getTypeData() == Type.BOTTOM) {
/* 152 */         return (clickedFace == BlockFace.UP || (flag && isFaceHorizontal()));
/*     */       }
/* 154 */       return (clickedFace == BlockFace.DOWN || (!flag && isFaceHorizontal()));
/*     */     } 
/*     */     
/* 157 */     if (state.getType() == StateTypes.SNOW) {
/* 158 */       int layers = state.getLayers();
/* 159 */       if (heldItem == state.getType() && layers < 8) {
/* 160 */         return (face == BlockFace.UP);
/*     */       }
/* 162 */       return (layers == 1);
/*     */     } 
/*     */     
/* 165 */     if (state.getType() == StateTypes.VINE) {
/* 166 */       if (baseReplaceable) return true; 
/* 167 */       if (heldItem != state.getType()) return false; 
/* 168 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && !state.isUp())
/* 169 */         return true; 
/* 170 */       if (state.getNorth() == North.FALSE) return true; 
/* 171 */       if (state.getSouth() == South.FALSE) return true; 
/* 172 */       if (state.getEast() == East.FALSE) return true; 
/* 173 */       return (state.getWest() == West.FALSE);
/*     */     } 
/* 175 */     if (state.getType() == StateTypes.LADDER && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_13)) {
/* 176 */       return true;
/*     */     }
/*     */     
/* 179 */     return baseReplaceable;
/*     */   }
/*     */   
/*     */   public boolean isFaceFullCenter(BlockFace facing) {
/* 183 */     WrappedBlockState data = getDirectionalState(facing);
/* 184 */     CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), data);
/*     */     
/* 186 */     if (box.isNull()) return false; 
/* 187 */     if (isFullFace(facing)) return true; 
/* 188 */     if (BlockTags.LEAVES.contains(data.getType())) return false; 
/* 189 */     if (BlockTags.FENCE_GATES.contains(data.getType())) return false;
/*     */     
/* 191 */     int size = box.downCast(this.collisions);
/*     */     
/* 193 */     AxisSelect axis = AxisSelect.byFace(facing.getOppositeFace());
/*     */     
/* 195 */     for (int i = 0; i < size; i++) {
/* 196 */       SimpleCollisionBox simpleBox = this.collisions[i];
/* 197 */       simpleBox = axis.modify(simpleBox);
/* 198 */       if (simpleBox.minX <= 0.4375D && simpleBox.maxX >= 0.4375D && simpleBox.minY <= 0.0D && simpleBox.maxY >= 0.625D && simpleBox.minZ <= 0.4375D && simpleBox.maxZ >= 0.5625D)
/*     */       {
/*     */         
/* 201 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 205 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFaceRigid(BlockFace facing) {
/* 209 */     WrappedBlockState data = getDirectionalState(facing);
/* 210 */     CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), data);
/*     */     
/* 212 */     if (box.isNull()) return false; 
/* 213 */     if (isFullFace(facing)) return true; 
/* 214 */     if (BlockTags.LEAVES.contains(data.getType())) return false;
/*     */     
/* 216 */     int size = box.downCast(this.collisions);
/*     */     
/* 218 */     AxisSelect axis = AxisSelect.byFace(facing.getOppositeFace());
/*     */     
/* 220 */     for (int i = 0; i < size; i++) {
/* 221 */       SimpleCollisionBox simpleBox = this.collisions[i];
/* 222 */       simpleBox = axis.modify(simpleBox);
/* 223 */       if (simpleBox.minX <= 0.125D && simpleBox.maxX >= 0.875D && simpleBox.minY <= 0.0D && simpleBox.maxY >= 1.0D && simpleBox.minZ <= 0.125D && simpleBox.maxZ >= 0.875D)
/*     */       {
/*     */         
/* 226 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 230 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFullFace(BlockFace relative) {
/* 234 */     WrappedBlockState state = getDirectionalState(relative);
/* 235 */     BlockFace face = relative.getOppositeFace();
/* 236 */     BlockFace bukkitFace = BlockFace.valueOf(face.name());
/*     */     
/* 238 */     AxisSelect axis = AxisSelect.byFace(face);
/*     */     
/* 240 */     CollisionBox box = CollisionData.getData(state.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), state);
/*     */     
/* 242 */     StateType blockMaterial = state.getType();
/*     */     
/* 244 */     if (BlockTags.LEAVES.contains(blockMaterial))
/*     */     {
/* 246 */       return false; } 
/* 247 */     if (blockMaterial == StateTypes.SNOW)
/* 248 */       return (state.getLayers() == 8 || face == BlockFace.DOWN); 
/* 249 */     if (BlockTags.STAIRS.contains(blockMaterial)) {
/* 250 */       if (face == BlockFace.UP) {
/* 251 */         return (state.getHalf() == Half.TOP);
/*     */       }
/* 253 */       if (face == BlockFace.DOWN) {
/* 254 */         return (state.getHalf() == Half.BOTTOM);
/*     */       }
/*     */       
/* 257 */       return (state.getFacing() == bukkitFace);
/* 258 */     }  if (blockMaterial == StateTypes.COMPOSTER)
/* 259 */       return (face != BlockFace.UP); 
/* 260 */     if (blockMaterial == StateTypes.SOUL_SAND)
/* 261 */       return true; 
/* 262 */     if (blockMaterial == StateTypes.LADDER)
/* 263 */       return (state.getFacing().getOppositeFace() == bukkitFace); 
/* 264 */     if (BlockTags.TRAPDOORS.contains(blockMaterial))
/* 265 */       return ((state.getFacing().getOppositeFace() == bukkitFace && state.isOpen()) || (state
/* 266 */         .getHalf() == Half.TOP && !state.isOpen() && bukkitFace == BlockFace.UP) || (state
/* 267 */         .getHalf() == Half.BOTTOM && !state.isOpen() && bukkitFace == BlockFace.DOWN)); 
/* 268 */     if (BlockTags.DOORS.contains(blockMaterial)) {
/* 269 */       CollisionData data = CollisionData.getData(blockMaterial);
/*     */       
/* 271 */       CollisionFactory collisionFactory = data.dynamic; if (collisionFactory instanceof DoorHandler) { DoorHandler doorHandler = (DoorHandler)collisionFactory;
/* 272 */         return 
/*     */ 
/*     */           
/* 275 */           (doorHandler.fetchDirection(this.player, this.player.getClientVersion(), state, this.position.x, this.position.y, this.position.z).getOppositeFace() == bukkitFace); }
/*     */     
/*     */     } 
/*     */     
/* 279 */     int size = box.downCast(this.collisions);
/*     */     
/* 281 */     for (int i = 0; i < size; i++) {
/* 282 */       SimpleCollisionBox simpleBox = this.collisions[i];
/* 283 */       if (axis.modify(simpleBox).isFullBlockNoCache()) return true;
/*     */     
/*     */     } 
/*     */     
/* 287 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isBlockFaceOpen(BlockFace facing) {
/* 291 */     Vector3i pos = getPlacedBlockPos();
/* 292 */     pos = pos.add(facing.getModX(), facing.getModY(), facing.getModZ());
/*     */     
/* 294 */     if (pos.getY() >= this.player.compensatedWorld.getMaxHeight()) return false;
/*     */     
/* 296 */     return this.player.compensatedWorld.getBlock(pos).getType().isReplaceable();
/*     */   }
/*     */   
/*     */   public boolean isFaceEmpty(BlockFace facing) {
/* 300 */     WrappedBlockState data = getDirectionalState(facing);
/* 301 */     CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), data);
/*     */     
/* 303 */     if (box.isNull()) return false; 
/* 304 */     if (isFullFace(facing)) return true; 
/* 305 */     if (BlockTags.LEAVES.contains(data.getType())) return false;
/*     */     
/* 307 */     int size = box.downCast(this.collisions);
/*     */     
/* 309 */     AxisSelect axis = AxisSelect.byFace(facing.getOppositeFace());
/*     */     
/* 311 */     for (int i = 0; i < size; i++) {
/* 312 */       SimpleCollisionBox simpleBox = this.collisions[i];
/* 313 */       simpleBox = axis.modify(simpleBox);
/*     */       
/* 315 */       switch (facing) {
/*     */         case NORTH:
/* 317 */           if (simpleBox.minZ == 0.0D) return false; 
/*     */           break;
/*     */         case SOUTH:
/* 320 */           if (simpleBox.maxZ == 1.0D) return false; 
/*     */           break;
/*     */         case EAST:
/* 323 */           if (simpleBox.maxX == 1.0D) return false; 
/*     */           break;
/*     */         case WEST:
/* 326 */           if (simpleBox.minX == 0.0D) return false; 
/*     */           break;
/*     */         case UP:
/* 329 */           if (simpleBox.maxY == 1.0D) return false; 
/*     */           break;
/*     */         case DOWN:
/* 332 */           if (simpleBox.minY == 0.0D) return false;
/*     */           
/*     */           break;
/*     */       } 
/*     */     } 
/* 337 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isLava(BlockFace facing) {
/* 341 */     Vector3i pos = getPlacedBlockPos();
/* 342 */     pos = pos.add(facing.getModX(), facing.getModY(), facing.getModZ());
/* 343 */     return (this.player.compensatedWorld.getBlock(pos).getType() == StateTypes.LAVA);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecondaryUse() {
/* 348 */     return this.player.isSneaking;
/*     */   }
/*     */   
/*     */   public boolean isInWater() {
/* 352 */     Vector3i pos = getPlacedBlockPos();
/* 353 */     return this.player.compensatedWorld.isWaterSourceBlock(pos.getX(), pos.getY(), pos.getZ());
/*     */   }
/*     */   
/*     */   public boolean isInLiquid() {
/* 357 */     Vector3i pos = getPlacedBlockPos();
/* 358 */     WrappedBlockState data = this.player.compensatedWorld.getBlock(pos);
/* 359 */     return (Materials.isWater(this.player.getClientVersion(), data) || data.getType() == StateTypes.LAVA);
/*     */   }
/*     */   
/*     */   public StateType getBelowMaterial() {
/* 363 */     return getBelowState().getType();
/*     */   }
/*     */   
/*     */   public boolean isOn(StateType... mat) {
/* 367 */     StateType lookingFor = getBelowMaterial();
/* 368 */     return Arrays.<StateType>stream(mat).anyMatch(m -> (m == lookingFor));
/*     */   }
/*     */   
/*     */   public boolean isOnDirt() {
/* 372 */     return isOn(new StateType[] { StateTypes.DIRT, StateTypes.GRASS_BLOCK, StateTypes.PODZOL, StateTypes.COARSE_DIRT, StateTypes.MYCELIUM, StateTypes.ROOTED_DIRT, StateTypes.MOSS_BLOCK });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBlockPlacedPowered() {
/* 379 */     Vector3i placed = getPlacedBlockPos();
/*     */     
/* 381 */     for (BlockFace face : BY_3D) {
/* 382 */       Vector3i modified = placed.add(face.getModX(), face.getModY(), face.getModZ());
/*     */ 
/*     */       
/* 385 */       if (this.player.compensatedWorld.getRawPowerAtState(face, modified.getX(), modified.getY(), modified.getZ()) > 0) {
/* 386 */         return true;
/*     */       }
/*     */ 
/*     */       
/* 390 */       WrappedBlockState state = this.player.compensatedWorld.getBlock(modified);
/*     */ 
/*     */       
/* 393 */       boolean isByDefaultConductive = (!Materials.isSolidBlockingBlacklist(state.getType(), this.player.getClientVersion()) && CollisionData.getData(state.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), state).isFullBlock());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 398 */       if ((state.getType() == StateTypes.SOUL_SAND || 
/* 399 */         !BlockTags.GLASS_BLOCKS.contains(state.getType())) && state.getType() != StateTypes.MOVING_PISTON && state
/* 400 */         .getType() != StateTypes.BEACON && state.getType() != StateTypes.REDSTONE_BLOCK && state
/* 401 */         .getType() != StateTypes.OBSERVER && isByDefaultConductive)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 407 */         for (BlockFace recursive : BY_3D) {
/* 408 */           Vector3i poweredRecursive = placed.add(recursive.getModX(), recursive.getModY(), recursive.getModZ());
/*     */ 
/*     */           
/* 411 */           if (this.player.compensatedWorld.getDirectSignalAtState(recursive, poweredRecursive.getX(), poweredRecursive.getY(), poweredRecursive.getZ()) > 0) {
/* 412 */             return true;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 417 */     return false;
/*     */   }
/*     */   
/*     */   public void setFace(BlockFace face) {
/* 421 */     this.face = face;
/* 422 */     this.faceId = face.getFaceValue();
/*     */   }
/*     */   
/*     */   public void setFaceId(int face) {
/* 426 */     this.faceId = face;
/* 427 */     this.face = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9) ? BlockFace.getBlockFaceByValue(this.faceId) : BlockFace.getLegacyBlockFaceByValue(this.faceId);
/*     */   }
/*     */   
/*     */   private List<BlockFace> getNearestLookingDirections() {
/* 431 */     float f = this.player.yRot * 0.017453292F;
/* 432 */     float f1 = -this.player.xRot * 0.017453292F;
/* 433 */     float f2 = this.player.trigHandler.sin(f);
/* 434 */     float f3 = this.player.trigHandler.cos(f);
/* 435 */     float f4 = this.player.trigHandler.sin(f1);
/* 436 */     float f5 = this.player.trigHandler.cos(f1);
/* 437 */     boolean flag = (f4 > 0.0F);
/* 438 */     boolean flag1 = (f2 < 0.0F);
/* 439 */     boolean flag2 = (f5 > 0.0F);
/* 440 */     float f6 = flag ? f4 : -f4;
/* 441 */     float f7 = flag1 ? -f2 : f2;
/* 442 */     float f8 = flag2 ? f5 : -f5;
/* 443 */     float f9 = f6 * f3;
/* 444 */     float f10 = f8 * f3;
/* 445 */     BlockFace direction = flag ? BlockFace.EAST : BlockFace.WEST;
/* 446 */     BlockFace direction1 = flag1 ? BlockFace.UP : BlockFace.DOWN;
/* 447 */     BlockFace direction2 = flag2 ? BlockFace.SOUTH : BlockFace.NORTH;
/* 448 */     if (f6 > f8) {
/* 449 */       if (f7 > f9) {
/* 450 */         return makeDirList(direction1, direction, direction2);
/*     */       }
/* 452 */       return (f10 > f7) ? makeDirList(direction, direction2, direction1) : makeDirList(direction, direction1, direction2);
/*     */     } 
/* 454 */     if (f7 > f10) {
/* 455 */       return makeDirList(direction1, direction2, direction);
/*     */     }
/* 457 */     return (f9 > f7) ? makeDirList(direction2, direction, direction1) : makeDirList(direction2, direction1, direction);
/*     */   }
/*     */ 
/*     */   
/*     */   private List<BlockFace> makeDirList(BlockFace one, BlockFace two, BlockFace three) {
/* 462 */     return Arrays.asList(new BlockFace[] { one, two, three, three.getOppositeFace(), two.getOppositeFace(), one.getOppositeFace() });
/*     */   }
/*     */   
/*     */   public BlockFace getNearestVerticalDirection() {
/* 466 */     return (this.player.yRot < 0.0F) ? BlockFace.UP : BlockFace.DOWN;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<BlockFace> getNearestPlacingDirections() {
/* 472 */     BlockFace[] faces = getNearestLookingDirections().<BlockFace>toArray(new BlockFace[0]);
/*     */     
/* 474 */     if (!this.replaceClicked) {
/* 475 */       BlockFace direction = getFace();
/*     */ 
/*     */       
/* 478 */       int i = 0;
/* 479 */       for (; i < faces.length && faces[i] != direction.getOppositeFace(); i++);
/*     */       
/* 481 */       if (i > 0) {
/* 482 */         System.arraycopy(faces, 0, faces, 1, i);
/* 483 */         faces[0] = direction.getOppositeFace();
/*     */       } 
/*     */     } 
/*     */     
/* 487 */     return Arrays.asList(faces);
/*     */   }
/*     */   
/*     */   public boolean isFaceVertical() {
/* 491 */     return !isFaceHorizontal();
/*     */   }
/*     */   
/*     */   public boolean isFaceHorizontal() {
/* 495 */     BlockFace face = getFace();
/* 496 */     return (face == BlockFace.NORTH || face == BlockFace.EAST || face == BlockFace.SOUTH || face == BlockFace.WEST);
/*     */   }
/*     */   
/*     */   public boolean isXAxis() {
/* 500 */     BlockFace face = getFace();
/* 501 */     return (face == BlockFace.WEST || face == BlockFace.EAST);
/*     */   }
/*     */   
/*     */   public Vector3i getPlacedBlockPos() {
/* 505 */     if (this.replaceClicked) return this.position;
/*     */     
/* 507 */     int x = this.position.getX() + getNormalBlockFace().getX();
/* 508 */     int y = this.position.getY() + getNormalBlockFace().getY();
/* 509 */     int z = this.position.getZ() + getNormalBlockFace().getZ();
/* 510 */     return new Vector3i(x, y, z);
/*     */   }
/*     */   
/*     */   public Vector3i getNormalBlockFace() {
/* 514 */     switch (this.face) { case DOWN: case SOUTH: case NORTH: case WEST: case EAST:  }  return 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 520 */       new Vector3i(0, 1, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(StateType material) {
/* 525 */     set(material.createBlockState(CompensatedWorld.blockVersion));
/*     */   }
/*     */   
/*     */   public void set(BlockFace face, WrappedBlockState state) {
/* 529 */     Vector3i blockPos = getPlacedBlockPos().add(face.getModX(), face.getModY(), face.getModZ());
/* 530 */     set(blockPos, state);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Vector3i position, WrappedBlockState state) {
/* 535 */     CollisionBox box = CollisionData.getData(state.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), state, position.getX(), position.getY(), position.getZ());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 540 */     if (state.getType() != StateTypes.SCAFFOLDING) {
/*     */ 
/*     */ 
/*     */       
/* 544 */       if (box.isIntersected(this.player.boundingBox)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 553 */       if (this.player.getClientVersion().isNewerThan(ClientVersion.V_1_8)) {
/* 554 */         for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 555 */           SimpleCollisionBox interpBox = entity.getPossibleCollisionBoxes();
/*     */           
/* 557 */           double scale = entity.getAttributeValue(Attributes.SCALE);
/* 558 */           double width = BoundingBoxSize.getWidth(this.player, entity) * scale;
/* 559 */           double height = BoundingBoxSize.getHeight(this.player, entity) * scale;
/* 560 */           double interpWidth = Math.max(interpBox.maxX - interpBox.minX, interpBox.maxZ - interpBox.minZ);
/* 561 */           double interpHeight = interpBox.maxY - interpBox.minY;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 566 */           if (interpWidth - width > 0.05D || interpHeight - height > 0.05D) {
/* 567 */             Vector3d entityPos = entity.trackedServerPosition.getPos();
/* 568 */             interpBox = GetBoundingBox.getPacketEntityBoundingBox(this.player, entityPos.getX(), entityPos.getY(), entityPos.getZ(), entity);
/*     */           } 
/*     */           
/* 571 */           if (box.isIntersected(interpBox)) {
/*     */             return;
/*     */           } }
/*     */       
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 579 */     WrappedBlockState existingState = this.player.compensatedWorld.getBlock(position);
/* 580 */     if (!this.replaceClicked && !canBeReplaced(this.material, existingState, this.face)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 585 */     if (this.player.compensatedWorld.getMaxHeight() <= position.getY() || position.getY() < this.player.compensatedWorld.getMinHeight()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 590 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && 
/* 591 */       state.getInternalData().containsKey(StateValue.WATERLOGGED)) {
/* 592 */       state.setWaterlogged((existingState.getType() == StateTypes.WATER && existingState.getLevel() == 0));
/*     */     }
/*     */ 
/*     */     
/* 596 */     this.player.inventory.onBlockPlace(this);
/* 597 */     this.player.compensatedWorld.updateBlock(position.getX(), position.getY(), position.getZ(), state.getGlobalId());
/*     */   }
/*     */   
/*     */   public boolean isZAxis() {
/* 601 */     BlockFace face = getFace();
/* 602 */     return (face == BlockFace.NORTH || face == BlockFace.SOUTH);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tryCascadeBlockUpdates(Vector3i pos) {
/* 607 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2))
/*     */       return; 
/* 609 */     cascadeBlockUpdates(pos);
/*     */   }
/*     */ 
/*     */   
/*     */   private void cascadeBlockUpdates(Vector3i pos) {}
/*     */ 
/*     */   
/*     */   public void set(WrappedBlockState state) {
/* 617 */     set(getPlacedBlockPos(), state);
/*     */   }
/*     */   
/*     */   public void resync() {
/* 621 */     this.isCancelled = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3dm getClickedLocation() {
/* 636 */     SimpleCollisionBox box = new SimpleCollisionBox(this.position);
/* 637 */     Vector3dm look = ReachUtils.getLook(this.player, this.player.xRot, this.player.yRot);
/*     */     
/* 639 */     double distance = this.player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 3.0D;
/* 640 */     Vector3dm eyePos = new Vector3dm(this.player.x, this.player.y + this.player.getEyeHeight(), this.player.z);
/* 641 */     Vector3dm endReachPos = eyePos.clone().add(new Vector3dm(look.getX() * distance, look.getY() * distance, look.getZ() * distance));
/* 642 */     Vector3dm intercept = (Vector3dm)ReachUtils.calculateIntercept(box, eyePos, endReachPos).first();
/*     */ 
/*     */ 
/*     */     
/* 646 */     if (intercept == null) return new Vector3dm();
/*     */     
/* 648 */     intercept.setX(intercept.getX() - box.minX);
/* 649 */     intercept.setY(intercept.getY() - box.minY);
/* 650 */     intercept.setZ(intercept.getZ() - box.minZ);
/*     */     
/* 652 */     return intercept;
/*     */   }
/*     */ 
/*     */   
/*     */   public BlockFace getPlayerFacing() {
/* 657 */     return BY_2D[GrimMath.floor(this.player.xRot / 90.0D + 0.5D) & 0x3];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void set() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield material : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateType;
/*     */     //   4: ifnonnull -> 16
/*     */     //   7: <illegal opcode> makeConcatWithConstants : ()Ljava/lang/String;
/*     */     //   12: invokestatic warn : (Ljava/lang/String;)V
/*     */     //   15: return
/*     */     //   16: aload_0
/*     */     //   17: aload_0
/*     */     //   18: getfield material : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateType;
/*     */     //   21: invokevirtual set : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateType;)V
/*     */     //   24: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #661	-> 0
/*     */     //   #662	-> 7
/*     */     //   #663	-> 15
/*     */     //   #665	-> 16
/*     */     //   #666	-> 24
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	25	0	this	Lac/grim/grimac/utils/anticheat/update/BlockPlace;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAbove() {
/* 669 */     Vector3i placed = getPlacedBlockPos();
/* 670 */     placed = placed.add(0, 1, 0);
/* 671 */     set(placed, this.material.createBlockState(CompensatedWorld.blockVersion));
/*     */   }
/*     */   
/*     */   public void setAbove(WrappedBlockState toReplaceWith) {
/* 675 */     Vector3i placed = getPlacedBlockPos();
/* 676 */     placed = placed.add(0, 1, 0);
/* 677 */     set(placed, toReplaceWith);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\antichea\\update\BlockPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
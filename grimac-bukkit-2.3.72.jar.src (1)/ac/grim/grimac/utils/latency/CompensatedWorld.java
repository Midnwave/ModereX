/*     */ package ac.grim.grimac.utils.latency;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.Palette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BaseStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Half;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*     */ import ac.grim.grimac.shaded.fastutil.ints.Int2ObjectMap;
/*     */ import ac.grim.grimac.shaded.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.longs.Long2ObjectMap;
/*     */ import ac.grim.grimac.shaded.fastutil.longs.Long2ObjectOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2ObjectLinkedOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.utils.change.BlockModification;
/*     */ import ac.grim.grimac.utils.chunks.Column;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.BlockPrediction;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.PistonData;
/*     */ import ac.grim.grimac.utils.data.ShulkerData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityShulker;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class CompensatedWorld {
/*  67 */   public static final ClientVersion blockVersion = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*  68 */   private static final WrappedBlockState airData = WrappedBlockState.getByGlobalId(blockVersion, 0);
/*     */   
/*     */   public final GrimPlayer player;
/*     */   public final Long2ObjectMap<Column> chunks;
/*  72 */   public final Set<PistonData> activePistons = new HashSet<>();
/*  73 */   public final Set<ShulkerData> openShulkerBoxes = new HashSet<>();
/*     */   
/*  75 */   private int minHeight = 0; @Generated public int getMinHeight() { return this.minHeight; }
/*     */   
/*  77 */   private int maxHeight = 256; @Generated public int getMaxHeight() { return this.maxHeight; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private final Long2ObjectOpenHashMap<BlockPrediction> originalServerBlocks = new Long2ObjectOpenHashMap();
/*     */   
/*  86 */   private List<Vector3i> currentlyChangedBlocks = new LinkedList<>();
/*  87 */   private final Int2ObjectMap<List<Vector3i>> serverIsCurrentlyProcessingThesePredictions = (Int2ObjectMap<List<Vector3i>>)new Int2ObjectOpenHashMap();
/*  88 */   private final Object2ObjectLinkedOpenHashMap<Pair<Vector3i, DiggingAction>, Vector3d> unackedActions = new Object2ObjectLinkedOpenHashMap();
/*     */   
/*     */   private boolean isCurrentlyPredicting = false;
/*     */   public boolean isRaining = false;
/*     */   private final boolean noNegativeBlocks;
/*     */   
/*     */   public CompensatedWorld(GrimPlayer player) {
/*  95 */     this.player = player;
/*  96 */     this.chunks = (Long2ObjectMap<Column>)new Long2ObjectOpenHashMap(81, 0.5F);
/*  97 */     this.noNegativeBlocks = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_16_4);
/*     */   }
/*     */   
/*     */   public void startPredicting() {
/* 101 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_18_2))
/* 102 */       return;  this.isCurrentlyPredicting = true;
/*     */   }
/*     */   
/*     */   public void handlePredictionConfirmation(int prediction) {
/* 106 */     for (ObjectIterator<Map.Entry<Integer, List<Vector3i>>> objectIterator = this.serverIsCurrentlyProcessingThesePredictions.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) {
/* 107 */       Map.Entry<Integer, List<Vector3i>> iter = objectIterator.next();
/* 108 */       if (((Integer)iter.getKey()).intValue() <= prediction) {
/* 109 */         applyBlockChanges(iter.getValue());
/* 110 */         objectIterator.remove();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleBlockBreakAck(Vector3i blockPos, int blockState, DiggingAction action, boolean accepted) {
/* 118 */     if (!accepted || action != DiggingAction.START_DIGGING || !this.unackedActions.containsKey(new Pair(blockPos, action))) {
/* 119 */       this.player.sendTransaction();
/* 120 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             Vector3d playerPos = (Vector3d)this.unackedActions.remove(new Pair(blockPos, action));
/*     */             handleAck(blockPos, blockState, playerPos);
/*     */           });
/*     */     } else {
/* 125 */       this.unackedActions.remove(new Pair(blockPos, action));
/*     */     } 
/*     */     
/* 128 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */           while (this.unackedActions.size() >= 50) {
/*     */             this.unackedActions.removeFirst();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void applyBlockChanges(List<Vector3i> toApplyBlocks) {
/* 136 */     this.player.sendTransaction();
/* 137 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> toApplyBlocks.forEach(()));
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
/*     */   private void handleAck(Vector3i vector3i, int originalBlockId, Vector3d playerPosition) {
/* 151 */     if (getBlock(vector3i).getGlobalId() != originalBlockId) {
/* 152 */       this.player.blockHistory.add(new BlockModification(
/*     */             
/* 154 */             getBlock(vector3i), 
/* 155 */             WrappedBlockState.getByGlobalId(originalBlockId), vector3i, 
/*     */             
/* 157 */             (GrimAPI.INSTANCE.getTickManager()).currentTick, BlockModification.Cause.HANDLE_NETTY_SYNC_TRANSACTION));
/*     */ 
/*     */ 
/*     */       
/* 161 */       updateBlock(vector3i.getX(), vector3i.getY(), vector3i.getZ(), originalBlockId);
/* 162 */       WrappedBlockState state = WrappedBlockState.getByGlobalId(blockVersion, originalBlockId);
/*     */ 
/*     */       
/* 165 */       if (playerPosition != null && CollisionData.getData(state.getType()).getMovementCollisionBox(this.player, this.player.getClientVersion(), state, vector3i.getX(), vector3i.getY(), vector3i.getZ()).isIntersected(this.player.boundingBox)) {
/* 166 */         this.player.lastX = this.player.x;
/* 167 */         this.player.lastY = this.player.y;
/* 168 */         this.player.lastZ = this.player.z;
/* 169 */         this.player.x = playerPosition.getX();
/* 170 */         this.player.y = playerPosition.getY();
/* 171 */         this.player.z = playerPosition.getZ();
/* 172 */         this.player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.x, this.player.y, this.player.z);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleBlockBreakPrediction(WrapperPlayClientPlayerDigging digging) {
/* 179 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14_4) && this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_18_2)) {
/* 180 */       this.unackedActions.put(new Pair(digging.getBlockPosition(), digging.getAction()), new Vector3d(this.player.x, this.player.y, this.player.z));
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopPredicting(PacketWrapper<?> wrapper) {
/* 185 */     if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_18_2))
/* 186 */       return;  this.isCurrentlyPredicting = false;
/*     */     
/* 188 */     if (this.currentlyChangedBlocks.isEmpty())
/*     */       return; 
/* 190 */     List<Vector3i> toApplyBlocks = this.currentlyChangedBlocks;
/* 191 */     this.currentlyChangedBlocks = new LinkedList<>();
/*     */ 
/*     */     
/* 194 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19)) {
/*     */       
/* 196 */       int confirmationId = 0;
/* 197 */       if (wrapper instanceof WrapperPlayClientPlayerBlockPlacement) {
/* 198 */         confirmationId = ((WrapperPlayClientPlayerBlockPlacement)wrapper).getSequence();
/* 199 */       } else if (wrapper instanceof WrapperPlayClientUseItem) {
/* 200 */         confirmationId = ((WrapperPlayClientUseItem)wrapper).getSequence();
/* 201 */       } else if (wrapper instanceof WrapperPlayClientPlayerDigging) {
/* 202 */         confirmationId = ((WrapperPlayClientPlayerDigging)wrapper).getSequence();
/*     */       } 
/*     */       
/* 205 */       this.serverIsCurrentlyProcessingThesePredictions.put(confirmationId, toApplyBlocks);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 211 */       GrimAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().run(GrimAPI.INSTANCE.getGrimPlugin(), () -> this.player.runSafely(()));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long chunkPositionToLong(int x, int z) {
/* 219 */     return (x & 0xFFFFFFFFL) << 32L | z & 0xFFFFFFFFL;
/*     */   }
/*     */   
/*     */   public boolean isNearHardEntity(SimpleCollisionBox playerBox) {
/* 223 */     for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/* 224 */       if ((entity.isBoat || entity.type == EntityTypes.SHULKER || entity.isHappyGhast) && this.player.compensatedEntities.self.getRiding() != entity) {
/* 225 */         SimpleCollisionBox box = entity.getPossibleCollisionBoxes();
/* 226 */         if (box.isIntersected(playerBox)) {
/* 227 */           return true;
/*     */         }
/*     */       }  }
/*     */ 
/*     */ 
/*     */     
/* 233 */     for (ShulkerData data : this.openShulkerBoxes) {
/* 234 */       SimpleCollisionBox shulkerCollision = data.getCollision();
/* 235 */       if (playerBox.isCollided(shulkerCollision)) {
/* 236 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 241 */     for (PistonData data : this.activePistons) {
/* 242 */       for (SimpleCollisionBox box : data.boxes) {
/* 243 */         if (playerBox.isCollided(box)) {
/* 244 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 249 */     return false;
/*     */   }
/*     */   
/*     */   private static BaseChunk create() {
/* 253 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18))
/* 254 */       return (BaseChunk)new Chunk_v1_18(); 
/* 255 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
/* 256 */       return (BaseChunk)new Chunk_v1_9(0, DataPalette.createForChunk());
/*     */     }
/* 258 */     return (BaseChunk)new Chunk_v1_9(0, new DataPalette((Palette)new ListPalette(4), (BaseStorage)new LegacyFlexibleStorage(4, 4096), PaletteType.CHUNK));
/*     */   }
/*     */   
/*     */   public void updateBlock(Vector3i pos, WrappedBlockState state) {
/* 262 */     updateBlock(pos.getX(), pos.getY(), pos.getZ(), state.getGlobalId());
/*     */   }
/*     */   
/*     */   public void updateBlock(int x, int y, int z, int combinedID) {
/* 266 */     Vector3i asVector = new Vector3i(x, y, z);
/* 267 */     BlockPrediction prediction = (BlockPrediction)this.originalServerBlocks.get(asVector.getSerializedPosition());
/*     */     
/* 269 */     if (this.isCurrentlyPredicting) {
/* 270 */       if (prediction == null) {
/* 271 */         this.originalServerBlocks.put(asVector.getSerializedPosition(), new BlockPrediction(this.currentlyChangedBlocks, asVector, getBlock(asVector).getGlobalId(), new Vector3d(this.player.x, this.player.y, this.player.z)));
/*     */       } else {
/* 273 */         prediction.setForBlockUpdate(this.currentlyChangedBlocks);
/*     */       } 
/* 275 */       this.currentlyChangedBlocks.add(asVector);
/*     */     } 
/*     */     
/* 278 */     if (!this.isCurrentlyPredicting && prediction != null) {
/*     */       
/* 280 */       prediction.setOriginalBlockId(combinedID);
/*     */       
/*     */       return;
/*     */     } 
/* 284 */     Column column = getChunk(x >> 4, z >> 4);
/*     */ 
/*     */     
/* 287 */     int offsetY = y - this.minHeight;
/*     */     
/* 289 */     if (column != null) {
/* 290 */       if ((column.chunks()).length <= offsetY >> 4 || offsetY >> 4 < 0)
/*     */         return; 
/* 292 */       BaseChunk chunk = column.chunks()[offsetY >> 4];
/*     */       
/* 294 */       if (chunk == null) {
/* 295 */         chunk = create();
/* 296 */         column.chunks()[offsetY >> 4] = chunk;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 301 */         chunk.set(null, 0, 0, 0, 0);
/*     */       } 
/*     */ 
/*     */       
/* 305 */       this.player.pointThreeEstimator.handleChangeBlock(x, y, z, chunk.get(blockVersion, x & 0xF, offsetY & 0xF, z & 0xF));
/*     */       
/* 307 */       chunk.set(null, x & 0xF, offsetY & 0xF, z & 0xF, combinedID);
/*     */ 
/*     */       
/* 310 */       this.player.pointThreeEstimator.handleChangeBlock(x, y, z, WrappedBlockState.getByGlobalId(blockVersion, combinedID));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tickOpenable(int blockX, int blockY, int blockZ) {
/* 315 */     WrappedBlockState data = getBlock(blockX, blockY, blockZ);
/* 316 */     StateType type = data.getType();
/* 317 */     if (Materials.isClientSideOpenableDoor(type, this.player.getClientVersion())) {
/* 318 */       WrappedBlockState otherDoor = getBlock(blockX, blockY + (
/* 319 */           (data.getHalf() == Half.LOWER) ? 1 : -1), blockZ);
/*     */       
/* 321 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 322 */         if (BlockTags.DOORS.contains(otherDoor.getType())) {
/* 323 */           otherDoor.setOpen(!otherDoor.isOpen());
/* 324 */           updateBlock(blockX, blockY + ((data.getHalf() == Half.LOWER) ? 1 : -1), blockZ, otherDoor.getGlobalId());
/*     */         } 
/* 326 */         data.setOpen(!data.isOpen());
/* 327 */         updateBlock(blockX, blockY, blockZ, data.getGlobalId());
/*     */       
/*     */       }
/* 330 */       else if (data.getHalf() == Half.LOWER) {
/* 331 */         data.setOpen(!data.isOpen());
/* 332 */         updateBlock(blockX, blockY, blockZ, data.getGlobalId());
/* 333 */       } else if (BlockTags.DOORS.contains(otherDoor.getType()) && otherDoor.getHalf() == Half.LOWER) {
/*     */         
/* 335 */         otherDoor.setOpen(!otherDoor.isOpen());
/* 336 */         updateBlock(blockX, blockY - 1, blockZ, otherDoor.getGlobalId());
/*     */       }
/*     */     
/* 339 */     } else if (Materials.isClientSideOpenableTrapdoor(type, this.player.getClientVersion()) || BlockTags.FENCE_GATES.contains(type)) {
/*     */       
/* 341 */       data.setOpen(!data.isOpen());
/* 342 */       updateBlock(blockX, blockY, blockZ, data.getGlobalId());
/* 343 */     } else if (BlockTags.BUTTONS.contains(type)) {
/* 344 */       data.setPowered(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tickPlayerInPistonPushingArea() {
/* 349 */     this.player.uncertaintyHandler.tick();
/*     */     
/* 351 */     if (this.player.boundingBox == null)
/*     */       return; 
/* 353 */     SimpleCollisionBox expandedBB = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.lastX, this.player.lastY, this.player.lastZ, 0.001F, 0.001F);
/* 354 */     expandedBB.expandToAbsoluteCoordinates(this.player.x, this.player.y, this.player.z);
/* 355 */     SimpleCollisionBox playerBox = expandedBB.copy().expand(1.0D);
/*     */     
/* 357 */     double modX = 0.0D;
/* 358 */     double modY = 0.0D;
/* 359 */     double modZ = 0.0D;
/*     */     
/* 361 */     label40: for (PistonData data : this.activePistons) {
/* 362 */       for (SimpleCollisionBox box : data.boxes) {
/* 363 */         if (playerBox.isCollided(box)) {
/* 364 */           modX = Math.max(modX, Math.abs(data.direction.getModX() * 0.51D));
/* 365 */           modY = Math.max(modY, Math.abs(data.direction.getModY() * 0.51D));
/* 366 */           modZ = Math.max(modZ, Math.abs(data.direction.getModZ() * 0.51D));
/*     */           
/* 368 */           playerBox.expandMax(modX, modY, modZ);
/* 369 */           playerBox.expandMin(modX * -1.0D, modY * -1.0D, modZ * -1.0D);
/*     */           
/* 371 */           if (!data.hasSlimeBlock) { if (data.hasHoneyBlock && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_15_2)) {
/* 372 */               this.player.uncertaintyHandler.slimePistonBounces.add(data.direction);
/*     */             }
/*     */             continue; }
/*     */           
/*     */           continue label40;
/*     */         } 
/*     */       } 
/*     */     } 
/* 380 */     for (ShulkerData data : this.openShulkerBoxes) {
/* 381 */       BlockFace direction; SimpleCollisionBox shulkerCollision = data.getCollision();
/*     */ 
/*     */       
/* 384 */       if (data.entity == null) {
/* 385 */         WrappedBlockState state = getBlock(data.blockPos.getX(), data.blockPos.getY(), data.blockPos.getZ());
/* 386 */         direction = state.getFacing();
/*     */       } else {
/* 388 */         direction = ((PacketEntityShulker)data.entity).facing.getOppositeFace();
/*     */       } 
/*     */       
/* 391 */       if (direction == null) direction = BlockFace.UP;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 396 */       if (direction.getModX() == -1 || direction.getModY() == -1 || direction.getModZ() == -1) {
/* 397 */         shulkerCollision.expandMin(direction.getModX(), direction.getModY(), direction.getModZ());
/*     */       } else {
/* 399 */         shulkerCollision.expandMax(direction.getModZ(), direction.getModY(), direction.getModZ());
/*     */       } 
/*     */       
/* 402 */       if (playerBox.isCollided(shulkerCollision)) {
/* 403 */         modX = Math.max(modX, Math.abs(direction.getModX() * 0.51D));
/* 404 */         modY = Math.max(modY, Math.abs(direction.getModY() * 0.51D));
/* 405 */         modZ = Math.max(modZ, Math.abs(direction.getModZ() * 0.51D));
/*     */         
/* 407 */         playerBox.expandMax(modX, modY, modZ);
/* 408 */         playerBox.expandMin(modX, modY, modZ);
/*     */         
/* 410 */         this.player.uncertaintyHandler.isSteppingNearShulker = true;
/*     */       } 
/*     */     } 
/*     */     
/* 414 */     this.player.uncertaintyHandler.pistonX.add(Double.valueOf(modX));
/* 415 */     this.player.uncertaintyHandler.pistonY.add(Double.valueOf(modY));
/* 416 */     this.player.uncertaintyHandler.pistonZ.add(Double.valueOf(modZ));
/*     */     
/* 418 */     removeInvalidPistonLikeStuff(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeInvalidPistonLikeStuff(int transactionId) {
/* 423 */     if (transactionId != 0) {
/* 424 */       this.activePistons.removeIf(data -> (data.lastTransactionSent < transactionId));
/* 425 */       this.openShulkerBoxes.removeIf(data -> (data.isClosing && data.lastTransactionSent < transactionId));
/*     */     } else {
/* 427 */       this.activePistons.removeIf(PistonData::tickIfGuaranteedFinished);
/* 428 */       this.openShulkerBoxes.removeIf(ShulkerData::tickIfGuaranteedFinished);
/*     */     } 
/*     */     
/* 431 */     this.openShulkerBoxes.removeIf(box -> (box.blockPos != null) ? (!Materials.isShulker(getBlock(box.blockPos).getType())) : (!this.player.compensatedEntities.entityMap.containsValue(box.entity)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WrappedBlockState getBlock(Vector3i position) {
/* 441 */     return getBlock(position.x, position.y, position.z);
/*     */   }
/*     */   
/*     */   public WrappedBlockState getBlock(int x, int y, int z) {
/* 445 */     if (this.noNegativeBlocks && y < 0) return airData;
/*     */     
/*     */     try {
/* 448 */       Column column = getChunk(x >> 4, z >> 4);
/*     */       
/* 450 */       y -= this.minHeight;
/* 451 */       if (column == null || y < 0 || y >> 4 >= (column.chunks()).length) return airData;
/*     */       
/* 453 */       BaseChunk chunk = column.chunks()[y >> 4];
/* 454 */       if (chunk != null) {
/* 455 */         return chunk.get(blockVersion, x & 0xF, y & 0xF, z & 0xF);
/*     */       }
/* 457 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 460 */     return airData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRawPowerAtState(BlockFace face, int x, int y, int z) {
/* 467 */     WrappedBlockState block = getBlock(x, y, z);
/*     */     
/* 469 */     if (block.getType() == StateTypes.REDSTONE_BLOCK)
/* 470 */       return 15; 
/* 471 */     if (block.getType() == StateTypes.DETECTOR_RAIL)
/* 472 */       return block.isPowered() ? 15 : 0; 
/* 473 */     if (block.getType() == StateTypes.REDSTONE_TORCH)
/* 474 */       return (face != BlockFace.UP && block.isLit()) ? 15 : 0; 
/* 475 */     if (block.getType() == StateTypes.REDSTONE_WIRE) {
/* 476 */       BlockFace needed = face.getOppositeFace();
/*     */       
/* 478 */       BlockFace badOne = needed.getCW();
/* 479 */       BlockFace badTwo = needed.getCCW();
/*     */       
/* 481 */       boolean isPowered = false;
/* 482 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 483 */         switch (needed) {
/*     */           case DOWN:
/* 485 */             isPowered = true;
/*     */             break;
/*     */           case NORTH:
/* 488 */             isPowered = (block.getNorth() == North.TRUE);
/* 489 */             if (isPowered && (badOne == BlockFace.NORTH || badTwo == BlockFace.NORTH)) {
/* 490 */               return 0;
/*     */             }
/*     */             break;
/*     */           case SOUTH:
/* 494 */             isPowered = (block.getSouth() == South.TRUE);
/* 495 */             if (isPowered && (badOne == BlockFace.SOUTH || badTwo == BlockFace.SOUTH)) {
/* 496 */               return 0;
/*     */             }
/*     */             break;
/*     */           case WEST:
/* 500 */             isPowered = (block.getWest() == West.TRUE);
/* 501 */             if (isPowered && (badOne == BlockFace.WEST || badTwo == BlockFace.WEST)) {
/* 502 */               return 0;
/*     */             }
/*     */             break;
/*     */           case EAST:
/* 506 */             isPowered = (block.getEast() == East.TRUE);
/* 507 */             if (isPowered && (badOne == BlockFace.EAST || badTwo == BlockFace.EAST)) {
/* 508 */               return 0;
/*     */             }
/*     */             break;
/*     */         } 
/*     */       } else {
/* 513 */         isPowered = true;
/*     */       } 
/*     */       
/* 516 */       return isPowered ? block.getPower() : 0;
/* 517 */     }  if (block.getType() == StateTypes.REDSTONE_WALL_TORCH)
/* 518 */       return (block.getFacing() != face && block.isLit()) ? 15 : 0; 
/* 519 */     if (block.getType() == StateTypes.DAYLIGHT_DETECTOR)
/* 520 */       return block.getPower(); 
/* 521 */     if (block.getType() == StateTypes.OBSERVER)
/* 522 */       return (block.getFacing() == face && block.isPowered()) ? 15 : 0; 
/* 523 */     if (block.getType() == StateTypes.REPEATER)
/* 524 */       return (block.getFacing() == face && block.isPowered()) ? 15 : 0; 
/* 525 */     if (block.getType() == StateTypes.LECTERN)
/* 526 */       return block.isPowered() ? 15 : 0; 
/* 527 */     if (block.getType() == StateTypes.TARGET) {
/* 528 */       return block.getPower();
/*     */     }
/*     */     
/* 531 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDirectSignalAtState(BlockFace face, int x, int y, int z) {
/* 536 */     WrappedBlockState block = getBlock(x, y, z);
/*     */     
/* 538 */     if (block.getType() == StateTypes.DETECTOR_RAIL) {
/* 539 */       boolean isPowered = ((Boolean)block.getInternalData().getOrDefault(StateValue.POWERED, Boolean.valueOf(false))).booleanValue();
/* 540 */       return (face == BlockFace.UP && isPowered) ? 15 : 0;
/* 541 */     }  if (block.getType() == StateTypes.REDSTONE_TORCH)
/* 542 */       return (face != BlockFace.UP && block.isLit()) ? 15 : 0; 
/* 543 */     if (block.getType() == StateTypes.LEVER || BlockTags.BUTTONS.contains(block.getType()))
/* 544 */       return (block.getFacing().getOppositeFace() == face && block.isPowered()) ? 15 : 0; 
/* 545 */     if (block.getType() == StateTypes.REDSTONE_WALL_TORCH)
/* 546 */       return (face == BlockFace.DOWN && block.isPowered()) ? 15 : 0; 
/* 547 */     if (block.getType() == StateTypes.LECTERN)
/* 548 */       return (face == BlockFace.UP && block.isPowered()) ? 15 : 0; 
/* 549 */     if (block.getType() == StateTypes.OBSERVER)
/* 550 */       return (block.getFacing() == face && block.isPowered()) ? 15 : 0; 
/* 551 */     if (block.getType() == StateTypes.REPEATER)
/* 552 */       return (block.getFacing() == face && block.isPowered()) ? 15 : 0; 
/* 553 */     if (block.getType() == StateTypes.REDSTONE_WIRE) {
/* 554 */       BlockFace needed = face.getOppositeFace();
/*     */       
/* 556 */       BlockFace badOne = needed.getCW();
/* 557 */       BlockFace badTwo = needed.getCCW();
/*     */       
/* 559 */       boolean isPowered = false;
/* 560 */       switch (needed) {
/*     */ 
/*     */ 
/*     */         
/*     */         case NORTH:
/* 565 */           isPowered = (block.getNorth() == North.TRUE);
/* 566 */           if (isPowered && (badOne == BlockFace.NORTH || badTwo == BlockFace.NORTH)) {
/* 567 */             return 0;
/*     */           }
/*     */           break;
/*     */         case SOUTH:
/* 571 */           isPowered = (block.getSouth() == South.TRUE);
/* 572 */           if (isPowered && (badOne == BlockFace.SOUTH || badTwo == BlockFace.SOUTH)) {
/* 573 */             return 0;
/*     */           }
/*     */           break;
/*     */         case WEST:
/* 577 */           isPowered = (block.getWest() == West.TRUE);
/* 578 */           if (isPowered && (badOne == BlockFace.WEST || badTwo == BlockFace.WEST)) {
/* 579 */             return 0;
/*     */           }
/*     */           break;
/*     */         case EAST:
/* 583 */           isPowered = (block.getEast() == East.TRUE);
/* 584 */           if (isPowered && (badOne == BlockFace.EAST || badTwo == BlockFace.EAST)) {
/* 585 */             return 0;
/*     */           }
/*     */           break;
/*     */       } 
/*     */       
/* 590 */       return isPowered ? block.getPower() : 0;
/*     */     } 
/*     */     
/* 593 */     return 0;
/*     */   }
/*     */   
/*     */   public Column getChunk(int chunkX, int chunkZ) {
/* 597 */     long chunkPosition = chunkPositionToLong(chunkX, chunkZ);
/* 598 */     return (Column)this.chunks.get(chunkPosition);
/*     */   }
/*     */   
/*     */   public boolean isChunkLoaded(int chunkX, int chunkZ) {
/* 602 */     long chunkPosition = chunkPositionToLong(chunkX, chunkZ);
/* 603 */     return this.chunks.containsKey(chunkPosition);
/*     */   }
/*     */   
/*     */   public void addToCache(Column chunk, int chunkX, int chunkZ) {
/* 607 */     long chunkPosition = chunkPositionToLong(chunkX, chunkZ);
/* 608 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.chunks.put(chunkPosition, chunk));
/*     */   }
/*     */   
/*     */   public StateType getBlockType(double x, double y, double z) {
/* 612 */     return getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)).getType();
/*     */   }
/*     */   
/*     */   public WrappedBlockState getBlock(double x, double y, double z) {
/* 616 */     return getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
/*     */   }
/*     */   
/*     */   public double getFluidLevelAt(int x, int y, int z) {
/* 620 */     return Math.max(getWaterFluidLevelAt(x, y, z), getLavaFluidLevelAt(x, y, z));
/*     */   }
/*     */   
/*     */   public boolean isWaterSourceBlock(int x, int y, int z) {
/* 624 */     WrappedBlockState bukkitBlock = getBlock(x, y, z);
/* 625 */     return Materials.isWaterSource(this.player.getClientVersion(), bukkitBlock);
/*     */   }
/*     */   
/*     */   public boolean containsLiquid(SimpleCollisionBox var0) {
/* 629 */     return Collisions.hasMaterial(this.player, var0, data -> (Materials.isWater(this.player.getClientVersion(), (WrappedBlockState)data.first()) || ((WrappedBlockState)data.first()).getType() == StateTypes.LAVA));
/*     */   }
/*     */   
/*     */   public double getLavaFluidLevelAt(int x, int y, int z) {
/* 633 */     WrappedBlockState magicBlockState = getBlock(x, y, z);
/* 634 */     WrappedBlockState magicBlockStateAbove = getBlock(x, y + 1, z);
/*     */     
/* 636 */     if (magicBlockState.getType() != StateTypes.LAVA) return 0.0D; 
/* 637 */     if (magicBlockStateAbove.getType() == StateTypes.LAVA) return 1.0D;
/*     */     
/* 639 */     int level = magicBlockState.getLevel();
/*     */ 
/*     */     
/* 642 */     if (level >= 8)
/*     */     {
/* 644 */       return 0.8888888955116272D;
/*     */     }
/*     */     
/* 647 */     return ((8 - level) / 9.0F);
/*     */   }
/*     */   
/*     */   public boolean containsLava(SimpleCollisionBox var0) {
/* 651 */     return Collisions.hasMaterial(this.player, var0, data -> (((WrappedBlockState)data.first()).getType() == StateTypes.LAVA));
/*     */   }
/*     */   
/*     */   public double getWaterFluidLevelAt(double x, double y, double z) {
/* 655 */     return getWaterFluidLevelAt(GrimMath.floor(x), GrimMath.floor(y), GrimMath.floor(z));
/*     */   }
/*     */   
/*     */   public double getWaterFluidLevelAt(int x, int y, int z) {
/* 659 */     WrappedBlockState wrappedBlock = getBlock(x, y, z);
/* 660 */     boolean isWater = Materials.isWater(this.player.getClientVersion(), wrappedBlock);
/*     */     
/* 662 */     if (!isWater) return 0.0D;
/*     */ 
/*     */     
/* 665 */     if (Materials.isWater(this.player.getClientVersion(), getBlock(x, y + 1, z))) {
/* 666 */       return 1.0D;
/*     */     }
/*     */ 
/*     */     
/* 670 */     if (wrappedBlock.getType() == StateTypes.WATER) {
/* 671 */       int level = wrappedBlock.getLevel();
/*     */ 
/*     */       
/* 674 */       if ((level & 0x8) == 8) return 0.8888888955116272D;
/*     */       
/* 676 */       return ((8 - level) / 9.0F);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 681 */     return 0.8888888955116272D;
/*     */   }
/*     */   
/*     */   public void removeChunkLater(int chunkX, int chunkZ) {
/* 685 */     long chunkPosition = chunkPositionToLong(chunkX, chunkZ);
/* 686 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.chunks.remove(chunkPosition));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDimension(DimensionType dimension, User user) {
/* 691 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17))
/*     */       return; 
/* 693 */     this.minHeight = dimension.getMinY();
/* 694 */     this.maxHeight = this.minHeight + dimension.getHeight();
/*     */   }
/*     */   
/*     */   public WrappedBlockState getBlock(Vector3dm aboveCCWPos) {
/* 698 */     return getBlock(aboveCCWPos.getX(), aboveCCWPos.getY(), aboveCCWPos.getZ());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\CompensatedWorld.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
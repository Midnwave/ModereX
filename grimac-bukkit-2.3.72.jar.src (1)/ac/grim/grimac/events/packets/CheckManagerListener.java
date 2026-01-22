/*     */ package ac.grim.grimac.events.packets;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Location;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientVehicleMove;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*     */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*     */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*     */ import ac.grim.grimac.utils.anticheat.update.VehiclePositionUpdate;
/*     */ import ac.grim.grimac.utils.data.BlockPlaceSnapshot;
/*     */ import ac.grim.grimac.utils.data.HeadRotation;
/*     */ import ac.grim.grimac.utils.data.HitData;
/*     */ import ac.grim.grimac.utils.data.RotationData;
/*     */ import ac.grim.grimac.utils.data.TeleportAcceptData;
/*     */ import ac.grim.grimac.utils.data.VelocityData;
/*     */ import ac.grim.grimac.utils.math.VectorUtils;
/*     */ import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ import ac.grim.grimac.utils.nmsutil.WorldRayTrace;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CheckManagerListener extends PacketListenerAbstract {
/*     */   static {
/*  52 */     BREAKABLE = (type -> Boolean.valueOf((!type.isAir() && type.getHardness() != -1.0F && type != StateTypes.WATER && type != StateTypes.LAVA)));
/*     */   } private static final Function<StateType, Boolean> BREAKABLE;
/*     */   public CheckManagerListener() {
/*  55 */     super(PacketListenerPriority.LOW);
/*     */   }
/*     */   
/*     */   private static void placeWaterLavaSnowBucket(GrimPlayer player, ItemStack held, StateType toPlace, InteractionHand hand, int sequence) {
/*  59 */     HitData data = WorldRayTrace.getNearestBlockHitResult(player, StateTypes.AIR, false, true, true);
/*  60 */     if (data != null) {
/*  61 */       BlockPlace blockPlace = new BlockPlace(player, hand, data.position(), data.closestDirection().getFaceValue(), data.closestDirection(), held, data, sequence);
/*     */       
/*  63 */       boolean didPlace = false;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  68 */       if (Materials.isPlaceableWaterBucket(blockPlace.itemStack.getType()) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/*  69 */         blockPlace.replaceClicked = true;
/*  70 */         WrappedBlockState existing = blockPlace.getExistingBlockData();
/*  71 */         if (!((Boolean)existing.getInternalData().getOrDefault(StateValue.WATERLOGGED, Boolean.valueOf(true))).booleanValue())
/*     */         {
/*  73 */           didPlace = true;
/*     */         }
/*     */       } 
/*     */       
/*  77 */       if (!didPlace) {
/*     */         
/*  79 */         blockPlace.replaceClicked = false;
/*  80 */         blockPlace.set(toPlace);
/*     */       } 
/*     */       
/*  83 */       if (player.gamemode != GameMode.CREATIVE) {
/*  84 */         player.inventory.markSlotAsResyncing(blockPlace);
/*  85 */         if (hand == InteractionHand.MAIN_HAND) {
/*  86 */           player.inventory.inventory.setHeldItem(ItemStack.builder().type(ItemTypes.BUCKET).amount(1).build());
/*     */         } else {
/*  88 */           player.inventory.inventory.setPlayerInventoryItem(45, ItemStack.builder().type(ItemTypes.BUCKET).amount(1).build());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void handleQueuedPlaces(GrimPlayer player, boolean hasLook, float pitch, float yaw, long now) {
/*     */     BlockPlaceSnapshot snapshot;
/*  97 */     while ((snapshot = player.placeUseItemPackets.poll()) != null) {
/*  98 */       double lastX = player.x;
/*  99 */       double lastY = player.y;
/* 100 */       double lastZ = player.z;
/*     */       
/* 102 */       player.x = player.packetStateData.lastClaimedPosition.getX();
/* 103 */       player.y = player.packetStateData.lastClaimedPosition.getY();
/* 104 */       player.z = player.packetStateData.lastClaimedPosition.getZ();
/*     */       
/* 106 */       boolean lastSneaking = player.isSneaking;
/* 107 */       player.isSneaking = snapshot.isSneaking();
/*     */       
/* 109 */       if (player.inVehicle()) {
/* 110 */         Vector3d posFromVehicle = BoundingBoxSize.getRidingOffsetFromVehicle(player.compensatedEntities.self.getRiding(), player);
/* 111 */         player.x = posFromVehicle.getX();
/* 112 */         player.y = posFromVehicle.getY();
/* 113 */         player.z = posFromVehicle.getZ();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 119 */       if ((now - player.lastBlockPlaceUseItem < 15L || player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) && hasLook) {
/* 120 */         player.xRot = yaw;
/* 121 */         player.yRot = pitch;
/*     */       } 
/*     */       
/* 124 */       player.compensatedWorld.startPredicting();
/* 125 */       handleBlockPlaceOrUseItem(snapshot.getWrapper(), player);
/* 126 */       player.compensatedWorld.stopPredicting(snapshot.getWrapper());
/*     */       
/* 128 */       player.x = lastX;
/* 129 */       player.y = lastY;
/* 130 */       player.z = lastZ;
/* 131 */       player.isSneaking = lastSneaking;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void handleQueuedBreaks(GrimPlayer player, boolean hasLook, float pitch, float yaw, long now) {
/*     */     BlockBreak blockBreak;
/* 137 */     while ((blockBreak = player.queuedBreaks.poll()) != null) {
/* 138 */       double lastX = player.x;
/* 139 */       double lastY = player.y;
/* 140 */       double lastZ = player.z;
/*     */       
/* 142 */       player.x = player.packetStateData.lastClaimedPosition.getX();
/* 143 */       player.y = player.packetStateData.lastClaimedPosition.getY();
/* 144 */       player.z = player.packetStateData.lastClaimedPosition.getZ();
/*     */       
/* 146 */       if (player.inVehicle()) {
/* 147 */         Vector3d posFromVehicle = BoundingBoxSize.getRidingOffsetFromVehicle(player.compensatedEntities.self.getRiding(), player);
/* 148 */         player.x = posFromVehicle.getX();
/* 149 */         player.y = posFromVehicle.getY();
/* 150 */         player.z = posFromVehicle.getZ();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 156 */       if ((now - player.lastBlockBreak < 15L || player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) && hasLook) {
/* 157 */         player.xRot = yaw;
/* 158 */         player.yRot = pitch;
/*     */       } 
/*     */       
/* 161 */       player.checkManager.onPostFlyingBlockBreak(blockBreak);
/*     */       
/* 163 */       player.x = lastX;
/* 164 */       player.y = lastY;
/* 165 */       player.z = lastZ;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void handleUseItem(GrimPlayer player, ItemStack placedWith, InteractionHand hand, int sequence) {
/* 171 */     if (placedWith.getType() == ItemTypes.LILY_PAD) {
/* 172 */       placeLilypad(player, hand, sequence);
/*     */       
/*     */       return;
/*     */     } 
/* 176 */     StateType toBucketMat = Materials.transformBucketMaterial(placedWith.getType());
/* 177 */     if (toBucketMat != null) {
/* 178 */       placeWaterLavaSnowBucket(player, placedWith, toBucketMat, hand, sequence);
/*     */     }
/*     */     
/* 181 */     if (placedWith.getType() == ItemTypes.BUCKET) {
/* 182 */       placeBucket(player, hand, sequence);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void handleBlockPlaceOrUseItem(PacketWrapper<?> packet, GrimPlayer player) {
/* 188 */     if (packet instanceof WrapperPlayClientPlayerBlockPlacement) { WrapperPlayClientPlayerBlockPlacement place = (WrapperPlayClientPlayerBlockPlacement)packet;
/* 189 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/*     */         
/* 191 */         if (player.gamemode == GameMode.SPECTATOR || player.gamemode == GameMode.ADVENTURE) {
/*     */           return;
/*     */         }
/* 194 */         if (place.getFace() == BlockFace.OTHER) {
/* 195 */           ItemStack placedWith = player.inventory.getHeldItem();
/* 196 */           if (place.getHand() == InteractionHand.OFF_HAND) {
/* 197 */             placedWith = player.inventory.getOffHand();
/*     */           }
/*     */           
/* 200 */           handleUseItem(player, placedWith, place.getHand(), place.getSequence());
/*     */           return;
/*     */         } 
/*     */       }  }
/*     */     
/* 205 */     if (packet instanceof WrapperPlayClientUseItem) { WrapperPlayClientUseItem place = (WrapperPlayClientUseItem)packet;
/* 206 */       if (player.gamemode == GameMode.SPECTATOR || player.gamemode == GameMode.ADVENTURE) {
/*     */         return;
/*     */       }
/* 209 */       ItemStack placedWith = player.inventory.getHeldItem();
/* 210 */       if (place.getHand() == InteractionHand.OFF_HAND) {
/* 211 */         placedWith = player.inventory.getOffHand();
/*     */       }
/*     */       
/* 214 */       handleUseItem(player, placedWith, place.getHand(), place.getSequence()); }
/*     */ 
/*     */ 
/*     */     
/* 218 */     if (packet instanceof WrapperPlayClientPlayerBlockPlacement) { WrapperPlayClientPlayerBlockPlacement place = (WrapperPlayClientPlayerBlockPlacement)packet;
/* 219 */       ItemStack placedWith = player.inventory.getHeldItem();
/* 220 */       ItemStack offhand = player.inventory.getOffHand();
/*     */       
/* 222 */       boolean onlyAir = (placedWith.isEmpty() && offhand.isEmpty());
/*     */ 
/*     */       
/* 225 */       if ((!player.isSneaking || onlyAir) && place.getHand() == InteractionHand.MAIN_HAND) {
/* 226 */         Vector3i blockPosition = place.getBlockPosition();
/* 227 */         BlockPlace blockPlace = new BlockPlace(player, place.getHand(), blockPosition, place.getFaceId(), place.getFace(), placedWith, WorldRayTrace.getNearestBlockHitResult(player, null, true, false, false), place.getSequence());
/*     */ 
/*     */         
/* 230 */         StateType placedAgainst = blockPlace.getPlacedAgainstMaterial();
/* 231 */         if ((player.getClientVersion().isOlderThan(ClientVersion.V_1_11) && (placedAgainst == StateTypes.IRON_TRAPDOOR || placedAgainst == StateTypes.IRON_DOOR || BlockTags.FENCES
/* 232 */           .contains(placedAgainst))) || (player
/* 233 */           .getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8) && BlockTags.CAULDRONS.contains(placedAgainst)) || 
/* 234 */           Materials.isClientSideInteractable(placedAgainst)) {
/* 235 */           player.checkManager.onPostFlyingBlockPlace(blockPlace);
/* 236 */           Vector3i location = blockPlace.position;
/* 237 */           player.compensatedWorld.tickOpenable(location.x, location.y, location.z);
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */ 
/*     */         
/* 244 */         if (ConsumesBlockPlace.consumesPlace(player, player.compensatedWorld.getBlock(blockPlace.position), blockPlace)) {
/* 245 */           player.checkManager.onPostFlyingBlockPlace(blockPlace);
/*     */           
/*     */           return;
/*     */         } 
/*     */       }  }
/*     */     
/* 251 */     if (packet instanceof WrapperPlayClientPlayerBlockPlacement) { WrapperPlayClientPlayerBlockPlacement place = (WrapperPlayClientPlayerBlockPlacement)packet;
/* 252 */       if (player.gamemode == GameMode.SPECTATOR || player.gamemode == GameMode.ADVENTURE) {
/*     */         return;
/*     */       }
/* 255 */       Vector3i blockPosition = place.getBlockPosition();
/* 256 */       BlockFace face = place.getFace();
/* 257 */       ItemStack placedWith = player.inventory.getHeldItem();
/* 258 */       if (place.getHand() == InteractionHand.OFF_HAND) {
/* 259 */         placedWith = player.inventory.getOffHand();
/*     */       }
/*     */       
/* 262 */       BlockPlace blockPlace = new BlockPlace(player, place.getHand(), blockPosition, place.getFaceId(), face, placedWith, WorldRayTrace.getNearestBlockHitResult(player, null, true, false, false), place.getSequence());
/*     */       
/* 264 */       player.checkManager.onPostFlyingBlockPlace(blockPlace);
/*     */       
/* 266 */       blockPlace.isInside = ((Boolean)place.getInsideBlock().orElse(Boolean.valueOf(false))).booleanValue();
/*     */       
/* 268 */       if (placedWith.getType().getPlacedType() != null || placedWith.getType() == ItemTypes.FLINT_AND_STEEL || placedWith.getType() == ItemTypes.FIRE_CHARGE) {
/* 269 */         BlockPlaceResult.getMaterialData(placedWith.getType()).applyBlockPlaceToWorld(player, blockPlace);
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   private static void placeBucket(GrimPlayer player, InteractionHand hand, int sequence) {
/* 275 */     HitData data = WorldRayTrace.getNearestBlockHitResult(player, null, true, false, true);
/*     */     
/* 277 */     if (data != null) {
/* 278 */       BlockPlace blockPlace = new BlockPlace(player, hand, data.position(), data.closestDirection().getFaceValue(), data.closestDirection(), ItemStack.EMPTY, data, sequence);
/* 279 */       blockPlace.replaceClicked = true;
/*     */       
/* 281 */       boolean placed = false;
/* 282 */       ItemType type = null;
/*     */       
/* 284 */       if (data.state().getType() == StateTypes.POWDER_SNOW) {
/* 285 */         blockPlace.set(StateTypes.AIR);
/* 286 */         type = ItemTypes.POWDER_SNOW_BUCKET;
/* 287 */         placed = true;
/*     */       } 
/*     */       
/* 290 */       if (data.state().getType() == StateTypes.LAVA) {
/* 291 */         blockPlace.set(StateTypes.AIR);
/* 292 */         type = ItemTypes.LAVA_BUCKET;
/* 293 */         placed = true;
/*     */       } 
/*     */ 
/*     */       
/* 297 */       if (!placed && !player.compensatedWorld.isWaterSourceBlock(data.position().getX(), data.position().getY(), data.position().getZ())) {
/*     */         return;
/*     */       }
/*     */       
/* 301 */       if (data.state().getType() == StateTypes.KELP || data.state().getType() == StateTypes.SEAGRASS || data.state().getType() == StateTypes.TALL_SEAGRASS) {
/*     */         return;
/*     */       }
/*     */       
/* 305 */       if (!placed) {
/* 306 */         type = ItemTypes.WATER_BUCKET;
/*     */       }
/*     */       
/* 309 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 310 */         WrappedBlockState existing = blockPlace.getExistingBlockData();
/* 311 */         if (existing.getInternalData().containsKey(StateValue.WATERLOGGED)) {
/* 312 */           existing.setWaterlogged(false);
/* 313 */           blockPlace.set(existing);
/* 314 */           placed = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 319 */       if (!placed) {
/* 320 */         blockPlace.set(StateTypes.AIR);
/*     */       }
/*     */       
/* 323 */       if (player.gamemode != GameMode.CREATIVE) {
/* 324 */         player.inventory.markSlotAsResyncing(blockPlace);
/* 325 */         setPlayerItem(player, hand, type);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setPlayerItem(GrimPlayer player, InteractionHand hand, ItemType type) {
/* 332 */     if (player.gamemode != GameMode.CREATIVE) {
/* 333 */       if (hand == InteractionHand.MAIN_HAND) {
/* 334 */         if (player.inventory.getHeldItem().getAmount() == 1) {
/* 335 */           player.inventory.inventory.setHeldItem(ItemStack.builder().type(type).amount(1).build());
/*     */         } else {
/* 337 */           player.inventory.inventory.add(ItemStack.builder().type(type).amount(1).build());
/*     */           
/* 339 */           player.inventory.getHeldItem().setAmount(player.inventory.getHeldItem().getAmount() - 1);
/*     */         }
/*     */       
/* 342 */       } else if (player.inventory.getOffHand().getAmount() == 1) {
/* 343 */         player.inventory.inventory.setPlayerInventoryItem(45, ItemStack.builder().type(type).amount(1).build());
/*     */       } else {
/* 345 */         player.inventory.inventory.add(45, ItemStack.builder().type(type).amount(1).build());
/*     */         
/* 347 */         player.inventory.getOffHand().setAmount(player.inventory.getOffHand().getAmount() - 1);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void placeLilypad(GrimPlayer player, InteractionHand hand, int sequence) {
/* 354 */     HitData data = WorldRayTrace.getNearestBlockHitResult(player, null, true, false, true);
/*     */     
/* 356 */     if (data != null) {
/*     */       
/* 358 */       if (player.compensatedWorld.getFluidLevelAt(data.position().getX(), data.position().getY() + 1, data.position().getZ()) > 0.0D) {
/*     */         return;
/*     */       }
/* 361 */       BlockPlace blockPlace = new BlockPlace(player, hand, data.position(), data.closestDirection().getFaceValue(), data.closestDirection(), ItemStack.EMPTY, data, sequence);
/* 362 */       blockPlace.replaceClicked = false;
/*     */ 
/*     */       
/* 365 */       if (player.compensatedWorld.getWaterFluidLevelAt(data.position().getX(), data.position().getY(), data.position().getZ()) > 0.0D || data
/* 366 */         .state().getType() == StateTypes.ICE || data.state().getType() == StateTypes.FROSTED_ICE) {
/* 367 */         Vector3i pos = data.position();
/* 368 */         pos = pos.add(0, 1, 0);
/*     */         
/* 370 */         blockPlace.set(pos, StateTypes.LILY_PAD.createBlockState(CompensatedWorld.blockVersion));
/*     */         
/* 372 */         if (player.gamemode != GameMode.CREATIVE) {
/* 373 */           player.inventory.markSlotAsResyncing(blockPlace);
/* 374 */           if (hand == InteractionHand.MAIN_HAND) {
/* 375 */             player.inventory.inventory.getHeldItem().setAmount(player.inventory.inventory.getHeldItem().getAmount() - 1);
/*     */           } else {
/* 377 */             player.inventory.getOffHand().setAmount(player.inventory.getOffHand().getAmount() - 1);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/* 386 */     GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 387 */     if (player == null)
/*     */       return; 
/* 389 */     if (event.getConnectionState() != ConnectionState.PLAY) {
/*     */       
/* 391 */       if (event.getConnectionState() != ConnectionState.CONFIGURATION)
/* 392 */         return;  player.checkManager.onPacketReceive(event);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 397 */     if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
/* 398 */       WrapperPlayClientVehicleMove move = new WrapperPlayClientVehicleMove(event);
/* 399 */       Vector3d position = move.getPosition();
/* 400 */       player.packetStateData.lastPacketWasTeleport = player.getSetbackTeleportUtil().checkVehicleTeleportQueue(position.getX(), position.getY(), position.getZ());
/*     */     } 
/*     */     
/* 403 */     TeleportAcceptData teleportData = null;
/*     */     
/* 405 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/* 406 */       player.serverOpenedInventoryThisTick = false;
/*     */       
/* 408 */       WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
/*     */       
/* 410 */       Vector3d position = VectorUtils.clampVector(flying.getLocation().getPosition());
/*     */       
/* 412 */       teleportData = (flying.hasPositionChanged() && flying.hasRotationChanged()) ? player.getSetbackTeleportUtil().checkTeleportQueue(position.getX(), position.getY(), position.getZ()) : new TeleportAcceptData();
/* 413 */       player.packetStateData.lastPacketWasTeleport = teleportData.isTeleport();
/*     */       
/* 415 */       if (flying.hasRotationChanged() && !flying.hasPositionChanged() && !flying.isOnGround() && !flying.isHorizontalCollision()) {
/* 416 */         List<RotationData> rotations = new ArrayList<>();
/*     */         
/* 418 */         for (RotationData data : player.pendingRotations) {
/* 419 */           rotations.add(data);
/* 420 */           if (!data.isAccepted()) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/* 426 */         Collections.reverse(rotations);
/*     */         
/* 428 */         for (RotationData data : rotations) {
/* 429 */           if (data.getYaw() == flying.getLocation().getYaw() && data.getPitch() == flying.getLocation().getPitch() && data.getTransaction() == player.getLastTransactionReceived()) {
/* 430 */             player.packetStateData.lastPacketWasTeleport = true;
/* 431 */             data.accept();
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 437 */       player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = isMojangStupid(player, event, flying);
/*     */     } 
/*     */     
/* 440 */     if (player.inVehicle() ? (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) : (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate)) {
/*     */       
/* 442 */       int kbEntityId = player.inVehicle() ? player.getRidingVehicleId() : player.entityID;
/*     */       
/* 444 */       VelocityData calculatedFirstBreadKb = player.checkManager.getKnockbackHandler().calculateFirstBreadKnockback(kbEntityId, player.lastTransactionReceived.get());
/* 445 */       VelocityData calculatedRequireKb = player.checkManager.getKnockbackHandler().calculateRequiredKB(kbEntityId, player.lastTransactionReceived.get(), false);
/* 446 */       player.firstBreadKB = (calculatedFirstBreadKb == null) ? player.firstBreadKB : calculatedFirstBreadKb;
/* 447 */       player.likelyKB = (calculatedRequireKb == null) ? player.likelyKB : calculatedRequireKb;
/*     */       
/* 449 */       VelocityData calculateFirstBreadExplosion = player.checkManager.getExplosionHandler().getFirstBreadAddedExplosion(player.lastTransactionReceived.get());
/* 450 */       VelocityData calculateRequiredExplosion = player.checkManager.getExplosionHandler().getPossibleExplosions(player.lastTransactionReceived.get(), false);
/* 451 */       player.firstBreadExplosion = (calculateFirstBreadExplosion == null) ? player.firstBreadExplosion : calculateFirstBreadExplosion;
/* 452 */       player.likelyExplosions = (calculateRequiredExplosion == null) ? player.likelyExplosions : calculateRequiredExplosion;
/*     */     } 
/*     */     
/* 455 */     player.checkManager.onPrePredictionReceivePacket(event);
/*     */ 
/*     */     
/* 458 */     if (event.isCancelled() && (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) || event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE)) {
/* 459 */       player.packetStateData.cancelDuplicatePacket = false;
/*     */       
/*     */       return;
/*     */     } 
/* 463 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/* 464 */       WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
/* 465 */       Location pos = flying.getLocation();
/* 466 */       boolean ignoreRotation = (player.packetStateData.lastPacketWasOnePointSeventeenDuplicate && player.isIgnoreDuplicatePacketRotation());
/* 467 */       handleFlying(player, pos.getX(), pos.getY(), pos.getZ(), ignoreRotation ? 0.0F : pos.getYaw(), ignoreRotation ? 0.0F : pos.getPitch(), flying.hasPositionChanged(), (flying.hasRotationChanged() && !ignoreRotation), flying.isOnGround(), teleportData, event);
/*     */     } 
/*     */     
/* 470 */     if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE && player.inVehicle()) {
/* 471 */       WrapperPlayClientVehicleMove move = new WrapperPlayClientVehicleMove(event);
/* 472 */       Vector3d position = move.getPosition();
/*     */       
/* 474 */       player.lastX = player.x;
/* 475 */       player.lastY = player.y;
/* 476 */       player.lastZ = player.z;
/*     */       
/* 478 */       Vector3d clamp = VectorUtils.clampVector(position);
/* 479 */       player.x = clamp.getX();
/* 480 */       player.y = clamp.getY();
/* 481 */       player.z = clamp.getZ();
/*     */       
/* 483 */       player.xRot = move.getYaw();
/* 484 */       player.yRot = move.getPitch();
/*     */       
/* 486 */       VehiclePositionUpdate update = new VehiclePositionUpdate(clamp, position, move.getYaw(), move.getPitch(), player.packetStateData.lastPacketWasTeleport);
/* 487 */       player.checkManager.onVehiclePositionUpdate(update);
/*     */       
/* 489 */       player.packetStateData.receivedSteerVehicle = false;
/*     */     } 
/*     */     
/* 492 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/* 493 */       handleDigging(player, event);
/*     */     }
/*     */     
/* 496 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
/* 497 */       WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
/* 498 */       player.lastBlockPlaceUseItem = System.currentTimeMillis();
/*     */       
/* 500 */       ItemStack placedWith = player.inventory.getHeldItem();
/* 501 */       if (packet.getHand() == InteractionHand.OFF_HAND) {
/* 502 */         placedWith = player.inventory.getOffHand();
/*     */       }
/*     */ 
/*     */       
/* 506 */       if (packet.getFace() == BlockFace.OTHER && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/* 507 */         player.placeUseItemPackets.add(new BlockPlaceSnapshot((PacketWrapper)packet, player.isSneaking));
/*     */       } else {
/*     */         
/* 510 */         BlockPlace blockPlace = new BlockPlace(player, packet.getHand(), packet.getBlockPosition(), packet.getFaceId(), packet.getFace(), placedWith, WorldRayTrace.getNearestBlockHitResult(player, null, true, false, false), packet.getSequence());
/* 511 */         blockPlace.cursor = packet.getCursorPosition();
/*     */         
/* 513 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_11) && player.getClientVersion().isOlderThan(ClientVersion.V_1_11))
/*     */         {
/*     */           
/* 516 */           if (packet.getCursorPosition().getX() * 15.0F % 1.0F == 0.0F && packet.getCursorPosition().getY() * 15.0F % 1.0F == 0.0F && packet.getCursorPosition().getZ() * 15.0F % 1.0F == 0.0F) {
/*     */             
/* 518 */             int trueByteX = (int)(packet.getCursorPosition().getX() * 15.0F);
/* 519 */             int trueByteY = (int)(packet.getCursorPosition().getY() * 15.0F);
/* 520 */             int trueByteZ = (int)(packet.getCursorPosition().getZ() * 15.0F);
/*     */             
/* 522 */             blockPlace.cursor = new Vector3f(trueByteX / 16.0F, trueByteY / 16.0F, trueByteZ / 16.0F);
/*     */           } 
/*     */         }
/*     */         
/* 526 */         player.checkManager.onBlockPlace(blockPlace);
/*     */         
/* 528 */         if (event.isCancelled() || blockPlace.isCancelled() || player.getSetbackTeleportUtil().shouldBlockMovement()) {
/*     */           
/* 530 */           if (!event.isCancelled()) {
/* 531 */             event.setCancelled(true);
/* 532 */             player.onPacketCancel();
/*     */           } 
/*     */           
/* 535 */           Vector3i facePos = new Vector3i(packet.getBlockPosition().getX() + packet.getFace().getModX(), packet.getBlockPosition().getY() + packet.getFace().getModY(), packet.getBlockPosition().getZ() + packet.getFace().getModZ());
/*     */ 
/*     */           
/* 538 */           if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19)) {
/* 539 */             player.user.sendPacket((PacketWrapper)new WrapperPlayServerAcknowledgeBlockChanges(packet.getSequence()));
/*     */           } else {
/* 541 */             player.resyncPosition(packet.getBlockPosition());
/* 542 */             player.resyncPosition(facePos);
/*     */           } 
/*     */ 
/*     */           
/* 546 */           if (player.platformPlayer != null) {
/* 547 */             if (packet.getHand() == InteractionHand.MAIN_HAND) {
/* 548 */               ItemStack mainHand = player.platformPlayer.getInventory().getItemInHand();
/* 549 */               player.user.sendPacket((PacketWrapper)new WrapperPlayServerSetSlot(0, player.inventory.stateID, 36 + player.packetStateData.lastSlotSelected, mainHand));
/*     */             } else {
/* 551 */               ItemStack offHand = player.platformPlayer.getInventory().getItemInOffHand();
/* 552 */               player.user.sendPacket((PacketWrapper)new WrapperPlayServerSetSlot(0, player.inventory.stateID, 45, offHand));
/*     */             } 
/*     */           }
/*     */         } else {
/*     */           
/* 557 */           player.placeUseItemPackets.add(new BlockPlaceSnapshot((PacketWrapper)packet, player.isSneaking));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 562 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
/* 563 */       WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);
/* 564 */       player.placeUseItemPackets.add(new BlockPlaceSnapshot((PacketWrapper)packet, player.isSneaking));
/* 565 */       player.lastBlockPlaceUseItem = System.currentTimeMillis();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 570 */     player.checkManager.onPacketReceive(event);
/*     */     
/* 572 */     if (player.packetStateData.cancelDuplicatePacket) {
/* 573 */       event.setCancelled(true);
/* 574 */       player.packetStateData.cancelDuplicatePacket = false;
/*     */     } 
/*     */     
/* 577 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
/* 578 */       player.serverOpenedInventoryThisTick = false;
/* 579 */       if (!player.packetStateData.didSendMovementBeforeTickEnd) {
/*     */         
/* 581 */         player.packetStateData.didLastLastMovementIncludePosition = player.packetStateData.didLastMovementIncludePosition;
/* 582 */         player.packetStateData.didLastMovementIncludePosition = false;
/*     */       } 
/* 584 */       player.packetStateData.didSendMovementBeforeTickEnd = false;
/*     */     } 
/*     */ 
/*     */     
/* 588 */     player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = false;
/* 589 */     player.packetStateData.lastPacketWasTeleport = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/* 594 */     if (event.getConnectionState() != ConnectionState.PLAY)
/* 595 */       return;  GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 596 */     if (player == null)
/*     */       return; 
/* 598 */     if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
/* 599 */       player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.serverOpenedInventoryThisTick = true);
/*     */     }
/*     */     
/* 602 */     player.checkManager.onPacketSend(event);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isMojangStupid(GrimPlayer player, PacketReceiveEvent event, WrapperPlayClientPlayerFlying flying) {
/* 607 */     if (player.packetStateData.lastPacketWasTeleport) return false;
/*     */     
/* 609 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)) return false;
/*     */     
/* 611 */     Location location = flying.getLocation();
/* 612 */     double threshold = player.getMovementThreshold();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 619 */     if (!player.packetStateData.lastPacketWasTeleport && flying.hasPositionChanged() && flying.hasRotationChanged() && ((flying
/*     */       
/* 621 */       .isOnGround() == player.packetStateData.packetPlayerOnGround && player
/*     */       
/* 623 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17) && player.filterMojangStupidityOnMojangStupidity
/*     */       
/* 625 */       .distanceSquared(location.getPosition()) < threshold * threshold) || player
/*     */       
/* 627 */       .inVehicle())) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 632 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9)) {
/* 633 */         if (player.isCancelDuplicatePacket()) {
/* 634 */           player.packetStateData.cancelDuplicatePacket = true;
/*     */         }
/*     */       } else {
/*     */         
/* 638 */         flying.setLocation(new Location(player.filterMojangStupidityOnMojangStupidity.getX(), player.filterMojangStupidityOnMojangStupidity.getY(), player.filterMojangStupidityOnMojangStupidity.getZ(), location.getYaw(), location.getPitch()));
/* 639 */         event.markForReEncode(true);
/*     */       } 
/*     */       
/* 642 */       player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = true;
/*     */       
/* 644 */       if (!player.isIgnoreDuplicatePacketRotation()) {
/* 645 */         if (player.xRot != location.getYaw() || player.yRot != location.getPitch()) {
/* 646 */           player.lastXRot = player.xRot;
/* 647 */           player.lastYRot = player.yRot;
/*     */         } 
/*     */ 
/*     */         
/* 651 */         player.xRot = location.getYaw();
/* 652 */         player.yRot = location.getPitch();
/*     */       } 
/*     */       
/* 655 */       player.packetStateData.lastClaimedPosition = location.getPosition();
/* 656 */       return true;
/*     */     } 
/* 658 */     return false;
/*     */   }
/*     */   
/*     */   private static void handleFlying(GrimPlayer player, double x, double y, double z, float yaw, float pitch, boolean hasPosition, boolean hasLook, boolean onGround, TeleportAcceptData teleportData, PacketReceiveEvent event) {
/* 662 */     long now = System.currentTimeMillis();
/*     */     
/* 664 */     if (!hasPosition)
/*     */     {
/*     */       
/* 667 */       player.uncertaintyHandler.lastPointThree.reset();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 674 */     if (hasLook && (!player.packetStateData.lastPacketWasOnePointSeventeenDuplicate || player.xRot != yaw || player.yRot != pitch)) {
/*     */       
/* 676 */       player.lastXRot = player.xRot;
/* 677 */       player.lastYRot = player.yRot;
/*     */     } 
/*     */     
/* 680 */     handleQueuedPlaces(player, hasLook, pitch, yaw, now);
/* 681 */     handleQueuedBreaks(player, hasLook, pitch, yaw, now);
/*     */ 
/*     */     
/* 684 */     if (hasPosition) {
/* 685 */       player.packetStateData.lastClaimedPosition = new Vector3d(x, y, z);
/*     */     }
/*     */ 
/*     */     
/* 689 */     if (!hasPosition && onGround != player.packetStateData.packetPlayerOnGround && !player.inVehicle()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 694 */       boolean canFeasiblyPointThree = Collisions.slowCouldPointThreeHitGround(player, player.x, player.y, player.z);
/* 695 */       if ((!canFeasiblyPointThree && !player.compensatedWorld.isNearHardEntity(player.boundingBox.copy().expand(4.0D))) || (player.clientVelocity
/* 696 */         .getY() > 0.06D && !player.uncertaintyHandler.wasAffectedByStuckSpeed())) {
/*     */         
/* 698 */         player.getSetbackTeleportUtil().executeForceResync();
/*     */       } else {
/*     */         
/* 701 */         player.lastOnGround = onGround;
/* 702 */         player.clientClaimsLastOnGround = onGround;
/* 703 */         player.uncertaintyHandler.onGroundUncertain = true;
/*     */       } 
/*     */     } 
/*     */     
/* 707 */     if (!player.packetStateData.lastPacketWasTeleport) {
/* 708 */       player.packetStateData.packetPlayerOnGround = onGround;
/*     */     }
/*     */     
/* 711 */     if (hasLook) {
/* 712 */       player.xRot = yaw;
/* 713 */       player.yRot = pitch;
/*     */       
/* 715 */       float deltaXRot = player.xRot - player.lastXRot;
/* 716 */       float deltaYRot = player.yRot - player.lastYRot;
/*     */       
/* 718 */       RotationUpdate update = new RotationUpdate(new HeadRotation(player.lastXRot, player.lastYRot), new HeadRotation(player.xRot, player.yRot), deltaXRot, deltaYRot);
/* 719 */       player.checkManager.onRotationUpdate(update);
/*     */     } 
/*     */     
/* 722 */     if (hasPosition) {
/* 723 */       Vector3d position = new Vector3d(x, y, z);
/* 724 */       Vector3d clampVector = VectorUtils.clampVector(position);
/* 725 */       PositionUpdate update = new PositionUpdate(new Vector3d(player.x, player.y, player.z), position, onGround, teleportData.getSetback(), teleportData.getTeleportData(), teleportData.isTeleport());
/*     */ 
/*     */       
/* 728 */       if (!player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
/* 729 */         player.filterMojangStupidityOnMojangStupidity = clampVector;
/*     */       }
/*     */       
/* 732 */       if (!player.inVehicle() && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
/* 733 */         player.lastX = player.x;
/* 734 */         player.lastY = player.y;
/* 735 */         player.lastZ = player.z;
/*     */         
/* 737 */         player.x = clampVector.getX();
/* 738 */         player.y = clampVector.getY();
/* 739 */         player.z = clampVector.getZ();
/*     */         
/* 741 */         player.checkManager.onPositionUpdate(update);
/* 742 */       } else if (update.isTeleport()) {
/* 743 */         player.getSetbackTeleportUtil().onPredictionComplete(new PredictionComplete(0.0D, update, true));
/*     */       } 
/*     */     } 
/*     */     
/* 747 */     player.packetStateData.didLastLastMovementIncludePosition = player.packetStateData.didLastMovementIncludePosition;
/* 748 */     player.packetStateData.didLastMovementIncludePosition = hasPosition;
/*     */     
/* 750 */     if (!player.packetStateData.lastPacketWasTeleport) {
/* 751 */       player.packetStateData.didSendMovementBeforeTickEnd = true;
/*     */     }
/*     */     
/* 754 */     player.packetStateData.horseInteractCausedForcedRotation = false;
/*     */   }
/*     */   
/*     */   private static void handleDigging(GrimPlayer player, PacketReceiveEvent event) {
/* 758 */     player.lastBlockBreak = System.currentTimeMillis();
/*     */     
/* 760 */     WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
/* 761 */     DiggingAction action = packet.getAction();
/*     */     
/* 763 */     if (action != DiggingAction.START_DIGGING && action != DiggingAction.FINISHED_DIGGING && action != DiggingAction.CANCELLED_DIGGING) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 769 */     BlockBreak blockBreak = new BlockBreak(player, packet.getBlockPosition(), packet.getBlockFace(), packet.getBlockFaceId(), action, packet.getSequence(), player.compensatedWorld.getBlock(packet.getBlockPosition()));
/*     */     
/* 771 */     player.checkManager.onBlockBreak(blockBreak);
/*     */     
/* 773 */     if (blockBreak.isCancelled()) {
/* 774 */       event.setCancelled(true);
/* 775 */       player.onPacketCancel();
/* 776 */       player.resyncPosition(blockBreak.position, packet.getSequence());
/*     */       
/*     */       return;
/*     */     } 
/* 780 */     player.queuedBreaks.add(blockBreak);
/*     */     
/* 782 */     if (action == DiggingAction.FINISHED_DIGGING && ((Boolean)BREAKABLE.apply(blockBreak.block.getType())).booleanValue()) {
/* 783 */       player.compensatedWorld.startPredicting();
/* 784 */       player.compensatedWorld.updateBlock(blockBreak.position.x, blockBreak.position.y, blockBreak.position.z, 0);
/* 785 */       player.compensatedWorld.stopPredicting((PacketWrapper)packet);
/*     */     } 
/*     */     
/* 788 */     if (action == DiggingAction.START_DIGGING) {
/* 789 */       double damage = BlockBreakSpeed.getBlockDamage(player, blockBreak.block);
/*     */ 
/*     */       
/* 792 */       if (damage >= 1.0D) {
/* 793 */         player.compensatedWorld.startPredicting();
/* 794 */         player.blockHistory.add(new BlockModification(player.compensatedWorld
/*     */               
/* 796 */               .getBlock(blockBreak.position), 
/* 797 */               WrappedBlockState.getByGlobalId(0), blockBreak.position, 
/*     */               
/* 799 */               (GrimAPI.INSTANCE.getTickManager()).currentTick, BlockModification.Cause.START_DIGGING));
/*     */ 
/*     */ 
/*     */         
/* 803 */         if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && Materials.isWaterSource(player.getClientVersion(), blockBreak.block)) {
/*     */ 
/*     */           
/* 806 */           player.compensatedWorld.updateBlock(blockBreak.position, StateTypes.WATER.createBlockState(CompensatedWorld.blockVersion));
/*     */         } else {
/* 808 */           player.compensatedWorld.updateBlock(blockBreak.position.x, blockBreak.position.y, blockBreak.position.z, 0);
/*     */         } 
/* 810 */         player.compensatedWorld.stopPredicting((PacketWrapper)packet);
/*     */       } 
/*     */     } 
/*     */     
/* 814 */     player.compensatedWorld.handleBlockBreakPrediction(packet);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\CheckManagerListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
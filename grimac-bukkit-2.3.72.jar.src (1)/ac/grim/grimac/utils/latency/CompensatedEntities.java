/*     */ package ac.grim.grimac.utils.latency;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attribute;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.Equipment;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*     */ import ac.grim.grimac.shaded.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.ints.IntArraySet;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.utils.data.ShulkerData;
/*     */ import ac.grim.grimac.utils.data.TrackerData;
/*     */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityArmorStand;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityCamel;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityGuardian;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHook;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityShulker;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntitySizeable;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
/*     */ import ac.grim.grimac.utils.data.packetentity.dragon.PacketEntityEnderDragon;
/*     */ import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
/*     */ import ac.grim.grimac.utils.nmsutil.WatchableIndexUtil;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class CompensatedEntities {
/*  42 */   public static final UUID SPRINTING_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
/*  43 */   public static final UUID SNOW_MODIFIER_UUID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");
/*     */   
/*  45 */   public final Int2ObjectOpenHashMap<PacketEntity> entityMap = new Int2ObjectOpenHashMap(40, 0.7F);
/*  46 */   public final IntArraySet entitiesRemovedThisTick = new IntArraySet();
/*  47 */   public final Int2ObjectOpenHashMap<TrackerData> serverPositionsMap = new Int2ObjectOpenHashMap(40, 0.7F);
/*  48 */   public final Object2ObjectOpenHashMap<UUID, UserProfile> profiles = new Object2ObjectOpenHashMap();
/*  49 */   public Integer serverPlayerVehicle = null;
/*     */   public boolean hasSprintingAttributeEnabled = false;
/*     */   public TrackerData selfTrackedEntity;
/*     */   public PacketEntitySelf self;
/*     */   private final GrimPlayer player;
/*     */   
/*     */   public CompensatedEntities(GrimPlayer player) {
/*  56 */     this.player = player;
/*  57 */     this.self = new PacketEntitySelf(player);
/*  58 */     this.selfTrackedEntity = new TrackerData(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, EntityTypes.PLAYER, player.lastTransactionSent.get());
/*     */   }
/*     */   
/*     */   public int getPacketEntityID(PacketEntity entity) {
/*  62 */     for (ObjectIterator<Map.Entry<Integer, PacketEntity>> objectIterator = this.entityMap.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Map.Entry<Integer, PacketEntity> entry = objectIterator.next();
/*  63 */       if (entry.getValue() == entity) {
/*  64 */         return ((Integer)entry.getKey()).intValue();
/*     */       } }
/*     */     
/*  67 */     return Integer.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public void tick() {
/*  71 */     this.self.setPositionRaw(this.player, new SimpleCollisionBox(this.player.x, this.player.y, this.player.z, this.player.x, this.player.y, this.player.z));
/*  72 */     for (ObjectIterator<PacketEntity> objectIterator = this.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity vehicle = objectIterator.next();
/*  73 */       for (PacketEntity passenger : vehicle.passengers) {
/*  74 */         tickPassenger(vehicle, passenger);
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   public void removeEntity(int entityID) {
/*  80 */     PacketEntity entity = (PacketEntity)this.entityMap.remove(entityID);
/*  81 */     if (entity == null)
/*     */       return; 
/*  83 */     if (entity instanceof PacketEntityEnderDragon) { PacketEntityEnderDragon dragon = (PacketEntityEnderDragon)entity;
/*  84 */       for (int i = 1; i < dragon.getParts().size() + 1; i++) {
/*  85 */         this.entityMap.remove(entityID + i);
/*     */       } }
/*     */ 
/*     */     
/*  89 */     for (PacketEntity passenger : new ArrayList(entity.passengers)) {
/*  90 */       passenger.eject();
/*     */     }
/*     */   }
/*     */   
/*     */   public OptionalInt getSlowFallingAmplifier() {
/*  95 */     return this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2) ? OptionalInt.empty() : getPotionLevelForPlayer(PotionTypes.SLOW_FALLING);
/*     */   }
/*     */   
/*     */   public OptionalInt getPotionLevelForPlayer(PotionType type) {
/*  99 */     return getEntityInControl().getPotionEffectLevel(type);
/*     */   }
/*     */   
/*     */   public OptionalInt getPotionLevelForSelfPlayer(PotionType type) {
/* 103 */     return this.self.getPotionEffectLevel(type);
/*     */   }
/*     */   
/*     */   public boolean hasPotionEffect(PotionType type) {
/* 107 */     return getEntityInControl().hasPotionEffect(type);
/*     */   }
/*     */   
/*     */   public PacketEntity getEntityInControl() {
/* 111 */     return (this.self.getRiding() != null) ? this.self.getRiding() : (PacketEntity)this.self;
/*     */   }
/*     */   
/*     */   public void updateAttributes(int entityID, List<WrapperPlayServerUpdateAttributes.Property> objects) {
/* 115 */     if (entityID == this.player.entityID)
/*     */     {
/* 117 */       for (WrapperPlayServerUpdateAttributes.Property snapshotWrapper : objects) {
/* 118 */         Attribute attribute = snapshotWrapper.getAttribute();
/* 119 */         if (attribute != Attributes.MOVEMENT_SPEED)
/*     */           continue; 
/* 121 */         boolean found = false;
/* 122 */         List<WrapperPlayServerUpdateAttributes.PropertyModifier> modifiers = snapshotWrapper.getModifiers();
/* 123 */         for (WrapperPlayServerUpdateAttributes.PropertyModifier modifier : modifiers) {
/* 124 */           ResourceLocation name = modifier.getName();
/* 125 */           if (name.getKey().equals(SPRINTING_MODIFIER_UUID.toString()) || name.getKey().equals("sprinting")) {
/* 126 */             found = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */         
/* 132 */         this.hasSprintingAttributeEnabled = found;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 137 */     PacketEntity entity = this.player.compensatedEntities.getEntity(entityID);
/* 138 */     if (entity == null)
/*     */       return; 
/* 140 */     for (WrapperPlayServerUpdateAttributes.Property snapshotWrapper : objects) {
/* 141 */       Attribute attribute = snapshotWrapper.getAttribute();
/* 142 */       if (attribute == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 146 */       if (attribute == Attributes.HORSE_JUMP_STRENGTH) {
/* 147 */         attribute = Attributes.JUMP_STRENGTH;
/*     */       }
/*     */       
/* 150 */       Optional<ValuedAttribute> valuedAttribute = entity.getAttribute(attribute);
/* 151 */       if (valuedAttribute.isEmpty()) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 156 */       ((ValuedAttribute)valuedAttribute.get()).with(snapshotWrapper);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void tickPassenger(PacketEntity riding, PacketEntity passenger) {
/* 161 */     if (riding == null || passenger == null) {
/*     */       return;
/*     */     }
/*     */     
/* 165 */     passenger.setPositionRaw(this.player, riding.getPossibleLocationBoxes().offset(0.0D, BoundingBoxSize.getMyRidingOffset(riding) + BoundingBoxSize.getPassengerRidingOffset(this.player, passenger), 0.0D));
/*     */     
/* 167 */     for (PacketEntity passengerPassenger : riding.passengers) {
/* 168 */       tickPassenger(passenger, passengerPassenger);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addEntity(int entityID, UUID uuid, EntityType entityType, Vector3d position, float xRot, int data) {
/*     */     PacketEntity packetEntity;
/* 174 */     if (entityType == EntityTypes.ITEM) {
/*     */       return;
/*     */     }
/* 177 */     if (EntityTypes.HAPPY_GHAST.equals(entityType)) {
/* 178 */       PacketEntityHappyGhast packetEntityHappyGhast = new PacketEntityHappyGhast(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), xRot);
/* 179 */     } else if (EntityTypes.CAMEL.equals(entityType)) {
/* 180 */       PacketEntityCamel packetEntityCamel = new PacketEntityCamel(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), xRot);
/* 181 */     } else if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
/* 182 */       PacketEntityHorse packetEntityHorse = new PacketEntityHorse(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), xRot);
/* 183 */     } else if (entityType == EntityTypes.SLIME || entityType == EntityTypes.MAGMA_CUBE || entityType == EntityTypes.PHANTOM) {
/* 184 */       PacketEntitySizeable packetEntitySizeable = new PacketEntitySizeable(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/* 185 */     } else if (EntityTypes.PIG.equals(entityType)) {
/* 186 */       PacketEntityRideable packetEntityRideable = new PacketEntityRideable(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/* 187 */     } else if (EntityTypes.SHULKER.equals(entityType)) {
/* 188 */       PacketEntityShulker packetEntityShulker = new PacketEntityShulker(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/* 189 */     } else if (EntityTypes.STRIDER.equals(entityType)) {
/* 190 */       PacketEntityStrider packetEntityStrider = new PacketEntityStrider(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/* 191 */     } else if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.BOAT) || EntityTypes.CHICKEN.equals(entityType)) {
/* 192 */       PacketEntityTrackXRot packetEntityTrackXRot = new PacketEntityTrackXRot(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), xRot);
/* 193 */     } else if (EntityTypes.FISHING_BOBBER.equals(entityType)) {
/* 194 */       PacketEntityHook packetEntityHook = new PacketEntityHook(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), data);
/* 195 */     } else if (EntityTypes.ENDER_DRAGON.equals(entityType)) {
/* 196 */       PacketEntityEnderDragon packetEntityEnderDragon = new PacketEntityEnderDragon(this.player, uuid, entityID, position.getX(), position.getY(), position.getZ());
/*     */     }
/* 198 */     else if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_ARROW) || EntityTypes.FIREWORK_ROCKET
/* 199 */       .equals(entityType) || EntityTypes.BLOCK_DISPLAY
/* 200 */       .equals(entityType) || EntityTypes.TEXT_DISPLAY
/* 201 */       .equals(entityType) || EntityTypes.LIGHTNING_BOLT
/* 202 */       .equals(entityType) || EntityTypes.EXPERIENCE_BOTTLE
/* 203 */       .equals(entityType) || EntityTypes.EXPERIENCE_ORB
/* 204 */       .equals(entityType)) {
/*     */       
/* 206 */       PacketEntityUnHittable packetEntityUnHittable = new PacketEntityUnHittable(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/* 207 */     } else if (EntityTypes.ARMOR_STAND.equals(entityType)) {
/* 208 */       PacketEntityArmorStand packetEntityArmorStand = new PacketEntityArmorStand(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ(), data);
/* 209 */     } else if (EntityTypes.PAINTING.equals(entityType)) {
/* 210 */       PacketEntityPainting packetEntityPainting = new PacketEntityPainting(this.player, uuid, position.x, position.y, position.z, Direction.values()[data]);
/* 211 */     } else if (EntityTypes.GUARDIAN.equals(entityType)) {
/* 212 */       PacketEntityGuardian packetEntityGuardian = new PacketEntityGuardian(this.player, uuid, entityType, position.x, position.y, position.z, false);
/* 213 */     } else if (EntityTypes.ELDER_GUARDIAN.equals(entityType)) {
/* 214 */       PacketEntityGuardian packetEntityGuardian = new PacketEntityGuardian(this.player, uuid, entityType, position.x, position.y, position.z, true);
/*     */     } else {
/* 216 */       packetEntity = new PacketEntity(this.player, uuid, entityType, position.getX(), position.getY(), position.getZ());
/*     */     } 
/*     */     
/* 219 */     this.entityMap.put(entityID, packetEntity);
/*     */   }
/*     */   
/*     */   public PacketEntity getEntity(int entityID) {
/* 223 */     if (entityID == this.player.entityID) {
/* 224 */       return (PacketEntity)this.self;
/*     */     }
/* 226 */     return (PacketEntity)this.entityMap.get(entityID);
/*     */   }
/*     */   
/*     */   public TrackerData getTrackedEntity(int id) {
/* 230 */     if (id == this.player.entityID) {
/* 231 */       return this.selfTrackedEntity;
/*     */     }
/* 233 */     return (TrackerData)this.serverPositionsMap.get(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateEntityMetadata(int entityID, List<EntityData<?>> watchableObjects) {
/* 238 */     PacketEntity entity = this.player.compensatedEntities.getEntity(entityID);
/* 239 */     if (entity == null)
/*     */       return; 
/* 241 */     if (entity.isAgeable) {
/*     */       int id;
/* 243 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
/* 244 */         id = 12;
/* 245 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 246 */         id = 11;
/* 247 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 248 */         id = 12;
/* 249 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 250 */         id = 14;
/* 251 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 252 */         id = 15;
/*     */       } else {
/* 254 */         id = 16;
/*     */       } 
/*     */ 
/*     */       
/* 258 */       EntityData<?> ageableObject = WatchableIndexUtil.getIndex(watchableObjects, id);
/* 259 */       if (ageableObject != null) {
/* 260 */         Object value = ageableObject.getValue();
/*     */         
/* 262 */         if (value instanceof Boolean) {
/* 263 */           entity.isBaby = ((Boolean)value).booleanValue();
/* 264 */         } else if (value instanceof Byte) {
/* 265 */           entity.isBaby = (((Byte)value).byteValue() < 0);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 270 */     if (entity instanceof PacketEntitySizeable) { int id; PacketEntitySizeable sizeable = (PacketEntitySizeable)entity;
/*     */       
/* 272 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
/* 273 */         id = 16;
/* 274 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 275 */         id = 11;
/* 276 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 277 */         id = 12;
/* 278 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 279 */         id = 14;
/* 280 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 281 */         id = 15;
/*     */       } else {
/* 283 */         id = 16;
/*     */       } 
/*     */       
/* 286 */       EntityData<?> sizeObject = WatchableIndexUtil.getIndex(watchableObjects, id);
/* 287 */       if (sizeObject != null) {
/* 288 */         Object value = sizeObject.getValue();
/* 289 */         if (value instanceof Integer) {
/* 290 */           sizeable.size = Math.max(((Integer)value).intValue(), 1);
/* 291 */         } else if (value instanceof Byte) {
/* 292 */           sizeable.size = Math.max(((Byte)value).byteValue(), 1);
/*     */         } 
/*     */       }  }
/*     */ 
/*     */     
/* 297 */     if (entity instanceof PacketEntityShulker) { int id; PacketEntityShulker shulker = (PacketEntityShulker)entity;
/*     */ 
/*     */       
/* 300 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 301 */         id = 11;
/* 302 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 303 */         id = 12;
/* 304 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 305 */         id = 14;
/* 306 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 307 */         id = 15;
/*     */       } else {
/* 309 */         id = 16;
/*     */       } 
/*     */       
/* 312 */       EntityData<?> shulkerAttached = WatchableIndexUtil.getIndex(watchableObjects, id);
/*     */       
/* 314 */       if (shulkerAttached != null)
/*     */       {
/* 316 */         shulker.facing = BlockFace.valueOf(shulkerAttached.getValue().toString().toUpperCase());
/*     */       }
/*     */       
/* 319 */       EntityData<?> height = WatchableIndexUtil.getIndex(watchableObjects, id + 2);
/* 320 */       if (height != null) {
/* 321 */         if (((Byte)height.getValue()).byteValue() == 0) {
/* 322 */           ShulkerData data = new ShulkerData(shulker, this.player.lastTransactionSent.get(), true);
/* 323 */           this.player.compensatedWorld.openShulkerBoxes.remove(data);
/* 324 */           this.player.compensatedWorld.openShulkerBoxes.add(data);
/*     */         } else {
/* 326 */           ShulkerData data = new ShulkerData(shulker, this.player.lastTransactionSent.get(), false);
/* 327 */           this.player.compensatedWorld.openShulkerBoxes.remove(data);
/* 328 */           this.player.compensatedWorld.openShulkerBoxes.add(data);
/*     */         } 
/*     */       } }
/*     */ 
/*     */     
/* 333 */     if (entity instanceof PacketEntityRideable) { PacketEntityRideable rideable = (PacketEntityRideable)entity;
/* 334 */       int offset = 0;
/* 335 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
/* 336 */         if (entity.type == EntityTypes.PIG) {
/* 337 */           EntityData<?> pigSaddle = WatchableIndexUtil.getIndex(watchableObjects, 16);
/* 338 */           if (pigSaddle != null) {
/* 339 */             rideable.hasSaddle = (((Byte)pigSaddle.getValue()).byteValue() != 0);
/*     */           }
/*     */         } 
/* 342 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 343 */         offset = 5;
/* 344 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 345 */         offset = 4;
/* 346 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 347 */         offset = 2;
/* 348 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 349 */         offset = 1;
/*     */       } 
/*     */       
/* 352 */       if (entity.type == EntityTypes.PIG) {
/* 353 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 354 */           offset = 1;
/*     */         }
/* 356 */         EntityData<?> pigSaddle = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
/* 357 */         if (pigSaddle != null) {
/* 358 */           rideable.hasSaddle = ((Boolean)pigSaddle.getValue()).booleanValue();
/*     */         }
/*     */         
/* 361 */         EntityData<?> pigBoost = WatchableIndexUtil.getIndex(watchableObjects, 18 - offset);
/* 362 */         if (pigBoost != null) {
/* 363 */           rideable.boostTimeMax = ((Integer)pigBoost.getValue()).intValue();
/* 364 */           rideable.currentBoostTime = 0;
/*     */         } 
/* 366 */       } else if (entity instanceof PacketEntityStrider) {
/* 367 */         EntityData<?> striderBoost = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
/* 368 */         if (striderBoost != null) {
/* 369 */           rideable.boostTimeMax = ((Integer)striderBoost.getValue()).intValue();
/* 370 */           rideable.currentBoostTime = 0;
/*     */         } 
/*     */         
/* 373 */         EntityData<?> striderShaking = WatchableIndexUtil.getIndex(watchableObjects, 18 - offset);
/* 374 */         if (striderShaking != null) {
/* 375 */           ((PacketEntityStrider)rideable).isShaking = ((Boolean)striderShaking.getValue()).booleanValue();
/*     */         }
/*     */         
/* 378 */         EntityData<?> striderSaddle = WatchableIndexUtil.getIndex(watchableObjects, 19 - offset);
/* 379 */         if (striderSaddle != null) {
/* 380 */           rideable.hasSaddle = ((Boolean)striderSaddle.getValue()).booleanValue();
/*     */         }
/*     */       }  }
/*     */ 
/*     */     
/* 385 */     if (entity instanceof PacketEntityHorse) { PacketEntityHorse horse = (PacketEntityHorse)entity;
/* 386 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9_4)) {
/* 387 */         int offset = 0;
/*     */         
/* 389 */         if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 390 */           offset = 5;
/* 391 */         } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 392 */           offset = 4;
/* 393 */         } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 394 */           offset = 2;
/* 395 */         } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 396 */           offset = 1;
/*     */         } 
/*     */         
/* 399 */         EntityData<?> horseByte = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
/* 400 */         if (horseByte != null) {
/* 401 */           byte info = ((Byte)horseByte.getValue()).byteValue();
/*     */           
/* 403 */           horse.isTame = ((info & 0x2) != 0);
/* 404 */           horse.hasSaddle = ((info & 0x4) != 0);
/* 405 */           horse.isRearing = ((info & 0x20) != 0);
/*     */         } 
/*     */ 
/*     */         
/* 409 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20) && 
/* 410 */           entity instanceof PacketEntityCamel) { PacketEntityCamel camel = (PacketEntityCamel)entity;
/* 411 */           EntityData<?> entityData = WatchableIndexUtil.getIndex(watchableObjects, 18);
/* 412 */           if (entityData != null) {
/* 413 */             camel.dashing = ((Boolean)entityData.getValue()).booleanValue();
/*     */           } }
/*     */ 
/*     */       
/*     */       } else {
/*     */         
/* 419 */         EntityData<?> horseByte = WatchableIndexUtil.getIndex(watchableObjects, 16);
/* 420 */         if (horseByte != null) {
/* 421 */           int info = ((Integer)horseByte.getValue()).intValue();
/*     */           
/* 423 */           horse.isTame = ((info & 0x2) != 0);
/*     */           
/* 425 */           horse.hasSaddle = ((info & 0x4) != 0);
/*     */           
/* 427 */           horse.isRearing = ((info & 0x40) != 0);
/*     */         } 
/*     */       }  }
/*     */ 
/*     */     
/* 432 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9_4)) {
/* 433 */       EntityData<?> gravity = WatchableIndexUtil.getIndex(watchableObjects, 5);
/*     */       
/* 435 */       if (gravity != null) {
/* 436 */         Object gravityObject = gravity.getValue();
/*     */         
/* 438 */         if (gravityObject instanceof Boolean)
/*     */         {
/*     */           
/* 441 */           entity.hasGravity = !((Boolean)gravityObject).booleanValue();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 446 */     if (entity.type == EntityTypes.FIREWORK_ROCKET) {
/* 447 */       int offset = 0;
/* 448 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/* 449 */         offset = 2;
/* 450 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 451 */         offset = 1;
/*     */       } 
/*     */       
/* 454 */       EntityData<?> fireworkWatchableObject = WatchableIndexUtil.getIndex(watchableObjects, 9 - offset);
/* 455 */       if (fireworkWatchableObject == null)
/*     */         return; 
/* 457 */       if (fireworkWatchableObject.getValue() instanceof Integer) {
/* 458 */         int attachedEntityID = ((Integer)fireworkWatchableObject.getValue()).intValue();
/* 459 */         if (attachedEntityID == this.player.entityID) {
/* 460 */           this.player.fireworks.addNewFirework(entityID);
/*     */         }
/*     */       } else {
/* 463 */         Optional<Integer> attachedEntityID = (Optional<Integer>)fireworkWatchableObject.getValue();
/*     */         
/* 465 */         if (attachedEntityID.isPresent() && ((Integer)attachedEntityID.get()).equals(Integer.valueOf(this.player.entityID))) {
/* 466 */           this.player.fireworks.addNewFirework(entityID);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 471 */     if (entity instanceof PacketEntityHook) { int index; PacketEntityHook hook = (PacketEntityHook)entity;
/*     */       
/* 473 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 474 */         index = 5;
/* 475 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 476 */         index = 6;
/* 477 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 478 */         index = 7;
/*     */       } else {
/* 480 */         index = 8;
/*     */       } 
/*     */       
/* 483 */       EntityData<?> hookWatchableObject = WatchableIndexUtil.getIndex(watchableObjects, index);
/* 484 */       if (hookWatchableObject == null)
/*     */         return; 
/* 486 */       Integer attachedEntityID = (Integer)hookWatchableObject.getValue();
/* 487 */       hook.attached = attachedEntityID.intValue() - 1; }
/*     */ 
/*     */     
/* 490 */     if (entity instanceof PacketEntityArmorStand) {
/*     */       int index;
/* 492 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
/* 493 */         index = 10;
/* 494 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
/* 495 */         index = 11;
/* 496 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 497 */         index = 13;
/* 498 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 499 */         index = 14;
/*     */       } else {
/* 501 */         index = 15;
/*     */       } 
/*     */       
/* 504 */       EntityData<?> armorStandByte = WatchableIndexUtil.getIndex(watchableObjects, index);
/* 505 */       if (armorStandByte != null) {
/* 506 */         byte info = ((Byte)armorStandByte.getValue()).byteValue();
/*     */         
/* 508 */         entity.isBaby = ((info & 0x1) != 0);
/* 509 */         ((PacketEntityArmorStand)entity).isMarker = ((info & 0x10) != 0);
/*     */       } 
/*     */     } 
/*     */     
/* 513 */     if (entity instanceof PacketEntityGuardian && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_11)) {
/*     */       int index, isElderlyBitMask;
/*     */       
/* 516 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/* 517 */         index = 16;
/* 518 */         isElderlyBitMask = 4;
/* 519 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_10)) {
/* 520 */         index = 11;
/* 521 */         isElderlyBitMask = 4;
/*     */       } else {
/* 523 */         index = 12;
/* 524 */         isElderlyBitMask = 4;
/*     */       } 
/*     */       
/* 527 */       EntityData<?> guardianByte = WatchableIndexUtil.getIndex(watchableObjects, index);
/* 528 */       if (guardianByte != null) {
/* 529 */         int info = ((Integer)guardianByte.getValue()).intValue();
/* 530 */         ((PacketEntityGuardian)entity).isElder = ((info & isElderlyBitMask) != 0);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateEntityEquipment(int entityId, List<Equipment> equipment) {
/* 536 */     PacketEntity entity = this.player.compensatedEntities.getEntity(entityId);
/* 537 */     if (entity == null || !entity.trackEntityEquipment)
/*     */       return; 
/* 539 */     for (Equipment equipmentItem : equipment)
/* 540 */       entity.setItemBySlot(equipmentItem.getSlot(), equipmentItem.getItem()); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\CompensatedEntities.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
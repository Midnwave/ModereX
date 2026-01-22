/*     */ package ac.grim.grimac.events.packets;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.type.PacketCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.EntityPositionData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.UserProfile;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import ac.grim.grimac.utils.data.TrackerData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityHook;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
/*     */ import ac.grim.grimac.utils.reflection.ViaVersionUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ public class PacketEntityReplication extends Check implements PacketCheck {
/*  59 */   private final AtomicBoolean hasSentPreWavePacket = new AtomicBoolean(true);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  78 */   private final List<Integer> despawnedEntitiesThisTransaction = new ArrayList<>();
/*     */ 
/*     */   
/*  81 */   private int maxFireworkBoostPing = 1000;
/*     */   
/*     */   public PacketEntityReplication(GrimPlayer player) {
/*  84 */     super(player);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  90 */     if (!isTickPacket(event.getPacketType()))
/*  91 */       return;  this.player.compensatedEntities.entitiesRemovedThisTick.clear();
/*  92 */     boolean isTickingReliably = this.player.isTickingReliablyFor(3);
/*     */     
/*  94 */     PacketEntity playerVehicle = this.player.compensatedEntities.self.getRiding();
/*  95 */     for (ObjectIterator<PacketEntity> objectIterator = this.player.compensatedEntities.entityMap.values().iterator(); objectIterator.hasNext(); ) { PacketEntity entity = objectIterator.next();
/*  96 */       if (entity == playerVehicle && !this.player.vehicleData.lastDummy) {
/*     */ 
/*     */         
/*  99 */         entity.setPositionRaw(this.player, entity.getPossibleLocationBoxes()); continue;
/*     */       } 
/* 101 */       entity.onMovement(isTickingReliably); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/* 109 */     if ((event.getPacketType() == PacketType.Play.Server.PING || event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) && this.player.packetStateData.lastServerTransWasValid) {
/* 110 */       this.despawnedEntitiesThisTransaction.clear();
/* 111 */     } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
/* 112 */       WrapperPlayServerSpawnLivingEntity packetOutEntity = new WrapperPlayServerSpawnLivingEntity(event);
/* 113 */       addEntity(packetOutEntity.getEntityId(), packetOutEntity.getEntityUUID(), packetOutEntity.getEntityType(), packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
/* 114 */     } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
/* 115 */       WrapperPlayServerSpawnEntity packetOutEntity = new WrapperPlayServerSpawnEntity(event);
/* 116 */       addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID().orElse(null), packetOutEntity.getEntityType(), packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), (List<EntityData<?>>)null, packetOutEntity.getData());
/* 117 */     } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
/* 118 */       WrapperPlayServerSpawnPlayer packetOutEntity = new WrapperPlayServerSpawnPlayer(event);
/* 119 */       addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID(), EntityTypes.PLAYER, packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
/* 120 */     } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PAINTING) {
/* 121 */       WrapperPlayServerSpawnPainting packetOutEntity = new WrapperPlayServerSpawnPainting(event);
/* 122 */       addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID(), EntityTypes.PAINTING, packetOutEntity.getPosition().toVector3d(), 0.0F, 0.0F, (List<EntityData<?>>)null, packetOutEntity.getDirection().getHorizontalIndex());
/* 123 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
/* 124 */       WrapperPlayServerEntityRelativeMove move = new WrapperPlayServerEntityRelativeMove(event);
/* 125 */       handleMoveEntity(event, move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), (Float)null, (Float)null, true, true);
/* 126 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
/* 127 */       WrapperPlayServerEntityRelativeMoveAndRotation move = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
/* 128 */       handleMoveEntity(event, move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), Float.valueOf(move.getYaw() * 0.7111111F), Float.valueOf(move.getPitch() * 0.7111111F), true, true);
/* 129 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
/* 130 */       WrapperPlayServerEntityTeleport move = new WrapperPlayServerEntityTeleport(event);
/* 131 */       Vector3d pos = move.getPosition();
/* 132 */       handleMoveEntity(event, move.getEntityId(), pos.getX(), pos.getY(), pos.getZ(), Float.valueOf(move.getYaw()), Float.valueOf(move.getPitch()), false, true);
/* 133 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
/*     */       
/* 135 */       WrapperPlayServerEntityPositionSync move = new WrapperPlayServerEntityPositionSync(event);
/* 136 */       EntityPositionData values = move.getValues();
/* 137 */       Vector3d pos = values.getPosition();
/*     */ 
/*     */       
/* 140 */       handleMoveEntity(event, move.getId(), pos.getX(), pos.getY(), pos.getZ(), Float.valueOf(values.getYaw()), Float.valueOf(values.getPitch()), false, true);
/* 141 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
/* 142 */       WrapperPlayServerEntityRotation move = new WrapperPlayServerEntityRotation(event);
/* 143 */       handleMoveEntity(event, move.getEntityId(), 0.0D, 0.0D, 0.0D, Float.valueOf(move.getYaw() * 0.7111111F), Float.valueOf(move.getPitch() * 0.7111111F), true, false);
/* 144 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
/* 145 */       WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata(event);
/* 146 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.compensatedEntities.updateEntityMetadata(entityMetadata.getEntityId(), entityMetadata.getEntityMetadata()));
/* 147 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
/* 148 */       WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment(event);
/* 149 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.compensatedEntities.updateEntityEquipment(equipment.getEntityId(), equipment.getEquipment()));
/*     */ 
/*     */     
/*     */     }
/* 153 */     else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
/* 154 */       WrapperPlayServerPlayerInfoUpdate info = new WrapperPlayServerPlayerInfoUpdate(event);
/* 155 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry : info.getEntries()) {
/*     */               UserProfile gameProfile = entry.getGameProfile();
/*     */               UUID uuid = gameProfile.getUUID();
/*     */               this.player.compensatedEntities.profiles.put(uuid, gameProfile);
/*     */             } 
/*     */           });
/* 162 */     } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
/* 163 */       WrapperPlayServerPlayerInfoRemove remove = new WrapperPlayServerPlayerInfoRemove(event);
/* 164 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> { Objects.requireNonNull(this.player.compensatedEntities.profiles); remove.getProfileIds().forEach(this.player.compensatedEntities.profiles::remove); });
/* 165 */     } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
/* 166 */       WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
/* 167 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             if (info.getAction() == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
/*     */               for (WrapperPlayServerPlayerInfo.PlayerData entry : info.getPlayerDataList()) {
/*     */                 UserProfile gameProfile = entry.getUserProfile();
/*     */                 UUID uuid = gameProfile.getUUID();
/*     */                 this.player.compensatedEntities.profiles.put(uuid, gameProfile);
/*     */               } 
/*     */             } else if (info.getAction() == WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
/*     */               info.getPlayerDataList().forEach(());
/*     */             } 
/*     */           });
/* 178 */     } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
/* 179 */       WrapperPlayServerEntityEffect effect = new WrapperPlayServerEntityEffect(event);
/*     */       
/* 181 */       PotionType type = effect.getPotionType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 188 */       if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && ViaVersionUtil.isAvailable && type.getId(this.player.getClientVersion()) > 23) {
/* 189 */         event.setCancelled(true);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 195 */       if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_13) && ViaVersionUtil.isAvailable && type.getId(this.player.getClientVersion()) == 30) {
/* 196 */         event.setCancelled(true);
/*     */         
/*     */         return;
/*     */       } 
/* 200 */       if (isDirectlyAffectingPlayer(this.player, effect.getEntityId())) this.player.sendTransaction();
/*     */       
/* 202 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             PacketEntity entity = this.player.compensatedEntities.getEntity(effect.getEntityId());
/*     */             if (entity == null)
/*     */               return; 
/*     */             entity.addPotionEffect(type, effect.getEffectAmplifier());
/*     */           });
/* 208 */     } else if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
/* 209 */       WrapperPlayServerRemoveEntityEffect effect = new WrapperPlayServerRemoveEntityEffect(event);
/*     */       
/* 211 */       if (isDirectlyAffectingPlayer(this.player, effect.getEntityId())) this.player.sendTransaction();
/*     */       
/* 213 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */             PacketEntity entity = this.player.compensatedEntities.getEntity(effect.getEntityId());
/*     */             if (entity == null)
/*     */               return; 
/*     */             entity.removePotionEffect(effect.getPotionType());
/*     */           });
/* 219 */     } else if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
/* 220 */       WrapperPlayServerUpdateAttributes attributes = new WrapperPlayServerUpdateAttributes(event);
/*     */       
/* 222 */       int entityID = attributes.getEntityId();
/*     */ 
/*     */       
/* 225 */       if (isDirectlyAffectingPlayer(this.player, entityID)) this.player.sendTransaction();
/*     */       
/* 227 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.compensatedEntities.updateAttributes(entityID, attributes.getProperties()));
/*     */     }
/* 229 */     else if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
/* 230 */       WrapperPlayServerEntityStatus status = new WrapperPlayServerEntityStatus(event);
/*     */ 
/*     */       
/* 233 */       if (status.getStatus() == 3) {
/* 234 */         PacketEntity entity = this.player.compensatedEntities.getEntity(status.getEntityId());
/*     */         
/* 236 */         if (entity == null)
/* 237 */           return;  entity.isDead = true;
/*     */       } 
/*     */       
/* 240 */       if (status.getStatus() == 9) {
/* 241 */         if (status.getEntityId() != this.player.entityID)
/*     */           return; 
/* 243 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.packetStateData.setSlowedByUsingItem(false));
/* 244 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get() + 1, () -> this.player.packetStateData.setSlowedByUsingItem(false));
/*     */       } 
/*     */       
/* 247 */       if (status.getStatus() == 31) {
/* 248 */         PacketEntityHook hookEntity; PacketEntity hook = this.player.compensatedEntities.getEntity(status.getEntityId());
/* 249 */         if (hook instanceof PacketEntityHook) { hookEntity = (PacketEntityHook)hook; }
/*     */         else { return; }
/* 251 */          if (hookEntity.attached == this.player.entityID) {
/* 252 */           this.player.sendTransaction();
/*     */           
/* 254 */           this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.uncertaintyHandler.fishingRodPulls.add(Integer.valueOf(hookEntity.owner)));
/*     */         } 
/*     */       } 
/*     */       
/* 258 */       if (status.getStatus() >= 24 && status.getStatus() <= 28 && status.getEntityId() == this.player.entityID) {
/* 259 */         this.player.compensatedEntities.self.opLevel = status.getStatus() - 24;
/*     */       }
/* 261 */     } else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
/* 262 */       WrapperPlayServerSetSlot slot = new WrapperPlayServerSetSlot(event);
/*     */       
/* 264 */       if (slot.getWindowId() == 0) {
/* 265 */         Runnable task = () -> {
/*     */             if ((slot.getSlot() - 36 == this.player.packetStateData.lastSlotSelected && (!this.player.inventory.getHeldItem().is(slot.getItem().getType()) || this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8))) || (slot.getSlot() == 45 && !this.player.inventory.getOffHand().is(slot.getItem().getType()))) {
/*     */               InteractionHand hand = (slot.getSlot() == 45) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
/*     */ 
/*     */               
/*     */               if (hand == this.player.packetStateData.itemInUseHand) {
/*     */                 this.player.packetStateData.setSlowedByUsingItem(false);
/*     */               }
/*     */               
/*     */               if (this.player.isResetItemUsageOnItemUpdate() && hand == GrimAPI.INSTANCE.getItemResetHandler().getItemUsageHand(this.player.platformPlayer)) {
/*     */                 GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(this.player.platformPlayer);
/*     */               }
/*     */             } 
/*     */           };
/*     */         
/* 280 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), task);
/* 281 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get() + 1, task);
/*     */       } 
/* 283 */     } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
/* 284 */       WrapperPlayServerWindowItems items = new WrapperPlayServerWindowItems(event);
/*     */       
/* 286 */       if (items.getWindowId() == 0) {
/* 287 */         Runnable task = () -> {
/*     */             if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/*     */               this.player.packetStateData.setSlowedByUsingItem(false);
/*     */               
/*     */               if (this.player.isResetItemUsageOnItemUpdate()) {
/*     */                 GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(this.player.platformPlayer);
/*     */               }
/*     */             } else {
/*     */               if (items.getItems().size() > 45 && !this.player.inventory.getOffHand().is(((ItemStack)items.getItems().get(45)).getType())) {
/*     */                 if (this.player.packetStateData.itemInUseHand == InteractionHand.OFF_HAND) {
/*     */                   this.player.packetStateData.setSlowedByUsingItem(false);
/*     */                 }
/*     */                 
/*     */                 if (this.player.isResetItemUsageOnItemUpdate() && GrimAPI.INSTANCE.getItemResetHandler().getItemUsageHand(this.player.platformPlayer) == InteractionHand.OFF_HAND) {
/*     */                   GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(this.player.platformPlayer);
/*     */                 }
/*     */               } 
/*     */               
/*     */               if (!this.player.inventory.getHeldItem().is(((ItemStack)items.getItems().get(this.player.packetStateData.lastSlotSelected + 36)).getType())) {
/*     */                 if (this.player.packetStateData.itemInUseHand == InteractionHand.MAIN_HAND) {
/*     */                   this.player.packetStateData.setSlowedByUsingItem(false);
/*     */                 }
/*     */                 
/*     */                 if (this.player.isResetItemUsageOnItemUpdate() && GrimAPI.INSTANCE.getItemResetHandler().getItemUsageHand(this.player.platformPlayer) == InteractionHand.MAIN_HAND) {
/*     */                   GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(this.player.platformPlayer);
/*     */                 }
/*     */               } 
/*     */             } 
/*     */           };
/* 316 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), task);
/* 317 */         this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get() + 1, task);
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 322 */     else if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
/* 323 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.packetStateData.setSlowedByUsingItem(false));
/* 324 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get() + 1, () -> this.player.packetStateData.setSlowedByUsingItem(false));
/* 325 */     } else if (event.getPacketType() == PacketType.Play.Server.OPEN_HORSE_WINDOW) {
/* 326 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> this.player.packetStateData.setSlowedByUsingItem(false));
/* 327 */       this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get() + 1, () -> this.player.packetStateData.setSlowedByUsingItem(false));
/* 328 */     } else if (event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS) {
/* 329 */       WrapperPlayServerSetPassengers mount = new WrapperPlayServerSetPassengers(event);
/*     */       
/* 331 */       int vehicleID = mount.getEntityId();
/* 332 */       int[] passengers = mount.getPassengers();
/*     */       
/* 334 */       handleMountVehicle(event, vehicleID, passengers);
/* 335 */     } else if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
/* 336 */       WrapperPlayServerAttachEntity attach = new WrapperPlayServerAttachEntity(event);
/*     */ 
/*     */       
/* 339 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*     */         return;
/*     */       }
/*     */       
/* 343 */       if (!attach.isLeash()) {
/*     */         
/* 345 */         int vehicleID = attach.getHoldingId();
/* 346 */         int attachID = attach.getAttachedId();
/* 347 */         TrackerData trackerData = this.player.compensatedEntities.getTrackedEntity(attachID);
/*     */         
/* 349 */         if (trackerData != null) {
/*     */ 
/*     */           
/* 352 */           if (vehicleID == -1) {
/* 353 */             vehicleID = trackerData.getLegacyPointEightMountedUpon();
/* 354 */             handleMountVehicle(event, vehicleID, new int[0]);
/*     */           } else {
/* 356 */             trackerData.setLegacyPointEightMountedUpon(vehicleID);
/* 357 */             handleMountVehicle(event, vehicleID, new int[] { attachID });
/*     */           } 
/*     */         } else {
/*     */           
/* 361 */           LogUtil.warn("Server sent an invalid attach entity packet for entity " + attach.getHoldingId() + " with passenger " + attach.getAttachedId() + "! The client ignores this.");
/*     */         } 
/*     */       } 
/* 364 */     } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
/* 365 */       WrapperPlayServerDestroyEntities destroy = new WrapperPlayServerDestroyEntities(event);
/*     */       
/* 367 */       int[] destroyEntityIds = destroy.getEntityIds();
/*     */       
/* 369 */       for (int entityID : destroyEntityIds) {
/* 370 */         this.despawnedEntitiesThisTransaction.add(Integer.valueOf(entityID));
/* 371 */         this.player.compensatedEntities.serverPositionsMap.remove(entityID);
/*     */         
/* 373 */         if (this.player.compensatedEntities.serverPlayerVehicle != null && this.player.compensatedEntities.serverPlayerVehicle.intValue() == entityID) {
/* 374 */           this.player.compensatedEntities.serverPlayerVehicle = null;
/*     */         }
/*     */       } 
/*     */       
/* 378 */       int destroyTransaction = this.player.lastTransactionSent.get() + 1;
/* 379 */       this.player.latencyUtils.addRealTimeTask(destroyTransaction, () -> {
/*     */             for (int integer : destroyEntityIds) {
/*     */               this.player.compensatedEntities.removeEntity(integer);
/*     */               
/*     */               this.player.fireworks.removeFirework(integer);
/*     */               
/*     */               this.player.compensatedEntities.entitiesRemovedThisTick.add(integer);
/*     */             } 
/*     */           });
/*     */       
/* 389 */       if (this.maxFireworkBoostPing > 0) {
/* 390 */         this.player.runNettyTaskInMs(() -> { if (this.player.lastTransactionReceived.get() >= destroyTransaction) return;  for (int entityID : destroyEntityIds) { if (this.player.fireworks.hasFirework(entityID)) { this.player.getSetbackTeleportUtil().executeViolationSetback(); break; }  }  }this.maxFireworkBoostPing);
/*     */       }
/*     */     } 
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
/*     */   private void handleMountVehicle(PacketSendEvent event, int vehicleID, int[] passengers) {
/* 405 */     boolean wasInVehicle = (this.player.compensatedEntities.serverPlayerVehicle != null && this.player.compensatedEntities.serverPlayerVehicle.intValue() == vehicleID);
/* 406 */     boolean inThisVehicle = false;
/*     */     
/* 408 */     for (int passenger : passengers) {
/* 409 */       inThisVehicle = (passenger == this.player.entityID);
/* 410 */       if (inThisVehicle)
/*     */         break; 
/*     */     } 
/* 413 */     if (inThisVehicle && !wasInVehicle) {
/* 414 */       this.player.handleMountVehicle(vehicleID);
/*     */     }
/*     */     
/* 417 */     if (!inThisVehicle && wasInVehicle) {
/* 418 */       this.player.handleDismountVehicle(event);
/*     */     }
/*     */     
/* 421 */     if (wasInVehicle || inThisVehicle) {
/* 422 */       this.player.sendTransaction();
/*     */     }
/* 424 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */           PacketEntity vehicle = this.player.compensatedEntities.getEntity(vehicleID);
/*     */           if (vehicle == null) {
/*     */             return;
/*     */           }
/*     */           for (PacketEntity passenger : new ArrayList(vehicle.passengers)) {
/*     */             passenger.eject();
/*     */           }
/*     */           for (int entityID : passengers) {
/*     */             PacketEntity passenger = this.player.compensatedEntities.getEntity(entityID);
/*     */             if (passenger != null) {
/*     */               passenger.mount(vehicle);
/*     */             }
/*     */           } 
/*     */         });
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
/*     */   
/*     */   private void handleMoveEntity(PacketSendEvent event, int entityId, double deltaX, double deltaY, double deltaZ, Float yaw, Float pitch, boolean isRelative, boolean hasPos) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   4: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*     */     //   7: iload_2
/*     */     //   8: invokevirtual getTrackedEntity : (I)Lac/grim/grimac/utils/data/TrackerData;
/*     */     //   11: astore #13
/*     */     //   13: aload_0
/*     */     //   14: getfield hasSentPreWavePacket : Ljava/util/concurrent/atomic/AtomicBoolean;
/*     */     //   17: iconst_0
/*     */     //   18: iconst_1
/*     */     //   19: invokevirtual compareAndSet : (ZZ)Z
/*     */     //   22: istore #14
/*     */     //   24: iload #14
/*     */     //   26: ifeq -> 36
/*     */     //   29: aload_0
/*     */     //   30: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   33: invokevirtual sendTransaction : ()V
/*     */     //   36: aload #13
/*     */     //   38: ifnull -> 432
/*     */     //   41: iload #11
/*     */     //   43: ifeq -> 347
/*     */     //   46: aload_0
/*     */     //   47: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   50: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*     */     //   53: getfield serverPlayerVehicle : Ljava/lang/Integer;
/*     */     //   56: ifnull -> 136
/*     */     //   59: aload_0
/*     */     //   60: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   63: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*     */     //   66: getfield serverPlayerVehicle : Ljava/lang/Integer;
/*     */     //   69: invokevirtual intValue : ()I
/*     */     //   72: iload_2
/*     */     //   73: if_icmpne -> 136
/*     */     //   76: aload_0
/*     */     //   77: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   80: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   83: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   86: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   89: ifeq -> 136
/*     */     //   92: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   95: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   98: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   103: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_21_2 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   106: invokevirtual isOlderThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   109: ifeq -> 136
/*     */     //   112: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   115: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   118: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   123: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   126: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   129: ifeq -> 136
/*     */     //   132: iconst_1
/*     */     //   133: goto -> 137
/*     */     //   136: iconst_0
/*     */     //   137: istore #15
/*     */     //   139: iload #15
/*     */     //   141: ifne -> 215
/*     */     //   144: dload_3
/*     */     //   145: invokestatic abs : (D)D
/*     */     //   148: ldc2_w 3.9375
/*     */     //   151: dcmpl
/*     */     //   152: ifge -> 179
/*     */     //   155: dload #5
/*     */     //   157: invokestatic abs : (D)D
/*     */     //   160: ldc2_w 3.9375
/*     */     //   163: dcmpl
/*     */     //   164: ifge -> 179
/*     */     //   167: dload #7
/*     */     //   169: invokestatic abs : (D)D
/*     */     //   172: ldc2_w 3.9375
/*     */     //   175: dcmpl
/*     */     //   176: iflt -> 306
/*     */     //   179: aload_0
/*     */     //   180: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   183: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   186: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   189: invokevirtual isOlderThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   192: ifeq -> 306
/*     */     //   195: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   198: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   201: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   206: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   209: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   212: ifeq -> 306
/*     */     //   215: aload_0
/*     */     //   216: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   219: getfield user : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/User;
/*     */     //   222: new ac/grim/grimac/shaded/com/github/retrooper/packetevents/wrapper/play/server/WrapperPlayServerEntityTeleport
/*     */     //   225: dup
/*     */     //   226: iload_2
/*     */     //   227: new ac/grim/grimac/shaded/com/github/retrooper/packetevents/util/Vector3d
/*     */     //   230: dup
/*     */     //   231: aload #13
/*     */     //   233: invokevirtual getX : ()D
/*     */     //   236: dload_3
/*     */     //   237: dadd
/*     */     //   238: aload #13
/*     */     //   240: invokevirtual getY : ()D
/*     */     //   243: dload #5
/*     */     //   245: dadd
/*     */     //   246: aload #13
/*     */     //   248: invokevirtual getZ : ()D
/*     */     //   251: dload #7
/*     */     //   253: dadd
/*     */     //   254: invokespecial <init> : (DDD)V
/*     */     //   257: aload #9
/*     */     //   259: ifnonnull -> 270
/*     */     //   262: aload #13
/*     */     //   264: invokevirtual getXRot : ()F
/*     */     //   267: goto -> 275
/*     */     //   270: aload #9
/*     */     //   272: invokevirtual floatValue : ()F
/*     */     //   275: aload #10
/*     */     //   277: ifnonnull -> 288
/*     */     //   280: aload #13
/*     */     //   282: invokevirtual getYRot : ()F
/*     */     //   285: goto -> 293
/*     */     //   288: aload #10
/*     */     //   290: invokevirtual floatValue : ()F
/*     */     //   293: iconst_0
/*     */     //   294: invokespecial <init> : (ILac/grim/grimac/shaded/com/github/retrooper/packetevents/util/Vector3d;FFZ)V
/*     */     //   297: invokevirtual writePacket : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/wrapper/PacketWrapper;)V
/*     */     //   300: aload_1
/*     */     //   301: iconst_1
/*     */     //   302: invokevirtual setCancelled : (Z)V
/*     */     //   305: return
/*     */     //   306: aload #13
/*     */     //   308: aload #13
/*     */     //   310: invokevirtual getX : ()D
/*     */     //   313: dload_3
/*     */     //   314: dadd
/*     */     //   315: invokevirtual setX : (D)V
/*     */     //   318: aload #13
/*     */     //   320: aload #13
/*     */     //   322: invokevirtual getY : ()D
/*     */     //   325: dload #5
/*     */     //   327: dadd
/*     */     //   328: invokevirtual setY : (D)V
/*     */     //   331: aload #13
/*     */     //   333: aload #13
/*     */     //   335: invokevirtual getZ : ()D
/*     */     //   338: dload #7
/*     */     //   340: dadd
/*     */     //   341: invokevirtual setZ : (D)V
/*     */     //   344: goto -> 367
/*     */     //   347: aload #13
/*     */     //   349: dload_3
/*     */     //   350: invokevirtual setX : (D)V
/*     */     //   353: aload #13
/*     */     //   355: dload #5
/*     */     //   357: invokevirtual setY : (D)V
/*     */     //   360: aload #13
/*     */     //   362: dload #7
/*     */     //   364: invokevirtual setZ : (D)V
/*     */     //   367: aload #9
/*     */     //   369: ifnull -> 392
/*     */     //   372: aload #13
/*     */     //   374: aload #9
/*     */     //   376: invokevirtual floatValue : ()F
/*     */     //   379: invokevirtual setXRot : (F)V
/*     */     //   382: aload #13
/*     */     //   384: aload #10
/*     */     //   386: invokevirtual floatValue : ()F
/*     */     //   389: invokevirtual setYRot : (F)V
/*     */     //   392: aload #13
/*     */     //   394: invokevirtual getLastTransactionHung : ()I
/*     */     //   397: aload_0
/*     */     //   398: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   401: getfield lastTransactionSent : Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   404: invokevirtual get : ()I
/*     */     //   407: if_icmpne -> 417
/*     */     //   410: aload_0
/*     */     //   411: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   414: invokevirtual sendTransaction : ()V
/*     */     //   417: aload #13
/*     */     //   419: aload_0
/*     */     //   420: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   423: getfield lastTransactionSent : Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   426: invokevirtual get : ()I
/*     */     //   429: invokevirtual setLastTransactionHung : (I)V
/*     */     //   432: aload_0
/*     */     //   433: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   436: getfield lastTransactionSent : Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   439: invokevirtual get : ()I
/*     */     //   442: istore #15
/*     */     //   444: aload_0
/*     */     //   445: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   448: getfield latencyUtils : Lac/grim/grimac/utils/latency/LatencyUtils;
/*     */     //   451: iload #15
/*     */     //   453: aload_0
/*     */     //   454: iload_2
/*     */     //   455: aload #9
/*     */     //   457: iload #11
/*     */     //   459: iload #12
/*     */     //   461: dload_3
/*     */     //   462: dload #5
/*     */     //   464: dload #7
/*     */     //   466: <illegal opcode> run : (Lac/grim/grimac/events/packets/PacketEntityReplication;ILjava/lang/Float;ZZDDD)Ljava/lang/Runnable;
/*     */     //   471: invokevirtual addRealTimeTask : (ILjava/lang/Runnable;)V
/*     */     //   474: aload_0
/*     */     //   475: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   478: getfield latencyUtils : Lac/grim/grimac/utils/latency/LatencyUtils;
/*     */     //   481: iload #15
/*     */     //   483: iconst_1
/*     */     //   484: iadd
/*     */     //   485: aload_0
/*     */     //   486: iload_2
/*     */     //   487: <illegal opcode> run : (Lac/grim/grimac/events/packets/PacketEntityReplication;I)Ljava/lang/Runnable;
/*     */     //   492: invokevirtual addRealTimeTask : (ILjava/lang/Runnable;)V
/*     */     //   495: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #445	-> 0
/*     */     //   #447	-> 13
/*     */     //   #448	-> 24
/*     */     //   #450	-> 36
/*     */     //   #452	-> 41
/*     */     //   #458	-> 46
/*     */     //   #459	-> 69
/*     */     //   #460	-> 80
/*     */     //   #463	-> 92
/*     */     //   #464	-> 112
/*     */     //   #470	-> 139
/*     */     //   #471	-> 145
/*     */     //   #472	-> 215
/*     */     //   #473	-> 300
/*     */     //   #474	-> 305
/*     */     //   #477	-> 306
/*     */     //   #478	-> 318
/*     */     //   #479	-> 331
/*     */     //   #480	-> 344
/*     */     //   #481	-> 347
/*     */     //   #482	-> 353
/*     */     //   #483	-> 360
/*     */     //   #485	-> 367
/*     */     //   #486	-> 372
/*     */     //   #487	-> 382
/*     */     //   #491	-> 392
/*     */     //   #492	-> 410
/*     */     //   #494	-> 417
/*     */     //   #497	-> 432
/*     */     //   #499	-> 444
/*     */     //   #510	-> 474
/*     */     //   #515	-> 495
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   139	205	15	vanillaVehicleFlight	Z
/*     */     //   0	496	0	this	Lac/grim/grimac/events/packets/PacketEntityReplication;
/*     */     //   0	496	1	event	Lac/grim/grimac/shaded/com/github/retrooper/packetevents/event/PacketSendEvent;
/*     */     //   0	496	2	entityId	I
/*     */     //   0	496	3	deltaX	D
/*     */     //   0	496	5	deltaY	D
/*     */     //   0	496	7	deltaZ	D
/*     */     //   0	496	9	yaw	Ljava/lang/Float;
/*     */     //   0	496	10	pitch	Ljava/lang/Float;
/*     */     //   0	496	11	isRelative	Z
/*     */     //   0	496	12	hasPos	Z
/*     */     //   13	483	13	data	Lac/grim/grimac/utils/data/TrackerData;
/*     */     //   24	472	14	didNotSendPreWave	Z
/*     */     //   444	52	15	lastTrans	I
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
/*     */   
/*     */   public void addEntity(int entityID, UUID uuid, EntityType type, Vector3d position, float xRot, float yRot, List<EntityData<?>> entityMetadata, int extraData) {
/* 518 */     if (this.despawnedEntitiesThisTransaction.contains(Integer.valueOf(entityID))) {
/* 519 */       this.player.sendTransaction();
/*     */     }
/*     */     
/* 522 */     this.player.compensatedEntities.serverPositionsMap.put(entityID, new TrackerData(position.getX(), position.getY(), position.getZ(), xRot, yRot, type, this.player.lastTransactionSent.get()));
/*     */     
/* 524 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */           this.player.compensatedEntities.addEntity(entityID, uuid, type, position, xRot, extraData);
/*     */           if (entityMetadata != null) {
/*     */             this.player.compensatedEntities.updateEntityMetadata(entityID, entityMetadata);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isDirectlyAffectingPlayer(GrimPlayer player, int entityID) {
/* 534 */     return ((player.compensatedEntities.serverPlayerVehicle == null && entityID == player.entityID) || (player.compensatedEntities.serverPlayerVehicle != null && entityID == player.compensatedEntities.serverPlayerVehicle
/* 535 */       .intValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEndOfTickEvent() {
/* 540 */     this.player.sendTransaction(true);
/*     */   }
/*     */   
/*     */   public void tickStartTick() {
/* 544 */     this.hasSentPreWavePacket.set(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 549 */     this.maxFireworkBoostPing = config.getIntElse("max-ping-firework-boost", 1000);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketEntityReplication.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
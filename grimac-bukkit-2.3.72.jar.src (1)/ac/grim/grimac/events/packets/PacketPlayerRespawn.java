/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsE;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsF;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsG;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsH;
/*     */ import ac.grim.grimac.checks.impl.elytra.ElytraC;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
/*     */ import ac.grim.grimac.utils.data.KnownInput;
/*     */ import ac.grim.grimac.utils.data.TrackerData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
/*     */ import ac.grim.grimac.utils.enums.Pose;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class PacketPlayerRespawn
/*     */   extends PacketListenerAbstract
/*     */ {
/*     */   private static final byte KEEP_ATTRIBUTES = 1;
/*     */   private static final byte KEEP_TRACKED_DATA = 2;
/*     */   private static final byte KEEP_ALL = 3;
/*     */   
/*     */   public PacketPlayerRespawn() {
/*  57 */     super(PacketListenerPriority.HIGH);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean hasFlag(WrapperPlayServerRespawn respawn, byte flag) {
/*  62 */     if (flag == 1) {
/*     */ 
/*     */       
/*  65 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
/*  66 */         return false;
/*     */       }
/*  68 */     } else if (flag == 2) {
/*     */       
/*  70 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
/*  71 */         return true;
/*     */       }
/*     */     } 
/*     */     
/*  75 */     return ((respawn.getKeptData() & flag) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  80 */     if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
/*  81 */       WrapperPlayServerUpdateHealth health = new WrapperPlayServerUpdateHealth(event);
/*     */       
/*  83 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  84 */       if (player == null)
/*     */         return; 
/*  86 */       if (player.packetStateData.lastFood == health.getFood() && player.packetStateData.lastHealth == health
/*  87 */         .getHealth() && player.packetStateData.lastSaturation == health
/*  88 */         .getFoodSaturation() && 
/*  89 */         PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/*     */         return;
/*     */       }
/*  92 */       player.packetStateData.lastFood = health.getFood();
/*  93 */       player.packetStateData.lastHealth = health.getHealth();
/*  94 */       player.packetStateData.lastSaturation = health.getFoodSaturation();
/*     */       
/*  96 */       player.sendTransaction();
/*     */       
/*  98 */       if (health.getFood() == 20) {
/*  99 */         player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get(), () -> player.food = 20);
/*     */       } else {
/* 101 */         player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get() + 1, () -> player.food = health.getFood());
/*     */       } 
/*     */       
/* 104 */       if (health.getHealth() <= 0.0F) {
/* 105 */         player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedEntities.self.isDead = true);
/*     */       } else {
/* 107 */         player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.compensatedEntities.self.isDead = false);
/*     */       } 
/*     */       
/* 110 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */     } 
/*     */     
/* 113 */     if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
/* 114 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 115 */       if (player == null)
/*     */         return; 
/* 117 */       WrapperPlayServerJoinGame joinGame = new WrapperPlayServerJoinGame(event);
/* 118 */       player.gamemode = joinGame.getGameMode();
/* 119 */       player.entityID = joinGame.getEntityId();
/* 120 */       player.dimensionType = joinGame.getDimensionType();
/* 121 */       player.worldName = joinGame.getWorldName();
/*     */       
/* 123 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17))
/*     */         return; 
/* 125 */       player.compensatedWorld.setDimension(joinGame.getDimensionType(), event.getUser());
/*     */     } 
/*     */     
/* 128 */     if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
/* 129 */       WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(event);
/*     */       
/* 131 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 132 */       if (player == null)
/*     */         return; 
/* 134 */       List<Runnable> tasks = event.getTasksAfterSend();
/* 135 */       Objects.requireNonNull(player); tasks.add(player::sendTransaction);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 140 */       (player.getSetbackTeleportUtil()).hasAcceptedSpawnTeleport = false;
/* 141 */       (player.getSetbackTeleportUtil()).lastKnownGoodPosition = null;
/*     */ 
/*     */       
/* 144 */       if (isWorldChange(player, respawn)) {
/* 145 */         player.compensatedEntities.serverPositionsMap.clear();
/*     */       }
/*     */       
/* 148 */       player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
/*     */             if (player.getClientVersion().isOlderThan(ClientVersion.V_1_16) || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20)) {
/*     */               player.isSneaking = false;
/*     */             }
/*     */             player.lastOnGround = false;
/*     */             player.clientClaimsLastOnGround = false;
/*     */             player.onGround = false;
/*     */             player.isInBed = false;
/*     */             player.packetStateData.setSlowedByUsingItem(false);
/*     */             player.packetStateData.packetPlayerOnGround = false;
/*     */             player.packetStateData.lastClaimedPosition = new Vector3d();
/*     */             player.filterMojangStupidityOnMojangStupidity = new Vector3d();
/*     */             boolean keepTrackedData = hasFlag(respawn, (byte)2);
/*     */             if (!keepTrackedData) {
/*     */               player.powderSnowFrozenTicks = 0;
/*     */               player.compensatedEntities.self.hasGravity = true;
/*     */               player.playerEntityHasGravity = true;
/*     */               player.packetStateData.knownInput = KnownInput.DEFAULT;
/*     */               ((ElytraC)player.checkManager.getPostPredictionCheck(ElytraC.class)).exempt = true;
/*     */               if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_4)) {
/*     */                 player.isSprinting = false;
/*     */               } else {
/*     */                 player.lastSprintingForSpeed = false;
/*     */               } 
/*     */             } 
/*     */             ((BadPacketsE)player.checkManager.getPacketCheck(BadPacketsE.class)).handleRespawn();
/*     */             ((BadPacketsG)player.checkManager.getPacketCheck(BadPacketsG.class)).handleRespawn();
/*     */             if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)) {
/*     */               ((BadPacketsF)player.checkManager.getPacketCheck(BadPacketsF.class)).exemptNext = true;
/*     */             }
/*     */             if (isWorldChange(player, respawn)) {
/*     */               player.compensatedEntities.entityMap.clear();
/*     */               player.compensatedWorld.activePistons.clear();
/*     */               player.compensatedWorld.openShulkerBoxes.clear();
/*     */               player.compensatedWorld.chunks.clear();
/*     */               player.compensatedWorld.isRaining = false;
/*     */               ((BadPacketsH)player.checkManager.getBlockPlaceCheck(BadPacketsH.class)).onWorldChange();
/*     */             } 
/*     */             player.dimensionType = respawn.getDimensionType();
/*     */             player.worldName = respawn.getWorldName().orElse(null);
/*     */             player.compensatedEntities.serverPlayerVehicle = null;
/*     */             player.compensatedEntities.self = new PacketEntitySelf(player, player.compensatedEntities.self);
/*     */             player.compensatedEntities.selfTrackedEntity = new TrackerData(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, EntityTypes.PLAYER, player.lastTransactionSent.get());
/*     */             if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14)) {
/*     */               player.isSprinting = false;
/*     */               ((BadPacketsF)player.checkManager.getPacketCheck(BadPacketsF.class)).lastSprinting = false;
/*     */               player.compensatedEntities.hasSprintingAttributeEnabled = false;
/*     */             } 
/*     */             player.pose = Pose.STANDING;
/*     */             player.clientVelocity = new Vector3dm();
/*     */             player.gamemode = respawn.getGameMode();
/*     */             if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
/*     */               player.compensatedWorld.setDimension(respawn.getDimensionType(), event.getUser());
/*     */             }
/*     */             if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && !hasFlag(respawn, (byte)1)) {
/*     */               player.compensatedEntities.self.resetAttributes();
/*     */               player.compensatedEntities.hasSprintingAttributeEnabled = false;
/*     */             } 
/*     */           });
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isWorldChange(GrimPlayer player, WrapperPlayServerRespawn respawn) {
/* 228 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_16)) {
/* 229 */       return !Objects.equals(respawn.getWorldName().orElse(null), player.worldName);
/*     */     }
/*     */     
/* 232 */     ClientVersion version = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/* 233 */     return (respawn.getDimensionType().getId(version) != player.dimensionType.getId(version) || 
/* 234 */       !Objects.equals(respawn.getDimensionType().getName(), player.dimensionType.getName()));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerRespawn.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
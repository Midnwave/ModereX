/*     */ package ac.grim.grimac.manager;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsN;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineElytra;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineNormal;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineWater;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.chunks.Column;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.SetBackData;
/*     */ import ac.grim.grimac.utils.data.TeleportAcceptData;
/*     */ import ac.grim.grimac.utils.data.TeleportData;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.VelocityData;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Location;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.math.VectorUtils;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.ReachUtils;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class SetbackTeleportUtil
/*     */   extends Check
/*     */   implements PostPredictionCheck
/*     */ {
/*  51 */   public final ConcurrentLinkedQueue<TeleportData> pendingTeleports = new ConcurrentLinkedQueue<>();
/*  52 */   private final Random random = new Random();
/*     */ 
/*     */   
/*     */   public boolean hasAcceptedSpawnTeleport = false;
/*     */   
/*     */   public boolean blockOffsets = false;
/*     */   
/*     */   public SetbackPosWithVector lastKnownGoodPosition;
/*     */   
/*     */   public boolean isSendingSetback = false;
/*     */   
/*  63 */   public int cheatVehicleInterpolationDelay = 0;
/*     */ 
/*     */   
/*  66 */   private SetBackData requiredSetBack = null; @Generated public SetBackData getRequiredSetBack() { return this.requiredSetBack; }
/*     */   
/*  68 */   private long lastWorldResync = 0L;
/*     */   
/*     */   public SetbackTeleportUtil(GrimPlayer player) {
/*  71 */     super(player);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  77 */     Vector3dm afterTickFriction = this.player.clientVelocity.clone();
/*     */ 
/*     */ 
/*     */     
/*  81 */     if (predictionComplete.getData().getSetback() != null) {
/*     */       
/*  83 */       if (this.cheatVehicleInterpolationDelay > 0) this.cheatVehicleInterpolationDelay = 10;
/*     */       
/*  85 */       this.lastKnownGoodPosition = new SetbackPosWithVector(new Vector3d(this.player.x, this.player.y, this.player.z), afterTickFriction);
/*  86 */     } else if (this.requiredSetBack == null || this.requiredSetBack.isComplete()) {
/*  87 */       this.cheatVehicleInterpolationDelay--;
/*     */ 
/*     */       
/*  90 */       this.lastKnownGoodPosition = new SetbackPosWithVector(new Vector3d(this.player.x, this.player.y, this.player.z), afterTickFriction);
/*     */     } 
/*     */     
/*  93 */     if (this.requiredSetBack != null) this.requiredSetBack.tick(); 
/*     */   }
/*     */   
/*     */   public void executeForceResync() {
/*  97 */     if (this.player.gamemode == GameMode.SPECTATOR || this.player.disableGrim)
/*     */       return; 
/*  99 */     if (this.lastKnownGoodPosition == null)
/* 100 */       return;  blockMovementsUntilResync(true, true);
/*     */   }
/*     */   
/*     */   public void executeNonSimulatingSetback() {
/* 104 */     if (this.player.gamemode == GameMode.SPECTATOR || this.player.disableGrim)
/*     */       return; 
/* 106 */     if (this.lastKnownGoodPosition == null)
/* 107 */       return;  blockMovementsUntilResync(false, false);
/*     */   }
/*     */   
/*     */   public boolean executeViolationSetback() {
/* 111 */     if (isExempt()) return false; 
/* 112 */     blockMovementsUntilResync(true, false);
/* 113 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isExempt() {
/* 119 */     if (this.lastKnownGoodPosition == null) return true;
/*     */     
/* 121 */     if (this.player.disableGrim) return true;
/*     */     
/* 123 */     return (this.player.platformPlayer != null && this.player.noSetbackPermission);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void simulateFriction(Vector3dm vector) {
/* 129 */     if (this.player.wasTouchingWater) {
/* 130 */       PredictionEngineWater.staticVectorEndOfTick(this.player, vector, 0.8F, this.player.gravity, true);
/* 131 */     } else if (this.player.wasTouchingLava) {
/* 132 */       vector.multiply(0.5D);
/* 133 */       if (this.player.hasGravity)
/* 134 */         vector.add(new Vector3dm(0.0D, -this.player.gravity / 4.0D, 0.0D)); 
/* 135 */     } else if (this.player.isGliding) {
/* 136 */       PredictionEngineElytra.getElytraMovement(this.player, vector, ReachUtils.getLook(this.player, this.player.xRot, this.player.yRot)).multiply(this.player.stuckSpeedMultiplier).multiply(new Vector3dm(0.99F, 0.98F, 0.99F));
/* 137 */       vector.setY(vector.getY() - 0.05D);
/*     */     } else {
/* 139 */       PredictionEngineNormal.staticVectorEndOfTick(this.player, vector);
/* 140 */       vector.multiply(this.player.stuckSpeedMultiplier);
/*     */     } 
/*     */ 
/*     */     
/* 144 */     (new PredictionEngine()).applyMovementThreshold(this.player, new HashSet(Collections.singletonList(new VectorData(vector, VectorData.VectorType.BestVelPicked))));
/*     */   }
/*     */   
/*     */   private void blockMovementsUntilResync(boolean simulateNextTickPosition, boolean isResync) {
/* 148 */     if (this.requiredSetBack == null)
/* 149 */       return;  if (this.player.platformPlayer != null && this.player.noSetbackPermission)
/*     */       return; 
/* 151 */     this.requiredSetBack.setPlugin(false);
/* 152 */     if (isPendingSetback()) {
/*     */       return;
/*     */     }
/* 155 */     if (System.currentTimeMillis() - this.lastWorldResync > 5000L) {
/* 156 */       this.player.resyncPositions(this.player.boundingBox.copy().expand(1.0D));
/* 157 */       this.lastWorldResync = System.currentTimeMillis();
/*     */     } 
/*     */     
/* 160 */     Vector3dm clientVel = this.lastKnownGoodPosition.vector.clone();
/*     */     
/* 162 */     Pair<VelocityData, Vector3dm> futureKb = this.player.checkManager.getKnockbackHandler().getFutureKnockback();
/* 163 */     VelocityData futureExplosion = this.player.checkManager.getExplosionHandler().getFutureExplosion();
/*     */ 
/*     */     
/* 166 */     if (futureKb.first() != null) {
/* 167 */       clientVel = (Vector3dm)futureKb.second();
/*     */     }
/*     */ 
/*     */     
/* 171 */     if (futureExplosion != null && (futureKb.first() == null || ((VelocityData)futureKb.first()).transaction < futureExplosion.transaction)) {
/* 172 */       clientVel.add(futureExplosion.vector);
/*     */     }
/*     */     
/* 175 */     Vector3d position = this.lastKnownGoodPosition.pos;
/*     */     
/* 177 */     SimpleCollisionBox oldBB = this.player.boundingBox;
/* 178 */     this.player.boundingBox = GetBoundingBox.getPlayerBoundingBox(this.player, position.getX(), position.getY(), position.getZ());
/*     */ 
/*     */     
/* 181 */     if (simulateNextTickPosition) {
/* 182 */       Vector3dm collide = Collisions.collide(this.player, clientVel.getX(), clientVel.getY(), clientVel.getZ());
/*     */       
/* 184 */       position = position.withX(position.getX() + collide.getX());
/* 185 */       position = position.withY(position.getY() + collide.getY());
/*     */       
/* 187 */       if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9))
/*     */       {
/*     */         
/* 190 */         position = position.withY(position.getY() + 1.0E-7D);
/*     */       }
/* 192 */       position = position.withZ(position.getZ() + collide.getZ());
/*     */       
/* 194 */       if (clientVel.getX() != collide.getX()) clientVel.setX(0); 
/* 195 */       if (clientVel.getY() != collide.getY()) clientVel.setY(0); 
/* 196 */       if (clientVel.getZ() != collide.getZ()) clientVel.setZ(0);
/*     */       
/* 198 */       simulateFriction(clientVel);
/*     */     } 
/*     */     
/* 201 */     this.player.boundingBox = oldBB;
/*     */     
/* 203 */     if (!this.hasAcceptedSpawnTeleport || this.player.isFlying) {
/* 204 */       clientVel = null;
/*     */     }
/*     */     
/* 207 */     if (isResync) {
/* 208 */       this.blockOffsets = true;
/*     */     }
/*     */     
/* 211 */     SetBackData data = new SetBackData(new TeleportData(position, new Vector3d(), new RelativeFlag(24), this.player.lastTransactionSent.get(), 0), this.player.xRot, this.player.yRot, clientVel, this.player.inVehicle(), false);
/* 212 */     sendSetback(data);
/*     */   }
/*     */   
/*     */   private void sendSetback(SetBackData data) {
/* 216 */     this.isSendingSetback = true;
/* 217 */     Vector3d position = data.getTeleportData().getLocation();
/*     */ 
/*     */     
/*     */     try {
/* 221 */       if (this.player.inVehicle()) {
/* 222 */         int vehicleId = this.player.getRidingVehicleId();
/* 223 */         if (this.player.compensatedEntities.serverPlayerVehicle != null) {
/*     */           
/* 225 */           if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 226 */             this.player.user.sendPacket((PacketWrapper)new WrapperPlayServerSetPassengers(vehicleId, new int[2]));
/*     */           } else {
/* 228 */             this.player.user.sendPacket((PacketWrapper)new WrapperPlayServerAttachEntity(vehicleId, -1, false));
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 233 */           this.player.user.sendPacket((PacketWrapper)new WrapperPlayServerEntityTeleport(vehicleId, new Vector3d(position.getX(), position.getY(), position.getZ()), this.player.xRot % 360.0F, 0.0F, false));
/* 234 */           (this.player.getSetbackTeleportUtil()).cheatVehicleInterpolationDelay = Integer.MAX_VALUE;
/*     */ 
/*     */           
/* 237 */           GrimAPI.INSTANCE.getScheduler().getEntityScheduler().execute((GrimEntity)this.player.platformPlayer, GrimAPI.INSTANCE.getGrimPlugin(), () -> { if (this.player.platformPlayer != null) { GrimEntity vehicle = this.player.platformPlayer.getVehicle(); if (vehicle != null) vehicle.eject();  }  }null, 0L);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 248 */       double y = position.getY();
/* 249 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_7_10)) {
/* 250 */         y += 1.62D;
/*     */       }
/*     */ 
/*     */       
/* 254 */       this.player.sendTransaction();
/*     */ 
/*     */       
/* 257 */       int teleportId = this.random.nextInt() | Integer.MIN_VALUE;
/* 258 */       data.setPlugin(false);
/* 259 */       data.getTeleportData().setTeleportId(teleportId);
/* 260 */       data.getTeleportData().setTransaction(this.player.lastTransactionSent.get());
/*     */ 
/*     */       
/* 263 */       addSentTeleport(new Location(null, position.getX(), y, position.getZ(), this.player.xRot % 360.0F, this.player.yRot % 360.0F), new Vector3d(), data.getTeleportData().getTransaction(), new RelativeFlag(24), false, teleportId);
/*     */       
/* 265 */       this.requiredSetBack = data;
/*     */       
/* 267 */       PacketEvents.getAPI().getProtocolManager().sendPacketSilently(this.player.user.getChannel(), (PacketWrapper)new WrapperPlayServerPlayerPositionAndLook(position.getX(), position.getY(), position.getZ(), 0.0F, 0.0F, data.getTeleportData().getFlags().getMask(), teleportId, false));
/* 268 */       this.player.sendTransaction();
/*     */       
/* 270 */       if (data.getVelocity() != null && data.getVelocity().lengthSquared() > 0.0D) {
/* 271 */         this.player.user.sendPacket((PacketWrapper)new WrapperPlayServerEntityVelocity(this.player.entityID, new Vector3d(data.getVelocity().getX(), data.getVelocity().getY(), data.getVelocity().getZ())));
/*     */       }
/*     */     } finally {
/* 274 */       this.isSendingSetback = false;
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
/*     */   public TeleportAcceptData checkTeleportQueue(double x, double y, double z) {
/* 287 */     TeleportAcceptData teleportData = new TeleportAcceptData();
/*     */     
/*     */     TeleportData teleportPos;
/* 290 */     while ((teleportPos = this.pendingTeleports.peek()) != null) {
/* 291 */       double trueTeleportX = (teleportPos.isRelativeX() ? this.player.x : 0.0D) + teleportPos.getLocation().getX();
/* 292 */       double trueTeleportY = (teleportPos.isRelativeY() ? this.player.y : 0.0D) + teleportPos.getLocation().getY();
/* 293 */       double trueTeleportZ = (teleportPos.isRelativeZ() ? this.player.z : 0.0D) + teleportPos.getLocation().getZ();
/*     */ 
/*     */       
/* 296 */       Vector3d clamped = VectorUtils.clampVector(new Vector3d(trueTeleportX, trueTeleportY, trueTeleportZ));
/* 297 */       double threshold = teleportPos.isRelativePos() ? this.player.getMovementThreshold() : 0.0D;
/* 298 */       boolean closeEnoughY = (Math.abs(clamped.getY() - y) <= 1.0E-7D + threshold);
/*     */       
/* 300 */       if (this.player.lastTransactionReceived.get() == teleportPos.getTransaction() && Math.abs(clamped.getX() - x) <= threshold && closeEnoughY && Math.abs(clamped.getZ() - z) <= threshold) {
/* 301 */         this.pendingTeleports.poll();
/* 302 */         this.hasAcceptedSpawnTeleport = true;
/* 303 */         this.blockOffsets = false;
/*     */ 
/*     */ 
/*     */         
/* 307 */         if (this.requiredSetBack != null && this.requiredSetBack.getTeleportData().getTransaction() == teleportPos.getTransaction()) {
/* 308 */           teleportData.setSetback(this.requiredSetBack);
/* 309 */           this.requiredSetBack.setComplete(true);
/*     */         } 
/*     */         
/* 312 */         teleportData.setTeleportData(teleportPos);
/* 313 */         teleportData.setTeleport(true); break;
/*     */       } 
/* 315 */       if (this.player.lastTransactionReceived.get() > teleportPos.getTransaction()) {
/*     */         
/* 317 */         ((BadPacketsN)this.player.checkManager.<BadPacketsN>getCheck(BadPacketsN.class)).flagAndAlert();
/* 318 */         this.pendingTeleports.poll();
/* 319 */         this.requiredSetBack.setPlugin(false);
/* 320 */         if (this.pendingTeleports.isEmpty()) {
/* 321 */           sendSetback(this.requiredSetBack);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 329 */     return teleportData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkVehicleTeleportQueue(double x, double y, double z) {
/* 339 */     int lastTransaction = this.player.lastTransactionReceived.get();
/*     */     
/*     */     while (true) {
/* 342 */       Pair<Integer, Vector3d> teleportPos = this.player.vehicleData.vehicleTeleports.peek();
/* 343 */       if (teleportPos == null || 
/* 344 */         lastTransaction < ((Integer)teleportPos.first()).intValue()) {
/*     */         break;
/*     */       }
/*     */       
/* 348 */       Vector3d position = (Vector3d)teleportPos.second();
/* 349 */       if (position.getX() == x && position.getY() == y && position.getZ() == z) {
/* 350 */         this.player.vehicleData.vehicleTeleports.poll();
/*     */         
/* 352 */         return true;
/* 353 */       }  if (lastTransaction > ((Integer)teleportPos.first()).intValue() + 1) {
/* 354 */         this.player.vehicleData.vehicleTeleports.poll();
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/*     */       break;
/*     */     } 
/*     */     
/* 364 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldBlockMovement() {
/* 373 */     return (insideUnloadedChunk() || this.blockOffsets || (this.requiredSetBack != null && !this.requiredSetBack.isComplete()));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isPendingSetback() {
/* 378 */     if (this.requiredSetBack.getTeleportData().isRelativeX() || this.requiredSetBack.getTeleportData().isRelativeY() || this.requiredSetBack.getTeleportData().isRelativeZ()) {
/* 379 */       return false;
/*     */     }
/*     */     
/* 382 */     return (this.requiredSetBack != null && !this.requiredSetBack.isComplete());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean insideUnloadedChunk() {
/* 391 */     Column column = this.player.compensatedWorld.getChunk(GrimMath.floor(this.player.x) >> 4, GrimMath.floor(this.player.z) >> 4);
/*     */ 
/*     */     
/* 394 */     return (!this.player.disableGrim && (column == null || column.transaction() >= this.player.lastTransactionReceived.get() || 
/*     */       
/* 396 */       !(this.player.getSetbackTeleportUtil()).hasAcceptedSpawnTeleport));
/*     */   }
/*     */   
/*     */   public void addSentTeleport(Location position, Vector3d velocity, int transaction, RelativeFlag flags, boolean plugin, int teleportId) {
/* 400 */     TeleportData data = new TeleportData(new Vector3d(position.getX(), position.getY(), position.getZ()), velocity, flags, transaction, teleportId);
/* 401 */     this.pendingTeleports.add(data);
/*     */     
/* 403 */     Vector3d safePosition = new Vector3d(position.getX(), position.getY(), position.getZ());
/*     */ 
/*     */     
/* 406 */     if (flags.has(RelativeFlag.X)) {
/* 407 */       safePosition = safePosition.withX(safePosition.getX() + this.lastKnownGoodPosition.pos.getX());
/*     */     }
/*     */     
/* 410 */     if (flags.has(RelativeFlag.Y)) {
/* 411 */       safePosition = safePosition.withY(safePosition.getY() + this.lastKnownGoodPosition.pos.getY());
/*     */     }
/*     */     
/* 414 */     if (flags.has(RelativeFlag.Z)) {
/* 415 */       safePosition = safePosition.withZ(safePosition.getZ() + this.lastKnownGoodPosition.pos.getZ());
/*     */     }
/*     */     
/* 418 */     data = new TeleportData(safePosition, velocity, new RelativeFlag(24), transaction, teleportId);
/* 419 */     this.requiredSetBack = new SetBackData(data, this.player.xRot, this.player.yRot, null, false, plugin);
/*     */     
/* 421 */     this.lastKnownGoodPosition = new SetbackPosWithVector(safePosition, new Vector3dm());
/*     */   } public static class SetbackPosWithVector {
/*     */     private Vector3d pos; private Vector3dm vector; @Generated
/* 424 */     public SetbackPosWithVector(Vector3d pos, Vector3dm vector) { this.pos = pos; this.vector = vector; }
/*     */     @Generated
/* 426 */     public void setPos(Vector3d pos) { this.pos = pos; } @Generated public void setVector(Vector3dm vector) { this.vector = vector; }
/*     */     @Generated
/* 428 */     public Vector3d getPos() { return this.pos; } @Generated
/* 429 */     public Vector3dm getVector() { return this.vector; }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\SetbackTeleportUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
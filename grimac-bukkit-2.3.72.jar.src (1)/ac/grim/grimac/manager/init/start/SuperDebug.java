/*     */ package ac.grim.grimac.manager.init.start;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.UncertaintyHandler;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2IntMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.Object2IntOpenHashMap;
/*     */ import ac.grim.grimac.shaded.fastutil.objects.ObjectIterator;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.lists.EvictingQueue;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class SuperDebug
/*     */   extends Check implements PostPredictionCheck {
/*  28 */   private static final StringBuilder[] flags = new StringBuilder[256];
/*     */   
/*  30 */   Object2IntMap<StringBuilder> continuedDebug = (Object2IntMap<StringBuilder>)new Object2IntOpenHashMap();
/*     */   
/*  32 */   List<VectorData> predicted = (List<VectorData>)new EvictingQueue(60);
/*  33 */   List<Vector3dm> actually = (List<Vector3dm>)new EvictingQueue(60);
/*  34 */   List<Location> locations = (List<Location>)new EvictingQueue(60);
/*  35 */   List<Vector3dm> startTickClientVel = (List<Vector3dm>)new EvictingQueue(60);
/*  36 */   List<Vector3dm> baseTickAddition = (List<Vector3dm>)new EvictingQueue(60);
/*  37 */   List<Vector3dm> baseTickWater = (List<Vector3dm>)new EvictingQueue(60);
/*     */   
/*     */   public SuperDebug(GrimPlayer player) {
/*  40 */     super(player);
/*     */   }
/*     */   
/*     */   public static StringBuilder getFlag(int identifier) {
/*  44 */     identifier--;
/*  45 */     if (identifier >= flags.length || identifier < 0) return null; 
/*  46 */     return flags[identifier];
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  51 */     if (!predictionComplete.isChecked())
/*     */       return; 
/*  53 */     Location location = new Location(this.player.x, this.player.y, this.player.z, this.player.xRot, this.player.yRot, (this.player.platformPlayer == null) ? "null" : this.player.platformPlayer.getWorld().getName());
/*     */     
/*  55 */     for (ObjectIterator<Map.Entry<StringBuilder, Integer>> objectIterator = this.continuedDebug.object2IntEntrySet().iterator(); objectIterator.hasNext(); ) {
/*  56 */       Map.Entry<StringBuilder, Integer> debug = objectIterator.next();
/*  57 */       appendDebug(debug.getKey(), this.player.predictedVelocity, this.player.actualMovement, location, this.player.startTickClientVel, this.player.baseTickAddition, this.player.baseTickWaterPushing);
/*  58 */       debug.setValue(Integer.valueOf(((Integer)debug.getValue()).intValue() - 1));
/*  59 */       if (((Integer)debug.getValue()).intValue() <= 0) objectIterator.remove();
/*     */     
/*     */     } 
/*  62 */     this.predicted.add(this.player.predictedVelocity);
/*  63 */     this.actually.add(this.player.actualMovement);
/*  64 */     this.locations.add(location);
/*  65 */     this.startTickClientVel.add(this.player.startTickClientVel);
/*  66 */     this.baseTickAddition.add(this.player.baseTickAddition);
/*  67 */     this.baseTickWater.add(this.player.baseTickWaterPushing);
/*     */     
/*  69 */     if (predictionComplete.getIdentifier() == 0)
/*     */       return; 
/*  71 */     StringBuilder sb = new StringBuilder();
/*  72 */     sb.append("Grim Version: ").append(GrimAPI.INSTANCE.getExternalAPI().getGrimVersion());
/*  73 */     sb.append("\n");
/*  74 */     sb.append("Player Name: ");
/*  75 */     sb.append(this.player.user.getName());
/*  76 */     sb.append("\nClient Version: ");
/*  77 */     sb.append(this.player.getClientVersion().getReleaseName());
/*  78 */     sb.append("\nClient Brand: ");
/*  79 */     sb.append(this.player.getBrand());
/*  80 */     sb.append("\nServer Version: ");
/*  81 */     sb.append(PacketEvents.getAPI().getServerManager().getVersion().getReleaseName());
/*  82 */     sb.append("\nPing: ");
/*  83 */     sb.append(this.player.getTransactionPing());
/*  84 */     sb.append("ms\n\n");
/*     */     
/*  86 */     for (int i = 0; i < this.predicted.size(); i++) {
/*  87 */       VectorData predict = this.predicted.get(i);
/*  88 */       Vector3dm actual = this.actually.get(i);
/*  89 */       Location loc = this.locations.get(i);
/*  90 */       Vector3dm startTickVel = this.startTickClientVel.get(i);
/*  91 */       Vector3dm addition = this.baseTickAddition.get(i);
/*  92 */       Vector3dm water = this.baseTickWater.get(i);
/*  93 */       appendDebug(sb, predict, actual, loc, startTickVel, addition, water);
/*     */     } 
/*     */     
/*  96 */     UncertaintyHandler uncertaintyHandler = this.player.uncertaintyHandler;
/*  97 */     sb.append("XNeg: ");
/*  98 */     sb.append(uncertaintyHandler.xNegativeUncertainty);
/*  99 */     sb.append("\nXPos: ");
/* 100 */     sb.append(uncertaintyHandler.xPositiveUncertainty);
/* 101 */     sb.append("\nYNeg: ");
/* 102 */     sb.append(uncertaintyHandler.yNegativeUncertainty);
/* 103 */     sb.append("\nYPos: ");
/* 104 */     sb.append(uncertaintyHandler.yPositiveUncertainty);
/* 105 */     sb.append("\nZNeg: ");
/* 106 */     sb.append(uncertaintyHandler.zNegativeUncertainty);
/* 107 */     sb.append("\nZPos: ");
/* 108 */     sb.append(uncertaintyHandler.zPositiveUncertainty);
/* 109 */     sb.append("\nStuck: ");
/* 110 */     sb.append(uncertaintyHandler.stuckOnEdge.hasOccurredSince(1));
/* 111 */     sb.append("\n\n0.03: ");
/* 112 */     sb.append(uncertaintyHandler.lastMovementWasZeroPointZeroThree);
/* 113 */     sb.append("\n0.03 reset: ");
/* 114 */     sb.append(uncertaintyHandler.lastMovementWasUnknown003VectorReset);
/* 115 */     sb.append("\n0.03 vertical: ");
/* 116 */     sb.append(uncertaintyHandler.wasZeroPointThreeVertically);
/*     */     
/* 118 */     sb.append("\n\nIs gliding: ");
/* 119 */     sb.append(this.player.isGliding);
/* 120 */     sb.append("\nIs swimming: ");
/* 121 */     sb.append(this.player.isSwimming);
/* 122 */     sb.append("\nIs on ground: ");
/* 123 */     sb.append(this.player.onGround);
/* 124 */     sb.append("\nClient claims ground: ");
/* 125 */     sb.append(this.player.clientClaimsLastOnGround);
/* 126 */     sb.append("\nLast on ground: ");
/* 127 */     sb.append(this.player.lastOnGround);
/* 128 */     sb.append("\nWater: ");
/* 129 */     sb.append(this.player.wasTouchingWater);
/* 130 */     sb.append("\nLava: ");
/* 131 */     sb.append(this.player.wasTouchingLava);
/* 132 */     sb.append("\nVehicle: ");
/* 133 */     sb.append(this.player.inVehicle());
/*     */     
/* 135 */     sb.append("\n\n");
/* 136 */     sb.append("Bounding box: ");
/* 137 */     sb.append("minX=");
/* 138 */     sb.append(this.player.boundingBox.minX);
/* 139 */     sb.append(", minY=");
/* 140 */     sb.append(this.player.boundingBox.minY);
/* 141 */     sb.append(", minZ=");
/* 142 */     sb.append(this.player.boundingBox.minZ);
/* 143 */     sb.append(", maxX=");
/* 144 */     sb.append(this.player.boundingBox.maxX);
/* 145 */     sb.append(", maxY=");
/* 146 */     sb.append(this.player.boundingBox.maxY);
/* 147 */     sb.append(", maxZ=");
/* 148 */     sb.append(this.player.boundingBox.maxZ);
/* 149 */     sb.append('}');
/* 150 */     sb.append("\n");
/*     */     
/* 152 */     int maxLength = 0;
/* 153 */     int maxPosLength = 0;
/*     */     
/*     */     int y;
/* 156 */     for (y = GrimMath.floor(this.player.boundingBox.minY) - 2; y <= GrimMath.ceil(this.player.boundingBox.maxY) + 2; y++) {
/* 157 */       for (int z = GrimMath.floor(this.player.boundingBox.minZ) - 2; z <= GrimMath.ceil(this.player.boundingBox.maxZ) + 2; z++) {
/* 158 */         maxPosLength = (int)Math.max(maxPosLength, Math.ceil(Math.log10(Math.abs(z))));
/* 159 */         for (int x = GrimMath.floor(this.player.boundingBox.minX) - 2; x <= GrimMath.ceil(this.player.boundingBox.maxX) + 2; x++) {
/* 160 */           maxPosLength = (int)Math.max(maxPosLength, Math.ceil(Math.log10(Math.abs(x))));
/* 161 */           WrappedBlockState block = this.player.compensatedWorld.getBlock(x, y, z);
/* 162 */           maxLength = Math.max(block.toString().replace("minecraft:", "").length(), maxLength);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 167 */     maxPosLength += 4;
/* 168 */     maxLength++;
/*     */     
/* 170 */     for (y = GrimMath.ceil(this.player.boundingBox.maxY) + 2; y >= GrimMath.floor(this.player.boundingBox.minY) - 2; y--) {
/* 171 */       sb.append("y: ");
/* 172 */       sb.append(y);
/* 173 */       sb.append("\n");
/*     */       
/* 175 */       sb.append(String.format("%-" + maxPosLength + "s", new Object[] { "x: " }));
/* 176 */       for (int x = GrimMath.floor(this.player.boundingBox.minX) - 2; x <= GrimMath.ceil(this.player.boundingBox.maxX) + 2; x++) {
/* 177 */         sb.append(String.format("%-" + maxLength + "s", new Object[] { Integer.valueOf(x) }));
/*     */       } 
/* 179 */       sb.append("\n");
/*     */       
/* 181 */       for (int z = GrimMath.floor(this.player.boundingBox.minZ) - 2; z <= GrimMath.ceil(this.player.boundingBox.maxZ) + 2; z++) {
/* 182 */         sb.append(String.format("%-" + maxPosLength + "s", new Object[] { "z: " + z + " " }));
/* 183 */         for (int j = GrimMath.floor(this.player.boundingBox.minX) - 2; j <= GrimMath.ceil(this.player.boundingBox.maxX) + 2; j++) {
/* 184 */           WrappedBlockState block = this.player.compensatedWorld.getBlock(j, y, z);
/* 185 */           sb.append(String.format("%-" + maxLength + "s", new Object[] { block.toString().replace("minecraft:", "") }));
/*     */         } 
/* 187 */         sb.append("\n");
/*     */       } 
/*     */       
/* 190 */       sb.append("\n\n\n");
/*     */     } 
/*     */     
/* 193 */     flags[predictionComplete.getIdentifier() - 1] = sb;
/* 194 */     this.continuedDebug.put(sb, 40);
/*     */   }
/*     */   
/*     */   private void appendDebug(StringBuilder sb, VectorData predict, Vector3dm actual, Location location, Vector3dm startTick, Vector3dm addition, Vector3dm water) {
/* 198 */     if (predict.isZeroPointZeroThree()) {
/* 199 */       sb.append("Movement threshold/tick skipping\n");
/*     */     }
/* 201 */     if (predict.isAttackSlow()) {
/* 202 */       sb.append("* 0.6 horizontal attack slowdown\n");
/*     */     }
/* 204 */     if (predict.isKnockback()) {
/* 205 */       if (this.player.firstBreadKB != null) {
/* 206 */         sb.append("First bread knockback: ").append(this.player.firstBreadKB.vector).append("\n");
/*     */       }
/* 208 */       if (this.player.likelyKB != null) {
/* 209 */         sb.append("Second bread knockback: ").append(this.player.likelyKB.vector).append("\n");
/*     */       }
/*     */     } 
/* 212 */     if (predict.isExplosion()) {
/* 213 */       if (this.player.firstBreadExplosion != null) {
/* 214 */         sb.append("First bread explosion: ").append(this.player.firstBreadExplosion.vector).append("\n");
/*     */       }
/* 216 */       if (this.player.likelyExplosions != null) {
/* 217 */         sb.append("Second bread explosion: ").append(this.player.likelyExplosions.vector).append("\n");
/*     */       }
/*     */     } 
/* 220 */     if (predict.isTrident()) {
/* 221 */       sb.append("Trident\n");
/*     */     }
/* 223 */     if (predict.isSwimHop()) {
/* 224 */       sb.append("Swim hop\n");
/*     */     }
/* 226 */     if (predict.isJump()) {
/* 227 */       sb.append("Jump\n");
/*     */     }
/*     */ 
/*     */     
/* 231 */     Set<VectorData> set = new HashSet<>(Collections.singletonList(new VectorData(startTick.clone(), VectorData.VectorType.BestVelPicked)));
/* 232 */     (new PredictionEngine()).applyMovementThreshold(this.player, set);
/* 233 */     Vector3dm trueStartVel = ((VectorData)set.toArray()[0]).vector;
/*     */     
/* 235 */     Vector3dm clientMovement = getPlayerMathMovement(this.player, actual.clone().subtract(trueStartVel), location.xRot);
/* 236 */     Vector3dm simulatedMovement = getPlayerMathMovement(this.player, predict.vector.clone().subtract(trueStartVel), location.xRot);
/* 237 */     Vector3dm offset = actual.clone().subtract(predict.vector);
/* 238 */     trueStartVel.add(addition);
/* 239 */     trueStartVel.add(water);
/*     */     
/* 241 */     sb.append("Simulated: ");
/* 242 */     sb.append(predict.vector.toString());
/* 243 */     sb.append("\nActually:  ");
/* 244 */     sb.append(actual);
/* 245 */     sb.append("\nOffset Vector: ");
/* 246 */     sb.append(offset);
/* 247 */     sb.append("\nOffset: ");
/* 248 */     sb.append(offset.length());
/* 249 */     sb.append("\nLocation:  ");
/* 250 */     sb.append(location);
/* 251 */     sb.append("\nInitial velocity: ");
/* 252 */     sb.append(startTick);
/*     */     
/* 254 */     if (addition.lengthSquared() > 0.0D) {
/* 255 */       sb.append("\nInitial vel addition: ");
/* 256 */       sb.append(addition);
/*     */     } 
/* 258 */     if (water.lengthSquared() > 0.0D) {
/* 259 */       sb.append("\nWater vel addition: ");
/* 260 */       sb.append(water);
/*     */     } 
/*     */     
/* 263 */     sb.append("\nClient input:    ");
/* 264 */     sb.append(clientMovement);
/* 265 */     sb.append(" length: ");
/* 266 */     sb.append(clientMovement.length());
/* 267 */     sb.append("\nSimulated input: ");
/* 268 */     sb.append(simulatedMovement);
/* 269 */     sb.append(" length: ");
/* 270 */     sb.append(simulatedMovement.length());
/*     */ 
/*     */     
/* 273 */     sb.append("\n\n");
/*     */   }
/*     */   
/*     */   private Vector3dm getPlayerMathMovement(GrimPlayer player, Vector3dm wantedMovement, float f2) {
/* 277 */     float f3 = player.trigHandler.sin(f2 * 0.017453292F);
/* 278 */     float f4 = player.trigHandler.cos(f2 * 0.017453292F);
/*     */     
/* 280 */     float bestTheoreticalX = (float)(f3 * wantedMovement.getZ() + f4 * wantedMovement.getX()) / (f3 * f3 + f4 * f4);
/* 281 */     float bestTheoreticalZ = (float)(-f3 * wantedMovement.getX() + f4 * wantedMovement.getZ()) / (f3 * f3 + f4 * f4);
/*     */     
/* 283 */     return new Vector3dm(bestTheoreticalX, 0.0F, bestTheoreticalZ);
/*     */   } private static final class Location { double x; @Generated
/*     */     public Location(double x, double y, double z, float xRot, float yRot, String world) {
/* 286 */       this.x = x; this.y = y; this.z = z; this.xRot = xRot; this.yRot = yRot; this.world = world;
/*     */     }
/*     */     double y; double z;
/*     */     float xRot;
/*     */     float yRot;
/*     */     String world;
/*     */     
/*     */     public String toString() {
/* 294 */       return "x: " + this.x + " y: " + this.y + " z: " + this.z + " xRot: " + this.xRot + " yRot: " + this.yRot + " world: " + this.world;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\SuperDebug.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
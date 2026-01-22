/*     */ package ac.grim.grimac.predictionengine.predictions;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.SneakingEstimator;
/*     */ import ac.grim.grimac.predictionengine.movementtick.MovementTickerPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.KnownInput;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vec2;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.math.VectorUtils;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.JumpPower;
/*     */ import ac.grim.grimac.utils.nmsutil.Riptide;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ public class PredictionEngine
/*     */ {
/*     */   public static Vector3dm clampMovementToHardBorder(GrimPlayer player, Vector3dm outputVel) {
/*  31 */     return outputVel;
/*     */   }
/*     */   public static Vector3dm transformInputsToVector(GrimPlayer player, Vector3dm theoreticalInput) {
/*     */     float bestPossibleX, bestPossibleZ;
/*  35 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5)) {
/*  36 */       Vec2 moveVector = (new Vec2((float)theoreticalInput.getX(), (float)theoreticalInput.getZ())).normalized();
/*  37 */       Vec2 input = modifyInput(player, moveVector);
/*  38 */       return new Vector3dm(input.x(), 0.0F, input.y());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  44 */     if (player.isSlowMovement) {
/*  45 */       bestPossibleX = (float)(theoreticalInput.getX() * player.sneakingSpeedMultiplier);
/*  46 */       bestPossibleZ = (float)(theoreticalInput.getZ() * player.sneakingSpeedMultiplier);
/*     */     } else {
/*  48 */       bestPossibleX = Math.min(Math.max(-1.0F, (float)Math.round(theoreticalInput.getX())), 1.0F);
/*  49 */       bestPossibleZ = Math.min(Math.max(-1.0F, (float)Math.round(theoreticalInput.getZ())), 1.0F);
/*     */     } 
/*     */     
/*  52 */     if (player.packetStateData.isSlowedByUsingItem()) {
/*  53 */       bestPossibleX *= 0.2F;
/*  54 */       bestPossibleZ *= 0.2F;
/*     */     } 
/*     */     
/*  57 */     Vector3dm inputVector = new Vector3dm(bestPossibleX, 0.0F, bestPossibleZ);
/*  58 */     inputVector.multiply(0.98F);
/*     */ 
/*     */     
/*  61 */     inputVector = new Vector3dm((float)inputVector.getX(), (float)inputVector.getY(), (float)inputVector.getZ());
/*     */     
/*  63 */     if (inputVector.lengthSquared() > 1.0D) {
/*  64 */       double d0 = Math.sqrt(inputVector.getX() * inputVector.getX() + inputVector.getY() * inputVector.getY() + inputVector.getZ() * inputVector.getZ());
/*  65 */       inputVector = new Vector3dm(inputVector.getX() / d0, inputVector.getY() / d0, inputVector.getZ() / d0);
/*     */     } 
/*     */     
/*  68 */     return inputVector;
/*     */   }
/*     */   
/*     */   public static Vec2 modifyInput(GrimPlayer player, Vec2 moveVector) {
/*  72 */     if (moveVector.lengthSquared() == 0.0F) {
/*  73 */       return moveVector;
/*     */     }
/*  75 */     Vec2 input = moveVector.scale(0.98F);
/*  76 */     if (player.packetStateData.isSlowedByUsingItem() && !player.inVehicle()) {
/*  77 */       input = input.scale(0.2F);
/*     */     }
/*     */     
/*  80 */     if (player.isSlowMovement) {
/*  81 */       input = input.scale(player.sneakingSpeedMultiplier);
/*     */     }
/*     */     
/*  84 */     return modifyInputSpeedForSquareMovement(input);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Vec2 modifyInputSpeedForSquareMovement(Vec2 input) {
/*  89 */     float length = input.length();
/*  90 */     if (length <= 0.0F) {
/*  91 */       return input;
/*     */     }
/*  93 */     Vec2 multiplied = input.scale(1.0F / length);
/*  94 */     float distance = distanceToUnitSquare(multiplied);
/*  95 */     float min = Math.min(length * distance, 1.0F);
/*  96 */     return multiplied.scale(min);
/*     */   }
/*     */ 
/*     */   
/*     */   private static float distanceToUnitSquare(Vec2 input) {
/* 101 */     float x = Math.abs(input.x());
/* 102 */     float z = Math.abs(input.y());
/* 103 */     float additional = (z > x) ? (x / z) : (z / x);
/* 104 */     return GrimMath.sqrt(1.0F + GrimMath.square(additional));
/*     */   }
/*     */   
/*     */   public void guessBestMovement(float speed, GrimPlayer player) {
/* 108 */     Set<VectorData> init = fetchPossibleStartTickVectors(player);
/*     */     
/* 110 */     if (player.uncertaintyHandler.influencedByBouncyBlock()) {
/* 111 */       for (VectorData data : init) {
/*     */         
/* 113 */         Vector3dm toZeroVec = (new PredictionEngine()).handleStartingVelocityUncertainty(player, data, new Vector3dm(0, -1000000000, 0));
/*     */         
/* 115 */         player.uncertaintyHandler.nextTickSlimeBlockUncertainty = Math.max(Math.abs(toZeroVec.getY()), player.uncertaintyHandler.nextTickSlimeBlockUncertainty);
/*     */       } 
/*     */     }
/*     */     
/* 119 */     player.updateVelocityMovementSkipping();
/* 120 */     player.couldSkipTick = (player.couldSkipTick || player.pointThreeEstimator.determineCanSkipTick(speed, init));
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
/* 131 */     List<VectorData> possibleVelocities = applyInputsToVelocityPossibilities(player, init, speed);
/*     */ 
/*     */     
/* 134 */     if (player.couldSkipTick) {
/* 135 */       addZeroPointThreeToPossibilities(speed, player, possibleVelocities);
/*     */     }
/*     */ 
/*     */     
/* 139 */     doPredictions(player, possibleVelocities, speed);
/*     */ 
/*     */ 
/*     */     
/* 143 */     (new MovementTickerPlayer(player)).move(player.clientVelocity.clone(), player.predictedVelocity.vector);
/* 144 */     endOfTick(player, player.gravity);
/*     */   }
/*     */ 
/*     */   
/*     */   private void doPredictions(GrimPlayer player, List<VectorData> possibleVelocities, float speed) {
/* 149 */     possibleVelocities.sort((a, b) -> sortVectorData(a, b, player));
/*     */     
/* 151 */     ((SneakingEstimator)player.checkManager.getPostPredictionCheck(SneakingEstimator.class)).storePossibleVelocities(possibleVelocities);
/*     */     
/* 153 */     double bestInput = Double.MAX_VALUE;
/*     */     
/* 155 */     VectorData bestCollisionVel = null;
/* 156 */     Vector3dm beforeCollisionMovement = null;
/* 157 */     Vector3dm originalClientVel = player.clientVelocity.clone();
/*     */     
/* 159 */     SimpleCollisionBox originalBB = player.boundingBox;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 164 */     SimpleCollisionBox pointThreeThanksMojang = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) ? GetBoundingBox.getBoundingBoxFromPosAndSize(player, player.lastX, player.lastY, player.lastZ, 0.6F, 0.6F) : originalBB;
/*     */     
/* 166 */     player.skippedTickInActualMovement = false;
/*     */     
/* 168 */     for (VectorData clientVelAfterInput : possibleVelocities) {
/* 169 */       Vector3dm primaryPushMovement = handleStartingVelocityUncertainty(player, clientVelAfterInput, player.actualMovement);
/*     */       
/* 171 */       Vector3dm bestTheoreticalCollisionResult = VectorUtils.cutBoxToVector(player.actualMovement, (new SimpleCollisionBox(0.0D, Math.min(0.0D, primaryPushMovement.getY()), 0.0D, primaryPushMovement.getX(), Math.max(0.6D, primaryPushMovement.getY()), primaryPushMovement.getZ())).sort());
/*     */ 
/*     */       
/* 174 */       if (bestTheoreticalCollisionResult.distanceSquared(player.actualMovement) > bestInput && !clientVelAfterInput.isKnockback() && !clientVelAfterInput.isExplosion()) {
/*     */         continue;
/*     */       }
/*     */       
/* 178 */       if (clientVelAfterInput.isZeroPointZeroThree()) {
/* 179 */         player.boundingBox = pointThreeThanksMojang;
/*     */       } else {
/* 181 */         player.boundingBox = originalBB;
/*     */       } 
/*     */ 
/*     */       
/* 185 */       Pair<Vector3dm, Vector3dm> output = doSeekingWallCollisions(player, primaryPushMovement, originalClientVel, clientVelAfterInput);
/* 186 */       primaryPushMovement = (Vector3dm)output.first();
/* 187 */       Vector3dm outputVel = clampMovementToHardBorder(player, (Vector3dm)output.second());
/*     */       
/* 189 */       double resultAccuracy = outputVel.distanceSquared(player.actualMovement);
/*     */ 
/*     */       
/* 192 */       if (clientVelAfterInput.isZeroPointZeroThree() && resultAccuracy < 1.0E-6D) {
/* 193 */         player.skippedTickInActualMovement = true;
/*     */       }
/*     */       
/* 196 */       if (clientVelAfterInput.isKnockback()) {
/* 197 */         player.checkManager.getKnockbackHandler().handlePredictionAnalysis(Math.sqrt(player.uncertaintyHandler.reduceOffset(resultAccuracy)));
/*     */       }
/*     */       
/* 200 */       if (clientVelAfterInput.isExplosion()) {
/* 201 */         player.checkManager.getExplosionHandler().handlePredictionAnalysis(Math.sqrt(player.uncertaintyHandler.reduceOffset(resultAccuracy)));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 208 */       if ((clientVelAfterInput.isKnockback() || clientVelAfterInput.isExplosion()) && !clientVelAfterInput.isZeroPointZeroThree()) {
/* 209 */         boolean wasVelocityPointThree = player.pointThreeEstimator.determineCanSkipTick(speed, new HashSet(Collections.singletonList(clientVelAfterInput)));
/*     */         
/* 211 */         if (clientVelAfterInput.isKnockback()) {
/* 212 */           player.checkManager.getKnockbackHandler().setPointThree(wasVelocityPointThree);
/*     */         }
/* 214 */         if (clientVelAfterInput.isExplosion()) {
/* 215 */           player.checkManager.getExplosionHandler().setPointThree(wasVelocityPointThree);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 222 */       if (player.packetStateData.isSlowedByUsingItem() && !clientVelAfterInput.isFlipItem()) {
/* 223 */         player.checkManager.getNoSlow().handlePredictionAnalysis(Math.sqrt(player.uncertaintyHandler.reduceOffset(resultAccuracy)));
/*     */       }
/*     */       
/* 226 */       if (player.checkManager.getKnockbackHandler().shouldIgnoreForPrediction(clientVelAfterInput) || player.checkManager
/* 227 */         .getExplosionHandler().shouldIgnoreForPrediction(clientVelAfterInput)) {
/*     */         continue;
/*     */       }
/*     */       
/* 231 */       if (resultAccuracy < bestInput) {
/* 232 */         bestCollisionVel = clientVelAfterInput.returnNewModified(outputVel, VectorData.VectorType.BestVelPicked);
/* 233 */         bestCollisionVel.preUncertainty = clientVelAfterInput;
/* 234 */         beforeCollisionMovement = primaryPushMovement;
/*     */ 
/*     */         
/* 237 */         if (player.wouldCollisionResultFlagGroundSpoof(primaryPushMovement.getY(), bestCollisionVel.vector.getY())) {
/* 238 */           resultAccuracy += 1.0E-8D;
/*     */         }
/* 240 */         bestInput = resultAccuracy;
/*     */       } 
/*     */ 
/*     */       
/* 244 */       if (bestInput < 1.0000000000000002E-10D && !player.checkManager.getKnockbackHandler().wouldFlag() && !player.checkManager.getExplosionHandler().wouldFlag()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 249 */     assert beforeCollisionMovement != null;
/*     */     
/* 251 */     player.clientVelocity = beforeCollisionMovement.clone();
/* 252 */     player.predictedVelocity = bestCollisionVel;
/* 253 */     player.boundingBox = originalBB;
/*     */ 
/*     */     
/* 256 */     if (player.predictedVelocity.isZeroPointZeroThree()) {
/* 257 */       player.skippedTickInActualMovement = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private Pair<Vector3dm, Vector3dm> doSeekingWallCollisions(GrimPlayer player, Vector3dm primaryPushMovement, Vector3dm originalClientVel, VectorData clientVelAfterInput) {
/* 262 */     boolean vehicleKB = (player.inVehicle() && clientVelAfterInput.isKnockback() && clientVelAfterInput.vector.getY() == 0.0D);
/*     */     
/* 264 */     double xAdditional = Math.signum(primaryPushMovement.getX()) * 1.0E-7D;
/*     */ 
/*     */     
/* 267 */     double yAdditional = vehicleKB ? 0.0D : (((primaryPushMovement.getY() > 0.0D) ? true : -1) * 1.0E-7D);
/* 268 */     double zAdditional = Math.signum(primaryPushMovement.getZ()) * 1.0E-7D;
/*     */ 
/*     */     
/* 271 */     double testX = primaryPushMovement.getX() + xAdditional;
/* 272 */     double testY = primaryPushMovement.getY() + yAdditional;
/* 273 */     double testZ = primaryPushMovement.getZ() + zAdditional;
/* 274 */     primaryPushMovement = new Vector3dm(testX, testY, testZ);
/*     */     
/* 276 */     Vector3dm outputVel = Collisions.collide(player, primaryPushMovement.getX(), primaryPushMovement.getY(), primaryPushMovement.getZ(), originalClientVel.getY(), clientVelAfterInput);
/*     */     
/* 278 */     if (testX == outputVel.getX()) {
/* 279 */       primaryPushMovement.setX(primaryPushMovement.getX() - xAdditional);
/* 280 */       outputVel.setX(outputVel.getX() - xAdditional);
/*     */     } 
/*     */     
/* 283 */     if (testY == outputVel.getY()) {
/* 284 */       primaryPushMovement.setY(primaryPushMovement.getY() - yAdditional);
/* 285 */       outputVel.setY(outputVel.getY() - yAdditional);
/*     */     } 
/*     */     
/* 288 */     if (testZ == outputVel.getZ()) {
/* 289 */       primaryPushMovement.setZ(primaryPushMovement.getZ() - zAdditional);
/* 290 */       outputVel.setZ(outputVel.getZ() - zAdditional);
/*     */     } 
/*     */     
/* 293 */     return new Pair(primaryPushMovement, outputVel);
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
/*     */   private void addZeroPointThreeToPossibilities(float speed, GrimPlayer player, List<VectorData> possibleVelocities) {
/*     */     // Byte code:
/*     */     //   0: new java/util/HashSet
/*     */     //   3: dup
/*     */     //   4: invokespecial <init> : ()V
/*     */     //   7: astore #4
/*     */     //   9: new ac/grim/grimac/utils/math/Vector3dm
/*     */     //   12: dup
/*     */     //   13: invokespecial <init> : ()V
/*     */     //   16: astore #5
/*     */     //   18: aload_2
/*     */     //   19: getfield pointThreeEstimator : Lac/grim/grimac/predictionengine/PointThreeEstimator;
/*     */     //   22: invokevirtual controlsVerticalMovement : ()Z
/*     */     //   25: ifne -> 44
/*     */     //   28: aload #5
/*     */     //   30: aload_2
/*     */     //   31: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   34: invokevirtual getY : ()D
/*     */     //   37: invokevirtual setY : (D)Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   40: pop
/*     */     //   41: goto -> 78
/*     */     //   44: aload #4
/*     */     //   46: new ac/grim/grimac/utils/data/VectorData
/*     */     //   49: dup
/*     */     //   50: new ac/grim/grimac/utils/math/Vector3dm
/*     */     //   53: dup
/*     */     //   54: dconst_0
/*     */     //   55: aload_2
/*     */     //   56: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   59: invokevirtual getY : ()D
/*     */     //   62: dconst_0
/*     */     //   63: invokespecial <init> : (DDD)V
/*     */     //   66: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.ZeroPointZeroThree : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   69: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   72: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   77: pop
/*     */     //   78: aload #4
/*     */     //   80: new ac/grim/grimac/utils/data/VectorData
/*     */     //   83: dup
/*     */     //   84: aload #5
/*     */     //   86: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.ZeroPointZeroThree : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   89: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   92: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   97: pop
/*     */     //   98: aload_2
/*     */     //   99: getfield pointThreeEstimator : Lac/grim/grimac/predictionengine/PointThreeEstimator;
/*     */     //   102: getfield isNearFluid : Z
/*     */     //   105: ifeq -> 169
/*     */     //   108: aload_2
/*     */     //   109: aload_2
/*     */     //   110: getfield boundingBox : Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   113: invokevirtual copy : ()Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   116: ldc2_w 0.4
/*     */     //   119: dconst_0
/*     */     //   120: ldc2_w 0.4
/*     */     //   123: invokevirtual expand : (DDD)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   126: invokestatic isEmpty : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;)Z
/*     */     //   129: ifne -> 169
/*     */     //   132: aload_2
/*     */     //   133: getfield onGround : Z
/*     */     //   136: ifne -> 169
/*     */     //   139: aload #4
/*     */     //   141: new ac/grim/grimac/utils/data/VectorData
/*     */     //   144: dup
/*     */     //   145: new ac/grim/grimac/utils/math/Vector3dm
/*     */     //   148: dup
/*     */     //   149: dconst_0
/*     */     //   150: ldc2_w 0.3
/*     */     //   153: dconst_0
/*     */     //   154: invokespecial <init> : (DDD)V
/*     */     //   157: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.ZeroPointZeroThree : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   160: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   163: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   168: pop
/*     */     //   169: aload_2
/*     */     //   170: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   173: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_13 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   176: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   179: ifeq -> 197
/*     */     //   182: aload_2
/*     */     //   183: getfield isSwimming : Z
/*     */     //   186: ifeq -> 197
/*     */     //   189: aload_2
/*     */     //   190: aload #4
/*     */     //   192: invokestatic transformSwimmingVectors : (Lac/grim/grimac/player/GrimPlayer;Ljava/util/Set;)Ljava/util/Set;
/*     */     //   195: astore #4
/*     */     //   197: aload_2
/*     */     //   198: getfield pointThreeEstimator : Lac/grim/grimac/predictionengine/PointThreeEstimator;
/*     */     //   201: invokevirtual isNearClimbable : ()Z
/*     */     //   204: ifeq -> 304
/*     */     //   207: aload_2
/*     */     //   208: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   211: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_14 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   214: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   217: ifne -> 264
/*     */     //   220: aload_2
/*     */     //   221: aload_2
/*     */     //   222: getfield boundingBox : Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   225: invokevirtual copy : ()Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   228: aload_2
/*     */     //   229: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   232: invokevirtual getX : ()D
/*     */     //   235: dconst_0
/*     */     //   236: aload_2
/*     */     //   237: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   240: invokevirtual getZ : ()D
/*     */     //   243: invokevirtual expand : (DDD)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   246: ldc2_w 0.5
/*     */     //   249: ldc2_w -1.0E-7
/*     */     //   252: ldc2_w 0.5
/*     */     //   255: invokevirtual expand : (DDD)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   258: invokestatic isEmpty : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;)Z
/*     */     //   261: ifne -> 304
/*     */     //   264: new ac/grim/grimac/utils/math/Vector3dm
/*     */     //   267: dup
/*     */     //   268: dconst_0
/*     */     //   269: ldc2_w 0.2
/*     */     //   272: dconst_0
/*     */     //   273: invokespecial <init> : (DDD)V
/*     */     //   276: astore #6
/*     */     //   278: aload_2
/*     */     //   279: aload #6
/*     */     //   281: invokestatic staticVectorEndOfTick : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/math/Vector3dm;)V
/*     */     //   284: aload #4
/*     */     //   286: new ac/grim/grimac/utils/data/VectorData
/*     */     //   289: dup
/*     */     //   290: aload #6
/*     */     //   292: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.ZeroPointZeroThree : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   295: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   298: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   303: pop
/*     */     //   304: aload_0
/*     */     //   305: aload_2
/*     */     //   306: aload #4
/*     */     //   308: invokevirtual addJumpsToPossibilities : (Lac/grim/grimac/player/GrimPlayer;Ljava/util/Set;)V
/*     */     //   311: aload_0
/*     */     //   312: aload_2
/*     */     //   313: aload #4
/*     */     //   315: invokevirtual addExplosionToPossibilities : (Lac/grim/grimac/player/GrimPlayer;Ljava/util/Set;)V
/*     */     //   318: aload_2
/*     */     //   319: getfield packetStateData : Lac/grim/grimac/utils/data/PacketStateData;
/*     */     //   322: getfield tryingToRiptide : Z
/*     */     //   325: ifeq -> 381
/*     */     //   328: aload_2
/*     */     //   329: invokestatic getRiptideVelocity : (Lac/grim/grimac/player/GrimPlayer;)Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   332: astore #6
/*     */     //   334: aload #4
/*     */     //   336: new ac/grim/grimac/utils/data/VectorData
/*     */     //   339: dup
/*     */     //   340: aload_2
/*     */     //   341: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   344: invokevirtual clone : ()Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   347: aload #6
/*     */     //   349: invokevirtual add : (Lac/grim/grimac/utils/math/Vector3dm;)Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   352: new ac/grim/grimac/utils/data/VectorData
/*     */     //   355: dup
/*     */     //   356: new ac/grim/grimac/utils/math/Vector3dm
/*     */     //   359: dup
/*     */     //   360: invokespecial <init> : ()V
/*     */     //   363: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.ZeroPointZeroThree : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   366: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   369: getstatic ac/grim/grimac/utils/data/VectorData$VectorType.Trident : Lac/grim/grimac/utils/data/VectorData$VectorType;
/*     */     //   372: invokespecial <init> : (Lac/grim/grimac/utils/math/Vector3dm;Lac/grim/grimac/utils/data/VectorData;Lac/grim/grimac/utils/data/VectorData$VectorType;)V
/*     */     //   375: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   380: pop
/*     */     //   381: aload_3
/*     */     //   382: aload_0
/*     */     //   383: aload_2
/*     */     //   384: aload #4
/*     */     //   386: fload_1
/*     */     //   387: invokevirtual applyInputsToVelocityPossibilities : (Lac/grim/grimac/player/GrimPlayer;Ljava/util/Set;F)Ljava/util/List;
/*     */     //   390: invokeinterface addAll : (Ljava/util/Collection;)Z
/*     */     //   395: pop
/*     */     //   396: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #298	-> 0
/*     */     //   #302	-> 9
/*     */     //   #305	-> 18
/*     */     //   #306	-> 28
/*     */     //   #308	-> 44
/*     */     //   #311	-> 78
/*     */     //   #314	-> 98
/*     */     //   #315	-> 139
/*     */     //   #319	-> 169
/*     */     //   #320	-> 189
/*     */     //   #328	-> 197
/*     */     //   #329	-> 232
/*     */     //   #328	-> 243
/*     */     //   #329	-> 255
/*     */     //   #328	-> 258
/*     */     //   #332	-> 264
/*     */     //   #333	-> 278
/*     */     //   #335	-> 284
/*     */     //   #339	-> 304
/*     */     //   #340	-> 311
/*     */     //   #342	-> 318
/*     */     //   #343	-> 328
/*     */     //   #344	-> 334
/*     */     //   #347	-> 381
/*     */     //   #348	-> 396
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   278	26	6	hackyClimbVector	Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   334	47	6	riptideAddition	Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   0	397	0	this	Lac/grim/grimac/predictionengine/predictions/PredictionEngine;
/*     */     //   0	397	1	speed	F
/*     */     //   0	397	2	player	Lac/grim/grimac/player/GrimPlayer;
/*     */     //   0	397	3	possibleVelocities	Ljava/util/List;
/*     */     //   9	388	4	pointThreePossibilities	Ljava/util/Set;
/*     */     //   18	379	5	pointThreeVector	Lac/grim/grimac/utils/math/Vector3dm;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	397	3	possibleVelocities	Ljava/util/List<Lac/grim/grimac/utils/data/VectorData;>;
/*     */     //   9	388	4	pointThreePossibilities	Ljava/util/Set<Lac/grim/grimac/utils/data/VectorData;>;
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
/*     */   public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
/* 351 */     List<VectorData> returnVectors = new ArrayList<>();
/* 352 */     loopVectors(player, possibleVectors, speed, returnVectors);
/* 353 */     return returnVectors;
/*     */   }
/*     */   
/*     */   public void addFluidPushingToStartingVectors(GrimPlayer player, Set<VectorData> data) {
/* 357 */     for (VectorData vectorData : data) {
/*     */       
/* 359 */       if (vectorData.isKnockback() && player.baseTickAddition.lengthSquared() != 0.0D) {
/* 360 */         vectorData.vector = vectorData.vector.add(player.baseTickAddition);
/*     */       }
/*     */       
/* 363 */       if (vectorData.isKnockback() && player.baseTickWaterPushing.lengthSquared() != 0.0D) {
/* 364 */         if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 365 */           Vector3dm vec3 = player.baseTickWaterPushing.clone();
/* 366 */           if (Math.abs(vectorData.vector.getX()) < 0.003D && Math.abs(vectorData.vector.getZ()) < 0.003D && player.baseTickWaterPushing.length() < 0.0045000000000000005D) {
/* 367 */             vec3 = vec3.normalize().multiply(0.0045000000000000005D);
/*     */           }
/*     */           
/* 370 */           vectorData.vector = vectorData.vector.add(vec3); continue;
/*     */         } 
/* 372 */         vectorData.vector = vectorData.vector.add(player.baseTickWaterPushing);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<VectorData> fetchPossibleStartTickVectors(GrimPlayer player) {
/* 380 */     Set<VectorData> velocities = player.getPossibleVelocities();
/*     */     
/* 382 */     addExplosionToPossibilities(player, velocities);
/*     */     
/* 384 */     if (player.packetStateData.tryingToRiptide) {
/* 385 */       Vector3dm riptideAddition = Riptide.getRiptideVelocity(player);
/* 386 */       velocities.add(new VectorData(player.clientVelocity.clone().add(riptideAddition), VectorData.VectorType.Trident));
/*     */     } 
/*     */ 
/*     */     
/* 390 */     addFluidPushingToStartingVectors(player, velocities);
/*     */     
/* 392 */     addAttackSlowToPossibilities(player, velocities);
/*     */     
/* 394 */     addNonEffectiveAI(player, velocities);
/*     */     
/* 396 */     applyMovementThreshold(player, velocities);
/*     */     
/* 398 */     addJumpsToPossibilities(player, velocities);
/*     */     
/* 400 */     return velocities;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addNonEffectiveAI(GrimPlayer player, Set<VectorData> data) {
/* 405 */     if (!player.inVehicle() || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5))
/*     */       return; 
/* 407 */     for (VectorData vectorData : data) {
/* 408 */       vectorData.vector = vectorData.vector.clone().multiply(0.98D);
/*     */     }
/*     */   }
/*     */   
/*     */   private void addAttackSlowToPossibilities(GrimPlayer player, Set<VectorData> velocities) {
/* 413 */     for (int x = 1; x <= Math.min(player.maxAttackSlow, 5); x++) {
/* 414 */       for (VectorData data : new HashSet(velocities)) {
/* 415 */         if (player.minAttackSlow > 0) {
/* 416 */           data.vector.setX(data.vector.getX() * 0.6D);
/* 417 */           data.vector.setZ(data.vector.getZ() * 0.6D);
/* 418 */           data.addVectorType(VectorData.VectorType.AttackSlow); continue;
/*     */         } 
/* 420 */         velocities.add(data.returnNewModified(data.vector.clone().multiply(new Vector3dm(0.6D, 1.0D, 0.6D)), VectorData.VectorType.AttackSlow));
/*     */       } 
/*     */ 
/*     */       
/* 424 */       if (player.minAttackSlow > 0) {
/* 425 */         player.minAttackSlow--;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {}
/*     */ 
/*     */   
/*     */   public void applyMovementThreshold(GrimPlayer player, Set<VectorData> velocities) {
/* 435 */     double minimumMovement = 0.003D;
/* 436 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/* 437 */       minimumMovement = 0.005D;
/*     */     }
/*     */     
/* 440 */     for (VectorData vector : velocities) {
/* 441 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5) && !player.inVehicle()) {
/* 442 */         if (Collisions.getHorizontalDistanceSqr(vector.vector) < 9.0E-6D) {
/* 443 */           vector.vector.setX(0.0D);
/* 444 */           vector.vector.setZ(0.0D);
/*     */         } 
/*     */       } else {
/* 447 */         if (Math.abs(vector.vector.getX()) < minimumMovement) {
/* 448 */           vector.vector.setX(0.0D);
/*     */         }
/*     */         
/* 451 */         if (Math.abs(vector.vector.getZ()) < minimumMovement) {
/* 452 */           vector.vector.setZ(0.0D);
/*     */         }
/*     */       } 
/*     */       
/* 456 */       if (Math.abs(vector.vector.getY()) < minimumMovement) {
/* 457 */         vector.vector.setY(0.0D);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExplosionToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/* 463 */     for (VectorData vector : new HashSet(existingVelocities)) {
/* 464 */       if (player.likelyExplosions != null) {
/* 465 */         existingVelocities.add(new VectorData(vector.vector.clone().add(player.likelyExplosions.vector), vector, VectorData.VectorType.Explosion));
/*     */       }
/*     */       
/* 468 */       if (player.firstBreadExplosion != null) {
/* 469 */         existingVelocities.add((new VectorData(vector.vector.clone().add(player.firstBreadExplosion.vector), vector, VectorData.VectorType.Explosion))
/* 470 */             .returnNewModified(vector.vector.clone().add(player.firstBreadExplosion.vector), VectorData.VectorType.FirstBreadExplosion));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public int sortVectorData(VectorData a, VectorData b, GrimPlayer player) {
/* 476 */     int aScore = 0;
/* 477 */     int bScore = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 486 */     if (a.isExplosion()) {
/* 487 */       aScore -= 5;
/*     */     }
/* 489 */     if (a.isKnockback()) {
/* 490 */       aScore -= 5;
/*     */     }
/* 492 */     if (b.isExplosion()) {
/* 493 */       bScore -= 5;
/*     */     }
/* 495 */     if (b.isKnockback()) {
/* 496 */       bScore -= 5;
/*     */     }
/* 498 */     if (a.isFirstBreadExplosion()) {
/* 499 */       aScore++;
/*     */     }
/* 501 */     if (b.isFirstBreadExplosion()) {
/* 502 */       bScore++;
/*     */     }
/* 504 */     if (a.isFirstBreadKb()) {
/* 505 */       aScore++;
/*     */     }
/* 507 */     if (b.isFirstBreadKb()) {
/* 508 */       bScore++;
/*     */     }
/* 510 */     if (a.isFlipItem()) {
/* 511 */       aScore += 3;
/*     */     }
/* 513 */     if (b.isFlipItem()) {
/* 514 */       bScore += 3;
/*     */     }
/* 516 */     if (a.isZeroPointZeroThree()) {
/* 517 */       aScore--;
/*     */     }
/* 519 */     if (b.isZeroPointZeroThree()) {
/* 520 */       bScore--;
/*     */     }
/*     */     
/* 523 */     if (player.inVehicle() ? player.clientControlledVerticalCollision : player.onGround) if (a.vector.getY() >= 0.0D) {
/* 524 */         aScore += 2;
/*     */       } 
/* 526 */     if (player.inVehicle() ? player.clientControlledVerticalCollision : player.onGround) if (b.vector.getY() >= 0.0D) {
/* 527 */         bScore += 2;
/*     */       } 
/* 529 */     if (aScore != bScore) {
/* 530 */       return Integer.compare(aScore, bScore);
/*     */     }
/* 532 */     return Double.compare(a.vector.distanceSquared(player.actualMovement), b.vector.distanceSquared(player.actualMovement));
/*     */   }
/*     */   
/*     */   public Vector3dm handleStartingVelocityUncertainty(GrimPlayer player, VectorData vector, Vector3dm targetVec) {
/* 536 */     double avgColliding = ((Integer)Collections.<Integer>max((Collection<? extends Integer>)player.uncertaintyHandler.collidingEntities)).intValue();
/*     */     
/* 538 */     double additionHorizontal = player.uncertaintyHandler.getOffsetHorizontal(vector);
/* 539 */     double additionVertical = player.uncertaintyHandler.getVerticalOffset(vector);
/*     */     
/* 541 */     double pistonX = ((Double)Collections.<Double>max((Collection<? extends Double>)player.uncertaintyHandler.pistonX)).doubleValue();
/* 542 */     double pistonY = ((Double)Collections.<Double>max((Collection<? extends Double>)player.uncertaintyHandler.pistonY)).doubleValue();
/* 543 */     double pistonZ = ((Double)Collections.<Double>max((Collection<? extends Double>)player.uncertaintyHandler.pistonZ)).doubleValue();
/*     */     
/* 545 */     additionHorizontal += player.uncertaintyHandler.lastHorizontalOffset;
/* 546 */     additionVertical += player.uncertaintyHandler.lastVerticalOffset;
/*     */     
/* 548 */     VectorData originalVec = vector;
/* 549 */     while (originalVec.lastVector != null) {
/* 550 */       originalVec = originalVec.lastVector;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 556 */     double bonusY = 0.0D;
/* 557 */     if (player.uncertaintyHandler.lastFlyingStatusChange.hasOccurredSince(4)) {
/* 558 */       additionHorizontal += 0.3D;
/* 559 */       bonusY += 0.3D;
/*     */     } 
/*     */     
/* 562 */     if (player.uncertaintyHandler.lastUnderwaterFlyingHack.hasOccurredSince(9)) {
/* 563 */       bonusY += 0.2D;
/*     */     }
/*     */     
/* 566 */     if (player.uncertaintyHandler.lastHardCollidingLerpingEntity.hasOccurredSince(2)) {
/* 567 */       additionHorizontal += 0.1D;
/* 568 */       bonusY += 0.1D;
/*     */     } 
/*     */     
/* 571 */     if (pistonX != 0.0D || pistonY != 0.0D || pistonZ != 0.0D) {
/* 572 */       additionHorizontal += 0.1D;
/* 573 */       bonusY += 0.1D;
/*     */     } 
/*     */ 
/*     */     
/* 577 */     double horizontalFluid = player.pointThreeEstimator.getHorizontalFluidPushingUncertainty(vector);
/* 578 */     additionHorizontal += horizontalFluid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 586 */     Vector3dm uncertainty = new Vector3dm(avgColliding * 0.08D, additionVertical, avgColliding * 0.08D);
/*     */     
/* 588 */     Vector3dm min = new Vector3dm(player.uncertaintyHandler.xNegativeUncertainty - additionHorizontal, -bonusY + player.uncertaintyHandler.yNegativeUncertainty, player.uncertaintyHandler.zNegativeUncertainty - additionHorizontal);
/* 589 */     Vector3dm max = new Vector3dm(player.uncertaintyHandler.xPositiveUncertainty + additionHorizontal, bonusY + player.uncertaintyHandler.yPositiveUncertainty, player.uncertaintyHandler.zPositiveUncertainty + additionHorizontal);
/*     */     
/* 591 */     Vector3dm minVector = vector.vector.clone().add(min.subtract(uncertainty));
/* 592 */     Vector3dm maxVector = vector.vector.clone().add(max.add(uncertainty));
/*     */ 
/*     */     
/* 595 */     if (player.uncertaintyHandler.onGroundUncertain && vector.vector.getY() < 0.0D) {
/* 596 */       maxVector.setY(0);
/*     */     }
/*     */ 
/*     */     
/* 600 */     double gravityOffset = player.pointThreeEstimator.getAdditionalVerticalUncertainty(vector);
/* 601 */     if (gravityOffset > 0.0D) {
/* 602 */       maxVector.setY(maxVector.getY() + gravityOffset);
/*     */     } else {
/* 604 */       minVector.setY(minVector.getY() + gravityOffset);
/*     */     } 
/*     */ 
/*     */     
/* 608 */     double verticalFluid = player.pointThreeEstimator.getVerticalFluidPushingUncertainty(vector);
/* 609 */     minVector.setY(minVector.getY() - verticalFluid);
/*     */ 
/*     */     
/* 612 */     double bubbleFluid = player.pointThreeEstimator.getVerticalBubbleUncertainty(vector);
/* 613 */     maxVector.setY(maxVector.getY() + bubbleFluid);
/* 614 */     minVector.setY(minVector.getY() - bubbleFluid);
/*     */ 
/*     */ 
/*     */     
/* 618 */     if (!player.pointThreeEstimator.canPredictNextVerticalMovement()) {
/* 619 */       minVector.setY(minVector.getY() - player.compensatedEntities.self.getAttributeValue(Attributes.GRAVITY));
/*     */     }
/*     */ 
/*     */     
/* 623 */     if (player.actualMovement.getY() >= 0.0D && player.uncertaintyHandler.influencedByBouncyBlock() && 
/* 624 */       player.uncertaintyHandler.thisTickSlimeBlockUncertainty != 0.0D && !vector.isJump()) {
/* 625 */       if (player.uncertaintyHandler.thisTickSlimeBlockUncertainty > maxVector.getY()) {
/* 626 */         maxVector.setY(player.uncertaintyHandler.thisTickSlimeBlockUncertainty);
/*     */       }
/* 628 */       if (minVector.getY() > 0.0D) minVector.setY(0);
/*     */     
/*     */     } 
/*     */     
/* 632 */     if (vector.isZeroPointZeroThree() && vector.isSwimHop()) {
/* 633 */       minVector.setY(minVector.getY() - 0.06D);
/*     */     }
/*     */     
/* 636 */     SimpleCollisionBox box = new SimpleCollisionBox(minVector, maxVector);
/* 637 */     box.sort();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 642 */     double levitation = player.pointThreeEstimator.positiveLevitation(maxVector.getY());
/* 643 */     box.combineToMinimum(box.minX, levitation, box.minZ);
/* 644 */     levitation = player.pointThreeEstimator.positiveLevitation(minVector.getY());
/* 645 */     box.combineToMinimum(box.minX, levitation, box.minZ);
/* 646 */     levitation = player.pointThreeEstimator.negativeLevitation(maxVector.getY());
/* 647 */     box.combineToMinimum(box.minX, levitation, box.minZ);
/* 648 */     levitation = player.pointThreeEstimator.negativeLevitation(minVector.getY());
/* 649 */     box.combineToMinimum(box.minX, levitation, box.minZ);
/*     */ 
/*     */     
/* 652 */     SneakingEstimator sneaking = (SneakingEstimator)player.checkManager.getPostPredictionCheck(SneakingEstimator.class);
/* 653 */     box.minX += (sneaking.getSneakingPotentialHiddenVelocity()).minX;
/* 654 */     box.minZ += (sneaking.getSneakingPotentialHiddenVelocity()).minZ;
/* 655 */     box.maxX += (sneaking.getSneakingPotentialHiddenVelocity()).maxX;
/* 656 */     box.maxZ += (sneaking.getSneakingPotentialHiddenVelocity()).maxZ;
/*     */     
/* 658 */     if (player.uncertaintyHandler.fireworksBox != null) {
/* 659 */       double minXdiff = Math.min(0.0D, player.uncertaintyHandler.fireworksBox.minX - originalVec.vector.getX());
/* 660 */       double minYdiff = Math.min(0.0D, player.uncertaintyHandler.fireworksBox.minY - originalVec.vector.getY());
/* 661 */       double minZdiff = Math.min(0.0D, player.uncertaintyHandler.fireworksBox.minZ - originalVec.vector.getZ());
/* 662 */       double maxXdiff = Math.max(0.0D, player.uncertaintyHandler.fireworksBox.maxX - originalVec.vector.getX());
/* 663 */       double maxYdiff = Math.max(0.0D, player.uncertaintyHandler.fireworksBox.maxY - originalVec.vector.getY());
/* 664 */       double maxZdiff = Math.max(0.0D, player.uncertaintyHandler.fireworksBox.maxZ - originalVec.vector.getZ());
/*     */       
/* 666 */       box.expandMin(minXdiff, minYdiff, minZdiff);
/* 667 */       box.expandMax(maxXdiff, maxYdiff, maxZdiff);
/*     */     } 
/*     */     
/* 670 */     SimpleCollisionBox rod = player.uncertaintyHandler.fishingRodPullBox;
/* 671 */     if (rod != null) {
/* 672 */       box.expandMin(rod.minX, rod.minY, rod.minZ);
/* 673 */       box.expandMax(rod.maxX, rod.maxY, rod.maxZ);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 680 */     if (player.uncertaintyHandler.stuckOnEdge.hasOccurredSince(0) || player.uncertaintyHandler.isSteppingOnSlime)
/*     */     {
/* 682 */       box.expandToAbsoluteCoordinates(0.0D, box.maxY, 0.0D);
/*     */     }
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
/* 717 */     if (player.uncertaintyHandler.lastVehicleSwitch.hasOccurredSince(0) || player.uncertaintyHandler.lastHardCollidingLerpingEntity.hasOccurredSince(3) || (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && vector.vector.getY() > 0.0D && vector.isZeroPointZeroThree() && !Collisions.isEmpty(player, GetBoundingBox.getBoundingBoxFromPosAndSize(player, player.lastX, vector.vector.getY() + player.lastY + 0.6D, player.lastZ, 0.6F, 1.26F)))) {
/* 718 */       box.expandToAbsoluteCoordinates(0.0D, 0.0D, 0.0D);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 723 */     if (player.uncertaintyHandler.lastVehicleSwitch.hasOccurredSince(1)) {
/* 724 */       double trueFriction = player.lastOnGround ? (player.friction * 0.91D) : 0.91D;
/* 725 */       if (player.wasTouchingLava) trueFriction = 0.5D; 
/* 726 */       if (player.wasTouchingWater) trueFriction = 0.96D;
/*     */       
/* 728 */       double maxY = Math.max(box.maxY, box.maxY + (box.maxY - player.gravity) * 0.91D);
/* 729 */       double minY = Math.min(box.minY, box.minY + (box.minY - player.gravity) * 0.91D);
/* 730 */       double minX = Math.min(box.minX, box.minX + -player.speed * trueFriction);
/* 731 */       double minZ = Math.min(box.minZ, box.minZ + -player.speed * trueFriction);
/* 732 */       double maxX = Math.max(box.maxX, box.maxX + player.speed * trueFriction);
/* 733 */       double maxZ = Math.max(box.maxZ, box.maxZ + player.speed * trueFriction);
/*     */       
/* 735 */       box = new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
/* 736 */       box.expand(0.05D, 0.0D, 0.05D);
/*     */     } 
/*     */     
/* 739 */     if (player.uncertaintyHandler.lastVehicleSwitch.hasOccurredSince(10)) {
/* 740 */       box.expand(0.001D);
/*     */     }
/*     */     
/* 743 */     minVector = box.min();
/* 744 */     maxVector = box.max();
/*     */     
/* 746 */     if (pistonX != 0.0D) {
/* 747 */       minVector.setX(Math.min(minVector.getX() - pistonX, pistonX));
/* 748 */       maxVector.setX(Math.max(maxVector.getX() + pistonX, pistonX));
/*     */     } 
/* 750 */     if (pistonY != 0.0D) {
/* 751 */       minVector.setY(Math.min(minVector.getY() - pistonY, pistonY));
/* 752 */       maxVector.setY(Math.max(maxVector.getY() + pistonY, pistonY));
/*     */     } 
/* 754 */     if (pistonZ != 0.0D) {
/* 755 */       minVector.setZ(Math.min(minVector.getZ() - pistonZ, pistonZ));
/* 756 */       maxVector.setZ(Math.max(maxVector.getZ() + pistonZ, pistonZ));
/*     */     } 
/* 758 */     return VectorUtils.cutBoxToVector(targetVec, minVector, maxVector);
/*     */   }
/*     */   
/*     */   public void endOfTick(GrimPlayer player, double d) {
/* 762 */     player.canSwimHop = canSwimHop(player);
/* 763 */     player.lastWasClimbing = 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loopVectors(GrimPlayer player, Set<VectorData> possibleVectors, float speed, List<VectorData> returnVectors) {
/* 771 */     int forwardMin = (player.isSprinting && !player.isSwimming) ? 1 : -1;
/* 772 */     int forwardMax = 1;
/* 773 */     int strafeMin = -1;
/* 774 */     int strafeMax = 1;
/*     */ 
/*     */     
/* 777 */     if (player.supportsEndTick()) {
/* 778 */       forwardMin = forwardMax = strafeMin = strafeMax = 0;
/*     */       
/* 780 */       KnownInput knownInput = player.packetStateData.knownInput;
/* 781 */       if (knownInput.forward() || (player.isSprinting && !player.isSwimming)) {
/* 782 */         forwardMax++;
/* 783 */         forwardMin++;
/*     */       } 
/*     */       
/* 786 */       if (knownInput.backward() && (!player.isSprinting || player.isSwimming)) {
/* 787 */         forwardMax--;
/* 788 */         forwardMin--;
/*     */       } 
/*     */       
/* 791 */       if (knownInput.left()) {
/* 792 */         strafeMax++;
/* 793 */         strafeMin++;
/*     */       } 
/*     */       
/* 796 */       if (knownInput.right()) {
/* 797 */         strafeMax--;
/* 798 */         strafeMin--;
/*     */       } 
/*     */     } 
/*     */     
/* 802 */     for (int loopSlowed = 0; loopSlowed <= 1; loopSlowed++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 808 */       for (int loopUsingItem = 0; loopUsingItem <= 1; loopUsingItem++) {
/* 809 */         for (VectorData possibleLastTickOutput : possibleVectors) {
/*     */           
/* 811 */           if (loopSlowed == 1 && !possibleLastTickOutput.isZeroPointZeroThree() && player.isForceSlowMovement())
/*     */             continue; 
/* 813 */           for (int strafe = strafeMin; strafe <= strafeMax; strafe++) {
/* 814 */             for (int forward = forwardMin; forward <= forwardMax; forward++) {
/* 815 */               for (int applyStuckSpeed = 1; applyStuckSpeed >= 0 && (
/* 816 */                 applyStuckSpeed != 0 || !player.isForceStuckSpeed()); applyStuckSpeed--) {
/*     */                 
/* 818 */                 Vector3dm input = transformInputsToVector(player, new Vector3dm(strafe, 0, forward));
/*     */                 
/* 820 */                 VectorData result = new VectorData(possibleLastTickOutput.vector.clone().add(getMovementResultFromInput(player, input, speed, player.xRot)), possibleLastTickOutput, VectorData.VectorType.InputResult);
/*     */                 
/* 822 */                 result.input = input;
/* 823 */                 if (applyStuckSpeed != 0) {
/* 824 */                   result = result.returnNewModified(result.vector.clone().multiply(player.stuckSpeedMultiplier), VectorData.VectorType.StuckMultiplier);
/*     */                 }
/* 826 */                 result = result.returnNewModified(handleOnClimbable(result.vector.clone(), player), VectorData.VectorType.Climbable);
/*     */                 
/* 828 */                 if (loopUsingItem == 1)
/* 829 */                   result = result.returnNewModified(VectorData.VectorType.Flip_Use_Item); 
/* 830 */                 returnVectors.add(result);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 836 */         player.packetStateData.setSlowedByUsingItem(!player.packetStateData.isSlowedByUsingItem());
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 841 */       player.isSlowMovement = !player.isSlowMovement;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canSwimHop(GrimPlayer player) {
/* 847 */     if (player.inVehicle() && (player.compensatedEntities.self.getRiding()).isBoat) {
/* 848 */       return false;
/*     */     }
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
/* 873 */     SimpleCollisionBox oldBox = player.inVehicle() ? GetBoundingBox.getCollisionBoxForPlayer(player, player.lastX, player.lastY, player.lastZ) : GetBoundingBox.getBoundingBoxFromPosAndSize(player, player.lastX, player.lastY, player.lastZ, 0.6F, 1.8F);
/*     */     
/* 875 */     if (!player.compensatedWorld.containsLiquid(oldBox.expand(0.1D, 0.1D, 0.1D))) return false;
/*     */     
/* 877 */     SimpleCollisionBox oldBB = player.boundingBox;
/* 878 */     player.boundingBox = player.boundingBox.copy().expand(-player.getMovementThreshold(), 0.0D, -player.getMovementThreshold());
/*     */ 
/*     */     
/* 881 */     double pointThreeToGround = Collisions.collide(player, 0.0D, -player.getMovementThreshold(), 0.0D).getY() + 1.0E-7D;
/* 882 */     player.boundingBox = oldBB;
/*     */ 
/*     */     
/* 885 */     SimpleCollisionBox newBox = player.inVehicle() ? GetBoundingBox.getCollisionBoxForPlayer(player, player.x, player.y, player.z) : GetBoundingBox.getBoundingBoxFromPosAndSize(player, player.x, player.y, player.z, 0.6F, 1.8F);
/*     */     
/* 887 */     return (player.uncertaintyHandler.lastHardCollidingLerpingEntity.hasOccurredSince(3) || !Collisions.isEmpty(player, newBox.expand(player.clientVelocity.getX(), -1.0D * pointThreeToGround, player.clientVelocity.getZ()).expand(0.5D, 0.03D, 0.5D)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3dm getMovementResultFromInput(GrimPlayer player, Vector3dm inputVector, float f, float f2) {
/* 893 */     float f3 = player.trigHandler.sin(f2 * 0.017453292F);
/* 894 */     float f4 = player.trigHandler.cos(f2 * 0.017453292F);
/*     */     
/* 896 */     double xResult = inputVector.getX() * f4 - inputVector.getZ() * f3;
/* 897 */     double zResult = inputVector.getZ() * f4 + inputVector.getX() * f3;
/*     */     
/* 899 */     return new Vector3dm(xResult * f, 0.0D, zResult * f);
/*     */   }
/*     */   
/*     */   public Vector3dm handleOnClimbable(Vector3dm vector, GrimPlayer player) {
/* 903 */     return vector;
/*     */   }
/*     */   
/*     */   public void doJump(GrimPlayer player, Vector3dm vector) {
/* 907 */     if (!player.lastOnGround || player.onGround) {
/*     */       return;
/*     */     }
/* 910 */     JumpPower.jumpFromGround(player, vector);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngine.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
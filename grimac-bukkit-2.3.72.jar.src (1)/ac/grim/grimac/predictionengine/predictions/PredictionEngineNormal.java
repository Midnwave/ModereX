/*     */ package ac.grim.grimac.predictionengine.predictions;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.JumpPower;
/*     */ import java.util.HashSet;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PredictionEngineNormal
/*     */   extends PredictionEngine
/*     */ {
/*     */   public static void staticVectorEndOfTick(GrimPlayer player, Vector3dm vector) {
/*  23 */     double adjustedY = vector.getY();
/*  24 */     OptionalInt levitation = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.LEVITATION);
/*  25 */     if (levitation.isPresent()) {
/*  26 */       adjustedY += (0.05D * (levitation.getAsInt() + 1) - vector.getY()) * 0.2D;
/*     */       
/*  28 */       player.fallDistance = 0.0D;
/*  29 */     } else if (player.hasGravity) {
/*  30 */       adjustedY -= player.gravity;
/*     */     } 
/*     */     
/*  33 */     vector.setX(vector.getX() * player.friction);
/*  34 */     vector.setY(adjustedY * 0.9800000190734863D);
/*  35 */     vector.setZ(vector.getZ() * player.friction);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
/*  40 */     if (player.supportsEndTick() && !player.packetStateData.knownInput.jump()) {
/*     */       return;
/*     */     }
/*     */     
/*  44 */     for (VectorData vector : new HashSet(existingVelocities)) {
/*  45 */       Vector3dm jump = vector.vector.clone();
/*     */       
/*  47 */       if (!player.isFlying) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  53 */         OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.JUMP_BOOST);
/*  54 */         if (((jumpBoost.isEmpty() || jumpBoost.getAsInt() >= 0) && player.onGround) || !player.lastOnGround) {
/*     */           return;
/*     */         }
/*  57 */         JumpPower.jumpFromGround(player, jump);
/*     */       } else {
/*  59 */         jump.add(new Vector3dm(0.0F, player.flySpeed * 3.0F, 0.0F));
/*  60 */         if (!player.wasFlying) {
/*  61 */           Vector3dm edgeCaseJump = jump.clone();
/*  62 */           JumpPower.jumpFromGround(player, edgeCaseJump);
/*  63 */           existingVelocities.add(vector.returnNewModified(edgeCaseJump, VectorData.VectorType.Jump));
/*     */         } 
/*     */       } 
/*     */       
/*  67 */       existingVelocities.add(vector.returnNewModified(jump, VectorData.VectorType.Jump));
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
/*     */   public void endOfTick(GrimPlayer player, double delta) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: dload_2
/*     */     //   3: invokespecial endOfTick : (Lac/grim/grimac/player/GrimPlayer;D)V
/*     */     //   6: iconst_0
/*     */     //   7: istore #4
/*     */     //   9: aload_1
/*     */     //   10: invokevirtual inVehicle : ()Z
/*     */     //   13: ifne -> 86
/*     */     //   16: aload_1
/*     */     //   17: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   20: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_17 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   23: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   26: ifeq -> 86
/*     */     //   29: aload_1
/*     */     //   30: getfield compensatedWorld : Lac/grim/grimac/utils/latency/CompensatedWorld;
/*     */     //   33: aload_1
/*     */     //   34: getfield x : D
/*     */     //   37: aload_1
/*     */     //   38: getfield y : D
/*     */     //   41: aload_1
/*     */     //   42: getfield z : D
/*     */     //   45: invokevirtual getBlockType : (DDD)Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateType;
/*     */     //   48: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateTypes.POWDER_SNOW : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/type/StateType;
/*     */     //   51: if_acmpne -> 86
/*     */     //   54: aload_1
/*     */     //   55: getfield inventory : Lac/grim/grimac/utils/latency/CompensatedInventory;
/*     */     //   58: invokevirtual getBoots : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/item/ItemStack;
/*     */     //   61: astore #5
/*     */     //   63: aload #5
/*     */     //   65: ifnull -> 83
/*     */     //   68: aload #5
/*     */     //   70: invokevirtual getType : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/item/type/ItemType;
/*     */     //   73: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/item/type/ItemTypes.LEATHER_BOOTS : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/item/type/ItemType;
/*     */     //   76: if_acmpne -> 83
/*     */     //   79: iconst_1
/*     */     //   80: goto -> 84
/*     */     //   83: iconst_0
/*     */     //   84: istore #4
/*     */     //   86: aload_1
/*     */     //   87: aload_1
/*     */     //   88: aload_1
/*     */     //   89: getfield x : D
/*     */     //   92: aload_1
/*     */     //   93: getfield y : D
/*     */     //   96: aload_1
/*     */     //   97: getfield z : D
/*     */     //   100: invokestatic onClimbable : (Lac/grim/grimac/player/GrimPlayer;DDD)Z
/*     */     //   103: putfield isClimbing : Z
/*     */     //   106: aload_1
/*     */     //   107: getfield lastWasClimbing : D
/*     */     //   110: dconst_0
/*     */     //   111: dcmpl
/*     */     //   112: ifne -> 189
/*     */     //   115: aload_1
/*     */     //   116: getfield pointThreeEstimator : Lac/grim/grimac/predictionengine/PointThreeEstimator;
/*     */     //   119: invokevirtual isNearClimbable : ()Z
/*     */     //   122: ifne -> 132
/*     */     //   125: aload_1
/*     */     //   126: getfield isClimbing : Z
/*     */     //   129: ifeq -> 189
/*     */     //   132: aload_1
/*     */     //   133: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   136: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_14 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   139: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   142: ifne -> 194
/*     */     //   145: aload_1
/*     */     //   146: aload_1
/*     */     //   147: getfield boundingBox : Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   150: invokevirtual copy : ()Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   153: aload_1
/*     */     //   154: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   157: invokevirtual getX : ()D
/*     */     //   160: dconst_0
/*     */     //   161: aload_1
/*     */     //   162: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   165: invokevirtual getZ : ()D
/*     */     //   168: invokevirtual expand : (DDD)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   171: ldc2_w 0.5
/*     */     //   174: ldc2_w -1.0E-7
/*     */     //   177: ldc2_w 0.5
/*     */     //   180: invokevirtual expand : (DDD)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   183: invokestatic isEmpty : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;)Z
/*     */     //   186: ifeq -> 194
/*     */     //   189: iload #4
/*     */     //   191: ifeq -> 224
/*     */     //   194: aload_1
/*     */     //   195: getfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   198: invokevirtual clone : ()Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   201: ldc2_w 0.2
/*     */     //   204: invokevirtual setY : (D)Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   207: astore #5
/*     */     //   209: aload_1
/*     */     //   210: aload #5
/*     */     //   212: invokestatic staticVectorEndOfTick : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/math/Vector3dm;)V
/*     */     //   215: aload_1
/*     */     //   216: aload #5
/*     */     //   218: invokevirtual getY : ()D
/*     */     //   221: putfield lastWasClimbing : D
/*     */     //   224: aload_1
/*     */     //   225: invokevirtual getPossibleVelocitiesMinusKnockback : ()Ljava/util/Set;
/*     */     //   228: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   233: astore #5
/*     */     //   235: aload #5
/*     */     //   237: invokeinterface hasNext : ()Z
/*     */     //   242: ifeq -> 269
/*     */     //   245: aload #5
/*     */     //   247: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   252: checkcast ac/grim/grimac/utils/data/VectorData
/*     */     //   255: astore #6
/*     */     //   257: aload_1
/*     */     //   258: aload #6
/*     */     //   260: getfield vector : Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   263: invokestatic staticVectorEndOfTick : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/utils/math/Vector3dm;)V
/*     */     //   266: goto -> 235
/*     */     //   269: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #73	-> 0
/*     */     //   #75	-> 6
/*     */     //   #77	-> 9
/*     */     //   #78	-> 45
/*     */     //   #79	-> 54
/*     */     //   #80	-> 63
/*     */     //   #83	-> 86
/*     */     //   #86	-> 106
/*     */     //   #87	-> 150
/*     */     //   #88	-> 157
/*     */     //   #87	-> 168
/*     */     //   #88	-> 180
/*     */     //   #87	-> 183
/*     */     //   #89	-> 194
/*     */     //   #90	-> 209
/*     */     //   #91	-> 215
/*     */     //   #94	-> 224
/*     */     //   #95	-> 257
/*     */     //   #96	-> 266
/*     */     //   #97	-> 269
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   63	23	5	boots	Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/item/ItemStack;
/*     */     //   209	15	5	ladderVelocity	Lac/grim/grimac/utils/math/Vector3dm;
/*     */     //   257	9	6	vector	Lac/grim/grimac/utils/data/VectorData;
/*     */     //   0	270	0	this	Lac/grim/grimac/predictionengine/predictions/PredictionEngineNormal;
/*     */     //   0	270	1	player	Lac/grim/grimac/player/GrimPlayer;
/*     */     //   0	270	2	delta	D
/*     */     //   9	261	4	walkingOnPowderSnow	Z
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
/*     */   public Vector3dm handleOnClimbable(Vector3dm vector, GrimPlayer player) {
/* 101 */     if (player.isClimbing) {
/*     */       
/* 103 */       player.fallDistance = 0.0D;
/*     */       
/* 105 */       vector.setX(GrimMath.clamp(vector.getX(), -0.15000000596046448D, 0.15000000596046448D));
/* 106 */       vector.setZ(GrimMath.clamp(vector.getZ(), -0.15000000596046448D, 0.15000000596046448D));
/* 107 */       vector.setY(Math.max(vector.getY(), -0.15000000596046448D));
/*     */ 
/*     */       
/* 110 */       if (vector.getY() < 0.0D && player.compensatedWorld.getBlockType(player.lastX, player.lastY, player.lastZ) != StateTypes.SCAFFOLDING && player.isSneaking && !player.isFlying) {
/* 111 */         vector.setY(0.0D);
/*     */       }
/*     */     } 
/*     */     
/* 115 */     return vector;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\predictions\PredictionEngineNormal.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
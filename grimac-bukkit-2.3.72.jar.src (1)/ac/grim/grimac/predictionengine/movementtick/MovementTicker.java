/*     */ package ac.grim.grimac.predictionengine.movementtick;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.predictionengine.PlayerBaseTick;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*     */ import ac.grim.grimac.predictionengine.predictions.PredictionEngineElytra;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*     */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*     */ import ac.grim.grimac.utils.nmsutil.FluidFallingAdjustedMovement;
/*     */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*     */ import ac.grim.grimac.utils.nmsutil.MainSupportingBlockPosFinder;
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
/*     */ public class MovementTicker
/*     */ {
/*     */   public final GrimPlayer player;
/*     */   
/*     */   public MovementTicker(GrimPlayer player) {
/*  40 */     this.player = player;
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
/*     */   public static void handleEntityCollisions(GrimPlayer player) {
/*     */     // Byte code:
/*     */     //   0: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   3: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   6: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   11: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   14: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   17: istore_1
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   22: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   25: invokevirtual isOlderThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   28: ifne -> 56
/*     */     //   31: iload_1
/*     */     //   32: ifne -> 52
/*     */     //   35: getstatic ac/grim/grimac/utils/reflection/ViaVersionUtil.isAvailable : Z
/*     */     //   38: ifeq -> 56
/*     */     //   41: invokestatic getConfig : ()Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;
/*     */     //   44: invokeinterface isPreventCollision : ()Z
/*     */     //   49: ifne -> 56
/*     */     //   52: iconst_1
/*     */     //   53: goto -> 57
/*     */     //   56: iconst_0
/*     */     //   57: istore_2
/*     */     //   58: iload_2
/*     */     //   59: ifne -> 63
/*     */     //   62: return
/*     */     //   63: iconst_0
/*     */     //   64: istore_3
/*     */     //   65: iconst_0
/*     */     //   66: istore #4
/*     */     //   68: aload_0
/*     */     //   69: invokevirtual inVehicle : ()Z
/*     */     //   72: ifne -> 293
/*     */     //   75: aload_0
/*     */     //   76: getfield gamemode : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/GameMode;
/*     */     //   79: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/GameMode.SPECTATOR : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/GameMode;
/*     */     //   82: if_acmpeq -> 293
/*     */     //   85: aload_0
/*     */     //   86: aload_0
/*     */     //   87: getfield lastX : D
/*     */     //   90: aload_0
/*     */     //   91: getfield lastY : D
/*     */     //   94: aload_0
/*     */     //   95: getfield lastZ : D
/*     */     //   98: ldc 0.6
/*     */     //   100: ldc 1.8
/*     */     //   102: invokestatic getBoundingBoxFromPosAndSize : (Lac/grim/grimac/player/GrimPlayer;DDDFF)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   105: astore #5
/*     */     //   107: aload #5
/*     */     //   109: aload_0
/*     */     //   110: aload_0
/*     */     //   111: getfield x : D
/*     */     //   114: aload_0
/*     */     //   115: getfield y : D
/*     */     //   118: aload_0
/*     */     //   119: getfield z : D
/*     */     //   122: ldc 0.6
/*     */     //   124: ldc 1.8
/*     */     //   126: invokestatic getBoundingBoxFromPosAndSize : (Lac/grim/grimac/player/GrimPlayer;DDDFF)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   129: aload_0
/*     */     //   130: invokevirtual getMovementThreshold : ()D
/*     */     //   133: invokevirtual expand : (D)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   136: invokevirtual encompass : (Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;)Lac/grim/grimac/utils/collisions/datatypes/CollisionBox;
/*     */     //   139: pop
/*     */     //   140: aload #5
/*     */     //   142: ldc2_w 0.2
/*     */     //   145: invokevirtual expand : (D)Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   148: pop
/*     */     //   149: aload_0
/*     */     //   150: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*     */     //   153: ldc ac/grim/grimac/utils/team/TeamHandler
/*     */     //   155: invokevirtual getPacketCheck : (Ljava/lang/Class;)Lac/grim/grimac/checks/type/PacketCheck;
/*     */     //   158: checkcast ac/grim/grimac/utils/team/TeamHandler
/*     */     //   161: astore #6
/*     */     //   163: aload #6
/*     */     //   165: ifnull -> 176
/*     */     //   168: aload #6
/*     */     //   170: invokevirtual getPlayerTeam : ()Lac/grim/grimac/utils/team/EntityTeam;
/*     */     //   173: goto -> 177
/*     */     //   176: aconst_null
/*     */     //   177: astore #7
/*     */     //   179: aload_0
/*     */     //   180: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*     */     //   183: getfield entityMap : Lac/grim/grimac/shaded/fastutil/ints/Int2ObjectOpenHashMap;
/*     */     //   186: invokevirtual values : ()Lac/grim/grimac/shaded/fastutil/objects/ObjectCollection;
/*     */     //   189: invokeinterface iterator : ()Lac/grim/grimac/shaded/fastutil/objects/ObjectIterator;
/*     */     //   194: astore #8
/*     */     //   196: aload #8
/*     */     //   198: invokeinterface hasNext : ()Z
/*     */     //   203: ifeq -> 293
/*     */     //   206: aload #8
/*     */     //   208: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   213: checkcast ac/grim/grimac/utils/data/packetentity/PacketEntity
/*     */     //   216: astore #9
/*     */     //   218: aload #9
/*     */     //   220: invokevirtual getPossibleCollisionBoxes : ()Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   223: astore #10
/*     */     //   225: aload #5
/*     */     //   227: aload #10
/*     */     //   229: invokevirtual isCollided : (Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;)Z
/*     */     //   232: ifne -> 238
/*     */     //   235: goto -> 196
/*     */     //   238: iinc #4, 1
/*     */     //   241: aload #9
/*     */     //   243: invokevirtual isPushable : ()Z
/*     */     //   246: ifne -> 252
/*     */     //   249: goto -> 196
/*     */     //   252: iload_1
/*     */     //   253: ifeq -> 287
/*     */     //   256: aload #6
/*     */     //   258: ifnull -> 271
/*     */     //   261: aload #6
/*     */     //   263: aload #9
/*     */     //   265: invokevirtual getEntityTeam : (Lac/grim/grimac/utils/data/packetentity/PacketEntity;)Lac/grim/grimac/utils/team/EntityTeam;
/*     */     //   268: goto -> 272
/*     */     //   271: aconst_null
/*     */     //   272: astore #11
/*     */     //   274: aload #11
/*     */     //   276: aload #7
/*     */     //   278: invokestatic canBePushedBy : (Lac/grim/grimac/utils/team/EntityTeam;Lac/grim/grimac/utils/team/EntityTeam;)Z
/*     */     //   281: ifne -> 287
/*     */     //   284: goto -> 196
/*     */     //   287: iinc #3, 1
/*     */     //   290: goto -> 196
/*     */     //   293: aload_0
/*     */     //   294: getfield isGliding : Z
/*     */     //   297: ifeq -> 334
/*     */     //   300: iload_3
/*     */     //   301: ifle -> 334
/*     */     //   304: aload_0
/*     */     //   305: getfield uncertaintyHandler : Lac/grim/grimac/predictionengine/UncertaintyHandler;
/*     */     //   308: dup
/*     */     //   309: getfield yNegativeUncertainty : D
/*     */     //   312: ldc2_w 0.05
/*     */     //   315: dsub
/*     */     //   316: putfield yNegativeUncertainty : D
/*     */     //   319: aload_0
/*     */     //   320: getfield uncertaintyHandler : Lac/grim/grimac/predictionengine/UncertaintyHandler;
/*     */     //   323: dup
/*     */     //   324: getfield yPositiveUncertainty : D
/*     */     //   327: ldc2_w 0.05
/*     */     //   330: dadd
/*     */     //   331: putfield yPositiveUncertainty : D
/*     */     //   334: aload_0
/*     */     //   335: getfield uncertaintyHandler : Lac/grim/grimac/predictionengine/UncertaintyHandler;
/*     */     //   338: getfield riptideEntities : Lac/grim/grimac/utils/lists/EvictingQueue;
/*     */     //   341: iload #4
/*     */     //   343: invokestatic valueOf : (I)Ljava/lang/Integer;
/*     */     //   346: invokevirtual add : (Ljava/lang/Object;)Z
/*     */     //   349: pop
/*     */     //   350: aload_0
/*     */     //   351: getfield uncertaintyHandler : Lac/grim/grimac/predictionengine/UncertaintyHandler;
/*     */     //   354: getfield collidingEntities : Lac/grim/grimac/utils/lists/EvictingQueue;
/*     */     //   357: iload_3
/*     */     //   358: invokestatic valueOf : (I)Ljava/lang/Integer;
/*     */     //   361: invokevirtual add : (Ljava/lang/Object;)Z
/*     */     //   364: pop
/*     */     //   365: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #45	-> 0
/*     */     //   #46	-> 18
/*     */     //   #49	-> 41
/*     */     //   #50	-> 58
/*     */     //   #52	-> 63
/*     */     //   #53	-> 65
/*     */     //   #56	-> 68
/*     */     //   #58	-> 85
/*     */     //   #59	-> 107
/*     */     //   #60	-> 140
/*     */     //   #62	-> 149
/*     */     //   #63	-> 163
/*     */     //   #64	-> 179
/*     */     //   #66	-> 218
/*     */     //   #67	-> 225
/*     */     //   #69	-> 238
/*     */     //   #71	-> 241
/*     */     //   #75	-> 252
/*     */     //   #76	-> 256
/*     */     //   #77	-> 274
/*     */     //   #80	-> 287
/*     */     //   #81	-> 290
/*     */     //   #84	-> 293
/*     */     //   #87	-> 304
/*     */     //   #88	-> 319
/*     */     //   #91	-> 334
/*     */     //   #92	-> 350
/*     */     //   #93	-> 365
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   274	13	11	entityTeam	Lac/grim/grimac/utils/team/EntityTeam;
/*     */     //   225	65	10	entityBox	Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   218	72	9	entity	Lac/grim/grimac/utils/data/packetentity/PacketEntity;
/*     */     //   107	186	5	playerBox	Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*     */     //   163	130	6	teamHandler	Lac/grim/grimac/utils/team/TeamHandler;
/*     */     //   179	114	7	playerTeam	Lac/grim/grimac/utils/team/EntityTeam;
/*     */     //   0	366	0	player	Lac/grim/grimac/player/GrimPlayer;
/*     */     //   18	348	1	serverSupported	Z
/*     */     //   58	308	2	hasEntityPushing	Z
/*     */     //   65	301	3	possibleCollidingEntities	I
/*     */     //   68	298	4	possibleRiptideEntities	I
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
/*     */   private boolean isHorizontalCollisionSoft(Vector3dm collide) {
/*  96 */     double horizontalLengthSquared = collide.getX() * collide.getX() + collide.getZ() * collide.getZ();
/*  97 */     if (horizontalLengthSquared < 9.999999747378752E-6D) return false;
/*     */     
/*  99 */     float xxa = (float)this.player.predictedVelocity.input.getX();
/* 100 */     float zza = (float)this.player.predictedVelocity.input.getZ();
/*     */     
/* 102 */     float yawInRadians = this.player.xRot * 0.017453292F;
/* 103 */     double sin = this.player.trigHandler.sin(yawInRadians);
/* 104 */     double cos = this.player.trigHandler.cos(yawInRadians);
/* 105 */     double g = xxa * cos - zza * sin;
/* 106 */     double h = zza * cos + xxa * sin;
/* 107 */     double i = g * g + h * h;
/* 108 */     return (i >= 9.999999747378752E-6D && Math.acos((g * collide.getX() + h * collide.getZ()) / Math.sqrt(i * horizontalLengthSquared)) < 0.13962633907794952D);
/*     */   }
/*     */   
/*     */   public void move(Vector3dm inputVel, Vector3dm collide) {
/* 112 */     if (this.player.stuckSpeedMultiplier.getX() < 0.99D) {
/* 113 */       this.player.clientVelocity = new Vector3dm();
/*     */     }
/*     */     
/* 116 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2)) {
/* 117 */       boolean xAxis = !GrimMath.equal(inputVel.getX(), collide.getX());
/* 118 */       boolean zAxis = !GrimMath.equal(inputVel.getZ(), collide.getZ());
/*     */       
/* 120 */       if (xAxis) {
/* 121 */         this.player.clientVelocity.setX(0);
/*     */       }
/*     */       
/* 124 */       if (zAxis) {
/* 125 */         this.player.clientVelocity.setZ(0);
/*     */       }
/*     */       
/* 128 */       this.player.horizontalCollision = (xAxis || zAxis);
/* 129 */       this.player.softHorizontalCollision = (this.player.horizontalCollision && isHorizontalCollisionSoft(collide));
/*     */     } else {
/* 131 */       if (inputVel.getX() != collide.getX()) {
/* 132 */         this.player.clientVelocity.setX(0);
/*     */       }
/*     */       
/* 135 */       if (inputVel.getZ() != collide.getZ()) {
/* 136 */         this.player.clientVelocity.setZ(0);
/*     */       }
/*     */       
/* 139 */       this.player.horizontalCollision = (inputVel.getX() != collide.getX() || inputVel.getZ() != collide.getZ());
/*     */     } 
/*     */     
/* 142 */     this.player.verticalCollision = (inputVel.getY() != collide.getY());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     boolean calculatedOnGround = (this.player.verticalCollision && inputVel.getY() < 0.0D);
/*     */ 
/*     */     
/* 151 */     if (inputVel.getY() == -1.0E-7D && collide.getY() > -1.0E-7D && collide.getY() <= 0.0D && !this.player.inVehicle())
/* 152 */       calculatedOnGround = this.player.onGround; 
/* 153 */     this.player.clientClaimsLastOnGround = this.player.onGround;
/*     */ 
/*     */ 
/*     */     
/* 157 */     if (this.player.inVehicle() && this.player.clientControlledVerticalCollision && this.player.uncertaintyHandler.isStepMovement && (inputVel
/* 158 */       .getY() <= 0.0D || this.player.predictedVelocity.isSwimHop())) {
/* 159 */       calculatedOnGround = true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 167 */     if (this.player.inVehicle() || !this.player.exemptOnGround()) {
/* 168 */       this.player.onGround = calculatedOnGround;
/*     */     }
/*     */ 
/*     */     
/* 172 */     this.player.boundingBox = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.x, this.player.y, this.player.z);
/*     */ 
/*     */     
/* 175 */     PacketEntity riding = this.player.compensatedEntities.self.getRiding();
/*     */     
/* 177 */     if (this.player.getClientVersion() != ClientVersion.V_1_21_4 && !this.player.wasTouchingWater && (riding == null || (!riding.isBoat && !riding.isHappyGhast))) {
/* 178 */       PlayerBaseTick.updateInWaterStateAndDoWaterCurrentPushing(this.player);
/*     */     }
/*     */     
/* 181 */     if (this.player.onGround) {
/* 182 */       this.player.fallDistance = 0.0D;
/* 183 */     } else if (collide.getY() < 0.0D) {
/* 184 */       this.player.fallDistance -= collide.getY();
/* 185 */       this.player.vehicleData.lastYd = collide.getY();
/*     */     } 
/*     */ 
/*     */     
/* 189 */     if (riding instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityStrider) {
/* 190 */       Collisions.handleInsideBlocks(this.player);
/*     */     }
/*     */     
/* 193 */     this.player.mainSupportingBlockData = MainSupportingBlockPosFinder.findMainSupportingBlockPos(this.player, this.player.mainSupportingBlockData, new Vector3d(collide.getX(), collide.getY(), collide.getZ()), this.player.boundingBox, this.player.onGround);
/* 194 */     StateType onBlock = BlockProperties.getOnPos(this.player, this.player.mainSupportingBlockData, new Vector3d(this.player.x, this.player.y, this.player.z));
/*     */ 
/*     */     
/* 197 */     if (inputVel.getY() != collide.getY())
/*     */     {
/*     */ 
/*     */       
/* 201 */       if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) && (onBlock == StateTypes.SLIME_BLOCK || (onBlock == StateTypes.HONEY_BLOCK && this.player
/* 202 */         .getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_14_4)))) {
/* 203 */         if (this.player.isSneaking) {
/* 204 */           this.player.clientVelocity.setY(0);
/*     */         }
/* 206 */         else if (this.player.clientVelocity.getY() < 0.0D) {
/* 207 */           this.player.clientVelocity.setY(-this.player.clientVelocity.getY() * ((
/* 208 */               riding != null && !riding.isLivingEntity) ? 0.8D : 1.0D));
/*     */         }
/*     */       
/* 211 */       } else if (BlockTags.BEDS.contains(onBlock) && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12)) {
/* 212 */         if (this.player.clientVelocity.getY() < 0.0D) {
/* 213 */           this.player.clientVelocity.setY(-this.player.clientVelocity.getY() * 0.6600000262260437D * ((
/* 214 */               riding != null && !riding.isLivingEntity) ? 0.8D : 1.0D));
/*     */         }
/*     */       } else {
/* 217 */         this.player.clientVelocity.setY(0);
/*     */       } 
/*     */     }
/*     */     
/* 221 */     collide = PredictionEngine.clampMovementToHardBorder(this.player, collide);
/*     */ 
/*     */     
/* 224 */     if (collide.lengthSquared() <= 1.0E-7D && (this.player
/*     */       
/* 226 */       .getClientVersion().isOlderThan(ClientVersion.V_1_21_2) || inputVel.lengthSquared() - collide.lengthSquared() >= 1.0E-7D)) {
/* 227 */       collide = new Vector3dm();
/* 228 */     } else if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5)) {
/* 229 */       Vector3d from = new Vector3d(this.player.lastX, this.player.lastY, this.player.lastZ);
/* 230 */       Vector3d to = new Vector3d(this.player.x, this.player.y, this.player.z);
/*     */       
/* 232 */       this.player.addMovementThisTick(new GrimPlayer.Movement(from, to, true));
/*     */     } 
/*     */ 
/*     */     
/* 236 */     this.player.predictedVelocity = new VectorData(collide.clone(), this.player.predictedVelocity.lastVector, this.player.predictedVelocity.vectorType);
/*     */     
/* 238 */     float f = BlockProperties.getBlockSpeedFactor(this.player, this.player.mainSupportingBlockData, new Vector3d(this.player.x, this.player.y, this.player.z));
/* 239 */     this.player.clientVelocity.multiply(new Vector3dm(f, 1.0F, f));
/*     */     
/* 241 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 246 */     if (this.player.stuckSpeedMultiplier.getX() < 0.99D) {
/* 247 */       this.player.uncertaintyHandler.lastStuckSpeedMultiplier.reset();
/*     */     }
/*     */     
/* 250 */     this.player.stuckSpeedMultiplier = new Vector3dm(1, 1, 1);
/*     */ 
/*     */     
/* 253 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_16)) {
/* 254 */       this.player.wasTouchingLava = false;
/*     */     }
/* 256 */     Collisions.handleInsideBlocks(this.player);
/*     */     
/* 258 */     if (this.player.stuckSpeedMultiplier.getX() < 0.9D)
/*     */     {
/* 260 */       this.player.fallDistance = 0.0D;
/*     */     }
/*     */ 
/*     */     
/* 264 */     if (this.player.isFlying) {
/* 265 */       this.player.stuckSpeedMultiplier = new Vector3dm(1, 1, 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void livingEntityAIStep() {
/* 270 */     handleEntityCollisions(this.player);
/*     */     
/* 272 */     SimpleCollisionBox oldBB = this.player.boundingBox.copy();
/*     */     
/* 274 */     if (!this.player.inVehicle()) {
/* 275 */       playerEntityTravel();
/*     */     } else {
/* 277 */       livingEntityTravel();
/*     */     } 
/*     */     
/* 280 */     this.player.uncertaintyHandler.xNegativeUncertainty = 0.0D;
/* 281 */     this.player.uncertaintyHandler.xPositiveUncertainty = 0.0D;
/* 282 */     this.player.uncertaintyHandler.yNegativeUncertainty = 0.0D;
/* 283 */     this.player.uncertaintyHandler.yPositiveUncertainty = 0.0D;
/* 284 */     this.player.uncertaintyHandler.zNegativeUncertainty = 0.0D;
/* 285 */     this.player.uncertaintyHandler.zPositiveUncertainty = 0.0D;
/*     */ 
/*     */     
/* 288 */     if (this.player.uncertaintyHandler.lastTeleportTicks.hasOccurredSince(0)) {
/* 289 */       this.player.uncertaintyHandler.yNegativeUncertainty -= 0.02D;
/*     */     }
/*     */     
/* 292 */     if (this.player.isFlying) {
/* 293 */       SimpleCollisionBox playerBox = GetBoundingBox.getCollisionBoxForPlayer(this.player, this.player.lastX, this.player.lastY, this.player.lastZ);
/* 294 */       if (!Collisions.isEmpty(this.player, playerBox.copy().offset(0.0D, 0.1D, 0.0D))) {
/* 295 */         this.player.uncertaintyHandler.yPositiveUncertainty = (this.player.flySpeed * 5.0F);
/*     */       }
/*     */       
/* 298 */       if (!Collisions.isEmpty(this.player, playerBox.copy().offset(0.0D, -0.1D, 0.0D))) {
/* 299 */         this.player.uncertaintyHandler.yNegativeUncertainty = (this.player.flySpeed * -5.0F);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 304 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14) || this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 310 */     oldBB.expand(-1.0E-7D);
/*     */     
/* 312 */     double posX = Math.max(0.0D, this.player.predictedVelocity.vector.getX()) + 1.0E-7D;
/* 313 */     double negX = Math.min(0.0D, this.player.predictedVelocity.vector.getX()) - 1.0E-7D;
/* 314 */     double posZ = Math.max(0.0D, this.player.predictedVelocity.vector.getZ()) + 1.0E-7D;
/* 315 */     double negZ = Math.min(0.0D, this.player.predictedVelocity.vector.getZ()) - 1.0E-7D;
/*     */     
/* 317 */     boolean xAxisCollision = !Collisions.isEmpty(this.player, oldBB.expandMin(negX, 0.0D, 0.0D).expandMax(posX, 0.0D, 0.0D));
/* 318 */     boolean zAxisCollision = !Collisions.isEmpty(this.player, oldBB.expandMin(0.0D, 0.0D, negZ).expandMax(0.0D, 0.0D, posZ));
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
/* 329 */     zAxisCollision = (zAxisCollision || this.player.actualMovement.getZ() == 0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     if (zAxisCollision && xAxisCollision) {
/* 336 */       double playerSpeed = this.player.speed;
/*     */       
/* 338 */       if (this.player.wasTouchingWater) {
/* 339 */         float swimSpeed = 0.02F;
/* 340 */         if (this.player.depthStriderLevel > 0.0F) {
/* 341 */           swimSpeed = (float)(swimSpeed + (this.player.speed - swimSpeed) * this.player.depthStriderLevel / 3.0D);
/*     */         }
/* 343 */         playerSpeed = swimSpeed;
/* 344 */       } else if (this.player.wasTouchingLava) {
/* 345 */         playerSpeed = 0.019999999552965164D;
/* 346 */       } else if (this.player.isGliding) {
/* 347 */         playerSpeed = 0.4D;
/*     */ 
/*     */         
/* 350 */         this.player.uncertaintyHandler.yNegativeUncertainty -= 0.05D;
/* 351 */         this.player.uncertaintyHandler.yPositiveUncertainty += 0.05D;
/*     */       } 
/*     */       
/* 354 */       this.player.uncertaintyHandler.xNegativeUncertainty -= playerSpeed * 3.0D;
/* 355 */       this.player.uncertaintyHandler.xPositiveUncertainty += playerSpeed * 3.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playerEntityTravel() {
/* 360 */     if (this.player.isFlying && !this.player.inVehicle()) {
/* 361 */       double oldY = this.player.clientVelocity.getY();
/* 362 */       double oldYJumping = oldY + (this.player.flySpeed * 3.0F);
/* 363 */       livingEntityTravel();
/*     */       
/* 365 */       if (this.player.predictedVelocity.isKnockback() || this.player.predictedVelocity.isTrident() || this.player.uncertaintyHandler.yPositiveUncertainty != 0.0D || this.player.uncertaintyHandler.yNegativeUncertainty != 0.0D || this.player.isGliding) {
/*     */         
/* 367 */         this.player.clientVelocity.setY(this.player.actualMovement.getY() * 0.6D);
/* 368 */       } else if (Math.abs(oldY - this.player.actualMovement.getY()) < oldYJumping - this.player.actualMovement.getY()) {
/* 369 */         this.player.clientVelocity.setY(oldY * 0.6D);
/*     */       } else {
/* 371 */         this.player.clientVelocity.setY(oldYJumping * 0.6D);
/*     */       } 
/*     */     } else {
/*     */       
/* 375 */       livingEntityTravel();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {}
/*     */ 
/*     */   
/*     */   public void doLavaMove() {}
/*     */ 
/*     */   
/*     */   public void doNormalMove(float blockFriction) {}
/*     */ 
/*     */   
/*     */   public void livingEntityTravel() {
/* 391 */     double playerGravity = !this.player.inVehicle() ? this.player.compensatedEntities.self.getAttributeValue(Attributes.GRAVITY) : this.player.compensatedEntities.self.getRiding().getAttributeValue(Attributes.GRAVITY);
/*     */     
/* 393 */     boolean isFalling = (this.player.actualMovement.getY() <= 0.0D);
/* 394 */     if (isFalling && this.player.compensatedEntities.getSlowFallingAmplifier().isPresent()) {
/* 395 */       playerGravity = this.player.getClientVersion().isOlderThan(ClientVersion.V_1_20_5) ? 0.01D : Math.min(playerGravity, 0.01D);
/*     */       
/* 397 */       this.player.fallDistance = 0.0D;
/*     */     } 
/*     */     
/* 400 */     this.player.gravity = playerGravity;
/*     */ 
/*     */ 
/*     */     
/* 404 */     double lavaLevel = 0.0D;
/* 405 */     if (canStandOnLava()) {
/* 406 */       lavaLevel = this.player.compensatedWorld.getLavaFluidLevelAt(GrimMath.floor(this.player.lastX), GrimMath.floor(this.player.lastY), GrimMath.floor(this.player.lastZ));
/*     */     }
/* 408 */     if (this.player.wasTouchingWater && !this.player.isFlying) {
/*     */ 
/*     */       
/* 411 */       boolean isSkeletonHorse = (this.player.inVehicle() && (this.player.compensatedEntities.self.getRiding()).type == EntityTypes.SKELETON_HORSE && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13));
/* 412 */       float swimFriction = (this.player.isSprinting && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) ? 0.9F : (isSkeletonHorse ? 0.96F : 0.8F);
/* 413 */       float swimSpeed = 0.02F;
/*     */       
/* 415 */       if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_21) && this.player.depthStriderLevel > 3.0F) {
/* 416 */         this.player.depthStriderLevel = 3.0F;
/*     */       }
/*     */       
/* 419 */       if (!this.player.lastOnGround) {
/* 420 */         this.player.depthStriderLevel *= 0.5F;
/*     */       }
/*     */       
/* 423 */       if (this.player.depthStriderLevel > 0.0F) {
/* 424 */         float divisor = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) ? 1.0F : 3.0F;
/* 425 */         swimFriction += (0.54600006F - swimFriction) * this.player.depthStriderLevel / divisor;
/* 426 */         swimSpeed = (float)(swimSpeed + (this.player.speed - swimSpeed) * this.player.depthStriderLevel / divisor);
/*     */       } 
/*     */       
/* 429 */       if (this.player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.DOLPHINS_GRACE).isPresent()) {
/* 430 */         swimFriction = 0.96F;
/*     */       }
/*     */       
/* 433 */       this.player.friction = swimFriction;
/* 434 */       doWaterMove(swimSpeed, isFalling, swimFriction);
/*     */       
/* 436 */       this.player.isClimbing = Collisions.onClimbable(this.player, this.player.x, this.player.y, this.player.z);
/*     */ 
/*     */ 
/*     */       
/* 440 */       if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && this.player.isClimbing) {
/* 441 */         this.player.lastWasClimbing = FluidFallingAdjustedMovement.getFluidFallingAdjustedMovement(this.player, playerGravity, isFalling, this.player.clientVelocity.clone().setY(0.1600000023841858D)).getY();
/*     */       
/*     */       }
/*     */     }
/* 445 */     else if (this.player.wasTouchingLava && !this.player.isFlying && (lavaLevel <= 0.0D || !canStandOnLava())) {
/* 446 */       this.player.friction = 0.5F;
/*     */       
/* 448 */       doLavaMove();
/*     */ 
/*     */       
/* 451 */       if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && this.player.slightlyTouchingLava) {
/* 452 */         this.player.clientVelocity = this.player.clientVelocity.multiply(new Vector3dm(0.5D, 0.800000011920929D, 0.5D));
/* 453 */         this.player.clientVelocity = FluidFallingAdjustedMovement.getFluidFallingAdjustedMovement(this.player, playerGravity, isFalling, this.player.clientVelocity);
/*     */       } else {
/* 455 */         this.player.clientVelocity.multiply(0.5D);
/*     */       } 
/*     */       
/* 458 */       if (this.player.hasGravity) {
/* 459 */         this.player.clientVelocity.add(new Vector3dm(0.0D, -playerGravity / 4.0D, 0.0D));
/*     */       }
/* 461 */     } else if (this.player.isGliding) {
/* 462 */       if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5) && Collisions.onClimbable(this.player, this.player.lastX, this.player.lastY, this.player.lastZ)) {
/* 463 */         float blockFriction = BlockProperties.getFriction(this.player, this.player.mainSupportingBlockData, new Vector3d(this.player.lastX, this.player.lastY, this.player.lastZ));
/* 464 */         this.player.friction = this.player.lastOnGround ? (blockFriction * 0.91F) : 0.91F;
/*     */         
/* 466 */         doNormalMove(blockFriction);
/*     */         
/* 468 */         this.player.isGliding = false;
/* 469 */         this.player.pointThreeEstimator.updatePlayerGliding();
/*     */       } else {
/* 471 */         this.player.friction = 0.99F;
/*     */         
/* 473 */         if (this.player.clientVelocity.getY() > -0.5D) {
/* 474 */           this.player.fallDistance = 1.0D;
/*     */         }
/*     */         
/* 477 */         (new PredictionEngineElytra()).guessBestMovement(0.0F, this.player);
/*     */       } 
/*     */     } else {
/* 480 */       float blockFriction = BlockProperties.getFriction(this.player, this.player.mainSupportingBlockData, new Vector3d(this.player.lastX, this.player.lastY, this.player.lastZ));
/* 481 */       this.player.friction = this.player.lastOnGround ? (blockFriction * 0.91F) : 0.91F;
/*     */       
/* 483 */       doNormalMove(blockFriction);
/*     */     } 
/*     */ 
/*     */     
/* 487 */     if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2)) {
/*     */       
/* 489 */       if (this.player.stuckSpeedMultiplier.getX() < 0.99D) {
/* 490 */         this.player.uncertaintyHandler.lastStuckSpeedMultiplier.reset();
/*     */       }
/*     */       
/* 493 */       this.player.stuckSpeedMultiplier = new Vector3dm(1, 1, 1);
/* 494 */       this.player.finalMovementsThisTick.clear();
/*     */       
/* 496 */       Vector3d from = new Vector3d(this.player.lastX, this.player.lastY, this.player.lastZ);
/* 497 */       Vector3d to = new Vector3d(this.player.x, this.player.y, this.player.z);
/*     */       
/* 499 */       ClientVersion clientVersion = this.player.getClientVersion();
/* 500 */       if (clientVersion.isOlderThan(ClientVersion.V_1_21_5)) {
/* 501 */         this.player.finalMovementsThisTick.add(new GrimPlayer.Movement(from, to, false));
/* 502 */       } else if (clientVersion.isNewerThanOrEquals(ClientVersion.V_1_21_5)) {
/* 503 */         this.player.finalMovementsThisTick.addAll(this.player.movementThisTick);
/* 504 */         this.player.movementThisTick.clear();
/*     */         
/* 506 */         if (this.player.finalMovementsThisTick.isEmpty()) {
/* 507 */           this.player.finalMovementsThisTick.add(new GrimPlayer.Movement(from, to, false));
/* 508 */         } else if (((GrimPlayer.Movement)this.player.finalMovementsThisTick.get(this.player.finalMovementsThisTick.size() - 1)).to().distanceSquared(to) > 9.999999439624929E-11D) {
/* 509 */           this.player.finalMovementsThisTick.add(new GrimPlayer.Movement(((GrimPlayer.Movement)this.player.finalMovementsThisTick.get(this.player.finalMovementsThisTick.size() - 1)).to(), to, false));
/*     */         } 
/*     */       } 
/*     */       
/* 513 */       Collisions.applyEffectsFromBlocks(this.player);
/*     */       
/* 515 */       if (this.player.stuckSpeedMultiplier.getX() < 0.9D)
/*     */       {
/* 517 */         this.player.fallDistance = 0.0D;
/*     */       }
/*     */ 
/*     */       
/* 521 */       if (this.player.isFlying) {
/* 522 */         this.player.stuckSpeedMultiplier = new Vector3dm(1, 1, 1);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean canStandOnLava() {
/* 528 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTicker.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
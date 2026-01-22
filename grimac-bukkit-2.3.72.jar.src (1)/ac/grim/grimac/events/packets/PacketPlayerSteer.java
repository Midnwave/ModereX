/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.predictions.PredictionEngine;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
/*    */ import ac.grim.grimac.utils.data.KnownInput;
/*    */ import ac.grim.grimac.utils.math.Vec2;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PacketPlayerSteer
/*    */   extends PacketListenerAbstract
/*    */ {
/*    */   public PacketPlayerSteer() {
/* 24 */     super(PacketListenerPriority.LOW);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPreVia() {
/* 29 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 34 */     if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
/* 35 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 36 */       if (player == null)
/*    */         return; 
/* 38 */       WrapperPlayClientSteerVehicle steer = new WrapperPlayClientSteerVehicle(event);
/*    */       
/* 40 */       float forwards = steer.getForward();
/* 41 */       float sideways = steer.getSideways();
/*    */       
/* 43 */       player.vehicleData.nextVehicleForward = forwards;
/* 44 */       player.vehicleData.nextVehicleHorizontal = sideways;
/*    */       
/* 46 */       tickPlayerWorld(player);
/* 47 */     } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_INPUT) {
/* 48 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 49 */       if (player == null)
/*    */         return; 
/* 51 */       WrapperPlayClientPlayerInput input = new WrapperPlayClientPlayerInput(event);
/* 52 */       byte forward = 0;
/* 53 */       byte sideways = 0;
/* 54 */       if (input.isForward()) {
/* 55 */         forward = (byte)(forward + 1);
/*    */       }
/*    */       
/* 58 */       if (input.isBackward()) {
/* 59 */         forward = (byte)(forward - 1);
/*    */       }
/*    */       
/* 62 */       if (input.isLeft()) {
/* 63 */         sideways = (byte)(sideways + 1);
/*    */       }
/*    */       
/* 66 */       if (input.isRight()) {
/* 67 */         sideways = (byte)(sideways - 1);
/*    */       }
/*    */ 
/*    */ 
/*    */       
/* 72 */       Vec2 inputVector = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5) ? PredictionEngine.modifyInput(player, (new Vec2(forward, sideways)).normalized()) : new Vec2(forward * 0.98F, sideways * 0.98F);
/*    */       
/* 74 */       player.vehicleData.nextVehicleForward = inputVector.x();
/* 75 */       player.vehicleData.nextVehicleHorizontal = inputVector.y();
/*    */ 
/*    */       
/* 78 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_6)) {
/* 79 */         player.isSneaking = input.isShift();
/*    */       }
/*    */       
/* 82 */       player.packetStateData.knownInput = new KnownInput(input.isForward(), input.isBackward(), input.isLeft(), input.isRight(), input.isJump(), input.isShift(), input.isSprint());
/* 83 */     } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
/* 84 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 85 */       if (player == null || !player.inVehicle() || player.getClientVersion().isOlderThan(ClientVersion.V_1_21_2)) {
/*    */         return;
/*    */       }
/* 88 */       tickPlayerWorld(player);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void tickPlayerWorld(GrimPlayer player) {
/*    */     // Byte code:
/*    */     //   0: aload_1
/*    */     //   1: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*    */     //   4: getfield self : Lac/grim/grimac/utils/data/packetentity/PacketEntitySelf;
/*    */     //   7: invokevirtual getRiding : ()Lac/grim/grimac/utils/data/packetentity/PacketEntity;
/*    */     //   10: astore_2
/*    */     //   11: aload_1
/*    */     //   12: getfield packetStateData : Lac/grim/grimac/utils/data/PacketStateData;
/*    */     //   15: getfield receivedSteerVehicle : Z
/*    */     //   18: ifeq -> 423
/*    */     //   21: aload_2
/*    */     //   22: ifnull -> 423
/*    */     //   25: aload_2
/*    */     //   26: getfield isBoat : Z
/*    */     //   29: ifne -> 58
/*    */     //   32: aload_2
/*    */     //   33: getfield isHappyGhast : Z
/*    */     //   36: ifne -> 58
/*    */     //   39: aload_2
/*    */     //   40: instanceof ac/grim/grimac/utils/data/packetentity/PacketEntityHorse
/*    */     //   43: ifeq -> 112
/*    */     //   46: aload_2
/*    */     //   47: checkcast ac/grim/grimac/utils/data/packetentity/PacketEntityHorse
/*    */     //   50: astore_3
/*    */     //   51: aload_3
/*    */     //   52: invokevirtual hasSaddle : ()Z
/*    */     //   55: ifeq -> 112
/*    */     //   58: aload_2
/*    */     //   59: getfield passengers : Ljava/util/List;
/*    */     //   62: iconst_0
/*    */     //   63: invokeinterface get : (I)Ljava/lang/Object;
/*    */     //   68: aload_1
/*    */     //   69: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*    */     //   72: getfield self : Lac/grim/grimac/utils/data/packetentity/PacketEntitySelf;
/*    */     //   75: if_acmpne -> 112
/*    */     //   78: aload_1
/*    */     //   79: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*    */     //   82: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*    */     //   85: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*    */     //   88: ifeq -> 112
/*    */     //   91: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*    */     //   94: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*    */     //   97: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*    */     //   102: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*    */     //   105: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*    */     //   108: ifeq -> 112
/*    */     //   111: return
/*    */     //   112: aload_1
/*    */     //   113: getfield compensatedWorld : Lac/grim/grimac/utils/latency/CompensatedWorld;
/*    */     //   116: invokevirtual tickPlayerInPistonPushingArea : ()V
/*    */     //   119: aload_1
/*    */     //   120: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*    */     //   123: invokevirtual tick : ()V
/*    */     //   126: aload_1
/*    */     //   127: getfield vehicleData : Lac/grim/grimac/utils/data/VehicleData;
/*    */     //   130: iconst_1
/*    */     //   131: putfield lastDummy : Z
/*    */     //   134: aload_1
/*    */     //   135: invokevirtual inVehicle : ()Z
/*    */     //   138: ifeq -> 148
/*    */     //   141: aload_1
/*    */     //   142: invokevirtual getRidingVehicleId : ()I
/*    */     //   145: goto -> 152
/*    */     //   148: aload_1
/*    */     //   149: getfield entityID : I
/*    */     //   152: istore_3
/*    */     //   153: aload_1
/*    */     //   154: aload_1
/*    */     //   155: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   158: invokevirtual getKnockbackHandler : ()Lac/grim/grimac/checks/impl/velocity/KnockbackHandler;
/*    */     //   161: iload_3
/*    */     //   162: aload_1
/*    */     //   163: getfield lastTransactionReceived : Ljava/util/concurrent/atomic/AtomicInteger;
/*    */     //   166: invokevirtual get : ()I
/*    */     //   169: invokevirtual calculateFirstBreadKnockback : (II)Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   172: putfield firstBreadKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   175: aload_1
/*    */     //   176: aload_1
/*    */     //   177: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   180: invokevirtual getKnockbackHandler : ()Lac/grim/grimac/checks/impl/velocity/KnockbackHandler;
/*    */     //   183: iload_3
/*    */     //   184: aload_1
/*    */     //   185: getfield lastTransactionReceived : Ljava/util/concurrent/atomic/AtomicInteger;
/*    */     //   188: invokevirtual get : ()I
/*    */     //   191: iconst_0
/*    */     //   192: invokevirtual calculateRequiredKB : (IIZ)Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   195: putfield likelyKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   198: aload_1
/*    */     //   199: getfield firstBreadKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   202: ifnull -> 216
/*    */     //   205: aload_1
/*    */     //   206: aload_1
/*    */     //   207: getfield firstBreadKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   210: getfield vector : Lac/grim/grimac/utils/math/Vector3dm;
/*    */     //   213: putfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*    */     //   216: aload_1
/*    */     //   217: getfield likelyKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   220: ifnull -> 234
/*    */     //   223: aload_1
/*    */     //   224: aload_1
/*    */     //   225: getfield likelyKB : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   228: getfield vector : Lac/grim/grimac/utils/math/Vector3dm;
/*    */     //   231: putfield clientVelocity : Lac/grim/grimac/utils/math/Vector3dm;
/*    */     //   234: aload_1
/*    */     //   235: aload_1
/*    */     //   236: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   239: invokevirtual getExplosionHandler : ()Lac/grim/grimac/checks/impl/velocity/ExplosionHandler;
/*    */     //   242: aload_1
/*    */     //   243: getfield lastTransactionReceived : Ljava/util/concurrent/atomic/AtomicInteger;
/*    */     //   246: invokevirtual get : ()I
/*    */     //   249: invokevirtual getFirstBreadAddedExplosion : (I)Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   252: putfield firstBreadExplosion : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   255: aload_1
/*    */     //   256: aload_1
/*    */     //   257: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   260: invokevirtual getExplosionHandler : ()Lac/grim/grimac/checks/impl/velocity/ExplosionHandler;
/*    */     //   263: aload_1
/*    */     //   264: getfield lastTransactionReceived : Ljava/util/concurrent/atomic/AtomicInteger;
/*    */     //   267: invokevirtual get : ()I
/*    */     //   270: iconst_0
/*    */     //   271: invokevirtual getPossibleExplosions : (IZ)Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   274: putfield likelyExplosions : Lac/grim/grimac/utils/data/VelocityData;
/*    */     //   277: aload_1
/*    */     //   278: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   281: invokevirtual getExplosionHandler : ()Lac/grim/grimac/checks/impl/velocity/ExplosionHandler;
/*    */     //   284: invokevirtual forceExempt : ()V
/*    */     //   287: aload_1
/*    */     //   288: getfield checkManager : Lac/grim/grimac/manager/CheckManager;
/*    */     //   291: invokevirtual getKnockbackHandler : ()Lac/grim/grimac/checks/impl/velocity/KnockbackHandler;
/*    */     //   294: invokevirtual forceExempt : ()V
/*    */     //   297: aload_1
/*    */     //   298: aload_1
/*    */     //   299: getfield x : D
/*    */     //   302: putfield lastX : D
/*    */     //   305: aload_1
/*    */     //   306: aload_1
/*    */     //   307: getfield y : D
/*    */     //   310: putfield lastY : D
/*    */     //   313: aload_1
/*    */     //   314: aload_1
/*    */     //   315: getfield z : D
/*    */     //   318: putfield lastZ : D
/*    */     //   321: aload_1
/*    */     //   322: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*    */     //   325: getfield self : Lac/grim/grimac/utils/data/packetentity/PacketEntitySelf;
/*    */     //   328: invokevirtual getRiding : ()Lac/grim/grimac/utils/data/packetentity/PacketEntity;
/*    */     //   331: invokevirtual getPossibleCollisionBoxes : ()Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*    */     //   334: astore #4
/*    */     //   336: aload_1
/*    */     //   337: aload #4
/*    */     //   339: getfield minX : D
/*    */     //   342: aload #4
/*    */     //   344: getfield maxX : D
/*    */     //   347: dadd
/*    */     //   348: ldc2_w 2.0
/*    */     //   351: ddiv
/*    */     //   352: putfield x : D
/*    */     //   355: aload_1
/*    */     //   356: aload #4
/*    */     //   358: getfield minY : D
/*    */     //   361: aload #4
/*    */     //   363: getfield maxY : D
/*    */     //   366: dadd
/*    */     //   367: ldc2_w 2.0
/*    */     //   370: ddiv
/*    */     //   371: putfield y : D
/*    */     //   374: aload_1
/*    */     //   375: aload #4
/*    */     //   377: getfield minZ : D
/*    */     //   380: aload #4
/*    */     //   382: getfield maxZ : D
/*    */     //   385: dadd
/*    */     //   386: ldc2_w 2.0
/*    */     //   389: ddiv
/*    */     //   390: putfield z : D
/*    */     //   393: aload_1
/*    */     //   394: getfield isSprinting : Z
/*    */     //   397: aload_1
/*    */     //   398: getfield lastSprinting : Z
/*    */     //   401: if_icmpeq -> 415
/*    */     //   404: aload_1
/*    */     //   405: getfield compensatedEntities : Lac/grim/grimac/utils/latency/CompensatedEntities;
/*    */     //   408: aload_1
/*    */     //   409: getfield isSprinting : Z
/*    */     //   412: putfield hasSprintingAttributeEnabled : Z
/*    */     //   415: aload_1
/*    */     //   416: aload_1
/*    */     //   417: getfield isSprinting : Z
/*    */     //   420: putfield lastSprinting : Z
/*    */     //   423: aload_1
/*    */     //   424: getfield packetStateData : Lac/grim/grimac/utils/data/PacketStateData;
/*    */     //   427: iconst_1
/*    */     //   428: putfield receivedSteerVehicle : Z
/*    */     //   431: return
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #93	-> 0
/*    */     //   #98	-> 11
/*    */     //   #101	-> 25
/*    */     //   #102	-> 63
/*    */     //   #104	-> 79
/*    */     //   #106	-> 91
/*    */     //   #107	-> 111
/*    */     //   #111	-> 112
/*    */     //   #112	-> 119
/*    */     //   #115	-> 126
/*    */     //   #118	-> 134
/*    */     //   #119	-> 153
/*    */     //   #120	-> 175
/*    */     //   #123	-> 198
/*    */     //   #124	-> 205
/*    */     //   #126	-> 216
/*    */     //   #127	-> 223
/*    */     //   #130	-> 234
/*    */     //   #131	-> 255
/*    */     //   #134	-> 277
/*    */     //   #135	-> 287
/*    */     //   #141	-> 297
/*    */     //   #142	-> 305
/*    */     //   #143	-> 313
/*    */     //   #145	-> 321
/*    */     //   #147	-> 336
/*    */     //   #148	-> 355
/*    */     //   #149	-> 374
/*    */     //   #151	-> 393
/*    */     //   #152	-> 404
/*    */     //   #154	-> 415
/*    */     //   #157	-> 423
/*    */     //   #158	-> 431
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   51	7	3	horse	Lac/grim/grimac/utils/data/packetentity/PacketEntityHorse;
/*    */     //   153	270	3	controllingEntityId	I
/*    */     //   336	87	4	vehiclePos	Lac/grim/grimac/utils/collisions/datatypes/SimpleCollisionBox;
/*    */     //   0	432	0	this	Lac/grim/grimac/events/packets/PacketPlayerSteer;
/*    */     //   0	432	1	player	Lac/grim/grimac/player/GrimPlayer;
/*    */     //   11	421	2	riding	Lac/grim/grimac/utils/data/packetentity/PacketEntity;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerSteer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.manager;
/*     */ import ac.grim.grimac.api.AbstractCheck;
/*     */ import ac.grim.grimac.checks.impl.aim.AimDuplicateLook;
/*     */ import ac.grim.grimac.checks.impl.aim.AimModulo360;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsA;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsB;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsD;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsI;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsJ;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsK;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsL;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsM;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsN;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsO;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsP;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsQ;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsR;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsS;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsT;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsU;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsV;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsW;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsX;
/*     */ import ac.grim.grimac.checks.impl.breaking.InvalidBreak;
/*     */ import ac.grim.grimac.checks.impl.breaking.NoSwingBreak;
/*     */ import ac.grim.grimac.checks.impl.breaking.RotationBreak;
/*     */ import ac.grim.grimac.checks.impl.groundspoof.NoFall;
/*     */ import ac.grim.grimac.checks.impl.misc.GhostBlockMitigation;
/*     */ import ac.grim.grimac.checks.impl.misc.Post;
/*     */ import ac.grim.grimac.checks.impl.misc.TransactionOrder;
/*     */ import ac.grim.grimac.checks.impl.movement.NoSlow;
/*     */ import ac.grim.grimac.checks.impl.movement.PredictionRunner;
/*     */ import ac.grim.grimac.checks.impl.movement.VehiclePredictionRunner;
/*     */ import ac.grim.grimac.checks.impl.prediction.DebugHandler;
/*     */ import ac.grim.grimac.checks.impl.prediction.GroundSpoof;
/*     */ import ac.grim.grimac.checks.impl.prediction.OffsetHandler;
/*     */ import ac.grim.grimac.checks.impl.scaffolding.FabricatedPlace;
/*     */ import ac.grim.grimac.checks.impl.timer.TimerLimit;
/*     */ import ac.grim.grimac.checks.impl.velocity.ExplosionHandler;
/*     */ import ac.grim.grimac.checks.impl.velocity.KnockbackHandler;
/*     */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*     */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*     */ import ac.grim.grimac.checks.type.PacketCheck;
/*     */ import ac.grim.grimac.checks.type.PositionCheck;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.checks.type.RotationCheck;
/*     */ import ac.grim.grimac.checks.type.VehicleCheck;
/*     */ import ac.grim.grimac.events.packets.PacketChangeGameState;
/*     */ import ac.grim.grimac.events.packets.PacketEntityReplication;
/*     */ import ac.grim.grimac.manager.init.start.SuperDebug;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*     */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*     */ import ac.grim.grimac.utils.latency.CompensatedCooldown;
/*     */ import ac.grim.grimac.utils.team.TeamHandler;
/*     */ import com.google.common.collect.ClassToInstanceMap;
/*     */ import com.google.common.collect.ImmutableClassToInstanceMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CheckManager {
/*  66 */   private static final AtomicBoolean initedAtomic = new AtomicBoolean(false);
/*     */   private static boolean inited;
/*     */   public final ClassToInstanceMap<AbstractCheck> allChecks;
/*     */   private final ClassToInstanceMap<PacketCheck> packetChecks;
/*     */   private final ClassToInstanceMap<PositionCheck> positionChecks;
/*     */   private final ClassToInstanceMap<RotationCheck> rotationChecks;
/*     */   private final ClassToInstanceMap<VehicleCheck> vehicleChecks;
/*     */   private final ClassToInstanceMap<PacketCheck> prePredictionChecks;
/*     */   private final ClassToInstanceMap<BlockBreakCheck> blockBreakChecks;
/*     */   private final ClassToInstanceMap<BlockPlaceCheck> blockPlaceChecks;
/*     */   private final ClassToInstanceMap<PostPredictionCheck> postPredictionChecks;
/*  77 */   private PacketEntityReplication packetEntityReplication = null;
/*     */   
/*     */   public CheckManager(GrimPlayer player) {
/*  80 */     this
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
/* 140 */       .packetChecks = (ClassToInstanceMap<PacketCheck>)(new ImmutableClassToInstanceMap.Builder()).put(PacketOrderProcessor.class, player.packetOrderProcessor).put(Reach.class, new Reach(player)).put(PacketEntityReplication.class, new PacketEntityReplication(player)).put(PacketChangeGameState.class, new PacketChangeGameState(player)).put(CompensatedInventory.class, player.inventory).put(PacketPlayerAbilities.class, new PacketPlayerAbilities(player)).put(PacketWorldBorder.class, new PacketWorldBorder(player)).put(ActionManager.class, player.actionManager).put(TeamHandler.class, new TeamHandler(player)).put(ClientBrand.class, new ClientBrand(player)).put(NoFall.class, new NoFall(player)).put(ChatA.class, new ChatA(player)).put(ChatB.class, new ChatB(player)).put(ChatC.class, new ChatC(player)).put(ChatD.class, new ChatD(player)).put(ExploitA.class, new ExploitA(player)).put(ExploitB.class, new ExploitB(player)).put(BadPacketsA.class, new BadPacketsA(player)).put(BadPacketsB.class, new BadPacketsB(player)).put(BadPacketsC.class, new BadPacketsC(player)).put(BadPacketsD.class, new BadPacketsD(player)).put(BadPacketsE.class, new BadPacketsE(player)).put(BadPacketsF.class, new BadPacketsF(player)).put(BadPacketsG.class, new BadPacketsG(player)).put(BadPacketsI.class, new BadPacketsI(player)).put(BadPacketsJ.class, new BadPacketsJ(player)).put(BadPacketsK.class, new BadPacketsK(player)).put(BadPacketsL.class, new BadPacketsL(player)).put(BadPacketsM.class, new BadPacketsM(player)).put(BadPacketsO.class, new BadPacketsO(player)).put(BadPacketsP.class, new BadPacketsP(player)).put(BadPacketsQ.class, new BadPacketsQ(player)).put(BadPacketsR.class, new BadPacketsR(player)).put(BadPacketsS.class, new BadPacketsS(player)).put(BadPacketsT.class, new BadPacketsT(player)).put(BadPacketsU.class, new BadPacketsU(player)).put(BadPacketsV.class, new BadPacketsV(player)).put(BadPacketsY.class, new BadPacketsY(player)).put(MultiActionsA.class, new MultiActionsA(player)).put(MultiActionsC.class, new MultiActionsC(player)).put(MultiActionsD.class, new MultiActionsD(player)).put(MultiActionsE.class, new MultiActionsE(player)).put(PacketOrderB.class, new PacketOrderB(player)).put(PacketOrderC.class, new PacketOrderC(player)).put(PacketOrderD.class, new PacketOrderD(player)).put(PacketOrderO.class, new PacketOrderO(player)).put(SprintA.class, new SprintA(player)).put(VehicleA.class, new VehicleA(player)).put(VehicleB.class, new VehicleB(player)).put(VehicleD.class, new VehicleD(player)).put(VehicleE.class, new VehicleE(player)).put(VehicleF.class, new VehicleF(player)).put(CrashB.class, new CrashB(player)).put(CrashD.class, new CrashD(player)).put(CrashE.class, new CrashE(player)).put(CrashF.class, new CrashF(player)).put(CrashH.class, new CrashH(player)).put(CrashI.class, new CrashI(player)).put(SetbackBlocker.class, new SetbackBlocker(player)).build();
/*     */     
/* 142 */     this
/*     */ 
/*     */       
/* 145 */       .positionChecks = (ClassToInstanceMap<PositionCheck>)(new ImmutableClassToInstanceMap.Builder()).put(PredictionRunner.class, new PredictionRunner(player)).put(CompensatedCooldown.class, new CompensatedCooldown(player)).build();
/* 146 */     this
/*     */ 
/*     */ 
/*     */       
/* 150 */       .rotationChecks = (ClassToInstanceMap<RotationCheck>)(new ImmutableClassToInstanceMap.Builder()).put(AimProcessor.class, new AimProcessor(player)).put(AimModulo360.class, new AimModulo360(player)).put(AimDuplicateLook.class, new AimDuplicateLook(player)).build();
/* 151 */     this
/*     */       
/* 153 */       .vehicleChecks = (ClassToInstanceMap<VehicleCheck>)(new ImmutableClassToInstanceMap.Builder()).put(VehiclePredictionRunner.class, new VehiclePredictionRunner(player)).build();
/*     */     
/* 155 */     this
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 199 */       .postPredictionChecks = (ClassToInstanceMap<PostPredictionCheck>)(new ImmutableClassToInstanceMap.Builder()).put(NegativeTimer.class, new NegativeTimer(player)).put(ExplosionHandler.class, new ExplosionHandler(player)).put(KnockbackHandler.class, new KnockbackHandler(player)).put(GhostBlockDetector.class, new GhostBlockDetector(player)).put(Phase.class, new Phase(player)).put(Post.class, new Post(player)).put(PacketOrderA.class, new PacketOrderA(player)).put(PacketOrderE.class, new PacketOrderE(player)).put(PacketOrderF.class, new PacketOrderF(player)).put(PacketOrderG.class, new PacketOrderG(player)).put(PacketOrderH.class, new PacketOrderH(player)).put(PacketOrderI.class, new PacketOrderI(player)).put(PacketOrderJ.class, new PacketOrderJ(player)).put(PacketOrderK.class, new PacketOrderK(player)).put(PacketOrderL.class, new PacketOrderL(player)).put(PacketOrderM.class, new PacketOrderM(player)).put(GroundSpoof.class, new GroundSpoof(player)).put(OffsetHandler.class, new OffsetHandler(player)).put(SuperDebug.class, new SuperDebug(player)).put(DebugHandler.class, new DebugHandler(player)).put(BadPacketsX.class, new BadPacketsX(player)).put(NoSlow.class, new NoSlow(player)).put(SprintB.class, new SprintB(player)).put(SprintC.class, new SprintC(player)).put(SprintD.class, new SprintD(player)).put(SprintE.class, new SprintE(player)).put(SprintF.class, new SprintF(player)).put(SprintG.class, new SprintG(player)).put(MultiInteractA.class, new MultiInteractA(player)).put(MultiInteractB.class, new MultiInteractB(player)).put(ElytraA.class, new ElytraA(player)).put(ElytraB.class, new ElytraB(player)).put(ElytraC.class, new ElytraC(player)).put(ElytraD.class, new ElytraD(player)).put(ElytraE.class, new ElytraE(player)).put(ElytraF.class, new ElytraF(player)).put(ElytraG.class, new ElytraG(player)).put(ElytraH.class, new ElytraH(player)).put(ElytraI.class, new ElytraI(player)).put(SetbackTeleportUtil.class, new SetbackTeleportUtil(player)).put(CompensatedFireworks.class, player.fireworks).put(SneakingEstimator.class, new SneakingEstimator(player)).put(LastInstanceManager.class, player.lastInstanceManager).build();
/*     */     
/* 201 */     this
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
/* 217 */       .blockPlaceChecks = (ClassToInstanceMap<BlockPlaceCheck>)(new ImmutableClassToInstanceMap.Builder()).put(InvalidPlaceA.class, new InvalidPlaceA(player)).put(InvalidPlaceB.class, new InvalidPlaceB(player)).put(AirLiquidPlace.class, new AirLiquidPlace(player)).put(MultiPlace.class, new MultiPlace(player)).put(MultiActionsF.class, new MultiActionsF(player)).put(MultiActionsG.class, new MultiActionsG(player)).put(BadPacketsH.class, new BadPacketsH(player)).put(CrashG.class, new CrashG(player)).put(FarPlace.class, new FarPlace(player)).put(FabricatedPlace.class, new FabricatedPlace(player)).put(PositionPlace.class, new PositionPlace(player)).put(RotationPlace.class, new RotationPlace(player)).put(PacketOrderN.class, new PacketOrderN(player)).put(DuplicateRotPlace.class, new DuplicateRotPlace(player)).put(GhostBlockMitigation.class, new GhostBlockMitigation(player)).build();
/*     */     
/* 219 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 226 */       .prePredictionChecks = (ClassToInstanceMap<PacketCheck>)(new ImmutableClassToInstanceMap.Builder()).put(Timer.class, new Timer(player)).put(TickTimer.class, new TickTimer(player)).put(TimerLimit.class, new TimerLimit(player)).put(CrashA.class, new CrashA(player)).put(CrashC.class, new CrashC(player)).put(VehicleTimer.class, new VehicleTimer(player)).build();
/*     */     
/* 228 */     this
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
/* 240 */       .blockBreakChecks = (ClassToInstanceMap<BlockBreakCheck>)(new ImmutableClassToInstanceMap.Builder()).put(AirLiquidBreak.class, new AirLiquidBreak(player)).put(WrongBreak.class, new WrongBreak(player)).put(RotationBreak.class, new RotationBreak(player)).put(FastBreak.class, new FastBreak(player)).put(MultiBreak.class, new MultiBreak(player)).put(NoSwingBreak.class, new NoSwingBreak(player)).put(FarBreak.class, new FarBreak(player)).put(InvalidBreak.class, new InvalidBreak(player)).put(PositionBreakA.class, new PositionBreakA(player)).put(PositionBreakB.class, new PositionBreakB(player)).put(MultiActionsB.class, new MultiActionsB(player)).build();
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
/* 251 */     ImmutableClassToInstanceMap immutableClassToInstanceMap = (new ImmutableClassToInstanceMap.Builder()).put(BadPacketsN.class, new BadPacketsN(player)).put(BadPacketsW.class, new BadPacketsW(player)).put(TransactionOrder.class, new TransactionOrder(player)).put(VehicleC.class, new VehicleC(player)).put(Hitboxes.class, new Hitboxes(player)).build();
/*     */     
/* 253 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 263 */       .allChecks = (ClassToInstanceMap<AbstractCheck>)(new ImmutableClassToInstanceMap.Builder()).putAll((Map)this.packetChecks).putAll((Map)this.positionChecks).putAll((Map)this.rotationChecks).putAll((Map)this.vehicleChecks).putAll((Map)this.postPredictionChecks).putAll((Map)this.blockPlaceChecks).putAll((Map)this.prePredictionChecks).putAll((Map)this.blockBreakChecks).putAll((Map)immutableClassToInstanceMap).build();
/*     */     
/* 265 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends AbstractCheck> T getCheck(Class<T> check) {
/* 270 */     return (T)this.allChecks.get(check);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends PositionCheck> T getPositionCheck(Class<T> check) {
/* 275 */     return (T)this.positionChecks.get(check);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends RotationCheck> T getRotationCheck(Class<T> check) {
/* 280 */     return (T)this.rotationChecks.get(check);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends BlockPlaceCheck> T getBlockPlaceCheck(Class<T> check) {
/* 285 */     return (T)this.blockPlaceChecks.get(check);
/*     */   }
/*     */   
/*     */   public void onPrePredictionReceivePacket(PacketReceiveEvent packet) {
/* 289 */     for (PacketCheck check : this.prePredictionChecks.values()) {
/* 290 */       check.onPacketReceive(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent packet) {
/* 295 */     for (PacketCheck check : this.packetChecks.values()) {
/* 296 */       check.onPacketReceive(packet);
/*     */     }
/* 298 */     for (PostPredictionCheck check : this.postPredictionChecks.values()) {
/* 299 */       check.onPacketReceive(packet);
/*     */     }
/* 301 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 302 */       check.onPacketReceive(packet);
/*     */     }
/* 304 */     for (BlockBreakCheck check : this.blockBreakChecks.values()) {
/* 305 */       check.onPacketReceive(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPacketSend(PacketSendEvent packet) {
/* 310 */     for (PacketCheck check : this.prePredictionChecks.values()) {
/* 311 */       check.onPacketSend(packet);
/*     */     }
/* 313 */     for (PacketCheck check : this.packetChecks.values()) {
/* 314 */       check.onPacketSend(packet);
/*     */     }
/* 316 */     for (PostPredictionCheck check : this.postPredictionChecks.values()) {
/* 317 */       check.onPacketSend(packet);
/*     */     }
/* 319 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 320 */       check.onPacketSend(packet);
/*     */     }
/* 322 */     for (BlockBreakCheck check : this.blockBreakChecks.values()) {
/* 323 */       check.onPacketSend(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPositionUpdate(PositionUpdate position) {
/* 328 */     for (PositionCheck check : this.positionChecks.values()) {
/* 329 */       check.onPositionUpdate(position);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onRotationUpdate(RotationUpdate rotation) {
/* 334 */     for (RotationCheck check : this.rotationChecks.values()) {
/* 335 */       check.process(rotation);
/*     */     }
/* 337 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 338 */       check.process(rotation);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onVehiclePositionUpdate(VehiclePositionUpdate update) {
/* 343 */     for (VehicleCheck check : this.vehicleChecks.values()) {
/* 344 */       check.process(update);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPredictionFinish(PredictionComplete complete) {
/* 349 */     for (PostPredictionCheck check : this.postPredictionChecks.values()) {
/* 350 */       check.onPredictionComplete(complete);
/*     */     }
/* 352 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 353 */       check.onPredictionComplete(complete);
/*     */     }
/* 355 */     for (BlockBreakCheck check : this.blockBreakChecks.values()) {
/* 356 */       check.onPredictionComplete(complete);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onBlockPlace(BlockPlace place) {
/* 361 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 362 */       check.onBlockPlace(place);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPostFlyingBlockPlace(BlockPlace place) {
/* 367 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 368 */       check.onPostFlyingBlockPlace(place);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onBlockBreak(BlockBreak blockBreak) {
/* 373 */     for (BlockBreakCheck check : this.blockBreakChecks.values()) {
/* 374 */       check.onBlockBreak(blockBreak);
/*     */     }
/* 376 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 377 */       check.onBlockBreak(blockBreak);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPostFlyingBlockBreak(BlockBreak blockBreak) {
/* 382 */     for (BlockBreakCheck check : this.blockBreakChecks.values()) {
/* 383 */       check.onPostFlyingBlockBreak(blockBreak);
/*     */     }
/* 385 */     for (BlockPlaceCheck check : this.blockPlaceChecks.values()) {
/* 386 */       check.onPostFlyingBlockBreak(blockBreak);
/*     */     }
/*     */   }
/*     */   
/*     */   public ExplosionHandler getExplosionHandler() {
/* 391 */     return getPostPredictionCheck(ExplosionHandler.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends PacketCheck> T getPacketCheck(Class<T> check) {
/* 396 */     return (T)this.packetChecks.get(check);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends PacketCheck> T getPrePredictionCheck(Class<T> check) {
/* 401 */     return (T)this.prePredictionChecks.get(check);
/*     */   }
/*     */   
/*     */   public PacketEntityReplication getEntityReplication() {
/* 405 */     if (this.packetEntityReplication == null)
/* 406 */       this.packetEntityReplication = getPacketCheck(PacketEntityReplication.class); 
/* 407 */     return this.packetEntityReplication;
/*     */   }
/*     */   
/*     */   public NoFall getNoFall() {
/* 411 */     return getPacketCheck(NoFall.class);
/*     */   }
/*     */   
/*     */   public KnockbackHandler getKnockbackHandler() {
/* 415 */     return getPostPredictionCheck(KnockbackHandler.class);
/*     */   }
/*     */   
/*     */   public CompensatedCooldown getCompensatedCooldown() {
/* 419 */     return getPositionCheck(CompensatedCooldown.class);
/*     */   }
/*     */   
/*     */   public NoSlow getNoSlow() {
/* 423 */     return getPostPredictionCheck(NoSlow.class);
/*     */   }
/*     */   
/*     */   public SetbackTeleportUtil getSetbackUtil() {
/* 427 */     return getPostPredictionCheck(SetbackTeleportUtil.class);
/*     */   }
/*     */   
/*     */   public DebugHandler getDebugHandler() {
/* 431 */     return getPostPredictionCheck(DebugHandler.class);
/*     */   }
/*     */   
/*     */   public OffsetHandler getOffsetHandler() {
/* 435 */     return getPostPredictionCheck(OffsetHandler.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends PostPredictionCheck> T getPostPredictionCheck(Class<T> check) {
/* 440 */     return (T)this.postPredictionChecks.get(check);
/*     */   }
/*     */   
/*     */   private void init() {
/* 444 */     if (inited || initedAtomic.getAndSet(true))
/* 445 */       return;  inited = true;
/*     */     
/* 447 */     String[] permissions = { "grim.exempt.", "grim.nosetback.", "grim.nomodifypacket." };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 453 */     for (AbstractCheck check : this.allChecks.values()) {
/* 454 */       if (check.getConfigName() == null)
/* 455 */         continue;  String id = check.getConfigName().toLowerCase();
/* 456 */       for (String permissionName : permissions) {
/* 457 */         permissionName = permissionName + permissionName;
/* 458 */         GrimAPI.INSTANCE.getPermissionManager().registerPermission(permissionName, PermissionDefaultValue.FALSE);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\CheckManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
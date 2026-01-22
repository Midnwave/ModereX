/*     */ package ac.grim.grimac.checks.impl.breaking;
/*     */ 
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import ac.grim.grimac.utils.nmsutil.BlockBreakSpeed;
/*     */ import ac.grim.grimac.utils.reflection.ViaVersionUtil;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @CheckData(name = "FastBreak", description = "Breaking blocks too quickly")
/*     */ public class FastBreak
/*     */   extends Check
/*     */   implements BlockBreakCheck
/*     */ {
/*  32 */   private static final Set<StateType> EXEMPT_STATES = Set.of();
/*  33 */   private final boolean clientOlderThanServer = (PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion() > this.player.getClientVersion().getProtocolVersion()); Vector3i targetBlockPosition; double maximumBlockDamage; long lastFinishBreak;
/*     */   
/*     */   public FastBreak(GrimPlayer playerData) {
/*  36 */     super(playerData);
/*     */ 
/*     */ 
/*     */     
/*  40 */     this.targetBlockPosition = null;
/*     */ 
/*     */     
/*  43 */     this.maximumBlockDamage = 0.0D;
/*     */     
/*  45 */     this.lastFinishBreak = 0L;
/*     */     
/*  47 */     this.startBreak = 0L;
/*     */ 
/*     */     
/*  50 */     this.blockBreakBalance = 0.0D;
/*  51 */     this.blockDelayBalance = 0.0D;
/*     */   }
/*     */   long startBreak; double blockBreakBalance; double blockDelayBalance;
/*     */   public void onBlockBreak(BlockBreak blockBreak) {
/*  55 */     if (blockBreak.action == DiggingAction.START_DIGGING) {
/*  56 */       if (!ViaVersionUtil.isAvailable) {
/*     */         
/*  58 */         WrappedBlockState defaultState = WrappedBlockState.getDefaultState(this.player.getClientVersion(), blockBreak.block.getType());
/*  59 */         if (defaultState.getType() == StateTypes.AIR || EXEMPT_STATES.contains(defaultState.getType())) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  68 */       WrappedBlockState block = this.clientOlderThanServer ? WrappedBlockState.getByGlobalId(this.player.getClientVersion(), this.player.getViaTranslatedClientBlockID(blockBreak.block.getGlobalId())) : blockBreak.block;
/*     */       
/*  70 */       this.startBreak = System.currentTimeMillis() - ((this.targetBlockPosition == null) ? 50L : 0L);
/*  71 */       this.targetBlockPosition = blockBreak.position;
/*     */       
/*  73 */       this.maximumBlockDamage = BlockBreakSpeed.getBlockDamage(this.player, block);
/*     */       
/*  75 */       double breakDelay = (System.currentTimeMillis() - this.lastFinishBreak);
/*     */       
/*  77 */       if (breakDelay >= 275.0D) {
/*  78 */         this.blockDelayBalance *= 0.9D;
/*     */       } else {
/*  80 */         this.blockDelayBalance += 300.0D - breakDelay;
/*     */       } 
/*     */       
/*  83 */       if (this.blockDelayBalance > 1000.0D && 
/*  84 */         flagAndAlert("delay=" + breakDelay + "ms, type=" + String.valueOf(blockBreak.block.getType())) && shouldModifyPackets()) {
/*  85 */         blockBreak.cancel();
/*     */       }
/*     */ 
/*     */       
/*  89 */       clampBalance();
/*     */     } 
/*     */     
/*  92 */     if (blockBreak.action == DiggingAction.FINISHED_DIGGING && this.targetBlockPosition != null) {
/*  93 */       double predictedTime = Math.ceil(1.0D / this.maximumBlockDamage) * 50.0D;
/*  94 */       double realTime = (System.currentTimeMillis() - this.startBreak);
/*  95 */       double diff = predictedTime - realTime;
/*     */       
/*  97 */       clampBalance();
/*     */       
/*  99 */       if (diff < 25.0D) {
/* 100 */         this.blockBreakBalance *= 0.9D;
/*     */       } else {
/* 102 */         this.blockBreakBalance += diff;
/*     */       } 
/*     */       
/* 105 */       if (this.blockBreakBalance > 1000.0D && 
/* 106 */         flagAndAlert("diff=" + diff + "ms, balance=" + this.blockBreakBalance + "ms, type=" + String.valueOf(blockBreak.block.getType())) && shouldModifyPackets()) {
/* 107 */         blockBreak.cancel();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 112 */       this.lastFinishBreak = this.startBreak = System.currentTimeMillis();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   4: invokevirtual getClientVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   7: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion.V_1_9 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;
/*     */     //   10: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/player/ClientVersion;)Z
/*     */     //   13: ifeq -> 29
/*     */     //   16: aload_1
/*     */     //   17: invokevirtual getPacketType : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/packettype/PacketTypeCommon;
/*     */     //   20: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/packettype/PacketType$Play$Client.ANIMATION : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/packettype/PacketType$Play$Client;
/*     */     //   23: if_acmpne -> 78
/*     */     //   26: goto -> 39
/*     */     //   29: aload_1
/*     */     //   30: invokevirtual getPacketType : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/packettype/PacketTypeCommon;
/*     */     //   33: invokestatic isFlying : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/packettype/PacketTypeCommon;)Z
/*     */     //   36: ifeq -> 78
/*     */     //   39: aload_0
/*     */     //   40: getfield targetBlockPosition : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/util/Vector3i;
/*     */     //   43: ifnull -> 78
/*     */     //   46: aload_0
/*     */     //   47: aload_0
/*     */     //   48: getfield maximumBlockDamage : D
/*     */     //   51: aload_0
/*     */     //   52: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   55: aload_0
/*     */     //   56: getfield player : Lac/grim/grimac/player/GrimPlayer;
/*     */     //   59: getfield compensatedWorld : Lac/grim/grimac/utils/latency/CompensatedWorld;
/*     */     //   62: aload_0
/*     */     //   63: getfield targetBlockPosition : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/util/Vector3i;
/*     */     //   66: invokevirtual getBlock : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/util/Vector3i;)Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/WrappedBlockState;
/*     */     //   69: invokestatic getBlockDamage : (Lac/grim/grimac/player/GrimPlayer;Lac/grim/grimac/shaded/com/github/retrooper/packetevents/protocol/world/states/WrappedBlockState;)D
/*     */     //   72: invokestatic max : (DD)D
/*     */     //   75: putfield maximumBlockDamage : D
/*     */     //   78: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #120	-> 0
/*     */     //   #121	-> 46
/*     */     //   #123	-> 78
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	79	0	this	Lac/grim/grimac/checks/impl/breaking/FastBreak;
/*     */     //   0	79	1	event	Lac/grim/grimac/shaded/com/github/retrooper/packetevents/event/PacketReceiveEvent;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void clampBalance() {
/* 126 */     double balance = Math.max(1000, this.player.getTransactionPing());
/* 127 */     this.blockBreakBalance = GrimMath.clamp(this.blockBreakBalance, -balance, balance);
/* 128 */     this.blockDelayBalance = GrimMath.clamp(this.blockDelayBalance, -balance, balance);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\FastBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
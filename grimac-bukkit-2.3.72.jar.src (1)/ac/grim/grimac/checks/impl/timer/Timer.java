/*     */ package ac.grim.grimac.checks.impl.timer;
/*     */ 
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.PacketCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*     */ 
/*     */ @CheckData(name = "Timer", configName = "TimerA", setback = 10.0D)
/*     */ public class Timer extends Check implements PacketCheck {
/*  14 */   long timerBalanceRealTime = 0L;
/*     */ 
/*     */   
/*  17 */   long knownPlayerClockTime = (long)(System.nanoTime() - 6.0E10D);
/*  18 */   long lastMovementPlayerClock = (long)(System.nanoTime() - 6.0E10D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long clockDrift;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasGottenMovementAfterTransaction = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer(GrimPlayer player) {
/*  54 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  59 */     if (this.hasGottenMovementAfterTransaction && checkForTransaction(event.getPacketType())) {
/*  60 */       this.knownPlayerClockTime = this.lastMovementPlayerClock;
/*  61 */       this.lastMovementPlayerClock = this.player.getPlayerClockAtLeast();
/*  62 */       this.hasGottenMovementAfterTransaction = false;
/*     */     } 
/*     */     
/*  65 */     if (!shouldCountPacketForTimer(event.getPacketType()))
/*     */       return; 
/*  67 */     this.hasGottenMovementAfterTransaction = true;
/*  68 */     this.timerBalanceRealTime = (long)(this.timerBalanceRealTime + 5.0E7D);
/*     */     
/*  70 */     doCheck(event);
/*     */   }
/*     */   
/*     */   public void doCheck(PacketReceiveEvent event) {
/*  74 */     if (this.timerBalanceRealTime > System.nanoTime()) {
/*  75 */       if (flagAndAlert()) {
/*     */         
/*  77 */         if (shouldModifyPackets()) {
/*  78 */           event.setCancelled(true);
/*  79 */           this.player.onPacketCancel();
/*     */         } 
/*     */         
/*  82 */         if (shouldSetback()) {
/*  83 */           this.player.getSetbackTeleportUtil().executeNonSimulatingSetback();
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  88 */       this.timerBalanceRealTime = (long)(this.timerBalanceRealTime - 5.0E7D);
/*     */     } 
/*     */     
/*  91 */     limitFallBehind();
/*     */   }
/*     */   
/*     */   protected void limitFallBehind() {
/*  95 */     this.timerBalanceRealTime = Math.max(this.timerBalanceRealTime, this.lastMovementPlayerClock - this.clockDrift);
/*     */   }
/*     */   
/*     */   public boolean checkForTransaction(PacketTypeCommon packetType) {
/*  99 */     return (packetType == PacketType.Play.Client.PONG || packetType == PacketType.Play.Client.WINDOW_CONFIRMATION);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldCountPacketForTimer(PacketTypeCommon packetType) {
/* 105 */     return isTickPacket(packetType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 110 */     this.clockDrift = (long)(config.getDoubleElse(getConfigName() + ".drift", 120.0D) * 1000000.0D);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\timer\Timer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.checks.impl.velocity;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.data.Pair;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.VelocityData;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Objects;
/*     */ import lombok.Generated;
/*     */ 
/*     */ @CheckData(name = "AntiKB", alternativeName = "AntiKnockback", configName = "Knockback", setback = 10.0D, decay = 0.025D)
/*     */ public class KnockbackHandler
/*     */   extends Check implements PostPredictionCheck {
/*  27 */   Deque<VelocityData> firstBreadMap = new LinkedList<>();
/*     */   
/*  29 */   Deque<VelocityData> lastKnockbackKnownTaken = new LinkedList<>();
/*  30 */   VelocityData firstBreadOnlyKnockback = null; boolean knockbackPointThree = false; @Generated
/*  31 */   public boolean isKnockbackPointThree() { return this.knockbackPointThree; }
/*     */   
/*     */   double offsetToFlag; double maxAdv;
/*     */   double immediate;
/*     */   double ceiling;
/*     */   double multiplier;
/*     */   double threshold;
/*     */   
/*     */   public KnockbackHandler(GrimPlayer player) {
/*  40 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  45 */     if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
/*  46 */       WrapperPlayServerEntityVelocity velocity = new WrapperPlayServerEntityVelocity(event);
/*  47 */       int entityId = velocity.getEntityId();
/*     */       
/*  49 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  50 */       if (player == null) {
/*     */         return;
/*     */       }
/*     */       
/*  54 */       if (player.compensatedEntities.serverPlayerVehicle != null && entityId != player.compensatedEntities.serverPlayerVehicle.intValue()) {
/*     */         return;
/*     */       }
/*  57 */       if (player.compensatedEntities.serverPlayerVehicle == null && entityId != player.entityID) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  63 */       Vector3d playerVelocity = velocity.getVelocity();
/*     */ 
/*     */       
/*  66 */       if (playerVelocity.getY() == -0.04D) {
/*  67 */         velocity.setVelocity(playerVelocity.add(new Vector3d(0.0D, 1.25E-4D, 0.0D)));
/*  68 */         playerVelocity = velocity.getVelocity();
/*  69 */         event.markForReEncode(true);
/*     */       } 
/*     */ 
/*     */       
/*  73 */       player.sendTransaction();
/*  74 */       addPlayerKnockback(entityId, player.lastTransactionSent.get(), new Vector3dm(playerVelocity.getX(), playerVelocity.getY(), playerVelocity.getZ()));
/*  75 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Pair<VelocityData, Vector3dm> getFutureKnockback() {
/*  82 */     if (!this.firstBreadMap.isEmpty()) {
/*  83 */       VelocityData data = this.firstBreadMap.peek();
/*  84 */       return new Pair(data, (data != null) ? data.vector : null);
/*     */     } 
/*     */ 
/*     */     
/*  88 */     if (!this.lastKnockbackKnownTaken.isEmpty()) {
/*  89 */       VelocityData data = this.lastKnockbackKnownTaken.peek();
/*  90 */       return new Pair(data, (data != null) ? data.vector : null);
/*     */     } 
/*     */ 
/*     */     
/*  94 */     if (this.player.firstBreadKB != null && this.player.likelyKB == null) {
/*  95 */       VelocityData data = this.player.firstBreadKB;
/*  96 */       return new Pair(data, data.vector.clone());
/*  97 */     }  if (this.player.likelyKB != null) {
/*  98 */       VelocityData data = this.player.likelyKB;
/*  99 */       return new Pair(data, data.vector.clone());
/*     */     } 
/* 101 */     return new Pair(null, null);
/*     */   }
/*     */   
/*     */   private void addPlayerKnockback(int entityID, int breadOne, Vector3dm knockback) {
/* 105 */     this.firstBreadMap.add(new VelocityData(entityID, breadOne, (this.player.getSetbackTeleportUtil()).isSendingSetback, knockback));
/*     */   }
/*     */   
/*     */   public VelocityData calculateRequiredKB(int entityID, int transaction, boolean isJustTesting) {
/* 109 */     tickKnockback(transaction);
/*     */     
/* 111 */     VelocityData returnLastKB = null;
/* 112 */     for (VelocityData data : this.lastKnockbackKnownTaken) {
/* 113 */       if (data.entityID == entityID) {
/* 114 */         returnLastKB = data;
/*     */       }
/*     */     } 
/* 117 */     if (!isJustTesting) {
/* 118 */       this.lastKnockbackKnownTaken.clear();
/*     */     }
/* 120 */     return returnLastKB;
/*     */   }
/*     */   
/*     */   private void tickKnockback(int transactionID) {
/* 124 */     this.firstBreadOnlyKnockback = null;
/* 125 */     if (this.firstBreadMap.isEmpty())
/* 126 */       return;  VelocityData data = this.firstBreadMap.peek();
/* 127 */     while (data != null) {
/* 128 */       if (data.transaction == transactionID) {
/* 129 */         this.firstBreadOnlyKnockback = new VelocityData(data.entityID, data.transaction, data.isSetback, data.vector);
/*     */         break;
/*     */       } 
/* 132 */       if (data.transaction < transactionID) {
/* 133 */         if (this.firstBreadOnlyKnockback != null) {
/* 134 */           this.lastKnockbackKnownTaken.add(new VelocityData(data.entityID, data.transaction, data.vector, data.isSetback, data.offset));
/*     */         } else {
/* 136 */           this.lastKnockbackKnownTaken.add(new VelocityData(data.entityID, data.transaction, data.isSetback, data.vector));
/*     */         } 
/*     */ 
/*     */         
/* 140 */         this.firstBreadOnlyKnockback = null;
/* 141 */         this.firstBreadMap.poll();
/* 142 */         data = this.firstBreadMap.peek();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceExempt() {
/* 151 */     if (this.player.firstBreadKB != null) {
/* 152 */       this.player.firstBreadKB.offset = 0.0D;
/*     */     }
/*     */     
/* 155 */     if (this.player.likelyKB != null) {
/* 156 */       this.player.likelyKB.offset = 0.0D;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setPointThree(boolean isPointThree) {
/* 161 */     this.knockbackPointThree = (this.knockbackPointThree || isPointThree);
/*     */   }
/*     */   
/*     */   public void handlePredictionAnalysis(double offset) {
/* 165 */     if (this.player.firstBreadKB != null) {
/* 166 */       this.player.firstBreadKB.offset = Math.min(this.player.firstBreadKB.offset, offset);
/*     */     }
/*     */     
/* 169 */     if (this.player.likelyKB != null) {
/* 170 */       this.player.likelyKB.offset = Math.min(this.player.likelyKB.offset, offset);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 176 */     double offset = predictionComplete.getOffset();
/* 177 */     if (!predictionComplete.isChecked() || predictionComplete.getData().isTeleport()) {
/* 178 */       forceExempt();
/*     */       
/*     */       return;
/*     */     } 
/* 182 */     boolean wasZero = this.knockbackPointThree;
/* 183 */     this.knockbackPointThree = false;
/*     */     
/* 185 */     if (this.player.likelyKB == null && this.player.firstBreadKB == null) {
/*     */       return;
/*     */     }
/*     */     
/* 189 */     if (this.player.predictedVelocity.isFirstBreadKb()) {
/* 190 */       this.firstBreadOnlyKnockback = null;
/* 191 */       this.firstBreadMap.poll();
/*     */     } 
/*     */     
/* 194 */     if (wasZero || this.player.predictedVelocity.isKnockback()) {
/*     */       
/* 196 */       if (this.player.firstBreadKB != null) {
/* 197 */         this.player.firstBreadKB.offset = Math.min(this.player.firstBreadKB.offset, offset);
/*     */       }
/*     */ 
/*     */       
/* 201 */       if (this.player.likelyKB != null) {
/* 202 */         this.player.likelyKB.offset = Math.min(this.player.likelyKB.offset, offset);
/*     */       }
/*     */     } 
/*     */     
/* 206 */     if (this.player.likelyKB != null) {
/* 207 */       if (this.player.likelyKB.offset > this.offsetToFlag) {
/* 208 */         this.threshold = Math.min(this.threshold + this.player.likelyKB.offset, this.ceiling);
/* 209 */         if (this.player.likelyKB.isSetback) {
/* 210 */           if (!isNoSetbackPermission()) {
/* 211 */             this.player.getSetbackTeleportUtil().executeViolationSetback();
/*     */           }
/* 213 */         } else if (flagAndAlert((this.player.likelyKB.offset == 2.147483647E9D) ? "ignored knockback" : ("o: " + 
/* 214 */             formatOffset(this.player.likelyKB.offset)))) {
/* 215 */           if (this.player.likelyKB.offset >= this.immediate || this.threshold >= this.maxAdv) {
/* 216 */             setbackIfAboveSetbackVL();
/*     */           }
/*     */         } else {
/* 219 */           reward();
/*     */         } 
/* 221 */       } else if (this.threshold > 0.05D) {
/* 222 */         this.threshold *= this.multiplier;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean shouldIgnoreForPrediction(VectorData data) {
/* 228 */     if (data.isKnockback() && data.isFirstBreadKb()) {
/* 229 */       return (this.player.firstBreadKB.offset > this.offsetToFlag);
/*     */     }
/* 231 */     return false;
/*     */   }
/*     */   
/*     */   public boolean wouldFlag() {
/* 235 */     return ((this.player.likelyKB != null && this.player.likelyKB.offset > this.offsetToFlag) || (this.player.firstBreadKB != null && this.player.firstBreadKB.offset > this.offsetToFlag));
/*     */   }
/*     */   
/*     */   public VelocityData calculateFirstBreadKnockback(int entityID, int transaction) {
/* 239 */     tickKnockback(transaction);
/* 240 */     if (this.firstBreadOnlyKnockback != null && this.firstBreadOnlyKnockback.entityID == entityID)
/* 241 */       return this.firstBreadOnlyKnockback; 
/* 242 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 247 */     this.offsetToFlag = config.getDoubleElse("Knockback.threshold", 0.001D);
/* 248 */     this.maxAdv = config.getDoubleElse("Knockback.max-advantage", 1.0D);
/* 249 */     this.immediate = config.getDoubleElse("Knockback.immediate-setback-threshold", 0.1D);
/* 250 */     this.multiplier = config.getDoubleElse("Knockback.setback-decay-multiplier", 0.999D);
/* 251 */     this.ceiling = config.getDoubleElse("Knockback.max-ceiling", 4.0D);
/* 252 */     if (this.maxAdv < 0.0D) this.maxAdv = Double.MAX_VALUE; 
/* 253 */     if (this.immediate < 0.0D) this.immediate = Double.MAX_VALUE; 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\velocity\KnockbackHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.checks.impl.velocity;
/*     */ 
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import ac.grim.grimac.utils.data.VelocityData;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Objects;
/*     */ import lombok.Generated;
/*     */ 
/*     */ @CheckData(name = "AntiExplosion", configName = "Explosion", setback = 10.0D)
/*     */ public class ExplosionHandler
/*     */   extends Check implements PostPredictionCheck {
/*  32 */   Deque<VelocityData> firstBreadMap = new LinkedList<>();
/*     */   
/*  34 */   VelocityData lastExplosionsKnownTaken = null;
/*  35 */   VelocityData firstBreadAddedExplosion = null; @Generated
/*     */   public boolean isExplosionPointThree() {
/*  37 */     return this.explosionPointThree;
/*     */   }
/*     */   boolean explosionPointThree = false;
/*     */   double offsetToFlag;
/*     */   double setbackVL;
/*     */   
/*     */   public ExplosionHandler(GrimPlayer player) {
/*  44 */     super(player);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  49 */     if (event.getPacketType() == PacketType.Play.Server.EXPLOSION) {
/*  50 */       WrapperPlayServerExplosion explosion = new WrapperPlayServerExplosion(event);
/*     */ 
/*     */       
/*  53 */       boolean hasBlocks = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21_2);
/*  54 */       if (hasBlocks) {
/*  55 */         handleBlockExplosions(explosion);
/*     */       }
/*     */       
/*  58 */       Vector3d velocity = explosion.getKnockback();
/*  59 */       if (velocity != null && (velocity.x != 0.0D || velocity.y != 0.0D || velocity.z != 0.0D)) {
/*     */         
/*  61 */         if (!hasBlocks || explosion.getRecords().isEmpty()) this.player.sendTransaction(); 
/*  62 */         addPlayerExplosion(this.player.lastTransactionSent.get(), velocity);
/*  63 */         Objects.requireNonNull(this.player); event.getTasksAfterSend().add(this.player::sendTransaction);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleBlockExplosions(WrapperPlayServerExplosion explosion) {
/*  69 */     WrapperPlayServerExplosion.BlockInteraction blockInteraction = explosion.getBlockInteraction();
/*  70 */     boolean shouldDestroy = (blockInteraction != WrapperPlayServerExplosion.BlockInteraction.KEEP_BLOCKS);
/*  71 */     if (explosion.getRecords().isEmpty() || !shouldDestroy) {
/*     */       return;
/*     */     }
/*     */     
/*  75 */     this.player.sendTransaction();
/*     */     
/*  77 */     this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
/*     */           for (Vector3i record : explosion.getRecords()) {
/*     */             if (blockInteraction != WrapperPlayServerExplosion.BlockInteraction.TRIGGER_BLOCKS) {
/*     */               this.player.compensatedWorld.updateBlock(record.x, record.y, record.z, 0);
/*     */               
/*     */               continue;
/*     */             } 
/*     */             
/*     */             WrappedBlockState state = this.player.compensatedWorld.getBlock(record);
/*     */             
/*     */             StateType type = state.getType();
/*     */             if (BlockTags.CANDLES.contains(type) || BlockTags.CANDLE_CAKES.contains(type)) {
/*     */               state.setLit(false);
/*     */               continue;
/*     */             } 
/*     */             if (type == StateTypes.BELL) {
/*     */               continue;
/*     */             }
/*     */             Object poweredValue = state.getInternalData().get(StateValue.POWERED);
/*  96 */             boolean canFlip = ((poweredValue != null && !((Boolean)poweredValue).booleanValue()) || type == StateTypes.LEVER);
/*     */             if (canFlip) {
/*     */               this.player.compensatedWorld.tickOpenable(record.x, record.y, record.z);
/*     */             }
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VelocityData getFutureExplosion() {
/* 107 */     if (!this.firstBreadMap.isEmpty()) {
/* 108 */       return this.firstBreadMap.peek();
/*     */     }
/*     */ 
/*     */     
/* 112 */     if (this.lastExplosionsKnownTaken != null) {
/* 113 */       return this.lastExplosionsKnownTaken;
/*     */     }
/*     */ 
/*     */     
/* 117 */     if (this.player.firstBreadExplosion != null && this.player.likelyExplosions == null)
/* 118 */       return this.player.firstBreadExplosion; 
/* 119 */     if (this.player.likelyExplosions != null) {
/* 120 */       return this.player.likelyExplosions;
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */   
/*     */   public boolean shouldIgnoreForPrediction(VectorData data) {
/* 126 */     if (data.isExplosion() && data.isFirstBreadExplosion()) {
/* 127 */       return (this.player.firstBreadExplosion.offset > this.offsetToFlag);
/*     */     }
/* 129 */     return false;
/*     */   }
/*     */   
/*     */   public boolean wouldFlag() {
/* 133 */     return ((this.player.likelyExplosions != null && this.player.likelyExplosions.offset > this.offsetToFlag) || (this.player.firstBreadExplosion != null && this.player.firstBreadExplosion.offset > this.offsetToFlag));
/*     */   }
/*     */   
/*     */   public void addPlayerExplosion(int breadOne, Vector3d explosion) {
/* 137 */     this.firstBreadMap.add(new VelocityData(-1, breadOne, (this.player.getSetbackTeleportUtil()).isSendingSetback, new Vector3dm(explosion.getX(), explosion.getY(), explosion.getZ())));
/*     */   }
/*     */   
/*     */   public void setPointThree(boolean isPointThree) {
/* 141 */     this.explosionPointThree = (this.explosionPointThree || isPointThree);
/*     */   }
/*     */   
/*     */   public void handlePredictionAnalysis(double offset) {
/* 145 */     if (this.player.firstBreadExplosion != null) {
/* 146 */       this.player.firstBreadExplosion.offset = Math.min(this.player.firstBreadExplosion.offset, offset);
/*     */     }
/*     */     
/* 149 */     if (this.player.likelyExplosions != null) {
/* 150 */       this.player.likelyExplosions.offset = Math.min(this.player.likelyExplosions.offset, offset);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void forceExempt() {
/* 156 */     if (this.player.firstBreadExplosion != null) {
/* 157 */       this.player.firstBreadExplosion.offset = 0.0D;
/*     */     }
/*     */     
/* 160 */     if (this.player.likelyExplosions != null) {
/* 161 */       this.player.likelyExplosions.offset = 0.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 167 */     double offset = predictionComplete.getOffset();
/*     */     
/* 169 */     boolean wasZero = this.explosionPointThree;
/* 170 */     this.explosionPointThree = false;
/*     */     
/* 172 */     if (this.player.likelyExplosions == null && this.player.firstBreadExplosion == null) {
/* 173 */       this.firstBreadAddedExplosion = null;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 184 */     int minTrans = Math.min((this.player.likelyExplosions != null) ? this.player.likelyExplosions.transaction : Integer.MAX_VALUE, 
/* 185 */         (this.player.firstBreadExplosion != null) ? this.player.firstBreadExplosion.transaction : Integer.MAX_VALUE);
/* 186 */     int kbTrans = Math.max((this.player.likelyKB != null) ? this.player.likelyKB.transaction : Integer.MIN_VALUE, 
/* 187 */         (this.player.firstBreadKB != null) ? this.player.firstBreadKB.transaction : Integer.MIN_VALUE);
/*     */     
/* 189 */     if (this.player.predictedVelocity.isFirstBreadExplosion()) {
/* 190 */       this.firstBreadAddedExplosion = null;
/* 191 */       this.firstBreadMap.poll();
/*     */     } 
/*     */     
/* 194 */     if (wasZero || this.player.predictedVelocity.isExplosion() || minTrans < kbTrans) {
/*     */ 
/*     */       
/* 197 */       if (this.player.firstBreadExplosion != null) {
/* 198 */         this.player.firstBreadExplosion.offset = Math.min(this.player.firstBreadExplosion.offset, offset);
/*     */       }
/*     */       
/* 201 */       if (this.player.likelyExplosions != null) {
/* 202 */         this.player.likelyExplosions.offset = Math.min(this.player.likelyExplosions.offset, offset);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 207 */     if (this.player.likelyExplosions != null && !this.player.compensatedEntities.self.isDead) {
/* 208 */       if (this.player.likelyExplosions.offset > this.offsetToFlag) {
/* 209 */         flagAndAlertWithSetback((this.player.likelyExplosions.offset == 2.147483647E9D) ? "ignored explosion" : ("o: " + formatOffset(offset)));
/*     */       } else {
/* 211 */         reward();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public VelocityData getPossibleExplosions(int lastTransaction, boolean isJustTesting) {
/* 217 */     handleTransactionPacket(lastTransaction);
/* 218 */     if (this.lastExplosionsKnownTaken == null) {
/* 219 */       return null;
/*     */     }
/* 221 */     VelocityData returnLastExplosion = this.lastExplosionsKnownTaken;
/* 222 */     if (!isJustTesting) {
/* 223 */       this.lastExplosionsKnownTaken = null;
/*     */     }
/* 225 */     return returnLastExplosion;
/*     */   }
/*     */   
/*     */   private void handleTransactionPacket(int transactionID) {
/* 229 */     VelocityData data = this.firstBreadMap.peek();
/* 230 */     while (data != null) {
/* 231 */       if (data.transaction == transactionID) {
/* 232 */         if (this.lastExplosionsKnownTaken != null) {
/* 233 */           this.firstBreadAddedExplosion = new VelocityData(-1, data.transaction, data.isSetback, this.lastExplosionsKnownTaken.vector.clone().add(data.vector)); break;
/*     */         } 
/* 235 */         this.firstBreadAddedExplosion = new VelocityData(-1, data.transaction, data.isSetback, data.vector); break;
/*     */       } 
/* 237 */       if (data.transaction < transactionID) {
/* 238 */         if (this.lastExplosionsKnownTaken != null) {
/* 239 */           this.lastExplosionsKnownTaken.vector.add(data.vector);
/*     */         } else {
/* 241 */           this.lastExplosionsKnownTaken = new VelocityData(-1, data.transaction, data.isSetback, data.vector);
/*     */         } 
/*     */ 
/*     */         
/* 245 */         this.firstBreadAddedExplosion = null;
/* 246 */         this.firstBreadMap.poll();
/* 247 */         data = this.firstBreadMap.peek();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VelocityData getFirstBreadAddedExplosion(int lastTransaction) {
/* 255 */     handleTransactionPacket(lastTransaction);
/* 256 */     return this.firstBreadAddedExplosion;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 261 */     this.offsetToFlag = config.getDoubleElse("Explosion.threshold", 1.0E-5D);
/* 262 */     this.setbackVL = config.getDoubleElse("Explosion.setbackvl", 10.0D);
/* 263 */     if (this.setbackVL == -1.0D) this.setbackVL = Double.MAX_VALUE; 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\velocity\ExplosionHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
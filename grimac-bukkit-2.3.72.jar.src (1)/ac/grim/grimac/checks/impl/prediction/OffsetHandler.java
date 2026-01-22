/*     */ package ac.grim.grimac.checks.impl.prediction;
/*     */ import ac.grim.grimac.api.AbstractCheck;
/*     */ import ac.grim.grimac.api.GrimUser;
/*     */ import ac.grim.grimac.api.config.ConfigManager;
/*     */ import ac.grim.grimac.api.event.GrimEvent;
/*     */ import ac.grim.grimac.api.event.events.CompletePredictionEvent;
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.CheckData;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ @CheckData(name = "Simulation", decay = 0.02D)
/*     */ public class OffsetHandler extends Check implements PostPredictionCheck {
/*  16 */   private static final AtomicInteger flags = new AtomicInteger(0);
/*     */   
/*     */   double setbackDecayMultiplier;
/*     */   
/*     */   double threshold;
/*     */   double immediateSetbackThreshold;
/*     */   double maxAdvantage;
/*     */   double maxCeiling;
/*     */   double setbackViolationThreshold;
/*  25 */   double advantageGained = 0.0D;
/*     */   
/*     */   public OffsetHandler(GrimPlayer player) {
/*  28 */     super(player);
/*     */   }
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  32 */     if (!predictionComplete.isChecked())
/*     */       return; 
/*  34 */     double offset = predictionComplete.getOffset();
/*     */     
/*  36 */     CompletePredictionEvent completePredictionEvent = new CompletePredictionEvent((GrimUser)this.player, (AbstractCheck)this, offset);
/*  37 */     GrimAPI.INSTANCE.getEventBus().post((GrimEvent)completePredictionEvent);
/*     */     
/*  39 */     if (completePredictionEvent.isCancelled())
/*     */       return; 
/*  41 */     if (offset >= this.threshold || offset >= this.immediateSetbackThreshold) {
/*  42 */       this.advantageGained += offset;
/*  43 */       giveOffsetLenienceNextTick(offset);
/*     */       
/*  45 */       synchronized (flags) {
/*  46 */         String humanFormattedOffset; int flagId = (flags.get() & 0xFF) + 1;
/*     */ 
/*     */         
/*  49 */         if (offset < 0.001D) {
/*  50 */           humanFormattedOffset = String.format("%.4E", new Object[] { Double.valueOf(offset) });
/*     */           
/*  52 */           humanFormattedOffset = humanFormattedOffset.replace("E-0", "E-");
/*     */         } else {
/*     */           
/*  55 */           humanFormattedOffset = String.format("%6f", new Object[] { Double.valueOf(offset) });
/*     */           
/*  57 */           humanFormattedOffset = humanFormattedOffset.replace("0.", ".");
/*     */         } 
/*     */         
/*  60 */         String verbose = humanFormattedOffset + " /gl " + humanFormattedOffset;
/*  61 */         if (flag(verbose)) {
/*  62 */           if (alert(verbose)) {
/*  63 */             flags.incrementAndGet();
/*  64 */             predictionComplete.setIdentifier(flagId);
/*     */           } 
/*     */           
/*  67 */           if ((this.advantageGained >= this.maxAdvantage || offset >= this.immediateSetbackThreshold) && 
/*  68 */             !isNoSetbackPermission() && this.violations >= this.setbackViolationThreshold)
/*     */           {
/*  70 */             this.player.getSetbackTeleportUtil().executeViolationSetback();
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/*  75 */       this.advantageGained = Math.min(this.advantageGained, this.maxCeiling);
/*     */     } else {
/*  77 */       this.advantageGained *= this.setbackDecayMultiplier;
/*     */     } 
/*     */     
/*  80 */     removeOffsetLenience();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void giveOffsetLenienceNextTick(double offset) {
/*  88 */     double minimizedOffset = Math.min(offset, 1.0D);
/*     */ 
/*     */     
/*  91 */     this.player.uncertaintyHandler.lastHorizontalOffset = minimizedOffset;
/*  92 */     this.player.uncertaintyHandler.lastVerticalOffset = minimizedOffset;
/*     */   }
/*     */   
/*     */   private void removeOffsetLenience() {
/*  96 */     this.player.uncertaintyHandler.lastHorizontalOffset = 0.0D;
/*  97 */     this.player.uncertaintyHandler.lastVerticalOffset = 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onReload(ConfigManager config) {
/* 102 */     this.setbackDecayMultiplier = config.getDoubleElse("Simulation.setback-decay-multiplier", 0.999D);
/* 103 */     this.threshold = config.getDoubleElse("Simulation.threshold", 0.001D);
/* 104 */     this.immediateSetbackThreshold = config.getDoubleElse("Simulation.immediate-setback-threshold", 0.1D);
/* 105 */     this.maxAdvantage = config.getDoubleElse("Simulation.max-advantage", 1.0D);
/* 106 */     this.maxCeiling = config.getDoubleElse("Simulation.max-ceiling", 4.0D);
/* 107 */     this.setbackViolationThreshold = config.getDoubleElse("Simulation.setback-violation-threshold", 1.0D);
/* 108 */     if (this.maxAdvantage == -1.0D) this.maxAdvantage = Double.MAX_VALUE; 
/* 109 */     if (this.immediateSetbackThreshold == -1.0D) this.immediateSetbackThreshold = Double.MAX_VALUE; 
/*     */   }
/*     */   
/*     */   public boolean doesOffsetFlag(double offset) {
/* 113 */     return (offset >= this.threshold);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\prediction\OffsetHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
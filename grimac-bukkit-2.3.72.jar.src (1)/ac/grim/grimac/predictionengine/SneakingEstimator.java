/*     */ package ac.grim.grimac.predictionengine;
/*     */ 
/*     */ import ac.grim.grimac.checks.Check;
/*     */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.VectorData;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import lombok.Generated;
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
/*     */ public class SneakingEstimator
/*     */   extends Check
/*     */   implements PostPredictionCheck
/*     */ {
/*  35 */   SimpleCollisionBox sneakingPotentialHiddenVelocity = new SimpleCollisionBox(); @Generated public SimpleCollisionBox getSneakingPotentialHiddenVelocity() { return this.sneakingPotentialHiddenVelocity; }
/*     */   
/*  37 */   List<VectorData> possible = new ArrayList<>();
/*     */   
/*     */   public SneakingEstimator(GrimPlayer player) {
/*  40 */     super(player);
/*     */   }
/*     */   
/*     */   public void storePossibleVelocities(List<VectorData> possible) {
/*  44 */     this.possible = possible;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/*  49 */     if (!predictionComplete.isChecked())
/*     */       return; 
/*  51 */     double trueFriction = this.player.lastOnGround ? (this.player.friction * 0.91D) : 0.91D;
/*  52 */     if (this.player.wasTouchingLava) trueFriction = 0.5D; 
/*  53 */     if (this.player.wasTouchingWater) trueFriction = 0.96D; 
/*  54 */     if (this.player.isGliding) trueFriction = 0.99D;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     if (!this.player.uncertaintyHandler.stuckOnEdge.hasOccurredSince(0)) {
/*  60 */       this.sneakingPotentialHiddenVelocity = new SimpleCollisionBox();
/*     */       
/*     */       return;
/*     */     } 
/*  64 */     for (VectorData data : this.possible) {
/*     */       
/*  66 */       if (data.isJump() == this.player.predictedVelocity.isJump() && data.isKnockback() == this.player.predictedVelocity.isKnockback() && data.isExplosion() == this.player.predictedVelocity.isExplosion()) {
/*     */         
/*  68 */         if (this.player.uncertaintyHandler.lastStuckWest.hasOccurredSince(0) || this.player.uncertaintyHandler.lastStuckNorth.hasOccurredSince(0)) {
/*  69 */           this.sneakingPotentialHiddenVelocity.minX = Math.min(this.sneakingPotentialHiddenVelocity.minX, data.vector.getX());
/*  70 */           this.sneakingPotentialHiddenVelocity.minZ = Math.min(this.sneakingPotentialHiddenVelocity.minZ, data.vector.getZ());
/*     */         } 
/*     */         
/*  73 */         if (this.player.uncertaintyHandler.lastStuckEast.hasOccurredSince(0) || this.player.uncertaintyHandler.lastStuckSouth.hasOccurredSince(0)) {
/*  74 */           this.sneakingPotentialHiddenVelocity.maxX = Math.max(this.sneakingPotentialHiddenVelocity.maxX, data.vector.getX());
/*  75 */           this.sneakingPotentialHiddenVelocity.maxZ = Math.max(this.sneakingPotentialHiddenVelocity.maxZ, data.vector.getZ());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  82 */     this.sneakingPotentialHiddenVelocity.minX *= trueFriction;
/*  83 */     this.sneakingPotentialHiddenVelocity.minZ *= trueFriction;
/*  84 */     this.sneakingPotentialHiddenVelocity.maxX *= trueFriction;
/*  85 */     this.sneakingPotentialHiddenVelocity.maxZ *= trueFriction;
/*     */     
/*  87 */     this.sneakingPotentialHiddenVelocity.minX = Math.min(-0.15D, this.sneakingPotentialHiddenVelocity.minX);
/*  88 */     this.sneakingPotentialHiddenVelocity.minZ = Math.min(-0.15D, this.sneakingPotentialHiddenVelocity.minZ);
/*  89 */     this.sneakingPotentialHiddenVelocity.maxX = Math.max(0.15D, this.sneakingPotentialHiddenVelocity.maxX);
/*  90 */     this.sneakingPotentialHiddenVelocity.maxZ = Math.max(0.15D, this.sneakingPotentialHiddenVelocity.maxZ);
/*     */ 
/*     */     
/*  93 */     if (!this.player.uncertaintyHandler.lastStuckEast.hasOccurredSince(0)) {
/*  94 */       this.sneakingPotentialHiddenVelocity.maxX = 0.0D;
/*     */     }
/*  96 */     if (!this.player.uncertaintyHandler.lastStuckWest.hasOccurredSince(0)) {
/*  97 */       this.sneakingPotentialHiddenVelocity.minX = 0.0D;
/*     */     }
/*  99 */     if (!this.player.uncertaintyHandler.lastStuckNorth.hasOccurredSince(0)) {
/* 100 */       this.sneakingPotentialHiddenVelocity.minZ = 0.0D;
/*     */     }
/* 102 */     if (!this.player.uncertaintyHandler.lastStuckSouth.hasOccurredSince(0))
/* 103 */       this.sneakingPotentialHiddenVelocity.maxZ = 0.0D; 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\SneakingEstimator.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
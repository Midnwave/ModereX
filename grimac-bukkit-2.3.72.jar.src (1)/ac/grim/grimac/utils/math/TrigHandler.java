/*    */ package ac.grim.grimac.utils.math;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class TrigHandler {
/*    */   private final GrimPlayer player;
/*  9 */   private double buffer = 0.0D; @Generated
/* 10 */   public boolean isVanillaMath() { return this.isVanillaMath; }
/*    */   
/*    */   private boolean isVanillaMath = true;
/*    */   public TrigHandler(GrimPlayer player) {
/* 14 */     this.player = player;
/*    */   }
/*    */   
/*    */   public void toggleShitMath() {
/* 18 */     this.isVanillaMath = !this.isVanillaMath;
/*    */   }
/*    */   
/*    */   public Vector3dm getVanillaMathMovement(Vector3dm wantedMovement, float f, float f2) {
/* 22 */     float f3 = VanillaMath.sin(GrimMath.radians(f2));
/* 23 */     float f4 = VanillaMath.cos(GrimMath.radians(f2));
/*    */     
/* 25 */     float bestTheoreticalX = (float)(f3 * wantedMovement.getZ() + f4 * wantedMovement.getX()) / (f3 * f3 + f4 * f4) / f;
/* 26 */     float bestTheoreticalZ = (float)(-f3 * wantedMovement.getX() + f4 * wantedMovement.getZ()) / (f3 * f3 + f4 * f4) / f;
/*    */     
/* 28 */     return new Vector3dm(bestTheoreticalX, 0.0F, bestTheoreticalZ);
/*    */   }
/*    */   
/*    */   public Vector3dm getShitMathMovement(Vector3dm wantedMovement, float f, float f2) {
/* 32 */     float f3 = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.sin(GrimMath.radians(f2)) : LegacyFastMath.sin(GrimMath.radians(f2));
/* 33 */     float f4 = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.cos(GrimMath.radians(f2)) : LegacyFastMath.cos(GrimMath.radians(f2));
/*    */     
/* 35 */     float bestTheoreticalX = (float)(f3 * wantedMovement.getZ() + f4 * wantedMovement.getX()) / (f3 * f3 + f4 * f4) / f;
/* 36 */     float bestTheoreticalZ = (float)(-f3 * wantedMovement.getX() + f4 * wantedMovement.getZ()) / (f3 * f3 + f4 * f4) / f;
/*    */     
/* 38 */     return new Vector3dm(bestTheoreticalX, 0.0F, bestTheoreticalZ);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setOffset(double offset) {
/* 46 */     if (offset == 0.0D || offset > 0.001D) {
/*    */       return;
/*    */     }
/*    */     
/* 50 */     if (offset > 1.0E-5D) {
/* 51 */       Vector3dm trueMovement = this.player.actualMovement.clone().subtract(this.player.startTickClientVel);
/* 52 */       Vector3dm correctMath = getVanillaMathMovement(trueMovement, 0.1F, this.player.xRot);
/* 53 */       Vector3dm fastMath = getShitMathMovement(trueMovement, 0.1F, this.player.xRot);
/*    */       
/* 55 */       correctMath = new Vector3dm(Math.abs(correctMath.getX()), 0.0D, Math.abs(correctMath.getZ()));
/* 56 */       fastMath = new Vector3dm(Math.abs(fastMath.getX()), 0.0D, Math.abs(fastMath.getZ()));
/*    */       
/* 58 */       double minCorrectHorizontal = Math.min(correctMath.getX(), correctMath.getZ());
/*    */       
/* 60 */       minCorrectHorizontal = Math.min(minCorrectHorizontal, Math.abs(correctMath.getX() - correctMath.getZ()));
/*    */       
/* 62 */       double minFastMathHorizontal = Math.min(fastMath.getX(), fastMath.getZ());
/*    */       
/* 64 */       minFastMathHorizontal = Math.min(minFastMathHorizontal, Math.abs(fastMath.getX() - fastMath.getZ()));
/*    */       
/* 66 */       boolean newVanilla = (minCorrectHorizontal < minFastMathHorizontal);
/*    */       
/* 68 */       this.buffer += (newVanilla != this.isVanillaMath) ? 1.0D : -0.25D;
/*    */       
/* 70 */       if (this.buffer > 5.0D) {
/* 71 */         this.buffer = 0.0D;
/* 72 */         this.isVanillaMath = !this.isVanillaMath;
/*    */       } 
/*    */       
/* 75 */       this.buffer = Math.max(0.0D, this.buffer);
/*    */     } 
/*    */   }
/*    */   
/*    */   public float sin(float value) {
/* 80 */     return this.isVanillaMath ? VanillaMath.sin(value) : (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.sin(value) : LegacyFastMath.sin(value));
/*    */   }
/*    */   
/*    */   public float cos(float value) {
/* 84 */     return this.isVanillaMath ? VanillaMath.cos(value) : (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? OptifineFastMath.cos(value) : LegacyFastMath.cos(value));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\TrigHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
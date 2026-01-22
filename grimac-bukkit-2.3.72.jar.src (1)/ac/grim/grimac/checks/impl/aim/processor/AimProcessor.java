/*    */ package ac.grim.grimac.checks.impl.aim.processor;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.RotationCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*    */ import ac.grim.grimac.utils.data.Pair;
/*    */ import ac.grim.grimac.utils.lists.RunningMode;
/*    */ import ac.grim.grimac.utils.math.GrimMath;
/*    */ 
/*    */ 
/*    */ public class AimProcessor
/*    */   extends Check
/*    */   implements RotationCheck
/*    */ {
/*    */   private static final int SIGNIFICANT_SAMPLES_THRESHOLD = 15;
/*    */   private static final int TOTAL_SAMPLES_THRESHOLD = 80;
/*    */   public double sensitivityX;
/*    */   public double sensitivityY;
/*    */   public double divisorX;
/* 21 */   private final RunningMode xRotMode = new RunningMode(80); public double divisorY; public double modeX; public double modeY; public double deltaDotsX; public double deltaDotsY;
/* 22 */   private final RunningMode yRotMode = new RunningMode(80);
/*    */   private float lastXRot;
/*    */   private float lastYRot;
/*    */   
/*    */   public AimProcessor(GrimPlayer playerData) {
/* 27 */     super(playerData);
/*    */   }
/*    */   
/*    */   public static double convertToSensitivity(double var13) {
/* 31 */     double var11 = var13 / 0.15000000596046448D / 8.0D;
/* 32 */     double var9 = Math.cbrt(var11);
/* 33 */     return (var9 - 0.20000000298023224D) / 0.6000000238418579D;
/*    */   }
/*    */ 
/*    */   
/*    */   public void process(RotationUpdate rotationUpdate) {
/* 38 */     rotationUpdate.setProcessor(this);
/*    */     
/* 40 */     float deltaXRot = rotationUpdate.getDeltaXRotABS();
/*    */     
/* 42 */     this.divisorX = GrimMath.gcd(deltaXRot, this.lastXRot);
/* 43 */     if (deltaXRot > 0.0F && deltaXRot < 5.0F && this.divisorX > GrimMath.MINIMUM_DIVISOR) {
/* 44 */       this.xRotMode.add(this.divisorX);
/* 45 */       this.lastXRot = deltaXRot;
/*    */     } 
/*    */     
/* 48 */     float deltaYRot = rotationUpdate.getDeltaYRotABS();
/*    */     
/* 50 */     this.divisorY = GrimMath.gcd(deltaYRot, this.lastYRot);
/*    */     
/* 52 */     if (deltaYRot > 0.0F && deltaYRot < 5.0F && this.divisorY > GrimMath.MINIMUM_DIVISOR) {
/* 53 */       this.yRotMode.add(this.divisorY);
/* 54 */       this.lastYRot = deltaYRot;
/*    */     } 
/*    */     
/* 57 */     if (this.xRotMode.size() > 15) {
/* 58 */       Pair<Double, Integer> modeX = this.xRotMode.getMode();
/* 59 */       if (((Integer)modeX.second()).intValue() > 15) {
/* 60 */         this.modeX = ((Double)modeX.first()).doubleValue();
/* 61 */         this.sensitivityX = convertToSensitivity(this.modeX);
/*    */       } 
/*    */     } 
/* 64 */     if (this.yRotMode.size() > 15) {
/* 65 */       Pair<Double, Integer> modeY = this.yRotMode.getMode();
/* 66 */       if (((Integer)modeY.second()).intValue() > 15) {
/* 67 */         this.modeY = ((Double)modeY.first()).doubleValue();
/* 68 */         this.sensitivityY = convertToSensitivity(this.modeY);
/*    */       } 
/*    */     } 
/*    */     
/* 72 */     this.deltaDotsX = deltaXRot / this.modeX;
/* 73 */     this.deltaDotsY = deltaYRot / this.modeY;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\aim\processor\AimProcessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.utils.anticheat.update;
/*    */ public final class RotationUpdate {
/*    */   private HeadRotation from;
/*    */   private HeadRotation to;
/*    */   private AimProcessor processor;
/*    */   private float deltaYRot;
/*    */   
/*    */   @Generated
/*  9 */   public void setFrom(HeadRotation from) { this.from = from; } private float deltaXRot; private boolean isCinematic; private double sensitivityX; private double sensitivityY; @Generated public void setTo(HeadRotation to) { this.to = to; } @Generated public void setProcessor(AimProcessor processor) { this.processor = processor; } @Generated public void setDeltaYRot(float deltaYRot) { this.deltaYRot = deltaYRot; } @Generated public void setDeltaXRot(float deltaXRot) { this.deltaXRot = deltaXRot; } @Generated public void setCinematic(boolean isCinematic) { this.isCinematic = isCinematic; } @Generated public void setSensitivityX(double sensitivityX) { this.sensitivityX = sensitivityX; } @Generated public void setSensitivityY(double sensitivityY) { this.sensitivityY = sensitivityY; }
/*    */   @Generated
/* 11 */   public HeadRotation getFrom() { return this.from; } @Generated public HeadRotation getTo() { return this.to; } @Generated
/* 12 */   public AimProcessor getProcessor() { return this.processor; } @Generated
/* 13 */   public float getDeltaYRot() { return this.deltaYRot; } @Generated public float getDeltaXRot() { return this.deltaXRot; } @Generated
/* 14 */   public boolean isCinematic() { return this.isCinematic; } @Generated
/* 15 */   public double getSensitivityX() { return this.sensitivityX; } @Generated public double getSensitivityY() { return this.sensitivityY; }
/*    */   
/*    */   public RotationUpdate(HeadRotation from, HeadRotation to, float deltaXRot, float deltaYRot) {
/* 18 */     this.from = from;
/* 19 */     this.to = to;
/* 20 */     this.deltaXRot = deltaXRot;
/* 21 */     this.deltaYRot = deltaYRot;
/*    */   }
/*    */   
/*    */   public float getDeltaXRotABS() {
/* 25 */     return Math.abs(this.deltaXRot);
/*    */   }
/*    */   
/*    */   public float getDeltaYRotABS() {
/* 29 */     return Math.abs(this.deltaYRot);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\antichea\\update\RotationUpdate.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
/*    */ 
/*    */ @CheckData(name = "DuplicateRotPlace", experimental = true)
/*    */ public class DuplicateRotPlace extends BlockPlaceCheck {
/*    */   private float deltaX;
/*    */   private float deltaY;
/*    */   private float lastPlacedDeltaX;
/*    */   private double lastPlacedDeltaDotsX;
/*    */   private double deltaDotsX;
/*    */   private boolean rotated = false;
/*    */   
/*    */   public DuplicateRotPlace(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void process(RotationUpdate rotationUpdate) {
/* 24 */     this.deltaX = rotationUpdate.getDeltaXRotABS();
/* 25 */     this.deltaY = rotationUpdate.getDeltaYRotABS();
/* 26 */     this.deltaDotsX = (rotationUpdate.getProcessor()).deltaDotsX;
/* 27 */     this.rotated = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPostFlyingBlockPlace(BlockPlace place) {
/* 32 */     if (this.rotated && !this.player.inVehicle()) {
/* 33 */       if (this.deltaX > 2.0F) {
/* 34 */         float xDiff = Math.abs(this.deltaX - this.lastPlacedDeltaX);
/* 35 */         double xDiffDots = Math.abs(this.deltaDotsX - this.lastPlacedDeltaDotsX);
/*    */         
/* 37 */         if (xDiff < 1.0E-4D) {
/* 38 */           flagAndAlert("x=" + xDiff + " xdots=" + xDiffDots + " y=" + this.deltaY);
/*    */         } else {
/* 40 */           reward();
/*    */         } 
/*    */       } else {
/* 43 */         reward();
/*    */       } 
/* 45 */       this.lastPlacedDeltaX = this.deltaX;
/* 46 */       this.lastPlacedDeltaDotsX = this.deltaDotsX;
/* 47 */       this.rotated = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\DuplicateRotPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
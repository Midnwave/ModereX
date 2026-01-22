/*    */ package ac.grim.grimac.utils.anticheat.update;
/*    */ public class VehiclePositionUpdate {
/*    */   private final Vector3d from;
/*    */   private final Vector3d to;
/*    */   
/*    */   @Generated
/*    */   public VehiclePositionUpdate(Vector3d from, Vector3d to, float xRot, float yRot, boolean isTeleport) {
/*  8 */     this.from = from; this.to = to; this.xRot = xRot; this.yRot = yRot; this.isTeleport = isTeleport;
/*    */   } private final float xRot; private final float yRot; private final boolean isTeleport;
/*    */   @Generated
/*    */   public Vector3d getFrom() {
/* 12 */     return this.from; } @Generated public Vector3d getTo() { return this.to; } @Generated
/* 13 */   public float getXRot() { return this.xRot; } @Generated public float getYRot() { return this.yRot; } @Generated
/* 14 */   public boolean isTeleport() { return this.isTeleport; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\antichea\\update\VehiclePositionUpdate.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
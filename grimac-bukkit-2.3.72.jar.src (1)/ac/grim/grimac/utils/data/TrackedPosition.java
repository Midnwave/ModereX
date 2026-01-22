/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class TrackedPosition {
/*    */   private static final double MODERN_COORDINATE_SCALE = 4096.0D;
/*    */   private static final double LEGACY_COORDINATE_SCALE = 32.0D;
/*    */   private final double scale;
/*    */   
/*    */   @Generated
/*    */   public double getScale() {
/* 13 */     return this.scale;
/* 14 */   } private Vector3d pos = new Vector3d(); @Generated public void setPos(Vector3d pos) { this.pos = pos; } @Generated
/* 15 */   public Vector3d getPos() { return this.pos; }
/*    */ 
/*    */   
/*    */   public TrackedPosition() {
/* 19 */     this.scale = 4096.0D;
/*    */   }
/*    */   
/*    */   public static long pack(double value, double scale) {
/* 23 */     return Math.round(value * scale);
/*    */   }
/*    */   
/*    */   public static double packLegacy(double value, double scale) {
/* 27 */     return Math.floor(value * scale);
/*    */   }
/*    */   
/*    */   private double unpack(long value) {
/* 31 */     return value / this.scale;
/*    */   }
/*    */   
/*    */   private double unpackLegacy(double value) {
/* 35 */     return value / this.scale;
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3d withDelta(long x, long y, long z) {
/* 40 */     if (x == 0L && y == 0L && z == 0L) {
/* 41 */       return this.pos;
/*    */     }
/*    */     
/* 44 */     double d = (x == 0L) ? this.pos.x : unpack(pack(this.pos.x, this.scale) + x);
/* 45 */     double e = (y == 0L) ? this.pos.y : unpack(pack(this.pos.y, this.scale) + y);
/* 46 */     double f = (z == 0L) ? this.pos.z : unpack(pack(this.pos.z, this.scale) + z);
/* 47 */     return new Vector3d(d, e, f);
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3d withDeltaLegacy(double x, double y, double z) {
/* 52 */     double d = unpackLegacy(packLegacy(this.pos.x, this.scale) + x);
/* 53 */     double e = unpackLegacy(packLegacy(this.pos.y, this.scale) + y);
/* 54 */     double f = unpackLegacy(packLegacy(this.pos.z, this.scale) + z);
/* 55 */     return new Vector3d(d, e, f);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\TrackedPosition.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
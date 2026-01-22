/*    */ package ac.grim.grimac.utils.data;
/*    */ public class TrackerData { private double x; private double y; private double z;
/*    */   private float xRot;
/*    */   
/*    */   @Generated
/*  6 */   public void setX(double x) { this.x = x; } private float yRot; private EntityType entityType; private int lastTransactionHung; private int legacyPointEightMountedUpon; @Generated public void setY(double y) { this.y = y; } @Generated public void setZ(double z) { this.z = z; } @Generated public void setXRot(float xRot) { this.xRot = xRot; } @Generated public void setYRot(float yRot) { this.yRot = yRot; } @Generated public void setEntityType(EntityType entityType) { this.entityType = entityType; } @Generated public void setLastTransactionHung(int lastTransactionHung) { this.lastTransactionHung = lastTransactionHung; } @Generated public void setLegacyPointEightMountedUpon(int legacyPointEightMountedUpon) { this.legacyPointEightMountedUpon = legacyPointEightMountedUpon; } @Generated public boolean equals(Object o) { if (o == this) return true;  if (!(o instanceof TrackerData)) return false;  TrackerData other = (TrackerData)o; if (!other.canEqual(this)) return false;  if (Double.compare(getX(), other.getX()) != 0) return false;  if (Double.compare(getY(), other.getY()) != 0) return false;  if (Double.compare(getZ(), other.getZ()) != 0) return false;  if (Float.compare(getXRot(), other.getXRot()) != 0) return false;  if (Float.compare(getYRot(), other.getYRot()) != 0) return false;  if (getLastTransactionHung() != other.getLastTransactionHung()) return false;  if (getLegacyPointEightMountedUpon() != other.getLegacyPointEightMountedUpon()) return false;  Object this$entityType = getEntityType(), other$entityType = other.getEntityType(); return !((this$entityType == null) ? (other$entityType != null) : !this$entityType.equals(other$entityType)); } @Generated protected boolean canEqual(Object other) { return other instanceof TrackerData; } @Generated public int hashCode() { int PRIME = 59; result = 1; long $x = Double.doubleToLongBits(getX()); result = result * 59 + (int)($x >>> 32L ^ $x); long $y = Double.doubleToLongBits(getY()); result = result * 59 + (int)($y >>> 32L ^ $y); long $z = Double.doubleToLongBits(getZ()); result = result * 59 + (int)($z >>> 32L ^ $z); result = result * 59 + Float.floatToIntBits(getXRot()); result = result * 59 + Float.floatToIntBits(getYRot()); result = result * 59 + getLastTransactionHung(); result = result * 59 + getLegacyPointEightMountedUpon(); Object $entityType = getEntityType(); return result * 59 + (($entityType == null) ? 43 : $entityType.hashCode()); } @Generated public String toString() { return "TrackerData(x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", xRot=" + getXRot() + ", yRot=" + getYRot() + ", entityType=" + String.valueOf(getEntityType()) + ", lastTransactionHung=" + getLastTransactionHung() + ", legacyPointEightMountedUpon=" + getLegacyPointEightMountedUpon() + ")"; }
/*    */   @Generated
/*  8 */   public double getX() { return this.x; } @Generated public double getY() { return this.y; } @Generated public double getZ() { return this.z; } @Generated
/*  9 */   public float getXRot() { return this.xRot; } @Generated public float getYRot() { return this.yRot; } @Generated
/* 10 */   public EntityType getEntityType() { return this.entityType; } @Generated
/* 11 */   public int getLastTransactionHung() { return this.lastTransactionHung; } @Generated
/* 12 */   public int getLegacyPointEightMountedUpon() { return this.legacyPointEightMountedUpon; }
/*    */   
/*    */   public TrackerData(double x, double y, double z, float xRot, float yRot, EntityType entityType, int lastTransactionHung) {
/* 15 */     this.x = x;
/* 16 */     this.y = y;
/* 17 */     this.z = z;
/* 18 */     this.xRot = xRot;
/* 19 */     this.yRot = yRot;
/* 20 */     this.entityType = entityType;
/* 21 */     this.lastTransactionHung = lastTransactionHung;
/*    */   } }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\TrackerData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
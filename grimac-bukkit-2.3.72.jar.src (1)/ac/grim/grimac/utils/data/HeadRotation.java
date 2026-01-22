/*   */ package ac.grim.grimac.utils.data;
/*   */ public class HeadRotation {
/*   */   float yaw;
/*   */   
/*   */   @Generated
/* 6 */   public void setYaw(float yaw) { this.yaw = yaw; } float pitch; @Generated public void setPitch(float pitch) { this.pitch = pitch; } @Generated public boolean equals(Object o) { if (o == this) return true;  if (!(o instanceof HeadRotation)) return false;  HeadRotation other = (HeadRotation)o; return !other.canEqual(this) ? false : ((Float.compare(getYaw(), other.getYaw()) != 0) ? false : (!(Float.compare(getPitch(), other.getPitch()) != 0))); } @Generated protected boolean canEqual(Object other) { return other instanceof HeadRotation; } @Generated public int hashCode() { int PRIME = 59; result = 1; result = result * 59 + Float.floatToIntBits(getYaw()); return result * 59 + Float.floatToIntBits(getPitch()); } @Generated public String toString() { return "HeadRotation(yaw=" + getYaw() + ", pitch=" + getPitch() + ")"; } @Generated
/* 7 */   public HeadRotation(float yaw, float pitch) { this.yaw = yaw; this.pitch = pitch; }
/*   */   @Generated
/* 9 */   public float getYaw() { return this.yaw; } @Generated public float getPitch() { return this.pitch; }
/*   */ 
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\HeadRotation.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
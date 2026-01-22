/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ public final class RotationData {
/*    */   private final float yaw;
/*    */   private final float pitch;
/*    */   private final int transaction;
/*    */   private boolean isAccepted;
/*    */   
/*    */   @Generated
/* 10 */   public RotationData(float yaw, float pitch, int transaction) { this.yaw = yaw; this.pitch = pitch; this.transaction = transaction; } @Generated
/* 11 */   public boolean equals(Object o) { if (o == this) return true;  if (!(o instanceof RotationData)) return false;  RotationData other = (RotationData)o; return (Float.compare(getYaw(), other.getYaw()) != 0) ? false : ((Float.compare(getPitch(), other.getPitch()) != 0) ? false : ((getTransaction() != other.getTransaction()) ? false : (!(isAccepted() != other.isAccepted())))); } @Generated public int hashCode() { int PRIME = 59; result = 1; result = result * 59 + Float.floatToIntBits(getYaw()); result = result * 59 + Float.floatToIntBits(getPitch()); result = result * 59 + getTransaction(); return result * 59 + (isAccepted() ? 79 : 97); } @Generated
/* 12 */   public String toString() { return "RotationData(yaw=" + getYaw() + ", pitch=" + getPitch() + ", transaction=" + getTransaction() + ", isAccepted=" + isAccepted() + ")"; }
/*    */   @Generated
/* 14 */   public float getYaw() { return this.yaw; } @Generated
/* 15 */   public float getPitch() { return this.pitch; } @Generated
/* 16 */   public int getTransaction() { return this.transaction; } @Generated
/* 17 */   public boolean isAccepted() { return this.isAccepted; }
/*    */   
/*    */   @Contract(mutates = "this")
/*    */   public void accept() {
/* 21 */     this.isAccepted = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\RotationData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
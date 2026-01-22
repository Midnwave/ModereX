/*    */ package ac.grim.grimac.utils.data;
/*    */ public class SetBackData { TeleportData teleportData;
/*    */   float xRot;
/*    */   float yRot;
/*    */   Vector3dm velocity;
/*    */   boolean vehicle;
/*    */   
/*    */   @Generated
/*  9 */   public void setTeleportData(TeleportData teleportData) { this.teleportData = teleportData; } @Generated public void setXRot(float xRot) { this.xRot = xRot; } @Generated public void setYRot(float yRot) { this.yRot = yRot; } @Generated public void setVelocity(Vector3dm velocity) { this.velocity = velocity; } @Generated public void setVehicle(boolean vehicle) { this.vehicle = vehicle; } @Generated public void setComplete(boolean isComplete) { this.isComplete = isComplete; } @Generated public void setPlugin(boolean isPlugin) { this.isPlugin = isPlugin; } @Generated public void setTicksComplete(int ticksComplete) { this.ticksComplete = ticksComplete; } @Generated
/* 10 */   public String toString() { return "SetBackData(teleportData=" + String.valueOf(getTeleportData()) + ", xRot=" + getXRot() + ", yRot=" + getYRot() + ", velocity=" + String.valueOf(getVelocity()) + ", vehicle=" + isVehicle() + ", isComplete=" + isComplete() + ", isPlugin=" + isPlugin() + ", ticksComplete=" + getTicksComplete() + ")"; }
/*    */   @Generated
/* 12 */   public TeleportData getTeleportData() { return this.teleportData; } @Generated
/* 13 */   public float getXRot() { return this.xRot; } @Generated public float getYRot() { return this.yRot; } @Generated
/* 14 */   public Vector3dm getVelocity() { return this.velocity; } @Generated
/* 15 */   public boolean isVehicle() { return this.vehicle; } boolean isComplete = false; @Generated
/* 16 */   public boolean isComplete() { return this.isComplete; } boolean isPlugin; @Generated
/*    */   public boolean isPlugin() {
/* 18 */     return this.isPlugin;
/* 19 */   } int ticksComplete = 0; @Generated public int getTicksComplete() { return this.ticksComplete; }
/*    */   
/*    */   public SetBackData(TeleportData teleportData, float xRot, float yRot, Vector3dm velocity, boolean vehicle, boolean isPlugin) {
/* 22 */     this.teleportData = teleportData;
/* 23 */     this.xRot = xRot;
/* 24 */     this.yRot = yRot;
/* 25 */     this.velocity = velocity;
/* 26 */     this.vehicle = vehicle;
/* 27 */     this.isPlugin = isPlugin;
/*    */   }
/*    */   
/*    */   public void tick() {
/* 31 */     if (this.isComplete) this.ticksComplete++; 
/*    */   } }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\SetBackData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
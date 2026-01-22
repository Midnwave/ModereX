/*    */ package ac.grim.grimac.utils.anticheat.update;
/*    */ 
/*    */ 
/*    */ public final class PositionUpdate {
/*    */   private final Vector3d from;
/*    */   private final Vector3d to;
/*    */   private final boolean onGround;
/*    */   
/*    */   @Generated
/* 10 */   public PositionUpdate(Vector3d from, Vector3d to, boolean onGround, SetBackData setback, TeleportData teleportData, boolean isTeleport) { this.from = from; this.to = to; this.onGround = onGround; this.setback = setback; this.teleportData = teleportData; this.isTeleport = isTeleport; } private final SetBackData setback; private final TeleportData teleportData; private boolean isTeleport; @Generated
/*    */   public void setTeleport(boolean isTeleport) {
/* 12 */     this.isTeleport = isTeleport; }
/*    */   @Generated
/* 14 */   public Vector3d getFrom() { return this.from; } @Generated public Vector3d getTo() { return this.to; } @Generated
/* 15 */   public boolean isOnGround() { return this.onGround; } @Generated
/* 16 */   public SetBackData getSetback() { return this.setback; } @Generated
/* 17 */   public TeleportData getTeleportData() { return this.teleportData; } @Generated
/* 18 */   public boolean isTeleport() { return this.isTeleport; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\antichea\\update\PositionUpdate.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
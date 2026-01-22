/*    */ package ac.grim.grimac.utils.data;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public class TeleportData {
/*    */   Vector3d location;
/*    */   Vector3d velocity;
/*    */   
/*    */   @Generated
/*    */   public TeleportData(Vector3d location, Vector3d velocity, RelativeFlag flags, int transaction, int teleportId) {
/* 11 */     this.location = location; this.velocity = velocity; this.flags = flags; this.transaction = transaction; this.teleportId = teleportId;
/*    */   } RelativeFlag flags; int transaction; int teleportId; @Generated
/*    */   public Vector3d getLocation() {
/* 14 */     return this.location; } @Generated
/* 15 */   public Vector3d getVelocity() { return this.velocity; } @Generated
/* 16 */   public RelativeFlag getFlags() { return this.flags; } @Generated
/* 17 */   public void setTransaction(int transaction) { this.transaction = transaction; } @Generated
/* 18 */   public int getTransaction() { return this.transaction; } @Generated
/* 19 */   public void setTeleportId(int teleportId) { this.teleportId = teleportId; } @Generated
/* 20 */   public int getTeleportId() { return this.teleportId; }
/*    */   
/*    */   public void modifyVector(GrimPlayer player, Vector3dm vector) {
/* 23 */     boolean isStupidTeleportSystem = player.supportsEndTick();
/* 24 */     if (!isStupidTeleportSystem) {
/* 25 */       if (!isRelativeX()) {
/* 26 */         vector.setX(0);
/*    */       }
/*    */       
/* 29 */       if (!isRelativeY()) {
/* 30 */         vector.setY(0);
/* 31 */         player.lastWasClimbing = 0.0D;
/* 32 */         player.canSwimHop = false;
/*    */       } 
/*    */       
/* 35 */       if (!isRelativeZ()) {
/* 36 */         vector.setZ(0);
/*    */       }
/*    */     } 
/*    */     
/* 40 */     if (this.velocity != null && isStupidTeleportSystem) {
/*    */       
/* 42 */       if (isRelativeDeltaX()) {
/* 43 */         vector.setX(vector.getX() + this.velocity.getX());
/*    */       } else {
/* 45 */         vector.setX(this.velocity.getX());
/*    */       } 
/*    */       
/* 48 */       if (isRelativeDeltaY()) {
/* 49 */         vector.setY(vector.getY() + this.velocity.getY());
/*    */       } else {
/* 51 */         vector.setY(this.velocity.getY());
/*    */         
/* 53 */         player.lastWasClimbing = 0.0D;
/* 54 */         player.canSwimHop = false;
/*    */       } 
/*    */       
/* 57 */       if (isRelativeDeltaZ()) {
/* 58 */         vector.setZ(vector.getZ() + this.velocity.getZ());
/*    */       } else {
/* 60 */         vector.setZ(this.velocity.getZ());
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isRelativeVelocity() {
/* 66 */     return (isRelativeDeltaX() || isRelativeDeltaY() || isRelativeDeltaZ());
/*    */   }
/*    */   
/*    */   public boolean isRelativeDeltaX() {
/* 70 */     return this.flags.has(RelativeFlag.DELTA_X);
/*    */   }
/*    */   
/*    */   public boolean isRelativeDeltaY() {
/* 74 */     return this.flags.has(RelativeFlag.DELTA_Y);
/*    */   }
/*    */   
/*    */   public boolean isRelativeDeltaZ() {
/* 78 */     return this.flags.has(RelativeFlag.DELTA_Z);
/*    */   }
/*    */   
/*    */   public boolean isRelativePos() {
/* 82 */     return (isRelativeX() || isRelativeY() || isRelativeZ());
/*    */   }
/*    */   
/*    */   public boolean isRelativeX() {
/* 86 */     return this.flags.has(RelativeFlag.X.getMask());
/*    */   }
/*    */   
/*    */   public boolean isRelativeY() {
/* 90 */     return this.flags.has(RelativeFlag.Y.getMask());
/*    */   }
/*    */   
/*    */   public boolean isRelativeZ() {
/* 94 */     return this.flags.has(RelativeFlag.Z.getMask());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\TeleportData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
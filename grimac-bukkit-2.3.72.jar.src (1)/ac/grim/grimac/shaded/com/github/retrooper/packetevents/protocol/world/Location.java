/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Location
/*     */ {
/*     */   private Vector3d position;
/*     */   private float yaw;
/*     */   private float pitch;
/*     */   
/*     */   public Location(Vector3d position, float yaw, float pitch) {
/*  31 */     this.position = position;
/*  32 */     this.yaw = yaw;
/*  33 */     this.pitch = pitch;
/*     */   }
/*     */   
/*     */   public Location(double x, double y, double z, float yaw, float pitch) {
/*  37 */     this(new Vector3d(x, y, z), yaw, pitch);
/*     */   }
/*     */   
/*     */   public Vector3d getPosition() {
/*  41 */     return this.position;
/*     */   }
/*     */   
/*     */   public double getX() {
/*  45 */     return this.position.getX();
/*     */   }
/*     */   
/*     */   public double getY() {
/*  49 */     return this.position.getY();
/*     */   }
/*     */   
/*     */   public double getZ() {
/*  53 */     return this.position.getZ();
/*     */   }
/*     */   
/*     */   public void setPosition(Vector3d position) {
/*  57 */     this.position = position;
/*     */   }
/*     */   
/*     */   public float getYaw() {
/*  61 */     return this.yaw;
/*     */   }
/*     */   
/*     */   public void setYaw(float yaw) {
/*  65 */     this.yaw = yaw;
/*     */   }
/*     */   
/*     */   public float getPitch() {
/*  69 */     return this.pitch;
/*     */   }
/*     */   
/*     */   public void setPitch(float pitch) {
/*  73 */     this.pitch = pitch;
/*     */   }
/*     */   
/*     */   public Vector3f getDirection() {
/*  77 */     double rotX = getYaw();
/*  78 */     double rotY = getPitch();
/*  79 */     float y = (float)-Math.sin(Math.toRadians(rotY));
/*  80 */     double xz = Math.cos(Math.toRadians(rotY));
/*  81 */     float x = (float)(-xz * Math.sin(Math.toRadians(rotX)));
/*  82 */     float z = (float)(xz * Math.cos(Math.toRadians(rotX)));
/*  83 */     return new Vector3f(x, y, z);
/*     */   }
/*     */   
/*     */   public void setDirection(Vector3f vector) {
/*  87 */     double _2PI = 6.283185307179586D;
/*  88 */     double x = vector.getX();
/*  89 */     double z = vector.getZ();
/*  90 */     if (x == 0.0D && z == 0.0D) {
/*  91 */       this.pitch = (vector.getY() > 0.0D) ? -90.0F : 90.0F;
/*     */     } else {
/*  93 */       double theta = Math.atan2(-x, z);
/*  94 */       this.yaw = (float)Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
/*  95 */       double x2 = x * x;
/*  96 */       double z2 = z * z;
/*  97 */       double xz = Math.sqrt(x2 + z2);
/*  98 */       this.pitch = (float)Math.toDegrees(Math.atan(-vector.getY() / xz));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Location clone() {
/* 104 */     return new Location(this.position, this.yaw, this.pitch);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return "Location {[" + this.position.toString() + "], yaw: " + this.yaw + ", pitch: " + this.pitch + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\Location.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
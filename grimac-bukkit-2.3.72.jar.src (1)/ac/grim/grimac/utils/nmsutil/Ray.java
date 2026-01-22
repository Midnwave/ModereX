/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.data.Pair;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public class Ray implements Cloneable {
/*    */   private final Vector3dm origin;
/*    */   private final Vector3dm direction;
/*    */   
/*    */   @Generated
/* 12 */   public Vector3dm getOrigin() { return this.origin; } @Generated
/* 13 */   public Vector3dm getDirection() { return this.direction; }
/*    */   
/*    */   public Ray(Vector3dm origin, Vector3dm direction) {
/* 16 */     this.origin = origin;
/* 17 */     this.direction = direction;
/*    */   }
/*    */   
/*    */   public Ray(GrimPlayer player, double x, double y, double z, float xRot, float yRot) {
/* 21 */     this.origin = new Vector3dm(x, y, z);
/* 22 */     this.direction = calculateDirection(player, xRot, yRot);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static Vector3dm calculateDirection(GrimPlayer player, float xRot, float yRot) {
/* 28 */     Vector3dm vector = new Vector3dm();
/* 29 */     float rotX = (float)Math.toRadians(xRot);
/* 30 */     float rotY = (float)Math.toRadians(yRot);
/* 31 */     vector.setY(-player.trigHandler.sin(rotY));
/* 32 */     double xz = player.trigHandler.cos(rotY);
/* 33 */     vector.setX(-xz * player.trigHandler.sin(rotX));
/* 34 */     vector.setZ(xz * player.trigHandler.cos(rotX));
/* 35 */     return vector;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Ray clone() {
/* 41 */     return new Ray(this.origin.clone(), this.direction.clone());
/*    */   }
/*    */   
/*    */   public String toString() {
/* 45 */     return "origin: " + String.valueOf(this.origin) + " direction: " + String.valueOf(this.direction);
/*    */   }
/*    */   
/*    */   public Vector3dm getPointAtDistance(double distance) {
/* 49 */     Vector3dm dir = new Vector3dm(this.direction.getX(), this.direction.getY(), this.direction.getZ());
/* 50 */     Vector3dm orig = new Vector3dm(this.origin.getX(), this.origin.getY(), this.origin.getZ());
/* 51 */     return orig.add(dir.multiply(distance));
/*    */   }
/*    */ 
/*    */   
/*    */   public Pair<Vector3dm, Vector3dm> closestPointsBetweenLines(Ray other) {
/* 56 */     Vector3dm n1 = this.direction.clone().crossProduct(other.direction.clone().crossProduct(this.direction));
/* 57 */     Vector3dm n2 = other.direction.clone().crossProduct(this.direction.clone().crossProduct(other.direction));
/*    */     
/* 59 */     Vector3dm c1 = this.origin.clone().add(this.direction.clone().multiply(other.origin.clone().subtract(this.origin).dot(n2) / this.direction.dot(n2)));
/* 60 */     Vector3dm c2 = other.origin.clone().add(other.direction.clone().multiply(this.origin.clone().subtract(other.origin).dot(n1) / other.direction.dot(n1)));
/*    */     
/* 62 */     return new Pair(c1, c2);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\Ray.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
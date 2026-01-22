/*    */ package ac.grim.grimac.utils.math;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ public final class VectorUtils {
/*    */   @Generated
/*    */   private VectorUtils() {
/*  8 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static Vector3dm cutBoxToVector(Vector3dm vectorToCutTo, Vector3dm min, Vector3dm max) {
/* 10 */     SimpleCollisionBox box = (new SimpleCollisionBox(min, max)).sort();
/* 11 */     return cutBoxToVector(vectorToCutTo, box);
/*    */   }
/*    */   
/*    */   public static Vector3dm cutBoxToVector(Vector3dm vectorCutTo, SimpleCollisionBox box) {
/* 15 */     return new Vector3dm(GrimMath.clamp(vectorCutTo.getX(), box.minX, box.maxX), 
/* 16 */         GrimMath.clamp(vectorCutTo.getY(), box.minY, box.maxY), 
/* 17 */         GrimMath.clamp(vectorCutTo.getZ(), box.minZ, box.maxZ));
/*    */   }
/*    */   
/*    */   public static Vector3dm fromVec3d(Vector3d vector3d) {
/* 21 */     return new Vector3dm(vector3d.getX(), vector3d.getY(), vector3d.getZ());
/*    */   }
/*    */ 
/*    */   
/*    */   public static Vector3d clampVector(Vector3d toClamp) {
/* 26 */     double x = GrimMath.clamp(toClamp.getX(), -3.0E7D, 3.0E7D);
/* 27 */     double y = GrimMath.clamp(toClamp.getY(), -2.0E7D, 2.0E7D);
/* 28 */     double z = GrimMath.clamp(toClamp.getZ(), -3.0E7D, 3.0E7D);
/*    */     
/* 30 */     return new Vector3d(x, y, z);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\VectorUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
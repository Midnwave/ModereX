/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ public class Vector3f
/*     */ {
/*     */   public final float x;
/*     */   public final float y;
/*     */   public final float z;
/*     */   
/*     */   public Vector3f() {
/*  51 */     this.x = 0.0F;
/*  52 */     this.y = 0.0F;
/*  53 */     this.z = 0.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3f(float x, float y, float z) {
/*  64 */     this.x = x;
/*  65 */     this.y = y;
/*  66 */     this.z = z;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3f(float[] array) {
/*  78 */     if (array.length > 0) {
/*  79 */       this.x = array[0];
/*     */     } else {
/*  81 */       this.x = 0.0F;
/*  82 */       this.y = 0.0F;
/*  83 */       this.z = 0.0F;
/*     */       
/*     */       return;
/*     */     } 
/*  87 */     if (array.length > 1) {
/*  88 */       this.y = array[1];
/*     */     } else {
/*  90 */       this.y = 0.0F;
/*  91 */       this.z = 0.0F;
/*     */       
/*     */       return;
/*     */     } 
/*  95 */     if (array.length > 2) {
/*  96 */       this.z = array[2];
/*     */     } else {
/*  98 */       this.z = 0.0F;
/*     */     } 
/*     */   }
/*     */   
/*     */   public float getX() {
/* 103 */     return this.x;
/*     */   }
/*     */   
/*     */   public float getY() {
/* 107 */     return this.y;
/*     */   }
/*     */   
/*     */   public float getZ() {
/* 111 */     return this.z;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 123 */     if (obj instanceof Vector3f) {
/* 124 */       Vector3f vec = (Vector3f)obj;
/* 125 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 126 */     }  if (obj instanceof Vector3d) {
/* 127 */       Vector3d vec = (Vector3d)obj;
/* 128 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 129 */     }  if (obj instanceof Vector3i) {
/* 130 */       Vector3i vec = (Vector3i)obj;
/* 131 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/*     */     } 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 138 */     return Objects.hash(new Object[] { Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z) });
/*     */   }
/*     */   
/*     */   public Vector3f add(float x, float y, float z) {
/* 142 */     return new Vector3f(this.x + x, this.y + y, this.z + z);
/*     */   }
/*     */   
/*     */   public Vector3f add(Vector3f other) {
/* 146 */     return add(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3f offset(BlockFace face) {
/* 150 */     return add(face.getModX(), face.getModY(), face.getModZ());
/*     */   }
/*     */   
/*     */   public Vector3f subtract(float x, float y, float z) {
/* 154 */     return new Vector3f(this.x - x, this.y - y, this.z - z);
/*     */   }
/*     */   
/*     */   public Vector3f subtract(Vector3f other) {
/* 158 */     return subtract(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3f multiply(float x, float y, float z) {
/* 162 */     return new Vector3f(this.x * x, this.y * y, this.z * z);
/*     */   }
/*     */   
/*     */   public Vector3f multiply(Vector3f other) {
/* 166 */     return multiply(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3f multiply(float value) {
/* 170 */     return multiply(value, value, value);
/*     */   }
/*     */   
/*     */   public Vector3f crossProduct(Vector3f other) {
/* 174 */     float newX = this.y * other.z - other.y * this.z;
/* 175 */     float newY = this.z * other.x - other.z * this.x;
/* 176 */     float newZ = this.x * other.y - other.x * this.y;
/* 177 */     return new Vector3f(newX, newY, newZ);
/*     */   }
/*     */   
/*     */   public float dot(Vector3f other) {
/* 181 */     return this.x * other.x + this.y * other.y + this.z * other.z;
/*     */   }
/*     */   
/*     */   public Vector3f with(Float x, Float y, Float z) {
/* 185 */     return new Vector3f((x == null) ? this.x : x.floatValue(), (y == null) ? this.y : y.floatValue(), (z == null) ? this.z : z.floatValue());
/*     */   }
/*     */   
/*     */   public Vector3f withX(float x) {
/* 189 */     return new Vector3f(x, this.y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3f withY(float y) {
/* 193 */     return new Vector3f(this.x, y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3f withZ(float z) {
/* 197 */     return new Vector3f(this.x, this.y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 202 */     return "X: " + this.x + ", Y: " + this.y + ", Z: " + this.z;
/*     */   }
/*     */   
/*     */   public static Vector3f zero() {
/* 206 */     return new Vector3f();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Vector3f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package ac.grim.grimac.utils.math;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.io.Serializable;
/*     */ import java.util.Random;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class Vector3dm implements Cloneable, Serializable {
/*     */   private static final long serialVersionUID = -2657651106777219169L;
/*     */   public static final double epsilon = 1.0E-6D;
/*     */   protected double x;
/*     */   protected double y;
/*     */   protected double z;
/*  16 */   private static final Random random = new Random(); @Generated
/*     */   public double getX() {
/*  18 */     return this.x; }
/*     */   @Generated
/*  20 */   public double getY() { return this.y; } @Generated
/*     */   public double getZ() {
/*  22 */     return this.z;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public Vector3dm() {
/*  27 */     this.x = 0.0D;
/*  28 */     this.y = 0.0D;
/*  29 */     this.z = 0.0D;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public Vector3dm(int x, int y, int z) {
/*  34 */     this.x = x;
/*  35 */     this.y = y;
/*  36 */     this.z = z;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public Vector3dm(double x, double y, double z) {
/*  41 */     this.x = x;
/*  42 */     this.y = y;
/*  43 */     this.z = z;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public Vector3dm(float x, float y, float z) {
/*  48 */     this.x = x;
/*  49 */     this.y = y;
/*  50 */     this.z = z;
/*     */   }
/*     */   @Contract("_, _ -> new")
/*     */   @NotNull
/*     */   public static Vector3dm min(@NotNull Vector3dm a, @NotNull Vector3dm b) {
/*  55 */     return new Vector3dm(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
/*     */   }
/*     */   @Contract("_, _ -> new")
/*     */   @NotNull
/*     */   public static Vector3dm max(@NotNull Vector3dm a, @NotNull Vector3dm b) {
/*  60 */     return new Vector3dm(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
/*     */   }
/*     */   @Contract(" -> new")
/*     */   @NotNull
/*     */   public static Vector3dm getRandom() {
/*  65 */     return new Vector3dm(random.nextDouble(), random.nextDouble(), random.nextDouble());
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm add(@NotNull Vector3dm vec) {
/*  69 */     this.x += vec.x;
/*  70 */     this.y += vec.y;
/*  71 */     this.z += vec.z;
/*  72 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm subtract(@NotNull Vector3dm vec) {
/*  76 */     this.x -= vec.x;
/*  77 */     this.y -= vec.y;
/*  78 */     this.z -= vec.z;
/*  79 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm multiply(@NotNull Vector3dm vec) {
/*  83 */     this.x *= vec.x;
/*  84 */     this.y *= vec.y;
/*  85 */     this.z *= vec.z;
/*  86 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm divide(@NotNull Vector3dm vec) {
/*  90 */     this.x /= vec.x;
/*  91 */     this.y /= vec.y;
/*  92 */     this.z /= vec.z;
/*  93 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm copy(@NotNull Vector3dm vec) {
/*  97 */     this.x = vec.x;
/*  98 */     this.y = vec.y;
/*  99 */     this.z = vec.z;
/* 100 */     return this;
/*     */   }
/*     */   
/*     */   public double length() {
/* 104 */     return Math.sqrt(GrimMath.square(this.x) + GrimMath.square(this.y) + GrimMath.square(this.z));
/*     */   }
/*     */   
/*     */   public double lengthSquared() {
/* 108 */     return GrimMath.square(this.x) + GrimMath.square(this.y) + GrimMath.square(this.z);
/*     */   }
/*     */   
/*     */   public double distance(@NotNull Vector3dm o) {
/* 112 */     return Math.sqrt(GrimMath.square(this.x - o.x) + GrimMath.square(this.y - o.y) + GrimMath.square(this.z - o.z));
/*     */   }
/*     */   
/*     */   public double distanceSquared(@NotNull Vector3dm o) {
/* 116 */     return GrimMath.square(this.x - o.x) + GrimMath.square(this.y - o.y) + GrimMath.square(this.z - o.z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm midpoint(@NotNull Vector3dm other) {
/* 120 */     this.x = (this.x + other.x) / 2.0D;
/* 121 */     this.y = (this.y + other.y) / 2.0D;
/* 122 */     this.z = (this.z + other.z) / 2.0D;
/* 123 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm getMidpoint(@NotNull Vector3dm other) {
/* 127 */     double x = (this.x + other.x) / 2.0D;
/* 128 */     double y = (this.y + other.y) / 2.0D;
/* 129 */     double z = (this.z + other.z) / 2.0D;
/* 130 */     return new Vector3dm(x, y, z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm multiply(int m) {
/* 134 */     this.x *= m;
/* 135 */     this.y *= m;
/* 136 */     this.z *= m;
/* 137 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm multiply(double m) {
/* 141 */     this.x *= m;
/* 142 */     this.y *= m;
/* 143 */     this.z *= m;
/* 144 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm multiply(float m) {
/* 148 */     this.x *= m;
/* 149 */     this.y *= m;
/* 150 */     this.z *= m;
/* 151 */     return this;
/*     */   }
/*     */   
/*     */   public double dot(@NotNull Vector3dm other) {
/* 155 */     return this.x * other.x + this.y * other.y + this.z * other.z;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm crossProduct(@NotNull Vector3dm o) {
/* 159 */     double newX = this.y * o.z - o.y * this.z;
/* 160 */     double newY = this.z * o.x - o.z * this.x;
/* 161 */     double newZ = this.x * o.y - o.x * this.y;
/* 162 */     this.x = newX;
/* 163 */     this.y = newY;
/* 164 */     this.z = newZ;
/* 165 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm getCrossProduct(@NotNull Vector3dm o) {
/* 169 */     double x = this.y * o.z - o.y * this.z;
/* 170 */     double y = this.z * o.x - o.z * this.x;
/* 171 */     double z = this.x * o.y - o.x * this.y;
/* 172 */     return new Vector3dm(x, y, z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm normalize() {
/* 176 */     double length = length();
/* 177 */     this.x /= length;
/* 178 */     this.y /= length;
/* 179 */     this.z /= length;
/* 180 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm zero() {
/* 184 */     this.x = 0.0D;
/* 185 */     this.y = 0.0D;
/* 186 */     this.z = 0.0D;
/* 187 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isZero() {
/* 191 */     return (this.x == 0.0D && this.y == 0.0D && this.z == 0.0D);
/*     */   }
/*     */   @NotNull
/*     */   Vector3dm normalizeZeros() {
/* 195 */     if (this.x == -0.0D) {
/* 196 */       this.x = 0.0D;
/*     */     }
/*     */     
/* 199 */     if (this.y == -0.0D) {
/* 200 */       this.y = 0.0D;
/*     */     }
/*     */     
/* 203 */     if (this.z == -0.0D) {
/* 204 */       this.z = 0.0D;
/*     */     }
/*     */     
/* 207 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isInAABB(@NotNull Vector3dm min, @NotNull Vector3dm max) {
/* 211 */     return (this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z);
/*     */   }
/*     */   
/*     */   public boolean isInSphere(@NotNull Vector3dm origin, double radius) {
/* 215 */     return (GrimMath.square(origin.x - this.x) + GrimMath.square(origin.y - this.y) + GrimMath.square(origin.z - this.z) <= GrimMath.square(radius));
/*     */   }
/*     */   
/*     */   public boolean isNormalized() {
/* 219 */     return (Math.abs(lengthSquared() - 1.0D) < 1.0E-6D);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm rotateAroundX(double angle) {
/* 223 */     double angleCos = Math.cos(angle);
/* 224 */     double angleSin = Math.sin(angle);
/* 225 */     double y = angleCos * getY() - angleSin * getZ();
/* 226 */     double z = angleSin * getY() + angleCos * getZ();
/* 227 */     return setY(y).setZ(z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm rotateAroundY(double angle) {
/* 231 */     double angleCos = Math.cos(angle);
/* 232 */     double angleSin = Math.sin(angle);
/* 233 */     double x = angleCos * getX() + angleSin * getZ();
/* 234 */     double z = -angleSin * getX() + angleCos * getZ();
/* 235 */     return setX(x).setZ(z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm rotateAroundZ(double angle) {
/* 239 */     double angleCos = Math.cos(angle);
/* 240 */     double angleSin = Math.sin(angle);
/* 241 */     double x = angleCos * getX() - angleSin * getY();
/* 242 */     double y = angleSin * getX() + angleCos * getY();
/* 243 */     return setX(x).setY(y);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setX(int x) {
/* 247 */     this.x = x;
/* 248 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setX(double x) {
/* 252 */     this.x = x;
/* 253 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setX(float x) {
/* 257 */     this.x = x;
/* 258 */     return this;
/*     */   }
/*     */   
/*     */   public int getBlockX() {
/* 262 */     return GrimMath.mojangFloor(this.x);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setY(int y) {
/* 266 */     this.y = y;
/* 267 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setY(double y) {
/* 271 */     this.y = y;
/* 272 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setY(float y) {
/* 276 */     this.y = y;
/* 277 */     return this;
/*     */   }
/*     */   
/*     */   public int getBlockY() {
/* 281 */     return GrimMath.mojangFloor(this.y);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setZ(int z) {
/* 285 */     this.z = z;
/* 286 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setZ(double z) {
/* 290 */     this.z = z;
/* 291 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm setZ(float z) {
/* 295 */     this.z = z;
/* 296 */     return this;
/*     */   }
/*     */   
/*     */   public int getBlockZ() {
/* 300 */     return GrimMath.mojangFloor(this.z);
/*     */   }
/*     */   
/*     */   @Contract(value = "null -> false", pure = true)
/*     */   public boolean equals(Object obj) {
/* 305 */     if (obj instanceof Vector3dm) { Vector3dm other = (Vector3dm)obj; if (Math.abs(this.x - other.x) < 1.0E-6D && Math.abs(this.y - other.y) < 1.0E-6D && Math.abs(this.z - other.z) < 1.0E-6D && getClass().equals(obj.getClass())); }  return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 309 */     int hash = 7;
/* 310 */     hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
/* 311 */     hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
/* 312 */     hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
/* 313 */     return hash;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3dm clone() {
/*     */     try {
/* 318 */       return (Vector3dm)super.clone();
/* 319 */     } catch (CloneNotSupportedException e) {
/* 320 */       throw new Error(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 325 */     return "" + this.x + "," + this.x + "," + this.y;
/*     */   }
/*     */   @NotNull
/*     */   public Vector3f toVector3f() {
/* 329 */     return new Vector3f((float)this.x, (float)this.y, (float)this.z);
/*     */   }
/*     */   @NotNull
/*     */   public Vector3d toVector3d() {
/* 333 */     return new Vector3d(this.x, this.y, this.z);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\Vector3dm.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
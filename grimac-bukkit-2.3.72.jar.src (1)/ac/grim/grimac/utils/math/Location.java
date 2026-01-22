/*     */ package ac.grim.grimac.utils.math;
/*     */ 
/*     */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Objects;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class Location implements Cloneable {
/*     */   private Reference<PlatformWorld> world;
/*     */   private double x;
/*     */   private double y;
/*     */   
/*     */   @Generated
/*  15 */   public double getX() { return this.x; } private double z; private float pitch; private float yaw; @Generated
/*  16 */   public void setX(double x) { this.x = x; }
/*     */   @Generated
/*  18 */   public double getY() { return this.y; } @Generated
/*  19 */   public void setY(double y) { this.y = y; }
/*     */   @Generated
/*  21 */   public double getZ() { return this.z; } @Generated
/*  22 */   public void setZ(double z) { this.z = z; }
/*     */   @Generated
/*  24 */   public float getPitch() { return this.pitch; } @Generated
/*  25 */   public void setPitch(float pitch) { this.pitch = pitch; }
/*     */   @Generated
/*  27 */   public float getYaw() { return this.yaw; } @Generated
/*  28 */   public void setYaw(float yaw) { this.yaw = yaw; }
/*     */ 
/*     */   
/*     */   public Location(PlatformWorld world, double x, double y, double z) {
/*  32 */     this(world, x, y, z, 0.0F, 0.0F);
/*     */   }
/*     */   
/*     */   public Location(PlatformWorld world, double x, double y, double z, float yaw, float pitch) {
/*  36 */     if (world != null) {
/*  37 */       this.world = new WeakReference<>(world);
/*     */     }
/*     */     
/*  40 */     this.x = x;
/*  41 */     this.y = y;
/*  42 */     this.z = z;
/*  43 */     this.pitch = pitch;
/*  44 */     this.yaw = yaw;
/*     */   }
/*     */   
/*     */   public static float normalizeYaw(float yaw) {
/*  48 */     yaw %= 360.0F;
/*  49 */     if (yaw >= 180.0F) {
/*  50 */       yaw -= 360.0F;
/*  51 */     } else if (yaw < -180.0F) {
/*  52 */       yaw += 360.0F;
/*     */     } 
/*     */     
/*  55 */     return yaw;
/*     */   }
/*     */   
/*     */   public static float normalizePitch(float pitch) {
/*  59 */     if (pitch > 90.0F) {
/*  60 */       pitch = 90.0F;
/*  61 */     } else if (pitch < -90.0F) {
/*  62 */       pitch = -90.0F;
/*     */     } 
/*     */     
/*  65 */     return pitch;
/*     */   }
/*     */   
/*     */   public PlatformWorld getWorld() {
/*  69 */     if (this.world == null) {
/*  70 */       return null;
/*     */     }
/*  72 */     return this.world.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWorld(@Nullable PlatformWorld world) {
/*  77 */     this.world = (world == null) ? null : new WeakReference<>(world);
/*     */   }
/*     */   @NotNull
/*     */   public Location add(@NotNull Location vec) {
/*  81 */     if (((Location)Objects.<Location>requireNonNull(vec)).getWorld() == getWorld()) {
/*  82 */       this.x += vec.x;
/*  83 */       this.y += vec.y;
/*  84 */       this.z += vec.z;
/*  85 */       return this;
/*     */     } 
/*  87 */     throw new IllegalArgumentException("Cannot add Locations of differing worlds");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Location add(double x, double y, double z) {
/*  92 */     this.x += x;
/*  93 */     this.y += y;
/*  94 */     this.z += z;
/*  95 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Location subtract(@NotNull Location vec) {
/*  99 */     if (((Location)Objects.<Location>requireNonNull(vec)).getWorld() == getWorld()) {
/* 100 */       this.x -= vec.x;
/* 101 */       this.y -= vec.y;
/* 102 */       this.z -= vec.z;
/* 103 */       return this;
/*     */     } 
/* 105 */     throw new IllegalArgumentException("Cannot add Locations of differing worlds");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Location subtract(double x, double y, double z) {
/* 110 */     this.x -= x;
/* 111 */     this.y -= y;
/* 112 */     this.z -= z;
/* 113 */     return this;
/*     */   }
/*     */   
/*     */   public double distance(@NotNull Location o) {
/* 117 */     return Math.sqrt(distanceSquared(o));
/*     */   }
/*     */   
/*     */   public double distanceSquared(@NotNull Location o) {
/* 121 */     if (o.getWorld() != null && getWorld() != null) {
/* 122 */       if (o.getWorld() != getWorld()) {
/* 123 */         throw new IllegalArgumentException("Cannot measure distance between " + getWorld().getName() + " and " + o.getWorld().getName());
/*     */       }
/* 125 */       return (this.x - o.x) * (this.x - o.x) + (this.y - o.y) * (this.y - o.y) + (this.z - o.z) * (this.z - o.z);
/*     */     } 
/*     */     
/* 128 */     throw new IllegalArgumentException("Cannot measure distance to a null world");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Location multiply(double m) {
/* 133 */     this.x *= m;
/* 134 */     this.y *= m;
/* 135 */     this.z *= m;
/* 136 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Location zero() {
/* 140 */     this.x = 0.0D;
/* 141 */     this.y = 0.0D;
/* 142 */     this.z = 0.0D;
/* 143 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Location set(double x, double y, double z) {
/* 147 */     this.x = x;
/* 148 */     this.y = y;
/* 149 */     this.z = z;
/* 150 */     return this;
/*     */   }
/*     */   @NotNull
/*     */   public Location add(@NotNull Location base, double x, double y, double z) {
/* 154 */     return set(base.x + x, base.y + y, base.z + z);
/*     */   }
/*     */   @NotNull
/*     */   public Location subtract(@NotNull Location base, double x, double y, double z) {
/* 158 */     return set(base.x - x, base.y - y, base.z - z);
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj) {
/* 162 */     if (obj == null || getClass() != obj.getClass()) {
/* 163 */       return false;
/*     */     }
/* 165 */     Location other = (Location)obj;
/* 166 */     return (Objects.equals((this.world == null) ? null : this.world.get(), (other.world == null) ? null : other.world.get()) && 
/* 167 */       Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x) && 
/* 168 */       Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y) && 
/* 169 */       Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z) && 
/* 170 */       Float.floatToIntBits(this.pitch) == Float.floatToIntBits(other.pitch) && 
/* 171 */       Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 176 */     int hash = 3;
/* 177 */     PlatformWorld world = (this.world == null) ? null : this.world.get();
/* 178 */     hash = 19 * hash + ((world != null) ? world.hashCode() : 0);
/* 179 */     hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
/* 180 */     hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
/* 181 */     hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
/* 182 */     hash = 19 * hash + Float.floatToIntBits(this.pitch);
/* 183 */     hash = 19 * hash + Float.floatToIntBits(this.yaw);
/* 184 */     return hash;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 188 */     return "Location{world=" + String.valueOf((this.world == null) ? null : this.world.get()) + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",pitch=" + this.pitch + ",yaw=" + this.yaw + "}";
/*     */   }
/*     */   @NotNull
/*     */   public Location clone() {
/*     */     try {
/* 193 */       return (Location)super.clone();
/* 194 */     } catch (CloneNotSupportedException e) {
/* 195 */       throw new Error(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double x() {
/* 200 */     return getX();
/*     */   }
/*     */   
/*     */   public int getBlockX() {
/* 204 */     return GrimMath.mojangFloor(this.x);
/*     */   }
/*     */   
/*     */   public double y() {
/* 208 */     return getY();
/*     */   }
/*     */   
/*     */   public int getBlockY() {
/* 212 */     return GrimMath.mojangFloor(this.y);
/*     */   }
/*     */   
/*     */   public double z() {
/* 216 */     return getZ();
/*     */   }
/*     */   
/*     */   public int getBlockZ() {
/* 220 */     return GrimMath.mojangFloor(this.z);
/*     */   }
/*     */   
/*     */   public boolean isWorldLoaded() {
/* 224 */     if (this.world == null) {
/* 225 */       return false;
/*     */     }
/* 227 */     PlatformWorld world = this.world.get();
/* 228 */     return (world != null && world.isLoaded());
/*     */   }
/*     */ 
/*     */   
/*     */   public Vector3dm toVector() {
/* 233 */     return new Vector3dm(this.x, this.y, this.z);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\Location.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
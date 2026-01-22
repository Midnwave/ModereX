/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public class Vector3d
/*     */ {
/*     */   public final double x;
/*     */   public final double y;
/*     */   public final double z;
/*     */   
/*     */   public Vector3d() {
/*  58 */     this.x = 0.0D;
/*  59 */     this.y = 0.0D;
/*  60 */     this.z = 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3d(double x, double y, double z) {
/*  71 */     this.x = x;
/*  72 */     this.y = y;
/*  73 */     this.z = z;
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
/*     */   public Vector3d(double[] array) {
/*  85 */     if (array.length > 0) {
/*  86 */       this.x = array[0];
/*     */     } else {
/*  88 */       this.x = 0.0D;
/*  89 */       this.y = 0.0D;
/*  90 */       this.z = 0.0D;
/*     */       
/*     */       return;
/*     */     } 
/*  94 */     if (array.length > 1) {
/*  95 */       this.y = array[1];
/*     */     } else {
/*  97 */       this.y = 0.0D;
/*  98 */       this.z = 0.0D;
/*     */       
/*     */       return;
/*     */     } 
/* 102 */     if (array.length > 2) {
/* 103 */       this.z = array[2];
/*     */     } else {
/* 105 */       this.z = 0.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Vector3d read(PacketWrapper<?> wrapper) {
/* 110 */     double x = wrapper.readDouble();
/* 111 */     double y = wrapper.readDouble();
/* 112 */     double z = wrapper.readDouble();
/* 113 */     return new Vector3d(x, y, z);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Vector3d vector) {
/* 117 */     wrapper.writeDouble(vector.x);
/* 118 */     wrapper.writeDouble(vector.y);
/* 119 */     wrapper.writeDouble(vector.z);
/*     */   }
/*     */   
/*     */   public static Vector3d decode(NBT tag, ClientVersion version) {
/* 123 */     NBTList<?> list = (NBTList)tag;
/* 124 */     double x = ((NBTNumber)list.getTag(0)).getAsDouble();
/* 125 */     double y = ((NBTNumber)list.getTag(1)).getAsDouble();
/* 126 */     double z = ((NBTNumber)list.getTag(2)).getAsDouble();
/* 127 */     return new Vector3d(x, y, z);
/*     */   }
/*     */   
/*     */   public static NBT encode(Vector3d vector3d, ClientVersion version) {
/* 131 */     NBTList<NBTDouble> list = new NBTList(NBTType.DOUBLE, 3);
/* 132 */     list.addTag((NBT)new NBTDouble(vector3d.x));
/* 133 */     list.addTag((NBT)new NBTDouble(vector3d.y));
/* 134 */     list.addTag((NBT)new NBTDouble(vector3d.z));
/* 135 */     return (NBT)list;
/*     */   }
/*     */   
/*     */   public double getX() {
/* 139 */     return this.x;
/*     */   }
/*     */   
/*     */   public double getY() {
/* 143 */     return this.y;
/*     */   }
/*     */   
/*     */   public double getZ() {
/* 147 */     return this.z;
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
/* 159 */     if (obj instanceof Vector3d) {
/* 160 */       Vector3d vec = (Vector3d)obj;
/* 161 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 162 */     }  if (obj instanceof Vector3f) {
/* 163 */       Vector3f vec = (Vector3f)obj;
/* 164 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 165 */     }  if (obj instanceof Vector3i) {
/* 166 */       Vector3i vec = (Vector3i)obj;
/* 167 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/*     */     } 
/* 169 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 174 */     return Objects.hash(new Object[] { Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z) });
/*     */   }
/*     */   
/*     */   public Vector3d add(double x, double y, double z) {
/* 178 */     return new Vector3d(this.x + x, this.y + y, this.z + z);
/*     */   }
/*     */   
/*     */   public Vector3d add(Vector3d other) {
/* 182 */     return add(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3d offset(BlockFace face) {
/* 186 */     return add(face.getModX(), face.getModY(), face.getModZ());
/*     */   }
/*     */   
/*     */   public Vector3d subtract(double x, double y, double z) {
/* 190 */     return new Vector3d(this.x - x, this.y - y, this.z - z);
/*     */   }
/*     */   
/*     */   public Vector3d subtract(Vector3d other) {
/* 194 */     return subtract(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3d multiply(double x, double y, double z) {
/* 198 */     return new Vector3d(this.x * x, this.y * y, this.z * z);
/*     */   }
/*     */   
/*     */   public Vector3d multiply(Vector3d other) {
/* 202 */     return multiply(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3d multiply(double value) {
/* 206 */     return multiply(value, value, value);
/*     */   }
/*     */   
/*     */   public Vector3d crossProduct(Vector3d other) {
/* 210 */     double newX = this.y * other.z - other.y * this.z;
/* 211 */     double newY = this.z * other.x - other.z * this.x;
/* 212 */     double newZ = this.x * other.y - other.x * this.y;
/* 213 */     return new Vector3d(newX, newY, newZ);
/*     */   }
/*     */   
/*     */   public double dot(Vector3d other) {
/* 217 */     return this.x * other.x + this.y * other.y + this.z * other.z;
/*     */   }
/*     */   
/*     */   public Vector3d with(Double x, Double y, Double z) {
/* 221 */     return new Vector3d((x == null) ? this.x : x.doubleValue(), (y == null) ? this.y : y.doubleValue(), (z == null) ? this.z : z.doubleValue());
/*     */   }
/*     */   
/*     */   public Vector3d withX(double x) {
/* 225 */     return new Vector3d(x, this.y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3d withY(double y) {
/* 229 */     return new Vector3d(this.x, y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3d withZ(double z) {
/* 233 */     return new Vector3d(this.x, this.y, z);
/*     */   }
/*     */   
/*     */   public double distance(Vector3d other) {
/* 237 */     return Math.sqrt(distanceSquared(other));
/*     */   }
/*     */   
/*     */   public double length() {
/* 241 */     return Math.sqrt(lengthSquared());
/*     */   }
/*     */   
/*     */   public double lengthSquared() {
/* 245 */     return this.x * this.x + this.y * this.y + this.z * this.z;
/*     */   }
/*     */   
/*     */   public Vector3d normalize() {
/* 249 */     double length = length();
/*     */     
/* 251 */     return new Vector3d(this.x / length, this.y / length, this.z / length);
/*     */   }
/*     */   
/*     */   public double distanceSquared(Vector3d other) {
/* 255 */     double distX = (this.x - other.x) * (this.x - other.x);
/* 256 */     double distY = (this.y - other.y) * (this.y - other.y);
/* 257 */     double distZ = (this.z - other.z) * (this.z - other.z);
/* 258 */     return distX + distY + distZ;
/*     */   }
/*     */   
/*     */   public Vector3i toVector3i() {
/* 262 */     return new Vector3i((int)this.x, (int)this.y, (int)this.z);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 267 */     return "X: " + this.x + ", Y: " + this.y + ", Z: " + this.z;
/*     */   }
/*     */   
/*     */   public static Vector3d zero() {
/* 271 */     return new Vector3d();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Vector3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
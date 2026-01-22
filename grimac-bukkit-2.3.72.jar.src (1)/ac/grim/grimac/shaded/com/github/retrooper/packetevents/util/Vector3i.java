/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
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
/*     */ 
/*     */ public class Vector3i
/*     */ {
/*     */   public final int x;
/*     */   public final int y;
/*     */   public final int z;
/*     */   
/*     */   public Vector3i() {
/*  55 */     this.x = 0;
/*  56 */     this.y = 0;
/*  57 */     this.z = 0;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Vector3i(long val) {
/*  62 */     this(val, PacketEvents.getAPI().getServerManager().getVersion());
/*     */   }
/*     */   
/*     */   public Vector3i(long val, ServerVersion serverVersion) {
/*  66 */     int y, z, x = (int)(val >> 38L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
/*  73 */       y = (int)(val << 52L >> 52L);
/*  74 */       z = (int)(val << 26L >> 38L);
/*     */     } else {
/*     */       
/*  77 */       y = (int)(val >> 26L & 0xFFFL);
/*  78 */       z = (int)(val << 38L >> 38L);
/*     */     } 
/*     */     
/*  81 */     this.x = x;
/*  82 */     this.y = y;
/*  83 */     this.z = z;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector3i(int x, int y, int z) {
/*  94 */     this.x = x;
/*  95 */     this.y = y;
/*  96 */     this.z = z;
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
/*     */   public Vector3i(int[] array) {
/* 108 */     if (array.length > 0) {
/* 109 */       this.x = array[0];
/*     */     } else {
/* 111 */       this.x = 0;
/* 112 */       this.y = 0;
/* 113 */       this.z = 0;
/*     */       return;
/*     */     } 
/* 116 */     if (array.length > 1) {
/* 117 */       this.y = array[1];
/*     */     } else {
/* 119 */       this.y = 0;
/* 120 */       this.z = 0;
/*     */       return;
/*     */     } 
/* 123 */     if (array.length > 2) {
/* 124 */       this.z = array[2];
/*     */     } else {
/* 126 */       this.z = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Vector3i read(PacketWrapper<?> wrapper) {
/* 131 */     int x = wrapper.readVarInt();
/* 132 */     int y = wrapper.readVarInt();
/* 133 */     int z = wrapper.readVarInt();
/* 134 */     return new Vector3i(x, y, z);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Vector3i vector) {
/* 138 */     wrapper.writeVarInt(vector.x);
/* 139 */     wrapper.writeVarInt(vector.y);
/* 140 */     wrapper.writeVarInt(vector.z);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSerializedPosition(ServerVersion serverVersion) {
/* 145 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
/* 146 */       long x = (getX() & 0x3FFFFFF);
/* 147 */       long y = (getY() & 0xFFF);
/* 148 */       long z = (getZ() & 0x3FFFFFF);
/*     */       
/* 150 */       return x << 38L | z << 12L | y;
/*     */     } 
/*     */     
/* 153 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
/* 154 */       return (getX() & 0x3FFFFFF) << 38L | (getZ() & 0x3FFFFFF) << 12L | (getY() & 0xFFF);
/*     */     }
/*     */     
/* 157 */     return (getX() & 0x3FFFFFF) << 38L | (getY() & 0xFFF) << 26L | (getZ() & 0x3FFFFFF);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public long getSerializedPosition() {
/* 162 */     return getSerializedPosition(PacketEvents.getAPI().getServerManager().getVersion());
/*     */   }
/*     */   
/*     */   public int getX() {
/* 166 */     return this.x;
/*     */   }
/*     */   
/*     */   public int getY() {
/* 170 */     return this.y;
/*     */   }
/*     */   
/*     */   public int getZ() {
/* 174 */     return this.z;
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
/* 186 */     if (obj instanceof Vector3i) {
/* 187 */       Vector3i vec = (Vector3i)obj;
/* 188 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 189 */     }  if (obj instanceof Vector3d) {
/* 190 */       Vector3d vec = (Vector3d)obj;
/* 191 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/* 192 */     }  if (obj instanceof Vector3f) {
/* 193 */       Vector3f vec = (Vector3f)obj;
/* 194 */       return (this.x == vec.x && this.y == vec.y && this.z == vec.z);
/*     */     } 
/* 196 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 201 */     return Objects.hash(new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z) });
/*     */   }
/*     */   
/*     */   public Vector3d toVector3d() {
/* 205 */     return new Vector3d(this.x, this.y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3i add(int x, int y, int z) {
/* 209 */     return new Vector3i(this.x + x, this.y + y, this.z + z);
/*     */   }
/*     */   
/*     */   public Vector3i add(Vector3i other) {
/* 213 */     return add(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3i offset(BlockFace face) {
/* 217 */     return add(face.getModX(), face.getModY(), face.getModZ());
/*     */   }
/*     */   
/*     */   public Vector3i subtract(int x, int y, int z) {
/* 221 */     return new Vector3i(this.x - x, this.y - y, this.z - z);
/*     */   }
/*     */   
/*     */   public Vector3i subtract(Vector3i other) {
/* 225 */     return subtract(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3i multiply(int x, int y, int z) {
/* 229 */     return new Vector3i(this.x * x, this.y * y, this.z * z);
/*     */   }
/*     */   
/*     */   public Vector3i multiply(Vector3i other) {
/* 233 */     return multiply(other.x, other.y, other.z);
/*     */   }
/*     */   
/*     */   public Vector3i multiply(int value) {
/* 237 */     return multiply(value, value, value);
/*     */   }
/*     */   
/*     */   public Vector3i crossProduct(Vector3i other) {
/* 241 */     int newX = this.y * other.z - other.y * this.z;
/* 242 */     int newY = this.z * other.x - other.z * this.x;
/* 243 */     int newZ = this.x * other.y - other.x * this.y;
/* 244 */     return new Vector3i(newX, newY, newZ);
/*     */   }
/*     */   
/*     */   public int dot(Vector3i other) {
/* 248 */     return this.x * other.x + this.y * other.y + this.z * other.z;
/*     */   }
/*     */   
/*     */   public Vector3i with(Integer x, Integer y, Integer z) {
/* 252 */     return new Vector3i((x == null) ? this.x : x.intValue(), (y == null) ? this.y : y.intValue(), (z == null) ? this.z : z.intValue());
/*     */   }
/*     */   
/*     */   public Vector3i withX(int x) {
/* 256 */     return new Vector3i(x, this.y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3i withY(int y) {
/* 260 */     return new Vector3i(this.x, y, this.z);
/*     */   }
/*     */   
/*     */   public Vector3i withZ(int z) {
/* 264 */     return new Vector3i(this.x, this.y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 269 */     return "X: " + this.x + ", Y: " + this.y + ", Z: " + this.z;
/*     */   }
/*     */   
/*     */   public static Vector3i zero() {
/* 273 */     return new Vector3i();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Vector3i.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
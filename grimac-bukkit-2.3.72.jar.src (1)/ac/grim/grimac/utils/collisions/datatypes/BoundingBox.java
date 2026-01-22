/*     */ package ac.grim.grimac.utils.collisions.datatypes;
/*     */ import ac.grim.grimac.utils.math.Vector3dm;
/*     */ 
/*     */ public class BoundingBox {
/*     */   public final float minX;
/*     */   public final float minY;
/*     */   public final float minZ;
/*     */   
/*     */   public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
/*  10 */     this.minX = minX;
/*  11 */     this.minY = minY;
/*  12 */     this.minZ = minZ;
/*  13 */     this.maxX = maxX;
/*  14 */     this.maxY = maxY;
/*  15 */     this.maxZ = maxZ;
/*     */   }
/*     */   public final float maxX; public final float maxY; public final float maxZ;
/*     */   public BoundingBox(Vector3dm min, Vector3dm max) {
/*  19 */     this.minX = (float)Math.min(min.getX(), max.getX());
/*  20 */     this.minY = (float)Math.min(min.getY(), max.getY());
/*  21 */     this.minZ = (float)Math.min(min.getZ(), max.getZ());
/*  22 */     this.maxX = (float)Math.max(min.getX(), max.getX());
/*  23 */     this.maxY = (float)Math.max(min.getY(), max.getY());
/*  24 */     this.maxZ = (float)Math.max(min.getZ(), max.getZ());
/*     */   }
/*     */   
/*     */   public BoundingBox(BoundingBox one, BoundingBox two) {
/*  28 */     this.minX = Math.min(one.minX, two.minX);
/*  29 */     this.minY = Math.min(one.minY, two.minY);
/*  30 */     this.minZ = Math.min(one.minZ, two.minZ);
/*  31 */     this.maxX = Math.max(one.maxX, two.maxX);
/*  32 */     this.maxY = Math.max(one.maxY, two.maxY);
/*  33 */     this.maxZ = Math.max(one.maxZ, two.maxZ);
/*     */   }
/*     */   
/*     */   public BoundingBox add(float x, float y, float z) {
/*  37 */     return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
/*     */   }
/*     */   
/*     */   public BoundingBox add(Vector3dm vector) {
/*  41 */     return add((float)vector.getX(), (float)vector.getY(), (float)vector.getZ());
/*     */   }
/*     */   
/*     */   public BoundingBox grow(float x, float y, float z) {
/*  45 */     return new BoundingBox(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
/*     */   }
/*     */   
/*     */   public BoundingBox shrink(float x, float y, float z) {
/*  49 */     return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX - x, this.maxY - y, this.maxZ - z);
/*     */   }
/*     */   
/*     */   public BoundingBox add(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
/*  53 */     return new BoundingBox(this.minX + minX, this.minY + minY, this.minZ + minZ, this.maxX + maxX, this.maxY + maxY, this.maxZ + maxZ);
/*     */   }
/*     */   
/*     */   public BoundingBox subtract(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
/*  57 */     return new BoundingBox(this.minX - minX, this.minY - minY, this.minZ - minZ, this.maxX - maxX, this.maxY - maxY, this.maxZ - maxZ);
/*     */   }
/*     */   
/*     */   public boolean intersectsWithBox(Vector3dm vector) {
/*  61 */     return (vector.getX() > this.minX && vector.getX() < this.maxX && vector
/*  62 */       .getY() > this.minY && vector.getY() < this.maxY && vector
/*  63 */       .getZ() > this.minZ && vector.getZ() < this.maxZ);
/*     */   }
/*     */   
/*     */   public Vector3dm getMinimum() {
/*  67 */     return new Vector3dm(this.minX, this.minY, this.minZ);
/*     */   }
/*     */   
/*     */   public Vector3dm getMaximum() {
/*  71 */     return new Vector3dm(this.maxX, this.maxY, this.maxZ);
/*     */   }
/*     */   
/*     */   public boolean collides(Vector3dm vector) {
/*  75 */     return (vector.getX() >= this.minX && vector.getX() <= this.maxX && vector.getY() >= this.minY && vector.getY() <= this.maxY && vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ);
/*     */   }
/*     */   
/*     */   public boolean collidesHorizontally(Vector3dm vector) {
/*  79 */     return (vector.getX() >= this.minX && vector.getX() <= this.maxX && vector
/*  80 */       .getY() > this.minY && vector.getY() < this.maxY && vector
/*  81 */       .getZ() >= this.minZ && vector.getZ() <= this.maxZ);
/*     */   }
/*     */   
/*     */   public boolean collidesVertically(Vector3dm vector) {
/*  85 */     return (vector.getX() > this.minX && vector.getX() < this.maxX && vector
/*  86 */       .getY() >= this.minY && vector.getY() <= this.maxY && vector
/*  87 */       .getZ() > this.minZ && vector.getZ() < this.maxZ);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calculateXOffset(BoundingBox other, double offsetX) {
/*  96 */     if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
/*  97 */       if (offsetX > 0.0D && other.maxX <= this.minX) {
/*  98 */         double offset = (this.minX - other.maxX);
/*  99 */         if (offset < offsetX) {
/* 100 */           return offset;
/*     */         }
/* 102 */       } else if (offsetX < 0.0D && other.minX >= this.maxX) {
/* 103 */         double offset = (this.maxX - other.minX);
/* 104 */         if (offset > offsetX) {
/* 105 */           return offset;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 110 */     return offsetX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calculateYOffset(BoundingBox other, double offsetY) {
/* 119 */     if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
/* 120 */       if (offsetY > 0.0D && other.maxY <= this.minY) {
/* 121 */         double offset = (this.minY - other.maxY);
/* 122 */         if (offset < offsetY) {
/* 123 */           return offset;
/*     */         }
/* 125 */       } else if (offsetY < 0.0D && other.minY >= this.maxY) {
/* 126 */         double offset = (this.maxY - other.minY);
/* 127 */         if (offset > offsetY) {
/* 128 */           return offset;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 133 */     return offsetY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calculateZOffset(BoundingBox other, double offsetZ) {
/* 142 */     if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
/* 143 */       if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
/* 144 */         double offset = (this.minZ - other.maxZ);
/* 145 */         if (offset < offsetZ) {
/* 146 */           return offset;
/*     */         }
/* 148 */       } else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
/* 149 */         double offset = (this.maxZ - other.minZ);
/* 150 */         if (offset > offsetZ) {
/* 151 */           return offset;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 156 */     return offsetZ;
/*     */   }
/*     */   
/*     */   public BoundingBox addCoord(float x, float y, float z) {
/* 160 */     return new BoundingBox(
/* 161 */         (x < 0.0F) ? (this.minX + x) : this.minX, 
/* 162 */         (y < 0.0F) ? (this.minY + y) : this.minY, 
/* 163 */         (z < 0.0F) ? (this.minZ + z) : this.minZ, 
/* 164 */         (x > 0.0F) ? (this.maxX + x) : this.maxX, 
/* 165 */         (y > 0.0F) ? (this.maxY + y) : this.maxY, 
/* 166 */         (z > 0.0F) ? (this.maxZ + z) : this.maxZ);
/*     */   }
/*     */ 
/*     */   
/*     */   public SimpleCollisionBox toCollisionBox() {
/* 171 */     return new SimpleCollisionBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 175 */     return "[" + this.minX + ", " + this.minY + ", " + this.minZ + ", " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\BoundingBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
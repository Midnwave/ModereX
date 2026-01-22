/*     */ package ac.grim.grimac.utils.collisions.datatypes;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ComplexCollisionBox
/*     */   implements CollisionBox
/*     */ {
/*     */   public static final int DEFAULT_MAX_COLLISION_BOX_SIZE = 15;
/*     */   private final SimpleCollisionBox[] boxes;
/*     */   private int currentLength;
/*     */   
/*     */   public ComplexCollisionBox(SimpleCollisionBox... boxes) {
/*  14 */     this(15, boxes);
/*     */   }
/*     */   
/*     */   public ComplexCollisionBox(int maxIndex) {
/*  18 */     this.boxes = new SimpleCollisionBox[maxIndex];
/*     */   }
/*     */   
/*     */   public ComplexCollisionBox(int maxIndex, SimpleCollisionBox... boxes) {
/*  22 */     this.boxes = new SimpleCollisionBox[maxIndex];
/*  23 */     this.currentLength = Math.min(maxIndex, boxes.length);
/*  24 */     System.arraycopy(boxes, 0, this.boxes, 0, this.currentLength);
/*     */   }
/*     */   
/*     */   public boolean add(SimpleCollisionBox collisionBox) {
/*  28 */     this.boxes[this.currentLength] = collisionBox;
/*  29 */     this.currentLength++;
/*  30 */     return (this.currentLength <= this.boxes.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public CollisionBox union(SimpleCollisionBox other) {
/*  35 */     add(other);
/*  36 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCollided(SimpleCollisionBox other) {
/*  41 */     for (int i = 0; i < this.currentLength; i++) {
/*  42 */       if (this.boxes[i].isCollided(other)) return true; 
/*     */     } 
/*  44 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIntersected(SimpleCollisionBox other) {
/*  49 */     for (int i = 0; i < this.currentLength; i++) {
/*  50 */       if (this.boxes[i].isIntersected(other)) return true; 
/*     */     } 
/*  52 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public CollisionBox copy() {
/*  57 */     ComplexCollisionBox copy = new ComplexCollisionBox(this.boxes.length);
/*  58 */     for (int i = 0; i < this.currentLength; i++) {
/*  59 */       copy.boxes[i] = this.boxes[i].copy();
/*     */     }
/*  61 */     copy.currentLength = this.currentLength;
/*  62 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public CollisionBox offset(double x, double y, double z) {
/*  67 */     for (int i = 0; i < this.currentLength; i++) {
/*  68 */       this.boxes[i].offset(x, y, z);
/*     */     }
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void downCast(List<SimpleCollisionBox> list) {
/*  75 */     list.addAll(Arrays.<SimpleCollisionBox>asList(this.boxes).subList(0, this.currentLength));
/*     */   }
/*     */ 
/*     */   
/*     */   public int downCast(SimpleCollisionBox[] list) {
/*  80 */     System.arraycopy(this.boxes, 0, list, 0, this.currentLength);
/*  81 */     return this.currentLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNull() {
/*  86 */     for (int i = 0; i < this.currentLength; i++) {
/*  87 */       if (!this.boxes[i].isNull()) return false; 
/*     */     } 
/*  89 */     return true;
/*     */   }
/*     */   
/*     */   public int size() {
/*  93 */     int size = 0;
/*  94 */     for (SimpleCollisionBox box : this.boxes) {
/*  95 */       if (box != null) size++; 
/*     */     } 
/*  97 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFullBlock() {
/* 102 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\ComplexCollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
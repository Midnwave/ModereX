/*    */ package ac.grim.grimac.utils.math;
/*    */ public final class Vec2 extends Record {
/*    */   private final float x;
/*    */   private final float y;
/*    */   
/*  6 */   public Vec2(float x, float y) { this.x = x; this.y = y; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/math/Vec2;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  6 */     //   0	7	0	this	Lac/grim/grimac/utils/math/Vec2; } public float x() { return this.x; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/math/Vec2;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/math/Vec2; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/math/Vec2;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/math/Vec2;
/*  6 */     //   0	8	1	o	Ljava/lang/Object; } public float y() { return this.y; }
/*    */   
/*  8 */   public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
/*    */   @Contract("_ -> new")
/*    */   @NotNull
/*    */   public Vec2 scale(float scalar) {
/* 12 */     return new Vec2(this.x * scalar, this.y * scalar);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public float dot(@NotNull Vec2 vec) {
/* 17 */     return this.x * vec.x + this.y * vec.y;
/*    */   }
/*    */   @Contract("_ -> new")
/*    */   @NotNull
/*    */   public Vec2 add(@NotNull Vec2 vec) {
/* 22 */     return new Vec2(this.x + vec.x, this.y + vec.y);
/*    */   }
/*    */   @Contract("_ -> new")
/*    */   @NotNull
/*    */   public Vec2 add(float vec) {
/* 27 */     return new Vec2(this.x + vec, this.y + vec);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public boolean equals(@NotNull Vec2 vec) {
/* 32 */     return (this.x == vec.x && this.y == vec.y);
/*    */   }
/*    */   
/*    */   public Vec2 normalized() {
/* 36 */     float length = GrimMath.sqrt(this.x * this.x + this.y * this.y);
/* 37 */     return (length < 1.0E-4F) ? ZERO : new Vec2(this.x / length, this.y / length);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public float length() {
/* 42 */     return GrimMath.sqrt(this.x * this.x + this.y * this.y);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public float lengthSquared() {
/* 47 */     return this.x * this.x + this.y * this.y;
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public float distanceToSqr(@NotNull Vec2 vec) {
/* 52 */     float dx = vec.x - this.x;
/* 53 */     float dy = vec.y - this.y;
/* 54 */     return dx * dx + dy * dy;
/*    */   }
/*    */   @Contract(" -> new")
/*    */   @NotNull
/*    */   public Vec2 negated() {
/* 59 */     return new Vec2(-this.x, -this.y);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\Vec2.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
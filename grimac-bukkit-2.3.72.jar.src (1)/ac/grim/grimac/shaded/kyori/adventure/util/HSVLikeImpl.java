/*    */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class HSVLikeImpl
/*    */   implements HSVLike
/*    */ {
/*    */   private final float h;
/*    */   private final float s;
/*    */   private final float v;
/*    */   
/*    */   HSVLikeImpl(float h, float s, float v) {
/* 36 */     requireInsideRange(h, "h");
/* 37 */     requireInsideRange(s, "s");
/* 38 */     requireInsideRange(v, "v");
/* 39 */     this.h = h;
/* 40 */     this.s = s;
/* 41 */     this.v = v;
/*    */   }
/*    */ 
/*    */   
/*    */   public float h() {
/* 46 */     return this.h;
/*    */   }
/*    */ 
/*    */   
/*    */   public float s() {
/* 51 */     return this.s;
/*    */   }
/*    */ 
/*    */   
/*    */   public float v() {
/* 56 */     return this.v;
/*    */   }
/*    */   
/*    */   private static void requireInsideRange(float number, String name) throws IllegalArgumentException {
/* 60 */     if (number < 0.0F || 1.0F < number) {
/* 61 */       throw new IllegalArgumentException(name + " (" + number + ") is not inside the required range: [0,1]");
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 68 */     if (this == other) return true; 
/* 69 */     if (!(other instanceof HSVLikeImpl)) return false; 
/* 70 */     HSVLikeImpl that = (HSVLikeImpl)other;
/* 71 */     return (ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 76 */     return Objects.hash(new Object[] { Float.valueOf(this.h), Float.valueOf(this.s), Float.valueOf(this.v) });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return Internals.toString(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\HSVLikeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
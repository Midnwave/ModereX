/*     */ package ac.grim.grimac.shaded.incendo.cloud.type.range;
/*     */ 
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "FloatRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class FloatRangeImpl
/*     */   implements FloatRange
/*     */ {
/*     */   private final float minFloat;
/*     */   private final float maxFloat;
/*     */   
/*     */   private FloatRangeImpl(float minFloat, float maxFloat) {
/*  52 */     this.minFloat = minFloat;
/*  53 */     this.maxFloat = maxFloat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float minFloat() {
/*  61 */     return this.minFloat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float maxFloat() {
/*  69 */     return this.maxFloat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final FloatRangeImpl withMinFloat(float value) {
/*  79 */     if (Float.floatToIntBits(this.minFloat) == Float.floatToIntBits(value)) return this; 
/*  80 */     return new FloatRangeImpl(value, this.maxFloat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final FloatRangeImpl withMaxFloat(float value) {
/*  90 */     if (Float.floatToIntBits(this.maxFloat) == Float.floatToIntBits(value)) return this; 
/*  91 */     return new FloatRangeImpl(this.minFloat, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof FloatRangeImpl && 
/* 102 */       equalTo(0, (FloatRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, FloatRangeImpl another) {
/* 106 */     return (Float.floatToIntBits(this.minFloat) == Float.floatToIntBits(another.minFloat) && 
/* 107 */       Float.floatToIntBits(this.maxFloat) == Float.floatToIntBits(another.maxFloat));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 116 */     int h = 5381;
/* 117 */     h += (h << 5) + Float.hashCode(this.minFloat);
/* 118 */     h += (h << 5) + Float.hashCode(this.maxFloat);
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "FloatRange{minFloat=" + this.minFloat + ", maxFloat=" + this.maxFloat + "}";
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
/*     */   
/*     */   public static FloatRangeImpl of(float minFloat, float maxFloat) {
/* 141 */     return new FloatRangeImpl(minFloat, maxFloat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatRangeImpl copyOf(FloatRange instance) {
/* 152 */     if (instance instanceof FloatRangeImpl) {
/* 153 */       return (FloatRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minFloat(), instance.maxFloat());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\FloatRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
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
/*     */ @Generated(from = "DoubleRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class DoubleRangeImpl
/*     */   implements DoubleRange
/*     */ {
/*     */   private final double minDouble;
/*     */   private final double maxDouble;
/*     */   
/*     */   private DoubleRangeImpl(double minDouble, double maxDouble) {
/*  52 */     this.minDouble = minDouble;
/*  53 */     this.maxDouble = maxDouble;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double minDouble() {
/*  61 */     return this.minDouble;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double maxDouble() {
/*  69 */     return this.maxDouble;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final DoubleRangeImpl withMinDouble(double value) {
/*  79 */     if (Double.doubleToLongBits(this.minDouble) == Double.doubleToLongBits(value)) return this; 
/*  80 */     return new DoubleRangeImpl(value, this.maxDouble);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final DoubleRangeImpl withMaxDouble(double value) {
/*  90 */     if (Double.doubleToLongBits(this.maxDouble) == Double.doubleToLongBits(value)) return this; 
/*  91 */     return new DoubleRangeImpl(this.minDouble, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof DoubleRangeImpl && 
/* 102 */       equalTo(0, (DoubleRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, DoubleRangeImpl another) {
/* 106 */     return (Double.doubleToLongBits(this.minDouble) == Double.doubleToLongBits(another.minDouble) && 
/* 107 */       Double.doubleToLongBits(this.maxDouble) == Double.doubleToLongBits(another.maxDouble));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 116 */     int h = 5381;
/* 117 */     h += (h << 5) + Double.hashCode(this.minDouble);
/* 118 */     h += (h << 5) + Double.hashCode(this.maxDouble);
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "DoubleRange{minDouble=" + this.minDouble + ", maxDouble=" + this.maxDouble + "}";
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
/*     */   public static DoubleRangeImpl of(double minDouble, double maxDouble) {
/* 141 */     return new DoubleRangeImpl(minDouble, maxDouble);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DoubleRangeImpl copyOf(DoubleRange instance) {
/* 152 */     if (instance instanceof DoubleRangeImpl) {
/* 153 */       return (DoubleRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minDouble(), instance.maxDouble());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\DoubleRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
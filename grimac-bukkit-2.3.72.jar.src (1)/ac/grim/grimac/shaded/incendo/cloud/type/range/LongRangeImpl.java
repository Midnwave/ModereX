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
/*     */ @Generated(from = "LongRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class LongRangeImpl
/*     */   implements LongRange
/*     */ {
/*     */   private final long minLong;
/*     */   private final long maxLong;
/*     */   
/*     */   private LongRangeImpl(long minLong, long maxLong) {
/*  52 */     this.minLong = minLong;
/*  53 */     this.maxLong = maxLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long minLong() {
/*  61 */     return this.minLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long maxLong() {
/*  69 */     return this.maxLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final LongRangeImpl withMinLong(long value) {
/*  79 */     if (this.minLong == value) return this; 
/*  80 */     return new LongRangeImpl(value, this.maxLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final LongRangeImpl withMaxLong(long value) {
/*  90 */     if (this.maxLong == value) return this; 
/*  91 */     return new LongRangeImpl(this.minLong, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof LongRangeImpl && 
/* 102 */       equalTo(0, (LongRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, LongRangeImpl another) {
/* 106 */     return (this.minLong == another.minLong && this.maxLong == another.maxLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 116 */     int h = 5381;
/* 117 */     h += (h << 5) + Long.hashCode(this.minLong);
/* 118 */     h += (h << 5) + Long.hashCode(this.maxLong);
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "LongRange{minLong=" + this.minLong + ", maxLong=" + this.maxLong + "}";
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
/*     */   public static LongRangeImpl of(long minLong, long maxLong) {
/* 141 */     return new LongRangeImpl(minLong, maxLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LongRangeImpl copyOf(LongRange instance) {
/* 152 */     if (instance instanceof LongRangeImpl) {
/* 153 */       return (LongRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minLong(), instance.maxLong());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\LongRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
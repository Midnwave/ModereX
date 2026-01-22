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
/*     */ @Generated(from = "IntRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class IntRangeImpl
/*     */   implements IntRange
/*     */ {
/*     */   private final int minInt;
/*     */   private final int maxInt;
/*     */   
/*     */   private IntRangeImpl(int minInt, int maxInt) {
/*  52 */     this.minInt = minInt;
/*  53 */     this.maxInt = maxInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int minInt() {
/*  61 */     return this.minInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxInt() {
/*  69 */     return this.maxInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IntRangeImpl withMinInt(int value) {
/*  79 */     if (this.minInt == value) return this; 
/*  80 */     return new IntRangeImpl(value, this.maxInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IntRangeImpl withMaxInt(int value) {
/*  90 */     if (this.maxInt == value) return this; 
/*  91 */     return new IntRangeImpl(this.minInt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof IntRangeImpl && 
/* 102 */       equalTo(0, (IntRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, IntRangeImpl another) {
/* 106 */     return (this.minInt == another.minInt && this.maxInt == another.maxInt);
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
/* 117 */     h += (h << 5) + this.minInt;
/* 118 */     h += (h << 5) + this.maxInt;
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "IntRange{minInt=" + this.minInt + ", maxInt=" + this.maxInt + "}";
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
/*     */   public static IntRangeImpl of(int minInt, int maxInt) {
/* 141 */     return new IntRangeImpl(minInt, maxInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IntRangeImpl copyOf(IntRange instance) {
/* 152 */     if (instance instanceof IntRangeImpl) {
/* 153 */       return (IntRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minInt(), instance.maxInt());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\IntRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
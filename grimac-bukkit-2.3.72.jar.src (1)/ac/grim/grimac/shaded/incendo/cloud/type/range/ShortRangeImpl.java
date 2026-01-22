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
/*     */ @Generated(from = "ShortRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class ShortRangeImpl
/*     */   implements ShortRange
/*     */ {
/*     */   private final short minShort;
/*     */   private final short maxShort;
/*     */   
/*     */   private ShortRangeImpl(short minShort, short maxShort) {
/*  52 */     this.minShort = minShort;
/*  53 */     this.maxShort = maxShort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short minShort() {
/*  61 */     return this.minShort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short maxShort() {
/*  69 */     return this.maxShort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ShortRangeImpl withMinShort(short value) {
/*  79 */     if (this.minShort == value) return this; 
/*  80 */     return new ShortRangeImpl(value, this.maxShort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ShortRangeImpl withMaxShort(short value) {
/*  90 */     if (this.maxShort == value) return this; 
/*  91 */     return new ShortRangeImpl(this.minShort, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof ShortRangeImpl && 
/* 102 */       equalTo(0, (ShortRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, ShortRangeImpl another) {
/* 106 */     return (this.minShort == another.minShort && this.maxShort == another.maxShort);
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
/* 117 */     h += (h << 5) + Short.hashCode(this.minShort);
/* 118 */     h += (h << 5) + Short.hashCode(this.maxShort);
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "ShortRange{minShort=" + this.minShort + ", maxShort=" + this.maxShort + "}";
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
/*     */   public static ShortRangeImpl of(short minShort, short maxShort) {
/* 141 */     return new ShortRangeImpl(minShort, maxShort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ShortRangeImpl copyOf(ShortRange instance) {
/* 152 */     if (instance instanceof ShortRangeImpl) {
/* 153 */       return (ShortRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minShort(), instance.maxShort());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\ShortRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
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
/*     */ @Generated(from = "ByteRange", generator = "Immutables")
/*     */ @Immutable
/*     */ final class ByteRangeImpl
/*     */   implements ByteRange
/*     */ {
/*     */   private final byte minByte;
/*     */   private final byte maxByte;
/*     */   
/*     */   private ByteRangeImpl(byte minByte, byte maxByte) {
/*  52 */     this.minByte = minByte;
/*  53 */     this.maxByte = maxByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte minByte() {
/*  61 */     return this.minByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte maxByte() {
/*  69 */     return this.maxByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ByteRangeImpl withMinByte(byte value) {
/*  79 */     if (this.minByte == value) return this; 
/*  80 */     return new ByteRangeImpl(value, this.maxByte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ByteRangeImpl withMaxByte(byte value) {
/*  90 */     if (this.maxByte == value) return this; 
/*  91 */     return new ByteRangeImpl(this.minByte, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 100 */     if (this == another) return true; 
/* 101 */     return (another instanceof ByteRangeImpl && 
/* 102 */       equalTo(0, (ByteRangeImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, ByteRangeImpl another) {
/* 106 */     return (this.minByte == another.minByte && this.maxByte == another.maxByte);
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
/* 117 */     h += (h << 5) + Byte.hashCode(this.minByte);
/* 118 */     h += (h << 5) + Byte.hashCode(this.maxByte);
/* 119 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "ByteRange{minByte=" + this.minByte + ", maxByte=" + this.maxByte + "}";
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
/*     */   public static ByteRangeImpl of(byte minByte, byte maxByte) {
/* 141 */     return new ByteRangeImpl(minByte, maxByte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteRangeImpl copyOf(ByteRange instance) {
/* 152 */     if (instance instanceof ByteRangeImpl) {
/* 153 */       return (ByteRangeImpl)instance;
/*     */     }
/* 155 */     return of(instance.minByte(), instance.maxByte());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\range\ByteRangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
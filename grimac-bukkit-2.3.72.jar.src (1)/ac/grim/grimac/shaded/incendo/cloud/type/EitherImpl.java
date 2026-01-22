/*     */ package ac.grim.grimac.shaded.incendo.cloud.type;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "Either", generator = "Immutables")
/*     */ @Immutable
/*     */ final class EitherImpl<U, V>
/*     */   implements Either<U, V>
/*     */ {
/*     */   @Nullable
/*     */   private final U primary;
/*     */   @Nullable
/*     */   private final V fallback;
/*     */   
/*     */   private EitherImpl(Optional<? extends U> primary, Optional<? extends V> fallback) {
/*  55 */     this.primary = primary.orElse(null);
/*  56 */     this.fallback = fallback.orElse(null);
/*     */   }
/*     */ 
/*     */   
/*     */   private EitherImpl(@Nullable U primary, @Nullable V fallback) {
/*  61 */     this.primary = primary;
/*  62 */     this.fallback = fallback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private EitherImpl(EitherImpl<U, V> original, @Nullable U primary, @Nullable V fallback) {
/*  69 */     this.primary = primary;
/*  70 */     this.fallback = fallback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<U> primary() {
/*  78 */     return Optional.ofNullable(this.primary);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<V> fallback() {
/*  86 */     return Optional.ofNullable(this.fallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final EitherImpl<U, V> withPrimary(@Nullable U value) {
/*  95 */     U newValue = value;
/*  96 */     if (this.primary == newValue) return this; 
/*  97 */     return new EitherImpl(this, newValue, this.fallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final EitherImpl<U, V> withPrimary(Optional<? extends U> optional) {
/* 108 */     U value = optional.orElse(null);
/* 109 */     if (this.primary == value) return this; 
/* 110 */     return new EitherImpl(this, value, this.fallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final EitherImpl<U, V> withFallback(@Nullable V value) {
/* 119 */     V newValue = value;
/* 120 */     if (this.fallback == newValue) return this; 
/* 121 */     return new EitherImpl(this, this.primary, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final EitherImpl<U, V> withFallback(Optional<? extends V> optional) {
/* 132 */     V value = optional.orElse(null);
/* 133 */     if (this.fallback == value) return this; 
/* 134 */     return new EitherImpl(this, this.primary, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 143 */     if (this == another) return true; 
/* 144 */     return (another instanceof EitherImpl && 
/* 145 */       equalTo(0, (EitherImpl<?, ?>)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, EitherImpl<?, ?> another) {
/* 149 */     return (Objects.equals(this.primary, another.primary) && 
/* 150 */       Objects.equals(this.fallback, another.fallback));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 159 */     int h = 5381;
/* 160 */     h += (h << 5) + Objects.hashCode(this.primary);
/* 161 */     h += (h << 5) + Objects.hashCode(this.fallback);
/* 162 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 171 */     StringBuilder builder = new StringBuilder("Either{");
/* 172 */     if (this.primary != null) {
/* 173 */       builder.append("primary=").append(this.primary);
/*     */     }
/* 175 */     if (this.fallback != null) {
/* 176 */       if (builder.length() > 7) builder.append(", "); 
/* 177 */       builder.append("fallback=").append(this.fallback);
/*     */     } 
/* 179 */     return builder.append("}").toString();
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
/*     */   public static <U, V> EitherImpl<U, V> of(Optional<? extends U> primary, Optional<? extends V> fallback) {
/* 191 */     return new EitherImpl<>(primary, fallback);
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
/*     */   public static <U, V> EitherImpl<U, V> of(@Nullable U primary, @Nullable V fallback) {
/* 203 */     return new EitherImpl<>(primary, fallback);
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
/*     */   public static <U, V> EitherImpl<U, V> copyOf(Either<U, V> instance) {
/* 216 */     if (instance instanceof EitherImpl) {
/* 217 */       return (EitherImpl<U, V>)instance;
/*     */     }
/* 219 */     return of(instance.primary(), instance.fallback());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\EitherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
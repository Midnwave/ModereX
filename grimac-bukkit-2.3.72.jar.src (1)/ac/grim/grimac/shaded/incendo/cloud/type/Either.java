/*     */ package ac.grim.grimac.shaded.incendo.cloud.type;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Value.Immutable;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ @Immutable
/*     */ public interface Either<U, V>
/*     */ {
/*     */   static <U, V> Either<U, V> ofPrimary(U value) {
/*  55 */     return EitherImpl.of(Objects.requireNonNull(value, "value"), (V)null);
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
/*     */   static <U, V> Either<U, V> ofFallback(V value) {
/*  67 */     return EitherImpl.of((U)null, Objects.requireNonNull(value, "value"));
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
/*     */   default U primaryOrMapFallback(Function<V, U> mapFallback) {
/*  91 */     return primary().orElseGet(() -> mapFallback.apply(fallback().get()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default V fallbackOrMapPrimary(Function<U, V> mapPrimary) {
/* 101 */     return fallback().orElseGet(() -> mapPrimary.apply(primary().get()));
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
/*     */ 
/*     */ 
/*     */   
/*     */   default <R> R mapEither(Function<U, R> mapPrimary, Function<V, R> mapFallback) {
/* 117 */     return primary()
/* 118 */       .<R>map(mapPrimary)
/* 119 */       .orElseGet(() -> fallback().map(mapFallback).get());
/*     */   }
/*     */   
/*     */   Optional<U> primary();
/*     */   
/*     */   Optional<V> fallback();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\Either.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
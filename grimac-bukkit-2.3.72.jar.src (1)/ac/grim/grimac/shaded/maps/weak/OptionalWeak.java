/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ public class OptionalWeak<W extends Weak<W>>
/*     */ {
/*  15 */   private static final OptionalWeak EMPTY = new OptionalWeak(Optional.empty());
/*     */   
/*     */   public static <T extends Weak<T>> OptionalWeak<T> of(T val) {
/*  18 */     return new OptionalWeak<>(Optional.ofNullable(val));
/*     */   }
/*     */   private final Optional<W> inner;
/*     */   
/*     */   public static <T extends Weak<T>> OptionalWeak<T> empty() {
/*  23 */     return EMPTY;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private OptionalWeak(Optional<W> inner) {
/*  29 */     this.inner = inner.filter(d -> d.isPresent());
/*     */   }
/*     */   
/*     */   public W get() {
/*  33 */     return this.inner.get();
/*     */   }
/*     */   
/*     */   public boolean isPresent() {
/*  37 */     return this.inner.isPresent();
/*     */   }
/*     */   
/*     */   public void ifPresent(Consumer<? super W> consumer) {
/*  41 */     this.inner.ifPresent(consumer);
/*     */   }
/*     */   
/*     */   public OptionalWeak<W> filter(Predicate<? super W> predicate) {
/*  45 */     Objects.requireNonNull(predicate);
/*  46 */     if (!isPresent()) return this; 
/*  47 */     return predicate.test(get()) ? this : empty();
/*     */   }
/*     */ 
/*     */   
/*     */   public <U> Optional<U> map(Function<? super W, ? extends U> mapper) {
/*  52 */     return this.inner.map(mapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public <U> Optional<U> flatMap(Function<? super W, Optional<U>> mapper) {
/*  57 */     return this.inner.flatMap(mapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public W orElse(W other) {
/*  62 */     return this.inner.orElse(other);
/*     */   }
/*     */ 
/*     */   
/*     */   public W orElseGet(Supplier<? extends W> other) {
/*  67 */     return this.inner.orElseGet(other);
/*     */   }
/*     */ 
/*     */   
/*     */   public <X extends Throwable> W orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
/*  72 */     return this.inner.<X>orElseThrow(exceptionSupplier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<Object> asObject() {
/*  79 */     return this.inner.map(d -> d.asObject());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<T> as(Class<T> type) {
/*  90 */     return this.inner.filter(d -> d.is(type)).map(d -> d.as(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<String> asString() {
/*  98 */     return as(String.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<List<T>> asList() {
/* 106 */     return this.inner.filter(d -> d.isList()).map(d -> d.asList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <K, V> Optional<Map<K, V>> asMap() {
/* 114 */     return this.inner.filter(d -> d.isMap()).map(d -> d.asMap());
/*     */   }
/*     */   
/*     */   public ConverterMaybe convert() {
/* 118 */     return new ConverterMaybe(this.inner);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 123 */     if (this == o) return true; 
/* 124 */     if (o == null || getClass() != o.getClass()) return false; 
/* 125 */     OptionalWeak<?> that = (OptionalWeak)o;
/* 126 */     return Objects.equals(this.inner, that.inner);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 131 */     return Objects.hashCode(this.inner);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 136 */     return this.inner.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\OptionalWeak.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
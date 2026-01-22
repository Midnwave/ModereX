/*     */ package ac.grim.grimac.shaded.fastutil.floats;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Objects;
/*     */ import java.util.Spliterator;
/*     */ import java.util.function.Consumer;
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
/*     */ public interface FloatSpliterator
/*     */   extends Spliterator.OfPrimitive<Float, FloatConsumer, FloatSpliterator>
/*     */ {
/*     */   @Deprecated
/*     */   default boolean tryAdvance(Consumer<? super Float> action) {
/*  41 */     Objects.requireNonNull(action); return tryAdvance((action instanceof FloatConsumer) ? (FloatConsumer)action : action::accept);
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
/*     */   @Deprecated
/*     */   default void forEachRemaining(Consumer<? super Float> action) {
/*  58 */     Objects.requireNonNull(action); forEachRemaining((action instanceof FloatConsumer) ? (FloatConsumer)action : action::accept);
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
/*     */   default long skip(long n) {
/*  78 */     if (n < 0L) throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
/*  79 */     long i = n;
/*  80 */     while (i-- != 0L && tryAdvance(unused -> {
/*     */         
/*     */         }));
/*  83 */     return n - i - 1L;
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
/*     */   default FloatComparator getComparator() {
/* 103 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   FloatSpliterator trySplit();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\floats\FloatSpliterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
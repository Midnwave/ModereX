/*     */ package ac.grim.grimac.shaded.fastutil.shorts;
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
/*     */ public interface ShortSpliterator
/*     */   extends Spliterator.OfPrimitive<Short, ShortConsumer, ShortSpliterator>
/*     */ {
/*     */   @Deprecated
/*     */   default boolean tryAdvance(Consumer<? super Short> action) {
/*  41 */     Objects.requireNonNull(action); return tryAdvance((action instanceof ShortConsumer) ? (ShortConsumer)action : action::accept);
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
/*     */   default void forEachRemaining(Consumer<? super Short> action) {
/*  58 */     Objects.requireNonNull(action); forEachRemaining((action instanceof ShortConsumer) ? (ShortConsumer)action : action::accept);
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
/*     */   default ShortComparator getComparator() {
/* 103 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   ShortSpliterator trySplit();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\shorts\ShortSpliterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
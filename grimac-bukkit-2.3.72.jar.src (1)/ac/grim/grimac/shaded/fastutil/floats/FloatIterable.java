/*     */ package ac.grim.grimac.shaded.fastutil.floats;
/*     */ 
/*     */ import ac.grim.grimac.shaded.fastutil.doubles.DoubleIterator;
/*     */ import ac.grim.grimac.shaded.fastutil.doubles.DoubleIterators;
/*     */ import ac.grim.grimac.shaded.fastutil.doubles.DoubleSpliterator;
/*     */ import ac.grim.grimac.shaded.fastutil.doubles.DoubleSpliterators;
/*     */ import java.util.Iterator;
/*     */ import java.util.Objects;
/*     */ import java.util.Spliterator;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.DoubleConsumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface FloatIterable
/*     */   extends Iterable<Float>
/*     */ {
/*     */   default DoubleIterator doubleIterator() {
/*  74 */     return DoubleIterators.wrap(iterator());
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
/*     */   default FloatSpliterator spliterator() {
/*  90 */     return FloatSpliterators.asSpliteratorUnknownSize(iterator(), 0);
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
/*     */   default DoubleSpliterator doubleSpliterator() {
/* 105 */     return DoubleSpliterators.wrap(spliterator());
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
/*     */   default void forEach(FloatConsumer action) {
/* 120 */     Objects.requireNonNull(action);
/* 121 */     iterator().forEachRemaining(action);
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
/*     */   default void forEach(DoubleConsumer action) {
/* 137 */     Objects.requireNonNull(action);
/* 138 */     Objects.requireNonNull(action); forEach((action instanceof FloatConsumer) ? (FloatConsumer)action : action::accept);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void forEach(Consumer<? super Float> action) {
/* 149 */     Objects.requireNonNull(action);
/*     */ 
/*     */     
/* 152 */     Objects.requireNonNull(action); forEach((action instanceof FloatConsumer) ? (FloatConsumer)action : action::accept);
/*     */   }
/*     */   
/*     */   FloatIterator iterator();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\floats\FloatIterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
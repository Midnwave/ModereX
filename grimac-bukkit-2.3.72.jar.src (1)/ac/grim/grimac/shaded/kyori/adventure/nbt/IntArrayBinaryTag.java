/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Iterator;
/*    */ import java.util.PrimitiveIterator;
/*    */ import java.util.Spliterator;
/*    */ import java.util.function.IntConsumer;
/*    */ import java.util.stream.IntStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface IntArrayBinaryTag
/*    */   extends ArrayBinaryTag, Iterable<Integer>
/*    */ {
/*    */   @NotNull
/*    */   static IntArrayBinaryTag intArrayBinaryTag(int... value) {
/* 48 */     return new IntArrayBinaryTagImpl(value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   @ScheduledForRemoval(inVersion = "5.0.0")
/*    */   @NotNull
/*    */   static IntArrayBinaryTag of(int... value) {
/* 62 */     return new IntArrayBinaryTagImpl(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<IntArrayBinaryTag> type() {
/* 67 */     return BinaryTagTypes.INT_ARRAY;
/*    */   }
/*    */   
/*    */   int[] value();
/*    */   
/*    */   int size();
/*    */   
/*    */   int get(int paramInt);
/*    */   
/*    */   PrimitiveIterator.OfInt iterator();
/*    */   
/*    */   Spliterator.OfInt spliterator();
/*    */   
/*    */   @NotNull
/*    */   IntStream stream();
/*    */   
/*    */   void forEachInt(@NotNull IntConsumer paramIntConsumer);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\IntArrayBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
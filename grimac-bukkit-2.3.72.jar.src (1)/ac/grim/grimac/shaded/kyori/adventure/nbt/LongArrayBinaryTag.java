/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Iterator;
/*    */ import java.util.PrimitiveIterator;
/*    */ import java.util.Spliterator;
/*    */ import java.util.function.LongConsumer;
/*    */ import java.util.stream.LongStream;
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
/*    */ public interface LongArrayBinaryTag
/*    */   extends ArrayBinaryTag, Iterable<Long>
/*    */ {
/*    */   @NotNull
/*    */   static LongArrayBinaryTag longArrayBinaryTag(long... value) {
/* 48 */     return new LongArrayBinaryTagImpl(value);
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
/*    */   static LongArrayBinaryTag of(long... value) {
/* 62 */     return new LongArrayBinaryTagImpl(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<LongArrayBinaryTag> type() {
/* 67 */     return BinaryTagTypes.LONG_ARRAY;
/*    */   }
/*    */   
/*    */   long[] value();
/*    */   
/*    */   int size();
/*    */   
/*    */   long get(int paramInt);
/*    */   
/*    */   PrimitiveIterator.OfLong iterator();
/*    */   
/*    */   Spliterator.OfLong spliterator();
/*    */   
/*    */   @NotNull
/*    */   LongStream stream();
/*    */   
/*    */   void forEachLong(@NotNull LongConsumer paramLongConsumer);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\LongArrayBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
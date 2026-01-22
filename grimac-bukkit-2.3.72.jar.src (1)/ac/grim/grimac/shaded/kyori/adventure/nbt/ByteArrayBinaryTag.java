/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public interface ByteArrayBinaryTag
/*    */   extends ArrayBinaryTag, Iterable<Byte>
/*    */ {
/*    */   @NotNull
/*    */   static ByteArrayBinaryTag byteArrayBinaryTag(byte... value) {
/* 43 */     return new ByteArrayBinaryTagImpl(value);
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
/*    */   static ByteArrayBinaryTag of(byte... value) {
/* 57 */     return new ByteArrayBinaryTagImpl(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<ByteArrayBinaryTag> type() {
/* 62 */     return BinaryTagTypes.BYTE_ARRAY;
/*    */   }
/*    */   
/*    */   byte[] value();
/*    */   
/*    */   int size();
/*    */   
/*    */   byte get(int paramInt);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ByteArrayBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
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
/*    */ public interface ByteBinaryTag
/*    */   extends NumberBinaryTag
/*    */ {
/* 40 */   public static final ByteBinaryTag ZERO = new ByteBinaryTagImpl((byte)0);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 47 */   public static final ByteBinaryTag ONE = new ByteBinaryTagImpl((byte)1);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static ByteBinaryTag byteBinaryTag(byte value) {
/* 57 */     if (value == 0)
/* 58 */       return ZERO; 
/* 59 */     if (value == 1) {
/* 60 */       return ONE;
/*    */     }
/* 62 */     return new ByteBinaryTagImpl(value);
/*    */   }
/*    */ 
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
/*    */   static ByteBinaryTag of(byte value) {
/* 77 */     return byteBinaryTag(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<ByteBinaryTag> type() {
/* 82 */     return BinaryTagTypes.BYTE;
/*    */   }
/*    */   
/*    */   byte value();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ByteBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
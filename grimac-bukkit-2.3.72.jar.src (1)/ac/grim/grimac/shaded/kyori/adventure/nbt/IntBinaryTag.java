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
/*    */ public interface IntBinaryTag
/*    */   extends NumberBinaryTag
/*    */ {
/*    */   @NotNull
/*    */   static IntBinaryTag intBinaryTag(int value) {
/* 43 */     return new IntBinaryTagImpl(value);
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
/*    */   static IntBinaryTag of(int value) {
/* 57 */     return new IntBinaryTagImpl(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<IntBinaryTag> type() {
/* 62 */     return BinaryTagTypes.INT;
/*    */   }
/*    */   
/*    */   int value();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\IntBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
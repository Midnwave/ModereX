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
/*    */ public interface StringBinaryTag
/*    */   extends BinaryTag
/*    */ {
/*    */   @NotNull
/*    */   static StringBinaryTag stringBinaryTag(@NotNull String value) {
/* 43 */     return new StringBinaryTagImpl(value);
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
/*    */   static StringBinaryTag of(@NotNull String value) {
/* 57 */     return new StringBinaryTagImpl(value);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagType<StringBinaryTag> type() {
/* 62 */     return BinaryTagTypes.STRING;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   String value();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\StringBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
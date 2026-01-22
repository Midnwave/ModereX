/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt.api;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.DataComponentValue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface BinaryTagHolder
/*    */   extends DataComponentValue.TagSerializable
/*    */ {
/*    */   @NotNull
/*    */   static <T, EX extends Exception> BinaryTagHolder encode(@NotNull T nbt, @NotNull Codec<? super T, String, ?, EX> codec) throws EX {
/* 54 */     return new BinaryTagHolderImpl((String)codec.encode(nbt));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static BinaryTagHolder binaryTagHolder(@NotNull String string) {
/* 65 */     return new BinaryTagHolderImpl(string);
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
/*    */   static BinaryTagHolder of(@NotNull String string) {
/* 79 */     return new BinaryTagHolderImpl(string);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   String string();
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   default BinaryTagHolder asBinaryTag() {
/* 92 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> paramCodec) throws DX;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\api\BinaryTagHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
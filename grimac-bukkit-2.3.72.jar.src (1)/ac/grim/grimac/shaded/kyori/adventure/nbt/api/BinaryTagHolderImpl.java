/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt.api;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
/*    */ import java.util.Objects;
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
/*    */ final class BinaryTagHolderImpl
/*    */   implements BinaryTagHolder
/*    */ {
/*    */   private final String string;
/*    */   
/*    */   BinaryTagHolderImpl(String string) {
/* 35 */     this.string = Objects.<String>requireNonNull(string, "string");
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String string() {
/* 40 */     return this.string;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> codec) throws DX {
/* 45 */     return (T)codec.decode(this.string);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 50 */     return 31 * this.string.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object that) {
/* 55 */     if (!(that instanceof BinaryTagHolderImpl)) {
/* 56 */       return false;
/*    */     }
/*    */     
/* 59 */     return this.string.equals(((BinaryTagHolderImpl)that).string);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return this.string;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\api\BinaryTagHolderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
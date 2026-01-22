/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public final class NbtTagHolder
/*    */   implements BinaryTagHolder
/*    */ {
/*    */   private final NBT tag;
/*    */   
/*    */   public NbtTagHolder(NBT tag) {
/* 33 */     this.tag = tag;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String string() {
/* 39 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> codec) throws DX {
/* 44 */     return (T)codec.decode(string());
/*    */   }
/*    */   
/*    */   public NBT getTag() {
/* 48 */     return this.tag;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\adventure\NbtTagHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
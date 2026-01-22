/*    */ package ac.grim.grimac.shaded.kyori.adventure.util;
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
/*    */ public interface Codec<D, E, DX extends Throwable, EX extends Throwable>
/*    */ {
/*    */   @NotNull
/*    */   static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> codec(@NotNull final Decoder<D, E, DX> decoder, @NotNull final Encoder<D, E, EX> encoder) {
/* 52 */     return new Codec<D, E, DX, EX>() {
/*    */         @NotNull
/*    */         public D decode(@NotNull E encoded) throws DX {
/* 55 */           return (D)decoder.decode(encoded);
/*    */         }
/*    */         
/*    */         @NotNull
/*    */         public E encode(@NotNull D decoded) throws EX {
/* 60 */           return (E)encoder.encode(decoded);
/*    */         }
/*    */       };
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   @ScheduledForRemoval(inVersion = "5.0.0")
/*    */   @NotNull
/*    */   static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> of(@NotNull final Decoder<D, E, DX> decoder, @NotNull final Encoder<D, E, EX> encoder) {
/* 81 */     return new Codec<D, E, DX, EX>() {
/*    */         @NotNull
/*    */         public D decode(@NotNull E encoded) throws DX {
/* 84 */           return (D)decoder.decode(encoded);
/*    */         }
/*    */         
/*    */         @NotNull
/*    */         public E encode(@NotNull D decoded) throws EX {
/* 89 */           return (E)encoder.encode(decoded);
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   D decode(@NotNull E paramE) throws DX;
/*    */   
/*    */   @NotNull
/*    */   E encode(@NotNull D paramD) throws EX;
/*    */   
/*    */   public static interface Decoder<D, E, X extends Throwable> {
/*    */     @NotNull
/*    */     D decode(@NotNull E param1E) throws X;
/*    */   }
/*    */   
/*    */   public static interface Encoder<D, E, X extends Throwable> {
/*    */     @NotNull
/*    */     E encode(@NotNull D param1D) throws X;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
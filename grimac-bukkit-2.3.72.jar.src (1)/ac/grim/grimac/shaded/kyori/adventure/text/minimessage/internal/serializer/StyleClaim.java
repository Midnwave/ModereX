/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*    */ import java.util.Objects;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Predicate;
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
/*    */ public interface StyleClaim<V>
/*    */ {
/*    */   @NotNull
/*    */   static <T> StyleClaim<T> claim(@NotNull String claimKey, @NotNull Function<Style, T> lens, @NotNull BiConsumer<T, TokenEmitter> emitable) {
/* 53 */     return claim(claimKey, lens, $ -> true, emitable);
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
/*    */   @NotNull
/*    */   static <T> StyleClaim<T> claim(@NotNull String claimKey, @NotNull Function<Style, T> lens, @NotNull Predicate<T> filter, @NotNull BiConsumer<T, TokenEmitter> emitable) {
/* 68 */     return new StyleClaimImpl<>(
/* 69 */         Objects.<String>requireNonNull(claimKey, "claimKey"), 
/* 70 */         Objects.<Function<Style, T>>requireNonNull(lens, "lens"), 
/* 71 */         Objects.<Predicate<T>>requireNonNull(filter, "filter"), 
/* 72 */         Objects.<BiConsumer<T, TokenEmitter>>requireNonNull(emitable, "emitable"));
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   String claimKey();
/*    */   
/*    */   @Nullable
/*    */   Emitable apply(@NotNull Style paramStyle);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\StyleClaim.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
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
/*    */ class StyleClaimImpl<V>
/*    */   implements StyleClaim<V>
/*    */ {
/*    */   private final String claimKey;
/*    */   private final Function<Style, V> lens;
/*    */   private final Predicate<V> filter;
/*    */   private final BiConsumer<V, TokenEmitter> emitable;
/*    */   
/*    */   StyleClaimImpl(String claimKey, Function<Style, V> lens, Predicate<V> filter, BiConsumer<V, TokenEmitter> emitable) {
/* 41 */     this.claimKey = claimKey;
/* 42 */     this.lens = lens;
/* 43 */     this.filter = filter;
/* 44 */     this.emitable = emitable;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String claimKey() {
/* 49 */     return this.claimKey;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Emitable apply(@NotNull Style style) {
/* 54 */     V element = this.lens.apply(style);
/* 55 */     if (element == null || !this.filter.test(element)) return null;
/*    */     
/* 57 */     return emitter -> this.emitable.accept((V)element, emitter);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hash(new Object[] { this.claimKey });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 67 */     if (this == other) return true; 
/* 68 */     if (!(other instanceof StyleClaimImpl)) return false; 
/* 69 */     StyleClaimImpl<?> that = (StyleClaimImpl)other;
/* 70 */     return Objects.equals(this.claimKey, that.claimKey);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\StyleClaimImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
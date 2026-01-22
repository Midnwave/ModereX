/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*    */ import java.util.Set;
/*    */ import java.util.function.BiFunction;
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
/*    */ final class StyleClaimingResolverImpl
/*    */   implements TagResolver, SerializableResolver.Single
/*    */ {
/*    */   @NotNull
/*    */   private final Set<String> names;
/*    */   @NotNull
/*    */   private final BiFunction<ArgumentQueue, Context, Tag> handler;
/*    */   @NotNull
/*    */   private final StyleClaim<?> styleClaim;
/*    */   
/*    */   StyleClaimingResolverImpl(@NotNull Set<String> names, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler, @NotNull StyleClaim<?> styleClaim) {
/* 42 */     this.names = names;
/* 43 */     this.handler = handler;
/* 44 */     this.styleClaim = styleClaim;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
/* 49 */     if (!this.names.contains(name)) return null;
/*    */     
/* 51 */     return this.handler.apply(arguments, ctx);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean has(@NotNull String name) {
/* 56 */     return this.names.contains(name);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public StyleClaim<?> claimStyle() {
/* 61 */     return this.styleClaim;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\StyleClaimingResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*    */ import java.util.Set;
/*    */ import java.util.function.BiFunction;
/*    */ import java.util.function.Function;
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
/*    */ class ComponentClaimingResolverImpl
/*    */   implements TagResolver, SerializableResolver.Single
/*    */ {
/*    */   @NotNull
/*    */   private final Set<String> names;
/*    */   @NotNull
/*    */   private final BiFunction<ArgumentQueue, Context, Tag> handler;
/*    */   @NotNull
/*    */   private final Function<Component, Emitable> componentClaim;
/*    */   
/*    */   ComponentClaimingResolverImpl(Set<String> names, BiFunction<ArgumentQueue, Context, Tag> handler, Function<Component, Emitable> componentClaim) {
/* 44 */     this.names = names;
/* 45 */     this.handler = handler;
/* 46 */     this.componentClaim = componentClaim;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
/* 51 */     if (!this.names.contains(name)) return null;
/*    */     
/* 53 */     return this.handler.apply(arguments, ctx);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean has(@NotNull String name) {
/* 58 */     return this.names.contains(name);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Emitable claimComponent(@NotNull Component component) {
/* 63 */     return this.componentClaim.apply(component);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\ComponentClaimingResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
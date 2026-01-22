/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.TagInternals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface SerializableResolver
/*     */ {
/*     */   @NotNull
/*     */   static TagResolver claimingComponent(@NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler, @NotNull Function<Component, Emitable> componentClaim) {
/*  59 */     return claimingComponent(Collections.singleton(name), handler, componentClaim);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver claimingComponent(@NotNull Set<String> names, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler, @NotNull Function<Component, Emitable> componentClaim) {
/*  72 */     Set<String> ownNames = new HashSet<>(names);
/*  73 */     for (String name : ownNames) {
/*  74 */       TagInternals.assertValidTagName(name);
/*     */     }
/*  76 */     Objects.requireNonNull(handler, "handler");
/*  77 */     return new ComponentClaimingResolverImpl(ownNames, handler, componentClaim);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver claimingStyle(@NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler, @NotNull StyleClaim<?> styleClaim) {
/*  90 */     return claimingStyle(Collections.singleton(name), handler, styleClaim);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver claimingStyle(@NotNull Set<String> names, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler, @NotNull StyleClaim<?> styleClaim) {
/* 103 */     Set<String> ownNames = new HashSet<>(names);
/* 104 */     for (String name : ownNames) {
/* 105 */       TagInternals.assertValidTagName(name);
/*     */     }
/* 107 */     Objects.requireNonNull(handler, "handler");
/* 108 */     return new StyleClaimingResolverImpl(ownNames, handler, styleClaim);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void handle(@NotNull Component paramComponent, @NotNull ClaimConsumer paramClaimConsumer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Single
/*     */     extends SerializableResolver
/*     */   {
/*     */     default void handle(@NotNull Component serializable, @NotNull ClaimConsumer consumer) {
/* 128 */       StyleClaim<?> style = claimStyle();
/* 129 */       if (style != null && !consumer.styleClaimed(style.claimKey())) {
/* 130 */         Emitable applied = style.apply(serializable.style());
/* 131 */         if (applied != null) {
/* 132 */           consumer.style(style.claimKey(), applied);
/*     */         }
/*     */       } 
/* 135 */       if (!consumer.componentClaimed()) {
/* 136 */         Emitable component = claimComponent(serializable);
/* 137 */         if (component != null) {
/* 138 */           consumer.component(component);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     default StyleClaim<?> claimStyle() {
/* 153 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     default Emitable claimComponent(@NotNull Component component) {
/* 168 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\SerializableResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
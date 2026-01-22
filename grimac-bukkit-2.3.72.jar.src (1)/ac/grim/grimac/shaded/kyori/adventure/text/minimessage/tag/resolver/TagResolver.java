/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.TagInternals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.TagPattern;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard.StandardTags;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.stream.Collector;
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
/*     */ 
/*     */ public interface TagResolver
/*     */ {
/*     */   @NotNull
/*     */   static Builder builder() {
/*  62 */     return new TagResolverBuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver standard() {
/*  74 */     return StandardTags.defaults();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver empty() {
/*  84 */     return EmptyTagResolver.INSTANCE;
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
/*     */   static Single resolver(@TagPattern @NotNull String name, @NotNull Tag tag) {
/*  96 */     TagInternals.assertValidTagName(name);
/*  97 */     return new SingleResolver(name, 
/*     */         
/*  99 */         Objects.<Tag>requireNonNull(tag, "tag"));
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
/*     */   static TagResolver resolver(@TagPattern @NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
/* 112 */     return resolver(Collections.singleton(name), handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver resolver(@NotNull final Set<String> names, @NotNull final BiFunction<ArgumentQueue, Context, Tag> handler) {
/* 124 */     Set<String> ownNames = new HashSet<>(names);
/* 125 */     for (String name : ownNames) {
/* 126 */       TagInternals.assertValidTagName(name);
/*     */     }
/* 128 */     Objects.requireNonNull(handler, "handler");
/*     */     
/* 130 */     return new TagResolver() {
/*     */         @Nullable
/*     */         public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
/* 133 */           if (!names.contains(name)) return null;
/*     */           
/* 135 */           return handler.apply(arguments, ctx);
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean has(@NotNull String name) {
/* 140 */           return names.contains(name);
/*     */         }
/*     */       };
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
/*     */   static TagResolver resolver(@NotNull TagResolver... resolvers) {
/* 155 */     if (((TagResolver[])Objects.requireNonNull((T)resolvers, "resolvers")).length == 1) {
/* 156 */       return Objects.<TagResolver>requireNonNull(resolvers[0], "resolvers must not contain null elements");
/*     */     }
/* 158 */     return builder().resolvers(resolvers).build();
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
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver resolver(@NotNull Iterable<? extends TagResolver> resolvers) {
/* 174 */     if (resolvers instanceof Collection) {
/* 175 */       int size = ((Collection)resolvers).size();
/* 176 */       if (size == 0) return empty(); 
/* 177 */       if (size == 1) return Objects.<TagResolver>requireNonNull(resolvers.iterator().next(), "resolvers must not contain null elements");
/*     */     
/*     */     } 
/* 180 */     return builder().resolvers(resolvers).build();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static TagResolver caching(WithoutArguments resolver) {
/* 197 */     if (resolver instanceof CachingTagResolver) {
/* 198 */       return resolver;
/*     */     }
/* 200 */     return new CachingTagResolver(Objects.<WithoutArguments>requireNonNull(resolver, "resolver"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Collector<TagResolver, ?, TagResolver> toTagResolver() {
/* 211 */     return TagResolverBuilderImpl.COLLECTOR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Tag resolve(@TagPattern @NotNull String paramString, @NotNull ArgumentQueue paramArgumentQueue, @NotNull Context paramContext) throws ParsingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean has(@NotNull String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NonExtendable
/*     */   public static interface Single
/*     */     extends WithoutArguments
/*     */   {
/*     */     @NotNull
/*     */     String key();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Tag tag();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     default Tag resolve(@TagPattern @NotNull String name) {
/* 265 */       if (has(name)) {
/* 266 */         return tag();
/*     */       }
/* 268 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     default boolean has(@NotNull String name) {
/* 273 */       return name.equalsIgnoreCase(key());
/*     */     }
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
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface WithoutArguments
/*     */     extends TagResolver
/*     */   {
/*     */     @Nullable
/*     */     Tag resolve(@TagPattern @NotNull String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default boolean has(@NotNull String name) {
/* 302 */       return (resolve(name) != null);
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     default Tag resolve(@TagPattern @NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
/* 307 */       Tag resolved = resolve(name);
/* 308 */       if (resolved != null && arguments.hasNext()) {
/* 309 */         throw ctx.newException("Tag '<" + name + ">' does not accept any arguments");
/*     */       }
/* 311 */       return resolved;
/*     */     }
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
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */   {
/*     */     @NotNull
/*     */     Builder tag(@TagPattern @NotNull String param1String, @NotNull Tag param1Tag);
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
/*     */     @NotNull
/*     */     default Builder tag(@TagPattern @NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
/* 342 */       return tag(Collections.singleton(name), handler);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default Builder tag(@NotNull Set<String> names, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
/* 354 */       return resolver(TagResolver.resolver(names, handler));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder resolver(@NotNull TagResolver param1TagResolver);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder resolvers(@NotNull TagResolver... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder resolvers(@NotNull Iterable<? extends TagResolver> param1Iterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default Builder caching(TagResolver.WithoutArguments dynamic) {
/* 392 */       return resolver(TagResolver.caching(dynamic));
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     TagResolver build();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\TagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
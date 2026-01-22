/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.TagInternals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ final class TagResolverBuilderImpl
/*     */   implements TagResolver.Builder
/*     */ {
/*     */   static final Collector<TagResolver, TagResolver.Builder, TagResolver> COLLECTOR;
/*     */   
/*     */   static {
/*  40 */     COLLECTOR = Collector.of(TagResolver::builder, TagResolver.Builder::resolver, (left, right) -> TagResolver.builder().resolvers(new TagResolver[] { left.build(), right.build() }, ), TagResolver.Builder::build, new Collector.Characteristics[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   private final Map<String, Tag> replacements = new HashMap<>();
/*  48 */   private final List<TagResolver> resolvers = new ArrayList<>();
/*     */ 
/*     */   
/*     */   public TagResolver.Builder tag(@NotNull String name, @NotNull Tag tag) {
/*  52 */     TagInternals.assertValidTagName(Objects.<String>requireNonNull(name, "name"));
/*  53 */     this.replacements.put(name, 
/*     */         
/*  55 */         Objects.<Tag>requireNonNull(tag, "tag"));
/*     */     
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public TagResolver.Builder resolver(@NotNull TagResolver resolver) {
/*  62 */     if (resolver instanceof SequentialTagResolver) {
/*  63 */       resolvers(((SequentialTagResolver)resolver).resolvers, false);
/*  64 */     } else if (!consumePotentialMappable(resolver)) {
/*  65 */       popMap();
/*  66 */       this.resolvers.add(Objects.<TagResolver>requireNonNull(resolver, "resolver"));
/*     */     } 
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public TagResolver.Builder resolvers(@NotNull TagResolver... resolvers) {
/*  73 */     return resolvers(resolvers, true);
/*     */   }
/*     */   
/*     */   private TagResolver.Builder resolvers(@NotNull TagResolver[] resolvers, boolean forwards) {
/*  77 */     boolean popped = false;
/*  78 */     Objects.requireNonNull(resolvers, "resolvers");
/*  79 */     if (forwards) {
/*  80 */       for (TagResolver resolver : resolvers) {
/*  81 */         popped = single(resolver, popped);
/*     */       }
/*     */     } else {
/*  84 */       for (int i = resolvers.length - 1; i >= 0; i--) {
/*  85 */         popped = single(resolvers[i], popped);
/*     */       }
/*     */     } 
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public TagResolver.Builder resolvers(@NotNull Iterable<? extends TagResolver> resolvers) {
/*  93 */     boolean popped = false;
/*  94 */     for (TagResolver resolver : Objects.<Iterable>requireNonNull(resolvers, "resolvers")) {
/*  95 */       popped = single(resolver, popped);
/*     */     }
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   private boolean single(TagResolver resolver, boolean popped) {
/* 101 */     if (resolver instanceof SequentialTagResolver) {
/* 102 */       resolvers(((SequentialTagResolver)resolver).resolvers, false);
/* 103 */     } else if (!consumePotentialMappable(resolver)) {
/* 104 */       if (!popped) {
/* 105 */         popMap();
/*     */       }
/* 107 */       this.resolvers.add(Objects.<TagResolver>requireNonNull(resolver, "resolvers[?]"));
/* 108 */       return true;
/*     */     } 
/* 110 */     return false;
/*     */   }
/*     */   
/*     */   private void popMap() {
/* 114 */     if (!this.replacements.isEmpty()) {
/* 115 */       this.resolvers.add(new MapTagResolver(new HashMap<>(this.replacements)));
/* 116 */       this.replacements.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean consumePotentialMappable(TagResolver resolver) {
/* 121 */     if (resolver instanceof MappableResolver) {
/* 122 */       return ((MappableResolver)resolver).contributeToMap(this.replacements);
/*     */     }
/* 124 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public TagResolver build() {
/* 130 */     popMap();
/* 131 */     if (this.resolvers.size() == 0)
/* 132 */       return EmptyTagResolver.INSTANCE; 
/* 133 */     if (this.resolvers.size() == 1) {
/* 134 */       return this.resolvers.get(0);
/*     */     }
/* 136 */     TagResolver[] resolvers = this.resolvers.<TagResolver>toArray(new TagResolver[0]);
/* 137 */     Collections.reverse(Arrays.asList((Object[])resolvers));
/* 138 */     return new SequentialTagResolver(resolvers);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\TagResolverBuilderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
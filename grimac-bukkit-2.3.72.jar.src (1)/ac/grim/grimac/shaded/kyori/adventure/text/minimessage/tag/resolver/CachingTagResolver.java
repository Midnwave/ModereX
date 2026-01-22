/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ final class CachingTagResolver
/*    */   implements TagResolver.WithoutArguments, MappableResolver, SerializableResolver
/*    */ {
/* 38 */   private static final Tag NULL_REPLACEMENT = (Tag)(() -> {
/*    */       throw new UnsupportedOperationException("no-op null tag");
/*    */     });
/*    */   
/* 42 */   private final Map<String, Tag> cache = new HashMap<>();
/*    */   private final TagResolver.WithoutArguments resolver;
/*    */   
/*    */   CachingTagResolver(TagResolver.WithoutArguments resolver) {
/* 46 */     this.resolver = resolver;
/*    */   }
/*    */   
/*    */   private Tag query(@NotNull String key) {
/* 50 */     return this.cache.computeIfAbsent(key, k -> {
/*    */           Tag result = this.resolver.resolve(k);
/*    */           return (result == null) ? NULL_REPLACEMENT : result;
/*    */         });
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Tag resolve(@NotNull String name) {
/* 58 */     Tag potentialValue = query(name);
/* 59 */     return (potentialValue == NULL_REPLACEMENT) ? null : potentialValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean has(@NotNull String name) {
/* 64 */     return (query(name) != NULL_REPLACEMENT);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contributeToMap(@NotNull Map<String, Tag> map) {
/* 69 */     if (this.resolver instanceof MappableResolver) {
/* 70 */       return ((MappableResolver)this.resolver).contributeToMap(map);
/*    */     }
/* 72 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void handle(@NotNull Component serializable, @NotNull ClaimConsumer consumer) {
/* 78 */     if (this.resolver instanceof SerializableResolver) {
/* 79 */       ((SerializableResolver)this.resolver).handle(serializable, consumer);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 85 */     if (this == other) {
/* 86 */       return true;
/*    */     }
/* 88 */     if (!(other instanceof CachingTagResolver)) {
/* 89 */       return false;
/*    */     }
/* 91 */     CachingTagResolver that = (CachingTagResolver)other;
/* 92 */     return Objects.equals(this.resolver, that.resolver);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 97 */     return Objects.hash(new Object[] { this.resolver });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\CachingTagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
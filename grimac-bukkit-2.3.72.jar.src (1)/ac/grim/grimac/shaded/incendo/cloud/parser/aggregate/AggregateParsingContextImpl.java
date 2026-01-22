/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
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
/*     */ final class AggregateParsingContextImpl<C>
/*     */   implements AggregateParsingContext<C>
/*     */ {
/*  40 */   private final Map<CloudKey<?>, Object> storage = new HashMap<>();
/*     */   
/*     */   private final Collection<String> validKeys;
/*     */ 
/*     */   
/*     */   AggregateParsingContextImpl(AggregateParser<C, ?> parser) {
/*  46 */     this
/*     */ 
/*     */       
/*  49 */       .validKeys = (Collection<String>)parser.components().stream().map(CommandComponent::name).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> void store(CloudKey<V> key, V value) {
/*  54 */     this.storage.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> void store(String key, V value) {
/*  59 */     this.storage.put(CloudKey.of(key), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(CloudKey<?> key) {
/*  64 */     this.storage.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> V computeIfAbsent(CloudKey<V> key, Function<CloudKey<V>, V> defaultFunction) {
/*  72 */     return (V)this.storage.computeIfAbsent(key, k -> defaultFunction.apply(k));
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> Optional<V> optional(CloudKey<V> key) {
/*  77 */     Object value = this.storage.get(key);
/*  78 */     if (value != null) {
/*  79 */       V castedValue = (V)value;
/*  80 */       return Optional.of(castedValue);
/*     */     } 
/*  82 */     return Optional.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> Optional<V> optional(String key) {
/*  88 */     Object value = this.storage.get(CloudKey.of(key));
/*  89 */     if (value != null) {
/*  90 */       V castedValue = (V)value;
/*  91 */       return Optional.of(castedValue);
/*     */     } 
/*  93 */     return Optional.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> V get(CloudKey<V> key) {
/*  99 */     if (!this.validKeys.contains(key.name())) {
/* 100 */       throw new NullPointerException("No value with the given key has been stored in the context");
/*     */     }
/* 102 */     Object value = Objects.requireNonNull(this.storage.get(key));
/* 103 */     return (V)value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> V get(String key) {
/* 109 */     if (!this.validKeys.contains(key)) {
/* 110 */       throw new NullPointerException("No value with the given key has been stored in the context");
/*     */     }
/* 112 */     Object value = Objects.requireNonNull(this.storage.get(CloudKey.of(key)));
/* 113 */     return (V)value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(CloudKey<?> key) {
/* 118 */     return this.storage.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String key) {
/* 123 */     return this.storage.containsKey(CloudKey.of(key));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<CloudKey<?>, ? extends Object> all() {
/* 128 */     return this.storage;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParsingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
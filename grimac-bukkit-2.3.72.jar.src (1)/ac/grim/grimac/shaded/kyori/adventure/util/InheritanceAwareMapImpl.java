/*     */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ final class InheritanceAwareMapImpl<C, V>
/*     */   implements InheritanceAwareMap<C, V>
/*     */ {
/*  38 */   private static final Object NONE = new Object();
/*     */   
/*  40 */   static final InheritanceAwareMapImpl EMPTY = new InheritanceAwareMapImpl(false, Collections.emptyMap());
/*     */   
/*     */   private final Map<Class<? extends C>, V> declaredValues;
/*     */   private final boolean strict;
/*  44 */   private final transient ConcurrentMap<Class<? extends C>, Object> cache = new ConcurrentHashMap<>();
/*     */   
/*     */   InheritanceAwareMapImpl(boolean strict, Map<Class<? extends C>, V> declaredValues) {
/*  47 */     this.strict = strict;
/*  48 */     this.declaredValues = declaredValues;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(@NotNull Class<? extends C> clazz) {
/*  53 */     return (get(clazz) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V get(@NotNull Class<? extends C> clazz) {
/*  59 */     Object ret = this.cache.computeIfAbsent(clazz, c -> {
/*     */           V value = this.declaredValues.get(c);
/*     */           
/*     */           if (value != null) {
/*     */             return (Function)value;
/*     */           }
/*     */           for (Map.Entry<Class<? extends C>, V> entry : this.declaredValues.entrySet()) {
/*     */             if (((Class)entry.getKey()).isAssignableFrom(c)) {
/*     */               return (Function)entry.getValue();
/*     */             }
/*     */           } 
/*     */           return NONE;
/*     */         });
/*  72 */     return (ret == NONE) ? null : (V)ret;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public InheritanceAwareMap<C, V> with(@NotNull Class<? extends C> clazz, @NotNull V value) {
/*  77 */     if (Objects.equals(this.declaredValues.get(clazz), value)) return this; 
/*  78 */     if (this.strict) validateNoneInHierarchy(clazz, this.declaredValues);
/*     */     
/*  80 */     Map<Class<? extends C>, V> newValues = new LinkedHashMap<>(this.declaredValues);
/*  81 */     newValues.put(clazz, value);
/*  82 */     return new InheritanceAwareMapImpl(this.strict, Collections.unmodifiableMap(newValues));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public InheritanceAwareMap<C, V> without(@NotNull Class<? extends C> clazz) {
/*  87 */     if (!this.declaredValues.containsKey(clazz)) return this;
/*     */     
/*  89 */     Map<Class<? extends C>, V> newValues = new LinkedHashMap<>(this.declaredValues);
/*  90 */     newValues.remove(clazz);
/*  91 */     return new InheritanceAwareMapImpl(this.strict, Collections.unmodifiableMap(newValues));
/*     */   }
/*     */   
/*     */   static final class BuilderImpl<C, V> implements InheritanceAwareMap.Builder<C, V> {
/*     */     private boolean strict;
/*  96 */     private final Map<Class<? extends C>, V> values = new LinkedHashMap<>();
/*     */     
/*     */     @NotNull
/*     */     public InheritanceAwareMap<C, V> build() {
/* 100 */       return new InheritanceAwareMapImpl<>(this.strict, Collections.unmodifiableMap(new LinkedHashMap<>(this.values)));
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public InheritanceAwareMap.Builder<C, V> strict(boolean strict) {
/* 105 */       if (strict && !this.strict) {
/* 106 */         for (Class<? extends C> clazz : this.values.keySet()) {
/* 107 */           InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values);
/*     */         }
/*     */       }
/* 110 */       this.strict = strict;
/* 111 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public InheritanceAwareMap.Builder<C, V> put(@NotNull Class<? extends C> clazz, @NotNull V value) {
/* 116 */       if (this.strict) InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values); 
/* 117 */       this.values.put(
/* 118 */           Objects.<Class<? extends C>>requireNonNull(clazz, "clazz"), 
/* 119 */           Objects.requireNonNull(value, "value"));
/*     */       
/* 121 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public InheritanceAwareMap.Builder<C, V> remove(@NotNull Class<? extends C> clazz) {
/* 126 */       this.values.remove(Objects.requireNonNull(clazz, "clazz"));
/* 127 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public InheritanceAwareMap.Builder<C, V> putAll(@NotNull InheritanceAwareMap<? extends C, ? extends V> map) {
/* 133 */       InheritanceAwareMapImpl<?, V> impl = (InheritanceAwareMapImpl)map;
/* 134 */       if (this.strict && (
/* 135 */         !this.values.isEmpty() || !impl.strict)) {
/* 136 */         for (Map.Entry<? extends Class<?>, V> entry : (Iterable<Map.Entry<? extends Class<?>, V>>)impl.declaredValues.entrySet()) {
/* 137 */           InheritanceAwareMapImpl.validateNoneInHierarchy(entry.getKey(), this.values);
/* 138 */           this.values.put((Class<? extends C>)entry.getKey(), entry.getValue());
/*     */         } 
/* 140 */         return this;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 145 */       this.values.putAll(impl.declaredValues);
/* 146 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   private static void validateNoneInHierarchy(Class<?> beingRegistered, Map<? extends Class<?>, ?> entries) {
/* 151 */     for (Class<?> clazz : entries.keySet()) {
/* 152 */       testHierarchy(clazz, beingRegistered);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void testHierarchy(Class<?> existing, Class<?> beingRegistered) {
/* 157 */     if (!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing)))
/* 158 */       throw new IllegalArgumentException("Conflict detected between already registered type " + existing + " and newly registered type " + beingRegistered + "! Types in a strict inheritance-aware map must not share a common hierarchy!"); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\InheritanceAwareMapImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
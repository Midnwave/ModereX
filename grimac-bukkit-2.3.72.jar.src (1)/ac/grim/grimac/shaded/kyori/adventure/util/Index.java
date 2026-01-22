/*     */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.IntFunction;
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
/*     */ public final class Index<K, V>
/*     */ {
/*     */   private final Map<K, V> keyToValue;
/*     */   private final Map<V, K> valueToKey;
/*     */   
/*     */   private Index(Map<K, V> keyToValue, Map<V, K> valueToKey) {
/*  52 */     this.keyToValue = keyToValue;
/*  53 */     this.valueToKey = valueToKey;
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
/*     */   @NotNull
/*     */   public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction) {
/*  67 */     return create(type, keyFunction, type.getEnumConstants());
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
/*     */   @SafeVarargs
/*     */   @NotNull
/*     */   public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction, @NotNull V... values) {
/*  84 */     return create(values, length -> new EnumMap<>(type), keyFunction);
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
/*     */   @SafeVarargs
/*     */   @NotNull
/*     */   public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, @NotNull V... values) {
/* 100 */     return create(values, HashMap::new, keyFunction);
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
/*     */   @NotNull
/*     */   public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, @NotNull List<V> constants) {
/* 114 */     return create(constants, HashMap::new, keyFunction);
/*     */   }
/*     */   @NotNull
/*     */   private static <K, V> Index<K, V> create(V[] values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
/* 118 */     return create(Arrays.asList(values), valueToKeyFactory, keyFunction);
/*     */   }
/*     */   @NotNull
/*     */   private static <K, V> Index<K, V> create(List<V> values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
/* 122 */     int length = values.size();
/* 123 */     Map<K, V> keyToValue = new HashMap<>(length);
/* 124 */     Map<V, K> valueToKey = valueToKeyFactory.apply(length);
/* 125 */     for (int i = 0; i < length; i++) {
/* 126 */       V value = values.get(i);
/* 127 */       K key = keyFunction.apply(value);
/* 128 */       if (keyToValue.putIfAbsent(key, value) != null) {
/* 129 */         throw new IllegalStateException(String.format("Key %s already mapped to value %s", new Object[] { key, keyToValue.get(key) }));
/*     */       }
/* 131 */       if (valueToKey.putIfAbsent(value, key) != null) {
/* 132 */         throw new IllegalStateException(String.format("Value %s already mapped to key %s", new Object[] { value, valueToKey.get(value) }));
/*     */       }
/*     */     } 
/* 135 */     return new Index<>(Collections.unmodifiableMap(keyToValue), Collections.unmodifiableMap(valueToKey));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Set<K> keys() {
/* 145 */     return Collections.unmodifiableSet(this.keyToValue.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public K key(@NotNull V value) {
/* 156 */     return this.valueToKey.get(value);
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
/*     */   public K keyOrThrow(@NotNull V value) {
/* 168 */     K key = key(value);
/* 169 */     if (key == null) {
/* 170 */       throw new NoSuchElementException("There is no key for value " + value);
/*     */     }
/* 172 */     return key;
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
/*     */   @Contract("_, null -> null; _, !null -> !null")
/*     */   public K keyOr(@NotNull V value, @Nullable K defaultKey) {
/* 185 */     K key = key(value);
/* 186 */     return (key == null) ? defaultKey : key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Set<V> values() {
/* 196 */     return Collections.unmodifiableSet(this.valueToKey.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V value(@NotNull K key) {
/* 207 */     return this.keyToValue.get(key);
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
/*     */   public V valueOrThrow(@NotNull K key) {
/* 219 */     V value = value(key);
/* 220 */     if (value == null) {
/* 221 */       throw new NoSuchElementException("There is no value for key " + key);
/*     */     }
/* 223 */     return value;
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
/*     */   @Contract("_, null -> null; _, !null -> !null")
/*     */   public V valueOr(@NotNull K key, @Nullable V defaultValue) {
/* 236 */     V value = value(key);
/* 237 */     return (value == null) ? defaultValue : value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Map<K, V> keyToValue() {
/* 247 */     return Collections.unmodifiableMap(this.keyToValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Map<V, K> valueToKey() {
/* 257 */     return Collections.unmodifiableMap(this.valueToKey);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\Index.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
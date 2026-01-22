/*     */ package ac.grim.grimac.shaded.maps;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.SortedMap;
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
/*     */ public class Fluent
/*     */ {
/*     */   public static interface Map<K, V>
/*     */     extends java.util.Map<K, V>
/*     */   {
/*     */     default Map<K, V> append(K key, V val) {
/*  42 */       put(key, val);
/*  43 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default Map<K, V> appendAll(java.util.Map<? extends K, ? extends V> map) {
/*  51 */       putAll(map);
/*  52 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default Map<K, V> append(java.util.Map.Entry<? extends K, ? extends V> entry) {
/*  62 */       return append(entry.getKey(), entry.getValue());
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default java.util.Map<K, V> unmodifiable() {
/*  80 */       return Collections.unmodifiableMap(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class HashMap<K, V> extends java.util.HashMap<K, V> implements Map<K, V> {
/*     */     public HashMap(int initialCapacity, float loadFactor) {
/*  86 */       super(initialCapacity, loadFactor);
/*     */     }
/*     */     public HashMap(int initialCapacity) {
/*  89 */       super(initialCapacity);
/*     */     }
/*     */     public HashMap(java.util.Map<? extends K, ? extends V> m) {
/*  92 */       super(m);
/*     */     }
/*     */     
/*     */     public HashMap() {}
/*     */   }
/*     */   
/*     */   public static class LinkedHashMap<K, V> extends java.util.LinkedHashMap<K, V> implements Map<K, V> { public LinkedHashMap(int initialCapacity, float loadFactor) {
/*  99 */       super(initialCapacity, loadFactor);
/*     */     }
/*     */     public LinkedHashMap(int initialCapacity) {
/* 102 */       super(initialCapacity);
/*     */     }
/*     */     public LinkedHashMap(java.util.Map<? extends K, ? extends V> m) {
/* 105 */       super(m);
/*     */     }
/*     */     
/*     */     public LinkedHashMap() {} }
/*     */ 
/*     */   
/*     */   public static class IdentityHashMap<K, V> extends java.util.IdentityHashMap<K, V> implements Map<K, V> {
/*     */     public IdentityHashMap(int expectedMaxSize) {
/* 113 */       super(expectedMaxSize);
/*     */     } public IdentityHashMap() {}
/*     */     public IdentityHashMap(java.util.Map<? extends K, ? extends V> m) {
/* 116 */       super(m);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class EnumMap<K extends Enum<K>, V> extends java.util.EnumMap<K, V> implements Map<K, V> {
/*     */     public EnumMap(Class<K> keyType) {
/* 122 */       super(keyType);
/*     */     }
/*     */     public EnumMap(java.util.EnumMap<K, ? extends V> m) {
/* 125 */       super(m);
/*     */     }
/*     */     public EnumMap(java.util.Map<K, ? extends V> m) {
/* 128 */       super(m);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class WeakHashMap<K, V> extends java.util.WeakHashMap<K, V> implements Map<K, V> {
/*     */     public WeakHashMap(int initialCapacity, float loadFactor) {
/* 134 */       super(initialCapacity, loadFactor);
/*     */     }
/*     */     public WeakHashMap(int initialCapacity) {
/* 137 */       super(initialCapacity);
/*     */     }
/*     */     public WeakHashMap() {}
/*     */     public WeakHashMap(java.util.Map<? extends K, ? extends V> m) {
/* 141 */       super(m);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ConcurrentSkipListMap<K, V> extends java.util.concurrent.ConcurrentSkipListMap<K, V> implements Map<K, V> { public ConcurrentSkipListMap() {}
/*     */     
/*     */     public ConcurrentSkipListMap(Comparator<? super K> comparator) {
/* 148 */       super(comparator);
/*     */     }
/*     */     public ConcurrentSkipListMap(java.util.Map<? extends K, ? extends V> m) {
/* 151 */       super(m);
/*     */     }
/*     */     public ConcurrentSkipListMap(SortedMap<K, ? extends V> m) {
/* 154 */       super(m);
/*     */     } }
/*     */   
/*     */   public static class ConcurrentHashMap<K, V> extends java.util.concurrent.ConcurrentHashMap<K, V> implements Map<K, V> {
/*     */     public ConcurrentHashMap() {}
/*     */     
/*     */     public ConcurrentHashMap(int initialCapacity) {
/* 161 */       super(initialCapacity);
/*     */     }
/*     */     public ConcurrentHashMap(java.util.Map<? extends K, ? extends V> m) {
/* 164 */       super(m);
/*     */     }
/*     */     public ConcurrentHashMap(int initialCapacity, float loadFactor) {
/* 167 */       super(initialCapacity, loadFactor);
/*     */     }
/*     */     public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
/* 170 */       super(initialCapacity, loadFactor, concurrencyLevel);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\Fluent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
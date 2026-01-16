package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class l3<K, V> {
   private final Map<K, V> d;
   private final Map<V, K> n;
   private static final long a = kt.a(4518153974724992227L, 9041640572992862404L, MethodHandles.lookup().lookupClass()).a(261527146892356L);

   private l3(Map<K, V> var1, Map<V, K> var2) {
      this.d = var1;
      this.n = var2;
   }

   public static <K, V extends Enum<V>> l3<K, V> Q(Class<V> var0, Function<? super V, ? extends K> var1) {
      return I(var0, var1, (Enum[])var0.getEnumConstants());
   }

   public static <K, V extends Enum<V>> l3<K, V> I(Class<V> var0, Function<? super V, ? extends K> var1, V... var2) {
      return a(var2, l3::lambda$create$0, var1);
   }

   public static <K, V> l3<K, V> f(Function<? super V, ? extends K> var0, V... var1) {
      return a(var1, HashMap::new, var0);
   }

   public static <K, V> l3<K, V> v(Function<? super V, ? extends K> var0, List<V> var1) {
      return I(var1, HashMap::new, var0);
   }

   private static <K, V> l3<K, V> a(V[] var0, IntFunction<Map<V, K>> var1, Function<? super V, ? extends K> var2) {
      return I(Arrays.asList(var0), var1, var2);
   }

   private static <K, V> l3<K, V> I(List<V> var0, IntFunction<Map<V, K>> var1, Function<? super V, ? extends K> var2) {
      long var3 = a ^ 78406651733604L;
      int var5 = var0.size();
      HashMap var6 = new HashMap(var5);
      Map var7 = (Map)var1.apply(var5);

      for(int var8 = 0; var8 < var5; ++var8) {
         Object var9 = var0.get(var8);
         Object var10 = var2.apply(var9);
         if (var6.putIfAbsent(var10, var9) != null) {
            throw new IllegalStateException(String.format("Key %s already mapped to value %s", var10, var6.get(var10)));
         }

         if (var7.putIfAbsent(var9, var10) != null) {
            throw new IllegalStateException(String.format("Value %s already mapped to key %s", var9, var7.get(var9)));
         }
      }

      return new l3(Collections.unmodifiableMap(var6), Collections.unmodifiableMap(var7));
   }

   public Set<K> O() {
      return Collections.unmodifiableSet(this.d.keySet());
   }

   public K Q(V var1) {
      return this.n.get(var1);
   }

   public Set<V> x() {
      return Collections.unmodifiableSet(this.n.keySet());
   }

   public V P(K var1) {
      return this.d.get(var1);
   }

   private static Map lambda$create$0(Class var0, int var1) {
      return new EnumMap(var0);
   }
}

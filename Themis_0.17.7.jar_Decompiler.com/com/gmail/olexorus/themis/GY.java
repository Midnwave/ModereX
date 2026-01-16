package com.gmail.olexorus.themis;

import java.util.LinkedHashMap;
import java.util.Map;

class gY extends gf {
   public static final <K, V> Map<K, V> f() {
      return (Map)a.k;
   }

   public static final <K, V> Map<K, V> X(Map<? extends K, ? extends V> var0) {
      Map var10000;
      switch(var0.size()) {
      case 0:
         var10000 = gI.f();
         break;
      case 1:
         var10000 = gI.f(var0);
         break;
      default:
         var10000 = gI.c(var0);
      }

      return var10000;
   }

   public static final <K, V> Map<K, V> c(Map<? extends K, ? extends V> var0) {
      return (Map)(new LinkedHashMap(var0));
   }
}

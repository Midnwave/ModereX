package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class Ws<K, V> extends WN<K, V> {
   private static final long a = kt.a(6492763293200691363L, -2564468576718471743L, MethodHandles.lookup().lookupClass()).a(278838334920773L);

   public Ws(K var1) {
      super(var1);
   }

   public void G(Map<K, V> var1) {
      var1.remove(this.a());
   }

   public String toString() {
      long var1 = a ^ 15379205948670L;
      return "- " + this.a();
   }
}

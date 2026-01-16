package com.gmail.olexorus.themis;

import java.util.Map;

public abstract class WN<K, V> implements TH<Map<K, V>> {
   private final K M;

   public WN(K var1) {
      this.M = var1;
   }

   public K a() {
      return this.M;
   }

   public abstract void G(Map<K, V> var1);
}

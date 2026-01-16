package com.gmail.olexorus.themis;

import java.util.Map.Entry;

final class o1 implements Entry<K, V> {
   final RS v;

   o1(RS var1) {
      this.v = var1;
   }

   public K getKey() {
      return this.v.D;
   }

   public V getValue() {
      return this.v.T;
   }

   public V setValue(V var1) {
      throw new UnsupportedOperationException();
   }
}

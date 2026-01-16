package com.gmail.olexorus.themis;

import java.util.Iterator;
import java.util.Map.Entry;

abstract class mq {
   private final Iterator<Entry<K, RS<K, V>>> E;
   private RS<K, V> K;
   final z4 u;

   mq(z4 var1) {
      this.u = var1;
      this.E = this.u.entrySet().iterator();
   }

   public boolean hasNext() {
      return this.E.hasNext();
   }

   public RS<K, V> b() {
      this.K = (RS)((Entry)this.E.next()).getValue();
      return this.K;
   }

   public void remove() {
      this.E.remove();
   }
}

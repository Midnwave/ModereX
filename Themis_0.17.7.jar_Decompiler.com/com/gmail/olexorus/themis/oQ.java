package com.gmail.olexorus.themis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

final class Oq extends AbstractSet<Entry<ux, NW>> {
   final c8 v;

   Oq(c8 var1) {
      this.v = var1;
   }

   public Iterator<Entry<ux, NW>> iterator() {
      return new NQ(this);
   }

   public int size() {
      return c8.z();
   }
}
